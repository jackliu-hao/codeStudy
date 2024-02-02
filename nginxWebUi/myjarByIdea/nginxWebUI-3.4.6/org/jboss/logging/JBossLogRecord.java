package org.jboss.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

class JBossLogRecord extends LogRecord {
   private static final long serialVersionUID = 2492784413065296060L;
   private static final String LOGGER_CLASS_NAME = Logger.class.getName();
   private boolean resolved;
   private final String loggerClassName;

   JBossLogRecord(Level level, String msg) {
      super(level, msg);
      this.loggerClassName = LOGGER_CLASS_NAME;
   }

   JBossLogRecord(Level level, String msg, String loggerClassName) {
      super(level, msg);
      this.loggerClassName = loggerClassName;
   }

   public String getSourceClassName() {
      if (!this.resolved) {
         this.resolve();
      }

      return super.getSourceClassName();
   }

   public void setSourceClassName(String sourceClassName) {
      this.resolved = true;
      super.setSourceClassName(sourceClassName);
   }

   public String getSourceMethodName() {
      if (!this.resolved) {
         this.resolve();
      }

      return super.getSourceMethodName();
   }

   public void setSourceMethodName(String sourceMethodName) {
      this.resolved = true;
      super.setSourceMethodName(sourceMethodName);
   }

   private void resolve() {
      this.resolved = true;
      StackTraceElement[] stack = (new Throwable()).getStackTrace();
      boolean found = false;
      StackTraceElement[] var3 = stack;
      int var4 = stack.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         StackTraceElement element = var3[var5];
         String className = element.getClassName();
         if (found) {
            if (!this.loggerClassName.equals(className)) {
               this.setSourceClassName(className);
               this.setSourceMethodName(element.getMethodName());
               return;
            }
         } else {
            found = this.loggerClassName.equals(className);
         }
      }

      this.setSourceClassName("<unknown>");
      this.setSourceMethodName("<unknown>");
   }

   protected Object writeReplace() {
      LogRecord replacement = new LogRecord(this.getLevel(), this.getMessage());
      replacement.setResourceBundle(this.getResourceBundle());
      replacement.setLoggerName(this.getLoggerName());
      replacement.setMillis(this.getMillis());
      replacement.setParameters(this.getParameters());
      replacement.setResourceBundleName(this.getResourceBundleName());
      replacement.setSequenceNumber(this.getSequenceNumber());
      replacement.setSourceClassName(this.getSourceClassName());
      replacement.setSourceMethodName(this.getSourceMethodName());
      replacement.setThreadID(this.getThreadID());
      replacement.setThrown(this.getThrown());
      return replacement;
   }
}
