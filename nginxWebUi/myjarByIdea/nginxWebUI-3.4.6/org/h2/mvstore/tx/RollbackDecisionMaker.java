package org.h2.mvstore.tx;

import org.h2.mvstore.MVMap;
import org.h2.value.VersionedValue;

final class RollbackDecisionMaker extends MVMap.DecisionMaker<Record<?, ?>> {
   private final TransactionStore store;
   private final long transactionId;
   private final long toLogId;
   private final TransactionStore.RollbackListener listener;
   private MVMap.Decision decision;

   RollbackDecisionMaker(TransactionStore var1, long var2, long var4, TransactionStore.RollbackListener var6) {
      this.store = var1;
      this.transactionId = var2;
      this.toLogId = var4;
      this.listener = var6;
   }

   public MVMap.Decision decide(Record var1, Record var2) {
      assert this.decision == null;

      if (var1 == null) {
         this.decision = MVMap.Decision.ABORT;
      } else {
         VersionedValue var3 = var1.oldValue;
         long var4;
         if (var3 == null || (var4 = var3.getOperationId()) == 0L || (long)TransactionStore.getTransactionId(var4) == this.transactionId && TransactionStore.getLogId(var4) < this.toLogId) {
            int var6 = var1.mapId;
            MVMap var7 = this.store.openMap(var6);
            if (var7 != null && !var7.isClosed()) {
               Object var8 = var1.key;
               VersionedValue var9 = (VersionedValue)var7.operate(var8, var3, MVMap.DecisionMaker.DEFAULT);
               this.listener.onRollback(var7, var8, var9, var3);
            }
         }

         this.decision = MVMap.Decision.REMOVE;
      }

      return this.decision;
   }

   public void reset() {
      this.decision = null;
   }

   public String toString() {
      return "rollback-" + this.transactionId;
   }
}
