/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.collection.EnumerationIter;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.resource.ResourceUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ClassLoaderUtil;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
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
/*     */ public class ClassScanner
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final String packageName;
/*     */   private final String packageNameWithDot;
/*     */   private final String packageDirName;
/*     */   private final String packagePath;
/*     */   private final Filter<Class<?>> classFilter;
/*     */   private final Charset charset;
/*     */   private ClassLoader classLoader;
/*     */   private boolean initialize;
/*  67 */   private final Set<Class<?>> classes = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<Class<?>> scanAllPackageByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
/*  77 */     return scanAllPackage(packageName, clazz -> clazz.isAnnotationPresent(annotationClass));
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
/*     */   public static Set<Class<?>> scanPackageByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
/*  89 */     return scanPackage(packageName, clazz -> clazz.isAnnotationPresent(annotationClass));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<Class<?>> scanAllPackageBySuper(String packageName, Class<?> superClass) {
/* 100 */     return scanAllPackage(packageName, clazz -> (superClass.isAssignableFrom(clazz) && !superClass.equals(clazz)));
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
/*     */   public static Set<Class<?>> scanPackageBySuper(String packageName, Class<?> superClass) {
/* 112 */     return scanPackage(packageName, clazz -> (superClass.isAssignableFrom(clazz) && !superClass.equals(clazz)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<Class<?>> scanAllPackage() {
/* 122 */     return scanAllPackage("", null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<Class<?>> scanPackage() {
/* 131 */     return scanPackage("", null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<Class<?>> scanPackage(String packageName) {
/* 141 */     return scanPackage(packageName, null);
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
/*     */   public static Set<Class<?>> scanAllPackage(String packageName, Filter<Class<?>> classFilter) {
/* 155 */     return (new ClassScanner(packageName, classFilter)).scan(true);
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
/*     */   public static Set<Class<?>> scanPackage(String packageName, Filter<Class<?>> classFilter) {
/* 168 */     return (new ClassScanner(packageName, classFilter)).scan();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassScanner() {
/* 175 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassScanner(String packageName) {
/* 184 */     this(packageName, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassScanner(String packageName, Filter<Class<?>> classFilter) {
/* 194 */     this(packageName, classFilter, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassScanner(String packageName, Filter<Class<?>> classFilter, Charset charset) {
/* 205 */     packageName = StrUtil.nullToEmpty(packageName);
/* 206 */     this.packageName = packageName;
/* 207 */     this.packageNameWithDot = StrUtil.addSuffixIfNot(packageName, ".");
/* 208 */     this.packageDirName = packageName.replace('.', File.separatorChar);
/* 209 */     this.packagePath = packageName.replace('.', '/');
/* 210 */     this.classFilter = classFilter;
/* 211 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Class<?>> scan() {
/* 221 */     return scan(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Class<?>> scan(boolean forceScanJavaClassPaths) {
/* 232 */     for (URL url : ResourceUtil.getResourceIter(this.packagePath)) {
/* 233 */       switch (url.getProtocol()) {
/*     */         case "file":
/* 235 */           scanFile(new File(URLUtil.decode(url.getFile(), this.charset.name())), null);
/*     */         
/*     */         case "jar":
/* 238 */           scanJar(URLUtil.getJarFile(url));
/*     */       } 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 244 */     if (forceScanJavaClassPaths || CollUtil.isEmpty(this.classes)) {
/* 245 */       scanJavaClassPaths();
/*     */     }
/*     */     
/* 248 */     return Collections.unmodifiableSet(this.classes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInitialize(boolean initialize) {
/* 257 */     this.initialize = initialize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setClassLoader(ClassLoader classLoader) {
/* 267 */     this.classLoader = classLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void scanJavaClassPaths() {
/* 276 */     String[] javaClassPaths = ClassUtil.getJavaClassPaths();
/* 277 */     for (String classPath : javaClassPaths) {
/*     */       
/* 279 */       classPath = URLUtil.decode(classPath, CharsetUtil.systemCharsetName());
/*     */       
/* 281 */       scanFile(new File(classPath), null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void scanFile(File file, String rootDir) {
/* 292 */     if (file.isFile()) {
/* 293 */       String fileName = file.getAbsolutePath();
/* 294 */       if (fileName.endsWith(".class")) {
/*     */ 
/*     */ 
/*     */         
/* 298 */         String className = fileName.substring(rootDir.length(), fileName.length() - 6).replace(File.separatorChar, '.');
/*     */         
/* 300 */         addIfAccept(className);
/* 301 */       } else if (fileName.endsWith(".jar")) {
/*     */         try {
/* 303 */           scanJar(new JarFile(file));
/* 304 */         } catch (IOException e) {
/* 305 */           throw new IORuntimeException(e);
/*     */         } 
/*     */       } 
/* 308 */     } else if (file.isDirectory()) {
/* 309 */       File[] files = file.listFiles();
/* 310 */       if (null != files) {
/* 311 */         for (File subFile : files) {
/* 312 */           scanFile(subFile, (null == rootDir) ? subPathBeforePackage(file) : rootDir);
/*     */         }
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void scanJar(JarFile jar) {
/* 325 */     for (JarEntry entry : new EnumerationIter(jar.entries())) {
/* 326 */       String name = StrUtil.removePrefix(entry.getName(), "/");
/* 327 */       if ((StrUtil.isEmpty(this.packagePath) || name.startsWith(this.packagePath)) && 
/* 328 */         name.endsWith(".class") && false == entry.isDirectory()) {
/*     */ 
/*     */         
/* 331 */         String className = name.substring(0, name.length() - 6).replace('/', '.');
/* 332 */         addIfAccept(loadClass(className));
/*     */       } 
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
/*     */   private Class<?> loadClass(String className) {
/* 345 */     ClassLoader loader = this.classLoader;
/* 346 */     if (null == loader) {
/* 347 */       loader = ClassLoaderUtil.getClassLoader();
/* 348 */       this.classLoader = loader;
/*     */     } 
/*     */     
/* 351 */     Class<?> clazz = null;
/*     */     try {
/* 353 */       clazz = Class.forName(className, this.initialize, loader);
/* 354 */     } catch (NoClassDefFoundError|ClassNotFoundException noClassDefFoundError) {
/*     */     
/* 356 */     } catch (UnsupportedClassVersionError unsupportedClassVersionError) {
/*     */     
/* 358 */     } catch (Exception e) {
/* 359 */       throw new RuntimeException(e);
/*     */     } 
/* 361 */     return clazz;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addIfAccept(String className) {
/* 370 */     if (StrUtil.isBlank(className)) {
/*     */       return;
/*     */     }
/* 373 */     int classLen = className.length();
/* 374 */     int packageLen = this.packageName.length();
/* 375 */     if (classLen == packageLen) {
/*     */       
/* 377 */       if (className.equals(this.packageName)) {
/* 378 */         addIfAccept(loadClass(className));
/*     */       }
/* 380 */     } else if (classLen > packageLen) {
/*     */       
/* 382 */       if (".".equals(this.packageNameWithDot) || className.startsWith(this.packageNameWithDot)) {
/* 383 */         addIfAccept(loadClass(className));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addIfAccept(Class<?> clazz) {
/* 394 */     if (null != clazz) {
/* 395 */       Filter<Class<?>> classFilter = this.classFilter;
/* 396 */       if (classFilter == null || classFilter.accept(clazz)) {
/* 397 */         this.classes.add(clazz);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String subPathBeforePackage(File file) {
/* 409 */     String filePath = file.getAbsolutePath();
/* 410 */     if (StrUtil.isNotEmpty(this.packageDirName)) {
/* 411 */       filePath = StrUtil.subBefore(filePath, this.packageDirName, true);
/*     */     }
/* 413 */     return StrUtil.addSuffixIfNot(filePath, File.separator);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\ClassScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */