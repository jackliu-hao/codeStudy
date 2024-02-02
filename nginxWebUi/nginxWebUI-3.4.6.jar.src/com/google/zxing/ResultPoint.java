/*     */ package com.google.zxing;
/*     */ 
/*     */ import com.google.zxing.common.detector.MathUtils;
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
/*     */ public class ResultPoint
/*     */ {
/*     */   private final float x;
/*     */   private final float y;
/*     */   
/*     */   public ResultPoint(float x, float y) {
/*  33 */     this.x = x;
/*  34 */     this.y = y;
/*     */   }
/*     */   
/*     */   public final float getX() {
/*  38 */     return this.x;
/*     */   }
/*     */   
/*     */   public final float getY() {
/*  42 */     return this.y;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean equals(Object other) {
/*  47 */     if (other instanceof ResultPoint) {
/*  48 */       ResultPoint otherPoint = (ResultPoint)other;
/*  49 */       return (this.x == otherPoint.x && this.y == otherPoint.y);
/*     */     } 
/*  51 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/*  56 */     return 31 * Float.floatToIntBits(this.x) + Float.floatToIntBits(this.y);
/*     */   }
/*     */ 
/*     */   
/*     */   public final String toString() {
/*  61 */     return "(" + this.x + ',' + this.y + ')';
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void orderBestPatterns(ResultPoint[] patterns) {
/*     */     ResultPoint pointA, pointB, pointC;
/*  73 */     float zeroOneDistance = distance(patterns[0], patterns[1]);
/*  74 */     float oneTwoDistance = distance(patterns[1], patterns[2]);
/*  75 */     float zeroTwoDistance = distance(patterns[0], patterns[2]);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  81 */     if (oneTwoDistance >= zeroOneDistance && oneTwoDistance >= zeroTwoDistance) {
/*  82 */       pointB = patterns[0];
/*  83 */       pointA = patterns[1];
/*  84 */       pointC = patterns[2];
/*  85 */     } else if (zeroTwoDistance >= oneTwoDistance && zeroTwoDistance >= zeroOneDistance) {
/*  86 */       pointB = patterns[1];
/*  87 */       pointA = patterns[0];
/*  88 */       pointC = patterns[2];
/*     */     } else {
/*  90 */       pointB = patterns[2];
/*  91 */       pointA = patterns[0];
/*  92 */       pointC = patterns[1];
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  99 */     if (crossProductZ(pointA, pointB, pointC) < 0.0F) {
/* 100 */       ResultPoint temp = pointA;
/* 101 */       pointA = pointC;
/* 102 */       pointC = temp;
/*     */     } 
/*     */     
/* 105 */     patterns[0] = pointA;
/* 106 */     patterns[1] = pointB;
/* 107 */     patterns[2] = pointC;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float distance(ResultPoint pattern1, ResultPoint pattern2) {
/* 117 */     return MathUtils.distance(pattern1.x, pattern1.y, pattern2.x, pattern2.y);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static float crossProductZ(ResultPoint pointA, ResultPoint pointB, ResultPoint pointC) {
/* 126 */     float bX = pointB.x;
/* 127 */     float bY = pointB.y;
/* 128 */     return (pointC.x - bX) * (pointA.y - bY) - (pointC.y - bY) * (pointA.x - bX);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\ResultPoint.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */