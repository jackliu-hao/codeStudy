/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ import org.noear.solon.validation.Validator;
/*    */ import org.noear.solon.validation.util.StringUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MaxValidator
/*    */   implements Validator<Max>
/*    */ {
/* 14 */   public static final MaxValidator instance = new MaxValidator();
/*    */ 
/*    */   
/*    */   public String message(Max anno) {
/* 18 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(Max anno) {
/* 23 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(Max anno, Object val0, StringBuilder tmp) {
/* 28 */     if (!(val0 instanceof Number)) {
/* 29 */       return Result.failure();
/*    */     }
/*    */     
/* 32 */     Number val = (Number)val0;
/*    */     
/* 34 */     if (val == null || val.longValue() > anno.value()) {
/* 35 */       return Result.failure();
/*    */     }
/* 37 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, Max anno, String name, StringBuilder tmp) {
/* 43 */     String val = ctx.param(name);
/*    */     
/* 45 */     if (!StringUtils.isInteger(val) || Long.parseLong(val) > anno.value()) {
/* 46 */       tmp.append(',').append(name);
/*    */     }
/*    */     
/* 49 */     if (tmp.length() > 1) {
/* 50 */       return Result.failure(tmp.substring(1));
/*    */     }
/* 52 */     return Result.succeed();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\MaxValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */