/*    */ package org.wildfly.common.os;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ import java.util.Locale;
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
/*    */ public final class ProcessRedirect
/*    */ {
/*    */   public static ProcessBuilder.Redirect discard() {
/* 38 */     return ProcessBuilder.Redirect.to(new File(isWindows() ? "NUL" : "/dev/null"));
/*    */   }
/*    */   
/*    */   private static boolean isWindows() {
/* 42 */     SecurityManager sm = System.getSecurityManager();
/* 43 */     return ((sm == null) ? getOsName() : getOsNamePrivileged()).toLowerCase(Locale.ROOT).contains("windows");
/*    */   }
/*    */   
/*    */   private static String getOsNamePrivileged() {
/* 47 */     return AccessController.<String>doPrivileged(new PrivilegedAction<String>() {
/*    */           public String run() {
/* 49 */             return ProcessRedirect.getOsName();
/*    */           }
/*    */         });
/*    */   }
/*    */   
/*    */   private static String getOsName() {
/* 55 */     return System.getProperty("os.name", "unknown");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\os\ProcessRedirect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */