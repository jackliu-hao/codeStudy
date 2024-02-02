/*    */ package com.google.zxing.pdf417.decoder;
/*    */ 
/*    */ import java.util.Formatter;
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
/*    */ class DetectionResultColumn
/*    */ {
/*    */   private static final int MAX_NEARBY_DISTANCE = 5;
/*    */   private final BoundingBox boundingBox;
/*    */   private final Codeword[] codewords;
/*    */   
/*    */   DetectionResultColumn(BoundingBox boundingBox) {
/* 32 */     this.boundingBox = new BoundingBox(boundingBox);
/* 33 */     this.codewords = new Codeword[boundingBox.getMaxY() - boundingBox.getMinY() + 1];
/*    */   }
/*    */   
/*    */   final Codeword getCodewordNearby(int imageRow) {
/*    */     Codeword codeword;
/* 38 */     if ((codeword = getCodeword(imageRow)) != null) {
/* 39 */       return codeword;
/*    */     }
/* 41 */     for (int i = 1; i < 5; i++) {
/*    */       int nearImageRow;
/* 43 */       if ((nearImageRow = imageRowToCodewordIndex(imageRow) - i) >= 0 && (
/*    */         
/* 45 */         codeword = this.codewords[nearImageRow]) != null) {
/* 46 */         return codeword;
/*    */       }
/*    */ 
/*    */       
/* 50 */       if ((nearImageRow = imageRowToCodewordIndex(imageRow) + i) < this.codewords.length && (
/*    */         
/* 52 */         codeword = this.codewords[nearImageRow]) != null) {
/* 53 */         return codeword;
/*    */       }
/*    */     } 
/*    */     
/* 57 */     return null;
/*    */   }
/*    */   
/*    */   final int imageRowToCodewordIndex(int imageRow) {
/* 61 */     return imageRow - this.boundingBox.getMinY();
/*    */   }
/*    */   
/*    */   final void setCodeword(int imageRow, Codeword codeword) {
/* 65 */     this.codewords[imageRowToCodewordIndex(imageRow)] = codeword;
/*    */   }
/*    */   
/*    */   final Codeword getCodeword(int imageRow) {
/* 69 */     return this.codewords[imageRowToCodewordIndex(imageRow)];
/*    */   }
/*    */   
/*    */   final BoundingBox getBoundingBox() {
/* 73 */     return this.boundingBox;
/*    */   }
/*    */   
/*    */   final Codeword[] getCodewords() {
/* 77 */     return this.codewords;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 82 */     Formatter formatter = new Formatter();
/* 83 */     int row = 0; Codeword[] arrayOfCodeword; int i; byte b;
/* 84 */     for (i = (arrayOfCodeword = this.codewords).length, b = 0; b < i; b++) {
/* 85 */       Codeword codeword; if ((codeword = arrayOfCodeword[b]) == null) {
/* 86 */         formatter.format("%3d:    |   %n", new Object[] { Integer.valueOf(row++) });
/*    */       } else {
/*    */         
/* 89 */         formatter.format("%3d: %3d|%3d%n", new Object[] { Integer.valueOf(row++), Integer.valueOf(codeword.getRowNumber()), Integer.valueOf(codeword.getValue()) });
/*    */       } 
/* 91 */     }  String result = formatter.toString();
/* 92 */     formatter.close();
/* 93 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\decoder\DetectionResultColumn.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */