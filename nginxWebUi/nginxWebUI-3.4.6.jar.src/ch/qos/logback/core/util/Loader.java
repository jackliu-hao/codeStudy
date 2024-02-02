/*     */ package ch.qos.logback.core.util;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ public class Loader
/*     */ {
/*     */   static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";
/*     */   private static boolean ignoreTCL = false;
/*     */   public static final String IGNORE_TCL_PROPERTY_NAME = "logback.ignoreTCL";
/*     */   private static boolean HAS_GET_CLASS_LOADER_PERMISSION = false;
/*     */   
/*     */   static {
/*  39 */     String ignoreTCLProp = OptionHelper.getSystemProperty("logback.ignoreTCL", null);
/*     */     
/*  41 */     if (ignoreTCLProp != null) {
/*  42 */       ignoreTCL = OptionHelper.toBoolean(ignoreTCLProp, true);
/*     */     }
/*     */     
/*  45 */     HAS_GET_CLASS_LOADER_PERMISSION = ((Boolean)AccessController.<Boolean>doPrivileged(new PrivilegedAction<Boolean>() {
/*     */           public Boolean run() {
/*     */             try {
/*  48 */               AccessController.checkPermission(new RuntimePermission("getClassLoader"));
/*  49 */               return Boolean.valueOf(true);
/*  50 */             } catch (SecurityException e) {
/*     */ 
/*     */               
/*  53 */               return Boolean.valueOf(false);
/*     */             } 
/*     */           }
/*     */         })).booleanValue();
/*     */   }
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
/*     */   public static Set<URL> getResources(String resource, ClassLoader classLoader) throws IOException {
/*  71 */     Set<URL> urlSet = new HashSet<URL>();
/*  72 */     Enumeration<URL> urlEnum = classLoader.getResources(resource);
/*  73 */     while (urlEnum.hasMoreElements()) {
/*  74 */       URL url = urlEnum.nextElement();
/*  75 */       urlSet.add(url);
/*     */     } 
/*  77 */     return urlSet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URL getResource(String resource, ClassLoader classLoader) {
/*     */     try {
/*  88 */       return classLoader.getResource(resource);
/*  89 */     } catch (Throwable t) {
/*  90 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URL getResourceBySelfClassLoader(String resource) {
/* 102 */     return getResource(resource, getClassLoaderOfClass(Loader.class));
/*     */   }
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
/*     */   public static ClassLoader getTCL() {
/* 115 */     return Thread.currentThread().getContextClassLoader();
/*     */   }
/*     */   
/*     */   public static Class<?> loadClass(String clazz, Context context) throws ClassNotFoundException {
/* 119 */     ClassLoader cl = getClassLoaderOfObject(context);
/* 120 */     return cl.loadClass(clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ClassLoader getClassLoaderOfObject(Object o) {
/* 131 */     if (o == null) {
/* 132 */       throw new NullPointerException("Argument cannot be null");
/*     */     }
/* 134 */     return getClassLoaderOfClass(o.getClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ClassLoader getClassLoaderAsPrivileged(final Class<?> clazz) {
/* 144 */     if (!HAS_GET_CLASS_LOADER_PERMISSION) {
/* 145 */       return null;
/*     */     }
/* 147 */     return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction<ClassLoader>() {
/*     */           public ClassLoader run() {
/* 149 */             return clazz.getClassLoader();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ClassLoader getClassLoaderOfClass(Class<?> clazz) {
/* 162 */     ClassLoader cl = clazz.getClassLoader();
/* 163 */     if (cl == null) {
/* 164 */       return ClassLoader.getSystemClassLoader();
/*     */     }
/* 166 */     return cl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> loadClass(String clazz) throws ClassNotFoundException {
/* 178 */     if (ignoreTCL) {
/* 179 */       return Class.forName(clazz);
/*     */     }
/*     */     try {
/* 182 */       return getTCL().loadClass(clazz);
/* 183 */     } catch (Throwable e) {
/*     */ 
/*     */ 
/*     */       
/* 187 */       return Class.forName(clazz);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\Loader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */