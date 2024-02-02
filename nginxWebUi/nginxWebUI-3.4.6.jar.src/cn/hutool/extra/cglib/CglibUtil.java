/*     */ package cn.hutool.extra.cglib;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import net.sf.cglib.beans.BeanCopier;
/*     */ import net.sf.cglib.beans.BeanMap;
/*     */ import net.sf.cglib.core.Converter;
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
/*     */ public class CglibUtil
/*     */ {
/*     */   public static <T> T copy(Object source, Class<T> targetClass) {
/*  34 */     return copy(source, targetClass, (Converter)null);
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
/*     */   public static <T> T copy(Object source, Class<T> targetClass, Converter converter) {
/*  48 */     T target = (T)ReflectUtil.newInstanceIfPossible(targetClass);
/*  49 */     copy(source, target, converter);
/*  50 */     return target;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void copy(Object source, Object target) {
/*  60 */     copy(source, target, (Converter)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void copy(Object source, Object target, Converter converter) {
/*  71 */     Assert.notNull(source, "Source bean must be not null.", new Object[0]);
/*  72 */     Assert.notNull(target, "Target bean must be not null.", new Object[0]);
/*     */     
/*  74 */     Class<?> sourceClass = source.getClass();
/*  75 */     Class<?> targetClass = target.getClass();
/*  76 */     BeanCopier beanCopier = BeanCopierCache.INSTANCE.get(sourceClass, targetClass, converter);
/*     */     
/*  78 */     beanCopier.copy(source, target, converter);
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
/*     */   public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target) {
/*  91 */     return copyList(source, target, null, null);
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
/*     */   public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target, Converter converter) {
/* 106 */     return copyList(source, target, converter, null);
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
/*     */   public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target, BiConsumer<S, T> callback) {
/* 121 */     return copyList(source, target, null, callback);
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
/*     */   public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target, Converter converter, BiConsumer<S, T> callback) {
/* 136 */     return (List<T>)source.stream().map(s -> {
/*     */           T t = target.get();
/*     */           copy(s, t, converter);
/*     */           if (callback != null) {
/*     */             callback.accept(s, t);
/*     */           }
/*     */           return (Function)t;
/* 143 */         }).collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanMap toMap(Object bean) {
/* 154 */     return BeanMap.create(bean);
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
/*     */   public static <T> T fillBean(Map map, T bean) {
/* 167 */     BeanMap.create(bean).putAll(map);
/* 168 */     return bean;
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
/*     */   public static <T> T toBean(Map map, Class<T> beanClass) {
/* 181 */     return fillBean(map, (T)ReflectUtil.newInstanceIfPossible(beanClass));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\cglib\CglibUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */