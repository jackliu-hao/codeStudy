package org.h2.mvstore.tx;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.h2.engine.IsolationLevel;
import org.h2.mvstore.Cursor;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.RootReference;
import org.h2.mvstore.rtree.MVRTreeMap;
import org.h2.mvstore.rtree.SpatialDataType;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.LongDataType;
import org.h2.mvstore.type.MetaType;
import org.h2.mvstore.type.ObjectDataType;
import org.h2.mvstore.type.StringDataType;
import org.h2.util.StringUtils;
import org.h2.value.VersionedValue;

public class TransactionStore {
   final MVStore store;
   final int timeoutMillis;
   private final MVMap<Integer, Object[]> preparedTransactions;
   private final MVMap<String, DataType<?>> typeRegistry;
   final MVMap<Long, Record<?, ?>>[] undoLogs;
   private final MVMap.Builder<Long, Record<?, ?>> undoLogBuilder;
   private final DataType<?> dataType;
   final AtomicReference<VersionedBitSet> openTransactions;
   final AtomicReference<BitSet> committingTransactions;
   private boolean init;
   private int maxTransactionId;
   private final AtomicReferenceArray<Transaction> transactions;
   private static final String TYPE_REGISTRY_NAME = "_";
   public static final String UNDO_LOG_NAME_PREFIX = "undoLog";
   private static final char UNDO_LOG_COMMITTED = '-';
   private static final char UNDO_LOG_OPEN = '.';
   private static final int MAX_OPEN_TRANSACTIONS = 65535;
   private static final int LOG_ID_BITS = 40;
   private static final long LOG_ID_MASK = 1099511627775L;
   private static final RollbackListener ROLLBACK_LISTENER_NONE = (var0, var1, var2, var3) -> {
   };

   private static String getUndoLogName(int var0) {
      return var0 > 0 ? "undoLog." + var0 : "undoLog.";
   }

   public TransactionStore(MVStore var1) {
      this(var1, new ObjectDataType());
   }

   public TransactionStore(MVStore var1, DataType<?> var2) {
      this(var1, new MetaType((Object)null, var1.backgroundExceptionHandler), var2, 0);
   }

   public TransactionStore(MVStore var1, MetaType<?> var2, DataType<?> var3, int var4) {
      this.undoLogs = new MVMap['\uffff'];
      this.openTransactions = new AtomicReference(new VersionedBitSet());
      this.committingTransactions = new AtomicReference(new BitSet());
      this.maxTransactionId = 65535;
      this.transactions = new AtomicReferenceArray(65536);
      this.store = var1;
      this.dataType = var3;
      this.timeoutMillis = var4;
      this.typeRegistry = openTypeRegistry(var1, var2);
      this.preparedTransactions = var1.openMap("openTransactions", new MVMap.Builder());
      this.undoLogBuilder = this.createUndoLogBuilder();
   }

   MVMap.Builder<Long, Record<?, ?>> createUndoLogBuilder() {
      return (new MVMap.Builder()).singleWriter().keyType(LongDataType.INSTANCE).valueType(new Record.Type(this));
   }

   private static MVMap<String, DataType<?>> openTypeRegistry(MVStore var0, MetaType<?> var1) {
      MVMap.Builder var2 = (new MVMap.Builder()).keyType(StringDataType.INSTANCE).valueType(var1);
      return var0.openMap("_", var2);
   }

   public void init() {
      this.init(ROLLBACK_LISTENER_NONE);
   }

   public void init(RollbackListener var1) {
      if (!this.init) {
         Iterator var2 = this.store.getMapNames().iterator();

         while(true) {
            while(true) {
               String var3;
               do {
                  if (!var2.hasNext()) {
                     this.init = true;
                     return;
                  }

                  var3 = (String)var2.next();
               } while(!var3.startsWith("undoLog"));

               if (var3.length() > "undoLog".length()) {
                  boolean var4 = var3.charAt("undoLog".length()) == '-';
                  if (this.store.hasData(var3)) {
                     int var5 = StringUtils.parseUInt31(var3, "undoLog".length() + 1, var3.length());
                     VersionedBitSet var6 = (VersionedBitSet)this.openTransactions.get();
                     if (!var6.get(var5)) {
                        Object[] var7 = (Object[])this.preparedTransactions.get(var5);
                        int var8;
                        String var9;
                        if (var7 == null) {
                           var8 = 1;
                           var9 = null;
                        } else {
                           var8 = (Integer)var7[0];
                           var9 = (String)var7[1];
                        }

                        MVMap var10 = this.store.openMap(var3, this.undoLogBuilder);
                        this.undoLogs[var5] = var10;
                        Long var11 = (Long)var10.lastKey();

                        assert var11 != null;

                        assert getTransactionId(var11) == var5;

                        long var12 = getLogId(var11) + 1L;
                        if (var4) {
                           this.store.renameMap(var10, getUndoLogName(var5));
                           this.markUndoLogAsCommitted(var5);
                        } else {
                           var4 = var12 > 1099511627775L;
                        }

                        if (var4) {
                           var8 = 3;
                           var11 = (Long)var10.lowerKey(var11);

                           assert var11 == null || getTransactionId(var11) == var5;

                           var12 = var11 == null ? 0L : getLogId(var11) + 1L;
                        }

                        this.registerTransaction(var5, var8, var9, var12, this.timeoutMillis, 0, IsolationLevel.READ_COMMITTED, var1);
                        continue;
                     }
                  }
               }

               if (!this.store.isReadOnly()) {
                  this.store.removeMap(var3);
               }
            }
         }
      }
   }

   private void markUndoLogAsCommitted(int var1) {
      this.addUndoLogRecord(var1, 1099511627775L, Record.COMMIT_MARKER);
   }

   public void endLeftoverTransactions() {
      List var1 = this.getOpenTransactions();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Transaction var3 = (Transaction)var2.next();
         int var4 = var3.getStatus();
         if (var4 == 3) {
            var3.commit();
         } else if (var4 != 2) {
            var3.rollback();
         }
      }

   }

   int getMaxTransactionId() {
      return this.maxTransactionId;
   }

   public void setMaxTransactionId(int var1) {
      DataUtils.checkArgument(var1 <= 65535, "Concurrent transactions limit is too high: {0}", var1);
      this.maxTransactionId = var1;
   }

   public boolean hasMap(String var1) {
      return this.store.hasMap(var1);
   }

   static long getOperationId(int var0, long var1) {
      DataUtils.checkArgument(var0 >= 0 && var0 < 16777216, "Transaction id out of range: {0}", var0);
      DataUtils.checkArgument(var1 >= 0L && var1 <= 1099511627775L, "Transaction log id out of range: {0}", var1);
      return (long)var0 << 40 | var1;
   }

   static int getTransactionId(long var0) {
      return (int)(var0 >>> 40);
   }

   static long getLogId(long var0) {
      return var0 & 1099511627775L;
   }

   public List<Transaction> getOpenTransactions() {
      if (!this.init) {
         this.init();
      }

      ArrayList var1 = new ArrayList();
      int var2 = 0;
      BitSet var3 = (BitSet)this.openTransactions.get();

      while((var2 = var3.nextSetBit(var2 + 1)) > 0) {
         Transaction var4 = this.getTransaction(var2);
         if (var4 != null && var4.getStatus() != 0) {
            var1.add(var4);
         }
      }

      return var1;
   }

   public synchronized void close() {
      this.store.commit();
   }

   public Transaction begin() {
      return this.begin(ROLLBACK_LISTENER_NONE, this.timeoutMillis, 0, IsolationLevel.READ_COMMITTED);
   }

   public Transaction begin(RollbackListener var1, int var2, int var3, IsolationLevel var4) {
      Transaction var5 = this.registerTransaction(0, 1, (String)null, 0L, var2, var3, var4, var1);
      return var5;
   }

   private Transaction registerTransaction(int var1, int var2, String var3, long var4, int var6, int var7, IsolationLevel var8, RollbackListener var9) {
      int var10;
      long var11;
      boolean var13;
      do {
         VersionedBitSet var14 = (VersionedBitSet)this.openTransactions.get();
         if (var1 == 0) {
            var10 = var14.nextClearBit(1);
         } else {
            var10 = var1;

            assert !var14.get(var1);
         }

         if (var10 > this.maxTransactionId) {
            throw DataUtils.newMVStoreException(102, "There are {0} open transactions", var10 - 1);
         }

         VersionedBitSet var15 = var14.clone();
         var15.set(var10);
         var11 = var15.getVersion() + 1L;
         var15.setVersion(var11);
         var13 = this.openTransactions.compareAndSet(var14, var15);
      } while(!var13);

      Transaction var17 = new Transaction(this, var10, var11, var2, var3, var4, var6, var7, var8, var9);

      assert this.transactions.get(var10) == null;

      this.transactions.set(var10, var17);
      if (this.undoLogs[var10] == null) {
         String var18 = getUndoLogName(var10);
         MVMap var16 = this.store.openMap(var18, this.undoLogBuilder);
         this.undoLogs[var10] = var16;
      }

      return var17;
   }

   void storeTransaction(Transaction var1) {
      if (var1.getStatus() == 2 || var1.getName() != null) {
         Object[] var2 = new Object[]{var1.getStatus(), var1.getName()};
         this.preparedTransactions.put(var1.getId(), var2);
         var1.wasStored = true;
      }

   }

   long addUndoLogRecord(int var1, long var2, Record<?, ?> var4) {
      MVMap var5 = this.undoLogs[var1];
      long var6 = getOperationId(var1, var2);
      if (var2 == 0L && !var5.isEmpty()) {
         throw DataUtils.newMVStoreException(102, "An old transaction with the same id is still open: {0}", var1);
      } else {
         var5.append(var6, var4);
         return var6;
      }
   }

   void removeUndoLogRecord(int var1) {
      this.undoLogs[var1].trimLast();
   }

   void removeMap(TransactionMap<?, ?> var1) {
      this.store.removeMap(var1.map);
   }

   void commit(Transaction var1, boolean var2) {
      if (!this.store.isClosed()) {
         int var3 = var1.transactionId;
         MVMap var4 = this.undoLogs[var3];
         Cursor var5;
         if (var2) {
            this.removeUndoLogRecord(var3);
            var5 = var4.cursor((Object)null);
         } else {
            var5 = var4.cursor((Object)null);
            this.markUndoLogAsCommitted(var3);
         }

         this.flipCommittingTransactionsBit(var3, true);
         CommitDecisionMaker var6 = new CommitDecisionMaker();

         try {
            while(var5.hasNext()) {
               Long var7 = (Long)var5.next();
               Record var8 = (Record)var5.getValue();
               int var9 = var8.mapId;
               MVMap var10 = this.openMap(var9);
               if (var10 != null && !var10.isClosed()) {
                  Object var11 = var8.key;
                  var6.setUndoKey(var7);
                  var10.operate(var11, (Object)null, var6);
               }
            }
         } finally {
            try {
               var4.clear();
            } finally {
               this.flipCommittingTransactionsBit(var3, false);
            }
         }
      }

   }

   private void flipCommittingTransactionsBit(int var1, boolean var2) {
      boolean var3;
      do {
         BitSet var4 = (BitSet)this.committingTransactions.get();

         assert var4.get(var1) != var2 : var2 ? "Double commit" : "Mysterious bit's disappearance";

         BitSet var5 = (BitSet)var4.clone();
         var5.set(var1, var2);
         var3 = this.committingTransactions.compareAndSet(var4, var5);
      } while(!var3);

   }

   <K, V> MVMap<K, VersionedValue<V>> openVersionedMap(String var1, DataType<K> var2, DataType<V> var3) {
      VersionedValueType var4 = var3 == null ? null : new VersionedValueType(var3);
      return this.openMap(var1, var2, var4);
   }

   public <K, V> MVMap<K, V> openMap(String var1, DataType<K> var2, DataType<V> var3) {
      return this.store.openMap(var1, (new TxMapBuilder(this.typeRegistry, this.dataType)).keyType(var2).valueType(var3));
   }

   <K, V> MVMap<K, VersionedValue<V>> openMap(int var1) {
      MVMap var2 = this.store.getMap(var1);
      if (var2 == null) {
         String var3 = this.store.getMapName(var1);
         if (var3 == null) {
            return null;
         }

         TxMapBuilder var4 = new TxMapBuilder(this.typeRegistry, this.dataType);
         var2 = this.store.openMap(var1, var4);
      }

      return var2;
   }

   <K, V> MVMap<K, VersionedValue<V>> getMap(int var1) {
      MVMap var2 = this.store.getMap(var1);
      if (var2 == null && !this.init) {
         var2 = this.openMap(var1);
      }

      assert var2 != null : "map with id " + var1 + " is missing" + (this.init ? "" : " during initialization");

      return var2;
   }

   void endTransaction(Transaction var1, boolean var2) {
      var1.closeIt();
      int var3 = var1.transactionId;
      this.transactions.set(var3, (Object)null);

      boolean var4;
      do {
         VersionedBitSet var5 = (VersionedBitSet)this.openTransactions.get();

         assert var5.get(var3);

         VersionedBitSet var6 = var5.clone();
         var6.clear(var3);
         var4 = this.openTransactions.compareAndSet(var5, var6);
      } while(!var4);

      if (var2) {
         boolean var8 = var1.wasStored;
         if (var8 && !this.preparedTransactions.isClosed()) {
            this.preparedTransactions.remove(var3);
         }

         if (this.store.getFileStore() != null) {
            if (!var8 && this.store.getAutoCommitDelay() != 0) {
               if (this.isUndoEmpty()) {
                  int var9 = this.store.getUnsavedMemory();
                  int var7 = this.store.getAutoCommitMemory();
                  if (var9 * 4 > var7 * 3) {
                     this.store.tryCommit();
                  }
               }
            } else {
               this.store.commit();
            }
         }
      }

   }

   RootReference<Long, Record<?, ?>>[] collectUndoLogRootReferences() {
      BitSet var1 = (BitSet)this.openTransactions.get();
      RootReference[] var2 = new RootReference[var1.length()];

      for(int var3 = var1.nextSetBit(0); var3 >= 0; var3 = var1.nextSetBit(var3 + 1)) {
         MVMap var4 = this.undoLogs[var3];
         if (var4 != null) {
            RootReference var5 = var4.getRoot();
            if (var5.needFlush()) {
               return null;
            }

            var2[var3] = var5;
         }
      }

      return var2;
   }

   static long calculateUndoLogsTotalSize(RootReference<Long, Record<?, ?>>[] var0) {
      long var1 = 0L;
      RootReference[] var3 = var0;
      int var4 = var0.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         RootReference var6 = var3[var5];
         if (var6 != null) {
            var1 += var6.getTotalCount();
         }
      }

      return var1;
   }

   private boolean isUndoEmpty() {
      BitSet var1 = (BitSet)this.openTransactions.get();

      for(int var2 = var1.nextSetBit(0); var2 >= 0; var2 = var1.nextSetBit(var2 + 1)) {
         MVMap var3 = this.undoLogs[var2];
         if (var3 != null && !var3.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   Transaction getTransaction(int var1) {
      return (Transaction)this.transactions.get(var1);
   }

   void rollbackTo(Transaction var1, long var2, long var4) {
      int var6 = var1.getId();
      MVMap var7 = this.undoLogs[var6];
      RollbackDecisionMaker var8 = new RollbackDecisionMaker(this, (long)var6, var4, var1.listener);

      for(long var9 = var2 - 1L; var9 >= var4; --var9) {
         Long var11 = getOperationId(var6, var9);
         var7.operate(var11, (Object)null, var8);
         var8.reset();
      }

   }

   Iterator<Change> getChanges(final Transaction var1, final long var2, final long var4) {
      final MVMap var6 = this.undoLogs[var1.getId()];
      return new Iterator<Change>() {
         private long logId = var2 - 1L;
         private Change current;

         private void fetchNext() {
            int var1x = var1.getId();

            while(this.logId >= var4) {
               Long var2x = TransactionStore.getOperationId(var1x, this.logId);
               Record var3 = (Record)var6.get(var2x);
               --this.logId;
               if (var3 == null) {
                  var2x = (Long)var6.floorKey(var2x);
                  if (var2x == null || TransactionStore.getTransactionId(var2x) != var1x) {
                     break;
                  }

                  this.logId = TransactionStore.getLogId(var2x);
               } else {
                  int var4x = var3.mapId;
                  MVMap var5 = TransactionStore.this.openMap(var4x);
                  if (var5 != null) {
                     VersionedValue var6x = var3.oldValue;
                     this.current = new Change(var5.getName(), var3.key, var6x == null ? null : var6x.getCurrentValue());
                     return;
                  }
               }
            }

            this.current = null;
         }

         public boolean hasNext() {
            if (this.current == null) {
               this.fetchNext();
            }

            return this.current != null;
         }

         public Change next() {
            if (!this.hasNext()) {
               throw DataUtils.newUnsupportedOperationException("no data");
            } else {
               Change var1x = this.current;
               this.current = null;
               return var1x;
            }
         }
      };
   }

   private static final class TxMapBuilder<K, V> extends MVMap.Builder<K, V> {
      private final MVMap<String, DataType<?>> typeRegistry;
      private final DataType defaultDataType;

      TxMapBuilder(MVMap<String, DataType<?>> var1, DataType<?> var2) {
         this.typeRegistry = var1;
         this.defaultDataType = var2;
      }

      private void registerDataType(DataType<?> var1) {
         String var2 = getDataTypeRegistrationKey(var1);
         DataType var3 = (DataType)this.typeRegistry.putIfAbsent(var2, var1);
         if (var3 != null) {
         }

      }

      static String getDataTypeRegistrationKey(DataType<?> var0) {
         return Integer.toHexString(Objects.hashCode(var0));
      }

      public MVMap<K, V> create(MVStore var1, Map<String, Object> var2) {
         DataType var3 = this.getKeyType();
         if (var3 == null) {
            String var4 = (String)var2.remove("key");
            if (var4 != null) {
               var3 = (DataType)this.typeRegistry.get(var4);
               if (var3 == null) {
                  throw DataUtils.newMVStoreException(106, "Data type with hash {0} can not be found", var4);
               }

               this.setKeyType(var3);
            }
         } else {
            this.registerDataType(var3);
         }

         DataType var6 = this.getValueType();
         if (var6 == null) {
            String var5 = (String)var2.remove("val");
            if (var5 != null) {
               var6 = (DataType)this.typeRegistry.get(var5);
               if (var6 == null) {
                  throw DataUtils.newMVStoreException(106, "Data type with hash {0} can not be found", var5);
               }

               this.setValueType(var6);
            }
         } else {
            this.registerDataType(var6);
         }

         if (this.getKeyType() == null) {
            this.setKeyType(this.defaultDataType);
            this.registerDataType(this.getKeyType());
         }

         if (this.getValueType() == null) {
            this.setValueType(new VersionedValueType(this.defaultDataType));
            this.registerDataType(this.getValueType());
         }

         var2.put("store", var1);
         var2.put("key", this.getKeyType());
         var2.put("val", this.getValueType());
         return this.create(var2);
      }

      protected MVMap<K, V> create(Map<String, Object> var1) {
         if ("rtree".equals(var1.get("type"))) {
            MVRTreeMap var2 = new MVRTreeMap(var1, (SpatialDataType)this.getKeyType(), this.getValueType());
            return var2;
         } else {
            return new TMVMap(var1, this.getKeyType(), this.getValueType());
         }
      }

      private static final class TMVMap<K, V> extends MVMap<K, V> {
         private final String type;

         TMVMap(Map<String, Object> var1, DataType<K> var2, DataType<V> var3) {
            super(var1, var2, var3);
            this.type = (String)var1.get("type");
         }

         private TMVMap(MVMap<K, V> var1) {
            super(var1);
            this.type = var1.getType();
         }

         protected MVMap<K, V> cloneIt() {
            return new TMVMap(this);
         }

         public String getType() {
            return this.type;
         }

         protected String asString(String var1) {
            StringBuilder var2 = new StringBuilder();
            var2.append(super.asString(var1));
            DataUtils.appendMap(var2, "key", TransactionStore.TxMapBuilder.getDataTypeRegistrationKey(this.getKeyType()));
            DataUtils.appendMap(var2, "val", TransactionStore.TxMapBuilder.getDataTypeRegistrationKey(this.getValueType()));
            return var2.toString();
         }
      }
   }

   public interface RollbackListener {
      void onRollback(MVMap<Object, VersionedValue<Object>> var1, Object var2, VersionedValue<Object> var3, VersionedValue<Object> var4);
   }

   public static class Change {
      public final String mapName;
      public final Object key;
      public final Object value;

      public Change(String var1, Object var2, Object var3) {
         this.mapName = var1;
         this.key = var2;
         this.value = var3;
      }
   }
}
