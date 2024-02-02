/*    */ package org.apache.commons.logging;
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
/*    */ public class LogConfigurationException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 8486587136871052495L;
/*    */   
/*    */   public LogConfigurationException() {}
/*    */   
/*    */   public LogConfigurationException(String message) {
/* 45 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LogConfigurationException(Throwable cause) {
/* 55 */     this((cause == null) ? null : cause.toString(), cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LogConfigurationException(String message, Throwable cause) {
/* 65 */     super(message + " (Caused by " + cause + ")");
/* 66 */     this.cause = cause;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 72 */   protected Throwable cause = null;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Throwable getCause() {
/* 78 */     return this.cause;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\logging\LogConfigurationException.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */