/*    */ package org.h2.index;
/*    */ 
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.result.Row;
/*    */ import org.h2.result.SearchRow;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueBigint;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class RangeCursor
/*    */   implements Cursor
/*    */ {
/*    */   private boolean beforeFirst;
/*    */   private long current;
/*    */   private Row currentRow;
/*    */   private final long start;
/*    */   private final long end;
/*    */   private final long step;
/*    */   
/*    */   RangeCursor(long paramLong1, long paramLong2, long paramLong3) {
/* 25 */     this.start = paramLong1;
/* 26 */     this.end = paramLong2;
/* 27 */     this.step = paramLong3;
/* 28 */     this.beforeFirst = true;
/*    */   }
/*    */ 
/*    */   
/*    */   public Row get() {
/* 33 */     return this.currentRow;
/*    */   }
/*    */ 
/*    */   
/*    */   public SearchRow getSearchRow() {
/* 38 */     return (SearchRow)this.currentRow;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean next() {
/* 43 */     if (this.beforeFirst) {
/* 44 */       this.beforeFirst = false;
/* 45 */       this.current = this.start;
/*    */     } else {
/* 47 */       this.current += this.step;
/*    */     } 
/* 49 */     this.currentRow = Row.get(new Value[] { (Value)ValueBigint.get(this.current) }, 1);
/* 50 */     return (this.step > 0L) ? ((this.current <= this.end)) : ((this.current >= this.end));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean previous() {
/* 55 */     throw DbException.getInternalError(toString());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\RangeCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */