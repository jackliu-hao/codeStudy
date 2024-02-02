package org.h2.engine;

import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.h2.api.JavaObjectSerializer;
import org.h2.command.Command;
import org.h2.command.CommandInterface;
import org.h2.command.Parser;
import org.h2.command.Prepared;
import org.h2.command.ddl.Analyze;
import org.h2.constraint.Constraint;
import org.h2.index.Index;
import org.h2.index.ViewIndex;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.meta.DatabaseMeta;
import org.h2.jdbc.meta.DatabaseMetaLocal;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceSystem;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.db.MVIndex;
import org.h2.mvstore.db.MVTable;
import org.h2.mvstore.db.Store;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionStore;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.store.DataHandler;
import org.h2.store.InDoubtTransaction;
import org.h2.table.Table;
import org.h2.util.DateTimeUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.SmallLRUCache;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.value.Value;
import org.h2.value.ValueLob;
import org.h2.value.ValueNull;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueVarchar;
import org.h2.value.VersionedValue;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataInMemory;

public final class SessionLocal extends Session implements TransactionStore.RollbackListener {
   private static final String SYSTEM_IDENTIFIER_PREFIX = "_";
   private static int nextSerialId;
   private static final ThreadLocal<Session> THREAD_LOCAL_SESSION = new ThreadLocal();
   private final int serialId;
   private final Database database;
   private final User user;
   private final int id;
   private NetworkConnectionInfo networkConnectionInfo;
   private final ArrayList<Table> locks;
   private boolean autoCommit;
   private Random random;
   private int lockTimeout;
   private HashMap<SequenceAndPrepared, RowNumberAndValue> nextValueFor;
   private WeakHashMap<Sequence, Value> currentValueFor;
   private Value lastIdentity;
   private HashMap<String, Savepoint> savepoints;
   private HashMap<String, Table> localTempTables;
   private HashMap<String, Index> localTempTableIndexes;
   private HashMap<String, Constraint> localTempTableConstraints;
   private int throttleMs;
   private long lastThrottleNs;
   private Command currentCommand;
   private boolean allowLiterals;
   private String currentSchemaName;
   private String[] schemaSearchPath;
   private Trace trace;
   private HashMap<String, ValueLob> removeLobMap;
   private int systemIdentifier;
   private HashMap<String, Procedure> procedures;
   private boolean autoCommitAtTransactionEnd;
   private String currentTransactionName;
   private volatile long cancelAtNs;
   private final ValueTimestampTimeZone sessionStart;
   private Instant commandStartOrEnd;
   private ValueTimestampTimeZone currentTimestamp;
   private HashMap<String, Value> variables;
   private int queryTimeout;
   private boolean commitOrRollbackDisabled;
   private Table waitForLock;
   private Thread waitForLockThread;
   private int modificationId;
   private int objectId;
   private final int queryCacheSize;
   private SmallLRUCache<String, Command> queryCache;
   private long modificationMetaID;
   private int createViewLevel;
   private volatile SmallLRUCache<Object, ViewIndex> viewIndexCache;
   private HashMap<Object, ViewIndex> subQueryIndexCache;
   private boolean lazyQueryExecution;
   private BitSet nonKeywords;
   private TimeZoneProvider timeZone;
   private HashSet<Table> tablesToAnalyze;
   private LinkedList<TimeoutValue> temporaryResultLobs;
   private ArrayList<ValueLob> temporaryLobs;
   private Transaction transaction;
   private final AtomicReference<State> state;
   private long startStatement;
   private IsolationLevel isolationLevel;
   private long snapshotDataModificationId;
   private BitSet idsToRelease;
   private boolean truncateLargeLength;
   private boolean variableBinary;
   private boolean oldInformationSchema;
   private boolean quirksMode;

   static Session getThreadLocalSession() {
      Session var0 = (Session)THREAD_LOCAL_SESSION.get();
      if (var0 == null) {
         THREAD_LOCAL_SESSION.remove();
      }

      return var0;
   }

   public SessionLocal(Database var1, User var2, int var3) {
      this.serialId = nextSerialId++;
      this.locks = Utils.newSmallArrayList();
      this.autoCommit = true;
      this.lastIdentity = ValueNull.INSTANCE;
      this.modificationMetaID = -1L;
      this.state = new AtomicReference(SessionLocal.State.INIT);
      this.startStatement = -1L;
      this.isolationLevel = IsolationLevel.READ_COMMITTED;
      this.database = var1;
      this.queryTimeout = var1.getSettings().maxQueryTimeout;
      this.queryCacheSize = var1.getSettings().queryCacheSize;
      this.user = var2;
      this.id = var3;
      this.lockTimeout = var1.getLockTimeout();
      Schema var4 = var1.getMainSchema();
      this.currentSchemaName = var4 != null ? var4.getName() : var1.sysIdentifier("PUBLIC");
      this.timeZone = DateTimeUtils.getTimeZone();
      this.sessionStart = DateTimeUtils.currentTimestamp(this.timeZone, this.commandStartOrEnd = Instant.now());
   }

   public void setLazyQueryExecution(boolean var1) {
      this.lazyQueryExecution = var1;
   }

   public boolean isLazyQueryExecution() {
      return this.lazyQueryExecution;
   }

   public void setParsingCreateView(boolean var1) {
      this.createViewLevel += var1 ? 1 : -1;
   }

   public boolean isParsingCreateView() {
      return this.createViewLevel != 0;
   }

   public ArrayList<String> getClusterServers() {
      return new ArrayList();
   }

   public boolean setCommitOrRollbackDisabled(boolean var1) {
      boolean var2 = this.commitOrRollbackDisabled;
      this.commitOrRollbackDisabled = var1;
      return var2;
   }

   private void initVariables() {
      if (this.variables == null) {
         this.variables = this.database.newStringMap();
      }

   }

   public void setVariable(String var1, Value var2) {
      this.initVariables();
      ++this.modificationId;
      Value var3;
      if (var2 == ValueNull.INSTANCE) {
         var3 = (Value)this.variables.remove(var1);
      } else {
         if (var2 instanceof ValueLob) {
            var2 = ((ValueLob)var2).copy(this.database, -1);
         }

         var3 = (Value)this.variables.put(var1, var2);
      }

      if (var3 instanceof ValueLob) {
         ((ValueLob)var3).remove();
      }

   }

   public Value getVariable(String var1) {
      this.initVariables();
      Value var2 = (Value)this.variables.get(var1);
      return (Value)(var2 == null ? ValueNull.INSTANCE : var2);
   }

   public String[] getVariableNames() {
      return this.variables == null ? new String[0] : (String[])this.variables.keySet().toArray(new String[0]);
   }

   public Table findLocalTempTable(String var1) {
      return this.localTempTables == null ? null : (Table)this.localTempTables.get(var1);
   }

   public List<Table> getLocalTempTables() {
      return (List)(this.localTempTables == null ? Collections.emptyList() : new ArrayList(this.localTempTables.values()));
   }

   public void addLocalTempTable(Table var1) {
      if (this.localTempTables == null) {
         this.localTempTables = this.database.newStringMap();
      }

      if (this.localTempTables.putIfAbsent(var1.getName(), var1) != null) {
         StringBuilder var2 = new StringBuilder();
         var1.getSQL(var2, 3).append(" AS ");
         Parser.quoteIdentifier(var1.getName(), 3);
         throw DbException.get(42101, (String)var2.toString());
      } else {
         ++this.modificationId;
      }
   }

   public void removeLocalTempTable(Table var1) {
      ++this.modificationId;
      if (this.localTempTables != null) {
         this.localTempTables.remove(var1.getName());
      }

      synchronized(this.database) {
         var1.removeChildrenAndResources(this);
      }
   }

   public Index findLocalTempTableIndex(String var1) {
      return this.localTempTableIndexes == null ? null : (Index)this.localTempTableIndexes.get(var1);
   }

   public HashMap<String, Index> getLocalTempTableIndexes() {
      return this.localTempTableIndexes == null ? new HashMap() : this.localTempTableIndexes;
   }

   public void addLocalTempTableIndex(Index var1) {
      if (this.localTempTableIndexes == null) {
         this.localTempTableIndexes = this.database.newStringMap();
      }

      if (this.localTempTableIndexes.putIfAbsent(var1.getName(), var1) != null) {
         throw DbException.get(42111, (String)var1.getTraceSQL());
      }
   }

   public void removeLocalTempTableIndex(Index var1) {
      if (this.localTempTableIndexes != null) {
         this.localTempTableIndexes.remove(var1.getName());
         synchronized(this.database) {
            var1.removeChildrenAndResources(this);
         }
      }

   }

   public Constraint findLocalTempTableConstraint(String var1) {
      return this.localTempTableConstraints == null ? null : (Constraint)this.localTempTableConstraints.get(var1);
   }

   public HashMap<String, Constraint> getLocalTempTableConstraints() {
      return this.localTempTableConstraints == null ? new HashMap() : this.localTempTableConstraints;
   }

   public void addLocalTempTableConstraint(Constraint var1) {
      if (this.localTempTableConstraints == null) {
         this.localTempTableConstraints = this.database.newStringMap();
      }

      String var2 = var1.getName();
      if (this.localTempTableConstraints.putIfAbsent(var2, var1) != null) {
         throw DbException.get(90045, var1.getTraceSQL());
      }
   }

   void removeLocalTempTableConstraint(Constraint var1) {
      if (this.localTempTableConstraints != null) {
         this.localTempTableConstraints.remove(var1.getName());
         synchronized(this.database) {
            var1.removeChildrenAndResources(this);
         }
      }

   }

   public boolean getAutoCommit() {
      return this.autoCommit;
   }

   public User getUser() {
      return this.user;
   }

   public void setAutoCommit(boolean var1) {
      this.autoCommit = var1;
   }

   public int getLockTimeout() {
      return this.lockTimeout;
   }

   public void setLockTimeout(int var1) {
      this.lockTimeout = var1;
      if (this.hasTransaction()) {
         this.transaction.setTimeoutMillis(var1);
      }

   }

   public synchronized CommandInterface prepareCommand(String var1, int var2) {
      return this.prepareLocal(var1);
   }

   public Prepared prepare(String var1) {
      return this.prepare(var1, false, false);
   }

   public Prepared prepare(String var1, boolean var2, boolean var3) {
      Parser var4 = new Parser(this);
      var4.setRightsChecked(var2);
      var4.setLiteralsChecked(var3);
      return var4.prepare(var1);
   }

