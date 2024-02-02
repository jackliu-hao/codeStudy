/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ConnectionIsClosedException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptorChain;
/*     */ import com.mysql.cj.exceptions.OperationCancelledException;
/*     */ import com.mysql.cj.interceptors.QueryInterceptor;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.NetworkResources;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.protocol.Resultset;
/*     */ import com.mysql.cj.protocol.SocketConnection;
/*     */ import com.mysql.cj.protocol.SocketFactory;
/*     */ import com.mysql.cj.protocol.a.NativeMessageBuilder;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.protocol.a.NativeProtocol;
/*     */ import com.mysql.cj.protocol.a.NativeServerSession;
/*     */ import com.mysql.cj.protocol.a.NativeSocketConnection;
/*     */ import com.mysql.cj.protocol.a.ResultsetFactory;
/*     */ import com.mysql.cj.result.Field;
/*     */ import com.mysql.cj.result.LongValueFactory;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.result.StringValueFactory;
/*     */ import com.mysql.cj.result.ValueFactory;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.net.SocketAddress;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Timer;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.function.Supplier;
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
/*     */ public class NativeSession
/*     */   extends CoreSession
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 5323638898749073419L;
/*     */   private CacheAdapter<String, Map<String, String>> serverConfigCache;
/*  89 */   private long lastQueryFinishedTime = 0L;
/*     */ 
/*     */   
/*     */   private boolean needsPing = false;
/*     */   
/*  94 */   private NativeMessageBuilder commandBuilder = null;
/*     */ 
/*     */   
/*     */   private boolean isClosed = true;
/*     */ 
/*     */   
/*     */   private Throwable forceClosedReason;
/*     */   
/* 102 */   private CopyOnWriteArrayList<WeakReference<Session.SessionEventListener>> listeners = new CopyOnWriteArrayList<>();
/*     */   private transient Timer cancelTimer;
/*     */   private static final String SERVER_VERSION_STRING_VAR_NAME = "server_version_string";
/*     */   
/*     */   public NativeSession(HostInfo hostInfo, PropertySet propSet) {
/* 107 */     super(hostInfo, propSet);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(HostInfo hi, String user, String password, String database, int loginTimeout, TransactionEventHandler transactionManager) throws IOException {
/* 113 */     this.hostInfo = hi;
/*     */ 
/*     */     
/* 116 */     setSessionMaxRows(-1);
/*     */ 
/*     */     
/* 119 */     NativeSocketConnection nativeSocketConnection = new NativeSocketConnection();
/* 120 */     nativeSocketConnection.connect(this.hostInfo.getHost(), this.hostInfo.getPort(), this.propertySet, getExceptionInterceptor(), this.log, loginTimeout);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 125 */     if (this.protocol == null) {
/* 126 */       this.protocol = (Protocol<? extends Message>)NativeProtocol.getInstance(this, (SocketConnection)nativeSocketConnection, this.propertySet, this.log, transactionManager);
/*     */     } else {
/* 128 */       this.protocol.init(this, (SocketConnection)nativeSocketConnection, this.propertySet, transactionManager);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 133 */     this.protocol.connect(user, password, database);
/*     */     
/* 135 */     this.isClosed = false;
/*     */     
/* 137 */     this.commandBuilder = new NativeMessageBuilder(getServerSession().supportsQueryAttributes());
/*     */   }
/*     */ 
/*     */   
/*     */   public NativeProtocol getProtocol() {
/* 142 */     return (NativeProtocol)this.protocol;
/*     */   }
/*     */ 
/*     */   
/*     */   public void quit() {
/* 147 */     if (this.protocol != null) {
/*     */       try {
/* 149 */         ((NativeProtocol)this.protocol).quit();
/* 150 */       } catch (Exception exception) {}
/*     */     }
/*     */ 
/*     */     
/* 154 */     synchronized (this) {
/* 155 */       if (this.cancelTimer != null) {
/* 156 */         this.cancelTimer.cancel();
/* 157 */         this.cancelTimer = null;
/*     */       } 
/*     */     } 
/* 160 */     this.isClosed = true;
/* 161 */     super.quit();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void forceClose() {
/* 167 */     if (this.protocol != null) {
/*     */       
/*     */       try {
/*     */ 
/*     */ 
/*     */         
/* 173 */         this.protocol.getSocketConnection().forceClose();
/* 174 */         ((NativeProtocol)this.protocol).releaseResources();
/* 175 */       } catch (Throwable throwable) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 180 */     synchronized (this) {
/* 181 */       if (this.cancelTimer != null) {
/* 182 */         this.cancelTimer.cancel();
/* 183 */         this.cancelTimer = null;
/*     */       } 
/*     */     } 
/* 186 */     this.isClosed = true;
/* 187 */     super.forceClose();
/*     */   }
/*     */   
/*     */   public void enableMultiQueries() {
/* 191 */     sendCommand(this.commandBuilder.buildComSetOption(((NativeProtocol)this.protocol).getSharedSendPacket(), 0), false, 0);
/* 192 */     ((NativeServerSession)getServerSession()).preserveOldTransactionState();
/*     */   }
/*     */   
/*     */   public void disableMultiQueries() {
/* 196 */     sendCommand(this.commandBuilder.buildComSetOption(((NativeProtocol)this.protocol).getSharedSendPacket(), 1), false, 0);
/* 197 */     ((NativeServerSession)getServerSession()).preserveOldTransactionState();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag) {
/* 206 */     return ((NativeServerSession)this.protocol.getServerSession()).isSetNeededForAutoCommitMode(autoCommitFlag, false);
/*     */   }
/*     */   
/*     */   public int getSessionMaxRows() {
/* 210 */     return this.sessionMaxRows;
/*     */   }
/*     */   
/*     */   public void setSessionMaxRows(int sessionMaxRows) {
/* 214 */     this.sessionMaxRows = sessionMaxRows;
/*     */   }
/*     */   
/*     */   public void setQueryInterceptors(List<QueryInterceptor> queryInterceptors) {
/* 218 */     ((NativeProtocol)this.protocol).setQueryInterceptors(queryInterceptors);
/*     */   }
/*     */   
/*     */   public boolean isServerLocal(Session sess) {
/* 222 */     SocketFactory factory = this.protocol.getSocketConnection().getSocketFactory();
/* 223 */     return factory.isLocallyConnected(sess);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdownServer() {
/* 231 */     if (versionMeetsMinimum(5, 7, 9)) {
/* 232 */       sendCommand(this.commandBuilder.buildComQuery(getSharedSendPacket(), "SHUTDOWN"), false, 0);
/*     */     } else {
/* 234 */       sendCommand(this.commandBuilder.buildComShutdown(getSharedSendPacket()), false, 0);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setSocketTimeout(int milliseconds) {
/* 239 */     getPropertySet().getProperty(PropertyKey.socketTimeout).setValue(Integer.valueOf(milliseconds));
/* 240 */     ((NativeProtocol)this.protocol).setSocketTimeout(milliseconds);
/*     */   }
/*     */   
/*     */   public int getSocketTimeout() {
/* 244 */     RuntimeProperty<Integer> sto = getPropertySet().getProperty(PropertyKey.socketTimeout);
/* 245 */     return ((Integer)sto.getValue()).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NativePacketPayload getSharedSendPacket() {
/* 255 */     return ((NativeProtocol)this.protocol).getSharedSendPacket();
/*     */   }
/*     */   
/*     */   public void dumpPacketRingBuffer() {
/* 259 */     ((NativeProtocol)this.protocol).dumpPacketRingBuffer();
/*     */   }
/*     */   
/*     */   public <T extends Resultset> T invokeQueryInterceptorsPre(Supplier<String> sql, Query interceptedQuery, boolean forceExecute) {
/* 263 */     return (T)((NativeProtocol)this.protocol).invokeQueryInterceptorsPre(sql, interceptedQuery, forceExecute);
/*     */   }
/*     */   
/*     */   public <T extends Resultset> T invokeQueryInterceptorsPost(Supplier<String> sql, Query interceptedQuery, T originalResultSet, boolean forceExecute) {
/* 267 */     return (T)((NativeProtocol)this.protocol).invokeQueryInterceptorsPost(sql, interceptedQuery, (Resultset)originalResultSet, forceExecute);
/*     */   }
/*     */   
/*     */   public boolean shouldIntercept() {
/* 271 */     return (((NativeProtocol)this.protocol).getQueryInterceptors() != null);
/*     */   }
/*     */   
/*     */   public long getCurrentTimeNanosOrMillis() {
/* 275 */     return ((NativeProtocol)this.protocol).getCurrentTimeNanosOrMillis();
/*     */   }
/*     */   
/*     */   public final NativePacketPayload sendCommand(NativePacketPayload queryPacket, boolean skipCheck, int timeoutMillis) {
/* 279 */     return (NativePacketPayload)this.protocol.sendCommand((Message)queryPacket, skipCheck, timeoutMillis);
/*     */   }
/*     */   
/*     */   public long getSlowQueryThreshold() {
/* 283 */     return ((NativeProtocol)this.protocol).getSlowQueryThreshold();
/*     */   }
/*     */   
/*     */   public boolean hadWarnings() {
/* 287 */     return ((NativeProtocol)this.protocol).hadWarnings();
/*     */   }
/*     */   
/*     */   public void clearInputStream() {
/* 291 */     ((NativeProtocol)this.protocol).clearInputStream();
/*     */   }
/*     */   
/*     */   public NetworkResources getNetworkResources() {
/* 295 */     return this.protocol.getSocketConnection().getNetworkResources();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSSLEstablished() {
/* 300 */     return this.protocol.getSocketConnection().isSSLEstablished();
/*     */   }
/*     */   
/*     */   public int getCommandCount() {
/* 304 */     return ((NativeProtocol)this.protocol).getCommandCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getRemoteSocketAddress() {
/*     */     try {
/* 310 */       return this.protocol.getSocketConnection().getMysqlSocket().getRemoteSocketAddress();
/* 311 */     } catch (IOException e) {
/* 312 */       throw new CJCommunicationsException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public InputStream getLocalInfileInputStream() {
/* 317 */     return this.protocol.getLocalInfileInputStream();
/*     */   }
/*     */   
/*     */   public void setLocalInfileInputStream(InputStream stream) {
/* 321 */     this.protocol.setLocalInfileInputStream(stream);
/*     */   }
/*     */   
/*     */   private void createConfigCacheIfNeeded(Object syncMutex) {
/* 325 */     synchronized (syncMutex) {
/* 326 */       if (this.serverConfigCache != null) {
/*     */         return;
/*     */       }
/*     */       
/*     */       try {
/* 331 */         Class<?> factoryClass = Class.forName(getPropertySet().getStringProperty(PropertyKey.serverConfigCacheFactory).getStringValue());
/*     */ 
/*     */         
/* 334 */         CacheAdapterFactory<String, Map<String, String>> cacheFactory = (CacheAdapterFactory<String, Map<String, String>>)factoryClass.newInstance();
/*     */         
/* 336 */         this.serverConfigCache = cacheFactory.getInstance(syncMutex, this.hostInfo.getDatabaseUrl(), 2147483647, 2147483647);
/*     */         
/* 338 */         ExceptionInterceptor evictOnCommsError = new ExceptionInterceptor()
/*     */           {
/*     */             public ExceptionInterceptor init(Properties config, Log log1) {
/* 341 */               return this;
/*     */             }
/*     */ 
/*     */             
/*     */             public void destroy() {}
/*     */ 
/*     */             
/*     */             public Exception interceptException(Exception sqlEx) {
/* 349 */               if (sqlEx instanceof SQLException && ((SQLException)sqlEx).getSQLState() != null && ((SQLException)sqlEx)
/* 350 */                 .getSQLState().startsWith("08")) {
/* 351 */                 NativeSession.this.serverConfigCache.invalidate(NativeSession.this.hostInfo.getDatabaseUrl());
/*     */               }
/* 353 */               return null;
/*     */             }
/*     */           };
/*     */         
/* 357 */         if (this.exceptionInterceptor == null) {
/* 358 */           this.exceptionInterceptor = evictOnCommsError;
/*     */         } else {
/* 360 */           ((ExceptionInterceptorChain)this.exceptionInterceptor).addRingZero(evictOnCommsError);
/*     */         } 
/* 362 */       } catch (ClassNotFoundException e) {
/* 363 */         throw ExceptionFactory.createException(Messages.getString("Connection.CantFindCacheFactory", new Object[] {
/* 364 */                 getPropertySet().getStringProperty(PropertyKey.parseInfoCacheFactory).getValue(), PropertyKey.parseInfoCacheFactory
/* 365 */               }), e, getExceptionInterceptor());
/* 366 */       } catch (InstantiationException|IllegalAccessException|CJException e) {
/* 367 */         throw ExceptionFactory.createException(Messages.getString("Connection.CantLoadCacheFactory", new Object[] {
/* 368 */                 getPropertySet().getStringProperty(PropertyKey.parseInfoCacheFactory).getValue(), PropertyKey.parseInfoCacheFactory
/* 369 */               }), e, getExceptionInterceptor());
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
/*     */   public void loadServerVariables(Object syncMutex, String version) {
/* 388 */     if (((Boolean)this.cacheServerConfiguration.getValue()).booleanValue()) {
/* 389 */       createConfigCacheIfNeeded(syncMutex);
/*     */       
/* 391 */       Map<String, String> cachedVariableMap = this.serverConfigCache.get(this.hostInfo.getDatabaseUrl());
/*     */       
/* 393 */       if (cachedVariableMap != null) {
/* 394 */         String cachedServerVersion = cachedVariableMap.get("server_version_string");
/*     */         
/* 396 */         if (cachedServerVersion != null && getServerSession().getServerVersion() != null && cachedServerVersion
/* 397 */           .equals(getServerSession().getServerVersion().toString())) {
/* 398 */           Map<String, String> localVariableMap = this.protocol.getServerSession().getServerVariables();
/* 399 */           Map<String, String> newLocalVariableMap = new HashMap<>();
/* 400 */           newLocalVariableMap.putAll(cachedVariableMap);
/* 401 */           newLocalVariableMap.putAll(localVariableMap);
/* 402 */           this.protocol.getServerSession().setServerVariables(newLocalVariableMap);
/*     */           
/*     */           return;
/*     */         } 
/* 406 */         this.serverConfigCache.invalidate(this.hostInfo.getDatabaseUrl());
/*     */       } 
/*     */     } 
/*     */     
/*     */     try {
/* 411 */       if (version != null && version.indexOf('*') != -1) {
/* 412 */         StringBuilder buf = new StringBuilder(version.length() + 10);
/* 413 */         for (int i = 0; i < version.length(); i++) {
/* 414 */           char c = version.charAt(i);
/* 415 */           buf.append((c == '*') ? "[star]" : Character.valueOf(c));
/*     */         } 
/* 417 */         version = buf.toString();
/*     */       } 
/*     */       
/* 420 */       String versionComment = (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue()).booleanValue() || version == null) ? "" : ("/* " + version + " */");
/*     */       
/* 422 */       this.protocol.getServerSession().setServerVariables(new HashMap<>());
/*     */       
/* 424 */       if (versionMeetsMinimum(5, 1, 0)) {
/* 425 */         StringBuilder queryBuf = (new StringBuilder(versionComment)).append("SELECT");
/* 426 */         queryBuf.append("  @@session.auto_increment_increment AS auto_increment_increment");
/* 427 */         queryBuf.append(", @@character_set_client AS character_set_client");
/* 428 */         queryBuf.append(", @@character_set_connection AS character_set_connection");
/* 429 */         queryBuf.append(", @@character_set_results AS character_set_results");
/* 430 */         queryBuf.append(", @@character_set_server AS character_set_server");
/* 431 */         queryBuf.append(", @@collation_server AS collation_server");
/* 432 */         queryBuf.append(", @@collation_connection AS collation_connection");
/* 433 */         queryBuf.append(", @@init_connect AS init_connect");
/* 434 */         queryBuf.append(", @@interactive_timeout AS interactive_timeout");
/* 435 */         if (!versionMeetsMinimum(5, 5, 0)) {
/* 436 */           queryBuf.append(", @@language AS language");
/*     */         }
/* 438 */         queryBuf.append(", @@license AS license");
/* 439 */         queryBuf.append(", @@lower_case_table_names AS lower_case_table_names");
/* 440 */         queryBuf.append(", @@max_allowed_packet AS max_allowed_packet");
/* 441 */         queryBuf.append(", @@net_write_timeout AS net_write_timeout");
/* 442 */         queryBuf.append(", @@performance_schema AS performance_schema");
/* 443 */         if (!versionMeetsMinimum(8, 0, 3)) {
/* 444 */           queryBuf.append(", @@query_cache_size AS query_cache_size");
/* 445 */           queryBuf.append(", @@query_cache_type AS query_cache_type");
/*     */         } 
/* 447 */         queryBuf.append(", @@sql_mode AS sql_mode");
/* 448 */         queryBuf.append(", @@system_time_zone AS system_time_zone");
/* 449 */         queryBuf.append(", @@time_zone AS time_zone");
/* 450 */         if (versionMeetsMinimum(8, 0, 3) || (versionMeetsMinimum(5, 7, 20) && !versionMeetsMinimum(8, 0, 0))) {
/* 451 */           queryBuf.append(", @@transaction_isolation AS transaction_isolation");
/*     */         } else {
/* 453 */           queryBuf.append(", @@tx_isolation AS transaction_isolation");
/*     */         } 
/* 455 */         queryBuf.append(", @@wait_timeout AS wait_timeout");
/*     */         
/* 457 */         NativePacketPayload resultPacket = sendCommand(this.commandBuilder.buildComQuery(null, queryBuf.toString()), false, 0);
/* 458 */         Resultset rs = ((NativeProtocol)this.protocol).readAllResults(-1, false, resultPacket, false, null, (ProtocolEntityFactory)new ResultsetFactory(Resultset.Type.FORWARD_ONLY, null));
/*     */         
/* 460 */         Field[] f = rs.getColumnDefinition().getFields();
/*     */         
/* 462 */         StringValueFactory stringValueFactory = new StringValueFactory(this.propertySet);
/*     */         Row r;
/* 464 */         if (f.length > 0 && (r = (Row)rs.getRows().next()) != null) {
/* 465 */           for (int i = 0; i < f.length; i++) {
/* 466 */             String value = (String)r.getValue(i, (ValueFactory)stringValueFactory);
/* 467 */             this.protocol.getServerSession().getServerVariables().put(f[i].getColumnLabel(), 
/* 468 */                 "utf8mb3".equalsIgnoreCase(value) ? "utf8" : value);
/*     */           }
/*     */         
/*     */         }
/*     */       } else {
/*     */         
/* 474 */         NativePacketPayload resultPacket = sendCommand(this.commandBuilder.buildComQuery(null, versionComment + "SHOW VARIABLES"), false, 0);
/* 475 */         Resultset rs = ((NativeProtocol)this.protocol).readAllResults(-1, false, resultPacket, false, null, (ProtocolEntityFactory)new ResultsetFactory(Resultset.Type.FORWARD_ONLY, null));
/*     */         
/* 477 */         StringValueFactory stringValueFactory = new StringValueFactory(this.propertySet);
/*     */         Row r;
/* 479 */         while ((r = (Row)rs.getRows().next()) != null) {
/* 480 */           this.protocol.getServerSession().getServerVariables().put(r.getValue(0, (ValueFactory)stringValueFactory), r.getValue(1, (ValueFactory)stringValueFactory));
/*     */         }
/*     */       } 
/* 483 */     } catch (IOException e) {
/* 484 */       throw ExceptionFactory.createException(e.getMessage(), e);
/*     */     } 
/*     */     
/* 487 */     if (((Boolean)this.cacheServerConfiguration.getValue()).booleanValue()) {
/* 488 */       this.protocol.getServerSession().getServerVariables().put("server_version_string", getServerSession().getServerVersion().toString());
/* 489 */       Map<String, String> localVariableMap = new HashMap<>();
/* 490 */       localVariableMap.putAll(this.protocol.getServerSession().getServerVariables());
/* 491 */       this.serverConfigCache.put(this.hostInfo.getDatabaseUrl(), Collections.unmodifiableMap(localVariableMap));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setSessionVariables() {
/* 496 */     String sessionVariables = (String)getPropertySet().getStringProperty(PropertyKey.sessionVariables).getValue();
/* 497 */     if (sessionVariables != null) {
/* 498 */       List<String> variablesToSet = new ArrayList<>();
/* 499 */       for (String part : StringUtils.split(sessionVariables, ",", "\"'(", "\"')", "\"'", true)) {
/* 500 */         variablesToSet.addAll(StringUtils.split(part, ";", "\"'(", "\"')", "\"'", true));
/*     */       }
/*     */       
/* 503 */       if (!variablesToSet.isEmpty()) {
/* 504 */         StringBuilder query = new StringBuilder("SET ");
/* 505 */         String separator = "";
/* 506 */         for (String variableToSet : variablesToSet) {
/* 507 */           if (variableToSet.length() > 0) {
/* 508 */             query.append(separator);
/* 509 */             if (!variableToSet.startsWith("@")) {
/* 510 */               query.append("SESSION ");
/*     */             }
/* 512 */             query.append(variableToSet);
/* 513 */             separator = ",";
/*     */           } 
/*     */         } 
/* 516 */         sendCommand(this.commandBuilder.buildComQuery(null, query.toString()), false, 0);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getProcessHost() {
/*     */     try {
/* 523 */       long threadId = getThreadId();
/* 524 */       String processHost = findProcessHost(threadId);
/*     */       
/* 526 */       if (processHost == null) {
/*     */         
/* 528 */         this.log.logWarn(String.format("Connection id %d not found in \"SHOW PROCESSLIST\", assuming 32-bit overflow, using SELECT CONNECTION_ID() instead", new Object[] {
/* 529 */                 Long.valueOf(threadId)
/*     */               }));
/* 531 */         NativePacketPayload resultPacket = sendCommand(this.commandBuilder.buildComQuery(null, "SELECT CONNECTION_ID()"), false, 0);
/* 532 */         Resultset rs = ((NativeProtocol)this.protocol).readAllResults(-1, false, resultPacket, false, null, (ProtocolEntityFactory)new ResultsetFactory(Resultset.Type.FORWARD_ONLY, null));
/*     */ 
/*     */         
/* 535 */         LongValueFactory longValueFactory = new LongValueFactory(getPropertySet());
/*     */         Row r;
/* 537 */         if ((r = (Row)rs.getRows().next()) != null) {
/* 538 */           threadId = ((Long)r.getValue(0, (ValueFactory)longValueFactory)).longValue();
/* 539 */           processHost = findProcessHost(threadId);
/*     */         } else {
/* 541 */           this.log.logError("No rows returned for statement \"SELECT CONNECTION_ID()\", local connection check will most likely be incorrect");
/*     */         } 
/*     */       } 
/*     */       
/* 545 */       if (processHost == null)
/* 546 */         this.log.logWarn(String.format("Cannot find process listing for connection %d in SHOW PROCESSLIST output, unable to determine if locally connected", new Object[] {
/* 547 */                 Long.valueOf(threadId)
/*     */               })); 
/* 549 */       return processHost;
/* 550 */     } catch (IOException e) {
/* 551 */       throw ExceptionFactory.createException(e.getMessage(), e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private String findProcessHost(long threadId) {
/*     */     try {
/* 557 */       String processHost = null;
/*     */       
/* 559 */       String ps = this.protocol.getServerSession().getServerVariable("performance_schema");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 567 */       NativePacketPayload resultPacket = (versionMeetsMinimum(5, 6, 0) && ps != null && ("1".contentEquals(ps) || "ON".contentEquals(ps))) ? sendCommand(this.commandBuilder.buildComQuery(null, "select PROCESSLIST_ID, PROCESSLIST_USER, PROCESSLIST_HOST from performance_schema.threads where PROCESSLIST_ID=" + threadId), false, 0) : sendCommand(this.commandBuilder.buildComQuery(null, "SHOW PROCESSLIST"), false, 0);
/*     */       
/* 569 */       Resultset rs = ((NativeProtocol)this.protocol).readAllResults(-1, false, resultPacket, false, null, (ProtocolEntityFactory)new ResultsetFactory(Resultset.Type.FORWARD_ONLY, null));
/*     */       
/* 571 */       LongValueFactory longValueFactory = new LongValueFactory(getPropertySet());
/* 572 */       StringValueFactory stringValueFactory = new StringValueFactory(this.propertySet);
/*     */       Row r;
/* 574 */       while ((r = (Row)rs.getRows().next()) != null) {
/* 575 */         long id = ((Long)r.getValue(0, (ValueFactory)longValueFactory)).longValue();
/* 576 */         if (threadId == id) {
/* 577 */           processHost = (String)r.getValue(2, (ValueFactory)stringValueFactory);
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 582 */       return processHost;
/*     */     }
/* 584 */     catch (IOException e) {
/* 585 */       throw ExceptionFactory.createException(e.getMessage(), e);
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
/*     */   public String queryServerVariable(String varName) {
/*     */     try {
/* 599 */       NativePacketPayload resultPacket = sendCommand(this.commandBuilder.buildComQuery(null, "SELECT " + varName), false, 0);
/* 600 */       Resultset rs = ((NativeProtocol)this.protocol).readAllResults(-1, false, resultPacket, false, null, (ProtocolEntityFactory)new ResultsetFactory(Resultset.Type.FORWARD_ONLY, null));
/*     */       
/* 602 */       StringValueFactory stringValueFactory = new StringValueFactory(this.propertySet);
/*     */       Row r;
/* 604 */       if ((r = (Row)rs.getRows().next()) != null) {
/* 605 */         String s = (String)r.getValue(0, (ValueFactory)stringValueFactory);
/* 606 */         if (s != null) {
/* 607 */           return s;
/*     */         }
/*     */       } 
/*     */       
/* 611 */       return null;
/*     */     }
/* 613 */     catch (IOException e) {
/* 614 */       throw ExceptionFactory.createException(e.getMessage(), e);
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
/*     */   public <T extends Resultset> T execSQL(Query callingQuery, String query, int maxRows, NativePacketPayload packet, boolean streamResults, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory, ColumnDefinition cachedMetadata, boolean isBatch) {
/* 647 */     long queryStartTime = ((Boolean)this.gatherPerfMetrics.getValue()).booleanValue() ? System.currentTimeMillis() : 0L;
/* 648 */     int endOfQueryPacketPosition = (packet != null) ? packet.getPosition() : 0;
/*     */     
/* 650 */     this.lastQueryFinishedTime = 0L;
/*     */     
/* 652 */     if (((Boolean)this.autoReconnect.getValue()).booleanValue() && (getServerSession().isAutoCommit() || ((Boolean)this.autoReconnectForPools.getValue()).booleanValue()) && this.needsPing && !isBatch) {
/*     */       try {
/* 654 */         ping(false, 0);
/* 655 */         this.needsPing = false;
/*     */       }
/* 657 */       catch (Exception Ex) {
/* 658 */         invokeReconnectListeners();
/*     */       } 
/*     */     }
/*     */     
/*     */     try {
/* 663 */       return (T)((packet == null) ? ((NativeProtocol)this.protocol)
/* 664 */         .sendQueryString(callingQuery, query, (String)this.characterEncoding.getValue(), maxRows, streamResults, cachedMetadata, resultSetFactory) : ((NativeProtocol)this.protocol)
/*     */         
/* 666 */         .sendQueryPacket(callingQuery, packet, maxRows, streamResults, cachedMetadata, resultSetFactory));
/*     */     }
/* 668 */     catch (CJException sqlE) {
/* 669 */       if (((Boolean)getPropertySet().getBooleanProperty(PropertyKey.dumpQueriesOnException).getValue()).booleanValue()) {
/* 670 */         String extractedSql = NativePacketPayload.extractSqlFromPacket(query, packet, endOfQueryPacketPosition, ((Integer)
/* 671 */             getPropertySet().getIntegerProperty(PropertyKey.maxQuerySizeToLog).getValue()).intValue());
/* 672 */         StringBuilder messageBuf = new StringBuilder(extractedSql.length() + 32);
/* 673 */         messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
/* 674 */         messageBuf.append(extractedSql);
/* 675 */         messageBuf.append("\n\n");
/* 676 */         sqlE.appendMessage(messageBuf.toString());
/*     */       } 
/*     */       
/* 679 */       if (((Boolean)this.autoReconnect.getValue()).booleanValue()) {
/* 680 */         if (sqlE instanceof CJCommunicationsException)
/*     */         {
/* 682 */           this.protocol.getSocketConnection().forceClose();
/*     */         }
/* 684 */         this.needsPing = true;
/* 685 */       } else if (sqlE instanceof CJCommunicationsException) {
/* 686 */         invokeCleanupListeners((Throwable)sqlE);
/*     */       } 
/* 688 */       throw sqlE;
/*     */     }
/* 690 */     catch (Throwable ex) {
/* 691 */       if (((Boolean)this.autoReconnect.getValue()).booleanValue()) {
/* 692 */         if (ex instanceof IOException) {
/*     */           
/* 694 */           this.protocol.getSocketConnection().forceClose();
/* 695 */         } else if (ex instanceof IOException) {
/* 696 */           invokeCleanupListeners(ex);
/*     */         } 
/* 698 */         this.needsPing = true;
/*     */       } 
/* 700 */       throw ExceptionFactory.createException(ex.getMessage(), ex, this.exceptionInterceptor);
/*     */     } finally {
/*     */       
/* 703 */       if (((Boolean)this.maintainTimeStats.getValue()).booleanValue()) {
/* 704 */         this.lastQueryFinishedTime = System.currentTimeMillis();
/*     */       }
/*     */       
/* 707 */       if (((Boolean)this.gatherPerfMetrics.getValue()).booleanValue()) {
/* 708 */         ((NativeProtocol)this.protocol).getMetricsHolder().registerQueryExecutionTime(System.currentTimeMillis() - queryStartTime);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long getIdleFor() {
/* 715 */     return (this.lastQueryFinishedTime == 0L) ? 0L : (System.currentTimeMillis() - this.lastQueryFinishedTime);
/*     */   }
/*     */   
/*     */   public boolean isNeedsPing() {
/* 719 */     return this.needsPing;
/*     */   }
/*     */   
/*     */   public void setNeedsPing(boolean needsPing) {
/* 723 */     this.needsPing = needsPing;
/*     */   }
/*     */   
/*     */   public void ping(boolean checkForClosedConnection, int timeoutMillis) {
/* 727 */     if (checkForClosedConnection) {
/* 728 */       checkClosed();
/*     */     }
/*     */     
/* 731 */     long pingMillisLifetime = ((Integer)getPropertySet().getIntegerProperty(PropertyKey.selfDestructOnPingSecondsLifetime).getValue()).intValue();
/* 732 */     int pingMaxOperations = ((Integer)getPropertySet().getIntegerProperty(PropertyKey.selfDestructOnPingMaxOperations).getValue()).intValue();
/*     */     
/* 734 */     if ((pingMillisLifetime > 0L && System.currentTimeMillis() - this.connectionCreationTimeMillis > pingMillisLifetime) || (pingMaxOperations > 0 && pingMaxOperations <= 
/* 735 */       getCommandCount())) {
/*     */       
/* 737 */       invokeNormalCloseListeners();
/*     */       
/* 739 */       throw ExceptionFactory.createException(Messages.getString("Connection.exceededConnectionLifetime"), "08S01", 0, false, null, this.exceptionInterceptor);
/*     */     } 
/*     */     
/* 742 */     sendCommand(this.commandBuilder.buildComPing(null), false, timeoutMillis);
/*     */   }
/*     */   
/*     */   public long getConnectionCreationTimeMillis() {
/* 746 */     return this.connectionCreationTimeMillis;
/*     */   }
/*     */   
/*     */   public void setConnectionCreationTimeMillis(long connectionCreationTimeMillis) {
/* 750 */     this.connectionCreationTimeMillis = connectionCreationTimeMillis;
/*     */   }
/*     */   
/*     */   public boolean isClosed() {
/* 754 */     return this.isClosed;
/*     */   }
/*     */   
/*     */   public void checkClosed() {
/* 758 */     if (this.isClosed) {
/* 759 */       if (this.forceClosedReason != null && this.forceClosedReason.getClass().equals(OperationCancelledException.class)) {
/* 760 */         throw (OperationCancelledException)this.forceClosedReason;
/*     */       }
/* 762 */       throw (ConnectionIsClosedException)ExceptionFactory.createException(ConnectionIsClosedException.class, Messages.getString("Connection.2"), this.forceClosedReason, 
/* 763 */           getExceptionInterceptor());
/*     */     } 
/*     */   }
/*     */   
/*     */   public Throwable getForceClosedReason() {
/* 768 */     return this.forceClosedReason;
/*     */   }
/*     */   
/*     */   public void setForceClosedReason(Throwable forceClosedReason) {
/* 772 */     this.forceClosedReason = forceClosedReason;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addListener(Session.SessionEventListener l) {
/* 777 */     this.listeners.addIfAbsent(new WeakReference<>(l));
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeListener(Session.SessionEventListener listener) {
/* 782 */     for (WeakReference<Session.SessionEventListener> wr : this.listeners) {
/* 783 */       Session.SessionEventListener l = wr.get();
/* 784 */       if (l == listener) {
/* 785 */         this.listeners.remove(wr);
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void invokeNormalCloseListeners() {
/* 792 */     for (WeakReference<Session.SessionEventListener> wr : this.listeners) {
/* 793 */       Session.SessionEventListener l = wr.get();
/* 794 */       if (l != null) {
/* 795 */         l.handleNormalClose(); continue;
/*     */       } 
/* 797 */       this.listeners.remove(wr);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void invokeReconnectListeners() {
/* 803 */     for (WeakReference<Session.SessionEventListener> wr : this.listeners) {
/* 804 */       Session.SessionEventListener l = wr.get();
/* 805 */       if (l != null) {
/* 806 */         l.handleReconnect(); continue;
/*     */       } 
/* 808 */       this.listeners.remove(wr);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void invokeCleanupListeners(Throwable whyCleanedUp) {
/* 814 */     for (WeakReference<Session.SessionEventListener> wr : this.listeners) {
/* 815 */       Session.SessionEventListener l = wr.get();
/* 816 */       if (l != null) {
/* 817 */         l.handleCleanup(whyCleanedUp); continue;
/*     */       } 
/* 819 */       this.listeners.remove(wr);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getIdentifierQuoteString() {
/* 826 */     return (this.protocol != null && this.protocol.getServerSession().useAnsiQuotedIdentifiers()) ? "\"" : "`";
/*     */   }
/*     */   
/*     */   public synchronized Timer getCancelTimer() {
/* 830 */     if (this.cancelTimer == null) {
/* 831 */       this.cancelTimer = new Timer("MySQL Statement Cancellation Timer", Boolean.TRUE.booleanValue());
/*     */     }
/* 833 */     return this.cancelTimer;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\NativeSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */