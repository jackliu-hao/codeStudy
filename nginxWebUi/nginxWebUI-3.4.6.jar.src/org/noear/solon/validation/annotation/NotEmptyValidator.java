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
/*    */ public class NotEmptyValidator
/*    */   implements Validator<NotEmpty>
/*    */ {
/* 14 */   public static final NotEmptyValidator instance = new NotEmptyValidator();
/*    */ 
/*    */   
/*    */   public String message(NotEmpty anno) {
/* 18 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(NotEmpty anno) {
/* 23 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(NotEmpty anno, Object val0, StringBuilder tmp) {
/* 28 */     if (!(val0 instanceof String)) {
/* 29 */       return Result.failure();
/*    */     }
/*    */     
/* 32 */     String val = (String)val0;
/*    */     
/* 34 */     if (Utils.isEmpty(val)) {
/* 35 */       return Result.failure();
/*    */     }
/* 37 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, NotEmpty anno, String name, StringBuilder tmp) {
/* 43 */     if (name == null) {
/*    */       
/* 45 */       for (String key : anno.value()) {
/* 46 */         if (Utils.isEmpty(ctx.param(key))) {
/* 47 */           tmp.append(',').append(key);
/*    */         }
/*    */       }
/*    */     
/*    */     }
/* 52 */     else if (Utils.isEmpty(ctx.param(name))) {
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\NotEmptyValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */