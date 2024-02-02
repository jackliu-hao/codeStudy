/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ import org.noear.solon.validation.Validator;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WhitelistValidator
/*    */   implements Validator<Whitelist>
/*    */ {
/* 13 */   public static final WhitelistValidator instance = new WhitelistValidator();
/*    */   
/*    */   private WhitelistChecker checker = (anno, ctx) -> false;
/*    */   
/*    */   public void setChecker(WhitelistChecker checker) {
/* 18 */     if (checker != null) {
/* 19 */       this.checker = checker;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String message(Whitelist anno) {
/* 25 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(Whitelist anno) {
/* 30 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, Whitelist anno, String name, StringBuilder tmp) {
/* 35 */     if (this.checker.check(anno, ctx)) {
/* 36 */       return Result.succeed();
/*    */     }
/* 38 */     return Result.failure(403);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\WhitelistValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */