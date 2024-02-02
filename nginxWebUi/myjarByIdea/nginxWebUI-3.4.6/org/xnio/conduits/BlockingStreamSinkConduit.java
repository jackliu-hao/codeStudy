package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSourceChannel;

public final class BlockingStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private boolean resumed;

   public BlockingStreamSinkConduit(StreamSinkConduit next) {
      super(next);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      if (this.resumed) {
         return ((StreamSinkConduit)this.next).transferFrom(src, position, count);
      } else {
         long res;
         while((res = ((StreamSinkConduit)this.next).transferFrom(src, position, count)) == 0L) {
            ((StreamSinkConduit)this.next).awaitWritable();
         }

         return res;
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      if (this.resumed) {
         return ((StreamSinkConduit)this.next).transferFrom(source, count, throughBuffer);
      } else {
         long res;
         while((res = ((StreamSinkConduit)this.next).transferFrom(source, count, throughBuffer)) == 0L) {
            ((StreamSinkConduit)this.next).awaitWritable();
         }

         return res;
      }
   }

   public int write(ByteBuffer src) throws IOException {
      return this.write(src, false);
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      return this.write(srcs, offs, len, false);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return this.write(src, true);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return this.write(srcs, offset, length, true);
   }

   private int write(ByteBuffer src, boolean writeFinal) throws IOException {
      if (this.resumed) {
         return this.doWrite(src, writeFinal);
      } else {
         int res;
         while((long)(res = this.doWrite(src, writeFinal)) == 0L) {
            ((StreamSinkConduit)this.next).awaitWritable();
         }

         return res;
      }
   }

   private long write(ByteBuffer[] srcs, int offs, int len, boolean writeFinal) throws IOException {
      if (this.resumed) {
         return this.doWrite(srcs, offs, len, writeFinal);
      } else {
         long res;
         while((res = ((StreamSinkConduit)this.next).write(srcs, offs, len)) == 0L) {
            ((StreamSinkConduit)this.next).awaitWritable();
         }

         return res;
      }
   }

   private int doWrite(ByteBuffer src, boolean writeFinal) throws IOException {
      return writeFinal ? ((StreamSinkConduit)this.next).writeFinal(src) : ((StreamSinkConduit)this.next).write(src);
   }

   private long doWrite(ByteBuffer[] srcs, int offs, int len, boolean writeFinal) throws IOException {
      return writeFinal ? ((StreamSinkConduit)this.next).writeFinal(srcs, offs, len) : ((StreamSinkConduit)this.next).write(srcs, offs, len);
   }

   public boolean flush() throws IOException {
      if (this.resumed) {
         return ((StreamSinkConduit)this.next).flush();
      } else {
         while(!((StreamSinkConduit)this.next).flush()) {
            ((StreamSinkConduit)this.next).awaitWritable();
         }

         return true;
      }
   }

   public void resumeWrites() {
      this.resumed = true;
      ((StreamSinkConduit)this.next).resumeWrites();
   }

   public void suspendWrites() {
      this.resumed = false;
      ((StreamSinkConduit)this.next).suspendWrites();
   }

   public void wakeupWrites() {
      this.resumed = true;
      ((StreamSinkConduit)this.next).wakeupWrites();
   }

   public boolean isWriteResumed() {
      return this.resumed;
   }
}
