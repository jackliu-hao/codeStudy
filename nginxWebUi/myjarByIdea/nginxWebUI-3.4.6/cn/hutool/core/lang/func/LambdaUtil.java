package cn.hutool.core.lang.func;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;

public class LambdaUtil {
   private static final WeakConcurrentMap<String, SerializedLambda> cache = new WeakConcurrentMap();

   public static <R> Class<R> getRealClass(Func0<?> func) {
      SerializedLambda lambda = resolve(func);
      checkLambdaTypeCanGetClass(lambda.getImplMethodKind());
      return ClassUtil.loadClass(lambda.getImplClass());
   }

   public static <T> SerializedLambda resolve(Func1<T, ?> func) {
      return _resolve(func);
   }

   public static <R> SerializedLambda resolve(Func0<R> func) {
      return _resolve(func);
   }

   public static <P> String getMethodName(Func1<P, ?> func) {
      return resolve(func).getImplMethodName();
   }

   public static <R> String getMethodName(Func0<R> func) {
      return resolve(func).getImplMethodName();
   }

   public static <P, R> Class<P> getRealClass(Func1<P, R> func) {
      SerializedLambda lambda = resolve(func);
      checkLambdaTypeCanGetClass(lambda.getImplMethodKind());
      String instantiatedMethodType = lambda.getInstantiatedMethodType();
      return ClassUtil.loadClass(StrUtil.sub(instantiatedMethodType, 2, StrUtil.indexOf(instantiatedMethodType, ';')));
   }

   public static <T> String getFieldName(Func1<T, ?> func) throws IllegalArgumentException {
      return BeanUtil.getFieldName(getMethodName(func));
   }

   public static <T> String getFieldName(Func0<T> func) throws IllegalArgumentException {
      return BeanUtil.getFieldName(getMethodName(func));
   }

   private static void checkLambdaTypeCanGetClass(int implMethodKind) {
      if (implMethodKind != 5 && implMethodKind != 6) {
         throw new IllegalArgumentException("该lambda不是合适的方法引用");
      }
   }

   private static SerializedLambda _resolve(Serializable func) {
      return (SerializedLambda)cache.computeIfAbsent(func.getClass().getName(), (key) -> {
         return (SerializedLambda)ReflectUtil.invoke(func, (String)"writeReplace");
      });
   }
}
