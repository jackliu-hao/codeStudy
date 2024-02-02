/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.regex.Pattern;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ import org.noear.solon.validation.Validator;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PatternValidator
/*    */   implements Validator<Pattern>
/*    */ {
/* 17 */   private static final Map<String, Pattern> cached = new ConcurrentHashMap<>();
/*    */   
/* 19 */   public static final PatternValidator instance = new PatternValidator();
/*    */ 
/*    */   
/*    */   public String message(Pattern anno) {
/* 23 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(Pattern anno) {
/* 28 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(Pattern anno, Object val0, StringBuilder tmp) {
/* 33 */     if (val0 != null && !(val0 instanceof String)) {
/* 34 */       return Result.failure();
/*    */     }
/*    */     
/* 37 */     String val = (String)val0;
/*    */     
/* 39 */     if (!verify(anno, val)) {
/* 40 */       return Result.failure();
/*    */     }
/* 42 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, Pattern anno, String name, StringBuilder tmp) {
/* 48 */     String val = ctx.param(name);
/*    */     
/* 50 */     if (!verify(anno, val)) {
/* 51 */       return Result.failure(name);
/*    */     }
/* 53 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean verify(Pattern anno, String val) {
/* 59 */     if (Utils.isEmpty(val)) {
/* 60 */       return true;
/*    */     }
/*    */     
/* 63 */     Pattern pt = cached.get(anno.value());
/*    */     
/* 65 */     if (pt == null) {
/* 66 */       pt = Pattern.compile(anno.value());
/* 67 */       cached.putIfAbsent(anno.value(), pt);
/*    */     } 
/*    */     
/* 70 */     return pt.matcher(val).find();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\PatternValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */