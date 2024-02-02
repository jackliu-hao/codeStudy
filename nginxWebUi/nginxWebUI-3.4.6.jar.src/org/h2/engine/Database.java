/*      */ package org.h2.engine;
/*      */ 
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.BitSet;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ import org.h2.api.DatabaseEventListener;
/*      */ import org.h2.api.JavaObjectSerializer;
/*      */ import org.h2.api.TableEngine;
/*      */ import org.h2.command.Prepared;
/*      */ import org.h2.command.ddl.CreateTableData;
/*      */ import org.h2.command.dml.SetTypes;
/*      */ import org.h2.constraint.Constraint;
/*      */ import org.h2.index.Cursor;
/*      */ import org.h2.index.Index;
/*      */ import org.h2.index.IndexType;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.message.Trace;
/*      */ import org.h2.message.TraceSystem;
/*      */ import org.h2.mode.DefaultNullOrdering;
/*      */ import org.h2.mode.PgCatalogSchema;
/*      */ import org.h2.mvstore.MVStoreException;
/*      */ import org.h2.mvstore.db.LobStorageMap;
/*      */ import org.h2.mvstore.db.Store;
/*      */ import org.h2.result.Row;
/*      */ import org.h2.result.RowFactory;
/*      */ import org.h2.result.SearchRow;
/*      */ import org.h2.schema.InformationSchema;
/*      */ import org.h2.schema.Schema;
/*      */ import org.h2.schema.SchemaObject;
/*      */ import org.h2.schema.Sequence;
/*      */ import org.h2.schema.TriggerObject;
/*      */ import org.h2.security.auth.Authenticator;
/*      */ import org.h2.store.DataHandler;
/*      */ import org.h2.store.FileLock;
/*      */ import org.h2.store.FileLockMethod;
/*      */ import org.h2.store.FileStore;
/*      */ import org.h2.store.InDoubtTransaction;
/*      */ import org.h2.store.LobStorageInterface;
/*      */ import org.h2.store.fs.FileUtils;
/*      */ import org.h2.table.Column;
/*      */ import org.h2.table.IndexColumn;
/*      */ import org.h2.table.Table;
/*      */ import org.h2.table.TableLinkConnection;
/*      */ import org.h2.table.TableSynonym;
/*      */ import org.h2.table.TableType;
/*      */ import org.h2.table.TableView;
/*      */ import org.h2.tools.DeleteDbFiles;
/*      */ import org.h2.tools.Server;
/*      */ import org.h2.util.JdbcUtils;
/*      */ import org.h2.util.MathUtils;
/*      */ import org.h2.util.NetUtils;
/*      */ import org.h2.util.NetworkConnectionInfo;
/*      */ import org.h2.util.SmallLRUCache;
/*      */ import org.h2.util.SourceCompiler;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.TempFileDeleter;
/*      */ import org.h2.util.TimeZoneProvider;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.value.CaseInsensitiveConcurrentMap;
/*      */ import org.h2.value.CaseInsensitiveMap;
/*      */ import org.h2.value.CompareMode;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueTimestampTimeZone;
/*      */ 
/*      */ public final class Database
/*      */   implements DataHandler, CastDataProvider {
/*      */   private static int initialPowerOffCount;
/*      */   private static final boolean ASSERT;
/*      */   private static final ThreadLocal<SessionLocal> META_LOCK_DEBUGGING;
/*      */   private static final ThreadLocal<Database> META_LOCK_DEBUGGING_DB;
/*      */   private static final ThreadLocal<Throwable> META_LOCK_DEBUGGING_STACK;
/*      */   private static final String SYSTEM_USER_NAME = "DBA";
/*      */   private final boolean persistent;
/*      */   private final String databaseName;
/*      */   private final String databaseShortName;
/*      */   private final String databaseURL;
/*      */   private final String cipher;
/*      */   private final byte[] filePasswordHash;
/*      */   private final byte[] fileEncryptionKey;
/*      */   private final ConcurrentHashMap<String, RightOwner> usersAndRoles;
/*      */   private final ConcurrentHashMap<String, Setting> settings;
/*      */   private final ConcurrentHashMap<String, Schema> schemas;
/*      */   private final ConcurrentHashMap<String, Right> rights;
/*      */   private final ConcurrentHashMap<String, Comment> comments;
/*      */   private final HashMap<String, TableEngine> tableEngines;
/*      */   private final Set<SessionLocal> userSessions;
/*      */   private final AtomicReference<SessionLocal> exclusiveSession;
/*      */   private final BitSet objectIds;
/*      */   private final Object lobSyncObject;
/*      */   private final Schema mainSchema;
/*      */   private final Schema infoSchema;
/*  104 */   private static final SessionLocal[] EMPTY_SESSION_ARRAY = new SessionLocal[0]; private final Schema pgCatalogSchema; private int nextSessionId; private int nextTempTableId; private final User systemUser; private SessionLocal systemSession; private SessionLocal lobSession; private final Table meta; private final Index metaIdIndex; private FileLock lock; private volatile boolean starting; private final TraceSystem traceSystem; private final Trace trace; private final FileLockMethod fileLockMethod; private final Role publicRole; private final AtomicLong modificationDataId; private final AtomicLong modificationMetaId; private final AtomicLong remoteSettingsId; private CompareMode compareMode; private String cluster; private boolean readOnly;
/*      */   
/*      */   static {
/*  107 */     boolean bool = false;
/*      */     
/*  109 */     assert bool = true;
/*  110 */     ASSERT = bool;
/*  111 */     if (bool) {
/*  112 */       META_LOCK_DEBUGGING = new ThreadLocal<>();
/*  113 */       META_LOCK_DEBUGGING_DB = new ThreadLocal<>();
/*  114 */       META_LOCK_DEBUGGING_STACK = new ThreadLocal<>();
/*      */     } else {
/*  116 */       META_LOCK_DEBUGGING = null;
/*  117 */       META_LOCK_DEBUGGING_DB = null;
/*  118 */       META_LOCK_DEBUGGING_STACK = null;
/*      */     } 
/*      */   }
/*      */   private DatabaseEventListener eventListener; private int maxMemoryRows; private int lockMode; private int maxLengthInplaceLob; private int allowLiterals; private int powerOffCount; private volatile int closeDelay; private DelayedDatabaseCloser delayedCloser; private volatile boolean closing;
/*      */   private boolean ignoreCase;
/*      */   private boolean deleteFilesOnDisconnect;
/*      */   private boolean optimizeReuseResults;
/*      */   private final String cacheType;
/*      */   private boolean referentialIntegrity;
/*      */   private Mode mode;
/*      */   private DefaultNullOrdering defaultNullOrdering;
/*      */   private int maxOperationMemory;
/*      */   private SmallLRUCache<String, String[]> lobFileListCache;
/*      */   private final boolean autoServerMode;
/*      */   private final int autoServerPort;
/*      */   
/*      */   public Database(ConnectionInfo paramConnectionInfo, String paramString) {
/*      */     String str5;
/*  136 */     this.usersAndRoles = new ConcurrentHashMap<>();
/*  137 */     this.settings = new ConcurrentHashMap<>();
/*  138 */     this.schemas = new ConcurrentHashMap<>();
/*  139 */     this.rights = new ConcurrentHashMap<>();
/*  140 */     this.comments = new ConcurrentHashMap<>();
/*      */     
/*  142 */     this.tableEngines = new HashMap<>();
/*      */     
/*  144 */     this.userSessions = Collections.synchronizedSet(new HashSet<>());
/*  145 */     this.exclusiveSession = new AtomicReference<>();
/*  146 */     this.objectIds = new BitSet();
/*  147 */     this.lobSyncObject = new Object();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  165 */     this.modificationDataId = new AtomicLong();
/*  166 */     this.modificationMetaId = new AtomicLong();
/*      */ 
/*      */ 
/*      */     
/*  170 */     this.remoteSettingsId = new AtomicLong();
/*      */     
/*  172 */     this.cluster = "''";
/*      */ 
/*      */     
/*  175 */     this.maxMemoryRows = SysProperties.MAX_MEMORY_ROWS;
/*      */ 
/*      */     
/*  178 */     this.allowLiterals = 2;
/*      */     
/*  180 */     this.powerOffCount = initialPowerOffCount;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  186 */     this.optimizeReuseResults = true;
/*      */     
/*  188 */     this.referentialIntegrity = true;
/*  189 */     this.mode = Mode.getRegular();
/*  190 */     this.defaultNullOrdering = DefaultNullOrdering.LOW;
/*  191 */     this.maxOperationMemory = 100000;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  198 */     this.tempFileDeleter = TempFileDeleter.getInstance();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  203 */     this.defaultTableType = 0;
/*      */ 
/*      */ 
/*      */     
/*  207 */     this.backgroundException = new AtomicReference<>();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  212 */     this.queryStatisticsMaxEntries = 100;
/*      */     
/*  214 */     this.rowFactory = RowFactory.getRowFactory();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  220 */     if (ASSERT) {
/*  221 */       META_LOCK_DEBUGGING.set(null);
/*  222 */       META_LOCK_DEBUGGING_DB.set(null);
/*  223 */       META_LOCK_DEBUGGING_STACK.set(null);
/*      */     } 
/*  225 */     String str1 = paramConnectionInfo.getName();
/*  226 */     this.dbSettings = paramConnectionInfo.getDbSettings();
/*  227 */     this.compareMode = CompareMode.getInstance(null, 0);
/*  228 */     this.persistent = paramConnectionInfo.isPersistent();
/*  229 */     this.filePasswordHash = paramConnectionInfo.getFilePasswordHash();
/*  230 */     this.fileEncryptionKey = paramConnectionInfo.getFileEncryptionKey();
/*  231 */     this.databaseName = str1;
/*  232 */     this.databaseShortName = parseDatabaseShortName();
/*  233 */     this.maxLengthInplaceLob = 256;
/*  234 */     this.cipher = paramString;
/*  235 */     this.autoServerMode = paramConnectionInfo.getProperty("AUTO_SERVER", false);
/*  236 */     this.autoServerPort = paramConnectionInfo.getProperty("AUTO_SERVER_PORT", 0);
/*  237 */     this.pageSize = paramConnectionInfo.getProperty("PAGE_SIZE", 4096);
/*  238 */     if (paramString != null && this.pageSize % 4096 != 0) {
/*  239 */       throw DbException.getUnsupportedException("CIPHER && PAGE_SIZE=" + this.pageSize);
/*      */     }
/*  241 */     String str2 = StringUtils.toLowerEnglish(paramConnectionInfo.getProperty("ACCESS_MODE_DATA", "rw"));
/*  242 */     if ("r".equals(str2)) {
/*  243 */       this.readOnly = true;
/*      */     }
/*  245 */     String str3 = paramConnectionInfo.getProperty("FILE_LOCK", (String)null);
/*  246 */     this.fileLockMethod = (str3 != null) ? FileLock.getFileLockMethod(str3) : (this.autoServerMode ? FileLockMethod.FILE : FileLockMethod.FS);
/*      */     
/*  248 */     this.databaseURL = paramConnectionInfo.getURL();
/*  249 */     String str4 = paramConnectionInfo.removeProperty("DATABASE_EVENT_LISTENER", (String)null);
/*  250 */     if (str4 != null) {
/*  251 */       setEventListenerClass(StringUtils.trim(str4, true, true, "'"));
/*      */     }
/*  253 */     str4 = paramConnectionInfo.removeProperty("MODE", (String)null);
/*  254 */     if (str4 != null) {
/*  255 */       this.mode = Mode.getInstance(str4);
/*  256 */       if (this.mode == null) {
/*  257 */         throw DbException.get(90088, str4);
/*      */       }
/*      */     } 
/*  260 */     str4 = paramConnectionInfo.removeProperty("DEFAULT_NULL_ORDERING", (String)null);
/*  261 */     if (str4 != null) {
/*      */       try {
/*  263 */         this.defaultNullOrdering = DefaultNullOrdering.valueOf(StringUtils.toUpperEnglish(str4));
/*  264 */       } catch (RuntimeException runtimeException) {
/*  265 */         throw DbException.getInvalidValueException("DEFAULT_NULL_ORDERING", str4);
/*      */       } 
/*      */     }
/*  268 */     str4 = paramConnectionInfo.getProperty("JAVA_OBJECT_SERIALIZER", (String)null);
/*  269 */     if (str4 != null) {
/*  270 */       str4 = StringUtils.trim(str4, true, true, "'");
/*  271 */       this.javaObjectSerializerName = str4;
/*      */     } 
/*  273 */     this.allowBuiltinAliasOverride = paramConnectionInfo.getProperty("BUILTIN_ALIAS_OVERRIDE", false);
/*  274 */     boolean bool = this.dbSettings.dbCloseOnExit;
/*  275 */     int i = paramConnectionInfo.getIntProperty(9, 1);
/*  276 */     int j = paramConnectionInfo.getIntProperty(8, 0);
/*      */     
/*  278 */     this.cacheType = StringUtils.toUpperEnglish(paramConnectionInfo.removeProperty("CACHE_TYPE", "LRU"));
/*  279 */     this.ignoreCatalogs = paramConnectionInfo.getProperty("IGNORE_CATALOGS", this.dbSettings.ignoreCatalogs);
/*  280 */     this.lockMode = paramConnectionInfo.getProperty("LOCK_MODE", 3);
/*      */     
/*  282 */     if (this.persistent) {
/*  283 */       if (this.readOnly) {
/*  284 */         if (i >= 3) {
/*  285 */           str5 = Utils.getProperty("java.io.tmpdir", ".") + "/h2_" + System.currentTimeMillis() + ".trace.db";
/*      */         } else {
/*      */           
/*  288 */           str5 = null;
/*      */         } 
/*      */       } else {
/*  291 */         str5 = str1 + ".trace.db";
/*      */       } 
/*      */     } else {
/*  294 */       str5 = null;
/*      */     } 
/*  296 */     this.traceSystem = new TraceSystem(str5);
/*  297 */     this.traceSystem.setLevelFile(i);
/*  298 */     this.traceSystem.setLevelSystemOut(j);
/*  299 */     this.trace = this.traceSystem.getTrace(2);
/*  300 */     this.trace.info("opening {0} (build {1})", new Object[] { str1, Integer.valueOf(210) });
/*      */     try {
/*  302 */       if (this.autoServerMode && (this.readOnly || !this.persistent || this.fileLockMethod == FileLockMethod.NO || this.fileLockMethod == FileLockMethod.FS))
/*      */       {
/*  304 */         throw DbException.getUnsupportedException("AUTO_SERVER=TRUE && (readOnly || inMemory || FILE_LOCK=NO || FILE_LOCK=FS)");
/*      */       }
/*      */       
/*  307 */       if (this.persistent) {
/*  308 */         String str = str1 + ".lock.db";
/*  309 */         if (this.readOnly) {
/*  310 */           if (FileUtils.exists(str)) {
/*  311 */             throw DbException.get(90020, "Lock file exists: " + str);
/*      */           }
/*  313 */         } else if (this.fileLockMethod != FileLockMethod.NO && this.fileLockMethod != FileLockMethod.FS) {
/*  314 */           this.lock = new FileLock(this.traceSystem, str, 1000);
/*  315 */           this.lock.lock(this.fileLockMethod);
/*  316 */           if (this.autoServerMode) {
/*  317 */             startServer(this.lock.getUniqueId());
/*      */           }
/*      */         } 
/*  320 */         deleteOldTempFiles();
/*      */       } 
/*  322 */       this.starting = true;
/*  323 */       if (this.dbSettings.mvStore) {
/*  324 */         this.store = new Store(this);
/*      */       } else {
/*  326 */         throw new UnsupportedOperationException();
/*      */       } 
/*  328 */       this.starting = false;
/*  329 */       this.systemUser = new User(this, 0, "DBA", true);
/*  330 */       this.systemUser.setAdmin(true);
/*  331 */       this.mainSchema = new Schema(this, 0, sysIdentifier("PUBLIC"), this.systemUser, true);
/*      */       
/*  333 */       this.infoSchema = (Schema)new InformationSchema(this, this.systemUser);
/*  334 */       this.schemas.put(this.mainSchema.getName(), this.mainSchema);
/*  335 */       this.schemas.put(this.infoSchema.getName(), this.infoSchema);
/*  336 */       if (this.mode.getEnum() == Mode.ModeEnum.PostgreSQL) {
/*  337 */         this.pgCatalogSchema = (Schema)new PgCatalogSchema(this, this.systemUser);
/*  338 */         this.schemas.put(this.pgCatalogSchema.getName(), this.pgCatalogSchema);
/*      */       } else {
/*  340 */         this.pgCatalogSchema = null;
/*      */       } 
/*  342 */       this.publicRole = new Role(this, 0, sysIdentifier("PUBLIC"), true);
/*  343 */       this.usersAndRoles.put(this.publicRole.getName(), this.publicRole);
/*  344 */       this.systemSession = createSession(this.systemUser);
/*  345 */       this.lobSession = createSession(this.systemUser);
/*  346 */       Set<String> set = this.dbSettings.getSettings().keySet();
/*  347 */       this.store.getTransactionStore().init(this.lobSession);
/*  348 */       set.removeIf(paramString -> paramString.startsWith("PAGE_STORE_"));
/*  349 */       CreateTableData createTableData = createSysTableData();
/*  350 */       this.starting = true;
/*  351 */       this.meta = this.mainSchema.createTable(createTableData);
/*  352 */       IndexColumn[] arrayOfIndexColumn = IndexColumn.wrap(new Column[] { createTableData.columns.get(0) });
/*  353 */       this.metaIdIndex = this.meta.addIndex(this.systemSession, "SYS_ID", 0, arrayOfIndexColumn, 1, 
/*  354 */           IndexType.createPrimaryKey(false, false), true, null);
/*  355 */       this.systemSession.commit(true);
/*  356 */       this.objectIds.set(0);
/*  357 */       executeMeta();
/*  358 */       this.systemSession.commit(true);
/*  359 */       this.store.getTransactionStore().endLeftoverTransactions();
/*  360 */       this.store.removeTemporaryMaps(this.objectIds);
/*  361 */       recompileInvalidViews();
/*  362 */       this.starting = false;
/*  363 */       if (!this.readOnly) {
/*      */         
/*  365 */         String str = SetTypes.getTypeName(28);
/*  366 */         Setting setting = this.settings.get(str);
/*  367 */         if (setting == null) {
/*  368 */           setting = new Setting(this, allocateObjectId(), str);
/*  369 */           setting.setIntValue(210);
/*  370 */           lockMeta(this.systemSession);
/*  371 */           addDatabaseObject(this.systemSession, setting);
/*      */         } 
/*      */       } 
/*  374 */       this.lobStorage = (LobStorageInterface)new LobStorageMap(this);
/*  375 */       this.lobSession.commit(true);
/*  376 */       this.systemSession.commit(true);
/*  377 */       this.trace.info("opened {0}", new Object[] { str1 });
/*  378 */       if (this.persistent) {
/*  379 */         int k = paramConnectionInfo.getProperty("WRITE_DELAY", 500);
/*  380 */         setWriteDelay(k);
/*      */       } 
/*  382 */       if (bool) {
/*  383 */         OnExitDatabaseCloser.register(this);
/*      */       }
/*  385 */     } catch (Throwable throwable) {
/*      */       try {
/*  387 */         if (throwable instanceof OutOfMemoryError) {
/*  388 */           throwable.fillInStackTrace();
/*      */         }
/*  390 */         if (throwable instanceof DbException) {
/*  391 */           if (((DbException)throwable).getErrorCode() == 90020) {
/*  392 */             stopServer();
/*      */           } else {
/*      */             
/*  395 */             this.trace.error(throwable, "opening {0}", new Object[] { str1 });
/*      */           } 
/*      */         }
/*  398 */         this.traceSystem.close();
/*  399 */         closeOpenFilesAndUnlock();
/*  400 */       } catch (Throwable throwable1) {
/*  401 */         throwable.addSuppressed(throwable1);
/*      */       } 
/*  403 */       throw DbException.convert(throwable);
/*      */     } 
/*      */   }
/*      */   private Server server; private HashMap<TableLinkConnection, TableLinkConnection> linkConnections; private final TempFileDeleter tempFileDeleter; private int compactMode; private SourceCompiler compiler; private final LobStorageInterface lobStorage; private final int pageSize; private int defaultTableType; private final DbSettings dbSettings; private final Store store; private boolean allowBuiltinAliasOverride; private final AtomicReference<DbException> backgroundException; private JavaObjectSerializer javaObjectSerializer; private String javaObjectSerializerName; private volatile boolean javaObjectSerializerInitialized; private boolean queryStatistics; private int queryStatisticsMaxEntries; private QueryStatisticsData queryStatisticsData; private RowFactory rowFactory; private boolean ignoreCatalogs; private Authenticator authenticator;
/*      */   public int getLockTimeout() {
/*  408 */     Setting setting = findSetting(SetTypes.getTypeName(5));
/*  409 */     return (setting == null) ? 2000 : setting.getIntValue();
/*      */   }
/*      */   
/*      */   public RowFactory getRowFactory() {
/*  413 */     return this.rowFactory;
/*      */   }
/*      */   
/*      */   public void setRowFactory(RowFactory paramRowFactory) {
/*  417 */     this.rowFactory = paramRowFactory;
/*      */   }
/*      */   
/*      */   public static void setInitialPowerOffCount(int paramInt) {
/*  421 */     initialPowerOffCount = paramInt;
/*      */   }
/*      */   
/*      */   public void setPowerOffCount(int paramInt) {
/*  425 */     if (this.powerOffCount == -1) {
/*      */       return;
/*      */     }
/*  428 */     this.powerOffCount = paramInt;
/*      */   }
/*      */   
/*      */   public Store getStore() {
/*  432 */     return this.store;
/*      */   }
/*      */   
/*      */   public long getModificationDataId() {
/*  436 */     return this.modificationDataId.get();
/*      */   }
/*      */   
/*      */   public long getNextModificationDataId() {
/*  440 */     return this.modificationDataId.incrementAndGet();
/*      */   }
/*      */   
/*      */   public long getModificationMetaId() {
/*  444 */     return this.modificationMetaId.get();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public long getNextModificationMetaId() {
/*  450 */     this.modificationDataId.incrementAndGet();
/*  451 */     return this.modificationMetaId.incrementAndGet() - 1L;
/*      */   }
/*      */   
/*      */   public long getRemoteSettingsId() {
/*  455 */     return this.remoteSettingsId.get();
/*      */   }
/*      */   
/*      */   public long getNextRemoteSettingsId() {
/*  459 */     return this.remoteSettingsId.incrementAndGet();
/*      */   }
/*      */   
/*      */   public int getPowerOffCount() {
/*  463 */     return this.powerOffCount;
/*      */   }
/*      */ 
/*      */   
/*      */   public void checkPowerOff() {
/*  468 */     if (this.powerOffCount != 0) {
/*  469 */       checkPowerOff2();
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkPowerOff2() {
/*  474 */     if (this.powerOffCount > 1) {
/*  475 */       this.powerOffCount--;
/*      */       return;
/*      */     } 
/*  478 */     if (this.powerOffCount != -1) {
/*      */       try {
/*  480 */         this.powerOffCount = -1;
/*  481 */         this.store.closeImmediately();
/*  482 */         if (this.lock != null) {
/*  483 */           stopServer();
/*      */           
/*  485 */           this.lock.unlock();
/*  486 */           this.lock = null;
/*      */         } 
/*  488 */         if (this.traceSystem != null) {
/*  489 */           this.traceSystem.close();
/*      */         }
/*  491 */       } catch (DbException dbException) {
/*  492 */         DbException.traceThrowable((Throwable)dbException);
/*      */       } 
/*      */     }
/*  495 */     Engine.close(this.databaseName);
/*  496 */     throw DbException.get(90098);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Trace getTrace(int paramInt) {
/*  506 */     return this.traceSystem.getTrace(paramInt);
/*      */   }
/*      */ 
/*      */   
/*      */   public FileStore openFile(String paramString1, String paramString2, boolean paramBoolean) {
/*  511 */     if (paramBoolean && !FileUtils.exists(paramString1)) {
/*  512 */       throw DbException.get(90124, paramString1);
/*      */     }
/*  514 */     FileStore fileStore = FileStore.open(this, paramString1, paramString2, this.cipher, this.filePasswordHash);
/*      */     
/*      */     try {
/*  517 */       fileStore.init();
/*  518 */     } catch (DbException dbException) {
/*  519 */       fileStore.closeSilently();
/*  520 */       throw dbException;
/*      */     } 
/*  522 */     return fileStore;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean validateFilePasswordHash(String paramString, byte[] paramArrayOfbyte) {
/*  533 */     if (!Objects.equals(paramString, this.cipher)) {
/*  534 */       return false;
/*      */     }
/*  536 */     return Utils.compareSecure(paramArrayOfbyte, this.filePasswordHash);
/*      */   }
/*      */   
/*      */   private String parseDatabaseShortName() {
/*  540 */     String str = this.databaseName;
/*  541 */     int i = str.length(), j = i;
/*  542 */     while (--j >= 0) {
/*  543 */       char c = str.charAt(j);
/*  544 */       switch (c) {
/*      */         case '/':
/*      */         case ':':
/*      */         case '\\':
/*      */           break;
/*      */       } 
/*      */     } 
/*  551 */     str = (++j == i) ? "UNNAMED" : str.substring(j);
/*  552 */     return StringUtils.truncateString(this.dbSettings.databaseToUpper ? 
/*  553 */         StringUtils.toUpperEnglish(str) : (this.dbSettings.databaseToLower ? 
/*  554 */         StringUtils.toLowerEnglish(str) : str), 256);
/*      */   }
/*      */ 
/*      */   
/*      */   private CreateTableData createSysTableData() {
/*  559 */     CreateTableData createTableData = new CreateTableData();
/*  560 */     ArrayList<Column> arrayList = createTableData.columns;
/*  561 */     Column column = new Column("ID", TypeInfo.TYPE_INTEGER);
/*  562 */     column.setNullable(false);
/*  563 */     arrayList.add(column);
/*  564 */     arrayList.add(new Column("HEAD", TypeInfo.TYPE_INTEGER));
/*  565 */     arrayList.add(new Column("TYPE", TypeInfo.TYPE_INTEGER));
/*  566 */     arrayList.add(new Column("SQL", TypeInfo.TYPE_VARCHAR));
/*  567 */     createTableData.tableName = "SYS";
/*  568 */     createTableData.id = 0;
/*  569 */     createTableData.temporary = false;
/*  570 */     createTableData.persistData = this.persistent;
/*  571 */     createTableData.persistIndexes = this.persistent;
/*  572 */     createTableData.isHidden = true;
/*  573 */     createTableData.session = this.systemSession;
/*  574 */     return createTableData;
/*      */   }
/*      */   
/*      */   private void executeMeta() {
/*  578 */     Cursor cursor = this.metaIdIndex.find(this.systemSession, null, null);
/*  579 */     ArrayList<MetaRecord> arrayList1 = new ArrayList(), arrayList2 = new ArrayList();
/*  580 */     ArrayList<MetaRecord> arrayList3 = new ArrayList(), arrayList4 = new ArrayList();
/*  581 */     ArrayList<MetaRecord> arrayList5 = new ArrayList();
/*  582 */     while (cursor.next()) {
/*  583 */       MetaRecord metaRecord = new MetaRecord((SearchRow)cursor.get());
/*  584 */       this.objectIds.set(metaRecord.getId());
/*  585 */       switch (metaRecord.getObjectType()) {
/*      */         case 2:
/*      */         case 6:
/*      */         case 9:
/*      */         case 10:
/*  590 */           arrayList1.add(metaRecord);
/*      */           continue;
/*      */         case 12:
/*  593 */           arrayList2.add(metaRecord);
/*      */           continue;
/*      */         case 0:
/*      */         case 1:
/*      */         case 3:
/*      */         case 11:
/*  599 */           arrayList3.add(metaRecord);
/*      */           continue;
/*      */         case 5:
/*  602 */           arrayList4.add(metaRecord);
/*      */           continue;
/*      */       } 
/*  605 */       arrayList5.add(metaRecord);
/*      */     } 
/*      */     
/*  608 */     synchronized (this.systemSession) {
/*  609 */       executeMeta(arrayList1);
/*      */       
/*  611 */       int i = arrayList2.size();
/*  612 */       if (i > 0) {
/*  613 */         for (byte b = 0;; i = b) {
/*  614 */           DbException dbException = null;
/*  615 */           for (byte b1 = 0; b1 < i; b1++) {
/*  616 */             MetaRecord metaRecord = arrayList2.get(b1);
/*      */             try {
/*  618 */               metaRecord.prepareAndExecute(this, this.systemSession, this.eventListener);
/*  619 */             } catch (DbException dbException1) {
/*  620 */               if (dbException == null) {
/*  621 */                 dbException = dbException1;
/*      */               }
/*  623 */               arrayList2.set(b++, metaRecord);
/*      */             } 
/*      */           } 
/*  626 */           if (dbException == null) {
/*      */             break;
/*      */           }
/*  629 */           if (i == b) {
/*  630 */             throw dbException;
/*      */           }
/*      */         } 
/*      */       }
/*  634 */       executeMeta(arrayList3);
/*      */       
/*  636 */       i = arrayList4.size();
/*  637 */       if (i > 0) {
/*  638 */         ArrayList<Prepared> arrayList = new ArrayList(i);
/*  639 */         for (byte b = 0; b < i; b++) {
/*  640 */           Prepared prepared = ((MetaRecord)arrayList4.get(b)).prepare(this, this.systemSession, this.eventListener);
/*  641 */           if (prepared != null) {
/*  642 */             arrayList.add(prepared);
/*      */           }
/*      */         } 
/*  645 */         arrayList.sort(MetaRecord.CONSTRAINTS_COMPARATOR);
/*      */ 
/*      */         
/*  648 */         for (Prepared prepared : arrayList) {
/*  649 */           MetaRecord.execute(this, prepared, this.eventListener, prepared.getSQL());
/*      */         }
/*      */       } 
/*  652 */       executeMeta(arrayList5);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void executeMeta(ArrayList<MetaRecord> paramArrayList) {
/*  657 */     if (!paramArrayList.isEmpty()) {
/*  658 */       paramArrayList.sort(null);
/*  659 */       for (MetaRecord metaRecord : paramArrayList) {
/*  660 */         metaRecord.prepareAndExecute(this, this.systemSession, this.eventListener);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private void startServer(String paramString) {
/*      */     try {
/*  667 */       this.server = Server.createTcpServer(new String[] { "-tcpPort", 
/*  668 */             Integer.toString(this.autoServerPort), "-tcpAllowOthers", "-tcpDaemon", "-key", paramString, this.databaseName });
/*      */ 
/*      */ 
/*      */       
/*  672 */       this.server.start();
/*  673 */     } catch (SQLException sQLException) {
/*  674 */       throw DbException.convert(sQLException);
/*      */     } 
/*  676 */     String str1 = NetUtils.getLocalAddress();
/*  677 */     String str2 = str1 + ":" + this.server.getPort();
/*  678 */     this.lock.setProperty("server", str2);
/*  679 */     String str3 = NetUtils.getHostName(str1);
/*  680 */     this.lock.setProperty("hostName", str3);
/*  681 */     this.lock.save();
/*      */   }
/*      */   
/*      */   private void stopServer() {
/*  685 */     if (this.server != null) {
/*  686 */       Server server = this.server;
/*      */ 
/*      */ 
/*      */       
/*  690 */       this.server = null;
/*  691 */       server.stop();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void recompileInvalidViews()
/*      */   {
/*      */     while (true) {
/*  698 */       boolean bool = false;
/*  699 */       for (Schema schema : this.schemas.values()) {
/*  700 */         for (Table table : schema.getAllTablesAndViews(null)) {
/*  701 */           if (table instanceof TableView) {
/*  702 */             TableView tableView = (TableView)table;
/*  703 */             if (tableView.isInvalid()) {
/*  704 */               tableView.recompile(this.systemSession, true, false);
/*  705 */               if (!tableView.isInvalid()) {
/*  706 */                 bool = true;
/*      */               }
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*  712 */       if (!bool) {
/*  713 */         TableView.clearIndexCaches(this);
/*      */         return;
/*      */       } 
/*      */     }  } private void addMeta(SessionLocal paramSessionLocal, DbObject paramDbObject) {
/*  717 */     assert Thread.holdsLock(this);
/*  718 */     int i = paramDbObject.getId();
/*  719 */     if (i > 0 && !paramDbObject.isTemporary() && 
/*  720 */       !isReadOnly()) {
/*  721 */       Row row = this.meta.getTemplateRow();
/*  722 */       MetaRecord.populateRowFromDBObject(paramDbObject, (SearchRow)row);
/*  723 */       assert this.objectIds.get(i);
/*  724 */       if (SysProperties.CHECK) {
/*  725 */         verifyMetaLocked(paramSessionLocal);
/*      */       }
/*  727 */       Cursor cursor = this.metaIdIndex.find(paramSessionLocal, (SearchRow)row, (SearchRow)row);
/*  728 */       if (!cursor.next()) {
/*  729 */         this.meta.addRow(paramSessionLocal, row);
/*      */       } else {
/*  731 */         assert this.starting;
/*  732 */         Row row1 = cursor.get();
/*  733 */         MetaRecord metaRecord = new MetaRecord((SearchRow)row1);
/*  734 */         assert metaRecord.getId() == paramDbObject.getId();
/*  735 */         assert metaRecord.getObjectType() == paramDbObject.getType();
/*  736 */         if (!metaRecord.getSQL().equals(paramDbObject.getCreateSQLForMeta())) {
/*  737 */           this.meta.updateRow(paramSessionLocal, row1, row);
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
/*      */   public void verifyMetaLocked(SessionLocal paramSessionLocal) {
/*  750 */     if (this.lockMode != 0 && this.meta != null && !this.meta.isLockedExclusivelyBy(paramSessionLocal)) {
/*  751 */       throw DbException.getInternalError();
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
/*      */   public boolean lockMeta(SessionLocal paramSessionLocal) {
/*  766 */     if (this.meta == null) {
/*  767 */       return true;
/*      */     }
/*  769 */     if (ASSERT) {
/*  770 */       lockMetaAssertion(paramSessionLocal);
/*      */     }
/*  772 */     return this.meta.lock(paramSessionLocal, 2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void lockMetaAssertion(SessionLocal paramSessionLocal) {
/*  779 */     if (META_LOCK_DEBUGGING_DB.get() != null && META_LOCK_DEBUGGING_DB.get() != this) {
/*  780 */       SessionLocal sessionLocal = META_LOCK_DEBUGGING.get();
/*  781 */       if (sessionLocal == null) {
/*  782 */         META_LOCK_DEBUGGING.set(paramSessionLocal);
/*  783 */         META_LOCK_DEBUGGING_DB.set(this);
/*  784 */         META_LOCK_DEBUGGING_STACK.set(new Throwable("Last meta lock granted in this stack trace, this is debug information for following IllegalStateException"));
/*      */       }
/*  786 */       else if (sessionLocal != paramSessionLocal) {
/*  787 */         ((Throwable)META_LOCK_DEBUGGING_STACK.get()).printStackTrace();
/*  788 */         throw new IllegalStateException("meta currently locked by " + sessionLocal + ", sessionid=" + sessionLocal.getId() + " and trying to be locked by different session, " + paramSessionLocal + ", sessionid=" + paramSessionLocal
/*      */             
/*  790 */             .getId() + " on same thread");
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unlockMeta(SessionLocal paramSessionLocal) {
/*  801 */     if (this.meta != null) {
/*  802 */       unlockMetaDebug(paramSessionLocal);
/*  803 */       this.meta.unlock(paramSessionLocal);
/*  804 */       paramSessionLocal.unlock(this.meta);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static void unlockMetaDebug(SessionLocal paramSessionLocal) {
/*  815 */     if (ASSERT && 
/*  816 */       META_LOCK_DEBUGGING.get() == paramSessionLocal) {
/*  817 */       META_LOCK_DEBUGGING.set(null);
/*  818 */       META_LOCK_DEBUGGING_DB.set(null);
/*  819 */       META_LOCK_DEBUGGING_STACK.set(null);
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
/*      */   public void removeMeta(SessionLocal paramSessionLocal, int paramInt) {
/*  831 */     if (paramInt > 0 && !this.starting) {
/*  832 */       SearchRow searchRow = this.meta.getRowFactory().createRow();
/*  833 */       searchRow.setValue(0, (Value)ValueInteger.get(paramInt));
/*  834 */       boolean bool = lockMeta(paramSessionLocal);
/*      */       try {
/*  836 */         Cursor cursor = this.metaIdIndex.find(paramSessionLocal, searchRow, searchRow);
/*  837 */         if (cursor.next()) {
/*  838 */           Row row = cursor.get();
/*  839 */           this.meta.removeRow(paramSessionLocal, row);
/*  840 */           if (SysProperties.CHECK) {
/*  841 */             checkMetaFree(paramSessionLocal, paramInt);
/*      */           }
/*      */         } 
/*      */       } finally {
/*  845 */         if (!bool)
/*      */         {
/*      */           
/*  848 */           unlockMeta(paramSessionLocal);
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  855 */       paramSessionLocal.scheduleDatabaseObjectIdForRelease(paramInt);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void releaseDatabaseObjectIds(BitSet paramBitSet) {
/*  864 */     synchronized (this.objectIds) {
/*  865 */       this.objectIds.andNot(paramBitSet);
/*      */     } 
/*      */   } private Map<String, DbObject> getMap(int paramInt) {
/*      */     ConcurrentHashMap<String, RightOwner> concurrentHashMap3;
/*      */     ConcurrentHashMap<String, Setting> concurrentHashMap2;
/*      */     ConcurrentHashMap<String, Right> concurrentHashMap1;
/*      */     ConcurrentHashMap<String, Schema> concurrentHashMap;
/*  872 */     switch (paramInt) {
/*      */       case 2:
/*      */       case 7:
/*  875 */         return (Map)this.usersAndRoles;
/*      */       
/*      */       case 6:
/*  878 */         return (Map)this.settings;
/*      */       
/*      */       case 8:
/*  881 */         return (Map)this.rights;
/*      */       
/*      */       case 10:
/*  884 */         return (Map)this.schemas;
/*      */       
/*      */       case 13:
/*  887 */         return (Map)this.comments;
/*      */     } 
/*      */     
/*  890 */     throw DbException.getInternalError("type=" + paramInt);
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
/*      */   public void addSchemaObject(SessionLocal paramSessionLocal, SchemaObject paramSchemaObject) {
/*  902 */     int i = paramSchemaObject.getId();
/*  903 */     if (i > 0 && !this.starting) {
/*  904 */       checkWritingAllowed();
/*      */     }
/*  906 */     lockMeta(paramSessionLocal);
/*  907 */     synchronized (this) {
/*  908 */       paramSchemaObject.getSchema().add(paramSchemaObject);
/*  909 */       addMeta(paramSessionLocal, (DbObject)paramSchemaObject);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void addDatabaseObject(SessionLocal paramSessionLocal, DbObject paramDbObject) {
/*  920 */     int i = paramDbObject.getId();
/*  921 */     if (i > 0 && !this.starting) {
/*  922 */       checkWritingAllowed();
/*      */     }
/*  924 */     Map<String, DbObject> map = getMap(paramDbObject.getType());
/*  925 */     if (paramDbObject.getType() == 2) {
/*  926 */       User user = (User)paramDbObject;
/*  927 */       if (user.isAdmin() && this.systemUser.getName().equals("DBA")) {
/*  928 */         this.systemUser.rename(user.getName());
/*      */       }
/*      */     } 
/*  931 */     String str = paramDbObject.getName();
/*  932 */     if (SysProperties.CHECK && map.get(str) != null) {
/*  933 */       throw DbException.getInternalError("object already exists");
/*      */     }
/*  935 */     lockMeta(paramSessionLocal);
/*  936 */     addMeta(paramSessionLocal, paramDbObject);
/*  937 */     map.put(str, paramDbObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Comment findComment(DbObject paramDbObject) {
/*  948 */     if (paramDbObject.getType() == 13) {
/*  949 */       return null;
/*      */     }
/*  951 */     String str = Comment.getKey(paramDbObject);
/*  952 */     return this.comments.get(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Role findRole(String paramString) {
/*  962 */     RightOwner rightOwner = findUserOrRole(paramString);
/*  963 */     return (rightOwner instanceof Role) ? (Role)rightOwner : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Schema findSchema(String paramString) {
/*  973 */     if (paramString == null) {
/*  974 */       return null;
/*      */     }
/*  976 */     return this.schemas.get(paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Setting findSetting(String paramString) {
/*  986 */     return this.settings.get(paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public User findUser(String paramString) {
/*  996 */     RightOwner rightOwner = findUserOrRole(paramString);
/*  997 */     return (rightOwner instanceof User) ? (User)rightOwner : null;
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
/*      */   public User getUser(String paramString) {
/* 1009 */     User user = findUser(paramString);
/* 1010 */     if (user == null) {
/* 1011 */       throw DbException.get(90032, paramString);
/*      */     }
/* 1013 */     return user;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RightOwner findUserOrRole(String paramString) {
/* 1023 */     return this.usersAndRoles.get(StringUtils.toUpperEnglish(paramString));
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
/*      */   synchronized SessionLocal createSession(User paramUser, NetworkConnectionInfo paramNetworkConnectionInfo) {
/* 1035 */     if (this.closing) {
/* 1036 */       return null;
/*      */     }
/* 1038 */     if (this.exclusiveSession.get() != null) {
/* 1039 */       throw DbException.get(90135);
/*      */     }
/* 1041 */     SessionLocal sessionLocal = createSession(paramUser);
/* 1042 */     sessionLocal.setNetworkConnectionInfo(paramNetworkConnectionInfo);
/* 1043 */     this.userSessions.add(sessionLocal);
/* 1044 */     this.trace.info("connecting session #{0} to {1}", new Object[] { Integer.valueOf(sessionLocal.getId()), this.databaseName });
/* 1045 */     if (this.delayedCloser != null) {
/* 1046 */       this.delayedCloser.reset();
/* 1047 */       this.delayedCloser = null;
/*      */     } 
/* 1049 */     return sessionLocal;
/*      */   }
/*      */   
/*      */   private SessionLocal createSession(User paramUser) {
/* 1053 */     int i = ++this.nextSessionId;
/* 1054 */     return new SessionLocal(this, paramUser, i);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void removeSession(SessionLocal paramSessionLocal) {
/* 1063 */     if (paramSessionLocal != null) {
/* 1064 */       this.exclusiveSession.compareAndSet(paramSessionLocal, null);
/* 1065 */       if (this.userSessions.remove(paramSessionLocal)) {
/* 1066 */         this.trace.info("disconnecting session #{0}", new Object[] { Integer.valueOf(paramSessionLocal.getId()) });
/*      */       }
/*      */     } 
/* 1069 */     if (isUserSession(paramSessionLocal)) {
/* 1070 */       if (this.userSessions.isEmpty()) {
/* 1071 */         if (this.closeDelay == 0)
/* 1072 */         { close(false); }
/* 1073 */         else { if (this.closeDelay < 0) {
/*      */             return;
/*      */           }
/* 1076 */           this.delayedCloser = new DelayedDatabaseCloser(this, this.closeDelay * 1000); }
/*      */       
/*      */       }
/* 1079 */       if (paramSessionLocal != null) {
/* 1080 */         this.trace.info("disconnected session #{0}", new Object[] { Integer.valueOf(paramSessionLocal.getId()) });
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   boolean isUserSession(SessionLocal paramSessionLocal) {
/* 1086 */     return (paramSessionLocal != this.systemSession && paramSessionLocal != this.lobSession);
/*      */   }
/*      */   
/*      */   private synchronized void closeAllSessionsExcept(SessionLocal paramSessionLocal) {
/* 1090 */     SessionLocal[] arrayOfSessionLocal = this.userSessions.<SessionLocal>toArray(EMPTY_SESSION_ARRAY);
/* 1091 */     for (SessionLocal sessionLocal : arrayOfSessionLocal) {
/* 1092 */       if (sessionLocal != paramSessionLocal)
/*      */       {
/* 1094 */         sessionLocal.suspend();
/*      */       }
/*      */     } 
/*      */     
/* 1098 */     int i = 2 * getLockTimeout();
/* 1099 */     long l1 = System.currentTimeMillis();
/*      */ 
/*      */     
/* 1102 */     long l2 = Math.max(i / 20, 1);
/* 1103 */     boolean bool = false;
/* 1104 */     while (!bool) {
/*      */ 
/*      */       
/*      */       try {
/* 1108 */         wait(l2);
/* 1109 */       } catch (InterruptedException interruptedException) {}
/*      */ 
/*      */       
/* 1112 */       if (System.currentTimeMillis() - l1 > i) {
/* 1113 */         for (SessionLocal sessionLocal : arrayOfSessionLocal) {
/* 1114 */           if (sessionLocal != paramSessionLocal && !sessionLocal.isClosed()) {
/*      */             
/*      */             try {
/* 1117 */               sessionLocal.close();
/* 1118 */             } catch (Throwable throwable) {
/* 1119 */               this.trace.error(throwable, "disconnecting session #{0}", new Object[] { Integer.valueOf(sessionLocal.getId()) });
/*      */             } 
/*      */           }
/*      */         } 
/*      */         break;
/*      */       } 
/* 1125 */       bool = true;
/* 1126 */       for (SessionLocal sessionLocal : arrayOfSessionLocal) {
/* 1127 */         if (sessionLocal != paramSessionLocal && !sessionLocal.isClosed()) {
/* 1128 */           bool = false;
/*      */           break;
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
/*      */   void close(boolean paramBoolean) {
/* 1142 */     DbException dbException = this.backgroundException.getAndSet(null);
/*      */     try {
/* 1144 */       closeImpl(paramBoolean);
/* 1145 */     } catch (Throwable throwable) {
/* 1146 */       if (dbException != null) {
/* 1147 */         throwable.addSuppressed((Throwable)dbException);
/*      */       }
/* 1149 */       throw throwable;
/*      */     } 
/* 1151 */     if (dbException != null)
/*      */     {
/* 1153 */       throw DbException.get(dbException.getErrorCode(), dbException, new String[] { dbException.getMessage() });
/*      */     }
/*      */   }
/*      */   
/*      */   private void closeImpl(boolean paramBoolean) {
/* 1158 */     synchronized (this) {
/* 1159 */       if (this.closing || (!paramBoolean && !this.userSessions.isEmpty())) {
/*      */         return;
/*      */       }
/* 1162 */       this.closing = true;
/* 1163 */       stopServer();
/* 1164 */       if (!this.userSessions.isEmpty()) {
/* 1165 */         assert paramBoolean;
/* 1166 */         this.trace.info("closing {0} from shutdown hook", new Object[] { this.databaseName });
/* 1167 */         closeAllSessionsExcept(null);
/*      */       } 
/* 1169 */       this.trace.info("closing {0}", new Object[] { this.databaseName });
/* 1170 */       if (this.eventListener != null) {
/*      */         
/* 1172 */         this.closing = false;
/* 1173 */         DatabaseEventListener databaseEventListener = this.eventListener;
/*      */         
/* 1175 */         this.eventListener = null;
/* 1176 */         databaseEventListener.closingDatabase();
/* 1177 */         this.closing = true;
/* 1178 */         if (!this.userSessions.isEmpty()) {
/* 1179 */           this.trace.info("event listener {0} left connection open", new Object[] { databaseEventListener.getClass().getName() });
/*      */           
/* 1181 */           closeAllSessionsExcept(null);
/*      */         } 
/*      */       } 
/* 1184 */       if (!isReadOnly()) {
/* 1185 */         removeOrphanedLobs();
/*      */       }
/*      */     } 
/*      */     try {
/*      */       try {
/* 1190 */         if (this.systemSession != null) {
/* 1191 */           if (this.powerOffCount != -1) {
/* 1192 */             for (Schema schema : this.schemas.values()) {
/* 1193 */               for (Table table : schema.getAllTablesAndViews(null)) {
/* 1194 */                 if (table.isGlobalTemporary()) {
/* 1195 */                   table.removeChildrenAndResources(this.systemSession); continue;
/*      */                 } 
/* 1197 */                 table.close(this.systemSession);
/*      */               } 
/*      */             } 
/*      */             
/* 1201 */             for (Schema schema : this.schemas.values()) {
/* 1202 */               for (Sequence sequence : schema.getAllSequences()) {
/* 1203 */                 sequence.close();
/*      */               }
/*      */             } 
/*      */           } 
/* 1207 */           for (Schema schema : this.schemas.values()) {
/* 1208 */             for (TriggerObject triggerObject : schema.getAllTriggers()) {
/*      */               try {
/* 1210 */                 triggerObject.close();
/* 1211 */               } catch (SQLException sQLException) {
/* 1212 */                 this.trace.error(sQLException, "close");
/*      */               } 
/*      */             } 
/*      */           } 
/* 1216 */           if (this.powerOffCount != -1) {
/* 1217 */             this.meta.close(this.systemSession);
/* 1218 */             this.systemSession.commit(true);
/*      */           } 
/*      */         } 
/* 1221 */       } catch (DbException dbException) {
/* 1222 */         this.trace.error((Throwable)dbException, "close");
/*      */       } 
/* 1224 */       this.tempFileDeleter.deleteAll();
/*      */       try {
/* 1226 */         if (this.lobSession != null) {
/* 1227 */           this.lobSession.close();
/* 1228 */           this.lobSession = null;
/*      */         } 
/* 1230 */         if (this.systemSession != null) {
/* 1231 */           this.systemSession.close();
/* 1232 */           this.systemSession = null;
/*      */         } 
/* 1234 */         closeOpenFilesAndUnlock();
/* 1235 */       } catch (DbException dbException) {
/* 1236 */         this.trace.error((Throwable)dbException, "close");
/*      */       } 
/* 1238 */       this.trace.info("closed");
/* 1239 */       this.traceSystem.close();
/* 1240 */       OnExitDatabaseCloser.unregister(this);
/* 1241 */       if (this.deleteFilesOnDisconnect && this.persistent) {
/* 1242 */         this.deleteFilesOnDisconnect = false;
/*      */         try {
/* 1244 */           String str1 = FileUtils.getParent(this.databaseName);
/* 1245 */           String str2 = FileUtils.getName(this.databaseName);
/* 1246 */           DeleteDbFiles.execute(str1, str2, true);
/* 1247 */         } catch (Exception exception) {}
/*      */       }
/*      */     
/*      */     } finally {
/*      */       
/* 1252 */       Engine.close(this.databaseName);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void removeOrphanedLobs() {
/* 1258 */     if (!this.persistent) {
/*      */       return;
/*      */     }
/*      */     try {
/* 1262 */       this.lobStorage.removeAllForTable(-1);
/* 1263 */     } catch (DbException dbException) {
/* 1264 */       this.trace.error((Throwable)dbException, "close");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized void closeOpenFilesAndUnlock() {
/*      */     try {
/* 1273 */       if (!this.store.getMvStore().isClosed()) {
/* 1274 */         if (this.compactMode == 81) {
/* 1275 */           this.store.closeImmediately();
/*      */         } else {
/* 1277 */           boolean bool = (this.compactMode == 82 || this.compactMode == 84 || this.dbSettings.defragAlways) ? true : this.dbSettings.maxCompactTime;
/*      */ 
/*      */ 
/*      */           
/* 1281 */           this.store.close(bool);
/*      */         } 
/*      */       }
/* 1284 */       if (this.persistent)
/*      */       {
/*      */ 
/*      */         
/* 1288 */         if (this.lock != null || this.fileLockMethod == FileLockMethod.NO || this.fileLockMethod == FileLockMethod.FS) {
/* 1289 */           deleteOldTempFiles();
/*      */         }
/*      */       }
/*      */     } finally {
/* 1293 */       if (this.lock != null) {
/* 1294 */         this.lock.unlock();
/* 1295 */         this.lock = null;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private synchronized void closeFiles() {
/*      */     try {
/* 1302 */       this.store.closeImmediately();
/* 1303 */     } catch (DbException dbException) {
/* 1304 */       this.trace.error((Throwable)dbException, "close");
/*      */     } 
/*      */   }
/*      */   
/*      */   private void checkMetaFree(SessionLocal paramSessionLocal, int paramInt) {
/* 1309 */     SearchRow searchRow = this.meta.getRowFactory().createRow();
/* 1310 */     searchRow.setValue(0, (Value)ValueInteger.get(paramInt));
/* 1311 */     Cursor cursor = this.metaIdIndex.find(paramSessionLocal, searchRow, searchRow);
/* 1312 */     if (cursor.next()) {
/* 1313 */       throw DbException.getInternalError();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int allocateObjectId() {
/*      */     int i;
/* 1324 */     synchronized (this.objectIds) {
/* 1325 */       i = this.objectIds.nextClearBit(0);
/* 1326 */       this.objectIds.set(i);
/*      */     } 
/* 1328 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public User getSystemUser() {
/* 1337 */     return this.systemUser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Schema getMainSchema() {
/* 1346 */     return this.mainSchema;
/*      */   }
/*      */   
/*      */   public ArrayList<Comment> getAllComments() {
/* 1350 */     return new ArrayList<>(this.comments.values());
/*      */   }
/*      */   
/*      */   public int getAllowLiterals() {
/* 1354 */     if (this.starting) {
/* 1355 */       return 2;
/*      */     }
/* 1357 */     return this.allowLiterals;
/*      */   }
/*      */   
/*      */   public ArrayList<Right> getAllRights() {
/* 1361 */     return new ArrayList<>(this.rights.values());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayList<Table> getAllTablesAndViews() {
/* 1370 */     ArrayList<Table> arrayList = new ArrayList();
/* 1371 */     for (Schema schema : this.schemas.values()) {
/* 1372 */       arrayList.addAll(schema.getAllTablesAndViews(null));
/*      */     }
/* 1374 */     return arrayList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayList<TableSynonym> getAllSynonyms() {
/* 1383 */     ArrayList<TableSynonym> arrayList = new ArrayList();
/* 1384 */     for (Schema schema : this.schemas.values()) {
/* 1385 */       arrayList.addAll(schema.getAllSynonyms());
/*      */     }
/* 1387 */     return arrayList;
/*      */   }
/*      */   
/*      */   public Collection<Schema> getAllSchemas() {
/* 1391 */     return this.schemas.values();
/*      */   }
/*      */   
/*      */   public Collection<Schema> getAllSchemasNoMeta() {
/* 1395 */     return this.schemas.values();
/*      */   }
/*      */   
/*      */   public Collection<Setting> getAllSettings() {
/* 1399 */     return this.settings.values();
/*      */   }
/*      */   
/*      */   public Collection<RightOwner> getAllUsersAndRoles() {
/* 1403 */     return this.usersAndRoles.values();
/*      */   }
/*      */   
/*      */   public String getCacheType() {
/* 1407 */     return this.cacheType;
/*      */   }
/*      */   
/*      */   public String getCluster() {
/* 1411 */     return this.cluster;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompareMode getCompareMode() {
/* 1416 */     return this.compareMode;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getDatabasePath() {
/* 1421 */     if (this.persistent) {
/* 1422 */       return FileUtils.toRealPath(this.databaseName);
/*      */     }
/* 1424 */     return null;
/*      */   }
/*      */   
/*      */   public String getShortName() {
/* 1428 */     return this.databaseShortName;
/*      */   }
/*      */   
/*      */   public String getName() {
/* 1432 */     return this.databaseName;
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
/*      */   public SessionLocal[] getSessions(boolean paramBoolean) {
/*      */     ArrayList<SessionLocal> arrayList;
/* 1446 */     synchronized (this) {
/* 1447 */       arrayList = new ArrayList<>(this.userSessions);
/*      */     } 
/* 1449 */     if (paramBoolean) {
/*      */       
/* 1451 */       SessionLocal sessionLocal = this.systemSession;
/* 1452 */       if (sessionLocal != null) {
/* 1453 */         arrayList.add(sessionLocal);
/*      */       }
/* 1455 */       sessionLocal = this.lobSession;
/* 1456 */       if (sessionLocal != null) {
/* 1457 */         arrayList.add(sessionLocal);
/*      */       }
/*      */     } 
/* 1460 */     return arrayList.<SessionLocal>toArray(new SessionLocal[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateMeta(SessionLocal paramSessionLocal, DbObject paramDbObject) {
/* 1470 */     int i = paramDbObject.getId();
/* 1471 */     if (i > 0) {
/* 1472 */       if (!this.starting && !paramDbObject.isTemporary()) {
/* 1473 */         Row row1 = this.meta.getTemplateRow();
/* 1474 */         MetaRecord.populateRowFromDBObject(paramDbObject, (SearchRow)row1);
/* 1475 */         Row row2 = this.metaIdIndex.getRow(paramSessionLocal, i);
/* 1476 */         if (row2 != null) {
/* 1477 */           this.meta.updateRow(paramSessionLocal, row2, row1);
/*      */         }
/*      */       } 
/*      */       
/* 1481 */       synchronized (this.objectIds) {
/* 1482 */         this.objectIds.set(i);
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
/*      */   
/*      */   public synchronized void renameSchemaObject(SessionLocal paramSessionLocal, SchemaObject paramSchemaObject, String paramString) {
/* 1496 */     checkWritingAllowed();
/* 1497 */     paramSchemaObject.getSchema().rename(paramSchemaObject, paramString);
/* 1498 */     updateMetaAndFirstLevelChildren(paramSessionLocal, (DbObject)paramSchemaObject);
/*      */   }
/*      */   
/*      */   private synchronized void updateMetaAndFirstLevelChildren(SessionLocal paramSessionLocal, DbObject paramDbObject) {
/* 1502 */     ArrayList<DbObject> arrayList = paramDbObject.getChildren();
/* 1503 */     Comment comment = findComment(paramDbObject);
/* 1504 */     if (comment != null) {
/* 1505 */       throw DbException.getInternalError(comment.toString());
/*      */     }
/* 1507 */     updateMeta(paramSessionLocal, paramDbObject);
/*      */     
/* 1509 */     if (arrayList != null) {
/* 1510 */       for (DbObject dbObject : arrayList) {
/* 1511 */         if (dbObject.getCreateSQL() != null) {
/* 1512 */           updateMeta(paramSessionLocal, dbObject);
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
/*      */ 
/*      */   
/*      */   public synchronized void renameDatabaseObject(SessionLocal paramSessionLocal, DbObject paramDbObject, String paramString) {
/* 1527 */     checkWritingAllowed();
/* 1528 */     int i = paramDbObject.getType();
/* 1529 */     Map<String, DbObject> map = getMap(i);
/* 1530 */     if (SysProperties.CHECK) {
/* 1531 */       if (!map.containsKey(paramDbObject.getName())) {
/* 1532 */         throw DbException.getInternalError("not found: " + paramDbObject.getName());
/*      */       }
/* 1534 */       if (paramDbObject.getName().equals(paramString) || map.containsKey(paramString)) {
/* 1535 */         throw DbException.getInternalError("object already exists: " + paramString);
/*      */       }
/*      */     } 
/* 1538 */     paramDbObject.checkRename();
/* 1539 */     map.remove(paramDbObject.getName());
/* 1540 */     paramDbObject.rename(paramString);
/* 1541 */     map.put(paramString, paramDbObject);
/* 1542 */     updateMetaAndFirstLevelChildren(paramSessionLocal, paramDbObject);
/*      */   }
/*      */   
/*      */   private void deleteOldTempFiles() {
/* 1546 */     String str = FileUtils.getParent(this.databaseName);
/* 1547 */     for (String str1 : FileUtils.newDirectoryStream(str)) {
/* 1548 */       if (str1.endsWith(".temp.db") && str1
/* 1549 */         .startsWith(this.databaseName))
/*      */       {
/* 1551 */         FileUtils.tryDelete(str1);
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
/*      */   public Schema getSchema(String paramString) {
/* 1564 */     Schema schema = findSchema(paramString);
/* 1565 */     if (schema == null) {
/* 1566 */       throw DbException.get(90079, paramString);
/*      */     }
/* 1568 */     return schema;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void removeDatabaseObject(SessionLocal paramSessionLocal, DbObject paramDbObject) {
/* 1578 */     checkWritingAllowed();
/* 1579 */     String str = paramDbObject.getName();
/* 1580 */     int i = paramDbObject.getType();
/* 1581 */     Map<String, DbObject> map = getMap(i);
/* 1582 */     if (SysProperties.CHECK && !map.containsKey(str)) {
/* 1583 */       throw DbException.getInternalError("not found: " + str);
/*      */     }
/* 1585 */     Comment comment = findComment(paramDbObject);
/* 1586 */     lockMeta(paramSessionLocal);
/* 1587 */     if (comment != null) {
/* 1588 */       removeDatabaseObject(paramSessionLocal, comment);
/*      */     }
/* 1590 */     int j = paramDbObject.getId();
/* 1591 */     paramDbObject.removeChildrenAndResources(paramSessionLocal);
/* 1592 */     map.remove(str);
/* 1593 */     removeMeta(paramSessionLocal, j);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Table getDependentTable(SchemaObject paramSchemaObject, Table paramTable) {
/* 1604 */     switch (paramSchemaObject.getType()) {
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/*      */       case 5:
/*      */       case 8:
/*      */       case 13:
/* 1611 */         return null;
/*      */     } 
/*      */     
/* 1614 */     HashSet hashSet = new HashSet();
/* 1615 */     for (Schema schema : this.schemas.values()) {
/* 1616 */       for (Table table : schema.getAllTablesAndViews(null)) {
/* 1617 */         if (paramTable == table || TableType.VIEW == table.getTableType()) {
/*      */           continue;
/*      */         }
/* 1620 */         hashSet.clear();
/* 1621 */         table.addDependencies(hashSet);
/* 1622 */         if (hashSet.contains(paramSchemaObject)) {
/* 1623 */           return table;
/*      */         }
/*      */       } 
/*      */     } 
/* 1627 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeSchemaObject(SessionLocal paramSessionLocal, SchemaObject paramSchemaObject) {
/* 1638 */     int i = paramSchemaObject.getType();
/* 1639 */     if (i == 0) {
/* 1640 */       Table table = (Table)paramSchemaObject;
/* 1641 */       if (table.isTemporary() && !table.isGlobalTemporary()) {
/* 1642 */         paramSessionLocal.removeLocalTempTable(table);
/*      */         return;
/*      */       } 
/* 1645 */     } else if (i == 1) {
/* 1646 */       Index index = (Index)paramSchemaObject;
/* 1647 */       Table table = index.getTable();
/* 1648 */       if (table.isTemporary() && !table.isGlobalTemporary()) {
/* 1649 */         paramSessionLocal.removeLocalTempTableIndex(index);
/*      */         return;
/*      */       } 
/* 1652 */     } else if (i == 5) {
/* 1653 */       Constraint constraint = (Constraint)paramSchemaObject;
/* 1654 */       if (constraint.getConstraintType() != Constraint.Type.DOMAIN) {
/* 1655 */         Table table = constraint.getTable();
/* 1656 */         if (table.isTemporary() && !table.isGlobalTemporary()) {
/* 1657 */           paramSessionLocal.removeLocalTempTableConstraint(constraint);
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1662 */     checkWritingAllowed();
/* 1663 */     lockMeta(paramSessionLocal);
/* 1664 */     synchronized (this) {
/* 1665 */       Comment comment = findComment((DbObject)paramSchemaObject);
/* 1666 */       if (comment != null) {
/* 1667 */         removeDatabaseObject(paramSessionLocal, comment);
/*      */       }
/* 1669 */       paramSchemaObject.getSchema().remove(paramSchemaObject);
/* 1670 */       int j = paramSchemaObject.getId();
/* 1671 */       if (!this.starting) {
/* 1672 */         Table table = getDependentTable(paramSchemaObject, null);
/* 1673 */         if (table != null) {
/* 1674 */           paramSchemaObject.getSchema().add(paramSchemaObject);
/* 1675 */           throw DbException.get(90107, new String[] { paramSchemaObject.getTraceSQL(), table.getTraceSQL() });
/*      */         } 
/* 1677 */         paramSchemaObject.removeChildrenAndResources(paramSessionLocal);
/*      */       } 
/* 1679 */       removeMeta(paramSessionLocal, j);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isPersistent() {
/* 1689 */     return this.persistent;
/*      */   }
/*      */   
/*      */   public TraceSystem getTraceSystem() {
/* 1693 */     return this.traceSystem;
/*      */   }
/*      */   
/*      */   public synchronized void setCacheSize(int paramInt) {
/* 1697 */     if (this.starting) {
/* 1698 */       int i = MathUtils.convertLongToInt(Utils.getMemoryMax()) / 2;
/* 1699 */       paramInt = Math.min(paramInt, i);
/*      */     } 
/* 1701 */     this.store.setCacheSize(Math.max(1, paramInt));
/*      */   }
/*      */   
/*      */   public synchronized void setMasterUser(User paramUser) {
/* 1705 */     lockMeta(this.systemSession);
/* 1706 */     addDatabaseObject(this.systemSession, paramUser);
/* 1707 */     this.systemSession.commit(true);
/*      */   }
/*      */   
/*      */   public Role getPublicRole() {
/* 1711 */     return this.publicRole;
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
/*      */   public synchronized String getTempTableName(String paramString, SessionLocal paramSessionLocal) {
/*      */     while (true) {
/* 1724 */       String str = paramString + "_COPY_" + paramSessionLocal.getId() + "_" + this.nextTempTableId++;
/*      */       
/* 1726 */       if (this.mainSchema.findTableOrView(paramSessionLocal, str) == null)
/* 1727 */         return str; 
/*      */     } 
/*      */   }
/*      */   public void setCompareMode(CompareMode paramCompareMode) {
/* 1731 */     this.compareMode = paramCompareMode;
/*      */   }
/*      */   
/*      */   public void setCluster(String paramString) {
/* 1735 */     this.cluster = paramString;
/*      */   }
/*      */ 
/*      */   
/*      */   public void checkWritingAllowed() {
/* 1740 */     if (this.readOnly) {
/* 1741 */       throw DbException.get(90097);
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isReadOnly() {
/* 1746 */     return this.readOnly;
/*      */   }
/*      */   
/*      */   public void setWriteDelay(int paramInt) {
/* 1750 */     this.store.getMvStore().setAutoCommitDelay((paramInt < 0) ? 0 : paramInt);
/*      */   }
/*      */   
/*      */   public int getRetentionTime() {
/* 1754 */     return this.store.getMvStore().getRetentionTime();
/*      */   }
/*      */   
/*      */   public void setRetentionTime(int paramInt) {
/* 1758 */     this.store.getMvStore().setRetentionTime(paramInt);
/*      */   }
/*      */   
/*      */   public void setAllowBuiltinAliasOverride(boolean paramBoolean) {
/* 1762 */     this.allowBuiltinAliasOverride = paramBoolean;
/*      */   }
/*      */   
/*      */   public boolean isAllowBuiltinAliasOverride() {
/* 1766 */     return this.allowBuiltinAliasOverride;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayList<InDoubtTransaction> getInDoubtTransactions() {
/* 1775 */     return this.store.getInDoubtTransactions();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   synchronized void prepareCommit(SessionLocal paramSessionLocal, String paramString) {
/* 1785 */     if (!this.readOnly) {
/* 1786 */       this.store.prepareCommit(paramSessionLocal, paramString);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void throwLastBackgroundException() {
/* 1795 */     if (!this.store.getMvStore().isBackgroundThread()) {
/* 1796 */       DbException dbException = this.backgroundException.getAndSet(null);
/* 1797 */       if (dbException != null)
/*      */       {
/* 1799 */         throw DbException.get(dbException.getErrorCode(), dbException, new String[] { dbException.getMessage() });
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setBackgroundException(DbException paramDbException) {
/* 1805 */     if (this.backgroundException.compareAndSet(null, paramDbException)) {
/* 1806 */       TraceSystem traceSystem = getTraceSystem();
/* 1807 */       if (traceSystem != null) {
/* 1808 */         traceSystem.getTrace(2).error((Throwable)paramDbException, "flush");
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public Throwable getBackgroundException() {
/* 1814 */     MVStoreException mVStoreException = this.store.getMvStore().getPanicException();
/* 1815 */     if (mVStoreException != null) {
/* 1816 */       return (Throwable)mVStoreException;
/*      */     }
/* 1818 */     return (Throwable)this.backgroundException.getAndSet(null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void flush() {
/* 1826 */     if (!this.readOnly) {
/*      */       try {
/* 1828 */         this.store.flush();
/* 1829 */       } catch (RuntimeException runtimeException) {
/* 1830 */         this.backgroundException.compareAndSet(null, DbException.convert(runtimeException));
/* 1831 */         throw runtimeException;
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   public void setEventListener(DatabaseEventListener paramDatabaseEventListener) {
/* 1837 */     this.eventListener = paramDatabaseEventListener;
/*      */   }
/*      */   
/*      */   public void setEventListenerClass(String paramString) {
/* 1841 */     if (paramString == null || paramString.isEmpty()) {
/* 1842 */       this.eventListener = null;
/*      */     } else {
/*      */       try {
/* 1845 */         this
/* 1846 */           .eventListener = JdbcUtils.loadUserClass(paramString).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 1847 */         String str = this.databaseURL;
/* 1848 */         if (this.cipher != null) {
/* 1849 */           str = str + ";CIPHER=" + this.cipher;
/*      */         }
/* 1851 */         this.eventListener.init(str);
/* 1852 */       } catch (Throwable throwable) {
/* 1853 */         throw DbException.get(90099, throwable, new String[] { paramString, throwable
/*      */               
/* 1855 */               .toString() });
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
/*      */ 
/*      */   
/*      */   public void setProgress(int paramInt, String paramString, long paramLong1, long paramLong2) {
/* 1870 */     if (this.eventListener != null) {
/*      */       try {
/* 1872 */         this.eventListener.setProgress(paramInt, paramString, paramLong1, paramLong2);
/* 1873 */       } catch (Exception exception) {}
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
/*      */   public void exceptionThrown(SQLException paramSQLException, String paramString) {
/* 1887 */     if (this.eventListener != null) {
/*      */       try {
/* 1889 */         this.eventListener.exceptionThrown(paramSQLException, paramString);
/* 1890 */       } catch (Exception exception) {}
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void sync() {
/* 1901 */     if (this.readOnly) {
/*      */       return;
/*      */     }
/* 1904 */     this.store.sync();
/*      */   }
/*      */   
/*      */   public int getMaxMemoryRows() {
/* 1908 */     return this.maxMemoryRows;
/*      */   }
/*      */   
/*      */   public void setMaxMemoryRows(int paramInt) {
/* 1912 */     this.maxMemoryRows = paramInt;
/*      */   }
/*      */   
/*      */   public void setLockMode(int paramInt) {
/* 1916 */     switch (paramInt) {
/*      */       case 0:
/*      */       case 3:
/*      */         break;
/*      */       case 1:
/*      */       case 2:
/* 1922 */         paramInt = 3;
/*      */         break;
/*      */       default:
/* 1925 */         throw DbException.getInvalidValueException("lock mode", Integer.valueOf(paramInt));
/*      */     } 
/* 1927 */     this.lockMode = paramInt;
/*      */   }
/*      */   
/*      */   public int getLockMode() {
/* 1931 */     return this.lockMode;
/*      */   }
/*      */   
/*      */   public void setCloseDelay(int paramInt) {
/* 1935 */     this.closeDelay = paramInt;
/*      */   }
/*      */   
/*      */   public SessionLocal getSystemSession() {
/* 1939 */     return this.systemSession;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClosing() {
/* 1948 */     return this.closing;
/*      */   }
/*      */   
/*      */   public void setMaxLengthInplaceLob(int paramInt) {
/* 1952 */     this.maxLengthInplaceLob = paramInt;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMaxLengthInplaceLob() {
/* 1957 */     return this.maxLengthInplaceLob;
/*      */   }
/*      */   
/*      */   public void setIgnoreCase(boolean paramBoolean) {
/* 1961 */     this.ignoreCase = paramBoolean;
/*      */   }
/*      */   
/*      */   public boolean getIgnoreCase() {
/* 1965 */     if (this.starting)
/*      */     {
/* 1967 */       return false;
/*      */     }
/* 1969 */     return this.ignoreCase;
/*      */   }
/*      */   
/*      */   public void setIgnoreCatalogs(boolean paramBoolean) {
/* 1973 */     this.ignoreCatalogs = paramBoolean;
/*      */   }
/*      */   
/*      */   public boolean getIgnoreCatalogs() {
/* 1977 */     return this.ignoreCatalogs;
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized void setDeleteFilesOnDisconnect(boolean paramBoolean) {
/* 1982 */     this.deleteFilesOnDisconnect = paramBoolean;
/*      */   }
/*      */   
/*      */   public void setAllowLiterals(int paramInt) {
/* 1986 */     this.allowLiterals = paramInt;
/*      */   }
/*      */   
/*      */   public boolean getOptimizeReuseResults() {
/* 1990 */     return this.optimizeReuseResults;
/*      */   }
/*      */   
/*      */   public void setOptimizeReuseResults(boolean paramBoolean) {
/* 1994 */     this.optimizeReuseResults = paramBoolean;
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getLobSyncObject() {
/* 1999 */     return this.lobSyncObject;
/*      */   }
/*      */   
/*      */   public int getSessionCount() {
/* 2003 */     return this.userSessions.size();
/*      */   }
/*      */   
/*      */   public void setReferentialIntegrity(boolean paramBoolean) {
/* 2007 */     this.referentialIntegrity = paramBoolean;
/*      */   }
/*      */   
/*      */   public boolean getReferentialIntegrity() {
/* 2011 */     return this.referentialIntegrity;
/*      */   }
/*      */   
/*      */   public void setQueryStatistics(boolean paramBoolean) {
/* 2015 */     this.queryStatistics = paramBoolean;
/* 2016 */     synchronized (this) {
/* 2017 */       if (!paramBoolean) {
/* 2018 */         this.queryStatisticsData = null;
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean getQueryStatistics() {
/* 2024 */     return this.queryStatistics;
/*      */   }
/*      */   
/*      */   public void setQueryStatisticsMaxEntries(int paramInt) {
/* 2028 */     this.queryStatisticsMaxEntries = paramInt;
/* 2029 */     if (this.queryStatisticsData != null) {
/* 2030 */       synchronized (this) {
/* 2031 */         if (this.queryStatisticsData != null) {
/* 2032 */           this.queryStatisticsData.setMaxQueryEntries(this.queryStatisticsMaxEntries);
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   public QueryStatisticsData getQueryStatisticsData() {
/* 2039 */     if (!this.queryStatistics) {
/* 2040 */       return null;
/*      */     }
/* 2042 */     if (this.queryStatisticsData == null) {
/* 2043 */       synchronized (this) {
/* 2044 */         if (this.queryStatisticsData == null) {
/* 2045 */           this.queryStatisticsData = new QueryStatisticsData(this.queryStatisticsMaxEntries);
/*      */         }
/*      */       } 
/*      */     }
/* 2049 */     return this.queryStatisticsData;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isStarting() {
/* 2059 */     return this.starting;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void opened() {
/* 2067 */     if (this.eventListener != null) {
/* 2068 */       this.eventListener.opened();
/*      */     }
/*      */   }
/*      */   
/*      */   public void setMode(Mode paramMode) {
/* 2073 */     this.mode = paramMode;
/* 2074 */     getNextRemoteSettingsId();
/*      */   }
/*      */ 
/*      */   
/*      */   public Mode getMode() {
/* 2079 */     return this.mode;
/*      */   }
/*      */   
/*      */   public void setDefaultNullOrdering(DefaultNullOrdering paramDefaultNullOrdering) {
/* 2083 */     this.defaultNullOrdering = paramDefaultNullOrdering;
/*      */   }
/*      */   
/*      */   public DefaultNullOrdering getDefaultNullOrdering() {
/* 2087 */     return this.defaultNullOrdering;
/*      */   }
/*      */   
/*      */   public void setMaxOperationMemory(int paramInt) {
/* 2091 */     this.maxOperationMemory = paramInt;
/*      */   }
/*      */   
/*      */   public int getMaxOperationMemory() {
/* 2095 */     return this.maxOperationMemory;
/*      */   }
/*      */   
/*      */   public SessionLocal getExclusiveSession() {
/* 2099 */     return this.exclusiveSession.get();
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
/*      */   public boolean setExclusiveSession(SessionLocal paramSessionLocal, boolean paramBoolean) {
/* 2111 */     if (this.exclusiveSession.get() != paramSessionLocal && 
/* 2112 */       !this.exclusiveSession.compareAndSet(null, paramSessionLocal)) {
/* 2113 */       return false;
/*      */     }
/* 2115 */     if (paramBoolean) {
/* 2116 */       closeAllSessionsExcept(paramSessionLocal);
/*      */     }
/* 2118 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean unsetExclusiveSession(SessionLocal paramSessionLocal) {
/* 2129 */     return (this.exclusiveSession.get() == null || this.exclusiveSession
/* 2130 */       .compareAndSet(paramSessionLocal, null));
/*      */   }
/*      */ 
/*      */   
/*      */   public SmallLRUCache<String, String[]> getLobFileListCache() {
/* 2135 */     if (this.lobFileListCache == null) {
/* 2136 */       this.lobFileListCache = SmallLRUCache.newInstance(128);
/*      */     }
/* 2138 */     return this.lobFileListCache;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSysTableLocked() {
/* 2147 */     return (this.meta == null || this.meta.isLockedExclusively());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSysTableLockedBy(SessionLocal paramSessionLocal) {
/* 2158 */     return (this.meta == null || this.meta.isLockedExclusivelyBy(paramSessionLocal));
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
/*      */   public TableLinkConnection getLinkConnection(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 2172 */     if (this.linkConnections == null) {
/* 2173 */       this.linkConnections = new HashMap<>();
/*      */     }
/* 2175 */     return TableLinkConnection.open(this.linkConnections, paramString1, paramString2, paramString3, paramString4, this.dbSettings.shareLinkedConnections);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 2181 */     return this.databaseShortName + ":" + super.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void shutdownImmediately() {
/* 2188 */     this.closing = true;
/* 2189 */     setPowerOffCount(1);
/*      */     try {
/* 2191 */       checkPowerOff();
/* 2192 */     } catch (DbException dbException) {}
/*      */ 
/*      */     
/* 2195 */     closeFiles();
/* 2196 */     this.powerOffCount = 0;
/*      */   }
/*      */ 
/*      */   
/*      */   public TempFileDeleter getTempFileDeleter() {
/* 2201 */     return this.tempFileDeleter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Table getFirstUserTable() {
/* 2211 */     for (Schema schema : this.schemas.values()) {
/* 2212 */       for (Table table : schema.getAllTablesAndViews(null)) {
/* 2213 */         if (table.getCreateSQL() == null || table.isHidden()) {
/*      */           continue;
/*      */         }
/*      */         
/* 2217 */         if (schema.getId() == -1 && table
/* 2218 */           .getName().equalsIgnoreCase("LOB_BLOCKS")) {
/*      */           continue;
/*      */         }
/* 2221 */         return table;
/*      */       } 
/*      */     } 
/* 2224 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void checkpoint() {
/* 2231 */     if (this.persistent) {
/* 2232 */       this.store.flush();
/*      */     }
/* 2234 */     getTempFileDeleter().deleteUnused();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setReadOnly(boolean paramBoolean) {
/* 2243 */     this.readOnly = paramBoolean;
/*      */   }
/*      */   
/*      */   public void setCompactMode(int paramInt) {
/* 2247 */     this.compactMode = paramInt;
/*      */   }
/*      */   
/*      */   public SourceCompiler getCompiler() {
/* 2251 */     if (this.compiler == null) {
/* 2252 */       this.compiler = new SourceCompiler();
/*      */     }
/* 2254 */     return this.compiler;
/*      */   }
/*      */ 
/*      */   
/*      */   public LobStorageInterface getLobStorage() {
/* 2259 */     return this.lobStorage;
/*      */   }
/*      */   
/*      */   public SessionLocal getLobSession() {
/* 2263 */     return this.lobSession;
/*      */   }
/*      */   
/*      */   public int getDefaultTableType() {
/* 2267 */     return this.defaultTableType;
/*      */   }
/*      */   
/*      */   public void setDefaultTableType(int paramInt) {
/* 2271 */     this.defaultTableType = paramInt;
/*      */   }
/*      */   
/*      */   public DbSettings getSettings() {
/* 2275 */     return this.dbSettings;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <V> HashMap<String, V> newStringMap() {
/* 2286 */     return this.dbSettings.caseInsensitiveIdentifiers ? (HashMap<String, V>)new CaseInsensitiveMap() : new HashMap<>();
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
/*      */   public <V> HashMap<String, V> newStringMap(int paramInt) {
/* 2298 */     return this.dbSettings.caseInsensitiveIdentifiers ? (HashMap<String, V>)new CaseInsensitiveMap(paramInt) : new HashMap<>(paramInt);
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
/*      */   public <V> ConcurrentHashMap<String, V> newConcurrentStringMap() {
/* 2310 */     return this.dbSettings.caseInsensitiveIdentifiers ? (ConcurrentHashMap<String, V>)new CaseInsensitiveConcurrentMap() : new ConcurrentHashMap<>();
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
/*      */   public boolean equalsIdentifiers(String paramString1, String paramString2) {
/* 2323 */     return (paramString1.equals(paramString2) || (this.dbSettings.caseInsensitiveIdentifiers && paramString1.equalsIgnoreCase(paramString2)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String sysIdentifier(String paramString) {
/* 2334 */     assert isUpperSysIdentifier(paramString);
/* 2335 */     return this.dbSettings.databaseToLower ? StringUtils.toLowerEnglish(paramString) : paramString;
/*      */   }
/*      */   
/*      */   private static boolean isUpperSysIdentifier(String paramString) {
/* 2339 */     int i = paramString.length();
/* 2340 */     if (i == 0) {
/* 2341 */       return false;
/*      */     }
/* 2343 */     char c = paramString.charAt(0);
/* 2344 */     if (c < 'A' || c > 'Z') {
/* 2345 */       return false;
/*      */     }
/* 2347 */     i--;
/* 2348 */     for (byte b = 1; b < i; b++) {
/* 2349 */       c = paramString.charAt(b);
/* 2350 */       if ((c < 'A' || c > 'Z') && c != '_') {
/* 2351 */         return false;
/*      */       }
/*      */     } 
/* 2354 */     if (i > 0) {
/* 2355 */       c = paramString.charAt(i);
/* 2356 */       if (c < 'A' || c > 'Z') {
/* 2357 */         return false;
/*      */       }
/*      */     } 
/* 2360 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readLob(long paramLong1, byte[] paramArrayOfbyte1, long paramLong2, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2) {
/* 2365 */     throw DbException.getInternalError();
/*      */   }
/*      */   
/*      */   public byte[] getFileEncryptionKey() {
/* 2369 */     return this.fileEncryptionKey;
/*      */   }
/*      */   
/*      */   public int getPageSize() {
/* 2373 */     return this.pageSize;
/*      */   }
/*      */ 
/*      */   
/*      */   public JavaObjectSerializer getJavaObjectSerializer() {
/* 2378 */     initJavaObjectSerializer();
/* 2379 */     return this.javaObjectSerializer;
/*      */   }
/*      */   
/*      */   private void initJavaObjectSerializer() {
/* 2383 */     if (this.javaObjectSerializerInitialized) {
/*      */       return;
/*      */     }
/* 2386 */     synchronized (this) {
/* 2387 */       if (this.javaObjectSerializerInitialized) {
/*      */         return;
/*      */       }
/* 2390 */       String str = this.javaObjectSerializerName;
/* 2391 */       if (str != null) {
/* 2392 */         str = str.trim();
/* 2393 */         if (!str.isEmpty() && 
/* 2394 */           !str.equals("null")) {
/*      */           try {
/* 2396 */             this
/* 2397 */               .javaObjectSerializer = JdbcUtils.loadUserClass(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 2398 */           } catch (Exception exception) {
/* 2399 */             throw DbException.convert(exception);
/*      */           } 
/*      */         }
/*      */       } 
/* 2403 */       this.javaObjectSerializerInitialized = true;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setJavaObjectSerializerName(String paramString) {
/* 2408 */     synchronized (this) {
/* 2409 */       this.javaObjectSerializerInitialized = false;
/* 2410 */       this.javaObjectSerializerName = paramString;
/* 2411 */       getNextRemoteSettingsId();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TableEngine getTableEngine(String paramString) {
/* 2422 */     assert Thread.holdsLock(this);
/*      */     
/* 2424 */     TableEngine tableEngine = this.tableEngines.get(paramString);
/* 2425 */     if (tableEngine == null) {
/*      */       try {
/* 2427 */         tableEngine = JdbcUtils.loadUserClass(paramString).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 2428 */       } catch (Exception exception) {
/* 2429 */         throw DbException.convert(exception);
/*      */       } 
/* 2431 */       this.tableEngines.put(paramString, tableEngine);
/*      */     } 
/* 2433 */     return tableEngine;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Authenticator getAuthenticator() {
/* 2441 */     return this.authenticator;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAuthenticator(Authenticator paramAuthenticator) {
/* 2450 */     if (paramAuthenticator != null) {
/* 2451 */       paramAuthenticator.init(this);
/*      */     }
/* 2453 */     this.authenticator = paramAuthenticator;
/*      */   }
/*      */ 
/*      */   
/*      */   public ValueTimestampTimeZone currentTimestamp() {
/* 2458 */     Session session = SessionLocal.getThreadLocalSession();
/* 2459 */     if (session != null) {
/* 2460 */       return session.currentTimestamp();
/*      */     }
/* 2462 */     throw DbException.getUnsupportedException("Unsafe comparison or cast");
/*      */   }
/*      */ 
/*      */   
/*      */   public TimeZoneProvider currentTimeZone() {
/* 2467 */     Session session = SessionLocal.getThreadLocalSession();
/* 2468 */     if (session != null) {
/* 2469 */       return session.currentTimeZone();
/*      */     }
/* 2471 */     throw DbException.getUnsupportedException("Unsafe comparison or cast");
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean zeroBasedEnums() {
/* 2476 */     return this.dbSettings.zeroBasedEnums;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\Database.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */