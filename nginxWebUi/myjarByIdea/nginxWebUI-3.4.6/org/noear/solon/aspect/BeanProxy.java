package org.noear.solon.aspect;

import java.lang.reflect.InvocationHandler;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;

class BeanProxy implements BeanWrap.Proxy {
   static final BeanProxy global = new BeanProxy();
   InvocationHandler handler;

   private BeanProxy() {
   }

   protected BeanProxy(InvocationHandler handler) {
      this.handler = handler;
   }

   public Object getProxy(AopContext context, Object bean) {
      return (new BeanInvocationHandler(context, bean, this.handler)).getProxy();
   }
}
