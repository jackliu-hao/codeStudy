package com.mysql.cj.jdbc.jmx;

import java.sql.SQLException;

public interface ReplicationGroupManagerMBean {
   void addReplicaHost(String var1, String var2) throws SQLException;

   /** @deprecated */
   @Deprecated
   default void addSlaveHost(String groupFilter, String host) throws SQLException {
      this.addReplicaHost(groupFilter, host);
   }

   void removeReplicaHost(String var1, String var2) throws SQLException;

   /** @deprecated */
   @Deprecated
   default void removeSlaveHost(String groupFilter, String host) throws SQLException {
      this.removeReplicaHost(groupFilter, host);
   }

   void promoteReplicaToSource(String var1, String var2) throws SQLException;

   /** @deprecated */
   @Deprecated
   default void promoteSlaveToMaster(String groupFilter, String host) throws SQLException {
      this.promoteReplicaToSource(groupFilter, host);
   }

   void removeSourceHost(String var1, String var2) throws SQLException;

   /** @deprecated */
   @Deprecated
   default void removeMasterHost(String groupFilter, String host) throws SQLException {
      this.removeSourceHost(groupFilter, host);
   }

   String getSourceHostsList(String var1);

   /** @deprecated */
   @Deprecated
   default String getMasterHostsList(String group) {
      return this.getSourceHostsList(group);
   }

   String getReplicaHostsList(String var1);

   /** @deprecated */
   @Deprecated
   default String getSlaveHostsList(String group) {
      return this.getReplicaHostsList(group);
   }

   String getRegisteredConnectionGroups();

   int getActiveSourceHostCount(String var1);

   /** @deprecated */
   @Deprecated
   default int getActiveMasterHostCount(String group) {
      return this.getActiveSourceHostCount(group);
   }

   int getActiveReplicaHostCount(String var1);

   /** @deprecated */
   @Deprecated
   default int getActiveSlaveHostCount(String group) {
      return this.getActiveReplicaHostCount(group);
   }

   int getReplicaPromotionCount(String var1);

   /** @deprecated */
   @Deprecated
   default int getSlavePromotionCount(String group) {
      return this.getReplicaPromotionCount(group);
   }

   long getTotalLogicalConnectionCount(String var1);

   long getActiveLogicalConnectionCount(String var1);
}
