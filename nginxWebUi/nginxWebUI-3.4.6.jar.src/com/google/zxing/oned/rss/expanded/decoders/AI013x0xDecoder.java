/*    */ package com.google.zxing.oned.rss.expanded.decoders;
/*    */ 
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
/*    */ abstract class AI013x0xDecoder
/*    */   extends AI01weightDecoder
/*    */ {
/*    */   private static final int HEADER_SIZE = 5;
/*    */   private static final int WEIGHT_SIZE = 15;
/*    */   
/*    */   AI013x0xDecoder(BitArray information) {
/* 41 */     super(information);
/*    */   }
/*    */ 
/*    */   
/*    */   public String parseInformation() throws NotFoundException {
/* 46 */     if (getInformation().getSize() != 60) {
/* 47 */       throw NotFoundException.getNotFoundInstance();
/*    */     }
/*    */     
/* 50 */     StringBuilder buf = new StringBuilder();
/*    */     
/* 52 */     encodeCompressedGtin(buf, 5);
/* 53 */     encodeCompressedWeight(buf, 45, 15);
/*    */     
/* 55 */     return buf.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\AI013x0xDecoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */