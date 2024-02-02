/*    */ package freemarker.cache;
/*    */ 
/*    */ import java.util.Map;
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
/*    */ public class StrongCacheStorage
/*    */   implements ConcurrentCacheStorage, CacheStorageWithGetSize
/*    */ {
/* 34 */   private final Map map = new ConcurrentHashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isConcurrent() {
/* 41 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object get(Object key) {
/* 46 */     return this.map.get(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public void put(Object key, Object value) {
/* 51 */     this.map.put(key, value);
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove(Object key) {
/* 56 */     this.map.remove(key);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getSize() {
/* 66 */     return this.map.size();
/*    */   }
/*    */ 
/*    */   
/*    */   public void clear() {
/* 71 */     this.map.clear();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\StrongCacheStorage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */