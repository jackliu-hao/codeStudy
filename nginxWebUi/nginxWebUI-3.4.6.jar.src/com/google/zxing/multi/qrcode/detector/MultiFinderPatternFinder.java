/*     */ package com.google.zxing.multi.qrcode.detector;
/*     */ 
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.ResultPointCallback;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.qrcode.detector.FinderPattern;
/*     */ import com.google.zxing.qrcode.detector.FinderPatternFinder;
/*     */ import com.google.zxing.qrcode.detector.FinderPatternInfo;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class MultiFinderPatternFinder
/*     */   extends FinderPatternFinder
/*     */ {
/*  51 */   private static final FinderPatternInfo[] EMPTY_RESULT_ARRAY = new FinderPatternInfo[0];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final float MAX_MODULE_COUNT_PER_EDGE = 180.0F;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final float MIN_MODULE_COUNT_PER_EDGE = 9.0F;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final float DIFF_MODSIZE_CUTOFF_PERCENT = 0.05F;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final float DIFF_MODSIZE_CUTOFF = 0.5F;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class ModuleSizeComparator
/*     */     implements Serializable, Comparator<FinderPattern>
/*     */   {
/*     */     private ModuleSizeComparator() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int compare(FinderPattern center1, FinderPattern center2) {
/*     */       float value;
/*  83 */       return ((value = center2.getEstimatedModuleSize() - center1.getEstimatedModuleSize()) < 0.0D) ? -1 : ((value > 0.0D) ? 1 : 0);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   MultiFinderPatternFinder(BitMatrix image) {
/*  93 */     super(image);
/*     */   }
/*     */   
/*     */   MultiFinderPatternFinder(BitMatrix image, ResultPointCallback resultPointCallback) {
/*  97 */     super(image, resultPointCallback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private FinderPattern[][] selectMutipleBestPatterns() throws NotFoundException {
/*     */     List<FinderPattern> possibleCenters;
/*     */     int size;
/* 110 */     if ((size = (possibleCenters = getPossibleCenters()).size()) < 3)
/*     */     {
/* 112 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 118 */     if (size == 3) {
/* 119 */       return new FinderPattern[][] { { possibleCenters
/*     */             
/* 121 */             .get(0), possibleCenters
/* 122 */             .get(1), possibleCenters
/* 123 */             .get(2) } };
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 129 */     Collections.sort(possibleCenters, new ModuleSizeComparator());
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
/* 146 */     List<FinderPattern[]> results = (List)new ArrayList<>();
/*     */     
/* 148 */     for (int i1 = 0; i1 < size - 2; i1++) {
/*     */       FinderPattern p1;
/* 150 */       if ((p1 = possibleCenters.get(i1)) != null)
/*     */       {
/*     */ 
/*     */         
/* 154 */         for (int i2 = i1 + 1; i2 < size - 1; i2++) {
/*     */           FinderPattern p2;
/* 156 */           if ((p2 = possibleCenters.get(i2)) != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 162 */             float vModSize12 = (p1.getEstimatedModuleSize() - p2.getEstimatedModuleSize()) / Math.min(p1.getEstimatedModuleSize(), p2.getEstimatedModuleSize());
/* 163 */             if (Math.abs(p1.getEstimatedModuleSize() - p2.getEstimatedModuleSize()) <= 
/* 164 */               0.5F || vModSize12 < 0.05F)
/*     */             
/*     */             { 
/*     */ 
/*     */ 
/*     */               
/* 170 */               for (int i3 = i2 + 1; i3 < size; i3++)
/*     */               { FinderPattern p3;
/* 172 */                 if ((p3 = possibleCenters.get(i3)) != null)
/*     */                 
/*     */                 { 
/*     */ 
/*     */ 
/*     */                   
/* 178 */                   float vModSize23 = (p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize()) / Math.min(p2.getEstimatedModuleSize(), p3.getEstimatedModuleSize());
/* 179 */                   if (Math.abs(p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize()) <= 
/* 180 */                     0.5F || vModSize23 < 0.05F)
/*     */                   { FinderPattern[] test;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                     
/* 187 */                     ResultPoint.orderBestPatterns((ResultPoint[])(test = new FinderPattern[] { p1, p2, p3 }));
/*     */                     
/*     */                     FinderPatternInfo info;
/*     */                     
/* 191 */                     float dA = ResultPoint.distance((ResultPoint)(info = new FinderPatternInfo(test)).getTopLeft(), (ResultPoint)info.getBottomLeft());
/* 192 */                     float dC = ResultPoint.distance((ResultPoint)info.getTopRight(), (ResultPoint)info.getBottomLeft());
/* 193 */                     float dB = ResultPoint.distance((ResultPoint)info.getTopLeft(), (ResultPoint)info.getTopRight());
/*     */                     
/*     */                     float estimatedModuleCount;
/*     */                     
/* 197 */                     if ((estimatedModuleCount = (dA + dB) / p1.getEstimatedModuleSize() * 2.0F) <= 180.0F && estimatedModuleCount >= 9.0F)
/*     */                     {
/*     */ 
/*     */ 
/*     */ 
/*     */                       
/* 203 */                       if (Math.abs((dA - dB) / Math.min(dA, dB)) < 
/* 204 */                         0.1F)
/*     */                       
/*     */                       { 
/*     */ 
/*     */                         
/* 209 */                         float dCpy = (float)Math.sqrt((dA * dA + dB * dB));
/*     */                         
/* 211 */                         if (Math.abs((dC - dCpy) / Math.min(dC, dCpy)) < 
/*     */                           
/* 213 */                           0.1F)
/*     */                         {
/*     */ 
/*     */ 
/*     */                           
/* 218 */                           results.add(test); }  }  }  } else { break; }  }  }  }
/*     */             else { break; }
/*     */           
/*     */           } 
/*     */         }  } 
/* 223 */     }  if (!results.isEmpty()) {
/* 224 */       return results.<FinderPattern[]>toArray(new FinderPattern[results.size()][]);
/*     */     }
/*     */ 
/*     */     
/* 228 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */   
/*     */   public FinderPatternInfo[] findMulti(Map<DecodeHintType, ?> hints) throws NotFoundException {
/* 232 */     boolean tryHarder = (hints != null && hints.containsKey(DecodeHintType.TRY_HARDER));
/* 233 */     boolean pureBarcode = (hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE));
/*     */     BitMatrix image;
/* 235 */     int maxI = (image = getImage()).getHeight();
/* 236 */     int maxJ = image.getWidth();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     int iSkip;
/*     */ 
/*     */ 
/*     */     
/* 245 */     if ((iSkip = (int)(maxI / 228.0F * 3.0F)) < 3 || tryHarder) {
/* 246 */       iSkip = 3;
/*     */     }
/*     */     
/* 249 */     int[] stateCount = new int[5]; int i;
/* 250 */     for (i = iSkip - 1; i < maxI; i += iSkip) {
/*     */       
/* 252 */       stateCount[0] = 0;
/* 253 */       stateCount[1] = 0;
/* 254 */       stateCount[2] = 0;
/* 255 */       stateCount[3] = 0;
/* 256 */       stateCount[4] = 0;
/* 257 */       int currentState = 0;
/* 258 */       for (int k = 0; k < maxJ; k++) {
/* 259 */         if (image.get(k, i)) {
/*     */           
/* 261 */           if ((currentState & 0x1) == 1) {
/* 262 */             currentState++;
/*     */           }
/* 264 */           stateCount[currentState] = stateCount[currentState] + 1;
/*     */         }
/* 266 */         else if ((currentState & 0x1) == 0) {
/* 267 */           if (currentState == 4) {
/* 268 */             if (foundPatternCross(stateCount) && handlePossibleCenter(stateCount, i, k, pureBarcode)) {
/*     */               
/* 270 */               currentState = 0;
/* 271 */               stateCount[0] = 0;
/* 272 */               stateCount[1] = 0;
/* 273 */               stateCount[2] = 0;
/* 274 */               stateCount[3] = 0;
/* 275 */               stateCount[4] = 0;
/*     */             } else {
/* 277 */               stateCount[0] = stateCount[2];
/* 278 */               stateCount[1] = stateCount[3];
/* 279 */               stateCount[2] = stateCount[4];
/* 280 */               stateCount[3] = 1;
/* 281 */               stateCount[4] = 0;
/* 282 */               currentState = 3;
/*     */             } 
/*     */           } else {
/* 285 */             stateCount[++currentState] = stateCount[++currentState] + 1;
/*     */           } 
/*     */         } else {
/* 288 */           stateCount[currentState] = stateCount[currentState] + 1;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 293 */       if (foundPatternCross(stateCount)) {
/* 294 */         handlePossibleCenter(stateCount, i, maxJ, pureBarcode);
/*     */       }
/*     */     } 
/* 297 */     FinderPattern[][] patternInfo = selectMutipleBestPatterns();
/* 298 */     List<FinderPatternInfo> result = new ArrayList<>(); FinderPattern[][] arrayOfFinderPattern1; int j; byte b;
/* 299 */     for (j = (arrayOfFinderPattern1 = patternInfo).length, b = 0; b < j; b++) {
/* 300 */       FinderPattern[] pattern; ResultPoint.orderBestPatterns((ResultPoint[])(pattern = arrayOfFinderPattern1[b]));
/* 301 */       result.add(new FinderPatternInfo(pattern));
/*     */     } 
/*     */     
/* 304 */     if (result.isEmpty()) {
/* 305 */       return EMPTY_RESULT_ARRAY;
/*     */     }
/* 307 */     return result.<FinderPatternInfo>toArray(new FinderPatternInfo[result.size()]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\multi\qrcode\detector\MultiFinderPatternFinder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */