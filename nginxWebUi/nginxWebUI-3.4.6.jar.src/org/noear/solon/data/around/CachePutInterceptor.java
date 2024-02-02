/*    */ package org.noear.solon.data.around;
/*    */ 
/*    */ import org.noear.solon.core.aspect.Interceptor;
/*    */ import org.noear.solon.core.aspect.Invocation;
/*    */ import org.noear.solon.data.annotation.CachePut;
/*    */ import org.noear.solon.data.cache.CacheExecutorImp;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CachePutInterceptor
/*    */   implements Interceptor
/*    */ {
/*    */   public Object doIntercept(Invocation inv) throws Throwable {
/* 18 */     Object tmp = inv.invoke();
/*    */     
/* 20 */     CachePut anno = (CachePut)inv.method().getAnnotation(CachePut.class);
/* 21 */     CacheExecutorImp.global
/* 22 */       .cachePut(anno, inv, tmp);
/*    */     
/* 24 */     return tmp;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\around\CachePutInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */