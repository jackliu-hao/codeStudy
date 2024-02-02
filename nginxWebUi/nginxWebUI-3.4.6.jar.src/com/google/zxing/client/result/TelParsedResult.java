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
/*    */ public final class TelParsedResult
/*    */   extends ParsedResult
/*    */ {
/*    */   private final String number;
/*    */   private final String telURI;
/*    */   private final String title;
/*    */   
/*    */   public TelParsedResult(String number, String telURI, String title) {
/* 31 */     super(ParsedResultType.TEL);
/* 32 */     this.number = number;
/* 33 */     this.telURI = telURI;
/* 34 */     this.title = title;
/*    */   }
/*    */   
/*    */   public String getNumber() {
/* 38 */     return this.number;
/*    */   }
/*    */   
/*    */   public String getTelURI() {
/* 42 */     return this.telURI;
/*    */   }
/*    */   
/*    */   public String getTitle() {
/* 46 */     return this.title;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDisplayResult() {
/* 51 */     StringBuilder result = new StringBuilder(20);
/* 52 */     maybeAppend(this.number, result);
/* 53 */     maybeAppend(this.title, result);
/* 54 */     return result.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\TelParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */