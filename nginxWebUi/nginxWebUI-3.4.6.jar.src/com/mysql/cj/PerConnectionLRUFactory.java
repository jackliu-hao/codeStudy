/*    */ package com.mysql.cj;
/*    */ 
/*    */ import com.mysql.cj.util.LRUCache;
/*    */ import java.util.Set;
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
/*    */ 
/*    */ 
/*    */ public class PerConnectionLRUFactory
/*    */   implements CacheAdapterFactory<String, ParseInfo>
/*    */ {
/*    */   public CacheAdapter<String, ParseInfo> getInstance(Object syncMutex, String url, int cacheMaxSize, int maxKeySize) {
/* 40 */     return new PerConnectionLRU(syncMutex, cacheMaxSize, maxKeySize);
/*    */   }
/*    */   
/*    */   class PerConnectionLRU implements CacheAdapter<String, ParseInfo> {
/*    */     private final int cacheSqlLimit;
/*    */     private final LRUCache<String, ParseInfo> cache;
/*    */     private final Object syncMutex;
/*    */     
/*    */     protected PerConnectionLRU(Object syncMutex, int cacheMaxSize, int maxKeySize) {
/* 49 */       int cacheSize = cacheMaxSize;
/* 50 */       this.cacheSqlLimit = maxKeySize;
/* 51 */       this.cache = new LRUCache(cacheSize);
/* 52 */       this.syncMutex = syncMutex;
/*    */     }
/*    */     
/*    */     public ParseInfo get(String key) {
/* 56 */       if (key == null || key.length() > this.cacheSqlLimit) {
/* 57 */         return null;
/*    */       }
/*    */       
/* 60 */       synchronized (this.syncMutex) {
/* 61 */         return (ParseInfo)this.cache.get(key);
/*    */       } 
/*    */     }
/*    */     
/*    */     public void put(String key, ParseInfo value) {
/* 66 */       if (key == null || key.length() > this.cacheSqlLimit) {
/*    */         return;
/*    */       }
/*    */       
/* 70 */       synchronized (this.syncMutex) {
/* 71 */         this.cache.put(key, value);
/*    */       } 
/*    */     }
/*    */     
/*    */     public void invalidate(String key) {
/* 76 */       synchronized (this.syncMutex) {
/* 77 */         this.cache.remove(key);
/*    */       } 
/*    */     }
/*    */     
/*    */     public void invalidateAll(Set<String> keys) {
/* 82 */       synchronized (this.syncMutex) {
/* 83 */         for (String key : keys) {
/* 84 */           this.cache.remove(key);
/*    */         }
/*    */       } 
/*    */     }
/*    */ 
/*    */     
/*    */     public void invalidateAll() {
/* 91 */       synchronized (this.syncMutex) {
/* 92 */         this.cache.clear();
/*    */       } 
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\PerConnectionLRUFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */