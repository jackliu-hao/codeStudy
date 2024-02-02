package cn.hutool.core.map;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class TransMap<K, V> extends MapWrapper<K, V> {
   private static final long serialVersionUID = 1L;

   public TransMap(Supplier<Map<K, V>> mapFactory) {
      super(mapFactory);
   }

   public TransMap(Map<K, V> emptyMap) {
      super(emptyMap);
   }

   public V get(Object key) {
      return super.get(this.customKey(key));
   }

   public V put(K key, V value) {
      return super.put(this.customKey(key), this.customValue(value));
   }

   public void putAll(Map<? extends K, ? extends V> m) {
      m.forEach(this::put);
   }

   public boolean containsKey(Object key) {
      return super.containsKey(this.customKey(key));
   }

   public V remove(Object key) {
      return super.remove(this.customKey(key));
   }

   public boolean remove(Object key, Object value) {
      return super.remove(this.customKey(key), this.customValue(value));
   }

   public boolean replace(K key, V oldValue, V newValue) {
      return super.replace(this.customKey(key), this.customValue(oldValue), this.customValue(this.values()));
   }

   public V replace(K key, V value) {
      return super.replace(this.customKey(key), this.customValue(value));
   }

   public V getOrDefault(Object key, V defaultValue) {
      return super.getOrDefault(this.customKey(key), this.customValue(defaultValue));
   }

   public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      return super.computeIfPresent(this.customKey(key), (k, v) -> {
         return remappingFunction.apply(this.customKey(k), this.customValue(v));
      });
   }

   public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      return super.compute(this.customKey(key), (k, v) -> {
         return remappingFunction.apply(this.customKey(k), this.customValue(v));
      });
   }

   public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      return super.merge(this.customKey(key), this.customValue(value), (v1, v2) -> {
         return remappingFunction.apply(this.customValue(v1), this.customValue(v2));
      });
   }

   protected abstract K customKey(Object var1);

   protected abstract V customValue(Object var1);
}
