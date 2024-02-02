package com.mysql.cj.jdbc.ha;

import com.mysql.cj.Messages;
import com.mysql.cj.PingTarget;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.url.LoadBalanceConnectionUrl;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.jdbc.ConnectionGroup;
import com.mysql.cj.jdbc.ConnectionGroupManager;
import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.JdbcStatement;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.Util;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class LoadBalancedConnectionProxy extends MultiHostConnectionProxy implements PingTarget {
   private ConnectionGroup connectionGroup = null;
   private long connectionGroupProxyID = 0L;
   protected Map<String, ConnectionImpl> liveConnections;
   private Map<String, Integer> hostsToListIndexMap;
   private Map<ConnectionImpl, String> connectionsToHostsMap;
   private long totalPhysicalConnections = 0L;
   private long[] responseTimes;
   private int retriesAllDown;
   private BalanceStrategy balancer;
   private int globalBlocklistTimeout = 0;
   private static Map<String, Long> globalBlocklist = new HashMap();
   private int hostRemovalGracePeriod = 0;
   private Set<String> hostsToRemove = new HashSet();
   private boolean inTransaction = false;
   private long transactionStartTime = 0L;
   private long transactionCount = 0L;
   private LoadBalanceExceptionChecker exceptionChecker;
   private static Class<?>[] INTERFACES_TO_PROXY = new Class[]{LoadBalancedConnection.class, JdbcConnection.class};
   private static LoadBalancedConnection nullLBConnectionInstance = null;

   public static LoadBalancedConnection createProxyInstance(ConnectionUrl connectionUrl) throws SQLException {
      LoadBalancedConnectionProxy connProxy = new LoadBalancedConnectionProxy(connectionUrl);
      return (LoadBalancedConnection)Proxy.newProxyInstance(LoadBalancedConnection.class.getClassLoader(), INTERFACES_TO_PROXY, connProxy);
   }

   public LoadBalancedConnectionProxy(ConnectionUrl connectionUrl) throws SQLException {
      Properties props = connectionUrl.getConnectionArgumentsAsProperties();
      String group = props.getProperty(PropertyKey.loadBalanceConnectionGroup.getKeyName(), (String)null);
      boolean enableJMX = false;
      String enableJMXAsString = props.getProperty(PropertyKey.ha_enableJMX.getKeyName(), "false");

      try {
         enableJMX = Boolean.parseBoolean(enableJMXAsString);
      } catch (Exception var22) {
         throw SQLError.createSQLException(Messages.getString("MultihostConnection.badValueForHaEnableJMX", new Object[]{enableJMXAsString}), "S1009", (ExceptionInterceptor)null);
      }

      List hosts;
      if (!StringUtils.isNullOrEmpty(group) && LoadBalanceConnectionUrl.class.isAssignableFrom(connectionUrl.getClass())) {
         this.connectionGroup = ConnectionGroupManager.getConnectionGroupInstance(group);
         if (enableJMX) {
            ConnectionGroupManager.registerJmx();
         }

         this.connectionGroupProxyID = this.connectionGroup.registerConnectionProxy(this, ((LoadBalanceConnectionUrl)connectionUrl).getHostInfoListAsHostPortPairs());
         hosts = ((LoadBalanceConnectionUrl)connectionUrl).getHostInfoListFromHostPortPairs(this.connectionGroup.getInitialHosts());
      } else {
         hosts = connectionUrl.getHostsList();
      }

      int numHosts = this.initializeHostsSpecs(connectionUrl, hosts);
      this.liveConnections = new HashMap(numHosts);
      this.hostsToListIndexMap = new HashMap(numHosts);

      for(int i = 0; i < numHosts; ++i) {
         this.hostsToListIndexMap.put(((HostInfo)this.hostsList.get(i)).getHostPortPair(), i);
      }

      this.connectionsToHostsMap = new HashMap(numHosts);
      this.responseTimes = new long[numHosts];
      String retriesAllDownAsString = props.getProperty(PropertyKey.retriesAllDown.getKeyName(), "120");

      try {
         this.retriesAllDown = Integer.parseInt(retriesAllDownAsString);
      } catch (NumberFormatException var21) {
         throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForRetriesAllDown", new Object[]{retriesAllDownAsString}), "S1009", (ExceptionInterceptor)null);
      }

      String blocklistTimeoutAsString = props.getProperty(PropertyKey.loadBalanceBlocklistTimeout.getKeyName(), "0");

      try {
         this.globalBlocklistTimeout = Integer.parseInt(blocklistTimeoutAsString);
      } catch (NumberFormatException var20) {
         throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceBlocklistTimeout", new Object[]{blocklistTimeoutAsString}), "S1009", (ExceptionInterceptor)null);
      }

      String hostRemovalGracePeriodAsString = props.getProperty(PropertyKey.loadBalanceHostRemovalGracePeriod.getKeyName(), "15000");

      try {
         this.hostRemovalGracePeriod = Integer.parseInt(hostRemovalGracePeriodAsString);
      } catch (NumberFormatException var19) {
         throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceHostRemovalGracePeriod", new Object[]{hostRemovalGracePeriodAsString}), "S1009", (ExceptionInterceptor)null);
      }

      String strategy = props.getProperty(PropertyKey.ha_loadBalanceStrategy.getKeyName(), "random");

      try {
         switch (strategy) {
            case "random":
               this.balancer = new RandomBalanceStrategy();
               break;
            case "bestResponseTime":
               this.balancer = new BestResponseTimeBalanceStrategy();
               break;
            case "serverAffinity":
               this.balancer = new ServerAffinityStrategy(props.getProperty(PropertyKey.serverAffinityOrder.getKeyName(), (String)null));
               break;
            default:
               this.balancer = (BalanceStrategy)Class.forName(strategy).newInstance();
         }
      } catch (Throwable var18) {
         throw SQLError.createSQLException(Messages.getString("InvalidLoadBalanceStrategy", new Object[]{strategy}), "S1009", var18, (ExceptionInterceptor)null);
      }

      String autoCommitSwapThresholdAsString = props.getProperty(PropertyKey.loadBalanceAutoCommitStatementThreshold.getKeyName(), "0");

      try {
         Integer.parseInt(autoCommitSwapThresholdAsString);
      } catch (NumberFormatException var17) {
         throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceAutoCommitStatementThreshold", new Object[]{autoCommitSwapThresholdAsString}), "S1009", (ExceptionInterceptor)null);
      }

      String autoCommitSwapRegex = props.getProperty(PropertyKey.loadBalanceAutoCommitStatementRegex.getKeyName(), "");
      if (!"".equals(autoCommitSwapRegex)) {
         try {
            "".matches(autoCommitSwapRegex);
         } catch (Exception var16) {
            throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceAutoCommitStatementRegex", new Object[]{autoCommitSwapRegex}), "S1009", (ExceptionInterceptor)null);
         }
      }

      try {
         String lbExceptionChecker = props.getProperty(PropertyKey.loadBalanceExceptionChecker.getKeyName(), StandardLoadBalanceExceptionChecker.class.getName());
         this.exceptionChecker = (LoadBalanceExceptionChecker)Util.getInstance(lbExceptionChecker, new Class[0], new Object[0], (ExceptionInterceptor)null, Messages.getString("InvalidLoadBalanceExceptionChecker"));
         this.exceptionChecker.init(props);
      } catch (CJException var15) {
         throw SQLExceptionsMapping.translateException(var15, (ExceptionInterceptor)null);
      }

      this.pickNewConnection();
   }

   JdbcConnection getNewWrapperForThisAsConnection() throws SQLException {
      return new LoadBalancedMySQLConnection(this);
   }

   protected void propagateProxyDown(JdbcConnection proxyConn) {
      Iterator var2 = this.liveConnections.values().iterator();

      while(var2.hasNext()) {
         JdbcConnection c = (JdbcConnection)var2.next();
         c.setProxy(proxyConn);
      }

   }

   /** @deprecated */
   @Deprecated
   public boolean shouldExceptionTriggerFailover(Throwable t) {
      return this.shouldExceptionTriggerConnectionSwitch(t);
   }

   boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
      return t instanceof SQLException && this.exceptionChecker.shouldExceptionTriggerFailover(t);
   }

   boolean isSourceConnection() {
      return true;
   }

   synchronized void invalidateConnection(JdbcConnection conn) throws SQLException {
      super.invalidateConnection(conn);
      if (this.isGlobalBlocklistEnabled()) {
         String host = (String)this.connectionsToHostsMap.get(conn);
         if (host != null) {
            this.addToGlobalBlocklist(host);
         }
      }

      this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
      Object mappedHost = this.connectionsToHostsMap.remove(conn);
      if (mappedHost != null && this.hostsToListIndexMap.containsKey(mappedHost)) {
         int hostIndex = (Integer)this.hostsToListIndexMap.get(mappedHost);
         synchronized(this.responseTimes) {
            this.responseTimes[hostIndex] = 0L;
         }
      }

   }

   public synchronized void pickNewConnection() throws SQLException {
      if (!this.isClosed || !this.closedExplicitly) {
         List<String> hostPortList = Collections.unmodifiableList((List)this.hostsList.stream().map((hi) -> {
            return hi.getHostPortPair();
         }).collect(Collectors.toList()));
         if (this.currentConnection == null) {
            this.currentConnection = this.balancer.pickConnection(this, hostPortList, Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes.clone(), this.retriesAllDown);
         } else {
            if (this.currentConnection.isClosed()) {
               this.invalidateCurrentConnection();
            }

            int pingTimeout = (Integer)this.currentConnection.getPropertySet().getIntegerProperty(PropertyKey.loadBalancePingTimeout).getValue();
            boolean pingBeforeReturn = (Boolean)this.currentConnection.getPropertySet().getBooleanProperty(PropertyKey.loadBalanceValidateConnectionOnSwapServer).getValue();
            int hostsTried = 0;
            int hostsToTry = this.hostsList.size();

            while(hostsTried < hostsToTry) {
               ConnectionImpl newConn = null;

               try {
                  newConn = (ConnectionImpl)this.balancer.pickConnection(this, hostPortList, Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes.clone(), this.retriesAllDown);
                  if (this.currentConnection != null) {
                     if (pingBeforeReturn) {
                        newConn.pingInternal(true, pingTimeout);
                     }

                     this.syncSessionState(this.currentConnection, newConn);
                  }

                  this.currentConnection = newConn;
                  return;
               } catch (SQLException var8) {
                  if (this.shouldExceptionTriggerConnectionSwitch(var8) && newConn != null) {
                     this.invalidateConnection(newConn);
                  }

                  ++hostsTried;
               }
            }

            this.isClosed = true;
            this.closedReason = "Connection closed after inability to pick valid new connection during load-balance.";
         }
      }
   }

   public synchronized ConnectionImpl createConnectionForHost(HostInfo hostInfo) throws SQLException {
      ConnectionImpl conn = super.createConnectionForHost(hostInfo);
      this.liveConnections.put(hostInfo.getHostPortPair(), conn);
      this.connectionsToHostsMap.put(conn, hostInfo.getHostPortPair());
      this.removeFromGlobalBlocklist(hostInfo.getHostPortPair());
      ++this.totalPhysicalConnections;
      Iterator var3 = conn.getQueryInterceptorsInstances().iterator();

      while(var3.hasNext()) {
         QueryInterceptor stmtInterceptor = (QueryInterceptor)var3.next();
         if (stmtInterceptor instanceof LoadBalancedAutoCommitInterceptor) {
            ((LoadBalancedAutoCommitInterceptor)stmtInterceptor).resumeCounters();
            break;
         }
      }

      return conn;
   }

   void syncSessionState(JdbcConnection source, JdbcConnection target, boolean readOnly) throws SQLException {
      LoadBalancedAutoCommitInterceptor lbAutoCommitStmtInterceptor = null;
      Iterator var5 = target.getQueryInterceptorsInstances().iterator();

      while(var5.hasNext()) {
         QueryInterceptor stmtInterceptor = (QueryInterceptor)var5.next();
         if (stmtInterceptor instanceof LoadBalancedAutoCommitInterceptor) {
            lbAutoCommitStmtInterceptor = (LoadBalancedAutoCommitInterceptor)stmtInterceptor;
            lbAutoCommitStmtInterceptor.pauseCounters();
            break;
         }
      }

      super.syncSessionState(source, target, readOnly);
      if (lbAutoCommitStmtInterceptor != null) {
         lbAutoCommitStmtInterceptor.resumeCounters();
      }

   }

   public synchronized ConnectionImpl createConnectionForHost(String hostPortPair) throws SQLException {
      Iterator var2 = this.hostsList.iterator();

      HostInfo hi;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         hi = (HostInfo)var2.next();
      } while(!hi.getHostPortPair().equals(hostPortPair));

      return this.createConnectionForHost(hi);
   }

   private synchronized void closeAllConnections() {
      Iterator var1 = this.liveConnections.values().iterator();

      while(var1.hasNext()) {
         Connection c = (Connection)var1.next();

         try {
            c.close();
         } catch (SQLException var4) {
         }
      }

      if (!this.isClosed && this.connectionGroup != null) {
         this.connectionGroup.closeConnectionProxy(this);
      }

      this.liveConnections.clear();
      this.connectionsToHostsMap.clear();
   }

   synchronized void doClose() {
      this.closeAllConnections();
   }

   synchronized void doAbortInternal() {
      Iterator var1 = this.liveConnections.values().iterator();

      while(var1.hasNext()) {
         JdbcConnection c = (JdbcConnection)var1.next();

         try {
            c.abortInternal();
         } catch (SQLException var4) {
         }
      }

      if (!this.isClosed && this.connectionGroup != null) {
         this.connectionGroup.closeConnectionProxy(this);
      }

      this.liveConnections.clear();
      this.connectionsToHostsMap.clear();
   }

   synchronized void doAbort(Executor executor) {
      Iterator var2 = this.liveConnections.values().iterator();

      while(var2.hasNext()) {
         Connection c = (Connection)var2.next();

         try {
            c.abort(executor);
         } catch (SQLException var5) {
         }
      }

      if (!this.isClosed && this.connectionGroup != null) {
         this.connectionGroup.closeConnectionProxy(this);
      }

      this.liveConnections.clear();
      this.connectionsToHostsMap.clear();
   }

   Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable {
      String methodName = method.getName();
      if (this.isClosed && !this.allowedOnClosedConnection(method) && method.getExceptionTypes().length > 0) {
         if (!this.autoReconnect || this.closedExplicitly) {
            String reason = "No operations allowed after connection closed.";
            if (this.closedReason != null) {
               reason = reason + " " + this.closedReason;
            }

            Class[] var29 = method.getExceptionTypes();
            int var7 = var29.length;

            for(int var30 = 0; var30 < var7; ++var30) {
               Class<?> excls = var29[var30];
               if (SQLException.class.isAssignableFrom(excls)) {
                  throw SQLError.createSQLException(reason, "08003", (ExceptionInterceptor)null);
               }
            }

            throw (CJCommunicationsException)ExceptionFactory.createException(CJCommunicationsException.class, reason);
         }

         this.currentConnection = null;
         this.pickNewConnection();
         this.isClosed = false;
         this.closedReason = null;
      }

      if (!this.inTransaction) {
         this.inTransaction = true;
         this.transactionStartTime = System.nanoTime();
         ++this.transactionCount;
      }

      Object result = null;
      boolean var22 = false;

      String host;
      Integer hostIndex;
      label298: {
         try {
            var22 = true;
            result = method.invoke(this.thisAsConnection, args);
            if (result != null) {
               if (result instanceof JdbcStatement) {
                  ((JdbcStatement)result).setPingTarget(this);
               }

               result = this.proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), result);
               var22 = false;
            } else {
               var22 = false;
            }
            break label298;
         } catch (InvocationTargetException var26) {
            this.dealWithInvocationException(var26);
            var22 = false;
         } finally {
            if (var22) {
               if ("commit".equals(methodName) || "rollback".equals(methodName)) {
                  this.inTransaction = false;
                  String host = (String)this.connectionsToHostsMap.get(this.currentConnection);
                  if (host != null) {
                     synchronized(this.responseTimes) {
                        Integer hostIndex = (Integer)this.hostsToListIndexMap.get(host);
                        if (hostIndex != null && hostIndex < this.responseTimes.length) {
                           this.responseTimes[hostIndex] = System.nanoTime() - this.transactionStartTime;
                        }
                     }
                  }

                  this.pickNewConnection();
               }

            }
         }

         if (!"commit".equals(methodName) && !"rollback".equals(methodName)) {
            return result;
         }

         this.inTransaction = false;
         host = (String)this.connectionsToHostsMap.get(this.currentConnection);
         if (host != null) {
            synchronized(this.responseTimes) {
               hostIndex = (Integer)this.hostsToListIndexMap.get(host);
               if (hostIndex != null && hostIndex < this.responseTimes.length) {
                  this.responseTimes[hostIndex] = System.nanoTime() - this.transactionStartTime;
               }
            }
         }

         this.pickNewConnection();
         return result;
      }

      if ("commit".equals(methodName) || "rollback".equals(methodName)) {
         this.inTransaction = false;
         host = (String)this.connectionsToHostsMap.get(this.currentConnection);
         if (host != null) {
            synchronized(this.responseTimes) {
               hostIndex = (Integer)this.hostsToListIndexMap.get(host);
               if (hostIndex != null && hostIndex < this.responseTimes.length) {
                  this.responseTimes[hostIndex] = System.nanoTime() - this.transactionStartTime;
               }
            }
         }

         this.pickNewConnection();
      }

      return result;
   }

   public synchronized void doPing() throws SQLException {
      SQLException se = null;
      boolean foundHost = false;
      int pingTimeout = (Integer)this.currentConnection.getPropertySet().getIntegerProperty(PropertyKey.loadBalancePingTimeout).getValue();
      synchronized(this) {
         Iterator var5 = this.hostsList.iterator();

         label57:
         while(true) {
            String host;
            ConnectionImpl conn;
            do {
               if (!var5.hasNext()) {
                  break label57;
               }

               HostInfo hi = (HostInfo)var5.next();
               host = hi.getHostPortPair();
               conn = (ConnectionImpl)this.liveConnections.get(host);
            } while(conn == null);

            try {
               if (pingTimeout == 0) {
                  conn.ping();
               } else {
                  conn.pingInternal(true, pingTimeout);
               }

               foundHost = true;
            } catch (SQLException var11) {
               if (host.equals(this.connectionsToHostsMap.get(this.currentConnection))) {
                  this.closeAllConnections();
                  this.isClosed = true;
                  this.closedReason = "Connection closed because ping of current connection failed.";
                  throw var11;
               }

               if (var11.getMessage().equals(Messages.getString("Connection.exceededConnectionLifetime"))) {
                  if (se == null) {
                     se = var11;
                  }
               } else {
                  se = var11;
                  if (this.isGlobalBlocklistEnabled()) {
                     this.addToGlobalBlocklist(host);
                  }
               }

               this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
            }
         }
      }

      if (!foundHost) {
         this.closeAllConnections();
         this.isClosed = true;
         this.closedReason = "Connection closed due to inability to ping any active connections.";
         if (se != null) {
            throw se;
         }

         ((ConnectionImpl)this.currentConnection).throwConnectionClosedException();
      }

   }

   public void addToGlobalBlocklist(String host, long timeout) {
      if (this.isGlobalBlocklistEnabled()) {
         synchronized(globalBlocklist) {
            globalBlocklist.put(host, timeout);
         }
      }

   }

   public void removeFromGlobalBlocklist(String host) {
      if (this.isGlobalBlocklistEnabled() && globalBlocklist.containsKey(host)) {
         synchronized(globalBlocklist) {
            globalBlocklist.remove(host);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public void removeFromGlobalBlacklist(String host) {
      this.removeFromGlobalBlocklist(host);
   }

   /** @deprecated */
   @Deprecated
   public void addToGlobalBlacklist(String host, long timeout) {
      this.addToGlobalBlocklist(host, timeout);
   }

   public void addToGlobalBlocklist(String host) {
      this.addToGlobalBlocklist(host, System.currentTimeMillis() + (long)this.globalBlocklistTimeout);
   }

   /** @deprecated */
   @Deprecated
   public void addToGlobalBlacklist(String host) {
      this.addToGlobalBlocklist(host);
   }

   public boolean isGlobalBlocklistEnabled() {
      return this.globalBlocklistTimeout > 0;
   }

   /** @deprecated */
   @Deprecated
   public boolean isGlobalBlacklistEnabled() {
      return this.isGlobalBlocklistEnabled();
   }

   public synchronized Map<String, Long> getGlobalBlocklist() {
      HashMap blocklistClone;
      if (this.isGlobalBlocklistEnabled()) {
         blocklistClone = new HashMap(globalBlocklist.size());
         synchronized(globalBlocklist) {
            blocklistClone.putAll(globalBlocklist);
         }

         Set<String> keys = blocklistClone.keySet();
         keys.retainAll((Collection)this.hostsList.stream().map((hi) -> {
            return hi.getHostPortPair();
         }).collect(Collectors.toList()));
         Iterator<String> i = keys.iterator();

         while(i.hasNext()) {
            String host = (String)i.next();
            Long timeout = (Long)globalBlocklist.get(host);
            if (timeout != null && timeout < System.currentTimeMillis()) {
               synchronized(globalBlocklist) {
                  globalBlocklist.remove(host);
               }

               i.remove();
            }
         }

         if (keys.size() == this.hostsList.size()) {
            return new HashMap(1);
         } else {
            return blocklistClone;
         }
      } else if (this.hostsToRemove.isEmpty()) {
         return new HashMap(1);
      } else {
         blocklistClone = new HashMap();
         Iterator var2 = this.hostsToRemove.iterator();

         while(var2.hasNext()) {
            String h = (String)var2.next();
            blocklistClone.put(h, System.currentTimeMillis() + 5000L);
         }

         return blocklistClone;
      }
   }

   /** @deprecated */
   @Deprecated
   public synchronized Map<String, Long> getGlobalBlacklist() {
      return this.getGlobalBlocklist();
   }

   public void removeHostWhenNotInUse(String hostPortPair) throws SQLException {
      if (this.hostRemovalGracePeriod <= 0) {
         this.removeHost(hostPortPair);
      } else {
         int timeBetweenChecks = this.hostRemovalGracePeriod > 1000 ? 1000 : this.hostRemovalGracePeriod;
         synchronized(this) {
            this.addToGlobalBlocklist(hostPortPair, System.currentTimeMillis() + (long)this.hostRemovalGracePeriod + (long)timeBetweenChecks);
            long cur = System.currentTimeMillis();

            while(true) {
               if (System.currentTimeMillis() >= cur + (long)this.hostRemovalGracePeriod) {
                  break;
               }

               this.hostsToRemove.add(hostPortPair);
               if (!hostPortPair.equals(this.currentConnection.getHostPortPair())) {
                  this.removeHost(hostPortPair);
                  return;
               }

               try {
                  Thread.sleep((long)timeBetweenChecks);
               } catch (InterruptedException var8) {
               }
            }
         }

         this.removeHost(hostPortPair);
      }
   }

   public synchronized void removeHost(String hostPortPair) throws SQLException {
      if (this.connectionGroup != null && this.connectionGroup.getInitialHosts().size() == 1 && this.connectionGroup.getInitialHosts().contains(hostPortPair)) {
         throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.0"), (ExceptionInterceptor)null);
      } else {
         this.hostsToRemove.add(hostPortPair);
         this.connectionsToHostsMap.remove(this.liveConnections.remove(hostPortPair));
         if (this.hostsToListIndexMap.remove(hostPortPair) != null) {
            long[] newResponseTimes = new long[this.responseTimes.length - 1];
            int newIdx = 0;
            Iterator var4 = this.hostsList.iterator();

            while(var4.hasNext()) {
               HostInfo hostInfo = (HostInfo)var4.next();
               String host = hostInfo.getHostPortPair();
               if (!this.hostsToRemove.contains(host)) {
                  Integer idx = (Integer)this.hostsToListIndexMap.get(host);
                  if (idx != null && idx < this.responseTimes.length) {
                     newResponseTimes[newIdx] = this.responseTimes[idx];
                  }

                  this.hostsToListIndexMap.put(host, newIdx++);
               }
            }

            this.responseTimes = newResponseTimes;
         }

         if (hostPortPair.equals(this.currentConnection.getHostPortPair())) {
            this.invalidateConnection(this.currentConnection);
            this.pickNewConnection();
         }

      }
   }

   public synchronized boolean addHost(String hostPortPair) {
      if (this.hostsToListIndexMap.containsKey(hostPortPair)) {
         return false;
      } else {
         long[] newResponseTimes = new long[this.responseTimes.length + 1];
         System.arraycopy(this.responseTimes, 0, newResponseTimes, 0, this.responseTimes.length);
         this.responseTimes = newResponseTimes;
         if (this.hostsList.stream().noneMatch((hi) -> {
            return hostPortPair.equals(hi.getHostPortPair());
         })) {
            this.hostsList.add(this.connectionUrl.getHostOrSpawnIsolated(hostPortPair));
         }

         this.hostsToListIndexMap.put(hostPortPair, this.responseTimes.length - 1);
         this.hostsToRemove.remove(hostPortPair);
         return true;
      }
   }

   public synchronized boolean inTransaction() {
      return this.inTransaction;
   }

   public synchronized long getTransactionCount() {
      return this.transactionCount;
   }

   public synchronized long getActivePhysicalConnectionCount() {
      return (long)this.liveConnections.size();
   }

   public synchronized long getTotalPhysicalConnectionCount() {
      return this.totalPhysicalConnections;
   }

   public synchronized long getConnectionGroupProxyID() {
      return this.connectionGroupProxyID;
   }

   public synchronized String getCurrentActiveHost() {
      JdbcConnection c = this.currentConnection;
      if (c != null) {
         Object o = this.connectionsToHostsMap.get(c);
         if (o != null) {
            return o.toString();
         }
      }

      return null;
   }

   public synchronized long getCurrentTransactionDuration() {
      return this.inTransaction && this.transactionStartTime > 0L ? System.nanoTime() - this.transactionStartTime : 0L;
   }

   static synchronized LoadBalancedConnection getNullLoadBalancedConnectionInstance() {
      if (nullLBConnectionInstance == null) {
         nullLBConnectionInstance = (LoadBalancedConnection)Proxy.newProxyInstance(LoadBalancedConnection.class.getClassLoader(), INTERFACES_TO_PROXY, new NullLoadBalancedConnectionProxy());
      }

      return nullLBConnectionInstance;
   }

   private static class NullLoadBalancedConnectionProxy implements InvocationHandler {
      public NullLoadBalancedConnectionProxy() {
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         SQLException exceptionToThrow = SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.unusableConnection"), "25000", 1000001, true, (ExceptionInterceptor)null);
         Class<?>[] declaredException = method.getExceptionTypes();
         Class[] var6 = declaredException;
         int var7 = declaredException.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Class<?> declEx = var6[var8];
            if (declEx.isAssignableFrom(exceptionToThrow.getClass())) {
               throw exceptionToThrow;
            }
         }

         throw new IllegalStateException(exceptionToThrow.getMessage(), exceptionToThrow);
      }
   }
}
