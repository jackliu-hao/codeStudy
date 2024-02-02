/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.MysqlxSession;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.x.XMessage;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FindStatementImpl
/*     */   extends FilterableStatement<FindStatement, DocResult>
/*     */   implements FindStatement
/*     */ {
/*     */   FindStatementImpl(MysqlxSession mysqlxSession, String schema, String collection, String criteria) {
/*  44 */     super(new DocFilterParams(schema, collection));
/*  45 */     this.mysqlxSession = mysqlxSession;
/*  46 */     if (criteria != null && criteria.length() > 0) {
/*  47 */       this.filterParams.setCriteria(criteria);
/*     */     }
/*  49 */     if (!this.mysqlxSession.supportsPreparedStatements()) {
/*  50 */       this.preparedState = PreparableStatement.PreparedState.UNSUPPORTED;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected DocResult executeStatement() {
/*  56 */     return (DocResult)this.mysqlxSession.query((Message)getMessageBuilder().buildFind(this.filterParams), new StreamingDocResultBuilder(this.mysqlxSession));
/*     */   }
/*     */ 
/*     */   
/*     */   protected XMessage getPrepareStatementXMessage() {
/*  61 */     return getMessageBuilder().buildPrepareFind(this.preparedStatementId, this.filterParams);
/*     */   }
/*     */ 
/*     */   
/*     */   protected DocResult executePreparedStatement() {
/*  66 */     return (DocResult)this.mysqlxSession.query((Message)getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new StreamingDocResultBuilder(this.mysqlxSession));
/*     */   }
/*     */ 
/*     */   
/*     */   public CompletableFuture<DocResult> executeAsync() {
/*  71 */     return this.mysqlxSession.queryAsync((Message)getMessageBuilder().buildFind(this.filterParams), new DocResultBuilder(this.mysqlxSession));
/*     */   }
/*     */ 
/*     */   
/*     */   public FindStatement fields(String... projection) {
/*  76 */     resetPrepareState();
/*  77 */     this.filterParams.setFields(projection);
/*  78 */     return this;
/*     */   }
/*     */   
/*     */   public FindStatement fields(Expression docProjection) {
/*  82 */     resetPrepareState();
/*  83 */     ((DocFilterParams)this.filterParams).setFields(docProjection);
/*  84 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FindStatement groupBy(String... groupBy) {
/*  89 */     resetPrepareState();
/*  90 */     this.filterParams.setGrouping(groupBy);
/*  91 */     return this;
/*     */   }
/*     */   
/*     */   public FindStatement having(String having) {
/*  95 */     resetPrepareState();
/*  96 */     this.filterParams.setGroupingCriteria(having);
/*  97 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FindStatement lockShared() {
/* 102 */     return lockShared(Statement.LockContention.DEFAULT);
/*     */   }
/*     */ 
/*     */   
/*     */   public FindStatement lockShared(Statement.LockContention lockContention) {
/* 107 */     resetPrepareState();
/* 108 */     this.filterParams.setLock(FilterParams.RowLock.SHARED_LOCK);
/* 109 */     switch (lockContention) {
/*     */       case NOWAIT:
/* 111 */         this.filterParams.setLockOption(FilterParams.RowLockOptions.NOWAIT);
/*     */         break;
/*     */       case SKIP_LOCKED:
/* 114 */         this.filterParams.setLockOption(FilterParams.RowLockOptions.SKIP_LOCKED);
/*     */         break;
/*     */     } 
/*     */     
/* 118 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FindStatement lockExclusive() {
/* 123 */     return lockExclusive(Statement.LockContention.DEFAULT);
/*     */   }
/*     */ 
/*     */   
/*     */   public FindStatement lockExclusive(Statement.LockContention lockContention) {
/* 128 */     resetPrepareState();
/* 129 */     this.filterParams.setLock(FilterParams.RowLock.EXCLUSIVE_LOCK);
/* 130 */     switch (lockContention) {
/*     */       case NOWAIT:
/* 132 */         this.filterParams.setLockOption(FilterParams.RowLockOptions.NOWAIT);
/*     */         break;
/*     */       case SKIP_LOCKED:
/* 135 */         this.filterParams.setLockOption(FilterParams.RowLockOptions.SKIP_LOCKED);
/*     */         break;
/*     */     } 
/*     */     
/* 139 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public FindStatement where(String searchCondition) {
/* 148 */     return super.where(searchCondition);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\FindStatementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */