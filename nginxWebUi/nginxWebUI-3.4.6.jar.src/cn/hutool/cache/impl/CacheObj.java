/*     */ package cn.hutool.cache.impl;
/*     */ 
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.Date;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ public class CacheObj<K, V>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final K key;
/*     */   protected final V obj;
/*     */   protected volatile long lastAccess;
/*  29 */   protected AtomicLong accessCount = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final long ttl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CacheObj(K key, V obj, long ttl) {
/*  43 */     this.key = key;
/*  44 */     this.obj = obj;
/*  45 */     this.ttl = ttl;
/*  46 */     this.lastAccess = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public K getKey() {
/*  56 */     return this.key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V getValue() {
/*  66 */     return this.obj;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTtl() {
/*  76 */     return this.ttl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getExpiredTime() {
/*  86 */     if (this.ttl > 0L) {
/*  87 */       return (Date)DateUtil.date(this.lastAccess + this.ttl);
/*     */     }
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLastAccess() {
/*  99 */     return this.lastAccess;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 104 */     return "CacheObj [key=" + this.key + ", obj=" + this.obj + ", lastAccess=" + this.lastAccess + ", accessCount=" + this.accessCount + ", ttl=" + this.ttl + "]";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isExpired() {
/* 113 */     if (this.ttl > 0L)
/*     */     {
/* 115 */       return (System.currentTimeMillis() - this.lastAccess > this.ttl);
/*     */     }
/* 117 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected V get(boolean isUpdateLastAccess) {
/* 128 */     if (isUpdateLastAccess) {
/* 129 */       this.lastAccess = System.currentTimeMillis();
/*     */     }
/* 131 */     this.accessCount.getAndIncrement();
/* 132 */     return this.obj;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\impl\CacheObj.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */