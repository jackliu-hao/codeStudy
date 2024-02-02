/*     */ package com.github.jaiimageio.impl.plugins.wbmp;
/*     */ 
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.MultiPixelPackedSampleModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.RenderedImage;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.io.IOException;
/*     */ import javax.imageio.IIOImage;
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.ImageWriteParam;
/*     */ import javax.imageio.ImageWriter;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.spi.ImageWriterSpi;
/*     */ import javax.imageio.stream.ImageOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WBMPImageWriter
/*     */   extends ImageWriter
/*     */ {
/*  78 */   private ImageOutputStream stream = null;
/*     */ 
/*     */   
/*     */   private static int getNumBits(int intValue) {
/*  82 */     int numBits = 32;
/*  83 */     int mask = Integer.MIN_VALUE;
/*  84 */     while (mask != 0 && (intValue & mask) == 0) {
/*  85 */       numBits--;
/*  86 */       mask >>>= 1;
/*     */     } 
/*  88 */     return numBits;
/*     */   }
/*     */ 
/*     */   
/*     */   private static byte[] intToMultiByte(int intValue) {
/*  93 */     int numBitsLeft = getNumBits(intValue);
/*  94 */     byte[] multiBytes = new byte[(numBitsLeft + 6) / 7];
/*     */     
/*  96 */     int maxIndex = multiBytes.length - 1;
/*  97 */     for (int b = 0; b <= maxIndex; b++) {
/*  98 */       multiBytes[b] = (byte)(intValue >>> (maxIndex - b) * 7 & 0x7F);
/*  99 */       if (b != maxIndex) {
/* 100 */         multiBytes[b] = (byte)(multiBytes[b] | Byte.MIN_VALUE);
/*     */       }
/*     */     } 
/*     */     
/* 104 */     return multiBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WBMPImageWriter(ImageWriterSpi originator) {
/* 111 */     super(originator);
/*     */   }
/*     */   
/*     */   public void setOutput(Object output) {
/* 115 */     super.setOutput(output);
/* 116 */     if (output != null) {
/* 117 */       if (!(output instanceof ImageOutputStream))
/* 118 */         throw new IllegalArgumentException(I18N.getString("WBMPImageWriter")); 
/* 119 */       this.stream = (ImageOutputStream)output;
/*     */     } else {
/* 121 */       this.stream = null;
/*     */     } 
/*     */   }
/*     */   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
/* 125 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
/* 130 */     WBMPMetadata meta = new WBMPMetadata();
/* 131 */     meta.wbmpType = 0;
/* 132 */     return meta;
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
/* 137 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public IIOMetadata convertImageMetadata(IIOMetadata metadata, ImageTypeSpecifier type, ImageWriteParam param) {
/* 143 */     return null;
/*     */   }
/*     */   
/*     */   public boolean canWriteRasters() {
/* 147 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
/* 153 */     if (this.stream == null) {
/* 154 */       throw new IllegalStateException(I18N.getString("WBMPImageWriter3"));
/*     */     }
/*     */     
/* 157 */     if (image == null) {
/* 158 */       throw new IllegalArgumentException(I18N.getString("WBMPImageWriter4"));
/*     */     }
/*     */     
/* 161 */     clearAbortRequest();
/* 162 */     processImageStarted(0);
/* 163 */     if (param == null) {
/* 164 */       param = getDefaultWriteParam();
/*     */     }
/* 166 */     RenderedImage input = null;
/* 167 */     Raster inputRaster = null;
/* 168 */     boolean writeRaster = image.hasRaster();
/* 169 */     Rectangle sourceRegion = param.getSourceRegion();
/* 170 */     SampleModel sampleModel = null;
/*     */     
/* 172 */     if (writeRaster) {
/* 173 */       inputRaster = image.getRaster();
/* 174 */       sampleModel = inputRaster.getSampleModel();
/*     */     } else {
/* 176 */       input = image.getRenderedImage();
/* 177 */       sampleModel = input.getSampleModel();
/*     */       
/* 179 */       inputRaster = input.getData();
/*     */     } 
/*     */     
/* 182 */     checkSampleModel(sampleModel);
/* 183 */     if (sourceRegion == null) {
/* 184 */       sourceRegion = inputRaster.getBounds();
/*     */     } else {
/* 186 */       sourceRegion = sourceRegion.intersection(inputRaster.getBounds());
/*     */     } 
/* 188 */     if (sourceRegion.isEmpty()) {
/* 189 */       throw new RuntimeException(I18N.getString("WBMPImageWriter1"));
/*     */     }
/* 191 */     int scaleX = param.getSourceXSubsampling();
/* 192 */     int scaleY = param.getSourceYSubsampling();
/* 193 */     int xOffset = param.getSubsamplingXOffset();
/* 194 */     int yOffset = param.getSubsamplingYOffset();
/*     */     
/* 196 */     sourceRegion.translate(xOffset, yOffset);
/* 197 */     sourceRegion.width -= xOffset;
/* 198 */     sourceRegion.height -= yOffset;
/*     */     
/* 200 */     int minX = sourceRegion.x / scaleX;
/* 201 */     int minY = sourceRegion.y / scaleY;
/* 202 */     int w = (sourceRegion.width + scaleX - 1) / scaleX;
/* 203 */     int h = (sourceRegion.height + scaleY - 1) / scaleY;
/*     */     
/* 205 */     Rectangle destinationRegion = new Rectangle(minX, minY, w, h);
/* 206 */     sampleModel = sampleModel.createCompatibleSampleModel(w, h);
/*     */     
/* 208 */     SampleModel destSM = sampleModel;
/*     */ 
/*     */     
/* 211 */     if (sampleModel.getDataType() != 0 || !(sampleModel instanceof MultiPixelPackedSampleModel) || ((MultiPixelPackedSampleModel)sampleModel)
/*     */       
/* 213 */       .getDataBitOffset() != 0) {
/* 214 */       destSM = new MultiPixelPackedSampleModel(0, w, h, 1, w + 7 >> 3, 0);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 220 */     if (!destinationRegion.equals(sourceRegion)) {
/* 221 */       if (scaleX == 1 && scaleY == 1) {
/* 222 */         inputRaster = inputRaster.createChild(inputRaster.getMinX(), inputRaster
/* 223 */             .getMinY(), w, h, minX, minY, null);
/*     */       } else {
/*     */         
/* 226 */         WritableRaster ras = Raster.createWritableRaster(destSM, new Point(minX, minY));
/*     */ 
/*     */         
/* 229 */         byte[] data = ((DataBufferByte)ras.getDataBuffer()).getData();
/*     */         
/* 231 */         int j = minY, y = sourceRegion.y, k = 0;
/* 232 */         for (; j < minY + h; j++, y += scaleY) {
/*     */           
/* 234 */           int i = 0, x = sourceRegion.x;
/* 235 */           for (; i < w; i++, x += scaleX) {
/* 236 */             int v = inputRaster.getSample(x, y, 0);
/* 237 */             data[k + (i >> 3)] = (byte)(data[k + (i >> 3)] | v << 7 - (i & 0x7));
/*     */           } 
/* 239 */           k += w + 7 >> 3;
/*     */         } 
/* 241 */         inputRaster = ras;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 246 */     if (!destSM.equals(inputRaster.getSampleModel())) {
/*     */       
/* 248 */       WritableRaster raster = Raster.createWritableRaster(destSM, new Point(inputRaster
/* 249 */             .getMinX(), inputRaster
/* 250 */             .getMinY()));
/* 251 */       raster.setRect(inputRaster);
/* 252 */       inputRaster = raster;
/*     */     } 
/*     */ 
/*     */     
/* 256 */     boolean isWhiteZero = false;
/* 257 */     if (!writeRaster && input.getColorModel() instanceof IndexColorModel) {
/* 258 */       IndexColorModel icm = (IndexColorModel)input.getColorModel();
/* 259 */       isWhiteZero = (icm.getRed(0) > icm.getRed(1));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 264 */     int lineStride = ((MultiPixelPackedSampleModel)destSM).getScanlineStride();
/* 265 */     int bytesPerRow = (w + 7) / 8;
/* 266 */     byte[] bdata = ((DataBufferByte)inputRaster.getDataBuffer()).getData();
/*     */ 
/*     */     
/* 269 */     this.stream.write(0);
/* 270 */     this.stream.write(0);
/* 271 */     this.stream.write(intToMultiByte(w));
/* 272 */     this.stream.write(intToMultiByte(h));
/*     */ 
/*     */     
/* 275 */     if (!isWhiteZero && lineStride == bytesPerRow) {
/*     */       
/* 277 */       this.stream.write(bdata, 0, h * bytesPerRow);
/* 278 */       processImageProgress(100.0F);
/*     */     } else {
/*     */       
/* 281 */       int offset = 0;
/* 282 */       if (!isWhiteZero) {
/*     */         
/* 284 */         for (int row = 0; row < h && 
/* 285 */           !abortRequested(); row++) {
/*     */           
/* 287 */           this.stream.write(bdata, offset, bytesPerRow);
/* 288 */           offset += lineStride;
/* 289 */           processImageProgress(100.0F * row / h);
/*     */         } 
/*     */       } else {
/*     */         
/* 293 */         byte[] inverted = new byte[bytesPerRow];
/* 294 */         for (int row = 0; row < h && 
/* 295 */           !abortRequested(); row++) {
/*     */           
/* 297 */           for (int col = 0; col < bytesPerRow; col++) {
/* 298 */             inverted[col] = (byte)(bdata[col + offset] ^ 0xFFFFFFFF);
/*     */           }
/* 300 */           this.stream.write(inverted, 0, bytesPerRow);
/* 301 */           offset += lineStride;
/* 302 */           processImageProgress(100.0F * row / h);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 307 */     if (abortRequested()) {
/* 308 */       processWriteAborted();
/*     */     } else {
/* 310 */       processImageComplete();
/* 311 */       this.stream.flushBefore(this.stream.getStreamPosition());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void reset() {
/* 316 */     super.reset();
/* 317 */     this.stream = null;
/*     */   }
/*     */   
/*     */   private void checkSampleModel(SampleModel sm) {
/* 321 */     int type = sm.getDataType();
/* 322 */     if (type < 0 || type > 3 || sm
/* 323 */       .getNumBands() != 1 || sm.getSampleSize(0) != 1)
/* 324 */       throw new IllegalArgumentException(I18N.getString("WBMPImageWriter2")); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\wbmp\WBMPImageWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */