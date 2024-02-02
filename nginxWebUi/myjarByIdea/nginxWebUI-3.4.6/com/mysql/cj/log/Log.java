package com.mysql.cj.log;

public interface Log {
   String LOGGER_INSTANCE_NAME = "MySQL";

   boolean isDebugEnabled();

   boolean isErrorEnabled();

   boolean isFatalEnabled();

   boolean isInfoEnabled();

   boolean isTraceEnabled();

   boolean isWarnEnabled();

   void logDebug(Object var1);

   void logDebug(Object var1, Throwable var2);

   void logError(Object var1);

   void logError(Object var1, Throwable var2);

   void logFatal(Object var1);

   void logFatal(Object var1, Throwable var2);

   void logInfo(Object var1);

   void logInfo(Object var1, Throwable var2);

   void logTrace(Object var1);

   void logTrace(Object var1, Throwable var2);

   void logWarn(Object var1);

   void logWarn(Object var1, Throwable var2);
}
