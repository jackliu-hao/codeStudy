/*    */ package cn.hutool.core.bean;
/*    */ 
/*    */ import cn.hutool.core.lang.func.Func0;
/*    */ import cn.hutool.core.map.ReferenceConcurrentMap;
/*    */ import cn.hutool.core.map.WeakConcurrentMap;
/*    */ import java.beans.PropertyDescriptor;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum BeanInfoCache
/*    */ {
/* 17 */   INSTANCE;
/*    */   BeanInfoCache() {
/* 19 */     this.pdCache = new WeakConcurrentMap();
/* 20 */     this.ignoreCasePdCache = new WeakConcurrentMap();
/*    */   }
/*    */ 
/*    */   
/*    */   private final WeakConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> ignoreCasePdCache;
/*    */   
/*    */   private final WeakConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> pdCache;
/*    */ 
/*    */   
/*    */   public Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> beanClass, boolean ignoreCase) {
/* 30 */     return (Map<String, PropertyDescriptor>)getCache(ignoreCase).get(beanClass);
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
/*    */ 
/*    */ 
/*    */   
/*    */   public Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> beanClass, boolean ignoreCase, Func0<Map<String, PropertyDescriptor>> supplier) {
/* 46 */     return (Map<String, PropertyDescriptor>)getCache(ignoreCase).computeIfAbsent(beanClass, key -> (Map)supplier.callWithRuntimeException());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void putPropertyDescriptorMap(Class<?> beanClass, Map<String, PropertyDescriptor> fieldNamePropertyDescriptorMap, boolean ignoreCase) {
/* 57 */     getCache(ignoreCase).put(beanClass, fieldNamePropertyDescriptorMap);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void clear() {
/* 66 */     this.pdCache.clear();
/* 67 */     this.ignoreCasePdCache.clear();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private ReferenceConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> getCache(boolean ignoreCase) {
/* 78 */     return ignoreCase ? (ReferenceConcurrentMap<Class<?>, Map<String, PropertyDescriptor>>)this.ignoreCasePdCache : (ReferenceConcurrentMap<Class<?>, Map<String, PropertyDescriptor>>)this.pdCache;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\BeanInfoCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */