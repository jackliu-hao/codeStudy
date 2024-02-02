package com.mysql.cj;

import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ConnectionIsClosedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.ExceptionInterceptorChain;
import com.mysql.cj.exceptions.OperationCancelledException;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.NetworkResources;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.SocketConnection;
import com.mysql.cj.protocol.SocketFactory;
import com.mysql.cj.protocol.a.NativeMessageBuilder;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.protocol.a.NativeProtocol;
import com.mysql.cj.protocol.a.NativeServerSession;
import com.mysql.cj.protocol.a.NativeSocketConnection;
import com.mysql.cj.protocol.a.ResultsetFactory;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.LongValueFactory;
import com.mysql.cj.result.Row;
import com.mysql.cj.result.StringValueFactory;
import com.mysql.cj.result.ValueFactory;
import com.mysql.cj.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class NativeSession extends CoreSession implements Serializable {
   private static final long serialVersionUID = 5323638898749073419L;
   private CacheAdapter<String, Map<String, String>> serverConfigCache;
   private long lastQueryFinishedTime = 0L;
   private boolean needsPing = false;
   private NativeMessageBuilder commandBuilder = null;
   private boolean isClosed = true;
   private Throwable forceClosedReason;
   private CopyOnWriteArrayList<WeakReference<Session.SessionEventListener>> listeners = new CopyOnWriteArrayList();
   private transient Timer cancelTimer;
   private static final String SERVER_VERSION_STRING_VAR_NAME = "server_version_string";

   public NativeSession(HostInfo hostInfo, PropertySet propSet) {
      super(hostInfo, propSet);
   }

   public void connect(HostInfo hi, String user, String password, String database, int loginTimeout, TransactionEventHandler transactionManager) throws IOException {
      this.hostInfo = hi;
      this.setSessionMaxRows(-1);
      SocketConnection socketConnection = new NativeSocketConnection();
      socketConnection.connect(this.hostInfo.getHost(), this.hostInfo.getPort(), this.propertySet, this.getExceptionInterceptor(), this.log, loginTimeout);
      if (this.protocol == null) {
         this.protocol = NativeProtocol.getInstance(this, socketConnection, this.propertySet, this.log, transactionManager);
      } else {
         this.protocol.init(this, socketConnection, this.propertySet, transactionManager);
      }

      this.protocol.connect(user, password, database);
      this.isClosed = false;
      this.commandBuilder = new NativeMessageBuilder(this.getServerSession().supportsQueryAttributes());
   }

   public NativeProtocol getProtocol() {
      return (NativeProtocol)this.protocol;
   }

   public void quit() {
      if (this.protocol != null) {
         try {
            ((NativeProtocol)this.protocol).quit();
         } catch (Exception var3) {
         }
      }

      synchronized(this) {
         if (this.cancelTimer != null) {
            this.cancelTimer.cancel();
            this.cancelTimer = null;
         }
      }

      this.isClosed = true;
      super.quit();
   }

   public void forceClose() {
      if (this.protocol != null) {
         try {
            this.protocol.getSocketConnection().forceClose();
            ((NativeProtocol)this.protocol).releaseResources();
         } catch (Throwable var3) {
         }
      }

      synchronized(this) {
         if (this.cancelTimer != null) {
            this.cancelTimer.cancel();
            this.cancelTimer = null;
         }
      }

      this.isClosed = true;
      super.forceClose();
   }

   public void enableMultiQueries() {
      this.sendCommand(this.commandBuilder.buildComSetOption(((NativeProtocol)this.protocol).getSharedSendPacket(), 0), false, 0);
      ((NativeServerSession)this.getServerSession()).preserveOldTransactionState();
   }

   public void disableMultiQueries() {
      this.sendCommand(this.commandBuilder.buildComSetOption(((NativeProtocol)this.protocol).getSharedSendPacket(), 1), false, 0);
      ((NativeServerSession)this.getServerSession()).preserveOldTransactionState();
   }

   public boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag) {
      return ((NativeServerSession)this.protocol.getServerSession()).isSetNeededForAutoCommitMode(autoCommitFlag, false);
   }

   public int getSessionMaxRows() {
      return this.sessionMaxRows;
   }

   public void setSessionMaxRows(int sessionMaxRows) {
      this.sessionMaxRows = sessionMaxRows;
   }

   public void setQueryInterceptors(List<QueryInterceptor> queryInterceptors) {
      ((NativeProtocol)this.protocol).setQueryInterceptors(queryInterceptors);
   }

   public boolean isServerLocal(Session sess) {
      SocketFactory factory = this.protocol.getSocketConnection().getSocketFactory();
      return factory.isLocallyConnected(sess);
   }

   public void shutdownServer() {
      if (this.versionMeetsMinimum(5, 7, 9)) {
         this.sendCommand(this.commandBuilder.buildComQuery(this.getSharedSendPacket(), "SHUTDOWN"), false, 0);
      } else {
         this.sendCommand(this.commandBuilder.buildComShutdown(this.getSharedSendPacket()), false, 0);
      }

   }

   public void setSocketTimeout(int milliseconds) {
      this.getPropertySet().getProperty(PropertyKey.socketTimeout).setValue(milliseconds);
      ((NativeProtocol)this.protocol).setSocketTimeout(milliseconds);
   }

   public int getSocketTimeout() {
      RuntimeProperty<Integer> sto = this.getPropertySet().getProperty(PropertyKey.socketTimeout);
      return (Integer)sto.getValue();
   }

   public NativePacketPayload getSharedSendPacket() {
      return ((NativeProtocol)this.protocol).getSharedSendPacket();
   }

   public void dumpPacketRingBuffer() {
      ((NativeProtocol)this.protocol).dumpPacketRingBuffer();
   }

   public <T extends Resultset> T invokeQueryInterceptorsPre(Supplier<String> sql, Query interceptedQuery, boolean forceExecute) {
      return ((NativeProtocol)this.protocol).invokeQueryInterceptorsPre(sql, interceptedQuery, forceExecute);
   }

   public <T extends Resultset> T invokeQueryInterceptorsPost(Supplier<String> sql, Query interceptedQuery, T originalResultSet, boolean forceExecute) {
      return ((NativeProtocol)this.protocol).invokeQueryInterceptorsPost(sql, interceptedQuery, originalResultSet, forceExecute);
   }

   public boolean shouldIntercept() {
      return ((NativeProtocol)this.protocol).getQueryInterceptors() != null;
   }

   public long getCurrentTimeNanosOrMillis() {
      return ((NativeProtocol)this.protocol).getCurrentTimeNanosOrMillis();
   }

   public final NativePacketPayload sendCommand(NativePacketPayload queryPacket, boolean skipCheck, int timeoutMillis) {
      return (NativePacketPayload)this.protocol.sendCommand(queryPacket, skipCheck, timeoutMillis);
   }

   public long getSlowQueryThreshold() {
      return ((NativeProtocol)this.protocol).getSlowQueryThreshold();
   }

   public boolean hadWarnings() {
      return ((NativeProtocol)this.protocol).hadWarnings();
   }

   public void clearInputStream() {
      ((NativeProtocol)this.protocol).clearInputStream();
   }

   public NetworkResources getNetworkResources() {
      return this.protocol.getSocketConnection().getNetworkResources();
   }

   public boolean isSSLEstablished() {
      return this.protocol.getSocketConnection().isSSLEstablished();
   }

   public int getCommandCount() {
      return ((NativeProtocol)this.protocol).getCommandCount();
   }

   public SocketAddress getRemoteSocketAddress() {
      try {
         return this.protocol.getSocketConnection().getMysqlSocket().getRemoteSocketAddress();
      } catch (IOException var2) {
         throw new CJCommunicationsException(var2);
      }
   }

   public InputStream getLocalInfileInputStream() {
      return this.protocol.getLocalInfileInputStream();
   }

   public void setLocalInfileInputStream(InputStream stream) {
      this.protocol.setLocalInfileInputStream(stream);
   }

   private void createConfigCacheIfNeeded(Object syncMutex) {
      synchronized(syncMutex) {
         if (this.serverConfigCache == null) {
            try {
               Class<?> factoryClass = Class.forName(this.getPropertySet().getStringProperty(PropertyKey.serverConfigCacheFactory).getStringValue());
               CacheAdapterFactory<String, Map<String, String>> cacheFactory = (CacheAdapterFactory)factoryClass.newInstance();
               this.serverConfigCache = cacheFactory.getInstance(syncMutex, this.hostInfo.getDatabaseUrl(), Integer.MAX_VALUE, Integer.MAX_VALUE);
               ExceptionInterceptor evictOnCommsError = new ExceptionInterceptor() {
                  public ExceptionInterceptor init(Properties config, Log log1) {
                     return this;
                  }

                  public void destroy() {
                  }

                  public Exception interceptException(Exception sqlEx) {
                     if (sqlEx instanceof SQLException && ((SQLException)sqlEx).getSQLState() != null && ((SQLException)sqlEx).getSQLState().startsWith("08")) {
                        NativeSession.this.serverConfigCache.invalidate(NativeSession.this.hostInfo.getDatabaseUrl());
                     }

                     return null;
                  }
               };
               if (this.exceptionInterceptor == null) {
                  this.exceptionInterceptor = evictOnCommsError;
               } else {
                  ((ExceptionInterceptorChain)this.exceptionInterceptor).addRingZero(evictOnCommsError);
               }
            } catch (ClassNotFoundException var7) {
               throw ExceptionFactory.createException((String)Messages.getString("Connection.CantFindCacheFactory", new Object[]{this.getPropertySet().getStringProperty(PropertyKey.parseInfoCacheFactory).getValue(), PropertyKey.parseInfoCacheFactory}), (Throwable)var7, (ExceptionInterceptor)this.getExceptionInterceptor());
            } catch (IllegalAccessException | CJException | InstantiationException var8) {
               throw ExceptionFactory.createException((String)Messages.getString("Connection.CantLoadCacheFactory", new Object[]{this.getPropertySet().getStringProperty(PropertyKey.parseInfoCacheFactory).getValue(), PropertyKey.parseInfoCacheFactory}), (Throwable)var8, (ExceptionInterceptor)this.getExceptionInterceptor());
            }

         }
      }
   }

   public void loadServerVariables(Object syncMutex, String version) {
      if ((Boolean)this.cacheServerConfiguration.getValue()) {
         this.createConfigCacheIfNeeded(syncMutex);
         Map<String, String> cachedVariableMap = (Map)this.serverConfigCache.get(this.hostInfo.getDatabaseUrl());
         if (cachedVariableMap != null) {
            String cachedServerVersion = (String)cachedVariableMap.get("server_version_string");
            if (cachedServerVersion != null && this.getServerSession().getServerVersion() != null && cachedServerVersion.equals(this.getServerSession().getServerVersion().toString())) {
               Map<String, String> localVariableMap = this.protocol.getServerSession().getServerVariables();
               Map<String, String> newLocalVariableMap = new HashMap();
               newLocalVariableMap.putAll(cachedVariableMap);
               newLocalVariableMap.putAll(localVariableMap);
               this.protocol.getServerSession().setServerVariables(newLocalVariableMap);
               return;
            }

            this.serverConfigCache.invalidate(this.hostInfo.getDatabaseUrl());
         }
      }

      try {
         if (version != null && version.indexOf(42) != -1) {
            StringBuilder buf = new StringBuilder(version.length() + 10);

            for(int i = 0; i < version.length(); ++i) {
               char c = version.charAt(i);
               buf.append(c == '*' ? "[star]" : c);
            }

            version = buf.toString();
         }

         String versionComment = !(Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue() && version != null ? "/* " + version + " */" : "";
         this.protocol.getServerSession().setServerVariables(new HashMap());
         if (this.versionMeetsMinimum(5, 1, 0)) {
            StringBuilder queryBuf = (new StringBuilder(versionComment)).append("SELECT");
            queryBuf.append("  @@session.auto_increment_increment AS auto_increment_increment");
            queryBuf.append(", @@character_set_client AS character_set_client");
            queryBuf.append(", @@character_set_connection AS character_set_connection");
            queryBuf.append(", @@character_set_results AS character_set_results");
            queryBuf.append(", @@character_set_server AS character_set_server");
            queryBuf.append(", @@collation_server AS collation_server");
            queryBuf.append(", @@collation_connection AS collation_connection");
            queryBuf.append(", @@init_connect AS init_connect");
            queryBuf.append(", @@interactive_timeout AS interactive_timeout");
            if (!this.versionMeetsMinimum(5, 5, 0)) {
               queryBuf.append(", @@language AS language");
            }

            queryBuf.append(", @@license AS license");
            queryBuf.append(", @@lower_case_table_names AS lower_case_table_names");
            queryBuf.append(", @@max_allowed_packet AS max_allowed_packet");
            queryBuf.append(", @@net_write_timeout AS net_write_timeout");
            queryBuf.append(", @@performance_schema AS performance_schema");
            if (!this.versionMeetsMinimum(8, 0, 3)) {
               queryBuf.append(", @@query_cache_size AS query_cache_size");
               queryBuf.append(", @@query_cache_type AS query_cache_type");
            }

            queryBuf.append(", @@sql_mode AS sql_mode");
            queryBuf.append(", @@system_time_zone AS system_time_zone");
            queryBuf.append(", @@time_zone AS time_zone");
            if (!this.versionMeetsMinimum(8, 0, 3) && (!this.versionMeetsMinimum(5, 7, 20) || this.versionMeetsMinimum(8, 0, 0))) {
               queryBuf.append(", @@tx_isolation AS transaction_isolation");
            } else {
               queryBuf.append(", @@transaction_isolation AS transaction_isolation");
            }

            queryBuf.append(", @@wait_timeout AS wait_timeout");
            NativePacketPayload resultPacket = this.sendCommand(this.commandBuilder.buildComQuery((NativePacketPayload)null, (String)queryBuf.toString()), false, 0);
            Resultset rs = ((NativeProtocol)this.protocol).readAllResults(-1, false, resultPacket, false, (ColumnDefinition)null, new ResultsetFactory(Resultset.Type.FORWARD_ONLY, (Resultset.Concurrency)null));
            Field[] f = rs.getColumnDefinition().getFields();
            if (f.length > 0) {
               ValueFactory<String> vf = new StringValueFactory(this.propertySet);
               Row r;
               if ((r = (Row)rs.getRows().next()) != null) {
                  for(int i = 0; i < f.length; ++i) {
                     String value = (String)r.getValue(i, vf);
                     this.protocol.getServerSession().getServerVariables().put(f[i].getColumnLabel(), "utf8mb3".equalsIgnoreCase(value) ? "utf8" : value);
                  }
               }
            }
         } else {
            NativePacketPayload resultPacket = this.sendCommand(this.commandBuilder.buildComQuery((NativePacketPayload)null, (String)(versionComment + "SHOW VARIABLES")), false, 0);
            Resultset rs = ((NativeProtocol)this.protocol).readAllResults(-1, false, resultPacket, false, (ColumnDefinition)null, new ResultsetFactory(Resultset.Type.FORWARD_ONLY, (Resultset.Concurrency)null));
            ValueFactory<String> vf = new StringValueFactory(this.propertySet);

            Row r;
            while((r = (Row)rs.getRows().next()) != null) {
               this.protocol.getServerSession().getServerVariables().put(r.getValue(0, vf), r.getValue(1, vf));
            }
         }
      } catch (IOException var12) {
         throw ExceptionFactory.createException((String)var12.getMessage(), (Throwable)var12);
      }

      if ((Boolean)this.cacheServerConfiguration.getValue()) {
         this.protocol.getServerSession().getServerVariables().put("server_version_string", this.getServerSession().getServerVersion().toString());
         Map<String, String> localVariableMap = new HashMap();
         localVariableMap.putAll(this.protocol.getServerSession().getServerVariables());
         this.serverConfigCache.put(this.hostInfo.getDatabaseUrl(), Collections.unmodifiableMap(localVariableMap));
      }

   }

   public void setSessionVariables() {
      String sessionVariables = (String)this.getPropertySet().getStringProperty(PropertyKey.sessionVariables).getValue();
      if (sessionVariables != null) {
         List<String> variablesToSet = new ArrayList();
         Iterator var3 = StringUtils.split(sessionVariables, ",", "\"'(", "\"')", "\"'", true).iterator();

         String separator;
         while(var3.hasNext()) {
            separator = (String)var3.next();
            variablesToSet.addAll(StringUtils.split(separator, ";", "\"'(", "\"')", "\"'", true));
         }

         if (!variablesToSet.isEmpty()) {
            StringBuilder query = new StringBuilder("SET ");
            separator = "";
            Iterator var5 = variablesToSet.iterator();

            while(var5.hasNext()) {
               String variableToSet = (String)var5.next();
               if (variableToSet.length() > 0) {
                  query.append(separator);
                  if (!variableToSet.startsWith("@")) {
                     query.append("SESSION ");
                  }

                  query.append(variableToSet);
                  separator = ",";
               }
            }

            this.sendCommand(this.commandBuilder.buildComQuery((NativePacketPayload)null, (String)query.toString()), false, 0);
         }
      }

   }

   public String getProcessHost() {
      try {
         long threadId = this.getThreadId();
         String processHost = this.findProcessHost(threadId);
         if (processHost == null) {
            this.log.logWarn(String.format("Connection id %d not found in \"SHOW PROCESSLIST\", assuming 32-bit overflow, using SELECT CONNECTION_ID() instead", threadId));
            NativePacketPayload resultPacket = this.sendCommand(this.commandBuilder.buildComQuery((NativePacketPayload)null, (String)"SELECT CONNECTION_ID()"), false, 0);
            Resultset rs = ((NativeProtocol)this.protocol).readAllResults(-1, false, resultPacket, false, (ColumnDefinition)null, new ResultsetFactory(Resultset.Type.FORWARD_ONLY, (Resultset.Concurrency)null));
            ValueFactory<Long> lvf = new LongValueFactory(this.getPropertySet());
            Row r;
            if ((r = (Row)rs.getRows().next()) != null) {
               threadId = (Long)r.getValue(0, lvf);
               processHost = this.findProcessHost(threadId);
            } else {
               this.log.logError("No rows returned for statement \"SELECT CONNECTION_ID()\", local connection check will most likely be incorrect");
            }
         }

         if (processHost == null) {
            this.log.logWarn(String.format("Cannot find process listing for connection %d in SHOW PROCESSLIST output, unable to determine if locally connected", threadId));
         }

         return processHost;
      } catch (IOException var8) {
         throw ExceptionFactory.createException((String)var8.getMessage(), (Throwable)var8);
      }
   }

   private String findProcessHost(long threadId) {
      try {
         String processHost = null;
         String ps = this.protocol.getServerSession().getServerVariable("performance_schema");
         NativePacketPayload resultPacket = !this.versionMeetsMinimum(5, 6, 0) || ps == null || !"1".contentEquals(ps) && !"ON".contentEquals(ps) ? this.sendCommand(this.commandBuilder.buildComQuery((NativePacketPayload)null, (String)"SHOW PROCESSLIST"), false, 0) : this.sendCommand(this.commandBuilder.buildComQuery((NativePacketPayload)null, (String)("select PROCESSLIST_ID, PROCESSLIST_USER, PROCESSLIST_HOST from performance_schema.threads where PROCESSLIST_ID=" + threadId)), false, 0);
         Resultset rs = ((NativeProtocol)this.protocol).readAllResults(-1, false, resultPacket, false, (ColumnDefinition)null, new ResultsetFactory(Resultset.Type.FORWARD_ONLY, (Resultset.Concurrency)null));
         ValueFactory<Long> lvf = new LongValueFactory(this.getPropertySet());
         ValueFactory<String> svf = new StringValueFactory(this.propertySet);

         Row r;
         while((r = (Row)rs.getRows().next()) != null) {
            long id = (Long)r.getValue(0, lvf);
            if (threadId == id) {
               processHost = (String)r.getValue(2, svf);
               break;
            }
         }

         return processHost;
      } catch (IOException var12) {
         throw ExceptionFactory.createException((String)var12.getMessage(), (Throwable)var12);
      }
   }

   public String queryServerVariable(String varName) {
      try {
         NativePacketPayload resultPacket = this.sendCommand(this.commandBuilder.buildComQuery((NativePacketPayload)null, (String)("SELECT " + varName)), false, 0);
         Resultset rs = ((NativeProtocol)this.protocol).readAllResults(-1, false, resultPacket, false, (ColumnDefinition)null, new ResultsetFactory(Resultset.Type.FORWARD_ONLY, (Resultset.Concurrency)null));
         ValueFactory<String> svf = new StringValueFactory(this.propertySet);
         Row r;
         if ((r = (Row)rs.getRows().next()) != null) {
            String s = (String)r.getValue(0, svf);
            if (s != null) {
               return s;
            }
         }

         return null;
      } catch (IOException var7) {
         throw ExceptionFactory.createException((String)var7.getMessage(), (Throwable)var7);
      }
   }

   public <T extends Resultset> T execSQL(Query callingQuery, String query, int maxRows, NativePacketPayload packet, boolean streamResults, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory, ColumnDefinition cachedMetadata, boolean isBatch) {
      long queryStartTime = (Boolean)this.gatherPerfMetrics.getValue() ? System.currentTimeMillis() : 0L;
      int endOfQueryPacketPosition = packet != null ? packet.getPosition() : 0;
      this.lastQueryFinishedTime = 0L;
      if ((Boolean)this.autoReconnect.getValue() && (this.getServerSession().isAutoCommit() || (Boolean)this.autoReconnectForPools.getValue()) && this.needsPing && !isBatch) {
         try {
            this.ping(false, 0);
            this.needsPing = false;
         } catch (Exception var20) {
            this.invokeReconnectListeners();
         }
      }

      Resultset var12;
      try {
         var12 = packet == null ? ((NativeProtocol)this.protocol).sendQueryString(callingQuery, query, (String)this.characterEncoding.getValue(), maxRows, streamResults, cachedMetadata, resultSetFactory) : ((NativeProtocol)this.protocol).sendQueryPacket(callingQuery, packet, maxRows, streamResults, cachedMetadata, resultSetFactory);
      } catch (CJException var21) {
         if ((Boolean)this.getPropertySet().getBooleanProperty(PropertyKey.dumpQueriesOnException).getValue()) {
            String extractedSql = NativePacketPayload.extractSqlFromPacket(query, packet, endOfQueryPacketPosition, (Integer)this.getPropertySet().getIntegerProperty(PropertyKey.maxQuerySizeToLog).getValue());
            StringBuilder messageBuf = new StringBuilder(extractedSql.length() + 32);
            messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
            messageBuf.append(extractedSql);
            messageBuf.append("\n\n");
            var21.appendMessage(messageBuf.toString());
         }

         if ((Boolean)this.autoReconnect.getValue()) {
            if (var21 instanceof CJCommunicationsException) {
               this.protocol.getSocketConnection().forceClose();
            }

            this.needsPing = true;
         } else if (var21 instanceof CJCommunicationsException) {
            this.invokeCleanupListeners(var21);
         }

         throw var21;
      } catch (Throwable var22) {
         if ((Boolean)this.autoReconnect.getValue()) {
            if (var22 instanceof IOException) {
               this.protocol.getSocketConnection().forceClose();
            } else if (var22 instanceof IOException) {
               this.invokeCleanupListeners(var22);
            }

            this.needsPing = true;
         }

         throw ExceptionFactory.createException(var22.getMessage(), var22, this.exceptionInterceptor);
      } finally {
         if ((Boolean)this.maintainTimeStats.getValue()) {
            this.lastQueryFinishedTime = System.currentTimeMillis();
         }

         if ((Boolean)this.gatherPerfMetrics.getValue()) {
            ((NativeProtocol)this.protocol).getMetricsHolder().registerQueryExecutionTime(System.currentTimeMillis() - queryStartTime);
         }

      }

      return var12;
   }

   public long getIdleFor() {
      return this.lastQueryFinishedTime == 0L ? 0L : System.currentTimeMillis() - this.lastQueryFinishedTime;
   }

   public boolean isNeedsPing() {
      return this.needsPing;
   }

   public void setNeedsPing(boolean needsPing) {
      this.needsPing = needsPing;
   }

   public void ping(boolean checkForClosedConnection, int timeoutMillis) {
      if (checkForClosedConnection) {
         this.checkClosed();
      }

      long pingMillisLifetime = (long)(Integer)this.getPropertySet().getIntegerProperty(PropertyKey.selfDestructOnPingSecondsLifetime).getValue();
      int pingMaxOperations = (Integer)this.getPropertySet().getIntegerProperty(PropertyKey.selfDestructOnPingMaxOperations).getValue();
      if ((pingMillisLifetime <= 0L || System.currentTimeMillis() - this.connectionCreationTimeMillis <= pingMillisLifetime) && (pingMaxOperations <= 0 || pingMaxOperations > this.getCommandCount())) {
         this.sendCommand(this.commandBuilder.buildComPing((NativePacketPayload)null), false, timeoutMillis);
      } else {
         this.invokeNormalCloseListeners();
         throw ExceptionFactory.createException(Messages.getString("Connection.exceededConnectionLifetime"), "08S01", 0, false, (Throwable)null, this.exceptionInterceptor);
      }
   }

   public long getConnectionCreationTimeMillis() {
      return this.connectionCreationTimeMillis;
   }

   public void setConnectionCreationTimeMillis(long connectionCreationTimeMillis) {
      this.connectionCreationTimeMillis = connectionCreationTimeMillis;
   }

   public boolean isClosed() {
      return this.isClosed;
   }

   public void checkClosed() {
      if (this.isClosed) {
         if (this.forceClosedReason != null && this.forceClosedReason.getClass().equals(OperationCancelledException.class)) {
            throw (OperationCancelledException)this.forceClosedReason;
         } else {
            throw (ConnectionIsClosedException)ExceptionFactory.createException(ConnectionIsClosedException.class, Messages.getString("Connection.2"), this.forceClosedReason, this.getExceptionInterceptor());
         }
      }
   }

   public Throwable getForceClosedReason() {
      return this.forceClosedReason;
   }

   public void setForceClosedReason(Throwable forceClosedReason) {
      this.forceClosedReason = forceClosedReason;
   }

   public void addListener(Session.SessionEventListener l) {
      this.listeners.addIfAbsent(new WeakReference(l));
   }

   public void removeListener(Session.SessionEventListener listener) {
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         WeakReference<Session.SessionEventListener> wr = (WeakReference)var2.next();
         Session.SessionEventListener l = (Session.SessionEventListener)wr.get();
         if (l == listener) {
            this.listeners.remove(wr);
            break;
         }
      }

   }

   protected void invokeNormalCloseListeners() {
      Iterator var1 = this.listeners.iterator();

      while(var1.hasNext()) {
         WeakReference<Session.SessionEventListener> wr = (WeakReference)var1.next();
         Session.SessionEventListener l = (Session.SessionEventListener)wr.get();
         if (l != null) {
            l.handleNormalClose();
         } else {
            this.listeners.remove(wr);
         }
      }

   }

   protected void invokeReconnectListeners() {
      Iterator var1 = this.listeners.iterator();

      while(var1.hasNext()) {
         WeakReference<Session.SessionEventListener> wr = (WeakReference)var1.next();
         Session.SessionEventListener l = (Session.SessionEventListener)wr.get();
         if (l != null) {
            l.handleReconnect();
         } else {
            this.listeners.remove(wr);
         }
      }

   }

   public void invokeCleanupListeners(Throwable whyCleanedUp) {
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         WeakReference<Session.SessionEventListener> wr = (WeakReference)var2.next();
         Session.SessionEventListener l = (Session.SessionEventListener)wr.get();
         if (l != null) {
            l.handleCleanup(whyCleanedUp);
         } else {
            this.listeners.remove(wr);
         }
      }

   }

   public String getIdentifierQuoteString() {
      return this.protocol != null && this.protocol.getServerSession().useAnsiQuotedIdentifiers() ? "\"" : "`";
   }

   public synchronized Timer getCancelTimer() {
      if (this.cancelTimer == null) {
         this.cancelTimer = new Timer("MySQL Statement Cancellation Timer", Boolean.TRUE);
      }

      return this.cancelTimer;
   }
}
