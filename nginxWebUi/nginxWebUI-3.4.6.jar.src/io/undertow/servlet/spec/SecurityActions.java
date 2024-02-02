/*    */ package io.undertow.servlet.spec;
/*    */ 
/*    */ import io.undertow.server.session.Session;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ import javax.servlet.ServletContext;
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
/*    */ class SecurityActions
/*    */ {
/*    */   static ServletRequestContext currentServletRequestContext() {
/* 31 */     if (System.getSecurityManager() == null) {
/* 32 */       return ServletRequestContext.current();
/*    */     }
/* 34 */     return AccessController.<ServletRequestContext>doPrivileged(new PrivilegedAction<ServletRequestContext>()
/*    */         {
/*    */           public ServletRequestContext run() {
/* 37 */             return ServletRequestContext.current();
/*    */           }
/*    */         });
/*    */   }
/*    */ 
/*    */   
/*    */   static HttpSessionImpl forSession(final Session session, final ServletContext servletContext, final boolean newSession) {
/* 44 */     if (System.getSecurityManager() == null) {
/* 45 */       return HttpSessionImpl.forSession(session, servletContext, newSession);
/*    */     }
/* 47 */     return AccessController.<HttpSessionImpl>doPrivileged(new PrivilegedAction<HttpSessionImpl>()
/*    */         {
/*    */           public HttpSessionImpl run() {
/* 50 */             return HttpSessionImpl.forSession(session, servletContext, newSession);
/*    */           }
/*    */         });
/*    */   }
/*    */ 
/*    */   
/*    */   static void setCurrentRequestContext(final ServletRequestContext servletRequestContext) {
/* 57 */     if (System.getSecurityManager() == null) {
/* 58 */       ServletRequestContext.setCurrentRequestContext(servletRequestContext);
/*    */     } else {
/* 60 */       AccessController.doPrivileged(new PrivilegedAction()
/*    */           {
/*    */             public Object run() {
/* 63 */               ServletRequestContext.setCurrentRequestContext(servletRequestContext);
/* 64 */               return null;
/*    */             }
/*    */           });
/*    */     } 
/*    */   }
/*    */   
/*    */   static void clearCurrentServletAttachments() {
/* 71 */     if (System.getSecurityManager() == null) {
/* 72 */       ServletRequestContext.clearCurrentServletAttachments();
/*    */     } else {
/* 74 */       AccessController.doPrivileged(new PrivilegedAction()
/*    */           {
/*    */             public Object run() {
/* 77 */               ServletRequestContext.clearCurrentServletAttachments();
/* 78 */               return null;
/*    */             }
/*    */           });
/*    */     } 
/*    */   }
/*    */   
/*    */   static ServletRequestContext requireCurrentServletRequestContext() {
/* 85 */     if (System.getSecurityManager() == null) {
/* 86 */       return ServletRequestContext.requireCurrent();
/*    */     }
/* 88 */     return AccessController.<ServletRequestContext>doPrivileged(new PrivilegedAction<ServletRequestContext>()
/*    */         {
/*    */           public ServletRequestContext run() {
/* 91 */             return ServletRequestContext.requireCurrent();
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\SecurityActions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */