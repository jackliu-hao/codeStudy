package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSourceChannel;

public abstract class AbstractStreamSinkConduit<D extends StreamSinkConduit> extends AbstractSinkConduit<D> implements StreamSinkConduit {
   protected AbstractStreamSinkConduit(D next) {
      super(next);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return ((StreamSinkConduit)this.next).transferFrom(src, position, count);
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return ((StreamSinkConduit)this.next).transferFrom(source, count, throughBuffer);
   }

   public int write(ByteBuffer src) throws IOException {
      return ((StreamSinkConduit)this.next).write(src);
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      return ((StreamSinkConduit)this.next).write(srcs, offs, len);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return ((StreamSinkConduit)this.next).writeFinal(src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return ((StreamSinkConduit)this.next).writeFinal(srcs, offset, length);
   }
}
