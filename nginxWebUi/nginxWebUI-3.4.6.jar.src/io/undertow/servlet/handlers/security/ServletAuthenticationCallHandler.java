/*    */ package io.undertow.servlet.handlers.security;
/*    */ 
/*    */ import io.undertow.security.api.SecurityContext;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServletAuthenticationCallHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   
/*    */   public ServletAuthenticationCallHandler(HttpHandler next) {
/* 40 */     this.next = next;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 50 */     if (exchange.isInIoThread()) {
/* 51 */       exchange.dispatch(this);
/*    */       return;
/*    */     } 
/* 54 */     SecurityContext context = exchange.getSecurityContext();
/* 55 */     if (context.authenticate()) {
/* 56 */       if (!exchange.isComplete()) {
/* 57 */         this.next.handleRequest(exchange);
/*    */       }
/*    */     }
/* 60 */     else if (exchange.getStatusCode() >= 400 && !exchange.isComplete()) {
/* 61 */       ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 62 */       src.getOriginalResponse().sendError(exchange.getStatusCode());
/*    */     } else {
/* 64 */       exchange.endExchange();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\security\ServletAuthenticationCallHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */