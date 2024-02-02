package org.h2.mvstore.tx;

import java.util.BitSet;
import java.util.function.Function;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.type.DataType;
import org.h2.value.VersionedValue;

class TxDecisionMaker<K, V> extends MVMap.DecisionMaker<VersionedValue<V>> {
   private final int mapId;
   protected K key;
   private V value;
   private final Transaction transaction;
   private long undoKey;
   private long lastOperationId;
   private Transaction blockingTransaction;
   private MVMap.Decision decision;
   private V lastValue;

   TxDecisionMaker(int var1, Transaction var2) {
      this.mapId = var1;
      this.transaction = var2;
   }

   void initialize(K var1, V var2) {
      this.key = var1;
      this.value = var2;
      this.decision = null;
      this.reset();
   }

   public MVMap.Decision decide(VersionedValue<V> var1, VersionedValue<V> var2) {
      assert this.decision == null;

      long var3;
      int var5;
      if (var1 != null && (var3 = var1.getOperationId()) != 0L && !this.isThisTransaction(var5 = TransactionStore.getTransactionId(var3))) {
         Object var6;
         if (this.isCommitted(var5)) {
            var6 = var1.getCurrentValue();
            this.logAndDecideToPut(var6 == null ? null : VersionedValueCommitted.getInstance(var6), var6);
         } else if (this.getBlockingTransaction() != null) {
            this.lastValue = var1.getCurrentValue();
            this.decision = MVMap.Decision.ABORT;
         } else if (this.isRepeatedOperation(var3)) {
            var6 = var1.getCommittedValue();
            this.logAndDecideToPut(var6 == null ? null : VersionedValueCommitted.getInstance(var6), var6);
         } else {
            this.decision = MVMap.Decision.REPEAT;
         }
      } else {
         this.logAndDecideToPut(var1, var1 == null ? null : var1.getCommittedValue());
      }

      return this.decision;
   }

   public final void reset() {
      if (this.decision != MVMap.Decision.REPEAT) {
         this.lastOperationId = 0L;
         if (this.decision == MVMap.Decision.PUT) {
            this.transaction.logUndo();
         }
      }

      this.blockingTransaction = null;
      this.decision = null;
      this.lastValue = null;
   }

   public <T extends VersionedValue<V>> T selectValue(T var1, T var2) {
      return VersionedValueUncommitted.getInstance(this.undoKey, this.getNewValue(var1), this.lastValue);
   }

   V getNewValue(VersionedValue<V> var1) {
      return this.value;
   }

   MVMap.Decision logAndDecideToPut(VersionedValue<V> var1, V var2) {
      this.undoKey = this.transaction.log(new Record(this.mapId, this.key, var1));
      this.lastValue = var2;
      return this.setDecision(MVMap.Decision.PUT);
   }

   final MVMap.Decision decideToAbort(V var1) {
      this.lastValue = var1;
      return this.setDecision(MVMap.Decision.ABORT);
   }

   final boolean allowNonRepeatableRead() {
      return this.transaction.allowNonRepeatableRead();
   }

   final MVMap.Decision getDecision() {
      return this.decision;
   }

   final Transaction getBlockingTransaction() {
      return this.blockingTransaction;
   }

   final V getLastValue() {
      return this.lastValue;
   }

   final boolean isThisTransaction(int var1) {
      return var1 == this.transaction.transactionId;
   }

   final boolean isCommitted(int var1) {
      TransactionStore var4 = this.transaction.store;

      Transaction var2;
      boolean var3;
      do {
         var2 = var4.getTransaction(var1);
         var3 = ((BitSet)var4.committingTransactions.get()).get(var1);
      } while(var2 != var4.getTransaction(var1));

      if (!var3) {
         this.blockingTransaction = var2;
      }

      return var3;
   }

   final boolean isRepeatedOperation(long var1) {
      if (var1 == this.lastOperationId) {
         return true;
      } else {
         this.lastOperationId = var1;
         return false;
      }
   }

   final MVMap.Decision setDecision(MVMap.Decision var1) {
      return this.decision = var1;
   }

   public final String toString() {
      return "txdm " + this.transaction.transactionId;
   }

   public static final class RepeatableReadLockDecisionMaker<K, V> extends LockDecisionMaker<K, V> {
      private final DataType<VersionedValue<V>> valueType;
      private final Function<K, V> snapshotValueSupplier;

      RepeatableReadLockDecisionMaker(int var1, Transaction var2, DataType<VersionedValue<V>> var3, Function<K, V> var4) {
         super(var1, var2);
         this.valueType = var3;
         this.snapshotValueSupplier = var4;
      }

      MVMap.Decision logAndDecideToPut(VersionedValue<V> var1, V var2) {
         Object var3 = this.snapshotValueSupplier.apply(this.key);
         if (var3 == null || var1 != null && this.valueType.compare(VersionedValueCommitted.getInstance(var3), var1) == 0) {
            return super.logAndDecideToPut(var1, var2);
         } else {
            throw DataUtils.newMVStoreException(105, "");
         }
      }
   }

   public static class LockDecisionMaker<K, V> extends TxDecisionMaker<K, V> {
      LockDecisionMaker(int var1, Transaction var2) {
         super(var1, var2);
      }

      public MVMap.Decision decide(VersionedValue<V> var1, VersionedValue<V> var2) {
         MVMap.Decision var3 = super.decide(var1, var2);
         if (var1 == null) {
            assert var3 == MVMap.Decision.PUT;

            var3 = this.setDecision(MVMap.Decision.REMOVE);
         }

         return var3;
      }

      V getNewValue(VersionedValue<V> var1) {
         return var1 == null ? null : var1.getCurrentValue();
      }
   }

   public static final class PutIfAbsentDecisionMaker<K, V> extends TxDecisionMaker<K, V> {
      private final Function<K, V> oldValueSupplier;

      PutIfAbsentDecisionMaker(int var1, Transaction var2, Function<K, V> var3) {
         super(var1, var2);
         this.oldValueSupplier = var3;
      }

      public MVMap.Decision decide(VersionedValue<V> var1, VersionedValue<V> var2) {
         assert this.getDecision() == null;

         if (var1 == null) {
            Object var7 = this.getValueInSnapshot();
            return var7 != null ? this.decideToAbort(var7) : this.logAndDecideToPut((VersionedValue)null, (Object)null);
         } else {
            long var4 = var1.getOperationId();
            int var3;
            Object var6;
            if (var4 != 0L && !this.isThisTransaction(var3 = TransactionStore.getTransactionId(var4))) {
               if (this.isCommitted(var3)) {
                  if (var1.getCurrentValue() != null) {
                     return this.decideToAbort(var1.getCurrentValue());
                  } else {
                     var6 = this.getValueInSnapshot();
                     return var6 != null ? this.decideToAbort(var6) : this.logAndDecideToPut((VersionedValue)null, (Object)null);
                  }
               } else if (this.getBlockingTransaction() != null) {
                  return this.decideToAbort(var1.getCurrentValue());
               } else if (this.isRepeatedOperation(var4)) {
                  var6 = var1.getCommittedValue();
                  return var6 != null ? this.decideToAbort(var6) : this.logAndDecideToPut((VersionedValue)null, (Object)null);
               } else {
                  return this.setDecision(MVMap.Decision.REPEAT);
               }
            } else if (var1.getCurrentValue() != null) {
               return this.decideToAbort(var1.getCurrentValue());
            } else {
               if (var4 == 0L) {
                  var6 = this.getValueInSnapshot();
                  if (var6 != null) {
                     return this.decideToAbort(var6);
                  }
               }

               return this.logAndDecideToPut(var1, var1.getCommittedValue());
            }
         }
      }

      private V getValueInSnapshot() {
         return this.allowNonRepeatableRead() ? null : this.oldValueSupplier.apply(this.key);
      }
   }
}
