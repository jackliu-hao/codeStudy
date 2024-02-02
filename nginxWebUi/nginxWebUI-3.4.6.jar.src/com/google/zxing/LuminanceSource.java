/*     */ package com.google.zxing;
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
/*     */ public abstract class LuminanceSource
/*     */ {
/*     */   private final int width;
/*     */   private final int height;
/*     */   
/*     */   protected LuminanceSource(int width, int height) {
/*  34 */     this.width = width;
/*  35 */     this.height = height;
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
/*     */   public abstract byte[] getRow(int paramInt, byte[] paramArrayOfbyte);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract byte[] getMatrix();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getWidth() {
/*  66 */     return this.width;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getHeight() {
/*  73 */     return this.height;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCropSupported() {
/*  80 */     return false;
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
/*     */   public LuminanceSource crop(int left, int top, int width, int height) {
/*  94 */     throw new UnsupportedOperationException("This luminance source does not support cropping.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRotateSupported() {
/* 101 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LuminanceSource invert() {
/* 109 */     return new InvertedLuminanceSource(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LuminanceSource rotateCounterClockwise() {
/* 119 */     throw new UnsupportedOperationException("This luminance source does not support rotation by 90 degrees.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LuminanceSource rotateCounterClockwise45() {
/* 129 */     throw new UnsupportedOperationException("This luminance source does not support rotation by 45 degrees.");
/*     */   }
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 134 */     byte[] row = new byte[this.width];
/* 135 */     StringBuilder result = new StringBuilder(this.height * (this.width + 1));
/* 136 */     for (int y = 0; y < this.height; y++) {
/* 137 */       row = getRow(y, row);
/* 138 */       for (int x = 0; x < this.width; x++) {
/*     */         char c;
/*     */         int luminance;
/* 141 */         if ((luminance = row[x] & 0xFF) < 64) {
/* 142 */           c = '#';
/* 143 */         } else if (luminance < 128) {
/* 144 */           c = '+';
/* 145 */         } else if (luminance < 192) {
/* 146 */           c = '.';
/*     */         } else {
/* 148 */           c = ' ';
/*     */         } 
/* 150 */         result.append(c);
/*     */       } 
/* 152 */       result.append('\n');
/*     */     } 
/* 154 */     return result.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\LuminanceSource.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */