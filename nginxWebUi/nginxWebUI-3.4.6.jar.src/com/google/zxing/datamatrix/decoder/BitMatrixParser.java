/*     */ package com.google.zxing.datamatrix.decoder;
/*     */ 
/*     */ import com.google.zxing.FormatException;
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
/*     */ 
/*     */ final class BitMatrixParser
/*     */ {
/*     */   private final BitMatrix mappingBitMatrix;
/*     */   private final BitMatrix readMappingMatrix;
/*     */   private final Version version;
/*     */   
/*     */   BitMatrixParser(BitMatrix bitMatrix) throws FormatException {
/*     */     int dimension;
/*  37 */     if ((dimension = bitMatrix.getHeight()) < 8 || dimension > 144 || (dimension & 0x1) != 0) {
/*  38 */       throw FormatException.getFormatInstance();
/*     */     }
/*     */     
/*  41 */     this.version = readVersion(bitMatrix);
/*  42 */     this.mappingBitMatrix = extractDataRegion(bitMatrix);
/*  43 */     this.readMappingMatrix = new BitMatrix(this.mappingBitMatrix.getWidth(), this.mappingBitMatrix.getHeight());
/*     */   }
/*     */   
/*     */   Version getVersion() {
/*  47 */     return this.version;
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
/*     */   private static Version readVersion(BitMatrix bitMatrix) throws FormatException {
/*  62 */     int numRows = bitMatrix.getHeight();
/*  63 */     int numColumns = bitMatrix.getWidth();
/*  64 */     return Version.getVersionForDimensions(numRows, numColumns);
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
/*     */   byte[] readCodewords() throws FormatException {
/*  77 */     byte[] result = new byte[this.version.getTotalCodewords()];
/*  78 */     int resultOffset = 0;
/*     */     
/*  80 */     int row = 4;
/*  81 */     int column = 0;
/*     */     
/*  83 */     int numRows = this.mappingBitMatrix.getHeight();
/*  84 */     int numColumns = this.mappingBitMatrix.getWidth();
/*     */     
/*  86 */     boolean corner1Read = false;
/*  87 */     boolean corner2Read = false;
/*  88 */     boolean corner3Read = false;
/*  89 */     boolean corner4Read = false;
/*     */ 
/*     */ 
/*     */     
/*     */     do {
/*  94 */       if (row == numRows && column == 0 && !corner1Read) {
/*  95 */         result[resultOffset++] = (byte)readCorner1(numRows, numColumns);
/*  96 */         row -= 2;
/*  97 */         column += 2;
/*  98 */         corner1Read = true;
/*  99 */       } else if (row == numRows - 2 && column == 0 && (numColumns & 0x3) != 0 && !corner2Read) {
/* 100 */         result[resultOffset++] = (byte)readCorner2(numRows, numColumns);
/* 101 */         row -= 2;
/* 102 */         column += 2;
/* 103 */         corner2Read = true;
/* 104 */       } else if (row == numRows + 4 && column == 2 && (numColumns & 0x7) == 0 && !corner3Read) {
/* 105 */         result[resultOffset++] = (byte)readCorner3(numRows, numColumns);
/* 106 */         row -= 2;
/* 107 */         column += 2;
/* 108 */         corner3Read = true;
/* 109 */       } else if (row == numRows - 2 && column == 0 && (numColumns & 0x7) == 4 && !corner4Read) {
/* 110 */         result[resultOffset++] = (byte)readCorner4(numRows, numColumns);
/* 111 */         row -= 2;
/* 112 */         column += 2;
/* 113 */         corner4Read = true;
/*     */       } else {
/*     */         
/*     */         do {
/* 117 */           if (row < numRows && column >= 0 && !this.readMappingMatrix.get(column, row)) {
/* 118 */             result[resultOffset++] = (byte)readUtah(row, column, numRows, numColumns);
/*     */           }
/* 120 */           row -= 2;
/* 121 */           column += 2;
/* 122 */         } while (row >= 0 && column < numColumns);
/* 123 */         row++;
/* 124 */         column += 3;
/*     */ 
/*     */         
/*     */         do {
/* 128 */           if (row >= 0 && column < numColumns && !this.readMappingMatrix.get(column, row)) {
/* 129 */             result[resultOffset++] = (byte)readUtah(row, column, numRows, numColumns);
/*     */           }
/* 131 */           row += 2;
/* 132 */           column -= 2;
/* 133 */         } while (row < numRows && column >= 0);
/* 134 */         row += 3;
/* 135 */         column++;
/*     */       } 
/* 137 */     } while (row < numRows || column < numColumns);
/*     */     
/* 139 */     if (resultOffset != this.version.getTotalCodewords()) {
/* 140 */       throw FormatException.getFormatInstance();
/*     */     }
/* 142 */     return result;
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
/*     */   private boolean readModule(int row, int column, int numRows, int numColumns) {
/* 156 */     if (row < 0) {
/* 157 */       row += numRows;
/* 158 */       column += 4 - (numRows + 4 & 0x7);
/*     */     } 
/* 160 */     if (column < 0) {
/* 161 */       column += numColumns;
/* 162 */       row += 4 - (numColumns + 4 & 0x7);
/*     */     } 
/* 164 */     this.readMappingMatrix.set(column, row);
/* 165 */     return this.mappingBitMatrix.get(column, row);
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
/*     */   private int readUtah(int row, int column, int numRows, int numColumns) {
/* 180 */     int currentByte = 0;
/* 181 */     if (readModule(row - 2, column - 2, numRows, numColumns)) {
/* 182 */       currentByte = 1;
/*     */     }
/* 184 */     currentByte <<= 1;
/* 185 */     if (readModule(row - 2, column - 1, numRows, numColumns)) {
/* 186 */       currentByte |= 0x1;
/*     */     }
/* 188 */     currentByte <<= 1;
/* 189 */     if (readModule(row - 1, column - 2, numRows, numColumns)) {
/* 190 */       currentByte |= 0x1;
/*     */     }
/* 192 */     currentByte <<= 1;
/* 193 */     if (readModule(row - 1, column - 1, numRows, numColumns)) {
/* 194 */       currentByte |= 0x1;
/*     */     }
/* 196 */     currentByte <<= 1;
/* 197 */     if (readModule(row - 1, column, numRows, numColumns)) {
/* 198 */       currentByte |= 0x1;
/*     */     }
/* 200 */     currentByte <<= 1;
/* 201 */     if (readModule(row, column - 2, numRows, numColumns)) {
/* 202 */       currentByte |= 0x1;
/*     */     }
/* 204 */     currentByte <<= 1;
/* 205 */     if (readModule(row, column - 1, numRows, numColumns)) {
/* 206 */       currentByte |= 0x1;
/*     */     }
/* 208 */     currentByte <<= 1;
/* 209 */     if (readModule(row, column, numRows, numColumns)) {
/* 210 */       currentByte |= 0x1;
/*     */     }
/* 212 */     return currentByte;
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
/*     */   private int readCorner1(int numRows, int numColumns) {
/* 225 */     int currentByte = 0;
/* 226 */     if (readModule(numRows - 1, 0, numRows, numColumns)) {
/* 227 */       currentByte = 1;
/*     */     }
/* 229 */     currentByte <<= 1;
/* 230 */     if (readModule(numRows - 1, 1, numRows, numColumns)) {
/* 231 */       currentByte |= 0x1;
/*     */     }
/* 233 */     currentByte <<= 1;
/* 234 */     if (readModule(numRows - 1, 2, numRows, numColumns)) {
/* 235 */       currentByte |= 0x1;
/*     */     }
/* 237 */     currentByte <<= 1;
/* 238 */     if (readModule(0, numColumns - 2, numRows, numColumns)) {
/* 239 */       currentByte |= 0x1;
/*     */     }
/* 241 */     currentByte <<= 1;
/* 242 */     if (readModule(0, numColumns - 1, numRows, numColumns)) {
/* 243 */       currentByte |= 0x1;
/*     */     }
/* 245 */     currentByte <<= 1;
/* 246 */     if (readModule(1, numColumns - 1, numRows, numColumns)) {
/* 247 */       currentByte |= 0x1;
/*     */     }
/* 249 */     currentByte <<= 1;
/* 250 */     if (readModule(2, numColumns - 1, numRows, numColumns)) {
/* 251 */       currentByte |= 0x1;
/*     */     }
/* 253 */     currentByte <<= 1;
/* 254 */     if (readModule(3, numColumns - 1, numRows, numColumns)) {
/* 255 */       currentByte |= 0x1;
/*     */     }
/* 257 */     return currentByte;
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
/*     */   private int readCorner2(int numRows, int numColumns) {
/* 270 */     int currentByte = 0;
/* 271 */     if (readModule(numRows - 3, 0, numRows, numColumns)) {
/* 272 */       currentByte = 1;
/*     */     }
/* 274 */     currentByte <<= 1;
/* 275 */     if (readModule(numRows - 2, 0, numRows, numColumns)) {
/* 276 */       currentByte |= 0x1;
/*     */     }
/* 278 */     currentByte <<= 1;
/* 279 */     if (readModule(numRows - 1, 0, numRows, numColumns)) {
/* 280 */       currentByte |= 0x1;
/*     */     }
/* 282 */     currentByte <<= 1;
/* 283 */     if (readModule(0, numColumns - 4, numRows, numColumns)) {
/* 284 */       currentByte |= 0x1;
/*     */     }
/* 286 */     currentByte <<= 1;
/* 287 */     if (readModule(0, numColumns - 3, numRows, numColumns)) {
/* 288 */       currentByte |= 0x1;
/*     */     }
/* 290 */     currentByte <<= 1;
/* 291 */     if (readModule(0, numColumns - 2, numRows, numColumns)) {
/* 292 */       currentByte |= 0x1;
/*     */     }
/* 294 */     currentByte <<= 1;
/* 295 */     if (readModule(0, numColumns - 1, numRows, numColumns)) {
/* 296 */       currentByte |= 0x1;
/*     */     }
/* 298 */     currentByte <<= 1;
/* 299 */     if (readModule(1, numColumns - 1, numRows, numColumns)) {
/* 300 */       currentByte |= 0x1;
/*     */     }
/* 302 */     return currentByte;
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
/*     */   private int readCorner3(int numRows, int numColumns) {
/* 315 */     int currentByte = 0;
/* 316 */     if (readModule(numRows - 1, 0, numRows, numColumns)) {
/* 317 */       currentByte = 1;
/*     */     }
/* 319 */     currentByte <<= 1;
/* 320 */     if (readModule(numRows - 1, numColumns - 1, numRows, numColumns)) {
/* 321 */       currentByte |= 0x1;
/*     */     }
/* 323 */     currentByte <<= 1;
/* 324 */     if (readModule(0, numColumns - 3, numRows, numColumns)) {
/* 325 */       currentByte |= 0x1;
/*     */     }
/* 327 */     currentByte <<= 1;
/* 328 */     if (readModule(0, numColumns - 2, numRows, numColumns)) {
/* 329 */       currentByte |= 0x1;
/*     */     }
/* 331 */     currentByte <<= 1;
/* 332 */     if (readModule(0, numColumns - 1, numRows, numColumns)) {
/* 333 */       currentByte |= 0x1;
/*     */     }
/* 335 */     currentByte <<= 1;
/* 336 */     if (readModule(1, numColumns - 3, numRows, numColumns)) {
/* 337 */       currentByte |= 0x1;
/*     */     }
/* 339 */     currentByte <<= 1;
/* 340 */     if (readModule(1, numColumns - 2, numRows, numColumns)) {
/* 341 */       currentByte |= 0x1;
/*     */     }
/* 343 */     currentByte <<= 1;
/* 344 */     if (readModule(1, numColumns - 1, numRows, numColumns)) {
/* 345 */       currentByte |= 0x1;
/*     */     }
/* 347 */     return currentByte;
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
/*     */   private int readCorner4(int numRows, int numColumns) {
/* 360 */     int currentByte = 0;
/* 361 */     if (readModule(numRows - 3, 0, numRows, numColumns)) {
/* 362 */       currentByte = 1;
/*     */     }
/* 364 */     currentByte <<= 1;
/* 365 */     if (readModule(numRows - 2, 0, numRows, numColumns)) {
/* 366 */       currentByte |= 0x1;
/*     */     }
/* 368 */     currentByte <<= 1;
/* 369 */     if (readModule(numRows - 1, 0, numRows, numColumns)) {
/* 370 */       currentByte |= 0x1;
/*     */     }
/* 372 */     currentByte <<= 1;
/* 373 */     if (readModule(0, numColumns - 2, numRows, numColumns)) {
/* 374 */       currentByte |= 0x1;
/*     */     }
/* 376 */     currentByte <<= 1;
/* 377 */     if (readModule(0, numColumns - 1, numRows, numColumns)) {
/* 378 */       currentByte |= 0x1;
/*     */     }
/* 380 */     currentByte <<= 1;
/* 381 */     if (readModule(1, numColumns - 1, numRows, numColumns)) {
/* 382 */       currentByte |= 0x1;
/*     */     }
/* 384 */     currentByte <<= 1;
/* 385 */     if (readModule(2, numColumns - 1, numRows, numColumns)) {
/* 386 */       currentByte |= 0x1;
/*     */     }
/* 388 */     currentByte <<= 1;
/* 389 */     if (readModule(3, numColumns - 1, numRows, numColumns)) {
/* 390 */       currentByte |= 0x1;
/*     */     }
/* 392 */     return currentByte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BitMatrix extractDataRegion(BitMatrix bitMatrix) {
/* 403 */     int symbolSizeRows = this.version.getSymbolSizeRows();
/* 404 */     int symbolSizeColumns = this.version.getSymbolSizeColumns();
/*     */     
/* 406 */     if (bitMatrix.getHeight() != symbolSizeRows) {
/* 407 */       throw new IllegalArgumentException("Dimension of bitMarix must match the version size");
/*     */     }
/*     */     
/* 410 */     int dataRegionSizeRows = this.version.getDataRegionSizeRows();
/* 411 */     int dataRegionSizeColumns = this.version.getDataRegionSizeColumns();
/*     */     
/* 413 */     int numDataRegionsRow = symbolSizeRows / dataRegionSizeRows;
/* 414 */     int numDataRegionsColumn = symbolSizeColumns / dataRegionSizeColumns;
/*     */     
/* 416 */     int sizeDataRegionRow = numDataRegionsRow * dataRegionSizeRows;
/* 417 */     int sizeDataRegionColumn = numDataRegionsColumn * dataRegionSizeColumns;
/*     */     
/* 419 */     BitMatrix bitMatrixWithoutAlignment = new BitMatrix(sizeDataRegionColumn, sizeDataRegionRow);
/* 420 */     for (int dataRegionRow = 0; dataRegionRow < numDataRegionsRow; dataRegionRow++) {
/* 421 */       int dataRegionRowOffset = dataRegionRow * dataRegionSizeRows;
/* 422 */       for (int dataRegionColumn = 0; dataRegionColumn < numDataRegionsColumn; dataRegionColumn++) {
/* 423 */         int dataRegionColumnOffset = dataRegionColumn * dataRegionSizeColumns;
/* 424 */         for (int i = 0; i < dataRegionSizeRows; i++) {
/* 425 */           int readRowOffset = dataRegionRow * (dataRegionSizeRows + 2) + 1 + i;
/* 426 */           int writeRowOffset = dataRegionRowOffset + i;
/* 427 */           for (int j = 0; j < dataRegionSizeColumns; j++) {
/* 428 */             int readColumnOffset = dataRegionColumn * (dataRegionSizeColumns + 2) + 1 + j;
/* 429 */             if (bitMatrix.get(readColumnOffset, readRowOffset)) {
/* 430 */               int writeColumnOffset = dataRegionColumnOffset + j;
/* 431 */               bitMatrixWithoutAlignment.set(writeColumnOffset, writeRowOffset);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 437 */     return bitMatrixWithoutAlignment;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\decoder\BitMatrixParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */