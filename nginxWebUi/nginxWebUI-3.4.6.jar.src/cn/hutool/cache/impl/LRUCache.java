/*    */ package cn.hutool.cache.impl;
/*    */ 
/*    */ import cn.hutool.core.lang.mutable.Mutable;
/*    */ import cn.hutool.core.map.FixedLinkedHashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
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
/*    */ public class LRUCache<K, V>
/*    */   extends ReentrantCache<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public LRUCache(int capacity) {
/* 28 */     this(capacity, 0L);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LRUCache(int capacity, long timeout) {
/* 37 */     if (Integer.MAX_VALUE == capacity) {
/* 38 */       capacity--;
/*    */     }
/*    */     
/* 41 */     this.capacity = capacity;
/* 42 */     this.timeout = timeout;
/*    */ 
/*    */     
/* 45 */     this.cacheMap = (Map<Mutable<K>, CacheObj<K, V>>)new FixedLinkedHashMap(capacity);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected int pruneCache() {
/* 55 */     if (!isPruneExpiredActive()) {
/* 56 */       return 0;
/*    */     }
/* 58 */     int count = 0;
/* 59 */     Iterator<CacheObj<K, V>> values = cacheObjIter();
/*    */     
/* 61 */     while (values.hasNext()) {
/* 62 */       CacheObj<K, V> co = values.next();
/* 63 */       if (co.isExpired()) {
/* 64 */         values.remove();
/* 65 */         onRemove(co.key, co.obj);
/* 66 */         count++;
/*    */       } 
/*    */     } 
/* 69 */     return count;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\impl\LRUCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */