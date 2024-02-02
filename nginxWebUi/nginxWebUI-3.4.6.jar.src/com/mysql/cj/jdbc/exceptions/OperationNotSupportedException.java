/*    */ package com.mysql.cj.jdbc.exceptions;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class OperationNotSupportedException
/*    */   extends SQLException
/*    */ {
/*    */   static final long serialVersionUID = 474918612056813430L;
/*    */   
/*    */   public OperationNotSupportedException() {
/* 42 */     super(Messages.getString("RowDataDynamic.3"), "S1009");
/*    */   }
/*    */   
/*    */   public OperationNotSupportedException(String message) {
/* 46 */     super(message, "S1009");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\exceptions\OperationNotSupportedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */