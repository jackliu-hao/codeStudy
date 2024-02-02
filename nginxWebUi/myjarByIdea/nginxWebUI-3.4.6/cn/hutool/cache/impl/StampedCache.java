package cn.hutool.cache.impl;

import cn.hutool.core.collection.CopiedIter;
import java.util.Iterator;
import java.util.concurrent.locks.StampedLock;

public abstract class StampedCache<K, V> extends AbstractCache<K, V> {
   private static final long serialVersionUID = 1L;
   protected final StampedLock lock = new StampedLock();

   public void put(K key, V object, long timeout) {
      long stamp = this.lock.writeLock();

      try {
         this.putWithoutLock(key, object, timeout);
      } finally {
         this.lock.unlockWrite(stamp);
      }

   }

   public boolean containsKey(K key) {
      long stamp = this.lock.readLock();

      try {
         CacheObj<K, V> co = this.getWithoutLock(key);
         boolean var5;
         if (co == null) {
            var5 = false;
            return var5;
         }

         if (!co.isExpired()) {
            var5 = true;
            return var5;
         }
      } finally {
         this.lock.unlockRead(stamp);
      }

      this.remove(key, true);
      return false;
   }

   public V get(K key, boolean isUpdateLastAccess) {
      long stamp = this.lock.tryOptimisticRead();
      CacheObj<K, V> co = this.getWithoutLock(key);
      if (!this.lock.validate(stamp)) {
         stamp = this.lock.readLock();

         try {
            co = this.getWithoutLock(key);
         } finally {
            this.lock.unlockRead(stamp);
         }
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
      long stamp = this.lock.readLock();

      CopiedIter copiedIterator;
      try {
         copiedIterator = CopiedIter.copyOf(this.cacheObjIter());
      } finally {
         this.lock.unlockRead(stamp);
      }

      return new CacheObjIterator(copiedIterator);
   }

   public final int prune() {
      long stamp = this.lock.writeLock();

      int var3;
      try {
         var3 = this.pruneCache();
      } finally {
         this.lock.unlockWrite(stamp);
      }

      return var3;
   }

   public void remove(K key) {
      this.remove(key, false);
   }

   public void clear() {
      long stamp = this.lock.writeLock();

      try {
         this.cacheMap.clear();
      } finally {
         this.lock.unlockWrite(stamp);
      }

   }

   private void remove(K key, boolean withMissCount) {
      long stamp = this.lock.writeLock();

      CacheObj co;
      try {
         co = this.removeWithoutLock(key, withMissCount);
      } finally {
         this.lock.unlockWrite(stamp);
      }

      if (null != co) {
         this.onRemove(co.key, co.obj);
      }

   }
}
