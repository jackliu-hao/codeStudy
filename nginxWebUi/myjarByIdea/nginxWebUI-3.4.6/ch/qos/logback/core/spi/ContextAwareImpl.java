package ch.qos.logback.core.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;

public class ContextAwareImpl implements ContextAware {
   private int noContextWarning = 0;
   protected Context context;
   final Object origin;

   public ContextAwareImpl(Context context, Object origin) {
      this.context = context;
      this.origin = origin;
   }

   protected Object getOrigin() {
      return this.origin;
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
      this.addStatus(new InfoStatus(msg, this.getOrigin()));
   }

   public void addInfo(String msg, Throwable ex) {
      this.addStatus(new InfoStatus(msg, this.getOrigin(), ex));
   }

   public void addWarn(String msg) {
      this.addStatus(new WarnStatus(msg, this.getOrigin()));
   }

   public void addWarn(String msg, Throwable ex) {
      this.addStatus(new WarnStatus(msg, this.getOrigin(), ex));
   }

   public void addError(String msg) {
      this.addStatus(new ErrorStatus(msg, this.getOrigin()));
   }

   public void addError(String msg, Throwable ex) {
      this.addStatus(new ErrorStatus(msg, this.getOrigin(), ex));
   }
}
