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
/*     */ public class SelectStatementImpl
/*     */   extends FilterableStatement<SelectStatement, RowResult>
/*     */   implements SelectStatement
/*     */ {
/*     */   SelectStatementImpl(MysqlxSession mysqlxSession, String schema, String table, String... projection) {
/*  44 */     super(new TableFilterParams(schema, table));
/*  45 */     this.mysqlxSession = mysqlxSession;
/*  46 */     if (projection != null && projection.length > 0) {
/*  47 */       this.filterParams.setFields(projection);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected RowResult executeStatement() {
/*  53 */     return (RowResult)this.mysqlxSession.query((Message)getMessageBuilder().buildFind(this.filterParams), new StreamingRowResultBuilder(this.mysqlxSession));
/*     */   }
/*     */ 
/*     */   
/*     */   protected XMessage getPrepareStatementXMessage() {
/*  58 */     return getMessageBuilder().buildPrepareFind(this.preparedStatementId, this.filterParams);
/*     */   }
/*     */ 
/*     */   
/*     */   protected RowResult executePreparedStatement() {
/*  63 */     return (RowResult)this.mysqlxSession.query((Message)getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new StreamingRowResultBuilder(this.mysqlxSession));
/*     */   }
/*     */ 
/*     */   
/*     */   public CompletableFuture<RowResult> executeAsync() {
/*  68 */     return this.mysqlxSession.queryAsync((Message)getMessageBuilder().buildFind(this.filterParams), new RowResultBuilder(this.mysqlxSession));
/*     */   }
/*     */ 
/*     */   
/*     */   public SelectStatement groupBy(String... groupBy) {
/*  73 */     resetPrepareState();
/*  74 */     this.filterParams.setGrouping(groupBy);
/*  75 */     return this;
/*     */   }
/*     */   
/*     */   public SelectStatement having(String having) {
/*  79 */     resetPrepareState();
/*  80 */     this.filterParams.setGroupingCriteria(having);
/*  81 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FilterParams getFilterParams() {
/*  86 */     return this.filterParams;
/*     */   }
/*     */ 
/*     */   
/*     */   public SelectStatement lockShared() {
/*  91 */     return lockShared(Statement.LockContention.DEFAULT);
/*     */   }
/*     */ 
/*     */   
/*     */   public SelectStatement lockShared(Statement.LockContention lockContention) {
/*  96 */     resetPrepareState();
/*  97 */     this.filterParams.setLock(FilterParams.RowLock.SHARED_LOCK);
/*  98 */     switch (lockContention) {
/*     */       case NOWAIT:
/* 100 */         this.filterParams.setLockOption(FilterParams.RowLockOptions.NOWAIT);
/*     */         break;
/*     */       case SKIP_LOCKED:
/* 103 */         this.filterParams.setLockOption(FilterParams.RowLockOptions.SKIP_LOCKED);
/*     */         break;
/*     */     } 
/*     */     
/* 107 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SelectStatement lockExclusive() {
/* 112 */     return lockExclusive(Statement.LockContention.DEFAULT);
/*     */   }
/*     */ 
/*     */   
/*     */   public SelectStatement lockExclusive(Statement.LockContention lockContention) {
/* 117 */     resetPrepareState();
/* 118 */     this.filterParams.setLock(FilterParams.RowLock.EXCLUSIVE_LOCK);
/* 119 */     switch (lockContention) {
/*     */       case NOWAIT:
/* 121 */         this.filterParams.setLockOption(FilterParams.RowLockOptions.NOWAIT);
/*     */         break;
/*     */       case SKIP_LOCKED:
/* 124 */         this.filterParams.setLockOption(FilterParams.RowLockOptions.SKIP_LOCKED);
/*     */         break;
/*     */     } 
/*     */     
/* 128 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\SelectStatementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */