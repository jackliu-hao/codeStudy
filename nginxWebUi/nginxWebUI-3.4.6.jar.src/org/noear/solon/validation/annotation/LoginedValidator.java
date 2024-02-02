/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ import org.noear.solon.validation.Validator;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LoginedValidator
/*    */   implements Validator<Logined>
/*    */ {
/* 14 */   public static final LoginedValidator instance = new LoginedValidator();
/*    */   
/* 16 */   private LoginedChecker checker = new LoginedCheckerImp();
/*    */   
/*    */   public void setChecker(LoginedChecker checker) {
/* 19 */     if (checker != null) {
/* 20 */       this.checker = checker;
/*    */     }
/*    */   }
/*    */   
/* 24 */   public static String sessionUserKeyName = "user_id";
/*    */ 
/*    */   
/*    */   public String message(Logined anno) {
/* 28 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(Logined anno) {
/* 33 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, Logined anno, String name, StringBuilder tmp) {
/* 38 */     String userKeyName = anno.value();
/* 39 */     if (Utils.isEmpty(userKeyName)) {
/* 40 */       userKeyName = sessionUserKeyName;
/*    */     }
/*    */     
/* 43 */     if (this.checker.check(anno, ctx, userKeyName)) {
/* 44 */       return Result.succeed();
/*    */     }
/* 46 */     return Result.failure();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\LoginedValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */