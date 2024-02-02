/*     */ package com.github.jaiimageio.impl.plugins.raw;
/*     */ 
/*     */ import com.github.jaiimageio.stream.RawImageInputStream;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.RenderedImage;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import javax.imageio.ImageReadParam;
/*     */ import javax.imageio.ImageReader;
/*     */ import javax.imageio.ImageTypeSpecifier;
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
/*     */ 
/*     */ 
/*     */ public class RawImageReader
/*     */   extends ImageReader
/*     */ {
/*  75 */   private RawImageInputStream iis = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void computeRegionsWrapper(ImageReadParam param, int srcWidth, int srcHeight, BufferedImage image, Rectangle srcRegion, Rectangle destRegion) {
/*  87 */     computeRegions(param, srcWidth, srcHeight, image, srcRegion, destRegion);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RawImageReader(ImageReaderSpi originator) {
/*  95 */     super(originator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
/* 105 */     super.setInput(input, seekForwardOnly, ignoreMetadata);
/* 106 */     this.iis = (RawImageInputStream)input;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNumImages(boolean allowSearch) throws IOException {
/* 111 */     return this.iis.getNumImages();
/*     */   }
/*     */   
/*     */   public int getWidth(int imageIndex) throws IOException {
/* 115 */     checkIndex(imageIndex);
/* 116 */     return (this.iis.getImageDimension(imageIndex)).width;
/*     */   }
/*     */   
/*     */   public int getHeight(int imageIndex) throws IOException {
/* 120 */     checkIndex(imageIndex);
/*     */     
/* 122 */     return (this.iis.getImageDimension(imageIndex)).height;
/*     */   }
/*     */   
/*     */   public int getTileWidth(int imageIndex) throws IOException {
/* 126 */     checkIndex(imageIndex);
/* 127 */     return this.iis.getImageType().getSampleModel().getWidth();
/*     */   }
/*     */   
/*     */   public int getTileHeight(int imageIndex) throws IOException {
/* 131 */     checkIndex(imageIndex);
/* 132 */     return this.iis.getImageType().getSampleModel().getHeight();
/*     */   }
/*     */   
/*     */   private void checkIndex(int imageIndex) throws IOException {
/* 136 */     if (imageIndex < 0 || imageIndex >= getNumImages(true)) {
/* 137 */       throw new IndexOutOfBoundsException(I18N.getString("RawImageReader0"));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator getImageTypes(int imageIndex) throws IOException {
/* 143 */     checkIndex(imageIndex);
/* 144 */     ArrayList<ImageTypeSpecifier> list = new ArrayList(1);
/* 145 */     list.add(this.iis.getImageType());
/* 146 */     return list.iterator();
/*     */   }
/*     */   
/*     */   public ImageReadParam getDefaultReadParam() {
/* 150 */     return new ImageReadParam();
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
/* 155 */     return null;
/*     */   }
/*     */   
/*     */   public IIOMetadata getStreamMetadata() throws IOException {
/* 159 */     return null;
/*     */   }
/*     */   
/*     */   public boolean isRandomAccessEasy(int imageIndex) throws IOException {
/* 163 */     checkIndex(imageIndex);
/* 164 */     return true;
/*     */   }
/*     */   
/*     */   public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
/*     */     WritableRaster raster;
/* 169 */     if (param == null)
/* 170 */       param = getDefaultReadParam(); 
/* 171 */     checkIndex(imageIndex);
/* 172 */     clearAbortRequest();
/* 173 */     processImageStarted(imageIndex);
/*     */     
/* 175 */     BufferedImage bi = param.getDestination();
/* 176 */     RawRenderedImage image = new RawRenderedImage(this.iis, this, param, imageIndex);
/*     */     
/* 178 */     Point offset = param.getDestinationOffset();
/*     */ 
/*     */     
/* 181 */     if (bi == null) {
/* 182 */       ColorModel colorModel = image.getColorModel();
/* 183 */       SampleModel sampleModel = image.getSampleModel();
/*     */ 
/*     */       
/* 186 */       ImageTypeSpecifier type = param.getDestinationType();
/* 187 */       if (type != null) {
/* 188 */         colorModel = type.getColorModel();
/*     */       }
/* 190 */       raster = Raster.createWritableRaster(sampleModel
/* 191 */           .createCompatibleSampleModel(image.getMinX() + image
/* 192 */             .getWidth(), image
/* 193 */             .getMinY() + image
/* 194 */             .getHeight()), new Point(0, 0));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 200 */       bi = new BufferedImage(colorModel, raster, (colorModel != null) ? colorModel.isAlphaPremultiplied() : false, new Hashtable<Object, Object>());
/*     */     } else {
/*     */       
/* 203 */       raster = bi.getWritableTile(0, 0);
/*     */     } 
/*     */     
/* 206 */     image.setDestImage(bi);
/*     */     
/* 208 */     image.readAsRaster(raster);
/* 209 */     image.clearDestImage();
/*     */     
/* 211 */     if (abortRequested()) {
/* 212 */       processReadAborted();
/*     */     } else {
/* 214 */       processImageComplete();
/* 215 */     }  return bi;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RenderedImage readAsRenderedImage(int imageIndex, ImageReadParam param) throws IOException {
/* 221 */     if (param == null) {
/* 222 */       param = getDefaultReadParam();
/*     */     }
/* 224 */     checkIndex(imageIndex);
/* 225 */     clearAbortRequest();
/* 226 */     processImageStarted(0);
/*     */     
/* 228 */     RawRenderedImage rawRenderedImage = new RawRenderedImage(this.iis, this, param, imageIndex);
/*     */ 
/*     */     
/* 231 */     if (abortRequested()) {
/* 232 */       processReadAborted();
/*     */     } else {
/* 234 */       processImageComplete();
/* 235 */     }  return (RenderedImage)rawRenderedImage;
/*     */   }
/*     */ 
/*     */   
/*     */   public Raster readRaster(int imageIndex, ImageReadParam param) throws IOException {
/* 240 */     BufferedImage bi = read(imageIndex, param);
/* 241 */     return bi.getData();
/*     */   }
/*     */   
/*     */   public boolean canReadRaster() {
/* 245 */     return true;
/*     */   }
/*     */   
/*     */   public void reset() {
/* 249 */     super.reset();
/* 250 */     this.iis = null;
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
/*     */   public void processImageUpdateWrapper(BufferedImage theImage, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {
/* 262 */     processImageUpdate(theImage, minX, minY, width, height, periodX, periodY, bands);
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
/*     */   public void processImageProgressWrapper(float percentageDone) {
/* 274 */     processImageProgress(percentageDone);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getAbortRequest() {
/* 281 */     return abortRequested();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\raw\RawImageReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */