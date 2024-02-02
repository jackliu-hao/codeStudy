/*    */ package org.jboss.threads;
/*    */ 
/*    */ import org.jboss.logging.Logger;
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
/*    */ class LoggingUncaughtExceptionHandler
/*    */   implements Thread.UncaughtExceptionHandler
/*    */ {
/*    */   private final Logger log;
/*    */   
/*    */   LoggingUncaughtExceptionHandler(Logger log) {
/* 28 */     this.log = log;
/*    */   }
/*    */   
/*    */   public void uncaughtException(Thread thread, Throwable throwable) {
/* 32 */     this.log.errorf(throwable, "Thread %s threw an uncaught exception", thread);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 36 */     return String.format("%s to \"%s\"", new Object[] { super.toString(), this.log.getName() });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\LoggingUncaughtExceptionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */