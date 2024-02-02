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
/*    */ public class InfoStatus
/*    */   extends StatusBase
/*    */ {
/*    */   public InfoStatus(String msg, Object origin) {
/* 18 */     super(0, msg, origin);
/*    */   }
/*    */   
/*    */   public InfoStatus(String msg, Object origin, Throwable t) {
/* 22 */     super(0, msg, origin, t);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\status\InfoStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */