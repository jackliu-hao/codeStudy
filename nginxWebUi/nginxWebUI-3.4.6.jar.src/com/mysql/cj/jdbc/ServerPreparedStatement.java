/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.BindValue;
/*     */ import com.mysql.cj.CancelQueryTask;
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.MysqlType;
/*     */ import com.mysql.cj.NativeSession;
/*     */ import com.mysql.cj.ParseInfo;
/*     */ import com.mysql.cj.PreparedQuery;
/*     */ import com.mysql.cj.Query;
/*     */ import com.mysql.cj.QueryAttributesBindValue;
/*     */ import com.mysql.cj.QueryBindings;
/*     */ import com.mysql.cj.ServerPreparedQuery;
/*     */ import com.mysql.cj.ServerPreparedQueryBindValue;
/*     */ import com.mysql.cj.ServerPreparedQueryBindings;
/*     */ import com.mysql.cj.Session;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*     */ import com.mysql.cj.jdbc.result.ResultSetMetaData;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.result.Row;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.net.URL;
/*     */ import java.sql.Blob;
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLType;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServerPreparedStatement
/*     */   extends ClientPreparedStatement
/*     */ {
/*     */   private boolean hasOnDuplicateKeyUpdate = false;
/*     */   private boolean invalid = false;
/*     */   private CJException invalidationException;
/*     */   protected boolean isCacheable = false;
/*     */   protected boolean isCached = false;
/*     */   
/*     */   protected static ServerPreparedStatement getInstance(JdbcConnection conn, String sql, String db, int resultSetType, int resultSetConcurrency) throws SQLException {
/* 103 */     return new ServerPreparedStatement(conn, sql, db, resultSetType, resultSetConcurrency);
/*     */   }
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
/*     */   protected ServerPreparedStatement(JdbcConnection conn, String sql, String db, int resultSetType, int resultSetConcurrency) throws SQLException {
/* 124 */     super(conn, db);
/*     */     
/* 126 */     checkNullOrEmptyQuery(sql);
/* 127 */     String statementComment = this.session.getProtocol().getQueryComment();
/* 128 */     ((PreparedQuery)this.query).setOriginalSql((statementComment == null) ? sql : ("/* " + statementComment + " */ " + sql));
/* 129 */     ((PreparedQuery)this.query).setParseInfo(new ParseInfo(((PreparedQuery)this.query).getOriginalSql(), (Session)this.session, this.charEncoding));
/*     */     
/* 131 */     this.hasOnDuplicateKeyUpdate = (((PreparedQuery)this.query).getParseInfo().getFirstStmtChar() == 'I' && containsOnDuplicateKeyInString(sql));
/*     */     
/*     */     try {
/* 134 */       serverPrepare(sql);
/* 135 */     } catch (CJException|SQLException sqlEx) {
/* 136 */       realClose(false, true);
/*     */       
/* 138 */       throw SQLExceptionsMapping.translateException(sqlEx, this.exceptionInterceptor);
/*     */     } 
/*     */     
/* 141 */     setResultSetType(resultSetType);
/* 142 */     setResultSetConcurrency(resultSetConcurrency);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void initQuery() {
/* 147 */     this.query = (Query)ServerPreparedQuery.getInstance(this.session);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 152 */     StringBuilder toStringBuf = new StringBuilder();
/*     */     
/* 154 */     toStringBuf.append(getClass().getName() + "[");
/* 155 */     toStringBuf.append(((ServerPreparedQuery)this.query).getServerStatementId());
/* 156 */     toStringBuf.append("]: ");
/*     */     
/*     */     try {
/* 159 */       toStringBuf.append(asSql());
/* 160 */     } catch (SQLException sqlEx) {
/* 161 */       toStringBuf.append(Messages.getString("ServerPreparedStatement.6"));
/* 162 */       toStringBuf.append(sqlEx);
/*     */     } 
/*     */     
/* 165 */     return toStringBuf.toString();
/*     */   }
/*     */   
/*     */   public void addBatch() throws SQLException {
/*     */     
/* 170 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 171 */         this.query.addBatch(((PreparedQuery)this.query).getQueryBindings().clone());
/*     */       }  return; }
/* 173 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public String asSql(boolean quoteStreamsAndUnknowns) throws SQLException {
/* 177 */     synchronized (checkClosed().getConnectionMutex()) {
/* 178 */       ClientPreparedStatement pStmtForSub = null;
/*     */       
/*     */       try {
/* 181 */         pStmtForSub = ClientPreparedStatement.getInstance(this.connection, ((PreparedQuery)this.query).getOriginalSql(), getCurrentDatabase());
/*     */         
/* 183 */         int numParameters = ((PreparedQuery)pStmtForSub.query).getParameterCount();
/* 184 */         int ourNumParameters = ((PreparedQuery)this.query).getParameterCount();
/*     */         
/* 186 */         ServerPreparedQueryBindValue[] parameterBindings = (ServerPreparedQueryBindValue[])((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).getBindValues();
/*     */         
/* 188 */         for (int i = 0; i < numParameters && i < ourNumParameters; i++) {
/* 189 */           if (parameterBindings[i] != null) {
/* 190 */             if (parameterBindings[i].isNull()) {
/* 191 */               pStmtForSub.setNull(i + 1, MysqlType.NULL);
/*     */             } else {
/* 193 */               ServerPreparedQueryBindValue bindValue = parameterBindings[i];
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 198 */               switch (bindValue.bufferType) {
/*     */                 
/*     */                 case 1:
/* 201 */                   pStmtForSub.setByte(i + 1, ((Long)bindValue.value).byteValue());
/*     */                   break;
/*     */                 case 2:
/* 204 */                   pStmtForSub.setShort(i + 1, ((Long)bindValue.value).shortValue());
/*     */                   break;
/*     */                 case 3:
/* 207 */                   pStmtForSub.setInt(i + 1, ((Long)bindValue.value).intValue());
/*     */                   break;
/*     */                 case 8:
/* 210 */                   pStmtForSub.setLong(i + 1, ((Long)bindValue.value).longValue());
/*     */                   break;
/*     */                 case 4:
/* 213 */                   pStmtForSub.setFloat(i + 1, ((Float)bindValue.value).floatValue());
/*     */                   break;
/*     */                 case 5:
/* 216 */                   pStmtForSub.setDouble(i + 1, ((Double)bindValue.value).doubleValue());
/*     */                   break;
/*     */                 default:
/* 219 */                   pStmtForSub.setObject(i + 1, (parameterBindings[i]).value);
/*     */                   break;
/*     */               } 
/*     */             
/*     */             } 
/*     */           }
/*     */         } 
/* 226 */         return pStmtForSub.asSql(quoteStreamsAndUnknowns);
/*     */       } finally {
/* 228 */         if (pStmtForSub != null) {
/*     */           try {
/* 230 */             pStmtForSub.close();
/* 231 */           } catch (SQLException sQLException) {}
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JdbcConnection checkClosed() {
/* 241 */     if (this.invalid) {
/* 242 */       throw this.invalidationException;
/*     */     }
/*     */     
/* 245 */     return super.checkClosed();
/*     */   }
/*     */   
/*     */   public void clearParameters() {
/*     */     
/* 250 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 251 */         ((ServerPreparedQuery)this.query).clearParameters(true);
/*     */       }  return; }
/* 253 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } protected void setClosed(boolean flag) {
/* 256 */     this.isClosed = flag;
/*     */   }
/*     */   
/*     */   public void close() throws SQLException {
/*     */     
/* 261 */     try { JdbcConnection locallyScopedConn = this.connection;
/*     */       
/* 263 */       if (locallyScopedConn == null) {
/*     */         return;
/*     */       }
/*     */       
/* 267 */       synchronized (locallyScopedConn.getConnectionMutex()) {
/* 268 */         if (this.isClosed) {
/*     */           return;
/*     */         }
/*     */         
/* 272 */         if (this.isCacheable && isPoolable()) {
/* 273 */           clearParameters();
/* 274 */           clearAttributes();
/*     */           
/* 276 */           this.isClosed = true;
/*     */           
/* 278 */           this.connection.recachePreparedStatement(this);
/* 279 */           this.isCached = true;
/*     */           
/*     */           return;
/*     */         } 
/* 283 */         this.isClosed = false;
/* 284 */         realClose(true, true);
/*     */       }  return; }
/* 286 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   protected long[] executeBatchSerially(int batchTimeout) throws SQLException {
/* 290 */     synchronized (checkClosed().getConnectionMutex()) {
/* 291 */       JdbcConnection locallyScopedConn = this.connection;
/*     */       
/* 293 */       if (locallyScopedConn.isReadOnly()) {
/* 294 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.2") + Messages.getString("ServerPreparedStatement.3"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */ 
/*     */       
/* 298 */       clearWarnings();
/*     */ 
/*     */ 
/*     */       
/* 302 */       ServerPreparedQueryBindValue[] oldBindValues = (ServerPreparedQueryBindValue[])((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).getBindValues();
/*     */       
/*     */       try {
/* 305 */         long[] updateCounts = null;
/*     */         
/* 307 */         if (this.query.getBatchedArgs() != null) {
/* 308 */           int nbrCommands = this.query.getBatchedArgs().size();
/* 309 */           updateCounts = new long[nbrCommands];
/*     */           
/* 311 */           if (this.retrieveGeneratedKeys) {
/* 312 */             this.batchedGeneratedKeys = new ArrayList<>(nbrCommands);
/*     */           }
/*     */           
/* 315 */           for (int i = 0; i < nbrCommands; i++) {
/* 316 */             updateCounts[i] = -3L;
/*     */           }
/*     */           
/* 319 */           SQLException sqlEx = null;
/*     */           
/* 321 */           int commandIndex = 0;
/*     */           
/* 323 */           ServerPreparedQueryBindValue[] previousBindValuesForBatch = null;
/*     */           
/* 325 */           CancelQueryTask timeoutTask = null;
/*     */           
/*     */           try {
/* 328 */             timeoutTask = startQueryTimer(this, batchTimeout);
/*     */             
/* 330 */             for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/* 331 */               Object arg = this.query.getBatchedArgs().get(commandIndex);
/*     */               
/*     */               try {
/* 334 */                 if (arg instanceof String) {
/* 335 */                   updateCounts[commandIndex] = executeUpdateInternal((String)arg, true, this.retrieveGeneratedKeys);
/*     */ 
/*     */                   
/* 338 */                   getBatchedGeneratedKeys((this.results.getFirstCharOfQuery() == 'I' && containsOnDuplicateKeyInString((String)arg)) ? 1 : 0);
/*     */                 } else {
/* 340 */                   ((ServerPreparedQuery)this.query).setQueryBindings((QueryBindings)arg);
/* 341 */                   ServerPreparedQueryBindValue[] parameterBindings = (ServerPreparedQueryBindValue[])((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).getBindValues();
/*     */ 
/*     */ 
/*     */                   
/* 345 */                   if (previousBindValuesForBatch != null) {
/* 346 */                     for (int j = 0; j < parameterBindings.length; j++) {
/* 347 */                       if ((parameterBindings[j]).bufferType != (previousBindValuesForBatch[j]).bufferType) {
/* 348 */                         ((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).getSendTypesToServer().set(true);
/*     */                         
/*     */                         break;
/*     */                       } 
/*     */                     } 
/*     */                   }
/*     */                   
/*     */                   try {
/* 356 */                     updateCounts[commandIndex] = executeUpdateInternal(false, true);
/*     */                   } finally {
/* 358 */                     previousBindValuesForBatch = parameterBindings;
/*     */                   } 
/*     */ 
/*     */                   
/* 362 */                   getBatchedGeneratedKeys(containsOnDuplicateKeyUpdateInSQL() ? 1 : 0);
/*     */                 } 
/* 364 */               } catch (SQLException ex) {
/* 365 */                 updateCounts[commandIndex] = -3L;
/*     */                 
/* 367 */                 if (this.continueBatchOnError && !(ex instanceof com.mysql.cj.jdbc.exceptions.MySQLTimeoutException) && !(ex instanceof com.mysql.cj.jdbc.exceptions.MySQLStatementCancelledException) && 
/* 368 */                   !hasDeadlockOrTimeoutRolledBackTx(ex)) {
/* 369 */                   sqlEx = ex;
/*     */                 } else {
/* 371 */                   long[] newUpdateCounts = new long[commandIndex];
/* 372 */                   System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
/*     */                   
/* 374 */                   throw SQLError.createBatchUpdateException(ex, newUpdateCounts, this.exceptionInterceptor);
/*     */                 } 
/*     */               } 
/*     */             } 
/*     */           } finally {
/* 379 */             stopQueryTimer(timeoutTask, false, false);
/* 380 */             resetCancelledState();
/*     */           } 
/*     */           
/* 383 */           if (sqlEx != null) {
/* 384 */             throw SQLError.createBatchUpdateException(sqlEx, updateCounts, this.exceptionInterceptor);
/*     */           }
/*     */         } 
/*     */         
/* 388 */         return (updateCounts != null) ? updateCounts : new long[0];
/*     */       } finally {
/* 390 */         ((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).setBindValues((BindValue[])oldBindValues);
/* 391 */         ((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).getSendTypesToServer().set(true);
/*     */         
/* 393 */         clearBatch();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static SQLException appendMessageToException(SQLException sqlEx, String messageToAppend, ExceptionInterceptor interceptor) {
/* 399 */     String sqlState = sqlEx.getSQLState();
/* 400 */     int vendorErrorCode = sqlEx.getErrorCode();
/*     */     
/* 402 */     SQLException sqlExceptionWithNewMessage = SQLError.createSQLException(sqlEx.getMessage() + messageToAppend, sqlState, vendorErrorCode, interceptor);
/* 403 */     sqlExceptionWithNewMessage.setStackTrace(sqlEx.getStackTrace());
/*     */     
/* 405 */     return sqlExceptionWithNewMessage;
/*     */   }
/*     */ 
/*     */   
/*     */   protected <M extends com.mysql.cj.protocol.Message> ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, M sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, ColumnDefinition metadata, boolean isBatch) throws SQLException {
/*     */     
/* 411 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 412 */         ((PreparedQuery)this.query).getQueryBindings()
/* 413 */           .setNumberOfExecutions(((PreparedQuery)this.query).getQueryBindings().getNumberOfExecutions() + 1);
/*     */ 
/*     */ 
/*     */         
/* 417 */         try { return serverExecute(maxRowsToRetrieve, createStreamingResultSet, metadata); }
/* 418 */         catch (SQLException sqlEx)
/*     */         
/* 420 */         { if (((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.enablePacketDebug).getValue()).booleanValue()) {
/* 421 */             this.session.dumpPacketRingBuffer();
/*     */           }
/*     */           
/* 424 */           if (((Boolean)this.dumpQueriesOnException.getValue()).booleanValue()) {
/* 425 */             String extractedSql = toString();
/* 426 */             StringBuilder messageBuf = new StringBuilder(extractedSql.length() + 32);
/* 427 */             messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
/* 428 */             messageBuf.append(extractedSql);
/* 429 */             messageBuf.append("\n\n");
/*     */             
/* 431 */             sqlEx = appendMessageToException(sqlEx, messageBuf.toString(), this.exceptionInterceptor);
/*     */           } 
/*     */           
/* 434 */           throw sqlEx; }
/* 435 */         catch (Exception ex)
/* 436 */         { if (((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.enablePacketDebug).getValue()).booleanValue()) {
/* 437 */             this.session.dumpPacketRingBuffer();
/*     */           }
/*     */           
/* 440 */           SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1000", ex, this.exceptionInterceptor);
/*     */           
/* 442 */           if (((Boolean)this.dumpQueriesOnException.getValue()).booleanValue()) {
/* 443 */             String extractedSql = toString();
/* 444 */             StringBuilder messageBuf = new StringBuilder(extractedSql.length() + 32);
/* 445 */             messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
/* 446 */             messageBuf.append(extractedSql);
/* 447 */             messageBuf.append("\n\n");
/*     */             
/* 449 */             sqlEx = appendMessageToException(sqlEx, messageBuf.toString(), this.exceptionInterceptor);
/*     */           } 
/*     */           
/* 452 */           throw sqlEx; } 
/*     */       }  }
/* 454 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
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
/*     */   protected ServerPreparedQueryBindValue getBinding(int parameterIndex, boolean forLongData) throws SQLException {
/*     */     
/* 470 */     try { synchronized (checkClosed().getConnectionMutex())
/* 471 */       { int i = getCoreParameterIndex(parameterIndex);
/* 472 */         return ((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).getBinding(i, forLongData); }  }
/* 473 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public ResultSetMetaData getMetaData() throws SQLException {
/*     */     
/* 478 */     try { synchronized (checkClosed().getConnectionMutex())
/* 479 */       { ColumnDefinition resultFields = ((ServerPreparedQuery)this.query).getResultFields();
/*     */         
/* 481 */         return (resultFields == null || resultFields.getFields() == null) ? null : (ResultSetMetaData)new ResultSetMetaData((Session)this.session, resultFields
/* 482 */             .getFields(), ((Boolean)this.session
/* 483 */             .getPropertySet().getBooleanProperty(PropertyKey.useOldAliasMetadataBehavior).getValue()).booleanValue(), ((Boolean)this.session
/* 484 */             .getPropertySet().getBooleanProperty(PropertyKey.yearIsDateType).getValue()).booleanValue(), this.exceptionInterceptor); }  }
/* 485 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public ParameterMetaData getParameterMetaData() throws SQLException {
/*     */     
/* 490 */     try { synchronized (checkClosed().getConnectionMutex())
/* 491 */       { if (this.parameterMetaData == null) {
/* 492 */           this
/* 493 */             .parameterMetaData = new MysqlParameterMetadata((Session)this.session, ((ServerPreparedQuery)this.query).getParameterFields(), ((PreparedQuery)this.query).getParameterCount(), this.exceptionInterceptor);
/*     */         }
/*     */         
/* 496 */         return this.parameterMetaData; }  }
/* 497 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public boolean isNull(int paramIndex) {
/* 502 */     throw new IllegalArgumentException(Messages.getString("ServerPreparedStatement.7"));
/*     */   }
/*     */   
/*     */   public void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
/*     */     try {
/* 507 */       JdbcConnection locallyScopedConn = this.connection;
/* 508 */       if (locallyScopedConn == null) {
/*     */         return;
/*     */       }
/*     */       
/* 512 */       synchronized (locallyScopedConn.getConnectionMutex()) {
/* 513 */         if (this.connection != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 521 */           CJException exceptionDuringClose = null;
/*     */           
/* 523 */           if (this.isCached) {
/* 524 */             locallyScopedConn.decachePreparedStatement(this);
/* 525 */             this.isCached = false;
/*     */           } 
/*     */           
/* 528 */           super.realClose(calledExplicitly, closeOpenResults);
/*     */           
/* 530 */           ((ServerPreparedQuery)this.query).clearParameters(false);
/*     */ 
/*     */           
/* 533 */           if (calledExplicitly && !locallyScopedConn.isClosed()) {
/* 534 */             synchronized (locallyScopedConn.getConnectionMutex()) {
/*     */               try {
/* 536 */                 ((NativeSession)locallyScopedConn.getSession()).sendCommand(this.commandBuilder
/* 537 */                     .buildComStmtClose(null, ((ServerPreparedQuery)this.query).getServerStatementId()), true, 0);
/* 538 */               } catch (CJException sqlEx) {
/* 539 */                 exceptionDuringClose = sqlEx;
/*     */               } 
/*     */             } 
/*     */           }
/*     */           
/* 544 */           if (exceptionDuringClose != null)
/* 545 */             throw exceptionDuringClose; 
/*     */         } 
/*     */       }  return;
/*     */     } catch (CJException cJException) {
/* 549 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void rePrepare() {
/* 559 */     synchronized (checkClosed().getConnectionMutex()) {
/* 560 */       this.invalidationException = null;
/*     */       
/*     */       try {
/* 563 */         serverPrepare(((PreparedQuery)this.query).getOriginalSql());
/* 564 */       } catch (Exception ex) {
/* 565 */         this.invalidationException = ExceptionFactory.createException(ex.getMessage(), ex);
/*     */       } 
/*     */       
/* 568 */       if (this.invalidationException != null) {
/* 569 */         this.invalid = true;
/*     */         
/* 571 */         this.query.closeQuery();
/*     */         
/* 573 */         if (this.results != null) {
/*     */           try {
/* 575 */             this.results.close();
/* 576 */           } catch (Exception exception) {}
/*     */         }
/*     */ 
/*     */         
/* 580 */         if (this.generatedKeysResults != null) {
/*     */           try {
/* 582 */             this.generatedKeysResults.close();
/* 583 */           } catch (Exception exception) {}
/*     */         }
/*     */ 
/*     */         
/*     */         try {
/* 588 */           closeAllOpenResults();
/* 589 */         } catch (Exception exception) {}
/*     */ 
/*     */         
/* 592 */         if (this.connection != null && !((Boolean)this.dontTrackOpenResources.getValue()).booleanValue()) {
/* 593 */           this.connection.unregisterStatement(this);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
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
/*     */   protected ResultSetInternalMethods serverExecute(int maxRowsToRetrieve, boolean createStreamingResultSet, ColumnDefinition metadata) throws SQLException {
/*     */     
/* 630 */     try { synchronized (checkClosed().getConnectionMutex())
/* 631 */       { this.results = (ResultSetInternalMethods)((ServerPreparedQuery)this.query).serverExecute(maxRowsToRetrieve, createStreamingResultSet, metadata, (ProtocolEntityFactory)this.resultSetFactory);
/* 632 */         return this.results; }  }
/* 633 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } protected void serverPrepare(String sql) throws SQLException {
/*     */     try {
/* 637 */       synchronized (checkClosed().getConnectionMutex()) {
/* 638 */         SQLException t = null;
/*     */ 
/*     */         
/*     */         try {
/* 642 */           ServerPreparedQuery q = (ServerPreparedQuery)this.query;
/* 643 */           q.serverPrepare(sql);
/* 644 */         } catch (IOException ioEx) {
/* 645 */           t = SQLError.createCommunicationsException(this.connection, this.session.getProtocol().getPacketSentTimeHolder(), this.session
/* 646 */               .getProtocol().getPacketReceivedTimeHolder(), ioEx, this.exceptionInterceptor);
/* 647 */         } catch (CJException sqlEx) {
/* 648 */           SQLException ex = SQLExceptionsMapping.translateException((Throwable)sqlEx);
/*     */           
/* 650 */           if (((Boolean)this.dumpQueriesOnException.getValue()).booleanValue()) {
/* 651 */             StringBuilder messageBuf = new StringBuilder(((PreparedQuery)this.query).getOriginalSql().length() + 32);
/* 652 */             messageBuf.append("\n\nQuery being prepared when exception was thrown:\n\n");
/* 653 */             messageBuf.append(((PreparedQuery)this.query).getOriginalSql());
/*     */             
/* 655 */             ex = appendMessageToException(ex, messageBuf.toString(), this.exceptionInterceptor);
/*     */           } 
/*     */           
/* 658 */           t = ex;
/*     */         } finally {
/*     */           
/*     */           try {
/* 662 */             this.session.clearInputStream();
/* 663 */           } catch (Exception e) {
/* 664 */             if (t == null) {
/* 665 */               t = SQLError.createCommunicationsException(this.connection, this.session.getProtocol().getPacketSentTimeHolder(), this.session
/* 666 */                   .getProtocol().getPacketReceivedTimeHolder(), e, this.exceptionInterceptor);
/*     */             }
/*     */           } 
/* 669 */           if (t != null)
/* 670 */             throw t; 
/*     */         } 
/*     */       }  return;
/*     */     } catch (CJException cJException) {
/* 674 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*     */     } 
/*     */   }
/*     */   protected void checkBounds(int parameterIndex, int parameterIndexOffset) throws SQLException {
/* 678 */     int paramCount = ((PreparedQuery)this.query).getParameterCount();
/* 679 */     if (paramCount == 0) {
/* 680 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ServerPreparedStatement.8"), this.session
/* 681 */           .getExceptionInterceptor());
/*     */     }
/*     */     
/* 684 */     if (parameterIndex < 0 || parameterIndex > paramCount) {
/* 685 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 686 */           Messages.getString("ServerPreparedStatement.9") + (parameterIndex + 1) + Messages.getString("ServerPreparedStatement.10") + paramCount, this.session
/* 687 */           .getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
/*     */     
/* 694 */     try { checkClosed();
/*     */       
/* 696 */       throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void setURL(int parameterIndex, URL x) throws SQLException {
/*     */     
/* 701 */     try { checkClosed();
/*     */       
/* 703 */       setString(parameterIndex, x.toString()); return; }
/* 704 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public long getServerStatementId() {
/* 708 */     return ((ServerPreparedQuery)this.query).getServerStatementId();
/*     */   }
/*     */ 
/*     */   
/*     */   protected int setOneBatchedParameterSet(PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet) throws SQLException {
/* 713 */     ServerPreparedQueryBindValue[] paramArg = (ServerPreparedQueryBindValue[])((ServerPreparedQueryBindings)paramSet).getBindValues();
/*     */     
/* 715 */     for (int j = 0; j < paramArg.length; j++) {
/* 716 */       if (paramArg[j].isNull()) {
/* 717 */         batchedStatement.setNull(batchedParamIndex++, MysqlType.NULL.getJdbcType());
/*     */       }
/* 719 */       else if (paramArg[j].isStream()) {
/* 720 */         Object value = (paramArg[j]).value;
/*     */         
/* 722 */         if (value instanceof byte[]) {
/* 723 */           batchedStatement.setBytes(batchedParamIndex++, (byte[])value);
/* 724 */         } else if (value instanceof InputStream) {
/* 725 */           batchedStatement.setBinaryStream(batchedParamIndex++, (InputStream)value, paramArg[j].getStreamLength());
/* 726 */         } else if (value instanceof Blob) {
/*     */           try {
/* 728 */             batchedStatement.setBinaryStream(batchedParamIndex++, ((Blob)value).getBinaryStream(), paramArg[j].getStreamLength());
/* 729 */           } catch (Throwable t) {
/* 730 */             throw ExceptionFactory.createException(t.getMessage(), this.session.getExceptionInterceptor());
/*     */           } 
/* 732 */         } else if (value instanceof Reader) {
/* 733 */           batchedStatement.setCharacterStream(batchedParamIndex++, (Reader)value, paramArg[j].getStreamLength());
/*     */         } else {
/* 735 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 736 */               Messages.getString("ServerPreparedStatement.18") + value.getClass().getName() + "'", this.session.getExceptionInterceptor());
/*     */         } 
/*     */       } else {
/* 739 */         Object value; switch ((paramArg[j]).bufferType) {
/*     */           case 1:
/* 741 */             batchedStatement.setByte(batchedParamIndex++, ((Long)(paramArg[j]).value).byteValue());
/*     */             break;
/*     */           case 2:
/* 744 */             batchedStatement.setShort(batchedParamIndex++, ((Long)(paramArg[j]).value).shortValue());
/*     */             break;
/*     */           case 3:
/* 747 */             batchedStatement.setInt(batchedParamIndex++, ((Long)(paramArg[j]).value).intValue());
/*     */             break;
/*     */           case 8:
/* 750 */             batchedStatement.setLong(batchedParamIndex++, ((Long)(paramArg[j]).value).longValue());
/*     */             break;
/*     */           case 4:
/* 753 */             batchedStatement.setFloat(batchedParamIndex++, ((Float)(paramArg[j]).value).floatValue());
/*     */             break;
/*     */           case 5:
/* 756 */             batchedStatement.setDouble(batchedParamIndex++, ((Double)(paramArg[j]).value).doubleValue());
/*     */             break;
/*     */           case 11:
/* 759 */             batchedStatement.setTime(batchedParamIndex++, (Time)(paramArg[j]).value);
/*     */             break;
/*     */           case 10:
/* 762 */             batchedStatement.setObject(batchedParamIndex++, (paramArg[j]).value, (SQLType)MysqlType.DATE);
/*     */             break;
/*     */           case 12:
/* 765 */             batchedStatement.setObject(batchedParamIndex++, (paramArg[j]).value);
/*     */             break;
/*     */           case 7:
/* 768 */             batchedStatement.setTimestamp(batchedParamIndex++, (Timestamp)(paramArg[j]).value);
/*     */             break;
/*     */           case 0:
/*     */           case 15:
/*     */           case 246:
/*     */           case 253:
/*     */           case 254:
/* 775 */             value = (paramArg[j]).value;
/*     */             
/* 777 */             if (value instanceof byte[]) {
/* 778 */               batchedStatement.setBytes(batchedParamIndex, (byte[])value);
/*     */             } else {
/* 780 */               batchedStatement.setString(batchedParamIndex, (String)value);
/*     */             } 
/*     */ 
/*     */ 
/*     */             
/* 785 */             if (batchedStatement instanceof ServerPreparedStatement) {
/* 786 */               ServerPreparedQueryBindValue asBound = ((ServerPreparedStatement)batchedStatement).getBinding(batchedParamIndex, false);
/* 787 */               asBound.bufferType = (paramArg[j]).bufferType;
/*     */             } 
/*     */             
/* 790 */             batchedParamIndex++;
/*     */             break;
/*     */           
/*     */           default:
/* 794 */             throw new IllegalArgumentException(Messages.getString("ServerPreparedStatement.26", new Object[] { Integer.valueOf(batchedParamIndex) }));
/*     */         } 
/*     */ 
/*     */       
/*     */       } 
/*     */     } 
/* 800 */     return batchedParamIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean containsOnDuplicateKeyUpdateInSQL() {
/* 805 */     return this.hasOnDuplicateKeyUpdate;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClientPreparedStatement prepareBatchedInsertSQL(JdbcConnection localConn, int numBatches) throws SQLException {
/* 810 */     synchronized (checkClosed().getConnectionMutex()) {
/*     */ 
/*     */       
/* 813 */       ClientPreparedStatement pstmt = localConn.prepareStatement(((PreparedQuery)this.query).getParseInfo().getSqlForBatch(numBatches), this.resultSetConcurrency, this.query.getResultType().getIntValue()).<ClientPreparedStatement>unwrap(ClientPreparedStatement.class);
/* 814 */       pstmt.setRetrieveGeneratedKeys(this.retrieveGeneratedKeys);
/*     */       
/* 816 */       getQueryAttributesBindings().runThroughAll(a -> pstmt.setAttribute(a.getName(), a.getValue()));
/*     */       
/* 818 */       return pstmt;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPoolable(boolean poolable) throws SQLException {
/*     */     
/* 831 */     try { super.setPoolable(poolable);
/*     */       
/* 833 */       if (!poolable && this.isCached) {
/* 834 */         this.connection.decachePreparedStatement(this);
/* 835 */         this.isCached = false;
/*     */         
/* 837 */         if (this.isClosed) {
/* 838 */           this.isClosed = false;
/* 839 */           realClose(true, true);
/*     */         } 
/*     */       }  return; }
/* 842 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ServerPreparedStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */