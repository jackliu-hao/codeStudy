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
/*    */ final class BarcodeRow
/*    */ {
/*    */   private final byte[] row;
/*    */   private int currentLocation;
/*    */   
/*    */   BarcodeRow(int width) {
/* 32 */     this.row = new byte[width];
/* 33 */     this.currentLocation = 0;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void set(int x, byte value) {
/* 43 */     this.row[x] = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void set(int x, boolean black) {
/* 53 */     this.row[x] = (byte)(black ? 1 : 0);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void addBar(boolean black, int width) {
/* 61 */     for (int ii = 0; ii < width; ii++) {
/* 62 */       set(this.currentLocation++, black);
/*    */     }
/*    */   }
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
/*    */   byte[] getScaledRow(int scale) {
/* 79 */     byte[] output = new byte[this.row.length * scale];
/* 80 */     for (int i = 0; i < output.length; i++) {
/* 81 */       output[i] = this.row[i / scale];
/*    */     }
/* 83 */     return output;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\encoder\BarcodeRow.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */