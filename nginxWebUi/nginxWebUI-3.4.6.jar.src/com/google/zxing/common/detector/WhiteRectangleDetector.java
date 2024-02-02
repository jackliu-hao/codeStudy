/*     */ package com.google.zxing.common.detector;
/*     */ 
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.common.BitMatrix;
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
/*     */ public final class WhiteRectangleDetector
/*     */ {
/*     */   private static final int INIT_SIZE = 10;
/*     */   private static final int CORR = 1;
/*     */   private final BitMatrix image;
/*     */   private final int height;
/*     */   private final int width;
/*     */   private final int leftInit;
/*     */   private final int rightInit;
/*     */   private final int downInit;
/*     */   private final int upInit;
/*     */   
/*     */   public WhiteRectangleDetector(BitMatrix image) throws NotFoundException {
/*  47 */     this(image, 10, image.getWidth() / 2, image.getHeight() / 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WhiteRectangleDetector(BitMatrix image, int initSize, int x, int y) throws NotFoundException {
/*  58 */     this.image = image;
/*  59 */     this.height = image.getHeight();
/*  60 */     this.width = image.getWidth();
/*  61 */     int halfsize = initSize / 2;
/*  62 */     this.leftInit = x - halfsize;
/*  63 */     this.rightInit = x + halfsize;
/*  64 */     this.upInit = y - halfsize;
/*  65 */     this.downInit = y + halfsize;
/*  66 */     if (this.upInit < 0 || this.leftInit < 0 || this.downInit >= this.height || this.rightInit >= this.width) {
/*  67 */       throw NotFoundException.getNotFoundInstance();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultPoint[] detect() throws NotFoundException {
/*  87 */     int left = this.leftInit;
/*  88 */     int right = this.rightInit;
/*  89 */     int up = this.upInit;
/*  90 */     int down = this.downInit;
/*  91 */     boolean sizeExceeded = false;
/*  92 */     boolean aBlackPointFoundOnBorder = true;
/*  93 */     boolean atLeastOneBlackPointFoundOnBorder = false;
/*     */     
/*  95 */     boolean atLeastOneBlackPointFoundOnRight = false;
/*  96 */     boolean atLeastOneBlackPointFoundOnBottom = false;
/*  97 */     boolean atLeastOneBlackPointFoundOnLeft = false;
/*  98 */     boolean atLeastOneBlackPointFoundOnTop = false;
/*     */     
/* 100 */     while (aBlackPointFoundOnBorder) {
/*     */       
/* 102 */       aBlackPointFoundOnBorder = false;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 107 */       boolean rightBorderNotWhite = true;
/* 108 */       while ((rightBorderNotWhite || !atLeastOneBlackPointFoundOnRight) && right < this.width) {
/*     */         
/* 110 */         if (rightBorderNotWhite = containsBlackPoint(up, down, right, false)) {
/* 111 */           right++;
/* 112 */           aBlackPointFoundOnBorder = true;
/* 113 */           atLeastOneBlackPointFoundOnRight = true; continue;
/* 114 */         }  if (!atLeastOneBlackPointFoundOnRight) {
/* 115 */           right++;
/*     */         }
/*     */       } 
/*     */       
/* 119 */       if (right >= this.width) {
/* 120 */         sizeExceeded = true;
/*     */ 
/*     */         
/*     */         break;
/*     */       } 
/*     */ 
/*     */       
/* 127 */       boolean bottomBorderNotWhite = true;
/* 128 */       while ((bottomBorderNotWhite || !atLeastOneBlackPointFoundOnBottom) && down < this.height) {
/*     */         
/* 130 */         if (bottomBorderNotWhite = containsBlackPoint(left, right, down, true)) {
/* 131 */           down++;
/* 132 */           aBlackPointFoundOnBorder = true;
/* 133 */           atLeastOneBlackPointFoundOnBottom = true; continue;
/* 134 */         }  if (!atLeastOneBlackPointFoundOnBottom) {
/* 135 */           down++;
/*     */         }
/*     */       } 
/*     */       
/* 139 */       if (down >= this.height) {
/* 140 */         sizeExceeded = true;
/*     */ 
/*     */         
/*     */         break;
/*     */       } 
/*     */ 
/*     */       
/* 147 */       boolean leftBorderNotWhite = true;
/* 148 */       while ((leftBorderNotWhite || !atLeastOneBlackPointFoundOnLeft) && left >= 0) {
/*     */         
/* 150 */         if (leftBorderNotWhite = containsBlackPoint(up, down, left, false)) {
/* 151 */           left--;
/* 152 */           aBlackPointFoundOnBorder = true;
/* 153 */           atLeastOneBlackPointFoundOnLeft = true; continue;
/* 154 */         }  if (!atLeastOneBlackPointFoundOnLeft) {
/* 155 */           left--;
/*     */         }
/*     */       } 
/*     */       
/* 159 */       if (left < 0) {
/* 160 */         sizeExceeded = true;
/*     */ 
/*     */         
/*     */         break;
/*     */       } 
/*     */ 
/*     */       
/* 167 */       boolean topBorderNotWhite = true;
/* 168 */       while ((topBorderNotWhite || !atLeastOneBlackPointFoundOnTop) && up >= 0) {
/*     */         
/* 170 */         if (topBorderNotWhite = containsBlackPoint(left, right, up, true)) {
/* 171 */           up--;
/* 172 */           aBlackPointFoundOnBorder = true;
/* 173 */           atLeastOneBlackPointFoundOnTop = true; continue;
/* 174 */         }  if (!atLeastOneBlackPointFoundOnTop) {
/* 175 */           up--;
/*     */         }
/*     */       } 
/*     */       
/* 179 */       if (up < 0) {
/* 180 */         sizeExceeded = true;
/*     */         
/*     */         break;
/*     */       } 
/* 184 */       if (aBlackPointFoundOnBorder) {
/* 185 */         atLeastOneBlackPointFoundOnBorder = true;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 190 */     if (!sizeExceeded && atLeastOneBlackPointFoundOnBorder) {
/*     */       
/* 192 */       int maxSize = right - left;
/*     */       
/* 194 */       ResultPoint z = null;
/* 195 */       for (int i = 1; z == null && i < maxSize; i++) {
/* 196 */         z = getBlackPointOnSegment(left, (down - i), (left + i), down);
/*     */       }
/*     */       
/* 199 */       if (z == null) {
/* 200 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/*     */       
/* 203 */       ResultPoint t = null;
/*     */       
/* 205 */       for (int j = 1; t == null && j < maxSize; j++) {
/* 206 */         t = getBlackPointOnSegment(left, (up + j), (left + j), up);
/*     */       }
/*     */       
/* 209 */       if (t == null) {
/* 210 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/*     */       
/* 213 */       ResultPoint x = null;
/*     */       
/* 215 */       for (int k = 1; x == null && k < maxSize; k++) {
/* 216 */         x = getBlackPointOnSegment(right, (up + k), (right - k), up);
/*     */       }
/*     */       
/* 219 */       if (x == null) {
/* 220 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/*     */       
/* 223 */       ResultPoint y = null;
/*     */       
/* 225 */       for (int m = 1; y == null && m < maxSize; m++) {
/* 226 */         y = getBlackPointOnSegment(right, (down - m), (right - m), down);
/*     */       }
/*     */       
/* 229 */       if (y == null) {
/* 230 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/*     */       
/* 233 */       return centerEdges(y, z, x, t);
/*     */     } 
/*     */     
/* 236 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */ 
/*     */   
/*     */   private ResultPoint getBlackPointOnSegment(float aX, float aY, float bX, float bY) {
/* 241 */     int dist = MathUtils.round(MathUtils.distance(aX, aY, bX, bY));
/* 242 */     float xStep = (bX - aX) / dist;
/* 243 */     float yStep = (bY - aY) / dist;
/*     */     
/* 245 */     for (int i = 0; i < dist; i++) {
/* 246 */       int x = MathUtils.round(aX + i * xStep);
/* 247 */       int y = MathUtils.round(aY + i * yStep);
/* 248 */       if (this.image.get(x, y)) {
/* 249 */         return new ResultPoint(x, y);
/*     */       }
/*     */     } 
/* 252 */     return null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ResultPoint[] centerEdges(ResultPoint y, ResultPoint z, ResultPoint x, ResultPoint t) {
/* 278 */     float yi = y.getX();
/* 279 */     float yj = y.getY();
/* 280 */     float zi = z.getX();
/* 281 */     float zj = z.getY();
/* 282 */     float xi = x.getX();
/* 283 */     float xj = x.getY();
/* 284 */     float ti = t.getX();
/* 285 */     float tj = t.getY();
/*     */     
/* 287 */     if (yi < this.width / 2.0F) {
/* 288 */       return new ResultPoint[] { new ResultPoint(ti - 1.0F, tj + 1.0F), new ResultPoint(zi + 1.0F, zj + 1.0F), new ResultPoint(xi - 1.0F, xj - 1.0F), new ResultPoint(yi + 1.0F, yj - 1.0F) };
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 294 */     return new ResultPoint[] { new ResultPoint(ti + 1.0F, tj + 1.0F), new ResultPoint(zi + 1.0F, zj - 1.0F), new ResultPoint(xi - 1.0F, xj + 1.0F), new ResultPoint(yi - 1.0F, yj - 1.0F) };
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
/*     */   private boolean containsBlackPoint(int a, int b, int fixed, boolean horizontal) {
/* 313 */     if (horizontal) {
/* 314 */       for (int x = a; x <= b; x++) {
/* 315 */         if (this.image.get(x, fixed)) {
/* 316 */           return true;
/*     */         }
/*     */       } 
/*     */     } else {
/* 320 */       for (int y = a; y <= b; y++) {
/* 321 */         if (this.image.get(fixed, y)) {
/* 322 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 327 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\detector\WhiteRectangleDetector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */