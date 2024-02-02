/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.URL;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.NClob;
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.Ref;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLType;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ import java.util.HashMap;
/*     */ import javax.sql.StatementEvent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PreparedStatementWrapper
/*     */   extends StatementWrapper
/*     */   implements PreparedStatement
/*     */ {
/*     */   protected static PreparedStatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap) throws SQLException {
/*  68 */     return new PreparedStatementWrapper(c, conn, toWrap);
/*     */   }
/*     */   
/*     */   PreparedStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap) {
/*  72 */     super(c, conn, toWrap);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setArray(int parameterIndex, Array x) throws SQLException {
/*     */     try {
/*  78 */       if (this.wrappedStmt != null) {
/*  79 */         ((PreparedStatement)this.wrappedStmt).setArray(parameterIndex, x);
/*     */       } else {
/*  81 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/*  84 */     } catch (SQLException sqlEx) {
/*  85 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
/*     */     try {
/*  92 */       if (this.wrappedStmt != null) {
/*  93 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x, length);
/*     */       } else {
/*  95 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/*  98 */     } catch (SQLException sqlEx) {
/*  99 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
/*     */     try {
/* 106 */       if (this.wrappedStmt != null) {
/* 107 */         ((PreparedStatement)this.wrappedStmt).setBigDecimal(parameterIndex, x);
/*     */       } else {
/* 109 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 112 */     } catch (SQLException sqlEx) {
/* 113 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
/*     */     try {
/* 120 */       if (this.wrappedStmt != null) {
/* 121 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x, length);
/*     */       } else {
/* 123 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 126 */     } catch (SQLException sqlEx) {
/* 127 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, Blob x) throws SQLException {
/*     */     try {
/* 134 */       if (this.wrappedStmt != null) {
/* 135 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, x);
/*     */       } else {
/* 137 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 140 */     } catch (SQLException sqlEx) {
/* 141 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBoolean(int parameterIndex, boolean x) throws SQLException {
/*     */     try {
/* 148 */       if (this.wrappedStmt != null) {
/* 149 */         ((PreparedStatement)this.wrappedStmt).setBoolean(parameterIndex, x);
/*     */       } else {
/* 151 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 154 */     } catch (SQLException sqlEx) {
/* 155 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setByte(int parameterIndex, byte x) throws SQLException {
/*     */     try {
/* 162 */       if (this.wrappedStmt != null) {
/* 163 */         ((PreparedStatement)this.wrappedStmt).setByte(parameterIndex, x);
/*     */       } else {
/* 165 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 168 */     } catch (SQLException sqlEx) {
/* 169 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBytes(int parameterIndex, byte[] x) throws SQLException {
/*     */     try {
/* 176 */       if (this.wrappedStmt != null) {
/* 177 */         ((PreparedStatement)this.wrappedStmt).setBytes(parameterIndex, x);
/*     */       } else {
/* 179 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 182 */     } catch (SQLException sqlEx) {
/* 183 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
/*     */     try {
/* 190 */       if (this.wrappedStmt != null) {
/* 191 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader, length);
/*     */       } else {
/* 193 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 196 */     } catch (SQLException sqlEx) {
/* 197 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Clob x) throws SQLException {
/*     */     try {
/* 204 */       if (this.wrappedStmt != null) {
/* 205 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, x);
/*     */       } else {
/* 207 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 210 */     } catch (SQLException sqlEx) {
/* 211 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDate(int parameterIndex, Date x) throws SQLException {
/*     */     try {
/* 218 */       if (this.wrappedStmt != null) {
/* 219 */         ((PreparedStatement)this.wrappedStmt).setDate(parameterIndex, x);
/*     */       } else {
/* 221 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 224 */     } catch (SQLException sqlEx) {
/* 225 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
/*     */     try {
/* 232 */       if (this.wrappedStmt != null) {
/* 233 */         ((PreparedStatement)this.wrappedStmt).setDate(parameterIndex, x, cal);
/*     */       } else {
/* 235 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 238 */     } catch (SQLException sqlEx) {
/* 239 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDouble(int parameterIndex, double x) throws SQLException {
/*     */     try {
/* 246 */       if (this.wrappedStmt != null) {
/* 247 */         ((PreparedStatement)this.wrappedStmt).setDouble(parameterIndex, x);
/*     */       } else {
/* 249 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 252 */     } catch (SQLException sqlEx) {
/* 253 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFloat(int parameterIndex, float x) throws SQLException {
/*     */     try {
/* 260 */       if (this.wrappedStmt != null) {
/* 261 */         ((PreparedStatement)this.wrappedStmt).setFloat(parameterIndex, x);
/*     */       } else {
/* 263 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 266 */     } catch (SQLException sqlEx) {
/* 267 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInt(int parameterIndex, int x) throws SQLException {
/*     */     try {
/* 274 */       if (this.wrappedStmt != null) {
/* 275 */         ((PreparedStatement)this.wrappedStmt).setInt(parameterIndex, x);
/*     */       } else {
/* 277 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 280 */     } catch (SQLException sqlEx) {
/* 281 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLong(int parameterIndex, long x) throws SQLException {
/*     */     try {
/* 288 */       if (this.wrappedStmt != null) {
/* 289 */         ((PreparedStatement)this.wrappedStmt).setLong(parameterIndex, x);
/*     */       } else {
/* 291 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 294 */     } catch (SQLException sqlEx) {
/* 295 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSetMetaData getMetaData() throws SQLException {
/*     */     try {
/* 302 */       if (this.wrappedStmt != null) {
/* 303 */         return ((PreparedStatement)this.wrappedStmt).getMetaData();
/*     */       }
/*     */       
/* 306 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */     }
/* 308 */     catch (SQLException sqlEx) {
/* 309 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 312 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setNull(int parameterIndex, int sqlType) throws SQLException {
/*     */     try {
/* 318 */       if (this.wrappedStmt != null) {
/* 319 */         ((PreparedStatement)this.wrappedStmt).setNull(parameterIndex, sqlType);
/*     */       } else {
/* 321 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 324 */     } catch (SQLException sqlEx) {
/* 325 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
/*     */     try {
/* 332 */       if (this.wrappedStmt != null) {
/* 333 */         ((PreparedStatement)this.wrappedStmt).setNull(parameterIndex, sqlType, typeName);
/*     */       } else {
/* 335 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 338 */     } catch (SQLException sqlEx) {
/* 339 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setObject(int parameterIndex, Object x) throws SQLException {
/*     */     try {
/* 346 */       if (this.wrappedStmt != null) {
/* 347 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x);
/*     */       } else {
/* 349 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 352 */     } catch (SQLException sqlEx) {
/* 353 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
/*     */     try {
/* 360 */       if (this.wrappedStmt != null) {
/* 361 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType);
/*     */       } else {
/* 363 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 366 */     } catch (SQLException sqlEx) {
/* 367 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
/*     */     try {
/* 374 */       if (this.wrappedStmt != null) {
/* 375 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType, scale);
/*     */       } else {
/* 377 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 380 */     } catch (SQLException sqlEx) {
/* 381 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ParameterMetaData getParameterMetaData() throws SQLException {
/*     */     try {
/* 388 */       if (this.wrappedStmt != null) {
/* 389 */         return ((PreparedStatement)this.wrappedStmt).getParameterMetaData();
/*     */       }
/*     */       
/* 392 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */     }
/* 394 */     catch (SQLException sqlEx) {
/* 395 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 398 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setRef(int parameterIndex, Ref x) throws SQLException {
/*     */     try {
/* 404 */       if (this.wrappedStmt != null) {
/* 405 */         ((PreparedStatement)this.wrappedStmt).setRef(parameterIndex, x);
/*     */       } else {
/* 407 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 410 */     } catch (SQLException sqlEx) {
/* 411 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setShort(int parameterIndex, short x) throws SQLException {
/*     */     try {
/* 418 */       if (this.wrappedStmt != null) {
/* 419 */         ((PreparedStatement)this.wrappedStmt).setShort(parameterIndex, x);
/*     */       } else {
/* 421 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 424 */     } catch (SQLException sqlEx) {
/* 425 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setString(int parameterIndex, String x) throws SQLException {
/*     */     try {
/* 432 */       if (this.wrappedStmt != null) {
/* 433 */         ((PreparedStatement)this.wrappedStmt).setString(parameterIndex, x);
/*     */       } else {
/* 435 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 438 */     } catch (SQLException sqlEx) {
/* 439 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTime(int parameterIndex, Time x) throws SQLException {
/*     */     try {
/* 446 */       if (this.wrappedStmt != null) {
/* 447 */         ((PreparedStatement)this.wrappedStmt).setTime(parameterIndex, x);
/*     */       } else {
/* 449 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 452 */     } catch (SQLException sqlEx) {
/* 453 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
/*     */     try {
/* 460 */       if (this.wrappedStmt != null) {
/* 461 */         ((PreparedStatement)this.wrappedStmt).setTime(parameterIndex, x, cal);
/*     */       } else {
/* 463 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 466 */     } catch (SQLException sqlEx) {
/* 467 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
/*     */     try {
/* 474 */       if (this.wrappedStmt != null) {
/* 475 */         ((PreparedStatement)this.wrappedStmt).setTimestamp(parameterIndex, x);
/*     */       } else {
/* 477 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 480 */     } catch (SQLException sqlEx) {
/* 481 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
/*     */     try {
/* 488 */       if (this.wrappedStmt != null) {
/* 489 */         ((PreparedStatement)this.wrappedStmt).setTimestamp(parameterIndex, x, cal);
/*     */       } else {
/* 491 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 494 */     } catch (SQLException sqlEx) {
/* 495 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setURL(int parameterIndex, URL x) throws SQLException {
/*     */     try {
/* 502 */       if (this.wrappedStmt != null) {
/* 503 */         ((PreparedStatement)this.wrappedStmt).setURL(parameterIndex, x);
/*     */       } else {
/* 505 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 508 */     } catch (SQLException sqlEx) {
/* 509 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
/*     */     try {
/* 517 */       if (this.wrappedStmt != null) {
/* 518 */         ((PreparedStatement)this.wrappedStmt).setUnicodeStream(parameterIndex, x, length);
/*     */       } else {
/* 520 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 523 */     } catch (SQLException sqlEx) {
/* 524 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addBatch() throws SQLException {
/*     */     try {
/* 531 */       if (this.wrappedStmt != null) {
/* 532 */         ((PreparedStatement)this.wrappedStmt).addBatch();
/*     */       } else {
/* 534 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 537 */     } catch (SQLException sqlEx) {
/* 538 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearParameters() throws SQLException {
/*     */     try {
/* 545 */       if (this.wrappedStmt != null) {
/* 546 */         ((PreparedStatement)this.wrappedStmt).clearParameters();
/*     */       } else {
/* 548 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 551 */     } catch (SQLException sqlEx) {
/* 552 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean execute() throws SQLException {
/*     */     try {
/* 559 */       if (this.wrappedStmt != null) {
/* 560 */         return ((PreparedStatement)this.wrappedStmt).execute();
/*     */       }
/*     */       
/* 563 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */     }
/* 565 */     catch (SQLException sqlEx) {
/* 566 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 569 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public ResultSet executeQuery() throws SQLException {
/* 574 */     ResultSet rs = null;
/*     */     try {
/* 576 */       if (this.wrappedStmt == null) {
/* 577 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */ 
/*     */       
/* 581 */       rs = ((PreparedStatement)this.wrappedStmt).executeQuery();
/* 582 */       ((ResultSetInternalMethods)rs).setWrapperStatement(this);
/*     */     }
/* 584 */     catch (SQLException sqlEx) {
/* 585 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */     
/* 588 */     return rs;
/*     */   }
/*     */ 
/*     */   
/*     */   public int executeUpdate() throws SQLException {
/*     */     try {
/* 594 */       if (this.wrappedStmt != null) {
/* 595 */         return ((PreparedStatement)this.wrappedStmt).executeUpdate();
/*     */       }
/*     */       
/* 598 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */     }
/* 600 */     catch (SQLException sqlEx) {
/* 601 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 604 */       return -1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public String toString() {
/* 609 */     StringBuilder buf = new StringBuilder(super.toString());
/*     */     
/* 611 */     if (this.wrappedStmt != null) {
/* 612 */       buf.append(": ");
/*     */       try {
/* 614 */         buf.append(((ClientPreparedStatement)this.wrappedStmt).asSql());
/* 615 */       } catch (SQLException sqlEx) {
/* 616 */         buf.append("EXCEPTION: " + sqlEx.toString());
/*     */       } 
/*     */     } 
/*     */     
/* 620 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/*     */     try {
/* 626 */       if (this.wrappedStmt != null) {
/* 627 */         ((PreparedStatement)this.wrappedStmt).setRowId(parameterIndex, x);
/*     */       } else {
/* 629 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 632 */     } catch (SQLException sqlEx) {
/* 633 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNString(int parameterIndex, String value) throws SQLException {
/*     */     try {
/* 640 */       if (this.wrappedStmt != null) {
/* 641 */         ((PreparedStatement)this.wrappedStmt).setNString(parameterIndex, value);
/*     */       } else {
/* 643 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 646 */     } catch (SQLException sqlEx) {
/* 647 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
/*     */     try {
/* 654 */       if (this.wrappedStmt != null) {
/* 655 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value, length);
/*     */       } else {
/* 657 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 660 */     } catch (SQLException sqlEx) {
/* 661 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, NClob value) throws SQLException {
/*     */     try {
/* 668 */       if (this.wrappedStmt != null) {
/* 669 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, value);
/*     */       } else {
/* 671 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 674 */     } catch (SQLException sqlEx) {
/* 675 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
/*     */     try {
/* 682 */       if (this.wrappedStmt != null) {
/* 683 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader, length);
/*     */       } else {
/* 685 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 688 */     } catch (SQLException sqlEx) {
/* 689 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
/*     */     try {
/* 696 */       if (this.wrappedStmt != null) {
/* 697 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream, length);
/*     */       } else {
/* 699 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 702 */     } catch (SQLException sqlEx) {
/* 703 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
/*     */     try {
/* 710 */       if (this.wrappedStmt != null) {
/* 711 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader, length);
/*     */       } else {
/* 713 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 716 */     } catch (SQLException sqlEx) {
/* 717 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
/*     */     try {
/* 724 */       if (this.wrappedStmt != null) {
/* 725 */         ((PreparedStatement)this.wrappedStmt).setSQLXML(parameterIndex, xmlObject);
/*     */       } else {
/* 727 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 730 */     } catch (SQLException sqlEx) {
/* 731 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
/*     */     try {
/* 738 */       if (this.wrappedStmt != null) {
/* 739 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x, length);
/*     */       } else {
/* 741 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 744 */     } catch (SQLException sqlEx) {
/* 745 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
/*     */     try {
/* 752 */       if (this.wrappedStmt != null) {
/* 753 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x, length);
/*     */       } else {
/* 755 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 758 */     } catch (SQLException sqlEx) {
/* 759 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/*     */     try {
/* 766 */       if (this.wrappedStmt != null) {
/* 767 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader, length);
/*     */       } else {
/* 769 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 772 */     } catch (SQLException sqlEx) {
/* 773 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
/*     */     try {
/* 780 */       if (this.wrappedStmt != null) {
/* 781 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x);
/*     */       } else {
/* 783 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 786 */     } catch (SQLException sqlEx) {
/* 787 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
/*     */     try {
/* 794 */       if (this.wrappedStmt != null) {
/* 795 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x);
/*     */       } else {
/* 797 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 800 */     } catch (SQLException sqlEx) {
/* 801 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
/*     */     try {
/* 808 */       if (this.wrappedStmt != null) {
/* 809 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader);
/*     */       } else {
/* 811 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 814 */     } catch (SQLException sqlEx) {
/* 815 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
/*     */     try {
/* 823 */       if (this.wrappedStmt != null) {
/* 824 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value);
/*     */       } else {
/* 826 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 829 */     } catch (SQLException sqlEx) {
/* 830 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader) throws SQLException {
/*     */     try {
/* 838 */       if (this.wrappedStmt != null) {
/* 839 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader);
/*     */       } else {
/* 841 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 844 */     } catch (SQLException sqlEx) {
/* 845 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
/*     */     try {
/* 853 */       if (this.wrappedStmt != null) {
/* 854 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream);
/*     */       } else {
/* 856 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 859 */     } catch (SQLException sqlEx) {
/* 860 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader) throws SQLException {
/*     */     try {
/* 867 */       if (this.wrappedStmt != null) {
/* 868 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader);
/*     */       } else {
/* 870 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 873 */     } catch (SQLException sqlEx) {
/* 874 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 881 */     boolean isInstance = iface.isInstance(this);
/*     */     
/* 883 */     if (isInstance) {
/* 884 */       return true;
/*     */     }
/*     */     
/* 887 */     String interfaceClassName = iface.getName();
/*     */     
/* 889 */     return (interfaceClassName.equals("com.mysql.cj.jdbc.Statement") || interfaceClassName.equals("java.sql.Statement") || interfaceClassName
/* 890 */       .equals("java.sql.Wrapper") || interfaceClassName.equals("java.sql.PreparedStatement"));
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
/*     */     try {
/* 896 */       if ("java.sql.Statement".equals(iface.getName()) || "java.sql.PreparedStatement".equals(iface.getName()) || "java.sql.Wrapper.class"
/* 897 */         .equals(iface.getName())) {
/* 898 */         return iface.cast(this);
/*     */       }
/*     */       
/* 901 */       if (this.unwrappedInterfaces == null) {
/* 902 */         this.unwrappedInterfaces = new HashMap<>();
/*     */       }
/*     */       
/* 905 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*     */       
/* 907 */       if (cachedUnwrapped == null) {
/* 908 */         cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
/*     */         
/* 910 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */       } 
/*     */       
/* 913 */       return iface.cast(cachedUnwrapped);
/* 914 */     } catch (ClassCastException cce) {
/* 915 */       throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", this.exceptionInterceptor);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void close() throws SQLException {
/* 922 */     if (this.pooledConnection == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 927 */     MysqlPooledConnection con = this.pooledConnection;
/*     */     
/*     */     try {
/* 930 */       super.close();
/*     */     } finally {
/*     */       try {
/* 933 */         StatementEvent e = new StatementEvent(con, this);
/* 934 */         con.fireStatementEvent(e);
/*     */       } finally {
/* 936 */         this.unwrappedInterfaces = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long executeLargeUpdate() throws SQLException {
/*     */     try {
/* 944 */       if (this.wrappedStmt != null) {
/* 945 */         return ((ClientPreparedStatement)this.wrappedStmt).executeLargeUpdate();
/*     */       }
/*     */       
/* 948 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 950 */     catch (SQLException sqlEx) {
/* 951 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 954 */       return -1L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
/*     */     try {
/* 960 */       if (this.wrappedStmt != null) {
/* 961 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType);
/*     */       } else {
/* 963 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 966 */     } catch (SQLException sqlEx) {
/* 967 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
/*     */     try {
/* 974 */       if (this.wrappedStmt != null) {
/* 975 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType, scaleOrLength);
/*     */       } else {
/* 977 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 980 */     } catch (SQLException sqlEx) {
/* 981 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\PreparedStatementWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */