package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.AbstractRuntimeProperty;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;

public class MysqlDataSource extends JdbcPropertySetImpl implements DataSource, Referenceable, Serializable, JdbcPropertySet {
   static final long serialVersionUID = -5515846944416881264L;
   protected static final NonRegisteringDriver mysqlDriver;
   protected transient PrintWriter logWriter = null;
   protected String databaseName = null;
   protected String encoding = null;
   protected String url = null;
   protected boolean explicitUrl = false;
   protected String hostName = null;
   protected int port = 3306;
   protected boolean explicitPort = false;
   protected String user = null;
   protected String password = null;
   protected String profileSQLString = "false";
   protected String description = "MySQL Connector/J Data Source";

   public Connection getConnection() throws SQLException {
      try {
         return this.getConnection(this.user, this.password);
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2);
      }
   }

   public Connection getConnection(String userID, String pass) throws SQLException {
      try {
         Properties props = this.exposeAsProperties();
         if (userID != null) {
            props.setProperty(PropertyKey.USER.getKeyName(), userID);
         }

         if (pass != null) {
            props.setProperty(PropertyKey.PASSWORD.getKeyName(), pass);
         }

         return this.getConnection(props);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5);
      }
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String value) {
      this.description = value;
   }

   public void setDatabaseName(String dbName) {
      this.databaseName = dbName;
   }

   public String getDatabaseName() {
      return this.databaseName != null ? this.databaseName : "";
   }

   public void setLogWriter(PrintWriter output) throws SQLException {
      try {
         this.logWriter = output;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3);
      }
   }

   public PrintWriter getLogWriter() {
      try {
         return this.logWriter;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2);
      }
   }

   public void setLoginTimeout(int seconds) throws SQLException {
      try {
         ;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3);
      }
   }

   public int getLoginTimeout() {
      try {
         return 0;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2);
      }
   }

   public void setPassword(String pass) {
      this.password = pass;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPort(int p) {
      this.port = p;
      this.explicitPort = true;
   }

   public int getPort() {
      return this.port;
   }

   public void setPortNumber(int p) {
      this.setPort(p);
   }

   public int getPortNumber() {
      return this.getPort();
   }

   public void setPropertiesViaRef(Reference ref) throws SQLException {
      Iterator var2 = PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet().iterator();

      while(var2.hasNext()) {
         PropertyKey propKey = (PropertyKey)var2.next();
         RuntimeProperty<?> propToSet = this.getProperty(propKey);
         if (ref != null) {
            propToSet.initializeFrom((Reference)ref, (ExceptionInterceptor)null);
         }
      }

      this.postInitialization();
   }

   public Reference getReference() throws NamingException {
      String factoryName = MysqlDataSourceFactory.class.getName();
      Reference ref = new Reference(this.getClass().getName(), factoryName, (String)null);
      ref.add(new StringRefAddr(PropertyKey.USER.getKeyName(), this.getUser()));
      ref.add(new StringRefAddr(PropertyKey.PASSWORD.getKeyName(), this.password));
      ref.add(new StringRefAddr("serverName", this.getServerName()));
      ref.add(new StringRefAddr("port", "" + this.getPort()));
      ref.add(new StringRefAddr("explicitPort", String.valueOf(this.explicitPort)));
      ref.add(new StringRefAddr("databaseName", this.getDatabaseName()));
      ref.add(new StringRefAddr("url", this.getUrl()));
      ref.add(new StringRefAddr("explicitUrl", String.valueOf(this.explicitUrl)));
      Iterator var3 = PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet().iterator();

      while(var3.hasNext()) {
         PropertyKey propKey = (PropertyKey)var3.next();
         RuntimeProperty<?> propToStore = this.getProperty(propKey);
         String val = propToStore.getStringValue();
         if (val != null) {
            ref.add(new StringRefAddr(propToStore.getPropertyDefinition().getName(), val));
         }
      }

      return ref;
   }

   public void setServerName(String serverName) {
      this.hostName = serverName;
   }

   public String getServerName() {
      return this.hostName != null ? this.hostName : "";
   }

   public void setURL(String url) {
      this.setUrl(url);
   }

   public String getURL() {
      return this.getUrl();
   }

   public void setUrl(String url) {
      this.url = url;
      this.explicitUrl = true;
   }

   public String getUrl() {
      if (!this.explicitUrl) {
         StringBuilder sbUrl = new StringBuilder(ConnectionUrl.Type.SINGLE_CONNECTION.getScheme());
         sbUrl.append("//").append(this.getServerName());

         try {
            if (this.explicitPort || !this.getBooleanRuntimeProperty(PropertyKey.dnsSrv.getKeyName())) {
               sbUrl.append(":").append(this.getPort());
            }
         } catch (SQLException var3) {
            sbUrl.append(":").append(this.getPort());
         }

         sbUrl.append("/").append(this.getDatabaseName());
         return sbUrl.toString();
      } else {
         return this.url;
      }
   }

   public void setUser(String userID) {
      this.user = userID;
   }

   public String getUser() {
      return this.user;
   }

   protected Connection getConnection(Properties props) throws SQLException {
      String jdbcUrlToUse = this.explicitUrl ? this.url : this.getUrl();
      ConnectionUrl connUrl = ConnectionUrl.getConnectionUrlInstance(jdbcUrlToUse, (Properties)null);
      Properties urlProps = connUrl.getConnectionArgumentsAsProperties();
      urlProps.remove(PropertyKey.HOST.getKeyName());
      urlProps.remove(PropertyKey.PORT.getKeyName());
      urlProps.remove(PropertyKey.DBNAME.getKeyName());
      urlProps.stringPropertyNames().stream().forEach((k) -> {
         props.setProperty(k, urlProps.getProperty(k));
      });
      return mysqlDriver.connect(jdbcUrlToUse, props);
   }

   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      return null;
   }

   public <T> T unwrap(Class<T> iface) throws SQLException {
      try {
         return null;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3);
      }
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      try {
         return false;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3);
      }
   }

   protected String getStringRuntimeProperty(String name) throws SQLException {
      try {
         return (String)this.getStringProperty(name).getValue();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3);
      }
   }

   protected void setStringRuntimeProperty(String name, String value) throws SQLException {
      try {
         ((AbstractRuntimeProperty)this.getStringProperty(name)).setValueInternal(value, (String)null, (ExceptionInterceptor)null);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4);
      }
   }

   protected boolean getBooleanRuntimeProperty(String name) throws SQLException {
      try {
         return (Boolean)this.getBooleanProperty(name).getValue();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3);
      }
   }

   protected void setBooleanRuntimeProperty(String name, boolean value) throws SQLException {
      try {
         ((AbstractRuntimeProperty)this.getBooleanProperty(name)).setValueInternal(value, (String)null, (ExceptionInterceptor)null);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4);
      }
   }

   protected int getIntegerRuntimeProperty(String name) throws SQLException {
      try {
         return (Integer)this.getIntegerProperty(name).getValue();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3);
      }
   }

   protected void setIntegerRuntimeProperty(String name, int value) throws SQLException {
      try {
         ((AbstractRuntimeProperty)this.getIntegerProperty(name)).setValueInternal(value, (String)null, (ExceptionInterceptor)null);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4);
      }
   }

   protected long getLongRuntimeProperty(String name) throws SQLException {
      try {
         return (Long)this.getLongProperty(name).getValue();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3);
      }
   }

   protected void setLongRuntimeProperty(String name, long value) throws SQLException {
      try {
         ((AbstractRuntimeProperty)this.getLongProperty(name)).setValueInternal(value, (String)null, (ExceptionInterceptor)null);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5);
      }
   }

   protected int getMemorySizeRuntimeProperty(String name) throws SQLException {
      try {
         return (Integer)this.getMemorySizeProperty(name).getValue();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3);
      }
   }

   protected void setMemorySizeRuntimeProperty(String name, int value) throws SQLException {
      try {
         ((AbstractRuntimeProperty)this.getMemorySizeProperty(name)).setValueInternal(value, (String)null, (ExceptionInterceptor)null);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4);
      }
   }

   protected String getEnumRuntimeProperty(String name) throws SQLException {
      try {
         return this.getEnumProperty(name).getStringValue();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3);
      }
   }

   protected void setEnumRuntimeProperty(String name, String value) throws SQLException {
      try {
         ((AbstractRuntimeProperty)this.getEnumProperty(name)).setValueInternal(value, (ExceptionInterceptor)null);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4);
      }
   }

   public Properties exposeAsProperties() {
      Properties props = new Properties();
      Iterator var2 = PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet().iterator();

      while(var2.hasNext()) {
         PropertyKey propKey = (PropertyKey)var2.next();
         RuntimeProperty<?> propToGet = this.getProperty(propKey);
         String propValue = propToGet.getStringValue();
         if (propValue != null && propToGet.isExplicitlySet()) {
            props.setProperty(propToGet.getPropertyDefinition().getName(), propValue);
         }
      }

      return props;
   }

   static {
      try {
         mysqlDriver = new NonRegisteringDriver();
      } catch (Exception var1) {
         throw new RuntimeException(Messages.getString("MysqlDataSource.0"));
      }
   }

   public String getConnectionLifecycleInterceptors() throws SQLException {
      return this.getStringRuntimeProperty("connectionLifecycleInterceptors");
   }

   public void setConnectionLifecycleInterceptors(String var1) throws SQLException {
      this.setStringRuntimeProperty("connectionLifecycleInterceptors", var1);
   }

   public String getSocksProxyHost() throws SQLException {
      return this.getStringRuntimeProperty("socksProxyHost");
   }

   public void setSocksProxyHost(String var1) throws SQLException {
      this.setStringRuntimeProperty("socksProxyHost", var1);
   }

   public String getResourceId() throws SQLException {
      return this.getStringRuntimeProperty("resourceId");
   }

   public void setResourceId(String var1) throws SQLException {
      this.setStringRuntimeProperty("resourceId", var1);
   }

   public int getLoadBalanceBlacklistTimeout() throws SQLException {
      return this.getIntegerRuntimeProperty("loadBalanceBlacklistTimeout");
   }

   public void setLoadBalanceBlacklistTimeout(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("loadBalanceBlacklistTimeout", var1);
   }

   public boolean getEnablePacketDebug() throws SQLException {
      return this.getBooleanRuntimeProperty("enablePacketDebug");
   }

   public void setEnablePacketDebug(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("enablePacketDebug", var1);
   }

   public String getServerRSAPublicKeyFile() throws SQLException {
      return this.getStringRuntimeProperty("serverRSAPublicKeyFile");
   }

   public void setServerRSAPublicKeyFile(String var1) throws SQLException {
      this.setStringRuntimeProperty("serverRSAPublicKeyFile", var1);
   }

   public int getPrepStmtCacheSqlLimit() throws SQLException {
      return this.getIntegerRuntimeProperty("prepStmtCacheSqlLimit");
   }

   public void setPrepStmtCacheSqlLimit(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("prepStmtCacheSqlLimit", var1);
   }

   public String getSocketFactory() throws SQLException {
      return this.getStringRuntimeProperty("socketFactory");
   }

   public void setSocketFactory(String var1) throws SQLException {
      this.setStringRuntimeProperty("socketFactory", var1);
   }

   public String getLoadBalanceConnectionGroup() throws SQLException {
      return this.getStringRuntimeProperty("loadBalanceConnectionGroup");
   }

   public void setLoadBalanceConnectionGroup(String var1) throws SQLException {
      this.setStringRuntimeProperty("loadBalanceConnectionGroup", var1);
   }

   public int getNetTimeoutForStreamingResults() throws SQLException {
      return this.getIntegerRuntimeProperty("netTimeoutForStreamingResults");
   }

   public void setNetTimeoutForStreamingResults(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("netTimeoutForStreamingResults", var1);
   }

   public String getConnectionAttributes() throws SQLException {
      return this.getStringRuntimeProperty("connectionAttributes");
   }

   public void setConnectionAttributes(String var1) throws SQLException {
      this.setStringRuntimeProperty("connectionAttributes", var1);
   }

   public boolean getCompensateOnDuplicateKeyUpdateCounts() throws SQLException {
      return this.getBooleanRuntimeProperty("compensateOnDuplicateKeyUpdateCounts");
   }

   public void setCompensateOnDuplicateKeyUpdateCounts(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("compensateOnDuplicateKeyUpdateCounts", var1);
   }

   public boolean getDisconnectOnExpiredPasswords() throws SQLException {
      return this.getBooleanRuntimeProperty("disconnectOnExpiredPasswords");
   }

   public void setDisconnectOnExpiredPasswords(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("disconnectOnExpiredPasswords", var1);
   }

   public int getSelfDestructOnPingSecondsLifetime() throws SQLException {
      return this.getIntegerRuntimeProperty("selfDestructOnPingSecondsLifetime");
   }

   public void setSelfDestructOnPingSecondsLifetime(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("selfDestructOnPingSecondsLifetime", var1);
   }

   public String getPasswordCharacterEncoding() throws SQLException {
      return this.getStringRuntimeProperty("passwordCharacterEncoding");
   }

   public void setPasswordCharacterEncoding(String var1) throws SQLException {
      this.setStringRuntimeProperty("passwordCharacterEncoding", var1);
   }

   public boolean getYearIsDateType() throws SQLException {
      return this.getBooleanRuntimeProperty("yearIsDateType");
   }

   public void setYearIsDateType(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("yearIsDateType", var1);
   }

   public boolean getDontCheckOnDuplicateKeyUpdateInSQL() throws SQLException {
      return this.getBooleanRuntimeProperty("dontCheckOnDuplicateKeyUpdateInSQL");
   }

   public void setDontCheckOnDuplicateKeyUpdateInSQL(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("dontCheckOnDuplicateKeyUpdateInSQL", var1);
   }

   public String getCharacterSetResults() throws SQLException {
      return this.getStringRuntimeProperty("characterSetResults");
   }

   public void setCharacterSetResults(String var1) throws SQLException {
      this.setStringRuntimeProperty("characterSetResults", var1);
   }

   public String getLocalSocketAddress() throws SQLException {
      return this.getStringRuntimeProperty("localSocketAddress");
   }

   public void setLocalSocketAddress(String var1) throws SQLException {
      this.setStringRuntimeProperty("localSocketAddress", var1);
   }

   public boolean getReconnectAtTxEnd() throws SQLException {
      return this.getBooleanRuntimeProperty("reconnectAtTxEnd");
   }

   public void setReconnectAtTxEnd(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("reconnectAtTxEnd", var1);
   }

   public String getDatabaseTerm() throws SQLException {
      return this.getEnumRuntimeProperty("databaseTerm");
   }

   public void setDatabaseTerm(String var1) throws SQLException {
      this.setEnumRuntimeProperty("databaseTerm", var1);
   }

   public boolean getCacheDefaultTimezone() throws SQLException {
      return this.getBooleanRuntimeProperty("cacheDefaultTimezone");
   }

   public void setCacheDefaultTimezone(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("cacheDefaultTimezone", var1);
   }

   public int getMaxQuerySizeToLog() throws SQLException {
      return this.getIntegerRuntimeProperty("maxQuerySizeToLog");
   }

   public void setMaxQuerySizeToLog(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("maxQuerySizeToLog", var1);
   }

   public boolean getMaintainTimeStats() throws SQLException {
      return this.getBooleanRuntimeProperty("maintainTimeStats");
   }

   public void setMaintainTimeStats(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("maintainTimeStats", var1);
   }

   public boolean getDnsSrv() throws SQLException {
      return this.getBooleanRuntimeProperty("dnsSrv");
   }

   public void setDnsSrv(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("dnsSrv", var1);
   }

   public boolean getTransformedBitIsBoolean() throws SQLException {
      return this.getBooleanRuntimeProperty("transformedBitIsBoolean");
   }

   public void setTransformedBitIsBoolean(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("transformedBitIsBoolean", var1);
   }

   public boolean getIncludeThreadNamesAsStatementComment() throws SQLException {
      return this.getBooleanRuntimeProperty("includeThreadNamesAsStatementComment");
   }

   public void setIncludeThreadNamesAsStatementComment(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("includeThreadNamesAsStatementComment", var1);
   }

   public String getTrustCertificateKeyStoreType() throws SQLException {
      return this.getStringRuntimeProperty("trustCertificateKeyStoreType");
   }

   public void setTrustCertificateKeyStoreType(String var1) throws SQLException {
      this.setStringRuntimeProperty("trustCertificateKeyStoreType", var1);
   }

   public boolean getUseInformationSchema() throws SQLException {
      return this.getBooleanRuntimeProperty("useInformationSchema");
   }

   public void setUseInformationSchema(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useInformationSchema", var1);
   }

   public boolean getPadCharsWithSpace() throws SQLException {
      return this.getBooleanRuntimeProperty("padCharsWithSpace");
   }

   public void setPadCharsWithSpace(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("padCharsWithSpace", var1);
   }

   public boolean getUseOldAliasMetadataBehavior() throws SQLException {
      return this.getBooleanRuntimeProperty("useOldAliasMetadataBehavior");
   }

   public void setUseOldAliasMetadataBehavior(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useOldAliasMetadataBehavior", var1);
   }

   public String getEnabledSSLCipherSuites() throws SQLException {
      return this.getStringRuntimeProperty("enabledSSLCipherSuites");
   }

   public void setEnabledSSLCipherSuites(String var1) throws SQLException {
      this.setStringRuntimeProperty("enabledSSLCipherSuites", var1);
   }

   public boolean getUseHostsInPrivileges() throws SQLException {
      return this.getBooleanRuntimeProperty("useHostsInPrivileges");
   }

   public void setUseHostsInPrivileges(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useHostsInPrivileges", var1);
   }

   public String getClobCharacterEncoding() throws SQLException {
      return this.getStringRuntimeProperty("clobCharacterEncoding");
   }

   public void setClobCharacterEncoding(String var1) throws SQLException {
      this.setStringRuntimeProperty("clobCharacterEncoding", var1);
   }

   public String getAuthenticationPlugins() throws SQLException {
      return this.getStringRuntimeProperty("authenticationPlugins");
   }

   public void setAuthenticationPlugins(String var1) throws SQLException {
      this.setStringRuntimeProperty("authenticationPlugins", var1);
   }

   public boolean getReadOnlyPropagatesToServer() throws SQLException {
      return this.getBooleanRuntimeProperty("readOnlyPropagatesToServer");
   }

   public void setReadOnlyPropagatesToServer(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("readOnlyPropagatesToServer", var1);
   }

   public boolean getPreserveInstants() throws SQLException {
      return this.getBooleanRuntimeProperty("preserveInstants");
   }

   public void setPreserveInstants(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("preserveInstants", var1);
   }

   public boolean getUseOnlyServerErrorMessages() throws SQLException {
      return this.getBooleanRuntimeProperty("useOnlyServerErrorMessages");
   }

   public void setUseOnlyServerErrorMessages(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useOnlyServerErrorMessages", var1);
   }

   public boolean getCacheCallableStmts() throws SQLException {
      return this.getBooleanRuntimeProperty("cacheCallableStmts");
   }

   public void setCacheCallableStmts(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("cacheCallableStmts", var1);
   }

   public boolean getFailOverReadOnly() throws SQLException {
      return this.getBooleanRuntimeProperty("failOverReadOnly");
   }

   public void setFailOverReadOnly(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("failOverReadOnly", var1);
   }

   public boolean getCacheResultSetMetadata() throws SQLException {
      return this.getBooleanRuntimeProperty("cacheResultSetMetadata");
   }

   public void setCacheResultSetMetadata(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("cacheResultSetMetadata", var1);
   }

   public boolean getVerifyServerCertificate() throws SQLException {
      return this.getBooleanRuntimeProperty("verifyServerCertificate");
   }

   public void setVerifyServerCertificate(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("verifyServerCertificate", var1);
   }

   public String getSessionVariables() throws SQLException {
      return this.getStringRuntimeProperty("sessionVariables");
   }

   public void setSessionVariables(String var1) throws SQLException {
      this.setStringRuntimeProperty("sessionVariables", var1);
   }

   public String getCustomCharsetMapping() throws SQLException {
      return this.getStringRuntimeProperty("customCharsetMapping");
   }

   public void setCustomCharsetMapping(String var1) throws SQLException {
      this.setStringRuntimeProperty("customCharsetMapping", var1);
   }

   public String getConnectionCollation() throws SQLException {
      return this.getStringRuntimeProperty("connectionCollation");
   }

   public void setConnectionCollation(String var1) throws SQLException {
      this.setStringRuntimeProperty("connectionCollation", var1);
   }

   public String getClientCertificateKeyStoreUrl() throws SQLException {
      return this.getStringRuntimeProperty("clientCertificateKeyStoreUrl");
   }

   public void setClientCertificateKeyStoreUrl(String var1) throws SQLException {
      this.setStringRuntimeProperty("clientCertificateKeyStoreUrl", var1);
   }

   public boolean getFallbackToSystemKeyStore() throws SQLException {
      return this.getBooleanRuntimeProperty("fallbackToSystemKeyStore");
   }

   public void setFallbackToSystemKeyStore(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("fallbackToSystemKeyStore", var1);
   }

   public int getLoadBalanceAutoCommitStatementThreshold() throws SQLException {
      return this.getIntegerRuntimeProperty("loadBalanceAutoCommitStatementThreshold");
   }

   public void setLoadBalanceAutoCommitStatementThreshold(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("loadBalanceAutoCommitStatementThreshold", var1);
   }

   public int getSlowQueryThresholdMillis() throws SQLException {
      return this.getIntegerRuntimeProperty("slowQueryThresholdMillis");
   }

   public void setSlowQueryThresholdMillis(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("slowQueryThresholdMillis", var1);
   }

   public String getProfilerEventHandler() throws SQLException {
      return this.getStringRuntimeProperty("profilerEventHandler");
   }

   public void setProfilerEventHandler(String var1) throws SQLException {
      this.setStringRuntimeProperty("profilerEventHandler", var1);
   }

   public boolean getTreatUtilDateAsTimestamp() throws SQLException {
      return this.getBooleanRuntimeProperty("treatUtilDateAsTimestamp");
   }

   public void setTreatUtilDateAsTimestamp(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("treatUtilDateAsTimestamp", var1);
   }

   public boolean getHaEnableJMX() throws SQLException {
      return this.getBooleanRuntimeProperty("haEnableJMX");
   }

   public void setHaEnableJMX(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("haEnableJMX", var1);
   }

   public String getZeroDateTimeBehavior() throws SQLException {
      return this.getEnumRuntimeProperty("zeroDateTimeBehavior");
   }

   public void setZeroDateTimeBehavior(String var1) throws SQLException {
      this.setEnumRuntimeProperty("zeroDateTimeBehavior", var1);
   }

   public boolean getEnableQueryTimeouts() throws SQLException {
      return this.getBooleanRuntimeProperty("enableQueryTimeouts");
   }

   public void setEnableQueryTimeouts(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("enableQueryTimeouts", var1);
   }

   public boolean getIncludeInnodbStatusInDeadlockExceptions() throws SQLException {
      return this.getBooleanRuntimeProperty("includeInnodbStatusInDeadlockExceptions");
   }

   public void setIncludeInnodbStatusInDeadlockExceptions(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("includeInnodbStatusInDeadlockExceptions", var1);
   }

   public boolean getTrackSessionState() throws SQLException {
      return this.getBooleanRuntimeProperty("trackSessionState");
   }

   public void setTrackSessionState(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("trackSessionState", var1);
   }

   public int getSecondsBeforeRetryMaster() throws SQLException {
      return this.getIntegerRuntimeProperty("secondsBeforeRetryMaster");
   }

   public void setSecondsBeforeRetryMaster(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("secondsBeforeRetryMaster", var1);
   }

   public String getServerAffinityOrder() throws SQLException {
      return this.getStringRuntimeProperty("serverAffinityOrder");
   }

   public void setServerAffinityOrder(String var1) throws SQLException {
      this.setStringRuntimeProperty("serverAffinityOrder", var1);
   }

   public boolean getUseColumnNamesInFindColumn() throws SQLException {
      return this.getBooleanRuntimeProperty("useColumnNamesInFindColumn");
   }

   public void setUseColumnNamesInFindColumn(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useColumnNamesInFindColumn", var1);
   }

   public boolean getSendFractionalSecondsForTime() throws SQLException {
      return this.getBooleanRuntimeProperty("sendFractionalSecondsForTime");
   }

   public void setSendFractionalSecondsForTime(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("sendFractionalSecondsForTime", var1);
   }

   public boolean getForceConnectionTimeZoneToSession() throws SQLException {
      return this.getBooleanRuntimeProperty("forceConnectionTimeZoneToSession");
   }

   public void setForceConnectionTimeZoneToSession(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("forceConnectionTimeZoneToSession", var1);
   }

   public String getServerTimezone() throws SQLException {
      return this.getStringRuntimeProperty("serverTimezone");
   }

   public void setServerTimezone(String var1) throws SQLException {
      this.setStringRuntimeProperty("serverTimezone", var1);
   }

   public boolean getOverrideSupportsIntegrityEnhancementFacility() throws SQLException {
      return this.getBooleanRuntimeProperty("overrideSupportsIntegrityEnhancementFacility");
   }

   public void setOverrideSupportsIntegrityEnhancementFacility(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("overrideSupportsIntegrityEnhancementFacility", var1);
   }

   public int getLoadBalancePingTimeout() throws SQLException {
      return this.getIntegerRuntimeProperty("loadBalancePingTimeout");
   }

   public void setLoadBalancePingTimeout(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("loadBalancePingTimeout", var1);
   }

   public String getLoadBalanceExceptionChecker() throws SQLException {
      return this.getStringRuntimeProperty("loadBalanceExceptionChecker");
   }

   public void setLoadBalanceExceptionChecker(String var1) throws SQLException {
      this.setStringRuntimeProperty("loadBalanceExceptionChecker", var1);
   }

   public boolean getInteractiveClient() throws SQLException {
      return this.getBooleanRuntimeProperty("interactiveClient");
   }

   public void setInteractiveClient(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("interactiveClient", var1);
   }

   public int getConnectTimeout() throws SQLException {
      return this.getIntegerRuntimeProperty("connectTimeout");
   }

   public void setConnectTimeout(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("connectTimeout", var1);
   }

   public String getPassword1() throws SQLException {
      return this.getStringRuntimeProperty("password1");
   }

   public void setPassword1(String var1) throws SQLException {
      this.setStringRuntimeProperty("password1", var1);
   }

   public boolean getProfileSQL() throws SQLException {
      return this.getBooleanRuntimeProperty("profileSQL");
   }

   public void setProfileSQL(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("profileSQL", var1);
   }

   public String getPassword2() throws SQLException {
      return this.getStringRuntimeProperty("password2");
   }

   public void setPassword2(String var1) throws SQLException {
      this.setStringRuntimeProperty("password2", var1);
   }

   public boolean getClobberStreamingResults() throws SQLException {
      return this.getBooleanRuntimeProperty("clobberStreamingResults");
   }

   public void setClobberStreamingResults(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("clobberStreamingResults", var1);
   }

   public int getTcpSndBuf() throws SQLException {
      return this.getIntegerRuntimeProperty("tcpSndBuf");
   }

   public void setTcpSndBuf(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("tcpSndBuf", var1);
   }

   public boolean getAllowNanAndInf() throws SQLException {
      return this.getBooleanRuntimeProperty("allowNanAndInf");
   }

   public void setAllowNanAndInf(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("allowNanAndInf", var1);
   }

   public boolean getAllowLoadLocalInfile() throws SQLException {
      return this.getBooleanRuntimeProperty("allowLoadLocalInfile");
   }

   public void setAllowLoadLocalInfile(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("allowLoadLocalInfile", var1);
   }

   public boolean getTcpNoDelay() throws SQLException {
      return this.getBooleanRuntimeProperty("tcpNoDelay");
   }

   public void setTcpNoDelay(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("tcpNoDelay", var1);
   }

   public boolean getExplainSlowQueries() throws SQLException {
      return this.getBooleanRuntimeProperty("explainSlowQueries");
   }

   public void setExplainSlowQueries(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("explainSlowQueries", var1);
   }

   public boolean getAllowMultiQueries() throws SQLException {
      return this.getBooleanRuntimeProperty("allowMultiQueries");
   }

   public void setAllowMultiQueries(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("allowMultiQueries", var1);
   }

   public boolean getAutoReconnectForPools() throws SQLException {
      return this.getBooleanRuntimeProperty("autoReconnectForPools");
   }

   public void setAutoReconnectForPools(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("autoReconnectForPools", var1);
   }

   public boolean getAutoGenerateTestcaseScript() throws SQLException {
      return this.getBooleanRuntimeProperty("autoGenerateTestcaseScript");
   }

   public void setAutoGenerateTestcaseScript(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("autoGenerateTestcaseScript", var1);
   }

   public boolean getUseUnbufferedInput() throws SQLException {
      return this.getBooleanRuntimeProperty("useUnbufferedInput");
   }

   public void setUseUnbufferedInput(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useUnbufferedInput", var1);
   }

   public int getCallableStmtCacheSize() throws SQLException {
      return this.getIntegerRuntimeProperty("callableStmtCacheSize");
   }

   public void setCallableStmtCacheSize(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("callableStmtCacheSize", var1);
   }

   public int getLocatorFetchBufferSize() throws SQLException {
      return this.getIntegerRuntimeProperty("locatorFetchBufferSize");
   }

   public void setLocatorFetchBufferSize(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("locatorFetchBufferSize", var1);
   }

   public boolean getCacheServerConfiguration() throws SQLException {
      return this.getBooleanRuntimeProperty("cacheServerConfiguration");
   }

   public void setCacheServerConfiguration(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("cacheServerConfiguration", var1);
   }

   public int getMetadataCacheSize() throws SQLException {
      return this.getIntegerRuntimeProperty("metadataCacheSize");
   }

   public void setMetadataCacheSize(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("metadataCacheSize", var1);
   }

   public boolean getUseSSL() throws SQLException {
      return this.getBooleanRuntimeProperty("useSSL");
   }

   public void setUseSSL(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useSSL", var1);
   }

   public boolean getContinueBatchOnError() throws SQLException {
      return this.getBooleanRuntimeProperty("continueBatchOnError");
   }

   public void setContinueBatchOnError(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("continueBatchOnError", var1);
   }

   public boolean getUseCursorFetch() throws SQLException {
      return this.getBooleanRuntimeProperty("useCursorFetch");
   }

   public void setUseCursorFetch(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useCursorFetch", var1);
   }

   public boolean getGetProceduresReturnsFunctions() throws SQLException {
      return this.getBooleanRuntimeProperty("getProceduresReturnsFunctions");
   }

   public void setGetProceduresReturnsFunctions(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("getProceduresReturnsFunctions", var1);
   }

   public boolean getNullCatalogMeansCurrent() throws SQLException {
      return this.getBooleanRuntimeProperty("nullCatalogMeansCurrent");
   }

   public void setNullCatalogMeansCurrent(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("nullCatalogMeansCurrent", var1);
   }

   public boolean getEmptyStringsConvertToZero() throws SQLException {
      return this.getBooleanRuntimeProperty("emptyStringsConvertToZero");
   }

   public void setEmptyStringsConvertToZero(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("emptyStringsConvertToZero", var1);
   }

   public String getSslMode() throws SQLException {
      return this.getEnumRuntimeProperty("sslMode");
   }

   public void setSslMode(String var1) throws SQLException {
      this.setEnumRuntimeProperty("sslMode", var1);
   }

   public boolean getCreateDatabaseIfNotExist() throws SQLException {
      return this.getBooleanRuntimeProperty("createDatabaseIfNotExist");
   }

   public void setCreateDatabaseIfNotExist(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("createDatabaseIfNotExist", var1);
   }

   public int getSocketTimeout() throws SQLException {
      return this.getIntegerRuntimeProperty("socketTimeout");
   }

   public void setSocketTimeout(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("socketTimeout", var1);
   }

   public String getClientCertificateKeyStorePassword() throws SQLException {
      return this.getStringRuntimeProperty("clientCertificateKeyStorePassword");
   }

   public void setClientCertificateKeyStorePassword(String var1) throws SQLException {
      this.setStringRuntimeProperty("clientCertificateKeyStorePassword", var1);
   }

   public boolean getFallbackToSystemTrustStore() throws SQLException {
      return this.getBooleanRuntimeProperty("fallbackToSystemTrustStore");
   }

   public void setFallbackToSystemTrustStore(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("fallbackToSystemTrustStore", var1);
   }

   public boolean getAlwaysSendSetIsolation() throws SQLException {
      return this.getBooleanRuntimeProperty("alwaysSendSetIsolation");
   }

   public void setAlwaysSendSetIsolation(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("alwaysSendSetIsolation", var1);
   }

   public boolean getDetectCustomCollations() throws SQLException {
      return this.getBooleanRuntimeProperty("detectCustomCollations");
   }

   public void setDetectCustomCollations(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("detectCustomCollations", var1);
   }

   public String getPropertiesTransform() throws SQLException {
      return this.getStringRuntimeProperty("propertiesTransform");
   }

   public void setPropertiesTransform(String var1) throws SQLException {
      this.setStringRuntimeProperty("propertiesTransform", var1);
   }

   public boolean getUseServerPrepStmts() throws SQLException {
      return this.getBooleanRuntimeProperty("useServerPrepStmts");
   }

   public void setUseServerPrepStmts(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useServerPrepStmts", var1);
   }

   public boolean getTinyInt1isBit() throws SQLException {
      return this.getBooleanRuntimeProperty("tinyInt1isBit");
   }

   public void setTinyInt1isBit(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("tinyInt1isBit", var1);
   }

   public String getAllowLoadLocalInfileInPath() throws SQLException {
      return this.getStringRuntimeProperty("allowLoadLocalInfileInPath");
   }

   public void setAllowLoadLocalInfileInPath(String var1) throws SQLException {
      this.setStringRuntimeProperty("allowLoadLocalInfileInPath", var1);
   }

   public boolean getUseUsageAdvisor() throws SQLException {
      return this.getBooleanRuntimeProperty("useUsageAdvisor");
   }

   public void setUseUsageAdvisor(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useUsageAdvisor", var1);
   }

   public boolean getAllowSlaveDownConnections() throws SQLException {
      return this.getBooleanRuntimeProperty("allowSlaveDownConnections");
   }

   public void setAllowSlaveDownConnections(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("allowSlaveDownConnections", var1);
   }

   public String getCharacterEncoding() throws SQLException {
      return this.getStringRuntimeProperty("characterEncoding");
   }

   public void setCharacterEncoding(String var1) throws SQLException {
      this.setStringRuntimeProperty("characterEncoding", var1);
   }

   public boolean getAllowPublicKeyRetrieval() throws SQLException {
      return this.getBooleanRuntimeProperty("allowPublicKeyRetrieval");
   }

   public void setAllowPublicKeyRetrieval(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("allowPublicKeyRetrieval", var1);
   }

   public boolean getRequireSSL() throws SQLException {
      return this.getBooleanRuntimeProperty("requireSSL");
   }

   public void setRequireSSL(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("requireSSL", var1);
   }

   public int getMaxAllowedPacket() throws SQLException {
      return this.getIntegerRuntimeProperty("maxAllowedPacket");
   }

   public void setMaxAllowedPacket(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("maxAllowedPacket", var1);
   }

   public boolean getTraceProtocol() throws SQLException {
      return this.getBooleanRuntimeProperty("traceProtocol");
   }

   public void setTraceProtocol(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("traceProtocol", var1);
   }

   public boolean getJdbcCompliantTruncation() throws SQLException {
      return this.getBooleanRuntimeProperty("jdbcCompliantTruncation");
   }

   public void setJdbcCompliantTruncation(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("jdbcCompliantTruncation", var1);
   }

   public boolean getCachePrepStmts() throws SQLException {
      return this.getBooleanRuntimeProperty("cachePrepStmts");
   }

   public void setCachePrepStmts(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("cachePrepStmts", var1);
   }

   public boolean getStrictUpdates() throws SQLException {
      return this.getBooleanRuntimeProperty("strictUpdates");
   }

   public void setStrictUpdates(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("strictUpdates", var1);
   }

   public boolean getRewriteBatchedStatements() throws SQLException {
      return this.getBooleanRuntimeProperty("rewriteBatchedStatements");
   }

   public void setRewriteBatchedStatements(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("rewriteBatchedStatements", var1);
   }

   public boolean getAllowMasterDownConnections() throws SQLException {
      return this.getBooleanRuntimeProperty("allowMasterDownConnections");
   }

   public void setAllowMasterDownConnections(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("allowMasterDownConnections", var1);
   }

   public boolean getAutoSlowLog() throws SQLException {
      return this.getBooleanRuntimeProperty("autoSlowLog");
   }

   public void setAutoSlowLog(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("autoSlowLog", var1);
   }

   public boolean getUseLocalSessionState() throws SQLException {
      return this.getBooleanRuntimeProperty("useLocalSessionState");
   }

   public void setUseLocalSessionState(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useLocalSessionState", var1);
   }

   public int getReportMetricsIntervalMillis() throws SQLException {
      return this.getIntegerRuntimeProperty("reportMetricsIntervalMillis");
   }

   public void setReportMetricsIntervalMillis(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("reportMetricsIntervalMillis", var1);
   }

   public String getLoadBalanceSQLExceptionSubclassFailover() throws SQLException {
      return this.getStringRuntimeProperty("loadBalanceSQLExceptionSubclassFailover");
   }

   public void setLoadBalanceSQLExceptionSubclassFailover(String var1) throws SQLException {
      this.setStringRuntimeProperty("loadBalanceSQLExceptionSubclassFailover", var1);
   }

   public boolean getReadFromMasterWhenNoSlaves() throws SQLException {
      return this.getBooleanRuntimeProperty("readFromMasterWhenNoSlaves");
   }

   public void setReadFromMasterWhenNoSlaves(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("readFromMasterWhenNoSlaves", var1);
   }

   public int getQueriesBeforeRetryMaster() throws SQLException {
      return this.getIntegerRuntimeProperty("queriesBeforeRetryMaster");
   }

   public void setQueriesBeforeRetryMaster(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("queriesBeforeRetryMaster", var1);
   }

   public String getLogger() throws SQLException {
      return this.getStringRuntimeProperty("logger");
   }

   public void setLogger(String var1) throws SQLException {
      this.setStringRuntimeProperty("logger", var1);
   }

   public boolean getLogSlowQueries() throws SQLException {
      return this.getBooleanRuntimeProperty("logSlowQueries");
   }

   public void setLogSlowQueries(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("logSlowQueries", var1);
   }

   public boolean getDontTrackOpenResources() throws SQLException {
      return this.getBooleanRuntimeProperty("dontTrackOpenResources");
   }

   public void setDontTrackOpenResources(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("dontTrackOpenResources", var1);
   }

   public boolean getPedantic() throws SQLException {
      return this.getBooleanRuntimeProperty("pedantic");
   }

   public void setPedantic(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("pedantic", var1);
   }

   public boolean getUltraDevHack() throws SQLException {
      return this.getBooleanRuntimeProperty("ultraDevHack");
   }

   public void setUltraDevHack(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("ultraDevHack", var1);
   }

   public boolean getNoAccessToProcedureBodies() throws SQLException {
      return this.getBooleanRuntimeProperty("noAccessToProcedureBodies");
   }

   public void setNoAccessToProcedureBodies(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("noAccessToProcedureBodies", var1);
   }

   public int getInitialTimeout() throws SQLException {
      return this.getIntegerRuntimeProperty("initialTimeout");
   }

   public void setInitialTimeout(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("initialTimeout", var1);
   }

   public String getServerConfigCacheFactory() throws SQLException {
      return this.getStringRuntimeProperty("serverConfigCacheFactory");
   }

   public void setServerConfigCacheFactory(String var1) throws SQLException {
      this.setStringRuntimeProperty("serverConfigCacheFactory", var1);
   }

   public boolean getGenerateSimpleParameterMetadata() throws SQLException {
      return this.getBooleanRuntimeProperty("generateSimpleParameterMetadata");
   }

   public void setGenerateSimpleParameterMetadata(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("generateSimpleParameterMetadata", var1);
   }

   public boolean getDumpQueriesOnException() throws SQLException {
      return this.getBooleanRuntimeProperty("dumpQueriesOnException");
   }

   public void setDumpQueriesOnException(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("dumpQueriesOnException", var1);
   }

   public boolean getPopulateInsertRowWithDefaultValues() throws SQLException {
      return this.getBooleanRuntimeProperty("populateInsertRowWithDefaultValues");
   }

   public void setPopulateInsertRowWithDefaultValues(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("populateInsertRowWithDefaultValues", var1);
   }

   public boolean getAutoClosePStmtStreams() throws SQLException {
      return this.getBooleanRuntimeProperty("autoClosePStmtStreams");
   }

   public void setAutoClosePStmtStreams(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("autoClosePStmtStreams", var1);
   }

   public long getSlowQueryThresholdNanos() throws SQLException {
      return this.getLongRuntimeProperty("slowQueryThresholdNanos");
   }

   public void setSlowQueryThresholdNanos(long var1) throws SQLException {
      this.setLongRuntimeProperty("slowQueryThresholdNanos", var1);
   }

   public int getResultSetSizeThreshold() throws SQLException {
      return this.getIntegerRuntimeProperty("resultSetSizeThreshold");
   }

   public void setResultSetSizeThreshold(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("resultSetSizeThreshold", var1);
   }

   public boolean getSendFractionalSeconds() throws SQLException {
      return this.getBooleanRuntimeProperty("sendFractionalSeconds");
   }

   public void setSendFractionalSeconds(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("sendFractionalSeconds", var1);
   }

   public boolean getRollbackOnPooledClose() throws SQLException {
      return this.getBooleanRuntimeProperty("rollbackOnPooledClose");
   }

   public void setRollbackOnPooledClose(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("rollbackOnPooledClose", var1);
   }

   public boolean getLoadBalanceValidateConnectionOnSwapServer() throws SQLException {
      return this.getBooleanRuntimeProperty("loadBalanceValidateConnectionOnSwapServer");
   }

   public void setLoadBalanceValidateConnectionOnSwapServer(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("loadBalanceValidateConnectionOnSwapServer", var1);
   }

   public String getTrustCertificateKeyStoreUrl() throws SQLException {
      return this.getStringRuntimeProperty("trustCertificateKeyStoreUrl");
   }

   public void setTrustCertificateKeyStoreUrl(String var1) throws SQLException {
      this.setStringRuntimeProperty("trustCertificateKeyStoreUrl", var1);
   }

   public String getClientCertificateKeyStoreType() throws SQLException {
      return this.getStringRuntimeProperty("clientCertificateKeyStoreType");
   }

   public void setClientCertificateKeyStoreType(String var1) throws SQLException {
      this.setStringRuntimeProperty("clientCertificateKeyStoreType", var1);
   }

   public boolean getLogXaCommands() throws SQLException {
      return this.getBooleanRuntimeProperty("logXaCommands");
   }

   public void setLogXaCommands(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("logXaCommands", var1);
   }

   public boolean getParanoid() throws SQLException {
      return this.getBooleanRuntimeProperty("paranoid");
   }

   public void setParanoid(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("paranoid", var1);
   }

   public boolean getEmulateLocators() throws SQLException {
      return this.getBooleanRuntimeProperty("emulateLocators");
   }

   public void setEmulateLocators(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("emulateLocators", var1);
   }

   public boolean getFunctionsNeverReturnBlobs() throws SQLException {
      return this.getBooleanRuntimeProperty("functionsNeverReturnBlobs");
   }

   public void setFunctionsNeverReturnBlobs(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("functionsNeverReturnBlobs", var1);
   }

   public String getUseConfigs() throws SQLException {
      return this.getStringRuntimeProperty("useConfigs");
   }

   public void setUseConfigs(String var1) throws SQLException {
      this.setStringRuntimeProperty("useConfigs", var1);
   }

   public boolean getScrollTolerantForwardOnly() throws SQLException {
      return this.getBooleanRuntimeProperty("scrollTolerantForwardOnly");
   }

   public void setScrollTolerantForwardOnly(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("scrollTolerantForwardOnly", var1);
   }

   public boolean getTcpKeepAlive() throws SQLException {
      return this.getBooleanRuntimeProperty("tcpKeepAlive");
   }

   public void setTcpKeepAlive(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("tcpKeepAlive", var1);
   }

   public boolean getQueryTimeoutKillsConnection() throws SQLException {
      return this.getBooleanRuntimeProperty("queryTimeoutKillsConnection");
   }

   public void setQueryTimeoutKillsConnection(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("queryTimeoutKillsConnection", var1);
   }

   public boolean getEmulateUnsupportedPstmts() throws SQLException {
      return this.getBooleanRuntimeProperty("emulateUnsupportedPstmts");
   }

   public void setEmulateUnsupportedPstmts(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("emulateUnsupportedPstmts", var1);
   }

   public int getPacketDebugBufferSize() throws SQLException {
      return this.getIntegerRuntimeProperty("packetDebugBufferSize");
   }

   public void setPacketDebugBufferSize(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("packetDebugBufferSize", var1);
   }

   public String getOciConfigFile() throws SQLException {
      return this.getStringRuntimeProperty("ociConfigFile");
   }

   public void setOciConfigFile(String var1) throws SQLException {
      this.setStringRuntimeProperty("ociConfigFile", var1);
   }

   public int getLoadBalanceHostRemovalGracePeriod() throws SQLException {
      return this.getIntegerRuntimeProperty("loadBalanceHostRemovalGracePeriod");
   }

   public void setLoadBalanceHostRemovalGracePeriod(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("loadBalanceHostRemovalGracePeriod", var1);
   }

   public boolean getHoldResultsOpenOverStatementClose() throws SQLException {
      return this.getBooleanRuntimeProperty("holdResultsOpenOverStatementClose");
   }

   public void setHoldResultsOpenOverStatementClose(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("holdResultsOpenOverStatementClose", var1);
   }

   public boolean getUseLocalTransactionState() throws SQLException {
      return this.getBooleanRuntimeProperty("useLocalTransactionState");
   }

   public void setUseLocalTransactionState(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useLocalTransactionState", var1);
   }

   public int getSocksProxyPort() throws SQLException {
      return this.getIntegerRuntimeProperty("socksProxyPort");
   }

   public void setSocksProxyPort(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("socksProxyPort", var1);
   }

   public String getEnabledTLSProtocols() throws SQLException {
      return this.getStringRuntimeProperty("enabledTLSProtocols");
   }

   public void setEnabledTLSProtocols(String var1) throws SQLException {
      this.setStringRuntimeProperty("enabledTLSProtocols", var1);
   }

   public int getLargeRowSizeThreshold() throws SQLException {
      return this.getIntegerRuntimeProperty("largeRowSizeThreshold");
   }

   public void setLargeRowSizeThreshold(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("largeRowSizeThreshold", var1);
   }

   public boolean getIncludeThreadDumpInDeadlockExceptions() throws SQLException {
      return this.getBooleanRuntimeProperty("includeThreadDumpInDeadlockExceptions");
   }

   public void setIncludeThreadDumpInDeadlockExceptions(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("includeThreadDumpInDeadlockExceptions", var1);
   }

   public boolean getUseCompression() throws SQLException {
      return this.getBooleanRuntimeProperty("useCompression");
   }

   public void setUseCompression(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useCompression", var1);
   }

   public String getReplicationConnectionGroup() throws SQLException {
      return this.getStringRuntimeProperty("replicationConnectionGroup");
   }

   public void setReplicationConnectionGroup(String var1) throws SQLException {
      this.setStringRuntimeProperty("replicationConnectionGroup", var1);
   }

   public String getLdapServerHostname() throws SQLException {
      return this.getStringRuntimeProperty("ldapServerHostname");
   }

   public void setLdapServerHostname(String var1) throws SQLException {
      this.setStringRuntimeProperty("ldapServerHostname", var1);
   }

   public int getTcpRcvBuf() throws SQLException {
      return this.getIntegerRuntimeProperty("tcpRcvBuf");
   }

   public void setTcpRcvBuf(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("tcpRcvBuf", var1);
   }

   public boolean getUseNanosForElapsedTime() throws SQLException {
      return this.getBooleanRuntimeProperty("useNanosForElapsedTime");
   }

   public void setUseNanosForElapsedTime(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useNanosForElapsedTime", var1);
   }

   public int getRetriesAllDown() throws SQLException {
      return this.getIntegerRuntimeProperty("retriesAllDown");
   }

   public void setRetriesAllDown(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("retriesAllDown", var1);
   }

   public boolean getIgnoreNonTxTables() throws SQLException {
      return this.getBooleanRuntimeProperty("ignoreNonTxTables");
   }

   public void setIgnoreNonTxTables(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("ignoreNonTxTables", var1);
   }

   public boolean getElideSetAutoCommits() throws SQLException {
      return this.getBooleanRuntimeProperty("elideSetAutoCommits");
   }

   public void setElideSetAutoCommits(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("elideSetAutoCommits", var1);
   }

   public String getDisabledAuthenticationPlugins() throws SQLException {
      return this.getStringRuntimeProperty("disabledAuthenticationPlugins");
   }

   public void setDisabledAuthenticationPlugins(String var1) throws SQLException {
      this.setStringRuntimeProperty("disabledAuthenticationPlugins", var1);
   }

   public boolean getAutoDeserialize() throws SQLException {
      return this.getBooleanRuntimeProperty("autoDeserialize");
   }

   public void setAutoDeserialize(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("autoDeserialize", var1);
   }

   public boolean getProcessEscapeCodesForPrepStmts() throws SQLException {
      return this.getBooleanRuntimeProperty("processEscapeCodesForPrepStmts");
   }

   public void setProcessEscapeCodesForPrepStmts(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("processEscapeCodesForPrepStmts", var1);
   }

   public boolean getBlobsAreStrings() throws SQLException {
      return this.getBooleanRuntimeProperty("blobsAreStrings");
   }

   public void setBlobsAreStrings(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("blobsAreStrings", var1);
   }

   public boolean getEnableEscapeProcessing() throws SQLException {
      return this.getBooleanRuntimeProperty("enableEscapeProcessing");
   }

   public void setEnableEscapeProcessing(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("enableEscapeProcessing", var1);
   }

   public boolean getNoDatetimeStringSync() throws SQLException {
      return this.getBooleanRuntimeProperty("noDatetimeStringSync");
   }

   public void setNoDatetimeStringSync(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("noDatetimeStringSync", var1);
   }

   public int getMaxRows() throws SQLException {
      return this.getIntegerRuntimeProperty("maxRows");
   }

   public void setMaxRows(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("maxRows", var1);
   }

   public int getPrepStmtCacheSize() throws SQLException {
      return this.getIntegerRuntimeProperty("prepStmtCacheSize");
   }

   public void setPrepStmtCacheSize(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("prepStmtCacheSize", var1);
   }

   public String getLoadBalanceSQLStateFailover() throws SQLException {
      return this.getStringRuntimeProperty("loadBalanceSQLStateFailover");
   }

   public void setLoadBalanceSQLStateFailover(String var1) throws SQLException {
      this.setStringRuntimeProperty("loadBalanceSQLStateFailover", var1);
   }

   public int getSelfDestructOnPingMaxOperations() throws SQLException {
      return this.getIntegerRuntimeProperty("selfDestructOnPingMaxOperations");
   }

   public void setSelfDestructOnPingMaxOperations(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("selfDestructOnPingMaxOperations", var1);
   }

   public String getHaLoadBalanceStrategy() throws SQLException {
      return this.getStringRuntimeProperty("haLoadBalanceStrategy");
   }

   public void setHaLoadBalanceStrategy(String var1) throws SQLException {
      this.setStringRuntimeProperty("haLoadBalanceStrategy", var1);
   }

   public String getExceptionInterceptors() throws SQLException {
      return this.getStringRuntimeProperty("exceptionInterceptors");
   }

   public void setExceptionInterceptors(String var1) throws SQLException {
      this.setStringRuntimeProperty("exceptionInterceptors", var1);
   }

   public String getTrustCertificateKeyStorePassword() throws SQLException {
      return this.getStringRuntimeProperty("trustCertificateKeyStorePassword");
   }

   public void setTrustCertificateKeyStorePassword(String var1) throws SQLException {
      this.setStringRuntimeProperty("trustCertificateKeyStorePassword", var1);
   }

   public String getClientInfoProvider() throws SQLException {
      return this.getStringRuntimeProperty("clientInfoProvider");
   }

   public void setClientInfoProvider(String var1) throws SQLException {
      this.setStringRuntimeProperty("clientInfoProvider", var1);
   }

   public boolean getAllowUrlInLocalInfile() throws SQLException {
      return this.getBooleanRuntimeProperty("allowUrlInLocalInfile");
   }

   public void setAllowUrlInLocalInfile(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("allowUrlInLocalInfile", var1);
   }

   public int getBlobSendChunkSize() throws SQLException {
      return this.getIntegerRuntimeProperty("blobSendChunkSize");
   }

   public void setBlobSendChunkSize(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("blobSendChunkSize", var1);
   }

   public String getParseInfoCacheFactory() throws SQLException {
      return this.getStringRuntimeProperty("parseInfoCacheFactory");
   }

   public void setParseInfoCacheFactory(String var1) throws SQLException {
      this.setStringRuntimeProperty("parseInfoCacheFactory", var1);
   }

   public String getDefaultAuthenticationPlugin() throws SQLException {
      return this.getStringRuntimeProperty("defaultAuthenticationPlugin");
   }

   public void setDefaultAuthenticationPlugin(String var1) throws SQLException {
      this.setStringRuntimeProperty("defaultAuthenticationPlugin", var1);
   }

   public boolean getAutoReconnect() throws SQLException {
      return this.getBooleanRuntimeProperty("autoReconnect");
   }

   public void setAutoReconnect(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("autoReconnect", var1);
   }

   public String getLoadBalanceAutoCommitStatementRegex() throws SQLException {
      return this.getStringRuntimeProperty("loadBalanceAutoCommitStatementRegex");
   }

   public void setLoadBalanceAutoCommitStatementRegex(String var1) throws SQLException {
      this.setStringRuntimeProperty("loadBalanceAutoCommitStatementRegex", var1);
   }

   public boolean getGatherPerfMetrics() throws SQLException {
      return this.getBooleanRuntimeProperty("gatherPerfMetrics");
   }

   public void setGatherPerfMetrics(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("gatherPerfMetrics", var1);
   }

   public boolean getUseReadAheadInput() throws SQLException {
      return this.getBooleanRuntimeProperty("useReadAheadInput");
   }

   public void setUseReadAheadInput(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useReadAheadInput", var1);
   }

   public boolean getUseAffectedRows() throws SQLException {
      return this.getBooleanRuntimeProperty("useAffectedRows");
   }

   public void setUseAffectedRows(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useAffectedRows", var1);
   }

   public String getQueryInterceptors() throws SQLException {
      return this.getStringRuntimeProperty("queryInterceptors");
   }

   public void setQueryInterceptors(String var1) throws SQLException {
      this.setStringRuntimeProperty("queryInterceptors", var1);
   }

   public boolean getPinGlobalTxToPhysicalConnection() throws SQLException {
      return this.getBooleanRuntimeProperty("pinGlobalTxToPhysicalConnection");
   }

   public void setPinGlobalTxToPhysicalConnection(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("pinGlobalTxToPhysicalConnection", var1);
   }

   public int getMaxReconnects() throws SQLException {
      return this.getIntegerRuntimeProperty("maxReconnects");
   }

   public void setMaxReconnects(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("maxReconnects", var1);
   }

   public String getPassword3() throws SQLException {
      return this.getStringRuntimeProperty("password3");
   }

   public void setPassword3(String var1) throws SQLException {
      this.setStringRuntimeProperty("password3", var1);
   }

   public boolean getUseStreamLengthsInPrepStmts() throws SQLException {
      return this.getBooleanRuntimeProperty("useStreamLengthsInPrepStmts");
   }

   public void setUseStreamLengthsInPrepStmts(boolean var1) throws SQLException {
      this.setBooleanRuntimeProperty("useStreamLengthsInPrepStmts", var1);
   }

   public int getDefaultFetchSize() throws SQLException {
      return this.getIntegerRuntimeProperty("defaultFetchSize");
   }

   public void setDefaultFetchSize(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("defaultFetchSize", var1);
   }

   public int getTcpTrafficClass() throws SQLException {
      return this.getIntegerRuntimeProperty("tcpTrafficClass");
   }

   public void setTcpTrafficClass(int var1) throws SQLException {
      this.setIntegerRuntimeProperty("tcpTrafficClass", var1);
   }
}
