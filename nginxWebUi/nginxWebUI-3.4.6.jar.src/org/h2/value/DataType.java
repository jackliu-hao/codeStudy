/*     */ package org.h2.value;
/*     */ 
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLType;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.h2.api.IntervalQualifier;
/*     */ import org.h2.engine.Mode;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DataType
/*     */ {
/*  33 */   private static final HashMap<String, DataType> TYPES_BY_NAME = new HashMap<>(128);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  38 */   static final DataType[] TYPES_BY_VALUE_TYPE = new DataType[42];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int type;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int sqlType;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long minPrecision;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long maxPrecision;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int minScale;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int maxScale;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String prefix;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String suffix;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String params;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean caseSensitive;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportsPrecision;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportsScale;
/*     */ 
/*     */ 
/*     */   
/*     */   public long defaultPrecision;
/*     */ 
/*     */ 
/*     */   
/*     */   public int defaultScale;
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean specialPrecisionScale;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 116 */     DataType dataType = new DataType();
/* 117 */     dataType.defaultPrecision = dataType.maxPrecision = dataType.minPrecision = 1L;
/* 118 */     add(0, 0, dataType, new String[] { "NULL" });
/* 119 */     add(1, 1, createString(true, true), new String[] { "CHARACTER", "CHAR", "NCHAR", "NATIONAL CHARACTER", "NATIONAL CHAR" });
/*     */     
/* 121 */     add(2, 12, createString(true, false), new String[] { "CHARACTER VARYING", "VARCHAR", "CHAR VARYING", "NCHAR VARYING", "NATIONAL CHARACTER VARYING", "NATIONAL CHAR VARYING", "VARCHAR2", "NVARCHAR", "NVARCHAR2", "VARCHAR_CASESENSITIVE", "TID", "LONGVARCHAR", "LONGNVARCHAR" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 127 */     add(3, 2005, createLob(true), new String[] { "CHARACTER LARGE OBJECT", "CLOB", "CHAR LARGE OBJECT", "TINYTEXT", "TEXT", "MEDIUMTEXT", "LONGTEXT", "NTEXT", "NCLOB", "NCHAR LARGE OBJECT", "NATIONAL CHARACTER LARGE OBJECT" });
/*     */ 
/*     */     
/* 130 */     add(4, 12, createString(false, false), new String[] { "VARCHAR_IGNORECASE" });
/* 131 */     add(5, -2, createBinary(true), new String[] { "BINARY" });
/* 132 */     add(6, -3, createBinary(false), new String[] { "BINARY VARYING", "VARBINARY", "RAW", "BYTEA", "LONG RAW", "LONGVARBINARY" });
/*     */     
/* 134 */     add(7, 2004, createLob(false), new String[] { "BINARY LARGE OBJECT", "BLOB", "TINYBLOB", "MEDIUMBLOB", "LONGBLOB", "IMAGE" });
/*     */     
/* 136 */     add(8, 16, createNumeric(1, 0), new String[] { "BOOLEAN", "BIT", "BOOL" });
/* 137 */     add(9, -6, createNumeric(8, 0), new String[] { "TINYINT" });
/* 138 */     add(10, 5, createNumeric(16, 0), new String[] { "SMALLINT", "INT2" });
/* 139 */     add(11, 4, createNumeric(32, 0), new String[] { "INTEGER", "INT", "MEDIUMINT", "INT4", "SIGNED" });
/*     */ 
/*     */     
/* 142 */     add(12, -5, createNumeric(64, 0), new String[] { "BIGINT", "INT8", "LONG" });
/*     */     
/* 144 */     dataType = new DataType();
/* 145 */     dataType.minPrecision = 1L;
/* 146 */     dataType.defaultPrecision = dataType.maxPrecision = 100000L;
/* 147 */     dataType.defaultScale = 0;
/* 148 */     dataType.maxScale = 100000;
/* 149 */     dataType.minScale = 0;
/* 150 */     dataType.params = "PRECISION,SCALE";
/* 151 */     dataType.supportsPrecision = true;
/* 152 */     dataType.supportsScale = true;
/* 153 */     add(13, 2, dataType, new String[] { "NUMERIC", "DECIMAL", "DEC" });
/* 154 */     add(14, 7, createNumeric(24, 0), new String[] { "REAL", "FLOAT4" });
/* 155 */     add(15, 8, createNumeric(53, 0), new String[] { "DOUBLE PRECISION", "DOUBLE", "FLOAT8" });
/*     */     
/* 157 */     add(15, 6, createNumeric(53, 0), new String[] { "FLOAT" });
/* 158 */     dataType = new DataType();
/* 159 */     dataType.minPrecision = 1L;
/* 160 */     dataType.defaultPrecision = dataType.maxPrecision = 100000L;
/* 161 */     dataType.params = "PRECISION";
/* 162 */     dataType.supportsPrecision = true;
/* 163 */     add(16, 2, dataType, new String[] { "DECFLOAT" });
/* 164 */     add(17, 91, createDate(10, 10, "DATE", false, 0, 0), new String[] { "DATE" });
/* 165 */     add(18, 92, 
/* 166 */         createDate(18, 8, "TIME", true, 0, 9), new String[] { "TIME", "TIME WITHOUT TIME ZONE" });
/*     */ 
/*     */     
/* 169 */     add(19, 2013, 
/* 170 */         createDate(24, 14, "TIME WITH TIME ZONE", true, 0, 9), new String[] { "TIME WITH TIME ZONE" });
/*     */ 
/*     */     
/* 173 */     add(20, 93, 
/* 174 */         createDate(29, 26, "TIMESTAMP", true, 6, 9), new String[] { "TIMESTAMP", "TIMESTAMP WITHOUT TIME ZONE", "DATETIME", "DATETIME2", "SMALLDATETIME" });
/*     */ 
/*     */     
/* 177 */     add(21, 2014, 
/* 178 */         createDate(35, 32, "TIMESTAMP WITH TIME ZONE", true, 6, 9), new String[] { "TIMESTAMP WITH TIME ZONE" });
/*     */ 
/*     */     
/* 181 */     for (byte b = 22; b <= 34; b++) {
/* 182 */       addInterval(b);
/*     */     }
/* 184 */     add(35, 2000, createBinary(false), new String[] { "JAVA_OBJECT", "OBJECT", "OTHER" });
/* 185 */     dataType = createString(false, false);
/* 186 */     dataType.supportsPrecision = false;
/* 187 */     dataType.params = "ELEMENT [,...]";
/* 188 */     add(36, 1111, dataType, new String[] { "ENUM" });
/* 189 */     add(37, 1111, createGeometry(), new String[] { "GEOMETRY" });
/* 190 */     add(38, 1111, createString(true, false, "JSON '", "'"), new String[] { "JSON" });
/* 191 */     dataType = new DataType();
/* 192 */     dataType.prefix = dataType.suffix = "'";
/* 193 */     dataType.defaultPrecision = dataType.maxPrecision = dataType.minPrecision = 16L;
/* 194 */     add(39, -2, dataType, new String[] { "UUID" });
/* 195 */     dataType = new DataType();
/* 196 */     dataType.prefix = "ARRAY[";
/* 197 */     dataType.suffix = "]";
/* 198 */     dataType.params = "CARDINALITY";
/* 199 */     dataType.supportsPrecision = true;
/* 200 */     dataType.defaultPrecision = dataType.maxPrecision = 65536L;
/* 201 */     add(40, 2003, dataType, new String[] { "ARRAY" });
/* 202 */     dataType = new DataType();
/* 203 */     dataType.prefix = "ROW(";
/* 204 */     dataType.suffix = ")";
/* 205 */     dataType.params = "NAME DATA_TYPE [,...]";
/* 206 */     add(41, 1111, dataType, new String[] { "ROW" });
/*     */   }
/*     */   
/*     */   private static void addInterval(int paramInt) {
/* 210 */     IntervalQualifier intervalQualifier = IntervalQualifier.valueOf(paramInt - 22);
/* 211 */     String str = intervalQualifier.toString();
/* 212 */     DataType dataType = new DataType();
/* 213 */     dataType.prefix = "INTERVAL '";
/* 214 */     dataType.suffix = "' " + str;
/* 215 */     dataType.supportsPrecision = true;
/* 216 */     dataType.defaultPrecision = 2L;
/* 217 */     dataType.minPrecision = 1L;
/* 218 */     dataType.maxPrecision = 18L;
/* 219 */     if (intervalQualifier.hasSeconds()) {
/* 220 */       dataType.supportsScale = true;
/* 221 */       dataType.defaultScale = 6;
/* 222 */       dataType.maxScale = 9;
/* 223 */       dataType.params = "PRECISION,SCALE";
/*     */     } else {
/* 225 */       dataType.params = "PRECISION";
/*     */     } 
/* 227 */     add(paramInt, 1111, dataType, new String[] { ("INTERVAL " + str).intern() });
/*     */   }
/*     */   
/*     */   private static void add(int paramInt1, int paramInt2, DataType paramDataType, String... paramVarArgs) {
/* 231 */     paramDataType.type = paramInt1;
/* 232 */     paramDataType.sqlType = paramInt2;
/* 233 */     if (TYPES_BY_VALUE_TYPE[paramInt1] == null) {
/* 234 */       TYPES_BY_VALUE_TYPE[paramInt1] = paramDataType;
/*     */     }
/* 236 */     for (String str : paramVarArgs) {
/* 237 */       TYPES_BY_NAME.put(str, paramDataType);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataType createNumeric(int paramInt1, int paramInt2) {
/* 249 */     DataType dataType = new DataType();
/* 250 */     dataType.defaultPrecision = dataType.maxPrecision = dataType.minPrecision = paramInt1;
/* 251 */     dataType.defaultScale = dataType.maxScale = dataType.minScale = paramInt2;
/* 252 */     return dataType;
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
/*     */   public static DataType createDate(int paramInt1, int paramInt2, String paramString, boolean paramBoolean, int paramInt3, int paramInt4) {
/* 268 */     DataType dataType = new DataType();
/* 269 */     dataType.prefix = paramString + " '";
/* 270 */     dataType.suffix = "'";
/* 271 */     dataType.maxPrecision = paramInt1;
/* 272 */     dataType.defaultPrecision = dataType.minPrecision = paramInt2;
/* 273 */     if (paramBoolean) {
/* 274 */       dataType.params = "SCALE";
/* 275 */       dataType.supportsScale = true;
/* 276 */       dataType.maxScale = paramInt4;
/* 277 */       dataType.defaultScale = paramInt3;
/*     */     } 
/* 279 */     return dataType;
/*     */   }
/*     */   
/*     */   private static DataType createString(boolean paramBoolean1, boolean paramBoolean2) {
/* 283 */     return createString(paramBoolean1, paramBoolean2, "'", "'");
/*     */   }
/*     */   
/*     */   private static DataType createBinary(boolean paramBoolean) {
/* 287 */     return createString(false, paramBoolean, "X'", "'");
/*     */   }
/*     */   
/*     */   private static DataType createString(boolean paramBoolean1, boolean paramBoolean2, String paramString1, String paramString2) {
/* 291 */     DataType dataType = new DataType();
/* 292 */     dataType.prefix = paramString1;
/* 293 */     dataType.suffix = paramString2;
/* 294 */     dataType.params = "LENGTH";
/* 295 */     dataType.caseSensitive = paramBoolean1;
/* 296 */     dataType.supportsPrecision = true;
/* 297 */     dataType.minPrecision = 1L;
/* 298 */     dataType.maxPrecision = 1048576L;
/* 299 */     dataType.defaultPrecision = paramBoolean2 ? 1L : 1048576L;
/* 300 */     return dataType;
/*     */   }
/*     */   
/*     */   private static DataType createLob(boolean paramBoolean) {
/* 304 */     DataType dataType = paramBoolean ? createString(true, false) : createBinary(false);
/* 305 */     dataType.maxPrecision = Long.MAX_VALUE;
/* 306 */     dataType.defaultPrecision = Long.MAX_VALUE;
/* 307 */     return dataType;
/*     */   }
/*     */   
/*     */   private static DataType createGeometry() {
/* 311 */     DataType dataType = new DataType();
/* 312 */     dataType.prefix = "'";
/* 313 */     dataType.suffix = "'";
/* 314 */     dataType.params = "TYPE,SRID";
/* 315 */     dataType.maxPrecision = Long.MAX_VALUE;
/* 316 */     dataType.defaultPrecision = Long.MAX_VALUE;
/* 317 */     return dataType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataType getDataType(int paramInt) {
/* 327 */     if (paramInt == -1) {
/* 328 */       throw DbException.get(50004, "?");
/*     */     }
/* 330 */     if (paramInt >= 0 && paramInt < 42) {
/* 331 */       return TYPES_BY_VALUE_TYPE[paramInt];
/*     */     }
/* 333 */     return TYPES_BY_VALUE_TYPE[0];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int convertTypeToSQLType(TypeInfo paramTypeInfo) {
/* 343 */     int i = paramTypeInfo.getValueType();
/* 344 */     switch (i) {
/*     */       case 13:
/* 346 */         return (paramTypeInfo.getExtTypeInfo() != null) ? 3 : 2;
/*     */       case 14:
/*     */       case 15:
/* 349 */         if (paramTypeInfo.getDeclaredPrecision() >= 0L) {
/* 350 */           return 6;
/*     */         }
/*     */         break;
/*     */     } 
/* 354 */     return (getDataType(i)).sqlType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int convertSQLTypeToValueType(int paramInt, String paramString) {
/*     */     DataType dataType;
/* 366 */     switch (paramInt) {
/*     */       case -2:
/* 368 */         if (paramString.equalsIgnoreCase("UUID")) {
/* 369 */           return 39;
/*     */         }
/*     */         break;
/*     */       case 1111:
/* 373 */         dataType = TYPES_BY_NAME.get(StringUtils.toUpperEnglish(paramString));
/* 374 */         if (dataType != null) {
/* 375 */           return dataType.type;
/*     */         }
/*     */         break;
/*     */     } 
/* 379 */     return convertSQLTypeToValueType(paramInt);
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
/*     */   public static int getValueTypeFromResultSet(ResultSetMetaData paramResultSetMetaData, int paramInt) throws SQLException {
/* 393 */     return convertSQLTypeToValueType(paramResultSetMetaData
/* 394 */         .getColumnType(paramInt), paramResultSetMetaData
/* 395 */         .getColumnTypeName(paramInt));
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
/*     */   public static boolean isBinaryColumn(ResultSetMetaData paramResultSetMetaData, int paramInt) throws SQLException {
/* 411 */     switch (paramResultSetMetaData.getColumnType(paramInt)) {
/*     */       case -2:
/* 413 */         if (paramResultSetMetaData.getColumnTypeName(paramInt).equals("UUID")) {
/*     */           break;
/*     */         }
/*     */       
/*     */       case -4:
/*     */       case -3:
/*     */       case 2000:
/*     */       case 2004:
/* 421 */         return true;
/*     */     } 
/* 423 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int convertSQLTypeToValueType(SQLType paramSQLType) {
/* 433 */     if (paramSQLType instanceof org.h2.api.H2Type)
/* 434 */       return paramSQLType.getVendorTypeNumber().intValue(); 
/* 435 */     if (paramSQLType instanceof java.sql.JDBCType) {
/* 436 */       return convertSQLTypeToValueType(paramSQLType.getVendorTypeNumber().intValue());
/*     */     }
/* 438 */     throw DbException.get(50004, (paramSQLType == null) ? "<null>" : 
/* 439 */         unknownSqlTypeToString(new StringBuilder(), paramSQLType).toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int convertSQLTypeToValueType(int paramInt) {
/* 450 */     switch (paramInt) {
/*     */       case -15:
/*     */       case 1:
/* 453 */         return 1;
/*     */       case -16:
/*     */       case -9:
/*     */       case -1:
/*     */       case 12:
/* 458 */         return 2;
/*     */       case 2:
/*     */       case 3:
/* 461 */         return 13;
/*     */       case -7:
/*     */       case 16:
/* 464 */         return 8;
/*     */       case 4:
/* 466 */         return 11;
/*     */       case 5:
/* 468 */         return 10;
/*     */       case -6:
/* 470 */         return 9;
/*     */       case -5:
/* 472 */         return 12;
/*     */       case 7:
/* 474 */         return 14;
/*     */       case 6:
/*     */       case 8:
/* 477 */         return 15;
/*     */       case -2:
/* 479 */         return 5;
/*     */       case -4:
/*     */       case -3:
/* 482 */         return 6;
/*     */       case 1111:
/* 484 */         return -1;
/*     */       case 2000:
/* 486 */         return 35;
/*     */       case 91:
/* 488 */         return 17;
/*     */       case 92:
/* 490 */         return 18;
/*     */       case 93:
/* 492 */         return 20;
/*     */       case 2013:
/* 494 */         return 19;
/*     */       case 2014:
/* 496 */         return 21;
/*     */       case 2004:
/* 498 */         return 7;
/*     */       case 2005:
/*     */       case 2011:
/* 501 */         return 3;
/*     */       case 0:
/* 503 */         return 0;
/*     */       case 2003:
/* 505 */         return 40;
/*     */     } 
/* 507 */     throw DbException.get(50004, 
/* 508 */         Integer.toString(paramInt));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sqlTypeToString(SQLType paramSQLType) {
/* 519 */     if (paramSQLType == null) {
/* 520 */       return "null";
/*     */     }
/* 522 */     if (paramSQLType instanceof java.sql.JDBCType) {
/* 523 */       return "JDBCType." + paramSQLType.getName();
/*     */     }
/* 525 */     if (paramSQLType instanceof org.h2.api.H2Type) {
/* 526 */       return paramSQLType.toString();
/*     */     }
/* 528 */     return unknownSqlTypeToString(new StringBuilder("/* "), paramSQLType).append(" */ null").toString();
/*     */   }
/*     */   
/*     */   private static StringBuilder unknownSqlTypeToString(StringBuilder paramStringBuilder, SQLType paramSQLType) {
/* 532 */     return paramStringBuilder.append(StringUtils.quoteJavaString(paramSQLType.getVendor())).append('/')
/* 533 */       .append(StringUtils.quoteJavaString(paramSQLType.getName())).append(" [")
/* 534 */       .append(paramSQLType.getVendorTypeNumber()).append(']');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataType getTypeByName(String paramString, Mode paramMode) {
/* 545 */     DataType dataType = (DataType)paramMode.typeByNameMap.get(paramString);
/* 546 */     if (dataType == null) {
/* 547 */       dataType = TYPES_BY_NAME.get(paramString);
/*     */     }
/* 549 */     return dataType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isIndexable(TypeInfo paramTypeInfo) {
/*     */     ExtTypeInfoRow extTypeInfoRow;
/* 559 */     switch (paramTypeInfo.getValueType()) {
/*     */       case -1:
/*     */       case 0:
/*     */       case 3:
/*     */       case 7:
/* 564 */         return false;
/*     */       case 40:
/* 566 */         return isIndexable((TypeInfo)paramTypeInfo.getExtTypeInfo());
/*     */       case 41:
/* 568 */         extTypeInfoRow = (ExtTypeInfoRow)paramTypeInfo.getExtTypeInfo();
/* 569 */         for (Map.Entry<String, TypeInfo> entry : extTypeInfoRow.getFields()) {
/* 570 */           if (!isIndexable((TypeInfo)entry.getValue())) {
/* 571 */             return false;
/*     */           }
/*     */         } 
/*     */         break;
/*     */     } 
/*     */     
/* 577 */     return true;
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
/*     */   public static boolean areStableComparable(TypeInfo paramTypeInfo1, TypeInfo paramTypeInfo2) {
/* 592 */     int i = paramTypeInfo1.getValueType();
/* 593 */     int j = paramTypeInfo2.getValueType();
/* 594 */     switch (i) {
/*     */       case -1:
/*     */       case 0:
/*     */       case 3:
/*     */       case 7:
/*     */       case 41:
/* 600 */         return false;
/*     */       
/*     */       case 17:
/*     */       case 20:
/* 604 */         return (j == 17 || j == 20);
/*     */       
/*     */       case 18:
/*     */       case 19:
/*     */       case 21:
/* 609 */         return (i == j);
/*     */       case 40:
/* 611 */         if (j == 40) {
/* 612 */           return areStableComparable((TypeInfo)paramTypeInfo1.getExtTypeInfo(), (TypeInfo)paramTypeInfo2.getExtTypeInfo());
/*     */         }
/* 614 */         return false;
/*     */     } 
/* 616 */     switch (j) {
/*     */       case -1:
/*     */       case 0:
/*     */       case 3:
/*     */       case 7:
/*     */       case 41:
/* 622 */         return false;
/*     */     } 
/* 624 */     return true;
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
/*     */   public static boolean isDateTimeType(int paramInt) {
/* 637 */     return (paramInt >= 17 && paramInt <= 21);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isIntervalType(int paramInt) {
/* 647 */     return (paramInt >= 22 && paramInt <= 34);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isYearMonthIntervalType(int paramInt) {
/* 657 */     return (paramInt == 22 || paramInt == 23 || paramInt == 28);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isLargeObject(int paramInt) {
/* 667 */     return (paramInt == 7 || paramInt == 3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isNumericType(int paramInt) {
/* 677 */     return (paramInt >= 9 && paramInt <= 16);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBinaryStringType(int paramInt) {
/* 687 */     return (paramInt >= 5 && paramInt <= 7);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isCharacterStringType(int paramInt) {
/* 697 */     return (paramInt >= 1 && paramInt <= 4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isStringType(int paramInt) {
/* 707 */     return (paramInt == 2 || paramInt == 1 || paramInt == 4);
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
/*     */   public static boolean isBinaryStringOrSpecialBinaryType(int paramInt) {
/* 720 */     switch (paramInt) {
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/*     */       case 35:
/*     */       case 37:
/*     */       case 38:
/*     */       case 39:
/* 728 */         return true;
/*     */     } 
/* 730 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasTotalOrdering(int paramInt) {
/* 741 */     switch (paramInt) {
/*     */ 
/*     */ 
/*     */       
/*     */       case 5:
/*     */       case 6:
/*     */       case 8:
/*     */       case 9:
/*     */       case 10:
/*     */       case 11:
/*     */       case 12:
/*     */       case 14:
/*     */       case 15:
/*     */       case 17:
/*     */       case 18:
/*     */       case 20:
/*     */       case 22:
/*     */       case 23:
/*     */       case 24:
/*     */       case 25:
/*     */       case 26:
/*     */       case 27:
/*     */       case 28:
/*     */       case 29:
/*     */       case 30:
/*     */       case 31:
/*     */       case 32:
/*     */       case 33:
/*     */       case 34:
/*     */       case 35:
/*     */       case 36:
/*     */       case 37:
/*     */       case 39:
/* 774 */         return true;
/*     */     } 
/* 776 */     return false;
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
/*     */   public static long addPrecision(long paramLong1, long paramLong2) {
/* 791 */     long l = paramLong1 + paramLong2;
/* 792 */     if ((paramLong1 | paramLong2 | l) < 0L) {
/* 793 */       return Long.MAX_VALUE;
/*     */     }
/* 795 */     return l;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object getDefaultForPrimitiveType(Class<?> paramClass) {
/* 806 */     if (paramClass == boolean.class)
/* 807 */       return Boolean.FALSE; 
/* 808 */     if (paramClass == byte.class)
/* 809 */       return Byte.valueOf((byte)0); 
/* 810 */     if (paramClass == char.class)
/* 811 */       return Character.valueOf(false); 
/* 812 */     if (paramClass == short.class)
/* 813 */       return Short.valueOf((short)0); 
/* 814 */     if (paramClass == int.class)
/* 815 */       return Integer.valueOf(0); 
/* 816 */     if (paramClass == long.class)
/* 817 */       return Long.valueOf(0L); 
/* 818 */     if (paramClass == float.class)
/* 819 */       return Float.valueOf(0.0F); 
/* 820 */     if (paramClass == double.class) {
/* 821 */       return Double.valueOf(0.0D);
/*     */     }
/* 823 */     throw DbException.getInternalError("primitive=" + paramClass.toString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\DataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */