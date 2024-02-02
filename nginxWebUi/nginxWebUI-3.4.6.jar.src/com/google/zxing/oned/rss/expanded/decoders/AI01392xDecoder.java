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
/*    */ 
/*    */ final class AI01392xDecoder
/*    */   extends AI01decoder
/*    */ {
/*    */   private static final int HEADER_SIZE = 8;
/*    */   private static final int LAST_DIGIT_SIZE = 2;
/*    */   
/*    */   AI01392xDecoder(BitArray information) {
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
/* 57 */     buf.append("(392");
/* 58 */     buf.append(lastAIdigit);
/* 59 */     buf.append(')');
/*    */ 
/*    */     
/* 62 */     DecodedInformation decodedInformation = getGeneralDecoder().decodeGeneralPurposeField(50, null);
/* 63 */     buf.append(decodedInformation.getNewString());
/*    */     
/* 65 */     return buf.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\AI01392xDecoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */