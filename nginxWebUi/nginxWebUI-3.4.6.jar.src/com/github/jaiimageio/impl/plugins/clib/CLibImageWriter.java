/*     */ package com.github.jaiimageio.impl.plugins.clib;
/*     */ 
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.DataBuffer;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.DataBufferUShort;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.RenderedImage;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.ImageWriteParam;
/*     */ import javax.imageio.ImageWriter;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.spi.ImageWriterSpi;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class CLibImageWriter
/*     */   extends ImageWriter
/*     */ {
/*     */   private static final Object getDataBufferData(DataBuffer db) {
/*     */     Object data;
/*  70 */     int dType = db.getDataType();
/*  71 */     switch (dType) {
/*     */       case 0:
/*  73 */         data = ((DataBufferByte)db).getData();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  83 */         return data;case 1: data = ((DataBufferUShort)db).getData(); return data;
/*     */     } 
/*     */     throw new IllegalArgumentException(I18N.getString("Generic0") + " " + dType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Raster getContiguousData(RenderedImage im, Rectangle region) {
/*     */     Raster raster;
/*  97 */     if (im == null)
/*  98 */       throw new IllegalArgumentException("im == null"); 
/*  99 */     if (region == null) {
/* 100 */       throw new IllegalArgumentException("region == null");
/*     */     }
/*     */ 
/*     */     
/* 104 */     if (im.getNumXTiles() == 1 && im.getNumYTiles() == 1) {
/*     */       
/* 106 */       raster = im.getTile(im.getMinTileX(), im.getMinTileY());
/*     */ 
/*     */       
/* 109 */       Rectangle bounds = raster.getBounds();
/* 110 */       if (!bounds.equals(region)) {
/* 111 */         raster = raster.createChild(region.x, region.y, region.width, region.height, region.x, region.y, null);
/*     */ 
/*     */       
/*     */       }
/*     */ 
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 121 */       SampleModel sampleModel = im.getSampleModel();
/*     */       
/* 123 */       WritableRaster target = (sampleModel.getSampleSize(0) == 8) ? Raster.createInterleavedRaster(0, im
/* 124 */           .getWidth(), im
/* 125 */           .getHeight(), sampleModel
/* 126 */           .getNumBands(), new Point(im
/* 127 */             .getMinX(), im
/* 128 */             .getMinY())) : null;
/*     */ 
/*     */ 
/*     */       
/* 132 */       raster = im.copyData(target);
/*     */     } 
/*     */     
/* 135 */     return raster;
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
/*     */ 
/*     */   
/*     */   private static void reformat(Raster source, int[] sourceBands, int subsampleX, int subsampleY, WritableRaster dst) {
/* 161 */     if (source == null)
/* 162 */       throw new IllegalArgumentException("source == null!"); 
/* 163 */     if (dst == null) {
/* 164 */       throw new IllegalArgumentException("dst == null!");
/*     */     }
/*     */ 
/*     */     
/* 168 */     Rectangle sourceBounds = source.getBounds();
/* 169 */     if (sourceBounds.isEmpty()) {
/* 170 */       throw new IllegalArgumentException("source.getBounds().isEmpty()!");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 175 */     boolean isSubBanding = false;
/* 176 */     int numSourceBands = source.getSampleModel().getNumBands();
/* 177 */     if (sourceBands != null) {
/* 178 */       if (sourceBands.length > numSourceBands) {
/* 179 */         throw new IllegalArgumentException("sourceBands.length > numSourceBands!");
/*     */       }
/*     */ 
/*     */       
/* 183 */       boolean isRamp = (sourceBands.length == numSourceBands);
/* 184 */       for (int i = 0; i < sourceBands.length; i++) {
/* 185 */         if (sourceBands[i] < 0 || sourceBands[i] >= numSourceBands) {
/* 186 */           throw new IllegalArgumentException("sourceBands[i] < 0 || sourceBands[i] >= numSourceBands!");
/*     */         }
/* 188 */         if (sourceBands[i] != i) {
/* 189 */           isRamp = false;
/*     */         }
/*     */       } 
/*     */       
/* 193 */       isSubBanding = !isRamp;
/*     */     } 
/*     */ 
/*     */     
/* 197 */     int sourceWidth = sourceBounds.width;
/* 198 */     int[] pixels = new int[sourceWidth * numSourceBands];
/*     */ 
/*     */     
/* 201 */     int sourceX = sourceBounds.x;
/* 202 */     int sourceY = sourceBounds.y;
/* 203 */     int numBands = (sourceBands != null) ? sourceBands.length : numSourceBands;
/*     */     
/* 205 */     int dstWidth = dst.getWidth();
/* 206 */     int dstYMax = dst.getHeight() - 1;
/* 207 */     int copyFromIncrement = numSourceBands * subsampleX;
/*     */ 
/*     */     
/* 210 */     for (int dstY = 0; dstY <= dstYMax; dstY++) {
/*     */       
/* 212 */       source.getPixels(sourceX, sourceY, sourceWidth, 1, pixels);
/*     */ 
/*     */       
/* 215 */       if (isSubBanding) {
/* 216 */         int copyFrom = 0;
/* 217 */         int copyTo = 0;
/* 218 */         for (int i = 0; i < dstWidth; i++) {
/* 219 */           for (int j = 0; j < numBands; j++) {
/* 220 */             pixels[copyTo++] = pixels[copyFrom + sourceBands[j]];
/*     */           }
/* 222 */           copyFrom += copyFromIncrement;
/*     */         } 
/*     */       } else {
/* 225 */         int copyFrom = copyFromIncrement;
/* 226 */         int copyTo = numSourceBands;
/*     */         
/* 228 */         for (int i = 1; i < dstWidth; i++) {
/* 229 */           int k = copyFrom;
/* 230 */           for (int j = 0; j < numSourceBands; j++) {
/* 231 */             pixels[copyTo++] = pixels[k++];
/*     */           }
/* 233 */           copyFrom += copyFromIncrement;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 238 */       dst.setPixels(0, dstY, dstWidth, 1, pixels);
/*     */ 
/*     */       
/* 241 */       sourceY += subsampleY;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected CLibImageWriter(ImageWriterSpi originatingProvider) {
/* 246 */     super(originatingProvider);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
/* 252 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
/* 257 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
/* 263 */     return null;
/*     */   }
/*     */   
/*     */   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
/* 267 */     return null;
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
/*     */   private static final Rectangle getSourceRegion(ImageWriteParam param, int sourceMinX, int sourceMinY, int srcWidth, int srcHeight) {
/* 291 */     Rectangle sourceRegion = new Rectangle(sourceMinX, sourceMinY, srcWidth, srcHeight);
/*     */     
/* 293 */     if (param != null) {
/* 294 */       Rectangle region = param.getSourceRegion();
/* 295 */       if (region != null) {
/* 296 */         sourceRegion = sourceRegion.intersection(region);
/*     */       }
/*     */       
/* 299 */       int subsampleXOffset = param.getSubsamplingXOffset();
/* 300 */       int subsampleYOffset = param.getSubsamplingYOffset();
/* 301 */       sourceRegion.x += subsampleXOffset;
/* 302 */       sourceRegion.y += subsampleYOffset;
/* 303 */       sourceRegion.width -= subsampleXOffset;
/* 304 */       sourceRegion.height -= subsampleYOffset;
/*     */     } 
/*     */     
/* 307 */     return sourceRegion;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\clib\CLibImageWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */