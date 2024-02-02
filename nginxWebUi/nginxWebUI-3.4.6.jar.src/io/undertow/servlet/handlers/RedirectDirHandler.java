/*    */ package io.undertow.servlet.handlers;
/*    */ 
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.Headers;
/*    */ import io.undertow.util.Methods;
/*    */ import io.undertow.util.RedirectBuilder;
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
/*    */ public class RedirectDirHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private static final String HTTP2_UPGRADE_PREFIX = "h2";
/*    */   private final HttpHandler next;
/*    */   private final ServletPathMatches paths;
/*    */   
/*    */   public RedirectDirHandler(HttpHandler next, ServletPathMatches paths) {
/* 41 */     this.next = next;
/* 42 */     this.paths = paths;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 47 */     String path = exchange.getRelativePath();
/* 48 */     ServletPathMatch info = this.paths.getServletHandlerByPath(path);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 54 */     String upgradeString = exchange.getRequestHeaders().getFirst(Headers.UPGRADE);
/* 55 */     boolean isUpgradeRequest = (upgradeString != null && !upgradeString.startsWith("h2"));
/* 56 */     if (info.getType() == ServletPathMatch.Type.REDIRECT && !isUpgradeRequest) {
/*    */ 
/*    */       
/* 59 */       if (exchange.getRequestMethod().equals(Methods.GET) || exchange.getRequestMethod().equals(Methods.HEAD)) {
/* 60 */         exchange.setStatusCode(302);
/*    */       } else {
/* 62 */         exchange.setStatusCode(307);
/*    */       } 
/* 64 */       exchange.getResponseHeaders().put(Headers.LOCATION, 
/* 65 */           RedirectBuilder.redirect(exchange, exchange.getRelativePath() + "/", true));
/*    */       return;
/*    */     } 
/* 68 */     this.next.handleRequest(exchange);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\RedirectDirHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */