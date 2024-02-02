/*    */ package com.mysql.cj.util;
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
/*    */ public class LogUtils
/*    */ {
/*    */   public static final String CALLER_INFORMATION_NOT_AVAILABLE = "Caller information not available";
/* 38 */   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
/*    */   
/* 40 */   private static final int LINE_SEPARATOR_LENGTH = LINE_SEPARATOR.length();
/*    */   
/*    */   public static String findCallingClassAndMethod(Throwable t) {
/* 43 */     String stackTraceAsString = Util.stackTraceToString(t);
/*    */     
/* 45 */     String callingClassAndMethod = "Caller information not available";
/*    */     
/* 47 */     int endInternalMethods = Math.max(Math.max(stackTraceAsString.lastIndexOf("com.mysql.cj"), stackTraceAsString.lastIndexOf("com.mysql.cj.core")), stackTraceAsString
/* 48 */         .lastIndexOf("com.mysql.cj.jdbc"));
/*    */     
/* 50 */     if (endInternalMethods != -1) {
/* 51 */       int endOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endInternalMethods);
/*    */       
/* 53 */       if (endOfLine != -1) {
/* 54 */         int nextEndOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endOfLine + LINE_SEPARATOR_LENGTH);
/*    */         
/* 56 */         callingClassAndMethod = (nextEndOfLine != -1) ? stackTraceAsString.substring(endOfLine + LINE_SEPARATOR_LENGTH, nextEndOfLine) : stackTraceAsString.substring(endOfLine + LINE_SEPARATOR_LENGTH);
/*    */       } 
/*    */     } 
/*    */     
/* 60 */     if (!callingClassAndMethod.startsWith("\tat ") && !callingClassAndMethod.startsWith("at ")) {
/* 61 */       return "at " + callingClassAndMethod;
/*    */     }
/*    */     
/* 64 */     return callingClassAndMethod;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\LogUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */