/*    */ package com.google.zxing.oned.rss.expanded.decoders;
/*    */ 
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
/*    */ abstract class AI01decoder
/*    */   extends AbstractExpandedDecoder
/*    */ {
/*    */   static final int GTIN_SIZE = 40;
/*    */   
/*    */   AI01decoder(BitArray information) {
/* 40 */     super(information);
/*    */   }
/*    */   
/*    */   final void encodeCompressedGtin(StringBuilder buf, int currentPos) {
/* 44 */     buf.append("(01)");
/* 45 */     int initialPosition = buf.length();
/* 46 */     buf.append('9');
/*    */     
/* 48 */     encodeCompressedGtinWithoutAI(buf, currentPos, initialPosition);
/*    */   }
/*    */   
/*    */   final void encodeCompressedGtinWithoutAI(StringBuilder buf, int currentPos, int initialBufferPosition) {
/* 52 */     for (int i = 0; i < 4; i++) {
/*    */       int currentBlock;
/* 54 */       if ((currentBlock = getGeneralDecoder().extractNumericValueFromBitArray(currentPos + i * 10, 10)) / 100 == 0) {
/* 55 */         buf.append('0');
/*    */       }
/* 57 */       if (currentBlock / 10 == 0) {
/* 58 */         buf.append('0');
/*    */       }
/* 60 */       buf.append(currentBlock);
/*    */     } 
/*    */     
/* 63 */     appendCheckDigit(buf, initialBufferPosition);
/*    */   }
/*    */   
/*    */   private static void appendCheckDigit(StringBuilder buf, int currentPos) {
/* 67 */     int checkDigit = 0;
/* 68 */     for (int i = 0; i < 13; i++) {
/* 69 */       int digit = buf.charAt(i + currentPos) - 48;
/* 70 */       checkDigit += ((i & 0x1) == 0) ? (3 * digit) : digit;
/*    */     } 
/*    */ 
/*    */     
/* 74 */     if ((checkDigit = 10 - checkDigit % 10) == 10) {
/* 75 */       checkDigit = 0;
/*    */     }
/*    */     
/* 78 */     buf.append(checkDigit);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\AI01decoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */