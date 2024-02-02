package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.util.StringUtils;
import java.lang.reflect.InvocationHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ServerAffinityStrategy extends RandomBalanceStrategy {
   public String[] affinityOrderedServers = null;

   public ServerAffinityStrategy(String affinityOrdervers) {
      if (!StringUtils.isNullOrEmpty(affinityOrdervers)) {
         this.affinityOrderedServers = affinityOrdervers.split(",");
      }

   }

   public ConnectionImpl pickConnection(InvocationHandler proxy, List<String> configuredHosts, Map<String, JdbcConnection> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
      if (this.affinityOrderedServers == null) {
         return super.pickConnection(proxy, configuredHosts, liveConnections, responseTimes, numRetries);
      } else {
         Map<String, Long> blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
         String[] var7 = this.affinityOrderedServers;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String host = var7[var9];
            if (configuredHosts.contains(host) && !blockList.containsKey(host)) {
               ConnectionImpl conn = (ConnectionImpl)liveConnections.get(host);
               if (conn != null) {
                  return conn;
               }

               try {
                  conn = ((LoadBalancedConnectionProxy)proxy).createConnectionForHost(host);
                  return conn;
               } catch (SQLException var13) {
                  if (((LoadBalancedConnectionProxy)proxy).shouldExceptionTriggerConnectionSwitch(var13)) {
                     ((LoadBalancedConnectionProxy)proxy).addToGlobalBlocklist(host);
                  }
               }
            }
         }

         return super.pickConnection(proxy, configuredHosts, liveConnections, responseTimes, numRetries);
      }
   }
}
