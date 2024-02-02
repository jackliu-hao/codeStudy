package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSinkChannel;

public final class BlockingStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   private boolean resumed;

   public BlockingStreamSourceConduit(StreamSourceConduit next) {
      super(next);
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      if (this.resumed) {
         return ((StreamSourceConduit)this.next).transferTo(position, count, target);
      } else {
         long res;
         while((res = ((StreamSourceConduit)this.next).transferTo(position, count, target)) == 0L) {
            ((StreamSourceConduit)this.next).awaitReadable();
         }

         return res;
      }
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      if (this.resumed) {
         return ((StreamSourceConduit)this.next).transferTo(count, throughBuffer, target);
      } else {
         long res;
         while((res = ((StreamSourceConduit)this.next).transferTo(count, throughBuffer, target)) == 0L && !throughBuffer.hasRemaining()) {
            ((StreamSourceConduit)this.next).awaitReadable();
         }

         return res;
      }
   }

   public int read(ByteBuffer dst) throws IOException {
      if (this.resumed) {
         return ((StreamSourceConduit)this.next).read(dst);
      } else {
         int res;
         while((res = ((StreamSourceConduit)this.next).read(dst)) == 0) {
            ((StreamSourceConduit)this.next).awaitReadable();
         }

         return res;
      }
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      if (this.resumed) {
         return ((StreamSourceConduit)this.next).read(dsts, offs, len);
      } else {
         long res;
         while((res = ((StreamSourceConduit)this.next).read(dsts, offs, len)) == 0L) {
            ((StreamSourceConduit)this.next).awaitReadable();
         }

         return res;
      }
   }

   public void resumeReads() {
      this.resumed = true;
      ((StreamSourceConduit)this.next).resumeReads();
   }

   public void wakeupReads() {
      this.resumed = true;
      ((StreamSourceConduit)this.next).wakeupReads();
   }

   public void suspendReads() {
      this.resumed = false;
      ((StreamSourceConduit)this.next).suspendReads();
   }

   public boolean isReadResumed() {
      return this.resumed;
   }
}
