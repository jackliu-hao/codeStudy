/*    */ package ch.qos.logback.core.status;
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
/*    */ public class WarnStatus
/*    */   extends StatusBase
/*    */ {
/*    */   public WarnStatus(String msg, Object origin) {
/* 19 */     super(1, msg, origin);
/*    */   }
/*    */   
/*    */   public WarnStatus(String msg, Object origin, Throwable t) {
/* 23 */     super(1, msg, origin, t);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\status\WarnStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */