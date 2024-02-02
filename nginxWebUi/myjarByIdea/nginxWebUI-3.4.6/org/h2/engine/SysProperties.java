package org.h2.engine;

import org.h2.util.MathUtils;
import org.h2.util.Utils;

public class SysProperties {
   public static final String H2_SCRIPT_DIRECTORY = "h2.scriptDirectory";
   public static final String H2_BROWSER = "h2.browser";
   public static final String USER_HOME = Utils.getProperty("user.home", "");
   public static final String ALLOWED_CLASSES = Utils.getProperty("h2.allowedClasses", "*");
   public static final boolean ENABLE_ANONYMOUS_TLS = Utils.getProperty("h2.enableAnonymousTLS", true);
   public static final String BIND_ADDRESS = Utils.getProperty("h2.bindAddress", (String)null);
   public static final boolean CHECK = Utils.getProperty("h2.check", !"0.9".equals(Utils.getProperty("java.specification.version", (String)null)));
   public static final String CLIENT_TRACE_DIRECTORY = Utils.getProperty("h2.clientTraceDirectory", "trace.db/");
   public static final int COLLATOR_CACHE_SIZE = Utils.getProperty("h2.collatorCacheSize", 32000);
   public static final int CONSOLE_MAX_TABLES_LIST_INDEXES = Utils.getProperty("h2.consoleTableIndexes", 100);
   public static final int CONSOLE_MAX_TABLES_LIST_COLUMNS = Utils.getProperty("h2.consoleTableColumns", 500);
   public static final int CONSOLE_MAX_PROCEDURES_LIST_COLUMNS = Utils.getProperty("h2.consoleProcedureColumns", 300);
   public static final boolean CONSOLE_STREAM = Utils.getProperty("h2.consoleStream", true);
   public static final int CONSOLE_TIMEOUT = Utils.getProperty("h2.consoleTimeout", 1800000);
   public static final int DATASOURCE_TRACE_LEVEL = Utils.getProperty("h2.dataSourceTraceLevel", 1);
   public static final int DELAY_WRONG_PASSWORD_MIN = Utils.getProperty("h2.delayWrongPasswordMin", 250);
   public static final int DELAY_WRONG_PASSWORD_MAX = Utils.getProperty("h2.delayWrongPasswordMax", 4000);
   public static final boolean JAVA_SYSTEM_COMPILER = Utils.getProperty("h2.javaSystemCompiler", true);
   public static boolean lobCloseBetweenReads = Utils.getProperty("h2.lobCloseBetweenReads", false);
   public static final int LOB_CLIENT_MAX_SIZE_MEMORY = Utils.getProperty("h2.lobClientMaxSizeMemory", 1048576);
   public static final int MAX_FILE_RETRY = Math.max(1, Utils.getProperty("h2.maxFileRetry", 16));
   public static final int MAX_RECONNECT = Utils.getProperty("h2.maxReconnect", 3);
   public static final int MAX_MEMORY_ROWS = getAutoScaledForMemoryProperty("h2.maxMemoryRows", 40000);
   public static final long MAX_TRACE_DATA_LENGTH = (long)Utils.getProperty("h2.maxTraceDataLength", 65535);
   public static final boolean NIO_LOAD_MAPPED = Utils.getProperty("h2.nioLoadMapped", false);
   public static final boolean NIO_CLEANER_HACK = Utils.getProperty("h2.nioCleanerHack", false);
   public static final boolean OBJECT_CACHE = Utils.getProperty("h2.objectCache", true);
   public static final int OBJECT_CACHE_MAX_PER_ELEMENT_SIZE = Utils.getProperty("h2.objectCacheMaxPerElementSize", 4096);
   public static final int OBJECT_CACHE_SIZE;
   public static final String PG_DEFAULT_CLIENT_ENCODING;
   public static final String PREFIX_TEMP_FILE;
   public static boolean FORCE_AUTOCOMMIT_OFF_ON_COMMIT;
   public static final int SERVER_CACHED_OBJECTS;
   public static final int SERVER_RESULT_SET_FETCH_SIZE;
   public static final int SOCKET_CONNECT_RETRY;
   public static final int SOCKET_CONNECT_TIMEOUT;
   public static final long SPLIT_FILE_SIZE_SHIFT;
   public static final boolean TRACE_IO;
   public static final boolean THREAD_DEADLOCK_DETECTOR;
   public static final String URL_MAP;
   public static final boolean USE_THREAD_CONTEXT_CLASS_LOADER;
   public static final String JAVA_OBJECT_SERIALIZER;
   public static final String AUTH_CONFIG_FILE;
   private static final String H2_BASE_DIR = "h2.baseDir";

   private SysProperties() {
   }

   public static void setBaseDir(String var0) {
      if (!var0.endsWith("/")) {
         var0 = var0 + "/";
      }

      System.setProperty("h2.baseDir", var0);
   }

   public static String getBaseDir() {
      return Utils.getProperty("h2.baseDir", (String)null);
   }

   public static String getScriptDirectory() {
      return Utils.getProperty("h2.scriptDirectory", "");
   }

   private static int getAutoScaledForMemoryProperty(String var0, int var1) {
      String var2 = Utils.getProperty(var0, (String)null);
      if (var2 != null) {
         try {
            return Integer.decode(var2);
         } catch (NumberFormatException var4) {
         }
      }

      return Utils.scaleForAvailableMemory(var1);
   }

   static {
      try {
         OBJECT_CACHE_SIZE = MathUtils.nextPowerOf2(Utils.getProperty("h2.objectCacheSize", 1024));
      } catch (IllegalArgumentException var1) {
         throw new IllegalStateException("Invalid h2.objectCacheSize", var1);
      }

      PG_DEFAULT_CLIENT_ENCODING = Utils.getProperty("h2.pgClientEncoding", "UTF-8");
      PREFIX_TEMP_FILE = Utils.getProperty("h2.prefixTempFile", "h2.temp");
      FORCE_AUTOCOMMIT_OFF_ON_COMMIT = Utils.getProperty("h2.forceAutoCommitOffOnCommit", false);
      SERVER_CACHED_OBJECTS = Utils.getProperty("h2.serverCachedObjects", 64);
      SERVER_RESULT_SET_FETCH_SIZE = Utils.getProperty("h2.serverResultSetFetchSize", 100);
      SOCKET_CONNECT_RETRY = Utils.getProperty("h2.socketConnectRetry", 16);
      SOCKET_CONNECT_TIMEOUT = Utils.getProperty("h2.socketConnectTimeout", 2000);
      SPLIT_FILE_SIZE_SHIFT = (long)Utils.getProperty("h2.splitFileSizeShift", 30);
      TRACE_IO = Utils.getProperty("h2.traceIO", false);
      THREAD_DEADLOCK_DETECTOR = Utils.getProperty("h2.threadDeadlockDetector", false);
      URL_MAP = Utils.getProperty("h2.urlMap", (String)null);
      USE_THREAD_CONTEXT_CLASS_LOADER = Utils.getProperty("h2.useThreadContextClassLoader", false);
      JAVA_OBJECT_SERIALIZER = Utils.getProperty("h2.javaObjectSerializer", (String)null);
      AUTH_CONFIG_FILE = Utils.getProperty("h2.authConfigFile", (String)null);
   }
}
