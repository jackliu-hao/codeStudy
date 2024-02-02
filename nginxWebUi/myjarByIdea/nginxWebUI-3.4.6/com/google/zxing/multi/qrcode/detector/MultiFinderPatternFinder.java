package com.google.zxing.multi.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.detector.FinderPattern;
import com.google.zxing.qrcode.detector.FinderPatternFinder;
import com.google.zxing.qrcode.detector.FinderPatternInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

final class MultiFinderPatternFinder extends FinderPatternFinder {
   private static final FinderPatternInfo[] EMPTY_RESULT_ARRAY = new FinderPatternInfo[0];
   private static final float MAX_MODULE_COUNT_PER_EDGE = 180.0F;
   private static final float MIN_MODULE_COUNT_PER_EDGE = 9.0F;
   private static final float DIFF_MODSIZE_CUTOFF_PERCENT = 0.05F;
   private static final float DIFF_MODSIZE_CUTOFF = 0.5F;

   MultiFinderPatternFinder(BitMatrix image) {
      super(image);
   }

   MultiFinderPatternFinder(BitMatrix image, ResultPointCallback resultPointCallback) {
      super(image, resultPointCallback);
   }

   private FinderPattern[][] selectMutipleBestPatterns() throws NotFoundException {
      List possibleCenters;
      int size;
      if ((size = (possibleCenters = this.getPossibleCenters()).size()) < 3) {
         throw NotFoundException.getNotFoundInstance();
      } else if (size == 3) {
         return new FinderPattern[][]{{(FinderPattern)possibleCenters.get(0), (FinderPattern)possibleCenters.get(1), (FinderPattern)possibleCenters.get(2)}};
      } else {
         Collections.sort(possibleCenters, new ModuleSizeComparator());
         List<FinderPattern[]> results = new ArrayList();

         for(int i1 = 0; i1 < size - 2; ++i1) {
            FinderPattern p1;
            if ((p1 = (FinderPattern)possibleCenters.get(i1)) != null) {
               for(int i2 = i1 + 1; i2 < size - 1; ++i2) {
                  FinderPattern p2;
                  if ((p2 = (FinderPattern)possibleCenters.get(i2)) != null) {
                     float vModSize12 = (p1.getEstimatedModuleSize() - p2.getEstimatedModuleSize()) / Math.min(p1.getEstimatedModuleSize(), p2.getEstimatedModuleSize());
                     if (Math.abs(p1.getEstimatedModuleSize() - p2.getEstimatedModuleSize()) > 0.5F && vModSize12 >= 0.05F) {
                        break;
                     }

                     for(int i3 = i2 + 1; i3 < size; ++i3) {
                        FinderPattern p3;
                        if ((p3 = (FinderPattern)possibleCenters.get(i3)) != null) {
                           float vModSize23 = (p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize()) / Math.min(p2.getEstimatedModuleSize(), p3.getEstimatedModuleSize());
                           if (Math.abs(p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize()) > 0.5F && vModSize23 >= 0.05F) {
                              break;
                           }

                           FinderPattern[] test;
                           ResultPoint.orderBestPatterns(test = new FinderPattern[]{p1, p2, p3});
                           FinderPatternInfo info;
                           float dA = ResultPoint.distance((info = new FinderPatternInfo(test)).getTopLeft(), info.getBottomLeft());
                           float dC = ResultPoint.distance(info.getTopRight(), info.getBottomLeft());
                           float dB = ResultPoint.distance(info.getTopLeft(), info.getTopRight());
                           float estimatedModuleCount;
                           if (!((estimatedModuleCount = (dA + dB) / (p1.getEstimatedModuleSize() * 2.0F)) > 180.0F) && !(estimatedModuleCount < 9.0F) && !(Math.abs((dA - dB) / Math.min(dA, dB)) >= 0.1F)) {
                              float dCpy = (float)Math.sqrt((double)(dA * dA + dB * dB));
                              if (!(Math.abs((dC - dCpy) / Math.min(dC, dCpy)) >= 0.1F)) {
                                 results.add(test);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (!results.isEmpty()) {
            return (FinderPattern[][])results.toArray(new FinderPattern[results.size()][]);
         } else {
            throw NotFoundException.getNotFoundInstance();
         }
      }
   }

   public FinderPatternInfo[] findMulti(Map<DecodeHintType, ?> hints) throws NotFoundException {
      boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
      boolean pureBarcode = hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE);
      BitMatrix image;
      int maxI = (image = this.getImage()).getHeight();
      int maxJ = image.getWidth();
      int iSkip;
      if ((iSkip = (int)((float)maxI / 228.0F * 3.0F)) < 3 || tryHarder) {
         iSkip = 3;
      }

      int[] stateCount = new int[5];

      for(int i = iSkip - 1; i < maxI; i += iSkip) {
         stateCount[0] = 0;
         stateCount[1] = 0;
         stateCount[2] = 0;
         stateCount[3] = 0;
         stateCount[4] = 0;
         int currentState = 0;

         for(int j = 0; j < maxJ; ++j) {
            int var10002;
            if (image.get(j, i)) {
               if ((currentState & 1) == 1) {
                  ++currentState;
               }

               var10002 = stateCount[currentState]++;
            } else if ((currentState & 1) == 0) {
               if (currentState == 4) {
                  if (foundPatternCross(stateCount) && this.handlePossibleCenter(stateCount, i, j, pureBarcode)) {
                     currentState = 0;
                     stateCount[0] = 0;
                     stateCount[1] = 0;
                     stateCount[2] = 0;
                     stateCount[3] = 0;
                     stateCount[4] = 0;
                  } else {
                     stateCount[0] = stateCount[2];
                     stateCount[1] = stateCount[3];
                     stateCount[2] = stateCount[4];
                     stateCount[3] = 1;
                     stateCount[4] = 0;
                     currentState = 3;
                  }
               } else {
                  ++currentState;
                  var10002 = stateCount[currentState]++;
               }
            } else {
               var10002 = stateCount[currentState]++;
            }
         }

         if (foundPatternCross(stateCount)) {
            this.handlePossibleCenter(stateCount, i, maxJ, pureBarcode);
         }
      }

      FinderPattern[][] patternInfo = this.selectMutipleBestPatterns();
      List<FinderPatternInfo> result = new ArrayList();
      FinderPattern[][] var16 = patternInfo;
      int var12 = patternInfo.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         FinderPattern[] pattern;
         ResultPoint.orderBestPatterns(pattern = var16[var13]);
         result.add(new FinderPatternInfo(pattern));
      }

      if (result.isEmpty()) {
         return EMPTY_RESULT_ARRAY;
      } else {
         return (FinderPatternInfo[])result.toArray(new FinderPatternInfo[result.size()]);
      }
   }

   private static final class ModuleSizeComparator implements Serializable, Comparator<FinderPattern> {
      private ModuleSizeComparator() {
      }

      public int compare(FinderPattern center1, FinderPattern center2) {
         float value;
         if ((double)(value = center2.getEstimatedModuleSize() - center1.getEstimatedModuleSize()) < 0.0) {
            return -1;
         } else {
            return (double)value > 0.0 ? 1 : 0;
         }
      }

      // $FF: synthetic method
      ModuleSizeComparator(Object x0) {
         this();
      }
   }
}
