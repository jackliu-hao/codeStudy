/*    */ package org.noear.solon.data.around;
/*    */ 
/*    */ import org.noear.solon.core.aspect.Interceptor;
/*    */ import org.noear.solon.core.aspect.Invocation;
/*    */ import org.noear.solon.data.annotation.Cache;
/*    */ import org.noear.solon.data.cache.CacheExecutorImp;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CacheInterceptor
/*    */   implements Interceptor
/*    */ {
/*    */   public Object doIntercept(Invocation inv) throws Throwable {
/* 17 */     Cache anno = (Cache)inv.method().getAnnotation(Cache.class);
/*    */     
/* 19 */     return CacheExecutorImp.global
/* 20 */       .cache(anno, inv, () -> inv.invoke());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\around\CacheInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */