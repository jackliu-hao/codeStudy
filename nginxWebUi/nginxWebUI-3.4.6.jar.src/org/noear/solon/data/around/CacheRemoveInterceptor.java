/*    */ package org.noear.solon.data.around;
/*    */ 
/*    */ import org.noear.solon.core.aspect.Interceptor;
/*    */ import org.noear.solon.core.aspect.Invocation;
/*    */ import org.noear.solon.data.annotation.CacheRemove;
/*    */ import org.noear.solon.data.cache.CacheExecutorImp;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CacheRemoveInterceptor
/*    */   implements Interceptor
/*    */ {
/*    */   public Object doIntercept(Invocation inv) throws Throwable {
/* 17 */     Object tmp = inv.invoke();
/*    */     
/* 19 */     CacheRemove anno = (CacheRemove)inv.method().getAnnotation(CacheRemove.class);
/* 20 */     CacheExecutorImp.global
/* 21 */       .cacheRemove(anno, inv, tmp);
/*    */     
/* 23 */     return tmp;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\around\CacheRemoveInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */