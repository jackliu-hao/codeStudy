/*     */ package com.mysql.cj.conf.url;
/*     */ 
/*     */ import com.mysql.cj.conf.ConnectionUrl;
/*     */ import com.mysql.cj.conf.ConnectionUrlParser;
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.HostsListView;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.stream.Collectors;
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
/*     */ public class ReplicationConnectionUrl
/*     */   extends ConnectionUrl
/*     */ {
/*     */   private static final String TYPE_SOURCE = "SOURCE";
/*     */   private static final String TYPE_REPLICA = "REPLICA";
/*     */   @Deprecated
/*     */   private static final String TYPE_SOURCE_DEPRECATED = "MASTER";
/*     */   @Deprecated
/*     */   private static final String TYPE_REPLICA_DEPRECATED = "SLAVE";
/*  55 */   private List<HostInfo> sourceHosts = new ArrayList<>();
/*  56 */   private List<HostInfo> replicaHosts = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReplicationConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
/*  67 */     super(connStrParser, info);
/*  68 */     this.type = ConnectionUrl.Type.REPLICATION_CONNECTION;
/*     */ 
/*     */     
/*  71 */     LinkedList<HostInfo> undefinedHosts = new LinkedList<>();
/*  72 */     for (HostInfo hi : this.hosts) {
/*  73 */       Map<String, String> hostProperties = hi.getHostProperties();
/*  74 */       if (hostProperties.containsKey(PropertyKey.TYPE.getKeyName())) {
/*  75 */         if ("SOURCE".equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName())) || "MASTER"
/*  76 */           .equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName()))) {
/*  77 */           this.sourceHosts.add(hi); continue;
/*  78 */         }  if ("REPLICA".equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName())) || "SLAVE"
/*  79 */           .equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName()))) {
/*  80 */           this.replicaHosts.add(hi); continue;
/*     */         } 
/*  82 */         undefinedHosts.add(hi);
/*     */         continue;
/*     */       } 
/*  85 */       undefinedHosts.add(hi);
/*     */     } 
/*     */     
/*  88 */     if (!undefinedHosts.isEmpty()) {
/*  89 */       if (this.sourceHosts.isEmpty()) {
/*  90 */         this.sourceHosts.add(undefinedHosts.removeFirst());
/*     */       }
/*  92 */       this.replicaHosts.addAll(undefinedHosts);
/*     */     } 
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
/*     */   public ReplicationConnectionUrl(List<HostInfo> sources, List<HostInfo> replicas, Map<String, String> properties) {
/* 123 */     this.originalConnStr = ConnectionUrl.Type.REPLICATION_CONNECTION.getScheme() + "//**internally_generated**" + System.currentTimeMillis() + "**";
/* 124 */     this.originalDatabase = properties.containsKey(PropertyKey.DBNAME.getKeyName()) ? properties.get(PropertyKey.DBNAME.getKeyName()) : "";
/* 125 */     this.type = ConnectionUrl.Type.REPLICATION_CONNECTION;
/* 126 */     this.properties.putAll(properties);
/* 127 */     injectPerTypeProperties(this.properties);
/* 128 */     setupPropertiesTransformer();
/* 129 */     sources.stream().map(this::fixHostInfo).peek(this.sourceHosts::add).forEach(this.hosts::add);
/* 130 */     replicas.stream().map(this::fixHostInfo).peek(this.replicaHosts::add).forEach(this.hosts::add);
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
/*     */   public List<HostInfo> getHostsList(HostsListView view) {
/* 143 */     switch (view) {
/*     */       case SOURCES:
/* 145 */         return Collections.unmodifiableList(this.sourceHosts);
/*     */       case REPLICAS:
/* 147 */         return Collections.unmodifiableList(this.replicaHosts);
/*     */     } 
/* 149 */     return super.getHostsList(HostsListView.ALL);
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
/*     */   public HostInfo getSourceHostOrSpawnIsolated(String hostPortPair) {
/* 161 */     return getHostOrSpawnIsolated(hostPortPair, this.sourceHosts);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getSourcesListAsHostPortPairs() {
/* 170 */     return (List<String>)this.sourceHosts.stream().map(hi -> hi.getHostPortPair()).collect(Collectors.toList());
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
/*     */   public List<HostInfo> getSourceHostsListFromHostPortPairs(Collection<String> hostPortPairs) {
/* 182 */     return (List<HostInfo>)hostPortPairs.stream().map(this::getSourceHostOrSpawnIsolated).collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HostInfo getReplicaHostOrSpawnIsolated(String hostPortPair) {
/* 193 */     return getHostOrSpawnIsolated(hostPortPair, this.replicaHosts);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getReplicasListAsHostPortPairs() {
/* 202 */     return (List<String>)this.replicaHosts.stream().map(hi -> hi.getHostPortPair()).collect(Collectors.toList());
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
/*     */   public List<HostInfo> getReplicaHostsListFromHostPortPairs(Collection<String> hostPortPairs) {
/* 214 */     return (List<HostInfo>)hostPortPairs.stream().map(this::getReplicaHostOrSpawnIsolated).collect(Collectors.toList());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\con\\url\ReplicationConnectionUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */