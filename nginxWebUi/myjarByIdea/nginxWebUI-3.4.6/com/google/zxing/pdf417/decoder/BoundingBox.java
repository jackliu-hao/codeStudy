package com.google.zxing.pdf417.decoder;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;

final class BoundingBox {
   private BitMatrix image;
   private ResultPoint topLeft;
   private ResultPoint bottomLeft;
   private ResultPoint topRight;
   private ResultPoint bottomRight;
   private int minX;
   private int maxX;
   private int minY;
   private int maxY;

   BoundingBox(BitMatrix image, ResultPoint topLeft, ResultPoint bottomLeft, ResultPoint topRight, ResultPoint bottomRight) throws NotFoundException {
      if ((topLeft != null || topRight != null) && (bottomLeft != null || bottomRight != null) && (topLeft == null || bottomLeft != null) && (topRight == null || bottomRight != null)) {
         this.init(image, topLeft, bottomLeft, topRight, bottomRight);
      } else {
         throw NotFoundException.getNotFoundInstance();
      }
   }

   BoundingBox(BoundingBox boundingBox) {
      this.init(boundingBox.image, boundingBox.topLeft, boundingBox.bottomLeft, boundingBox.topRight, boundingBox.bottomRight);
   }

   private void init(BitMatrix image, ResultPoint topLeft, ResultPoint bottomLeft, ResultPoint topRight, ResultPoint bottomRight) {
      this.image = image;
      this.topLeft = topLeft;
      this.bottomLeft = bottomLeft;
      this.topRight = topRight;
      this.bottomRight = bottomRight;
      this.calculateMinMaxValues();
   }

   static BoundingBox merge(BoundingBox leftBox, BoundingBox rightBox) throws NotFoundException {
      if (leftBox == null) {
         return rightBox;
      } else {
         return rightBox == null ? leftBox : new BoundingBox(leftBox.image, leftBox.topLeft, leftBox.bottomLeft, rightBox.topRight, rightBox.bottomRight);
      }
   }

   BoundingBox addMissingRows(int missingStartRows, int missingEndRows, boolean isLeft) throws NotFoundException {
      ResultPoint newTopLeft = this.topLeft;
      ResultPoint newBottomLeft = this.bottomLeft;
      ResultPoint newTopRight = this.topRight;
      ResultPoint newBottomRight = this.bottomRight;
      ResultPoint bottom;
      int newMaxY;
      ResultPoint newBottom;
      if (missingStartRows > 0) {
         if ((newMaxY = (int)(bottom = isLeft ? this.topLeft : this.topRight).getY() - missingStartRows) < 0) {
            newMaxY = 0;
         }

         newBottom = new ResultPoint(bottom.getX(), (float)newMaxY);
         if (isLeft) {
            newTopLeft = newBottom;
         } else {
            newTopRight = newBottom;
         }
      }

      if (missingEndRows > 0) {
         if ((newMaxY = (int)(bottom = isLeft ? this.bottomLeft : this.bottomRight).getY() + missingEndRows) >= this.image.getHeight()) {
            newMaxY = this.image.getHeight() - 1;
         }

         newBottom = new ResultPoint(bottom.getX(), (float)newMaxY);
         if (isLeft) {
            newBottomLeft = newBottom;
         } else {
            newBottomRight = newBottom;
         }
      }

      this.calculateMinMaxValues();
      return new BoundingBox(this.image, newTopLeft, newBottomLeft, newTopRight, newBottomRight);
   }

   private void calculateMinMaxValues() {
      if (this.topLeft == null) {
         this.topLeft = new ResultPoint(0.0F, this.topRight.getY());
         this.bottomLeft = new ResultPoint(0.0F, this.bottomRight.getY());
      } else if (this.topRight == null) {
         this.topRight = new ResultPoint((float)(this.image.getWidth() - 1), this.topLeft.getY());
         this.bottomRight = new ResultPoint((float)(this.image.getWidth() - 1), this.bottomLeft.getY());
      }

      this.minX = (int)Math.min(this.topLeft.getX(), this.bottomLeft.getX());
      this.maxX = (int)Math.max(this.topRight.getX(), this.bottomRight.getX());
      this.minY = (int)Math.min(this.topLeft.getY(), this.topRight.getY());
      this.maxY = (int)Math.max(this.bottomLeft.getY(), this.bottomRight.getY());
   }

   int getMinX() {
      return this.minX;
   }

   int getMaxX() {
      return this.maxX;
   }

   int getMinY() {
      return this.minY;
   }

   int getMaxY() {
      return this.maxY;
   }

   ResultPoint getTopLeft() {
      return this.topLeft;
   }

   ResultPoint getTopRight() {
      return this.topRight;
   }

   ResultPoint getBottomLeft() {
      return this.bottomLeft;
   }

   ResultPoint getBottomRight() {
      return this.bottomRight;
   }
}
