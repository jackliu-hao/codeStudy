package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcConnection;
import java.lang.reflect.InvocationHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SequentialBalanceStrategy implements BalanceStrategy {
   private int currentHostIndex = -1;

   public ConnectionImpl pickConnection(InvocationHandler proxy, List<String> configuredHosts, Map<String, JdbcConnection> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
      int numHosts = configuredHosts.size();
      SQLException ex = null;
      Map<String, Long> blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
      int attempts = 0;

      ConnectionImpl conn;
      while(true) {
         if (attempts >= numRetries) {
            if (ex != null) {
               throw ex;
            }

            return null;
         }

         label129: {
            if (numHosts == 1) {
               this.currentHostIndex = 0;
            } else {
               int i;
               if (this.currentHostIndex == -1) {
                  i = (int)Math.floor(Math.random() * (double)numHosts);

                  int i;
                  for(i = i; i < numHosts; ++i) {
                     if (!blockList.containsKey(configuredHosts.get(i))) {
                        this.currentHostIndex = i;
                        break;
                     }
                  }

                  if (this.currentHostIndex == -1) {
                     for(i = 0; i < i; ++i) {
                        if (!blockList.containsKey(configuredHosts.get(i))) {
                           this.currentHostIndex = i;
                           break;
                        }
                     }
                  }

                  if (this.currentHostIndex == -1) {
                     blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();

                     try {
                        Thread.sleep(250L);
                     } catch (InterruptedException var16) {
                     }
                     break label129;
                  }
               } else {
                  i = this.currentHostIndex + 1;

                  boolean foundGoodHost;
                  for(foundGoodHost = false; i < numHosts; ++i) {
                     if (!blockList.containsKey(configuredHosts.get(i))) {
                        this.currentHostIndex = i;
                        foundGoodHost = true;
                        break;
                     }
                  }

                  if (!foundGoodHost) {
                     for(i = 0; i < this.currentHostIndex; ++i) {
                        if (!blockList.containsKey(configuredHosts.get(i))) {
                           this.currentHostIndex = i;
                           foundGoodHost = true;
                           break;
                        }
                     }
                  }

                  if (!foundGoodHost) {
                     blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();

                     try {
                        Thread.sleep(250L);
                     } catch (InterruptedException var15) {
                     }
                     break label129;
                  }
               }
            }

            String hostPortSpec = (String)configuredHosts.get(this.currentHostIndex);
            conn = (ConnectionImpl)liveConnections.get(hostPortSpec);
            if (conn != null) {
               break;
            }

            try {
               conn = ((LoadBalancedConnectionProxy)proxy).createConnectionForHost(hostPortSpec);
               break;
            } catch (SQLException var17) {
               ex = var17;
               if (!((LoadBalancedConnectionProxy)proxy).shouldExceptionTriggerConnectionSwitch(var17)) {
                  throw var17;
               }
            }

            ((LoadBalancedConnectionProxy)proxy).addToGlobalBlocklist(hostPortSpec);

            try {
               Thread.sleep(250L);
            } catch (InterruptedException var14) {
            }
         }

         ++attempts;
      }

      return conn;
   }
}
