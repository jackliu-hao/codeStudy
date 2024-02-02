/*    */ package com.google.zxing.qrcode.encoder;
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
/*    */ public final class ByteMatrix
/*    */ {
/*    */   private final byte[][] bytes;
/*    */   private final int width;
/*    */   private final int height;
/*    */   
/*    */   public ByteMatrix(int width, int height) {
/* 32 */     this.bytes = new byte[height][width];
/* 33 */     this.width = width;
/* 34 */     this.height = height;
/*    */   }
/*    */   
/*    */   public int getHeight() {
/* 38 */     return this.height;
/*    */   }
/*    */   
/*    */   public int getWidth() {
/* 42 */     return this.width;
/*    */   }
/*    */   
/*    */   public byte get(int x, int y) {
/* 46 */     return this.bytes[y][x];
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[][] getArray() {
/* 53 */     return this.bytes;
/*    */   }
/*    */   
/*    */   public void set(int x, int y, byte value) {
/* 57 */     this.bytes[y][x] = value;
/*    */   }
/*    */   
/*    */   public void set(int x, int y, int value) {
/* 61 */     this.bytes[y][x] = (byte)value;
/*    */   }
/*    */   
/*    */   public void set(int x, int y, boolean value) {
/* 65 */     this.bytes[y][x] = (byte)(value ? 1 : 0);
/*    */   }
/*    */   
/*    */   public void clear(byte value) {
/* 69 */     for (int y = 0; y < this.height; y++) {
/* 70 */       for (int x = 0; x < this.width; x++) {
/* 71 */         this.bytes[y][x] = value;
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 78 */     StringBuilder result = new StringBuilder(2 * this.width * this.height + 2);
/* 79 */     for (int y = 0; y < this.height; y++) {
/* 80 */       for (int x = 0; x < this.width; x++) {
/* 81 */         switch (this.bytes[y][x]) {
/*    */           case 0:
/* 83 */             result.append(" 0");
/*    */             break;
/*    */           case 1:
/* 86 */             result.append(" 1");
/*    */             break;
/*    */           default:
/* 89 */             result.append("  ");
/*    */             break;
/*    */         } 
/*    */       } 
/* 93 */       result.append('\n');
/*    */     } 
/* 95 */     return result.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\encoder\ByteMatrix.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */