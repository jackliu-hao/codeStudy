package org.xnio.conduits;

import org.xnio.XnioWorker;
import org.xnio._private.Messages;

public abstract class AbstractConduit<D extends Conduit> implements Conduit {
   protected final D next;

   protected AbstractConduit(D next) {
      if (next == null) {
         throw Messages.msg.nullParameter("next");
      } else {
         this.next = next;
      }
   }

   public XnioWorker getWorker() {
      return this.next.getWorker();
   }
}
