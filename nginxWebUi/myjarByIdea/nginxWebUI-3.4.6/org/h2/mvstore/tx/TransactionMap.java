package org.h2.mvstore.tx;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import org.h2.engine.IsolationLevel;
import org.h2.mvstore.Cursor;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.RootReference;
import org.h2.mvstore.type.DataType;
import org.h2.value.VersionedValue;

public final class TransactionMap<K, V> extends AbstractMap<K, V> {
   public final MVMap<K, VersionedValue<V>> map;
   private final Transaction transaction;
   private Snapshot<K, VersionedValue<V>> snapshot;
   private Snapshot<K, VersionedValue<V>> statementSnapshot;
   private boolean hasChanges;
   private final TxDecisionMaker<K, V> txDecisionMaker;
   private final TxDecisionMaker<K, V> ifAbsentDecisionMaker;
   private final TxDecisionMaker<K, V> lockDecisionMaker;

   TransactionMap(Transaction var1, MVMap<K, VersionedValue<V>> var2) {
      this.transaction = var1;
      this.map = var2;
      this.txDecisionMaker = new TxDecisionMaker(var2.getId(), var1);
      this.ifAbsentDecisionMaker = new TxDecisionMaker.PutIfAbsentDecisionMaker(var2.getId(), var1, this::getFromSnapshot);
      this.lockDecisionMaker = (TxDecisionMaker)(var1.allowNonRepeatableRead() ? new TxDecisionMaker.LockDecisionMaker(var2.getId(), var1) : new TxDecisionMaker.RepeatableReadLockDecisionMaker(var2.getId(), var1, var2.getValueType(), this::getFromSnapshot));
   }

   public TransactionMap<K, V> getInstance(Transaction var1) {
      return var1.openMapX(this.map);
   }

   public int size() {
      long var1 = this.sizeAsLong();
      return var1 > 2147483647L ? Integer.MAX_VALUE : (int)var1;
   }

   public long sizeAsLongMax() {
      return this.map.sizeAsLong();
   }

   public long sizeAsLong() {
      IsolationLevel var1 = this.transaction.getIsolationLevel();
      if (!var1.allowNonRepeatableRead() && this.hasChanges) {
         return this.sizeAsLongRepeatableReadWithChanges();
      } else {
         Snapshot var2;
         RootReference[] var3;
         do {
            var2 = this.getSnapshot();
            var3 = this.getTransaction().getUndoLogRootReferences();
         } while(!var2.equals(this.getSnapshot()));

         RootReference var4 = var2.root;
         long var5 = var4.getTotalCount();
         long var7 = var3 == null ? var5 : TransactionStore.calculateUndoLogsTotalSize(var3);
         return var7 == 0L ? var5 : this.adjustSize(var3, var4, var1 == IsolationLevel.READ_UNCOMMITTED ? null : var2.committingTransactions, var5, var7);
      }
   }

   private long adjustSize(RootReference<Long, Record<?, ?>>[] var1, RootReference<K, VersionedValue<V>> var2, BitSet var3, long var4, long var6) {
      if (2L * var6 > var4) {
         Cursor var8 = this.map.cursor(var2, (Object)null, (Object)null, false);

         while(var8.hasNext()) {
            var8.next();
            VersionedValue var9 = (VersionedValue)var8.getValue();

            assert var9 != null;

            long var10 = var9.getOperationId();
            if (var10 != 0L && this.isIrrelevant(var10, var9, var3)) {
               --var4;
            }
         }
      } else {
         assert var1 != null;

         RootReference[] var17 = var1;
         int var18 = var1.length;

         for(int var19 = 0; var19 < var18; ++var19) {
            RootReference var11 = var17[var19];
            if (var11 != null) {
               Cursor var12 = var11.root.map.cursor(var11, (Object)null, (Object)null, false);

               while(var12.hasNext()) {
                  var12.next();
                  Record var13 = (Record)var12.getValue();
                  if (var13.mapId == this.map.getId()) {
                     VersionedValue var14 = (VersionedValue)this.map.get(var2.root, var13.key);
                     if (var14 != null) {
                        long var15 = (Long)var12.getKey();

                        assert var15 != 0L;

                        if (var14.getOperationId() == var15 && this.isIrrelevant(var15, var14, var3)) {
                           --var4;
                        }
                     }
                  }
               }
            }
         }
      }

      return var4;
   }

   private boolean isIrrelevant(long var1, VersionedValue<?> var3, BitSet var4) {
      Object var5;
      if (var4 == null) {
         var5 = var3.getCurrentValue();
      } else {
         int var6 = TransactionStore.getTransactionId(var1);
         var5 = var6 != this.transaction.transactionId && !var4.get(var6) ? var3.getCommittedValue() : var3.getCurrentValue();
      }

      return var5 == null;
   }

   private long sizeAsLongRepeatableReadWithChanges() {
      long var1 = 0L;

      for(RepeatableIterator var3 = new RepeatableIterator(this, (Object)null, (Object)null, false, false); var3.fetchNext() != null; ++var1) {
      }

      return var1;
   }

   public V remove(Object var1) {
      return this.set(var1, (Object)null);
   }

   public V put(K var1, V var2) {
      DataUtils.checkArgument(var2 != null, "The value may not be null");
      return this.set(var1, var2);
   }

   public V putIfAbsent(K var1, V var2) {
      DataUtils.checkArgument(var2 != null, "The value may not be null");
      this.ifAbsentDecisionMaker.initialize(var1, var2);
      Object var3 = this.set(var1, this.ifAbsentDecisionMaker);
      if (this.ifAbsentDecisionMaker.getDecision() == MVMap.Decision.ABORT) {
         var3 = this.ifAbsentDecisionMaker.getLastValue();
      }

      return var3;
   }

   public void append(K var1, V var2) {
      this.map.append(var1, VersionedValueUncommitted.getInstance(this.transaction.log(new Record(this.map.getId(), var1, (VersionedValue)null)), var2, (Object)null));
      this.hasChanges = true;
   }

   public V lock(K var1) {
      this.lockDecisionMaker.initialize(var1, (Object)null);
      return this.set(var1, this.lockDecisionMaker);
   }

   public V putCommitted(K var1, V var2) {
      DataUtils.checkArgument(var2 != null, "The value may not be null");
      VersionedValue var3 = VersionedValueCommitted.getInstance(var2);
      VersionedValue var4 = (VersionedValue)this.map.put(var1, var3);
      Object var5 = var4 == null ? null : var4.getCurrentValue();
      return var5;
   }

   private V set(K var1, V var2) {
      this.txDecisionMaker.initialize(var1, var2);
      return this.set(var1, this.txDecisionMaker);
   }

   private V set(Object var1, TxDecisionMaker<K, V> var2) {
      String var5 = null;

      Transaction var3;
      VersionedValue var4;
      do {
         assert this.transaction.getBlockerId() == 0;

         var4 = (VersionedValue)this.map.operate(var1, (Object)null, var2);
         MVMap.Decision var7 = var2.getDecision();

         assert var7 != null;

         assert var7 != MVMap.Decision.REPEAT;

         var3 = var2.getBlockingTransaction();
         if (var7 != MVMap.Decision.ABORT || var3 == null) {
            this.hasChanges |= var7 != MVMap.Decision.ABORT;
            Object var8 = var4 == null ? null : var4.getCurrentValue();
            return var8;
         }

         var2.reset();
         if (var5 == null) {
            var5 = this.map.getName();
         }
      } while(this.transaction.waitFor(var3, var5, var1));

      throw DataUtils.newMVStoreException(101, "Map entry <{0}> with key <{1}> and value {2} is locked by tx {3} and can not be updated by tx {4} within allocated time interval {5} ms.", var5, var1, var4, var3.transactionId, this.transaction.transactionId, this.transaction.timeoutMillis);
   }

   public boolean tryRemove(K var1) {
      return this.trySet(var1, (Object)null);
   }

   public boolean tryPut(K var1, V var2) {
      DataUtils.checkArgument(var2 != null, "The value may not be null");
      return this.trySet(var1, var2);
   }

   public boolean trySet(K var1, V var2) {
      try {
         this.set(var1, var2);
         return true;
      } catch (MVStoreException var4) {
         return false;
      }
   }

   public V get(Object var1) {
      return this.getImmediate(var1);
   }

   public V getFromSnapshot(K var1) {
      Snapshot var2;
      VersionedValue var3;
      switch (this.transaction.isolationLevel) {
         case READ_UNCOMMITTED:
            var2 = this.getStatementSnapshot();
            var3 = (VersionedValue)this.map.get(var2.root.root, var1);
            if (var3 != null) {
               return var3.getCurrentValue();
            }

            return null;
         case REPEATABLE_READ:
         case SNAPSHOT:
         case SERIALIZABLE:
            if (this.transaction.hasChanges()) {
               var2 = this.getStatementSnapshot();
               var3 = (VersionedValue)this.map.get(var2.root.root, var1);
               if (var3 != null) {
                  long var4 = var3.getOperationId();
                  if (var4 != 0L && this.transaction.transactionId == TransactionStore.getTransactionId(var4)) {
                     return var3.getCurrentValue();
                  }
               }
            }
         case READ_COMMITTED:
         default:
            var2 = this.getSnapshot();
            return this.getFromSnapshot(var2.root, var2.committingTransactions, var1);
      }
   }

   private V getFromSnapshot(RootReference<K, VersionedValue<V>> var1, BitSet var2, K var3) {
      VersionedValue var4 = (VersionedValue)this.map.get(var1.root, var3);
      if (var4 == null) {
         return null;
      } else {
         long var5 = var4.getOperationId();
         if (var5 != 0L) {
            int var7 = TransactionStore.getTransactionId(var5);
            if (var7 != this.transaction.transactionId && !var2.get(var7)) {
               return var4.getCommittedValue();
            }
         }

         return var4.getCurrentValue();
      }
   }

   public V getImmediate(K var1) {
      return this.useSnapshot((var2, var3) -> {
         return this.getFromSnapshot(var2, var3, var1);
      });
   }

   Snapshot<K, VersionedValue<V>> getSnapshot() {
      return this.snapshot == null ? this.createSnapshot() : this.snapshot;
   }

   Snapshot<K, VersionedValue<V>> getStatementSnapshot() {
      return this.statementSnapshot == null ? this.createSnapshot() : this.statementSnapshot;
   }

   void setStatementSnapshot(Snapshot<K, VersionedValue<V>> var1) {
      this.statementSnapshot = var1;
   }

   void promoteSnapshot() {
      if (this.snapshot == null) {
         this.snapshot = this.statementSnapshot;
      }

   }

   Snapshot<K, VersionedValue<V>> createSnapshot() {
      return (Snapshot)this.useSnapshot(Snapshot::new);
   }

   <R> R useSnapshot(BiFunction<RootReference<K, VersionedValue<V>>, BitSet, R> var1) {
      AtomicReference var2 = this.transaction.store.committingTransactions;
      BitSet var3 = (BitSet)var2.get();

      BitSet var4;
      RootReference var5;
      do {
         var4 = var3;
         var5 = this.map.getRoot();
         var3 = (BitSet)var2.get();
      } while(var3 != var4);

      return var1.apply(var5, var3);
   }

   public boolean containsKey(Object var1) {
      return this.getImmediate(var1) != null;
   }

   public boolean isDeletedByCurrentTransaction(K var1) {
      VersionedValue var2 = (VersionedValue)this.map.get(var1);
      if (var2 == null) {
         return false;
      } else {
         long var3 = var2.getOperationId();
         return var3 != 0L && TransactionStore.getTransactionId(var3) == this.transaction.transactionId && var2.getCurrentValue() == null;
      }
   }

   public boolean isSameTransaction(K var1) {
      VersionedValue var2 = (VersionedValue)this.map.get(var1);
      if (var2 == null) {
         return false;
      } else {
         int var3 = TransactionStore.getTransactionId(var2.getOperationId());
         return var3 == this.transaction.transactionId;
      }
   }

   public boolean isClosed() {
      return this.map.isClosed();
   }

   public void clear() {
      this.map.clear();
      this.hasChanges = true;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      return new AbstractSet<Map.Entry<K, V>>() {
         public Iterator<Map.Entry<K, V>> iterator() {
            return TransactionMap.this.entryIterator((Object)null, (Object)null);
         }

         public int size() {
            return TransactionMap.this.size();
         }

         public boolean contains(Object var1) {
            return TransactionMap.this.containsKey(var1);
         }
      };
   }

   public Map.Entry<K, V> firstEntry() {
      return (Map.Entry)this.chooseIterator((Object)null, (Object)null, false, true).fetchNext();
   }

   public K firstKey() {
      return this.chooseIterator((Object)null, (Object)null, false, false).fetchNext();
   }

   public Map.Entry<K, V> lastEntry() {
      return (Map.Entry)this.chooseIterator((Object)null, (Object)null, true, true).fetchNext();
   }

   public K lastKey() {
      return this.chooseIterator((Object)null, (Object)null, true, false).fetchNext();
   }

   public Map.Entry<K, V> higherEntry(K var1) {
      return this.higherLowerEntry(var1, false);
   }

   public K higherKey(K var1) {
      return this.higherLowerKey(var1, false);
   }

   public Map.Entry<K, V> ceilingEntry(K var1) {
      return (Map.Entry)this.chooseIterator(var1, (Object)null, false, true).fetchNext();
   }

   public K ceilingKey(K var1) {
      return this.chooseIterator(var1, (Object)null, false, false).fetchNext();
   }

   public Map.Entry<K, V> floorEntry(K var1) {
      return (Map.Entry)this.chooseIterator(var1, (Object)null, true, true).fetchNext();
   }

   public K floorKey(K var1) {
      return this.chooseIterator(var1, (Object)null, true, false).fetchNext();
   }

   public Map.Entry<K, V> lowerEntry(K var1) {
      return this.higherLowerEntry(var1, true);
   }

   public K lowerKey(K var1) {
      return this.higherLowerKey(var1, true);
   }

   private Map.Entry<K, V> higherLowerEntry(K var1, boolean var2) {
      TMIterator var3 = this.chooseIterator(var1, (Object)null, var2, true);
      Map.Entry var4 = (Map.Entry)var3.fetchNext();
      if (var4 != null && this.map.getKeyType().compare(var1, var4.getKey()) == 0) {
         var4 = (Map.Entry)var3.fetchNext();
      }

      return var4;
   }

   private K higherLowerKey(K var1, boolean var2) {
      TMIterator var3 = this.chooseIterator(var1, (Object)null, var2, false);
      Object var4 = var3.fetchNext();
      if (var4 != null && this.map.getKeyType().compare(var1, var4) == 0) {
         var4 = var3.fetchNext();
      }

      return var4;
   }

   public Iterator<K> keyIterator(K var1) {
      return this.chooseIterator(var1, (Object)null, false, false);
   }

   public TMIterator<K, V, K> keyIterator(K var1, boolean var2) {
      return this.chooseIterator(var1, (Object)null, var2, false);
   }

   public TMIterator<K, V, K> keyIterator(K var1, K var2) {
      return this.chooseIterator(var1, var2, false, false);
   }

   public TMIterator<K, V, K> keyIteratorUncommitted(K var1, K var2) {
      return new ValidationIterator(this, var1, var2);
   }

   public TMIterator<K, V, Map.Entry<K, V>> entryIterator(K var1, K var2) {
      return this.chooseIterator(var1, var2, false, true);
   }

   private <X> TMIterator<K, V, X> chooseIterator(K var1, K var2, boolean var3, boolean var4) {
      switch (this.transaction.isolationLevel) {
         case READ_UNCOMMITTED:
            return new UncommittedIterator(this, var1, var2, var3, var4);
         case REPEATABLE_READ:
         case SNAPSHOT:
         case SERIALIZABLE:
            if (this.hasChanges) {
               return new RepeatableIterator(this, var1, var2, var3, var4);
            }
         case READ_COMMITTED:
         default:
            return new CommittedIterator(this, var1, var2, var3, var4);
      }
   }

   public Transaction getTransaction() {
      return this.transaction;
   }

   public DataType<K> getKeyType() {
      return this.map.getKeyType();
   }

   public abstract static class TMIterator<K, V, X> implements Iterator<X> {
      final int transactionId;
      final BitSet committingTransactions;
      protected final Cursor<K, VersionedValue<V>> cursor;
      private final boolean forEntries;
      X current;

      TMIterator(TransactionMap<K, V> var1, K var2, K var3, Snapshot<K, VersionedValue<V>> var4, boolean var5, boolean var6) {
         Transaction var7 = var1.getTransaction();
         this.transactionId = var7.transactionId;
         this.forEntries = var6;
         this.cursor = var1.map.cursor(var4.root, var2, var3, var5);
         this.committingTransactions = var4.committingTransactions;
      }

      final X toElement(K var1, Object var2) {
         return this.forEntries ? new AbstractMap.SimpleImmutableEntry(var1, var2) : var1;
      }

      public abstract X fetchNext();

      public final boolean hasNext() {
         return this.current != null || (this.current = this.fetchNext()) != null;
      }

      public final X next() {
         Object var1 = this.current;
         if (var1 == null) {
            if ((var1 = this.fetchNext()) == null) {
               throw new NoSuchElementException();
            }
         } else {
            this.current = null;
         }

         return var1;
      }
   }

   private static final class RepeatableIterator<K, V, X> extends TMIterator<K, V, X> {
      private final DataType<K> keyType;
      private K snapshotKey;
      private Object snapshotValue;
      private final Cursor<K, VersionedValue<V>> uncommittedCursor;
      private K uncommittedKey;
      private V uncommittedValue;

      RepeatableIterator(TransactionMap<K, V> var1, K var2, K var3, boolean var4, boolean var5) {
         super(var1, var2, var3, var1.getSnapshot(), var4, var5);
         this.keyType = var1.map.getKeyType();
         Snapshot var6 = var1.getStatementSnapshot();
         this.uncommittedCursor = var1.map.cursor(var6.root, var2, var3, var4);
      }

      public X fetchNext() {
         Object var1 = null;

         do {
            if (this.snapshotKey == null) {
               this.fetchSnapshot();
            }

            if (this.uncommittedKey == null) {
               this.fetchUncommitted();
            }

            if (this.snapshotKey == null && this.uncommittedKey == null) {
               break;
            }

            int var2 = this.snapshotKey == null ? 1 : (this.uncommittedKey == null ? -1 : this.keyType.compare(this.snapshotKey, this.uncommittedKey));
            if (var2 < 0) {
               var1 = this.toElement(this.snapshotKey, this.snapshotValue);
               this.snapshotKey = null;
               break;
            }

            if (this.uncommittedValue != null) {
               var1 = this.toElement(this.uncommittedKey, this.uncommittedValue);
            }

            if (var2 == 0) {
               this.snapshotKey = null;
            }

            this.uncommittedKey = null;
         } while(var1 == null);

         return var1;
      }

      private void fetchSnapshot() {
         while(true) {
            if (this.cursor.hasNext()) {
               Object var1 = this.cursor.next();
               VersionedValue var2 = (VersionedValue)this.cursor.getValue();
               if (var2 == null) {
                  continue;
               }

               Object var3 = var2.getCommittedValue();
               long var4 = var2.getOperationId();
               if (var4 != 0L) {
                  int var6 = TransactionStore.getTransactionId(var4);
                  if (var6 == this.transactionId || this.committingTransactions.get(var6)) {
                     var3 = var2.getCurrentValue();
                  }
               }

               if (var3 == null) {
                  continue;
               }

               this.snapshotKey = var1;
               this.snapshotValue = var3;
               return;
            }

            return;
         }
      }

      private void fetchUncommitted() {
         while(true) {
            if (this.uncommittedCursor.hasNext()) {
               Object var1 = this.uncommittedCursor.next();
               VersionedValue var2 = (VersionedValue)this.uncommittedCursor.getValue();
               if (var2 == null) {
                  continue;
               }

               long var3 = var2.getOperationId();
               if (var3 == 0L || this.transactionId != TransactionStore.getTransactionId(var3)) {
                  continue;
               }

               this.uncommittedKey = var1;
               this.uncommittedValue = var2.getCurrentValue();
               return;
            }

            return;
         }
      }
   }

   private static final class CommittedIterator<K, V, X> extends TMIterator<K, V, X> {
      CommittedIterator(TransactionMap<K, V> var1, K var2, K var3, boolean var4, boolean var5) {
         super(var1, var2, var3, var1.getSnapshot(), var4, var5);
      }

      public X fetchNext() {
         while(true) {
            if (this.cursor.hasNext()) {
               Object var1 = this.cursor.next();
               VersionedValue var2 = (VersionedValue)this.cursor.getValue();
               if (var2 == null) {
                  continue;
               }

               long var3 = var2.getOperationId();
               if (var3 != 0L) {
                  int var5 = TransactionStore.getTransactionId(var3);
                  if (var5 != this.transactionId && !this.committingTransactions.get(var5)) {
                     Object var6 = var2.getCommittedValue();
                     if (var6 == null) {
                        continue;
                     }

                     return this.toElement(var1, var6);
                  }
               }

               Object var7 = var2.getCurrentValue();
               if (var7 == null) {
                  continue;
               }

               return this.toElement(var1, var7);
            }

            return null;
         }
      }
   }

   private static final class ValidationIterator<K, V, X> extends UncommittedIterator<K, V, X> {
      ValidationIterator(TransactionMap<K, V> var1, K var2, K var3) {
         super(var1, var2, var3, var1.createSnapshot(), false, false);
      }

      boolean shouldIgnoreRemoval(VersionedValue<?> var1) {
         assert var1.getCurrentValue() == null;

         long var2 = var1.getOperationId();
         if (var2 == 0L) {
            return false;
         } else {
            int var4 = TransactionStore.getTransactionId(var2);
            return this.transactionId != var4 && !this.committingTransactions.get(var4);
         }
      }
   }

   private static class UncommittedIterator<K, V, X> extends TMIterator<K, V, X> {
      UncommittedIterator(TransactionMap<K, V> var1, K var2, K var3, boolean var4, boolean var5) {
         super(var1, var2, var3, var1.createSnapshot(), var4, var5);
      }

      UncommittedIterator(TransactionMap<K, V> var1, K var2, K var3, Snapshot<K, VersionedValue<V>> var4, boolean var5, boolean var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public final X fetchNext() {
         while(true) {
            if (this.cursor.hasNext()) {
               Object var1 = this.cursor.next();
               VersionedValue var2 = (VersionedValue)this.cursor.getValue();
               if (var2 == null) {
                  continue;
               }

               Object var3 = var2.getCurrentValue();
               if (var3 == null && !this.shouldIgnoreRemoval(var2)) {
                  continue;
               }

               return this.toElement(var1, var3);
            }

            return null;
         }
      }

      boolean shouldIgnoreRemoval(VersionedValue<?> var1) {
         return false;
      }
   }
}
