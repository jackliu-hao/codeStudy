/*    */ package com.google.zxing.qrcode.decoder;
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
/*    */ public enum ErrorCorrectionLevel
/*    */ {
/* 28 */   L(1),
/*    */   
/* 30 */   M(0),
/*    */   
/* 32 */   Q(3),
/*    */   
/* 34 */   H(2); private static final ErrorCorrectionLevel[] FOR_BITS;
/*    */   static {
/* 36 */     FOR_BITS = new ErrorCorrectionLevel[] { M, L, H, Q };
/*    */   }
/*    */   private final int bits;
/*    */   
/*    */   ErrorCorrectionLevel(int bits) {
/* 41 */     this.bits = bits;
/*    */   }
/*    */   
/*    */   public int getBits() {
/* 45 */     return this.bits;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ErrorCorrectionLevel forBits(int bits) {
/* 53 */     if (bits < 0 || bits >= FOR_BITS.length) {
/* 54 */       throw new IllegalArgumentException();
/*    */     }
/* 56 */     return FOR_BITS[bits];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\decoder\ErrorCorrectionLevel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */