/*    */ package io.undertow.server.handlers.resource;
/*    */ 
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
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
/*    */   static Boolean isSymbolicLink(final Path file) {
/* 28 */     if (System.getSecurityManager() == null) {
/* 29 */       return Boolean.valueOf(Files.isSymbolicLink(file));
/*    */     }
/* 31 */     return AccessController.<Boolean>doPrivileged(new PrivilegedAction<Boolean>()
/*    */         {
/*    */           public Boolean run() {
/* 34 */             return Boolean.valueOf(Files.isSymbolicLink(file));
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\SecurityActions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */