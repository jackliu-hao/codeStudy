/*    */ package io.undertow.security.handlers;
/*    */ 
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.NetworkUtils;
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
/*    */ public class SinglePortConfidentialityHandler
/*    */   extends AbstractConfidentialityHandler
/*    */ {
/*    */   private final int redirectPort;
/*    */   
/*    */   public SinglePortConfidentialityHandler(HttpHandler next, int redirectPort) {
/* 38 */     super(next);
/* 39 */     this.redirectPort = (redirectPort == 443) ? -1 : redirectPort;
/*    */   }
/*    */ 
/*    */   
/*    */   protected URI getRedirectURI(HttpServerExchange exchange) throws URISyntaxException {
/* 44 */     return getRedirectURI(exchange, this.redirectPort);
/*    */   }
/*    */   
/*    */   protected URI getRedirectURI(HttpServerExchange exchange, int port) throws URISyntaxException {
/* 48 */     StringBuilder uriBuilder = new StringBuilder();
/* 49 */     uriBuilder.append("https://");
/* 50 */     uriBuilder.append(NetworkUtils.formatPossibleIpv6Address(exchange.getHostName()));
/* 51 */     if (port > 0) {
/* 52 */       uriBuilder.append(":").append(port);
/*    */     }
/* 54 */     String uri = exchange.getRequestURI();
/* 55 */     if (exchange.isHostIncludedInRequestURI()) {
/* 56 */       int slashCount = 0;
/* 57 */       for (int i = 0; i < uri.length(); i++) {
/*    */         
/* 59 */         slashCount++;
/* 60 */         if (uri.charAt(i) == '/' && slashCount == 3) {
/* 61 */           uri = uri.substring(i);
/*    */           
/*    */           break;
/*    */         } 
/*    */       } 
/*    */     } 
/* 67 */     uriBuilder.append(uri);
/* 68 */     String queryString = exchange.getQueryString();
/* 69 */     if (queryString != null && !queryString.isEmpty()) {
/* 70 */       uriBuilder.append("?").append(queryString);
/*    */     }
/* 72 */     return new URI(uriBuilder.toString());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\handlers\SinglePortConfidentialityHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */