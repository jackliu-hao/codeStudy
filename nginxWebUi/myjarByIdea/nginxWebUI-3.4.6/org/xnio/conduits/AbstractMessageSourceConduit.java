package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class AbstractMessageSourceConduit<D extends MessageSourceConduit> extends AbstractSourceConduit<D> implements MessageSourceConduit {
   protected AbstractMessageSourceConduit(D next) {
      super(next);
   }

   public int receive(ByteBuffer dst) throws IOException {
      return ((MessageSourceConduit)this.next).receive(dst);
   }

   public long receive(ByteBuffer[] dsts, int offs, int len) throws IOException {
      return ((MessageSourceConduit)this.next).receive(dsts, offs, len);
   }
}
