/*    */ package com.google.zxing.pdf417.encoder;
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
/*    */ public final class BarcodeMatrix
/*    */ {
/*    */   private final BarcodeRow[] matrix;
/*    */   private int currentRow;
/*    */   private final int height;
/*    */   private final int width;
/*    */   
/*    */   BarcodeMatrix(int height, int width) {
/* 36 */     this.matrix = new BarcodeRow[height];
/*    */     
/* 38 */     for (int i = 0, matrixLength = this.matrix.length; i < matrixLength; i++) {
/* 39 */       this.matrix[i] = new BarcodeRow((width + 4) * 17 + 1);
/*    */     }
/* 41 */     this.width = width * 17;
/* 42 */     this.height = height;
/* 43 */     this.currentRow = -1;
/*    */   }
/*    */   
/*    */   void set(int x, int y, byte value) {
/* 47 */     this.matrix[y].set(x, value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void startRow() {
/* 57 */     this.currentRow++;
/*    */   }
/*    */   
/*    */   BarcodeRow getCurrentRow() {
/* 61 */     return this.matrix[this.currentRow];
/*    */   }
/*    */   
/*    */   public byte[][] getMatrix() {
/* 65 */     return getScaledMatrix(1, 1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[][] getScaledMatrix(int xScale, int yScale) {
/* 75 */     byte[][] matrixOut = new byte[this.height * yScale][this.width * xScale];
/* 76 */     int yMax = this.height * yScale;
/* 77 */     for (int i = 0; i < yMax; i++) {
/* 78 */       matrixOut[yMax - i - 1] = this.matrix[i / yScale].getScaledRow(xScale);
/*    */     }
/* 80 */     return matrixOut;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\encoder\BarcodeMatrix.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */