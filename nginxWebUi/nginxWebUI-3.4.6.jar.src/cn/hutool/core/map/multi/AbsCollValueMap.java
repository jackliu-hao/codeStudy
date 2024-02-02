/*     */ package cn.hutool.core.map.multi;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.map.MapWrapper;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public abstract class AbsCollValueMap<K, V, C extends Collection<V>>
/*     */   extends MapWrapper<K, C>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected static final int DEFAULT_COLLECTION_INITIAL_CAPACITY = 3;
/*     */   
/*     */   public AbsCollValueMap() {
/*  33 */     this(16);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbsCollValueMap(int initialCapacity) {
/*  42 */     this(initialCapacity, 0.75F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbsCollValueMap(Map<? extends K, C> m) {
/*  51 */     this(0.75F, m);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbsCollValueMap(float loadFactor, Map<? extends K, C> m) {
/*  61 */     this(m.size(), loadFactor);
/*  62 */     putAll(m);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbsCollValueMap(int initialCapacity, float loadFactor) {
/*  72 */     super(new HashMap<>(initialCapacity, loadFactor));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void putAllValues(Map<? extends K, ? extends Collection<V>> m) {
/*  83 */     if (null != m) {
/*  84 */       m.forEach((key, valueColl) -> {
/*     */             if (null != valueColl) {
/*     */               valueColl.forEach(());
/*     */             }
/*     */           });
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
/*     */   public void putValue(K key, V value) {
/* 100 */     Collection<V> collection = (Collection)get(key);
/* 101 */     if (null == collection) {
/* 102 */       collection = (Collection<V>)createCollection();
/* 103 */       put(key, collection);
/*     */     } 
/* 105 */     collection.add(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V get(K key, int index) {
/* 116 */     Collection<V> collection = (Collection<V>)get(key);
/* 117 */     return (V)CollUtil.get(collection, index);
/*     */   }
/*     */   
/*     */   protected abstract C createCollection();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\multi\AbsCollValueMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */