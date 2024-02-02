/*    */ package com.zaxxer.hikari.pool;
/*    */ 
/*    */ import java.sql.PreparedStatement;
/*    */ import java.sql.ResultSet;
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
/*    */ public abstract class ProxyPreparedStatement
/*    */   extends ProxyStatement
/*    */   implements PreparedStatement
/*    */ {
/*    */   ProxyPreparedStatement(ProxyConnection connection, PreparedStatement statement) {
/* 32 */     super(connection, statement);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean execute() throws SQLException {
/* 43 */     this.connection.markCommitStateDirty();
/* 44 */     return ((PreparedStatement)this.delegate).execute();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ResultSet executeQuery() throws SQLException {
/* 51 */     this.connection.markCommitStateDirty();
/* 52 */     ResultSet resultSet = ((PreparedStatement)this.delegate).executeQuery();
/* 53 */     return ProxyFactory.getProxyResultSet(this.connection, this, resultSet);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int executeUpdate() throws SQLException {
/* 60 */     this.connection.markCommitStateDirty();
/* 61 */     return ((PreparedStatement)this.delegate).executeUpdate();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long executeLargeUpdate() throws SQLException {
/* 68 */     this.connection.markCommitStateDirty();
/* 69 */     return ((PreparedStatement)this.delegate).executeLargeUpdate();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\pool\ProxyPreparedStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */