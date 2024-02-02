/*     */ package com.mysql.cj.conf;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.PerConnectionLRUFactory;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.log.StandardLogger;
/*     */ import com.mysql.cj.util.PerVmServerConfigCacheFactory;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PropertyDefinitions
/*     */ {
/*     */   public static final String SYSP_line_separator = "line.separator";
/*     */   public static final String SYSP_java_vendor = "java.vendor";
/*     */   public static final String SYSP_java_version = "java.version";
/*     */   public static final String SYSP_java_vm_vendor = "java.vm.vendor";
/*     */   public static final String SYSP_os_name = "os.name";
/*     */   public static final String SYSP_os_arch = "os.arch";
/*     */   public static final String SYSP_os_version = "os.version";
/*     */   public static final String SYSP_file_encoding = "file.encoding";
/*     */   public static final String SYSP_disableAbandonedConnectionCleanup = "com.mysql.cj.disableAbandonedConnectionCleanup";
/*     */   public static final String SYSP_testsuite_url = "com.mysql.cj.testsuite.url";
/*     */   public static final String SYSP_testsuite_url_cluster = "com.mysql.cj.testsuite.url.cluster";
/*     */   public static final String SYSP_testsuite_url_mysqlx = "com.mysql.cj.testsuite.mysqlx.url";
/*     */   public static final String SYSP_testsuite_cantGrant = "com.mysql.cj.testsuite.cantGrant";
/*     */   public static final String SYSP_testsuite_unavailable_host = "com.mysql.cj.testsuite.unavailable.host";
/*     */   public static final String SYSP_testsuite_ds_host = "com.mysql.cj.testsuite.ds.host";
/*     */   public static final String SYSP_testsuite_ds_port = "com.mysql.cj.testsuite.ds.port";
/*     */   public static final String SYSP_testsuite_ds_db = "com.mysql.cj.testsuite.ds.db";
/*     */   public static final String SYSP_testsuite_ds_user = "com.mysql.cj.testsuite.ds.user";
/*     */   public static final String SYSP_testsuite_ds_password = "com.mysql.cj.testsuite.ds.password";
/*     */   public static final String SYSP_testsuite_loadstoreperf_tabletype = "com.mysql.cj.testsuite.loadstoreperf.tabletype";
/*     */   public static final String SYSP_testsuite_loadstoreperf_useBigResults = "com.mysql.cj.testsuite.loadstoreperf.useBigResults";
/*     */   public static final String SYSP_testsuite_miniAdminTest_runShutdown = "com.mysql.cj.testsuite.miniAdminTest.runShutdown";
/*     */   public static final String SYSP_testsuite_noDebugOutput = "com.mysql.cj.testsuite.noDebugOutput";
/*     */   public static final String SYSP_testsuite_retainArtifacts = "com.mysql.cj.testsuite.retainArtifacts";
/*     */   public static final String SYSP_testsuite_runLongTests = "com.mysql.cj.testsuite.runLongTests";
/*     */   public static final String SYSP_testsuite_serverController_basedir = "com.mysql.cj.testsuite.serverController.basedir";
/*     */   public static final String SYSP_com_mysql_cj_build_verbose = "com.mysql.cj.build.verbose";
/* 105 */   public static final String CATEGORY_AUTH = Messages.getString("ConnectionProperties.categoryAuthentication");
/* 106 */   public static final String CATEGORY_CONNECTION = Messages.getString("ConnectionProperties.categoryConnection");
/* 107 */   public static final String CATEGORY_SESSION = Messages.getString("ConnectionProperties.categorySession");
/* 108 */   public static final String CATEGORY_NETWORK = Messages.getString("ConnectionProperties.categoryNetworking");
/* 109 */   public static final String CATEGORY_SECURITY = Messages.getString("ConnectionProperties.categorySecurity");
/* 110 */   public static final String CATEGORY_STATEMENTS = Messages.getString("ConnectionProperties.categoryStatements");
/* 111 */   public static final String CATEGORY_PREPARED_STATEMENTS = Messages.getString("ConnectionProperties.categoryPreparedStatements");
/* 112 */   public static final String CATEGORY_RESULT_SETS = Messages.getString("ConnectionProperties.categoryResultSets");
/* 113 */   public static final String CATEGORY_METADATA = Messages.getString("ConnectionProperties.categoryMetadata");
/* 114 */   public static final String CATEGORY_BLOBS = Messages.getString("ConnectionProperties.categoryBlobs");
/* 115 */   public static final String CATEGORY_DATETIMES = Messages.getString("ConnectionProperties.categoryDatetimes");
/* 116 */   public static final String CATEGORY_HA = Messages.getString("ConnectionProperties.categoryHA");
/* 117 */   public static final String CATEGORY_PERFORMANCE = Messages.getString("ConnectionProperties.categoryPerformance");
/* 118 */   public static final String CATEGORY_DEBUGING_PROFILING = Messages.getString("ConnectionProperties.categoryDebuggingProfiling");
/* 119 */   public static final String CATEGORY_EXCEPTIONS = Messages.getString("ConnectionProperties.categoryExceptions");
/* 120 */   public static final String CATEGORY_INTEGRATION = Messages.getString("ConnectionProperties.categoryIntegration");
/* 121 */   public static final String CATEGORY_JDBC = Messages.getString("ConnectionProperties.categoryJDBC");
/* 122 */   public static final String CATEGORY_XDEVAPI = Messages.getString("ConnectionProperties.categoryXDevAPI");
/* 123 */   public static final String CATEGORY_USER_DEFINED = Messages.getString("ConnectionProperties.categoryUserDefined");
/*     */   
/* 125 */   public static final String[] PROPERTY_CATEGORIES = new String[] { CATEGORY_AUTH, CATEGORY_CONNECTION, CATEGORY_SESSION, CATEGORY_NETWORK, CATEGORY_SECURITY, CATEGORY_STATEMENTS, CATEGORY_PREPARED_STATEMENTS, CATEGORY_RESULT_SETS, CATEGORY_METADATA, CATEGORY_BLOBS, CATEGORY_DATETIMES, CATEGORY_HA, CATEGORY_PERFORMANCE, CATEGORY_DEBUGING_PROFILING, CATEGORY_EXCEPTIONS, CATEGORY_INTEGRATION, CATEGORY_JDBC, CATEGORY_XDEVAPI };
/*     */ 
/*     */   
/*     */   public static final boolean DEFAULT_VALUE_TRUE = true;
/*     */ 
/*     */   
/*     */   public static final boolean DEFAULT_VALUE_FALSE = false;
/*     */ 
/*     */   
/* 134 */   public static final String DEFAULT_VALUE_NULL_STRING = null;
/* 135 */   public static final String NO_ALIAS = null;
/*     */ 
/*     */   
/*     */   public static final boolean RUNTIME_MODIFIABLE = true;
/*     */   
/*     */   public static final boolean RUNTIME_NOT_MODIFIABLE = false;
/*     */   
/*     */   public static final Map<PropertyKey, PropertyDefinition<?>> PROPERTY_KEY_TO_PROPERTY_DEFINITION;
/*     */ 
/*     */   
/*     */   public enum ZeroDatetimeBehavior
/*     */   {
/* 147 */     CONVERT_TO_NULL, EXCEPTION, ROUND;
/*     */   }
/*     */   
/*     */   public enum SslMode {
/* 151 */     PREFERRED, REQUIRED, VERIFY_CA, VERIFY_IDENTITY, DISABLED;
/*     */   }
/*     */   
/*     */   public enum XdevapiSslMode {
/* 155 */     REQUIRED, VERIFY_CA, VERIFY_IDENTITY, DISABLED;
/*     */   }
/*     */   
/*     */   public enum AuthMech {
/* 159 */     PLAIN, MYSQL41, SHA256_MEMORY, EXTERNAL;
/*     */   }
/*     */   
/*     */   public enum Compression {
/* 163 */     PREFERRED, REQUIRED, DISABLED;
/*     */   }
/*     */   
/*     */   public enum DatabaseTerm {
/* 167 */     CATALOG, SCHEMA;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 176 */     String STANDARD_LOGGER_NAME = StandardLogger.class.getName();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 868 */     PropertyDefinition[] arrayOfPropertyDefinition = { new StringPropertyDefinition(PropertyKey.USER, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.Username"), Messages.getString("ConnectionProperties.allVersions"), CATEGORY_AUTH, -2147483647), new StringPropertyDefinition(PropertyKey.PASSWORD, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.Password"), Messages.getString("ConnectionProperties.allVersions"), CATEGORY_AUTH, -2147483646), new StringPropertyDefinition(PropertyKey.password1, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.Password1"), "8.0.27", CATEGORY_AUTH, -2147483645), new StringPropertyDefinition(PropertyKey.password2, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.Password2"), "8.0.27", CATEGORY_AUTH, -2147483644), new StringPropertyDefinition(PropertyKey.password3, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.Password3"), "8.0.27", CATEGORY_AUTH, -2147483643), new StringPropertyDefinition(PropertyKey.authenticationPlugins, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.authenticationPlugins"), "5.1.19", CATEGORY_AUTH, -2147483642), new StringPropertyDefinition(PropertyKey.disabledAuthenticationPlugins, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.disabledAuthenticationPlugins"), "5.1.19", CATEGORY_AUTH, -2147483641), new StringPropertyDefinition(PropertyKey.defaultAuthenticationPlugin, "mysql_native_password", true, Messages.getString("ConnectionProperties.defaultAuthenticationPlugin"), "5.1.19", CATEGORY_AUTH, -2147483640), new StringPropertyDefinition(PropertyKey.ldapServerHostname, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.ldapServerHostname"), "8.0.23", CATEGORY_AUTH, -2147483639), new StringPropertyDefinition(PropertyKey.ociConfigFile, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.ociConfigFile"), "8.0.27", CATEGORY_AUTH, -2147483641), new StringPropertyDefinition(PropertyKey.passwordCharacterEncoding, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.passwordCharacterEncoding"), "5.1.7", CATEGORY_CONNECTION, -2147483648), new StringPropertyDefinition(PropertyKey.connectionAttributes, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.connectionAttributes"), "5.1.25", CATEGORY_CONNECTION, 7), new StringPropertyDefinition(PropertyKey.clientInfoProvider, "com.mysql.cj.jdbc.CommentClientInfoProvider", true, Messages.getString("ConnectionProperties.clientInfoProvider"), "5.1.0", CATEGORY_CONNECTION, -2147483648), new StringPropertyDefinition(PropertyKey.connectionLifecycleInterceptors, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.connectionLifecycleInterceptors"), "5.1.4", CATEGORY_CONNECTION, 2147483647), new BooleanPropertyDefinition(PropertyKey.createDatabaseIfNotExist, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.createDatabaseIfNotExist"), "3.1.9", CATEGORY_CONNECTION, -2147483648), new BooleanPropertyDefinition(PropertyKey.interactiveClient, Boolean.valueOf(false), false, Messages.getString("ConnectionProperties.interactiveClient"), "3.1.0", CATEGORY_CONNECTION, -2147483648), new StringPropertyDefinition(PropertyKey.propertiesTransform, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.connectionPropertiesTransform"), "3.1.4", CATEGORY_CONNECTION, -2147483648), new BooleanPropertyDefinition(PropertyKey.rollbackOnPooledClose, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.rollbackOnPooledClose"), "3.0.15", CATEGORY_CONNECTION, -2147483648), new StringPropertyDefinition(PropertyKey.useConfigs, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.useConfigs"), "3.1.5", CATEGORY_CONNECTION, 2147483647), new BooleanPropertyDefinition(PropertyKey.useAffectedRows, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.useAffectedRows"), "5.1.7", CATEGORY_CONNECTION, -2147483648), new BooleanPropertyDefinition(PropertyKey.disconnectOnExpiredPasswords, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.disconnectOnExpiredPasswords"), "5.1.23", CATEGORY_CONNECTION, -2147483648), new BooleanPropertyDefinition(PropertyKey.detectCustomCollations, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.detectCustomCollations"), "5.1.29", CATEGORY_CONNECTION, -2147483648), new EnumPropertyDefinition<>(PropertyKey.databaseTerm, DatabaseTerm.CATALOG, true, Messages.getString("ConnectionProperties.databaseTerm"), "8.0.17", CATEGORY_CONNECTION, -2147483648), new StringPropertyDefinition(PropertyKey.characterEncoding, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.characterEncoding"), "1.1g", CATEGORY_SESSION, -2147483648), new StringPropertyDefinition(PropertyKey.characterSetResults, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.characterSetResults"), "3.0.13", CATEGORY_SESSION, -2147483648), new StringPropertyDefinition(PropertyKey.customCharsetMapping, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.customCharsetMapping"), "8.0.26", CATEGORY_SESSION, -2147483648), new StringPropertyDefinition(PropertyKey.connectionCollation, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.connectionCollation"), "3.0.13", CATEGORY_SESSION, -2147483648), new StringPropertyDefinition(PropertyKey.sessionVariables, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.sessionVariables"), "3.1.8", CATEGORY_SESSION, 2147483647), new BooleanPropertyDefinition(PropertyKey.trackSessionState, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.trackSessionState"), "8.0.26", CATEGORY_SESSION, -2147483648), new BooleanPropertyDefinition(PropertyKey.useUnbufferedInput, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.useUnbufferedInput"), "3.0.11", CATEGORY_NETWORK, -2147483648), new IntegerPropertyDefinition(PropertyKey.connectTimeout, 0, true, Messages.getString("ConnectionProperties.connectTimeout"), "3.0.1", CATEGORY_NETWORK, 9, 0, 2147483647), new StringPropertyDefinition(PropertyKey.localSocketAddress, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.localSocketAddress"), "5.0.5", CATEGORY_NETWORK, -2147483648), new StringPropertyDefinition(PropertyKey.socketFactory, "com.mysql.cj.protocol.StandardSocketFactory", true, Messages.getString("ConnectionProperties.socketFactory"), "3.0.3", CATEGORY_NETWORK, 4), new StringPropertyDefinition(PropertyKey.socksProxyHost, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.socksProxyHost"), "5.1.34", CATEGORY_NETWORK, 1), new IntegerPropertyDefinition(PropertyKey.socksProxyPort, 1080, true, Messages.getString("ConnectionProperties.socksProxyPort"), "5.1.34", CATEGORY_NETWORK, 2, 0, 65535), new IntegerPropertyDefinition(PropertyKey.socketTimeout, 0, true, Messages.getString("ConnectionProperties.socketTimeout"), "3.0.1", CATEGORY_NETWORK, 10, 0, 2147483647), new BooleanPropertyDefinition(PropertyKey.tcpNoDelay, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.tcpNoDelay"), "5.0.7", CATEGORY_NETWORK, -2147483648), new BooleanPropertyDefinition(PropertyKey.tcpKeepAlive, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.tcpKeepAlive"), "5.0.7", CATEGORY_NETWORK, -2147483648), new IntegerPropertyDefinition(PropertyKey.tcpRcvBuf, 0, true, Messages.getString("ConnectionProperties.tcpSoRcvBuf"), "5.0.7", CATEGORY_NETWORK, -2147483648, 0, 2147483647), new IntegerPropertyDefinition(PropertyKey.tcpSndBuf, 0, true, Messages.getString("ConnectionProperties.tcpSoSndBuf"), "5.0.7", CATEGORY_NETWORK, -2147483648, 0, 2147483647), new IntegerPropertyDefinition(PropertyKey.tcpTrafficClass, 0, true, Messages.getString("ConnectionProperties.tcpTrafficClass"), "5.0.7", CATEGORY_NETWORK, -2147483648, 0, 255), new BooleanPropertyDefinition(PropertyKey.useCompression, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.useCompression"), "3.0.17", CATEGORY_NETWORK, -2147483648), new IntegerPropertyDefinition(PropertyKey.maxAllowedPacket, 65535, true, Messages.getString("ConnectionProperties.maxAllowedPacket"), "5.1.8", CATEGORY_NETWORK, -2147483648), new BooleanPropertyDefinition(PropertyKey.dnsSrv, Boolean.valueOf(false), false, Messages.getString("ConnectionProperties.dnsSrv"), "8.0.19", CATEGORY_NETWORK, -2147483648), new BooleanPropertyDefinition(PropertyKey.paranoid, Boolean.valueOf(false), false, Messages.getString("ConnectionProperties.paranoid"), "3.0.1", CATEGORY_SECURITY, 1), new StringPropertyDefinition(PropertyKey.serverRSAPublicKeyFile, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.serverRSAPublicKeyFile"), "5.1.31", CATEGORY_SECURITY, 2), new BooleanPropertyDefinition(PropertyKey.allowPublicKeyRetrieval, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.allowPublicKeyRetrieval"), "5.1.31", CATEGORY_SECURITY, 3), new EnumPropertyDefinition<>(PropertyKey.sslMode, SslMode.PREFERRED, true, Messages.getString("ConnectionProperties.sslMode"), "8.0.13", CATEGORY_SECURITY, 4), new StringPropertyDefinition(PropertyKey.trustCertificateKeyStoreUrl, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.trustCertificateKeyStoreUrl"), "5.1.0", CATEGORY_SECURITY, 5), new StringPropertyDefinition(PropertyKey.trustCertificateKeyStoreType, "JKS", true, Messages.getString("ConnectionProperties.trustCertificateKeyStoreType"), "5.1.0", CATEGORY_SECURITY, 6), new StringPropertyDefinition(PropertyKey.trustCertificateKeyStorePassword, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.trustCertificateKeyStorePassword"), "5.1.0", CATEGORY_SECURITY, 7), new BooleanPropertyDefinition(PropertyKey.fallbackToSystemTrustStore, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.fallbackToSystemTrustStore"), "8.0.22", CATEGORY_SECURITY, 8), new StringPropertyDefinition(PropertyKey.clientCertificateKeyStoreUrl, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.clientCertificateKeyStoreUrl"), "5.1.0", CATEGORY_SECURITY, 9), new StringPropertyDefinition(PropertyKey.clientCertificateKeyStoreType, "JKS", true, Messages.getString("ConnectionProperties.clientCertificateKeyStoreType"), "5.1.0", CATEGORY_SECURITY, 10), new StringPropertyDefinition(PropertyKey.clientCertificateKeyStorePassword, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.clientCertificateKeyStorePassword"), "5.1.0", CATEGORY_SECURITY, 11), new BooleanPropertyDefinition(PropertyKey.fallbackToSystemKeyStore, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.fallbackToSystemKeyStore"), "8.0.22", CATEGORY_SECURITY, 12), new StringPropertyDefinition(PropertyKey.tlsCiphersuites, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.tlsCiphersuites"), "5.1.35", CATEGORY_SECURITY, 13), new StringPropertyDefinition(PropertyKey.tlsVersions, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.tlsVersions"), "8.0.8", CATEGORY_SECURITY, 14), new BooleanPropertyDefinition(PropertyKey.allowLoadLocalInfile, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.loadDataLocal"), "3.0.3", CATEGORY_SECURITY, 2147483647), new StringPropertyDefinition(PropertyKey.allowLoadLocalInfileInPath, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.loadDataLocalInPath"), "8.0.22", CATEGORY_SECURITY, 2147483647), new BooleanPropertyDefinition(PropertyKey.allowMultiQueries, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.allowMultiQueries"), "3.1.1", CATEGORY_SECURITY, 2147483647), new BooleanPropertyDefinition(PropertyKey.allowUrlInLocalInfile, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.allowUrlInLoadLocal"), "3.1.4", CATEGORY_SECURITY, 2147483647), new BooleanPropertyDefinition(PropertyKey.useSSL, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.useSSL"), "3.0.2", CATEGORY_SECURITY, 2147483647), new BooleanPropertyDefinition(PropertyKey.requireSSL, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.requireSSL"), "3.1.0", CATEGORY_SECURITY, 2147483647), new BooleanPropertyDefinition(PropertyKey.verifyServerCertificate, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.verifyServerCertificate"), "5.1.6", CATEGORY_SECURITY, 2147483647), new BooleanPropertyDefinition(PropertyKey.continueBatchOnError, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.continueBatchOnError"), "3.0.3", CATEGORY_STATEMENTS, -2147483648), new BooleanPropertyDefinition(PropertyKey.dontTrackOpenResources, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.dontTrackOpenResources"), "3.1.7", CATEGORY_STATEMENTS, -2147483648), new BooleanPropertyDefinition(PropertyKey.queryTimeoutKillsConnection, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.queryTimeoutKillsConnection"), "5.1.9", CATEGORY_STATEMENTS, -2147483648), new StringPropertyDefinition(PropertyKey.queryInterceptors, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.queryInterceptors"), "8.0.7", CATEGORY_STATEMENTS, -2147483648), new BooleanPropertyDefinition(PropertyKey.cacheDefaultTimeZone, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.cacheDefaultTimeZone"), "8.0.20", CATEGORY_STATEMENTS, -2147483648), new BooleanPropertyDefinition(PropertyKey.allowNanAndInf, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.allowNANandINF"), "3.1.5", CATEGORY_PREPARED_STATEMENTS, -2147483648), new BooleanPropertyDefinition(PropertyKey.autoClosePStmtStreams, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.autoClosePstmtStreams"), "3.1.12", CATEGORY_PREPARED_STATEMENTS, -2147483648), new BooleanPropertyDefinition(PropertyKey.compensateOnDuplicateKeyUpdateCounts, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.compensateOnDuplicateKeyUpdateCounts"), "5.1.7", CATEGORY_PREPARED_STATEMENTS, -2147483648), new BooleanPropertyDefinition(PropertyKey.useServerPrepStmts, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.useServerPrepStmts"), "3.1.0", CATEGORY_PREPARED_STATEMENTS, -2147483648), new BooleanPropertyDefinition(PropertyKey.emulateUnsupportedPstmts, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.emulateUnsupportedPstmts"), "3.1.7", CATEGORY_PREPARED_STATEMENTS, -2147483648), new BooleanPropertyDefinition(PropertyKey.generateSimpleParameterMetadata, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.generateSimpleParameterMetadata"), "5.0.5", CATEGORY_PREPARED_STATEMENTS, -2147483648), new BooleanPropertyDefinition(PropertyKey.processEscapeCodesForPrepStmts, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.processEscapeCodesForPrepStmts"), "3.1.12", CATEGORY_PREPARED_STATEMENTS, -2147483648), new BooleanPropertyDefinition(PropertyKey.useStreamLengthsInPrepStmts, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.useStreamLengthsInPrepStmts"), "3.0.2", CATEGORY_PREPARED_STATEMENTS, -2147483648), new BooleanPropertyDefinition(PropertyKey.clobberStreamingResults, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.clobberStreamingResults"), "3.0.9", CATEGORY_RESULT_SETS, -2147483648), new BooleanPropertyDefinition(PropertyKey.emptyStringsConvertToZero, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.emptyStringsConvertToZero"), "3.1.8", CATEGORY_RESULT_SETS, -2147483648), new BooleanPropertyDefinition(PropertyKey.holdResultsOpenOverStatementClose, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.holdRSOpenOverStmtClose"), "3.1.7", CATEGORY_RESULT_SETS, -2147483648), new BooleanPropertyDefinition(PropertyKey.jdbcCompliantTruncation, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.jdbcCompliantTruncation"), "3.1.2", CATEGORY_RESULT_SETS, -2147483648), new IntegerPropertyDefinition(PropertyKey.maxRows, -1, true, Messages.getString("ConnectionProperties.maxRows"), Messages.getString("ConnectionProperties.allVersions"), CATEGORY_RESULT_SETS, -2147483648, -1, 2147483647), new IntegerPropertyDefinition(PropertyKey.netTimeoutForStreamingResults, 600, true, Messages.getString("ConnectionProperties.netTimeoutForStreamingResults"), "5.1.0", CATEGORY_RESULT_SETS, -2147483648, 0, 2147483647), new BooleanPropertyDefinition(PropertyKey.padCharsWithSpace, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.padCharsWithSpace"), "5.0.6", CATEGORY_RESULT_SETS, -2147483648), new BooleanPropertyDefinition(PropertyKey.populateInsertRowWithDefaultValues, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.populateInsertRowWithDefaultValues"), "5.0.5", CATEGORY_RESULT_SETS, -2147483648), new BooleanPropertyDefinition(PropertyKey.strictUpdates, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.strictUpdates"), "3.0.4", CATEGORY_RESULT_SETS, -2147483648), new BooleanPropertyDefinition(PropertyKey.tinyInt1isBit, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.tinyInt1isBit"), "3.0.16", CATEGORY_RESULT_SETS, -2147483648), new BooleanPropertyDefinition(PropertyKey.transformedBitIsBoolean, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.transformedBitIsBoolean"), "3.1.9", CATEGORY_RESULT_SETS, -2147483648), new BooleanPropertyDefinition(PropertyKey.scrollTolerantForwardOnly, Boolean.valueOf(false), false, Messages.getString("ConnectionProperties.scrollTolerantForwardOnly"), "8.0.24", CATEGORY_RESULT_SETS, -2147483648), new BooleanPropertyDefinition(PropertyKey.noAccessToProcedureBodies, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.noAccessToProcedureBodies"), "5.0.3", CATEGORY_METADATA, -2147483648), new BooleanPropertyDefinition(PropertyKey.nullDatabaseMeansCurrent, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.nullCatalogMeansCurrent"), "3.1.8", CATEGORY_METADATA, -2147483648), new BooleanPropertyDefinition(PropertyKey.useHostsInPrivileges, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.useHostsInPrivileges"), "3.0.2", CATEGORY_METADATA, -2147483648), new BooleanPropertyDefinition(PropertyKey.useInformationSchema, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.useInformationSchema"), "5.0.0", CATEGORY_METADATA, -2147483648), new BooleanPropertyDefinition(PropertyKey.getProceduresReturnsFunctions, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.getProceduresReturnsFunctions"), "5.1.26", CATEGORY_METADATA, -2147483648), new BooleanPropertyDefinition(PropertyKey.autoDeserialize, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.autoDeserialize"), "3.1.5", CATEGORY_BLOBS, -2147483648), new MemorySizePropertyDefinition(PropertyKey.blobSendChunkSize, 1048576, true, Messages.getString("ConnectionProperties.blobSendChunkSize"), "3.1.9", CATEGORY_BLOBS, -2147483648, 0, 0), new BooleanPropertyDefinition(PropertyKey.blobsAreStrings, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.blobsAreStrings"), "5.0.8", CATEGORY_BLOBS, -2147483648), new BooleanPropertyDefinition(PropertyKey.functionsNeverReturnBlobs, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.functionsNeverReturnBlobs"), "5.0.8", CATEGORY_BLOBS, -2147483648), new StringPropertyDefinition(PropertyKey.clobCharacterEncoding, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.clobCharacterEncoding"), "5.0.0", CATEGORY_BLOBS, -2147483648), new BooleanPropertyDefinition(PropertyKey.emulateLocators, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.emulateLocators"), "3.1.0", CATEGORY_BLOBS, -2147483648), new MemorySizePropertyDefinition(PropertyKey.locatorFetchBufferSize, 1048576, true, Messages.getString("ConnectionProperties.locatorFetchBufferSize"), "3.2.1", CATEGORY_BLOBS, -2147483648, 0, 2147483647), new BooleanPropertyDefinition(PropertyKey.noDatetimeStringSync, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.noDatetimeStringSync"), "3.1.7", CATEGORY_DATETIMES, -2147483648), new StringPropertyDefinition(PropertyKey.connectionTimeZone, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.connectionTimeZone"), "3.0.2", CATEGORY_DATETIMES, -2147483648), new BooleanPropertyDefinition(PropertyKey.forceConnectionTimeZoneToSession, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.forceConnectionTimeZoneToSession"), "8.0.23", CATEGORY_DATETIMES, -2147483648), new BooleanPropertyDefinition(PropertyKey.preserveInstants, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.preserveInstants"), "8.0.23", CATEGORY_DATETIMES, -2147483648), new BooleanPropertyDefinition(PropertyKey.treatUtilDateAsTimestamp, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.treatUtilDateAsTimestamp"), "5.0.5", CATEGORY_DATETIMES, -2147483648), new BooleanPropertyDefinition(PropertyKey.sendFractionalSeconds, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.sendFractionalSeconds"), "5.1.37", CATEGORY_DATETIMES, -2147483648), new BooleanPropertyDefinition(PropertyKey.sendFractionalSecondsForTime, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.sendFractionalSecondsForTime"), "8.0.23", CATEGORY_DATETIMES, -2147483648), new BooleanPropertyDefinition(PropertyKey.yearIsDateType, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.yearIsDateType"), "3.1.9", CATEGORY_DATETIMES, -2147483648), new EnumPropertyDefinition<>(PropertyKey.zeroDateTimeBehavior, ZeroDatetimeBehavior.EXCEPTION, true, Messages.getString("ConnectionProperties.zeroDateTimeBehavior", new Object[] { ZeroDatetimeBehavior.EXCEPTION, ZeroDatetimeBehavior.ROUND, ZeroDatetimeBehavior.CONVERT_TO_NULL }), "3.1.4", CATEGORY_DATETIMES, -2147483648), new BooleanPropertyDefinition(PropertyKey.allowSourceDownConnections, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.allowSourceDownConnections"), "5.1.27", CATEGORY_HA, 2147483647), new BooleanPropertyDefinition(PropertyKey.allowReplicaDownConnections, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.allowReplicaDownConnections"), "6.0.2", CATEGORY_HA, 2147483647), new BooleanPropertyDefinition(PropertyKey.readFromSourceWhenNoReplicas, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.readFromSourceWhenNoReplicas"), "6.0.2", CATEGORY_HA, 2147483647), new BooleanPropertyDefinition(PropertyKey.autoReconnect, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.autoReconnect"), "1.1", CATEGORY_HA, 0), new BooleanPropertyDefinition(PropertyKey.autoReconnectForPools, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.autoReconnectForPools"), "3.1.3", CATEGORY_HA, 1), new BooleanPropertyDefinition(PropertyKey.failOverReadOnly, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.failoverReadOnly"), "3.0.12", CATEGORY_HA, 2), new IntegerPropertyDefinition(PropertyKey.initialTimeout, 2, false, Messages.getString("ConnectionProperties.initialTimeout"), "1.1", CATEGORY_HA, 5, 1, 2147483647), new StringPropertyDefinition(PropertyKey.ha_loadBalanceStrategy, "random", true, Messages.getString("ConnectionProperties.loadBalanceStrategy"), "5.0.6", CATEGORY_HA, -2147483648), new IntegerPropertyDefinition(PropertyKey.loadBalanceBlocklistTimeout, 0, true, Messages.getString("ConnectionProperties.loadBalanceBlocklistTimeout"), "5.1.0", CATEGORY_HA, -2147483648, 0, 2147483647), new IntegerPropertyDefinition(PropertyKey.loadBalancePingTimeout, 0, true, Messages.getString("ConnectionProperties.loadBalancePingTimeout"), "5.1.13", CATEGORY_HA, -2147483648, 0, 2147483647), new BooleanPropertyDefinition(PropertyKey.loadBalanceValidateConnectionOnSwapServer, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.loadBalanceValidateConnectionOnSwapServer"), "5.1.13", CATEGORY_HA, -2147483648), new StringPropertyDefinition(PropertyKey.loadBalanceConnectionGroup, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.loadBalanceConnectionGroup"), "5.1.13", CATEGORY_HA, -2147483648), new StringPropertyDefinition(PropertyKey.loadBalanceExceptionChecker, "com.mysql.cj.jdbc.ha.StandardLoadBalanceExceptionChecker", true, Messages.getString("ConnectionProperties.loadBalanceExceptionChecker"), "5.1.13", CATEGORY_HA, -2147483648), new StringPropertyDefinition(PropertyKey.loadBalanceSQLStateFailover, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.loadBalanceSQLStateFailover"), "5.1.13", CATEGORY_HA, -2147483648), new StringPropertyDefinition(PropertyKey.loadBalanceSQLExceptionSubclassFailover, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.loadBalanceSQLExceptionSubclassFailover"), "5.1.13", CATEGORY_HA, -2147483648), new StringPropertyDefinition(PropertyKey.loadBalanceAutoCommitStatementRegex, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.loadBalanceAutoCommitStatementRegex"), "5.1.15", CATEGORY_HA, -2147483648), new IntegerPropertyDefinition(PropertyKey.loadBalanceAutoCommitStatementThreshold, 0, true, Messages.getString("ConnectionProperties.loadBalanceAutoCommitStatementThreshold"), "5.1.15", CATEGORY_HA, -2147483648, 0, 2147483647), new IntegerPropertyDefinition(PropertyKey.maxReconnects, 3, true, Messages.getString("ConnectionProperties.maxReconnects"), "1.1", CATEGORY_HA, 4, 1, 2147483647), new IntegerPropertyDefinition(PropertyKey.retriesAllDown, 120, true, Messages.getString("ConnectionProperties.retriesAllDown"), "5.1.6", CATEGORY_HA, 4, 0, 2147483647), new BooleanPropertyDefinition(PropertyKey.pinGlobalTxToPhysicalConnection, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.pinGlobalTxToPhysicalConnection"), "5.0.1", CATEGORY_HA, -2147483648), new IntegerPropertyDefinition(PropertyKey.queriesBeforeRetrySource, 50, true, Messages.getString("ConnectionProperties.queriesBeforeRetrySource"), "3.0.2", CATEGORY_HA, 7, 0, 2147483647), new BooleanPropertyDefinition(PropertyKey.reconnectAtTxEnd, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.reconnectAtTxEnd"), "3.0.10", CATEGORY_HA, 4), new StringPropertyDefinition(PropertyKey.replicationConnectionGroup, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.replicationConnectionGroup"), "8.0.7", CATEGORY_HA, -2147483648), new StringPropertyDefinition(PropertyKey.resourceId, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.resourceId"), "5.0.1", CATEGORY_HA, -2147483648), new IntegerPropertyDefinition(PropertyKey.secondsBeforeRetrySource, 30, true, Messages.getString("ConnectionProperties.secondsBeforeRetrySource"), "3.0.2", CATEGORY_HA, 8, 0, 2147483647), new IntegerPropertyDefinition(PropertyKey.selfDestructOnPingSecondsLifetime, 0, true, Messages.getString("ConnectionProperties.selfDestructOnPingSecondsLifetime"), "5.1.6", CATEGORY_HA, 2147483647, 0, 2147483647), new IntegerPropertyDefinition(PropertyKey.selfDestructOnPingMaxOperations, 0, true, Messages.getString("ConnectionProperties.selfDestructOnPingMaxOperations"), "5.1.6", CATEGORY_HA, 2147483647, 0, 2147483647), new BooleanPropertyDefinition(PropertyKey.ha_enableJMX, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.ha.enableJMX"), "5.1.27", CATEGORY_HA, 2147483647), new IntegerPropertyDefinition(PropertyKey.loadBalanceHostRemovalGracePeriod, 15000, true, Messages.getString("ConnectionProperties.loadBalanceHostRemovalGracePeriod"), "6.0.3", CATEGORY_HA, 2147483647, 0, 2147483647), new StringPropertyDefinition(PropertyKey.serverAffinityOrder, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.serverAffinityOrder"), "8.0.8", CATEGORY_HA, -2147483648), new BooleanPropertyDefinition(PropertyKey.alwaysSendSetIsolation, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.alwaysSendSetIsolation"), "3.1.7", CATEGORY_PERFORMANCE, 2147483647), new BooleanPropertyDefinition(PropertyKey.cacheCallableStmts, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.cacheCallableStatements"), "3.1.2", CATEGORY_PERFORMANCE, -2147483648), new BooleanPropertyDefinition(PropertyKey.cachePrepStmts, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.cachePrepStmts"), "3.0.10", CATEGORY_PERFORMANCE, -2147483648), new BooleanPropertyDefinition(PropertyKey.cacheResultSetMetadata, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.cacheRSMetadata"), "3.1.1", CATEGORY_PERFORMANCE, -2147483648), new StringPropertyDefinition(PropertyKey.serverConfigCacheFactory, PerVmServerConfigCacheFactory.class.getName(), true, Messages.getString("ConnectionProperties.serverConfigCacheFactory"), "5.1.1", CATEGORY_PERFORMANCE, 12), new BooleanPropertyDefinition(PropertyKey.cacheServerConfiguration, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.cacheServerConfiguration"), "3.1.5", CATEGORY_PERFORMANCE, -2147483648), new IntegerPropertyDefinition(PropertyKey.callableStmtCacheSize, 100, true, Messages.getString("ConnectionProperties.callableStmtCacheSize"), "3.1.2", CATEGORY_PERFORMANCE, 5, 0, 2147483647), new IntegerPropertyDefinition(PropertyKey.defaultFetchSize, 0, true, Messages.getString("ConnectionProperties.defaultFetchSize"), "3.1.9", CATEGORY_PERFORMANCE, -2147483648), new BooleanPropertyDefinition(PropertyKey.elideSetAutoCommits, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.eliseSetAutoCommit"), "3.1.3", CATEGORY_PERFORMANCE, -2147483648), new BooleanPropertyDefinition(PropertyKey.enableQueryTimeouts, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.enableQueryTimeouts"), "5.0.6", CATEGORY_PERFORMANCE, -2147483648), new MemorySizePropertyDefinition(PropertyKey.largeRowSizeThreshold, 2048, true, Messages.getString("ConnectionProperties.largeRowSizeThreshold"), "5.1.1", CATEGORY_PERFORMANCE, -2147483648, 0, 2147483647), new BooleanPropertyDefinition(PropertyKey.maintainTimeStats, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.maintainTimeStats"), "3.1.9", CATEGORY_PERFORMANCE, 2147483647), new IntegerPropertyDefinition(PropertyKey.metadataCacheSize, 50, true, Messages.getString("ConnectionProperties.metadataCacheSize"), "3.1.1", CATEGORY_PERFORMANCE, 5, 1, 2147483647), new IntegerPropertyDefinition(PropertyKey.prepStmtCacheSize, 25, true, Messages.getString("ConnectionProperties.prepStmtCacheSize"), "3.0.10", CATEGORY_PERFORMANCE, 10, 0, 2147483647), new IntegerPropertyDefinition(PropertyKey.prepStmtCacheSqlLimit, 256, true, Messages.getString("ConnectionProperties.prepStmtCacheSqlLimit"), "3.0.10", CATEGORY_PERFORMANCE, 11, 1, 2147483647), new StringPropertyDefinition(PropertyKey.parseInfoCacheFactory, PerConnectionLRUFactory.class.getName(), true, Messages.getString("ConnectionProperties.parseInfoCacheFactory"), "5.1.1", CATEGORY_PERFORMANCE, 12), new BooleanPropertyDefinition(PropertyKey.rewriteBatchedStatements, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.rewriteBatchedStatements"), "3.1.13", CATEGORY_PERFORMANCE, -2147483648), new BooleanPropertyDefinition(PropertyKey.useCursorFetch, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.useCursorFetch"), "5.0.0", CATEGORY_PERFORMANCE, 2147483647), new BooleanPropertyDefinition(PropertyKey.useLocalSessionState, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.useLocalSessionState"), "3.1.7", CATEGORY_PERFORMANCE, 5), new BooleanPropertyDefinition(PropertyKey.useLocalTransactionState, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.useLocalTransactionState"), "5.1.7", CATEGORY_PERFORMANCE, 6), new BooleanPropertyDefinition(PropertyKey.useReadAheadInput, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.useReadAheadInput"), "3.1.5", CATEGORY_PERFORMANCE, -2147483648), new BooleanPropertyDefinition(PropertyKey.dontCheckOnDuplicateKeyUpdateInSQL, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.dontCheckOnDuplicateKeyUpdateInSQL"), "5.1.32", CATEGORY_PERFORMANCE, -2147483648), new BooleanPropertyDefinition(PropertyKey.readOnlyPropagatesToServer, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.readOnlyPropagatesToServer"), "5.1.35", CATEGORY_PERFORMANCE, -2147483648), new BooleanPropertyDefinition(PropertyKey.enableEscapeProcessing, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.enableEscapeProcessing"), "6.0.1", CATEGORY_PERFORMANCE, -2147483648), new StringPropertyDefinition(PropertyKey.logger, STANDARD_LOGGER_NAME, true, Messages.getString("ConnectionProperties.logger", new Object[] { Log.class.getName(), STANDARD_LOGGER_NAME }), "3.1.1", CATEGORY_DEBUGING_PROFILING, 0), new StringPropertyDefinition(PropertyKey.profilerEventHandler, "com.mysql.cj.log.LoggingProfilerEventHandler", true, Messages.getString("ConnectionProperties.profilerEventHandler"), "5.1.6", CATEGORY_DEBUGING_PROFILING, 1), new BooleanPropertyDefinition(PropertyKey.useNanosForElapsedTime, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.useNanosForElapsedTime"), "5.0.7", CATEGORY_DEBUGING_PROFILING, 2), new IntegerPropertyDefinition(PropertyKey.maxQuerySizeToLog, 2048, true, Messages.getString("ConnectionProperties.maxQuerySizeToLog"), "3.1.3", CATEGORY_DEBUGING_PROFILING, 3, 0, 2147483647), new BooleanPropertyDefinition(PropertyKey.profileSQL, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.profileSQL"), "3.1.0", CATEGORY_DEBUGING_PROFILING, 4), new BooleanPropertyDefinition(PropertyKey.logSlowQueries, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.logSlowQueries"), "3.1.2", CATEGORY_DEBUGING_PROFILING, 5), new IntegerPropertyDefinition(PropertyKey.slowQueryThresholdMillis, 2000, true, Messages.getString("ConnectionProperties.slowQueryThresholdMillis"), "3.1.2", CATEGORY_DEBUGING_PROFILING, 6, 0, 2147483647), new LongPropertyDefinition(PropertyKey.slowQueryThresholdNanos, 0L, true, Messages.getString("ConnectionProperties.slowQueryThresholdNanos"), "5.0.7", CATEGORY_DEBUGING_PROFILING, 7), new BooleanPropertyDefinition(PropertyKey.autoSlowLog, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.autoSlowLog"), "5.1.4", CATEGORY_DEBUGING_PROFILING, 8), new BooleanPropertyDefinition(PropertyKey.explainSlowQueries, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.explainSlowQueries"), "3.1.2", CATEGORY_DEBUGING_PROFILING, 9), new BooleanPropertyDefinition(PropertyKey.gatherPerfMetrics, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.gatherPerfMetrics"), "3.1.2", CATEGORY_DEBUGING_PROFILING, 10), new IntegerPropertyDefinition(PropertyKey.reportMetricsIntervalMillis, 30000, true, Messages.getString("ConnectionProperties.reportMetricsIntervalMillis"), "3.1.2", CATEGORY_DEBUGING_PROFILING, 11, 0, 2147483647), new BooleanPropertyDefinition(PropertyKey.logXaCommands, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.logXaCommands"), "5.0.5", CATEGORY_DEBUGING_PROFILING, 12), new BooleanPropertyDefinition(PropertyKey.traceProtocol, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.traceProtocol"), "3.1.2", CATEGORY_DEBUGING_PROFILING, 13), new BooleanPropertyDefinition(PropertyKey.enablePacketDebug, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.enablePacketDebug"), "3.1.3", CATEGORY_DEBUGING_PROFILING, 14), new IntegerPropertyDefinition(PropertyKey.packetDebugBufferSize, 20, true, Messages.getString("ConnectionProperties.packetDebugBufferSize"), "3.1.3", CATEGORY_DEBUGING_PROFILING, 15, 1, 2147483647), new BooleanPropertyDefinition(PropertyKey.useUsageAdvisor, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.useUsageAdvisor"), "3.1.1", CATEGORY_DEBUGING_PROFILING, 16), new IntegerPropertyDefinition(PropertyKey.resultSetSizeThreshold, 100, true, Messages.getString("ConnectionProperties.resultSetSizeThreshold"), "5.0.5", CATEGORY_DEBUGING_PROFILING, 17), new BooleanPropertyDefinition(PropertyKey.autoGenerateTestcaseScript, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.autoGenerateTestcaseScript"), "3.1.9", CATEGORY_DEBUGING_PROFILING, 18), new BooleanPropertyDefinition(PropertyKey.dumpQueriesOnException, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.dumpQueriesOnException"), "3.1.3", CATEGORY_EXCEPTIONS, -2147483648), new StringPropertyDefinition(PropertyKey.exceptionInterceptors, DEFAULT_VALUE_NULL_STRING, true, Messages.getString("ConnectionProperties.exceptionInterceptors"), "5.1.8", CATEGORY_EXCEPTIONS, -2147483648), new BooleanPropertyDefinition(PropertyKey.includeInnodbStatusInDeadlockExceptions, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.includeInnodbStatusInDeadlockExceptions"), "5.0.7", CATEGORY_EXCEPTIONS, -2147483648), new BooleanPropertyDefinition(PropertyKey.includeThreadDumpInDeadlockExceptions, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.includeThreadDumpInDeadlockExceptions"), "5.1.15", CATEGORY_EXCEPTIONS, -2147483648), new BooleanPropertyDefinition(PropertyKey.includeThreadNamesAsStatementComment, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.includeThreadNamesAsStatementComment"), "5.1.15", CATEGORY_EXCEPTIONS, -2147483648), new BooleanPropertyDefinition(PropertyKey.ignoreNonTxTables, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.ignoreNonTxTables"), "3.0.9", CATEGORY_EXCEPTIONS, -2147483648), new BooleanPropertyDefinition(PropertyKey.useOnlyServerErrorMessages, Boolean.valueOf(true), true, Messages.getString("ConnectionProperties.useOnlyServerErrorMessages"), "3.0.15", CATEGORY_EXCEPTIONS, -2147483648), new BooleanPropertyDefinition(PropertyKey.overrideSupportsIntegrityEnhancementFacility, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.overrideSupportsIEF"), "3.1.12", CATEGORY_INTEGRATION, -2147483648), new BooleanPropertyDefinition(PropertyKey.ultraDevHack, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.ultraDevHack"), "2.0.3", CATEGORY_INTEGRATION, -2147483648), new BooleanPropertyDefinition(PropertyKey.pedantic, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.pedantic"), "3.0.0", CATEGORY_JDBC, -2147483648), new BooleanPropertyDefinition(PropertyKey.useColumnNamesInFindColumn, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.useColumnNamesInFindColumn"), "5.1.7", CATEGORY_JDBC, 2147483647), new BooleanPropertyDefinition(PropertyKey.useOldAliasMetadataBehavior, Boolean.valueOf(false), true, Messages.getString("ConnectionProperties.useOldAliasMetadataBehavior"), "5.0.4", CATEGORY_JDBC, -2147483648), new EnumPropertyDefinition<>(PropertyKey.xdevapiSslMode, XdevapiSslMode.REQUIRED, true, Messages.getString("ConnectionProperties.xdevapiSslMode"), "8.0.7", CATEGORY_XDEVAPI, -2147483648), new StringPropertyDefinition(PropertyKey.xdevapiTlsCiphersuites, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.xdevapiTlsCiphersuites"), "8.0.19", CATEGORY_XDEVAPI, -2147483648), new StringPropertyDefinition(PropertyKey.xdevapiTlsVersions, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.xdevapiTlsVersions"), "8.0.19", CATEGORY_XDEVAPI, -2147483648), new StringPropertyDefinition(PropertyKey.xdevapiSslKeyStoreUrl, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.xdevapiSslKeyStoreUrl"), "8.0.22", CATEGORY_XDEVAPI, -2147483648), new StringPropertyDefinition(PropertyKey.xdevapiSslKeyStorePassword, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.xdevapiSslKeyStorePassword"), "8.0.22", CATEGORY_XDEVAPI, -2147483648), new StringPropertyDefinition(PropertyKey.xdevapiSslKeyStoreType, "JKS", false, Messages.getString("ConnectionProperties.xdevapiSslKeyStoreType"), "8.0.22", CATEGORY_XDEVAPI, -2147483648), new BooleanPropertyDefinition(PropertyKey.xdevapiFallbackToSystemKeyStore, Boolean.valueOf(true), false, Messages.getString("ConnectionProperties.xdevapiFallbackToSystemKeyStore"), "8.0.22", CATEGORY_XDEVAPI, -2147483648), new StringPropertyDefinition(PropertyKey.xdevapiSslTrustStoreUrl, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.xdevapiSslTrustStoreUrl"), "6.0.6", CATEGORY_XDEVAPI, -2147483648), new StringPropertyDefinition(PropertyKey.xdevapiSslTrustStorePassword, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.xdevapiSslTrustStorePassword"), "6.0.6", CATEGORY_XDEVAPI, -2147483648), new StringPropertyDefinition(PropertyKey.xdevapiSslTrustStoreType, "JKS", false, Messages.getString("ConnectionProperties.xdevapiSslTrustStoreType"), "6.0.6", CATEGORY_XDEVAPI, -2147483648), new BooleanPropertyDefinition(PropertyKey.xdevapiFallbackToSystemTrustStore, Boolean.valueOf(true), false, Messages.getString("ConnectionProperties.xdevapiFallbackToSystemTrustStore"), "8.0.22", CATEGORY_XDEVAPI, -2147483648), new EnumPropertyDefinition<>(PropertyKey.xdevapiAuth, AuthMech.PLAIN, false, Messages.getString("ConnectionProperties.auth"), "8.0.8", CATEGORY_XDEVAPI, -2147483648), new IntegerPropertyDefinition(PropertyKey.xdevapiConnectTimeout, 10000, true, Messages.getString("ConnectionProperties.xdevapiConnectTimeout"), "8.0.13", CATEGORY_XDEVAPI, -2147483648, 0, 2147483647), new StringPropertyDefinition(PropertyKey.xdevapiConnectionAttributes, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.xdevapiConnectionAttributes"), "8.0.16", CATEGORY_XDEVAPI, -2147483648), new BooleanPropertyDefinition(PropertyKey.xdevapiDnsSrv, Boolean.valueOf(false), false, Messages.getString("ConnectionProperties.xdevapiDnsSrv"), "8.0.19", CATEGORY_XDEVAPI, -2147483648), new EnumPropertyDefinition<>(PropertyKey.xdevapiCompression, Compression.PREFERRED, false, Messages.getString("ConnectionProperties.xdevapiCompression"), "8.0.20", CATEGORY_XDEVAPI, -2147483648), new StringPropertyDefinition(PropertyKey.xdevapiCompressionAlgorithms, "zstd_stream,lz4_message,deflate_stream", false, Messages.getString("ConnectionProperties.xdevapiCompressionAlgorithms"), "8.0.22", CATEGORY_XDEVAPI, -2147483648), new StringPropertyDefinition(PropertyKey.xdevapiCompressionExtensions, DEFAULT_VALUE_NULL_STRING, false, Messages.getString("ConnectionProperties.xdevapiCompressionExtensions"), "8.0.22", CATEGORY_XDEVAPI, -2147483648) };
/*     */ 
/*     */ 
/*     */     
/* 872 */     HashMap<PropertyKey, PropertyDefinition<?>> propertyKeyToPropertyDefinitionMap = new HashMap<>();
/* 873 */     for (PropertyDefinition<?> pdef : arrayOfPropertyDefinition) {
/* 874 */       propertyKeyToPropertyDefinitionMap.put(pdef.getPropertyKey(), pdef);
/*     */     }
/* 876 */     PROPERTY_KEY_TO_PROPERTY_DEFINITION = Collections.unmodifiableMap(propertyKeyToPropertyDefinitionMap);
/*     */   }
/*     */   
/*     */   public static PropertyDefinition<?> getPropertyDefinition(PropertyKey propertyKey) {
/* 880 */     return PROPERTY_KEY_TO_PROPERTY_DEFINITION.get(propertyKey);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\PropertyDefinitions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */