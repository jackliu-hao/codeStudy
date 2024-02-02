/*      */ package com.mysql.cj.jdbc;
/*      */ 
/*      */ import com.mysql.cj.CancelQueryTask;
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.MysqlType;
/*      */ import com.mysql.cj.NativeSession;
/*      */ import com.mysql.cj.ParseInfo;
/*      */ import com.mysql.cj.PingTarget;
/*      */ import com.mysql.cj.Query;
/*      */ import com.mysql.cj.QueryAttributesBindValue;
/*      */ import com.mysql.cj.QueryAttributesBindings;
/*      */ import com.mysql.cj.QueryReturnType;
/*      */ import com.mysql.cj.Session;
/*      */ import com.mysql.cj.SimpleQuery;
/*      */ import com.mysql.cj.TransactionEventHandler;
/*      */ import com.mysql.cj.conf.HostInfo;
/*      */ import com.mysql.cj.conf.PropertyDefinitions;
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.conf.RuntimeProperty;
/*      */ import com.mysql.cj.exceptions.AssertionFailedException;
/*      */ import com.mysql.cj.exceptions.CJException;
/*      */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*      */ import com.mysql.cj.exceptions.CJTimeoutException;
/*      */ import com.mysql.cj.exceptions.ExceptionFactory;
/*      */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*      */ import com.mysql.cj.exceptions.StatementIsClosedException;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*      */ import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
/*      */ import com.mysql.cj.jdbc.result.ResultSetFactory;
/*      */ import com.mysql.cj.jdbc.result.ResultSetImpl;
/*      */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*      */ import com.mysql.cj.protocol.ColumnDefinition;
/*      */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*      */ import com.mysql.cj.protocol.Resultset;
/*      */ import com.mysql.cj.protocol.ResultsetRows;
/*      */ import com.mysql.cj.protocol.a.NativeMessageBuilder;
/*      */ import com.mysql.cj.protocol.a.result.ByteArrayRow;
/*      */ import com.mysql.cj.protocol.a.result.ResultsetRowsStatic;
/*      */ import com.mysql.cj.result.DefaultColumnDefinition;
/*      */ import com.mysql.cj.result.Field;
/*      */ import com.mysql.cj.result.Row;
/*      */ import com.mysql.cj.util.StringUtils;
/*      */ import com.mysql.cj.util.Util;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.math.BigInteger;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.Connection;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Statement;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class StatementImpl
/*      */   implements JdbcStatement
/*      */ {
/*      */   protected static final String PING_MARKER = "/* ping */";
/*      */   public static final byte USES_VARIABLES_FALSE = 0;
/*      */   public static final byte USES_VARIABLES_TRUE = 1;
/*      */   public static final byte USES_VARIABLES_UNKNOWN = -1;
/*  109 */   protected NativeMessageBuilder commandBuilder = null;
/*      */ 
/*      */   
/*  112 */   protected String charEncoding = null;
/*      */ 
/*      */   
/*  115 */   protected volatile JdbcConnection connection = null;
/*      */ 
/*      */   
/*      */   protected boolean doEscapeProcessing = true;
/*      */ 
/*      */   
/*      */   protected boolean isClosed = false;
/*      */ 
/*      */   
/*  124 */   protected long lastInsertId = -1L;
/*      */ 
/*      */   
/*  127 */   protected int maxFieldSize = ((Integer)PropertyDefinitions.getPropertyDefinition(PropertyKey.maxAllowedPacket).getDefaultValue()).intValue();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  133 */   public int maxRows = -1;
/*      */ 
/*      */   
/*  136 */   protected Set<ResultSetInternalMethods> openResults = new HashSet<>();
/*      */ 
/*      */   
/*      */   protected boolean pedantic = false;
/*      */ 
/*      */   
/*      */   protected boolean profileSQL = false;
/*      */ 
/*      */   
/*  145 */   protected ResultSetInternalMethods results = null;
/*      */   
/*  147 */   protected ResultSetInternalMethods generatedKeysResults = null;
/*      */ 
/*      */   
/*  150 */   protected int resultSetConcurrency = 0;
/*      */ 
/*      */   
/*  153 */   protected long updateCount = -1L;
/*      */ 
/*      */   
/*      */   protected boolean useUsageAdvisor = false;
/*      */ 
/*      */   
/*  159 */   protected SQLWarning warningChain = null;
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean holdResultsOpenOverClose = false;
/*      */ 
/*      */ 
/*      */   
/*  167 */   protected ArrayList<Row> batchedGeneratedKeys = null;
/*      */   
/*      */   protected boolean retrieveGeneratedKeys = false;
/*      */   
/*      */   protected boolean continueBatchOnError = false;
/*      */   
/*  173 */   protected PingTarget pingTarget = null;
/*      */   
/*      */   protected ExceptionInterceptor exceptionInterceptor;
/*      */   
/*      */   protected boolean lastQueryIsOnDupKeyUpdate = false;
/*      */   
/*      */   private boolean isImplicitlyClosingResults = false;
/*      */   
/*      */   protected RuntimeProperty<Boolean> dontTrackOpenResources;
/*      */   
/*      */   protected RuntimeProperty<Boolean> dumpQueriesOnException;
/*      */   
/*      */   protected boolean logSlowQueries = false;
/*      */   
/*      */   protected RuntimeProperty<Boolean> rewriteBatchedStatements;
/*      */   
/*      */   protected RuntimeProperty<Integer> maxAllowedPacket;
/*      */   protected boolean dontCheckOnDuplicateKeyUpdateInSQL;
/*      */   protected ResultSetFactory resultSetFactory;
/*      */   protected Query query;
/*  193 */   protected NativeSession session = null;
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
/*      */   private Resultset.Type originalResultSetType;
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
/*      */   private int originalFetchSize;
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
/*      */   private boolean isPoolable;
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
/*      */   private boolean closeOnCompletion;
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
/*      */   protected void initQuery() {
/*  263 */     this.query = (Query)new SimpleQuery(this.session);
/*      */   }
/*      */   
/*      */   public void addBatch(String sql) throws SQLException {
/*      */     try {
/*  268 */       synchronized (checkClosed().getConnectionMutex()) {
/*  269 */         if (sql != null)
/*  270 */           this.query.addBatch(sql); 
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/*  273 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   public void addBatch(Object batch) {
/*  277 */     this.query.addBatch(batch);
/*      */   }
/*      */ 
/*      */   
/*      */   public List<Object> getBatchedArgs() {
/*  282 */     return this.query.getBatchedArgs();
/*      */   }
/*      */   
/*      */   public void cancel() throws SQLException {
/*      */     try {
/*  287 */       if (!this.query.getStatementExecuting().get()) {
/*      */         return;
/*      */       }
/*      */       
/*  291 */       if (!this.isClosed && this.connection != null) {
/*  292 */         NativeSession newSession = null;
/*      */         
/*      */         try {
/*  295 */           HostInfo hostInfo = this.session.getHostInfo();
/*  296 */           String database = hostInfo.getDatabase();
/*  297 */           String user = hostInfo.getUser();
/*  298 */           String password = hostInfo.getPassword();
/*  299 */           newSession = new NativeSession(this.session.getHostInfo(), this.session.getPropertySet());
/*  300 */           newSession.connect(hostInfo, user, password, database, 30000, new TransactionEventHandler()
/*      */               {
/*      */                 public void transactionCompleted() {}
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*      */                 public void transactionBegun() {}
/*      */               });
/*  309 */           newSession.sendCommand((new NativeMessageBuilder(newSession.getServerSession().supportsQueryAttributes()))
/*  310 */               .buildComQuery(newSession.getSharedSendPacket(), "KILL QUERY " + this.session.getThreadId()), false, 0);
/*  311 */           setCancelStatus(Query.CancelStatus.CANCELED_BY_USER);
/*  312 */         } catch (IOException e) {
/*  313 */           throw SQLExceptionsMapping.translateException(e, this.exceptionInterceptor);
/*      */         } finally {
/*  315 */           if (newSession != null)
/*  316 */             newSession.forceClose(); 
/*      */         } 
/*      */       } 
/*      */       return;
/*      */     } catch (CJException cJException) {
/*  321 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
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
/*      */   protected JdbcConnection checkClosed() {
/*  333 */     JdbcConnection c = this.connection;
/*      */     
/*  335 */     if (c == null) {
/*  336 */       throw (StatementIsClosedException)ExceptionFactory.createException(StatementIsClosedException.class, Messages.getString("Statement.AlreadyClosed"), getExceptionInterceptor());
/*      */     }
/*      */     
/*  339 */     return c;
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
/*      */   protected boolean isResultSetProducingQuery(String sql) {
/*  351 */     QueryReturnType queryReturnType = ParseInfo.getQueryReturnType(sql, this.session.getServerSession().isNoBackslashEscapesSet());
/*  352 */     return (queryReturnType == QueryReturnType.PRODUCES_RESULT_SET || queryReturnType == QueryReturnType.MAY_PRODUCE_RESULT_SET);
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
/*      */   protected boolean isNonResultSetProducingQuery(String sql) {
/*  364 */     QueryReturnType queryReturnType = ParseInfo.getQueryReturnType(sql, this.session.getServerSession().isNoBackslashEscapesSet());
/*  365 */     return (queryReturnType == QueryReturnType.DOES_NOT_PRODUCE_RESULT_SET || queryReturnType == QueryReturnType.MAY_PRODUCE_RESULT_SET);
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
/*      */   protected void checkNullOrEmptyQuery(String sql) throws SQLException {
/*  378 */     if (sql == null) {
/*  379 */       throw SQLError.createSQLException(Messages.getString("Statement.59"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*  382 */     if (sql.length() == 0) {
/*  383 */       throw SQLError.createSQLException(Messages.getString("Statement.61"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */   public void clearBatch() throws SQLException {
/*      */     
/*  389 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  390 */         this.query.clearBatchedArgs();
/*      */       }  return; }
/*  392 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void clearBatchedArgs() {
/*  396 */     this.query.clearBatchedArgs();
/*      */   }
/*      */   
/*      */   public void clearWarnings() throws SQLException {
/*      */     try {
/*  401 */       synchronized (checkClosed().getConnectionMutex()) {
/*  402 */         setClearWarningsCalled(true);
/*  403 */         this.warningChain = null;
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/*  406 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
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
/*      */   
/*      */   public void close() throws SQLException {
/*      */     
/*  424 */     try { realClose(true, true); return; }
/*  425 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void closeAllOpenResults() throws SQLException {
/*  434 */     JdbcConnection locallyScopedConn = this.connection;
/*      */     
/*  436 */     if (locallyScopedConn == null) {
/*      */       return;
/*      */     }
/*      */     
/*  440 */     synchronized (locallyScopedConn.getConnectionMutex()) {
/*  441 */       if (this.openResults != null) {
/*  442 */         for (ResultSetInternalMethods element : this.openResults) {
/*      */           try {
/*  444 */             element.realClose(false);
/*  445 */           } catch (SQLException sqlEx) {
/*  446 */             AssertionFailedException.shouldNotHappen(sqlEx);
/*      */           } 
/*      */         } 
/*      */         
/*  450 */         this.openResults.clear();
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
/*      */   protected void implicitlyCloseAllOpenResults() throws SQLException {
/*  462 */     this.isImplicitlyClosingResults = true;
/*      */     try {
/*  464 */       if (!this.holdResultsOpenOverClose && !((Boolean)this.dontTrackOpenResources.getValue()).booleanValue()) {
/*  465 */         if (this.results != null) {
/*  466 */           this.results.realClose(false);
/*      */         }
/*  468 */         if (this.generatedKeysResults != null) {
/*  469 */           this.generatedKeysResults.realClose(false);
/*      */         }
/*  471 */         closeAllOpenResults();
/*      */       } 
/*      */     } finally {
/*  474 */       this.isImplicitlyClosingResults = false;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void removeOpenResultSet(ResultSetInternalMethods rs) {
/*      */     try {
/*      */       try {
/*  481 */         synchronized (checkClosed().getConnectionMutex()) {
/*  482 */           if (this.openResults != null) {
/*  483 */             this.openResults.remove(rs);
/*      */           }
/*      */           
/*  486 */           boolean hasMoreResults = (rs.getNextResultset() != null);
/*      */ 
/*      */           
/*  489 */           if (this.results == rs && !hasMoreResults) {
/*  490 */             this.results = null;
/*      */           }
/*  492 */           if (this.generatedKeysResults == rs) {
/*  493 */             this.generatedKeysResults = null;
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  499 */           if (!this.isImplicitlyClosingResults && !hasMoreResults) {
/*  500 */             checkAndPerformCloseOnCompletionAction();
/*      */           }
/*      */         } 
/*  503 */       } catch (StatementIsClosedException statementIsClosedException) {}
/*      */       return;
/*      */     } catch (CJException cJException) {
/*  506 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public int getOpenResultSetCount() {
/*      */     
/*      */     try { 
/*  511 */       try { synchronized (checkClosed().getConnectionMutex()) {
/*  512 */           if (this.openResults != null) {
/*  513 */             return this.openResults.size();
/*      */           }
/*      */           
/*  516 */           return 0;
/*      */         }  }
/*  518 */       catch (StatementIsClosedException e)
/*      */       
/*      */       { 
/*  521 */         return 0; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkAndPerformCloseOnCompletionAction() {
/*      */     try {
/*  531 */       synchronized (checkClosed().getConnectionMutex()) {
/*  532 */         if (isCloseOnCompletion() && !((Boolean)this.dontTrackOpenResources.getValue()).booleanValue() && getOpenResultSetCount() == 0 && (this.results == null || 
/*  533 */           !this.results.hasRows() || this.results.isClosed()) && (this.generatedKeysResults == null || 
/*  534 */           !this.generatedKeysResults.hasRows() || this.generatedKeysResults.isClosed())) {
/*  535 */           realClose(false, false);
/*      */         }
/*      */       } 
/*  538 */     } catch (SQLException sQLException) {}
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ResultSetInternalMethods createResultSetUsingServerFetch(String sql) throws SQLException {
/*      */     
/*  550 */     try { synchronized (checkClosed().getConnectionMutex())
/*  551 */       { PreparedStatement pStmt = this.connection.prepareStatement(sql, this.query.getResultType().getIntValue(), this.resultSetConcurrency);
/*      */         
/*  553 */         pStmt.setFetchSize(this.query.getResultFetchSize());
/*      */         
/*  555 */         if (getQueryTimeout() > 0) {
/*  556 */           pStmt.setQueryTimeout(getQueryTimeout());
/*      */         }
/*      */         
/*  559 */         if (this.maxRows > -1) {
/*  560 */           pStmt.setMaxRows(this.maxRows);
/*      */         }
/*      */         
/*  563 */         statementBegins();
/*      */         
/*  565 */         pStmt.execute();
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  570 */         ResultSetInternalMethods rs = ((StatementImpl)pStmt).getResultSetInternal();
/*      */         
/*  572 */         rs.setStatementUsedForFetchingRows((ClientPreparedStatement)pStmt);
/*      */         
/*  574 */         this.results = rs;
/*      */         
/*  576 */         return rs; }  }
/*  577 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean createStreamingResultSet() {
/*  588 */     return (this.query.getResultType() == Resultset.Type.FORWARD_ONLY && this.resultSetConcurrency == 1007 && this.query
/*  589 */       .getResultFetchSize() == Integer.MIN_VALUE);
/*      */   }
/*      */   public void enableStreamingResults() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { this.originalResultSetType = this.query.getResultType(); this.originalFetchSize = this.query.getResultFetchSize(); setFetchSize(-2147483648); setResultSetType(Resultset.Type.FORWARD_ONLY); }  return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  }
/*  592 */   public void disableStreamingResults() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { if (this.query.getResultFetchSize() == Integer.MIN_VALUE && this.query.getResultType() == Resultset.Type.FORWARD_ONLY) { setFetchSize(this.originalFetchSize); setResultSetType(this.originalResultSetType); }  }  return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } protected void setupStreamingTimeout(JdbcConnection con) throws SQLException { int netTimeoutForStreamingResults = ((Integer)this.session.getPropertySet().getIntegerProperty(PropertyKey.netTimeoutForStreamingResults).getValue()).intValue(); if (createStreamingResultSet() && netTimeoutForStreamingResults > 0) executeSimpleNonQuery(con, "SET net_write_timeout=" + netTimeoutForStreamingResults);  } public CancelQueryTask startQueryTimer(Query stmtToCancel, int timeout) { return this.query.startQueryTimer(stmtToCancel, timeout); } public void stopQueryTimer(CancelQueryTask timeoutTask, boolean rethrowCancelReason, boolean checkCancelTimeout) { this.query.stopQueryTimer(timeoutTask, rethrowCancelReason, checkCancelTimeout); } public boolean execute(String sql) throws SQLException { try { return executeInternal(sql, false); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } private boolean executeInternal(String sql, boolean returnGeneratedKeys) throws SQLException { try { JdbcConnection locallyScopedConn = checkClosed(); synchronized (locallyScopedConn.getConnectionMutex()) { checkClosed(); checkNullOrEmptyQuery(sql); resetCancelledState(); implicitlyCloseAllOpenResults(); if (sql.charAt(0) == '/' && sql.startsWith("/* ping */")) { doPingInstead(); return true; }  this.retrieveGeneratedKeys = returnGeneratedKeys; this.lastQueryIsOnDupKeyUpdate = (returnGeneratedKeys && ParseInfo.firstCharOfStatementUc(sql, this.session.getServerSession().isNoBackslashEscapesSet()) == 'I' && containsOnDuplicateKeyInString(sql)); if (!ParseInfo.isReadOnlySafeQuery(sql, this.session.getServerSession().isNoBackslashEscapesSet()) && locallyScopedConn.isReadOnly()) throw SQLError.createSQLException(Messages.getString("Statement.27") + Messages.getString("Statement.28"), "S1009", getExceptionInterceptor());  try { setupStreamingTimeout(locallyScopedConn); if (this.doEscapeProcessing) { Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.session.getServerSession().getSessionTimeZone(), this.session.getServerSession().getCapabilities().serverSupportsFracSecs(), this.session.getServerSession().isServerTruncatesFracSecs(), getExceptionInterceptor()); sql = (escapedSqlResult instanceof String) ? (String)escapedSqlResult : ((EscapeProcessorResult)escapedSqlResult).escapedSql; }  CachedResultSetMetaData cachedMetaData = null; ResultSetInternalMethods rs = null; this.batchedGeneratedKeys = null; if (useServerFetch()) { rs = createResultSetUsingServerFetch(sql); } else { CancelQueryTask timeoutTask = null; String oldDb = null; try { timeoutTask = startQueryTimer(this, getTimeoutInMillis()); if (!locallyScopedConn.getDatabase().equals(getCurrentDatabase())) { oldDb = locallyScopedConn.getDatabase(); locallyScopedConn.setDatabase(getCurrentDatabase()); }  if (((Boolean)locallyScopedConn.getPropertySet().getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()).booleanValue()) cachedMetaData = locallyScopedConn.getCachedMetaData(sql);  locallyScopedConn.setSessionMaxRows(isResultSetProducingQuery(sql) ? this.maxRows : -1); statementBegins(); rs = (ResultSetInternalMethods)((NativeSession)locallyScopedConn.getSession()).execSQL(this, sql, this.maxRows, null, createStreamingResultSet(), getResultSetFactory(), (ColumnDefinition)cachedMetaData, false); if (timeoutTask != null) { stopQueryTimer(timeoutTask, true, true); timeoutTask = null; }  } catch (CJTimeoutException|com.mysql.cj.exceptions.OperationCancelledException e) { throw SQLExceptionsMapping.translateException(e, this.exceptionInterceptor); } finally { stopQueryTimer(timeoutTask, false, false); if (oldDb != null) locallyScopedConn.setDatabase(oldDb);  }  }  if (rs != null) { this.lastInsertId = rs.getUpdateID(); this.results = rs; rs.setFirstCharOfQuery(ParseInfo.firstCharOfStatementUc(sql, this.session.getServerSession().isNoBackslashEscapesSet())); if (rs.hasRows()) if (cachedMetaData != null) { locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results); } else if (((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()).booleanValue()) { locallyScopedConn.initializeResultsMetadataFromCache(sql, (CachedResultSetMetaData)null, this.results); }   }  return (rs != null && rs.hasRows()); } finally { this.query.getStatementExecuting().set(false); }  }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public void statementBegins() { this.query.statementBegins(); } public void resetCancelledState() { try { synchronized (checkClosed().getConnectionMutex()) { this.query.resetCancelledState(); }  return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public boolean execute(String sql, int returnGeneratedKeys) throws SQLException { try { return executeInternal(sql, (returnGeneratedKeys == 1)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public boolean execute(String sql, int[] generatedKeyIndices) throws SQLException { try { return executeInternal(sql, (generatedKeyIndices != null && generatedKeyIndices.length > 0)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public boolean execute(String sql, String[] generatedKeyNames) throws SQLException { try { return executeInternal(sql, (generatedKeyNames != null && generatedKeyNames.length > 0)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int[] executeBatch() throws SQLException { try { return Util.truncateAndConvertToInt(executeBatchInternal()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } protected long[] executeBatchInternal() throws SQLException { JdbcConnection locallyScopedConn = checkClosed(); synchronized (locallyScopedConn.getConnectionMutex()) { if (locallyScopedConn.isReadOnly()) throw SQLError.createSQLException(Messages.getString("Statement.34") + Messages.getString("Statement.35"), "S1009", getExceptionInterceptor());  implicitlyCloseAllOpenResults(); List<Object> batchedArgs = this.query.getBatchedArgs(); if (batchedArgs == null || batchedArgs.size() == 0) return new long[0];  int individualStatementTimeout = getTimeoutInMillis(); setTimeoutInMillis(0); CancelQueryTask timeoutTask = null; try {  } finally { stopQueryTimer(timeoutTask, false, false); resetCancelledState(); setTimeoutInMillis(individualStatementTimeout); clearBatch(); }  }  } protected final boolean hasDeadlockOrTimeoutRolledBackTx(SQLException ex) { int vendorCode = ex.getErrorCode(); switch (vendorCode) { case 1206: case 1213: return true;case 1205: return false; }  return false; } private long[] executeBatchUsingMultiQueries(boolean multiQueriesEnabled, int nbrCommands, int individualStatementTimeout) throws SQLException { try { JdbcConnection locallyScopedConn = checkClosed(); synchronized (locallyScopedConn.getConnectionMutex()) { if (!multiQueriesEnabled) this.session.enableMultiQueries();  Statement batchStmt = null; CancelQueryTask timeoutTask = null; try { long[] updateCounts = new long[nbrCommands]; for (int i = 0; i < nbrCommands; i++) updateCounts[i] = -3L;  int commandIndex = 0; StringBuilder queryBuf = new StringBuilder(); batchStmt = locallyScopedConn.createStatement(); JdbcStatement jdbcBatchedStmt = (JdbcStatement)batchStmt; getQueryAttributesBindings().runThroughAll(a -> jdbcBatchedStmt.setAttribute(a.getName(), a.getValue())); timeoutTask = startQueryTimer((StatementImpl)batchStmt, individualStatementTimeout); int counter = 0; String connectionEncoding = (String)locallyScopedConn.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue(); int numberOfBytesPerChar = StringUtils.startsWithIgnoreCase(connectionEncoding, "utf") ? 3 : (this.session.getServerSession().getCharsetSettings().isMultibyteCharset(connectionEncoding) ? 2 : 1); int escapeAdjust = 1; batchStmt.setEscapeProcessing(this.doEscapeProcessing); if (this.doEscapeProcessing) escapeAdjust = 2;  SQLException sqlEx = null; int argumentSetsInBatchSoFar = 0; for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) { String nextQuery = this.query.getBatchedArgs().get(commandIndex); if (((queryBuf.length() + nextQuery.length()) * numberOfBytesPerChar + 1 + 4) * escapeAdjust + 32 > ((Integer)this.maxAllowedPacket.getValue()).intValue()) { try { batchStmt.execute(queryBuf.toString(), 1); } catch (SQLException ex) { sqlEx = handleExceptionForBatch(commandIndex, argumentSetsInBatchSoFar, updateCounts, ex); }  counter = processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts); queryBuf = new StringBuilder(); argumentSetsInBatchSoFar = 0; }  queryBuf.append(nextQuery); queryBuf.append(";"); argumentSetsInBatchSoFar++; }  if (queryBuf.length() > 0) { try { batchStmt.execute(queryBuf.toString(), 1); } catch (SQLException ex) { sqlEx = handleExceptionForBatch(commandIndex - 1, argumentSetsInBatchSoFar, updateCounts, ex); }  counter = processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts); }  if (timeoutTask != null) { stopQueryTimer(timeoutTask, true, true); timeoutTask = null; }  if (sqlEx != null) throw SQLError.createBatchUpdateException(sqlEx, updateCounts, getExceptionInterceptor());  return (updateCounts != null) ? updateCounts : new long[0]; } finally { stopQueryTimer(timeoutTask, false, false); resetCancelledState(); try { if (batchStmt != null) batchStmt.close();  } finally { if (!multiQueriesEnabled) this.session.disableMultiQueries();  }  }  }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } protected int processMultiCountsAndKeys(StatementImpl batchedStatement, int updateCountCounter, long[] updateCounts) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { updateCounts[updateCountCounter++] = batchedStatement.getLargeUpdateCount(); boolean doGenKeys = (this.batchedGeneratedKeys != null); byte[][] row = (byte[][])null; if (doGenKeys) { long generatedKey = batchedStatement.getLastInsertID(); row = new byte[1][]; row[0] = StringUtils.getBytes(Long.toString(generatedKey)); this.batchedGeneratedKeys.add(new ByteArrayRow(row, getExceptionInterceptor())); }  while (batchedStatement.getMoreResults() || batchedStatement.getLargeUpdateCount() != -1L) { updateCounts[updateCountCounter++] = batchedStatement.getLargeUpdateCount(); if (doGenKeys) { long generatedKey = batchedStatement.getLastInsertID(); row = new byte[1][]; row[0] = StringUtils.getBytes(Long.toString(generatedKey)); this.batchedGeneratedKeys.add(new ByteArrayRow(row, getExceptionInterceptor())); }  }  return updateCountCounter; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } protected SQLException handleExceptionForBatch(int endOfBatchIndex, int numValuesPerBatch, long[] updateCounts, SQLException ex) throws BatchUpdateException, SQLException { for (int j = endOfBatchIndex; j > endOfBatchIndex - numValuesPerBatch; j--) updateCounts[j] = -3L;  if (this.continueBatchOnError && !(ex instanceof com.mysql.cj.jdbc.exceptions.MySQLTimeoutException) && !(ex instanceof com.mysql.cj.jdbc.exceptions.MySQLStatementCancelledException) && !hasDeadlockOrTimeoutRolledBackTx(ex)) return ex;  long[] newUpdateCounts = new long[endOfBatchIndex]; System.arraycopy(updateCounts, 0, newUpdateCounts, 0, endOfBatchIndex); throw SQLError.createBatchUpdateException(ex, newUpdateCounts, getExceptionInterceptor()); } public ResultSet executeQuery(String sql) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { JdbcConnection locallyScopedConn = this.connection; this.retrieveGeneratedKeys = false; checkNullOrEmptyQuery(sql); resetCancelledState(); implicitlyCloseAllOpenResults(); if (sql.charAt(0) == '/' && sql.startsWith("/* ping */")) { doPingInstead(); return (ResultSet)this.results; }  setupStreamingTimeout(locallyScopedConn); if (this.doEscapeProcessing) { Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.session.getServerSession().getSessionTimeZone(), this.session.getServerSession().getCapabilities().serverSupportsFracSecs(), this.session.getServerSession().isServerTruncatesFracSecs(), getExceptionInterceptor()); sql = (escapedSqlResult instanceof String) ? (String)escapedSqlResult : ((EscapeProcessorResult)escapedSqlResult).escapedSql; }  if (!isResultSetProducingQuery(sql)) throw SQLError.createSQLException(Messages.getString("Statement.57"), "S1009", getExceptionInterceptor());  CachedResultSetMetaData cachedMetaData = null; if (useServerFetch()) { this.results = createResultSetUsingServerFetch(sql); return (ResultSet)this.results; }  CancelQueryTask timeoutTask = null; String oldDb = null; try { timeoutTask = startQueryTimer(this, getTimeoutInMillis()); if (!locallyScopedConn.getDatabase().equals(getCurrentDatabase())) { oldDb = locallyScopedConn.getDatabase(); locallyScopedConn.setDatabase(getCurrentDatabase()); }  if (((Boolean)locallyScopedConn.getPropertySet().getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()).booleanValue()) cachedMetaData = locallyScopedConn.getCachedMetaData(sql);  locallyScopedConn.setSessionMaxRows(this.maxRows); statementBegins(); this.results = (ResultSetInternalMethods)((NativeSession)locallyScopedConn.getSession()).execSQL(this, sql, this.maxRows, null, createStreamingResultSet(), getResultSetFactory(), (ColumnDefinition)cachedMetaData, false); if (timeoutTask != null) { stopQueryTimer(timeoutTask, true, true); timeoutTask = null; }  } catch (CJTimeoutException|com.mysql.cj.exceptions.OperationCancelledException e) { throw SQLExceptionsMapping.translateException(e, this.exceptionInterceptor); } finally { this.query.getStatementExecuting().set(false); stopQueryTimer(timeoutTask, false, false); if (oldDb != null) locallyScopedConn.setDatabase(oldDb);  }  this.lastInsertId = this.results.getUpdateID(); if (cachedMetaData != null) { locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results); } else if (((Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()).booleanValue()) { locallyScopedConn.initializeResultsMetadataFromCache(sql, (CachedResultSetMetaData)null, this.results); }  return (ResultSet)this.results; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } protected void doPingInstead() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { if (this.pingTarget != null) { try { this.pingTarget.doPing(); } catch (SQLException e) { throw e; } catch (Exception e) { throw SQLError.createSQLException(e.getMessage(), "08S01", e, getExceptionInterceptor()); }  } else { this.connection.ping(); }  ResultSetInternalMethods fakeSelectOneResultSet = generatePingResultSet(); this.results = fakeSelectOneResultSet; }  return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } protected ResultSetInternalMethods generatePingResultSet() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { String encoding = this.session.getServerSession().getCharsetSettings().getMetadataEncoding(); int collationIndex = this.session.getServerSession().getCharsetSettings().getMetadataCollationIndex(); Field[] fields = { new Field(null, "1", collationIndex, encoding, MysqlType.BIGINT, 1) }; ArrayList<Row> rows = new ArrayList<>(); byte[] colVal = { 49 }; rows.add(new ByteArrayRow(new byte[][] { colVal }, getExceptionInterceptor())); return (ResultSetInternalMethods)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields))); }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public void executeSimpleNonQuery(JdbcConnection c, String nonQuery) throws SQLException { try { synchronized (c.getConnectionMutex()) { ((ResultSetImpl)((NativeSession)c.getSession()).execSQL(this, nonQuery, -1, null, false, getResultSetFactory(), null, false)).close(); }  return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int executeUpdate(String sql) throws SQLException { try { return Util.truncateAndConvertToInt(executeLargeUpdate(sql)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } protected long executeUpdateInternal(String sql, boolean isBatch, boolean returnGeneratedKeys) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { JdbcConnection locallyScopedConn = this.connection; checkNullOrEmptyQuery(sql); resetCancelledState(); char firstStatementChar = ParseInfo.firstCharOfStatementUc(sql, this.session.getServerSession().isNoBackslashEscapesSet()); if (!isNonResultSetProducingQuery(sql)) throw SQLError.createSQLException(Messages.getString("Statement.46"), "01S03", getExceptionInterceptor());  this.retrieveGeneratedKeys = returnGeneratedKeys; this.lastQueryIsOnDupKeyUpdate = (returnGeneratedKeys && firstStatementChar == 'I' && containsOnDuplicateKeyInString(sql)); ResultSetInternalMethods rs = null; if (this.doEscapeProcessing) { Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.session.getServerSession().getSessionTimeZone(), this.session.getServerSession().getCapabilities().serverSupportsFracSecs(), this.session.getServerSession().isServerTruncatesFracSecs(), getExceptionInterceptor()); sql = (escapedSqlResult instanceof String) ? (String)escapedSqlResult : ((EscapeProcessorResult)escapedSqlResult).escapedSql; }  if (locallyScopedConn.isReadOnly(false)) throw SQLError.createSQLException(Messages.getString("Statement.42") + Messages.getString("Statement.43"), "S1009", getExceptionInterceptor());  implicitlyCloseAllOpenResults(); CancelQueryTask timeoutTask = null; String oldDb = null; try { timeoutTask = startQueryTimer(this, getTimeoutInMillis()); if (!locallyScopedConn.getDatabase().equals(getCurrentDatabase())) { oldDb = locallyScopedConn.getDatabase(); locallyScopedConn.setDatabase(getCurrentDatabase()); }  locallyScopedConn.setSessionMaxRows(-1); statementBegins(); rs = (ResultSetInternalMethods)((NativeSession)locallyScopedConn.getSession()).execSQL(this, sql, -1, null, false, getResultSetFactory(), null, isBatch); if (timeoutTask != null) { stopQueryTimer(timeoutTask, true, true); timeoutTask = null; }  } catch (CJTimeoutException|com.mysql.cj.exceptions.OperationCancelledException e) { throw SQLExceptionsMapping.translateException(e, this.exceptionInterceptor); } finally { stopQueryTimer(timeoutTask, false, false); if (oldDb != null) locallyScopedConn.setDatabase(oldDb);  if (!isBatch) this.query.getStatementExecuting().set(false);  }  this.results = rs; rs.setFirstCharOfQuery(firstStatementChar); this.updateCount = rs.getUpdateCount(); this.lastInsertId = rs.getUpdateID(); return this.updateCount; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException { try { return Util.truncateAndConvertToInt(executeLargeUpdate(sql, autoGeneratedKeys)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int executeUpdate(String sql, int[] columnIndexes) throws SQLException { try { return Util.truncateAndConvertToInt(executeLargeUpdate(sql, columnIndexes)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int executeUpdate(String sql, String[] columnNames) throws SQLException { try { return Util.truncateAndConvertToInt(executeLargeUpdate(sql, columnNames)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public Connection getConnection() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { return this.connection; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int getFetchDirection() throws SQLException { try { return 1000; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int getFetchSize() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { return this.query.getResultFetchSize(); }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public ResultSet getGeneratedKeys() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { if (!this.retrieveGeneratedKeys) throw SQLError.createSQLException(Messages.getString("Statement.GeneratedKeysNotRequested"), "S1009", getExceptionInterceptor());  if (this.batchedGeneratedKeys == null) { if (this.lastQueryIsOnDupKeyUpdate) return (ResultSet)(this.generatedKeysResults = getGeneratedKeysInternal(1L));  return (ResultSet)(this.generatedKeysResults = getGeneratedKeysInternal()); }  String encoding = this.session.getServerSession().getCharsetSettings().getMetadataEncoding(); int collationIndex = this.session.getServerSession().getCharsetSettings().getMetadataCollationIndex(); Field[] fields = new Field[1]; fields[0] = new Field("", "GENERATED_KEY", collationIndex, encoding, MysqlType.BIGINT_UNSIGNED, 20); this.generatedKeysResults = (ResultSetInternalMethods)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(this.batchedGeneratedKeys, (ColumnDefinition)new DefaultColumnDefinition(fields))); return (ResultSet)this.generatedKeysResults; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } protected ResultSetInternalMethods getGeneratedKeysInternal() throws SQLException { long numKeys = getLargeUpdateCount(); return getGeneratedKeysInternal(numKeys); } protected ResultSetInternalMethods getGeneratedKeysInternal(long numKeys) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { String encoding = this.session.getServerSession().getCharsetSettings().getMetadataEncoding(); int collationIndex = this.session.getServerSession().getCharsetSettings().getMetadataCollationIndex(); Field[] fields = new Field[1]; fields[0] = new Field("", "GENERATED_KEY", collationIndex, encoding, MysqlType.BIGINT_UNSIGNED, 20); ArrayList<Row> rowSet = new ArrayList<>(); long beginAt = getLastInsertID(); if (this.results != null) { String serverInfo = this.results.getServerInfo(); if (numKeys > 0L && this.results.getFirstCharOfQuery() == 'R' && serverInfo != null && serverInfo.length() > 0) numKeys = getRecordCountFromInfo(serverInfo);  if (beginAt != 0L && numKeys > 0L) for (int i = 0; i < numKeys; i++) { byte[][] row = new byte[1][]; if (beginAt > 0L) { row[0] = StringUtils.getBytes(Long.toString(beginAt)); } else { byte[] asBytes = new byte[8]; asBytes[7] = (byte)(int)(beginAt & 0xFFL); asBytes[6] = (byte)(int)(beginAt >>> 8L); asBytes[5] = (byte)(int)(beginAt >>> 16L); asBytes[4] = (byte)(int)(beginAt >>> 24L); asBytes[3] = (byte)(int)(beginAt >>> 32L); asBytes[2] = (byte)(int)(beginAt >>> 40L); asBytes[1] = (byte)(int)(beginAt >>> 48L); asBytes[0] = (byte)(int)(beginAt >>> 56L); BigInteger val = new BigInteger(1, asBytes); row[0] = val.toString().getBytes(); }  rowSet.add(new ByteArrayRow(row, getExceptionInterceptor())); beginAt += this.connection.getAutoIncrementIncrement(); }   }  ResultSetImpl gkRs = this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rowSet, (ColumnDefinition)new DefaultColumnDefinition(fields))); return (ResultSetInternalMethods)gkRs; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public long getLastInsertID() { try { synchronized (checkClosed().getConnectionMutex()) { return this.lastInsertId; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public long getLongUpdateCount() { try { synchronized (checkClosed().getConnectionMutex()) { if (this.results == null) return -1L;  if (this.results.hasRows()) return -1L;  return this.updateCount; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int getMaxFieldSize() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { return this.maxFieldSize; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int getMaxRows() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { if (this.maxRows <= 0) return 0;  return this.maxRows; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public StatementImpl(JdbcConnection c, String db) throws SQLException { this.originalResultSetType = Resultset.Type.FORWARD_ONLY;
/*  593 */     this.originalFetchSize = 0;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2003 */     this.isPoolable = false;
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
/* 2061 */     this.closeOnCompletion = false; if (c == null || c.isClosed()) throw SQLError.createSQLException(Messages.getString("Statement.0"), "08003", null);  this.connection = c; this.session = (NativeSession)c.getSession(); this.exceptionInterceptor = c.getExceptionInterceptor(); this.commandBuilder = new NativeMessageBuilder(this.session.getServerSession().supportsQueryAttributes()); try { initQuery(); } catch (CJException e) { throw SQLExceptionsMapping.translateException(e, getExceptionInterceptor()); }  this.query.setCurrentDatabase(db); JdbcPropertySet pset = c.getPropertySet(); this.dontTrackOpenResources = pset.getBooleanProperty(PropertyKey.dontTrackOpenResources); this.dumpQueriesOnException = pset.getBooleanProperty(PropertyKey.dumpQueriesOnException); this.continueBatchOnError = ((Boolean)pset.getBooleanProperty(PropertyKey.continueBatchOnError).getValue()).booleanValue(); this.pedantic = ((Boolean)pset.getBooleanProperty(PropertyKey.pedantic).getValue()).booleanValue(); this.rewriteBatchedStatements = pset.getBooleanProperty(PropertyKey.rewriteBatchedStatements); this.charEncoding = (String)pset.getStringProperty(PropertyKey.characterEncoding).getValue(); this.profileSQL = ((Boolean)pset.getBooleanProperty(PropertyKey.profileSQL).getValue()).booleanValue(); this.useUsageAdvisor = ((Boolean)pset.getBooleanProperty(PropertyKey.useUsageAdvisor).getValue()).booleanValue(); this.logSlowQueries = ((Boolean)pset.getBooleanProperty(PropertyKey.logSlowQueries).getValue()).booleanValue(); this.maxAllowedPacket = pset.getIntegerProperty(PropertyKey.maxAllowedPacket); this.dontCheckOnDuplicateKeyUpdateInSQL = ((Boolean)pset.getBooleanProperty(PropertyKey.dontCheckOnDuplicateKeyUpdateInSQL).getValue()).booleanValue(); this.doEscapeProcessing = ((Boolean)pset.getBooleanProperty(PropertyKey.enableEscapeProcessing).getValue()).booleanValue(); this.maxFieldSize = ((Integer)this.maxAllowedPacket.getValue()).intValue(); if (!((Boolean)this.dontTrackOpenResources.getValue()).booleanValue()) c.registerStatement(this);  int defaultFetchSize = ((Integer)pset.getIntegerProperty(PropertyKey.defaultFetchSize).getValue()).intValue(); if (defaultFetchSize != 0) setFetchSize(defaultFetchSize);  int maxRowsConn = ((Integer)pset.getIntegerProperty(PropertyKey.maxRows).getValue()).intValue(); if (maxRowsConn != -1) setMaxRows(maxRowsConn);  this.holdResultsOpenOverClose = ((Boolean)pset.getBooleanProperty(PropertyKey.holdResultsOpenOverStatementClose).getValue()).booleanValue(); this.resultSetFactory = new ResultSetFactory(this.connection, this); }
/*      */   public boolean getMoreResults() throws SQLException { try { return getMoreResults(1); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  }
/*      */   public boolean getMoreResults(int current) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { if (this.results == null) return false;  boolean streamingMode = createStreamingResultSet(); if (streamingMode && this.results.hasRows()) while (this.results.next());  ResultSetInternalMethods nextResultSet = (ResultSetInternalMethods)this.results.getNextResultset(); switch (current) { case 1: if (this.results != null) { if (!streamingMode && !((Boolean)this.dontTrackOpenResources.getValue()).booleanValue()) this.results.realClose(false);  this.results.clearNextResultset(); }  break;case 3: if (this.results != null) { if (!streamingMode && !((Boolean)this.dontTrackOpenResources.getValue()).booleanValue()) this.results.realClose(false);  this.results.clearNextResultset(); }  closeAllOpenResults(); break;case 2: if (!((Boolean)this.dontTrackOpenResources.getValue()).booleanValue()) this.openResults.add(this.results);  this.results.clearNextResultset(); break;default: throw SQLError.createSQLException(Messages.getString("Statement.19"), "S1009", getExceptionInterceptor()); }  this.results = nextResultSet; if (this.results == null) { this.updateCount = -1L; this.lastInsertId = -1L; } else if (this.results.hasRows()) { this.updateCount = -1L; this.lastInsertId = -1L; } else { this.updateCount = this.results.getUpdateCount(); this.lastInsertId = this.results.getUpdateID(); }  boolean moreResults = (this.results != null && this.results.hasRows()); if (!moreResults) checkAndPerformCloseOnCompletionAction();  return moreResults; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  }
/*      */   public int getQueryTimeout() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { return getTimeoutInMillis() / 1000; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  }
/* 2065 */   private long getRecordCountFromInfo(String serverInfo) { StringBuilder recordsBuf = new StringBuilder(); long recordsCount = 0L; long duplicatesCount = 0L; char c = Character.MIN_VALUE; int length = serverInfo.length(); int i = 0; for (; i < length; i++) { c = serverInfo.charAt(i); if (Character.isDigit(c)) break;  }  recordsBuf.append(c); i++; for (; i < length; i++) { c = serverInfo.charAt(i); if (!Character.isDigit(c)) break;  recordsBuf.append(c); }  recordsCount = Long.parseLong(recordsBuf.toString()); StringBuilder duplicatesBuf = new StringBuilder(); for (; i < length; i++) { c = serverInfo.charAt(i); if (Character.isDigit(c)) break;  }  duplicatesBuf.append(c); i++; for (; i < length; i++) { c = serverInfo.charAt(i); if (!Character.isDigit(c)) break;  duplicatesBuf.append(c); }  duplicatesCount = Long.parseLong(duplicatesBuf.toString()); return recordsCount - duplicatesCount; } public ResultSet getResultSet() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { return (this.results != null && this.results.hasRows()) ? (ResultSet)this.results : null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int getResultSetConcurrency() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { return this.resultSetConcurrency; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int getResultSetHoldability() throws SQLException { try { return 1; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } protected ResultSetInternalMethods getResultSetInternal() { try { try { synchronized (checkClosed().getConnectionMutex()) { return this.results; }  } catch (StatementIsClosedException e) { return this.results; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int getResultSetType() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { return this.query.getResultType().getIntValue(); }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public int getUpdateCount() throws SQLException { try { return Util.truncateAndConvertToInt(getLargeUpdateCount()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public SQLWarning getWarnings() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { if (isClearWarningsCalled()) return null;  SQLWarning pendingWarningsFromServer = this.session.getProtocol().convertShowWarningsToSQLWarnings(false); if (this.warningChain != null) { this.warningChain.setNextWarning(pendingWarningsFromServer); } else { this.warningChain = pendingWarningsFromServer; }  return this.warningChain; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } protected void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException { JdbcConnection locallyScopedConn = this.connection; if (locallyScopedConn == null || this.isClosed) return;  if (!((Boolean)this.dontTrackOpenResources.getValue()).booleanValue()) locallyScopedConn.unregisterStatement(this);  if (this.useUsageAdvisor && !calledExplicitly) this.session.getProfilerEventHandler().processEvent((byte)0, (Session)this.session, this, null, 0L, new Throwable(), Messages.getString("Statement.63"));  if (closeOpenResults) closeOpenResults = (!this.holdResultsOpenOverClose && !((Boolean)this.dontTrackOpenResources.getValue()).booleanValue());  if (closeOpenResults) { if (this.results != null) try { this.results.close(); } catch (Exception exception) {}  if (this.generatedKeysResults != null) try { this.generatedKeysResults.close(); } catch (Exception exception) {}  closeAllOpenResults(); }  clearAttributes(); this.isClosed = true; closeQuery(); this.results = null; this.generatedKeysResults = null; this.connection = null; this.session = null; this.warningChain = null; this.openResults = null; this.batchedGeneratedKeys = null; this.pingTarget = null; this.resultSetFactory = null; } public void setCursorName(String name) throws SQLException { try { return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public void setEscapeProcessing(boolean enable) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { this.doEscapeProcessing = enable; }  return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public void setFetchDirection(int direction) throws SQLException { try { switch (direction) { case 1000: case 1001: case 1002: return; }  throw SQLError.createSQLException(Messages.getString("Statement.5"), "S1009", getExceptionInterceptor()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public void setFetchSize(int rows) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { if ((rows < 0 && rows != Integer.MIN_VALUE) || (this.maxRows > 0 && rows > getMaxRows())) throw SQLError.createSQLException(Messages.getString("Statement.7"), "S1009", getExceptionInterceptor());  this.query.setResultFetchSize(rows); }  return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public void setHoldResultsOpenOverClose(boolean holdResultsOpenOverClose) { try { try { synchronized (checkClosed().getConnectionMutex()) { this.holdResultsOpenOverClose = holdResultsOpenOverClose; }  } catch (StatementIsClosedException statementIsClosedException) {} return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public void setMaxFieldSize(int max) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { if (max < 0) throw SQLError.createSQLException(Messages.getString("Statement.11"), "S1009", getExceptionInterceptor());  int maxBuf = ((Integer)this.maxAllowedPacket.getValue()).intValue(); if (max > maxBuf) throw SQLError.createSQLException(Messages.getString("Statement.13", new Object[] { Long.valueOf(maxBuf) }), "S1009", getExceptionInterceptor());  this.maxFieldSize = max; }  return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public void setMaxRows(int max) throws SQLException { try { setLargeMaxRows(max); return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public void setQueryTimeout(int seconds) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { if (seconds < 0) throw SQLError.createSQLException(Messages.getString("Statement.21"), "S1009", getExceptionInterceptor());  setTimeoutInMillis(seconds * 1000); }  return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } void setResultSetConcurrency(int concurrencyFlag) throws SQLException { try { try { synchronized (checkClosed().getConnectionMutex()) { this.resultSetConcurrency = concurrencyFlag; this.resultSetFactory = new ResultSetFactory(this.connection, this); }  } catch (StatementIsClosedException statementIsClosedException) {} return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } void setResultSetType(Resultset.Type typeFlag) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { this.query.setResultType(typeFlag); this.resultSetFactory = new ResultSetFactory(this.connection, this); }  } catch (StatementIsClosedException statementIsClosedException) {} } void setResultSetType(int typeFlag) throws SQLException { this.query.setResultType(Resultset.Type.fromValue(typeFlag, Resultset.Type.FORWARD_ONLY)); } protected void getBatchedGeneratedKeys(Statement batchedStatement) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { if (this.retrieveGeneratedKeys) { ResultSet rs = null; try { rs = batchedStatement.getGeneratedKeys(); while (rs.next()) { this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor())); }  } finally { if (rs != null) rs.close();  }  }  }  return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } protected void getBatchedGeneratedKeys(int maxKeys) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { if (this.retrieveGeneratedKeys) { ResultSetInternalMethods resultSetInternalMethods; ResultSet rs = null; try { resultSetInternalMethods = (maxKeys == 0) ? getGeneratedKeysInternal() : getGeneratedKeysInternal(maxKeys); while (resultSetInternalMethods.next()) { this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { resultSetInternalMethods.getBytes(1) }, getExceptionInterceptor())); }  } finally { this.isImplicitlyClosingResults = true; try { if (resultSetInternalMethods != null) resultSetInternalMethods.close();  } finally { this.isImplicitlyClosingResults = false; }  }  }  }  return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } private boolean useServerFetch() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { return (((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.useCursorFetch).getValue()).booleanValue() && this.query.getResultFetchSize() > 0 && this.query.getResultType() == Resultset.Type.FORWARD_ONLY); }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public boolean isClosed() throws SQLException { try { JdbcConnection locallyScopedConn = this.connection; if (locallyScopedConn == null) return true;  synchronized (locallyScopedConn.getConnectionMutex()) { return this.isClosed; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public boolean isPoolable() throws SQLException { try { checkClosed(); return this.isPoolable; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public void setPoolable(boolean poolable) throws SQLException { try { checkClosed(); this.isPoolable = poolable; return; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public boolean isWrapperFor(Class<?> iface) throws SQLException { try { checkClosed(); return iface.isInstance(this); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public <T> T unwrap(Class<T> iface) throws SQLException { try { try { return iface.cast(this); } catch (ClassCastException cce) { throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", getExceptionInterceptor()); }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } public InputStream getLocalInfileInputStream() { return this.session.getLocalInfileInputStream(); } public void setLocalInfileInputStream(InputStream stream) { this.session.setLocalInfileInputStream(stream); } public void setPingTarget(PingTarget pingTarget) { this.pingTarget = pingTarget; } public ExceptionInterceptor getExceptionInterceptor() { return this.exceptionInterceptor; } protected boolean containsOnDuplicateKeyInString(String sql) { return (ParseInfo.getOnDuplicateKeyLocation(sql, this.dontCheckOnDuplicateKeyUpdateInSQL, ((Boolean)this.rewriteBatchedStatements.getValue()).booleanValue(), this.session.getServerSession().isNoBackslashEscapesSet()) != -1); } public void closeOnCompletion() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) {
/* 2066 */         this.closeOnCompletion = true;
/*      */       }  return; }
/* 2068 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */      }
/*      */    public boolean isCloseOnCompletion() throws SQLException {
/*      */     
/* 2072 */     try { synchronized (checkClosed().getConnectionMutex())
/* 2073 */       { return this.closeOnCompletion; }  }
/* 2074 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public long[] executeLargeBatch() throws SQLException {
/*      */     
/* 2079 */     try { return executeBatchInternal(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public long executeLargeUpdate(String sql) throws SQLException {
/*      */     
/* 2084 */     try { return executeUpdateInternal(sql, false, false); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
/*      */     
/* 2089 */     try { return executeUpdateInternal(sql, false, (autoGeneratedKeys == 1)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
/*      */     
/* 2094 */     try { return executeUpdateInternal(sql, false, (columnIndexes != null && columnIndexes.length > 0)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
/*      */     
/* 2099 */     try { return executeUpdateInternal(sql, false, (columnNames != null && columnNames.length > 0)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public long getLargeMaxRows() throws SQLException {
/*      */     
/* 2105 */     try { return getMaxRows(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public long getLargeUpdateCount() throws SQLException {
/*      */     
/* 2110 */     try { synchronized (checkClosed().getConnectionMutex())
/* 2111 */       { if (this.results == null) {
/* 2112 */           return -1L;
/*      */         }
/*      */         
/* 2115 */         if (this.results.hasRows()) {
/* 2116 */           return -1L;
/*      */         }
/*      */         
/* 2119 */         return this.results.getUpdateCount(); }  }
/* 2120 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setLargeMaxRows(long max) throws SQLException {
/*      */     
/* 2125 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 2126 */         if (max > 50000000L || max < 0L) {
/* 2127 */           throw SQLError.createSQLException(Messages.getString("Statement.15") + max + " > " + 50000000 + ".", "S1009", 
/* 2128 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 2131 */         if (max == 0L) {
/* 2132 */           max = -1L;
/*      */         }
/*      */         
/* 2135 */         this.maxRows = (int)max;
/*      */       }  return; }
/* 2137 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getCurrentDatabase() {
/* 2141 */     return this.query.getCurrentDatabase();
/*      */   }
/*      */   
/*      */   public long getServerStatementId() {
/* 2145 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("Statement.65"));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T extends Resultset, M extends com.mysql.cj.protocol.Message> ProtocolEntityFactory<T, M> getResultSetFactory() {
/* 2151 */     return (ProtocolEntityFactory<T, M>)this.resultSetFactory;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getId() {
/* 2156 */     return this.query.getId();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCancelStatus(Query.CancelStatus cs) {
/* 2161 */     this.query.setCancelStatus(cs);
/*      */   }
/*      */   
/*      */   public void checkCancelTimeout() {
/*      */     
/* 2166 */     try { this.query.checkCancelTimeout(); return; }
/* 2167 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Session getSession() {
/* 2171 */     return (Session)this.session;
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getCancelTimeoutMutex() {
/* 2176 */     return this.query.getCancelTimeoutMutex();
/*      */   }
/*      */ 
/*      */   
/*      */   public void closeQuery() {
/* 2181 */     if (this.query != null) {
/* 2182 */       this.query.closeQuery();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public int getResultFetchSize() {
/* 2188 */     return this.query.getResultFetchSize();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setResultFetchSize(int fetchSize) {
/* 2193 */     this.query.setResultFetchSize(fetchSize);
/*      */   }
/*      */ 
/*      */   
/*      */   public Resultset.Type getResultType() {
/* 2198 */     return this.query.getResultType();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setResultType(Resultset.Type resultSetType) {
/* 2203 */     this.query.setResultType(resultSetType);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getTimeoutInMillis() {
/* 2208 */     return this.query.getTimeoutInMillis();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTimeoutInMillis(int timeoutInMillis) {
/* 2213 */     this.query.setTimeoutInMillis(timeoutInMillis);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getExecuteTime() {
/* 2218 */     return this.query.getExecuteTime();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setExecuteTime(long executeTime) {
/* 2223 */     this.query.setExecuteTime(executeTime);
/*      */   }
/*      */ 
/*      */   
/*      */   public AtomicBoolean getStatementExecuting() {
/* 2228 */     return this.query.getStatementExecuting();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCurrentDatabase(String currentDb) {
/* 2233 */     this.query.setCurrentDatabase(currentDb);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isClearWarningsCalled() {
/* 2238 */     return this.query.isClearWarningsCalled();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setClearWarningsCalled(boolean clearWarningsCalled) {
/* 2243 */     this.query.setClearWarningsCalled(clearWarningsCalled);
/*      */   }
/*      */ 
/*      */   
/*      */   public Query getQuery() {
/* 2248 */     return this.query;
/*      */   }
/*      */ 
/*      */   
/*      */   public QueryAttributesBindings getQueryAttributesBindings() {
/* 2253 */     return this.query.getQueryAttributesBindings();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setAttribute(String name, Object value) {
/* 2258 */     getQueryAttributesBindings().setAttribute(name, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public void clearAttributes() {
/* 2263 */     getQueryAttributesBindings().clearAttributes();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\StatementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */