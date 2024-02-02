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
/*    */ public class InvalidConnectionAttributeException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = -4814924499233623016L;
/*    */   
/*    */   public InvalidConnectionAttributeException() {
/* 38 */     setSQLState("01S00");
/*    */   }
/*    */   
/*    */   public InvalidConnectionAttributeException(String message) {
/* 42 */     super(message);
/* 43 */     setSQLState("01S00");
/*    */   }
/*    */   
/*    */   public InvalidConnectionAttributeException(String message, Throwable cause) {
/* 47 */     super(message, cause);
/* 48 */     setSQLState("01S00");
/*    */   }
/*    */   
/*    */   public InvalidConnectionAttributeException(Throwable cause) {
/* 52 */     super(cause);
/* 53 */     setSQLState("01S00");
/*    */   }
/*    */   
/*    */   public InvalidConnectionAttributeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 57 */     super(message, cause, enableSuppression, writableStackTrace);
/* 58 */     setSQLState("01S00");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\InvalidConnectionAttributeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */