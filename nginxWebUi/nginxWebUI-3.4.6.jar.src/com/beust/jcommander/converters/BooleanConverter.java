/*    */ package com.beust.jcommander.converters;
/*    */ 
/*    */ import com.beust.jcommander.ParameterException;
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
/*    */ public class BooleanConverter
/*    */   extends BaseConverter<Boolean>
/*    */ {
/*    */   public BooleanConverter(String optionName) {
/* 31 */     super(optionName);
/*    */   }
/*    */   
/*    */   public Boolean convert(String value) {
/* 35 */     if ("false".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
/* 36 */       return Boolean.valueOf(Boolean.parseBoolean(value));
/*    */     }
/* 38 */     throw new ParameterException(getErrorString(value, "a boolean"));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\converters\BooleanConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */