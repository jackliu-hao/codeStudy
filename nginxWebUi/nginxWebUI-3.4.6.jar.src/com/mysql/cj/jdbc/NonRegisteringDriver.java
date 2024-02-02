/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Constants;
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.ConnectionUrl;
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.UnableToConnectException;
/*     */ import com.mysql.cj.exceptions.UnsupportedConnectionStringException;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import com.mysql.cj.jdbc.ha.FailoverConnectionProxy;
/*     */ import com.mysql.cj.jdbc.ha.LoadBalancedConnectionProxy;
/*     */ import com.mysql.cj.jdbc.ha.ReplicationConnectionProxy;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverPropertyInfo;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Logger;
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
/*     */ public class NonRegisteringDriver
/*     */   implements Driver
/*     */ {
/*     */   public static String getOSName() {
/*  83 */     return Constants.OS_NAME;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getPlatform() {
/*  93 */     return Constants.OS_ARCH;
/*     */   }
/*     */   
/*     */   static {
/*     */     try {
/*  98 */       Class.forName(AbandonedConnectionCleanupThread.class.getName());
/*  99 */     } catch (ClassNotFoundException classNotFoundException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int getMajorVersionInternal() {
/* 110 */     return StringUtils.safeIntParse("8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int getMinorVersionInternal() {
/* 119 */     return StringUtils.safeIntParse("0");
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
/*     */   public boolean acceptsURL(String url) throws SQLException {
/*     */     
/* 147 */     try { return ConnectionUrl.acceptsUrl(url); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection connect(String url, Properties info) throws SQLException {
/*     */     try {
/*     */       try {
/* 186 */         if (!ConnectionUrl.acceptsUrl(url))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 192 */           return null;
/*     */         }
/*     */         
/* 195 */         ConnectionUrl conStr = ConnectionUrl.getConnectionUrlInstance(url, info);
/* 196 */         switch (conStr.getType()) {
/*     */           case SINGLE_CONNECTION:
/* 198 */             return ConnectionImpl.getInstance(conStr.getMainHost());
/*     */           
/*     */           case FAILOVER_CONNECTION:
/*     */           case FAILOVER_DNS_SRV_CONNECTION:
/* 202 */             return FailoverConnectionProxy.createProxyInstance(conStr);
/*     */           
/*     */           case LOADBALANCE_CONNECTION:
/*     */           case LOADBALANCE_DNS_SRV_CONNECTION:
/* 206 */             return (Connection)LoadBalancedConnectionProxy.createProxyInstance(conStr);
/*     */           
/*     */           case REPLICATION_CONNECTION:
/*     */           case REPLICATION_DNS_SRV_CONNECTION:
/* 210 */             return (Connection)ReplicationConnectionProxy.createProxyInstance(conStr);
/*     */         } 
/*     */         
/* 213 */         return null;
/*     */       
/*     */       }
/* 216 */       catch (UnsupportedConnectionStringException e) {
/*     */         
/* 218 */         return null;
/*     */       }
/* 220 */       catch (CJException ex) {
/* 221 */         throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, 
/* 222 */             Messages.getString("NonRegisteringDriver.17", new Object[] { ex.toString() }), ex);
/*     */       } 
/*     */     } catch (CJException cJException) {
/*     */       throw SQLExceptionsMapping.translateException(cJException);
/*     */     } 
/*     */   } public int getMajorVersion() {
/* 228 */     return getMajorVersionInternal();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMinorVersion() {
/* 233 */     return getMinorVersionInternal();
/*     */   }
/*     */   
/*     */   public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
/*     */     
/* 238 */     try { String host = "";
/* 239 */       String port = "";
/* 240 */       String database = "";
/* 241 */       String user = "";
/* 242 */       String password = "";
/*     */       
/* 244 */       if (!StringUtils.isNullOrEmpty(url)) {
/* 245 */         ConnectionUrl connStr = ConnectionUrl.getConnectionUrlInstance(url, info);
/* 246 */         if (connStr.getType() == ConnectionUrl.Type.SINGLE_CONNECTION) {
/* 247 */           HostInfo hostInfo = connStr.getMainHost();
/* 248 */           info = hostInfo.exposeAsProperties();
/*     */         } 
/*     */       } 
/*     */       
/* 252 */       if (info != null) {
/* 253 */         host = info.getProperty(PropertyKey.HOST.getKeyName());
/* 254 */         port = info.getProperty(PropertyKey.PORT.getKeyName());
/* 255 */         database = info.getProperty(PropertyKey.DBNAME.getKeyName());
/* 256 */         user = info.getProperty(PropertyKey.USER.getKeyName());
/* 257 */         password = info.getProperty(PropertyKey.PASSWORD.getKeyName());
/*     */       } 
/*     */       
/* 260 */       DriverPropertyInfo hostProp = new DriverPropertyInfo(PropertyKey.HOST.getKeyName(), host);
/* 261 */       hostProp.required = true;
/* 262 */       hostProp.description = Messages.getString("NonRegisteringDriver.3");
/*     */       
/* 264 */       DriverPropertyInfo portProp = new DriverPropertyInfo(PropertyKey.PORT.getKeyName(), port);
/* 265 */       portProp.required = false;
/* 266 */       portProp.description = Messages.getString("NonRegisteringDriver.7");
/*     */       
/* 268 */       DriverPropertyInfo dbProp = new DriverPropertyInfo(PropertyKey.DBNAME.getKeyName(), database);
/* 269 */       dbProp.required = false;
/* 270 */       dbProp.description = Messages.getString("NonRegisteringDriver.10");
/*     */       
/* 272 */       DriverPropertyInfo userProp = new DriverPropertyInfo(PropertyKey.USER.getKeyName(), user);
/* 273 */       userProp.required = true;
/* 274 */       userProp.description = Messages.getString("NonRegisteringDriver.13");
/*     */       
/* 276 */       DriverPropertyInfo passwordProp = new DriverPropertyInfo(PropertyKey.PASSWORD.getKeyName(), password);
/* 277 */       passwordProp.required = true;
/* 278 */       passwordProp.description = Messages.getString("NonRegisteringDriver.16");
/*     */       
/* 280 */       JdbcPropertySet propSet = new JdbcPropertySetImpl();
/* 281 */       propSet.initializeProperties(info);
/* 282 */       List<DriverPropertyInfo> driverPropInfo = propSet.exposeAsDriverPropertyInfo();
/*     */       
/* 284 */       DriverPropertyInfo[] dpi = new DriverPropertyInfo[5 + driverPropInfo.size()];
/* 285 */       dpi[0] = hostProp;
/* 286 */       dpi[1] = portProp;
/* 287 */       dpi[2] = dbProp;
/* 288 */       dpi[3] = userProp;
/* 289 */       dpi[4] = passwordProp;
/* 290 */       System.arraycopy(driverPropInfo.toArray(new DriverPropertyInfo[0]), 0, dpi, 5, driverPropInfo.size());
/*     */       
/* 292 */       return dpi; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean jdbcCompliant() {
/* 299 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
/* 304 */     throw new SQLFeatureNotSupportedException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\NonRegisteringDriver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */