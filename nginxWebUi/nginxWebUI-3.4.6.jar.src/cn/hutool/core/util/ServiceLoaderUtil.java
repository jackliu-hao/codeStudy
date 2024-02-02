/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ServiceConfigurationError;
/*     */ import java.util.ServiceLoader;
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
/*     */ public class ServiceLoaderUtil
/*     */ {
/*     */   public static <T> T loadFirstAvailable(Class<T> clazz) {
/*  33 */     Iterator<T> iterator = load(clazz).iterator();
/*  34 */     while (iterator.hasNext()) {
/*     */       try {
/*  36 */         return iterator.next();
/*  37 */       } catch (ServiceConfigurationError serviceConfigurationError) {}
/*     */     } 
/*     */ 
/*     */     
/*  41 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T loadFirst(Class<T> clazz) {
/*  52 */     Iterator<T> iterator = load(clazz).iterator();
/*  53 */     if (iterator.hasNext()) {
/*  54 */       return iterator.next();
/*     */     }
/*  56 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> ServiceLoader<T> load(Class<T> clazz) {
/*  67 */     return load(clazz, null);
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
/*     */   public static <T> ServiceLoader<T> load(Class<T> clazz, ClassLoader loader) {
/*  79 */     return ServiceLoader.load(clazz, ObjectUtil.<ClassLoader>defaultIfNull(loader, ClassLoaderUtil::getClassLoader));
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
/*     */   public static <T> List<T> loadList(Class<T> clazz) {
/*  91 */     return loadList(clazz, null);
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
/*     */   public static <T> List<T> loadList(Class<T> clazz, ClassLoader loader) {
/* 104 */     return ListUtil.list(false, load(clazz, loader));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\ServiceLoaderUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */