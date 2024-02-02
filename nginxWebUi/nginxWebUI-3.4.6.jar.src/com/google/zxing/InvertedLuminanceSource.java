/*    */ package com.google.zxing;
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
/*    */ public final class InvertedLuminanceSource
/*    */   extends LuminanceSource
/*    */ {
/*    */   private final LuminanceSource delegate;
/*    */   
/*    */   public InvertedLuminanceSource(LuminanceSource delegate) {
/* 30 */     super(delegate.getWidth(), delegate.getHeight());
/* 31 */     this.delegate = delegate;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] getRow(int y, byte[] row) {
/* 36 */     row = this.delegate.getRow(y, row);
/* 37 */     int width = getWidth();
/* 38 */     for (int i = 0; i < width; i++) {
/* 39 */       row[i] = (byte)(255 - (row[i] & 0xFF));
/*    */     }
/* 41 */     return row;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] getMatrix() {
/* 46 */     byte[] matrix = this.delegate.getMatrix();
/*    */     int length;
/* 48 */     byte[] invertedMatrix = new byte[length = getWidth() * getHeight()];
/* 49 */     for (int i = 0; i < length; i++) {
/* 50 */       invertedMatrix[i] = (byte)(255 - (matrix[i] & 0xFF));
/*    */     }
/* 52 */     return invertedMatrix;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCropSupported() {
/* 57 */     return this.delegate.isCropSupported();
/*    */   }
/*    */ 
/*    */   
/*    */   public LuminanceSource crop(int left, int top, int width, int height) {
/* 62 */     return new InvertedLuminanceSource(this.delegate.crop(left, top, width, height));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isRotateSupported() {
/* 67 */     return this.delegate.isRotateSupported();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LuminanceSource invert() {
/* 75 */     return this.delegate;
/*    */   }
/*    */ 
/*    */   
/*    */   public LuminanceSource rotateCounterClockwise() {
/* 80 */     return new InvertedLuminanceSource(this.delegate.rotateCounterClockwise());
/*    */   }
/*    */ 
/*    */   
/*    */   public LuminanceSource rotateCounterClockwise45() {
/* 85 */     return new InvertedLuminanceSource(this.delegate.rotateCounterClockwise45());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\InvertedLuminanceSource.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */