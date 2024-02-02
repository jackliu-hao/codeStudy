/*     */ package com.mysql.cj.jdbc.ha;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
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
/*     */ public class ReplicationConnectionGroup
/*     */ {
/*     */   private String groupName;
/*  45 */   private long connections = 0L;
/*  46 */   private long replicasAdded = 0L;
/*  47 */   private long replicasRemoved = 0L;
/*  48 */   private long replicasPromoted = 0L;
/*  49 */   private long activeConnections = 0L;
/*  50 */   private HashMap<Long, ReplicationConnection> replicationConnections = new HashMap<>();
/*  51 */   private Set<String> replicaHostList = new CopyOnWriteArraySet<>();
/*     */   private boolean isInitialized = false;
/*  53 */   private Set<String> sourceHostList = new CopyOnWriteArraySet<>();
/*     */   
/*     */   ReplicationConnectionGroup(String groupName) {
/*  56 */     this.groupName = groupName;
/*     */   }
/*     */   
/*     */   public long getConnectionCount() {
/*  60 */     return this.connections;
/*     */   }
/*     */ 
/*     */   
/*     */   public long registerReplicationConnection(ReplicationConnection conn, List<String> localSourceList, List<String> localReplicaList) {
/*     */     long currentConnectionId;
/*  66 */     synchronized (this) {
/*  67 */       if (!this.isInitialized) {
/*  68 */         if (localSourceList != null) {
/*  69 */           this.sourceHostList.addAll(localSourceList);
/*     */         }
/*  71 */         if (localReplicaList != null) {
/*  72 */           this.replicaHostList.addAll(localReplicaList);
/*     */         }
/*  74 */         this.isInitialized = true;
/*     */       } 
/*  76 */       currentConnectionId = ++this.connections;
/*  77 */       this.replicationConnections.put(Long.valueOf(currentConnectionId), conn);
/*     */     } 
/*  79 */     this.activeConnections++;
/*     */     
/*  81 */     return currentConnectionId;
/*     */   }
/*     */   
/*     */   public String getGroupName() {
/*  85 */     return this.groupName;
/*     */   }
/*     */   
/*     */   public Collection<String> getSourceHosts() {
/*  89 */     return this.sourceHostList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Collection<String> getMasterHosts() {
/* 100 */     return getSourceHosts();
/*     */   }
/*     */   
/*     */   public Collection<String> getReplicaHosts() {
/* 104 */     return this.replicaHostList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Collection<String> getSlaveHosts() {
/* 115 */     return getReplicaHosts();
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
/*     */   public void addReplicaHost(String hostPortPair) throws SQLException {
/* 135 */     if (this.replicaHostList.add(hostPortPair)) {
/* 136 */       this.replicasAdded++;
/*     */ 
/*     */       
/* 139 */       for (ReplicationConnection c : this.replicationConnections.values()) {
/* 140 */         c.addReplicaHost(hostPortPair);
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
/*     */   @Deprecated
/*     */   public void addSlaveHost(String hostPortPair) throws SQLException {
/* 155 */     addReplicaHost(hostPortPair);
/*     */   }
/*     */   
/*     */   public void handleCloseConnection(ReplicationConnection conn) {
/* 159 */     this.replicationConnections.remove(Long.valueOf(conn.getConnectionGroupId()));
/* 160 */     this.activeConnections--;
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
/*     */   public void removeReplicaHost(String hostPortPair, boolean closeGently) throws SQLException {
/* 182 */     if (this.replicaHostList.remove(hostPortPair)) {
/* 183 */       this.replicasRemoved++;
/*     */ 
/*     */       
/* 186 */       for (ReplicationConnection c : this.replicationConnections.values()) {
/* 187 */         c.removeReplica(hostPortPair, closeGently);
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
/*     */   public void removeSlaveHost(String hostPortPair, boolean closeGently) throws SQLException {
/* 204 */     removeReplicaHost(hostPortPair, closeGently);
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
/*     */   public void promoteReplicaToSource(String hostPortPair) throws SQLException {
/* 225 */     if ((this.replicaHostList.remove(hostPortPair) | this.sourceHostList.add(hostPortPair)) != 0) {
/* 226 */       this.replicasPromoted++;
/*     */       
/* 228 */       for (ReplicationConnection c : this.replicationConnections.values()) {
/* 229 */         c.promoteReplicaToSource(hostPortPair);
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
/*     */   @Deprecated
/*     */   public void promoteSlaveToMaster(String hostPortPair) throws SQLException {
/* 244 */     promoteReplicaToSource(hostPortPair);
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
/*     */   public void removeSourceHost(String hostPortPair) throws SQLException {
/* 256 */     removeSourceHost(hostPortPair, true);
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
/*     */   public void removeMasterHost(String hostPortPair) throws SQLException {
/* 269 */     removeSourceHost(hostPortPair);
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
/*     */   public void removeSourceHost(String hostPortPair, boolean closeGently) throws SQLException {
/* 290 */     if (this.sourceHostList.remove(hostPortPair))
/*     */     {
/* 292 */       for (ReplicationConnection c : this.replicationConnections.values()) {
/* 293 */         c.removeSourceHost(hostPortPair, closeGently);
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
/*     */   public void removeMasterHost(String hostPortPair, boolean closeGently) throws SQLException {
/* 310 */     removeSourceHost(hostPortPair, closeGently);
/*     */   }
/*     */   
/*     */   public int getConnectionCountWithHostAsReplica(String hostPortPair) {
/* 314 */     int matched = 0;
/*     */     
/* 316 */     for (ReplicationConnection c : this.replicationConnections.values()) {
/* 317 */       if (c.isHostReplica(hostPortPair)) {
/* 318 */         matched++;
/*     */       }
/*     */     } 
/* 321 */     return matched;
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
/*     */   public int getConnectionCountWithHostAsSlave(String hostPortPair) {
/* 334 */     return getConnectionCountWithHostAsReplica(hostPortPair);
/*     */   }
/*     */   
/*     */   public int getConnectionCountWithHostAsSource(String hostPortPair) {
/* 338 */     int matched = 0;
/*     */     
/* 340 */     for (ReplicationConnection c : this.replicationConnections.values()) {
/* 341 */       if (c.isHostSource(hostPortPair)) {
/* 342 */         matched++;
/*     */       }
/*     */     } 
/* 345 */     return matched;
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
/*     */   public int getConnectionCountWithHostAsMaster(String hostPortPair) {
/* 358 */     return getConnectionCountWithHostAsSource(hostPortPair);
/*     */   }
/*     */   
/*     */   public long getNumberOfReplicasAdded() {
/* 362 */     return this.replicasAdded;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public long getNumberOfSlavesAdded() {
/* 373 */     return getNumberOfReplicasAdded();
/*     */   }
/*     */   
/*     */   public long getNumberOfReplicasRemoved() {
/* 377 */     return this.replicasRemoved;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public long getNumberOfSlavesRemoved() {
/* 388 */     return getNumberOfReplicasRemoved();
/*     */   }
/*     */   
/*     */   public long getNumberOfReplicaPromotions() {
/* 392 */     return this.replicasPromoted;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public long getNumberOfSlavePromotions() {
/* 403 */     return getNumberOfReplicaPromotions();
/*     */   }
/*     */   
/*     */   public long getTotalConnectionCount() {
/* 407 */     return this.connections;
/*     */   }
/*     */   
/*     */   public long getActiveConnectionCount() {
/* 411 */     return this.activeConnections;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 416 */     return "ReplicationConnectionGroup[groupName=" + this.groupName + ",sourceHostList=" + this.sourceHostList + ",replicaHostList=" + this.replicaHostList + "]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\ReplicationConnectionGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */