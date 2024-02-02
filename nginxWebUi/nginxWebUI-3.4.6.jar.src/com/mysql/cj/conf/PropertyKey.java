/*     */ package com.mysql.cj.conf;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum PropertyKey
/*     */ {
/*  43 */   USER("user", false),
/*     */   
/*  45 */   PASSWORD("password", false),
/*     */   
/*  47 */   HOST("host", false),
/*     */   
/*  49 */   PORT("port", false),
/*     */   
/*  51 */   PROTOCOL("protocol", false),
/*     */   
/*  53 */   PATH("path", "namedPipePath", false),
/*     */   
/*  55 */   TYPE("type", false),
/*     */   
/*  57 */   ADDRESS("address", false),
/*     */   
/*  59 */   PRIORITY("priority", false),
/*     */   
/*  61 */   DBNAME("dbname", false),
/*     */   
/*  63 */   allowLoadLocalInfile("allowLoadLocalInfile", true),
/*  64 */   allowLoadLocalInfileInPath("allowLoadLocalInfileInPath", true),
/*  65 */   allowMultiQueries("allowMultiQueries", true),
/*  66 */   allowNanAndInf("allowNanAndInf", true),
/*  67 */   allowPublicKeyRetrieval("allowPublicKeyRetrieval", true),
/*  68 */   allowReplicaDownConnections("allowReplicaDownConnections", "allowSlaveDownConnections", true),
/*  69 */   allowSourceDownConnections("allowSourceDownConnections", "allowMasterDownConnections", true),
/*  70 */   allowUrlInLocalInfile("allowUrlInLocalInfile", true),
/*  71 */   alwaysSendSetIsolation("alwaysSendSetIsolation", true),
/*  72 */   authenticationPlugins("authenticationPlugins", true),
/*  73 */   autoClosePStmtStreams("autoClosePStmtStreams", true),
/*  74 */   autoDeserialize("autoDeserialize", true),
/*  75 */   autoGenerateTestcaseScript("autoGenerateTestcaseScript", true),
/*  76 */   autoReconnect("autoReconnect", true),
/*  77 */   autoReconnectForPools("autoReconnectForPools", true),
/*  78 */   autoSlowLog("autoSlowLog", true),
/*  79 */   blobsAreStrings("blobsAreStrings", true),
/*  80 */   blobSendChunkSize("blobSendChunkSize", true),
/*  81 */   cacheCallableStmts("cacheCallableStmts", true),
/*  82 */   cacheDefaultTimeZone("cacheDefaultTimeZone", "cacheDefaultTimezone", true),
/*  83 */   cachePrepStmts("cachePrepStmts", true),
/*  84 */   cacheResultSetMetadata("cacheResultSetMetadata", true),
/*  85 */   cacheServerConfiguration("cacheServerConfiguration", true),
/*  86 */   callableStmtCacheSize("callableStmtCacheSize", true),
/*  87 */   characterEncoding("characterEncoding", true),
/*  88 */   characterSetResults("characterSetResults", true),
/*  89 */   clientCertificateKeyStorePassword("clientCertificateKeyStorePassword", true),
/*  90 */   clientCertificateKeyStoreType("clientCertificateKeyStoreType", true),
/*  91 */   clientCertificateKeyStoreUrl("clientCertificateKeyStoreUrl", true),
/*  92 */   clientInfoProvider("clientInfoProvider", true),
/*  93 */   clobberStreamingResults("clobberStreamingResults", true),
/*  94 */   clobCharacterEncoding("clobCharacterEncoding", true),
/*  95 */   compensateOnDuplicateKeyUpdateCounts("compensateOnDuplicateKeyUpdateCounts", true),
/*  96 */   connectionAttributes("connectionAttributes", true),
/*  97 */   connectionCollation("connectionCollation", true),
/*  98 */   connectionLifecycleInterceptors("connectionLifecycleInterceptors", true),
/*  99 */   connectionTimeZone("connectionTimeZone", "serverTimezone", true),
/* 100 */   connectTimeout("connectTimeout", true),
/* 101 */   continueBatchOnError("continueBatchOnError", true),
/* 102 */   createDatabaseIfNotExist("createDatabaseIfNotExist", true),
/* 103 */   customCharsetMapping("customCharsetMapping", true),
/* 104 */   databaseTerm("databaseTerm", true),
/* 105 */   defaultAuthenticationPlugin("defaultAuthenticationPlugin", true),
/* 106 */   defaultFetchSize("defaultFetchSize", true),
/* 107 */   detectCustomCollations("detectCustomCollations", true),
/* 108 */   disabledAuthenticationPlugins("disabledAuthenticationPlugins", true),
/* 109 */   disconnectOnExpiredPasswords("disconnectOnExpiredPasswords", true),
/* 110 */   dnsSrv("dnsSrv", true),
/* 111 */   dontCheckOnDuplicateKeyUpdateInSQL("dontCheckOnDuplicateKeyUpdateInSQL", true),
/* 112 */   dontTrackOpenResources("dontTrackOpenResources", true),
/* 113 */   dumpQueriesOnException("dumpQueriesOnException", true),
/* 114 */   elideSetAutoCommits("elideSetAutoCommits", true),
/* 115 */   emptyStringsConvertToZero("emptyStringsConvertToZero", true),
/* 116 */   emulateLocators("emulateLocators", true),
/* 117 */   emulateUnsupportedPstmts("emulateUnsupportedPstmts", true),
/* 118 */   enableEscapeProcessing("enableEscapeProcessing", true),
/* 119 */   enablePacketDebug("enablePacketDebug", true),
/* 120 */   enableQueryTimeouts("enableQueryTimeouts", true),
/* 121 */   exceptionInterceptors("exceptionInterceptors", true),
/* 122 */   explainSlowQueries("explainSlowQueries", true),
/* 123 */   failOverReadOnly("failOverReadOnly", true),
/* 124 */   fallbackToSystemKeyStore("fallbackToSystemKeyStore", true),
/* 125 */   fallbackToSystemTrustStore("fallbackToSystemTrustStore", true),
/* 126 */   forceConnectionTimeZoneToSession("forceConnectionTimeZoneToSession", true),
/* 127 */   functionsNeverReturnBlobs("functionsNeverReturnBlobs", true),
/* 128 */   gatherPerfMetrics("gatherPerfMetrics", true),
/* 129 */   generateSimpleParameterMetadata("generateSimpleParameterMetadata", true),
/* 130 */   getProceduresReturnsFunctions("getProceduresReturnsFunctions", true),
/* 131 */   ha_enableJMX("ha.enableJMX", "haEnableJMX", true),
/* 132 */   ha_loadBalanceStrategy("ha.loadBalanceStrategy", "haLoadBalanceStrategy", true),
/* 133 */   holdResultsOpenOverStatementClose("holdResultsOpenOverStatementClose", true),
/* 134 */   ignoreNonTxTables("ignoreNonTxTables", true),
/* 135 */   includeInnodbStatusInDeadlockExceptions("includeInnodbStatusInDeadlockExceptions", true),
/* 136 */   includeThreadDumpInDeadlockExceptions("includeThreadDumpInDeadlockExceptions", true),
/* 137 */   includeThreadNamesAsStatementComment("includeThreadNamesAsStatementComment", true),
/* 138 */   initialTimeout("initialTimeout", true),
/* 139 */   interactiveClient("interactiveClient", true),
/* 140 */   jdbcCompliantTruncation("jdbcCompliantTruncation", true),
/* 141 */   largeRowSizeThreshold("largeRowSizeThreshold", true),
/* 142 */   ldapServerHostname("ldapServerHostname", true),
/* 143 */   loadBalanceAutoCommitStatementRegex("loadBalanceAutoCommitStatementRegex", true),
/* 144 */   loadBalanceAutoCommitStatementThreshold("loadBalanceAutoCommitStatementThreshold", true),
/* 145 */   loadBalanceBlocklistTimeout("loadBalanceBlocklistTimeout", "loadBalanceBlacklistTimeout", true),
/* 146 */   loadBalanceConnectionGroup("loadBalanceConnectionGroup", true),
/* 147 */   loadBalanceExceptionChecker("loadBalanceExceptionChecker", true),
/* 148 */   loadBalanceHostRemovalGracePeriod("loadBalanceHostRemovalGracePeriod", true),
/* 149 */   loadBalancePingTimeout("loadBalancePingTimeout", true),
/* 150 */   loadBalanceSQLExceptionSubclassFailover("loadBalanceSQLExceptionSubclassFailover", true),
/* 151 */   loadBalanceSQLStateFailover("loadBalanceSQLStateFailover", true),
/* 152 */   loadBalanceValidateConnectionOnSwapServer("loadBalanceValidateConnectionOnSwapServer", true),
/* 153 */   localSocketAddress("localSocketAddress", true),
/* 154 */   locatorFetchBufferSize("locatorFetchBufferSize", true),
/* 155 */   logger("logger", true),
/* 156 */   logSlowQueries("logSlowQueries", true),
/* 157 */   logXaCommands("logXaCommands", true),
/* 158 */   maintainTimeStats("maintainTimeStats", true),
/* 159 */   maxAllowedPacket("maxAllowedPacket", true),
/* 160 */   maxQuerySizeToLog("maxQuerySizeToLog", true),
/* 161 */   maxReconnects("maxReconnects", true),
/* 162 */   maxRows("maxRows", true),
/* 163 */   metadataCacheSize("metadataCacheSize", true),
/* 164 */   netTimeoutForStreamingResults("netTimeoutForStreamingResults", true),
/* 165 */   noAccessToProcedureBodies("noAccessToProcedureBodies", true),
/* 166 */   noDatetimeStringSync("noDatetimeStringSync", true),
/* 167 */   nullDatabaseMeansCurrent("nullDatabaseMeansCurrent", "nullCatalogMeansCurrent", true),
/* 168 */   ociConfigFile("ociConfigFile", true),
/* 169 */   overrideSupportsIntegrityEnhancementFacility("overrideSupportsIntegrityEnhancementFacility", true),
/* 170 */   packetDebugBufferSize("packetDebugBufferSize", true),
/* 171 */   padCharsWithSpace("padCharsWithSpace", true),
/* 172 */   paranoid("paranoid", false),
/* 173 */   parseInfoCacheFactory("parseInfoCacheFactory", true),
/* 174 */   password1("password1", true),
/* 175 */   password2("password2", true),
/* 176 */   password3("password3", true),
/* 177 */   passwordCharacterEncoding("passwordCharacterEncoding", true),
/* 178 */   pedantic("pedantic", true),
/* 179 */   pinGlobalTxToPhysicalConnection("pinGlobalTxToPhysicalConnection", true),
/* 180 */   populateInsertRowWithDefaultValues("populateInsertRowWithDefaultValues", true),
/* 181 */   prepStmtCacheSize("prepStmtCacheSize", true),
/* 182 */   prepStmtCacheSqlLimit("prepStmtCacheSqlLimit", true),
/* 183 */   preserveInstants("preserveInstants", true),
/* 184 */   processEscapeCodesForPrepStmts("processEscapeCodesForPrepStmts", true),
/* 185 */   profilerEventHandler("profilerEventHandler", true),
/* 186 */   profileSQL("profileSQL", true),
/* 187 */   propertiesTransform("propertiesTransform", true),
/* 188 */   queriesBeforeRetrySource("queriesBeforeRetrySource", "queriesBeforeRetryMaster", true),
/* 189 */   queryInterceptors("queryInterceptors", true),
/* 190 */   queryTimeoutKillsConnection("queryTimeoutKillsConnection", true),
/* 191 */   readFromSourceWhenNoReplicas("readFromSourceWhenNoReplicas", "readFromMasterWhenNoSlaves", true),
/* 192 */   readOnlyPropagatesToServer("readOnlyPropagatesToServer", true),
/* 193 */   reconnectAtTxEnd("reconnectAtTxEnd", true),
/* 194 */   replicationConnectionGroup("replicationConnectionGroup", true),
/* 195 */   reportMetricsIntervalMillis("reportMetricsIntervalMillis", true),
/* 196 */   requireSSL("requireSSL", true),
/* 197 */   resourceId("resourceId", true),
/* 198 */   resultSetSizeThreshold("resultSetSizeThreshold", true),
/* 199 */   retriesAllDown("retriesAllDown", true),
/* 200 */   rewriteBatchedStatements("rewriteBatchedStatements", true),
/* 201 */   rollbackOnPooledClose("rollbackOnPooledClose", true),
/* 202 */   scrollTolerantForwardOnly("scrollTolerantForwardOnly", true),
/* 203 */   secondsBeforeRetrySource("secondsBeforeRetrySource", "secondsBeforeRetryMaster", true),
/* 204 */   selfDestructOnPingMaxOperations("selfDestructOnPingMaxOperations", true),
/* 205 */   selfDestructOnPingSecondsLifetime("selfDestructOnPingSecondsLifetime", true),
/* 206 */   sendFractionalSeconds("sendFractionalSeconds", true),
/* 207 */   sendFractionalSecondsForTime("sendFractionalSecondsForTime", true),
/* 208 */   serverAffinityOrder("serverAffinityOrder", true),
/* 209 */   serverConfigCacheFactory("serverConfigCacheFactory", true),
/* 210 */   serverRSAPublicKeyFile("serverRSAPublicKeyFile", true),
/* 211 */   sessionVariables("sessionVariables", true),
/* 212 */   slowQueryThresholdMillis("slowQueryThresholdMillis", true),
/* 213 */   slowQueryThresholdNanos("slowQueryThresholdNanos", true),
/* 214 */   socketFactory("socketFactory", true),
/* 215 */   socketTimeout("socketTimeout", true),
/* 216 */   socksProxyHost("socksProxyHost", true),
/* 217 */   socksProxyPort("socksProxyPort", true),
/* 218 */   sslMode("sslMode", true),
/* 219 */   strictUpdates("strictUpdates", true),
/* 220 */   tcpKeepAlive("tcpKeepAlive", true),
/* 221 */   tcpNoDelay("tcpNoDelay", true),
/* 222 */   tcpRcvBuf("tcpRcvBuf", true),
/* 223 */   tcpSndBuf("tcpSndBuf", true),
/* 224 */   tcpTrafficClass("tcpTrafficClass", true),
/* 225 */   tinyInt1isBit("tinyInt1isBit", true),
/* 226 */   tlsCiphersuites("tlsCiphersuites", "enabledSSLCipherSuites", true),
/* 227 */   tlsVersions("tlsVersions", "enabledTLSProtocols", true),
/* 228 */   traceProtocol("traceProtocol", true),
/* 229 */   trackSessionState("trackSessionState", true),
/* 230 */   transformedBitIsBoolean("transformedBitIsBoolean", true),
/* 231 */   treatUtilDateAsTimestamp("treatUtilDateAsTimestamp", true),
/* 232 */   trustCertificateKeyStorePassword("trustCertificateKeyStorePassword", true),
/* 233 */   trustCertificateKeyStoreType("trustCertificateKeyStoreType", true),
/* 234 */   trustCertificateKeyStoreUrl("trustCertificateKeyStoreUrl", true),
/* 235 */   ultraDevHack("ultraDevHack", true),
/* 236 */   useAffectedRows("useAffectedRows", true),
/* 237 */   useColumnNamesInFindColumn("useColumnNamesInFindColumn", true),
/* 238 */   useCompression("useCompression", true),
/* 239 */   useConfigs("useConfigs", true),
/* 240 */   useCursorFetch("useCursorFetch", true),
/* 241 */   useHostsInPrivileges("useHostsInPrivileges", true),
/* 242 */   useInformationSchema("useInformationSchema", true),
/* 243 */   useLocalSessionState("useLocalSessionState", true),
/* 244 */   useLocalTransactionState("useLocalTransactionState", true),
/* 245 */   useNanosForElapsedTime("useNanosForElapsedTime", true),
/* 246 */   useOldAliasMetadataBehavior("useOldAliasMetadataBehavior", true),
/* 247 */   useOnlyServerErrorMessages("useOnlyServerErrorMessages", true),
/* 248 */   useReadAheadInput("useReadAheadInput", true),
/* 249 */   useServerPrepStmts("useServerPrepStmts", true),
/* 250 */   useSSL("useSSL", true),
/* 251 */   useStreamLengthsInPrepStmts("useStreamLengthsInPrepStmts", true),
/* 252 */   useUnbufferedInput("useUnbufferedInput", true),
/* 253 */   useUsageAdvisor("useUsageAdvisor", true),
/* 254 */   verifyServerCertificate("verifyServerCertificate", true),
/*     */   
/* 256 */   xdevapiAsyncResponseTimeout("xdevapi.asyncResponseTimeout", "xdevapiAsyncResponseTimeout", true),
/* 257 */   xdevapiAuth("xdevapi.auth", "xdevapiAuth", true),
/* 258 */   xdevapiConnectTimeout("xdevapi.connect-timeout", "xdevapiConnectTimeout", true),
/* 259 */   xdevapiConnectionAttributes("xdevapi.connection-attributes", "xdevapiConnectionAttributes", true),
/* 260 */   xdevapiCompression("xdevapi.compression", "xdevapiCompression", true),
/* 261 */   xdevapiCompressionAlgorithms("xdevapi.compression-algorithms", "xdevapiCompressionAlgorithms", true),
/* 262 */   xdevapiCompressionExtensions("xdevapi.compression-extensions", "xdevapiCompressionExtensions", true),
/* 263 */   xdevapiDnsSrv("xdevapi.dns-srv", "xdevapiDnsSrv", true),
/* 264 */   xdevapiFallbackToSystemKeyStore("xdevapi.fallback-to-system-keystore", "xdevapiFallbackToSystemKeyStore", true),
/* 265 */   xdevapiFallbackToSystemTrustStore("xdevapi.fallback-to-system-truststore", "xdevapiFallbackToSystemTrustStore", true),
/* 266 */   xdevapiSslKeyStorePassword("xdevapi.ssl-keystore-password", "xdevapiSslKeystorePassword", true),
/* 267 */   xdevapiSslKeyStoreType("xdevapi.ssl-keystore-type", "xdevapiSslKeystoreType", true),
/* 268 */   xdevapiSslKeyStoreUrl("xdevapi.ssl-keystore", "xdevapiSslKeystore", true),
/* 269 */   xdevapiSslMode("xdevapi.ssl-mode", "xdevapiSslMode", true),
/* 270 */   xdevapiSslTrustStorePassword("xdevapi.ssl-truststore-password", "xdevapiSslTruststorePassword", true),
/* 271 */   xdevapiSslTrustStoreType("xdevapi.ssl-truststore-type", "xdevapiSslTruststoreType", true),
/* 272 */   xdevapiSslTrustStoreUrl("xdevapi.ssl-truststore", "xdevapiSslTruststore", true),
/* 273 */   xdevapiTlsCiphersuites("xdevapi.tls-ciphersuites", "xdevapiTlsCiphersuites", true),
/* 274 */   xdevapiTlsVersions("xdevapi.tls-versions", "xdevapiTlsVersions", true),
/*     */   
/* 276 */   yearIsDateType("yearIsDateType", true),
/* 277 */   zeroDateTimeBehavior("zeroDateTimeBehavior", true);
/*     */ 
/*     */ 
/*     */   
/* 281 */   private String ccAlias = null;
/*     */   
/*     */   static {
/* 284 */     caseInsensitiveValues = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
/*     */     
/* 286 */     for (PropertyKey pk : values()) {
/* 287 */       if (!pk.isCaseSensitive) {
/* 288 */         caseInsensitiveValues.put(pk.getKeyName(), pk);
/* 289 */         if (pk.getCcAlias() != null) {
/* 290 */           caseInsensitiveValues.put(pk.getCcAlias(), pk);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isCaseSensitive = false;
/*     */   
/*     */   private String keyName;
/*     */   
/*     */   private static Map<String, PropertyKey> caseInsensitiveValues;
/*     */ 
/*     */   
/*     */   PropertyKey(String keyName, boolean isCaseSensitive) {
/* 305 */     this.keyName = keyName;
/* 306 */     this.isCaseSensitive = isCaseSensitive;
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
/*     */   PropertyKey(String keyName, String alias, boolean isCaseSensitive) {
/* 321 */     this.ccAlias = alias;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 326 */     return this.keyName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getKeyName() {
/* 336 */     return this.keyName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCcAlias() {
/* 346 */     return this.ccAlias;
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
/*     */   public static PropertyKey fromValue(String value) {
/* 358 */     for (PropertyKey k : values()) {
/* 359 */       if (k.isCaseSensitive) {
/* 360 */         if (k.getKeyName().equals(value) || (k.getCcAlias() != null && k.getCcAlias().equals(value))) {
/* 361 */           return k;
/*     */         }
/*     */       }
/* 364 */       else if (k.getKeyName().equalsIgnoreCase(value) || (k.getCcAlias() != null && k.getCcAlias().equalsIgnoreCase(value))) {
/* 365 */         return k;
/*     */       } 
/*     */     } 
/*     */     
/* 369 */     return null;
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
/*     */   public static String normalizeCase(String keyName) {
/* 381 */     PropertyKey pk = caseInsensitiveValues.get(keyName);
/* 382 */     return (pk == null) ? keyName : pk.getKeyName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\PropertyKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */