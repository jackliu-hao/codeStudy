/*     */ package org.h2.jdbc.meta;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import org.h2.command.CommandInterface;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.expression.ParameterInterface;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mode.DefaultNullOrdering;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueVarchar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class DatabaseMetaLegacy
/*     */   extends DatabaseMetaLocalBase
/*     */ {
/*  31 */   private static final Value PERCENT = ValueVarchar.get("%");
/*     */   
/*  33 */   private static final Value BACKSLASH = ValueVarchar.get("\\");
/*     */   
/*  35 */   private static final Value YES = ValueVarchar.get("YES");
/*     */   
/*  37 */   private static final Value NO = ValueVarchar.get("NO");
/*     */   
/*  39 */   private static final Value SCHEMA_MAIN = ValueVarchar.get("PUBLIC");
/*     */   
/*     */   private final Session session;
/*     */   
/*     */   public DatabaseMetaLegacy(Session paramSession) {
/*  44 */     this.session = paramSession;
/*     */   }
/*     */ 
/*     */   
/*     */   public final DefaultNullOrdering defaultNullOrdering() {
/*  49 */     return DefaultNullOrdering.LOW;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSQLKeywords() {
/*  54 */     return "CURRENT_CATALOG,CURRENT_SCHEMA,GROUPS,IF,ILIKE,INTERSECTS,KEY,LIMIT,MINUS,OFFSET,QUALIFY,REGEXP,ROWNUM,SYSDATE,SYSTIME,SYSTIMESTAMP,TODAY,TOP,_ROWID_";
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
/*     */ 
/*     */   
/*     */   public String getNumericFunctions() {
/*  71 */     return getFunctions("Functions (Numeric)");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStringFunctions() {
/*  76 */     return getFunctions("Functions (String)");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSystemFunctions() {
/*  81 */     return getFunctions("Functions (System)");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTimeDateFunctions() {
/*  86 */     return getFunctions("Functions (Time and Date)");
/*     */   }
/*     */   
/*     */   private String getFunctions(String paramString) {
/*  90 */     String str = "SELECT TOPIC FROM INFORMATION_SCHEMA.HELP WHERE SECTION = ?";
/*  91 */     Value[] arrayOfValue = { getString(paramString) };
/*  92 */     ResultInterface resultInterface = executeQuery(str, arrayOfValue);
/*  93 */     StringBuilder stringBuilder = new StringBuilder();
/*  94 */     while (resultInterface.next()) {
/*  95 */       String str1 = resultInterface.currentRow()[0].getString().trim();
/*  96 */       String[] arrayOfString = StringUtils.arraySplit(str1, ',', true);
/*  97 */       for (String str2 : arrayOfString) {
/*  98 */         if (stringBuilder.length() != 0) {
/*  99 */           stringBuilder.append(',');
/*     */         }
/* 101 */         String str3 = str2.trim();
/* 102 */         int i = str3.indexOf(' ');
/* 103 */         if (i >= 0) {
/*     */           
/* 105 */           StringUtils.trimSubstring(stringBuilder, str3, 0, i);
/*     */         } else {
/* 107 */           stringBuilder.append(str3);
/*     */         } 
/*     */       } 
/*     */     } 
/* 111 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSearchStringEscape() {
/* 116 */     return "\\";
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getProcedures(String paramString1, String paramString2, String paramString3) {
/* 121 */     return executeQuery("SELECT ALIAS_CATALOG PROCEDURE_CAT, ALIAS_SCHEMA PROCEDURE_SCHEM, ALIAS_NAME PROCEDURE_NAME, COLUMN_COUNT NUM_INPUT_PARAMS, ZERO() NUM_OUTPUT_PARAMS, ZERO() NUM_RESULT_SETS, REMARKS, RETURNS_RESULT PROCEDURE_TYPE, ALIAS_NAME SPECIFIC_NAME FROM INFORMATION_SCHEMA.FUNCTION_ALIASES WHERE ALIAS_CATALOG LIKE ?1 ESCAPE ?4 AND ALIAS_SCHEMA LIKE ?2 ESCAPE ?4 AND ALIAS_NAME LIKE ?3 ESCAPE ?4 ORDER BY PROCEDURE_SCHEM, PROCEDURE_NAME, NUM_INPUT_PARAMS", new Value[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 136 */           getCatalogPattern(paramString1), 
/* 137 */           getSchemaPattern(paramString2), 
/* 138 */           getPattern(paramString3), BACKSLASH
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getProcedureColumns(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 145 */     return executeQuery("SELECT ALIAS_CATALOG PROCEDURE_CAT, ALIAS_SCHEMA PROCEDURE_SCHEM, ALIAS_NAME PROCEDURE_NAME, COLUMN_NAME, COLUMN_TYPE, DATA_TYPE, TYPE_NAME, PRECISION, PRECISION LENGTH, SCALE, RADIX, NULLABLE, REMARKS, COLUMN_DEFAULT COLUMN_DEF, ZERO() SQL_DATA_TYPE, ZERO() SQL_DATETIME_SUB, ZERO() CHAR_OCTET_LENGTH, POS ORDINAL_POSITION, ?1 IS_NULLABLE, ALIAS_NAME SPECIFIC_NAME FROM INFORMATION_SCHEMA.FUNCTION_COLUMNS WHERE ALIAS_CATALOG LIKE ?2 ESCAPE ?6 AND ALIAS_SCHEMA LIKE ?3 ESCAPE ?6 AND ALIAS_NAME LIKE ?4 ESCAPE ?6 AND COLUMN_NAME LIKE ?5 ESCAPE ?6 ORDER BY PROCEDURE_SCHEM, PROCEDURE_NAME, ORDINAL_POSITION", new Value[] { YES, 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 173 */           getCatalogPattern(paramString1), 
/* 174 */           getSchemaPattern(paramString2), 
/* 175 */           getPattern(paramString3), 
/* 176 */           getPattern(paramString4), BACKSLASH });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getTables(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
/* 182 */     byte b1 = (paramArrayOfString != null) ? paramArrayOfString.length : 0;
/* 183 */     boolean bool = (paramArrayOfString == null || Arrays.<String>asList(paramArrayOfString).contains("SYNONYM")) ? true : false;
/*     */     
/* 185 */     StringBuilder stringBuilder = new StringBuilder(1008);
/* 186 */     if (bool) {
/* 187 */       stringBuilder.append("SELECT TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS, TYPE_CAT, TYPE_SCHEM, TYPE_NAME, SELF_REFERENCING_COL_NAME, REF_GENERATION, SQL FROM (SELECT SYNONYM_CATALOG TABLE_CAT, SYNONYM_SCHEMA TABLE_SCHEM, SYNONYM_NAME as TABLE_NAME, TYPE_NAME AS TABLE_TYPE, REMARKS, TYPE_NAME TYPE_CAT, TYPE_NAME TYPE_SCHEM, TYPE_NAME AS TYPE_NAME, TYPE_NAME SELF_REFERENCING_COL_NAME, TYPE_NAME REF_GENERATION, NULL AS SQL FROM INFORMATION_SCHEMA.SYNONYMS WHERE SYNONYM_CATALOG LIKE ?1 ESCAPE ?4 AND SYNONYM_SCHEMA LIKE ?2 ESCAPE ?4 AND SYNONYM_NAME LIKE ?3 ESCAPE ?4 UNION ");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 218 */     stringBuilder.append("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS, TYPE_NAME TYPE_CAT, TYPE_NAME TYPE_SCHEM, TYPE_NAME, TYPE_NAME SELF_REFERENCING_COL_NAME, TYPE_NAME REF_GENERATION, SQL FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?4 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND TABLE_NAME LIKE ?3 ESCAPE ?4");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 234 */     if (b1) {
/* 235 */       stringBuilder.append(" AND TABLE_TYPE IN(");
/* 236 */       for (byte b = 0; b < b1; b++) {
/* 237 */         if (b > 0) {
/* 238 */           stringBuilder.append(", ");
/*     */         }
/* 240 */         stringBuilder.append('?').append(b + 5);
/*     */       } 
/* 242 */       stringBuilder.append(')');
/*     */     } 
/* 244 */     if (bool) {
/* 245 */       stringBuilder.append(')');
/*     */     }
/* 247 */     Value[] arrayOfValue = new Value[b1 + 4];
/* 248 */     arrayOfValue[0] = getCatalogPattern(paramString1);
/* 249 */     arrayOfValue[1] = getSchemaPattern(paramString2);
/* 250 */     arrayOfValue[2] = getPattern(paramString3);
/* 251 */     arrayOfValue[3] = BACKSLASH;
/* 252 */     for (byte b2 = 0; b2 < b1; b2++) {
/* 253 */       arrayOfValue[b2 + 4] = getString(paramArrayOfString[b2]);
/*     */     }
/* 255 */     return executeQuery(stringBuilder.append(" ORDER BY TABLE_TYPE, TABLE_SCHEM, TABLE_NAME").toString(), arrayOfValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getSchemas() {
/* 260 */     return executeQuery("SELECT SCHEMA_NAME TABLE_SCHEM, CATALOG_NAME TABLE_CATALOG FROM INFORMATION_SCHEMA.SCHEMATA ORDER BY SCHEMA_NAME", new Value[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getCatalogs() {
/* 269 */     return executeQuery("SELECT CATALOG_NAME TABLE_CAT FROM INFORMATION_SCHEMA.CATALOGS", new Value[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getTableTypes() {
/* 275 */     return executeQuery("SELECT TYPE TABLE_TYPE FROM INFORMATION_SCHEMA.TABLE_TYPES ORDER BY TABLE_TYPE", new Value[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getColumns(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 284 */     return executeQuery("SELECT TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME, COLUMN_SIZE, BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB, CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE, SCOPE_CATALOG, SCOPE_SCHEMA, SCOPE_TABLE, SOURCE_DATA_TYPE, IS_AUTOINCREMENT, IS_GENERATEDCOLUMN FROM (SELECT s.SYNONYM_CATALOG TABLE_CAT, s.SYNONYM_SCHEMA TABLE_SCHEM, s.SYNONYM_NAME TABLE_NAME, c.COLUMN_NAME, c.DATA_TYPE, c.TYPE_NAME, c.CHARACTER_MAXIMUM_LENGTH COLUMN_SIZE, c.CHARACTER_MAXIMUM_LENGTH BUFFER_LENGTH, c.NUMERIC_SCALE DECIMAL_DIGITS, c.NUMERIC_PRECISION_RADIX NUM_PREC_RADIX, c.NULLABLE, c.REMARKS, c.COLUMN_DEFAULT COLUMN_DEF, c.DATA_TYPE SQL_DATA_TYPE, ZERO() SQL_DATETIME_SUB, c.CHARACTER_OCTET_LENGTH CHAR_OCTET_LENGTH, c.ORDINAL_POSITION, c.IS_NULLABLE IS_NULLABLE, CAST(c.SOURCE_DATA_TYPE AS VARCHAR) SCOPE_CATALOG, CAST(c.SOURCE_DATA_TYPE AS VARCHAR) SCOPE_SCHEMA, CAST(c.SOURCE_DATA_TYPE AS VARCHAR) SCOPE_TABLE, c.SOURCE_DATA_TYPE, CASE WHEN c.SEQUENCE_NAME IS NULL THEN CAST(?1 AS VARCHAR) ELSE CAST(?2 AS VARCHAR) END IS_AUTOINCREMENT, CASE WHEN c.IS_COMPUTED THEN CAST(?2 AS VARCHAR) ELSE CAST(?1 AS VARCHAR) END IS_GENERATEDCOLUMN FROM INFORMATION_SCHEMA.COLUMNS c JOIN INFORMATION_SCHEMA.SYNONYMS s ON s.SYNONYM_FOR = c.TABLE_NAME AND s.SYNONYM_FOR_SCHEMA = c.TABLE_SCHEMA WHERE s.SYNONYM_CATALOG LIKE ?3 ESCAPE ?7 AND s.SYNONYM_SCHEMA LIKE ?4 ESCAPE ?7 AND s.SYNONYM_NAME LIKE ?5 ESCAPE ?7 AND c.COLUMN_NAME LIKE ?6 ESCAPE ?7 UNION SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME, CHARACTER_MAXIMUM_LENGTH COLUMN_SIZE, CHARACTER_MAXIMUM_LENGTH BUFFER_LENGTH, NUMERIC_SCALE DECIMAL_DIGITS, NUMERIC_PRECISION_RADIX NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEFAULT COLUMN_DEF, DATA_TYPE SQL_DATA_TYPE, ZERO() SQL_DATETIME_SUB, CHARACTER_OCTET_LENGTH CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE IS_NULLABLE, CAST(SOURCE_DATA_TYPE AS VARCHAR) SCOPE_CATALOG, CAST(SOURCE_DATA_TYPE AS VARCHAR) SCOPE_SCHEMA, CAST(SOURCE_DATA_TYPE AS VARCHAR) SCOPE_TABLE, SOURCE_DATA_TYPE, CASE WHEN SEQUENCE_NAME IS NULL THEN CAST(?1 AS VARCHAR) ELSE CAST(?2 AS VARCHAR) END IS_AUTOINCREMENT, CASE WHEN IS_COMPUTED THEN CAST(?2 AS VARCHAR) ELSE CAST(?1 AS VARCHAR) END IS_GENERATEDCOLUMN FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG LIKE ?3 ESCAPE ?7 AND TABLE_SCHEMA LIKE ?4 ESCAPE ?7 AND TABLE_NAME LIKE ?5 ESCAPE ?7 AND COLUMN_NAME LIKE ?6 ESCAPE ?7) ORDER BY TABLE_SCHEM, TABLE_NAME, ORDINAL_POSITION", new Value[] { NO, YES, 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 379 */           getCatalogPattern(paramString1), 
/* 380 */           getSchemaPattern(paramString2), 
/* 381 */           getPattern(paramString3), 
/* 382 */           getPattern(paramString4), BACKSLASH });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getColumnPrivileges(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 388 */     return executeQuery("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, GRANTOR, GRANTEE, PRIVILEGE_TYPE PRIVILEGE, IS_GRANTABLE FROM INFORMATION_SCHEMA.COLUMN_PRIVILEGES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?5 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?5 AND TABLE_NAME = ?3 AND COLUMN_NAME LIKE ?4 ESCAPE ?5 ORDER BY COLUMN_NAME, PRIVILEGE", new Value[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 403 */           getCatalogPattern(paramString1), 
/* 404 */           getSchemaPattern(paramString2), 
/* 405 */           getString(paramString3), 
/* 406 */           getPattern(paramString4), BACKSLASH
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getTablePrivileges(String paramString1, String paramString2, String paramString3) {
/* 412 */     return executeQuery("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, GRANTOR, GRANTEE, PRIVILEGE_TYPE PRIVILEGE, IS_GRANTABLE FROM INFORMATION_SCHEMA.TABLE_PRIVILEGES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?4 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND TABLE_NAME LIKE ?3 ESCAPE ?4 ORDER BY TABLE_SCHEM, TABLE_NAME, PRIVILEGE", new Value[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 425 */           getCatalogPattern(paramString1), 
/* 426 */           getSchemaPattern(paramString2), 
/* 427 */           getPattern(paramString3), BACKSLASH
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getBestRowIdentifier(String paramString1, String paramString2, String paramString3, int paramInt, boolean paramBoolean) {
/* 434 */     return executeQuery("SELECT CAST(?1 AS SMALLINT) SCOPE, C.COLUMN_NAME, C.DATA_TYPE, C.TYPE_NAME, C.CHARACTER_MAXIMUM_LENGTH COLUMN_SIZE, C.CHARACTER_MAXIMUM_LENGTH BUFFER_LENGTH, CAST(C.NUMERIC_SCALE AS SMALLINT) DECIMAL_DIGITS, CAST(?2 AS SMALLINT) PSEUDO_COLUMN FROM INFORMATION_SCHEMA.INDEXES I, INFORMATION_SCHEMA.COLUMNS C WHERE C.TABLE_NAME = I.TABLE_NAME AND C.COLUMN_NAME = I.COLUMN_NAME AND C.TABLE_CATALOG LIKE ?3 ESCAPE ?6 AND C.TABLE_SCHEMA LIKE ?4 ESCAPE ?6 AND C.TABLE_NAME = ?5 AND I.PRIMARY_KEY = TRUE ORDER BY SCOPE", new Value[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 453 */           (Value)ValueInteger.get(2), 
/*     */           
/* 455 */           (Value)ValueInteger.get(1), 
/* 456 */           getCatalogPattern(paramString1), 
/* 457 */           getSchemaPattern(paramString2), 
/* 458 */           getString(paramString3), BACKSLASH
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getPrimaryKeys(String paramString1, String paramString2, String paramString3) {
/* 464 */     return executeQuery("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION KEY_SEQ, COALESCE(CONSTRAINT_NAME, INDEX_NAME) PK_NAME FROM INFORMATION_SCHEMA.INDEXES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?4 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND TABLE_NAME = ?3 AND PRIMARY_KEY = TRUE ORDER BY COLUMN_NAME", new Value[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 477 */           getCatalogPattern(paramString1), 
/* 478 */           getSchemaPattern(paramString2), 
/* 479 */           getString(paramString3), BACKSLASH
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getImportedKeys(String paramString1, String paramString2, String paramString3) {
/* 485 */     return executeQuery("SELECT PKTABLE_CATALOG PKTABLE_CAT, PKTABLE_SCHEMA PKTABLE_SCHEM, PKTABLE_NAME PKTABLE_NAME, PKCOLUMN_NAME, FKTABLE_CATALOG FKTABLE_CAT, FKTABLE_SCHEMA FKTABLE_SCHEM, FKTABLE_NAME, FKCOLUMN_NAME, ORDINAL_POSITION KEY_SEQ, UPDATE_RULE, DELETE_RULE, FK_NAME, PK_NAME, DEFERRABILITY FROM INFORMATION_SCHEMA.CROSS_REFERENCES WHERE FKTABLE_CATALOG LIKE ?1 ESCAPE ?4 AND FKTABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND FKTABLE_NAME = ?3 ORDER BY PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME, FK_NAME, KEY_SEQ", new Value[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 505 */           getCatalogPattern(paramString1), 
/* 506 */           getSchemaPattern(paramString2), 
/* 507 */           getString(paramString3), BACKSLASH
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getExportedKeys(String paramString1, String paramString2, String paramString3) {
/* 513 */     return executeQuery("SELECT PKTABLE_CATALOG PKTABLE_CAT, PKTABLE_SCHEMA PKTABLE_SCHEM, PKTABLE_NAME PKTABLE_NAME, PKCOLUMN_NAME, FKTABLE_CATALOG FKTABLE_CAT, FKTABLE_SCHEMA FKTABLE_SCHEM, FKTABLE_NAME, FKCOLUMN_NAME, ORDINAL_POSITION KEY_SEQ, UPDATE_RULE, DELETE_RULE, FK_NAME, PK_NAME, DEFERRABILITY FROM INFORMATION_SCHEMA.CROSS_REFERENCES WHERE PKTABLE_CATALOG LIKE ?1 ESCAPE ?4 AND PKTABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND PKTABLE_NAME = ?3 ORDER BY FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, FK_NAME, KEY_SEQ", new Value[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 533 */           getCatalogPattern(paramString1), 
/* 534 */           getSchemaPattern(paramString2), 
/* 535 */           getString(paramString3), BACKSLASH
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getCrossReference(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6) {
/* 542 */     return executeQuery("SELECT PKTABLE_CATALOG PKTABLE_CAT, PKTABLE_SCHEMA PKTABLE_SCHEM, PKTABLE_NAME PKTABLE_NAME, PKCOLUMN_NAME, FKTABLE_CATALOG FKTABLE_CAT, FKTABLE_SCHEMA FKTABLE_SCHEM, FKTABLE_NAME, FKCOLUMN_NAME, ORDINAL_POSITION KEY_SEQ, UPDATE_RULE, DELETE_RULE, FK_NAME, PK_NAME, DEFERRABILITY FROM INFORMATION_SCHEMA.CROSS_REFERENCES WHERE PKTABLE_CATALOG LIKE ?1 ESCAPE ?7 AND PKTABLE_SCHEMA LIKE ?2 ESCAPE ?7 AND PKTABLE_NAME = ?3 AND FKTABLE_CATALOG LIKE ?4 ESCAPE ?7 AND FKTABLE_SCHEMA LIKE ?5 ESCAPE ?7 AND FKTABLE_NAME = ?6 ORDER BY FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, FK_NAME, KEY_SEQ", new Value[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 565 */           getCatalogPattern(paramString1), 
/* 566 */           getSchemaPattern(paramString2), 
/* 567 */           getString(paramString3), 
/* 568 */           getCatalogPattern(paramString4), 
/* 569 */           getSchemaPattern(paramString5), 
/* 570 */           getString(paramString6), BACKSLASH
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getTypeInfo() {
/* 576 */     return executeQuery("SELECT TYPE_NAME, DATA_TYPE, PRECISION, PREFIX LITERAL_PREFIX, SUFFIX LITERAL_SUFFIX, PARAMS CREATE_PARAMS, NULLABLE, CASE_SENSITIVE, SEARCHABLE, FALSE UNSIGNED_ATTRIBUTE, FALSE FIXED_PREC_SCALE, AUTO_INCREMENT, TYPE_NAME LOCAL_TYPE_NAME, MINIMUM_SCALE, MAXIMUM_SCALE, DATA_TYPE SQL_DATA_TYPE, ZERO() SQL_DATETIME_SUB, RADIX NUM_PREC_RADIX FROM INFORMATION_SCHEMA.TYPE_INFO ORDER BY DATA_TYPE, POS", new Value[0]);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getIndexInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2) {
/* 602 */     String str = paramBoolean1 ? "NON_UNIQUE=FALSE" : "TRUE";
/* 603 */     return executeQuery("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, NON_UNIQUE, TABLE_CATALOG INDEX_QUALIFIER, INDEX_NAME, INDEX_TYPE TYPE, ORDINAL_POSITION, COLUMN_NAME, ASC_OR_DESC, CARDINALITY, PAGES, FILTER_CONDITION, SORT_TYPE FROM INFORMATION_SCHEMA.INDEXES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?4 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND (" + str + ") AND TABLE_NAME = ?3 ORDER BY NON_UNIQUE, TYPE, TABLE_SCHEM, INDEX_NAME, ORDINAL_POSITION", new Value[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 625 */           getCatalogPattern(paramString1), 
/* 626 */           getSchemaPattern(paramString2), 
/* 627 */           getString(paramString3), BACKSLASH
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getSchemas(String paramString1, String paramString2) {
/* 633 */     return executeQuery("SELECT SCHEMA_NAME TABLE_SCHEM, CATALOG_NAME TABLE_CATALOG FROM INFORMATION_SCHEMA.SCHEMATA WHERE CATALOG_NAME LIKE ?1 ESCAPE ?3 AND SCHEMA_NAME LIKE ?2 ESCAPE ?3 ORDER BY SCHEMA_NAME", new Value[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 640 */           getCatalogPattern(paramString1), 
/* 641 */           getSchemaPattern(paramString2), BACKSLASH
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getPseudoColumns(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 648 */     return (ResultInterface)getPseudoColumnsResult();
/*     */   }
/*     */   
/*     */   private ResultInterface executeQuery(String paramString, Value... paramVarArgs) {
/* 652 */     checkClosed();
/* 653 */     synchronized (this.session) {
/* 654 */       CommandInterface commandInterface = this.session.prepareCommand(paramString, 2147483647);
/* 655 */       int i = paramVarArgs.length;
/* 656 */       if (i > 0) {
/* 657 */         ArrayList<ParameterInterface> arrayList = commandInterface.getParameters();
/* 658 */         for (byte b = 0; b < i; b++) {
/* 659 */           ((ParameterInterface)arrayList.get(b)).setValue(paramVarArgs[b], true);
/*     */         }
/*     */       } 
/* 662 */       ResultInterface resultInterface = commandInterface.executeQuery(0L, false);
/* 663 */       commandInterface.close();
/* 664 */       return resultInterface;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void checkClosed() {
/* 670 */     if (this.session.isClosed()) {
/* 671 */       throw DbException.get(90121);
/*     */     }
/*     */   }
/*     */   
/*     */   private Value getString(String paramString) {
/* 676 */     return (paramString != null) ? ValueVarchar.get(paramString, (CastDataProvider)this.session) : (Value)ValueNull.INSTANCE;
/*     */   }
/*     */   
/*     */   private Value getPattern(String paramString) {
/* 680 */     return (paramString == null) ? PERCENT : getString(paramString);
/*     */   }
/*     */   
/*     */   private Value getSchemaPattern(String paramString) {
/* 684 */     return (paramString == null) ? PERCENT : (paramString.isEmpty() ? SCHEMA_MAIN : getString(paramString));
/*     */   }
/*     */   
/*     */   private Value getCatalogPattern(String paramString) {
/* 688 */     return (paramString == null || paramString.isEmpty()) ? PERCENT : getString(paramString);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\meta\DatabaseMetaLegacy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */