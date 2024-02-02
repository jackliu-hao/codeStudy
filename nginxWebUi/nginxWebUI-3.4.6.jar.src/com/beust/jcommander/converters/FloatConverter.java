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
/*    */ public class FloatConverter
/*    */   extends BaseConverter<Float>
/*    */ {
/*    */   public FloatConverter(String optionName) {
/* 31 */     super(optionName);
/*    */   }
/*    */   
/*    */   public Float convert(String value) {
/*    */     try {
/* 36 */       return Float.valueOf(Float.parseFloat(value));
/* 37 */     } catch (NumberFormatException ex) {
/* 38 */       throw new ParameterException(getErrorString(value, "a float"));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\converters\FloatConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */