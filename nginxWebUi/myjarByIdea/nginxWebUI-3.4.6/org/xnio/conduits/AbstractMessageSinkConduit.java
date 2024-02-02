package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class AbstractMessageSinkConduit<D extends MessageSinkConduit> extends AbstractSinkConduit<D> implements MessageSinkConduit {
   protected AbstractMessageSinkConduit(D next) {
      super(next);
   }

   public boolean send(ByteBuffer src) throws IOException {
      return ((MessageSinkConduit)this.next).send(src);
   }

   public boolean send(ByteBuffer[] srcs, int offs, int len) throws IOException {
      return ((MessageSinkConduit)this.next).send(srcs, offs, len);
   }

   public boolean sendFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
      return ((MessageSinkConduit)this.next).sendFinal(srcs, offs, len);
   }

   public boolean sendFinal(ByteBuffer src) throws IOException {
      return ((MessageSinkConduit)this.next).sendFinal(src);
   }
}
