/*    */ package org.noear.solon.data.around;
/*    */ 
/*    */ import org.noear.solon.core.ValHolder;
/*    */ import org.noear.solon.core.aspect.Interceptor;
/*    */ import org.noear.solon.core.aspect.Invocation;
/*    */ import org.noear.solon.data.annotation.Tran;
/*    */ import org.noear.solon.data.tran.TranUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TranInterceptor
/*    */   implements Interceptor
/*    */ {
/*    */   public Object doIntercept(Invocation inv) throws Throwable {
/* 18 */     ValHolder val0 = new ValHolder();
/*    */     
/* 20 */     Tran anno = (Tran)inv.method().getAnnotation(Tran.class);
/* 21 */     TranUtils.execute(anno, () -> val0.value = inv.invoke());
/*    */ 
/*    */ 
/*    */     
/* 25 */     return val0.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\around\TranInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */