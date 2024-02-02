/*     */ package org.slf4j.helpers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Util
/*     */ {
/*     */   private static ClassContextSecurityManager SECURITY_MANAGER;
/*     */   
/*     */   public static String safeGetSystemProperty(String key) {
/*  40 */     if (key == null) {
/*  41 */       throw new IllegalArgumentException("null input");
/*     */     }
/*  43 */     String result = null;
/*     */     try {
/*  45 */       result = System.getProperty(key);
/*  46 */     } catch (SecurityException securityException) {}
/*     */ 
/*     */     
/*  49 */     return result;
/*     */   }
/*     */   
/*     */   public static boolean safeGetBooleanSystemProperty(String key) {
/*  53 */     String value = safeGetSystemProperty(key);
/*  54 */     if (value == null) {
/*  55 */       return false;
/*     */     }
/*  57 */     return value.equalsIgnoreCase("true");
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class ClassContextSecurityManager
/*     */     extends SecurityManager
/*     */   {
/*     */     private ClassContextSecurityManager() {}
/*     */     
/*     */     protected Class<?>[] getClassContext() {
/*  67 */       return super.getClassContext();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = false;
/*     */   
/*     */   private static ClassContextSecurityManager getSecurityManager() {
/*  75 */     if (SECURITY_MANAGER != null)
/*  76 */       return SECURITY_MANAGER; 
/*  77 */     if (SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED) {
/*  78 */       return null;
/*     */     }
/*  80 */     SECURITY_MANAGER = safeCreateSecurityManager();
/*  81 */     SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = true;
/*  82 */     return SECURITY_MANAGER;
/*     */   }
/*     */ 
/*     */   
/*     */   private static ClassContextSecurityManager safeCreateSecurityManager() {
/*     */     try {
/*  88 */       return new ClassContextSecurityManager();
/*  89 */     } catch (SecurityException sm) {
/*  90 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> getCallingClass() {
/* 100 */     ClassContextSecurityManager securityManager = getSecurityManager();
/* 101 */     if (securityManager == null)
/* 102 */       return null; 
/* 103 */     Class<?>[] trace = securityManager.getClassContext();
/* 104 */     String thisClassName = Util.class.getName();
/*     */     
/*     */     int i;
/*     */     
/* 108 */     for (i = 0; i < trace.length && 
/* 109 */       !thisClassName.equals(trace[i].getName()); i++);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     if (i >= trace.length || i + 2 >= trace.length) {
/* 115 */       throw new IllegalStateException("Failed to find org.slf4j.helpers.Util or its caller in the stack; this should not happen");
/*     */     }
/*     */     
/* 118 */     return trace[i + 2];
/*     */   }
/*     */   
/*     */   public static final void report(String msg, Throwable t) {
/* 122 */     System.err.println(msg);
/* 123 */     System.err.println("Reported exception:");
/* 124 */     t.printStackTrace();
/*     */   }
/*     */   
/*     */   public static final void report(String msg) {
/* 128 */     System.err.println("SLF4J: " + msg);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\helpers\Util.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */