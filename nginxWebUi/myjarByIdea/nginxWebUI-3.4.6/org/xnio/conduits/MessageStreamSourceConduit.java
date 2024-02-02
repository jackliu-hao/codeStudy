package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import org.xnio.channels.StreamSinkChannel;

public final class MessageStreamSourceConduit extends AbstractSourceConduit<MessageSourceConduit> implements StreamSourceConduit {
   public MessageStreamSourceConduit(MessageSourceConduit next) {
      super(next);
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return target.transferFrom(new ConduitReadableByteChannel(this), position, count);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return Conduits.transfer((StreamSourceConduit)this, count, throughBuffer, (WritableByteChannel)target);
   }

   public int read(ByteBuffer dst) throws IOException {
      return ((MessageSourceConduit)this.next).receive(dst);
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      return ((MessageSourceConduit)this.next).receive(dsts, offs, len);
   }
}
