/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.util.Collection;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ import org.noear.solon.validation.Validator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SizeValidator
/*    */   implements Validator<Size>
/*    */ {
/* 15 */   public static final SizeValidator instance = new SizeValidator();
/*    */ 
/*    */   
/*    */   public String message(Size anno) {
/* 19 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(Size anno) {
/* 24 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(Size anno, Object val0, StringBuilder tmp) {
/* 29 */     if (val0 != null && !(val0 instanceof Collection)) {
/* 30 */       return Result.failure();
/*    */     }
/*    */     
/* 33 */     Collection val = (Collection)val0;
/*    */     
/* 35 */     if (!verify(anno, val)) {
/* 36 */       return Result.failure();
/*    */     }
/* 38 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, Size anno, String name, StringBuilder tmp) {
/* 44 */     return Result.failure();
/*    */   }
/*    */ 
/*    */   
/*    */   private boolean verify(Size anno, Collection val) {
/* 49 */     if (val == null) {
/* 50 */       return true;
/*    */     }
/*    */     
/* 53 */     if (anno.min() > 0 && val.size() < anno.min()) {
/* 54 */       return false;
/*    */     }
/*    */     
/* 57 */     if (anno.max() > 0 && val.size() > anno.max()) {
/* 58 */       return false;
/*    */     }
/*    */     
/* 61 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\SizeValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */