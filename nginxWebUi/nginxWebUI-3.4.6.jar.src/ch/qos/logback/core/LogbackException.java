/*    */ package ch.qos.logback.core;
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
/*    */ public class LogbackException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = -799956346239073266L;
/*    */   
/*    */   public LogbackException(String msg) {
/* 21 */     super(msg);
/*    */   }
/*    */   
/*    */   public LogbackException(String msg, Throwable nested) {
/* 25 */     super(msg, nested);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\LogbackException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */