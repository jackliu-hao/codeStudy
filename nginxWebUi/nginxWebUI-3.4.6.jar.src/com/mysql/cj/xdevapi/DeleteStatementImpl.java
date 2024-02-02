/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.MysqlxSession;
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.protocol.x.XMessage;
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
/*    */ public class DeleteStatementImpl
/*    */   extends FilterableStatement<DeleteStatement, Result>
/*    */   implements DeleteStatement
/*    */ {
/*    */   DeleteStatementImpl(MysqlxSession mysqlxSession, String schema, String table) {
/* 42 */     super(new TableFilterParams(schema, table, false));
/* 43 */     this.mysqlxSession = mysqlxSession;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Result executeStatement() {
/* 48 */     return (Result)this.mysqlxSession.query((Message)getMessageBuilder().buildDelete(this.filterParams), new UpdateResultBuilder<>());
/*    */   }
/*    */ 
/*    */   
/*    */   protected XMessage getPrepareStatementXMessage() {
/* 53 */     return getMessageBuilder().buildPrepareDelete(this.preparedStatementId, this.filterParams);
/*    */   }
/*    */ 
/*    */   
/*    */   protected Result executePreparedStatement() {
/* 58 */     return (Result)this.mysqlxSession.query((Message)getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new UpdateResultBuilder<>());
/*    */   }
/*    */   
/*    */   public CompletableFuture<Result> executeAsync() {
/* 62 */     return this.mysqlxSession.queryAsync((Message)getMessageBuilder().buildDelete(this.filterParams), new UpdateResultBuilder<>());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\DeleteStatementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */