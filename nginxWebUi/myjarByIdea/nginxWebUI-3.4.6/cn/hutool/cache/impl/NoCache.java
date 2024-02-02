package cn.hutool.cache.impl;

import cn.hutool.cache.Cache;
import cn.hutool.core.lang.func.Func0;
import java.util.Iterator;

public class NoCache<K, V> implements Cache<K, V> {
   private static final long serialVersionUID = 1L;

   public int capacity() {
      return 0;
   }

   public long timeout() {
      return 0L;
   }

   public void put(K key, V object) {
   }

   public void put(K key, V object, long timeout) {
   }

   public boolean containsKey(K key) {
      return false;
   }

   public V get(K key) {
      return null;
   }

   public V get(K key, boolean isUpdateLastAccess) {
      return null;
   }

   public V get(K key, Func0<V> supplier) {
      return this.get(key, true, supplier);
   }

   public V get(K key, boolean isUpdateLastAccess, Func0<V> supplier) {
      try {
         return null == supplier ? null : supplier.call();
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }
   }

   public Iterator<V> iterator() {
      return new Iterator<V>() {
         public boolean hasNext() {
            return false;
         }

         public V next() {
            return null;
         }
      };
   }

   public Iterator<CacheObj<K, V>> cacheObjIterator() {
      return null;
   }

   public int prune() {
      return 0;
   }

   public boolean isFull() {
      return false;
   }

   public void remove(K key) {
   }

   public void clear() {
   }

   public int size() {
      return 0;
   }

   public boolean isEmpty() {
      return false;
   }
}
