/*    */ package io.undertow.servlet.api;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class SecurityActions
/*    */ {
/*    */   static String getSystemProperty(final String prop) {
/* 32 */     if (System.getSecurityManager() == null) {
/* 33 */       return System.getProperty(prop);
/*    */     }
/* 35 */     return AccessController.<String>doPrivileged(new PrivilegedAction() {
/*    */           public Object run() {
/* 37 */             return System.getProperty(prop);
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\SecurityActions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */