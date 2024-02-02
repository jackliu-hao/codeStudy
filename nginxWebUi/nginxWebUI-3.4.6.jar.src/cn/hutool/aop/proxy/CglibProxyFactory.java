/*    */ package cn.hutool.aop.proxy;
/*    */ 
/*    */ import cn.hutool.aop.aspects.Aspect;
/*    */ import cn.hutool.aop.interceptor.CglibInterceptor;
/*    */ import net.sf.cglib.proxy.Callback;
/*    */ import net.sf.cglib.proxy.Enhancer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CglibProxyFactory
/*    */   extends ProxyFactory
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public <T> T proxy(T target, Aspect aspect) {
/* 19 */     Enhancer enhancer = new Enhancer();
/* 20 */     enhancer.setSuperclass(target.getClass());
/* 21 */     enhancer.setCallback((Callback)new CglibInterceptor(target, aspect));
/* 22 */     return (T)enhancer.create();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\aop\proxy\CglibProxyFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */