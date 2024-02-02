/*    */ package org.h2.result;
/*    */ 
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueBigint;
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
/*    */ public final class Sparse
/*    */   extends DefaultRow
/*    */ {
/*    */   private final int columnCount;
/*    */   private final int[] map;
/*    */   
/*    */   Sparse(int paramInt1, int paramInt2, int[] paramArrayOfint) {
/* 24 */     super(new Value[paramInt2]);
/* 25 */     this.columnCount = paramInt1;
/* 26 */     this.map = paramArrayOfint;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getColumnCount() {
/* 31 */     return this.columnCount;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(int paramInt) {
/* 36 */     if (paramInt == -1) {
/* 37 */       return (Value)ValueBigint.get(getKey());
/*    */     }
/* 39 */     int i = this.map[paramInt];
/* 40 */     return (i > 0) ? super.getValue(i - 1) : null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setValue(int paramInt, Value paramValue) {
/* 45 */     if (paramInt == -1) {
/* 46 */       setKey(paramValue.getLong());
/*    */     }
/* 48 */     int i = this.map[paramInt];
/* 49 */     if (i > 0) {
/* 50 */       super.setValue(i - 1, paramValue);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void copyFrom(SearchRow paramSearchRow) {
/* 56 */     setKey(paramSearchRow.getKey());
/* 57 */     for (byte b = 0; b < this.map.length; b++) {
/* 58 */       int i = this.map[b];
/* 59 */       if (i > 0)
/* 60 */         super.setValue(i - 1, paramSearchRow.getValue(b)); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\Sparse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */