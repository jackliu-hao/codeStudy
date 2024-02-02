/*     */ package cn.hutool.cache.impl;
/*     */ 
/*     */ import cn.hutool.cache.Cache;
/*     */ import cn.hutool.core.lang.func.Func0;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NoCache<K, V>
/*     */   implements Cache<K, V>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public int capacity() {
/*  20 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public long timeout() {
/*  25 */     return 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void put(K key, V object) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void put(K key, V object, long timeout) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsKey(K key) {
/*  40 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(K key) {
/*  45 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(K key, boolean isUpdateLastAccess) {
/*  50 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(K key, Func0<V> supplier) {
/*  55 */     return get(key, true, supplier);
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(K key, boolean isUpdateLastAccess, Func0<V> supplier) {
/*     */     try {
/*  61 */       return (null == supplier) ? null : (V)supplier.call();
/*  62 */     } catch (Exception e) {
/*  63 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<V> iterator() {
/*  69 */     return new Iterator<V>()
/*     */       {
/*     */         public boolean hasNext() {
/*  72 */           return false;
/*     */         }
/*     */ 
/*     */         
/*     */         public V next() {
/*  77 */           return null;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<CacheObj<K, V>> cacheObjIterator() {
/*  84 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int prune() {
/*  89 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFull() {
/*  94 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(K key) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 109 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 114 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\impl\NoCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */