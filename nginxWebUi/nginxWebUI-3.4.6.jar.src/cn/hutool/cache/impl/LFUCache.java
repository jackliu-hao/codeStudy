/*    */ package cn.hutool.cache.impl;
/*    */ 
/*    */ import cn.hutool.core.lang.mutable.Mutable;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
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
/*    */ public class LFUCache<K, V>
/*    */   extends StampedCache<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public LFUCache(int capacity) {
/* 27 */     this(capacity, 0L);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LFUCache(int capacity, long timeout) {
/* 37 */     if (Integer.MAX_VALUE == capacity) {
/* 38 */       capacity--;
/*    */     }
/*    */     
/* 41 */     this.capacity = capacity;
/* 42 */     this.timeout = timeout;
/* 43 */     this.cacheMap = new HashMap<>(capacity + 1, 1.0F);
/*    */   }
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
/*    */   protected int pruneCache() {
/* 56 */     int count = 0;
/* 57 */     CacheObj<K, V> comin = null;
/*    */ 
/*    */     
/* 60 */     Iterator<CacheObj<K, V>> values = cacheObjIter();
/*    */     
/* 62 */     while (values.hasNext()) {
/* 63 */       CacheObj<K, V> co = values.next();
/* 64 */       if (co.isExpired() == true) {
/* 65 */         values.remove();
/* 66 */         onRemove(co.key, co.obj);
/* 67 */         count++;
/*    */         
/*    */         continue;
/*    */       } 
/*    */       
/* 72 */       if (comin == null || co.accessCount.get() < comin.accessCount.get()) {
/* 73 */         comin = co;
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 78 */     if (isFull() && comin != null) {
/* 79 */       long minAccessCount = comin.accessCount.get();
/*    */       
/* 81 */       values = cacheObjIter();
/*    */       
/* 83 */       while (values.hasNext()) {
/* 84 */         CacheObj<K, V> co1 = values.next();
/* 85 */         if (co1.accessCount.addAndGet(-minAccessCount) <= 0L) {
/* 86 */           values.remove();
/* 87 */           onRemove(co1.key, co1.obj);
/* 88 */           count++;
/*    */         } 
/*    */       } 
/*    */     } 
/*    */     
/* 93 */     return count;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\impl\LFUCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */