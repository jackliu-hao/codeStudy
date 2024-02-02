/*     */ package com.zaxxer.hikari.pool;
/*     */ 
/*     */ import com.zaxxer.hikari.util.ClockSource;
/*     */ import com.zaxxer.hikari.util.ConcurrentBag;
/*     */ import com.zaxxer.hikari.util.FastList;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
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
/*     */ final class PoolEntry
/*     */   implements ConcurrentBag.IConcurrentBagEntry
/*     */ {
/*  38 */   private static final Logger LOGGER = LoggerFactory.getLogger(PoolEntry.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  45 */   private volatile int state = 0;
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
/*  60 */   private static final AtomicIntegerFieldUpdater<PoolEntry> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(PoolEntry.class, "state");
/*     */   Connection connection;
/*     */   long lastAccessed;
/*     */   
/*     */   PoolEntry(Connection connection, PoolBase pool, boolean isReadOnly, boolean isAutoCommit) {
/*  65 */     this.connection = connection;
/*  66 */     this.hikariPool = (HikariPool)pool;
/*  67 */     this.isReadOnly = isReadOnly;
/*  68 */     this.isAutoCommit = isAutoCommit;
/*  69 */     this.lastAccessed = ClockSource.currentTime();
/*  70 */     this.openStatements = new FastList(Statement.class, 16);
/*     */   }
/*     */   long lastBorrowed; private volatile boolean evict; private volatile ScheduledFuture<?> endOfLife;
/*     */   private volatile ScheduledFuture<?> keepalive;
/*     */   private final FastList<Statement> openStatements;
/*     */   private final HikariPool hikariPool;
/*     */   private final boolean isReadOnly;
/*     */   private final boolean isAutoCommit;
/*     */   
/*     */   void recycle(long lastAccessed) {
/*  80 */     if (this.connection != null) {
/*  81 */       this.lastAccessed = lastAccessed;
/*  82 */       this.hikariPool.recycle(this);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setFutureEol(ScheduledFuture<?> endOfLife) {
/*  93 */     this.endOfLife = endOfLife;
/*     */   }
/*     */   
/*     */   public void setKeepalive(ScheduledFuture<?> keepalive) {
/*  97 */     this.keepalive = keepalive;
/*     */   }
/*     */ 
/*     */   
/*     */   Connection createProxyConnection(ProxyLeakTask leakTask, long now) {
/* 102 */     return ProxyFactory.getProxyConnection(this, this.connection, this.openStatements, leakTask, now, this.isReadOnly, this.isAutoCommit);
/*     */   }
/*     */ 
/*     */   
/*     */   void resetConnectionState(ProxyConnection proxyConnection, int dirtyBits) throws SQLException {
/* 107 */     this.hikariPool.resetConnectionState(this.connection, proxyConnection, dirtyBits);
/*     */   }
/*     */ 
/*     */   
/*     */   String getPoolName() {
/* 112 */     return this.hikariPool.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isMarkedEvicted() {
/* 117 */     return this.evict;
/*     */   }
/*     */ 
/*     */   
/*     */   void markEvicted() {
/* 122 */     this.evict = true;
/*     */   }
/*     */ 
/*     */   
/*     */   void evict(String closureReason) {
/* 127 */     this.hikariPool.closeConnection(this, closureReason);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   long getMillisSinceBorrowed() {
/* 133 */     return ClockSource.elapsedMillis(this.lastBorrowed);
/*     */   }
/*     */ 
/*     */   
/*     */   PoolBase getPoolBase() {
/* 138 */     return this.hikariPool;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 145 */     long now = ClockSource.currentTime();
/* 146 */     return this.connection + ", accessed " + 
/* 147 */       ClockSource.elapsedDisplayString(this.lastAccessed, now) + " ago, " + 
/* 148 */       stateToString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getState() {
/* 159 */     return stateUpdater.get(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean compareAndSet(int expect, int update) {
/* 166 */     return stateUpdater.compareAndSet(this, expect, update);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setState(int update) {
/* 173 */     stateUpdater.set(this, update);
/*     */   }
/*     */ 
/*     */   
/*     */   Connection close() {
/* 178 */     ScheduledFuture<?> eol = this.endOfLife;
/* 179 */     if (eol != null && !eol.isDone() && !eol.cancel(false)) {
/* 180 */       LOGGER.warn("{} - maxLifeTime expiration task cancellation unexpectedly returned false for connection {}", getPoolName(), this.connection);
/*     */     }
/*     */     
/* 183 */     ScheduledFuture<?> ka = this.keepalive;
/* 184 */     if (ka != null && !ka.isDone() && !ka.cancel(false)) {
/* 185 */       LOGGER.warn("{} - keepalive task cancellation unexpectedly returned false for connection {}", getPoolName(), this.connection);
/*     */     }
/*     */     
/* 188 */     Connection con = this.connection;
/* 189 */     this.connection = null;
/* 190 */     this.endOfLife = null;
/* 191 */     this.keepalive = null;
/* 192 */     return con;
/*     */   }
/*     */ 
/*     */   
/*     */   private String stateToString() {
/* 197 */     switch (this.state) {
/*     */       case 1:
/* 199 */         return "IN_USE";
/*     */       case 0:
/* 201 */         return "NOT_IN_USE";
/*     */       case -1:
/* 203 */         return "REMOVED";
/*     */       case -2:
/* 205 */         return "RESERVED";
/*     */     } 
/* 207 */     return "Invalid";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\pool\PoolEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */