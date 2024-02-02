/*    */ package com.mysql.cj.exceptions;
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
/*    */ public class RSAException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = -1878681511263159173L;
/*    */   
/*    */   public RSAException() {}
/*    */   
/*    */   public RSAException(String message) {
/* 41 */     super(message);
/*    */   }
/*    */   
/*    */   public RSAException(String message, Throwable cause) {
/* 45 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public RSAException(Throwable cause) {
/* 49 */     super(cause);
/*    */   }
/*    */   
/*    */   protected RSAException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 53 */     super(message, cause, enableSuppression, writableStackTrace);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\RSAException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */