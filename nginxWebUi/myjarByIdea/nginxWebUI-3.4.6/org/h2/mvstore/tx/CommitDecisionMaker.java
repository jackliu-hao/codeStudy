package org.h2.mvstore.tx;

import org.h2.mvstore.MVMap;
import org.h2.value.VersionedValue;

final class CommitDecisionMaker<V> extends MVMap.DecisionMaker<VersionedValue<V>> {
   private long undoKey;
   private MVMap.Decision decision;

   void setUndoKey(long var1) {
      this.undoKey = var1;
      this.reset();
   }

   public MVMap.Decision decide(VersionedValue<V> var1, VersionedValue<V> var2) {
      assert this.decision == null;

      if (var1 != null && var1.getOperationId() == this.undoKey) {
         if (var1.getCurrentValue() == null) {
            this.decision = MVMap.Decision.REMOVE;
         } else {
            this.decision = MVMap.Decision.PUT;
         }
      } else {
         this.decision = MVMap.Decision.ABORT;
      }

      return this.decision;
   }

   public <T extends VersionedValue<V>> T selectValue(T var1, T var2) {
      assert this.decision == MVMap.Decision.PUT;

      assert var1 != null;

      return VersionedValueCommitted.getInstance(var1.getCurrentValue());
   }

   public void reset() {
      this.decision = null;
   }

   public String toString() {
      return "commit " + TransactionStore.getTransactionId(this.undoKey);
   }
}
