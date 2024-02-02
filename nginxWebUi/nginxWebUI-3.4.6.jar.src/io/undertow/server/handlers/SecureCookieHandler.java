/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.server.HandlerWrapper;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.ResponseCommitListener;
/*    */ import io.undertow.server.SecureCookieCommitListener;
/*    */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
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
/*    */ public class SecureCookieHandler
/*    */   implements HttpHandler
/*    */ {
/* 36 */   public static final HandlerWrapper WRAPPER = new HandlerWrapper()
/*    */     {
/*    */       public HttpHandler wrap(HttpHandler handler) {
/* 39 */         return new SecureCookieHandler(handler);
/*    */       }
/*    */     };
/*    */   
/*    */   private final HttpHandler next;
/*    */   
/*    */   public SecureCookieHandler(HttpHandler next) {
/* 46 */     this.next = next;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 51 */     if (exchange.isSecure()) {
/* 52 */       exchange.addResponseCommitListener((ResponseCommitListener)SecureCookieCommitListener.INSTANCE);
/*    */     }
/* 54 */     this.next.handleRequest(exchange);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 59 */     return "secure-cookie()";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements HandlerBuilder
/*    */   {
/*    */     public String name() {
/* 66 */       return "secure-cookie";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 71 */       return Collections.emptyMap();
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 76 */       return Collections.emptySet();
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 81 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public HandlerWrapper build(Map<String, Object> config) {
/* 86 */       return SecureCookieHandler.WRAPPER;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\SecureCookieHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */