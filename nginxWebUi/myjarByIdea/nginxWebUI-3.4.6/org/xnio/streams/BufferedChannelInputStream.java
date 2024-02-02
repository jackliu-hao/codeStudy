package org.xnio.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio._private.Messages;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSourceChannel;

public class BufferedChannelInputStream extends InputStream {
   private final StreamSourceChannel channel;
   private final ByteBuffer buffer;
   private volatile int flags;
   private volatile long timeout;
   private static final AtomicIntegerFieldUpdater<BufferedChannelInputStream> flagsUpdater = AtomicIntegerFieldUpdater.newUpdater(BufferedChannelInputStream.class, "flags");
   private static final int FLAG_EOF = 2;
   private static final int FLAG_ENTERED = 1;

   public BufferedChannelInputStream(StreamSourceChannel channel, int bufferSize) {
      if (channel == null) {
         throw Messages.msg.nullParameter("channel");
      } else if (bufferSize < 1) {
         throw Messages.msg.parameterOutOfRange("bufferSize");
      } else {
         this.channel = channel;
         this.buffer = ByteBuffer.allocate(bufferSize);
         this.buffer.limit(0);
      }
   }

   public BufferedChannelInputStream(StreamSourceChannel channel, int bufferSize, long timeout, TimeUnit unit) {
      if (channel == null) {
         throw Messages.msg.nullParameter("channel");
      } else if (unit == null) {
         throw Messages.msg.nullParameter("unit");
      } else if (bufferSize < 1) {
         throw Messages.msg.parameterOutOfRange("bufferSize");
      } else if (timeout < 0L) {
         throw Messages.msg.parameterOutOfRange("timeout");
      } else {
         this.channel = channel;
         this.buffer = ByteBuffer.allocate(bufferSize);
         this.buffer.limit(0);
         long calcTimeout = unit.toNanos(timeout);
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

      int res;
      try {
         StreamSourceChannel channel = this.channel;
         ByteBuffer buffer = this.buffer;
         if (!buffer.hasRemaining()) {
            if (eof) {
               byte var19 = -1;
               return var19;
            }

            long start = System.nanoTime();
            long elapsed = 0L;

            while(true) {
               buffer.clear();

               try {
                  res = channel.read(buffer);
               } finally {
                  buffer.flip();
               }

               if (res == -1) {
                  eof = true;
                  byte var20 = -1;
                  return var20;
               }

               if (res > 0) {
                  int var11 = buffer.get() & 255;
                  return var11;
               }

               long timeout = this.timeout;
               if (timeout == 0L) {
                  channel.awaitReadable();
               } else {
                  if (timeout < elapsed) {
                     throw Messages.msg.readTimeout();
                  }

                  channel.awaitReadable(timeout - elapsed, TimeUnit.NANOSECONDS);
               }

               elapsed = System.nanoTime() - start;
            }
         }

         res = buffer.get() & 255;
      } finally {
         this.exit(eof);
      }

      return res;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (len < 1) {
         return 0;
      } else {
         boolean eof = this.enter();

         try {
            int total = 0;
            ByteBuffer buffer = this.buffer;
            ByteBuffer userBuffer = ByteBuffer.wrap(b, off, len);
            int var22;
            if (buffer.hasRemaining()) {
               total += Buffers.copy(userBuffer, buffer);
               if (!userBuffer.hasRemaining()) {
                  var22 = total;
                  return var22;
               }
            }

            assert !buffer.hasRemaining();

            assert userBuffer.hasRemaining();

            if (eof) {
               var22 = total == 0 ? -1 : total;
               return var22;
            } else {
               StreamSourceChannel channel = this.channel;
               long start = System.nanoTime();
               long elapsed = 0L;

               while(true) {
                  int res = channel.read(userBuffer);
                  int var16;
                  if (res == -1) {
                     eof = true;
                     var16 = total == 0 ? -1 : total;
                     return var16;
                  }

                  total += res;
                  if (total > 0) {
                     var16 = total;
                     return var16;
                  }

                  long timeout = this.timeout;

                  try {
                     if (timeout == 0L) {
                        channel.awaitReadable();
                     } else {
                        if (timeout < elapsed) {
                           throw Messages.msg.readTimeout();
                        }

                        channel.awaitReadable(timeout - elapsed, TimeUnit.NANOSECONDS);
                     }
                  } catch (InterruptedIOException var20) {
                     var20.bytesTransferred = total;
                     throw var20;
                  }

                  elapsed = System.nanoTime() - start;
               }
            }
         } finally {
            this.exit(eof);
         }
      }
   }

   public long skip(long n) throws IOException {
      if (n < 1L) {
         return 0L;
      } else {
         boolean eof = this.enter();

         try {
            n = Math.min(n, 2147483647L);
            long total = 0L;
            ByteBuffer buffer = this.buffer;
            if (buffer.hasRemaining()) {
               int cnt = (int)Math.min((long)buffer.remaining(), n);
               Buffers.skip(buffer, cnt);
               total += (long)cnt;
               n -= (long)cnt;

               assert n == 0L || !buffer.hasRemaining();

               if (n == 0L) {
                  long var8 = total;
                  return var8;
               }
            }

            assert !buffer.hasRemaining();

            long timeout;
            if (eof) {
               timeout = total;
               return timeout;
            } else {
               long start = System.nanoTime();
               long elapsed = 0L;

               long var15;
               while(n != 0L) {
                  long res = Channels.drain(this.channel, n);
                  if (res == -1L) {
                     var15 = total;
                     return var15;
                  }

                  if (res == 0L) {
                     timeout = this.timeout;

                     try {
                        if (timeout == 0L) {
                           this.channel.awaitReadable();
                        } else {
                           if (timeout < elapsed) {
                              throw Messages.msg.readTimeout();
                           }

                           this.channel.awaitReadable(timeout - elapsed, TimeUnit.NANOSECONDS);
                        }
                     } catch (InterruptedIOException var20) {
                        assert total < 2147483647L;

                        var20.bytesTransferred = (int)total;
                        throw var20;
                     }

                     elapsed = System.nanoTime() - start;
                  } else {
                     total += res;
                     n -= res;
                  }
               }

               var15 = total;
               return var15;
            }
         } finally {
            this.exit(eof);
         }
      }
   }

   public int available() throws IOException {
      boolean eof = this.enter();

      int var16;
      try {
         ByteBuffer buffer = this.buffer;
         int rem = buffer.remaining();
         if (rem > 0 || eof) {
            var16 = rem;
            return var16;
         }

         buffer.clear();

         try {
            this.channel.read(buffer);
         } catch (IOException var13) {
            IOException e = var13;
            throw var13;
         } finally {
            buffer.flip();
         }

         var16 = buffer.remaining();
      } finally {
         this.exit(eof);
      }

      return var16;
   }

   public void close() throws IOException {
      this.enter();

      try {
         this.buffer.clear().flip();
         this.channel.shutdownReads();
      } finally {
         this.exit(true);
      }

   }
}
