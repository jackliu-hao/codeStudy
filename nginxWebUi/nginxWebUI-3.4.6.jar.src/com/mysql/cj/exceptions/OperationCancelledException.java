/*    */ package com.mysql.cj.exceptions;
/*    */ 
/*    */ import com.mysql.cj.Messages;
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
/*    */ public class OperationCancelledException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = 9001418688349454695L;
/*    */   
/*    */   public OperationCancelledException() {
/* 39 */     super(Messages.getString("MySQLStatementCancelledException.0"));
/*    */   }
/*    */   
/*    */   public OperationCancelledException(String message) {
/* 43 */     super(message);
/*    */   }
/*    */   
/*    */   public OperationCancelledException(Throwable cause) {
/* 47 */     super(cause);
/*    */   }
/*    */   
/*    */   public OperationCancelledException(String message, Throwable cause) {
/* 51 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\OperationCancelledException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */