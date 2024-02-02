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
/*    */ public interface ProtocolEntityFactory<T, M extends Message>
/*    */ {
/*    */   default T createFromMessage(M message) {
/* 45 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not allowed");
/*    */   }
/*    */   
/*    */   default Resultset.Type getResultSetType() {
/* 49 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not allowed");
/*    */   }
/*    */   
/*    */   default Resultset.Concurrency getResultSetConcurrency() {
/* 53 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not allowed");
/*    */   }
/*    */   
/*    */   default int getFetchSize() {
/* 57 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not allowed");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default T createFromProtocolEntity(ProtocolEntity protocolEntity) {
/* 68 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not allowed");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\ProtocolEntityFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */