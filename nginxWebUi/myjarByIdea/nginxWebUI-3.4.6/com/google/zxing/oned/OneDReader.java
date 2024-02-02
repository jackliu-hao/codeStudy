package com.google.zxing.oned;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public abstract class OneDReader implements Reader {
   public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
      return this.decode(image, (Map)null);
   }

   public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
      try {
         return this.doDecode(image, hints);
      } catch (NotFoundException var11) {
         if (hints != null && hints.containsKey(DecodeHintType.TRY_HARDER) && image.isRotateSupported()) {
            BinaryBitmap rotatedImage = image.rotateCounterClockwise();
            Result result;
            Map<ResultMetadataType, ?> metadata = (result = this.doDecode(rotatedImage, hints)).getResultMetadata();
            int orientation = 270;
            if (metadata != null && metadata.containsKey(ResultMetadataType.ORIENTATION)) {
               orientation = (270 + (Integer)metadata.get(ResultMetadataType.ORIENTATION)) % 360;
            }

            result.putMetadata(ResultMetadataType.ORIENTATION, orientation);
            ResultPoint[] points;
            if ((points = result.getResultPoints()) != null) {
               int height = rotatedImage.getHeight();

               for(int i = 0; i < points.length; ++i) {
                  points[i] = new ResultPoint((float)height - points[i].getY() - 1.0F, points[i].getX());
               }
            }

            return result;
         } else {
            throw var11;
         }
      }
   }

   public void reset() {
   }

   private Result doDecode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
      int width = image.getWidth();
      int height = image.getHeight();
      BitArray row = new BitArray(width);
      int middle = height >> 1;
      boolean tryHarder = hints != null && ((Map)hints).containsKey(DecodeHintType.TRY_HARDER);
      int rowStep = Math.max(1, height >> (tryHarder ? 8 : 5));
      int maxLines;
      if (tryHarder) {
         maxLines = height;
      } else {
         maxLines = 15;
      }

      int x = 0;

      while(true) {
         if (x < maxLines) {
            int rowStepsAboveOrBelow = (x + 1) / 2;
            boolean isAbove = (x & 1) == 0;
            int rowNumber;
            if ((rowNumber = middle + rowStep * (isAbove ? rowStepsAboveOrBelow : -rowStepsAboveOrBelow)) >= 0 && rowNumber < height) {
               label86: {
                  try {
                     row = image.getBlackRow(rowNumber, row);
                  } catch (NotFoundException var17) {
                     break label86;
                  }

                  int attempt = 0;

                  while(attempt < 2) {
                     if (attempt == 1) {
                        row.reverse();
                        if (hints != null && ((Map)hints).containsKey(DecodeHintType.NEED_RESULT_POINT_CALLBACK)) {
                           EnumMap newHints;
                           (newHints = new EnumMap(DecodeHintType.class)).putAll((Map)hints);
                           newHints.remove(DecodeHintType.NEED_RESULT_POINT_CALLBACK);
                           hints = newHints;
                        }
                     }

                     try {
                        Result result = this.decodeRow(rowNumber, row, (Map)hints);
                        if (attempt == 1) {
                           result.putMetadata(ResultMetadataType.ORIENTATION, 180);
                           ResultPoint[] points;
                           if ((points = result.getResultPoints()) != null) {
                              points[0] = new ResultPoint((float)width - points[0].getX() - 1.0F, points[0].getY());
                              points[1] = new ResultPoint((float)width - points[1].getX() - 1.0F, points[1].getY());
                           }
                        }

                        return result;
                     } catch (ReaderException var18) {
                        ++attempt;
                     }
                  }
               }

               ++x;
               continue;
            }
         }

         throw NotFoundException.getNotFoundInstance();
      }
   }

   protected static void recordPattern(BitArray row, int start, int[] counters) throws NotFoundException {
      int numCounters = counters.length;
      Arrays.fill(counters, 0, numCounters, 0);
      int end = row.getSize();
      if (start >= end) {
         throw NotFoundException.getNotFoundInstance();
      } else {
         boolean isWhite = !row.get(start);
         int counterPosition = 0;

         int i;
         for(i = start; i < end; ++i) {
            if (row.get(i) ^ isWhite) {
               int var10002 = counters[counterPosition]++;
            } else {
               ++counterPosition;
               if (counterPosition == numCounters) {
                  break;
               }

               counters[counterPosition] = 1;
               isWhite = !isWhite;
            }
         }

         if (counterPosition != numCounters && (counterPosition != numCounters - 1 || i != end)) {
            throw NotFoundException.getNotFoundInstance();
         }
      }
   }

   protected static void recordPatternInReverse(BitArray row, int start, int[] counters) throws NotFoundException {
      int numTransitionsLeft = counters.length;
      boolean last = row.get(start);

      while(start > 0 && numTransitionsLeft >= 0) {
         --start;
         if (row.get(start) != last) {
            --numTransitionsLeft;
            last = !last;
         }
      }

      if (numTransitionsLeft >= 0) {
         throw NotFoundException.getNotFoundInstance();
      } else {
         recordPattern(row, start + 1, counters);
      }
   }

   protected static float patternMatchVariance(int[] counters, int[] pattern, float maxIndividualVariance) {
      int numCounters = counters.length;
      int total = 0;
      int patternLength = 0;

      for(int i = 0; i < numCounters; ++i) {
         total += counters[i];
         patternLength += pattern[i];
      }

      if (total < patternLength) {
         return Float.POSITIVE_INFINITY;
      } else {
         float unitBarWidth = (float)total / (float)patternLength;
         maxIndividualVariance *= unitBarWidth;
         float totalVariance = 0.0F;

         for(int x = 0; x < numCounters; ++x) {
            int counter = counters[x];
            float scaledPattern = (float)pattern[x] * unitBarWidth;
            float variance;
            if ((variance = (float)counter > scaledPattern ? (float)counter - scaledPattern : scaledPattern - (float)counter) > maxIndividualVariance) {
               return Float.POSITIVE_INFINITY;
            }

            totalVariance += variance;
         }

         return totalVariance / (float)total;
      }
   }

   public abstract Result decodeRow(int var1, BitArray var2, Map<DecodeHintType, ?> var3) throws NotFoundException, ChecksumException, FormatException;
}
