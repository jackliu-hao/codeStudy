/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.MysqlxSession;
/*     */ import com.mysql.cj.exceptions.AssertionFailedException;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.x.StatementExecuteOk;
/*     */ import com.mysql.cj.protocol.x.XMessageBuilder;
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
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
/*     */ public class AddStatementImpl
/*     */   implements AddStatement
/*     */ {
/*     */   private MysqlxSession mysqlxSession;
/*     */   private String schemaName;
/*     */   private String collectionName;
/*     */   private List<DbDoc> newDocs;
/*     */   private boolean upsert = false;
/*     */   
/*     */   AddStatementImpl(MysqlxSession mysqlxSession, String schema, String collection, DbDoc newDoc) {
/*  55 */     this.mysqlxSession = mysqlxSession;
/*  56 */     this.schemaName = schema;
/*  57 */     this.collectionName = collection;
/*  58 */     this.newDocs = new ArrayList<>();
/*  59 */     this.newDocs.add(newDoc);
/*     */   }
/*     */   
/*     */   AddStatementImpl(MysqlxSession mysqlxSession, String schema, String collection, DbDoc[] newDocs) {
/*  63 */     this.mysqlxSession = mysqlxSession;
/*  64 */     this.schemaName = schema;
/*  65 */     this.collectionName = collection;
/*  66 */     this.newDocs = new ArrayList<>();
/*  67 */     this.newDocs.addAll(Arrays.asList(newDocs));
/*     */   }
/*     */   
/*     */   public AddStatement add(String jsonString) {
/*     */     try {
/*  72 */       DbDoc doc = JsonParser.parseDoc(new StringReader(jsonString));
/*  73 */       return add(new DbDoc[] { doc });
/*  74 */     } catch (IOException ex) {
/*  75 */       throw AssertionFailedException.shouldNotHappen(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public AddStatement add(DbDoc... docs) {
/*  80 */     this.newDocs.addAll(Arrays.asList(docs));
/*  81 */     return this;
/*     */   }
/*     */   
/*     */   private List<String> serializeDocs() {
/*  85 */     return (List<String>)this.newDocs.stream().map(Object::toString).collect(Collectors.toList());
/*     */   }
/*     */   
/*     */   public AddResult execute() {
/*  89 */     if (this.newDocs.size() == 0) {
/*  90 */       StatementExecuteOk ok = new StatementExecuteOk(0L, null, Collections.emptyList(), Collections.emptyList());
/*  91 */       return new AddResultImpl(ok);
/*     */     } 
/*  93 */     return (AddResult)this.mysqlxSession.query((Message)((XMessageBuilder)this.mysqlxSession.getMessageBuilder()).buildDocInsert(this.schemaName, this.collectionName, 
/*  94 */           serializeDocs(), this.upsert), new AddResultBuilder());
/*     */   }
/*     */   
/*     */   public CompletableFuture<AddResult> executeAsync() {
/*  98 */     if (this.newDocs.size() == 0) {
/*  99 */       StatementExecuteOk ok = new StatementExecuteOk(0L, null, Collections.emptyList(), Collections.emptyList());
/* 100 */       return CompletableFuture.completedFuture(new AddResultImpl(ok));
/*     */     } 
/* 102 */     return this.mysqlxSession.queryAsync((Message)((XMessageBuilder)this.mysqlxSession.getMessageBuilder()).buildDocInsert(this.schemaName, this.collectionName, 
/* 103 */           serializeDocs(), this.upsert), new AddResultBuilder());
/*     */   }
/*     */   
/*     */   public boolean isUpsert() {
/* 107 */     return this.upsert;
/*     */   }
/*     */   
/*     */   public AddStatement setUpsert(boolean upsert) {
/* 111 */     this.upsert = upsert;
/* 112 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\AddStatementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */