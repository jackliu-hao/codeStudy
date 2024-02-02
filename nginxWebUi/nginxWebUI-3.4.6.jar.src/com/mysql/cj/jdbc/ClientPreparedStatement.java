/*      */ package com.mysql.cj.jdbc;
/*      */ 
/*      */ import com.mysql.cj.BindValue;
/*      */ import com.mysql.cj.CancelQueryTask;
/*      */ import com.mysql.cj.ClientPreparedQuery;
/*      */ import com.mysql.cj.ClientPreparedQueryBindings;
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.MysqlType;
/*      */ import com.mysql.cj.NativeSession;
/*      */ import com.mysql.cj.ParseInfo;
/*      */ import com.mysql.cj.PreparedQuery;
/*      */ import com.mysql.cj.Query;
/*      */ import com.mysql.cj.QueryAttributesBindValue;
/*      */ import com.mysql.cj.QueryBindings;
/*      */ import com.mysql.cj.QueryReturnType;
/*      */ import com.mysql.cj.Session;
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.exceptions.CJException;
/*      */ import com.mysql.cj.exceptions.FeatureNotAvailableException;
/*      */ import com.mysql.cj.exceptions.StatementIsClosedException;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*      */ import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
/*      */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*      */ import com.mysql.cj.jdbc.result.ResultSetMetaData;
/*      */ import com.mysql.cj.protocol.ColumnDefinition;
/*      */ import com.mysql.cj.protocol.Message;
/*      */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*      */ import com.mysql.cj.result.Row;
/*      */ import com.mysql.cj.util.Util;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.JDBCType;
/*      */ import java.sql.NClob;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.RowId;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLType;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ClientPreparedStatement
/*      */   extends StatementImpl
/*      */   implements JdbcPreparedStatement
/*      */ {
/*      */   protected boolean batchHasPlainStatements = false;
/*      */   protected MysqlParameterMetadata parameterMetaData;
/*      */   private ResultSetMetaData pstmtResultMetaData;
/*      */   protected String batchedValuesClause;
/*      */   private boolean doPingInstead;
/*      */   private boolean compensateForOnDuplicateKeyUpdate = false;
/*  118 */   protected int rewrittenBatchSize = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static ClientPreparedStatement getInstance(JdbcConnection conn, String sql, String db) throws SQLException {
/*  134 */     return new ClientPreparedStatement(conn, sql, db);
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
/*      */ 
/*      */   
/*      */   protected static ClientPreparedStatement getInstance(JdbcConnection conn, String sql, String db, ParseInfo cachedParseInfo) throws SQLException {
/*  153 */     return new ClientPreparedStatement(conn, sql, db, cachedParseInfo);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void initQuery() {
/*  158 */     this.query = (Query)new ClientPreparedQuery(this.session);
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
/*      */   protected ClientPreparedStatement(JdbcConnection conn, String db) throws SQLException {
/*  173 */     super(conn, db);
/*      */     
/*  175 */     setPoolable(true);
/*  176 */     this.compensateForOnDuplicateKeyUpdate = ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.compensateOnDuplicateKeyUpdateCounts).getValue()).booleanValue();
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
/*      */   public ClientPreparedStatement(JdbcConnection conn, String sql, String db) throws SQLException {
/*  193 */     this(conn, sql, db, (ParseInfo)null);
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
/*      */ 
/*      */   
/*      */   public ClientPreparedStatement(JdbcConnection conn, String sql, String db, ParseInfo cachedParseInfo) throws SQLException {
/*  212 */     this(conn, db);
/*      */     
/*      */     try {
/*  215 */       ((PreparedQuery)this.query).checkNullOrEmptyQuery(sql);
/*  216 */       ((PreparedQuery)this.query).setOriginalSql(sql);
/*  217 */       ((PreparedQuery)this.query).setParseInfo((cachedParseInfo != null) ? cachedParseInfo : new ParseInfo(sql, (Session)this.session, this.charEncoding));
/*  218 */     } catch (CJException e) {
/*  219 */       throw SQLExceptionsMapping.translateException(e, this.exceptionInterceptor);
/*      */     } 
/*      */     
/*  222 */     this.doPingInstead = sql.startsWith("/* ping */");
/*      */     
/*  224 */     initializeFromParseInfo();
/*      */   }
/*      */ 
/*      */   
/*      */   public QueryBindings<?> getQueryBindings() {
/*  229 */     return ((PreparedQuery)this.query).getQueryBindings();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*  239 */     StringBuilder buf = new StringBuilder();
/*  240 */     buf.append(getClass().getName());
/*  241 */     buf.append(": ");
/*      */     
/*      */     try {
/*  244 */       buf.append(asSql());
/*  245 */     } catch (SQLException sqlEx) {
/*  246 */       buf.append("EXCEPTION: " + sqlEx.toString());
/*      */     } 
/*      */     
/*  249 */     return buf.toString();
/*      */   }
/*      */   
/*      */   public void addBatch() throws SQLException {
/*      */     
/*  254 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  255 */         QueryBindings<?> queryBindings = ((PreparedQuery)this.query).getQueryBindings();
/*  256 */         queryBindings.checkAllParametersSet();
/*  257 */         this.query.addBatch(queryBindings.clone());
/*      */       }  return; }
/*  259 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void addBatch(String sql) throws SQLException {
/*      */     
/*  263 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  264 */         this.batchHasPlainStatements = true;
/*      */         
/*  266 */         super.addBatch(sql);
/*      */       }  return; }
/*  268 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public String asSql() throws SQLException {
/*  271 */     return ((PreparedQuery)this.query).asSql(false);
/*      */   }
/*      */   public String asSql(boolean quoteStreamsAndUnknowns) throws SQLException {
/*      */     
/*  275 */     try { synchronized (checkClosed().getConnectionMutex())
/*  276 */       { return ((PreparedQuery)this.query).asSql(quoteStreamsAndUnknowns); }  }
/*  277 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void clearBatch() throws SQLException {
/*      */     
/*  282 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  283 */         this.batchHasPlainStatements = false;
/*      */         
/*  285 */         super.clearBatch();
/*      */       }  return; }
/*  287 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void clearParameters() throws SQLException {
/*      */     try {
/*  291 */       synchronized (checkClosed().getConnectionMutex()) {
/*  292 */         for (BindValue bv : ((PreparedQuery)this.query).getQueryBindings().getBindValues())
/*  293 */           bv.reset(); 
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/*  296 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean checkReadOnlySafeStatement() throws SQLException {
/*      */     
/*  306 */     try { synchronized (checkClosed().getConnectionMutex())
/*  307 */       { return (ParseInfo.isReadOnlySafeQuery(((PreparedQuery)this.query).getOriginalSql(), this.session.getServerSession().isNoBackslashEscapesSet()) || 
/*  308 */           !this.connection.isReadOnly()); }  }
/*  309 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean execute() throws SQLException {
/*      */     
/*  314 */     try { synchronized (checkClosed().getConnectionMutex())
/*      */       
/*  316 */       { JdbcConnection locallyScopedConn = this.connection;
/*      */         
/*  318 */         if (!this.doPingInstead && !checkReadOnlySafeStatement()) {
/*  319 */           throw SQLError.createSQLException(Messages.getString("PreparedStatement.20") + Messages.getString("PreparedStatement.21"), "S1009", this.exceptionInterceptor);
/*      */         }
/*      */ 
/*      */         
/*  323 */         ResultSetInternalMethods rs = null;
/*      */         
/*  325 */         this.lastQueryIsOnDupKeyUpdate = false;
/*      */         
/*  327 */         if (this.retrieveGeneratedKeys) {
/*  328 */           this.lastQueryIsOnDupKeyUpdate = containsOnDuplicateKeyUpdateInSQL();
/*      */         }
/*      */         
/*  331 */         this.batchedGeneratedKeys = null;
/*      */         
/*  333 */         resetCancelledState();
/*      */         
/*  335 */         implicitlyCloseAllOpenResults();
/*      */         
/*  337 */         clearWarnings();
/*      */         
/*  339 */         if (this.doPingInstead) {
/*  340 */           doPingInstead();
/*      */           
/*  342 */           return true;
/*      */         } 
/*      */         
/*  345 */         setupStreamingTimeout(locallyScopedConn);
/*      */         
/*  347 */         Message sendPacket = ((PreparedQuery)this.query).fillSendPacket();
/*      */         
/*  349 */         String oldDb = null;
/*      */         
/*  351 */         if (!locallyScopedConn.getDatabase().equals(getCurrentDatabase())) {
/*  352 */           oldDb = locallyScopedConn.getDatabase();
/*  353 */           locallyScopedConn.setDatabase(getCurrentDatabase());
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  359 */         CachedResultSetMetaData cachedMetadata = null;
/*      */         
/*  361 */         boolean cacheResultSetMetadata = ((Boolean)locallyScopedConn.getPropertySet().getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()).booleanValue();
/*  362 */         if (cacheResultSetMetadata) {
/*  363 */           cachedMetadata = locallyScopedConn.getCachedMetaData(((PreparedQuery)this.query).getOriginalSql());
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  369 */         locallyScopedConn.setSessionMaxRows((getParseInfo().getFirstStmtChar() == 'S') ? this.maxRows : -1);
/*      */         
/*  371 */         rs = executeInternal(this.maxRows, sendPacket, createStreamingResultSet(), (getParseInfo().getFirstStmtChar() == 'S'), (ColumnDefinition)cachedMetadata, false);
/*      */         
/*  373 */         if (cachedMetadata != null) {
/*  374 */           locallyScopedConn.initializeResultsMetadataFromCache(((PreparedQuery)this.query).getOriginalSql(), cachedMetadata, rs);
/*      */         }
/*  376 */         else if (rs.hasRows() && cacheResultSetMetadata) {
/*  377 */           locallyScopedConn.initializeResultsMetadataFromCache(((PreparedQuery)this.query).getOriginalSql(), (CachedResultSetMetaData)null, rs);
/*      */         } 
/*      */ 
/*      */         
/*  381 */         if (this.retrieveGeneratedKeys) {
/*  382 */           rs.setFirstCharOfQuery(getParseInfo().getFirstStmtChar());
/*      */         }
/*      */         
/*  385 */         if (oldDb != null) {
/*  386 */           locallyScopedConn.setDatabase(oldDb);
/*      */         }
/*      */         
/*  389 */         if (rs != null) {
/*  390 */           this.lastInsertId = rs.getUpdateID();
/*      */           
/*  392 */           this.results = rs;
/*      */         } 
/*      */         
/*  395 */         return (rs != null && rs.hasRows()); }  }
/*  396 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   protected long[] executeBatchInternal() throws SQLException {
/*  401 */     synchronized (checkClosed().getConnectionMutex()) {
/*      */       
/*  403 */       if (this.connection.isReadOnly()) {
/*  404 */         throw new SQLException(Messages.getString("PreparedStatement.25") + Messages.getString("PreparedStatement.26"), "S1009");
/*      */       }
/*      */ 
/*      */       
/*  408 */       if (this.query.getBatchedArgs() == null || this.query.getBatchedArgs().size() == 0) {
/*  409 */         return new long[0];
/*      */       }
/*      */ 
/*      */       
/*  413 */       int batchTimeout = getTimeoutInMillis();
/*  414 */       setTimeoutInMillis(0);
/*      */       
/*  416 */       resetCancelledState();
/*      */       
/*      */       try {
/*  419 */         statementBegins();
/*      */         
/*  421 */         clearWarnings();
/*      */         
/*  423 */         if (!this.batchHasPlainStatements && ((Boolean)this.rewriteBatchedStatements.getValue()).booleanValue()) {
/*      */           
/*  425 */           if (getParseInfo().canRewriteAsMultiValueInsertAtSqlLevel()) {
/*  426 */             return executeBatchedInserts(batchTimeout);
/*      */           }
/*      */           
/*  429 */           if (!this.batchHasPlainStatements && this.query.getBatchedArgs() != null && this.query
/*  430 */             .getBatchedArgs().size() > 3) {
/*  431 */             return executePreparedBatchAsMultiStatement(batchTimeout);
/*      */           }
/*      */         } 
/*      */         
/*  435 */         return executeBatchSerially(batchTimeout);
/*      */       } finally {
/*  437 */         this.query.getStatementExecuting().set(false);
/*      */         
/*  439 */         clearBatch();
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
/*      */ 
/*      */   
/*      */   protected long[] executePreparedBatchAsMultiStatement(int batchTimeout) throws SQLException {
/*      */     
/*  457 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*      */         
/*  459 */         if (this.batchedValuesClause == null) {
/*  460 */           this.batchedValuesClause = ((PreparedQuery)this.query).getOriginalSql() + ";";
/*      */         }
/*      */         
/*  463 */         JdbcConnection locallyScopedConn = this.connection;
/*      */         
/*  465 */         boolean multiQueriesEnabled = ((Boolean)locallyScopedConn.getPropertySet().getBooleanProperty(PropertyKey.allowMultiQueries).getValue()).booleanValue();
/*  466 */         CancelQueryTask timeoutTask = null;
/*      */ 
/*      */         
/*  469 */         try { clearWarnings();
/*      */           
/*  471 */           int numBatchedArgs = this.query.getBatchedArgs().size();
/*      */           
/*  473 */           if (this.retrieveGeneratedKeys) {
/*  474 */             this.batchedGeneratedKeys = new ArrayList<>(numBatchedArgs);
/*      */           }
/*      */           
/*  477 */           int numValuesPerBatch = ((PreparedQuery)this.query).computeBatchSize(numBatchedArgs);
/*      */           
/*  479 */           if (numBatchedArgs < numValuesPerBatch) {
/*  480 */             numValuesPerBatch = numBatchedArgs;
/*      */           }
/*      */           
/*  483 */           PreparedStatement batchedStatement = null;
/*      */           
/*  485 */           int batchedParamIndex = 1;
/*  486 */           int numberToExecuteAsMultiValue = 0;
/*  487 */           int batchCounter = 0;
/*  488 */           int updateCountCounter = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */            }
/*      */         
/*      */         finally
/*      */         
/*      */         { 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  594 */           stopQueryTimer(timeoutTask, false, false);
/*  595 */           resetCancelledState();
/*      */           
/*  597 */           if (!multiQueriesEnabled) {
/*  598 */             ((NativeSession)locallyScopedConn.getSession()).disableMultiQueries();
/*      */           }
/*      */           
/*  601 */           clearBatch(); } 
/*      */       }  }
/*  603 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   protected int setOneBatchedParameterSet(PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet) throws SQLException {
/*  607 */     QueryBindings<?> paramArg = (QueryBindings)paramSet;
/*      */     
/*  609 */     BindValue[] bindValues = paramArg.getBindValues();
/*      */     
/*  611 */     for (int j = 0; j < bindValues.length; j++) {
/*  612 */       if (bindValues[j].isNull()) {
/*  613 */         batchedStatement.setNull(batchedParamIndex++, MysqlType.NULL.getJdbcType());
/*      */       }
/*  615 */       else if (bindValues[j].isStream()) {
/*  616 */         batchedStatement.setBinaryStream(batchedParamIndex++, bindValues[j].getStreamValue(), bindValues[j].getStreamLength());
/*      */       } else {
/*  618 */         ((JdbcPreparedStatement)batchedStatement).setBytesNoEscapeNoQuotes(batchedParamIndex++, bindValues[j].getByteValue());
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  623 */     return batchedParamIndex;
/*      */   }
/*      */   private String generateMultiStatementForBatch(int numBatches) throws SQLException {
/*      */     
/*  627 */     try { synchronized (checkClosed().getConnectionMutex())
/*  628 */       { String origSql = ((PreparedQuery)this.query).getOriginalSql();
/*  629 */         StringBuilder newStatementSql = new StringBuilder((origSql.length() + 1) * numBatches);
/*      */         
/*  631 */         newStatementSql.append(origSql);
/*      */         
/*  633 */         for (int i = 0; i < numBatches - 1; i++) {
/*  634 */           newStatementSql.append(';');
/*  635 */           newStatementSql.append(origSql);
/*      */         } 
/*      */         
/*  638 */         return newStatementSql.toString(); }  }
/*  639 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
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
/*      */   protected long[] executeBatchedInserts(int batchTimeout) throws SQLException {
/*      */     
/*  655 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  656 */         String valuesClause = getParseInfo().getValuesClause();
/*      */         
/*  658 */         JdbcConnection locallyScopedConn = this.connection;
/*      */         
/*  660 */         if (valuesClause == null) {
/*  661 */           return executeBatchSerially(batchTimeout);
/*      */         }
/*      */         
/*  664 */         int numBatchedArgs = this.query.getBatchedArgs().size();
/*      */         
/*  666 */         if (this.retrieveGeneratedKeys) {
/*  667 */           this.batchedGeneratedKeys = new ArrayList<>(numBatchedArgs);
/*      */         }
/*      */         
/*  670 */         int numValuesPerBatch = ((PreparedQuery)this.query).computeBatchSize(numBatchedArgs);
/*      */         
/*  672 */         if (numBatchedArgs < numValuesPerBatch) {
/*  673 */           numValuesPerBatch = numBatchedArgs;
/*      */         }
/*      */         
/*  676 */         JdbcPreparedStatement batchedStatement = null;
/*      */         
/*  678 */         int batchedParamIndex = 1;
/*  679 */         long updateCountRunningTotal = 0L;
/*  680 */         int numberToExecuteAsMultiValue = 0;
/*  681 */         int batchCounter = 0;
/*  682 */         CancelQueryTask timeoutTask = null;
/*  683 */         SQLException sqlEx = null;
/*      */         
/*  685 */         long[] updateCounts = new long[numBatchedArgs];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         try {  }
/*      */         finally
/*  773 */         { stopQueryTimer(timeoutTask, false, false);
/*  774 */           resetCancelledState(); } 
/*      */       }  }
/*  776 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
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
/*      */   protected long[] executeBatchSerially(int batchTimeout) throws SQLException {
/*      */     
/*  790 */     try { synchronized (checkClosed().getConnectionMutex())
/*  791 */       { if (this.connection == null) {
/*  792 */           checkClosed();
/*      */         }
/*      */         
/*  795 */         long[] updateCounts = null;
/*      */         
/*  797 */         if (this.query.getBatchedArgs() != null) {
/*  798 */           int nbrCommands = this.query.getBatchedArgs().size();
/*  799 */           updateCounts = new long[nbrCommands];
/*      */           
/*  801 */           for (int i = 0; i < nbrCommands; i++) {
/*  802 */             updateCounts[i] = -3L;
/*      */           }
/*      */           
/*  805 */           SQLException sqlEx = null;
/*      */           
/*  807 */           CancelQueryTask timeoutTask = null;
/*      */           
/*      */           try {
/*  810 */             timeoutTask = startQueryTimer(this, batchTimeout);
/*      */             
/*  812 */             if (this.retrieveGeneratedKeys) {
/*  813 */               this.batchedGeneratedKeys = new ArrayList<>(nbrCommands);
/*      */             }
/*      */             
/*  816 */             int batchCommandIndex = ((PreparedQuery)this.query).getBatchCommandIndex();
/*      */             
/*  818 */             for (batchCommandIndex = 0; batchCommandIndex < nbrCommands; batchCommandIndex++) {
/*      */               
/*  820 */               ((PreparedQuery)this.query).setBatchCommandIndex(batchCommandIndex);
/*      */               
/*  822 */               Object arg = this.query.getBatchedArgs().get(batchCommandIndex);
/*      */               
/*      */               try {
/*  825 */                 if (arg instanceof String) {
/*  826 */                   updateCounts[batchCommandIndex] = executeUpdateInternal((String)arg, true, this.retrieveGeneratedKeys);
/*      */ 
/*      */                   
/*  829 */                   getBatchedGeneratedKeys((this.results.getFirstCharOfQuery() == 'I' && containsOnDuplicateKeyInString((String)arg)) ? 1 : 0);
/*      */                 } else {
/*  831 */                   QueryBindings<?> queryBindings = (QueryBindings)arg;
/*  832 */                   updateCounts[batchCommandIndex] = executeUpdateInternal(queryBindings, true);
/*      */ 
/*      */                   
/*  835 */                   getBatchedGeneratedKeys(containsOnDuplicateKeyUpdateInSQL() ? 1 : 0);
/*      */                 } 
/*  837 */               } catch (SQLException ex) {
/*  838 */                 updateCounts[batchCommandIndex] = -3L;
/*      */                 
/*  840 */                 if (this.continueBatchOnError && !(ex instanceof com.mysql.cj.jdbc.exceptions.MySQLTimeoutException) && !(ex instanceof com.mysql.cj.jdbc.exceptions.MySQLStatementCancelledException) && 
/*  841 */                   !hasDeadlockOrTimeoutRolledBackTx(ex)) {
/*  842 */                   sqlEx = ex;
/*      */                 } else {
/*  844 */                   long[] newUpdateCounts = new long[batchCommandIndex];
/*  845 */                   System.arraycopy(updateCounts, 0, newUpdateCounts, 0, batchCommandIndex);
/*      */                   
/*  847 */                   throw SQLError.createBatchUpdateException(ex, newUpdateCounts, this.exceptionInterceptor);
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */             
/*  852 */             if (sqlEx != null) {
/*  853 */               throw SQLError.createBatchUpdateException(sqlEx, updateCounts, this.exceptionInterceptor);
/*      */             }
/*  855 */           } catch (NullPointerException npe) {
/*      */             try {
/*  857 */               checkClosed();
/*  858 */             } catch (StatementIsClosedException connectionClosedEx) {
/*  859 */               int batchCommandIndex = ((PreparedQuery)this.query).getBatchCommandIndex();
/*  860 */               updateCounts[batchCommandIndex] = -3L;
/*      */               
/*  862 */               long[] newUpdateCounts = new long[batchCommandIndex];
/*      */               
/*  864 */               System.arraycopy(updateCounts, 0, newUpdateCounts, 0, batchCommandIndex);
/*      */               
/*  866 */               throw SQLError.createBatchUpdateException(SQLExceptionsMapping.translateException(connectionClosedEx), newUpdateCounts, this.exceptionInterceptor);
/*      */             } 
/*      */ 
/*      */             
/*  870 */             throw npe;
/*      */           } finally {
/*  872 */             ((PreparedQuery)this.query).setBatchCommandIndex(-1);
/*      */             
/*  874 */             stopQueryTimer(timeoutTask, false, false);
/*  875 */             resetCancelledState();
/*      */           } 
/*      */         } 
/*      */         
/*  879 */         return (updateCounts != null) ? updateCounts : new long[0]; }  }
/*  880 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected <M extends Message> ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, M sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, ColumnDefinition metadata, boolean isBatch) throws SQLException {
/*      */     try {
/*  911 */       synchronized (checkClosed().getConnectionMutex()) {
/*      */         ResultSetInternalMethods rs;
/*      */         
/*  914 */         JdbcConnection locallyScopedConnection = this.connection;
/*      */         
/*  916 */         ((PreparedQuery)this.query).getQueryBindings()
/*  917 */           .setNumberOfExecutions(((PreparedQuery)this.query).getQueryBindings().getNumberOfExecutions() + 1);
/*      */ 
/*      */ 
/*      */         
/*  921 */         CancelQueryTask timeoutTask = null;
/*      */         
/*      */         try {
/*  924 */           timeoutTask = startQueryTimer(this, getTimeoutInMillis());
/*      */           
/*  926 */           if (!isBatch) {
/*  927 */             statementBegins();
/*      */           }
/*      */           
/*  930 */           rs = (ResultSetInternalMethods)((NativeSession)locallyScopedConnection.getSession()).execSQL(this, null, maxRowsToRetrieve, (NativePacketPayload)sendPacket, createStreamingResultSet, 
/*  931 */               getResultSetFactory(), metadata, isBatch);
/*      */           
/*  933 */           if (timeoutTask != null) {
/*  934 */             stopQueryTimer(timeoutTask, true, true);
/*  935 */             timeoutTask = null;
/*      */           } 
/*      */         } finally {
/*      */           
/*  939 */           if (!isBatch) {
/*  940 */             this.query.getStatementExecuting().set(false);
/*      */           }
/*      */           
/*  943 */           stopQueryTimer(timeoutTask, false, false);
/*      */         } 
/*      */         
/*  946 */         return rs;
/*      */       
/*      */       }
/*      */     
/*      */     }
/*      */     catch (CJException cJException) {
/*      */       
/*  953 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   public ResultSet executeQuery() throws SQLException {
/*      */     
/*  958 */     try { synchronized (checkClosed().getConnectionMutex())
/*      */       
/*  960 */       { JdbcConnection locallyScopedConn = this.connection;
/*      */         
/*  962 */         if (!this.doPingInstead) {
/*  963 */           QueryReturnType queryReturnType = getParseInfo().getQueryReturnType();
/*  964 */           if (queryReturnType != QueryReturnType.PRODUCES_RESULT_SET && queryReturnType != QueryReturnType.MAY_PRODUCE_RESULT_SET) {
/*  965 */             throw SQLError.createSQLException(Messages.getString("Statement.57"), "S1009", 
/*  966 */                 getExceptionInterceptor());
/*      */           }
/*      */         } 
/*      */         
/*  970 */         this.batchedGeneratedKeys = null;
/*      */         
/*  972 */         resetCancelledState();
/*      */         
/*  974 */         implicitlyCloseAllOpenResults();
/*      */         
/*  976 */         clearWarnings();
/*      */         
/*  978 */         if (this.doPingInstead) {
/*  979 */           doPingInstead();
/*      */           
/*  981 */           return (ResultSet)this.results;
/*      */         } 
/*      */         
/*  984 */         setupStreamingTimeout(locallyScopedConn);
/*      */         
/*  986 */         Message sendPacket = ((PreparedQuery)this.query).fillSendPacket();
/*      */         
/*  988 */         String oldDb = null;
/*      */         
/*  990 */         if (!locallyScopedConn.getDatabase().equals(getCurrentDatabase())) {
/*  991 */           oldDb = locallyScopedConn.getDatabase();
/*  992 */           locallyScopedConn.setDatabase(getCurrentDatabase());
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  998 */         CachedResultSetMetaData cachedMetadata = null;
/*  999 */         boolean cacheResultSetMetadata = ((Boolean)locallyScopedConn.getPropertySet().getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()).booleanValue();
/*      */         
/* 1001 */         String origSql = ((PreparedQuery)this.query).getOriginalSql();
/*      */         
/* 1003 */         if (cacheResultSetMetadata) {
/* 1004 */           cachedMetadata = locallyScopedConn.getCachedMetaData(origSql);
/*      */         }
/*      */         
/* 1007 */         locallyScopedConn.setSessionMaxRows(this.maxRows);
/*      */         
/* 1009 */         this.results = executeInternal(this.maxRows, sendPacket, createStreamingResultSet(), true, (ColumnDefinition)cachedMetadata, false);
/*      */         
/* 1011 */         if (oldDb != null) {
/* 1012 */           locallyScopedConn.setDatabase(oldDb);
/*      */         }
/*      */         
/* 1015 */         if (cachedMetadata != null) {
/* 1016 */           locallyScopedConn.initializeResultsMetadataFromCache(origSql, cachedMetadata, this.results);
/*      */         }
/* 1018 */         else if (cacheResultSetMetadata) {
/* 1019 */           locallyScopedConn.initializeResultsMetadataFromCache(origSql, (CachedResultSetMetaData)null, this.results);
/*      */         } 
/*      */ 
/*      */         
/* 1023 */         this.lastInsertId = this.results.getUpdateID();
/*      */         
/* 1025 */         return (ResultSet)this.results; }  }
/* 1026 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int executeUpdate() throws SQLException {
/*      */     
/* 1031 */     try { return Util.truncateAndConvertToInt(executeLargeUpdate()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long executeUpdateInternal(boolean clearBatchedGeneratedKeysAndWarnings, boolean isBatch) throws SQLException {
/*      */     
/* 1040 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1041 */       { if (clearBatchedGeneratedKeysAndWarnings) {
/* 1042 */           clearWarnings();
/* 1043 */           this.batchedGeneratedKeys = null;
/*      */         } 
/*      */         
/* 1046 */         return executeUpdateInternal(((PreparedQuery)this.query).getQueryBindings(), isBatch); }  }
/* 1047 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
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
/*      */   protected long executeUpdateInternal(QueryBindings<?> bindings, boolean isReallyBatch) throws SQLException {
/*      */     
/* 1065 */     try { synchronized (checkClosed().getConnectionMutex())
/*      */       
/* 1067 */       { JdbcConnection locallyScopedConn = this.connection;
/*      */         
/* 1069 */         if (locallyScopedConn.isReadOnly(false)) {
/* 1070 */           throw SQLError.createSQLException(Messages.getString("PreparedStatement.34") + Messages.getString("PreparedStatement.35"), "S1009", this.exceptionInterceptor);
/*      */         }
/*      */ 
/*      */         
/* 1074 */         if (!isNonResultSetProducingQuery()) {
/* 1075 */           throw SQLError.createSQLException(Messages.getString("PreparedStatement.37"), "01S03", this.exceptionInterceptor);
/*      */         }
/*      */         
/* 1078 */         resetCancelledState();
/*      */         
/* 1080 */         implicitlyCloseAllOpenResults();
/*      */         
/* 1082 */         ResultSetInternalMethods rs = null;
/*      */         
/* 1084 */         Message sendPacket = ((PreparedQuery)this.query).fillSendPacket(bindings);
/*      */         
/* 1086 */         String oldDb = null;
/*      */         
/* 1088 */         if (!locallyScopedConn.getDatabase().equals(getCurrentDatabase())) {
/* 1089 */           oldDb = locallyScopedConn.getDatabase();
/* 1090 */           locallyScopedConn.setDatabase(getCurrentDatabase());
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1096 */         locallyScopedConn.setSessionMaxRows(-1);
/*      */         
/* 1098 */         rs = executeInternal(-1, sendPacket, false, false, (ColumnDefinition)null, isReallyBatch);
/*      */         
/* 1100 */         if (this.retrieveGeneratedKeys) {
/* 1101 */           rs.setFirstCharOfQuery(getParseInfo().getFirstStmtChar());
/*      */         }
/*      */         
/* 1104 */         if (oldDb != null) {
/* 1105 */           locallyScopedConn.setDatabase(oldDb);
/*      */         }
/*      */         
/* 1108 */         this.results = rs;
/*      */         
/* 1110 */         this.updateCount = rs.getUpdateCount();
/*      */         
/* 1112 */         if (containsOnDuplicateKeyUpdateInSQL() && this.compensateForOnDuplicateKeyUpdate && (
/* 1113 */           this.updateCount == 2L || this.updateCount == 0L)) {
/* 1114 */           this.updateCount = 1L;
/*      */         }
/*      */ 
/*      */         
/* 1118 */         this.lastInsertId = rs.getUpdateID();
/*      */         
/* 1120 */         return this.updateCount; }  }
/* 1121 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   protected boolean containsOnDuplicateKeyUpdateInSQL() {
/* 1125 */     return getParseInfo().containsOnDuplicateKeyUpdateInSQL();
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
/*      */   protected ClientPreparedStatement prepareBatchedInsertSQL(JdbcConnection localConn, int numBatches) throws SQLException {
/*      */     
/* 1140 */     try { synchronized (checkClosed().getConnectionMutex())
/*      */       
/* 1142 */       { ClientPreparedStatement pstmt = new ClientPreparedStatement(localConn, "Rewritten batch of: " + ((PreparedQuery)this.query).getOriginalSql(), getCurrentDatabase(), getParseInfo().getParseInfoForBatch(numBatches));
/* 1143 */         pstmt.setRetrieveGeneratedKeys(this.retrieveGeneratedKeys);
/* 1144 */         pstmt.rewrittenBatchSize = numBatches;
/*      */         
/* 1146 */         getQueryAttributesBindings().runThroughAll(a -> pstmt.setAttribute(a.getName(), a.getValue()));
/*      */         
/* 1148 */         return pstmt; }  }
/* 1149 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } protected void setRetrieveGeneratedKeys(boolean flag) throws SQLException {
/*      */     
/* 1153 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1154 */         this.retrieveGeneratedKeys = flag;
/*      */       }  return; }
/* 1156 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public byte[] getBytesRepresentation(int parameterIndex) throws SQLException {
/*      */     
/* 1160 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1161 */       { return ((PreparedQuery)this.query).getQueryBindings().getBytesRepresentation(getCoreParameterIndex(parameterIndex)); }  }
/* 1162 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public byte[] getOrigBytes(int parameterIndex) throws SQLException {
/* 1167 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1168 */       return ((PreparedQuery)this.query).getQueryBindings().getOrigBytes(getCoreParameterIndex(parameterIndex));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultSetMetaData getMetaData() throws SQLException {
/*      */     
/* 1175 */     try { synchronized (checkClosed().getConnectionMutex())
/*      */       
/*      */       { 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1183 */         if (!isResultSetProducingQuery()) {
/* 1184 */           return null;
/*      */         }
/*      */         
/* 1187 */         JdbcPreparedStatement mdStmt = null;
/* 1188 */         ResultSet mdRs = null;
/*      */         
/* 1190 */         if (this.pstmtResultMetaData == null) {
/*      */           
/*      */           try {
/* 1193 */             mdStmt = new ClientPreparedStatement(this.connection, ((PreparedQuery)this.query).getOriginalSql(), getCurrentDatabase(), getParseInfo());
/*      */             
/* 1195 */             mdStmt.setMaxRows(1);
/*      */             
/* 1197 */             int paramCount = ((PreparedQuery)this.query).getParameterCount();
/*      */             
/* 1199 */             for (int i = 1; i <= paramCount; i++) {
/* 1200 */               mdStmt.setString(i, (String)null);
/*      */             }
/*      */             
/* 1203 */             boolean hadResults = mdStmt.execute();
/*      */             
/* 1205 */             if (hadResults) {
/* 1206 */               mdRs = mdStmt.getResultSet();
/*      */               
/* 1208 */               this.pstmtResultMetaData = mdRs.getMetaData();
/*      */             } else {
/* 1210 */               this
/*      */                 
/* 1212 */                 .pstmtResultMetaData = (ResultSetMetaData)new ResultSetMetaData((Session)this.session, new com.mysql.cj.result.Field[0], ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.useOldAliasMetadataBehavior).getValue()).booleanValue(), ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.yearIsDateType).getValue()).booleanValue(), this.exceptionInterceptor);
/*      */             } 
/*      */           } finally {
/* 1215 */             SQLException sqlExRethrow = null;
/*      */             
/* 1217 */             if (mdRs != null) {
/*      */               try {
/* 1219 */                 mdRs.close();
/* 1220 */               } catch (SQLException sqlEx) {
/* 1221 */                 sqlExRethrow = sqlEx;
/*      */               } 
/*      */               
/* 1224 */               mdRs = null;
/*      */             } 
/*      */             
/* 1227 */             if (mdStmt != null) {
/*      */               try {
/* 1229 */                 mdStmt.close();
/* 1230 */               } catch (SQLException sqlEx) {
/* 1231 */                 sqlExRethrow = sqlEx;
/*      */               } 
/*      */               
/* 1234 */               mdStmt = null;
/*      */             } 
/*      */             
/* 1237 */             if (sqlExRethrow != null) {
/* 1238 */               throw sqlExRethrow;
/*      */             }
/*      */           } 
/*      */         }
/*      */         
/* 1243 */         return this.pstmtResultMetaData; }  }
/* 1244 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isResultSetProducingQuery() {
/* 1254 */     QueryReturnType queryReturnType = getParseInfo().getQueryReturnType();
/* 1255 */     return (queryReturnType == QueryReturnType.PRODUCES_RESULT_SET || queryReturnType == QueryReturnType.MAY_PRODUCE_RESULT_SET);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isNonResultSetProducingQuery() {
/* 1265 */     QueryReturnType queryReturnType = getParseInfo().getQueryReturnType();
/* 1266 */     return (queryReturnType == QueryReturnType.DOES_NOT_PRODUCE_RESULT_SET || queryReturnType == QueryReturnType.MAY_PRODUCE_RESULT_SET);
/*      */   }
/*      */   
/*      */   public ParameterMetaData getParameterMetaData() throws SQLException {
/*      */     
/* 1271 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1272 */       { if (this.parameterMetaData == null) {
/* 1273 */           if (((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.generateSimpleParameterMetadata).getValue()).booleanValue()) {
/* 1274 */             this.parameterMetaData = new MysqlParameterMetadata(((PreparedQuery)this.query).getParameterCount());
/*      */           } else {
/* 1276 */             this.parameterMetaData = new MysqlParameterMetadata((Session)this.session, null, ((PreparedQuery)this.query).getParameterCount(), this.exceptionInterceptor);
/*      */           } 
/*      */         }
/*      */ 
/*      */         
/* 1281 */         return this.parameterMetaData; }  }
/* 1282 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ParseInfo getParseInfo() {
/* 1287 */     return ((PreparedQuery)this.query).getParseInfo();
/*      */   }
/*      */   
/*      */   private void initializeFromParseInfo() throws SQLException {
/*      */     
/* 1292 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*      */         
/* 1294 */         int parameterCount = (getParseInfo().getStaticSql()).length - 1;
/* 1295 */         ((PreparedQuery)this.query).setParameterCount(parameterCount);
/* 1296 */         ((PreparedQuery)this.query).setQueryBindings((QueryBindings)new ClientPreparedQueryBindings(parameterCount, (Session)this.session));
/* 1297 */         ((ClientPreparedQueryBindings)((ClientPreparedQuery)this.query).getQueryBindings()).setLoadDataQuery(getParseInfo().isLoadData());
/*      */         
/* 1299 */         clearParameters();
/*      */       }  return; }
/* 1301 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public boolean isNull(int paramIndex) throws SQLException {
/*      */     
/* 1305 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1306 */       { return ((PreparedQuery)this.query).getQueryBindings().getBindValues()[getCoreParameterIndex(paramIndex)].isNull(); }  }
/* 1307 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
/* 1312 */     JdbcConnection locallyScopedConn = this.connection;
/*      */     
/* 1314 */     if (locallyScopedConn == null) {
/*      */       return;
/*      */     }
/*      */     
/* 1318 */     synchronized (locallyScopedConn.getConnectionMutex()) {
/*      */ 
/*      */ 
/*      */       
/* 1322 */       if (this.isClosed) {
/*      */         return;
/*      */       }
/*      */       
/* 1326 */       if (this.useUsageAdvisor) {
/* 1327 */         QueryBindings<?> qb = ((PreparedQuery)this.query).getQueryBindings();
/* 1328 */         if (qb == null || qb.getNumberOfExecutions() <= 1) {
/* 1329 */           this.session.getProfilerEventHandler().processEvent((byte)0, (Session)this.session, this, null, 0L, new Throwable(), 
/* 1330 */               Messages.getString("PreparedStatement.43"));
/*      */         }
/*      */       } 
/*      */       
/* 1334 */       super.realClose(calledExplicitly, closeOpenResults);
/*      */       
/* 1336 */       ((PreparedQuery)this.query).setOriginalSql(null);
/* 1337 */       ((PreparedQuery)this.query).setQueryBindings(null);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public String getPreparedSql() {
/* 1343 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1344 */       if (this.rewrittenBatchSize == 0) {
/* 1345 */         return ((PreparedQuery)this.query).getOriginalSql();
/*      */       }
/*      */       
/*      */       try {
/* 1349 */         return getParseInfo().getSqlForBatch();
/* 1350 */       } catch (UnsupportedEncodingException e) {
/* 1351 */         throw new RuntimeException(e);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public int getUpdateCount() throws SQLException {
/*      */     
/* 1358 */     try { int count = super.getUpdateCount();
/*      */       
/* 1360 */       if (containsOnDuplicateKeyUpdateInSQL() && this.compensateForOnDuplicateKeyUpdate && (
/* 1361 */         count == 2 || count == 0)) {
/* 1362 */         count = 1;
/*      */       }
/*      */ 
/*      */       
/* 1366 */       return count; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public long executeLargeUpdate() throws SQLException {
/*      */     
/* 1371 */     try { return executeUpdateInternal(true, false); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public ParameterBindings getParameterBindings() throws SQLException {
/*      */     
/* 1375 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1376 */       { return new ParameterBindingsImpl((PreparedQuery)this.query, (Session)this.session, this.resultSetFactory); }  }
/* 1377 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int getParameterIndexOffset() {
/* 1387 */     return 0;
/*      */   }
/*      */   protected void checkBounds(int paramIndex, int parameterIndexOffset) throws SQLException {
/*      */     try {
/* 1391 */       synchronized (checkClosed().getConnectionMutex()) {
/* 1392 */         if (paramIndex < 1) {
/* 1393 */           throw SQLError.createSQLException(Messages.getString("PreparedStatement.49") + paramIndex + Messages.getString("PreparedStatement.50"), "S1009", this.exceptionInterceptor);
/*      */         }
/* 1395 */         if (paramIndex > ((PreparedQuery)this.query).getParameterCount()) {
/* 1396 */           throw SQLError.createSQLException(
/* 1397 */               Messages.getString("PreparedStatement.51") + paramIndex + Messages.getString("PreparedStatement.52") + ((PreparedQuery)this.query)
/* 1398 */               .getParameterCount() + Messages.getString("PreparedStatement.53"), "S1009", this.exceptionInterceptor);
/*      */         }
/* 1400 */         if (parameterIndexOffset == -1 && paramIndex == 1)
/* 1401 */           throw SQLError.createSQLException(Messages.getString("PreparedStatement.63"), "S1009", this.exceptionInterceptor); 
/*      */       } 
/*      */       return;
/*      */     } catch (CJException cJException) {
/* 1405 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public final int getCoreParameterIndex(int paramIndex) throws SQLException {
/* 1408 */     int parameterIndexOffset = getParameterIndexOffset();
/* 1409 */     checkBounds(paramIndex, parameterIndexOffset);
/* 1410 */     return paramIndex - 1 + parameterIndexOffset;
/*      */   }
/*      */   
/*      */   public void setArray(int i, Array x) throws SQLException {
/*      */     
/* 1415 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
/*      */     
/* 1420 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1421 */         ((PreparedQuery)this.query).getQueryBindings().setAsciiStream(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1423 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
/*      */     
/* 1427 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1428 */         ((PreparedQuery)this.query).getQueryBindings().setAsciiStream(getCoreParameterIndex(parameterIndex), x, length);
/*      */       }  return; }
/* 1430 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
/*      */     
/* 1434 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1435 */         ((PreparedQuery)this.query).getQueryBindings().setAsciiStream(getCoreParameterIndex(parameterIndex), x, length);
/*      */       }  return; }
/* 1437 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
/*      */     
/* 1441 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1442 */         ((PreparedQuery)this.query).getQueryBindings().setBigDecimal(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1444 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
/*      */     
/* 1448 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1449 */         ((PreparedQuery)this.query).getQueryBindings().setBinaryStream(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1451 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
/*      */     
/* 1455 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1456 */         ((PreparedQuery)this.query).getQueryBindings().setBinaryStream(getCoreParameterIndex(parameterIndex), x, length);
/*      */       }  return; }
/* 1458 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
/*      */     
/* 1462 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1463 */         ((PreparedQuery)this.query).getQueryBindings().setBinaryStream(getCoreParameterIndex(parameterIndex), x, length);
/*      */       }  return; }
/* 1465 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBlob(int i, Blob x) throws SQLException {
/*      */     
/* 1469 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1470 */         ((PreparedQuery)this.query).getQueryBindings().setBlob(getCoreParameterIndex(i), x);
/*      */       }  return; }
/* 1472 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
/*      */     
/* 1476 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1477 */         ((PreparedQuery)this.query).getQueryBindings().setBlob(getCoreParameterIndex(parameterIndex), inputStream);
/*      */       }  return; }
/* 1479 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
/*      */     
/* 1483 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1484 */         ((PreparedQuery)this.query).getQueryBindings().setBlob(getCoreParameterIndex(parameterIndex), inputStream, length);
/*      */       }  return; }
/* 1486 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBoolean(int parameterIndex, boolean x) throws SQLException {
/*      */     
/* 1490 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1491 */         ((PreparedQuery)this.query).getQueryBindings().setBoolean(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1493 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setByte(int parameterIndex, byte x) throws SQLException {
/*      */     
/* 1497 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1498 */         ((PreparedQuery)this.query).getQueryBindings().setByte(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1500 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBytes(int parameterIndex, byte[] x) throws SQLException {
/*      */     
/* 1504 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1505 */         ((PreparedQuery)this.query).getQueryBindings().setBytes(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1507 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBytes(int parameterIndex, byte[] x, boolean checkForIntroducer, boolean escapeForMBChars) throws SQLException {
/*      */     
/* 1511 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1512 */         ((PreparedQuery)this.query).getQueryBindings().setBytes(getCoreParameterIndex(parameterIndex), x, checkForIntroducer, escapeForMBChars);
/*      */       }  return; }
/* 1514 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setBytesNoEscape(int parameterIndex, byte[] parameterAsBytes) throws SQLException {
/* 1518 */     ((PreparedQuery)this.query).getQueryBindings().setBytesNoEscape(getCoreParameterIndex(parameterIndex), parameterAsBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBytesNoEscapeNoQuotes(int parameterIndex, byte[] parameterAsBytes) throws SQLException {
/* 1523 */     ((PreparedQuery)this.query).getQueryBindings().setBytesNoEscapeNoQuotes(getCoreParameterIndex(parameterIndex), parameterAsBytes);
/*      */   }
/*      */   
/*      */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
/*      */     
/* 1528 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1529 */         ((PreparedQuery)this.query).getQueryBindings().setCharacterStream(getCoreParameterIndex(parameterIndex), reader);
/*      */       }  return; }
/* 1531 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
/*      */     
/* 1535 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1536 */         ((PreparedQuery)this.query).getQueryBindings().setCharacterStream(getCoreParameterIndex(parameterIndex), reader, length);
/*      */       }  return; }
/* 1538 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/*      */     
/* 1542 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1543 */         ((PreparedQuery)this.query).getQueryBindings().setCharacterStream(getCoreParameterIndex(parameterIndex), reader, length);
/*      */       }  return; }
/* 1545 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setClob(int parameterIndex, Reader reader) throws SQLException {
/*      */     
/* 1549 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1550 */         ((PreparedQuery)this.query).getQueryBindings().setCharacterStream(getCoreParameterIndex(parameterIndex), reader);
/*      */       }  return; }
/* 1552 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
/*      */     
/* 1556 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1557 */         ((PreparedQuery)this.query).getQueryBindings().setCharacterStream(getCoreParameterIndex(parameterIndex), reader, length);
/*      */       }  return; }
/* 1559 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setClob(int i, Clob x) throws SQLException {
/*      */     
/* 1563 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1564 */         ((PreparedQuery)this.query).getQueryBindings().setClob(getCoreParameterIndex(i), x);
/*      */       }  return; }
/* 1566 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setDate(int parameterIndex, Date x) throws SQLException {
/*      */     
/* 1570 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1571 */         ((PreparedQuery)this.query).getQueryBindings().setDate(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1573 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
/*      */     
/* 1577 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1578 */         ((PreparedQuery)this.query).getQueryBindings().setDate(getCoreParameterIndex(parameterIndex), x, cal);
/*      */       }  return; }
/* 1580 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setDouble(int parameterIndex, double x) throws SQLException {
/*      */     
/* 1584 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1585 */         ((PreparedQuery)this.query).getQueryBindings().setDouble(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1587 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setFloat(int parameterIndex, float x) throws SQLException {
/*      */     
/* 1591 */     try { ((PreparedQuery)this.query).getQueryBindings().setFloat(getCoreParameterIndex(parameterIndex), x); return; }
/* 1592 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setInt(int parameterIndex, int x) throws SQLException {
/*      */     
/* 1596 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1597 */         ((PreparedQuery)this.query).getQueryBindings().setInt(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1599 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setLong(int parameterIndex, long x) throws SQLException {
/*      */     
/* 1603 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1604 */         ((PreparedQuery)this.query).getQueryBindings().setLong(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1606 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setBigInteger(int parameterIndex, BigInteger x) throws SQLException {
/* 1610 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1611 */       ((PreparedQuery)this.query).getQueryBindings().setBigInteger(getCoreParameterIndex(parameterIndex), x);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
/*      */     
/* 1617 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1618 */         ((PreparedQuery)this.query).getQueryBindings().setNCharacterStream(getCoreParameterIndex(parameterIndex), value);
/*      */       }  return; }
/* 1620 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/*      */     
/* 1624 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1625 */         ((PreparedQuery)this.query).getQueryBindings().setNCharacterStream(getCoreParameterIndex(parameterIndex), reader, length);
/*      */       }  return; }
/* 1627 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setNClob(int parameterIndex, Reader reader) throws SQLException {
/*      */     
/* 1631 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1632 */         ((PreparedQuery)this.query).getQueryBindings().setNClob(getCoreParameterIndex(parameterIndex), reader);
/*      */       }  return; }
/* 1634 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
/*      */     
/* 1638 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1639 */         ((PreparedQuery)this.query).getQueryBindings().setNClob(getCoreParameterIndex(parameterIndex), reader, length);
/*      */       }  return; }
/* 1641 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setNClob(int parameterIndex, NClob value) throws SQLException {
/*      */     
/* 1645 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1646 */         ((PreparedQuery)this.query).getQueryBindings().setNClob(getCoreParameterIndex(parameterIndex), value);
/*      */       }  return; }
/* 1648 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
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
/*      */   public void setNString(int parameterIndex, String x) throws SQLException {
/*      */     
/* 1666 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1667 */         ((PreparedQuery)this.query).getQueryBindings().setNString(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1669 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setNull(int parameterIndex, int sqlType) throws SQLException {
/*      */     
/* 1673 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1674 */         ((PreparedQuery)this.query).getQueryBindings().setNull(getCoreParameterIndex(parameterIndex));
/*      */       }  return; }
/* 1676 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
/*      */     
/* 1680 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1681 */         ((PreparedQuery)this.query).getQueryBindings().setNull(getCoreParameterIndex(parameterIndex));
/*      */       }  return; }
/* 1683 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setNull(int parameterIndex, MysqlType mysqlType) throws SQLException {
/* 1687 */     setNull(parameterIndex, mysqlType.getJdbcType());
/*      */   }
/*      */   
/*      */   public void setObject(int parameterIndex, Object parameterObj) throws SQLException {
/*      */     
/* 1692 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1693 */         ((PreparedQuery)this.query).getQueryBindings().setObject(getCoreParameterIndex(parameterIndex), parameterObj);
/*      */       }  return; }
/* 1695 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setObject(int parameterIndex, Object parameterObj, int targetSqlType) throws SQLException {
/*      */     try {
/* 1699 */       synchronized (checkClosed().getConnectionMutex()) {
/*      */         try {
/* 1701 */           ((PreparedQuery)this.query).getQueryBindings().setObject(getCoreParameterIndex(parameterIndex), parameterObj, 
/* 1702 */               MysqlType.getByJdbcType(targetSqlType));
/* 1703 */         } catch (FeatureNotAvailableException nae) {
/* 1704 */           throw SQLError.createSQLFeatureNotSupportedException(Messages.getString("Statement.UnsupportedSQLType") + JDBCType.valueOf(targetSqlType), "S1C00", this.exceptionInterceptor);
/*      */         } 
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/* 1708 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setObject(int parameterIndex, Object parameterObj, SQLType targetSqlType) throws SQLException {
/*      */     
/* 1712 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1713 */         if (targetSqlType instanceof MysqlType) {
/* 1714 */           ((PreparedQuery)this.query).getQueryBindings().setObject(getCoreParameterIndex(parameterIndex), parameterObj, (MysqlType)targetSqlType);
/*      */         } else {
/* 1716 */           setObject(parameterIndex, parameterObj, targetSqlType.getVendorTypeNumber().intValue());
/*      */         } 
/*      */       }  return; }
/* 1719 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale) throws SQLException {
/*      */     try {
/* 1723 */       synchronized (checkClosed().getConnectionMutex()) {
/*      */         try {
/* 1725 */           ((PreparedQuery)this.query).getQueryBindings().setObject(getCoreParameterIndex(parameterIndex), parameterObj, 
/* 1726 */               MysqlType.getByJdbcType(targetSqlType), scale);
/* 1727 */         } catch (FeatureNotAvailableException nae) {
/* 1728 */           throw SQLError.createSQLFeatureNotSupportedException(Messages.getString("Statement.UnsupportedSQLType") + JDBCType.valueOf(targetSqlType), "S1C00", this.exceptionInterceptor);
/*      */         } 
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/* 1732 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
/*      */     
/* 1736 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1737 */         if (targetSqlType instanceof MysqlType) {
/* 1738 */           ((PreparedQuery)this.query).getQueryBindings().setObject(getCoreParameterIndex(parameterIndex), x, (MysqlType)targetSqlType, scaleOrLength);
/*      */         } else {
/*      */           
/* 1741 */           setObject(parameterIndex, x, targetSqlType.getVendorTypeNumber().intValue(), scaleOrLength);
/*      */         } 
/*      */       }  return; }
/* 1744 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setRef(int i, Ref x) throws SQLException {
/*      */     
/* 1748 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/*      */     
/* 1753 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setShort(int parameterIndex, short x) throws SQLException {
/*      */     
/* 1758 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1759 */         ((PreparedQuery)this.query).getQueryBindings().setShort(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1761 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
/*      */     
/* 1765 */     try { if (xmlObject == null) {
/* 1766 */         setNull(parameterIndex, MysqlType.VARCHAR);
/*      */       } else {
/*      */         
/* 1769 */         setCharacterStream(parameterIndex, ((MysqlSQLXML)xmlObject).serializeAsCharacterStream());
/*      */       }  return; }
/* 1771 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setString(int parameterIndex, String x) throws SQLException {
/*      */     
/* 1775 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1776 */         ((PreparedQuery)this.query).getQueryBindings().setString(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1778 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setTime(int parameterIndex, Time x) throws SQLException {
/*      */     
/* 1782 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1783 */         ((PreparedQuery)this.query).getQueryBindings().setTime(getCoreParameterIndex(parameterIndex), x);
/*      */       }  return; }
/* 1785 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
/*      */     
/* 1789 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1790 */         ((PreparedQuery)this.query).getQueryBindings().setTime(getCoreParameterIndex(parameterIndex), x, cal);
/*      */       }  return; }
/* 1792 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
/*      */     
/* 1796 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1797 */         ((PreparedQuery)this.query).getQueryBindings().setTimestamp(getCoreParameterIndex(parameterIndex), x, MysqlType.TIMESTAMP);
/*      */       }  return; }
/* 1799 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
/*      */     
/* 1803 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1804 */         ((PreparedQuery)this.query).getQueryBindings().setTimestamp(getCoreParameterIndex(parameterIndex), x, cal, MysqlType.TIMESTAMP);
/*      */       }  return; }
/* 1806 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setTimestamp(int parameterIndex, Timestamp x, Calendar targetCalendar, int fractionalLength) throws SQLException {
/* 1809 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1810 */       ((PreparedQuery)this.query).getQueryBindings().setTimestamp(getCoreParameterIndex(parameterIndex), x, targetCalendar, fractionalLength, MysqlType.TIMESTAMP);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
/*      */     
/* 1818 */     try { setBinaryStream(parameterIndex, x, length);
/* 1819 */       ((PreparedQuery)this.query).getQueryBindings().getBindValues()[getCoreParameterIndex(parameterIndex)].setMysqlType(MysqlType.TEXT); return; }
/* 1820 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setURL(int parameterIndex, URL arg) throws SQLException {
/*      */     
/* 1824 */     try { if (arg == null) {
/* 1825 */         setNull(parameterIndex, MysqlType.VARCHAR);
/*      */       } else {
/* 1827 */         setString(parameterIndex, arg.toString());
/* 1828 */         ((PreparedQuery)this.query).getQueryBindings().getBindValues()[getCoreParameterIndex(parameterIndex)].setMysqlType(MysqlType.VARCHAR);
/*      */       }  return; }
/* 1830 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ClientPreparedStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */