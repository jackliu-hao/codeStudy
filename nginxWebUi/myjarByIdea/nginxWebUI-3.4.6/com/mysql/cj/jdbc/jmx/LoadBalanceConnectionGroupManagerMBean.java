package com.mysql.cj.jdbc.jmx;

import java.sql.SQLException;

public interface LoadBalanceConnectionGroupManagerMBean {
   int getActiveHostCount(String var1);

   int getTotalHostCount(String var1);

   long getTotalLogicalConnectionCount(String var1);

   long getActiveLogicalConnectionCount(String var1);

   long getActivePhysicalConnectionCount(String var1);

   long getTotalPhysicalConnectionCount(String var1);

   long getTotalTransactionCount(String var1);

   void removeHost(String var1, String var2) throws SQLException;

   void stopNewConnectionsToHost(String var1, String var2) throws SQLException;

   void addHost(String var1, String var2, boolean var3);

   String getActiveHostsList(String var1);

   String getRegisteredConnectionGroups();
}
