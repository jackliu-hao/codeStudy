/*     */ package com.google.zxing.qrcode.encoder;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class MaskUtil
/*     */ {
/*     */   private static final int N1 = 3;
/*     */   private static final int N2 = 3;
/*     */   private static final int N3 = 40;
/*     */   private static final int N4 = 10;
/*     */   
/*     */   static int applyMaskPenaltyRule1(ByteMatrix matrix) {
/*  41 */     return applyMaskPenaltyRule1Internal(matrix, true) + applyMaskPenaltyRule1Internal(matrix, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int applyMaskPenaltyRule2(ByteMatrix matrix) {
/*  50 */     int penalty = 0;
/*  51 */     byte[][] array = matrix.getArray();
/*  52 */     int width = matrix.getWidth();
/*  53 */     int height = matrix.getHeight();
/*  54 */     for (int y = 0; y < height - 1; y++) {
/*  55 */       for (int x = 0; x < width - 1; x++) {
/*     */         int value;
/*  57 */         if ((value = array[y][x]) == array[y][x + 1] && value == array[y + 1][x] && value == array[y + 1][x + 1]) {
/*  58 */           penalty++;
/*     */         }
/*     */       } 
/*     */     } 
/*  62 */     return 3 * penalty;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int applyMaskPenaltyRule3(ByteMatrix matrix) {
/*  71 */     int numPenalties = 0;
/*  72 */     byte[][] array = matrix.getArray();
/*  73 */     int width = matrix.getWidth();
/*  74 */     int height = matrix.getHeight();
/*  75 */     for (int y = 0; y < height; y++) {
/*  76 */       for (int x = 0; x < width; x++) {
/*  77 */         byte[] arrayY = array[y];
/*  78 */         if (x + 6 < width && arrayY[x] == 1 && arrayY[x + 1] == 0 && arrayY[x + 2] == 1 && arrayY[x + 3] == 1 && arrayY[x + 4] == 1 && arrayY[x + 5] == 0 && arrayY[x + 6] == 1 && (
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  86 */           isWhiteHorizontal(arrayY, x - 4, x) || isWhiteHorizontal(arrayY, x + 7, x + 11))) {
/*  87 */           numPenalties++;
/*     */         }
/*  89 */         if (y + 6 < height && array[y][x] == 1 && array[y + 1][x] == 0 && array[y + 2][x] == 1 && array[y + 3][x] == 1 && array[y + 4][x] == 1 && array[y + 5][x] == 0 && array[y + 6][x] == 1 && (
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  97 */           isWhiteVertical(array, x, y - 4, y) || isWhiteVertical(array, x, y + 7, y + 11))) {
/*  98 */           numPenalties++;
/*     */         }
/*     */       } 
/*     */     } 
/* 102 */     return numPenalties * 40;
/*     */   }
/*     */   
/*     */   private static boolean isWhiteHorizontal(byte[] rowArray, int from, int to) {
/* 106 */     from = Math.max(from, 0);
/* 107 */     to = Math.min(to, rowArray.length);
/* 108 */     for (int i = from; i < to; i++) {
/* 109 */       if (rowArray[i] == 1) {
/* 110 */         return false;
/*     */       }
/*     */     } 
/* 113 */     return true;
/*     */   }
/*     */   
/*     */   private static boolean isWhiteVertical(byte[][] array, int col, int from, int to) {
/* 117 */     from = Math.max(from, 0);
/* 118 */     to = Math.min(to, array.length);
/* 119 */     for (int i = from; i < to; i++) {
/* 120 */       if (array[i][col] == 1) {
/* 121 */         return false;
/*     */       }
/*     */     } 
/* 124 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int applyMaskPenaltyRule4(ByteMatrix matrix) {
/* 132 */     int numDarkCells = 0;
/* 133 */     byte[][] array = matrix.getArray();
/* 134 */     int width = matrix.getWidth();
/* 135 */     int height = matrix.getHeight();
/* 136 */     for (int y = 0; y < height; y++) {
/* 137 */       byte[] arrayY = array[y];
/* 138 */       for (int x = 0; x < width; x++) {
/* 139 */         if (arrayY[x] == 1) {
/* 140 */           numDarkCells++;
/*     */         }
/*     */       } 
/*     */     } 
/* 144 */     int numTotalCells = matrix.getHeight() * matrix.getWidth();
/*     */     
/* 146 */     return Math.abs((numDarkCells << 1) - numTotalCells) * 10 / numTotalCells * 10;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean getDataMaskBit(int maskPattern, int x, int y) {
/*     */     int intermediate;
/*     */     int temp;
/* 156 */     switch (maskPattern) {
/*     */       case 0:
/* 158 */         intermediate = y + x & 0x1;
/*     */         break;
/*     */       case 1:
/* 161 */         intermediate = y & 0x1;
/*     */         break;
/*     */       case 2:
/* 164 */         intermediate = x % 3;
/*     */         break;
/*     */       case 3:
/* 167 */         intermediate = (y + x) % 3;
/*     */         break;
/*     */       case 4:
/* 170 */         intermediate = y / 2 + x / 3 & 0x1;
/*     */         break;
/*     */       
/*     */       case 5:
/* 174 */         intermediate = ((temp = y * x) & 0x1) + temp % 3;
/*     */         break;
/*     */       
/*     */       case 6:
/* 178 */         intermediate = ((temp = y * x) & 0x1) + temp % 3 & 0x1;
/*     */         break;
/*     */       
/*     */       case 7:
/* 182 */         intermediate = y * x % 3 + (y + x & 0x1) & 0x1;
/*     */         break;
/*     */       default:
/* 185 */         throw new IllegalArgumentException("Invalid mask pattern: " + maskPattern);
/*     */     } 
/* 187 */     return (intermediate == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int applyMaskPenaltyRule1Internal(ByteMatrix matrix, boolean isHorizontal) {
/* 195 */     int penalty = 0;
/* 196 */     int iLimit = isHorizontal ? matrix.getHeight() : matrix.getWidth();
/* 197 */     int jLimit = isHorizontal ? matrix.getWidth() : matrix.getHeight();
/* 198 */     byte[][] array = matrix.getArray();
/* 199 */     for (int i = 0; i < iLimit; i++) {
/* 200 */       int numSameBitCells = 0;
/* 201 */       int prevBit = -1;
/* 202 */       for (int j = 0; j < jLimit; j++) {
/*     */         int bit;
/* 204 */         if ((bit = isHorizontal ? array[i][j] : array[j][i]) == prevBit) {
/* 205 */           numSameBitCells++;
/*     */         } else {
/* 207 */           if (numSameBitCells >= 5) {
/* 208 */             penalty += 3 + numSameBitCells - 5;
/*     */           }
/* 210 */           numSameBitCells = 1;
/* 211 */           prevBit = bit;
/*     */         } 
/*     */       } 
/* 214 */       if (numSameBitCells >= 5) {
/* 215 */         penalty += 3 + numSameBitCells - 5;
/*     */       }
/*     */     } 
/* 218 */     return penalty;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\encoder\MaskUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */