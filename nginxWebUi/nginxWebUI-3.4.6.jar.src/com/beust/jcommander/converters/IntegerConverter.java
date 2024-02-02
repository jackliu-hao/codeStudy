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
/*    */ public class IntegerConverter
/*    */   extends BaseConverter<Integer>
/*    */ {
/*    */   public IntegerConverter(String optionName) {
/* 31 */     super(optionName);
/*    */   }
/*    */   
/*    */   public Integer convert(String value) {
/*    */     try {
/* 36 */       return Integer.valueOf(Integer.parseInt(value));
/* 37 */     } catch (NumberFormatException ex) {
/* 38 */       throw new ParameterException(getErrorString(value, "an integer"));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\converters\IntegerConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */