package cn.hutool.core.map.multi;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SetValueMap<K, V> extends AbsCollValueMap<K, V, Set<V>> {
   private static final long serialVersionUID = 6044017508487827899L;

   public SetValueMap() {
      this(16);
   }

   public SetValueMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public SetValueMap(Map<? extends K, ? extends Collection<V>> m) {
      this(0.75F, m);
   }

   public SetValueMap(float loadFactor, Map<? extends K, ? extends Collection<V>> m) {
      this(m.size(), loadFactor);
      this.putAllValues(m);
   }

   public SetValueMap(int initialCapacity, float loadFactor) {
      super(new HashMap(initialCapacity, loadFactor));
   }

   protected Set<V> createCollection() {
      return new LinkedHashSet(3);
   }
}
