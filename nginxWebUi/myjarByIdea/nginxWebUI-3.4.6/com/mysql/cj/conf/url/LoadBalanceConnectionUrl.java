package com.mysql.cj.conf.url;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.ConnectionUrlParser;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.util.StringUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoadBalanceConnectionUrl extends ConnectionUrl {
   public LoadBalanceConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
      super(connStrParser, info);
      this.type = ConnectionUrl.Type.LOADBALANCE_CONNECTION;
   }

   public LoadBalanceConnectionUrl(List<HostInfo> hosts, Map<String, String> properties) {
      this.originalConnStr = ConnectionUrl.Type.LOADBALANCE_CONNECTION.getScheme() + "//**internally_generated**" + System.currentTimeMillis() + "**";
      this.originalDatabase = properties.containsKey(PropertyKey.DBNAME.getKeyName()) ? (String)properties.get(PropertyKey.DBNAME.getKeyName()) : "";
      this.type = ConnectionUrl.Type.LOADBALANCE_CONNECTION;
      this.properties.putAll(properties);
      this.injectPerTypeProperties(this.properties);
      this.setupPropertiesTransformer();
      Stream var10000 = hosts.stream().map(this::fixHostInfo);
      List var10001 = this.hosts;
      var10000.forEach(var10001::add);
   }

   protected void injectPerTypeProperties(Map<String, String> props) {
      if (props.containsKey(PropertyKey.loadBalanceAutoCommitStatementThreshold.getKeyName())) {
         try {
            int autoCommitSwapThreshold = Integer.parseInt((String)props.get(PropertyKey.loadBalanceAutoCommitStatementThreshold.getKeyName()));
            if (autoCommitSwapThreshold > 0) {
               String queryInterceptors = (String)props.get(PropertyKey.queryInterceptors.getKeyName());
               String lbi = "com.mysql.cj.jdbc.ha.LoadBalancedAutoCommitInterceptor";
               if (StringUtils.isNullOrEmpty(queryInterceptors)) {
                  props.put(PropertyKey.queryInterceptors.getKeyName(), lbi);
               } else {
                  props.put(PropertyKey.queryInterceptors.getKeyName(), queryInterceptors + "," + lbi);
               }
            }
         } catch (Throwable var5) {
         }
      }

   }

   public List<String> getHostInfoListAsHostPortPairs() {
      return (List)this.hosts.stream().map((hi) -> {
         return hi.getHostPortPair();
      }).collect(Collectors.toList());
   }

   public List<HostInfo> getHostInfoListFromHostPortPairs(Collection<String> hostPortPairs) {
      return (List)hostPortPairs.stream().map(this::getHostOrSpawnIsolated).collect(Collectors.toList());
   }
}
