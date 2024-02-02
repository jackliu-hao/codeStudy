package com.mysql.cj.conf;

import java.util.Map;
import java.util.TreeMap;

public enum PropertyKey {
   USER("user", false),
   PASSWORD("password", false),
   HOST("host", false),
   PORT("port", false),
   PROTOCOL("protocol", false),
   PATH("path", "namedPipePath", false),
   TYPE("type", false),
   ADDRESS("address", false),
   PRIORITY("priority", false),
   DBNAME("dbname", false),
   allowLoadLocalInfile("allowLoadLocalInfile", true),
   allowLoadLocalInfileInPath("allowLoadLocalInfileInPath", true),
   allowMultiQueries("allowMultiQueries", true),
   allowNanAndInf("allowNanAndInf", true),
   allowPublicKeyRetrieval("allowPublicKeyRetrieval", true),
   allowReplicaDownConnections("allowReplicaDownConnections", "allowSlaveDownConnections", true),
   allowSourceDownConnections("allowSourceDownConnections", "allowMasterDownConnections", true),
   allowUrlInLocalInfile("allowUrlInLocalInfile", true),
   alwaysSendSetIsolation("alwaysSendSetIsolation", true),
   authenticationPlugins("authenticationPlugins", true),
   autoClosePStmtStreams("autoClosePStmtStreams", true),
   autoDeserialize("autoDeserialize", true),
   autoGenerateTestcaseScript("autoGenerateTestcaseScript", true),
   autoReconnect("autoReconnect", true),
   autoReconnectForPools("autoReconnectForPools", true),
   autoSlowLog("autoSlowLog", true),
   blobsAreStrings("blobsAreStrings", true),
   blobSendChunkSize("blobSendChunkSize", true),
   cacheCallableStmts("cacheCallableStmts", true),
   cacheDefaultTimeZone("cacheDefaultTimeZone", "cacheDefaultTimezone", true),
   cachePrepStmts("cachePrepStmts", true),
   cacheResultSetMetadata("cacheResultSetMetadata", true),
   cacheServerConfiguration("cacheServerConfiguration", true),
   callableStmtCacheSize("callableStmtCacheSize", true),
   characterEncoding("characterEncoding", true),
   characterSetResults("characterSetResults", true),
   clientCertificateKeyStorePassword("clientCertificateKeyStorePassword", true),
   clientCertificateKeyStoreType("clientCertificateKeyStoreType", true),
   clientCertificateKeyStoreUrl("clientCertificateKeyStoreUrl", true),
   clientInfoProvider("clientInfoProvider", true),
   clobberStreamingResults("clobberStreamingResults", true),
   clobCharacterEncoding("clobCharacterEncoding", true),
   compensateOnDuplicateKeyUpdateCounts("compensateOnDuplicateKeyUpdateCounts", true),
   connectionAttributes("connectionAttributes", true),
   connectionCollation("connectionCollation", true),
   connectionLifecycleInterceptors("connectionLifecycleInterceptors", true),
   connectionTimeZone("connectionTimeZone", "serverTimezone", true),
   connectTimeout("connectTimeout", true),
   continueBatchOnError("continueBatchOnError", true),
   createDatabaseIfNotExist("createDatabaseIfNotExist", true),
   customCharsetMapping("customCharsetMapping", true),
   databaseTerm("databaseTerm", true),
   defaultAuthenticationPlugin("defaultAuthenticationPlugin", true),
   defaultFetchSize("defaultFetchSize", true),
   detectCustomCollations("detectCustomCollations", true),
   disabledAuthenticationPlugins("disabledAuthenticationPlugins", true),
   disconnectOnExpiredPasswords("disconnectOnExpiredPasswords", true),
   dnsSrv("dnsSrv", true),
   dontCheckOnDuplicateKeyUpdateInSQL("dontCheckOnDuplicateKeyUpdateInSQL", true),
   dontTrackOpenResources("dontTrackOpenResources", true),
   dumpQueriesOnException("dumpQueriesOnException", true),
   elideSetAutoCommits("elideSetAutoCommits", true),
   emptyStringsConvertToZero("emptyStringsConvertToZero", true),
   emulateLocators("emulateLocators", true),
   emulateUnsupportedPstmts("emulateUnsupportedPstmts", true),
   enableEscapeProcessing("enableEscapeProcessing", true),
   enablePacketDebug("enablePacketDebug", true),
   enableQueryTimeouts("enableQueryTimeouts", true),
   exceptionInterceptors("exceptionInterceptors", true),
   explainSlowQueries("explainSlowQueries", true),
   failOverReadOnly("failOverReadOnly", true),
   fallbackToSystemKeyStore("fallbackToSystemKeyStore", true),
   fallbackToSystemTrustStore("fallbackToSystemTrustStore", true),
   forceConnectionTimeZoneToSession("forceConnectionTimeZoneToSession", true),
   functionsNeverReturnBlobs("functionsNeverReturnBlobs", true),
   gatherPerfMetrics("gatherPerfMetrics", true),
   generateSimpleParameterMetadata("generateSimpleParameterMetadata", true),
   getProceduresReturnsFunctions("getProceduresReturnsFunctions", true),
   ha_enableJMX("ha.enableJMX", "haEnableJMX", true),
   ha_loadBalanceStrategy("ha.loadBalanceStrategy", "haLoadBalanceStrategy", true),
   holdResultsOpenOverStatementClose("holdResultsOpenOverStatementClose", true),
   ignoreNonTxTables("ignoreNonTxTables", true),
   includeInnodbStatusInDeadlockExceptions("includeInnodbStatusInDeadlockExceptions", true),
   includeThreadDumpInDeadlockExceptions("includeThreadDumpInDeadlockExceptions", true),
   includeThreadNamesAsStatementComment("includeThreadNamesAsStatementComment", true),
   initialTimeout("initialTimeout", true),
   interactiveClient("interactiveClient", true),
   jdbcCompliantTruncation("jdbcCompliantTruncation", true),
   largeRowSizeThreshold("largeRowSizeThreshold", true),
   ldapServerHostname("ldapServerHostname", true),
   loadBalanceAutoCommitStatementRegex("loadBalanceAutoCommitStatementRegex", true),
   loadBalanceAutoCommitStatementThreshold("loadBalanceAutoCommitStatementThreshold", true),
   loadBalanceBlocklistTimeout("loadBalanceBlocklistTimeout", "loadBalanceBlacklistTimeout", true),
   loadBalanceConnectionGroup("loadBalanceConnectionGroup", true),
   loadBalanceExceptionChecker("loadBalanceExceptionChecker", true),
   loadBalanceHostRemovalGracePeriod("loadBalanceHostRemovalGracePeriod", true),
   loadBalancePingTimeout("loadBalancePingTimeout", true),
   loadBalanceSQLExceptionSubclassFailover("loadBalanceSQLExceptionSubclassFailover", true),
   loadBalanceSQLStateFailover("loadBalanceSQLStateFailover", true),
   loadBalanceValidateConnectionOnSwapServer("loadBalanceValidateConnectionOnSwapServer", true),
   localSocketAddress("localSocketAddress", true),
   locatorFetchBufferSize("locatorFetchBufferSize", true),
   logger("logger", true),
   logSlowQueries("logSlowQueries", true),
   logXaCommands("logXaCommands", true),
   maintainTimeStats("maintainTimeStats", true),
   maxAllowedPacket("maxAllowedPacket", true),
   maxQuerySizeToLog("maxQuerySizeToLog", true),
   maxReconnects("maxReconnects", true),
   maxRows("maxRows", true),
   metadataCacheSize("metadataCacheSize", true),
   netTimeoutForStreamingResults("netTimeoutForStreamingResults", true),
   noAccessToProcedureBodies("noAccessToProcedureBodies", true),
   noDatetimeStringSync("noDatetimeStringSync", true),
   nullDatabaseMeansCurrent("nullDatabaseMeansCurrent", "nullCatalogMeansCurrent", true),
   ociConfigFile("ociConfigFile", true),
   overrideSupportsIntegrityEnhancementFacility("overrideSupportsIntegrityEnhancementFacility", true),
   packetDebugBufferSize("packetDebugBufferSize", true),
   padCharsWithSpace("padCharsWithSpace", true),
   paranoid("paranoid", false),
   parseInfoCacheFactory("parseInfoCacheFactory", true),
   password1("password1", true),
   password2("password2", true),
   password3("password3", true),
   passwordCharacterEncoding("passwordCharacterEncoding", true),
   pedantic("pedantic", true),
   pinGlobalTxToPhysicalConnection("pinGlobalTxToPhysicalConnection", true),
   populateInsertRowWithDefaultValues("populateInsertRowWithDefaultValues", true),
   prepStmtCacheSize("prepStmtCacheSize", true),
   prepStmtCacheSqlLimit("prepStmtCacheSqlLimit", true),
   preserveInstants("preserveInstants", true),
   processEscapeCodesForPrepStmts("processEscapeCodesForPrepStmts", true),
   profilerEventHandler("profilerEventHandler", true),
   profileSQL("profileSQL", true),
   propertiesTransform("propertiesTransform", true),
   queriesBeforeRetrySource("queriesBeforeRetrySource", "queriesBeforeRetryMaster", true),
   queryInterceptors("queryInterceptors", true),
   queryTimeoutKillsConnection("queryTimeoutKillsConnection", true),
   readFromSourceWhenNoReplicas("readFromSourceWhenNoReplicas", "readFromMasterWhenNoSlaves", true),
   readOnlyPropagatesToServer("readOnlyPropagatesToServer", true),
   reconnectAtTxEnd("reconnectAtTxEnd", true),
   replicationConnectionGroup("replicationConnectionGroup", true),
   reportMetricsIntervalMillis("reportMetricsIntervalMillis", true),
   requireSSL("requireSSL", true),
   resourceId("resourceId", true),
   resultSetSizeThreshold("resultSetSizeThreshold", true),
   retriesAllDown("retriesAllDown", true),
   rewriteBatchedStatements("rewriteBatchedStatements", true),
   rollbackOnPooledClose("rollbackOnPooledClose", true),
   scrollTolerantForwardOnly("scrollTolerantForwardOnly", true),
   secondsBeforeRetrySource("secondsBeforeRetrySource", "secondsBeforeRetryMaster", true),
   selfDestructOnPingMaxOperations("selfDestructOnPingMaxOperations", true),
   selfDestructOnPingSecondsLifetime("selfDestructOnPingSecondsLifetime", true),
   sendFractionalSeconds("sendFractionalSeconds", true),
   sendFractionalSecondsForTime("sendFractionalSecondsForTime", true),
   serverAffinityOrder("serverAffinityOrder", true),
   serverConfigCacheFactory("serverConfigCacheFactory", true),
   serverRSAPublicKeyFile("serverRSAPublicKeyFile", true),
   sessionVariables("sessionVariables", true),
   slowQueryThresholdMillis("slowQueryThresholdMillis", true),
   slowQueryThresholdNanos("slowQueryThresholdNanos", true),
   socketFactory("socketFactory", true),
   socketTimeout("socketTimeout", true),
   socksProxyHost("socksProxyHost", true),
   socksProxyPort("socksProxyPort", true),
   sslMode("sslMode", true),
   strictUpdates("strictUpdates", true),
   tcpKeepAlive("tcpKeepAlive", true),
   tcpNoDelay("tcpNoDelay", true),
   tcpRcvBuf("tcpRcvBuf", true),
   tcpSndBuf("tcpSndBuf", true),
   tcpTrafficClass("tcpTrafficClass", true),
   tinyInt1isBit("tinyInt1isBit", true),
   tlsCiphersuites("tlsCiphersuites", "enabledSSLCipherSuites", true),
   tlsVersions("tlsVersions", "enabledTLSProtocols", true),
   traceProtocol("traceProtocol", true),
   trackSessionState("trackSessionState", true),
   transformedBitIsBoolean("transformedBitIsBoolean", true),
   treatUtilDateAsTimestamp("treatUtilDateAsTimestamp", true),
   trustCertificateKeyStorePassword("trustCertificateKeyStorePassword", true),
   trustCertificateKeyStoreType("trustCertificateKeyStoreType", true),
   trustCertificateKeyStoreUrl("trustCertificateKeyStoreUrl", true),
   ultraDevHack("ultraDevHack", true),
   useAffectedRows("useAffectedRows", true),
   useColumnNamesInFindColumn("useColumnNamesInFindColumn", true),
   useCompression("useCompression", true),
   useConfigs("useConfigs", true),
   useCursorFetch("useCursorFetch", true),
   useHostsInPrivileges("useHostsInPrivileges", true),
   useInformationSchema("useInformationSchema", true),
   useLocalSessionState("useLocalSessionState", true),
   useLocalTransactionState("useLocalTransactionState", true),
   useNanosForElapsedTime("useNanosForElapsedTime", true),
   useOldAliasMetadataBehavior("useOldAliasMetadataBehavior", true),
   useOnlyServerErrorMessages("useOnlyServerErrorMessages", true),
   useReadAheadInput("useReadAheadInput", true),
   useServerPrepStmts("useServerPrepStmts", true),
   useSSL("useSSL", true),
   useStreamLengthsInPrepStmts("useStreamLengthsInPrepStmts", true),
   useUnbufferedInput("useUnbufferedInput", true),
   useUsageAdvisor("useUsageAdvisor", true),
   verifyServerCertificate("verifyServerCertificate", true),
   xdevapiAsyncResponseTimeout("xdevapi.asyncResponseTimeout", "xdevapiAsyncResponseTimeout", true),
   xdevapiAuth("xdevapi.auth", "xdevapiAuth", true),
   xdevapiConnectTimeout("xdevapi.connect-timeout", "xdevapiConnectTimeout", true),
   xdevapiConnectionAttributes("xdevapi.connection-attributes", "xdevapiConnectionAttributes", true),
   xdevapiCompression("xdevapi.compression", "xdevapiCompression", true),
   xdevapiCompressionAlgorithms("xdevapi.compression-algorithms", "xdevapiCompressionAlgorithms", true),
   xdevapiCompressionExtensions("xdevapi.compression-extensions", "xdevapiCompressionExtensions", true),
   xdevapiDnsSrv("xdevapi.dns-srv", "xdevapiDnsSrv", true),
   xdevapiFallbackToSystemKeyStore("xdevapi.fallback-to-system-keystore", "xdevapiFallbackToSystemKeyStore", true),
   xdevapiFallbackToSystemTrustStore("xdevapi.fallback-to-system-truststore", "xdevapiFallbackToSystemTrustStore", true),
   xdevapiSslKeyStorePassword("xdevapi.ssl-keystore-password", "xdevapiSslKeystorePassword", true),
   xdevapiSslKeyStoreType("xdevapi.ssl-keystore-type", "xdevapiSslKeystoreType", true),
   xdevapiSslKeyStoreUrl("xdevapi.ssl-keystore", "xdevapiSslKeystore", true),
   xdevapiSslMode("xdevapi.ssl-mode", "xdevapiSslMode", true),
   xdevapiSslTrustStorePassword("xdevapi.ssl-truststore-password", "xdevapiSslTruststorePassword", true),
   xdevapiSslTrustStoreType("xdevapi.ssl-truststore-type", "xdevapiSslTruststoreType", true),
   xdevapiSslTrustStoreUrl("xdevapi.ssl-truststore", "xdevapiSslTruststore", true),
   xdevapiTlsCiphersuites("xdevapi.tls-ciphersuites", "xdevapiTlsCiphersuites", true),
   xdevapiTlsVersions("xdevapi.tls-versions", "xdevapiTlsVersions", true),
   yearIsDateType("yearIsDateType", true),
   zeroDateTimeBehavior("zeroDateTimeBehavior", true);

   private String keyName;
   private String ccAlias;
   private boolean isCaseSensitive;
   private static Map<String, PropertyKey> caseInsensitiveValues = new TreeMap(String.CASE_INSENSITIVE_ORDER);

   private PropertyKey(String keyName, boolean isCaseSensitive) {
      this.ccAlias = null;
      this.isCaseSensitive = false;
      this.keyName = keyName;
      this.isCaseSensitive = isCaseSensitive;
   }

   private PropertyKey(String keyName, String alias, boolean isCaseSensitive) {
      this(keyName, isCaseSensitive);
      this.ccAlias = alias;
   }

   public String toString() {
      return this.keyName;
   }

   public String getKeyName() {
      return this.keyName;
   }

   public String getCcAlias() {
      return this.ccAlias;
   }

   public static PropertyKey fromValue(String value) {
      PropertyKey[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         PropertyKey k = var1[var3];
         if (k.isCaseSensitive) {
            if (k.getKeyName().equals(value) || k.getCcAlias() != null && k.getCcAlias().equals(value)) {
               return k;
            }
         } else if (k.getKeyName().equalsIgnoreCase(value) || k.getCcAlias() != null && k.getCcAlias().equalsIgnoreCase(value)) {
            return k;
         }
      }

      return null;
   }

   public static String normalizeCase(String keyName) {
      PropertyKey pk = (PropertyKey)caseInsensitiveValues.get(keyName);
      return pk == null ? keyName : pk.getKeyName();
   }

   static {
      PropertyKey[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         PropertyKey pk = var0[var2];
         if (!pk.isCaseSensitive) {
            caseInsensitiveValues.put(pk.getKeyName(), pk);
            if (pk.getCcAlias() != null) {
               caseInsensitiveValues.put(pk.getCcAlias(), pk);
            }
         }
      }

   }
}
