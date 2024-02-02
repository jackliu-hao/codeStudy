/*     */ package com.mysql.cj.conf.url;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.BooleanPropertyDefinition;
/*     */ import com.mysql.cj.conf.ConnectionUrl;
/*     */ import com.mysql.cj.conf.ConnectionUrlParser;
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.HostsListView;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.InvalidConnectionAttributeException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ReplicationDnsSrvConnectionUrl
/*     */   extends ConnectionUrl
/*     */ {
/*     */   private static final String DEFAULT_HOST = "";
/*     */   private static final int DEFAULT_PORT = -1;
/*     */   private static final String TYPE_SOURCE = "SOURCE";
/*     */   private static final String TYPE_REPLICA = "REPLICA";
/*     */   @Deprecated
/*     */   private static final String TYPE_SOURCE_DEPRECATED = "MASTER";
/*     */   @Deprecated
/*     */   private static final String TYPE_REPLICA_DEPRECATED = "SLAVE";
/*  59 */   private List<HostInfo> sourceHosts = new ArrayList<>();
/*  60 */   private List<HostInfo> replicaHosts = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReplicationDnsSrvConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
/*  71 */     super(connStrParser, info);
/*  72 */     this.type = ConnectionUrl.Type.REPLICATION_DNS_SRV_CONNECTION;
/*     */ 
/*     */     
/*  75 */     LinkedList<HostInfo> undefinedHosts = new LinkedList<>();
/*  76 */     for (HostInfo hi : this.hosts) {
/*  77 */       Map<String, String> hostProperties = hi.getHostProperties();
/*  78 */       if (hostProperties.containsKey(PropertyKey.TYPE.getKeyName())) {
/*  79 */         if ("SOURCE".equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName())) || "MASTER"
/*  80 */           .equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName()))) {
/*  81 */           this.sourceHosts.add(hi); continue;
/*  82 */         }  if ("REPLICA".equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName())) || "SLAVE"
/*  83 */           .equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName()))) {
/*  84 */           this.replicaHosts.add(hi); continue;
/*     */         } 
/*  86 */         undefinedHosts.add(hi);
/*     */         continue;
/*     */       } 
/*  89 */       undefinedHosts.add(hi);
/*     */     } 
/*     */     
/*  92 */     if (!undefinedHosts.isEmpty()) {
/*  93 */       if (this.sourceHosts.isEmpty()) {
/*  94 */         this.sourceHosts.add(undefinedHosts.removeFirst());
/*     */       }
/*  96 */       this.replicaHosts.addAll(undefinedHosts);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 108 */     HostInfo srvHostSource = this.sourceHosts.isEmpty() ? null : this.sourceHosts.get(0);
/* 109 */     Map<String, String> hostPropsSource = (srvHostSource == null) ? Collections.<String, String>emptyMap() : srvHostSource.getHostProperties();
/* 110 */     HostInfo srvHostReplica = this.replicaHosts.isEmpty() ? null : this.replicaHosts.get(0);
/* 111 */     Map<String, String> hostPropsReplica = (srvHostReplica == null) ? Collections.<String, String>emptyMap() : srvHostReplica.getHostProperties();
/* 112 */     if (srvHostSource == null || srvHostReplica == null || "".equals(srvHostSource.getHost()) || "".equals(srvHostReplica.getHost())) {
/* 113 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.20"));
/*     */     }
/* 115 */     if (this.sourceHosts.size() != 1 || this.replicaHosts.size() != 1) {
/* 116 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.21"));
/*     */     }
/* 118 */     if (srvHostSource.getPort() != -1 || srvHostReplica.getPort() != -1) {
/* 119 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.22"));
/*     */     }
/* 121 */     if ((hostPropsSource.containsKey(PropertyKey.dnsSrv.getKeyName()) || hostPropsReplica.containsKey(PropertyKey.dnsSrv.getKeyName())) && (
/* 122 */       !BooleanPropertyDefinition.booleanFrom(PropertyKey.dnsSrv.getKeyName(), hostPropsSource.get(PropertyKey.dnsSrv.getKeyName()), null).booleanValue() || 
/* 123 */       !BooleanPropertyDefinition.booleanFrom(PropertyKey.dnsSrv.getKeyName(), hostPropsReplica.get(PropertyKey.dnsSrv.getKeyName()), null).booleanValue())) {
/* 124 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, 
/* 125 */           Messages.getString("ConnectionString.23", new Object[] { PropertyKey.dnsSrv.getKeyName() }));
/*     */     }
/*     */     
/* 128 */     if ((hostPropsSource.containsKey(PropertyKey.PROTOCOL.getKeyName()) && ((String)hostPropsSource.get(PropertyKey.PROTOCOL.getKeyName())).equalsIgnoreCase("PIPE")) || (hostPropsReplica
/* 129 */       .containsKey(PropertyKey.PROTOCOL.getKeyName()) && ((String)hostPropsReplica
/* 130 */       .get(PropertyKey.PROTOCOL.getKeyName())).equalsIgnoreCase("PIPE"))) {
/* 131 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.24"));
/*     */     }
/* 133 */     if (hostPropsSource.containsKey(PropertyKey.replicationConnectionGroup.getKeyName()) || hostPropsReplica
/* 134 */       .containsKey(PropertyKey.replicationConnectionGroup.getKeyName())) {
/* 135 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, 
/* 136 */           Messages.getString("ConnectionString.25", new Object[] { PropertyKey.replicationConnectionGroup.getKeyName() }));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDefaultHost() {
/* 142 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDefaultPort() {
/* 147 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<HostInfo> getHostsList(HostsListView view) {
/* 161 */     switch (view) {
/*     */       case SOURCES:
/* 163 */         return getHostsListFromDnsSrv(this.sourceHosts.get(0));
/*     */       case REPLICAS:
/* 165 */         return getHostsListFromDnsSrv(this.replicaHosts.get(0));
/*     */     } 
/* 167 */     return super.getHostsList(HostsListView.ALL);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\con\\url\ReplicationDnsSrvConnectionUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */