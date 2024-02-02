/*     */ package io.undertow.util;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentMap;
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
/*     */ public class CopyOnWriteMap<K, V>
/*     */   implements ConcurrentMap<K, V>
/*     */ {
/*  39 */   private volatile Map<K, V> delegate = Collections.emptyMap();
/*     */ 
/*     */   
/*     */   public CopyOnWriteMap() {}
/*     */   
/*     */   public CopyOnWriteMap(Map<K, V> existing) {
/*  45 */     this.delegate = new HashMap<>(existing);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized V putIfAbsent(K key, V value) {
/*  50 */     Map<K, V> delegate = this.delegate;
/*  51 */     V existing = delegate.get(key);
/*  52 */     if (existing != null) {
/*  53 */       return existing;
/*     */     }
/*  55 */     putInternal(key, value);
/*  56 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean remove(Object key, Object value) {
/*  61 */     Map<K, V> delegate = this.delegate;
/*  62 */     V existing = delegate.get(key);
/*  63 */     if (existing.equals(value)) {
/*  64 */       removeInternal(key);
/*  65 */       return true;
/*     */     } 
/*  67 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean replace(K key, V oldValue, V newValue) {
/*  72 */     Map<K, V> delegate = this.delegate;
/*  73 */     V existing = delegate.get(key);
/*  74 */     if (existing.equals(oldValue)) {
/*  75 */       putInternal(key, newValue);
/*  76 */       return true;
/*     */     } 
/*  78 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized V replace(K key, V value) {
/*  83 */     Map<K, V> delegate = this.delegate;
/*  84 */     V existing = delegate.get(key);
/*  85 */     if (existing != null) {
/*  86 */       putInternal(key, value);
/*  87 */       return existing;
/*     */     } 
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  94 */     return this.delegate.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  99 */     return this.delegate.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 104 */     return this.delegate.containsKey(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/* 109 */     return this.delegate.containsValue(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(Object key) {
/* 114 */     return this.delegate.get(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized V put(K key, V value) {
/* 119 */     return putInternal(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized V remove(Object key) {
/* 124 */     return removeInternal(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void putAll(Map<? extends K, ? extends V> m) {
/* 129 */     Map<K, V> delegate = new HashMap<>(this.delegate);
/* 130 */     for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
/* 131 */       delegate.put(e.getKey(), e.getValue());
/*     */     }
/* 133 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void clear() {
/* 138 */     this.delegate = Collections.emptyMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<K> keySet() {
/* 143 */     return this.delegate.keySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<V> values() {
/* 148 */     return this.delegate.values();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<K, V>> entrySet() {
/* 153 */     return this.delegate.entrySet();
/*     */   }
/*     */ 
/*     */   
/*     */   private V putInternal(K key, V value) {
/* 158 */     Map<K, V> delegate = new HashMap<>(this.delegate);
/* 159 */     V existing = delegate.put(key, value);
/* 160 */     this.delegate = delegate;
/* 161 */     return existing;
/*     */   }
/*     */   
/*     */   public V removeInternal(Object key) {
/* 165 */     Map<K, V> delegate = new HashMap<>(this.delegate);
/* 166 */     V existing = delegate.remove(key);
/* 167 */     this.delegate = delegate;
/* 168 */     return existing;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\CopyOnWriteMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */