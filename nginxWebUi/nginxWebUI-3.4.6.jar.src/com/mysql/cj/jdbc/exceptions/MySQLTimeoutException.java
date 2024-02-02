/*    */ package com.mysql.cj.jdbc.exceptions;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import java.sql.SQLTimeoutException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MySQLTimeoutException
/*    */   extends SQLTimeoutException
/*    */ {
/*    */   static final long serialVersionUID = -789621240523239939L;
/*    */   
/*    */   public MySQLTimeoutException(String reason, String SQLState, int vendorCode) {
/* 41 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLTimeoutException(String reason, String SQLState) {
/* 45 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLTimeoutException(String reason) {
/* 49 */     super(reason);
/*    */   }
/*    */   
/*    */   public MySQLTimeoutException() {
/* 53 */     super(Messages.getString("MySQLTimeoutException.0"));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\exceptions\MySQLTimeoutException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */