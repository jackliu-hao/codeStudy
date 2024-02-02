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
/*    */ 
/*    */ 
/*    */ public class LengthValidator
/*    */   implements Validator<Length>
/*    */ {
/* 16 */   public static final LengthValidator instance = new LengthValidator();
/*    */ 
/*    */   
/*    */   public String message(Length anno) {
/* 20 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(Length anno) {
/* 25 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(Length anno, Object val0, StringBuilder tmp) {
/* 30 */     if (val0 != null && !(val0 instanceof String)) {
/* 31 */       return Result.failure();
/*    */     }
/*    */     
/* 34 */     String val = (String)val0;
/*    */     
/* 36 */     if (!verify(anno, val)) {
/* 37 */       return Result.failure();
/*    */     }
/* 39 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, Length anno, String name, StringBuilder tmp) {
/* 45 */     String val = ctx.param(name);
/*    */     
/* 47 */     if (!verify(anno, val)) {
/* 48 */       return Result.failure(name);
/*    */     }
/* 50 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean verify(Length anno, String val) {
/* 56 */     if (val == null) {
/* 57 */       return true;
/*    */     }
/*    */     
/* 60 */     if (anno.min() > 0 && val.length() < anno.min()) {
/* 61 */       return false;
/*    */     }
/*    */     
/* 64 */     if (anno.max() > 0 && val.length() > anno.max()) {
/* 65 */       return false;
/*    */     }
/*    */     
/* 68 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\LengthValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */