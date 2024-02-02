package org.xnio.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.Bits;
import org.xnio._private.Messages;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSourceChannel;

public class ChannelInputStream extends InputStream {
   protected final StreamSourceChannel channel;
   private volatile int flags;
   private volatile long timeout;
   private static final AtomicIntegerFieldUpdater<ChannelInputStream> flagsUpdater = AtomicIntegerFieldUpdater.newUpdater(ChannelInputStream.class, "flags");
   private static final int FLAG_EOF = 2;
   private static final int FLAG_ENTERED = 1;

   public ChannelInputStream(StreamSourceChannel channel) {
      if (channel == null) {
         throw Messages.msg.nullParameter("channel");
      } else {
         this.channel = channel;
      }
   }

   public ChannelInputStream(StreamSourceChannel channel, long timeout, TimeUnit timeoutUnit) {
      if (channel == null) {
         throw Messages.msg.nullParameter("channel");
      } else if (timeoutUnit == null) {
         throw Messages.msg.nullParameter("timeoutUnit");
      } else if (timeout < 0L) {
         throw Messages.msg.parameterOutOfRange("timeout");
      } else {
         this.channel = channel;
         long calcTimeout = timeoutUnit.toNanos(timeout);
         this.timeout = timeout == 0L ? 0L : (calcTimeout < 1L ? 1L : calcTimeout);
      }
   }

   private boolean enter() {
      int old = this.flags;

      while(!Bits.allAreSet(old, 1)) {
         if (flagsUpdater.compareAndSet(this, old, old | 1)) {
            return Bits.allAreSet(old, 2);
         }
      }

      throw Messages.msg.concurrentAccess();
   }

   private void exit(boolean setEof) {
      int oldFlags;
      int newFlags;
      do {
         oldFlags = this.flags;
         newFlags = oldFlags & -2;
         if (setEof) {
            newFlags |= 2;
         }
      } while(!flagsUpdater.compareAndSet(this, oldFlags, newFlags));

   }

   public long getReadTimeout(TimeUnit unit) {
      if (unit == null) {
         throw Messages.msg.nullParameter("unit");
      } else {
         return unit.convert(this.timeout, TimeUnit.NANOSECONDS);
      }
   }

   public void setReadTimeout(long timeout, TimeUnit unit) {
      if (timeout < 0L) {
         throw Messages.msg.parameterOutOfRange("timeout");
      } else if (unit == null) {
         throw Messages.msg.nullParameter("unit");
      } else {
         long calcTimeout = unit.toNanos(timeout);
         this.timeout = timeout == 0L ? 0L : (calcTimeout < 1L ? 1L : calcTimeout);
      }
   }

   public int read() throws IOException {
      boolean eof = this.enter();

      int var15;
      try {
         if (eof) {
            byte var14 = -1;
            return var14;
         }

         byte[] array = new byte[1];
         ByteBuffer buffer = ByteBuffer.wrap(array);
         int res = this.channel.read(buffer);
         if (res == 0) {
            long start = System.nanoTime();
            long elapsed = 0L;

            do {
               long timeout = this.timeout;
               if (timeout == 0L) {
                  this.channel.awaitReadable();
               } else {
                  if (timeout < elapsed) {
                     throw Messages.msg.readTimeout();
                  }

                  this.channel.awaitReadable(timeout - elapsed, TimeUnit.NANOSECONDS);
               }

               elapsed = System.nanoTime() - start;
               res = this.channel.read(buffer);
            } while(res == 0);
         }

         var15 = (eof = res == -1) ? -1 : array[0] & 255;
      } finally {
         this.exit(eof);
      }

      return var15;
   }

   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (len >= 1 && off + len <= b.length) {
         boolean eof = this.enter();

         int var17;
         try {
            if (eof) {
               byte var16 = -1;
               return var16;
            }

            ByteBuffer buffer = ByteBuffer.wrap(b, off, len);
            int res = this.channel.read(buffer);
            if (res == 0) {
               long start = System.nanoTime();
               long elapsed = 0L;

               do {
                  long timeout = this.timeout;
                  if (timeout == 0L) {
                     this.channel.awaitReadable();
                  } else {
                     if (timeout < elapsed) {
                        throw Messages.msg.readTimeout();
                     }

                     this.channel.awaitReadable(timeout - elapsed, TimeUnit.NANOSECONDS);
                  }

                  elapsed = System.nanoTime() - start;
                  res = this.channel.read(buffer);
               } while(res == 0);
            }

            var17 = (eof = res == -1) ? -1 : buffer.position() - off;
         } finally {
            this.exit(eof);
         }

         return var17;
      } else {
         return 0;
      }
   }

   public long skip(long n) throws IOException {
      if (n < 1L) {
         return 0L;
      } else {
         boolean eof = this.enter();

         long var14;
         try {
            long total;
            if (eof) {
               total = 0L;
               return total;
            }

            n = Math.min(n, 2147483647L);
            total = 0L;
            long start = System.nanoTime();
            long elapsed = 0L;

            while(n != 0L) {
               long res = Channels.drain(this.channel, n);
               if (res == -1L) {
                  var14 = total;
                  return var14;
               }

               if (res == 0L) {
                  long timeout = this.timeout;

                  try {
                     if (timeout == 0L) {
                        this.channel.awaitReadable();
                     } else {
                        if (timeout < elapsed) {
                           throw Messages.msg.readTimeout();
                        }

                        this.channel.awaitReadable(timeout - elapsed, TimeUnit.NANOSECONDS);
                     }
                  } catch (InterruptedIOException var19) {
                     assert total < 2147483647L;

                     var19.bytesTransferred = (int)total;
                     throw var19;
                  }

                  elapsed = System.nanoTime() - start;
               } else {
                  total += res;
                  n -= res;
               }
            }

            var14 = total;
         } finally {
            this.exit(eof);
         }

         return var14;
      }
   }

   public void close() throws IOException {
      this.enter();

      try {
         this.channel.shutdownReads();
      } finally {
         this.exit(true);
      }

   }
}
