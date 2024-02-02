package com.google.zxing.common.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;

/** @deprecated */
@Deprecated
public final class MonochromeRectangleDetector {
   private static final int MAX_MODULES = 32;
   private final BitMatrix image;

   public MonochromeRectangleDetector(BitMatrix image) {
      this.image = image;
   }

   public ResultPoint[] detect() throws NotFoundException {
      int height = this.image.getHeight();
      int width = this.image.getWidth();
      int halfHeight = height / 2;
      int halfWidth = width / 2;
      int deltaY = Math.max(1, height / 256);
      int deltaX = Math.max(1, width / 256);
      int top = (int)this.findCornerFromCenter(halfWidth, 0, 0, width, halfHeight, -deltaY, 0, height, halfWidth / 2).getY() - 1;
      ResultPoint pointB;
      int left = (int)(pointB = this.findCornerFromCenter(halfWidth, -deltaX, 0, width, halfHeight, 0, top, height, halfHeight / 2)).getX() - 1;
      ResultPoint pointC;
      int right = (int)(pointC = this.findCornerFromCenter(halfWidth, deltaX, left, width, halfHeight, 0, top, height, halfHeight / 2)).getX() + 1;
      ResultPoint pointD;
      int bottom = (int)(pointD = this.findCornerFromCenter(halfWidth, 0, left, right, halfHeight, deltaY, top, height, halfWidth / 2)).getY() + 1;
      ResultPoint pointA = this.findCornerFromCenter(halfWidth, 0, left, right, halfHeight, -deltaY, top, bottom, halfWidth / 4);
      return new ResultPoint[]{pointA, pointB, pointC, pointD};
   }

   private ResultPoint findCornerFromCenter(int centerX, int deltaX, int left, int right, int centerY, int deltaY, int top, int bottom, int maxWhiteRun) throws NotFoundException {
      int[] lastRange = null;
      int y = centerY;

      for(int x = centerX; y < bottom && y >= top && x < right && x >= left; x += deltaX) {
         int[] range;
         if (deltaX == 0) {
            range = this.blackWhiteRange(y, maxWhiteRun, left, right, true);
         } else {
            range = this.blackWhiteRange(x, maxWhiteRun, top, bottom, false);
         }

         if (range == null) {
            if (lastRange == null) {
               throw NotFoundException.getNotFoundInstance();
            }

            int lastX;
            if (deltaX == 0) {
               lastX = y - deltaY;
               if (lastRange[0] < centerX) {
                  if (lastRange[1] > centerX) {
                     return new ResultPoint((float)lastRange[deltaY > 0 ? 0 : 1], (float)lastX);
                  }

                  return new ResultPoint((float)lastRange[0], (float)lastX);
               }

               return new ResultPoint((float)lastRange[1], (float)lastX);
            }

            lastX = x - deltaX;
            if (lastRange[0] < centerY) {
               if (lastRange[1] > centerY) {
                  return new ResultPoint((float)lastX, (float)lastRange[deltaX < 0 ? 0 : 1]);
               }

               return new ResultPoint((float)lastX, (float)lastRange[0]);
            }

            return new ResultPoint((float)lastX, (float)lastRange[1]);
         }

         lastRange = range;
         y += deltaY;
      }

      throw NotFoundException.getNotFoundInstance();
   }

   private int[] blackWhiteRange(int fixedDimension, int maxWhiteRun, int minDim, int maxDim, boolean horizontal) {
      int center;
      int start = center = (minDim + maxDim) / 2;

      int end;
      int whiteRunStart;
      while(start >= minDim) {
         label89: {
            if (horizontal) {
               if (!this.image.get(start, fixedDimension)) {
                  break label89;
               }
            } else if (!this.image.get(fixedDimension, start)) {
               break label89;
            }

            --start;
            continue;
         }

         end = start;

         while(true) {
            --start;
            if (start < minDim) {
               break;
            }

            if (horizontal) {
               if (this.image.get(start, fixedDimension)) {
                  break;
               }
            } else if (this.image.get(fixedDimension, start)) {
               break;
            }
         }

         whiteRunStart = end - start;
         if (start < minDim || whiteRunStart > maxWhiteRun) {
            start = end;
            break;
         }
      }

      ++start;
      end = center;

      while(end < maxDim) {
         label99: {
            if (horizontal) {
               if (this.image.get(end, fixedDimension)) {
                  break label99;
               }
            } else if (this.image.get(fixedDimension, end)) {
               break label99;
            }

            whiteRunStart = end;

            while(true) {
               ++end;
               if (end >= maxDim) {
                  break;
               }

               if (horizontal) {
                  if (this.image.get(end, fixedDimension)) {
                     break;
                  }
               } else if (this.image.get(fixedDimension, end)) {
                  break;
               }
            }

            int whiteRunSize = end - whiteRunStart;
            if (end < maxDim && whiteRunSize <= maxWhiteRun) {
               continue;
            }

            end = whiteRunStart;
            break;
         }

         ++end;
      }

      --end;
      return end > start ? new int[]{start, end} : null;
   }
}
