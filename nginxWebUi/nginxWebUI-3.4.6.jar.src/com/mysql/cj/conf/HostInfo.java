/*     */ package com.mysql.cj.conf;
/*     */ 
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
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
/*     */ public class HostInfo
/*     */   implements DatabaseUrlContainer
/*     */ {
/*     */   public static final int NO_PORT = -1;
/*     */   private static final String HOST_PORT_SEPARATOR = ":";
/*     */   private final DatabaseUrlContainer originalUrl;
/*     */   private final String host;
/*     */   private final int port;
/*     */   private final String user;
/*     */   private final String password;
/*  58 */   private final Map<String, String> hostProperties = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HostInfo() {
/*  64 */     this(null, null, -1, null, null, null);
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
/*     */   public HostInfo(DatabaseUrlContainer url, String host, int port, String user, String password) {
/*  82 */     this(url, host, port, user, password, null);
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
/*     */   public HostInfo(DatabaseUrlContainer url, String host, int port, String user, String password, Map<String, String> properties) {
/* 102 */     this.originalUrl = url;
/* 103 */     this.host = host;
/* 104 */     this.port = port;
/* 105 */     this.user = user;
/* 106 */     this.password = password;
/* 107 */     if (properties != null) {
/* 108 */       this.hostProperties.putAll(properties);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHost() {
/* 118 */     return this.host;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPort() {
/* 127 */     return this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHostPortPair() {
/* 136 */     return this.host + ":" + this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUser() {
/* 145 */     return this.user;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPassword() {
/* 154 */     return this.password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getHostProperties() {
/* 163 */     return Collections.unmodifiableMap(this.hostProperties);
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
/*     */   public String getProperty(String key) {
/* 175 */     return this.hostProperties.get(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDatabase() {
/* 184 */     String database = this.hostProperties.get(PropertyKey.DBNAME.getKeyName());
/* 185 */     return StringUtils.isNullOrEmpty(database) ? "" : database;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Properties exposeAsProperties() {
/* 195 */     Properties props = new Properties();
/* 196 */     this.hostProperties.entrySet().stream().forEach(e -> props.setProperty((String)e.getKey(), (e.getValue() == null) ? "" : (String)e.getValue()));
/* 197 */     props.setProperty(PropertyKey.HOST.getKeyName(), getHost());
/* 198 */     props.setProperty(PropertyKey.PORT.getKeyName(), String.valueOf(getPort()));
/* 199 */     if (getUser() != null) {
/* 200 */       props.setProperty(PropertyKey.USER.getKeyName(), getUser());
/*     */     }
/* 202 */     if (getPassword() != null) {
/* 203 */       props.setProperty(PropertyKey.PASSWORD.getKeyName(), getPassword());
/*     */     }
/* 205 */     return props;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDatabaseUrl() {
/* 215 */     return (this.originalUrl != null) ? this.originalUrl.getDatabaseUrl() : "";
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
/*     */   public boolean equalHostPortPair(HostInfo hi) {
/* 227 */     return (((getHost() != null && getHost().equals(hi.getHost())) || (getHost() == null && hi.getHost() == null)) && getPort() == hi.getPort());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 237 */     StringBuilder asStr = new StringBuilder(super.toString());
/* 238 */     asStr.append(String.format(" :: {host: \"%s\", port: %d, hostProperties: %s}", new Object[] { this.host, Integer.valueOf(this.port), this.hostProperties }));
/* 239 */     return asStr.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\HostInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */