package io.undertow.server.protocol.ajp;

import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.conduits.AbstractFramedStreamSinkConduit;
import io.undertow.conduits.ConduitListener;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.WriteReadyHandler;

final class AjpServerResponseConduit extends AbstractFramedStreamSinkConduit {
   private static final Logger log = Logger.getLogger("io.undertow.server.channel.ajp.response");
   private static final int DEFAULT_MAX_DATA_SIZE = 8192;
   private static final Map<HttpString, Integer> HEADER_MAP;
   private static final ByteBuffer FLUSH_PACKET = ByteBuffer.allocateDirect(8);
   private static final int FLAG_START = 1;
   private static final int FLAG_WRITE_RESUMED = 4;
   private static final int FLAG_WRITE_READ_BODY_CHUNK_FROM_LISTENER = 8;
   private static final int FLAG_WRITE_SHUTDOWN = 16;
   private static final int FLAG_READS_DONE = 32;
   private static final int FLAG_FLUSH_QUEUED = 64;
   private static final ByteBuffer CLOSE_FRAME_PERSISTENT;
   private static final ByteBuffer CLOSE_FRAME_NON_PERSISTENT;
   private final ByteBufferPool pool;
   private int state = 1;
   private final HttpServerExchange exchange;
   private final ConduitListener<? super AjpServerResponseConduit> finishListener;
   private final boolean headRequest;

   AjpServerResponseConduit(StreamSinkConduit next, ByteBufferPool pool, HttpServerExchange exchange, ConduitListener<? super AjpServerResponseConduit> finishListener, boolean headRequest) {
      super(next);
      this.pool = pool;
      this.exchange = exchange;
      this.finishListener = finishListener;
      this.headRequest = headRequest;
      this.state = 1;
   }

   private static void putInt(ByteBuffer buf, int value) {
      buf.put((byte)(value >> 8 & 255));
      buf.put((byte)(value & 255));
   }

   private static void putString(ByteBuffer buf, String value) {
      int length = value.length();
      putInt(buf, length);

      for(int i = 0; i < length; ++i) {
         char c = value.charAt(i);
         if (c != '\r' && c != '\n') {
            buf.put((byte)c);
         } else {
            buf.put((byte)32);
         }
      }

      buf.put((byte)0);
   }

   private void putHttpString(ByteBuffer buf, HttpString value) {
      int length = value.length();
      putInt(buf, length);
      value.appendTo(buf);
      buf.put((byte)0);
   }

   private void processAJPHeader() {
      int oldState = this.state;
      if (Bits.anyAreSet(oldState, 1)) {
         PooledByteBuffer[] byteBuffers = null;
         Connectors.flattenCookies(this.exchange);
         PooledByteBuffer pooled = this.pool.allocate();
         ByteBuffer buffer = pooled.getBuffer();
         buffer.put((byte)65);
         buffer.put((byte)66);
         buffer.put((byte)0);
         buffer.put((byte)0);
         buffer.put((byte)4);
         putInt(buffer, this.exchange.getStatusCode());
         String reason = this.exchange.getReasonPhrase();
         if (reason == null) {
            reason = StatusCodes.getReason(this.exchange.getStatusCode());
         }

         if (reason.length() + 4 > buffer.remaining()) {
            pooled.close();
            throw UndertowMessages.MESSAGES.reasonPhraseToLargeForBuffer(reason);
         }

         putString(buffer, reason);
         int headers = 0;
         HeaderMap responseHeaders = this.exchange.getResponseHeaders();

         Iterator var8;
         HttpString header;
         for(var8 = responseHeaders.getHeaderNames().iterator(); var8.hasNext(); headers += responseHeaders.get(header).size()) {
            header = (HttpString)var8.next();
         }

         putInt(buffer, headers);
         var8 = responseHeaders.getHeaderNames().iterator();

         while(var8.hasNext()) {
            header = (HttpString)var8.next();

            String headerValue;
            for(Iterator var10 = responseHeaders.get(header).iterator(); var10.hasNext(); putString(buffer, headerValue)) {
               headerValue = (String)var10.next();
               if (buffer.remaining() < header.length() + headerValue.length() + 6) {
                  buffer.flip();
                  if (byteBuffers == null) {
                     byteBuffers = new PooledByteBuffer[]{pooled, null};
                  } else {
                     PooledByteBuffer[] old = byteBuffers;
                     byteBuffers = new PooledByteBuffer[byteBuffers.length + 1];
                     System.arraycopy(old, 0, byteBuffers, 0, old.length);
                  }

                  pooled = this.pool.allocate();
                  byteBuffers[byteBuffers.length - 1] = pooled;
                  buffer = pooled.getBuffer();
               }

               Integer headerCode = (Integer)HEADER_MAP.get(header);
               if (headerCode != null) {
                  putInt(buffer, headerCode);
               } else {
                  this.putHttpString(buffer, header);
               }
            }
         }

         if (byteBuffers == null) {
            int dataLength = buffer.position() - 4;
            buffer.put(2, (byte)(dataLength >> 8 & 255));
            buffer.put(3, (byte)(dataLength & 255));
            buffer.flip();
            this.queueFrame(new AbstractFramedStreamSinkConduit.PooledBufferFrameCallback(pooled), new ByteBuffer[]{buffer});
         } else {
            ByteBuffer[] bufs = new ByteBuffer[byteBuffers.length];

            int dataLength;
            for(dataLength = 0; dataLength < bufs.length; ++dataLength) {
               bufs[dataLength] = byteBuffers[dataLength].getBuffer();
            }

            dataLength = (int)(Buffers.remaining(bufs) - 4L);
            bufs[0].put(2, (byte)(dataLength >> 8 & 255));
            bufs[0].put(3, (byte)(dataLength & 255));
            buffer.flip();
            this.queueFrame(new AbstractFramedStreamSinkConduit.PooledBuffersFrameCallback(byteBuffers), bufs);
         }

         this.state &= -2;
      }

   }

   protected void queueCloseFrames() {
      this.processAJPHeader();
      ByteBuffer buffer = this.exchange.isPersistent() ? CLOSE_FRAME_PERSISTENT.duplicate() : CLOSE_FRAME_NON_PERSISTENT.duplicate();
      this.queueFrame((AbstractFramedStreamSinkConduit.FrameCallBack)null, new ByteBuffer[]{buffer});
   }

   private void queueRemainingBytes(ByteBuffer src, ByteBuffer[] buffers) {
      List<PooledByteBuffer> pools = new ArrayList(4);

      try {
         PooledByteBuffer newPooledBuffer = this.pool.allocate();
         pools.add(newPooledBuffer);

         while(src.remaining() > newPooledBuffer.getBuffer().remaining()) {
            ByteBuffer dupSrc = src.duplicate();
            dupSrc.limit(dupSrc.position() + newPooledBuffer.getBuffer().remaining());
            newPooledBuffer.getBuffer().put(dupSrc);
            src.position(dupSrc.position());
            newPooledBuffer.getBuffer().flip();
            newPooledBuffer = this.pool.allocate();
            pools.add(newPooledBuffer);
         }

         newPooledBuffer.getBuffer().put(src);
         newPooledBuffer.getBuffer().flip();
         ByteBuffer[] savedBuffers = new ByteBuffer[pools.size() + 2];
         int i = 0;
         savedBuffers[i++] = buffers[0];

         PooledByteBuffer p;
         for(Iterator var7 = pools.iterator(); var7.hasNext(); savedBuffers[i++] = p.getBuffer()) {
            p = (PooledByteBuffer)var7.next();
         }

         savedBuffers[i] = buffers[2];
         this.queueFrame(new AbstractFramedStreamSinkConduit.PooledBuffersFrameCallback((PooledByteBuffer[])pools.toArray(new PooledByteBuffer[0])), savedBuffers);
      } catch (Error | RuntimeException var9) {
         Iterator var5 = pools.iterator();

         while(var5.hasNext()) {
            PooledByteBuffer p = (PooledByteBuffer)var5.next();
            p.close();
         }

         throw var9;
      }
   }

   public int write(ByteBuffer src) throws IOException {
      if (this.queuedDataLength() > 0L && !this.flushQueuedData()) {
         return 0;
      } else {
         this.processAJPHeader();
         int limit;
         if (this.headRequest) {
            limit = src.remaining();
            src.position(src.position() + limit);
            return limit;
         } else {
            limit = src.limit();

            try {
               int maxData = this.exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_AJP_PACKET_SIZE, 8192) - 8;
               if (src.remaining() > maxData) {
                  src.limit(src.position() + maxData);
               }

               int writeSize = src.remaining();
               ByteBuffer[] buffers = this.createHeader(src);
               int toWrite = 0;
               ByteBuffer[] var7 = buffers;
               int var8 = buffers.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  ByteBuffer buffer = var7[var9];
                  toWrite += buffer.remaining();
               }

               long r = 0L;

               int var17;
               do {
                  r = super.write(buffers, 0, buffers.length);
                  toWrite = (int)((long)toWrite - r);
                  if (r == -1L) {
                     throw new ClosedChannelException();
                  }

                  if (r == 0L) {
                     this.queueRemainingBytes(src, buffers);
                     var17 = writeSize;
                     return var17;
                  }
               } while(toWrite > 0);

               var17 = writeSize;
               return var17;
            } catch (RuntimeException | IOException var14) {
               IoUtils.safeClose((Closeable)this.exchange.getConnection());
               throw var14;
            } finally {
               src.limit(limit);
            }
         }
      }
   }

   private ByteBuffer[] createHeader(ByteBuffer src) {
      int remaining = src.remaining();
      int chunkSize = remaining + 4;
      byte[] header = new byte[]{65, 66, (byte)(chunkSize >> 8 & 255), (byte)(chunkSize & 255), 3, (byte)(remaining >> 8 & 255), (byte)(remaining & 255)};
      byte[] footer = new byte[]{0};
      ByteBuffer[] buffers = new ByteBuffer[]{ByteBuffer.wrap(header), src, ByteBuffer.wrap(footer)};
      return buffers;
   }

   public long write(ByteBuffer[] srcs) throws IOException {
      return this.write(srcs, 0, srcs.length);
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      long total = 0L;

      for(int i = offset; i < offset + length; ++i) {
         while(srcs[i].hasRemaining()) {
            int written = this.write(srcs[i]);
            if (written == 0) {
               return total;
            }

            total += (long)written;
         }
      }

      return total;
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return src.transferTo(position, count, new ConduitWritableByteChannel(this));
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return IoUtils.transfer(source, count, throughBuffer, new ConduitWritableByteChannel(this));
   }

   protected void finished() {
      if (this.finishListener != null) {
         this.finishListener.handleEvent(this);
      }

   }

   public void setWriteReadyHandler(WriteReadyHandler handler) {
      ((StreamSinkConduit)this.next).setWriteReadyHandler(new AjpServerWriteReadyHandler(handler));
   }

   public void suspendWrites() {
      log.trace("suspend");
      this.state &= -5;
      if (Bits.allAreClear(this.state, 8)) {
         ((StreamSinkConduit)this.next).suspendWrites();
      }

   }

   public void resumeWrites() {
      log.trace("resume");
      this.state |= 4;
      ((StreamSinkConduit)this.next).resumeWrites();
   }

   public boolean flush() throws IOException {
      this.processAJPHeader();
      if (Bits.allAreClear(this.state, 64) && !this.isWritesTerminated()) {
         this.queueFrame(new AbstractFramedStreamSinkConduit.FrameCallBack() {
            public void done() {
               AjpServerResponseConduit.this.state = AjpServerResponseConduit.this.state & -65;
            }

            public void failed(IOException e) {
            }
         }, new ByteBuffer[]{FLUSH_PACKET.duplicate()});
         this.state |= 64;
      }

      return this.flushQueuedData();
   }

   public boolean isWriteResumed() {
      return Bits.anyAreSet(this.state, 4);
   }

   public void wakeupWrites() {
      log.trace("wakeup");
      this.state |= 4;
      ((StreamSinkConduit)this.next).wakeupWrites();
   }

   protected void doTerminateWrites() throws IOException {
      try {
         if (!this.exchange.isPersistent()) {
            ((StreamSinkConduit)this.next).terminateWrites();
         }

         this.state |= 16;
      } catch (RuntimeException | IOException var2) {
         IoUtils.safeClose((Closeable)this.exchange.getConnection());
         throw var2;
      }
   }

   public boolean isWriteShutdown() {
      return super.isWriteShutdown() || Bits.anyAreSet(this.state, 16);
   }

   boolean doGetRequestBodyChunk(ByteBuffer buffer, final AjpServerRequestConduit requestChannel) throws IOException {
      if (this.isWriteShutdown()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         super.write(buffer);
         if (buffer.hasRemaining()) {
            this.state |= 8;
            this.queueFrame(new AbstractFramedStreamSinkConduit.FrameCallBack() {
               public void done() {
                  AjpServerResponseConduit.this.state = AjpServerResponseConduit.this.state & -9;
                  if (Bits.allAreClear(AjpServerResponseConduit.this.state, 4)) {
                     ((StreamSinkConduit)AjpServerResponseConduit.this.next).suspendWrites();
                  }

               }

               public void failed(IOException e) {
                  requestChannel.setReadBodyChunkError(e);
               }
            }, new ByteBuffer[]{buffer});
            ((StreamSinkConduit)this.next).resumeWrites();
            return false;
         } else {
            return true;
         }
      }
   }

   static {
      Map<HttpString, Integer> headers = new HashMap();
      headers.put(Headers.CONTENT_TYPE, 40961);
      headers.put(Headers.CONTENT_LANGUAGE, 40962);
      headers.put(Headers.CONTENT_LENGTH, 40963);
      headers.put(Headers.DATE, 40964);
      headers.put(Headers.LAST_MODIFIED, 40965);
      headers.put(Headers.LOCATION, 40966);
      headers.put(Headers.SET_COOKIE, 40967);
      headers.put(Headers.SET_COOKIE2, 40968);
      headers.put(Headers.SERVLET_ENGINE, 40969);
      headers.put(Headers.STATUS, 40970);
      headers.put(Headers.WWW_AUTHENTICATE, 40971);
      HEADER_MAP = Collections.unmodifiableMap(headers);
      FLUSH_PACKET.put((byte)65);
      FLUSH_PACKET.put((byte)66);
      FLUSH_PACKET.put((byte)0);
      FLUSH_PACKET.put((byte)4);
      FLUSH_PACKET.put((byte)3);
      FLUSH_PACKET.put((byte)0);
      FLUSH_PACKET.put((byte)0);
      FLUSH_PACKET.put((byte)0);
      FLUSH_PACKET.flip();
      ByteBuffer buffer = ByteBuffer.wrap(new byte[6]);
      buffer.put((byte)65);
      buffer.put((byte)66);
      buffer.put((byte)0);
      buffer.put((byte)2);
      buffer.put((byte)5);
      buffer.put((byte)1);
      buffer.flip();
      CLOSE_FRAME_PERSISTENT = buffer;
      buffer = ByteBuffer.wrap(new byte[6]);
      buffer.put(CLOSE_FRAME_PERSISTENT.duplicate());
      buffer.put(5, (byte)0);
      buffer.flip();
      CLOSE_FRAME_NON_PERSISTENT = buffer;
   }

   private final class AjpServerWriteReadyHandler implements WriteReadyHandler {
      private final WriteReadyHandler delegate;

      private AjpServerWriteReadyHandler(WriteReadyHandler delegate) {
         this.delegate = delegate;
      }

      public void writeReady() {
         if (Bits.anyAreSet(AjpServerResponseConduit.this.state, 8)) {
            try {
               AjpServerResponseConduit.this.flushQueuedData();
            } catch (IOException var2) {
               AjpServerResponseConduit.log.debug("Error flushing when doing async READ_BODY_CHUNK flush", (Throwable)var2);
            }
         }

         if (Bits.anyAreSet(AjpServerResponseConduit.this.state, 4)) {
            this.delegate.writeReady();
         }

      }

      public void forceTermination() {
         this.delegate.forceTermination();
      }

      public void terminated() {
         this.delegate.terminated();
      }

      // $FF: synthetic method
      AjpServerWriteReadyHandler(WriteReadyHandler x1, Object x2) {
         this(x1);
      }
   }
}
