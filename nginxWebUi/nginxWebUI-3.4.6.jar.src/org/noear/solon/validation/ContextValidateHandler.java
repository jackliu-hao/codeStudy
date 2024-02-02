/*    */ package org.noear.solon.validation;
/*    */ 
/*    */ import org.noear.solon.core.handle.Action;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Handler;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ContextValidateHandler
/*    */   implements Handler
/*    */ {
/*    */   public void handle(Context ctx) throws Throwable {
/* 17 */     if (ctx.getHandled()) {
/*    */       return;
/*    */     }
/*    */     
/* 21 */     Action a = ctx.action();
/*    */     
/* 23 */     if (a != null)
/* 24 */       ValidatorManager.validateOfContext(ctx, a); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\ContextValidateHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */