package cn.hutool.cache.impl;

import cn.hutool.core.collection.CopiedIter;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ReentrantCache<K, V> extends AbstractCache<K, V> {
   private static final long serialVersionUID = 1L;
   protected final ReentrantLock lock = new ReentrantLock();

   public void put(K key, V object, long timeout) {
      this.lock.lock();

      try {
         this.putWithoutLock(key, object, timeout);
      } finally {
         this.lock.unlock();
      }

   }

   public boolean containsKey(K key) {
      this.lock.lock();

      label55: {
         boolean var3;
         try {
            CacheObj<K, V> co = this.getWithoutLock(key);
            if (co != null) {
               if (co.isExpired()) {
                  break label55;
               }

               var3 = true;
               return var3;
            }

            var3 = false;
         } finally {
            this.lock.unlock();
         }

         return var3;
      }

      this.remove(key, true);
      return false;
   }

   public V get(K key, boolean isUpdateLastAccess) {
      this.lock.lock();

      CacheObj co;
      try {
         co = this.getWithoutLock(key);
      } finally {
         this.lock.unlock();
      }

      if (null == co) {
         this.missCount.increment();
         return null;
      } else if (!co.isExpired()) {
         this.hitCount.increment();
         return co.get(isUpdateLastAccess);
      } else {
         this.remove(key, true);
         return null;
      }
   }

   public Iterator<CacheObj<K, V>> cacheObjIterator() {
      this.lock.lock();

      CopiedIter copiedIterator;
      try {
         copiedIterator = CopiedIter.copyOf(this.cacheObjIter());
      } finally {
         this.lock.unlock();
      }

      return new CacheObjIterator(copiedIterator);
   }

   public final int prune() {
      this.lock.lock();

      int var1;
      try {
         var1 = this.pruneCache();
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   public void remove(K key) {
      this.remove(key, false);
   }

   public void clear() {
      this.lock.lock();

      try {
         this.cacheMap.clear();
      } finally {
         this.lock.unlock();
      }

   }

   public String toString() {
      this.lock.lock();

      String var1;
      try {
         var1 = super.toString();
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   private void remove(K key, boolean withMissCount) {
      this.lock.lock();

      CacheObj co;
      try {
         co = this.removeWithoutLock(key, withMissCount);
      } finally {
         this.lock.unlock();
      }

      if (null != co) {
         this.onRemove(co.key, co.obj);
      }

   }
}
