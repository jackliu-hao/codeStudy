package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Buffers;
import org.xnio.Pooled;
import org.xnio.channels.StreamSinkChannel;

public final class PushBackStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> implements StreamSourceConduit {
   private StreamSourceConduit current;
   private boolean shutdown;

   public PushBackStreamSourceConduit(StreamSourceConduit next) {
      super(next);
      this.current = (StreamSourceConduit)this.next;
   }

   public void resumeReads() {
      this.current.resumeReads();
   }

   public int read(ByteBuffer dst) throws IOException {
      return this.current.read(dst);
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      return this.current.read(dsts, offs, len);
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return this.current.transferTo(position, count, target);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return this.current.transferTo(count, throughBuffer, target);
   }

   public void awaitReadable() throws IOException {
      this.current.awaitReadable();
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      this.current.awaitReadable(time, timeUnit);
   }

   public void terminateReads() throws IOException {
      this.shutdown = true;
      this.current.terminateReads();
   }

   public void setReadReadyHandler(ReadReadyHandler handler) {
      this.current.setReadReadyHandler(handler);
   }

   public void pushBack(Pooled<ByteBuffer> pooledBuffer) {
      if (pooledBuffer != null) {
         if (!this.shutdown && ((ByteBuffer)pooledBuffer.getResource()).hasRemaining()) {
            this.current = new BufferConduit(this.current, pooledBuffer);
         } else {
            pooledBuffer.free();
         }

      }
   }

   class BufferConduit extends AbstractStreamSourceConduit<StreamSourceConduit> implements StreamSourceConduit {
      private final Pooled<ByteBuffer> pooledBuffer;

      BufferConduit(StreamSourceConduit next, Pooled<ByteBuffer> pooledBuffer) {
         super(next);
         this.pooledBuffer = pooledBuffer;
      }

      public void resumeReads() {
         ((StreamSourceConduit)this.next).wakeupReads();
      }

      public void terminateReads() throws IOException {
         try {
            super.terminateReads();
         } finally {
            if (this.pooledBuffer != null) {
               this.pooledBuffer.free();
            }

            ((StreamSourceConduit)this.next).terminateReads();
         }

      }

      public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      }

      public void awaitReadable() throws IOException {
      }

      public int read(ByteBuffer dst) throws IOException {
         if (!dst.hasRemaining()) {
            return 0;
         } else {
            StreamSourceConduit next = (StreamSourceConduit)this.next;

            int cnt;
            try {
               ByteBuffer src = (ByteBuffer)this.pooledBuffer.getResource();
               cnt = Buffers.copy(dst, src);
               if (src.hasRemaining()) {
                  return cnt;
               }

               this.moveToNext();
               if (cnt > 0 && next == PushBackStreamSourceConduit.this.next) {
                  return cnt;
               }
            } catch (IllegalStateException var5) {
               this.moveToNext();
               cnt = 0;
            }

            int res = next.read(dst);
            return res > 0 ? res + cnt : (cnt > 0 ? cnt : res);
         }
      }

      public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
         StreamSourceConduit next = (StreamSourceConduit)this.next;

         long cnt;
         try {
            ByteBuffer src = (ByteBuffer)this.pooledBuffer.getResource();
            cnt = (long)Buffers.copy(dsts, offs, len, src);
            if (src.hasRemaining()) {
               return cnt;
            }

            this.moveToNext();
            if (cnt > 0L && next == PushBackStreamSourceConduit.this.next) {
               return cnt;
            }
         } catch (IllegalStateException var9) {
            this.moveToNext();
            cnt = 0L;
         }

         long res = next.read(dsts, offs, len);
         return res > 0L ? res + cnt : (cnt > 0L ? cnt : res);
      }

      public long transferTo(long position, long count, FileChannel target) throws IOException {
         long cnt;
         try {
            ByteBuffer src = (ByteBuffer)this.pooledBuffer.getResource();
            int pos = src.position();
            int rem = src.remaining();
            if ((long)rem > count) {
               long var11;
               try {
                  src.limit(pos + (int)count);
                  var11 = (long)target.write(src, position);
               } finally {
                  src.limit(pos + rem);
               }

               return var11;
            }

            cnt = (long)target.write(src, position);
            if (cnt != (long)rem) {
               return cnt;
            }

            this.moveToNext();
            position += cnt;
            count -= cnt;
         } catch (IllegalStateException var17) {
            this.moveToNext();
            cnt = 0L;
         }

         return cnt + ((StreamSourceConduit)this.next).transferTo(position, count, target);
      }

      public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
         long cnt;
         try {
            ByteBuffer src = (ByteBuffer)this.pooledBuffer.getResource();
            int pos = src.position();
            int rem = src.remaining();
            if ((long)rem > count) {
               long var11;
               try {
                  src.limit(pos + (int)count);
                  int resx = target.write(src);
                  if (resx == 0) {
                     throughBuffer.clear();
                     Buffers.copy(throughBuffer, src);
                     throughBuffer.flip();
                  } else {
                     throughBuffer.clear();
                     throughBuffer.flip();
                  }

                  var11 = (long)resx;
               } finally {
                  src.limit(pos + rem);
               }

               return var11;
            }

            cnt = (long)target.write(src);
            if (cnt != (long)rem) {
               if (cnt == 0L) {
                  throughBuffer.clear();
                  Buffers.copy(throughBuffer, src);
                  throughBuffer.flip();
               } else {
                  throughBuffer.clear();
                  throughBuffer.flip();
               }

               return cnt;
            }

            this.moveToNext();
         } catch (IllegalStateException var17) {
            this.moveToNext();
            cnt = 0L;
         }

         long res = ((StreamSourceConduit)this.next).transferTo(count - cnt, throughBuffer, target);
         return res > 0L ? cnt + res : (cnt > 0L ? cnt : res);
      }

      private final void moveToNext() {
         PushBackStreamSourceConduit.this.current = (StreamSourceConduit)this.next;
         this.pooledBuffer.free();
      }
   }
}
