/*      */ package org.h2.engine;
/*      */ 
/*      */ import java.time.Instant;
/*      */ import java.util.ArrayList;
/*      */ import java.util.BitSet;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import java.util.WeakHashMap;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ import org.h2.api.JavaObjectSerializer;
/*      */ import org.h2.command.Command;
/*      */ import org.h2.command.CommandInterface;
/*      */ import org.h2.command.Parser;
/*      */ import org.h2.command.Prepared;
/*      */ import org.h2.command.ddl.Analyze;
/*      */ import org.h2.constraint.Constraint;
/*      */ import org.h2.index.Index;
/*      */ import org.h2.index.ViewIndex;
/*      */ import org.h2.jdbc.JdbcConnection;
/*      */ import org.h2.jdbc.meta.DatabaseMeta;
/*      */ import org.h2.jdbc.meta.DatabaseMetaLocal;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.message.Trace;
/*      */ import org.h2.message.TraceSystem;
/*      */ import org.h2.mvstore.MVMap;
/*      */ import org.h2.mvstore.db.MVIndex;
/*      */ import org.h2.mvstore.db.MVTable;
/*      */ import org.h2.mvstore.db.Store;
/*      */ import org.h2.mvstore.tx.Transaction;
/*      */ import org.h2.mvstore.tx.TransactionStore;
/*      */ import org.h2.result.Row;
/*      */ import org.h2.schema.Schema;
/*      */ import org.h2.schema.Sequence;
/*      */ import org.h2.store.DataHandler;
/*      */ import org.h2.store.InDoubtTransaction;
/*      */ import org.h2.table.Table;
/*      */ import org.h2.util.DateTimeUtils;
/*      */ import org.h2.util.NetworkConnectionInfo;
/*      */ import org.h2.util.SmallLRUCache;
/*      */ import org.h2.util.TimeZoneProvider;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueLob;
/*      */ import org.h2.value.ValueNull;
/*      */ import org.h2.value.ValueTimestampTimeZone;
/*      */ import org.h2.value.ValueVarchar;
/*      */ import org.h2.value.VersionedValue;
/*      */ import org.h2.value.lob.LobData;
/*      */ import org.h2.value.lob.LobDataDatabase;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class SessionLocal
/*      */   extends Session
/*      */   implements TransactionStore.RollbackListener
/*      */ {
/*      */   private static final String SYSTEM_IDENTIFIER_PREFIX = "_";
/*      */   private static int nextSerialId;
/*      */   
/*      */   public enum State
/*      */   {
/*   74 */     INIT, RUNNING, BLOCKED, SLEEP, THROTTLED, SUSPENDED, CLOSED;
/*      */   }
/*      */   
/*      */   private static final class SequenceAndPrepared
/*      */   {
/*      */     private final Sequence sequence;
/*      */     private final Prepared prepared;
/*      */     
/*      */     SequenceAndPrepared(Sequence param1Sequence, Prepared param1Prepared) {
/*   83 */       this.sequence = param1Sequence;
/*   84 */       this.prepared = param1Prepared;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*   89 */       return 31 * (31 + this.prepared.hashCode()) + this.sequence.hashCode();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object param1Object) {
/*   94 */       if (this == param1Object) {
/*   95 */         return true;
/*      */       }
/*   97 */       if (param1Object == null || param1Object.getClass() != SequenceAndPrepared.class) {
/*   98 */         return false;
/*      */       }
/*  100 */       SequenceAndPrepared sequenceAndPrepared = (SequenceAndPrepared)param1Object;
/*  101 */       return (this.sequence == sequenceAndPrepared.sequence && this.prepared == sequenceAndPrepared.prepared);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static final class RowNumberAndValue
/*      */   {
/*      */     long rowNumber;
/*      */     
/*      */     Value nextValue;
/*      */     
/*      */     RowNumberAndValue(long param1Long, Value param1Value) {
/*  113 */       this.rowNumber = param1Long;
/*  114 */       this.nextValue = param1Value;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  129 */   private static final ThreadLocal<Session> THREAD_LOCAL_SESSION = new ThreadLocal<>();
/*      */   
/*      */   static Session getThreadLocalSession() {
/*  132 */     Session session = THREAD_LOCAL_SESSION.get();
/*  133 */     if (session == null) {
/*  134 */       THREAD_LOCAL_SESSION.remove();
/*      */     }
/*  136 */     return session;
/*      */   }
/*      */   
/*  139 */   private final int serialId = nextSerialId++;
/*      */   
/*      */   private final Database database;
/*      */   
/*      */   private final User user;
/*      */   private final int id;
/*      */   private NetworkConnectionInfo networkConnectionInfo;
/*  146 */   private final ArrayList<Table> locks = Utils.newSmallArrayList();
/*      */   
/*      */   private boolean autoCommit = true;
/*      */   private Random random;
/*      */   private int lockTimeout;
/*      */   private HashMap<SequenceAndPrepared, RowNumberAndValue> nextValueFor;
/*      */   private WeakHashMap<Sequence, Value> currentValueFor;
/*  153 */   private Value lastIdentity = (Value)ValueNull.INSTANCE;
/*      */   
/*      */   private HashMap<String, Savepoint> savepoints;
/*      */   private HashMap<String, Table> localTempTables;
/*      */   private HashMap<String, Index> localTempTableIndexes;
/*      */   private HashMap<String, Constraint> localTempTableConstraints;
/*      */   private int throttleMs;
/*      */   private long lastThrottleNs;
/*      */   private Command currentCommand;
/*      */   private boolean allowLiterals;
/*      */   private String currentSchemaName;
/*      */   private String[] schemaSearchPath;
/*      */   private Trace trace;
/*      */   private HashMap<String, ValueLob> removeLobMap;
/*      */   private int systemIdentifier;
/*      */   private HashMap<String, Procedure> procedures;
/*      */   private boolean autoCommitAtTransactionEnd;
/*      */   private String currentTransactionName;
/*      */   private volatile long cancelAtNs;
/*      */   private final ValueTimestampTimeZone sessionStart;
/*      */   private Instant commandStartOrEnd;
/*      */   private ValueTimestampTimeZone currentTimestamp;
/*      */   private HashMap<String, Value> variables;
/*      */   private int queryTimeout;
/*      */   private boolean commitOrRollbackDisabled;
/*      */   private Table waitForLock;
/*      */   private Thread waitForLockThread;
/*      */   private int modificationId;
/*      */   private int objectId;
/*      */   private final int queryCacheSize;
/*      */   private SmallLRUCache<String, Command> queryCache;
/*  184 */   private long modificationMetaID = -1L;
/*      */ 
/*      */   
/*      */   private int createViewLevel;
/*      */ 
/*      */   
/*      */   private volatile SmallLRUCache<Object, ViewIndex> viewIndexCache;
/*      */ 
/*      */   
/*      */   private HashMap<Object, ViewIndex> subQueryIndexCache;
/*      */ 
/*      */   
/*      */   private boolean lazyQueryExecution;
/*      */ 
/*      */   
/*      */   private BitSet nonKeywords;
/*      */ 
/*      */   
/*      */   private TimeZoneProvider timeZone;
/*      */ 
/*      */   
/*      */   private HashSet<Table> tablesToAnalyze;
/*      */ 
/*      */   
/*      */   private LinkedList<TimeoutValue> temporaryResultLobs;
/*      */ 
/*      */   
/*      */   private ArrayList<ValueLob> temporaryLobs;
/*      */ 
/*      */   
/*      */   private Transaction transaction;
/*      */   
/*  216 */   private final AtomicReference<State> state = new AtomicReference<>(State.INIT);
/*  217 */   private long startStatement = -1L;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  222 */   private IsolationLevel isolationLevel = IsolationLevel.READ_COMMITTED;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long snapshotDataModificationId;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private BitSet idsToRelease;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean truncateLargeLength;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean variableBinary;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean oldInformationSchema;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean quirksMode;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SessionLocal(Database paramDatabase, User paramUser, int paramInt) {
/*  259 */     this.database = paramDatabase;
/*  260 */     this.queryTimeout = (paramDatabase.getSettings()).maxQueryTimeout;
/*  261 */     this.queryCacheSize = (paramDatabase.getSettings()).queryCacheSize;
/*  262 */     this.user = paramUser;
/*  263 */     this.id = paramInt;
/*  264 */     this.lockTimeout = paramDatabase.getLockTimeout();
/*  265 */     Schema schema = paramDatabase.getMainSchema();
/*  266 */     this
/*  267 */       .currentSchemaName = (schema != null) ? schema.getName() : paramDatabase.sysIdentifier("PUBLIC");
/*  268 */     this.timeZone = DateTimeUtils.getTimeZone();
/*  269 */     this.sessionStart = DateTimeUtils.currentTimestamp(this.timeZone, this.commandStartOrEnd = Instant.now());
/*      */   }
/*      */   
/*      */   public void setLazyQueryExecution(boolean paramBoolean) {
/*  273 */     this.lazyQueryExecution = paramBoolean;
/*      */   }
/*      */   
/*      */   public boolean isLazyQueryExecution() {
/*  277 */     return this.lazyQueryExecution;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setParsingCreateView(boolean paramBoolean) {
/*  289 */     this.createViewLevel += paramBoolean ? 1 : -1;
/*      */   }
/*      */   
/*      */   public boolean isParsingCreateView() {
/*  293 */     return (this.createViewLevel != 0);
/*      */   }
/*      */ 
/*      */   
/*      */   public ArrayList<String> getClusterServers() {
/*  298 */     return new ArrayList<>();
/*      */   }
/*      */   
/*      */   public boolean setCommitOrRollbackDisabled(boolean paramBoolean) {
/*  302 */     boolean bool = this.commitOrRollbackDisabled;
/*  303 */     this.commitOrRollbackDisabled = paramBoolean;
/*  304 */     return bool;
/*      */   }
/*      */   
/*      */   private void initVariables() {
/*  308 */     if (this.variables == null) {
/*  309 */       this.variables = this.database.newStringMap();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setVariable(String paramString, Value paramValue) {
/*      */     Value value;
/*  320 */     initVariables();
/*  321 */     this.modificationId++;
/*      */     
/*  323 */     if (paramValue == ValueNull.INSTANCE) {
/*  324 */       value = this.variables.remove(paramString);
/*      */     } else {
/*  326 */       ValueLob valueLob; if (paramValue instanceof ValueLob)
/*      */       {
/*  328 */         valueLob = ((ValueLob)paramValue).copy(this.database, -1);
/*      */       }
/*  330 */       value = (Value)this.variables.put(paramString, valueLob);
/*      */     } 
/*  332 */     if (value instanceof ValueLob) {
/*  333 */       ((ValueLob)value).remove();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Value getVariable(String paramString) {
/*  346 */     initVariables();
/*  347 */     Value value = this.variables.get(paramString);
/*  348 */     return (value == null) ? (Value)ValueNull.INSTANCE : value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getVariableNames() {
/*  357 */     if (this.variables == null) {
/*  358 */       return new String[0];
/*      */     }
/*  360 */     return (String[])this.variables.keySet().toArray((Object[])new String[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Table findLocalTempTable(String paramString) {
/*  371 */     if (this.localTempTables == null) {
/*  372 */       return null;
/*      */     }
/*  374 */     return this.localTempTables.get(paramString);
/*      */   }
/*      */   
/*      */   public List<Table> getLocalTempTables() {
/*  378 */     if (this.localTempTables == null) {
/*  379 */       return Collections.emptyList();
/*      */     }
/*  381 */     return new ArrayList<>(this.localTempTables.values());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addLocalTempTable(Table paramTable) {
/*  391 */     if (this.localTempTables == null) {
/*  392 */       this.localTempTables = this.database.newStringMap();
/*      */     }
/*  394 */     if (this.localTempTables.putIfAbsent(paramTable.getName(), paramTable) != null) {
/*  395 */       StringBuilder stringBuilder = new StringBuilder();
/*  396 */       paramTable.getSQL(stringBuilder, 3).append(" AS ");
/*  397 */       Parser.quoteIdentifier(paramTable.getName(), 3);
/*  398 */       throw DbException.get(42101, stringBuilder.toString());
/*      */     } 
/*  400 */     this.modificationId++;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeLocalTempTable(Table paramTable) {
/*  409 */     this.modificationId++;
/*  410 */     if (this.localTempTables != null) {
/*  411 */       this.localTempTables.remove(paramTable.getName());
/*      */     }
/*  413 */     synchronized (this.database) {
/*  414 */       paramTable.removeChildrenAndResources(this);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Index findLocalTempTableIndex(String paramString) {
/*  426 */     if (this.localTempTableIndexes == null) {
/*  427 */       return null;
/*      */     }
/*  429 */     return this.localTempTableIndexes.get(paramString);
/*      */   }
/*      */   
/*      */   public HashMap<String, Index> getLocalTempTableIndexes() {
/*  433 */     if (this.localTempTableIndexes == null) {
/*  434 */       return new HashMap<>();
/*      */     }
/*  436 */     return this.localTempTableIndexes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addLocalTempTableIndex(Index paramIndex) {
/*  446 */     if (this.localTempTableIndexes == null) {
/*  447 */       this.localTempTableIndexes = this.database.newStringMap();
/*      */     }
/*  449 */     if (this.localTempTableIndexes.putIfAbsent(paramIndex.getName(), paramIndex) != null) {
/*  450 */       throw DbException.get(42111, paramIndex.getTraceSQL());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeLocalTempTableIndex(Index paramIndex) {
/*  460 */     if (this.localTempTableIndexes != null) {
/*  461 */       this.localTempTableIndexes.remove(paramIndex.getName());
/*  462 */       synchronized (this.database) {
/*  463 */         paramIndex.removeChildrenAndResources(this);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Constraint findLocalTempTableConstraint(String paramString) {
/*  476 */     if (this.localTempTableConstraints == null) {
/*  477 */       return null;
/*      */     }
/*  479 */     return this.localTempTableConstraints.get(paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HashMap<String, Constraint> getLocalTempTableConstraints() {
/*  489 */     if (this.localTempTableConstraints == null) {
/*  490 */       return new HashMap<>();
/*      */     }
/*  492 */     return this.localTempTableConstraints;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addLocalTempTableConstraint(Constraint paramConstraint) {
/*  502 */     if (this.localTempTableConstraints == null) {
/*  503 */       this.localTempTableConstraints = this.database.newStringMap();
/*      */     }
/*  505 */     String str = paramConstraint.getName();
/*  506 */     if (this.localTempTableConstraints.putIfAbsent(str, paramConstraint) != null) {
/*  507 */       throw DbException.get(90045, paramConstraint.getTraceSQL());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void removeLocalTempTableConstraint(Constraint paramConstraint) {
/*  517 */     if (this.localTempTableConstraints != null) {
/*  518 */       this.localTempTableConstraints.remove(paramConstraint.getName());
/*  519 */       synchronized (this.database) {
/*  520 */         paramConstraint.removeChildrenAndResources(this);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean getAutoCommit() {
/*  527 */     return this.autoCommit;
/*      */   }
/*      */   
/*      */   public User getUser() {
/*  531 */     return this.user;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setAutoCommit(boolean paramBoolean) {
/*  536 */     this.autoCommit = paramBoolean;
/*      */   }
/*      */   
/*      */   public int getLockTimeout() {
/*  540 */     return this.lockTimeout;
/*      */   }
/*      */   
/*      */   public void setLockTimeout(int paramInt) {
/*  544 */     this.lockTimeout = paramInt;
/*  545 */     if (hasTransaction()) {
/*  546 */       this.transaction.setTimeoutMillis(paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized CommandInterface prepareCommand(String paramString, int paramInt) {
/*  553 */     return (CommandInterface)prepareLocal(paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Prepared prepare(String paramString) {
/*  564 */     return prepare(paramString, false, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Prepared prepare(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
/*  577 */     Parser parser = new Parser(this);
/*  578 */     parser.setRightsChecked(paramBoolean1);
/*  579 */     parser.setLiteralsChecked(paramBoolean2);
/*  580 */     return parser.prepare(paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Command prepareLocal(String paramString) {
/*      */     Command command;
/*  591 */     if (isClosed()) {
/*  592 */       throw DbException.get(90067, "session closed");
/*      */     }
/*      */ 
/*      */     
/*  596 */     if (this.queryCacheSize > 0) {
/*  597 */       if (this.queryCache == null) {
/*  598 */         this.queryCache = SmallLRUCache.newInstance(this.queryCacheSize);
/*  599 */         this.modificationMetaID = this.database.getModificationMetaId();
/*      */       } else {
/*  601 */         long l = this.database.getModificationMetaId();
/*  602 */         if (l != this.modificationMetaID) {
/*  603 */           this.queryCache.clear();
/*  604 */           this.modificationMetaID = l;
/*      */         } 
/*  606 */         command = (Command)this.queryCache.get(paramString);
/*  607 */         if (command != null && command.canReuse()) {
/*  608 */           command.reuse();
/*  609 */           return command;
/*      */         } 
/*      */       } 
/*      */     }
/*  613 */     Parser parser = new Parser(this);
/*      */     try {
/*  615 */       command = parser.prepareCommand(paramString);
/*      */     } finally {
/*      */       
/*  618 */       this.subQueryIndexCache = null;
/*      */     } 
/*  620 */     if (this.queryCache != null && 
/*  621 */       command.isCacheable()) {
/*  622 */       this.queryCache.put(paramString, command);
/*      */     }
/*      */     
/*  625 */     return command;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void scheduleDatabaseObjectIdForRelease(int paramInt) {
/*  634 */     if (this.idsToRelease == null) {
/*  635 */       this.idsToRelease = new BitSet();
/*      */     }
/*  637 */     this.idsToRelease.set(paramInt);
/*      */   }
/*      */   
/*      */   public Database getDatabase() {
/*  641 */     return this.database;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void commit(boolean paramBoolean) {
/*  652 */     beforeCommitOrRollback();
/*  653 */     if (hasTransaction()) {
/*      */       try {
/*  655 */         markUsedTablesAsUpdated();
/*  656 */         this.transaction.commit();
/*  657 */         removeTemporaryLobs(true);
/*  658 */         endTransaction();
/*      */       } finally {
/*  660 */         this.transaction = null;
/*      */       } 
/*  662 */       if (!paramBoolean) {
/*      */ 
/*      */         
/*  665 */         cleanTempTables(false);
/*  666 */         if (this.autoCommitAtTransactionEnd) {
/*  667 */           this.autoCommit = true;
/*  668 */           this.autoCommitAtTransactionEnd = false;
/*      */         } 
/*      */       } 
/*  671 */       analyzeTables();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void markUsedTablesAsUpdated() {
/*  677 */     if (!this.locks.isEmpty()) {
/*  678 */       for (Table table : this.locks) {
/*  679 */         if (table instanceof MVTable) {
/*  680 */           ((MVTable)table).commit();
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void analyzeTables() {
/*  690 */     if (this.tablesToAnalyze != null && 
/*  691 */       Thread.holdsLock(this)) {
/*      */ 
/*      */       
/*  694 */       HashSet<Table> hashSet = this.tablesToAnalyze;
/*  695 */       this.tablesToAnalyze = null;
/*  696 */       int i = (getDatabase().getSettings()).analyzeSample / 10;
/*  697 */       for (Table table : hashSet) {
/*  698 */         Analyze.analyzeTable(this, table, i, false);
/*      */       }
/*      */       
/*  701 */       this.database.unlockMeta(this);
/*      */ 
/*      */       
/*  704 */       commit(true);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void removeTemporaryLobs(boolean paramBoolean) {
/*  709 */     if (this.temporaryLobs != null) {
/*  710 */       for (ValueLob valueLob : this.temporaryLobs) {
/*  711 */         if (!valueLob.isLinkedToTable()) {
/*  712 */           valueLob.remove();
/*      */         }
/*      */       } 
/*  715 */       this.temporaryLobs.clear();
/*      */     } 
/*  717 */     if (this.temporaryResultLobs != null && !this.temporaryResultLobs.isEmpty()) {
/*  718 */       long l = System.nanoTime() - (this.database.getSettings()).lobTimeout * 1000000L;
/*  719 */       while (!this.temporaryResultLobs.isEmpty()) {
/*  720 */         TimeoutValue timeoutValue = this.temporaryResultLobs.getFirst();
/*  721 */         if (paramBoolean && timeoutValue.created - l >= 0L) {
/*      */           break;
/*      */         }
/*  724 */         ValueLob valueLob = ((TimeoutValue)this.temporaryResultLobs.removeFirst()).value;
/*  725 */         if (!valueLob.isLinkedToTable()) {
/*  726 */           valueLob.remove();
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void beforeCommitOrRollback() {
/*  733 */     if (this.commitOrRollbackDisabled && !this.locks.isEmpty()) {
/*  734 */       throw DbException.get(90058);
/*      */     }
/*  736 */     this.currentTransactionName = null;
/*  737 */     this.currentTimestamp = null;
/*  738 */     this.database.throwLastBackgroundException();
/*      */   }
/*      */   
/*      */   private void endTransaction() {
/*  742 */     if (this.removeLobMap != null && !this.removeLobMap.isEmpty()) {
/*  743 */       for (ValueLob valueLob : this.removeLobMap.values()) {
/*  744 */         valueLob.remove();
/*      */       }
/*  746 */       this.removeLobMap = null;
/*      */     } 
/*  748 */     unlockAll();
/*  749 */     if (this.idsToRelease != null) {
/*  750 */       this.database.releaseDatabaseObjectIds(this.idsToRelease);
/*  751 */       this.idsToRelease = null;
/*      */     } 
/*  753 */     if (hasTransaction() && !this.transaction.allowNonRepeatableRead()) {
/*  754 */       this.snapshotDataModificationId = this.database.getNextModificationDataId();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getSnapshotDataModificationId() {
/*  765 */     return this.snapshotDataModificationId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void rollback() {
/*  772 */     beforeCommitOrRollback();
/*  773 */     if (hasTransaction()) {
/*  774 */       rollbackTo((Savepoint)null);
/*      */     }
/*  776 */     this.idsToRelease = null;
/*  777 */     cleanTempTables(false);
/*  778 */     if (this.autoCommitAtTransactionEnd) {
/*  779 */       this.autoCommit = true;
/*  780 */       this.autoCommitAtTransactionEnd = false;
/*      */     } 
/*  782 */     endTransaction();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void rollbackTo(Savepoint paramSavepoint) {
/*  791 */     byte b = (paramSavepoint == null) ? 0 : paramSavepoint.logIndex;
/*  792 */     if (hasTransaction()) {
/*  793 */       markUsedTablesAsUpdated();
/*  794 */       if (paramSavepoint == null) {
/*  795 */         this.transaction.rollback();
/*  796 */         this.transaction = null;
/*      */       } else {
/*  798 */         this.transaction.rollbackToSavepoint(paramSavepoint.transactionSavepoint);
/*      */       } 
/*      */     } 
/*  801 */     if (this.savepoints != null) {
/*  802 */       String[] arrayOfString = (String[])this.savepoints.keySet().toArray((Object[])new String[0]);
/*  803 */       for (String str : arrayOfString) {
/*  804 */         Savepoint savepoint = this.savepoints.get(str);
/*  805 */         int i = savepoint.logIndex;
/*  806 */         if (i > b) {
/*  807 */           this.savepoints.remove(str);
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  815 */     if (this.queryCache != null) {
/*  816 */       this.queryCache.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasPendingTransaction() {
/*  822 */     return (hasTransaction() && this.transaction.hasChanges() && this.transaction.getStatus() != 2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Savepoint setSavepoint() {
/*  831 */     Savepoint savepoint = new Savepoint();
/*  832 */     savepoint.transactionSavepoint = getStatementSavepoint();
/*  833 */     return savepoint;
/*      */   }
/*      */   
/*      */   public int getId() {
/*  837 */     return this.id;
/*      */   }
/*      */ 
/*      */   
/*      */   public void cancel() {
/*  842 */     this.cancelAtNs = Utils.currentNanoTime();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void suspend() {
/*  849 */     cancel();
/*  850 */     if (transitionToState(State.SUSPENDED, false) == State.SLEEP) {
/*  851 */       close();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() {
/*  859 */     if (this.state.getAndSet(State.CLOSED) != State.CLOSED) {
/*      */       try {
/*  861 */         this.database.throwLastBackgroundException();
/*      */         
/*  863 */         this.database.checkPowerOff();
/*      */ 
/*      */         
/*  866 */         if (hasPreparedTransaction()) {
/*  867 */           if (this.currentTransactionName != null) {
/*  868 */             this.removeLobMap = null;
/*      */           }
/*  870 */           endTransaction();
/*      */         } else {
/*  872 */           rollback();
/*  873 */           removeTemporaryLobs(false);
/*  874 */           cleanTempTables(true);
/*  875 */           commit(true);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  881 */         this.database.unlockMeta(this);
/*      */       } finally {
/*  883 */         this.database.removeSession(this);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerTableAsLocked(Table paramTable) {
/*  896 */     if (SysProperties.CHECK && 
/*  897 */       this.locks.contains(paramTable)) {
/*  898 */       throw DbException.getInternalError(paramTable.toString());
/*      */     }
/*      */     
/*  901 */     this.locks.add(paramTable);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerTableAsUpdated(Table paramTable) {
/*  911 */     if (!this.locks.contains(paramTable)) {
/*  912 */       this.locks.add(paramTable);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void unlock(Table paramTable) {
/*  922 */     this.locks.remove(paramTable);
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean hasTransaction() {
/*  927 */     return (this.transaction != null);
/*      */   }
/*      */   
/*      */   private void unlockAll() {
/*  931 */     if (!this.locks.isEmpty()) {
/*  932 */       Table[] arrayOfTable = this.locks.<Table>toArray(new Table[0]);
/*  933 */       for (Table table : arrayOfTable) {
/*  934 */         if (table != null) {
/*  935 */           table.unlock(this);
/*      */         }
/*      */       } 
/*  938 */       this.locks.clear();
/*      */     } 
/*  940 */     Database.unlockMetaDebug(this);
/*  941 */     this.savepoints = null;
/*  942 */     this.sessionStateChanged = true;
/*      */   }
/*      */   
/*      */   private void cleanTempTables(boolean paramBoolean) {
/*  946 */     if (this.localTempTables != null && !this.localTempTables.isEmpty()) {
/*  947 */       Iterator<Table> iterator = this.localTempTables.values().iterator();
/*  948 */       while (iterator.hasNext()) {
/*  949 */         Table table = iterator.next();
/*  950 */         if (paramBoolean || table.getOnCommitDrop()) {
/*  951 */           this.modificationId++;
/*  952 */           table.setModified();
/*  953 */           iterator.remove();
/*      */ 
/*      */           
/*  956 */           this.database.lockMeta(this);
/*  957 */           table.removeChildrenAndResources(this);
/*  958 */           if (paramBoolean)
/*  959 */             this.database.throwLastBackgroundException();  continue;
/*      */         } 
/*  961 */         if (table.getOnCommitTruncate()) {
/*  962 */           table.truncate(this);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public Random getRandom() {
/*  969 */     if (this.random == null) {
/*  970 */       this.random = new Random();
/*      */     }
/*  972 */     return this.random;
/*      */   }
/*      */ 
/*      */   
/*      */   public Trace getTrace() {
/*  977 */     if (this.trace != null && !isClosed()) {
/*  978 */       return this.trace;
/*      */     }
/*  980 */     String str = "jdbc[" + this.id + "]";
/*  981 */     if (isClosed()) {
/*  982 */       return (new TraceSystem(null)).getTrace(str);
/*      */     }
/*  984 */     this.trace = this.database.getTraceSystem().getTrace(str);
/*  985 */     return this.trace;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Value getNextValueFor(Sequence paramSequence, Prepared paramPrepared) {
/*      */     Value value;
/*  999 */     Mode mode = this.database.getMode();
/* 1000 */     if (mode.nextValueReturnsDifferentValues || paramPrepared == null) {
/* 1001 */       value = paramSequence.getNext(this);
/*      */     } else {
/* 1003 */       if (this.nextValueFor == null) {
/* 1004 */         this.nextValueFor = new HashMap<>();
/*      */       }
/* 1006 */       SequenceAndPrepared sequenceAndPrepared = new SequenceAndPrepared(paramSequence, paramPrepared);
/* 1007 */       RowNumberAndValue rowNumberAndValue = this.nextValueFor.get(sequenceAndPrepared);
/* 1008 */       long l = paramPrepared.getCurrentRowNumber();
/* 1009 */       if (rowNumberAndValue != null) {
/* 1010 */         if (rowNumberAndValue.rowNumber == l) {
/* 1011 */           value = rowNumberAndValue.nextValue;
/*      */         } else {
/* 1013 */           rowNumberAndValue.nextValue = value = paramSequence.getNext(this);
/* 1014 */           rowNumberAndValue.rowNumber = l;
/*      */         } 
/*      */       } else {
/* 1017 */         value = paramSequence.getNext(this);
/* 1018 */         this.nextValueFor.put(sequenceAndPrepared, new RowNumberAndValue(l, value));
/*      */       } 
/*      */     } 
/* 1021 */     WeakHashMap<Sequence, Value> weakHashMap = this.currentValueFor;
/* 1022 */     if (weakHashMap == null) {
/* 1023 */       this.currentValueFor = weakHashMap = new WeakHashMap<>();
/*      */     }
/* 1025 */     weakHashMap.put(paramSequence, value);
/* 1026 */     if (mode.takeGeneratedSequenceValue) {
/* 1027 */       this.lastIdentity = value;
/*      */     }
/* 1029 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Value getCurrentValueFor(Sequence paramSequence) {
/* 1042 */     WeakHashMap<Sequence, Value> weakHashMap = this.currentValueFor;
/* 1043 */     if (weakHashMap != null) {
/* 1044 */       Value value = weakHashMap.get(paramSequence);
/* 1045 */       if (value != null) {
/* 1046 */         return value;
/*      */       }
/*      */     } 
/* 1049 */     throw DbException.get(90148, paramSequence.getTraceSQL());
/*      */   }
/*      */   
/*      */   public void setLastIdentity(Value paramValue) {
/* 1053 */     this.lastIdentity = paramValue;
/*      */   }
/*      */   
/*      */   public Value getLastIdentity() {
/* 1057 */     return this.lastIdentity;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean containsUncommitted() {
/* 1066 */     return (this.transaction != null && this.transaction.hasChanges());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addSavepoint(String paramString) {
/* 1075 */     if (this.savepoints == null) {
/* 1076 */       this.savepoints = this.database.newStringMap();
/*      */     }
/* 1078 */     this.savepoints.put(paramString, setSavepoint());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void rollbackToSavepoint(String paramString) {
/* 1087 */     beforeCommitOrRollback();
/*      */     Savepoint savepoint;
/* 1089 */     if (this.savepoints == null || (savepoint = this.savepoints.get(paramString)) == null) {
/* 1090 */       throw DbException.get(90063, paramString);
/*      */     }
/* 1092 */     rollbackTo(savepoint);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void prepareCommit(String paramString) {
/* 1101 */     if (hasPendingTransaction())
/*      */     {
/*      */       
/* 1104 */       this.database.prepareCommit(this, paramString);
/*      */     }
/* 1106 */     this.currentTransactionName = paramString;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasPreparedTransaction() {
/* 1116 */     return (this.currentTransactionName != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPreparedTransaction(String paramString, boolean paramBoolean) {
/* 1126 */     if (hasPreparedTransaction() && this.currentTransactionName.equals(paramString)) {
/* 1127 */       if (paramBoolean) {
/* 1128 */         commit(false);
/*      */       } else {
/* 1130 */         rollback();
/*      */       } 
/*      */     } else {
/* 1133 */       ArrayList<InDoubtTransaction> arrayList = this.database.getInDoubtTransactions();
/* 1134 */       boolean bool1 = paramBoolean ? true : true;
/* 1135 */       boolean bool2 = false;
/* 1136 */       for (InDoubtTransaction inDoubtTransaction : arrayList) {
/* 1137 */         if (inDoubtTransaction.getTransactionName().equals(paramString)) {
/* 1138 */           inDoubtTransaction.setState(bool1);
/* 1139 */           bool2 = true;
/*      */           break;
/*      */         } 
/*      */       } 
/* 1143 */       if (!bool2) {
/* 1144 */         throw DbException.get(90129, paramString);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClosed() {
/* 1152 */     return (this.state.get() == State.CLOSED);
/*      */   }
/*      */   
/*      */   public boolean isOpen() {
/* 1156 */     State state = this.state.get();
/* 1157 */     checkSuspended(state);
/* 1158 */     return (state != State.CLOSED);
/*      */   }
/*      */   
/*      */   public void setThrottle(int paramInt) {
/* 1162 */     this.throttleMs = paramInt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void throttle() {
/* 1169 */     if (this.throttleMs == 0) {
/*      */       return;
/*      */     }
/* 1172 */     long l = System.nanoTime();
/* 1173 */     if (this.lastThrottleNs != 0L && l - this.lastThrottleNs < 50000000L) {
/*      */       return;
/*      */     }
/* 1176 */     this.lastThrottleNs = Utils.nanoTimePlusMillis(l, this.throttleMs);
/* 1177 */     State state = transitionToState(State.THROTTLED, false);
/*      */     
/* 1179 */     try { Thread.sleep(this.throttleMs); }
/* 1180 */     catch (InterruptedException interruptedException) {  }
/*      */     finally
/* 1182 */     { transitionToState(state, false); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setCurrentCommand(Command paramCommand) {
/* 1193 */     State state = (paramCommand == null) ? State.SLEEP : State.RUNNING;
/* 1194 */     transitionToState(state, true);
/* 1195 */     if (isOpen()) {
/* 1196 */       this.currentCommand = paramCommand;
/* 1197 */       this.commandStartOrEnd = Instant.now();
/* 1198 */       if (paramCommand != null) {
/* 1199 */         if (this.queryTimeout > 0) {
/* 1200 */           this.cancelAtNs = Utils.currentNanoTimePlusMillis(this.queryTimeout);
/*      */         }
/*      */       } else {
/* 1203 */         if (this.currentTimestamp != null && !(this.database.getMode()).dateTimeValueWithinTransaction) {
/* 1204 */           this.currentTimestamp = null;
/*      */         }
/* 1206 */         if (this.nextValueFor != null) {
/* 1207 */           this.nextValueFor.clear();
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private State transitionToState(State paramState, boolean paramBoolean) {
/*      */     State state;
/* 1215 */     while ((state = this.state.get()) != State.CLOSED && (!paramBoolean || 
/* 1216 */       checkSuspended(state)) && 
/* 1217 */       !this.state.compareAndSet(state, paramState));
/* 1218 */     return state;
/*      */   }
/*      */   
/*      */   private boolean checkSuspended(State paramState) {
/* 1222 */     if (paramState == State.SUSPENDED) {
/* 1223 */       close();
/* 1224 */       throw DbException.get(90135);
/*      */     } 
/* 1226 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void checkCanceled() {
/* 1236 */     throttle();
/* 1237 */     long l = this.cancelAtNs;
/* 1238 */     if (l == 0L) {
/*      */       return;
/*      */     }
/* 1241 */     if (System.nanoTime() - l >= 0L) {
/* 1242 */       this.cancelAtNs = 0L;
/* 1243 */       throw DbException.get(57014);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getCancel() {
/* 1253 */     return this.cancelAtNs;
/*      */   }
/*      */   
/*      */   public Command getCurrentCommand() {
/* 1257 */     return this.currentCommand;
/*      */   }
/*      */   
/*      */   public ValueTimestampTimeZone getCommandStartOrEnd() {
/* 1261 */     return DateTimeUtils.currentTimestamp(this.timeZone, this.commandStartOrEnd);
/*      */   }
/*      */   
/*      */   public boolean getAllowLiterals() {
/* 1265 */     return this.allowLiterals;
/*      */   }
/*      */   
/*      */   public void setAllowLiterals(boolean paramBoolean) {
/* 1269 */     this.allowLiterals = paramBoolean;
/*      */   }
/*      */   
/*      */   public void setCurrentSchema(Schema paramSchema) {
/* 1273 */     this.modificationId++;
/* 1274 */     if (this.queryCache != null) {
/* 1275 */       this.queryCache.clear();
/*      */     }
/* 1277 */     this.currentSchemaName = paramSchema.getName();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getCurrentSchemaName() {
/* 1282 */     return this.currentSchemaName;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCurrentSchemaName(String paramString) {
/* 1287 */     Schema schema = this.database.getSchema(paramString);
/* 1288 */     setCurrentSchema(schema);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JdbcConnection createConnection(boolean paramBoolean) {
/*      */     String str;
/* 1300 */     if (paramBoolean) {
/* 1301 */       str = "jdbc:columnlist:connection";
/*      */     } else {
/* 1303 */       str = "jdbc:default:connection";
/*      */     } 
/* 1305 */     return new JdbcConnection(this, getUser().getName(), str);
/*      */   }
/*      */ 
/*      */   
/*      */   public DataHandler getDataHandler() {
/* 1310 */     return this.database;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeAtCommit(ValueLob paramValueLob) {
/* 1319 */     if (paramValueLob.isLinkedToTable()) {
/* 1320 */       if (this.removeLobMap == null) {
/* 1321 */         this.removeLobMap = new HashMap<>();
/*      */       }
/* 1323 */       this.removeLobMap.put(paramValueLob.toString(), paramValueLob);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeAtCommitStop(ValueLob paramValueLob) {
/* 1333 */     if (paramValueLob.isLinkedToTable() && this.removeLobMap != null) {
/* 1334 */       this.removeLobMap.remove(paramValueLob.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNextSystemIdentifier(String paramString) {
/*      */     while (true) {
/* 1348 */       String str = "_" + this.systemIdentifier++;
/* 1349 */       if (!paramString.contains(str)) {
/* 1350 */         return str;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addProcedure(Procedure paramProcedure) {
/* 1359 */     if (this.procedures == null) {
/* 1360 */       this.procedures = this.database.newStringMap();
/*      */     }
/* 1362 */     this.procedures.put(paramProcedure.getName(), paramProcedure);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeProcedure(String paramString) {
/* 1371 */     if (this.procedures != null) {
/* 1372 */       this.procedures.remove(paramString);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Procedure getProcedure(String paramString) {
/* 1384 */     if (this.procedures == null) {
/* 1385 */       return null;
/*      */     }
/* 1387 */     return this.procedures.get(paramString);
/*      */   }
/*      */   
/*      */   public void setSchemaSearchPath(String[] paramArrayOfString) {
/* 1391 */     this.modificationId++;
/* 1392 */     this.schemaSearchPath = paramArrayOfString;
/*      */   }
/*      */   
/*      */   public String[] getSchemaSearchPath() {
/* 1396 */     return this.schemaSearchPath;
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/* 1401 */     return this.serialId;
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1406 */     return "#" + this.serialId + " (user: " + ((this.user == null) ? "<null>" : this.user.getName()) + ", " + this.state.get() + ")";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void begin() {
/* 1413 */     this.autoCommitAtTransactionEnd = true;
/* 1414 */     this.autoCommit = false;
/*      */   }
/*      */   
/*      */   public ValueTimestampTimeZone getSessionStart() {
/* 1418 */     return this.sessionStart;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<Table> getLocks() {
/* 1425 */     if (this.database.getLockMode() == 0 || this.locks.isEmpty()) {
/* 1426 */       return Collections.emptySet();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1432 */     Object[] arrayOfObject = this.locks.toArray();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1437 */     switch (arrayOfObject.length) {
/*      */       case 1:
/* 1439 */         object = arrayOfObject[0];
/* 1440 */         if (object != null) {
/* 1441 */           return Collections.singleton((Table)object);
/*      */         }
/*      */ 
/*      */       
/*      */       case 0:
/* 1446 */         return Collections.emptySet();
/*      */     } 
/* 1448 */     Object object = new HashSet();
/* 1449 */     for (Object object1 : arrayOfObject) {
/* 1450 */       if (object1 != null) {
/* 1451 */         object.add((Table)object1);
/*      */       }
/*      */     } 
/* 1454 */     return (Set<Table>)object;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void waitIfExclusiveModeEnabled() {
/* 1464 */     transitionToState(State.RUNNING, true);
/*      */ 
/*      */     
/* 1467 */     if (this.database.getLobSession() == this) {
/*      */       return;
/*      */     }
/* 1470 */     while (isOpen()) {
/* 1471 */       SessionLocal sessionLocal = this.database.getExclusiveSession();
/* 1472 */       if (sessionLocal == null || sessionLocal == this) {
/*      */         break;
/*      */       }
/* 1475 */       if (Thread.holdsLock(sessionLocal)) {
/*      */         break;
/*      */       }
/*      */       
/*      */       try {
/* 1480 */         Thread.sleep(100L);
/* 1481 */       } catch (InterruptedException interruptedException) {}
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<Object, ViewIndex> getViewIndexCache(boolean paramBoolean) {
/* 1496 */     if (paramBoolean) {
/*      */ 
/*      */ 
/*      */       
/* 1500 */       if (this.subQueryIndexCache == null) {
/* 1501 */         this.subQueryIndexCache = new HashMap<>();
/*      */       }
/* 1503 */       return this.subQueryIndexCache;
/*      */     } 
/* 1505 */     SmallLRUCache<Object, ViewIndex> smallLRUCache = this.viewIndexCache;
/* 1506 */     if (smallLRUCache == null) {
/* 1507 */       this.viewIndexCache = smallLRUCache = SmallLRUCache.newInstance(64);
/*      */     }
/* 1509 */     return (Map<Object, ViewIndex>)smallLRUCache;
/*      */   }
/*      */   
/*      */   public void setQueryTimeout(int paramInt) {
/* 1513 */     int i = (this.database.getSettings()).maxQueryTimeout;
/* 1514 */     if (i != 0 && (i < paramInt || paramInt == 0))
/*      */     {
/* 1516 */       paramInt = i;
/*      */     }
/* 1518 */     this.queryTimeout = paramInt;
/*      */ 
/*      */     
/* 1521 */     this.cancelAtNs = 0L;
/*      */   }
/*      */   
/*      */   public int getQueryTimeout() {
/* 1525 */     return this.queryTimeout;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setWaitForLock(Table paramTable, Thread paramThread) {
/* 1536 */     this.waitForLock = paramTable;
/* 1537 */     this.waitForLockThread = paramThread;
/*      */   }
/*      */   
/*      */   public Table getWaitForLock() {
/* 1541 */     return this.waitForLock;
/*      */   }
/*      */   
/*      */   public Thread getWaitForLockThread() {
/* 1545 */     return this.waitForLockThread;
/*      */   }
/*      */   
/*      */   public int getModificationId() {
/* 1549 */     return this.modificationId;
/*      */   }
/*      */   
/*      */   public Value getTransactionId() {
/* 1553 */     if (this.transaction == null || !this.transaction.hasChanges()) {
/* 1554 */       return (Value)ValueNull.INSTANCE;
/*      */     }
/* 1556 */     return ValueVarchar.get(Long.toString(this.transaction.getSequenceNum()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int nextObjectId() {
/* 1565 */     return this.objectId++;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Transaction getTransaction() {
/* 1574 */     if (this.transaction == null) {
/* 1575 */       Store store = this.database.getStore();
/* 1576 */       if (store.getMvStore().isClosed()) {
/* 1577 */         Throwable throwable = this.database.getBackgroundException();
/* 1578 */         this.database.shutdownImmediately();
/* 1579 */         throw DbException.get(90098, throwable, new String[0]);
/*      */       } 
/* 1581 */       this.transaction = store.getTransactionStore().begin(this, this.lockTimeout, this.id, this.isolationLevel);
/* 1582 */       this.startStatement = -1L;
/*      */     } 
/* 1584 */     return this.transaction;
/*      */   }
/*      */   
/*      */   private long getStatementSavepoint() {
/* 1588 */     if (this.startStatement == -1L) {
/* 1589 */       this.startStatement = getTransaction().setSavepoint();
/*      */     }
/* 1591 */     return this.startStatement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void startStatementWithinTransaction(Command paramCommand) {
/* 1600 */     Transaction transaction = getTransaction();
/* 1601 */     if (transaction != null) {
/* 1602 */       HashSet<MVMap<Object, VersionedValue<Object>>> hashSet = new HashSet();
/* 1603 */       if (paramCommand != null) {
/* 1604 */         HashSet<MVTable> hashSet1; Set set = paramCommand.getDependencies();
/* 1605 */         switch (transaction.getIsolationLevel()) {
/*      */           case SNAPSHOT:
/*      */           case SERIALIZABLE:
/* 1608 */             if (!transaction.hasStatementDependencies()) {
/* 1609 */               for (Schema schema : this.database.getAllSchemasNoMeta()) {
/* 1610 */                 for (Table table : schema.getAllTablesAndViews(null)) {
/* 1611 */                   if (table instanceof MVTable) {
/* 1612 */                     addTableToDependencies((MVTable)table, hashSet);
/*      */                   }
/*      */                 } 
/*      */               } 
/*      */               break;
/*      */             } 
/*      */           
/*      */           case READ_COMMITTED:
/*      */           case READ_UNCOMMITTED:
/* 1621 */             for (DbObject dbObject : set) {
/* 1622 */               if (dbObject instanceof MVTable) {
/* 1623 */                 addTableToDependencies((MVTable)dbObject, hashSet);
/*      */               }
/*      */             } 
/*      */             break;
/*      */           case REPEATABLE_READ:
/* 1628 */             hashSet1 = new HashSet();
/* 1629 */             for (DbObject dbObject : set) {
/* 1630 */               if (dbObject instanceof MVTable) {
/* 1631 */                 addTableToDependencies((MVTable)dbObject, hashSet, hashSet1);
/*      */               }
/*      */             } 
/*      */             break;
/*      */         } 
/*      */       } 
/* 1637 */       transaction.markStatementStart(hashSet);
/*      */     } 
/* 1639 */     this.startStatement = -1L;
/* 1640 */     if (paramCommand != null) {
/* 1641 */       setCurrentCommand(paramCommand);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static void addTableToDependencies(MVTable paramMVTable, HashSet<MVMap<Object, VersionedValue<Object>>> paramHashSet) {
/* 1647 */     for (Index index : paramMVTable.getIndexes()) {
/* 1648 */       if (index instanceof MVIndex) {
/* 1649 */         paramHashSet.add(((MVIndex)index).getMVMap());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private static void addTableToDependencies(MVTable paramMVTable, HashSet<MVMap<Object, VersionedValue<Object>>> paramHashSet, HashSet<MVTable> paramHashSet1) {
/* 1656 */     if (!paramHashSet1.add(paramMVTable)) {
/*      */       return;
/*      */     }
/* 1659 */     addTableToDependencies(paramMVTable, paramHashSet);
/* 1660 */     ArrayList arrayList = paramMVTable.getConstraints();
/* 1661 */     if (arrayList != null) {
/* 1662 */       for (Constraint constraint : arrayList) {
/* 1663 */         Table table = constraint.getTable();
/* 1664 */         if (table != paramMVTable && table instanceof MVTable) {
/* 1665 */           addTableToDependencies((MVTable)table, paramHashSet, paramHashSet1);
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void endStatement() {
/* 1676 */     setCurrentCommand((Command)null);
/* 1677 */     if (hasTransaction()) {
/* 1678 */       this.transaction.markStatementEnd();
/*      */     }
/* 1680 */     this.startStatement = -1L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearViewIndexCache() {
/* 1687 */     this.viewIndexCache = null;
/*      */   }
/*      */ 
/*      */   
/*      */   public ValueLob addTemporaryLob(ValueLob paramValueLob) {
/* 1692 */     LobData lobData = paramValueLob.getLobData();
/* 1693 */     if (lobData instanceof org.h2.value.lob.LobDataInMemory) {
/* 1694 */       return paramValueLob;
/*      */     }
/* 1696 */     int i = ((LobDataDatabase)lobData).getTableId();
/* 1697 */     if (i == -3 || i == -2) {
/* 1698 */       if (this.temporaryResultLobs == null) {
/* 1699 */         this.temporaryResultLobs = new LinkedList<>();
/*      */       }
/* 1701 */       this.temporaryResultLobs.add(new TimeoutValue(paramValueLob));
/*      */     } else {
/* 1703 */       if (this.temporaryLobs == null) {
/* 1704 */         this.temporaryLobs = new ArrayList<>();
/*      */       }
/* 1706 */       this.temporaryLobs.add(paramValueLob);
/*      */     } 
/* 1708 */     return paramValueLob;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isRemote() {
/* 1713 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void markTableForAnalyze(Table paramTable) {
/* 1722 */     if (this.tablesToAnalyze == null) {
/* 1723 */       this.tablesToAnalyze = new HashSet<>();
/*      */     }
/* 1725 */     this.tablesToAnalyze.add(paramTable);
/*      */   }
/*      */   
/*      */   public State getState() {
/* 1729 */     return (getBlockingSessionId() != 0) ? State.BLOCKED : this.state.get();
/*      */   }
/*      */   
/*      */   public int getBlockingSessionId() {
/* 1733 */     return (this.transaction == null) ? 0 : this.transaction.getBlockerId();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void onRollback(MVMap<Object, VersionedValue<Object>> paramMVMap, Object paramObject, VersionedValue<Object> paramVersionedValue1, VersionedValue<Object> paramVersionedValue2) {
/* 1742 */     Store store = this.database.getStore();
/* 1743 */     MVTable mVTable = store.getTable(paramMVMap.getName());
/* 1744 */     if (mVTable != null) {
/* 1745 */       Row row1 = (paramVersionedValue1 == null) ? null : (Row)paramVersionedValue1.getCurrentValue();
/* 1746 */       Row row2 = (paramVersionedValue2 == null) ? null : (Row)paramVersionedValue2.getCurrentValue();
/* 1747 */       mVTable.fireAfterRow(this, row1, row2, true);
/*      */       
/* 1749 */       if (mVTable.getContainsLargeObject()) {
/* 1750 */         if (row1 != null) {
/* 1751 */           byte b; int i; for (b = 0, i = row1.getColumnCount(); b < i; b++) {
/* 1752 */             Value value = row1.getValue(b);
/* 1753 */             if (value instanceof ValueLob) {
/* 1754 */               removeAtCommit((ValueLob)value);
/*      */             }
/*      */           } 
/*      */         } 
/* 1758 */         if (row2 != null) {
/* 1759 */           byte b; int i; for (b = 0, i = row2.getColumnCount(); b < i; b++) {
/* 1760 */             Value value = row2.getValue(b);
/* 1761 */             if (value instanceof ValueLob) {
/* 1762 */               removeAtCommitStop((ValueLob)value);
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class Savepoint
/*      */   {
/*      */     int logIndex;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     long transactionSavepoint;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class TimeoutValue
/*      */   {
/* 1795 */     final long created = System.nanoTime();
/*      */ 
/*      */     
/*      */     final ValueLob value;
/*      */ 
/*      */ 
/*      */     
/*      */     TimeoutValue(ValueLob param1ValueLob) {
/* 1803 */       this.value = param1ValueLob;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public NetworkConnectionInfo getNetworkConnectionInfo() {
/* 1814 */     return this.networkConnectionInfo;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNetworkConnectionInfo(NetworkConnectionInfo paramNetworkConnectionInfo) {
/* 1819 */     this.networkConnectionInfo = paramNetworkConnectionInfo;
/*      */   }
/*      */ 
/*      */   
/*      */   public ValueTimestampTimeZone currentTimestamp() {
/* 1824 */     ValueTimestampTimeZone valueTimestampTimeZone = this.currentTimestamp;
/* 1825 */     if (valueTimestampTimeZone == null) {
/* 1826 */       this.currentTimestamp = valueTimestampTimeZone = DateTimeUtils.currentTimestamp(this.timeZone, this.commandStartOrEnd);
/*      */     }
/* 1828 */     return valueTimestampTimeZone;
/*      */   }
/*      */ 
/*      */   
/*      */   public Mode getMode() {
/* 1833 */     return this.database.getMode();
/*      */   }
/*      */ 
/*      */   
/*      */   public JavaObjectSerializer getJavaObjectSerializer() {
/* 1838 */     return this.database.getJavaObjectSerializer();
/*      */   }
/*      */ 
/*      */   
/*      */   public IsolationLevel getIsolationLevel() {
/* 1843 */     return this.isolationLevel;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setIsolationLevel(IsolationLevel paramIsolationLevel) {
/* 1848 */     commit(false);
/* 1849 */     this.isolationLevel = paramIsolationLevel;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BitSet getNonKeywords() {
/* 1858 */     return this.nonKeywords;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNonKeywords(BitSet paramBitSet) {
/* 1867 */     this.nonKeywords = paramBitSet;
/*      */   }
/*      */ 
/*      */   
/*      */   public Session.StaticSettings getStaticSettings() {
/* 1872 */     Session.StaticSettings staticSettings = this.staticSettings;
/* 1873 */     if (staticSettings == null) {
/* 1874 */       DbSettings dbSettings = this.database.getSettings();
/* 1875 */       this.staticSettings = staticSettings = new Session.StaticSettings(dbSettings.databaseToUpper, dbSettings.databaseToLower, dbSettings.caseInsensitiveIdentifiers);
/*      */     } 
/*      */     
/* 1878 */     return staticSettings;
/*      */   }
/*      */ 
/*      */   
/*      */   public Session.DynamicSettings getDynamicSettings() {
/* 1883 */     return new Session.DynamicSettings(this.database.getMode(), this.timeZone);
/*      */   }
/*      */ 
/*      */   
/*      */   public TimeZoneProvider currentTimeZone() {
/* 1888 */     return this.timeZone;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTimeZone(TimeZoneProvider paramTimeZoneProvider) {
/* 1897 */     if (!paramTimeZoneProvider.equals(this.timeZone)) {
/* 1898 */       this.timeZone = paramTimeZoneProvider;
/* 1899 */       ValueTimestampTimeZone valueTimestampTimeZone = this.currentTimestamp;
/* 1900 */       if (valueTimestampTimeZone != null) {
/* 1901 */         long l1 = valueTimestampTimeZone.getDateValue();
/* 1902 */         long l2 = valueTimestampTimeZone.getTimeNanos();
/* 1903 */         int i = valueTimestampTimeZone.getTimeZoneOffsetSeconds();
/* 1904 */         this.currentTimestamp = DateTimeUtils.timestampTimeZoneAtOffset(l1, l2, i, paramTimeZoneProvider
/* 1905 */             .getTimeZoneOffsetUTC(
/* 1906 */               DateTimeUtils.getEpochSeconds(l1, l2, i)));
/*      */       } 
/* 1908 */       this.modificationId++;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean areEqual(Value paramValue1, Value paramValue2) {
/* 1921 */     return (paramValue1.compareTo(paramValue2, this, this.database.getCompareMode()) == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int compare(Value paramValue1, Value paramValue2) {
/* 1934 */     return paramValue1.compareTo(paramValue2, this, this.database.getCompareMode());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int compareWithNull(Value paramValue1, Value paramValue2, boolean paramBoolean) {
/* 1949 */     return paramValue1.compareWithNull(paramValue2, paramBoolean, this, this.database.getCompareMode());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int compareTypeSafe(Value paramValue1, Value paramValue2) {
/* 1962 */     return paramValue1.compareTypeSafe(paramValue2, this.database.getCompareMode(), this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTruncateLargeLength(boolean paramBoolean) {
/* 1973 */     this.truncateLargeLength = paramBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTruncateLargeLength() {
/* 1983 */     return this.truncateLargeLength;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setVariableBinary(boolean paramBoolean) {
/* 1994 */     this.variableBinary = paramBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isVariableBinary() {
/* 2004 */     return this.variableBinary;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOldInformationSchema(boolean paramBoolean) {
/* 2015 */     this.oldInformationSchema = paramBoolean;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isOldInformationSchema() {
/* 2020 */     return this.oldInformationSchema;
/*      */   }
/*      */ 
/*      */   
/*      */   public DatabaseMeta getDatabaseMeta() {
/* 2025 */     return (DatabaseMeta)new DatabaseMetaLocal(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean zeroBasedEnums() {
/* 2030 */     return this.database.zeroBasedEnums();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setQuirksMode(boolean paramBoolean) {
/* 2040 */     this.quirksMode = paramBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isQuirksMode() {
/* 2050 */     return (this.quirksMode || this.database.isStarting());
/*      */   }
/*      */ 
/*      */   
/*      */   public Session setThreadLocalSession() {
/* 2055 */     Session session = THREAD_LOCAL_SESSION.get();
/* 2056 */     THREAD_LOCAL_SESSION.set(this);
/* 2057 */     return session;
/*      */   }
/*      */ 
/*      */   
/*      */   public void resetThreadLocalSession(Session paramSession) {
/* 2062 */     if (paramSession == null) {
/* 2063 */       THREAD_LOCAL_SESSION.remove();
/*      */     } else {
/* 2065 */       THREAD_LOCAL_SESSION.set(paramSession);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\SessionLocal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */