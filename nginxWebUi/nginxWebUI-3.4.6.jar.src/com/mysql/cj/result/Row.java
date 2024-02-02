/*    */ package com.mysql.cj.result;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import com.mysql.cj.protocol.ColumnDefinition;
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface Row
/*    */   extends ProtocolEntity
/*    */ {
/*    */   <T> T getValue(int paramInt, ValueFactory<T> paramValueFactory);
/*    */   
/*    */   default Row setMetadata(ColumnDefinition columnDefinition) {
/* 64 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
/*    */   }
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
/*    */   default byte[] getBytes(int columnIndex) {
/* 77 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default void setBytes(int columnIndex, byte[] value) {
/* 89 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
/*    */   }
/*    */   
/*    */   boolean getNull(int paramInt);
/*    */   
/*    */   boolean wasNull();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\Row.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */