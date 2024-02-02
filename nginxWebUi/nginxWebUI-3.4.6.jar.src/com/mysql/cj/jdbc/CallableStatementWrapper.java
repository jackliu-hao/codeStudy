/*      */ package com.mysql.cj.jdbc;
/*      */ 
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.NClob;
/*      */ import java.sql.Ref;
/*      */ import java.sql.RowId;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLType;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class CallableStatementWrapper
/*      */   extends PreparedStatementWrapper
/*      */   implements CallableStatement
/*      */ {
/*      */   protected static CallableStatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap) throws SQLException {
/*   64 */     return new CallableStatementWrapper(c, conn, toWrap);
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
/*      */   public CallableStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap) {
/*   76 */     super(c, conn, toWrap);
/*      */   }
/*      */ 
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
/*      */     try {
/*   82 */       if (this.wrappedStmt != null) {
/*   83 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType);
/*      */       } else {
/*   85 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*   88 */     } catch (SQLException sqlEx) {
/*   89 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
/*      */     try {
/*   96 */       if (this.wrappedStmt != null) {
/*   97 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType, scale);
/*      */       } else {
/*   99 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  102 */     } catch (SQLException sqlEx) {
/*  103 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean wasNull() throws SQLException {
/*      */     try {
/*  110 */       if (this.wrappedStmt != null) {
/*  111 */         return ((CallableStatement)this.wrappedStmt).wasNull();
/*      */       }
/*  113 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  116 */     catch (SQLException sqlEx) {
/*  117 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  120 */       return false;
/*      */     } 
/*      */   }
/*      */   
/*      */   public String getString(int parameterIndex) throws SQLException {
/*      */     try {
/*  126 */       if (this.wrappedStmt != null) {
/*  127 */         return ((CallableStatement)this.wrappedStmt).getString(parameterIndex);
/*      */       }
/*  129 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  132 */     catch (SQLException sqlEx) {
/*  133 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  135 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean getBoolean(int parameterIndex) throws SQLException {
/*      */     try {
/*  141 */       if (this.wrappedStmt != null) {
/*  142 */         return ((CallableStatement)this.wrappedStmt).getBoolean(parameterIndex);
/*      */       }
/*  144 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  147 */     catch (SQLException sqlEx) {
/*  148 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  151 */       return false;
/*      */     } 
/*      */   }
/*      */   
/*      */   public byte getByte(int parameterIndex) throws SQLException {
/*      */     try {
/*  157 */       if (this.wrappedStmt != null) {
/*  158 */         return ((CallableStatement)this.wrappedStmt).getByte(parameterIndex);
/*      */       }
/*  160 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  163 */     catch (SQLException sqlEx) {
/*  164 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  167 */       return 0;
/*      */     } 
/*      */   }
/*      */   
/*      */   public short getShort(int parameterIndex) throws SQLException {
/*      */     try {
/*  173 */       if (this.wrappedStmt != null) {
/*  174 */         return ((CallableStatement)this.wrappedStmt).getShort(parameterIndex);
/*      */       }
/*  176 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  179 */     catch (SQLException sqlEx) {
/*  180 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  183 */       return 0;
/*      */     } 
/*      */   }
/*      */   
/*      */   public int getInt(int parameterIndex) throws SQLException {
/*      */     try {
/*  189 */       if (this.wrappedStmt != null) {
/*  190 */         return ((CallableStatement)this.wrappedStmt).getInt(parameterIndex);
/*      */       }
/*  192 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  195 */     catch (SQLException sqlEx) {
/*  196 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  199 */       return 0;
/*      */     } 
/*      */   }
/*      */   
/*      */   public long getLong(int parameterIndex) throws SQLException {
/*      */     try {
/*  205 */       if (this.wrappedStmt != null) {
/*  206 */         return ((CallableStatement)this.wrappedStmt).getLong(parameterIndex);
/*      */       }
/*  208 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  211 */     catch (SQLException sqlEx) {
/*  212 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  215 */       return 0L;
/*      */     } 
/*      */   }
/*      */   
/*      */   public float getFloat(int parameterIndex) throws SQLException {
/*      */     try {
/*  221 */       if (this.wrappedStmt != null) {
/*  222 */         return ((CallableStatement)this.wrappedStmt).getFloat(parameterIndex);
/*      */       }
/*  224 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  227 */     catch (SQLException sqlEx) {
/*  228 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  231 */       return 0.0F;
/*      */     } 
/*      */   }
/*      */   
/*      */   public double getDouble(int parameterIndex) throws SQLException {
/*      */     try {
/*  237 */       if (this.wrappedStmt != null) {
/*  238 */         return ((CallableStatement)this.wrappedStmt).getDouble(parameterIndex);
/*      */       }
/*  240 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  243 */     catch (SQLException sqlEx) {
/*  244 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  247 */       return 0.0D;
/*      */     } 
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
/*      */     try {
/*  254 */       if (this.wrappedStmt != null) {
/*  255 */         return ((CallableStatement)this.wrappedStmt).getBigDecimal(parameterIndex, scale);
/*      */       }
/*  257 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  260 */     catch (SQLException sqlEx) {
/*  261 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  264 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public byte[] getBytes(int parameterIndex) throws SQLException {
/*      */     try {
/*  270 */       if (this.wrappedStmt != null) {
/*  271 */         return ((CallableStatement)this.wrappedStmt).getBytes(parameterIndex);
/*      */       }
/*  273 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  276 */     catch (SQLException sqlEx) {
/*  277 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  280 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Date getDate(int parameterIndex) throws SQLException {
/*      */     try {
/*  286 */       if (this.wrappedStmt != null) {
/*  287 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterIndex);
/*      */       }
/*  289 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  292 */     catch (SQLException sqlEx) {
/*  293 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  296 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Time getTime(int parameterIndex) throws SQLException {
/*      */     try {
/*  302 */       if (this.wrappedStmt != null) {
/*  303 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterIndex);
/*      */       }
/*  305 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  308 */     catch (SQLException sqlEx) {
/*  309 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  312 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Timestamp getTimestamp(int parameterIndex) throws SQLException {
/*      */     try {
/*  318 */       if (this.wrappedStmt != null) {
/*  319 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterIndex);
/*      */       }
/*  321 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  324 */     catch (SQLException sqlEx) {
/*  325 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  328 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Object getObject(int parameterIndex) throws SQLException {
/*      */     try {
/*  334 */       if (this.wrappedStmt != null) {
/*  335 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterIndex);
/*      */       }
/*  337 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  340 */     catch (SQLException sqlEx) {
/*  341 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  344 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
/*      */     try {
/*  350 */       if (this.wrappedStmt != null) {
/*  351 */         return ((CallableStatement)this.wrappedStmt).getBigDecimal(parameterIndex);
/*      */       }
/*  353 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  356 */     catch (SQLException sqlEx) {
/*  357 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  360 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Object getObject(int parameterIndex, Map<String, Class<?>> typeMap) throws SQLException {
/*      */     try {
/*  366 */       if (this.wrappedStmt != null) {
/*  367 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterIndex, typeMap);
/*      */       }
/*  369 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  372 */     catch (SQLException sqlEx) {
/*  373 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  375 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Ref getRef(int parameterIndex) throws SQLException {
/*      */     try {
/*  381 */       if (this.wrappedStmt != null) {
/*  382 */         return ((CallableStatement)this.wrappedStmt).getRef(parameterIndex);
/*      */       }
/*  384 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  387 */     catch (SQLException sqlEx) {
/*  388 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  391 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Blob getBlob(int parameterIndex) throws SQLException {
/*      */     try {
/*  397 */       if (this.wrappedStmt != null) {
/*  398 */         return ((CallableStatement)this.wrappedStmt).getBlob(parameterIndex);
/*      */       }
/*  400 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  403 */     catch (SQLException sqlEx) {
/*  404 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  407 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Clob getClob(int parameterIndex) throws SQLException {
/*      */     try {
/*  413 */       if (this.wrappedStmt != null) {
/*  414 */         return ((CallableStatement)this.wrappedStmt).getClob(parameterIndex);
/*      */       }
/*  416 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  419 */     catch (SQLException sqlEx) {
/*  420 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  422 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Array getArray(int parameterIndex) throws SQLException {
/*      */     try {
/*  428 */       if (this.wrappedStmt != null) {
/*  429 */         return ((CallableStatement)this.wrappedStmt).getArray(parameterIndex);
/*      */       }
/*  431 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  434 */     catch (SQLException sqlEx) {
/*  435 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  437 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
/*      */     try {
/*  443 */       if (this.wrappedStmt != null) {
/*  444 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterIndex, cal);
/*      */       }
/*  446 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  449 */     catch (SQLException sqlEx) {
/*  450 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  452 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
/*      */     try {
/*  458 */       if (this.wrappedStmt != null) {
/*  459 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterIndex, cal);
/*      */       }
/*  461 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  464 */     catch (SQLException sqlEx) {
/*  465 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  467 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
/*      */     try {
/*  473 */       if (this.wrappedStmt != null) {
/*  474 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterIndex, cal);
/*      */       }
/*  476 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  479 */     catch (SQLException sqlEx) {
/*  480 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  482 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
/*      */     try {
/*  488 */       if (this.wrappedStmt != null) {
/*  489 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(paramIndex, sqlType, typeName);
/*      */       } else {
/*  491 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  494 */     } catch (SQLException sqlEx) {
/*  495 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
/*      */     try {
/*  502 */       if (this.wrappedStmt != null) {
/*  503 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType);
/*      */       } else {
/*  505 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  508 */     } catch (SQLException sqlEx) {
/*  509 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
/*      */     try {
/*  516 */       if (this.wrappedStmt != null) {
/*  517 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, scale);
/*      */       } else {
/*  519 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  522 */     } catch (SQLException sqlEx) {
/*  523 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
/*      */     try {
/*  530 */       if (this.wrappedStmt != null) {
/*  531 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, typeName);
/*      */       } else {
/*  533 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  536 */     } catch (SQLException sqlEx) {
/*  537 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public URL getURL(int parameterIndex) throws SQLException {
/*      */     try {
/*  544 */       if (this.wrappedStmt != null) {
/*  545 */         return ((CallableStatement)this.wrappedStmt).getURL(parameterIndex);
/*      */       }
/*  547 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  550 */     catch (SQLException sqlEx) {
/*  551 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  554 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setURL(String parameterName, URL val) throws SQLException {
/*      */     try {
/*  560 */       if (this.wrappedStmt != null) {
/*  561 */         ((CallableStatement)this.wrappedStmt).setURL(parameterName, val);
/*      */       } else {
/*  563 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  566 */     } catch (SQLException sqlEx) {
/*  567 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNull(String parameterName, int sqlType) throws SQLException {
/*      */     try {
/*  574 */       if (this.wrappedStmt != null) {
/*  575 */         ((CallableStatement)this.wrappedStmt).setNull(parameterName, sqlType);
/*      */       } else {
/*  577 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  580 */     } catch (SQLException sqlEx) {
/*  581 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBoolean(String parameterName, boolean x) throws SQLException {
/*      */     try {
/*  588 */       if (this.wrappedStmt != null) {
/*  589 */         ((CallableStatement)this.wrappedStmt).setBoolean(parameterName, x);
/*      */       } else {
/*  591 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  594 */     } catch (SQLException sqlEx) {
/*  595 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setByte(String parameterName, byte x) throws SQLException {
/*      */     try {
/*  602 */       if (this.wrappedStmt != null) {
/*  603 */         ((CallableStatement)this.wrappedStmt).setByte(parameterName, x);
/*      */       } else {
/*  605 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  608 */     } catch (SQLException sqlEx) {
/*  609 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setShort(String parameterName, short x) throws SQLException {
/*      */     try {
/*  616 */       if (this.wrappedStmt != null) {
/*  617 */         ((CallableStatement)this.wrappedStmt).setShort(parameterName, x);
/*      */       } else {
/*  619 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  622 */     } catch (SQLException sqlEx) {
/*  623 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setInt(String parameterName, int x) throws SQLException {
/*      */     try {
/*  630 */       if (this.wrappedStmt != null) {
/*  631 */         ((CallableStatement)this.wrappedStmt).setInt(parameterName, x);
/*      */       } else {
/*  633 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  636 */     } catch (SQLException sqlEx) {
/*  637 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setLong(String parameterName, long x) throws SQLException {
/*      */     try {
/*  644 */       if (this.wrappedStmt != null) {
/*  645 */         ((CallableStatement)this.wrappedStmt).setLong(parameterName, x);
/*      */       } else {
/*  647 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  650 */     } catch (SQLException sqlEx) {
/*  651 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setFloat(String parameterName, float x) throws SQLException {
/*      */     try {
/*  658 */       if (this.wrappedStmt != null) {
/*  659 */         ((CallableStatement)this.wrappedStmt).setFloat(parameterName, x);
/*      */       } else {
/*  661 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  664 */     } catch (SQLException sqlEx) {
/*  665 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setDouble(String parameterName, double x) throws SQLException {
/*      */     try {
/*  672 */       if (this.wrappedStmt != null) {
/*  673 */         ((CallableStatement)this.wrappedStmt).setDouble(parameterName, x);
/*      */       } else {
/*  675 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  678 */     } catch (SQLException sqlEx) {
/*  679 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
/*      */     try {
/*  686 */       if (this.wrappedStmt != null) {
/*  687 */         ((CallableStatement)this.wrappedStmt).setBigDecimal(parameterName, x);
/*      */       } else {
/*  689 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  692 */     } catch (SQLException sqlEx) {
/*  693 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setString(String parameterName, String x) throws SQLException {
/*      */     try {
/*  700 */       if (this.wrappedStmt != null) {
/*  701 */         ((CallableStatement)this.wrappedStmt).setString(parameterName, x);
/*      */       } else {
/*  703 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  706 */     } catch (SQLException sqlEx) {
/*  707 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBytes(String parameterName, byte[] x) throws SQLException {
/*      */     try {
/*  714 */       if (this.wrappedStmt != null) {
/*  715 */         ((CallableStatement)this.wrappedStmt).setBytes(parameterName, x);
/*      */       } else {
/*  717 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  720 */     } catch (SQLException sqlEx) {
/*  721 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setDate(String parameterName, Date x) throws SQLException {
/*      */     try {
/*  728 */       if (this.wrappedStmt != null) {
/*  729 */         ((CallableStatement)this.wrappedStmt).setDate(parameterName, x);
/*      */       } else {
/*  731 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  734 */     } catch (SQLException sqlEx) {
/*  735 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTime(String parameterName, Time x) throws SQLException {
/*      */     try {
/*  742 */       if (this.wrappedStmt != null) {
/*  743 */         ((CallableStatement)this.wrappedStmt).setTime(parameterName, x);
/*      */       } else {
/*  745 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  748 */     } catch (SQLException sqlEx) {
/*  749 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
/*      */     try {
/*  756 */       if (this.wrappedStmt != null) {
/*  757 */         ((CallableStatement)this.wrappedStmt).setTimestamp(parameterName, x);
/*      */       } else {
/*  759 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  762 */     } catch (SQLException sqlEx) {
/*  763 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
/*      */     try {
/*  770 */       if (this.wrappedStmt != null) {
/*  771 */         ((CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x, length);
/*      */       } else {
/*  773 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  776 */     } catch (SQLException sqlEx) {
/*  777 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
/*      */     try {
/*  785 */       if (this.wrappedStmt != null) {
/*  786 */         ((CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x, length);
/*      */       } else {
/*  788 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  791 */     } catch (SQLException sqlEx) {
/*  792 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
/*      */     try {
/*  799 */       if (this.wrappedStmt != null) {
/*  800 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType, scale);
/*      */       } else {
/*  802 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  805 */     } catch (SQLException sqlEx) {
/*  806 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
/*      */     try {
/*  813 */       if (this.wrappedStmt != null) {
/*  814 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType);
/*      */       } else {
/*  816 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  819 */     } catch (SQLException sqlEx) {
/*  820 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setObject(String parameterName, Object x) throws SQLException {
/*      */     try {
/*  827 */       if (this.wrappedStmt != null) {
/*  828 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x);
/*      */       } else {
/*  830 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  833 */     } catch (SQLException sqlEx) {
/*  834 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
/*      */     try {
/*  841 */       if (this.wrappedStmt != null) {
/*  842 */         ((CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader, length);
/*      */       } else {
/*  844 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  847 */     } catch (SQLException sqlEx) {
/*  848 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
/*      */     try {
/*  855 */       if (this.wrappedStmt != null) {
/*  856 */         ((CallableStatement)this.wrappedStmt).setDate(parameterName, x, cal);
/*      */       } else {
/*  858 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  861 */     } catch (SQLException sqlEx) {
/*  862 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
/*      */     try {
/*  869 */       if (this.wrappedStmt != null) {
/*  870 */         ((CallableStatement)this.wrappedStmt).setTime(parameterName, x, cal);
/*      */       } else {
/*  872 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  875 */     } catch (SQLException sqlEx) {
/*  876 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
/*      */     try {
/*  883 */       if (this.wrappedStmt != null) {
/*  884 */         ((CallableStatement)this.wrappedStmt).setTimestamp(parameterName, x, cal);
/*      */       } else {
/*  886 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  889 */     } catch (SQLException sqlEx) {
/*  890 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
/*      */     try {
/*  897 */       if (this.wrappedStmt != null) {
/*  898 */         ((CallableStatement)this.wrappedStmt).setNull(parameterName, sqlType, typeName);
/*      */       } else {
/*  900 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/*  903 */     } catch (SQLException sqlEx) {
/*  904 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public String getString(String parameterName) throws SQLException {
/*      */     try {
/*  911 */       if (this.wrappedStmt != null) {
/*  912 */         return ((CallableStatement)this.wrappedStmt).getString(parameterName);
/*      */       }
/*  914 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  917 */     catch (SQLException sqlEx) {
/*  918 */       checkAndFireConnectionError(sqlEx);
/*      */       
/*  920 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean getBoolean(String parameterName) throws SQLException {
/*      */     try {
/*  926 */       if (this.wrappedStmt != null) {
/*  927 */         return ((CallableStatement)this.wrappedStmt).getBoolean(parameterName);
/*      */       }
/*  929 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  932 */     catch (SQLException sqlEx) {
/*  933 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  936 */       return false;
/*      */     } 
/*      */   }
/*      */   
/*      */   public byte getByte(String parameterName) throws SQLException {
/*      */     try {
/*  942 */       if (this.wrappedStmt != null) {
/*  943 */         return ((CallableStatement)this.wrappedStmt).getByte(parameterName);
/*      */       }
/*  945 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  948 */     catch (SQLException sqlEx) {
/*  949 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  952 */       return 0;
/*      */     } 
/*      */   }
/*      */   
/*      */   public short getShort(String parameterName) throws SQLException {
/*      */     try {
/*  958 */       if (this.wrappedStmt != null) {
/*  959 */         return ((CallableStatement)this.wrappedStmt).getShort(parameterName);
/*      */       }
/*  961 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  964 */     catch (SQLException sqlEx) {
/*  965 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  968 */       return 0;
/*      */     } 
/*      */   }
/*      */   
/*      */   public int getInt(String parameterName) throws SQLException {
/*      */     try {
/*  974 */       if (this.wrappedStmt != null) {
/*  975 */         return ((CallableStatement)this.wrappedStmt).getInt(parameterName);
/*      */       }
/*  977 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  980 */     catch (SQLException sqlEx) {
/*  981 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/*  984 */       return 0;
/*      */     } 
/*      */   }
/*      */   
/*      */   public long getLong(String parameterName) throws SQLException {
/*      */     try {
/*  990 */       if (this.wrappedStmt != null) {
/*  991 */         return ((CallableStatement)this.wrappedStmt).getLong(parameterName);
/*      */       }
/*  993 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/*  996 */     catch (SQLException sqlEx) {
/*  997 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1000 */       return 0L;
/*      */     } 
/*      */   }
/*      */   
/*      */   public float getFloat(String parameterName) throws SQLException {
/*      */     try {
/* 1006 */       if (this.wrappedStmt != null) {
/* 1007 */         return ((CallableStatement)this.wrappedStmt).getFloat(parameterName);
/*      */       }
/* 1009 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1012 */     catch (SQLException sqlEx) {
/* 1013 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1016 */       return 0.0F;
/*      */     } 
/*      */   }
/*      */   
/*      */   public double getDouble(String parameterName) throws SQLException {
/*      */     try {
/* 1022 */       if (this.wrappedStmt != null) {
/* 1023 */         return ((CallableStatement)this.wrappedStmt).getDouble(parameterName);
/*      */       }
/* 1025 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1028 */     catch (SQLException sqlEx) {
/* 1029 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1032 */       return 0.0D;
/*      */     } 
/*      */   }
/*      */   
/*      */   public byte[] getBytes(String parameterName) throws SQLException {
/*      */     try {
/* 1038 */       if (this.wrappedStmt != null) {
/* 1039 */         return ((CallableStatement)this.wrappedStmt).getBytes(parameterName);
/*      */       }
/* 1041 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1044 */     catch (SQLException sqlEx) {
/* 1045 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1048 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Date getDate(String parameterName) throws SQLException {
/*      */     try {
/* 1054 */       if (this.wrappedStmt != null) {
/* 1055 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterName);
/*      */       }
/* 1057 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1060 */     catch (SQLException sqlEx) {
/* 1061 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1064 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Time getTime(String parameterName) throws SQLException {
/*      */     try {
/* 1070 */       if (this.wrappedStmt != null) {
/* 1071 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterName);
/*      */       }
/* 1073 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1076 */     catch (SQLException sqlEx) {
/* 1077 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1080 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Timestamp getTimestamp(String parameterName) throws SQLException {
/*      */     try {
/* 1086 */       if (this.wrappedStmt != null) {
/* 1087 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterName);
/*      */       }
/* 1089 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1092 */     catch (SQLException sqlEx) {
/* 1093 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1096 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Object getObject(String parameterName) throws SQLException {
/*      */     try {
/* 1102 */       if (this.wrappedStmt != null) {
/* 1103 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterName);
/*      */       }
/* 1105 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1108 */     catch (SQLException sqlEx) {
/* 1109 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1112 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public BigDecimal getBigDecimal(String parameterName) throws SQLException {
/*      */     try {
/* 1118 */       if (this.wrappedStmt != null) {
/* 1119 */         return ((CallableStatement)this.wrappedStmt).getBigDecimal(parameterName);
/*      */       }
/* 1121 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1124 */     catch (SQLException sqlEx) {
/* 1125 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1128 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Object getObject(String parameterName, Map<String, Class<?>> typeMap) throws SQLException {
/*      */     try {
/* 1134 */       if (this.wrappedStmt != null) {
/* 1135 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterName, typeMap);
/*      */       }
/* 1137 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1140 */     catch (SQLException sqlEx) {
/* 1141 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1143 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Ref getRef(String parameterName) throws SQLException {
/*      */     try {
/* 1149 */       if (this.wrappedStmt != null) {
/* 1150 */         return ((CallableStatement)this.wrappedStmt).getRef(parameterName);
/*      */       }
/* 1152 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1155 */     catch (SQLException sqlEx) {
/* 1156 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1159 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Blob getBlob(String parameterName) throws SQLException {
/*      */     try {
/* 1165 */       if (this.wrappedStmt != null) {
/* 1166 */         return ((CallableStatement)this.wrappedStmt).getBlob(parameterName);
/*      */       }
/* 1168 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1171 */     catch (SQLException sqlEx) {
/* 1172 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1175 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Clob getClob(String parameterName) throws SQLException {
/*      */     try {
/* 1181 */       if (this.wrappedStmt != null) {
/* 1182 */         return ((CallableStatement)this.wrappedStmt).getClob(parameterName);
/*      */       }
/* 1184 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1187 */     catch (SQLException sqlEx) {
/* 1188 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1190 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Array getArray(String parameterName) throws SQLException {
/*      */     try {
/* 1196 */       if (this.wrappedStmt != null) {
/* 1197 */         return ((CallableStatement)this.wrappedStmt).getArray(parameterName);
/*      */       }
/* 1199 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1202 */     catch (SQLException sqlEx) {
/* 1203 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1205 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Date getDate(String parameterName, Calendar cal) throws SQLException {
/*      */     try {
/* 1211 */       if (this.wrappedStmt != null) {
/* 1212 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterName, cal);
/*      */       }
/* 1214 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1217 */     catch (SQLException sqlEx) {
/* 1218 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1220 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Time getTime(String parameterName, Calendar cal) throws SQLException {
/*      */     try {
/* 1226 */       if (this.wrappedStmt != null) {
/* 1227 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterName, cal);
/*      */       }
/* 1229 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1232 */     catch (SQLException sqlEx) {
/* 1233 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1235 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
/*      */     try {
/* 1241 */       if (this.wrappedStmt != null) {
/* 1242 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterName, cal);
/*      */       }
/* 1244 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1247 */     catch (SQLException sqlEx) {
/* 1248 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1250 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public URL getURL(String parameterName) throws SQLException {
/*      */     try {
/* 1256 */       if (this.wrappedStmt != null) {
/* 1257 */         return ((CallableStatement)this.wrappedStmt).getURL(parameterName);
/*      */       }
/* 1259 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     
/*      */     }
/* 1262 */     catch (SQLException sqlEx) {
/* 1263 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1266 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public RowId getRowId(String parameterName) throws SQLException {
/*      */     try {
/* 1272 */       if (this.wrappedStmt != null) {
/* 1273 */         return ((CallableStatement)this.wrappedStmt).getRowId(parameterName);
/*      */       }
/* 1275 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     }
/* 1277 */     catch (SQLException sqlEx) {
/* 1278 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1281 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public RowId getRowId(int parameterIndex) throws SQLException {
/*      */     try {
/* 1287 */       if (this.wrappedStmt != null) {
/* 1288 */         return ((CallableStatement)this.wrappedStmt).getRowId(parameterIndex);
/*      */       }
/* 1290 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     }
/* 1292 */     catch (SQLException sqlEx) {
/* 1293 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1296 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setRowId(String parameterName, RowId x) throws SQLException {
/*      */     try {
/* 1302 */       if (this.wrappedStmt != null) {
/* 1303 */         ((CallableStatement)this.wrappedStmt).setRowId(parameterName, x);
/*      */       } else {
/* 1305 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1308 */     } catch (SQLException sqlEx) {
/* 1309 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNString(String parameterName, String value) throws SQLException {
/*      */     try {
/* 1316 */       if (this.wrappedStmt != null) {
/* 1317 */         ((CallableStatement)this.wrappedStmt).setNString(parameterName, value);
/*      */       } else {
/* 1319 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1322 */     } catch (SQLException sqlEx) {
/* 1323 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/* 1330 */       if (this.wrappedStmt != null) {
/* 1331 */         ((CallableStatement)this.wrappedStmt).setNCharacterStream(parameterName, reader, length);
/*      */       } else {
/* 1333 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1336 */     } catch (SQLException sqlEx) {
/* 1337 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNClob(String parameterName, NClob value) throws SQLException {
/*      */     try {
/* 1344 */       if (this.wrappedStmt != null) {
/* 1345 */         ((CallableStatement)this.wrappedStmt).setNClob(parameterName, value);
/*      */       } else {
/* 1347 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1350 */     } catch (SQLException sqlEx) {
/* 1351 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setClob(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/* 1358 */       if (this.wrappedStmt != null) {
/* 1359 */         ((CallableStatement)this.wrappedStmt).setClob(parameterName, reader, length);
/*      */       } else {
/* 1361 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1364 */     } catch (SQLException sqlEx) {
/* 1365 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBlob(String parameterName, InputStream x, long length) throws SQLException {
/*      */     try {
/* 1372 */       if (this.wrappedStmt != null) {
/* 1373 */         ((CallableStatement)this.wrappedStmt).setBlob(parameterName, x, length);
/*      */       } else {
/* 1375 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1378 */     } catch (SQLException sqlEx) {
/* 1379 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/* 1386 */       if (this.wrappedStmt != null) {
/* 1387 */         ((CallableStatement)this.wrappedStmt).setNClob(parameterName, reader, length);
/*      */       } else {
/* 1389 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1392 */     } catch (SQLException sqlEx) {
/* 1393 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public NClob getNClob(String parameterName) throws SQLException {
/*      */     try {
/* 1400 */       if (this.wrappedStmt != null) {
/* 1401 */         return ((CallableStatement)this.wrappedStmt).getNClob(parameterName);
/*      */       }
/* 1403 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     }
/* 1405 */     catch (SQLException sqlEx) {
/* 1406 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1409 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public NClob getNClob(int parameterIndex) throws SQLException {
/*      */     try {
/* 1415 */       if (this.wrappedStmt != null) {
/* 1416 */         return ((CallableStatement)this.wrappedStmt).getNClob(parameterIndex);
/*      */       }
/* 1418 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     }
/* 1420 */     catch (SQLException sqlEx) {
/* 1421 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1424 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
/*      */     try {
/* 1430 */       if (this.wrappedStmt != null) {
/* 1431 */         ((CallableStatement)this.wrappedStmt).setSQLXML(parameterName, xmlObject);
/*      */       } else {
/* 1433 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1436 */     } catch (SQLException sqlEx) {
/* 1437 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public SQLXML getSQLXML(int parameterIndex) throws SQLException {
/*      */     try {
/* 1444 */       if (this.wrappedStmt != null) {
/* 1445 */         return ((CallableStatement)this.wrappedStmt).getSQLXML(parameterIndex);
/*      */       }
/* 1447 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     }
/* 1449 */     catch (SQLException sqlEx) {
/* 1450 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1452 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public SQLXML getSQLXML(String parameterName) throws SQLException {
/*      */     try {
/* 1458 */       if (this.wrappedStmt != null) {
/* 1459 */         return ((CallableStatement)this.wrappedStmt).getSQLXML(parameterName);
/*      */       }
/* 1461 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     }
/* 1463 */     catch (SQLException sqlEx) {
/* 1464 */       checkAndFireConnectionError(sqlEx);
/*      */       
/* 1466 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public String getNString(int parameterIndex) throws SQLException {
/*      */     try {
/* 1472 */       if (this.wrappedStmt != null) {
/* 1473 */         return ((CallableStatement)this.wrappedStmt).getNString(parameterIndex);
/*      */       }
/* 1475 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     }
/* 1477 */     catch (SQLException sqlEx) {
/* 1478 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1481 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public String getNString(String parameterName) throws SQLException {
/*      */     try {
/* 1487 */       if (this.wrappedStmt != null) {
/* 1488 */         return ((CallableStatement)this.wrappedStmt).getNString(parameterName);
/*      */       }
/* 1490 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     }
/* 1492 */     catch (SQLException sqlEx) {
/* 1493 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1496 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Reader getNCharacterStream(int parameterIndex) throws SQLException {
/*      */     try {
/* 1502 */       if (this.wrappedStmt != null) {
/* 1503 */         return ((CallableStatement)this.wrappedStmt).getNCharacterStream(parameterIndex);
/*      */       }
/* 1505 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     }
/* 1507 */     catch (SQLException sqlEx) {
/* 1508 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1511 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Reader getNCharacterStream(String parameterName) throws SQLException {
/*      */     try {
/* 1517 */       if (this.wrappedStmt != null) {
/* 1518 */         return ((CallableStatement)this.wrappedStmt).getNCharacterStream(parameterName);
/*      */       }
/* 1520 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     }
/* 1522 */     catch (SQLException sqlEx) {
/* 1523 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1526 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Reader getCharacterStream(int parameterIndex) throws SQLException {
/*      */     try {
/* 1532 */       if (this.wrappedStmt != null) {
/* 1533 */         return ((CallableStatement)this.wrappedStmt).getCharacterStream(parameterIndex);
/*      */       }
/* 1535 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     }
/* 1537 */     catch (SQLException sqlEx) {
/* 1538 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1541 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public Reader getCharacterStream(String parameterName) throws SQLException {
/*      */     try {
/* 1547 */       if (this.wrappedStmt != null) {
/* 1548 */         return ((CallableStatement)this.wrappedStmt).getCharacterStream(parameterName);
/*      */       }
/* 1550 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */     }
/* 1552 */     catch (SQLException sqlEx) {
/* 1553 */       checkAndFireConnectionError(sqlEx);
/*      */ 
/*      */       
/* 1556 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setBlob(String parameterName, Blob x) throws SQLException {
/*      */     try {
/* 1562 */       if (this.wrappedStmt != null) {
/* 1563 */         ((CallableStatement)this.wrappedStmt).setBlob(parameterName, x);
/*      */       } else {
/* 1565 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1568 */     } catch (SQLException sqlEx) {
/* 1569 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setClob(String parameterName, Clob x) throws SQLException {
/*      */     try {
/* 1576 */       if (this.wrappedStmt != null) {
/* 1577 */         ((CallableStatement)this.wrappedStmt).setClob(parameterName, x);
/*      */       } else {
/* 1579 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1582 */     } catch (SQLException sqlEx) {
/* 1583 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
/*      */     try {
/* 1590 */       if (this.wrappedStmt != null) {
/* 1591 */         ((CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x, length);
/*      */       } else {
/* 1593 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1596 */     } catch (SQLException sqlEx) {
/* 1597 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
/*      */     try {
/* 1604 */       if (this.wrappedStmt != null) {
/* 1605 */         ((CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x, length);
/*      */       } else {
/* 1607 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1610 */     } catch (SQLException sqlEx) {
/* 1611 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/* 1618 */       if (this.wrappedStmt != null) {
/* 1619 */         ((CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader, length);
/*      */       } else {
/* 1621 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1624 */     } catch (SQLException sqlEx) {
/* 1625 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
/*      */     try {
/* 1632 */       if (this.wrappedStmt != null) {
/* 1633 */         ((CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x);
/*      */       } else {
/* 1635 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1638 */     } catch (SQLException sqlEx) {
/* 1639 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
/*      */     try {
/* 1646 */       if (this.wrappedStmt != null) {
/* 1647 */         ((CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x);
/*      */       } else {
/* 1649 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1652 */     } catch (SQLException sqlEx) {
/* 1653 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/* 1660 */       if (this.wrappedStmt != null) {
/* 1661 */         ((CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader);
/*      */       } else {
/* 1663 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1666 */     } catch (SQLException sqlEx) {
/* 1667 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNCharacterStream(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/* 1674 */       if (this.wrappedStmt != null) {
/* 1675 */         ((CallableStatement)this.wrappedStmt).setNCharacterStream(parameterName, reader);
/*      */       } else {
/* 1677 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1680 */     } catch (SQLException sqlEx) {
/* 1681 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setClob(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/* 1688 */       if (this.wrappedStmt != null) {
/* 1689 */         ((CallableStatement)this.wrappedStmt).setClob(parameterName, reader);
/*      */       } else {
/* 1691 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1694 */     } catch (SQLException sqlEx) {
/* 1695 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBlob(String parameterName, InputStream x) throws SQLException {
/*      */     try {
/* 1702 */       if (this.wrappedStmt != null) {
/* 1703 */         ((CallableStatement)this.wrappedStmt).setBlob(parameterName, x);
/*      */       } else {
/* 1705 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1708 */     } catch (SQLException sqlEx) {
/* 1709 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNClob(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/* 1716 */       if (this.wrappedStmt != null) {
/* 1717 */         ((CallableStatement)this.wrappedStmt).setNClob(parameterName, reader);
/*      */       } else {
/* 1719 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1722 */     } catch (SQLException sqlEx) {
/* 1723 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
/* 1729 */     if (this.wrappedStmt != null) {
/* 1730 */       return null;
/*      */     }
/* 1732 */     throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
/* 1737 */     if (this.wrappedStmt != null) {
/* 1738 */       return null;
/*      */     }
/* 1740 */     throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 1746 */     boolean isInstance = iface.isInstance(this);
/*      */     
/* 1748 */     if (isInstance) {
/* 1749 */       return true;
/*      */     }
/*      */     
/* 1752 */     String interfaceClassName = iface.getName();
/*      */     
/* 1754 */     return (interfaceClassName.equals("com.mysql.cj.jdbc.Statement") || interfaceClassName.equals("java.sql.Statement") || interfaceClassName
/* 1755 */       .equals("java.sql.Wrapper") || interfaceClassName.equals("java.sql.PreparedStatement") || interfaceClassName
/* 1756 */       .equals("java.sql.CallableStatement"));
/*      */   }
/*      */ 
/*      */   
/*      */   public void close() throws SQLException {
/*      */     try {
/* 1762 */       super.close();
/*      */     } finally {
/* 1764 */       this.unwrappedInterfaces = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
/*      */     try {
/* 1771 */       if ("java.sql.Statement".equals(iface.getName()) || "java.sql.CallableStatement".equals(iface.getName()) || "java.sql.PreparedStatement"
/* 1772 */         .equals(iface.getName()) || "java.sql.Wrapper.class".equals(iface.getName())) {
/* 1773 */         return iface.cast(this);
/*      */       }
/*      */       
/* 1776 */       if (this.unwrappedInterfaces == null) {
/* 1777 */         this.unwrappedInterfaces = new HashMap<>();
/*      */       }
/*      */       
/* 1780 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*      */       
/* 1782 */       if (cachedUnwrapped == null) {
/* 1783 */         cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
/*      */         
/* 1785 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*      */       } 
/*      */       
/* 1788 */       return iface.cast(cachedUnwrapped);
/* 1789 */     } catch (ClassCastException cce) {
/* 1790 */       throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", this.exceptionInterceptor);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
/*      */     try {
/* 1798 */       if (this.wrappedStmt != null) {
/* 1799 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType);
/*      */       } else {
/* 1801 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1804 */     } catch (SQLException sqlEx) {
/* 1805 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
/*      */     try {
/* 1812 */       if (this.wrappedStmt != null) {
/* 1813 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType, scale);
/*      */       } else {
/* 1815 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1818 */     } catch (SQLException sqlEx) {
/* 1819 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
/*      */     try {
/* 1826 */       if (this.wrappedStmt != null) {
/* 1827 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType, typeName);
/*      */       } else {
/* 1829 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1832 */     } catch (SQLException sqlEx) {
/* 1833 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
/*      */     try {
/* 1840 */       if (this.wrappedStmt != null) {
/* 1841 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType);
/*      */       } else {
/* 1843 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1846 */     } catch (SQLException sqlEx) {
/* 1847 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
/*      */     try {
/* 1854 */       if (this.wrappedStmt != null) {
/* 1855 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, scale);
/*      */       } else {
/* 1857 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1860 */     } catch (SQLException sqlEx) {
/* 1861 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
/*      */     try {
/* 1868 */       if (this.wrappedStmt != null) {
/* 1869 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, typeName);
/*      */       } else {
/* 1871 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1874 */     } catch (SQLException sqlEx) {
/* 1875 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
/*      */     try {
/* 1882 */       if (this.wrappedStmt != null) {
/* 1883 */         ((CallableStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType);
/*      */       } else {
/* 1885 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1888 */     } catch (SQLException sqlEx) {
/* 1889 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
/*      */     try {
/* 1896 */       if (this.wrappedStmt != null) {
/* 1897 */         ((CallableStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType, scaleOrLength);
/*      */       } else {
/* 1899 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1902 */     } catch (SQLException sqlEx) {
/* 1903 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
/*      */     try {
/* 1910 */       if (this.wrappedStmt != null) {
/* 1911 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType);
/*      */       } else {
/* 1913 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1916 */     } catch (SQLException sqlEx) {
/* 1917 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
/*      */     try {
/* 1924 */       if (this.wrappedStmt != null) {
/* 1925 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType, scaleOrLength);
/*      */       } else {
/* 1927 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     
/* 1930 */     } catch (SQLException sqlEx) {
/* 1931 */       checkAndFireConnectionError(sqlEx);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\CallableStatementWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */