package org.h2.engine;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.h2.api.DatabaseEventListener;
import org.h2.api.JavaObjectSerializer;
import org.h2.api.TableEngine;
import org.h2.command.Prepared;
import org.h2.command.ddl.CreateTableData;
import org.h2.command.dml.SetTypes;
import org.h2.constraint.Constraint;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceSystem;
import org.h2.mode.DefaultNullOrdering;
import org.h2.mode.PgCatalogSchema;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.db.LobStorageMap;
import org.h2.mvstore.db.Store;
import org.h2.result.Row;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.schema.InformationSchema;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.schema.Sequence;
import org.h2.schema.TriggerObject;
import org.h2.security.auth.Authenticator;
import org.h2.store.DataHandler;
import org.h2.store.FileLock;
import org.h2.store.FileLockMethod;
import org.h2.store.FileStore;
import org.h2.store.InDoubtTransaction;
import org.h2.store.LobStorageInterface;
import org.h2.store.fs.FileUtils;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.table.TableLinkConnection;
import org.h2.table.TableSynonym;
import org.h2.table.TableType;
import org.h2.table.TableView;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.h2.util.JdbcUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.SmallLRUCache;
import org.h2.util.SourceCompiler;
import org.h2.util.StringUtils;
import org.h2.util.TempFileDeleter;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.value.CaseInsensitiveConcurrentMap;
import org.h2.value.CaseInsensitiveMap;
import org.h2.value.CompareMode;
import org.h2.value.TypeInfo;
import org.h2.value.ValueInteger;
import org.h2.value.ValueTimestampTimeZone;

public final class Database implements DataHandler, CastDataProvider {
   private static int initialPowerOffCount;
   private static final boolean ASSERT;
   private static final ThreadLocal<SessionLocal> META_LOCK_DEBUGGING;
   private static final ThreadLocal<Database> META_LOCK_DEBUGGING_DB;
   private static final ThreadLocal<Throwable> META_LOCK_DEBUGGING_STACK;
   private static final SessionLocal[] EMPTY_SESSION_ARRAY = new SessionLocal[0];
   private static final String SYSTEM_USER_NAME = "DBA";
   private final boolean persistent;
   private final String databaseName;
   private final String databaseShortName;
   private final String databaseURL;
   private final String cipher;
   private final byte[] filePasswordHash;
   private final byte[] fileEncryptionKey;
   private final ConcurrentHashMap<String, RightOwner> usersAndRoles = new ConcurrentHashMap();
   private final ConcurrentHashMap<String, Setting> settings = new ConcurrentHashMap();
   private final ConcurrentHashMap<String, Schema> schemas = new ConcurrentHashMap();
   private final ConcurrentHashMap<String, Right> rights = new ConcurrentHashMap();
   private final ConcurrentHashMap<String, Comment> comments = new ConcurrentHashMap();
   private final HashMap<String, TableEngine> tableEngines = new HashMap();
   private final Set<SessionLocal> userSessions = Collections.synchronizedSet(new HashSet());
   private final AtomicReference<SessionLocal> exclusiveSession = new AtomicReference();
   private final BitSet objectIds = new BitSet();
   private final Object lobSyncObject = new Object();
   private final Schema mainSchema;
   private final Schema infoSchema;
   private final Schema pgCatalogSchema;
   private int nextSessionId;
   private int nextTempTableId;
   private final User systemUser;
   private SessionLocal systemSession;
   private SessionLocal lobSession;
   private final Table meta;
   private final Index metaIdIndex;
   private FileLock lock;
   private volatile boolean starting;
   private final TraceSystem traceSystem;
   private final Trace trace;
   private final FileLockMethod fileLockMethod;
   private final Role publicRole;
   private final AtomicLong modificationDataId = new AtomicLong();
   private final AtomicLong modificationMetaId = new AtomicLong();
   private final AtomicLong remoteSettingsId = new AtomicLong();
   private CompareMode compareMode;
   private String cluster = "''";
   private boolean readOnly;
   private DatabaseEventListener eventListener;
   private int maxMemoryRows;
   private int lockMode;
   private int maxLengthInplaceLob;
   private int allowLiterals;
   private int powerOffCount;
   private volatile int closeDelay;
   private DelayedDatabaseCloser delayedCloser;
   private volatile boolean closing;
   private boolean ignoreCase;
   private boolean deleteFilesOnDisconnect;
   private boolean optimizeReuseResults;
   private final String cacheType;
   private boolean referentialIntegrity;
   private Mode mode;
   private DefaultNullOrdering defaultNullOrdering;
   private int maxOperationMemory;
   private SmallLRUCache<String, String[]> lobFileListCache;
   private final boolean autoServerMode;
   private final int autoServerPort;
   private Server server;
   private HashMap<TableLinkConnection, TableLinkConnection> linkConnections;
   private final TempFileDeleter tempFileDeleter;
   private int compactMode;
   private SourceCompiler compiler;
   private final LobStorageInterface lobStorage;
   private final int pageSize;
   private int defaultTableType;
   private final DbSettings dbSettings;
   private final Store store;
   private boolean allowBuiltinAliasOverride;
   private final AtomicReference<DbException> backgroundException;
   private JavaObjectSerializer javaObjectSerializer;
   private String javaObjectSerializerName;
   private volatile boolean javaObjectSerializerInitialized;
   private boolean queryStatistics;
   private int queryStatisticsMaxEntries;
   private QueryStatisticsData queryStatisticsData;
   private RowFactory rowFactory;
   private boolean ignoreCatalogs;
   private Authenticator authenticator;

   public Database(ConnectionInfo var1, String var2) {
      this.maxMemoryRows = SysProperties.MAX_MEMORY_ROWS;
      this.allowLiterals = 2;
      this.powerOffCount = initialPowerOffCount;
      this.optimizeReuseResults = true;
      this.referentialIntegrity = true;
      this.mode = Mode.getRegular();
      this.defaultNullOrdering = DefaultNullOrdering.LOW;
      this.maxOperationMemory = 100000;
      this.tempFileDeleter = TempFileDeleter.getInstance();
      this.defaultTableType = 0;
      this.backgroundException = new AtomicReference();
      this.queryStatisticsMaxEntries = 100;
      this.rowFactory = RowFactory.getRowFactory();
      if (ASSERT) {
         META_LOCK_DEBUGGING.set((Object)null);
         META_LOCK_DEBUGGING_DB.set((Object)null);
         META_LOCK_DEBUGGING_STACK.set((Object)null);
      }

      String var3 = var1.getName();
      this.dbSettings = var1.getDbSettings();
      this.compareMode = CompareMode.getInstance((String)null, 0);
      this.persistent = var1.isPersistent();
      this.filePasswordHash = var1.getFilePasswordHash();
      this.fileEncryptionKey = var1.getFileEncryptionKey();
      this.databaseName = var3;
      this.databaseShortName = this.parseDatabaseShortName();
      this.maxLengthInplaceLob = 256;
      this.cipher = var2;
      this.autoServerMode = var1.getProperty("AUTO_SERVER", false);
      this.autoServerPort = var1.getProperty("AUTO_SERVER_PORT", 0);
      this.pageSize = var1.getProperty("PAGE_SIZE", 4096);
      if (var2 != null && this.pageSize % 4096 != 0) {
         throw DbException.getUnsupportedException("CIPHER && PAGE_SIZE=" + this.pageSize);
      } else {
         String var4 = StringUtils.toLowerEnglish(var1.getProperty("ACCESS_MODE_DATA", "rw"));
         if ("r".equals(var4)) {
            this.readOnly = true;
         }

         String var5 = var1.getProperty("FILE_LOCK", (String)null);
         this.fileLockMethod = var5 != null ? FileLock.getFileLockMethod(var5) : (this.autoServerMode ? FileLockMethod.FILE : FileLockMethod.FS);
         this.databaseURL = var1.getURL();
         String var6 = var1.removeProperty("DATABASE_EVENT_LISTENER", (String)null);
         if (var6 != null) {
            this.setEventListenerClass(StringUtils.trim(var6, true, true, "'"));
         }

         var6 = var1.removeProperty("MODE", (String)null);
         if (var6 != null) {
            this.mode = Mode.getInstance(var6);
            if (this.mode == null) {
               throw DbException.get(90088, var6);
            }
         }

         var6 = var1.removeProperty("DEFAULT_NULL_ORDERING", (String)null);
         if (var6 != null) {
            try {
               this.defaultNullOrdering = DefaultNullOrdering.valueOf(StringUtils.toUpperEnglish(var6));
            } catch (RuntimeException var17) {
               throw DbException.getInvalidValueException("DEFAULT_NULL_ORDERING", var6);
            }
         }

         var6 = var1.getProperty("JAVA_OBJECT_SERIALIZER", (String)null);
         if (var6 != null) {
            var6 = StringUtils.trim(var6, true, true, "'");
            this.javaObjectSerializerName = var6;
         }

         this.allowBuiltinAliasOverride = var1.getProperty("BUILTIN_ALIAS_OVERRIDE", false);
         boolean var7 = this.dbSettings.dbCloseOnExit;
         int var8 = var1.getIntProperty(9, 1);
         int var9 = var1.getIntProperty(8, 0);
         this.cacheType = StringUtils.toUpperEnglish(var1.removeProperty("CACHE_TYPE", "LRU"));
         this.ignoreCatalogs = var1.getProperty("IGNORE_CATALOGS", this.dbSettings.ignoreCatalogs);
         this.lockMode = var1.getProperty("LOCK_MODE", 3);
         String var10;
         if (this.persistent) {
            if (this.readOnly) {
               if (var8 >= 3) {
                  var10 = Utils.getProperty("java.io.tmpdir", ".") + "/h2_" + System.currentTimeMillis() + ".trace.db";
               } else {
                  var10 = null;
               }
            } else {
               var10 = var3 + ".trace.db";
            }
         } else {
            var10 = null;
         }

         this.traceSystem = new TraceSystem(var10);
         this.traceSystem.setLevelFile(var8);
         this.traceSystem.setLevelSystemOut(var9);
         this.trace = this.traceSystem.getTrace(2);
         this.trace.info("opening {0} (build {1})", var3, 210);

         try {
            if (!this.autoServerMode || !this.readOnly && this.persistent && this.fileLockMethod != FileLockMethod.NO && this.fileLockMethod != FileLockMethod.FS) {
               if (this.persistent) {
                  String var19 = var3 + ".lock.db";
                  if (this.readOnly) {
                     if (FileUtils.exists(var19)) {
                        throw DbException.get(90020, "Lock file exists: " + var19);
                     }
                  } else if (this.fileLockMethod != FileLockMethod.NO && this.fileLockMethod != FileLockMethod.FS) {
                     this.lock = new FileLock(this.traceSystem, var19, 1000);
                     this.lock.lock(this.fileLockMethod);
                     if (this.autoServerMode) {
                        this.startServer(this.lock.getUniqueId());
                     }
                  }

                  this.deleteOldTempFiles();
               }

               this.starting = true;
               if (this.dbSettings.mvStore) {
                  this.store = new Store(this);
                  this.starting = false;
                  this.systemUser = new User(this, 0, "DBA", true);
                  this.systemUser.setAdmin(true);
                  this.mainSchema = new Schema(this, 0, this.sysIdentifier("PUBLIC"), this.systemUser, true);
                  this.infoSchema = new InformationSchema(this, this.systemUser);
                  this.schemas.put(this.mainSchema.getName(), this.mainSchema);
                  this.schemas.put(this.infoSchema.getName(), this.infoSchema);
                  if (this.mode.getEnum() == Mode.ModeEnum.PostgreSQL) {
                     this.pgCatalogSchema = new PgCatalogSchema(this, this.systemUser);
                     this.schemas.put(this.pgCatalogSchema.getName(), this.pgCatalogSchema);
                  } else {
                     this.pgCatalogSchema = null;
                  }

                  this.publicRole = new Role(this, 0, this.sysIdentifier("PUBLIC"), true);
                  this.usersAndRoles.put(this.publicRole.getName(), this.publicRole);
                  this.systemSession = this.createSession(this.systemUser);
                  this.lobSession = this.createSession(this.systemUser);
                  Set var20 = this.dbSettings.getSettings().keySet();
                  this.store.getTransactionStore().init(this.lobSession);
                  var20.removeIf((var0) -> {
                     return var0.startsWith("PAGE_STORE_");
                  });
                  CreateTableData var12 = this.createSysTableData();
                  this.starting = true;
                  this.meta = this.mainSchema.createTable(var12);
                  IndexColumn[] var13 = IndexColumn.wrap(new Column[]{(Column)var12.columns.get(0)});
                  this.metaIdIndex = this.meta.addIndex(this.systemSession, "SYS_ID", 0, var13, 1, IndexType.createPrimaryKey(false, false), true, (String)null);
                  this.systemSession.commit(true);
                  this.objectIds.set(0);
                  this.executeMeta();
                  this.systemSession.commit(true);
                  this.store.getTransactionStore().endLeftoverTransactions();
                  this.store.removeTemporaryMaps(this.objectIds);
                  this.recompileInvalidViews();
                  this.starting = false;
                  if (!this.readOnly) {
                     String var14 = SetTypes.getTypeName(28);
                     Setting var15 = (Setting)this.settings.get(var14);
                     if (var15 == null) {
                        var15 = new Setting(this, this.allocateObjectId(), var14);
                        var15.setIntValue(210);
                        this.lockMeta(this.systemSession);
                        this.addDatabaseObject(this.systemSession, var15);
                     }
                  }

                  this.lobStorage = new LobStorageMap(this);
                  this.lobSession.commit(true);
                  this.systemSession.commit(true);
                  this.trace.info("opened {0}", var3);
                  if (this.persistent) {
                     int var21 = var1.getProperty("WRITE_DELAY", 500);
                     this.setWriteDelay(var21);
                  }

                  if (var7) {
                     OnExitDatabaseCloser.register(this);
                  }

               } else {
                  throw new UnsupportedOperationException();
               }
            } else {
               throw DbException.getUnsupportedException("AUTO_SERVER=TRUE && (readOnly || inMemory || FILE_LOCK=NO || FILE_LOCK=FS)");
            }
         } catch (Throwable var18) {
            Throwable var11 = var18;

            try {
               if (var11 instanceof OutOfMemoryError) {
                  var11.fillInStackTrace();
               }

               if (var11 instanceof DbException) {
                  if (((DbException)var11).getErrorCode() == 90020) {
                     this.stopServer();
                  } else {
                     this.trace.error(var11, "opening {0}", var3);
                  }
               }

               this.traceSystem.close();
               this.closeOpenFilesAndUnlock();
            } catch (Throwable var16) {
               var18.addSuppressed(var16);
            }

            throw DbException.convert(var18);
         }
      }
   }

   public int getLockTimeout() {
      Setting var1 = this.findSetting(SetTypes.getTypeName(5));
      return var1 == null ? 2000 : var1.getIntValue();
   }

   public RowFactory getRowFactory() {
      return this.rowFactory;
   }

   public void setRowFactory(RowFactory var1) {
      this.rowFactory = var1;
   }

   public static void setInitialPowerOffCount(int var0) {
      initialPowerOffCount = var0;
   }

   public void setPowerOffCount(int var1) {
      if (this.powerOffCount != -1) {
         this.powerOffCount = var1;
      }
   }

   public Store getStore() {
      return this.store;
   }

   public long getModificationDataId() {
      return this.modificationDataId.get();
   }

   public long getNextModificationDataId() {
      return this.modificationDataId.incrementAndGet();
   }

   public long getModificationMetaId() {
      return this.modificationMetaId.get();
   }

   public long getNextModificationMetaId() {
      this.modificationDataId.incrementAndGet();
      return this.modificationMetaId.incrementAndGet() - 1L;
   }

   public long getRemoteSettingsId() {
      return this.remoteSettingsId.get();
   }

   public long getNextRemoteSettingsId() {
      return this.remoteSettingsId.incrementAndGet();
   }

   public int getPowerOffCount() {
      return this.powerOffCount;
   }

   public void checkPowerOff() {
      if (this.powerOffCount != 0) {
         this.checkPowerOff2();
      }

   }

   private void checkPowerOff2() {
      if (this.powerOffCount > 1) {
         --this.powerOffCount;
      } else {
         if (this.powerOffCount != -1) {
            try {
               this.powerOffCount = -1;
               this.store.closeImmediately();
               if (this.lock != null) {
                  this.stopServer();
                  this.lock.unlock();
                  this.lock = null;
               }

               if (this.traceSystem != null) {
                  this.traceSystem.close();
               }
            } catch (DbException var2) {
               DbException.traceThrowable(var2);
            }
         }

         Engine.close(this.databaseName);
         throw DbException.get(90098);
      }
   }

   public Trace getTrace(int var1) {
      return this.traceSystem.getTrace(var1);
   }

   public FileStore openFile(String var1, String var2, boolean var3) {
      if (var3 && !FileUtils.exists(var1)) {
         throw DbException.get(90124, var1);
      } else {
         FileStore var4 = FileStore.open(this, var1, var2, this.cipher, this.filePasswordHash);

         try {
            var4.init();
            return var4;
         } catch (DbException var6) {
            var4.closeSilently();
            throw var6;
         }
      }
   }

   boolean validateFilePasswordHash(String var1, byte[] var2) {
      return !Objects.equals(var1, this.cipher) ? false : Utils.compareSecure(var2, this.filePasswordHash);
   }

   private String parseDatabaseShortName() {
      String var1 = this.databaseName;
      int var2 = var1.length();
      int var3 = var2;

      label31:
      while(true) {
         --var3;
         if (var3 < 0) {
            break;
         }

         char var4 = var1.charAt(var3);
         switch (var4) {
            case '/':
            case ':':
            case '\\':
               break label31;
         }
      }

      ++var3;
      var1 = var3 == var2 ? "UNNAMED" : var1.substring(var3);
      return StringUtils.truncateString(this.dbSettings.databaseToUpper ? StringUtils.toUpperEnglish(var1) : (this.dbSettings.databaseToLower ? StringUtils.toLowerEnglish(var1) : var1), 256);
   }

   private CreateTableData createSysTableData() {
      CreateTableData var1 = new CreateTableData();
      ArrayList var2 = var1.columns;
      Column var3 = new Column("ID", TypeInfo.TYPE_INTEGER);
      var3.setNullable(false);
      var2.add(var3);
      var2.add(new Column("HEAD", TypeInfo.TYPE_INTEGER));
      var2.add(new Column("TYPE", TypeInfo.TYPE_INTEGER));
      var2.add(new Column("SQL", TypeInfo.TYPE_VARCHAR));
      var1.tableName = "SYS";
      var1.id = 0;
      var1.temporary = false;
      var1.persistData = this.persistent;
      var1.persistIndexes = this.persistent;
      var1.isHidden = true;
      var1.session = this.systemSession;
      return var1;
   }

   private void executeMeta() {
      Cursor var1 = this.metaIdIndex.find(this.systemSession, (SearchRow)null, (SearchRow)null);
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();
      ArrayList var6 = new ArrayList();

      while(var1.next()) {
         MetaRecord var7 = new MetaRecord(var1.get());
         this.objectIds.set(var7.getId());
         switch (var7.getObjectType()) {
            case 0:
            case 1:
            case 3:
            case 11:
               var4.add(var7);
               break;
            case 2:
            case 6:
            case 9:
            case 10:
               var2.add(var7);
               break;
            case 4:
            case 7:
            case 8:
            default:
               var6.add(var7);
               break;
            case 5:
               var5.add(var7);
               break;
            case 12:
               var3.add(var7);
         }
      }

      synchronized(this.systemSession) {
         this.executeMeta(var2);
         int var8 = var3.size();
         if (var8 > 0) {
            int var9 = 0;

            while(true) {
               DbException var10 = null;

               for(int var11 = 0; var11 < var8; ++var11) {
                  MetaRecord var12 = (MetaRecord)var3.get(var11);

                  try {
                     var12.prepareAndExecute(this, this.systemSession, this.eventListener);
                  } catch (DbException var15) {
                     if (var10 == null) {
                        var10 = var15;
                     }

                     var3.set(var9++, var12);
                  }
               }

               if (var10 == null) {
                  break;
               }

               if (var8 == var9) {
                  throw var10;
               }

               var8 = var9;
            }
         }

         this.executeMeta(var4);
         var8 = var5.size();
         if (var8 > 0) {
            ArrayList var17 = new ArrayList(var8);

            Prepared var20;
            for(int var18 = 0; var18 < var8; ++var18) {
               var20 = ((MetaRecord)var5.get(var18)).prepare(this, this.systemSession, this.eventListener);
               if (var20 != null) {
                  var17.add(var20);
               }
            }

            var17.sort(MetaRecord.CONSTRAINTS_COMPARATOR);
            Iterator var19 = var17.iterator();

            while(var19.hasNext()) {
               var20 = (Prepared)var19.next();
               MetaRecord.execute(this, var20, this.eventListener, var20.getSQL());
            }
         }

         this.executeMeta(var6);
      }
   }

   private void executeMeta(ArrayList<MetaRecord> var1) {
      if (!var1.isEmpty()) {
         var1.sort((Comparator)null);
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            MetaRecord var3 = (MetaRecord)var2.next();
            var3.prepareAndExecute(this, this.systemSession, this.eventListener);
         }
      }

   }

   private void startServer(String var1) {
      try {
         this.server = Server.createTcpServer("-tcpPort", Integer.toString(this.autoServerPort), "-tcpAllowOthers", "-tcpDaemon", "-key", var1, this.databaseName);
         this.server.start();
      } catch (SQLException var5) {
         throw DbException.convert(var5);
      }

      String var2 = NetUtils.getLocalAddress();
      String var3 = var2 + ":" + this.server.getPort();
      this.lock.setProperty("server", var3);
      String var4 = NetUtils.getHostName(var2);
      this.lock.setProperty("hostName", var4);
      this.lock.save();
   }

   private void stopServer() {
      if (this.server != null) {
         Server var1 = this.server;
         this.server = null;
         var1.stop();
      }

   }

   private void recompileInvalidViews() {
      boolean var1;
      do {
         var1 = false;
         Iterator var2 = this.schemas.values().iterator();

         while(var2.hasNext()) {
            Schema var3 = (Schema)var2.next();
            Iterator var4 = var3.getAllTablesAndViews((SessionLocal)null).iterator();

            while(var4.hasNext()) {
               Table var5 = (Table)var4.next();
               if (var5 instanceof TableView) {
                  TableView var6 = (TableView)var5;
                  if (var6.isInvalid()) {
                     var6.recompile(this.systemSession, true, false);
                     if (!var6.isInvalid()) {
                        var1 = true;
                     }
                  }
               }
            }
         }
      } while(var1);

      TableView.clearIndexCaches(this);
   }

   private void addMeta(SessionLocal var1, DbObject var2) {
      assert Thread.holdsLock(this);

      int var3 = var2.getId();
      if (var3 > 0 && !var2.isTemporary() && !this.isReadOnly()) {
         Row var4 = this.meta.getTemplateRow();
         MetaRecord.populateRowFromDBObject(var2, var4);

         assert this.objectIds.get(var3);

         if (SysProperties.CHECK) {
            this.verifyMetaLocked(var1);
         }

         Cursor var5 = this.metaIdIndex.find(var1, var4, var4);
         if (!var5.next()) {
            this.meta.addRow(var1, var4);
         } else {
            assert this.starting;

            Row var6 = var5.get();
            MetaRecord var7 = new MetaRecord(var6);

            assert var7.getId() == var2.getId();

            assert var7.getObjectType() == var2.getType();

            if (!var7.getSQL().equals(var2.getCreateSQLForMeta())) {
               this.meta.updateRow(var1, var6, var4);
            }
         }
      }

   }

   public void verifyMetaLocked(SessionLocal var1) {
      if (this.lockMode != 0 && this.meta != null && !this.meta.isLockedExclusivelyBy(var1)) {
         throw DbException.getInternalError();
      }
   }

   public boolean lockMeta(SessionLocal var1) {
      if (this.meta == null) {
         return true;
      } else {
         if (ASSERT) {
            this.lockMetaAssertion(var1);
         }

         return this.meta.lock(var1, 2);
      }
   }

   private void lockMetaAssertion(SessionLocal var1) {
      if (META_LOCK_DEBUGGING_DB.get() != null && META_LOCK_DEBUGGING_DB.get() != this) {
         SessionLocal var2 = (SessionLocal)META_LOCK_DEBUGGING.get();
         if (var2 == null) {
            META_LOCK_DEBUGGING.set(var1);
            META_LOCK_DEBUGGING_DB.set(this);
            META_LOCK_DEBUGGING_STACK.set(new Throwable("Last meta lock granted in this stack trace, this is debug information for following IllegalStateException"));
         } else if (var2 != var1) {
            ((Throwable)META_LOCK_DEBUGGING_STACK.get()).printStackTrace();
            throw new IllegalStateException("meta currently locked by " + var2 + ", sessionid=" + var2.getId() + " and trying to be locked by different session, " + var1 + ", sessionid=" + var1.getId() + " on same thread");
         }
      }

   }

   public void unlockMeta(SessionLocal var1) {
      if (this.meta != null) {
         unlockMetaDebug(var1);
         this.meta.unlock(var1);
         var1.unlock(this.meta);
      }

   }

   static void unlockMetaDebug(SessionLocal var0) {
      if (ASSERT && META_LOCK_DEBUGGING.get() == var0) {
         META_LOCK_DEBUGGING.set((Object)null);
         META_LOCK_DEBUGGING_DB.set((Object)null);
         META_LOCK_DEBUGGING_STACK.set((Object)null);
      }

   }

   public void removeMeta(SessionLocal var1, int var2) {
      if (var2 > 0 && !this.starting) {
         SearchRow var3 = this.meta.getRowFactory().createRow();
         var3.setValue(0, ValueInteger.get(var2));
         boolean var4 = this.lockMeta(var1);

         try {
            Cursor var5 = this.metaIdIndex.find(var1, var3, var3);
            if (var5.next()) {
               Row var6 = var5.get();
               this.meta.removeRow(var1, var6);
               if (SysProperties.CHECK) {
                  this.checkMetaFree(var1, var2);
               }
            }
         } finally {
            if (!var4) {
               this.unlockMeta(var1);
            }

         }

         var1.scheduleDatabaseObjectIdForRelease(var2);
      }

   }

   public void releaseDatabaseObjectIds(BitSet var1) {
      synchronized(this.objectIds) {
         this.objectIds.andNot(var1);
      }
   }

   private Map<String, DbObject> getMap(int var1) {
      ConcurrentHashMap var2;
      switch (var1) {
         case 2:
         case 7:
            var2 = this.usersAndRoles;
            break;
         case 3:
         case 4:
         case 5:
         case 9:
         case 11:
         case 12:
         default:
            throw DbException.getInternalError("type=" + var1);
         case 6:
            var2 = this.settings;
            break;
         case 8:
            var2 = this.rights;
            break;
         case 10:
            var2 = this.schemas;
            break;
         case 13:
            var2 = this.comments;
      }

      return var2;
   }

   public void addSchemaObject(SessionLocal var1, SchemaObject var2) {
      int var3 = var2.getId();
      if (var3 > 0 && !this.starting) {
         this.checkWritingAllowed();
      }

      this.lockMeta(var1);
      synchronized(this) {
         var2.getSchema().add(var2);
         this.addMeta(var1, var2);
      }
   }

   public synchronized void addDatabaseObject(SessionLocal var1, DbObject var2) {
      int var3 = var2.getId();
      if (var3 > 0 && !this.starting) {
         this.checkWritingAllowed();
      }

      Map var4 = this.getMap(var2.getType());
      if (var2.getType() == 2) {
         User var5 = (User)var2;
         if (var5.isAdmin() && this.systemUser.getName().equals("DBA")) {
            this.systemUser.rename(var5.getName());
         }
      }

      String var6 = var2.getName();
      if (SysProperties.CHECK && var4.get(var6) != null) {
         throw DbException.getInternalError("object already exists");
      } else {
         this.lockMeta(var1);
         this.addMeta(var1, var2);
         var4.put(var6, var2);
      }
   }

   public Comment findComment(DbObject var1) {
      if (var1.getType() == 13) {
         return null;
      } else {
         String var2 = Comment.getKey(var1);
         return (Comment)this.comments.get(var2);
      }
   }

   public Role findRole(String var1) {
      RightOwner var2 = this.findUserOrRole(var1);
      return var2 instanceof Role ? (Role)var2 : null;
   }

   public Schema findSchema(String var1) {
      return var1 == null ? null : (Schema)this.schemas.get(var1);
   }

   public Setting findSetting(String var1) {
      return (Setting)this.settings.get(var1);
   }

   public User findUser(String var1) {
      RightOwner var2 = this.findUserOrRole(var1);
      return var2 instanceof User ? (User)var2 : null;
   }

   public User getUser(String var1) {
      User var2 = this.findUser(var1);
      if (var2 == null) {
         throw DbException.get(90032, var1);
      } else {
         return var2;
      }
   }

   public RightOwner findUserOrRole(String var1) {
      return (RightOwner)this.usersAndRoles.get(StringUtils.toUpperEnglish(var1));
   }

   synchronized SessionLocal createSession(User var1, NetworkConnectionInfo var2) {
      if (this.closing) {
         return null;
      } else if (this.exclusiveSession.get() != null) {
         throw DbException.get(90135);
      } else {
         SessionLocal var3 = this.createSession(var1);
         var3.setNetworkConnectionInfo(var2);
         this.userSessions.add(var3);
         this.trace.info("connecting session #{0} to {1}", var3.getId(), this.databaseName);
         if (this.delayedCloser != null) {
            this.delayedCloser.reset();
            this.delayedCloser = null;
         }

         return var3;
      }
   }

   private SessionLocal createSession(User var1) {
      int var2 = ++this.nextSessionId;
      return new SessionLocal(this, var1, var2);
   }

   public synchronized void removeSession(SessionLocal var1) {
      if (var1 != null) {
         this.exclusiveSession.compareAndSet(var1, (Object)null);
         if (this.userSessions.remove(var1)) {
            this.trace.info("disconnecting session #{0}", var1.getId());
         }
      }

      if (this.isUserSession(var1)) {
         if (this.userSessions.isEmpty()) {
            if (this.closeDelay == 0) {
               this.close(false);
            } else {
               if (this.closeDelay < 0) {
                  return;
               }

               this.delayedCloser = new DelayedDatabaseCloser(this, this.closeDelay * 1000);
            }
         }

         if (var1 != null) {
            this.trace.info("disconnected session #{0}", var1.getId());
         }
      }

   }

   boolean isUserSession(SessionLocal var1) {
      return var1 != this.systemSession && var1 != this.lobSession;
   }

   private synchronized void closeAllSessionsExcept(SessionLocal var1) {
      SessionLocal[] var2 = (SessionLocal[])this.userSessions.toArray(EMPTY_SESSION_ARRAY);
      SessionLocal[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         SessionLocal var6 = var3[var5];
         if (var6 != var1) {
            var6.suspend();
         }
      }

      int var16 = 2 * this.getLockTimeout();
      long var17 = System.currentTimeMillis();
      long var18 = (long)Math.max(var16 / 20, 1);
      boolean var8 = false;

      while(!var8) {
         try {
            this.wait(var18);
         } catch (InterruptedException var15) {
         }

         SessionLocal[] var9;
         int var10;
         int var11;
         SessionLocal var12;
         if (System.currentTimeMillis() - var17 > (long)var16) {
            var9 = var2;
            var10 = var2.length;

            for(var11 = 0; var11 < var10; ++var11) {
               var12 = var9[var11];
               if (var12 != var1 && !var12.isClosed()) {
                  try {
                     var12.close();
                  } catch (Throwable var14) {
                     this.trace.error(var14, "disconnecting session #{0}", var12.getId());
                  }
               }
            }

            return;
         }

         var8 = true;
         var9 = var2;
         var10 = var2.length;

         for(var11 = 0; var11 < var10; ++var11) {
            var12 = var9[var11];
            if (var12 != var1 && !var12.isClosed()) {
               var8 = false;
               break;
            }
         }
      }

   }

   void close(boolean var1) {
      DbException var2 = (DbException)this.backgroundException.getAndSet((Object)null);

      try {
         this.closeImpl(var1);
      } catch (Throwable var4) {
         if (var2 != null) {
            var4.addSuppressed(var2);
         }

         throw var4;
      }

      if (var2 != null) {
         throw DbException.get(var2.getErrorCode(), var2, var2.getMessage());
      }
   }

   private void closeImpl(boolean var1) {
      synchronized(this) {
         if (this.closing || !var1 && !this.userSessions.isEmpty()) {
            return;
         }

         this.closing = true;
         this.stopServer();
         if (!this.userSessions.isEmpty()) {
            assert var1;

            this.trace.info("closing {0} from shutdown hook", this.databaseName);
            this.closeAllSessionsExcept((SessionLocal)null);
         }

         this.trace.info("closing {0}", this.databaseName);
         if (this.eventListener != null) {
            this.closing = false;
            DatabaseEventListener var3 = this.eventListener;
            this.eventListener = null;
            var3.closingDatabase();
            this.closing = true;
            if (!this.userSessions.isEmpty()) {
               this.trace.info("event listener {0} left connection open", var3.getClass().getName());
               this.closeAllSessionsExcept((SessionLocal)null);
            }
         }

         if (!this.isReadOnly()) {
            this.removeOrphanedLobs();
         }
      }

      try {
         try {
            if (this.systemSession != null) {
               Iterator var2;
               Iterator var4;
               Schema var21;
               if (this.powerOffCount != -1) {
                  var2 = this.schemas.values().iterator();

                  while(var2.hasNext()) {
                     var21 = (Schema)var2.next();
                     var4 = var21.getAllTablesAndViews((SessionLocal)null).iterator();

                     while(var4.hasNext()) {
                        Table var5 = (Table)var4.next();
                        if (var5.isGlobalTemporary()) {
                           var5.removeChildrenAndResources(this.systemSession);
                        } else {
                           var5.close(this.systemSession);
                        }
                     }
                  }

                  var2 = this.schemas.values().iterator();

                  while(var2.hasNext()) {
                     var21 = (Schema)var2.next();
                     var4 = var21.getAllSequences().iterator();

                     while(var4.hasNext()) {
                        Sequence var23 = (Sequence)var4.next();
                        var23.close();
                     }
                  }
               }

               var2 = this.schemas.values().iterator();

               while(true) {
                  if (!var2.hasNext()) {
                     if (this.powerOffCount != -1) {
                        this.meta.close(this.systemSession);
                        this.systemSession.commit(true);
                     }
                     break;
                  }

                  var21 = (Schema)var2.next();
                  var4 = var21.getAllTriggers().iterator();

                  while(var4.hasNext()) {
                     TriggerObject var24 = (TriggerObject)var4.next();

                     try {
                        var24.close();
                     } catch (SQLException var16) {
                        this.trace.error(var16, "close");
                     }
                  }
               }
            }
         } catch (DbException var17) {
            this.trace.error(var17, "close");
         }

         this.tempFileDeleter.deleteAll();

         try {
            if (this.lobSession != null) {
               this.lobSession.close();
               this.lobSession = null;
            }

            if (this.systemSession != null) {
               this.systemSession.close();
               this.systemSession = null;
            }

            this.closeOpenFilesAndUnlock();
         } catch (DbException var15) {
            this.trace.error(var15, "close");
         }

         this.trace.info("closed");
         this.traceSystem.close();
         OnExitDatabaseCloser.unregister(this);
         if (this.deleteFilesOnDisconnect && this.persistent) {
            this.deleteFilesOnDisconnect = false;

            try {
               String var20 = FileUtils.getParent(this.databaseName);
               String var22 = FileUtils.getName(this.databaseName);
               DeleteDbFiles.execute(var20, var22, true);
            } catch (Exception var14) {
            }
         }
      } finally {
         Engine.close(this.databaseName);
      }

   }

   private void removeOrphanedLobs() {
      if (this.persistent) {
         try {
            this.lobStorage.removeAllForTable(-1);
         } catch (DbException var2) {
            this.trace.error(var2, "close");
         }

      }
   }

   private synchronized void closeOpenFilesAndUnlock() {
      try {
         if (!this.store.getMvStore().isClosed()) {
            if (this.compactMode == 81) {
               this.store.closeImmediately();
            } else {
               int var1 = this.compactMode != 82 && this.compactMode != 84 && !this.dbSettings.defragAlways ? this.dbSettings.maxCompactTime : -1;
               this.store.close(var1);
            }
         }

         if (this.persistent && (this.lock != null || this.fileLockMethod == FileLockMethod.NO || this.fileLockMethod == FileLockMethod.FS)) {
            this.deleteOldTempFiles();
         }
      } finally {
         if (this.lock != null) {
            this.lock.unlock();
            this.lock = null;
         }

      }

   }

   private synchronized void closeFiles() {
      try {
         this.store.closeImmediately();
      } catch (DbException var2) {
         this.trace.error(var2, "close");
      }

   }

   private void checkMetaFree(SessionLocal var1, int var2) {
      SearchRow var3 = this.meta.getRowFactory().createRow();
      var3.setValue(0, ValueInteger.get(var2));
      Cursor var4 = this.metaIdIndex.find(var1, var3, var3);
      if (var4.next()) {
         throw DbException.getInternalError();
      }
   }

   public int allocateObjectId() {
      synchronized(this.objectIds) {
         int var1 = this.objectIds.nextClearBit(0);
         this.objectIds.set(var1);
         return var1;
      }
   }

   public User getSystemUser() {
      return this.systemUser;
   }

   public Schema getMainSchema() {
      return this.mainSchema;
   }

   public ArrayList<Comment> getAllComments() {
      return new ArrayList(this.comments.values());
   }

   public int getAllowLiterals() {
      return this.starting ? 2 : this.allowLiterals;
   }

   public ArrayList<Right> getAllRights() {
      return new ArrayList(this.rights.values());
   }

   public ArrayList<Table> getAllTablesAndViews() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.schemas.values().iterator();

      while(var2.hasNext()) {
         Schema var3 = (Schema)var2.next();
         var1.addAll(var3.getAllTablesAndViews((SessionLocal)null));
      }

      return var1;
   }

   public ArrayList<TableSynonym> getAllSynonyms() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.schemas.values().iterator();

      while(var2.hasNext()) {
         Schema var3 = (Schema)var2.next();
         var1.addAll(var3.getAllSynonyms());
      }

      return var1;
   }

   public Collection<Schema> getAllSchemas() {
      return this.schemas.values();
   }

   public Collection<Schema> getAllSchemasNoMeta() {
      return this.schemas.values();
   }

   public Collection<Setting> getAllSettings() {
      return this.settings.values();
   }

   public Collection<RightOwner> getAllUsersAndRoles() {
      return this.usersAndRoles.values();
   }

   public String getCacheType() {
      return this.cacheType;
   }

   public String getCluster() {
      return this.cluster;
   }

   public CompareMode getCompareMode() {
      return this.compareMode;
   }

   public String getDatabasePath() {
      return this.persistent ? FileUtils.toRealPath(this.databaseName) : null;
   }

   public String getShortName() {
      return this.databaseShortName;
   }

   public String getName() {
      return this.databaseName;
   }

   public SessionLocal[] getSessions(boolean var1) {
      ArrayList var2;
      synchronized(this) {
         var2 = new ArrayList(this.userSessions);
      }

      if (var1) {
         SessionLocal var3 = this.systemSession;
         if (var3 != null) {
            var2.add(var3);
         }

         var3 = this.lobSession;
         if (var3 != null) {
            var2.add(var3);
         }
      }

      return (SessionLocal[])var2.toArray(new SessionLocal[0]);
   }

   public void updateMeta(SessionLocal var1, DbObject var2) {
      int var3 = var2.getId();
      if (var3 > 0) {
         if (!this.starting && !var2.isTemporary()) {
            Row var4 = this.meta.getTemplateRow();
            MetaRecord.populateRowFromDBObject(var2, var4);
            Row var5 = this.metaIdIndex.getRow(var1, (long)var3);
            if (var5 != null) {
               this.meta.updateRow(var1, var5, var4);
            }
         }

         synchronized(this.objectIds) {
            this.objectIds.set(var3);
         }
      }

   }

   public synchronized void renameSchemaObject(SessionLocal var1, SchemaObject var2, String var3) {
      this.checkWritingAllowed();
      var2.getSchema().rename(var2, var3);
      this.updateMetaAndFirstLevelChildren(var1, var2);
   }

   private synchronized void updateMetaAndFirstLevelChildren(SessionLocal var1, DbObject var2) {
      ArrayList var3 = var2.getChildren();
      Comment var4 = this.findComment(var2);
      if (var4 != null) {
         throw DbException.getInternalError(var4.toString());
      } else {
         this.updateMeta(var1, var2);
         if (var3 != null) {
            Iterator var5 = var3.iterator();

            while(var5.hasNext()) {
               DbObject var6 = (DbObject)var5.next();
               if (var6.getCreateSQL() != null) {
                  this.updateMeta(var1, var6);
               }
            }
         }

      }
   }

   public synchronized void renameDatabaseObject(SessionLocal var1, DbObject var2, String var3) {
      this.checkWritingAllowed();
      int var4 = var2.getType();
      Map var5 = this.getMap(var4);
      if (SysProperties.CHECK) {
         if (!var5.containsKey(var2.getName())) {
            throw DbException.getInternalError("not found: " + var2.getName());
         }

         if (var2.getName().equals(var3) || var5.containsKey(var3)) {
            throw DbException.getInternalError("object already exists: " + var3);
         }
      }

      var2.checkRename();
      var5.remove(var2.getName());
      var2.rename(var3);
      var5.put(var3, var2);
      this.updateMetaAndFirstLevelChildren(var1, var2);
   }

   private void deleteOldTempFiles() {
      String var1 = FileUtils.getParent(this.databaseName);
      Iterator var2 = FileUtils.newDirectoryStream(var1).iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (var3.endsWith(".temp.db") && var3.startsWith(this.databaseName)) {
            FileUtils.tryDelete(var3);
         }
      }

   }

   public Schema getSchema(String var1) {
      Schema var2 = this.findSchema(var1);
      if (var2 == null) {
         throw DbException.get(90079, var1);
      } else {
         return var2;
      }
   }

   public synchronized void removeDatabaseObject(SessionLocal var1, DbObject var2) {
      this.checkWritingAllowed();
      String var3 = var2.getName();
      int var4 = var2.getType();
      Map var5 = this.getMap(var4);
      if (SysProperties.CHECK && !var5.containsKey(var3)) {
         throw DbException.getInternalError("not found: " + var3);
      } else {
         Comment var6 = this.findComment(var2);
         this.lockMeta(var1);
         if (var6 != null) {
            this.removeDatabaseObject(var1, var6);
         }

         int var7 = var2.getId();
         var2.removeChildrenAndResources(var1);
         var5.remove(var3);
         this.removeMeta(var1, var7);
      }
   }

   public Table getDependentTable(SchemaObject var1, Table var2) {
      switch (var1.getType()) {
         case 1:
         case 2:
         case 4:
         case 5:
         case 8:
         case 13:
            return null;
         case 3:
         case 6:
         case 7:
         case 9:
         case 10:
         case 11:
         case 12:
         default:
            HashSet var3 = new HashSet();
            Iterator var4 = this.schemas.values().iterator();

            while(var4.hasNext()) {
               Schema var5 = (Schema)var4.next();
               Iterator var6 = var5.getAllTablesAndViews((SessionLocal)null).iterator();

               while(var6.hasNext()) {
                  Table var7 = (Table)var6.next();
                  if (var2 != var7 && TableType.VIEW != var7.getTableType()) {
                     var3.clear();
                     var7.addDependencies(var3);
                     if (var3.contains(var1)) {
                        return var7;
                     }
                  }
               }
            }

            return null;
      }
   }

   public void removeSchemaObject(SessionLocal var1, SchemaObject var2) {
      int var3 = var2.getType();
      if (var3 == 0) {
         Table var4 = (Table)var2;
         if (var4.isTemporary() && !var4.isGlobalTemporary()) {
            var1.removeLocalTempTable(var4);
            return;
         }
      } else {
         Table var5;
         if (var3 == 1) {
            Index var10 = (Index)var2;
            var5 = var10.getTable();
            if (var5.isTemporary() && !var5.isGlobalTemporary()) {
               var1.removeLocalTempTableIndex(var10);
               return;
            }
         } else if (var3 == 5) {
            Constraint var11 = (Constraint)var2;
            if (var11.getConstraintType() != Constraint.Type.DOMAIN) {
               var5 = var11.getTable();
               if (var5.isTemporary() && !var5.isGlobalTemporary()) {
                  var1.removeLocalTempTableConstraint(var11);
                  return;
               }
            }
         }
      }

      this.checkWritingAllowed();
      this.lockMeta(var1);
      synchronized(this) {
         Comment var12 = this.findComment(var2);
         if (var12 != null) {
            this.removeDatabaseObject(var1, var12);
         }

         var2.getSchema().remove(var2);
         int var6 = var2.getId();
         if (!this.starting) {
            Table var7 = this.getDependentTable(var2, (Table)null);
            if (var7 != null) {
               var2.getSchema().add(var2);
               throw DbException.get(90107, var2.getTraceSQL(), var7.getTraceSQL());
            }

            var2.removeChildrenAndResources(var1);
         }

         this.removeMeta(var1, var6);
      }
   }

   public boolean isPersistent() {
      return this.persistent;
   }

   public TraceSystem getTraceSystem() {
      return this.traceSystem;
   }

   public synchronized void setCacheSize(int var1) {
      if (this.starting) {
         int var2 = MathUtils.convertLongToInt(Utils.getMemoryMax()) / 2;
         var1 = Math.min(var1, var2);
      }

      this.store.setCacheSize(Math.max(1, var1));
   }

   public synchronized void setMasterUser(User var1) {
      this.lockMeta(this.systemSession);
      this.addDatabaseObject(this.systemSession, var1);
      this.systemSession.commit(true);
   }

   public Role getPublicRole() {
      return this.publicRole;
   }

   public synchronized String getTempTableName(String var1, SessionLocal var2) {
      String var3;
      do {
         var3 = var1 + "_COPY_" + var2.getId() + "_" + this.nextTempTableId++;
      } while(this.mainSchema.findTableOrView(var2, var3) != null);

      return var3;
   }

   public void setCompareMode(CompareMode var1) {
      this.compareMode = var1;
   }

   public void setCluster(String var1) {
      this.cluster = var1;
   }

   public void checkWritingAllowed() {
      if (this.readOnly) {
         throw DbException.get(90097);
      }
   }

   public boolean isReadOnly() {
      return this.readOnly;
   }

   public void setWriteDelay(int var1) {
      this.store.getMvStore().setAutoCommitDelay(var1 < 0 ? 0 : var1);
   }

   public int getRetentionTime() {
      return this.store.getMvStore().getRetentionTime();
   }

   public void setRetentionTime(int var1) {
      this.store.getMvStore().setRetentionTime(var1);
   }

   public void setAllowBuiltinAliasOverride(boolean var1) {
      this.allowBuiltinAliasOverride = var1;
   }

   public boolean isAllowBuiltinAliasOverride() {
      return this.allowBuiltinAliasOverride;
   }

   public ArrayList<InDoubtTransaction> getInDoubtTransactions() {
      return this.store.getInDoubtTransactions();
   }

   synchronized void prepareCommit(SessionLocal var1, String var2) {
      if (!this.readOnly) {
         this.store.prepareCommit(var1, var2);
      }

   }

   void throwLastBackgroundException() {
      if (!this.store.getMvStore().isBackgroundThread()) {
         DbException var1 = (DbException)this.backgroundException.getAndSet((Object)null);
         if (var1 != null) {
            throw DbException.get(var1.getErrorCode(), var1, var1.getMessage());
         }
      }

   }

   public void setBackgroundException(DbException var1) {
      if (this.backgroundException.compareAndSet((Object)null, var1)) {
         TraceSystem var2 = this.getTraceSystem();
         if (var2 != null) {
            var2.getTrace(2).error(var1, "flush");
         }
      }

   }

   public Throwable getBackgroundException() {
      MVStoreException var1 = this.store.getMvStore().getPanicException();
      return (Throwable)(var1 != null ? var1 : (Throwable)this.backgroundException.getAndSet((Object)null));
   }

   public synchronized void flush() {
      if (!this.readOnly) {
         try {
            this.store.flush();
         } catch (RuntimeException var2) {
            this.backgroundException.compareAndSet((Object)null, DbException.convert(var2));
            throw var2;
         }
      }

   }

   public void setEventListener(DatabaseEventListener var1) {
      this.eventListener = var1;
   }

   public void setEventListenerClass(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         try {
            this.eventListener = (DatabaseEventListener)JdbcUtils.loadUserClass(var1).getDeclaredConstructor().newInstance();
            String var2 = this.databaseURL;
            if (this.cipher != null) {
               var2 = var2 + ";CIPHER=" + this.cipher;
            }

            this.eventListener.init(var2);
         } catch (Throwable var3) {
            throw DbException.get(90099, var3, var1, var3.toString());
         }
      } else {
         this.eventListener = null;
      }

   }

   public void setProgress(int var1, String var2, long var3, long var5) {
      if (this.eventListener != null) {
         try {
            this.eventListener.setProgress(var1, var2, var3, var5);
         } catch (Exception var8) {
         }
      }

   }

   public void exceptionThrown(SQLException var1, String var2) {
      if (this.eventListener != null) {
         try {
            this.eventListener.exceptionThrown(var1, var2);
         } catch (Exception var4) {
         }
      }

   }

   public synchronized void sync() {
      if (!this.readOnly) {
         this.store.sync();
      }
   }

   public int getMaxMemoryRows() {
      return this.maxMemoryRows;
   }

   public void setMaxMemoryRows(int var1) {
      this.maxMemoryRows = var1;
   }

   public void setLockMode(int var1) {
      switch (var1) {
         case 1:
         case 2:
            var1 = 3;
         case 0:
         case 3:
            this.lockMode = var1;
            return;
         default:
            throw DbException.getInvalidValueException("lock mode", var1);
      }
   }

   public int getLockMode() {
      return this.lockMode;
   }

   public void setCloseDelay(int var1) {
      this.closeDelay = var1;
   }

   public SessionLocal getSystemSession() {
      return this.systemSession;
   }

   public boolean isClosing() {
      return this.closing;
   }

   public void setMaxLengthInplaceLob(int var1) {
      this.maxLengthInplaceLob = var1;
   }

   public int getMaxLengthInplaceLob() {
      return this.maxLengthInplaceLob;
   }

   public void setIgnoreCase(boolean var1) {
      this.ignoreCase = var1;
   }

   public boolean getIgnoreCase() {
      return this.starting ? false : this.ignoreCase;
   }

   public void setIgnoreCatalogs(boolean var1) {
      this.ignoreCatalogs = var1;
   }

   public boolean getIgnoreCatalogs() {
      return this.ignoreCatalogs;
   }

   public synchronized void setDeleteFilesOnDisconnect(boolean var1) {
      this.deleteFilesOnDisconnect = var1;
   }

   public void setAllowLiterals(int var1) {
      this.allowLiterals = var1;
   }

   public boolean getOptimizeReuseResults() {
      return this.optimizeReuseResults;
   }

   public void setOptimizeReuseResults(boolean var1) {
      this.optimizeReuseResults = var1;
   }

   public Object getLobSyncObject() {
      return this.lobSyncObject;
   }

   public int getSessionCount() {
      return this.userSessions.size();
   }

   public void setReferentialIntegrity(boolean var1) {
      this.referentialIntegrity = var1;
   }

   public boolean getReferentialIntegrity() {
      return this.referentialIntegrity;
   }

   public void setQueryStatistics(boolean var1) {
      this.queryStatistics = var1;
      synchronized(this) {
         if (!var1) {
            this.queryStatisticsData = null;
         }

      }
   }

   public boolean getQueryStatistics() {
      return this.queryStatistics;
   }

   public void setQueryStatisticsMaxEntries(int var1) {
      this.queryStatisticsMaxEntries = var1;
      if (this.queryStatisticsData != null) {
         synchronized(this) {
            if (this.queryStatisticsData != null) {
               this.queryStatisticsData.setMaxQueryEntries(this.queryStatisticsMaxEntries);
            }
         }
      }

   }

   public QueryStatisticsData getQueryStatisticsData() {
      if (!this.queryStatistics) {
         return null;
      } else {
         if (this.queryStatisticsData == null) {
            synchronized(this) {
               if (this.queryStatisticsData == null) {
                  this.queryStatisticsData = new QueryStatisticsData(this.queryStatisticsMaxEntries);
               }
            }
         }

         return this.queryStatisticsData;
      }
   }

   public boolean isStarting() {
      return this.starting;
   }

   void opened() {
      if (this.eventListener != null) {
         this.eventListener.opened();
      }

   }

   public void setMode(Mode var1) {
      this.mode = var1;
      this.getNextRemoteSettingsId();
   }

   public Mode getMode() {
      return this.mode;
   }

   public void setDefaultNullOrdering(DefaultNullOrdering var1) {
      this.defaultNullOrdering = var1;
   }

   public DefaultNullOrdering getDefaultNullOrdering() {
      return this.defaultNullOrdering;
   }

   public void setMaxOperationMemory(int var1) {
      this.maxOperationMemory = var1;
   }

   public int getMaxOperationMemory() {
      return this.maxOperationMemory;
   }

   public SessionLocal getExclusiveSession() {
      return (SessionLocal)this.exclusiveSession.get();
   }

   public boolean setExclusiveSession(SessionLocal var1, boolean var2) {
      if (this.exclusiveSession.get() != var1 && !this.exclusiveSession.compareAndSet((Object)null, var1)) {
         return false;
      } else {
         if (var2) {
            this.closeAllSessionsExcept(var1);
         }

         return true;
      }
   }

   public boolean unsetExclusiveSession(SessionLocal var1) {
      return this.exclusiveSession.get() == null || this.exclusiveSession.compareAndSet(var1, (Object)null);
   }

   public SmallLRUCache<String, String[]> getLobFileListCache() {
      if (this.lobFileListCache == null) {
         this.lobFileListCache = SmallLRUCache.newInstance(128);
      }

      return this.lobFileListCache;
   }

   public boolean isSysTableLocked() {
      return this.meta == null || this.meta.isLockedExclusively();
   }

   public boolean isSysTableLockedBy(SessionLocal var1) {
      return this.meta == null || this.meta.isLockedExclusivelyBy(var1);
   }

   public TableLinkConnection getLinkConnection(String var1, String var2, String var3, String var4) {
      if (this.linkConnections == null) {
         this.linkConnections = new HashMap();
      }

      return TableLinkConnection.open(this.linkConnections, var1, var2, var3, var4, this.dbSettings.shareLinkedConnections);
   }

   public String toString() {
      return this.databaseShortName + ":" + super.toString();
   }

   public void shutdownImmediately() {
      this.closing = true;
      this.setPowerOffCount(1);

      try {
         this.checkPowerOff();
      } catch (DbException var2) {
      }

      this.closeFiles();
      this.powerOffCount = 0;
   }

   public TempFileDeleter getTempFileDeleter() {
      return this.tempFileDeleter;
   }

   public Table getFirstUserTable() {
      Iterator var1 = this.schemas.values().iterator();

      while(var1.hasNext()) {
         Schema var2 = (Schema)var1.next();
         Iterator var3 = var2.getAllTablesAndViews((SessionLocal)null).iterator();

         while(var3.hasNext()) {
            Table var4 = (Table)var3.next();
            if (var4.getCreateSQL() != null && !var4.isHidden() && (var2.getId() != -1 || !var4.getName().equalsIgnoreCase("LOB_BLOCKS"))) {
               return var4;
            }
         }
      }

      return null;
   }

   public void checkpoint() {
      if (this.persistent) {
         this.store.flush();
      }

      this.getTempFileDeleter().deleteUnused();
   }

   public void setReadOnly(boolean var1) {
      this.readOnly = var1;
   }

   public void setCompactMode(int var1) {
      this.compactMode = var1;
   }

   public SourceCompiler getCompiler() {
      if (this.compiler == null) {
         this.compiler = new SourceCompiler();
      }

      return this.compiler;
   }

   public LobStorageInterface getLobStorage() {
      return this.lobStorage;
   }

   public SessionLocal getLobSession() {
      return this.lobSession;
   }

   public int getDefaultTableType() {
      return this.defaultTableType;
   }

   public void setDefaultTableType(int var1) {
      this.defaultTableType = var1;
   }

   public DbSettings getSettings() {
      return this.dbSettings;
   }

   public <V> HashMap<String, V> newStringMap() {
      return (HashMap)(this.dbSettings.caseInsensitiveIdentifiers ? new CaseInsensitiveMap() : new HashMap());
   }

   public <V> HashMap<String, V> newStringMap(int var1) {
      return (HashMap)(this.dbSettings.caseInsensitiveIdentifiers ? new CaseInsensitiveMap(var1) : new HashMap(var1));
   }

   public <V> ConcurrentHashMap<String, V> newConcurrentStringMap() {
      return (ConcurrentHashMap)(this.dbSettings.caseInsensitiveIdentifiers ? new CaseInsensitiveConcurrentMap() : new ConcurrentHashMap());
   }

   public boolean equalsIdentifiers(String var1, String var2) {
      return var1.equals(var2) || this.dbSettings.caseInsensitiveIdentifiers && var1.equalsIgnoreCase(var2);
   }

   public String sysIdentifier(String var1) {
      assert isUpperSysIdentifier(var1);

      return this.dbSettings.databaseToLower ? StringUtils.toLowerEnglish(var1) : var1;
   }

   private static boolean isUpperSysIdentifier(String var0) {
      int var1 = var0.length();
      if (var1 == 0) {
         return false;
      } else {
         char var2 = var0.charAt(0);
         if (var2 >= 'A' && var2 <= 'Z') {
            --var1;

            for(int var3 = 1; var3 < var1; ++var3) {
               var2 = var0.charAt(var3);
               if ((var2 < 'A' || var2 > 'Z') && var2 != '_') {
                  return false;
               }
            }

            if (var1 > 0) {
               var2 = var0.charAt(var1);
               if (var2 < 'A' || var2 > 'Z') {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public int readLob(long var1, byte[] var3, long var4, byte[] var6, int var7, int var8) {
      throw DbException.getInternalError();
   }

   public byte[] getFileEncryptionKey() {
      return this.fileEncryptionKey;
   }

   public int getPageSize() {
      return this.pageSize;
   }

   public JavaObjectSerializer getJavaObjectSerializer() {
      this.initJavaObjectSerializer();
      return this.javaObjectSerializer;
   }

   private void initJavaObjectSerializer() {
      if (!this.javaObjectSerializerInitialized) {
         synchronized(this) {
            if (!this.javaObjectSerializerInitialized) {
               String var2 = this.javaObjectSerializerName;
               if (var2 != null) {
                  var2 = var2.trim();
                  if (!var2.isEmpty() && !var2.equals("null")) {
                     try {
                        this.javaObjectSerializer = (JavaObjectSerializer)JdbcUtils.loadUserClass(var2).getDeclaredConstructor().newInstance();
                     } catch (Exception var5) {
                        throw DbException.convert(var5);
                     }
                  }
               }

               this.javaObjectSerializerInitialized = true;
            }
         }
      }
   }

   public void setJavaObjectSerializerName(String var1) {
      synchronized(this) {
         this.javaObjectSerializerInitialized = false;
         this.javaObjectSerializerName = var1;
         this.getNextRemoteSettingsId();
      }
   }

   public TableEngine getTableEngine(String var1) {
      assert Thread.holdsLock(this);

      TableEngine var2 = (TableEngine)this.tableEngines.get(var1);
      if (var2 == null) {
         try {
            var2 = (TableEngine)JdbcUtils.loadUserClass(var1).getDeclaredConstructor().newInstance();
         } catch (Exception var4) {
            throw DbException.convert(var4);
         }

         this.tableEngines.put(var1, var2);
      }

      return var2;
   }

   public Authenticator getAuthenticator() {
      return this.authenticator;
   }

   public void setAuthenticator(Authenticator var1) {
      if (var1 != null) {
         var1.init(this);
      }

      this.authenticator = var1;
   }

   public ValueTimestampTimeZone currentTimestamp() {
      Session var1 = SessionLocal.getThreadLocalSession();
      if (var1 != null) {
         return var1.currentTimestamp();
      } else {
         throw DbException.getUnsupportedException("Unsafe comparison or cast");
      }
   }

   public TimeZoneProvider currentTimeZone() {
      Session var1 = SessionLocal.getThreadLocalSession();
      if (var1 != null) {
         return var1.currentTimeZone();
      } else {
         throw DbException.getUnsupportedException("Unsafe comparison or cast");
      }
   }

   public boolean zeroBasedEnums() {
      return this.dbSettings.zeroBasedEnums;
   }

   static {
      boolean var0 = false;
      if (!$assertionsDisabled) {
         var0 = true;
         if (false) {
            throw new AssertionError();
         }
      }

      ASSERT = var0;
      if (var0) {
         META_LOCK_DEBUGGING = new ThreadLocal();
         META_LOCK_DEBUGGING_DB = new ThreadLocal();
         META_LOCK_DEBUGGING_STACK = new ThreadLocal();
      } else {
         META_LOCK_DEBUGGING = null;
         META_LOCK_DEBUGGING_DB = null;
         META_LOCK_DEBUGGING_STACK = null;
      }

   }
}
