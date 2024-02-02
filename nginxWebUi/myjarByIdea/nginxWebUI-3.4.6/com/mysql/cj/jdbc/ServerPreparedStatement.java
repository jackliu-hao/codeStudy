package com.mysql.cj.jdbc;

import com.mysql.cj.CancelQueryTask;
import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.NativeSession;
import com.mysql.cj.ParseInfo;
import com.mysql.cj.PreparedQuery;
import com.mysql.cj.ServerPreparedQuery;
import com.mysql.cj.ServerPreparedQueryBindValue;
import com.mysql.cj.ServerPreparedQueryBindings;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.jdbc.exceptions.MySQLStatementCancelledException;
import com.mysql.cj.jdbc.exceptions.MySQLTimeoutException;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.a.NativePacketPayload;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ServerPreparedStatement extends ClientPreparedStatement {
   private boolean hasOnDuplicateKeyUpdate = false;
   private boolean invalid = false;
   private CJException invalidationException;
   protected boolean isCacheable = false;
   protected boolean isCached = false;

   protected static ServerPreparedStatement getInstance(JdbcConnection conn, String sql, String db, int resultSetType, int resultSetConcurrency) throws SQLException {
      return new ServerPreparedStatement(conn, sql, db, resultSetType, resultSetConcurrency);
   }

   protected ServerPreparedStatement(JdbcConnection conn, String sql, String db, int resultSetType, int resultSetConcurrency) throws SQLException {
      super(conn, db);
      this.checkNullOrEmptyQuery(sql);
      String statementComment = this.session.getProtocol().getQueryComment();
      ((PreparedQuery)this.query).setOriginalSql(statementComment == null ? sql : "/* " + statementComment + " */ " + sql);
      ((PreparedQuery)this.query).setParseInfo(new ParseInfo(((PreparedQuery)this.query).getOriginalSql(), this.session, this.charEncoding));
      this.hasOnDuplicateKeyUpdate = ((PreparedQuery)this.query).getParseInfo().getFirstStmtChar() == 'I' && this.containsOnDuplicateKeyInString(sql);

      try {
         this.serverPrepare(sql);
      } catch (SQLException | CJException var8) {
         this.realClose(false, true);
         throw SQLExceptionsMapping.translateException(var8, this.exceptionInterceptor);
      }

      this.setResultSetType(resultSetType);
      this.setResultSetConcurrency(resultSetConcurrency);
   }

   protected void initQuery() {
      this.query = ServerPreparedQuery.getInstance(this.session);
   }

   public String toString() {
      StringBuilder toStringBuf = new StringBuilder();
      toStringBuf.append(this.getClass().getName() + "[");
      toStringBuf.append(((ServerPreparedQuery)this.query).getServerStatementId());
      toStringBuf.append("]: ");

      try {
         toStringBuf.append(this.asSql());
      } catch (SQLException var3) {
         toStringBuf.append(Messages.getString("ServerPreparedStatement.6"));
         toStringBuf.append(var3);
      }

      return toStringBuf.toString();
   }

   public void addBatch() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.query.addBatch(((PreparedQuery)this.query).getQueryBindings().clone());
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public String asSql(boolean quoteStreamsAndUnknowns) throws SQLException {
      synchronized(this.checkClosed().getConnectionMutex()) {
         ClientPreparedStatement pStmtForSub = null;

         String var19;
         try {
            pStmtForSub = ClientPreparedStatement.getInstance(this.connection, ((PreparedQuery)this.query).getOriginalSql(), this.getCurrentDatabase());
            int numParameters = ((PreparedQuery)pStmtForSub.query).getParameterCount();
            int ourNumParameters = ((PreparedQuery)this.query).getParameterCount();
            ServerPreparedQueryBindValue[] parameterBindings = (ServerPreparedQueryBindValue[])((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).getBindValues();

            for(int i = 0; i < numParameters && i < ourNumParameters; ++i) {
               if (parameterBindings[i] != null) {
                  if (parameterBindings[i].isNull()) {
                     pStmtForSub.setNull(i + 1, MysqlType.NULL);
                  } else {
                     ServerPreparedQueryBindValue bindValue = parameterBindings[i];
                     switch (bindValue.bufferType) {
                        case 1:
                           pStmtForSub.setByte(i + 1, ((Long)bindValue.value).byteValue());
                           break;
                        case 2:
                           pStmtForSub.setShort(i + 1, ((Long)bindValue.value).shortValue());
                           break;
                        case 3:
                           pStmtForSub.setInt(i + 1, ((Long)bindValue.value).intValue());
                           break;
                        case 4:
                           pStmtForSub.setFloat(i + 1, (Float)bindValue.value);
                           break;
                        case 5:
                           pStmtForSub.setDouble(i + 1, (Double)bindValue.value);
                           break;
                        case 6:
                        case 7:
                        default:
                           pStmtForSub.setObject(i + 1, parameterBindings[i].value);
                           break;
                        case 8:
                           pStmtForSub.setLong(i + 1, (Long)bindValue.value);
                     }
                  }
               }
            }

            var19 = pStmtForSub.asSql(quoteStreamsAndUnknowns);
         } finally {
            if (pStmtForSub != null) {
               try {
                  pStmtForSub.close();
               } catch (SQLException var16) {
               }
            }

         }

         return var19;
      }
   }

   protected JdbcConnection checkClosed() {
      if (this.invalid) {
         throw this.invalidationException;
      } else {
         return super.checkClosed();
      }
   }

   public void clearParameters() {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            ((ServerPreparedQuery)this.query).clearParameters(true);
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   protected void setClosed(boolean flag) {
      this.isClosed = flag;
   }

   public void close() throws SQLException {
      try {
         JdbcConnection locallyScopedConn = this.connection;
         if (locallyScopedConn != null) {
            synchronized(locallyScopedConn.getConnectionMutex()) {
               if (!this.isClosed) {
                  if (this.isCacheable && this.isPoolable()) {
                     this.clearParameters();
                     this.clearAttributes();
                     this.isClosed = true;
                     this.connection.recachePreparedStatement(this);
                     this.isCached = true;
                  } else {
                     this.isClosed = false;
                     this.realClose(true, true);
                  }
               }
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   protected long[] executeBatchSerially(int batchTimeout) throws SQLException {
      synchronized(this.checkClosed().getConnectionMutex()) {
         JdbcConnection locallyScopedConn = this.connection;
         if (locallyScopedConn.isReadOnly()) {
            throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.2") + Messages.getString("ServerPreparedStatement.3"), "S1009", this.exceptionInterceptor);
         } else {
            this.clearWarnings();
            ServerPreparedQueryBindValue[] oldBindValues = (ServerPreparedQueryBindValue[])((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).getBindValues();

            long[] var38;
            try {
               long[] updateCounts = null;
               if (this.query.getBatchedArgs() != null) {
                  int nbrCommands = this.query.getBatchedArgs().size();
                  updateCounts = new long[nbrCommands];
                  if (this.retrieveGeneratedKeys) {
                     this.batchedGeneratedKeys = new ArrayList(nbrCommands);
                  }

                  for(int i = 0; i < nbrCommands; ++i) {
                     updateCounts[i] = -3L;
                  }

                  SQLException sqlEx = null;
                  int commandIndex = false;
                  ServerPreparedQueryBindValue[] previousBindValuesForBatch = null;
                  CancelQueryTask timeoutTask = null;

                  try {
                     timeoutTask = this.startQueryTimer(this, batchTimeout);

                     for(int commandIndex = 0; commandIndex < nbrCommands; ++commandIndex) {
                        Object arg = this.query.getBatchedArgs().get(commandIndex);

                        try {
                           if (arg instanceof String) {
                              updateCounts[commandIndex] = this.executeUpdateInternal((String)arg, true, this.retrieveGeneratedKeys);
                              this.getBatchedGeneratedKeys(this.results.getFirstCharOfQuery() == 'I' && this.containsOnDuplicateKeyInString((String)arg) ? 1 : 0);
                           } else {
                              ((ServerPreparedQuery)this.query).setQueryBindings((ServerPreparedQueryBindings)arg);
                              ServerPreparedQueryBindValue[] parameterBindings = (ServerPreparedQueryBindValue[])((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).getBindValues();
                              if (previousBindValuesForBatch != null) {
                                 for(int j = 0; j < parameterBindings.length; ++j) {
                                    if (parameterBindings[j].bufferType != previousBindValuesForBatch[j].bufferType) {
                                       ((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).getSendTypesToServer().set(true);
                                       break;
                                    }
                                 }
                              }

                              try {
                                 updateCounts[commandIndex] = this.executeUpdateInternal(false, true);
                              } finally {
                                 previousBindValuesForBatch = parameterBindings;
                              }

                              this.getBatchedGeneratedKeys(this.containsOnDuplicateKeyUpdateInSQL() ? 1 : 0);
                           }
                        } catch (SQLException var34) {
                           updateCounts[commandIndex] = -3L;
                           if (!this.continueBatchOnError || var34 instanceof MySQLTimeoutException || var34 instanceof MySQLStatementCancelledException || this.hasDeadlockOrTimeoutRolledBackTx(var34)) {
                              long[] newUpdateCounts = new long[commandIndex];
                              System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
                              throw SQLError.createBatchUpdateException(var34, newUpdateCounts, this.exceptionInterceptor);
                           }

                           sqlEx = var34;
                        }
                     }
                  } finally {
                     this.stopQueryTimer(timeoutTask, false, false);
                     this.resetCancelledState();
                  }

                  if (sqlEx != null) {
                     throw SQLError.createBatchUpdateException(sqlEx, updateCounts, this.exceptionInterceptor);
                  }
               }

               var38 = updateCounts != null ? updateCounts : new long[0];
            } finally {
               ((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).setBindValues(oldBindValues);
               ((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).getSendTypesToServer().set(true);
               this.clearBatch();
            }

            return var38;
         }
      }
   }

   private static SQLException appendMessageToException(SQLException sqlEx, String messageToAppend, ExceptionInterceptor interceptor) {
      String sqlState = sqlEx.getSQLState();
      int vendorErrorCode = sqlEx.getErrorCode();
      SQLException sqlExceptionWithNewMessage = SQLError.createSQLException(sqlEx.getMessage() + messageToAppend, sqlState, vendorErrorCode, interceptor);
      sqlExceptionWithNewMessage.setStackTrace(sqlEx.getStackTrace());
      return sqlExceptionWithNewMessage;
   }

   protected <M extends Message> ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, M sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, ColumnDefinition metadata, boolean isBatch) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            ((PreparedQuery)this.query).getQueryBindings().setNumberOfExecutions(((PreparedQuery)this.query).getQueryBindings().getNumberOfExecutions() + 1);

            ResultSetInternalMethods var10000;
            try {
               var10000 = this.serverExecute(maxRowsToRetrieve, createStreamingResultSet, metadata);
            } catch (SQLException var14) {
               SQLException sqlEx = var14;
               if ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.enablePacketDebug).getValue()) {
                  this.session.dumpPacketRingBuffer();
               }

               if ((Boolean)this.dumpQueriesOnException.getValue()) {
                  String extractedSql = this.toString();
                  StringBuilder messageBuf = new StringBuilder(extractedSql.length() + 32);
                  messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
                  messageBuf.append(extractedSql);
                  messageBuf.append("\n\n");
                  sqlEx = appendMessageToException(var14, messageBuf.toString(), this.exceptionInterceptor);
               }

               throw sqlEx;
            } catch (Exception var15) {
               if ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.enablePacketDebug).getValue()) {
                  this.session.dumpPacketRingBuffer();
               }

               SQLException sqlEx = SQLError.createSQLException(var15.toString(), "S1000", var15, this.exceptionInterceptor);
               if ((Boolean)this.dumpQueriesOnException.getValue()) {
                  String extractedSql = this.toString();
                  StringBuilder messageBuf = new StringBuilder(extractedSql.length() + 32);
                  messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
                  messageBuf.append(extractedSql);
                  messageBuf.append("\n\n");
                  sqlEx = appendMessageToException(sqlEx, messageBuf.toString(), this.exceptionInterceptor);
               }

               throw sqlEx;
            }

            return var10000;
         }
      } catch (CJException var17) {
         throw SQLExceptionsMapping.translateException(var17, this.getExceptionInterceptor());
      }
   }

   protected ServerPreparedQueryBindValue getBinding(int parameterIndex, boolean forLongData) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            int i = this.getCoreParameterIndex(parameterIndex);
            return ((ServerPreparedQueryBindings)((ServerPreparedQuery)this.query).getQueryBindings()).getBinding(i, forLongData);
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public ResultSetMetaData getMetaData() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            ColumnDefinition resultFields = ((ServerPreparedQuery)this.query).getResultFields();
            return resultFields != null && resultFields.getFields() != null ? new com.mysql.cj.jdbc.result.ResultSetMetaData(this.session, resultFields.getFields(), (Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.useOldAliasMetadataBehavior).getValue(), (Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.yearIsDateType).getValue(), this.exceptionInterceptor) : null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public ParameterMetaData getParameterMetaData() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (this.parameterMetaData == null) {
               this.parameterMetaData = new MysqlParameterMetadata(this.session, ((ServerPreparedQuery)this.query).getParameterFields(), ((PreparedQuery)this.query).getParameterCount(), this.exceptionInterceptor);
            }

            return this.parameterMetaData;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public boolean isNull(int paramIndex) {
      throw new IllegalArgumentException(Messages.getString("ServerPreparedStatement.7"));
   }

   public void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
      try {
         JdbcConnection locallyScopedConn = this.connection;
         if (locallyScopedConn != null) {
            synchronized(locallyScopedConn.getConnectionMutex()) {
               if (this.connection != null) {
                  CJException exceptionDuringClose = null;
                  if (this.isCached) {
                     locallyScopedConn.decachePreparedStatement(this);
                     this.isCached = false;
                  }

                  super.realClose(calledExplicitly, closeOpenResults);
                  ((ServerPreparedQuery)this.query).clearParameters(false);
                  if (calledExplicitly && !locallyScopedConn.isClosed()) {
                     synchronized(locallyScopedConn.getConnectionMutex()) {
                        try {
                           ((NativeSession)locallyScopedConn.getSession()).sendCommand(this.commandBuilder.buildComStmtClose((NativePacketPayload)null, ((ServerPreparedQuery)this.query).getServerStatementId()), true, 0);
                        } catch (CJException var11) {
                           exceptionDuringClose = var11;
                        }
                     }
                  }

                  if (exceptionDuringClose != null) {
                     throw exceptionDuringClose;
                  }
               }

            }
         }
      } catch (CJException var14) {
         throw SQLExceptionsMapping.translateException(var14, this.getExceptionInterceptor());
      }
   }

   protected void rePrepare() {
      synchronized(this.checkClosed().getConnectionMutex()) {
         this.invalidationException = null;

         try {
            this.serverPrepare(((PreparedQuery)this.query).getOriginalSql());
         } catch (Exception var7) {
            this.invalidationException = ExceptionFactory.createException((String)var7.getMessage(), (Throwable)var7);
         }

         if (this.invalidationException != null) {
            this.invalid = true;
            this.query.closeQuery();
            if (this.results != null) {
               try {
                  this.results.close();
               } catch (Exception var6) {
               }
            }

            if (this.generatedKeysResults != null) {
               try {
                  this.generatedKeysResults.close();
               } catch (Exception var5) {
               }
            }

            try {
               this.closeAllOpenResults();
            } catch (Exception var4) {
            }

            if (this.connection != null && !(Boolean)this.dontTrackOpenResources.getValue()) {
               this.connection.unregisterStatement(this);
            }
         }

      }
   }

   protected ResultSetInternalMethods serverExecute(int maxRowsToRetrieve, boolean createStreamingResultSet, ColumnDefinition metadata) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.results = (ResultSetInternalMethods)((ServerPreparedQuery)this.query).serverExecute(maxRowsToRetrieve, createStreamingResultSet, metadata, this.resultSetFactory);
            return this.results;
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   protected void serverPrepare(String sql) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            SQLException t = null;

            try {
               ServerPreparedQuery q = (ServerPreparedQuery)this.query;
               q.serverPrepare(sql);
            } catch (IOException var21) {
               t = SQLError.createCommunicationsException(this.connection, this.session.getProtocol().getPacketSentTimeHolder(), this.session.getProtocol().getPacketReceivedTimeHolder(), var21, this.exceptionInterceptor);
            } catch (CJException var22) {
               SQLException ex = SQLExceptionsMapping.translateException(var22);
               if ((Boolean)this.dumpQueriesOnException.getValue()) {
                  StringBuilder messageBuf = new StringBuilder(((PreparedQuery)this.query).getOriginalSql().length() + 32);
                  messageBuf.append("\n\nQuery being prepared when exception was thrown:\n\n");
                  messageBuf.append(((PreparedQuery)this.query).getOriginalSql());
                  ex = appendMessageToException(ex, messageBuf.toString(), this.exceptionInterceptor);
               }

               t = ex;
            } finally {
               try {
                  this.session.clearInputStream();
               } catch (Exception var20) {
                  if (t == null) {
                     t = SQLError.createCommunicationsException(this.connection, this.session.getProtocol().getPacketSentTimeHolder(), this.session.getProtocol().getPacketReceivedTimeHolder(), var20, this.exceptionInterceptor);
                  }
               }

               if (t != null) {
                  throw t;
               }

            }

         }
      } catch (CJException var25) {
         throw SQLExceptionsMapping.translateException(var25, this.getExceptionInterceptor());
      }
   }

   protected void checkBounds(int parameterIndex, int parameterIndexOffset) throws SQLException {
      int paramCount = ((PreparedQuery)this.query).getParameterCount();
      if (paramCount == 0) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ServerPreparedStatement.8"), this.session.getExceptionInterceptor());
      } else if (parameterIndex < 0 || parameterIndex > paramCount) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ServerPreparedStatement.9") + (parameterIndex + 1) + Messages.getString("ServerPreparedStatement.10") + paramCount, this.session.getExceptionInterceptor());
      }
   }

   /** @deprecated */
   @Deprecated
   public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
      try {
         this.checkClosed();
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void setURL(int parameterIndex, URL x) throws SQLException {
      try {
         this.checkClosed();
         this.setString(parameterIndex, x.toString());
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public long getServerStatementId() {
      return ((ServerPreparedQuery)this.query).getServerStatementId();
   }

   protected int setOneBatchedParameterSet(PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet) throws SQLException {
      ServerPreparedQueryBindValue[] paramArg = (ServerPreparedQueryBindValue[])((ServerPreparedQueryBindings)paramSet).getBindValues();

      for(int j = 0; j < paramArg.length; ++j) {
         if (paramArg[j].isNull()) {
            batchedStatement.setNull(batchedParamIndex++, MysqlType.NULL.getJdbcType());
         } else {
            Object value;
            if (paramArg[j].isStream()) {
               value = paramArg[j].value;
               if (value instanceof byte[]) {
                  batchedStatement.setBytes(batchedParamIndex++, (byte[])((byte[])value));
               } else if (value instanceof InputStream) {
                  batchedStatement.setBinaryStream(batchedParamIndex++, (InputStream)value, paramArg[j].getStreamLength());
               } else if (value instanceof java.sql.Blob) {
                  try {
                     batchedStatement.setBinaryStream(batchedParamIndex++, ((java.sql.Blob)value).getBinaryStream(), paramArg[j].getStreamLength());
                  } catch (Throwable var8) {
                     throw ExceptionFactory.createException(var8.getMessage(), this.session.getExceptionInterceptor());
                  }
               } else {
                  if (!(value instanceof Reader)) {
                     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ServerPreparedStatement.18") + value.getClass().getName() + "'", this.session.getExceptionInterceptor());
                  }

                  batchedStatement.setCharacterStream(batchedParamIndex++, (Reader)value, paramArg[j].getStreamLength());
               }
            } else {
               switch (paramArg[j].bufferType) {
                  case 0:
                  case 15:
                  case 246:
                  case 253:
                  case 254:
                     value = paramArg[j].value;
                     if (value instanceof byte[]) {
                        batchedStatement.setBytes(batchedParamIndex, (byte[])((byte[])value));
                     } else {
                        batchedStatement.setString(batchedParamIndex, (String)value);
                     }

                     if (batchedStatement instanceof ServerPreparedStatement) {
                        ServerPreparedQueryBindValue asBound = ((ServerPreparedStatement)batchedStatement).getBinding(batchedParamIndex, false);
                        asBound.bufferType = paramArg[j].bufferType;
                     }

                     ++batchedParamIndex;
                     break;
                  case 1:
                     batchedStatement.setByte(batchedParamIndex++, ((Long)paramArg[j].value).byteValue());
                     break;
                  case 2:
                     batchedStatement.setShort(batchedParamIndex++, ((Long)paramArg[j].value).shortValue());
                     break;
                  case 3:
                     batchedStatement.setInt(batchedParamIndex++, ((Long)paramArg[j].value).intValue());
                     break;
                  case 4:
                     batchedStatement.setFloat(batchedParamIndex++, (Float)paramArg[j].value);
                     break;
                  case 5:
                     batchedStatement.setDouble(batchedParamIndex++, (Double)paramArg[j].value);
                     break;
                  case 7:
                     batchedStatement.setTimestamp(batchedParamIndex++, (Timestamp)paramArg[j].value);
                     break;
                  case 8:
                     batchedStatement.setLong(batchedParamIndex++, (Long)paramArg[j].value);
                     break;
                  case 10:
                     batchedStatement.setObject(batchedParamIndex++, paramArg[j].value, MysqlType.DATE);
                     break;
                  case 11:
                     batchedStatement.setTime(batchedParamIndex++, (Time)paramArg[j].value);
                     break;
                  case 12:
                     batchedStatement.setObject(batchedParamIndex++, paramArg[j].value);
                     break;
                  default:
                     throw new IllegalArgumentException(Messages.getString("ServerPreparedStatement.26", new Object[]{batchedParamIndex}));
               }
            }
         }
      }

      return batchedParamIndex;
   }

   protected boolean containsOnDuplicateKeyUpdateInSQL() {
      return this.hasOnDuplicateKeyUpdate;
   }

   protected ClientPreparedStatement prepareBatchedInsertSQL(JdbcConnection localConn, int numBatches) throws SQLException {
      synchronized(this.checkClosed().getConnectionMutex()) {
         ClientPreparedStatement var10000;
         try {
            ClientPreparedStatement pstmt = (ClientPreparedStatement)localConn.prepareStatement(((PreparedQuery)this.query).getParseInfo().getSqlForBatch(numBatches), this.resultSetConcurrency, this.query.getResultType().getIntValue()).unwrap(ClientPreparedStatement.class);
            pstmt.setRetrieveGeneratedKeys(this.retrieveGeneratedKeys);
            this.getQueryAttributesBindings().runThroughAll((a) -> {
               pstmt.setAttribute(a.getName(), a.getValue());
            });
            var10000 = pstmt;
         } catch (UnsupportedEncodingException var7) {
            SQLException sqlEx = SQLError.createSQLException(Messages.getString("ServerPreparedStatement.27"), "S1000", this.exceptionInterceptor);
            sqlEx.initCause(var7);
            throw sqlEx;
         }

         return var10000;
      }
   }

   public void setPoolable(boolean poolable) throws SQLException {
      try {
         super.setPoolable(poolable);
         if (!poolable && this.isCached) {
            this.connection.decachePreparedStatement(this);
            this.isCached = false;
            if (this.isClosed) {
               this.isClosed = false;
               this.realClose(true, true);
            }
         }

      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }
}
