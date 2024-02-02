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
/*    */ public class CJTimeoutException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = -7440108828056331100L;
/*    */   
/*    */   public CJTimeoutException() {
/* 39 */     super(Messages.getString("MySQLTimeoutException.0"));
/*    */   }
/*    */   
/*    */   public CJTimeoutException(String message) {
/* 43 */     super(message);
/*    */   }
/*    */   
/*    */   public CJTimeoutException(Throwable cause) {
/* 47 */     super(cause);
/*    */   }
/*    */   
/*    */   public CJTimeoutException(String message, Throwable cause) {
/* 51 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\CJTimeoutException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */