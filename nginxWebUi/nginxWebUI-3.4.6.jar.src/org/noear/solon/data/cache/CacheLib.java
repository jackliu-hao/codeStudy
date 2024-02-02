/*    */ package org.noear.solon.data.cache;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.noear.solon.annotation.Note;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CacheLib
/*    */ {
/* 19 */   private static Map<String, CacheService> cacheServiceMap = new HashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Note("缓存服务集合；只读")
/*    */   public static Map<String, CacheService> cacheServiceMap() {
/* 27 */     return Collections.unmodifiableMap(cacheServiceMap);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Note("添加缓存服务")
/*    */   public static void cacheServiceAdd(String name, CacheService cs) {
/* 35 */     cacheServiceMap.put(name, cs);
/*    */   }
/*    */   
/*    */   @Note("添加缓存服务")
/*    */   public static void cacheServiceAddIfAbsent(String name, CacheService cs) {
/* 40 */     cacheServiceMap.putIfAbsent(name, cs);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Note("获取缓存服务")
/*    */   public static CacheService cacheServiceGet(String name) {
/* 48 */     return cacheServiceMap.get(name);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 54 */   private static Map<String, CacheFactory> cacheFactoryMap = new HashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void cacheFactoryAdd(String driverType, CacheFactory factory) {
/* 62 */     cacheFactoryMap.put(driverType, factory);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static CacheFactory cacheFactoryGet(String driverType) {
/* 71 */     return cacheFactoryMap.get(driverType);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\cache\CacheLib.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */