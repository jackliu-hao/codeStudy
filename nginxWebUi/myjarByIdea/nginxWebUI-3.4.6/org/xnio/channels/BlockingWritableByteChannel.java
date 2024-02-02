package org.xnio.channels;

import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Buffers;
import org.xnio._private.Messages;

public class BlockingWritableByteChannel implements GatheringByteChannel, Flushable {
   private final StreamSinkChannel delegate;
   private volatile long writeTimeout;

   public BlockingWritableByteChannel(StreamSinkChannel delegate) {
      this.delegate = delegate;
   }

   public BlockingWritableByteChannel(StreamSinkChannel delegate, long writeTimeout, TimeUnit writeTimeoutUnit) {
      if (writeTimeout < 0L) {
         throw Messages.msg.parameterOutOfRange("writeTimeout");
      } else {
         this.delegate = delegate;
         long calcTimeout = writeTimeoutUnit.toNanos(writeTimeout);
         this.writeTimeout = writeTimeout == 0L ? 0L : (calcTimeout < 1L ? 1L : calcTimeout);
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
