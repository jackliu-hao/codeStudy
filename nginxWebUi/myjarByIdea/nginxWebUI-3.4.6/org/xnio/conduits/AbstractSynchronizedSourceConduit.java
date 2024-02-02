package org.xnio.conduits;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.xnio.XnioIoThread;

public abstract class AbstractSynchronizedSourceConduit<D extends SourceConduit> extends AbstractSynchronizedConduit<D> implements SourceConduit {
   protected AbstractSynchronizedSourceConduit(D next) {
      super(next);
   }

   protected AbstractSynchronizedSourceConduit(D next, Object lock) {
      super(next, lock);
   }

   public void terminateReads() throws IOException {
      synchronized(this.lock) {
         ((SourceConduit)this.next).terminateReads();
      }
   }

   public boolean isReadShutdown() {
      synchronized(this.lock) {
         return ((SourceConduit)this.next).isReadShutdown();
      }
   }

   public void resumeReads() {
      synchronized(this.lock) {
         ((SourceConduit)this.next).resumeReads();
      }
   }

   public void suspendReads() {
      synchronized(this.lock) {
         ((SourceConduit)this.next).suspendReads();
      }
   }

   public void wakeupReads() {
      synchronized(this.lock) {
         ((SourceConduit)this.next).wakeupReads();
      }
   }

   public boolean isReadResumed() {
      synchronized(this.lock) {
         return ((SourceConduit)this.next).isReadResumed();
      }
   }

   public void awaitReadable() throws IOException {
      synchronized(this.lock) {
         ((SourceConduit)this.next).awaitReadable();
      }
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      synchronized(this.lock) {
         ((SourceConduit)this.next).awaitReadable(time, timeUnit);
      }
   }

   public XnioIoThread getReadThread() {
      return ((SourceConduit)this.next).getReadThread();
   }

   public void setReadReadyHandler(ReadReadyHandler handler) {
      synchronized(this.lock) {
         ((SourceConduit)this.next).setReadReadyHandler(handler);
      }
   }
}
