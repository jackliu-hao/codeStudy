package org.h2.util;

import java.util.ArrayList;

public class CacheTQ implements Cache {
   static final String TYPE_NAME = "TQ";
   private final Cache lru;
   private final Cache fifo;
   private final SmallLRUCache<Integer, Object> recentlyUsed = SmallLRUCache.newInstance(1024);
   private int lastUsed = -1;
   private int maxMemory;

   CacheTQ(CacheWriter var1, int var2) {
      this.maxMemory = var2;
      this.lru = new CacheLRU(var1, (int)((double)var2 * 0.8), false);
      this.fifo = new CacheLRU(var1, (int)((double)var2 * 0.2), true);
      this.setMaxMemory(4 * var2);
   }

   public void clear() {
      this.lru.clear();
      this.fifo.clear();
      this.recentlyUsed.clear();
      this.lastUsed = -1;
   }

   public CacheObject find(int var1) {
      CacheObject var2 = this.lru.find(var1);
      if (var2 == null) {
         var2 = this.fifo.find(var1);
      }

      return var2;
   }

   public CacheObject get(int var1) {
      CacheObject var2 = this.lru.find(var1);
      if (var2 != null) {
         return var2;
      } else {
         var2 = this.fifo.find(var1);
         if (var2 != null && !var2.isStream()) {
            if (this.recentlyUsed.get(var1) != null) {
               if (this.lastUsed != var1) {
                  this.fifo.remove(var1);
                  this.lru.put(var2);
               }
            } else {
               this.recentlyUsed.put(var1, this);
            }

            this.lastUsed = var1;
         }

         return var2;
      }
   }

   public ArrayList<CacheObject> getAllChanged() {
      ArrayList var1 = this.lru.getAllChanged();
      ArrayList var2 = this.fifo.getAllChanged();
      ArrayList var3 = new ArrayList(var1.size() + var2.size());
      var3.addAll(var1);
      var3.addAll(var2);
      return var3;
   }

   public int getMaxMemory() {
      return this.maxMemory;
   }

   public int getMemory() {
      return this.lru.getMemory() + this.fifo.getMemory();
   }

   public void put(CacheObject var1) {
      if (var1.isStream()) {
         this.fifo.put(var1);
      } else if (this.recentlyUsed.get(var1.getPos()) != null) {
         this.lru.put(var1);
      } else {
         this.fifo.put(var1);
         this.lastUsed = var1.getPos();
      }

   }

   public boolean remove(int var1) {
      boolean var2 = this.lru.remove(var1);
      if (!var2) {
         var2 = this.fifo.remove(var1);
      }

      this.recentlyUsed.remove(var1);
      return var2;
   }

   public void setMaxMemory(int var1) {
      this.maxMemory = var1;
      this.lru.setMaxMemory((int)((double)var1 * 0.8));
      this.fifo.setMaxMemory((int)((double)var1 * 0.2));
      this.recentlyUsed.setMaxSize(4 * var1);
   }

   public CacheObject update(int var1, CacheObject var2) {
      return this.lru.find(var1) != null ? this.lru.update(var1, var2) : this.fifo.update(var1, var2);
   }
}
