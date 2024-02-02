package org.noear.solon.data.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.noear.solon.annotation.Note;

public class CacheLib {
   private static Map<String, CacheService> cacheServiceMap = new HashMap();
   private static Map<String, CacheFactory> cacheFactoryMap = new HashMap();

   @Note("缓存服务集合；只读")
   public static Map<String, CacheService> cacheServiceMap() {
      return Collections.unmodifiableMap(cacheServiceMap);
   }

   @Note("添加缓存服务")
   public static void cacheServiceAdd(String name, CacheService cs) {
      cacheServiceMap.put(name, cs);
   }

   @Note("添加缓存服务")
   public static void cacheServiceAddIfAbsent(String name, CacheService cs) {
      cacheServiceMap.putIfAbsent(name, cs);
   }

   @Note("获取缓存服务")
   public static CacheService cacheServiceGet(String name) {
      return (CacheService)cacheServiceMap.get(name);
   }

   public static void cacheFactoryAdd(String driverType, CacheFactory factory) {
      cacheFactoryMap.put(driverType, factory);
   }

   public static CacheFactory cacheFactoryGet(String driverType) {
      return (CacheFactory)cacheFactoryMap.get(driverType);
   }
}
