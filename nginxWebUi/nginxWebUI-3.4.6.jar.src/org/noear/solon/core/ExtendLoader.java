/*     */ package org.noear.solon.core;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.function.Predicate;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.core.util.PrintUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExtendLoader
/*     */ {
/*  20 */   private static final ExtendLoader instance = new ExtendLoader();
/*     */ 
/*     */   
/*     */   private static String path;
/*     */ 
/*     */   
/*     */   public static String path() {
/*  27 */     return path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<ClassLoader> load(String extend) {
/*  36 */     return load(extend, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<ClassLoader> load(String extend, boolean autoMake) {
/*  46 */     return load(extend, autoMake, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<ClassLoader> load(String extend, boolean autoMake, Predicate<String> filter) {
/*  57 */     List<ClassLoader> loaders = new ArrayList<>();
/*     */     
/*  59 */     loaders.add(JarClassLoader.global());
/*     */     
/*  61 */     if (Utils.isNotEmpty(extend)) {
/*  62 */       if (extend.startsWith("!")) {
/*  63 */         extend = extend.substring(1);
/*  64 */         autoMake = true;
/*     */       } 
/*     */       
/*  67 */       extend = Utils.buildExt(extend, autoMake);
/*     */       
/*  69 */       if (extend != null) {
/*     */         
/*  71 */         path = extend;
/*     */ 
/*     */         
/*  74 */         PrintUtil.blueln("Extend: " + path);
/*     */ 
/*     */         
/*  77 */         instance.loadFile(loaders, new File(path), filter);
/*     */       } 
/*     */     } 
/*     */     
/*  81 */     return loaders;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean loadJar(List<ClassLoader> loaders, File file) {
/*     */     try {
/*  91 */       if (Solon.app().enableJarIsolation() || file.getName().startsWith("!")) {
/*  92 */         loaders.add(JarClassLoader.loadJar(file));
/*     */       } else {
/*  94 */         JarClassLoader.global().addJar(file);
/*     */       } 
/*     */       
/*  97 */       return true;
/*  98 */     } catch (Throwable ex) {
/*  99 */       EventBus.push(ex);
/* 100 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean loadJar(File file) {
/*     */     try {
/* 106 */       JarClassLoader.global().addJar(file);
/* 107 */       return true;
/* 108 */     } catch (Throwable ex) {
/* 109 */       EventBus.push(ex);
/* 110 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean unloadJar(File file) {
/*     */     try {
/* 119 */       JarClassLoader.global().removeJar(file);
/* 120 */       return true;
/* 121 */     } catch (Throwable ex) {
/* 122 */       EventBus.push(ex);
/* 123 */       return false;
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
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadFile(List<ClassLoader> loaders, File file, Predicate<String> filter) {
/* 139 */     if (!file.exists()) {
/*     */       return;
/*     */     }
/*     */     
/* 143 */     if (file.isDirectory()) {
/* 144 */       File[] tmps = file.listFiles();
/* 145 */       for (File tmp : tmps) {
/* 146 */         loadFileDo(loaders, tmp, filter);
/*     */       }
/*     */     } else {
/* 149 */       loadFileDo(loaders, file, filter);
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
/*     */   private void loadFileDo(List<ClassLoader> loaders, File file, Predicate<String> filter) {
/* 161 */     if (file.isFile()) {
/* 162 */       String path = file.getAbsolutePath();
/*     */ 
/*     */       
/* 165 */       if (filter != null && 
/* 166 */         !filter.test(path)) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 173 */         if (path.endsWith(".jar") || path.endsWith(".zip")) {
/* 174 */           loadJar(loaders, file);
/*     */           
/*     */           return;
/*     */         } 
/*     */         
/* 179 */         if (path.endsWith(".properties")) {
/* 180 */           Solon.cfg().loadAdd(file.toURI().toURL());
/*     */           
/* 182 */           PrintUtil.blueln("loaded: " + path);
/*     */           
/*     */           return;
/*     */         } 
/* 186 */         if (path.endsWith(".yml")) {
/* 187 */           if (!PropsLoader.global().isSupport(path)) {
/* 188 */             throw new IllegalStateException("Do not support the *.yml");
/*     */           }
/*     */           
/* 191 */           Solon.cfg().loadAdd(file.toURI().toURL());
/*     */           
/* 193 */           PrintUtil.blueln("loaded: " + path);
/*     */           return;
/*     */         } 
/* 196 */       } catch (Throwable ex) {
/* 197 */         EventBus.push(ex);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\ExtendLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */