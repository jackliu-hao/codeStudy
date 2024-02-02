/*    */ package org.h2.mvstore.tx;
/*    */ 
/*    */ import org.h2.mvstore.MVMap;
/*    */ import org.h2.value.VersionedValue;
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
/*    */ final class CommitDecisionMaker<V>
/*    */   extends MVMap.DecisionMaker<VersionedValue<V>>
/*    */ {
/*    */   private long undoKey;
/*    */   private MVMap.Decision decision;
/*    */   
/*    */   void setUndoKey(long paramLong) {
/* 23 */     this.undoKey = paramLong;
/* 24 */     reset();
/*    */   }
/*    */ 
/*    */   
/*    */   public MVMap.Decision decide(VersionedValue<V> paramVersionedValue1, VersionedValue<V> paramVersionedValue2) {
/* 29 */     assert this.decision == null;
/* 30 */     if (paramVersionedValue1 == null || paramVersionedValue1
/*    */ 
/*    */       
/* 33 */       .getOperationId() != this.undoKey) {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 39 */       this.decision = MVMap.Decision.ABORT;
/* 40 */     } else if (paramVersionedValue1.getCurrentValue() == null) {
/* 41 */       this.decision = MVMap.Decision.REMOVE;
/*    */     } else {
/* 43 */       this.decision = MVMap.Decision.PUT;
/*    */     } 
/* 45 */     return this.decision;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public <T extends VersionedValue<V>> T selectValue(T paramT1, T paramT2) {
/* 51 */     assert this.decision == MVMap.Decision.PUT;
/* 52 */     assert paramT1 != null;
/* 53 */     return (T)VersionedValueCommitted.getInstance(paramT1.getCurrentValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public void reset() {
/* 58 */     this.decision = null;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 63 */     return "commit " + TransactionStore.getTransactionId(this.undoKey);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\tx\CommitDecisionMaker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */