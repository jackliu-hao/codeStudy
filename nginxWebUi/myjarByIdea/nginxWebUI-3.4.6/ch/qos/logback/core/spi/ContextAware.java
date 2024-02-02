package ch.qos.logback.core.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;

public interface ContextAware {
   void setContext(Context var1);

   Context getContext();

   void addStatus(Status var1);

   void addInfo(String var1);

   void addInfo(String var1, Throwable var2);

   void addWarn(String var1);

   void addWarn(String var1, Throwable var2);

   void addError(String var1);

   void addError(String var1, Throwable var2);
}
