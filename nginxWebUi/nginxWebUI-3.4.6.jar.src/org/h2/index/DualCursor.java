/*    */ package org.h2.index;
/*    */ 
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.result.Row;
/*    */ import org.h2.result.SearchRow;
/*    */ import org.h2.value.Value;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class DualCursor
/*    */   implements Cursor
/*    */ {
/*    */   private Row currentRow;
/*    */   
/*    */   public Row get() {
/* 25 */     return this.currentRow;
/*    */   }
/*    */ 
/*    */   
/*    */   public SearchRow getSearchRow() {
/* 30 */     return (SearchRow)this.currentRow;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean next() {
/* 35 */     if (this.currentRow == null) {
/* 36 */       this.currentRow = Row.get(Value.EMPTY_VALUES, 1);
/* 37 */       return true;
/*    */     } 
/* 39 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean previous() {
/* 45 */     throw DbException.getInternalError(toString());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\DualCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */