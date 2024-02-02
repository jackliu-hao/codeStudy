package io.undertow.conduits;

import io.undertow.util.WorkerUtils;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.StreamSinkConduit;

public class RateLimitingStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private final long time;
   private final int bytes;
   private boolean writesResumed = false;
   private int byteCount = 0;
   private long startTime = 0L;
   private long nextSendTime = 0L;
   private boolean scheduled = false;

   public RateLimitingStreamSinkConduit(StreamSinkConduit next, int bytes, long time, TimeUnit timeUnit) {
      super(next);
      this.writesResumed = next.isWriteResumed();
      this.time = timeUnit.toMillis(time);
      this.bytes = bytes;
   }

   public int write(ByteBuffer src) throws IOException {
      if (!this.canSend()) {
         return 0;
      } else {
         int bytes = this.bytes - this.byteCount;
         int old = src.limit();
         if (src.remaining() > bytes) {
            src.limit(src.position() + bytes);
         }

         int var5;
         try {
            int written = super.write(src);
            this.handleWritten((long)written);
            var5 = written;
         } finally {
            src.limit(old);
         }

         return var5;
      }
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      if (!this.canSend()) {
         return 0L;
      } else {
         int bytes = this.bytes - this.byteCount;
         long written = super.transferFrom(src, position, Math.min(count, (long)bytes));
         this.handleWritten(written);
         return written;
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      if (!this.canSend()) {
         return 0L;
      } else {
         int bytes = this.bytes - this.byteCount;
         long written = super.transferFrom(source, Math.min(count, (long)bytes), throughBuffer);
         this.handleWritten(written);
         return written;
      }
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      if (!this.canSend()) {
         return 0L;
      } else {
         int old = 0;
         int adjPos = -1;
         long rem = (long)(this.bytes - this.byteCount);

         for(int i = offs; i < offs + len; ++i) {
            ByteBuffer buf = srcs[i];
            rem -= (long)buf.remaining();
            if (rem < 0L) {
               adjPos = i;
               old = buf.limit();
               buf.limit((int)((long)buf.limit() + rem));
               break;
            }
         }

         boolean var16 = false;

         long var10;
         try {
            var16 = true;
            long written;
            if (adjPos == -1) {
               written = super.write(srcs, offs, len);
            } else {
               written = super.write(srcs, offs, adjPos - offs + 1);
            }

            this.handleWritten(written);
            var10 = written;
            var16 = false;
         } finally {
            if (var16) {
               if (adjPos != -1) {
                  ByteBuffer buf = srcs[adjPos];
                  buf.limit(old);
               }

            }
         }

         if (adjPos != -1) {
            ByteBuffer buf = srcs[adjPos];
            buf.limit(old);
         }

         return var10;
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      if (!this.canSend()) {
         return 0;
      } else {
         int bytes = this.bytes - this.byteCount;
         int old = src.limit();
         if (src.remaining() > bytes) {
            src.limit(src.position() + bytes);
         }

         int var5;
         try {
            int written = super.writeFinal(src);
            this.handleWritten((long)written);
            var5 = written;
         } finally {
            src.limit(old);
         }

         return var5;
      }
   }

   public long writeFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
      if (!this.canSend()) {
         return 0L;
      } else {
         int old = 0;
         int adjPos = -1;
         long rem = (long)(this.bytes - this.byteCount);

         for(int i = offs; i < offs + len; ++i) {
            ByteBuffer buf = srcs[i];
            rem -= (long)buf.remaining();
            if (rem < 0L) {
               adjPos = i;
               old = buf.limit();
               buf.limit((int)((long)buf.limit() + rem));
               break;
            }
         }

         boolean var16 = false;

         long var10;
         try {
            var16 = true;
            long written;
            if (adjPos == -1) {
               written = super.writeFinal(srcs, offs, len);
            } else {
               written = super.writeFinal(srcs, offs, adjPos - offs + 1);
            }

            this.handleWritten(written);
            var10 = written;
            var16 = false;
         } finally {
            if (var16) {
               if (adjPos != -1) {
                  ByteBuffer buf = srcs[adjPos];
                  buf.limit(old);
               }

            }
         }

         if (adjPos != -1) {
            ByteBuffer buf = srcs[adjPos];
            buf.limit(old);
         }

         return var10;
      }
   }

   public void resumeWrites() {
      this.writesResumed = true;
      if (this.canSend()) {
         super.resumeWrites();
      }

   }

   public void suspendWrites() {
      this.writesResumed = false;
      super.suspendWrites();
   }

   public void wakeupWrites() {
      this.writesResumed = true;
      if (this.canSend()) {
         super.wakeupWrites();
      }

   }

   public boolean isWriteResumed() {
      return this.writesResumed;
   }

   public void awaitWritable() throws IOException {
      long toGo = this.nextSendTime - System.currentTimeMillis();
      if (toGo > 0L) {
         try {
            Thread.sleep(toGo);
         } catch (InterruptedException var4) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException();
         }
      }

      super.awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      long toGo = this.nextSendTime - System.currentTimeMillis();
      if (toGo > 0L) {
         try {
            Thread.sleep(Math.min(toGo, timeUnit.toMillis(time)));
         } catch (InterruptedException var7) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException();
         }
      } else {
         super.awaitWritable(time, timeUnit);
      }
   }

   private boolean canSend() {
      if (this.byteCount < this.bytes) {
         return true;
      } else if (System.currentTimeMillis() > this.nextSendTime) {
         this.byteCount = 0;
         this.startTime = 0L;
         this.nextSendTime = 0L;
         return true;
      } else {
         if (this.writesResumed) {
            this.handleWritesResumedWhenBlocked();
         }

         return false;
      }
   }

   private void handleWritten(long written) {
      if (written != 0L) {
         this.byteCount = (int)((long)this.byteCount + written);
         if (this.byteCount < this.bytes) {
            if (this.startTime == 0L) {
               this.startTime = System.currentTimeMillis();
               this.nextSendTime = System.currentTimeMillis() + this.time;
            }
         } else {
            if (this.startTime == 0L) {
               this.startTime = System.currentTimeMillis();
            }

            this.nextSendTime = this.startTime + this.time;
            if (this.writesResumed) {
               this.handleWritesResumedWhenBlocked();
            }
         }

      }
   }

   private void handleWritesResumedWhenBlocked() {
      if (!this.scheduled) {
         this.scheduled = true;
         ((StreamSinkConduit)this.next).suspendWrites();
         long millis = this.nextSendTime - System.currentTimeMillis();
         WorkerUtils.executeAfter(this.getWriteThread(), new Runnable() {
            public void run() {
               RateLimitingStreamSinkConduit.this.scheduled = false;
               if (RateLimitingStreamSinkConduit.this.writesResumed) {
                  ((StreamSinkConduit)RateLimitingStreamSinkConduit.this.next).wakeupWrites();
               }

            }
         }, millis, TimeUnit.MILLISECONDS);
      }
   }
}
