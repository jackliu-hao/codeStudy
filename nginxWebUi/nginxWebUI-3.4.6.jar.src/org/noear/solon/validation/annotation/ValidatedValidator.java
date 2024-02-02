/*    */ package org.noear.solon.validation.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import org.noear.solon.core.Aop;
/*    */ import org.noear.solon.core.BeanWrap;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ import org.noear.solon.validation.BeanValidator;
/*    */ import org.noear.solon.validation.BeanValidatorDefault;
/*    */ import org.noear.solon.validation.Validator;
/*    */ 
/*    */ public class ValidatedValidator
/*    */   implements Validator<Validated>
/*    */ {
/* 15 */   public static final ValidatedValidator instance = new ValidatedValidator();
/*    */   
/*    */   private BeanValidator validator;
/*    */   
/*    */   public ValidatedValidator() {
/* 20 */     this.validator = (BeanValidator)new BeanValidatorDefault();
/*    */     
/* 22 */     Aop.getAsyn(BeanValidator.class, bw -> this.validator = (BeanValidator)bw.get());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Class<?>[] groups(Validated anno) {
/* 29 */     return anno.value();
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfValue(Validated anno, Object val, StringBuilder tmp) {
/* 34 */     return this.validator.validate(val, anno.value());
/*    */   }
/*    */ 
/*    */   
/*    */   public Result validateOfContext(Context ctx, Validated anno, String name, StringBuilder tmp) {
/* 39 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\ValidatedValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */