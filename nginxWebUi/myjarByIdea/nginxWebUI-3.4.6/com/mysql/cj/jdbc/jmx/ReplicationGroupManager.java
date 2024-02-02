package com.mysql.cj.jdbc.jmx;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.ha.ReplicationConnectionGroup;
import com.mysql.cj.jdbc.ha.ReplicationConnectionGroupManager;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.Iterator;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class ReplicationGroupManager implements ReplicationGroupManagerMBean {
   private boolean isJmxRegistered = false;

   public synchronized void registerJmx() throws SQLException {
      if (!this.isJmxRegistered) {
         MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

         try {
            ObjectName name = new ObjectName("com.mysql.cj.jdbc.jmx:type=ReplicationGroupManager");
            mbs.registerMBean(this, name);
            this.isJmxRegistered = true;
         } catch (Exception var3) {
            throw SQLError.createSQLException(Messages.getString("ReplicationGroupManager.0"), (String)null, var3, (ExceptionInterceptor)null);
         }
      }
   }

   public void addReplicaHost(String groupFilter, String host) throws SQLException {
      ReplicationConnectionGroupManager.addReplicaHost(groupFilter, host);
   }

   public void removeReplicaHost(String groupFilter, String host) throws SQLException {
      ReplicationConnectionGroupManager.removeReplicaHost(groupFilter, host);
   }

   public void promoteReplicaToSource(String groupFilter, String host) throws SQLException {
      ReplicationConnectionGroupManager.promoteReplicaToSource(groupFilter, host);
   }

   public void removeSourceHost(String groupFilter, String host) throws SQLException {
      ReplicationConnectionGroupManager.removeSourceHost(groupFilter, host);
   }

   public String getSourceHostsList(String group) {
      StringBuilder sb = new StringBuilder("");
      boolean found = false;
      Iterator var4 = ReplicationConnectionGroupManager.getSourceHosts(group).iterator();

      while(var4.hasNext()) {
         String host = (String)var4.next();
         if (found) {
            sb.append(",");
         }

         found = true;
         sb.append(host);
      }

      return sb.toString();
   }

   public String getReplicaHostsList(String group) {
      StringBuilder sb = new StringBuilder("");
      boolean found = false;
      Iterator var4 = ReplicationConnectionGroupManager.getReplicaHosts(group).iterator();

      while(var4.hasNext()) {
         String host = (String)var4.next();
         if (found) {
            sb.append(",");
         }

         found = true;
         sb.append(host);
      }

      return sb.toString();
   }

   public String getRegisteredConnectionGroups() {
      StringBuilder sb = new StringBuilder("");
      boolean found = false;
      Iterator var3 = ReplicationConnectionGroupManager.getGroupsMatching((String)null).iterator();

      while(var3.hasNext()) {
         ReplicationConnectionGroup group = (ReplicationConnectionGroup)var3.next();
         if (found) {
            sb.append(",");
         }

         found = true;
         sb.append(group.getGroupName());
      }

      return sb.toString();
   }

   public int getActiveSourceHostCount(String group) {
      return ReplicationConnectionGroupManager.getSourceHosts(group).size();
   }

   public int getActiveReplicaHostCount(String group) {
      return ReplicationConnectionGroupManager.getReplicaHosts(group).size();
   }

   public int getReplicaPromotionCount(String group) {
      return ReplicationConnectionGroupManager.getNumberOfSourcePromotion(group);
   }

   public long getTotalLogicalConnectionCount(String group) {
      return ReplicationConnectionGroupManager.getTotalConnectionCount(group);
   }

   public long getActiveLogicalConnectionCount(String group) {
      return ReplicationConnectionGroupManager.getActiveConnectionCount(group);
   }
}
