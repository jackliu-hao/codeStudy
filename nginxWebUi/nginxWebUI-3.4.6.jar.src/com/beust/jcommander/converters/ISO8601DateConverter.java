/*    */ package com.beust.jcommander.converters;
/*    */ 
/*    */ import com.beust.jcommander.ParameterException;
/*    */ import java.text.ParseException;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
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
/*    */ public class ISO8601DateConverter
/*    */   extends BaseConverter<Date>
/*    */ {
/* 35 */   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
/*    */   
/*    */   public ISO8601DateConverter(String optionName) {
/* 38 */     super(optionName);
/*    */   }
/*    */   
/*    */   public Date convert(String value) {
/*    */     try {
/* 43 */       return DATE_FORMAT.parse(value);
/* 44 */     } catch (ParseException pe) {
/* 45 */       throw new ParameterException(getErrorString(value, String.format("an ISO-8601 formatted date (%s)", new Object[] { DATE_FORMAT.toPattern() })));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\converters\ISO8601DateConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */