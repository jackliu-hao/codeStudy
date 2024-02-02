/*    */ package org.noear.solon.validation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class ValidatorFailureHandlerDefault
/*    */   implements ValidatorFailureHandler
/*    */ {
/*    */   public boolean onFailure(Context ctx, Annotation anno, Result rst, String msg) throws Throwable {
/* 19 */     ctx.setHandled(true);
/*    */     
/* 21 */     if (rst.getCode() > 400 && rst.getCode() < 500) {
/* 22 */       ctx.status(rst.getCode());
/*    */     } else {
/* 24 */       ctx.status(400);
/*    */     } 
/*    */     
/* 27 */     if (!ctx.getRendered()) {
/*    */       
/* 29 */       if (Utils.isEmpty(msg)) {
/* 30 */         if (Utils.isEmpty(rst.getDescription())) {
/*    */ 
/*    */ 
/*    */ 
/*    */           
/* 35 */           msg = (new StringBuilder(100)).append("@").append(anno.annotationType().getSimpleName()).append(" verification failed").toString();
/*    */ 
/*    */         
/*    */         }
/*    */         else {
/*    */ 
/*    */           
/* 42 */           msg = (new StringBuilder(100)).append("@").append(anno.annotationType().getSimpleName()).append(" verification failed: ").append(rst.getDescription()).toString();
/*    */         } 
/*    */       }
/*    */       
/* 46 */       ctx.render(Result.failure(rst.getCode(), msg));
/*    */     } 
/*    */ 
/*    */     
/* 50 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\ValidatorFailureHandlerDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */