/*    */ package org.h2.result;
/*    */ 
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueBigint;
/*    */ import org.h2.value.ValueNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimpleRowValue
/*    */   extends SearchRow
/*    */ {
/*    */   private int index;
/*    */   private final int virtualColumnCount;
/*    */   private Value data;
/*    */   
/*    */   public SimpleRowValue(int paramInt) {
/* 23 */     this.virtualColumnCount = paramInt;
/*    */   }
/*    */   
/*    */   public SimpleRowValue(int paramInt1, int paramInt2) {
/* 27 */     this.virtualColumnCount = paramInt1;
/* 28 */     this.index = paramInt2;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getColumnCount() {
/* 33 */     return this.virtualColumnCount;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(int paramInt) {
/* 38 */     if (paramInt == -1) {
/* 39 */       return (Value)ValueBigint.get(getKey());
/*    */     }
/* 41 */     return (paramInt == this.index) ? this.data : null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setValue(int paramInt, Value paramValue) {
/* 46 */     if (paramInt == -1) {
/* 47 */       setKey(paramValue.getLong());
/*    */     }
/* 49 */     this.index = paramInt;
/* 50 */     this.data = paramValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 55 */     return "( /* " + this.key + " */ " + ((this.data == null) ? "null" : this.data
/* 56 */       .getTraceSQL()) + " )";
/*    */   }
/*    */ 
/*    */   
/*    */   public int getMemory() {
/* 61 */     return 40 + ((this.data == null) ? 0 : this.data.getMemory());
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isNull(int paramInt) {
/* 66 */     return (paramInt != this.index || this.data == null || this.data == ValueNull.INSTANCE);
/*    */   }
/*    */ 
/*    */   
/*    */   public void copyFrom(SearchRow paramSearchRow) {
/* 71 */     setKey(paramSearchRow.getKey());
/* 72 */     setValue(this.index, paramSearchRow.getValue(this.index));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\SimpleRowValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */