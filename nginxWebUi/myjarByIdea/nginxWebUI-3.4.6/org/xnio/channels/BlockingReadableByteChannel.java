package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ScatteringByteChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Buffers;
import org.xnio._private.Messages;

public class BlockingReadableByteChannel implements ScatteringByteChannel {
   private final StreamSourceChannel delegate;
   private volatile long readTimeout;

   public BlockingReadableByteChannel(StreamSourceChannel delegate) {
      this.delegate = delegate;
   }

   public BlockingReadableByteChannel(StreamSourceChannel delegate, long readTimeout, TimeUnit readTimeoutUnit) {
      if (readTimeout < 0L) {
         throw Messages.msg.parameterOutOfRange("readTimeout");
      } else {
         this.delegate = delegate;
         long calcTimeout = readTimeoutUnit.toNanos(readTimeout);
         this.readTimeout = readTimeout == 0L ? 0L : (calcTimeout < 1L ? 1L : calcTimeout);
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

   public boolean isOpen() {
      return this.delegate.isOpen();
   }

   public void close() throws IOException {
      this.delegate.close();
   }
}
