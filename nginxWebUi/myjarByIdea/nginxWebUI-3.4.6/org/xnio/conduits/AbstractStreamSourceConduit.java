package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSinkChannel;

public abstract class AbstractStreamSourceConduit<D extends StreamSourceConduit> extends AbstractSourceConduit<D> implements StreamSourceConduit {
   protected AbstractStreamSourceConduit(D next) {
      super(next);
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return ((StreamSourceConduit)this.next).transferTo(position, count, target);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return ((StreamSourceConduit)this.next).transferTo(count, throughBuffer, target);
   }

   public int read(ByteBuffer dst) throws IOException {
      return ((StreamSourceConduit)this.next).read(dst);
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      return ((StreamSourceConduit)this.next).read(dsts, offs, len);
   }
}
