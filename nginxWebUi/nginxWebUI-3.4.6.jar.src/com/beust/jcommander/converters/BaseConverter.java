/*    */ package com.beust.jcommander.converters;
/*    */ 
/*    */ import com.beust.jcommander.IStringConverter;
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
/*    */ public abstract class BaseConverter<T>
/*    */   implements IStringConverter<T>
/*    */ {
/*    */   private String m_optionName;
/*    */   
/*    */   public BaseConverter(String optionName) {
/* 33 */     this.m_optionName = optionName;
/*    */   }
/*    */   
/*    */   public String getOptionName() {
/* 37 */     return this.m_optionName;
/*    */   }
/*    */   
/*    */   protected String getErrorString(String value, String to) {
/* 41 */     return "\"" + getOptionName() + "\": couldn't convert \"" + value + "\" to " + to;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\converters\BaseConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */