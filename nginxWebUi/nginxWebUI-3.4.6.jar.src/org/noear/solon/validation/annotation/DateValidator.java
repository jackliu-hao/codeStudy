/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ import org.noear.solon.validation.Validator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DateValidator
/*    */   implements Validator<Date>
/*    */ {
/* 16 */   public static final DateValidator instance = new DateValidator();
/*    */ 
/*    */ 
/*    */   
/*    */   public String message(Date anno) {
/* 21 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(Date anno) {
/* 26 */     return anno.groups();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfValue(Date anno, Object val0, StringBuilder tmp) {
/* 34 */     if (val0 != null && !(val0 instanceof String)) {
/* 35 */       return Result.failure();
/*    */     }
/*    */     
/* 38 */     String val = (String)val0;
/*    */     
/* 40 */     if (!verify(anno, val)) {
/* 41 */       return Result.failure();
/*    */     }
/* 43 */     return Result.succeed();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, Date anno, String name, StringBuilder tmp) {
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
/*    */   private boolean verify(Date anno, String val) {
/* 63 */     if (Utils.isEmpty(val)) {
/* 64 */       return true;
/*    */     }
/*    */     
/*    */     try {
/* 68 */       if (Utils.isEmpty(anno.value())) {
/* 69 */         DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(val);
/*    */       } else {
/* 71 */         DateTimeFormatter.ofPattern(anno.value()).parse(val);
/*    */       } 
/*    */       
/* 74 */       return true;
/* 75 */     } catch (Exception ex) {
/* 76 */       return false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\DateValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */