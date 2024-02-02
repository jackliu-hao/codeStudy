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
/*    */ abstract class Token
/*    */ {
/* 23 */   static final Token EMPTY = new SimpleToken(null, 0, 0);
/*    */   
/*    */   private final Token previous;
/*    */   
/*    */   Token(Token previous) {
/* 28 */     this.previous = previous;
/*    */   }
/*    */   
/*    */   final Token getPrevious() {
/* 32 */     return this.previous;
/*    */   }
/*    */   
/*    */   final Token add(int value, int bitCount) {
/* 36 */     return new SimpleToken(this, value, bitCount);
/*    */   }
/*    */ 
/*    */   
/*    */   final Token addBinaryShift(int start, int byteCount) {
/* 41 */     return new BinaryShiftToken(this, start, byteCount);
/*    */   }
/*    */   
/*    */   abstract void appendTo(BitArray paramBitArray, byte[] paramArrayOfbyte);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\aztec\encoder\Token.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */