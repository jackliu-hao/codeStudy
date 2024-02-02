/*     */ package org.h2.table;
/*     */ 
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.index.IndexType;
/*     */ import org.h2.index.LinkedIndex;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.jdbc.JdbcResultSet;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TableLink
/*     */   extends Table
/*     */ {
/*     */   private static final int MAX_RETRY = 2;
/*     */   private static final long ROW_COUNT_APPROXIMATION = 100000L;
/*     */   private final String originalSchema;
/*     */   private String driver;
/*     */   private String url;
/*     */   private String user;
/*     */   private String password;
/*     */   private String originalTable;
/*     */   private String qualifiedTableName;
/*     */   private TableLinkConnection conn;
/*  56 */   private HashMap<String, PreparedStatement> preparedMap = new HashMap<>();
/*  57 */   private final ArrayList<Index> indexes = Utils.newSmallArrayList();
/*     */   private final boolean emitUpdates;
/*     */   private LinkedIndex linkedIndex;
/*     */   private DbException connectException;
/*     */   private boolean storesLowerCase;
/*     */   private boolean storesMixedCase;
/*     */   private boolean storesMixedCaseQuoted;
/*     */   private boolean supportsMixedCaseIdentifiers;
/*     */   private boolean globalTemporary;
/*     */   private boolean readOnly;
/*     */   private final boolean targetsMySql;
/*  68 */   private int fetchSize = 0;
/*     */   
/*     */   private boolean autocommit = true;
/*     */ 
/*     */   
/*     */   public TableLink(Schema paramSchema, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, boolean paramBoolean1, boolean paramBoolean2) {
/*  74 */     super(paramSchema, paramInt, paramString1, false, true);
/*  75 */     this.driver = paramString2;
/*  76 */     this.url = paramString3;
/*  77 */     this.user = paramString4;
/*  78 */     this.password = paramString5;
/*  79 */     this.originalSchema = paramString6;
/*  80 */     this.originalTable = paramString7;
/*  81 */     this.emitUpdates = paramBoolean1;
/*  82 */     this.targetsMySql = isMySqlUrl(this.url);
/*     */     try {
/*  84 */       connect();
/*  85 */     } catch (DbException dbException) {
/*  86 */       if (!paramBoolean2) {
/*  87 */         throw dbException;
/*     */       }
/*  89 */       Column[] arrayOfColumn = new Column[0];
/*  90 */       setColumns(arrayOfColumn);
/*  91 */       this.linkedIndex = new LinkedIndex(this, paramInt, IndexColumn.wrap(arrayOfColumn), 0, IndexType.createNonUnique(false));
/*  92 */       this.indexes.add(this.linkedIndex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void connect() {
/*  97 */     this.connectException = null;
/*  98 */     for (byte b = 0;; b++) {
/*     */       try {
/* 100 */         this.conn = this.database.getLinkConnection(this.driver, this.url, this.user, this.password);
/* 101 */         this.conn.setAutoCommit(this.autocommit);
/* 102 */         synchronized (this.conn) {
/*     */           
/* 104 */           readMetaData();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           return;
/*     */         } 
/* 113 */       } catch (DbException dbException) {
/* 114 */         if (b >= 2) {
/* 115 */           this.connectException = dbException;
/* 116 */           throw dbException;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void readMetaData() throws SQLException {
/* 123 */     DatabaseMetaData databaseMetaData = this.conn.getConnection().getMetaData();
/* 124 */     this.storesLowerCase = databaseMetaData.storesLowerCaseIdentifiers();
/* 125 */     this.storesMixedCase = databaseMetaData.storesMixedCaseIdentifiers();
/* 126 */     this.storesMixedCaseQuoted = databaseMetaData.storesMixedCaseQuotedIdentifiers();
/* 127 */     this.supportsMixedCaseIdentifiers = databaseMetaData.supportsMixedCaseIdentifiers();
/* 128 */     ArrayList<Column> arrayList = Utils.newSmallArrayList();
/* 129 */     HashMap<Object, Object> hashMap = new HashMap<>();
/* 130 */     String str = null;
/* 131 */     boolean bool = this.originalTable.startsWith("(");
/* 132 */     if (!bool) {
/* 133 */       try (ResultSet null = databaseMetaData.getTables(null, this.originalSchema, this.originalTable, null)) {
/* 134 */         if (resultSet.next() && resultSet.next()) {
/* 135 */           throw DbException.get(90080, this.originalTable);
/*     */         }
/*     */       } 
/* 138 */       try (ResultSet null = databaseMetaData.getColumns(null, this.originalSchema, this.originalTable, null)) {
/* 139 */         byte b = 0;
/* 140 */         String str1 = null;
/* 141 */         while (resultSet.next()) {
/* 142 */           String str2 = resultSet.getString("TABLE_CAT");
/* 143 */           if (str1 == null) {
/* 144 */             str1 = str2;
/*     */           }
/* 146 */           String str3 = resultSet.getString("TABLE_SCHEM");
/* 147 */           if (str == null) {
/* 148 */             str = str3;
/*     */           }
/* 150 */           if (!Objects.equals(str1, str2) || 
/* 151 */             !Objects.equals(str, str3)) {
/*     */ 
/*     */             
/* 154 */             hashMap.clear();
/* 155 */             arrayList.clear();
/*     */             break;
/*     */           } 
/* 158 */           String str4 = resultSet.getString("COLUMN_NAME");
/* 159 */           str4 = convertColumnName(str4);
/* 160 */           int j = resultSet.getInt("DATA_TYPE");
/* 161 */           String str5 = resultSet.getString("TYPE_NAME");
/* 162 */           long l = resultSet.getInt("COLUMN_SIZE");
/* 163 */           l = convertPrecision(j, l);
/* 164 */           int k = resultSet.getInt("DECIMAL_DIGITS");
/* 165 */           k = convertScale(j, k);
/* 166 */           int m = DataType.convertSQLTypeToValueType(j, str5);
/* 167 */           Column column = new Column(str4, TypeInfo.getTypeInfo(m, l, k, null), this, b++);
/* 168 */           arrayList.add(column);
/* 169 */           hashMap.put(str4, column);
/*     */         } 
/*     */       } 
/*     */     } 
/* 173 */     if (this.originalTable.indexOf('.') < 0 && !StringUtils.isNullOrEmpty(str)) {
/* 174 */       this.qualifiedTableName = str + '.' + this.originalTable;
/*     */     } else {
/* 176 */       this.qualifiedTableName = this.originalTable;
/*     */     } 
/*     */ 
/*     */     
/* 180 */     try(Statement null = this.conn.getConnection().createStatement(); 
/* 181 */         ResultSet null = statement.executeQuery("SELECT * FROM " + this.qualifiedTableName + " T WHERE 1=0")) {
/* 182 */       if (resultSet instanceof JdbcResultSet) {
/* 183 */         ResultInterface resultInterface = ((JdbcResultSet)resultSet).getResult();
/* 184 */         arrayList.clear();
/* 185 */         hashMap.clear(); byte b; int j;
/* 186 */         for (b = 0, j = resultInterface.getVisibleColumnCount(); b < j; ) {
/* 187 */           String str1 = resultInterface.getColumnName(b);
/* 188 */           Column column = new Column(str1, resultInterface.getColumnType(b), this, ++b);
/* 189 */           arrayList.add(column);
/* 190 */           hashMap.put(str1, column);
/*     */         } 
/* 192 */       } else if (arrayList.isEmpty()) {
/*     */         
/* 194 */         ResultSetMetaData resultSetMetaData = resultSet.getMetaData(); byte b; int j;
/* 195 */         for (b = 0, j = resultSetMetaData.getColumnCount(); b < j; ) {
/* 196 */           String str1 = resultSetMetaData.getColumnName(b + 1);
/* 197 */           str1 = convertColumnName(str1);
/* 198 */           int k = resultSetMetaData.getColumnType(b + 1);
/* 199 */           long l = resultSetMetaData.getPrecision(b + 1);
/* 200 */           l = convertPrecision(k, l);
/* 201 */           int m = resultSetMetaData.getScale(b + 1);
/* 202 */           m = convertScale(k, m);
/* 203 */           int n = DataType.getValueTypeFromResultSet(resultSetMetaData, b + 1);
/* 204 */           Column column = new Column(str1, TypeInfo.getTypeInfo(n, l, m, null), this, b++);
/* 205 */           arrayList.add(column);
/* 206 */           hashMap.put(str1, column);
/*     */         } 
/*     */       } 
/* 209 */     } catch (Exception exception) {
/* 210 */       throw DbException.get(42102, exception, new String[] { this.originalTable + '(' + exception + ')' });
/*     */     } 
/*     */     
/* 213 */     Column[] arrayOfColumn = arrayList.<Column>toArray(new Column[0]);
/* 214 */     setColumns(arrayOfColumn);
/* 215 */     int i = getId();
/* 216 */     this.linkedIndex = new LinkedIndex(this, i, IndexColumn.wrap(arrayOfColumn), 0, IndexType.createNonUnique(false));
/* 217 */     this.indexes.add(this.linkedIndex);
/* 218 */     if (!bool) {
/* 219 */       readIndexes(databaseMetaData, (HashMap)hashMap);
/*     */     }
/*     */   }
/*     */   
/*     */   private void readIndexes(DatabaseMetaData paramDatabaseMetaData, HashMap<String, Column> paramHashMap) {
/* 224 */     String str = null;
/* 225 */     try (ResultSet null = paramDatabaseMetaData.getPrimaryKeys(null, this.originalSchema, this.originalTable)) {
/* 226 */       if (resultSet.next()) {
/* 227 */         str = readPrimaryKey(resultSet, paramHashMap);
/*     */       }
/* 229 */     } catch (Exception exception) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 234 */     try (ResultSet null = paramDatabaseMetaData.getIndexInfo(null, this.originalSchema, this.originalTable, false, true)) {
/* 235 */       readIndexes(resultSet, paramHashMap, str);
/* 236 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String readPrimaryKey(ResultSet paramResultSet, HashMap<String, Column> paramHashMap) throws SQLException {
/* 243 */     String str = null;
/*     */     
/* 245 */     ArrayList<Column> arrayList = Utils.newSmallArrayList();
/*     */     while (true) {
/* 247 */       int i = paramResultSet.getInt("KEY_SEQ");
/* 248 */       if (StringUtils.isNullOrEmpty(str)) {
/* 249 */         str = paramResultSet.getString("PK_NAME");
/*     */       }
/* 251 */       while (arrayList.size() < i) {
/* 252 */         arrayList.add(null);
/*     */       }
/* 254 */       String str1 = paramResultSet.getString("COLUMN_NAME");
/* 255 */       str1 = convertColumnName(str1);
/* 256 */       Column column = paramHashMap.get(str1);
/* 257 */       if (i == 0) {
/*     */         
/* 259 */         arrayList.add(column);
/*     */       } else {
/* 261 */         arrayList.set(i - 1, column);
/*     */       } 
/* 263 */       if (!paramResultSet.next()) {
/* 264 */         addIndex(arrayList, arrayList.size(), IndexType.createPrimaryKey(false, false));
/* 265 */         return str;
/*     */       } 
/*     */     } 
/*     */   } private void readIndexes(ResultSet paramResultSet, HashMap<String, Column> paramHashMap, String paramString) throws SQLException {
/* 269 */     String str = null;
/* 270 */     ArrayList<Column> arrayList = Utils.newSmallArrayList();
/* 271 */     byte b = 0;
/* 272 */     IndexType indexType = null;
/* 273 */     while (paramResultSet.next()) {
/* 274 */       if (paramResultSet.getShort("TYPE") == 0) {
/*     */         continue;
/*     */       }
/*     */       
/* 278 */       String str1 = paramResultSet.getString("INDEX_NAME");
/* 279 */       if (paramString != null && paramString.equals(str1)) {
/*     */         continue;
/*     */       }
/* 282 */       if (str != null && !str.equals(str1)) {
/* 283 */         addIndex(arrayList, b, indexType);
/* 284 */         b = 0;
/* 285 */         str = null;
/*     */       } 
/* 287 */       if (str == null) {
/* 288 */         str = str1;
/* 289 */         arrayList.clear();
/*     */       } 
/* 291 */       if (!paramResultSet.getBoolean("NON_UNIQUE")) {
/* 292 */         b++;
/*     */       }
/*     */       
/* 295 */       indexType = (b > 0) ? IndexType.createUnique(false, false) : IndexType.createNonUnique(false);
/* 296 */       String str2 = paramResultSet.getString("COLUMN_NAME");
/* 297 */       str2 = convertColumnName(str2);
/* 298 */       Column column = paramHashMap.get(str2);
/* 299 */       arrayList.add(column);
/*     */     } 
/* 301 */     if (str != null) {
/* 302 */       addIndex(arrayList, b, indexType);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static long convertPrecision(int paramInt, long paramLong) {
/* 310 */     switch (paramInt) {
/*     */       case 2:
/*     */       case 3:
/* 313 */         if (paramLong == 0L) {
/* 314 */           paramLong = 65535L;
/*     */         }
/*     */         break;
/*     */       case 91:
/* 318 */         paramLong = Math.max(10L, paramLong);
/*     */         break;
/*     */       case 93:
/* 321 */         paramLong = Math.max(29L, paramLong);
/*     */         break;
/*     */       case 92:
/* 324 */         paramLong = Math.max(18L, paramLong);
/*     */         break;
/*     */     } 
/* 327 */     return paramLong;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static int convertScale(int paramInt1, int paramInt2) {
/* 333 */     switch (paramInt1) {
/*     */       case 2:
/*     */       case 3:
/* 336 */         if (paramInt2 < 0) {
/* 337 */           paramInt2 = 32767;
/*     */         }
/*     */         break;
/*     */     } 
/* 341 */     return paramInt2;
/*     */   }
/*     */   
/*     */   private String convertColumnName(String paramString) {
/* 345 */     if (this.targetsMySql) {
/*     */       
/* 347 */       paramString = StringUtils.toUpperEnglish(paramString);
/* 348 */     } else if ((this.storesMixedCase || this.storesLowerCase) && paramString
/* 349 */       .equals(StringUtils.toLowerEnglish(paramString))) {
/* 350 */       paramString = StringUtils.toUpperEnglish(paramString);
/* 351 */     } else if (this.storesMixedCase && !this.supportsMixedCaseIdentifiers) {
/*     */       
/* 353 */       paramString = StringUtils.toUpperEnglish(paramString);
/* 354 */     } else if (this.storesMixedCase && this.storesMixedCaseQuoted) {
/*     */       
/* 356 */       paramString = StringUtils.toUpperEnglish(paramString);
/*     */     } 
/* 358 */     return paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void addIndex(List<Column> paramList, int paramInt, IndexType paramIndexType) {
/* 364 */     int i = paramList.indexOf(null);
/* 365 */     if (i == 0) {
/* 366 */       this.trace.info("Omitting linked index - no recognized columns."); return;
/*     */     } 
/* 368 */     if (i > 0) {
/* 369 */       this.trace.info("Unrecognized columns in linked index. Registering the index against the leading {0} recognized columns of {1} total columns.", new Object[] {
/*     */             
/* 371 */             Integer.valueOf(i), Integer.valueOf(paramList.size()) });
/* 372 */       paramList = paramList.subList(0, i);
/*     */     } 
/* 374 */     Column[] arrayOfColumn = paramList.<Column>toArray(new Column[0]);
/* 375 */     LinkedIndex linkedIndex = new LinkedIndex(this, 0, IndexColumn.wrap(arrayOfColumn), paramInt, paramIndexType);
/* 376 */     this.indexes.add(linkedIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDropSQL() {
/* 381 */     StringBuilder stringBuilder = new StringBuilder("DROP TABLE IF EXISTS ");
/* 382 */     return getSQL(stringBuilder, 0).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/* 387 */     StringBuilder stringBuilder = new StringBuilder("CREATE FORCE ");
/* 388 */     if (isTemporary()) {
/* 389 */       if (this.globalTemporary) {
/* 390 */         stringBuilder.append("GLOBAL ");
/*     */       } else {
/* 392 */         stringBuilder.append("LOCAL ");
/*     */       } 
/* 394 */       stringBuilder.append("TEMPORARY ");
/*     */     } 
/* 396 */     stringBuilder.append("LINKED TABLE ");
/* 397 */     getSQL(stringBuilder, 0);
/* 398 */     if (this.comment != null) {
/* 399 */       stringBuilder.append(" COMMENT ");
/* 400 */       StringUtils.quoteStringSQL(stringBuilder, this.comment);
/*     */     } 
/* 402 */     stringBuilder.append('(');
/* 403 */     StringUtils.quoteStringSQL(stringBuilder, this.driver).append(", ");
/* 404 */     StringUtils.quoteStringSQL(stringBuilder, this.url).append(", ");
/* 405 */     StringUtils.quoteStringSQL(stringBuilder, this.user).append(", ");
/* 406 */     StringUtils.quoteStringSQL(stringBuilder, this.password).append(", ");
/* 407 */     StringUtils.quoteStringSQL(stringBuilder, this.originalTable).append(')');
/* 408 */     if (this.emitUpdates) {
/* 409 */       stringBuilder.append(" EMIT UPDATES");
/*     */     }
/* 411 */     if (this.readOnly) {
/* 412 */       stringBuilder.append(" READONLY");
/*     */     }
/* 414 */     if (this.fetchSize != 0) {
/* 415 */       stringBuilder.append(" FETCH_SIZE ").append(this.fetchSize);
/*     */     }
/* 417 */     if (!this.autocommit) {
/* 418 */       stringBuilder.append(" AUTOCOMMIT OFF");
/*     */     }
/* 420 */     stringBuilder.append(" /*").append("--hide--").append("*/");
/* 421 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Index addIndex(SessionLocal paramSessionLocal, String paramString1, int paramInt1, IndexColumn[] paramArrayOfIndexColumn, int paramInt2, IndexType paramIndexType, boolean paramBoolean, String paramString2) {
/* 427 */     throw DbException.getUnsupportedException("LINK");
/*     */   }
/*     */ 
/*     */   
/*     */   public Index getScanIndex(SessionLocal paramSessionLocal) {
/* 432 */     return (Index)this.linkedIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInsertable() {
/* 437 */     return !this.readOnly;
/*     */   }
/*     */   
/*     */   private void checkReadOnly() {
/* 441 */     if (this.readOnly) {
/* 442 */       throw DbException.get(90097);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 448 */     checkReadOnly();
/* 449 */     getScanIndex(paramSessionLocal).remove(paramSessionLocal, paramRow);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 454 */     checkReadOnly();
/* 455 */     getScanIndex(paramSessionLocal).add(paramSessionLocal, paramRow);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close(SessionLocal paramSessionLocal) {
/* 460 */     if (this.conn != null) {
/*     */       try {
/* 462 */         this.conn.close(false);
/*     */       } finally {
/* 464 */         this.conn = null;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized long getRowCount(SessionLocal paramSessionLocal) {
/* 472 */     String str = "SELECT COUNT(*) FROM " + this.qualifiedTableName + " as foo";
/*     */     try {
/* 474 */       PreparedStatement preparedStatement = execute(str, (ArrayList<Value>)null, false, paramSessionLocal);
/* 475 */       ResultSet resultSet = preparedStatement.getResultSet();
/* 476 */       resultSet.next();
/* 477 */       long l = resultSet.getLong(1);
/* 478 */       resultSet.close();
/* 479 */       reusePreparedStatement(preparedStatement, str);
/* 480 */       return l;
/* 481 */     } catch (Exception exception) {
/* 482 */       throw wrapException(str, exception);
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
/*     */   public static DbException wrapException(String paramString, Exception paramException) {
/* 494 */     SQLException sQLException = DbException.toSQLException(paramException);
/* 495 */     return DbException.get(90111, sQLException, new String[] { paramString, sQLException
/* 496 */           .toString() });
/*     */   }
/*     */   
/*     */   public String getQualifiedTable() {
/* 500 */     return this.qualifiedTableName;
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
/*     */   public PreparedStatement execute(String paramString, ArrayList<Value> paramArrayList, boolean paramBoolean, SessionLocal paramSessionLocal) {
/* 515 */     if (this.conn == null) {
/* 516 */       throw this.connectException;
/*     */     }
/* 518 */     for (byte b = 0;; b++) {
/*     */       try {
/* 520 */         synchronized (this.conn) {
/* 521 */           PreparedStatement preparedStatement = this.preparedMap.remove(paramString);
/* 522 */           if (preparedStatement == null) {
/* 523 */             preparedStatement = this.conn.getConnection().prepareStatement(paramString);
/* 524 */             if (this.fetchSize != 0) {
/* 525 */               preparedStatement.setFetchSize(this.fetchSize);
/*     */             }
/*     */           } 
/* 528 */           if (this.trace.isDebugEnabled()) {
/* 529 */             StringBuilder stringBuilder = (new StringBuilder(getName())).append(":\n").append(paramString);
/* 530 */             if (paramArrayList != null && !paramArrayList.isEmpty()) {
/* 531 */               stringBuilder.append(" {"); byte b1; int i;
/* 532 */               for (b1 = 0, i = paramArrayList.size(); b1 < i; ) {
/* 533 */                 Value value = paramArrayList.get(b1);
/* 534 */                 if (b1 > 0) {
/* 535 */                   stringBuilder.append(", ");
/*     */                 }
/* 537 */                 stringBuilder.append(++b1).append(": ");
/* 538 */                 value.getSQL(stringBuilder, 0);
/*     */               } 
/* 540 */               stringBuilder.append('}');
/*     */             } 
/* 542 */             stringBuilder.append(';');
/* 543 */             this.trace.debug(stringBuilder.toString());
/*     */           } 
/* 545 */           if (paramArrayList != null) {
/* 546 */             JdbcConnection jdbcConnection = paramSessionLocal.createConnection(false); byte b1; int i;
/* 547 */             for (b1 = 0, i = paramArrayList.size(); b1 < i; b1++) {
/* 548 */               Value value = paramArrayList.get(b1);
/* 549 */               JdbcUtils.set(preparedStatement, b1 + 1, value, jdbcConnection);
/*     */             } 
/*     */           } 
/* 552 */           preparedStatement.execute();
/* 553 */           if (paramBoolean) {
/* 554 */             reusePreparedStatement(preparedStatement, paramString);
/* 555 */             return null;
/*     */           } 
/* 557 */           return preparedStatement;
/*     */         } 
/* 559 */       } catch (SQLException sQLException) {
/* 560 */         if (b >= 2) {
/* 561 */           throw DbException.convert(sQLException);
/*     */         }
/* 563 */         this.conn.close(true);
/* 564 */         connect();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkSupportAlter() {
/* 571 */     throw DbException.getUnsupportedException("LINK");
/*     */   }
/*     */ 
/*     */   
/*     */   public long truncate(SessionLocal paramSessionLocal) {
/* 576 */     throw DbException.getUnsupportedException("LINK");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canGetRowCount(SessionLocal paramSessionLocal) {
/* 581 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canDrop() {
/* 586 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public TableType getTableType() {
/* 591 */     return TableType.TABLE_LINK;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 596 */     super.removeChildrenAndResources(paramSessionLocal);
/* 597 */     close(paramSessionLocal);
/* 598 */     this.database.removeMeta(paramSessionLocal, getId());
/* 599 */     this.driver = null;
/* 600 */     this.url = this.user = this.password = this.originalTable = null;
/* 601 */     this.preparedMap = null;
/* 602 */     invalidate();
/*     */   }
/*     */   
/*     */   public boolean isOracle() {
/* 606 */     return this.url.startsWith("jdbc:oracle:");
/*     */   }
/*     */   
/*     */   private static boolean isMySqlUrl(String paramString) {
/* 610 */     return (paramString.startsWith("jdbc:mysql:") || paramString
/* 611 */       .startsWith("jdbc:mariadb:"));
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<Index> getIndexes() {
/* 616 */     return this.indexes;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxDataModificationId() {
/* 622 */     return Long.MAX_VALUE;
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateRows(Prepared paramPrepared, SessionLocal paramSessionLocal, LocalResult paramLocalResult) {
/* 627 */     checkReadOnly();
/* 628 */     if (this.emitUpdates) {
/* 629 */       while (paramLocalResult.next()) {
/* 630 */         paramPrepared.checkCanceled();
/* 631 */         Row row1 = paramLocalResult.currentRowForTable();
/* 632 */         paramLocalResult.next();
/* 633 */         Row row2 = paramLocalResult.currentRowForTable();
/* 634 */         this.linkedIndex.update(row1, row2, paramSessionLocal);
/*     */       } 
/*     */     } else {
/* 637 */       super.updateRows(paramPrepared, paramSessionLocal, paramLocalResult);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setGlobalTemporary(boolean paramBoolean) {
/* 642 */     this.globalTemporary = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setReadOnly(boolean paramBoolean) {
/* 646 */     this.readOnly = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 651 */     return 100000L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reusePreparedStatement(PreparedStatement paramPreparedStatement, String paramString) {
/* 661 */     synchronized (this.conn) {
/* 662 */       this.preparedMap.put(paramString, paramPreparedStatement);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDeterministic() {
/* 668 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkWritingAllowed() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void convertInsertRow(SessionLocal paramSessionLocal, Row paramRow, Boolean paramBoolean) {
/* 682 */     convertRow(paramSessionLocal, paramRow);
/*     */   }
/*     */ 
/*     */   
/*     */   public void convertUpdateRow(SessionLocal paramSessionLocal, Row paramRow, boolean paramBoolean) {
/* 687 */     convertRow(paramSessionLocal, paramRow);
/*     */   }
/*     */   
/*     */   private void convertRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 691 */     for (byte b = 0; b < this.columns.length; b++) {
/* 692 */       Value value = paramRow.getValue(b);
/* 693 */       if (value != null) {
/*     */         
/* 695 */         Column column = this.columns[b];
/* 696 */         Value value1 = column.validateConvertUpdateSequence(paramSessionLocal, value, paramRow);
/* 697 */         if (value1 != value) {
/* 698 */           paramRow.setValue(b, value1);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFetchSize(int paramInt) {
/* 710 */     this.fetchSize = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAutoCommit(boolean paramBoolean) {
/* 719 */     this.autocommit = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getAutocommit() {
/* 727 */     return this.autocommit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFetchSize() {
/* 737 */     return this.fetchSize;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\TableLink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */