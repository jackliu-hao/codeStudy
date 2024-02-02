package com.mysql.cj.conf.url;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.ConnectionUrlParser;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.HostsListView;
import com.mysql.cj.conf.PropertyKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReplicationConnectionUrl extends ConnectionUrl {
   private static final String TYPE_SOURCE = "SOURCE";
   private static final String TYPE_REPLICA = "REPLICA";
   /** @deprecated */
   @Deprecated
   private static final String TYPE_SOURCE_DEPRECATED = "MASTER";
   /** @deprecated */
   @Deprecated
   private static final String TYPE_REPLICA_DEPRECATED = "SLAVE";
   private List<HostInfo> sourceHosts = new ArrayList();
   private List<HostInfo> replicaHosts = new ArrayList();

   public ReplicationConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
      super(connStrParser, info);
      this.type = ConnectionUrl.Type.REPLICATION_CONNECTION;
      LinkedList<HostInfo> undefinedHosts = new LinkedList();
      Iterator var4 = this.hosts.iterator();

      while(true) {
         while(true) {
            while(true) {
               while(var4.hasNext()) {
                  HostInfo hi = (HostInfo)var4.next();
                  Map<String, String> hostProperties = hi.getHostProperties();
                  if (hostProperties.containsKey(PropertyKey.TYPE.getKeyName())) {
                     if (!"SOURCE".equalsIgnoreCase((String)hostProperties.get(PropertyKey.TYPE.getKeyName())) && !"MASTER".equalsIgnoreCase((String)hostProperties.get(PropertyKey.TYPE.getKeyName()))) {
                        if (!"REPLICA".equalsIgnoreCase((String)hostProperties.get(PropertyKey.TYPE.getKeyName())) && !"SLAVE".equalsIgnoreCase((String)hostProperties.get(PropertyKey.TYPE.getKeyName()))) {
                           undefinedHosts.add(hi);
                        } else {
                           this.replicaHosts.add(hi);
                        }
                     } else {
                        this.sourceHosts.add(hi);
                     }
                  } else {
                     undefinedHosts.add(hi);
                  }
               }

               if (!undefinedHosts.isEmpty()) {
                  if (this.sourceHosts.isEmpty()) {
                     this.sourceHosts.add(undefinedHosts.removeFirst());
                  }

                  this.replicaHosts.addAll(undefinedHosts);
               }

               return;
            }
         }
      }
   }

   public ReplicationConnectionUrl(List<HostInfo> sources, List<HostInfo> replicas, Map<String, String> properties) {
      this.originalConnStr = ConnectionUrl.Type.REPLICATION_CONNECTION.getScheme() + "//**internally_generated**" + System.currentTimeMillis() + "**";
      this.originalDatabase = properties.containsKey(PropertyKey.DBNAME.getKeyName()) ? (String)properties.get(PropertyKey.DBNAME.getKeyName()) : "";
      this.type = ConnectionUrl.Type.REPLICATION_CONNECTION;
      this.properties.putAll(properties);
      this.injectPerTypeProperties(this.properties);
      this.setupPropertiesTransformer();
      Stream var10000 = sources.stream().map(this::fixHostInfo);
      List var10001 = this.sourceHosts;
      var10001.getClass();
      var10000 = var10000.peek(var10001::add);
      var10001 = this.hosts;
      var10000.forEach(var10001::add);
      var10000 = replicas.stream().map(this::fixHostInfo);
      var10001 = this.replicaHosts;
      var10001.getClass();
      var10000 = var10000.peek(var10001::add);
      var10001 = this.hosts;
      var10000.forEach(var10001::add);
   }

   public List<HostInfo> getHostsList(HostsListView view) {
      switch (view) {
         case SOURCES:
            return Collections.unmodifiableList(this.sourceHosts);
         case REPLICAS:
            return Collections.unmodifiableList(this.replicaHosts);
         default:
            return super.getHostsList(HostsListView.ALL);
      }
   }

   public HostInfo getSourceHostOrSpawnIsolated(String hostPortPair) {
      return super.getHostOrSpawnIsolated(hostPortPair, this.sourceHosts);
   }

   public List<String> getSourcesListAsHostPortPairs() {
      return (List)this.sourceHosts.stream().map((hi) -> {
         return hi.getHostPortPair();
      }).collect(Collectors.toList());
   }

   public List<HostInfo> getSourceHostsListFromHostPortPairs(Collection<String> hostPortPairs) {
      return (List)hostPortPairs.stream().map(this::getSourceHostOrSpawnIsolated).collect(Collectors.toList());
   }

   public HostInfo getReplicaHostOrSpawnIsolated(String hostPortPair) {
      return super.getHostOrSpawnIsolated(hostPortPair, this.replicaHosts);
   }

   public List<String> getReplicasListAsHostPortPairs() {
      return (List)this.replicaHosts.stream().map((hi) -> {
         return hi.getHostPortPair();
      }).collect(Collectors.toList());
   }

   public List<HostInfo> getReplicaHostsListFromHostPortPairs(Collection<String> hostPortPairs) {
      return (List)hostPortPairs.stream().map(this::getReplicaHostOrSpawnIsolated).collect(Collectors.toList());
   }
}
