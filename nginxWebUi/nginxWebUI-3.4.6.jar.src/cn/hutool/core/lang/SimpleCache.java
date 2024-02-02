/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.collection.TransIter;
/*     */ import cn.hutool.core.lang.func.Func0;
/*     */ import cn.hutool.core.lang.mutable.Mutable;
/*     */ import cn.hutool.core.lang.mutable.MutableObj;
/*     */ import cn.hutool.core.map.WeakConcurrentMap;
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import java.util.function.Predicate;
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
/*     */ public class SimpleCache<K, V>
/*     */   implements Iterable<Map.Entry<K, V>>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Map<Mutable<K>, V> rawMap;
/*  35 */   private final ReadWriteLock lock = new ReentrantReadWriteLock();
/*     */ 
/*     */ 
/*     */   
/*  39 */   protected final Map<K, Lock> keyLockMap = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCache() {
/*  45 */     this((Map<Mutable<K>, V>)new WeakConcurrentMap());
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
/*     */   public SimpleCache(Map<Mutable<K>, V> initMap) {
/*  59 */     this.rawMap = initMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V get(K key) {
/*  69 */     this.lock.readLock().lock();
/*     */     try {
/*  71 */       return this.rawMap.get(MutableObj.of(key));
/*     */     } finally {
/*  73 */       this.lock.readLock().unlock();
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
/*     */   public V get(K key, Func0<V> supplier) {
/*  85 */     return get(key, null, supplier);
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
/*     */   public V get(K key, Predicate<V> validPredicate, Func0<V> supplier) {
/*  98 */     V v = get(key);
/*  99 */     if (null != validPredicate && null != v && false == validPredicate.test(v)) {
/* 100 */       v = null;
/*     */     }
/* 102 */     if (null == v && null != supplier) {
/*     */       
/* 104 */       Lock keyLock = this.keyLockMap.computeIfAbsent(key, k -> new ReentrantLock());
/* 105 */       keyLock.lock();
/*     */       
/*     */       try {
/* 108 */         v = get(key);
/* 109 */         if (null == v || (null != validPredicate && false == validPredicate.test(v))) {
/*     */           try {
/* 111 */             v = (V)supplier.call();
/* 112 */           } catch (Exception e) {
/* 113 */             throw new RuntimeException(e);
/*     */           } 
/* 115 */           put(key, v);
/*     */         } 
/*     */       } finally {
/* 118 */         keyLock.unlock();
/* 119 */         this.keyLockMap.remove(key);
/*     */       } 
/*     */     } 
/*     */     
/* 123 */     return v;
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
/*     */   public V put(K key, V value) {
/* 135 */     this.lock.writeLock().lock();
/*     */     try {
/* 137 */       this.rawMap.put(MutableObj.of(key), value);
/*     */     } finally {
/* 139 */       this.lock.writeLock().unlock();
/*     */     } 
/* 141 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V remove(K key) {
/* 152 */     this.lock.writeLock().lock();
/*     */     try {
/* 154 */       return this.rawMap.remove(MutableObj.of(key));
/*     */     } finally {
/* 156 */       this.lock.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 165 */     this.lock.writeLock().lock();
/*     */     try {
/* 167 */       this.rawMap.clear();
/*     */     } finally {
/* 169 */       this.lock.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<K, V>> iterator() {
/* 175 */     return (Iterator<Map.Entry<K, V>>)new TransIter(this.rawMap.entrySet().iterator(), entry -> new Map.Entry<K, V>()
/*     */         {
/*     */           public K getKey() {
/* 178 */             return (K)((Mutable)entry.getKey()).get();
/*     */           }
/*     */ 
/*     */           
/*     */           public V getValue() {
/* 183 */             return (V)entry.getValue();
/*     */           }
/*     */ 
/*     */           
/*     */           public V setValue(Object value) {
/* 188 */             return (V)entry.setValue(value);
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\SimpleCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */