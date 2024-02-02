/*     */ package com.google.zxing.aztec.detector;
/*     */ 
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.aztec.AztecDetectorResult;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.common.GridSampler;
/*     */ import com.google.zxing.common.detector.MathUtils;
/*     */ import com.google.zxing.common.detector.WhiteRectangleDetector;
/*     */ import com.google.zxing.common.reedsolomon.GenericGF;
/*     */ import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
/*     */ import com.google.zxing.common.reedsolomon.ReedSolomonException;
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
/*     */ public final class Detector
/*     */ {
/*     */   private final BitMatrix image;
/*     */   private boolean compact;
/*     */   private int nbLayers;
/*     */   private int nbDataBlocks;
/*     */   private int nbCenterLayers;
/*     */   private int shift;
/*     */   
/*     */   public Detector(BitMatrix image) {
/*  48 */     this.image = image;
/*     */   }
/*     */   
/*     */   public AztecDetectorResult detect() throws NotFoundException {
/*  52 */     return detect(false);
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
/*     */   public AztecDetectorResult detect(boolean isMirror) throws NotFoundException {
/*  65 */     Point pCenter = getMatrixCenter();
/*     */ 
/*     */ 
/*     */     
/*  69 */     ResultPoint[] bullsEyeCorners = getBullsEyeCorners(pCenter);
/*     */     
/*  71 */     if (isMirror) {
/*  72 */       ResultPoint temp = bullsEyeCorners[0];
/*  73 */       bullsEyeCorners[0] = bullsEyeCorners[2];
/*  74 */       bullsEyeCorners[2] = temp;
/*     */     } 
/*     */ 
/*     */     
/*  78 */     extractParameters(bullsEyeCorners);
/*     */ 
/*     */     
/*  81 */     BitMatrix bits = sampleGrid(this.image, bullsEyeCorners[this.shift % 4], bullsEyeCorners[(this.shift + 1) % 4], bullsEyeCorners[(this.shift + 2) % 4], bullsEyeCorners[(this.shift + 3) % 4]);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  88 */     ResultPoint[] corners = getMatrixCornerPoints(bullsEyeCorners);
/*     */     
/*  90 */     return new AztecDetectorResult(bits, corners, this.compact, this.nbDataBlocks, this.nbLayers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void extractParameters(ResultPoint[] bullsEyeCorners) throws NotFoundException {
/* 100 */     if (!isValid(bullsEyeCorners[0]) || !isValid(bullsEyeCorners[1]) || 
/* 101 */       !isValid(bullsEyeCorners[2]) || !isValid(bullsEyeCorners[3])) {
/* 102 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/* 104 */     int length = 2 * this.nbCenterLayers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 110 */     int[] sides = { sampleLine(bullsEyeCorners[0], bullsEyeCorners[1], length), sampleLine(bullsEyeCorners[1], bullsEyeCorners[2], length), sampleLine(bullsEyeCorners[2], bullsEyeCorners[3], length), sampleLine(bullsEyeCorners[3], bullsEyeCorners[0], length) };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 117 */     this.shift = getRotation(sides, length);
/*     */ 
/*     */     
/* 120 */     long parameterData = 0L;
/* 121 */     for (int i = 0; i < 4; i++) {
/* 122 */       int side = sides[(this.shift + i) % 4];
/* 123 */       if (this.compact) {
/*     */ 
/*     */         
/* 126 */         parameterData = (parameterData << 7L) + (side >> 1 & 0x7F);
/*     */       }
/*     */       else {
/*     */         
/* 130 */         parameterData = (parameterData << 10L) + ((side >> 2 & 0x3E0) + (side >> 1 & 0x1F));
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 136 */     int correctedData = getCorrectedParameterData(parameterData, this.compact);
/*     */     
/* 138 */     if (this.compact) {
/*     */       
/* 140 */       this.nbLayers = (correctedData >> 6) + 1;
/* 141 */       this.nbDataBlocks = (correctedData & 0x3F) + 1;
/*     */       return;
/*     */     } 
/* 144 */     this.nbLayers = (correctedData >> 11) + 1;
/* 145 */     this.nbDataBlocks = (correctedData & 0x7FF) + 1;
/*     */   }
/*     */ 
/*     */   
/* 149 */   private static final int[] EXPECTED_CORNER_BITS = new int[] { 3808, 476, 2107, 1799 };
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
/*     */   private static int getRotation(int[] sides, int length) throws NotFoundException {
/* 166 */     int cornerBits = 0; int arrayOfInt[], i; byte b;
/* 167 */     for (i = (arrayOfInt = sides).length, b = 0; b < i; b++) {
/*     */       
/* 169 */       int side, t = ((side = arrayOfInt[b]) >> length - 2 << 1) + (side & 0x1);
/* 170 */       cornerBits = (cornerBits << 3) + t;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 175 */     cornerBits = ((cornerBits & 0x1) << 11) + (cornerBits >> 1);
/*     */ 
/*     */ 
/*     */     
/* 179 */     for (int shift = 0; shift < 4; shift++) {
/* 180 */       if (Integer.bitCount(cornerBits ^ EXPECTED_CORNER_BITS[shift]) <= 2) {
/* 181 */         return shift;
/*     */       }
/*     */     } 
/* 184 */     throw NotFoundException.getNotFoundInstance();
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
/*     */   private static int getCorrectedParameterData(long parameterData, boolean compact) throws NotFoundException {
/*     */     int numCodewords, numDataCodewords;
/* 198 */     if (compact) {
/* 199 */       numCodewords = 7;
/* 200 */       numDataCodewords = 2;
/*     */     } else {
/* 202 */       numCodewords = 10;
/* 203 */       numDataCodewords = 4;
/*     */     } 
/*     */     
/* 206 */     int numECCodewords = numCodewords - numDataCodewords;
/* 207 */     int[] parameterWords = new int[numCodewords];
/* 208 */     for (int i = numCodewords - 1; i >= 0; i--) {
/* 209 */       parameterWords[i] = (int)parameterData & 0xF;
/* 210 */       parameterData >>= 4L;
/*     */     } 
/*     */     try {
/* 213 */       (new ReedSolomonDecoder(GenericGF.AZTEC_PARAM))
/* 214 */         .decode(parameterWords, numECCodewords);
/* 215 */     } catch (ReedSolomonException reedSolomonException) {
/* 216 */       throw NotFoundException.getNotFoundInstance();
/*     */     } 
/*     */     
/* 219 */     int j = 0;
/* 220 */     for (int k = 0; k < numDataCodewords; k++) {
/* 221 */       j = (j << 4) + parameterWords[k];
/*     */     }
/* 223 */     return j;
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
/*     */   private ResultPoint[] getBullsEyeCorners(Point pCenter) throws NotFoundException {
/* 237 */     Point pina = pCenter;
/* 238 */     Point pinb = pCenter;
/* 239 */     Point pinc = pCenter;
/* 240 */     Point pind = pCenter;
/*     */     
/* 242 */     boolean color = true;
/*     */     
/* 244 */     for (this.nbCenterLayers = 1; this.nbCenterLayers < 9; ) {
/* 245 */       Point pouta = getFirstDifferent(pina, color, 1, -1);
/* 246 */       Point poutb = getFirstDifferent(pinb, color, 1, 1);
/* 247 */       Point poutc = getFirstDifferent(pinc, color, -1, 1);
/* 248 */       Point poutd = getFirstDifferent(pind, color, -1, -1);
/*     */ 
/*     */       
/*     */       float q;
/*     */ 
/*     */       
/* 254 */       if (this.nbCenterLayers <= 2 || ((
/*     */         
/* 256 */         q = distance(poutd, pouta) * this.nbCenterLayers / distance(pind, pina) * (this.nbCenterLayers + 2)) >= 0.75D && q <= 1.25D && isWhiteOrBlackRectangle(pouta, poutb, poutc, poutd))) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 261 */         pina = pouta;
/* 262 */         pinb = poutb;
/* 263 */         pinc = poutc;
/* 264 */         pind = poutd;
/*     */         
/* 266 */         color = !color; this.nbCenterLayers++;
/*     */       } 
/*     */     } 
/* 269 */     if (this.nbCenterLayers != 5 && this.nbCenterLayers != 7) {
/* 270 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 273 */     this.compact = (this.nbCenterLayers == 5);
/*     */ 
/*     */ 
/*     */     
/* 277 */     ResultPoint pinax = new ResultPoint(pina.getX() + 0.5F, pina.getY() - 0.5F);
/* 278 */     ResultPoint pinbx = new ResultPoint(pinb.getX() + 0.5F, pinb.getY() + 0.5F);
/* 279 */     ResultPoint pincx = new ResultPoint(pinc.getX() - 0.5F, pinc.getY() + 0.5F);
/* 280 */     ResultPoint pindx = new ResultPoint(pind.getX() - 0.5F, pind.getY() - 0.5F);
/*     */ 
/*     */ 
/*     */     
/* 284 */     return expandSquare(new ResultPoint[] { pinax, pinbx, pincx, pindx }, (2 * this.nbCenterLayers - 3), (2 * this.nbCenterLayers));
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
/*     */   private Point getMatrixCenter() {
/*     */     ResultPoint pointA, pointB, pointC, pointD;
/*     */     try {
/*     */       ResultPoint[] cornerPoints;
/* 305 */       pointA = (cornerPoints = (new WhiteRectangleDetector(this.image)).detect())[0];
/* 306 */       pointB = cornerPoints[1];
/* 307 */       pointC = cornerPoints[2];
/* 308 */       pointD = cornerPoints[3];
/*     */     }
/* 310 */     catch (NotFoundException notFoundException) {
/*     */ 
/*     */ 
/*     */       
/* 314 */       int j = this.image.getWidth() / 2;
/* 315 */       int k = this.image.getHeight() / 2;
/* 316 */       pointA = getFirstDifferent(new Point(j + 7, k - 7), false, 1, -1).toResultPoint();
/* 317 */       pointB = getFirstDifferent(new Point(j + 7, k + 7), false, 1, 1).toResultPoint();
/* 318 */       pointC = getFirstDifferent(new Point(j - 7, k + 7), false, -1, 1).toResultPoint();
/* 319 */       pointD = getFirstDifferent(new Point(j - 7, k - 7), false, -1, -1).toResultPoint();
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 324 */     int cx = MathUtils.round((pointA.getX() + pointD.getX() + pointB.getX() + pointC.getX()) / 4.0F);
/* 325 */     int cy = MathUtils.round((pointA.getY() + pointD.getY() + pointB.getY() + pointC.getY()) / 4.0F);
/*     */ 
/*     */     
/*     */     try {
/*     */       ResultPoint[] cornerPoints;
/*     */ 
/*     */       
/* 332 */       pointA = (cornerPoints = (new WhiteRectangleDetector(this.image, 15, cx, cy)).detect())[0];
/* 333 */       pointB = cornerPoints[1];
/* 334 */       pointC = cornerPoints[2];
/* 335 */       pointD = cornerPoints[3];
/* 336 */     } catch (NotFoundException notFoundException) {
/*     */ 
/*     */       
/* 339 */       pointA = getFirstDifferent(new Point(cx + 7, cy - 7), false, 1, -1).toResultPoint();
/* 340 */       pointB = getFirstDifferent(new Point(cx + 7, cy + 7), false, 1, 1).toResultPoint();
/* 341 */       pointC = getFirstDifferent(new Point(cx - 7, cy + 7), false, -1, 1).toResultPoint();
/* 342 */       pointD = getFirstDifferent(new Point(cx - 7, cy - 7), false, -1, -1).toResultPoint();
/*     */     } 
/*     */ 
/*     */     
/* 346 */     int i = MathUtils.round((pointA.getX() + pointD.getX() + pointB.getX() + pointC.getX()) / 4.0F);
/* 347 */     cy = MathUtils.round((pointA.getY() + pointD.getY() + pointB.getY() + pointC.getY()) / 4.0F);
/*     */     
/* 349 */     return new Point(i, cy);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ResultPoint[] getMatrixCornerPoints(ResultPoint[] bullsEyeCorners) {
/* 359 */     return expandSquare(bullsEyeCorners, (2 * this.nbCenterLayers), getDimension());
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
/*     */   private BitMatrix sampleGrid(BitMatrix image, ResultPoint topLeft, ResultPoint topRight, ResultPoint bottomRight, ResultPoint bottomLeft) throws NotFoundException {
/* 373 */     GridSampler sampler = GridSampler.getInstance();
/*     */     
/*     */     int dimension;
/* 376 */     float low = (dimension = getDimension()) / 2.0F - this.nbCenterLayers;
/* 377 */     float high = dimension / 2.0F + this.nbCenterLayers;
/*     */     
/* 379 */     return sampler.sampleGrid(image, dimension, dimension, low, low, high, low, high, high, low, high, topLeft
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 386 */         .getX(), topLeft.getY(), topRight
/* 387 */         .getX(), topRight.getY(), bottomRight
/* 388 */         .getX(), bottomRight.getY(), bottomLeft
/* 389 */         .getX(), bottomLeft.getY());
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
/*     */   private int sampleLine(ResultPoint p1, ResultPoint p2, int size) {
/* 401 */     int result = 0;
/*     */ 
/*     */     
/* 404 */     float d, moduleSize = (d = distance(p1, p2)) / size;
/* 405 */     float px = p1.getX();
/* 406 */     float py = p1.getY();
/* 407 */     float dx = moduleSize * (p2.getX() - p1.getX()) / d;
/* 408 */     float dy = moduleSize * (p2.getY() - p1.getY()) / d;
/* 409 */     for (int i = 0; i < size; i++) {
/* 410 */       if (this.image.get(MathUtils.round(px + i * dx), MathUtils.round(py + i * dy))) {
/* 411 */         result |= 1 << size - i - 1;
/*     */       }
/*     */     } 
/* 414 */     return result;
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
/*     */   private boolean isWhiteOrBlackRectangle(Point p1, Point p2, Point p3, Point p4) {
/* 428 */     p1 = new Point(p1.getX() - 3, p1.getY() + 3);
/* 429 */     p2 = new Point(p2.getX() - 3, p2.getY() - 3);
/* 430 */     p3 = new Point(p3.getX() + 3, p3.getY() - 3);
/* 431 */     p4 = new Point(p4.getX() + 3, p4.getY() + 3);
/*     */     
/*     */     int cInit;
/*     */     
/* 435 */     if ((cInit = getColor(p4, p1)) == 0) {
/* 436 */       return false;
/*     */     }
/*     */     
/* 439 */     if (getColor(p1, p2) != 
/*     */       
/* 441 */       cInit) {
/* 442 */       return false;
/*     */     }
/*     */     
/* 445 */     if (getColor(p2, p3) != 
/*     */       
/* 447 */       cInit) {
/* 448 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 453 */     return (getColor(p3, p4) == cInit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getColor(Point p1, Point p2) {
/* 463 */     float d = distance(p1, p2);
/* 464 */     float dx = (p2.getX() - p1.getX()) / d;
/* 465 */     float dy = (p2.getY() - p1.getY()) / d;
/* 466 */     int error = 0;
/*     */     
/* 468 */     float px = p1.getX();
/* 469 */     float py = p1.getY();
/*     */     
/* 471 */     boolean colorModel = this.image.get(p1.getX(), p1.getY());
/*     */     
/* 473 */     int iMax = (int)Math.ceil(d);
/* 474 */     for (int i = 0; i < iMax; i++) {
/* 475 */       px += dx;
/* 476 */       py += dy;
/* 477 */       if (this.image.get(MathUtils.round(px), MathUtils.round(py)) != colorModel) {
/* 478 */         error++;
/*     */       }
/*     */     } 
/*     */     
/*     */     float errRatio;
/*     */     
/* 484 */     if ((errRatio = error / d) > 0.1F && errRatio < 0.9F) {
/* 485 */       return 0;
/*     */     }
/*     */     
/* 488 */     return (((errRatio <= 0.1F)) == colorModel) ? 1 : -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Point getFirstDifferent(Point init, boolean color, int dx, int dy) {
/* 495 */     int x = init.getX() + dx;
/* 496 */     int y = init.getY() + dy;
/*     */     
/* 498 */     while (isValid(x, y) && this.image.get(x, y) == color) {
/* 499 */       x += dx;
/* 500 */       y += dy;
/*     */     } 
/*     */     
/* 503 */     x -= dx;
/* 504 */     y -= dy;
/*     */     
/* 506 */     while (isValid(x, y) && this.image.get(x, y) == color) {
/* 507 */       x += dx;
/*     */     }
/* 509 */     x -= dx;
/*     */     
/* 511 */     while (isValid(x, y) && this.image.get(x, y) == color) {
/* 512 */       y += dy;
/*     */     }
/* 514 */     y -= dy;
/*     */     
/* 516 */     return new Point(x, y);
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
/*     */   private static ResultPoint[] expandSquare(ResultPoint[] cornerPoints, float oldSide, float newSide) {
/* 528 */     float ratio = newSide / oldSide * 2.0F;
/* 529 */     float dx = cornerPoints[0].getX() - cornerPoints[2].getX();
/* 530 */     float dy = cornerPoints[0].getY() - cornerPoints[2].getY();
/* 531 */     float centerx = (cornerPoints[0].getX() + cornerPoints[2].getX()) / 2.0F;
/* 532 */     float centery = (cornerPoints[0].getY() + cornerPoints[2].getY()) / 2.0F;
/*     */     
/* 534 */     ResultPoint result0 = new ResultPoint(centerx + ratio * dx, centery + ratio * dy);
/* 535 */     ResultPoint result2 = new ResultPoint(centerx - ratio * dx, centery - ratio * dy);
/*     */     
/* 537 */     dx = cornerPoints[1].getX() - cornerPoints[3].getX();
/* 538 */     dy = cornerPoints[1].getY() - cornerPoints[3].getY();
/* 539 */     centerx = (cornerPoints[1].getX() + cornerPoints[3].getX()) / 2.0F;
/* 540 */     centery = (cornerPoints[1].getY() + cornerPoints[3].getY()) / 2.0F;
/* 541 */     ResultPoint result1 = new ResultPoint(centerx + ratio * dx, centery + ratio * dy);
/* 542 */     ResultPoint result3 = new ResultPoint(centerx - ratio * dx, centery - ratio * dy);
/*     */     
/* 544 */     return new ResultPoint[] { result0, result1, result2, result3 };
/*     */   }
/*     */   
/*     */   private boolean isValid(int x, int y) {
/* 548 */     return (x >= 0 && x < this.image.getWidth() && y > 0 && y < this.image.getHeight());
/*     */   }
/*     */   
/*     */   private boolean isValid(ResultPoint point) {
/* 552 */     int x = MathUtils.round(point.getX());
/* 553 */     int y = MathUtils.round(point.getY());
/* 554 */     return isValid(x, y);
/*     */   }
/*     */   
/*     */   private static float distance(Point a, Point b) {
/* 558 */     return MathUtils.distance(a.getX(), a.getY(), b.getX(), b.getY());
/*     */   }
/*     */   
/*     */   private static float distance(ResultPoint a, ResultPoint b) {
/* 562 */     return MathUtils.distance(a.getX(), a.getY(), b.getX(), b.getY());
/*     */   }
/*     */   
/*     */   private int getDimension() {
/* 566 */     if (this.compact) {
/* 567 */       return 4 * this.nbLayers + 11;
/*     */     }
/* 569 */     if (this.nbLayers <= 4) {
/* 570 */       return 4 * this.nbLayers + 15;
/*     */     }
/* 572 */     return 4 * this.nbLayers + 2 * ((this.nbLayers - 4) / 8 + 1) + 15;
/*     */   }
/*     */   
/*     */   static final class Point {
/*     */     private final int x;
/*     */     private final int y;
/*     */     
/*     */     ResultPoint toResultPoint() {
/* 580 */       return new ResultPoint(getX(), getY());
/*     */     }
/*     */     
/*     */     Point(int x, int y) {
/* 584 */       this.x = x;
/* 585 */       this.y = y;
/*     */     }
/*     */     
/*     */     int getX() {
/* 589 */       return this.x;
/*     */     }
/*     */     
/*     */     int getY() {
/* 593 */       return this.y;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 598 */       return "<" + this.x + ' ' + this.y + '>';
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\aztec\detector\Detector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */