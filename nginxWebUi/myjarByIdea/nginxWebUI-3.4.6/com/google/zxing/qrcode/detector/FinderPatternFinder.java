package com.google.zxing.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FinderPatternFinder {
   private static final int CENTER_QUORUM = 2;
   protected static final int MIN_SKIP = 3;
   protected static final int MAX_MODULES = 57;
   private final BitMatrix image;
   private final List<FinderPattern> possibleCenters;
   private boolean hasSkipped;
   private final int[] crossCheckStateCount;
   private final ResultPointCallback resultPointCallback;

   public FinderPatternFinder(BitMatrix image) {
      this(image, (ResultPointCallback)null);
   }

   public FinderPatternFinder(BitMatrix image, ResultPointCallback resultPointCallback) {
      this.image = image;
      this.possibleCenters = new ArrayList();
      this.crossCheckStateCount = new int[5];
      this.resultPointCallback = resultPointCallback;
   }

   protected final BitMatrix getImage() {
      return this.image;
   }

   protected final List<FinderPattern> getPossibleCenters() {
      return this.possibleCenters;
   }

   final FinderPatternInfo find(Map<DecodeHintType, ?> hints) throws NotFoundException {
      boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
      boolean pureBarcode = hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE);
      int maxI = this.image.getHeight();
      int maxJ = this.image.getWidth();
      int iSkip;
      if ((iSkip = 3 * maxI / 228) < 3 || tryHarder) {
         iSkip = 3;
      }

      boolean done = false;
      int[] stateCount = new int[5];

      for(int i = iSkip - 1; i < maxI && !done; i += iSkip) {
         stateCount[0] = 0;
         stateCount[1] = 0;
         stateCount[2] = 0;
         stateCount[3] = 0;
         stateCount[4] = 0;
         int currentState = 0;

         for(int j = 0; j < maxJ; ++j) {
            int var10002;
            if (this.image.get(j, i)) {
               if ((currentState & 1) == 1) {
                  ++currentState;
               }

               var10002 = stateCount[currentState]++;
            } else if ((currentState & 1) == 0) {
               if (currentState == 4) {
                  if (foundPatternCross(stateCount)) {
                     if (this.handlePossibleCenter(stateCount, i, j, pureBarcode)) {
                        iSkip = 2;
                        if (this.hasSkipped) {
                           done = this.haveMultiplyConfirmedCenters();
                        } else {
                           int rowSkip;
                           if ((rowSkip = this.findRowSkip()) > stateCount[2]) {
                              i += rowSkip - stateCount[2] - 2;
                              j = maxJ - 1;
                           }
                        }

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

         if (foundPatternCross(stateCount) && this.handlePossibleCenter(stateCount, i, maxJ, pureBarcode)) {
            iSkip = stateCount[0];
            if (this.hasSkipped) {
               done = this.haveMultiplyConfirmedCenters();
            }
         }
      }

      FinderPattern[] patternInfo;
      ResultPoint.orderBestPatterns(patternInfo = this.selectBestPatterns());
      return new FinderPatternInfo(patternInfo);
   }

   private static float centerFromEnd(int[] stateCount, int end) {
      return (float)(end - stateCount[4] - stateCount[3]) - (float)stateCount[2] / 2.0F;
   }

   protected static boolean foundPatternCross(int[] stateCount) {
      int totalModuleSize = 0;

      for(int i = 0; i < 5; ++i) {
         int count;
         if ((count = stateCount[i]) == 0) {
            return false;
         }

         totalModuleSize += count;
      }

      if (totalModuleSize < 7) {
         return false;
      } else {
         float moduleSize;
         float maxVariance = (moduleSize = (float)totalModuleSize / 7.0F) / 2.0F;
         if (Math.abs(moduleSize - (float)stateCount[0]) < maxVariance && Math.abs(moduleSize - (float)stateCount[1]) < maxVariance && Math.abs(3.0F * moduleSize - (float)stateCount[2]) < 3.0F * maxVariance && Math.abs(moduleSize - (float)stateCount[3]) < maxVariance && Math.abs(moduleSize - (float)stateCount[4]) < maxVariance) {
            return true;
         } else {
            return false;
         }
      }
   }

   private int[] getCrossCheckStateCount() {
      this.crossCheckStateCount[0] = 0;
      this.crossCheckStateCount[1] = 0;
      this.crossCheckStateCount[2] = 0;
      this.crossCheckStateCount[3] = 0;
      this.crossCheckStateCount[4] = 0;
      return this.crossCheckStateCount;
   }

   private boolean crossCheckDiagonal(int startI, int centerJ, int maxCount, int originalStateCountTotal) {
      int[] stateCount = this.getCrossCheckStateCount();

      int var10002;
      int i;
      for(i = 0; startI >= i && centerJ >= i && this.image.get(centerJ - i, startI - i); ++i) {
         var10002 = stateCount[2]++;
      }

      if (startI >= i && centerJ >= i) {
         while(startI >= i && centerJ >= i && !this.image.get(centerJ - i, startI - i) && stateCount[1] <= maxCount) {
            var10002 = stateCount[1]++;
            ++i;
         }

         if (startI >= i && centerJ >= i && stateCount[1] <= maxCount) {
            while(startI >= i && centerJ >= i && this.image.get(centerJ - i, startI - i) && stateCount[0] <= maxCount) {
               var10002 = stateCount[0]++;
               ++i;
            }

            if (stateCount[0] > maxCount) {
               return false;
            } else {
               int maxI = this.image.getHeight();
               int maxJ = this.image.getWidth();

               for(i = 1; startI + i < maxI && centerJ + i < maxJ && this.image.get(centerJ + i, startI + i); ++i) {
                  var10002 = stateCount[2]++;
               }

               if (startI + i < maxI && centerJ + i < maxJ) {
                  while(startI + i < maxI && centerJ + i < maxJ && !this.image.get(centerJ + i, startI + i) && stateCount[3] < maxCount) {
                     var10002 = stateCount[3]++;
                     ++i;
                  }

                  if (startI + i < maxI && centerJ + i < maxJ && stateCount[3] < maxCount) {
                     while(startI + i < maxI && centerJ + i < maxJ && this.image.get(centerJ + i, startI + i) && stateCount[4] < maxCount) {
                        var10002 = stateCount[4]++;
                        ++i;
                     }

                     if (stateCount[4] >= maxCount) {
                        return false;
                     } else {
                        return Math.abs(stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4] - originalStateCountTotal) < 2 * originalStateCountTotal && foundPatternCross(stateCount);
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private float crossCheckVertical(int startI, int centerJ, int maxCount, int originalStateCountTotal) {
      BitMatrix image;
      int maxI = (image = this.image).getHeight();
      int[] stateCount = this.getCrossCheckStateCount();

      int var10002;
      int i;
      for(i = startI; i >= 0 && image.get(centerJ, i); --i) {
         var10002 = stateCount[2]++;
      }

      if (i < 0) {
         return Float.NaN;
      } else {
         while(i >= 0 && !image.get(centerJ, i) && stateCount[1] <= maxCount) {
            var10002 = stateCount[1]++;
            --i;
         }

         if (i >= 0 && stateCount[1] <= maxCount) {
            while(i >= 0 && image.get(centerJ, i) && stateCount[0] <= maxCount) {
               var10002 = stateCount[0]++;
               --i;
            }

            if (stateCount[0] > maxCount) {
               return Float.NaN;
            } else {
               for(i = startI + 1; i < maxI && image.get(centerJ, i); ++i) {
                  var10002 = stateCount[2]++;
               }

               if (i == maxI) {
                  return Float.NaN;
               } else {
                  while(i < maxI && !image.get(centerJ, i) && stateCount[3] < maxCount) {
                     var10002 = stateCount[3]++;
                     ++i;
                  }

                  if (i != maxI && stateCount[3] < maxCount) {
                     while(i < maxI && image.get(centerJ, i) && stateCount[4] < maxCount) {
                        var10002 = stateCount[4]++;
                        ++i;
                     }

                     if (stateCount[4] >= maxCount) {
                        return Float.NaN;
                     } else {
                        int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
                        if (5 * Math.abs(stateCountTotal - originalStateCountTotal) >= 2 * originalStateCountTotal) {
                           return Float.NaN;
                        } else {
                           return foundPatternCross(stateCount) ? centerFromEnd(stateCount, i) : Float.NaN;
                        }
                     }
                  } else {
                     return Float.NaN;
                  }
               }
            }
         } else {
            return Float.NaN;
         }
      }
   }

   private float crossCheckHorizontal(int startJ, int centerI, int maxCount, int originalStateCountTotal) {
      BitMatrix image;
      int maxJ = (image = this.image).getWidth();
      int[] stateCount = this.getCrossCheckStateCount();

      int var10002;
      int j;
      for(j = startJ; j >= 0 && image.get(j, centerI); --j) {
         var10002 = stateCount[2]++;
      }

      if (j < 0) {
         return Float.NaN;
      } else {
         while(j >= 0 && !image.get(j, centerI) && stateCount[1] <= maxCount) {
            var10002 = stateCount[1]++;
            --j;
         }

         if (j >= 0 && stateCount[1] <= maxCount) {
            while(j >= 0 && image.get(j, centerI) && stateCount[0] <= maxCount) {
               var10002 = stateCount[0]++;
               --j;
            }

            if (stateCount[0] > maxCount) {
               return Float.NaN;
            } else {
               for(j = startJ + 1; j < maxJ && image.get(j, centerI); ++j) {
                  var10002 = stateCount[2]++;
               }

               if (j == maxJ) {
                  return Float.NaN;
               } else {
                  while(j < maxJ && !image.get(j, centerI) && stateCount[3] < maxCount) {
                     var10002 = stateCount[3]++;
                     ++j;
                  }

                  if (j != maxJ && stateCount[3] < maxCount) {
                     while(j < maxJ && image.get(j, centerI) && stateCount[4] < maxCount) {
                        var10002 = stateCount[4]++;
                        ++j;
                     }

                     if (stateCount[4] >= maxCount) {
                        return Float.NaN;
                     } else {
                        int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
                        if (5 * Math.abs(stateCountTotal - originalStateCountTotal) >= originalStateCountTotal) {
                           return Float.NaN;
                        } else {
                           return foundPatternCross(stateCount) ? centerFromEnd(stateCount, j) : Float.NaN;
                        }
                     }
                  } else {
                     return Float.NaN;
                  }
               }
            }
         } else {
            return Float.NaN;
         }
      }
   }

   protected final boolean handlePossibleCenter(int[] stateCount, int i, int j, boolean pureBarcode) {
      int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
      float centerJ = centerFromEnd(stateCount, j);
      float centerI;
      if (Float.isNaN(centerI = this.crossCheckVertical(i, (int)centerJ, stateCount[2], stateCountTotal)) || Float.isNaN(centerJ = this.crossCheckHorizontal((int)centerJ, (int)centerI, stateCount[2], stateCountTotal)) || pureBarcode && !this.crossCheckDiagonal((int)centerI, (int)centerJ, stateCount[2], stateCountTotal)) {
         return false;
      } else {
         float estimatedModuleSize = (float)stateCountTotal / 7.0F;
         boolean found = false;

         for(int index = 0; index < this.possibleCenters.size(); ++index) {
            FinderPattern center;
            if ((center = (FinderPattern)this.possibleCenters.get(index)).aboutEquals(estimatedModuleSize, centerI, centerJ)) {
               this.possibleCenters.set(index, center.combineEstimate(centerI, centerJ, estimatedModuleSize));
               found = true;
               break;
            }
         }

         if (!found) {
            FinderPattern point = new FinderPattern(centerJ, centerI, estimatedModuleSize);
            this.possibleCenters.add(point);
            if (this.resultPointCallback != null) {
               this.resultPointCallback.foundPossibleResultPoint(point);
            }
         }

         return true;
      }
   }

   private int findRowSkip() {
      if (this.possibleCenters.size() <= 1) {
         return 0;
      } else {
         ResultPoint firstConfirmedCenter = null;
         Iterator var2 = this.possibleCenters.iterator();

         while(var2.hasNext()) {
            FinderPattern center;
            if ((center = (FinderPattern)var2.next()).getCount() >= 2) {
               if (firstConfirmedCenter != null) {
                  this.hasSkipped = true;
                  return (int)(Math.abs(firstConfirmedCenter.getX() - center.getX()) - Math.abs(firstConfirmedCenter.getY() - center.getY())) / 2;
               }

               firstConfirmedCenter = center;
            }
         }

         return 0;
      }
   }

   private boolean haveMultiplyConfirmedCenters() {
      int confirmedCount = 0;
      float totalModuleSize = 0.0F;
      int max = this.possibleCenters.size();
      Iterator var4 = this.possibleCenters.iterator();

      while(var4.hasNext()) {
         FinderPattern pattern;
         if ((pattern = (FinderPattern)var4.next()).getCount() >= 2) {
            ++confirmedCount;
            totalModuleSize += pattern.getEstimatedModuleSize();
         }
      }

      if (confirmedCount < 3) {
         return false;
      } else {
         float average = totalModuleSize / (float)max;
         float totalDeviation = 0.0F;

         FinderPattern pattern;
         for(Iterator var6 = this.possibleCenters.iterator(); var6.hasNext(); totalDeviation += Math.abs(pattern.getEstimatedModuleSize() - average)) {
            pattern = (FinderPattern)var6.next();
         }

         return totalDeviation <= 0.05F * totalModuleSize;
      }
   }

   private FinderPattern[] selectBestPatterns() throws NotFoundException {
      int startSize;
      if ((startSize = this.possibleCenters.size()) < 3) {
         throw NotFoundException.getNotFoundInstance();
      } else {
         float totalModuleSize;
         float average;
         if (startSize > 3) {
            totalModuleSize = 0.0F;
            average = 0.0F;

            float limit;
            for(Iterator var4 = this.possibleCenters.iterator(); var4.hasNext(); average += limit * limit) {
               limit = ((FinderPattern)var4.next()).getEstimatedModuleSize();
               totalModuleSize += limit;
            }

            float average = totalModuleSize / (float)startSize;
            float stdDev = (float)Math.sqrt((double)(average / (float)startSize - average * average));
            Collections.sort(this.possibleCenters, new FurthestFromAverageComparator(average));
            limit = Math.max(0.2F * average, stdDev);

            for(int i = 0; i < this.possibleCenters.size() && this.possibleCenters.size() > 3; ++i) {
               if (Math.abs(((FinderPattern)this.possibleCenters.get(i)).getEstimatedModuleSize() - average) > limit) {
                  this.possibleCenters.remove(i);
                  --i;
               }
            }
         }

         if (this.possibleCenters.size() > 3) {
            totalModuleSize = 0.0F;

            FinderPattern possibleCenter;
            for(Iterator var8 = this.possibleCenters.iterator(); var8.hasNext(); totalModuleSize += possibleCenter.getEstimatedModuleSize()) {
               possibleCenter = (FinderPattern)var8.next();
            }

            average = totalModuleSize / (float)this.possibleCenters.size();
            Collections.sort(this.possibleCenters, new CenterComparator(average));
            this.possibleCenters.subList(3, this.possibleCenters.size()).clear();
         }

         return new FinderPattern[]{(FinderPattern)this.possibleCenters.get(0), (FinderPattern)this.possibleCenters.get(1), (FinderPattern)this.possibleCenters.get(2)};
      }
   }

   private static final class CenterComparator implements Serializable, Comparator<FinderPattern> {
      private final float average;

      private CenterComparator(float f) {
         this.average = f;
      }

      public int compare(FinderPattern center1, FinderPattern center2) {
         if (center2.getCount() == center1.getCount()) {
            float dA = Math.abs(center2.getEstimatedModuleSize() - this.average);
            float dB = Math.abs(center1.getEstimatedModuleSize() - this.average);
            if (dA < dB) {
               return 1;
            } else {
               return dA == dB ? 0 : -1;
            }
         } else {
            return center2.getCount() - center1.getCount();
         }
      }

      // $FF: synthetic method
      CenterComparator(float x0, Object x1) {
         this(x0);
      }
   }

   private static final class FurthestFromAverageComparator implements Serializable, Comparator<FinderPattern> {
      private final float average;

      private FurthestFromAverageComparator(float f) {
         this.average = f;
      }

      public int compare(FinderPattern center1, FinderPattern center2) {
         float dA = Math.abs(center2.getEstimatedModuleSize() - this.average);
         float dB = Math.abs(center1.getEstimatedModuleSize() - this.average);
         if (dA < dB) {
            return -1;
         } else {
            return dA == dB ? 0 : 1;
         }
      }

      // $FF: synthetic method
      FurthestFromAverageComparator(float x0, Object x1) {
         this(x0);
      }
   }
}
