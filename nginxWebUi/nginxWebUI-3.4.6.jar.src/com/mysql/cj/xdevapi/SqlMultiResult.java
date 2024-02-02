/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.ResultStreamer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
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
/*     */ public class SqlMultiResult
/*     */   implements SqlResult, ResultStreamer
/*     */ {
/*     */   private Supplier<SqlResult> resultStream;
/*  45 */   private List<SqlResult> pendingResults = new ArrayList<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private SqlResult currentResult;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlMultiResult(Supplier<SqlResult> resultStream) {
/*  55 */     this.resultStream = resultStream;
/*  56 */     this.currentResult = resultStream.get();
/*     */   }
/*     */   
/*     */   private SqlResult getCurrentResult() {
/*  60 */     if (this.currentResult == null) {
/*  61 */       throw new WrongArgumentException("No active result");
/*     */     }
/*  63 */     return this.currentResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean nextResult() {
/*  68 */     if (this.currentResult == null) {
/*  69 */       return false;
/*     */     }
/*     */     try {
/*  72 */       if (ResultStreamer.class.isAssignableFrom(this.currentResult.getClass())) {
/*  73 */         ((ResultStreamer)this.currentResult).finishStreaming();
/*     */       }
/*     */     } finally {
/*     */       
/*  77 */       this.currentResult = null;
/*     */     } 
/*  79 */     this.currentResult = (this.pendingResults.size() > 0) ? this.pendingResults.remove(0) : this.resultStream.get();
/*  80 */     return (this.currentResult != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void finishStreaming() {
/*  85 */     if (this.currentResult == null) {
/*     */       return;
/*     */     }
/*  88 */     if (ResultStreamer.class.isAssignableFrom(this.currentResult.getClass())) {
/*  89 */       ((ResultStreamer)this.currentResult).finishStreaming();
/*     */     }
/*  91 */     SqlResult pendingRs = null;
/*  92 */     while ((pendingRs = this.resultStream.get()) != null) {
/*  93 */       if (ResultStreamer.class.isAssignableFrom(pendingRs.getClass())) {
/*  94 */         ((ResultStreamer)pendingRs).finishStreaming();
/*     */       }
/*  96 */       this.pendingResults.add(pendingRs);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasData() {
/* 102 */     return getCurrentResult().hasData();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getAffectedItemsCount() {
/* 107 */     return getCurrentResult().getAffectedItemsCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getAutoIncrementValue() {
/* 112 */     return getCurrentResult().getAutoIncrementValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWarningsCount() {
/* 117 */     return getCurrentResult().getWarningsCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Warning> getWarnings() {
/* 122 */     return getCurrentResult().getWarnings();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/* 127 */     return getCurrentResult().getColumnCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Column> getColumns() {
/* 132 */     return getCurrentResult().getColumns();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> getColumnNames() {
/* 137 */     return getCurrentResult().getColumnNames();
/*     */   }
/*     */ 
/*     */   
/*     */   public long count() {
/* 142 */     return getCurrentResult().count();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Row> fetchAll() {
/* 147 */     return getCurrentResult().fetchAll();
/*     */   }
/*     */ 
/*     */   
/*     */   public Row next() {
/* 152 */     return getCurrentResult().next();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 157 */     return getCurrentResult().hasNext();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\SqlMultiResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */