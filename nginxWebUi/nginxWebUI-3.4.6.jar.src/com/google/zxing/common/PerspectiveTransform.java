/*     */ package com.google.zxing.common;
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
/*     */ public final class PerspectiveTransform
/*     */ {
/*     */   private final float a11;
/*     */   private final float a12;
/*     */   private final float a13;
/*     */   private final float a21;
/*     */   private final float a22;
/*     */   private final float a23;
/*     */   private final float a31;
/*     */   private final float a32;
/*     */   private final float a33;
/*     */   
/*     */   private PerspectiveTransform(float a11, float a21, float a31, float a12, float a22, float a32, float a13, float a23, float a33) {
/*  41 */     this.a11 = a11;
/*  42 */     this.a12 = a12;
/*  43 */     this.a13 = a13;
/*  44 */     this.a21 = a21;
/*  45 */     this.a22 = a22;
/*  46 */     this.a23 = a23;
/*  47 */     this.a31 = a31;
/*  48 */     this.a32 = a32;
/*  49 */     this.a33 = a33;
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
/*     */   public static PerspectiveTransform quadrilateralToQuadrilateral(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, float x0p, float y0p, float x1p, float y1p, float x2p, float y2p, float x3p, float y3p) {
/*  61 */     PerspectiveTransform qToS = quadrilateralToSquare(x0, y0, x1, y1, x2, y2, x3, y3);
/*     */     
/*  63 */     return squareToQuadrilateral(x0p, y0p, x1p, y1p, x2p, y2p, x3p, y3p).times(qToS);
/*     */   }
/*     */   
/*     */   public void transformPoints(float[] points) {
/*  67 */     int max = points.length;
/*  68 */     float a11 = this.a11;
/*  69 */     float a12 = this.a12;
/*  70 */     float a13 = this.a13;
/*  71 */     float a21 = this.a21;
/*  72 */     float a22 = this.a22;
/*  73 */     float a23 = this.a23;
/*  74 */     float a31 = this.a31;
/*  75 */     float a32 = this.a32;
/*  76 */     float a33 = this.a33;
/*  77 */     for (int i = 0; i < max; i += 2) {
/*  78 */       float x = points[i];
/*  79 */       float y = points[i + 1];
/*  80 */       float denominator = a13 * x + a23 * y + a33;
/*  81 */       points[i] = (a11 * x + a21 * y + a31) / denominator;
/*  82 */       points[i + 1] = (a12 * x + a22 * y + a32) / denominator;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void transformPoints(float[] xValues, float[] yValues) {
/*  87 */     int n = xValues.length;
/*  88 */     for (int i = 0; i < n; i++) {
/*  89 */       float x = xValues[i];
/*  90 */       float y = yValues[i];
/*  91 */       float denominator = this.a13 * x + this.a23 * y + this.a33;
/*  92 */       xValues[i] = (this.a11 * x + this.a21 * y + this.a31) / denominator;
/*  93 */       yValues[i] = (this.a12 * x + this.a22 * y + this.a32) / denominator;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PerspectiveTransform squareToQuadrilateral(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
/* 101 */     float dx3 = x0 - x1 + x2 - x3;
/* 102 */     float dy3 = y0 - y1 + y2 - y3;
/* 103 */     if (dx3 == 0.0F && dy3 == 0.0F)
/*     */     {
/* 105 */       return new PerspectiveTransform(x1 - x0, x2 - x1, x0, y1 - y0, y2 - y1, y0, 0.0F, 0.0F, 1.0F);
/*     */     }
/*     */ 
/*     */     
/* 109 */     float dx1 = x1 - x2;
/* 110 */     float dx2 = x3 - x2;
/* 111 */     float dy1 = y1 - y2;
/* 112 */     float dy2 = y3 - y2;
/* 113 */     float denominator = dx1 * dy2 - dx2 * dy1;
/* 114 */     float a13 = (dx3 * dy2 - dx2 * dy3) / denominator;
/* 115 */     float a23 = (dx1 * dy3 - dx3 * dy1) / denominator;
/* 116 */     return new PerspectiveTransform(x1 - x0 + a13 * x1, x3 - x0 + a23 * x3, x0, y1 - y0 + a13 * y1, y3 - y0 + a23 * y3, y0, a13, a23, 1.0F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PerspectiveTransform quadrilateralToSquare(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
/* 127 */     return squareToQuadrilateral(x0, y0, x1, y1, x2, y2, x3, y3).buildAdjoint();
/*     */   }
/*     */ 
/*     */   
/*     */   PerspectiveTransform buildAdjoint() {
/* 132 */     return new PerspectiveTransform(this.a22 * this.a33 - this.a23 * this.a32, this.a23 * this.a31 - this.a21 * this.a33, this.a21 * this.a32 - this.a22 * this.a31, this.a13 * this.a32 - this.a12 * this.a33, this.a11 * this.a33 - this.a13 * this.a31, this.a12 * this.a31 - this.a11 * this.a32, this.a12 * this.a23 - this.a13 * this.a22, this.a13 * this.a21 - this.a11 * this.a23, this.a11 * this.a22 - this.a12 * this.a21);
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
/*     */   PerspectiveTransform times(PerspectiveTransform other) {
/* 144 */     return new PerspectiveTransform(this.a11 * other.a11 + this.a21 * other.a12 + this.a31 * other.a13, this.a11 * other.a21 + this.a21 * other.a22 + this.a31 * other.a23, this.a11 * other.a31 + this.a21 * other.a32 + this.a31 * other.a33, this.a12 * other.a11 + this.a22 * other.a12 + this.a32 * other.a13, this.a12 * other.a21 + this.a22 * other.a22 + this.a32 * other.a23, this.a12 * other.a31 + this.a22 * other.a32 + this.a32 * other.a33, this.a13 * other.a11 + this.a23 * other.a12 + this.a33 * other.a13, this.a13 * other.a21 + this.a23 * other.a22 + this.a33 * other.a23, this.a13 * other.a31 + this.a23 * other.a32 + this.a33 * other.a33);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\PerspectiveTransform.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */