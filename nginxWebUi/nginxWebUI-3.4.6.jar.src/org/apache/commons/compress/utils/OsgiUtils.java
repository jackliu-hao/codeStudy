/*    */ package org.apache.commons.compress.utils;
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
/*    */ public class OsgiUtils
/*    */ {
/*    */   private static final boolean inOsgiEnvironment;
/*    */   
/*    */   static {
/* 31 */     Class<?> classloaderClass = OsgiUtils.class.getClassLoader().getClass();
/* 32 */     inOsgiEnvironment = isBundleReference(classloaderClass);
/*    */   }
/*    */   
/*    */   private static boolean isBundleReference(Class<?> clazz) {
/* 36 */     Class<?> c = clazz;
/* 37 */     while (c != null) {
/* 38 */       if (c.getName().equals("org.osgi.framework.BundleReference")) {
/* 39 */         return true;
/*    */       }
/* 41 */       for (Class<?> ifc : c.getInterfaces()) {
/* 42 */         if (isBundleReference(ifc)) {
/* 43 */           return true;
/*    */         }
/*    */       } 
/* 46 */       c = c.getSuperclass();
/*    */     } 
/* 48 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isRunningInOsgiEnvironment() {
/* 56 */     return inOsgiEnvironment;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\OsgiUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */