package com.mysql.cj.util;

import com.mysql.cj.CacheAdapter;
import com.mysql.cj.CacheAdapterFactory;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PerVmServerConfigCacheFactory implements CacheAdapterFactory<String, Map<String, String>> {
   static final ConcurrentHashMap<String, Map<String, String>> serverConfigByUrl = new ConcurrentHashMap();
   private static final CacheAdapter<String, Map<String, String>> serverConfigCache = new CacheAdapter<String, Map<String, String>>() {
      public Map<String, String> get(String key) {
         return (Map)PerVmServerConfigCacheFactory.serverConfigByUrl.get(key);
      }

      public void put(String key, Map<String, String> value) {
         PerVmServerConfigCacheFactory.serverConfigByUrl.putIfAbsent(key, value);
      }

      public void invalidate(String key) {
         PerVmServerConfigCacheFactory.serverConfigByUrl.remove(key);
      }

      public void invalidateAll(Set<String> keys) {
         Iterator var2 = keys.iterator();

         while(var2.hasNext()) {
            String key = (String)var2.next();
            PerVmServerConfigCacheFactory.serverConfigByUrl.remove(key);
         }

      }

      public void invalidateAll() {
         PerVmServerConfigCacheFactory.serverConfigByUrl.clear();
      }
   };

   public CacheAdapter<String, Map<String, String>> getInstance(Object syncMutex, String url, int cacheMaxSize, int maxKeySize) {
      return serverConfigCache;
   }
}
