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
/*    */ public class UnsupportedConnectionStringException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = 3991597077197801820L;
/*    */   
/*    */   public UnsupportedConnectionStringException() {
/* 38 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   public UnsupportedConnectionStringException(String message) {
/* 42 */     super(message);
/* 43 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   public UnsupportedConnectionStringException(String message, Throwable cause) {
/* 47 */     super(message, cause);
/* 48 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   public UnsupportedConnectionStringException(Throwable cause) {
/* 52 */     super(cause);
/* 53 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   public UnsupportedConnectionStringException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 57 */     super(message, cause, enableSuppression, writableStackTrace);
/* 58 */     setSQLState("S1009");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\UnsupportedConnectionStringException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */