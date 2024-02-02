package org.h2.mvstore.db;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.h2.command.ddl.CreateTableData;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintReferential;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.SysProperties;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.mode.DefaultNullOrdering;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionStore;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.schema.SchemaObject;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.table.TableBase;
import org.h2.table.TableType;
import org.h2.util.DebuggingThreadLocal;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;

public class MVTable extends TableBase {
   public static final DebuggingThreadLocal<String> WAITING_FOR_LOCK;
   public static final DebuggingThreadLocal<ArrayList<String>> EXCLUSIVE_LOCKS;
   public static final DebuggingThreadLocal<ArrayList<String>> SHARED_LOCKS;
   private static final String NO_EXTRA_INFO = "";
   private final boolean containsLargeObject;
   private volatile SessionLocal lockExclusiveSession;
   private final ConcurrentHashMap<SessionLocal, SessionLocal> lockSharedSessions = new ConcurrentHashMap();
   private Column rowIdColumn;
   private final MVPrimaryIndex primaryIndex;
   private final ArrayList<Index> indexes = Utils.newSmallArrayList();
   private final AtomicLong lastModificationId = new AtomicLong();
   private final ArrayDeque<SessionLocal> waitingSessions = new ArrayDeque();
   private final Trace traceLock;
   private final AtomicInteger changesUntilAnalyze;
   private int nextAnalyze;
   private final Store store;
   private final TransactionStore transactionStore;

   public MVTable(CreateTableData var1, Store var2) {
      super(var1);
      this.isHidden = var1.isHidden;
      boolean var3 = false;
      Column[] var4 = this.getColumns();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Column var7 = var4[var6];
         if (DataType.isLargeObject(var7.getType().getValueType())) {
            var3 = true;
            break;
         }
      }

