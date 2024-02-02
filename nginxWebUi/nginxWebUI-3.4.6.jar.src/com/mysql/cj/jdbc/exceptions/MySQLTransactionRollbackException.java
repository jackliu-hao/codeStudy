/*    */ package com.mysql.cj.jdbc.exceptions;
/*    */ 
/*    */ import com.mysql.cj.exceptions.DeadlockTimeoutRollbackMarker;
/*    */ import java.sql.SQLTransactionRollbackException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MySQLTransactionRollbackException
/*    */   extends SQLTransactionRollbackException
/*    */   implements DeadlockTimeoutRollbackMarker
/*    */ {
/*    */   static final long serialVersionUID = 6034999468737899730L;
/*    */   
/*    */   public MySQLTransactionRollbackException(String reason, String SQLState, int vendorCode) {
/* 41 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLTransactionRollbackException(String reason, String SQLState) {
/* 45 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLTransactionRollbackException(String reason) {
/* 49 */     super(reason);
/*    */   }
/*    */   
/*    */   public MySQLTransactionRollbackException() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\exceptions\MySQLTransactionRollbackException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */