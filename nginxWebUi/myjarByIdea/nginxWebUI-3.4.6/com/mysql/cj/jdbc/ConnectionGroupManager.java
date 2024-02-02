package com.mysql.cj.jdbc;

import com.mysql.cj.jdbc.jmx.LoadBalanceConnectionGroupManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConnectionGroupManager {
   private static HashMap<String, ConnectionGroup> GROUP_MAP = new HashMap();
   private static LoadBalanceConnectionGroupManager mbean = new LoadBalanceConnectionGroupManager();
   private static boolean hasRegisteredJmx = false;

   public static synchronized ConnectionGroup getConnectionGroupInstance(String groupName) {
      if (GROUP_MAP.containsKey(groupName)) {
         return (ConnectionGroup)GROUP_MAP.get(groupName);
      } else {
         ConnectionGroup group = new ConnectionGroup(groupName);
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

   public static ConnectionGroup getConnectionGroup(String groupName) {
      return (ConnectionGroup)GROUP_MAP.get(groupName);
   }

   private static Collection<ConnectionGroup> getGroupsMatching(String group) {
      HashSet s;
      if (group != null && !group.equals("")) {
         s = new HashSet();
         ConnectionGroup o = (ConnectionGroup)GROUP_MAP.get(group);
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

   public static void addHost(String group, String hostPortPair, boolean forExisting) {
      Collection<ConnectionGroup> s = getGroupsMatching(group);
      Iterator var4 = s.iterator();

      while(var4.hasNext()) {
         ConnectionGroup cg = (ConnectionGroup)var4.next();
         cg.addHost(hostPortPair, forExisting);
      }

   }

   public static int getActiveHostCount(String group) {
      Set<String> active = new HashSet();
      Collection<ConnectionGroup> s = getGroupsMatching(group);
      Iterator var3 = s.iterator();

      while(var3.hasNext()) {
         ConnectionGroup cg = (ConnectionGroup)var3.next();
         active.addAll(cg.getInitialHosts());
      }

      return active.size();
   }

   public static long getActiveLogicalConnectionCount(String group) {
      int count = 0;
      Collection<ConnectionGroup> s = getGroupsMatching(group);

      ConnectionGroup cg;
      for(Iterator var3 = s.iterator(); var3.hasNext(); count = (int)((long)count + cg.getActiveLogicalConnectionCount())) {
         cg = (ConnectionGroup)var3.next();
      }

      return (long)count;
   }

   public static long getActivePhysicalConnectionCount(String group) {
      int count = 0;
      Collection<ConnectionGroup> s = getGroupsMatching(group);

      ConnectionGroup cg;
      for(Iterator var3 = s.iterator(); var3.hasNext(); count = (int)((long)count + cg.getActivePhysicalConnectionCount())) {
         cg = (ConnectionGroup)var3.next();
      }

      return (long)count;
   }

   public static int getTotalHostCount(String group) {
      Collection<ConnectionGroup> s = getGroupsMatching(group);
      Set<String> hosts = new HashSet();
      Iterator var3 = s.iterator();

      while(var3.hasNext()) {
         ConnectionGroup cg = (ConnectionGroup)var3.next();
         hosts.addAll(cg.getInitialHosts());
         hosts.addAll(cg.getClosedHosts());
      }

      return hosts.size();
   }

   public static long getTotalLogicalConnectionCount(String group) {
      long count = 0L;
      Collection<ConnectionGroup> s = getGroupsMatching(group);

      ConnectionGroup cg;
      for(Iterator var4 = s.iterator(); var4.hasNext(); count += cg.getTotalLogicalConnectionCount()) {
         cg = (ConnectionGroup)var4.next();
      }

      return count;
   }

   public static long getTotalPhysicalConnectionCount(String group) {
      long count = 0L;
      Collection<ConnectionGroup> s = getGroupsMatching(group);

      ConnectionGroup cg;
      for(Iterator var4 = s.iterator(); var4.hasNext(); count += cg.getTotalPhysicalConnectionCount()) {
         cg = (ConnectionGroup)var4.next();
      }

      return count;
   }

   public static long getTotalTransactionCount(String group) {
      long count = 0L;
      Collection<ConnectionGroup> s = getGroupsMatching(group);

      ConnectionGroup cg;
      for(Iterator var4 = s.iterator(); var4.hasNext(); count += cg.getTotalTransactionCount()) {
         cg = (ConnectionGroup)var4.next();
      }

      return count;
   }

   public static void removeHost(String group, String hostPortPair) throws SQLException {
      removeHost(group, hostPortPair, false);
   }

   public static void removeHost(String group, String host, boolean removeExisting) throws SQLException {
      Collection<ConnectionGroup> s = getGroupsMatching(group);
      Iterator var4 = s.iterator();

      while(var4.hasNext()) {
         ConnectionGroup cg = (ConnectionGroup)var4.next();
         cg.removeHost(host, removeExisting);
      }

   }

   public static String getActiveHostLists(String group) {
      Collection<ConnectionGroup> s = getGroupsMatching(group);
      Map<String, Integer> hosts = new HashMap();
      Iterator var3 = s.iterator();

      while(var3.hasNext()) {
         ConnectionGroup cg = (ConnectionGroup)var3.next();
         Collection<String> l = cg.getInitialHosts();

         String host;
         Integer o;
         for(Iterator var6 = l.iterator(); var6.hasNext(); hosts.put(host, o)) {
            host = (String)var6.next();
            o = (Integer)hosts.get(host);
            if (o == null) {
               o = 1;
            } else {
               o = o + 1;
            }
         }
      }

      StringBuilder sb = new StringBuilder();
      String sep = "";

      for(Iterator var11 = hosts.keySet().iterator(); var11.hasNext(); sep = ",") {
         String host = (String)var11.next();
         sb.append(sep);
         sb.append(host);
         sb.append('(');
         sb.append(hosts.get(host));
         sb.append(')');
      }

      return sb.toString();
   }

   public static String getRegisteredConnectionGroups() {
      Collection<ConnectionGroup> s = getGroupsMatching((String)null);
      StringBuilder sb = new StringBuilder();
      String sep = "";

      for(Iterator var3 = s.iterator(); var3.hasNext(); sep = ",") {
         ConnectionGroup cg = (ConnectionGroup)var3.next();
         String group = cg.getGroupName();
         sb.append(sep);
         sb.append(group);
      }

      return sb.toString();
   }
}