      this.containsLargeObject = var3;
      this.nextAnalyze = this.database.getSettings().analyzeAuto;
      this.changesUntilAnalyze = this.nextAnalyze <= 0 ? null : new AtomicInteger(this.nextAnalyze);
      this.store = var2;
      this.transactionStore = var2.getTransactionStore();
      this.traceLock = this.database.getTrace(7);
      this.primaryIndex = new MVPrimaryIndex(this.database, this, this.getId(), IndexColumn.wrap(this.getColumns()), IndexType.createScan(true));
      this.indexes.add(this.primaryIndex);
   }

   public String getMapName() {
      return this.primaryIndex.getMapName();
   }

   public boolean lock(SessionLocal var1, int var2) {
      if (this.database.getLockMode() == 0) {
         var1.registerTableAsUpdated(this);
         return false;
      } else if (var2 == 0 && this.lockExclusiveSession == null) {
         return false;
      } else if (this.lockExclusiveSession == var1) {
         return true;
      } else if (var2 != 2 && this.lockSharedSessions.containsKey(var1)) {
         return true;
      } else {
         synchronized(this) {
            if (var2 != 2 && this.lockSharedSessions.containsKey(var1)) {
               return true;
            } else {
               var1.setWaitForLock(this, Thread.currentThread());
               if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
                  WAITING_FOR_LOCK.set(this.getName());
               }

               this.waitingSessions.addLast(var1);

               try {
                  this.doLock1(var1, var2);
               } finally {
                  var1.setWaitForLock((Table)null, (Thread)null);
                  if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
                     WAITING_FOR_LOCK.remove();
                  }

                  this.waitingSessions.remove(var1);
               }

               return false;
            }
         }
      }
   }

   private void doLock1(SessionLocal var1, int var2) {
      this.traceLock(var1, var2, MVTable.TraceLockEvent.TRACE_LOCK_REQUESTING_FOR, "");
      long var3 = 0L;
      boolean var5 = false;

      while(this.waitingSessions.getFirst() != var1 || this.lockExclusiveSession != null || !this.doLock2(var1, var2)) {
         if (var5) {
            ArrayList var6 = this.checkDeadlock(var1, (SessionLocal)null, (Set)null);
            if (var6 != null) {
               throw DbException.get(40001, (String)getDeadlockDetails(var6, var2));
            }
         } else {
            var5 = true;
         }

         long var11 = System.nanoTime();
         if (var3 == 0L) {
            var3 = Utils.nanoTimePlusMillis(var11, var1.getLockTimeout());
         } else if (var11 - var3 >= 0L) {
            this.traceLock(var1, var2, MVTable.TraceLockEvent.TRACE_LOCK_TIMEOUT_AFTER, Integer.toString(var1.getLockTimeout()));
            throw DbException.get(50200, (String)this.getName());
         }

         try {
            this.traceLock(var1, var2, MVTable.TraceLockEvent.TRACE_LOCK_WAITING_FOR, "");
            long var8 = Math.min(100L, (var3 - var11) / 1000000L);
            if (var8 == 0L) {
               var8 = 1L;
            }

            this.wait(var8);
         } catch (InterruptedException var10) {
         }
      }

   }

   private boolean doLock2(SessionLocal var1, int var2) {
      switch (var2) {
         case 1:
            if (this.lockSharedSessions.putIfAbsent(var1, var1) == null) {
               this.traceLock(var1, var2, MVTable.TraceLockEvent.TRACE_LOCK_OK, "");
               var1.registerTableAsLocked(this);
               if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
                  this.addLockToDebugList(SHARED_LOCKS);
               }
            }
            break;
         case 2:
            int var3 = this.lockSharedSessions.size();
            if (var3 == 0) {
               this.traceLock(var1, var2, MVTable.TraceLockEvent.TRACE_LOCK_ADDED_FOR, "");
               var1.registerTableAsLocked(this);
            } else {
               if (var3 != 1 || !this.lockSharedSessions.containsKey(var1)) {
                  return false;
               }

               this.traceLock(var1, var2, MVTable.TraceLockEvent.TRACE_LOCK_ADD_UPGRADED_FOR, "");
            }

            this.lockExclusiveSession = var1;
            if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
               this.addLockToDebugList(EXCLUSIVE_LOCKS);
            }
      }

      return true;
   }

   private void addLockToDebugList(DebuggingThreadLocal<ArrayList<String>> var1) {
      ArrayList var2 = (ArrayList)var1.get();
      if (var2 == null) {
         var2 = new ArrayList();
         var1.set(var2);
      }

      var2.add(this.getName());
   }

   private void traceLock(SessionLocal var1, int var2, TraceLockEvent var3, String var4) {
      if (this.traceLock.isDebugEnabled()) {
         this.traceLock.debug("{0} {1} {2} {3} {4}", var1.getId(), lockTypeToString(var2), var3.getEventText(), this.getName(), var4);
      }

   }

   public void unlock(SessionLocal var1) {
      if (this.database != null) {
         int var2;
         ArrayList var3;
         if (this.lockExclusiveSession == var1) {
            var2 = 2;
            this.lockSharedSessions.remove(var1);
            this.lockExclusiveSession = null;
            if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
               var3 = (ArrayList)EXCLUSIVE_LOCKS.get();
               if (var3 != null) {
                  var3.remove(this.getName());
               }
            }
         } else {
            var2 = this.lockSharedSessions.remove(var1) != null ? 1 : 0;
            if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
               var3 = (ArrayList)SHARED_LOCKS.get();
               if (var3 != null) {
                  var3.remove(this.getName());
               }
            }
         }

         this.traceLock(var1, var2, MVTable.TraceLockEvent.TRACE_LOCK_UNLOCK, "");
         if (var2 != 0 && !this.waitingSessions.isEmpty()) {
            synchronized(this) {
               this.notifyAll();
            }
         }
      }

   }

   public void close(SessionLocal var1) {
   }

   public Row getRow(SessionLocal var1, long var2) {
      return this.primaryIndex.getRow(var1, var2);
   }

   public Index addIndex(SessionLocal var1, String var2, int var3, IndexColumn[] var4, int var5, IndexType var6, boolean var7, String var8) {
      var4 = prepareColumns(this.database, var4, var6);
      boolean var9 = this.isTemporary() && !this.isGlobalTemporary();
      if (!var9) {
         this.database.lockMeta(var1);
      }

      int var11 = this.primaryIndex.getMainIndexColumn() != -1 ? -1 : getMainIndexColumn(var6, var4);
      if (this.database.isStarting()) {
         if (this.transactionStore.hasMap("index." + var3)) {
            var11 = -1;
         }
      } else if (this.primaryIndex.getRowCountMax() != 0L) {
         var11 = -1;
      }

      Object var10;
      if (var11 != -1) {
         this.primaryIndex.setMainIndexColumn(var11);
         var10 = new MVDelegateIndex(this, var3, var2, this.primaryIndex, var6);
      } else if (var6.isSpatial()) {
         var10 = new MVSpatialIndex(var1.getDatabase(), this, var3, var2, var4, var5, var6);
      } else {
         var10 = new MVSecondaryIndex(var1.getDatabase(), this, var3, var2, var4, var5, var6);
      }

      if (((MVIndex)var10).needRebuild()) {
         this.rebuildIndex(var1, (MVIndex)var10, var2);
      }

      ((MVIndex)var10).setTemporary(this.isTemporary());
      if (((MVIndex)var10).getCreateSQL() != null) {
         ((MVIndex)var10).setComment(var8);
         if (var9) {
            var1.addLocalTempTableIndex((Index)var10);
         } else {
            this.database.addSchemaObject(var1, (SchemaObject)var10);
         }
      }

      this.indexes.add(var10);
      this.setModified();
      return (Index)var10;
   }

   private void rebuildIndex(SessionLocal var1, MVIndex<?, ?> var2, String var3) {
      try {
         if (var1.getDatabase().isPersistent() && !(var2 instanceof MVSpatialIndex)) {
            this.rebuildIndexBlockMerge(var1, var2);
         } else {
            this.rebuildIndexBuffered(var1, var2);
         }

      } catch (DbException var7) {
         this.getSchema().freeUniqueName(var3);

         try {
            var2.remove(var1);
         } catch (DbException var6) {
            this.trace.error(var6, "could not remove index");
            throw var6;
         }

         throw var7;
      }
   }

   private void rebuildIndexBlockMerge(SessionLocal var1, MVIndex<?, ?> var2) {
      Index var3 = this.getScanIndex(var1);
      long var4 = var3.getRowCount(var1);
      long var6 = var4;
      Cursor var8 = var3.find(var1, (SearchRow)null, (SearchRow)null);
      long var9 = 0L;
      Store var11 = var1.getDatabase().getStore();
      int var12 = this.database.getMaxMemoryRows() / 2;
      ArrayList var13 = new ArrayList(var12);
      String var14 = this.getName() + ':' + var2.getName();

      ArrayList var15;
      for(var15 = Utils.newSmallArrayList(); var8.next(); --var4) {
         Row var16 = var8.get();
         var13.add(var16);
         this.database.setProgress(1, var14, var9++, var6);
         if (var13.size() >= var12) {
            sortRows(var13, var2);
            String var17 = var11.nextTemporaryMapName();
            var2.addRowsToBuffer(var13, var17);
            var15.add(var17);
            var13.clear();
         }
      }

      sortRows(var13, var2);
      if (!var15.isEmpty()) {
         String var18 = var11.nextTemporaryMapName();
         var2.addRowsToBuffer(var13, var18);
         var15.add(var18);
         var13.clear();
         var2.addBufferedRows(var15);
      } else {
         addRowsToIndex(var1, var13, var2);
      }

      if (var4 != 0L) {
         throw DbException.getInternalError("rowcount remaining=" + var4 + ' ' + this.getName());
      }
   }

   private void rebuildIndexBuffered(SessionLocal var1, Index var2) {
      Index var3 = this.getScanIndex(var1);
      long var4 = var3.getRowCount(var1);
      long var6 = var4;
      Cursor var8 = var3.find(var1, (SearchRow)null, (SearchRow)null);
      long var9 = 0L;
      int var11 = (int)Math.min(var4, (long)this.database.getMaxMemoryRows());
      ArrayList var12 = new ArrayList(var11);

      for(String var13 = this.getName() + ':' + var2.getName(); var8.next(); --var4) {
         Row var14 = var8.get();
         var12.add(var14);
         this.database.setProgress(1, var13, var9++, var6);
         if (var12.size() >= var11) {
            addRowsToIndex(var1, var12, var2);
         }
      }

      addRowsToIndex(var1, var12, var2);
      if (var4 != 0L) {
         throw DbException.getInternalError("rowcount remaining=" + var4 + ' ' + this.getName());
      }
   }

   public void removeRow(SessionLocal var1, Row var2) {
      this.syncLastModificationIdWithDatabase();
      Transaction var3 = var1.getTransaction();
      long var4 = var3.setSavepoint();

      try {
         for(int var6 = this.indexes.size() - 1; var6 >= 0; --var6) {
            Index var7 = (Index)this.indexes.get(var6);
            var7.remove(var1, var2);
         }
      } catch (Throwable var9) {
         try {
            var3.rollbackToSavepoint(var4);
         } catch (Throwable var8) {
            var9.addSuppressed(var8);
         }

         throw DbException.convert(var9);
      }

      this.analyzeIfRequired(var1);
   }

   public long truncate(SessionLocal var1) {
      this.syncLastModificationIdWithDatabase();
      long var2 = this.getRowCountApproximation(var1);

      for(int var4 = this.indexes.size() - 1; var4 >= 0; --var4) {
         Index var5 = (Index)this.indexes.get(var4);
         var5.truncate(var1);
      }

      if (this.changesUntilAnalyze != null) {
         this.changesUntilAnalyze.set(this.nextAnalyze);
      }

      return var2;
   }

   public void addRow(SessionLocal var1, Row var2) {
      this.syncLastModificationIdWithDatabase();
      Transaction var3 = var1.getTransaction();
      long var4 = var3.setSavepoint();

      try {
         Iterator var6 = this.indexes.iterator();

         while(var6.hasNext()) {
            Index var7 = (Index)var6.next();
            var7.add(var1, var2);
         }
      } catch (Throwable var9) {
         try {
            var3.rollbackToSavepoint(var4);
         } catch (Throwable var8) {
            var9.addSuppressed(var8);
         }

         throw DbException.convert(var9);
      }

      this.analyzeIfRequired(var1);
   }

   public void updateRow(SessionLocal var1, Row var2, Row var3) {
      var3.setKey(var2.getKey());
      this.syncLastModificationIdWithDatabase();
      Transaction var4 = var1.getTransaction();
      long var5 = var4.setSavepoint();

      try {
         Iterator var7 = this.indexes.iterator();

         while(var7.hasNext()) {
            Index var8 = (Index)var7.next();
            var8.update(var1, var2, var3);
         }
      } catch (Throwable var10) {
         try {
            var4.rollbackToSavepoint(var5);
         } catch (Throwable var9) {
            var10.addSuppressed(var9);
         }

         throw DbException.convert(var10);
      }

      this.analyzeIfRequired(var1);
   }

   public Row lockRow(SessionLocal var1, Row var2) {
      Row var3 = this.primaryIndex.lockRow(var1, var2);
      if (var3 == null || !var2.hasSharedData(var3)) {
         this.syncLastModificationIdWithDatabase();
      }

      return var3;
   }

   private void analyzeIfRequired(SessionLocal var1) {
      if (this.changesUntilAnalyze != null && this.changesUntilAnalyze.decrementAndGet() == 0) {
         if (this.nextAnalyze <= 1073741823) {
            this.nextAnalyze *= 2;
         }

         this.changesUntilAnalyze.set(this.nextAnalyze);
         var1.markTableForAnalyze(this);
      }

   }

   public Index getScanIndex(SessionLocal var1) {
      return this.primaryIndex;
   }

   public ArrayList<Index> getIndexes() {
      return this.indexes;
   }

   public long getMaxDataModificationId() {
      return this.lastModificationId.get();
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      if (this.containsLargeObject) {
         this.truncate(var1);
         this.database.getLobStorage().removeAllForTable(this.getId());
         this.database.lockMeta(var1);
      }

      this.database.getStore().removeTable(this);
      super.removeChildrenAndResources(var1);

      Index var2;
      for(; this.indexes.size() > 1; this.indexes.remove(var2)) {
         var2 = (Index)this.indexes.get(1);
         var2.remove(var1);
         if (var2.getName() != null) {
            this.database.removeSchemaObject(var1, var2);
         }
      }

      this.primaryIndex.remove(var1);
      this.indexes.clear();
      this.close(var1);
      this.invalidate();
   }

   public long getRowCount(SessionLocal var1) {
      return this.primaryIndex.getRowCount(var1);
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return this.primaryIndex.getRowCountApproximation(var1);
   }

   public long getDiskSpaceUsed() {
      return this.primaryIndex.getDiskSpaceUsed();
   }

   Transaction getTransactionBegin() {
      return this.transactionStore.begin();
   }

   public boolean isRowLockable() {
      return true;
   }

   public void commit() {
      if (this.database != null) {
         this.syncLastModificationIdWithDatabase();
      }

   }

   private void syncLastModificationIdWithDatabase() {
      long var1 = this.database.getNextModificationDataId();

      long var3;
      do {
         var3 = this.lastModificationId.get();
      } while(var1 > var3 && !this.lastModificationId.compareAndSet(var3, var1));

   }

   DbException convertException(MVStoreException var1) {
      int var2 = var1.getErrorCode();
      if (var2 == 101) {
         throw DbException.get(90131, var1, this.getName());
      } else if (var2 == 105) {
         throw DbException.get(40001, var1, this.getName());
      } else {
         return this.store.convertMVStoreException(var1);
      }
   }

   public int getMainIndexColumn() {
      return this.primaryIndex.getMainIndexColumn();
   }

   private static void addRowsToIndex(SessionLocal var0, ArrayList<Row> var1, Index var2) {
      sortRows(var1, var2);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Row var4 = (Row)var3.next();
         var2.add(var0, var4);
      }

      var1.clear();
   }

   private static String getDeadlockDetails(ArrayList<SessionLocal> var0, int var1) {
      StringBuilder var2 = new StringBuilder();
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         SessionLocal var4 = (SessionLocal)var3.next();
         Table var5 = var4.getWaitForLock();
         Thread var6 = var4.getWaitForLockThread();
         var2.append("\nSession ").append(var4).append(" on thread ").append(var6.getName()).append(" is waiting to lock ").append(var5.toString()).append(" (").append(lockTypeToString(var1)).append(") while locking ");
         boolean var7 = false;
         Iterator var8 = var4.getLocks().iterator();

         while(var8.hasNext()) {
            Table var9 = (Table)var8.next();
            if (var7) {
               var2.append(", ");
            }

            var7 = true;
            var2.append(var9.toString());
            if (var9 instanceof MVTable) {
               if (((MVTable)var9).lockExclusiveSession == var4) {
                  var2.append(" (exclusive)");
               } else {
                  var2.append(" (shared)");
               }
            }
         }

         var2.append('.');
      }

      return var2.toString();
   }

   private static String lockTypeToString(int var0) {
      return var0 == 0 ? "shared read" : (var0 == 1 ? "shared write" : "exclusive");
   }

   private static void sortRows(ArrayList<? extends SearchRow> var0, Index var1) {
      var0.sort(var1::compareRows);
   }

   public boolean canDrop() {
      return true;
   }

   public boolean canGetRowCount(SessionLocal var1) {
      return true;
   }

   public boolean canTruncate() {
      if (this.getCheckForeignKeyConstraints() && this.database.getReferentialIntegrity()) {
         ArrayList var1 = this.getConstraints();
         if (var1 != null) {
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               Constraint var3 = (Constraint)var2.next();
               if (var3.getConstraintType() == Constraint.Type.REFERENTIAL) {
                  ConstraintReferential var4 = (ConstraintReferential)var3;
                  if (var4.getRefTable() == this) {
                     return false;
                  }
               }
            }
         }
      }

      return true;
   }

   public ArrayList<SessionLocal> checkDeadlock(SessionLocal var1, SessionLocal var2, Set<SessionLocal> var3) {
      synchronized(this.getClass()) {
         if (var2 == null) {
            var2 = var1;
            var3 = new HashSet();
         } else {
            if (var2 == var1) {
               return new ArrayList(0);
            }

            if (((Set)var3).contains(var1)) {
               return null;
            }
         }

         ((Set)var3).add(var1);
         ArrayList var5 = null;
         Iterator var6 = this.lockSharedSessions.keySet().iterator();

         while(var6.hasNext()) {
            SessionLocal var7 = (SessionLocal)var6.next();
            if (var7 != var1) {
               Table var8 = var7.getWaitForLock();
               if (var8 != null) {
                  var5 = var8.checkDeadlock(var7, var2, (Set)var3);
                  if (var5 != null) {
                     var5.add(var1);
                     break;
                  }
               }
            }
         }

         SessionLocal var11 = this.lockExclusiveSession;
         if (var5 == null && var11 != null) {
            Table var12 = var11.getWaitForLock();
            if (var12 != null) {
               var5 = var12.checkDeadlock(var11, var2, (Set)var3);
               if (var5 != null) {
                  var5.add(var1);
               }
            }
         }

         return var5;
      }
   }

   public void checkSupportAlter() {
   }

   public boolean getContainsLargeObject() {
      return this.containsLargeObject;
   }

   public Column getRowIdColumn() {
      if (this.rowIdColumn == null) {
         this.rowIdColumn = new Column("_ROWID_", TypeInfo.TYPE_BIGINT, this, -1);
         this.rowIdColumn.setRowId(true);
         this.rowIdColumn.setNullable(false);
      }

      return this.rowIdColumn;
   }

   public TableType getTableType() {
      return TableType.TABLE;
   }

   public boolean isDeterministic() {
      return true;
   }

   public boolean isLockedExclusively() {
      return this.lockExclusiveSession != null;
   }

   public boolean isLockedExclusivelyBy(SessionLocal var1) {
      return this.lockExclusiveSession == var1;
   }

   protected void invalidate() {
      super.invalidate();
      this.lockExclusiveSession = null;
   }

   public String toString() {
      return this.getTraceSQL();
   }

   private static IndexColumn[] prepareColumns(Database var0, IndexColumn[] var1, IndexType var2) {
      int var4;
      IndexColumn var6;
      if (var2.isPrimaryKey()) {
         IndexColumn[] var3 = var1;
         var4 = var1.length;

         int var5;
         for(var5 = 0; var5 < var4; ++var5) {
            var6 = var3[var5];
            Column var7 = var6.column;
            if (var7.isNullable()) {
               throw DbException.get(90023, var7.getName());
            }
         }

         var3 = var1;
         var4 = var1.length;

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = var3[var5];
            var6.column.setPrimaryKey(true);
         }
      } else if (!var2.isSpatial()) {
         int var10 = 0;

         for(var4 = var1.length; var10 < var4 && (var1[var10].sortType & 6) != 0; ++var10) {
         }

         if (var10 != var4) {
            var1 = (IndexColumn[])var1.clone();

            for(DefaultNullOrdering var11 = var0.getDefaultNullOrdering(); var10 < var4; ++var10) {
               var6 = var1[var10];
               int var12 = var6.sortType;
               int var8 = var11.addExplicitNullOrdering(var12);
               if (var8 != var12) {
                  IndexColumn var9 = new IndexColumn(var6.columnName, var8);
                  var9.column = var6.column;
                  var1[var10] = var9;
               }
            }
         }
      }

      return var1;
   }

   static {
      if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
         WAITING_FOR_LOCK = new DebuggingThreadLocal();
         EXCLUSIVE_LOCKS = new DebuggingThreadLocal();
         SHARED_LOCKS = new DebuggingThreadLocal();
      } else {
         WAITING_FOR_LOCK = null;
         EXCLUSIVE_LOCKS = null;
         SHARED_LOCKS = null;
      }

   }

   private static enum TraceLockEvent {
      TRACE_LOCK_OK("ok"),
      TRACE_LOCK_WAITING_FOR("waiting for"),
      TRACE_LOCK_REQUESTING_FOR("requesting for"),
      TRACE_LOCK_TIMEOUT_AFTER("timeout after "),
      TRACE_LOCK_UNLOCK("unlock"),
      TRACE_LOCK_ADDED_FOR("added for"),
      TRACE_LOCK_ADD_UPGRADED_FOR("add (upgraded) for ");

      private final String eventText;

      private TraceLockEvent(String var3) {
         this.eventText = var3;
      }

      public String getEventText() {
         return this.eventText;
      }
   }
}
