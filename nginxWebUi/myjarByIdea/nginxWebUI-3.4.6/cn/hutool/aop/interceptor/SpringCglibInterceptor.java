package cn.hutool.aop.interceptor;

import cn.hutool.aop.aspects.Aspect;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class SpringCglibInterceptor implements MethodInterceptor, Serializable {
   private static final long serialVersionUID = 1L;
   private final Object target;
   private final Aspect aspect;

   public SpringCglibInterceptor(Object target, Aspect aspect) {
      this.target = target;
      this.aspect = aspect;
   }

   public Object getTarget() {
      return this.target;
   }

   public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
      Object target = this.target;
      Object result = null;
      if (this.aspect.before(target, method, args)) {
         try {
            result = proxy.invoke(target, args);
         } catch (InvocationTargetException var8) {
            if (this.aspect.afterException(target, method, args, var8.getTargetException())) {
               throw var8;
            }
         }
      }

      return this.aspect.after(target, method, args, result) ? result : null;
   }
}
