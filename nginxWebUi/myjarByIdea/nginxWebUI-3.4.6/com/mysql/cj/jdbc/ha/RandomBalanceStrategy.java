package com.mysql.cj.jdbc.ha;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.exceptions.SQLError;
import java.lang.reflect.InvocationHandler;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomBalanceStrategy implements BalanceStrategy {
   public ConnectionImpl pickConnection(InvocationHandler proxy, List<String> configuredHosts, Map<String, JdbcConnection> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
      int numHosts = configuredHosts.size();
      SQLException ex = null;
      List<String> allowList = new ArrayList(numHosts);
      allowList.addAll(configuredHosts);
      Map<String, Long> blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
      allowList.removeAll(blockList.keySet());
      Map<String, Integer> allowListMap = this.getArrayIndexMap(allowList);
      int attempts = 0;

      ConnectionImpl conn;
      while(true) {
         if (attempts >= numRetries) {
            if (ex != null) {
               throw ex;
            }

            return null;
         }

         int random = (int)Math.floor(Math.random() * (double)allowList.size());
         if (allowList.size() == 0) {
            throw SQLError.createSQLException(Messages.getString("RandomBalanceStrategy.0"), (ExceptionInterceptor)null);
         }

         String hostPortSpec = (String)allowList.get(random);
         conn = (ConnectionImpl)liveConnections.get(hostPortSpec);
         if (conn != null) {
            break;
         }

         try {
            conn = ((LoadBalancedConnectionProxy)proxy).createConnectionForHost(hostPortSpec);
            break;
         } catch (SQLException var19) {
            ex = var19;
            if (!((LoadBalancedConnectionProxy)proxy).shouldExceptionTriggerConnectionSwitch(var19)) {
               throw var19;
            }

            Integer allowListIndex = (Integer)allowListMap.get(hostPortSpec);
            if (allowListIndex != null) {
               allowList.remove(allowListIndex);
               allowListMap = this.getArrayIndexMap(allowList);
            }

            ((LoadBalancedConnectionProxy)proxy).addToGlobalBlocklist(hostPortSpec);
            if (allowList.size() == 0) {
               ++attempts;

               try {
                  Thread.sleep(250L);
               } catch (InterruptedException var18) {
               }

               new HashMap(numHosts);
               allowList.addAll(configuredHosts);
               blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
               allowList.removeAll(blockList.keySet());
               allowListMap = this.getArrayIndexMap(allowList);
            }
         }
      }

      return conn;
   }

   private Map<String, Integer> getArrayIndexMap(List<String> l) {
      Map<String, Integer> m = new HashMap(l.size());

      for(int i = 0; i < l.size(); ++i) {
         m.put(l.get(i), i);
      }

      return m;
   }
}
