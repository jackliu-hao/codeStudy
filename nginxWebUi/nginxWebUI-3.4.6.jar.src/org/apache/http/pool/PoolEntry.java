/*     */ package org.apache.http.pool;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*     */ public abstract class PoolEntry<T, C>
/*     */ {
/*     */   private final String id;
/*     */   private final T route;
/*     */   private final C conn;
/*     */   private final long created;
/*     */   private final long validityDeadline;
/*     */   private long updated;
/*     */   private long expiry;
/*     */   private volatile Object state;
/*     */   
/*     */   public PoolEntry(String id, T route, C conn, long timeToLive, TimeUnit timeUnit) {
/*  79 */     Args.notNull(route, "Route");
/*  80 */     Args.notNull(conn, "Connection");
/*  81 */     Args.notNull(timeUnit, "Time unit");
/*  82 */     this.id = id;
/*  83 */     this.route = route;
/*  84 */     this.conn = conn;
/*  85 */     this.created = System.currentTimeMillis();
/*  86 */     this.updated = this.created;
/*  87 */     if (timeToLive > 0L) {
/*  88 */       long deadline = this.created + timeUnit.toMillis(timeToLive);
/*     */       
/*  90 */       this.validityDeadline = (deadline > 0L) ? deadline : Long.MAX_VALUE;
/*     */     } else {
/*  92 */       this.validityDeadline = Long.MAX_VALUE;
/*     */     } 
/*  94 */     this.expiry = this.validityDeadline;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PoolEntry(String id, T route, C conn) {
/* 105 */     this(id, route, conn, 0L, TimeUnit.MILLISECONDS);
/*     */   }
/*     */   
/*     */   public String getId() {
/* 109 */     return this.id;
/*     */   }
/*     */   
/*     */   public T getRoute() {
/* 113 */     return this.route;
/*     */   }
/*     */   
/*     */   public C getConnection() {
/* 117 */     return this.conn;
/*     */   }
/*     */   
/*     */   public long getCreated() {
/* 121 */     return this.created;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getValidityDeadline() {
/* 128 */     return this.validityDeadline;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public long getValidUnit() {
/* 136 */     return this.validityDeadline;
/*     */   }
/*     */   
/*     */   public Object getState() {
/* 140 */     return this.state;
/*     */   }
/*     */   
/*     */   public void setState(Object state) {
/* 144 */     this.state = state;
/*     */   }
/*     */   
/*     */   public synchronized long getUpdated() {
/* 148 */     return this.updated;
/*     */   }
/*     */   
/*     */   public synchronized long getExpiry() {
/* 152 */     return this.expiry;
/*     */   }
/*     */   public synchronized void updateExpiry(long time, TimeUnit timeUnit) {
/*     */     long newExpiry;
/* 156 */     Args.notNull(timeUnit, "Time unit");
/* 157 */     this.updated = System.currentTimeMillis();
/*     */     
/* 159 */     if (time > 0L) {
/* 160 */       newExpiry = this.updated + timeUnit.toMillis(time);
/*     */     } else {
/* 162 */       newExpiry = Long.MAX_VALUE;
/*     */     } 
/* 164 */     this.expiry = Math.min(newExpiry, this.validityDeadline);
/*     */   }
/*     */   
/*     */   public synchronized boolean isExpired(long now) {
/* 168 */     return (now >= this.expiry);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void close();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean isClosed();
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 184 */     StringBuilder buffer = new StringBuilder();
/* 185 */     buffer.append("[id:");
/* 186 */     buffer.append(this.id);
/* 187 */     buffer.append("][route:");
/* 188 */     buffer.append(this.route);
/* 189 */     buffer.append("][state:");
/* 190 */     buffer.append(this.state);
/* 191 */     buffer.append("]");
/* 192 */     return buffer.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\pool\PoolEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */