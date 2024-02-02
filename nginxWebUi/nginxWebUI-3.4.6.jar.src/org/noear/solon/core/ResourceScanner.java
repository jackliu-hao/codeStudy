/*     */ package org.noear.solon.core;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.Enumeration;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.event.EventBus;
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
/*     */ public class ResourceScanner
/*     */ {
/*     */   public Set<String> scan(ClassLoader classLoader, String path, Predicate<String> filter) {
/*  35 */     Set<String> urls = new LinkedHashSet<>();
/*     */     
/*  37 */     if (classLoader == null) {
/*  38 */       return urls;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/*  43 */       Enumeration<URL> roots = Utils.getResources(classLoader, path);
/*     */ 
/*     */       
/*  46 */       while (roots.hasMoreElements())
/*     */       {
/*  48 */         scanDo(roots.nextElement(), path, filter, urls);
/*     */       }
/*  50 */     } catch (IOException ex) {
/*  51 */       EventBus.push(ex);
/*     */     } 
/*     */     
/*  54 */     return urls;
/*     */   }
/*     */   
/*     */   protected void scanDo(URL url, String path, Predicate<String> filter, Set<String> urls) throws IOException {
/*  58 */     if ("file".equals(url.getProtocol())) {
/*     */ 
/*     */       
/*  61 */       String fp = URLDecoder.decode(url.getFile(), Solon.encoding());
/*  62 */       doScanByFile(new File(fp), path, filter, urls);
/*  63 */     } else if ("jar".equals(url.getProtocol())) {
/*     */ 
/*     */       
/*  66 */       JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
/*  67 */       doScanByJar(jar, path, filter, urls);
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
/*     */   
/*     */   protected void doScanByFile(File dir, String path, Predicate<String> filter, Set<String> urls) {
/*  80 */     if (!dir.exists() || !dir.isDirectory()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  85 */     File[] dirfiles = dir.listFiles(f -> (f.isDirectory() || filter.test(f.getName())));
/*     */     
/*  87 */     if (dirfiles != null) {
/*  88 */       for (File f : dirfiles) {
/*  89 */         String p2 = path + "/" + f.getName();
/*     */         
/*  91 */         if (f.isDirectory()) {
/*  92 */           doScanByFile(f, p2, filter, urls);
/*     */         }
/*  94 */         else if (p2.startsWith("/")) {
/*  95 */           urls.add(p2.substring(1));
/*     */         } else {
/*  97 */           urls.add(p2);
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
/*     */ 
/*     */   
/*     */   protected void doScanByJar(JarFile jar, String path, Predicate<String> filter, Set<String> urls) {
/* 112 */     Enumeration<JarEntry> entry = jar.entries();
/*     */     
/* 114 */     while (entry.hasMoreElements()) {
/* 115 */       JarEntry e = entry.nextElement();
/* 116 */       String n = e.getName();
/*     */       
/* 118 */       if (n.charAt(0) == '/') {
/* 119 */         n = n.substring(1);
/*     */       }
/*     */       
/* 122 */       if (e.isDirectory() || !n.startsWith(path) || !filter.test(n)) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/* 127 */       if (n.startsWith("/")) {
/* 128 */         urls.add(n.substring(1)); continue;
/*     */       } 
/* 130 */       urls.add(n);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\ResourceScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */