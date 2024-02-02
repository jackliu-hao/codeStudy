/*     */ package com.google.zxing.pdf417.decoder;
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
/*     */ final class BoundingBox
/*     */ {
/*     */   private BitMatrix image;
/*     */   private ResultPoint topLeft;
/*     */   private ResultPoint bottomLeft;
/*     */   private ResultPoint topRight;
/*     */   private ResultPoint bottomRight;
/*     */   private int minX;
/*     */   private int maxX;
/*     */   private int minY;
/*     */   private int maxY;
/*     */   
/*     */   BoundingBox(BitMatrix image, ResultPoint topLeft, ResultPoint bottomLeft, ResultPoint topRight, ResultPoint bottomRight) throws NotFoundException {
/*  43 */     if ((topLeft == null && topRight == null) || (bottomLeft == null && bottomRight == null) || (topLeft != null && bottomLeft == null) || (topRight != null && bottomRight == null))
/*     */     {
/*     */ 
/*     */       
/*  47 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*  49 */     init(image, topLeft, bottomLeft, topRight, bottomRight);
/*     */   }
/*     */   
/*     */   BoundingBox(BoundingBox boundingBox) {
/*  53 */     init(boundingBox.image, boundingBox.topLeft, boundingBox.bottomLeft, boundingBox.topRight, boundingBox.bottomRight);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(BitMatrix image, ResultPoint topLeft, ResultPoint bottomLeft, ResultPoint topRight, ResultPoint bottomRight) {
/*  61 */     this.image = image;
/*  62 */     this.topLeft = topLeft;
/*  63 */     this.bottomLeft = bottomLeft;
/*  64 */     this.topRight = topRight;
/*  65 */     this.bottomRight = bottomRight;
/*  66 */     calculateMinMaxValues();
/*     */   }
/*     */   
/*     */   static BoundingBox merge(BoundingBox leftBox, BoundingBox rightBox) throws NotFoundException {
/*  70 */     if (leftBox == null) {
/*  71 */       return rightBox;
/*     */     }
/*  73 */     if (rightBox == null) {
/*  74 */       return leftBox;
/*     */     }
/*  76 */     return new BoundingBox(leftBox.image, leftBox.topLeft, leftBox.bottomLeft, rightBox.topRight, rightBox.bottomRight);
/*     */   }
/*     */   
/*     */   BoundingBox addMissingRows(int missingStartRows, int missingEndRows, boolean isLeft) throws NotFoundException {
/*  80 */     ResultPoint newTopLeft = this.topLeft;
/*  81 */     ResultPoint newBottomLeft = this.bottomLeft;
/*  82 */     ResultPoint newTopRight = this.topRight;
/*  83 */     ResultPoint newBottomRight = this.bottomRight;
/*     */     
/*  85 */     if (missingStartRows > 0) {
/*     */       ResultPoint top;
/*     */       int newMinY;
/*  88 */       if ((newMinY = (int)(top = isLeft ? this.topLeft : this.topRight).getY() - missingStartRows) < 0) {
/*  89 */         newMinY = 0;
/*     */       }
/*  91 */       ResultPoint newTop = new ResultPoint(top.getX(), newMinY);
/*  92 */       if (isLeft) {
/*  93 */         newTopLeft = newTop;
/*     */       } else {
/*  95 */         newTopRight = newTop;
/*     */       } 
/*     */     } 
/*     */     
/*  99 */     if (missingEndRows > 0) {
/*     */       ResultPoint bottom;
/*     */       int newMaxY;
/* 102 */       if ((newMaxY = (int)(bottom = isLeft ? this.bottomLeft : this.bottomRight).getY() + missingEndRows) >= this.image.getHeight()) {
/* 103 */         newMaxY = this.image.getHeight() - 1;
/*     */       }
/* 105 */       ResultPoint newBottom = new ResultPoint(bottom.getX(), newMaxY);
/* 106 */       if (isLeft) {
/* 107 */         newBottomLeft = newBottom;
/*     */       } else {
/* 109 */         newBottomRight = newBottom;
/*     */       } 
/*     */     } 
/*     */     
/* 113 */     calculateMinMaxValues();
/* 114 */     return new BoundingBox(this.image, newTopLeft, newBottomLeft, newTopRight, newBottomRight);
/*     */   }
/*     */   
/*     */   private void calculateMinMaxValues() {
/* 118 */     if (this.topLeft == null) {
/* 119 */       this.topLeft = new ResultPoint(0.0F, this.topRight.getY());
/* 120 */       this.bottomLeft = new ResultPoint(0.0F, this.bottomRight.getY());
/* 121 */     } else if (this.topRight == null) {
/* 122 */       this.topRight = new ResultPoint((this.image.getWidth() - 1), this.topLeft.getY());
/* 123 */       this.bottomRight = new ResultPoint((this.image.getWidth() - 1), this.bottomLeft.getY());
/*     */     } 
/*     */     
/* 126 */     this.minX = (int)Math.min(this.topLeft.getX(), this.bottomLeft.getX());
/* 127 */     this.maxX = (int)Math.max(this.topRight.getX(), this.bottomRight.getX());
/* 128 */     this.minY = (int)Math.min(this.topLeft.getY(), this.topRight.getY());
/* 129 */     this.maxY = (int)Math.max(this.bottomLeft.getY(), this.bottomRight.getY());
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
/*     */   int getMinX() {
/* 145 */     return this.minX;
/*     */   }
/*     */   
/*     */   int getMaxX() {
/* 149 */     return this.maxX;
/*     */   }
/*     */   
/*     */   int getMinY() {
/* 153 */     return this.minY;
/*     */   }
/*     */   
/*     */   int getMaxY() {
/* 157 */     return this.maxY;
/*     */   }
/*     */   
/*     */   ResultPoint getTopLeft() {
/* 161 */     return this.topLeft;
/*     */   }
/*     */   
/*     */   ResultPoint getTopRight() {
/* 165 */     return this.topRight;
/*     */   }
/*     */   
/*     */   ResultPoint getBottomLeft() {
/* 169 */     return this.bottomLeft;
/*     */   }
/*     */   
/*     */   ResultPoint getBottomRight() {
/* 173 */     return this.bottomRight;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\decoder\BoundingBox.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */