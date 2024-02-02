/*     */ package org.noear.solon.aspect;
/*     */ 
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.function.Predicate;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.Aop;
/*     */ import org.noear.solon.core.BeanWrap;
/*     */ import org.noear.solon.core.JarClassLoader;
/*     */ import org.noear.solon.core.util.ScanUtil;
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
/*     */ public class AspectUtil
/*     */ {
/*     */   public static boolean binding(BeanWrap bw, String name, boolean typed) {
/*  29 */     if (bw.proxy() instanceof AspectUtil) {
/*  30 */       return false;
/*     */     }
/*  32 */     bw.proxySet(BeanProxy.global);
/*  33 */     bw.context().beanRegister(bw, name, typed);
/*  34 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean binding(BeanWrap bw) {
/*  42 */     return binding(bw, "", false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   private static Set<Class<?>> tryAttachCached = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void attach(Class<?> clz, InvocationHandler handler) {
/*  56 */     if (clz.isAnnotation() || clz.isInterface() || clz.isEnum() || clz.isPrimitive()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  61 */     if (tryAttachCached.contains(clz)) {
/*     */       return;
/*     */     }
/*  64 */     tryAttachCached.add(clz);
/*     */ 
/*     */     
/*  67 */     Aop.wrapAndPut(clz).proxySet(new BeanProxy(handler));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void attachByScan(String basePackage, InvocationHandler handler) {
/*  77 */     attachByScan((ClassLoader)JarClassLoader.global(), basePackage, null, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void attachByScan(String basePackage, Predicate<String> filter, InvocationHandler handler) {
/*  88 */     attachByScan((ClassLoader)JarClassLoader.global(), basePackage, filter, handler);
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
/*     */   public static void attachByScan(ClassLoader classLoader, String basePackage, Predicate<String> filter, InvocationHandler handler) {
/* 100 */     if (Utils.isEmpty(basePackage)) {
/*     */       return;
/*     */     }
/*     */     
/* 104 */     if (classLoader == null) {
/*     */       return;
/*     */     }
/*     */     
/* 108 */     if (filter == null) {
/* 109 */       filter = (s -> true);
/*     */     }
/*     */     
/* 112 */     String dir = basePackage.replace('.', '/');
/*     */ 
/*     */     
/* 115 */     ScanUtil.scan(classLoader, dir, n -> n.endsWith(".class"))
/* 116 */       .stream()
/* 117 */       .sorted(Comparator.comparing(s -> Integer.valueOf(s.length())))
/* 118 */       .filter(filter)
/* 119 */       .forEach(name -> {
/*     */           String className = name.substring(0, name.length() - 6);
/*     */           Class<?> clz = Utils.loadClass(classLoader, className.replace("/", "."));
/*     */           if (clz != null)
/*     */             attach(clz, handler); 
/*     */         });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\aspect\AspectUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */