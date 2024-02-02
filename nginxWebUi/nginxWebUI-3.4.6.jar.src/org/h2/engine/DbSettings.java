/*     */ package org.h2.engine;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import org.h2.message.DbException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DbSettings
/*     */   extends SettingsBase
/*     */ {
/*     */   static final int TABLE_SIZE = 64;
/*  36 */   public static final DbSettings DEFAULT = new DbSettings(new HashMap<>(64));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   public final int analyzeAuto = get("ANALYZE_AUTO", 2000);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   public final int analyzeSample = get("ANALYZE_SAMPLE", 10000);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   public final int autoCompactFillRate = get("AUTO_COMPACT_FILL_RATE", 90);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean databaseToLower;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean databaseToUpper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   public final boolean caseInsensitiveIdentifiers = get("CASE_INSENSITIVE_IDENTIFIERS", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  96 */   public final boolean dbCloseOnExit = get("DB_CLOSE_ON_EXIT", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 106 */   public final boolean defaultConnection = get("DEFAULT_CONNECTION", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 113 */   public final String defaultEscape = get("DEFAULT_ESCAPE", "\\");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 121 */   public final boolean defragAlways = get("DEFRAG_ALWAYS", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 128 */   public final boolean dropRestrict = get("DROP_RESTRICT", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 136 */   public final int estimatedFunctionTableRows = get("ESTIMATED_FUNCTION_TABLE_ROWS", 1000);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 146 */   public final int lobTimeout = get("LOB_TIMEOUT", 300000);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 152 */   public final int maxCompactTime = get("MAX_COMPACT_TIME", 200);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 160 */   public final int maxQueryTimeout = get("MAX_QUERY_TIMEOUT", 0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 174 */   public final boolean optimizeDistinct = get("OPTIMIZE_DISTINCT", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 181 */   public final boolean optimizeEvaluatableSubqueries = get("OPTIMIZE_EVALUATABLE_SUBQUERIES", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 190 */   public final boolean optimizeInsertFromSelect = get("OPTIMIZE_INSERT_FROM_SELECT", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 198 */   public final boolean optimizeInList = get("OPTIMIZE_IN_LIST", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 205 */   public final boolean optimizeInSelect = get("OPTIMIZE_IN_SELECT", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 211 */   public final boolean optimizeOr = get("OPTIMIZE_OR", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 218 */   public final boolean optimizeTwoEquals = get("OPTIMIZE_TWO_EQUALS", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 224 */   public final boolean optimizeSimpleSingleRowSubqueries = get("OPTIMIZE_SIMPLE_SINGLE_ROW_SUBQUERIES", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 237 */   public final int queryCacheSize = get("QUERY_CACHE_SIZE", 8);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 243 */   public final boolean recompileAlways = get("RECOMPILE_ALWAYS", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 251 */   public final boolean reuseSpace = get("REUSE_SPACE", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 260 */   public final boolean shareLinkedConnections = get("SHARE_LINKED_CONNECTIONS", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 268 */   public final String defaultTableEngine = get("DEFAULT_TABLE_ENGINE", (String)null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 275 */   public final boolean mvStore = get("MV_STORE", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 282 */   public final boolean compressData = get("COMPRESS", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 290 */   public final boolean ignoreCatalogs = get("IGNORE_CATALOGS", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 297 */   public final boolean zeroBasedEnums = get("ZERO_BASED_ENUMS", false);
/*     */   
/*     */   private DbSettings(HashMap<String, String> paramHashMap) {
/* 300 */     super(paramHashMap);
/* 301 */     boolean bool1 = get("DATABASE_TO_LOWER", false);
/* 302 */     boolean bool2 = containsKey("DATABASE_TO_UPPER");
/* 303 */     boolean bool3 = get("DATABASE_TO_UPPER", true);
/* 304 */     if (bool1 && bool3) {
/* 305 */       if (bool2) {
/* 306 */         throw DbException.get(90021, "DATABASE_TO_LOWER & DATABASE_TO_UPPER");
/*     */       }
/*     */       
/* 309 */       bool3 = false;
/*     */     } 
/* 311 */     this.databaseToLower = bool1;
/* 312 */     this.databaseToUpper = bool3;
/* 313 */     HashMap<String, String> hashMap = getSettings();
/* 314 */     hashMap.put("DATABASE_TO_LOWER", Boolean.toString(bool1));
/* 315 */     hashMap.put("DATABASE_TO_UPPER", Boolean.toString(bool3));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static DbSettings getInstance(HashMap<String, String> paramHashMap) {
/* 326 */     return new DbSettings(paramHashMap);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\DbSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */