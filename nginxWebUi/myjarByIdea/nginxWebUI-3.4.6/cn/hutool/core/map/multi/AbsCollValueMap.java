package cn.hutool.core.map.multi;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapWrapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbsCollValueMap<K, V, C extends Collection<V>> extends MapWrapper<K, C> {
   private static final long serialVersionUID = 1L;
   protected static final int DEFAULT_COLLECTION_INITIAL_CAPACITY = 3;

   public AbsCollValueMap() {
      this(16);
   }

   public AbsCollValueMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public AbsCollValueMap(Map<? extends K, C> m) {
      this(0.75F, m);
   }

   public AbsCollValueMap(float loadFactor, Map<? extends K, C> m) {
      this(m.size(), loadFactor);
      this.putAll(m);
   }

   public AbsCollValueMap(int initialCapacity, float loadFactor) {
      super((Map)(new HashMap(initialCapacity, loadFactor)));
   }

   public void putAllValues(Map<? extends K, ? extends Collection<V>> m) {
      if (null != m) {
         m.forEach((key, valueColl) -> {
            if (null != valueColl) {
               valueColl.forEach((value) -> {
                  this.putValue(key, value);
               });
            }

         });
      }

   }

   public void putValue(K key, V value) {
      C collection = (Collection)this.get(key);
      if (null == collection) {
         collection = this.createCollection();
         this.put(key, collection);
      }

      collection.add(value);
   }

   public V get(K key, int index) {
      Collection<V> collection = (Collection)this.get(key);
      return CollUtil.get(collection, index);
   }

   protected abstract C createCollection();
}
