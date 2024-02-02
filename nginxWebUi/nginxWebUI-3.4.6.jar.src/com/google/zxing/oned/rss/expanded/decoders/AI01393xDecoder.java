/*    */ package com.google.zxing.oned.rss.expanded.decoders;
/*    */ 
/*    */ import com.google.zxing.FormatException;
/*    */ import com.google.zxing.NotFoundException;
/*    */ import com.google.zxing.common.BitArray;
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
/*    */ final class AI01393xDecoder
/*    */   extends AI01decoder
/*    */ {
/*    */   private static final int HEADER_SIZE = 8;
/*    */   private static final int LAST_DIGIT_SIZE = 2;
/*    */   private static final int FIRST_THREE_DIGITS_SIZE = 10;
/*    */   
/*    */   AI01393xDecoder(BitArray information) {
/* 42 */     super(information);
/*    */   }
/*    */ 
/*    */   
/*    */   public String parseInformation() throws NotFoundException, FormatException {
/* 47 */     if (getInformation().getSize() < 48) {
/* 48 */       throw NotFoundException.getNotFoundInstance();
/*    */     }
/*    */     
/* 51 */     StringBuilder buf = new StringBuilder();
/*    */     
/* 53 */     encodeCompressedGtin(buf, 8);
/*    */ 
/*    */     
/* 56 */     int lastAIdigit = getGeneralDecoder().extractNumericValueFromBitArray(48, 2);
/*    */     
/* 58 */     buf.append("(393");
/* 59 */     buf.append(lastAIdigit);
/* 60 */     buf.append(')');
/*    */     
/*    */     int firstThreeDigits;
/*    */     
/* 64 */     if ((firstThreeDigits = getGeneralDecoder().extractNumericValueFromBitArray(50, 10)) / 100 == 0) {
/* 65 */       buf.append('0');
/*    */     }
/* 67 */     if (firstThreeDigits / 10 == 0) {
/* 68 */       buf.append('0');
/*    */     }
/* 70 */     buf.append(firstThreeDigits);
/*    */ 
/*    */     
/* 73 */     DecodedInformation generalInformation = getGeneralDecoder().decodeGeneralPurposeField(60, null);
/* 74 */     buf.append(generalInformation.getNewString());
/*    */     
/* 76 */     return buf.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\AI01393xDecoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */