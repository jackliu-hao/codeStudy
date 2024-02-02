package io.undertow.server.handlers.cache;

import io.undertow.util.ConcurrentDirectDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class LRUCache<K, V> {
   private static final int SAMPLE_INTERVAL = 5;
   private final int maxEntries;
   private final ConcurrentMap<K, CacheEntry<K, V>> cache;
   private final ConcurrentDirectDeque<CacheEntry<K, V>> accessQueue;
   private final int maxAge;
   private final boolean fifo;

   public LRUCache(int maxEntries, int maxAge) {
      this.maxAge = maxAge;
      this.cache = new ConcurrentHashMap(16);
      this.accessQueue = ConcurrentDirectDeque.newInstance();
      this.maxEntries = maxEntries;
      this.fifo = false;
   }

   public LRUCache(int maxEntries, int maxAge, boolean fifo) {
      this.maxAge = maxAge;
      this.cache = new ConcurrentHashMap(16);
      this.accessQueue = ConcurrentDirectDeque.newInstance();
      this.maxEntries = maxEntries;
      this.fifo = fifo;
   }

   public void add(K key, V newValue) {
      CacheEntry<K, V> value = (CacheEntry)this.cache.get(key);
      if (value == null) {
         long expires;
         if (this.maxAge == -1) {
            expires = -1L;
         } else {
            expires = System.currentTimeMillis() + (long)this.maxAge;
         }

         value = new CacheEntry(key, newValue, expires);
         CacheEntry result = (CacheEntry)this.cache.putIfAbsent(key, value);
         if (result != null) {
            value = result;
            result.setValue(newValue);
         }

         this.bumpAccess(value);
         if (this.cache.size() > this.maxEntries) {
            CacheEntry<K, V> oldest = (CacheEntry)this.accessQueue.poll();
            if (oldest != value) {
               this.remove(oldest.key());
            }
         }
      }

   }

   public V get(K key) {
      CacheEntry<K, V> cacheEntry = (CacheEntry)this.cache.get(key);
      if (cacheEntry == null) {
         return null;
      } else {
         long expires = cacheEntry.getExpires();
         if (expires != -1L && System.currentTimeMillis() > expires) {
            this.remove(key);
            return null;
         } else {
            if (!this.fifo && cacheEntry.hit() % 5 == 0) {
               this.bumpAccess(cacheEntry);
            }

            return cacheEntry.getValue();
         }
      }
   }

   private void bumpAccess(CacheEntry<K, V> cacheEntry) {
      Object prevToken = cacheEntry.claimToken();
      if (!Boolean.FALSE.equals(prevToken)) {
         if (prevToken != null) {
            this.accessQueue.removeToken(prevToken);
         }

         Object token = null;

         try {
            token = this.accessQueue.offerLastAndReturnToken(cacheEntry);
         } catch (Throwable var5) {
         }

         if (!cacheEntry.setToken(token) && token != null) {
            this.accessQueue.removeToken(token);
         }
      }

   }

   public V remove(K key) {
      CacheEntry<K, V> remove = (CacheEntry)this.cache.remove(key);
      if (remove != null) {
         Object old = remove.clearToken();
         if (old != null) {
            this.accessQueue.removeToken(old);
         }

         return remove.getValue();
      } else {
         return null;
      }
   }

   public void clear() {
      this.cache.clear();
      this.accessQueue.clear();
   }

   public static final class CacheEntry<K, V> {
      private static final Object CLAIM_TOKEN = new Object();
      private static final AtomicIntegerFieldUpdater<CacheEntry> hitsUpdater = AtomicIntegerFieldUpdater.newUpdater(CacheEntry.class, "hits");
      private static final AtomicReferenceFieldUpdater<CacheEntry, Object> tokenUpdator = AtomicReferenceFieldUpdater.newUpdater(CacheEntry.class, Object.class, "accessToken");
      private final K key;
      private volatile V value;
      private final long expires;
      private volatile int hits;
      private volatile Object accessToken;

      private CacheEntry(K key, V value, long expires) {
         this.hits = 1;
         this.key = key;
         this.value = value;
         this.expires = expires;
      }

      public void setValue(V value) {
         this.value = value;
      }

      public V getValue() {
         return this.value;
      }

      public int hit() {
         int i;
         do {
            i = this.hits;
         } while(!hitsUpdater.weakCompareAndSet(this, i++, i));

         return i;
      }

      public K key() {
         return this.key;
      }

      Object claimToken() {
         Object current;
         do {
            current = this.accessToken;
            if (current == CLAIM_TOKEN) {
               return Boolean.FALSE;
            }
         } while(!tokenUpdator.compareAndSet(this, current, CLAIM_TOKEN));

         return current;
      }

      boolean setToken(Object token) {
         return tokenUpdator.compareAndSet(this, CLAIM_TOKEN, token);
      }

      Object clearToken() {
         Object old = tokenUpdator.getAndSet(this, (Object)null);
         return old == CLAIM_TOKEN ? null : old;
      }

      public long getExpires() {
         return this.expires;
      }

      // $FF: synthetic method
      CacheEntry(Object x0, Object x1, long x2, Object x3) {
         this(x0, x1, x2);
      }
   }
}
