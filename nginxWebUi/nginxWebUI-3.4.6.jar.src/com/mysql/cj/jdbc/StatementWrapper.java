/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.sql.Statement;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */   extends WrapperBase
/*     */   implements Statement
/*     */ {
/*     */   protected Statement wrappedStmt;
/*     */   protected ConnectionWrapper wrappedConn;
/*     */   
/*     */   protected static StatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap) throws SQLException {
/*  50 */     return new StatementWrapper(c, conn, toWrap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap) {
/*  58 */     super(conn);
/*  59 */     this.wrappedStmt = toWrap;
/*  60 */     this.wrappedConn = c;
/*     */   }
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/*     */     try {
/*  66 */       if (this.wrappedStmt != null) {
/*  67 */         return this.wrappedConn;
/*     */       }
/*     */       
/*  70 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/*  72 */     catch (SQLException sqlEx) {
/*  73 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/*  76 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setCursorName(String name) throws SQLException {
/*     */     try {
/*  82 */       if (this.wrappedStmt != null) {
/*  83 */         this.wrappedStmt.setCursorName(name);
/*     */       } else {
/*  85 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/*  88 */     } catch (SQLException sqlEx) {
/*  89 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEscapeProcessing(boolean enable) throws SQLException {
/*     */     try {
/*  96 */       if (this.wrappedStmt != null) {
/*  97 */         this.wrappedStmt.setEscapeProcessing(enable);
/*     */       } else {
/*  99 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 102 */     } catch (SQLException sqlEx) {
/* 103 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFetchDirection(int direction) throws SQLException {
/*     */     try {
/* 110 */       if (this.wrappedStmt != null) {
/* 111 */         this.wrappedStmt.setFetchDirection(direction);
/*     */       } else {
/* 113 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 116 */     } catch (SQLException sqlEx) {
/* 117 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFetchDirection() throws SQLException {
/*     */     try {
/* 124 */       if (this.wrappedStmt != null) {
/* 125 */         return this.wrappedStmt.getFetchDirection();
/*     */       }
/*     */       
/* 128 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 130 */     catch (SQLException sqlEx) {
/* 131 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 134 */       return 1000;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setFetchSize(int rows) throws SQLException {
/*     */     try {
/* 140 */       if (this.wrappedStmt != null) {
/* 141 */         this.wrappedStmt.setFetchSize(rows);
/*     */       } else {
/* 143 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 146 */     } catch (SQLException sqlEx) {
/* 147 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFetchSize() throws SQLException {
/*     */     try {
/* 154 */       if (this.wrappedStmt != null) {
/* 155 */         return this.wrappedStmt.getFetchSize();
/*     */       }
/*     */       
/* 158 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 160 */     catch (SQLException sqlEx) {
/* 161 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 164 */       return 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public ResultSet getGeneratedKeys() throws SQLException {
/*     */     try {
/* 170 */       if (this.wrappedStmt != null) {
/* 171 */         return this.wrappedStmt.getGeneratedKeys();
/*     */       }
/*     */       
/* 174 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 176 */     catch (SQLException sqlEx) {
/* 177 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 180 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setMaxFieldSize(int max) throws SQLException {
/*     */     try {
/* 186 */       if (this.wrappedStmt != null) {
/* 187 */         this.wrappedStmt.setMaxFieldSize(max);
/*     */       } else {
/* 189 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 192 */     } catch (SQLException sqlEx) {
/* 193 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxFieldSize() throws SQLException {
/*     */     try {
/* 200 */       if (this.wrappedStmt != null) {
/* 201 */         return this.wrappedStmt.getMaxFieldSize();
/*     */       }
/*     */       
/* 204 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 206 */     catch (SQLException sqlEx) {
/* 207 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 210 */       return 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setMaxRows(int max) throws SQLException {
/*     */     try {
/* 216 */       if (this.wrappedStmt != null) {
/* 217 */         this.wrappedStmt.setMaxRows(max);
/*     */       } else {
/* 219 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 222 */     } catch (SQLException sqlEx) {
/* 223 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxRows() throws SQLException {
/*     */     try {
/* 230 */       if (this.wrappedStmt != null) {
/* 231 */         return this.wrappedStmt.getMaxRows();
/*     */       }
/*     */       
/* 234 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 236 */     catch (SQLException sqlEx) {
/* 237 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 240 */       return 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean getMoreResults() throws SQLException {
/*     */     try {
/* 246 */       if (this.wrappedStmt != null) {
/* 247 */         return this.wrappedStmt.getMoreResults();
/*     */       }
/*     */       
/* 250 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 252 */     catch (SQLException sqlEx) {
/* 253 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 256 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean getMoreResults(int current) throws SQLException {
/*     */     try {
/* 262 */       if (this.wrappedStmt != null) {
/* 263 */         return this.wrappedStmt.getMoreResults(current);
/*     */       }
/*     */       
/* 266 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 268 */     catch (SQLException sqlEx) {
/* 269 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 272 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setQueryTimeout(int seconds) throws SQLException {
/*     */     try {
/* 278 */       if (this.wrappedStmt != null) {
/* 279 */         this.wrappedStmt.setQueryTimeout(seconds);
/*     */       } else {
/* 281 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 284 */     } catch (SQLException sqlEx) {
/* 285 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getQueryTimeout() throws SQLException {
/*     */     try {
/* 292 */       if (this.wrappedStmt != null) {
/* 293 */         return this.wrappedStmt.getQueryTimeout();
/*     */       }
/*     */       
/* 296 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 298 */     catch (SQLException sqlEx) {
/* 299 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 302 */       return 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public ResultSet getResultSet() throws SQLException {
/*     */     try {
/* 308 */       if (this.wrappedStmt != null) {
/* 309 */         ResultSet rs = this.wrappedStmt.getResultSet();
/*     */         
/* 311 */         if (rs != null) {
/* 312 */           ((ResultSetInternalMethods)rs).setWrapperStatement(this);
/*     */         }
/* 314 */         return rs;
/*     */       } 
/*     */       
/* 317 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 319 */     catch (SQLException sqlEx) {
/* 320 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 323 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getResultSetConcurrency() throws SQLException {
/*     */     try {
/* 329 */       if (this.wrappedStmt != null) {
/* 330 */         return this.wrappedStmt.getResultSetConcurrency();
/*     */       }
/*     */       
/* 333 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 335 */     catch (SQLException sqlEx) {
/* 336 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 339 */       return 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getResultSetHoldability() throws SQLException {
/*     */     try {
/* 345 */       if (this.wrappedStmt != null) {
/* 346 */         return this.wrappedStmt.getResultSetHoldability();
/*     */       }
/*     */       
/* 349 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 351 */     catch (SQLException sqlEx) {
/* 352 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 355 */       return 1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getResultSetType() throws SQLException {
/*     */     try {
/* 361 */       if (this.wrappedStmt != null) {
/* 362 */         return this.wrappedStmt.getResultSetType();
/*     */       }
/*     */       
/* 365 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 367 */     catch (SQLException sqlEx) {
/* 368 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 371 */       return 1003;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getUpdateCount() throws SQLException {
/*     */     try {
/* 377 */       if (this.wrappedStmt != null) {
/* 378 */         return this.wrappedStmt.getUpdateCount();
/*     */       }
/*     */       
/* 381 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 383 */     catch (SQLException sqlEx) {
/* 384 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 387 */       return -1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public SQLWarning getWarnings() throws SQLException {
/*     */     try {
/* 393 */       if (this.wrappedStmt != null) {
/* 394 */         return this.wrappedStmt.getWarnings();
/*     */       }
/*     */       
/* 397 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 399 */     catch (SQLException sqlEx) {
/* 400 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 403 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addBatch(String sql) throws SQLException {
/*     */     try {
/* 409 */       if (this.wrappedStmt != null) {
/* 410 */         this.wrappedStmt.addBatch(sql);
/*     */       } else {
/* 412 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 415 */     } catch (SQLException sqlEx) {
/* 416 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void cancel() throws SQLException {
/*     */     try {
/* 423 */       if (this.wrappedStmt != null) {
/* 424 */         this.wrappedStmt.cancel();
/*     */       } else {
/* 426 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 429 */     } catch (SQLException sqlEx) {
/* 430 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearBatch() throws SQLException {
/*     */     try {
/* 437 */       if (this.wrappedStmt != null) {
/* 438 */         this.wrappedStmt.clearBatch();
/*     */       } else {
/* 440 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 443 */     } catch (SQLException sqlEx) {
/* 444 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearWarnings() throws SQLException {
/*     */     try {
/* 451 */       if (this.wrappedStmt != null) {
/* 452 */         this.wrappedStmt.clearWarnings();
/*     */       } else {
/* 454 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 457 */     } catch (SQLException sqlEx) {
/* 458 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws SQLException {
/*     */     try {
/* 465 */       if (this.wrappedStmt != null) {
/* 466 */         this.wrappedStmt.close();
/*     */       }
/* 468 */     } catch (SQLException sqlEx) {
/* 469 */       checkAndFireConnectionError(sqlEx);
/*     */     } finally {
/* 471 */       this.wrappedStmt = null;
/* 472 */       this.pooledConnection = null;
/* 473 */       this.unwrappedInterfaces = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
/*     */     try {
/* 480 */       if (this.wrappedStmt != null) {
/* 481 */         return this.wrappedStmt.execute(sql, autoGeneratedKeys);
/*     */       }
/*     */       
/* 484 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 486 */     catch (SQLException sqlEx) {
/* 487 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 490 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean execute(String sql, int[] columnIndexes) throws SQLException {
/*     */     try {
/* 496 */       if (this.wrappedStmt != null) {
/* 497 */         return this.wrappedStmt.execute(sql, columnIndexes);
/*     */       }
/*     */       
/* 500 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 502 */     catch (SQLException sqlEx) {
/* 503 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 506 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean execute(String sql, String[] columnNames) throws SQLException {
/*     */     try {
/* 512 */       if (this.wrappedStmt != null) {
/* 513 */         return this.wrappedStmt.execute(sql, columnNames);
/*     */       }
/*     */       
/* 516 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 518 */     catch (SQLException sqlEx) {
/* 519 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 522 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean execute(String sql) throws SQLException {
/*     */     try {
/* 528 */       if (this.wrappedStmt != null) {
/* 529 */         return this.wrappedStmt.execute(sql);
/*     */       }
/*     */       
/* 532 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 534 */     catch (SQLException sqlEx) {
/* 535 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 538 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int[] executeBatch() throws SQLException {
/*     */     try {
/* 544 */       if (this.wrappedStmt != null) {
/* 545 */         return this.wrappedStmt.executeBatch();
/*     */       }
/*     */       
/* 548 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 550 */     catch (SQLException sqlEx) {
/* 551 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 554 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public ResultSet executeQuery(String sql) throws SQLException {
/* 559 */     ResultSet rs = null;
/*     */     try {
/* 561 */       if (this.wrappedStmt == null) {
/* 562 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */ 
/*     */       
/* 566 */       rs = this.wrappedStmt.executeQuery(sql);
/* 567 */       ((ResultSetInternalMethods)rs).setWrapperStatement(this);
/*     */     }
/* 569 */     catch (SQLException sqlEx) {
/* 570 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */     
/* 573 */     return rs;
/*     */   }
/*     */ 
/*     */   
/*     */   public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
/*     */     try {
/* 579 */       if (this.wrappedStmt != null) {
/* 580 */         return this.wrappedStmt.executeUpdate(sql, autoGeneratedKeys);
/*     */       }
/*     */       
/* 583 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 585 */     catch (SQLException sqlEx) {
/* 586 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 589 */       return -1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
/*     */     try {
/* 595 */       if (this.wrappedStmt != null) {
/* 596 */         return this.wrappedStmt.executeUpdate(sql, columnIndexes);
/*     */       }
/*     */       
/* 599 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 601 */     catch (SQLException sqlEx) {
/* 602 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 605 */       return -1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int executeUpdate(String sql, String[] columnNames) throws SQLException {
/*     */     try {
/* 611 */       if (this.wrappedStmt != null) {
/* 612 */         return this.wrappedStmt.executeUpdate(sql, columnNames);
/*     */       }
/*     */       
/* 615 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 617 */     catch (SQLException sqlEx) {
/* 618 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 621 */       return -1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int executeUpdate(String sql) throws SQLException {
/*     */     try {
/* 627 */       if (this.wrappedStmt != null) {
/* 628 */         return this.wrappedStmt.executeUpdate(sql);
/*     */       }
/*     */       
/* 631 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 633 */     catch (SQLException sqlEx) {
/* 634 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 637 */       return -1;
/*     */     } 
/*     */   }
/*     */   public void enableStreamingResults() throws SQLException {
/*     */     try {
/* 642 */       if (this.wrappedStmt != null) {
/* 643 */         ((JdbcStatement)this.wrappedStmt).enableStreamingResults();
/*     */       } else {
/* 645 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 648 */     } catch (SQLException sqlEx) {
/* 649 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
/*     */     try {
/* 656 */       if ("java.sql.Statement".equals(iface.getName()) || "java.sql.Wrapper.class".equals(iface.getName())) {
/* 657 */         return iface.cast(this);
/*     */       }
/*     */       
/* 660 */       if (this.unwrappedInterfaces == null) {
/* 661 */         this.unwrappedInterfaces = new HashMap<>();
/*     */       }
/*     */       
/* 664 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*     */       
/* 666 */       if (cachedUnwrapped == null) {
/* 667 */         cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
/*     */         
/* 669 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */       } 
/*     */       
/* 672 */       return iface.cast(cachedUnwrapped);
/* 673 */     } catch (ClassCastException cce) {
/* 674 */       throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", this.exceptionInterceptor);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 682 */     boolean isInstance = iface.isInstance(this);
/*     */     
/* 684 */     if (isInstance) {
/* 685 */       return true;
/*     */     }
/*     */     
/* 688 */     String interfaceClassName = iface.getName();
/*     */     
/* 690 */     return (interfaceClassName.equals("com.mysql.cj.jdbc.Statement") || interfaceClassName.equals("java.sql.Statement") || interfaceClassName
/* 691 */       .equals("java.sql.Wrapper"));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() throws SQLException {
/*     */     try {
/* 697 */       if (this.wrappedStmt != null) {
/* 698 */         return this.wrappedStmt.isClosed();
/*     */       }
/* 700 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 702 */     catch (SQLException sqlEx) {
/* 703 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 706 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setPoolable(boolean poolable) throws SQLException {
/*     */     try {
/* 712 */       if (this.wrappedStmt != null) {
/* 713 */         this.wrappedStmt.setPoolable(poolable);
/*     */       } else {
/* 715 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 718 */     } catch (SQLException sqlEx) {
/* 719 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPoolable() throws SQLException {
/*     */     try {
/* 726 */       if (this.wrappedStmt != null) {
/* 727 */         return this.wrappedStmt.isPoolable();
/*     */       }
/* 729 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 731 */     catch (SQLException sqlEx) {
/* 732 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 735 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void closeOnCompletion() throws SQLException {
/* 740 */     if (this.wrappedStmt == null) {
/* 741 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCloseOnCompletion() throws SQLException {
/* 748 */     if (this.wrappedStmt == null) {
/* 749 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/* 752 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] executeLargeBatch() throws SQLException {
/*     */     try {
/* 758 */       if (this.wrappedStmt != null) {
/* 759 */         return ((StatementImpl)this.wrappedStmt).executeLargeBatch();
/*     */       }
/*     */       
/* 762 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 764 */     catch (SQLException sqlEx) {
/* 765 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 768 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long executeLargeUpdate(String sql) throws SQLException {
/*     */     try {
/* 774 */       if (this.wrappedStmt != null) {
/* 775 */         return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql);
/*     */       }
/*     */       
/* 778 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 780 */     catch (SQLException sqlEx) {
/* 781 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 784 */       return -1L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
/*     */     try {
/* 790 */       if (this.wrappedStmt != null) {
/* 791 */         return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql, autoGeneratedKeys);
/*     */       }
/*     */       
/* 794 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 796 */     catch (SQLException sqlEx) {
/* 797 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 800 */       return -1L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
/*     */     try {
/* 806 */       if (this.wrappedStmt != null) {
/* 807 */         return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql, columnIndexes);
/*     */       }
/*     */       
/* 810 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 812 */     catch (SQLException sqlEx) {
/* 813 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 816 */       return -1L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
/*     */     try {
/* 822 */       if (this.wrappedStmt != null) {
/* 823 */         return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql, columnNames);
/*     */       }
/*     */       
/* 826 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 828 */     catch (SQLException sqlEx) {
/* 829 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 832 */       return -1L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long getLargeMaxRows() throws SQLException {
/*     */     try {
/* 838 */       if (this.wrappedStmt != null) {
/* 839 */         return ((StatementImpl)this.wrappedStmt).getLargeMaxRows();
/*     */       }
/*     */       
/* 842 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 844 */     catch (SQLException sqlEx) {
/* 845 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 848 */       return 0L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long getLargeUpdateCount() throws SQLException {
/*     */     try {
/* 854 */       if (this.wrappedStmt != null) {
/* 855 */         return ((StatementImpl)this.wrappedStmt).getLargeUpdateCount();
/*     */       }
/*     */       
/* 858 */       throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */     }
/* 860 */     catch (SQLException sqlEx) {
/* 861 */       checkAndFireConnectionError(sqlEx);
/*     */ 
/*     */       
/* 864 */       return -1L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setLargeMaxRows(long max) throws SQLException {
/*     */     try {
/* 870 */       if (this.wrappedStmt != null) {
/* 871 */         ((StatementImpl)this.wrappedStmt).setLargeMaxRows(max);
/*     */       } else {
/* 873 */         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     
/* 876 */     } catch (SQLException sqlEx) {
/* 877 */       checkAndFireConnectionError(sqlEx);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\StatementWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */