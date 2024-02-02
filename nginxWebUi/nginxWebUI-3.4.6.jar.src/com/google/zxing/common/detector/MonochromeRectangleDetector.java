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
/*     */ @Deprecated
/*     */ public final class MonochromeRectangleDetector
/*     */ {
/*     */   private static final int MAX_MODULES = 32;
/*     */   private final BitMatrix image;
/*     */   
/*     */   public MonochromeRectangleDetector(BitMatrix image) {
/*  39 */     this.image = image;
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
/*     */   public ResultPoint[] detect() throws NotFoundException {
/*  53 */     int height = this.image.getHeight();
/*  54 */     int width = this.image.getWidth();
/*  55 */     int halfHeight = height / 2;
/*  56 */     int halfWidth = width / 2;
/*  57 */     int deltaY = Math.max(1, height / 256);
/*  58 */     int deltaX = Math.max(1, width / 256);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  66 */     int top = (int)findCornerFromCenter(halfWidth, 0, 0, width, halfHeight, -deltaY, 0, height, halfWidth / 2).getY() - 1;
/*     */     
/*     */     ResultPoint pointB;
/*  69 */     int left = (int)(pointB = findCornerFromCenter(halfWidth, -deltaX, 0, width, halfHeight, 0, top, height, halfHeight / 2)).getX() - 1;
/*     */     
/*     */     ResultPoint pointC;
/*  72 */     int right = (int)(pointC = findCornerFromCenter(halfWidth, deltaX, left, width, halfHeight, 0, top, height, halfHeight / 2)).getX() + 1;
/*     */     
/*     */     ResultPoint pointD;
/*  75 */     int bottom = (int)(pointD = findCornerFromCenter(halfWidth, 0, left, right, halfHeight, deltaY, top, height, halfWidth / 2)).getY() + 1;
/*     */ 
/*     */     
/*  78 */     ResultPoint pointA = findCornerFromCenter(halfWidth, 0, left, right, halfHeight, -deltaY, top, bottom, halfWidth / 4);
/*     */ 
/*     */     
/*  81 */     return new ResultPoint[] { pointA, pointB, pointC, pointD };
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ResultPoint findCornerFromCenter(int centerX, int deltaX, int left, int right, int centerY, int deltaY, int top, int bottom, int maxWhiteRun) throws NotFoundException {
/* 111 */     int[] lastRange = null;
/* 112 */     int y = centerY, x = centerX;
/* 113 */     for (; y < bottom && y >= top && x < right && x >= left; 
/* 114 */       y += deltaY, x += deltaX) {
/*     */       int[] range;
/* 116 */       if (deltaX == 0) {
/*     */         
/* 118 */         range = blackWhiteRange(y, maxWhiteRun, left, right, true);
/*     */       } else {
/*     */         
/* 121 */         range = blackWhiteRange(x, maxWhiteRun, top, bottom, false);
/*     */       } 
/* 123 */       if (range == null) {
/* 124 */         if (lastRange == null) {
/* 125 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/*     */         
/* 128 */         if (deltaX == 0) {
/* 129 */           int lastY = y - deltaY;
/* 130 */           if (lastRange[0] < centerX) {
/* 131 */             if (lastRange[1] > centerX)
/*     */             {
/* 133 */               return new ResultPoint(lastRange[(deltaY > 0) ? 0 : 1], lastY);
/*     */             }
/* 135 */             return new ResultPoint(lastRange[0], lastY);
/*     */           } 
/* 137 */           return new ResultPoint(lastRange[1], lastY);
/*     */         } 
/*     */         
/* 140 */         int lastX = x - deltaX;
/* 141 */         if (lastRange[0] < centerY) {
/* 142 */           if (lastRange[1] > centerY) {
/* 143 */             return new ResultPoint(lastX, lastRange[(deltaX < 0) ? 0 : 1]);
/*     */           }
/* 145 */           return new ResultPoint(lastX, lastRange[0]);
/*     */         } 
/* 147 */         return new ResultPoint(lastX, lastRange[1]);
/*     */       } 
/*     */ 
/*     */       
/* 151 */       lastRange = range;
/*     */     } 
/* 153 */     throw NotFoundException.getNotFoundInstance();
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
/*     */   private int[] blackWhiteRange(int fixedDimension, int maxWhiteRun, int minDim, int maxDim, boolean horizontal) {
/* 175 */     int center = (minDim + maxDim) / 2, start = center;
/* 176 */     while (start >= minDim) {
/* 177 */       if (horizontal ? this.image.get(start, fixedDimension) : this.image.get(fixedDimension, start)) {
/* 178 */         start--; continue;
/*     */       } 
/* 180 */       int whiteRunStart = start;
/*     */       do {
/* 182 */         start--;
/* 183 */       } while (start >= minDim && (horizontal ? this.image.get(start, fixedDimension) : this.image
/* 184 */         .get(fixedDimension, start)));
/* 185 */       int whiteRunSize = whiteRunStart - start;
/* 186 */       if (start < minDim || whiteRunSize > maxWhiteRun) {
/* 187 */         start = whiteRunStart;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 192 */     start++;
/*     */ 
/*     */     
/* 195 */     int end = center;
/* 196 */     while (end < maxDim) {
/* 197 */       if (horizontal ? this.image.get(end, fixedDimension) : this.image.get(fixedDimension, end)) {
/* 198 */         end++; continue;
/*     */       } 
/* 200 */       int whiteRunStart = end;
/*     */       do {
/* 202 */         end++;
/* 203 */       } while (end < maxDim && (horizontal ? this.image.get(end, fixedDimension) : this.image
/* 204 */         .get(fixedDimension, end)));
/* 205 */       int whiteRunSize = end - whiteRunStart;
/* 206 */       if (end >= maxDim || whiteRunSize > maxWhiteRun) {
/* 207 */         end = whiteRunStart;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 212 */     end--;
/*     */     
/* 214 */     return (end > start) ? new int[] { start, end } : null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\detector\MonochromeRectangleDetector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */