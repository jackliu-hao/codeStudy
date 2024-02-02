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
/*    */ final class RollbackDecisionMaker
/*    */   extends MVMap.DecisionMaker<Record<?, ?>>
/*    */ {
/*    */   private final TransactionStore store;
/*    */   private final long transactionId;
/*    */   private final long toLogId;
/*    */   private final TransactionStore.RollbackListener listener;
/*    */   private MVMap.Decision decision;
/*    */   
/*    */   RollbackDecisionMaker(TransactionStore paramTransactionStore, long paramLong1, long paramLong2, TransactionStore.RollbackListener paramRollbackListener) {
/* 25 */     this.store = paramTransactionStore;
/* 26 */     this.transactionId = paramLong1;
/* 27 */     this.toLogId = paramLong2;
/* 28 */     this.listener = paramRollbackListener;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public MVMap.Decision decide(Record paramRecord1, Record paramRecord2) {
/* 34 */     assert this.decision == null;
/* 35 */     if (paramRecord1 == null) {
/*    */ 
/*    */       
/* 38 */       this.decision = MVMap.Decision.ABORT;
/*    */     } else {
/* 40 */       VersionedValue<Object> versionedValue = paramRecord1.oldValue;
/*    */       long l;
/* 42 */       if (versionedValue == null || (
/* 43 */         l = versionedValue.getOperationId()) == 0L || (
/* 44 */         TransactionStore.getTransactionId(l) == this.transactionId && 
/* 45 */         TransactionStore.getLogId(l) < this.toLogId)) {
/* 46 */         int i = paramRecord1.mapId;
/* 47 */         MVMap<?, VersionedValue<?>> mVMap = this.store.openMap(i);
/* 48 */         if (mVMap != null && !mVMap.isClosed()) {
/* 49 */           K k = paramRecord1.key;
/* 50 */           VersionedValue<Object> versionedValue1 = (VersionedValue)mVMap.operate(k, versionedValue, MVMap.DecisionMaker.DEFAULT);
/*    */           
/* 52 */           this.listener.onRollback((MVMap)mVMap, k, versionedValue1, versionedValue);
/*    */         } 
/*    */       } 
/* 55 */       this.decision = MVMap.Decision.REMOVE;
/*    */     } 
/* 57 */     return this.decision;
/*    */   }
/*    */ 
/*    */   
/*    */   public void reset() {
/* 62 */     this.decision = null;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 67 */     return "rollback-" + this.transactionId;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\tx\RollbackDecisionMaker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */