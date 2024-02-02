package org.xnio.conduits;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.xnio.XnioIoThread;

public abstract class AbstractSourceConduit<D extends SourceConduit> extends AbstractConduit<D> implements SourceConduit {
   protected AbstractSourceConduit(D next) {
      super(next);
   }

   public void terminateReads() throws IOException {
      ((SourceConduit)this.next).terminateReads();
   }

   public boolean isReadShutdown() {
      return ((SourceConduit)this.next).isReadShutdown();
   }

   public void resumeReads() {
      ((SourceConduit)this.next).resumeReads();
   }

   public void suspendReads() {
      ((SourceConduit)this.next).suspendReads();
   }

   public void wakeupReads() {
      ((SourceConduit)this.next).wakeupReads();
   }

   public boolean isReadResumed() {
      return ((SourceConduit)this.next).isReadResumed();
   }

   public void awaitReadable() throws IOException {
      ((SourceConduit)this.next).awaitReadable();
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      ((SourceConduit)this.next).awaitReadable(time, timeUnit);
   }

   public XnioIoThread getReadThread() {
      return ((SourceConduit)this.next).getReadThread();
   }

   public void setReadReadyHandler(ReadReadyHandler handler) {
      ((SourceConduit)this.next).setReadReadyHandler(handler);
   }
}
