/*    */ package org.jboss.logging;
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
/*    */ class SecurityActions
/*    */ {
/*    */   static String getSystemProperty(final String key) {
/* 31 */     if (System.getSecurityManager() == null) {
/* 32 */       return System.getProperty(key);
/*    */     }
/* 34 */     return AccessController.<String>doPrivileged(new PrivilegedAction<String>()
/*    */         {
/*    */           public String run() {
/* 37 */             return System.getProperty(key);
/*    */           }
/*    */         });
/*    */   }
/*    */   
/*    */   static String getSystemProperty(final String key, final String dft) {
/* 43 */     if (System.getSecurityManager() == null) {
/* 44 */       return System.getProperty(key, dft);
/*    */     }
/* 46 */     return AccessController.<String>doPrivileged(new PrivilegedAction<String>()
/*    */         {
/*    */           public String run() {
/* 49 */             return System.getProperty(key, dft);
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\SecurityActions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */