package com.mysql.cj.log;

public interface Log {
  public static final String LOGGER_INSTANCE_NAME = "MySQL";
  
  boolean isDebugEnabled();
  
  boolean isErrorEnabled();
  
  boolean isFatalEnabled();
  
  boolean isInfoEnabled();
  
  boolean isTraceEnabled();
  
  boolean isWarnEnabled();
  
  void logDebug(Object paramObject);
  
  void logDebug(Object paramObject, Throwable paramThrowable);
  
  void logError(Object paramObject);
  
  void logError(Object paramObject, Throwable paramThrowable);
  
  void logFatal(Object paramObject);
  
  void logFatal(Object paramObject, Throwable paramThrowable);
  
  void logInfo(Object paramObject);
  
  void logInfo(Object paramObject, Throwable paramThrowable);
  
  void logTrace(Object paramObject);
  
  void logTrace(Object paramObject, Throwable paramThrowable);
  
  void logWarn(Object paramObject);
  
  void logWarn(Object paramObject, Throwable paramThrowable);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\log\Log.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */