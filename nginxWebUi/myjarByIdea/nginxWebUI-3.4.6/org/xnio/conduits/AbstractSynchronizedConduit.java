package org.xnio.conduits;

public abstract class AbstractSynchronizedConduit<D extends Conduit> extends AbstractConduit<D> {
   protected final Object lock;

   protected AbstractSynchronizedConduit(D next) {
      this(next, new Object());
   }

   protected AbstractSynchronizedConduit(D next, Object lock) {
      super(next);
      this.lock = lock;
   }
}
