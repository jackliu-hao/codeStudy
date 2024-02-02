/*    */ package io.undertow.servlet.core;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.servlet.api.ThreadSetupHandler;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
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
/*    */ class ServletRequestContextThreadSetupAction
/*    */   implements ThreadSetupHandler
/*    */ {
/* 30 */   static final ServletRequestContextThreadSetupAction INSTANCE = new ServletRequestContextThreadSetupAction();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public <T, C> ThreadSetupHandler.Action<T, C> create(final ThreadSetupHandler.Action<T, C> action) {
/* 38 */     return new ThreadSetupHandler.Action<T, C>()
/*    */       {
/*    */         public T call(HttpServerExchange exchange, C context) throws Exception {
/* 41 */           if (exchange == null) {
/* 42 */             return (T)action.call(null, context);
/*    */           }
/* 44 */           ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 45 */           ServletRequestContext old = ServletRequestContext.current();
/* 46 */           SecurityActions.setCurrentRequestContext(servletRequestContext);
/*    */           try {
/* 48 */             return (T)action.call(exchange, context);
/*    */           } finally {
/* 50 */             ServletRequestContext.setCurrentRequestContext(old);
/*    */           } 
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ServletRequestContextThreadSetupAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */