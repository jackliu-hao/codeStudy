/*    */ package cn.hutool.extra.cglib;
/*    */ 
/*    */ import cn.hutool.core.map.WeakConcurrentMap;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.lang.invoke.SerializedLambda;
/*    */ import net.sf.cglib.beans.BeanCopier;
/*    */ import net.sf.cglib.core.Converter;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum BeanCopierCache
/*    */ {
/* 19 */   INSTANCE;
/*    */   BeanCopierCache() {
/* 21 */     this.cache = new WeakConcurrentMap();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final WeakConcurrentMap<String, BeanCopier> cache;
/*    */ 
/*    */ 
/*    */   
/*    */   public BeanCopier get(Class<?> srcClass, Class<?> targetClass, Converter converter) {
/* 32 */     return get(srcClass, targetClass, (null != converter));
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
/*    */   public BeanCopier get(Class<?> srcClass, Class<?> targetClass, boolean useConverter) {
/* 45 */     String key = genKey(srcClass, targetClass, useConverter);
/* 46 */     return (BeanCopier)this.cache.computeIfAbsent(key, () -> BeanCopier.create(srcClass, targetClass, useConverter));
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
/*    */   private String genKey(Class<?> srcClass, Class<?> targetClass, boolean useConverter) {
/* 62 */     StringBuilder key = StrUtil.builder().append(srcClass.getName()).append('#').append(targetClass.getName()).append('#').append(useConverter ? 1 : 0);
/* 63 */     return key.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\cglib\BeanCopierCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */