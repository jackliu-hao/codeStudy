package org.jboss.logging;

public interface BasicLogger {
   boolean isEnabled(Logger.Level var1);

   boolean isTraceEnabled();

   void trace(Object var1);

   void trace(Object var1, Throwable var2);

   void trace(String var1, Object var2, Throwable var3);

   void trace(String var1, Object var2, Object[] var3, Throwable var4);

   void tracev(String var1, Object... var2);

   void tracev(String var1, Object var2);

   void tracev(String var1, Object var2, Object var3);

   void tracev(String var1, Object var2, Object var3, Object var4);

   void tracev(Throwable var1, String var2, Object... var3);

   void tracev(Throwable var1, String var2, Object var3);

   void tracev(Throwable var1, String var2, Object var3, Object var4);

   void tracev(Throwable var1, String var2, Object var3, Object var4, Object var5);

   void tracef(String var1, Object... var2);

   void tracef(String var1, Object var2);

   void tracef(String var1, Object var2, Object var3);

   void tracef(String var1, Object var2, Object var3, Object var4);

   void tracef(Throwable var1, String var2, Object... var3);

   void tracef(Throwable var1, String var2, Object var3);

   void tracef(Throwable var1, String var2, Object var3, Object var4);

   void tracef(Throwable var1, String var2, Object var3, Object var4, Object var5);

   void tracef(String var1, int var2);

   void tracef(String var1, int var2, int var3);

   void tracef(String var1, int var2, Object var3);

   void tracef(String var1, int var2, int var3, int var4);

   void tracef(String var1, int var2, int var3, Object var4);

   void tracef(String var1, int var2, Object var3, Object var4);

   void tracef(Throwable var1, String var2, int var3);

   void tracef(Throwable var1, String var2, int var3, int var4);

   void tracef(Throwable var1, String var2, int var3, Object var4);

   void tracef(Throwable var1, String var2, int var3, int var4, int var5);

   void tracef(Throwable var1, String var2, int var3, int var4, Object var5);

   void tracef(Throwable var1, String var2, int var3, Object var4, Object var5);

   void tracef(String var1, long var2);

   void tracef(String var1, long var2, long var4);

   void tracef(String var1, long var2, Object var4);

   void tracef(String var1, long var2, long var4, long var6);

   void tracef(String var1, long var2, long var4, Object var6);

   void tracef(String var1, long var2, Object var4, Object var5);

   void tracef(Throwable var1, String var2, long var3);

   void tracef(Throwable var1, String var2, long var3, long var5);

   void tracef(Throwable var1, String var2, long var3, Object var5);

   void tracef(Throwable var1, String var2, long var3, long var5, long var7);

   void tracef(Throwable var1, String var2, long var3, long var5, Object var7);

   void tracef(Throwable var1, String var2, long var3, Object var5, Object var6);

   boolean isDebugEnabled();

   void debug(Object var1);

   void debug(Object var1, Throwable var2);

   void debug(String var1, Object var2, Throwable var3);

   void debug(String var1, Object var2, Object[] var3, Throwable var4);

   void debugv(String var1, Object... var2);

   void debugv(String var1, Object var2);

   void debugv(String var1, Object var2, Object var3);

   void debugv(String var1, Object var2, Object var3, Object var4);

   void debugv(Throwable var1, String var2, Object... var3);

   void debugv(Throwable var1, String var2, Object var3);

   void debugv(Throwable var1, String var2, Object var3, Object var4);

   void debugv(Throwable var1, String var2, Object var3, Object var4, Object var5);

   void debugf(String var1, Object... var2);

   void debugf(String var1, Object var2);

   void debugf(String var1, Object var2, Object var3);

   void debugf(String var1, Object var2, Object var3, Object var4);

   void debugf(Throwable var1, String var2, Object... var3);

   void debugf(Throwable var1, String var2, Object var3);

   void debugf(Throwable var1, String var2, Object var3, Object var4);

   void debugf(Throwable var1, String var2, Object var3, Object var4, Object var5);

   void debugf(String var1, int var2);

   void debugf(String var1, int var2, int var3);

   void debugf(String var1, int var2, Object var3);

