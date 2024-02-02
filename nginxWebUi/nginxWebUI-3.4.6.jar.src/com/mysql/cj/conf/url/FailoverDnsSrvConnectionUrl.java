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
/*     */ public class FailoverDnsSrvConnectionUrl
/*     */   extends ConnectionUrl
/*     */ {
/*     */   private static final String DEFAULT_HOST = "";
/*     */   private static final int DEFAULT_PORT = -1;
/*     */   
/*     */   public FailoverDnsSrvConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
/*  59 */     super(connStrParser, info);
/*  60 */     this.type = ConnectionUrl.Type.FAILOVER_DNS_SRV_CONNECTION;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  70 */     HostInfo srvHost = getMainHost();
/*  71 */     Map<String, String> hostProps = srvHost.getHostProperties();
/*  72 */     if ("".equals(srvHost.getHost())) {
/*  73 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.18"));
/*     */     }
/*  75 */     if (this.hosts.size() != 1) {
/*  76 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.19"));
/*     */     }
/*  78 */     if (srvHost.getPort() != -1) {
/*  79 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.22"));
/*     */     }
/*  81 */     if (hostProps.containsKey(PropertyKey.dnsSrv.getKeyName()) && 
/*  82 */       !BooleanPropertyDefinition.booleanFrom(PropertyKey.dnsSrv.getKeyName(), hostProps.get(PropertyKey.dnsSrv.getKeyName()), null).booleanValue()) {
/*  83 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, 
/*  84 */           Messages.getString("ConnectionString.23", new Object[] { PropertyKey.dnsSrv.getKeyName() }));
/*     */     }
/*     */     
/*  87 */     if (hostProps.containsKey(PropertyKey.PROTOCOL.getKeyName()) && ((String)hostProps.get(PropertyKey.PROTOCOL.getKeyName())).equalsIgnoreCase("PIPE")) {
/*  88 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.24"));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDefaultHost() {
/*  94 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDefaultPort() {
/*  99 */     return -1;
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
/* 113 */     return getHostsListFromDnsSrv(getMainHost());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\con\\url\FailoverDnsSrvConnectionUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */