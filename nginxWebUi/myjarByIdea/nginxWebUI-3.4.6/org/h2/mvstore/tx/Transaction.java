package org.h2.mvstore.tx;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.h2.engine.IsolationLevel;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.RootReference;
import org.h2.mvstore.type.DataType;
import org.h2.value.VersionedValue;

public final class Transaction {
   public static final int STATUS_CLOSED = 0;
   public static final int STATUS_OPEN = 1;
   public static final int STATUS_PREPARED = 2;
   public static final int STATUS_COMMITTED = 3;
   private static final int STATUS_ROLLING_BACK = 4;
   private static final int STATUS_ROLLED_BACK = 5;
   private static final String[] STATUS_NAMES = new String[]{"CLOSED", "OPEN", "PREPARED", "COMMITTED", "ROLLING_BACK", "ROLLED_BACK"};
   static final int LOG_ID_BITS = 40;
   private static final int LOG_ID_BITS1 = 41;
   private static final long LOG_ID_LIMIT = 1099511627776L;
   private static final long LOG_ID_MASK = 2199023255551L;
   private static final int STATUS_BITS = 4;
   private static final int STATUS_MASK = 15;
   final TransactionStore store;
   final TransactionStore.RollbackListener listener;
   final int transactionId;
   final long sequenceNum;
   private final AtomicLong statusAndLogId;
   private MVStore.TxCounter txCounter;
   private String name;
   boolean wasStored;
   int timeoutMillis;
   private final int ownerId;
   private volatile Transaction blockingTransaction;
   private String blockingMapName;
   private Object blockingKey;
   private volatile boolean notificationRequested;
   private RootReference<Long, Record<?, ?>>[] undoLogRootReferences;
   private final Map<Integer, TransactionMap<?, ?>> transactionMaps = new HashMap();
   final IsolationLevel isolationLevel;

   Transaction(TransactionStore var1, int var2, long var3, int var5, String var6, long var7, int var9, int var10, IsolationLevel var11, TransactionStore.RollbackListener var12) {
      this.store = var1;
      this.transactionId = var2;
      this.sequenceNum = var3;
      this.statusAndLogId = new AtomicLong(composeState(var5, var7, false));
      this.name = var6;
      this.setTimeoutMillis(var9);
      this.ownerId = var10;
      this.isolationLevel = var11;
      this.listener = var12;
   }

   public int getId() {
      return this.transactionId;
   }

   public long getSequenceNum() {
      return this.sequenceNum;
   }

   public int getStatus() {
      return getStatus(this.statusAndLogId.get());
   }

   RootReference<Long, Record<?, ?>>[] getUndoLogRootReferences() {
      return this.undoLogRootReferences;
   }

   private long setStatus(int var1) {
      long var2;
      long var8;
      do {
         var2 = this.statusAndLogId.get();
         long var4 = getLogId(var2);
         int var6 = getStatus(var2);
         boolean var7;
         switch (var1) {
            case 0:
               var7 = var6 == 3 || var6 == 5;
               break;
            case 1:
            default:
               var7 = false;
               break;
            case 2:
               var7 = var6 == 1;
               break;
            case 3:
               var7 = var6 == 1 || var6 == 2 || var6 == 3;
               break;
            case 4:
               var7 = var6 == 1;
               break;
            case 5:
               var7 = var6 == 1 || var6 == 2 || var6 == 4;
         }

         if (!var7) {
            throw DataUtils.newMVStoreException(103, "Transaction was illegally transitioned from {0} to {1}", getStatusName(var6), getStatusName(var1));
         }

         var8 = composeState(var1, var4, hasRollback(var2));
      } while(!this.statusAndLogId.compareAndSet(var2, var8));

      return var2;
   }

   public boolean hasChanges() {
      return hasChanges(this.statusAndLogId.get());
   }

   public void setName(String var1) {
      this.checkNotClosed();
      this.name = var1;
      this.store.storeTransaction(this);
   }

   public String getName() {
      return this.name;
   }

   public int getBlockerId() {
      Transaction var1 = this.blockingTransaction;
      return var1 == null ? 0 : var1.ownerId;
   }

   public long setSavepoint() {
      return this.getLogId();
   }

   public boolean hasStatementDependencies() {
      return !this.transactionMaps.isEmpty();
   }

   public IsolationLevel getIsolationLevel() {
      return this.isolationLevel;
   }

   boolean isReadCommitted() {
      return this.isolationLevel == IsolationLevel.READ_COMMITTED;
   }

   public boolean allowNonRepeatableRead() {
      return this.isolationLevel.allowNonRepeatableRead();
   }

   public void markStatementStart(HashSet<MVMap<Object, VersionedValue<Object>>> var1) {
      this.markStatementEnd();
      if (this.txCounter == null) {
         this.txCounter = this.store.store.registerVersionUsage();
      }

      if (var1 != null && !var1.isEmpty()) {
         BitSet var2;
         Iterator var3;
         MVMap var4;
         TransactionMap var5;
         do {
            var2 = (BitSet)this.store.committingTransactions.get();
            var3 = var1.iterator();

            while(var3.hasNext()) {
               var4 = (MVMap)var3.next();
               var5 = this.openMapX(var4);
               var5.setStatementSnapshot(new Snapshot(var4.flushAndGetRoot(), var2));
            }

            if (this.isReadCommitted()) {
               this.undoLogRootReferences = this.store.collectUndoLogRootReferences();
            }
         } while(var2 != this.store.committingTransactions.get());

         var3 = var1.iterator();

         while(var3.hasNext()) {
            var4 = (MVMap)var3.next();
            var5 = this.openMapX(var4);
            var5.promoteSnapshot();
         }

      }
   }

   public void markStatementEnd() {
      if (this.allowNonRepeatableRead()) {
         this.releaseSnapshot();
      }

      Iterator var1 = this.transactionMaps.values().iterator();

      while(var1.hasNext()) {
         TransactionMap var2 = (TransactionMap)var1.next();
         var2.setStatementSnapshot((Snapshot)null);
      }

   }

   private void markTransactionEnd() {
      if (!this.allowNonRepeatableRead()) {
         this.releaseSnapshot();
      }

   }

   private void releaseSnapshot() {
      this.transactionMaps.clear();
      this.undoLogRootReferences = null;
      MVStore.TxCounter var1 = this.txCounter;
      if (var1 != null) {
         this.txCounter = null;
         this.store.store.deregisterVersionUsage(var1);
      }

   }

   long log(Record<?, ?> var1) {
      long var2 = this.statusAndLogId.getAndIncrement();
      long var4 = getLogId(var2);
      if (var4 >= 1099511627776L) {
         throw DataUtils.newMVStoreException(104, "Transaction {0} has too many changes", this.transactionId);
      } else {
         int var6 = getStatus(var2);
         this.checkOpen(var6);
         long var7 = this.store.addUndoLogRecord(this.transactionId, var4, var1);
         return var7;
      }
   }

   void logUndo() {
      long var1 = this.statusAndLogId.decrementAndGet();
      long var3 = getLogId(var1);
      if (var3 >= 1099511627776L) {
         throw DataUtils.newMVStoreException(100, "Transaction {0} has internal error", this.transactionId);
      } else {
         int var5 = getStatus(var1);
         this.checkOpen(var5);
         this.store.removeUndoLogRecord(this.transactionId);
      }
   }

   public <K, V> TransactionMap<K, V> openMap(String var1) {
      return this.openMap(var1, (DataType)null, (DataType)null);
   }

   public <K, V> TransactionMap<K, V> openMap(String var1, DataType<K> var2, DataType<V> var3) {
      MVMap var4 = this.store.openVersionedMap(var1, var2, var3);
      return this.openMapX(var4);
   }

   public <K, V> TransactionMap<K, V> openMapX(MVMap<K, VersionedValue<V>> var1) {
      this.checkNotClosed();
      int var2 = var1.getId();
      TransactionMap var3 = (TransactionMap)this.transactionMaps.get(var2);
      if (var3 == null) {
         var3 = new TransactionMap(this, var1);
         this.transactionMaps.put(var2, var3);
      }

      return var3;
   }

   public void prepare() {
      this.setStatus(2);
      this.store.storeTransaction(this);
   }

   public void commit() {
      assert ((VersionedBitSet)this.store.openTransactions.get()).get(this.transactionId);

      this.markTransactionEnd();
      Throwable var1 = null;
      boolean var2 = false;
      int var3 = 1;

      try {
         long var4 = this.setStatus(3);
         var2 = hasChanges(var4);
         var3 = getStatus(var4);
         if (var2) {
            this.store.commit(this, var3 == 3);
         }
      } catch (Throwable var13) {
         var1 = var13;
         throw var13;
      } finally {
         if (isActive(var3)) {
            try {
               this.store.endTransaction(this, var2);
            } catch (Throwable var12) {
               if (var1 == null) {
                  throw var12;
               }

               var1.addSuppressed(var12);
            }
         }

      }

   }

   public void rollbackToSavepoint(long var1) {
      long var3 = this.setStatus(4);
      long var5 = getLogId(var3);
      boolean var18 = false;

      boolean var7;
      try {
         var18 = true;
         this.store.rollbackTo(this, var5, var1);
         var18 = false;
      } finally {
         if (var18) {
            this.notifyAllWaitingTransactions();
            long var13 = composeState(4, var5, hasRollback(var3));
            long var15 = composeState(1, var1, true);

            do {
               var7 = this.statusAndLogId.compareAndSet(var13, var15);
            } while(!var7 && this.statusAndLogId.get() == var13);

         }
      }

      this.notifyAllWaitingTransactions();
      long var8 = composeState(4, var5, hasRollback(var3));
      long var10 = composeState(1, var1, true);

      do {
         var7 = this.statusAndLogId.compareAndSet(var8, var10);
      } while(!var7 && this.statusAndLogId.get() == var8);

      if (!var7) {
         throw DataUtils.newMVStoreException(103, "Transaction {0} concurrently modified while rollback to savepoint was in progress", this.transactionId);
      }
   }

   public void rollback() {
      this.markTransactionEnd();
      Throwable var1 = null;
      int var2 = 1;

      try {
         long var3 = this.setStatus(5);
         var2 = getStatus(var3);
         long var5 = getLogId(var3);
         if (var5 > 0L) {
            this.store.rollbackTo(this, var5, 0L);
         }
      } catch (Throwable var15) {
         var2 = this.getStatus();
         if (isActive(var2)) {
            var1 = var15;
            throw var15;
         }
      } finally {
         try {
            if (isActive(var2)) {
               this.store.endTransaction(this, true);
            }
         } catch (Throwable var14) {
            if (var1 == null) {
               throw var14;
            }

            var1.addSuppressed(var14);
         }

      }

   }

   private static boolean isActive(int var0) {
      return var0 != 0 && var0 != 3 && var0 != 5;
   }

   public Iterator<TransactionStore.Change> getChanges(long var1) {
      return this.store.getChanges(this, this.getLogId(), var1);
   }

   public void setTimeoutMillis(int var1) {
      this.timeoutMillis = var1 > 0 ? var1 : this.store.timeoutMillis;
   }

   private long getLogId() {
      return getLogId(this.statusAndLogId.get());
   }

   private void checkOpen(int var1) {
      if (var1 != 1) {
         throw DataUtils.newMVStoreException(103, "Transaction {0} has status {1}, not OPEN", this.transactionId, getStatusName(var1));
      }
   }

   private void checkNotClosed() {
      if (this.getStatus() == 0) {
         throw DataUtils.newMVStoreException(4, "Transaction {0} is closed", this.transactionId);
      }
   }

   void closeIt() {
      this.transactionMaps.clear();
      long var1 = this.setStatus(0);
      this.store.store.deregisterVersionUsage(this.txCounter);
      if (hasChanges(var1) || hasRollback(var1)) {
         this.notifyAllWaitingTransactions();
      }

   }

   private void notifyAllWaitingTransactions() {
      if (this.notificationRequested) {
         synchronized(this) {
            this.notifyAll();
         }
      }

   }

   public boolean waitFor(Transaction var1, String var2, Object var3) {
      this.blockingTransaction = var1;
      this.blockingMapName = var2;
      this.blockingKey = var3;
      if (this.isDeadlocked(var1)) {
         this.tryThrowDeadLockException(false);
      }

      boolean var4 = var1.waitForThisToEnd(this.timeoutMillis, this);
      this.blockingMapName = null;
      this.blockingKey = null;
      this.blockingTransaction = null;
      return var4;
   }

   private boolean isDeadlocked(Transaction var1) {
      Transaction var2 = var1;
      int var3 = this.store.getMaxTransactionId();

      Transaction var5;
      for(Transaction var4 = var1; (var5 = var4.blockingTransaction) != null && var4.getStatus() == 1 && var3 > 0; --var3) {
         if (var5.sequenceNum > var2.sequenceNum) {
            var2 = var5;
         }

         if (var5 == this) {
            if (var2 == this) {
               return true;
            }

            Transaction var6 = var2.blockingTransaction;
            if (var6 != null) {
               var2.setStatus(4);
               var6.notifyAllWaitingTransactions();
               return false;
            }
         }

         var4 = var5;
      }

      return false;
   }

   private void tryThrowDeadLockException(boolean var1) {
      BitSet var2 = new BitSet();
      StringBuilder var3 = new StringBuilder(String.format("Transaction %d has been chosen as a deadlock victim. Details:%n", this.transactionId));

      Transaction var5;
      for(Transaction var4 = this; !var2.get(var4.transactionId) && (var5 = var4.blockingTransaction) != null; var4 = var5) {
         var2.set(var4.transactionId);
         var3.append(String.format("Transaction %d attempts to update map <%s> entry with key <%s> modified by transaction %s%n", var4.transactionId, var4.blockingMapName, var4.blockingKey, var4.blockingTransaction));
         if (var5 == this) {
            var1 = true;
         }
      }

      if (var1) {
         throw DataUtils.newMVStoreException(105, "{0}", var3.toString());
      }
   }

   private synchronized boolean waitForThisToEnd(int var1, Transaction var2) {
      long var3 = System.currentTimeMillis() + (long)var1;
      this.notificationRequested = true;

      long var5;
      int var7;
      while((var7 = getStatus(var5 = this.statusAndLogId.get())) != 0 && var7 != 5 && !hasRollback(var5)) {
         if (var2.getStatus() != 1) {
            var2.tryThrowDeadLockException(true);
         }

         long var8 = var3 - System.currentTimeMillis();
         if (var8 <= 0L) {
            return false;
         }

         try {
            this.wait(var8);
         } catch (InterruptedException var11) {
            return false;
         }
      }

      return true;
   }

   public <K, V> void removeMap(TransactionMap<K, V> var1) {
      this.store.removeMap(var1);
   }

   public String toString() {
      return this.transactionId + "(" + this.sequenceNum + ") " + this.stateToString();
   }

   private String stateToString() {
      return stateToString(this.statusAndLogId.get());
   }

   private static String stateToString(long var0) {
      return getStatusName(getStatus(var0)) + (hasRollback(var0) ? "<" : "") + " " + getLogId(var0);
   }

   private static int getStatus(long var0) {
      return (int)(var0 >>> 41) & 15;
   }

   private static long getLogId(long var0) {
      return var0 & 2199023255551L;
   }

   private static boolean hasRollback(long var0) {
      return (var0 & 35184372088832L) != 0L;
   }

   private static boolean hasChanges(long var0) {
      return getLogId(var0) != 0L;
   }

   private static long composeState(int var0, long var1, boolean var3) {
      assert var1 < 1099511627776L : var1;

      assert (var0 & -16) == 0 : var0;

      if (var3) {
         var0 |= 16;
      }

      return (long)var0 << 41 | var1;
   }

   private static String getStatusName(int var0) {
      return var0 >= 0 && var0 < STATUS_NAMES.length ? STATUS_NAMES[var0] : "UNKNOWN_STATUS_" + var0;
   }
}
