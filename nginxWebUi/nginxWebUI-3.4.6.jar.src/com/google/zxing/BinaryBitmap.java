/*     */ package com.google.zxing;
/*     */ 
/*     */ import com.google.zxing.common.BitArray;
/*     */ import com.google.zxing.common.BitMatrix;
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
/*     */ public final class BinaryBitmap
/*     */ {
/*     */   private final Binarizer binarizer;
/*     */   private BitMatrix matrix;
/*     */   
/*     */   public BinaryBitmap(Binarizer binarizer) {
/*  34 */     if (binarizer == null) {
/*  35 */       throw new IllegalArgumentException("Binarizer must be non-null.");
/*     */     }
/*  37 */     this.binarizer = binarizer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWidth() {
/*  44 */     return this.binarizer.getWidth();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHeight() {
/*  51 */     return this.binarizer.getHeight();
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
/*     */   public BitArray getBlackRow(int y, BitArray row) throws NotFoundException {
/*  66 */     return this.binarizer.getBlackRow(y, row);
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
/*     */   public BitMatrix getBlackMatrix() throws NotFoundException {
/*  84 */     if (this.matrix == null) {
/*  85 */       this.matrix = this.binarizer.getBlackMatrix();
/*     */     }
/*  87 */     return this.matrix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCropSupported() {
/*  94 */     return this.binarizer.getLuminanceSource().isCropSupported();
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
/*     */   public BinaryBitmap crop(int left, int top, int width, int height) {
/* 108 */     LuminanceSource newSource = this.binarizer.getLuminanceSource().crop(left, top, width, height);
/* 109 */     return new BinaryBitmap(this.binarizer.createBinarizer(newSource));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRotateSupported() {
/* 116 */     return this.binarizer.getLuminanceSource().isRotateSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BinaryBitmap rotateCounterClockwise() {
/* 126 */     LuminanceSource newSource = this.binarizer.getLuminanceSource().rotateCounterClockwise();
/* 127 */     return new BinaryBitmap(this.binarizer.createBinarizer(newSource));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BinaryBitmap rotateCounterClockwise45() {
/* 137 */     LuminanceSource newSource = this.binarizer.getLuminanceSource().rotateCounterClockwise45();
/* 138 */     return new BinaryBitmap(this.binarizer.createBinarizer(newSource));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*     */     try {
/* 144 */       return getBlackMatrix().toString();
/* 145 */     } catch (NotFoundException notFoundException) {
/* 146 */       return "";
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\BinaryBitmap.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */