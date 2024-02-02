/*    */ package io.undertow.servlet.handlers;
/*    */ 
/*    */ import io.undertow.server.session.Session;
/*    */ import io.undertow.servlet.spec.HttpSessionImpl;
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
/*    */ class SecurityActions
/*    */ {
/*    */   static HttpSessionImpl forSession(final Session session, final ServletContext servletContext, final boolean newSession) {
/* 30 */     if (System.getSecurityManager() == null) {
/* 31 */       return HttpSessionImpl.forSession(session, servletContext, newSession);
/*    */     }
/* 33 */     return AccessController.<HttpSessionImpl>doPrivileged(new PrivilegedAction<HttpSessionImpl>()
/*    */         {
/*    */           public HttpSessionImpl run() {
/* 36 */             return HttpSessionImpl.forSession(session, servletContext, newSession);
/*    */           }
/*    */         });
/*    */   }
/*    */ 
/*    */   
/*    */   static ServletRequestContext requireCurrentServletRequestContext() {
/* 43 */     if (System.getSecurityManager() == null) {
/* 44 */       return ServletRequestContext.requireCurrent();
/*    */     }
/* 46 */     return AccessController.<ServletRequestContext>doPrivileged(new PrivilegedAction<ServletRequestContext>()
/*    */         {
/*    */           public ServletRequestContext run() {
/* 49 */             return ServletRequestContext.requireCurrent();
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\SecurityActions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */