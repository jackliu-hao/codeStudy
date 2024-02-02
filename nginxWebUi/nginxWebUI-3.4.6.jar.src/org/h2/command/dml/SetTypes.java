/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SetTypes
/*     */ {
/*     */   public static final int IGNORECASE = 0;
/*     */   public static final int MAX_LOG_SIZE = 1;
/*     */   public static final int MODE = 2;
/*     */   public static final int READONLY = 3;
/*     */   public static final int LOCK_TIMEOUT = 4;
/*     */   public static final int DEFAULT_LOCK_TIMEOUT = 5;
/*     */   public static final int DEFAULT_TABLE_TYPE = 6;
/*     */   public static final int CACHE_SIZE = 7;
/*     */   public static final int TRACE_LEVEL_SYSTEM_OUT = 8;
/*     */   public static final int TRACE_LEVEL_FILE = 9;
/*     */   public static final int TRACE_MAX_FILE_SIZE = 10;
/*     */   public static final int COLLATION = 11;
/*     */   public static final int CLUSTER = 12;
/*     */   public static final int WRITE_DELAY = 13;
/*     */   public static final int DATABASE_EVENT_LISTENER = 14;
/*     */   public static final int MAX_MEMORY_ROWS = 15;
/*     */   public static final int LOCK_MODE = 16;
/*     */   public static final int DB_CLOSE_DELAY = 17;
/*     */   public static final int THROTTLE = 18;
/*     */   public static final int MAX_MEMORY_UNDO = 19;
/*     */   public static final int MAX_LENGTH_INPLACE_LOB = 20;
/*     */   public static final int ALLOW_LITERALS = 21;
/*     */   public static final int SCHEMA = 22;
/*     */   public static final int OPTIMIZE_REUSE_RESULTS = 23;
/*     */   public static final int SCHEMA_SEARCH_PATH = 24;
/*     */   public static final int REFERENTIAL_INTEGRITY = 25;
/*     */   public static final int MAX_OPERATION_MEMORY = 26;
/*     */   public static final int EXCLUSIVE = 27;
/*     */   public static final int CREATE_BUILD = 28;
/*     */   public static final int VARIABLE = 29;
/*     */   public static final int QUERY_TIMEOUT = 30;
/*     */   public static final int REDO_LOG_BINARY = 31;
/*     */   public static final int JAVA_OBJECT_SERIALIZER = 32;
/*     */   public static final int RETENTION_TIME = 33;
/*     */   public static final int QUERY_STATISTICS = 34;
/*     */   public static final int QUERY_STATISTICS_MAX_ENTRIES = 35;
/*     */   public static final int LAZY_QUERY_EXECUTION = 36;
/*     */   public static final int BUILTIN_ALIAS_OVERRIDE = 37;
/*     */   public static final int AUTHENTICATOR = 38;
/*     */   public static final int IGNORE_CATALOGS = 39;
/*     */   public static final int CATALOG = 40;
/*     */   public static final int NON_KEYWORDS = 41;
/*     */   public static final int TIME_ZONE = 42;
/*     */   public static final int VARIABLE_BINARY = 43;
/*     */   public static final int DEFAULT_NULL_ORDERING = 44;
/*     */   public static final int TRUNCATE_LARGE_LENGTH = 45;
/*     */   private static final int COUNT = 46;
/*     */   private static final ArrayList<String> TYPES;
/*     */   
/*     */   static {
/* 254 */     ArrayList<String> arrayList = new ArrayList(46);
/* 255 */     arrayList.add("IGNORECASE");
/* 256 */     arrayList.add("MAX_LOG_SIZE");
/* 257 */     arrayList.add("MODE");
/* 258 */     arrayList.add("READONLY");
/* 259 */     arrayList.add("LOCK_TIMEOUT");
/* 260 */     arrayList.add("DEFAULT_LOCK_TIMEOUT");
/* 261 */     arrayList.add("DEFAULT_TABLE_TYPE");
/* 262 */     arrayList.add("CACHE_SIZE");
/* 263 */     arrayList.add("TRACE_LEVEL_SYSTEM_OUT");
/* 264 */     arrayList.add("TRACE_LEVEL_FILE");
/* 265 */     arrayList.add("TRACE_MAX_FILE_SIZE");
/* 266 */     arrayList.add("COLLATION");
/* 267 */     arrayList.add("CLUSTER");
/* 268 */     arrayList.add("WRITE_DELAY");
/* 269 */     arrayList.add("DATABASE_EVENT_LISTENER");
/* 270 */     arrayList.add("MAX_MEMORY_ROWS");
/* 271 */     arrayList.add("LOCK_MODE");
/* 272 */     arrayList.add("DB_CLOSE_DELAY");
/* 273 */     arrayList.add("THROTTLE");
/* 274 */     arrayList.add("MAX_MEMORY_UNDO");
/* 275 */     arrayList.add("MAX_LENGTH_INPLACE_LOB");
/* 276 */     arrayList.add("ALLOW_LITERALS");
/* 277 */     arrayList.add("SCHEMA");
/* 278 */     arrayList.add("OPTIMIZE_REUSE_RESULTS");
/* 279 */     arrayList.add("SCHEMA_SEARCH_PATH");
/* 280 */     arrayList.add("REFERENTIAL_INTEGRITY");
/* 281 */     arrayList.add("MAX_OPERATION_MEMORY");
/* 282 */     arrayList.add("EXCLUSIVE");
/* 283 */     arrayList.add("CREATE_BUILD");
/* 284 */     arrayList.add("@");
/* 285 */     arrayList.add("QUERY_TIMEOUT");
/* 286 */     arrayList.add("REDO_LOG_BINARY");
/* 287 */     arrayList.add("JAVA_OBJECT_SERIALIZER");
/* 288 */     arrayList.add("RETENTION_TIME");
/* 289 */     arrayList.add("QUERY_STATISTICS");
/* 290 */     arrayList.add("QUERY_STATISTICS_MAX_ENTRIES");
/* 291 */     arrayList.add("LAZY_QUERY_EXECUTION");
/* 292 */     arrayList.add("BUILTIN_ALIAS_OVERRIDE");
/* 293 */     arrayList.add("AUTHENTICATOR");
/* 294 */     arrayList.add("IGNORE_CATALOGS");
/* 295 */     arrayList.add("CATALOG");
/* 296 */     arrayList.add("NON_KEYWORDS");
/* 297 */     arrayList.add("TIME ZONE");
/* 298 */     arrayList.add("VARIABLE_BINARY");
/* 299 */     arrayList.add("DEFAULT_NULL_ORDERING");
/* 300 */     arrayList.add("TRUNCATE_LARGE_LENGTH");
/* 301 */     TYPES = arrayList;
/* 302 */     assert arrayList.size() == 46;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getType(String paramString) {
/* 312 */     return TYPES.indexOf(paramString);
/*     */   }
/*     */   
/*     */   public static ArrayList<String> getTypes() {
/* 316 */     return TYPES;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getTypeName(int paramInt) {
/* 326 */     return TYPES.get(paramInt);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\SetTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */