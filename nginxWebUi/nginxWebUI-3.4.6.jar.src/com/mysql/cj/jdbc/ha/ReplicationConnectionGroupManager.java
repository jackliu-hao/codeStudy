/*     */ package com.mysql.cj.jdbc.ha;
/*     */ 
/*     */ import com.mysql.cj.jdbc.jmx.ReplicationGroupManager;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
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
/*     */ public class ReplicationConnectionGroupManager
/*     */ {
/*  40 */   private static HashMap<String, ReplicationConnectionGroup> GROUP_MAP = new HashMap<>();
/*     */   
/*  42 */   private static ReplicationGroupManager mbean = new ReplicationGroupManager();
/*     */   
/*     */   private static boolean hasRegisteredJmx = false;
/*     */   
/*     */   public static synchronized ReplicationConnectionGroup getConnectionGroupInstance(String groupName) {
/*  47 */     if (GROUP_MAP.containsKey(groupName)) {
/*  48 */       return GROUP_MAP.get(groupName);
/*     */     }
/*  50 */     ReplicationConnectionGroup group = new ReplicationConnectionGroup(groupName);
/*  51 */     GROUP_MAP.put(groupName, group);
/*  52 */     return group;
/*     */   }
/*     */   
/*     */   public static void registerJmx() throws SQLException {
/*  56 */     if (hasRegisteredJmx) {
/*     */       return;
/*     */     }
/*     */     
/*  60 */     mbean.registerJmx();
/*  61 */     hasRegisteredJmx = true;
/*     */   }
/*     */   
/*     */   public static ReplicationConnectionGroup getConnectionGroup(String groupName) {
/*  65 */     return GROUP_MAP.get(groupName);
/*     */   }
/*     */   
/*     */   public static Collection<ReplicationConnectionGroup> getGroupsMatching(String group) {
/*  69 */     if (group == null || group.equals("")) {
/*  70 */       Set<ReplicationConnectionGroup> set = new HashSet<>();
/*     */       
/*  72 */       set.addAll(GROUP_MAP.values());
/*  73 */       return set;
/*     */     } 
/*  75 */     Set<ReplicationConnectionGroup> s = new HashSet<>();
/*  76 */     ReplicationConnectionGroup o = GROUP_MAP.get(group);
/*  77 */     if (o != null) {
/*  78 */       s.add(o);
/*     */     }
/*  80 */     return s;
/*     */   }
/*     */   
/*     */   public static void addReplicaHost(String group, String hostPortPair) throws SQLException {
/*  84 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/*  85 */     for (ReplicationConnectionGroup cg : s) {
/*  86 */       cg.addReplicaHost(hostPortPair);
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
/*     */   public static void addSlaveHost(String group, String hostPortPair) throws SQLException {
/* 102 */     addReplicaHost(group, hostPortPair);
/*     */   }
/*     */   
/*     */   public static void removeReplicaHost(String group, String hostPortPair) throws SQLException {
/* 106 */     removeReplicaHost(group, hostPortPair, true);
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
/*     */   public static void removeSlaveHost(String group, String hostPortPair) throws SQLException {
/* 121 */     removeReplicaHost(group, hostPortPair);
/*     */   }
/*     */   
/*     */   public static void removeReplicaHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
/* 125 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/* 126 */     for (ReplicationConnectionGroup cg : s) {
/* 127 */       cg.removeReplicaHost(hostPortPair, closeGently);
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
/*     */   public static void removeSlaveHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
/* 145 */     removeReplicaHost(group, hostPortPair, closeGently);
/*     */   }
/*     */   
/*     */   public static void promoteReplicaToSource(String group, String hostPortPair) throws SQLException {
/* 149 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/* 150 */     for (ReplicationConnectionGroup cg : s) {
/* 151 */       cg.promoteReplicaToSource(hostPortPair);
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
/*     */   public static void promoteSlaveToMaster(String group, String hostPortPair) throws SQLException {
/* 167 */     promoteReplicaToSource(group, hostPortPair);
/*     */   }
/*     */   
/*     */   public static long getReplicaPromotionCount(String group) throws SQLException {
/* 171 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/* 172 */     long promoted = 0L;
/* 173 */     for (ReplicationConnectionGroup cg : s) {
/* 174 */       long tmp = cg.getNumberOfReplicaPromotions();
/* 175 */       if (tmp > promoted) {
/* 176 */         promoted = tmp;
/*     */       }
/*     */     } 
/* 179 */     return promoted;
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
/*     */   @Deprecated
/*     */   public static long getSlavePromotionCount(String group) throws SQLException {
/* 193 */     return getReplicaPromotionCount(group);
/*     */   }
/*     */   
/*     */   public static void removeSourceHost(String group, String hostPortPair) throws SQLException {
/* 197 */     removeSourceHost(group, hostPortPair, true);
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
/*     */   public static void removeMasterHost(String group, String hostPortPair) throws SQLException {
/* 212 */     removeSourceHost(group, hostPortPair);
/*     */   }
/*     */   
/*     */   public static void removeSourceHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
/* 216 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/* 217 */     for (ReplicationConnectionGroup cg : s) {
/* 218 */       cg.removeSourceHost(hostPortPair, closeGently);
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
/*     */   public static void removeMasterHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
/* 236 */     removeSourceHost(group, hostPortPair, closeGently);
/*     */   }
/*     */   
/*     */   public static String getRegisteredReplicationConnectionGroups() {
/* 240 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(null);
/* 241 */     StringBuilder sb = new StringBuilder();
/* 242 */     String sep = "";
/* 243 */     for (ReplicationConnectionGroup cg : s) {
/* 244 */       String group = cg.getGroupName();
/* 245 */       sb.append(sep);
/* 246 */       sb.append(group);
/* 247 */       sep = ",";
/*     */     } 
/* 249 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static int getNumberOfSourcePromotion(String groupFilter) {
/* 253 */     int total = 0;
/* 254 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
/* 255 */     for (ReplicationConnectionGroup cg : s) {
/* 256 */       total = (int)(total + cg.getNumberOfReplicaPromotions());
/*     */     }
/* 258 */     return total;
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
/*     */   public static int getNumberOfMasterPromotion(String groupFilter) {
/* 271 */     return getNumberOfSourcePromotion(groupFilter);
/*     */   }
/*     */   
/*     */   public static int getConnectionCountWithHostAsReplica(String groupFilter, String hostPortPair) {
/* 275 */     int total = 0;
/* 276 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
/* 277 */     for (ReplicationConnectionGroup cg : s) {
/* 278 */       total += cg.getConnectionCountWithHostAsReplica(hostPortPair);
/*     */     }
/* 280 */     return total;
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
/*     */   public static int getConnectionCountWithHostAsSlave(String groupFilter, String hostPortPair) {
/* 295 */     return getConnectionCountWithHostAsReplica(groupFilter, hostPortPair);
/*     */   }
/*     */   
/*     */   public static int getConnectionCountWithHostAsSource(String groupFilter, String hostPortPair) {
/* 299 */     int total = 0;
/* 300 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
/* 301 */     for (ReplicationConnectionGroup cg : s) {
/* 302 */       total += cg.getConnectionCountWithHostAsSource(hostPortPair);
/*     */     }
/* 304 */     return total;
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
/*     */   public static int getConnectionCountWithHostAsMaster(String groupFilter, String hostPortPair) {
/* 319 */     return getConnectionCountWithHostAsSource(groupFilter, hostPortPair);
/*     */   }
/*     */   
/*     */   public static Collection<String> getReplicaHosts(String groupFilter) {
/* 323 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
/* 324 */     Collection<String> hosts = new ArrayList<>();
/* 325 */     for (ReplicationConnectionGroup cg : s) {
/* 326 */       hosts.addAll(cg.getReplicaHosts());
/*     */     }
/* 328 */     return hosts;
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
/*     */   public static Collection<String> getSlaveHosts(String groupFilter) {
/* 341 */     return getReplicaHosts(groupFilter);
/*     */   }
/*     */   
/*     */   public static Collection<String> getSourceHosts(String groupFilter) {
/* 345 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
/* 346 */     Collection<String> hosts = new ArrayList<>();
/* 347 */     for (ReplicationConnectionGroup cg : s) {
/* 348 */       hosts.addAll(cg.getSourceHosts());
/*     */     }
/* 350 */     return hosts;
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
/*     */   public static Collection<String> getMasterHosts(String groupFilter) {
/* 363 */     return getSourceHosts(groupFilter);
/*     */   }
/*     */   
/*     */   public static long getTotalConnectionCount(String group) {
/* 367 */     long connections = 0L;
/* 368 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/* 369 */     for (ReplicationConnectionGroup cg : s) {
/* 370 */       connections += cg.getTotalConnectionCount();
/*     */     }
/* 372 */     return connections;
/*     */   }
/*     */   
/*     */   public static long getActiveConnectionCount(String group) {
/* 376 */     long connections = 0L;
/* 377 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/* 378 */     for (ReplicationConnectionGroup cg : s) {
/* 379 */       connections += cg.getActiveConnectionCount();
/*     */     }
/* 381 */     return connections;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\ReplicationConnectionGroupManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */