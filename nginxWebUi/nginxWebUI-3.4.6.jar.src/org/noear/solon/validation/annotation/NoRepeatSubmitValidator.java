/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.lang.annotation.Annotation;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ import org.noear.solon.validation.Validator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NoRepeatSubmitValidator
/*    */   implements Validator<NoRepeatSubmit>
/*    */ {
/* 16 */   public static final NoRepeatSubmitValidator instance = new NoRepeatSubmitValidator();
/*    */   
/*    */   private NoRepeatSubmitChecker checker;
/*    */   
/*    */   public void setChecker(NoRepeatSubmitChecker checker) {
/* 21 */     if (checker != null) {
/* 22 */       this.checker = checker;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String message(NoRepeatSubmit anno) {
/* 28 */     return anno.message();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(NoRepeatSubmit anno) {
/* 33 */     return anno.groups();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, NoRepeatSubmit anno, String name, StringBuilder tmp) {
/* 38 */     if (this.checker == null) {
/* 39 */       throw new IllegalArgumentException("Missing NoRepeatSubmitChecker Setting");
/*    */     }
/*    */     
/* 42 */     tmp.append(ctx.pathNew()).append("#");
/*    */     
/* 44 */     for (HttpPart part : anno.value()) {
/* 45 */       switch (part) {
/*    */         case body:
/*    */           try {
/* 48 */             tmp.append("body:");
/* 49 */             tmp.append(ctx.bodyNew()).append(";");
/* 50 */           } catch (IOException ex) {
/* 51 */             throw new RuntimeException(ex);
/*    */           } 
/*    */           break;
/*    */         
/*    */         case headers:
/* 56 */           tmp.append("headers:");
/* 57 */           ctx.headerMap().forEach((k, v) -> tmp.append(k).append("=").append(v).append(";"));
/*    */           break;
/*    */ 
/*    */ 
/*    */         
/*    */         default:
/* 63 */           tmp.append("params:");
/* 64 */           ctx.paramMap().forEach((k, v) -> tmp.append(k).append("=").append(v).append(";"));
/*    */           break;
/*    */       } 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     } 
/* 72 */     if (this.checker.check(anno, ctx, Utils.md5(tmp.toString()), anno.seconds())) {
/* 73 */       return Result.succeed();
/*    */     }
/* 75 */     return Result.failure();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\NoRepeatSubmitValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */