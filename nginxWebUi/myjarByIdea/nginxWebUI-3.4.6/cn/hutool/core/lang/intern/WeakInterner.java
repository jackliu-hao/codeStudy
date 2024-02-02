package cn.hutool.core.lang.intern;

import cn.hutool.core.map.WeakConcurrentMap;

public class WeakInterner<T> implements Interner<T> {
   private final WeakConcurrentMap<T, T> cache = new WeakConcurrentMap();

   public T intern(T sample) {
      return null == sample ? null : this.cache.computeIfAbsent(sample, (key) -> {
         return sample;
      });
   }
}
