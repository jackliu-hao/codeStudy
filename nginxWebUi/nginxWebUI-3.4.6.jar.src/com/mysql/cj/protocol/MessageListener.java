/*    */ package com.mysql.cj.protocol;
/*    */ 
/*    */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
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
/*    */ public interface MessageListener<M extends Message>
/*    */ {
/*    */   default boolean processMessage(M message) {
/* 54 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not allowed");
/*    */   }
/*    */   
/*    */   void error(Throwable paramThrowable);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\MessageListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */