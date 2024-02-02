/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ import org.noear.solon.validation.Validator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NotNullValidator
/*    */   implements Validator<NotNull>
/*    */ {
/* 14 */   public static final NotNullValidator instance = new NotNullValidator();
/*    */ 
/*    */   
/*    */   public String message(NotNull anno) {
/* 18 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(NotNull anno) {
/* 23 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(NotNull anno, Object val, StringBuilder tmp) {
/* 28 */     if (val == null) {
/* 29 */       return Result.failure();
/*    */     }
/* 31 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, NotNull anno, String name, StringBuilder tmp) {
/* 37 */     if (name == null) {
/*    */       
/* 39 */       for (String key : anno.value()) {
/* 40 */         if (ctx.param(key) == null) {
/* 41 */           tmp.append(',').append(key);
/*    */         }
/*    */       }
/*    */     
/*    */     }
/* 46 */     else if (ctx.param(name) == null) {
/* 47 */       tmp.append(',').append(name);
/*    */     } 
/*    */ 
/*    */     
/* 51 */     if (tmp.length() > 1) {
/* 52 */       return Result.failure(tmp.substring(1));
/*    */     }
/* 54 */     return Result.succeed();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\NotNullValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */