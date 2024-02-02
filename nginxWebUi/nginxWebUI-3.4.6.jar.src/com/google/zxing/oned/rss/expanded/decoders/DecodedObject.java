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
/*    */ abstract class DecodedObject
/*    */ {
/*    */   private final int newPosition;
/*    */   
/*    */   DecodedObject(int newPosition) {
/* 37 */     this.newPosition = newPosition;
/*    */   }
/*    */   
/*    */   final int getNewPosition() {
/* 41 */     return this.newPosition;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\DecodedObject.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */