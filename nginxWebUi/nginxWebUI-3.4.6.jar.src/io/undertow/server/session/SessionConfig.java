/*    */ package io.undertow.server.session;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.AttachmentKey;
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
/*    */ 
/*    */ 
/*    */ public interface SessionConfig
/*    */ {
/* 36 */   public static final AttachmentKey<SessionConfig> ATTACHMENT_KEY = AttachmentKey.create(SessionConfig.class);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void setSessionId(HttpServerExchange paramHttpServerExchange, String paramString);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void clearSession(HttpServerExchange paramHttpServerExchange, String paramString);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   String findSessionId(HttpServerExchange paramHttpServerExchange);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   SessionCookieSource sessionCookieSource(HttpServerExchange paramHttpServerExchange);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   String rewriteUrl(String paramString1, String paramString2);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public enum SessionCookieSource
/*    */   {
/* 75 */     URL,
/* 76 */     COOKIE,
/* 77 */     SSL,
/* 78 */     OTHER,
/* 79 */     NONE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\session\SessionConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */