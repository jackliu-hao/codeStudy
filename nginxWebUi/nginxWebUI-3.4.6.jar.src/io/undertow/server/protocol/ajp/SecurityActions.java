/*    */ package io.undertow.server.protocol.ajp;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class SecurityActions
/*    */ {
/*    */   static String getSystemProperty(final String key) {
/* 37 */     return (System.getSecurityManager() == null) ? System.getProperty(key) : AccessController.<String>doPrivileged(new PrivilegedAction<String>()
/*    */         {
/*    */           public String run() {
/* 40 */             return System.getProperty(key);
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\ajp\SecurityActions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */