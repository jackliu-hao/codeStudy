package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSourceChannel;

public final class SynchronizedStreamSinkConduit extends AbstractSynchronizedSinkConduit<StreamSinkConduit> implements StreamSinkConduit {
   public SynchronizedStreamSinkConduit(StreamSinkConduit next) {
      super(next);
   }

   public SynchronizedStreamSinkConduit(StreamSinkConduit next, Object lock) {
      super(next, lock);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      synchronized(this.lock) {
         return ((StreamSinkConduit)this.next).transferFrom(src, position, count);
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      synchronized(this.lock) {
         return ((StreamSinkConduit)this.next).transferFrom(source, count, throughBuffer);
      }
   }

   public int write(ByteBuffer src) throws IOException {
      synchronized(this.lock) {
         return ((StreamSinkConduit)this.next).write(src);
      }
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      synchronized(this.lock) {
         return ((StreamSinkConduit)this.next).write(srcs, offs, len);
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      synchronized(this.lock) {
         return ((StreamSinkConduit)this.next).writeFinal(src);
      }
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      synchronized(this.lock) {
         return ((StreamSinkConduit)this.next).writeFinal(srcs, offset, length);
      }
   }
}
