package org.jboss.logging;

public interface BasicLogger {
  boolean isEnabled(Logger.Level paramLevel);
  
  boolean isTraceEnabled();
  
  void trace(Object paramObject);
  
  void trace(Object paramObject, Throwable paramThrowable);
  
  void trace(String paramString, Object paramObject, Throwable paramThrowable);
  
  void trace(String paramString, Object paramObject, Object[] paramArrayOfObject, Throwable paramThrowable);
  
  void tracev(String paramString, Object... paramVarArgs);
  
  void tracev(String paramString, Object paramObject);
  
  void tracev(String paramString, Object paramObject1, Object paramObject2);
  
  void tracev(String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void tracev(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void tracev(Throwable paramThrowable, String paramString, Object paramObject);
  
  void tracev(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void tracev(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void tracef(String paramString, Object... paramVarArgs);
  
  void tracef(String paramString, Object paramObject);
  
  void tracef(String paramString, Object paramObject1, Object paramObject2);
  
  void tracef(String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void tracef(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void tracef(Throwable paramThrowable, String paramString, Object paramObject);
  
  void tracef(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void tracef(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void tracef(String paramString, int paramInt);
  
  void tracef(String paramString, int paramInt1, int paramInt2);
  
  void tracef(String paramString, int paramInt, Object paramObject);
  
  void tracef(String paramString, int paramInt1, int paramInt2, int paramInt3);
  
  void tracef(String paramString, int paramInt1, int paramInt2, Object paramObject);
  
  void tracef(String paramString, int paramInt, Object paramObject1, Object paramObject2);
  
  void tracef(Throwable paramThrowable, String paramString, int paramInt);
  
  void tracef(Throwable paramThrowable, String paramString, int paramInt1, int paramInt2);
  
  void tracef(Throwable paramThrowable, String paramString, int paramInt, Object paramObject);
  
  void tracef(Throwable paramThrowable, String paramString, int paramInt1, int paramInt2, int paramInt3);
  
  void tracef(Throwable paramThrowable, String paramString, int paramInt1, int paramInt2, Object paramObject);
  
  void tracef(Throwable paramThrowable, String paramString, int paramInt, Object paramObject1, Object paramObject2);
  
  void tracef(String paramString, long paramLong);
  
  void tracef(String paramString, long paramLong1, long paramLong2);
  
  void tracef(String paramString, long paramLong, Object paramObject);
  
  void tracef(String paramString, long paramLong1, long paramLong2, long paramLong3);
  
  void tracef(String paramString, long paramLong1, long paramLong2, Object paramObject);
  
  void tracef(String paramString, long paramLong, Object paramObject1, Object paramObject2);
  
  void tracef(Throwable paramThrowable, String paramString, long paramLong);
  
  void tracef(Throwable paramThrowable, String paramString, long paramLong1, long paramLong2);
  
  void tracef(Throwable paramThrowable, String paramString, long paramLong, Object paramObject);
  
  void tracef(Throwable paramThrowable, String paramString, long paramLong1, long paramLong2, long paramLong3);
  
  void tracef(Throwable paramThrowable, String paramString, long paramLong1, long paramLong2, Object paramObject);
  
  void tracef(Throwable paramThrowable, String paramString, long paramLong, Object paramObject1, Object paramObject2);
  
  boolean isDebugEnabled();
  
  void debug(Object paramObject);
  
  void debug(Object paramObject, Throwable paramThrowable);
  
  void debug(String paramString, Object paramObject, Throwable paramThrowable);
  
  void debug(String paramString, Object paramObject, Object[] paramArrayOfObject, Throwable paramThrowable);
  
  void debugv(String paramString, Object... paramVarArgs);
  
  void debugv(String paramString, Object paramObject);
  
  void debugv(String paramString, Object paramObject1, Object paramObject2);
  
  void debugv(String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void debugv(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void debugv(Throwable paramThrowable, String paramString, Object paramObject);
  
  void debugv(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void debugv(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void debugf(String paramString, Object... paramVarArgs);
  
  void debugf(String paramString, Object paramObject);
  
  void debugf(String paramString, Object paramObject1, Object paramObject2);
  
  void debugf(String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void debugf(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void debugf(Throwable paramThrowable, String paramString, Object paramObject);
  
  void debugf(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void debugf(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void debugf(String paramString, int paramInt);
  
  void debugf(String paramString, int paramInt1, int paramInt2);
  
  void debugf(String paramString, int paramInt, Object paramObject);
  
  void debugf(String paramString, int paramInt1, int paramInt2, int paramInt3);
  
  void debugf(String paramString, int paramInt1, int paramInt2, Object paramObject);
  
  void debugf(String paramString, int paramInt, Object paramObject1, Object paramObject2);
  
  void debugf(Throwable paramThrowable, String paramString, int paramInt);
  
  void debugf(Throwable paramThrowable, String paramString, int paramInt1, int paramInt2);
  
  void debugf(Throwable paramThrowable, String paramString, int paramInt, Object paramObject);
  
  void debugf(Throwable paramThrowable, String paramString, int paramInt1, int paramInt2, int paramInt3);
  
  void debugf(Throwable paramThrowable, String paramString, int paramInt1, int paramInt2, Object paramObject);
  
  void debugf(Throwable paramThrowable, String paramString, int paramInt, Object paramObject1, Object paramObject2);
  
  void debugf(String paramString, long paramLong);
  
  void debugf(String paramString, long paramLong1, long paramLong2);
  
  void debugf(String paramString, long paramLong, Object paramObject);
  
  void debugf(String paramString, long paramLong1, long paramLong2, long paramLong3);
  
  void debugf(String paramString, long paramLong1, long paramLong2, Object paramObject);
  
  void debugf(String paramString, long paramLong, Object paramObject1, Object paramObject2);
  
  void debugf(Throwable paramThrowable, String paramString, long paramLong);
  
  void debugf(Throwable paramThrowable, String paramString, long paramLong1, long paramLong2);
  
  void debugf(Throwable paramThrowable, String paramString, long paramLong, Object paramObject);
  
  void debugf(Throwable paramThrowable, String paramString, long paramLong1, long paramLong2, long paramLong3);
  
  void debugf(Throwable paramThrowable, String paramString, long paramLong1, long paramLong2, Object paramObject);
  
  void debugf(Throwable paramThrowable, String paramString, long paramLong, Object paramObject1, Object paramObject2);
  
  boolean isInfoEnabled();
  
  void info(Object paramObject);
  
  void info(Object paramObject, Throwable paramThrowable);
  
  void info(String paramString, Object paramObject, Throwable paramThrowable);
  
  void info(String paramString, Object paramObject, Object[] paramArrayOfObject, Throwable paramThrowable);
  
  void infov(String paramString, Object... paramVarArgs);
  
  void infov(String paramString, Object paramObject);
  
  void infov(String paramString, Object paramObject1, Object paramObject2);
  
  void infov(String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void infov(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void infov(Throwable paramThrowable, String paramString, Object paramObject);
  
  void infov(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void infov(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void infof(String paramString, Object... paramVarArgs);
  
  void infof(String paramString, Object paramObject);
  
  void infof(String paramString, Object paramObject1, Object paramObject2);
  
  void infof(String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void infof(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void infof(Throwable paramThrowable, String paramString, Object paramObject);
  
  void infof(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void infof(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void warn(Object paramObject);
  
  void warn(Object paramObject, Throwable paramThrowable);
  
  void warn(String paramString, Object paramObject, Throwable paramThrowable);
  
  void warn(String paramString, Object paramObject, Object[] paramArrayOfObject, Throwable paramThrowable);
  
  void warnv(String paramString, Object... paramVarArgs);
  
  void warnv(String paramString, Object paramObject);
  
  void warnv(String paramString, Object paramObject1, Object paramObject2);
  
  void warnv(String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void warnv(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void warnv(Throwable paramThrowable, String paramString, Object paramObject);
  
  void warnv(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void warnv(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void warnf(String paramString, Object... paramVarArgs);
  
  void warnf(String paramString, Object paramObject);
  
  void warnf(String paramString, Object paramObject1, Object paramObject2);
  
  void warnf(String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void warnf(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void warnf(Throwable paramThrowable, String paramString, Object paramObject);
  
  void warnf(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void warnf(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void error(Object paramObject);
  
  void error(Object paramObject, Throwable paramThrowable);
  
  void error(String paramString, Object paramObject, Throwable paramThrowable);
  
  void error(String paramString, Object paramObject, Object[] paramArrayOfObject, Throwable paramThrowable);
  
  void errorv(String paramString, Object... paramVarArgs);
  
  void errorv(String paramString, Object paramObject);
  
  void errorv(String paramString, Object paramObject1, Object paramObject2);
  
  void errorv(String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void errorv(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void errorv(Throwable paramThrowable, String paramString, Object paramObject);
  
  void errorv(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void errorv(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void errorf(String paramString, Object... paramVarArgs);
  
  void errorf(String paramString, Object paramObject);
  
  void errorf(String paramString, Object paramObject1, Object paramObject2);
  
  void errorf(String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void errorf(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void errorf(Throwable paramThrowable, String paramString, Object paramObject);
  
  void errorf(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void errorf(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void fatal(Object paramObject);
  
  void fatal(Object paramObject, Throwable paramThrowable);
  
  void fatal(String paramString, Object paramObject, Throwable paramThrowable);
  
  void fatal(String paramString, Object paramObject, Object[] paramArrayOfObject, Throwable paramThrowable);
  
  void fatalv(String paramString, Object... paramVarArgs);
  
  void fatalv(String paramString, Object paramObject);
  
  void fatalv(String paramString, Object paramObject1, Object paramObject2);
  
  void fatalv(String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void fatalv(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void fatalv(Throwable paramThrowable, String paramString, Object paramObject);
  
  void fatalv(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void fatalv(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void fatalf(String paramString, Object... paramVarArgs);
  
  void fatalf(String paramString, Object paramObject);
  
  void fatalf(String paramString, Object paramObject1, Object paramObject2);
  
  void fatalf(String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void fatalf(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void fatalf(Throwable paramThrowable, String paramString, Object paramObject);
  
  void fatalf(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void fatalf(Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void log(Logger.Level paramLevel, Object paramObject);
  
  void log(Logger.Level paramLevel, Object paramObject, Throwable paramThrowable);
  
  void log(Logger.Level paramLevel, String paramString, Object paramObject, Throwable paramThrowable);
  
  void log(String paramString, Logger.Level paramLevel, Object paramObject, Object[] paramArrayOfObject, Throwable paramThrowable);
  
  void logv(Logger.Level paramLevel, String paramString, Object... paramVarArgs);
  
  void logv(Logger.Level paramLevel, String paramString, Object paramObject);
  
  void logv(Logger.Level paramLevel, String paramString, Object paramObject1, Object paramObject2);
  
  void logv(Logger.Level paramLevel, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void logv(Logger.Level paramLevel, Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void logv(Logger.Level paramLevel, Throwable paramThrowable, String paramString, Object paramObject);
  
  void logv(Logger.Level paramLevel, Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void logv(Logger.Level paramLevel, Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void logv(String paramString1, Logger.Level paramLevel, Throwable paramThrowable, String paramString2, Object... paramVarArgs);
  
  void logv(String paramString1, Logger.Level paramLevel, Throwable paramThrowable, String paramString2, Object paramObject);
  
  void logv(String paramString1, Logger.Level paramLevel, Throwable paramThrowable, String paramString2, Object paramObject1, Object paramObject2);
  
  void logv(String paramString1, Logger.Level paramLevel, Throwable paramThrowable, String paramString2, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void logf(Logger.Level paramLevel, String paramString, Object... paramVarArgs);
  
  void logf(Logger.Level paramLevel, String paramString, Object paramObject);
  
  void logf(Logger.Level paramLevel, String paramString, Object paramObject1, Object paramObject2);
  
  void logf(Logger.Level paramLevel, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void logf(Logger.Level paramLevel, Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void logf(Logger.Level paramLevel, Throwable paramThrowable, String paramString, Object paramObject);
  
  void logf(Logger.Level paramLevel, Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2);
  
  void logf(Logger.Level paramLevel, Throwable paramThrowable, String paramString, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void logf(String paramString1, Logger.Level paramLevel, Throwable paramThrowable, String paramString2, Object paramObject);
  
  void logf(String paramString1, Logger.Level paramLevel, Throwable paramThrowable, String paramString2, Object paramObject1, Object paramObject2);
  
  void logf(String paramString1, Logger.Level paramLevel, Throwable paramThrowable, String paramString2, Object paramObject1, Object paramObject2, Object paramObject3);
  
  void logf(String paramString1, Logger.Level paramLevel, Throwable paramThrowable, String paramString2, Object... paramVarArgs);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\BasicLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */