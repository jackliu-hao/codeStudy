/*    */ package com.mysql.cj.jdbc.exceptions;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import java.sql.SQLNonTransientException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MySQLStatementCancelledException
/*    */   extends SQLNonTransientException
/*    */ {
/*    */   static final long serialVersionUID = -8762717748377197378L;
/*    */   
/*    */   public MySQLStatementCancelledException(String reason, String SQLState, int vendorCode) {
/* 41 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLStatementCancelledException(String reason, String SQLState) {
/* 45 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLStatementCancelledException(String reason) {
/* 49 */     super(reason);
/*    */   }
/*    */   
/*    */   public MySQLStatementCancelledException() {
/* 53 */     super(Messages.getString("MySQLStatementCancelledException.0"));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\exceptions\MySQLStatementCancelledException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */