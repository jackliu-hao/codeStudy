/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.lang.func.Func0;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.stream.Collectors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Singleton
/*     */ {
/*  21 */   private static final ConcurrentHashMap<String, Object> POOL = new ConcurrentHashMap<>();
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
/*     */   public static <T> T get(Class<T> clazz, Object... params) {
/*  37 */     Assert.notNull(clazz, "Class must be not null !", new Object[0]);
/*  38 */     String key = buildKey(clazz.getName(), params);
/*  39 */     return get(key, () -> ReflectUtil.newInstance(clazz, params));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T get(String key, Func0<T> supplier) {
/*  58 */     Object value = POOL.get(key);
/*  59 */     if (null == value) {
/*  60 */       POOL.putIfAbsent(key, supplier.callWithRuntimeException());
/*  61 */       value = POOL.get(key);
/*     */     } 
/*  63 */     return (T)value;
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
/*     */   public static <T> T get(String className, Object... params) {
/*  76 */     Assert.notBlank(className, "Class name must be not blank !", new Object[0]);
/*  77 */     Class<T> clazz = ClassUtil.loadClass(className);
/*  78 */     return get(clazz, params);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void put(Object obj) {
/*  88 */     Assert.notNull(obj, "Bean object must be not null !", new Object[0]);
/*  89 */     put(obj.getClass().getName(), obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void put(String key, Object obj) {
/* 100 */     POOL.put(key, obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean exists(Class<?> clazz, Object... params) {
/* 111 */     if (null != clazz) {
/* 112 */       String key = buildKey(clazz.getName(), params);
/* 113 */       return POOL.containsKey(key);
/*     */     } 
/* 115 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<Class<?>> getExistClass() {
/* 124 */     return (Set<Class<?>>)POOL.values().stream().map(Object::getClass).collect(Collectors.toSet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void remove(Class<?> clazz) {
/* 133 */     if (null != clazz) {
/* 134 */       remove(clazz.getName());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void remove(String key) {
/* 144 */     POOL.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void destroy() {
/* 151 */     POOL.clear();
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
/*     */   private static String buildKey(String className, Object... params) {
/* 164 */     if (ArrayUtil.isEmpty(params)) {
/* 165 */       return className;
/*     */     }
/* 167 */     return StrUtil.format("{}#{}", new Object[] { className, ArrayUtil.join(params, "_") });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Singleton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */