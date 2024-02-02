package org.h2.command.dml;

import java.util.ArrayList;

public class SetTypes {
   public static final int IGNORECASE = 0;
   public static final int MAX_LOG_SIZE = 1;
   public static final int MODE = 2;
   public static final int READONLY = 3;
   public static final int LOCK_TIMEOUT = 4;
   public static final int DEFAULT_LOCK_TIMEOUT = 5;
   public static final int DEFAULT_TABLE_TYPE = 6;
   public static final int CACHE_SIZE = 7;
   public static final int TRACE_LEVEL_SYSTEM_OUT = 8;
   public static final int TRACE_LEVEL_FILE = 9;
   public static final int TRACE_MAX_FILE_SIZE = 10;
   public static final int COLLATION = 11;
   public static final int CLUSTER = 12;
   public static final int WRITE_DELAY = 13;
   public static final int DATABASE_EVENT_LISTENER = 14;
   public static final int MAX_MEMORY_ROWS = 15;
   public static final int LOCK_MODE = 16;
   public static final int DB_CLOSE_DELAY = 17;
   public static final int THROTTLE = 18;
   public static final int MAX_MEMORY_UNDO = 19;
   public static final int MAX_LENGTH_INPLACE_LOB = 20;
   public static final int ALLOW_LITERALS = 21;
   public static final int SCHEMA = 22;
   public static final int OPTIMIZE_REUSE_RESULTS = 23;
   public static final int SCHEMA_SEARCH_PATH = 24;
   public static final int REFERENTIAL_INTEGRITY = 25;
   public static final int MAX_OPERATION_MEMORY = 26;
   public static final int EXCLUSIVE = 27;
   public static final int CREATE_BUILD = 28;
   public static final int VARIABLE = 29;
   public static final int QUERY_TIMEOUT = 30;
   public static final int REDO_LOG_BINARY = 31;
   public static final int JAVA_OBJECT_SERIALIZER = 32;
   public static final int RETENTION_TIME = 33;
   public static final int QUERY_STATISTICS = 34;
   public static final int QUERY_STATISTICS_MAX_ENTRIES = 35;
   public static final int LAZY_QUERY_EXECUTION = 36;
   public static final int BUILTIN_ALIAS_OVERRIDE = 37;
   public static final int AUTHENTICATOR = 38;
   public static final int IGNORE_CATALOGS = 39;
   public static final int CATALOG = 40;
   public static final int NON_KEYWORDS = 41;
   public static final int TIME_ZONE = 42;
   public static final int VARIABLE_BINARY = 43;
   public static final int DEFAULT_NULL_ORDERING = 44;
   public static final int TRUNCATE_LARGE_LENGTH = 45;
   private static final int COUNT = 46;
   private static final ArrayList<String> TYPES;

   private SetTypes() {
   }

   public static int getType(String var0) {
      return TYPES.indexOf(var0);
   }

   public static ArrayList<String> getTypes() {
      return TYPES;
   }

   public static String getTypeName(int var0) {
      return (String)TYPES.get(var0);
   }

   static {
      ArrayList var0 = new ArrayList(46);
      var0.add("IGNORECASE");
      var0.add("MAX_LOG_SIZE");
      var0.add("MODE");
      var0.add("READONLY");
      var0.add("LOCK_TIMEOUT");
      var0.add("DEFAULT_LOCK_TIMEOUT");
      var0.add("DEFAULT_TABLE_TYPE");
      var0.add("CACHE_SIZE");
      var0.add("TRACE_LEVEL_SYSTEM_OUT");
      var0.add("TRACE_LEVEL_FILE");
      var0.add("TRACE_MAX_FILE_SIZE");
      var0.add("COLLATION");
      var0.add("CLUSTER");
      var0.add("WRITE_DELAY");
      var0.add("DATABASE_EVENT_LISTENER");
      var0.add("MAX_MEMORY_ROWS");
      var0.add("LOCK_MODE");
      var0.add("DB_CLOSE_DELAY");
      var0.add("THROTTLE");
      var0.add("MAX_MEMORY_UNDO");
      var0.add("MAX_LENGTH_INPLACE_LOB");
      var0.add("ALLOW_LITERALS");
      var0.add("SCHEMA");
      var0.add("OPTIMIZE_REUSE_RESULTS");
      var0.add("SCHEMA_SEARCH_PATH");
      var0.add("REFERENTIAL_INTEGRITY");
      var0.add("MAX_OPERATION_MEMORY");
      var0.add("EXCLUSIVE");
      var0.add("CREATE_BUILD");
      var0.add("@");
      var0.add("QUERY_TIMEOUT");
      var0.add("REDO_LOG_BINARY");
      var0.add("JAVA_OBJECT_SERIALIZER");
      var0.add("RETENTION_TIME");
      var0.add("QUERY_STATISTICS");
      var0.add("QUERY_STATISTICS_MAX_ENTRIES");
      var0.add("LAZY_QUERY_EXECUTION");
      var0.add("BUILTIN_ALIAS_OVERRIDE");
      var0.add("AUTHENTICATOR");
      var0.add("IGNORE_CATALOGS");
      var0.add("CATALOG");
      var0.add("NON_KEYWORDS");
      var0.add("TIME ZONE");
      var0.add("VARIABLE_BINARY");
      var0.add("DEFAULT_NULL_ORDERING");
      var0.add("TRUNCATE_LARGE_LENGTH");
      TYPES = var0;

      assert var0.size() == 46;

   }
}
