package com.mysql.cj.jdbc.jmx;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.ConnectionGroupManager;
import com.mysql.cj.jdbc.exceptions.SQLError;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class LoadBalanceConnectionGroupManager implements LoadBalanceConnectionGroupManagerMBean {
   private boolean isJmxRegistered = false;

   public synchronized void registerJmx() throws SQLException {
      if (!this.isJmxRegistered) {
         MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

         try {
            ObjectName name = new ObjectName("com.mysql.cj.jdbc.jmx:type=LoadBalanceConnectionGroupManager");
            mbs.registerMBean(this, name);
            this.isJmxRegistered = true;
         } catch (Exception var3) {
            throw SQLError.createSQLException(Messages.getString("LoadBalanceConnectionGroupManager.0"), (String)null, var3, (ExceptionInterceptor)null);
         }
      }
   }

   public void addHost(String group, String host, boolean forExisting) {
      try {
         ConnectionGroupManager.addHost(group, host, forExisting);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public int getActiveHostCount(String group) {
      return ConnectionGroupManager.getActiveHostCount(group);
   }

   public long getActiveLogicalConnectionCount(String group) {
      return ConnectionGroupManager.getActiveLogicalConnectionCount(group);
   }

   public long getActivePhysicalConnectionCount(String group) {
      return ConnectionGroupManager.getActivePhysicalConnectionCount(group);
   }

   public int getTotalHostCount(String group) {
      return ConnectionGroupManager.getTotalHostCount(group);
   }

   public long getTotalLogicalConnectionCount(String group) {
      return ConnectionGroupManager.getTotalLogicalConnectionCount(group);
   }

   public long getTotalPhysicalConnectionCount(String group) {
      return ConnectionGroupManager.getTotalPhysicalConnectionCount(group);
   }

   public long getTotalTransactionCount(String group) {
      return ConnectionGroupManager.getTotalTransactionCount(group);
   }

   public void removeHost(String group, String host) throws SQLException {
      ConnectionGroupManager.removeHost(group, host);
   }

   public String getActiveHostsList(String group) {
      return ConnectionGroupManager.getActiveHostLists(group);
   }

   public String getRegisteredConnectionGroups() {
      return ConnectionGroupManager.getRegisteredConnectionGroups();
   }

   public void stopNewConnectionsToHost(String group, String host) throws SQLException {
      ConnectionGroupManager.removeHost(group, host);
   }
}
