/*     */ package cn.hutool.extra.validation;
/*     */ 
/*     */ import jakarta.validation.ConstraintViolation;
/*     */ import jakarta.validation.Validation;
/*     */ import jakarta.validation.Validator;
/*     */ import jakarta.validation.ValidatorFactory;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ValidationUtil
/*     */ {
/*     */   private static final Validator validator;
/*     */   
/*     */   static {
/*  29 */     try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
/*  30 */       validator = factory.getValidator();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Validator getValidator() {
/*  40 */     return validator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Set<ConstraintViolation<T>> validate(T bean, Class<?>... groups) {
/*  52 */     return validator.validate(bean, groups);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Set<ConstraintViolation<T>> validateProperty(T bean, String propertyName, Class<?>... groups) {
/*  65 */     return validator.validateProperty(bean, propertyName, groups);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> BeanValidationResult warpValidate(T bean, Class<?>... groups) {
/*  77 */     return warpBeanValidationResult(validate(bean, groups));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> BeanValidationResult warpValidateProperty(T bean, String propertyName, Class<?>... groups) {
/*  90 */     return warpBeanValidationResult(validateProperty(bean, propertyName, groups));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T> BeanValidationResult warpBeanValidationResult(Set<ConstraintViolation<T>> constraintViolations) {
/* 100 */     BeanValidationResult result = new BeanValidationResult(constraintViolations.isEmpty());
/* 101 */     for (ConstraintViolation<T> constraintViolation : constraintViolations) {
/* 102 */       BeanValidationResult.ErrorMessage errorMessage = new BeanValidationResult.ErrorMessage();
/* 103 */       errorMessage.setPropertyName(constraintViolation.getPropertyPath().toString());
/* 104 */       errorMessage.setMessage(constraintViolation.getMessage());
/* 105 */       errorMessage.setValue(constraintViolation.getInvalidValue());
/* 106 */       result.addErrorMessage(errorMessage);
/*     */     } 
/* 108 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\validation\ValidationUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */