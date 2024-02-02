/*    */ package cn.hutool.cache.impl;
/*    */ 
/*    */ import cn.hutool.cache.GlobalPruneTimer;
/*    */ import cn.hutool.core.lang.mutable.Mutable;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ScheduledFuture;
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
/*    */ public class TimedCache<K, V>
/*    */   extends StampedCache<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private ScheduledFuture<?> pruneJobFuture;
/*    */   
/*    */   public TimedCache(long timeout) {
/* 32 */     this(timeout, new HashMap<>());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TimedCache(long timeout, Map<Mutable<K>, CacheObj<K, V>> map) {
/* 42 */     this.capacity = 0;
/* 43 */     this.timeout = timeout;
/* 44 */     this.cacheMap = map;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected int pruneCache() {
/* 55 */     int count = 0;
/* 56 */     Iterator<CacheObj<K, V>> values = cacheObjIter();
/*    */     
/* 58 */     while (values.hasNext()) {
/* 59 */       CacheObj<K, V> co = values.next();
/* 60 */       if (co.isExpired()) {
/* 61 */         values.remove();
/* 62 */         onRemove(co.key, co.obj);
/* 63 */         count++;
/*    */       } 
/*    */     } 
/* 66 */     return count;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void schedulePrune(long delay) {
/* 76 */     this.pruneJobFuture = GlobalPruneTimer.INSTANCE.schedule(this::prune, delay);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void cancelPruneSchedule() {
/* 83 */     if (null != this.pruneJobFuture)
/* 84 */       this.pruneJobFuture.cancel(true); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\impl\TimedCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */