/*     */ package org.noear.solon.core;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.BiPredicate;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Predicate;
/*     */ import org.noear.solon.Utils;
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
/*     */ public class Aop
/*     */ {
/*  34 */   private static final AopContext ac = new AopContext();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static AopContext context() {
/*  40 */     return ac;
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
/*     */   public static BeanWrap wrap(Class<?> type, Object bean) {
/*  53 */     return ac.wrap(type, bean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanWrap wrapAndPut(Class<?> type) {
/*  62 */     return wrapAndPut(type, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanWrap wrapAndPut(Class<?> type, Object bean) {
/*  72 */     return ac.wrapAndPut(type, bean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean has(Object nameOrType) {
/*  83 */     return ac.hasWrap(nameOrType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T get(String name) {
/*  93 */     return ac.getBean(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T get(Class<T> type) {
/* 102 */     return ac.getBean(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T getOrNew(Class<T> type) {
/* 111 */     return ac.getBeanOrNew(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void getAsyn(String name, Consumer<BeanWrap> callback) {
/* 120 */     ac.getWrapAsyn(name, callback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void getAsyn(Class<?> type, Consumer<BeanWrap> callback) {
/* 129 */     ac.getWrapAsyn(type, callback);
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
/*     */   public static <T> T inject(T bean) {
/* 141 */     ac.beanInject(bean);
/* 142 */     return bean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T inject(T bean, Properties propS) {
/* 152 */     return (T)Utils.injectProperties(bean, propS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void beanOnloaded(Consumer<AopContext> fun) {
/* 160 */     ac.beanOnloaded(fun);
/*     */   }
/*     */   
/*     */   public static void beanOnloaded(int index, Consumer<AopContext> fun) {
/* 164 */     ac.beanOnloaded(index, fun);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void beanForeach(BiConsumer<String, BeanWrap> action) {
/* 173 */     ac.beanForeach(action);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void beanForeach(Consumer<BeanWrap> action) {
/* 182 */     ac.beanForeach(action);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<BeanWrap> beanFind(BiPredicate<String, BeanWrap> filter) {
/* 191 */     return ac.beanFind(filter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<BeanWrap> beanFind(Predicate<BeanWrap> filter) {
/* 200 */     return ac.beanFind(filter);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\Aop.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */