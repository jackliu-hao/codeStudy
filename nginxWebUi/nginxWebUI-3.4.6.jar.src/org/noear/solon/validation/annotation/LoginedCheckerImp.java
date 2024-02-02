/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import org.noear.solon.core.handle.Context;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LoginedCheckerImp
/*    */   implements LoginedChecker
/*    */ {
/*    */   public boolean check(Logined anno, Context ctx, String userKeyName) {
/* 13 */     Object userKey = ctx.session(userKeyName);
/*    */     
/* 15 */     if (userKey == null) {
/* 16 */       return false;
/*    */     }
/*    */     
/* 19 */     if (userKey instanceof Number && (
/* 20 */       (Number)userKey).longValue() < 1L) {
/* 21 */       return false;
/*    */     }
/*    */ 
/*    */     
/* 25 */     if (userKey instanceof String && (
/* 26 */       (String)userKey).length() < 1) {
/* 27 */       return false;
/*    */     }
/*    */ 
/*    */     
/* 31 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\LoginedCheckerImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */