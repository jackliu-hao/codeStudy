/*    */ package com.google.zxing.oned.rss.expanded.decoders;
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
/*    */ final class DecodedInformation
/*    */   extends DecodedObject
/*    */ {
/*    */   private final String newString;
/*    */   private final int remainingValue;
/*    */   private final boolean remaining;
/*    */   
/*    */   DecodedInformation(int newPosition, String newString) {
/* 40 */     super(newPosition);
/* 41 */     this.newString = newString;
/* 42 */     this.remaining = false;
/* 43 */     this.remainingValue = 0;
/*    */   }
/*    */   
/*    */   DecodedInformation(int newPosition, String newString, int remainingValue) {
/* 47 */     super(newPosition);
/* 48 */     this.remaining = true;
/* 49 */     this.remainingValue = remainingValue;
/* 50 */     this.newString = newString;
/*    */   }
/*    */   
/*    */   String getNewString() {
/* 54 */     return this.newString;
/*    */   }
/*    */   
/*    */   boolean isRemaining() {
/* 58 */     return this.remaining;
/*    */   }
/*    */   
/*    */   int getRemainingValue() {
/* 62 */     return this.remainingValue;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\DecodedInformation.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */