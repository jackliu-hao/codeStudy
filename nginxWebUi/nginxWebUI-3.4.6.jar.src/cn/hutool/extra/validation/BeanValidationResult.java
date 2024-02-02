/*     */ package cn.hutool.extra.validation;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class BeanValidationResult
/*     */ {
/*     */   private boolean success;
/*  20 */   private List<ErrorMessage> errorMessages = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanValidationResult(boolean success) {
/*  28 */     this.success = success;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSuccess() {
/*  37 */     return this.success;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanValidationResult setSuccess(boolean success) {
/*  47 */     this.success = success;
/*  48 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<ErrorMessage> getErrorMessages() {
/*  57 */     return this.errorMessages;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanValidationResult setErrorMessages(List<ErrorMessage> errorMessages) {
/*  67 */     this.errorMessages = errorMessages;
/*  68 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanValidationResult addErrorMessage(ErrorMessage errorMessage) {
/*  78 */     this.errorMessages.add(errorMessage);
/*  79 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class ErrorMessage
/*     */   {
/*     */     private String propertyName;
/*     */ 
/*     */ 
/*     */     
/*     */     private String message;
/*     */ 
/*     */ 
/*     */     
/*     */     private Object value;
/*     */ 
/*     */ 
/*     */     
/*     */     public String getPropertyName() {
/* 100 */       return this.propertyName;
/*     */     }
/*     */     
/*     */     public void setPropertyName(String propertyName) {
/* 104 */       this.propertyName = propertyName;
/*     */     }
/*     */     
/*     */     public String getMessage() {
/* 108 */       return this.message;
/*     */     }
/*     */     
/*     */     public void setMessage(String message) {
/* 112 */       this.message = message;
/*     */     }
/*     */     
/*     */     public Object getValue() {
/* 116 */       return this.value;
/*     */     }
/*     */     
/*     */     public void setValue(Object value) {
/* 120 */       this.value = value;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 125 */       return "ErrorMessage{propertyName='" + this.propertyName + '\'' + ", message='" + this.message + '\'' + ", value=" + this.value + '}';
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\validation\BeanValidationResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */