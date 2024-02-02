package com.google.zxing.common.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;

public final class WhiteRectangleDetector {
   private static final int INIT_SIZE = 10;
   private static final int CORR = 1;
   private final BitMatrix image;
   private final int height;
   private final int width;
   private final int leftInit;
   private final int rightInit;
   private final int downInit;
   private final int upInit;

   public WhiteRectangleDetector(BitMatrix image) throws NotFoundException {
      this(image, 10, image.getWidth() / 2, image.getHeight() / 2);
   }

   public WhiteRectangleDetector(BitMatrix image, int initSize, int x, int y) throws NotFoundException {
      this.image = image;
      this.height = image.getHeight();
      this.width = image.getWidth();
      int halfsize = initSize / 2;
      this.leftInit = x - halfsize;
      this.rightInit = x + halfsize;
      this.upInit = y - halfsize;
      this.downInit = y + halfsize;
      if (this.upInit < 0 || this.leftInit < 0 || this.downInit >= this.height || this.rightInit >= this.width) {
         throw NotFoundException.getNotFoundInstance();
      }
   }

   public ResultPoint[] detect() throws NotFoundException {
      int left = this.leftInit;
      int right = this.rightInit;
      int up = this.upInit;
      int down = this.downInit;
      boolean sizeExceeded = false;
      boolean aBlackPointFoundOnBorder = true;
      boolean atLeastOneBlackPointFoundOnBorder = false;
      boolean atLeastOneBlackPointFoundOnRight = false;
      boolean atLeastOneBlackPointFoundOnBottom = false;
      boolean atLeastOneBlackPointFoundOnLeft = false;
      boolean atLeastOneBlackPointFoundOnTop = false;

      while(aBlackPointFoundOnBorder) {
         aBlackPointFoundOnBorder = false;
         boolean rightBorderNotWhite = true;

         while((rightBorderNotWhite || !atLeastOneBlackPointFoundOnRight) && right < this.width) {
            if (rightBorderNotWhite = this.containsBlackPoint(up, down, right, false)) {
               ++right;
               aBlackPointFoundOnBorder = true;
               atLeastOneBlackPointFoundOnRight = true;
            } else if (!atLeastOneBlackPointFoundOnRight) {
               ++right;
            }
         }

         if (right >= this.width) {
            sizeExceeded = true;
            break;
         }

         boolean bottomBorderNotWhite = true;

         while((bottomBorderNotWhite || !atLeastOneBlackPointFoundOnBottom) && down < this.height) {
            if (bottomBorderNotWhite = this.containsBlackPoint(left, right, down, true)) {
               ++down;
               aBlackPointFoundOnBorder = true;
               atLeastOneBlackPointFoundOnBottom = true;
            } else if (!atLeastOneBlackPointFoundOnBottom) {
               ++down;
            }
         }

         if (down >= this.height) {
            sizeExceeded = true;
            break;
         }

         boolean leftBorderNotWhite = true;

         while((leftBorderNotWhite || !atLeastOneBlackPointFoundOnLeft) && left >= 0) {
            if (leftBorderNotWhite = this.containsBlackPoint(up, down, left, false)) {
               --left;
               aBlackPointFoundOnBorder = true;
               atLeastOneBlackPointFoundOnLeft = true;
            } else if (!atLeastOneBlackPointFoundOnLeft) {
               --left;
            }
         }

         if (left < 0) {
            sizeExceeded = true;
            break;
         }

         boolean topBorderNotWhite = true;

         while((topBorderNotWhite || !atLeastOneBlackPointFoundOnTop) && up >= 0) {
            if (topBorderNotWhite = this.containsBlackPoint(left, right, up, true)) {
               --up;
               aBlackPointFoundOnBorder = true;
               atLeastOneBlackPointFoundOnTop = true;
            } else if (!atLeastOneBlackPointFoundOnTop) {
               --up;
            }
         }

         if (up < 0) {
            sizeExceeded = true;
            break;
         }

         if (aBlackPointFoundOnBorder) {
            atLeastOneBlackPointFoundOnBorder = true;
         }
      }

      if (!sizeExceeded && atLeastOneBlackPointFoundOnBorder) {
         int maxSize = right - left;
         ResultPoint z = null;

         for(int i = 1; z == null && i < maxSize; ++i) {
            z = this.getBlackPointOnSegment((float)left, (float)(down - i), (float)(left + i), (float)down);
         }

         if (z == null) {
            throw NotFoundException.getNotFoundInstance();
         } else {
            ResultPoint t = null;

            for(int i = 1; t == null && i < maxSize; ++i) {
               t = this.getBlackPointOnSegment((float)left, (float)(up + i), (float)(left + i), (float)up);
            }

            if (t == null) {
               throw NotFoundException.getNotFoundInstance();
            } else {
               ResultPoint x = null;

               for(int i = 1; x == null && i < maxSize; ++i) {
                  x = this.getBlackPointOnSegment((float)right, (float)(up + i), (float)(right - i), (float)up);
               }

               if (x == null) {
                  throw NotFoundException.getNotFoundInstance();
               } else {
                  ResultPoint y = null;

                  for(int i = 1; y == null && i < maxSize; ++i) {
                     y = this.getBlackPointOnSegment((float)right, (float)(down - i), (float)(right - i), (float)down);
                  }

                  if (y == null) {
                     throw NotFoundException.getNotFoundInstance();
                  } else {
                     return this.centerEdges(y, z, x, t);
                  }
               }
            }
         }
      } else {
         throw NotFoundException.getNotFoundInstance();
      }
   }

   private ResultPoint getBlackPointOnSegment(float aX, float aY, float bX, float bY) {
      int dist = MathUtils.round(MathUtils.distance(aX, aY, bX, bY));
      float xStep = (bX - aX) / (float)dist;
      float yStep = (bY - aY) / (float)dist;

      for(int i = 0; i < dist; ++i) {
         int x = MathUtils.round(aX + (float)i * xStep);
         int y = MathUtils.round(aY + (float)i * yStep);
         if (this.image.get(x, y)) {
            return new ResultPoint((float)x, (float)y);
         }
      }

      return null;
   }

   private ResultPoint[] centerEdges(ResultPoint y, ResultPoint z, ResultPoint x, ResultPoint t) {
      float yi = y.getX();
      float yj = y.getY();
      float zi = z.getX();
      float zj = z.getY();
      float xi = x.getX();
      float xj = x.getY();
      float ti = t.getX();
      float tj = t.getY();
      return yi < (float)this.width / 2.0F ? new ResultPoint[]{new ResultPoint(ti - 1.0F, tj + 1.0F), new ResultPoint(zi + 1.0F, zj + 1.0F), new ResultPoint(xi - 1.0F, xj - 1.0F), new ResultPoint(yi + 1.0F, yj - 1.0F)} : new ResultPoint[]{new ResultPoint(ti + 1.0F, tj + 1.0F), new ResultPoint(zi + 1.0F, zj - 1.0F), new ResultPoint(xi - 1.0F, xj + 1.0F), new ResultPoint(yi - 1.0F, yj - 1.0F)};
   }

   private boolean containsBlackPoint(int a, int b, int fixed, boolean horizontal) {
      int x;
      if (horizontal) {
         for(x = a; x <= b; ++x) {
            if (this.image.get(x, fixed)) {
               return true;
            }
         }
      } else {
         for(x = a; x <= b; ++x) {
            if (this.image.get(fixed, x)) {
               return true;
            }
         }
      }

      return false;
   }
}
