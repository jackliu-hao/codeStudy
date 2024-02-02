package ch.qos.logback.core.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;

public class ContextAwareBase implements ContextAware {
   private int noContextWarning = 0;
   protected Context context;
   final Object declaredOrigin;

   public ContextAwareBase() {
      this.declaredOrigin = this;
   }

   public ContextAwareBase(ContextAware declaredOrigin) {
      this.declaredOrigin = declaredOrigin;
   }

   public void setContext(Context context) {
      if (this.context == null) {
         this.context = context;
      } else if (this.context != context) {
         throw new IllegalStateException("Context has been already set");
      }

   }

   public Context getContext() {
      return this.context;
   }

   public StatusManager getStatusManager() {
      return this.context == null ? null : this.context.getStatusManager();
   }

   protected Object getDeclaredOrigin() {
      return this.declaredOrigin;
   }

   public void addStatus(Status status) {
      if (this.context == null) {
         if (this.noContextWarning++ == 0) {
            System.out.println("LOGBACK: No context given for " + this);
         }

      } else {
         StatusManager sm = this.context.getStatusManager();
         if (sm != null) {
            sm.add(status);
         }

      }
   }

   public void addInfo(String msg) {
      this.addStatus(new InfoStatus(msg, this.getDeclaredOrigin()));
   }

   public void addInfo(String msg, Throwable ex) {
      this.addStatus(new InfoStatus(msg, this.getDeclaredOrigin(), ex));
   }

   public void addWarn(String msg) {
      this.addStatus(new WarnStatus(msg, this.getDeclaredOrigin()));
   }

   public void addWarn(String msg, Throwable ex) {
      this.addStatus(new WarnStatus(msg, this.getDeclaredOrigin(), ex));
   }

   public void addError(String msg) {
      this.addStatus(new ErrorStatus(msg, this.getDeclaredOrigin()));
   }

   public void addError(String msg, Throwable ex) {
      this.addStatus(new ErrorStatus(msg, this.getDeclaredOrigin(), ex));
   }
}
