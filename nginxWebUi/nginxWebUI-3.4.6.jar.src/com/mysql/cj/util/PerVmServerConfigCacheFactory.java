/*    */ package com.mysql.cj.util;
/*    */ 
/*    */ import com.mysql.cj.CacheAdapter;
/*    */ import com.mysql.cj.CacheAdapterFactory;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.concurrent.ConcurrentHashMap;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PerVmServerConfigCacheFactory
/*    */   implements CacheAdapterFactory<String, Map<String, String>>
/*    */ {
/* 40 */   static final ConcurrentHashMap<String, Map<String, String>> serverConfigByUrl = new ConcurrentHashMap<>();
/*    */   
/* 42 */   private static final CacheAdapter<String, Map<String, String>> serverConfigCache = new CacheAdapter<String, Map<String, String>>()
/*    */     {
/*    */       public Map<String, String> get(String key) {
/* 45 */         return PerVmServerConfigCacheFactory.serverConfigByUrl.get(key);
/*    */       }
/*    */       
/*    */       public void put(String key, Map<String, String> value) {
/* 49 */         PerVmServerConfigCacheFactory.serverConfigByUrl.putIfAbsent(key, value);
/*    */       }
/*    */       
/*    */       public void invalidate(String key) {
/* 53 */         PerVmServerConfigCacheFactory.serverConfigByUrl.remove(key);
/*    */       }
/*    */       
/*    */       public void invalidateAll(Set<String> keys) {
/* 57 */         for (String key : keys) {
/* 58 */           PerVmServerConfigCacheFactory.serverConfigByUrl.remove(key);
/*    */         }
/*    */       }
/*    */       
/*    */       public void invalidateAll() {
/* 63 */         PerVmServerConfigCacheFactory.serverConfigByUrl.clear();
/*    */       }
/*    */     };
/*    */   
/*    */   public CacheAdapter<String, Map<String, String>> getInstance(Object syncMutex, String url, int cacheMaxSize, int maxKeySize) {
/* 68 */     return serverConfigCache;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\PerVmServerConfigCacheFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */