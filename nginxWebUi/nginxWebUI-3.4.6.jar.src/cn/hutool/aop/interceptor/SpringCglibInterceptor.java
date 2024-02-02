/*    */ package cn.hutool.aop.interceptor;
/*    */ 
/*    */ import cn.hutool.aop.aspects.Aspect;
/*    */ import java.io.Serializable;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import org.springframework.cglib.proxy.MethodInterceptor;
/*    */ import org.springframework.cglib.proxy.MethodProxy;
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
/*    */ public class SpringCglibInterceptor
/*    */   implements MethodInterceptor, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Object target;
/*    */   private final Aspect aspect;
/*    */   
/*    */   public SpringCglibInterceptor(Object target, Aspect aspect) {
/* 29 */     this.target = target;
/* 30 */     this.aspect = aspect;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getTarget() {
/* 39 */     return this.target;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
/* 44 */     Object target = this.target;
/* 45 */     Object result = null;
/*    */     
/* 47 */     if (this.aspect.before(target, method, args)) {
/*    */       
/*    */       try {
/* 50 */         result = proxy.invoke(target, args);
/* 51 */       } catch (InvocationTargetException e) {
/*    */         
/* 53 */         if (this.aspect.afterException(target, method, args, e.getTargetException())) {
/* 54 */           throw e;
/*    */         }
/*    */       } 
/*    */     }
/*    */ 
/*    */     
/* 60 */     if (this.aspect.after(target, method, args, result)) {
/* 61 */       return result;
/*    */     }
/* 63 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\aop\interceptor\SpringCglibInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */