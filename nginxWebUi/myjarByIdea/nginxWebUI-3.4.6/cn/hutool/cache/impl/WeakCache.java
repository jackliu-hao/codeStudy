package cn.hutool.cache.impl;

import cn.hutool.cache.CacheListener;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.lang.mutable.Mutable;
import cn.hutool.core.map.WeakConcurrentMap;
import java.lang.ref.Reference;

public class WeakCache<K, V> extends TimedCache<K, V> {
   private static final long serialVersionUID = 1L;

   public WeakCache(long timeout) {
      super(timeout, new WeakConcurrentMap());
   }

   public WeakCache<K, V> setListener(CacheListener<K, V> listener) {
      super.setListener(listener);
      WeakConcurrentMap<Mutable<K>, CacheObj<K, V>> map = (WeakConcurrentMap)this.cacheMap;
      map.setPurgeListener((key, value) -> {
         listener.onRemove(Opt.ofNullable(key).map(Reference::get).map(Mutable::get).get(), value.getValue());
      });
      return this;
   }
}
