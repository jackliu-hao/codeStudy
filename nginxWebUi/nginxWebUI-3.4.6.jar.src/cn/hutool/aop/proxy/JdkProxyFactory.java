/*    */ package cn.hutool.aop.proxy;
/*    */ 
/*    */ import cn.hutool.aop.ProxyUtil;
/*    */ import cn.hutool.aop.aspects.Aspect;
/*    */ import cn.hutool.aop.interceptor.JdkInterceptor;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JdkProxyFactory
/*    */   extends ProxyFactory
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public <T> T proxy(T target, Aspect aspect) {
/* 17 */     return (T)ProxyUtil.newProxyInstance(target
/* 18 */         .getClass().getClassLoader(), (InvocationHandler)new JdkInterceptor(target, aspect), target
/*    */         
/* 20 */         .getClass().getInterfaces());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\aop\proxy\JdkProxyFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */