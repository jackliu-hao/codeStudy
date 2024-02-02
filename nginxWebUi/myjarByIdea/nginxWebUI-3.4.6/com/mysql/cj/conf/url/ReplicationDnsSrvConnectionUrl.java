package com.mysql.cj.conf.url;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.BooleanPropertyDefinition;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.ConnectionUrlParser;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.HostsListView;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.InvalidConnectionAttributeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ReplicationDnsSrvConnectionUrl extends ConnectionUrl {
   private static final String DEFAULT_HOST = "";
   private static final int DEFAULT_PORT = -1;
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

   public ReplicationDnsSrvConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
      super(connStrParser, info);
      this.type = ConnectionUrl.Type.REPLICATION_DNS_SRV_CONNECTION;
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

               HostInfo srvHostSource = this.sourceHosts.isEmpty() ? null : (HostInfo)this.sourceHosts.get(0);
               Map<String, String> hostPropsSource = srvHostSource == null ? Collections.emptyMap() : srvHostSource.getHostProperties();
               HostInfo srvHostReplica = this.replicaHosts.isEmpty() ? null : (HostInfo)this.replicaHosts.get(0);
               Map<String, String> hostPropsReplica = srvHostReplica == null ? Collections.emptyMap() : srvHostReplica.getHostProperties();
               if (srvHostSource != null && srvHostReplica != null && !"".equals(srvHostSource.getHost()) && !"".equals(srvHostReplica.getHost())) {
                  if (this.sourceHosts.size() == 1 && this.replicaHosts.size() == 1) {
                     if (srvHostSource.getPort() == -1 && srvHostReplica.getPort() == -1) {
                        if (!hostPropsSource.containsKey(PropertyKey.dnsSrv.getKeyName()) && !hostPropsReplica.containsKey(PropertyKey.dnsSrv.getKeyName()) || BooleanPropertyDefinition.booleanFrom(PropertyKey.dnsSrv.getKeyName(), (String)hostPropsSource.get(PropertyKey.dnsSrv.getKeyName()), (ExceptionInterceptor)null) && BooleanPropertyDefinition.booleanFrom(PropertyKey.dnsSrv.getKeyName(), (String)hostPropsReplica.get(PropertyKey.dnsSrv.getKeyName()), (ExceptionInterceptor)null)) {
                           if ((!hostPropsSource.containsKey(PropertyKey.PROTOCOL.getKeyName()) || !((String)hostPropsSource.get(PropertyKey.PROTOCOL.getKeyName())).equalsIgnoreCase("PIPE")) && (!hostPropsReplica.containsKey(PropertyKey.PROTOCOL.getKeyName()) || !((String)hostPropsReplica.get(PropertyKey.PROTOCOL.getKeyName())).equalsIgnoreCase("PIPE"))) {
                              if (!hostPropsSource.containsKey(PropertyKey.replicationConnectionGroup.getKeyName()) && !hostPropsReplica.containsKey(PropertyKey.replicationConnectionGroup.getKeyName())) {
                                 return;
                              }

                              throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.25", new Object[]{PropertyKey.replicationConnectionGroup.getKeyName()}));
                           }

                           throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.24"));
                        }

                        throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.23", new Object[]{PropertyKey.dnsSrv.getKeyName()}));
                     }

                     throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.22"));
                  }

                  throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.21"));
               }

               throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.20"));
            }
         }
      }
   }

   public String getDefaultHost() {
      return "";
   }

   public int getDefaultPort() {
      return -1;
   }

   public List<HostInfo> getHostsList(HostsListView view) {
      switch (view) {
         case SOURCES:
            return this.getHostsListFromDnsSrv((HostInfo)this.sourceHosts.get(0));
         case REPLICAS:
            return this.getHostsListFromDnsSrv((HostInfo)this.replicaHosts.get(0));
         default:
            return super.getHostsList(HostsListView.ALL);
      }
   }
}