   public Command prepareLocal(String var1) {
      if (this.isClosed()) {
         throw DbException.get(90067, "session closed");
      } else {
         Command var2;
         if (this.queryCacheSize > 0) {
            if (this.queryCache == null) {
               this.queryCache = SmallLRUCache.newInstance(this.queryCacheSize);
               this.modificationMetaID = this.database.getModificationMetaId();
            } else {
               long var3 = this.database.getModificationMetaId();
               if (var3 != this.modificationMetaID) {
                  this.queryCache.clear();
                  this.modificationMetaID = var3;
               }

               var2 = (Command)this.queryCache.get(var1);
               if (var2 != null && var2.canReuse()) {
                  var2.reuse();
                  return var2;
               }
            }
         }

         Parser var8 = new Parser(this);

         try {
            var2 = var8.prepareCommand(var1);
         } finally {
            this.subQueryIndexCache = null;
         }

         if (this.queryCache != null && var2.isCacheable()) {
            this.queryCache.put(var1, var2);
         }

         return var2;
      }
   }

   protected void scheduleDatabaseObjectIdForRelease(int var1) {
      if (this.idsToRelease == null) {
         this.idsToRelease = new BitSet();
      }

      this.idsToRelease.set(var1);
   }

   public Database getDatabase() {
      return this.database;
   }

   public void commit(boolean var1) {
      this.beforeCommitOrRollback();
      if (this.hasTransaction()) {
         try {
            this.markUsedTablesAsUpdated();
            this.transaction.commit();
            this.removeTemporaryLobs(true);
            this.endTransaction();
         } finally {
            this.transaction = null;
         }

         if (!var1) {
            this.cleanTempTables(false);
            if (this.autoCommitAtTransactionEnd) {
               this.autoCommit = true;
               this.autoCommitAtTransactionEnd = false;
            }
         }

         this.analyzeTables();
      }

   }

   private void markUsedTablesAsUpdated() {
      if (!this.locks.isEmpty()) {
         Iterator var1 = this.locks.iterator();

         while(var1.hasNext()) {
            Table var2 = (Table)var1.next();
            if (var2 instanceof MVTable) {
               ((MVTable)var2).commit();
            }
         }
      }

   }

   private void analyzeTables() {
      if (this.tablesToAnalyze != null && Thread.holdsLock(this)) {
         HashSet var1 = this.tablesToAnalyze;
         this.tablesToAnalyze = null;
         int var2 = this.getDatabase().getSettings().analyzeSample / 10;
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Table var4 = (Table)var3.next();
            Analyze.analyzeTable(this, var4, var2, false);
         }

         this.database.unlockMeta(this);
         this.commit(true);
      }

   }

   private void removeTemporaryLobs(boolean var1) {
      if (this.temporaryLobs != null) {
         Iterator var2 = this.temporaryLobs.iterator();

         while(var2.hasNext()) {
            ValueLob var3 = (ValueLob)var2.next();
            if (!var3.isLinkedToTable()) {
               var3.remove();
            }
         }

         this.temporaryLobs.clear();
      }

      if (this.temporaryResultLobs != null && !this.temporaryResultLobs.isEmpty()) {
         long var6 = System.nanoTime() - (long)this.database.getSettings().lobTimeout * 1000000L;

         while(!this.temporaryResultLobs.isEmpty()) {
            TimeoutValue var4 = (TimeoutValue)this.temporaryResultLobs.getFirst();
            if (var1 && var4.created - var6 >= 0L) {
               break;
            }

            ValueLob var5 = ((TimeoutValue)this.temporaryResultLobs.removeFirst()).value;
            if (!var5.isLinkedToTable()) {
               var5.remove();
            }
         }
      }

   }

   private void beforeCommitOrRollback() {
      if (this.commitOrRollbackDisabled && !this.locks.isEmpty()) {
         throw DbException.get(90058);
      } else {
         this.currentTransactionName = null;
         this.currentTimestamp = null;
         this.database.throwLastBackgroundException();
      }
   }

   private void endTransaction() {
      if (this.removeLobMap != null && !this.removeLobMap.isEmpty()) {
         Iterator var1 = this.removeLobMap.values().iterator();

         while(var1.hasNext()) {
            ValueLob var2 = (ValueLob)var1.next();
            var2.remove();
         }

         this.removeLobMap = null;
      }

      this.unlockAll();
      if (this.idsToRelease != null) {
         this.database.releaseDatabaseObjectIds(this.idsToRelease);
         this.idsToRelease = null;
      }

      if (this.hasTransaction() && !this.transaction.allowNonRepeatableRead()) {
         this.snapshotDataModificationId = this.database.getNextModificationDataId();
      }

   }

   public long getSnapshotDataModificationId() {
      return this.snapshotDataModificationId;
   }

   public void rollback() {
      this.beforeCommitOrRollback();
      if (this.hasTransaction()) {
         this.rollbackTo((Savepoint)null);
      }

      this.idsToRelease = null;
      this.cleanTempTables(false);
      if (this.autoCommitAtTransactionEnd) {
         this.autoCommit = true;
         this.autoCommitAtTransactionEnd = false;
      }

      this.endTransaction();
   }

   public void rollbackTo(Savepoint var1) {
      int var2 = var1 == null ? 0 : var1.logIndex;
      if (this.hasTransaction()) {
         this.markUsedTablesAsUpdated();
         if (var1 == null) {
            this.transaction.rollback();
            this.transaction = null;
         } else {
            this.transaction.rollbackToSavepoint(var1.transactionSavepoint);
         }
      }

      if (this.savepoints != null) {
         String[] var3 = (String[])this.savepoints.keySet().toArray(new String[0]);
         String[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            Savepoint var8 = (Savepoint)this.savepoints.get(var7);
            int var9 = var8.logIndex;
            if (var9 > var2) {
               this.savepoints.remove(var7);
            }
         }
      }

      if (this.queryCache != null) {
         this.queryCache.clear();
      }

   }

   public boolean hasPendingTransaction() {
      return this.hasTransaction() && this.transaction.hasChanges() && this.transaction.getStatus() != 2;
   }

   public Savepoint setSavepoint() {
      Savepoint var1 = new Savepoint();
      var1.transactionSavepoint = this.getStatementSavepoint();
      return var1;
   }

   public int getId() {
      return this.id;
   }

   public void cancel() {
      this.cancelAtNs = Utils.currentNanoTime();
   }

   void suspend() {
      this.cancel();
      if (this.transitionToState(SessionLocal.State.SUSPENDED, false) == SessionLocal.State.SLEEP) {
         this.close();
      }

   }

   public void close() {
      if (this.state.getAndSet(SessionLocal.State.CLOSED) != SessionLocal.State.CLOSED) {
         try {
            this.database.throwLastBackgroundException();
            this.database.checkPowerOff();
            if (this.hasPreparedTransaction()) {
               if (this.currentTransactionName != null) {
                  this.removeLobMap = null;
               }

               this.endTransaction();
            } else {
               this.rollback();
               this.removeTemporaryLobs(false);
               this.cleanTempTables(true);
               this.commit(true);
            }

            this.database.unlockMeta(this);
         } finally {
            this.database.removeSession(this);
         }
      }

   }

   public void registerTableAsLocked(Table var1) {
      if (SysProperties.CHECK && this.locks.contains(var1)) {
         throw DbException.getInternalError(var1.toString());
      } else {
         this.locks.add(var1);
      }
   }

   public void registerTableAsUpdated(Table var1) {
      if (!this.locks.contains(var1)) {
         this.locks.add(var1);
      }

   }

   void unlock(Table var1) {
      this.locks.remove(var1);
   }

   private boolean hasTransaction() {
      return this.transaction != null;
   }

   private void unlockAll() {
      if (!this.locks.isEmpty()) {
         Table[] var1 = (Table[])this.locks.toArray(new Table[0]);
         Table[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Table var5 = var2[var4];
            if (var5 != null) {
               var5.unlock(this);
            }
         }

         this.locks.clear();
      }

      Database.unlockMetaDebug(this);
      this.savepoints = null;
      this.sessionStateChanged = true;
   }

   private void cleanTempTables(boolean var1) {
      if (this.localTempTables != null && !this.localTempTables.isEmpty()) {
         Iterator var2 = this.localTempTables.values().iterator();

         while(true) {
            while(var2.hasNext()) {
               Table var3 = (Table)var2.next();
               if (!var1 && !var3.getOnCommitDrop()) {
                  if (var3.getOnCommitTruncate()) {
                     var3.truncate(this);
                  }
               } else {
                  ++this.modificationId;
                  var3.setModified();
                  var2.remove();
                  this.database.lockMeta(this);
                  var3.removeChildrenAndResources(this);
                  if (var1) {
                     this.database.throwLastBackgroundException();
                  }
               }
            }

            return;
         }
      }
   }

   public Random getRandom() {
      if (this.random == null) {
         this.random = new Random();
      }

      return this.random;
   }

   public Trace getTrace() {
      if (this.trace != null && !this.isClosed()) {
         return this.trace;
      } else {
         String var1 = "jdbc[" + this.id + "]";
         if (this.isClosed()) {
            return (new TraceSystem((String)null)).getTrace(var1);
         } else {
            this.trace = this.database.getTraceSystem().getTrace(var1);
            return this.trace;
         }
      }
   }

   public Value getNextValueFor(Sequence var1, Prepared var2) {
      Mode var4 = this.database.getMode();
      Value var3;
      if (!var4.nextValueReturnsDifferentValues && var2 != null) {
         if (this.nextValueFor == null) {
            this.nextValueFor = new HashMap();
         }

         SequenceAndPrepared var5 = new SequenceAndPrepared(var1, var2);
         RowNumberAndValue var6 = (RowNumberAndValue)this.nextValueFor.get(var5);
         long var7 = var2.getCurrentRowNumber();
         if (var6 != null) {
            if (var6.rowNumber == var7) {
               var3 = var6.nextValue;
            } else {
               var6.nextValue = var3 = var1.getNext(this);
               var6.rowNumber = var7;
            }
         } else {
            var3 = var1.getNext(this);
            this.nextValueFor.put(var5, new RowNumberAndValue(var7, var3));
         }
      } else {
         var3 = var1.getNext(this);
      }

      WeakHashMap var9 = this.currentValueFor;
      if (var9 == null) {
         this.currentValueFor = var9 = new WeakHashMap();
      }

      var9.put(var1, var3);
      if (var4.takeGeneratedSequenceValue) {
         this.lastIdentity = var3;
      }

      return var3;
   }

   public Value getCurrentValueFor(Sequence var1) {
      WeakHashMap var2 = this.currentValueFor;
      if (var2 != null) {
         Value var3 = (Value)var2.get(var1);
         if (var3 != null) {
            return var3;
         }
      }

      throw DbException.get(90148, var1.getTraceSQL());
   }

   public void setLastIdentity(Value var1) {
      this.lastIdentity = var1;
   }

   public Value getLastIdentity() {
      return this.lastIdentity;
   }

   public boolean containsUncommitted() {
      return this.transaction != null && this.transaction.hasChanges();
   }

   public void addSavepoint(String var1) {
      if (this.savepoints == null) {
         this.savepoints = this.database.newStringMap();
      }

      this.savepoints.put(var1, this.setSavepoint());
   }

   public void rollbackToSavepoint(String var1) {
      this.beforeCommitOrRollback();
      Savepoint var2;
      if (this.savepoints != null && (var2 = (Savepoint)this.savepoints.get(var1)) != null) {
         this.rollbackTo(var2);
      } else {
         throw DbException.get(90063, var1);
      }
   }

   public void prepareCommit(String var1) {
      if (this.hasPendingTransaction()) {
         this.database.prepareCommit(this, var1);
      }

      this.currentTransactionName = var1;
   }

   public boolean hasPreparedTransaction() {
      return this.currentTransactionName != null;
   }

   public void setPreparedTransaction(String var1, boolean var2) {
      if (this.hasPreparedTransaction() && this.currentTransactionName.equals(var1)) {
         if (var2) {
            this.commit(false);
         } else {
            this.rollback();
         }
      } else {
         ArrayList var3 = this.database.getInDoubtTransactions();
         int var4 = var2 ? 1 : 2;
         boolean var5 = false;
         Iterator var6 = var3.iterator();

         while(var6.hasNext()) {
            InDoubtTransaction var7 = (InDoubtTransaction)var6.next();
            if (var7.getTransactionName().equals(var1)) {
               var7.setState(var4);
               var5 = true;
               break;
            }
         }

         if (!var5) {
            throw DbException.get(90129, var1);
         }
      }

   }

   public boolean isClosed() {
      return this.state.get() == SessionLocal.State.CLOSED;
   }

   public boolean isOpen() {
      State var1 = (State)this.state.get();
      this.checkSuspended(var1);
      return var1 != SessionLocal.State.CLOSED;
   }

   public void setThrottle(int var1) {
      this.throttleMs = var1;
   }

   public void throttle() {
      if (this.throttleMs != 0) {
         long var1 = System.nanoTime();
         if (this.lastThrottleNs == 0L || var1 - this.lastThrottleNs >= 50000000L) {
            this.lastThrottleNs = Utils.nanoTimePlusMillis(var1, this.throttleMs);
            State var3 = this.transitionToState(SessionLocal.State.THROTTLED, false);

            try {
               Thread.sleep((long)this.throttleMs);
            } catch (InterruptedException var8) {
            } finally {
               this.transitionToState(var3, false);
            }

         }
      }
   }

   private void setCurrentCommand(Command var1) {
      State var2 = var1 == null ? SessionLocal.State.SLEEP : SessionLocal.State.RUNNING;
      this.transitionToState(var2, true);
      if (this.isOpen()) {
         this.currentCommand = var1;
         this.commandStartOrEnd = Instant.now();
         if (var1 != null) {
            if (this.queryTimeout > 0) {
               this.cancelAtNs = Utils.currentNanoTimePlusMillis(this.queryTimeout);
            }
         } else {
            if (this.currentTimestamp != null && !this.database.getMode().dateTimeValueWithinTransaction) {
               this.currentTimestamp = null;
            }

            if (this.nextValueFor != null) {
               this.nextValueFor.clear();
            }
         }
      }

   }

   private State transitionToState(State var1, boolean var2) {
      State var3;
      while((var3 = (State)this.state.get()) != SessionLocal.State.CLOSED && (!var2 || this.checkSuspended(var3)) && !this.state.compareAndSet(var3, var1)) {
      }

      return var3;
   }

   private boolean checkSuspended(State var1) {
      if (var1 == SessionLocal.State.SUSPENDED) {
         this.close();
         throw DbException.get(90135);
      } else {
         return true;
      }
   }

   public void checkCanceled() {
      this.throttle();
      long var1 = this.cancelAtNs;
      if (var1 != 0L) {
         if (System.nanoTime() - var1 >= 0L) {
            this.cancelAtNs = 0L;
            throw DbException.get(57014);
         }
      }
   }

   public long getCancel() {
      return this.cancelAtNs;
   }

   public Command getCurrentCommand() {
      return this.currentCommand;
   }

   public ValueTimestampTimeZone getCommandStartOrEnd() {
      return DateTimeUtils.currentTimestamp(this.timeZone, this.commandStartOrEnd);
   }

   public boolean getAllowLiterals() {
      return this.allowLiterals;
   }

   public void setAllowLiterals(boolean var1) {
      this.allowLiterals = var1;
   }

   public void setCurrentSchema(Schema var1) {
      ++this.modificationId;
      if (this.queryCache != null) {
         this.queryCache.clear();
      }

      this.currentSchemaName = var1.getName();
   }

   public String getCurrentSchemaName() {
      return this.currentSchemaName;
   }

   public void setCurrentSchemaName(String var1) {
      Schema var2 = this.database.getSchema(var1);
      this.setCurrentSchema(var2);
   }

   public JdbcConnection createConnection(boolean var1) {
      String var2;
      if (var1) {
         var2 = "jdbc:columnlist:connection";
      } else {
         var2 = "jdbc:default:connection";
      }

      return new JdbcConnection(this, this.getUser().getName(), var2);
   }

   public DataHandler getDataHandler() {
      return this.database;
   }

   public void removeAtCommit(ValueLob var1) {
      if (var1.isLinkedToTable()) {
         if (this.removeLobMap == null) {
            this.removeLobMap = new HashMap();
         }

         this.removeLobMap.put(var1.toString(), var1);
      }

   }

   public void removeAtCommitStop(ValueLob var1) {
      if (var1.isLinkedToTable() && this.removeLobMap != null) {
         this.removeLobMap.remove(var1.toString());
      }

   }

   public String getNextSystemIdentifier(String var1) {
      String var2;
      do {
         var2 = "_" + this.systemIdentifier++;
      } while(var1.contains(var2));

      return var2;
   }

   public void addProcedure(Procedure var1) {
      if (this.procedures == null) {
         this.procedures = this.database.newStringMap();
      }

      this.procedures.put(var1.getName(), var1);
   }

   public void removeProcedure(String var1) {
      if (this.procedures != null) {
         this.procedures.remove(var1);
      }

   }

   public Procedure getProcedure(String var1) {
      return this.procedures == null ? null : (Procedure)this.procedures.get(var1);
   }

   public void setSchemaSearchPath(String[] var1) {
      ++this.modificationId;
      this.schemaSearchPath = var1;
   }

   public String[] getSchemaSearchPath() {
      return this.schemaSearchPath;
   }

   public int hashCode() {
      return this.serialId;
   }

   public String toString() {
      return "#" + this.serialId + " (user: " + (this.user == null ? "<null>" : this.user.getName()) + ", " + this.state.get() + ")";
   }

   public void begin() {
      this.autoCommitAtTransactionEnd = true;
      this.autoCommit = false;
   }

   public ValueTimestampTimeZone getSessionStart() {
      return this.sessionStart;
   }

   public Set<Table> getLocks() {
      if (this.database.getLockMode() != 0 && !this.locks.isEmpty()) {
         Object[] var1 = this.locks.toArray();
         switch (var1.length) {
            case 1:
               Object var2 = var1[0];
               if (var2 != null) {
                  return Collections.singleton((Table)var2);
               }
            case 0:
               return Collections.emptySet();
            default:
               HashSet var7 = new HashSet();
               Object[] var3 = var1;
               int var4 = var1.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Object var6 = var3[var5];
                  if (var6 != null) {
                     var7.add((Table)var6);
                  }
               }

               return var7;
         }
      } else {
         return Collections.emptySet();
      }
   }

   public void waitIfExclusiveModeEnabled() {
      this.transitionToState(SessionLocal.State.RUNNING, true);
      if (this.database.getLobSession() != this) {
         while(this.isOpen()) {
            SessionLocal var1 = this.database.getExclusiveSession();
            if (var1 == null || var1 == this || Thread.holdsLock(var1)) {
               break;
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var3) {
            }
         }

      }
   }

   public Map<Object, ViewIndex> getViewIndexCache(boolean var1) {
      if (var1) {
         if (this.subQueryIndexCache == null) {
            this.subQueryIndexCache = new HashMap();
         }

         return this.subQueryIndexCache;
      } else {
         SmallLRUCache var2 = this.viewIndexCache;
         if (var2 == null) {
            this.viewIndexCache = var2 = SmallLRUCache.newInstance(64);
         }

         return var2;
      }
   }

   public void setQueryTimeout(int var1) {
      int var2 = this.database.getSettings().maxQueryTimeout;
      if (var2 != 0 && (var2 < var1 || var1 == 0)) {
         var1 = var2;
      }

      this.queryTimeout = var1;
      this.cancelAtNs = 0L;
   }

   public int getQueryTimeout() {
      return this.queryTimeout;
   }

   public void setWaitForLock(Table var1, Thread var2) {
      this.waitForLock = var1;
      this.waitForLockThread = var2;
   }

   public Table getWaitForLock() {
      return this.waitForLock;
   }

   public Thread getWaitForLockThread() {
      return this.waitForLockThread;
   }

   public int getModificationId() {
      return this.modificationId;
   }

   public Value getTransactionId() {
      return (Value)(this.transaction != null && this.transaction.hasChanges() ? ValueVarchar.get(Long.toString(this.transaction.getSequenceNum())) : ValueNull.INSTANCE);
   }

   public int nextObjectId() {
      return this.objectId++;
   }

   public Transaction getTransaction() {
      if (this.transaction == null) {
         Store var1 = this.database.getStore();
         if (var1.getMvStore().isClosed()) {
            Throwable var2 = this.database.getBackgroundException();
            this.database.shutdownImmediately();
            throw DbException.get(90098, var2);
         }

         this.transaction = var1.getTransactionStore().begin(this, this.lockTimeout, this.id, this.isolationLevel);
         this.startStatement = -1L;
      }

      return this.transaction;
   }

   private long getStatementSavepoint() {
      if (this.startStatement == -1L) {
         this.startStatement = this.getTransaction().setSavepoint();
      }

      return this.startStatement;
   }

   public void startStatementWithinTransaction(Command var1) {
      Transaction var2 = this.getTransaction();
      if (var2 != null) {
         HashSet var3 = new HashSet();
         if (var1 != null) {
            Set var4 = var1.getDependencies();
            Iterator var9;
            label57:
            switch (var2.getIsolationLevel()) {
               case SNAPSHOT:
               case SERIALIZABLE:
                  if (!var2.hasStatementDependencies()) {
                     var9 = this.database.getAllSchemasNoMeta().iterator();

                     while(true) {
                        if (!var9.hasNext()) {
                           break label57;
                        }

                        Schema var11 = (Schema)var9.next();
                        Iterator var12 = var11.getAllTablesAndViews((SessionLocal)null).iterator();

                        while(var12.hasNext()) {
                           Table var8 = (Table)var12.next();
                           if (var8 instanceof MVTable) {
                              addTableToDependencies((MVTable)var8, var3);
                           }
                        }
                     }
                  }
               case READ_COMMITTED:
               case READ_UNCOMMITTED:
                  var9 = var4.iterator();

                  while(true) {
                     if (!var9.hasNext()) {
                        break label57;
                     }

                     DbObject var10 = (DbObject)var9.next();
                     if (var10 instanceof MVTable) {
                        addTableToDependencies((MVTable)var10, var3);
                     }
                  }
               case REPEATABLE_READ:
                  HashSet var5 = new HashSet();
                  Iterator var6 = var4.iterator();

                  while(var6.hasNext()) {
                     DbObject var7 = (DbObject)var6.next();
                     if (var7 instanceof MVTable) {
                        addTableToDependencies((MVTable)var7, var3, var5);
                     }
                  }
            }
         }

         var2.markStatementStart(var3);
      }

      this.startStatement = -1L;
      if (var1 != null) {
         this.setCurrentCommand(var1);
      }

   }

   private static void addTableToDependencies(MVTable var0, HashSet<MVMap<Object, VersionedValue<Object>>> var1) {
      Iterator var2 = var0.getIndexes().iterator();

      while(var2.hasNext()) {
         Index var3 = (Index)var2.next();
         if (var3 instanceof MVIndex) {
            var1.add(((MVIndex)var3).getMVMap());
         }
      }

   }

   private static void addTableToDependencies(MVTable var0, HashSet<MVMap<Object, VersionedValue<Object>>> var1, HashSet<MVTable> var2) {
      if (var2.add(var0)) {
         addTableToDependencies(var0, var1);
         ArrayList var3 = var0.getConstraints();
         if (var3 != null) {
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               Constraint var5 = (Constraint)var4.next();
               Table var6 = var5.getTable();
               if (var6 != var0 && var6 instanceof MVTable) {
                  addTableToDependencies((MVTable)var6, var1, var2);
               }
            }
         }

      }
   }

   public void endStatement() {
      this.setCurrentCommand((Command)null);
      if (this.hasTransaction()) {
         this.transaction.markStatementEnd();
      }

      this.startStatement = -1L;
   }

   public void clearViewIndexCache() {
      this.viewIndexCache = null;
   }

   public ValueLob addTemporaryLob(ValueLob var1) {
      LobData var2 = var1.getLobData();
      if (var2 instanceof LobDataInMemory) {
         return var1;
      } else {
         int var3 = ((LobDataDatabase)var2).getTableId();
         if (var3 != -3 && var3 != -2) {
            if (this.temporaryLobs == null) {
               this.temporaryLobs = new ArrayList();
            }

            this.temporaryLobs.add(var1);
         } else {
            if (this.temporaryResultLobs == null) {
               this.temporaryResultLobs = new LinkedList();
            }

            this.temporaryResultLobs.add(new TimeoutValue(var1));
         }

         return var1;
      }
   }

   public boolean isRemote() {
      return false;
   }

   public void markTableForAnalyze(Table var1) {
      if (this.tablesToAnalyze == null) {
         this.tablesToAnalyze = new HashSet();
      }

      this.tablesToAnalyze.add(var1);
   }

   public State getState() {
      return this.getBlockingSessionId() != 0 ? SessionLocal.State.BLOCKED : (State)this.state.get();
   }

   public int getBlockingSessionId() {
      return this.transaction == null ? 0 : this.transaction.getBlockerId();
   }

   public void onRollback(MVMap<Object, VersionedValue<Object>> var1, Object var2, VersionedValue<Object> var3, VersionedValue<Object> var4) {
      Store var5 = this.database.getStore();
      MVTable var6 = var5.getTable(var1.getName());
      if (var6 != null) {
         Row var7 = var3 == null ? null : (Row)var3.getCurrentValue();
         Row var8 = var4 == null ? null : (Row)var4.getCurrentValue();
         var6.fireAfterRow(this, var7, var8, true);
         if (var6.getContainsLargeObject()) {
            int var9;
            int var10;
            Value var11;
            if (var7 != null) {
               var9 = 0;

               for(var10 = var7.getColumnCount(); var9 < var10; ++var9) {
                  var11 = var7.getValue(var9);
                  if (var11 instanceof ValueLob) {
                     this.removeAtCommit((ValueLob)var11);
                  }
               }
            }

            if (var8 != null) {
               var9 = 0;

               for(var10 = var8.getColumnCount(); var9 < var10; ++var9) {
                  var11 = var8.getValue(var9);
                  if (var11 instanceof ValueLob) {
                     this.removeAtCommitStop((ValueLob)var11);
                  }
               }
            }
         }
      }

   }

   public NetworkConnectionInfo getNetworkConnectionInfo() {
      return this.networkConnectionInfo;
   }

   public void setNetworkConnectionInfo(NetworkConnectionInfo var1) {
      this.networkConnectionInfo = var1;
   }

   public ValueTimestampTimeZone currentTimestamp() {
      ValueTimestampTimeZone var1 = this.currentTimestamp;
      if (var1 == null) {
         this.currentTimestamp = var1 = DateTimeUtils.currentTimestamp(this.timeZone, this.commandStartOrEnd);
      }

      return var1;
   }

   public Mode getMode() {
      return this.database.getMode();
   }

   public JavaObjectSerializer getJavaObjectSerializer() {
      return this.database.getJavaObjectSerializer();
   }

   public IsolationLevel getIsolationLevel() {
      return this.isolationLevel;
   }

   public void setIsolationLevel(IsolationLevel var1) {
      this.commit(false);
      this.isolationLevel = var1;
   }

   public BitSet getNonKeywords() {
      return this.nonKeywords;
   }

   public void setNonKeywords(BitSet var1) {
      this.nonKeywords = var1;
   }

   public Session.StaticSettings getStaticSettings() {
      Session.StaticSettings var1 = this.staticSettings;
      if (var1 == null) {
         DbSettings var2 = this.database.getSettings();
         this.staticSettings = var1 = new Session.StaticSettings(var2.databaseToUpper, var2.databaseToLower, var2.caseInsensitiveIdentifiers);
      }

      return var1;
   }

   public Session.DynamicSettings getDynamicSettings() {
      return new Session.DynamicSettings(this.database.getMode(), this.timeZone);
   }

   public TimeZoneProvider currentTimeZone() {
      return this.timeZone;
   }

   public void setTimeZone(TimeZoneProvider var1) {
      if (!var1.equals(this.timeZone)) {
         this.timeZone = var1;
         ValueTimestampTimeZone var2 = this.currentTimestamp;
         if (var2 != null) {
            long var3 = var2.getDateValue();
            long var5 = var2.getTimeNanos();
            int var7 = var2.getTimeZoneOffsetSeconds();
            this.currentTimestamp = DateTimeUtils.timestampTimeZoneAtOffset(var3, var5, var7, var1.getTimeZoneOffsetUTC(DateTimeUtils.getEpochSeconds(var3, var5, var7)));
         }

         ++this.modificationId;
      }

   }

   public boolean areEqual(Value var1, Value var2) {
      return var1.compareTo(var2, this, this.database.getCompareMode()) == 0;
   }

   public int compare(Value var1, Value var2) {
      return var1.compareTo(var2, this, this.database.getCompareMode());
   }

   public int compareWithNull(Value var1, Value var2, boolean var3) {
      return var1.compareWithNull(var2, var3, this, this.database.getCompareMode());
   }

   public int compareTypeSafe(Value var1, Value var2) {
      return var1.compareTypeSafe(var2, this.database.getCompareMode(), this);
   }

   public void setTruncateLargeLength(boolean var1) {
      this.truncateLargeLength = var1;
   }

   public boolean isTruncateLargeLength() {
      return this.truncateLargeLength;
   }

   public void setVariableBinary(boolean var1) {
      this.variableBinary = var1;
   }

   public boolean isVariableBinary() {
      return this.variableBinary;
   }

   public void setOldInformationSchema(boolean var1) {
      this.oldInformationSchema = var1;
   }

   public boolean isOldInformationSchema() {
      return this.oldInformationSchema;
   }

   public DatabaseMeta getDatabaseMeta() {
      return new DatabaseMetaLocal(this);
   }

   public boolean zeroBasedEnums() {
      return this.database.zeroBasedEnums();
   }

   public void setQuirksMode(boolean var1) {
      this.quirksMode = var1;
   }

   public boolean isQuirksMode() {
      return this.quirksMode || this.database.isStarting();
   }

   public Session setThreadLocalSession() {
      Session var1 = (Session)THREAD_LOCAL_SESSION.get();
      THREAD_LOCAL_SESSION.set(this);
      return var1;
   }

   public void resetThreadLocalSession(Session var1) {
      if (var1 == null) {
         THREAD_LOCAL_SESSION.remove();
      } else {
         THREAD_LOCAL_SESSION.set(var1);
      }

   }

   public static class TimeoutValue {
      final long created = System.nanoTime();
      final ValueLob value;

      TimeoutValue(ValueLob var1) {
         this.value = var1;
      }
   }

   public static class Savepoint {
      int logIndex;
      long transactionSavepoint;
   }

   private static final class RowNumberAndValue {
      long rowNumber;
      Value nextValue;

      RowNumberAndValue(long var1, Value var3) {
         this.rowNumber = var1;
         this.nextValue = var3;
      }
   }

   private static final class SequenceAndPrepared {
      private final Sequence sequence;
      private final Prepared prepared;

      SequenceAndPrepared(Sequence var1, Prepared var2) {
         this.sequence = var1;
         this.prepared = var2;
      }

      public int hashCode() {
         return 31 * (31 + this.prepared.hashCode()) + this.sequence.hashCode();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && var1.getClass() == SequenceAndPrepared.class) {
            SequenceAndPrepared var2 = (SequenceAndPrepared)var1;
            return this.sequence == var2.sequence && this.prepared == var2.prepared;
         } else {
            return false;
         }
      }
   }

   public static enum State {
      INIT,
      RUNNING,
      BLOCKED,
      SLEEP,
      THROTTLED,
      SUSPENDED,
      CLOSED;
   }
}
