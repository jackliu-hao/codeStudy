/*     */ package com.mysql.cj.protocol.a.result;
/*     */ 
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.ResultsetRows;
/*     */ import com.mysql.cj.result.Row;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResultsetRowsStatic
/*     */   extends AbstractResultsetRows
/*     */   implements ResultsetRows
/*     */ {
/*     */   private List<Row> rows;
/*     */   
/*     */   public ResultsetRowsStatic(List<? extends Row> rows, ColumnDefinition columnDefinition) {
/*  55 */     this.currentPositionInFetchedRows = -1;
/*  56 */     this.rows = (List)rows;
/*  57 */     this.metadata = columnDefinition;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addRow(Row row) {
/*  62 */     this.rows.add(row);
/*     */   }
/*     */ 
/*     */   
/*     */   public void afterLast() {
/*  67 */     if (this.rows.size() > 0) {
/*  68 */       this.currentPositionInFetchedRows = this.rows.size();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void beforeFirst() {
/*  74 */     if (this.rows.size() > 0) {
/*  75 */       this.currentPositionInFetchedRows = -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void beforeLast() {
/*  81 */     if (this.rows.size() > 0) {
/*  82 */       this.currentPositionInFetchedRows = this.rows.size() - 2;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Row get(int atIndex) {
/*  88 */     if (atIndex < 0 || atIndex >= this.rows.size()) {
/*  89 */       return null;
/*     */     }
/*     */     
/*  92 */     return ((Row)this.rows.get(atIndex)).setMetadata(this.metadata);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPosition() {
/*  97 */     return this.currentPositionInFetchedRows;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 102 */     boolean hasMore = (this.currentPositionInFetchedRows + 1 < this.rows.size());
/*     */     
/* 104 */     return hasMore;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAfterLast() {
/* 109 */     return (this.currentPositionInFetchedRows >= this.rows.size() && this.rows.size() != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isBeforeFirst() {
/* 114 */     return (this.currentPositionInFetchedRows == -1 && this.rows.size() != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDynamic() {
/* 119 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 124 */     return (this.rows.size() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFirst() {
/* 129 */     return (this.currentPositionInFetchedRows == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLast() {
/* 135 */     if (this.rows.size() == 0) {
/* 136 */       return false;
/*     */     }
/*     */     
/* 139 */     return (this.currentPositionInFetchedRows == this.rows.size() - 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void moveRowRelative(int rowsToMove) {
/* 144 */     if (this.rows.size() > 0) {
/* 145 */       this.currentPositionInFetchedRows += rowsToMove;
/* 146 */       if (this.currentPositionInFetchedRows < -1) {
/* 147 */         beforeFirst();
/* 148 */       } else if (this.currentPositionInFetchedRows > this.rows.size()) {
/* 149 */         afterLast();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Row next() {
/* 156 */     this.currentPositionInFetchedRows++;
/*     */     
/* 158 */     if (this.currentPositionInFetchedRows > this.rows.size()) {
/* 159 */       afterLast();
/* 160 */     } else if (this.currentPositionInFetchedRows < this.rows.size()) {
/* 161 */       Row row = this.rows.get(this.currentPositionInFetchedRows);
/*     */       
/* 163 */       return row.setMetadata(this.metadata);
/*     */     } 
/*     */     
/* 166 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove() {
/* 171 */     this.rows.remove(getPosition());
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCurrentRow(int newIndex) {
/* 176 */     this.currentPositionInFetchedRows = newIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 181 */     return this.rows.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean wasEmpty() {
/* 186 */     return (this.rows != null && this.rows.size() == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\result\ResultsetRowsStatic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */