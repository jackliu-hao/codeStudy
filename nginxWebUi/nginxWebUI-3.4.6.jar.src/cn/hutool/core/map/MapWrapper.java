/*     */ package cn.hutool.core.map;
/*     */ 
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
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
/*     */ public class MapWrapper<K, V>
/*     */   implements Map<K, V>, Iterable<Map.Entry<K, V>>, Serializable, Cloneable
/*     */ {
/*     */   private static final long serialVersionUID = -7524578042008586382L;
/*     */   protected static final float DEFAULT_LOAD_FACTOR = 0.75F;
/*     */   protected static final int DEFAULT_INITIAL_CAPACITY = 16;
/*     */   private Map<K, V> raw;
/*     */   
/*     */   public MapWrapper(Supplier<Map<K, V>> mapFactory) {
/*  49 */     this(mapFactory.get());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MapWrapper(Map<K, V> raw) {
/*  58 */     this.raw = raw;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<K, V> getRaw() {
/*  67 */     return this.raw;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  72 */     return this.raw.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  77 */     return this.raw.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/*  82 */     return this.raw.containsKey(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/*  87 */     return this.raw.containsValue(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(Object key) {
/*  92 */     return this.raw.get(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public V put(K key, V value) {
/*  97 */     return this.raw.put(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public V remove(Object key) {
/* 102 */     return this.raw.remove(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends K, ? extends V> m) {
/* 107 */     this.raw.putAll(m);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 112 */     this.raw.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<V> values() {
/* 117 */     return this.raw.values();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<K> keySet() {
/* 122 */     return this.raw.keySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<K, V>> entrySet() {
/* 127 */     return this.raw.entrySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<K, V>> iterator() {
/* 132 */     return entrySet().iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 137 */     if (this == o) {
/* 138 */       return true;
/*     */     }
/* 140 */     if (o == null || getClass() != o.getClass()) {
/* 141 */       return false;
/*     */     }
/* 143 */     MapWrapper<?, ?> that = (MapWrapper<?, ?>)o;
/* 144 */     return Objects.equals(this.raw, that.raw);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 149 */     return Objects.hash(new Object[] { this.raw });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 154 */     return this.raw.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void forEach(BiConsumer<? super K, ? super V> action) {
/* 160 */     this.raw.forEach(action);
/*     */   }
/*     */ 
/*     */   
/*     */   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
/* 165 */     this.raw.replaceAll(function);
/*     */   }
/*     */ 
/*     */   
/*     */   public V putIfAbsent(K key, V value) {
/* 170 */     return this.raw.putIfAbsent(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object key, Object value) {
/* 175 */     return this.raw.remove(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean replace(K key, V oldValue, V newValue) {
/* 180 */     return this.raw.replace(key, oldValue, newValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public V replace(K key, V value) {
/* 185 */     return this.raw.replace(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
/* 190 */     return this.raw.computeIfAbsent(key, mappingFunction);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V getOrDefault(Object key, V defaultValue) {
/* 197 */     return this.raw.getOrDefault(key, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
/* 202 */     return this.raw.computeIfPresent(key, remappingFunction);
/*     */   }
/*     */ 
/*     */   
/*     */   public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
/* 207 */     return this.raw.compute(key, remappingFunction);
/*     */   }
/*     */ 
/*     */   
/*     */   public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
/* 212 */     return this.raw.merge(key, value, remappingFunction);
/*     */   }
/*     */ 
/*     */   
/*     */   public MapWrapper<K, V> clone() throws CloneNotSupportedException {
/* 217 */     MapWrapper<K, V> clone = (MapWrapper<K, V>)super.clone();
/* 218 */     clone.raw = (Map<K, V>)ObjectUtil.clone(this.raw);
/* 219 */     return clone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeObject(ObjectOutputStream out) throws IOException {
/* 226 */     out.defaultWriteObject();
/* 227 */     out.writeObject(this.raw);
/*     */   }
/*     */ 
/*     */   
/*     */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 232 */     in.defaultReadObject();
/* 233 */     this.raw = (Map<K, V>)in.readObject();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\MapWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */