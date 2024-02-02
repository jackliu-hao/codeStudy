/*    */ package cn.hutool.core.lang.intern;
/*    */ 
/*    */ import cn.hutool.core.map.WeakConcurrentMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WeakInterner<T>
/*    */   implements Interner<T>
/*    */ {
/* 13 */   private final WeakConcurrentMap<T, T> cache = new WeakConcurrentMap();
/*    */ 
/*    */   
/*    */   public T intern(T sample) {
/* 17 */     if (null == sample) {
/* 18 */       return null;
/*    */     }
/* 20 */     return (T)this.cache.computeIfAbsent(sample, key -> sample);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\intern\WeakInterner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */