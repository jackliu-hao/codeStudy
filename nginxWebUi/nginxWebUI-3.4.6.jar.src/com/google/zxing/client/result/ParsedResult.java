/*    */ package com.google.zxing.client.result;
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
/*    */ public abstract class ParsedResult
/*    */ {
/*    */   private final ParsedResultType type;
/*    */   
/*    */   protected ParsedResult(ParsedResultType type) {
/* 35 */     this.type = type;
/*    */   }
/*    */   
/*    */   public final ParsedResultType getType() {
/* 39 */     return this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   public abstract String getDisplayResult();
/*    */   
/*    */   public final String toString() {
/* 46 */     return getDisplayResult();
/*    */   }
/*    */   
/*    */   public static void maybeAppend(String value, StringBuilder result) {
/* 50 */     if (value != null && !value.isEmpty()) {
/*    */       
/* 52 */       if (result.length() > 0) {
/* 53 */         result.append('\n');
/*    */       }
/* 55 */       result.append(value);
/*    */     } 
/*    */   }
/*    */   
/*    */   public static void maybeAppend(String[] values, StringBuilder result) {
/* 60 */     if (values != null) {
/* 61 */       String[] arrayOfString; int i; byte b; for (i = (arrayOfString = values).length, b = 0; b < i; b++)
/* 62 */         maybeAppend(arrayOfString[b], result); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\ParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */