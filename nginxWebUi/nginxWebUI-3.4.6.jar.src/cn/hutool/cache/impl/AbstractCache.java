/*     */ package cn.hutool.cache.impl;
/*     */ 
/*     */ import cn.hutool.cache.Cache;
/*     */ import cn.hutool.cache.CacheListener;
/*     */ import cn.hutool.core.lang.func.Func0;
/*     */ import cn.hutool.core.lang.mutable.Mutable;
/*     */ import cn.hutool.core.lang.mutable.MutableObj;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.LongAdder;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import java.util.stream.Collectors;
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
/*     */ public abstract class AbstractCache<K, V>
/*     */   implements Cache<K, V>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected Map<Mutable<K>, CacheObj<K, V>> cacheMap;
/*  38 */   protected final Map<K, Lock> keyLockMap = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int capacity;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long timeout;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean existCustomTimeout;
/*     */ 
/*     */ 
/*     */   
/*  57 */   protected LongAdder hitCount = new LongAdder();
/*     */ 
/*     */ 
/*     */   
/*  61 */   protected LongAdder missCount = new LongAdder();
/*     */ 
/*     */ 
/*     */   
/*     */   protected CacheListener<K, V> listener;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void put(K key, V object) {
/*  71 */     put(key, object, this.timeout);
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
/*     */   protected void putWithoutLock(K key, V object, long timeout) {
/*  83 */     CacheObj<K, V> co = new CacheObj<>(key, object, timeout);
/*  84 */     if (timeout != 0L) {
/*  85 */       this.existCustomTimeout = true;
/*     */     }
/*  87 */     if (isFull()) {
/*  88 */       pruneCache();
/*     */     }
/*  90 */     this.cacheMap.put(MutableObj.of(key), co);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getHitCount() {
/*  99 */     return this.hitCount.sum();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMissCount() {
/* 106 */     return this.missCount.sum();
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(K key, boolean isUpdateLastAccess, Func0<V> supplier) {
/* 111 */     V v = (V)get(key, isUpdateLastAccess);
/* 112 */     if (null == v && null != supplier) {
/*     */       
/* 114 */       Lock keyLock = this.keyLockMap.computeIfAbsent(key, k -> new ReentrantLock());
/* 115 */       keyLock.lock();
/*     */       
/*     */       try {
/* 118 */         CacheObj<K, V> co = getWithoutLock(key);
/* 119 */         if (null == co || co.isExpired()) {
/*     */           try {
/* 121 */             v = (V)supplier.call();
/* 122 */           } catch (Exception e) {
/* 123 */             throw new RuntimeException(e);
/*     */           } 
/* 125 */           put(key, v, this.timeout);
/*     */         } else {
/* 127 */           v = co.get(isUpdateLastAccess);
/*     */         } 
/*     */       } finally {
/* 130 */         keyLock.unlock();
/* 131 */         this.keyLockMap.remove(key);
/*     */       } 
/*     */     } 
/* 134 */     return v;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CacheObj<K, V> getWithoutLock(K key) {
/* 144 */     return this.cacheMap.get(MutableObj.of(key));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<V> iterator() {
/* 150 */     CacheObjIterator<K, V> copiedIterator = (CacheObjIterator<K, V>)cacheObjIterator();
/* 151 */     return new CacheValuesIterator<>(copiedIterator);
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
/*     */   public int capacity() {
/* 166 */     return this.capacity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long timeout() {
/* 175 */     return this.timeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isPruneExpiredActive() {
/* 184 */     return (this.timeout != 0L || this.existCustomTimeout);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFull() {
/* 189 */     return (this.capacity > 0 && this.cacheMap.size() >= this.capacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 194 */     return this.cacheMap.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 199 */     return this.cacheMap.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 204 */     return this.cacheMap.toString();
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
/*     */   public AbstractCache<K, V> setListener(CacheListener<K, V> listener) {
/* 217 */     this.listener = listener;
/* 218 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<K> keySet() {
/* 228 */     return (Set<K>)this.cacheMap.keySet().stream().map(Mutable::get).collect(Collectors.toSet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void onRemove(K key, V cachedObject) {
/* 239 */     CacheListener<K, V> listener = this.listener;
/* 240 */     if (null != listener) {
/* 241 */       listener.onRemove(key, cachedObject);
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
/*     */   protected CacheObj<K, V> removeWithoutLock(K key, boolean withMissCount) {
/* 253 */     CacheObj<K, V> co = this.cacheMap.remove(MutableObj.of(key));
/* 254 */     if (withMissCount)
/*     */     {
/* 256 */       this.missCount.increment();
/*     */     }
/* 258 */     return co;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Iterator<CacheObj<K, V>> cacheObjIter() {
/* 267 */     return this.cacheMap.values().iterator();
/*     */   }
/*     */   
/*     */   protected abstract int pruneCache();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\impl\AbstractCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */