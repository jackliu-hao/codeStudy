/*     */ package com.mysql.cj.conf.url;
/*     */ 
/*     */ import com.mysql.cj.conf.ConnectionUrl;
/*     */ import com.mysql.cj.conf.ConnectionUrlParser;
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.util.Collection;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LoadBalanceConnectionUrl
/*     */   extends ConnectionUrl
/*     */ {
/*     */   public LoadBalanceConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
/*  55 */     super(connStrParser, info);
/*  56 */     this.type = ConnectionUrl.Type.LOADBALANCE_CONNECTION;
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
/*     */   public LoadBalanceConnectionUrl(List<HostInfo> hosts, Map<String, String> properties) {
/*  82 */     this.originalConnStr = ConnectionUrl.Type.LOADBALANCE_CONNECTION.getScheme() + "//**internally_generated**" + System.currentTimeMillis() + "**";
/*  83 */     this.originalDatabase = properties.containsKey(PropertyKey.DBNAME.getKeyName()) ? properties.get(PropertyKey.DBNAME.getKeyName()) : "";
/*  84 */     this.type = ConnectionUrl.Type.LOADBALANCE_CONNECTION;
/*  85 */     this.properties.putAll(properties);
/*  86 */     injectPerTypeProperties(this.properties);
/*  87 */     setupPropertiesTransformer();
/*  88 */     hosts.stream().map(this::fixHostInfo).forEach(this.hosts::add);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void injectPerTypeProperties(Map<String, String> props) {
/*  99 */     if (props.containsKey(PropertyKey.loadBalanceAutoCommitStatementThreshold.getKeyName())) {
/*     */       try {
/* 101 */         int autoCommitSwapThreshold = Integer.parseInt(props.get(PropertyKey.loadBalanceAutoCommitStatementThreshold.getKeyName()));
/* 102 */         if (autoCommitSwapThreshold > 0) {
/* 103 */           String queryInterceptors = props.get(PropertyKey.queryInterceptors.getKeyName());
/* 104 */           String lbi = "com.mysql.cj.jdbc.ha.LoadBalancedAutoCommitInterceptor";
/* 105 */           if (StringUtils.isNullOrEmpty(queryInterceptors)) {
/* 106 */             props.put(PropertyKey.queryInterceptors.getKeyName(), lbi);
/*     */           } else {
/* 108 */             props.put(PropertyKey.queryInterceptors.getKeyName(), queryInterceptors + "," + lbi);
/*     */           } 
/*     */         } 
/* 111 */       } catch (Throwable throwable) {}
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
/*     */   public List<String> getHostInfoListAsHostPortPairs() {
/* 123 */     return (List<String>)this.hosts.stream().map(hi -> hi.getHostPortPair()).collect(Collectors.toList());
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
/*     */   public List<HostInfo> getHostInfoListFromHostPortPairs(Collection<String> hostPortPairs) {
/* 135 */     return (List<HostInfo>)hostPortPairs.stream().map(this::getHostOrSpawnIsolated).collect(Collectors.toList());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\con\\url\LoadBalanceConnectionUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */