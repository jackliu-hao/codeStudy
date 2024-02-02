/*    */ package io.undertow.security.handlers;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.Headers;
/*    */ import java.net.URI;
/*    */ import java.net.URISyntaxException;
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
/*    */ public abstract class AbstractConfidentialityHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   
/*    */   protected AbstractConfidentialityHandler(HttpHandler next) {
/* 40 */     this.next = next;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 45 */     if (isConfidential(exchange) || !confidentialityRequired(exchange)) {
/* 46 */       this.next.handleRequest(exchange);
/*    */     } else {
/*    */       try {
/* 49 */         URI redirectUri = getRedirectURI(exchange);
/* 50 */         UndertowLogger.SECURITY_LOGGER.debugf("Redirecting request %s to %s to meet confidentiality requirements", exchange, redirectUri);
/* 51 */         exchange.setStatusCode(302);
/* 52 */         exchange.getResponseHeaders().put(Headers.LOCATION, redirectUri.toString());
/* 53 */       } catch (Exception e) {
/* 54 */         UndertowLogger.REQUEST_LOGGER.exceptionProcessingRequest(e);
/* 55 */         exchange.setStatusCode(500);
/*    */       } 
/* 57 */       exchange.endExchange();
/*    */     } 
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
/*    */   protected boolean isConfidential(HttpServerExchange exchange) {
/* 70 */     return exchange.getRequestScheme().equals("https");
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
/*    */   protected boolean confidentialityRequired(HttpServerExchange exchange) {
/* 84 */     return true;
/*    */   }
/*    */   
/*    */   protected abstract URI getRedirectURI(HttpServerExchange paramHttpServerExchange) throws URISyntaxException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\handlers\AbstractConfidentialityHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */