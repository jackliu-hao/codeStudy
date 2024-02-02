/*     */ package com.mysql.cj.protocol.a.result;
/*     */ 
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.Resultset;
/*     */ import com.mysql.cj.protocol.ResultsetRows;
/*     */ import com.mysql.cj.result.DefaultColumnDefinition;
/*     */ import com.mysql.cj.result.Row;
/*     */ import java.util.HashMap;
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
/*     */ public class NativeResultset
/*     */   implements Resultset
/*     */ {
/*     */   protected ColumnDefinition columnDefinition;
/*     */   protected ResultsetRows rowData;
/*  49 */   protected Resultset nextResultset = null;
/*     */ 
/*     */   
/*     */   protected int resultId;
/*     */ 
/*     */   
/*     */   protected long updateCount;
/*     */ 
/*     */   
/*  58 */   protected long updateId = -1L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   protected String serverInfo = null;
/*     */ 
/*     */   
/*  66 */   protected Row thisRow = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NativeResultset() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NativeResultset(OkPacket ok) {
/*  78 */     this.updateCount = ok.getUpdateCount();
/*  79 */     this.updateId = ok.getUpdateID();
/*  80 */     this.serverInfo = ok.getInfo();
/*  81 */     this.columnDefinition = (ColumnDefinition)new DefaultColumnDefinition(new com.mysql.cj.result.Field[0]);
/*     */   }
/*     */   
/*     */   public NativeResultset(ResultsetRows rows) {
/*  85 */     this.columnDefinition = rows.getMetadata();
/*  86 */     this.rowData = rows;
/*  87 */     this.updateCount = this.rowData.size();
/*     */ 
/*     */     
/*  90 */     if (this.rowData.size() > 0) {
/*  91 */       if (this.updateCount == 1L && 
/*  92 */         this.thisRow == null) {
/*  93 */         this.rowData.close();
/*  94 */         this.updateCount = -1L;
/*     */       } 
/*     */     } else {
/*     */       
/*  98 */       this.thisRow = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColumnDefinition(ColumnDefinition metadata) {
/* 105 */     this.columnDefinition = metadata;
/*     */   }
/*     */ 
/*     */   
/*     */   public ColumnDefinition getColumnDefinition() {
/* 110 */     return this.columnDefinition;
/*     */   }
/*     */   
/*     */   public boolean hasRows() {
/* 114 */     return (this.rowData != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getResultId() {
/* 119 */     return this.resultId;
/*     */   }
/*     */   
/*     */   public void initRowsWithMetadata() {
/* 123 */     if (this.rowData != null) {
/* 124 */       this.rowData.setMetadata(this.columnDefinition);
/*     */     }
/* 126 */     this.columnDefinition.setColumnToIndexCache(new HashMap<>());
/*     */   }
/*     */   
/*     */   public synchronized void setNextResultset(Resultset nextResultset) {
/* 130 */     this.nextResultset = nextResultset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Resultset getNextResultset() {
/* 138 */     return this.nextResultset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void clearNextResultset() {
/* 147 */     this.nextResultset = null;
/*     */   }
/*     */   
/*     */   public long getUpdateCount() {
/* 151 */     return this.updateCount;
/*     */   }
/*     */   
/*     */   public long getUpdateID() {
/* 155 */     return this.updateId;
/*     */   }
/*     */   
/*     */   public String getServerInfo() {
/* 159 */     return this.serverInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultsetRows getRows() {
/* 164 */     return this.rowData;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\result\NativeResultset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */