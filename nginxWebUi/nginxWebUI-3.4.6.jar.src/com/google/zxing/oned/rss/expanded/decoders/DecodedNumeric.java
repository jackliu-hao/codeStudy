/*    */ package com.google.zxing.oned.rss.expanded.decoders;
/*    */ 
/*    */ import com.google.zxing.FormatException;
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
/*    */ final class DecodedNumeric
/*    */   extends DecodedObject
/*    */ {
/*    */   private final int firstDigit;
/*    */   private final int secondDigit;
/*    */   static final int FNC1 = 10;
/*    */   
/*    */   DecodedNumeric(int newPosition, int firstDigit, int secondDigit) throws FormatException {
/* 43 */     super(newPosition);
/*    */     
/* 45 */     if (firstDigit < 0 || firstDigit > 10 || secondDigit < 0 || secondDigit > 10) {
/* 46 */       throw FormatException.getFormatInstance();
/*    */     }
/*    */     
/* 49 */     this.firstDigit = firstDigit;
/* 50 */     this.secondDigit = secondDigit;
/*    */   }
/*    */   
/*    */   int getFirstDigit() {
/* 54 */     return this.firstDigit;
/*    */   }
/*    */   
/*    */   int getSecondDigit() {
/* 58 */     return this.secondDigit;
/*    */   }
/*    */   
/*    */   int getValue() {
/* 62 */     return this.firstDigit * 10 + this.secondDigit;
/*    */   }
/*    */   
/*    */   boolean isFirstDigitFNC1() {
/* 66 */     return (this.firstDigit == 10);
/*    */   }
/*    */   
/*    */   boolean isSecondDigitFNC1() {
/* 70 */     return (this.secondDigit == 10);
/*    */   }
/*    */   
/*    */   boolean isAnyFNC1() {
/* 74 */     return (this.firstDigit == 10 || this.secondDigit == 10);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\DecodedNumeric.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */