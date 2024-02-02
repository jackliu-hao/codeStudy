/*    */ package org.jboss.logging;
/*    */ 
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.LogRecord;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class JBossLogRecord
/*    */   extends LogRecord
/*    */ {
/*    */   private static final long serialVersionUID = 2492784413065296060L;
/* 27 */   private static final String LOGGER_CLASS_NAME = Logger.class.getName();
/*    */   
/*    */   private boolean resolved;
/*    */   private final String loggerClassName;
/*    */   
/*    */   JBossLogRecord(Level level, String msg) {
/* 33 */     super(level, msg);
/* 34 */     this.loggerClassName = LOGGER_CLASS_NAME;
/*    */   }
/*    */   
/*    */   JBossLogRecord(Level level, String msg, String loggerClassName) {
/* 38 */     super(level, msg);
/* 39 */     this.loggerClassName = loggerClassName;
/*    */   }
/*    */   
/*    */   public String getSourceClassName() {
/* 43 */     if (!this.resolved) {
/* 44 */       resolve();
/*    */     }
/* 46 */     return super.getSourceClassName();
/*    */   }
/*    */   
/*    */   public void setSourceClassName(String sourceClassName) {
/* 50 */     this.resolved = true;
/* 51 */     super.setSourceClassName(sourceClassName);
/*    */   }
/*    */   
/*    */   public String getSourceMethodName() {
/* 55 */     if (!this.resolved) {
/* 56 */       resolve();
/*    */     }
/* 58 */     return super.getSourceMethodName();
/*    */   }
/*    */   
/*    */   public void setSourceMethodName(String sourceMethodName) {
/* 62 */     this.resolved = true;
/* 63 */     super.setSourceMethodName(sourceMethodName);
/*    */   }
/*    */   
/*    */   private void resolve() {
/* 67 */     this.resolved = true;
/* 68 */     StackTraceElement[] stack = (new Throwable()).getStackTrace();
/* 69 */     boolean found = false;
/* 70 */     for (StackTraceElement element : stack) {
/* 71 */       String className = element.getClassName();
/* 72 */       if (found) {
/* 73 */         if (!this.loggerClassName.equals(className)) {
/* 74 */           setSourceClassName(className);
/* 75 */           setSourceMethodName(element.getMethodName());
/*    */           return;
/*    */         } 
/*    */       } else {
/* 79 */         found = this.loggerClassName.equals(className);
/*    */       } 
/*    */     } 
/* 82 */     setSourceClassName("<unknown>");
/* 83 */     setSourceMethodName("<unknown>");
/*    */   }
/*    */   
/*    */   protected Object writeReplace() {
/* 87 */     LogRecord replacement = new LogRecord(getLevel(), getMessage());
/* 88 */     replacement.setResourceBundle(getResourceBundle());
/* 89 */     replacement.setLoggerName(getLoggerName());
/* 90 */     replacement.setMillis(getMillis());
/* 91 */     replacement.setParameters(getParameters());
/* 92 */     replacement.setResourceBundleName(getResourceBundleName());
/* 93 */     replacement.setSequenceNumber(getSequenceNumber());
/* 94 */     replacement.setSourceClassName(getSourceClassName());
/* 95 */     replacement.setSourceMethodName(getSourceMethodName());
/* 96 */     replacement.setThreadID(getThreadID());
/* 97 */     replacement.setThrown(getThrown());
/* 98 */     return replacement;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\JBossLogRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */