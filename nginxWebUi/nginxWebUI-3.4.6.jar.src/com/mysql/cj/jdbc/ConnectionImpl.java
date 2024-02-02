/*      */ package com.mysql.cj.jdbc;
/*      */ 
/*      */ import com.mysql.cj.CacheAdapter;
/*      */ import com.mysql.cj.CacheAdapterFactory;
/*      */ import com.mysql.cj.LicenseConfiguration;
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.NativeSession;
/*      */ import com.mysql.cj.NoSubInterceptorWrapper;
/*      */ import com.mysql.cj.ParseInfo;
/*      */ import com.mysql.cj.PreparedQuery;
/*      */ import com.mysql.cj.ServerVersion;
/*      */ import com.mysql.cj.Session;
/*      */ import com.mysql.cj.conf.HostInfo;
/*      */ import com.mysql.cj.conf.PropertyDefinitions;
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.conf.PropertySet;
/*      */ import com.mysql.cj.conf.RuntimeProperty;
/*      */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*      */ import com.mysql.cj.exceptions.CJException;
/*      */ import com.mysql.cj.exceptions.ExceptionFactory;
/*      */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*      */ import com.mysql.cj.exceptions.ExceptionInterceptorChain;
/*      */ import com.mysql.cj.exceptions.UnableToConnectException;
/*      */ import com.mysql.cj.interceptors.QueryInterceptor;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*      */ import com.mysql.cj.jdbc.ha.MultiHostMySQLConnection;
/*      */ import com.mysql.cj.jdbc.interceptors.ConnectionLifecycleInterceptor;
/*      */ import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
/*      */ import com.mysql.cj.jdbc.result.CachedResultSetMetaDataImpl;
/*      */ import com.mysql.cj.jdbc.result.ResultSetFactory;
/*      */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*      */ import com.mysql.cj.jdbc.result.UpdatableResultSet;
/*      */ import com.mysql.cj.log.StandardLogger;
/*      */ import com.mysql.cj.protocol.ColumnDefinition;
/*      */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*      */ import com.mysql.cj.protocol.ServerSessionStateController;
/*      */ import com.mysql.cj.protocol.SocksProxySocketFactory;
/*      */ import com.mysql.cj.util.LRUCache;
/*      */ import com.mysql.cj.util.StringUtils;
/*      */ import com.mysql.cj.util.Util;
/*      */ import java.io.Serializable;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.InvocationHandler;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.DriverManager;
/*      */ import java.sql.NClob;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLClientInfoException;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLPermission;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Struct;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Random;
/*      */ import java.util.Stack;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.stream.Collectors;
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
/*      */ 
/*      */ 
/*      */ public class ConnectionImpl
/*      */   implements JdbcConnection, Session.SessionEventListener, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 4009476458425101761L;
/*  111 */   private static final SQLPermission SET_NETWORK_TIMEOUT_PERM = new SQLPermission("setNetworkTimeout");
/*      */   
/*  113 */   private static final SQLPermission ABORT_PERM = new SQLPermission("abort");
/*      */ 
/*      */   
/*      */   public String getHost() {
/*  117 */     return this.session.getHostInfo().getHost();
/*      */   }
/*      */   
/*  120 */   private JdbcConnection parentProxy = null;
/*  121 */   private JdbcConnection topProxy = null;
/*  122 */   private InvocationHandler realProxy = null;
/*      */ 
/*      */   
/*      */   public boolean isProxySet() {
/*  126 */     return (this.topProxy != null);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setProxy(JdbcConnection proxy) {
/*  131 */     if (this.parentProxy == null) {
/*  132 */       this.parentProxy = proxy;
/*      */     }
/*  134 */     this.topProxy = proxy;
/*  135 */     this.realProxy = (this.topProxy instanceof MultiHostMySQLConnection) ? (InvocationHandler)((MultiHostMySQLConnection)proxy).getThisAsProxy() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private JdbcConnection getProxy() {
/*  141 */     return (this.topProxy != null) ? this.topProxy : this;
/*      */   }
/*      */ 
/*      */   
/*      */   public JdbcConnection getMultiHostSafeProxy() {
/*  146 */     return getProxy();
/*      */   }
/*      */ 
/*      */   
/*      */   public JdbcConnection getMultiHostParentProxy() {
/*  151 */     return this.parentProxy;
/*      */   }
/*      */ 
/*      */   
/*      */   public JdbcConnection getActiveMySQLConnection() {
/*  156 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getConnectionMutex() {
/*  161 */     return (this.realProxy != null) ? this.realProxy : getProxy();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class CompoundCacheKey
/*      */   {
/*      */     final String componentOne;
/*      */ 
/*      */     
/*      */     final String componentTwo;
/*      */     
/*      */     final int hashCode;
/*      */ 
/*      */     
/*      */     CompoundCacheKey(String partOne, String partTwo) {
/*  177 */       this.componentOne = partOne;
/*  178 */       this.componentTwo = partTwo;
/*      */       
/*  180 */       int hc = 17;
/*  181 */       hc = 31 * hc + ((this.componentOne != null) ? this.componentOne.hashCode() : 0);
/*  182 */       hc = 31 * hc + ((this.componentTwo != null) ? this.componentTwo.hashCode() : 0);
/*  183 */       this.hashCode = hc;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  188 */       if (this == obj) {
/*  189 */         return true;
/*      */       }
/*  191 */       if (obj != null && CompoundCacheKey.class.isAssignableFrom(obj.getClass())) {
/*  192 */         CompoundCacheKey another = (CompoundCacheKey)obj;
/*  193 */         if ((this.componentOne == null) ? (another.componentOne == null) : this.componentOne.equals(another.componentOne)) {
/*  194 */           return (this.componentTwo == null) ? ((another.componentTwo == null)) : this.componentTwo.equals(another.componentTwo);
/*      */         }
/*      */       } 
/*  197 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  202 */       return this.hashCode;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*  207 */   protected static final String DEFAULT_LOGGER_CLASS = StandardLogger.class.getName();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  213 */   private static Map<String, Integer> mapTransIsolationNameToValue = null;
/*      */   
/*      */   protected static Map<?, ?> roundRobinStatsMap;
/*      */   
/*      */   private List<ConnectionLifecycleInterceptor> connectionLifecycleInterceptors;
/*      */   private static final int DEFAULT_RESULT_SET_TYPE = 1003;
/*      */   private static final int DEFAULT_RESULT_SET_CONCURRENCY = 1007;
/*      */   private static final Random random;
/*      */   private CacheAdapter<String, ParseInfo> cachedPreparedStatementParams;
/*      */   
/*      */   static {
/*  224 */     mapTransIsolationNameToValue = new HashMap<>(8);
/*  225 */     mapTransIsolationNameToValue.put("READ-UNCOMMITED", Integer.valueOf(1));
/*  226 */     mapTransIsolationNameToValue.put("READ-UNCOMMITTED", Integer.valueOf(1));
/*  227 */     mapTransIsolationNameToValue.put("READ-COMMITTED", Integer.valueOf(2));
/*  228 */     mapTransIsolationNameToValue.put("REPEATABLE-READ", Integer.valueOf(4));
/*  229 */     mapTransIsolationNameToValue.put("SERIALIZABLE", Integer.valueOf(8));
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
/*  245 */     random = new Random();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static JdbcConnection getInstance(HostInfo hostInfo) throws SQLException {
/*      */     return new ConnectionImpl(hostInfo);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static synchronized int getNextRoundRobinHostIndex(String url, List<?> hostList) {
/*  257 */     int indexRange = hostList.size();
/*      */     
/*  259 */     int index = random.nextInt(indexRange);
/*      */     
/*  261 */     return index;
/*      */   }
/*      */   
/*      */   private static boolean nullSafeCompare(String s1, String s2) {
/*  265 */     if (s1 == null && s2 == null) {
/*  266 */       return true;
/*      */     }
/*      */     
/*  269 */     if (s1 == null && s2 != null) {
/*  270 */       return false;
/*      */     }
/*      */     
/*  273 */     return (s1 != null && s1.equals(s2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  280 */   private String database = null;
/*      */ 
/*      */   
/*  283 */   private DatabaseMetaData dbmd = null;
/*      */   
/*  285 */   private NativeSession session = null;
/*      */ 
/*      */   
/*      */   private boolean isInGlobalTx = false;
/*      */ 
/*      */   
/*  291 */   private int isolationLevel = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  297 */   private final CopyOnWriteArrayList<JdbcStatement> openStatements = new CopyOnWriteArrayList<>();
/*      */ 
/*      */   
/*      */   private LRUCache<CompoundCacheKey, CallableStatement.CallableStatementParamInfo> parsedCallableStatementCache;
/*      */   
/*  302 */   private String password = null;
/*      */ 
/*      */   
/*  305 */   protected Properties props = null;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean readOnly = false;
/*      */ 
/*      */ 
/*      */   
/*      */   protected LRUCache<String, CachedResultSetMetaData> resultSetMetadataCache;
/*      */ 
/*      */ 
/*      */   
/*      */   private Map<String, Class<?>> typeMap;
/*      */ 
/*      */   
/*  320 */   private String user = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private LRUCache<String, Boolean> serverSideStatementCheckCache;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private LRUCache<CompoundCacheKey, ServerPreparedStatement> serverSideStatementCache;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private HostInfo origHostInfo;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String origHostToConnectTo;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int origPortToConnectTo;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasTriedSourceFlag = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List<QueryInterceptor> queryInterceptors;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JdbcPropertySet propertySet;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> autoReconnectForPools;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> cachePrepStmts;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> autoReconnect;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> useUsageAdvisor;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> reconnectAtTxEnd;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> emulateUnsupportedPstmts;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> ignoreNonTxTables;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> pedantic;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Integer> prepStmtCacheSqlLimit;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> useLocalSessionState;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> useServerPrepStmts;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> processEscapeCodesForPrepStmts;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> useLocalTransactionState;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> disconnectOnExpiredPasswords;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> readOnlyPropagatesToServer;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ResultSetFactory nullStatementResultSetFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int autoIncrementIncrement;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ClientInfoProvider infoProvider;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JdbcPropertySet getPropertySet() {
/*  474 */     return this.propertySet;
/*      */   }
/*      */   
/*      */   public void unSafeQueryInterceptors() throws SQLException {
/*      */     try {
/*  479 */       this
/*  480 */         .queryInterceptors = (List<QueryInterceptor>)this.queryInterceptors.stream().map(u -> ((NoSubInterceptorWrapper)u).getUnderlyingInterceptor()).collect(Collectors.toList());
/*      */       
/*  482 */       if (this.session != null)
/*  483 */         this.session.setQueryInterceptors(this.queryInterceptors);  return;
/*      */     } catch (CJException cJException) {
/*  485 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void initializeSafeQueryInterceptors() throws SQLException {
/*      */     
/*  489 */     try { this
/*      */ 
/*      */         
/*  492 */         .queryInterceptors = (List<QueryInterceptor>)Util.loadClasses(this.propertySet.getStringProperty(PropertyKey.queryInterceptors).getStringValue(), "MysqlIo.BadQueryInterceptor", getExceptionInterceptor()).stream().map(o -> new NoSubInterceptorWrapper(o.init(this, this.props, this.session.getLog()))).collect(Collectors.toList()); return; }
/*  493 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public List<QueryInterceptor> getQueryInterceptorsInstances() {
/*  497 */     return this.queryInterceptors;
/*      */   }
/*      */   
/*      */   private boolean canHandleAsServerPreparedStatement(String sql) throws SQLException {
/*  501 */     if (sql == null || sql.length() == 0) {
/*  502 */       return true;
/*      */     }
/*      */     
/*  505 */     if (!((Boolean)this.useServerPrepStmts.getValue()).booleanValue()) {
/*  506 */       return false;
/*      */     }
/*      */     
/*  509 */     boolean allowMultiQueries = ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowMultiQueries).getValue()).booleanValue();
/*      */     
/*  511 */     if (((Boolean)this.cachePrepStmts.getValue()).booleanValue()) {
/*  512 */       synchronized (this.serverSideStatementCheckCache) {
/*  513 */         Boolean flag = (Boolean)this.serverSideStatementCheckCache.get(sql);
/*      */         
/*  515 */         if (flag != null) {
/*  516 */           return flag.booleanValue();
/*      */         }
/*      */         
/*  519 */         boolean canHandle = StringUtils.canHandleAsServerPreparedStatementNoCache(sql, getServerVersion(), allowMultiQueries, this.session
/*  520 */             .getServerSession().isNoBackslashEscapesSet(), this.session.getServerSession().useAnsiQuotedIdentifiers());
/*      */         
/*  522 */         if (sql.length() < ((Integer)this.prepStmtCacheSqlLimit.getValue()).intValue()) {
/*  523 */           this.serverSideStatementCheckCache.put(sql, canHandle ? Boolean.TRUE : Boolean.FALSE);
/*      */         }
/*      */         
/*  526 */         return canHandle;
/*      */       } 
/*      */     }
/*      */     
/*  530 */     return StringUtils.canHandleAsServerPreparedStatementNoCache(sql, getServerVersion(), allowMultiQueries, this.session
/*  531 */         .getServerSession().isNoBackslashEscapesSet(), this.session.getServerSession().useAnsiQuotedIdentifiers());
/*      */   }
/*      */   
/*      */   public void changeUser(String userName, String newPassword) throws SQLException {
/*      */     
/*  536 */     try { synchronized (getConnectionMutex()) {
/*  537 */         checkClosed();
/*      */         
/*  539 */         if (userName == null || userName.equals("")) {
/*  540 */           userName = "";
/*      */         }
/*      */         
/*  543 */         if (newPassword == null) {
/*  544 */           newPassword = "";
/*      */         }
/*      */         
/*      */         try {
/*  548 */           this.session.changeUser(userName, newPassword, this.database);
/*  549 */         } catch (CJException ex) {
/*      */           
/*  551 */           if ("28000".equals(ex.getSQLState())) {
/*  552 */             cleanup((Throwable)ex);
/*      */           }
/*  554 */           throw ex;
/*      */         } 
/*  556 */         this.user = userName;
/*  557 */         this.password = newPassword;
/*      */         
/*  559 */         this.session.getServerSession().getCharsetSettings().configurePostHandshake(true);
/*      */         
/*  561 */         this.session.setSessionVariables();
/*      */         
/*  563 */         setupServerForTruncationChecks();
/*      */       }  return; }
/*  565 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void checkClosed() {
/*  569 */     this.session.checkClosed();
/*      */   }
/*      */   
/*      */   public void throwConnectionClosedException() throws SQLException {
/*      */     
/*  574 */     try { SQLException ex = SQLError.createSQLException(Messages.getString("Connection.2"), "08003", 
/*  575 */           getExceptionInterceptor());
/*      */       
/*  577 */       if (this.session.getForceClosedReason() != null) {
/*  578 */         ex.initCause(this.session.getForceClosedReason());
/*      */       }
/*      */       
/*  581 */       throw ex; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkTransactionIsolationLevel() {
/*  589 */     String s = this.session.getServerSession().getServerVariable("transaction_isolation");
/*  590 */     if (s == null) {
/*  591 */       s = this.session.getServerSession().getServerVariable("tx_isolation");
/*      */     }
/*      */     
/*  594 */     if (s != null) {
/*  595 */       Integer intTI = mapTransIsolationNameToValue.get(s);
/*      */       
/*  597 */       if (intTI != null) {
/*  598 */         this.isolationLevel = intTI.intValue();
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public void abortInternal() throws SQLException {
/*      */     
/*  605 */     try { this.session.forceClose(); return; }
/*  606 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void cleanup(Throwable whyCleanedUp) {
/*      */     try {
/*  611 */       if (this.session != null) {
/*  612 */         if (isClosed()) {
/*  613 */           this.session.forceClose();
/*      */         } else {
/*  615 */           realClose(false, false, false, whyCleanedUp);
/*      */         } 
/*      */       }
/*  618 */     } catch (SQLException|CJException sQLException) {}
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void clearHasTriedMaster() {
/*  626 */     this.hasTriedSourceFlag = false;
/*      */   }
/*      */   public void clearWarnings() throws SQLException {
/*      */     try {
/*      */       return;
/*      */     } catch (CJException cJException) {
/*  632 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
/*      */     
/*  636 */     try { return clientPrepareStatement(sql, 1003, 1007); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*      */     
/*  641 */     try { PreparedStatement pStmt = clientPrepareStatement(sql);
/*      */       
/*  643 */       ((ClientPreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndex == 1));
/*      */       
/*  645 */       return pStmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     
/*  650 */     try { return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, boolean processEscapeCodesIfNeeded) throws SQLException {
/*      */     
/*  656 */     try { checkClosed(); String nativeSql = (processEscapeCodesIfNeeded && ((Boolean)this.processEscapeCodesForPrepStmts.getValue()).booleanValue()) ? nativeSQL(sql) : sql;
/*      */       
/*  658 */       ClientPreparedStatement pStmt = null;
/*      */       
/*  660 */       if (((Boolean)this.cachePrepStmts.getValue()).booleanValue()) {
/*  661 */         ParseInfo pStmtInfo = (ParseInfo)this.cachedPreparedStatementParams.get(nativeSql);
/*      */         
/*  663 */         if (pStmtInfo == null) {
/*  664 */           pStmt = ClientPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database);
/*      */           
/*  666 */           this.cachedPreparedStatementParams.put(nativeSql, pStmt.getParseInfo());
/*      */         } else {
/*  668 */           pStmt = ClientPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database, pStmtInfo);
/*      */         } 
/*      */       } else {
/*  671 */         pStmt = ClientPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database);
/*      */       } 
/*      */       
/*  674 */       pStmt.setResultSetType(resultSetType);
/*  675 */       pStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */       
/*  677 */       return pStmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*      */     
/*  683 */     try { ClientPreparedStatement pStmt = (ClientPreparedStatement)clientPrepareStatement(sql);
/*      */       
/*  685 */       pStmt.setRetrieveGeneratedKeys((autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0));
/*      */       
/*  687 */       return pStmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*      */     
/*  692 */     try { ClientPreparedStatement pStmt = (ClientPreparedStatement)clientPrepareStatement(sql);
/*      */       
/*  694 */       pStmt.setRetrieveGeneratedKeys((autoGenKeyColNames != null && autoGenKeyColNames.length > 0));
/*      */       
/*  696 */       return pStmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*      */     
/*  702 */     try { return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void close() throws SQLException {
/*      */     
/*  707 */     try { synchronized (getConnectionMutex()) {
/*  708 */         if (this.connectionLifecycleInterceptors != null) {
/*  709 */           for (ConnectionLifecycleInterceptor cli : this.connectionLifecycleInterceptors) {
/*  710 */             cli.close();
/*      */           }
/*      */         }
/*      */         
/*  714 */         realClose(true, true, false, null);
/*      */       }  return; }
/*  716 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void normalClose() {
/*      */     try {
/*  721 */       close();
/*  722 */     } catch (SQLException e) {
/*  723 */       ExceptionFactory.createException(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void closeAllOpenStatements() throws SQLException {
/*  734 */     SQLException postponedException = null;
/*      */     
/*  736 */     for (JdbcStatement stmt : this.openStatements) {
/*      */       try {
/*  738 */         ((StatementImpl)stmt).realClose(false, true);
/*  739 */       } catch (SQLException sqlEx) {
/*  740 */         postponedException = sqlEx;
/*      */       } 
/*      */     } 
/*      */     
/*  744 */     if (postponedException != null) {
/*  745 */       throw postponedException;
/*      */     }
/*      */   }
/*      */   
/*      */   private void closeStatement(Statement stmt) {
/*  750 */     if (stmt != null) {
/*      */       try {
/*  752 */         stmt.close();
/*  753 */       } catch (SQLException sQLException) {}
/*      */ 
/*      */ 
/*      */       
/*  757 */       stmt = null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void commit() throws SQLException {
/*      */     
/*  763 */     try { synchronized (getConnectionMutex()) {
/*  764 */         checkClosed();
/*      */         
/*      */         try {
/*  767 */           if (this.connectionLifecycleInterceptors != null) {
/*      */             
/*  769 */             IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator())
/*      */               {
/*      */                 void forEach(ConnectionLifecycleInterceptor each) throws SQLException
/*      */                 {
/*  773 */                   if (!each.commit()) {
/*  774 */                     this.stopIterating = true;
/*      */                   }
/*      */                 }
/*      */               };
/*      */             
/*  779 */             iter.doForAll();
/*      */             
/*  781 */             if (!iter.fullIteration()) {
/*      */               return;
/*      */             }
/*      */           } 
/*      */           
/*  786 */           if (this.session.getServerSession().isAutoCommit()) {
/*  787 */             throw SQLError.createSQLException(Messages.getString("Connection.3"), getExceptionInterceptor());
/*      */           }
/*  789 */           if (((Boolean)this.useLocalTransactionState.getValue()).booleanValue() && 
/*  790 */             !this.session.getServerSession().inTransactionOnServer()) {
/*      */             return;
/*      */           }
/*      */ 
/*      */           
/*  795 */           this.session.execSQL(null, "commit", -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
/*  796 */         } catch (SQLException sqlException) {
/*  797 */           if ("08S01".equals(sqlException.getSQLState())) {
/*  798 */             throw SQLError.createSQLException(Messages.getString("Connection.4"), "08007", 
/*  799 */                 getExceptionInterceptor());
/*      */           }
/*      */           
/*  802 */           throw sqlException;
/*      */         } finally {
/*  804 */           this.session.setNeedsPing(((Boolean)this.reconnectAtTxEnd.getValue()).booleanValue());
/*      */         } 
/*      */       }  return; }
/*  807 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void createNewIO(boolean isForReconnect) {
/*      */     
/*  812 */     try { synchronized (getConnectionMutex()) {
/*      */ 
/*      */         
/*      */         try {
/*      */ 
/*      */           
/*  818 */           if (!((Boolean)this.autoReconnect.getValue()).booleanValue()) {
/*  819 */             connectOneTryOnly(isForReconnect);
/*      */             
/*      */             return;
/*      */           } 
/*      */           
/*  824 */           connectWithRetries(isForReconnect);
/*  825 */         } catch (SQLException ex) {
/*  826 */           throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, ex.getMessage(), ex);
/*      */         } 
/*      */       }  return; }
/*  829 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } private void connectWithRetries(boolean isForReconnect) throws SQLException {
/*  832 */     double timeout = ((Integer)this.propertySet.getIntegerProperty(PropertyKey.initialTimeout).getValue()).intValue();
/*  833 */     boolean connectionGood = false;
/*      */     
/*  835 */     Exception connectionException = null;
/*      */     
/*  837 */     int attemptCount = 0;
/*  838 */     for (; attemptCount < ((Integer)this.propertySet.getIntegerProperty(PropertyKey.maxReconnects).getValue()).intValue() && !connectionGood; attemptCount++) {
/*      */       try {
/*  840 */         boolean oldAutoCommit; int oldIsolationLevel; boolean oldReadOnly; String oldDb; this.session.forceClose();
/*      */         
/*  842 */         JdbcConnection c = getProxy();
/*  843 */         this.session.connect(this.origHostInfo, this.user, this.password, this.database, getLoginTimeout(), c);
/*  844 */         pingInternal(false, 0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  851 */         synchronized (getConnectionMutex()) {
/*      */           
/*  853 */           oldAutoCommit = getAutoCommit();
/*  854 */           oldIsolationLevel = this.isolationLevel;
/*  855 */           oldReadOnly = isReadOnly(false);
/*  856 */           oldDb = getDatabase();
/*      */           
/*  858 */           this.session.setQueryInterceptors(this.queryInterceptors);
/*      */         } 
/*      */ 
/*      */         
/*  862 */         initializePropsFromServer();
/*      */         
/*  864 */         if (isForReconnect) {
/*      */           
/*  866 */           setAutoCommit(oldAutoCommit);
/*  867 */           setTransactionIsolation(oldIsolationLevel);
/*  868 */           setDatabase(oldDb);
/*  869 */           setReadOnly(oldReadOnly);
/*      */         } 
/*      */         
/*  872 */         connectionGood = true;
/*      */         
/*      */         break;
/*  875 */       } catch (UnableToConnectException rejEx) {
/*  876 */         close();
/*  877 */         this.session.getProtocol().getSocketConnection().forceClose();
/*      */       }
/*  879 */       catch (Exception EEE) {
/*  880 */         connectionException = EEE;
/*  881 */         connectionGood = false;
/*      */       } 
/*      */       
/*  884 */       if (connectionGood) {
/*      */         break;
/*      */       }
/*      */       
/*  888 */       if (attemptCount > 0) {
/*      */         try {
/*  890 */           Thread.sleep((long)timeout * 1000L);
/*  891 */         } catch (InterruptedException interruptedException) {}
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  897 */     if (!connectionGood) {
/*      */       
/*  899 */       SQLException chainedEx = SQLError.createSQLException(
/*  900 */           Messages.getString("Connection.UnableToConnectWithRetries", new Object[] {
/*  901 */               this.propertySet.getIntegerProperty(PropertyKey.maxReconnects).getValue()
/*  902 */             }), "08001", connectionException, getExceptionInterceptor());
/*  903 */       throw chainedEx;
/*      */     } 
/*      */     
/*  906 */     if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue()).booleanValue() && !((Boolean)this.autoReconnect.getValue()).booleanValue()) {
/*  907 */       this.password = null;
/*  908 */       this.user = null;
/*      */     } 
/*      */     
/*  911 */     if (isForReconnect) {
/*      */ 
/*      */ 
/*      */       
/*  915 */       Iterator<JdbcStatement> statementIter = this.openStatements.iterator();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  921 */       Stack<JdbcStatement> serverPreparedStatements = null;
/*      */       
/*  923 */       while (statementIter.hasNext()) {
/*  924 */         JdbcStatement statementObj = statementIter.next();
/*      */         
/*  926 */         if (statementObj instanceof ServerPreparedStatement) {
/*  927 */           if (serverPreparedStatements == null) {
/*  928 */             serverPreparedStatements = new Stack<>();
/*      */           }
/*      */           
/*  931 */           serverPreparedStatements.add(statementObj);
/*      */         } 
/*      */       } 
/*      */       
/*  935 */       if (serverPreparedStatements != null) {
/*  936 */         while (!serverPreparedStatements.isEmpty()) {
/*  937 */           ((ServerPreparedStatement)serverPreparedStatements.pop()).rePrepare();
/*      */         }
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private void connectOneTryOnly(boolean isForReconnect) throws SQLException {
/*  944 */     Exception connectionNotEstablishedBecause = null;
/*      */ 
/*      */     
/*      */     try {
/*  948 */       JdbcConnection c = getProxy();
/*  949 */       this.session.connect(this.origHostInfo, this.user, this.password, this.database, getLoginTimeout(), c);
/*      */ 
/*      */       
/*  952 */       boolean oldAutoCommit = getAutoCommit();
/*  953 */       int oldIsolationLevel = this.isolationLevel;
/*  954 */       boolean oldReadOnly = isReadOnly(false);
/*  955 */       String oldDb = getDatabase();
/*      */       
/*  957 */       this.session.setQueryInterceptors(this.queryInterceptors);
/*      */ 
/*      */       
/*  960 */       initializePropsFromServer();
/*      */       
/*  962 */       if (isForReconnect) {
/*      */         
/*  964 */         setAutoCommit(oldAutoCommit);
/*  965 */         setTransactionIsolation(oldIsolationLevel);
/*  966 */         setDatabase(oldDb);
/*  967 */         setReadOnly(oldReadOnly);
/*      */       } 
/*      */       
/*      */       return;
/*  971 */     } catch (UnableToConnectException rejEx) {
/*  972 */       close();
/*  973 */       this.session.getProtocol().getSocketConnection().forceClose();
/*  974 */       throw rejEx;
/*      */     }
/*  976 */     catch (Exception EEE) {
/*      */       
/*  978 */       if ((EEE instanceof com.mysql.cj.exceptions.PasswordExpiredException || (EEE instanceof SQLException && ((SQLException)EEE)
/*  979 */         .getErrorCode() == 1820)) && 
/*  980 */         !((Boolean)this.disconnectOnExpiredPasswords.getValue()).booleanValue()) {
/*      */         return;
/*      */       }
/*      */       
/*  984 */       if (this.session != null) {
/*  985 */         this.session.forceClose();
/*      */       }
/*      */       
/*  988 */       connectionNotEstablishedBecause = EEE;
/*      */       
/*  990 */       if (EEE instanceof SQLException) {
/*  991 */         throw (SQLException)EEE;
/*      */       }
/*      */       
/*  994 */       if (EEE.getCause() != null && EEE.getCause() instanceof SQLException) {
/*  995 */         throw (SQLException)EEE.getCause();
/*      */       }
/*      */       
/*  998 */       if (EEE instanceof CJException) {
/*  999 */         throw (CJException)EEE;
/*      */       }
/*      */       
/* 1002 */       SQLException chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnect"), "08001", 
/* 1003 */           getExceptionInterceptor());
/* 1004 */       chainedEx.initCause(connectionNotEstablishedBecause);
/*      */       
/* 1006 */       throw chainedEx;
/*      */     } 
/*      */   }
/*      */   
/*      */   private int getLoginTimeout() {
/* 1011 */     int loginTimeoutSecs = DriverManager.getLoginTimeout();
/* 1012 */     if (loginTimeoutSecs <= 0) {
/* 1013 */       return 0;
/*      */     }
/* 1015 */     return loginTimeoutSecs * 1000;
/*      */   }
/*      */   
/*      */   private void createPreparedStatementCaches() throws SQLException {
/* 1019 */     synchronized (getConnectionMutex()) {
/* 1020 */       int cacheSize = ((Integer)this.propertySet.getIntegerProperty(PropertyKey.prepStmtCacheSize).getValue()).intValue();
/* 1021 */       String parseInfoCacheFactory = (String)this.propertySet.getStringProperty(PropertyKey.parseInfoCacheFactory).getValue();
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/* 1026 */         Class<?> factoryClass = Class.forName(parseInfoCacheFactory);
/*      */ 
/*      */         
/* 1029 */         CacheAdapterFactory<String, ParseInfo> cacheFactory = (CacheAdapterFactory<String, ParseInfo>)factoryClass.newInstance();
/*      */         
/* 1031 */         this.cachedPreparedStatementParams = cacheFactory.getInstance(this, this.origHostInfo.getDatabaseUrl(), cacheSize, ((Integer)this.prepStmtCacheSqlLimit
/* 1032 */             .getValue()).intValue());
/*      */       }
/* 1034 */       catch (ClassNotFoundException|InstantiationException|IllegalAccessException e) {
/* 1035 */         SQLException sqlEx = SQLError.createSQLException(
/* 1036 */             Messages.getString("Connection.CantFindCacheFactory", new Object[] { parseInfoCacheFactory, PropertyKey.parseInfoCacheFactory
/* 1037 */               }), getExceptionInterceptor());
/* 1038 */         sqlEx.initCause(e);
/*      */         
/* 1040 */         throw sqlEx;
/* 1041 */       } catch (Exception e) {
/* 1042 */         SQLException sqlEx = SQLError.createSQLException(
/* 1043 */             Messages.getString("Connection.CantLoadCacheFactory", new Object[] { parseInfoCacheFactory, PropertyKey.parseInfoCacheFactory
/* 1044 */               }), getExceptionInterceptor());
/* 1045 */         sqlEx.initCause(e);
/*      */         
/* 1047 */         throw sqlEx;
/*      */       } 
/*      */       
/* 1050 */       if (((Boolean)this.useServerPrepStmts.getValue()).booleanValue()) {
/* 1051 */         this.serverSideStatementCheckCache = new LRUCache(cacheSize);
/*      */         
/* 1053 */         this.serverSideStatementCache = new LRUCache<CompoundCacheKey, ServerPreparedStatement>(cacheSize)
/*      */           {
/*      */             private static final long serialVersionUID = 7692318650375988114L;
/*      */ 
/*      */             
/*      */             protected boolean removeEldestEntry(Map.Entry<ConnectionImpl.CompoundCacheKey, ServerPreparedStatement> eldest) {
/* 1059 */               if (this.maxElements <= 1) {
/* 1060 */                 return false;
/*      */               }
/*      */               
/* 1063 */               boolean removeIt = super.removeEldestEntry(eldest);
/*      */               
/* 1065 */               if (removeIt) {
/* 1066 */                 ServerPreparedStatement ps = eldest.getValue();
/* 1067 */                 ps.isCached = false;
/* 1068 */                 ps.setClosed(false);
/*      */                 try {
/* 1070 */                   ps.realClose(true, true);
/* 1071 */                 } catch (SQLException sQLException) {}
/*      */               } 
/*      */ 
/*      */ 
/*      */               
/* 1076 */               return removeIt;
/*      */             }
/*      */           };
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public Statement createStatement() throws SQLException {
/*      */     
/* 1085 */     try { return createStatement(1003, 1007); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     
/* 1091 */     try { checkClosed(); StatementImpl stmt = new StatementImpl(getMultiHostSafeProxy(), this.database);
/* 1092 */       stmt.setResultSetType(resultSetType);
/* 1093 */       stmt.setResultSetConcurrency(resultSetConcurrency);
/*      */       
/* 1095 */       return stmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*      */     
/* 1100 */     try { if (((Boolean)this.pedantic.getValue()).booleanValue() && 
/* 1101 */         resultSetHoldability != 1) {
/* 1102 */         throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", 
/* 1103 */             getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */       
/* 1107 */       return createStatement(resultSetType, resultSetConcurrency); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public int getActiveStatementCount() {
/* 1112 */     return this.openStatements.size();
/*      */   }
/*      */   
/*      */   public boolean getAutoCommit() throws SQLException {
/*      */     
/* 1117 */     try { synchronized (getConnectionMutex())
/* 1118 */       { return this.session.getServerSession().isAutoCommit(); }  }
/* 1119 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getCatalog() throws SQLException {
/*      */     
/* 1124 */     try { synchronized (getConnectionMutex())
/* 1125 */       { return (this.propertySet.getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? null : this.database; }  }
/* 1126 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public String getCharacterSetMetadata() {
/* 1131 */     synchronized (getConnectionMutex()) {
/* 1132 */       return this.session.getServerSession().getCharsetSettings().getMetadataEncoding();
/*      */     } 
/*      */   }
/*      */   
/*      */   public int getHoldability() throws SQLException {
/*      */     
/* 1138 */     try { return 2; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public long getId() {
/* 1143 */     return this.session.getThreadId();
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
/*      */   public long getIdleFor() {
/* 1156 */     synchronized (getConnectionMutex()) {
/* 1157 */       return this.session.getIdleFor();
/*      */     } 
/*      */   }
/*      */   
/*      */   public DatabaseMetaData getMetaData() throws SQLException {
/*      */     
/* 1163 */     try { return getMetaData(true, true); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } private DatabaseMetaData getMetaData(boolean checkClosed, boolean checkForInfoSchema) throws SQLException {
/*      */     
/* 1167 */     try { if (checkClosed) {
/* 1168 */         checkClosed();
/*      */       }
/*      */       
/* 1171 */       DatabaseMetaData dbmeta = DatabaseMetaData.getInstance(getMultiHostSafeProxy(), this.database, checkForInfoSchema, this.nullStatementResultSetFactory);
/*      */ 
/*      */       
/* 1174 */       if (getSession() != null && getSession().getProtocol() != null) {
/* 1175 */         dbmeta.setMetadataEncoding(getSession().getServerSession().getCharsetSettings().getMetadataEncoding());
/* 1176 */         dbmeta.setMetadataCollationIndex(getSession().getServerSession().getCharsetSettings().getMetadataCollationIndex());
/*      */       } 
/*      */       
/* 1179 */       return dbmeta; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Statement getMetadataSafeStatement() throws SQLException {
/*      */     
/* 1184 */     try { return getMetadataSafeStatement(0); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Statement getMetadataSafeStatement(int maxRows) throws SQLException {
/* 1188 */     Statement stmt = createStatement();
/*      */     
/* 1190 */     stmt.setMaxRows((maxRows == -1) ? 0 : maxRows);
/*      */     
/* 1192 */     stmt.setEscapeProcessing(false);
/*      */     
/* 1194 */     if (stmt.getFetchSize() != 0) {
/* 1195 */       stmt.setFetchSize(0);
/*      */     }
/*      */     
/* 1198 */     return stmt;
/*      */   }
/*      */ 
/*      */   
/*      */   public ServerVersion getServerVersion() {
/* 1203 */     return this.session.getServerSession().getServerVersion();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getTransactionIsolation() throws SQLException {
/*      */     
/* 1209 */     try { synchronized (getConnectionMutex())
/* 1210 */       { if (!((Boolean)this.useLocalSessionState.getValue()).booleanValue()) {
/* 1211 */           String s = this.session.queryServerVariable((
/* 1212 */               versionMeetsMinimum(8, 0, 3) || (versionMeetsMinimum(5, 7, 20) && !versionMeetsMinimum(8, 0, 0))) ? "@@session.transaction_isolation" : "@@session.tx_isolation");
/*      */ 
/*      */           
/* 1215 */           if (s != null) {
/* 1216 */             Integer intTI = mapTransIsolationNameToValue.get(s);
/* 1217 */             if (intTI != null) {
/* 1218 */               this.isolationLevel = intTI.intValue();
/* 1219 */               return this.isolationLevel;
/*      */             } 
/* 1221 */             throw SQLError.createSQLException(Messages.getString("Connection.12", new Object[] { s }), "S1000", 
/* 1222 */                 getExceptionInterceptor());
/*      */           } 
/* 1224 */           throw SQLError.createSQLException(Messages.getString("Connection.13"), "S1000", getExceptionInterceptor());
/*      */         } 
/*      */         
/* 1227 */         return this.isolationLevel; }  }
/* 1228 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Map<String, Class<?>> getTypeMap() throws SQLException {
/*      */     
/* 1233 */     try { synchronized (getConnectionMutex())
/* 1234 */       { if (this.typeMap == null) {
/* 1235 */           this.typeMap = new HashMap<>();
/*      */         }
/*      */         
/* 1238 */         return this.typeMap; }  }
/* 1239 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public String getURL() {
/* 1244 */     return this.origHostInfo.getDatabaseUrl();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getUser() {
/* 1249 */     return this.user;
/*      */   }
/*      */   
/*      */   public SQLWarning getWarnings() throws SQLException {
/*      */     
/* 1254 */     try { return null; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean hasSameProperties(JdbcConnection c) {
/* 1259 */     return this.props.equals(c.getProperties());
/*      */   }
/*      */ 
/*      */   
/*      */   public Properties getProperties() {
/* 1264 */     return this.props;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public boolean hasTriedMaster() {
/* 1270 */     return this.hasTriedSourceFlag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initializePropsFromServer() throws SQLException {
/* 1281 */     String connectionInterceptorClasses = this.propertySet.getStringProperty(PropertyKey.connectionLifecycleInterceptors).getStringValue();
/*      */     
/* 1283 */     this.connectionLifecycleInterceptors = null;
/*      */     
/* 1285 */     if (connectionInterceptorClasses != null) {
/*      */       try {
/* 1287 */         this
/*      */ 
/*      */ 
/*      */           
/* 1291 */           .connectionLifecycleInterceptors = (List<ConnectionLifecycleInterceptor>)Util.loadClasses(this.propertySet.getStringProperty(PropertyKey.connectionLifecycleInterceptors).getStringValue(), "Connection.badLifecycleInterceptor", getExceptionInterceptor()).stream().map(o -> o.init(this, this.props, this.session.getLog())).collect(Collectors.toList());
/* 1292 */       } catch (CJException e) {
/* 1293 */         throw SQLExceptionsMapping.translateException(e, getExceptionInterceptor());
/*      */       } 
/*      */     }
/*      */     
/* 1297 */     this.session.setSessionVariables();
/*      */     
/* 1299 */     this.session.loadServerVariables(getConnectionMutex(), this.dbmd.getDriverVersion());
/*      */     
/* 1301 */     this.autoIncrementIncrement = this.session.getServerSession().getServerVariable("auto_increment_increment", 1);
/*      */     
/*      */     try {
/* 1304 */       LicenseConfiguration.checkLicenseType(this.session.getServerSession().getServerVariables());
/* 1305 */     } catch (CJException e) {
/* 1306 */       throw SQLError.createSQLException(e.getMessage(), "08001", getExceptionInterceptor());
/*      */     } 
/*      */     
/* 1309 */     this.session.getProtocol().initServerSession();
/*      */     
/* 1311 */     checkTransactionIsolationLevel();
/*      */     
/* 1313 */     handleAutoCommitDefaults();
/*      */     
/* 1315 */     ((DatabaseMetaData)this.dbmd).setMetadataEncoding(this.session.getServerSession().getCharsetSettings().getMetadataEncoding());
/* 1316 */     ((DatabaseMetaData)this.dbmd)
/* 1317 */       .setMetadataCollationIndex(this.session.getServerSession().getCharsetSettings().getMetadataCollationIndex());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1323 */     setupServerForTruncationChecks();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void handleAutoCommitDefaults() throws SQLException {
/*      */     try {
/* 1334 */       boolean resetAutoCommitDefault = false;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1339 */       String initConnectValue = this.session.getServerSession().getServerVariable("init_connect");
/* 1340 */       if (initConnectValue != null && initConnectValue.length() > 0) {
/*      */ 
/*      */         
/* 1343 */         String s = this.session.queryServerVariable("@@session.autocommit");
/* 1344 */         if (s != null) {
/* 1345 */           this.session.getServerSession().setAutoCommit(Boolean.parseBoolean(s));
/* 1346 */           if (!this.session.getServerSession().isAutoCommit()) {
/* 1347 */             resetAutoCommitDefault = true;
/*      */           }
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1353 */         resetAutoCommitDefault = true;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1361 */       if (resetAutoCommitDefault)
/*      */         try {
/* 1363 */           setAutoCommit(true);
/* 1364 */         } catch (SQLException ex) {
/* 1365 */           if (ex.getErrorCode() != 1820 || ((Boolean)this.disconnectOnExpiredPasswords.getValue()).booleanValue())
/* 1366 */             throw ex; 
/*      */         }  
/*      */       return;
/*      */     } catch (CJException cJException) {
/* 1370 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public boolean isClosed() {
/*      */     
/* 1374 */     try { return this.session.isClosed(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean isInGlobalTx() {
/* 1379 */     return this.isInGlobalTx;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isSourceConnection() {
/* 1384 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isReadOnly() throws SQLException {
/*      */     
/* 1389 */     try { return isReadOnly(true); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isReadOnly(boolean useSessionStatus) throws SQLException {
/*      */     
/* 1394 */     try { if (useSessionStatus && !this.session.isClosed() && versionMeetsMinimum(5, 6, 5) && !((Boolean)this.useLocalSessionState.getValue()).booleanValue() && ((Boolean)this.readOnlyPropagatesToServer
/* 1395 */         .getValue()).booleanValue()) {
/* 1396 */         String s = this.session.queryServerVariable((
/* 1397 */             versionMeetsMinimum(8, 0, 3) || (versionMeetsMinimum(5, 7, 20) && !versionMeetsMinimum(8, 0, 0))) ? "@@session.transaction_read_only" : "@@session.tx_read_only");
/*      */         
/* 1399 */         if (s != null) {
/* 1400 */           return (Integer.parseInt(s) != 0);
/*      */         }
/*      */       } 
/*      */       
/* 1404 */       return this.readOnly; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean isSameResource(JdbcConnection otherConnection) {
/* 1409 */     synchronized (getConnectionMutex()) {
/* 1410 */       if (otherConnection == null) {
/* 1411 */         return false;
/*      */       }
/*      */       
/* 1414 */       boolean directCompare = true;
/*      */       
/* 1416 */       String otherHost = ((ConnectionImpl)otherConnection).origHostToConnectTo;
/* 1417 */       String otherOrigDatabase = ((ConnectionImpl)otherConnection).origHostInfo.getDatabase();
/* 1418 */       String otherCurrentDb = ((ConnectionImpl)otherConnection).database;
/*      */       
/* 1420 */       if (!nullSafeCompare(otherHost, this.origHostToConnectTo)) {
/* 1421 */         directCompare = false;
/* 1422 */       } else if (otherHost != null && otherHost.indexOf(',') == -1 && otherHost.indexOf(':') == -1) {
/*      */         
/* 1424 */         directCompare = (((ConnectionImpl)otherConnection).origPortToConnectTo == this.origPortToConnectTo);
/*      */       } 
/*      */       
/* 1427 */       if (directCompare && (
/* 1428 */         !nullSafeCompare(otherOrigDatabase, this.origHostInfo.getDatabase()) || !nullSafeCompare(otherCurrentDb, this.database))) {
/* 1429 */         directCompare = false;
/*      */       }
/*      */ 
/*      */       
/* 1433 */       if (directCompare) {
/* 1434 */         return true;
/*      */       }
/*      */ 
/*      */       
/* 1438 */       String otherResourceId = (String)((ConnectionImpl)otherConnection).getPropertySet().getStringProperty(PropertyKey.resourceId).getValue();
/* 1439 */       String myResourceId = (String)this.propertySet.getStringProperty(PropertyKey.resourceId).getValue();
/*      */       
/* 1441 */       if (otherResourceId != null || myResourceId != null) {
/* 1442 */         directCompare = nullSafeCompare(otherResourceId, myResourceId);
/*      */         
/* 1444 */         if (directCompare) {
/* 1445 */           return true;
/*      */         }
/*      */       } 
/*      */       
/* 1449 */       return false;
/*      */     } 
/*      */   }
/*      */   
/* 1453 */   protected ConnectionImpl() { this.autoIncrementIncrement = 0; } public ConnectionImpl(HostInfo hostInfo) throws SQLException { this.autoIncrementIncrement = 0; try { this.origHostInfo = hostInfo; this.origHostToConnectTo = hostInfo.getHost(); this.origPortToConnectTo = hostInfo.getPort(); this.database = hostInfo.getDatabase(); this.user = hostInfo.getUser(); this.password = hostInfo.getPassword(); this.props = hostInfo.exposeAsProperties(); this.propertySet = new JdbcPropertySetImpl(); this.propertySet.initializeProperties(this.props); this.nullStatementResultSetFactory = new ResultSetFactory(this, null); this.session = new NativeSession(hostInfo, this.propertySet); this.session.addListener(this); this.autoReconnectForPools = this.propertySet.getBooleanProperty(PropertyKey.autoReconnectForPools); this.cachePrepStmts = this.propertySet.getBooleanProperty(PropertyKey.cachePrepStmts); this.autoReconnect = this.propertySet.getBooleanProperty(PropertyKey.autoReconnect); this.useUsageAdvisor = this.propertySet.getBooleanProperty(PropertyKey.useUsageAdvisor); this.reconnectAtTxEnd = this.propertySet.getBooleanProperty(PropertyKey.reconnectAtTxEnd); this.emulateUnsupportedPstmts = this.propertySet.getBooleanProperty(PropertyKey.emulateUnsupportedPstmts); this.ignoreNonTxTables = this.propertySet.getBooleanProperty(PropertyKey.ignoreNonTxTables); this.pedantic = this.propertySet.getBooleanProperty(PropertyKey.pedantic); this.prepStmtCacheSqlLimit = this.propertySet.getIntegerProperty(PropertyKey.prepStmtCacheSqlLimit); this.useLocalSessionState = this.propertySet.getBooleanProperty(PropertyKey.useLocalSessionState); this.useServerPrepStmts = this.propertySet.getBooleanProperty(PropertyKey.useServerPrepStmts); this.processEscapeCodesForPrepStmts = this.propertySet.getBooleanProperty(PropertyKey.processEscapeCodesForPrepStmts); this.useLocalTransactionState = this.propertySet.getBooleanProperty(PropertyKey.useLocalTransactionState); this.disconnectOnExpiredPasswords = this.propertySet.getBooleanProperty(PropertyKey.disconnectOnExpiredPasswords); this.readOnlyPropagatesToServer = this.propertySet.getBooleanProperty(PropertyKey.readOnlyPropagatesToServer); String exceptionInterceptorClasses = this.propertySet.getStringProperty(PropertyKey.exceptionInterceptors).getStringValue(); if (exceptionInterceptorClasses != null && !"".equals(exceptionInterceptorClasses)) this.exceptionInterceptor = (ExceptionInterceptor)new ExceptionInterceptorChain(exceptionInterceptorClasses, this.props, this.session.getLog());  if (((Boolean)this.cachePrepStmts.getValue()).booleanValue()) createPreparedStatementCaches();  if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.cacheCallableStmts).getValue()).booleanValue())
/*      */         this.parsedCallableStatementCache = new LRUCache(((Integer)this.propertySet.getIntegerProperty(PropertyKey.callableStmtCacheSize).getValue()).intValue());  if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowMultiQueries).getValue()).booleanValue())
/*      */         this.propertySet.getProperty(PropertyKey.cacheResultSetMetadata).setValue(Boolean.valueOf(false));  if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()).booleanValue())
/*      */         this.resultSetMetadataCache = new LRUCache(((Integer)this.propertySet.getIntegerProperty(PropertyKey.metadataCacheSize).getValue()).intValue());  if (this.propertySet.getStringProperty(PropertyKey.socksProxyHost).getStringValue() != null)
/* 1457 */         this.propertySet.getProperty(PropertyKey.socketFactory).setValue(SocksProxySocketFactory.class.getName());  this.dbmd = getMetaData(false, false); initializeSafeQueryInterceptors(); } catch (CJException e1) { throw SQLExceptionsMapping.translateException(e1, getExceptionInterceptor()); }  try { createNewIO(false); unSafeQueryInterceptors(); AbandonedConnectionCleanupThread.trackConnection(this, getSession().getNetworkResources()); } catch (SQLException ex) { cleanup(ex); throw ex; } catch (Exception ex) { cleanup(ex); throw SQLError.createSQLException(((Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue()).booleanValue() ? Messages.getString("Connection.0") : Messages.getString("Connection.1", new Object[] { this.session.getHostInfo().getHost(), Integer.valueOf(this.session.getHostInfo().getPort()) }), "08S01", ex, getExceptionInterceptor()); }  } public int getAutoIncrementIncrement() { return this.autoIncrementIncrement; }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean lowerCaseTableNames() {
/* 1462 */     return this.session.getServerSession().isLowerCaseTableNames();
/*      */   }
/*      */   
/*      */   public String nativeSQL(String sql) throws SQLException {
/*      */     
/* 1467 */     try { if (sql == null) {
/* 1468 */         return null;
/*      */       }
/*      */       
/* 1471 */       Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, getMultiHostSafeProxy().getSession().getServerSession().getSessionTimeZone(), 
/* 1472 */           getMultiHostSafeProxy().getSession().getServerSession().getCapabilities().serverSupportsFracSecs(), 
/* 1473 */           getMultiHostSafeProxy().getSession().getServerSession().isServerTruncatesFracSecs(), getExceptionInterceptor());
/*      */       
/* 1475 */       if (escapedSqlResult instanceof String) {
/* 1476 */         return (String)escapedSqlResult;
/*      */       }
/*      */       
/* 1479 */       return ((EscapeProcessorResult)escapedSqlResult).escapedSql; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   private CallableStatement parseCallableStatement(String sql) throws SQLException {
/* 1483 */     Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, getMultiHostSafeProxy().getSession().getServerSession().getSessionTimeZone(), 
/* 1484 */         getMultiHostSafeProxy().getSession().getServerSession().getCapabilities().serverSupportsFracSecs(), 
/* 1485 */         getMultiHostSafeProxy().getSession().getServerSession().isServerTruncatesFracSecs(), getExceptionInterceptor());
/*      */     
/* 1487 */     boolean isFunctionCall = false;
/* 1488 */     String parsedSql = null;
/*      */     
/* 1490 */     if (escapedSqlResult instanceof EscapeProcessorResult) {
/* 1491 */       parsedSql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/* 1492 */       isFunctionCall = ((EscapeProcessorResult)escapedSqlResult).callingStoredFunction;
/*      */     } else {
/* 1494 */       parsedSql = (String)escapedSqlResult;
/* 1495 */       isFunctionCall = false;
/*      */     } 
/*      */     
/* 1498 */     return CallableStatement.getInstance(getMultiHostSafeProxy(), parsedSql, this.database, isFunctionCall);
/*      */   }
/*      */   
/*      */   public void ping() throws SQLException {
/*      */     
/* 1503 */     try { pingInternal(true, 0); return; }
/* 1504 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
/*      */     
/* 1508 */     try { this.session.ping(checkForClosedConnection, timeoutMillis); return; }
/* 1509 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public CallableStatement prepareCall(String sql) throws SQLException {
/*      */     
/* 1514 */     try { return prepareCall(sql, 1003, 1007); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     
/* 1519 */     try { CallableStatement cStmt = null;
/*      */       
/* 1521 */       if (!((Boolean)this.propertySet.getBooleanProperty(PropertyKey.cacheCallableStmts).getValue()).booleanValue()) {
/*      */         
/* 1523 */         cStmt = parseCallableStatement(sql);
/*      */       } else {
/* 1525 */         synchronized (this.parsedCallableStatementCache) {
/* 1526 */           CompoundCacheKey key = new CompoundCacheKey(getDatabase(), sql);
/*      */           
/* 1528 */           CallableStatement.CallableStatementParamInfo cachedParamInfo = (CallableStatement.CallableStatementParamInfo)this.parsedCallableStatementCache.get(key);
/*      */           
/* 1530 */           if (cachedParamInfo != null) {
/* 1531 */             cStmt = CallableStatement.getInstance(getMultiHostSafeProxy(), cachedParamInfo);
/*      */           } else {
/* 1533 */             cStmt = parseCallableStatement(sql);
/*      */             
/* 1535 */             synchronized (cStmt) {
/* 1536 */               cachedParamInfo = cStmt.paramInfo;
/*      */             } 
/*      */             
/* 1539 */             this.parsedCallableStatementCache.put(key, cachedParamInfo);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 1544 */       cStmt.setResultSetType(resultSetType);
/* 1545 */       cStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */       
/* 1547 */       return cStmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*      */     
/* 1552 */     try { if (((Boolean)this.pedantic.getValue()).booleanValue() && 
/* 1553 */         resultSetHoldability != 1) {
/* 1554 */         throw SQLError.createSQLException(Messages.getString("Connection.17"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */       
/* 1558 */       CallableStatement cStmt = (CallableStatement)prepareCall(sql, resultSetType, resultSetConcurrency);
/*      */       
/* 1560 */       return cStmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public PreparedStatement prepareStatement(String sql) throws SQLException {
/*      */     
/* 1565 */     try { return prepareStatement(sql, 1003, 1007); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public PreparedStatement prepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*      */     
/* 1570 */     try { PreparedStatement pStmt = prepareStatement(sql);
/*      */       
/* 1572 */       ((ClientPreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndex == 1));
/*      */       
/* 1574 */       return pStmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     
/* 1579 */     try { synchronized (getConnectionMutex())
/* 1580 */       { checkClosed();
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1585 */         ClientPreparedStatement pStmt = null;
/*      */         
/* 1587 */         boolean canServerPrepare = true;
/*      */         
/* 1589 */         String nativeSql = ((Boolean)this.processEscapeCodesForPrepStmts.getValue()).booleanValue() ? nativeSQL(sql) : sql;
/*      */         
/* 1591 */         if (((Boolean)this.useServerPrepStmts.getValue()).booleanValue() && ((Boolean)this.emulateUnsupportedPstmts.getValue()).booleanValue()) {
/* 1592 */           canServerPrepare = canHandleAsServerPreparedStatement(nativeSql);
/*      */         }
/*      */         
/* 1595 */         if (((Boolean)this.useServerPrepStmts.getValue()).booleanValue() && canServerPrepare) {
/* 1596 */           if (((Boolean)this.cachePrepStmts.getValue()).booleanValue()) {
/* 1597 */             synchronized (this.serverSideStatementCache) {
/* 1598 */               pStmt = (ClientPreparedStatement)this.serverSideStatementCache.remove(new CompoundCacheKey(this.database, sql));
/*      */               
/* 1600 */               if (pStmt != null) {
/* 1601 */                 ((ServerPreparedStatement)pStmt).setClosed(false);
/* 1602 */                 pStmt.clearParameters();
/*      */               } 
/*      */               
/* 1605 */               if (pStmt == null) {
/*      */                 try {
/* 1607 */                   pStmt = ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database, resultSetType, resultSetConcurrency);
/*      */                   
/* 1609 */                   if (sql.length() < ((Integer)this.prepStmtCacheSqlLimit.getValue()).intValue()) {
/* 1610 */                     ((ServerPreparedStatement)pStmt).isCacheable = true;
/*      */                   }
/*      */                   
/* 1613 */                   pStmt.setResultSetType(resultSetType);
/* 1614 */                   pStmt.setResultSetConcurrency(resultSetConcurrency);
/* 1615 */                 } catch (SQLException sqlEx) {
/*      */                   
/* 1617 */                   if (((Boolean)this.emulateUnsupportedPstmts.getValue()).booleanValue()) {
/* 1618 */                     pStmt = (ClientPreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */                     
/* 1620 */                     if (sql.length() < ((Integer)this.prepStmtCacheSqlLimit.getValue()).intValue()) {
/* 1621 */                       this.serverSideStatementCheckCache.put(sql, Boolean.FALSE);
/*      */                     }
/*      */                   } else {
/* 1624 */                     throw sqlEx;
/*      */                   } 
/*      */                 } 
/*      */               }
/*      */             } 
/*      */           } else {
/*      */             try {
/* 1631 */               pStmt = ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database, resultSetType, resultSetConcurrency);
/*      */               
/* 1633 */               pStmt.setResultSetType(resultSetType);
/* 1634 */               pStmt.setResultSetConcurrency(resultSetConcurrency);
/* 1635 */             } catch (SQLException sqlEx) {
/*      */               
/* 1637 */               if (((Boolean)this.emulateUnsupportedPstmts.getValue()).booleanValue()) {
/* 1638 */                 pStmt = (ClientPreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */               } else {
/* 1640 */                 throw sqlEx;
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } else {
/* 1645 */           pStmt = (ClientPreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */         } 
/*      */         
/* 1648 */         return pStmt; }  }
/* 1649 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*      */     
/* 1654 */     try { if (((Boolean)this.pedantic.getValue()).booleanValue() && 
/* 1655 */         resultSetHoldability != 1) {
/* 1656 */         throw SQLError.createSQLException(Messages.getString("Connection.17"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */       
/* 1660 */       return prepareStatement(sql, resultSetType, resultSetConcurrency); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*      */     
/* 1665 */     try { PreparedStatement pStmt = prepareStatement(sql);
/*      */       
/* 1667 */       ((ClientPreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0));
/*      */       
/* 1669 */       return pStmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*      */     
/* 1674 */     try { PreparedStatement pStmt = prepareStatement(sql);
/*      */       
/* 1676 */       ((ClientPreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyColNames != null && autoGenKeyColNames.length > 0));
/*      */       
/* 1678 */       return pStmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason) throws SQLException {
/*      */     try {
/* 1683 */       SQLException sqlEx = null;
/*      */       
/* 1685 */       if (isClosed()) {
/*      */         return;
/*      */       }
/*      */       
/* 1689 */       this.session.setForceClosedReason(reason);
/*      */       
/*      */       try {
/* 1692 */         if (!skipLocalTeardown) {
/* 1693 */           if (!getAutoCommit() && issueRollback) {
/*      */             try {
/* 1695 */               rollback();
/* 1696 */             } catch (SQLException ex) {
/* 1697 */               sqlEx = ex;
/*      */             } 
/*      */           }
/*      */           
/* 1701 */           if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.gatherPerfMetrics).getValue()).booleanValue()) {
/* 1702 */             this.session.getProtocol().getMetricsHolder().reportMetrics(this.session.getLog());
/*      */           }
/*      */           
/* 1705 */           if (((Boolean)this.useUsageAdvisor.getValue()).booleanValue()) {
/* 1706 */             if (!calledExplicitly) {
/* 1707 */               this.session.getProfilerEventHandler().processEvent((byte)0, (Session)this.session, null, null, 0L, new Throwable(), 
/* 1708 */                   Messages.getString("Connection.18"));
/*      */             }
/*      */             
/* 1711 */             if (System.currentTimeMillis() - this.session.getConnectionCreationTimeMillis() < 500L) {
/* 1712 */               this.session.getProfilerEventHandler().processEvent((byte)0, (Session)this.session, null, null, 0L, new Throwable(), 
/* 1713 */                   Messages.getString("Connection.19"));
/*      */             }
/*      */           } 
/*      */           
/*      */           try {
/* 1718 */             closeAllOpenStatements();
/* 1719 */           } catch (SQLException ex) {
/* 1720 */             sqlEx = ex;
/*      */           } 
/*      */           
/* 1723 */           this.session.quit();
/*      */         } else {
/* 1725 */           this.session.forceClose();
/*      */         } 
/*      */         
/* 1728 */         if (this.queryInterceptors != null) {
/* 1729 */           for (int i = 0; i < this.queryInterceptors.size(); i++) {
/* 1730 */             ((QueryInterceptor)this.queryInterceptors.get(i)).destroy();
/*      */           }
/*      */         }
/*      */         
/* 1734 */         if (this.exceptionInterceptor != null) {
/* 1735 */           this.exceptionInterceptor.destroy();
/*      */         }
/*      */       } finally {
/*      */         
/* 1739 */         this.openStatements.clear();
/* 1740 */         this.queryInterceptors = null;
/* 1741 */         this.exceptionInterceptor = null;
/* 1742 */         this.nullStatementResultSetFactory = null;
/*      */       } 
/*      */       
/* 1745 */       if (sqlEx != null)
/* 1746 */         throw sqlEx; 
/*      */       return;
/*      */     } catch (CJException cJException) {
/* 1749 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void recachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
/*      */     try {
/* 1753 */       synchronized (getConnectionMutex()) {
/* 1754 */         if (((Boolean)this.cachePrepStmts.getValue()).booleanValue() && pstmt.isPoolable())
/* 1755 */           synchronized (this.serverSideStatementCache) {
/* 1756 */             Object oldServerPrepStmt = this.serverSideStatementCache.put(new CompoundCacheKey(pstmt
/* 1757 */                   .getCurrentDatabase(), ((PreparedQuery)pstmt.getQuery()).getOriginalSql()), pstmt);
/*      */             
/* 1759 */             if (oldServerPrepStmt != null && oldServerPrepStmt != pstmt) {
/* 1760 */               ((ServerPreparedStatement)oldServerPrepStmt).isCached = false;
/* 1761 */               ((ServerPreparedStatement)oldServerPrepStmt).setClosed(false);
/* 1762 */               ((ServerPreparedStatement)oldServerPrepStmt).realClose(true, true);
/*      */             } 
/*      */           }  
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/* 1767 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void decachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
/*      */     try {
/* 1771 */       synchronized (getConnectionMutex()) {
/* 1772 */         if (((Boolean)this.cachePrepStmts.getValue()).booleanValue())
/* 1773 */           synchronized (this.serverSideStatementCache) {
/* 1774 */             this.serverSideStatementCache
/* 1775 */               .remove(new CompoundCacheKey(pstmt.getCurrentDatabase(), ((PreparedQuery)pstmt.getQuery()).getOriginalSql()));
/*      */           }  
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/* 1779 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   public void registerStatement(JdbcStatement stmt) {
/* 1783 */     this.openStatements.addIfAbsent(stmt);
/*      */   }
/*      */   public void releaseSavepoint(Savepoint arg0) throws SQLException {
/*      */     try {
/*      */       return;
/*      */     } catch (CJException cJException) {
/* 1789 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void resetServerState() throws SQLException {
/*      */     try {
/* 1793 */       if (!((Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue()).booleanValue() && this.session != null)
/* 1794 */         changeUser(this.user, this.password);  return;
/*      */     } catch (CJException cJException) {
/* 1796 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void rollback() throws SQLException {
/*      */     
/* 1800 */     try { synchronized (getConnectionMutex()) {
/* 1801 */         checkClosed();
/*      */         
/*      */         try {
/* 1804 */           if (this.connectionLifecycleInterceptors != null) {
/*      */             
/* 1806 */             IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator())
/*      */               {
/*      */                 void forEach(ConnectionLifecycleInterceptor each) throws SQLException
/*      */                 {
/* 1810 */                   if (!each.rollback()) {
/* 1811 */                     this.stopIterating = true;
/*      */                   }
/*      */                 }
/*      */               };
/*      */             
/* 1816 */             iter.doForAll();
/*      */             
/* 1818 */             if (!iter.fullIteration()) {
/*      */               return;
/*      */             }
/*      */           } 
/* 1822 */           if (this.session.getServerSession().isAutoCommit()) {
/* 1823 */             throw SQLError.createSQLException(Messages.getString("Connection.20"), "08003", 
/* 1824 */                 getExceptionInterceptor());
/*      */           }
/*      */           try {
/* 1827 */             rollbackNoChecks();
/* 1828 */           } catch (SQLException sqlEx) {
/*      */             
/* 1830 */             if (((Boolean)this.ignoreNonTxTables.getInitialValue()).booleanValue() && sqlEx.getErrorCode() == 1196) {
/*      */               return;
/*      */             }
/* 1833 */             throw sqlEx;
/*      */           }
/*      */         
/* 1836 */         } catch (SQLException sqlException) {
/* 1837 */           if ("08S01".equals(sqlException.getSQLState())) {
/* 1838 */             throw SQLError.createSQLException(Messages.getString("Connection.21"), "08007", 
/* 1839 */                 getExceptionInterceptor());
/*      */           }
/*      */           
/* 1842 */           throw sqlException;
/*      */         } finally {
/* 1844 */           this.session.setNeedsPing(((Boolean)this.reconnectAtTxEnd.getValue()).booleanValue());
/*      */         } 
/*      */       }  return; }
/* 1847 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public void rollback(final Savepoint savepoint) throws SQLException { 
/* 1852 */     try { synchronized (getConnectionMutex()) {
/* 1853 */         checkClosed();
/*      */         
/*      */         try {
/* 1856 */           if (this.connectionLifecycleInterceptors != null) {
/*      */             
/* 1858 */             IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator())
/*      */               {
/*      */                 void forEach(ConnectionLifecycleInterceptor each) throws SQLException
/*      */                 {
/* 1862 */                   if (!each.rollback(savepoint)) {
/* 1863 */                     this.stopIterating = true;
/*      */                   }
/*      */                 }
/*      */               };
/*      */             
/* 1868 */             iter.doForAll();
/*      */             
/* 1870 */             if (!iter.fullIteration()) {
/*      */               return;
/*      */             }
/*      */           } 
/*      */           
/* 1875 */           StringBuilder rollbackQuery = new StringBuilder("ROLLBACK TO SAVEPOINT ");
/* 1876 */           rollbackQuery.append('`');
/* 1877 */           rollbackQuery.append(savepoint.getSavepointName());
/* 1878 */           rollbackQuery.append('`');
/*      */           
/* 1880 */           Statement stmt = null;
/*      */           
/*      */           try {
/* 1883 */             stmt = getMetadataSafeStatement();
/*      */             
/* 1885 */             stmt.executeUpdate(rollbackQuery.toString());
/* 1886 */           } catch (SQLException sqlEx) {
/* 1887 */             int errno = sqlEx.getErrorCode();
/*      */             
/* 1889 */             if (errno == 1181) {
/* 1890 */               String msg = sqlEx.getMessage();
/*      */               
/* 1892 */               if (msg != null) {
/* 1893 */                 int indexOfError153 = msg.indexOf("153");
/*      */                 
/* 1895 */                 if (indexOfError153 != -1) {
/* 1896 */                   throw SQLError.createSQLException(Messages.getString("Connection.22", new Object[] { savepoint.getSavepointName() }), "S1009", errno, 
/* 1897 */                       getExceptionInterceptor());
/*      */                 }
/*      */               } 
/*      */             } 
/*      */ 
/*      */             
/* 1903 */             if (((Boolean)this.ignoreNonTxTables.getValue()).booleanValue() && sqlEx.getErrorCode() != 1196) {
/* 1904 */               throw sqlEx;
/*      */             }
/*      */             
/* 1907 */             if ("08S01".equals(sqlEx.getSQLState())) {
/* 1908 */               throw SQLError.createSQLException(Messages.getString("Connection.23"), "08007", 
/* 1909 */                   getExceptionInterceptor());
/*      */             }
/*      */             
/* 1912 */             throw sqlEx;
/*      */           } finally {
/* 1914 */             closeStatement(stmt);
/*      */           } 
/*      */         } finally {
/* 1917 */           this.session.setNeedsPing(((Boolean)this.reconnectAtTxEnd.getValue()).booleanValue());
/*      */         } 
/*      */       }  return; }
/* 1920 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */      } private void rollbackNoChecks() throws SQLException {
/*      */     try {
/* 1923 */       synchronized (getConnectionMutex()) {
/* 1924 */         if (((Boolean)this.useLocalTransactionState.getValue()).booleanValue() && 
/* 1925 */           !this.session.getServerSession().inTransactionOnServer()) {
/*      */           return;
/*      */         }
/*      */ 
/*      */         
/* 1930 */         this.session.execSQL(null, "rollback", -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/* 1933 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
/*      */     
/* 1937 */     try { String nativeSql = ((Boolean)this.processEscapeCodesForPrepStmts.getValue()).booleanValue() ? nativeSQL(sql) : sql;
/*      */       
/* 1939 */       return ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, getDatabase(), 1003, 1007); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*      */     
/* 1945 */     try { String nativeSql = ((Boolean)this.processEscapeCodesForPrepStmts.getValue()).booleanValue() ? nativeSQL(sql) : sql;
/*      */       
/* 1947 */       ClientPreparedStatement pStmt = ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, getDatabase(), 1003, 1007);
/*      */ 
/*      */       
/* 1950 */       pStmt.setRetrieveGeneratedKeys((autoGenKeyIndex == 1));
/*      */       
/* 1952 */       return pStmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     
/* 1957 */     try { String nativeSql = ((Boolean)this.processEscapeCodesForPrepStmts.getValue()).booleanValue() ? nativeSQL(sql) : sql;
/*      */       
/* 1959 */       return ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, getDatabase(), resultSetType, resultSetConcurrency); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*      */     
/* 1965 */     try { if (((Boolean)this.pedantic.getValue()).booleanValue() && 
/* 1966 */         resultSetHoldability != 1) {
/* 1967 */         throw SQLError.createSQLException(Messages.getString("Connection.17"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */       
/* 1971 */       return serverPrepareStatement(sql, resultSetType, resultSetConcurrency); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*      */     
/* 1977 */     try { ClientPreparedStatement pStmt = (ClientPreparedStatement)serverPrepareStatement(sql);
/*      */       
/* 1979 */       pStmt.setRetrieveGeneratedKeys((autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0));
/*      */       
/* 1981 */       return pStmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*      */     
/* 1986 */     try { ClientPreparedStatement pStmt = (ClientPreparedStatement)serverPrepareStatement(sql);
/*      */       
/* 1988 */       pStmt.setRetrieveGeneratedKeys((autoGenKeyColNames != null && autoGenKeyColNames.length > 0));
/*      */       
/* 1990 */       return pStmt; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setAutoCommit(final boolean autoCommitFlag) throws SQLException {
/*      */     try {
/* 1995 */       synchronized (getConnectionMutex()) {
/* 1996 */         checkClosed();
/*      */         
/* 1998 */         if (this.connectionLifecycleInterceptors != null) {
/*      */           
/* 2000 */           IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator())
/*      */             {
/*      */               void forEach(ConnectionLifecycleInterceptor each) throws SQLException
/*      */               {
/* 2004 */                 if (!each.setAutoCommit(autoCommitFlag)) {
/* 2005 */                   this.stopIterating = true;
/*      */                 }
/*      */               }
/*      */             };
/*      */           
/* 2010 */           iter.doForAll();
/*      */           
/* 2012 */           if (!iter.fullIteration()) {
/*      */             return;
/*      */           }
/*      */         } 
/*      */         
/* 2017 */         if (((Boolean)this.autoReconnectForPools.getValue()).booleanValue()) {
/* 2018 */           this.autoReconnect.setValue(Boolean.valueOf(true));
/*      */         }
/*      */         
/* 2021 */         boolean isAutocommit = this.session.getServerSession().isAutocommit();
/*      */         try {
/* 2023 */           boolean needsSetOnServer = true;
/* 2024 */           if (((Boolean)this.useLocalSessionState.getValue()).booleanValue() && isAutocommit == autoCommitFlag) {
/* 2025 */             needsSetOnServer = false;
/* 2026 */           } else if (!((Boolean)this.autoReconnect.getValue()).booleanValue()) {
/* 2027 */             needsSetOnServer = getSession().isSetNeededForAutoCommitMode(autoCommitFlag);
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2033 */           this.session.getServerSession().setAutoCommit(autoCommitFlag);
/*      */           
/* 2035 */           if (needsSetOnServer) {
/* 2036 */             this.session.execSQL(null, autoCommitFlag ? "SET autocommit=1" : "SET autocommit=0", -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
/*      */           }
/*      */         }
/* 2039 */         catch (CJCommunicationsException e) {
/* 2040 */           throw e;
/* 2041 */         } catch (CJException e) {
/*      */           
/* 2043 */           this.session.getServerSession().setAutoCommit(isAutocommit);
/*      */           
/* 2045 */           throw SQLError.createSQLException(e.getMessage(), e.getSQLState(), e.getVendorCode(), e.isTransient(), e, getExceptionInterceptor());
/*      */         } finally {
/* 2047 */           if (((Boolean)this.autoReconnectForPools.getValue()).booleanValue())
/* 2048 */             this.autoReconnect.setValue(Boolean.valueOf(false)); 
/*      */         } 
/*      */         return;
/*      */       } 
/*      */     } catch (CJException cJException) {
/* 2053 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setCatalog(String catalog) throws SQLException { try {
/* 2058 */       if (this.propertySet.getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.CATALOG)
/* 2059 */         setDatabase(catalog);  return;
/*      */     } catch (CJException cJException) {
/* 2061 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  } public void setDatabase(final String db) throws SQLException {
/*      */     
/* 2064 */     try { synchronized (getConnectionMutex()) {
/* 2065 */         checkClosed();
/*      */         
/* 2067 */         if (db == null) {
/* 2068 */           throw SQLError.createSQLException("Database can not be null", "S1009", getExceptionInterceptor());
/*      */         }
/*      */         
/* 2071 */         if (this.connectionLifecycleInterceptors != null) {
/*      */           
/* 2073 */           IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator())
/*      */             {
/*      */               void forEach(ConnectionLifecycleInterceptor each) throws SQLException
/*      */               {
/* 2077 */                 if (!each.setDatabase(db)) {
/* 2078 */                   this.stopIterating = true;
/*      */                 }
/*      */               }
/*      */             };
/*      */           
/* 2083 */           iter.doForAll();
/*      */           
/* 2085 */           if (!iter.fullIteration()) {
/*      */             return;
/*      */           }
/*      */         } 
/*      */         
/* 2090 */         if (((Boolean)this.useLocalSessionState.getValue()).booleanValue()) {
/* 2091 */           if (this.session.getServerSession().isLowerCaseTableNames()) {
/* 2092 */             if (this.database.equalsIgnoreCase(db)) {
/*      */               return;
/*      */             }
/*      */           }
/* 2096 */           else if (this.database.equals(db)) {
/*      */             return;
/*      */           } 
/*      */         }
/*      */ 
/*      */         
/* 2102 */         String quotedId = this.session.getIdentifierQuoteString();
/*      */         
/* 2104 */         if (quotedId == null || quotedId.equals(" ")) {
/* 2105 */           quotedId = "";
/*      */         }
/*      */         
/* 2108 */         StringBuilder query = new StringBuilder("USE ");
/* 2109 */         query.append(StringUtils.quoteIdentifier(db, quotedId, ((Boolean)this.pedantic.getValue()).booleanValue()));
/*      */         
/* 2111 */         this.session.execSQL(null, query.toString(), -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
/*      */         
/* 2113 */         this.database = db;
/*      */       }  return; }
/* 2115 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public String getDatabase() throws SQLException {
/*      */     
/* 2119 */     try { synchronized (getConnectionMutex())
/* 2120 */       { return this.database; }  }
/* 2121 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   public void setFailedOver(boolean flag) {}
/*      */   
/*      */   public void setHoldability(int arg0) throws SQLException {
/*      */     try {
/*      */       return;
/*      */     } catch (CJException cJException) {
/* 2132 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   public void setInGlobalTx(boolean flag) {
/* 2136 */     this.isInGlobalTx = flag;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setReadOnly(boolean readOnlyFlag) throws SQLException {
/*      */     
/* 2142 */     try { checkClosed(); setReadOnlyInternal(readOnlyFlag); return; }
/* 2143 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
/*      */     
/* 2147 */     try { synchronized (getConnectionMutex()) {
/*      */         
/* 2149 */         if (((Boolean)this.readOnlyPropagatesToServer.getValue()).booleanValue() && versionMeetsMinimum(5, 6, 5) && (
/* 2150 */           !((Boolean)this.useLocalSessionState.getValue()).booleanValue() || readOnlyFlag != this.readOnly)) {
/* 2151 */           this.session.execSQL(null, "set session transaction " + (readOnlyFlag ? "read only" : "read write"), -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 2156 */         this.readOnly = readOnlyFlag;
/*      */       }  return; }
/* 2158 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public Savepoint setSavepoint() throws SQLException {
/*      */     
/* 2162 */     try { MysqlSavepoint savepoint = new MysqlSavepoint(getExceptionInterceptor());
/*      */       
/* 2164 */       setSavepoint(savepoint);
/*      */       
/* 2166 */       return savepoint; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   private void setSavepoint(MysqlSavepoint savepoint) throws SQLException {
/*      */     
/* 2171 */     try { synchronized (getConnectionMutex()) {
/* 2172 */         checkClosed();
/*      */         
/* 2174 */         StringBuilder savePointQuery = new StringBuilder("SAVEPOINT ");
/* 2175 */         savePointQuery.append('`');
/* 2176 */         savePointQuery.append(savepoint.getSavepointName());
/* 2177 */         savePointQuery.append('`');
/*      */         
/* 2179 */         Statement stmt = null;
/*      */         
/*      */         try {
/* 2182 */           stmt = getMetadataSafeStatement();
/*      */           
/* 2184 */           stmt.executeUpdate(savePointQuery.toString());
/*      */         } finally {
/* 2186 */           closeStatement(stmt);
/*      */         } 
/*      */       }  return; }
/* 2189 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public Savepoint setSavepoint(String name) throws SQLException {
/*      */     
/* 2193 */     try { synchronized (getConnectionMutex())
/* 2194 */       { MysqlSavepoint savepoint = new MysqlSavepoint(name, getExceptionInterceptor());
/*      */         
/* 2196 */         setSavepoint(savepoint);
/*      */         
/* 2198 */         return savepoint; }  }
/* 2199 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setTransactionIsolation(int level) throws SQLException {
/*      */     
/* 2204 */     try { synchronized (getConnectionMutex()) {
/* 2205 */         checkClosed();
/*      */         
/* 2207 */         String sql = null;
/*      */         
/* 2209 */         boolean shouldSendSet = false;
/*      */         
/* 2211 */         if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.alwaysSendSetIsolation).getValue()).booleanValue()) {
/* 2212 */           shouldSendSet = true;
/*      */         }
/* 2214 */         else if (level != this.isolationLevel) {
/* 2215 */           shouldSendSet = true;
/*      */         } 
/*      */ 
/*      */         
/* 2219 */         if (((Boolean)this.useLocalSessionState.getValue()).booleanValue()) {
/* 2220 */           shouldSendSet = (this.isolationLevel != level);
/*      */         }
/*      */         
/* 2223 */         if (shouldSendSet) {
/* 2224 */           switch (level) {
/*      */             case 0:
/* 2226 */               throw SQLError.createSQLException(Messages.getString("Connection.24"), getExceptionInterceptor());
/*      */             
/*      */             case 2:
/* 2229 */               sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED";
/*      */               break;
/*      */ 
/*      */             
/*      */             case 1:
/* 2234 */               sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED";
/*      */               break;
/*      */ 
/*      */             
/*      */             case 4:
/* 2239 */               sql = "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ";
/*      */               break;
/*      */ 
/*      */             
/*      */             case 8:
/* 2244 */               sql = "SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE";
/*      */               break;
/*      */ 
/*      */             
/*      */             default:
/* 2249 */               throw SQLError.createSQLException(Messages.getString("Connection.25", new Object[] { Integer.valueOf(level) }), "S1C00", 
/* 2250 */                   getExceptionInterceptor());
/*      */           } 
/*      */           
/* 2253 */           this.session.execSQL(null, sql, -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
/*      */           
/* 2255 */           this.isolationLevel = level;
/*      */         } 
/*      */       }  return; }
/* 2258 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setTypeMap(Map<String, Class<?>> map) throws SQLException { 
/* 2262 */     try { synchronized (getConnectionMutex()) {
/* 2263 */         this.typeMap = map;
/*      */       }  return; }
/* 2265 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */      } private void setupServerForTruncationChecks() throws SQLException {
/*      */     
/* 2268 */     try { synchronized (getConnectionMutex()) {
/* 2269 */         RuntimeProperty<Boolean> jdbcCompliantTruncation = this.propertySet.getProperty(PropertyKey.jdbcCompliantTruncation);
/* 2270 */         if (((Boolean)jdbcCompliantTruncation.getValue()).booleanValue()) {
/* 2271 */           String currentSqlMode = this.session.getServerSession().getServerVariable("sql_mode");
/*      */           
/* 2273 */           boolean strictTransTablesIsSet = (StringUtils.indexOfIgnoreCase(currentSqlMode, "STRICT_TRANS_TABLES") != -1);
/*      */           
/* 2275 */           if (currentSqlMode == null || currentSqlMode.length() == 0 || !strictTransTablesIsSet) {
/* 2276 */             StringBuilder commandBuf = new StringBuilder("SET sql_mode='");
/*      */             
/* 2278 */             if (currentSqlMode != null && currentSqlMode.length() > 0) {
/* 2279 */               commandBuf.append(currentSqlMode);
/* 2280 */               commandBuf.append(",");
/*      */             } 
/*      */             
/* 2283 */             commandBuf.append("STRICT_TRANS_TABLES'");
/*      */             
/* 2285 */             this.session.execSQL(null, commandBuf.toString(), -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
/*      */             
/* 2287 */             jdbcCompliantTruncation.setValue(Boolean.valueOf(false));
/* 2288 */           } else if (strictTransTablesIsSet) {
/*      */             
/* 2290 */             jdbcCompliantTruncation.setValue(Boolean.valueOf(false));
/*      */           } 
/*      */         } 
/*      */       }  return; }
/* 2294 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void shutdownServer() throws SQLException {
/*      */     
/*      */     try { try {
/* 2299 */         this.session.shutdownServer();
/* 2300 */       } catch (CJException ex) {
/* 2301 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.UnhandledExceptionDuringShutdown"), "S1000", 
/* 2302 */             getExceptionInterceptor());
/*      */         
/* 2304 */         sqlEx.initCause((Throwable)ex);
/*      */         
/* 2306 */         throw sqlEx;
/*      */       }  return; }
/* 2308 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void unregisterStatement(JdbcStatement stmt) {
/* 2312 */     this.openStatements.remove(stmt);
/*      */   }
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor) {
/*      */     
/* 2316 */     try { checkClosed(); return this.session.versionMeetsMinimum(major, minor, subminor); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public CachedResultSetMetaData getCachedMetaData(String sql) {
/* 2321 */     if (this.resultSetMetadataCache != null) {
/* 2322 */       synchronized (this.resultSetMetadataCache) {
/* 2323 */         return (CachedResultSetMetaData)this.resultSetMetadataCache.get(sql);
/*      */       } 
/*      */     }
/*      */     
/* 2327 */     return null;
/*      */   }
/*      */   
/*      */   public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet) throws SQLException {
/*      */     try {
/*      */       CachedResultSetMetaDataImpl cachedResultSetMetaDataImpl;
/* 2333 */       if (cachedMetaData == null) {
/*      */ 
/*      */         
/* 2336 */         cachedResultSetMetaDataImpl = new CachedResultSetMetaDataImpl();
/*      */ 
/*      */         
/* 2339 */         resultSet.getColumnDefinition().buildIndexMapping();
/* 2340 */         resultSet.initializeWithMetadata();
/*      */         
/* 2342 */         if (resultSet instanceof UpdatableResultSet) {
/* 2343 */           ((UpdatableResultSet)resultSet).checkUpdatability();
/*      */         }
/*      */         
/* 2346 */         resultSet.populateCachedMetaData((CachedResultSetMetaData)cachedResultSetMetaDataImpl);
/*      */         
/* 2348 */         this.resultSetMetadataCache.put(sql, cachedResultSetMetaDataImpl);
/*      */       } else {
/* 2350 */         resultSet.getColumnDefinition().initializeFrom((ColumnDefinition)cachedResultSetMetaDataImpl);
/* 2351 */         resultSet.initializeWithMetadata();
/*      */         
/* 2353 */         if (resultSet instanceof UpdatableResultSet)
/* 2354 */           ((UpdatableResultSet)resultSet).checkUpdatability(); 
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/* 2357 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   public String getStatementComment() {
/* 2361 */     return this.session.getProtocol().getQueryComment();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setStatementComment(String comment) {
/* 2366 */     this.session.getProtocol().setQueryComment(comment);
/*      */   }
/*      */ 
/*      */   
/*      */   public void transactionBegun() {
/* 2371 */     synchronized (getConnectionMutex()) {
/* 2372 */       if (this.connectionLifecycleInterceptors != null) {
/* 2373 */         this.connectionLifecycleInterceptors.stream().forEach(ConnectionLifecycleInterceptor::transactionBegun);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void transactionCompleted() {
/* 2380 */     synchronized (getConnectionMutex()) {
/* 2381 */       if (this.connectionLifecycleInterceptors != null) {
/* 2382 */         this.connectionLifecycleInterceptors.stream().forEach(ConnectionLifecycleInterceptor::transactionCompleted);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean storesLowerCaseTableName() {
/* 2389 */     return this.session.getServerSession().storesLowerCaseTableNames();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExceptionInterceptor getExceptionInterceptor() {
/* 2396 */     return this.exceptionInterceptor;
/*      */   }
/*      */   
/*      */   public boolean isServerLocal() throws SQLException {
/*      */     try {
/* 2401 */       synchronized (getConnectionMutex())
/*      */       {
/* 2403 */         return this.session.isServerLocal((Session)getSession());
/*      */       }
/*      */     
/*      */     } catch (CJException cJException) {
/*      */       
/* 2408 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   
/*      */   public int getSessionMaxRows() {
/* 2413 */     synchronized (getConnectionMutex()) {
/* 2414 */       return this.session.getSessionMaxRows();
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setSessionMaxRows(int max) throws SQLException {
/*      */     try {
/* 2420 */       synchronized (getConnectionMutex()) {
/* 2421 */         checkClosed();
/* 2422 */         if (this.session.getSessionMaxRows() != max) {
/* 2423 */           this.session.setSessionMaxRows(max);
/* 2424 */           this.session.execSQL(null, "SET SQL_SELECT_LIMIT=" + ((this.session.getSessionMaxRows() == -1) ? "DEFAULT" : (String)Integer.valueOf(this.session.getSessionMaxRows())), -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
/*      */         } 
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/* 2428 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setSchema(String schema) throws SQLException {
/*      */     try {
/* 2432 */       checkClosed();
/* 2433 */       if (this.propertySet.getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA)
/* 2434 */         setDatabase(schema);  return;
/*      */     } catch (CJException cJException) {
/* 2436 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public String getSchema() throws SQLException {
/*      */     
/* 2440 */     try { synchronized (getConnectionMutex())
/* 2441 */       { checkClosed();
/* 2442 */         return (this.propertySet.getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? this.database : null; }  }
/* 2443 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void abort(Executor executor) throws SQLException {
/*      */     try {
/* 2448 */       SecurityManager sec = System.getSecurityManager();
/*      */       
/* 2450 */       if (sec != null) {
/* 2451 */         sec.checkPermission(ABORT_PERM);
/*      */       }
/*      */       
/* 2454 */       if (executor == null) {
/* 2455 */         throw SQLError.createSQLException(Messages.getString("Connection.26"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/* 2458 */       executor.execute(new Runnable()
/*      */           {
/*      */             public void run()
/*      */             {
/*      */               try {
/* 2463 */                 ConnectionImpl.this.abortInternal();
/* 2464 */               } catch (SQLException e) {
/* 2465 */                 throw new RuntimeException(e);
/*      */               }  } });
/*      */       return;
/*      */     } catch (CJException cJException) {
/* 2469 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
/*      */     
/* 2473 */     try { synchronized (getConnectionMutex()) {
/* 2474 */         SecurityManager sec = System.getSecurityManager();
/*      */         
/* 2476 */         if (sec != null) {
/* 2477 */           sec.checkPermission(SET_NETWORK_TIMEOUT_PERM);
/*      */         }
/*      */         
/* 2480 */         if (executor == null) {
/* 2481 */           throw SQLError.createSQLException(Messages.getString("Connection.26"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */         
/* 2484 */         checkClosed();
/*      */         
/* 2486 */         executor.execute(new NetworkTimeoutSetter(this, milliseconds));
/*      */       }  return; }
/* 2488 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   private static class NetworkTimeoutSetter implements Runnable { private final WeakReference<JdbcConnection> connRef;
/*      */     private final int milliseconds;
/*      */     
/*      */     public NetworkTimeoutSetter(JdbcConnection conn, int milliseconds) {
/* 2495 */       this.connRef = new WeakReference<>(conn);
/* 2496 */       this.milliseconds = milliseconds;
/*      */     }
/*      */ 
/*      */     
/*      */     public void run() {
/* 2501 */       JdbcConnection conn = this.connRef.get();
/* 2502 */       if (conn != null) {
/* 2503 */         synchronized (conn.getConnectionMutex()) {
/* 2504 */           ((NativeSession)conn.getSession()).setSocketTimeout(this.milliseconds);
/*      */         } 
/*      */       }
/*      */     } }
/*      */ 
/*      */   
/*      */   public int getNetworkTimeout() throws SQLException {
/*      */     
/* 2512 */     try { synchronized (getConnectionMutex())
/* 2513 */       { checkClosed();
/* 2514 */         return this.session.getSocketTimeout(); }  }
/* 2515 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Clob createClob() {
/*      */     
/* 2520 */     try { return new Clob(getExceptionInterceptor()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Blob createBlob() {
/*      */     
/* 2525 */     try { return new Blob(getExceptionInterceptor()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public NClob createNClob() {
/*      */     
/* 2530 */     try { return new NClob(getExceptionInterceptor()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public SQLXML createSQLXML() throws SQLException {
/*      */     
/* 2535 */     try { return new MysqlSQLXML(getExceptionInterceptor()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isValid(int timeout) throws SQLException {
/*      */     
/* 2540 */     try { synchronized (getConnectionMutex())
/* 2541 */       { if (isClosed()) {
/* 2542 */           return false;
/*      */         }
/*      */         
/*      */         try {
/*      */           try {
/* 2547 */             pingInternal(false, timeout * 1000);
/* 2548 */           } catch (Throwable t) {
/*      */             try {
/* 2550 */               abortInternal();
/* 2551 */             } catch (Throwable throwable) {}
/*      */ 
/*      */ 
/*      */             
/* 2555 */             return false;
/*      */           }
/*      */         
/* 2558 */         } catch (Throwable t) {
/* 2559 */           return false;
/*      */         } 
/*      */         
/* 2562 */         return true; }  }
/* 2563 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   public ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
/*      */     
/* 2570 */     try { synchronized (getConnectionMutex())
/* 2571 */       { if (this.infoProvider == null) {
/* 2572 */           String clientInfoProvider = this.propertySet.getStringProperty(PropertyKey.clientInfoProvider).getStringValue();
/*      */           try {
/*      */             try {
/* 2575 */               this.infoProvider = (ClientInfoProvider)Util.getInstance(clientInfoProvider, new Class[0], new Object[0], 
/* 2576 */                   getExceptionInterceptor());
/* 2577 */             } catch (CJException ex1) {
/*      */               
/*      */               try {
/* 2580 */                 this.infoProvider = (ClientInfoProvider)Util.getInstance("com.mysql.cj.jdbc." + clientInfoProvider, new Class[0], new Object[0], 
/* 2581 */                     getExceptionInterceptor());
/* 2582 */               } catch (CJException ex2) {
/* 2583 */                 throw SQLExceptionsMapping.translateException(ex1, getExceptionInterceptor());
/*      */               } 
/*      */             } 
/* 2586 */           } catch (ClassCastException ex) {
/* 2587 */             throw SQLError.createSQLException(Messages.getString("Connection.ClientInfoNotImplemented", new Object[] { clientInfoProvider }), "S1009", 
/* 2588 */                 getExceptionInterceptor());
/*      */           } 
/*      */           
/* 2591 */           this.infoProvider.initialize(this, this.props);
/*      */         } 
/*      */         
/* 2594 */         return this.infoProvider; }  }
/* 2595 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public void setClientInfo(String name, String value) throws SQLClientInfoException {
/*      */     try {
/* 2601 */       getClientInfoProviderImpl().setClientInfo(this, name, value);
/* 2602 */     } catch (SQLClientInfoException ciEx) {
/* 2603 */       throw ciEx;
/* 2604 */     } catch (SQLException|CJException sqlEx) {
/* 2605 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 2606 */       clientInfoEx.initCause(sqlEx);
/*      */       
/* 2608 */       throw clientInfoEx;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setClientInfo(Properties properties) throws SQLClientInfoException {
/*      */     try {
/* 2615 */       getClientInfoProviderImpl().setClientInfo(this, properties);
/* 2616 */     } catch (SQLClientInfoException ciEx) {
/* 2617 */       throw ciEx;
/* 2618 */     } catch (SQLException|CJException sqlEx) {
/* 2619 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 2620 */       clientInfoEx.initCause(sqlEx);
/*      */       
/* 2622 */       throw clientInfoEx;
/*      */     } 
/*      */   }
/*      */   
/*      */   public String getClientInfo(String name) throws SQLException {
/*      */     
/* 2628 */     try { return getClientInfoProviderImpl().getClientInfo(this, name); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Properties getClientInfo() throws SQLException {
/*      */     
/* 2633 */     try { return getClientInfoProviderImpl().getClientInfo(this); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
/*      */     
/* 2638 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
/*      */     
/* 2643 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*      */     try {
/*      */       try {
/* 2651 */         return iface.cast(this);
/* 2652 */       } catch (ClassCastException cce) {
/* 2653 */         throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", 
/* 2654 */             getExceptionInterceptor());
/*      */       } 
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*      */     
/* 2663 */     try { checkClosed(); return iface.isInstance(this); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public NativeSession getSession() {
/* 2668 */     return this.session;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getHostPortPair() {
/* 2673 */     return this.origHostInfo.getHostPortPair();
/*      */   }
/*      */ 
/*      */   
/*      */   public void handleNormalClose() {
/*      */     try {
/* 2679 */       close();
/* 2680 */     } catch (SQLException e) {
/* 2681 */       ExceptionFactory.createException(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void handleReconnect() {
/* 2687 */     createNewIO(true);
/*      */   }
/*      */ 
/*      */   
/*      */   public void handleCleanup(Throwable whyCleanedUp) {
/* 2692 */     cleanup(whyCleanedUp);
/*      */   }
/*      */ 
/*      */   
/*      */   public ServerSessionStateController getServerSessionStateController() {
/* 2697 */     return this.session.getServerSession().getServerSessionStateController();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ConnectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */