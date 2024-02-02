/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.MysqlxSession;
/*    */ import com.mysql.cj.exceptions.FeatureNotAvailableException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SqlStatementImpl
/*    */   implements SqlStatement
/*    */ {
/*    */   private MysqlxSession mysqlxSession;
/*    */   private String sql;
/* 46 */   private List<Object> args = new ArrayList();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SqlStatementImpl(MysqlxSession mysqlxSession, String sql) {
/* 57 */     this.mysqlxSession = mysqlxSession;
/* 58 */     this.sql = sql;
/*    */   }
/*    */   
/*    */   public SqlResult execute() {
/* 62 */     return (SqlResult)this.mysqlxSession.query(this.mysqlxSession.getMessageBuilder().buildSqlStatement(this.sql, this.args), new StreamingSqlResultBuilder(this.mysqlxSession));
/*    */   }
/*    */ 
/*    */   
/*    */   public CompletableFuture<SqlResult> executeAsync() {
/* 67 */     return this.mysqlxSession.queryAsync(this.mysqlxSession.getMessageBuilder().buildSqlStatement(this.sql, this.args), new SqlResultBuilder(this.mysqlxSession));
/*    */   }
/*    */ 
/*    */   
/*    */   public SqlStatement clearBindings() {
/* 72 */     this.args.clear();
/* 73 */     return this;
/*    */   }
/*    */   
/*    */   public SqlStatement bind(List<Object> values) {
/* 77 */     this.args.addAll(values);
/* 78 */     return this;
/*    */   }
/*    */   
/*    */   public SqlStatement bind(Map<String, Object> values) {
/* 82 */     throw new FeatureNotAvailableException("Cannot bind named parameters for SQL statements");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\SqlStatementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */