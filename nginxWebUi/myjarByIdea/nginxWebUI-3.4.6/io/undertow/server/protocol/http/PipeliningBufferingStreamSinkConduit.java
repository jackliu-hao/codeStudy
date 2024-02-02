package io.undertow.server.protocol.http;

import io.undertow.UndertowLogger;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.AbstractServerConnection;
import io.undertow.server.HttpServerExchange;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.StreamConnection;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;

public class PipeliningBufferingStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private static final int SHUTDOWN = 1;
   private static final int DELEGATE_SHUTDOWN = 2;
   private static final int FLUSHING = 8;
   private int state;
   private final ByteBufferPool pool;
   private PooledByteBuffer buffer;

   public PipeliningBufferingStreamSinkConduit(StreamSinkConduit next, ByteBufferPool pool) {
      super(next);
      this.pool = pool;
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else {
         return src.transferTo(position, count, new ConduitWritableByteChannel(this));
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return IoUtils.transfer(source, count, throughBuffer, new ConduitWritableByteChannel(this));
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else {
         if (Bits.anyAreSet(this.state, 8)) {
            boolean res = this.flushBuffer();
            if (!res) {
               return 0L;
            }
         }

         PooledByteBuffer pooled = this.buffer;
         if (pooled == null) {
            this.buffer = pooled = this.pool.allocate();
         }

         ByteBuffer buffer = pooled.getBuffer();
         long total = Buffers.remaining(srcs, offset, length);
         if ((long)buffer.remaining() > total) {
            Buffers.copy(buffer, srcs, offset, length);
            return total;
         } else {
            return this.flushBufferWithUserData(srcs, offset, length);
         }
      }
   }

   public int write(ByteBuffer src) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else {
         if (Bits.anyAreSet(this.state, 8)) {
            boolean res = this.flushBuffer();
            if (!res) {
               return 0;
            }
         }

         PooledByteBuffer pooled = this.buffer;
         if (pooled == null) {
            this.buffer = pooled = this.pool.allocate();
         }

         ByteBuffer buffer = pooled.getBuffer();
         if (buffer.remaining() > src.remaining()) {
            int put = src.remaining();
            buffer.put(src);
            return put;
         } else {
            return (int)this.flushBufferWithUserData(new ByteBuffer[]{src}, 0, 1);
         }
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Conduits.writeFinalBasic(this, src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   private long flushBufferWithUserData(ByteBuffer[] byteBuffers, int offset, int length) throws IOException {
      ByteBuffer byteBuffer = this.buffer.getBuffer();
      if (byteBuffer.position() == 0) {
         long var15;
         try {
            var15 = ((StreamSinkConduit)this.next).write(byteBuffers, offset, length);
         } finally {
            this.buffer.close();
            this.buffer = null;
         }

         return var15;
      } else {
         if (!Bits.anyAreSet(this.state, 8)) {
            this.state |= 8;
            byteBuffer.flip();
         }

         int originalBufferedRemaining = byteBuffer.remaining();
         long toWrite = (long)originalBufferedRemaining;
         ByteBuffer[] writeBufs = new ByteBuffer[length + 1];
         writeBufs[0] = byteBuffer;

         for(int i = offset; i < offset + length; ++i) {
            writeBufs[i + 1 - offset] = byteBuffers[i];
            toWrite += (long)byteBuffers[i].remaining();
         }

         long res = 0L;
         long written = 0L;

         do {
            res = ((StreamSinkConduit)this.next).write(writeBufs, 0, writeBufs.length);
            written += res;
            if (res == 0L) {
               if (written > (long)originalBufferedRemaining) {
                  this.buffer.close();
                  this.buffer = null;
                  this.state &= -9;
                  return written - (long)originalBufferedRemaining;
               } else {
                  return 0L;
               }
            }
         } while(written < toWrite);

         this.buffer.close();
         this.buffer = null;
         this.state &= -9;
         return written - (long)originalBufferedRemaining;
      }
   }

   public boolean flushPipelinedData() throws IOException {
      return this.buffer != null && (this.buffer.getBuffer().position() != 0 || !Bits.allAreClear(this.state, 8)) ? this.flushBuffer() : ((StreamSinkConduit)this.next).flush();
   }

   public void setupPipelineBuffer(HttpServerExchange exchange) {
      ((HttpServerConnection)exchange.getConnection()).getChannel().getSinkChannel().setConduit(this);
   }

   private boolean flushBuffer() throws IOException {
      if (this.buffer == null) {
         return ((StreamSinkConduit)this.next).flush();
      } else {
         ByteBuffer byteBuffer = this.buffer.getBuffer();
         if (!Bits.anyAreSet(this.state, 8)) {
            this.state |= 8;
            byteBuffer.flip();
         }

         do {
            if (!byteBuffer.hasRemaining()) {
               if (!((StreamSinkConduit)this.next).flush()) {
                  return false;
               }

               this.buffer.close();
               this.buffer = null;
               this.state &= -9;
               return true;
            }
         } while(((StreamSinkConduit)this.next).write(byteBuffer) != 0);

         return false;
      }
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      if (this.buffer == null || !this.buffer.getBuffer().hasRemaining()) {
         ((StreamSinkConduit)this.next).awaitWritable(time, timeUnit);
      }
   }

   public void awaitWritable() throws IOException {
      if (this.buffer != null) {
         if (this.buffer.getBuffer().hasRemaining()) {
            return;
         }

         ((StreamSinkConduit)this.next).awaitWritable();
      }

   }

   public boolean flush() throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         if (!this.flushBuffer()) {
            return false;
         } else {
            if (Bits.anyAreSet(this.state, 1) && Bits.anyAreClear(this.state, 2)) {
               this.state |= 2;
               ((StreamSinkConduit)this.next).terminateWrites();
            }

            return ((StreamSinkConduit)this.next).flush();
         }
      } else {
         return true;
      }
   }

   public void terminateWrites() throws IOException {
      this.state |= 1;
      if (this.buffer == null) {
         this.state |= 2;
         ((StreamSinkConduit)this.next).terminateWrites();
      }

   }

   public void truncateWrites() throws IOException {
      try {
         ((StreamSinkConduit)this.next).truncateWrites();
      } finally {
         if (this.buffer != null) {
            this.buffer.close();
         }

      }

   }

   public void exchangeComplete(HttpServerExchange exchange) {
      HttpServerConnection connection = (HttpServerConnection)exchange.getConnection();
      if (connection.getExtraBytes() != null && !exchange.isUpgrade()) {
         connection.getReadListener().exchangeComplete(exchange);
      } else {
         this.performFlush(exchange, connection);
      }

   }

   void performFlush(final HttpServerExchange exchange, final HttpServerConnection connection) {
      try {
         final AbstractServerConnection.ConduitState oldState = connection.resetChannel();
         if (!this.flushPipelinedData()) {
            final StreamConnection channel = connection.getChannel();
            channel.getSinkChannel().setWriteListener(new ChannelListener<Channel>() {
               public void handleEvent(Channel c) {
                  try {
                     if (PipeliningBufferingStreamSinkConduit.this.flushPipelinedData()) {
                        channel.getSinkChannel().setWriteListener((ChannelListener)null);
                        channel.getSinkChannel().suspendWrites();
                        connection.restoreChannel(oldState);
                        connection.getReadListener().exchangeComplete(exchange);
                     }
                  } catch (IOException var3) {
                     UndertowLogger.REQUEST_IO_LOGGER.ioException(var3);
                     IoUtils.safeClose((Closeable)channel);
                  } catch (Throwable var4) {
                     UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var4);
                     IoUtils.safeClose((Closeable)channel);
                  }

               }
            });
            connection.getChannel().getSinkChannel().resumeWrites();
            return;
         }

         connection.restoreChannel(oldState);
         connection.getReadListener().exchangeComplete(exchange);
      } catch (IOException var5) {
         UndertowLogger.REQUEST_IO_LOGGER.ioException(var5);
         IoUtils.safeClose((Closeable)connection.getChannel());
      } catch (Throwable var6) {
         UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var6);
         IoUtils.safeClose((Closeable)connection.getChannel());
      }

   }
}
