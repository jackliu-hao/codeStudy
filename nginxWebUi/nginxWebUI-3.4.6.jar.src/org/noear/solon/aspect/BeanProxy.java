/*    */ package org.noear.solon.aspect;
/*    */ 
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.BeanWrap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class BeanProxy
/*    */   implements BeanWrap.Proxy
/*    */ {
/* 13 */   static final BeanProxy global = new BeanProxy();
/*    */   
/*    */   InvocationHandler handler;
/*    */ 
/*    */   
/*    */   private BeanProxy() {}
/*    */   
/*    */   protected BeanProxy(InvocationHandler handler) {
/* 21 */     this.handler = handler;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getProxy(AopContext context, Object bean) {
/* 29 */     return (new BeanInvocationHandler(context, bean, this.handler)).getProxy();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\aspect\BeanProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */