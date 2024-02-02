/*    */ package org.noear.solon.schedule;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import org.noear.solon.Utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MethodRunnable
/*    */   implements Runnable
/*    */ {
/*    */   private Object target;
/*    */   private Method method;
/*    */   
/*    */   public MethodRunnable(Object target, Method method) {
/* 18 */     this.target = target;
/* 19 */     this.method = method;
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/*    */     try {
/* 25 */       this.method.invoke(this.target, new Object[0]);
/* 26 */     } catch (Throwable e) {
/* 27 */       e = Utils.throwableUnwrap(e);
/* 28 */       throw new ScheduledException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\schedule\MethodRunnable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */