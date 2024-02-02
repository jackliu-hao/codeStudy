package cn.hutool.core.lang;

import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.invoke.SerializedLambda;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class Singleton {
   private static final ConcurrentHashMap<String, Object> POOL = new ConcurrentHashMap();

   private Singleton() {
   }

   public static <T> T get(Class<T> clazz, Object... params) {
      Assert.notNull(clazz, "Class must be not null !");
      String key = buildKey(clazz.getName(), params);
      return get(key, () -> {
         return ReflectUtil.newInstance(clazz, params);
      });
   }

   public static <T> T get(String key, Func0<T> supplier) {
      Object value = POOL.get(key);
      if (null == value) {
         POOL.putIfAbsent(key, supplier.callWithRuntimeException());
         value = POOL.get(key);
      }

      return value;
   }

   public static <T> T get(String className, Object... params) {
      Assert.notBlank(className, "Class name must be not blank !");
      Class<T> clazz = ClassUtil.loadClass(className);
      return get(clazz, params);
   }

   public static void put(Object obj) {
      Assert.notNull(obj, "Bean object must be not null !");
      put(obj.getClass().getName(), obj);
   }

   public static void put(String key, Object obj) {
      POOL.put(key, obj);
   }

   public static boolean exists(Class<?> clazz, Object... params) {
      if (null != clazz) {
         String key = buildKey(clazz.getName(), params);
         return POOL.containsKey(key);
      } else {
         return false;
      }
   }

   public static Set<Class<?>> getExistClass() {
      return (Set)POOL.values().stream().map(Object::getClass).collect(Collectors.toSet());
   }

   public static void remove(Class<?> clazz) {
      if (null != clazz) {
         remove(clazz.getName());
      }

   }

   public static void remove(String key) {
      POOL.remove(key);
   }

   public static void destroy() {
      POOL.clear();
   }

   private static String buildKey(String className, Object... params) {
      return ArrayUtil.isEmpty(params) ? className : StrUtil.format("{}#{}", new Object[]{className, ArrayUtil.join((Object[])params, "_")});
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "lambda$get$3f3ed817$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/lang/Singleton") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;")) {
               return () -> {
                  return ReflectUtil.newInstance(clazz, params);
               };
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
