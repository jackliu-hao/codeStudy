/*     */ package com.google.zxing.aztec.decoder;
/*     */ 
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.aztec.AztecDetectorResult;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.common.DecoderResult;
/*     */ import com.google.zxing.common.reedsolomon.GenericGF;
/*     */ import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
/*     */ import com.google.zxing.common.reedsolomon.ReedSolomonException;
/*     */ import java.util.Arrays;
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
/*     */ public final class Decoder
/*     */ {
/*     */   private enum Table
/*     */   {
/*  38 */     UPPER,
/*  39 */     LOWER,
/*  40 */     MIXED,
/*  41 */     DIGIT,
/*  42 */     PUNCT,
/*  43 */     BINARY;
/*     */   }
/*     */   
/*  46 */   private static final String[] UPPER_TABLE = new String[] { "CTRL_PS", " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "CTRL_LL", "CTRL_ML", "CTRL_DL", "CTRL_BS" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   private static final String[] LOWER_TABLE = new String[] { "CTRL_PS", " ", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "CTRL_US", "CTRL_ML", "CTRL_DL", "CTRL_BS" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   private static final String[] MIXED_TABLE = new String[] { "CTRL_PS", " ", "\001", "\002", "\003", "\004", "\005", "\006", "\007", "\b", "\t", "\n", "\013", "\f", "\r", "\033", "\034", "\035", "\036", "\037", "@", "\\", "^", "_", "`", "|", "~", "", "CTRL_LL", "CTRL_UL", "CTRL_PL", "CTRL_BS" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  62 */   private static final String[] PUNCT_TABLE = new String[] { "", "\r", "\r\n", ". ", ", ", ": ", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/", ":", ";", "<", "=", ">", "?", "[", "]", "{", "}", "CTRL_UL" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   private static final String[] DIGIT_TABLE = new String[] { "CTRL_PS", " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ",", ".", "CTRL_UL", "CTRL_US" };
/*     */ 
/*     */   
/*     */   private AztecDetectorResult ddata;
/*     */ 
/*     */   
/*     */   public DecoderResult decode(AztecDetectorResult detectorResult) throws FormatException {
/*  74 */     this.ddata = detectorResult;
/*  75 */     BitMatrix matrix = detectorResult.getBits();
/*  76 */     boolean[] rawbits = extractBits(matrix);
/*     */     boolean[] correctedBits;
/*  78 */     byte[] rawBytes = convertBoolArrayToByteArray(correctedBits = correctBits(rawbits));
/*  79 */     String result = getEncodedData(correctedBits);
/*     */     DecoderResult decoderResult;
/*  81 */     (decoderResult = new DecoderResult(rawBytes, result, null, null)).setNumBits(correctedBits.length);
/*  82 */     return decoderResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String highLevelDecode(boolean[] correctedBits) {
/*  87 */     return getEncodedData(correctedBits);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getEncodedData(boolean[] correctedBits) {
/*  96 */     int endIndex = correctedBits.length;
/*  97 */     Table latchTable = Table.UPPER;
/*  98 */     Table shiftTable = Table.UPPER;
/*  99 */     StringBuilder result = new StringBuilder(20);
/* 100 */     int index = 0;
/* 101 */     while (index < endIndex) {
/* 102 */       if (shiftTable == Table.BINARY) {
/* 103 */         if (endIndex - index >= 5) {
/*     */ 
/*     */           
/* 106 */           int length = readCode(correctedBits, index, 5);
/* 107 */           index += 5;
/* 108 */           if (length == 0)
/* 109 */             if (endIndex - index >= 11)
/*     */             
/*     */             { 
/* 112 */               length = readCode(correctedBits, index, 11) + 31;
/* 113 */               index += 11; }
/*     */             else { break; }
/* 115 */               for (int charCount = 0; charCount < length; charCount++) {
/* 116 */             if (endIndex - index < 8) {
/* 117 */               index = endIndex;
/*     */               break;
/*     */             } 
/* 120 */             int code = readCode(correctedBits, index, 8);
/* 121 */             result.append((char)code);
/* 122 */             index += 8;
/*     */           } 
/*     */           
/* 125 */           shiftTable = latchTable; continue;
/*     */         }  break;
/* 127 */       }  int size = (shiftTable == Table.DIGIT) ? 4 : 5;
/* 128 */       if (endIndex - index >= size) {
/*     */ 
/*     */         
/* 131 */         int code = readCode(correctedBits, index, size);
/* 132 */         index += size;
/*     */         String str;
/* 134 */         if ((str = getCharacter(shiftTable, code)).startsWith("CTRL_")) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 139 */           latchTable = shiftTable;
/* 140 */           shiftTable = getTable(str.charAt(5));
/* 141 */           if (str.charAt(6) == 'L')
/* 142 */             latchTable = shiftTable; 
/*     */           continue;
/*     */         } 
/* 145 */         result.append(str);
/*     */         
/* 147 */         shiftTable = latchTable;
/*     */       } 
/*     */     } 
/*     */     
/* 151 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Table getTable(char t) {
/* 158 */     switch (t) {
/*     */       case 'L':
/* 160 */         return Table.LOWER;
/*     */       case 'P':
/* 162 */         return Table.PUNCT;
/*     */       case 'M':
/* 164 */         return Table.MIXED;
/*     */       case 'D':
/* 166 */         return Table.DIGIT;
/*     */       case 'B':
/* 168 */         return Table.BINARY;
/*     */     } 
/*     */     
/* 171 */     return Table.UPPER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getCharacter(Table table, int code) {
/* 182 */     switch (table) {
/*     */       case UPPER:
/* 184 */         return UPPER_TABLE[code];
/*     */       case LOWER:
/* 186 */         return LOWER_TABLE[code];
/*     */       case MIXED:
/* 188 */         return MIXED_TABLE[code];
/*     */       case PUNCT:
/* 190 */         return PUNCT_TABLE[code];
/*     */       case DIGIT:
/* 192 */         return DIGIT_TABLE[code];
/*     */     } 
/*     */     
/* 195 */     throw new IllegalStateException("Bad table");
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
/*     */   private boolean[] correctBits(boolean[] rawbits) throws FormatException {
/*     */     GenericGF gf;
/*     */     int codewordSize;
/* 209 */     if (this.ddata.getNbLayers() <= 2) {
/* 210 */       codewordSize = 6;
/* 211 */       gf = GenericGF.AZTEC_DATA_6;
/* 212 */     } else if (this.ddata.getNbLayers() <= 8) {
/* 213 */       codewordSize = 8;
/* 214 */       gf = GenericGF.AZTEC_DATA_8;
/* 215 */     } else if (this.ddata.getNbLayers() <= 22) {
/* 216 */       codewordSize = 10;
/* 217 */       gf = GenericGF.AZTEC_DATA_10;
/*     */     } else {
/* 219 */       codewordSize = 12;
/* 220 */       gf = GenericGF.AZTEC_DATA_12;
/*     */     } 
/*     */     
/* 223 */     int numDataCodewords = this.ddata.getNbDatablocks();
/*     */     int numCodewords;
/* 225 */     if ((numCodewords = rawbits.length / codewordSize) < numDataCodewords) {
/* 226 */       throw FormatException.getFormatInstance();
/*     */     }
/* 228 */     int offset = rawbits.length % codewordSize;
/*     */     
/* 230 */     int[] dataWords = new int[numCodewords];
/* 231 */     for (int i = 0; i < numCodewords; i++, offset += codewordSize) {
/* 232 */       dataWords[i] = readCode(rawbits, offset, codewordSize);
/*     */     }
/*     */     
/*     */     try {
/* 236 */       (new ReedSolomonDecoder(gf))
/* 237 */         .decode(dataWords, numCodewords - numDataCodewords);
/*     */     } catch (ReedSolomonException reedSolomonException) {
/* 239 */       throw FormatException.getFormatInstance(null);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 244 */     int mask = (1 << codewordSize) - 1;
/* 245 */     int stuffedBits = 0;
/* 246 */     for (int j = 0; j < numDataCodewords; j++) {
/*     */       int dataWord;
/* 248 */       if ((dataWord = dataWords[j]) == 0 || dataWord == mask)
/* 249 */         throw FormatException.getFormatInstance(); 
/* 250 */       if (dataWord == 1 || dataWord == mask - 1) {
/* 251 */         stuffedBits++;
/*     */       }
/*     */     } 
/*     */     
/* 255 */     boolean[] correctedBits = new boolean[numDataCodewords * codewordSize - stuffedBits];
/* 256 */     int index = 0;
/* 257 */     for (int k = 0; k < numDataCodewords; k++) {
/*     */       int dataWord;
/* 259 */       if ((dataWord = dataWords[k]) == 1 || dataWord == mask - 1) {
/*     */         
/* 261 */         Arrays.fill(correctedBits, index, index + codewordSize - 1, (dataWord > 1));
/* 262 */         index += codewordSize - 1;
/*     */       } else {
/* 264 */         for (int bit = codewordSize - 1; bit >= 0; bit--) {
/* 265 */           correctedBits[index++] = ((dataWord & 1 << bit) != 0);
/*     */         }
/*     */       } 
/*     */     } 
/* 269 */     return correctedBits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean[] extractBits(BitMatrix matrix) {
/* 278 */     boolean compact = this.ddata.isCompact();
/* 279 */     int layers = this.ddata.getNbLayers();
/*     */     
/* 281 */     int baseMatrixSize, alignmentMap[] = new int[baseMatrixSize = (compact ? 11 : 14) + (layers << 2)];
/* 282 */     boolean[] rawbits = new boolean[totalBitsInLayer(layers, compact)];
/*     */     
/* 284 */     if (compact) {
/* 285 */       for (int j = 0; j < alignmentMap.length; j++) {
/* 286 */         alignmentMap[j] = j;
/*     */       }
/*     */     } else {
/* 289 */       int matrixSize = baseMatrixSize + 1 + 2 * (baseMatrixSize / 2 - 1) / 15;
/* 290 */       int origCenter = baseMatrixSize / 2;
/* 291 */       int center = matrixSize / 2;
/* 292 */       for (int j = 0; j < origCenter; j++) {
/* 293 */         int newOffset = j + j / 15;
/* 294 */         alignmentMap[origCenter - j - 1] = center - newOffset - 1;
/* 295 */         alignmentMap[origCenter + j] = center + newOffset + 1;
/*     */       } 
/*     */     } 
/* 298 */     for (int i = 0, rowOffset = 0; i < layers; i++) {
/* 299 */       int rowSize = (layers - i << 2) + (compact ? 9 : 12);
/*     */       
/* 301 */       int low = i << 1;
/*     */       
/* 303 */       int high = baseMatrixSize - 1 - low;
/*     */       
/* 305 */       for (int j = 0; j < rowSize; j++) {
/* 306 */         int columnOffset = j << 1;
/* 307 */         for (int k = 0; k < 2; k++) {
/*     */           
/* 309 */           rawbits[rowOffset + columnOffset + k] = matrix
/* 310 */             .get(alignmentMap[low + k], alignmentMap[low + j]);
/*     */           
/* 312 */           rawbits[rowOffset + 2 * rowSize + columnOffset + k] = matrix
/* 313 */             .get(alignmentMap[low + j], alignmentMap[high - k]);
/*     */           
/* 315 */           rawbits[rowOffset + 4 * rowSize + columnOffset + k] = matrix
/* 316 */             .get(alignmentMap[high - k], alignmentMap[high - j]);
/*     */           
/* 318 */           rawbits[rowOffset + rowSize * 6 + columnOffset + k] = matrix
/* 319 */             .get(alignmentMap[high - j], alignmentMap[low + k]);
/*     */         } 
/*     */       } 
/* 322 */       rowOffset += rowSize << 3;
/*     */     } 
/* 324 */     return rawbits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int readCode(boolean[] rawbits, int startIndex, int length) {
/* 331 */     int res = 0;
/* 332 */     for (int i = startIndex; i < startIndex + length; i++) {
/* 333 */       res <<= 1;
/* 334 */       if (rawbits[i]) {
/* 335 */         res |= 0x1;
/*     */       }
/*     */     } 
/* 338 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte readByte(boolean[] rawbits, int startIndex) {
/*     */     int n;
/* 346 */     if ((n = rawbits.length - startIndex) >= 8) {
/* 347 */       return (byte)readCode(rawbits, startIndex, 8);
/*     */     }
/* 349 */     return (byte)(readCode(rawbits, startIndex, n) << 8 - n);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static byte[] convertBoolArrayToByteArray(boolean[] boolArr) {
/* 356 */     byte[] byteArr = new byte[(boolArr.length + 7) / 8];
/* 357 */     for (int i = 0; i < byteArr.length; i++) {
/* 358 */       byteArr[i] = readByte(boolArr, i << 3);
/*     */     }
/* 360 */     return byteArr;
/*     */   }
/*     */   
/*     */   private static int totalBitsInLayer(int layers, boolean compact) {
/* 364 */     return ((compact ? 88 : 112) + (layers << 4)) * layers;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\aztec\decoder\Decoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */