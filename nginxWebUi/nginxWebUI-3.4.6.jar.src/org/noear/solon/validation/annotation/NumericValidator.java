/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ import org.noear.solon.validation.Validator;
/*    */ import org.noear.solon.validation.util.StringUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NumericValidator
/*    */   implements Validator<Numeric>
/*    */ {
/* 15 */   public static final NumericValidator instance = new NumericValidator();
/*    */ 
/*    */   
/*    */   public String message(Numeric anno) {
/* 19 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(Numeric anno) {
/* 24 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(Numeric anno, Object val0, StringBuilder tmp) {
/* 29 */     if (val0 != null && !(val0 instanceof String)) {
/* 30 */       return Result.failure();
/*    */     }
/*    */     
/* 33 */     String val = (String)val0;
/*    */     
/* 35 */     if (!verify(anno, val)) {
/* 36 */       return Result.failure();
/*    */     }
/* 38 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, Numeric anno, String name, StringBuilder tmp) {
/* 44 */     if (name == null) {
/*    */       
/* 46 */       for (String key : anno.value()) {
/* 47 */         String val = ctx.param(key);
/*    */         
/* 49 */         if (!verify(anno, val)) {
/* 50 */           tmp.append(',').append(key);
/*    */         }
/*    */       } 
/*    */     } else {
/*    */       
/* 55 */       String val = ctx.param(name);
/*    */       
/* 57 */       if (!verify(anno, val)) {
/* 58 */         tmp.append(',').append(name);
/*    */       }
/*    */     } 
/*    */     
/* 62 */     if (tmp.length() > 1) {
/* 63 */       return Result.failure(tmp.substring(1));
/*    */     }
/* 65 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean verify(Numeric anno, String val) {
/* 71 */     if (Utils.isEmpty(val)) {
/* 72 */       return true;
/*    */     }
/*    */     
/* 75 */     return StringUtils.isNumber(val);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\NumericValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */