/*     */ package org.noear.solon.core;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.net.URLConnection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JarClassLoader
/*     */   extends URLClassLoader
/*     */ {
/*     */   private static JarClassLoader global;
/*     */   
/*     */   static {
/*  24 */     JarClassLoader tmp = (JarClassLoader)Utils.newInstance("org.noear.solon.extend.impl.JarClassLoaderEx");
/*     */     
/*  26 */     if (tmp == null) {
/*  27 */       global = new JarClassLoader();
/*     */     } else {
/*  29 */       global = tmp;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JarClassLoader global() {
/*  37 */     return global;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void globalSet(JarClassLoader instance) {
/*  44 */     if (instance != null) {
/*  45 */       global = instance;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JarClassLoader loadJar(URL url) {
/*  53 */     JarClassLoader loader = new JarClassLoader();
/*  54 */     loader.addJar(url);
/*     */     
/*  56 */     return loader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JarClassLoader loadJar(File fileOrDir) {
/*  63 */     JarClassLoader loader = new JarClassLoader();
/*  64 */     loader.addJar(fileOrDir);
/*     */     
/*  66 */     return loader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  74 */   private Map<URL, JarURLConnection> cachedMap = new HashMap<>();
/*     */   
/*     */   public JarClassLoader() {
/*  77 */     this(Utils.getClassLoader());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JarClassLoader(ClassLoader parent) {
/*  84 */     super(new URL[0], parent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JarClassLoader(URL[] urls, ClassLoader parent) {
/*  92 */     super(urls, parent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addJar(URL url) {
/* 102 */     addJar(url, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addJar(File file) {
/*     */     try {
/* 112 */       addJar(file.toURI().toURL(), true);
/* 113 */     } catch (MalformedURLException ex) {
/* 114 */       throw new RuntimeException(ex);
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
/*     */   public void addJar(URL url, boolean useCaches) {
/*     */     try {
/* 127 */       URLConnection uc = url.openConnection();
/* 128 */       if (uc instanceof JarURLConnection) {
/* 129 */         JarURLConnection juc = (JarURLConnection)uc;
/* 130 */         juc.setUseCaches(useCaches);
/* 131 */         juc.getManifest();
/*     */         
/* 133 */         this.cachedMap.put(url, juc);
/*     */       } 
/* 135 */     } catch (Throwable ex) {
/* 136 */       System.err.println("Failed to cache plugin JAR file: " + url.toExternalForm());
/*     */     } 
/*     */     
/* 139 */     addURL(url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeJar(URL url) {
/* 148 */     JarURLConnection jarURL = this.cachedMap.get(url);
/*     */     
/*     */     try {
/* 151 */       if (jarURL != null) {
/* 152 */         jarURL.getJarFile().close();
/* 153 */         this.cachedMap.remove(url);
/*     */       } 
/* 155 */     } catch (Throwable ex) {
/* 156 */       System.err.println("Failed to unload JAR file\n" + ex);
/*     */     } 
/*     */   }
/*     */   public void removeJar(File file) {
/*     */     try {
/* 161 */       removeJar(file.toURI().toURL());
/* 162 */     } catch (MalformedURLException ex) {
/* 163 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 169 */     super.close();
/*     */     
/* 171 */     for (JarURLConnection jarURL : this.cachedMap.values()) {
/* 172 */       jarURL.getJarFile().close();
/*     */     }
/*     */     
/* 175 */     this.cachedMap.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> loadClass(String clzName) throws ClassNotFoundException {
/* 185 */     return super.loadClass(clzName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void bindingThread() {
/* 192 */     Thread.currentThread().setContextClassLoader(global());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration<URL> getResources(String name) throws IOException {
/* 200 */     Enumeration<URL> urls = super.getResources(name);
/*     */     
/* 202 */     if (urls == null || !urls.hasMoreElements()) {
/* 203 */       urls = ClassLoader.getSystemResources(name);
/*     */     }
/*     */     
/* 206 */     return urls;
/*     */   }
/*     */ 
/*     */   
/*     */   public URL getResource(String name) {
/* 211 */     URL url = super.getResource(name);
/*     */     
/* 213 */     if (url == null) {
/* 214 */       url = JarClassLoader.class.getResource(name);
/*     */     }
/*     */     
/* 217 */     return url;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\JarClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */