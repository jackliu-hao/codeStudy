package org.apache.http.client.methods;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicMarkableReference;
import org.apache.http.HttpRequest;
import org.apache.http.client.utils.CloneUtils;
import org.apache.http.concurrent.Cancellable;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.HeaderGroup;
import org.apache.http.params.HttpParams;

public abstract class AbstractExecutionAwareRequest extends AbstractHttpMessage implements HttpExecutionAware, AbortableHttpRequest, Cloneable, HttpRequest {
   private final AtomicMarkableReference<Cancellable> cancellableRef = new AtomicMarkableReference((Object)null, false);

   protected AbstractExecutionAwareRequest() {
   }

   /** @deprecated */
   @Deprecated
   public void setConnectionRequest(final ClientConnectionRequest connRequest) {
      this.setCancellable(new Cancellable() {
         public boolean cancel() {
            connRequest.abortRequest();
            return true;
         }
      });
   }

   /** @deprecated */
   @Deprecated
   public void setReleaseTrigger(final ConnectionReleaseTrigger releaseTrigger) {
      this.setCancellable(new Cancellable() {
         public boolean cancel() {
            try {
               releaseTrigger.abortConnection();
               return true;
            } catch (IOException var2) {
               return false;
            }
         }
      });
   }

   public void abort() {
      while(!this.cancellableRef.isMarked()) {
         Cancellable actualCancellable = (Cancellable)this.cancellableRef.getReference();
         if (this.cancellableRef.compareAndSet(actualCancellable, actualCancellable, false, true) && actualCancellable != null) {
            actualCancellable.cancel();
         }
      }

   }

   public boolean isAborted() {
      return this.cancellableRef.isMarked();
   }

   public void setCancellable(Cancellable cancellable) {
      Cancellable actualCancellable = (Cancellable)this.cancellableRef.getReference();
      if (!this.cancellableRef.compareAndSet(actualCancellable, cancellable, false, false)) {
         cancellable.cancel();
      }

   }

   public Object clone() throws CloneNotSupportedException {
      AbstractExecutionAwareRequest clone = (AbstractExecutionAwareRequest)super.clone();
      clone.headergroup = (HeaderGroup)CloneUtils.cloneObject(this.headergroup);
      clone.params = (HttpParams)CloneUtils.cloneObject(this.params);
      return clone;
   }

   /** @deprecated */
   @Deprecated
   public void completed() {
      this.cancellableRef.set((Object)null, false);
   }

   public void reset() {
      boolean marked;
      Cancellable actualCancellable;
      do {
         marked = this.cancellableRef.isMarked();
         actualCancellable = (Cancellable)this.cancellableRef.getReference();
         if (actualCancellable != null) {
            actualCancellable.cancel();
         }
      } while(!this.cancellableRef.compareAndSet(actualCancellable, (Object)null, marked, false));

   }
}
