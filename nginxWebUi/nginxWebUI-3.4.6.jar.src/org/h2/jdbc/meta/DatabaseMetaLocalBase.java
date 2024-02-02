/*     */ package org.h2.jdbc.meta;
/*     */ 
/*     */ import org.h2.engine.Constants;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.SimpleResult;
/*     */ import org.h2.value.TypeInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class DatabaseMetaLocalBase
/*     */   extends DatabaseMeta
/*     */ {
/*     */   public final String getDatabaseProductVersion() {
/*  20 */     return Constants.FULL_VERSION;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ResultInterface getVersionColumns(String paramString1, String paramString2, String paramString3) {
/*  25 */     checkClosed();
/*  26 */     SimpleResult simpleResult = new SimpleResult();
/*  27 */     simpleResult.addColumn("SCOPE", TypeInfo.TYPE_SMALLINT);
/*  28 */     simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
/*  29 */     simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
/*  30 */     simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
/*  31 */     simpleResult.addColumn("COLUMN_SIZE", TypeInfo.TYPE_INTEGER);
/*  32 */     simpleResult.addColumn("BUFFER_LENGTH", TypeInfo.TYPE_INTEGER);
/*  33 */     simpleResult.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_SMALLINT);
/*  34 */     simpleResult.addColumn("PSEUDO_COLUMN", TypeInfo.TYPE_SMALLINT);
/*  35 */     return (ResultInterface)simpleResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ResultInterface getUDTs(String paramString1, String paramString2, String paramString3, int[] paramArrayOfint) {
/*  40 */     checkClosed();
/*  41 */     SimpleResult simpleResult = new SimpleResult();
/*  42 */     simpleResult.addColumn("TYPE_CAT", TypeInfo.TYPE_VARCHAR);
/*  43 */     simpleResult.addColumn("TYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  44 */     simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
/*  45 */     simpleResult.addColumn("CLASS_NAME", TypeInfo.TYPE_VARCHAR);
/*  46 */     simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
/*  47 */     simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
/*  48 */     simpleResult.addColumn("BASE_TYPE", TypeInfo.TYPE_SMALLINT);
/*  49 */     return (ResultInterface)simpleResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ResultInterface getSuperTypes(String paramString1, String paramString2, String paramString3) {
/*  54 */     checkClosed();
/*  55 */     SimpleResult simpleResult = new SimpleResult();
/*  56 */     simpleResult.addColumn("TYPE_CAT", TypeInfo.TYPE_VARCHAR);
/*  57 */     simpleResult.addColumn("TYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  58 */     simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
/*  59 */     simpleResult.addColumn("SUPERTYPE_CAT", TypeInfo.TYPE_VARCHAR);
/*  60 */     simpleResult.addColumn("SUPERTYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  61 */     simpleResult.addColumn("SUPERTYPE_NAME", TypeInfo.TYPE_VARCHAR);
/*  62 */     return (ResultInterface)simpleResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ResultInterface getSuperTables(String paramString1, String paramString2, String paramString3) {
/*  67 */     checkClosed();
/*  68 */     SimpleResult simpleResult = new SimpleResult();
/*  69 */     simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
/*  70 */     simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  71 */     simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
/*  72 */     simpleResult.addColumn("SUPERTABLE_NAME", TypeInfo.TYPE_VARCHAR);
/*  73 */     return (ResultInterface)simpleResult;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final ResultInterface getAttributes(String paramString1, String paramString2, String paramString3, String paramString4) {
/*  79 */     checkClosed();
/*  80 */     SimpleResult simpleResult = new SimpleResult();
/*  81 */     simpleResult.addColumn("TYPE_CAT", TypeInfo.TYPE_VARCHAR);
/*  82 */     simpleResult.addColumn("TYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  83 */     simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
/*  84 */     simpleResult.addColumn("ATTR_NAME", TypeInfo.TYPE_VARCHAR);
/*  85 */     simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
/*  86 */     simpleResult.addColumn("ATTR_TYPE_NAME", TypeInfo.TYPE_VARCHAR);
/*  87 */     simpleResult.addColumn("ATTR_SIZE", TypeInfo.TYPE_INTEGER);
/*  88 */     simpleResult.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_INTEGER);
/*  89 */     simpleResult.addColumn("NUM_PREC_RADIX", TypeInfo.TYPE_INTEGER);
/*  90 */     simpleResult.addColumn("NULLABLE", TypeInfo.TYPE_INTEGER);
/*  91 */     simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
/*  92 */     simpleResult.addColumn("ATTR_DEF", TypeInfo.TYPE_VARCHAR);
/*  93 */     simpleResult.addColumn("SQL_DATA_TYPE", TypeInfo.TYPE_INTEGER);
/*  94 */     simpleResult.addColumn("SQL_DATETIME_SUB", TypeInfo.TYPE_INTEGER);
/*  95 */     simpleResult.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
/*  96 */     simpleResult.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER);
/*  97 */     simpleResult.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
/*  98 */     simpleResult.addColumn("SCOPE_CATALOG", TypeInfo.TYPE_VARCHAR);
/*  99 */     simpleResult.addColumn("SCOPE_SCHEMA", TypeInfo.TYPE_VARCHAR);
/* 100 */     simpleResult.addColumn("SCOPE_TABLE", TypeInfo.TYPE_VARCHAR);
/* 101 */     simpleResult.addColumn("SOURCE_DATA_TYPE", TypeInfo.TYPE_SMALLINT);
/* 102 */     return (ResultInterface)simpleResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getDatabaseMajorVersion() {
/* 107 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getDatabaseMinorVersion() {
/* 112 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ResultInterface getFunctions(String paramString1, String paramString2, String paramString3) {
/* 117 */     checkClosed();
/* 118 */     SimpleResult simpleResult = new SimpleResult();
/* 119 */     simpleResult.addColumn("FUNCTION_CAT", TypeInfo.TYPE_VARCHAR);
/* 120 */     simpleResult.addColumn("FUNCTION_SCHEM", TypeInfo.TYPE_VARCHAR);
/* 121 */     simpleResult.addColumn("FUNCTION_NAME", TypeInfo.TYPE_VARCHAR);
/* 122 */     simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
/* 123 */     simpleResult.addColumn("FUNCTION_TYPE", TypeInfo.TYPE_SMALLINT);
/* 124 */     simpleResult.addColumn("SPECIFIC_NAME", TypeInfo.TYPE_VARCHAR);
/* 125 */     return (ResultInterface)simpleResult;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final ResultInterface getFunctionColumns(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 131 */     checkClosed();
/* 132 */     SimpleResult simpleResult = new SimpleResult();
/* 133 */     simpleResult.addColumn("FUNCTION_CAT", TypeInfo.TYPE_VARCHAR);
/* 134 */     simpleResult.addColumn("FUNCTION_SCHEM", TypeInfo.TYPE_VARCHAR);
/* 135 */     simpleResult.addColumn("FUNCTION_NAME", TypeInfo.TYPE_VARCHAR);
/* 136 */     simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
/* 137 */     simpleResult.addColumn("COLUMN_TYPE", TypeInfo.TYPE_SMALLINT);
/* 138 */     simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
/* 139 */     simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
/* 140 */     simpleResult.addColumn("PRECISION", TypeInfo.TYPE_INTEGER);
/* 141 */     simpleResult.addColumn("LENGTH", TypeInfo.TYPE_INTEGER);
/* 142 */     simpleResult.addColumn("SCALE", TypeInfo.TYPE_SMALLINT);
/* 143 */     simpleResult.addColumn("RADIX", TypeInfo.TYPE_SMALLINT);
/* 144 */     simpleResult.addColumn("NULLABLE", TypeInfo.TYPE_SMALLINT);
/* 145 */     simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
/* 146 */     simpleResult.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
/* 147 */     simpleResult.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER);
/* 148 */     simpleResult.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
/* 149 */     simpleResult.addColumn("SPECIFIC_NAME", TypeInfo.TYPE_VARCHAR);
/* 150 */     return (ResultInterface)simpleResult;
/*     */   }
/*     */   
/*     */   final SimpleResult getPseudoColumnsResult() {
/* 154 */     checkClosed();
/* 155 */     SimpleResult simpleResult = new SimpleResult();
/* 156 */     simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
/* 157 */     simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
/* 158 */     simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
/* 159 */     simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
/* 160 */     simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
/* 161 */     simpleResult.addColumn("COLUMN_SIZE", TypeInfo.TYPE_INTEGER);
/* 162 */     simpleResult.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_INTEGER);
/* 163 */     simpleResult.addColumn("NUM_PREC_RADIX", TypeInfo.TYPE_INTEGER);
/* 164 */     simpleResult.addColumn("COLUMN_USAGE", TypeInfo.TYPE_VARCHAR);
/* 165 */     simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
/* 166 */     simpleResult.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
/* 167 */     simpleResult.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
/* 168 */     return simpleResult;
/*     */   }
/*     */   
/*     */   abstract void checkClosed();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\meta\DatabaseMetaLocalBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */