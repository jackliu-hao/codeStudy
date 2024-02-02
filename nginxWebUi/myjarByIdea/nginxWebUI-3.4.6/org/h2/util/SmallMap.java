package org.h2.util;

import java.util.HashMap;
import java.util.Iterator;
import org.h2.message.DbException;

public class SmallMap {
   private final HashMap<Integer, Object> map = new HashMap();
   private Object cache;
   private int cacheId;
   private int lastId;
   private final int maxElements;

   public SmallMap(int var1) {
      this.maxElements = var1;
   }

   public int addObject(int var1, Object var2) {
      if (this.map.size() > this.maxElements * 2) {
         Iterator var3 = this.map.keySet().iterator();

         while(var3.hasNext()) {
            Integer var4 = (Integer)var3.next();
            if (var4 + this.maxElements < this.lastId) {
               var3.remove();
            }
         }
      }

      if (var1 > this.lastId) {
         this.lastId = var1;
      }

      this.map.put(var1, var2);
      this.cacheId = var1;
      this.cache = var2;
      return var1;
   }

   public void freeObject(int var1) {
      if (this.cacheId == var1) {
         this.cacheId = -1;
         this.cache = null;
      }

      this.map.remove(var1);
   }

   public Object getObject(int var1, boolean var2) {
      if (var1 == this.cacheId) {
         return this.cache;
      } else {
         Object var3 = this.map.get(var1);
         if (var3 == null && !var2) {
            throw DbException.get(90007);
         } else {
            return var3;
         }
      }
   }
}
