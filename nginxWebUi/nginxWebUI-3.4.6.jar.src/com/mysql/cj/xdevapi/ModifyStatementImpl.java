/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.MysqlxSession;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.x.XMessage;
/*     */ import com.mysql.cj.protocol.x.XMessageBuilder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.stream.Collectors;
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
/*     */ public class ModifyStatementImpl
/*     */   extends FilterableStatement<ModifyStatement, Result>
/*     */   implements ModifyStatement
/*     */ {
/*  47 */   private List<UpdateSpec> updates = new ArrayList<>();
/*     */   
/*     */   ModifyStatementImpl(MysqlxSession mysqlxSession, String schema, String collection, String criteria) {
/*  50 */     super(new DocFilterParams(schema, collection, false));
/*  51 */     this.mysqlxSession = mysqlxSession;
/*  52 */     if (criteria == null || criteria.trim().length() == 0) {
/*  53 */       throw new XDevAPIError(Messages.getString("ModifyStatement.0", new String[] { "criteria" }));
/*     */     }
/*  55 */     this.filterParams.setCriteria(criteria);
/*  56 */     if (!this.mysqlxSession.supportsPreparedStatements()) {
/*  57 */       this.preparedState = PreparableStatement.PreparedState.UNSUPPORTED;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected Result executeStatement() {
/*  63 */     return (Result)this.mysqlxSession.query((Message)getMessageBuilder().buildDocUpdate(this.filterParams, this.updates), new UpdateResultBuilder<>());
/*     */   }
/*     */ 
/*     */   
/*     */   protected XMessage getPrepareStatementXMessage() {
/*  68 */     return getMessageBuilder().buildPrepareDocUpdate(this.preparedStatementId, this.filterParams, this.updates);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Result executePreparedStatement() {
/*  73 */     return (Result)this.mysqlxSession.query((Message)getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new UpdateResultBuilder<>());
/*     */   }
/*     */ 
/*     */   
/*     */   public CompletableFuture<Result> executeAsync() {
/*  78 */     return this.mysqlxSession.queryAsync((Message)((XMessageBuilder)this.mysqlxSession
/*  79 */         .getMessageBuilder()).buildDocUpdate(this.filterParams, this.updates), new UpdateResultBuilder<>());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ModifyStatement set(String docPath, Object value) {
/*  85 */     resetPrepareState();
/*  86 */     this.updates.add((new UpdateSpec(UpdateType.ITEM_SET, docPath)).setValue(value));
/*  87 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ModifyStatement change(String docPath, Object value) {
/*  92 */     resetPrepareState();
/*  93 */     this.updates.add((new UpdateSpec(UpdateType.ITEM_REPLACE, docPath)).setValue(value));
/*  94 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ModifyStatement unset(String... fields) {
/*  99 */     resetPrepareState();
/* 100 */     this.updates.addAll((Collection<? extends UpdateSpec>)Arrays.<String>stream(fields).map(docPath -> new UpdateSpec(UpdateType.ITEM_REMOVE, docPath)).collect(Collectors.toList()));
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ModifyStatement patch(DbDoc document) {
/* 106 */     resetPrepareState();
/* 107 */     return patch(document.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public ModifyStatement patch(String document) {
/* 112 */     resetPrepareState();
/* 113 */     this.updates.add((new UpdateSpec(UpdateType.MERGE_PATCH, "")).setValue(Expression.expr(document)));
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ModifyStatement arrayInsert(String field, Object value) {
/* 119 */     resetPrepareState();
/* 120 */     this.updates.add((new UpdateSpec(UpdateType.ARRAY_INSERT, field)).setValue(value));
/* 121 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ModifyStatement arrayAppend(String docPath, Object value) {
/* 126 */     resetPrepareState();
/* 127 */     this.updates.add((new UpdateSpec(UpdateType.ARRAY_APPEND, docPath)).setValue(value));
/* 128 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ModifyStatement where(String searchCondition) {
/* 137 */     return super.where(searchCondition);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\ModifyStatementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */