/*    */ package com.mysql.cj.result;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface RowList
/*    */   extends Iterator<Row>
/*    */ {
/*    */   public static final int RESULT_SET_SIZE_UNKNOWN = -1;
/*    */   
/*    */   default Row previous() {
/* 55 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
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
/*    */   default Row get(int n) {
/* 68 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default int getPosition() {
/* 77 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default int size() {
/* 86 */     return -1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\RowList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */