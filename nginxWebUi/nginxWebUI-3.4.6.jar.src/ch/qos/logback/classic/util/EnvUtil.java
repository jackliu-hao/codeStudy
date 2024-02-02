/*    */ package ch.qos.logback.classic.util;
/*    */ 
/*    */ import ch.qos.logback.core.util.Loader;
/*    */ import java.util.Iterator;
/*    */ import java.util.ServiceLoader;
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
/*    */ public class EnvUtil
/*    */ {
/* 31 */   static ClassLoader testServiceLoaderClassLoader = null;
/*    */   
/*    */   public static boolean isGroovyAvailable() {
/* 34 */     ClassLoader classLoader = Loader.getClassLoaderOfClass(EnvUtil.class);
/*    */     try {
/* 36 */       Class<?> bindingClass = classLoader.loadClass("groovy.lang.Binding");
/* 37 */       return (bindingClass != null);
/* 38 */     } catch (ClassNotFoundException e) {
/* 39 */       return false;
/*    */     } 
/*    */   }
/*    */   
/*    */   private static ClassLoader getServiceLoaderClassLoader() {
/* 44 */     return (testServiceLoaderClassLoader == null) ? Loader.getClassLoaderOfClass(EnvUtil.class) : testServiceLoaderClassLoader;
/*    */   }
/*    */   
/*    */   public static <T> T loadFromServiceLoader(Class<T> c) {
/* 48 */     ServiceLoader<T> loader = ServiceLoader.load(c, getServiceLoaderClassLoader());
/* 49 */     Iterator<T> it = loader.iterator();
/* 50 */     if (it.hasNext())
/* 51 */       return it.next(); 
/* 52 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classi\\util\EnvUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */