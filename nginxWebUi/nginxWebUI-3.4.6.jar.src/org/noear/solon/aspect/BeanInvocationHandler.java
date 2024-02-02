/*    */ package org.noear.solon.aspect;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.Method;
/*    */ import org.noear.solon.aspect.asm.AsmProxy;
/*    */ import org.noear.solon.core.AopContext;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BeanInvocationHandler
/*    */   implements InvocationHandler
/*    */ {
/*    */   private Object bean;
/*    */   private Object proxy;
/*    */   private InvocationHandler handler;
/*    */   private final AopContext context;
/*    */   
/*    */   public BeanInvocationHandler(AopContext ctx, Object bean, InvocationHandler handler) {
/* 26 */     this(ctx, bean.getClass(), bean, handler);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BeanInvocationHandler(AopContext ctx, Class<?> clazz, Object bean, InvocationHandler handler) {
/*    */     try {
/* 34 */       Constructor<?> constructor = clazz.getConstructor(new Class[0]);
/* 35 */       Object[] constructorParam = new Object[0];
/*    */       
/* 37 */       this.context = ctx;
/* 38 */       this.handler = handler;
/* 39 */       this.bean = bean;
/* 40 */       this.proxy = AsmProxy.newProxyInstance(this.context, this, clazz, constructor, constructorParam);
/* 41 */     } catch (RuntimeException ex) {
/* 42 */       throw ex;
/* 43 */     } catch (Throwable ex) {
/* 44 */       throw new RuntimeException(ex);
/*    */     } 
/*    */   }
/*    */   
/*    */   public Object getProxy() {
/* 49 */     return this.proxy;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/* 54 */     if (this.handler == null) {
/* 55 */       method.setAccessible(true);
/*    */       
/* 57 */       Object result = this.context.methodGet(method).invokeByAspect(this.bean, args);
/*    */       
/* 59 */       return result;
/*    */     } 
/* 61 */     return this.handler.invoke(this.bean, method, args);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\aspect\BeanInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */