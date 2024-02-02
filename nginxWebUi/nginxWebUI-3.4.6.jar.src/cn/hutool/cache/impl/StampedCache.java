/*     */ package cn.hutool.cache.impl;
/*     */ 
/*     */ import cn.hutool.core.collection.CopiedIter;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.locks.StampedLock;
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
/*     */ public abstract class StampedCache<K, V>
/*     */   extends AbstractCache<K, V>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  22 */   protected final StampedLock lock = new StampedLock();
/*     */ 
/*     */   
/*     */   public void put(K key, V object, long timeout) {
/*  26 */     long stamp = this.lock.writeLock();
/*     */     try {
/*  28 */       putWithoutLock(key, object, timeout);
/*     */     } finally {
/*  30 */       this.lock.unlockWrite(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(K key) {
/*  36 */     long stamp = this.lock.readLock();
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
/*  49 */       this.lock.unlockRead(stamp);
/*     */     } 
/*     */ 
/*     */     
/*  53 */     remove(key, true);
/*  54 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public V get(K key, boolean isUpdateLastAccess) {
/*  60 */     long stamp = this.lock.tryOptimisticRead();
/*  61 */     CacheObj<K, V> co = getWithoutLock(key);
/*  62 */     if (false == this.lock.validate(stamp)) {
/*     */       
/*  64 */       stamp = this.lock.readLock();
/*     */       try {
/*  66 */         co = getWithoutLock(key);
/*     */       } finally {
/*  68 */         this.lock.unlockRead(stamp);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  73 */     if (null == co) {
/*  74 */       this.missCount.increment();
/*  75 */       return null;
/*  76 */     }  if (false == co.isExpired()) {
/*  77 */       this.hitCount.increment();
/*  78 */       return co.get(isUpdateLastAccess);
/*     */     } 
/*     */ 
/*     */     
/*  82 */     remove(key, true);
/*  83 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<CacheObj<K, V>> cacheObjIterator() {
/*     */     CopiedIter<CacheObj<K, V>> copiedIterator;
/*  89 */     long stamp = this.lock.readLock();
/*     */     try {
/*  91 */       copiedIterator = CopiedIter.copyOf(cacheObjIter());
/*     */     } finally {
/*  93 */       this.lock.unlockRead(stamp);
/*     */     } 
/*  95 */     return new CacheObjIterator<>((Iterator<CacheObj<K, V>>)copiedIterator);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int prune() {
/* 100 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 102 */       return pruneCache();
/*     */     } finally {
/* 104 */       this.lock.unlockWrite(stamp);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(K key) {
/* 110 */     remove(key, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 115 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 117 */       this.cacheMap.clear();
/*     */     } finally {
/* 119 */       this.lock.unlockWrite(stamp);
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
/* 130 */     long stamp = this.lock.writeLock();
/*     */     
/*     */     try {
/* 133 */       co = removeWithoutLock(key, withMissCount);
/*     */     } finally {
/* 135 */       this.lock.unlockWrite(stamp);
/*     */     } 
/* 137 */     if (null != co)
/* 138 */       onRemove(co.key, co.obj); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\impl\StampedCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */