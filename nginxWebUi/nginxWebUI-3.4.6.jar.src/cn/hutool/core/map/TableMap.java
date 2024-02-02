/*     */ package cn.hutool.core.map;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class TableMap<K, V>
/*     */   implements Map<K, V>, Iterable<Map.Entry<K, V>>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final int DEFAULT_CAPACITY = 10;
/*     */   private final List<K> keys;
/*     */   private final List<V> values;
/*     */   
/*     */   public TableMap() {
/*  39 */     this(10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TableMap(int size) {
/*  48 */     this.keys = new ArrayList<>(size);
/*  49 */     this.values = new ArrayList<>(size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TableMap(K[] keys, V[] values) {
/*  59 */     this.keys = CollUtil.toList((Object[])keys);
/*  60 */     this.values = CollUtil.toList((Object[])values);
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  65 */     return this.keys.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  70 */     return CollUtil.isEmpty(this.keys);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/*  76 */     return this.keys.contains(key);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/*  82 */     return this.values.contains(value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public V get(Object key) {
/*  88 */     int index = this.keys.indexOf(key);
/*  89 */     if (index > -1 && index < this.values.size()) {
/*  90 */       return this.values.get(index);
/*     */     }
/*  92 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public K getKey(V value) {
/* 103 */     int index = this.values.indexOf(value);
/* 104 */     if (index > -1 && index < this.keys.size()) {
/* 105 */       return this.keys.get(index);
/*     */     }
/* 107 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<V> getValues(K key) {
/* 118 */     return CollUtil.getAny(this.values, 
/*     */         
/* 120 */         ListUtil.indexOfAll(this.keys, ele -> ObjectUtil.equal(ele, key)));
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
/*     */   public List<K> getKeys(V value) {
/* 132 */     return CollUtil.getAny(this.keys, 
/*     */         
/* 134 */         ListUtil.indexOfAll(this.values, ele -> ObjectUtil.equal(ele, value)));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public V put(K key, V value) {
/* 140 */     this.keys.add(key);
/* 141 */     this.values.add(value);
/* 142 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public V remove(Object key) {
/* 148 */     int index = this.keys.indexOf(key);
/* 149 */     if (index > -1) {
/* 150 */       this.keys.remove(index);
/* 151 */       if (index < this.values.size()) {
/* 152 */         this.values.remove(index);
/*     */       }
/*     */     } 
/* 155 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends K, ? extends V> m) {
/* 160 */     for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
/* 161 */       put(entry.getKey(), entry.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 167 */     this.keys.clear();
/* 168 */     this.values.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<K> keySet() {
/* 173 */     return new HashSet<>(this.keys);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<K> keys() {
/* 183 */     return Collections.unmodifiableList(this.keys);
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<V> values() {
/* 188 */     return Collections.unmodifiableList(this.values);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<K, V>> entrySet() {
/* 193 */     Set<Map.Entry<K, V>> hashSet = new LinkedHashSet<>();
/* 194 */     for (int i = 0; i < size(); i++) {
/* 195 */       hashSet.add(MapUtil.entry(this.keys.get(i), this.values.get(i)));
/*     */     }
/* 197 */     return hashSet;
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<K, V>> iterator() {
/* 202 */     return new Iterator<Map.Entry<K, V>>() {
/* 203 */         private final Iterator<K> keysIter = TableMap.this.keys.iterator();
/* 204 */         private final Iterator<V> valuesIter = TableMap.this.values.iterator();
/*     */ 
/*     */         
/*     */         public boolean hasNext() {
/* 208 */           return (this.keysIter.hasNext() && this.valuesIter.hasNext());
/*     */         }
/*     */ 
/*     */         
/*     */         public Map.Entry<K, V> next() {
/* 213 */           return MapUtil.entry(this.keysIter.next(), this.valuesIter.next());
/*     */         }
/*     */ 
/*     */         
/*     */         public void remove() {
/* 218 */           this.keysIter.remove();
/* 219 */           this.valuesIter.remove();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 226 */     return "TableMap{keys=" + this.keys + ", values=" + this.values + '}';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\TableMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */