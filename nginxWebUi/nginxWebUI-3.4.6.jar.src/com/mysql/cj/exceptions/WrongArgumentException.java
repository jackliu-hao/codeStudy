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
/*    */ public class WrongArgumentException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = 3991597077197801820L;
/*    */   
/*    */   public WrongArgumentException() {
/* 38 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   public WrongArgumentException(String message) {
/* 42 */     super(message);
/* 43 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   public WrongArgumentException(String message, Throwable cause) {
/* 47 */     super(message, cause);
/* 48 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   public WrongArgumentException(Throwable cause) {
/* 52 */     super(cause);
/* 53 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   public WrongArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 57 */     super(message, cause, enableSuppression, writableStackTrace);
/* 58 */     setSQLState("S1009");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\WrongArgumentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */