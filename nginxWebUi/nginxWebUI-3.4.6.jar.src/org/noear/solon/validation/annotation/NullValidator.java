/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ import org.noear.solon.validation.Validator;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NullValidator
/*    */   implements Validator<Null>
/*    */ {
/* 13 */   public static final NullValidator instance = new NullValidator();
/*    */ 
/*    */   
/*    */   public String message(Null anno) {
/* 17 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(Null anno) {
/* 22 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(Null anno, Object val, StringBuilder tmp) {
/* 27 */     if (val != null) {
/* 28 */       return Result.failure();
/*    */     }
/* 30 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, Null anno, String name, StringBuilder tmp) {
/* 36 */     if (name == null) {
/*    */       
/* 38 */       for (String key : anno.value()) {
/* 39 */         if (ctx.param(key) != null) {
/* 40 */           tmp.append(',').append(key);
/*    */         }
/*    */       }
/*    */     
/*    */     }
/* 45 */     else if (ctx.param(name) != null) {
/* 46 */       tmp.append(',').append(name);
/*    */     } 
/*    */ 
/*    */     
/* 50 */     if (tmp.length() > 1) {
/* 51 */       return Result.failure(tmp.substring(1));
/*    */     }
/* 53 */     return Result.succeed();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\NullValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */