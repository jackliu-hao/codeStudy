package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSinkChannel;

public final class SynchronizedStreamSourceConduit extends AbstractSynchronizedSourceConduit<StreamSourceConduit> implements StreamSourceConduit {
   public SynchronizedStreamSourceConduit(StreamSourceConduit next) {
      super(next);
   }

   public SynchronizedStreamSourceConduit(StreamSourceConduit next, Object lock) {
      super(next, lock);
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      synchronized(this.lock) {
         return ((StreamSourceConduit)this.next).transferTo(position, count, target);
      }
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      synchronized(this.lock) {
         return ((StreamSourceConduit)this.next).transferTo(count, throughBuffer, target);
      }
   }

   public int read(ByteBuffer dst) throws IOException {
      synchronized(this.lock) {
         return ((StreamSourceConduit)this.next).read(dst);
      }
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      synchronized(this.lock) {
         return ((StreamSourceConduit)this.next).read(dsts, offs, len);
      }
   }
}