   void debugf(String var1, int var2, int var3, int var4);

   void debugf(String var1, int var2, int var3, Object var4);

   void debugf(String var1, int var2, Object var3, Object var4);

   void debugf(Throwable var1, String var2, int var3);

   void debugf(Throwable var1, String var2, int var3, int var4);

   void debugf(Throwable var1, String var2, int var3, Object var4);

   void debugf(Throwable var1, String var2, int var3, int var4, int var5);

   void debugf(Throwable var1, String var2, int var3, int var4, Object var5);

   void debugf(Throwable var1, String var2, int var3, Object var4, Object var5);

   void debugf(String var1, long var2);

   void debugf(String var1, long var2, long var4);

   void debugf(String var1, long var2, Object var4);

   void debugf(String var1, long var2, long var4, long var6);

   void debugf(String var1, long var2, long var4, Object var6);

   void debugf(String var1, long var2, Object var4, Object var5);

   void debugf(Throwable var1, String var2, long var3);

   void debugf(Throwable var1, String var2, long var3, long var5);

   void debugf(Throwable var1, String var2, long var3, Object var5);

   void debugf(Throwable var1, String var2, long var3, long var5, long var7);

   void debugf(Throwable var1, String var2, long var3, long var5, Object var7);

   void debugf(Throwable var1, String var2, long var3, Object var5, Object var6);

   boolean isInfoEnabled();

   void info(Object var1);

   void info(Object var1, Throwable var2);

   void info(String var1, Object var2, Throwable var3);

   void info(String var1, Object var2, Object[] var3, Throwable var4);

   void infov(String var1, Object... var2);

   void infov(String var1, Object var2);

   void infov(String var1, Object var2, Object var3);

   void infov(String var1, Object var2, Object var3, Object var4);

   void infov(Throwable var1, String var2, Object... var3);

   void infov(Throwable var1, String var2, Object var3);

   void infov(Throwable var1, String var2, Object var3, Object var4);

   void infov(Throwable var1, String var2, Object var3, Object var4, Object var5);

   void infof(String var1, Object... var2);

   void infof(String var1, Object var2);

   void infof(String var1, Object var2, Object var3);

   void infof(String var1, Object var2, Object var3, Object var4);

   void infof(Throwable var1, String var2, Object... var3);

   void infof(Throwable var1, String var2, Object var3);

   void infof(Throwable var1, String var2, Object var3, Object var4);

   void infof(Throwable var1, String var2, Object var3, Object var4, Object var5);

   void warn(Object var1);

   void warn(Object var1, Throwable var2);

   void warn(String var1, Object var2, Throwable var3);

   void warn(String var1, Object var2, Object[] var3, Throwable var4);

   void warnv(String var1, Object... var2);

   void warnv(String var1, Object var2);

   void warnv(String var1, Object var2, Object var3);

   void warnv(String var1, Object var2, Object var3, Object var4);

   void warnv(Throwable var1, String var2, Object... var3);

   void warnv(Throwable var1, String var2, Object var3);

   void warnv(Throwable var1, String var2, Object var3, Object var4);

   void warnv(Throwable var1, String var2, Object var3, Object var4, Object var5);

   void warnf(String var1, Object... var2);

   void warnf(String var1, Object var2);

   void warnf(String var1, Object var2, Object var3);

   void warnf(String var1, Object var2, Object var3, Object var4);

   void warnf(Throwable var1, String var2, Object... var3);

   void warnf(Throwable var1, String var2, Object var3);

   void warnf(Throwable var1, String var2, Object var3, Object var4);

   void warnf(Throwable var1, String var2, Object var3, Object var4, Object var5);

   void error(Object var1);

   void error(Object var1, Throwable var2);

   void error(String var1, Object var2, Throwable var3);

   void error(String var1, Object var2, Object[] var3, Throwable var4);

   void errorv(String var1, Object... var2);

   void errorv(String var1, Object var2);

   void errorv(String var1, Object var2, Object var3);

   void errorv(String var1, Object var2, Object var3, Object var4);

   void errorv(Throwable var1, String var2, Object... var3);

   void errorv(Throwable var1, String var2, Object var3);

   void errorv(Throwable var1, String var2, Object var3, Object var4);

   void errorv(Throwable var1, String var2, Object var3, Object var4, Object var5);

   void errorf(String var1, Object... var2);

   void errorf(String var1, Object var2);

   void errorf(String var1, Object var2, Object var3);

   void errorf(String var1, Object var2, Object var3, Object var4);

   void errorf(Throwable var1, String var2, Object... var3);

   void errorf(Throwable var1, String var2, Object var3);

   void errorf(Throwable var1, String var2, Object var3, Object var4);

   void errorf(Throwable var1, String var2, Object var3, Object var4, Object var5);

   void fatal(Object var1);

   void fatal(Object var1, Throwable var2);

   void fatal(String var1, Object var2, Throwable var3);

   void fatal(String var1, Object var2, Object[] var3, Throwable var4);

   void fatalv(String var1, Object... var2);

   void fatalv(String var1, Object var2);

   void fatalv(String var1, Object var2, Object var3);

   void fatalv(String var1, Object var2, Object var3, Object var4);

   void fatalv(Throwable var1, String var2, Object... var3);

   void fatalv(Throwable var1, String var2, Object var3);

   void fatalv(Throwable var1, String var2, Object var3, Object var4);

   void fatalv(Throwable var1, String var2, Object var3, Object var4, Object var5);

   void fatalf(String var1, Object... var2);

   void fatalf(String var1, Object var2);

   void fatalf(String var1, Object var2, Object var3);

   void fatalf(String var1, Object var2, Object var3, Object var4);

   void fatalf(Throwable var1, String var2, Object... var3);

   void fatalf(Throwable var1, String var2, Object var3);

   void fatalf(Throwable var1, String var2, Object var3, Object var4);

   void fatalf(Throwable var1, String var2, Object var3, Object var4, Object var5);

   void log(Logger.Level var1, Object var2);

   void log(Logger.Level var1, Object var2, Throwable var3);

   void log(Logger.Level var1, String var2, Object var3, Throwable var4);

   void log(String var1, Logger.Level var2, Object var3, Object[] var4, Throwable var5);

   void logv(Logger.Level var1, String var2, Object... var3);

   void logv(Logger.Level var1, String var2, Object var3);

   void logv(Logger.Level var1, String var2, Object var3, Object var4);

   void logv(Logger.Level var1, String var2, Object var3, Object var4, Object var5);

   void logv(Logger.Level var1, Throwable var2, String var3, Object... var4);

   void logv(Logger.Level var1, Throwable var2, String var3, Object var4);

   void logv(Logger.Level var1, Throwable var2, String var3, Object var4, Object var5);

   void logv(Logger.Level var1, Throwable var2, String var3, Object var4, Object var5, Object var6);

   void logv(String var1, Logger.Level var2, Throwable var3, String var4, Object... var5);

   void logv(String var1, Logger.Level var2, Throwable var3, String var4, Object var5);

   void logv(String var1, Logger.Level var2, Throwable var3, String var4, Object var5, Object var6);

   void logv(String var1, Logger.Level var2, Throwable var3, String var4, Object var5, Object var6, Object var7);

   void logf(Logger.Level var1, String var2, Object... var3);

   void logf(Logger.Level var1, String var2, Object var3);

   void logf(Logger.Level var1, String var2, Object var3, Object var4);

   void logf(Logger.Level var1, String var2, Object var3, Object var4, Object var5);

   void logf(Logger.Level var1, Throwable var2, String var3, Object... var4);

   void logf(Logger.Level var1, Throwable var2, String var3, Object var4);

   void logf(Logger.Level var1, Throwable var2, String var3, Object var4, Object var5);

   void logf(Logger.Level var1, Throwable var2, String var3, Object var4, Object var5, Object var6);

   void logf(String var1, Logger.Level var2, Throwable var3, String var4, Object var5);

   void logf(String var1, Logger.Level var2, Throwable var3, String var4, Object var5, Object var6);

   void logf(String var1, Logger.Level var2, Throwable var3, String var4, Object var5, Object var6, Object var7);

   void logf(String var1, Logger.Level var2, Throwable var3, String var4, Object... var5);
}
