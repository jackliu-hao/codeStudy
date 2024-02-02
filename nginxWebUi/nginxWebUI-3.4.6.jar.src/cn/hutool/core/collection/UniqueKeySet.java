/*     */ package cn.hutool.core.collection;
/*     */ 
/*     */ import cn.hutool.core.map.MapBuilder;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.function.Function;
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
/*     */ public class UniqueKeySet<K, V>
/*     */   extends AbstractSet<V>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private Map<K, V> map;
/*     */   private final Function<V, K> uniqueGenerator;
/*     */   
/*     */   public UniqueKeySet(Function<V, K> uniqueGenerator) {
/*  42 */     this(false, uniqueGenerator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UniqueKeySet(Function<V, K> uniqueGenerator, Collection<? extends V> c) {
/*  53 */     this(false, uniqueGenerator, c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UniqueKeySet(boolean isLinked, Function<V, K> uniqueGenerator) {
/*  63 */     this(MapBuilder.create(isLinked), uniqueGenerator);
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
/*     */   public UniqueKeySet(boolean isLinked, Function<V, K> uniqueGenerator, Collection<? extends V> c) {
/*  75 */     this(isLinked, uniqueGenerator);
/*  76 */     addAll(c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UniqueKeySet(int initialCapacity, float loadFactor, Function<V, K> uniqueGenerator) {
/*  87 */     this(MapBuilder.create(new HashMap<>(initialCapacity, loadFactor)), uniqueGenerator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UniqueKeySet(MapBuilder<K, V> builder, Function<V, K> uniqueGenerator) {
/*  97 */     this.map = builder.build();
/*  98 */     this.uniqueGenerator = uniqueGenerator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<V> iterator() {
/* 105 */     return this.map.values().iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 110 */     return this.map.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 115 */     return this.map.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(Object o) {
/* 121 */     return this.map.containsKey(this.uniqueGenerator.apply((V)o));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(V v) {
/* 126 */     return (null == this.map.put(this.uniqueGenerator.apply(v), v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addIfAbsent(V v) {
/* 136 */     return (null == this.map.putIfAbsent(this.uniqueGenerator.apply(v), v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addAllIfAbsent(Collection<? extends V> c) {
/* 146 */     boolean modified = false;
/* 147 */     for (V v : c) {
/* 148 */       if (addIfAbsent(v))
/* 149 */         modified = true; 
/*     */     } 
/* 151 */     return modified;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean remove(Object o) {
/* 157 */     return (null != this.map.remove(this.uniqueGenerator.apply((V)o)));
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 162 */     this.map.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public UniqueKeySet<K, V> clone() {
/*     */     try {
/* 169 */       UniqueKeySet<K, V> newSet = (UniqueKeySet<K, V>)super.clone();
/* 170 */       newSet.map = (Map<K, V>)ObjectUtil.clone(this.map);
/* 171 */       return newSet;
/* 172 */     } catch (CloneNotSupportedException e) {
/* 173 */       throw new InternalError(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\UniqueKeySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */