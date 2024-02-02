/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.MysqlxSession;
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.protocol.x.XMessageBuilder;
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
/*    */ public class InsertStatementImpl
/*    */   implements InsertStatement
/*    */ {
/*    */   private MysqlxSession mysqlxSession;
/*    */   private String schemaName;
/*    */   private String tableName;
/* 44 */   private InsertParams insertParams = new InsertParams();
/*    */   
/*    */   InsertStatementImpl(MysqlxSession mysqlxSession, String schema, String table, String[] fields) {
/* 47 */     this.mysqlxSession = mysqlxSession;
/* 48 */     this.schemaName = schema;
/* 49 */     this.tableName = table;
/* 50 */     this.insertParams.setProjection(fields);
/*    */   }
/*    */   
/*    */   InsertStatementImpl(MysqlxSession mysqlxSession, String schema, String table, Map<String, Object> fieldsAndValues) {
/* 54 */     this.mysqlxSession = mysqlxSession;
/* 55 */     this.schemaName = schema;
/* 56 */     this.tableName = table;
/* 57 */     this.insertParams.setFieldsAndValues(fieldsAndValues);
/*    */   }
/*    */   
/*    */   public InsertResult execute() {
/* 61 */     return (InsertResult)this.mysqlxSession.query((Message)((XMessageBuilder)this.mysqlxSession
/* 62 */         .getMessageBuilder()).buildRowInsert(this.schemaName, this.tableName, this.insertParams), new InsertResultBuilder());
/*    */   }
/*    */ 
/*    */   
/*    */   public CompletableFuture<InsertResult> executeAsync() {
/* 67 */     return this.mysqlxSession.queryAsync((Message)((XMessageBuilder)this.mysqlxSession
/* 68 */         .getMessageBuilder()).buildRowInsert(this.schemaName, this.tableName, this.insertParams), new InsertResultBuilder());
/*    */   }
/*    */ 
/*    */   
/*    */   public InsertStatement values(List<Object> row) {
/* 73 */     this.insertParams.addRow(row);
/* 74 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\InsertStatementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */