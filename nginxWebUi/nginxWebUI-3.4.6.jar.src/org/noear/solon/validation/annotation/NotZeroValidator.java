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
/*    */ 
/*    */ public class NotZeroValidator
/*    */   implements Validator<NotZero>
/*    */ {
/* 15 */   public static final NotZeroValidator instance = new NotZeroValidator();
/*    */ 
/*    */   
/*    */   public String message(NotZero anno) {
/* 19 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(NotZero anno) {
/* 24 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(NotZero anno, Object val0, StringBuilder tmp) {
/* 29 */     if (!(val0 instanceof Number)) {
/* 30 */       return Result.failure();
/*    */     }
/*    */     
/* 33 */     Number val = (Number)val0;
/*    */     
/* 35 */     if (val == null || val.longValue() == 0L) {
/* 36 */       return Result.failure();
/*    */     }
/* 38 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, NotZero anno, String name, StringBuilder tmp) {
/* 44 */     if (name == null) {
/*    */       
/* 46 */       for (String key : anno.value()) {
/* 47 */         String val = ctx.param(key);
/*    */         
/* 49 */         if (!StringUtils.isInteger(val) || Long.parseLong(val) == 0L) {
/* 50 */           tmp.append(',').append(key);
/*    */         }
/*    */       } 
/*    */     } else {
/*    */       
/* 55 */       String val = ctx.param(name);
/*    */       
/* 57 */       if (!StringUtils.isInteger(val) || Long.parseLong(val) == 0L) {
/* 58 */         tmp.append(',').append(name);
/*    */       }
/*    */     } 
/*    */     
/* 62 */     if (tmp.length() > 1) {
/* 63 */       return Result.failure(tmp.substring(1));
/*    */     }
/* 65 */     return Result.succeed();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\NotZeroValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */