package cn.hutool.aop.proxy;

import cn.hutool.aop.aspects.Aspect;
import cn.hutool.aop.interceptor.CglibInterceptor;
import net.sf.cglib.proxy.Enhancer;

public class CglibProxyFactory extends ProxyFactory {
   private static final long serialVersionUID = 1L;

   public <T> T proxy(T target, Aspect aspect) {
      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass(target.getClass());
      enhancer.setCallback(new CglibInterceptor(target, aspect));
      return enhancer.create();
   }
}
