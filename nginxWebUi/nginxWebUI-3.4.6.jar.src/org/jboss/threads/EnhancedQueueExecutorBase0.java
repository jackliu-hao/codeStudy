/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ import java.util.concurrent.AbstractExecutorService;
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
/*    */ abstract class EnhancedQueueExecutorBase0
/*    */   extends AbstractExecutorService
/*    */ {
/*    */   int p00;
/*    */   int p01;
/*    */   int p02;
/*    */   int p03;
/*    */   int p04;
/*    */   int p05;
/*    */   int p06;
/*    */   int p07;
/*    */   int p08;
/*    */   int p09;
/*    */   int p0A;
/*    */   int p0B;
/*    */   int p0C;
/*    */   int p0D;
/*    */   int p0E;
/*    */   int p0F;
/*    */   
/*    */   static int readIntPropertyPrefixed(String name, int defVal) {
/*    */     try {
/* 43 */       return Integer.parseInt(readPropertyPrefixed(name, Integer.toString(defVal)));
/* 44 */     } catch (NumberFormatException ignored) {
/* 45 */       return defVal;
/*    */     } 
/*    */   }
/*    */   
/*    */   static boolean readBooleanPropertyPrefixed(String name, boolean defVal) {
/* 50 */     return Boolean.parseBoolean(readPropertyPrefixed(name, Boolean.toString(defVal)));
/*    */   }
/*    */   
/*    */   static String readPropertyPrefixed(String name, String defVal) {
/* 54 */     return readProperty("jboss.threads.eqe." + name, defVal);
/*    */   }
/*    */   
/*    */   static String readProperty(final String name, final String defVal) {
/* 58 */     SecurityManager sm = System.getSecurityManager();
/* 59 */     if (sm != null) {
/* 60 */       return AccessController.<String>doPrivileged(new PrivilegedAction<String>() {
/*    */             public String run() {
/* 62 */               return EnhancedQueueExecutorBase0.readPropertyRaw(name, defVal);
/*    */             }
/*    */           });
/*    */     }
/* 66 */     return readPropertyRaw(name, defVal);
/*    */   }
/*    */ 
/*    */   
/*    */   static String readPropertyRaw(String name, String defVal) {
/* 71 */     return System.getProperty(name, defVal);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\EnhancedQueueExecutorBase0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */