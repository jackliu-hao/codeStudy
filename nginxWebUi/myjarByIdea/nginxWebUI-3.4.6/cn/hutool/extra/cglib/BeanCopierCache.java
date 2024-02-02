package cn.hutool.extra.cglib;

import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.StrUtil;
import java.lang.invoke.SerializedLambda;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;

public enum BeanCopierCache {
   INSTANCE;

   private final WeakConcurrentMap<String, BeanCopier> cache = new WeakConcurrentMap();

   public BeanCopier get(Class<?> srcClass, Class<?> targetClass, Converter converter) {
      return this.get(srcClass, targetClass, null != converter);
   }

   public BeanCopier get(Class<?> srcClass, Class<?> targetClass, boolean useConverter) {
      String key = this.genKey(srcClass, targetClass, useConverter);
      return (BeanCopier)this.cache.computeIfAbsent(key, () -> {
         return BeanCopier.create(srcClass, targetClass, useConverter);
      });
   }

   private String genKey(Class<?> srcClass, Class<?> targetClass, boolean useConverter) {
      StringBuilder key = StrUtil.builder().append(srcClass.getName()).append('#').append(targetClass.getName()).append('#').append(useConverter ? 1 : 0);
      return key.toString();
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "lambda$get$3b77ef17$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/cglib/BeanCopierCache") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;Ljava/lang/Class;Z)Lnet/sf/cglib/beans/BeanCopier;")) {
               return () -> {
                  return BeanCopier.create(srcClass, targetClass, useConverter);
               };
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
