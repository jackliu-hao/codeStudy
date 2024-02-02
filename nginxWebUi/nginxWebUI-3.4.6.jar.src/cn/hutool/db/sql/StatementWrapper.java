/*     */ package cn.hutool.db.sql;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.URL;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Date;
/*     */ import java.sql.NClob;
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.Ref;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StatementWrapper
/*     */   implements PreparedStatement
/*     */ {
/*     */   private PreparedStatement rawStatement;
/*     */   
/*     */   public StatementWrapper(PreparedStatement rawStatement) {
/*  48 */     this.rawStatement = rawStatement;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet executeQuery(String sql) throws SQLException {
/*  53 */     return this.rawStatement.executeQuery(sql);
/*     */   }
/*     */ 
/*     */   
/*     */   public int executeUpdate(String sql) throws SQLException {
/*  58 */     return this.rawStatement.executeUpdate(sql);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws SQLException {
/*  63 */     this.rawStatement.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxFieldSize() throws SQLException {
/*  68 */     return this.rawStatement.getMaxFieldSize();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxFieldSize(int max) throws SQLException {
/*  73 */     this.rawStatement.setMaxFieldSize(max);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxRows() throws SQLException {
/*  78 */     return this.rawStatement.getMaxRows();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxRows(int max) throws SQLException {
/*  83 */     this.rawStatement.setMaxRows(max);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEscapeProcessing(boolean enable) throws SQLException {
/*  88 */     this.rawStatement.setEscapeProcessing(enable);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getQueryTimeout() throws SQLException {
/*  93 */     return this.rawStatement.getQueryTimeout();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setQueryTimeout(int seconds) throws SQLException {
/*  98 */     this.rawStatement.setQueryTimeout(seconds);
/*     */   }
/*     */ 
/*     */   
/*     */   public void cancel() throws SQLException {
/* 103 */     this.rawStatement.cancel();
/*     */   }
/*     */ 
/*     */   
/*     */   public SQLWarning getWarnings() throws SQLException {
/* 108 */     return this.rawStatement.getWarnings();
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearWarnings() throws SQLException {
/* 113 */     this.rawStatement.clearWarnings();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCursorName(String name) throws SQLException {
/* 118 */     this.rawStatement.setCursorName(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean execute(String sql) throws SQLException {
/* 123 */     return this.rawStatement.execute(sql);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getResultSet() throws SQLException {
/* 128 */     return this.rawStatement.getResultSet();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUpdateCount() throws SQLException {
/* 133 */     return this.rawStatement.getUpdateCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getMoreResults() throws SQLException {
/* 138 */     return this.rawStatement.getMoreResults();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFetchDirection(int direction) throws SQLException {
/* 143 */     this.rawStatement.setFetchDirection(direction);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFetchDirection() throws SQLException {
/* 148 */     return this.rawStatement.getFetchDirection();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFetchSize(int rows) throws SQLException {
/* 153 */     this.rawStatement.setFetchSize(rows);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFetchSize() throws SQLException {
/* 158 */     return this.rawStatement.getFetchSize();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getResultSetConcurrency() throws SQLException {
/* 163 */     return this.rawStatement.getResultSetConcurrency();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getResultSetType() throws SQLException {
/* 168 */     return this.rawStatement.getResultSetType();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addBatch(String sql) throws SQLException {
/* 173 */     this.rawStatement.addBatch(sql);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearBatch() throws SQLException {
/* 178 */     this.rawStatement.clearBatch();
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] executeBatch() throws SQLException {
/* 183 */     return this.rawStatement.executeBatch();
/*     */   }
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/* 188 */     return this.rawStatement.getConnection();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getMoreResults(int current) throws SQLException {
/* 193 */     return this.rawStatement.getMoreResults(current);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getGeneratedKeys() throws SQLException {
/* 198 */     return this.rawStatement.getGeneratedKeys();
/*     */   }
/*     */ 
/*     */   
/*     */   public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
/* 203 */     return this.rawStatement.executeUpdate(sql, autoGeneratedKeys);
/*     */   }
/*     */ 
/*     */   
/*     */   public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
/* 208 */     return this.rawStatement.executeUpdate(sql, columnIndexes);
/*     */   }
/*     */ 
/*     */   
/*     */   public int executeUpdate(String sql, String[] columnNames) throws SQLException {
/* 213 */     return this.rawStatement.executeUpdate(sql, columnNames);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
/* 218 */     return this.rawStatement.execute(sql, autoGeneratedKeys);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean execute(String sql, int[] columnIndexes) throws SQLException {
/* 223 */     return this.rawStatement.execute(sql, columnIndexes);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean execute(String sql, String[] columnNames) throws SQLException {
/* 228 */     return this.rawStatement.execute(sql, columnNames);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getResultSetHoldability() throws SQLException {
/* 233 */     return this.rawStatement.getResultSetHoldability();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() throws SQLException {
/* 238 */     return this.rawStatement.isClosed();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPoolable(boolean poolable) throws SQLException {
/* 243 */     this.rawStatement.setPoolable(poolable);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPoolable() throws SQLException {
/* 248 */     return this.rawStatement.isPoolable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeOnCompletion() throws SQLException {
/* 253 */     this.rawStatement.closeOnCompletion();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCloseOnCompletion() throws SQLException {
/* 258 */     return this.rawStatement.isCloseOnCompletion();
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/* 263 */     return this.rawStatement.unwrap(iface);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 268 */     return this.rawStatement.isWrapperFor(iface);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet executeQuery() throws SQLException {
/* 273 */     return this.rawStatement.executeQuery();
/*     */   }
/*     */ 
/*     */   
/*     */   public int executeUpdate() throws SQLException {
/* 278 */     return this.rawStatement.executeUpdate();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNull(int parameterIndex, int sqlType) throws SQLException {
/* 283 */     this.rawStatement.setNull(parameterIndex, sqlType);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBoolean(int parameterIndex, boolean x) throws SQLException {
/* 288 */     this.rawStatement.setBoolean(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setByte(int parameterIndex, byte x) throws SQLException {
/* 293 */     this.rawStatement.setByte(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setShort(int parameterIndex, short x) throws SQLException {
/* 298 */     this.rawStatement.setShort(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInt(int parameterIndex, int x) throws SQLException {
/* 303 */     this.rawStatement.setInt(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLong(int parameterIndex, long x) throws SQLException {
/* 308 */     this.rawStatement.setLong(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFloat(int parameterIndex, float x) throws SQLException {
/* 313 */     this.rawStatement.setFloat(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDouble(int parameterIndex, double x) throws SQLException {
/* 318 */     this.rawStatement.setDouble(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
/* 323 */     this.rawStatement.setBigDecimal(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setString(int parameterIndex, String x) throws SQLException {
/* 328 */     this.rawStatement.setString(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBytes(int parameterIndex, byte[] x) throws SQLException {
/* 333 */     this.rawStatement.setBytes(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDate(int parameterIndex, Date x) throws SQLException {
/* 338 */     this.rawStatement.setDate(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTime(int parameterIndex, Time x) throws SQLException {
/* 343 */     this.rawStatement.setTime(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
/* 348 */     this.rawStatement.setTimestamp(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
/* 353 */     this.rawStatement.setAsciiStream(parameterIndex, x, length);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
/* 359 */     this.rawStatement.setUnicodeStream(parameterIndex, x, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
/* 364 */     this.rawStatement.setBinaryStream(parameterIndex, x, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearParameters() throws SQLException {
/* 369 */     this.rawStatement.clearParameters();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
/* 374 */     this.rawStatement.setObject(parameterIndex, x, targetSqlType, targetSqlType);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setObject(int parameterIndex, Object x) throws SQLException {
/* 379 */     this.rawStatement.setObject(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean execute() throws SQLException {
/* 384 */     return this.rawStatement.execute();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addBatch() throws SQLException {
/* 389 */     this.rawStatement.addBatch();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
/* 394 */     this.rawStatement.setCharacterStream(parameterIndex, reader, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRef(int parameterIndex, Ref x) throws SQLException {
/* 399 */     this.rawStatement.setRef(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, Blob x) throws SQLException {
/* 404 */     this.rawStatement.setBlob(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Clob x) throws SQLException {
/* 409 */     this.rawStatement.setClob(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setArray(int parameterIndex, Array x) throws SQLException {
/* 414 */     this.rawStatement.setArray(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSetMetaData getMetaData() throws SQLException {
/* 419 */     return this.rawStatement.getMetaData();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
/* 424 */     this.rawStatement.setDate(parameterIndex, x, cal);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
/* 429 */     this.rawStatement.setTime(parameterIndex, x, cal);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
/* 434 */     this.rawStatement.setTimestamp(parameterIndex, x, cal);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
/* 439 */     this.rawStatement.setNull(parameterIndex, sqlType, typeName);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setURL(int parameterIndex, URL x) throws SQLException {
/* 444 */     this.rawStatement.setURL(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public ParameterMetaData getParameterMetaData() throws SQLException {
/* 449 */     return this.rawStatement.getParameterMetaData();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/* 454 */     this.rawStatement.setRowId(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNString(int parameterIndex, String value) throws SQLException {
/* 459 */     this.rawStatement.setNString(parameterIndex, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
/* 464 */     this.rawStatement.setCharacterStream(parameterIndex, value, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, NClob value) throws SQLException {
/* 469 */     this.rawStatement.setNClob(parameterIndex, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
/* 474 */     this.rawStatement.setClob(parameterIndex, reader, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
/* 479 */     this.rawStatement.setBlob(parameterIndex, inputStream, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
/* 484 */     this.rawStatement.setNClob(parameterIndex, reader, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
/* 489 */     this.rawStatement.setSQLXML(parameterIndex, xmlObject);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
/* 494 */     this.rawStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
/* 499 */     this.rawStatement.setAsciiStream(parameterIndex, x, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
/* 504 */     this.rawStatement.setBinaryStream(parameterIndex, x, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/* 509 */     this.rawStatement.setCharacterStream(parameterIndex, reader, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
/* 514 */     this.rawStatement.setAsciiStream(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
/* 519 */     this.rawStatement.setBinaryStream(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
/* 524 */     this.rawStatement.setCharacterStream(parameterIndex, reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
/* 529 */     this.rawStatement.setNCharacterStream(parameterIndex, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader) throws SQLException {
/* 534 */     this.rawStatement.setClob(parameterIndex, reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
/* 539 */     this.rawStatement.setBlob(parameterIndex, inputStream);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader) throws SQLException {
/* 544 */     this.rawStatement.setNClob(parameterIndex, reader);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\StatementWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */