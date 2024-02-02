/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.ha.LoadBalancedConnectionProxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class ConnectionGroup
/*     */ {
/*     */   private String groupName;
/*  46 */   private long connections = 0L;
/*  47 */   private long activeConnections = 0L;
/*  48 */   private HashMap<Long, LoadBalancedConnectionProxy> connectionProxies = new HashMap<>();
/*  49 */   private Set<String> hostList = new HashSet<>();
/*     */   private boolean isInitialized = false;
/*  51 */   private long closedProxyTotalPhysicalConnections = 0L;
/*  52 */   private long closedProxyTotalTransactions = 0L;
/*  53 */   private int activeHosts = 0;
/*  54 */   private Set<String> closedHosts = new HashSet<>();
/*     */   
/*     */   ConnectionGroup(String groupName) {
/*  57 */     this.groupName = groupName;
/*     */   }
/*     */ 
/*     */   
/*     */   public long registerConnectionProxy(LoadBalancedConnectionProxy proxy, List<String> localHostList) {
/*     */     long currentConnectionId;
/*  63 */     synchronized (this) {
/*  64 */       if (!this.isInitialized) {
/*  65 */         this.hostList.addAll(localHostList);
/*  66 */         this.isInitialized = true;
/*  67 */         this.activeHosts = localHostList.size();
/*     */       } 
/*  69 */       currentConnectionId = ++this.connections;
/*  70 */       this.connectionProxies.put(Long.valueOf(currentConnectionId), proxy);
/*     */     } 
/*  72 */     this.activeConnections++;
/*     */     
/*  74 */     return currentConnectionId;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroupName() {
/*  79 */     return this.groupName;
/*     */   }
/*     */   
/*     */   public Collection<String> getInitialHosts() {
/*  83 */     return this.hostList;
/*     */   }
/*     */   
/*     */   public int getActiveHostCount() {
/*  87 */     return this.activeHosts;
/*     */   }
/*     */   
/*     */   public Collection<String> getClosedHosts() {
/*  91 */     return this.closedHosts;
/*     */   }
/*     */   
/*     */   public long getTotalLogicalConnectionCount() {
/*  95 */     return this.connections;
/*     */   }
/*     */   
/*     */   public long getActiveLogicalConnectionCount() {
/*  99 */     return this.activeConnections;
/*     */   }
/*     */   
/*     */   public long getActivePhysicalConnectionCount() {
/* 103 */     long result = 0L;
/* 104 */     Map<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap<>();
/* 105 */     synchronized (this.connectionProxies) {
/* 106 */       proxyMap.putAll(this.connectionProxies);
/*     */     } 
/* 108 */     for (LoadBalancedConnectionProxy proxy : proxyMap.values()) {
/* 109 */       result += proxy.getActivePhysicalConnectionCount();
/*     */     }
/* 111 */     return result;
/*     */   }
/*     */   
/*     */   public long getTotalPhysicalConnectionCount() {
/* 115 */     long allConnections = this.closedProxyTotalPhysicalConnections;
/* 116 */     Map<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap<>();
/* 117 */     synchronized (this.connectionProxies) {
/* 118 */       proxyMap.putAll(this.connectionProxies);
/*     */     } 
/* 120 */     for (LoadBalancedConnectionProxy proxy : proxyMap.values()) {
/* 121 */       allConnections += proxy.getTotalPhysicalConnectionCount();
/*     */     }
/* 123 */     return allConnections;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTotalTransactionCount() {
/* 128 */     long transactions = this.closedProxyTotalTransactions;
/* 129 */     Map<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap<>();
/* 130 */     synchronized (this.connectionProxies) {
/* 131 */       proxyMap.putAll(this.connectionProxies);
/*     */     } 
/* 133 */     for (LoadBalancedConnectionProxy proxy : proxyMap.values()) {
/* 134 */       transactions += proxy.getTransactionCount();
/*     */     }
/* 136 */     return transactions;
/*     */   }
/*     */   
/*     */   public void closeConnectionProxy(LoadBalancedConnectionProxy proxy) {
/* 140 */     this.activeConnections--;
/* 141 */     this.connectionProxies.remove(Long.valueOf(proxy.getConnectionGroupProxyID()));
/* 142 */     this.closedProxyTotalPhysicalConnections += proxy.getTotalPhysicalConnectionCount();
/* 143 */     this.closedProxyTotalTransactions += proxy.getTransactionCount();
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
/*     */   public void removeHost(String hostPortPair) throws SQLException {
/* 156 */     removeHost(hostPortPair, false);
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
/*     */   public void removeHost(String hostPortPair, boolean removeExisting) throws SQLException {
/* 170 */     removeHost(hostPortPair, removeExisting, true);
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
/*     */   public synchronized void removeHost(String hostPortPair, boolean removeExisting, boolean waitForGracefulFailover) throws SQLException {
/* 187 */     if (this.activeHosts == 1) {
/* 188 */       throw SQLError.createSQLException(Messages.getString("ConnectionGroup.0"), null);
/*     */     }
/*     */     
/* 191 */     if (this.hostList.remove(hostPortPair)) {
/* 192 */       this.activeHosts--;
/*     */     } else {
/* 194 */       throw SQLError.createSQLException(Messages.getString("ConnectionGroup.1", new Object[] { hostPortPair }), null);
/*     */     } 
/*     */     
/* 197 */     if (removeExisting) {
/*     */       
/* 199 */       Map<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap<>();
/* 200 */       synchronized (this.connectionProxies) {
/* 201 */         proxyMap.putAll(this.connectionProxies);
/*     */       } 
/*     */       
/* 204 */       for (LoadBalancedConnectionProxy proxy : proxyMap.values()) {
/* 205 */         if (waitForGracefulFailover) {
/* 206 */           proxy.removeHostWhenNotInUse(hostPortPair); continue;
/*     */         } 
/* 208 */         proxy.removeHost(hostPortPair);
/*     */       } 
/*     */     } 
/*     */     
/* 212 */     this.closedHosts.add(hostPortPair);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addHost(String hostPortPair) {
/* 222 */     addHost(hostPortPair, false);
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
/*     */   public void addHost(String hostPortPair, boolean forExisting) {
/* 234 */     synchronized (this) {
/* 235 */       if (this.hostList.add(hostPortPair)) {
/* 236 */         this.activeHosts++;
/*     */       }
/*     */     } 
/*     */     
/* 240 */     if (!forExisting) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 245 */     Map<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap<>();
/* 246 */     synchronized (this.connectionProxies) {
/* 247 */       proxyMap.putAll(this.connectionProxies);
/*     */     } 
/*     */     
/* 250 */     for (LoadBalancedConnectionProxy proxy : proxyMap.values())
/* 251 */       proxy.addHost(hostPortPair); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ConnectionGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */