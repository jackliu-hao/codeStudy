package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSinkChannel;

public final class FixedLengthStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> implements StreamSourceConduit {
   private long remaining;

   public FixedLengthStreamSourceConduit(StreamSourceConduit next, long remaining) {
      super(next);
      this.remaining = remaining;
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      long length = this.remaining;
      if (length > 0L) {
         long res = ((StreamSourceConduit)this.next).transferTo(position, Math.min(count, length), target);
         if (res > 0L) {
            this.remaining = length - res;
         }

         return res;
      } else {
         return 0L;
      }
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      long length = this.remaining;
      if (length > 0L) {
         long res = ((StreamSourceConduit)this.next).transferTo(Math.min(count, length), throughBuffer, target);
         if (res > 0L) {
            this.remaining = length - res;
         }

         return res;
      } else {
         return -1L;
      }
   }

   public int read(ByteBuffer dst) throws IOException {
      int limit = dst.limit();
      int pos = dst.position();
      long length = this.remaining;
      if (length == 0L) {
         return -1;
      } else {
         int res;
         if ((long)(limit - pos) > length) {
            dst.limit(pos + (int)length);

            try {
               res = ((StreamSourceConduit)this.next).read(dst);
            } finally {
               dst.limit(limit);
            }
         } else {
            res = ((StreamSourceConduit)this.next).read(dst);
         }

         if ((long)res > 0L) {
            this.remaining = length - (long)res;
         }

         return res;
      }
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      if (len == 0) {
         return 0L;
      } else if (len == 1) {
         return (long)this.read(dsts[offs]);
      } else {
         long length = this.remaining;
         if (length == 0L) {
            return -1L;
         } else {
            long t = 0L;

            long res;
            for(int i = 0; (long)i < length; ++i) {
               ByteBuffer buffer = dsts[i + offs];
               int lim;
               t += (long)((lim = buffer.limit()) - buffer.position());
               if (t > length) {
                  buffer.limit(lim - (int)(t - length));

                  long var13;
                  try {
                     res = ((StreamSourceConduit)this.next).read(dsts, offs, i + 1);
                     if (res > 0L) {
                        this.remaining = length - res;
                     }

                     var13 = res;
                  } finally {
                     buffer.limit(lim);
                  }

                  return var13;
               }
            }

            res = t == 0L ? 0L : ((StreamSourceConduit)this.next).read(dsts, offs, len);
            if (res > 0L) {
               this.remaining = length - res;
            }

            return res;
         }
      }
   }

   public long getRemaining() {
      return this.remaining;
   }
}
