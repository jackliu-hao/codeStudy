package org.h2.util;

import java.util.ArrayList;
import java.util.Collections;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;

public class CacheLRU implements Cache {
   static final String TYPE_NAME = "LRU";
   private final CacheWriter writer;
   private final boolean fifo;
   private final CacheObject head = new CacheHead();
   private final int mask;
   private CacheObject[] values;
   private int recordCount;
   private final int len;
   private long maxMemory;
   private long memory;

   CacheLRU(CacheWriter var1, int var2, boolean var3) {
      this.writer = var1;
      this.fifo = var3;
      this.setMaxMemory(var2);

      try {
         long var4 = this.maxMemory / 64L;
         if (var4 > 2147483647L) {
            throw new IllegalArgumentException();
         }

         this.len = MathUtils.nextPowerOf2((int)var4);
      } catch (IllegalArgumentException var6) {
         throw new IllegalStateException("This much cache memory is not supported: " + var2 + "kb", var6);
      }

      this.mask = this.len - 1;
      this.clear();
   }

   public static Cache getCache(CacheWriter var0, String var1, int var2) {
      SoftValuesHashMap var3 = null;
      if (var1.startsWith("SOFT_")) {
         var3 = new SoftValuesHashMap();
         var1 = var1.substring("SOFT_".length());
      }

      Object var4;
      if ("LRU".equals(var1)) {
         var4 = new CacheLRU(var0, var2, false);
      } else {
         if (!"TQ".equals(var1)) {
            throw DbException.getInvalidValueException("CACHE_TYPE", var1);
         }

         var4 = new CacheTQ(var0, var2);
      }

      if (var3 != null) {
         var4 = new CacheSecondLevel((Cache)var4, var3);
      }

      return (Cache)var4;
   }

   public void clear() {
      this.head.cacheNext = this.head.cachePrevious = this.head;
      this.values = null;
      this.values = new CacheObject[this.len];
      this.recordCount = 0;
      this.memory = (long)this.len * 8L;
   }

   public void put(CacheObject var1) {
      int var2;
      if (SysProperties.CHECK) {
         var2 = var1.getPos();
         CacheObject var3 = this.find(var2);
         if (var3 != null) {
            throw DbException.getInternalError("try to add a record twice at pos " + var2);
         }
      }

      var2 = var1.getPos() & this.mask;
      var1.cacheChained = this.values[var2];
      this.values[var2] = var1;
      ++this.recordCount;
      this.memory += (long)var1.getMemory();
      this.addToFront(var1);
      this.removeOldIfRequired();
   }

   public CacheObject update(int var1, CacheObject var2) {
      CacheObject var3 = this.find(var1);
      if (var3 == null) {
         this.put(var2);
      } else {
         if (var3 != var2) {
            throw DbException.getInternalError("old!=record pos:" + var1 + " old:" + var3 + " new:" + var2);
         }

         if (!this.fifo) {
            this.removeFromLinkedList(var2);
            this.addToFront(var2);
         }
      }

      return var3;
   }

   private void removeOldIfRequired() {
      if (this.memory >= this.maxMemory) {
         this.removeOld();
      }

   }

   private void removeOld() {
      int var1 = 0;
      ArrayList var2 = new ArrayList();
      long var3 = this.memory;
      int var5 = this.recordCount;
      boolean var6 = false;
      CacheObject var7 = this.head.cacheNext;

      while(var5 > 16) {
         if (var2.isEmpty()) {
            if (var3 <= this.maxMemory) {
               break;
            }
         } else if (var3 * 4L <= this.maxMemory * 3L) {
            break;
         }

         CacheObject var8 = var7;
         var7 = var7.cacheNext;
         ++var1;
         if (var1 >= this.recordCount) {
            if (var6) {
               this.writer.getTrace().info("cannot remove records, cache size too small? records:" + this.recordCount + " memory:" + this.memory);
               break;
            }

            this.writer.flushLog();
            var6 = true;
            var1 = 0;
         }

         if (var8 == this.head) {
            throw DbException.getInternalError("try to remove head");
         }

         if (!var8.canRemove()) {
            this.removeFromLinkedList(var8);
            this.addToFront(var8);
         } else {
            --var5;
            var3 -= (long)var8.getMemory();
            if (var8.isChanged()) {
               var2.add(var8);
            } else {
               this.remove(var8.getPos());
            }
         }
      }

      if (!var2.isEmpty()) {
         if (!var6) {
            this.writer.flushLog();
         }

         Collections.sort(var2);
         long var15 = this.maxMemory;
         int var10 = var2.size();

         CacheObject var11;
         try {
            this.maxMemory = Long.MAX_VALUE;

            for(var1 = 0; var1 < var10; ++var1) {
               var11 = (CacheObject)var2.get(var1);
               this.writer.writeBack(var11);
            }
         } finally {
            this.maxMemory = var15;
         }

         for(var1 = 0; var1 < var10; ++var1) {
            var11 = (CacheObject)var2.get(var1);
            this.remove(var11.getPos());
            if (var11.cacheNext != null) {
               throw DbException.getInternalError();
            }
         }
      }

   }

   private void addToFront(CacheObject var1) {
      if (var1 == this.head) {
         throw DbException.getInternalError("try to move head");
      } else {
         var1.cacheNext = this.head;
         var1.cachePrevious = this.head.cachePrevious;
         var1.cachePrevious.cacheNext = var1;
         this.head.cachePrevious = var1;
      }
   }

   private void removeFromLinkedList(CacheObject var1) {
      if (var1 == this.head) {
         throw DbException.getInternalError("try to remove head");
      } else {
         var1.cachePrevious.cacheNext = var1.cacheNext;
         var1.cacheNext.cachePrevious = var1.cachePrevious;
         var1.cacheNext = null;
         var1.cachePrevious = null;
      }
   }

   public boolean remove(int var1) {
      int var2 = var1 & this.mask;
      CacheObject var3 = this.values[var2];
      if (var3 == null) {
         return false;
      } else {
         CacheObject var4;
         if (var3.getPos() == var1) {
            this.values[var2] = var3.cacheChained;
         } else {
            do {
               var4 = var3;
               var3 = var3.cacheChained;
               if (var3 == null) {
                  return false;
               }
            } while(var3.getPos() != var1);

            var4.cacheChained = var3.cacheChained;
         }

         --this.recordCount;
         this.memory -= (long)var3.getMemory();
         this.removeFromLinkedList(var3);
         if (SysProperties.CHECK) {
            var3.cacheChained = null;
            var4 = this.find(var1);
            if (var4 != null) {
               throw DbException.getInternalError("not removed: " + var4);
            }
         }

         return true;
      }
   }

   public CacheObject find(int var1) {
      CacheObject var2;
      for(var2 = this.values[var1 & this.mask]; var2 != null && var2.getPos() != var1; var2 = var2.cacheChained) {
      }

      return var2;
   }

   public CacheObject get(int var1) {
      CacheObject var2 = this.find(var1);
      if (var2 != null && !this.fifo) {
         this.removeFromLinkedList(var2);
         this.addToFront(var2);
      }

      return var2;
   }

   public ArrayList<CacheObject> getAllChanged() {
      ArrayList var1 = new ArrayList();

      for(CacheObject var2 = this.head.cacheNext; var2 != this.head; var2 = var2.cacheNext) {
         if (var2.isChanged()) {
            var1.add(var2);
         }
      }

      return var1;
   }

   public void setMaxMemory(int var1) {
      long var2 = (long)var1 * 1024L / 4L;
      this.maxMemory = var2 < 0L ? 0L : var2;
      this.removeOldIfRequired();
   }

   public int getMaxMemory() {
      return (int)(this.maxMemory * 4L / 1024L);
   }

   public int getMemory() {
      return (int)(this.memory * 4L / 1024L);
   }
}
