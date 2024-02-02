/*    */ package io.undertow.servlet.handlers.security;
/*    */ 
/*    */ import io.undertow.security.impl.SingleSignOnAuthenticationMechanism;
/*    */ import io.undertow.security.impl.SingleSignOnManager;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.session.Session;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
/*    */ import io.undertow.servlet.spec.HttpSessionImpl;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ import javax.servlet.http.HttpSession;
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
/*    */ public class ServletSingleSignOnAuthenticationMechanism
/*    */   extends SingleSignOnAuthenticationMechanism
/*    */ {
/*    */   public ServletSingleSignOnAuthenticationMechanism(SingleSignOnManager storage) {
/* 37 */     super(storage);
/*    */   }
/*    */ 
/*    */   
/*    */   protected Session getSession(HttpServerExchange exchange) {
/* 42 */     ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 43 */     HttpSessionImpl session = servletRequestContext.getCurrentServletContext().getSession(exchange, true);
/* 44 */     if (System.getSecurityManager() == null) {
/* 45 */       return session.getSession();
/*    */     }
/* 47 */     return AccessController.<Session>doPrivileged((PrivilegedAction<Session>)new HttpSessionImpl.UnwrapSessionAction((HttpSession)session));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\security\ServletSingleSignOnAuthenticationMechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */