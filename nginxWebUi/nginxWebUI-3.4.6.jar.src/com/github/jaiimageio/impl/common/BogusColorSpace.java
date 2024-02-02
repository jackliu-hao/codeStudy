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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BogusColorSpace
/*     */   extends ColorSpace
/*     */ {
/*     */   private static int getType(int numComponents) {
/*  63 */     if (numComponents < 1) {
/*  64 */       throw new IllegalArgumentException("numComponents < 1!");
/*     */     }
/*     */ 
/*     */     
/*  68 */     switch (numComponents)
/*     */     { case 1:
/*  70 */         type = 6;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  79 */         return type; }  int type = numComponents + 10; return type;
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
/*     */   public BogusColorSpace(int numComponents) {
/*  91 */     super(getType(numComponents), numComponents);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float[] toRGB(float[] colorvalue) {
/* 101 */     if (colorvalue.length < getNumComponents()) {
/* 102 */       throw new ArrayIndexOutOfBoundsException("colorvalue.length < getNumComponents()");
/*     */     }
/*     */ 
/*     */     
/* 106 */     float[] rgbvalue = new float[3];
/*     */     
/* 108 */     System.arraycopy(colorvalue, 0, rgbvalue, 0, 
/* 109 */         Math.min(3, getNumComponents()));
/*     */     
/* 111 */     return colorvalue;
/*     */   }
/*     */   
/*     */   public float[] fromRGB(float[] rgbvalue) {
/* 115 */     if (rgbvalue.length < 3) {
/* 116 */       throw new ArrayIndexOutOfBoundsException("rgbvalue.length < 3");
/*     */     }
/*     */ 
/*     */     
/* 120 */     float[] colorvalue = new float[getNumComponents()];
/*     */     
/* 122 */     System.arraycopy(rgbvalue, 0, colorvalue, 0, 
/* 123 */         Math.min(3, colorvalue.length));
/*     */     
/* 125 */     return rgbvalue;
/*     */   }
/*     */   
/*     */   public float[] toCIEXYZ(float[] colorvalue) {
/* 129 */     if (colorvalue.length < getNumComponents()) {
/* 130 */       throw new ArrayIndexOutOfBoundsException("colorvalue.length < getNumComponents()");
/*     */     }
/*     */ 
/*     */     
/* 134 */     float[] xyzvalue = new float[3];
/*     */     
/* 136 */     System.arraycopy(colorvalue, 0, xyzvalue, 0, 
/* 137 */         Math.min(3, getNumComponents()));
/*     */     
/* 139 */     return colorvalue;
/*     */   }
/*     */   
/*     */   public float[] fromCIEXYZ(float[] xyzvalue) {
/* 143 */     if (xyzvalue.length < 3) {
/* 144 */       throw new ArrayIndexOutOfBoundsException("xyzvalue.length < 3");
/*     */     }
/*     */ 
/*     */     
/* 148 */     float[] colorvalue = new float[getNumComponents()];
/*     */     
/* 150 */     System.arraycopy(xyzvalue, 0, colorvalue, 0, 
/* 151 */         Math.min(3, colorvalue.length));
/*     */     
/* 153 */     return xyzvalue;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\common\BogusColorSpace.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */