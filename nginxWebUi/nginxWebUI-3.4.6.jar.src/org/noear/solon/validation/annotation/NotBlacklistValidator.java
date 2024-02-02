/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ import org.noear.solon.validation.Validator;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NotBlacklistValidator
/*    */   implements Validator<NotBlacklist>
/*    */ {
/* 13 */   public static final NotBlacklistValidator instance = new NotBlacklistValidator();
/*    */   private NotBlacklistChecker checker = (anno, ctx) -> false;
/*    */   
/*    */   public void setChecker(NotBlacklistChecker checker) {
/* 17 */     if (checker != null) {
/* 18 */       this.checker = checker;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String message(NotBlacklist anno) {
/* 24 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(NotBlacklist anno) {
/* 29 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, NotBlacklist anno, String name, StringBuilder tmp) {
/* 34 */     if (this.checker.check(anno, ctx)) {
/* 35 */       return Result.succeed();
/*    */     }
/* 37 */     return Result.failure(403);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\NotBlacklistValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */