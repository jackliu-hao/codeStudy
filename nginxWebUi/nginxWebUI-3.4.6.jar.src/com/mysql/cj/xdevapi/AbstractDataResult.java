/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.ProtocolEntity;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.protocol.ResultStreamer;
/*     */ import com.mysql.cj.protocol.x.StatementExecuteOk;
/*     */ import com.mysql.cj.protocol.x.XMessage;
/*     */ import com.mysql.cj.result.BufferedRowList;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.result.RowList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractDataResult<T>
/*     */   implements ResultStreamer, Iterator<T>, Result
/*     */ {
/*  57 */   protected int position = -1;
/*  58 */   protected int count = -1;
/*     */ 
/*     */   
/*     */   protected RowList rows;
/*     */ 
/*     */   
/*     */   protected Supplier<ProtocolEntity> completer;
/*     */ 
/*     */   
/*     */   protected StatementExecuteOk ok;
/*     */ 
/*     */   
/*     */   protected ProtocolEntityFactory<T, XMessage> rowToData;
/*     */ 
/*     */   
/*     */   protected List<T> all;
/*     */ 
/*     */   
/*     */   public AbstractDataResult(RowList rows, Supplier<ProtocolEntity> completer, ProtocolEntityFactory<T, XMessage> rowToData) {
/*  77 */     this.rows = rows;
/*  78 */     this.completer = completer;
/*  79 */     this.rowToData = rowToData;
/*     */   }
/*     */   
/*     */   public T next() {
/*  83 */     if (this.all != null) {
/*  84 */       throw new WrongArgumentException("Cannot iterate after fetchAll()");
/*     */     }
/*     */     
/*  87 */     Row r = (Row)this.rows.next();
/*  88 */     if (r == null) {
/*  89 */       throw new NoSuchElementException();
/*     */     }
/*  91 */     this.position++;
/*  92 */     return (T)this.rowToData.createFromProtocolEntity((ProtocolEntity)r);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<T> fetchAll() {
/* 101 */     if (this.position > -1) {
/* 102 */       throw new WrongArgumentException("Cannot fetchAll() after starting iteration");
/*     */     }
/*     */     
/* 105 */     if (this.all == null) {
/* 106 */       this.all = new ArrayList<>((int)count());
/* 107 */       this.rows.forEachRemaining(r -> this.all.add((T)this.rowToData.createFromProtocolEntity((ProtocolEntity)r)));
/* 108 */       this.all = Collections.unmodifiableList(this.all);
/*     */     } 
/* 110 */     return this.all;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long count() {
/* 119 */     finishStreaming();
/* 120 */     return this.count;
/*     */   }
/*     */   
/*     */   public boolean hasNext() {
/* 124 */     return this.rows.hasNext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StatementExecuteOk getStatementExecuteOk() {
/* 133 */     finishStreaming();
/* 134 */     return this.ok;
/*     */   }
/*     */ 
/*     */   
/*     */   public void finishStreaming() {
/* 139 */     if (this.ok == null) {
/* 140 */       BufferedRowList remainingRows = new BufferedRowList((Iterator)this.rows);
/* 141 */       this.count = 1 + this.position + remainingRows.size();
/* 142 */       this.rows = (RowList)remainingRows;
/* 143 */       this.ok = (StatementExecuteOk)this.completer.get();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long getAffectedItemsCount() {
/* 149 */     return getStatementExecuteOk().getAffectedItemsCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWarningsCount() {
/* 154 */     return getStatementExecuteOk().getWarningsCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Warning> getWarnings() {
/* 159 */     return getStatementExecuteOk().getWarnings();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\AbstractDataResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */