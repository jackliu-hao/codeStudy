/*    */ package cn.hutool.cache.impl;
/*    */ 
/*    */ import cn.hutool.core.lang.mutable.Mutable;
/*    */ import java.util.Iterator;
/*    */ import java.util.LinkedHashMap;
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
/*    */ public class FIFOCache<K, V>
/*    */   extends StampedCache<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public FIFOCache(int capacity) {
/* 28 */     this(capacity, 0L);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FIFOCache(int capacity, long timeout) {
/* 38 */     this.capacity = capacity;
/* 39 */     this.timeout = timeout;
/* 40 */     this.cacheMap = new LinkedHashMap<>(capacity + 1, 1.0F, false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected int pruneCache() {
/* 49 */     int count = 0;
/* 50 */     CacheObj<K, V> first = null;
/*    */ 
/*    */     
/* 53 */     Iterator<CacheObj<K, V>> values = cacheObjIter();
/* 54 */     if (isPruneExpiredActive()) {
/*    */       
/* 56 */       while (values.hasNext()) {
/* 57 */         CacheObj<K, V> co = values.next();
/* 58 */         if (co.isExpired()) {
/* 59 */           values.remove();
/* 60 */           onRemove(co.key, co.obj);
/* 61 */           count++;
/*    */           continue;
/*    */         } 
/* 64 */         if (first == null) {
/* 65 */           first = co;
/*    */         }
/*    */       } 
/*    */     } else {
/* 69 */       first = values.hasNext() ? values.next() : null;
/*    */     } 
/*    */ 
/*    */     
/* 73 */     if (isFull() && null != first) {
/* 74 */       removeWithoutLock(first.key, false);
/* 75 */       onRemove(first.key, first.obj);
/* 76 */       count++;
/*    */     } 
/* 78 */     return count;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\impl\FIFOCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */