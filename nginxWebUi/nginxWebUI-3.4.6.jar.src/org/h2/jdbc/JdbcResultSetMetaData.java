/*     */ package org.h2.jdbc;
/*     */ 
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.Trace;
/*     */ import org.h2.message.TraceObject;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.ValueToObjectConverter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JdbcResultSetMetaData
/*     */   extends TraceObject
/*     */   implements ResultSetMetaData
/*     */ {
/*     */   private final String catalog;
/*     */   private final JdbcResultSet rs;
/*     */   private final JdbcPreparedStatement prep;
/*     */   private final ResultInterface result;
/*     */   private final int columnCount;
/*     */   
/*     */   JdbcResultSetMetaData(JdbcResultSet paramJdbcResultSet, JdbcPreparedStatement paramJdbcPreparedStatement, ResultInterface paramResultInterface, String paramString, Trace paramTrace, int paramInt) {
/*  31 */     setTrace(paramTrace, 5, paramInt);
/*  32 */     this.catalog = paramString;
/*  33 */     this.rs = paramJdbcResultSet;
/*  34 */     this.prep = paramJdbcPreparedStatement;
/*  35 */     this.result = paramResultInterface;
/*  36 */     this.columnCount = paramResultInterface.getVisibleColumnCount();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getColumnCount() throws SQLException {
/*     */     try {
/*  48 */       debugCodeCall("getColumnCount");
/*  49 */       checkClosed();
/*  50 */       return this.columnCount;
/*  51 */     } catch (Exception exception) {
/*  52 */       throw logAndConvert(exception);
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
/*     */   
/*     */   public String getColumnLabel(int paramInt) throws SQLException {
/*     */     try {
/*  66 */       return this.result.getAlias(getColumn("getColumnLabel", paramInt));
/*  67 */     } catch (Exception exception) {
/*  68 */       throw logAndConvert(exception);
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
/*     */   
/*     */   public String getColumnName(int paramInt) throws SQLException {
/*     */     try {
/*  82 */       return this.result.getColumnName(getColumn("getColumnName", paramInt));
/*  83 */     } catch (Exception exception) {
/*  84 */       throw logAndConvert(exception);
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
/*     */ 
/*     */   
/*     */   public int getColumnType(int paramInt) throws SQLException {
/*     */     try {
/*  99 */       return DataType.convertTypeToSQLType(this.result.getColumnType(getColumn("getColumnType", paramInt)));
/* 100 */     } catch (Exception exception) {
/* 101 */       throw logAndConvert(exception);
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
/*     */   
/*     */   public String getColumnTypeName(int paramInt) throws SQLException {
/*     */     try {
/* 115 */       return this.result.getColumnType(getColumn("getColumnTypeName", paramInt)).getDeclaredTypeName();
/* 116 */     } catch (Exception exception) {
/* 117 */       throw logAndConvert(exception);
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
/*     */   
/*     */   public String getSchemaName(int paramInt) throws SQLException {
/*     */     try {
/* 131 */       String str = this.result.getSchemaName(getColumn("getSchemaName", paramInt));
/* 132 */       return (str == null) ? "" : str;
/* 133 */     } catch (Exception exception) {
/* 134 */       throw logAndConvert(exception);
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
/*     */   
/*     */   public String getTableName(int paramInt) throws SQLException {
/*     */     try {
/* 148 */       String str = this.result.getTableName(getColumn("getTableName", paramInt));
/* 149 */       return (str == null) ? "" : str;
/* 150 */     } catch (Exception exception) {
/* 151 */       throw logAndConvert(exception);
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
/*     */   
/*     */   public String getCatalogName(int paramInt) throws SQLException {
/*     */     try {
/* 165 */       getColumn("getCatalogName", paramInt);
/* 166 */       return (this.catalog == null) ? "" : this.catalog;
/* 167 */     } catch (Exception exception) {
/* 168 */       throw logAndConvert(exception);
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
/*     */   
/*     */   public boolean isAutoIncrement(int paramInt) throws SQLException {
/*     */     try {
/* 182 */       return this.result.isIdentity(getColumn("isAutoIncrement", paramInt));
/* 183 */     } catch (Exception exception) {
/* 184 */       throw logAndConvert(exception);
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
/*     */ 
/*     */   
/*     */   public boolean isCaseSensitive(int paramInt) throws SQLException {
/*     */     try {
/* 199 */       getColumn("isCaseSensitive", paramInt);
/* 200 */       return true;
/* 201 */     } catch (Exception exception) {
/* 202 */       throw logAndConvert(exception);
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
/*     */ 
/*     */   
/*     */   public boolean isSearchable(int paramInt) throws SQLException {
/*     */     try {
/* 217 */       getColumn("isSearchable", paramInt);
/* 218 */       return true;
/* 219 */     } catch (Exception exception) {
/* 220 */       throw logAndConvert(exception);
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
/*     */ 
/*     */   
/*     */   public boolean isCurrency(int paramInt) throws SQLException {
/*     */     try {
/* 235 */       getColumn("isCurrency", paramInt);
/* 236 */       return false;
/* 237 */     } catch (Exception exception) {
/* 238 */       throw logAndConvert(exception);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int isNullable(int paramInt) throws SQLException {
/*     */     try {
/* 256 */       return this.result.getNullable(getColumn("isNullable", paramInt));
/* 257 */     } catch (Exception exception) {
/* 258 */       throw logAndConvert(exception);
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
/*     */ 
/*     */   
/*     */   public boolean isSigned(int paramInt) throws SQLException {
/*     */     try {
/* 273 */       return DataType.isNumericType(this.result.getColumnType(getColumn("isSigned", paramInt)).getValueType());
/* 274 */     } catch (Exception exception) {
/* 275 */       throw logAndConvert(exception);
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
/*     */ 
/*     */   
/*     */   public boolean isReadOnly(int paramInt) throws SQLException {
/*     */     try {
/* 290 */       getColumn("isReadOnly", paramInt);
/* 291 */       return false;
/* 292 */     } catch (Exception exception) {
/* 293 */       throw logAndConvert(exception);
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
/*     */ 
/*     */   
/*     */   public boolean isWritable(int paramInt) throws SQLException {
/*     */     try {
/* 308 */       getColumn("isWritable", paramInt);
/* 309 */       return true;
/* 310 */     } catch (Exception exception) {
/* 311 */       throw logAndConvert(exception);
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
/*     */ 
/*     */   
/*     */   public boolean isDefinitelyWritable(int paramInt) throws SQLException {
/*     */     try {
/* 326 */       getColumn("isDefinitelyWritable", paramInt);
/* 327 */       return false;
/* 328 */     } catch (Exception exception) {
/* 329 */       throw logAndConvert(exception);
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
/*     */ 
/*     */   
/*     */   public String getColumnClassName(int paramInt) throws SQLException {
/*     */     try {
/* 344 */       int i = this.result.getColumnType(getColumn("getColumnClassName", paramInt)).getValueType();
/* 345 */       return ValueToObjectConverter.getDefaultClass(i, true).getName();
/* 346 */     } catch (Exception exception) {
/* 347 */       throw logAndConvert(exception);
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
/*     */   
/*     */   public int getPrecision(int paramInt) throws SQLException {
/*     */     try {
/* 361 */       return MathUtils.convertLongToInt(this.result.getColumnType(getColumn("getPrecision", paramInt)).getPrecision());
/* 362 */     } catch (Exception exception) {
/* 363 */       throw logAndConvert(exception);
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
/*     */   
/*     */   public int getScale(int paramInt) throws SQLException {
/*     */     try {
/* 377 */       return this.result.getColumnType(getColumn("getScale", paramInt)).getScale();
/* 378 */     } catch (Exception exception) {
/* 379 */       throw logAndConvert(exception);
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
/*     */   
/*     */   public int getColumnDisplaySize(int paramInt) throws SQLException {
/*     */     try {
/* 393 */       return this.result.getColumnType(getColumn("getColumnDisplaySize", paramInt)).getDisplaySize();
/* 394 */     } catch (Exception exception) {
/* 395 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void checkClosed() {
/* 400 */     if (this.rs != null) {
/* 401 */       this.rs.checkClosed();
/*     */     }
/* 403 */     if (this.prep != null) {
/* 404 */       this.prep.checkClosed();
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
/*     */ 
/*     */ 
/*     */   
/*     */   private int getColumn(String paramString, int paramInt) {
/* 419 */     debugCodeCall(paramString, paramInt);
/* 420 */     checkClosed();
/* 421 */     if (paramInt < 1 || paramInt > this.columnCount) {
/* 422 */       throw DbException.getInvalidValueException("columnIndex", Integer.valueOf(paramInt));
/*     */     }
/* 424 */     return paramInt - 1;
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
/*     */   public <T> T unwrap(Class<T> paramClass) throws SQLException {
/*     */     try {
/* 437 */       if (isWrapperFor(paramClass)) {
/* 438 */         return (T)this;
/*     */       }
/* 440 */       throw DbException.getInvalidValueException("iface", paramClass);
/* 441 */     } catch (Exception exception) {
/* 442 */       throw logAndConvert(exception);
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
/*     */   public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
/* 454 */     return (paramClass != null && paramClass.isAssignableFrom(getClass()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 462 */     return getTraceObjectName() + ": columns=" + this.columnCount;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcResultSetMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */