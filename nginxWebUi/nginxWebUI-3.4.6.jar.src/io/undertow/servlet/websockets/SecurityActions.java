/*    */ package io.undertow.servlet.websockets;
/*    */ 
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
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
/*    */   static ServletRequestContext requireCurrentServletRequestContext() {
/* 27 */     if (System.getSecurityManager() == null) {
/* 28 */       return ServletRequestContext.requireCurrent();
/*    */     }
/* 30 */     return AccessController.<ServletRequestContext>doPrivileged(new PrivilegedAction<ServletRequestContext>()
/*    */         {
/*    */           public ServletRequestContext run() {
/* 33 */             return ServletRequestContext.requireCurrent();
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\websockets\SecurityActions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */