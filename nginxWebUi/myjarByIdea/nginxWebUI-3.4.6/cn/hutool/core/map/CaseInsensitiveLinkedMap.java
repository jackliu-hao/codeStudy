package cn.hutool.core.map;

import java.util.LinkedHashMap;
import java.util.Map;

public class CaseInsensitiveLinkedMap<K, V> extends CaseInsensitiveMap<K, V> {
   private static final long serialVersionUID = 4043263744224569870L;

   public CaseInsensitiveLinkedMap() {
      this(16);
   }

   public CaseInsensitiveLinkedMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public CaseInsensitiveLinkedMap(Map<? extends K, ? extends V> m) {
      this(0.75F, m);
   }

   public CaseInsensitiveLinkedMap(float loadFactor, Map<? extends K, ? extends V> m) {
      this(m.size(), loadFactor);
      this.putAll(m);
   }

   public CaseInsensitiveLinkedMap(int initialCapacity, float loadFactor) {
      super((Map)(new LinkedHashMap(initialCapacity, loadFactor)));
   }
}
