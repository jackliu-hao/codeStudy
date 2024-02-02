package org.noear.solon.data.cache;

import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.data.util.InvKeys;
import org.noear.solon.ext.SupplierEx;

public class CacheExecutorImp {
   public static final CacheExecutorImp global = new CacheExecutorImp();

   public Object cache(Cache anno, Invocation inv, SupplierEx executor) throws Throwable {
      if (anno == null) {
         return executor.get();
      } else {
         String key = anno.key();
         if (Utils.isEmpty(key)) {
            key = InvKeys.buildByInv(inv);
         } else {
            key = InvKeys.buildByTmlAndInv(key, inv);
         }

         Object result = null;
         CacheService cs = CacheLib.cacheServiceGet(anno.service());
         String keyLock = key + ":lock";
         synchronized(keyLock.intern()) {
            result = cs.get(key);
            if (result == null) {
               result = executor.get();
               if (result != null) {
                  cs.store(key, result, anno.seconds());
                  if (Utils.isNotEmpty(anno.tags())) {
                     String tags = InvKeys.buildByTmlAndInv(anno.tags(), inv, result);
                     CacheTags ct = new CacheTags(cs);
                     String[] var11 = tags.split(",");
                     int var12 = var11.length;

                     for(int var13 = 0; var13 < var12; ++var13) {
                        String tag = var11[var13];
                        ct.add(tag, key);
                     }
                  }
               }
            }

            return result;
         }
      }
   }

   public void cacheRemove(CacheRemove anno, Invocation inv, Object rstValue) {
      if (anno != null) {
         CacheService cs = CacheLib.cacheServiceGet(anno.service());
         String tags;
         int var8;
         if (Utils.isNotEmpty(anno.keys())) {
            tags = InvKeys.buildByTmlAndInv(anno.keys(), inv, rstValue);
            String[] var6 = tags.split(",");
            int var7 = var6.length;

            for(var8 = 0; var8 < var7; ++var8) {
               String key = var6[var8];
               cs.remove(key);
            }
         }

         if (Utils.isNotEmpty(anno.tags())) {
            tags = InvKeys.buildByTmlAndInv(anno.tags(), inv, rstValue);
            CacheTags ct = new CacheTags(cs);
            String[] var12 = tags.split(",");
            var8 = var12.length;

            for(int var13 = 0; var13 < var8; ++var13) {
               String tag = var12[var13];
               ct.remove(tag);
            }
         }

      }
   }

   public void cachePut(CachePut anno, Invocation inv, Object rstValue) {
      if (anno != null) {
         CacheService cs = CacheLib.cacheServiceGet(anno.service());
         String tags;
         if (Utils.isNotEmpty(anno.key())) {
            tags = InvKeys.buildByTmlAndInv(anno.key(), inv, rstValue);
            cs.store(tags, rstValue, anno.seconds());
         }

         if (Utils.isNotEmpty(anno.tags())) {
            tags = InvKeys.buildByTmlAndInv(anno.tags(), inv, rstValue);
            CacheTags ct = new CacheTags(cs);
            String[] var7 = tags.split(",");
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               String tag = var7[var9];
               ct.update(tag, rstValue, anno.seconds());
            }
         }

      }
   }
}
