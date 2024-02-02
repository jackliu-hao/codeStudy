package cn.hutool.core.map;

import cn.hutool.core.util.ObjectUtil;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class MapWrapper<K, V> implements Map<K, V>, Iterable<Map.Entry<K, V>>, Serializable, Cloneable {
   private static final long serialVersionUID = -7524578042008586382L;
   protected static final float DEFAULT_LOAD_FACTOR = 0.75F;
   protected static final int DEFAULT_INITIAL_CAPACITY = 16;
   private Map<K, V> raw;

   public MapWrapper(Supplier<Map<K, V>> mapFactory) {
      this((Map)mapFactory.get());
   }

   public MapWrapper(Map<K, V> raw) {
      this.raw = raw;
   }

   public Map<K, V> getRaw() {
      return this.raw;
   }

   public int size() {
      return this.raw.size();
   }

   public boolean isEmpty() {
      return this.raw.isEmpty();
   }

   public boolean containsKey(Object key) {
      return this.raw.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.raw.containsValue(value);
   }

   public V get(Object key) {
      return this.raw.get(key);
   }

   public V put(K key, V value) {
      return this.raw.put(key, value);
   }

   public V remove(Object key) {
      return this.raw.remove(key);
   }

   public void putAll(Map<? extends K, ? extends V> m) {
      this.raw.putAll(m);
   }

   public void clear() {
      this.raw.clear();
   }

   public Collection<V> values() {
      return this.raw.values();
   }

   public Set<K> keySet() {
      return this.raw.keySet();
   }

   public Set<Map.Entry<K, V>> entrySet() {
      return this.raw.entrySet();
   }

   public Iterator<Map.Entry<K, V>> iterator() {
      return this.entrySet().iterator();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MapWrapper<?, ?> that = (MapWrapper)o;
         return Objects.equals(this.raw, that.raw);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.raw});
   }

   public String toString() {
      return this.raw.toString();
   }

   public void forEach(BiConsumer<? super K, ? super V> action) {
      this.raw.forEach(action);
   }

   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
      this.raw.replaceAll(function);
   }

   public V putIfAbsent(K key, V value) {
      return this.raw.putIfAbsent(key, value);
   }

   public boolean remove(Object key, Object value) {
      return this.raw.remove(key, value);
   }

   public boolean replace(K key, V oldValue, V newValue) {
      return this.raw.replace(key, oldValue, newValue);
   }

   public V replace(K key, V value) {
      return this.raw.replace(key, value);
   }

   public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
      return this.raw.computeIfAbsent(key, mappingFunction);
   }

   public V getOrDefault(Object key, V defaultValue) {
      return this.raw.getOrDefault(key, defaultValue);
   }

   public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      return this.raw.computeIfPresent(key, remappingFunction);
   }

   public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      return this.raw.compute(key, remappingFunction);
   }

   public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      return this.raw.merge(key, value, remappingFunction);
   }

   public MapWrapper<K, V> clone() throws CloneNotSupportedException {
      MapWrapper<K, V> clone = (MapWrapper)super.clone();
      clone.raw = (Map)ObjectUtil.clone(this.raw);
      return clone;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.raw);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.raw = (Map)in.readObject();
   }
}
