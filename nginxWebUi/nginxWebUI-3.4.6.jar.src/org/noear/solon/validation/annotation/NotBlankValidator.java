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
/*    */ public class NotBlankValidator
/*    */   implements Validator<NotBlank>
/*    */ {
/* 14 */   public static final NotBlankValidator instance = new NotBlankValidator();
/*    */ 
/*    */   
/*    */   public String message(NotBlank anno) {
/* 18 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(NotBlank anno) {
/* 23 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(NotBlank anno, Object val0, StringBuilder tmp) {
/* 28 */     if (!(val0 instanceof String)) {
/* 29 */       return Result.failure();
/*    */     }
/*    */     
/* 32 */     String val = (String)val0;
/*    */     
/* 34 */     if (Utils.isBlank(val)) {
/* 35 */       return Result.failure();
/*    */     }
/* 37 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, NotBlank anno, String name, StringBuilder tmp) {
/* 43 */     if (name == null) {
/*    */       
/* 45 */       for (String key : anno.value()) {
/* 46 */         if (Utils.isBlank(ctx.param(key))) {
/* 47 */           tmp.append(',').append(key);
/*    */         }
/*    */       }
/*    */     
/*    */     }
/* 52 */     else if (Utils.isBlank(ctx.param(name))) {
/* 53 */       tmp.append(',').append(name);
/*    */     } 
/*    */ 
/*    */     
/* 57 */     if (tmp.length() > 1) {
/* 58 */       return Result.failure(tmp.substring(1));
/*    */     }
/* 60 */     return Result.succeed();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\NotBlankValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */