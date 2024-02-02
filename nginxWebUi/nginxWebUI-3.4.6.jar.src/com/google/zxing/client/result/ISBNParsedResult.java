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
/*    */ public final class ISBNParsedResult
/*    */   extends ParsedResult
/*    */ {
/*    */   private final String isbn;
/*    */   
/*    */   ISBNParsedResult(String isbn) {
/* 29 */     super(ParsedResultType.ISBN);
/* 30 */     this.isbn = isbn;
/*    */   }
/*    */   
/*    */   public String getISBN() {
/* 34 */     return this.isbn;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDisplayResult() {
/* 39 */     return this.isbn;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\ISBNParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */