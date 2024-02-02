/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ import java.util.concurrent.atomic.AtomicLong;
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
/*    */ public class RingIndexUtil
/*    */ {
/*    */   public static int ringNextIntByObj(Object object, AtomicInteger atomicInteger) {
/* 31 */     Assert.notNull(object);
/* 32 */     int modulo = CollUtil.size(object);
/* 33 */     return ringNextInt(modulo, atomicInteger);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int ringNextInt(int modulo, AtomicInteger atomicInteger) {
/* 44 */     Assert.notNull(atomicInteger);
/* 45 */     Assert.isTrue((modulo > 0));
/* 46 */     if (modulo <= 1) {
/* 47 */       return 0;
/*    */     }
/*    */     while (true) {
/* 50 */       int current = atomicInteger.get();
/* 51 */       int next = (current + 1) % modulo;
/* 52 */       if (atomicInteger.compareAndSet(current, next)) {
/* 53 */         return next;
/*    */       }
/*    */     } 
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
/*    */   public static long ringNextLong(long modulo, AtomicLong atomicLong) {
/* 67 */     Assert.notNull(atomicLong);
/* 68 */     Assert.isTrue((modulo > 0L));
/* 69 */     if (modulo <= 1L) {
/* 70 */       return 0L;
/*    */     }
/*    */     while (true) {
/* 73 */       long current = atomicLong.get();
/* 74 */       long next = (current + 1L) % modulo;
/* 75 */       if (atomicLong.compareAndSet(current, next))
/* 76 */         return next; 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\RingIndexUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */