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
/*    */ public class SSLParamsException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = -6597843374954727858L;
/*    */   
/*    */   public SSLParamsException() {
/* 38 */     setSQLState("08000");
/*    */   }
/*    */   
/*    */   public SSLParamsException(String message) {
/* 42 */     super(message);
/* 43 */     setSQLState("08000");
/*    */   }
/*    */   
/*    */   public SSLParamsException(String message, Throwable cause) {
/* 47 */     super(message, cause);
/* 48 */     setSQLState("08000");
/*    */   }
/*    */   
/*    */   public SSLParamsException(Throwable cause) {
/* 52 */     super(cause);
/* 53 */     setSQLState("08000");
/*    */   }
/*    */   
/*    */   public SSLParamsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 57 */     super(message, cause, enableSuppression, writableStackTrace);
/* 58 */     setSQLState("08000");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\SSLParamsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */