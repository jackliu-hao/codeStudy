/*    */ package io.undertow.util;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.session.Session;
/*    */ import io.undertow.server.session.SessionConfig;
/*    */ import io.undertow.server.session.SessionManager;
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
/*    */ public class Sessions
/*    */ {
/*    */   public static Session getSession(HttpServerExchange exchange) {
/* 40 */     return getSession(exchange, false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Session getOrCreateSession(HttpServerExchange exchange) {
/* 49 */     return getSession(exchange, true);
/*    */   }
/*    */   
/*    */   private static Session getSession(HttpServerExchange exchange, boolean create) {
/* 53 */     SessionManager sessionManager = (SessionManager)exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
/* 54 */     SessionConfig sessionConfig = (SessionConfig)exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
/* 55 */     if (sessionManager == null) {
/* 56 */       throw UndertowMessages.MESSAGES.sessionManagerNotFound();
/*    */     }
/* 58 */     Session session = sessionManager.getSession(exchange, sessionConfig);
/* 59 */     if (session == null && create) {
/* 60 */       session = sessionManager.createSession(exchange, sessionConfig);
/*    */     }
/* 62 */     return session;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\Sessions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */