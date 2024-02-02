/*    */ package org.h2.expression.aggregate;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ import java.util.TreeMap;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.value.Value;
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
/*    */ final class AggregateDataDistinctWithCounts
/*    */   extends AggregateData
/*    */ {
/*    */   private final boolean ignoreNulls;
/*    */   private final int maxDistinctCount;
/*    */   private TreeMap<Value, LongDataCounter> values;
/*    */   
/*    */   AggregateDataDistinctWithCounts(boolean paramBoolean, int paramInt) {
/* 35 */     this.ignoreNulls = paramBoolean;
/* 36 */     this.maxDistinctCount = paramInt;
/*    */   }
/*    */ 
/*    */   
/*    */   void add(SessionLocal paramSessionLocal, Value paramValue) {
/* 41 */     if (this.ignoreNulls && paramValue == ValueNull.INSTANCE) {
/*    */       return;
/*    */     }
/* 44 */     if (this.values == null) {
/* 45 */       this.values = new TreeMap<>((Comparator<? super Value>)paramSessionLocal.getDatabase().getCompareMode());
/*    */     }
/* 47 */     LongDataCounter longDataCounter = this.values.get(paramValue);
/* 48 */     if (longDataCounter == null) {
/* 49 */       if (this.values.size() >= this.maxDistinctCount) {
/*    */         return;
/*    */       }
/* 52 */       longDataCounter = new LongDataCounter();
/* 53 */       this.values.put(paramValue, longDataCounter);
/*    */     } 
/* 55 */     longDataCounter.count++;
/*    */   }
/*    */ 
/*    */   
/*    */   Value getValue(SessionLocal paramSessionLocal) {
/* 60 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   TreeMap<Value, LongDataCounter> getValues() {
/* 69 */     return this.values;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AggregateDataDistinctWithCounts.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */