/*    */ package cn.hutool.aop.interceptor;
/*    */ 
/*    */ import cn.hutool.aop.aspects.Aspect;
/*    */ import cn.hutool.core.util.ClassUtil;
/*    */ import cn.hutool.core.util.ReflectUtil;
/*    */ import java.io.Serializable;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
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
/*    */ public class JdkInterceptor
/*    */   implements InvocationHandler, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Object target;
/*    */   private final Aspect aspect;
/*    */   
/*    */   public JdkInterceptor(Object target, Aspect aspect) {
/* 31 */     this.target = target;
/* 32 */     this.aspect = aspect;
/*    */   }
/*    */   
/*    */   public Object getTarget() {
/* 36 */     return this.target;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/* 41 */     Object target = this.target;
/* 42 */     Aspect aspect = this.aspect;
/* 43 */     Object result = null;
/*    */ 
/*    */     
/* 46 */     if (aspect.before(target, method, args)) {
/* 47 */       ReflectUtil.setAccessible(method);
/*    */       
/*    */       try {
/* 50 */         result = method.invoke(ClassUtil.isStatic(method) ? null : target, args);
/* 51 */       } catch (InvocationTargetException e) {
/*    */         
/* 53 */         if (aspect.afterException(target, method, args, e.getTargetException())) {
/* 54 */           throw e;
/*    */         }
/*    */       } 
/*    */ 
/*    */       
/* 59 */       if (aspect.after(target, method, args, result)) {
/* 60 */         return result;
/*    */       }
/*    */     } 
/*    */     
/* 64 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\aop\interceptor\JdkInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */