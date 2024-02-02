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
/*    */ 
/*    */ final class DecodedChar
/*    */   extends DecodedObject
/*    */ {
/*    */   private final char value;
/*    */   static final char FNC1 = '$';
/*    */   
/*    */   DecodedChar(int newPosition, char value) {
/* 40 */     super(newPosition);
/* 41 */     this.value = value;
/*    */   }
/*    */   
/*    */   char getValue() {
/* 45 */     return this.value;
/*    */   }
/*    */   
/*    */   boolean isFNC1() {
/* 49 */     return (this.value == '$');
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\DecodedChar.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */