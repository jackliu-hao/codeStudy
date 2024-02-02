/*     */ package com.mysql.cj.jdbc.ha;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.ServerVersion;
/*     */ import com.mysql.cj.Session;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.interceptors.QueryInterceptor;
/*     */ import com.mysql.cj.jdbc.ClientInfoProvider;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import com.mysql.cj.jdbc.JdbcPreparedStatement;
/*     */ import com.mysql.cj.jdbc.JdbcPropertySet;
/*     */ import com.mysql.cj.jdbc.JdbcStatement;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
/*     */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*     */ import com.mysql.cj.protocol.ServerSessionStateController;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Clob;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.NClob;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Savepoint;
/*     */ import java.sql.Statement;
/*     */ import java.sql.Struct;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.Executor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultiHostMySQLConnection
/*     */   implements JdbcConnection
/*     */ {
/*     */   protected MultiHostConnectionProxy thisAsProxy;
/*     */   
/*     */   public MultiHostMySQLConnection(MultiHostConnectionProxy proxy) {
/*  88 */     this.thisAsProxy = proxy;
/*     */   }
/*     */   
/*     */   public MultiHostConnectionProxy getThisAsProxy() {
/*  92 */     return this.thisAsProxy;
/*     */   }
/*     */ 
/*     */   
/*     */   public JdbcConnection getActiveMySQLConnection() {
/*  97 */     synchronized (this.thisAsProxy) {
/*  98 */       return this.thisAsProxy.currentConnection;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void abortInternal() throws SQLException {
/*     */     
/* 104 */     try { getActiveMySQLConnection().abortInternal(); return; }
/* 105 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void changeUser(String userName, String newPassword) throws SQLException {
/*     */     
/* 109 */     try { getActiveMySQLConnection().changeUser(userName, newPassword); return; }
/* 110 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void checkClosed() {
/* 114 */     getActiveMySQLConnection().checkClosed();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void clearHasTriedMaster() {
/* 120 */     getActiveMySQLConnection().clearHasTriedMaster();
/*     */   }
/*     */   
/*     */   public void clearWarnings() throws SQLException {
/*     */     
/* 125 */     try { getActiveMySQLConnection().clearWarnings(); return; }
/* 126 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*     */     
/* 130 */     try { return getActiveMySQLConnection().clientPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*     */     
/* 135 */     try { return getActiveMySQLConnection().clientPrepareStatement(sql, resultSetType, resultSetConcurrency); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*     */     
/* 140 */     try { return getActiveMySQLConnection().clientPrepareStatement(sql, autoGenKeyIndex); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*     */     
/* 145 */     try { return getActiveMySQLConnection().clientPrepareStatement(sql, autoGenKeyIndexes); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*     */     
/* 150 */     try { return getActiveMySQLConnection().clientPrepareStatement(sql, autoGenKeyColNames); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
/*     */     
/* 155 */     try { return getActiveMySQLConnection().clientPrepareStatement(sql); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void close() throws SQLException {
/*     */     
/* 160 */     try { getActiveMySQLConnection().close(); return; }
/* 161 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void commit() throws SQLException {
/*     */     
/* 165 */     try { getActiveMySQLConnection().commit(); return; }
/* 166 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void createNewIO(boolean isForReconnect) {
/* 170 */     getActiveMySQLConnection().createNewIO(isForReconnect);
/*     */   }
/*     */   
/*     */   public Statement createStatement() throws SQLException {
/*     */     
/* 175 */     try { return getActiveMySQLConnection().createStatement(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*     */     
/* 180 */     try { return getActiveMySQLConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
/*     */     
/* 185 */     try { return getActiveMySQLConnection().createStatement(resultSetType, resultSetConcurrency); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public int getActiveStatementCount() {
/* 190 */     return getActiveMySQLConnection().getActiveStatementCount();
/*     */   }
/*     */   
/*     */   public boolean getAutoCommit() throws SQLException {
/*     */     
/* 195 */     try { return getActiveMySQLConnection().getAutoCommit(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public int getAutoIncrementIncrement() {
/* 200 */     return getActiveMySQLConnection().getAutoIncrementIncrement();
/*     */   }
/*     */ 
/*     */   
/*     */   public CachedResultSetMetaData getCachedMetaData(String sql) {
/* 205 */     return getActiveMySQLConnection().getCachedMetaData(sql);
/*     */   }
/*     */   
/*     */   public String getCatalog() throws SQLException {
/*     */     
/* 210 */     try { return getActiveMySQLConnection().getCatalog(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public String getCharacterSetMetadata() {
/* 215 */     return getActiveMySQLConnection().getCharacterSetMetadata();
/*     */   }
/*     */ 
/*     */   
/*     */   public ExceptionInterceptor getExceptionInterceptor() {
/* 220 */     return getActiveMySQLConnection().getExceptionInterceptor();
/*     */   }
/*     */   
/*     */   public int getHoldability() throws SQLException {
/*     */     
/* 225 */     try { return getActiveMySQLConnection().getHoldability(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public String getHost() {
/* 230 */     return getActiveMySQLConnection().getHost();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getId() {
/* 235 */     return getActiveMySQLConnection().getId();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getIdleFor() {
/* 240 */     return getActiveMySQLConnection().getIdleFor();
/*     */   }
/*     */ 
/*     */   
/*     */   public JdbcConnection getMultiHostSafeProxy() {
/* 245 */     return getThisAsProxy().getProxy();
/*     */   }
/*     */ 
/*     */   
/*     */   public JdbcConnection getMultiHostParentProxy() {
/* 250 */     return getThisAsProxy().getParentProxy();
/*     */   }
/*     */   
/*     */   public DatabaseMetaData getMetaData() throws SQLException {
/*     */     
/* 255 */     try { return getActiveMySQLConnection().getMetaData(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public Statement getMetadataSafeStatement() throws SQLException {
/*     */     
/* 260 */     try { return getActiveMySQLConnection().getMetadataSafeStatement(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public Properties getProperties() {
/* 265 */     return getActiveMySQLConnection().getProperties();
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerVersion getServerVersion() {
/* 270 */     return getActiveMySQLConnection().getServerVersion();
/*     */   }
/*     */ 
/*     */   
/*     */   public Session getSession() {
/* 275 */     return getActiveMySQLConnection().getSession();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStatementComment() {
/* 280 */     return getActiveMySQLConnection().getStatementComment();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<QueryInterceptor> getQueryInterceptorsInstances() {
/* 285 */     return getActiveMySQLConnection().getQueryInterceptorsInstances();
/*     */   }
/*     */   
/*     */   public int getTransactionIsolation() throws SQLException {
/*     */     
/* 290 */     try { return getActiveMySQLConnection().getTransactionIsolation(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public Map<String, Class<?>> getTypeMap() throws SQLException {
/*     */     
/* 295 */     try { return getActiveMySQLConnection().getTypeMap(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public String getURL() {
/* 300 */     return getActiveMySQLConnection().getURL();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUser() {
/* 305 */     return getActiveMySQLConnection().getUser();
/*     */   }
/*     */   
/*     */   public SQLWarning getWarnings() throws SQLException {
/*     */     
/* 310 */     try { return getActiveMySQLConnection().getWarnings(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public boolean hasSameProperties(JdbcConnection c) {
/* 315 */     return getActiveMySQLConnection().hasSameProperties(c);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean hasTriedMaster() {
/* 321 */     return getActiveMySQLConnection().hasTriedMaster();
/*     */   }
/*     */   
/*     */   public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet) throws SQLException {
/*     */     
/* 326 */     try { getActiveMySQLConnection().initializeResultsMetadataFromCache(sql, cachedMetaData, resultSet); return; }
/* 327 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void initializeSafeQueryInterceptors() throws SQLException {
/*     */     
/* 331 */     try { getActiveMySQLConnection().initializeSafeQueryInterceptors(); return; }
/* 332 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public boolean isInGlobalTx() {
/* 336 */     return getActiveMySQLConnection().isInGlobalTx();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSourceConnection() {
/* 341 */     return getThisAsProxy().isSourceConnection();
/*     */   }
/*     */   
/*     */   public boolean isReadOnly() throws SQLException {
/*     */     
/* 346 */     try { return getActiveMySQLConnection().isReadOnly(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public boolean isReadOnly(boolean useSessionStatus) throws SQLException {
/*     */     
/* 351 */     try { return getActiveMySQLConnection().isReadOnly(useSessionStatus); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public boolean isSameResource(JdbcConnection otherConnection) {
/* 356 */     return getActiveMySQLConnection().isSameResource(otherConnection);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean lowerCaseTableNames() {
/* 361 */     return getActiveMySQLConnection().lowerCaseTableNames();
/*     */   }
/*     */   
/*     */   public String nativeSQL(String sql) throws SQLException {
/*     */     
/* 366 */     try { return getActiveMySQLConnection().nativeSQL(sql); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void ping() throws SQLException {
/*     */     
/* 371 */     try { getActiveMySQLConnection().ping(); return; }
/* 372 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
/*     */     
/* 376 */     try { getActiveMySQLConnection().pingInternal(checkForClosedConnection, timeoutMillis); return; }
/* 377 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*     */     
/* 381 */     try { return getActiveMySQLConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*     */     
/* 386 */     try { return getActiveMySQLConnection().prepareCall(sql, resultSetType, resultSetConcurrency); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public CallableStatement prepareCall(String sql) throws SQLException {
/*     */     
/* 391 */     try { return getActiveMySQLConnection().prepareCall(sql); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*     */     
/* 396 */     try { return getActiveMySQLConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*     */     
/* 401 */     try { return getActiveMySQLConnection().prepareStatement(sql, resultSetType, resultSetConcurrency); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement prepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*     */     
/* 406 */     try { return getActiveMySQLConnection().prepareStatement(sql, autoGenKeyIndex); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*     */     
/* 411 */     try { return getActiveMySQLConnection().prepareStatement(sql, autoGenKeyIndexes); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*     */     
/* 416 */     try { return getActiveMySQLConnection().prepareStatement(sql, autoGenKeyColNames); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement prepareStatement(String sql) throws SQLException {
/*     */     
/* 421 */     try { return getActiveMySQLConnection().prepareStatement(sql); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason) throws SQLException {
/*     */     
/* 426 */     try { getActiveMySQLConnection().realClose(calledExplicitly, issueRollback, skipLocalTeardown, reason); return; }
/* 427 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void recachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
/*     */     
/* 431 */     try { getActiveMySQLConnection().recachePreparedStatement(pstmt); return; }
/* 432 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void decachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
/*     */     
/* 436 */     try { getActiveMySQLConnection().decachePreparedStatement(pstmt); return; }
/* 437 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void registerStatement(JdbcStatement stmt) {
/* 441 */     getActiveMySQLConnection().registerStatement(stmt);
/*     */   }
/*     */   
/*     */   public void releaseSavepoint(Savepoint arg0) throws SQLException {
/*     */     
/* 446 */     try { getActiveMySQLConnection().releaseSavepoint(arg0); return; }
/* 447 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void resetServerState() throws SQLException {
/*     */     
/* 451 */     try { getActiveMySQLConnection().resetServerState(); return; }
/* 452 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void rollback() throws SQLException {
/*     */     
/* 456 */     try { getActiveMySQLConnection().rollback(); return; }
/* 457 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void rollback(Savepoint savepoint) throws SQLException {
/*     */     
/* 461 */     try { getActiveMySQLConnection().rollback(savepoint); return; }
/* 462 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*     */     
/* 466 */     try { return getActiveMySQLConnection().serverPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*     */     
/* 471 */     try { return getActiveMySQLConnection().serverPrepareStatement(sql, resultSetType, resultSetConcurrency); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*     */     
/* 476 */     try { return getActiveMySQLConnection().serverPrepareStatement(sql, autoGenKeyIndex); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*     */     
/* 481 */     try { return getActiveMySQLConnection().serverPrepareStatement(sql, autoGenKeyIndexes); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*     */     
/* 486 */     try { return getActiveMySQLConnection().serverPrepareStatement(sql, autoGenKeyColNames); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
/*     */     
/* 491 */     try { return getActiveMySQLConnection().serverPrepareStatement(sql); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void setAutoCommit(boolean autoCommitFlag) throws SQLException {
/*     */     
/* 496 */     try { getActiveMySQLConnection().setAutoCommit(autoCommitFlag); return; }
/* 497 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void setDatabase(String dbName) throws SQLException {
/*     */     
/* 501 */     try { getActiveMySQLConnection().setDatabase(dbName); return; }
/* 502 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public String getDatabase() throws SQLException {
/*     */     
/* 506 */     try { return getActiveMySQLConnection().getDatabase(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void setCatalog(String catalog) throws SQLException {
/*     */     
/* 511 */     try { getActiveMySQLConnection().setCatalog(catalog); return; }
/* 512 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void setFailedOver(boolean flag) {
/* 516 */     getActiveMySQLConnection().setFailedOver(flag);
/*     */   }
/*     */   
/*     */   public void setHoldability(int arg0) throws SQLException {
/*     */     
/* 521 */     try { getActiveMySQLConnection().setHoldability(arg0); return; }
/* 522 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void setInGlobalTx(boolean flag) {
/* 526 */     getActiveMySQLConnection().setInGlobalTx(flag);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setProxy(JdbcConnection proxy) {
/* 531 */     getThisAsProxy().setProxy(proxy);
/*     */   }
/*     */   
/*     */   public void setReadOnly(boolean readOnlyFlag) throws SQLException {
/*     */     
/* 536 */     try { getActiveMySQLConnection().setReadOnly(readOnlyFlag); return; }
/* 537 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
/*     */     
/* 541 */     try { getActiveMySQLConnection().setReadOnlyInternal(readOnlyFlag); return; }
/* 542 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public Savepoint setSavepoint() throws SQLException {
/*     */     
/* 546 */     try { return getActiveMySQLConnection().setSavepoint(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public Savepoint setSavepoint(String name) throws SQLException {
/*     */     
/* 551 */     try { return getActiveMySQLConnection().setSavepoint(name); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public void setStatementComment(String comment) {
/* 556 */     getActiveMySQLConnection().setStatementComment(comment);
/*     */   }
/*     */   
/*     */   public void setTransactionIsolation(int level) throws SQLException {
/*     */     
/* 561 */     try { getActiveMySQLConnection().setTransactionIsolation(level); return; }
/* 562 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void shutdownServer() throws SQLException {
/*     */     
/* 566 */     try { getActiveMySQLConnection().shutdownServer(); return; }
/* 567 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public boolean storesLowerCaseTableName() {
/* 571 */     return getActiveMySQLConnection().storesLowerCaseTableName();
/*     */   }
/*     */   
/*     */   public void throwConnectionClosedException() throws SQLException {
/*     */     
/* 576 */     try { getActiveMySQLConnection().throwConnectionClosedException(); return; }
/* 577 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void transactionBegun() {
/* 581 */     getActiveMySQLConnection().transactionBegun();
/*     */   }
/*     */ 
/*     */   
/*     */   public void transactionCompleted() {
/* 586 */     getActiveMySQLConnection().transactionCompleted();
/*     */   }
/*     */ 
/*     */   
/*     */   public void unregisterStatement(JdbcStatement stmt) {
/* 591 */     getActiveMySQLConnection().unregisterStatement(stmt);
/*     */   }
/*     */   
/*     */   public void unSafeQueryInterceptors() throws SQLException {
/*     */     
/* 596 */     try { getActiveMySQLConnection().unSafeQueryInterceptors(); return; }
/* 597 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public boolean isClosed() throws SQLException {
/*     */     
/* 601 */     try { return (getThisAsProxy()).isClosed; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public boolean isProxySet() {
/* 606 */     return getActiveMySQLConnection().isProxySet();
/*     */   }
/*     */   
/*     */   public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
/*     */     
/* 611 */     try { getActiveMySQLConnection().setTypeMap(map); return; }
/* 612 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public boolean isServerLocal() throws SQLException {
/*     */     
/* 616 */     try { return getActiveMySQLConnection().isServerLocal(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void setSchema(String schema) throws SQLException {
/*     */     
/* 621 */     try { getActiveMySQLConnection().setSchema(schema); return; }
/* 622 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public String getSchema() throws SQLException {
/*     */     
/* 626 */     try { return getActiveMySQLConnection().getSchema(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void abort(Executor executor) throws SQLException {
/*     */     
/* 631 */     try { getActiveMySQLConnection().abort(executor); return; }
/* 632 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
/*     */     
/* 636 */     try { getActiveMySQLConnection().setNetworkTimeout(executor, milliseconds); return; }
/* 637 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public int getNetworkTimeout() throws SQLException {
/*     */     
/* 641 */     try { return getActiveMySQLConnection().getNetworkTimeout(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public Object getConnectionMutex() {
/* 646 */     return getActiveMySQLConnection().getConnectionMutex();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSessionMaxRows() {
/* 651 */     return getActiveMySQLConnection().getSessionMaxRows();
/*     */   }
/*     */   
/*     */   public void setSessionMaxRows(int max) throws SQLException {
/*     */     
/* 656 */     try { getActiveMySQLConnection().setSessionMaxRows(max); return; }
/* 657 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public SQLXML createSQLXML() throws SQLException {
/*     */     
/* 661 */     try { return getActiveMySQLConnection().createSQLXML(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
/*     */     
/* 666 */     try { return getActiveMySQLConnection().createArrayOf(typeName, elements); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
/*     */     
/* 671 */     try { return getActiveMySQLConnection().createStruct(typeName, attributes); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public Properties getClientInfo() throws SQLException {
/*     */     
/* 676 */     try { return getActiveMySQLConnection().getClientInfo(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public String getClientInfo(String name) throws SQLException {
/*     */     
/* 681 */     try { return getActiveMySQLConnection().getClientInfo(name); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public boolean isValid(int timeout) throws SQLException {
/*     */     
/* 686 */     try { return getActiveMySQLConnection().isValid(timeout); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public void setClientInfo(Properties properties) throws SQLClientInfoException {
/* 691 */     getActiveMySQLConnection().setClientInfo(properties);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClientInfo(String name, String value) throws SQLClientInfoException {
/* 696 */     getActiveMySQLConnection().setClientInfo(name, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*     */     
/* 702 */     try { return iface.isInstance(this); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*     */     try {
/*     */       try {
/* 709 */         return iface.cast(this);
/* 710 */       } catch (ClassCastException cce) {
/* 711 */         throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", 
/* 712 */             getExceptionInterceptor());
/*     */       } 
/*     */     } catch (CJException cJException) {
/*     */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*     */     }  } public Blob createBlob() throws SQLException {
/*     */     
/* 718 */     try { return getActiveMySQLConnection().createBlob(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public Clob createClob() throws SQLException {
/*     */     
/* 723 */     try { return getActiveMySQLConnection().createClob(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public NClob createNClob() throws SQLException {
/*     */     
/* 728 */     try { return getActiveMySQLConnection().createNClob(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
/*     */     
/* 733 */     try { synchronized (getThisAsProxy())
/* 734 */       { return getActiveMySQLConnection().getClientInfoProviderImpl(); }  }
/* 735 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public JdbcPropertySet getPropertySet() {
/* 740 */     return getActiveMySQLConnection().getPropertySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getHostPortPair() {
/* 745 */     return getActiveMySQLConnection().getHostPortPair();
/*     */   }
/*     */ 
/*     */   
/*     */   public void normalClose() {
/* 750 */     getActiveMySQLConnection().normalClose();
/*     */   }
/*     */ 
/*     */   
/*     */   public void cleanup(Throwable whyCleanedUp) {
/* 755 */     getActiveMySQLConnection().cleanup(whyCleanedUp);
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSessionStateController getServerSessionStateController() {
/* 760 */     return getActiveMySQLConnection().getServerSessionStateController();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\MultiHostMySQLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */