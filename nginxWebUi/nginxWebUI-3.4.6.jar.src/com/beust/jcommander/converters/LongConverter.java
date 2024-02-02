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
/*    */ public class LongConverter
/*    */   extends BaseConverter<Long>
/*    */ {
/*    */   public LongConverter(String optionName) {
/* 31 */     super(optionName);
/*    */   }
/*    */   
/*    */   public Long convert(String value) {
/*    */     try {
/* 36 */       return Long.valueOf(Long.parseLong(value));
/* 37 */     } catch (NumberFormatException ex) {
/* 38 */       throw new ParameterException(getErrorString(value, "a long"));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\converters\LongConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */