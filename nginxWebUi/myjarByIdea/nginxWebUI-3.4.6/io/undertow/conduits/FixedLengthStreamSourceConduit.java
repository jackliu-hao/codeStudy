package io.undertow.conduits;

import io.undertow.UndertowMessages;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Bits;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.StreamSourceConduit;

public final class FixedLengthStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   private final ConduitListener<? super FixedLengthStreamSourceConduit> finishListener;
   private long state;
   private static final long FLAG_CLOSED = Long.MIN_VALUE;
   private static final long FLAG_FINISHED = 4611686018427387904L;
   private static final long FLAG_LENGTH_CHECKED = 2305843009213693952L;
   private static final long MASK_COUNT = Bits.longBitMask(0, 60);
   private final HttpServerExchange exchange;

   public FixedLengthStreamSourceConduit(StreamSourceConduit next, long contentLength, ConduitListener<? super FixedLengthStreamSourceConduit> finishListener, HttpServerExchange exchange) {
      super(next);
      this.finishListener = finishListener;
      if (contentLength < 0L) {
         throw new IllegalArgumentException("Content length must be greater than or equal to zero");
      } else if (contentLength > MASK_COUNT) {
         throw new IllegalArgumentException("Content length is too long");
      } else {
         this.state = contentLength;
         this.exchange = exchange;
      }
   }

   public FixedLengthStreamSourceConduit(StreamSourceConduit next, long contentLength, ConduitListener<? super FixedLengthStreamSourceConduit> finishListener) {
      this(next, contentLength, finishListener, (HttpServerExchange)null);
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      long val = this.state;
      this.checkMaxSize(val);
      if (!Bits.anyAreSet(val, -4611686018427387904L) && !Bits.allAreClear(val, MASK_COUNT)) {
         long res = 0L;
         Throwable transferError = null;

         long var11;
         try {
            var11 = res = ((StreamSourceConduit)this.next).transferTo(position, Math.min(count, val & MASK_COUNT), target);
         } catch (RuntimeException | Error | IOException var16) {
            this.closeConnection();
            transferError = var16;
            throw var16;
         } finally {
            this.exitRead(res, transferError);
         }

         return var11;
      } else {
         if (Bits.allAreClear(val, 4611686018427387904L)) {
            this.invokeFinishListener();
         }

         return -1L;
      }
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      if (count == 0L) {
         return 0L;
      } else {
         long val = this.state;
         this.checkMaxSize(val);
         if (!Bits.anyAreSet(val, -4611686018427387904L) && !Bits.allAreClear(val, MASK_COUNT)) {
            long res = 0L;
            Throwable transferError = null;

            long var10;
            try {
               var10 = res = ((StreamSourceConduit)this.next).transferTo(Math.min(count, val & MASK_COUNT), throughBuffer, target);
            } catch (RuntimeException | Error | IOException var15) {
               this.closeConnection();
               transferError = var15;
               throw var15;
            } finally {
               this.exitRead(res + (long)throughBuffer.remaining(), transferError);
            }

            return var10;
         } else {
            if (Bits.allAreClear(val, 4611686018427387904L)) {
               this.invokeFinishListener();
            }

            return -1L;
         }
      }
   }

   private void checkMaxSize(long state) throws IOException {
      if (Bits.anyAreClear(state, 2305843009213693952L)) {
         HttpServerExchange exchange = this.exchange;
         if (exchange != null && exchange.getMaxEntitySize() > 0L && exchange.getMaxEntitySize() < (state & MASK_COUNT)) {
            Connectors.terminateRequest(exchange);
            exchange.setPersistent(false);
            this.finishListener.handleEvent(this);
            this.state |= -4611686018427387904L;
            throw UndertowMessages.MESSAGES.requestEntityWasTooLarge(exchange.getMaxEntitySize());
         }

         this.state |= 2305843009213693952L;
      }

   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      if (length == 0) {
         return 0L;
      } else if (length == 1) {
         return (long)this.read(dsts[offset]);
      } else {
         long val = this.state;
         this.checkMaxSize(val);
         if (!Bits.allAreSet(val, Long.MIN_VALUE) && !Bits.allAreClear(val, MASK_COUNT)) {
            long res = 0L;
            Throwable readError = null;

            long var28;
            try {
               if ((val & MASK_COUNT) == 0L) {
                  long var27 = -1L;
                  return var27;
               }

               long t = 0L;

               for(int i = 0; i < length; ++i) {
                  ByteBuffer buffer = dsts[i + offset];
                  int lim;
                  t += (long)((lim = buffer.limit()) - buffer.position());
                  if (t > (val & MASK_COUNT)) {
                     buffer.limit(lim - (int)(t - (val & MASK_COUNT)));

                     try {
                        long var14 = res = ((StreamSourceConduit)this.next).read(dsts, offset, i + 1);
                        return var14;
                     } finally {
                        buffer.limit(lim);
                     }
                  }
               }

               var28 = res = ((StreamSourceConduit)this.next).read(dsts, offset, length);
            } catch (RuntimeException | Error | IOException var25) {
               this.closeConnection();
               readError = var25;
               throw var25;
            } finally {
               this.exitRead(res, readError);
            }

            return var28;
         } else {
            if (Bits.allAreClear(val, 4611686018427387904L)) {
               this.invokeFinishListener();
            }

            return -1L;
         }
      }
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      return this.read(dsts, 0, dsts.length);
   }

   public int read(ByteBuffer dst) throws IOException {
      long val = this.state;
      this.checkMaxSize(val);
      if (!Bits.allAreSet(val, Long.MIN_VALUE) && !Bits.allAreClear(val, MASK_COUNT)) {
         int res = 0;
         long remaining = val & MASK_COUNT;
         Throwable readError = null;

         int var10;
         try {
            int lim = dst.limit();
            int pos = dst.position();
            if ((long)(lim - pos) > remaining) {
               dst.limit((int)(remaining + (long)pos));

               try {
                  var10 = res = ((StreamSourceConduit)this.next).read(dst);
                  return var10;
               } finally {
                  dst.limit(lim);
               }
            }

            var10 = res = ((StreamSourceConduit)this.next).read(dst);
         } catch (RuntimeException | Error | IOException var20) {
            this.closeConnection();
            readError = var20;
            throw var20;
         } finally {
            this.exitRead((long)res, readError);
         }

         return var10;
      } else {
         if (Bits.allAreClear(val, 4611686018427387904L)) {
            this.invokeFinishListener();
         }

         return -1;
      }
   }

   public boolean isReadResumed() {
      return Bits.allAreClear(this.state, Long.MIN_VALUE) && ((StreamSourceConduit)this.next).isReadResumed();
   }

   public void wakeupReads() {
      long val = this.state;
      if (!Bits.anyAreSet(val, -4611686018427387904L)) {
         ((StreamSourceConduit)this.next).wakeupReads();
      }
   }

   public void resumeReads() {
      long val = this.state;
      if (!Bits.anyAreSet(val, -4611686018427387904L)) {
         if (Bits.allAreClear(val, MASK_COUNT)) {
            ((StreamSourceConduit)this.next).wakeupReads();
         } else {
            ((StreamSourceConduit)this.next).resumeReads();
         }

      }
   }

   public void terminateReads() throws IOException {
      long val = this.enterShutdownReads();
      if (!Bits.allAreSet(val, Long.MIN_VALUE)) {
         this.exitShutdownReads(val);
      }
   }

   public void awaitReadable() throws IOException {
      long val = this.state;
      if (!Bits.allAreSet(val, Long.MIN_VALUE) && val != 0L) {
         ((StreamSourceConduit)this.next).awaitReadable();
      }
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      long val = this.state;
      if (!Bits.allAreSet(val, Long.MIN_VALUE) && val != 0L) {
         try {
            ((StreamSourceConduit)this.next).awaitReadable(time, timeUnit);
         } catch (RuntimeException | Error | IOException var7) {
            this.closeConnection();
            throw var7;
         }
      }
   }

   public long getRemaining() {
      return this.state & MASK_COUNT;
   }

   private long enterShutdownReads() {
      long oldVal = this.state;
      if (Bits.anyAreSet(oldVal, Long.MIN_VALUE)) {
         return oldVal;
      } else {
         long newVal = oldVal | Long.MIN_VALUE;
         this.state = newVal;
         return oldVal;
      }
   }

   private void exitShutdownReads(long oldVal) {
      if (!Bits.allAreClear(oldVal, MASK_COUNT)) {
         this.invokeFinishListener();
      }

   }

   private void exitRead(long consumed, Throwable readError) throws IOException {
      long oldVal = this.state;
      if (consumed == -1L) {
         if (Bits.anyAreSet(oldVal, MASK_COUNT)) {
            this.invokeFinishListener();
            this.state &= ~MASK_COUNT;
            IOException couldNotReadAll = UndertowMessages.MESSAGES.couldNotReadContentLengthData();
            if (readError != null) {
               couldNotReadAll.addSuppressed(readError);
            }

            throw couldNotReadAll;
         }
      } else {
         long newVal = oldVal - consumed;
         this.state = newVal;
      }
   }

   private void invokeFinishListener() {
      this.state |= 4611686018427387904L;
      this.finishListener.handleEvent(this);
   }

   private void closeConnection() {
      HttpServerExchange exchange = this.exchange;
      if (exchange != null) {
         IoUtils.safeClose((Closeable)exchange.getConnection());
      }

   }
}
