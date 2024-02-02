package cn.hutool.core.map;

import java.util.Map;

public abstract class CustomKeyMap<K, V> extends TransMap<K, V> {
   private static final long serialVersionUID = 4043263744224569870L;

   public CustomKeyMap(Map<K, V> emptyMap) {
      super(emptyMap);
   }

   protected V customValue(Object value) {
      return value;
   }
}
