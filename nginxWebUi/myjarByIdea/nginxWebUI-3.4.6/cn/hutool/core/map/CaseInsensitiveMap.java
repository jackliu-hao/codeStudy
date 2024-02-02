package cn.hutool.core.map;

import java.util.HashMap;
import java.util.Map;

public class CaseInsensitiveMap<K, V> extends FuncKeyMap<K, V> {
   private static final long serialVersionUID = 4043263744224569870L;

   public CaseInsensitiveMap() {
      this(16);
   }

   public CaseInsensitiveMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public CaseInsensitiveMap(Map<? extends K, ? extends V> m) {
      this(0.75F, m);
   }

   public CaseInsensitiveMap(float loadFactor, Map<? extends K, ? extends V> m) {
      this(m.size(), loadFactor);
      this.putAll(m);
   }

   public CaseInsensitiveMap(int initialCapacity, float loadFactor) {
      this(MapBuilder.create(new HashMap(initialCapacity, loadFactor)));
   }

   CaseInsensitiveMap(MapBuilder<K, V> emptyMapBuilder) {
      super(emptyMapBuilder.build(), (key) -> {
         if (key instanceof CharSequence) {
            key = key.toString().toLowerCase();
         }

         return key;
      });
   }
}
