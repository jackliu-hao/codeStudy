/*     */ package org.h2.index;
/*     */ 
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.value.Value;
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
/*     */ class VirtualTableCursor
/*     */   implements Cursor
/*     */ {
/*     */   private final VirtualTableIndex index;
/*     */   private final SearchRow first;
/*     */   private final SearchRow last;
/*     */   private final ResultInterface result;
/*     */   Value[] values;
/*     */   Row row;
/*     */   
/*     */   VirtualTableCursor(VirtualTableIndex paramVirtualTableIndex, SearchRow paramSearchRow1, SearchRow paramSearchRow2, ResultInterface paramResultInterface) {
/*  45 */     this.index = paramVirtualTableIndex;
/*  46 */     this.first = paramSearchRow1;
/*  47 */     this.last = paramSearchRow2;
/*  48 */     this.result = paramResultInterface;
/*     */   }
/*     */ 
/*     */   
/*     */   public Row get() {
/*  53 */     if (this.values == null) {
/*  54 */       return null;
/*     */     }
/*  56 */     if (this.row == null) {
/*  57 */       this.row = Row.get(this.values, 1);
/*     */     }
/*  59 */     return this.row;
/*     */   }
/*     */ 
/*     */   
/*     */   public SearchRow getSearchRow() {
/*  64 */     return (SearchRow)get();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean next() {
/*  69 */     SearchRow searchRow1 = this.first, searchRow2 = this.last;
/*  70 */     if (searchRow1 == null && searchRow2 == null) {
/*  71 */       return nextImpl();
/*     */     }
/*  73 */     while (nextImpl()) {
/*  74 */       Row row = get();
/*  75 */       if (searchRow1 != null) {
/*  76 */         int i = this.index.compareRows((SearchRow)row, searchRow1);
/*  77 */         if (i < 0) {
/*     */           continue;
/*     */         }
/*     */       } 
/*  81 */       if (searchRow2 != null) {
/*  82 */         int i = this.index.compareRows((SearchRow)row, searchRow2);
/*  83 */         if (i > 0) {
/*     */           continue;
/*     */         }
/*     */       } 
/*  87 */       return true;
/*     */     } 
/*  89 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean nextImpl() {
/*  98 */     this.row = null;
/*  99 */     if (this.result != null && this.result.next()) {
/* 100 */       this.values = this.result.currentRow();
/*     */     } else {
/* 102 */       this.values = null;
/*     */     } 
/* 104 */     return (this.values != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean previous() {
/* 109 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\VirtualTableCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */