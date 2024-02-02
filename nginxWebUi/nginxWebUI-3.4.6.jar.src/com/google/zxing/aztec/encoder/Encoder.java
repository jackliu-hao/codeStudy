/*     */ package com.google.zxing.aztec.encoder;
/*     */ 
/*     */ import com.google.zxing.common.BitArray;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.common.reedsolomon.GenericGF;
/*     */ import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
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
/*     */ public final class Encoder
/*     */ {
/*     */   public static final int DEFAULT_EC_PERCENT = 33;
/*     */   public static final int DEFAULT_AZTEC_LAYERS = 0;
/*     */   private static final int MAX_NB_BITS = 32;
/*     */   private static final int MAX_NB_BITS_COMPACT = 4;
/*  36 */   private static final int[] WORD_SIZE = new int[] { 4, 6, 6, 8, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12 };
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
/*     */   public static AztecCode encode(byte[] data) {
/*  51 */     return encode(data, 33, 0);
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
/*     */   public static AztecCode encode(byte[] data, int minECCPercent, int userSpecifiedLayers) {
/*     */     boolean compact;
/*     */     int layers, totalBitsInLayer, wordSize;
/*     */     BitArray stuffedBits;
/*     */     int matrixSize;
/*     */     BitArray bits;
/*  68 */     int eccBits = (bits = (new HighLevelEncoder(data)).encode()).getSize() * minECCPercent / 100 + 11;
/*  69 */     int totalSizeBits = bits.getSize() + eccBits;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  75 */     if (userSpecifiedLayers != 0) {
/*  76 */       compact = (userSpecifiedLayers < 0);
/*     */       
/*  78 */       if ((layers = Math.abs(userSpecifiedLayers)) > (compact ? 4 : 32)) {
/*  79 */         throw new IllegalArgumentException(
/*  80 */             String.format("Illegal value %s for layers", new Object[] { Integer.valueOf(userSpecifiedLayers) }));
/*     */       }
/*  82 */       totalBitsInLayer = totalBitsInLayer(layers, compact);
/*  83 */       wordSize = WORD_SIZE[layers];
/*  84 */       int usableBitsInLayers = totalBitsInLayer - totalBitsInLayer % wordSize;
/*     */       
/*  86 */       if ((stuffedBits = stuffBits(bits, wordSize)).getSize() + eccBits > usableBitsInLayers) {
/*  87 */         throw new IllegalArgumentException("Data to large for user specified layer");
/*     */       }
/*  89 */       if (compact && stuffedBits.getSize() > wordSize << 6)
/*     */       {
/*  91 */         throw new IllegalArgumentException("Data to large for user specified layer");
/*     */       }
/*     */     } else {
/*  94 */       wordSize = 0;
/*  95 */       stuffedBits = null;
/*     */ 
/*     */ 
/*     */       
/*  99 */       for (int j = 0;; j++) {
/* 100 */         if (j > 32) {
/* 101 */           throw new IllegalArgumentException("Data too large for an Aztec code");
/*     */         }
/*     */ 
/*     */         
/* 105 */         totalBitsInLayer = totalBitsInLayer(layers = (compact = (j <= 3)) ? (j + 1) : j, compact);
/* 106 */         if (totalSizeBits <= totalBitsInLayer) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 111 */           if (wordSize != WORD_SIZE[layers]) {
/* 112 */             wordSize = WORD_SIZE[layers];
/* 113 */             stuffedBits = stuffBits(bits, wordSize);
/*     */           } 
/* 115 */           int usableBitsInLayers = totalBitsInLayer - totalBitsInLayer % wordSize;
/* 116 */           if (!compact || stuffedBits.getSize() <= wordSize << 6) {
/*     */ 
/*     */ 
/*     */             
/* 120 */             if (stuffedBits.getSize() + eccBits > usableBitsInLayers)
/*     */               continue;  break;
/*     */           } 
/*     */         }  continue;
/*     */       } 
/* 125 */     }  BitArray messageBits = generateCheckWords(stuffedBits, totalBitsInLayer, wordSize);
/*     */ 
/*     */     
/* 128 */     int messageSizeInWords = stuffedBits.getSize() / wordSize;
/* 129 */     BitArray modeMessage = generateModeMessage(compact, layers, messageSizeInWords);
/*     */ 
/*     */ 
/*     */     
/* 133 */     int baseMatrixSize, alignmentMap[] = new int[baseMatrixSize = (compact ? 11 : 14) + (layers << 2)];
/*     */     
/* 135 */     if (compact) {
/*     */       
/* 137 */       matrixSize = baseMatrixSize;
/* 138 */       for (int j = 0; j < alignmentMap.length; j++) {
/* 139 */         alignmentMap[j] = j;
/*     */       }
/*     */     } else {
/* 142 */       matrixSize = baseMatrixSize + 1 + 2 * (baseMatrixSize / 2 - 1) / 15;
/* 143 */       int origCenter = baseMatrixSize / 2;
/* 144 */       int center = matrixSize / 2;
/* 145 */       for (int j = 0; j < origCenter; j++) {
/* 146 */         int newOffset = j + j / 15;
/* 147 */         alignmentMap[origCenter - j - 1] = center - newOffset - 1;
/* 148 */         alignmentMap[origCenter + j] = center + newOffset + 1;
/*     */       } 
/*     */     } 
/* 151 */     BitMatrix matrix = new BitMatrix(matrixSize);
/*     */     
/*     */     int i, rowOffset;
/* 154 */     for (i = 0, rowOffset = 0; i < layers; i++) {
/* 155 */       int rowSize = (layers - i << 2) + (compact ? 9 : 12);
/* 156 */       for (int j = 0; j < rowSize; j++) {
/* 157 */         int columnOffset = j << 1;
/* 158 */         for (int k = 0; k < 2; k++) {
/* 159 */           if (messageBits.get(rowOffset + columnOffset + k)) {
/* 160 */             matrix.set(alignmentMap[(i << 1) + k], alignmentMap[(i << 1) + j]);
/*     */           }
/* 162 */           if (messageBits.get(rowOffset + (rowSize << 1) + columnOffset + k)) {
/* 163 */             matrix.set(alignmentMap[(i << 1) + j], alignmentMap[baseMatrixSize - 1 - (i << 1) - k]);
/*     */           }
/* 165 */           if (messageBits.get(rowOffset + (rowSize << 2) + columnOffset + k)) {
/* 166 */             matrix.set(alignmentMap[baseMatrixSize - 1 - (i << 1) - k], alignmentMap[baseMatrixSize - 1 - (i << 1) - j]);
/*     */           }
/* 168 */           if (messageBits.get(rowOffset + rowSize * 6 + columnOffset + k)) {
/* 169 */             matrix.set(alignmentMap[baseMatrixSize - 1 - (i << 1) - j], alignmentMap[(i << 1) + k]);
/*     */           }
/*     */         } 
/*     */       } 
/* 173 */       rowOffset += rowSize << 3;
/*     */     } 
/*     */ 
/*     */     
/* 177 */     drawModeMessage(matrix, compact, matrixSize, modeMessage);
/*     */ 
/*     */     
/* 180 */     if (compact) {
/* 181 */       drawBullsEye(matrix, matrixSize / 2, 5);
/*     */     } else {
/* 183 */       drawBullsEye(matrix, matrixSize / 2, 7); int j;
/* 184 */       for (i = 0, j = 0; i < baseMatrixSize / 2 - 1; i += 15, j += 16) {
/* 185 */         for (int k = matrixSize / 2 & 0x1; k < matrixSize; k += 2) {
/* 186 */           matrix.set(matrixSize / 2 - j, k);
/* 187 */           matrix.set(matrixSize / 2 + j, k);
/* 188 */           matrix.set(k, matrixSize / 2 - j);
/* 189 */           matrix.set(k, matrixSize / 2 + j);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*     */     AztecCode aztec;
/* 195 */     (aztec = new AztecCode()).setCompact(compact);
/* 196 */     aztec.setSize(matrixSize);
/* 197 */     aztec.setLayers(layers);
/* 198 */     aztec.setCodeWords(messageSizeInWords);
/* 199 */     aztec.setMatrix(matrix);
/* 200 */     return aztec;
/*     */   }
/*     */   
/*     */   private static void drawBullsEye(BitMatrix matrix, int center, int size) {
/* 204 */     for (int i = 0; i < size; i += 2) {
/* 205 */       for (int j = center - i; j <= center + i; j++) {
/* 206 */         matrix.set(j, center - i);
/* 207 */         matrix.set(j, center + i);
/* 208 */         matrix.set(center - i, j);
/* 209 */         matrix.set(center + i, j);
/*     */       } 
/*     */     } 
/* 212 */     matrix.set(center - size, center - size);
/* 213 */     matrix.set(center - size + 1, center - size);
/* 214 */     matrix.set(center - size, center - size + 1);
/* 215 */     matrix.set(center + size, center - size);
/* 216 */     matrix.set(center + size, center - size + 1);
/* 217 */     matrix.set(center + size, center + size - 1);
/*     */   }
/*     */   
/*     */   static BitArray generateModeMessage(boolean compact, int layers, int messageSizeInWords) {
/* 221 */     BitArray modeMessage = new BitArray();
/* 222 */     if (compact) {
/* 223 */       modeMessage.appendBits(layers - 1, 2);
/* 224 */       modeMessage.appendBits(messageSizeInWords - 1, 6);
/* 225 */       modeMessage = generateCheckWords(modeMessage, 28, 4);
/*     */     } else {
/* 227 */       modeMessage.appendBits(layers - 1, 5);
/* 228 */       modeMessage.appendBits(messageSizeInWords - 1, 11);
/* 229 */       modeMessage = generateCheckWords(modeMessage, 40, 4);
/*     */     } 
/* 231 */     return modeMessage;
/*     */   }
/*     */   
/*     */   private static void drawModeMessage(BitMatrix matrix, boolean compact, int matrixSize, BitArray modeMessage) {
/* 235 */     int center = matrixSize / 2;
/* 236 */     if (compact) {
/* 237 */       for (int j = 0; j < 7; j++) {
/* 238 */         int offset = center - 3 + j;
/* 239 */         if (modeMessage.get(j)) {
/* 240 */           matrix.set(offset, center - 5);
/*     */         }
/* 242 */         if (modeMessage.get(j + 7)) {
/* 243 */           matrix.set(center + 5, offset);
/*     */         }
/* 245 */         if (modeMessage.get(20 - j)) {
/* 246 */           matrix.set(offset, center + 5);
/*     */         }
/* 248 */         if (modeMessage.get(27 - j))
/* 249 */           matrix.set(center - 5, offset); 
/*     */       } 
/*     */       return;
/*     */     } 
/* 253 */     for (int i = 0; i < 10; i++) {
/* 254 */       int offset = center - 5 + i + i / 5;
/* 255 */       if (modeMessage.get(i)) {
/* 256 */         matrix.set(offset, center - 7);
/*     */       }
/* 258 */       if (modeMessage.get(i + 10)) {
/* 259 */         matrix.set(center + 7, offset);
/*     */       }
/* 261 */       if (modeMessage.get(29 - i)) {
/* 262 */         matrix.set(offset, center + 7);
/*     */       }
/* 264 */       if (modeMessage.get(39 - i)) {
/* 265 */         matrix.set(center - 7, offset);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static BitArray generateCheckWords(BitArray bitArray, int totalBits, int wordSize) {
/* 273 */     int messageSizeInWords = bitArray.getSize() / wordSize;
/* 274 */     ReedSolomonEncoder rs = new ReedSolomonEncoder(getGF(wordSize));
/* 275 */     int totalWords = totalBits / wordSize;
/* 276 */     int[] messageWords = bitsToWords(bitArray, wordSize, totalWords);
/* 277 */     rs.encode(messageWords, totalWords - messageSizeInWords);
/* 278 */     int startPad = totalBits % wordSize;
/*     */     BitArray messageBits;
/* 280 */     (messageBits = new BitArray()).appendBits(0, startPad); int arrayOfInt1[], i; byte b;
/* 281 */     for (i = (arrayOfInt1 = messageWords).length, b = 0; b < i; ) { int messageWord = arrayOfInt1[b];
/* 282 */       messageBits.appendBits(messageWord, wordSize); b++; }
/*     */     
/* 284 */     return messageBits;
/*     */   }
/*     */   
/*     */   private static int[] bitsToWords(BitArray stuffedBits, int wordSize, int totalWords) {
/* 288 */     int[] message = new int[totalWords];
/*     */ 
/*     */     
/* 291 */     for (int i = 0, n = stuffedBits.getSize() / wordSize; i < n; i++) {
/* 292 */       int value = 0;
/* 293 */       for (int j = 0; j < wordSize; j++) {
/* 294 */         value |= stuffedBits.get(i * wordSize + j) ? (1 << wordSize - j - 1) : 0;
/*     */       }
/* 296 */       message[i] = value;
/*     */     } 
/* 298 */     return message;
/*     */   }
/*     */   
/*     */   private static GenericGF getGF(int wordSize) {
/* 302 */     switch (wordSize) {
/*     */       case 4:
/* 304 */         return GenericGF.AZTEC_PARAM;
/*     */       case 6:
/* 306 */         return GenericGF.AZTEC_DATA_6;
/*     */       case 8:
/* 308 */         return GenericGF.AZTEC_DATA_8;
/*     */       case 10:
/* 310 */         return GenericGF.AZTEC_DATA_10;
/*     */       case 12:
/* 312 */         return GenericGF.AZTEC_DATA_12;
/*     */     } 
/* 314 */     throw new IllegalArgumentException("Unsupported word size " + wordSize);
/*     */   }
/*     */ 
/*     */   
/*     */   static BitArray stuffBits(BitArray bits, int wordSize) {
/* 319 */     BitArray out = new BitArray();
/*     */     
/* 321 */     int n = bits.getSize();
/* 322 */     int mask = (1 << wordSize) - 2; int i;
/* 323 */     for (i = 0; i < n; i += wordSize) {
/* 324 */       int word = 0;
/* 325 */       for (int j = 0; j < wordSize; j++) {
/* 326 */         if (i + j >= n || bits.get(i + j)) {
/* 327 */           word |= 1 << wordSize - 1 - j;
/*     */         }
/*     */       } 
/* 330 */       if ((word & mask) == mask) {
/* 331 */         out.appendBits(word & mask, wordSize);
/* 332 */         i--;
/* 333 */       } else if ((word & mask) == 0) {
/* 334 */         out.appendBits(word | 0x1, wordSize);
/* 335 */         i--;
/*     */       } else {
/* 337 */         out.appendBits(word, wordSize);
/*     */       } 
/*     */     } 
/* 340 */     return out;
/*     */   }
/*     */   
/*     */   private static int totalBitsInLayer(int layers, boolean compact) {
/* 344 */     return ((compact ? 88 : 112) + (layers << 4)) * layers;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\aztec\encoder\Encoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */