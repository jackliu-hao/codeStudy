/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BinaryBitmap;
/*     */ import com.google.zxing.ChecksumException;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.Reader;
/*     */ import com.google.zxing.ReaderException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultMetadataType;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.common.BitArray;
/*     */ import java.util.Arrays;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
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
/*     */ public abstract class OneDReader
/*     */   implements Reader
/*     */ {
/*     */   public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
/*  46 */     return decode(image, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
/*     */     try {
/*  54 */       return doDecode(image, hints);
/*  55 */     } catch (NotFoundException nfe) {
/*  56 */       if (((hints != null && hints.containsKey(DecodeHintType.TRY_HARDER))) && 
/*  57 */         image.isRotateSupported()) {
/*  58 */         BinaryBitmap rotatedImage = image.rotateCounterClockwise();
/*     */         
/*     */         Result result;
/*  61 */         Map<ResultMetadataType, ?> metadata = (result = doDecode(rotatedImage, hints)).getResultMetadata();
/*  62 */         int orientation = 270;
/*  63 */         if (metadata != null && metadata.containsKey(ResultMetadataType.ORIENTATION))
/*     */         {
/*     */           
/*  66 */           orientation = (270 + ((Integer)metadata.get(ResultMetadataType.ORIENTATION)).intValue()) % 360;
/*     */         }
/*  68 */         result.putMetadata(ResultMetadataType.ORIENTATION, Integer.valueOf(orientation));
/*     */         
/*     */         ResultPoint[] points;
/*  71 */         if ((points = result.getResultPoints()) != null) {
/*  72 */           int height = rotatedImage.getHeight();
/*  73 */           for (int i = 0; i < points.length; i++) {
/*  74 */             points[i] = new ResultPoint(height - points[i].getY() - 1.0F, points[i].getX());
/*     */           }
/*     */         } 
/*  77 */         return result;
/*     */       } 
/*  79 */       throw nfe;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {}
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
/*     */   private Result doDecode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
/* 105 */     int maxLines, width = image.getWidth();
/* 106 */     int height = image.getHeight();
/* 107 */     BitArray row = new BitArray(width);
/*     */     
/* 109 */     int middle = height >> 1;
/* 110 */     boolean tryHarder = (hints != null && hints.containsKey(DecodeHintType.TRY_HARDER));
/* 111 */     int rowStep = Math.max(1, height >> (tryHarder ? 8 : 5));
/*     */     
/* 113 */     if (tryHarder) {
/* 114 */       maxLines = height;
/*     */     } else {
/* 116 */       maxLines = 15;
/*     */     } 
/*     */     
/* 119 */     for (int x = 0; x < maxLines; ) {
/*     */ 
/*     */       
/* 122 */       int rowStepsAboveOrBelow = (x + 1) / 2;
/* 123 */       boolean isAbove = ((x & 0x1) == 0);
/*     */       int rowNumber;
/* 125 */       if ((rowNumber = middle + rowStep * (isAbove ? rowStepsAboveOrBelow : -rowStepsAboveOrBelow)) >= 0 && rowNumber < height) {
/*     */ 
/*     */         
/*     */         try {
/*     */ 
/*     */ 
/*     */           
/* 132 */           row = image.getBlackRow(rowNumber, row);
/* 133 */         } catch (NotFoundException notFoundException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 139 */         for (int attempt = 0; attempt < 2; attempt++) {
/* 140 */           if (attempt == 1) {
/* 141 */             row.reverse();
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 146 */             if (hints != null && hints.containsKey(DecodeHintType.NEED_RESULT_POINT_CALLBACK)) {
/*     */               Map<DecodeHintType, Object> newHints;
/* 148 */               (newHints = new EnumMap<>(DecodeHintType.class)).putAll(hints);
/* 149 */               newHints.remove(DecodeHintType.NEED_RESULT_POINT_CALLBACK);
/* 150 */               hints = newHints;
/*     */             } 
/*     */           } 
/*     */           
/*     */           try {
/* 155 */             Result result = decodeRow(rowNumber, row, hints);
/*     */             
/* 157 */             if (attempt == 1) {
/*     */               
/* 159 */               result.putMetadata(ResultMetadataType.ORIENTATION, Integer.valueOf(180));
/*     */               
/*     */               ResultPoint[] points;
/* 162 */               if ((points = result.getResultPoints()) != null) {
/* 163 */                 points[0] = new ResultPoint(width - points[0].getX() - 1.0F, points[0].getY());
/* 164 */                 points[1] = new ResultPoint(width - points[1].getX() - 1.0F, points[1].getY());
/*     */               } 
/*     */             } 
/* 167 */             return result;
/* 168 */           } catch (ReaderException readerException) {}
/*     */         } 
/*     */         
/*     */         x++;
/*     */       } 
/*     */     } 
/* 174 */     throw NotFoundException.getNotFoundInstance();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static void recordPattern(BitArray row, int start, int[] counters) throws NotFoundException {
/* 193 */     int numCounters = counters.length;
/* 194 */     Arrays.fill(counters, 0, numCounters, 0);
/* 195 */     int end = row.getSize();
/* 196 */     if (start >= end) {
/* 197 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/* 199 */     boolean isWhite = !row.get(start);
/* 200 */     int counterPosition = 0;
/* 201 */     int i = start;
/* 202 */     while (i < end) {
/* 203 */       if (row.get(i) ^ isWhite) {
/* 204 */         counters[counterPosition] = counters[counterPosition] + 1;
/*     */       } else {
/* 206 */         counterPosition++;
/* 207 */         if (counterPosition != numCounters)
/*     */         
/*     */         { 
/* 210 */           counters[counterPosition] = 1;
/* 211 */           isWhite = !isWhite; }
/*     */         else { break; }
/*     */       
/* 214 */       }  i++;
/*     */     } 
/*     */ 
/*     */     
/* 218 */     if (counterPosition != numCounters && (counterPosition != numCounters - 1 || i != end)) {
/* 219 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected static void recordPatternInReverse(BitArray row, int start, int[] counters) throws NotFoundException {
/* 226 */     int numTransitionsLeft = counters.length;
/* 227 */     boolean last = row.get(start);
/* 228 */     while (start > 0 && numTransitionsLeft >= 0) {
/* 229 */       if (row.get(--start) != last) {
/* 230 */         numTransitionsLeft--;
/* 231 */         last = !last;
/*     */       } 
/*     */     } 
/* 234 */     if (numTransitionsLeft >= 0) {
/* 235 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/* 237 */     recordPattern(row, start + 1, counters);
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
/*     */   
/*     */   protected static float patternMatchVariance(int[] counters, int[] pattern, float maxIndividualVariance) {
/* 253 */     int numCounters = counters.length;
/* 254 */     int total = 0;
/* 255 */     int patternLength = 0;
/* 256 */     for (int i = 0; i < numCounters; i++) {
/* 257 */       total += counters[i];
/* 258 */       patternLength += pattern[i];
/*     */     } 
/* 260 */     if (total < patternLength)
/*     */     {
/*     */       
/* 263 */       return Float.POSITIVE_INFINITY;
/*     */     }
/*     */     
/* 266 */     float unitBarWidth = total / patternLength;
/* 267 */     maxIndividualVariance *= unitBarWidth;
/*     */     
/* 269 */     float totalVariance = 0.0F;
/* 270 */     for (int x = 0; x < numCounters; x++) {
/* 271 */       int counter = counters[x];
/* 272 */       float scaledPattern = pattern[x] * unitBarWidth;
/*     */       float variance;
/* 274 */       if ((variance = (counter > scaledPattern) ? (counter - scaledPattern) : (scaledPattern - counter)) > maxIndividualVariance) {
/* 275 */         return Float.POSITIVE_INFINITY;
/*     */       }
/* 277 */       totalVariance += variance;
/*     */     } 
/* 279 */     return totalVariance / total;
/*     */   }
/*     */   
/*     */   public abstract Result decodeRow(int paramInt, BitArray paramBitArray, Map<DecodeHintType, ?> paramMap) throws NotFoundException, ChecksumException, FormatException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\OneDReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */