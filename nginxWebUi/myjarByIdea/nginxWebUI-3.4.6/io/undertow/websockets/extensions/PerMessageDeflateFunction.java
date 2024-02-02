package io.undertow.websockets.extensions;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.util.ImmediatePooledByteBuffer;
import io.undertow.websockets.core.StreamSinkFrameChannel;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketLogger;
import io.undertow.websockets.core.WebSocketMessages;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.xnio.Buffers;
import org.xnio.IoUtils;

public class PerMessageDeflateFunction implements ExtensionFunction {
   private static final byte[] TAIL = new byte[]{0, 0, -1, -1};
   private final int deflaterLevel;
   private final boolean compressContextTakeover;
   private final boolean decompressContextTakeover;
   private final Inflater decompress;
   private final Deflater compress;
   private StreamSourceFrameChannel currentReadChannel;

   public PerMessageDeflateFunction(int deflaterLevel, boolean compressContextTakeover, boolean decompressContextTakeover) {
      this.deflaterLevel = deflaterLevel;
      this.decompress = new Inflater(true);
      this.compress = new Deflater(this.deflaterLevel, true);
      this.compressContextTakeover = compressContextTakeover;
      this.decompressContextTakeover = decompressContextTakeover;
   }

   public int writeRsv(int rsv) {
      return rsv | 4;
   }

   public boolean hasExtensionOpCode() {
      return false;
   }

   public synchronized PooledByteBuffer transformForWrite(PooledByteBuffer pooledBuffer, StreamSinkFrameChannel channel, boolean lastFrame) throws IOException {
      ByteBuffer buffer = pooledBuffer.getBuffer();
      PooledByteBuffer inputBuffer = null;
      if (buffer.hasArray()) {
         this.compress.setInput(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
      } else {
         inputBuffer = this.toArrayBacked(buffer, channel.getWebSocketChannel().getBufferPool());
         this.compress.setInput(inputBuffer.getBuffer().array(), inputBuffer.getBuffer().arrayOffset() + inputBuffer.getBuffer().position(), inputBuffer.getBuffer().remaining());
      }

      PooledByteBuffer output = this.allocateBufferWithArray(channel.getWebSocketChannel(), 0);
      ByteBuffer outputBuffer = output.getBuffer();
      boolean onceOnly = true;

      try {
         while(!this.compress.needsInput() && !this.compress.finished() || !outputBuffer.hasRemaining() || onceOnly && lastFrame) {
            onceOnly = false;
            if (!outputBuffer.hasRemaining()) {
               output = this.largerBuffer(output, channel.getWebSocketChannel(), outputBuffer.capacity() * 2);
               outputBuffer = output.getBuffer();
            }

            int n = this.compress.deflate(outputBuffer.array(), outputBuffer.arrayOffset() + outputBuffer.position(), outputBuffer.remaining(), lastFrame ? 2 : 0);
            outputBuffer.position(outputBuffer.position() + n);
         }
      } finally {
         IoUtils.safeClose(pooledBuffer, inputBuffer);
      }

      if (lastFrame) {
         outputBuffer.put((byte)0);
         if (!this.compressContextTakeover) {
            this.compress.reset();
         }
      }

      outputBuffer.flip();
      return output;
   }

   private PooledByteBuffer toArrayBacked(ByteBuffer buffer, ByteBufferPool pool) {
      if (pool.getBufferSize() < buffer.remaining()) {
         return new ImmediatePooledByteBuffer(ByteBuffer.wrap(Buffers.take(buffer)));
      } else {
         PooledByteBuffer newBuf = pool.getArrayBackedPool().allocate();
         newBuf.getBuffer().put(buffer);
         newBuf.getBuffer().flip();
         return newBuf;
      }
   }

   private PooledByteBuffer largerBuffer(PooledByteBuffer smaller, WebSocketChannel channel, int newSize) {
      ByteBuffer smallerBuffer = smaller.getBuffer();
      smallerBuffer.flip();
      PooledByteBuffer larger = this.allocateBufferWithArray(channel, newSize);
      larger.getBuffer().put(smallerBuffer);
      smaller.close();
      return larger;
   }

   private PooledByteBuffer allocateBufferWithArray(WebSocketChannel channel, int size) {
      return (PooledByteBuffer)(size > 0 && size > channel.getBufferPool().getBufferSize() ? new ImmediatePooledByteBuffer(ByteBuffer.allocate(size)) : channel.getBufferPool().getArrayBackedPool().allocate());
   }

   public synchronized PooledByteBuffer transformForRead(PooledByteBuffer pooledBuffer, StreamSourceFrameChannel channel, boolean lastFragmentOfMessage) throws IOException {
      if ((channel.getRsv() & 4) == 0) {
         return pooledBuffer;
      } else {
         PooledByteBuffer output = this.allocateBufferWithArray(channel.getWebSocketChannel(), 0);
         PooledByteBuffer inputBuffer = null;
         if (this.currentReadChannel != null && this.currentReadChannel != channel) {
            this.decompress.setInput(TAIL);
            output = this.decompress(channel.getWebSocketChannel(), output);
         }

         ByteBuffer buffer = pooledBuffer.getBuffer();
         if (buffer.hasArray()) {
            this.decompress.setInput(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
         } else {
            inputBuffer = this.toArrayBacked(buffer, channel.getWebSocketChannel().getBufferPool());
            this.decompress.setInput(inputBuffer.getBuffer().array(), inputBuffer.getBuffer().arrayOffset() + inputBuffer.getBuffer().position(), inputBuffer.getBuffer().remaining());
         }

         try {
            output = this.decompress(channel.getWebSocketChannel(), output);
         } finally {
            IoUtils.safeClose(inputBuffer, pooledBuffer);
         }

         if (lastFragmentOfMessage) {
            this.decompress.setInput(TAIL);
            output = this.decompress(channel.getWebSocketChannel(), output);
            this.currentReadChannel = null;
         } else {
            this.currentReadChannel = channel;
         }

         output.getBuffer().flip();
         return output;
      }
   }

   private PooledByteBuffer decompress(WebSocketChannel channel, PooledByteBuffer pooled) throws IOException {
      int n;
      for(ByteBuffer buffer = pooled.getBuffer(); !this.decompress.needsInput() && !this.decompress.finished(); buffer.position(buffer.position() + n)) {
         if (!buffer.hasRemaining()) {
            pooled = this.largerBuffer(pooled, channel, buffer.capacity() * 2);
            buffer = pooled.getBuffer();
         }

         try {
            n = this.decompress.inflate(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
         } catch (DataFormatException var6) {
            WebSocketLogger.EXTENSION_LOGGER.debug(var6.getMessage(), var6);
            throw WebSocketMessages.MESSAGES.badCompressedPayload(var6);
         }
      }

      return pooled;
   }

   public void dispose() {
      this.compress.end();
      this.decompress.end();
   }
}
