package cn.hutool.cache.impl;

import cn.hutool.core.date.DateUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class CacheObj<K, V> implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final K key;
   protected final V obj;
   protected volatile long lastAccess;
   protected AtomicLong accessCount = new AtomicLong();
   protected final long ttl;

   protected CacheObj(K key, V obj, long ttl) {
      this.key = key;
      this.obj = obj;
      this.ttl = ttl;
      this.lastAccess = System.currentTimeMillis();
   }

   public K getKey() {
      return this.key;
   }

   public V getValue() {
      return this.obj;
   }

   public long getTtl() {
      return this.ttl;
   }

   public Date getExpiredTime() {
      return this.ttl > 0L ? DateUtil.date(this.lastAccess + this.ttl) : null;
   }

   public long getLastAccess() {
      return this.lastAccess;
   }

   public String toString() {
      return "CacheObj [key=" + this.key + ", obj=" + this.obj + ", lastAccess=" + this.lastAccess + ", accessCount=" + this.accessCount + ", ttl=" + this.ttl + "]";
   }

   protected boolean isExpired() {
      if (this.ttl > 0L) {
         return System.currentTimeMillis() - this.lastAccess > this.ttl;
      } else {
         return false;
      }
   }

   protected V get(boolean isUpdateLastAccess) {
      if (isUpdateLastAccess) {
         this.lastAccess = System.currentTimeMillis();
      }

      this.accessCount.getAndIncrement();
      return this.obj;
   }
}
