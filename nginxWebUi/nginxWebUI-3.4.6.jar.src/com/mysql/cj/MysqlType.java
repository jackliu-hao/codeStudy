/*      */ package com.mysql.cj;
/*      */ 
/*      */ import com.mysql.cj.exceptions.FeatureNotAvailableException;
/*      */ import com.mysql.cj.util.StringUtils;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.sql.Date;
/*      */ import java.sql.SQLType;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.time.LocalDateTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public enum MysqlType
/*      */   implements SQLType
/*      */ {
/*   61 */   DECIMAL("DECIMAL", 3, BigDecimal.class, 64, true, Long.valueOf(65L), "[(M[,D])] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   67 */   DECIMAL_UNSIGNED("DECIMAL UNSIGNED", 3, BigDecimal.class, 96, true, 
/*   68 */     Long.valueOf(65L), "[(M[,D])] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   75 */   TINYINT("TINYINT", -6, Integer.class, 64, true, Long.valueOf(3L), "[(M)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   81 */   TINYINT_UNSIGNED("TINYINT UNSIGNED", -6, Integer.class, 96, true, Long.valueOf(3L), "[(M)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   92 */   BOOLEAN("BOOLEAN", 16, Boolean.class, 0, false, Long.valueOf(3L), ""),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   99 */   SMALLINT("SMALLINT", 5, Integer.class, 64, true, Long.valueOf(5L), "[(M)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  105 */   SMALLINT_UNSIGNED("SMALLINT UNSIGNED", 5, Integer.class, 96, true, 
/*  106 */     Long.valueOf(5L), "[(M)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  115 */   INT("INT", 4, Integer.class, 64, true, Long.valueOf(10L), "[(M)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  121 */   INT_UNSIGNED("INT UNSIGNED", 4, Long.class, 96, true, Long.valueOf(10L), "[(M)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  142 */   FLOAT("FLOAT", 7, Float.class, 64, true, Long.valueOf(12L), "[(M,D)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  148 */   FLOAT_UNSIGNED("FLOAT UNSIGNED", 7, Float.class, 96, true, Long.valueOf(12L), "[(M,D)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  166 */   DOUBLE("DOUBLE", 8, Double.class, 64, true, Long.valueOf(22L), "[(M,D)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  172 */   DOUBLE_UNSIGNED("DOUBLE UNSIGNED", 8, Double.class, 96, true, Long.valueOf(22L), "[(M,D)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  177 */   NULL("NULL", 0, Object.class, 0, false, Long.valueOf(0L), ""),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  192 */   TIMESTAMP("TIMESTAMP", 93, Timestamp.class, 0, false, Long.valueOf(26L), "[(fsp)]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  201 */   BIGINT("BIGINT", -5, Long.class, 64, true, Long.valueOf(19L), "[(M)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  207 */   BIGINT_UNSIGNED("BIGINT UNSIGNED", -5, BigInteger.class, 96, true, Long.valueOf(20L), "[(M)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  215 */   MEDIUMINT("MEDIUMINT", 4, Integer.class, 64, true, Long.valueOf(7L), "[(M)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  221 */   MEDIUMINT_UNSIGNED("MEDIUMINT UNSIGNED", 4, Integer.class, 96, true, 
/*  222 */     Long.valueOf(8L), "[(M)] [UNSIGNED] [ZEROFILL]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  230 */   DATE("DATE", 91, Date.class, 0, false, Long.valueOf(10L), ""),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  240 */   TIME("TIME", 92, Time.class, 0, false, Long.valueOf(16L), "[(fsp)]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  251 */   DATETIME("DATETIME", 93, LocalDateTime.class, 0, false, Long.valueOf(26L), "[(fsp)]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  258 */   YEAR("YEAR", 91, Date.class, 0, false, Long.valueOf(4L), "[(4)]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  280 */   VARCHAR("VARCHAR", 12, String.class, 0, false, Long.valueOf(65535L), "(M) [CHARACTER SET charset_name] [COLLATE collation_name]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  289 */   VARBINARY("VARBINARY", -3, null, 0, false, Long.valueOf(65535L), "(M)"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  295 */   BIT("BIT", -7, Boolean.class, 0, true, Long.valueOf(1L), "[(M)]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  302 */   JSON("JSON", -1, String.class, 0, false, Long.valueOf(1073741824L), ""),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  312 */   ENUM("ENUM", 1, String.class, 0, false, Long.valueOf(65535L), "('value1','value2',...) [CHARACTER SET charset_name] [COLLATE collation_name]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  323 */   SET("SET", 1, String.class, 0, false, Long.valueOf(64L), "('value1','value2',...) [CHARACTER SET charset_name] [COLLATE collation_name]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  331 */   TINYBLOB("TINYBLOB", -3, null, 0, false, Long.valueOf(255L), ""),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  340 */   TINYTEXT("TINYTEXT", 12, String.class, 0, false, Long.valueOf(255L), " [CHARACTER SET charset_name] [COLLATE collation_name]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  348 */   MEDIUMBLOB("MEDIUMBLOB", -4, null, 0, false, Long.valueOf(16777215L), ""),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  357 */   MEDIUMTEXT("MEDIUMTEXT", -1, String.class, 0, false, Long.valueOf(16777215L), " [CHARACTER SET charset_name] [COLLATE collation_name]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  366 */   LONGBLOB("LONGBLOB", -4, null, 0, false, Long.valueOf(4294967295L), ""),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  377 */   LONGTEXT("LONGTEXT", -1, String.class, 0, false, Long.valueOf(4294967295L), " [CHARACTER SET charset_name] [COLLATE collation_name]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  387 */   BLOB("BLOB", -4, null, 0, false, Long.valueOf(65535L), "[(M)]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  398 */   TEXT("TEXT", -1, String.class, 0, false, Long.valueOf(65535L), "[(M)] [CHARACTER SET charset_name] [COLLATE collation_name]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  416 */   CHAR("CHAR", 1, String.class, 0, false, Long.valueOf(255L), "[(M)] [CHARACTER SET charset_name] [COLLATE collation_name]"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  426 */   BINARY("BINARY", -2, null, 0, false, Long.valueOf(255L), "(M)"),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  432 */   GEOMETRY("GEOMETRY", -2, null, 0, false, Long.valueOf(65535L), ""),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  439 */   UNKNOWN("UNKNOWN", 1111, null, 0, false, Long.valueOf(65535L), ""); private final String name; protected int jdbcType; protected final Class<?> javaClass; private final int flagsMask; private final boolean isDecimal; private final Long precision; private final String createParams; public static final int FIELD_FLAG_NOT_NULL = 1; public static final int FIELD_FLAG_PRIMARY_KEY = 2; public static final int FIELD_FLAG_UNIQUE_KEY = 4; public static final int FIELD_FLAG_MULTIPLE_KEY = 8;
/*      */   public static final int FIELD_FLAG_BLOB = 16;
/*      */   public static final int FIELD_FLAG_UNSIGNED = 32;
/*      */   public static final int FIELD_FLAG_ZEROFILL = 64;
/*      */   public static final int FIELD_FLAG_BINARY = 128;
/*      */   public static final int FIELD_FLAG_AUTO_INCREMENT = 512;
/*      */   private static final boolean IS_DECIMAL = true;
/*      */   private static final boolean IS_NOT_DECIMAL = false;
/*      */   public static final int FIELD_TYPE_DECIMAL = 0;
/*      */   public static final int FIELD_TYPE_TINY = 1;
/*      */   public static final int FIELD_TYPE_SHORT = 2;
/*      */   public static final int FIELD_TYPE_LONG = 3;
/*      */   
/*      */   public static MysqlType getByName(String fullMysqlTypeName) {
/*  453 */     String typeName = "";
/*      */     
/*  455 */     if (fullMysqlTypeName.indexOf("(") != -1) {
/*  456 */       typeName = fullMysqlTypeName.substring(0, fullMysqlTypeName.indexOf("(")).trim();
/*      */     } else {
/*  458 */       typeName = fullMysqlTypeName;
/*      */     } 
/*      */ 
/*      */     
/*  462 */     if (StringUtils.indexOfIgnoreCase(typeName, "DECIMAL") != -1 || StringUtils.indexOfIgnoreCase(typeName, "DEC") != -1 || 
/*  463 */       StringUtils.indexOfIgnoreCase(typeName, "NUMERIC") != -1 || StringUtils.indexOfIgnoreCase(typeName, "FIXED") != -1) {
/*  464 */       return (StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "UNSIGNED") != -1) ? DECIMAL_UNSIGNED : DECIMAL;
/*      */     }
/*  466 */     if (StringUtils.indexOfIgnoreCase(typeName, "TINYBLOB") != -1)
/*      */     {
/*  468 */       return TINYBLOB;
/*      */     }
/*  470 */     if (StringUtils.indexOfIgnoreCase(typeName, "TINYTEXT") != -1)
/*      */     {
/*  472 */       return TINYTEXT;
/*      */     }
/*  474 */     if (StringUtils.indexOfIgnoreCase(typeName, "TINYINT") != -1 || StringUtils.indexOfIgnoreCase(typeName, "TINY") != -1 || 
/*  475 */       StringUtils.indexOfIgnoreCase(typeName, "INT1") != -1) {
/*  476 */       return (StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "UNSIGNED") != -1 || StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "ZEROFILL") != -1) ? TINYINT_UNSIGNED : TINYINT;
/*      */     }
/*      */ 
/*      */     
/*  480 */     if (StringUtils.indexOfIgnoreCase(typeName, "MEDIUMINT") != -1 || 
/*      */       
/*  482 */       StringUtils.indexOfIgnoreCase(typeName, "INT24") != -1 || StringUtils.indexOfIgnoreCase(typeName, "INT3") != -1 || 
/*  483 */       StringUtils.indexOfIgnoreCase(typeName, "MIDDLEINT") != -1) {
/*  484 */       return (StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "UNSIGNED") != -1 || StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "ZEROFILL") != -1) ? MEDIUMINT_UNSIGNED : MEDIUMINT;
/*      */     }
/*      */ 
/*      */     
/*  488 */     if (StringUtils.indexOfIgnoreCase(typeName, "SMALLINT") != -1 || StringUtils.indexOfIgnoreCase(typeName, "INT2") != -1) {
/*  489 */       return (StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "UNSIGNED") != -1 || StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "ZEROFILL") != -1) ? SMALLINT_UNSIGNED : SMALLINT;
/*      */     }
/*      */ 
/*      */     
/*  493 */     if (StringUtils.indexOfIgnoreCase(typeName, "BIGINT") != -1 || StringUtils.indexOfIgnoreCase(typeName, "SERIAL") != -1 || 
/*  494 */       StringUtils.indexOfIgnoreCase(typeName, "INT8") != -1)
/*      */     {
/*  496 */       return (StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "UNSIGNED") != -1 || StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "ZEROFILL") != -1) ? BIGINT_UNSIGNED : BIGINT;
/*      */     }
/*      */ 
/*      */     
/*  500 */     if (StringUtils.indexOfIgnoreCase(typeName, "POINT") != -1)
/*      */     {
/*      */       
/*  503 */       return GEOMETRY;
/*      */     }
/*  505 */     if (StringUtils.indexOfIgnoreCase(typeName, "INT") != -1 || StringUtils.indexOfIgnoreCase(typeName, "INTEGER") != -1 || 
/*  506 */       StringUtils.indexOfIgnoreCase(typeName, "INT4") != -1)
/*      */     {
/*  508 */       return (StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "UNSIGNED") != -1 || StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "ZEROFILL") != -1) ? INT_UNSIGNED : INT;
/*      */     }
/*      */ 
/*      */     
/*  512 */     if (StringUtils.indexOfIgnoreCase(typeName, "DOUBLE") != -1 || StringUtils.indexOfIgnoreCase(typeName, "REAL") != -1 || 
/*      */ 
/*      */       
/*  515 */       StringUtils.indexOfIgnoreCase(typeName, "FLOAT8") != -1)
/*      */     {
/*  517 */       return (StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "UNSIGNED") != -1 || StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "ZEROFILL") != -1) ? DOUBLE_UNSIGNED : DOUBLE;
/*      */     }
/*      */ 
/*      */     
/*  521 */     if (StringUtils.indexOfIgnoreCase(typeName, "FLOAT") != -1)
/*      */     {
/*      */ 
/*      */ 
/*      */       
/*  526 */       return (StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "UNSIGNED") != -1 || StringUtils.indexOfIgnoreCase(fullMysqlTypeName, "ZEROFILL") != -1) ? FLOAT_UNSIGNED : FLOAT;
/*      */     }
/*      */ 
/*      */     
/*  530 */     if (StringUtils.indexOfIgnoreCase(typeName, "NULL") != -1) {
/*  531 */       return NULL;
/*      */     }
/*  533 */     if (StringUtils.indexOfIgnoreCase(typeName, "TIMESTAMP") != -1)
/*      */     {
/*  535 */       return TIMESTAMP;
/*      */     }
/*  537 */     if (StringUtils.indexOfIgnoreCase(typeName, "DATETIME") != -1)
/*      */     {
/*  539 */       return DATETIME;
/*      */     }
/*  541 */     if (StringUtils.indexOfIgnoreCase(typeName, "DATE") != -1) {
/*  542 */       return DATE;
/*      */     }
/*  544 */     if (StringUtils.indexOfIgnoreCase(typeName, "TIME") != -1) {
/*  545 */       return TIME;
/*      */     }
/*  547 */     if (StringUtils.indexOfIgnoreCase(typeName, "YEAR") != -1) {
/*  548 */       return YEAR;
/*      */     }
/*  550 */     if (StringUtils.indexOfIgnoreCase(typeName, "LONGBLOB") != -1)
/*      */     {
/*  552 */       return LONGBLOB;
/*      */     }
/*  554 */     if (StringUtils.indexOfIgnoreCase(typeName, "LONGTEXT") != -1)
/*      */     {
/*  556 */       return LONGTEXT;
/*      */     }
/*  558 */     if (StringUtils.indexOfIgnoreCase(typeName, "MEDIUMBLOB") != -1 || StringUtils.indexOfIgnoreCase(typeName, "LONG VARBINARY") != -1)
/*      */     {
/*      */       
/*  561 */       return MEDIUMBLOB;
/*      */     }
/*  563 */     if (StringUtils.indexOfIgnoreCase(typeName, "MEDIUMTEXT") != -1 || StringUtils.indexOfIgnoreCase(typeName, "LONG VARCHAR") != -1 || 
/*  564 */       StringUtils.indexOfIgnoreCase(typeName, "LONG") != -1)
/*      */     {
/*      */       
/*  567 */       return MEDIUMTEXT;
/*      */     }
/*  569 */     if (StringUtils.indexOfIgnoreCase(typeName, "VARCHAR") != -1 || StringUtils.indexOfIgnoreCase(typeName, "NVARCHAR") != -1 || 
/*  570 */       StringUtils.indexOfIgnoreCase(typeName, "NATIONAL VARCHAR") != -1 || StringUtils.indexOfIgnoreCase(typeName, "CHARACTER VARYING") != -1)
/*      */     {
/*  572 */       return VARCHAR;
/*      */     }
/*  574 */     if (StringUtils.indexOfIgnoreCase(typeName, "VARBINARY") != -1) {
/*  575 */       return VARBINARY;
/*      */     }
/*  577 */     if (StringUtils.indexOfIgnoreCase(typeName, "BINARY") != -1 || StringUtils.indexOfIgnoreCase(typeName, "CHAR BYTE") != -1)
/*      */     {
/*      */       
/*  580 */       return BINARY;
/*      */     }
/*  582 */     if (StringUtils.indexOfIgnoreCase(typeName, "LINESTRING") != -1)
/*      */     {
/*      */       
/*  585 */       return GEOMETRY;
/*      */     }
/*  587 */     if (StringUtils.indexOfIgnoreCase(typeName, "STRING") != -1 || 
/*      */       
/*  589 */       StringUtils.indexOfIgnoreCase(typeName, "CHAR") != -1 || StringUtils.indexOfIgnoreCase(typeName, "NCHAR") != -1 || 
/*  590 */       StringUtils.indexOfIgnoreCase(typeName, "NATIONAL CHAR") != -1 || StringUtils.indexOfIgnoreCase(typeName, "CHARACTER") != -1) {
/*  591 */       return CHAR;
/*      */     }
/*  593 */     if (StringUtils.indexOfIgnoreCase(typeName, "BOOLEAN") != -1 || StringUtils.indexOfIgnoreCase(typeName, "BOOL") != -1) {
/*  594 */       return BOOLEAN;
/*      */     }
/*  596 */     if (StringUtils.indexOfIgnoreCase(typeName, "BIT") != -1) {
/*  597 */       return BIT;
/*      */     }
/*  599 */     if (StringUtils.indexOfIgnoreCase(typeName, "JSON") != -1) {
/*  600 */       return JSON;
/*      */     }
/*  602 */     if (StringUtils.indexOfIgnoreCase(typeName, "ENUM") != -1) {
/*  603 */       return ENUM;
/*      */     }
/*  605 */     if (StringUtils.indexOfIgnoreCase(typeName, "SET") != -1) {
/*  606 */       return SET;
/*      */     }
/*  608 */     if (StringUtils.indexOfIgnoreCase(typeName, "BLOB") != -1) {
/*  609 */       return BLOB;
/*      */     }
/*  611 */     if (StringUtils.indexOfIgnoreCase(typeName, "TEXT") != -1) {
/*  612 */       return TEXT;
/*      */     }
/*  614 */     if (StringUtils.indexOfIgnoreCase(typeName, "GEOM") != -1 || 
/*  615 */       StringUtils.indexOfIgnoreCase(typeName, "POINT") != -1 || 
/*  616 */       StringUtils.indexOfIgnoreCase(typeName, "POLYGON") != -1)
/*      */     {
/*  618 */       return GEOMETRY;
/*      */     }
/*      */ 
/*      */     
/*  622 */     return UNKNOWN;
/*      */   }
/*      */   public static final int FIELD_TYPE_FLOAT = 4; public static final int FIELD_TYPE_DOUBLE = 5; public static final int FIELD_TYPE_NULL = 6; public static final int FIELD_TYPE_TIMESTAMP = 7; public static final int FIELD_TYPE_LONGLONG = 8; public static final int FIELD_TYPE_INT24 = 9; public static final int FIELD_TYPE_DATE = 10; public static final int FIELD_TYPE_TIME = 11; public static final int FIELD_TYPE_DATETIME = 12; public static final int FIELD_TYPE_YEAR = 13; public static final int FIELD_TYPE_VARCHAR = 15; public static final int FIELD_TYPE_BIT = 16; public static final int FIELD_TYPE_JSON = 245; public static final int FIELD_TYPE_NEWDECIMAL = 246; public static final int FIELD_TYPE_ENUM = 247; public static final int FIELD_TYPE_SET = 248; public static final int FIELD_TYPE_TINY_BLOB = 249; public static final int FIELD_TYPE_MEDIUM_BLOB = 250; public static final int FIELD_TYPE_LONG_BLOB = 251; public static final int FIELD_TYPE_BLOB = 252; public static final int FIELD_TYPE_VAR_STRING = 253; public static final int FIELD_TYPE_STRING = 254; public static final int FIELD_TYPE_GEOMETRY = 255;
/*      */   public static MysqlType getByJdbcType(int jdbcType) {
/*  626 */     switch (jdbcType) {
/*      */       case -5:
/*  628 */         return BIGINT;
/*      */       case -2:
/*  630 */         return BINARY;
/*      */       case -7:
/*  632 */         return BIT;
/*      */       case 16:
/*  634 */         return BOOLEAN;
/*      */       case -15:
/*      */       case 1:
/*  637 */         return CHAR;
/*      */       case 91:
/*  639 */         return DATE;
/*      */       case 2:
/*      */       case 3:
/*  642 */         return DECIMAL;
/*      */       case 6:
/*      */       case 8:
/*  645 */         return DOUBLE;
/*      */       case 4:
/*  647 */         return INT;
/*      */       case -4:
/*      */       case 2000:
/*      */       case 2004:
/*  651 */         return BLOB;
/*      */       case -16:
/*      */       case -1:
/*      */       case 2005:
/*      */       case 2011:
/*  656 */         return TEXT;
/*      */       case 0:
/*  658 */         return NULL;
/*      */       case 7:
/*  660 */         return FLOAT;
/*      */       case 5:
/*  662 */         return SMALLINT;
/*      */       case 92:
/*  664 */         return TIME;
/*      */       case 93:
/*  666 */         return TIMESTAMP;
/*      */       case -6:
/*  668 */         return TINYINT;
/*      */       case -3:
/*  670 */         return VARBINARY;
/*      */       case -9:
/*      */       case 12:
/*      */       case 70:
/*      */       case 2009:
/*  675 */         return VARCHAR;
/*      */       
/*      */       case 2012:
/*  678 */         throw new FeatureNotAvailableException("REF_CURSOR type is not supported");
/*      */       case 2013:
/*  680 */         throw new FeatureNotAvailableException("TIME_WITH_TIMEZONE type is not supported");
/*      */       case 2014:
/*  682 */         throw new FeatureNotAvailableException("TIMESTAMP_WITH_TIMEZONE type is not supported");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  693 */     return UNKNOWN;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean supportsConvert(int fromType, int toType) {
/*  711 */     switch (fromType) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/*  722 */         switch (toType) {
/*      */           case -6:
/*      */           case -5:
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case -1:
/*      */           case 1:
/*      */           case 2:
/*      */           case 3:
/*      */           case 4:
/*      */           case 5:
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/*      */           case 12:
/*      */           case 91:
/*      */           case 92:
/*      */           case 93:
/*      */           case 1111:
/*  742 */             return true;
/*      */         } 
/*      */         
/*  745 */         return false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case -7:
/*  752 */         return false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case -6:
/*      */       case -5:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  767 */         switch (toType) {
/*      */           case -6:
/*      */           case -5:
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case -1:
/*      */           case 1:
/*      */           case 2:
/*      */           case 3:
/*      */           case 4:
/*      */           case 5:
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/*      */           case 12:
/*  783 */             return true;
/*      */         } 
/*      */         
/*  786 */         return false;
/*      */ 
/*      */ 
/*      */       
/*      */       case 0:
/*  791 */         return false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 1111:
/*  798 */         switch (toType) {
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case -1:
/*      */           case 1:
/*      */           case 12:
/*  805 */             return true;
/*      */         } 
/*      */         
/*  808 */         return false;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 91:
/*  814 */         switch (toType) {
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case -1:
/*      */           case 1:
/*      */           case 12:
/*  821 */             return true;
/*      */         } 
/*      */         
/*  824 */         return false;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 92:
/*  830 */         switch (toType) {
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case -1:
/*      */           case 1:
/*      */           case 12:
/*  837 */             return true;
/*      */         } 
/*      */         
/*  840 */         return false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 93:
/*  848 */         switch (toType) {
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case -1:
/*      */           case 1:
/*      */           case 12:
/*      */           case 91:
/*      */           case 92:
/*  857 */             return true;
/*      */         } 
/*      */         
/*  860 */         return false;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  865 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean isSigned(MysqlType type) {
/*  870 */     switch (type) {
/*      */       case DECIMAL:
/*      */       case TINYINT:
/*      */       case SMALLINT:
/*      */       case INT:
/*      */       case BIGINT:
/*      */       case MEDIUMINT:
/*      */       case FLOAT:
/*      */       case DOUBLE:
/*  879 */         return true;
/*      */     } 
/*  881 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   MysqlType(String mysqlTypeName, int jdbcType, Class<?> javaClass, int allowedFlags, boolean isDec, Long precision, String createParams) {
/*  921 */     this.name = mysqlTypeName;
/*  922 */     this.jdbcType = jdbcType;
/*  923 */     this.javaClass = javaClass;
/*  924 */     this.flagsMask = allowedFlags;
/*  925 */     this.isDecimal = isDec;
/*  926 */     this.precision = precision;
/*  927 */     this.createParams = createParams;
/*      */   }
/*      */   
/*      */   public String getName() {
/*  931 */     return this.name;
/*      */   }
/*      */   
/*      */   public int getJdbcType() {
/*  935 */     return this.jdbcType;
/*      */   }
/*      */   
/*      */   public boolean isAllowed(int flag) {
/*  939 */     return ((this.flagsMask & flag) > 0);
/*      */   }
/*      */   
/*      */   public String getClassName() {
/*  943 */     if (this.javaClass == null) {
/*  944 */       return "[B";
/*      */     }
/*  946 */     return this.javaClass.getName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isDecimal() {
/*  955 */     return this.isDecimal;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Long getPrecision() {
/*  975 */     return this.precision;
/*      */   }
/*      */   
/*      */   public String getCreateParams() {
/*  979 */     return this.createParams;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getVendor() {
/*  997 */     return "com.mysql.cj";
/*      */   }
/*      */ 
/*      */   
/*      */   public Integer getVendorTypeNumber() {
/* 1002 */     return Integer.valueOf(this.jdbcType);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\MysqlType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */