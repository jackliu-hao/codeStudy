/*     */ package org.h2.engine;
/*     */ 
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SysProperties
/*     */ {
/*     */   public static final String H2_SCRIPT_DIRECTORY = "h2.scriptDirectory";
/*     */   public static final String H2_BROWSER = "h2.browser";
/*  50 */   public static final String USER_HOME = Utils.getProperty("user.home", "");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   public static final String ALLOWED_CLASSES = Utils.getProperty("h2.allowedClasses", "*");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   public static final boolean ENABLE_ANONYMOUS_TLS = Utils.getProperty("h2.enableAnonymousTLS", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   public static final String BIND_ADDRESS = Utils.getProperty("h2.bindAddress", null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  79 */   public static final boolean CHECK = Utils.getProperty("h2.check", !"0.9".equals(Utils.getProperty("java.specification.version", null)));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   public static final String CLIENT_TRACE_DIRECTORY = Utils.getProperty("h2.clientTraceDirectory", "trace.db/");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  97 */   public static final int COLLATOR_CACHE_SIZE = Utils.getProperty("h2.collatorCacheSize", 32000);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 105 */   public static final int CONSOLE_MAX_TABLES_LIST_INDEXES = Utils.getProperty("h2.consoleTableIndexes", 100);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 113 */   public static final int CONSOLE_MAX_TABLES_LIST_COLUMNS = Utils.getProperty("h2.consoleTableColumns", 500);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 121 */   public static final int CONSOLE_MAX_PROCEDURES_LIST_COLUMNS = Utils.getProperty("h2.consoleProcedureColumns", 300);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 128 */   public static final boolean CONSOLE_STREAM = Utils.getProperty("h2.consoleStream", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 135 */   public static final int CONSOLE_TIMEOUT = Utils.getProperty("h2.consoleTimeout", 1800000);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 143 */   public static final int DATASOURCE_TRACE_LEVEL = Utils.getProperty("h2.dataSourceTraceLevel", 1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 155 */   public static final int DELAY_WRONG_PASSWORD_MIN = Utils.getProperty("h2.delayWrongPasswordMin", 250);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 166 */   public static final int DELAY_WRONG_PASSWORD_MAX = Utils.getProperty("h2.delayWrongPasswordMax", 4000);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 177 */   public static final boolean JAVA_SYSTEM_COMPILER = Utils.getProperty("h2.javaSystemCompiler", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 185 */   public static boolean lobCloseBetweenReads = Utils.getProperty("h2.lobCloseBetweenReads", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 194 */   public static final int LOB_CLIENT_MAX_SIZE_MEMORY = Utils.getProperty("h2.lobClientMaxSizeMemory", 1048576);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 205 */   public static final int MAX_FILE_RETRY = Math.max(1, Utils.getProperty("h2.maxFileRetry", 16));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 212 */   public static final int MAX_RECONNECT = Utils.getProperty("h2.maxReconnect", 3);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 220 */   public static final int MAX_MEMORY_ROWS = getAutoScaledForMemoryProperty("h2.maxMemoryRows", 40000);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 229 */   public static final long MAX_TRACE_DATA_LENGTH = Utils.getProperty("h2.maxTraceDataLength", 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 237 */   public static final boolean NIO_LOAD_MAPPED = Utils.getProperty("h2.nioLoadMapped", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 247 */   public static final boolean NIO_CLEANER_HACK = Utils.getProperty("h2.nioCleanerHack", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 255 */   public static final boolean OBJECT_CACHE = Utils.getProperty("h2.objectCache", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 263 */   public static final int OBJECT_CACHE_MAX_PER_ELEMENT_SIZE = Utils.getProperty("h2.objectCacheMaxPerElementSize", 4096);
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int OBJECT_CACHE_SIZE;
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*     */     try {
/* 273 */       OBJECT_CACHE_SIZE = MathUtils.nextPowerOf2(
/* 274 */           Utils.getProperty("h2.objectCacheSize", 1024));
/* 275 */     } catch (IllegalArgumentException illegalArgumentException) {
/* 276 */       throw new IllegalStateException("Invalid h2.objectCacheSize", illegalArgumentException);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 286 */   public static final String PG_DEFAULT_CLIENT_ENCODING = Utils.getProperty("h2.pgClientEncoding", "UTF-8");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 293 */   public static final String PREFIX_TEMP_FILE = Utils.getProperty("h2.prefixTempFile", "h2.temp");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 300 */   public static boolean FORCE_AUTOCOMMIT_OFF_ON_COMMIT = Utils.getProperty("h2.forceAutoCommitOffOnCommit", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 307 */   public static final int SERVER_CACHED_OBJECTS = Utils.getProperty("h2.serverCachedObjects", 64);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 315 */   public static final int SERVER_RESULT_SET_FETCH_SIZE = Utils.getProperty("h2.serverResultSetFetchSize", 100);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 324 */   public static final int SOCKET_CONNECT_RETRY = Utils.getProperty("h2.socketConnectRetry", 16);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 332 */   public static final int SOCKET_CONNECT_TIMEOUT = Utils.getProperty("h2.socketConnectTimeout", 2000);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 339 */   public static final long SPLIT_FILE_SIZE_SHIFT = Utils.getProperty("h2.splitFileSizeShift", 30);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 346 */   public static final boolean TRACE_IO = Utils.getProperty("h2.traceIO", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 354 */   public static final boolean THREAD_DEADLOCK_DETECTOR = Utils.getProperty("h2.threadDeadlockDetector", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 363 */   public static final String URL_MAP = Utils.getProperty("h2.urlMap", null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 372 */   public static final boolean USE_THREAD_CONTEXT_CLASS_LOADER = Utils.getProperty("h2.useThreadContextClassLoader", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 382 */   public static final String JAVA_OBJECT_SERIALIZER = Utils.getProperty("h2.javaObjectSerializer", null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 392 */   public static final String AUTH_CONFIG_FILE = Utils.getProperty("h2.authConfigFile", null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String H2_BASE_DIR = "h2.baseDir";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setBaseDir(String paramString) {
/* 405 */     if (!paramString.endsWith("/")) {
/* 406 */       paramString = paramString + "/";
/*     */     }
/* 408 */     System.setProperty("h2.baseDir", paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getBaseDir() {
/* 416 */     return Utils.getProperty("h2.baseDir", null);
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
/*     */   public static String getScriptDirectory() {
/* 428 */     return Utils.getProperty("h2.scriptDirectory", "");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getAutoScaledForMemoryProperty(String paramString, int paramInt) {
/* 438 */     String str = Utils.getProperty(paramString, null);
/* 439 */     if (str != null) {
/*     */       try {
/* 441 */         return Integer.decode(str).intValue();
/* 442 */       } catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */ 
/*     */     
/* 446 */     return Utils.scaleForAvailableMemory(paramInt);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\SysProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */