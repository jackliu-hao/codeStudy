package com.mysql.cj.jdbc;

import com.mysql.cj.CancelQueryTask;
import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.NativeSession;
import com.mysql.cj.ParseInfo;
import com.mysql.cj.PingTarget;
import com.mysql.cj.Query;
import com.mysql.cj.QueryAttributesBindings;
import com.mysql.cj.QueryReturnType;
import com.mysql.cj.Session;
import com.mysql.cj.SimpleQuery;
import com.mysql.cj.TransactionEventHandler;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.AssertionFailedException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.CJTimeoutException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.OperationCancelledException;
import com.mysql.cj.exceptions.StatementIsClosedException;
import com.mysql.cj.jdbc.exceptions.MySQLStatementCancelledException;
import com.mysql.cj.jdbc.exceptions.MySQLTimeoutException;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
import com.mysql.cj.jdbc.result.ResultSetFactory;
import com.mysql.cj.jdbc.result.ResultSetImpl;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.a.NativeMessageBuilder;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.protocol.a.result.ByteArrayRow;
import com.mysql.cj.protocol.a.result.ResultsetRowsStatic;
import com.mysql.cj.result.DefaultColumnDefinition;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.Row;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class StatementImpl implements JdbcStatement {
   protected static final String PING_MARKER = "/* ping */";
   public static final byte USES_VARIABLES_FALSE = 0;
   public static final byte USES_VARIABLES_TRUE = 1;
   public static final byte USES_VARIABLES_UNKNOWN = -1;
   protected NativeMessageBuilder commandBuilder = null;
   protected String charEncoding = null;
   protected volatile JdbcConnection connection = null;
   protected boolean doEscapeProcessing = true;
   protected boolean isClosed = false;
   protected long lastInsertId = -1L;
   protected int maxFieldSize;
   public int maxRows;
   protected Set<ResultSetInternalMethods> openResults;
   protected boolean pedantic;
   protected boolean profileSQL;
   protected ResultSetInternalMethods results;
   protected ResultSetInternalMethods generatedKeysResults;
   protected int resultSetConcurrency;
   protected long updateCount;
   protected boolean useUsageAdvisor;
   protected SQLWarning warningChain;
   protected boolean holdResultsOpenOverClose;
   protected ArrayList<Row> batchedGeneratedKeys;
   protected boolean retrieveGeneratedKeys;
   protected boolean continueBatchOnError;
   protected PingTarget pingTarget;
   protected ExceptionInterceptor exceptionInterceptor;
   protected boolean lastQueryIsOnDupKeyUpdate;
   private boolean isImplicitlyClosingResults;
   protected RuntimeProperty<Boolean> dontTrackOpenResources;
   protected RuntimeProperty<Boolean> dumpQueriesOnException;
   protected boolean logSlowQueries;
   protected RuntimeProperty<Boolean> rewriteBatchedStatements;
   protected RuntimeProperty<Integer> maxAllowedPacket;
   protected boolean dontCheckOnDuplicateKeyUpdateInSQL;
   protected ResultSetFactory resultSetFactory;
   protected Query query;
   protected NativeSession session;
   private Resultset.Type originalResultSetType;
   private int originalFetchSize;
   private boolean isPoolable;
   private boolean closeOnCompletion;

   public StatementImpl(JdbcConnection c, String db) throws SQLException {
      this.maxFieldSize = (Integer)PropertyDefinitions.getPropertyDefinition(PropertyKey.maxAllowedPacket).getDefaultValue();
      this.maxRows = -1;
      this.openResults = new HashSet();
      this.pedantic = false;
      this.profileSQL = false;
      this.results = null;
      this.generatedKeysResults = null;
      this.resultSetConcurrency = 0;
      this.updateCount = -1L;
      this.useUsageAdvisor = false;
      this.warningChain = null;
      this.holdResultsOpenOverClose = false;
      this.batchedGeneratedKeys = null;
      this.retrieveGeneratedKeys = false;
      this.continueBatchOnError = false;
      this.pingTarget = null;
      this.lastQueryIsOnDupKeyUpdate = false;
      this.isImplicitlyClosingResults = false;
      this.logSlowQueries = false;
      this.session = null;
      this.originalResultSetType = Resultset.Type.FORWARD_ONLY;
      this.originalFetchSize = 0;
      this.isPoolable = false;
      this.closeOnCompletion = false;
      if (c != null && !c.isClosed()) {
         this.connection = c;
         this.session = (NativeSession)c.getSession();
         this.exceptionInterceptor = c.getExceptionInterceptor();
         this.commandBuilder = new NativeMessageBuilder(this.session.getServerSession().supportsQueryAttributes());

         try {
            this.initQuery();
         } catch (CJException var6) {
            throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
         }

         this.query.setCurrentDatabase(db);
         JdbcPropertySet pset = c.getPropertySet();
         this.dontTrackOpenResources = pset.getBooleanProperty(PropertyKey.dontTrackOpenResources);
         this.dumpQueriesOnException = pset.getBooleanProperty(PropertyKey.dumpQueriesOnException);
         this.continueBatchOnError = (Boolean)pset.getBooleanProperty(PropertyKey.continueBatchOnError).getValue();
         this.pedantic = (Boolean)pset.getBooleanProperty(PropertyKey.pedantic).getValue();
         this.rewriteBatchedStatements = pset.getBooleanProperty(PropertyKey.rewriteBatchedStatements);
         this.charEncoding = (String)pset.getStringProperty(PropertyKey.characterEncoding).getValue();
         this.profileSQL = (Boolean)pset.getBooleanProperty(PropertyKey.profileSQL).getValue();
         this.useUsageAdvisor = (Boolean)pset.getBooleanProperty(PropertyKey.useUsageAdvisor).getValue();
         this.logSlowQueries = (Boolean)pset.getBooleanProperty(PropertyKey.logSlowQueries).getValue();
         this.maxAllowedPacket = pset.getIntegerProperty(PropertyKey.maxAllowedPacket);
         this.dontCheckOnDuplicateKeyUpdateInSQL = (Boolean)pset.getBooleanProperty(PropertyKey.dontCheckOnDuplicateKeyUpdateInSQL).getValue();
         this.doEscapeProcessing = (Boolean)pset.getBooleanProperty(PropertyKey.enableEscapeProcessing).getValue();
         this.maxFieldSize = (Integer)this.maxAllowedPacket.getValue();
         if (!(Boolean)this.dontTrackOpenResources.getValue()) {
            c.registerStatement(this);
         }

         int defaultFetchSize = (Integer)pset.getIntegerProperty(PropertyKey.defaultFetchSize).getValue();
         if (defaultFetchSize != 0) {
            this.setFetchSize(defaultFetchSize);
         }

         int maxRowsConn = (Integer)pset.getIntegerProperty(PropertyKey.maxRows).getValue();
         if (maxRowsConn != -1) {
            this.setMaxRows(maxRowsConn);
         }

         this.holdResultsOpenOverClose = (Boolean)pset.getBooleanProperty(PropertyKey.holdResultsOpenOverStatementClose).getValue();
         this.resultSetFactory = new ResultSetFactory(this.connection, this);
      } else {
         throw SQLError.createSQLException(Messages.getString("Statement.0"), "08003", (ExceptionInterceptor)null);
      }
   }

   protected void initQuery() {
      this.query = new SimpleQuery(this.session);
   }

   public void addBatch(String sql) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (sql != null) {
               this.query.addBatch(sql);
            }

         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void addBatch(Object batch) {
      this.query.addBatch(batch);
   }

   public List<Object> getBatchedArgs() {
      return this.query.getBatchedArgs();
   }

   public void cancel() throws SQLException {
      try {
         if (this.query.getStatementExecuting().get()) {
            if (!this.isClosed && this.connection != null) {
               NativeSession newSession = null;

               try {
                  HostInfo hostInfo = this.session.getHostInfo();
                  String database = hostInfo.getDatabase();
                  String user = hostInfo.getUser();
                  String password = hostInfo.getPassword();
                  newSession = new NativeSession(this.session.getHostInfo(), this.session.getPropertySet());
                  newSession.connect(hostInfo, user, password, database, 30000, new TransactionEventHandler() {
                     public void transactionCompleted() {
                     }

                     public void transactionBegun() {
                     }
                  });
                  newSession.sendCommand((new NativeMessageBuilder(newSession.getServerSession().supportsQueryAttributes())).buildComQuery(newSession.getSharedSendPacket(), "KILL QUERY " + this.session.getThreadId()), false, 0);
                  this.setCancelStatus(Query.CancelStatus.CANCELED_BY_USER);
               } catch (IOException var11) {
                  throw SQLExceptionsMapping.translateException(var11, this.exceptionInterceptor);
               } finally {
                  if (newSession != null) {
                     newSession.forceClose();
                  }

               }
            }

         }
      } catch (CJException var13) {
         throw SQLExceptionsMapping.translateException(var13, this.getExceptionInterceptor());
      }
   }

   protected JdbcConnection checkClosed() {
      JdbcConnection c = this.connection;
      if (c == null) {
         throw (StatementIsClosedException)ExceptionFactory.createException(StatementIsClosedException.class, Messages.getString("Statement.AlreadyClosed"), this.getExceptionInterceptor());
      } else {
         return c;
      }
   }

   protected boolean isResultSetProducingQuery(String sql) {
      QueryReturnType queryReturnType = ParseInfo.getQueryReturnType(sql, this.session.getServerSession().isNoBackslashEscapesSet());
      return queryReturnType == QueryReturnType.PRODUCES_RESULT_SET || queryReturnType == QueryReturnType.MAY_PRODUCE_RESULT_SET;
   }

   protected boolean isNonResultSetProducingQuery(String sql) {
      QueryReturnType queryReturnType = ParseInfo.getQueryReturnType(sql, this.session.getServerSession().isNoBackslashEscapesSet());
      return queryReturnType == QueryReturnType.DOES_NOT_PRODUCE_RESULT_SET || queryReturnType == QueryReturnType.MAY_PRODUCE_RESULT_SET;
   }

   protected void checkNullOrEmptyQuery(String sql) throws SQLException {
      if (sql == null) {
         throw SQLError.createSQLException(Messages.getString("Statement.59"), "S1009", this.getExceptionInterceptor());
      } else if (sql.length() == 0) {
         throw SQLError.createSQLException(Messages.getString("Statement.61"), "S1009", this.getExceptionInterceptor());
      }
   }

   public void clearBatch() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.query.clearBatchedArgs();
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void clearBatchedArgs() {
      this.query.clearBatchedArgs();
   }

   public void clearWarnings() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.setClearWarningsCalled(true);
            this.warningChain = null;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void close() throws SQLException {
      try {
         this.realClose(true, true);
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   protected void closeAllOpenResults() throws SQLException {
      JdbcConnection locallyScopedConn = this.connection;
      if (locallyScopedConn != null) {
         synchronized(locallyScopedConn.getConnectionMutex()) {
            if (this.openResults != null) {
               Iterator var3 = this.openResults.iterator();

               while(var3.hasNext()) {
                  ResultSetInternalMethods element = (ResultSetInternalMethods)var3.next();

                  try {
                     element.realClose(false);
                  } catch (SQLException var7) {
                     AssertionFailedException.shouldNotHappen((Exception)var7);
                  }
               }

               this.openResults.clear();
            }

         }
      }
   }

   protected void implicitlyCloseAllOpenResults() throws SQLException {
      this.isImplicitlyClosingResults = true;

      try {
         if (!this.holdResultsOpenOverClose && !(Boolean)this.dontTrackOpenResources.getValue()) {
            if (this.results != null) {
               this.results.realClose(false);
            }

            if (this.generatedKeysResults != null) {
               this.generatedKeysResults.realClose(false);
            }

            this.closeAllOpenResults();
         }
      } finally {
         this.isImplicitlyClosingResults = false;
      }

   }

   public void removeOpenResultSet(ResultSetInternalMethods rs) {
      try {
         try {
            synchronized(this.checkClosed().getConnectionMutex()) {
               if (this.openResults != null) {
                  this.openResults.remove(rs);
               }

               boolean hasMoreResults = rs.getNextResultset() != null;
               if (this.results == rs && !hasMoreResults) {
                  this.results = null;
               }

               if (this.generatedKeysResults == rs) {
                  this.generatedKeysResults = null;
               }

               if (!this.isImplicitlyClosingResults && !hasMoreResults) {
                  this.checkAndPerformCloseOnCompletionAction();
               }
            }
         } catch (StatementIsClosedException var7) {
         }

      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public int getOpenResultSetCount() {
      try {
         try {
            synchronized(this.checkClosed().getConnectionMutex()) {
               return this.openResults != null ? this.openResults.size() : 0;
            }
         } catch (StatementIsClosedException var5) {
            return 0;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   private void checkAndPerformCloseOnCompletionAction() {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (this.isCloseOnCompletion() && !(Boolean)this.dontTrackOpenResources.getValue() && this.getOpenResultSetCount() == 0 && (this.results == null || !this.results.hasRows() || this.results.isClosed()) && (this.generatedKeysResults == null || !this.generatedKeysResults.hasRows() || this.generatedKeysResults.isClosed())) {
               this.realClose(false, false);
            }
         }
      } catch (SQLException var4) {
      }

   }

   private ResultSetInternalMethods createResultSetUsingServerFetch(String sql) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            PreparedStatement pStmt = this.connection.prepareStatement(sql, this.query.getResultType().getIntValue(), this.resultSetConcurrency);
            pStmt.setFetchSize(this.query.getResultFetchSize());
            if (this.getQueryTimeout() > 0) {
               pStmt.setQueryTimeout(this.getQueryTimeout());
            }

            if (this.maxRows > -1) {
               pStmt.setMaxRows(this.maxRows);
            }

            this.statementBegins();
            pStmt.execute();
            ResultSetInternalMethods rs = ((StatementImpl)pStmt).getResultSetInternal();
            rs.setStatementUsedForFetchingRows((ClientPreparedStatement)pStmt);
            this.results = rs;
            return rs;
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   protected boolean createStreamingResultSet() {
      return this.query.getResultType() == Resultset.Type.FORWARD_ONLY && this.resultSetConcurrency == 1007 && this.query.getResultFetchSize() == Integer.MIN_VALUE;
   }

   public void enableStreamingResults() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.originalResultSetType = this.query.getResultType();
            this.originalFetchSize = this.query.getResultFetchSize();
            this.setFetchSize(Integer.MIN_VALUE);
            this.setResultSetType(Resultset.Type.FORWARD_ONLY);
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void disableStreamingResults() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (this.query.getResultFetchSize() == Integer.MIN_VALUE && this.query.getResultType() == Resultset.Type.FORWARD_ONLY) {
               this.setFetchSize(this.originalFetchSize);
               this.setResultSetType(this.originalResultSetType);
            }

         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   protected void setupStreamingTimeout(JdbcConnection con) throws SQLException {
      int netTimeoutForStreamingResults = (Integer)this.session.getPropertySet().getIntegerProperty(PropertyKey.netTimeoutForStreamingResults).getValue();
      if (this.createStreamingResultSet() && netTimeoutForStreamingResults > 0) {
         this.executeSimpleNonQuery(con, "SET net_write_timeout=" + netTimeoutForStreamingResults);
      }

   }

   public CancelQueryTask startQueryTimer(Query stmtToCancel, int timeout) {
      return this.query.startQueryTimer(stmtToCancel, timeout);
   }

   public void stopQueryTimer(CancelQueryTask timeoutTask, boolean rethrowCancelReason, boolean checkCancelTimeout) {
      this.query.stopQueryTimer(timeoutTask, rethrowCancelReason, checkCancelTimeout);
   }

   public boolean execute(String sql) throws SQLException {
      try {
         return this.executeInternal(sql, false);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   private boolean executeInternal(String sql, boolean returnGeneratedKeys) throws SQLException {
      try {
         JdbcConnection locallyScopedConn = this.checkClosed();
         synchronized(locallyScopedConn.getConnectionMutex()) {
            this.checkClosed();
            this.checkNullOrEmptyQuery(sql);
            this.resetCancelledState();
            this.implicitlyCloseAllOpenResults();
            if (sql.charAt(0) == '/' && sql.startsWith("/* ping */")) {
               this.doPingInstead();
               return true;
            } else {
               this.retrieveGeneratedKeys = returnGeneratedKeys;
               this.lastQueryIsOnDupKeyUpdate = returnGeneratedKeys && ParseInfo.firstCharOfStatementUc(sql, this.session.getServerSession().isNoBackslashEscapesSet()) == 'I' && this.containsOnDuplicateKeyInString(sql);
               if (!ParseInfo.isReadOnlySafeQuery(sql, this.session.getServerSession().isNoBackslashEscapesSet()) && locallyScopedConn.isReadOnly()) {
                  throw SQLError.createSQLException(Messages.getString("Statement.27") + Messages.getString("Statement.28"), "S1009", this.getExceptionInterceptor());
               } else {
                  boolean var30;
                  try {
                     this.setupStreamingTimeout(locallyScopedConn);
                     if (this.doEscapeProcessing) {
                        Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.session.getServerSession().getSessionTimeZone(), this.session.getServerSession().getCapabilities().serverSupportsFracSecs(), this.session.getServerSession().isServerTruncatesFracSecs(), this.getExceptionInterceptor());
                        sql = escapedSqlResult instanceof String ? (String)escapedSqlResult : ((EscapeProcessorResult)escapedSqlResult).escapedSql;
                     }

                     CachedResultSetMetaData cachedMetaData = null;
                     ResultSetInternalMethods rs = null;
                     this.batchedGeneratedKeys = null;
                     if (this.useServerFetch()) {
                        rs = this.createResultSetUsingServerFetch(sql);
                     } else {
                        CancelQueryTask timeoutTask = null;
                        String oldDb = null;

                        try {
                           timeoutTask = this.startQueryTimer(this, this.getTimeoutInMillis());
                           if (!locallyScopedConn.getDatabase().equals(this.getCurrentDatabase())) {
                              oldDb = locallyScopedConn.getDatabase();
                              locallyScopedConn.setDatabase(this.getCurrentDatabase());
                           }

                           if ((Boolean)locallyScopedConn.getPropertySet().getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()) {
                              cachedMetaData = locallyScopedConn.getCachedMetaData(sql);
                           }

                           locallyScopedConn.setSessionMaxRows(this.isResultSetProducingQuery(sql) ? this.maxRows : -1);
                           this.statementBegins();
                           rs = (ResultSetInternalMethods)((NativeSession)locallyScopedConn.getSession()).execSQL(this, sql, this.maxRows, (NativePacketPayload)null, this.createStreamingResultSet(), this.getResultSetFactory(), cachedMetaData, false);
                           if (timeoutTask != null) {
                              this.stopQueryTimer(timeoutTask, true, true);
                              timeoutTask = null;
                           }
                        } catch (OperationCancelledException | CJTimeoutException var24) {
                           throw SQLExceptionsMapping.translateException(var24, this.exceptionInterceptor);
                        } finally {
                           this.stopQueryTimer(timeoutTask, false, false);
                           if (oldDb != null) {
                              locallyScopedConn.setDatabase(oldDb);
                           }

                        }
                     }

                     if (rs != null) {
                        this.lastInsertId = rs.getUpdateID();
                        this.results = rs;
                        rs.setFirstCharOfQuery(ParseInfo.firstCharOfStatementUc(sql, this.session.getServerSession().isNoBackslashEscapesSet()));
                        if (rs.hasRows()) {
                           if (cachedMetaData != null) {
                              locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
                           } else if ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()) {
                              locallyScopedConn.initializeResultsMetadataFromCache(sql, (CachedResultSetMetaData)null, this.results);
                           }
                        }
                     }

                     var30 = rs != null && rs.hasRows();
                  } finally {
                     this.query.getStatementExecuting().set(false);
                  }

                  return var30;
               }
            }
         }
      } catch (CJException var28) {
         throw SQLExceptionsMapping.translateException(var28, this.getExceptionInterceptor());
      }
   }

   public void statementBegins() {
      this.query.statementBegins();
   }

   public void resetCancelledState() {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.query.resetCancelledState();
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public boolean execute(String sql, int returnGeneratedKeys) throws SQLException {
      try {
         return this.executeInternal(sql, returnGeneratedKeys == 1);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public boolean execute(String sql, int[] generatedKeyIndices) throws SQLException {
      try {
         return this.executeInternal(sql, generatedKeyIndices != null && generatedKeyIndices.length > 0);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public boolean execute(String sql, String[] generatedKeyNames) throws SQLException {
      try {
         return this.executeInternal(sql, generatedKeyNames != null && generatedKeyNames.length > 0);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public int[] executeBatch() throws SQLException {
      try {
         return Util.truncateAndConvertToInt(this.executeBatchInternal());
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   protected long[] executeBatchInternal() throws SQLException {
      JdbcConnection locallyScopedConn = this.checkClosed();
      synchronized(locallyScopedConn.getConnectionMutex()) {
         if (locallyScopedConn.isReadOnly()) {
            throw SQLError.createSQLException(Messages.getString("Statement.34") + Messages.getString("Statement.35"), "S1009", this.getExceptionInterceptor());
         } else {
            this.implicitlyCloseAllOpenResults();
            List<Object> batchedArgs = this.query.getBatchedArgs();
            if (batchedArgs != null && batchedArgs.size() != 0) {
               int individualStatementTimeout = this.getTimeoutInMillis();
               this.setTimeoutInMillis(0);
               CancelQueryTask timeoutTask = null;

               try {
                  this.resetCancelledState();
                  this.statementBegins();

                  try {
                     this.retrieveGeneratedKeys = true;
                     long[] updateCounts = null;
                     long[] nbrCommands;
                     if (batchedArgs != null) {
                        nbrCommands = (long[])batchedArgs.size();
                        this.batchedGeneratedKeys = new ArrayList(batchedArgs.size());
                        boolean multiQueriesEnabled = (Boolean)locallyScopedConn.getPropertySet().getBooleanProperty(PropertyKey.allowMultiQueries).getValue();
                        Object sqlEx;
                        if (multiQueriesEnabled || (Boolean)locallyScopedConn.getPropertySet().getBooleanProperty(PropertyKey.rewriteBatchedStatements).getValue() && nbrCommands > 4) {
                           sqlEx = this.executeBatchUsingMultiQueries(multiQueriesEnabled, (int)nbrCommands, individualStatementTimeout);
                           return (long[])sqlEx;
                        }

                        timeoutTask = this.startQueryTimer(this, individualStatementTimeout);
                        updateCounts = new long[nbrCommands];

                        for(int i = 0; i < nbrCommands; ++i) {
                           updateCounts[i] = -3L;
                        }

                        sqlEx = null;
                        int commandIndex = false;
                        int commandIndex = 0;

                        while(true) {
                           if (commandIndex < nbrCommands) {
                              label431: {
                                 try {
                                    String sql = (String)batchedArgs.get(commandIndex);
                                    updateCounts[commandIndex] = this.executeUpdateInternal(sql, true, true);
                                    if (timeoutTask != null) {
                                       this.checkCancelTimeout();
                                    }

                                    this.getBatchedGeneratedKeys(this.results.getFirstCharOfQuery() == 'I' && this.containsOnDuplicateKeyInString(sql) ? 1 : 0);
                                 } catch (SQLException var25) {
                                    updateCounts[commandIndex] = -3L;
                                    if (!this.continueBatchOnError || var25 instanceof MySQLTimeoutException || var25 instanceof MySQLStatementCancelledException || this.hasDeadlockOrTimeoutRolledBackTx(var25)) {
                                       long[] newUpdateCounts = new long[commandIndex];
                                       if (this.hasDeadlockOrTimeoutRolledBackTx(var25)) {
                                          for(int i = 0; i < newUpdateCounts.length; ++i) {
                                             newUpdateCounts[i] = -3L;
                                          }
                                       } else {
                                          System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
                                       }

                                       sqlEx = var25;
                                       break label431;
                                    }

                                    sqlEx = var25;
                                 }

                                 ++commandIndex;
                                 continue;
                              }
                           }

                           if (sqlEx != null) {
                              throw SQLError.createBatchUpdateException((SQLException)sqlEx, updateCounts, this.getExceptionInterceptor());
                           }
                           break;
                        }
                     }

                     if (timeoutTask != null) {
                        this.stopQueryTimer(timeoutTask, true, true);
                        timeoutTask = null;
                     }

                     nbrCommands = updateCounts != null ? updateCounts : new long[0];
                     return nbrCommands;
                  } finally {
                     this.query.getStatementExecuting().set(false);
                  }
               } finally {
                  this.stopQueryTimer(timeoutTask, false, false);
                  this.resetCancelledState();
                  this.setTimeoutInMillis(individualStatementTimeout);
                  this.clearBatch();
               }
            } else {
               return new long[0];
            }
         }
      }
   }

   protected final boolean hasDeadlockOrTimeoutRolledBackTx(SQLException ex) {
      int vendorCode = ex.getErrorCode();
      switch (vendorCode) {
         case 1205:
            return false;
         case 1206:
         case 1213:
            return true;
         default:
            return false;
      }
   }

   private long[] executeBatchUsingMultiQueries(boolean multiQueriesEnabled, int nbrCommands, int individualStatementTimeout) throws SQLException {
      try {
         JdbcConnection locallyScopedConn = this.checkClosed();
         synchronized(locallyScopedConn.getConnectionMutex()) {
            if (!multiQueriesEnabled) {
               this.session.enableMultiQueries();
            }

            Statement batchStmt = null;
            CancelQueryTask timeoutTask = null;

            long[] var53;
            try {
               long[] updateCounts = new long[nbrCommands];

               int commandIndex;
               for(commandIndex = 0; commandIndex < nbrCommands; ++commandIndex) {
                  updateCounts[commandIndex] = -3L;
               }

               int commandIndex = false;
               StringBuilder queryBuf = new StringBuilder();
               batchStmt = locallyScopedConn.createStatement();
               JdbcStatement jdbcBatchedStmt = (JdbcStatement)batchStmt;
               this.getQueryAttributesBindings().runThroughAll((a) -> {
                  jdbcBatchedStmt.setAttribute(a.getName(), a.getValue());
               });
               timeoutTask = this.startQueryTimer((StatementImpl)batchStmt, individualStatementTimeout);
               int counter = 0;
               String connectionEncoding = (String)locallyScopedConn.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue();
               int numberOfBytesPerChar = StringUtils.startsWithIgnoreCase(connectionEncoding, "utf") ? 3 : (this.session.getServerSession().getCharsetSettings().isMultibyteCharset(connectionEncoding) ? 2 : 1);
               int escapeAdjust = 1;
               batchStmt.setEscapeProcessing(this.doEscapeProcessing);
               if (this.doEscapeProcessing) {
                  escapeAdjust = 2;
               }

               SQLException sqlEx = null;
               int argumentSetsInBatchSoFar = 0;

               for(commandIndex = 0; commandIndex < nbrCommands; ++commandIndex) {
                  String nextQuery = (String)this.query.getBatchedArgs().get(commandIndex);
                  if (((queryBuf.length() + nextQuery.length()) * numberOfBytesPerChar + 1 + 4) * escapeAdjust + 32 > (Integer)this.maxAllowedPacket.getValue()) {
                     try {
                        batchStmt.execute(queryBuf.toString(), 1);
                     } catch (SQLException var47) {
                        sqlEx = this.handleExceptionForBatch(commandIndex, argumentSetsInBatchSoFar, updateCounts, var47);
                     }

                     counter = this.processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts);
                     queryBuf = new StringBuilder();
                     argumentSetsInBatchSoFar = 0;
                  }

                  queryBuf.append(nextQuery);
                  queryBuf.append(";");
                  ++argumentSetsInBatchSoFar;
               }

               if (queryBuf.length() > 0) {
                  try {
                     batchStmt.execute(queryBuf.toString(), 1);
                  } catch (SQLException var46) {
                     sqlEx = this.handleExceptionForBatch(commandIndex - 1, argumentSetsInBatchSoFar, updateCounts, var46);
                  }

                  this.processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts);
               }

               if (timeoutTask != null) {
                  this.stopQueryTimer(timeoutTask, true, true);
                  timeoutTask = null;
               }

               if (sqlEx != null) {
                  throw SQLError.createBatchUpdateException(sqlEx, updateCounts, this.getExceptionInterceptor());
               }

               var53 = updateCounts != null ? updateCounts : new long[0];
            } finally {
               this.stopQueryTimer(timeoutTask, false, false);
               this.resetCancelledState();

               try {
                  if (batchStmt != null) {
                     batchStmt.close();
                  }
               } finally {
                  if (!multiQueriesEnabled) {
                     this.session.disableMultiQueries();
                  }

               }

            }

            return var53;
         }
      } catch (CJException var51) {
         throw SQLExceptionsMapping.translateException(var51, this.getExceptionInterceptor());
      }
   }

   protected int processMultiCountsAndKeys(StatementImpl batchedStatement, int updateCountCounter, long[] updateCounts) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            updateCounts[updateCountCounter++] = batchedStatement.getLargeUpdateCount();
            boolean doGenKeys = this.batchedGeneratedKeys != null;
            byte[][] row = (byte[][])null;
            long generatedKey;
            if (doGenKeys) {
               generatedKey = batchedStatement.getLastInsertID();
               row = new byte[][]{StringUtils.getBytes(Long.toString(generatedKey))};
               this.batchedGeneratedKeys.add(new ByteArrayRow(row, this.getExceptionInterceptor()));
            }

            while(batchedStatement.getMoreResults() || batchedStatement.getLargeUpdateCount() != -1L) {
               updateCounts[updateCountCounter++] = batchedStatement.getLargeUpdateCount();
               if (doGenKeys) {
                  generatedKey = batchedStatement.getLastInsertID();
                  row = new byte[][]{StringUtils.getBytes(Long.toString(generatedKey))};
                  this.batchedGeneratedKeys.add(new ByteArrayRow(row, this.getExceptionInterceptor()));
               }
            }

            return updateCountCounter;
         }
      } catch (CJException var12) {
         throw SQLExceptionsMapping.translateException(var12, this.getExceptionInterceptor());
      }
   }

   protected SQLException handleExceptionForBatch(int endOfBatchIndex, int numValuesPerBatch, long[] updateCounts, SQLException ex) throws BatchUpdateException, SQLException {
      for(int j = endOfBatchIndex; j > endOfBatchIndex - numValuesPerBatch; --j) {
         updateCounts[j] = -3L;
      }

      if (this.continueBatchOnError && !(ex instanceof MySQLTimeoutException) && !(ex instanceof MySQLStatementCancelledException) && !this.hasDeadlockOrTimeoutRolledBackTx(ex)) {
         return ex;
      } else {
         long[] newUpdateCounts = new long[endOfBatchIndex];
         System.arraycopy(updateCounts, 0, newUpdateCounts, 0, endOfBatchIndex);
         throw SQLError.createBatchUpdateException(ex, newUpdateCounts, this.getExceptionInterceptor());
      }
   }

   public ResultSet executeQuery(String sql) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            JdbcConnection locallyScopedConn = this.connection;
            this.retrieveGeneratedKeys = false;
            this.checkNullOrEmptyQuery(sql);
            this.resetCancelledState();
            this.implicitlyCloseAllOpenResults();
            if (sql.charAt(0) == '/' && sql.startsWith("/* ping */")) {
               this.doPingInstead();
               return this.results;
            } else {
               this.setupStreamingTimeout(locallyScopedConn);
               if (this.doEscapeProcessing) {
                  Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.session.getServerSession().getSessionTimeZone(), this.session.getServerSession().getCapabilities().serverSupportsFracSecs(), this.session.getServerSession().isServerTruncatesFracSecs(), this.getExceptionInterceptor());
                  sql = escapedSqlResult instanceof String ? (String)escapedSqlResult : ((EscapeProcessorResult)escapedSqlResult).escapedSql;
               }

               if (!this.isResultSetProducingQuery(sql)) {
                  throw SQLError.createSQLException(Messages.getString("Statement.57"), "S1009", this.getExceptionInterceptor());
               } else {
                  CachedResultSetMetaData cachedMetaData = null;
                  if (this.useServerFetch()) {
                     this.results = this.createResultSetUsingServerFetch(sql);
                     return this.results;
                  } else {
                     CancelQueryTask timeoutTask = null;
                     String oldDb = null;

                     try {
                        timeoutTask = this.startQueryTimer(this, this.getTimeoutInMillis());
                        if (!locallyScopedConn.getDatabase().equals(this.getCurrentDatabase())) {
                           oldDb = locallyScopedConn.getDatabase();
                           locallyScopedConn.setDatabase(this.getCurrentDatabase());
                        }

                        if ((Boolean)locallyScopedConn.getPropertySet().getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()) {
                           cachedMetaData = locallyScopedConn.getCachedMetaData(sql);
                        }

                        locallyScopedConn.setSessionMaxRows(this.maxRows);
                        this.statementBegins();
                        this.results = (ResultSetInternalMethods)((NativeSession)locallyScopedConn.getSession()).execSQL(this, sql, this.maxRows, (NativePacketPayload)null, this.createStreamingResultSet(), this.getResultSetFactory(), cachedMetaData, false);
                        if (timeoutTask != null) {
                           this.stopQueryTimer(timeoutTask, true, true);
                           timeoutTask = null;
                        }
                     } catch (OperationCancelledException | CJTimeoutException var15) {
                        throw SQLExceptionsMapping.translateException(var15, this.exceptionInterceptor);
                     } finally {
                        this.query.getStatementExecuting().set(false);
                        this.stopQueryTimer(timeoutTask, false, false);
                        if (oldDb != null) {
                           locallyScopedConn.setDatabase(oldDb);
                        }

                     }

                     this.lastInsertId = this.results.getUpdateID();
                     if (cachedMetaData != null) {
                        locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
                     } else if ((Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()) {
                        locallyScopedConn.initializeResultsMetadataFromCache(sql, (CachedResultSetMetaData)null, this.results);
                     }

                     return this.results;
                  }
               }
            }
         }
      } catch (CJException var18) {
         throw SQLExceptionsMapping.translateException(var18, this.getExceptionInterceptor());
      }
   }

   protected void doPingInstead() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (this.pingTarget != null) {
               try {
                  this.pingTarget.doPing();
               } catch (SQLException var5) {
                  throw var5;
               } catch (Exception var6) {
                  throw SQLError.createSQLException(var6.getMessage(), "08S01", var6, this.getExceptionInterceptor());
               }
            } else {
               this.connection.ping();
            }

            ResultSetInternalMethods fakeSelectOneResultSet = this.generatePingResultSet();
            this.results = fakeSelectOneResultSet;
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   protected ResultSetInternalMethods generatePingResultSet() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            String encoding = this.session.getServerSession().getCharsetSettings().getMetadataEncoding();
            int collationIndex = this.session.getServerSession().getCharsetSettings().getMetadataCollationIndex();
            Field[] fields = new Field[]{new Field((String)null, "1", collationIndex, encoding, MysqlType.BIGINT, 1)};
            ArrayList<Row> rows = new ArrayList();
            byte[] colVal = new byte[]{49};
            rows.add(new ByteArrayRow(new byte[][]{colVal}, this.getExceptionInterceptor()));
            return this.resultSetFactory.createFromResultsetRows(1007, 1004, new ResultsetRowsStatic(rows, new DefaultColumnDefinition(fields)));
         }
      } catch (CJException var10) {
         throw SQLExceptionsMapping.translateException(var10, this.getExceptionInterceptor());
      }
   }

   public void executeSimpleNonQuery(JdbcConnection c, String nonQuery) throws SQLException {
      try {
         synchronized(c.getConnectionMutex()) {
            ((ResultSetImpl)((NativeSession)c.getSession()).execSQL(this, nonQuery, -1, (NativePacketPayload)null, false, this.getResultSetFactory(), (ColumnDefinition)null, false)).close();
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public int executeUpdate(String sql) throws SQLException {
      try {
         return Util.truncateAndConvertToInt(this.executeLargeUpdate(sql));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   protected long executeUpdateInternal(String sql, boolean isBatch, boolean returnGeneratedKeys) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            JdbcConnection locallyScopedConn = this.connection;
            this.checkNullOrEmptyQuery(sql);
            this.resetCancelledState();
            char firstStatementChar = ParseInfo.firstCharOfStatementUc(sql, this.session.getServerSession().isNoBackslashEscapesSet());
            if (!this.isNonResultSetProducingQuery(sql)) {
               throw SQLError.createSQLException(Messages.getString("Statement.46"), "01S03", this.getExceptionInterceptor());
            } else {
               this.retrieveGeneratedKeys = returnGeneratedKeys;
               this.lastQueryIsOnDupKeyUpdate = returnGeneratedKeys && firstStatementChar == 'I' && this.containsOnDuplicateKeyInString(sql);
               ResultSetInternalMethods rs = null;
               if (this.doEscapeProcessing) {
                  Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.session.getServerSession().getSessionTimeZone(), this.session.getServerSession().getCapabilities().serverSupportsFracSecs(), this.session.getServerSession().isServerTruncatesFracSecs(), this.getExceptionInterceptor());
                  sql = escapedSqlResult instanceof String ? (String)escapedSqlResult : ((EscapeProcessorResult)escapedSqlResult).escapedSql;
               }

               if (locallyScopedConn.isReadOnly(false)) {
                  throw SQLError.createSQLException(Messages.getString("Statement.42") + Messages.getString("Statement.43"), "S1009", this.getExceptionInterceptor());
               } else {
                  this.implicitlyCloseAllOpenResults();
                  CancelQueryTask timeoutTask = null;
                  String oldDb = null;

                  try {
                     timeoutTask = this.startQueryTimer(this, this.getTimeoutInMillis());
                     if (!locallyScopedConn.getDatabase().equals(this.getCurrentDatabase())) {
                        oldDb = locallyScopedConn.getDatabase();
                        locallyScopedConn.setDatabase(this.getCurrentDatabase());
                     }

                     locallyScopedConn.setSessionMaxRows(-1);
                     this.statementBegins();
                     rs = (ResultSetInternalMethods)((NativeSession)locallyScopedConn.getSession()).execSQL(this, sql, -1, (NativePacketPayload)null, false, this.getResultSetFactory(), (ColumnDefinition)null, isBatch);
                     if (timeoutTask != null) {
                        this.stopQueryTimer(timeoutTask, true, true);
                        timeoutTask = null;
                     }
                  } catch (OperationCancelledException | CJTimeoutException var18) {
                     throw SQLExceptionsMapping.translateException(var18, this.exceptionInterceptor);
                  } finally {
                     this.stopQueryTimer(timeoutTask, false, false);
                     if (oldDb != null) {
                        locallyScopedConn.setDatabase(oldDb);
                     }

                     if (!isBatch) {
                        this.query.getStatementExecuting().set(false);
                     }

                  }

                  this.results = rs;
                  rs.setFirstCharOfQuery(firstStatementChar);
                  this.updateCount = rs.getUpdateCount();
                  this.lastInsertId = rs.getUpdateID();
                  return this.updateCount;
               }
            }
         }
      } catch (CJException var21) {
         throw SQLExceptionsMapping.translateException(var21, this.getExceptionInterceptor());
      }
   }

   public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
      try {
         return Util.truncateAndConvertToInt(this.executeLargeUpdate(sql, autoGeneratedKeys));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
      try {
         return Util.truncateAndConvertToInt(this.executeLargeUpdate(sql, columnIndexes));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public int executeUpdate(String sql, String[] columnNames) throws SQLException {
      try {
         return Util.truncateAndConvertToInt(this.executeLargeUpdate(sql, columnNames));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public Connection getConnection() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.connection;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public int getFetchDirection() throws SQLException {
      try {
         return 1000;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public int getFetchSize() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.query.getResultFetchSize();
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public ResultSet getGeneratedKeys() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.retrieveGeneratedKeys) {
               throw SQLError.createSQLException(Messages.getString("Statement.GeneratedKeysNotRequested"), "S1009", this.getExceptionInterceptor());
            } else if (this.batchedGeneratedKeys == null) {
               return this.lastQueryIsOnDupKeyUpdate ? (this.generatedKeysResults = this.getGeneratedKeysInternal(1L)) : (this.generatedKeysResults = this.getGeneratedKeysInternal());
            } else {
               String encoding = this.session.getServerSession().getCharsetSettings().getMetadataEncoding();
               int collationIndex = this.session.getServerSession().getCharsetSettings().getMetadataCollationIndex();
               Field[] fields = new Field[]{new Field("", "GENERATED_KEY", collationIndex, encoding, MysqlType.BIGINT_UNSIGNED, 20)};
               this.generatedKeysResults = this.resultSetFactory.createFromResultsetRows(1007, 1004, new ResultsetRowsStatic(this.batchedGeneratedKeys, new DefaultColumnDefinition(fields)));
               return this.generatedKeysResults;
            }
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   protected ResultSetInternalMethods getGeneratedKeysInternal() throws SQLException {
      long numKeys = this.getLargeUpdateCount();
      return this.getGeneratedKeysInternal(numKeys);
   }

   protected ResultSetInternalMethods getGeneratedKeysInternal(long numKeys) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            String encoding = this.session.getServerSession().getCharsetSettings().getMetadataEncoding();
            int collationIndex = this.session.getServerSession().getCharsetSettings().getMetadataCollationIndex();
            Field[] fields = new Field[]{new Field("", "GENERATED_KEY", collationIndex, encoding, MysqlType.BIGINT_UNSIGNED, 20)};
            ArrayList<Row> rowSet = new ArrayList();
            long beginAt = this.getLastInsertID();
            if (this.results != null) {
               String serverInfo = this.results.getServerInfo();
               if (numKeys > 0L && this.results.getFirstCharOfQuery() == 'R' && serverInfo != null && serverInfo.length() > 0) {
                  numKeys = this.getRecordCountFromInfo(serverInfo);
               }

               if (beginAt != 0L && numKeys > 0L) {
                  for(int i = 0; (long)i < numKeys; ++i) {
                     byte[][] row = new byte[1][];
                     if (beginAt > 0L) {
                        row[0] = StringUtils.getBytes(Long.toString(beginAt));
                     } else {
                        byte[] asBytes = new byte[]{(byte)((int)(beginAt >>> 56)), (byte)((int)(beginAt >>> 48)), (byte)((int)(beginAt >>> 40)), (byte)((int)(beginAt >>> 32)), (byte)((int)(beginAt >>> 24)), (byte)((int)(beginAt >>> 16)), (byte)((int)(beginAt >>> 8)), (byte)((int)(beginAt & 255L))};
                        BigInteger val = new BigInteger(1, asBytes);
                        row[0] = val.toString().getBytes();
                     }

                     rowSet.add(new ByteArrayRow(row, this.getExceptionInterceptor()));
                     beginAt += (long)this.connection.getAutoIncrementIncrement();
                  }
               }
            }

            ResultSetImpl gkRs = this.resultSetFactory.createFromResultsetRows(1007, 1004, new ResultsetRowsStatic(rowSet, new DefaultColumnDefinition(fields)));
            return gkRs;
         }
      } catch (CJException var18) {
         throw SQLExceptionsMapping.translateException(var18, this.getExceptionInterceptor());
      }
   }

   public long getLastInsertID() {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.lastInsertId;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public long getLongUpdateCount() {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (this.results == null) {
               return -1L;
            } else {
               return this.results.hasRows() ? -1L : this.updateCount;
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public int getMaxFieldSize() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.maxFieldSize;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public int getMaxRows() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.maxRows <= 0 ? 0 : this.maxRows;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public boolean getMoreResults() throws SQLException {
      try {
         return this.getMoreResults(1);
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean getMoreResults(int current) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (this.results == null) {
               return false;
            } else {
               boolean streamingMode = this.createStreamingResultSet();
               if (streamingMode && this.results.hasRows()) {
                  while(true) {
                     if (this.results.next()) {
                        continue;
                     }
                  }
               }

               ResultSetInternalMethods nextResultSet = (ResultSetInternalMethods)this.results.getNextResultset();
               switch (current) {
                  case 1:
                     if (this.results != null) {
                        if (!streamingMode && !(Boolean)this.dontTrackOpenResources.getValue()) {
                           this.results.realClose(false);
                        }

                        this.results.clearNextResultset();
                     }
                     break;
                  case 2:
                     if (!(Boolean)this.dontTrackOpenResources.getValue()) {
                        this.openResults.add(this.results);
                     }

                     this.results.clearNextResultset();
                     break;
                  case 3:
                     if (this.results != null) {
                        if (!streamingMode && !(Boolean)this.dontTrackOpenResources.getValue()) {
                           this.results.realClose(false);
                        }

                        this.results.clearNextResultset();
                     }

                     this.closeAllOpenResults();
                     break;
                  default:
                     throw SQLError.createSQLException(Messages.getString("Statement.19"), "S1009", this.getExceptionInterceptor());
               }

               this.results = nextResultSet;
               if (this.results == null) {
                  this.updateCount = -1L;
                  this.lastInsertId = -1L;
               } else if (this.results.hasRows()) {
                  this.updateCount = -1L;
                  this.lastInsertId = -1L;
               } else {
                  this.updateCount = this.results.getUpdateCount();
                  this.lastInsertId = this.results.getUpdateID();
               }

               boolean moreResults = this.results != null && this.results.hasRows();
               if (!moreResults) {
                  this.checkAndPerformCloseOnCompletionAction();
               }

               return moreResults;
            }
         }
      } catch (CJException var9) {
         throw SQLExceptionsMapping.translateException(var9, this.getExceptionInterceptor());
      }
   }

   public int getQueryTimeout() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.getTimeoutInMillis() / 1000;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   private long getRecordCountFromInfo(String serverInfo) {
      StringBuilder recordsBuf = new StringBuilder();
      long recordsCount = 0L;
      long duplicatesCount = 0L;
      char c = 0;
      int length = serverInfo.length();

      int i;
      for(i = 0; i < length; ++i) {
         c = serverInfo.charAt(i);
         if (Character.isDigit(c)) {
            break;
         }
      }

      recordsBuf.append(c);
      ++i;

      while(i < length) {
         c = serverInfo.charAt(i);
         if (!Character.isDigit(c)) {
            break;
         }

         recordsBuf.append(c);
         ++i;
      }

      recordsCount = Long.parseLong(recordsBuf.toString());

      StringBuilder duplicatesBuf;
      for(duplicatesBuf = new StringBuilder(); i < length; ++i) {
         c = serverInfo.charAt(i);
         if (Character.isDigit(c)) {
            break;
         }
      }

      duplicatesBuf.append(c);
      ++i;

      while(i < length) {
         c = serverInfo.charAt(i);
         if (!Character.isDigit(c)) {
            break;
         }

         duplicatesBuf.append(c);
         ++i;
      }

      duplicatesCount = Long.parseLong(duplicatesBuf.toString());
      return recordsCount - duplicatesCount;
   }

   public ResultSet getResultSet() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.results != null && this.results.hasRows() ? this.results : null;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public int getResultSetConcurrency() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.resultSetConcurrency;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public int getResultSetHoldability() throws SQLException {
      try {
         return 1;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   protected ResultSetInternalMethods getResultSetInternal() {
      try {
         try {
            synchronized(this.checkClosed().getConnectionMutex()) {
               return this.results;
            }
         } catch (StatementIsClosedException var5) {
            return this.results;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public int getResultSetType() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.query.getResultType().getIntValue();
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public int getUpdateCount() throws SQLException {
      try {
         return Util.truncateAndConvertToInt(this.getLargeUpdateCount());
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public SQLWarning getWarnings() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (this.isClearWarningsCalled()) {
               return null;
            } else {
               SQLWarning pendingWarningsFromServer = this.session.getProtocol().convertShowWarningsToSQLWarnings(false);
               if (this.warningChain != null) {
                  this.warningChain.setNextWarning(pendingWarningsFromServer);
               } else {
                  this.warningChain = pendingWarningsFromServer;
               }

               return this.warningChain;
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   protected void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
      JdbcConnection locallyScopedConn = this.connection;
      if (locallyScopedConn != null && !this.isClosed) {
         if (!(Boolean)this.dontTrackOpenResources.getValue()) {
            locallyScopedConn.unregisterStatement(this);
         }

         if (this.useUsageAdvisor && !calledExplicitly) {
            this.session.getProfilerEventHandler().processEvent((byte)0, this.session, this, (Resultset)null, 0L, new Throwable(), Messages.getString("Statement.63"));
         }

         if (closeOpenResults) {
            closeOpenResults = !this.holdResultsOpenOverClose && !(Boolean)this.dontTrackOpenResources.getValue();
         }

         if (closeOpenResults) {
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

            this.closeAllOpenResults();
         }

         this.clearAttributes();
         this.isClosed = true;
         this.closeQuery();
         this.results = null;
         this.generatedKeysResults = null;
         this.connection = null;
         this.session = null;
         this.warningChain = null;
         this.openResults = null;
         this.batchedGeneratedKeys = null;
         this.pingTarget = null;
         this.resultSetFactory = null;
      }
   }

   public void setCursorName(String name) throws SQLException {
      try {
         ;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void setEscapeProcessing(boolean enable) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.doEscapeProcessing = enable;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void setFetchDirection(int direction) throws SQLException {
      try {
         switch (direction) {
            case 1000:
            case 1001:
            case 1002:
               return;
            default:
               throw SQLError.createSQLException(Messages.getString("Statement.5"), "S1009", this.getExceptionInterceptor());
         }
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void setFetchSize(int rows) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if ((rows >= 0 || rows == Integer.MIN_VALUE) && (this.maxRows <= 0 || rows <= this.getMaxRows())) {
               this.query.setResultFetchSize(rows);
            } else {
               throw SQLError.createSQLException(Messages.getString("Statement.7"), "S1009", this.getExceptionInterceptor());
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void setHoldResultsOpenOverClose(boolean holdResultsOpenOverClose) {
      try {
         try {
            synchronized(this.checkClosed().getConnectionMutex()) {
               this.holdResultsOpenOverClose = holdResultsOpenOverClose;
            }
         } catch (StatementIsClosedException var6) {
         }

      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void setMaxFieldSize(int max) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (max < 0) {
               throw SQLError.createSQLException(Messages.getString("Statement.11"), "S1009", this.getExceptionInterceptor());
            } else {
               int maxBuf = (Integer)this.maxAllowedPacket.getValue();
               if (max > maxBuf) {
                  throw SQLError.createSQLException(Messages.getString("Statement.13", new Object[]{(long)maxBuf}), "S1009", this.getExceptionInterceptor());
               } else {
                  this.maxFieldSize = max;
               }
            }
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void setMaxRows(int max) throws SQLException {
      try {
         this.setLargeMaxRows((long)max);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void setQueryTimeout(int seconds) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (seconds < 0) {
               throw SQLError.createSQLException(Messages.getString("Statement.21"), "S1009", this.getExceptionInterceptor());
            } else {
               this.setTimeoutInMillis(seconds * 1000);
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   void setResultSetConcurrency(int concurrencyFlag) throws SQLException {
      try {
         try {
            synchronized(this.checkClosed().getConnectionMutex()) {
               this.resultSetConcurrency = concurrencyFlag;
               this.resultSetFactory = new ResultSetFactory(this.connection, this);
            }
         } catch (StatementIsClosedException var6) {
         }

      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   void setResultSetType(Resultset.Type typeFlag) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.query.setResultType(typeFlag);
            this.resultSetFactory = new ResultSetFactory(this.connection, this);
         }
      } catch (StatementIsClosedException var5) {
      }

   }

   void setResultSetType(int typeFlag) throws SQLException {
      this.query.setResultType(Resultset.Type.fromValue(typeFlag, Resultset.Type.FORWARD_ONLY));
   }

   protected void getBatchedGeneratedKeys(Statement batchedStatement) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (this.retrieveGeneratedKeys) {
               ResultSet rs = null;

               try {
                  rs = batchedStatement.getGeneratedKeys();

                  while(rs.next()) {
                     this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][]{rs.getBytes(1)}, this.getExceptionInterceptor()));
                  }
               } finally {
                  if (rs != null) {
                     rs.close();
                  }

               }
            }

         }
      } catch (CJException var12) {
         throw SQLExceptionsMapping.translateException(var12, this.getExceptionInterceptor());
      }
   }

   protected void getBatchedGeneratedKeys(int maxKeys) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (this.retrieveGeneratedKeys) {
               ResultSet rs = null;

               try {
                  rs = maxKeys == 0 ? this.getGeneratedKeysInternal() : this.getGeneratedKeysInternal((long)maxKeys);

                  while(rs.next()) {
                     this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][]{rs.getBytes(1)}, this.getExceptionInterceptor()));
                  }
               } finally {
                  this.isImplicitlyClosingResults = true;

                  try {
                     if (rs != null) {
                        rs.close();
                     }
                  } finally {
                     this.isImplicitlyClosingResults = false;
                  }

               }
            }

         }
      } catch (CJException var27) {
         throw SQLExceptionsMapping.translateException(var27, this.getExceptionInterceptor());
      }
   }

   private boolean useServerFetch() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return (Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.useCursorFetch).getValue() && this.query.getResultFetchSize() > 0 && this.query.getResultType() == Resultset.Type.FORWARD_ONLY;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public boolean isClosed() throws SQLException {
      try {
         JdbcConnection locallyScopedConn = this.connection;
         if (locallyScopedConn == null) {
            return true;
         } else {
            synchronized(locallyScopedConn.getConnectionMutex()) {
               return this.isClosed;
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public boolean isPoolable() throws SQLException {
      try {
         this.checkClosed();
         return this.isPoolable;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void setPoolable(boolean poolable) throws SQLException {
      try {
         this.checkClosed();
         this.isPoolable = poolable;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
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

   public <T> T unwrap(Class<T> iface) throws SQLException {
      try {
         try {
            return iface.cast(this);
         } catch (ClassCastException var4) {
            throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[]{iface.toString()}), "S1009", this.getExceptionInterceptor());
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public InputStream getLocalInfileInputStream() {
      return this.session.getLocalInfileInputStream();
   }

   public void setLocalInfileInputStream(InputStream stream) {
      this.session.setLocalInfileInputStream(stream);
   }

   public void setPingTarget(PingTarget pingTarget) {
      this.pingTarget = pingTarget;
   }

   public ExceptionInterceptor getExceptionInterceptor() {
      return this.exceptionInterceptor;
   }

   protected boolean containsOnDuplicateKeyInString(String sql) {
      return ParseInfo.getOnDuplicateKeyLocation(sql, this.dontCheckOnDuplicateKeyUpdateInSQL, (Boolean)this.rewriteBatchedStatements.getValue(), this.session.getServerSession().isNoBackslashEscapesSet()) != -1;
   }

   public void closeOnCompletion() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.closeOnCompletion = true;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public boolean isCloseOnCompletion() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.closeOnCompletion;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public long[] executeLargeBatch() throws SQLException {
      try {
         return this.executeBatchInternal();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public long executeLargeUpdate(String sql) throws SQLException {
      try {
         return this.executeUpdateInternal(sql, false, false);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
      try {
         return this.executeUpdateInternal(sql, false, autoGeneratedKeys == 1);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
      try {
         return this.executeUpdateInternal(sql, false, columnIndexes != null && columnIndexes.length > 0);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
      try {
         return this.executeUpdateInternal(sql, false, columnNames != null && columnNames.length > 0);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public long getLargeMaxRows() throws SQLException {
      try {
         return (long)this.getMaxRows();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public long getLargeUpdateCount() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (this.results == null) {
               return -1L;
            } else {
               return this.results.hasRows() ? -1L : this.results.getUpdateCount();
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void setLargeMaxRows(long max) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (max <= 50000000L && max >= 0L) {
               if (max == 0L) {
                  max = -1L;
               }

               this.maxRows = (int)max;
            } else {
               throw SQLError.createSQLException(Messages.getString("Statement.15") + max + " > " + 50000000 + ".", "S1009", this.getExceptionInterceptor());
            }
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public String getCurrentDatabase() {
      return this.query.getCurrentDatabase();
   }

   public long getServerStatementId() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("Statement.65"));
   }

   public <T extends Resultset, M extends Message> ProtocolEntityFactory<T, M> getResultSetFactory() {
      return this.resultSetFactory;
   }

   public int getId() {
      return this.query.getId();
   }

   public void setCancelStatus(Query.CancelStatus cs) {
      this.query.setCancelStatus(cs);
   }

   public void checkCancelTimeout() {
      try {
         this.query.checkCancelTimeout();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public Session getSession() {
      return this.session;
   }

   public Object getCancelTimeoutMutex() {
      return this.query.getCancelTimeoutMutex();
   }

   public void closeQuery() {
      if (this.query != null) {
         this.query.closeQuery();
      }

   }

   public int getResultFetchSize() {
      return this.query.getResultFetchSize();
   }

   public void setResultFetchSize(int fetchSize) {
      this.query.setResultFetchSize(fetchSize);
   }

   public Resultset.Type getResultType() {
      return this.query.getResultType();
   }

   public void setResultType(Resultset.Type resultSetType) {
      this.query.setResultType(resultSetType);
   }

   public int getTimeoutInMillis() {
      return this.query.getTimeoutInMillis();
   }

   public void setTimeoutInMillis(int timeoutInMillis) {
      this.query.setTimeoutInMillis(timeoutInMillis);
   }

   public long getExecuteTime() {
      return this.query.getExecuteTime();
   }

   public void setExecuteTime(long executeTime) {
      this.query.setExecuteTime(executeTime);
   }

   public AtomicBoolean getStatementExecuting() {
      return this.query.getStatementExecuting();
   }

   public void setCurrentDatabase(String currentDb) {
      this.query.setCurrentDatabase(currentDb);
   }

   public boolean isClearWarningsCalled() {
      return this.query.isClearWarningsCalled();
   }

   public void setClearWarningsCalled(boolean clearWarningsCalled) {
      this.query.setClearWarningsCalled(clearWarningsCalled);
   }

   public Query getQuery() {
      return this.query;
   }

   public QueryAttributesBindings getQueryAttributesBindings() {
      return this.query.getQueryAttributesBindings();
   }

   public void setAttribute(String name, Object value) {
      this.getQueryAttributesBindings().setAttribute(name, value);
   }

   public void clearAttributes() {
      this.getQueryAttributesBindings().clearAttributes();
   }
}
