/*     */ package com.mysql.cj.jdbc.ha;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.PingTarget;
/*     */ import com.mysql.cj.conf.ConnectionUrl;
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.HostsListView;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.url.LoadBalanceConnectionUrl;
/*     */ import com.mysql.cj.conf.url.ReplicationConnectionUrl;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import com.mysql.cj.jdbc.JdbcStatement;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class ReplicationConnectionProxy
/*     */   extends MultiHostConnectionProxy
/*     */   implements PingTarget
/*     */ {
/*     */   private ReplicationConnection thisAsReplicationConnection;
/*     */   protected boolean enableJMX = false;
/*     */   protected boolean allowSourceDownConnections = false;
/*     */   protected boolean allowReplicaDownConnections = false;
/*     */   protected boolean readFromSourceWhenNoReplicas = false;
/*     */   protected boolean readFromSourceWhenNoReplicasOriginal = false;
/*     */   protected boolean readOnly = false;
/*     */   ReplicationConnectionGroup connectionGroup;
/*  71 */   private long connectionGroupID = -1L;
/*     */ 
/*     */ 
/*     */   
/*     */   private List<HostInfo> sourceHosts;
/*     */ 
/*     */   
/*     */   protected LoadBalancedConnection sourceConnection;
/*     */ 
/*     */   
/*     */   private List<HostInfo> replicaHosts;
/*     */ 
/*     */   
/*     */   protected LoadBalancedConnection replicasConnection;
/*     */ 
/*     */ 
/*     */   
/*     */   public static ReplicationConnection createProxyInstance(ConnectionUrl connectionUrl) throws SQLException {
/*  89 */     ReplicationConnectionProxy connProxy = new ReplicationConnectionProxy(connectionUrl);
/*  90 */     return (ReplicationConnection)Proxy.newProxyInstance(ReplicationConnection.class.getClassLoader(), new Class[] { ReplicationConnection.class, JdbcConnection.class }, connProxy);
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
/*     */   private ReplicationConnectionProxy(ConnectionUrl connectionUrl) throws SQLException {
/* 106 */     Properties props = connectionUrl.getConnectionArgumentsAsProperties();
/*     */     
/* 108 */     this.thisAsReplicationConnection = (ReplicationConnection)this.thisAsConnection;
/*     */     
/* 110 */     this.connectionUrl = connectionUrl;
/*     */     
/* 112 */     String enableJMXAsString = props.getProperty(PropertyKey.ha_enableJMX.getKeyName(), "false");
/*     */     try {
/* 114 */       this.enableJMX = Boolean.parseBoolean(enableJMXAsString);
/* 115 */     } catch (Exception e) {
/* 116 */       throw SQLError.createSQLException(Messages.getString("MultihostConnection.badValueForHaEnableJMX", new Object[] { enableJMXAsString }), "S1009", null);
/*     */     } 
/*     */ 
/*     */     
/* 120 */     String allowSourceDownConnectionsAsString = props.getProperty(PropertyKey.allowSourceDownConnections.getKeyName(), "false");
/*     */     try {
/* 122 */       this.allowSourceDownConnections = Boolean.parseBoolean(allowSourceDownConnectionsAsString);
/* 123 */     } catch (Exception e) {
/* 124 */       throw SQLError.createSQLException(
/* 125 */           Messages.getString("ReplicationConnectionProxy.badValueForAllowSourceDownConnections", new Object[] { enableJMXAsString }), "S1009", null);
/*     */     } 
/*     */ 
/*     */     
/* 129 */     String allowReplicaDownConnectionsAsString = props.getProperty(PropertyKey.allowReplicaDownConnections.getKeyName(), "false");
/*     */     try {
/* 131 */       this.allowReplicaDownConnections = Boolean.parseBoolean(allowReplicaDownConnectionsAsString);
/* 132 */     } catch (Exception e) {
/* 133 */       throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.badValueForAllowReplicaDownConnections", new Object[] { allowReplicaDownConnectionsAsString }), "S1009", null);
/*     */     } 
/*     */ 
/*     */     
/* 137 */     String readFromSourceWhenNoReplicasAsString = props.getProperty(PropertyKey.readFromSourceWhenNoReplicas.getKeyName());
/*     */     try {
/* 139 */       this.readFromSourceWhenNoReplicasOriginal = Boolean.parseBoolean(readFromSourceWhenNoReplicasAsString);
/*     */     }
/* 141 */     catch (Exception e) {
/* 142 */       throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.badValueForReadFromSourceWhenNoReplicas", new Object[] { readFromSourceWhenNoReplicasAsString }), "S1009", null);
/*     */     } 
/*     */ 
/*     */     
/* 146 */     String group = props.getProperty(PropertyKey.replicationConnectionGroup.getKeyName(), null);
/* 147 */     if (!StringUtils.isNullOrEmpty(group) && ReplicationConnectionUrl.class.isAssignableFrom(connectionUrl.getClass())) {
/* 148 */       this.connectionGroup = ReplicationConnectionGroupManager.getConnectionGroupInstance(group);
/* 149 */       if (this.enableJMX) {
/* 150 */         ReplicationConnectionGroupManager.registerJmx();
/*     */       }
/* 152 */       this.connectionGroupID = this.connectionGroup.registerReplicationConnection(this.thisAsReplicationConnection, ((ReplicationConnectionUrl)connectionUrl)
/* 153 */           .getSourcesListAsHostPortPairs(), ((ReplicationConnectionUrl)connectionUrl)
/* 154 */           .getReplicasListAsHostPortPairs());
/*     */       
/* 156 */       this.sourceHosts = ((ReplicationConnectionUrl)connectionUrl).getSourceHostsListFromHostPortPairs(this.connectionGroup.getSourceHosts());
/* 157 */       this.replicaHosts = ((ReplicationConnectionUrl)connectionUrl).getReplicaHostsListFromHostPortPairs(this.connectionGroup.getReplicaHosts());
/*     */     } else {
/* 159 */       this.sourceHosts = new ArrayList<>(connectionUrl.getHostsList(HostsListView.SOURCES));
/* 160 */       this.replicaHosts = new ArrayList<>(connectionUrl.getHostsList(HostsListView.REPLICAS));
/*     */     } 
/*     */     
/* 163 */     resetReadFromSourceWhenNoReplicas();
/*     */ 
/*     */     
/*     */     try {
/* 167 */       initializeReplicasConnection();
/* 168 */     } catch (SQLException e) {
/* 169 */       if (!this.allowReplicaDownConnections) {
/* 170 */         if (this.connectionGroup != null) {
/* 171 */           this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
/*     */         }
/* 173 */         throw e;
/*     */       } 
/*     */     } 
/*     */     
/* 177 */     SQLException exCaught = null;
/*     */     try {
/* 179 */       this.currentConnection = initializeSourceConnection();
/* 180 */     } catch (SQLException e) {
/* 181 */       exCaught = e;
/*     */     } 
/*     */     
/* 184 */     if (this.currentConnection == null) {
/* 185 */       if (this.allowSourceDownConnections && this.replicasConnection != null) {
/*     */         
/* 187 */         this.readOnly = true;
/* 188 */         this.currentConnection = this.replicasConnection;
/*     */       } else {
/* 190 */         if (this.connectionGroup != null) {
/* 191 */           this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
/*     */         }
/* 193 */         if (exCaught != null) {
/* 194 */           throw exCaught;
/*     */         }
/* 196 */         throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.initializationWithEmptyHostsLists"), "S1009", null);
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
/*     */   JdbcConnection getNewWrapperForThisAsConnection() throws SQLException {
/* 210 */     return new ReplicationMySQLConnection(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void propagateProxyDown(JdbcConnection proxyConn) {
/* 221 */     if (this.sourceConnection != null) {
/* 222 */       this.sourceConnection.setProxy(proxyConn);
/*     */     }
/* 224 */     if (this.replicasConnection != null) {
/* 225 */       this.replicasConnection.setProxy(proxyConn);
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
/*     */   boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
/* 237 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSourceConnection() {
/* 245 */     return (this.currentConnection != null && this.currentConnection == this.sourceConnection);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReplicasConnection() {
/* 254 */     return (this.currentConnection != null && this.currentConnection == this.replicasConnection);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean isSlavesConnection() {
/* 265 */     return isReplicasConnection();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void pickNewConnection() throws SQLException {}
/*     */ 
/*     */ 
/*     */   
/*     */   void syncSessionState(JdbcConnection source, JdbcConnection target, boolean readonly) throws SQLException {
/*     */     try {
/* 276 */       super.syncSessionState(source, target, readonly);
/* 277 */     } catch (SQLException e1) {
/*     */       
/*     */       try {
/* 280 */         super.syncSessionState(source, target, readonly);
/* 281 */       } catch (SQLException sQLException) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void doClose() throws SQLException {
/* 289 */     if (this.sourceConnection != null) {
/* 290 */       this.sourceConnection.close();
/*     */     }
/* 292 */     if (this.replicasConnection != null) {
/* 293 */       this.replicasConnection.close();
/*     */     }
/*     */     
/* 296 */     if (this.connectionGroup != null) {
/* 297 */       this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   void doAbortInternal() throws SQLException {
/* 303 */     this.sourceConnection.abortInternal();
/* 304 */     this.replicasConnection.abortInternal();
/* 305 */     if (this.connectionGroup != null) {
/* 306 */       this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   void doAbort(Executor executor) throws SQLException {
/* 312 */     this.sourceConnection.abort(executor);
/* 313 */     this.replicasConnection.abort(executor);
/* 314 */     if (this.connectionGroup != null) {
/* 315 */       this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable {
/* 325 */     checkConnectionCapabilityForMethod(method);
/*     */     
/* 327 */     boolean invokeAgain = false;
/*     */     while (true) {
/*     */       try {
/* 330 */         Object result = method.invoke(this.thisAsConnection, args);
/* 331 */         if (result != null && result instanceof JdbcStatement) {
/* 332 */           ((JdbcStatement)result).setPingTarget(this);
/*     */         }
/* 334 */         return result;
/* 335 */       } catch (InvocationTargetException e) {
/* 336 */         if (invokeAgain) {
/* 337 */           invokeAgain = false;
/* 338 */         } else if (e.getCause() != null && e.getCause() instanceof SQLException && ((SQLException)e
/* 339 */           .getCause()).getSQLState() == "25000" && ((SQLException)e
/* 340 */           .getCause()).getErrorCode() == 1000001) {
/*     */           
/*     */           try {
/* 343 */             setReadOnly(this.readOnly);
/* 344 */             invokeAgain = true;
/* 345 */           } catch (SQLException sQLException) {}
/*     */         } 
/*     */ 
/*     */         
/* 349 */         if (!invokeAgain) {
/* 350 */           throw e;
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
/*     */   private void checkConnectionCapabilityForMethod(Method method) throws Throwable {
/* 367 */     if (this.sourceHosts.isEmpty() && this.replicaHosts.isEmpty() && !ReplicationConnection.class.isAssignableFrom(method.getDeclaringClass())) {
/* 368 */       throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.noHostsInconsistentState"), "25000", 1000002, true, null);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void doPing() throws SQLException {
/* 378 */     boolean isSourceConn = isSourceConnection();
/*     */     
/* 380 */     SQLException sourcesPingException = null;
/* 381 */     SQLException replicasPingException = null;
/*     */     
/* 383 */     if (this.sourceConnection != null) {
/*     */       try {
/* 385 */         this.sourceConnection.ping();
/* 386 */       } catch (SQLException e) {
/* 387 */         sourcesPingException = e;
/*     */       } 
/*     */     } else {
/* 390 */       initializeSourceConnection();
/*     */     } 
/*     */     
/* 393 */     if (this.replicasConnection != null) {
/*     */       try {
/* 395 */         this.replicasConnection.ping();
/* 396 */       } catch (SQLException e) {
/* 397 */         replicasPingException = e;
/*     */       } 
/*     */     } else {
/*     */       try {
/* 401 */         initializeReplicasConnection();
/* 402 */         if (switchToReplicasConnectionIfNecessary()) {
/* 403 */           isSourceConn = false;
/*     */         }
/* 405 */       } catch (SQLException e) {
/* 406 */         if (this.sourceConnection == null || !this.readFromSourceWhenNoReplicas) {
/* 407 */           throw e;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 412 */     if (isSourceConn && sourcesPingException != null) {
/*     */       
/* 414 */       if (this.replicasConnection != null && replicasPingException == null) {
/* 415 */         this.sourceConnection = null;
/* 416 */         this.currentConnection = this.replicasConnection;
/* 417 */         this.readOnly = true;
/*     */       } 
/* 419 */       throw sourcesPingException;
/*     */     } 
/* 421 */     if (!isSourceConn && (replicasPingException != null || this.replicasConnection == null)) {
/*     */       
/* 423 */       if (this.sourceConnection != null && this.readFromSourceWhenNoReplicas && sourcesPingException == null) {
/* 424 */         this.replicasConnection = null;
/* 425 */         this.currentConnection = this.sourceConnection;
/* 426 */         this.readOnly = true;
/* 427 */         this.currentConnection.setReadOnly(true);
/*     */       } 
/* 429 */       if (replicasPingException != null) {
/* 430 */         throw replicasPingException;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private JdbcConnection initializeSourceConnection() throws SQLException {
/* 436 */     this.sourceConnection = null;
/*     */     
/* 438 */     if (this.sourceHosts.size() == 0) {
/* 439 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 443 */     LoadBalancedConnection newSourceConn = LoadBalancedConnectionProxy.createProxyInstance((ConnectionUrl)new LoadBalanceConnectionUrl(this.sourceHosts, this.connectionUrl.getOriginalProperties()));
/* 444 */     newSourceConn.setProxy(getProxy());
/*     */     
/* 446 */     this.sourceConnection = newSourceConn;
/* 447 */     return this.sourceConnection;
/*     */   }
/*     */   
/*     */   private JdbcConnection initializeReplicasConnection() throws SQLException {
/* 451 */     this.replicasConnection = null;
/*     */     
/* 453 */     if (this.replicaHosts.size() == 0) {
/* 454 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 458 */     LoadBalancedConnection newReplicasConn = LoadBalancedConnectionProxy.createProxyInstance((ConnectionUrl)new LoadBalanceConnectionUrl(this.replicaHosts, this.connectionUrl.getOriginalProperties()));
/* 459 */     newReplicasConn.setProxy(getProxy());
/* 460 */     newReplicasConn.setReadOnly(true);
/*     */     
/* 462 */     this.replicasConnection = newReplicasConn;
/* 463 */     return this.replicasConnection;
/*     */   }
/*     */   
/*     */   private synchronized boolean switchToSourceConnection() throws SQLException {
/* 467 */     if (this.sourceConnection == null || this.sourceConnection.isClosed()) {
/*     */       try {
/* 469 */         if (initializeSourceConnection() == null) {
/* 470 */           return false;
/*     */         }
/* 472 */       } catch (SQLException e) {
/* 473 */         this.currentConnection = null;
/* 474 */         throw e;
/*     */       } 
/*     */     }
/* 477 */     if (!isSourceConnection() && this.sourceConnection != null) {
/* 478 */       syncSessionState(this.currentConnection, this.sourceConnection, false);
/* 479 */       this.currentConnection = this.sourceConnection;
/*     */     } 
/* 481 */     return true;
/*     */   }
/*     */   
/*     */   private synchronized boolean switchToReplicasConnection() throws SQLException {
/* 485 */     if (this.replicasConnection == null || this.replicasConnection.isClosed()) {
/*     */       try {
/* 487 */         if (initializeReplicasConnection() == null) {
/* 488 */           return false;
/*     */         }
/* 490 */       } catch (SQLException e) {
/* 491 */         this.currentConnection = null;
/* 492 */         throw e;
/*     */       } 
/*     */     }
/* 495 */     if (!isReplicasConnection() && this.replicasConnection != null) {
/* 496 */       syncSessionState(this.currentConnection, this.replicasConnection, true);
/* 497 */       this.currentConnection = this.replicasConnection;
/*     */     } 
/* 499 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean switchToReplicasConnectionIfNecessary() throws SQLException {
/* 508 */     if (this.currentConnection == null || (isSourceConnection() && (this.readOnly || (this.sourceHosts.isEmpty() && this.currentConnection.isClosed()))) || (
/* 509 */       !isSourceConnection() && this.currentConnection.isClosed())) {
/* 510 */       return switchToReplicasConnection();
/*     */     }
/* 512 */     return false;
/*     */   }
/*     */   
/*     */   public synchronized JdbcConnection getCurrentConnection() {
/* 516 */     return (this.currentConnection == null) ? LoadBalancedConnectionProxy.getNullLoadBalancedConnectionInstance() : this.currentConnection;
/*     */   }
/*     */   
/*     */   public long getConnectionGroupId() {
/* 520 */     return this.connectionGroupID;
/*     */   }
/*     */   
/*     */   public synchronized JdbcConnection getSourceConnection() {
/* 524 */     return this.sourceConnection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public synchronized JdbcConnection getMasterConnection() {
/* 535 */     return getSourceConnection();
/*     */   }
/*     */   
/*     */   public synchronized void promoteReplicaToSource(String hostPortPair) throws SQLException {
/* 539 */     HostInfo host = getReplicaHost(hostPortPair);
/* 540 */     if (host == null) {
/*     */       return;
/*     */     }
/* 543 */     this.sourceHosts.add(host);
/* 544 */     removeReplica(hostPortPair);
/* 545 */     if (this.sourceConnection != null) {
/* 546 */       this.sourceConnection.addHost(hostPortPair);
/*     */     }
/*     */ 
/*     */     
/* 550 */     if (!this.readOnly && !isSourceConnection()) {
/* 551 */       switchToSourceConnection();
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
/*     */   @Deprecated
/*     */   public synchronized void promoteSlaveToMaster(String hostPortPair) throws SQLException {
/* 565 */     promoteReplicaToSource(hostPortPair);
/*     */   }
/*     */   
/*     */   public synchronized void removeSourceHost(String hostPortPair) throws SQLException {
/* 569 */     removeSourceHost(hostPortPair, true);
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
/*     */   @Deprecated
/*     */   public synchronized void removeMasterHost(String hostPortPair) throws SQLException {
/* 582 */     removeSourceHost(hostPortPair);
/*     */   }
/*     */   
/*     */   public synchronized void removeSourceHost(String hostPortPair, boolean waitUntilNotInUse) throws SQLException {
/* 586 */     removeSourceHost(hostPortPair, waitUntilNotInUse, false);
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
/*     */   @Deprecated
/*     */   public synchronized void removeMasterHost(String hostPortPair, boolean waitUntilNotInUse) throws SQLException {
/* 601 */     removeSourceHost(hostPortPair, waitUntilNotInUse);
/*     */   }
/*     */   
/*     */   public synchronized void removeSourceHost(String hostPortPair, boolean waitUntilNotInUse, boolean isNowReplica) throws SQLException {
/* 605 */     HostInfo host = getSourceHost(hostPortPair);
/* 606 */     if (host == null) {
/*     */       return;
/*     */     }
/* 609 */     if (isNowReplica) {
/* 610 */       this.replicaHosts.add(host);
/* 611 */       resetReadFromSourceWhenNoReplicas();
/*     */     } 
/* 613 */     this.sourceHosts.remove(host);
/*     */ 
/*     */     
/* 616 */     if (this.sourceConnection == null || this.sourceConnection.isClosed()) {
/* 617 */       this.sourceConnection = null;
/*     */       
/*     */       return;
/*     */     } 
/* 621 */     if (waitUntilNotInUse) {
/* 622 */       this.sourceConnection.removeHostWhenNotInUse(hostPortPair);
/*     */     } else {
/* 624 */       this.sourceConnection.removeHost(hostPortPair);
/*     */     } 
/*     */ 
/*     */     
/* 628 */     if (this.sourceHosts.isEmpty()) {
/* 629 */       this.sourceConnection.close();
/* 630 */       this.sourceConnection = null;
/*     */ 
/*     */       
/* 633 */       switchToReplicasConnectionIfNecessary();
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
/*     */   @Deprecated
/*     */   public synchronized void removeMasterHost(String hostPortPair, boolean waitUntilNotInUse, boolean isNowReplica) throws SQLException {
/* 651 */     removeSourceHost(hostPortPair, waitUntilNotInUse, isNowReplica);
/*     */   }
/*     */   
/*     */   public boolean isHostSource(String hostPortPair) {
/* 655 */     if (hostPortPair == null) {
/* 656 */       return false;
/*     */     }
/* 658 */     return this.sourceHosts.stream().anyMatch(hi -> hostPortPair.equalsIgnoreCase(hi.getHostPortPair()));
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
/*     */   @Deprecated
/*     */   public boolean isHostMaster(String hostPortPair) {
/* 671 */     return isHostSource(hostPortPair);
/*     */   }
/*     */   
/*     */   public synchronized JdbcConnection getReplicasConnection() {
/* 675 */     return this.replicasConnection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public synchronized JdbcConnection getSlavesConnection() {
/* 686 */     return getReplicasConnection();
/*     */   }
/*     */   
/*     */   public synchronized void addReplicaHost(String hostPortPair) throws SQLException {
/* 690 */     if (isHostReplica(hostPortPair)) {
/*     */       return;
/*     */     }
/* 693 */     this.replicaHosts.add(getConnectionUrl().getReplicaHostOrSpawnIsolated(hostPortPair));
/* 694 */     resetReadFromSourceWhenNoReplicas();
/* 695 */     if (this.replicasConnection == null) {
/* 696 */       initializeReplicasConnection();
/* 697 */       switchToReplicasConnectionIfNecessary();
/*     */     } else {
/* 699 */       this.replicasConnection.addHost(hostPortPair);
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
/*     */   @Deprecated
/*     */   public synchronized void addSlaveHost(String hostPortPair) throws SQLException {
/* 713 */     addReplicaHost(hostPortPair);
/*     */   }
/*     */   
/*     */   public synchronized void removeReplica(String hostPortPair) throws SQLException {
/* 717 */     removeReplica(hostPortPair, true);
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
/*     */   @Deprecated
/*     */   public synchronized void removeSlave(String hostPortPair) throws SQLException {
/* 730 */     removeReplica(hostPortPair);
/*     */   }
/*     */   
/*     */   public synchronized void removeReplica(String hostPortPair, boolean closeGently) throws SQLException {
/* 734 */     HostInfo host = getReplicaHost(hostPortPair);
/* 735 */     if (host == null) {
/*     */       return;
/*     */     }
/* 738 */     this.replicaHosts.remove(host);
/* 739 */     resetReadFromSourceWhenNoReplicas();
/*     */     
/* 741 */     if (this.replicasConnection == null || this.replicasConnection.isClosed()) {
/* 742 */       this.replicasConnection = null;
/*     */       
/*     */       return;
/*     */     } 
/* 746 */     if (closeGently) {
/* 747 */       this.replicasConnection.removeHostWhenNotInUse(hostPortPair);
/*     */     } else {
/* 749 */       this.replicasConnection.removeHost(hostPortPair);
/*     */     } 
/*     */ 
/*     */     
/* 753 */     if (this.replicaHosts.isEmpty()) {
/* 754 */       this.replicasConnection.close();
/* 755 */       this.replicasConnection = null;
/*     */ 
/*     */       
/* 758 */       switchToSourceConnection();
/* 759 */       if (isSourceConnection()) {
/* 760 */         this.currentConnection.setReadOnly(this.readOnly);
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
/*     */   @Deprecated
/*     */   public synchronized void removeSlave(String hostPortPair, boolean closeGently) throws SQLException {
/* 777 */     removeReplica(hostPortPair, closeGently);
/*     */   }
/*     */   
/*     */   public boolean isHostReplica(String hostPortPair) {
/* 781 */     if (hostPortPair == null) {
/* 782 */       return false;
/*     */     }
/* 784 */     return this.replicaHosts.stream().anyMatch(hi -> hostPortPair.equalsIgnoreCase(hi.getHostPortPair()));
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
/*     */   @Deprecated
/*     */   public boolean isHostSlave(String hostPortPair) {
/* 797 */     return isHostReplica(hostPortPair);
/*     */   }
/*     */   
/*     */   public synchronized void setReadOnly(boolean readOnly) throws SQLException {
/* 801 */     if (readOnly) {
/* 802 */       if (!isReplicasConnection() || this.currentConnection.isClosed()) {
/* 803 */         boolean switched = true;
/* 804 */         SQLException exceptionCaught = null;
/*     */         try {
/* 806 */           switched = switchToReplicasConnection();
/* 807 */         } catch (SQLException e) {
/* 808 */           switched = false;
/* 809 */           exceptionCaught = e;
/*     */         } 
/* 811 */         if (!switched && this.readFromSourceWhenNoReplicas && switchToSourceConnection()) {
/* 812 */           exceptionCaught = null;
/*     */         }
/* 814 */         if (exceptionCaught != null) {
/* 815 */           throw exceptionCaught;
/*     */         }
/*     */       }
/*     */     
/* 819 */     } else if (!isSourceConnection() || this.currentConnection.isClosed()) {
/* 820 */       boolean switched = true;
/* 821 */       SQLException exceptionCaught = null;
/*     */       try {
/* 823 */         switched = switchToSourceConnection();
/* 824 */       } catch (SQLException e) {
/* 825 */         switched = false;
/* 826 */         exceptionCaught = e;
/*     */       } 
/* 828 */       if (!switched && switchToReplicasConnectionIfNecessary()) {
/* 829 */         exceptionCaught = null;
/*     */       }
/* 831 */       if (exceptionCaught != null) {
/* 832 */         throw exceptionCaught;
/*     */       }
/*     */     } 
/*     */     
/* 836 */     this.readOnly = readOnly;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 842 */     if (this.readFromSourceWhenNoReplicas && isSourceConnection()) {
/* 843 */       this.currentConnection.setReadOnly(this.readOnly);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isReadOnly() throws SQLException {
/* 848 */     return (!isSourceConnection() || this.readOnly);
/*     */   }
/*     */   
/*     */   private void resetReadFromSourceWhenNoReplicas() {
/* 852 */     this.readFromSourceWhenNoReplicas = (this.replicaHosts.isEmpty() || this.readFromSourceWhenNoReplicasOriginal);
/*     */   }
/*     */   
/*     */   private HostInfo getSourceHost(String hostPortPair) {
/* 856 */     return this.sourceHosts.stream().filter(hi -> hostPortPair.equalsIgnoreCase(hi.getHostPortPair())).findFirst().orElse(null);
/*     */   }
/*     */   
/*     */   private HostInfo getReplicaHost(String hostPortPair) {
/* 860 */     return this.replicaHosts.stream().filter(hi -> hostPortPair.equalsIgnoreCase(hi.getHostPortPair())).findFirst().orElse(null);
/*     */   }
/*     */   
/*     */   private ReplicationConnectionUrl getConnectionUrl() {
/* 864 */     return (ReplicationConnectionUrl)this.connectionUrl;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\ReplicationConnectionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */