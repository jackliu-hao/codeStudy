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
/*     */ import com.mysql.cj.util.StringUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LoadBalanceDnsSrvConnectionUrl
/*     */   extends ConnectionUrl
/*     */ {
/*     */   private static final String DEFAULT_HOST = "";
/*     */   private static final int DEFAULT_PORT = -1;
/*     */   
/*     */   public LoadBalanceDnsSrvConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
/*  60 */     super(connStrParser, info);
/*  61 */     this.type = ConnectionUrl.Type.LOADBALANCE_DNS_SRV_CONNECTION;
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
/*  72 */     HostInfo srvHost = getMainHost();
/*  73 */     Map<String, String> hostProps = srvHost.getHostProperties();
/*  74 */     if ("".equals(srvHost.getHost())) {
/*  75 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.18"));
/*     */     }
/*  77 */     if (this.hosts.size() != 1) {
/*  78 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.19"));
/*     */     }
/*  80 */     if (srvHost.getPort() != -1) {
/*  81 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.22"));
/*     */     }
/*  83 */     if (hostProps.containsKey(PropertyKey.dnsSrv.getKeyName()) && 
/*  84 */       !BooleanPropertyDefinition.booleanFrom(PropertyKey.dnsSrv.getKeyName(), hostProps.get(PropertyKey.dnsSrv.getKeyName()), null).booleanValue()) {
/*  85 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, 
/*  86 */           Messages.getString("ConnectionString.23", new Object[] { PropertyKey.dnsSrv.getKeyName() }));
/*     */     }
/*     */     
/*  89 */     if (hostProps.containsKey(PropertyKey.PROTOCOL.getKeyName()) && ((String)hostProps.get(PropertyKey.PROTOCOL.getKeyName())).equalsIgnoreCase("PIPE")) {
/*  90 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.24"));
/*     */     }
/*  92 */     if (hostProps.containsKey(PropertyKey.loadBalanceConnectionGroup.getKeyName())) {
/*  93 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, 
/*  94 */           Messages.getString("ConnectionString.25", new Object[] { PropertyKey.loadBalanceConnectionGroup.getKeyName() }));
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
/*     */   protected void injectPerTypeProperties(Map<String, String> props) {
/* 107 */     if (props.containsKey(PropertyKey.loadBalanceAutoCommitStatementThreshold.getKeyName())) {
/*     */       try {
/* 109 */         int autoCommitSwapThreshold = Integer.parseInt(props.get(PropertyKey.loadBalanceAutoCommitStatementThreshold.getKeyName()));
/* 110 */         if (autoCommitSwapThreshold > 0) {
/* 111 */           String queryInterceptors = props.get(PropertyKey.queryInterceptors.getKeyName());
/* 112 */           String lbi = "com.mysql.cj.jdbc.ha.LoadBalancedAutoCommitInterceptor";
/* 113 */           if (StringUtils.isNullOrEmpty(queryInterceptors)) {
/* 114 */             props.put(PropertyKey.queryInterceptors.getKeyName(), lbi);
/*     */           } else {
/* 116 */             props.put(PropertyKey.queryInterceptors.getKeyName(), queryInterceptors + "," + lbi);
/*     */           } 
/*     */         } 
/* 119 */       } catch (Throwable throwable) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDefaultHost() {
/* 127 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDefaultPort() {
/* 132 */     return -1;
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
/* 146 */     return getHostsListFromDnsSrv(getMainHost());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\con\\url\LoadBalanceDnsSrvConnectionUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */