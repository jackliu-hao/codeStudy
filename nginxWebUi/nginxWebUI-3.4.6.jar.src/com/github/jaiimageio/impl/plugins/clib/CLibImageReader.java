/*     */ package com.github.jaiimageio.impl.plugins.clib;
/*     */ 
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import javax.imageio.ImageReader;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.spi.ImageReaderSpi;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class CLibImageReader
/*     */   extends ImageReader
/*     */ {
/*  64 */   private int currIndex = -1;
/*     */ 
/*     */   
/*  67 */   private long highWaterMark = Long.MIN_VALUE;
/*     */ 
/*     */ 
/*     */   
/*  71 */   private ArrayList imageStartPosition = new ArrayList();
/*     */ 
/*     */   
/*  74 */   private int numImages = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   private int mlibImageIndex = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean subBandsMatch(int[] sourceBands, int[] destinationBands) {
/*  88 */     if (sourceBands == null && destinationBands == null)
/*  89 */       return true; 
/*  90 */     if (sourceBands != null && destinationBands != null) {
/*  91 */       if (sourceBands.length != destinationBands.length)
/*     */       {
/*  93 */         return false;
/*     */       }
/*  95 */       for (int i = 0; i < sourceBands.length; i++) {
/*  96 */         if (sourceBands[i] != destinationBands[i]) {
/*  97 */           return false;
/*     */         }
/*     */       } 
/* 100 */       return true;
/*     */     } 
/*     */     
/* 103 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private static final void subsample(Raster src, int subX, int subY, WritableRaster dst) {
/* 108 */     int sx0 = src.getMinX();
/* 109 */     int sy0 = src.getMinY();
/* 110 */     int sw = src.getWidth();
/* 111 */     int syUB = sy0 + src.getHeight();
/*     */     
/* 113 */     int dx0 = dst.getMinX();
/* 114 */     int dy0 = dst.getMinY();
/* 115 */     int dw = dst.getWidth();
/*     */     
/* 117 */     int b = src.getSampleModel().getNumBands();
/* 118 */     int t = src.getSampleModel().getDataType();
/*     */     
/* 120 */     int numSubSamples = (sw + subX - 1) / subX;
/*     */     
/* 122 */     if (t == 4 || t == 5) {
/* 123 */       float[] fsamples = new float[sw];
/* 124 */       float[] fsubsamples = new float[numSubSamples];
/*     */       
/* 126 */       for (int k = 0; k < b; k++) {
/* 127 */         for (int sy = sy0, dy = dy0; sy < syUB; sy += subY, dy++) {
/* 128 */           src.getSamples(sx0, sy, sw, 1, k, fsamples); int i, s;
/* 129 */           for (i = 0, s = 0; i < sw; s++, i += subX) {
/* 130 */             fsubsamples[s] = fsamples[i];
/*     */           }
/* 132 */           dst.setSamples(dx0, dy, dw, 1, k, fsubsamples);
/*     */         } 
/*     */       } 
/*     */     } else {
/* 136 */       int[] samples = new int[sw];
/* 137 */       int[] subsamples = new int[numSubSamples];
/*     */       
/* 139 */       for (int k = 0; k < b; k++) {
/* 140 */         for (int sy = sy0, dy = dy0; sy < syUB; sy += subY, dy++) {
/* 141 */           src.getSamples(sx0, sy, sw, 1, k, samples); int i, s;
/* 142 */           for (i = 0, s = 0; i < sw; s++, i += subX) {
/* 143 */             subsamples[s] = samples[i];
/*     */           }
/* 145 */           dst.setSamples(dx0, dy, dw, 1, k, subsamples);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected CLibImageReader(ImageReaderSpi originatingProvider) {
/* 152 */     super(originatingProvider);
/*     */   }
/*     */ 
/*     */   
/*     */   private class SoloIterator
/*     */     implements Iterator
/*     */   {
/*     */     Object theObject;
/*     */     
/*     */     SoloIterator(Object o) {
/* 162 */       if (o == null) {
/* 163 */         new IllegalArgumentException(
/* 164 */             I18N.getString("CLibImageReader0"));
/*     */       }
/* 166 */       this.theObject = o;
/*     */     }
/*     */     
/*     */     public boolean hasNext() {
/* 170 */       return (this.theObject != null);
/*     */     }
/*     */     
/*     */     public Object next() {
/* 174 */       if (this.theObject == null) {
/* 175 */         throw new NoSuchElementException();
/*     */       }
/* 177 */       Object theNextObject = this.theObject;
/* 178 */       this.theObject = null;
/* 179 */       return theNextObject;
/*     */     }
/*     */     
/*     */     public void remove() {
/* 183 */       throw new UnsupportedOperationException();
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
/*     */   protected int getImageIndex() {
/* 196 */     return this.mlibImageIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadata getStreamMetadata() throws IOException {
/* 201 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\clib\CLibImageReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */