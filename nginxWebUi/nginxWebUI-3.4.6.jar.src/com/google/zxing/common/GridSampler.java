/*     */ package com.google.zxing.common;
/*     */ 
/*     */ import com.google.zxing.NotFoundException;
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
/*     */ 
/*     */ 
/*     */ public abstract class GridSampler
/*     */ {
/*  36 */   private static GridSampler gridSampler = new DefaultGridSampler();
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
/*     */   public static void setGridSampler(GridSampler newGridSampler) {
/*  48 */     gridSampler = newGridSampler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static GridSampler getInstance() {
/*  55 */     return gridSampler;
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
/*     */   public abstract BitMatrix sampleGrid(BitMatrix paramBitMatrix, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float paramFloat9, float paramFloat10, float paramFloat11, float paramFloat12, float paramFloat13, float paramFloat14, float paramFloat15, float paramFloat16) throws NotFoundException;
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
/*     */   public abstract BitMatrix sampleGrid(BitMatrix paramBitMatrix, int paramInt1, int paramInt2, PerspectiveTransform paramPerspectiveTransform) throws NotFoundException;
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
/*     */   protected static void checkAndNudgePoints(BitMatrix image, float[] points) throws NotFoundException {
/* 121 */     int width = image.getWidth();
/* 122 */     int height = image.getHeight();
/*     */     
/* 124 */     boolean nudged = true; int offset;
/* 125 */     for (offset = 0; offset < points.length && nudged; offset += 2) {
/* 126 */       int x = (int)points[offset];
/* 127 */       int y = (int)points[offset + 1];
/* 128 */       if (x < -1 || x > width || y < -1 || y > height) {
/* 129 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/* 131 */       nudged = false;
/* 132 */       if (x == -1) {
/* 133 */         points[offset] = 0.0F;
/* 134 */         nudged = true;
/* 135 */       } else if (x == width) {
/* 136 */         points[offset] = (width - 1);
/* 137 */         nudged = true;
/*     */       } 
/* 139 */       if (y == -1) {
/* 140 */         points[offset + 1] = 0.0F;
/* 141 */         nudged = true;
/* 142 */       } else if (y == height) {
/* 143 */         points[offset + 1] = (height - 1);
/* 144 */         nudged = true;
/*     */       } 
/*     */     } 
/*     */     
/* 148 */     nudged = true;
/* 149 */     for (offset = points.length - 2; offset >= 0 && nudged; offset -= 2) {
/* 150 */       int x = (int)points[offset];
/* 151 */       int y = (int)points[offset + 1];
/* 152 */       if (x < -1 || x > width || y < -1 || y > height) {
/* 153 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/* 155 */       nudged = false;
/* 156 */       if (x == -1) {
/* 157 */         points[offset] = 0.0F;
/* 158 */         nudged = true;
/* 159 */       } else if (x == width) {
/* 160 */         points[offset] = (width - 1);
/* 161 */         nudged = true;
/*     */       } 
/* 163 */       if (y == -1) {
/* 164 */         points[offset + 1] = 0.0F;
/* 165 */         nudged = true;
/* 166 */       } else if (y == height) {
/* 167 */         points[offset + 1] = (height - 1);
/* 168 */         nudged = true;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\GridSampler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */