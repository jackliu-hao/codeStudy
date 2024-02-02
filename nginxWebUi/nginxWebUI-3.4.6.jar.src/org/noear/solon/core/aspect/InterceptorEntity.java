/*    */ package org.noear.solon.core.aspect;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InterceptorEntity
/*    */   implements Interceptor
/*    */ {
/*    */   private final int index;
/*    */   private final Interceptor real;
/*    */   
/*    */   public InterceptorEntity(int index, Interceptor real) {
/* 17 */     this.index = index;
/* 18 */     this.real = real;
/*    */   }
/*    */   
/*    */   public int getIndex() {
/* 22 */     return this.index;
/*    */   }
/*    */   
/*    */   public Interceptor getReal() {
/* 26 */     return this.real;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object doIntercept(Invocation inv) throws Throwable {
/* 34 */     return this.real.doIntercept(inv);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\aspect\InterceptorEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */