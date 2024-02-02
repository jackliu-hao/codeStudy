package org.h2.util;

import java.util.ArrayList;
import java.util.Map;

class CacheSecondLevel implements Cache {
   private final Cache baseCache;
   private final Map<Integer, CacheObject> map;

   CacheSecondLevel(Cache var1, Map<Integer, CacheObject> var2) {
      this.baseCache = var1;
      this.map = var2;
   }

   public void clear() {
      this.map.clear();
      this.baseCache.clear();
   }

   public CacheObject find(int var1) {
      CacheObject var2 = this.baseCache.find(var1);
      if (var2 == null) {
         var2 = (CacheObject)this.map.get(var1);
      }

      return var2;
   }

   public CacheObject get(int var1) {
      CacheObject var2 = this.baseCache.get(var1);
      if (var2 == null) {
         var2 = (CacheObject)this.map.get(var1);
      }

      return var2;
   }

   public ArrayList<CacheObject> getAllChanged() {
      return this.baseCache.getAllChanged();
   }

   public int getMaxMemory() {
      return this.baseCache.getMaxMemory();
   }

   public int getMemory() {
      return this.baseCache.getMemory();
   }

   public void put(CacheObject var1) {
      this.baseCache.put(var1);
      this.map.put(var1.getPos(), var1);
   }

   public boolean remove(int var1) {
      boolean var2 = this.baseCache.remove(var1);
      var2 |= this.map.remove(var1) != null;
      return var2;
   }

   public void setMaxMemory(int var1) {
      this.baseCache.setMaxMemory(var1);
   }

   public CacheObject update(int var1, CacheObject var2) {
      CacheObject var3 = this.baseCache.update(var1, var2);
      this.map.put(var1, var2);
      return var3;
   }
}
