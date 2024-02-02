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
/*    */ 
/*    */ 
/*    */ public class StatementIsClosedException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = -4214028635985851906L;
/*    */   
/*    */   public StatementIsClosedException() {
/* 41 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   public StatementIsClosedException(String message) {
/* 45 */     super(message);
/* 46 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   public StatementIsClosedException(String message, Throwable cause) {
/* 50 */     super(message, cause);
/* 51 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   public StatementIsClosedException(Throwable cause) {
/* 55 */     super(cause);
/* 56 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   protected StatementIsClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 60 */     super(message, cause, enableSuppression, writableStackTrace);
/* 61 */     setSQLState("S1009");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\StatementIsClosedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */