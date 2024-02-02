/*    */ package io.undertow.server;
/*    */ 
/*    */ import io.undertow.server.handlers.Cookie;
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum SecureCookieCommitListener
/*    */   implements ResponseCommitListener
/*    */ {
/* 10 */   INSTANCE;
/*    */ 
/*    */   
/*    */   public void beforeCommit(HttpServerExchange exchange) {
/* 14 */     for (Cookie cookie : exchange.responseCookies())
/* 15 */       cookie.setSecure(true); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\SecureCookieCommitListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */