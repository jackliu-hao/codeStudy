package cn.hutool.core.lang;

import cn.hutool.core.collection.TransIter;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.mutable.Mutable;
import cn.hutool.core.lang.mutable.MutableObj;
import cn.hutool.core.map.WeakConcurrentMap;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

public class SimpleCache<K, V> implements Iterable<Map.Entry<K, V>>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<Mutable<K>, V> rawMap;
   private final ReadWriteLock lock;
   protected final Map<K, Lock> keyLockMap;

   public SimpleCache() {
      this(new WeakConcurrentMap());
   }

   public SimpleCache(Map<Mutable<K>, V> initMap) {
      this.lock = new ReentrantReadWriteLock();
      this.keyLockMap = new ConcurrentHashMap();
      this.rawMap = initMap;
   }

   public V get(K key) {
      this.lock.readLock().lock();

      Object var2;
      try {
         var2 = this.rawMap.get(MutableObj.of(key));
      } finally {
         this.lock.readLock().unlock();
      }

      return var2;
   }

   public V get(K key, Func0<V> supplier) {
      return this.get(key, (Predicate)null, supplier);
   }

   public V get(K key, Predicate<V> validPredicate, Func0<V> supplier) {
      V v = this.get(key);
      if (null != validPredicate && null != v && !validPredicate.test(v)) {
         v = null;
      }

      if (null == v && null != supplier) {
         Lock keyLock = (Lock)this.keyLockMap.computeIfAbsent(key, (k) -> {
            return new ReentrantLock();
         });
         keyLock.lock();

         try {
            v = this.get(key);
            if (null == v || null != validPredicate && !validPredicate.test(v)) {
               try {
                  v = supplier.call();
               } catch (Exception var10) {
                  throw new RuntimeException(var10);
               }

               this.put(key, v);
            }
         } finally {
            keyLock.unlock();
            this.keyLockMap.remove(key);
         }
      }

      return v;
   }

   public V put(K key, V value) {
      this.lock.writeLock().lock();

      try {
         this.rawMap.put(MutableObj.of(key), value);
      } finally {
         this.lock.writeLock().unlock();
      }

      return value;
   }

   public V remove(K key) {
      this.lock.writeLock().lock();

      Object var2;
      try {
         var2 = this.rawMap.remove(MutableObj.of(key));
      } finally {
         this.lock.writeLock().unlock();
      }

      return var2;
   }

   public void clear() {
      this.lock.writeLock().lock();

      try {
         this.rawMap.clear();
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   public Iterator<Map.Entry<K, V>> iterator() {
      return new TransIter(this.rawMap.entrySet().iterator(), (entry) -> {
         return new Map.Entry<K, V>() {
            public K getKey() {
               return ((Mutable)entry.getKey()).get();
            }

            public V getValue() {
               return entry.getValue();
            }

            public V setValue(V value) {
               return entry.setValue(value);
            }
         };
      });
   }
}
