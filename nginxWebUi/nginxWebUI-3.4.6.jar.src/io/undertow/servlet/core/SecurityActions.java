/*     */ package io.undertow.servlet.core;
/*     */ 
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.session.Session;
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.handlers.ServletInitialHandler;
/*     */ import io.undertow.servlet.handlers.ServletPathMatches;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.servlet.spec.HttpSessionImpl;
/*     */ import io.undertow.servlet.spec.ServletContextImpl;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import javax.servlet.ServletContext;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ final class SecurityActions
/*     */ {
/*     */   static ClassLoader getContextClassLoader() {
/*  48 */     if (System.getSecurityManager() == null) {
/*  49 */       return Thread.currentThread().getContextClassLoader();
/*     */     }
/*  51 */     return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction<ClassLoader>() {
/*     */           public ClassLoader run() {
/*  53 */             return Thread.currentThread().getContextClassLoader();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void setContextClassLoader(final ClassLoader classLoader) {
/*  66 */     if (System.getSecurityManager() == null) {
/*  67 */       Thread.currentThread().setContextClassLoader(classLoader);
/*     */     } else {
/*  69 */       AccessController.doPrivileged(new PrivilegedAction() {
/*     */             public Object run() {
/*  71 */               Thread.currentThread().setContextClassLoader(classLoader);
/*  72 */               return null;
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   static String getSystemProperty(final String prop) {
/*  79 */     if (System.getSecurityManager() == null) {
/*  80 */       return System.getProperty(prop);
/*     */     }
/*  82 */     return AccessController.<String>doPrivileged(new PrivilegedAction() {
/*     */           public Object run() {
/*  84 */             return System.getProperty(prop);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   static HttpSessionImpl forSession(final Session session, final ServletContext servletContext, final boolean newSession) {
/*  91 */     if (System.getSecurityManager() == null) {
/*  92 */       return HttpSessionImpl.forSession(session, servletContext, newSession);
/*     */     }
/*  94 */     return AccessController.<HttpSessionImpl>doPrivileged(new PrivilegedAction<HttpSessionImpl>()
/*     */         {
/*     */           public HttpSessionImpl run() {
/*  97 */             return HttpSessionImpl.forSession(session, servletContext, newSession);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   static ServletRequestContext currentServletRequestContext() {
/* 104 */     if (System.getSecurityManager() == null) {
/* 105 */       return ServletRequestContext.current();
/*     */     }
/* 107 */     return AccessController.<ServletRequestContext>doPrivileged(new PrivilegedAction<ServletRequestContext>()
/*     */         {
/*     */           public ServletRequestContext run() {
/* 110 */             return ServletRequestContext.current();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   static void setCurrentRequestContext(final ServletRequestContext servletRequestContext) {
/* 117 */     if (System.getSecurityManager() == null) {
/* 118 */       ServletRequestContext.setCurrentRequestContext(servletRequestContext);
/*     */     } else {
/* 120 */       AccessController.doPrivileged(new PrivilegedAction()
/*     */           {
/*     */             public Object run() {
/* 123 */               ServletRequestContext.setCurrentRequestContext(servletRequestContext);
/* 124 */               return null;
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   static void clearCurrentServletAttachments() {
/* 131 */     if (System.getSecurityManager() == null) {
/* 132 */       ServletRequestContext.clearCurrentServletAttachments();
/*     */     } else {
/* 134 */       AccessController.doPrivileged(new PrivilegedAction()
/*     */           {
/*     */             public Object run() {
/* 137 */               ServletRequestContext.clearCurrentServletAttachments();
/* 138 */               return null;
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */   static ServletRequestContext requireCurrentServletRequestContext() {
/* 144 */     if (System.getSecurityManager() == null) {
/* 145 */       return ServletRequestContext.requireCurrent();
/*     */     }
/* 147 */     return AccessController.<ServletRequestContext>doPrivileged(new PrivilegedAction<ServletRequestContext>()
/*     */         {
/*     */           public ServletRequestContext run() {
/* 150 */             return ServletRequestContext.requireCurrent();
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   static ServletInitialHandler createServletInitialHandler(final ServletPathMatches paths, final HttpHandler next, final Deployment deployment, final ServletContextImpl servletContext) {
/* 156 */     if (System.getSecurityManager() == null) {
/* 157 */       return new ServletInitialHandler(paths, next, deployment, servletContext);
/*     */     }
/* 159 */     return AccessController.<ServletInitialHandler>doPrivileged(new PrivilegedAction<ServletInitialHandler>()
/*     */         {
/*     */           public ServletInitialHandler run() {
/* 162 */             return new ServletInitialHandler(paths, next, deployment, servletContext);
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\SecurityActions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */