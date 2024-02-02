/*     */ package io.undertow.servlet.handlers.security;
/*     */ 
/*     */ import io.undertow.security.api.AuthenticatedSessionManager;
/*     */ import io.undertow.security.api.NotificationReceiver;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.api.SecurityNotification;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.session.Session;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.servlet.spec.HttpSessionImpl;
/*     */ import io.undertow.servlet.spec.ServletContextImpl;
/*     */ import io.undertow.servlet.util.SavedRequest;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import javax.servlet.http.HttpSession;
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
/*  48 */   public static final String ATTRIBUTE_NAME = CachedAuthenticatedSessionHandler.class.getName() + ".AuthenticatedSession";
/*     */   
/*  50 */   public static final String NO_ID_CHANGE_REQUIRED = CachedAuthenticatedSessionHandler.class.getName() + ".NoIdChangeRequired";
/*     */   
/*  52 */   private final NotificationReceiver NOTIFICATION_RECEIVER = new SecurityNotificationReceiver();
/*  53 */   private final AuthenticatedSessionManager SESSION_MANAGER = new ServletAuthenticatedSessionManager();
/*     */   
/*     */   private final HttpHandler next;
/*     */   private final ServletContextImpl servletContext;
/*     */   
/*     */   public CachedAuthenticatedSessionHandler(HttpHandler next, ServletContextImpl servletContext) {
/*  59 */     this.next = next;
/*  60 */     this.servletContext = servletContext;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  66 */     SecurityContext securityContext = exchange.getSecurityContext();
/*  67 */     securityContext.registerNotificationReceiver(this.NOTIFICATION_RECEIVER);
/*     */     
/*  69 */     HttpSessionImpl httpSessionImpl = this.servletContext.getSession(exchange, false);
/*     */ 
/*     */     
/*  72 */     if (httpSessionImpl != null) {
/*  73 */       exchange.putAttachment(AuthenticatedSessionManager.ATTACHMENT_KEY, this.SESSION_MANAGER);
/*  74 */       SavedRequest.tryRestoreRequest(exchange, (HttpSession)httpSessionImpl);
/*     */     } 
/*     */     
/*  77 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   private class SecurityNotificationReceiver implements NotificationReceiver {
/*     */     private SecurityNotificationReceiver() {}
/*     */     
/*     */     public void handleNotification(SecurityNotification notification) {
/*  84 */       SecurityNotification.EventType eventType = notification.getEventType();
/*  85 */       HttpSessionImpl httpSession = CachedAuthenticatedSessionHandler.this.servletContext.getSession(notification.getExchange(), false);
/*  86 */       switch (eventType) {
/*     */         case AUTHENTICATED:
/*  88 */           if (CachedAuthenticatedSessionHandler.this.isCacheable(notification)) {
/*  89 */             if (CachedAuthenticatedSessionHandler.this.servletContext.getDeployment().getDeploymentInfo().isChangeSessionIdOnLogin() && 
/*  90 */               httpSession != null) {
/*  91 */               Session session1 = CachedAuthenticatedSessionHandler.this.underlyingSession(httpSession);
/*  92 */               if (!httpSession.isNew() && 
/*  93 */                 !httpSession.isInvalid() && session1
/*  94 */                 .getAttribute(CachedAuthenticatedSessionHandler.NO_ID_CHANGE_REQUIRED) == null) {
/*  95 */                 ServletRequestContext src = (ServletRequestContext)notification.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*  96 */                 src.getOriginalRequest().changeSessionId();
/*     */               } 
/*  98 */               if (session1.getAttribute(CachedAuthenticatedSessionHandler.NO_ID_CHANGE_REQUIRED) == null) {
/*  99 */                 session1.setAttribute(CachedAuthenticatedSessionHandler.NO_ID_CHANGE_REQUIRED, Boolean.valueOf(true));
/*     */               }
/*     */             } 
/*     */             
/* 103 */             if (httpSession == null) {
/* 104 */               httpSession = CachedAuthenticatedSessionHandler.this.servletContext.getSession(notification.getExchange(), true);
/*     */             }
/* 106 */             Session session = CachedAuthenticatedSessionHandler.this.underlyingSession(httpSession);
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 111 */             session.setAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME, new AuthenticatedSessionManager.AuthenticatedSession(notification
/* 112 */                   .getAccount(), notification.getMechanism()));
/*     */           } 
/*     */           break;
/*     */         case LOGGED_OUT:
/* 116 */           if (httpSession != null) {
/* 117 */             Session session = CachedAuthenticatedSessionHandler.this.underlyingSession(httpSession);
/* 118 */             session.removeAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
/* 119 */             session.removeAttribute(CachedAuthenticatedSessionHandler.NO_ID_CHANGE_REQUIRED);
/*     */           } 
/*     */           break;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected Session underlyingSession(HttpSessionImpl httpSession) {
/*     */     Session session;
/* 129 */     if (System.getSecurityManager() == null) {
/* 130 */       session = httpSession.getSession();
/*     */     } else {
/* 132 */       session = AccessController.<Session>doPrivileged((PrivilegedAction<Session>)new HttpSessionImpl.UnwrapSessionAction((HttpSession)httpSession));
/*     */     } 
/* 134 */     return session;
/*     */   }
/*     */   
/*     */   private class ServletAuthenticatedSessionManager implements AuthenticatedSessionManager {
/*     */     private ServletAuthenticatedSessionManager() {}
/*     */     
/*     */     public AuthenticatedSessionManager.AuthenticatedSession lookupSession(HttpServerExchange exchange) {
/* 141 */       HttpSessionImpl httpSession = CachedAuthenticatedSessionHandler.this.servletContext.getSession(exchange, false);
/* 142 */       if (httpSession != null) {
/* 143 */         Session session = CachedAuthenticatedSessionHandler.this.underlyingSession(httpSession);
/* 144 */         return (AuthenticatedSessionManager.AuthenticatedSession)session.getAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
/*     */       } 
/* 146 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void clearSession(HttpServerExchange exchange) {
/* 151 */       HttpSessionImpl httpSession = CachedAuthenticatedSessionHandler.this.servletContext.getSession(exchange, false);
/* 152 */       if (httpSession != null) {
/* 153 */         Session session = CachedAuthenticatedSessionHandler.this.underlyingSession(httpSession);
/* 154 */         session.removeAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isCacheable(SecurityNotification notification) {
/* 161 */     return (notification.isProgramatic() || notification.isCachingRequired());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\security\CachedAuthenticatedSessionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */