package cn.hutool.core.map;

import cn.hutool.core.util.StrUtil;
import java.util.HashMap;
import java.util.Map;

public class CamelCaseMap<K, V> extends FuncKeyMap<K, V> {
   private static final long serialVersionUID = 4043263744224569870L;

   public CamelCaseMap() {
      this(16);
   }

   public CamelCaseMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public CamelCaseMap(Map<? extends K, ? extends V> m) {
      this(0.75F, m);
   }

   public CamelCaseMap(float loadFactor, Map<? extends K, ? extends V> m) {
      this(m.size(), loadFactor);
      this.putAll(m);
   }

   public CamelCaseMap(int initialCapacity, float loadFactor) {
      this(MapBuilder.create(new HashMap(initialCapacity, loadFactor)));
   }

   CamelCaseMap(MapBuilder<K, V> emptyMapBuilder) {
      super(emptyMapBuilder.build(), (key) -> {
         if (key instanceof CharSequence) {
            key = StrUtil.toCamelCase(key.toString());
         }

         return key;
      });
   }
}
