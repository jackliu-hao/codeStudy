/*     */ package org.h2.jdbcx;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.logging.Logger;
/*     */ import javax.sql.ConnectionEvent;
/*     */ import javax.sql.ConnectionEventListener;
/*     */ import javax.sql.ConnectionPoolDataSource;
/*     */ import javax.sql.DataSource;
/*     */ import javax.sql.PooledConnection;
/*     */ import org.h2.message.DbException;
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
/*     */ public final class JdbcConnectionPool
/*     */   implements DataSource, ConnectionEventListener, JdbcConnectionPoolBackwardsCompat
/*     */ {
/*     */   private static final int DEFAULT_TIMEOUT = 30;
/*     */   private static final int DEFAULT_MAX_CONNECTIONS = 10;
/*     */   private final ConnectionPoolDataSource dataSource;
/*  73 */   private final Queue<PooledConnection> recycledConnections = new ConcurrentLinkedQueue<>();
/*     */   private PrintWriter logWriter;
/*  75 */   private volatile int maxConnections = 10;
/*  76 */   private volatile int timeout = 30;
/*  77 */   private AtomicInteger activeConnections = new AtomicInteger();
/*  78 */   private AtomicBoolean isDisposed = new AtomicBoolean();
/*     */   
/*     */   protected JdbcConnectionPool(ConnectionPoolDataSource paramConnectionPoolDataSource) {
/*  81 */     this.dataSource = paramConnectionPoolDataSource;
/*  82 */     if (paramConnectionPoolDataSource != null) {
/*     */       try {
/*  84 */         this.logWriter = paramConnectionPoolDataSource.getLogWriter();
/*  85 */       } catch (SQLException sQLException) {}
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
/*     */   public static JdbcConnectionPool create(ConnectionPoolDataSource paramConnectionPoolDataSource) {
/*  98 */     return new JdbcConnectionPool(paramConnectionPoolDataSource);
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
/*     */   public static JdbcConnectionPool create(String paramString1, String paramString2, String paramString3) {
/* 111 */     JdbcDataSource jdbcDataSource = new JdbcDataSource();
/* 112 */     jdbcDataSource.setURL(paramString1);
/* 113 */     jdbcDataSource.setUser(paramString2);
/* 114 */     jdbcDataSource.setPassword(paramString3);
/* 115 */     return new JdbcConnectionPool(jdbcDataSource);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxConnections(int paramInt) {
/* 125 */     if (paramInt < 1) {
/* 126 */       throw new IllegalArgumentException("Invalid maxConnections value: " + paramInt);
/*     */     }
/* 128 */     this.maxConnections = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxConnections() {
/* 137 */     return this.maxConnections;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLoginTimeout() {
/* 147 */     return this.timeout;
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
/*     */   public void setLoginTimeout(int paramInt) {
/* 159 */     if (paramInt == 0) {
/* 160 */       paramInt = 30;
/*     */     }
/* 162 */     this.timeout = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dispose() {
/* 170 */     this.isDisposed.set(true);
/*     */     
/*     */     PooledConnection pooledConnection;
/* 173 */     while ((pooledConnection = this.recycledConnections.poll()) != null) {
/* 174 */       closeConnection(pooledConnection);
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
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/* 193 */     long l = System.nanoTime() + this.timeout * 1000000000L;
/* 194 */     byte b = 0;
/*     */     while (true) {
/* 196 */       if (this.activeConnections.incrementAndGet() <= this.maxConnections) {
/*     */         try {
/* 198 */           return getConnectionNow();
/* 199 */         } catch (Throwable throwable) {
/* 200 */           this.activeConnections.decrementAndGet();
/* 201 */           throw throwable;
/*     */         } 
/*     */       }
/* 204 */       this.activeConnections.decrementAndGet();
/*     */       
/* 206 */       if (--b < 0)
/*     */         
/*     */         try {
/*     */           
/* 210 */           b = 3;
/* 211 */           Thread.sleep(1L);
/* 212 */         } catch (InterruptedException interruptedException) {
/* 213 */           Thread.currentThread().interrupt();
/*     */         }  
/* 215 */       if (System.nanoTime() - l > 0L) {
/* 216 */         throw new SQLException("Login timeout", "08001", 8001);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection(String paramString1, String paramString2) {
/* 224 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   private Connection getConnectionNow() throws SQLException {
/* 228 */     if (this.isDisposed.get()) {
/* 229 */       throw new IllegalStateException("Connection pool has been disposed.");
/*     */     }
/* 231 */     PooledConnection pooledConnection = this.recycledConnections.poll();
/* 232 */     if (pooledConnection == null) {
/* 233 */       pooledConnection = this.dataSource.getPooledConnection();
/*     */     }
/* 235 */     Connection connection = pooledConnection.getConnection();
/* 236 */     pooledConnection.addConnectionEventListener(this);
/* 237 */     return connection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void recycleConnection(PooledConnection paramPooledConnection) {
/* 248 */     int i = this.activeConnections.decrementAndGet();
/* 249 */     if (i < 0) {
/* 250 */       this.activeConnections.incrementAndGet();
/* 251 */       throw new AssertionError();
/*     */     } 
/* 253 */     if (!this.isDisposed.get() && i < this.maxConnections) {
/* 254 */       this.recycledConnections.add(paramPooledConnection);
/* 255 */       if (this.isDisposed.get()) {
/* 256 */         dispose();
/*     */       }
/*     */     } else {
/* 259 */       closeConnection(paramPooledConnection);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void closeConnection(PooledConnection paramPooledConnection) {
/*     */     try {
/* 265 */       paramPooledConnection.close();
/* 266 */     } catch (SQLException sQLException) {
/* 267 */       if (this.logWriter != null) {
/* 268 */         sQLException.printStackTrace(this.logWriter);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void connectionClosed(ConnectionEvent paramConnectionEvent) {
/* 278 */     PooledConnection pooledConnection = (PooledConnection)paramConnectionEvent.getSource();
/* 279 */     pooledConnection.removeConnectionEventListener(this);
/* 280 */     recycleConnection(pooledConnection);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void connectionErrorOccurred(ConnectionEvent paramConnectionEvent) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getActiveConnections() {
/* 300 */     return this.activeConnections.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrintWriter getLogWriter() {
/* 308 */     return this.logWriter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLogWriter(PrintWriter paramPrintWriter) {
/* 316 */     this.logWriter = paramPrintWriter;
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
/* 329 */       if (isWrapperFor(paramClass)) {
/* 330 */         return (T)this;
/*     */       }
/* 332 */       throw DbException.getInvalidValueException("iface", paramClass);
/* 333 */     } catch (Exception exception) {
/* 334 */       throw DbException.toSQLException(exception);
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
/* 346 */     return (paramClass != null && paramClass.isAssignableFrom(getClass()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger getParentLogger() {
/* 354 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbcx\JdbcConnectionPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */