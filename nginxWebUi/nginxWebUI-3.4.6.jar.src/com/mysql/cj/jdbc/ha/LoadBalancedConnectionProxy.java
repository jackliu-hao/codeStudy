/*      */ package com.mysql.cj.jdbc.ha;
/*      */ 
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.PingTarget;
/*      */ import com.mysql.cj.conf.ConnectionUrl;
/*      */ import com.mysql.cj.conf.HostInfo;
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.conf.url.LoadBalanceConnectionUrl;
/*      */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*      */ import com.mysql.cj.exceptions.CJException;
/*      */ import com.mysql.cj.exceptions.ExceptionFactory;
/*      */ import com.mysql.cj.interceptors.QueryInterceptor;
/*      */ import com.mysql.cj.jdbc.ConnectionGroup;
/*      */ import com.mysql.cj.jdbc.ConnectionGroupManager;
/*      */ import com.mysql.cj.jdbc.ConnectionImpl;
/*      */ import com.mysql.cj.jdbc.JdbcConnection;
/*      */ import com.mysql.cj.jdbc.JdbcStatement;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*      */ import com.mysql.cj.util.StringUtils;
/*      */ import com.mysql.cj.util.Util;
/*      */ import java.lang.reflect.InvocationHandler;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.sql.Connection;
/*      */ import java.sql.SQLException;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class LoadBalancedConnectionProxy
/*      */   extends MultiHostConnectionProxy
/*      */   implements PingTarget
/*      */ {
/*      */   private ConnectionGroup connectionGroup;
/*      */   private long connectionGroupProxyID;
/*      */   protected Map<String, ConnectionImpl> liveConnections;
/*      */   private Map<String, Integer> hostsToListIndexMap;
/*      */   private Map<ConnectionImpl, String> connectionsToHostsMap;
/*      */   private long totalPhysicalConnections;
/*      */   private long[] responseTimes;
/*      */   private int retriesAllDown;
/*      */   private BalanceStrategy balancer;
/*      */   private int globalBlocklistTimeout;
/*   97 */   private static Map<String, Long> globalBlocklist = new HashMap<>();
/*      */   
/*      */   private int hostRemovalGracePeriod;
/*      */   
/*      */   private Set<String> hostsToRemove;
/*      */   
/*      */   private boolean inTransaction;
/*      */   
/*      */   private long transactionStartTime;
/*      */   private long transactionCount;
/*      */   private LoadBalanceExceptionChecker exceptionChecker;
/*  108 */   private static Class<?>[] INTERFACES_TO_PROXY = new Class[] { LoadBalancedConnection.class, JdbcConnection.class };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static LoadBalancedConnection createProxyInstance(ConnectionUrl connectionUrl) throws SQLException {
/*  120 */     LoadBalancedConnectionProxy connProxy = new LoadBalancedConnectionProxy(connectionUrl);
/*  121 */     return (LoadBalancedConnection)Proxy.newProxyInstance(LoadBalancedConnection.class.getClassLoader(), INTERFACES_TO_PROXY, connProxy);
/*      */   }
/*      */ 
/*      */   
/*      */   public LoadBalancedConnectionProxy(ConnectionUrl connectionUrl) throws SQLException {
/*      */     List<HostInfo> hosts;
/*      */     this.connectionGroup = null;
/*      */     this.connectionGroupProxyID = 0L;
/*      */     this.totalPhysicalConnections = 0L;
/*      */     this.globalBlocklistTimeout = 0;
/*      */     this.hostRemovalGracePeriod = 0;
/*      */     this.hostsToRemove = new HashSet<>();
/*      */     this.inTransaction = false;
/*      */     this.transactionStartTime = 0L;
/*      */     this.transactionCount = 0L;
/*  136 */     Properties props = connectionUrl.getConnectionArgumentsAsProperties();
/*      */     
/*  138 */     String group = props.getProperty(PropertyKey.loadBalanceConnectionGroup.getKeyName(), null);
/*  139 */     boolean enableJMX = false;
/*  140 */     String enableJMXAsString = props.getProperty(PropertyKey.ha_enableJMX.getKeyName(), "false");
/*      */     try {
/*  142 */       enableJMX = Boolean.parseBoolean(enableJMXAsString);
/*  143 */     } catch (Exception e) {
/*  144 */       throw SQLError.createSQLException(Messages.getString("MultihostConnection.badValueForHaEnableJMX", new Object[] { enableJMXAsString }), "S1009", null);
/*      */     } 
/*      */ 
/*      */     
/*  148 */     if (!StringUtils.isNullOrEmpty(group) && LoadBalanceConnectionUrl.class.isAssignableFrom(connectionUrl.getClass())) {
/*  149 */       this.connectionGroup = ConnectionGroupManager.getConnectionGroupInstance(group);
/*  150 */       if (enableJMX) {
/*  151 */         ConnectionGroupManager.registerJmx();
/*      */       }
/*  153 */       this.connectionGroupProxyID = this.connectionGroup.registerConnectionProxy(this, ((LoadBalanceConnectionUrl)connectionUrl)
/*  154 */           .getHostInfoListAsHostPortPairs());
/*  155 */       hosts = ((LoadBalanceConnectionUrl)connectionUrl).getHostInfoListFromHostPortPairs(this.connectionGroup.getInitialHosts());
/*      */     } else {
/*  157 */       hosts = connectionUrl.getHostsList();
/*      */     } 
/*      */ 
/*      */     
/*  161 */     int numHosts = initializeHostsSpecs(connectionUrl, hosts);
/*      */     
/*  163 */     this.liveConnections = new HashMap<>(numHosts);
/*  164 */     this.hostsToListIndexMap = new HashMap<>(numHosts);
/*  165 */     for (int i = 0; i < numHosts; i++) {
/*  166 */       this.hostsToListIndexMap.put(((HostInfo)this.hostsList.get(i)).getHostPortPair(), Integer.valueOf(i));
/*      */     }
/*  168 */     this.connectionsToHostsMap = new HashMap<>(numHosts);
/*  169 */     this.responseTimes = new long[numHosts];
/*      */     
/*  171 */     String retriesAllDownAsString = props.getProperty(PropertyKey.retriesAllDown.getKeyName(), "120");
/*      */     try {
/*  173 */       this.retriesAllDown = Integer.parseInt(retriesAllDownAsString);
/*  174 */     } catch (NumberFormatException nfe) {
/*  175 */       throw SQLError.createSQLException(
/*  176 */           Messages.getString("LoadBalancedConnectionProxy.badValueForRetriesAllDown", new Object[] { retriesAllDownAsString }), "S1009", null);
/*      */     } 
/*      */ 
/*      */     
/*  180 */     String blocklistTimeoutAsString = props.getProperty(PropertyKey.loadBalanceBlocklistTimeout.getKeyName(), "0");
/*      */     try {
/*  182 */       this.globalBlocklistTimeout = Integer.parseInt(blocklistTimeoutAsString);
/*  183 */     } catch (NumberFormatException nfe) {
/*  184 */       throw SQLError.createSQLException(
/*  185 */           Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceBlocklistTimeout", new Object[] { blocklistTimeoutAsString }), "S1009", null);
/*      */     } 
/*      */ 
/*      */     
/*  189 */     String hostRemovalGracePeriodAsString = props.getProperty(PropertyKey.loadBalanceHostRemovalGracePeriod.getKeyName(), "15000");
/*      */     try {
/*  191 */       this.hostRemovalGracePeriod = Integer.parseInt(hostRemovalGracePeriodAsString);
/*  192 */     } catch (NumberFormatException nfe) {
/*  193 */       throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceHostRemovalGracePeriod", new Object[] { hostRemovalGracePeriodAsString }), "S1009", null);
/*      */     } 
/*      */ 
/*      */     
/*  197 */     String strategy = props.getProperty(PropertyKey.ha_loadBalanceStrategy.getKeyName(), "random");
/*      */     try {
/*  199 */       switch (strategy) {
/*      */         case "random":
/*  201 */           this.balancer = new RandomBalanceStrategy();
/*      */           break;
/*      */         case "bestResponseTime":
/*  204 */           this.balancer = new BestResponseTimeBalanceStrategy();
/*      */           break;
/*      */         case "serverAffinity":
/*  207 */           this.balancer = new ServerAffinityStrategy(props.getProperty(PropertyKey.serverAffinityOrder.getKeyName(), null));
/*      */           break;
/*      */         default:
/*  210 */           this.balancer = (BalanceStrategy)Class.forName(strategy).newInstance(); break;
/*      */       } 
/*  212 */     } catch (Throwable t) {
/*  213 */       throw SQLError.createSQLException(Messages.getString("InvalidLoadBalanceStrategy", new Object[] { strategy }), "S1009", t, null);
/*      */     } 
/*      */ 
/*      */     
/*  217 */     String autoCommitSwapThresholdAsString = props.getProperty(PropertyKey.loadBalanceAutoCommitStatementThreshold.getKeyName(), "0");
/*      */     try {
/*  219 */       Integer.parseInt(autoCommitSwapThresholdAsString);
/*  220 */     } catch (NumberFormatException nfe) {
/*  221 */       throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceAutoCommitStatementThreshold", new Object[] { autoCommitSwapThresholdAsString }), "S1009", null);
/*      */     } 
/*      */ 
/*      */     
/*  225 */     String autoCommitSwapRegex = props.getProperty(PropertyKey.loadBalanceAutoCommitStatementRegex.getKeyName(), "");
/*  226 */     if (!"".equals(autoCommitSwapRegex)) {
/*      */       try {
/*  228 */         "".matches(autoCommitSwapRegex);
/*  229 */       } catch (Exception e) {
/*  230 */         throw SQLError.createSQLException(
/*  231 */             Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceAutoCommitStatementRegex", new Object[] { autoCommitSwapRegex }), "S1009", null);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/*  237 */       String lbExceptionChecker = props.getProperty(PropertyKey.loadBalanceExceptionChecker.getKeyName(), StandardLoadBalanceExceptionChecker.class
/*  238 */           .getName());
/*  239 */       this.exceptionChecker = (LoadBalanceExceptionChecker)Util.getInstance(lbExceptionChecker, new Class[0], new Object[0], null, 
/*  240 */           Messages.getString("InvalidLoadBalanceExceptionChecker"));
/*  241 */       this.exceptionChecker.init(props);
/*      */     }
/*  243 */     catch (CJException e) {
/*  244 */       throw SQLExceptionsMapping.translateException(e, null);
/*      */     } 
/*      */     
/*  247 */     pickNewConnection();
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
/*      */   JdbcConnection getNewWrapperForThisAsConnection() throws SQLException {
/*  260 */     return new LoadBalancedMySQLConnection(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void propagateProxyDown(JdbcConnection proxyConn) {
/*  271 */     for (JdbcConnection c : this.liveConnections.values()) {
/*  272 */       c.setProxy(proxyConn);
/*      */     }
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public boolean shouldExceptionTriggerFailover(Throwable t) {
/*  278 */     return shouldExceptionTriggerConnectionSwitch(t);
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
/*      */   boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
/*  290 */     return (t instanceof SQLException && this.exceptionChecker.shouldExceptionTriggerFailover(t));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean isSourceConnection() {
/*  298 */     return true;
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
/*      */   synchronized void invalidateConnection(JdbcConnection conn) throws SQLException {
/*  311 */     super.invalidateConnection(conn);
/*      */ 
/*      */     
/*  314 */     if (isGlobalBlocklistEnabled()) {
/*  315 */       String host = this.connectionsToHostsMap.get(conn);
/*  316 */       if (host != null) {
/*  317 */         addToGlobalBlocklist(host);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  322 */     this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
/*  323 */     Object mappedHost = this.connectionsToHostsMap.remove(conn);
/*  324 */     if (mappedHost != null && this.hostsToListIndexMap.containsKey(mappedHost)) {
/*  325 */       int hostIndex = ((Integer)this.hostsToListIndexMap.get(mappedHost)).intValue();
/*      */       
/*  327 */       synchronized (this.responseTimes) {
/*  328 */         this.responseTimes[hostIndex] = 0L;
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
/*      */   public synchronized void pickNewConnection() throws SQLException {
/*  341 */     if (this.isClosed && this.closedExplicitly) {
/*      */       return;
/*      */     }
/*      */     
/*  345 */     List<String> hostPortList = Collections.unmodifiableList((List<? extends String>)this.hostsList.stream().map(hi -> hi.getHostPortPair()).collect(Collectors.toList()));
/*      */     
/*  347 */     if (this.currentConnection == null) {
/*  348 */       this.currentConnection = this.balancer.pickConnection(this, hostPortList, (Map)Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes
/*  349 */           .clone(), this.retriesAllDown);
/*      */       
/*      */       return;
/*      */     } 
/*  353 */     if (this.currentConnection.isClosed()) {
/*  354 */       invalidateCurrentConnection();
/*      */     }
/*      */     
/*  357 */     int pingTimeout = ((Integer)this.currentConnection.getPropertySet().getIntegerProperty(PropertyKey.loadBalancePingTimeout).getValue()).intValue();
/*  358 */     boolean pingBeforeReturn = ((Boolean)this.currentConnection.getPropertySet().getBooleanProperty(PropertyKey.loadBalanceValidateConnectionOnSwapServer).getValue()).booleanValue();
/*      */     
/*  360 */     for (int hostsTried = 0, hostsToTry = this.hostsList.size(); hostsTried < hostsToTry; hostsTried++) {
/*  361 */       ConnectionImpl newConn = null;
/*      */       try {
/*  363 */         newConn = (ConnectionImpl)this.balancer.pickConnection(this, hostPortList, (Map)Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes
/*  364 */             .clone(), this.retriesAllDown);
/*      */         
/*  366 */         if (this.currentConnection != null) {
/*  367 */           if (pingBeforeReturn) {
/*  368 */             newConn.pingInternal(true, pingTimeout);
/*      */           }
/*      */           
/*  371 */           syncSessionState(this.currentConnection, (JdbcConnection)newConn);
/*      */         } 
/*      */         
/*  374 */         this.currentConnection = (JdbcConnection)newConn;
/*      */         
/*      */         return;
/*  377 */       } catch (SQLException e) {
/*  378 */         if (shouldExceptionTriggerConnectionSwitch(e) && newConn != null)
/*      */         {
/*  380 */           invalidateConnection((JdbcConnection)newConn);
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  386 */     this.isClosed = true;
/*  387 */     this.closedReason = "Connection closed after inability to pick valid new connection during load-balance.";
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
/*      */   public synchronized ConnectionImpl createConnectionForHost(HostInfo hostInfo) throws SQLException {
/*  402 */     ConnectionImpl conn = super.createConnectionForHost(hostInfo);
/*      */     
/*  404 */     this.liveConnections.put(hostInfo.getHostPortPair(), conn);
/*  405 */     this.connectionsToHostsMap.put(conn, hostInfo.getHostPortPair());
/*      */     
/*  407 */     removeFromGlobalBlocklist(hostInfo.getHostPortPair());
/*      */     
/*  409 */     this.totalPhysicalConnections++;
/*      */     
/*  411 */     for (QueryInterceptor stmtInterceptor : conn.getQueryInterceptorsInstances()) {
/*  412 */       if (stmtInterceptor instanceof LoadBalancedAutoCommitInterceptor) {
/*  413 */         ((LoadBalancedAutoCommitInterceptor)stmtInterceptor).resumeCounters();
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/*  418 */     return conn;
/*      */   }
/*      */ 
/*      */   
/*      */   void syncSessionState(JdbcConnection source, JdbcConnection target, boolean readOnly) throws SQLException {
/*  423 */     LoadBalancedAutoCommitInterceptor lbAutoCommitStmtInterceptor = null;
/*  424 */     for (QueryInterceptor stmtInterceptor : target.getQueryInterceptorsInstances()) {
/*  425 */       if (stmtInterceptor instanceof LoadBalancedAutoCommitInterceptor) {
/*  426 */         lbAutoCommitStmtInterceptor = (LoadBalancedAutoCommitInterceptor)stmtInterceptor;
/*  427 */         lbAutoCommitStmtInterceptor.pauseCounters();
/*      */         break;
/*      */       } 
/*      */     } 
/*  431 */     super.syncSessionState(source, target, readOnly);
/*  432 */     if (lbAutoCommitStmtInterceptor != null) {
/*  433 */       lbAutoCommitStmtInterceptor.resumeCounters();
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
/*      */   public synchronized ConnectionImpl createConnectionForHost(String hostPortPair) throws SQLException {
/*  449 */     for (HostInfo hi : this.hostsList) {
/*  450 */       if (hi.getHostPortPair().equals(hostPortPair)) {
/*  451 */         return createConnectionForHost(hi);
/*      */       }
/*      */     } 
/*  454 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized void closeAllConnections() {
/*  462 */     for (Connection c : this.liveConnections.values()) {
/*      */       try {
/*  464 */         c.close();
/*  465 */       } catch (SQLException sQLException) {}
/*      */     } 
/*      */ 
/*      */     
/*  469 */     if (!this.isClosed && 
/*  470 */       this.connectionGroup != null) {
/*  471 */       this.connectionGroup.closeConnectionProxy(this);
/*      */     }
/*      */ 
/*      */     
/*  475 */     this.liveConnections.clear();
/*  476 */     this.connectionsToHostsMap.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   synchronized void doClose() {
/*  484 */     closeAllConnections();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   synchronized void doAbortInternal() {
/*  493 */     for (JdbcConnection c : this.liveConnections.values()) {
/*      */       try {
/*  495 */         c.abortInternal();
/*  496 */       } catch (SQLException sQLException) {}
/*      */     } 
/*      */ 
/*      */     
/*  500 */     if (!this.isClosed && 
/*  501 */       this.connectionGroup != null) {
/*  502 */       this.connectionGroup.closeConnectionProxy(this);
/*      */     }
/*      */ 
/*      */     
/*  506 */     this.liveConnections.clear();
/*  507 */     this.connectionsToHostsMap.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   synchronized void doAbort(Executor executor) {
/*  516 */     for (Connection c : this.liveConnections.values()) {
/*      */       try {
/*  518 */         c.abort(executor);
/*  519 */       } catch (SQLException sQLException) {}
/*      */     } 
/*      */ 
/*      */     
/*  523 */     if (!this.isClosed && 
/*  524 */       this.connectionGroup != null) {
/*  525 */       this.connectionGroup.closeConnectionProxy(this);
/*      */     }
/*      */ 
/*      */     
/*  529 */     this.liveConnections.clear();
/*  530 */     this.connectionsToHostsMap.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable {
/*  540 */     String methodName = method.getName();
/*      */     
/*  542 */     if (this.isClosed && !allowedOnClosedConnection(method) && (method.getExceptionTypes()).length > 0) {
/*  543 */       if (this.autoReconnect && !this.closedExplicitly) {
/*      */         
/*  545 */         this.currentConnection = null;
/*  546 */         pickNewConnection();
/*  547 */         this.isClosed = false;
/*  548 */         this.closedReason = null;
/*      */       } else {
/*  550 */         String reason = "No operations allowed after connection closed.";
/*  551 */         if (this.closedReason != null) {
/*  552 */           reason = reason + " " + this.closedReason;
/*      */         }
/*      */         
/*  555 */         for (Class<?> excls : method.getExceptionTypes()) {
/*  556 */           if (SQLException.class.isAssignableFrom(excls)) {
/*  557 */             throw SQLError.createSQLException(reason, "08003", null);
/*      */           }
/*      */         } 
/*      */         
/*  561 */         throw (CJCommunicationsException)ExceptionFactory.createException(CJCommunicationsException.class, reason);
/*      */       } 
/*      */     }
/*      */     
/*  565 */     if (!this.inTransaction) {
/*  566 */       this.inTransaction = true;
/*  567 */       this.transactionStartTime = System.nanoTime();
/*  568 */       this.transactionCount++;
/*      */     } 
/*      */     
/*  571 */     Object result = null;
/*      */     
/*      */     try {
/*  574 */       result = method.invoke(this.thisAsConnection, args);
/*      */       
/*  576 */       if (result != null) {
/*  577 */         if (result instanceof JdbcStatement) {
/*  578 */           ((JdbcStatement)result).setPingTarget(this);
/*      */         }
/*  580 */         result = proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), result);
/*      */       }
/*      */     
/*  583 */     } catch (InvocationTargetException e) {
/*  584 */       dealWithInvocationException(e);
/*      */     } finally {
/*      */       
/*  587 */       if ("commit".equals(methodName) || "rollback".equals(methodName)) {
/*  588 */         this.inTransaction = false;
/*      */ 
/*      */         
/*  591 */         String host = this.connectionsToHostsMap.get(this.currentConnection);
/*      */         
/*  593 */         if (host != null) {
/*  594 */           synchronized (this.responseTimes) {
/*  595 */             Integer hostIndex = this.hostsToListIndexMap.get(host);
/*      */             
/*  597 */             if (hostIndex != null && hostIndex.intValue() < this.responseTimes.length) {
/*  598 */               this.responseTimes[hostIndex.intValue()] = System.nanoTime() - this.transactionStartTime;
/*      */             }
/*      */           } 
/*      */         }
/*  602 */         pickNewConnection();
/*      */       } 
/*      */     } 
/*      */     
/*  606 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void doPing() throws SQLException {
/*  617 */     SQLException se = null;
/*  618 */     boolean foundHost = false;
/*  619 */     int pingTimeout = ((Integer)this.currentConnection.getPropertySet().getIntegerProperty(PropertyKey.loadBalancePingTimeout).getValue()).intValue();
/*      */     
/*  621 */     synchronized (this) {
/*  622 */       for (HostInfo hi : this.hostsList) {
/*  623 */         String host = hi.getHostPortPair();
/*  624 */         ConnectionImpl conn = this.liveConnections.get(host);
/*  625 */         if (conn == null) {
/*      */           continue;
/*      */         }
/*      */         try {
/*  629 */           if (pingTimeout == 0) {
/*  630 */             conn.ping();
/*      */           } else {
/*  632 */             conn.pingInternal(true, pingTimeout);
/*      */           } 
/*  634 */           foundHost = true;
/*  635 */         } catch (SQLException e) {
/*      */           
/*  637 */           if (host.equals(this.connectionsToHostsMap.get(this.currentConnection))) {
/*      */             
/*  639 */             closeAllConnections();
/*  640 */             this.isClosed = true;
/*  641 */             this.closedReason = "Connection closed because ping of current connection failed.";
/*  642 */             throw e;
/*      */           } 
/*      */ 
/*      */           
/*  646 */           if (e.getMessage().equals(Messages.getString("Connection.exceededConnectionLifetime"))) {
/*      */             
/*  648 */             if (se == null) {
/*  649 */               se = e;
/*      */             }
/*      */           } else {
/*      */             
/*  653 */             se = e;
/*  654 */             if (isGlobalBlocklistEnabled()) {
/*  655 */               addToGlobalBlocklist(host);
/*      */             }
/*      */           } 
/*      */           
/*  659 */           this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  664 */     if (!foundHost) {
/*  665 */       closeAllConnections();
/*  666 */       this.isClosed = true;
/*  667 */       this.closedReason = "Connection closed due to inability to ping any active connections.";
/*      */       
/*  669 */       if (se != null) {
/*  670 */         throw se;
/*      */       }
/*      */       
/*  673 */       ((ConnectionImpl)this.currentConnection).throwConnectionClosedException();
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
/*      */   public void addToGlobalBlocklist(String host, long timeout) {
/*  686 */     if (isGlobalBlocklistEnabled()) {
/*  687 */       synchronized (globalBlocklist) {
/*  688 */         globalBlocklist.put(host, Long.valueOf(timeout));
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
/*      */   public void removeFromGlobalBlocklist(String host) {
/*  700 */     if (isGlobalBlocklistEnabled() && globalBlocklist.containsKey(host)) {
/*  701 */       synchronized (globalBlocklist) {
/*  702 */         globalBlocklist.remove(host);
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
/*      */   @Deprecated
/*      */   public void removeFromGlobalBlacklist(String host) {
/*  716 */     removeFromGlobalBlocklist(host);
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
/*      */   @Deprecated
/*      */   public void addToGlobalBlacklist(String host, long timeout) {
/*  730 */     addToGlobalBlocklist(host, timeout);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addToGlobalBlocklist(String host) {
/*  740 */     addToGlobalBlocklist(host, System.currentTimeMillis() + this.globalBlocklistTimeout);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void addToGlobalBlacklist(String host) {
/*  752 */     addToGlobalBlocklist(host);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isGlobalBlocklistEnabled() {
/*  761 */     return (this.globalBlocklistTimeout > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public boolean isGlobalBlacklistEnabled() {
/*  772 */     return isGlobalBlocklistEnabled();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Map<String, Long> getGlobalBlocklist() {
/*  782 */     if (!isGlobalBlocklistEnabled()) {
/*  783 */       if (this.hostsToRemove.isEmpty()) {
/*  784 */         return new HashMap<>(1);
/*      */       }
/*  786 */       HashMap<String, Long> fakedBlocklist = new HashMap<>();
/*  787 */       for (String h : this.hostsToRemove) {
/*  788 */         fakedBlocklist.put(h, Long.valueOf(System.currentTimeMillis() + 5000L));
/*      */       }
/*  790 */       return fakedBlocklist;
/*      */     } 
/*      */ 
/*      */     
/*  794 */     Map<String, Long> blocklistClone = new HashMap<>(globalBlocklist.size());
/*      */     
/*  796 */     synchronized (globalBlocklist) {
/*  797 */       blocklistClone.putAll(globalBlocklist);
/*      */     } 
/*  799 */     Set<String> keys = blocklistClone.keySet();
/*      */ 
/*      */     
/*  802 */     keys.retainAll((Collection)this.hostsList.stream().map(hi -> hi.getHostPortPair()).collect(Collectors.toList()));
/*      */ 
/*      */     
/*  805 */     for (Iterator<String> i = keys.iterator(); i.hasNext(); ) {
/*  806 */       String host = i.next();
/*      */       
/*  808 */       Long timeout = globalBlocklist.get(host);
/*  809 */       if (timeout != null && timeout.longValue() < System.currentTimeMillis()) {
/*      */         
/*  811 */         synchronized (globalBlocklist) {
/*  812 */           globalBlocklist.remove(host);
/*      */         } 
/*  814 */         i.remove();
/*      */       } 
/*      */     } 
/*      */     
/*  818 */     if (keys.size() == this.hostsList.size())
/*      */     {
/*      */       
/*  821 */       return new HashMap<>(1);
/*      */     }
/*      */     
/*  824 */     return blocklistClone;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public synchronized Map<String, Long> getGlobalBlacklist() {
/*  836 */     return getGlobalBlocklist();
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
/*      */   public void removeHostWhenNotInUse(String hostPortPair) throws SQLException {
/*  848 */     if (this.hostRemovalGracePeriod <= 0) {
/*  849 */       removeHost(hostPortPair);
/*      */       
/*      */       return;
/*      */     } 
/*  853 */     int timeBetweenChecks = (this.hostRemovalGracePeriod > 1000) ? 1000 : this.hostRemovalGracePeriod;
/*      */     
/*  855 */     synchronized (this) {
/*  856 */       addToGlobalBlocklist(hostPortPair, System.currentTimeMillis() + this.hostRemovalGracePeriod + timeBetweenChecks);
/*      */       
/*  858 */       long cur = System.currentTimeMillis();
/*      */       
/*  860 */       while (System.currentTimeMillis() < cur + this.hostRemovalGracePeriod) {
/*  861 */         this.hostsToRemove.add(hostPortPair);
/*      */         
/*  863 */         if (!hostPortPair.equals(this.currentConnection.getHostPortPair())) {
/*  864 */           removeHost(hostPortPair);
/*      */           
/*      */           return;
/*      */         } 
/*      */         try {
/*  869 */           Thread.sleep(timeBetweenChecks);
/*  870 */         } catch (InterruptedException interruptedException) {}
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  876 */     removeHost(hostPortPair);
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
/*      */   public synchronized void removeHost(String hostPortPair) throws SQLException {
/*  888 */     if (this.connectionGroup != null && 
/*  889 */       this.connectionGroup.getInitialHosts().size() == 1 && this.connectionGroup.getInitialHosts().contains(hostPortPair)) {
/*  890 */       throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.0"), null);
/*      */     }
/*      */ 
/*      */     
/*  894 */     this.hostsToRemove.add(hostPortPair);
/*      */     
/*  896 */     this.connectionsToHostsMap.remove(this.liveConnections.remove(hostPortPair));
/*  897 */     if (this.hostsToListIndexMap.remove(hostPortPair) != null) {
/*  898 */       long[] newResponseTimes = new long[this.responseTimes.length - 1];
/*  899 */       int newIdx = 0;
/*  900 */       for (HostInfo hostInfo : this.hostsList) {
/*  901 */         String host = hostInfo.getHostPortPair();
/*  902 */         if (!this.hostsToRemove.contains(host)) {
/*  903 */           Integer idx = this.hostsToListIndexMap.get(host);
/*  904 */           if (idx != null && idx.intValue() < this.responseTimes.length) {
/*  905 */             newResponseTimes[newIdx] = this.responseTimes[idx.intValue()];
/*      */           }
/*  907 */           this.hostsToListIndexMap.put(host, Integer.valueOf(newIdx++));
/*      */         } 
/*      */       } 
/*  910 */       this.responseTimes = newResponseTimes;
/*      */     } 
/*      */     
/*  913 */     if (hostPortPair.equals(this.currentConnection.getHostPortPair())) {
/*  914 */       invalidateConnection(this.currentConnection);
/*  915 */       pickNewConnection();
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
/*      */   public synchronized boolean addHost(String hostPortPair) {
/*  927 */     if (this.hostsToListIndexMap.containsKey(hostPortPair)) {
/*  928 */       return false;
/*      */     }
/*      */     
/*  931 */     long[] newResponseTimes = new long[this.responseTimes.length + 1];
/*  932 */     System.arraycopy(this.responseTimes, 0, newResponseTimes, 0, this.responseTimes.length);
/*      */     
/*  934 */     this.responseTimes = newResponseTimes;
/*  935 */     if (this.hostsList.stream().noneMatch(hi -> hostPortPair.equals(hi.getHostPortPair()))) {
/*  936 */       this.hostsList.add(this.connectionUrl.getHostOrSpawnIsolated(hostPortPair));
/*      */     }
/*  938 */     this.hostsToListIndexMap.put(hostPortPair, Integer.valueOf(this.responseTimes.length - 1));
/*  939 */     this.hostsToRemove.remove(hostPortPair);
/*      */     
/*  941 */     return true;
/*      */   }
/*      */   
/*      */   public synchronized boolean inTransaction() {
/*  945 */     return this.inTransaction;
/*      */   }
/*      */   
/*      */   public synchronized long getTransactionCount() {
/*  949 */     return this.transactionCount;
/*      */   }
/*      */   
/*      */   public synchronized long getActivePhysicalConnectionCount() {
/*  953 */     return this.liveConnections.size();
/*      */   }
/*      */   
/*      */   public synchronized long getTotalPhysicalConnectionCount() {
/*  957 */     return this.totalPhysicalConnections;
/*      */   }
/*      */   
/*      */   public synchronized long getConnectionGroupProxyID() {
/*  961 */     return this.connectionGroupProxyID;
/*      */   }
/*      */   
/*      */   public synchronized String getCurrentActiveHost() {
/*  965 */     JdbcConnection c = this.currentConnection;
/*  966 */     if (c != null) {
/*  967 */       Object o = this.connectionsToHostsMap.get(c);
/*  968 */       if (o != null) {
/*  969 */         return o.toString();
/*      */       }
/*      */     } 
/*  972 */     return null;
/*      */   }
/*      */   
/*      */   public synchronized long getCurrentTransactionDuration() {
/*  976 */     if (this.inTransaction && this.transactionStartTime > 0L) {
/*  977 */       return System.nanoTime() - this.transactionStartTime;
/*      */     }
/*  979 */     return 0L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class NullLoadBalancedConnectionProxy
/*      */     implements InvocationHandler
/*      */   {
/*      */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*  992 */       SQLException exceptionToThrow = SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.unusableConnection"), "25000", 1000001, true, null);
/*      */       
/*  994 */       Class<?>[] declaredException = method.getExceptionTypes();
/*  995 */       for (Class<?> declEx : declaredException) {
/*  996 */         if (declEx.isAssignableFrom(exceptionToThrow.getClass())) {
/*  997 */           throw exceptionToThrow;
/*      */         }
/*      */       } 
/* 1000 */       throw new IllegalStateException(exceptionToThrow.getMessage(), exceptionToThrow);
/*      */     }
/*      */   }
/*      */   
/* 1004 */   private static LoadBalancedConnection nullLBConnectionInstance = null;
/*      */   
/*      */   static synchronized LoadBalancedConnection getNullLoadBalancedConnectionInstance() {
/* 1007 */     if (nullLBConnectionInstance == null) {
/* 1008 */       nullLBConnectionInstance = (LoadBalancedConnection)Proxy.newProxyInstance(LoadBalancedConnection.class.getClassLoader(), INTERFACES_TO_PROXY, new NullLoadBalancedConnectionProxy());
/*      */     }
/*      */     
/* 1011 */     return nullLBConnectionInstance;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\LoadBalancedConnectionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */