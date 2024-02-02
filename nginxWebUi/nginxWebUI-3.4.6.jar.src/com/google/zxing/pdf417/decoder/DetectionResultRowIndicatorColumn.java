/*     */ package com.google.zxing.pdf417.decoder;
/*     */ 
/*     */ import com.google.zxing.ResultPoint;
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
/*     */ final class DetectionResultRowIndicatorColumn
/*     */   extends DetectionResultColumn
/*     */ {
/*     */   private final boolean isLeft;
/*     */   
/*     */   DetectionResultRowIndicatorColumn(BoundingBox boundingBox, boolean isLeft) {
/*  30 */     super(boundingBox);
/*  31 */     this.isLeft = isLeft; } private void setRowNumbers() {
/*     */     Codeword[] arrayOfCodeword;
/*     */     int i;
/*     */     byte b;
/*  35 */     for (i = (arrayOfCodeword = getCodewords()).length, b = 0; b < i; b++) {
/*  36 */       Codeword codeword; if ((codeword = arrayOfCodeword[b]) != null) {
/*  37 */         codeword.setRowNumberAsRowIndicatorColumn();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void adjustCompleteIndicatorColumnRowNumbers(BarcodeMetadata barcodeMetadata) {
/*  47 */     Codeword[] codewords = getCodewords();
/*  48 */     setRowNumbers();
/*  49 */     removeIncorrectCodewords(codewords, barcodeMetadata);
/*  50 */     BoundingBox boundingBox = getBoundingBox();
/*  51 */     ResultPoint top = this.isLeft ? boundingBox.getTopLeft() : boundingBox.getTopRight();
/*  52 */     ResultPoint bottom = this.isLeft ? boundingBox.getBottomLeft() : boundingBox.getBottomRight();
/*  53 */     int firstRow = imageRowToCodewordIndex((int)top.getY());
/*  54 */     int lastRow = imageRowToCodewordIndex((int)bottom.getY());
/*     */ 
/*     */ 
/*     */     
/*  58 */     int barcodeRow = -1;
/*  59 */     int maxRowHeight = 1;
/*  60 */     int currentRowHeight = 0;
/*  61 */     for (int codewordsRow = firstRow; codewordsRow < lastRow; codewordsRow++) {
/*  62 */       if (codewords[codewordsRow] != null) {
/*     */         Codeword codeword;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         int rowDifference;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  79 */         if ((rowDifference = (codeword = codewords[codewordsRow]).getRowNumber() - barcodeRow) == 0) {
/*  80 */           currentRowHeight++;
/*  81 */         } else if (rowDifference == 1) {
/*  82 */           maxRowHeight = Math.max(maxRowHeight, currentRowHeight);
/*  83 */           currentRowHeight = 1;
/*  84 */           barcodeRow = codeword.getRowNumber();
/*  85 */         } else if (rowDifference < 0 || codeword
/*  86 */           .getRowNumber() >= barcodeMetadata.getRowCount() || rowDifference > codewordsRow) {
/*     */           
/*  88 */           codewords[codewordsRow] = null;
/*     */         } else {
/*     */           int checkedRows;
/*  91 */           if (maxRowHeight > 2) {
/*  92 */             checkedRows = (maxRowHeight - 2) * rowDifference;
/*     */           } else {
/*  94 */             checkedRows = rowDifference;
/*     */           } 
/*  96 */           boolean closePreviousCodewordFound = (checkedRows >= codewordsRow);
/*  97 */           for (int i = 1; i <= checkedRows && !closePreviousCodewordFound; i++)
/*     */           {
/*     */             
/* 100 */             closePreviousCodewordFound = (codewords[codewordsRow - i] != null);
/*     */           }
/* 102 */           if (closePreviousCodewordFound) {
/* 103 */             codewords[codewordsRow] = null;
/*     */           } else {
/* 105 */             barcodeRow = codeword.getRowNumber();
/* 106 */             currentRowHeight = 1;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   int[] getRowHeights() {
/*     */     BarcodeMetadata barcodeMetadata;
/* 115 */     if ((barcodeMetadata = getBarcodeMetadata()) == null) {
/* 116 */       return null;
/*     */     }
/* 118 */     adjustIncompleteIndicatorColumnRowNumbers(barcodeMetadata);
/* 119 */     int[] result = new int[barcodeMetadata.getRowCount()]; Codeword[] arrayOfCodeword; int i; byte b;
/* 120 */     for (i = (arrayOfCodeword = getCodewords()).length, b = 0; b < i; ) {
/* 121 */       Codeword codeword; int rowNumber; if ((codeword = arrayOfCodeword[b]) != null && (
/*     */         
/* 123 */         rowNumber = codeword.getRowNumber()) < result.length)
/*     */       {
/*     */ 
/*     */         
/* 127 */         result[rowNumber] = result[rowNumber] + 1; } 
/*     */       b++;
/*     */     } 
/* 130 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void adjustIncompleteIndicatorColumnRowNumbers(BarcodeMetadata barcodeMetadata) {
/* 137 */     BoundingBox boundingBox = getBoundingBox();
/* 138 */     ResultPoint top = this.isLeft ? boundingBox.getTopLeft() : boundingBox.getTopRight();
/* 139 */     ResultPoint bottom = this.isLeft ? boundingBox.getBottomLeft() : boundingBox.getBottomRight();
/* 140 */     int firstRow = imageRowToCodewordIndex((int)top.getY());
/* 141 */     int lastRow = imageRowToCodewordIndex((int)bottom.getY());
/*     */     
/* 143 */     Codeword[] codewords = getCodewords();
/* 144 */     int barcodeRow = -1;
/* 145 */     int maxRowHeight = 1;
/* 146 */     int currentRowHeight = 0;
/* 147 */     for (int codewordsRow = firstRow; codewordsRow < lastRow; codewordsRow++) {
/* 148 */       if (codewords[codewordsRow] != null) {
/*     */         Codeword codeword;
/*     */ 
/*     */ 
/*     */         
/* 153 */         (codeword = codewords[codewordsRow]).setRowNumberAsRowIndicatorColumn();
/*     */ 
/*     */         
/*     */         int rowDifference;
/*     */ 
/*     */         
/* 159 */         if ((rowDifference = codeword.getRowNumber() - barcodeRow) == 0) {
/* 160 */           currentRowHeight++;
/* 161 */         } else if (rowDifference == 1) {
/* 162 */           maxRowHeight = Math.max(maxRowHeight, currentRowHeight);
/* 163 */           currentRowHeight = 1;
/* 164 */           barcodeRow = codeword.getRowNumber();
/* 165 */         } else if (codeword.getRowNumber() >= barcodeMetadata.getRowCount()) {
/* 166 */           codewords[codewordsRow] = null;
/*     */         } else {
/* 168 */           barcodeRow = codeword.getRowNumber();
/* 169 */           currentRowHeight = 1;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   BarcodeMetadata getBarcodeMetadata() {
/* 176 */     Codeword[] codewords = getCodewords();
/* 177 */     BarcodeValue barcodeColumnCount = new BarcodeValue();
/* 178 */     BarcodeValue barcodeRowCountUpperPart = new BarcodeValue();
/* 179 */     BarcodeValue barcodeRowCountLowerPart = new BarcodeValue();
/* 180 */     BarcodeValue barcodeECLevel = new BarcodeValue(); Codeword[] arrayOfCodeword1; int i; byte b;
/* 181 */     for (i = (arrayOfCodeword1 = codewords).length, b = 0; b < i; b++) {
/* 182 */       Codeword codeword; if ((codeword = arrayOfCodeword1[b]) != null) {
/*     */ 
/*     */         
/* 185 */         codeword.setRowNumberAsRowIndicatorColumn();
/* 186 */         int rowIndicatorValue = codeword.getValue() % 30;
/* 187 */         int codewordRowNumber = codeword.getRowNumber();
/* 188 */         if (!this.isLeft) {
/* 189 */           codewordRowNumber += 2;
/*     */         }
/* 191 */         switch (codewordRowNumber % 3) {
/*     */           case 0:
/* 193 */             barcodeRowCountUpperPart.setValue(rowIndicatorValue * 3 + 1);
/*     */             break;
/*     */           case 1:
/* 196 */             barcodeECLevel.setValue(rowIndicatorValue / 3);
/* 197 */             barcodeRowCountLowerPart.setValue(rowIndicatorValue % 3);
/*     */             break;
/*     */           case 2:
/* 200 */             barcodeColumnCount.setValue(rowIndicatorValue + 1);
/*     */             break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 205 */     if ((barcodeColumnCount.getValue()).length == 0 || (barcodeRowCountUpperPart
/* 206 */       .getValue()).length == 0 || (barcodeRowCountLowerPart
/* 207 */       .getValue()).length == 0 || (barcodeECLevel
/* 208 */       .getValue()).length == 0 || barcodeColumnCount
/* 209 */       .getValue()[0] <= 0 || barcodeRowCountUpperPart
/* 210 */       .getValue()[0] + barcodeRowCountLowerPart.getValue()[0] < 3 || barcodeRowCountUpperPart
/* 211 */       .getValue()[0] + barcodeRowCountLowerPart.getValue()[0] > 90) {
/* 212 */       return null;
/*     */     }
/*     */     
/* 215 */     BarcodeMetadata barcodeMetadata = new BarcodeMetadata(barcodeColumnCount.getValue()[0], barcodeRowCountUpperPart.getValue()[0], barcodeRowCountLowerPart.getValue()[0], barcodeECLevel.getValue()[0]);
/* 216 */     removeIncorrectCodewords(codewords, barcodeMetadata);
/* 217 */     return barcodeMetadata;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void removeIncorrectCodewords(Codeword[] codewords, BarcodeMetadata barcodeMetadata) {
/* 223 */     for (int codewordRow = 0; codewordRow < codewords.length; codewordRow++) {
/* 224 */       Codeword codeword = codewords[codewordRow];
/* 225 */       if (codewords[codewordRow] != null) {
/*     */ 
/*     */         
/* 228 */         int rowIndicatorValue = codeword.getValue() % 30;
/*     */         int codewordRowNumber;
/* 230 */         if ((codewordRowNumber = codeword.getRowNumber()) > barcodeMetadata.getRowCount()) {
/* 231 */           codewords[codewordRow] = null;
/*     */         } else {
/*     */           
/* 234 */           if (!this.isLeft) {
/* 235 */             codewordRowNumber += 2;
/*     */           }
/* 237 */           switch (codewordRowNumber % 3) {
/*     */             case 0:
/* 239 */               if (rowIndicatorValue * 3 + 1 != barcodeMetadata.getRowCountUpperPart()) {
/* 240 */                 codewords[codewordRow] = null;
/*     */               }
/*     */               break;
/*     */             case 1:
/* 244 */               if (rowIndicatorValue / 3 != barcodeMetadata.getErrorCorrectionLevel() || rowIndicatorValue % 3 != barcodeMetadata
/* 245 */                 .getRowCountLowerPart()) {
/* 246 */                 codewords[codewordRow] = null;
/*     */               }
/*     */               break;
/*     */             case 2:
/* 250 */               if (rowIndicatorValue + 1 != barcodeMetadata.getColumnCount())
/* 251 */                 codewords[codewordRow] = null; 
/*     */               break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   boolean isLeft() {
/* 259 */     return this.isLeft;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 264 */     return "IsLeft: " + this.isLeft + '\n' + super.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\decoder\DetectionResultRowIndicatorColumn.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */