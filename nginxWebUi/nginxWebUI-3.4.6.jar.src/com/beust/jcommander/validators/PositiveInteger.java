/*    */ package com.beust.jcommander.validators;
/*    */ 
/*    */ import com.beust.jcommander.IParameterValidator;
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
/*    */ 
/*    */ public class PositiveInteger
/*    */   implements IParameterValidator
/*    */ {
/*    */   public void validate(String name, String value) throws ParameterException {
/* 33 */     int n = Integer.parseInt(value);
/* 34 */     if (n < 0)
/* 35 */       throw new ParameterException("Parameter " + name + " should be positive (found " + value + ")"); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\validators\PositiveInteger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */