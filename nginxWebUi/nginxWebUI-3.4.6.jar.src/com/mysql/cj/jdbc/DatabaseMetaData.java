/*      */ package com.mysql.cj.jdbc;
/*      */ 
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.MysqlType;
/*      */ import com.mysql.cj.NativeSession;
/*      */ import com.mysql.cj.conf.PropertyDefinitions;
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.conf.RuntimeProperty;
/*      */ import com.mysql.cj.exceptions.AssertionFailedException;
/*      */ import com.mysql.cj.exceptions.CJException;
/*      */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*      */ import com.mysql.cj.jdbc.result.ResultSetFactory;
/*      */ import com.mysql.cj.jdbc.result.ResultSetImpl;
/*      */ import com.mysql.cj.protocol.ColumnDefinition;
/*      */ import com.mysql.cj.protocol.ResultsetRows;
/*      */ import com.mysql.cj.protocol.a.result.ByteArrayRow;
/*      */ import com.mysql.cj.protocol.a.result.ResultsetRowsStatic;
/*      */ import com.mysql.cj.result.DefaultColumnDefinition;
/*      */ import com.mysql.cj.result.Field;
/*      */ import com.mysql.cj.result.Row;
/*      */ import com.mysql.cj.util.SearchMode;
/*      */ import com.mysql.cj.util.StringUtils;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.RowIdLifetime;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.SortedMap;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TreeMap;
/*      */ import java.util.TreeSet;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DatabaseMetaData
/*      */   implements DatabaseMetaData
/*      */ {
/*   97 */   protected static int maxBufferSize = 65535;
/*      */   protected static final int MAX_IDENTIFIER_LENGTH = 64;
/*      */   private static final String SUPPORTS_FK = "SUPPORTS_FK";
/*      */   
/*      */   protected abstract class IteratorWithCleanup<T>
/*      */   {
/*      */     abstract void close() throws SQLException;
/*      */     
/*      */     abstract boolean hasNext() throws SQLException;
/*      */     
/*      */     abstract T next() throws SQLException;
/*      */   }
/*      */   
/*      */   class LocalAndReferencedColumns
/*      */   {
/*      */     String constraintName;
/*      */     List<String> localColumnsList;
/*      */     String referencedDatabase;
/*      */     List<String> referencedColumnsList;
/*      */     String referencedTable;
/*      */     
/*      */     LocalAndReferencedColumns(List<String> localColumns, List<String> refColumns, String constName, String refDatabase, String refTable) {
/*  119 */       this.localColumnsList = localColumns;
/*  120 */       this.referencedColumnsList = refColumns;
/*  121 */       this.constraintName = constName;
/*  122 */       this.referencedTable = refTable;
/*  123 */       this.referencedDatabase = refDatabase;
/*      */     }
/*      */   }
/*      */   
/*      */   protected class StringListIterator extends IteratorWithCleanup<String> {
/*  128 */     int idx = -1;
/*      */     
/*      */     List<String> list;
/*      */     
/*      */     StringListIterator(List<String> list) {
/*  133 */       this.list = list;
/*      */     }
/*      */ 
/*      */     
/*      */     void close() throws SQLException {
/*  138 */       this.list = null;
/*      */     }
/*      */ 
/*      */     
/*      */     boolean hasNext() throws SQLException {
/*  143 */       return (this.idx < this.list.size() - 1);
/*      */     }
/*      */ 
/*      */     
/*      */     String next() throws SQLException {
/*  148 */       this.idx++;
/*  149 */       return this.list.get(this.idx);
/*      */     }
/*      */   }
/*      */   
/*      */   protected class SingleStringIterator
/*      */     extends IteratorWithCleanup<String> {
/*      */     boolean onFirst = true;
/*      */     String value;
/*      */     
/*      */     SingleStringIterator(String s) {
/*  159 */       this.value = s;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void close() throws SQLException {}
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasNext() throws SQLException {
/*  169 */       return this.onFirst;
/*      */     }
/*      */ 
/*      */     
/*      */     String next() throws SQLException {
/*  174 */       this.onFirst = false;
/*  175 */       return this.value;
/*      */     } }
/*      */   class TypeDescriptor { int bufferLength; Integer datetimePrecision;
/*      */     Integer columnSize;
/*      */     Integer charOctetLength;
/*      */     Integer decimalDigits;
/*      */     
/*      */     TypeDescriptor(String typeInfo, String nullabilityInfo) throws SQLException {
/*      */       String temp;
/*      */       StringTokenizer tokenizer;
/*      */       int fract, numElements;
/*  186 */       this.datetimePrecision = null;
/*  187 */       this.columnSize = null;
/*  188 */       this.charOctetLength = null;
/*      */       
/*  190 */       this.decimalDigits = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  196 */       this.numPrecRadix = 10;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  202 */       if (typeInfo == null) {
/*  203 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.0"), "S1009", DatabaseMetaData.this
/*  204 */             .getExceptionInterceptor());
/*      */       }
/*      */       
/*  207 */       this.mysqlType = MysqlType.getByName(typeInfo);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  213 */       int maxLength = 0;
/*      */ 
/*      */       
/*  216 */       switch (this.mysqlType) {
/*      */         case LOCAL_TEMPORARY:
/*  218 */           temp = typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.lastIndexOf(")"));
/*  219 */           tokenizer = new StringTokenizer(temp, ",");
/*  220 */           while (tokenizer.hasMoreTokens()) {
/*  221 */             String nextToken = tokenizer.nextToken();
/*  222 */             maxLength = Math.max(maxLength, nextToken.length() - 2);
/*      */           } 
/*  224 */           this.columnSize = Integer.valueOf(maxLength);
/*      */           break;
/*      */         
/*      */         case null:
/*  228 */           temp = typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.lastIndexOf(")"));
/*  229 */           tokenizer = new StringTokenizer(temp, ",");
/*      */           
/*  231 */           numElements = tokenizer.countTokens();
/*  232 */           if (numElements > 0) {
/*  233 */             maxLength += numElements - 1;
/*      */           }
/*      */           
/*  236 */           while (tokenizer.hasMoreTokens()) {
/*  237 */             String setMember = tokenizer.nextToken().trim();
/*      */             
/*  239 */             if (setMember.startsWith("'") && setMember.endsWith("'")) {
/*  240 */               maxLength += setMember.length() - 2; continue;
/*      */             } 
/*  242 */             maxLength += setMember.length();
/*      */           } 
/*      */           
/*  245 */           this.columnSize = Integer.valueOf(maxLength);
/*      */           break;
/*      */         
/*      */         case null:
/*      */         case null:
/*  250 */           if (typeInfo.indexOf(",") != -1) {
/*      */             
/*  252 */             this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(",")).trim());
/*  253 */             this.decimalDigits = Integer.valueOf(typeInfo.substring(typeInfo.indexOf(",") + 1, typeInfo.indexOf(")")).trim()); break;
/*  254 */           }  if (typeInfo.indexOf("(") != -1) {
/*  255 */             int size = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(")")).trim()).intValue();
/*  256 */             if (size > 23) {
/*  257 */               this.mysqlType = (this.mysqlType == MysqlType.FLOAT) ? MysqlType.DOUBLE : MysqlType.DOUBLE_UNSIGNED;
/*  258 */               this.columnSize = Integer.valueOf(22);
/*  259 */               this.decimalDigits = Integer.valueOf(0);
/*      */             }  break;
/*      */           } 
/*  262 */           this.columnSize = Integer.valueOf(12);
/*  263 */           this.decimalDigits = Integer.valueOf(0);
/*      */           break;
/*      */         
/*      */         case TABLE:
/*      */         case VIEW:
/*      */         case SYSTEM_TABLE:
/*      */         case SYSTEM_VIEW:
/*  270 */           if (typeInfo.indexOf(",") != -1) {
/*      */             
/*  272 */             this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(",")).trim());
/*  273 */             this.decimalDigits = Integer.valueOf(typeInfo.substring(typeInfo.indexOf(",") + 1, typeInfo.indexOf(")")).trim()); break;
/*      */           } 
/*  275 */           switch (this.mysqlType) {
/*      */             case TABLE:
/*      */             case VIEW:
/*  278 */               this.columnSize = Integer.valueOf(65);
/*      */               break;
/*      */             case SYSTEM_TABLE:
/*      */             case SYSTEM_VIEW:
/*  282 */               this.columnSize = Integer.valueOf(22);
/*      */               break;
/*      */           } 
/*      */ 
/*      */           
/*  287 */           this.decimalDigits = Integer.valueOf(0);
/*      */           break;
/*      */ 
/*      */         
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*  305 */           if (this.mysqlType == MysqlType.CHAR) {
/*  306 */             this.columnSize = Integer.valueOf(1);
/*      */           }
/*  308 */           if (typeInfo.indexOf("(") != -1) {
/*  309 */             int endParenIndex = typeInfo.indexOf(")");
/*      */             
/*  311 */             if (endParenIndex == -1) {
/*  312 */               endParenIndex = typeInfo.length();
/*      */             }
/*      */             
/*  315 */             this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, endParenIndex).trim());
/*      */ 
/*      */             
/*  318 */             if (DatabaseMetaData.this.tinyInt1isBit && this.columnSize.intValue() == 1 && StringUtils.startsWithIgnoreCase(typeInfo, "tinyint")) {
/*  319 */               if (DatabaseMetaData.this.transformedBitIsBoolean) {
/*  320 */                 this.mysqlType = MysqlType.BOOLEAN; break;
/*      */               } 
/*  322 */               this.mysqlType = MysqlType.BIT;
/*      */             } 
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case null:
/*  330 */           if (DatabaseMetaData.this.tinyInt1isBit && typeInfo.indexOf("(1)") != -1) {
/*  331 */             if (DatabaseMetaData.this.transformedBitIsBoolean) {
/*  332 */               this.mysqlType = MysqlType.BOOLEAN; break;
/*      */             } 
/*  334 */             this.mysqlType = MysqlType.BIT;
/*      */             break;
/*      */           } 
/*  337 */           this.columnSize = Integer.valueOf(3);
/*      */           break;
/*      */ 
/*      */         
/*      */         case null:
/*  342 */           this.columnSize = Integer.valueOf(3);
/*      */           break;
/*      */         
/*      */         case null:
/*  346 */           this.datetimePrecision = Integer.valueOf(0);
/*  347 */           this.columnSize = Integer.valueOf(10);
/*      */           break;
/*      */         
/*      */         case null:
/*  351 */           this.datetimePrecision = Integer.valueOf(0);
/*  352 */           this.columnSize = Integer.valueOf(8);
/*  353 */           if (typeInfo.indexOf("(") != -1 && (
/*  354 */             fract = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(")")).trim()).intValue()) > 0) {
/*      */             
/*  356 */             this.datetimePrecision = Integer.valueOf(fract);
/*  357 */             TypeDescriptor typeDescriptor = this; typeDescriptor.columnSize = Integer.valueOf(typeDescriptor.columnSize.intValue() + fract + 1);
/*      */           } 
/*      */           break;
/*      */         
/*      */         case null:
/*      */         case null:
/*  363 */           this.datetimePrecision = Integer.valueOf(0);
/*  364 */           this.columnSize = Integer.valueOf(19);
/*  365 */           if (typeInfo.indexOf("(") != -1 && (
/*  366 */             fract = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(")")).trim()).intValue()) > 0) {
/*      */             
/*  368 */             this.datetimePrecision = Integer.valueOf(fract);
/*  369 */             TypeDescriptor typeDescriptor = this; typeDescriptor.columnSize = Integer.valueOf(typeDescriptor.columnSize.intValue() + fract + 1);
/*      */           } 
/*      */           break;
/*      */       } 
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
/*  383 */       if (this.columnSize == null)
/*      */       {
/*  385 */         this.columnSize = Integer.valueOf((this.mysqlType.getPrecision().longValue() > 2147483647L) ? Integer.MAX_VALUE : this.mysqlType.getPrecision().intValue());
/*      */       }
/*      */       
/*  388 */       switch (this.mysqlType) {
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*      */         case null:
/*  403 */           this.charOctetLength = this.columnSize;
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  410 */       this.bufferLength = DatabaseMetaData.maxBufferSize;
/*      */ 
/*      */       
/*  413 */       this.numPrecRadix = 10;
/*      */ 
/*      */       
/*  416 */       if (nullabilityInfo != null) {
/*  417 */         if (nullabilityInfo.equals("YES")) {
/*  418 */           this.nullability = 1;
/*  419 */           this.isNullable = "YES";
/*      */         }
/*  421 */         else if (nullabilityInfo.equals("UNKNOWN")) {
/*  422 */           this.nullability = 2;
/*  423 */           this.isNullable = "";
/*      */         }
/*      */         else {
/*      */           
/*  427 */           this.nullability = 0;
/*  428 */           this.isNullable = "NO";
/*      */         } 
/*      */       } else {
/*  431 */         this.nullability = 0;
/*  432 */         this.isNullable = "NO";
/*      */       } 
/*      */     }
/*      */     String isNullable;
/*      */     int nullability;
/*      */     int numPrecRadix;
/*      */     String mysqlTypeName;
/*      */     MysqlType mysqlType; }
/*      */   
/*      */   protected class IndexMetaDataKey implements Comparable<IndexMetaDataKey> { Boolean columnNonUnique;
/*      */     Short columnType;
/*      */     String columnIndexName;
/*      */     Short columnOrdinalPosition;
/*      */     
/*      */     IndexMetaDataKey(boolean columnNonUnique, short columnType, String columnIndexName, short columnOrdinalPosition) {
/*  447 */       this.columnNonUnique = Boolean.valueOf(columnNonUnique);
/*  448 */       this.columnType = Short.valueOf(columnType);
/*  449 */       this.columnIndexName = columnIndexName;
/*  450 */       this.columnOrdinalPosition = Short.valueOf(columnOrdinalPosition);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int compareTo(IndexMetaDataKey indexInfoKey) {
/*      */       int compareResult;
/*  457 */       if ((compareResult = this.columnNonUnique.compareTo(indexInfoKey.columnNonUnique)) != 0) {
/*  458 */         return compareResult;
/*      */       }
/*  460 */       if ((compareResult = this.columnType.compareTo(indexInfoKey.columnType)) != 0) {
/*  461 */         return compareResult;
/*      */       }
/*  463 */       if ((compareResult = this.columnIndexName.compareTo(indexInfoKey.columnIndexName)) != 0) {
/*  464 */         return compareResult;
/*      */       }
/*  466 */       return this.columnOrdinalPosition.compareTo(indexInfoKey.columnOrdinalPosition);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  471 */       if (obj == null) {
/*  472 */         return false;
/*      */       }
/*      */       
/*  475 */       if (obj == this) {
/*  476 */         return true;
/*      */       }
/*      */       
/*  479 */       if (!(obj instanceof IndexMetaDataKey)) {
/*  480 */         return false;
/*      */       }
/*  482 */       return (compareTo((IndexMetaDataKey)obj) == 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  487 */       assert false : "hashCode not designed";
/*  488 */       return 0;
/*      */     } }
/*      */ 
/*      */ 
/*      */   
/*      */   protected class TableMetaDataKey
/*      */     implements Comparable<TableMetaDataKey>
/*      */   {
/*      */     String tableType;
/*      */     String tableCat;
/*      */     String tableSchem;
/*      */     String tableName;
/*      */     
/*      */     TableMetaDataKey(String tableType, String tableCat, String tableSchem, String tableName) {
/*  502 */       this.tableType = (tableType == null) ? "" : tableType;
/*  503 */       this.tableCat = (tableCat == null) ? "" : tableCat;
/*  504 */       this.tableSchem = (tableSchem == null) ? "" : tableSchem;
/*  505 */       this.tableName = (tableName == null) ? "" : tableName;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int compareTo(TableMetaDataKey tablesKey) {
/*      */       int compareResult;
/*  512 */       if ((compareResult = this.tableType.compareTo(tablesKey.tableType)) != 0) {
/*  513 */         return compareResult;
/*      */       }
/*  515 */       if ((compareResult = this.tableCat.compareTo(tablesKey.tableCat)) != 0) {
/*  516 */         return compareResult;
/*      */       }
/*  518 */       if ((compareResult = this.tableSchem.compareTo(tablesKey.tableSchem)) != 0) {
/*  519 */         return compareResult;
/*      */       }
/*  521 */       return this.tableName.compareTo(tablesKey.tableName);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  526 */       if (obj == null) {
/*  527 */         return false;
/*      */       }
/*      */       
/*  530 */       if (obj == this) {
/*  531 */         return true;
/*      */       }
/*      */       
/*  534 */       if (!(obj instanceof TableMetaDataKey)) {
/*  535 */         return false;
/*      */       }
/*  537 */       return (compareTo((TableMetaDataKey)obj) == 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  542 */       assert false : "hashCode not designed";
/*  543 */       return 0;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected class ComparableWrapper<K extends Comparable<? super K>, V>
/*      */     implements Comparable<ComparableWrapper<K, V>>
/*      */   {
/*      */     K key;
/*      */ 
/*      */     
/*      */     V value;
/*      */ 
/*      */ 
/*      */     
/*      */     public ComparableWrapper(K key, V value) {
/*  560 */       this.key = key;
/*  561 */       this.value = value;
/*      */     }
/*      */     
/*      */     public K getKey() {
/*  565 */       return this.key;
/*      */     }
/*      */     
/*      */     public V getValue() {
/*  569 */       return this.value;
/*      */     }
/*      */     
/*      */     public int compareTo(ComparableWrapper<K, V> other) {
/*  573 */       return ((Comparable)getKey()).compareTo(other.getKey());
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  578 */       if (obj == null) {
/*  579 */         return false;
/*      */       }
/*      */       
/*  582 */       if (obj == this) {
/*  583 */         return true;
/*      */       }
/*      */       
/*  586 */       if (!(obj instanceof ComparableWrapper)) {
/*  587 */         return false;
/*      */       }
/*      */       
/*  590 */       Object otherKey = ((ComparableWrapper)obj).getKey();
/*  591 */       return this.key.equals(otherKey);
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  596 */       assert false : "hashCode not designed";
/*  597 */       return 0;
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/*  602 */       return "{KEY:" + this.key + "; VALUE:" + this.value + "}";
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected enum TableType
/*      */   {
/*  610 */     LOCAL_TEMPORARY("LOCAL TEMPORARY"), SYSTEM_TABLE("SYSTEM TABLE"), SYSTEM_VIEW("SYSTEM VIEW"), TABLE("TABLE", new String[] { "BASE TABLE" }),
/*  611 */     VIEW("VIEW"), UNKNOWN("UNKNOWN");
/*      */ 
/*      */     
/*      */     private String name;
/*      */     
/*      */     private byte[] nameAsBytes;
/*      */     
/*      */     private String[] synonyms;
/*      */ 
/*      */     
/*      */     TableType(String tableTypeName, String[] tableTypeSynonyms) {
/*  622 */       this.name = tableTypeName;
/*  623 */       this.nameAsBytes = tableTypeName.getBytes();
/*  624 */       this.synonyms = tableTypeSynonyms;
/*      */     }
/*      */     
/*      */     String getName() {
/*  628 */       return this.name;
/*      */     }
/*      */     
/*      */     byte[] asBytes() {
/*  632 */       return this.nameAsBytes;
/*      */     }
/*      */     
/*      */     boolean equalsTo(String tableTypeName) {
/*  636 */       return this.name.equalsIgnoreCase(tableTypeName);
/*      */     }
/*      */     
/*      */     static TableType getTableTypeEqualTo(String tableTypeName) {
/*  640 */       for (TableType tableType : values()) {
/*  641 */         if (tableType.equalsTo(tableTypeName)) {
/*  642 */           return tableType;
/*      */         }
/*      */       } 
/*  645 */       return UNKNOWN;
/*      */     }
/*      */     
/*      */     boolean compliesWith(String tableTypeName) {
/*  649 */       if (equalsTo(tableTypeName)) {
/*  650 */         return true;
/*      */       }
/*  652 */       if (this.synonyms != null) {
/*  653 */         for (String synonym : this.synonyms) {
/*  654 */           if (synonym.equalsIgnoreCase(tableTypeName)) {
/*  655 */             return true;
/*      */           }
/*      */         } 
/*      */       }
/*  659 */       return false;
/*      */     }
/*      */     
/*      */     static TableType getTableTypeCompliantWith(String tableTypeName) {
/*  663 */       for (TableType tableType : values()) {
/*  664 */         if (tableType.compliesWith(tableTypeName)) {
/*  665 */           return tableType;
/*      */         }
/*      */       } 
/*  668 */       return UNKNOWN;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected enum ProcedureType
/*      */   {
/*  676 */     PROCEDURE, FUNCTION;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  684 */   protected static final byte[] TABLE_AS_BYTES = "TABLE".getBytes();
/*      */   
/*  686 */   protected static final byte[] SYSTEM_TABLE_AS_BYTES = "SYSTEM TABLE".getBytes();
/*      */   
/*  688 */   protected static final byte[] VIEW_AS_BYTES = "VIEW".getBytes();
/*      */ 
/*      */   
/*  691 */   private static final String[] MYSQL_KEYWORDS = new String[] { "ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DENSE_RANK", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "EMPTY", "ENCLOSED", "ESCAPED", "EXCEPT", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FIRST_VALUE", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "FUNCTION", "GENERATED", "GET", "GRANT", "GROUP", "GROUPING", "GROUPS", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IO_AFTER_GTIDS", "IO_BEFORE_GTIDS", "IS", "ITERATE", "JOIN", "JSON_TABLE", "KEY", "KEYS", "KILL", "LAG", "LAST_VALUE", "LEAD", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_BIND", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NTH_VALUE", "NTILE", "NULL", "NUMERIC", "OF", "ON", "OPTIMIZE", "OPTIMIZER_COSTS", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "OVER", "PARTITION", "PERCENT_RANK", "PERSIST", "PERSIST_ONLY", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "RANK", "READ", "READS", "READ_WRITE", "REAL", "RECURSIVE", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "ROW", "ROWS", "ROW_NUMBER", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SIGNAL", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STORED", "STRAIGHT_JOIN", "SYSTEM", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "VIRTUAL", "WHEN", "WHERE", "WHILE", "WINDOW", "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL" };
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
/*  714 */   static final List<String> SQL2003_KEYWORDS = Arrays.asList(new String[] { "ABS", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "ARRAY", "AS", "ASENSITIVE", "ASYMMETRIC", "AT", "ATOMIC", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOOLEAN", "BOTH", "BY", "CALL", "CALLED", "CARDINALITY", "CASCADED", "CASE", "CAST", "CEIL", "CEILING", "CHAR", "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK", "CLOB", "CLOSE", "COALESCE", "COLLATE", "COLLECT", "COLUMN", "COMMIT", "CONDITION", "CONNECT", "CONSTRAINT", "CONVERT", "CORR", "CORRESPONDING", "COUNT", "COVAR_POP", "COVAR_SAMP", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CYCLE", "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELETE", "DENSE_RANK", "DEREF", "DESCRIBE", "DETERMINISTIC", "DISCONNECT", "DISTINCT", "DOUBLE", "DROP", "DYNAMIC", "EACH", "ELEMENT", "ELSE", "END", "END-EXEC", "ESCAPE", "EVERY", "EXCEPT", "EXEC", "EXECUTE", "EXISTS", "EXP", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FILTER", "FLOAT", "FLOOR", "FOR", "FOREIGN", "FREE", "FROM", "FULL", "FUNCTION", "FUSION", "GET", "GLOBAL", "GRANT", "GROUP", "GROUPING", "HAVING", "HOLD", "HOUR", "IDENTITY", "IN", "INDICATOR", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERSECTION", "INTERVAL", "INTO", "IS", "JOIN", "LANGUAGE", "LARGE", "LATERAL", "LEADING", "LEFT", "LIKE", "LN", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOWER", "MATCH", "MAX", "MEMBER", "MERGE", "METHOD", "MIN", "MINUTE", "MOD", "MODIFIES", "MODULE", "MONTH", "MULTISET", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NEW", "NO", "NONE", "NORMALIZE", "NOT", "NULL", "NULLIF", "NUMERIC", "OCTET_LENGTH", "OF", "OLD", "ON", "ONLY", "OPEN", "OR", "ORDER", "OUT", "OUTER", "OVER", "OVERLAPS", "OVERLAY", "PARAMETER", "PARTITION", "PERCENTILE_CONT", "PERCENTILE_DISC", "PERCENT_RANK", "POSITION", "POWER", "PRECISION", "PREPARE", "PRIMARY", "PROCEDURE", "RANGE", "RANK", "READS", "REAL", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "RELEASE", "RESULT", "RETURN", "RETURNS", "REVOKE", "RIGHT", "ROLLBACK", "ROLLUP", "ROW", "ROWS", "ROW_NUMBER", "SAVEPOINT", "SCOPE", "SCROLL", "SEARCH", "SECOND", "SELECT", "SENSITIVE", "SESSION_USER", "SET", "SIMILAR", "SMALLINT", "SOME", "SPECIFIC", "SPECIFICTYPE", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQRT", "START", "STATIC", "STDDEV_POP", "STDDEV_SAMP", "SUBMULTISET", "SUBSTRING", "SUM", "SYMMETRIC", "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESAMPLE", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSLATE", "TRANSLATION", "TREAT", "TRIGGER", "TRIM", "TRUE", "UESCAPE", "UNION", "UNIQUE", "UNKNOWN", "UNNEST", "UPDATE", "UPPER", "USER", "USING", "VALUE", "VALUES", "VARCHAR", "VARYING", "VAR_POP", "VAR_SAMP", "WHEN", "WHENEVER", "WHERE", "WIDTH_BUCKET", "WINDOW", "WITH", "WITHIN", "WITHOUT", "YEAR" });
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
/*  738 */   private static volatile String mysqlKeywords = null;
/*      */ 
/*      */   
/*      */   protected JdbcConnection conn;
/*      */ 
/*      */   
/*      */   protected NativeSession session;
/*      */   
/*  746 */   protected String database = null;
/*      */   
/*      */   protected final String quotedId;
/*      */   
/*      */   protected boolean pedantic;
/*      */   
/*      */   protected boolean tinyInt1isBit;
/*      */   
/*      */   protected boolean transformedBitIsBoolean;
/*      */   
/*      */   protected boolean useHostsInPrivileges;
/*      */   
/*      */   protected RuntimeProperty<PropertyDefinitions.DatabaseTerm> databaseTerm;
/*      */   protected RuntimeProperty<Boolean> nullDatabaseMeansCurrent;
/*      */   protected ResultSetFactory resultSetFactory;
/*      */   private String metadataEncoding;
/*      */   private int metadataCollationIndex;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*      */   
/*      */   protected static DatabaseMetaData getInstance(JdbcConnection connToSet, String databaseToSet, boolean checkForInfoSchema, ResultSetFactory resultSetFactory) throws SQLException {
/*  766 */     if (checkForInfoSchema && ((Boolean)connToSet.getPropertySet().getBooleanProperty(PropertyKey.useInformationSchema).getValue()).booleanValue()) {
/*  767 */       return new DatabaseMetaDataUsingInfoSchema(connToSet, databaseToSet, resultSetFactory);
/*      */     }
/*      */     
/*  770 */     return new DatabaseMetaData(connToSet, databaseToSet, resultSetFactory);
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
/*      */   protected DatabaseMetaData(JdbcConnection connToSet, String databaseToSet, ResultSetFactory resultSetFactory) {
/*  784 */     this.conn = connToSet;
/*  785 */     this.session = (NativeSession)connToSet.getSession();
/*  786 */     this.database = databaseToSet;
/*  787 */     this.resultSetFactory = resultSetFactory;
/*  788 */     this.exceptionInterceptor = this.conn.getExceptionInterceptor();
/*  789 */     this.databaseTerm = this.conn.getPropertySet().getEnumProperty(PropertyKey.databaseTerm);
/*  790 */     this.nullDatabaseMeansCurrent = this.conn.getPropertySet().getBooleanProperty(PropertyKey.nullDatabaseMeansCurrent);
/*  791 */     this.pedantic = ((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.pedantic).getValue()).booleanValue();
/*  792 */     this.tinyInt1isBit = ((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.tinyInt1isBit).getValue()).booleanValue();
/*  793 */     this.transformedBitIsBoolean = ((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.transformedBitIsBoolean).getValue()).booleanValue();
/*  794 */     this.useHostsInPrivileges = ((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.useHostsInPrivileges).getValue()).booleanValue();
/*  795 */     this.quotedId = this.session.getIdentifierQuoteString();
/*      */   }
/*      */   
/*      */   public boolean allProceduresAreCallable() throws SQLException {
/*      */     
/*  800 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean allTablesAreSelectable() throws SQLException {
/*      */     
/*  805 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   protected void convertToJdbcFunctionList(ResultSet proceduresRs, List<ComparableWrapper<String, Row>> procedureRows, Field[] fields) throws SQLException {
/*  809 */     while (proceduresRs.next()) {
/*  810 */       String procDb = proceduresRs.getString("db");
/*  811 */       String functionName = proceduresRs.getString("name");
/*      */       
/*  813 */       byte[][] rowData = (byte[][])null;
/*      */       
/*  815 */       if (fields != null && fields.length == 9) {
/*      */         
/*  817 */         rowData = new byte[9][];
/*  818 */         rowData[0] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b("def") : s2b(procDb);
/*  819 */         rowData[1] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b(procDb) : null;
/*  820 */         rowData[2] = s2b(functionName);
/*  821 */         rowData[3] = null;
/*  822 */         rowData[4] = null;
/*  823 */         rowData[5] = null;
/*  824 */         rowData[6] = s2b(proceduresRs.getString("comment"));
/*  825 */         rowData[7] = s2b(Integer.toString(2));
/*  826 */         rowData[8] = s2b(functionName);
/*      */       } else {
/*      */         
/*  829 */         rowData = new byte[6][];
/*      */         
/*  831 */         rowData[0] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b("def") : s2b(procDb);
/*  832 */         rowData[1] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b(procDb) : null;
/*  833 */         rowData[2] = s2b(functionName);
/*  834 */         rowData[3] = s2b(proceduresRs.getString("comment"));
/*  835 */         rowData[4] = s2b(Integer.toString(1));
/*  836 */         rowData[5] = s2b(functionName);
/*      */       } 
/*      */       
/*  839 */       procedureRows.add((ComparableWrapper)new ComparableWrapper<>(StringUtils.getFullyQualifiedName(procDb, functionName, this.quotedId, this.pedantic), new ByteArrayRow(rowData, 
/*  840 */               getExceptionInterceptor())));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void convertToJdbcProcedureList(boolean fromSelect, ResultSet proceduresRs, List<ComparableWrapper<String, Row>> procedureRows) throws SQLException {
/*  846 */     while (proceduresRs.next()) {
/*  847 */       String procDb = proceduresRs.getString("db");
/*  848 */       String procedureName = proceduresRs.getString("name");
/*  849 */       byte[][] rowData = new byte[9][];
/*  850 */       rowData[0] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b("def") : s2b(procDb);
/*  851 */       rowData[1] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b(procDb) : null;
/*  852 */       rowData[2] = s2b(procedureName);
/*  853 */       rowData[3] = null;
/*  854 */       rowData[4] = null;
/*  855 */       rowData[5] = null;
/*  856 */       rowData[6] = s2b(proceduresRs.getString("comment"));
/*      */       
/*  858 */       boolean isFunction = fromSelect ? "FUNCTION".equalsIgnoreCase(proceduresRs.getString("type")) : false;
/*  859 */       rowData[7] = s2b(isFunction ? Integer.toString(2) : Integer.toString(1));
/*      */       
/*  861 */       rowData[8] = s2b(procedureName);
/*      */       
/*  863 */       procedureRows.add((ComparableWrapper)new ComparableWrapper<>(StringUtils.getFullyQualifiedName(procDb, procedureName, this.quotedId, this.pedantic), new ByteArrayRow(rowData, 
/*  864 */               getExceptionInterceptor())));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private Row convertTypeDescriptorToProcedureRow(byte[] procNameAsBytes, byte[] procCatAsBytes, String paramName, boolean isOutParam, boolean isInParam, boolean isReturnParam, TypeDescriptor typeDesc, boolean forGetFunctionColumns, int ordinal) throws SQLException {
/*  870 */     byte[][] row = forGetFunctionColumns ? new byte[17][] : new byte[20][];
/*  871 */     row[0] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b("def") : procCatAsBytes;
/*  872 */     row[1] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? procCatAsBytes : null;
/*  873 */     row[2] = procNameAsBytes;
/*  874 */     row[3] = s2b(paramName);
/*  875 */     row[4] = s2b(String.valueOf(getColumnType(isOutParam, isInParam, isReturnParam, forGetFunctionColumns)));
/*  876 */     row[5] = s2b(Short.toString((short)typeDesc.mysqlType.getJdbcType()));
/*  877 */     row[6] = s2b(typeDesc.mysqlType.getName());
/*  878 */     row[7] = (typeDesc.datetimePrecision == null) ? s2b(typeDesc.columnSize.toString()) : s2b(typeDesc.datetimePrecision.toString());
/*  879 */     row[8] = (typeDesc.columnSize == null) ? null : s2b(typeDesc.columnSize.toString());
/*  880 */     row[9] = (typeDesc.decimalDigits == null) ? null : s2b(typeDesc.decimalDigits.toString());
/*  881 */     row[10] = s2b(Integer.toString(typeDesc.numPrecRadix));
/*      */     
/*  883 */     switch (typeDesc.nullability) {
/*      */       case 0:
/*  885 */         row[11] = s2b(String.valueOf(0));
/*      */         break;
/*      */       
/*      */       case 1:
/*  889 */         row[11] = s2b(String.valueOf(1));
/*      */         break;
/*      */       
/*      */       case 2:
/*  893 */         row[11] = s2b(String.valueOf(2));
/*      */         break;
/*      */       
/*      */       default:
/*  897 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.1"), "S1000", 
/*  898 */             getExceptionInterceptor());
/*      */     } 
/*      */     
/*  901 */     row[12] = null;
/*      */     
/*  903 */     if (forGetFunctionColumns) {
/*  904 */       row[13] = (typeDesc.charOctetLength == null) ? null : s2b(typeDesc.charOctetLength.toString());
/*  905 */       row[14] = s2b(String.valueOf(ordinal));
/*  906 */       row[15] = s2b(typeDesc.isNullable);
/*  907 */       row[16] = procNameAsBytes;
/*      */     } else {
/*      */       
/*  910 */       row[13] = null;
/*  911 */       row[14] = null;
/*  912 */       row[15] = null;
/*  913 */       row[16] = (typeDesc.charOctetLength == null) ? null : s2b(typeDesc.charOctetLength.toString());
/*  914 */       row[17] = s2b(String.valueOf(ordinal));
/*  915 */       row[18] = s2b(typeDesc.isNullable);
/*  916 */       row[19] = procNameAsBytes;
/*      */     } 
/*      */     
/*  919 */     return (Row)new ByteArrayRow(row, getExceptionInterceptor());
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
/*      */   protected int getColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns) {
/*  938 */     return getProcedureOrFunctionColumnType(isOutParam, isInParam, isReturnParam, forGetFunctionColumns);
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
/*      */   protected static int getProcedureOrFunctionColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns) {
/*  957 */     if (isInParam && isOutParam)
/*  958 */       return forGetFunctionColumns ? 2 : 2; 
/*  959 */     if (isInParam)
/*  960 */       return forGetFunctionColumns ? 1 : 1; 
/*  961 */     if (isOutParam)
/*  962 */       return forGetFunctionColumns ? 3 : 4; 
/*  963 */     if (isReturnParam) {
/*  964 */       return forGetFunctionColumns ? 4 : 5;
/*      */     }
/*  966 */     return forGetFunctionColumns ? 0 : 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ExceptionInterceptor getExceptionInterceptor() {
/*  973 */     return this.exceptionInterceptor;
/*      */   }
/*      */   
/*      */   public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
/*      */     
/*  978 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
/*      */     
/*  983 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean deletesAreDetected(int type) throws SQLException {
/*      */     
/*  988 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
/*      */     
/*  993 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
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
/*      */   public List<Row> extractForeignKeyForTable(ArrayList<Row> rows, ResultSet rs, String dbName) throws SQLException {
/* 1010 */     byte[][] row = new byte[3][];
/* 1011 */     row[0] = rs.getBytes(1);
/* 1012 */     row[1] = s2b("SUPPORTS_FK");
/*      */     
/* 1014 */     String createTableString = rs.getString(2);
/* 1015 */     StringTokenizer lineTokenizer = new StringTokenizer(createTableString, "\n");
/* 1016 */     StringBuilder commentBuf = new StringBuilder("comment; ");
/* 1017 */     boolean firstTime = true;
/*      */     
/* 1019 */     while (lineTokenizer.hasMoreTokens()) {
/* 1020 */       String line = lineTokenizer.nextToken().trim();
/*      */       
/* 1022 */       String constraintName = null;
/*      */       
/* 1024 */       if (StringUtils.startsWithIgnoreCase(line, "CONSTRAINT")) {
/* 1025 */         boolean usingBackTicks = true;
/* 1026 */         int beginPos = StringUtils.indexOfQuoteDoubleAware(line, this.quotedId, 0);
/*      */         
/* 1028 */         if (beginPos == -1) {
/* 1029 */           beginPos = line.indexOf("\"");
/* 1030 */           usingBackTicks = false;
/*      */         } 
/*      */         
/* 1033 */         if (beginPos != -1) {
/* 1034 */           int endPos = -1;
/*      */           
/* 1036 */           if (usingBackTicks) {
/* 1037 */             endPos = StringUtils.indexOfQuoteDoubleAware(line, this.quotedId, beginPos + 1);
/*      */           } else {
/* 1039 */             endPos = StringUtils.indexOfQuoteDoubleAware(line, "\"", beginPos + 1);
/*      */           } 
/*      */           
/* 1042 */           if (endPos != -1) {
/* 1043 */             constraintName = line.substring(beginPos + 1, endPos);
/* 1044 */             line = line.substring(endPos + 1, line.length()).trim();
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 1049 */       if (line.startsWith("FOREIGN KEY")) {
/* 1050 */         if (line.endsWith(",")) {
/* 1051 */           line = line.substring(0, line.length() - 1);
/*      */         }
/*      */         
/* 1054 */         int indexOfFK = line.indexOf("FOREIGN KEY");
/*      */         
/* 1056 */         String localColumnName = null;
/* 1057 */         String referencedDbName = StringUtils.quoteIdentifier(dbName, this.quotedId, this.pedantic);
/* 1058 */         String referencedTableName = null;
/* 1059 */         String referencedColumnName = null;
/*      */         
/* 1061 */         if (indexOfFK != -1) {
/* 1062 */           int afterFk = indexOfFK + "FOREIGN KEY".length();
/*      */           
/* 1064 */           int indexOfRef = StringUtils.indexOfIgnoreCase(afterFk, line, "REFERENCES", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */ 
/*      */           
/* 1067 */           if (indexOfRef != -1) {
/*      */             
/* 1069 */             int indexOfParenOpen = line.indexOf('(', afterFk);
/* 1070 */             int indexOfParenClose = StringUtils.indexOfIgnoreCase(indexOfParenOpen, line, ")", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */ 
/*      */             
/* 1073 */             if (indexOfParenOpen == -1 || indexOfParenClose == -1);
/*      */ 
/*      */ 
/*      */             
/* 1077 */             localColumnName = line.substring(indexOfParenOpen + 1, indexOfParenClose);
/*      */             
/* 1079 */             int afterRef = indexOfRef + "REFERENCES".length();
/*      */             
/* 1081 */             int referencedColumnBegin = StringUtils.indexOfIgnoreCase(afterRef, line, "(", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */ 
/*      */             
/* 1084 */             if (referencedColumnBegin != -1) {
/* 1085 */               referencedTableName = line.substring(afterRef, referencedColumnBegin);
/*      */               
/* 1087 */               int referencedColumnEnd = StringUtils.indexOfIgnoreCase(referencedColumnBegin + 1, line, ")", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */ 
/*      */               
/* 1090 */               if (referencedColumnEnd != -1) {
/* 1091 */                 referencedColumnName = line.substring(referencedColumnBegin + 1, referencedColumnEnd);
/*      */               }
/*      */               
/* 1094 */               int indexOfDbSep = StringUtils.indexOfIgnoreCase(0, referencedTableName, ".", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */ 
/*      */               
/* 1097 */               if (indexOfDbSep != -1) {
/* 1098 */                 referencedDbName = referencedTableName.substring(0, indexOfDbSep);
/* 1099 */                 referencedTableName = referencedTableName.substring(indexOfDbSep + 1);
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */         
/* 1105 */         if (!firstTime) {
/* 1106 */           commentBuf.append("; ");
/*      */         } else {
/* 1108 */           firstTime = false;
/*      */         } 
/*      */         
/* 1111 */         if (constraintName != null) {
/* 1112 */           commentBuf.append(constraintName);
/*      */         } else {
/* 1114 */           commentBuf.append("not_available");
/*      */         } 
/*      */         
/* 1117 */         commentBuf.append("(");
/* 1118 */         commentBuf.append(localColumnName);
/* 1119 */         commentBuf.append(") REFER ");
/* 1120 */         commentBuf.append(referencedDbName);
/* 1121 */         commentBuf.append("/");
/* 1122 */         commentBuf.append(referencedTableName);
/* 1123 */         commentBuf.append("(");
/* 1124 */         commentBuf.append(referencedColumnName);
/* 1125 */         commentBuf.append(")");
/*      */         
/* 1127 */         int lastParenIndex = line.lastIndexOf(")");
/*      */         
/* 1129 */         if (lastParenIndex != line.length() - 1) {
/* 1130 */           String cascadeOptions = line.substring(lastParenIndex + 1);
/* 1131 */           commentBuf.append(" ");
/* 1132 */           commentBuf.append(cascadeOptions);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1137 */     row[2] = s2b(commentBuf.toString());
/* 1138 */     rows.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */     
/* 1140 */     return rows;
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
/*      */   public ResultSet extractForeignKeyFromCreateTable(String dbName, String tableName) throws SQLException {
/* 1156 */     ArrayList<String> tableList = new ArrayList<>();
/* 1157 */     ResultSet rs = null;
/* 1158 */     Statement stmt = null;
/*      */     
/* 1160 */     if (tableName != null) {
/* 1161 */       tableList.add(tableName);
/*      */     } else {
/*      */       
/*      */       try {
/* 1165 */         rs = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? getTables(null, dbName, null, new String[] { "TABLE" }) : getTables(dbName, null, null, new String[] { "TABLE" });
/*      */         
/* 1167 */         while (rs.next()) {
/* 1168 */           tableList.add(rs.getString("TABLE_NAME"));
/*      */         }
/*      */       } finally {
/* 1171 */         if (rs != null) {
/* 1172 */           rs.close();
/*      */         }
/*      */         
/* 1175 */         rs = null;
/*      */       } 
/*      */     } 
/*      */     
/* 1179 */     ArrayList<Row> rows = new ArrayList<>();
/* 1180 */     Field[] fields = new Field[3];
/* 1181 */     fields[0] = new Field("", "Name", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 2147483647);
/* 1182 */     fields[1] = new Field("", "Type", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 1183 */     fields[2] = new Field("", "Comment", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 2147483647);
/*      */     
/* 1185 */     int numTables = tableList.size();
/* 1186 */     stmt = this.conn.getMetadataSafeStatement();
/*      */     
/*      */     try {
/* 1189 */       for (int i = 0; i < numTables; i++) {
/* 1190 */         String tableToExtract = tableList.get(i);
/*      */ 
/*      */         
/* 1193 */         String query = "SHOW CREATE TABLE " + StringUtils.getFullyQualifiedName(dbName, tableToExtract, this.quotedId, this.pedantic);
/*      */         
/*      */         try {
/* 1196 */           rs = stmt.executeQuery(query);
/* 1197 */         } catch (SQLException sqlEx) {
/*      */           
/* 1199 */           String sqlState = sqlEx.getSQLState();
/*      */           
/* 1201 */           if (!"42S02".equals(sqlState) && sqlEx.getErrorCode() != 1146 && sqlEx
/* 1202 */             .getErrorCode() != 1049) {
/* 1203 */             throw sqlEx;
/*      */           }
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1209 */         while (rs != null && rs.next()) {
/* 1210 */           extractForeignKeyForTable(rows, rs, dbName);
/*      */         }
/*      */       } 
/*      */     } finally {
/* 1214 */       if (rs != null) {
/* 1215 */         rs.close();
/*      */       }
/*      */       
/* 1218 */       rs = null;
/*      */       
/* 1220 */       if (stmt != null) {
/* 1221 */         stmt.close();
/*      */       }
/*      */       
/* 1224 */       stmt = null;
/*      */     } 
/*      */     
/* 1227 */     return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultSet getAttributes(String arg0, String arg1, String arg2, String arg3) throws SQLException {
/*      */     
/* 1233 */     try { Field[] fields = new Field[21];
/* 1234 */       fields[0] = new Field("", "TYPE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1235 */       fields[1] = new Field("", "TYPE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1236 */       fields[2] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1237 */       fields[3] = new Field("", "ATTR_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1238 */       fields[4] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 32);
/* 1239 */       fields[5] = new Field("", "ATTR_TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1240 */       fields[6] = new Field("", "ATTR_SIZE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
/* 1241 */       fields[7] = new Field("", "DECIMAL_DIGITS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
/* 1242 */       fields[8] = new Field("", "NUM_PREC_RADIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
/* 1243 */       fields[9] = new Field("", "NULLABLE ", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
/* 1244 */       fields[10] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1245 */       fields[11] = new Field("", "ATTR_DEF", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1246 */       fields[12] = new Field("", "SQL_DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
/* 1247 */       fields[13] = new Field("", "SQL_DATETIME_SUB", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
/* 1248 */       fields[14] = new Field("", "CHAR_OCTET_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
/* 1249 */       fields[15] = new Field("", "ORDINAL_POSITION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
/* 1250 */       fields[16] = new Field("", "IS_NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1251 */       fields[17] = new Field("", "SCOPE_CATALOG", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1252 */       fields[18] = new Field("", "SCOPE_SCHEMA", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1253 */       fields[19] = new Field("", "SCOPE_TABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1254 */       fields[20] = new Field("", "SOURCE_DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 32);
/*      */       
/* 1256 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(new ArrayList(), (ColumnDefinition)new DefaultColumnDefinition(fields))); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getBestRowIdentifier(String catalog, String schema, final String table, int scope, boolean nullable) throws SQLException {
/*      */     try {
/* 1262 */       if (table == null) {
/* 1263 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
/* 1264 */             getExceptionInterceptor());
/*      */       }
/*      */       
/* 1267 */       Field[] fields = new Field[8];
/* 1268 */       fields[0] = new Field("", "SCOPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
/* 1269 */       fields[1] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1270 */       fields[2] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
/* 1271 */       fields[3] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 1272 */       fields[4] = new Field("", "COLUMN_SIZE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 1273 */       fields[5] = new Field("", "BUFFER_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 1274 */       fields[6] = new Field("", "DECIMAL_DIGITS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 10);
/* 1275 */       fields[7] = new Field("", "PSEUDO_COLUMN", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
/*      */       
/* 1277 */       final ArrayList<Row> rows = new ArrayList<>();
/* 1278 */       final Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       
/* 1280 */       String db = getDatabase(catalog, schema);
/*      */ 
/*      */       
/*      */       try {
/* 1284 */         (new IterateBlock<String>(getDatabaseIterator(db))
/*      */           {
/*      */             void forEach(String dbStr) throws SQLException {
/* 1287 */               ResultSet results = null;
/*      */               
/*      */               try {
/* 1290 */                 StringBuilder queryBuf = new StringBuilder("SHOW COLUMNS FROM ");
/* 1291 */                 queryBuf.append(StringUtils.quoteIdentifier(table, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/* 1292 */                 queryBuf.append(" FROM ");
/* 1293 */                 queryBuf.append(StringUtils.quoteIdentifier(dbStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/*      */                 
/*      */                 try {
/* 1296 */                   results = stmt.executeQuery(queryBuf.toString());
/* 1297 */                 } catch (SQLException sqlEx) {
/* 1298 */                   String sqlState = sqlEx.getSQLState();
/* 1299 */                   int errorCode = sqlEx.getErrorCode();
/*      */                   
/* 1301 */                   if (!"42S02".equals(sqlState) && errorCode != 1146 && errorCode != 1049)
/*      */                   {
/* 1303 */                     throw sqlEx;
/*      */                   }
/*      */                 } 
/*      */                 
/* 1307 */                 while (results != null && results.next()) {
/* 1308 */                   String keyType = results.getString("Key");
/*      */                   
/* 1310 */                   if (keyType != null && 
/* 1311 */                     StringUtils.startsWithIgnoreCase(keyType, "PRI")) {
/* 1312 */                     byte[][] rowVal = new byte[8][];
/* 1313 */                     rowVal[0] = Integer.toString(2).getBytes();
/* 1314 */                     rowVal[1] = results.getBytes("Field");
/*      */                     
/* 1316 */                     String type = results.getString("Type");
/* 1317 */                     int size = stmt.getMaxFieldSize();
/* 1318 */                     int decimals = 0;
/* 1319 */                     boolean hasLength = false;
/*      */ 
/*      */ 
/*      */ 
/*      */                     
/* 1324 */                     if (type.indexOf("enum") != -1) {
/* 1325 */                       String temp = type.substring(type.indexOf("("), type.indexOf(")"));
/* 1326 */                       StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/* 1327 */                       int maxLength = 0;
/*      */                       
/* 1329 */                       while (tokenizer.hasMoreTokens()) {
/* 1330 */                         maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2);
/*      */                       }
/*      */                       
/* 1333 */                       size = maxLength;
/* 1334 */                       decimals = 0;
/* 1335 */                       type = "enum";
/* 1336 */                     } else if (type.indexOf("(") != -1) {
/* 1337 */                       hasLength = true;
/* 1338 */                       if (type.indexOf(",") != -1) {
/* 1339 */                         size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(",")));
/* 1340 */                         decimals = Integer.parseInt(type.substring(type.indexOf(",") + 1, type.indexOf(")")));
/*      */                       } else {
/* 1342 */                         size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(")")));
/*      */                       } 
/*      */                       
/* 1345 */                       type = type.substring(0, type.indexOf("("));
/*      */                     } 
/*      */                     
/* 1348 */                     MysqlType ft = MysqlType.getByName(type.toUpperCase());
/* 1349 */                     rowVal[2] = DatabaseMetaData.this.s2b(String.valueOf(ft.getJdbcType()));
/* 1350 */                     rowVal[3] = DatabaseMetaData.this.s2b(type);
/* 1351 */                     rowVal[4] = hasLength ? Integer.toString(size + decimals).getBytes() : Long.toString(ft.getPrecision().longValue()).getBytes();
/* 1352 */                     rowVal[5] = Integer.toString(DatabaseMetaData.maxBufferSize).getBytes();
/* 1353 */                     rowVal[6] = Integer.toString(decimals).getBytes();
/* 1354 */                     rowVal[7] = Integer.toString(1).getBytes();
/*      */                     
/* 1356 */                     rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                   }
/*      */                 
/*      */                 } 
/* 1360 */               } catch (SQLException sqlEx) {
/* 1361 */                 if (!"42S02".equals(sqlEx.getSQLState())) {
/* 1362 */                   throw sqlEx;
/*      */                 }
/*      */               } finally {
/* 1365 */                 if (results != null) {
/*      */                   try {
/* 1367 */                     results.close();
/* 1368 */                   } catch (Exception exception) {}
/*      */ 
/*      */                   
/* 1371 */                   results = null;
/*      */                 } 
/*      */               } 
/*      */             }
/* 1375 */           }).doForAll();
/*      */       } finally {
/* 1377 */         if (stmt != null) {
/* 1378 */           stmt.close();
/*      */         }
/*      */       } 
/*      */       
/* 1382 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
/*      */     } catch (CJException cJException) {
/*      */       
/* 1385 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void getCallStmtParameterTypes(String db, String quotedProcName, ProcedureType procType, String parameterNamePattern, List<Row> resultRows, boolean forGetFunctionColumns) throws SQLException {
/* 1397 */     Statement paramRetrievalStmt = null;
/* 1398 */     ResultSet paramRetrievalRs = null;
/*      */     
/* 1400 */     String parameterDef = null;
/*      */     
/* 1402 */     byte[] procNameAsBytes = null;
/* 1403 */     byte[] procCatAsBytes = null;
/*      */     
/* 1405 */     boolean isProcedureInAnsiMode = false;
/* 1406 */     String storageDefnDelims = null;
/* 1407 */     String storageDefnClosures = null;
/*      */     
/*      */     try {
/* 1410 */       paramRetrievalStmt = this.conn.getMetadataSafeStatement();
/* 1411 */       String oldDb = this.conn.getDatabase();
/* 1412 */       if (this.conn.lowerCaseTableNames() && db != null && db.length() != 0 && oldDb != null && oldDb.length() != 0) {
/*      */ 
/*      */         
/* 1415 */         ResultSet rs = null;
/*      */         
/*      */         try {
/* 1418 */           this.conn.setDatabase(StringUtils.unQuoteIdentifier(db, this.quotedId));
/* 1419 */           rs = paramRetrievalStmt.executeQuery("SELECT DATABASE()");
/* 1420 */           rs.next();
/*      */           
/* 1422 */           db = rs.getString(1);
/*      */         }
/*      */         finally {
/*      */           
/* 1426 */           this.conn.setDatabase(oldDb);
/*      */           
/* 1428 */           if (rs != null) {
/* 1429 */             rs.close();
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/* 1434 */       if (paramRetrievalStmt.getMaxRows() != 0) {
/* 1435 */         paramRetrievalStmt.setMaxRows(0);
/*      */       }
/*      */ 
/*      */       
/* 1439 */       int dotIndex = " ".equals(this.quotedId) ? quotedProcName.indexOf(".") : StringUtils.indexOfIgnoreCase(0, quotedProcName, ".", this.quotedId, this.quotedId, 
/* 1440 */           this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */       
/* 1442 */       String dbName = null;
/*      */       
/* 1444 */       if (dotIndex != -1 && dotIndex + 1 < quotedProcName.length()) {
/* 1445 */         dbName = quotedProcName.substring(0, dotIndex);
/* 1446 */         quotedProcName = quotedProcName.substring(dotIndex + 1);
/*      */       } else {
/* 1448 */         dbName = StringUtils.quoteIdentifier(db, this.quotedId, this.pedantic);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1453 */       String tmpProcName = StringUtils.unQuoteIdentifier(quotedProcName, this.quotedId);
/* 1454 */       procNameAsBytes = StringUtils.getBytes(tmpProcName, "UTF-8");
/*      */       
/* 1456 */       tmpProcName = StringUtils.unQuoteIdentifier(dbName, this.quotedId);
/* 1457 */       procCatAsBytes = StringUtils.getBytes(tmpProcName, "UTF-8");
/*      */ 
/*      */       
/* 1460 */       StringBuilder procNameBuf = new StringBuilder();
/* 1461 */       procNameBuf.append(dbName);
/* 1462 */       procNameBuf.append('.');
/* 1463 */       procNameBuf.append(quotedProcName);
/*      */       
/* 1465 */       String fieldName = null;
/* 1466 */       if (procType == ProcedureType.PROCEDURE) {
/* 1467 */         paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE PROCEDURE " + procNameBuf.toString());
/* 1468 */         fieldName = "Create Procedure";
/*      */       } else {
/* 1470 */         paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE FUNCTION " + procNameBuf.toString());
/* 1471 */         fieldName = "Create Function";
/*      */       } 
/*      */       
/* 1474 */       if (paramRetrievalRs.next()) {
/* 1475 */         String procedureDef = paramRetrievalRs.getString(fieldName);
/*      */         
/* 1477 */         if (!((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.noAccessToProcedureBodies).getValue()).booleanValue() && (procedureDef == null || procedureDef
/* 1478 */           .length() == 0)) {
/* 1479 */           throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.4"), "S1000", 
/* 1480 */               getExceptionInterceptor());
/*      */         }
/*      */         
/*      */         try {
/* 1484 */           String sqlMode = paramRetrievalRs.getString("sql_mode");
/*      */           
/* 1486 */           if (StringUtils.indexOfIgnoreCase(sqlMode, "ANSI") != -1) {
/* 1487 */             isProcedureInAnsiMode = true;
/*      */           }
/* 1489 */         } catch (SQLException sQLException) {}
/*      */ 
/*      */ 
/*      */         
/* 1493 */         String identifierMarkers = isProcedureInAnsiMode ? "`\"" : "`";
/* 1494 */         String identifierAndStringMarkers = "'" + identifierMarkers;
/* 1495 */         storageDefnDelims = "(" + identifierMarkers;
/* 1496 */         storageDefnClosures = ")" + identifierMarkers;
/*      */         
/* 1498 */         if (procedureDef != null && procedureDef.length() != 0) {
/*      */           
/* 1500 */           procedureDef = StringUtils.stripCommentsAndHints(procedureDef, identifierAndStringMarkers, identifierAndStringMarkers, 
/* 1501 */               !this.session.getServerSession().isNoBackslashEscapesSet());
/*      */           
/* 1503 */           int openParenIndex = StringUtils.indexOfIgnoreCase(0, procedureDef, "(", this.quotedId, this.quotedId, 
/* 1504 */               this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__FULL);
/* 1505 */           int endOfParamDeclarationIndex = 0;
/*      */           
/* 1507 */           endOfParamDeclarationIndex = endPositionOfParameterDeclaration(openParenIndex, procedureDef, this.quotedId);
/*      */           
/* 1509 */           if (procType == ProcedureType.FUNCTION) {
/*      */ 
/*      */ 
/*      */             
/* 1513 */             int returnsIndex = StringUtils.indexOfIgnoreCase(0, procedureDef, " RETURNS ", this.quotedId, this.quotedId, 
/* 1514 */                 this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__FULL);
/*      */             
/* 1516 */             int endReturnsDef = findEndOfReturnsClause(procedureDef, returnsIndex);
/*      */ 
/*      */ 
/*      */             
/* 1520 */             int declarationStart = returnsIndex + "RETURNS ".length();
/*      */             
/* 1522 */             while (declarationStart < procedureDef.length() && 
/* 1523 */               Character.isWhitespace(procedureDef.charAt(declarationStart))) {
/* 1524 */               declarationStart++;
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1530 */             String returnsDefn = procedureDef.substring(declarationStart, endReturnsDef).trim();
/* 1531 */             TypeDescriptor returnDescriptor = new TypeDescriptor(returnsDefn, "YES");
/*      */             
/* 1533 */             resultRows.add(convertTypeDescriptorToProcedureRow(procNameAsBytes, procCatAsBytes, "", false, false, true, returnDescriptor, forGetFunctionColumns, 0));
/*      */           } 
/*      */ 
/*      */           
/* 1537 */           if (openParenIndex == -1 || endOfParamDeclarationIndex == -1)
/*      */           {
/* 1539 */             throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.5"), "S1000", 
/* 1540 */                 getExceptionInterceptor());
/*      */           }
/*      */           
/* 1543 */           parameterDef = procedureDef.substring(openParenIndex + 1, endOfParamDeclarationIndex);
/*      */         } 
/*      */       } 
/*      */     } finally {
/*      */       
/* 1548 */       SQLException sqlExRethrow = null;
/*      */       
/* 1550 */       if (paramRetrievalRs != null) {
/*      */         try {
/* 1552 */           paramRetrievalRs.close();
/* 1553 */         } catch (SQLException sqlEx) {
/* 1554 */           sqlExRethrow = sqlEx;
/*      */         } 
/*      */         
/* 1557 */         paramRetrievalRs = null;
/*      */       } 
/*      */       
/* 1560 */       if (paramRetrievalStmt != null) {
/*      */         try {
/* 1562 */           paramRetrievalStmt.close();
/* 1563 */         } catch (SQLException sqlEx) {
/* 1564 */           sqlExRethrow = sqlEx;
/*      */         } 
/*      */         
/* 1567 */         paramRetrievalStmt = null;
/*      */       } 
/*      */       
/* 1570 */       if (sqlExRethrow != null) {
/* 1571 */         throw sqlExRethrow;
/*      */       }
/*      */     } 
/*      */     
/* 1575 */     if (parameterDef != null) {
/* 1576 */       int ordinal = 1;
/*      */       
/* 1578 */       List<String> parseList = StringUtils.split(parameterDef, ",", storageDefnDelims, storageDefnClosures, true);
/*      */       
/* 1580 */       int parseListLen = parseList.size();
/*      */       
/* 1582 */       for (int i = 0; i < parseListLen; i++) {
/* 1583 */         String declaration = parseList.get(i);
/*      */         
/* 1585 */         if (declaration.trim().length() == 0) {
/*      */           break;
/*      */         }
/*      */ 
/*      */         
/* 1590 */         declaration = declaration.replaceAll("[\\t\\n\\x0B\\f\\r]", " ");
/* 1591 */         StringTokenizer declarationTok = new StringTokenizer(declaration, " \t");
/*      */         
/* 1593 */         String paramName = null;
/* 1594 */         boolean isOutParam = false;
/* 1595 */         boolean isInParam = false;
/*      */         
/* 1597 */         if (declarationTok.hasMoreTokens()) {
/* 1598 */           String possibleParamName = declarationTok.nextToken();
/*      */           
/* 1600 */           if (possibleParamName.equalsIgnoreCase("OUT")) {
/* 1601 */             isOutParam = true;
/*      */             
/* 1603 */             if (declarationTok.hasMoreTokens()) {
/* 1604 */               paramName = declarationTok.nextToken();
/*      */             } else {
/* 1606 */               throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.6"), "S1000", 
/* 1607 */                   getExceptionInterceptor());
/*      */             } 
/* 1609 */           } else if (possibleParamName.equalsIgnoreCase("INOUT")) {
/* 1610 */             isOutParam = true;
/* 1611 */             isInParam = true;
/*      */             
/* 1613 */             if (declarationTok.hasMoreTokens()) {
/* 1614 */               paramName = declarationTok.nextToken();
/*      */             } else {
/* 1616 */               throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.6"), "S1000", 
/* 1617 */                   getExceptionInterceptor());
/*      */             } 
/* 1619 */           } else if (possibleParamName.equalsIgnoreCase("IN")) {
/* 1620 */             isOutParam = false;
/* 1621 */             isInParam = true;
/*      */             
/* 1623 */             if (declarationTok.hasMoreTokens()) {
/* 1624 */               paramName = declarationTok.nextToken();
/*      */             } else {
/* 1626 */               throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.6"), "S1000", 
/* 1627 */                   getExceptionInterceptor());
/*      */             } 
/*      */           } else {
/* 1630 */             isOutParam = false;
/* 1631 */             isInParam = true;
/*      */             
/* 1633 */             paramName = possibleParamName;
/*      */           } 
/*      */           
/* 1636 */           TypeDescriptor typeDesc = null;
/*      */           
/* 1638 */           if (declarationTok.hasMoreTokens()) {
/* 1639 */             StringBuilder typeInfoBuf = new StringBuilder(declarationTok.nextToken());
/*      */             
/* 1641 */             while (declarationTok.hasMoreTokens()) {
/* 1642 */               typeInfoBuf.append(" ");
/* 1643 */               typeInfoBuf.append(declarationTok.nextToken());
/*      */             } 
/*      */             
/* 1646 */             String typeInfo = typeInfoBuf.toString();
/*      */             
/* 1648 */             typeDesc = new TypeDescriptor(typeInfo, "YES");
/*      */           } else {
/* 1650 */             throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.7"), "S1000", 
/* 1651 */                 getExceptionInterceptor());
/*      */           } 
/*      */           
/* 1654 */           if ((paramName.startsWith("`") && paramName.endsWith("`")) || (isProcedureInAnsiMode && paramName
/* 1655 */             .startsWith("\"") && paramName.endsWith("\""))) {
/* 1656 */             paramName = paramName.substring(1, paramName.length() - 1);
/*      */           }
/*      */           
/* 1659 */           if (parameterNamePattern == null || StringUtils.wildCompareIgnoreCase(paramName, parameterNamePattern)) {
/* 1660 */             Row row = convertTypeDescriptorToProcedureRow(procNameAsBytes, procCatAsBytes, paramName, isOutParam, isInParam, false, typeDesc, forGetFunctionColumns, ordinal++);
/*      */ 
/*      */             
/* 1663 */             resultRows.add(row);
/*      */           } 
/*      */         } else {
/* 1666 */           throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.8"), "S1000", 
/* 1667 */               getExceptionInterceptor());
/*      */         } 
/*      */       } 
/*      */     } 
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
/*      */   private int endPositionOfParameterDeclaration(int beginIndex, String procedureDef, String quoteChar) throws SQLException {
/* 1692 */     int currentPos = beginIndex + 1;
/* 1693 */     int parenDepth = 1;
/*      */     
/* 1695 */     while (parenDepth > 0 && currentPos < procedureDef.length()) {
/* 1696 */       int closedParenIndex = StringUtils.indexOfIgnoreCase(currentPos, procedureDef, ")", quoteChar, quoteChar, 
/* 1697 */           this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */       
/* 1699 */       if (closedParenIndex != -1) {
/* 1700 */         int nextOpenParenIndex = StringUtils.indexOfIgnoreCase(currentPos, procedureDef, "(", quoteChar, quoteChar, 
/* 1701 */             this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */         
/* 1703 */         if (nextOpenParenIndex != -1 && nextOpenParenIndex < closedParenIndex) {
/* 1704 */           parenDepth++;
/* 1705 */           currentPos = closedParenIndex + 1; continue;
/*      */         } 
/* 1707 */         parenDepth--;
/* 1708 */         currentPos = closedParenIndex;
/*      */         
/*      */         continue;
/*      */       } 
/* 1712 */       throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.5"), "S1000", 
/* 1713 */           getExceptionInterceptor());
/*      */     } 
/*      */ 
/*      */     
/* 1717 */     return currentPos;
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
/*      */   private int findEndOfReturnsClause(String procedureDefn, int positionOfReturnKeyword) throws SQLException {
/* 1738 */     String openingMarkers = this.quotedId + "(";
/* 1739 */     String closingMarkers = this.quotedId + ")";
/*      */     
/* 1741 */     String[] tokens = { "LANGUAGE", "NOT", "DETERMINISTIC", "CONTAINS", "NO", "READ", "MODIFIES", "SQL", "COMMENT", "BEGIN", "RETURN" };
/*      */     
/* 1743 */     int startLookingAt = positionOfReturnKeyword + "RETURNS".length() + 1;
/*      */     
/* 1745 */     int endOfReturn = -1;
/*      */     int i;
/* 1747 */     for (i = 0; i < tokens.length; i++) {
/* 1748 */       int nextEndOfReturn = StringUtils.indexOfIgnoreCase(startLookingAt, procedureDefn, tokens[i], openingMarkers, closingMarkers, 
/* 1749 */           this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */       
/* 1751 */       if (nextEndOfReturn != -1 && (
/* 1752 */         endOfReturn == -1 || nextEndOfReturn < endOfReturn)) {
/* 1753 */         endOfReturn = nextEndOfReturn;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1758 */     if (endOfReturn != -1) {
/* 1759 */       return endOfReturn;
/*      */     }
/*      */ 
/*      */     
/* 1763 */     endOfReturn = StringUtils.indexOfIgnoreCase(startLookingAt, procedureDefn, ":", openingMarkers, closingMarkers, 
/* 1764 */         this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */     
/* 1766 */     if (endOfReturn != -1)
/*      */     {
/* 1768 */       for (i = endOfReturn; i > 0; i--) {
/* 1769 */         if (Character.isWhitespace(procedureDefn.charAt(i))) {
/* 1770 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1777 */     throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.5"), "S1000", getExceptionInterceptor());
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
/*      */   private int getCascadeDeleteOption(String cascadeOptions) {
/* 1791 */     int onDeletePos = cascadeOptions.indexOf("ON DELETE");
/*      */     
/* 1793 */     if (onDeletePos != -1) {
/* 1794 */       String deleteOptions = cascadeOptions.substring(onDeletePos, cascadeOptions.length());
/*      */       
/* 1796 */       if (deleteOptions.startsWith("ON DELETE CASCADE"))
/* 1797 */         return 0; 
/* 1798 */       if (deleteOptions.startsWith("ON DELETE SET NULL")) {
/* 1799 */         return 2;
/*      */       }
/*      */     } 
/*      */     
/* 1803 */     return 1;
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
/*      */   private int getCascadeUpdateOption(String cascadeOptions) {
/* 1817 */     int onUpdatePos = cascadeOptions.indexOf("ON UPDATE");
/*      */     
/* 1819 */     if (onUpdatePos != -1) {
/* 1820 */       String updateOptions = cascadeOptions.substring(onUpdatePos, cascadeOptions.length());
/*      */       
/* 1822 */       if (updateOptions.startsWith("ON UPDATE CASCADE"))
/* 1823 */         return 0; 
/* 1824 */       if (updateOptions.startsWith("ON UPDATE SET NULL")) {
/* 1825 */         return 2;
/*      */       }
/*      */     } 
/*      */     
/* 1829 */     return 1;
/*      */   }
/*      */   
/*      */   protected IteratorWithCleanup<String> getDatabaseIterator(String dbSpec) throws SQLException {
/* 1833 */     if (dbSpec == null) {
/* 1834 */       return ((Boolean)this.nullDatabaseMeansCurrent.getValue()).booleanValue() ? new SingleStringIterator(this.database) : new StringListIterator(getDatabases());
/*      */     }
/* 1836 */     return new SingleStringIterator(this.pedantic ? dbSpec : StringUtils.unQuoteIdentifier(dbSpec, this.quotedId));
/*      */   }
/*      */   
/*      */   protected IteratorWithCleanup<String> getSchemaPatternIterator(String schemaPattern) throws SQLException {
/* 1840 */     if (schemaPattern == null) {
/* 1841 */       return ((Boolean)this.nullDatabaseMeansCurrent.getValue()).booleanValue() ? new SingleStringIterator(this.database) : new StringListIterator(getDatabases());
/*      */     }
/* 1843 */     return new StringListIterator(getDatabases(schemaPattern));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected List<String> getDatabases() throws SQLException {
/* 1854 */     return getDatabases(null);
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
/*      */   protected List<String> getDatabases(String dbPattern) throws SQLException {
/* 1867 */     PreparedStatement pStmt = null;
/* 1868 */     ResultSet results = null;
/* 1869 */     Statement stmt = null;
/*      */     
/*      */     try {
/* 1872 */       stmt = this.conn.getMetadataSafeStatement();
/* 1873 */       StringBuilder queryBuf = new StringBuilder("SHOW DATABASES");
/* 1874 */       if (dbPattern != null) {
/* 1875 */         queryBuf.append(" LIKE ?");
/*      */       }
/* 1877 */       pStmt = prepareMetaDataSafeStatement(queryBuf.toString());
/* 1878 */       if (dbPattern != null) {
/* 1879 */         pStmt.setString(1, dbPattern);
/*      */       }
/* 1881 */       results = pStmt.executeQuery();
/*      */       
/* 1883 */       int dbCount = 0;
/* 1884 */       if (results.last()) {
/* 1885 */         dbCount = results.getRow();
/* 1886 */         results.beforeFirst();
/*      */       } 
/*      */       
/* 1889 */       List<String> resultsAsList = new ArrayList<>(dbCount);
/* 1890 */       while (results.next()) {
/* 1891 */         resultsAsList.add(results.getString(1));
/*      */       }
/* 1893 */       Collections.sort(resultsAsList);
/*      */       
/* 1895 */       return resultsAsList;
/*      */     } finally {
/*      */       
/* 1898 */       if (results != null) {
/*      */         try {
/* 1900 */           results.close();
/* 1901 */         } catch (SQLException sqlEx) {
/* 1902 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         } 
/*      */         
/* 1905 */         results = null;
/*      */       } 
/*      */       
/* 1908 */       if (pStmt != null) {
/*      */         try {
/* 1910 */           pStmt.close();
/* 1911 */         } catch (Exception exception) {}
/*      */ 
/*      */         
/* 1914 */         pStmt = null;
/*      */       } 
/*      */       
/* 1917 */       if (stmt != null) {
/*      */         try {
/* 1919 */           stmt.close();
/* 1920 */         } catch (SQLException sqlEx) {
/* 1921 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         } 
/*      */         
/* 1924 */         stmt = null;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public ResultSet getCatalogs() throws SQLException {
/*      */     
/* 1931 */     try { List<String> resultsAsList = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? new ArrayList<>() : getDatabases();
/*      */       
/* 1933 */       Field[] fields = new Field[1];
/* 1934 */       fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
/*      */       
/* 1936 */       ArrayList<Row> tuples = new ArrayList<>(resultsAsList.size());
/* 1937 */       for (String cat : resultsAsList) {
/* 1938 */         byte[][] rowVal = new byte[1][];
/* 1939 */         rowVal[0] = s2b(cat);
/* 1940 */         tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */       } 
/*      */       
/* 1943 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, (ColumnDefinition)new DefaultColumnDefinition(fields))); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public String getCatalogSeparator() throws SQLException {
/*      */     
/* 1949 */     try { return "."; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getCatalogTerm() throws SQLException {
/*      */     
/* 1954 */     try { return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "CATALOG" : "database"; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   protected String getDatabase(String catalog, String schema) {
/* 1958 */     if (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) {
/* 1959 */       return (schema == null && ((Boolean)this.nullDatabaseMeansCurrent.getValue()).booleanValue()) ? this.database : schema;
/*      */     }
/* 1961 */     return (catalog == null && ((Boolean)this.nullDatabaseMeansCurrent.getValue()).booleanValue()) ? this.database : catalog;
/*      */   }
/*      */   
/*      */   protected Field[] getColumnPrivilegesFields() {
/* 1965 */     Field[] fields = new Field[8];
/* 1966 */     fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
/* 1967 */     fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 1);
/* 1968 */     fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
/* 1969 */     fields[3] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
/* 1970 */     fields[4] = new Field("", "GRANTOR", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 77);
/* 1971 */     fields[5] = new Field("", "GRANTEE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 77);
/* 1972 */     fields[6] = new Field("", "PRIVILEGE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
/* 1973 */     fields[7] = new Field("", "IS_GRANTABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 3);
/* 1974 */     return fields;
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
/*      */     try {
/* 1980 */       String db = getDatabase(catalog, schema);
/*      */       
/* 1982 */       StringBuilder grantQueryBuf = new StringBuilder("SELECT c.host, c.db, t.grantor, c.user, c.table_name, c.column_name, c.column_priv");
/* 1983 */       grantQueryBuf.append(" FROM mysql.columns_priv c, mysql.tables_priv t");
/* 1984 */       grantQueryBuf.append(" WHERE c.host = t.host AND c.db = t.db AND c.table_name = t.table_name");
/* 1985 */       if (db != null) {
/* 1986 */         grantQueryBuf.append(" AND c.db = ?");
/*      */       }
/* 1988 */       grantQueryBuf.append(" AND c.table_name = ?");
/* 1989 */       if (columnNamePattern != null) {
/* 1990 */         grantQueryBuf.append(" AND c.column_name LIKE ?");
/*      */       }
/*      */       
/* 1993 */       PreparedStatement pStmt = null;
/* 1994 */       ResultSet results = null;
/* 1995 */       ArrayList<Row> grantRows = new ArrayList<>();
/*      */       
/*      */       try {
/* 1998 */         pStmt = prepareMetaDataSafeStatement(grantQueryBuf.toString());
/* 1999 */         int nextId = 1;
/* 2000 */         if (db != null) {
/* 2001 */           pStmt.setString(nextId++, db);
/*      */         }
/* 2003 */         pStmt.setString(nextId++, table);
/* 2004 */         if (columnNamePattern != null) {
/* 2005 */           pStmt.setString(nextId, columnNamePattern);
/*      */         }
/*      */         
/* 2008 */         results = pStmt.executeQuery();
/*      */         
/* 2010 */         while (results.next()) {
/* 2011 */           String host = results.getString(1);
/* 2012 */           db = results.getString(2);
/* 2013 */           String grantor = results.getString(3);
/* 2014 */           String user = results.getString(4);
/*      */           
/* 2016 */           if (user == null || user.length() == 0) {
/* 2017 */             user = "%";
/*      */           }
/*      */           
/* 2020 */           StringBuilder fullUser = new StringBuilder(user);
/*      */           
/* 2022 */           if (host != null && this.useHostsInPrivileges) {
/* 2023 */             fullUser.append("@");
/* 2024 */             fullUser.append(host);
/*      */           } 
/*      */           
/* 2027 */           String columnName = results.getString(6);
/* 2028 */           String allPrivileges = results.getString(7);
/*      */           
/* 2030 */           if (allPrivileges != null) {
/* 2031 */             allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
/*      */             
/* 2033 */             StringTokenizer st = new StringTokenizer(allPrivileges, ",");
/*      */             
/* 2035 */             while (st.hasMoreTokens()) {
/* 2036 */               String privilege = st.nextToken().trim();
/* 2037 */               byte[][] tuple = new byte[8][];
/* 2038 */               tuple[0] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b("def") : s2b(db);
/* 2039 */               tuple[1] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b(db) : null;
/* 2040 */               tuple[2] = s2b(table);
/* 2041 */               tuple[3] = s2b(columnName);
/* 2042 */               tuple[4] = (grantor != null) ? s2b(grantor) : null;
/* 2043 */               tuple[5] = s2b(fullUser.toString());
/* 2044 */               tuple[6] = s2b(privilege);
/* 2045 */               tuple[7] = null;
/* 2046 */               grantRows.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } finally {
/* 2051 */         if (results != null) {
/*      */           try {
/* 2053 */             results.close();
/* 2054 */           } catch (Exception exception) {}
/*      */ 
/*      */           
/* 2057 */           results = null;
/*      */         } 
/*      */         
/* 2060 */         if (pStmt != null) {
/*      */           try {
/* 2062 */             pStmt.close();
/* 2063 */           } catch (Exception exception) {}
/*      */ 
/*      */           
/* 2066 */           pStmt = null;
/*      */         } 
/*      */       } 
/*      */       
/* 2070 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(grantRows, (ColumnDefinition)new DefaultColumnDefinition(
/* 2071 */               getColumnPrivilegesFields())));
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   public ResultSet getColumns(String catalog, final String schemaPattern, final String tableNamePattern, String columnNamePattern) throws SQLException {
/*      */     try {
/* 2078 */       String db = getDatabase(catalog, schemaPattern);
/*      */       
/* 2080 */       final String colPattern = columnNamePattern;
/*      */       
/* 2082 */       Field[] fields = createColumnsFields();
/*      */       
/* 2084 */       final ArrayList<Row> rows = new ArrayList<>();
/* 2085 */       final Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       
/* 2087 */       final boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
/*      */ 
/*      */       
/*      */       try {
/* 2091 */         (new IterateBlock<String>(dbMapsToSchema ? getSchemaPatternIterator(db) : getDatabaseIterator(db))
/*      */           {
/*      */             void forEach(String dbStr) throws SQLException
/*      */             {
/* 2095 */               ArrayList<String> tableNameList = new ArrayList<>();
/*      */               
/* 2097 */               ResultSet tables = null;
/*      */ 
/*      */               
/*      */               try {
/* 2101 */                 tables = dbMapsToSchema ? DatabaseMetaData.this.getTables(null, dbStr, tableNamePattern, new String[0]) : DatabaseMetaData.this.getTables(dbStr, schemaPattern, tableNamePattern, new String[0]);
/*      */                 
/* 2103 */                 while (tables.next()) {
/* 2104 */                   String tableNameFromList = tables.getString("TABLE_NAME");
/* 2105 */                   tableNameList.add(tableNameFromList);
/*      */                 } 
/*      */               } finally {
/* 2108 */                 if (tables != null) {
/*      */                   try {
/* 2110 */                     tables.close();
/* 2111 */                   } catch (Exception sqlEx) {
/* 2112 */                     AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                   } 
/*      */                   
/* 2115 */                   tables = null;
/*      */                 } 
/*      */               } 
/*      */               
/* 2119 */               for (String tableName : tableNameList) {
/*      */                 
/* 2121 */                 ResultSet results = null;
/*      */                 
/*      */                 try {
/* 2124 */                   StringBuilder queryBuf = new StringBuilder("SHOW FULL COLUMNS FROM ");
/* 2125 */                   queryBuf.append(StringUtils.quoteIdentifier(tableName, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/* 2126 */                   queryBuf.append(" FROM ");
/* 2127 */                   queryBuf.append(StringUtils.quoteIdentifier(dbStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/* 2128 */                   if (colPattern != null) {
/* 2129 */                     queryBuf.append(" LIKE ");
/* 2130 */                     queryBuf.append(StringUtils.quoteIdentifier(colPattern, "'", true));
/*      */                   } 
/*      */ 
/*      */ 
/*      */ 
/*      */                   
/* 2136 */                   boolean fixUpOrdinalsRequired = false;
/* 2137 */                   Map<String, Integer> ordinalFixUpMap = null;
/*      */                   
/* 2139 */                   if (colPattern != null && !colPattern.equals("%")) {
/* 2140 */                     fixUpOrdinalsRequired = true;
/*      */                     
/* 2142 */                     StringBuilder fullColumnQueryBuf = new StringBuilder("SHOW FULL COLUMNS FROM ");
/* 2143 */                     fullColumnQueryBuf
/* 2144 */                       .append(StringUtils.quoteIdentifier(tableName, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/* 2145 */                     fullColumnQueryBuf.append(" FROM ");
/* 2146 */                     fullColumnQueryBuf.append(StringUtils.quoteIdentifier(dbStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/*      */                     
/* 2148 */                     results = stmt.executeQuery(fullColumnQueryBuf.toString());
/*      */                     
/* 2150 */                     ordinalFixUpMap = new HashMap<>();
/*      */                     
/* 2152 */                     int fullOrdinalPos = 1;
/*      */                     
/* 2154 */                     while (results.next()) {
/* 2155 */                       String fullOrdColName = results.getString("Field");
/*      */                       
/* 2157 */                       ordinalFixUpMap.put(fullOrdColName, Integer.valueOf(fullOrdinalPos++));
/*      */                     } 
/* 2159 */                     results.close();
/*      */                   } 
/*      */                   
/* 2162 */                   results = stmt.executeQuery(queryBuf.toString());
/*      */                   
/* 2164 */                   int ordPos = 1;
/*      */                   
/* 2166 */                   while (results.next()) {
/* 2167 */                     DatabaseMetaData.TypeDescriptor typeDesc = new DatabaseMetaData.TypeDescriptor(results.getString("Type"), results.getString("Null"));
/*      */                     
/* 2169 */                     byte[][] rowVal = new byte[24][];
/* 2170 */                     rowVal[0] = (DatabaseMetaData.this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? DatabaseMetaData.this.s2b("def") : DatabaseMetaData.this.s2b(dbStr);
/* 2171 */                     rowVal[1] = (DatabaseMetaData.this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? DatabaseMetaData.this.s2b(dbStr) : null;
/* 2172 */                     rowVal[2] = DatabaseMetaData.this.s2b(tableName);
/* 2173 */                     rowVal[3] = results.getBytes("Field");
/* 2174 */                     rowVal[4] = Short.toString((short)typeDesc.mysqlType.getJdbcType()).getBytes();
/* 2175 */                     rowVal[5] = DatabaseMetaData.this.s2b(typeDesc.mysqlType.getName());
/* 2176 */                     if (typeDesc.columnSize == null) {
/* 2177 */                       rowVal[6] = null;
/*      */                     } else {
/* 2179 */                       String collation = results.getString("Collation");
/* 2180 */                       int mbminlen = 1;
/* 2181 */                       if (collation != null)
/*      */                       {
/* 2183 */                         if (collation.indexOf("ucs2") > -1 || collation.indexOf("utf16") > -1) {
/* 2184 */                           mbminlen = 2;
/* 2185 */                         } else if (collation.indexOf("utf32") > -1) {
/* 2186 */                           mbminlen = 4;
/*      */                         } 
/*      */                       }
/* 2189 */                       rowVal[6] = (mbminlen == 1) ? DatabaseMetaData.this.s2b(typeDesc.columnSize.toString()) : DatabaseMetaData.this
/* 2190 */                         .s2b(Integer.valueOf(typeDesc.columnSize.intValue() / mbminlen).toString());
/*      */                     } 
/* 2192 */                     rowVal[7] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.bufferLength));
/* 2193 */                     rowVal[8] = (typeDesc.decimalDigits == null) ? null : DatabaseMetaData.this.s2b(typeDesc.decimalDigits.toString());
/* 2194 */                     rowVal[9] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.numPrecRadix));
/* 2195 */                     rowVal[10] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.nullability));
/*      */ 
/*      */ 
/*      */ 
/*      */                     
/*      */                     try {
/* 2201 */                       rowVal[11] = results.getBytes("Comment");
/* 2202 */                     } catch (Exception E) {
/* 2203 */                       rowVal[11] = new byte[0];
/*      */                     } 
/*      */                     
/* 2206 */                     rowVal[12] = results.getBytes("Default");
/* 2207 */                     (new byte[1])[0] = 48; rowVal[13] = new byte[1];
/* 2208 */                     (new byte[1])[0] = 48; rowVal[14] = new byte[1];
/*      */                     
/* 2210 */                     if (StringUtils.indexOfIgnoreCase(typeDesc.mysqlType.getName(), "CHAR") != -1 || 
/* 2211 */                       StringUtils.indexOfIgnoreCase(typeDesc.mysqlType.getName(), "BLOB") != -1 || 
/* 2212 */                       StringUtils.indexOfIgnoreCase(typeDesc.mysqlType.getName(), "TEXT") != -1 || 
/* 2213 */                       StringUtils.indexOfIgnoreCase(typeDesc.mysqlType.getName(), "ENUM") != -1 || 
/* 2214 */                       StringUtils.indexOfIgnoreCase(typeDesc.mysqlType.getName(), "SET") != -1 || 
/* 2215 */                       StringUtils.indexOfIgnoreCase(typeDesc.mysqlType.getName(), "BINARY") != -1) {
/* 2216 */                       rowVal[15] = rowVal[6];
/*      */                     } else {
/* 2218 */                       rowVal[15] = null;
/*      */                     } 
/*      */ 
/*      */                     
/* 2222 */                     if (!fixUpOrdinalsRequired) {
/* 2223 */                       rowVal[16] = Integer.toString(ordPos++).getBytes();
/*      */                     } else {
/* 2225 */                       String origColName = results.getString("Field");
/* 2226 */                       Integer realOrdinal = ordinalFixUpMap.get(origColName);
/*      */                       
/* 2228 */                       if (realOrdinal != null) {
/* 2229 */                         rowVal[16] = realOrdinal.toString().getBytes();
/*      */                       } else {
/* 2231 */                         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.10"), "S1000", DatabaseMetaData.this
/* 2232 */                             .getExceptionInterceptor());
/*      */                       } 
/*      */                     } 
/*      */                     
/* 2236 */                     rowVal[17] = DatabaseMetaData.this.s2b(typeDesc.isNullable);
/*      */ 
/*      */                     
/* 2239 */                     rowVal[18] = null;
/* 2240 */                     rowVal[19] = null;
/* 2241 */                     rowVal[20] = null;
/* 2242 */                     rowVal[21] = null;
/*      */                     
/* 2244 */                     rowVal[22] = DatabaseMetaData.this.s2b("");
/*      */                     
/* 2246 */                     String extra = results.getString("Extra");
/*      */                     
/* 2248 */                     if (extra != null) {
/* 2249 */                       rowVal[22] = DatabaseMetaData.this.s2b((StringUtils.indexOfIgnoreCase(extra, "auto_increment") != -1) ? "YES" : "NO");
/* 2250 */                       rowVal[23] = DatabaseMetaData.this.s2b((StringUtils.indexOfIgnoreCase(extra, "generated") != -1) ? "YES" : "NO");
/*      */                     } 
/*      */                     
/* 2253 */                     rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                   } 
/*      */                 } finally {
/* 2256 */                   if (results != null) {
/*      */                     try {
/* 2258 */                       results.close();
/* 2259 */                     } catch (Exception exception) {}
/*      */ 
/*      */                     
/* 2262 */                     results = null;
/*      */                   } 
/*      */                 } 
/*      */               } 
/*      */             }
/* 2267 */           }).doForAll();
/*      */       } finally {
/* 2269 */         if (stmt != null) {
/* 2270 */           stmt.close();
/*      */         }
/*      */       } 
/*      */       
/* 2274 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
/*      */     } catch (CJException cJException) {
/*      */       
/* 2277 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   protected Field[] createColumnsFields() {
/* 2281 */     Field[] fields = new Field[24];
/* 2282 */     fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2283 */     fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 2284 */     fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2285 */     fields[3] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 2286 */     fields[4] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 5);
/* 2287 */     fields[5] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 16);
/* 2288 */     fields[6] = new Field("", "COLUMN_SIZE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 
/* 2289 */         Integer.toString(2147483647).length());
/* 2290 */     fields[7] = new Field("", "BUFFER_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 2291 */     fields[8] = new Field("", "DECIMAL_DIGITS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 2292 */     fields[9] = new Field("", "NUM_PREC_RADIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 2293 */     fields[10] = new Field("", "NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 2294 */     fields[11] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 2295 */     fields[12] = new Field("", "COLUMN_DEF", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 2296 */     fields[13] = new Field("", "SQL_DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 2297 */     fields[14] = new Field("", "SQL_DATETIME_SUB", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 2298 */     fields[15] = new Field("", "CHAR_OCTET_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 
/* 2299 */         Integer.toString(2147483647).length());
/* 2300 */     fields[16] = new Field("", "ORDINAL_POSITION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 2301 */     fields[17] = new Field("", "IS_NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 3);
/* 2302 */     fields[18] = new Field("", "SCOPE_CATALOG", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2303 */     fields[19] = new Field("", "SCOPE_SCHEMA", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2304 */     fields[20] = new Field("", "SCOPE_TABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2305 */     fields[21] = new Field("", "SOURCE_DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 10);
/* 2306 */     fields[22] = new Field("", "IS_AUTOINCREMENT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 3);
/* 2307 */     fields[23] = new Field("", "IS_GENERATEDCOLUMN", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 3);
/* 2308 */     return fields;
/*      */   }
/*      */   
/*      */   public Connection getConnection() throws SQLException {
/*      */     
/* 2313 */     try { return this.conn; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getCrossReference(String primaryCatalog, String primarySchema, final String primaryTable, String foreignCatalog, String foreignSchema, final String foreignTable) throws SQLException {
/*      */     try {
/* 2319 */       if (primaryTable == null) {
/* 2320 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
/* 2321 */             getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */       
/* 2325 */       String foreignDb = getDatabase(foreignCatalog, foreignSchema);
/*      */       
/* 2327 */       Field[] fields = createFkMetadataFields();
/*      */       
/* 2329 */       final ArrayList<Row> tuples = new ArrayList<>();
/*      */       
/* 2331 */       Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       
/* 2333 */       final boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
/*      */ 
/*      */       
/*      */       try {
/* 2337 */         (new IterateBlock<String>(getDatabaseIterator(foreignDb))
/*      */           {
/*      */             void forEach(String dbStr) throws SQLException
/*      */             {
/* 2341 */               ResultSet fkresults = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               try {
/* 2348 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(dbStr, null);
/*      */                 
/* 2350 */                 String foreignTableWithCase = DatabaseMetaData.this.getTableNameWithCase(foreignTable);
/* 2351 */                 String primaryTableWithCase = DatabaseMetaData.this.getTableNameWithCase(primaryTable);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 2359 */                 while (fkresults.next()) {
/* 2360 */                   String tableType = fkresults.getString("Type");
/*      */                   
/* 2362 */                   if (tableType != null && (tableType.equalsIgnoreCase("innodb") || tableType.equalsIgnoreCase("SUPPORTS_FK"))) {
/* 2363 */                     String comment = fkresults.getString("Comment").trim();
/*      */                     
/* 2365 */                     if (comment != null) {
/* 2366 */                       StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */                       
/* 2368 */                       if (commentTokens.hasMoreTokens()) {
/* 2369 */                         String str = commentTokens.nextToken();
/*      */                       }
/*      */ 
/*      */ 
/*      */                       
/* 2374 */                       while (commentTokens.hasMoreTokens()) {
/* 2375 */                         String keys = commentTokens.nextToken();
/* 2376 */                         DatabaseMetaData.LocalAndReferencedColumns parsedInfo = DatabaseMetaData.this.parseTableStatusIntoLocalAndReferencedColumns(keys);
/*      */                         
/* 2378 */                         int keySeq = 1;
/*      */                         
/* 2380 */                         Iterator<String> referencingColumns = parsedInfo.localColumnsList.iterator();
/* 2381 */                         Iterator<String> referencedColumns = parsedInfo.referencedColumnsList.iterator();
/*      */                         
/* 2383 */                         while (referencingColumns.hasNext()) {
/* 2384 */                           String referencingColumn = StringUtils.unQuoteIdentifier(referencingColumns.next(), DatabaseMetaData.this.quotedId);
/*      */                           
/* 2386 */                           String dummy = fkresults.getString("Name");
/* 2387 */                           if (dummy.compareTo(foreignTableWithCase) != 0) {
/*      */                             continue;
/*      */                           }
/*      */ 
/*      */                           
/* 2392 */                           if (parsedInfo.referencedTable.compareTo(primaryTableWithCase) != 0) {
/*      */                             continue;
/*      */                           }
/*      */ 
/*      */                           
/* 2397 */                           byte[][] tuple = new byte[14][];
/* 2398 */                           tuple[0] = dbMapsToSchema ? DatabaseMetaData.this.s2b("def") : DatabaseMetaData.this.s2b(parsedInfo.referencedDatabase);
/* 2399 */                           tuple[1] = dbMapsToSchema ? DatabaseMetaData.this.s2b(parsedInfo.referencedDatabase) : null;
/* 2400 */                           tuple[2] = DatabaseMetaData.this.s2b(parsedInfo.referencedTable);
/* 2401 */                           tuple[3] = DatabaseMetaData.this.s2b(StringUtils.unQuoteIdentifier(referencedColumns.next(), DatabaseMetaData.this.quotedId));
/* 2402 */                           tuple[4] = dbMapsToSchema ? DatabaseMetaData.this.s2b("def") : DatabaseMetaData.this.s2b(dbStr);
/* 2403 */                           tuple[5] = dbMapsToSchema ? DatabaseMetaData.this.s2b(dbStr) : null;
/* 2404 */                           tuple[6] = DatabaseMetaData.this.s2b(dummy);
/* 2405 */                           tuple[7] = DatabaseMetaData.this.s2b(referencingColumn);
/* 2406 */                           tuple[8] = Integer.toString(keySeq).getBytes();
/*      */                           
/* 2408 */                           int[] actions = DatabaseMetaData.this.getForeignKeyActions(keys);
/* 2409 */                           tuple[9] = Integer.toString(actions[1]).getBytes();
/* 2410 */                           tuple[10] = Integer.toString(actions[0]).getBytes();
/*      */                           
/* 2412 */                           tuple[11] = DatabaseMetaData.this.s2b(parsedInfo.constraintName);
/* 2413 */                           tuple[12] = null;
/* 2414 */                           tuple[13] = Integer.toString(7).getBytes();
/* 2415 */                           tuples.add(new ByteArrayRow(tuple, DatabaseMetaData.this.getExceptionInterceptor()));
/* 2416 */                           keySeq++;
/*      */                         } 
/*      */                       } 
/*      */                     } 
/*      */                   } 
/*      */                 } 
/*      */               } finally {
/*      */                 
/* 2424 */                 if (fkresults != null) {
/*      */                   try {
/* 2426 */                     fkresults.close();
/* 2427 */                   } catch (Exception sqlEx) {
/* 2428 */                     AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                   } 
/*      */                   
/* 2431 */                   fkresults = null;
/*      */                 } 
/*      */               } 
/*      */             }
/* 2435 */           }).doForAll();
/*      */       } finally {
/* 2437 */         if (stmt != null) {
/* 2438 */           stmt.close();
/*      */         }
/*      */       } 
/*      */       
/* 2442 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, (ColumnDefinition)new DefaultColumnDefinition(fields)));
/*      */     } catch (CJException cJException) {
/*      */       
/* 2445 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   protected Field[] createFkMetadataFields() {
/* 2449 */     Field[] fields = new Field[14];
/* 2450 */     fields[0] = new Field("", "PKTABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2451 */     fields[1] = new Field("", "PKTABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 2452 */     fields[2] = new Field("", "PKTABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2453 */     fields[3] = new Field("", "PKCOLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 2454 */     fields[4] = new Field("", "FKTABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2455 */     fields[5] = new Field("", "FKTABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 2456 */     fields[6] = new Field("", "FKTABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2457 */     fields[7] = new Field("", "FKCOLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 2458 */     fields[8] = new Field("", "KEY_SEQ", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 2);
/* 2459 */     fields[9] = new Field("", "UPDATE_RULE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 2);
/* 2460 */     fields[10] = new Field("", "DELETE_RULE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 2);
/* 2461 */     fields[11] = new Field("", "FK_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 2462 */     fields[12] = new Field("", "PK_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 2463 */     fields[13] = new Field("", "DEFERRABILITY", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 2);
/* 2464 */     return fields;
/*      */   }
/*      */   
/*      */   public int getDatabaseMajorVersion() throws SQLException {
/*      */     
/* 2469 */     try { return this.conn.getServerVersion().getMajor(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getDatabaseMinorVersion() throws SQLException {
/*      */     
/* 2474 */     try { return this.conn.getServerVersion().getMinor(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getDatabaseProductName() throws SQLException {
/*      */     
/* 2479 */     try { return "MySQL"; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getDatabaseProductVersion() throws SQLException {
/*      */     
/* 2484 */     try { return this.conn.getServerVersion().toString(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getDefaultTransactionIsolation() throws SQLException {
/*      */     
/* 2489 */     try { return 2; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public int getDriverMajorVersion() {
/* 2494 */     return NonRegisteringDriver.getMajorVersionInternal();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getDriverMinorVersion() {
/* 2499 */     return NonRegisteringDriver.getMinorVersionInternal();
/*      */   }
/*      */   
/*      */   public String getDriverName() throws SQLException {
/*      */     
/* 2504 */     try { return "MySQL Connector/J"; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getDriverVersion() throws SQLException {
/*      */     
/* 2509 */     try { return "mysql-connector-java-8.0.28 (Revision: 7ff2161da3899f379fb3171b6538b191b1c5c7e2)"; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public ResultSet getExportedKeys(String catalog, String schema, final String table) throws SQLException {
/*      */     try {
/* 2514 */       if (table == null) {
/* 2515 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
/* 2516 */             getExceptionInterceptor());
/*      */       }
/*      */       
/* 2519 */       Field[] fields = createFkMetadataFields();
/*      */       
/* 2521 */       final ArrayList<Row> rows = new ArrayList<>();
/*      */       
/* 2523 */       Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       
/* 2525 */       String db = getDatabase(catalog, schema);
/*      */ 
/*      */       
/*      */       try {
/* 2529 */         (new IterateBlock<String>(getDatabaseIterator(db))
/*      */           {
/*      */             void forEach(String dbStr) throws SQLException {
/* 2532 */               ResultSet fkresults = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               try {
/* 2540 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(dbStr, null);
/*      */ 
/*      */                 
/* 2543 */                 String tableNameWithCase = DatabaseMetaData.this.getTableNameWithCase(table);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 2549 */                 while (fkresults.next()) {
/* 2550 */                   String tableType = fkresults.getString("Type");
/*      */                   
/* 2552 */                   if (tableType != null && (tableType.equalsIgnoreCase("innodb") || tableType.equalsIgnoreCase("SUPPORTS_FK"))) {
/* 2553 */                     String comment = fkresults.getString("Comment").trim();
/*      */                     
/* 2555 */                     if (comment != null) {
/* 2556 */                       StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */                       
/* 2558 */                       if (commentTokens.hasMoreTokens()) {
/* 2559 */                         commentTokens.nextToken();
/*      */                         
/* 2561 */                         while (commentTokens.hasMoreTokens()) {
/* 2562 */                           String keysComment = commentTokens.nextToken();
/* 2563 */                           DatabaseMetaData.this.populateKeyResults(dbStr, tableNameWithCase, keysComment, rows, fkresults.getString("Name"), true);
/*      */                         } 
/*      */                       } 
/*      */                     } 
/*      */                   } 
/*      */                 } 
/*      */               } finally {
/*      */                 
/* 2571 */                 if (fkresults != null) {
/*      */                   try {
/* 2573 */                     fkresults.close();
/* 2574 */                   } catch (SQLException sqlEx) {
/* 2575 */                     AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                   } 
/*      */                   
/* 2578 */                   fkresults = null;
/*      */                 } 
/*      */               } 
/*      */             }
/* 2582 */           }).doForAll();
/*      */       } finally {
/* 2584 */         if (stmt != null) {
/* 2585 */           stmt.close();
/*      */         }
/*      */       } 
/*      */       
/* 2589 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
/*      */     } catch (CJException cJException) {
/*      */       
/* 2592 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   public String getExtraNameCharacters() throws SQLException {
/*      */     
/* 2597 */     try { return "#@"; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
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
/*      */   protected int[] getForeignKeyActions(String commentString) {
/* 2610 */     int[] actions = { 1, 1 };
/*      */     
/* 2612 */     int lastParenIndex = commentString.lastIndexOf(")");
/*      */     
/* 2614 */     if (lastParenIndex != commentString.length() - 1) {
/* 2615 */       String cascadeOptions = commentString.substring(lastParenIndex + 1).trim().toUpperCase(Locale.ENGLISH);
/*      */       
/* 2617 */       actions[0] = getCascadeDeleteOption(cascadeOptions);
/* 2618 */       actions[1] = getCascadeUpdateOption(cascadeOptions);
/*      */     } 
/*      */     
/* 2621 */     return actions;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getIdentifierQuoteString() throws SQLException {
/*      */     
/* 2627 */     try { return this.session.getIdentifierQuoteString(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public ResultSet getImportedKeys(String catalog, String schema, final String table) throws SQLException {
/*      */     try {
/* 2632 */       if (table == null) {
/* 2633 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
/* 2634 */             getExceptionInterceptor());
/*      */       }
/*      */       
/* 2637 */       Field[] fields = createFkMetadataFields();
/*      */       
/* 2639 */       final ArrayList<Row> rows = new ArrayList<>();
/*      */       
/* 2641 */       Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       
/* 2643 */       String db = getDatabase(catalog, schema);
/*      */ 
/*      */       
/*      */       try {
/* 2647 */         (new IterateBlock<String>(getDatabaseIterator(db))
/*      */           {
/*      */             void forEach(String dbStr) throws SQLException {
/* 2650 */               ResultSet fkresults = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               try {
/* 2658 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(dbStr, table);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 2664 */                 while (fkresults.next()) {
/* 2665 */                   String tableType = fkresults.getString("Type");
/*      */                   
/* 2667 */                   if (tableType != null && (tableType.equalsIgnoreCase("innodb") || tableType.equalsIgnoreCase("SUPPORTS_FK"))) {
/* 2668 */                     String comment = fkresults.getString("Comment").trim();
/*      */                     
/* 2670 */                     if (comment != null) {
/* 2671 */                       StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */                       
/* 2673 */                       if (commentTokens.hasMoreTokens()) {
/* 2674 */                         commentTokens.nextToken();
/*      */                         
/* 2676 */                         while (commentTokens.hasMoreTokens()) {
/* 2677 */                           String keysComment = commentTokens.nextToken();
/* 2678 */                           DatabaseMetaData.this.populateKeyResults(dbStr, table, keysComment, rows, null, false);
/*      */                         } 
/*      */                       } 
/*      */                     } 
/*      */                   } 
/*      */                 } 
/*      */               } finally {
/* 2685 */                 if (fkresults != null) {
/*      */                   try {
/* 2687 */                     fkresults.close();
/* 2688 */                   } catch (SQLException sqlEx) {
/* 2689 */                     AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                   } 
/*      */                   
/* 2692 */                   fkresults = null;
/*      */                 } 
/*      */               } 
/*      */             }
/* 2696 */           }).doForAll();
/*      */       } finally {
/* 2698 */         if (stmt != null) {
/* 2699 */           stmt.close();
/*      */         }
/*      */       } 
/*      */       
/* 2703 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
/*      */     } catch (CJException cJException) {
/*      */       
/* 2706 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getIndexInfo(String catalog, String schema, final String table, final boolean unique, boolean approximate) throws SQLException {
/*      */     
/* 2715 */     try { Field[] fields = createIndexInfoFields();
/*      */       
/* 2717 */       final SortedMap<IndexMetaDataKey, Row> sortedRows = new TreeMap<>();
/* 2718 */       ArrayList<Row> rows = new ArrayList<>();
/* 2719 */       final Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       
/* 2721 */       String db = getDatabase(catalog, schema);
/* 2722 */       final boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
/*      */ 
/*      */       
/*      */       try {
/* 2726 */         (new IterateBlock<String>(getDatabaseIterator(db))
/*      */           {
/*      */             void forEach(String dbStr) throws SQLException
/*      */             {
/* 2730 */               ResultSet results = null;
/*      */               
/*      */               try {
/* 2733 */                 StringBuilder queryBuf = new StringBuilder("SHOW INDEX FROM ");
/* 2734 */                 queryBuf.append(StringUtils.quoteIdentifier(table, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/* 2735 */                 queryBuf.append(" FROM ");
/* 2736 */                 queryBuf.append(StringUtils.quoteIdentifier(dbStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/*      */                 
/*      */                 try {
/* 2739 */                   results = stmt.executeQuery(queryBuf.toString());
/* 2740 */                 } catch (SQLException sqlEx) {
/* 2741 */                   String sqlState = sqlEx.getSQLState();
/* 2742 */                   int errorCode = sqlEx.getErrorCode();
/*      */                   
/* 2744 */                   if (!"42S02".equals(sqlState) && errorCode != 1146 && errorCode != 1049)
/*      */                   {
/* 2746 */                     throw sqlEx;
/*      */                   }
/*      */                 } 
/*      */                 
/* 2750 */                 while (results != null && results.next()) {
/* 2751 */                   byte[][] row = new byte[14][];
/* 2752 */                   row[0] = dbMapsToSchema ? DatabaseMetaData.this.s2b("def") : DatabaseMetaData.this.s2b(dbStr);
/* 2753 */                   row[1] = dbMapsToSchema ? DatabaseMetaData.this.s2b(dbStr) : null;
/* 2754 */                   row[2] = results.getBytes("Table");
/*      */                   
/* 2756 */                   boolean indexIsUnique = (results.getInt("Non_unique") == 0);
/*      */                   
/* 2758 */                   row[3] = !indexIsUnique ? DatabaseMetaData.this.s2b("true") : DatabaseMetaData.this.s2b("false");
/* 2759 */                   row[4] = null;
/* 2760 */                   row[5] = results.getBytes("Key_name");
/* 2761 */                   short indexType = 3;
/* 2762 */                   row[6] = Integer.toString(indexType).getBytes();
/* 2763 */                   row[7] = results.getBytes("Seq_in_index");
/* 2764 */                   row[8] = results.getBytes("Column_name");
/* 2765 */                   row[9] = results.getBytes("Collation");
/*      */                   
/* 2767 */                   long cardinality = results.getLong("Cardinality");
/*      */                   
/* 2769 */                   row[10] = DatabaseMetaData.this.s2b(String.valueOf(cardinality));
/* 2770 */                   row[11] = DatabaseMetaData.this.s2b("0");
/* 2771 */                   row[12] = null;
/*      */ 
/*      */                   
/* 2774 */                   DatabaseMetaData.IndexMetaDataKey indexInfoKey = new DatabaseMetaData.IndexMetaDataKey(!indexIsUnique, indexType, results.getString("Key_name").toLowerCase(), results.getShort("Seq_in_index"));
/*      */                   
/* 2776 */                   if (unique) {
/* 2777 */                     if (indexIsUnique) {
/* 2778 */                       sortedRows.put(indexInfoKey, new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                     }
/*      */                     continue;
/*      */                   } 
/* 2782 */                   sortedRows.put(indexInfoKey, new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                 } 
/*      */               } finally {
/*      */                 
/* 2786 */                 if (results != null) {
/*      */                   try {
/* 2788 */                     results.close();
/* 2789 */                   } catch (Exception exception) {}
/*      */ 
/*      */                   
/* 2792 */                   results = null;
/*      */                 } 
/*      */               } 
/*      */             }
/* 2796 */           }).doForAll();
/*      */         
/* 2798 */         Iterator<Row> sortedRowsIterator = sortedRows.values().iterator();
/* 2799 */         while (sortedRowsIterator.hasNext()) {
/* 2800 */           rows.add(sortedRowsIterator.next());
/*      */         }
/*      */         
/* 2803 */         ResultSetImpl resultSetImpl = this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
/*      */ 
/*      */         
/* 2806 */         return (ResultSet)resultSetImpl;
/*      */       } finally {
/* 2808 */         if (stmt != null)
/* 2809 */           stmt.close(); 
/*      */       }  }
/* 2811 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   protected Field[] createIndexInfoFields() {
/* 2815 */     Field[] fields = new Field[13];
/* 2816 */     fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2817 */     fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 2818 */     fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2819 */     fields[3] = new Field("", "NON_UNIQUE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BOOLEAN, 4);
/* 2820 */     fields[4] = new Field("", "INDEX_QUALIFIER", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 1);
/* 2821 */     fields[5] = new Field("", "INDEX_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 2822 */     fields[6] = new Field("", "TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 32);
/* 2823 */     fields[7] = new Field("", "ORDINAL_POSITION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
/* 2824 */     fields[8] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 2825 */     fields[9] = new Field("", "ASC_OR_DESC", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 1);
/* 2826 */     fields[10] = new Field("", "CARDINALITY", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BIGINT, 20);
/* 2827 */     fields[11] = new Field("", "PAGES", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BIGINT, 20);
/* 2828 */     fields[12] = new Field("", "FILTER_CONDITION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 2829 */     return fields;
/*      */   }
/*      */   
/*      */   public int getJDBCMajorVersion() throws SQLException {
/*      */     
/* 2834 */     try { return 4; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getJDBCMinorVersion() throws SQLException {
/*      */     
/* 2839 */     try { return 2; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxBinaryLiteralLength() throws SQLException {
/*      */     
/* 2844 */     try { return 16777208; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxCatalogNameLength() throws SQLException {
/*      */     
/* 2849 */     try { return 32; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxCharLiteralLength() throws SQLException {
/*      */     
/* 2854 */     try { return 16777208; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxColumnNameLength() throws SQLException {
/*      */     
/* 2859 */     try { return 64; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxColumnsInGroupBy() throws SQLException {
/*      */     
/* 2864 */     try { return 64; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxColumnsInIndex() throws SQLException {
/*      */     
/* 2869 */     try { return 16; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxColumnsInOrderBy() throws SQLException {
/*      */     
/* 2874 */     try { return 64; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxColumnsInSelect() throws SQLException {
/*      */     
/* 2879 */     try { return 256; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxColumnsInTable() throws SQLException {
/*      */     
/* 2884 */     try { return 512; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxConnections() throws SQLException {
/*      */     
/* 2889 */     try { return 0; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxCursorNameLength() throws SQLException {
/*      */     
/* 2894 */     try { return 64; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxIndexLength() throws SQLException {
/*      */     
/* 2899 */     try { return 256; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxProcedureNameLength() throws SQLException {
/*      */     
/* 2904 */     try { return 0; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxRowSize() throws SQLException {
/*      */     
/* 2909 */     try { return 2147483639; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxSchemaNameLength() throws SQLException {
/*      */     
/* 2914 */     try { return 0; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxStatementLength() throws SQLException {
/*      */     
/* 2919 */     try { return maxBufferSize - 4; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxStatements() throws SQLException {
/*      */     
/* 2924 */     try { return 0; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxTableNameLength() throws SQLException {
/*      */     
/* 2929 */     try { return 64; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxTablesInSelect() throws SQLException {
/*      */     
/* 2934 */     try { return 256; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getMaxUserNameLength() throws SQLException {
/*      */     
/* 2939 */     try { return 16; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getNumericFunctions() throws SQLException {
/*      */     
/* 2944 */     try { return "ABS,ACOS,ASIN,ATAN,ATAN2,BIT_COUNT,CEILING,COS,COT,DEGREES,EXP,FLOOR,LOG,LOG10,MAX,MIN,MOD,PI,POW,POWER,RADIANS,RAND,ROUND,SIN,SQRT,TAN,TRUNCATE"; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   protected Field[] getPrimaryKeysFields() {
/* 2949 */     Field[] fields = new Field[6];
/* 2950 */     fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2951 */     fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 2952 */     fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 2953 */     fields[3] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 2954 */     fields[4] = new Field("", "KEY_SEQ", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
/* 2955 */     fields[5] = new Field("", "PK_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 2956 */     return fields;
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultSet getPrimaryKeys(String catalog, String schema, final String table) throws SQLException {
/*      */     try {
/* 2962 */       if (table == null) {
/* 2963 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
/* 2964 */             getExceptionInterceptor());
/*      */       }
/*      */       
/* 2967 */       String db = getDatabase(catalog, schema);
/* 2968 */       final boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
/*      */       
/* 2970 */       final ArrayList<Row> rows = new ArrayList<>();
/* 2971 */       final Statement stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/*      */       
/*      */       try {
/* 2975 */         (new IterateBlock<String>(getDatabaseIterator(db))
/*      */           {
/*      */             void forEach(String dbStr) throws SQLException {
/* 2978 */               ResultSet rs = null;
/*      */ 
/*      */               
/*      */               try {
/* 2982 */                 StringBuilder queryBuf = new StringBuilder("SHOW KEYS FROM ");
/* 2983 */                 queryBuf.append(StringUtils.quoteIdentifier(table, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/* 2984 */                 queryBuf.append(" FROM ");
/* 2985 */                 queryBuf.append(StringUtils.quoteIdentifier(dbStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/*      */                 
/*      */                 try {
/* 2988 */                   rs = stmt.executeQuery(queryBuf.toString());
/* 2989 */                 } catch (SQLException sqlEx) {
/* 2990 */                   String sqlState = sqlEx.getSQLState();
/* 2991 */                   int errorCode = sqlEx.getErrorCode();
/*      */                   
/* 2993 */                   if (!"42S02".equals(sqlState) && errorCode != 1146 && errorCode != 1049)
/*      */                   {
/* 2995 */                     throw sqlEx;
/*      */                   }
/*      */                 } 
/*      */                 
/* 2999 */                 TreeMap<String, byte[][]> sortMap = (TreeMap)new TreeMap<>();
/*      */                 
/* 3001 */                 while (rs != null && rs.next()) {
/* 3002 */                   String keyType = rs.getString("Key_name");
/*      */                   
/* 3004 */                   if (keyType != null && (
/* 3005 */                     keyType.equalsIgnoreCase("PRIMARY") || keyType.equalsIgnoreCase("PRI"))) {
/* 3006 */                     byte[][] tuple = new byte[6][];
/* 3007 */                     tuple[0] = dbMapsToSchema ? DatabaseMetaData.this.s2b("def") : DatabaseMetaData.this.s2b(dbStr);
/* 3008 */                     tuple[1] = dbMapsToSchema ? DatabaseMetaData.this.s2b(dbStr) : null;
/* 3009 */                     tuple[2] = DatabaseMetaData.this.s2b(table);
/*      */                     
/* 3011 */                     String columnName = rs.getString("Column_name");
/* 3012 */                     tuple[3] = DatabaseMetaData.this.s2b(columnName);
/* 3013 */                     tuple[4] = DatabaseMetaData.this.s2b(rs.getString("Seq_in_index"));
/* 3014 */                     tuple[5] = DatabaseMetaData.this.s2b(keyType);
/* 3015 */                     sortMap.put(columnName, tuple);
/*      */                   } 
/*      */                 } 
/*      */ 
/*      */ 
/*      */                 
/* 3021 */                 Iterator<byte[][]> sortedIterator = (Iterator)sortMap.values().iterator();
/*      */                 
/* 3023 */                 while (sortedIterator.hasNext()) {
/* 3024 */                   rows.add(new ByteArrayRow(sortedIterator.next(), DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                 }
/*      */               } finally {
/*      */                 
/* 3028 */                 if (rs != null) {
/*      */                   try {
/* 3030 */                     rs.close();
/* 3031 */                   } catch (Exception exception) {}
/*      */ 
/*      */                   
/* 3034 */                   rs = null;
/*      */                 } 
/*      */               } 
/*      */             }
/* 3038 */           }).doForAll();
/*      */       } finally {
/* 3040 */         if (stmt != null) {
/* 3041 */           stmt.close();
/*      */         }
/*      */       } 
/*      */       
/* 3045 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(
/* 3046 */               getPrimaryKeysFields())));
/*      */     } catch (CJException cJException) {
/* 3048 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   
/*      */   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
/*      */     try {
/* 3054 */       return getProcedureOrFunctionColumns(createProcedureColumnsFields(), catalog, schemaPattern, procedureNamePattern, columnNamePattern, true, ((Boolean)this.conn
/* 3055 */           .getPropertySet().getBooleanProperty(PropertyKey.getProceduresReturnsFunctions).getValue()).booleanValue());
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  } protected Field[] createProcedureColumnsFields() {
/* 3059 */     Field[] fields = new Field[20];
/* 3060 */     fields[0] = new Field("", "PROCEDURE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
/* 3061 */     fields[1] = new Field("", "PROCEDURE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
/* 3062 */     fields[2] = new Field("", "PROCEDURE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
/* 3063 */     fields[3] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
/* 3064 */     fields[4] = new Field("", "COLUMN_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
/* 3065 */     fields[5] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6);
/* 3066 */     fields[6] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
/* 3067 */     fields[7] = new Field("", "PRECISION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12);
/* 3068 */     fields[8] = new Field("", "LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12);
/* 3069 */     fields[9] = new Field("", "SCALE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 12);
/* 3070 */     fields[10] = new Field("", "RADIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6);
/* 3071 */     fields[11] = new Field("", "NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6);
/* 3072 */     fields[12] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
/* 3073 */     fields[13] = new Field("", "COLUMN_DEF", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
/* 3074 */     fields[14] = new Field("", "SQL_DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12);
/* 3075 */     fields[15] = new Field("", "SQL_DATETIME_SUB", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12);
/* 3076 */     fields[16] = new Field("", "CHAR_OCTET_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12);
/* 3077 */     fields[17] = new Field("", "ORDINAL_POSITION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12);
/* 3078 */     fields[18] = new Field("", "IS_NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
/* 3079 */     fields[19] = new Field("", "SPECIFIC_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
/* 3080 */     return fields;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected ResultSet getProcedureOrFunctionColumns(Field[] fields, String catalog, String schemaPattern, String procedureOrFunctionNamePattern, String columnNamePattern, boolean returnProcedures, boolean returnFunctions) throws SQLException {
/* 3086 */     String db = getDatabase(catalog, schemaPattern);
/* 3087 */     boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
/*      */     
/* 3089 */     List<ComparableWrapper<String, ProcedureType>> procsOrFuncsToExtractList = new ArrayList<>();
/*      */     
/* 3091 */     ResultSet procsAndOrFuncsRs = null;
/*      */ 
/*      */     
/*      */     try {
/* 3095 */       String tmpProcedureOrFunctionNamePattern = null;
/*      */       
/* 3097 */       if (procedureOrFunctionNamePattern != null && !procedureOrFunctionNamePattern.equals("%")) {
/* 3098 */         tmpProcedureOrFunctionNamePattern = StringUtils.sanitizeProcOrFuncName(procedureOrFunctionNamePattern);
/*      */       }
/*      */ 
/*      */       
/* 3102 */       if (tmpProcedureOrFunctionNamePattern == null) {
/* 3103 */         tmpProcedureOrFunctionNamePattern = procedureOrFunctionNamePattern;
/*      */       }
/*      */       else {
/*      */         
/* 3107 */         String tmpDb = db;
/* 3108 */         List<String> parseList = StringUtils.splitDBdotName(tmpProcedureOrFunctionNamePattern, tmpDb, this.quotedId, this.session
/* 3109 */             .getServerSession().isNoBackslashEscapesSet());
/*      */ 
/*      */         
/* 3112 */         if (parseList.size() == 2) {
/* 3113 */           tmpDb = parseList.get(0);
/* 3114 */           tmpProcedureOrFunctionNamePattern = parseList.get(1);
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 3120 */       procsAndOrFuncsRs = getProceduresAndOrFunctions(createFieldMetadataForGetProcedures(), catalog, schemaPattern, tmpProcedureOrFunctionNamePattern, returnProcedures, returnFunctions);
/*      */ 
/*      */       
/* 3123 */       boolean hasResults = false;
/* 3124 */       while (procsAndOrFuncsRs.next()) {
/* 3125 */         procsOrFuncsToExtractList.add(new ComparableWrapper<>(
/* 3126 */               StringUtils.getFullyQualifiedName(dbMapsToSchema ? procsAndOrFuncsRs.getString(2) : procsAndOrFuncsRs.getString(1), procsAndOrFuncsRs
/* 3127 */                 .getString(3), this.quotedId, this.pedantic), 
/* 3128 */               (procsAndOrFuncsRs.getShort(8) == 1) ? ProcedureType.PROCEDURE : ProcedureType.FUNCTION));
/* 3129 */         hasResults = true;
/*      */       } 
/*      */ 
/*      */       
/* 3133 */       if (hasResults)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3140 */         Collections.sort(procsOrFuncsToExtractList);
/*      */       
/*      */       }
/*      */     
/*      */     }
/*      */     finally {
/*      */       
/* 3147 */       SQLException rethrowSqlEx = null;
/*      */       
/* 3149 */       if (procsAndOrFuncsRs != null) {
/*      */         try {
/* 3151 */           procsAndOrFuncsRs.close();
/* 3152 */         } catch (SQLException sqlEx) {
/* 3153 */           rethrowSqlEx = sqlEx;
/*      */         } 
/*      */       }
/*      */       
/* 3157 */       if (rethrowSqlEx != null) {
/* 3158 */         throw rethrowSqlEx;
/*      */       }
/*      */     } 
/*      */     
/* 3162 */     ArrayList<Row> resultRows = new ArrayList<>();
/* 3163 */     int idx = 0;
/* 3164 */     String procNameToCall = "";
/*      */     
/* 3166 */     for (ComparableWrapper<String, ProcedureType> procOrFunc : procsOrFuncsToExtractList) {
/* 3167 */       String procName = procOrFunc.getKey();
/* 3168 */       ProcedureType procType = procOrFunc.getValue();
/*      */ 
/*      */       
/* 3171 */       if (!" ".equals(this.quotedId)) {
/* 3172 */         idx = StringUtils.indexOfIgnoreCase(0, procName, ".", this.quotedId, this.quotedId, 
/* 3173 */             this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */       } else {
/* 3175 */         idx = procName.indexOf(".");
/*      */       } 
/*      */       
/* 3178 */       if (idx > 0) {
/* 3179 */         db = StringUtils.unQuoteIdentifier(procName.substring(0, idx), this.quotedId);
/* 3180 */         procNameToCall = procName;
/*      */       } else {
/*      */         
/* 3183 */         procNameToCall = procName;
/*      */       } 
/* 3185 */       getCallStmtParameterTypes(db, procNameToCall, procType, columnNamePattern, resultRows, (fields.length == 17));
/*      */     } 
/*      */     
/* 3188 */     return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(resultRows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
/*      */   }
/*      */   
/*      */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException
/*      */   {
/*      */     try {
/* 3194 */       return getProceduresAndOrFunctions(createFieldMetadataForGetProcedures(), catalog, schemaPattern, procedureNamePattern, true, ((Boolean)this.conn
/* 3195 */           .getPropertySet().getBooleanProperty(PropertyKey.getProceduresReturnsFunctions).getValue()).booleanValue());
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  } protected Field[] createFieldMetadataForGetProcedures() {
/* 3199 */     Field[] fields = new Field[9];
/* 3200 */     fields[0] = new Field("", "PROCEDURE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 3201 */     fields[1] = new Field("", "PROCEDURE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 3202 */     fields[2] = new Field("", "PROCEDURE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 3203 */     fields[3] = new Field("", "reserved1", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 3204 */     fields[4] = new Field("", "reserved2", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 3205 */     fields[5] = new Field("", "reserved3", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 3206 */     fields[6] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 3207 */     fields[7] = new Field("", "PROCEDURE_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6);
/* 3208 */     fields[8] = new Field("", "SPECIFIC_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/*      */     
/* 3210 */     return fields;
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
/*      */   protected ResultSet getProceduresAndOrFunctions(final Field[] fields, String catalog, String schemaPattern, final String procedureNamePattern, final boolean returnProcedures, final boolean returnFunctions) throws SQLException {
/* 3232 */     ArrayList<Row> procedureRows = new ArrayList<>();
/*      */     
/* 3234 */     String db = getDatabase(catalog, schemaPattern);
/* 3235 */     final boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
/*      */     
/* 3237 */     final List<ComparableWrapper<String, Row>> procedureRowsToSort = new ArrayList<>();
/*      */     
/* 3239 */     (new IterateBlock<String>(dbMapsToSchema ? getSchemaPatternIterator(db) : getDatabaseIterator(db))
/*      */       {
/*      */         void forEach(String dbPattern) throws SQLException
/*      */         {
/* 3243 */           ResultSet proceduresRs = null;
/*      */           
/* 3245 */           StringBuilder selectFromMySQLProcSQL = new StringBuilder();
/*      */           
/* 3247 */           selectFromMySQLProcSQL.append("SELECT db, name, type, comment FROM mysql.proc WHERE");
/* 3248 */           if (returnProcedures && !returnFunctions) {
/* 3249 */             selectFromMySQLProcSQL.append(" type = 'PROCEDURE' AND ");
/* 3250 */           } else if (!returnProcedures && returnFunctions) {
/* 3251 */             selectFromMySQLProcSQL.append(" type = 'FUNCTION' AND ");
/*      */           } 
/*      */           
/* 3254 */           selectFromMySQLProcSQL.append(dbMapsToSchema ? " db LIKE ?" : " db = ?");
/*      */           
/* 3256 */           if (procedureNamePattern != null && procedureNamePattern.length() > 0) {
/* 3257 */             selectFromMySQLProcSQL.append(" AND name LIKE ?");
/*      */           }
/*      */           
/* 3260 */           selectFromMySQLProcSQL.append(" ORDER BY name, type");
/*      */           
/* 3262 */           PreparedStatement proceduresStmt = DatabaseMetaData.this.prepareMetaDataSafeStatement(selectFromMySQLProcSQL.toString());
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/* 3267 */             if (DatabaseMetaData.this.conn.lowerCaseTableNames()) {
/* 3268 */               dbPattern = dbPattern.toLowerCase();
/*      */             }
/* 3270 */             proceduresStmt.setString(1, dbPattern);
/*      */             
/* 3272 */             if (procedureNamePattern != null && procedureNamePattern.length() > 0) {
/* 3273 */               proceduresStmt.setString(2, procedureNamePattern);
/*      */             }
/*      */             
/*      */             try {
/* 3277 */               proceduresRs = proceduresStmt.executeQuery();
/*      */               
/* 3279 */               if (returnProcedures) {
/* 3280 */                 DatabaseMetaData.this.convertToJdbcProcedureList(true, proceduresRs, procedureRowsToSort);
/*      */               }
/*      */               
/* 3283 */               if (returnFunctions) {
/* 3284 */                 DatabaseMetaData.this.convertToJdbcFunctionList(proceduresRs, procedureRowsToSort, fields);
/*      */               }
/*      */             }
/* 3287 */             catch (SQLException sqlEx) {
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 3292 */               if (returnFunctions) {
/* 3293 */                 proceduresStmt.close();
/*      */                 
/* 3295 */                 String sql = "SHOW FUNCTION STATUS WHERE " + (dbMapsToSchema ? "Db LIKE ?" : "Db = ?");
/* 3296 */                 if (procedureNamePattern != null && procedureNamePattern.length() > 0) {
/* 3297 */                   sql = sql + " AND Name LIKE ?";
/*      */                 }
/* 3299 */                 proceduresStmt = DatabaseMetaData.this.prepareMetaDataSafeStatement(sql);
/* 3300 */                 proceduresStmt.setString(1, dbPattern);
/* 3301 */                 if (procedureNamePattern != null && procedureNamePattern.length() > 0) {
/* 3302 */                   proceduresStmt.setString(2, procedureNamePattern);
/*      */                 }
/* 3304 */                 proceduresRs = proceduresStmt.executeQuery();
/*      */                 
/* 3306 */                 DatabaseMetaData.this.convertToJdbcFunctionList(proceduresRs, procedureRowsToSort, fields);
/*      */               } 
/*      */ 
/*      */               
/* 3310 */               if (returnProcedures) {
/* 3311 */                 proceduresStmt.close();
/*      */                 
/* 3313 */                 String sql = "SHOW PROCEDURE STATUS WHERE " + (dbMapsToSchema ? "Db LIKE ?" : "Db = ?");
/* 3314 */                 if (procedureNamePattern != null && procedureNamePattern.length() > 0) {
/* 3315 */                   sql = sql + " AND Name LIKE ?";
/*      */                 }
/* 3317 */                 proceduresStmt = DatabaseMetaData.this.prepareMetaDataSafeStatement(sql);
/* 3318 */                 proceduresStmt.setString(1, dbPattern);
/* 3319 */                 if (procedureNamePattern != null && procedureNamePattern.length() > 0) {
/* 3320 */                   proceduresStmt.setString(2, procedureNamePattern);
/*      */                 }
/* 3322 */                 proceduresRs = proceduresStmt.executeQuery();
/*      */                 
/* 3324 */                 DatabaseMetaData.this.convertToJdbcProcedureList(false, proceduresRs, procedureRowsToSort);
/*      */               } 
/*      */             } 
/*      */           } finally {
/*      */             
/* 3329 */             SQLException rethrowSqlEx = null;
/*      */             
/* 3331 */             if (proceduresRs != null) {
/*      */               try {
/* 3333 */                 proceduresRs.close();
/* 3334 */               } catch (SQLException sqlEx) {
/* 3335 */                 rethrowSqlEx = sqlEx;
/*      */               } 
/*      */             }
/*      */             
/* 3339 */             if (proceduresStmt != null) {
/*      */               try {
/* 3341 */                 proceduresStmt.close();
/* 3342 */               } catch (SQLException sqlEx) {
/* 3343 */                 rethrowSqlEx = sqlEx;
/*      */               } 
/*      */             }
/*      */             
/* 3347 */             if (rethrowSqlEx != null) {
/* 3348 */               throw rethrowSqlEx;
/*      */             }
/*      */           } 
/*      */         }
/* 3352 */       }).doForAll();
/*      */     
/* 3354 */     Collections.sort(procedureRowsToSort);
/* 3355 */     for (ComparableWrapper<String, Row> procRow : procedureRowsToSort) {
/* 3356 */       procedureRows.add(procRow.getValue());
/*      */     }
/*      */     
/* 3359 */     return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(procedureRows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
/*      */   }
/*      */ 
/*      */   
/*      */   public String getProcedureTerm() throws SQLException {
/*      */     
/* 3365 */     try { return "PROCEDURE"; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getResultSetHoldability() throws SQLException {
/*      */     
/* 3370 */     try { return 1; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
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
/*      */   void populateKeyResults(String db, String table, String keysComment, List<Row> resultRows, String fkTableName, boolean isExport) throws SQLException {
/* 3395 */     LocalAndReferencedColumns parsedInfo = parseTableStatusIntoLocalAndReferencedColumns(keysComment);
/*      */     
/* 3397 */     if (isExport && !parsedInfo.referencedTable.equals(table)) {
/*      */       return;
/*      */     }
/*      */     
/* 3401 */     if (parsedInfo.localColumnsList.size() != parsedInfo.referencedColumnsList.size()) {
/* 3402 */       throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.12"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/* 3405 */     Iterator<String> localColumnNames = parsedInfo.localColumnsList.iterator();
/* 3406 */     Iterator<String> referColumnNames = parsedInfo.referencedColumnsList.iterator();
/*      */     
/* 3408 */     int keySeqIndex = 1;
/*      */     
/* 3410 */     boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
/*      */     
/* 3412 */     while (localColumnNames.hasNext()) {
/* 3413 */       byte[][] tuple = new byte[14][];
/* 3414 */       String lColumnName = StringUtils.unQuoteIdentifier(localColumnNames.next(), this.quotedId);
/* 3415 */       String rColumnName = StringUtils.unQuoteIdentifier(referColumnNames.next(), this.quotedId);
/*      */       
/* 3417 */       tuple[0] = dbMapsToSchema ? s2b("def") : s2b(parsedInfo.referencedDatabase);
/* 3418 */       tuple[1] = dbMapsToSchema ? s2b(parsedInfo.referencedDatabase) : null;
/* 3419 */       tuple[2] = s2b(isExport ? table : parsedInfo.referencedTable);
/* 3420 */       tuple[3] = s2b(rColumnName);
/* 3421 */       tuple[4] = dbMapsToSchema ? s2b("def") : s2b(db);
/* 3422 */       tuple[5] = dbMapsToSchema ? s2b(db) : null;
/* 3423 */       tuple[6] = s2b(isExport ? fkTableName : table);
/* 3424 */       tuple[7] = s2b(lColumnName);
/* 3425 */       tuple[8] = s2b(Integer.toString(keySeqIndex++));
/*      */       
/* 3427 */       int[] actions = getForeignKeyActions(keysComment);
/* 3428 */       tuple[9] = s2b(Integer.toString(actions[1]));
/* 3429 */       tuple[10] = s2b(Integer.toString(actions[0]));
/*      */       
/* 3431 */       tuple[11] = s2b(parsedInfo.constraintName);
/* 3432 */       tuple[12] = null;
/* 3433 */       tuple[13] = s2b(Integer.toString(7));
/* 3434 */       resultRows.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
/*      */     } 
/*      */   }
/*      */   
/*      */   public ResultSet getSchemas() throws SQLException {
/*      */     
/* 3440 */     try { return getSchemas(null, null); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
/*      */     
/* 3445 */     try { List<String> dbList = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? getDatabases(schemaPattern) : new ArrayList<>();
/*      */       
/* 3447 */       Field[] fields = new Field[2];
/* 3448 */       fields[0] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/* 3449 */       fields[1] = new Field("", "TABLE_CATALOG", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
/*      */       
/* 3451 */       ArrayList<Row> tuples = new ArrayList<>(dbList.size());
/* 3452 */       for (String db : dbList) {
/* 3453 */         byte[][] rowVal = new byte[2][];
/* 3454 */         rowVal[0] = s2b(db);
/* 3455 */         rowVal[1] = s2b("def");
/* 3456 */         tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */       } 
/*      */       
/* 3459 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, (ColumnDefinition)new DefaultColumnDefinition(fields))); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public String getSchemaTerm() throws SQLException {
/*      */     
/* 3465 */     try { return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SCHEMA" : ""; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getSearchStringEscape() throws SQLException {
/*      */     
/* 3470 */     try { return "\\"; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSQLKeywords() throws SQLException {
/*      */     
/* 3482 */     try { if (mysqlKeywords != null) {
/* 3483 */         return mysqlKeywords;
/*      */       }
/*      */       
/* 3486 */       synchronized (DatabaseMetaData.class)
/*      */       
/* 3488 */       { if (mysqlKeywords != null) {
/* 3489 */           return mysqlKeywords;
/*      */         }
/*      */         
/* 3492 */         Set<String> mysqlKeywordSet = new TreeSet<>();
/* 3493 */         StringBuilder mysqlKeywordsBuffer = new StringBuilder();
/*      */         
/* 3495 */         Collections.addAll(mysqlKeywordSet, MYSQL_KEYWORDS);
/* 3496 */         mysqlKeywordSet.removeAll(SQL2003_KEYWORDS);
/*      */         
/* 3498 */         for (String keyword : mysqlKeywordSet) {
/* 3499 */           mysqlKeywordsBuffer.append(",").append(keyword);
/*      */         }
/*      */         
/* 3502 */         mysqlKeywords = mysqlKeywordsBuffer.substring(1);
/* 3503 */         return mysqlKeywords; }  }
/* 3504 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getSQLStateType() throws SQLException {
/*      */     
/* 3509 */     try { return 2; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getStringFunctions() throws SQLException {
/*      */     
/* 3514 */     try { return "ASCII,BIN,BIT_LENGTH,CHAR,CHARACTER_LENGTH,CHAR_LENGTH,CONCAT,CONCAT_WS,CONV,ELT,EXPORT_SET,FIELD,FIND_IN_SET,HEX,INSERT,INSTR,LCASE,LEFT,LENGTH,LOAD_FILE,LOCATE,LOCATE,LOWER,LPAD,LTRIM,MAKE_SET,MATCH,MID,OCT,OCTET_LENGTH,ORD,POSITION,QUOTE,REPEAT,REPLACE,REVERSE,RIGHT,RPAD,RTRIM,SOUNDEX,SPACE,STRCMP,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING_INDEX,TRIM,UCASE,UPPER"; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getSuperTables(String arg0, String arg1, String arg2) throws SQLException {
/*      */     
/* 3522 */     try { Field[] fields = new Field[4];
/* 3523 */       fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 3524 */       fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 3525 */       fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 3526 */       fields[3] = new Field("", "SUPERTABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/*      */       
/* 3528 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(new ArrayList(), (ColumnDefinition)new DefaultColumnDefinition(fields))); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getSuperTypes(String arg0, String arg1, String arg2) throws SQLException {
/*      */     
/* 3534 */     try { Field[] fields = new Field[6];
/* 3535 */       fields[0] = new Field("", "TYPE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 3536 */       fields[1] = new Field("", "TYPE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 3537 */       fields[2] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 3538 */       fields[3] = new Field("", "SUPERTYPE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 3539 */       fields[4] = new Field("", "SUPERTYPE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 3540 */       fields[5] = new Field("", "SUPERTYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/*      */       
/* 3542 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(new ArrayList(), (ColumnDefinition)new DefaultColumnDefinition(fields))); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public String getSystemFunctions() throws SQLException {
/*      */     
/* 3548 */     try { return "DATABASE,USER,SYSTEM_USER,SESSION_USER,PASSWORD,ENCRYPT,LAST_INSERT_ID,VERSION"; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   protected String getTableNameWithCase(String table) {
/* 3552 */     String tableNameWithCase = this.conn.lowerCaseTableNames() ? table.toLowerCase() : table;
/*      */     
/* 3554 */     return tableNameWithCase;
/*      */   }
/*      */   
/*      */   public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
/*      */     
/* 3559 */     try { Field[] fields = new Field[7];
/* 3560 */       fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
/* 3561 */       fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 1);
/* 3562 */       fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
/* 3563 */       fields[3] = new Field("", "GRANTOR", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 77);
/* 3564 */       fields[4] = new Field("", "GRANTEE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 77);
/* 3565 */       fields[5] = new Field("", "PRIVILEGE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
/* 3566 */       fields[6] = new Field("", "IS_GRANTABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 3);
/*      */       
/* 3568 */       String dbPattern = getDatabase(catalog, schemaPattern);
/*      */       
/* 3570 */       StringBuilder grantQueryBuf = new StringBuilder("SELECT host,db,table_name,grantor,user,table_priv FROM mysql.tables_priv");
/*      */       
/* 3572 */       StringBuilder conditionBuf = new StringBuilder();
/* 3573 */       if (dbPattern != null) {
/* 3574 */         conditionBuf.append((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? " db LIKE ?" : " db = ?");
/*      */       }
/* 3576 */       if (tableNamePattern != null) {
/* 3577 */         if (conditionBuf.length() > 0) {
/* 3578 */           conditionBuf.append(" AND");
/*      */         }
/* 3580 */         conditionBuf.append(" table_name LIKE ?");
/*      */       } 
/* 3582 */       if (conditionBuf.length() > 0) {
/* 3583 */         grantQueryBuf.append(" WHERE");
/* 3584 */         grantQueryBuf.append(conditionBuf);
/*      */       } 
/*      */       
/* 3587 */       ResultSet results = null;
/* 3588 */       ArrayList<Row> grantRows = new ArrayList<>();
/* 3589 */       PreparedStatement pStmt = null;
/*      */       
/*      */       try {
/* 3592 */         pStmt = prepareMetaDataSafeStatement(grantQueryBuf.toString());
/* 3593 */         int nextId = 1;
/* 3594 */         if (dbPattern != null) {
/* 3595 */           pStmt.setString(nextId++, dbPattern);
/*      */         }
/* 3597 */         if (tableNamePattern != null) {
/* 3598 */           pStmt.setString(nextId, tableNamePattern);
/*      */         }
/*      */         
/* 3601 */         results = pStmt.executeQuery();
/*      */         
/* 3603 */         while (results.next()) {
/* 3604 */           String host = results.getString(1);
/* 3605 */           String db = results.getString(2);
/* 3606 */           String table = results.getString(3);
/* 3607 */           String grantor = results.getString(4);
/* 3608 */           String user = results.getString(5);
/*      */           
/* 3610 */           if (user == null || user.length() == 0) {
/* 3611 */             user = "%";
/*      */           }
/*      */           
/* 3614 */           StringBuilder fullUser = new StringBuilder(user);
/*      */           
/* 3616 */           if (host != null && this.useHostsInPrivileges) {
/* 3617 */             fullUser.append("@");
/* 3618 */             fullUser.append(host);
/*      */           } 
/*      */           
/* 3621 */           String allPrivileges = results.getString(6);
/*      */           
/* 3623 */           if (allPrivileges != null) {
/* 3624 */             allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
/*      */             
/* 3626 */             StringTokenizer st = new StringTokenizer(allPrivileges, ",");
/*      */             
/* 3628 */             while (st.hasMoreTokens()) {
/* 3629 */               String privilege = st.nextToken().trim();
/*      */ 
/*      */               
/* 3632 */               ResultSet columnResults = null;
/*      */               
/*      */               try {
/* 3635 */                 columnResults = getColumns(catalog, schemaPattern, table, null);
/*      */                 
/* 3637 */                 while (columnResults.next()) {
/* 3638 */                   byte[][] tuple = new byte[8][];
/* 3639 */                   tuple[0] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b("def") : s2b(db);
/* 3640 */                   tuple[1] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b(db) : null;
/* 3641 */                   tuple[2] = s2b(table);
/* 3642 */                   tuple[3] = (grantor != null) ? s2b(grantor) : null;
/* 3643 */                   tuple[4] = s2b(fullUser.toString());
/* 3644 */                   tuple[5] = s2b(privilege);
/* 3645 */                   tuple[6] = null;
/* 3646 */                   grantRows.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
/*      */                 } 
/*      */               } finally {
/* 3649 */                 if (columnResults != null) {
/*      */                   try {
/* 3651 */                     columnResults.close();
/* 3652 */                   } catch (Exception exception) {}
/*      */                 }
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } finally {
/*      */         
/* 3660 */         if (results != null) {
/*      */           try {
/* 3662 */             results.close();
/* 3663 */           } catch (Exception exception) {}
/*      */ 
/*      */           
/* 3666 */           results = null;
/*      */         } 
/*      */         
/* 3669 */         if (pStmt != null) {
/*      */           try {
/* 3671 */             pStmt.close();
/* 3672 */           } catch (Exception exception) {}
/*      */ 
/*      */           
/* 3675 */           pStmt = null;
/*      */         } 
/*      */       } 
/*      */       
/* 3679 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(grantRows, (ColumnDefinition)new DefaultColumnDefinition(fields))); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, final String[] types) throws SQLException {
/*      */     try {
/* 3686 */       final SortedMap<TableMetaDataKey, Row> sortedRows = new TreeMap<>();
/* 3687 */       ArrayList<Row> tuples = new ArrayList<>();
/*      */       
/* 3689 */       final Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       
/* 3691 */       String db = getDatabase(catalog, schemaPattern);
/*      */       
/* 3693 */       final boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
/*      */       
/* 3695 */       if (tableNamePattern != null) {
/* 3696 */         List<String> parseList = StringUtils.splitDBdotName(tableNamePattern, db, this.quotedId, this.session.getServerSession().isNoBackslashEscapesSet());
/*      */         
/* 3698 */         if (parseList.size() == 2) {
/* 3699 */           tableNamePattern = parseList.get(1);
/*      */         }
/*      */       } 
/*      */       
/* 3703 */       final String tableNamePat = tableNamePattern;
/*      */       
/*      */       try {
/* 3706 */         (new IterateBlock<String>(dbMapsToSchema ? getSchemaPatternIterator(db) : getDatabaseIterator(db))
/*      */           {
/*      */             void forEach(String dbPattern) throws SQLException
/*      */             {
/* 3710 */               boolean operatingOnSystemDB = ("information_schema".equalsIgnoreCase(dbPattern) || "mysql".equalsIgnoreCase(dbPattern) || "performance_schema".equalsIgnoreCase(dbPattern));
/*      */               
/* 3712 */               ResultSet results = null;
/*      */ 
/*      */               
/*      */               try {
/*      */                 try {
/* 3717 */                   StringBuilder sqlBuf = new StringBuilder("SHOW FULL TABLES FROM ");
/* 3718 */                   sqlBuf.append(StringUtils.quoteIdentifier(dbPattern, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/* 3719 */                   if (tableNamePat != null) {
/* 3720 */                     sqlBuf.append(" LIKE ");
/* 3721 */                     sqlBuf.append(StringUtils.quoteIdentifier(tableNamePat, "'", true));
/*      */                   } 
/*      */                   
/* 3724 */                   results = stmt.executeQuery(sqlBuf.toString());
/* 3725 */                 } catch (SQLException sqlEx) {
/* 3726 */                   if ("08S01".equals(sqlEx.getSQLState())) {
/* 3727 */                     throw sqlEx;
/*      */                   }
/*      */                   
/*      */                   return;
/*      */                 } 
/*      */                 
/* 3733 */                 boolean shouldReportTables = false;
/* 3734 */                 boolean shouldReportViews = false;
/* 3735 */                 boolean shouldReportSystemTables = false;
/* 3736 */                 boolean shouldReportSystemViews = false;
/* 3737 */                 boolean shouldReportLocalTemporaries = false;
/*      */                 
/* 3739 */                 if (types == null || types.length == 0) {
/* 3740 */                   shouldReportTables = true;
/* 3741 */                   shouldReportViews = true;
/* 3742 */                   shouldReportSystemTables = true;
/* 3743 */                   shouldReportSystemViews = true;
/* 3744 */                   shouldReportLocalTemporaries = true;
/*      */                 } else {
/* 3746 */                   for (int i = 0; i < types.length; i++) {
/* 3747 */                     if (DatabaseMetaData.TableType.TABLE.equalsTo(types[i])) {
/* 3748 */                       shouldReportTables = true;
/*      */                     }
/* 3750 */                     else if (DatabaseMetaData.TableType.VIEW.equalsTo(types[i])) {
/* 3751 */                       shouldReportViews = true;
/*      */                     }
/* 3753 */                     else if (DatabaseMetaData.TableType.SYSTEM_TABLE.equalsTo(types[i])) {
/* 3754 */                       shouldReportSystemTables = true;
/*      */                     }
/* 3756 */                     else if (DatabaseMetaData.TableType.SYSTEM_VIEW.equalsTo(types[i])) {
/* 3757 */                       shouldReportSystemViews = true;
/*      */                     }
/* 3759 */                     else if (DatabaseMetaData.TableType.LOCAL_TEMPORARY.equalsTo(types[i])) {
/* 3760 */                       shouldReportLocalTemporaries = true;
/*      */                     } 
/*      */                   } 
/*      */                 } 
/*      */                 
/* 3765 */                 int typeColumnIndex = 0;
/* 3766 */                 boolean hasTableTypes = false;
/*      */ 
/*      */                 
/*      */                 try {
/* 3770 */                   typeColumnIndex = results.findColumn("table_type");
/* 3771 */                   hasTableTypes = true;
/* 3772 */                 } catch (SQLException sqlEx) {
/*      */ 
/*      */                   
/*      */                   try {
/*      */ 
/*      */                     
/* 3778 */                     typeColumnIndex = results.findColumn("Type");
/* 3779 */                     hasTableTypes = true;
/* 3780 */                   } catch (SQLException sqlEx2) {
/* 3781 */                     hasTableTypes = false;
/*      */                   } 
/*      */                 } 
/*      */                 
/* 3785 */                 while (results.next()) {
/* 3786 */                   byte[][] row = new byte[10][];
/* 3787 */                   row[0] = dbMapsToSchema ? DatabaseMetaData.this.s2b("def") : DatabaseMetaData.this.s2b(dbPattern);
/* 3788 */                   row[1] = dbMapsToSchema ? DatabaseMetaData.this.s2b(dbPattern) : null;
/* 3789 */                   row[2] = results.getBytes(1);
/* 3790 */                   row[4] = new byte[0];
/* 3791 */                   row[5] = null;
/* 3792 */                   row[6] = null;
/* 3793 */                   row[7] = null;
/* 3794 */                   row[8] = null;
/* 3795 */                   row[9] = null;
/*      */                   
/* 3797 */                   if (hasTableTypes) {
/* 3798 */                     boolean reportTable; DatabaseMetaData.TableMetaDataKey tablesKey; String tableType = results.getString(typeColumnIndex);
/*      */                     
/* 3800 */                     switch (DatabaseMetaData.TableType.getTableTypeCompliantWith(tableType)) {
/*      */                       case TABLE:
/* 3802 */                         reportTable = false;
/* 3803 */                         tablesKey = null;
/*      */                         
/* 3805 */                         if (operatingOnSystemDB && shouldReportSystemTables) {
/* 3806 */                           row[3] = DatabaseMetaData.TableType.SYSTEM_TABLE.asBytes();
/* 3807 */                           tablesKey = new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.SYSTEM_TABLE.getName(), dbPattern, null, results.getString(1));
/* 3808 */                           reportTable = true;
/*      */                         }
/* 3810 */                         else if (!operatingOnSystemDB && shouldReportTables) {
/* 3811 */                           row[3] = DatabaseMetaData.TableType.TABLE.asBytes();
/* 3812 */                           tablesKey = new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.TABLE.getName(), dbPattern, null, results.getString(1));
/* 3813 */                           reportTable = true;
/*      */                         } 
/*      */                         
/* 3816 */                         if (reportTable) {
/* 3817 */                           sortedRows.put(tablesKey, new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                         }
/*      */                         continue;
/*      */                       
/*      */                       case VIEW:
/* 3822 */                         if (shouldReportViews) {
/* 3823 */                           row[3] = DatabaseMetaData.TableType.VIEW.asBytes();
/* 3824 */                           sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.VIEW.getName(), dbPattern, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this
/* 3825 */                                 .getExceptionInterceptor()));
/*      */                         } 
/*      */                         continue;
/*      */                       
/*      */                       case SYSTEM_TABLE:
/* 3830 */                         if (shouldReportSystemTables) {
/* 3831 */                           row[3] = DatabaseMetaData.TableType.SYSTEM_TABLE.asBytes();
/* 3832 */                           sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.SYSTEM_TABLE.getName(), dbPattern, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this
/* 3833 */                                 .getExceptionInterceptor()));
/*      */                         } 
/*      */                         continue;
/*      */                       
/*      */                       case SYSTEM_VIEW:
/* 3838 */                         if (shouldReportSystemViews) {
/* 3839 */                           row[3] = DatabaseMetaData.TableType.SYSTEM_VIEW.asBytes();
/* 3840 */                           sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.SYSTEM_VIEW.getName(), dbPattern, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this
/* 3841 */                                 .getExceptionInterceptor()));
/*      */                         } 
/*      */                         continue;
/*      */                       
/*      */                       case LOCAL_TEMPORARY:
/* 3846 */                         if (shouldReportLocalTemporaries) {
/* 3847 */                           row[3] = DatabaseMetaData.TableType.LOCAL_TEMPORARY.asBytes();
/* 3848 */                           sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.LOCAL_TEMPORARY.getName(), dbPattern, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this
/* 3849 */                                 .getExceptionInterceptor()));
/*      */                         } 
/*      */                         continue;
/*      */                     } 
/*      */                     
/* 3854 */                     row[3] = DatabaseMetaData.TableType.TABLE.asBytes();
/* 3855 */                     sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.TABLE.getName(), dbPattern, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this
/* 3856 */                           .getExceptionInterceptor()));
/*      */                     
/*      */                     continue;
/*      */                   } 
/*      */                   
/* 3861 */                   if (shouldReportTables)
/*      */                   {
/* 3863 */                     row[3] = DatabaseMetaData.TableType.TABLE.asBytes();
/* 3864 */                     sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.TABLE.getName(), dbPattern, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this
/* 3865 */                           .getExceptionInterceptor()));
/*      */                   }
/*      */                 
/*      */                 } 
/*      */               } finally {
/*      */                 
/* 3871 */                 if (results != null) {
/*      */                   try {
/* 3873 */                     results.close();
/* 3874 */                   } catch (Exception exception) {}
/*      */ 
/*      */                   
/* 3877 */                   results = null;
/*      */                 } 
/*      */               } 
/*      */             }
/* 3881 */           }).doForAll();
/*      */       } finally {
/* 3883 */         if (stmt != null) {
/* 3884 */           stmt.close();
/*      */         }
/*      */       } 
/*      */       
/* 3888 */       tuples.addAll(sortedRows.values());
/* 3889 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, 
/* 3890 */             createTablesFields()));
/*      */     } catch (CJException cJException) {
/* 3892 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   protected ColumnDefinition createTablesFields() {
/* 3896 */     Field[] fields = new Field[10];
/* 3897 */     fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 255);
/* 3898 */     fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
/* 3899 */     fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 255);
/* 3900 */     fields[3] = new Field("", "TABLE_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 5);
/* 3901 */     fields[4] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
/* 3902 */     fields[5] = new Field("", "TYPE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
/* 3903 */     fields[6] = new Field("", "TYPE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
/* 3904 */     fields[7] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
/* 3905 */     fields[8] = new Field("", "SELF_REFERENCING_COL_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
/* 3906 */     fields[9] = new Field("", "REF_GENERATION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
/* 3907 */     return (ColumnDefinition)new DefaultColumnDefinition(fields);
/*      */   }
/*      */   
/*      */   public ResultSet getTableTypes() throws SQLException {
/*      */     
/* 3912 */     try { ArrayList<Row> tuples = new ArrayList<>();
/* 3913 */       Field[] fields = { new Field("", "TABLE_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 256) };
/*      */       
/* 3915 */       tuples.add(new ByteArrayRow(new byte[][] { TableType.LOCAL_TEMPORARY.asBytes() }, getExceptionInterceptor()));
/* 3916 */       tuples.add(new ByteArrayRow(new byte[][] { TableType.SYSTEM_TABLE.asBytes() }, getExceptionInterceptor()));
/* 3917 */       tuples.add(new ByteArrayRow(new byte[][] { TableType.SYSTEM_VIEW.asBytes() }, getExceptionInterceptor()));
/* 3918 */       tuples.add(new ByteArrayRow(new byte[][] { TableType.TABLE.asBytes() }, getExceptionInterceptor()));
/* 3919 */       tuples.add(new ByteArrayRow(new byte[][] { TableType.VIEW.asBytes() }, getExceptionInterceptor()));
/*      */       
/* 3921 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, (ColumnDefinition)new DefaultColumnDefinition(fields))); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public String getTimeDateFunctions() throws SQLException {
/*      */     
/* 3927 */     try { return "DAYOFWEEK,WEEKDAY,DAYOFMONTH,DAYOFYEAR,MONTH,DAYNAME,MONTHNAME,QUARTER,WEEK,YEAR,HOUR,MINUTE,SECOND,PERIOD_ADD,PERIOD_DIFF,TO_DAYS,FROM_DAYS,DATE_FORMAT,TIME_FORMAT,CURDATE,CURRENT_DATE,CURTIME,CURRENT_TIME,NOW,SYSDATE,CURRENT_TIMESTAMP,UNIX_TIMESTAMP,FROM_UNIXTIME,SEC_TO_TIME,TIME_TO_SEC"; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
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
/*      */   private byte[][] getTypeInfo(String mysqlTypeName) throws SQLException {
/* 3942 */     MysqlType mt = MysqlType.getByName(mysqlTypeName);
/* 3943 */     byte[][] rowVal = new byte[18][];
/*      */     
/* 3945 */     rowVal[0] = s2b(mysqlTypeName);
/* 3946 */     rowVal[1] = Integer.toString(mt.getJdbcType()).getBytes();
/*      */     
/* 3948 */     rowVal[2] = Integer.toString((mt.getPrecision().longValue() > 2147483647L) ? Integer.MAX_VALUE : mt.getPrecision().intValue()).getBytes();
/* 3949 */     switch (mt) {
/*      */       case LOCAL_TEMPORARY:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/*      */       case null:
/* 3971 */         rowVal[3] = s2b("'");
/* 3972 */         rowVal[4] = s2b("'");
/*      */         break;
/*      */       default:
/* 3975 */         rowVal[3] = s2b("");
/* 3976 */         rowVal[4] = s2b(""); break;
/*      */     } 
/* 3978 */     rowVal[5] = s2b(mt.getCreateParams());
/* 3979 */     rowVal[6] = Integer.toString(1).getBytes();
/* 3980 */     rowVal[7] = s2b("true");
/* 3981 */     rowVal[8] = Integer.toString(3).getBytes();
/* 3982 */     rowVal[9] = s2b(mt.isAllowed(32) ? "true" : "false");
/* 3983 */     rowVal[10] = s2b("false");
/* 3984 */     rowVal[11] = s2b("false");
/* 3985 */     rowVal[12] = s2b(mt.getName());
/* 3986 */     switch (mt)
/*      */     { case TABLE:
/*      */       case VIEW:
/*      */       case SYSTEM_TABLE:
/*      */       case SYSTEM_VIEW:
/* 3991 */         rowVal[13] = s2b("-308");
/* 3992 */         rowVal[14] = s2b("308");
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
/* 4004 */         rowVal[15] = s2b("0");
/* 4005 */         rowVal[16] = s2b("0");
/* 4006 */         rowVal[17] = s2b("10");
/*      */         
/* 4008 */         return rowVal;case null: case null: rowVal[13] = s2b("-38"); rowVal[14] = s2b("38"); rowVal[15] = s2b("0"); rowVal[16] = s2b("0"); rowVal[17] = s2b("10"); return rowVal; }  rowVal[13] = s2b("0"); rowVal[14] = s2b("0"); rowVal[15] = s2b("0"); rowVal[16] = s2b("0"); rowVal[17] = s2b("10"); return rowVal;
/*      */   }
/*      */   
/*      */   public ResultSet getTypeInfo() throws SQLException {
/*      */     
/* 4013 */     try { Field[] fields = new Field[18];
/* 4014 */       fields[0] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 4015 */       fields[1] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 5);
/* 4016 */       fields[2] = new Field("", "PRECISION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 4017 */       fields[3] = new Field("", "LITERAL_PREFIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 4);
/* 4018 */       fields[4] = new Field("", "LITERAL_SUFFIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 4);
/* 4019 */       fields[5] = new Field("", "CREATE_PARAMS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 4020 */       fields[6] = new Field("", "NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
/* 4021 */       fields[7] = new Field("", "CASE_SENSITIVE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BOOLEAN, 3);
/* 4022 */       fields[8] = new Field("", "SEARCHABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 3);
/* 4023 */       fields[9] = new Field("", "UNSIGNED_ATTRIBUTE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BOOLEAN, 3);
/* 4024 */       fields[10] = new Field("", "FIXED_PREC_SCALE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BOOLEAN, 3);
/* 4025 */       fields[11] = new Field("", "AUTO_INCREMENT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BOOLEAN, 3);
/* 4026 */       fields[12] = new Field("", "LOCAL_TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 4027 */       fields[13] = new Field("", "MINIMUM_SCALE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
/* 4028 */       fields[14] = new Field("", "MAXIMUM_SCALE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
/* 4029 */       fields[15] = new Field("", "SQL_DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 4030 */       fields[16] = new Field("", "SQL_DATETIME_SUB", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 4031 */       fields[17] = new Field("", "NUM_PREC_RADIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/*      */       
/* 4033 */       ArrayList<Row> tuples = new ArrayList<>();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 4038 */       tuples.add(new ByteArrayRow(getTypeInfo("BIT"), getExceptionInterceptor()));
/* 4039 */       tuples.add(new ByteArrayRow(getTypeInfo("BOOL"), getExceptionInterceptor()));
/* 4040 */       tuples.add(new ByteArrayRow(getTypeInfo("TINYINT"), getExceptionInterceptor()));
/* 4041 */       tuples.add(new ByteArrayRow(getTypeInfo("TINYINT UNSIGNED"), getExceptionInterceptor()));
/* 4042 */       tuples.add(new ByteArrayRow(getTypeInfo("BIGINT"), getExceptionInterceptor()));
/* 4043 */       tuples.add(new ByteArrayRow(getTypeInfo("BIGINT UNSIGNED"), getExceptionInterceptor()));
/* 4044 */       tuples.add(new ByteArrayRow(getTypeInfo("LONG VARBINARY"), getExceptionInterceptor()));
/* 4045 */       tuples.add(new ByteArrayRow(getTypeInfo("MEDIUMBLOB"), getExceptionInterceptor()));
/* 4046 */       tuples.add(new ByteArrayRow(getTypeInfo("LONGBLOB"), getExceptionInterceptor()));
/* 4047 */       tuples.add(new ByteArrayRow(getTypeInfo("BLOB"), getExceptionInterceptor()));
/* 4048 */       tuples.add(new ByteArrayRow(getTypeInfo("VARBINARY"), getExceptionInterceptor()));
/* 4049 */       tuples.add(new ByteArrayRow(getTypeInfo("TINYBLOB"), getExceptionInterceptor()));
/* 4050 */       tuples.add(new ByteArrayRow(getTypeInfo("BINARY"), getExceptionInterceptor()));
/* 4051 */       tuples.add(new ByteArrayRow(getTypeInfo("LONG VARCHAR"), getExceptionInterceptor()));
/* 4052 */       tuples.add(new ByteArrayRow(getTypeInfo("MEDIUMTEXT"), getExceptionInterceptor()));
/* 4053 */       tuples.add(new ByteArrayRow(getTypeInfo("LONGTEXT"), getExceptionInterceptor()));
/* 4054 */       tuples.add(new ByteArrayRow(getTypeInfo("TEXT"), getExceptionInterceptor()));
/* 4055 */       tuples.add(new ByteArrayRow(getTypeInfo("CHAR"), getExceptionInterceptor()));
/* 4056 */       tuples.add(new ByteArrayRow(getTypeInfo("ENUM"), getExceptionInterceptor()));
/* 4057 */       tuples.add(new ByteArrayRow(getTypeInfo("SET"), getExceptionInterceptor()));
/* 4058 */       tuples.add(new ByteArrayRow(getTypeInfo("DECIMAL"), getExceptionInterceptor()));
/* 4059 */       tuples.add(new ByteArrayRow(getTypeInfo("NUMERIC"), getExceptionInterceptor()));
/* 4060 */       tuples.add(new ByteArrayRow(getTypeInfo("INTEGER"), getExceptionInterceptor()));
/* 4061 */       tuples.add(new ByteArrayRow(getTypeInfo("INTEGER UNSIGNED"), getExceptionInterceptor()));
/* 4062 */       tuples.add(new ByteArrayRow(getTypeInfo("INT"), getExceptionInterceptor()));
/* 4063 */       tuples.add(new ByteArrayRow(getTypeInfo("INT UNSIGNED"), getExceptionInterceptor()));
/* 4064 */       tuples.add(new ByteArrayRow(getTypeInfo("MEDIUMINT"), getExceptionInterceptor()));
/* 4065 */       tuples.add(new ByteArrayRow(getTypeInfo("MEDIUMINT UNSIGNED"), getExceptionInterceptor()));
/* 4066 */       tuples.add(new ByteArrayRow(getTypeInfo("SMALLINT"), getExceptionInterceptor()));
/* 4067 */       tuples.add(new ByteArrayRow(getTypeInfo("SMALLINT UNSIGNED"), getExceptionInterceptor()));
/* 4068 */       tuples.add(new ByteArrayRow(getTypeInfo("FLOAT"), getExceptionInterceptor()));
/* 4069 */       tuples.add(new ByteArrayRow(getTypeInfo("DOUBLE"), getExceptionInterceptor()));
/* 4070 */       tuples.add(new ByteArrayRow(getTypeInfo("DOUBLE PRECISION"), getExceptionInterceptor()));
/* 4071 */       tuples.add(new ByteArrayRow(getTypeInfo("REAL"), getExceptionInterceptor()));
/* 4072 */       tuples.add(new ByteArrayRow(getTypeInfo("VARCHAR"), getExceptionInterceptor()));
/* 4073 */       tuples.add(new ByteArrayRow(getTypeInfo("TINYTEXT"), getExceptionInterceptor()));
/* 4074 */       tuples.add(new ByteArrayRow(getTypeInfo("DATE"), getExceptionInterceptor()));
/* 4075 */       tuples.add(new ByteArrayRow(getTypeInfo("YEAR"), getExceptionInterceptor()));
/* 4076 */       tuples.add(new ByteArrayRow(getTypeInfo("TIME"), getExceptionInterceptor()));
/* 4077 */       tuples.add(new ByteArrayRow(getTypeInfo("DATETIME"), getExceptionInterceptor()));
/* 4078 */       tuples.add(new ByteArrayRow(getTypeInfo("TIMESTAMP"), getExceptionInterceptor()));
/*      */ 
/*      */ 
/*      */       
/* 4082 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, (ColumnDefinition)new DefaultColumnDefinition(fields))); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
/*      */     
/* 4088 */     try { Field[] fields = new Field[7];
/* 4089 */       fields[0] = new Field("", "TYPE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 32);
/* 4090 */       fields[1] = new Field("", "TYPE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 32);
/* 4091 */       fields[2] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 32);
/* 4092 */       fields[3] = new Field("", "CLASS_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 32);
/* 4093 */       fields[4] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 4094 */       fields[5] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 32);
/* 4095 */       fields[6] = new Field("", "BASE_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 10);
/*      */       
/* 4097 */       ArrayList<Row> tuples = new ArrayList<>();
/*      */       
/* 4099 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, (ColumnDefinition)new DefaultColumnDefinition(fields))); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public String getURL() throws SQLException {
/*      */     
/* 4105 */     try { return this.conn.getURL(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getUserName() throws SQLException {
/*      */     
/* 4110 */     try { if (this.useHostsInPrivileges) {
/* 4111 */         Statement stmt = null;
/* 4112 */         ResultSet rs = null;
/*      */         
/*      */         try {
/* 4115 */           stmt = this.conn.getMetadataSafeStatement();
/*      */           
/* 4117 */           rs = stmt.executeQuery("SELECT USER()");
/* 4118 */           rs.next();
/*      */           
/* 4120 */           return rs.getString(1);
/*      */         } finally {
/* 4122 */           if (rs != null) {
/*      */             try {
/* 4124 */               rs.close();
/* 4125 */             } catch (Exception ex) {
/* 4126 */               AssertionFailedException.shouldNotHappen(ex);
/*      */             } 
/*      */             
/* 4129 */             rs = null;
/*      */           } 
/*      */           
/* 4132 */           if (stmt != null) {
/*      */             try {
/* 4134 */               stmt.close();
/* 4135 */             } catch (Exception ex) {
/* 4136 */               AssertionFailedException.shouldNotHappen(ex);
/*      */             } 
/*      */             
/* 4139 */             stmt = null;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 4144 */       return this.conn.getUser(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   protected Field[] getVersionColumnsFields() {
/* 4148 */     Field[] fields = new Field[8];
/* 4149 */     fields[0] = new Field("", "SCOPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
/* 4150 */     fields[1] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
/* 4151 */     fields[2] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 5);
/* 4152 */     fields[3] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 16);
/* 4153 */     fields[4] = new Field("", "COLUMN_SIZE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 16);
/* 4154 */     fields[5] = new Field("", "BUFFER_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 16);
/* 4155 */     fields[6] = new Field("", "DECIMAL_DIGITS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 16);
/* 4156 */     fields[7] = new Field("", "PSEUDO_COLUMN", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
/* 4157 */     return fields;
/*      */   }
/*      */   
/*      */   public ResultSet getVersionColumns(String catalog, String schema, final String table) throws SQLException
/*      */   {
/*      */     try {
/* 4163 */       if (table == null) {
/* 4164 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
/* 4165 */             getExceptionInterceptor());
/*      */       }
/*      */       
/* 4168 */       final ArrayList<Row> rows = new ArrayList<>();
/*      */       
/* 4170 */       final Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       
/* 4172 */       String db = getDatabase(catalog, schema);
/*      */ 
/*      */       
/*      */       try {
/* 4176 */         (new IterateBlock<String>(getDatabaseIterator(db))
/*      */           {
/*      */             void forEach(String dbStr) throws SQLException
/*      */             {
/* 4180 */               ResultSet results = null;
/*      */               
/*      */               try {
/* 4183 */                 StringBuilder whereBuf = new StringBuilder(" Extra LIKE '%on update CURRENT_TIMESTAMP%'");
/* 4184 */                 List<String> rsFields = new ArrayList<>();
/*      */                 
/* 4186 */                 if (whereBuf.length() > 0 || rsFields.size() > 0) {
/* 4187 */                   StringBuilder queryBuf = new StringBuilder("SHOW COLUMNS FROM ");
/* 4188 */                   queryBuf.append(StringUtils.quoteIdentifier(table, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/* 4189 */                   queryBuf.append(" FROM ");
/* 4190 */                   queryBuf.append(StringUtils.quoteIdentifier(dbStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
/* 4191 */                   queryBuf.append(" WHERE");
/* 4192 */                   queryBuf.append(whereBuf.toString());
/*      */                   
/*      */                   try {
/* 4195 */                     results = stmt.executeQuery(queryBuf.toString());
/* 4196 */                   } catch (SQLException sqlEx) {
/* 4197 */                     String sqlState = sqlEx.getSQLState();
/* 4198 */                     int errorCode = sqlEx.getErrorCode();
/*      */                     
/* 4200 */                     if (!"42S02".equals(sqlState) && errorCode != 1146 && errorCode != 1049)
/*      */                     {
/* 4202 */                       throw sqlEx;
/*      */                     }
/*      */                   } 
/*      */                   
/* 4206 */                   while (results != null && results.next()) {
/* 4207 */                     DatabaseMetaData.TypeDescriptor typeDesc = new DatabaseMetaData.TypeDescriptor(results.getString("Type"), results.getString("Null"));
/* 4208 */                     byte[][] rowVal = new byte[8][];
/* 4209 */                     rowVal[0] = null;
/* 4210 */                     rowVal[1] = results.getBytes("Field");
/* 4211 */                     rowVal[2] = Short.toString((short)typeDesc.mysqlType.getJdbcType()).getBytes();
/* 4212 */                     rowVal[3] = DatabaseMetaData.this.s2b(typeDesc.mysqlType.getName());
/* 4213 */                     rowVal[4] = (typeDesc.columnSize == null) ? null : DatabaseMetaData.this.s2b(typeDesc.columnSize.toString());
/* 4214 */                     rowVal[5] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.bufferLength));
/* 4215 */                     rowVal[6] = (typeDesc.decimalDigits == null) ? null : DatabaseMetaData.this.s2b(typeDesc.decimalDigits.toString());
/* 4216 */                     rowVal[7] = Integer.toString(1).getBytes();
/* 4217 */                     rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                   } 
/*      */                 } 
/* 4220 */               } catch (SQLException sqlEx) {
/* 4221 */                 if (!"42S02".equals(sqlEx.getSQLState())) {
/* 4222 */                   throw sqlEx;
/*      */                 }
/*      */               } finally {
/* 4225 */                 if (results != null) {
/*      */                   try {
/* 4227 */                     results.close();
/* 4228 */                   } catch (Exception exception) {}
/*      */ 
/*      */                   
/* 4231 */                   results = null;
/*      */                 }
/*      */               
/*      */               } 
/*      */             }
/* 4236 */           }).doForAll();
/*      */       } finally {
/* 4238 */         if (stmt != null) {
/* 4239 */           stmt.close();
/*      */         }
/*      */       } 
/*      */       
/* 4243 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(
/* 4244 */               getVersionColumnsFields())));
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  } public boolean insertsAreDetected(int type) throws SQLException {
/*      */     
/* 4249 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isCatalogAtStart() throws SQLException {
/*      */     
/* 4254 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isReadOnly() throws SQLException {
/*      */     
/* 4259 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean locatorsUpdateCopy() throws SQLException {
/*      */     
/* 4264 */     try { return !((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.emulateLocators).getValue()).booleanValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean nullPlusNonNullIsNull() throws SQLException {
/*      */     
/* 4270 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean nullsAreSortedAtEnd() throws SQLException {
/*      */     
/* 4275 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean nullsAreSortedAtStart() throws SQLException {
/*      */     
/* 4280 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean nullsAreSortedHigh() throws SQLException {
/*      */     
/* 4285 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean nullsAreSortedLow() throws SQLException {
/*      */     
/* 4290 */     try { return !nullsAreSortedHigh(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean othersDeletesAreVisible(int type) throws SQLException {
/*      */     
/* 4295 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean othersInsertsAreVisible(int type) throws SQLException {
/*      */     
/* 4300 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean othersUpdatesAreVisible(int type) throws SQLException {
/*      */     
/* 4305 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean ownDeletesAreVisible(int type) throws SQLException {
/*      */     
/* 4310 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean ownInsertsAreVisible(int type) throws SQLException {
/*      */     
/* 4315 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean ownUpdatesAreVisible(int type) throws SQLException {
/*      */     
/* 4320 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
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
/*      */   protected LocalAndReferencedColumns parseTableStatusIntoLocalAndReferencedColumns(String keysComment) throws SQLException {
/* 4335 */     String columnsDelimitter = ",";
/*      */     
/* 4337 */     int indexOfOpenParenLocalColumns = StringUtils.indexOfIgnoreCase(0, keysComment, "(", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */ 
/*      */     
/* 4340 */     if (indexOfOpenParenLocalColumns == -1) {
/* 4341 */       throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.14"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/* 4344 */     String constraintName = StringUtils.unQuoteIdentifier(keysComment.substring(0, indexOfOpenParenLocalColumns).trim(), this.quotedId);
/* 4345 */     keysComment = keysComment.substring(indexOfOpenParenLocalColumns, keysComment.length());
/*      */     
/* 4347 */     String keysCommentTrimmed = keysComment.trim();
/*      */     
/* 4349 */     int indexOfCloseParenLocalColumns = StringUtils.indexOfIgnoreCase(0, keysCommentTrimmed, ")", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */ 
/*      */     
/* 4352 */     if (indexOfCloseParenLocalColumns == -1) {
/* 4353 */       throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.15"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/* 4356 */     String localColumnNamesString = keysCommentTrimmed.substring(1, indexOfCloseParenLocalColumns);
/*      */     
/* 4358 */     int indexOfRefer = StringUtils.indexOfIgnoreCase(0, keysCommentTrimmed, "REFER ", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */     
/* 4360 */     if (indexOfRefer == -1) {
/* 4361 */       throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.16"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/* 4364 */     int indexOfOpenParenReferCol = StringUtils.indexOfIgnoreCase(indexOfRefer, keysCommentTrimmed, "(", this.quotedId, this.quotedId, SearchMode.__MRK_COM_MYM_HNT_WS);
/*      */ 
/*      */     
/* 4367 */     if (indexOfOpenParenReferCol == -1) {
/* 4368 */       throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.17"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/* 4371 */     String referDbTableString = keysCommentTrimmed.substring(indexOfRefer + "REFER ".length(), indexOfOpenParenReferCol);
/*      */     
/* 4373 */     int indexOfSlash = StringUtils.indexOfIgnoreCase(0, referDbTableString, "/", this.quotedId, this.quotedId, SearchMode.__MRK_COM_MYM_HNT_WS);
/*      */     
/* 4375 */     if (indexOfSlash == -1) {
/* 4376 */       throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.18"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/* 4379 */     String referDb = StringUtils.unQuoteIdentifier(referDbTableString.substring(0, indexOfSlash), this.quotedId);
/* 4380 */     String referTable = StringUtils.unQuoteIdentifier(referDbTableString.substring(indexOfSlash + 1).trim(), this.quotedId);
/*      */     
/* 4382 */     int indexOfCloseParenRefer = StringUtils.indexOfIgnoreCase(indexOfOpenParenReferCol, keysCommentTrimmed, ")", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*      */ 
/*      */     
/* 4385 */     if (indexOfCloseParenRefer == -1) {
/* 4386 */       throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.19"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/* 4389 */     String referColumnNamesString = keysCommentTrimmed.substring(indexOfOpenParenReferCol + 1, indexOfCloseParenRefer);
/*      */     
/* 4391 */     List<String> referColumnsList = StringUtils.split(referColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
/* 4392 */     List<String> localColumnsList = StringUtils.split(localColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
/*      */     
/* 4394 */     return new LocalAndReferencedColumns(localColumnsList, referColumnsList, constraintName, referDb, referTable);
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
/*      */   protected byte[] s2b(String s) throws SQLException {
/* 4408 */     if (s == null) {
/* 4409 */       return null;
/*      */     }
/*      */     
/*      */     try {
/* 4413 */       return StringUtils.getBytes(s, this.conn.getCharacterSetMetadata());
/* 4414 */     } catch (CJException e) {
/* 4415 */       throw SQLExceptionsMapping.translateException(e, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean storesLowerCaseIdentifiers() throws SQLException {
/*      */     
/* 4421 */     try { return this.conn.storesLowerCaseTableName(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
/*      */     
/* 4427 */     try { return this.conn.storesLowerCaseTableName(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean storesMixedCaseIdentifiers() throws SQLException {
/*      */     
/* 4432 */     try { return !this.conn.storesLowerCaseTableName(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
/*      */     
/* 4438 */     try { return !this.conn.storesLowerCaseTableName(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean storesUpperCaseIdentifiers() throws SQLException {
/*      */     
/* 4443 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
/*      */     
/* 4449 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsAlterTableWithAddColumn() throws SQLException {
/*      */     
/* 4454 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsAlterTableWithDropColumn() throws SQLException {
/*      */     
/* 4459 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsANSI92EntryLevelSQL() throws SQLException {
/*      */     
/* 4465 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsANSI92FullSQL() throws SQLException {
/*      */     
/* 4470 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsANSI92IntermediateSQL() throws SQLException {
/*      */     
/* 4475 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsBatchUpdates() throws SQLException {
/*      */     
/* 4480 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsCatalogsInDataManipulation() throws SQLException {
/*      */     
/* 4485 */     try { return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.CATALOG); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
/*      */     
/* 4490 */     try { return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.CATALOG); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
/*      */     
/* 4495 */     try { return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.CATALOG); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsCatalogsInProcedureCalls() throws SQLException {
/*      */     
/* 4500 */     try { return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.CATALOG); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsCatalogsInTableDefinitions() throws SQLException {
/*      */     
/* 4505 */     try { return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.CATALOG); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsColumnAliasing() throws SQLException {
/*      */     
/* 4511 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsConvert() throws SQLException {
/*      */     
/* 4517 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsConvert(int fromType, int toType) throws SQLException {
/*      */     
/* 4522 */     try { return MysqlType.supportsConvert(fromType, toType); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsCoreSQLGrammar() throws SQLException {
/*      */     
/* 4527 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsCorrelatedSubqueries() throws SQLException {
/*      */     
/* 4533 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
/*      */     
/* 4538 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
/*      */     
/* 4543 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsDifferentTableCorrelationNames() throws SQLException {
/*      */     
/* 4549 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsExpressionsInOrderBy() throws SQLException {
/*      */     
/* 4554 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsExtendedSQLGrammar() throws SQLException {
/*      */     
/* 4559 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsFullOuterJoins() throws SQLException {
/*      */     
/* 4564 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsGetGeneratedKeys() {
/*      */     
/* 4569 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsGroupBy() throws SQLException {
/*      */     
/* 4574 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsGroupByBeyondSelect() throws SQLException {
/*      */     
/* 4579 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsGroupByUnrelated() throws SQLException {
/*      */     
/* 4584 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsIntegrityEnhancementFacility() throws SQLException {
/*      */     
/* 4589 */     try { if (!((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.overrideSupportsIntegrityEnhancementFacility).getValue()).booleanValue()) {
/* 4590 */         return false;
/*      */       }
/*      */       
/* 4593 */       return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsLikeEscapeClause() throws SQLException {
/*      */     
/* 4599 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsLimitedOuterJoins() throws SQLException {
/*      */     
/* 4604 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsMinimumSQLGrammar() throws SQLException {
/*      */     
/* 4610 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsMixedCaseIdentifiers() throws SQLException {
/*      */     
/* 4615 */     try { return !this.conn.lowerCaseTableNames(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
/*      */     
/* 4621 */     try { return !this.conn.lowerCaseTableNames(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsMultipleOpenResults() throws SQLException {
/*      */     
/* 4626 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsMultipleResultSets() throws SQLException {
/*      */     
/* 4631 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsMultipleTransactions() throws SQLException {
/*      */     
/* 4636 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsNamedParameters() throws SQLException {
/*      */     
/* 4641 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsNonNullableColumns() throws SQLException {
/*      */     
/* 4647 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
/*      */     
/* 4652 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
/*      */     
/* 4657 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
/*      */     
/* 4662 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
/*      */     
/* 4667 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsOrderByUnrelated() throws SQLException {
/*      */     
/* 4672 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsOuterJoins() throws SQLException {
/*      */     
/* 4677 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsPositionedDelete() throws SQLException {
/*      */     
/* 4682 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsPositionedUpdate() throws SQLException {
/*      */     
/* 4687 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
/*      */     
/* 4692 */     try { if ((type == 1003 || type == 1004) && (concurrency == 1007 || concurrency == 1008))
/*      */       {
/* 4694 */         return true; } 
/* 4695 */       if (type == 1005) {
/* 4696 */         return false;
/*      */       }
/* 4698 */       throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.20"), "S1009", getExceptionInterceptor()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsResultSetHoldability(int holdability) throws SQLException {
/*      */     
/* 4703 */     try { return (holdability == 1); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsResultSetType(int type) throws SQLException {
/*      */     
/* 4708 */     try { return (type == 1003 || type == 1004); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsSavepoints() throws SQLException {
/*      */     
/* 4713 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsSchemasInDataManipulation() throws SQLException {
/*      */     
/* 4718 */     try { return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsSchemasInIndexDefinitions() throws SQLException {
/*      */     
/* 4723 */     try { return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
/*      */     
/* 4728 */     try { return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsSchemasInProcedureCalls() throws SQLException {
/*      */     
/* 4733 */     try { return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsSchemasInTableDefinitions() throws SQLException {
/*      */     
/* 4738 */     try { return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsSelectForUpdate() throws SQLException {
/*      */     
/* 4743 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsStatementPooling() throws SQLException {
/*      */     
/* 4748 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsStoredProcedures() throws SQLException {
/*      */     
/* 4753 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsSubqueriesInComparisons() throws SQLException {
/*      */     
/* 4759 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsSubqueriesInExists() throws SQLException {
/*      */     
/* 4765 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsSubqueriesInIns() throws SQLException {
/*      */     
/* 4771 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsSubqueriesInQuantifieds() throws SQLException {
/*      */     
/* 4777 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsTableCorrelationNames() throws SQLException {
/*      */     
/* 4783 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
/*      */     
/* 4788 */     try { switch (level) {
/*      */         case 1:
/*      */         case 2:
/*      */         case 4:
/*      */         case 8:
/* 4793 */           return true;
/*      */       } 
/*      */       
/* 4796 */       return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsTransactions() throws SQLException {
/*      */     
/* 4802 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsUnion() throws SQLException {
/*      */     
/* 4808 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean supportsUnionAll() throws SQLException {
/*      */     
/* 4814 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean updatesAreDetected(int type) throws SQLException {
/*      */     
/* 4819 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean usesLocalFilePerTable() throws SQLException {
/*      */     
/* 4824 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean usesLocalFiles() throws SQLException {
/*      */     
/* 4829 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultSet getClientInfoProperties() throws SQLException {
/*      */     
/* 4836 */     try { Field[] fields = new Field[4];
/* 4837 */       fields[0] = new Field("", "NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 255);
/* 4838 */       fields[1] = new Field("", "MAX_LEN", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
/* 4839 */       fields[2] = new Field("", "DEFAULT_VALUE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 255);
/* 4840 */       fields[3] = new Field("", "DESCRIPTION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 255);
/*      */       
/* 4842 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(new ArrayList(), (ColumnDefinition)new DefaultColumnDefinition(fields))); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
/*      */     
/* 4848 */     try { return getProcedureOrFunctionColumns(createFunctionColumnsFields(), catalog, schemaPattern, functionNamePattern, columnNamePattern, false, true); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   protected Field[] createFunctionColumnsFields() {
/* 4852 */     Field[] fields = { new Field("", "FUNCTION_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "FUNCTION_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "FUNCTION_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "COLUMN_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 64), new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6), new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 64), new Field("", "PRECISION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "SCALE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 12), new Field("", "RADIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6), new Field("", "NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6), new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "CHAR_OCTET_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32), new Field("", "ORDINAL_POSITION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32), new Field("", "IS_NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 12), new Field("", "SPECIFIC_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 64) };
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
/* 4869 */     return fields;
/*      */   }
/*      */   
/*      */   protected Field[] getFunctionsFields() {
/* 4873 */     Field[] fields = new Field[6];
/* 4874 */     fields[0] = new Field("", "FUNCTION_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 4875 */     fields[1] = new Field("", "FUNCTION_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 4876 */     fields[2] = new Field("", "FUNCTION_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 4877 */     fields[3] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 4878 */     fields[4] = new Field("", "FUNCTION_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6);
/* 4879 */     fields[5] = new Field("", "SPECIFIC_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
/* 4880 */     return fields;
/*      */   }
/*      */   
/*      */   public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
/*      */     
/* 4885 */     try { return getProceduresAndOrFunctions(getFunctionsFields(), catalog, schemaPattern, functionNamePattern, false, true); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean providesQueryObjectGenerator() throws SQLException {
/* 4889 */     return false;
/*      */   }
/*      */   
/*      */   public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
/*      */     
/* 4894 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
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
/*      */   protected PreparedStatement prepareMetaDataSafeStatement(String sql) throws SQLException {
/* 4908 */     PreparedStatement pStmt = this.conn.clientPrepareStatement(sql, 1004, 1007);
/*      */     
/* 4910 */     if (pStmt.getMaxRows() != 0) {
/* 4911 */       pStmt.setMaxRows(0);
/*      */     }
/*      */     
/* 4914 */     ((JdbcStatement)pStmt).setHoldResultsOpenOverClose(true);
/*      */     
/* 4916 */     return pStmt;
/*      */   }
/*      */   
/*      */   public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
/*      */     
/* 4921 */     try { Field[] fields = { new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "COLUMN_SIZE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "DECIMAL_DIGITS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "NUM_PREC_RADIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "COLUMN_USAGE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "CHAR_OCTET_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "IS_NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512) };
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
/* 4934 */       return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(new ArrayList(), (ColumnDefinition)new DefaultColumnDefinition(fields))); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean generatedKeyAlwaysReturned() throws SQLException {
/*      */     
/* 4940 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*      */     try {
/*      */       try {
/* 4948 */         return iface.cast(this);
/* 4949 */       } catch (ClassCastException cce) {
/* 4950 */         throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", this.conn
/* 4951 */             .getExceptionInterceptor());
/*      */       } 
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*      */     
/* 4958 */     try { return iface.isInstance(this); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public RowIdLifetime getRowIdLifetime() throws SQLException {
/*      */     
/* 4963 */     try { return RowIdLifetime.ROWID_UNSUPPORTED; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
/*      */     
/* 4968 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getMetadataEncoding() {
/* 4972 */     return this.metadataEncoding;
/*      */   }
/*      */   
/*      */   public void setMetadataEncoding(String metadataEncoding) {
/* 4976 */     this.metadataEncoding = metadataEncoding;
/*      */   }
/*      */   
/*      */   public int getMetadataCollationIndex() {
/* 4980 */     return this.metadataCollationIndex;
/*      */   }
/*      */   
/*      */   public void setMetadataCollationIndex(int metadataCollationIndex) {
/* 4984 */     this.metadataCollationIndex = metadataCollationIndex;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\DatabaseMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */