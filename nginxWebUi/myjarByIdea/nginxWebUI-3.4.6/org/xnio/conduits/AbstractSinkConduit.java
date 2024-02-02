package org.xnio.conduits;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.xnio.XnioIoThread;

public abstract class AbstractSinkConduit<D extends SinkConduit> extends AbstractConduit<D> implements SinkConduit {
   protected AbstractSinkConduit(D next) {
      super(next);
   }

   public void terminateWrites() throws IOException {
      ((SinkConduit)this.next).terminateWrites();
   }

   public boolean isWriteShutdown() {
      return ((SinkConduit)this.next).isWriteShutdown();
   }

   public void resumeWrites() {
      ((SinkConduit)this.next).resumeWrites();
   }

   public void suspendWrites() {
      ((SinkConduit)this.next).suspendWrites();
   }

   public void wakeupWrites() {
      ((SinkConduit)this.next).wakeupWrites();
   }

   public boolean isWriteResumed() {
      return ((SinkConduit)this.next).isWriteResumed();
   }

   public void awaitWritable() throws IOException {
      ((SinkConduit)this.next).awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      ((SinkConduit)this.next).awaitWritable(time, timeUnit);
   }

   public XnioIoThread getWriteThread() {
      return ((SinkConduit)this.next).getWriteThread();
   }

   public void setWriteReadyHandler(WriteReadyHandler handler) {
      ((SinkConduit)this.next).setWriteReadyHandler(handler);
   }

   public void truncateWrites() throws IOException {
      ((SinkConduit)this.next).truncateWrites();
   }

   public boolean flush() throws IOException {
      return ((SinkConduit)this.next).flush();
   }
}
