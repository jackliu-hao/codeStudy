/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.http.client.BackoffManager;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.pool.ConnPoolControl;
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
/*     */ public class AIMDBackoffManager
/*     */   implements BackoffManager
/*     */ {
/*     */   private final ConnPoolControl<HttpRoute> connPerRoute;
/*     */   private final Clock clock;
/*     */   private final Map<HttpRoute, Long> lastRouteProbes;
/*     */   private final Map<HttpRoute, Long> lastRouteBackoffs;
/*  63 */   private long coolDown = 5000L;
/*  64 */   private double backoffFactor = 0.5D;
/*  65 */   private int cap = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AIMDBackoffManager(ConnPoolControl<HttpRoute> connPerRoute) {
/*  75 */     this(connPerRoute, new SystemClock());
/*     */   }
/*     */   
/*     */   AIMDBackoffManager(ConnPoolControl<HttpRoute> connPerRoute, Clock clock) {
/*  79 */     this.clock = clock;
/*  80 */     this.connPerRoute = connPerRoute;
/*  81 */     this.lastRouteProbes = new HashMap<HttpRoute, Long>();
/*  82 */     this.lastRouteBackoffs = new HashMap<HttpRoute, Long>();
/*     */   }
/*     */ 
/*     */   
/*     */   public void backOff(HttpRoute route) {
/*  87 */     synchronized (this.connPerRoute) {
/*  88 */       int curr = this.connPerRoute.getMaxPerRoute(route);
/*  89 */       Long lastUpdate = getLastUpdate(this.lastRouteBackoffs, route);
/*  90 */       long now = this.clock.getCurrentTime();
/*  91 */       if (now - lastUpdate.longValue() < this.coolDown) {
/*     */         return;
/*     */       }
/*  94 */       this.connPerRoute.setMaxPerRoute(route, getBackedOffPoolSize(curr));
/*  95 */       this.lastRouteBackoffs.put(route, Long.valueOf(now));
/*     */     } 
/*     */   }
/*     */   
/*     */   private int getBackedOffPoolSize(int curr) {
/* 100 */     if (curr <= 1) {
/* 101 */       return 1;
/*     */     }
/* 103 */     return (int)Math.floor(this.backoffFactor * curr);
/*     */   }
/*     */ 
/*     */   
/*     */   public void probe(HttpRoute route) {
/* 108 */     synchronized (this.connPerRoute) {
/* 109 */       int curr = this.connPerRoute.getMaxPerRoute(route);
/* 110 */       int max = (curr >= this.cap) ? this.cap : (curr + 1);
/* 111 */       Long lastProbe = getLastUpdate(this.lastRouteProbes, route);
/* 112 */       Long lastBackoff = getLastUpdate(this.lastRouteBackoffs, route);
/* 113 */       long now = this.clock.getCurrentTime();
/* 114 */       if (now - lastProbe.longValue() < this.coolDown || now - lastBackoff.longValue() < this.coolDown) {
/*     */         return;
/*     */       }
/* 117 */       this.connPerRoute.setMaxPerRoute(route, max);
/* 118 */       this.lastRouteProbes.put(route, Long.valueOf(now));
/*     */     } 
/*     */   }
/*     */   
/*     */   private Long getLastUpdate(Map<HttpRoute, Long> updates, HttpRoute route) {
/* 123 */     Long lastUpdate = updates.get(route);
/* 124 */     if (lastUpdate == null) {
/* 125 */       lastUpdate = Long.valueOf(0L);
/*     */     }
/* 127 */     return lastUpdate;
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
/*     */   public void setBackoffFactor(double d) {
/* 140 */     Args.check((d > 0.0D && d < 1.0D), "Backoff factor must be 0.0 < f < 1.0");
/* 141 */     this.backoffFactor = d;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCooldownMillis(long l) {
/* 152 */     Args.positive(this.coolDown, "Cool down");
/* 153 */     this.coolDown = l;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPerHostConnectionCap(int cap) {
/* 162 */     Args.positive(cap, "Per host connection cap");
/* 163 */     this.cap = cap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\AIMDBackoffManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */