package com.mysql.cj.jdbc.ha;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ReplicationConnectionGroup {
   private String groupName;
   private long connections = 0L;
   private long replicasAdded = 0L;
   private long replicasRemoved = 0L;
   private long replicasPromoted = 0L;
   private long activeConnections = 0L;
   private HashMap<Long, ReplicationConnection> replicationConnections = new HashMap();
   private Set<String> replicaHostList = new CopyOnWriteArraySet();
   private boolean isInitialized = false;
   private Set<String> sourceHostList = new CopyOnWriteArraySet();

   ReplicationConnectionGroup(String groupName) {
      this.groupName = groupName;
   }

   public long getConnectionCount() {
      return this.connections;
   }

   public long registerReplicationConnection(ReplicationConnection conn, List<String> localSourceList, List<String> localReplicaList) {
      long currentConnectionId;
      synchronized(this) {
         if (!this.isInitialized) {
            if (localSourceList != null) {
               this.sourceHostList.addAll(localSourceList);
            }

            if (localReplicaList != null) {
               this.replicaHostList.addAll(localReplicaList);
            }

            this.isInitialized = true;
         }

         currentConnectionId = ++this.connections;
         this.replicationConnections.put(currentConnectionId, conn);
      }

      ++this.activeConnections;
      return currentConnectionId;
   }

   public String getGroupName() {
      return this.groupName;
   }

   public Collection<String> getSourceHosts() {
      return this.sourceHostList;
   }

   /** @deprecated */
   @Deprecated
   public Collection<String> getMasterHosts() {
      return this.getSourceHosts();
   }

   public Collection<String> getReplicaHosts() {
      return this.replicaHostList;
   }

   /** @deprecated */
   @Deprecated
   public Collection<String> getSlaveHosts() {
      return this.getReplicaHosts();
   }

   public void addReplicaHost(String hostPortPair) throws SQLException {
      if (this.replicaHostList.add(hostPortPair)) {
         ++this.replicasAdded;
         Iterator var2 = this.replicationConnections.values().iterator();

         while(var2.hasNext()) {
            ReplicationConnection c = (ReplicationConnection)var2.next();
            c.addReplicaHost(hostPortPair);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public void addSlaveHost(String hostPortPair) throws SQLException {
      this.addReplicaHost(hostPortPair);
   }

   public void handleCloseConnection(ReplicationConnection conn) {
      this.replicationConnections.remove(conn.getConnectionGroupId());
      --this.activeConnections;
   }

   public void removeReplicaHost(String hostPortPair, boolean closeGently) throws SQLException {
      if (this.replicaHostList.remove(hostPortPair)) {
         ++this.replicasRemoved;
         Iterator var3 = this.replicationConnections.values().iterator();

         while(var3.hasNext()) {
            ReplicationConnection c = (ReplicationConnection)var3.next();
            c.removeReplica(hostPortPair, closeGently);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public void removeSlaveHost(String hostPortPair, boolean closeGently) throws SQLException {
      this.removeReplicaHost(hostPortPair, closeGently);
   }

   public void promoteReplicaToSource(String hostPortPair) throws SQLException {
      if (this.replicaHostList.remove(hostPortPair) | this.sourceHostList.add(hostPortPair)) {
         ++this.replicasPromoted;
         Iterator var2 = this.replicationConnections.values().iterator();

         while(var2.hasNext()) {
            ReplicationConnection c = (ReplicationConnection)var2.next();
            c.promoteReplicaToSource(hostPortPair);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public void promoteSlaveToMaster(String hostPortPair) throws SQLException {
      this.promoteReplicaToSource(hostPortPair);
   }

   public void removeSourceHost(String hostPortPair) throws SQLException {
      this.removeSourceHost(hostPortPair, true);
   }

   /** @deprecated */
   @Deprecated
   public void removeMasterHost(String hostPortPair) throws SQLException {
      this.removeSourceHost(hostPortPair);
   }

   public void removeSourceHost(String hostPortPair, boolean closeGently) throws SQLException {
      if (this.sourceHostList.remove(hostPortPair)) {
         Iterator var3 = this.replicationConnections.values().iterator();

         while(var3.hasNext()) {
            ReplicationConnection c = (ReplicationConnection)var3.next();
            c.removeSourceHost(hostPortPair, closeGently);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public void removeMasterHost(String hostPortPair, boolean closeGently) throws SQLException {
      this.removeSourceHost(hostPortPair, closeGently);
   }

   public int getConnectionCountWithHostAsReplica(String hostPortPair) {
      int matched = 0;
      Iterator var3 = this.replicationConnections.values().iterator();

      while(var3.hasNext()) {
         ReplicationConnection c = (ReplicationConnection)var3.next();
         if (c.isHostReplica(hostPortPair)) {
            ++matched;
         }
      }

      return matched;
   }

   /** @deprecated */
   @Deprecated
   public int getConnectionCountWithHostAsSlave(String hostPortPair) {
      return this.getConnectionCountWithHostAsReplica(hostPortPair);
   }

   public int getConnectionCountWithHostAsSource(String hostPortPair) {
      int matched = 0;
      Iterator var3 = this.replicationConnections.values().iterator();

      while(var3.hasNext()) {
         ReplicationConnection c = (ReplicationConnection)var3.next();
         if (c.isHostSource(hostPortPair)) {
            ++matched;
         }
      }

      return matched;
   }

   /** @deprecated */
   @Deprecated
   public int getConnectionCountWithHostAsMaster(String hostPortPair) {
      return this.getConnectionCountWithHostAsSource(hostPortPair);
   }

   public long getNumberOfReplicasAdded() {
      return this.replicasAdded;
   }

   /** @deprecated */
   @Deprecated
   public long getNumberOfSlavesAdded() {
      return this.getNumberOfReplicasAdded();
   }

   public long getNumberOfReplicasRemoved() {
      return this.replicasRemoved;
   }

   /** @deprecated */
   @Deprecated
   public long getNumberOfSlavesRemoved() {
      return this.getNumberOfReplicasRemoved();
   }

   public long getNumberOfReplicaPromotions() {
      return this.replicasPromoted;
   }

   /** @deprecated */
   @Deprecated
   public long getNumberOfSlavePromotions() {
      return this.getNumberOfReplicaPromotions();
   }

   public long getTotalConnectionCount() {
      return this.connections;
   }

   public long getActiveConnectionCount() {
      return this.activeConnections;
   }

   public String toString() {
      return "ReplicationConnectionGroup[groupName=" + this.groupName + ",sourceHostList=" + this.sourceHostList + ",replicaHostList=" + this.replicaHostList + "]";
   }
}
