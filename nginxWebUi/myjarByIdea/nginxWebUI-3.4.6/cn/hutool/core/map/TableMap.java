package cn.hutool.core.map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableMap<K, V> implements Map<K, V>, Iterable<Map.Entry<K, V>>, Serializable {
   private static final long serialVersionUID = 1L;
   private static final int DEFAULT_CAPACITY = 10;
   private final List<K> keys;
   private final List<V> values;

   public TableMap() {
      this(10);
   }

   public TableMap(int size) {
      this.keys = new ArrayList(size);
      this.values = new ArrayList(size);
   }

   public TableMap(K[] keys, V[] values) {
      this.keys = CollUtil.toList(keys);
      this.values = CollUtil.toList(values);
   }

   public int size() {
      return this.keys.size();
   }

   public boolean isEmpty() {
      return CollUtil.isEmpty((Collection)this.keys);
   }

   public boolean containsKey(Object key) {
      return this.keys.contains(key);
   }

   public boolean containsValue(Object value) {
      return this.values.contains(value);
   }

   public V get(Object key) {
      int index = this.keys.indexOf(key);
      return index > -1 && index < this.values.size() ? this.values.get(index) : null;
   }

   public K getKey(V value) {
      int index = this.values.indexOf(value);
      return index > -1 && index < this.keys.size() ? this.keys.get(index) : null;
   }

   public List<V> getValues(K key) {
      return CollUtil.getAny(this.values, ListUtil.indexOfAll(this.keys, (ele) -> {
         return ObjectUtil.equal(ele, key);
      }));
   }

   public List<K> getKeys(V value) {
      return CollUtil.getAny(this.keys, ListUtil.indexOfAll(this.values, (ele) -> {
         return ObjectUtil.equal(ele, value);
      }));
   }

   public V put(K key, V value) {
      this.keys.add(key);
      this.values.add(value);
      return null;
   }

   public V remove(Object key) {
      int index = this.keys.indexOf(key);
      if (index > -1) {
         this.keys.remove(index);
         if (index < this.values.size()) {
            this.values.remove(index);
         }
      }

      return null;
   }

   public void putAll(Map<? extends K, ? extends V> m) {
      Iterator var2 = m.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<? extends K, ? extends V> entry = (Map.Entry)var2.next();
         this.put(entry.getKey(), entry.getValue());
      }

   }

   public void clear() {
      this.keys.clear();
      this.values.clear();
   }

   public Set<K> keySet() {
      return new HashSet(this.keys);
   }

   public List<K> keys() {
      return Collections.unmodifiableList(this.keys);
   }

   public Collection<V> values() {
      return Collections.unmodifiableList(this.values);
   }

   public Set<Map.Entry<K, V>> entrySet() {
      Set<Map.Entry<K, V>> hashSet = new LinkedHashSet();

      for(int i = 0; i < this.size(); ++i) {
         hashSet.add(MapUtil.entry(this.keys.get(i), this.values.get(i)));
      }

      return hashSet;
   }

   public Iterator<Map.Entry<K, V>> iterator() {
      return new Iterator<Map.Entry<K, V>>() {
         private final Iterator<K> keysIter;
         private final Iterator<V> valuesIter;

         {
            this.keysIter = TableMap.this.keys.iterator();
            this.valuesIter = TableMap.this.values.iterator();
         }

         public boolean hasNext() {
            return this.keysIter.hasNext() && this.valuesIter.hasNext();
         }

         public Map.Entry<K, V> next() {
            return MapUtil.entry(this.keysIter.next(), this.valuesIter.next());
         }

         public void remove() {
            this.keysIter.remove();
            this.valuesIter.remove();
         }
      };
   }

   public String toString() {
      return "TableMap{keys=" + this.keys + ", values=" + this.values + '}';
   }
}
