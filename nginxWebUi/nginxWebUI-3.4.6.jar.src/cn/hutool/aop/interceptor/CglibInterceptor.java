/*    */ package cn.hutool.aop.interceptor;
/*    */ 
/*    */ import cn.hutool.aop.aspects.Aspect;
/*    */ import java.io.Serializable;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import net.sf.cglib.proxy.MethodInterceptor;
/*    */ import net.sf.cglib.proxy.MethodProxy;
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
/*    */ public class CglibInterceptor
/*    */   implements MethodInterceptor, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Object target;
/*    */   private final Aspect aspect;
/*    */   
/*    */   public CglibInterceptor(Object target, Aspect aspect) {
/* 29 */     this.target = target;
/* 30 */     this.aspect = aspect;
/*    */   }
/*    */   
/*    */   public Object getTarget() {
/* 34 */     return this.target;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
/* 39 */     Object target = this.target;
/* 40 */     Object result = null;
/*    */     
/* 42 */     if (this.aspect.before(target, method, args)) {
/*    */       
/*    */       try {
/* 45 */         result = proxy.invoke(target, args);
/* 46 */       } catch (InvocationTargetException e) {
/*    */         
/* 48 */         if (this.aspect.afterException(target, method, args, e.getTargetException())) {
/* 49 */           throw e;
/*    */         }
/*    */       } 
/*    */     }
/*    */ 
/*    */     
/* 55 */     if (this.aspect.after(target, method, args, result)) {
/* 56 */       return result;
/*    */     }
/* 58 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\aop\interceptor\CglibInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */