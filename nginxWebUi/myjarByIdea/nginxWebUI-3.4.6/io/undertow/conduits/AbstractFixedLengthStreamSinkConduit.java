package io.undertow.conduits;

import io.undertow.UndertowLogger;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.channels.FixedLengthOverflowException;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;

public abstract class AbstractFixedLengthStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private int config;
   private long state;
   private boolean broken = false;
   private static final int CONF_FLAG_CONFIGURABLE = 1;
   private static final int CONF_FLAG_PASS_CLOSE = 2;
   private static final long FLAG_CLOSE_REQUESTED = Long.MIN_VALUE;
   private static final long FLAG_CLOSE_COMPLETE = 4611686018427387904L;
   private static final long FLAG_FINISHED_CALLED = 2305843009213693952L;
   private static final long MASK_COUNT = Bits.longBitMask(0, 60);

   public AbstractFixedLengthStreamSinkConduit(StreamSinkConduit next, long contentLength, boolean configurable, boolean propagateClose) {
      super(next);
      if (contentLength < 0L) {
         throw new IllegalArgumentException("Content length must be greater than or equal to zero");
      } else if (contentLength > MASK_COUNT) {
         throw new IllegalArgumentException("Content length is too long");
      } else {
         this.config = (configurable ? 1 : 0) | (propagateClose ? 2 : 0);
         this.state = contentLength;
      }
   }

   protected void reset(long contentLength, boolean propagateClose) {
      this.state = contentLength;
      if (propagateClose) {
         this.config |= 2;
      } else {
         this.config &= -3;
      }

   }

   public int write(ByteBuffer src) throws IOException {
      long val = this.state;
      long remaining = val & MASK_COUNT;
      if (!src.hasRemaining()) {
         return 0;
      } else if (Bits.allAreSet(val, Long.MIN_VALUE)) {
         throw new ClosedChannelException();
      } else {
         int oldLimit = src.limit();
         if (remaining == 0L) {
            throw new FixedLengthOverflowException();
         } else {
            if ((long)src.remaining() > remaining) {
               src.limit((int)((long)src.position() + remaining));
            }

            int res = 0;

            int var8;
            try {
               var8 = res = ((StreamSinkConduit)this.next).write(src);
            } catch (RuntimeException | Error | IOException var12) {
               this.broken = true;
               throw var12;
            } finally {
               src.limit(oldLimit);
               this.exitWrite(val, (long)res);
            }

            return var8;
         }
      }
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (length == 0) {
         return 0L;
      } else if (length == 1) {
         return (long)this.write(srcs[offset]);
      } else {
         long val = this.state;
         long remaining = val & MASK_COUNT;
         if (Bits.allAreSet(val, Long.MIN_VALUE)) {
            throw new ClosedChannelException();
         } else {
            long toWrite = Buffers.remaining(srcs, offset, length);
            if (remaining == 0L) {
               throw new FixedLengthOverflowException();
            } else {
               int[] limits = null;
               long res;
               if (toWrite > remaining) {
                  limits = new int[length];
                  res = remaining;

                  for(int i = offset; i < offset + length; ++i) {
                     limits[i - offset] = srcs[i].limit();
                     int br = srcs[i].remaining();
                     if ((long)br < res) {
                        res -= (long)br;
                     } else {
                        srcs[i].limit((int)((long)srcs[i].position() + res));
                        res = 0L;
                     }
                  }
               }

               res = 0L;
               boolean var20 = false;

               long var23;
               try {
                  var20 = true;
                  var23 = res = ((StreamSinkConduit)this.next).write(srcs, offset, length);
                  var20 = false;
               } catch (RuntimeException | Error | IOException var21) {
                  this.broken = true;
                  throw var21;
               } finally {
                  if (var20) {
                     if (limits != null) {
                        for(int i = offset; i < offset + length; ++i) {
                           srcs[i].limit(limits[i - offset]);
                        }
                     }

                     this.exitWrite(val, res);
                  }
               }

               if (limits != null) {
                  for(int i = offset; i < offset + length; ++i) {
                     srcs[i].limit(limits[i - offset]);
                  }
               }

               this.exitWrite(val, res);
               return var23;
            }
         }
      }
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      try {
         return Conduits.writeFinalBasic(this, srcs, offset, length);
      } catch (RuntimeException | Error | IOException var5) {
         this.broken = true;
         throw var5;
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      try {
         return Conduits.writeFinalBasic(this, src);
      } catch (RuntimeException | Error | IOException var3) {
         this.broken = true;
         throw var3;
      }
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      if (count == 0L) {
         return 0L;
      } else {
         long val = this.state;
         if (Bits.allAreSet(val, Long.MIN_VALUE)) {
            throw new ClosedChannelException();
         } else if (Bits.allAreClear(val, MASK_COUNT)) {
            throw new FixedLengthOverflowException();
         } else {
            long res = 0L;

            long var10;
            try {
               var10 = res = ((StreamSinkConduit)this.next).transferFrom(src, position, Math.min(count, val & MASK_COUNT));
            } catch (RuntimeException | Error | IOException var15) {
               this.broken = true;
               throw var15;
            } finally {
               this.exitWrite(val, res);
            }

            return var10;
         }
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      if (count == 0L) {
         return 0L;
      } else {
         long val = this.state;
         if (Bits.allAreSet(val, Long.MIN_VALUE)) {
            throw new ClosedChannelException();
         } else if (Bits.allAreClear(val, MASK_COUNT)) {
            throw new FixedLengthOverflowException();
         } else {
            long res = 0L;

            long var9;
            try {
               var9 = res = ((StreamSinkConduit)this.next).transferFrom(source, Math.min(count, val & MASK_COUNT), throughBuffer);
            } catch (RuntimeException | Error | IOException var14) {
               this.broken = true;
               throw var14;
            } finally {
               this.exitWrite(val, res);
            }

            return var9;
         }
      }
   }

   public boolean flush() throws IOException {
      long val = this.state;
      if (Bits.anyAreSet(val, 4611686018427387904L)) {
         return true;
      } else {
         boolean flushed = false;

         boolean var4;
         try {
            var4 = flushed = ((StreamSinkConduit)this.next).flush();
         } catch (RuntimeException | Error | IOException var8) {
            this.broken = true;
            throw var8;
         } finally {
            this.exitFlush(val, flushed);
         }

         return var4;
      }
   }

   public boolean isWriteResumed() {
      return Bits.allAreClear(this.state, 4611686018427387904L) && ((StreamSinkConduit)this.next).isWriteResumed();
   }

   public void wakeupWrites() {
      long val = this.state;
      if (!Bits.anyAreSet(val, 4611686018427387904L)) {
         ((StreamSinkConduit)this.next).wakeupWrites();
      }
   }

   public void terminateWrites() throws IOException {
      long val = this.enterShutdown();
      if (Bits.anyAreSet(val, MASK_COUNT) && !this.broken) {
         UndertowLogger.REQUEST_IO_LOGGER.debugf("Fixed length stream closed with with %s bytes remaining", val & MASK_COUNT);

         try {
            ((StreamSinkConduit)this.next).truncateWrites();
         } finally {
            if (!Bits.anyAreSet(this.state, 2305843009213693952L)) {
               this.state |= 2305843009213693952L;
               this.channelFinished();
            }

         }
      } else if (Bits.allAreSet(this.config, 2)) {
         ((StreamSinkConduit)this.next).terminateWrites();
      }

   }

   public void truncateWrites() throws IOException {
      try {
         if (!Bits.anyAreSet(this.state, 2305843009213693952L)) {
            this.state |= 2305843009213693952L;
            this.channelFinished();
         }
      } finally {
         super.truncateWrites();
      }

   }

   public void awaitWritable() throws IOException {
      ((StreamSinkConduit)this.next).awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      ((StreamSinkConduit)this.next).awaitWritable(time, timeUnit);
   }

   public long getRemaining() {
      return this.state & MASK_COUNT;
   }

   private void exitWrite(long oldVal, long consumed) {
      long newVal = oldVal - consumed;
      this.state = newVal;
   }

   private void exitFlush(long oldVal, boolean flushed) {
      boolean callFinish = false;
      if ((Bits.anyAreSet(oldVal, Long.MIN_VALUE) || (oldVal & MASK_COUNT) == 0L) && flushed) {
         long newVal = oldVal | 4611686018427387904L;
         if (!Bits.anyAreSet(oldVal, 2305843009213693952L) && (newVal & MASK_COUNT) == 0L) {
            newVal |= 2305843009213693952L;
            callFinish = true;
         }

         this.state = newVal;
         if (callFinish) {
            this.channelFinished();
         }
      }

   }

   protected void channelFinished() {
   }

   private long enterShutdown() {
      long oldVal = this.state;
      if (Bits.anyAreSet(oldVal, -4611686018427387904L)) {
         return oldVal;
      } else {
         long newVal = oldVal | Long.MIN_VALUE;
         if (Bits.anyAreSet(oldVal, MASK_COUNT)) {
            newVal |= 4611686018427387904L;
         }

         this.state = newVal;
         return oldVal;
      }
   }
}
