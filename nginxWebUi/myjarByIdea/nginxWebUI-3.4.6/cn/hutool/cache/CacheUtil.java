package cn.hutool.cache;

import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.cache.impl.LFUCache;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.cache.impl.NoCache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.cache.impl.WeakCache;

public class CacheUtil {
   public static <K, V> FIFOCache<K, V> newFIFOCache(int capacity, long timeout) {
      return new FIFOCache(capacity, timeout);
   }

   public static <K, V> FIFOCache<K, V> newFIFOCache(int capacity) {
      return new FIFOCache(capacity);
   }

   public static <K, V> LFUCache<K, V> newLFUCache(int capacity, long timeout) {
      return new LFUCache(capacity, timeout);
   }

   public static <K, V> LFUCache<K, V> newLFUCache(int capacity) {
      return new LFUCache(capacity);
   }

   public static <K, V> LRUCache<K, V> newLRUCache(int capacity, long timeout) {
      return new LRUCache(capacity, timeout);
   }

   public static <K, V> LRUCache<K, V> newLRUCache(int capacity) {
      return new LRUCache(capacity);
   }

   public static <K, V> TimedCache<K, V> newTimedCache(long timeout) {
      return new TimedCache(timeout);
   }

   public static <K, V> WeakCache<K, V> newWeakCache(long timeout) {
      return new WeakCache(timeout);
   }

   public static <K, V> NoCache<K, V> newNoCache() {
      return new NoCache();
   }
}
