package com.mysql.cj.log;

public class NullLogger implements Log {
   public NullLogger(String instanceName) {
   }

   public boolean isDebugEnabled() {
      return false;
   }

   public boolean isErrorEnabled() {
      return false;
   }

   public boolean isFatalEnabled() {
      return false;
   }

   public boolean isInfoEnabled() {
      return false;
   }

   public boolean isTraceEnabled() {
      return false;
   }

   public boolean isWarnEnabled() {
      return false;
   }

   public void logDebug(Object msg) {
   }

   public void logDebug(Object msg, Throwable thrown) {
   }

   public void logError(Object msg) {
   }

   public void logError(Object msg, Throwable thrown) {
   }

   public void logFatal(Object msg) {
   }

   public void logFatal(Object msg, Throwable thrown) {
   }

   public void logInfo(Object msg) {
   }

   public void logInfo(Object msg, Throwable thrown) {
   }

   public void logTrace(Object msg) {
   }

   public void logTrace(Object msg, Throwable thrown) {
   }

   public void logWarn(Object msg) {
   }

   public void logWarn(Object msg, Throwable thrown) {
   }
}
