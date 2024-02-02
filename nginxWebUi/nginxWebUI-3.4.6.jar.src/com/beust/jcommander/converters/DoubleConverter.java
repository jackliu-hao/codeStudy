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
/*    */ public class DoubleConverter
/*    */   extends BaseConverter<Double>
/*    */ {
/*    */   public DoubleConverter(String optionName) {
/* 31 */     super(optionName);
/*    */   }
/*    */   
/*    */   public Double convert(String value) {
/*    */     try {
/* 36 */       return Double.valueOf(Double.parseDouble(value));
/* 37 */     } catch (NumberFormatException ex) {
/* 38 */       throw new ParameterException(getErrorString(value, "a double"));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\converters\DoubleConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */