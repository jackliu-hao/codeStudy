package cn.hutool.core.map;

import java.util.LinkedHashMap;
import java.util.Map;

public class CamelCaseLinkedMap<K, V> extends CamelCaseMap<K, V> {
   private static final long serialVersionUID = 4043263744224569870L;

   public CamelCaseLinkedMap() {
      this(16);
   }

   public CamelCaseLinkedMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public CamelCaseLinkedMap(Map<? extends K, ? extends V> m) {
      this(0.75F, m);
   }

   public CamelCaseLinkedMap(float loadFactor, Map<? extends K, ? extends V> m) {
      this(m.size(), loadFactor);
      this.putAll(m);
   }

   public CamelCaseLinkedMap(int initialCapacity, float loadFactor) {
      super((Map)(new LinkedHashMap(initialCapacity, loadFactor)));
   }
}
