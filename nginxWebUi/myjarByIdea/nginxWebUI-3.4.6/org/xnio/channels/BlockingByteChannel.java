package org.xnio.channels;

import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Buffers;
import org.xnio._private.Messages;

public class BlockingByteChannel implements ScatteringByteChannel, GatheringByteChannel, java.nio.channels.ByteChannel, Flushable {
   private final StreamChannel delegate;
   private volatile long readTimeout;
   private volatile long writeTimeout;

   public BlockingByteChannel(StreamChannel delegate) {
      this.delegate = delegate;
   }

   public BlockingByteChannel(StreamChannel delegate, long timeout, TimeUnit timeoutUnit) {
      this(delegate, timeout, timeoutUnit, timeout, timeoutUnit);
   }

   public BlockingByteChannel(StreamChannel delegate, long readTimeout, TimeUnit readTimeoutUnit, long writeTimeout, TimeUnit writeTimeoutUnit) {
      if (readTimeout < 0L) {
         throw Messages.msg.parameterOutOfRange("readTimeout");
      } else if (writeTimeout < 0L) {
         throw Messages.msg.parameterOutOfRange("writeTimeout");
      } else {
         long calcReadTimeout = readTimeoutUnit.toNanos(readTimeout);
         this.readTimeout = readTimeout == 0L ? 0L : (calcReadTimeout < 1L ? 1L : calcReadTimeout);
         long calcWriteTimeout = writeTimeoutUnit.toNanos(writeTimeout);
         this.writeTimeout = writeTimeout == 0L ? 0L : (calcWriteTimeout < 1L ? 1L : calcWriteTimeout);
         this.delegate = delegate;
      }
   }

   public void setReadTimeout(long readTimeout, TimeUnit readTimeoutUnit) {
      if (readTimeout < 0L) {
         throw Messages.msg.parameterOutOfRange("readTimeout");
      } else {
         long calcTimeout = readTimeoutUnit.toNanos(readTimeout);
         this.readTimeout = readTimeout == 0L ? 0L : (calcTimeout < 1L ? 1L : calcTimeout);
      }
   }

   public void setWriteTimeout(long writeTimeout, TimeUnit writeTimeoutUnit) {
      if (writeTimeout < 0L) {
         throw Messages.msg.parameterOutOfRange("writeTimeout");
      } else {
         long calcTimeout = writeTimeoutUnit.toNanos(writeTimeout);
         this.writeTimeout = writeTimeout == 0L ? 0L : (calcTimeout < 1L ? 1L : calcTimeout);
      }
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      if (!Buffers.hasRemaining(dsts, offset, length)) {
         return 0L;
      } else {
         StreamSourceChannel delegate = this.delegate;
         long res;
         if ((res = delegate.read(dsts, offset, length)) == 0L) {
            long start = System.nanoTime();
            long elapsed = 0L;

            do {
               long readTimeout = this.readTimeout;
               if (readTimeout != 0L && readTimeout != Long.MAX_VALUE) {
                  if (readTimeout <= elapsed) {
                     throw Messages.msg.readTimeout();
                  }

                  delegate.awaitReadable(readTimeout - elapsed, TimeUnit.NANOSECONDS);
               } else {
                  delegate.awaitReadable();
               }

               elapsed = System.nanoTime() - start;
            } while((res = delegate.read(dsts, offset, length)) == 0L);
         }

         return res;
      }
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      return this.read(dsts, 0, dsts.length);
   }

   public int read(ByteBuffer dst) throws IOException {
      if (!dst.hasRemaining()) {
         return 0;
      } else {
         StreamSourceChannel delegate = this.delegate;
         int res;
         if ((res = delegate.read(dst)) == 0) {
            long start = System.nanoTime();
            long elapsed = 0L;

            do {
               long readTimeout = this.readTimeout;
               if (readTimeout != 0L && readTimeout != Long.MAX_VALUE) {
                  if (readTimeout <= elapsed) {
                     throw Messages.msg.readTimeout();
                  }

                  delegate.awaitReadable(readTimeout - elapsed, TimeUnit.NANOSECONDS);
               } else {
                  delegate.awaitReadable();
               }

               elapsed = System.nanoTime() - start;
            } while((res = delegate.read(dst)) == 0);
         }

         return res;
      }
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (!Buffers.hasRemaining(srcs, offset, length)) {
         return 0L;
      } else {
         StreamSinkChannel delegate = this.delegate;
         long res;
         if ((res = delegate.write(srcs, offset, length)) == 0L) {
            long start = System.nanoTime();
            long elapsed = 0L;

            do {
               long writeTimeout = this.writeTimeout;
               if (writeTimeout != 0L && writeTimeout != Long.MAX_VALUE) {
                  if (writeTimeout <= elapsed) {
                     throw Messages.msg.writeTimeout();
                  }

                  delegate.awaitWritable(writeTimeout - elapsed, TimeUnit.NANOSECONDS);
               } else {
                  delegate.awaitWritable();
               }

               elapsed = System.nanoTime() - start;
            } while((res = delegate.write(srcs, offset, length)) == 0L);
         }

         return res;
      }
   }

   public long write(ByteBuffer[] srcs) throws IOException {
      return this.write(srcs, 0, srcs.length);
   }

   public int write(ByteBuffer src) throws IOException {
      if (!src.hasRemaining()) {
         return 0;
      } else {
         StreamSinkChannel delegate = this.delegate;
         int res;
         if ((long)(res = delegate.write(src)) == 0L) {
            long start = System.nanoTime();
            long elapsed = 0L;

            do {
               long writeTimeout = this.writeTimeout;
               if (writeTimeout != 0L && writeTimeout != Long.MAX_VALUE) {
                  if (writeTimeout <= elapsed) {
                     throw Messages.msg.writeTimeout();
                  }

                  delegate.awaitWritable(writeTimeout - elapsed, TimeUnit.NANOSECONDS);
               } else {
                  delegate.awaitWritable();
               }

               elapsed = System.nanoTime() - start;
            } while((long)(res = delegate.write(src)) == 0L);
         }

         return res;
      }
   }

   public boolean isOpen() {
      return this.delegate.isOpen();
   }

   public void flush() throws IOException {
      StreamSinkChannel delegate = this.delegate;
      if (!delegate.flush()) {
         long start = System.nanoTime();
         long elapsed = 0L;

         do {
            long writeTimeout = this.writeTimeout;
            if (writeTimeout != 0L && writeTimeout != Long.MAX_VALUE) {
               if (writeTimeout <= elapsed) {
                  throw Messages.msg.writeTimeout();
               }

               delegate.awaitWritable(writeTimeout - elapsed, TimeUnit.NANOSECONDS);
            } else {
               delegate.awaitWritable();
            }

            elapsed = System.nanoTime() - start;
         } while(!delegate.flush());
      }

   }

   public void close() throws IOException {
      this.delegate.close();
   }
}
