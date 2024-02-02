/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.List;
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
/*     */ public class JarClassLoader
/*     */   extends URLClassLoader
/*     */ {
/*     */   public static JarClassLoader load(File dir) {
/*  30 */     JarClassLoader loader = new JarClassLoader();
/*  31 */     loader.addJar(dir);
/*  32 */     loader.addURL(dir);
/*  33 */     return loader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JarClassLoader loadJar(File jarFile) {
/*  43 */     JarClassLoader loader = new JarClassLoader();
/*  44 */     loader.addJar(jarFile);
/*  45 */     return loader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void loadJar(URLClassLoader loader, File jarFile) throws UtilException {
/*     */     try {
/*  57 */       Method method = ClassUtil.getDeclaredMethod(URLClassLoader.class, "addURL", new Class[] { URL.class });
/*  58 */       if (null != method) {
/*  59 */         method.setAccessible(true);
/*  60 */         List<File> jars = loopJar(jarFile);
/*  61 */         for (File jar : jars) {
/*  62 */           ReflectUtil.invoke(loader, method, new Object[] { jar.toURI().toURL() });
/*     */         } 
/*     */       } 
/*  65 */     } catch (IOException e) {
/*  66 */       throw new UtilException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URLClassLoader loadJarToSystemClassLoader(File jarFile) {
/*  77 */     URLClassLoader urlClassLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
/*  78 */     loadJar(urlClassLoader, jarFile);
/*  79 */     return urlClassLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JarClassLoader() {
/*  88 */     this(new URL[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JarClassLoader(URL[] urls) {
/*  97 */     super(urls, ClassUtil.getClassLoader());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JarClassLoader(URL[] urls, ClassLoader classLoader) {
/* 107 */     super(urls, classLoader);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JarClassLoader addJar(File jarFileOrDir) {
/* 118 */     if (isJarFile(jarFileOrDir)) {
/* 119 */       return addURL(jarFileOrDir);
/*     */     }
/* 121 */     List<File> jars = loopJar(jarFileOrDir);
/* 122 */     for (File jar : jars) {
/* 123 */       addURL(jar);
/*     */     }
/* 125 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addURL(URL url) {
/* 130 */     super.addURL(url);
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
/*     */   public JarClassLoader addURL(File dir) {
/* 142 */     super.addURL(URLUtil.getURL(dir));
/* 143 */     return this;
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
/*     */   private static List<File> loopJar(File file) {
/* 155 */     return FileUtil.loopFiles(file, JarClassLoader::isJarFile);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isJarFile(File file) {
/* 166 */     if (false == FileUtil.isFile(file)) {
/* 167 */       return false;
/*     */     }
/* 169 */     return file.getPath().toLowerCase().endsWith(".jar");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\JarClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */