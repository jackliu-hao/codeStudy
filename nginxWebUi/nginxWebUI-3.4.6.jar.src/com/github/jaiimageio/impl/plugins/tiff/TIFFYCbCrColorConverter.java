/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFColorConverter;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFYCbCrColorConverter
/*     */   extends TIFFColorConverter
/*     */ {
/*  55 */   private float LumaRed = 0.299F;
/*  56 */   private float LumaGreen = 0.587F;
/*  57 */   private float LumaBlue = 0.114F;
/*     */   
/*  59 */   private float referenceBlackY = 0.0F;
/*  60 */   private float referenceWhiteY = 255.0F;
/*     */   
/*  62 */   private float referenceBlackCb = 128.0F;
/*  63 */   private float referenceWhiteCb = 255.0F;
/*     */   
/*  65 */   private float referenceBlackCr = 128.0F;
/*  66 */   private float referenceWhiteCr = 255.0F;
/*     */   
/*  68 */   private float codingRangeY = 255.0F;
/*  69 */   private float codingRangeCbCr = 127.0F;
/*     */   
/*     */   public TIFFYCbCrColorConverter(TIFFImageMetadata metadata) {
/*  72 */     TIFFImageMetadata tmetadata = metadata;
/*     */ 
/*     */     
/*  75 */     TIFFField f = tmetadata.getTIFFField(529);
/*  76 */     if (f != null && f.getCount() == 3) {
/*  77 */       this.LumaRed = f.getAsFloat(0);
/*  78 */       this.LumaGreen = f.getAsFloat(1);
/*  79 */       this.LumaBlue = f.getAsFloat(2);
/*     */     } 
/*     */ 
/*     */     
/*  83 */     f = tmetadata.getTIFFField(532);
/*  84 */     if (f != null && f.getCount() == 6) {
/*  85 */       this.referenceBlackY = f.getAsFloat(0);
/*  86 */       this.referenceWhiteY = f.getAsFloat(1);
/*  87 */       this.referenceBlackCb = f.getAsFloat(2);
/*  88 */       this.referenceWhiteCb = f.getAsFloat(3);
/*  89 */       this.referenceBlackCr = f.getAsFloat(4);
/*  90 */       this.referenceWhiteCr = f.getAsFloat(5);
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
/*     */   public void fromRGB(float r, float g, float b, float[] result) {
/* 108 */     float Y = this.LumaRed * r + this.LumaGreen * g + this.LumaBlue * b;
/* 109 */     float Cb = (b - Y) / (2.0F - 2.0F * this.LumaBlue);
/* 110 */     float Cr = (r - Y) / (2.0F - 2.0F * this.LumaRed);
/*     */ 
/*     */     
/* 113 */     result[0] = Y * (this.referenceWhiteY - this.referenceBlackY) / this.codingRangeY + this.referenceBlackY;
/*     */     
/* 115 */     result[1] = Cb * (this.referenceWhiteCb - this.referenceBlackCb) / this.codingRangeCbCr + this.referenceBlackCb;
/*     */     
/* 117 */     result[2] = Cr * (this.referenceWhiteCr - this.referenceBlackCr) / this.codingRangeCbCr + this.referenceBlackCr;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void toRGB(float x0, float x1, float x2, float[] rgb) {
/* 123 */     float Y = (x0 - this.referenceBlackY) * this.codingRangeY / (this.referenceWhiteY - this.referenceBlackY);
/*     */     
/* 125 */     float Cb = (x1 - this.referenceBlackCb) * this.codingRangeCbCr / (this.referenceWhiteCb - this.referenceBlackCb);
/*     */     
/* 127 */     float Cr = (x2 - this.referenceBlackCr) * this.codingRangeCbCr / (this.referenceWhiteCr - this.referenceBlackCr);
/*     */ 
/*     */ 
/*     */     
/* 131 */     rgb[0] = Cr * (2.0F - 2.0F * this.LumaRed) + Y;
/* 132 */     rgb[2] = Cb * (2.0F - 2.0F * this.LumaBlue) + Y;
/* 133 */     rgb[1] = (Y - this.LumaBlue * rgb[2] - this.LumaRed * rgb[0]) / this.LumaGreen;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFYCbCrColorConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */