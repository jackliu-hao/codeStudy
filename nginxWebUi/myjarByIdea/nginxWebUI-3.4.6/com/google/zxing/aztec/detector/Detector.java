package com.google.zxing.aztec.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.aztec.AztecDetectorResult;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GridSampler;
import com.google.zxing.common.detector.MathUtils;
import com.google.zxing.common.detector.WhiteRectangleDetector;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;

public final class Detector {
   private final BitMatrix image;
   private boolean compact;
   private int nbLayers;
   private int nbDataBlocks;
   private int nbCenterLayers;
   private int shift;
   private static final int[] EXPECTED_CORNER_BITS = new int[]{3808, 476, 2107, 1799};

   public Detector(BitMatrix image) {
      this.image = image;
   }

   public AztecDetectorResult detect() throws NotFoundException {
      return this.detect(false);
   }

   public AztecDetectorResult detect(boolean isMirror) throws NotFoundException {
      Point pCenter = this.getMatrixCenter();
      ResultPoint[] bullsEyeCorners = this.getBullsEyeCorners(pCenter);
      if (isMirror) {
         ResultPoint temp = bullsEyeCorners[0];
         bullsEyeCorners[0] = bullsEyeCorners[2];
         bullsEyeCorners[2] = temp;
      }

      this.extractParameters(bullsEyeCorners);
      BitMatrix bits = this.sampleGrid(this.image, bullsEyeCorners[this.shift % 4], bullsEyeCorners[(this.shift + 1) % 4], bullsEyeCorners[(this.shift + 2) % 4], bullsEyeCorners[(this.shift + 3) % 4]);
      ResultPoint[] corners = this.getMatrixCornerPoints(bullsEyeCorners);
      return new AztecDetectorResult(bits, corners, this.compact, this.nbDataBlocks, this.nbLayers);
   }

   private void extractParameters(ResultPoint[] bullsEyeCorners) throws NotFoundException {
      if (this.isValid(bullsEyeCorners[0]) && this.isValid(bullsEyeCorners[1]) && this.isValid(bullsEyeCorners[2]) && this.isValid(bullsEyeCorners[3])) {
         int length = 2 * this.nbCenterLayers;
         int[] sides = new int[]{this.sampleLine(bullsEyeCorners[0], bullsEyeCorners[1], length), this.sampleLine(bullsEyeCorners[1], bullsEyeCorners[2], length), this.sampleLine(bullsEyeCorners[2], bullsEyeCorners[3], length), this.sampleLine(bullsEyeCorners[3], bullsEyeCorners[0], length)};
         this.shift = getRotation(sides, length);
         long parameterData = 0L;

         int correctedData;
         for(correctedData = 0; correctedData < 4; ++correctedData) {
            int side = sides[(this.shift + correctedData) % 4];
            if (this.compact) {
               parameterData = (parameterData << 7) + (long)(side >> 1 & 127);
            } else {
               parameterData = (parameterData << 10) + (long)((side >> 2 & 992) + (side >> 1 & 31));
            }
         }

         correctedData = getCorrectedParameterData(parameterData, this.compact);
         if (this.compact) {
            this.nbLayers = (correctedData >> 6) + 1;
            this.nbDataBlocks = (correctedData & 63) + 1;
         } else {
            this.nbLayers = (correctedData >> 11) + 1;
            this.nbDataBlocks = (correctedData & 2047) + 1;
         }
      } else {
         throw NotFoundException.getNotFoundInstance();
      }
   }

   private static int getRotation(int[] sides, int length) throws NotFoundException {
      int cornerBits = 0;
      int[] var3 = sides;
      int var4 = sides.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int side;
         int t = ((side = var3[var5]) >> length - 2 << 1) + (side & 1);
         cornerBits = (cornerBits << 3) + t;
      }

      cornerBits = ((cornerBits & 1) << 11) + (cornerBits >> 1);

      for(int shift = 0; shift < 4; ++shift) {
         if (Integer.bitCount(cornerBits ^ EXPECTED_CORNER_BITS[shift]) <= 2) {
            return shift;
         }
      }

      throw NotFoundException.getNotFoundInstance();
   }

   private static int getCorrectedParameterData(long parameterData, boolean compact) throws NotFoundException {
      byte numCodewords;
      byte numDataCodewords;
      if (compact) {
         numCodewords = 7;
         numDataCodewords = 2;
      } else {
         numCodewords = 10;
         numDataCodewords = 4;
      }

      int numECCodewords = numCodewords - numDataCodewords;
      int[] parameterWords = new int[numCodewords];

      int result;
      for(result = numCodewords - 1; result >= 0; --result) {
         parameterWords[result] = (int)parameterData & 15;
         parameterData >>= 4;
      }

      try {
         (new ReedSolomonDecoder(GenericGF.AZTEC_PARAM)).decode(parameterWords, numECCodewords);
      } catch (ReedSolomonException var9) {
         throw NotFoundException.getNotFoundInstance();
      }

      result = 0;

      for(int i = 0; i < numDataCodewords; ++i) {
         result = (result << 4) + parameterWords[i];
      }

      return result;
   }

   private ResultPoint[] getBullsEyeCorners(Point pCenter) throws NotFoundException {
      Point pina = pCenter;
      Point pinb = pCenter;
      Point pinc = pCenter;
      Point pind = pCenter;
      boolean color = true;

      for(this.nbCenterLayers = 1; this.nbCenterLayers < 9; ++this.nbCenterLayers) {
         Point pouta = this.getFirstDifferent(pina, color, 1, -1);
         Point poutb = this.getFirstDifferent(pinb, color, 1, 1);
         Point poutc = this.getFirstDifferent(pinc, color, -1, 1);
         Point poutd = this.getFirstDifferent(pind, color, -1, -1);
         float q;
         if (this.nbCenterLayers > 2 && ((double)(q = distance(poutd, pouta) * (float)this.nbCenterLayers / (distance(pind, pina) * (float)(this.nbCenterLayers + 2))) < 0.75 || (double)q > 1.25 || !this.isWhiteOrBlackRectangle(pouta, poutb, poutc, poutd))) {
            break;
         }

         pina = pouta;
         pinb = poutb;
         pinc = poutc;
         pind = poutd;
         color = !color;
      }

      if (this.nbCenterLayers != 5 && this.nbCenterLayers != 7) {
         throw NotFoundException.getNotFoundInstance();
      } else {
         this.compact = this.nbCenterLayers == 5;
         ResultPoint pinax = new ResultPoint((float)pina.getX() + 0.5F, (float)pina.getY() - 0.5F);
         ResultPoint pinbx = new ResultPoint((float)pinb.getX() + 0.5F, (float)pinb.getY() + 0.5F);
         ResultPoint pincx = new ResultPoint((float)pinc.getX() - 0.5F, (float)pinc.getY() + 0.5F);
         ResultPoint pindx = new ResultPoint((float)pind.getX() - 0.5F, (float)pind.getY() - 0.5F);
         return expandSquare(new ResultPoint[]{pinax, pinbx, pincx, pindx}, (float)(2 * this.nbCenterLayers - 3), (float)(2 * this.nbCenterLayers));
      }
   }

   private Point getMatrixCenter() {
      ResultPoint pointA;
      ResultPoint pointB;
      ResultPoint pointC;
      ResultPoint pointD;
      int cy;
      try {
         ResultPoint[] cornerPoints;
         pointA = (cornerPoints = (new WhiteRectangleDetector(this.image)).detect())[0];
         pointB = cornerPoints[1];
         pointC = cornerPoints[2];
         pointD = cornerPoints[3];
      } catch (NotFoundException var9) {
         cy = this.image.getWidth() / 2;
         int cy = this.image.getHeight() / 2;
         pointA = this.getFirstDifferent(new Point(cy + 7, cy - 7), false, 1, -1).toResultPoint();
         pointB = this.getFirstDifferent(new Point(cy + 7, cy + 7), false, 1, 1).toResultPoint();
         pointC = this.getFirstDifferent(new Point(cy - 7, cy + 7), false, -1, 1).toResultPoint();
         pointD = this.getFirstDifferent(new Point(cy - 7, cy - 7), false, -1, -1).toResultPoint();
      }

      int cx = MathUtils.round((pointA.getX() + pointD.getX() + pointB.getX() + pointC.getX()) / 4.0F);
      cy = MathUtils.round((pointA.getY() + pointD.getY() + pointB.getY() + pointC.getY()) / 4.0F);

      try {
         ResultPoint[] cornerPoints;
         pointA = (cornerPoints = (new WhiteRectangleDetector(this.image, 15, cx, cy)).detect())[0];
         pointB = cornerPoints[1];
         pointC = cornerPoints[2];
         pointD = cornerPoints[3];
      } catch (NotFoundException var8) {
         pointA = this.getFirstDifferent(new Point(cx + 7, cy - 7), false, 1, -1).toResultPoint();
         pointB = this.getFirstDifferent(new Point(cx + 7, cy + 7), false, 1, 1).toResultPoint();
         pointC = this.getFirstDifferent(new Point(cx - 7, cy + 7), false, -1, 1).toResultPoint();
         pointD = this.getFirstDifferent(new Point(cx - 7, cy - 7), false, -1, -1).toResultPoint();
      }

      cx = MathUtils.round((pointA.getX() + pointD.getX() + pointB.getX() + pointC.getX()) / 4.0F);
      cy = MathUtils.round((pointA.getY() + pointD.getY() + pointB.getY() + pointC.getY()) / 4.0F);
      return new Point(cx, cy);
   }

   private ResultPoint[] getMatrixCornerPoints(ResultPoint[] bullsEyeCorners) {
      return expandSquare(bullsEyeCorners, (float)(2 * this.nbCenterLayers), (float)this.getDimension());
   }

   private BitMatrix sampleGrid(BitMatrix image, ResultPoint topLeft, ResultPoint topRight, ResultPoint bottomRight, ResultPoint bottomLeft) throws NotFoundException {
      GridSampler sampler = GridSampler.getInstance();
      int dimension;
      float low = (float)(dimension = this.getDimension()) / 2.0F - (float)this.nbCenterLayers;
      float high = (float)dimension / 2.0F + (float)this.nbCenterLayers;
      return sampler.sampleGrid(image, dimension, dimension, low, low, high, low, high, high, low, high, topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY(), bottomRight.getX(), bottomRight.getY(), bottomLeft.getX(), bottomLeft.getY());
   }

   private int sampleLine(ResultPoint p1, ResultPoint p2, int size) {
      int result = 0;
      float d;
      float moduleSize = (d = distance(p1, p2)) / (float)size;
      float px = p1.getX();
      float py = p1.getY();
      float dx = moduleSize * (p2.getX() - p1.getX()) / d;
      float dy = moduleSize * (p2.getY() - p1.getY()) / d;

      for(int i = 0; i < size; ++i) {
         if (this.image.get(MathUtils.round(px + (float)i * dx), MathUtils.round(py + (float)i * dy))) {
            result |= 1 << size - i - 1;
         }
      }

      return result;
   }

   private boolean isWhiteOrBlackRectangle(Point p1, Point p2, Point p3, Point p4) {
      p1 = new Point(p1.getX() - 3, p1.getY() + 3);
      p2 = new Point(p2.getX() - 3, p2.getY() - 3);
      p3 = new Point(p3.getX() + 3, p3.getY() - 3);
      p4 = new Point(p4.getX() + 3, p4.getY() + 3);
      int cInit;
      if ((cInit = this.getColor(p4, p1)) == 0) {
         return false;
      } else if (this.getColor(p1, p2) != cInit) {
         return false;
      } else if (this.getColor(p2, p3) != cInit) {
         return false;
      } else {
         return this.getColor(p3, p4) == cInit;
      }
   }

   private int getColor(Point p1, Point p2) {
      float d = distance(p1, p2);
      float dx = (float)(p2.getX() - p1.getX()) / d;
      float dy = (float)(p2.getY() - p1.getY()) / d;
      int error = 0;
      float px = (float)p1.getX();
      float py = (float)p1.getY();
      boolean colorModel = this.image.get(p1.getX(), p1.getY());
      int iMax = (int)Math.ceil((double)d);

      for(int i = 0; i < iMax; ++i) {
         px += dx;
         py += dy;
         if (this.image.get(MathUtils.round(px), MathUtils.round(py)) != colorModel) {
            ++error;
         }
      }

      float errRatio;
      if ((errRatio = (float)error / d) > 0.1F && errRatio < 0.9F) {
         return 0;
      } else if (errRatio <= 0.1F == colorModel) {
         return 1;
      } else {
         return -1;
      }
   }

   private Point getFirstDifferent(Point init, boolean color, int dx, int dy) {
      int x = init.getX() + dx;

      int y;
      for(y = init.getY() + dy; this.isValid(x, y) && this.image.get(x, y) == color; y += dy) {
         x += dx;
      }

      x -= dx;

      for(y -= dy; this.isValid(x, y) && this.image.get(x, y) == color; x += dx) {
      }

      for(x -= dx; this.isValid(x, y) && this.image.get(x, y) == color; y += dy) {
      }

      y -= dy;
      return new Point(x, y);
   }

   private static ResultPoint[] expandSquare(ResultPoint[] cornerPoints, float oldSide, float newSide) {
      float ratio = newSide / (oldSide * 2.0F);
      float dx = cornerPoints[0].getX() - cornerPoints[2].getX();
      float dy = cornerPoints[0].getY() - cornerPoints[2].getY();
      float centerx = (cornerPoints[0].getX() + cornerPoints[2].getX()) / 2.0F;
      float centery = (cornerPoints[0].getY() + cornerPoints[2].getY()) / 2.0F;
      ResultPoint result0 = new ResultPoint(centerx + ratio * dx, centery + ratio * dy);
      ResultPoint result2 = new ResultPoint(centerx - ratio * dx, centery - ratio * dy);
      dx = cornerPoints[1].getX() - cornerPoints[3].getX();
      dy = cornerPoints[1].getY() - cornerPoints[3].getY();
      centerx = (cornerPoints[1].getX() + cornerPoints[3].getX()) / 2.0F;
      centery = (cornerPoints[1].getY() + cornerPoints[3].getY()) / 2.0F;
      ResultPoint result1 = new ResultPoint(centerx + ratio * dx, centery + ratio * dy);
      ResultPoint result3 = new ResultPoint(centerx - ratio * dx, centery - ratio * dy);
      return new ResultPoint[]{result0, result1, result2, result3};
   }

   private boolean isValid(int x, int y) {
      return x >= 0 && x < this.image.getWidth() && y > 0 && y < this.image.getHeight();
   }

   private boolean isValid(ResultPoint point) {
      int x = MathUtils.round(point.getX());
      int y = MathUtils.round(point.getY());
      return this.isValid(x, y);
   }

   private static float distance(Point a, Point b) {
      return MathUtils.distance(a.getX(), a.getY(), b.getX(), b.getY());
   }

   private static float distance(ResultPoint a, ResultPoint b) {
      return MathUtils.distance(a.getX(), a.getY(), b.getX(), b.getY());
   }

   private int getDimension() {
      if (this.compact) {
         return 4 * this.nbLayers + 11;
      } else {
         return this.nbLayers <= 4 ? 4 * this.nbLayers + 15 : 4 * this.nbLayers + 2 * ((this.nbLayers - 4) / 8 + 1) + 15;
      }
   }

   static final class Point {
      private final int x;
      private final int y;

      ResultPoint toResultPoint() {
         return new ResultPoint((float)this.getX(), (float)this.getY());
      }

      Point(int x, int y) {
         this.x = x;
         this.y = y;
      }

      int getX() {
         return this.x;
      }

      int getY() {
         return this.y;
      }

      public String toString() {
         return "<" + this.x + ' ' + this.y + '>';
      }
   }
}
