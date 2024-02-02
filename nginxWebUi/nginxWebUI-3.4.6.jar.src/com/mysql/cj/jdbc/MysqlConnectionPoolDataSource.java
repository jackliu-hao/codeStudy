/*    */ package com.mysql.cj.jdbc;
/*    */ 
/*    */ import com.mysql.cj.exceptions.CJException;
/*    */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
/*    */ import javax.sql.ConnectionPoolDataSource;
/*    */ import javax.sql.PooledConnection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MysqlConnectionPoolDataSource
/*    */   extends MysqlDataSource
/*    */   implements ConnectionPoolDataSource
/*    */ {
/*    */   static final long serialVersionUID = -7767325445592304961L;
/*    */   
/*    */   public synchronized PooledConnection getPooledConnection() throws SQLException {
/*    */     
/* 48 */     try { Connection connection = getConnection();
/* 49 */       MysqlPooledConnection mysqlPooledConnection = MysqlPooledConnection.getInstance((JdbcConnection)connection);
/*    */       
/* 51 */       return mysqlPooledConnection; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*    */   
/*    */   }
/*    */   public synchronized PooledConnection getPooledConnection(String u, String p) throws SQLException {
/*    */     
/* 56 */     try { Connection connection = getConnection(u, p);
/* 57 */       MysqlPooledConnection mysqlPooledConnection = MysqlPooledConnection.getInstance((JdbcConnection)connection);
/*    */       
/* 59 */       return mysqlPooledConnection; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*    */   
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\MysqlConnectionPoolDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */