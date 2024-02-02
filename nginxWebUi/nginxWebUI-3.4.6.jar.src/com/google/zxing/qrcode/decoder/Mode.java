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
/*    */ public enum Mode
/*    */ {
/* 27 */   TERMINATOR(new int[] { 0, 0, 0 }, 0),
/* 28 */   NUMERIC(new int[] { 10, 12, 14 }, 1),
/* 29 */   ALPHANUMERIC(new int[] { 9, 11, 13 }, 2),
/* 30 */   STRUCTURED_APPEND(new int[] { 0, 0, 0 }, 3),
/* 31 */   BYTE(new int[] { 8, 16, 16 }, 4),
/* 32 */   ECI(new int[] { 0, 0, 0 }, 7),
/* 33 */   KANJI(new int[] { 8, 10, 12 }, 8),
/* 34 */   FNC1_FIRST_POSITION(new int[] { 0, 0, 0 }, 5),
/* 35 */   FNC1_SECOND_POSITION(new int[] { 0, 0, 0 }, 9),
/*    */   
/* 37 */   HANZI(new int[] { 8, 10, 12 }, 13);
/*    */   
/*    */   private final int[] characterCountBitsForVersions;
/*    */   private final int bits;
/*    */   
/*    */   Mode(int[] characterCountBitsForVersions, int bits) {
/* 43 */     this.characterCountBitsForVersions = characterCountBitsForVersions;
/* 44 */     this.bits = bits;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Mode forBits(int bits) {
/* 53 */     switch (bits) {
/*    */       case 0:
/* 55 */         return TERMINATOR;
/*    */       case 1:
/* 57 */         return NUMERIC;
/*    */       case 2:
/* 59 */         return ALPHANUMERIC;
/*    */       case 3:
/* 61 */         return STRUCTURED_APPEND;
/*    */       case 4:
/* 63 */         return BYTE;
/*    */       case 5:
/* 65 */         return FNC1_FIRST_POSITION;
/*    */       case 7:
/* 67 */         return ECI;
/*    */       case 8:
/* 69 */         return KANJI;
/*    */       case 9:
/* 71 */         return FNC1_SECOND_POSITION;
/*    */       
/*    */       case 13:
/* 74 */         return HANZI;
/*    */     } 
/* 76 */     throw new IllegalArgumentException();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCharacterCountBits(Version version) {
/*    */     int offset;
/*    */     int number;
/* 88 */     if ((number = version.getVersionNumber()) <= 9) {
/* 89 */       offset = 0;
/* 90 */     } else if (number <= 26) {
/* 91 */       offset = 1;
/*    */     } else {
/* 93 */       offset = 2;
/*    */     } 
/* 95 */     return this.characterCountBitsForVersions[offset];
/*    */   }
/*    */   
/*    */   public int getBits() {
/* 99 */     return this.bits;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\decoder\Mode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */