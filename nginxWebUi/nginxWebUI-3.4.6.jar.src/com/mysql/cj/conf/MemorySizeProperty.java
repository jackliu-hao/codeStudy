/*    */ package com.mysql.cj.conf;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*    */ import java.util.Properties;
/*    */ import javax.naming.Reference;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MemorySizeProperty
/*    */   extends IntegerProperty
/*    */ {
/*    */   private static final long serialVersionUID = 4200558564320133284L;
/*    */   private String initialValueAsString;
/*    */   protected String valueAsString;
/*    */   
/*    */   protected MemorySizeProperty(PropertyDefinition<Integer> propertyDefinition) {
/* 47 */     super(propertyDefinition);
/* 48 */     this.valueAsString = ((Integer)propertyDefinition.getDefaultValue()).toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public void initializeFrom(Properties extractFrom, ExceptionInterceptor exceptionInterceptor) {
/* 53 */     super.initializeFrom(extractFrom, exceptionInterceptor);
/* 54 */     this.initialValueAsString = this.valueAsString;
/*    */   }
/*    */ 
/*    */   
/*    */   public void initializeFrom(Reference ref, ExceptionInterceptor exceptionInterceptor) {
/* 59 */     super.initializeFrom(ref, exceptionInterceptor);
/* 60 */     this.initialValueAsString = this.valueAsString;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getStringValue() {
/* 65 */     return this.valueAsString;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setValueInternal(Integer value, String valueAsString, ExceptionInterceptor exceptionInterceptor) {
/* 70 */     super.setValueInternal(value, valueAsString, exceptionInterceptor);
/* 71 */     this.valueAsString = (valueAsString == null) ? String.valueOf(value.intValue()) : valueAsString;
/*    */   }
/*    */ 
/*    */   
/*    */   public void resetValue() {
/* 76 */     this.value = this.initialValue;
/* 77 */     this.valueAsString = this.initialValueAsString;
/* 78 */     invokeListeners();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\MemorySizeProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */