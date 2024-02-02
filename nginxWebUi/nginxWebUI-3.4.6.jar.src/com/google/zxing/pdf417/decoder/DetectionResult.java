/*     */ package com.google.zxing.pdf417.decoder;
/*     */ 
/*     */ import java.util.Formatter;
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
/*     */ final class DetectionResult
/*     */ {
/*     */   private static final int ADJUST_ROW_NUMBER_SKIP = 2;
/*     */   private final BarcodeMetadata barcodeMetadata;
/*     */   private final DetectionResultColumn[] detectionResultColumns;
/*     */   private BoundingBox boundingBox;
/*     */   private final int barcodeColumnCount;
/*     */   
/*     */   DetectionResult(BarcodeMetadata barcodeMetadata, BoundingBox boundingBox) {
/*  36 */     this.barcodeMetadata = barcodeMetadata;
/*  37 */     this.barcodeColumnCount = barcodeMetadata.getColumnCount();
/*  38 */     this.boundingBox = boundingBox;
/*  39 */     this.detectionResultColumns = new DetectionResultColumn[this.barcodeColumnCount + 2];
/*     */   }
/*     */   DetectionResultColumn[] getDetectionResultColumns() {
/*     */     int previousUnadjustedCount;
/*  43 */     adjustIndicatorColumnRowNumbers(this.detectionResultColumns[0]);
/*  44 */     adjustIndicatorColumnRowNumbers(this.detectionResultColumns[this.barcodeColumnCount + 1]);
/*  45 */     int unadjustedCodewordCount = 928;
/*     */     
/*     */     do {
/*  48 */       previousUnadjustedCount = unadjustedCodewordCount;
/*     */     }
/*  50 */     while ((unadjustedCodewordCount = adjustRowNumbers()) > 0 && unadjustedCodewordCount < previousUnadjustedCount);
/*  51 */     return this.detectionResultColumns;
/*     */   }
/*     */   
/*     */   private void adjustIndicatorColumnRowNumbers(DetectionResultColumn detectionResultColumn) {
/*  55 */     if (detectionResultColumn != null) {
/*  56 */       ((DetectionResultRowIndicatorColumn)detectionResultColumn)
/*  57 */         .adjustCompleteIndicatorColumnRowNumbers(this.barcodeMetadata);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int adjustRowNumbers() {
/*     */     int unadjustedCount;
/*  70 */     if ((unadjustedCount = adjustRowNumbersByRow()) == 0) {
/*  71 */       return 0;
/*     */     }
/*  73 */     for (int barcodeColumn = 1; barcodeColumn < this.barcodeColumnCount + 1; barcodeColumn++) {
/*  74 */       Codeword[] codewords = this.detectionResultColumns[barcodeColumn].getCodewords();
/*  75 */       for (int codewordsRow = 0; codewordsRow < codewords.length; codewordsRow++) {
/*  76 */         if (codewords[codewordsRow] != null)
/*     */         {
/*     */           
/*  79 */           if (!codewords[codewordsRow].hasValidRowNumber())
/*  80 */             adjustRowNumbers(barcodeColumn, codewordsRow, codewords); 
/*     */         }
/*     */       } 
/*     */     } 
/*  84 */     return unadjustedCount;
/*     */   }
/*     */   
/*     */   private int adjustRowNumbersByRow() {
/*  88 */     adjustRowNumbersFromBothRI();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  94 */     return adjustRowNumbersFromLRI() + adjustRowNumbersFromRRI();
/*     */   }
/*     */   
/*     */   private void adjustRowNumbersFromBothRI() {
/*  98 */     if (this.detectionResultColumns[0] == null || this.detectionResultColumns[this.barcodeColumnCount + 1] == null) {
/*     */       return;
/*     */     }
/* 101 */     Codeword[] LRIcodewords = this.detectionResultColumns[0].getCodewords();
/* 102 */     Codeword[] RRIcodewords = this.detectionResultColumns[this.barcodeColumnCount + 1].getCodewords();
/* 103 */     for (int codewordsRow = 0; codewordsRow < LRIcodewords.length; codewordsRow++) {
/* 104 */       if (LRIcodewords[codewordsRow] != null && RRIcodewords[codewordsRow] != null && LRIcodewords[codewordsRow]
/*     */         
/* 106 */         .getRowNumber() == RRIcodewords[codewordsRow].getRowNumber())
/* 107 */         for (int barcodeColumn = 1; barcodeColumn <= this.barcodeColumnCount; barcodeColumn++) {
/*     */           Codeword codeword;
/* 109 */           if ((codeword = this.detectionResultColumns[barcodeColumn].getCodewords()[codewordsRow]) != null) {
/*     */ 
/*     */             
/* 112 */             codeword.setRowNumber(LRIcodewords[codewordsRow].getRowNumber());
/* 113 */             if (!codeword.hasValidRowNumber()) {
/* 114 */               this.detectionResultColumns[barcodeColumn].getCodewords()[codewordsRow] = null;
/*     */             }
/*     */           } 
/*     */         }  
/*     */     } 
/*     */   }
/*     */   
/*     */   private int adjustRowNumbersFromRRI() {
/* 122 */     if (this.detectionResultColumns[this.barcodeColumnCount + 1] == null) {
/* 123 */       return 0;
/*     */     }
/* 125 */     int unadjustedCount = 0;
/* 126 */     Codeword[] codewords = this.detectionResultColumns[this.barcodeColumnCount + 1].getCodewords();
/* 127 */     for (int codewordsRow = 0; codewordsRow < codewords.length; codewordsRow++) {
/* 128 */       if (codewords[codewordsRow] != null) {
/*     */ 
/*     */         
/* 131 */         int rowIndicatorRowNumber = codewords[codewordsRow].getRowNumber();
/* 132 */         int invalidRowCounts = 0;
/* 133 */         for (int barcodeColumn = this.barcodeColumnCount + 1; barcodeColumn > 0 && invalidRowCounts < 2; barcodeColumn--) {
/*     */           Codeword codeword;
/* 135 */           if ((codeword = this.detectionResultColumns[barcodeColumn].getCodewords()[codewordsRow]) != null) {
/* 136 */             invalidRowCounts = adjustRowNumberIfValid(rowIndicatorRowNumber, invalidRowCounts, codeword);
/* 137 */             if (!codeword.hasValidRowNumber())
/* 138 */               unadjustedCount++; 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 143 */     return unadjustedCount;
/*     */   }
/*     */   
/*     */   private int adjustRowNumbersFromLRI() {
/* 147 */     if (this.detectionResultColumns[0] == null) {
/* 148 */       return 0;
/*     */     }
/* 150 */     int unadjustedCount = 0;
/* 151 */     Codeword[] codewords = this.detectionResultColumns[0].getCodewords();
/* 152 */     for (int codewordsRow = 0; codewordsRow < codewords.length; codewordsRow++) {
/* 153 */       if (codewords[codewordsRow] != null) {
/*     */ 
/*     */         
/* 156 */         int rowIndicatorRowNumber = codewords[codewordsRow].getRowNumber();
/* 157 */         int invalidRowCounts = 0;
/* 158 */         for (int barcodeColumn = 1; barcodeColumn < this.barcodeColumnCount + 1 && invalidRowCounts < 2; barcodeColumn++) {
/*     */           Codeword codeword;
/* 160 */           if ((codeword = this.detectionResultColumns[barcodeColumn].getCodewords()[codewordsRow]) != null) {
/* 161 */             invalidRowCounts = adjustRowNumberIfValid(rowIndicatorRowNumber, invalidRowCounts, codeword);
/* 162 */             if (!codeword.hasValidRowNumber())
/* 163 */               unadjustedCount++; 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 168 */     return unadjustedCount;
/*     */   }
/*     */   
/*     */   private static int adjustRowNumberIfValid(int rowIndicatorRowNumber, int invalidRowCounts, Codeword codeword) {
/* 172 */     if (codeword == null) {
/* 173 */       return invalidRowCounts;
/*     */     }
/* 175 */     if (!codeword.hasValidRowNumber()) {
/* 176 */       if (codeword.isValidRowNumber(rowIndicatorRowNumber)) {
/* 177 */         codeword.setRowNumber(rowIndicatorRowNumber);
/* 178 */         invalidRowCounts = 0;
/*     */       } else {
/* 180 */         invalidRowCounts++;
/*     */       } 
/*     */     }
/* 183 */     return invalidRowCounts;
/*     */   }
/*     */   
/*     */   private void adjustRowNumbers(int barcodeColumn, int codewordsRow, Codeword[] codewords) {
/* 187 */     Codeword codeword = codewords[codewordsRow];
/*     */     
/* 189 */     Codeword[] previousColumnCodewords = this.detectionResultColumns[barcodeColumn - 1].getCodewords(), nextColumnCodewords = previousColumnCodewords;
/* 190 */     if (this.detectionResultColumns[barcodeColumn + 1] != null) {
/* 191 */       nextColumnCodewords = this.detectionResultColumns[barcodeColumn + 1].getCodewords();
/*     */     }
/*     */     
/*     */     Codeword[] otherCodewords;
/*     */     
/* 196 */     (otherCodewords = new Codeword[14])[2] = previousColumnCodewords[codewordsRow];
/* 197 */     otherCodewords[3] = nextColumnCodewords[codewordsRow];
/*     */     
/* 199 */     if (codewordsRow > 0) {
/* 200 */       otherCodewords[0] = codewords[codewordsRow - 1];
/* 201 */       otherCodewords[4] = previousColumnCodewords[codewordsRow - 1];
/* 202 */       otherCodewords[5] = nextColumnCodewords[codewordsRow - 1];
/*     */     } 
/* 204 */     if (codewordsRow > 1) {
/* 205 */       otherCodewords[8] = codewords[codewordsRow - 2];
/* 206 */       otherCodewords[10] = previousColumnCodewords[codewordsRow - 2];
/* 207 */       otherCodewords[11] = nextColumnCodewords[codewordsRow - 2];
/*     */     } 
/* 209 */     if (codewordsRow < codewords.length - 1) {
/* 210 */       otherCodewords[1] = codewords[codewordsRow + 1];
/* 211 */       otherCodewords[6] = previousColumnCodewords[codewordsRow + 1];
/* 212 */       otherCodewords[7] = nextColumnCodewords[codewordsRow + 1];
/*     */     } 
/* 214 */     if (codewordsRow < codewords.length - 2) {
/* 215 */       otherCodewords[9] = codewords[codewordsRow + 2];
/* 216 */       otherCodewords[12] = previousColumnCodewords[codewordsRow + 2];
/* 217 */       otherCodewords[13] = nextColumnCodewords[codewordsRow + 2];
/*     */     }  Codeword[] arrayOfCodeword1; byte b;
/* 219 */     for (arrayOfCodeword1 = otherCodewords, b = 0; b < 14; ) { Codeword otherCodeword = arrayOfCodeword1[b];
/* 220 */       if (adjustRowNumber(codeword, otherCodeword)) {
/*     */         return;
/*     */       }
/*     */       b++; }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean adjustRowNumber(Codeword codeword, Codeword otherCodeword) {
/* 230 */     if (otherCodeword == null) {
/* 231 */       return false;
/*     */     }
/* 233 */     if (otherCodeword.hasValidRowNumber() && otherCodeword.getBucket() == codeword.getBucket()) {
/* 234 */       codeword.setRowNumber(otherCodeword.getRowNumber());
/* 235 */       return true;
/*     */     } 
/* 237 */     return false;
/*     */   }
/*     */   
/*     */   int getBarcodeColumnCount() {
/* 241 */     return this.barcodeColumnCount;
/*     */   }
/*     */   
/*     */   int getBarcodeRowCount() {
/* 245 */     return this.barcodeMetadata.getRowCount();
/*     */   }
/*     */   
/*     */   int getBarcodeECLevel() {
/* 249 */     return this.barcodeMetadata.getErrorCorrectionLevel();
/*     */   }
/*     */   
/*     */   public void setBoundingBox(BoundingBox boundingBox) {
/* 253 */     this.boundingBox = boundingBox;
/*     */   }
/*     */   
/*     */   BoundingBox getBoundingBox() {
/* 257 */     return this.boundingBox;
/*     */   }
/*     */   
/*     */   void setDetectionResultColumn(int barcodeColumn, DetectionResultColumn detectionResultColumn) {
/* 261 */     this.detectionResultColumns[barcodeColumn] = detectionResultColumn;
/*     */   }
/*     */   
/*     */   DetectionResultColumn getDetectionResultColumn(int barcodeColumn) {
/* 265 */     return this.detectionResultColumns[barcodeColumn];
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*     */     DetectionResultColumn rowIndicatorColumn;
/* 271 */     if ((rowIndicatorColumn = this.detectionResultColumns[0]) == null) {
/* 272 */       rowIndicatorColumn = this.detectionResultColumns[this.barcodeColumnCount + 1];
/*     */     }
/* 274 */     Formatter formatter = new Formatter();
/* 275 */     for (int codewordsRow = 0; codewordsRow < (rowIndicatorColumn.getCodewords()).length; codewordsRow++) {
/* 276 */       formatter.format("CW %3d:", new Object[] { Integer.valueOf(codewordsRow) });
/* 277 */       for (int barcodeColumn = 0; barcodeColumn < this.barcodeColumnCount + 2; barcodeColumn++) {
/* 278 */         if (this.detectionResultColumns[barcodeColumn] == null) {
/* 279 */           formatter.format("    |   ", new Object[0]);
/*     */         } else {
/*     */           Codeword codeword;
/*     */           
/* 283 */           if ((codeword = this.detectionResultColumns[barcodeColumn].getCodewords()[codewordsRow]) == null)
/* 284 */           { formatter.format("    |   ", new Object[0]); }
/*     */           else
/*     */           
/* 287 */           { formatter.format(" %3d|%3d", new Object[] { Integer.valueOf(codeword.getRowNumber()), Integer.valueOf(codeword.getValue()) }); } 
/*     */         } 
/* 289 */       }  formatter.format("%n", new Object[0]);
/*     */     } 
/* 291 */     String result = formatter.toString();
/* 292 */     formatter.close();
/* 293 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\decoder\DetectionResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */