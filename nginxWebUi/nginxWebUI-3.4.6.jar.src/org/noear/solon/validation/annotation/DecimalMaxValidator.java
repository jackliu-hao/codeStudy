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
/*    */ public class DecimalMaxValidator
/*    */   implements Validator<DecimalMax>
/*    */ {
/* 14 */   public static final DecimalMaxValidator instance = new DecimalMaxValidator();
/*    */ 
/*    */   
/*    */   public String message(DecimalMax anno) {
/* 18 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(DecimalMax anno) {
/* 23 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(DecimalMax anno, Object val0, StringBuilder tmp) {
/* 28 */     if (!(val0 instanceof Double)) {
/* 29 */       return Result.failure();
/*    */     }
/*    */     
/* 32 */     Double val = (Double)val0;
/*    */     
/* 34 */     if (val == null || val.doubleValue() > anno.value()) {
/* 35 */       return Result.failure();
/*    */     }
/* 37 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, DecimalMax anno, String name, StringBuilder tmp) {
/* 43 */     String val = ctx.param(name);
/*    */     
/* 45 */     if (!StringUtils.isNumber(val) || Double.parseDouble(val) > anno.value()) {
/* 46 */       return Result.failure(name);
/*    */     }
/* 48 */     return Result.succeed();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\DecimalMaxValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */