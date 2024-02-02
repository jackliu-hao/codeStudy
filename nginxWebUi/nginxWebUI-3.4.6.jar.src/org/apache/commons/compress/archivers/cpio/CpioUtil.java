/*     */ package org.apache.commons.compress.archivers.cpio;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class CpioUtil
/*     */ {
/*     */   static long fileType(long mode) {
/*  32 */     return mode & 0xF000L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long byteArray2long(byte[] number, boolean swapHalfWord) {
/*  47 */     if (number.length % 2 != 0) {
/*  48 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*  52 */     int pos = 0;
/*  53 */     byte[] tmp_number = new byte[number.length];
/*  54 */     System.arraycopy(number, 0, tmp_number, 0, number.length);
/*     */     
/*  56 */     if (!swapHalfWord) {
/*  57 */       byte tmp = 0;
/*  58 */       for (pos = 0; pos < tmp_number.length; pos++) {
/*  59 */         tmp = tmp_number[pos];
/*  60 */         tmp_number[pos++] = tmp_number[pos];
/*  61 */         tmp_number[pos] = tmp;
/*     */       } 
/*     */     } 
/*     */     
/*  65 */     long ret = (tmp_number[0] & 0xFF);
/*  66 */     for (pos = 1; pos < tmp_number.length; pos++) {
/*  67 */       ret <<= 8L;
/*  68 */       ret |= (tmp_number[pos] & 0xFF);
/*     */     } 
/*  70 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static byte[] long2byteArray(long number, int length, boolean swapHalfWord) {
/*  89 */     byte[] ret = new byte[length];
/*  90 */     int pos = 0;
/*     */ 
/*     */     
/*  93 */     if (length % 2 != 0 || length < 2) {
/*  94 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*  97 */     long tmp_number = number;
/*  98 */     for (pos = length - 1; pos >= 0; pos--) {
/*  99 */       ret[pos] = (byte)(int)(tmp_number & 0xFFL);
/* 100 */       tmp_number >>= 8L;
/*     */     } 
/*     */     
/* 103 */     if (!swapHalfWord) {
/* 104 */       byte tmp = 0;
/* 105 */       for (pos = 0; pos < length; pos++) {
/* 106 */         tmp = ret[pos];
/* 107 */         ret[pos++] = ret[pos];
/* 108 */         ret[pos] = tmp;
/*     */       } 
/*     */     } 
/*     */     
/* 112 */     return ret;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\cpio\CpioUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */