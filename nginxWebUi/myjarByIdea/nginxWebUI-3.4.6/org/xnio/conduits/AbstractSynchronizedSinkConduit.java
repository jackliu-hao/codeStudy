package org.xnio.conduits;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.xnio.XnioIoThread;

public abstract class AbstractSynchronizedSinkConduit<D extends SinkConduit> extends AbstractSynchronizedConduit<D> implements SinkConduit {
   protected AbstractSynchronizedSinkConduit(D next) {
      super(next);
   }

   protected AbstractSynchronizedSinkConduit(D next, Object lock) {
      super(next, lock);
   }

   public void terminateWrites() throws IOException {
      synchronized(this.lock) {
         ((SinkConduit)this.next).terminateWrites();
      }
   }

   public boolean isWriteShutdown() {
      synchronized(this.lock) {
         return ((SinkConduit)this.next).isWriteShutdown();
      }
   }

   public void resumeWrites() {
      synchronized(this.lock) {
         ((SinkConduit)this.next).resumeWrites();
      }
   }

   public void suspendWrites() {
      synchronized(this.lock) {
         ((SinkConduit)this.next).suspendWrites();
      }
   }

   public void wakeupWrites() {
      synchronized(this.lock) {
         ((SinkConduit)this.next).wakeupWrites();
      }
   }

   public boolean isWriteResumed() {
      synchronized(this.lock) {
         return ((SinkConduit)this.next).isWriteResumed();
      }
   }

   public void awaitWritable() throws IOException {
      synchronized(this.lock) {
         ((SinkConduit)this.next).awaitWritable();
      }
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      synchronized(this.lock) {
         ((SinkConduit)this.next).awaitWritable(time, timeUnit);
      }
   }

   public XnioIoThread getWriteThread() {
      return ((SinkConduit)this.next).getWriteThread();
   }

   public void setWriteReadyHandler(WriteReadyHandler handler) {
      synchronized(this.lock) {
         ((SinkConduit)this.next).setWriteReadyHandler(handler);
      }
   }

   public void truncateWrites() throws IOException {
      synchronized(this.lock) {
         ((SinkConduit)this.next).truncateWrites();
      }
   }

   public boolean flush() throws IOException {
      synchronized(this.lock) {
         return ((SinkConduit)this.next).flush();
      }
   }
}
