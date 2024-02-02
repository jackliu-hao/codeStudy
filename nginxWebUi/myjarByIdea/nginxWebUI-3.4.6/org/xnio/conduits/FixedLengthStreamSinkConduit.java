package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio._private.Messages;
import org.xnio.channels.StreamSourceChannel;

public final class FixedLengthStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> implements StreamSinkConduit {
   private long remaining;

   public FixedLengthStreamSinkConduit(FixedLengthStreamSinkConduit next) {
      super(next);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      if (count == 0L) {
         return 0L;
      } else {
         long remaining = this.remaining;
         if (remaining == 0L) {
            throw Messages.msg.fixedOverflow();
         } else {
            long res = 0L;

            long var10;
            try {
               var10 = res = ((StreamSinkConduit)this.next).transferFrom(src, position, Math.min(count, remaining));
            } finally {
               this.remaining = remaining - res;
            }

            return var10;
         }
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      if (count == 0L) {
         return 0L;
      } else {
         long remaining = this.remaining;
         if (remaining == 0L) {
            throw Messages.msg.fixedOverflow();
         } else {
            long res = 0L;

            long var9;
            try {
               var9 = res = ((StreamSinkConduit)this.next).transferFrom(source, Math.min(count, remaining), throughBuffer);
            } finally {
               this.remaining = remaining - res;
            }

            return var9;
         }
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return this.write(src, true);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return this.write(srcs, offset, length, true);
   }

   public int write(ByteBuffer src) throws IOException {
      return this.write(src, false);
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      return this.write(srcs, offs, len, false);
   }

   private int write(ByteBuffer src, boolean writeFinal) throws IOException {
      if (!src.hasRemaining()) {
         return 0;
      } else {
         int res = 0;
         long remaining = this.remaining;
         if (remaining == 0L) {
            throw Messages.msg.fixedOverflow();
         } else {
            int var8;
            try {
               int lim = src.limit();
               int pos = src.position();
               if ((long)(lim - pos) > remaining) {
                  src.limit((int)(remaining - (long)pos));

                  try {
                     var8 = res = this.doWrite(src, writeFinal);
                     return var8;
                  } finally {
                     src.limit(lim);
                  }
               }

               var8 = res = this.doWrite(src, writeFinal);
            } finally {
               this.remaining = remaining - (long)res;
            }

            return var8;
         }
      }
   }

   private long write(ByteBuffer[] srcs, int offs, int len, boolean writeFinal) throws IOException {
      if (len == 0) {
         return 0L;
      } else if (len == 1) {
         return (long)this.write(srcs[offs], writeFinal);
      } else {
         long remaining = this.remaining;
         if (remaining == 0L) {
            throw Messages.msg.fixedOverflow();
         } else {
            long res = 0L;

            long var24;
            try {
               long t = 0L;

               for(int i = 0; i < len; ++i) {
                  ByteBuffer buffer = srcs[i + offs];
                  int lim;
                  t += (long)((lim = buffer.limit()) - buffer.position());
                  if (t > remaining) {
                     buffer.limit(lim - (int)(t - remaining));

                     try {
                        long var14 = res = this.doWrite(srcs, offs, i + 1, writeFinal);
                        return var14;
                     } finally {
                        buffer.limit(lim);
                     }
                  }
               }

               if (t != 0L) {
                  var24 = res = this.doWrite(srcs, offs, len, writeFinal);
                  return var24;
               }

               var24 = 0L;
            } finally {
               this.remaining = remaining - res;
            }

            return var24;
         }
      }
   }

   private long doWrite(ByteBuffer[] srcs, int offs, int len, boolean writeFinal) throws IOException {
      return writeFinal ? ((StreamSinkConduit)this.next).writeFinal(srcs, offs, len) : ((StreamSinkConduit)this.next).write(srcs, offs, len);
   }

   private int doWrite(ByteBuffer src, boolean writeFinal) throws IOException {
      return writeFinal ? ((StreamSinkConduit)this.next).writeFinal(src) : ((StreamSinkConduit)this.next).write(src);
   }

   public void terminateWrites() throws IOException {
      ((StreamSinkConduit)this.next).terminateWrites();
      if (this.remaining > 0L) {
         throw Messages.msg.fixedOverflow();
      }
   }

   public void truncateWrites() throws IOException {
      ((StreamSinkConduit)this.next).terminateWrites();
      if (this.remaining > 0L) {
         throw Messages.msg.fixedOverflow();
      }
   }

   public long getRemaining() {
      return this.remaining;
   }
}
