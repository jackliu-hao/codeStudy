/*    */ package com.mysql.cj.log;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NullLogger
/*    */   implements Log
/*    */ {
/*    */   public NullLogger(String instanceName) {}
/*    */   
/*    */   public boolean isDebugEnabled() {
/* 47 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isErrorEnabled() {
/* 51 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isFatalEnabled() {
/* 55 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isInfoEnabled() {
/* 59 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isTraceEnabled() {
/* 63 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isWarnEnabled() {
/* 67 */     return false;
/*    */   }
/*    */   
/*    */   public void logDebug(Object msg) {}
/*    */   
/*    */   public void logDebug(Object msg, Throwable thrown) {}
/*    */   
/*    */   public void logError(Object msg) {}
/*    */   
/*    */   public void logError(Object msg, Throwable thrown) {}
/*    */   
/*    */   public void logFatal(Object msg) {}
/*    */   
/*    */   public void logFatal(Object msg, Throwable thrown) {}
/*    */   
/*    */   public void logInfo(Object msg) {}
/*    */   
/*    */   public void logInfo(Object msg, Throwable thrown) {}
/*    */   
/*    */   public void logTrace(Object msg) {}
/*    */   
/*    */   public void logTrace(Object msg, Throwable thrown) {}
/*    */   
/*    */   public void logWarn(Object msg) {}
/*    */   
/*    */   public void logWarn(Object msg, Throwable thrown) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\log\NullLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */