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
/*    */ public class EmailValidator
/*    */   implements Validator<Email>
/*    */ {
/* 17 */   private static final Map<String, Pattern> cached = new ConcurrentHashMap<>();
/*    */   
/* 19 */   public static final EmailValidator instance = new EmailValidator();
/*    */   
/*    */   public EmailValidator() {
/* 22 */     cached.putIfAbsent("", Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"));
/*    */   }
/*    */ 
/*    */   
/*    */   public String message(Email anno) {
/* 27 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(Email anno) {
/* 32 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(Email anno, Object val0, StringBuilder tmp) {
/* 37 */     if (val0 != null && !(val0 instanceof String)) {
/* 38 */       return Result.failure();
/*    */     }
/*    */     
/* 41 */     String val = (String)val0;
/*    */     
/* 43 */     if (!verify(anno, val)) {
/* 44 */       return Result.failure();
/*    */     }
/* 46 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, Email anno, String name, StringBuilder tmp) {
/* 52 */     String val = ctx.param(name);
/*    */     
/* 54 */     if (!verify(anno, val)) {
/* 55 */       return Result.failure(name);
/*    */     }
/* 57 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean verify(Email anno, String val) {
/* 63 */     if (Utils.isEmpty(val)) {
/* 64 */       return true;
/*    */     }
/*    */     
/* 67 */     Pattern pt = cached.get(anno.value());
/*    */     
/* 69 */     if (pt == null) {
/* 70 */       if (!anno.value().contains("@")) {
/* 71 */         throw new IllegalArgumentException("@Email value must have an @ sign");
/*    */       }
/*    */       
/* 74 */       pt = Pattern.compile(anno.value());
/* 75 */       cached.putIfAbsent(anno.value(), pt);
/*    */     } 
/*    */     
/* 78 */     return pt.matcher(val).find();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\EmailValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */