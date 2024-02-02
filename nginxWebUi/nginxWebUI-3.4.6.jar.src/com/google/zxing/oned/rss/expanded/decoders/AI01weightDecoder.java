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
/*    */ abstract class AI01weightDecoder
/*    */   extends AI01decoder
/*    */ {
/*    */   AI01weightDecoder(BitArray information) {
/* 37 */     super(information);
/*    */   }
/*    */   
/*    */   final void encodeCompressedWeight(StringBuilder buf, int currentPos, int weightSize) {
/* 41 */     int originalWeightNumeric = getGeneralDecoder().extractNumericValueFromBitArray(currentPos, weightSize);
/* 42 */     addWeightCode(buf, originalWeightNumeric);
/*    */     
/* 44 */     int weightNumeric = checkWeight(originalWeightNumeric);
/*    */     
/* 46 */     int currentDivisor = 100000;
/* 47 */     for (int i = 0; i < 5; i++) {
/* 48 */       if (weightNumeric / currentDivisor == 0) {
/* 49 */         buf.append('0');
/*    */       }
/* 51 */       currentDivisor /= 10;
/*    */     } 
/* 53 */     buf.append(weightNumeric);
/*    */   }
/*    */   
/*    */   protected abstract void addWeightCode(StringBuilder paramStringBuilder, int paramInt);
/*    */   
/*    */   protected abstract int checkWeight(int paramInt);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\AI01weightDecoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */