package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.jmx.ReplicationGroupManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ReplicationConnectionGroupManager {
   private static HashMap<String, ReplicationConnectionGroup> GROUP_MAP = new HashMap();
   private static ReplicationGroupManager mbean = new ReplicationGroupManager();
   private static boolean hasRegisteredJmx = false;

   public static synchronized ReplicationConnectionGroup getConnectionGroupInstance(String groupName) {
      if (GROUP_MAP.containsKey(groupName)) {
         return (ReplicationConnectionGroup)GROUP_MAP.get(groupName);
      } else {
         ReplicationConnectionGroup group = new ReplicationConnectionGroup(groupName);
         GROUP_MAP.put(groupName, group);
         return group;
      }
   }

   public static void registerJmx() throws SQLException {
      if (!hasRegisteredJmx) {
         mbean.registerJmx();
         hasRegisteredJmx = true;
      }
   }

   public static ReplicationConnectionGroup getConnectionGroup(String groupName) {
      return (ReplicationConnectionGroup)GROUP_MAP.get(groupName);
   }

   public static Collection<ReplicationConnectionGroup> getGroupsMatching(String group) {
      HashSet s;
      if (group != null && !group.equals("")) {
         s = new HashSet();
         ReplicationConnectionGroup o = (ReplicationConnectionGroup)GROUP_MAP.get(group);
         if (o != null) {
            s.add(o);
         }

         return s;
      } else {
         s = new HashSet();
         s.addAll(GROUP_MAP.values());
         return s;
      }
   }

   public static void addReplicaHost(String group, String hostPortPair) throws SQLException {
      Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
      Iterator var3 = s.iterator();

      while(var3.hasNext()) {
         ReplicationConnectionGroup cg = (ReplicationConnectionGroup)var3.next();
         cg.addReplicaHost(hostPortPair);
      }

   }

   /** @deprecated */
   @Deprecated
   public static void addSlaveHost(String group, String hostPortPair) throws SQLException {
      addReplicaHost(group, hostPortPair);
   }

   public static void removeReplicaHost(String group, String hostPortPair) throws SQLException {
      removeReplicaHost(group, hostPortPair, true);
   }

   /** @deprecated */
   @Deprecated
   public static void removeSlaveHost(String group, String hostPortPair) throws SQLException {
      removeReplicaHost(group, hostPortPair);
   }

   public static void removeReplicaHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
      Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
      Iterator var4 = s.iterator();

      while(var4.hasNext()) {
         ReplicationConnectionGroup cg = (ReplicationConnectionGroup)var4.next();
         cg.removeReplicaHost(hostPortPair, closeGently);
      }

   }

   /** @deprecated */
   @Deprecated
   public static void removeSlaveHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
      removeReplicaHost(group, hostPortPair, closeGently);
   }

   public static void promoteReplicaToSource(String group, String hostPortPair) throws SQLException {
      Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
      Iterator var3 = s.iterator();

      while(var3.hasNext()) {
         ReplicationConnectionGroup cg = (ReplicationConnectionGroup)var3.next();
         cg.promoteReplicaToSource(hostPortPair);
      }

   }

   /** @deprecated */
   @Deprecated
   public static void promoteSlaveToMaster(String group, String hostPortPair) throws SQLException {
      promoteReplicaToSource(group, hostPortPair);
   }

   public static long getReplicaPromotionCount(String group) throws SQLException {
      Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
      long promoted = 0L;
      Iterator var4 = s.iterator();

      while(var4.hasNext()) {
         ReplicationConnectionGroup cg = (ReplicationConnectionGroup)var4.next();
         long tmp = cg.getNumberOfReplicaPromotions();
         if (tmp > promoted) {
            promoted = tmp;
         }
      }

      return promoted;
   }

   /** @deprecated */
   @Deprecated
   public static long getSlavePromotionCount(String group) throws SQLException {
      return getReplicaPromotionCount(group);
   }

   public static void removeSourceHost(String group, String hostPortPair) throws SQLException {
      removeSourceHost(group, hostPortPair, true);
   }

   /** @deprecated */
   @Deprecated
   public static void removeMasterHost(String group, String hostPortPair) throws SQLException {
      removeSourceHost(group, hostPortPair);
   }

   public static void removeSourceHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
      Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
      Iterator var4 = s.iterator();

      while(var4.hasNext()) {
         ReplicationConnectionGroup cg = (ReplicationConnectionGroup)var4.next();
         cg.removeSourceHost(hostPortPair, closeGently);
      }

   }

   /** @deprecated */
   @Deprecated
   public static void removeMasterHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
      removeSourceHost(group, hostPortPair, closeGently);
   }

   public static String getRegisteredReplicationConnectionGroups() {
      Collection<ReplicationConnectionGroup> s = getGroupsMatching((String)null);
      StringBuilder sb = new StringBuilder();
      String sep = "";

      for(Iterator var3 = s.iterator(); var3.hasNext(); sep = ",") {
         ReplicationConnectionGroup cg = (ReplicationConnectionGroup)var3.next();
         String group = cg.getGroupName();
         sb.append(sep);
         sb.append(group);
      }

      return sb.toString();
   }

   public static int getNumberOfSourcePromotion(String groupFilter) {
      int total = 0;
      Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);

      ReplicationConnectionGroup cg;
      for(Iterator var3 = s.iterator(); var3.hasNext(); total = (int)((long)total + cg.getNumberOfReplicaPromotions())) {
         cg = (ReplicationConnectionGroup)var3.next();
      }

      return total;
   }

   /** @deprecated */
   @Deprecated
   public static int getNumberOfMasterPromotion(String groupFilter) {
      return getNumberOfSourcePromotion(groupFilter);
   }

   public static int getConnectionCountWithHostAsReplica(String groupFilter, String hostPortPair) {
      int total = 0;
      Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);

      ReplicationConnectionGroup cg;
      for(Iterator var4 = s.iterator(); var4.hasNext(); total += cg.getConnectionCountWithHostAsReplica(hostPortPair)) {
         cg = (ReplicationConnectionGroup)var4.next();
      }

      return total;
   }

   /** @deprecated */
   @Deprecated
   public static int getConnectionCountWithHostAsSlave(String groupFilter, String hostPortPair) {
      return getConnectionCountWithHostAsReplica(groupFilter, hostPortPair);
   }

   public static int getConnectionCountWithHostAsSource(String groupFilter, String hostPortPair) {
      int total = 0;
      Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);

      ReplicationConnectionGroup cg;
      for(Iterator var4 = s.iterator(); var4.hasNext(); total += cg.getConnectionCountWithHostAsSource(hostPortPair)) {
         cg = (ReplicationConnectionGroup)var4.next();
      }

      return total;
   }

   /** @deprecated */
   @Deprecated
   public static int getConnectionCountWithHostAsMaster(String groupFilter, String hostPortPair) {
      return getConnectionCountWithHostAsSource(groupFilter, hostPortPair);
   }

   public static Collection<String> getReplicaHosts(String groupFilter) {
      Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
      Collection<String> hosts = new ArrayList();
      Iterator var3 = s.iterator();

      while(var3.hasNext()) {
         ReplicationConnectionGroup cg = (ReplicationConnectionGroup)var3.next();
         hosts.addAll(cg.getReplicaHosts());
      }

      return hosts;
   }

   /** @deprecated */
   @Deprecated
   public static Collection<String> getSlaveHosts(String groupFilter) {
      return getReplicaHosts(groupFilter);
   }

   public static Collection<String> getSourceHosts(String groupFilter) {
      Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
      Collection<String> hosts = new ArrayList();
      Iterator var3 = s.iterator();

      while(var3.hasNext()) {
         ReplicationConnectionGroup cg = (ReplicationConnectionGroup)var3.next();
         hosts.addAll(cg.getSourceHosts());
      }

      return hosts;
   }

   /** @deprecated */
   @Deprecated
   public static Collection<String> getMasterHosts(String groupFilter) {
      return getSourceHosts(groupFilter);
   }

   public static long getTotalConnectionCount(String group) {
      long connections = 0L;
      Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);

      ReplicationConnectionGroup cg;
      for(Iterator var4 = s.iterator(); var4.hasNext(); connections += cg.getTotalConnectionCount()) {
         cg = (ReplicationConnectionGroup)var4.next();
      }

      return connections;
   }

   public static long getActiveConnectionCount(String group) {
      long connections = 0L;
      Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);

      ReplicationConnectionGroup cg;
      for(Iterator var4 = s.iterator(); var4.hasNext(); connections += cg.getActiveConnectionCount()) {
         cg = (ReplicationConnectionGroup)var4.next();
      }

      return connections;
   }
}
