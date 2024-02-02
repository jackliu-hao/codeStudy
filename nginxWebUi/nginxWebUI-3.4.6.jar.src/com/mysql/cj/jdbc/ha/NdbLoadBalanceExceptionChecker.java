/*    */ package com.mysql.cj.jdbc.ha;
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
/*    */ public class NdbLoadBalanceExceptionChecker
/*    */   extends StandardLoadBalanceExceptionChecker
/*    */ {
/*    */   public boolean shouldExceptionTriggerFailover(Throwable ex) {
/* 36 */     return (super.shouldExceptionTriggerFailover(ex) || checkNdbException(ex));
/*    */   }
/*    */ 
/*    */   
/*    */   private boolean checkNdbException(Throwable ex) {
/* 41 */     return (ex.getMessage().startsWith("Lock wait timeout exceeded") || (ex
/* 42 */       .getMessage().startsWith("Got temporary error") && ex.getMessage().endsWith("from NDB")));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\NdbLoadBalanceExceptionChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */