/*     */ package cn.hutool.cache.impl;
/*     */ 
/*     */ import cn.hutool.core.collection.CopiedIter;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.locks.ReentrantLock;
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
/*     */ public abstract class ReentrantCache<K, V>
/*     */   extends AbstractCache<K, V>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  22 */   protected final ReentrantLock lock = new ReentrantLock();
/*     */ 
/*     */   
/*     */   public void put(K key, V object, long timeout) {
/*  26 */     this.lock.lock();
/*     */     try {
/*  28 */       putWithoutLock(key, object, timeout);
/*     */     } finally {
/*  30 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(K key) {
/*  36 */     this.lock.lock();
/*     */     
/*     */     try {
/*  39 */       CacheObj<K, V> co = getWithoutLock(key);
/*  40 */       if (co == null) {
/*  41 */         return false;
/*     */       }
/*     */       
/*  44 */       if (false == co.isExpired())
/*     */       {
/*  46 */         return true;
/*     */       }
/*     */     } finally {
/*  49 */       this.lock.unlock();
/*     */     } 
/*     */ 
/*     */     
/*  53 */     remove(key, true);
/*  54 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(K key, boolean isUpdateLastAccess) {
/*     */     CacheObj<K, V> co;
/*  60 */     this.lock.lock();
/*     */     try {
/*  62 */       co = getWithoutLock(key);
/*     */     } finally {
/*  64 */       this.lock.unlock();
/*     */     } 
/*     */ 
/*     */     
/*  68 */     if (null == co) {
/*  69 */       this.missCount.increment();
/*  70 */       return null;
/*  71 */     }  if (false == co.isExpired()) {
/*  72 */       this.hitCount.increment();
/*  73 */       return co.get(isUpdateLastAccess);
/*     */     } 
/*     */ 
/*     */     
/*  77 */     remove(key, true);
/*  78 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<CacheObj<K, V>> cacheObjIterator() {
/*     */     CopiedIter<CacheObj<K, V>> copiedIterator;
/*  84 */     this.lock.lock();
/*     */     try {
/*  86 */       copiedIterator = CopiedIter.copyOf(cacheObjIter());
/*     */     } finally {
/*  88 */       this.lock.unlock();
/*     */     } 
/*  90 */     return new CacheObjIterator<>((Iterator<CacheObj<K, V>>)copiedIterator);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int prune() {
/*  95 */     this.lock.lock();
/*     */     try {
/*  97 */       return pruneCache();
/*     */     } finally {
/*  99 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(K key) {
/* 105 */     remove(key, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 110 */     this.lock.lock();
/*     */     try {
/* 112 */       this.cacheMap.clear();
/*     */     } finally {
/* 114 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 120 */     this.lock.lock();
/*     */     try {
/* 122 */       return super.toString();
/*     */     } finally {
/* 124 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void remove(K key, boolean withMissCount) {
/*     */     CacheObj<K, V> co;
/* 135 */     this.lock.lock();
/*     */     
/*     */     try {
/* 138 */       co = removeWithoutLock(key, withMissCount);
/*     */     } finally {
/* 140 */       this.lock.unlock();
/*     */     } 
/* 142 */     if (null != co)
/* 143 */       onRemove(co.key, co.obj); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\impl\ReentrantCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */