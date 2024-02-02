package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import org.xnio.Buffers;
import org.xnio.channels.StreamSourceChannel;

public final class MessageStreamSinkConduit extends AbstractSinkConduit<MessageSinkConduit> implements StreamSinkConduit {
   public MessageStreamSinkConduit(MessageSinkConduit next) {
      super(next);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return src.transferTo(position, count, new ConduitWritableByteChannel(this));
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return Conduits.transfer((ReadableByteChannel)source, count, throughBuffer, (StreamSinkConduit)this);
   }

   public int write(ByteBuffer src) throws IOException {
      int remaining = src.remaining();
      return ((MessageSinkConduit)this.next).send(src) ? remaining : 0;
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      long remaining = Buffers.remaining(srcs, offs, len);
      return ((MessageSinkConduit)this.next).send(srcs, offs, len) ? remaining : 0L;
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Conduits.writeFinalBasic(this, src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }
}
