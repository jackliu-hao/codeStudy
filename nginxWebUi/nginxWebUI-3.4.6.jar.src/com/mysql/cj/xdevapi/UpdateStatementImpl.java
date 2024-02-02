/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.MysqlxSession;
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.protocol.x.XMessage;
/*    */ import com.mysql.cj.protocol.x.XMessageBuilder;
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
/*    */ public class UpdateStatementImpl
/*    */   extends FilterableStatement<UpdateStatement, Result>
/*    */   implements UpdateStatement
/*    */ {
/* 43 */   private UpdateParams updateParams = new UpdateParams();
/*    */   
/*    */   UpdateStatementImpl(MysqlxSession mysqlxSession, String schema, String table) {
/* 46 */     super(new TableFilterParams(schema, table, false));
/* 47 */     this.mysqlxSession = mysqlxSession;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Result executeStatement() {
/* 52 */     return (Result)this.mysqlxSession.query((Message)getMessageBuilder().buildRowUpdate(this.filterParams, this.updateParams), new UpdateResultBuilder<>());
/*    */   }
/*    */ 
/*    */   
/*    */   protected XMessage getPrepareStatementXMessage() {
/* 57 */     return getMessageBuilder().buildPrepareRowUpdate(this.preparedStatementId, this.filterParams, this.updateParams);
/*    */   }
/*    */ 
/*    */   
/*    */   protected Result executePreparedStatement() {
/* 62 */     return (Result)this.mysqlxSession.query((Message)getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new UpdateResultBuilder<>());
/*    */   }
/*    */   
/*    */   public CompletableFuture<Result> executeAsync() {
/* 66 */     return this.mysqlxSession.queryAsync((Message)((XMessageBuilder)this.mysqlxSession
/* 67 */         .getMessageBuilder()).buildRowUpdate(this.filterParams, this.updateParams), new UpdateResultBuilder<>());
/*    */   }
/*    */ 
/*    */   
/*    */   public UpdateStatement set(Map<String, Object> fieldsAndValues) {
/* 72 */     resetPrepareState();
/* 73 */     this.updateParams.setUpdates(fieldsAndValues);
/* 74 */     return this;
/*    */   }
/*    */   
/*    */   public UpdateStatement set(String field, Object value) {
/* 78 */     resetPrepareState();
/* 79 */     this.updateParams.addUpdate(field, value);
/* 80 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\UpdateStatementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */