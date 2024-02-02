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
/*    */ 
/*    */ final class AI01AndOtherAIs
/*    */   extends AI01decoder
/*    */ {
/*    */   private static final int HEADER_SIZE = 4;
/*    */   
/*    */   AI01AndOtherAIs(BitArray information) {
/* 42 */     super(information);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String parseInformation() throws NotFoundException, FormatException {
/*    */     StringBuilder buff;
/* 49 */     (buff = new StringBuilder()).append("(01)");
/* 50 */     int initialGtinPosition = buff.length();
/* 51 */     int firstGtinDigit = getGeneralDecoder().extractNumericValueFromBitArray(4, 4);
/* 52 */     buff.append(firstGtinDigit);
/*    */     
/* 54 */     encodeCompressedGtinWithoutAI(buff, 8, initialGtinPosition);
/*    */     
/* 56 */     return getGeneralDecoder().decodeAllCodes(buff, 48);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\AI01AndOtherAIs.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */