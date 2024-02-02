/*      */ package com.mysql.cj.jdbc;
/*      */ 
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.MysqlConnection;
/*      */ import com.mysql.cj.ServerVersion;
/*      */ import com.mysql.cj.Session;
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.conf.PropertySet;
/*      */ import com.mysql.cj.exceptions.CJException;
/*      */ import com.mysql.cj.exceptions.ConnectionIsClosedException;
/*      */ import com.mysql.cj.exceptions.ExceptionFactory;
/*      */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*      */ import com.mysql.cj.interceptors.QueryInterceptor;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*      */ import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
/*      */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*      */ import com.mysql.cj.protocol.ServerSessionStateController;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.NClob;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLClientInfoException;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Struct;
/*      */ import java.sql.Wrapper;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.concurrent.Executor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ConnectionWrapper
/*      */   extends WrapperBase
/*      */   implements JdbcConnection
/*      */ {
/*   74 */   protected JdbcConnection mc = null;
/*      */   
/*   76 */   private String invalidHandleStr = "Logical handle no longer valid";
/*      */   
/*      */   private boolean closed;
/*      */   
/*      */   private boolean isForXa;
/*      */ 
/*      */   
/*      */   protected static ConnectionWrapper getInstance(MysqlPooledConnection mysqlPooledConnection, JdbcConnection mysqlConnection, boolean forXa) throws SQLException {
/*   84 */     return new ConnectionWrapper(mysqlPooledConnection, mysqlConnection, forXa);
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
/*      */   
/*      */   public ConnectionWrapper(MysqlPooledConnection mysqlPooledConnection, JdbcConnection mysqlConnection, boolean forXa) throws SQLException {
/*  101 */     super(mysqlPooledConnection);
/*      */     
/*  103 */     this.mc = mysqlConnection;
/*  104 */     this.closed = false;
/*  105 */     this.isForXa = forXa;
/*      */     
/*  107 */     if (this.isForXa) {
/*  108 */       setInGlobalTx(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void setAutoCommit(boolean autoCommit) throws SQLException {
/*      */     
/*  115 */     try { checkClosed(); if (autoCommit && isInGlobalTx()) {
/*  116 */         throw SQLError.createSQLException(Messages.getString("ConnectionWrapper.0"), "2D000", 1401, this.exceptionInterceptor);
/*      */       }
/*      */ 
/*      */       
/*      */       try {
/*  121 */         this.mc.setAutoCommit(autoCommit);
/*  122 */       } catch (SQLException sqlException) {
/*  123 */         checkAndFireConnectionError(sqlException);
/*      */       }  return; }
/*  125 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean getAutoCommit() throws SQLException {
/*      */     
/*  131 */     try { checkClosed(); try { return this.mc.getAutoCommit(); }
/*  132 */       catch (SQLException sqlException)
/*  133 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  136 */         return false; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public void setDatabase(String dbName) throws SQLException {
/*      */     
/*  142 */     try { checkClosed(); try { this.mc.setDatabase(dbName); }
/*  143 */       catch (SQLException sqlException)
/*  144 */       { checkAndFireConnectionError(sqlException); }
/*      */        return; }
/*  146 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public String getDatabase() throws SQLException {
/*      */     
/*  151 */     try { checkClosed(); try { return this.mc.getDatabase(); }
/*  152 */       catch (SQLException sqlException)
/*  153 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  156 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCatalog(String catalog) throws SQLException {
/*      */     
/*  163 */     try { checkClosed(); try { this.mc.setCatalog(catalog); }
/*  164 */       catch (SQLException sqlException)
/*  165 */       { checkAndFireConnectionError(sqlException); }
/*      */        return; }
/*  167 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public String getCatalog() throws SQLException {
/*      */     
/*  173 */     try { checkClosed(); try { return this.mc.getCatalog(); }
/*  174 */       catch (SQLException sqlException)
/*  175 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  178 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public boolean isClosed() throws SQLException {
/*      */     
/*  183 */     try { return (this.closed || this.mc.isClosed()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean isSourceConnection() {
/*  188 */     return this.mc.isSourceConnection();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setHoldability(int arg0) throws SQLException {
/*      */     
/*  194 */     try { checkClosed(); try { this.mc.setHoldability(arg0); }
/*  195 */       catch (SQLException sqlException)
/*  196 */       { checkAndFireConnectionError(sqlException); }
/*      */        return; }
/*  198 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public int getHoldability() throws SQLException {
/*      */     
/*  203 */     try { checkClosed(); try { return this.mc.getHoldability(); }
/*  204 */       catch (SQLException sqlException)
/*  205 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  208 */         return 1; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public long getIdleFor() {
/*  213 */     return this.mc.getIdleFor();
/*      */   }
/*      */ 
/*      */   
/*      */   public DatabaseMetaData getMetaData() throws SQLException {
/*      */     
/*  219 */     try { checkClosed(); try { return this.mc.getMetaData(); }
/*  220 */       catch (SQLException sqlException)
/*  221 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  224 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public void setReadOnly(boolean readOnly) throws SQLException {
/*      */     
/*  230 */     try { checkClosed(); try { this.mc.setReadOnly(readOnly); }
/*  231 */       catch (SQLException sqlException)
/*  232 */       { checkAndFireConnectionError(sqlException); }
/*      */        return; }
/*  234 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public boolean isReadOnly() throws SQLException {
/*      */     
/*  239 */     try { checkClosed(); try { return this.mc.isReadOnly(); }
/*  240 */       catch (SQLException sqlException)
/*  241 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  244 */         return false; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public Savepoint setSavepoint() throws SQLException {
/*      */     
/*  249 */     try { checkClosed(); if (isInGlobalTx()) {
/*  250 */         throw SQLError.createSQLException(Messages.getString("ConnectionWrapper.0"), "2D000", 1401, this.exceptionInterceptor);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  255 */       try { return this.mc.setSavepoint(); }
/*  256 */       catch (SQLException sqlException)
/*  257 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  260 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public Savepoint setSavepoint(String arg0) throws SQLException {
/*      */     
/*  265 */     try { checkClosed(); if (isInGlobalTx()) {
/*  266 */         throw SQLError.createSQLException(Messages.getString("ConnectionWrapper.0"), "2D000", 1401, this.exceptionInterceptor);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  271 */       try { return this.mc.setSavepoint(arg0); }
/*  272 */       catch (SQLException sqlException)
/*  273 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  276 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public void setTransactionIsolation(int level) throws SQLException {
/*      */     
/*  282 */     try { checkClosed(); try { this.mc.setTransactionIsolation(level); }
/*  283 */       catch (SQLException sqlException)
/*  284 */       { checkAndFireConnectionError(sqlException); }
/*      */        return; }
/*  286 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public int getTransactionIsolation() throws SQLException {
/*      */     
/*  291 */     try { checkClosed(); try { return this.mc.getTransactionIsolation(); }
/*  292 */       catch (SQLException sqlException)
/*  293 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  296 */         return 4; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public Map<String, Class<?>> getTypeMap() throws SQLException {
/*      */     
/*  302 */     try { checkClosed(); try { return this.mc.getTypeMap(); }
/*  303 */       catch (SQLException sqlException)
/*  304 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  307 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public SQLWarning getWarnings() throws SQLException {
/*      */     
/*  313 */     try { checkClosed(); try { return this.mc.getWarnings(); }
/*  314 */       catch (SQLException sqlException)
/*  315 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  318 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public void clearWarnings() throws SQLException {
/*      */     
/*  324 */     try { checkClosed(); try { this.mc.clearWarnings(); }
/*  325 */       catch (SQLException sqlException)
/*  326 */       { checkAndFireConnectionError(sqlException); }
/*      */        return; }
/*  328 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws SQLException {
/*      */     
/*      */     try { try {
/*  340 */         close(true);
/*      */       } finally {
/*  342 */         this.unwrappedInterfaces = null;
/*      */       }  return; }
/*  344 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public void commit() throws SQLException {
/*      */     
/*  348 */     try { checkClosed(); if (isInGlobalTx()) {
/*  349 */         throw SQLError.createSQLException(Messages.getString("ConnectionWrapper.1"), "2D000", 1401, this.exceptionInterceptor);
/*      */       }
/*      */ 
/*      */       
/*      */       try {
/*  354 */         this.mc.commit();
/*  355 */       } catch (SQLException sqlException) {
/*  356 */         checkAndFireConnectionError(sqlException);
/*      */       }  return; }
/*  358 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public Statement createStatement() throws SQLException {
/*      */     
/*  363 */     try { checkClosed(); try { return StatementWrapper.getInstance(this, this.pooledConnection, this.mc.createStatement()); }
/*  364 */       catch (SQLException sqlException)
/*  365 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  368 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     
/*  374 */     try { checkClosed(); try { return StatementWrapper.getInstance(this, this.pooledConnection, this.mc.createStatement(resultSetType, resultSetConcurrency)); }
/*  375 */       catch (SQLException sqlException)
/*  376 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  379 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException {
/*      */     
/*  385 */     try { checkClosed(); try { return StatementWrapper.getInstance(this, this.pooledConnection, this.mc.createStatement(arg0, arg1, arg2)); }
/*  386 */       catch (SQLException sqlException)
/*  387 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  390 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public String nativeSQL(String sql) throws SQLException {
/*      */     
/*  396 */     try { checkClosed(); try { return this.mc.nativeSQL(sql); }
/*  397 */       catch (SQLException sqlException)
/*  398 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  401 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public CallableStatement prepareCall(String sql) throws SQLException {
/*      */     
/*  407 */     try { checkClosed(); try { return CallableStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareCall(sql)); }
/*  408 */       catch (SQLException sqlException)
/*  409 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  412 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     
/*  418 */     try { checkClosed(); try { return CallableStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareCall(sql, resultSetType, resultSetConcurrency)); }
/*  419 */       catch (SQLException sqlException)
/*  420 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  423 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3) throws SQLException {
/*      */     
/*  429 */     try { checkClosed(); try { return CallableStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareCall(arg0, arg1, arg2, arg3)); }
/*  430 */       catch (SQLException sqlException)
/*  431 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  434 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public PreparedStatement clientPrepare(String sql) throws SQLException {
/*      */     
/*  439 */     try { checkClosed(); try { return new PreparedStatementWrapper(this, this.pooledConnection, this.mc.clientPrepareStatement(sql)); }
/*  440 */       catch (SQLException sqlException)
/*  441 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  444 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public PreparedStatement clientPrepare(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     
/*  449 */     try { checkClosed(); try { return new PreparedStatementWrapper(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency)); }
/*  450 */       catch (SQLException sqlException)
/*  451 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  454 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public PreparedStatement prepareStatement(String sql) throws SQLException {
/*      */     
/*  459 */     try { checkClosed(); PreparedStatement res = null;
/*      */       try {
/*  461 */         res = PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(sql));
/*  462 */       } catch (SQLException sqlException) {
/*  463 */         checkAndFireConnectionError(sqlException);
/*      */       } 
/*      */       
/*  466 */       return res; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     
/*  472 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(sql, resultSetType, resultSetConcurrency)); }
/*  473 */       catch (SQLException sqlException)
/*  474 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  477 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException {
/*      */     
/*  483 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1, arg2, arg3)); }
/*  484 */       catch (SQLException sqlException)
/*  485 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  488 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException {
/*      */     
/*  494 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1)); }
/*  495 */       catch (SQLException sqlException)
/*  496 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  499 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException {
/*      */     
/*  505 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1)); }
/*  506 */       catch (SQLException sqlException)
/*  507 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  510 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException {
/*      */     
/*  516 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1)); }
/*  517 */       catch (SQLException sqlException)
/*  518 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  521 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public void releaseSavepoint(Savepoint arg0) throws SQLException {
/*      */     
/*  527 */     try { checkClosed(); try { this.mc.releaseSavepoint(arg0); }
/*  528 */       catch (SQLException sqlException)
/*  529 */       { checkAndFireConnectionError(sqlException); }
/*      */        return; }
/*  531 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public void rollback() throws SQLException {
/*      */     
/*  535 */     try { checkClosed(); if (isInGlobalTx()) {
/*  536 */         throw SQLError.createSQLException(Messages.getString("ConnectionWrapper.2"), "2D000", 1401, this.exceptionInterceptor);
/*      */       }
/*      */ 
/*      */       
/*      */       try {
/*  541 */         this.mc.rollback();
/*  542 */       } catch (SQLException sqlException) {
/*  543 */         checkAndFireConnectionError(sqlException);
/*      */       }  return; }
/*  545 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public void rollback(Savepoint arg0) throws SQLException {
/*      */     
/*  549 */     try { checkClosed(); if (isInGlobalTx()) {
/*  550 */         throw SQLError.createSQLException(Messages.getString("ConnectionWrapper.2"), "2D000", 1401, this.exceptionInterceptor);
/*      */       }
/*      */ 
/*      */       
/*      */       try {
/*  555 */         this.mc.rollback(arg0);
/*  556 */       } catch (SQLException sqlException) {
/*  557 */         checkAndFireConnectionError(sqlException);
/*      */       }  return; }
/*  559 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public boolean isSameResource(JdbcConnection c) {
/*  563 */     if (c instanceof ConnectionWrapper) {
/*  564 */       return this.mc.isSameResource(((ConnectionWrapper)c).mc);
/*      */     }
/*  566 */     return this.mc.isSameResource(c);
/*      */   }
/*      */   
/*      */   protected void close(boolean fireClosedEvent) throws SQLException {
/*  570 */     synchronized (this.pooledConnection) {
/*  571 */       if (this.closed) {
/*      */         return;
/*      */       }
/*      */       
/*  575 */       if (!isInGlobalTx() && ((Boolean)this.mc.getPropertySet().getBooleanProperty(PropertyKey.rollbackOnPooledClose).getValue()).booleanValue() && !getAutoCommit()) {
/*  576 */         rollback();
/*      */       }
/*      */       
/*  579 */       if (fireClosedEvent) {
/*  580 */         this.pooledConnection.callConnectionEventListeners(2, null);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  585 */       this.closed = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void checkClosed() {
/*  591 */     if (this.closed) {
/*  592 */       throw (ConnectionIsClosedException)ExceptionFactory.createException(ConnectionIsClosedException.class, this.invalidHandleStr, this.exceptionInterceptor);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isInGlobalTx() {
/*  598 */     return this.mc.isInGlobalTx();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setInGlobalTx(boolean flag) {
/*  603 */     this.mc.setInGlobalTx(flag);
/*      */   }
/*      */   
/*      */   public void ping() throws SQLException {
/*      */     try {
/*  608 */       if (this.mc != null)
/*  609 */         this.mc.ping();  return;
/*      */     } catch (CJException cJException) {
/*  611 */       throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
/*      */     } 
/*      */   }
/*      */   public void changeUser(String userName, String newPassword) throws SQLException {
/*      */     
/*  616 */     try { checkClosed(); try { this.mc.changeUser(userName, newPassword); }
/*  617 */       catch (SQLException sqlException)
/*  618 */       { checkAndFireConnectionError(sqlException); }
/*      */        return; }
/*  620 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   @Deprecated
/*      */   public void clearHasTriedMaster() {
/*  625 */     this.mc.clearHasTriedMaster();
/*      */   }
/*      */ 
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
/*      */     
/*  631 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql)); }
/*  632 */       catch (SQLException sqlException)
/*  633 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  636 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*      */     
/*  642 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, autoGenKeyIndex)); }
/*  643 */       catch (SQLException sqlException)
/*  644 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  647 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     
/*  653 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency)); }
/*  654 */       catch (SQLException sqlException)
/*  655 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  658 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*      */     
/*  665 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc
/*  666 */             .clientPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability)); }
/*  667 */       catch (SQLException sqlException)
/*  668 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  671 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*      */     
/*  677 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, autoGenKeyIndexes)); }
/*  678 */       catch (SQLException sqlException)
/*  679 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  682 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*      */     
/*  688 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, autoGenKeyColNames)); }
/*  689 */       catch (SQLException sqlException)
/*  690 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  693 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public int getActiveStatementCount() {
/*  698 */     return this.mc.getActiveStatementCount();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getStatementComment() {
/*  703 */     return this.mc.getStatementComment();
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public boolean hasTriedMaster() {
/*  709 */     return this.mc.hasTriedMaster();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean lowerCaseTableNames() {
/*  714 */     return this.mc.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   
/*      */   public void resetServerState() throws SQLException {
/*      */     
/*  720 */     try { checkClosed(); try { this.mc.resetServerState(); }
/*  721 */       catch (SQLException sqlException)
/*  722 */       { checkAndFireConnectionError(sqlException); }
/*      */        return; }
/*  724 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
/*      */     
/*  729 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql)); }
/*  730 */       catch (SQLException sqlException)
/*  731 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  734 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*      */     
/*  740 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, autoGenKeyIndex)); }
/*  741 */       catch (SQLException sqlException)
/*  742 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  745 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*      */     
/*  751 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, resultSetType, resultSetConcurrency)); }
/*  752 */       catch (SQLException sqlException)
/*  753 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  756 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*      */     
/*  763 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc
/*  764 */             .serverPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability)); }
/*  765 */       catch (SQLException sqlException)
/*  766 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  769 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*      */     
/*  775 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, autoGenKeyIndexes)); }
/*  776 */       catch (SQLException sqlException)
/*  777 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  780 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*      */     
/*  786 */     try { checkClosed(); try { return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, autoGenKeyColNames)); }
/*  787 */       catch (SQLException sqlException)
/*  788 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  791 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public void setFailedOver(boolean flag) {
/*  796 */     this.mc.setFailedOver(flag);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setStatementComment(String comment) {
/*  801 */     this.mc.setStatementComment(comment);
/*      */   }
/*      */ 
/*      */   
/*      */   public void shutdownServer() throws SQLException {
/*      */     try {
/*  807 */       checkClosed(); try { this.mc.shutdownServer(); }
/*  808 */       catch (SQLException sqlException)
/*  809 */       { checkAndFireConnectionError(sqlException); }
/*      */        return;
/*      */     } catch (CJException cJException) {
/*  812 */       throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
/*      */     } 
/*      */   }
/*      */   public int getAutoIncrementIncrement() {
/*  816 */     return this.mc.getAutoIncrementIncrement();
/*      */   }
/*      */ 
/*      */   
/*      */   public ExceptionInterceptor getExceptionInterceptor() {
/*  821 */     return this.pooledConnection.getExceptionInterceptor();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasSameProperties(JdbcConnection c) {
/*  826 */     return this.mc.hasSameProperties(c);
/*      */   }
/*      */ 
/*      */   
/*      */   public Properties getProperties() {
/*  831 */     return this.mc.getProperties();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getHost() {
/*  836 */     return this.mc.getHost();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setProxy(JdbcConnection conn) {
/*  841 */     this.mc.setProxy(conn);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
/*      */     
/*  847 */     try { checkClosed(); try { this.mc.setTypeMap(map); }
/*  848 */       catch (SQLException sqlException)
/*  849 */       { checkAndFireConnectionError(sqlException); }
/*      */        return; }
/*  851 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public boolean isServerLocal() throws SQLException {
/*      */     
/*  855 */     try { return this.mc.isServerLocal(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public void setSchema(String schema) throws SQLException {
/*      */     
/*  860 */     try { checkClosed(); this.mc.setSchema(schema); return; }
/*  861 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public String getSchema() throws SQLException {
/*      */     
/*  865 */     try { return this.mc.getSchema(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public void abort(Executor executor) throws SQLException {
/*      */     
/*  870 */     try { this.mc.abort(executor); return; }
/*  871 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
/*      */     
/*  875 */     try { this.mc.setNetworkTimeout(executor, milliseconds); return; }
/*  876 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public int getNetworkTimeout() throws SQLException {
/*      */     
/*  880 */     try { return this.mc.getNetworkTimeout(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public void abortInternal() throws SQLException {
/*      */     
/*  885 */     try { this.mc.abortInternal(); return; }
/*  886 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public Object getConnectionMutex() {
/*  890 */     return this.mc.getConnectionMutex();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSessionMaxRows() {
/*  895 */     return this.mc.getSessionMaxRows();
/*      */   }
/*      */   
/*      */   public void setSessionMaxRows(int max) throws SQLException {
/*      */     
/*  900 */     try { this.mc.setSessionMaxRows(max); return; }
/*  901 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public Clob createClob() throws SQLException {
/*      */     
/*  906 */     try { checkClosed(); try { return this.mc.createClob(); }
/*  907 */       catch (SQLException sqlException)
/*  908 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  911 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public Blob createBlob() throws SQLException {
/*      */     
/*  917 */     try { checkClosed(); try { return this.mc.createBlob(); }
/*  918 */       catch (SQLException sqlException)
/*  919 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  922 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public NClob createNClob() throws SQLException {
/*      */     
/*  928 */     try { checkClosed(); try { return this.mc.createNClob(); }
/*  929 */       catch (SQLException sqlException)
/*  930 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  933 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public SQLXML createSQLXML() throws SQLException {
/*      */     
/*  939 */     try { checkClosed(); try { return this.mc.createSQLXML(); }
/*  940 */       catch (SQLException sqlException)
/*  941 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  944 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public synchronized boolean isValid(int timeout) throws SQLException {
/*      */     
/*      */     try { 
/*  950 */       try { return this.mc.isValid(timeout); }
/*  951 */       catch (SQLException sqlException)
/*  952 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/*  955 */         return false; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public void setClientInfo(String name, String value) throws SQLClientInfoException {
/*      */     
/*      */     try { try {
/*  961 */         checkClosed();
/*      */         
/*  963 */         this.mc.setClientInfo(name, value);
/*  964 */       } catch (SQLException sqlException) {
/*      */         try {
/*  966 */           checkAndFireConnectionError(sqlException);
/*  967 */         } catch (SQLException sqlEx2) {
/*  968 */           SQLClientInfoException clientEx = new SQLClientInfoException();
/*  969 */           clientEx.initCause(sqlEx2);
/*      */           
/*  971 */           throw clientEx;
/*      */         } 
/*      */       }  return; }
/*  974 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public void setClientInfo(Properties properties) throws SQLClientInfoException {
/*      */     
/*      */     try { try {
/*  979 */         checkClosed();
/*      */         
/*  981 */         this.mc.setClientInfo(properties);
/*  982 */       } catch (SQLException sqlException) {
/*      */         try {
/*  984 */           checkAndFireConnectionError(sqlException);
/*  985 */         } catch (SQLException sqlEx2) {
/*  986 */           SQLClientInfoException clientEx = new SQLClientInfoException();
/*  987 */           clientEx.initCause(sqlEx2);
/*      */           
/*  989 */           throw clientEx;
/*      */         } 
/*      */       }  return; }
/*  992 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public String getClientInfo(String name) throws SQLException {
/*      */     
/*  997 */     try { checkClosed(); try { return this.mc.getClientInfo(name); }
/*  998 */       catch (SQLException sqlException)
/*  999 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/* 1002 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public Properties getClientInfo() throws SQLException {
/*      */     
/* 1008 */     try { checkClosed(); try { return this.mc.getClientInfo(); }
/* 1009 */       catch (SQLException sqlException)
/* 1010 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/* 1013 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
/*      */     
/* 1019 */     try { checkClosed(); try { return this.mc.createArrayOf(typeName, elements); }
/* 1020 */       catch (SQLException sqlException)
/* 1021 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/* 1024 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
/*      */     
/* 1030 */     try { checkClosed(); try { return this.mc.createStruct(typeName, attributes); }
/* 1031 */       catch (SQLException sqlException)
/* 1032 */       { checkAndFireConnectionError(sqlException);
/*      */ 
/*      */         
/* 1035 */         return null; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
/*      */     
/*      */     try { 
/* 1041 */       try { if ("java.sql.Connection".equals(iface.getName()) || "java.sql.Wrapper.class".equals(iface.getName())) {
/* 1042 */           return iface.cast(this);
/*      */         }
/*      */         
/* 1045 */         if (this.unwrappedInterfaces == null) {
/* 1046 */           this.unwrappedInterfaces = new HashMap<>();
/*      */         }
/*      */         
/* 1049 */         Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*      */         
/* 1051 */         if (cachedUnwrapped == null) {
/* 1052 */           cachedUnwrapped = Proxy.newProxyInstance(this.mc.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.mc));
/*      */           
/* 1054 */           this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*      */         } 
/*      */         
/* 1057 */         return iface.cast(cachedUnwrapped); }
/* 1058 */       catch (ClassCastException cce)
/* 1059 */       { throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", this.exceptionInterceptor); }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*      */     try {
/* 1066 */       boolean isInstance = iface.isInstance(this);
/*      */       
/* 1068 */       if (isInstance) {
/* 1069 */         return true;
/*      */       }
/*      */       
/* 1072 */       return (iface.getName().equals(JdbcConnection.class.getName()) || iface.getName().equals(MysqlConnection.class.getName()) || iface
/* 1073 */         .getName().equals(Connection.class.getName()) || iface.getName().equals(Wrapper.class.getName()) || iface
/* 1074 */         .getName().equals(AutoCloseable.class.getName()));
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
/*      */     } 
/*      */   } public Session getSession() {
/* 1079 */     return this.mc.getSession();
/*      */   }
/*      */ 
/*      */   
/*      */   public long getId() {
/* 1084 */     return this.mc.getId();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getURL() {
/* 1089 */     return this.mc.getURL();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getUser() {
/* 1094 */     return this.mc.getUser();
/*      */   }
/*      */ 
/*      */   
/*      */   public void createNewIO(boolean isForReconnect) {
/* 1099 */     this.mc.createNewIO(isForReconnect);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isProxySet() {
/* 1104 */     return this.mc.isProxySet();
/*      */   }
/*      */ 
/*      */   
/*      */   public JdbcPropertySet getPropertySet() {
/* 1109 */     return this.mc.getPropertySet();
/*      */   }
/*      */ 
/*      */   
/*      */   public CachedResultSetMetaData getCachedMetaData(String sql) {
/* 1114 */     return this.mc.getCachedMetaData(sql);
/*      */   }
/*      */ 
/*      */   
/*      */   public String getCharacterSetMetadata() {
/* 1119 */     return this.mc.getCharacterSetMetadata();
/*      */   }
/*      */   
/*      */   public Statement getMetadataSafeStatement() throws SQLException {
/*      */     
/* 1124 */     try { return this.mc.getMetadataSafeStatement(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public ServerVersion getServerVersion() {
/* 1129 */     return this.mc.getServerVersion();
/*      */   }
/*      */ 
/*      */   
/*      */   public List<QueryInterceptor> getQueryInterceptorsInstances() {
/* 1134 */     return this.mc.getQueryInterceptorsInstances();
/*      */   }
/*      */   
/*      */   public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet) throws SQLException {
/*      */     
/* 1139 */     try { this.mc.initializeResultsMetadataFromCache(sql, cachedMetaData, resultSet); return; }
/* 1140 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public void initializeSafeQueryInterceptors() throws SQLException {
/*      */     
/* 1144 */     try { this.mc.initializeSafeQueryInterceptors(); return; }
/* 1145 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public boolean isReadOnly(boolean useSessionStatus) throws SQLException {
/*      */     
/* 1149 */     try { checkClosed(); return this.mc.isReadOnly(useSessionStatus); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
/*      */     
/* 1154 */     try { this.mc.pingInternal(checkForClosedConnection, timeoutMillis); return; }
/* 1155 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason) throws SQLException {
/*      */     
/* 1159 */     try { this.mc.realClose(calledExplicitly, issueRollback, skipLocalTeardown, reason); return; }
/* 1160 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public void recachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
/*      */     
/* 1164 */     try { this.mc.recachePreparedStatement(pstmt); return; }
/* 1165 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   } public void decachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
/*      */     
/* 1169 */     try { this.mc.decachePreparedStatement(pstmt); return; }
/* 1170 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public void registerStatement(JdbcStatement stmt) {
/* 1174 */     this.mc.registerStatement(stmt);
/*      */   }
/*      */   
/*      */   public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
/*      */     
/* 1179 */     try { this.mc.setReadOnlyInternal(readOnlyFlag); return; }
/* 1180 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public boolean storesLowerCaseTableName() {
/* 1184 */     return this.mc.storesLowerCaseTableName();
/*      */   }
/*      */   
/*      */   public void throwConnectionClosedException() throws SQLException {
/*      */     
/* 1189 */     try { this.mc.throwConnectionClosedException(); return; }
/* 1190 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public void transactionBegun() {
/* 1194 */     this.mc.transactionBegun();
/*      */   }
/*      */ 
/*      */   
/*      */   public void transactionCompleted() {
/* 1199 */     this.mc.transactionCompleted();
/*      */   }
/*      */ 
/*      */   
/*      */   public void unregisterStatement(JdbcStatement stmt) {
/* 1204 */     this.mc.unregisterStatement(stmt);
/*      */   }
/*      */   
/*      */   public void unSafeQueryInterceptors() throws SQLException {
/*      */     
/* 1209 */     try { this.mc.unSafeQueryInterceptors(); return; }
/* 1210 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   public JdbcConnection getMultiHostSafeProxy() {
/* 1214 */     return this.mc.getMultiHostSafeProxy();
/*      */   }
/*      */ 
/*      */   
/*      */   public JdbcConnection getMultiHostParentProxy() {
/* 1219 */     return this.mc.getMultiHostParentProxy();
/*      */   }
/*      */ 
/*      */   
/*      */   public JdbcConnection getActiveMySQLConnection() {
/* 1224 */     return this.mc.getActiveMySQLConnection();
/*      */   }
/*      */   
/*      */   public ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
/*      */     
/* 1229 */     try { return this.mc.getClientInfoProviderImpl(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*      */   
/*      */   }
/*      */   
/*      */   public String getHostPortPair() {
/* 1234 */     return this.mc.getHostPortPair();
/*      */   }
/*      */ 
/*      */   
/*      */   public void normalClose() {
/* 1239 */     this.mc.normalClose();
/*      */   }
/*      */ 
/*      */   
/*      */   public void cleanup(Throwable whyCleanedUp) {
/* 1244 */     this.mc.cleanup(whyCleanedUp);
/*      */   }
/*      */ 
/*      */   
/*      */   public ServerSessionStateController getServerSessionStateController() {
/* 1249 */     return this.mc.getServerSessionStateController();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ConnectionWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */