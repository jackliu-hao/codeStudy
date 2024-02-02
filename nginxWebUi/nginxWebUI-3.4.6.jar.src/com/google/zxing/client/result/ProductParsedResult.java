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
/*    */ public final class ProductParsedResult
/*    */   extends ParsedResult
/*    */ {
/*    */   private final String productID;
/*    */   private final String normalizedProductID;
/*    */   
/*    */   ProductParsedResult(String productID) {
/* 30 */     this(productID, productID);
/*    */   }
/*    */   
/*    */   ProductParsedResult(String productID, String normalizedProductID) {
/* 34 */     super(ParsedResultType.PRODUCT);
/* 35 */     this.productID = productID;
/* 36 */     this.normalizedProductID = normalizedProductID;
/*    */   }
/*    */   
/*    */   public String getProductID() {
/* 40 */     return this.productID;
/*    */   }
/*    */   
/*    */   public String getNormalizedProductID() {
/* 44 */     return this.normalizedProductID;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDisplayResult() {
/* 49 */     return this.productID;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\ProductParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */