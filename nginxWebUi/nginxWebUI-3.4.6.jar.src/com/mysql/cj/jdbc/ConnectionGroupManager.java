/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.jdbc.jmx.LoadBalanceConnectionGroupManager;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
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
/*     */ 
/*     */ public class ConnectionGroupManager
/*     */ {
/*  43 */   private static HashMap<String, ConnectionGroup> GROUP_MAP = new HashMap<>();
/*     */   
/*  45 */   private static LoadBalanceConnectionGroupManager mbean = new LoadBalanceConnectionGroupManager();
/*     */   
/*     */   private static boolean hasRegisteredJmx = false;
/*     */   
/*     */   public static synchronized ConnectionGroup getConnectionGroupInstance(String groupName) {
/*  50 */     if (GROUP_MAP.containsKey(groupName)) {
/*  51 */       return GROUP_MAP.get(groupName);
/*     */     }
/*  53 */     ConnectionGroup group = new ConnectionGroup(groupName);
/*  54 */     GROUP_MAP.put(groupName, group);
/*  55 */     return group;
/*     */   }
/*     */   
/*     */   public static void registerJmx() throws SQLException {
/*  59 */     if (hasRegisteredJmx) {
/*     */       return;
/*     */     }
/*     */     
/*  63 */     mbean.registerJmx();
/*  64 */     hasRegisteredJmx = true;
/*     */   }
/*     */   
/*     */   public static ConnectionGroup getConnectionGroup(String groupName) {
/*  68 */     return GROUP_MAP.get(groupName);
/*     */   }
/*     */   
/*     */   private static Collection<ConnectionGroup> getGroupsMatching(String group) {
/*  72 */     if (group == null || group.equals("")) {
/*  73 */       Set<ConnectionGroup> set = new HashSet<>();
/*     */       
/*  75 */       set.addAll(GROUP_MAP.values());
/*  76 */       return set;
/*     */     } 
/*  78 */     Set<ConnectionGroup> s = new HashSet<>();
/*  79 */     ConnectionGroup o = GROUP_MAP.get(group);
/*  80 */     if (o != null) {
/*  81 */       s.add(o);
/*     */     }
/*  83 */     return s;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void addHost(String group, String hostPortPair, boolean forExisting) {
/*  88 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/*  89 */     for (ConnectionGroup cg : s) {
/*  90 */       cg.addHost(hostPortPair, forExisting);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static int getActiveHostCount(String group) {
/*  96 */     Set<String> active = new HashSet<>();
/*  97 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/*  98 */     for (ConnectionGroup cg : s) {
/*  99 */       active.addAll(cg.getInitialHosts());
/*     */     }
/* 101 */     return active.size();
/*     */   }
/*     */   
/*     */   public static long getActiveLogicalConnectionCount(String group) {
/* 105 */     int count = 0;
/* 106 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 107 */     for (ConnectionGroup cg : s) {
/* 108 */       count = (int)(count + cg.getActiveLogicalConnectionCount());
/*     */     }
/* 110 */     return count;
/*     */   }
/*     */   
/*     */   public static long getActivePhysicalConnectionCount(String group) {
/* 114 */     int count = 0;
/* 115 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 116 */     for (ConnectionGroup cg : s) {
/* 117 */       count = (int)(count + cg.getActivePhysicalConnectionCount());
/*     */     }
/* 119 */     return count;
/*     */   }
/*     */   
/*     */   public static int getTotalHostCount(String group) {
/* 123 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 124 */     Set<String> hosts = new HashSet<>();
/* 125 */     for (ConnectionGroup cg : s) {
/* 126 */       hosts.addAll(cg.getInitialHosts());
/* 127 */       hosts.addAll(cg.getClosedHosts());
/*     */     } 
/* 129 */     return hosts.size();
/*     */   }
/*     */   
/*     */   public static long getTotalLogicalConnectionCount(String group) {
/* 133 */     long count = 0L;
/* 134 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 135 */     for (ConnectionGroup cg : s) {
/* 136 */       count += cg.getTotalLogicalConnectionCount();
/*     */     }
/* 138 */     return count;
/*     */   }
/*     */   
/*     */   public static long getTotalPhysicalConnectionCount(String group) {
/* 142 */     long count = 0L;
/* 143 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 144 */     for (ConnectionGroup cg : s) {
/* 145 */       count += cg.getTotalPhysicalConnectionCount();
/*     */     }
/* 147 */     return count;
/*     */   }
/*     */   
/*     */   public static long getTotalTransactionCount(String group) {
/* 151 */     long count = 0L;
/* 152 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 153 */     for (ConnectionGroup cg : s) {
/* 154 */       count += cg.getTotalTransactionCount();
/*     */     }
/* 156 */     return count;
/*     */   }
/*     */   
/*     */   public static void removeHost(String group, String hostPortPair) throws SQLException {
/* 160 */     removeHost(group, hostPortPair, false);
/*     */   }
/*     */   
/*     */   public static void removeHost(String group, String host, boolean removeExisting) throws SQLException {
/* 164 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 165 */     for (ConnectionGroup cg : s) {
/* 166 */       cg.removeHost(host, removeExisting);
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getActiveHostLists(String group) {
/* 171 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 172 */     Map<String, Integer> hosts = new HashMap<>();
/* 173 */     for (ConnectionGroup cg : s) {
/*     */       
/* 175 */       Collection<String> l = cg.getInitialHosts();
/* 176 */       for (String host : l) {
/* 177 */         Integer o = hosts.get(host);
/* 178 */         if (o == null) {
/* 179 */           o = Integer.valueOf(1);
/*     */         } else {
/* 181 */           o = Integer.valueOf(o.intValue() + 1);
/*     */         } 
/* 183 */         hosts.put(host, o);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 188 */     StringBuilder sb = new StringBuilder();
/* 189 */     String sep = "";
/* 190 */     for (String host : hosts.keySet()) {
/* 191 */       sb.append(sep);
/* 192 */       sb.append(host);
/* 193 */       sb.append('(');
/* 194 */       sb.append(hosts.get(host));
/* 195 */       sb.append(')');
/* 196 */       sep = ",";
/*     */     } 
/* 198 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static String getRegisteredConnectionGroups() {
/* 202 */     Collection<ConnectionGroup> s = getGroupsMatching(null);
/* 203 */     StringBuilder sb = new StringBuilder();
/* 204 */     String sep = "";
/* 205 */     for (ConnectionGroup cg : s) {
/* 206 */       String group = cg.getGroupName();
/* 207 */       sb.append(sep);
/* 208 */       sb.append(group);
/* 209 */       sep = ",";
/*     */     } 
/* 211 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ConnectionGroupManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */