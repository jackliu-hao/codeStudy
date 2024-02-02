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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XDevApiDnsSrvConnectionUrl
/*     */   extends ConnectionUrl
/*     */ {
/*     */   private static final String DEFAULT_HOST = "";
/*     */   private static final int DEFAULT_PORT = -1;
/*     */   
/*     */   public XDevApiDnsSrvConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
/*  63 */     super(connStrParser, info);
/*  64 */     this.type = ConnectionUrl.Type.XDEVAPI_DNS_SRV_SESSION;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  73 */     HostInfo srvHost = getMainHost();
/*  74 */     Map<String, String> hostProps = srvHost.getHostProperties();
/*  75 */     if ("".equals(srvHost.getHost())) {
/*  76 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.18"));
/*     */     }
/*  78 */     if (this.hosts.size() != 1) {
/*  79 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.19"));
/*     */     }
/*  81 */     if (srvHost.getPort() != -1) {
/*  82 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.22"));
/*     */     }
/*  84 */     if (hostProps.containsKey(PropertyKey.xdevapiDnsSrv.getKeyName()) && 
/*  85 */       !BooleanPropertyDefinition.booleanFrom(PropertyKey.xdevapiDnsSrv.getKeyName(), hostProps.get(PropertyKey.xdevapiDnsSrv.getKeyName()), null).booleanValue()) {
/*  86 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, 
/*  87 */           Messages.getString("ConnectionString.23", new Object[] { PropertyKey.xdevapiDnsSrv.getKeyName() }));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void preprocessPerTypeHostProperties(Map<String, String> hostProps) {
/*  94 */     if (hostProps.containsKey(PropertyKey.ADDRESS.getKeyName())) {
/*  95 */       String address = hostProps.get(PropertyKey.ADDRESS.getKeyName());
/*  96 */       ConnectionUrlParser.Pair<String, Integer> hostPortPair = ConnectionUrlParser.parseHostPortPair(address);
/*  97 */       String host = StringUtils.safeTrim((String)hostPortPair.left);
/*  98 */       Integer port = (Integer)hostPortPair.right;
/*  99 */       if (!StringUtils.isNullOrEmpty(host) && !hostProps.containsKey(PropertyKey.HOST.getKeyName())) {
/* 100 */         hostProps.put(PropertyKey.HOST.getKeyName(), host);
/*     */       }
/* 102 */       if (port.intValue() != -1 && !hostProps.containsKey(PropertyKey.PORT.getKeyName())) {
/* 103 */         hostProps.put(PropertyKey.PORT.getKeyName(), port.toString());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDefaultHost() {
/* 110 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDefaultPort() {
/* 115 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void fixProtocolDependencies(Map<String, String> hostProps) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<HostInfo> getHostsList(HostsListView view) {
/* 134 */     return getHostsListFromDnsSrv(getMainHost());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\con\\url\XDevApiDnsSrvConnectionUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */