/*    */ package freemarker.cache;
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
/*    */ public class NullCacheStorage
/*    */   implements ConcurrentCacheStorage, CacheStorageWithGetSize
/*    */ {
/* 35 */   public static final NullCacheStorage INSTANCE = new NullCacheStorage();
/*    */ 
/*    */   
/*    */   public boolean isConcurrent() {
/* 39 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object get(Object key) {
/* 44 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void put(Object key, Object value) {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void remove(Object key) {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void clear() {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getSize() {
/* 69 */     return 0;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\NullCacheStorage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */