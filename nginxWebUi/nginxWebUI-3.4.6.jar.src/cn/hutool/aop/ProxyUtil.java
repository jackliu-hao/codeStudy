/*    */ package cn.hutool.aop;
/*    */ 
/*    */ import cn.hutool.aop.aspects.Aspect;
/*    */ import cn.hutool.aop.proxy.ProxyFactory;
/*    */ import cn.hutool.core.util.ClassUtil;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.Proxy;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ProxyUtil
/*    */ {
/*    */   public static <T> T proxy(T target, Class<? extends Aspect> aspectClass) {
/* 26 */     return (T)ProxyFactory.createProxy(target, aspectClass);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T> T proxy(T target, Aspect aspect) {
/* 38 */     return (T)ProxyFactory.createProxy(target, aspect);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T> T newProxyInstance(ClassLoader classloader, InvocationHandler invocationHandler, Class<?>... interfaces) {
/* 60 */     return (T)Proxy.newProxyInstance(classloader, interfaces, invocationHandler);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T> T newProxyInstance(InvocationHandler invocationHandler, Class<?>... interfaces) {
/* 72 */     return newProxyInstance(ClassUtil.getClassLoader(), invocationHandler, interfaces);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\aop\ProxyUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */