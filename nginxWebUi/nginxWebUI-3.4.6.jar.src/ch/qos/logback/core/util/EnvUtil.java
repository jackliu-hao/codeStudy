/*    */ package ch.qos.logback.core.util;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class EnvUtil
/*    */ {
/*    */   private static boolean isJDK_N_OrHigher(int n) {
/* 25 */     List<String> versionList = new ArrayList<String>();
/*    */ 
/*    */     
/* 28 */     for (int i = 0; i < 5; i++) {
/* 29 */       versionList.add("1." + (n + i));
/*    */     }
/*    */     
/* 32 */     String javaVersion = System.getProperty("java.version");
/* 33 */     if (javaVersion == null) {
/* 34 */       return false;
/*    */     }
/* 36 */     for (String v : versionList) {
/* 37 */       if (javaVersion.startsWith(v))
/* 38 */         return true; 
/*    */     } 
/* 40 */     return false;
/*    */   }
/*    */   
/*    */   public static boolean isJDK5() {
/* 44 */     return isJDK_N_OrHigher(5);
/*    */   }
/*    */   
/*    */   public static boolean isJDK6OrHigher() {
/* 48 */     return isJDK_N_OrHigher(6);
/*    */   }
/*    */   
/*    */   public static boolean isJDK7OrHigher() {
/* 52 */     return isJDK_N_OrHigher(7);
/*    */   }
/*    */   
/*    */   public static boolean isJaninoAvailable() {
/* 56 */     ClassLoader classLoader = EnvUtil.class.getClassLoader();
/*    */     try {
/* 58 */       Class<?> bindingClass = classLoader.loadClass("org.codehaus.janino.ScriptEvaluator");
/* 59 */       return (bindingClass != null);
/* 60 */     } catch (ClassNotFoundException e) {
/* 61 */       return false;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static boolean isWindows() {
/* 66 */     String os = System.getProperty("os.name");
/* 67 */     return os.startsWith("Windows");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\EnvUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */