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
/*    */ 
/*    */ public abstract class AbstractExpandedDecoder
/*    */ {
/*    */   private final BitArray information;
/*    */   private final GeneralAppIdDecoder generalDecoder;
/*    */   
/*    */   AbstractExpandedDecoder(BitArray information) {
/* 43 */     this.information = information;
/* 44 */     this.generalDecoder = new GeneralAppIdDecoder(information);
/*    */   }
/*    */   
/*    */   protected final BitArray getInformation() {
/* 48 */     return this.information;
/*    */   }
/*    */   
/*    */   protected final GeneralAppIdDecoder getGeneralDecoder() {
/* 52 */     return this.generalDecoder;
/*    */   }
/*    */   
/*    */   public abstract String parseInformation() throws NotFoundException, FormatException;
/*    */   
/*    */   public static AbstractExpandedDecoder createDecoder(BitArray information) {
/* 58 */     if (information.get(1)) {
/* 59 */       return new AI01AndOtherAIs(information);
/*    */     }
/* 61 */     if (!information.get(2)) {
/* 62 */       return new AnyAIDecoder(information);
/*    */     }
/*    */     
/* 65 */     switch (GeneralAppIdDecoder.extractNumericValueFromBitArray(information, 1, 4)) {
/*    */       
/*    */       case 4:
/* 68 */         return new AI013103decoder(information);
/* 69 */       case 5: return new AI01320xDecoder(information);
/*    */     } 
/*    */     
/* 72 */     switch (GeneralAppIdDecoder.extractNumericValueFromBitArray(information, 1, 5)) {
/*    */       case 12:
/* 74 */         return new AI01392xDecoder(information);
/* 75 */       case 13: return new AI01393xDecoder(information);
/*    */     } 
/*    */     
/* 78 */     switch (GeneralAppIdDecoder.extractNumericValueFromBitArray(information, 1, 7)) {
/*    */       case 56:
/* 80 */         return new AI013x0x1xDecoder(information, "310", "11");
/* 81 */       case 57: return new AI013x0x1xDecoder(information, "320", "11");
/* 82 */       case 58: return new AI013x0x1xDecoder(information, "310", "13");
/* 83 */       case 59: return new AI013x0x1xDecoder(information, "320", "13");
/* 84 */       case 60: return new AI013x0x1xDecoder(information, "310", "15");
/* 85 */       case 61: return new AI013x0x1xDecoder(information, "320", "15");
/* 86 */       case 62: return new AI013x0x1xDecoder(information, "310", "17");
/* 87 */       case 63: return new AI013x0x1xDecoder(information, "320", "17");
/*    */     } 
/*    */     
/* 90 */     throw new IllegalStateException("unknown decoder: " + information);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\AbstractExpandedDecoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */