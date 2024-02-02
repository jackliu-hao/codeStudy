/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.AbstractRuntimeProperty;
/*     */ import com.mysql.cj.conf.ConnectionUrl;
/*     */ import com.mysql.cj.conf.PropertyDefinitions;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Logger;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.Referenceable;
/*     */ import javax.naming.StringRefAddr;
/*     */ import javax.sql.DataSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MysqlDataSource
/*     */   extends JdbcPropertySetImpl
/*     */   implements DataSource, Referenceable, Serializable, JdbcPropertySet
/*     */ {
/*     */   static final long serialVersionUID = -5515846944416881264L;
/*     */   protected static final NonRegisteringDriver mysqlDriver;
/*     */   
/*     */   static {
/*     */     try {
/*  64 */       mysqlDriver = new NonRegisteringDriver();
/*  65 */     } catch (Exception E) {
/*  66 */       throw new RuntimeException(Messages.getString("MysqlDataSource.0"));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*  71 */   protected transient PrintWriter logWriter = null;
/*     */ 
/*     */   
/*  74 */   protected String databaseName = null;
/*     */ 
/*     */   
/*  77 */   protected String encoding = null;
/*     */ 
/*     */   
/*  80 */   protected String url = null;
/*     */ 
/*     */   
/*     */   protected boolean explicitUrl = false;
/*     */ 
/*     */   
/*  86 */   protected String hostName = null;
/*     */ 
/*     */   
/*  89 */   protected int port = 3306;
/*     */ 
/*     */   
/*     */   protected boolean explicitPort = false;
/*     */ 
/*     */   
/*  95 */   protected String user = null;
/*     */ 
/*     */   
/*  98 */   protected String password = null;
/*     */ 
/*     */   
/* 101 */   protected String profileSQLString = "false";
/*     */   
/* 103 */   protected String description = "MySQL Connector/J Data Source";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/*     */     
/* 113 */     try { return getConnection(this.user, this.password); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */   public Connection getConnection(String userID, String pass) throws SQLException {
/*     */     
/* 118 */     try { Properties props = exposeAsProperties();
/*     */       
/* 120 */       if (userID != null) {
/* 121 */         props.setProperty(PropertyKey.USER.getKeyName(), userID);
/*     */       }
/*     */       
/* 124 */       if (pass != null) {
/* 125 */         props.setProperty(PropertyKey.PASSWORD.getKeyName(), pass);
/*     */       }
/*     */       
/* 128 */       return getConnection(props); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */   public String getDescription() {
/* 132 */     return this.description;
/*     */   }
/*     */   
/*     */   public void setDescription(String value) {
/* 136 */     this.description = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDatabaseName(String dbName) {
/* 146 */     this.databaseName = dbName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDatabaseName() {
/* 155 */     return (this.databaseName != null) ? this.databaseName : "";
/*     */   }
/*     */   
/*     */   public void setLogWriter(PrintWriter output) throws SQLException {
/*     */     
/* 160 */     try { this.logWriter = output; return; }
/* 161 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   } public PrintWriter getLogWriter() {
/*     */     
/* 165 */     try { return this.logWriter; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   } public void setLoginTimeout(int seconds) throws SQLException {
/*     */     try {
/*     */       return;
/*     */     } catch (CJException cJException) {
/* 171 */       throw SQLExceptionsMapping.translateException(cJException);
/*     */     } 
/*     */   } public int getLoginTimeout() {
/*     */     
/* 175 */     try { return 0; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPassword(String pass) {
/* 185 */     this.password = pass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPassword() {
/* 194 */     return this.password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPort(int p) {
/* 204 */     this.port = p;
/* 205 */     this.explicitPort = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPort() {
/* 214 */     return this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPortNumber(int p) {
/* 224 */     setPort(p);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPortNumber() {
/* 233 */     return getPort();
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
/*     */   public void setPropertiesViaRef(Reference ref) throws SQLException {
/* 247 */     for (PropertyKey propKey : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet()) {
/* 248 */       RuntimeProperty<?> propToSet = getProperty(propKey);
/*     */       
/* 250 */       if (ref != null) {
/* 251 */         propToSet.initializeFrom(ref, null);
/*     */       }
/*     */     } 
/*     */     
/* 255 */     postInitialization();
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
/*     */   public Reference getReference() throws NamingException {
/* 268 */     String factoryName = MysqlDataSourceFactory.class.getName();
/* 269 */     Reference ref = new Reference(getClass().getName(), factoryName, null);
/* 270 */     ref.add(new StringRefAddr(PropertyKey.USER.getKeyName(), getUser()));
/* 271 */     ref.add(new StringRefAddr(PropertyKey.PASSWORD.getKeyName(), this.password));
/* 272 */     ref.add(new StringRefAddr("serverName", getServerName()));
/* 273 */     ref.add(new StringRefAddr("port", "" + getPort()));
/* 274 */     ref.add(new StringRefAddr("explicitPort", String.valueOf(this.explicitPort)));
/* 275 */     ref.add(new StringRefAddr("databaseName", getDatabaseName()));
/* 276 */     ref.add(new StringRefAddr("url", getUrl()));
/* 277 */     ref.add(new StringRefAddr("explicitUrl", String.valueOf(this.explicitUrl)));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 282 */     for (PropertyKey propKey : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet()) {
/* 283 */       RuntimeProperty<?> propToStore = getProperty(propKey);
/*     */       
/* 285 */       String val = propToStore.getStringValue();
/* 286 */       if (val != null) {
/* 287 */         ref.add(new StringRefAddr(propToStore.getPropertyDefinition().getName(), val));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 292 */     return ref;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setServerName(String serverName) {
/* 302 */     this.hostName = serverName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getServerName() {
/* 311 */     return (this.hostName != null) ? this.hostName : "";
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
/*     */   public void setURL(String url) {
/* 325 */     setUrl(url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getURL() {
/* 334 */     return getUrl();
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
/*     */   public void setUrl(String url) {
/* 346 */     this.url = url;
/* 347 */     this.explicitUrl = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUrl() {
/* 356 */     if (!this.explicitUrl) {
/* 357 */       StringBuilder sbUrl = new StringBuilder(ConnectionUrl.Type.SINGLE_CONNECTION.getScheme());
/* 358 */       sbUrl.append("//").append(getServerName());
/*     */       try {
/* 360 */         if (this.explicitPort || !getBooleanRuntimeProperty(PropertyKey.dnsSrv.getKeyName())) {
/* 361 */           sbUrl.append(":").append(getPort());
/*     */         }
/* 363 */       } catch (SQLException e) {
/*     */         
/* 365 */         sbUrl.append(":").append(getPort());
/*     */       } 
/* 367 */       sbUrl.append("/").append(getDatabaseName());
/* 368 */       return sbUrl.toString();
/*     */     } 
/* 370 */     return this.url;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUser(String userID) {
/* 380 */     this.user = userID;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUser() {
/* 389 */     return this.user;
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
/*     */   protected Connection getConnection(Properties props) throws SQLException {
/* 404 */     String jdbcUrlToUse = this.explicitUrl ? this.url : getUrl();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 409 */     ConnectionUrl connUrl = ConnectionUrl.getConnectionUrlInstance(jdbcUrlToUse, null);
/* 410 */     Properties urlProps = connUrl.getConnectionArgumentsAsProperties();
/* 411 */     urlProps.remove(PropertyKey.HOST.getKeyName());
/* 412 */     urlProps.remove(PropertyKey.PORT.getKeyName());
/* 413 */     urlProps.remove(PropertyKey.DBNAME.getKeyName());
/* 414 */     urlProps.stringPropertyNames().stream().forEach(k -> props.setProperty(k, urlProps.getProperty(k)));
/*     */     
/* 416 */     return mysqlDriver.connect(jdbcUrlToUse, props);
/*     */   }
/*     */ 
/*     */   
/*     */   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
/* 421 */     return null;
/*     */   }
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*     */     
/* 426 */     try { return null; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*     */     
/* 431 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
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
/*     */   protected String getStringRuntimeProperty(String name) throws SQLException {
/*     */     
/* 444 */     try { return (String)getStringProperty(name).getValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
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
/*     */   protected void setStringRuntimeProperty(String name, String value) throws SQLException {
/*     */     
/* 458 */     try { ((AbstractRuntimeProperty)getStringProperty(name)).setValueInternal(value, null, null); return; }
/* 459 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean getBooleanRuntimeProperty(String name) throws SQLException {
/*     */     
/* 471 */     try { return ((Boolean)getBooleanProperty(name).getValue()).booleanValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
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
/*     */   protected void setBooleanRuntimeProperty(String name, boolean value) throws SQLException {
/*     */     
/* 485 */     try { ((AbstractRuntimeProperty)getBooleanProperty(name)).setValueInternal(Boolean.valueOf(value), null, null); return; }
/* 486 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getIntegerRuntimeProperty(String name) throws SQLException {
/*     */     
/* 498 */     try { return ((Integer)getIntegerProperty(name).getValue()).intValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
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
/*     */   protected void setIntegerRuntimeProperty(String name, int value) throws SQLException {
/*     */     
/* 512 */     try { ((AbstractRuntimeProperty)getIntegerProperty(name)).setValueInternal(Integer.valueOf(value), null, null); return; }
/* 513 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long getLongRuntimeProperty(String name) throws SQLException {
/*     */     
/* 525 */     try { return ((Long)getLongProperty(name).getValue()).longValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
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
/*     */   protected void setLongRuntimeProperty(String name, long value) throws SQLException {
/*     */     
/* 539 */     try { ((AbstractRuntimeProperty)getLongProperty(name)).setValueInternal(Long.valueOf(value), null, null); return; }
/* 540 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getMemorySizeRuntimeProperty(String name) throws SQLException {
/*     */     
/* 552 */     try { return ((Integer)getMemorySizeProperty(name).getValue()).intValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
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
/*     */   protected void setMemorySizeRuntimeProperty(String name, int value) throws SQLException {
/*     */     
/* 566 */     try { ((AbstractRuntimeProperty)getMemorySizeProperty(name)).setValueInternal(Integer.valueOf(value), null, null); return; }
/* 567 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getEnumRuntimeProperty(String name) throws SQLException {
/*     */     
/* 579 */     try { return getEnumProperty(name).getStringValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
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
/*     */   protected void setEnumRuntimeProperty(String name, String value) throws SQLException {
/*     */     
/* 593 */     try { ((AbstractRuntimeProperty)getEnumProperty(name)).setValueInternal(value, null); return; }
/* 594 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */   public Properties exposeAsProperties() {
/* 598 */     Properties props = new Properties();
/*     */     
/* 600 */     for (PropertyKey propKey : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet()) {
/* 601 */       RuntimeProperty<?> propToGet = getProperty(propKey);
/*     */       
/* 603 */       String propValue = propToGet.getStringValue();
/*     */       
/* 605 */       if (propValue != null && propToGet.isExplicitlySet()) {
/* 606 */         props.setProperty(propToGet.getPropertyDefinition().getName(), propValue);
/*     */       }
/*     */     } 
/*     */     
/* 610 */     return props;
/*     */   }
/*     */   
/*     */   public String getConnectionLifecycleInterceptors() throws SQLException {
/*     */     return getStringRuntimeProperty("connectionLifecycleInterceptors");
/*     */   }
/*     */   
/*     */   public void setConnectionLifecycleInterceptors(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("connectionLifecycleInterceptors", paramString);
/*     */   }
/*     */   
/*     */   public String getSocksProxyHost() throws SQLException {
/*     */     return getStringRuntimeProperty("socksProxyHost");
/*     */   }
/*     */   
/*     */   public void setSocksProxyHost(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("socksProxyHost", paramString);
/*     */   }
/*     */   
/*     */   public String getResourceId() throws SQLException {
/*     */     return getStringRuntimeProperty("resourceId");
/*     */   }
/*     */   
/*     */   public void setResourceId(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("resourceId", paramString);
/*     */   }
/*     */   
/*     */   public int getLoadBalanceBlacklistTimeout() throws SQLException {
/*     */     return getIntegerRuntimeProperty("loadBalanceBlacklistTimeout");
/*     */   }
/*     */   
/*     */   public void setLoadBalanceBlacklistTimeout(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("loadBalanceBlacklistTimeout", paramInt);
/*     */   }
/*     */   
/*     */   public boolean getEnablePacketDebug() throws SQLException {
/*     */     return getBooleanRuntimeProperty("enablePacketDebug");
/*     */   }
/*     */   
/*     */   public void setEnablePacketDebug(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("enablePacketDebug", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getServerRSAPublicKeyFile() throws SQLException {
/*     */     return getStringRuntimeProperty("serverRSAPublicKeyFile");
/*     */   }
/*     */   
/*     */   public void setServerRSAPublicKeyFile(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("serverRSAPublicKeyFile", paramString);
/*     */   }
/*     */   
/*     */   public int getPrepStmtCacheSqlLimit() throws SQLException {
/*     */     return getIntegerRuntimeProperty("prepStmtCacheSqlLimit");
/*     */   }
/*     */   
/*     */   public void setPrepStmtCacheSqlLimit(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("prepStmtCacheSqlLimit", paramInt);
/*     */   }
/*     */   
/*     */   public String getSocketFactory() throws SQLException {
/*     */     return getStringRuntimeProperty("socketFactory");
/*     */   }
/*     */   
/*     */   public void setSocketFactory(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("socketFactory", paramString);
/*     */   }
/*     */   
/*     */   public String getLoadBalanceConnectionGroup() throws SQLException {
/*     */     return getStringRuntimeProperty("loadBalanceConnectionGroup");
/*     */   }
/*     */   
/*     */   public void setLoadBalanceConnectionGroup(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("loadBalanceConnectionGroup", paramString);
/*     */   }
/*     */   
/*     */   public int getNetTimeoutForStreamingResults() throws SQLException {
/*     */     return getIntegerRuntimeProperty("netTimeoutForStreamingResults");
/*     */   }
/*     */   
/*     */   public void setNetTimeoutForStreamingResults(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("netTimeoutForStreamingResults", paramInt);
/*     */   }
/*     */   
/*     */   public String getConnectionAttributes() throws SQLException {
/*     */     return getStringRuntimeProperty("connectionAttributes");
/*     */   }
/*     */   
/*     */   public void setConnectionAttributes(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("connectionAttributes", paramString);
/*     */   }
/*     */   
/*     */   public boolean getCompensateOnDuplicateKeyUpdateCounts() throws SQLException {
/*     */     return getBooleanRuntimeProperty("compensateOnDuplicateKeyUpdateCounts");
/*     */   }
/*     */   
/*     */   public void setCompensateOnDuplicateKeyUpdateCounts(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("compensateOnDuplicateKeyUpdateCounts", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getDisconnectOnExpiredPasswords() throws SQLException {
/*     */     return getBooleanRuntimeProperty("disconnectOnExpiredPasswords");
/*     */   }
/*     */   
/*     */   public void setDisconnectOnExpiredPasswords(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("disconnectOnExpiredPasswords", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getSelfDestructOnPingSecondsLifetime() throws SQLException {
/*     */     return getIntegerRuntimeProperty("selfDestructOnPingSecondsLifetime");
/*     */   }
/*     */   
/*     */   public void setSelfDestructOnPingSecondsLifetime(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("selfDestructOnPingSecondsLifetime", paramInt);
/*     */   }
/*     */   
/*     */   public String getPasswordCharacterEncoding() throws SQLException {
/*     */     return getStringRuntimeProperty("passwordCharacterEncoding");
/*     */   }
/*     */   
/*     */   public void setPasswordCharacterEncoding(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("passwordCharacterEncoding", paramString);
/*     */   }
/*     */   
/*     */   public boolean getYearIsDateType() throws SQLException {
/*     */     return getBooleanRuntimeProperty("yearIsDateType");
/*     */   }
/*     */   
/*     */   public void setYearIsDateType(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("yearIsDateType", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getDontCheckOnDuplicateKeyUpdateInSQL() throws SQLException {
/*     */     return getBooleanRuntimeProperty("dontCheckOnDuplicateKeyUpdateInSQL");
/*     */   }
/*     */   
/*     */   public void setDontCheckOnDuplicateKeyUpdateInSQL(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("dontCheckOnDuplicateKeyUpdateInSQL", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getCharacterSetResults() throws SQLException {
/*     */     return getStringRuntimeProperty("characterSetResults");
/*     */   }
/*     */   
/*     */   public void setCharacterSetResults(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("characterSetResults", paramString);
/*     */   }
/*     */   
/*     */   public String getLocalSocketAddress() throws SQLException {
/*     */     return getStringRuntimeProperty("localSocketAddress");
/*     */   }
/*     */   
/*     */   public void setLocalSocketAddress(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("localSocketAddress", paramString);
/*     */   }
/*     */   
/*     */   public boolean getReconnectAtTxEnd() throws SQLException {
/*     */     return getBooleanRuntimeProperty("reconnectAtTxEnd");
/*     */   }
/*     */   
/*     */   public void setReconnectAtTxEnd(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("reconnectAtTxEnd", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getDatabaseTerm() throws SQLException {
/*     */     return getEnumRuntimeProperty("databaseTerm");
/*     */   }
/*     */   
/*     */   public void setDatabaseTerm(String paramString) throws SQLException {
/*     */     setEnumRuntimeProperty("databaseTerm", paramString);
/*     */   }
/*     */   
/*     */   public boolean getCacheDefaultTimezone() throws SQLException {
/*     */     return getBooleanRuntimeProperty("cacheDefaultTimezone");
/*     */   }
/*     */   
/*     */   public void setCacheDefaultTimezone(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("cacheDefaultTimezone", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getMaxQuerySizeToLog() throws SQLException {
/*     */     return getIntegerRuntimeProperty("maxQuerySizeToLog");
/*     */   }
/*     */   
/*     */   public void setMaxQuerySizeToLog(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("maxQuerySizeToLog", paramInt);
/*     */   }
/*     */   
/*     */   public boolean getMaintainTimeStats() throws SQLException {
/*     */     return getBooleanRuntimeProperty("maintainTimeStats");
/*     */   }
/*     */   
/*     */   public void setMaintainTimeStats(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("maintainTimeStats", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getDnsSrv() throws SQLException {
/*     */     return getBooleanRuntimeProperty("dnsSrv");
/*     */   }
/*     */   
/*     */   public void setDnsSrv(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("dnsSrv", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getTransformedBitIsBoolean() throws SQLException {
/*     */     return getBooleanRuntimeProperty("transformedBitIsBoolean");
/*     */   }
/*     */   
/*     */   public void setTransformedBitIsBoolean(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("transformedBitIsBoolean", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getIncludeThreadNamesAsStatementComment() throws SQLException {
/*     */     return getBooleanRuntimeProperty("includeThreadNamesAsStatementComment");
/*     */   }
/*     */   
/*     */   public void setIncludeThreadNamesAsStatementComment(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("includeThreadNamesAsStatementComment", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getTrustCertificateKeyStoreType() throws SQLException {
/*     */     return getStringRuntimeProperty("trustCertificateKeyStoreType");
/*     */   }
/*     */   
/*     */   public void setTrustCertificateKeyStoreType(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("trustCertificateKeyStoreType", paramString);
/*     */   }
/*     */   
/*     */   public boolean getUseInformationSchema() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useInformationSchema");
/*     */   }
/*     */   
/*     */   public void setUseInformationSchema(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useInformationSchema", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getPadCharsWithSpace() throws SQLException {
/*     */     return getBooleanRuntimeProperty("padCharsWithSpace");
/*     */   }
/*     */   
/*     */   public void setPadCharsWithSpace(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("padCharsWithSpace", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getUseOldAliasMetadataBehavior() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useOldAliasMetadataBehavior");
/*     */   }
/*     */   
/*     */   public void setUseOldAliasMetadataBehavior(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useOldAliasMetadataBehavior", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getEnabledSSLCipherSuites() throws SQLException {
/*     */     return getStringRuntimeProperty("enabledSSLCipherSuites");
/*     */   }
/*     */   
/*     */   public void setEnabledSSLCipherSuites(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("enabledSSLCipherSuites", paramString);
/*     */   }
/*     */   
/*     */   public boolean getUseHostsInPrivileges() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useHostsInPrivileges");
/*     */   }
/*     */   
/*     */   public void setUseHostsInPrivileges(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useHostsInPrivileges", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getClobCharacterEncoding() throws SQLException {
/*     */     return getStringRuntimeProperty("clobCharacterEncoding");
/*     */   }
/*     */   
/*     */   public void setClobCharacterEncoding(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("clobCharacterEncoding", paramString);
/*     */   }
/*     */   
/*     */   public String getAuthenticationPlugins() throws SQLException {
/*     */     return getStringRuntimeProperty("authenticationPlugins");
/*     */   }
/*     */   
/*     */   public void setAuthenticationPlugins(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("authenticationPlugins", paramString);
/*     */   }
/*     */   
/*     */   public boolean getReadOnlyPropagatesToServer() throws SQLException {
/*     */     return getBooleanRuntimeProperty("readOnlyPropagatesToServer");
/*     */   }
/*     */   
/*     */   public void setReadOnlyPropagatesToServer(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("readOnlyPropagatesToServer", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getPreserveInstants() throws SQLException {
/*     */     return getBooleanRuntimeProperty("preserveInstants");
/*     */   }
/*     */   
/*     */   public void setPreserveInstants(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("preserveInstants", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getUseOnlyServerErrorMessages() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useOnlyServerErrorMessages");
/*     */   }
/*     */   
/*     */   public void setUseOnlyServerErrorMessages(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useOnlyServerErrorMessages", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getCacheCallableStmts() throws SQLException {
/*     */     return getBooleanRuntimeProperty("cacheCallableStmts");
/*     */   }
/*     */   
/*     */   public void setCacheCallableStmts(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("cacheCallableStmts", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getFailOverReadOnly() throws SQLException {
/*     */     return getBooleanRuntimeProperty("failOverReadOnly");
/*     */   }
/*     */   
/*     */   public void setFailOverReadOnly(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("failOverReadOnly", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getCacheResultSetMetadata() throws SQLException {
/*     */     return getBooleanRuntimeProperty("cacheResultSetMetadata");
/*     */   }
/*     */   
/*     */   public void setCacheResultSetMetadata(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("cacheResultSetMetadata", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getVerifyServerCertificate() throws SQLException {
/*     */     return getBooleanRuntimeProperty("verifyServerCertificate");
/*     */   }
/*     */   
/*     */   public void setVerifyServerCertificate(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("verifyServerCertificate", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getSessionVariables() throws SQLException {
/*     */     return getStringRuntimeProperty("sessionVariables");
/*     */   }
/*     */   
/*     */   public void setSessionVariables(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("sessionVariables", paramString);
/*     */   }
/*     */   
/*     */   public String getCustomCharsetMapping() throws SQLException {
/*     */     return getStringRuntimeProperty("customCharsetMapping");
/*     */   }
/*     */   
/*     */   public void setCustomCharsetMapping(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("customCharsetMapping", paramString);
/*     */   }
/*     */   
/*     */   public String getConnectionCollation() throws SQLException {
/*     */     return getStringRuntimeProperty("connectionCollation");
/*     */   }
/*     */   
/*     */   public void setConnectionCollation(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("connectionCollation", paramString);
/*     */   }
/*     */   
/*     */   public String getClientCertificateKeyStoreUrl() throws SQLException {
/*     */     return getStringRuntimeProperty("clientCertificateKeyStoreUrl");
/*     */   }
/*     */   
/*     */   public void setClientCertificateKeyStoreUrl(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("clientCertificateKeyStoreUrl", paramString);
/*     */   }
/*     */   
/*     */   public boolean getFallbackToSystemKeyStore() throws SQLException {
/*     */     return getBooleanRuntimeProperty("fallbackToSystemKeyStore");
/*     */   }
/*     */   
/*     */   public void setFallbackToSystemKeyStore(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("fallbackToSystemKeyStore", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getLoadBalanceAutoCommitStatementThreshold() throws SQLException {
/*     */     return getIntegerRuntimeProperty("loadBalanceAutoCommitStatementThreshold");
/*     */   }
/*     */   
/*     */   public void setLoadBalanceAutoCommitStatementThreshold(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("loadBalanceAutoCommitStatementThreshold", paramInt);
/*     */   }
/*     */   
/*     */   public int getSlowQueryThresholdMillis() throws SQLException {
/*     */     return getIntegerRuntimeProperty("slowQueryThresholdMillis");
/*     */   }
/*     */   
/*     */   public void setSlowQueryThresholdMillis(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("slowQueryThresholdMillis", paramInt);
/*     */   }
/*     */   
/*     */   public String getProfilerEventHandler() throws SQLException {
/*     */     return getStringRuntimeProperty("profilerEventHandler");
/*     */   }
/*     */   
/*     */   public void setProfilerEventHandler(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("profilerEventHandler", paramString);
/*     */   }
/*     */   
/*     */   public boolean getTreatUtilDateAsTimestamp() throws SQLException {
/*     */     return getBooleanRuntimeProperty("treatUtilDateAsTimestamp");
/*     */   }
/*     */   
/*     */   public void setTreatUtilDateAsTimestamp(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("treatUtilDateAsTimestamp", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getHaEnableJMX() throws SQLException {
/*     */     return getBooleanRuntimeProperty("haEnableJMX");
/*     */   }
/*     */   
/*     */   public void setHaEnableJMX(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("haEnableJMX", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getZeroDateTimeBehavior() throws SQLException {
/*     */     return getEnumRuntimeProperty("zeroDateTimeBehavior");
/*     */   }
/*     */   
/*     */   public void setZeroDateTimeBehavior(String paramString) throws SQLException {
/*     */     setEnumRuntimeProperty("zeroDateTimeBehavior", paramString);
/*     */   }
/*     */   
/*     */   public boolean getEnableQueryTimeouts() throws SQLException {
/*     */     return getBooleanRuntimeProperty("enableQueryTimeouts");
/*     */   }
/*     */   
/*     */   public void setEnableQueryTimeouts(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("enableQueryTimeouts", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getIncludeInnodbStatusInDeadlockExceptions() throws SQLException {
/*     */     return getBooleanRuntimeProperty("includeInnodbStatusInDeadlockExceptions");
/*     */   }
/*     */   
/*     */   public void setIncludeInnodbStatusInDeadlockExceptions(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("includeInnodbStatusInDeadlockExceptions", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getTrackSessionState() throws SQLException {
/*     */     return getBooleanRuntimeProperty("trackSessionState");
/*     */   }
/*     */   
/*     */   public void setTrackSessionState(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("trackSessionState", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getSecondsBeforeRetryMaster() throws SQLException {
/*     */     return getIntegerRuntimeProperty("secondsBeforeRetryMaster");
/*     */   }
/*     */   
/*     */   public void setSecondsBeforeRetryMaster(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("secondsBeforeRetryMaster", paramInt);
/*     */   }
/*     */   
/*     */   public String getServerAffinityOrder() throws SQLException {
/*     */     return getStringRuntimeProperty("serverAffinityOrder");
/*     */   }
/*     */   
/*     */   public void setServerAffinityOrder(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("serverAffinityOrder", paramString);
/*     */   }
/*     */   
/*     */   public boolean getUseColumnNamesInFindColumn() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useColumnNamesInFindColumn");
/*     */   }
/*     */   
/*     */   public void setUseColumnNamesInFindColumn(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useColumnNamesInFindColumn", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getSendFractionalSecondsForTime() throws SQLException {
/*     */     return getBooleanRuntimeProperty("sendFractionalSecondsForTime");
/*     */   }
/*     */   
/*     */   public void setSendFractionalSecondsForTime(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("sendFractionalSecondsForTime", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getForceConnectionTimeZoneToSession() throws SQLException {
/*     */     return getBooleanRuntimeProperty("forceConnectionTimeZoneToSession");
/*     */   }
/*     */   
/*     */   public void setForceConnectionTimeZoneToSession(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("forceConnectionTimeZoneToSession", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getServerTimezone() throws SQLException {
/*     */     return getStringRuntimeProperty("serverTimezone");
/*     */   }
/*     */   
/*     */   public void setServerTimezone(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("serverTimezone", paramString);
/*     */   }
/*     */   
/*     */   public boolean getOverrideSupportsIntegrityEnhancementFacility() throws SQLException {
/*     */     return getBooleanRuntimeProperty("overrideSupportsIntegrityEnhancementFacility");
/*     */   }
/*     */   
/*     */   public void setOverrideSupportsIntegrityEnhancementFacility(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("overrideSupportsIntegrityEnhancementFacility", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getLoadBalancePingTimeout() throws SQLException {
/*     */     return getIntegerRuntimeProperty("loadBalancePingTimeout");
/*     */   }
/*     */   
/*     */   public void setLoadBalancePingTimeout(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("loadBalancePingTimeout", paramInt);
/*     */   }
/*     */   
/*     */   public String getLoadBalanceExceptionChecker() throws SQLException {
/*     */     return getStringRuntimeProperty("loadBalanceExceptionChecker");
/*     */   }
/*     */   
/*     */   public void setLoadBalanceExceptionChecker(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("loadBalanceExceptionChecker", paramString);
/*     */   }
/*     */   
/*     */   public boolean getInteractiveClient() throws SQLException {
/*     */     return getBooleanRuntimeProperty("interactiveClient");
/*     */   }
/*     */   
/*     */   public void setInteractiveClient(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("interactiveClient", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getConnectTimeout() throws SQLException {
/*     */     return getIntegerRuntimeProperty("connectTimeout");
/*     */   }
/*     */   
/*     */   public void setConnectTimeout(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("connectTimeout", paramInt);
/*     */   }
/*     */   
/*     */   public String getPassword1() throws SQLException {
/*     */     return getStringRuntimeProperty("password1");
/*     */   }
/*     */   
/*     */   public void setPassword1(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("password1", paramString);
/*     */   }
/*     */   
/*     */   public boolean getProfileSQL() throws SQLException {
/*     */     return getBooleanRuntimeProperty("profileSQL");
/*     */   }
/*     */   
/*     */   public void setProfileSQL(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("profileSQL", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getPassword2() throws SQLException {
/*     */     return getStringRuntimeProperty("password2");
/*     */   }
/*     */   
/*     */   public void setPassword2(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("password2", paramString);
/*     */   }
/*     */   
/*     */   public boolean getClobberStreamingResults() throws SQLException {
/*     */     return getBooleanRuntimeProperty("clobberStreamingResults");
/*     */   }
/*     */   
/*     */   public void setClobberStreamingResults(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("clobberStreamingResults", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getTcpSndBuf() throws SQLException {
/*     */     return getIntegerRuntimeProperty("tcpSndBuf");
/*     */   }
/*     */   
/*     */   public void setTcpSndBuf(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("tcpSndBuf", paramInt);
/*     */   }
/*     */   
/*     */   public boolean getAllowNanAndInf() throws SQLException {
/*     */     return getBooleanRuntimeProperty("allowNanAndInf");
/*     */   }
/*     */   
/*     */   public void setAllowNanAndInf(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("allowNanAndInf", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getAllowLoadLocalInfile() throws SQLException {
/*     */     return getBooleanRuntimeProperty("allowLoadLocalInfile");
/*     */   }
/*     */   
/*     */   public void setAllowLoadLocalInfile(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("allowLoadLocalInfile", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getTcpNoDelay() throws SQLException {
/*     */     return getBooleanRuntimeProperty("tcpNoDelay");
/*     */   }
/*     */   
/*     */   public void setTcpNoDelay(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("tcpNoDelay", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getExplainSlowQueries() throws SQLException {
/*     */     return getBooleanRuntimeProperty("explainSlowQueries");
/*     */   }
/*     */   
/*     */   public void setExplainSlowQueries(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("explainSlowQueries", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getAllowMultiQueries() throws SQLException {
/*     */     return getBooleanRuntimeProperty("allowMultiQueries");
/*     */   }
/*     */   
/*     */   public void setAllowMultiQueries(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("allowMultiQueries", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getAutoReconnectForPools() throws SQLException {
/*     */     return getBooleanRuntimeProperty("autoReconnectForPools");
/*     */   }
/*     */   
/*     */   public void setAutoReconnectForPools(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("autoReconnectForPools", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getAutoGenerateTestcaseScript() throws SQLException {
/*     */     return getBooleanRuntimeProperty("autoGenerateTestcaseScript");
/*     */   }
/*     */   
/*     */   public void setAutoGenerateTestcaseScript(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("autoGenerateTestcaseScript", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getUseUnbufferedInput() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useUnbufferedInput");
/*     */   }
/*     */   
/*     */   public void setUseUnbufferedInput(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useUnbufferedInput", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getCallableStmtCacheSize() throws SQLException {
/*     */     return getIntegerRuntimeProperty("callableStmtCacheSize");
/*     */   }
/*     */   
/*     */   public void setCallableStmtCacheSize(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("callableStmtCacheSize", paramInt);
/*     */   }
/*     */   
/*     */   public int getLocatorFetchBufferSize() throws SQLException {
/*     */     return getIntegerRuntimeProperty("locatorFetchBufferSize");
/*     */   }
/*     */   
/*     */   public void setLocatorFetchBufferSize(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("locatorFetchBufferSize", paramInt);
/*     */   }
/*     */   
/*     */   public boolean getCacheServerConfiguration() throws SQLException {
/*     */     return getBooleanRuntimeProperty("cacheServerConfiguration");
/*     */   }
/*     */   
/*     */   public void setCacheServerConfiguration(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("cacheServerConfiguration", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getMetadataCacheSize() throws SQLException {
/*     */     return getIntegerRuntimeProperty("metadataCacheSize");
/*     */   }
/*     */   
/*     */   public void setMetadataCacheSize(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("metadataCacheSize", paramInt);
/*     */   }
/*     */   
/*     */   public boolean getUseSSL() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useSSL");
/*     */   }
/*     */   
/*     */   public void setUseSSL(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useSSL", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getContinueBatchOnError() throws SQLException {
/*     */     return getBooleanRuntimeProperty("continueBatchOnError");
/*     */   }
/*     */   
/*     */   public void setContinueBatchOnError(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("continueBatchOnError", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getUseCursorFetch() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useCursorFetch");
/*     */   }
/*     */   
/*     */   public void setUseCursorFetch(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useCursorFetch", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getGetProceduresReturnsFunctions() throws SQLException {
/*     */     return getBooleanRuntimeProperty("getProceduresReturnsFunctions");
/*     */   }
/*     */   
/*     */   public void setGetProceduresReturnsFunctions(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("getProceduresReturnsFunctions", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getNullCatalogMeansCurrent() throws SQLException {
/*     */     return getBooleanRuntimeProperty("nullCatalogMeansCurrent");
/*     */   }
/*     */   
/*     */   public void setNullCatalogMeansCurrent(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("nullCatalogMeansCurrent", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getEmptyStringsConvertToZero() throws SQLException {
/*     */     return getBooleanRuntimeProperty("emptyStringsConvertToZero");
/*     */   }
/*     */   
/*     */   public void setEmptyStringsConvertToZero(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("emptyStringsConvertToZero", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getSslMode() throws SQLException {
/*     */     return getEnumRuntimeProperty("sslMode");
/*     */   }
/*     */   
/*     */   public void setSslMode(String paramString) throws SQLException {
/*     */     setEnumRuntimeProperty("sslMode", paramString);
/*     */   }
/*     */   
/*     */   public boolean getCreateDatabaseIfNotExist() throws SQLException {
/*     */     return getBooleanRuntimeProperty("createDatabaseIfNotExist");
/*     */   }
/*     */   
/*     */   public void setCreateDatabaseIfNotExist(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("createDatabaseIfNotExist", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getSocketTimeout() throws SQLException {
/*     */     return getIntegerRuntimeProperty("socketTimeout");
/*     */   }
/*     */   
/*     */   public void setSocketTimeout(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("socketTimeout", paramInt);
/*     */   }
/*     */   
/*     */   public String getClientCertificateKeyStorePassword() throws SQLException {
/*     */     return getStringRuntimeProperty("clientCertificateKeyStorePassword");
/*     */   }
/*     */   
/*     */   public void setClientCertificateKeyStorePassword(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("clientCertificateKeyStorePassword", paramString);
/*     */   }
/*     */   
/*     */   public boolean getFallbackToSystemTrustStore() throws SQLException {
/*     */     return getBooleanRuntimeProperty("fallbackToSystemTrustStore");
/*     */   }
/*     */   
/*     */   public void setFallbackToSystemTrustStore(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("fallbackToSystemTrustStore", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getAlwaysSendSetIsolation() throws SQLException {
/*     */     return getBooleanRuntimeProperty("alwaysSendSetIsolation");
/*     */   }
/*     */   
/*     */   public void setAlwaysSendSetIsolation(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("alwaysSendSetIsolation", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getDetectCustomCollations() throws SQLException {
/*     */     return getBooleanRuntimeProperty("detectCustomCollations");
/*     */   }
/*     */   
/*     */   public void setDetectCustomCollations(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("detectCustomCollations", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getPropertiesTransform() throws SQLException {
/*     */     return getStringRuntimeProperty("propertiesTransform");
/*     */   }
/*     */   
/*     */   public void setPropertiesTransform(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("propertiesTransform", paramString);
/*     */   }
/*     */   
/*     */   public boolean getUseServerPrepStmts() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useServerPrepStmts");
/*     */   }
/*     */   
/*     */   public void setUseServerPrepStmts(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useServerPrepStmts", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getTinyInt1isBit() throws SQLException {
/*     */     return getBooleanRuntimeProperty("tinyInt1isBit");
/*     */   }
/*     */   
/*     */   public void setTinyInt1isBit(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("tinyInt1isBit", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getAllowLoadLocalInfileInPath() throws SQLException {
/*     */     return getStringRuntimeProperty("allowLoadLocalInfileInPath");
/*     */   }
/*     */   
/*     */   public void setAllowLoadLocalInfileInPath(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("allowLoadLocalInfileInPath", paramString);
/*     */   }
/*     */   
/*     */   public boolean getUseUsageAdvisor() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useUsageAdvisor");
/*     */   }
/*     */   
/*     */   public void setUseUsageAdvisor(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useUsageAdvisor", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getAllowSlaveDownConnections() throws SQLException {
/*     */     return getBooleanRuntimeProperty("allowSlaveDownConnections");
/*     */   }
/*     */   
/*     */   public void setAllowSlaveDownConnections(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("allowSlaveDownConnections", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getCharacterEncoding() throws SQLException {
/*     */     return getStringRuntimeProperty("characterEncoding");
/*     */   }
/*     */   
/*     */   public void setCharacterEncoding(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("characterEncoding", paramString);
/*     */   }
/*     */   
/*     */   public boolean getAllowPublicKeyRetrieval() throws SQLException {
/*     */     return getBooleanRuntimeProperty("allowPublicKeyRetrieval");
/*     */   }
/*     */   
/*     */   public void setAllowPublicKeyRetrieval(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("allowPublicKeyRetrieval", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getRequireSSL() throws SQLException {
/*     */     return getBooleanRuntimeProperty("requireSSL");
/*     */   }
/*     */   
/*     */   public void setRequireSSL(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("requireSSL", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getMaxAllowedPacket() throws SQLException {
/*     */     return getIntegerRuntimeProperty("maxAllowedPacket");
/*     */   }
/*     */   
/*     */   public void setMaxAllowedPacket(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("maxAllowedPacket", paramInt);
/*     */   }
/*     */   
/*     */   public boolean getTraceProtocol() throws SQLException {
/*     */     return getBooleanRuntimeProperty("traceProtocol");
/*     */   }
/*     */   
/*     */   public void setTraceProtocol(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("traceProtocol", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getJdbcCompliantTruncation() throws SQLException {
/*     */     return getBooleanRuntimeProperty("jdbcCompliantTruncation");
/*     */   }
/*     */   
/*     */   public void setJdbcCompliantTruncation(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("jdbcCompliantTruncation", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getCachePrepStmts() throws SQLException {
/*     */     return getBooleanRuntimeProperty("cachePrepStmts");
/*     */   }
/*     */   
/*     */   public void setCachePrepStmts(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("cachePrepStmts", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getStrictUpdates() throws SQLException {
/*     */     return getBooleanRuntimeProperty("strictUpdates");
/*     */   }
/*     */   
/*     */   public void setStrictUpdates(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("strictUpdates", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getRewriteBatchedStatements() throws SQLException {
/*     */     return getBooleanRuntimeProperty("rewriteBatchedStatements");
/*     */   }
/*     */   
/*     */   public void setRewriteBatchedStatements(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("rewriteBatchedStatements", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getAllowMasterDownConnections() throws SQLException {
/*     */     return getBooleanRuntimeProperty("allowMasterDownConnections");
/*     */   }
/*     */   
/*     */   public void setAllowMasterDownConnections(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("allowMasterDownConnections", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getAutoSlowLog() throws SQLException {
/*     */     return getBooleanRuntimeProperty("autoSlowLog");
/*     */   }
/*     */   
/*     */   public void setAutoSlowLog(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("autoSlowLog", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getUseLocalSessionState() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useLocalSessionState");
/*     */   }
/*     */   
/*     */   public void setUseLocalSessionState(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useLocalSessionState", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getReportMetricsIntervalMillis() throws SQLException {
/*     */     return getIntegerRuntimeProperty("reportMetricsIntervalMillis");
/*     */   }
/*     */   
/*     */   public void setReportMetricsIntervalMillis(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("reportMetricsIntervalMillis", paramInt);
/*     */   }
/*     */   
/*     */   public String getLoadBalanceSQLExceptionSubclassFailover() throws SQLException {
/*     */     return getStringRuntimeProperty("loadBalanceSQLExceptionSubclassFailover");
/*     */   }
/*     */   
/*     */   public void setLoadBalanceSQLExceptionSubclassFailover(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("loadBalanceSQLExceptionSubclassFailover", paramString);
/*     */   }
/*     */   
/*     */   public boolean getReadFromMasterWhenNoSlaves() throws SQLException {
/*     */     return getBooleanRuntimeProperty("readFromMasterWhenNoSlaves");
/*     */   }
/*     */   
/*     */   public void setReadFromMasterWhenNoSlaves(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("readFromMasterWhenNoSlaves", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getQueriesBeforeRetryMaster() throws SQLException {
/*     */     return getIntegerRuntimeProperty("queriesBeforeRetryMaster");
/*     */   }
/*     */   
/*     */   public void setQueriesBeforeRetryMaster(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("queriesBeforeRetryMaster", paramInt);
/*     */   }
/*     */   
/*     */   public String getLogger() throws SQLException {
/*     */     return getStringRuntimeProperty("logger");
/*     */   }
/*     */   
/*     */   public void setLogger(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("logger", paramString);
/*     */   }
/*     */   
/*     */   public boolean getLogSlowQueries() throws SQLException {
/*     */     return getBooleanRuntimeProperty("logSlowQueries");
/*     */   }
/*     */   
/*     */   public void setLogSlowQueries(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("logSlowQueries", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getDontTrackOpenResources() throws SQLException {
/*     */     return getBooleanRuntimeProperty("dontTrackOpenResources");
/*     */   }
/*     */   
/*     */   public void setDontTrackOpenResources(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("dontTrackOpenResources", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getPedantic() throws SQLException {
/*     */     return getBooleanRuntimeProperty("pedantic");
/*     */   }
/*     */   
/*     */   public void setPedantic(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("pedantic", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getUltraDevHack() throws SQLException {
/*     */     return getBooleanRuntimeProperty("ultraDevHack");
/*     */   }
/*     */   
/*     */   public void setUltraDevHack(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("ultraDevHack", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getNoAccessToProcedureBodies() throws SQLException {
/*     */     return getBooleanRuntimeProperty("noAccessToProcedureBodies");
/*     */   }
/*     */   
/*     */   public void setNoAccessToProcedureBodies(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("noAccessToProcedureBodies", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getInitialTimeout() throws SQLException {
/*     */     return getIntegerRuntimeProperty("initialTimeout");
/*     */   }
/*     */   
/*     */   public void setInitialTimeout(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("initialTimeout", paramInt);
/*     */   }
/*     */   
/*     */   public String getServerConfigCacheFactory() throws SQLException {
/*     */     return getStringRuntimeProperty("serverConfigCacheFactory");
/*     */   }
/*     */   
/*     */   public void setServerConfigCacheFactory(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("serverConfigCacheFactory", paramString);
/*     */   }
/*     */   
/*     */   public boolean getGenerateSimpleParameterMetadata() throws SQLException {
/*     */     return getBooleanRuntimeProperty("generateSimpleParameterMetadata");
/*     */   }
/*     */   
/*     */   public void setGenerateSimpleParameterMetadata(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("generateSimpleParameterMetadata", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getDumpQueriesOnException() throws SQLException {
/*     */     return getBooleanRuntimeProperty("dumpQueriesOnException");
/*     */   }
/*     */   
/*     */   public void setDumpQueriesOnException(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("dumpQueriesOnException", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getPopulateInsertRowWithDefaultValues() throws SQLException {
/*     */     return getBooleanRuntimeProperty("populateInsertRowWithDefaultValues");
/*     */   }
/*     */   
/*     */   public void setPopulateInsertRowWithDefaultValues(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("populateInsertRowWithDefaultValues", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getAutoClosePStmtStreams() throws SQLException {
/*     */     return getBooleanRuntimeProperty("autoClosePStmtStreams");
/*     */   }
/*     */   
/*     */   public void setAutoClosePStmtStreams(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("autoClosePStmtStreams", paramBoolean);
/*     */   }
/*     */   
/*     */   public long getSlowQueryThresholdNanos() throws SQLException {
/*     */     return getLongRuntimeProperty("slowQueryThresholdNanos");
/*     */   }
/*     */   
/*     */   public void setSlowQueryThresholdNanos(long paramLong) throws SQLException {
/*     */     setLongRuntimeProperty("slowQueryThresholdNanos", paramLong);
/*     */   }
/*     */   
/*     */   public int getResultSetSizeThreshold() throws SQLException {
/*     */     return getIntegerRuntimeProperty("resultSetSizeThreshold");
/*     */   }
/*     */   
/*     */   public void setResultSetSizeThreshold(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("resultSetSizeThreshold", paramInt);
/*     */   }
/*     */   
/*     */   public boolean getSendFractionalSeconds() throws SQLException {
/*     */     return getBooleanRuntimeProperty("sendFractionalSeconds");
/*     */   }
/*     */   
/*     */   public void setSendFractionalSeconds(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("sendFractionalSeconds", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getRollbackOnPooledClose() throws SQLException {
/*     */     return getBooleanRuntimeProperty("rollbackOnPooledClose");
/*     */   }
/*     */   
/*     */   public void setRollbackOnPooledClose(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("rollbackOnPooledClose", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getLoadBalanceValidateConnectionOnSwapServer() throws SQLException {
/*     */     return getBooleanRuntimeProperty("loadBalanceValidateConnectionOnSwapServer");
/*     */   }
/*     */   
/*     */   public void setLoadBalanceValidateConnectionOnSwapServer(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("loadBalanceValidateConnectionOnSwapServer", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getTrustCertificateKeyStoreUrl() throws SQLException {
/*     */     return getStringRuntimeProperty("trustCertificateKeyStoreUrl");
/*     */   }
/*     */   
/*     */   public void setTrustCertificateKeyStoreUrl(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("trustCertificateKeyStoreUrl", paramString);
/*     */   }
/*     */   
/*     */   public String getClientCertificateKeyStoreType() throws SQLException {
/*     */     return getStringRuntimeProperty("clientCertificateKeyStoreType");
/*     */   }
/*     */   
/*     */   public void setClientCertificateKeyStoreType(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("clientCertificateKeyStoreType", paramString);
/*     */   }
/*     */   
/*     */   public boolean getLogXaCommands() throws SQLException {
/*     */     return getBooleanRuntimeProperty("logXaCommands");
/*     */   }
/*     */   
/*     */   public void setLogXaCommands(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("logXaCommands", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getParanoid() throws SQLException {
/*     */     return getBooleanRuntimeProperty("paranoid");
/*     */   }
/*     */   
/*     */   public void setParanoid(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("paranoid", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getEmulateLocators() throws SQLException {
/*     */     return getBooleanRuntimeProperty("emulateLocators");
/*     */   }
/*     */   
/*     */   public void setEmulateLocators(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("emulateLocators", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getFunctionsNeverReturnBlobs() throws SQLException {
/*     */     return getBooleanRuntimeProperty("functionsNeverReturnBlobs");
/*     */   }
/*     */   
/*     */   public void setFunctionsNeverReturnBlobs(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("functionsNeverReturnBlobs", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getUseConfigs() throws SQLException {
/*     */     return getStringRuntimeProperty("useConfigs");
/*     */   }
/*     */   
/*     */   public void setUseConfigs(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("useConfigs", paramString);
/*     */   }
/*     */   
/*     */   public boolean getScrollTolerantForwardOnly() throws SQLException {
/*     */     return getBooleanRuntimeProperty("scrollTolerantForwardOnly");
/*     */   }
/*     */   
/*     */   public void setScrollTolerantForwardOnly(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("scrollTolerantForwardOnly", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getTcpKeepAlive() throws SQLException {
/*     */     return getBooleanRuntimeProperty("tcpKeepAlive");
/*     */   }
/*     */   
/*     */   public void setTcpKeepAlive(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("tcpKeepAlive", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getQueryTimeoutKillsConnection() throws SQLException {
/*     */     return getBooleanRuntimeProperty("queryTimeoutKillsConnection");
/*     */   }
/*     */   
/*     */   public void setQueryTimeoutKillsConnection(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("queryTimeoutKillsConnection", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getEmulateUnsupportedPstmts() throws SQLException {
/*     */     return getBooleanRuntimeProperty("emulateUnsupportedPstmts");
/*     */   }
/*     */   
/*     */   public void setEmulateUnsupportedPstmts(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("emulateUnsupportedPstmts", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getPacketDebugBufferSize() throws SQLException {
/*     */     return getIntegerRuntimeProperty("packetDebugBufferSize");
/*     */   }
/*     */   
/*     */   public void setPacketDebugBufferSize(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("packetDebugBufferSize", paramInt);
/*     */   }
/*     */   
/*     */   public String getOciConfigFile() throws SQLException {
/*     */     return getStringRuntimeProperty("ociConfigFile");
/*     */   }
/*     */   
/*     */   public void setOciConfigFile(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("ociConfigFile", paramString);
/*     */   }
/*     */   
/*     */   public int getLoadBalanceHostRemovalGracePeriod() throws SQLException {
/*     */     return getIntegerRuntimeProperty("loadBalanceHostRemovalGracePeriod");
/*     */   }
/*     */   
/*     */   public void setLoadBalanceHostRemovalGracePeriod(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("loadBalanceHostRemovalGracePeriod", paramInt);
/*     */   }
/*     */   
/*     */   public boolean getHoldResultsOpenOverStatementClose() throws SQLException {
/*     */     return getBooleanRuntimeProperty("holdResultsOpenOverStatementClose");
/*     */   }
/*     */   
/*     */   public void setHoldResultsOpenOverStatementClose(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("holdResultsOpenOverStatementClose", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getUseLocalTransactionState() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useLocalTransactionState");
/*     */   }
/*     */   
/*     */   public void setUseLocalTransactionState(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useLocalTransactionState", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getSocksProxyPort() throws SQLException {
/*     */     return getIntegerRuntimeProperty("socksProxyPort");
/*     */   }
/*     */   
/*     */   public void setSocksProxyPort(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("socksProxyPort", paramInt);
/*     */   }
/*     */   
/*     */   public String getEnabledTLSProtocols() throws SQLException {
/*     */     return getStringRuntimeProperty("enabledTLSProtocols");
/*     */   }
/*     */   
/*     */   public void setEnabledTLSProtocols(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("enabledTLSProtocols", paramString);
/*     */   }
/*     */   
/*     */   public int getLargeRowSizeThreshold() throws SQLException {
/*     */     return getIntegerRuntimeProperty("largeRowSizeThreshold");
/*     */   }
/*     */   
/*     */   public void setLargeRowSizeThreshold(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("largeRowSizeThreshold", paramInt);
/*     */   }
/*     */   
/*     */   public boolean getIncludeThreadDumpInDeadlockExceptions() throws SQLException {
/*     */     return getBooleanRuntimeProperty("includeThreadDumpInDeadlockExceptions");
/*     */   }
/*     */   
/*     */   public void setIncludeThreadDumpInDeadlockExceptions(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("includeThreadDumpInDeadlockExceptions", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getUseCompression() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useCompression");
/*     */   }
/*     */   
/*     */   public void setUseCompression(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useCompression", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getReplicationConnectionGroup() throws SQLException {
/*     */     return getStringRuntimeProperty("replicationConnectionGroup");
/*     */   }
/*     */   
/*     */   public void setReplicationConnectionGroup(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("replicationConnectionGroup", paramString);
/*     */   }
/*     */   
/*     */   public String getLdapServerHostname() throws SQLException {
/*     */     return getStringRuntimeProperty("ldapServerHostname");
/*     */   }
/*     */   
/*     */   public void setLdapServerHostname(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("ldapServerHostname", paramString);
/*     */   }
/*     */   
/*     */   public int getTcpRcvBuf() throws SQLException {
/*     */     return getIntegerRuntimeProperty("tcpRcvBuf");
/*     */   }
/*     */   
/*     */   public void setTcpRcvBuf(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("tcpRcvBuf", paramInt);
/*     */   }
/*     */   
/*     */   public boolean getUseNanosForElapsedTime() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useNanosForElapsedTime");
/*     */   }
/*     */   
/*     */   public void setUseNanosForElapsedTime(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useNanosForElapsedTime", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getRetriesAllDown() throws SQLException {
/*     */     return getIntegerRuntimeProperty("retriesAllDown");
/*     */   }
/*     */   
/*     */   public void setRetriesAllDown(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("retriesAllDown", paramInt);
/*     */   }
/*     */   
/*     */   public boolean getIgnoreNonTxTables() throws SQLException {
/*     */     return getBooleanRuntimeProperty("ignoreNonTxTables");
/*     */   }
/*     */   
/*     */   public void setIgnoreNonTxTables(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("ignoreNonTxTables", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getElideSetAutoCommits() throws SQLException {
/*     */     return getBooleanRuntimeProperty("elideSetAutoCommits");
/*     */   }
/*     */   
/*     */   public void setElideSetAutoCommits(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("elideSetAutoCommits", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getDisabledAuthenticationPlugins() throws SQLException {
/*     */     return getStringRuntimeProperty("disabledAuthenticationPlugins");
/*     */   }
/*     */   
/*     */   public void setDisabledAuthenticationPlugins(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("disabledAuthenticationPlugins", paramString);
/*     */   }
/*     */   
/*     */   public boolean getAutoDeserialize() throws SQLException {
/*     */     return getBooleanRuntimeProperty("autoDeserialize");
/*     */   }
/*     */   
/*     */   public void setAutoDeserialize(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("autoDeserialize", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getProcessEscapeCodesForPrepStmts() throws SQLException {
/*     */     return getBooleanRuntimeProperty("processEscapeCodesForPrepStmts");
/*     */   }
/*     */   
/*     */   public void setProcessEscapeCodesForPrepStmts(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("processEscapeCodesForPrepStmts", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getBlobsAreStrings() throws SQLException {
/*     */     return getBooleanRuntimeProperty("blobsAreStrings");
/*     */   }
/*     */   
/*     */   public void setBlobsAreStrings(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("blobsAreStrings", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getEnableEscapeProcessing() throws SQLException {
/*     */     return getBooleanRuntimeProperty("enableEscapeProcessing");
/*     */   }
/*     */   
/*     */   public void setEnableEscapeProcessing(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("enableEscapeProcessing", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getNoDatetimeStringSync() throws SQLException {
/*     */     return getBooleanRuntimeProperty("noDatetimeStringSync");
/*     */   }
/*     */   
/*     */   public void setNoDatetimeStringSync(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("noDatetimeStringSync", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getMaxRows() throws SQLException {
/*     */     return getIntegerRuntimeProperty("maxRows");
/*     */   }
/*     */   
/*     */   public void setMaxRows(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("maxRows", paramInt);
/*     */   }
/*     */   
/*     */   public int getPrepStmtCacheSize() throws SQLException {
/*     */     return getIntegerRuntimeProperty("prepStmtCacheSize");
/*     */   }
/*     */   
/*     */   public void setPrepStmtCacheSize(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("prepStmtCacheSize", paramInt);
/*     */   }
/*     */   
/*     */   public String getLoadBalanceSQLStateFailover() throws SQLException {
/*     */     return getStringRuntimeProperty("loadBalanceSQLStateFailover");
/*     */   }
/*     */   
/*     */   public void setLoadBalanceSQLStateFailover(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("loadBalanceSQLStateFailover", paramString);
/*     */   }
/*     */   
/*     */   public int getSelfDestructOnPingMaxOperations() throws SQLException {
/*     */     return getIntegerRuntimeProperty("selfDestructOnPingMaxOperations");
/*     */   }
/*     */   
/*     */   public void setSelfDestructOnPingMaxOperations(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("selfDestructOnPingMaxOperations", paramInt);
/*     */   }
/*     */   
/*     */   public String getHaLoadBalanceStrategy() throws SQLException {
/*     */     return getStringRuntimeProperty("haLoadBalanceStrategy");
/*     */   }
/*     */   
/*     */   public void setHaLoadBalanceStrategy(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("haLoadBalanceStrategy", paramString);
/*     */   }
/*     */   
/*     */   public String getExceptionInterceptors() throws SQLException {
/*     */     return getStringRuntimeProperty("exceptionInterceptors");
/*     */   }
/*     */   
/*     */   public void setExceptionInterceptors(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("exceptionInterceptors", paramString);
/*     */   }
/*     */   
/*     */   public String getTrustCertificateKeyStorePassword() throws SQLException {
/*     */     return getStringRuntimeProperty("trustCertificateKeyStorePassword");
/*     */   }
/*     */   
/*     */   public void setTrustCertificateKeyStorePassword(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("trustCertificateKeyStorePassword", paramString);
/*     */   }
/*     */   
/*     */   public String getClientInfoProvider() throws SQLException {
/*     */     return getStringRuntimeProperty("clientInfoProvider");
/*     */   }
/*     */   
/*     */   public void setClientInfoProvider(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("clientInfoProvider", paramString);
/*     */   }
/*     */   
/*     */   public boolean getAllowUrlInLocalInfile() throws SQLException {
/*     */     return getBooleanRuntimeProperty("allowUrlInLocalInfile");
/*     */   }
/*     */   
/*     */   public void setAllowUrlInLocalInfile(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("allowUrlInLocalInfile", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getBlobSendChunkSize() throws SQLException {
/*     */     return getIntegerRuntimeProperty("blobSendChunkSize");
/*     */   }
/*     */   
/*     */   public void setBlobSendChunkSize(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("blobSendChunkSize", paramInt);
/*     */   }
/*     */   
/*     */   public String getParseInfoCacheFactory() throws SQLException {
/*     */     return getStringRuntimeProperty("parseInfoCacheFactory");
/*     */   }
/*     */   
/*     */   public void setParseInfoCacheFactory(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("parseInfoCacheFactory", paramString);
/*     */   }
/*     */   
/*     */   public String getDefaultAuthenticationPlugin() throws SQLException {
/*     */     return getStringRuntimeProperty("defaultAuthenticationPlugin");
/*     */   }
/*     */   
/*     */   public void setDefaultAuthenticationPlugin(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("defaultAuthenticationPlugin", paramString);
/*     */   }
/*     */   
/*     */   public boolean getAutoReconnect() throws SQLException {
/*     */     return getBooleanRuntimeProperty("autoReconnect");
/*     */   }
/*     */   
/*     */   public void setAutoReconnect(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("autoReconnect", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getLoadBalanceAutoCommitStatementRegex() throws SQLException {
/*     */     return getStringRuntimeProperty("loadBalanceAutoCommitStatementRegex");
/*     */   }
/*     */   
/*     */   public void setLoadBalanceAutoCommitStatementRegex(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("loadBalanceAutoCommitStatementRegex", paramString);
/*     */   }
/*     */   
/*     */   public boolean getGatherPerfMetrics() throws SQLException {
/*     */     return getBooleanRuntimeProperty("gatherPerfMetrics");
/*     */   }
/*     */   
/*     */   public void setGatherPerfMetrics(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("gatherPerfMetrics", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getUseReadAheadInput() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useReadAheadInput");
/*     */   }
/*     */   
/*     */   public void setUseReadAheadInput(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useReadAheadInput", paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getUseAffectedRows() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useAffectedRows");
/*     */   }
/*     */   
/*     */   public void setUseAffectedRows(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useAffectedRows", paramBoolean);
/*     */   }
/*     */   
/*     */   public String getQueryInterceptors() throws SQLException {
/*     */     return getStringRuntimeProperty("queryInterceptors");
/*     */   }
/*     */   
/*     */   public void setQueryInterceptors(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("queryInterceptors", paramString);
/*     */   }
/*     */   
/*     */   public boolean getPinGlobalTxToPhysicalConnection() throws SQLException {
/*     */     return getBooleanRuntimeProperty("pinGlobalTxToPhysicalConnection");
/*     */   }
/*     */   
/*     */   public void setPinGlobalTxToPhysicalConnection(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("pinGlobalTxToPhysicalConnection", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getMaxReconnects() throws SQLException {
/*     */     return getIntegerRuntimeProperty("maxReconnects");
/*     */   }
/*     */   
/*     */   public void setMaxReconnects(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("maxReconnects", paramInt);
/*     */   }
/*     */   
/*     */   public String getPassword3() throws SQLException {
/*     */     return getStringRuntimeProperty("password3");
/*     */   }
/*     */   
/*     */   public void setPassword3(String paramString) throws SQLException {
/*     */     setStringRuntimeProperty("password3", paramString);
/*     */   }
/*     */   
/*     */   public boolean getUseStreamLengthsInPrepStmts() throws SQLException {
/*     */     return getBooleanRuntimeProperty("useStreamLengthsInPrepStmts");
/*     */   }
/*     */   
/*     */   public void setUseStreamLengthsInPrepStmts(boolean paramBoolean) throws SQLException {
/*     */     setBooleanRuntimeProperty("useStreamLengthsInPrepStmts", paramBoolean);
/*     */   }
/*     */   
/*     */   public int getDefaultFetchSize() throws SQLException {
/*     */     return getIntegerRuntimeProperty("defaultFetchSize");
/*     */   }
/*     */   
/*     */   public void setDefaultFetchSize(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("defaultFetchSize", paramInt);
/*     */   }
/*     */   
/*     */   public int getTcpTrafficClass() throws SQLException {
/*     */     return getIntegerRuntimeProperty("tcpTrafficClass");
/*     */   }
/*     */   
/*     */   public void setTcpTrafficClass(int paramInt) throws SQLException {
/*     */     setIntegerRuntimeProperty("tcpTrafficClass", paramInt);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\MysqlDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */