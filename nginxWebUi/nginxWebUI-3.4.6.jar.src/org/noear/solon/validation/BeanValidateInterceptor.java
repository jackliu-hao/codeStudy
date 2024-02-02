/*    */ package org.noear.solon.validation;
/*    */ 
/*    */ import org.noear.solon.core.aspect.Interceptor;
/*    */ import org.noear.solon.core.aspect.Invocation;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BeanValidateInterceptor
/*    */   implements Interceptor
/*    */ {
/*    */   public Object doIntercept(Invocation inv) throws Throwable {
/* 16 */     ValidatorManager.validateOfInvocation(inv);
/*    */     
/* 18 */     return inv.invoke();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\BeanValidateInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */