/*    */ package io.undertow.servlet.core;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.servlet.api.ThreadSetupHandler;
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
/*    */ public class ContextClassLoaderSetupAction
/*    */   implements ThreadSetupHandler
/*    */ {
/*    */   private final ClassLoader classLoader;
/*    */   
/*    */   public ContextClassLoaderSetupAction(ClassLoader classLoader) {
/* 32 */     this.classLoader = classLoader;
/*    */   }
/*    */ 
/*    */   
/*    */   public <T, C> ThreadSetupHandler.Action<T, C> create(final ThreadSetupHandler.Action<T, C> action) {
/* 37 */     return new ThreadSetupHandler.Action<T, C>()
/*    */       {
/*    */         public T call(HttpServerExchange exchange, C context) throws Exception {
/* 40 */           ClassLoader old = SecurityActions.getContextClassLoader();
/* 41 */           SecurityActions.setContextClassLoader(ContextClassLoaderSetupAction.this.classLoader);
/*    */           try {
/* 43 */             return (T)action.call(exchange, context);
/*    */           } finally {
/* 45 */             SecurityActions.setContextClassLoader(old);
/*    */           } 
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ContextClassLoaderSetupAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */