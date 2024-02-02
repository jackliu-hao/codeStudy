/*    */ package org.h2.result;
/*    */ 
/*    */ import org.h2.engine.Session;
/*    */ import org.h2.value.Value;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class FetchedResult
/*    */   implements ResultInterface
/*    */ {
/* 16 */   long rowId = -1L;
/*    */ 
/*    */   
/*    */   Value[] currentRow;
/*    */ 
/*    */   
/*    */   Value[] nextRow;
/*    */ 
/*    */   
/*    */   boolean afterLast;
/*    */ 
/*    */   
/*    */   public final Value[] currentRow() {
/* 29 */     return this.currentRow;
/*    */   }
/*    */ 
/*    */   
/*    */   public final boolean next() {
/* 34 */     if (hasNext()) {
/* 35 */       this.rowId++;
/* 36 */       this.currentRow = this.nextRow;
/* 37 */       this.nextRow = null;
/* 38 */       return true;
/*    */     } 
/* 40 */     if (!this.afterLast) {
/* 41 */       this.rowId++;
/* 42 */       this.currentRow = null;
/* 43 */       this.afterLast = true;
/*    */     } 
/* 45 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public final boolean isAfterLast() {
/* 50 */     return this.afterLast;
/*    */   }
/*    */ 
/*    */   
/*    */   public final long getRowId() {
/* 55 */     return this.rowId;
/*    */   }
/*    */ 
/*    */   
/*    */   public final boolean needToClose() {
/* 60 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public final ResultInterface createShallowCopy(Session paramSession) {
/* 66 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\FetchedResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */