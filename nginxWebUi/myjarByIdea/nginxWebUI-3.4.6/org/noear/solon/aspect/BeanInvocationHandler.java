package org.noear.solon.aspect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.noear.solon.aspect.asm.AsmProxy;
import org.noear.solon.core.AopContext;

public class BeanInvocationHandler implements InvocationHandler {
   private Object bean;
   private Object proxy;
   private InvocationHandler handler;
   private final AopContext context;

   public BeanInvocationHandler(AopContext ctx, Object bean, InvocationHandler handler) {
      this(ctx, bean.getClass(), bean, handler);
   }

   public BeanInvocationHandler(AopContext ctx, Class<?> clazz, Object bean, InvocationHandler handler) {
      try {
         Constructor constructor = clazz.getConstructor();
         Object[] constructorParam = new Object[0];
         this.context = ctx;
         this.handler = handler;
         this.bean = bean;
         this.proxy = AsmProxy.newProxyInstance(this.context, this, clazz, constructor, constructorParam);
      } catch (RuntimeException var7) {
         throw var7;
      } catch (Throwable var8) {
         throw new RuntimeException(var8);
      }
   }

   public Object getProxy() {
      return this.proxy;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (this.handler == null) {
         method.setAccessible(true);
         Object result = this.context.methodGet(method).invokeByAspect(this.bean, args);
         return result;
      } else {
         return this.handler.invoke(this.bean, method, args);
      }
   }
}
