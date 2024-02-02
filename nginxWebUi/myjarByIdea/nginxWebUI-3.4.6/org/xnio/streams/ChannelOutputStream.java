package org.xnio.streams;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.Bits;
import org.xnio._private.Messages;
import org.xnio.channels.StreamSinkChannel;

public class ChannelOutputStream extends OutputStream {
   protected final StreamSinkChannel channel;
   private volatile int flags;
   private volatile long timeout;
   private static final AtomicIntegerFieldUpdater<ChannelOutputStream> flagsUpdater = AtomicIntegerFieldUpdater.newUpdater(ChannelOutputStream.class, "flags");
   private static final int FLAG_CLOSED = 2;
   private static final int FLAG_ENTERED = 1;

   public ChannelOutputStream(StreamSinkChannel channel) {
      if (channel == null) {
         throw Messages.msg.nullParameter("channel");
      } else {
         this.channel = channel;
      }
   }

   public ChannelOutputStream(StreamSinkChannel channel, long timeout, TimeUnit unit) {
      if (channel == null) {
         throw Messages.msg.nullParameter("channel");
      } else if (unit == null) {
         throw Messages.msg.nullParameter("unit");
      } else if (timeout < 0L) {
         throw Messages.msg.parameterOutOfRange("timeout");
      } else {
         this.channel = channel;
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

   public long getWriteTimeout(TimeUnit unit) {
      if (unit == null) {
         throw Messages.msg.nullParameter("unit");
      } else {
         return unit.convert(this.timeout, TimeUnit.NANOSECONDS);
      }
   }

   public void setWriteTimeout(long timeout, TimeUnit unit) {
      if (timeout < 0L) {
         throw Messages.msg.parameterOutOfRange("timeout");
      } else if (unit == null) {
         throw Messages.msg.nullParameter("unit");
      } else {
         long calcTimeout = unit.toNanos(timeout);
         this.timeout = timeout == 0L ? 0L : (calcTimeout < 1L ? 1L : calcTimeout);
      }
   }

   public void write(int b) throws IOException {
      boolean closed = this.enter();

      try {
         if (closed) {
            throw Messages.msg.streamClosed();
         }

         StreamSinkChannel channel = this.channel;
         ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte)b});
         int res = channel.write(buffer);
         if (res == 0) {
            long start = System.nanoTime();
            long elapsed = 0L;

            do {
               long timeout = this.timeout;
               if (timeout == 0L) {
                  channel.awaitWritable();
               } else {
                  if (timeout < elapsed) {
                     throw Messages.msg.writeTimeout();
                  }

                  channel.awaitWritable(timeout - elapsed, TimeUnit.NANOSECONDS);
               }

               elapsed = System.nanoTime() - start;
               res = channel.write(buffer);
            } while(res == 0);
         }
      } finally {
         this.exit(closed);
      }

   }

   public void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      if (len >= 1) {
         boolean closed = this.enter();

         try {
            if (closed) {
               throw Messages.msg.streamClosed();
            }

            StreamSinkChannel channel = this.channel;
            ByteBuffer buffer = ByteBuffer.wrap(b, off, len);

            while(buffer.hasRemaining()) {
               int res = channel.write(buffer);
               if (res == 0) {
                  long start = System.nanoTime();
                  long elapsed = 0L;

                  while(true) {
                     long timeout = this.timeout;

                     try {
                        if (timeout == 0L) {
                           channel.awaitWritable();
                        } else {
                           if (timeout < elapsed) {
                              throw Messages.msg.writeTimeout();
                           }

                           channel.awaitWritable(timeout - elapsed, TimeUnit.NANOSECONDS);
                        }
                     } catch (InterruptedIOException var18) {
                        var18.bytesTransferred = buffer.position() - off;
                        throw var18;
                     }

                     elapsed = System.nanoTime() - start;
                     res = channel.write(buffer);
                     if (res != 0) {
                        break;
                     }
                  }
               }
            }
         } finally {
            this.exit(closed);
         }

      }
   }

   public void flush() throws IOException {
      boolean closed = this.enter();

      try {
         StreamSinkChannel channel = this.channel;
         if (!channel.flush()) {
            long start = System.nanoTime();
            long elapsed = 0L;

            do {
               long timeout = this.timeout;
               if (timeout == 0L) {
                  channel.awaitWritable();
               } else {
                  if (timeout < elapsed) {
                     throw Messages.msg.writeTimeout();
                  }

                  channel.awaitWritable(timeout - elapsed, TimeUnit.NANOSECONDS);
               }

               elapsed = System.nanoTime() - start;
            } while(!channel.flush());
         }
      } finally {
         this.exit(closed);
      }

   }

   public void close() throws IOException {
      boolean closed = this.enter();

      try {
         if (closed) {
            return;
         }

         StreamSinkChannel channel = this.channel;
         channel.shutdownWrites();
         if (!channel.flush()) {
            long start = System.nanoTime();
            long elapsed = 0L;

            do {
               long timeout = this.timeout;
               if (timeout == 0L) {
                  channel.awaitWritable();
               } else {
                  if (timeout < elapsed) {
                     throw Messages.msg.writeTimeout();
                  }

                  channel.awaitWritable(timeout - elapsed, TimeUnit.NANOSECONDS);
               }

               elapsed = System.nanoTime() - start;
            } while(!channel.flush());
         }
      } finally {
         this.exit(true);
      }

   }
}
