/*     */ package io.undertow.servlet.handlers;
/*     */ 
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.session.Session;
/*     */ import io.undertow.server.session.SessionManager;
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import io.undertow.servlet.api.SessionPersistenceManager;
/*     */ import io.undertow.servlet.core.Lifecycle;
/*     */ import io.undertow.servlet.spec.HttpSessionImpl;
/*     */ import io.undertow.servlet.spec.ServletContextImpl;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import javax.servlet.http.HttpSessionActivationListener;
/*     */ import javax.servlet.http.HttpSessionEvent;
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
/*     */ public class SessionRestoringHandler
/*     */   implements HttpHandler, Lifecycle
/*     */ {
/*     */   private final String deploymentName;
/*     */   private final Map<String, SessionPersistenceManager.PersistentSession> data;
/*     */   private final SessionManager sessionManager;
/*     */   private final ServletContextImpl servletContext;
/*     */   private final HttpHandler next;
/*     */   private final SessionPersistenceManager sessionPersistenceManager;
/*     */   private volatile boolean started = false;
/*     */   
/*     */   public SessionRestoringHandler(String deploymentName, SessionManager sessionManager, ServletContextImpl servletContext, HttpHandler next, SessionPersistenceManager sessionPersistenceManager) {
/*  60 */     this.deploymentName = deploymentName;
/*  61 */     this.sessionManager = sessionManager;
/*  62 */     this.servletContext = servletContext;
/*  63 */     this.next = next;
/*  64 */     this.sessionPersistenceManager = sessionPersistenceManager;
/*  65 */     this.data = new ConcurrentHashMap<>();
/*     */   }
/*     */   
/*     */   public void start() {
/*  69 */     ClassLoader old = getTccl();
/*     */     try {
/*  71 */       setTccl(this.servletContext.getClassLoader());
/*     */       
/*     */       try {
/*  74 */         Map<String, SessionPersistenceManager.PersistentSession> sessionData = this.sessionPersistenceManager.loadSessionAttributes(this.deploymentName, this.servletContext.getClassLoader());
/*  75 */         if (sessionData != null) {
/*  76 */           this.data.putAll(sessionData);
/*     */         }
/*  78 */       } catch (Exception e) {
/*  79 */         UndertowServletLogger.ROOT_LOGGER.failedtoLoadPersistentSessions(e);
/*     */       } 
/*  81 */       this.started = true;
/*     */     } finally {
/*  83 */       setTccl(old);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void stop() {
/*  88 */     ClassLoader old = getTccl();
/*     */     try {
/*  90 */       setTccl(this.servletContext.getClassLoader());
/*  91 */       this.started = false;
/*  92 */       Map<String, SessionPersistenceManager.PersistentSession> objectData = new HashMap<>();
/*  93 */       for (String sessionId : this.sessionManager.getTransientSessions()) {
/*  94 */         Session session = this.sessionManager.getSession(sessionId);
/*  95 */         if (session != null) {
/*  96 */           HttpSessionEvent event = new HttpSessionEvent((HttpSession)SecurityActions.forSession(session, (ServletContext)this.servletContext, false));
/*  97 */           Map<String, Object> sessionData = new HashMap<>();
/*  98 */           for (String attr : session.getAttributeNames()) {
/*  99 */             Object attribute = session.getAttribute(attr);
/* 100 */             sessionData.put(attr, attribute);
/* 101 */             if (attribute instanceof HttpSessionActivationListener) {
/* 102 */               ((HttpSessionActivationListener)attribute).sessionWillPassivate(event);
/*     */             }
/*     */           } 
/* 105 */           objectData.put(sessionId, new SessionPersistenceManager.PersistentSession(new Date(session.getLastAccessedTime() + (session.getMaxInactiveInterval() * 1000)), sessionData));
/*     */         } 
/*     */       } 
/* 108 */       this.sessionPersistenceManager.persistSessions(this.deploymentName, objectData);
/* 109 */       this.data.clear();
/*     */     } finally {
/* 111 */       setTccl(old);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 117 */     String incomingSessionId = this.servletContext.getSessionConfig().findSessionId(exchange);
/* 118 */     if (incomingSessionId == null || !this.data.containsKey(incomingSessionId)) {
/* 119 */       this.next.handleRequest(exchange);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 124 */     SessionPersistenceManager.PersistentSession result = this.data.remove(incomingSessionId);
/* 125 */     if (result != null) {
/* 126 */       long time = System.currentTimeMillis();
/* 127 */       if (time < result.getExpiration().getTime()) {
/* 128 */         HttpSessionImpl session = this.servletContext.getSession(exchange, true);
/* 129 */         HttpSessionEvent event = new HttpSessionEvent((HttpSession)session);
/* 130 */         for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)result.getSessionData().entrySet()) {
/*     */           
/* 132 */           if (entry.getValue() instanceof HttpSessionActivationListener) {
/* 133 */             ((HttpSessionActivationListener)entry.getValue()).sessionDidActivate(event);
/*     */           }
/* 135 */           if (((String)entry.getKey()).startsWith("io.undertow")) {
/* 136 */             session.getSession().setAttribute(entry.getKey(), entry.getValue()); continue;
/*     */           } 
/* 138 */           session.setAttribute(entry.getKey(), entry.getValue());
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 143 */     this.next.handleRequest(exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStarted() {
/* 148 */     return this.started;
/*     */   }
/*     */   
/*     */   private ClassLoader getTccl() {
/* 152 */     if (System.getSecurityManager() == null) {
/* 153 */       return Thread.currentThread().getContextClassLoader();
/*     */     }
/* 155 */     return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction<ClassLoader>()
/*     */         {
/*     */           public ClassLoader run() {
/* 158 */             return Thread.currentThread().getContextClassLoader();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   private void setTccl(final ClassLoader classLoader) {
/* 165 */     if (System.getSecurityManager() == null) {
/* 166 */       Thread.currentThread().setContextClassLoader(classLoader);
/*     */     } else {
/* 168 */       AccessController.doPrivileged(new PrivilegedAction<Void>()
/*     */           {
/*     */             public Void run() {
/* 171 */               Thread.currentThread().setContextClassLoader(classLoader);
/* 172 */               return null;
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\SessionRestoringHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */