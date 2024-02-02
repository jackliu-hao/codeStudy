/*    */ package cn.hutool.core.bean;
/*    */ 
/*    */ import cn.hutool.core.lang.func.Func0;
/*    */ import cn.hutool.core.map.WeakConcurrentMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum BeanDescCache
/*    */ {
/* 13 */   INSTANCE;
/*    */   BeanDescCache() {
/* 15 */     this.bdCache = new WeakConcurrentMap();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final WeakConcurrentMap<Class<?>, BeanDesc> bdCache;
/*    */ 
/*    */ 
/*    */   
/*    */   public BeanDesc getBeanDesc(Class<?> beanClass, Func0<BeanDesc> supplier) {
/* 26 */     return (BeanDesc)this.bdCache.computeIfAbsent(beanClass, key -> (BeanDesc)supplier.callWithRuntimeException());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void clear() {
/* 35 */     this.bdCache.clear();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\BeanDescCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */