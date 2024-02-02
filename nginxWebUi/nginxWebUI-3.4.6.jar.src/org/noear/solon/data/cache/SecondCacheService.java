/*    */ package org.noear.solon.data.cache;
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
/*    */ public class SecondCacheService
/*    */   implements CacheService
/*    */ {
/*    */   private CacheService cache1;
/*    */   private CacheService cache2;
/*    */   private int bufferSeconds;
/*    */   
/*    */   public SecondCacheService(CacheService cache1, CacheService cache2) {
/* 20 */     this(cache1, cache2, 5);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SecondCacheService(CacheService cache1, CacheService cache2, int bufferSeconds) {
/* 29 */     this.cache1 = cache1;
/* 30 */     this.cache2 = cache2;
/* 31 */     this.bufferSeconds = bufferSeconds;
/*    */   }
/*    */ 
/*    */   
/*    */   public void store(String key, Object obj, int seconds) {
/* 36 */     this.cache1.store(key, obj, seconds);
/* 37 */     this.cache2.store(key, obj, seconds);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object get(String key) {
/* 42 */     Object temp = this.cache1.get(key);
/* 43 */     if (temp == null) {
/* 44 */       temp = this.cache2.get(key);
/* 45 */       if (this.bufferSeconds > 0 && temp != null) {
/* 46 */         this.cache1.store(key, temp, this.bufferSeconds);
/*    */       }
/*    */     } 
/* 49 */     return temp;
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove(String key) {
/* 54 */     this.cache2.remove(key);
/* 55 */     this.cache1.remove(key);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\cache\SecondCacheService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */