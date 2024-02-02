/*    */ package cn.hutool.aop.aspects;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimpleAspect
/*    */   implements Aspect, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public boolean before(Object target, Method method, Object[] args) {
/* 18 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean after(Object target, Method method, Object[] args, Object returnVal) {
/* 24 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean afterException(Object target, Method method, Object[] args, Throwable e) {
/* 30 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\aop\aspects\SimpleAspect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */