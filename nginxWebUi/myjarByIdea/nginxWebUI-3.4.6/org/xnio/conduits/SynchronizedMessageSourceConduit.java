package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class SynchronizedMessageSourceConduit extends AbstractSynchronizedSourceConduit<MessageSourceConduit> implements MessageSourceConduit {
   public SynchronizedMessageSourceConduit(MessageSourceConduit next) {
      super(next);
   }

   public SynchronizedMessageSourceConduit(MessageSourceConduit next, Object lock) {
      super(next, lock);
   }

   public int receive(ByteBuffer dst) throws IOException {
      synchronized(this.lock) {
         return ((MessageSourceConduit)this.next).receive(dst);
      }
   }

   public long receive(ByteBuffer[] dsts, int offs, int len) throws IOException {
      synchronized(this.lock) {
         return ((MessageSourceConduit)this.next).receive(dsts, offs, len);
      }
   }
}
