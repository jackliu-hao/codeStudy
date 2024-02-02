package io.undertow.conduits;

import io.undertow.UndertowLogger;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.http.HttpAttachments;
import io.undertow.util.Attachable;
import io.undertow.util.AttachmentKey;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.ImmediatePooledByteBuffer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.xnio.Bits;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;

public class ChunkedStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   /** @deprecated */
   @Deprecated
   public static final AttachmentKey<HeaderMap> TRAILERS;
   private final HeaderMap responseHeaders;
   private final ConduitListener<? super ChunkedStreamSinkConduit> finishListener;
   private final int config;
   private final ByteBufferPool bufferPool;
   private static final byte[] LAST_CHUNK;
   private static final byte[] CRLF;
   private final Attachable attachable;
   private int state;
   private int chunkleft = 0;
   private final ByteBuffer chunkingBuffer = ByteBuffer.allocate(12);
   private final ByteBuffer chunkingSepBuffer;
   private PooledByteBuffer lastChunkBuffer;
   private static final int CONF_FLAG_CONFIGURABLE = 1;
   private static final int CONF_FLAG_PASS_CLOSE = 2;
   private static final int FLAG_WRITES_SHUTDOWN = 1;
   private static final int FLAG_NEXT_SHUTDOWN = 4;
   private static final int FLAG_WRITTEN_FIRST_CHUNK = 8;
   private static final int FLAG_FIRST_DATA_WRITTEN = 16;
   private static final int FLAG_FINISHED = 32;
   private static final byte[] DIGITS;

   public ChunkedStreamSinkConduit(StreamSinkConduit next, ByteBufferPool bufferPool, boolean configurable, boolean passClose, HeaderMap responseHeaders, ConduitListener<? super ChunkedStreamSinkConduit> finishListener, Attachable attachable) {
      super(next);
      this.bufferPool = bufferPool;
      this.responseHeaders = responseHeaders;
      this.finishListener = finishListener;
      this.attachable = attachable;
      this.config = (configurable ? 1 : 0) | (passClose ? 2 : 0);
      this.chunkingSepBuffer = ByteBuffer.allocate(2);
      this.chunkingSepBuffer.flip();
   }

   public int write(ByteBuffer src) throws IOException {
      return this.doWrite(src);
   }

   int doWrite(ByteBuffer src) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else if (src.remaining() == 0) {
         return 0;
      } else {
         this.state |= 16;
         int oldLimit = src.limit();
         boolean dataRemaining = false;
         if (this.chunkleft == 0 && !this.chunkingSepBuffer.hasRemaining()) {
            this.chunkingBuffer.clear();
            putIntAsHexString(this.chunkingBuffer, src.remaining());
            this.chunkingBuffer.put(CRLF);
            this.chunkingBuffer.flip();
            this.chunkingSepBuffer.clear();
            this.chunkingSepBuffer.put(CRLF);
            this.chunkingSepBuffer.flip();
            this.state |= 8;
            this.chunkleft = src.remaining();
         } else if (src.remaining() > this.chunkleft) {
            dataRemaining = true;
            src.limit(this.chunkleft + src.position());
         }

         int var7;
         try {
            int chunkingSize = this.chunkingBuffer.remaining();
            int chunkingSepSize = this.chunkingSepBuffer.remaining();
            int originalRemaining;
            if (chunkingSize > 0 || chunkingSepSize > 0 || this.lastChunkBuffer != null) {
               originalRemaining = src.remaining();
               ByteBuffer[] buf;
               long result;
               if (this.lastChunkBuffer != null && !dataRemaining) {
                  buf = new ByteBuffer[]{this.chunkingBuffer, src, this.lastChunkBuffer.getBuffer()};
                  if (Bits.anyAreSet(this.state, 2)) {
                     result = ((StreamSinkConduit)this.next).writeFinal(buf, 0, buf.length);
                  } else {
                     result = ((StreamSinkConduit)this.next).write(buf, 0, buf.length);
                  }

                  if (!src.hasRemaining()) {
                     this.state |= 1;
                  }

                  if (!this.lastChunkBuffer.getBuffer().hasRemaining()) {
                     this.state |= 4;
                     this.lastChunkBuffer.close();
                  }
               } else {
                  buf = new ByteBuffer[]{this.chunkingBuffer, src, this.chunkingSepBuffer};
                  result = ((StreamSinkConduit)this.next).write(buf, 0, buf.length);
               }

               int srcWritten = originalRemaining - src.remaining();
               this.chunkleft -= srcWritten;
               if (result < (long)chunkingSize) {
                  byte var16 = 0;
                  return var16;
               }

               int var10 = srcWritten;
               return var10;
            }

            originalRemaining = ((StreamSinkConduit)this.next).write(src);
            this.chunkleft -= originalRemaining;
            var7 = originalRemaining;
         } finally {
            src.limit(oldLimit);
         }

         return var7;
      }
   }

   public void truncateWrites() throws IOException {
      try {
         if (this.lastChunkBuffer != null) {
            this.lastChunkBuffer.close();
         }

         if (Bits.allAreClear(this.state, 32)) {
            this.invokeFinishListener();
         }
      } finally {
         super.truncateWrites();
      }

   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      for(int i = offset; i < length; ++i) {
         if (srcs[i].hasRemaining()) {
            return (long)this.write(srcs[i]);
         }
      }

      return 0L;
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      if (!src.hasRemaining()) {
         this.terminateWrites();
         return 0;
      } else {
         if (this.lastChunkBuffer == null) {
            this.createLastChunk(true);
         }

         return this.doWrite(src);
      }
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else {
         return src.transferTo(position, count, new ConduitWritableByteChannel(this));
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else {
         return IoUtils.transfer(source, count, throughBuffer, new ConduitWritableByteChannel(this));
      }
   }

   public boolean flush() throws IOException {
      this.state |= 16;
      if (Bits.anyAreSet(this.state, 1)) {
         boolean val;
         if (Bits.anyAreSet(this.state, 4)) {
            val = ((StreamSinkConduit)this.next).flush();
            if (val && Bits.allAreClear(this.state, 32)) {
               this.invokeFinishListener();
            }

            return val;
         } else {
            ((StreamSinkConduit)this.next).write(this.lastChunkBuffer.getBuffer());
            if (!this.lastChunkBuffer.getBuffer().hasRemaining()) {
               this.lastChunkBuffer.close();
               if (Bits.anyAreSet(this.config, 2)) {
                  ((StreamSinkConduit)this.next).terminateWrites();
               }

               this.state |= 4;
               val = ((StreamSinkConduit)this.next).flush();
               if (val && Bits.allAreClear(this.state, 32)) {
                  this.invokeFinishListener();
               }

               return val;
            } else {
               return false;
            }
         }
      } else {
         return ((StreamSinkConduit)this.next).flush();
      }
   }

   private void invokeFinishListener() {
      this.state |= 32;
      if (this.finishListener != null) {
         this.finishListener.handleEvent(this);
      }

   }

   public void terminateWrites() throws IOException {
      if (!Bits.anyAreSet(this.state, 1)) {
         if (this.chunkleft != 0) {
            UndertowLogger.REQUEST_IO_LOGGER.debugf("Channel closed mid-chunk", new Object[0]);
            ((StreamSinkConduit)this.next).truncateWrites();
         }

         if (!Bits.anyAreSet(this.state, 16)) {
            this.responseHeaders.put(Headers.CONTENT_LENGTH, "0");
            this.responseHeaders.remove(Headers.TRANSFER_ENCODING);
            this.state |= 5;
            if (Bits.anyAreSet(this.state, 2)) {
               ((StreamSinkConduit)this.next).terminateWrites();
            }
         } else {
            this.createLastChunk(false);
            this.state |= 1;
         }

      }
   }

   private void createLastChunk(boolean writeFinal) throws UnsupportedEncodingException {
      PooledByteBuffer lastChunkBufferPooled = this.bufferPool.allocate();
      ByteBuffer lastChunkBuffer = lastChunkBufferPooled.getBuffer();
      if (writeFinal) {
         lastChunkBuffer.put(CRLF);
      } else if (this.chunkingSepBuffer.hasRemaining()) {
         lastChunkBuffer.put(this.chunkingSepBuffer);
      }

      lastChunkBuffer.put(LAST_CHUNK);
      HeaderMap attachment = (HeaderMap)this.attachable.getAttachment(HttpAttachments.RESPONSE_TRAILERS);
      Supplier<HeaderMap> supplier = (Supplier)this.attachable.getAttachment(HttpAttachments.RESPONSE_TRAILER_SUPPLIER);
      HeaderMap trailers;
      if (attachment != null && supplier == null) {
         trailers = attachment;
      } else if (attachment == null && supplier != null) {
         trailers = (HeaderMap)supplier.get();
      } else if (attachment != null) {
         HeaderMap supplied = (HeaderMap)supplier.get();
         Iterator var8 = supplied.iterator();

         while(var8.hasNext()) {
            HeaderValues k = (HeaderValues)var8.next();
            attachment.putAll(k.getHeaderName(), k);
         }

         trailers = attachment;
      } else {
         trailers = null;
      }

      if (trailers != null && trailers.size() != 0) {
         Iterator var11 = trailers.iterator();

         while(var11.hasNext()) {
            HeaderValues trailer = (HeaderValues)var11.next();
            Iterator var14 = trailer.iterator();

            while(var14.hasNext()) {
               String val = (String)var14.next();
               trailer.getHeaderName().appendTo(lastChunkBuffer);
               lastChunkBuffer.put((byte)58);
               lastChunkBuffer.put((byte)32);
               lastChunkBuffer.put(val.getBytes(StandardCharsets.US_ASCII));
               lastChunkBuffer.put(CRLF);
            }
         }

         lastChunkBuffer.put(CRLF);
      } else {
         lastChunkBuffer.put(CRLF);
      }

      lastChunkBuffer.flip();
      ByteBuffer data = ByteBuffer.allocate(lastChunkBuffer.remaining());
      data.put(lastChunkBuffer);
      data.flip();
      this.lastChunkBuffer = new ImmediatePooledByteBuffer(data);
      lastChunkBufferPooled.close();
   }

   public void awaitWritable() throws IOException {
      ((StreamSinkConduit)this.next).awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      ((StreamSinkConduit)this.next).awaitWritable(time, timeUnit);
   }

   private static void putIntAsHexString(ByteBuffer buf, int v) {
      byte int3 = (byte)(v >> 24);
      byte int2 = (byte)(v >> 16);
      byte int1 = (byte)(v >> 8);
      byte int0 = (byte)v;
      boolean nonZeroFound = false;
      if (int3 != 0) {
         buf.put(DIGITS[(240 & int3) >>> 4]).put(DIGITS[15 & int3]);
         nonZeroFound = true;
      }

      if (nonZeroFound || int2 != 0) {
         buf.put(DIGITS[(240 & int2) >>> 4]).put(DIGITS[15 & int2]);
         nonZeroFound = true;
      }

      if (nonZeroFound || int1 != 0) {
         buf.put(DIGITS[(240 & int1) >>> 4]).put(DIGITS[15 & int1]);
      }

      buf.put(DIGITS[(240 & int0) >>> 4]).put(DIGITS[15 & int0]);
   }

   static {
      TRAILERS = HttpAttachments.RESPONSE_TRAILERS;
      LAST_CHUNK = new byte[]{48, 13, 10};
      CRLF = new byte[]{13, 10};
      DIGITS = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
   }
}
