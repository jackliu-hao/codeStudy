package com.mysql.cj.jdbc;

import com.mysql.cj.CacheAdapter;
import com.mysql.cj.CacheAdapterFactory;
import com.mysql.cj.LicenseConfiguration;
import com.mysql.cj.Messages;
import com.mysql.cj.NativeSession;
import com.mysql.cj.NoSubInterceptorWrapper;
import com.mysql.cj.ParseInfo;
import com.mysql.cj.PreparedQuery;
import com.mysql.cj.Query;
import com.mysql.cj.ServerVersion;
import com.mysql.cj.Session;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.ExceptionInterceptorChain;
import com.mysql.cj.exceptions.PasswordExpiredException;
import com.mysql.cj.exceptions.UnableToConnectException;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.jdbc.ha.MultiHostMySQLConnection;
import com.mysql.cj.jdbc.interceptors.ConnectionLifecycleInterceptor;
import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
import com.mysql.cj.jdbc.result.CachedResultSetMetaDataImpl;
import com.mysql.cj.jdbc.result.ResultSetFactory;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import com.mysql.cj.jdbc.result.UpdatableResultSet;
import com.mysql.cj.log.StandardLogger;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerSessionStateController;
import com.mysql.cj.protocol.SocksProxySocketFactory;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.util.LRUCache;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.Util;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.sql.Array;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLPermission;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ConnectionImpl implements JdbcConnection, Session.SessionEventListener, Serializable {
   private static final long serialVersionUID = 4009476458425101761L;
   private static final SQLPermission SET_NETWORK_TIMEOUT_PERM = new SQLPermission("setNetworkTimeout");
   private static final SQLPermission ABORT_PERM = new SQLPermission("abort");
   private JdbcConnection parentProxy = null;
   private JdbcConnection topProxy = null;
   private InvocationHandler realProxy = null;
   protected static final String DEFAULT_LOGGER_CLASS = StandardLogger.class.getName();
   private static Map<String, Integer> mapTransIsolationNameToValue = null;
   protected static Map<?, ?> roundRobinStatsMap;
   private List<ConnectionLifecycleInterceptor> connectionLifecycleInterceptors;
   private static final int DEFAULT_RESULT_SET_TYPE = 1003;
   private static final int DEFAULT_RESULT_SET_CONCURRENCY = 1007;
   private static final Random random;
   private CacheAdapter<String, ParseInfo> cachedPreparedStatementParams;
   private String database = null;
   private java.sql.DatabaseMetaData dbmd = null;
   private NativeSession session = null;
   private boolean isInGlobalTx = false;
   private int isolationLevel = 2;
   private final CopyOnWriteArrayList<JdbcStatement> openStatements = new CopyOnWriteArrayList();
   private LRUCache<CompoundCacheKey, CallableStatement.CallableStatementParamInfo> parsedCallableStatementCache;
   private String password = null;
   protected Properties props = null;
   private boolean readOnly = false;
   protected LRUCache<String, CachedResultSetMetaData> resultSetMetadataCache;
   private Map<String, Class<?>> typeMap;
   private String user = null;
   private LRUCache<String, Boolean> serverSideStatementCheckCache;
   private LRUCache<CompoundCacheKey, ServerPreparedStatement> serverSideStatementCache;
   private HostInfo origHostInfo;
   private String origHostToConnectTo;
   private int origPortToConnectTo;
   private boolean hasTriedSourceFlag = false;
   private List<QueryInterceptor> queryInterceptors;
   protected JdbcPropertySet propertySet;
   private RuntimeProperty<Boolean> autoReconnectForPools;
   private RuntimeProperty<Boolean> cachePrepStmts;
   private RuntimeProperty<Boolean> autoReconnect;
   private RuntimeProperty<Boolean> useUsageAdvisor;
   private RuntimeProperty<Boolean> reconnectAtTxEnd;
   private RuntimeProperty<Boolean> emulateUnsupportedPstmts;
   private RuntimeProperty<Boolean> ignoreNonTxTables;
   private RuntimeProperty<Boolean> pedantic;
   private RuntimeProperty<Integer> prepStmtCacheSqlLimit;
   private RuntimeProperty<Boolean> useLocalSessionState;
   private RuntimeProperty<Boolean> useServerPrepStmts;
   private RuntimeProperty<Boolean> processEscapeCodesForPrepStmts;
   private RuntimeProperty<Boolean> useLocalTransactionState;
   private RuntimeProperty<Boolean> disconnectOnExpiredPasswords;
   private RuntimeProperty<Boolean> readOnlyPropagatesToServer;
   protected ResultSetFactory nullStatementResultSetFactory;
   private int autoIncrementIncrement = 0;
   private ExceptionInterceptor exceptionInterceptor;
   private ClientInfoProvider infoProvider;

   public String getHost() {
      return this.session.getHostInfo().getHost();
   }

   public boolean isProxySet() {
      return this.topProxy != null;
   }

   public void setProxy(JdbcConnection proxy) {
      if (this.parentProxy == null) {
         this.parentProxy = proxy;
      }

      this.topProxy = proxy;
      this.realProxy = this.topProxy instanceof MultiHostMySQLConnection ? ((MultiHostMySQLConnection)proxy).getThisAsProxy() : null;
   }

   private JdbcConnection getProxy() {
      return (JdbcConnection)(this.topProxy != null ? this.topProxy : this);
   }

   public JdbcConnection getMultiHostSafeProxy() {
      return this.getProxy();
   }

   public JdbcConnection getMultiHostParentProxy() {
      return this.parentProxy;
   }

   public JdbcConnection getActiveMySQLConnection() {
      return this;
   }

   public Object getConnectionMutex() {
      return this.realProxy != null ? this.realProxy : this.getProxy();
   }

   public static JdbcConnection getInstance(HostInfo hostInfo) throws SQLException {
      return new ConnectionImpl(hostInfo);
   }

   protected static synchronized int getNextRoundRobinHostIndex(String url, List<?> hostList) {
      int indexRange = hostList.size();
      int index = random.nextInt(indexRange);
      return index;
   }

   private static boolean nullSafeCompare(String s1, String s2) {
      if (s1 == null && s2 == null) {
         return true;
      } else if (s1 == null && s2 != null) {
         return false;
      } else {
         return s1 != null && s1.equals(s2);
      }
   }

   protected ConnectionImpl() {
   }

   public ConnectionImpl(HostInfo hostInfo) throws SQLException {
      try {
         this.origHostInfo = hostInfo;
         this.origHostToConnectTo = hostInfo.getHost();
         this.origPortToConnectTo = hostInfo.getPort();
         this.database = hostInfo.getDatabase();
         this.user = hostInfo.getUser();
         this.password = hostInfo.getPassword();
         this.props = hostInfo.exposeAsProperties();
         this.propertySet = new JdbcPropertySetImpl();
         this.propertySet.initializeProperties(this.props);
         this.nullStatementResultSetFactory = new ResultSetFactory(this, (StatementImpl)null);
         this.session = new NativeSession(hostInfo, this.propertySet);
         this.session.addListener(this);
         this.autoReconnectForPools = this.propertySet.getBooleanProperty(PropertyKey.autoReconnectForPools);
         this.cachePrepStmts = this.propertySet.getBooleanProperty(PropertyKey.cachePrepStmts);
         this.autoReconnect = this.propertySet.getBooleanProperty(PropertyKey.autoReconnect);
         this.useUsageAdvisor = this.propertySet.getBooleanProperty(PropertyKey.useUsageAdvisor);
         this.reconnectAtTxEnd = this.propertySet.getBooleanProperty(PropertyKey.reconnectAtTxEnd);
         this.emulateUnsupportedPstmts = this.propertySet.getBooleanProperty(PropertyKey.emulateUnsupportedPstmts);
         this.ignoreNonTxTables = this.propertySet.getBooleanProperty(PropertyKey.ignoreNonTxTables);
         this.pedantic = this.propertySet.getBooleanProperty(PropertyKey.pedantic);
         this.prepStmtCacheSqlLimit = this.propertySet.getIntegerProperty(PropertyKey.prepStmtCacheSqlLimit);
         this.useLocalSessionState = this.propertySet.getBooleanProperty(PropertyKey.useLocalSessionState);
         this.useServerPrepStmts = this.propertySet.getBooleanProperty(PropertyKey.useServerPrepStmts);
         this.processEscapeCodesForPrepStmts = this.propertySet.getBooleanProperty(PropertyKey.processEscapeCodesForPrepStmts);
         this.useLocalTransactionState = this.propertySet.getBooleanProperty(PropertyKey.useLocalTransactionState);
         this.disconnectOnExpiredPasswords = this.propertySet.getBooleanProperty(PropertyKey.disconnectOnExpiredPasswords);
         this.readOnlyPropagatesToServer = this.propertySet.getBooleanProperty(PropertyKey.readOnlyPropagatesToServer);
         String exceptionInterceptorClasses = this.propertySet.getStringProperty(PropertyKey.exceptionInterceptors).getStringValue();
         if (exceptionInterceptorClasses != null && !"".equals(exceptionInterceptorClasses)) {
            this.exceptionInterceptor = new ExceptionInterceptorChain(exceptionInterceptorClasses, this.props, this.session.getLog());
         }

         if ((Boolean)this.cachePrepStmts.getValue()) {
            this.createPreparedStatementCaches();
         }

         if ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.cacheCallableStmts).getValue()) {
            this.parsedCallableStatementCache = new LRUCache((Integer)this.propertySet.getIntegerProperty(PropertyKey.callableStmtCacheSize).getValue());
         }

         if ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowMultiQueries).getValue()) {
            this.propertySet.getProperty(PropertyKey.cacheResultSetMetadata).setValue(false);
         }

         if ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()) {
            this.resultSetMetadataCache = new LRUCache((Integer)this.propertySet.getIntegerProperty(PropertyKey.metadataCacheSize).getValue());
         }

         if (this.propertySet.getStringProperty(PropertyKey.socksProxyHost).getStringValue() != null) {
            this.propertySet.getProperty(PropertyKey.socketFactory).setValue(SocksProxySocketFactory.class.getName());
         }

         this.dbmd = this.getMetaData(false, false);
         this.initializeSafeQueryInterceptors();
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }

      try {
         this.createNewIO(false);
         this.unSafeQueryInterceptors();
         AbandonedConnectionCleanupThread.trackConnection(this, this.getSession().getNetworkResources());
      } catch (SQLException var3) {
         this.cleanup(var3);
         throw var3;
      } catch (Exception var4) {
         this.cleanup(var4);
         throw SQLError.createSQLException((Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue() ? Messages.getString("Connection.0") : Messages.getString("Connection.1", new Object[]{this.session.getHostInfo().getHost(), this.session.getHostInfo().getPort()}), "08S01", var4, this.getExceptionInterceptor());
      }
   }

   public JdbcPropertySet getPropertySet() {
      return this.propertySet;
   }

   public void unSafeQueryInterceptors() throws SQLException {
      try {
         this.queryInterceptors = (List)this.queryInterceptors.stream().map((u) -> {
            return ((NoSubInterceptorWrapper)u).getUnderlyingInterceptor();
         }).collect(Collectors.toList());
         if (this.session != null) {
            this.session.setQueryInterceptors(this.queryInterceptors);
         }

      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void initializeSafeQueryInterceptors() throws SQLException {
      try {
         this.queryInterceptors = (List)Util.loadClasses(this.propertySet.getStringProperty(PropertyKey.queryInterceptors).getStringValue(), "MysqlIo.BadQueryInterceptor", this.getExceptionInterceptor()).stream().map((o) -> {
            return new NoSubInterceptorWrapper(o.init(this, this.props, this.session.getLog()));
         }).collect(Collectors.toList());
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public List<QueryInterceptor> getQueryInterceptorsInstances() {
      return this.queryInterceptors;
   }

   private boolean canHandleAsServerPreparedStatement(String sql) throws SQLException {
      if (sql != null && sql.length() != 0) {
         if (!(Boolean)this.useServerPrepStmts.getValue()) {
            return false;
         } else {
            boolean allowMultiQueries = (Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowMultiQueries).getValue();
            if ((Boolean)this.cachePrepStmts.getValue()) {
               synchronized(this.serverSideStatementCheckCache) {
                  Boolean flag = (Boolean)this.serverSideStatementCheckCache.get(sql);
                  if (flag != null) {
                     return flag;
                  } else {
                     boolean canHandle = StringUtils.canHandleAsServerPreparedStatementNoCache(sql, this.getServerVersion(), allowMultiQueries, this.session.getServerSession().isNoBackslashEscapesSet(), this.session.getServerSession().useAnsiQuotedIdentifiers());
                     if (sql.length() < (Integer)this.prepStmtCacheSqlLimit.getValue()) {
                        this.serverSideStatementCheckCache.put(sql, canHandle ? Boolean.TRUE : Boolean.FALSE);
                     }

                     return canHandle;
                  }
               }
            } else {
               return StringUtils.canHandleAsServerPreparedStatementNoCache(sql, this.getServerVersion(), allowMultiQueries, this.session.getServerSession().isNoBackslashEscapesSet(), this.session.getServerSession().useAnsiQuotedIdentifiers());
            }
         }
      } else {
         return true;
      }
   }

   public void changeUser(String userName, String newPassword) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.checkClosed();
            if (userName == null || userName.equals("")) {
               userName = "";
            }

            if (newPassword == null) {
               newPassword = "";
            }

            try {
               this.session.changeUser(userName, newPassword, this.database);
            } catch (CJException var7) {
               if ("28000".equals(var7.getSQLState())) {
                  this.cleanup(var7);
               }

               throw var7;
            }

            this.user = userName;
            this.password = newPassword;
            this.session.getServerSession().getCharsetSettings().configurePostHandshake(true);
            this.session.setSessionVariables();
            this.setupServerForTruncationChecks();
         }
      } catch (CJException var9) {
         throw SQLExceptionsMapping.translateException(var9, this.getExceptionInterceptor());
      }
   }

   public void checkClosed() {
      this.session.checkClosed();
   }

   public void throwConnectionClosedException() throws SQLException {
      try {
         SQLException ex = SQLError.createSQLException(Messages.getString("Connection.2"), "08003", this.getExceptionInterceptor());
         if (this.session.getForceClosedReason() != null) {
            ex.initCause(this.session.getForceClosedReason());
         }

         throw ex;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   private void checkTransactionIsolationLevel() {
      String s = this.session.getServerSession().getServerVariable("transaction_isolation");
      if (s == null) {
         s = this.session.getServerSession().getServerVariable("tx_isolation");
      }

      if (s != null) {
         Integer intTI = (Integer)mapTransIsolationNameToValue.get(s);
         if (intTI != null) {
            this.isolationLevel = intTI;
         }
      }

   }

   public void abortInternal() throws SQLException {
      try {
         this.session.forceClose();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void cleanup(Throwable whyCleanedUp) {
      try {
         if (this.session != null) {
            if (this.isClosed()) {
               this.session.forceClose();
            } else {
               this.realClose(false, false, false, whyCleanedUp);
            }
         }
      } catch (CJException | SQLException var3) {
      }

   }

   /** @deprecated */
   @Deprecated
   public void clearHasTriedMaster() {
      this.hasTriedSourceFlag = false;
   }

   public void clearWarnings() throws SQLException {
      try {
         ;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
      try {
         return this.clientPrepareStatement(sql, 1003, 1007);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
      try {
         PreparedStatement pStmt = this.clientPrepareStatement(sql);
         ((ClientPreparedStatement)pStmt).setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
         return pStmt;
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      try {
         return this.clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, boolean processEscapeCodesIfNeeded) throws SQLException {
      try {
         this.checkClosed();
         String nativeSql = processEscapeCodesIfNeeded && (Boolean)this.processEscapeCodesForPrepStmts.getValue() ? this.nativeSQL(sql) : sql;
         ClientPreparedStatement pStmt = null;
         if ((Boolean)this.cachePrepStmts.getValue()) {
            ParseInfo pStmtInfo = (ParseInfo)this.cachedPreparedStatementParams.get(nativeSql);
            if (pStmtInfo == null) {
               pStmt = ClientPreparedStatement.getInstance(this.getMultiHostSafeProxy(), nativeSql, this.database);
               this.cachedPreparedStatementParams.put(nativeSql, pStmt.getParseInfo());
            } else {
               pStmt = ClientPreparedStatement.getInstance(this.getMultiHostSafeProxy(), nativeSql, this.database, pStmtInfo);
            }
         } else {
            pStmt = ClientPreparedStatement.getInstance(this.getMultiHostSafeProxy(), nativeSql, this.database);
         }

         pStmt.setResultSetType(resultSetType);
         pStmt.setResultSetConcurrency(resultSetConcurrency);
         return pStmt;
      } catch (CJException var9) {
         throw SQLExceptionsMapping.translateException(var9, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
      try {
         ClientPreparedStatement pStmt = (ClientPreparedStatement)this.clientPrepareStatement(sql);
         pStmt.setRetrieveGeneratedKeys(autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0);
         return pStmt;
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
      try {
         ClientPreparedStatement pStmt = (ClientPreparedStatement)this.clientPrepareStatement(sql);
         pStmt.setRetrieveGeneratedKeys(autoGenKeyColNames != null && autoGenKeyColNames.length > 0);
         return pStmt;
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
      try {
         return this.clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void close() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            if (this.connectionLifecycleInterceptors != null) {
               Iterator var2 = this.connectionLifecycleInterceptors.iterator();

               while(var2.hasNext()) {
                  ConnectionLifecycleInterceptor cli = (ConnectionLifecycleInterceptor)var2.next();
                  cli.close();
               }
            }

            this.realClose(true, true, false, (Throwable)null);
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void normalClose() {
      try {
         this.close();
      } catch (SQLException var2) {
         ExceptionFactory.createException((String)var2.getMessage(), (Throwable)var2);
      }

   }

   private void closeAllOpenStatements() throws SQLException {
      SQLException postponedException = null;
      Iterator var2 = this.openStatements.iterator();

      while(var2.hasNext()) {
         JdbcStatement stmt = (JdbcStatement)var2.next();

         try {
            ((StatementImpl)stmt).realClose(false, true);
         } catch (SQLException var5) {
            postponedException = var5;
         }
      }

      if (postponedException != null) {
         throw postponedException;
      }
   }

   private void closeStatement(Statement stmt) {
      if (stmt != null) {
         try {
            stmt.close();
         } catch (SQLException var3) {
         }

         stmt = null;
      }

   }

   public void commit() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.checkClosed();

            try {
               if (this.connectionLifecycleInterceptors != null) {
                  IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator()) {
                     void forEach(ConnectionLifecycleInterceptor each) throws SQLException {
                        if (!each.commit()) {
                           this.stopIterating = true;
                        }

                     }
                  };
                  iter.doForAll();
                  if (!iter.fullIteration()) {
                     return;
                  }
               }

               if (this.session.getServerSession().isAutoCommit()) {
                  throw SQLError.createSQLException(Messages.getString("Connection.3"), this.getExceptionInterceptor());
               }

               if ((Boolean)this.useLocalTransactionState.getValue() && !this.session.getServerSession().inTransactionOnServer()) {
                  return;
               }

               this.session.execSQL((Query)null, "commit", -1, (NativePacketPayload)null, false, this.nullStatementResultSetFactory, (ColumnDefinition)null, false);
            } catch (SQLException var10) {
               if ("08S01".equals(var10.getSQLState())) {
                  throw SQLError.createSQLException(Messages.getString("Connection.4"), "08007", this.getExceptionInterceptor());
               }

               throw var10;
            } finally {
               this.session.setNeedsPing((Boolean)this.reconnectAtTxEnd.getValue());
            }

         }
      } catch (CJException var13) {
         throw SQLExceptionsMapping.translateException(var13, this.getExceptionInterceptor());
      }
   }

   public void createNewIO(boolean isForReconnect) {
      try {
         synchronized(this.getConnectionMutex()) {
            try {
               if (!(Boolean)this.autoReconnect.getValue()) {
                  this.connectOneTryOnly(isForReconnect);
                  return;
               }

               this.connectWithRetries(isForReconnect);
            } catch (SQLException var6) {
               throw (UnableToConnectException)ExceptionFactory.createException((Class)UnableToConnectException.class, (String)var6.getMessage(), (Throwable)var6);
            }

         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   private void connectWithRetries(boolean isForReconnect) throws SQLException {
      double timeout = (double)(Integer)this.propertySet.getIntegerProperty(PropertyKey.initialTimeout).getValue();
      boolean connectionGood = false;
      Exception connectionException = null;

      for(int attemptCount = 0; attemptCount < (Integer)this.propertySet.getIntegerProperty(PropertyKey.maxReconnects).getValue() && !connectionGood; ++attemptCount) {
         try {
            this.session.forceClose();
            JdbcConnection c = this.getProxy();
            this.session.connect(this.origHostInfo, this.user, this.password, this.database, this.getLoginTimeout(), c);
            this.pingInternal(false, 0);
            boolean oldAutoCommit;
            int oldIsolationLevel;
            boolean oldReadOnly;
            String oldDb;
            synchronized(this.getConnectionMutex()) {
               oldAutoCommit = this.getAutoCommit();
               oldIsolationLevel = this.isolationLevel;
               oldReadOnly = this.isReadOnly(false);
               oldDb = this.getDatabase();
               this.session.setQueryInterceptors(this.queryInterceptors);
            }

            this.initializePropsFromServer();
            if (isForReconnect) {
               this.setAutoCommit(oldAutoCommit);
               this.setTransactionIsolation(oldIsolationLevel);
               this.setDatabase(oldDb);
               this.setReadOnly(oldReadOnly);
            }

            connectionGood = true;
            break;
         } catch (UnableToConnectException var16) {
            this.close();
            this.session.getProtocol().getSocketConnection().forceClose();
         } catch (Exception var17) {
            connectionException = var17;
            connectionGood = false;
         }

         if (connectionGood) {
            break;
         }

         if (attemptCount > 0) {
            try {
               Thread.sleep((long)timeout * 1000L);
            } catch (InterruptedException var14) {
            }
         }
      }

      if (!connectionGood) {
         SQLException chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnectWithRetries", new Object[]{this.propertySet.getIntegerProperty(PropertyKey.maxReconnects).getValue()}), "08001", connectionException, this.getExceptionInterceptor());
         throw chainedEx;
      } else {
         if ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue() && !(Boolean)this.autoReconnect.getValue()) {
            this.password = null;
            this.user = null;
         }

         if (isForReconnect) {
            Iterator<JdbcStatement> statementIter = this.openStatements.iterator();
            Stack<JdbcStatement> serverPreparedStatements = null;

            while(statementIter.hasNext()) {
               JdbcStatement statementObj = (JdbcStatement)statementIter.next();
               if (statementObj instanceof ServerPreparedStatement) {
                  if (serverPreparedStatements == null) {
                     serverPreparedStatements = new Stack();
                  }

                  serverPreparedStatements.add(statementObj);
               }
            }

            if (serverPreparedStatements != null) {
               while(!serverPreparedStatements.isEmpty()) {
                  ((ServerPreparedStatement)serverPreparedStatements.pop()).rePrepare();
               }
            }
         }

      }
   }

   private void connectOneTryOnly(boolean isForReconnect) throws SQLException {
      Exception connectionNotEstablishedBecause = null;

      try {
         JdbcConnection c = this.getProxy();
         this.session.connect(this.origHostInfo, this.user, this.password, this.database, this.getLoginTimeout(), c);
         boolean oldAutoCommit = this.getAutoCommit();
         int oldIsolationLevel = this.isolationLevel;
         boolean oldReadOnly = this.isReadOnly(false);
         String oldDb = this.getDatabase();
         this.session.setQueryInterceptors(this.queryInterceptors);
         this.initializePropsFromServer();
         if (isForReconnect) {
            this.setAutoCommit(oldAutoCommit);
            this.setTransactionIsolation(oldIsolationLevel);
            this.setDatabase(oldDb);
            this.setReadOnly(oldReadOnly);
         }

      } catch (UnableToConnectException var8) {
         this.close();
         this.session.getProtocol().getSocketConnection().forceClose();
         throw var8;
      } catch (Exception var9) {
         if (!(var9 instanceof PasswordExpiredException) && (!(var9 instanceof SQLException) || ((SQLException)var9).getErrorCode() != 1820) || (Boolean)this.disconnectOnExpiredPasswords.getValue()) {
            if (this.session != null) {
               this.session.forceClose();
            }

            if (var9 instanceof SQLException) {
               throw (SQLException)var9;
            } else if (var9.getCause() != null && var9.getCause() instanceof SQLException) {
               throw (SQLException)var9.getCause();
            } else if (var9 instanceof CJException) {
               throw (CJException)var9;
            } else {
               SQLException chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnect"), "08001", this.getExceptionInterceptor());
               chainedEx.initCause(var9);
               throw chainedEx;
            }
         }
      }
   }

   private int getLoginTimeout() {
      int loginTimeoutSecs = DriverManager.getLoginTimeout();
      return loginTimeoutSecs <= 0 ? 0 : loginTimeoutSecs * 1000;
   }

   private void createPreparedStatementCaches() throws SQLException {
      synchronized(this.getConnectionMutex()) {
         int cacheSize = (Integer)this.propertySet.getIntegerProperty(PropertyKey.prepStmtCacheSize).getValue();
         String parseInfoCacheFactory = (String)this.propertySet.getStringProperty(PropertyKey.parseInfoCacheFactory).getValue();

         SQLException sqlEx;
         try {
            Class<?> factoryClass = Class.forName(parseInfoCacheFactory);
            CacheAdapterFactory<String, ParseInfo> cacheFactory = (CacheAdapterFactory)factoryClass.newInstance();
            this.cachedPreparedStatementParams = cacheFactory.getInstance(this, this.origHostInfo.getDatabaseUrl(), cacheSize, (Integer)this.prepStmtCacheSqlLimit.getValue());
         } catch (InstantiationException | IllegalAccessException | ClassNotFoundException var7) {
            sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantFindCacheFactory", new Object[]{parseInfoCacheFactory, PropertyKey.parseInfoCacheFactory}), this.getExceptionInterceptor());
            sqlEx.initCause(var7);
            throw sqlEx;
         } catch (Exception var8) {
            sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[]{parseInfoCacheFactory, PropertyKey.parseInfoCacheFactory}), this.getExceptionInterceptor());
            sqlEx.initCause(var8);
            throw sqlEx;
         }

         if ((Boolean)this.useServerPrepStmts.getValue()) {
            this.serverSideStatementCheckCache = new LRUCache(cacheSize);
            this.serverSideStatementCache = new LRUCache<CompoundCacheKey, ServerPreparedStatement>(cacheSize) {
               private static final long serialVersionUID = 7692318650375988114L;

               protected boolean removeEldestEntry(Map.Entry<CompoundCacheKey, ServerPreparedStatement> eldest) {
                  if (this.maxElements <= 1) {
                     return false;
                  } else {
                     boolean removeIt = super.removeEldestEntry(eldest);
                     if (removeIt) {
                        ServerPreparedStatement ps = (ServerPreparedStatement)eldest.getValue();
                        ps.isCached = false;
                        ps.setClosed(false);

                        try {
                           ps.realClose(true, true);
                        } catch (SQLException var5) {
                        }
                     }

                     return removeIt;
                  }
               }
            };
         }

      }
   }

   public Statement createStatement() throws SQLException {
      try {
         return this.createStatement(1003, 1007);
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
      try {
         this.checkClosed();
         StatementImpl stmt = new StatementImpl(this.getMultiHostSafeProxy(), this.database);
         stmt.setResultSetType(resultSetType);
         stmt.setResultSetConcurrency(resultSetConcurrency);
         return stmt;
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
      try {
         if ((Boolean)this.pedantic.getValue() && resultSetHoldability != 1) {
            throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", this.getExceptionInterceptor());
         } else {
            return this.createStatement(resultSetType, resultSetConcurrency);
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public int getActiveStatementCount() {
      return this.openStatements.size();
   }

   public boolean getAutoCommit() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            return this.session.getServerSession().isAutoCommit();
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public String getCatalog() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            return this.propertySet.getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA ? null : this.database;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public String getCharacterSetMetadata() {
      synchronized(this.getConnectionMutex()) {
         return this.session.getServerSession().getCharsetSettings().getMetadataEncoding();
      }
   }

   public int getHoldability() throws SQLException {
      try {
         return 2;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public long getId() {
      return this.session.getThreadId();
   }

   public long getIdleFor() {
      synchronized(this.getConnectionMutex()) {
         return this.session.getIdleFor();
      }
   }

   public java.sql.DatabaseMetaData getMetaData() throws SQLException {
      try {
         return this.getMetaData(true, true);
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   private java.sql.DatabaseMetaData getMetaData(boolean checkClosed, boolean checkForInfoSchema) throws SQLException {
      try {
         if (checkClosed) {
            this.checkClosed();
         }

         DatabaseMetaData dbmeta = DatabaseMetaData.getInstance(this.getMultiHostSafeProxy(), this.database, checkForInfoSchema, this.nullStatementResultSetFactory);
         if (this.getSession() != null && this.getSession().getProtocol() != null) {
            dbmeta.setMetadataEncoding(this.getSession().getServerSession().getCharsetSettings().getMetadataEncoding());
            dbmeta.setMetadataCollationIndex(this.getSession().getServerSession().getCharsetSettings().getMetadataCollationIndex());
         }

         return dbmeta;
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public Statement getMetadataSafeStatement() throws SQLException {
      try {
         return this.getMetadataSafeStatement(0);
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public Statement getMetadataSafeStatement(int maxRows) throws SQLException {
      Statement stmt = this.createStatement();
      stmt.setMaxRows(maxRows == -1 ? 0 : maxRows);
      stmt.setEscapeProcessing(false);
      if (stmt.getFetchSize() != 0) {
         stmt.setFetchSize(0);
      }

      return stmt;
   }

   public ServerVersion getServerVersion() {
      return this.session.getServerSession().getServerVersion();
   }

   public int getTransactionIsolation() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            if ((Boolean)this.useLocalSessionState.getValue()) {
               return this.isolationLevel;
            } else {
               String s = this.session.queryServerVariable(!this.versionMeetsMinimum(8, 0, 3) && (!this.versionMeetsMinimum(5, 7, 20) || this.versionMeetsMinimum(8, 0, 0)) ? "@@session.tx_isolation" : "@@session.transaction_isolation");
               if (s != null) {
                  Integer intTI = (Integer)mapTransIsolationNameToValue.get(s);
                  if (intTI != null) {
                     this.isolationLevel = intTI;
                     return this.isolationLevel;
                  } else {
                     throw SQLError.createSQLException(Messages.getString("Connection.12", new Object[]{s}), "S1000", this.getExceptionInterceptor());
                  }
               } else {
                  throw SQLError.createSQLException(Messages.getString("Connection.13"), "S1000", this.getExceptionInterceptor());
               }
            }
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public Map<String, Class<?>> getTypeMap() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            if (this.typeMap == null) {
               this.typeMap = new HashMap();
            }

            return this.typeMap;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public String getURL() {
      return this.origHostInfo.getDatabaseUrl();
   }

   public String getUser() {
      return this.user;
   }

   public SQLWarning getWarnings() throws SQLException {
      try {
         return null;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean hasSameProperties(JdbcConnection c) {
      return this.props.equals(c.getProperties());
   }

   public Properties getProperties() {
      return this.props;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasTriedMaster() {
      return this.hasTriedSourceFlag;
   }

   private void initializePropsFromServer() throws SQLException {
      String connectionInterceptorClasses = this.propertySet.getStringProperty(PropertyKey.connectionLifecycleInterceptors).getStringValue();
      this.connectionLifecycleInterceptors = null;
      if (connectionInterceptorClasses != null) {
         try {
            this.connectionLifecycleInterceptors = (List)Util.loadClasses(this.propertySet.getStringProperty(PropertyKey.connectionLifecycleInterceptors).getStringValue(), "Connection.badLifecycleInterceptor", this.getExceptionInterceptor()).stream().map((o) -> {
               return o.init(this, this.props, this.session.getLog());
            }).collect(Collectors.toList());
         } catch (CJException var4) {
            throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
         }
      }

      this.session.setSessionVariables();
      this.session.loadServerVariables(this.getConnectionMutex(), this.dbmd.getDriverVersion());
      this.autoIncrementIncrement = this.session.getServerSession().getServerVariable("auto_increment_increment", 1);

      try {
         LicenseConfiguration.checkLicenseType(this.session.getServerSession().getServerVariables());
      } catch (CJException var3) {
         throw SQLError.createSQLException(var3.getMessage(), "08001", this.getExceptionInterceptor());
      }

      this.session.getProtocol().initServerSession();
      this.checkTransactionIsolationLevel();
      this.handleAutoCommitDefaults();
      ((DatabaseMetaData)this.dbmd).setMetadataEncoding(this.session.getServerSession().getCharsetSettings().getMetadataEncoding());
      ((DatabaseMetaData)this.dbmd).setMetadataCollationIndex(this.session.getServerSession().getCharsetSettings().getMetadataCollationIndex());
      this.setupServerForTruncationChecks();
   }

   private void handleAutoCommitDefaults() throws SQLException {
      try {
         boolean resetAutoCommitDefault = false;
         String initConnectValue = this.session.getServerSession().getServerVariable("init_connect");
         if (initConnectValue != null && initConnectValue.length() > 0) {
            String s = this.session.queryServerVariable("@@session.autocommit");
            if (s != null) {
               this.session.getServerSession().setAutoCommit(Boolean.parseBoolean(s));
               if (!this.session.getServerSession().isAutoCommit()) {
                  resetAutoCommitDefault = true;
               }
            }
         } else {
            resetAutoCommitDefault = true;
         }

         if (resetAutoCommitDefault) {
            try {
               this.setAutoCommit(true);
            } catch (SQLException var5) {
               if (var5.getErrorCode() != 1820 || (Boolean)this.disconnectOnExpiredPasswords.getValue()) {
                  throw var5;
               }
            }
         }

      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public boolean isClosed() {
      try {
         return this.session.isClosed();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean isInGlobalTx() {
      return this.isInGlobalTx;
   }

   public boolean isSourceConnection() {
      return false;
   }

   public boolean isReadOnly() throws SQLException {
      try {
         return this.isReadOnly(true);
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean isReadOnly(boolean useSessionStatus) throws SQLException {
      try {
         if (useSessionStatus && !this.session.isClosed() && this.versionMeetsMinimum(5, 6, 5) && !(Boolean)this.useLocalSessionState.getValue() && (Boolean)this.readOnlyPropagatesToServer.getValue()) {
            String s = this.session.queryServerVariable(!this.versionMeetsMinimum(8, 0, 3) && (!this.versionMeetsMinimum(5, 7, 20) || this.versionMeetsMinimum(8, 0, 0)) ? "@@session.tx_read_only" : "@@session.transaction_read_only");
            if (s != null) {
               return Integer.parseInt(s) != 0;
            }
         }

         return this.readOnly;
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public boolean isSameResource(JdbcConnection otherConnection) {
      synchronized(this.getConnectionMutex()) {
         if (otherConnection == null) {
            return false;
         } else {
            boolean directCompare = true;
            String otherHost = ((ConnectionImpl)otherConnection).origHostToConnectTo;
            String otherOrigDatabase = ((ConnectionImpl)otherConnection).origHostInfo.getDatabase();
            String otherCurrentDb = ((ConnectionImpl)otherConnection).database;
            if (!nullSafeCompare(otherHost, this.origHostToConnectTo)) {
               directCompare = false;
            } else if (otherHost != null && otherHost.indexOf(44) == -1 && otherHost.indexOf(58) == -1) {
               directCompare = ((ConnectionImpl)otherConnection).origPortToConnectTo == this.origPortToConnectTo;
            }

            if (directCompare && (!nullSafeCompare(otherOrigDatabase, this.origHostInfo.getDatabase()) || !nullSafeCompare(otherCurrentDb, this.database))) {
               directCompare = false;
            }

            if (directCompare) {
               return true;
            } else {
               String otherResourceId = (String)((ConnectionImpl)otherConnection).getPropertySet().getStringProperty(PropertyKey.resourceId).getValue();
               String myResourceId = (String)this.propertySet.getStringProperty(PropertyKey.resourceId).getValue();
               if (otherResourceId != null || myResourceId != null) {
                  directCompare = nullSafeCompare(otherResourceId, myResourceId);
                  if (directCompare) {
                     return true;
                  }
               }

               return false;
            }
         }
      }
   }

   public int getAutoIncrementIncrement() {
      return this.autoIncrementIncrement;
   }

   public boolean lowerCaseTableNames() {
      return this.session.getServerSession().isLowerCaseTableNames();
   }

   public String nativeSQL(String sql) throws SQLException {
      try {
         if (sql == null) {
            return null;
         } else {
            Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.getMultiHostSafeProxy().getSession().getServerSession().getSessionTimeZone(), this.getMultiHostSafeProxy().getSession().getServerSession().getCapabilities().serverSupportsFracSecs(), this.getMultiHostSafeProxy().getSession().getServerSession().isServerTruncatesFracSecs(), this.getExceptionInterceptor());
            return escapedSqlResult instanceof String ? (String)escapedSqlResult : ((EscapeProcessorResult)escapedSqlResult).escapedSql;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   private CallableStatement parseCallableStatement(String sql) throws SQLException {
      Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.getMultiHostSafeProxy().getSession().getServerSession().getSessionTimeZone(), this.getMultiHostSafeProxy().getSession().getServerSession().getCapabilities().serverSupportsFracSecs(), this.getMultiHostSafeProxy().getSession().getServerSession().isServerTruncatesFracSecs(), this.getExceptionInterceptor());
      boolean isFunctionCall = false;
      String parsedSql = null;
      if (escapedSqlResult instanceof EscapeProcessorResult) {
         parsedSql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
         isFunctionCall = ((EscapeProcessorResult)escapedSqlResult).callingStoredFunction;
      } else {
         parsedSql = (String)escapedSqlResult;
         isFunctionCall = false;
      }

      return CallableStatement.getInstance(this.getMultiHostSafeProxy(), parsedSql, this.database, isFunctionCall);
   }

   public void ping() throws SQLException {
      try {
         this.pingInternal(true, 0);
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
      try {
         this.session.ping(checkForClosedConnection, timeoutMillis);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
      try {
         return this.prepareCall(sql, 1003, 1007);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      try {
         CallableStatement cStmt = null;
         if (!(Boolean)this.propertySet.getBooleanProperty(PropertyKey.cacheCallableStmts).getValue()) {
            cStmt = this.parseCallableStatement(sql);
         } else {
            synchronized(this.parsedCallableStatementCache) {
               CompoundCacheKey key = new CompoundCacheKey(this.getDatabase(), sql);
               CallableStatement.CallableStatementParamInfo cachedParamInfo = (CallableStatement.CallableStatementParamInfo)this.parsedCallableStatementCache.get(key);
               if (cachedParamInfo != null) {
                  cStmt = CallableStatement.getInstance(this.getMultiHostSafeProxy(), cachedParamInfo);
               } else {
                  cStmt = this.parseCallableStatement(sql);
                  synchronized(cStmt) {
                     cachedParamInfo = cStmt.paramInfo;
                  }

                  this.parsedCallableStatementCache.put(key, cachedParamInfo);
               }
            }
         }

         cStmt.setResultSetType(resultSetType);
         cStmt.setResultSetConcurrency(resultSetConcurrency);
         return cStmt;
      } catch (CJException var14) {
         throw SQLExceptionsMapping.translateException(var14, this.getExceptionInterceptor());
      }
   }

   public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
      try {
         if ((Boolean)this.pedantic.getValue() && resultSetHoldability != 1) {
            throw SQLError.createSQLException(Messages.getString("Connection.17"), "S1009", this.getExceptionInterceptor());
         } else {
            CallableStatement cStmt = (CallableStatement)this.prepareCall(sql, resultSetType, resultSetConcurrency);
            return cStmt;
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement prepareStatement(String sql) throws SQLException {
      try {
         return this.prepareStatement(sql, 1003, 1007);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement prepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
      try {
         PreparedStatement pStmt = this.prepareStatement(sql);
         ((ClientPreparedStatement)pStmt).setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
         return pStmt;
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.checkClosed();
            ClientPreparedStatement pStmt = null;
            boolean canServerPrepare = true;
            String nativeSql = (Boolean)this.processEscapeCodesForPrepStmts.getValue() ? this.nativeSQL(sql) : sql;
            if ((Boolean)this.useServerPrepStmts.getValue() && (Boolean)this.emulateUnsupportedPstmts.getValue()) {
               canServerPrepare = this.canHandleAsServerPreparedStatement(nativeSql);
            }

            if ((Boolean)this.useServerPrepStmts.getValue() && canServerPrepare) {
               if ((Boolean)this.cachePrepStmts.getValue()) {
                  synchronized(this.serverSideStatementCache) {
                     pStmt = (ClientPreparedStatement)this.serverSideStatementCache.remove(new CompoundCacheKey(this.database, sql));
                     if (pStmt != null) {
                        ((ServerPreparedStatement)pStmt).setClosed(false);
                        ((ClientPreparedStatement)pStmt).clearParameters();
                     }

                     if (pStmt == null) {
                        try {
                           pStmt = ServerPreparedStatement.getInstance(this.getMultiHostSafeProxy(), nativeSql, this.database, resultSetType, resultSetConcurrency);
                           if (sql.length() < (Integer)this.prepStmtCacheSqlLimit.getValue()) {
                              ((ServerPreparedStatement)pStmt).isCacheable = true;
                           }

                           ((ClientPreparedStatement)pStmt).setResultSetType(resultSetType);
                           ((ClientPreparedStatement)pStmt).setResultSetConcurrency(resultSetConcurrency);
                        } catch (SQLException var14) {
                           if (!(Boolean)this.emulateUnsupportedPstmts.getValue()) {
                              throw var14;
                           }

                           pStmt = (ClientPreparedStatement)this.clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
                           if (sql.length() < (Integer)this.prepStmtCacheSqlLimit.getValue()) {
                              this.serverSideStatementCheckCache.put(sql, Boolean.FALSE);
                           }
                        }
                     }
                  }
               } else {
                  try {
                     pStmt = ServerPreparedStatement.getInstance(this.getMultiHostSafeProxy(), nativeSql, this.database, resultSetType, resultSetConcurrency);
                     ((ClientPreparedStatement)pStmt).setResultSetType(resultSetType);
                     ((ClientPreparedStatement)pStmt).setResultSetConcurrency(resultSetConcurrency);
                  } catch (SQLException var13) {
                     if (!(Boolean)this.emulateUnsupportedPstmts.getValue()) {
                        throw var13;
                     }

                     pStmt = (ClientPreparedStatement)this.clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
                  }
               }
            } else {
               pStmt = (ClientPreparedStatement)this.clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
            }

            return (PreparedStatement)pStmt;
         }
      } catch (CJException var17) {
         throw SQLExceptionsMapping.translateException(var17, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
      try {
         if ((Boolean)this.pedantic.getValue() && resultSetHoldability != 1) {
            throw SQLError.createSQLException(Messages.getString("Connection.17"), "S1009", this.getExceptionInterceptor());
         } else {
            return this.prepareStatement(sql, resultSetType, resultSetConcurrency);
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
      try {
         PreparedStatement pStmt = this.prepareStatement(sql);
         ((ClientPreparedStatement)pStmt).setRetrieveGeneratedKeys(autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0);
         return pStmt;
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
      try {
         PreparedStatement pStmt = this.prepareStatement(sql);
         ((ClientPreparedStatement)pStmt).setRetrieveGeneratedKeys(autoGenKeyColNames != null && autoGenKeyColNames.length > 0);
         return pStmt;
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason) throws SQLException {
      try {
         SQLException sqlEx = null;
         if (!this.isClosed()) {
            this.session.setForceClosedReason(reason);

            try {
               if (!skipLocalTeardown) {
                  if (!this.getAutoCommit() && issueRollback) {
                     try {
                        this.rollback();
                     } catch (SQLException var14) {
                        sqlEx = var14;
                     }
                  }

                  if ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.gatherPerfMetrics).getValue()) {
                     this.session.getProtocol().getMetricsHolder().reportMetrics(this.session.getLog());
                  }

                  if ((Boolean)this.useUsageAdvisor.getValue()) {
                     if (!calledExplicitly) {
                        this.session.getProfilerEventHandler().processEvent((byte)0, this.session, (Query)null, (Resultset)null, 0L, new Throwable(), Messages.getString("Connection.18"));
                     }

                     if (System.currentTimeMillis() - this.session.getConnectionCreationTimeMillis() < 500L) {
                        this.session.getProfilerEventHandler().processEvent((byte)0, this.session, (Query)null, (Resultset)null, 0L, new Throwable(), Messages.getString("Connection.19"));
                     }
                  }

                  try {
                     this.closeAllOpenStatements();
                  } catch (SQLException var13) {
                     sqlEx = var13;
                  }

                  this.session.quit();
               } else {
                  this.session.forceClose();
               }

               if (this.queryInterceptors != null) {
                  for(int i = 0; i < this.queryInterceptors.size(); ++i) {
                     ((QueryInterceptor)this.queryInterceptors.get(i)).destroy();
                  }
               }

               if (this.exceptionInterceptor != null) {
                  this.exceptionInterceptor.destroy();
               }
            } finally {
               this.openStatements.clear();
               this.queryInterceptors = null;
               this.exceptionInterceptor = null;
               this.nullStatementResultSetFactory = null;
            }

            if (sqlEx != null) {
               throw sqlEx;
            }
         }
      } catch (CJException var16) {
         throw SQLExceptionsMapping.translateException(var16, this.getExceptionInterceptor());
      }
   }

   public void recachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            if ((Boolean)this.cachePrepStmts.getValue() && pstmt.isPoolable()) {
               synchronized(this.serverSideStatementCache) {
                  Object oldServerPrepStmt = this.serverSideStatementCache.put(new CompoundCacheKey(pstmt.getCurrentDatabase(), ((PreparedQuery)pstmt.getQuery()).getOriginalSql()), (ServerPreparedStatement)pstmt);
                  if (oldServerPrepStmt != null && oldServerPrepStmt != pstmt) {
                     ((ServerPreparedStatement)oldServerPrepStmt).isCached = false;
                     ((ServerPreparedStatement)oldServerPrepStmt).setClosed(false);
                     ((ServerPreparedStatement)oldServerPrepStmt).realClose(true, true);
                  }
               }
            }

         }
      } catch (CJException var10) {
         throw SQLExceptionsMapping.translateException(var10, this.getExceptionInterceptor());
      }
   }

   public void decachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            if ((Boolean)this.cachePrepStmts.getValue()) {
               synchronized(this.serverSideStatementCache) {
                  this.serverSideStatementCache.remove(new CompoundCacheKey(pstmt.getCurrentDatabase(), ((PreparedQuery)pstmt.getQuery()).getOriginalSql()));
               }
            }

         }
      } catch (CJException var9) {
         throw SQLExceptionsMapping.translateException(var9, this.getExceptionInterceptor());
      }
   }

   public void registerStatement(JdbcStatement stmt) {
      this.openStatements.addIfAbsent(stmt);
   }

   public void releaseSavepoint(Savepoint arg0) throws SQLException {
      try {
         ;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void resetServerState() throws SQLException {
      try {
         if (!(Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue() && this.session != null) {
            this.changeUser(this.user, this.password);
         }

      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void rollback() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.checkClosed();

            try {
               if (this.connectionLifecycleInterceptors != null) {
                  IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator()) {
                     void forEach(ConnectionLifecycleInterceptor each) throws SQLException {
                        if (!each.rollback()) {
                           this.stopIterating = true;
                        }

                     }
                  };
                  iter.doForAll();
                  if (!iter.fullIteration()) {
                     return;
                  }
               }

               if (this.session.getServerSession().isAutoCommit()) {
                  throw SQLError.createSQLException(Messages.getString("Connection.20"), "08003", this.getExceptionInterceptor());
               }

               try {
                  this.rollbackNoChecks();
               } catch (SQLException var11) {
                  if ((Boolean)this.ignoreNonTxTables.getInitialValue() && var11.getErrorCode() == 1196) {
                     return;
                  }

                  throw var11;
               }
            } catch (SQLException var12) {
               if ("08S01".equals(var12.getSQLState())) {
                  throw SQLError.createSQLException(Messages.getString("Connection.21"), "08007", this.getExceptionInterceptor());
               }

               throw var12;
            } finally {
               this.session.setNeedsPing((Boolean)this.reconnectAtTxEnd.getValue());
            }

         }
      } catch (CJException var15) {
         throw SQLExceptionsMapping.translateException(var15, this.getExceptionInterceptor());
      }
   }

   public void rollback(final Savepoint savepoint) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.checkClosed();

            try {
               if (this.connectionLifecycleInterceptors != null) {
                  IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator()) {
                     void forEach(ConnectionLifecycleInterceptor each) throws SQLException {
                        if (!each.rollback(savepoint)) {
                           this.stopIterating = true;
                        }

                     }
                  };
                  iter.doForAll();
                  if (!iter.fullIteration()) {
                     return;
                  }
               }

               StringBuilder rollbackQuery = new StringBuilder("ROLLBACK TO SAVEPOINT ");
               rollbackQuery.append('`');
               rollbackQuery.append(savepoint.getSavepointName());
               rollbackQuery.append('`');
               Statement stmt = null;

               try {
                  stmt = this.getMetadataSafeStatement();
                  stmt.executeUpdate(rollbackQuery.toString());
               } catch (SQLException var23) {
                  int errno = var23.getErrorCode();
                  if (errno == 1181) {
                     String msg = var23.getMessage();
                     if (msg != null) {
                        int indexOfError153 = msg.indexOf("153");
                        if (indexOfError153 != -1) {
                           throw SQLError.createSQLException(Messages.getString("Connection.22", new Object[]{savepoint.getSavepointName()}), "S1009", errno, this.getExceptionInterceptor());
                        }
                     }
                  }

                  if ((Boolean)this.ignoreNonTxTables.getValue() && var23.getErrorCode() != 1196) {
                     throw var23;
                  } else if ("08S01".equals(var23.getSQLState())) {
                     throw SQLError.createSQLException(Messages.getString("Connection.23"), "08007", this.getExceptionInterceptor());
                  } else {
                     throw var23;
                  }
               } finally {
                  this.closeStatement(stmt);
               }
            } finally {
               this.session.setNeedsPing((Boolean)this.reconnectAtTxEnd.getValue());
            }
         }
      } catch (CJException var27) {
         throw SQLExceptionsMapping.translateException(var27, this.getExceptionInterceptor());
      }
   }

   private void rollbackNoChecks() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            if (!(Boolean)this.useLocalTransactionState.getValue() || this.session.getServerSession().inTransactionOnServer()) {
               this.session.execSQL((Query)null, "rollback", -1, (NativePacketPayload)null, false, this.nullStatementResultSetFactory, (ColumnDefinition)null, false);
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
      try {
         String nativeSql = (Boolean)this.processEscapeCodesForPrepStmts.getValue() ? this.nativeSQL(sql) : sql;
         return ServerPreparedStatement.getInstance(this.getMultiHostSafeProxy(), nativeSql, this.getDatabase(), 1003, 1007);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
      try {
         String nativeSql = (Boolean)this.processEscapeCodesForPrepStmts.getValue() ? this.nativeSQL(sql) : sql;
         ClientPreparedStatement pStmt = ServerPreparedStatement.getInstance(this.getMultiHostSafeProxy(), nativeSql, this.getDatabase(), 1003, 1007);
         pStmt.setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
         return pStmt;
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      try {
         String nativeSql = (Boolean)this.processEscapeCodesForPrepStmts.getValue() ? this.nativeSQL(sql) : sql;
         return ServerPreparedStatement.getInstance(this.getMultiHostSafeProxy(), nativeSql, this.getDatabase(), resultSetType, resultSetConcurrency);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
      try {
         if ((Boolean)this.pedantic.getValue() && resultSetHoldability != 1) {
            throw SQLError.createSQLException(Messages.getString("Connection.17"), "S1009", this.getExceptionInterceptor());
         } else {
            return this.serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
      try {
         ClientPreparedStatement pStmt = (ClientPreparedStatement)this.serverPrepareStatement(sql);
         pStmt.setRetrieveGeneratedKeys(autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0);
         return pStmt;
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
      try {
         ClientPreparedStatement pStmt = (ClientPreparedStatement)this.serverPrepareStatement(sql);
         pStmt.setRetrieveGeneratedKeys(autoGenKeyColNames != null && autoGenKeyColNames.length > 0);
         return pStmt;
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void setAutoCommit(final boolean autoCommitFlag) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.checkClosed();
            if (this.connectionLifecycleInterceptors != null) {
               IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator()) {
                  void forEach(ConnectionLifecycleInterceptor each) throws SQLException {
                     if (!each.setAutoCommit(autoCommitFlag)) {
                        this.stopIterating = true;
                     }

                  }
               };
               iter.doForAll();
               if (!iter.fullIteration()) {
                  return;
               }
            }

            if ((Boolean)this.autoReconnectForPools.getValue()) {
               this.autoReconnect.setValue(true);
            }

            boolean isAutocommit = this.session.getServerSession().isAutocommit();

            try {
               boolean needsSetOnServer = true;
               if ((Boolean)this.useLocalSessionState.getValue() && isAutocommit == autoCommitFlag) {
                  needsSetOnServer = false;
               } else if (!(Boolean)this.autoReconnect.getValue()) {
                  needsSetOnServer = this.getSession().isSetNeededForAutoCommitMode(autoCommitFlag);
               }

               this.session.getServerSession().setAutoCommit(autoCommitFlag);
               if (needsSetOnServer) {
                  this.session.execSQL((Query)null, autoCommitFlag ? "SET autocommit=1" : "SET autocommit=0", -1, (NativePacketPayload)null, false, this.nullStatementResultSetFactory, (ColumnDefinition)null, false);
               }
            } catch (CJCommunicationsException var13) {
               throw var13;
            } catch (CJException var14) {
               this.session.getServerSession().setAutoCommit(isAutocommit);
               throw SQLError.createSQLException(var14.getMessage(), var14.getSQLState(), var14.getVendorCode(), var14.isTransient(), var14, this.getExceptionInterceptor());
            } finally {
               if ((Boolean)this.autoReconnectForPools.getValue()) {
                  this.autoReconnect.setValue(false);
               }

            }

         }
      } catch (CJException var17) {
         throw SQLExceptionsMapping.translateException(var17, this.getExceptionInterceptor());
      }
   }

   public void setCatalog(String catalog) throws SQLException {
      try {
         if (this.propertySet.getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.CATALOG) {
            this.setDatabase(catalog);
         }

      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void setDatabase(final String db) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.checkClosed();
            if (db == null) {
               throw SQLError.createSQLException("Database can not be null", "S1009", this.getExceptionInterceptor());
            } else {
               if (this.connectionLifecycleInterceptors != null) {
                  IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator()) {
                     void forEach(ConnectionLifecycleInterceptor each) throws SQLException {
                        if (!each.setDatabase(db)) {
                           this.stopIterating = true;
                        }

                     }
                  };
                  iter.doForAll();
                  if (!iter.fullIteration()) {
                     return;
                  }
               }

               if ((Boolean)this.useLocalSessionState.getValue()) {
                  if (this.session.getServerSession().isLowerCaseTableNames()) {
                     if (this.database.equalsIgnoreCase(db)) {
                        return;
                     }
                  } else if (this.database.equals(db)) {
                     return;
                  }
               }

               String quotedId = this.session.getIdentifierQuoteString();
               if (quotedId == null || quotedId.equals(" ")) {
                  quotedId = "";
               }

               StringBuilder query = new StringBuilder("USE ");
               query.append(StringUtils.quoteIdentifier(db, quotedId, (Boolean)this.pedantic.getValue()));
               this.session.execSQL((Query)null, query.toString(), -1, (NativePacketPayload)null, false, this.nullStatementResultSetFactory, (ColumnDefinition)null, false);
               this.database = db;
            }
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public String getDatabase() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            return this.database;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void setFailedOver(boolean flag) {
   }

   public void setHoldability(int arg0) throws SQLException {
      try {
         ;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void setInGlobalTx(boolean flag) {
      this.isInGlobalTx = flag;
   }

   public void setReadOnly(boolean readOnlyFlag) throws SQLException {
      try {
         this.checkClosed();
         this.setReadOnlyInternal(readOnlyFlag);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            if ((Boolean)this.readOnlyPropagatesToServer.getValue() && this.versionMeetsMinimum(5, 6, 5) && (!(Boolean)this.useLocalSessionState.getValue() || readOnlyFlag != this.readOnly)) {
               this.session.execSQL((Query)null, "set session transaction " + (readOnlyFlag ? "read only" : "read write"), -1, (NativePacketPayload)null, false, this.nullStatementResultSetFactory, (ColumnDefinition)null, false);
            }

            this.readOnly = readOnlyFlag;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public Savepoint setSavepoint() throws SQLException {
      try {
         MysqlSavepoint savepoint = new MysqlSavepoint(this.getExceptionInterceptor());
         this.setSavepoint(savepoint);
         return savepoint;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   private void setSavepoint(MysqlSavepoint savepoint) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.checkClosed();
            StringBuilder savePointQuery = new StringBuilder("SAVEPOINT ");
            savePointQuery.append('`');
            savePointQuery.append(savepoint.getSavepointName());
            savePointQuery.append('`');
            Statement stmt = null;

            try {
               stmt = this.getMetadataSafeStatement();
               stmt.executeUpdate(savePointQuery.toString());
            } finally {
               this.closeStatement(stmt);
            }

         }
      } catch (CJException var13) {
         throw SQLExceptionsMapping.translateException(var13, this.getExceptionInterceptor());
      }
   }

   public Savepoint setSavepoint(String name) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            MysqlSavepoint savepoint = new MysqlSavepoint(name, this.getExceptionInterceptor());
            this.setSavepoint(savepoint);
            return savepoint;
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void setTransactionIsolation(int level) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.checkClosed();
            String sql = null;
            boolean shouldSendSet = false;
            if ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.alwaysSendSetIsolation).getValue()) {
               shouldSendSet = true;
            } else if (level != this.isolationLevel) {
               shouldSendSet = true;
            }

            if ((Boolean)this.useLocalSessionState.getValue()) {
               shouldSendSet = this.isolationLevel != level;
            }

            if (shouldSendSet) {
               switch (level) {
                  case 0:
                     throw SQLError.createSQLException(Messages.getString("Connection.24"), this.getExceptionInterceptor());
                  case 1:
                     sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED";
                     break;
                  case 2:
                     sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED";
                     break;
                  case 3:
                  case 5:
                  case 6:
                  case 7:
                  default:
                     throw SQLError.createSQLException(Messages.getString("Connection.25", new Object[]{level}), "S1C00", this.getExceptionInterceptor());
                  case 4:
                     sql = "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ";
                     break;
                  case 8:
                     sql = "SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE";
               }

               this.session.execSQL((Query)null, sql, -1, (NativePacketPayload)null, false, this.nullStatementResultSetFactory, (ColumnDefinition)null, false);
               this.isolationLevel = level;
            }

         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.typeMap = map;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   private void setupServerForTruncationChecks() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            RuntimeProperty<Boolean> jdbcCompliantTruncation = this.propertySet.getProperty(PropertyKey.jdbcCompliantTruncation);
            if ((Boolean)jdbcCompliantTruncation.getValue()) {
               String currentSqlMode = this.session.getServerSession().getServerVariable("sql_mode");
               boolean strictTransTablesIsSet = StringUtils.indexOfIgnoreCase(currentSqlMode, "STRICT_TRANS_TABLES") != -1;
               if (currentSqlMode != null && currentSqlMode.length() != 0 && strictTransTablesIsSet) {
                  if (strictTransTablesIsSet) {
                     jdbcCompliantTruncation.setValue(false);
                  }
               } else {
                  StringBuilder commandBuf = new StringBuilder("SET sql_mode='");
                  if (currentSqlMode != null && currentSqlMode.length() > 0) {
                     commandBuf.append(currentSqlMode);
                     commandBuf.append(",");
                  }

                  commandBuf.append("STRICT_TRANS_TABLES'");
                  this.session.execSQL((Query)null, commandBuf.toString(), -1, (NativePacketPayload)null, false, this.nullStatementResultSetFactory, (ColumnDefinition)null, false);
                  jdbcCompliantTruncation.setValue(false);
               }
            }

         }
      } catch (CJException var9) {
         throw SQLExceptionsMapping.translateException(var9, this.getExceptionInterceptor());
      }
   }

   public void shutdownServer() throws SQLException {
      try {
         try {
            this.session.shutdownServer();
         } catch (CJException var4) {
            SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.UnhandledExceptionDuringShutdown"), "S1000", this.getExceptionInterceptor());
            sqlEx.initCause(var4);
            throw sqlEx;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void unregisterStatement(JdbcStatement stmt) {
      this.openStatements.remove(stmt);
   }

   public boolean versionMeetsMinimum(int major, int minor, int subminor) {
      try {
         this.checkClosed();
         return this.session.versionMeetsMinimum(major, minor, subminor);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public CachedResultSetMetaData getCachedMetaData(String sql) {
      if (this.resultSetMetadataCache != null) {
         synchronized(this.resultSetMetadataCache) {
            return (CachedResultSetMetaData)this.resultSetMetadataCache.get(sql);
         }
      } else {
         return null;
      }
   }

   public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet) throws SQLException {
      try {
         if (cachedMetaData == null) {
            CachedResultSetMetaData cachedMetaData = new CachedResultSetMetaDataImpl();
            resultSet.getColumnDefinition().buildIndexMapping();
            resultSet.initializeWithMetadata();
            if (resultSet instanceof UpdatableResultSet) {
               ((UpdatableResultSet)resultSet).checkUpdatability();
            }

            resultSet.populateCachedMetaData(cachedMetaData);
            this.resultSetMetadataCache.put(sql, cachedMetaData);
         } else {
            resultSet.getColumnDefinition().initializeFrom(cachedMetaData);
            resultSet.initializeWithMetadata();
            if (resultSet instanceof UpdatableResultSet) {
               ((UpdatableResultSet)resultSet).checkUpdatability();
            }
         }

      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public String getStatementComment() {
      return this.session.getProtocol().getQueryComment();
   }

   public void setStatementComment(String comment) {
      this.session.getProtocol().setQueryComment(comment);
   }

   public void transactionBegun() {
      synchronized(this.getConnectionMutex()) {
         if (this.connectionLifecycleInterceptors != null) {
            this.connectionLifecycleInterceptors.stream().forEach(ConnectionLifecycleInterceptor::transactionBegun);
         }

      }
   }

   public void transactionCompleted() {
      synchronized(this.getConnectionMutex()) {
         if (this.connectionLifecycleInterceptors != null) {
            this.connectionLifecycleInterceptors.stream().forEach(ConnectionLifecycleInterceptor::transactionCompleted);
         }

      }
   }

   public boolean storesLowerCaseTableName() {
      return this.session.getServerSession().storesLowerCaseTableNames();
   }

   public ExceptionInterceptor getExceptionInterceptor() {
      return this.exceptionInterceptor;
   }

   public boolean isServerLocal() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            boolean var10000;
            try {
               var10000 = this.session.isServerLocal(this.getSession());
            } catch (CJException var6) {
               SQLException sqlEx = SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
               throw sqlEx;
            }

            return var10000;
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public int getSessionMaxRows() {
      synchronized(this.getConnectionMutex()) {
         return this.session.getSessionMaxRows();
      }
   }

   public void setSessionMaxRows(int max) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.checkClosed();
            if (this.session.getSessionMaxRows() != max) {
               this.session.setSessionMaxRows(max);
               this.session.execSQL((Query)null, "SET SQL_SELECT_LIMIT=" + (this.session.getSessionMaxRows() == -1 ? "DEFAULT" : this.session.getSessionMaxRows()), -1, (NativePacketPayload)null, false, this.nullStatementResultSetFactory, (ColumnDefinition)null, false);
            }

         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void setSchema(String schema) throws SQLException {
      try {
         this.checkClosed();
         if (this.propertySet.getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) {
            this.setDatabase(schema);
         }

      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public String getSchema() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.checkClosed();
            return this.propertySet.getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA ? this.database : null;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void abort(Executor executor) throws SQLException {
      try {
         SecurityManager sec = System.getSecurityManager();
         if (sec != null) {
            sec.checkPermission(ABORT_PERM);
         }

         if (executor == null) {
            throw SQLError.createSQLException(Messages.getString("Connection.26"), "S1009", this.getExceptionInterceptor());
         } else {
            executor.execute(new Runnable() {
               public void run() {
                  try {
                     ConnectionImpl.this.abortInternal();
                  } catch (SQLException var2) {
                     throw new RuntimeException(var2);
                  }
               }
            });
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            SecurityManager sec = System.getSecurityManager();
            if (sec != null) {
               sec.checkPermission(SET_NETWORK_TIMEOUT_PERM);
            }

            if (executor == null) {
               throw SQLError.createSQLException(Messages.getString("Connection.26"), "S1009", this.getExceptionInterceptor());
            } else {
               this.checkClosed();
               executor.execute(new NetworkTimeoutSetter(this, milliseconds));
            }
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public int getNetworkTimeout() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            this.checkClosed();
            return this.session.getSocketTimeout();
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public java.sql.Clob createClob() {
      try {
         return new Clob(this.getExceptionInterceptor());
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public java.sql.Blob createBlob() {
      try {
         return new Blob(this.getExceptionInterceptor());
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public java.sql.NClob createNClob() {
      try {
         return new NClob(this.getExceptionInterceptor());
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public SQLXML createSQLXML() throws SQLException {
      try {
         return new MysqlSQLXML(this.getExceptionInterceptor());
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean isValid(int timeout) throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            if (this.isClosed()) {
               return false;
            } else {
               boolean var10000;
               try {
                  try {
                     this.pingInternal(false, timeout * 1000);
                     return true;
                  } catch (Throwable var8) {
                     try {
                        this.abortInternal();
                     } catch (Throwable var7) {
                     }

                     var10000 = false;
                  }
               } catch (Throwable var9) {
                  return false;
               }

               return var10000;
            }
         }
      } catch (CJException var11) {
         throw SQLExceptionsMapping.translateException(var11, this.getExceptionInterceptor());
      }
   }

   public ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
      try {
         synchronized(this.getConnectionMutex()) {
            if (this.infoProvider == null) {
               String clientInfoProvider = this.propertySet.getStringProperty(PropertyKey.clientInfoProvider).getStringValue();

               try {
                  try {
                     this.infoProvider = (ClientInfoProvider)Util.getInstance(clientInfoProvider, new Class[0], new Object[0], this.getExceptionInterceptor());
                  } catch (CJException var8) {
                     try {
                        this.infoProvider = (ClientInfoProvider)Util.getInstance("com.mysql.cj.jdbc." + clientInfoProvider, new Class[0], new Object[0], this.getExceptionInterceptor());
                     } catch (CJException var7) {
                        throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
                     }
                  }
               } catch (ClassCastException var9) {
                  throw SQLError.createSQLException(Messages.getString("Connection.ClientInfoNotImplemented", new Object[]{clientInfoProvider}), "S1009", this.getExceptionInterceptor());
               }

               this.infoProvider.initialize(this, this.props);
            }

            return this.infoProvider;
         }
      } catch (CJException var11) {
         throw SQLExceptionsMapping.translateException(var11, this.getExceptionInterceptor());
      }
   }

   public void setClientInfo(String name, String value) throws SQLClientInfoException {
      try {
         this.getClientInfoProviderImpl().setClientInfo(this, name, value);
      } catch (SQLClientInfoException var5) {
         throw var5;
      } catch (CJException | SQLException var6) {
         SQLClientInfoException clientInfoEx = new SQLClientInfoException();
         clientInfoEx.initCause(var6);
         throw clientInfoEx;
      }
   }

   public void setClientInfo(Properties properties) throws SQLClientInfoException {
      try {
         this.getClientInfoProviderImpl().setClientInfo(this, properties);
      } catch (SQLClientInfoException var4) {
         throw var4;
      } catch (CJException | SQLException var5) {
         SQLClientInfoException clientInfoEx = new SQLClientInfoException();
         clientInfoEx.initCause(var5);
         throw clientInfoEx;
      }
   }

   public String getClientInfo(String name) throws SQLException {
      try {
         return this.getClientInfoProviderImpl().getClientInfo(this, name);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Properties getClientInfo() throws SQLException {
      try {
         return this.getClientInfoProviderImpl().getClientInfo(this);
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public <T> T unwrap(Class<T> iface) throws SQLException {
      try {
         try {
            return iface.cast(this);
         } catch (ClassCastException var4) {
            throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.getExceptionInterceptor());
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      try {
         this.checkClosed();
         return iface.isInstance(this);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public NativeSession getSession() {
      return this.session;
   }

   public String getHostPortPair() {
      return this.origHostInfo.getHostPortPair();
   }

   public void handleNormalClose() {
      try {
         this.close();
      } catch (SQLException var2) {
         ExceptionFactory.createException((String)var2.getMessage(), (Throwable)var2);
      }

   }

   public void handleReconnect() {
      this.createNewIO(true);
   }

   public void handleCleanup(Throwable whyCleanedUp) {
      this.cleanup(whyCleanedUp);
   }

   public ServerSessionStateController getServerSessionStateController() {
      return this.session.getServerSession().getServerSessionStateController();
   }

   static {
      mapTransIsolationNameToValue = new HashMap(8);
      mapTransIsolationNameToValue.put("READ-UNCOMMITED", 1);
      mapTransIsolationNameToValue.put("READ-UNCOMMITTED", 1);
      mapTransIsolationNameToValue.put("READ-COMMITTED", 2);
      mapTransIsolationNameToValue.put("REPEATABLE-READ", 4);
      mapTransIsolationNameToValue.put("SERIALIZABLE", 8);
      random = new Random();
   }

   private static class NetworkTimeoutSetter implements Runnable {
      private final WeakReference<JdbcConnection> connRef;
      private final int milliseconds;

      public NetworkTimeoutSetter(JdbcConnection conn, int milliseconds) {
         this.connRef = new WeakReference(conn);
         this.milliseconds = milliseconds;
      }

      public void run() {
         JdbcConnection conn = (JdbcConnection)this.connRef.get();
         if (conn != null) {
            synchronized(conn.getConnectionMutex()) {
               ((NativeSession)conn.getSession()).setSocketTimeout(this.milliseconds);
            }
         }

      }
   }

   static class CompoundCacheKey {
      final String componentOne;
      final String componentTwo;
      final int hashCode;

      CompoundCacheKey(String partOne, String partTwo) {
         this.componentOne = partOne;
         this.componentTwo = partTwo;
         int hc = 17;
         hc = 31 * hc + (this.componentOne != null ? this.componentOne.hashCode() : 0);
         hc = 31 * hc + (this.componentTwo != null ? this.componentTwo.hashCode() : 0);
         this.hashCode = hc;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else {
            if (obj != null && CompoundCacheKey.class.isAssignableFrom(obj.getClass())) {
               CompoundCacheKey another = (CompoundCacheKey)obj;
               if (this.componentOne == null) {
                  if (another.componentOne == null) {
                     return this.componentTwo == null ? another.componentTwo == null : this.componentTwo.equals(another.componentTwo);
                  }
               } else if (this.componentOne.equals(another.componentOne)) {
                  return this.componentTwo == null ? another.componentTwo == null : this.componentTwo.equals(another.componentTwo);
               }
            }

            return false;
         }
      }

      public int hashCode() {
         return this.hashCode;
      }
   }
}
