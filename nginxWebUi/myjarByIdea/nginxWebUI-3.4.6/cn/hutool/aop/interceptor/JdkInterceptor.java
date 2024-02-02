package cn.hutool.aop.interceptor;

import cn.hutool.aop.aspects.Aspect;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JdkInterceptor implements InvocationHandler, Serializable {
   private static final long serialVersionUID = 1L;
   private final Object target;
   private final Aspect aspect;

   public JdkInterceptor(Object target, Aspect aspect) {
      this.target = target;
      this.aspect = aspect;
   }

   public Object getTarget() {
      return this.target;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      Object target = this.target;
      Aspect aspect = this.aspect;
      Object result = null;
      if (aspect.before(target, method, args)) {
         ReflectUtil.setAccessible(method);

         try {
            result = method.invoke(ClassUtil.isStatic(method) ? null : target, args);
         } catch (InvocationTargetException var8) {
            if (aspect.afterException(target, method, args, var8.getTargetException())) {
               throw var8;
            }
         }

         if (aspect.after(target, method, args, result)) {
            return result;
         }
      }

      return null;
   }
}
