/*    */ package com.google.zxing.oned;
/*    */ 
/*    */ import com.google.zxing.NotFoundException;
/*    */ import com.google.zxing.ReaderException;
/*    */ import com.google.zxing.Result;
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
/*    */ final class UPCEANExtensionSupport
/*    */ {
/* 26 */   private static final int[] EXTENSION_START_PATTERN = new int[] { 1, 1, 2 };
/*    */   
/* 28 */   private final UPCEANExtension2Support twoSupport = new UPCEANExtension2Support();
/* 29 */   private final UPCEANExtension5Support fiveSupport = new UPCEANExtension5Support();
/*    */   
/*    */   Result decodeRow(int rowNumber, BitArray row, int rowOffset) throws NotFoundException {
/* 32 */     int[] extensionStartRange = UPCEANReader.findGuardPattern(row, rowOffset, false, EXTENSION_START_PATTERN);
/*    */     try {
/* 34 */       return this.fiveSupport.decodeRow(rowNumber, row, extensionStartRange);
/* 35 */     } catch (ReaderException readerException) {
/* 36 */       return this.twoSupport.decodeRow(rowNumber, row, extensionStartRange);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\UPCEANExtensionSupport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */