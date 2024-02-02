package cn.hutool.core.map;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class FuncMap<K, V> extends TransMap<K, V> {
   private static final long serialVersionUID = 1L;
   private final Function<Object, K> keyFunc;
   private final Function<Object, V> valueFunc;

   public FuncMap(Supplier<Map<K, V>> mapFactory, Function<Object, K> keyFunc, Function<Object, V> valueFunc) {
      this((Map)mapFactory.get(), keyFunc, valueFunc);
   }

   public FuncMap(Map<K, V> emptyMap, Function<Object, K> keyFunc, Function<Object, V> valueFunc) {
      super(emptyMap);
      this.keyFunc = keyFunc;
      this.valueFunc = valueFunc;
   }

   protected K customKey(Object key) {
      return null != this.keyFunc ? this.keyFunc.apply(key) : key;
   }

   protected V customValue(Object value) {
      return null != this.valueFunc ? this.valueFunc.apply(value) : value;
   }
}
