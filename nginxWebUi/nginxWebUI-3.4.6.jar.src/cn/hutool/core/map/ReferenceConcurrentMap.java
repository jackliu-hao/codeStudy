/*     */ package cn.hutool.core.map;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.lang.func.Func0;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.ReferenceUtil;
/*     */ import java.io.Serializable;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.Function;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ReferenceConcurrentMap<K, V>
/*     */   implements ConcurrentMap<K, V>, Iterable<Map.Entry<K, V>>, Serializable
/*     */ {
/*     */   final ConcurrentMap<Reference<K>, V> raw;
/*     */   private final ReferenceQueue<K> lastQueue;
/*     */   private final ReferenceUtil.ReferenceType keyType;
/*     */   private BiConsumer<Reference<? extends K>, V> purgeListener;
/*     */   
/*     */   public ReferenceConcurrentMap(ConcurrentMap<Reference<K>, V> raw, ReferenceUtil.ReferenceType referenceType) {
/*  53 */     this.raw = raw;
/*  54 */     this.keyType = referenceType;
/*  55 */     this.lastQueue = new ReferenceQueue<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPurgeListener(BiConsumer<Reference<? extends K>, V> purgeListener) {
/*  65 */     this.purgeListener = purgeListener;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  70 */     purgeStaleKeys();
/*  71 */     return this.raw.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  76 */     return (0 == size());
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(Object key) {
/*  81 */     purgeStaleKeys();
/*     */     
/*  83 */     return this.raw.get(ofKey((K)key, null));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/*  88 */     purgeStaleKeys();
/*     */     
/*  90 */     return this.raw.containsKey(ofKey((K)key, null));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/*  95 */     purgeStaleKeys();
/*  96 */     return this.raw.containsValue(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public V put(K key, V value) {
/* 101 */     purgeStaleKeys();
/* 102 */     return this.raw.put(ofKey(key, this.lastQueue), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public V putIfAbsent(K key, V value) {
/* 107 */     purgeStaleKeys();
/* 108 */     return this.raw.putIfAbsent(ofKey(key, this.lastQueue), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends K, ? extends V> m) {
/* 113 */     m.forEach(this::put);
/*     */   }
/*     */ 
/*     */   
/*     */   public V replace(K key, V value) {
/* 118 */     purgeStaleKeys();
/* 119 */     return this.raw.replace(ofKey(key, this.lastQueue), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean replace(K key, V oldValue, V newValue) {
/* 124 */     purgeStaleKeys();
/* 125 */     return this.raw.replace(ofKey(key, this.lastQueue), oldValue, newValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
/* 130 */     purgeStaleKeys();
/* 131 */     this.raw.replaceAll((kWeakKey, value) -> function.apply(kWeakKey.get(), value));
/*     */   }
/*     */ 
/*     */   
/*     */   public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
/* 136 */     purgeStaleKeys();
/* 137 */     return this.raw.computeIfAbsent(ofKey(key, this.lastQueue), kWeakKey -> mappingFunction.apply(key));
/*     */   }
/*     */ 
/*     */   
/*     */   public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
/* 142 */     purgeStaleKeys();
/* 143 */     return this.raw.computeIfPresent(ofKey(key, this.lastQueue), (kWeakKey, value) -> remappingFunction.apply(key, value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V computeIfAbsent(K key, Func0<? extends V> supplier) {
/* 154 */     return computeIfAbsent(key, keyParam -> supplier.callWithRuntimeException());
/*     */   }
/*     */ 
/*     */   
/*     */   public V remove(Object key) {
/* 159 */     purgeStaleKeys();
/*     */     
/* 161 */     return this.raw.remove(ofKey((K)key, null));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object key, Object value) {
/* 166 */     purgeStaleKeys();
/*     */     
/* 168 */     return this.raw.remove(ofKey((K)key, null), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 173 */     this.raw.clear();
/*     */     
/* 175 */     while (this.lastQueue.poll() != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<K> keySet() {
/* 181 */     Collection<K> trans = CollUtil.trans(this.raw.keySet(), reference -> (null == reference) ? null : reference.get());
/* 182 */     return new HashSet<>(trans);
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<V> values() {
/* 187 */     purgeStaleKeys();
/* 188 */     return this.raw.values();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<K, V>> entrySet() {
/* 193 */     purgeStaleKeys();
/* 194 */     return (Set<Map.Entry<K, V>>)this.raw.entrySet().stream()
/* 195 */       .map(entry -> new AbstractMap.SimpleImmutableEntry<>(((Reference)entry.getKey()).get(), entry.getValue()))
/* 196 */       .collect(Collectors.toSet());
/*     */   }
/*     */ 
/*     */   
/*     */   public void forEach(BiConsumer<? super K, ? super V> action) {
/* 201 */     purgeStaleKeys();
/* 202 */     this.raw.forEach((key, value) -> action.accept(key.get(), value));
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<K, V>> iterator() {
/* 207 */     return entrySet().iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
/* 212 */     purgeStaleKeys();
/* 213 */     return this.raw.compute(ofKey(key, this.lastQueue), (kWeakKey, value) -> remappingFunction.apply(key, value));
/*     */   }
/*     */ 
/*     */   
/*     */   public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
/* 218 */     purgeStaleKeys();
/* 219 */     return this.raw.merge(ofKey(key, this.lastQueue), value, remappingFunction);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void purgeStaleKeys() {
/*     */     Reference<? extends K> reference;
/* 228 */     while ((reference = this.lastQueue.poll()) != null) {
/* 229 */       V value = this.raw.remove(reference);
/* 230 */       if (null != this.purgeListener) {
/* 231 */         this.purgeListener.accept(reference, value);
/*     */       }
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
/*     */   private Reference<K> ofKey(K key, ReferenceQueue<? super K> queue) {
/* 244 */     switch (this.keyType) {
/*     */       case WEAK:
/* 246 */         return new WeakKey<>(key, queue);
/*     */       case SOFT:
/* 248 */         return new SoftKey<>(key, queue);
/*     */     } 
/* 250 */     throw new IllegalArgumentException("Unsupported key type: " + this.keyType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class WeakKey<K>
/*     */     extends WeakReference<K>
/*     */   {
/*     */     private final int hashCode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     WeakKey(K key, ReferenceQueue<? super K> queue) {
/* 268 */       super(key, queue);
/* 269 */       this.hashCode = key.hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 274 */       return this.hashCode;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object other) {
/* 279 */       if (other == this)
/* 280 */         return true; 
/* 281 */       if (other instanceof WeakKey) {
/* 282 */         return ObjectUtil.equals(((WeakKey)other).get(), get());
/*     */       }
/* 284 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class SoftKey<K>
/*     */     extends SoftReference<K>
/*     */   {
/*     */     private final int hashCode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     SoftKey(K key, ReferenceQueue<? super K> queue) {
/* 303 */       super(key, queue);
/* 304 */       this.hashCode = key.hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 309 */       return this.hashCode;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object other) {
/* 314 */       if (other == this)
/* 315 */         return true; 
/* 316 */       if (other instanceof SoftKey) {
/* 317 */         return ObjectUtil.equals(((SoftKey)other).get(), get());
/*     */       }
/* 319 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\ReferenceConcurrentMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */