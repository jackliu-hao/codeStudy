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
/*    */ public class ConnectionIsClosedException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = -8001652264426656450L;
/*    */   
/*    */   public ConnectionIsClosedException() {
/* 41 */     setSQLState("08003");
/*    */   }
/*    */   
/*    */   public ConnectionIsClosedException(String message) {
/* 45 */     super(message);
/* 46 */     setSQLState("08003");
/*    */   }
/*    */   
/*    */   public ConnectionIsClosedException(String message, Throwable cause) {
/* 50 */     super(message, cause);
/* 51 */     setSQLState("08003");
/*    */   }
/*    */   
/*    */   public ConnectionIsClosedException(Throwable cause) {
/* 55 */     super(cause);
/* 56 */     setSQLState("08003");
/*    */   }
/*    */   
/*    */   protected ConnectionIsClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 60 */     super(message, cause, enableSuppression, writableStackTrace);
/* 61 */     setSQLState("08003");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\ConnectionIsClosedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */