/*     */ package io.undertow.servlet.core;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.session.Session;
/*     */ import io.undertow.server.session.SessionListener;
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.api.ThreadSetupHandler;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.servlet.spec.HttpSessionImpl;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashSet;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import javax.servlet.http.HttpSessionBindingEvent;
/*     */ import javax.servlet.http.HttpSessionBindingListener;
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
/*     */ public class SessionListenerBridge
/*     */   implements SessionListener
/*     */ {
/*     */   public static final String IO_UNDERTOW = "io.undertow";
/*     */   private final ApplicationListeners applicationListeners;
/*     */   private final ServletContext servletContext;
/*     */   private final ThreadSetupHandler.Action<Void, Session> destroyedAction;
/*     */   
/*     */   public SessionListenerBridge(Deployment deployment, ApplicationListeners applicationListeners, ServletContext servletContext) {
/*  49 */     this.applicationListeners = applicationListeners;
/*  50 */     this.servletContext = servletContext;
/*  51 */     this.destroyedAction = deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, Session>()
/*     */         {
/*     */           public Void call(HttpServerExchange exchange, Session session) throws ServletException {
/*  54 */             SessionListenerBridge.this.doDestroy(session);
/*  55 */             return null;
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sessionCreated(Session session, HttpServerExchange exchange) {
/*  63 */     HttpSessionImpl httpSession = SecurityActions.forSession(session, this.servletContext, true);
/*  64 */     this.applicationListeners.sessionCreated((HttpSession)httpSession);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sessionDestroyed(Session session, HttpServerExchange exchange, SessionListener.SessionDestroyedReason reason) {
/*  70 */     if (reason == SessionListener.SessionDestroyedReason.TIMEOUT) {
/*     */       
/*     */       try {
/*  73 */         this.destroyedAction.call(exchange, session);
/*  74 */       } catch (Exception e) {
/*  75 */         throw new RuntimeException(e);
/*     */       } 
/*     */     } else {
/*  78 */       doDestroy(session);
/*     */     } 
/*     */     
/*  81 */     ServletRequestContext current = SecurityActions.currentServletRequestContext();
/*  82 */     Session underlying = null;
/*  83 */     if (current != null && current.getSession() != null) {
/*  84 */       if (System.getSecurityManager() == null) {
/*  85 */         underlying = current.getSession().getSession();
/*     */       } else {
/*  87 */         underlying = AccessController.<Session>doPrivileged((PrivilegedAction<Session>)new HttpSessionImpl.UnwrapSessionAction((HttpSession)current.getSession()));
/*     */       } 
/*     */     }
/*     */     
/*  91 */     if (current != null && underlying == session) {
/*  92 */       current.setSession(null);
/*     */     }
/*     */   }
/*     */   
/*     */   private void doDestroy(Session session) {
/*  97 */     HttpSessionImpl httpSession = SecurityActions.forSession(session, this.servletContext, false);
/*  98 */     this.applicationListeners.sessionDestroyed((HttpSession)httpSession);
/*     */ 
/*     */     
/* 101 */     HashSet<String> names = new HashSet<>(session.getAttributeNames());
/* 102 */     for (String attribute : names) {
/* 103 */       session.removeAttribute(attribute);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void attributeAdded(Session session, String name, Object value) {
/* 109 */     if (name.startsWith("io.undertow")) {
/*     */       return;
/*     */     }
/* 112 */     HttpSessionImpl httpSession = SecurityActions.forSession(session, this.servletContext, false);
/* 113 */     this.applicationListeners.httpSessionAttributeAdded((HttpSession)httpSession, name, value);
/* 114 */     if (value instanceof HttpSessionBindingListener) {
/* 115 */       ((HttpSessionBindingListener)value).valueBound(new HttpSessionBindingEvent((HttpSession)httpSession, name, value));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void attributeUpdated(Session session, String name, Object value, Object old) {
/* 121 */     if (name.startsWith("io.undertow")) {
/*     */       return;
/*     */     }
/* 124 */     HttpSessionImpl httpSession = SecurityActions.forSession(session, this.servletContext, false);
/* 125 */     if (old != value) {
/* 126 */       if (old instanceof HttpSessionBindingListener) {
/* 127 */         ((HttpSessionBindingListener)old).valueUnbound(new HttpSessionBindingEvent((HttpSession)httpSession, name, old));
/*     */       }
/* 129 */       this.applicationListeners.httpSessionAttributeReplaced((HttpSession)httpSession, name, old);
/*     */     } 
/* 131 */     if (value instanceof HttpSessionBindingListener) {
/* 132 */       ((HttpSessionBindingListener)value).valueBound(new HttpSessionBindingEvent((HttpSession)httpSession, name, value));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void attributeRemoved(Session session, String name, Object old) {
/* 138 */     if (name.startsWith("io.undertow")) {
/*     */       return;
/*     */     }
/* 141 */     HttpSessionImpl httpSession = SecurityActions.forSession(session, this.servletContext, false);
/* 142 */     if (old != null) {
/* 143 */       this.applicationListeners.httpSessionAttributeRemoved((HttpSession)httpSession, name, old);
/* 144 */       if (old instanceof HttpSessionBindingListener)
/* 145 */         ((HttpSessionBindingListener)old).valueUnbound(new HttpSessionBindingEvent((HttpSession)httpSession, name, old)); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void sessionIdChanged(Session session, String oldSessionId) {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\SessionListenerBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */