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
/*     */ public final class SimpleCMYKColorSpace
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
/*  62 */       theInstance = new SimpleCMYKColorSpace();
/*     */     }
/*  64 */     return theInstance;
/*     */   }
/*     */   
/*     */   private SimpleCMYKColorSpace() {
/*  68 */     super(9, 4);
/*  69 */     this.csRGB = ColorSpace.getInstance(1004);
/*     */   }
/*     */   
/*     */   public boolean equals(Object o) {
/*  73 */     return (o != null && o instanceof SimpleCMYKColorSpace);
/*     */   }
/*     */   
/*     */   public float[] toRGB(float[] colorvalue) {
/*  77 */     float C = colorvalue[0];
/*  78 */     float M = colorvalue[1];
/*  79 */     float Y = colorvalue[2];
/*  80 */     float K = colorvalue[3];
/*     */     
/*  82 */     float K1 = 1.0F - K;
/*     */ 
/*     */     
/*  85 */     float[] rgbvalue = { K1 * (1.0F - C), K1 * (1.0F - M), K1 * (1.0F - Y) };
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  90 */     for (int i = 0; i < 3; i++) {
/*  91 */       float v = rgbvalue[i];
/*     */       
/*  93 */       if (v < 0.0F) v = 0.0F;
/*     */       
/*  95 */       if (v < 0.0031308F) {
/*  96 */         rgbvalue[i] = 12.92F * v;
/*     */       } else {
/*  98 */         if (v > 1.0F) v = 1.0F;
/*     */         
/* 100 */         rgbvalue[i] = (float)(1.055D * Math.pow(v, 0.4166666666666667D) - 0.055D);
/*     */       } 
/*     */     } 
/*     */     
/* 104 */     return rgbvalue;
/*     */   }
/*     */ 
/*     */   
/*     */   public float[] fromRGB(float[] rgbvalue) {
/* 109 */     for (int i = 0; i < 3; i++) {
/* 110 */       if (rgbvalue[i] < 0.040449936F) {
/* 111 */         rgbvalue[i] = rgbvalue[i] / 12.92F;
/*     */       } else {
/* 113 */         rgbvalue[i] = 
/* 114 */           (float)Math.pow((rgbvalue[i] + 0.055D) / 1.055D, 2.4D);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 119 */     float C = 1.0F - rgbvalue[0];
/* 120 */     float M = 1.0F - rgbvalue[1];
/* 121 */     float Y = 1.0F - rgbvalue[2];
/* 122 */     float K = Math.min(C, Math.min(M, Y));
/*     */ 
/*     */     
/* 125 */     if (K != 1.0F) {
/* 126 */       float K1 = 1.0F - K;
/*     */       
/* 128 */       C = (C - K) / K1;
/* 129 */       M = (M - K) / K1;
/* 130 */       Y = (Y - K) / K1;
/*     */     } else {
/* 132 */       C = M = Y = 0.0F;
/*     */     } 
/*     */     
/* 135 */     return new float[] { C, M, Y, K };
/*     */   }
/*     */   
/*     */   public float[] toCIEXYZ(float[] colorvalue) {
/* 139 */     return this.csRGB.toCIEXYZ(toRGB(colorvalue));
/*     */   }
/*     */   
/*     */   public float[] fromCIEXYZ(float[] xyzvalue) {
/* 143 */     return fromRGB(this.csRGB.fromCIEXYZ(xyzvalue));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\common\SimpleCMYKColorSpace.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */