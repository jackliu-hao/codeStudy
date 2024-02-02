/*     */ package io.undertow.security.handlers;
/*     */ 
/*     */ import io.undertow.security.api.AuthenticatedSessionManager;
/*     */ import io.undertow.security.api.NotificationReceiver;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.api.SecurityNotification;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.session.Session;
/*     */ import io.undertow.server.session.SessionConfig;
/*     */ import io.undertow.server.session.SessionManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CachedAuthenticatedSessionHandler
/*     */   implements HttpHandler
/*     */ {
/*  43 */   public static final String ATTRIBUTE_NAME = CachedAuthenticatedSessionHandler.class.getName() + ".AuthenticatedSession";
/*     */   
/*  45 */   public static final String NO_ID_CHANGE_REQUIRED = CachedAuthenticatedSessionHandler.class.getName() + ".NoIdChangeRequired";
/*     */   
/*  47 */   private final NotificationReceiver NOTIFICATION_RECEIVER = new SecurityNotificationReceiver();
/*  48 */   private final AuthenticatedSessionManager SESSION_MANAGER = new ServletAuthenticatedSessionManager();
/*     */   
/*     */   private final HttpHandler next;
/*     */   
/*     */   public CachedAuthenticatedSessionHandler(HttpHandler next) {
/*  53 */     this.next = next;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  59 */     SecurityContext securityContext = exchange.getSecurityContext();
/*  60 */     securityContext.registerNotificationReceiver(this.NOTIFICATION_RECEIVER);
/*  61 */     SessionManager sessionManager = (SessionManager)exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
/*  62 */     SessionConfig sessionConfig = (SessionConfig)exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
/*  63 */     if (sessionManager == null || sessionConfig == null) {
/*  64 */       this.next.handleRequest(exchange);
/*     */       return;
/*     */     } 
/*  67 */     Session session = sessionManager.getSession(exchange, sessionConfig);
/*     */ 
/*     */     
/*  70 */     if (session != null) {
/*  71 */       exchange.putAttachment(AuthenticatedSessionManager.ATTACHMENT_KEY, this.SESSION_MANAGER);
/*     */     }
/*     */     
/*  74 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   private class SecurityNotificationReceiver implements NotificationReceiver {
/*     */     private SecurityNotificationReceiver() {}
/*     */     
/*     */     public void handleNotification(SecurityNotification notification) {
/*  81 */       SecurityNotification.EventType eventType = notification.getEventType();
/*  82 */       HttpServerExchange exchange = notification.getExchange();
/*  83 */       SessionManager sessionManager = (SessionManager)exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
/*  84 */       SessionConfig sessionConfig = (SessionConfig)exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
/*  85 */       if (sessionManager == null || sessionConfig == null) {
/*     */         return;
/*     */       }
/*  88 */       Session httpSession = sessionManager.getSession(exchange, sessionConfig);
/*  89 */       switch (eventType) {
/*     */         case AUTHENTICATED:
/*  91 */           if (CachedAuthenticatedSessionHandler.this.isCacheable(notification)) {
/*  92 */             if (httpSession == null) {
/*  93 */               httpSession = sessionManager.createSession(exchange, sessionConfig);
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*  99 */             httpSession.setAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME, new AuthenticatedSessionManager.AuthenticatedSession(notification
/* 100 */                   .getAccount(), notification.getMechanism()));
/*     */           } 
/*     */           break;
/*     */         case LOGGED_OUT:
/* 104 */           if (httpSession != null) {
/* 105 */             httpSession.removeAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
/* 106 */             httpSession.removeAttribute(CachedAuthenticatedSessionHandler.NO_ID_CHANGE_REQUIRED);
/*     */           } 
/*     */           break;
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ServletAuthenticatedSessionManager
/*     */     implements AuthenticatedSessionManager
/*     */   {
/*     */     private ServletAuthenticatedSessionManager() {}
/*     */     
/*     */     public AuthenticatedSessionManager.AuthenticatedSession lookupSession(HttpServerExchange exchange) {
/* 119 */       SessionManager sessionManager = (SessionManager)exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
/* 120 */       SessionConfig sessionConfig = (SessionConfig)exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
/* 121 */       if (sessionManager == null || sessionConfig == null) {
/* 122 */         return null;
/*     */       }
/* 124 */       Session httpSession = sessionManager.getSession(exchange, sessionConfig);
/* 125 */       if (httpSession != null) {
/* 126 */         return (AuthenticatedSessionManager.AuthenticatedSession)httpSession.getAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
/*     */       }
/* 128 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void clearSession(HttpServerExchange exchange) {
/* 133 */       SessionManager sessionManager = (SessionManager)exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
/* 134 */       SessionConfig sessionConfig = (SessionConfig)exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
/* 135 */       if (sessionManager == null || sessionConfig == null) {
/*     */         return;
/*     */       }
/* 138 */       Session httpSession = sessionManager.getSession(exchange, sessionConfig);
/* 139 */       if (httpSession != null) {
/* 140 */         httpSession.removeAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isCacheable(SecurityNotification notification) {
/* 147 */     return (notification.isProgramatic() || notification.isCachingRequired());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\handlers\CachedAuthenticatedSessionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */