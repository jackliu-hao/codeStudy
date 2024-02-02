/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.MysqlxSession;
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.protocol.x.XMessage;
/*    */ import com.mysql.cj.protocol.x.XMessageBuilder;
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
/*    */ public class RemoveStatementImpl
/*    */   extends FilterableStatement<RemoveStatement, Result>
/*    */   implements RemoveStatement
/*    */ {
/*    */   RemoveStatementImpl(MysqlxSession mysqlxSession, String schema, String collection, String criteria) {
/* 44 */     super(new DocFilterParams(schema, collection, false));
/* 45 */     this.mysqlxSession = mysqlxSession;
/* 46 */     if (criteria == null || criteria.trim().length() == 0) {
/* 47 */       throw new XDevAPIError(Messages.getString("RemoveStatement.0", new String[] { "criteria" }));
/*    */     }
/* 49 */     this.filterParams.setCriteria(criteria);
/*    */   }
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public RemoveStatement orderBy(String... sortFields) {
/* 55 */     return super.orderBy(sortFields);
/*    */   }
/*    */ 
/*    */   
/*    */   public Result executeStatement() {
/* 60 */     return (Result)this.mysqlxSession.query((Message)getMessageBuilder().buildDelete(this.filterParams), new UpdateResultBuilder<>());
/*    */   }
/*    */ 
/*    */   
/*    */   protected XMessage getPrepareStatementXMessage() {
/* 65 */     return getMessageBuilder().buildPrepareDelete(this.preparedStatementId, this.filterParams);
/*    */   }
/*    */ 
/*    */   
/*    */   protected Result executePreparedStatement() {
/* 70 */     return (Result)this.mysqlxSession.query((Message)getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new UpdateResultBuilder<>());
/*    */   }
/*    */   
/*    */   public CompletableFuture<Result> executeAsync() {
/* 74 */     return this.mysqlxSession.queryAsync((Message)((XMessageBuilder)this.mysqlxSession.getMessageBuilder()).buildDelete(this.filterParams), new UpdateResultBuilder<>());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public RemoveStatement where(String searchCondition) {
/* 84 */     return super.where(searchCondition);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\RemoveStatementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */