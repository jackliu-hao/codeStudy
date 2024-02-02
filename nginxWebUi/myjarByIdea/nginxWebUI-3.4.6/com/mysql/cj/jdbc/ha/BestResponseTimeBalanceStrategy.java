package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcConnection;
import java.lang.reflect.InvocationHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class BestResponseTimeBalanceStrategy implements BalanceStrategy {
   public ConnectionImpl pickConnection(InvocationHandler proxy, List<String> configuredHosts, Map<String, JdbcConnection> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
      Map<String, Long> blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
      SQLException ex = null;
      int attempts = 0;

      ConnectionImpl conn;
      while(true) {
         if (attempts >= numRetries) {
            if (ex != null) {
               throw ex;
            }

            return null;
         }

         long minResponseTime = Long.MAX_VALUE;
         int bestHostIndex = 0;
         if (blockList.size() == configuredHosts.size()) {
            blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
         }

         for(int i = 0; i < responseTimes.length; ++i) {
            long candidateResponseTime = responseTimes[i];
            if (candidateResponseTime < minResponseTime && !blockList.containsKey(configuredHosts.get(i))) {
               if (candidateResponseTime == 0L) {
                  bestHostIndex = i;
                  break;
               }

               bestHostIndex = i;
               minResponseTime = candidateResponseTime;
            }
         }

         String bestHost = (String)configuredHosts.get(bestHostIndex);
         conn = (ConnectionImpl)liveConnections.get(bestHost);
         if (conn != null) {
            break;
         }

         try {
            conn = ((LoadBalancedConnectionProxy)proxy).createConnectionForHost(bestHost);
            break;
         } catch (SQLException var17) {
            ex = var17;
            if (!((LoadBalancedConnectionProxy)proxy).shouldExceptionTriggerConnectionSwitch(var17)) {
               throw var17;
            }

            ((LoadBalancedConnectionProxy)proxy).addToGlobalBlocklist(bestHost);
            blockList.put(bestHost, (Object)null);
            if (blockList.size() == configuredHosts.size()) {
               ++attempts;

               try {
                  Thread.sleep(250L);
               } catch (InterruptedException var16) {
               }

               blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
            }
         }
      }

      return conn;
   }
}
