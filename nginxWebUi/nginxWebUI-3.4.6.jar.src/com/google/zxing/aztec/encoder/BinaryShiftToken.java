/*    */ package com.google.zxing.aztec.encoder;
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
/*    */ final class BinaryShiftToken
/*    */   extends Token
/*    */ {
/*    */   private final short binaryShiftStart;
/*    */   private final short binaryShiftByteCount;
/*    */   
/*    */   BinaryShiftToken(Token previous, int binaryShiftStart, int binaryShiftByteCount) {
/* 29 */     super(previous);
/* 30 */     this.binaryShiftStart = (short)binaryShiftStart;
/* 31 */     this.binaryShiftByteCount = (short)binaryShiftByteCount;
/*    */   }
/*    */ 
/*    */   
/*    */   public void appendTo(BitArray bitArray, byte[] text) {
/* 36 */     for (int i = 0; i < this.binaryShiftByteCount; i++) {
/* 37 */       if (i == 0 || (i == 31 && this.binaryShiftByteCount <= 62)) {
/*    */ 
/*    */         
/* 40 */         bitArray.appendBits(31, 5);
/* 41 */         if (this.binaryShiftByteCount > 62) {
/* 42 */           bitArray.appendBits(this.binaryShiftByteCount - 31, 16);
/* 43 */         } else if (i == 0) {
/*    */           
/* 45 */           bitArray.appendBits(Math.min(this.binaryShiftByteCount, 31), 5);
/*    */         } else {
/*    */           
/* 48 */           bitArray.appendBits(this.binaryShiftByteCount - 31, 5);
/*    */         } 
/*    */       } 
/* 51 */       bitArray.appendBits(text[this.binaryShiftStart + i], 8);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 57 */     return "<" + this.binaryShiftStart + "::" + (this.binaryShiftStart + this.binaryShiftByteCount - 1) + '>';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\aztec\encoder\BinaryShiftToken.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */