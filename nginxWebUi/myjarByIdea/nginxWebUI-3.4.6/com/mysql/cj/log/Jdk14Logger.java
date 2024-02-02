package com.mysql.cj.log;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Jdk14Logger implements Log {
   private static final Level DEBUG;
   private static final Level ERROR;
   private static final Level FATAL;
   private static final Level INFO;
   private static final Level TRACE;
   private static final Level WARN;
   protected Logger jdkLogger = null;

   public Jdk14Logger(String name) {
      this.jdkLogger = Logger.getLogger(name);
   }

   public boolean isDebugEnabled() {
      return this.jdkLogger.isLoggable(Level.FINE);
   }

   public boolean isErrorEnabled() {
      return this.jdkLogger.isLoggable(Level.SEVERE);
   }

   public boolean isFatalEnabled() {
      return this.jdkLogger.isLoggable(Level.SEVERE);
   }

   public boolean isInfoEnabled() {
      return this.jdkLogger.isLoggable(Level.INFO);
   }

   public boolean isTraceEnabled() {
      return this.jdkLogger.isLoggable(Level.FINEST);
   }

   public boolean isWarnEnabled() {
      return this.jdkLogger.isLoggable(Level.WARNING);
   }

   public void logDebug(Object message) {
      this.logInternal(DEBUG, message, (Throwable)null);
   }

   public void logDebug(Object message, Throwable exception) {
      this.logInternal(DEBUG, message, exception);
   }

   public void logError(Object message) {
      this.logInternal(ERROR, message, (Throwable)null);
   }

   public void logError(Object message, Throwable exception) {
      this.logInternal(ERROR, message, exception);
   }

   public void logFatal(Object message) {
      this.logInternal(FATAL, message, (Throwable)null);
   }

   public void logFatal(Object message, Throwable exception) {
      this.logInternal(FATAL, message, exception);
   }

   public void logInfo(Object message) {
      this.logInternal(INFO, message, (Throwable)null);
   }

   public void logInfo(Object message, Throwable exception) {
      this.logInternal(INFO, message, exception);
   }

   public void logTrace(Object message) {
      this.logInternal(TRACE, message, (Throwable)null);
   }

   public void logTrace(Object message, Throwable exception) {
      this.logInternal(TRACE, message, exception);
   }

   public void logWarn(Object message) {
      this.logInternal(WARN, message, (Throwable)null);
   }

   public void logWarn(Object message, Throwable exception) {
      this.logInternal(WARN, message, exception);
   }

   private static final int findCallerStackDepth(StackTraceElement[] stackTrace) {
      int numFrames = stackTrace.length;

      for(int i = 0; i < numFrames; ++i) {
         String callerClassName = stackTrace[i].getClassName();
         if (!callerClassName.startsWith("com.mysql.cj") && !callerClassName.startsWith("com.mysql.cj.core") && !callerClassName.startsWith("com.mysql.cj.jdbc")) {
            return i;
         }
      }

      return 0;
   }

   private void logInternal(Level level, Object msg, Throwable exception) {
      if (this.jdkLogger.isLoggable(level)) {
         String messageAsString = null;
         String callerMethodName = "N/A";
         String callerClassName = "N/A";
         if (msg instanceof ProfilerEvent) {
            messageAsString = msg.toString();
         } else {
            Throwable locationException = new Throwable();
            StackTraceElement[] locations = locationException.getStackTrace();
            int frameIdx = findCallerStackDepth(locations);
            if (frameIdx != 0) {
               callerClassName = locations[frameIdx].getClassName();
               callerMethodName = locations[frameIdx].getMethodName();
            }

            messageAsString = String.valueOf(msg);
         }

         if (exception == null) {
            this.jdkLogger.logp(level, callerClassName, callerMethodName, messageAsString);
         } else {
            this.jdkLogger.logp(level, callerClassName, callerMethodName, messageAsString, exception);
         }
      }

   }

   static {
      DEBUG = Level.FINE;
      ERROR = Level.SEVERE;
      FATAL = Level.SEVERE;
      INFO = Level.INFO;
      TRACE = Level.FINEST;
      WARN = Level.WARNING;
   }
}
