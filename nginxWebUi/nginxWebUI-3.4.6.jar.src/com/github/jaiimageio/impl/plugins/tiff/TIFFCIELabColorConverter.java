/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFColorConverter;
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
/*     */ public class TIFFCIELabColorConverter
/*     */   extends TIFFColorConverter
/*     */ {
/*     */   private static final float Xn = 95.047F;
/*     */   private static final float Yn = 100.0F;
/*     */   private static final float Zn = 108.883F;
/*  58 */   private static final float THRESHOLD = (float)Math.pow(0.008856D, 0.3333333333333333D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private float clamp(float x) {
/*  64 */     if (x < 0.0F)
/*  65 */       return 0.0F; 
/*  66 */     if (x > 100.0F) {
/*  67 */       return 255.0F;
/*     */     }
/*  69 */     return x * 2.55F;
/*     */   }
/*     */ 
/*     */   
/*     */   private float clamp2(float x) {
/*  74 */     if (x < 0.0F)
/*  75 */       return 0.0F; 
/*  76 */     if (x > 255.0F) {
/*  77 */       return 255.0F;
/*     */     }
/*  79 */     return x;
/*     */   }
/*     */ 
/*     */   
/*     */   public void fromRGB(float r, float g, float b, float[] result) {
/*  84 */     float X = 0.412453F * r + 0.35758F * g + 0.180423F * b;
/*  85 */     float Y = 0.212671F * r + 0.71516F * g + 0.072169F * b;
/*  86 */     float Z = 0.019334F * r + 0.119193F * g + 0.950227F * b;
/*     */     
/*  88 */     float YYn = Y / 100.0F;
/*  89 */     float XXn = X / 95.047F;
/*  90 */     float ZZn = Z / 108.883F;
/*     */     
/*  92 */     if (YYn < 0.008856F) {
/*  93 */       YYn = 7.787F * YYn + 0.13793103F;
/*     */     } else {
/*  95 */       YYn = (float)Math.pow(YYn, 0.3333333333333333D);
/*     */     } 
/*     */     
/*  98 */     if (XXn < 0.008856F) {
/*  99 */       XXn = 7.787F * XXn + 0.13793103F;
/*     */     } else {
/* 101 */       XXn = (float)Math.pow(XXn, 0.3333333333333333D);
/*     */     } 
/*     */     
/* 104 */     if (ZZn < 0.008856F) {
/* 105 */       ZZn = 7.787F * ZZn + 0.13793103F;
/*     */     } else {
/* 107 */       ZZn = (float)Math.pow(ZZn, 0.3333333333333333D);
/*     */     } 
/*     */     
/* 110 */     float LStar = 116.0F * YYn - 16.0F;
/* 111 */     float aStar = 500.0F * (XXn - YYn);
/* 112 */     float bStar = 200.0F * (YYn - ZZn);
/*     */     
/* 114 */     LStar *= 2.55F;
/* 115 */     if (aStar < 0.0F) {
/* 116 */       aStar += 256.0F;
/*     */     }
/* 118 */     if (bStar < 0.0F) {
/* 119 */       bStar += 256.0F;
/*     */     }
/*     */     
/* 122 */     result[0] = clamp2(LStar);
/* 123 */     result[1] = clamp2(aStar);
/* 124 */     result[2] = clamp2(bStar);
/*     */   }
/*     */   
/*     */   public void toRGB(float x0, float x1, float x2, float[] rgb) {
/* 128 */     float YYn, fY, X, Z, LStar = x0 * 100.0F / 255.0F;
/* 129 */     float aStar = (x1 > 128.0F) ? (x1 - 256.0F) : x1;
/* 130 */     float bStar = (x2 > 128.0F) ? (x2 - 256.0F) : x2;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 135 */     if (LStar < 8.0F) {
/* 136 */       YYn = LStar / 903.3F;
/* 137 */       fY = 7.787F * YYn + 0.13793103F;
/*     */     } else {
/* 139 */       float YYn_cubeRoot = (LStar + 16.0F) / 116.0F;
/* 140 */       YYn = YYn_cubeRoot * YYn_cubeRoot * YYn_cubeRoot;
/* 141 */       fY = (float)Math.pow(YYn, 0.3333333333333333D);
/*     */     } 
/* 143 */     float Y = YYn * 100.0F;
/*     */     
/* 145 */     float fX = fY + aStar / 500.0F;
/*     */     
/* 147 */     if (fX <= THRESHOLD) {
/* 148 */       X = 95.047F * (fX - 0.13793103F) / 7.787F;
/*     */     } else {
/* 150 */       X = 95.047F * fX * fX * fX;
/*     */     } 
/*     */     
/* 153 */     float fZ = fY - bStar / 200.0F;
/*     */     
/* 155 */     if (fZ <= THRESHOLD) {
/* 156 */       Z = 108.883F * (fZ - 0.13793103F) / 7.787F;
/*     */     } else {
/* 158 */       Z = 108.883F * fZ * fZ * fZ;
/*     */     } 
/*     */     
/* 161 */     float R = 3.240479F * X - 1.53715F * Y - 0.498535F * Z;
/* 162 */     float G = -0.969256F * X + 1.875992F * Y + 0.041556F * Z;
/* 163 */     float B = 0.055648F * X - 0.204043F * Y + 1.057311F * Z;
/*     */     
/* 165 */     rgb[0] = clamp(R);
/* 166 */     rgb[1] = clamp(G);
/* 167 */     rgb[2] = clamp(B);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFCIELabColorConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */