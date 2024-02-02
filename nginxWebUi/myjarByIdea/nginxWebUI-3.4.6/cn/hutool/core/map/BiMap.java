package cn.hutool.core.map;

import java.util.Map;

public class BiMap<K, V> extends MapWrapper<K, V> {
   private static final long serialVersionUID = 1L;
   private Map<V, K> inverse;

   public BiMap(Map<K, V> raw) {
      super(raw);
   }

   public V put(K key, V value) {
      if (null != this.inverse) {
         this.inverse.put(value, key);
      }

      return super.put(key, value);
   }

   public void putAll(Map<? extends K, ? extends V> m) {
      super.putAll(m);
      if (null != this.inverse) {
         m.forEach((key, value) -> {
            this.inverse.put(value, key);
         });
      }

   }

   public V remove(Object key) {
      V v = super.remove(key);
      if (null != this.inverse && null != v) {
         this.inverse.remove(v);
      }

      return v;
   }

   public boolean remove(Object key, Object value) {
      return super.remove(key, value) && null != this.inverse && this.inverse.remove(value, key);
   }

   public void clear() {
      super.clear();
      this.inverse = null;
   }

   public Map<V, K> getInverse() {
      if (null == this.inverse) {
         this.inverse = MapUtil.inverse(this.getRaw());
      }

      return this.inverse;
   }

   public K getKey(V value) {
      return this.getInverse().get(value);
   }
}
