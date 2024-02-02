/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
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
/*    */ 
/*    */ 
/*    */ class LegacyThreadSetupActionWrapper
/*    */   implements ThreadSetupHandler
/*    */ {
/*    */   private final ThreadSetupAction setupAction;
/*    */   
/*    */   LegacyThreadSetupActionWrapper(ThreadSetupAction setupAction) {
/* 34 */     this.setupAction = setupAction;
/*    */   }
/*    */ 
/*    */   
/*    */   public <T, C> ThreadSetupHandler.Action<T, C> create(final ThreadSetupHandler.Action<T, C> action) {
/* 39 */     return new ThreadSetupHandler.Action<T, C>()
/*    */       {
/*    */         public T call(HttpServerExchange exchange, C context) throws Exception {
/* 42 */           ThreadSetupAction.Handle handle = LegacyThreadSetupActionWrapper.this.setupAction.setup(exchange);
/*    */           try {
/* 44 */             return (T)action.call(exchange, context);
/*    */           } finally {
/* 46 */             if (handle != null)
/* 47 */               handle.tearDown(); 
/*    */           } 
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\LegacyThreadSetupActionWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */