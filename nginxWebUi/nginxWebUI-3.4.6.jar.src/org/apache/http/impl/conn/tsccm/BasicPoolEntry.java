/*     */ package org.apache.http.impl.conn.tsccm;
/*     */ 
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.http.conn.ClientConnectionOperator;
/*     */ import org.apache.http.conn.OperatedClientConnection;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.impl.conn.AbstractPoolEntry;
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
/*     */ @Deprecated
/*     */ public class BasicPoolEntry
/*     */   extends AbstractPoolEntry
/*     */ {
/*     */   private final long created;
/*     */   private long updated;
/*     */   private final long validUntil;
/*     */   private long expiry;
/*     */   
/*     */   public BasicPoolEntry(ClientConnectionOperator op, HttpRoute route, ReferenceQueue<Object> queue) {
/*  57 */     super(op, route);
/*  58 */     Args.notNull(route, "HTTP route");
/*  59 */     this.created = System.currentTimeMillis();
/*  60 */     this.validUntil = Long.MAX_VALUE;
/*  61 */     this.expiry = this.validUntil;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicPoolEntry(ClientConnectionOperator op, HttpRoute route) {
/*  72 */     this(op, route, -1L, TimeUnit.MILLISECONDS);
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
/*     */   public BasicPoolEntry(ClientConnectionOperator op, HttpRoute route, long connTTL, TimeUnit timeunit) {
/*  87 */     super(op, route);
/*  88 */     Args.notNull(route, "HTTP route");
/*  89 */     this.created = System.currentTimeMillis();
/*  90 */     if (connTTL > 0L) {
/*  91 */       this.validUntil = this.created + timeunit.toMillis(connTTL);
/*     */     } else {
/*  93 */       this.validUntil = Long.MAX_VALUE;
/*     */     } 
/*  95 */     this.expiry = this.validUntil;
/*     */   }
/*     */   
/*     */   protected final OperatedClientConnection getConnection() {
/*  99 */     return this.connection;
/*     */   }
/*     */   
/*     */   protected final HttpRoute getPlannedRoute() {
/* 103 */     return this.route;
/*     */   }
/*     */   
/*     */   protected final BasicPoolEntryRef getWeakRef() {
/* 107 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void shutdownEntry() {
/* 112 */     super.shutdownEntry();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCreated() {
/* 119 */     return this.created;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getUpdated() {
/* 126 */     return this.updated;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getExpiry() {
/* 133 */     return this.expiry;
/*     */   }
/*     */   
/*     */   public long getValidUntil() {
/* 137 */     return this.validUntil;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateExpiry(long time, TimeUnit timeunit) {
/*     */     long newExpiry;
/* 144 */     this.updated = System.currentTimeMillis();
/*     */     
/* 146 */     if (time > 0L) {
/* 147 */       newExpiry = this.updated + timeunit.toMillis(time);
/*     */     } else {
/* 149 */       newExpiry = Long.MAX_VALUE;
/*     */     } 
/* 151 */     this.expiry = Math.min(this.validUntil, newExpiry);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isExpired(long now) {
/* 158 */     return (now >= this.expiry);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\tsccm\BasicPoolEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */