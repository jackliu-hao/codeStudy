/*     */ package com.github.jaiimageio.impl.common;
/*     */ 
/*     */ import java.awt.color.ColorSpace;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class InvertedCMYKColorSpace
/*     */   extends ColorSpace
/*     */ {
/*  54 */   private static ColorSpace theInstance = null;
/*     */   
/*     */   private ColorSpace csRGB;
/*     */   
/*     */   private static final double power1 = 0.4166666666666667D;
/*     */   
/*     */   public static final synchronized ColorSpace getInstance() {
/*  61 */     if (theInstance == null) {
/*  62 */       theInstance = new InvertedCMYKColorSpace();
/*     */     }
/*  64 */     return theInstance;
/*     */   }
/*     */   
/*     */   private InvertedCMYKColorSpace() {
/*  68 */     super(9, 4);
/*  69 */     this.csRGB = ColorSpace.getInstance(1004);
/*     */   }
/*     */   
/*     */   public boolean equals(Object o) {
/*  73 */     return (o != null && o instanceof InvertedCMYKColorSpace);
/*     */   }
/*     */   
/*     */   public float[] toRGB(float[] colorvalue) {
/*  77 */     float C = colorvalue[0];
/*  78 */     float M = colorvalue[1];
/*  79 */     float Y = colorvalue[2];
/*  80 */     float K = colorvalue[3];
/*     */ 
/*     */     
/*  83 */     float[] rgbvalue = { K * C, K * M, K * Y };
/*     */ 
/*     */     
/*  86 */     for (int i = 0; i < 3; i++) {
/*  87 */       float v = rgbvalue[i];
/*     */       
/*  89 */       if (v < 0.0F) v = 0.0F;
/*     */       
/*  91 */       if (v < 0.0031308F) {
/*  92 */         rgbvalue[i] = 12.92F * v;
/*     */       } else {
/*  94 */         if (v > 1.0F) v = 1.0F;
/*     */         
/*  96 */         rgbvalue[i] = (float)(1.055D * Math.pow(v, 0.4166666666666667D) - 0.055D);
/*     */       } 
/*     */     } 
/*     */     
/* 100 */     return rgbvalue;
/*     */   }
/*     */ 
/*     */   
/*     */   public float[] fromRGB(float[] rgbvalue) {
/* 105 */     for (int i = 0; i < 3; i++) {
/* 106 */       if (rgbvalue[i] < 0.040449936F) {
/* 107 */         rgbvalue[i] = rgbvalue[i] / 12.92F;
/*     */       } else {
/* 109 */         rgbvalue[i] = 
/* 110 */           (float)Math.pow((rgbvalue[i] + 0.055D) / 1.055D, 2.4D);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 115 */     float C = rgbvalue[0];
/* 116 */     float M = rgbvalue[1];
/* 117 */     float Y = rgbvalue[2];
/* 118 */     float K = Math.max(C, Math.max(M, Y));
/*     */ 
/*     */     
/* 121 */     if (K != 0.0F) {
/* 122 */       C /= K;
/* 123 */       M /= K;
/* 124 */       Y /= K;
/*     */     } else {
/* 126 */       C = M = Y = 1.0F;
/*     */     } 
/*     */     
/* 129 */     return new float[] { C, M, Y, K };
/*     */   }
/*     */   
/*     */   public float[] toCIEXYZ(float[] colorvalue) {
/* 133 */     return this.csRGB.toCIEXYZ(toRGB(colorvalue));
/*     */   }
/*     */   
/*     */   public float[] fromCIEXYZ(float[] xyzvalue) {
/* 137 */     return fromRGB(this.csRGB.fromCIEXYZ(xyzvalue));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\common\InvertedCMYKColorSpace.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */