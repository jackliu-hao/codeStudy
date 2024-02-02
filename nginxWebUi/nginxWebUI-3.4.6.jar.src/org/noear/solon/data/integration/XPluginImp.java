/*    */ package org.noear.solon.data.integration;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.BeanWrap;
/*    */ import org.noear.solon.core.aspect.Interceptor;
/*    */ import org.noear.solon.data.annotation.Cache;
/*    */ import org.noear.solon.data.annotation.CachePut;
/*    */ import org.noear.solon.data.around.CacheRemoveInterceptor;
/*    */ import org.noear.solon.data.cache.CacheLib;
/*    */ import org.noear.solon.data.cache.CacheService;
/*    */ import org.noear.solon.data.cache.LocalCacheFactoryImpl;
/*    */ import org.noear.solon.data.cache.LocalCacheService;
/*    */ import org.noear.solon.data.tran.TranExecutor;
/*    */ import org.noear.solon.data.tran.TranExecutorImp;
/*    */ 
/*    */ public class XPluginImp implements Plugin {
/*    */   public void start(AopContext context) {
/* 18 */     CacheLib.cacheFactoryAdd("local", (CacheFactory)new LocalCacheFactoryImpl());
/*    */ 
/*    */     
/* 21 */     if (Solon.app().enableTransaction()) {
/* 22 */       context.wrapAndPut(TranExecutor.class, TranExecutorImp.global);
/*    */       
/* 24 */       context.beanAroundAdd(Tran.class, (Interceptor)new TranInterceptor(), 120);
/*    */     } 
/*    */ 
/*    */     
/* 28 */     if (Solon.app().enableCaching()) {
/* 29 */       CacheLib.cacheServiceAddIfAbsent("", LocalCacheService.instance);
/*    */       
/* 31 */       Solon.app().onEvent(BeanWrap.class, (EventListener)new CacheServiceEventListener());
/*    */       
/* 33 */       context.beanOnloaded(ctx -> {
/*    */             if (!ctx.hasWrap(CacheService.class)) {
/*    */               ctx.wrapAndPut(CacheService.class, LocalCacheService.instance);
/*    */             }
/*    */           });
/*    */       
/* 39 */       context.beanAroundAdd(CachePut.class, (Interceptor)new CachePutInterceptor(), 110);
/* 40 */       context.beanAroundAdd(CacheRemove.class, (Interceptor)new CacheRemoveInterceptor(), 110);
/* 41 */       context.beanAroundAdd(Cache.class, (Interceptor)new CacheInterceptor(), 111);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\integration\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */