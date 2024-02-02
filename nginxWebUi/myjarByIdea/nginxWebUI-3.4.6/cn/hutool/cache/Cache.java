package cn.hutool.cache;

import cn.hutool.cache.impl.CacheObj;
import cn.hutool.core.lang.func.Func0;
import java.io.Serializable;
import java.util.Iterator;

public interface Cache<K, V> extends Iterable<V>, Serializable {
   int capacity();

   long timeout();

   void put(K var1, V var2);

   void put(K var1, V var2, long var3);

   default V get(K key) {
      return this.get(key, true);
   }

   default V get(K key, Func0<V> supplier) {
      return this.get(key, true, supplier);
   }

   V get(K var1, boolean var2, Func0<V> var3);

   V get(K var1, boolean var2);

   Iterator<CacheObj<K, V>> cacheObjIterator();

   int prune();

   boolean isFull();

   void remove(K var1);

   void clear();

   int size();

   boolean isEmpty();

   boolean containsKey(K var1);

   default Cache<K, V> setListener(CacheListener<K, V> listener) {
      return this;
   }
}
