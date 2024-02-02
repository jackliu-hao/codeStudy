/*     */ package com.github.jaiimageio.impl.plugins.pcx;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.ImageUtil;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.RenderedImage;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteOrder;
/*     */ import javax.imageio.IIOImage;
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.ImageWriteParam;
/*     */ import javax.imageio.ImageWriter;
/*     */ import javax.imageio.metadata.IIOMetadata;
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
/*     */ public class PCXImageWriter
/*     */   extends ImageWriter
/*     */   implements PCXConstants
/*     */ {
/*     */   private ImageOutputStream ios;
/*     */   private Rectangle sourceRegion;
/*     */   private Rectangle destinationRegion;
/*     */   private int colorPlanes;
/*     */   private int bytesPerLine;
/*  72 */   private Raster inputRaster = null;
/*     */   private int scaleX;
/*     */   
/*     */   public PCXImageWriter(PCXImageWriterSpi imageWriterSpi) {
/*  76 */     super(imageWriterSpi);
/*     */   }
/*     */   private int scaleY;
/*     */   public void setOutput(Object output) {
/*  80 */     super.setOutput(output);
/*  81 */     if (output != null) {
/*  82 */       if (!(output instanceof ImageOutputStream))
/*  83 */         throw new IllegalArgumentException("output not instance of ImageOutputStream"); 
/*  84 */       this.ios = (ImageOutputStream)output;
/*  85 */       this.ios.setByteOrder(ByteOrder.LITTLE_ENDIAN);
/*     */     } else {
/*  87 */       this.ios = null;
/*     */     } 
/*     */   }
/*     */   public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
/*  91 */     if (inData instanceof PCXMetadata)
/*  92 */       return inData; 
/*  93 */     return null;
/*     */   }
/*     */   
/*     */   public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
/*  97 */     return null;
/*     */   }
/*     */   
/*     */   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
/* 101 */     PCXMetadata md = new PCXMetadata();
/* 102 */     md.bitsPerPixel = (byte)imageType.getSampleModel().getSampleSize()[0];
/* 103 */     return md;
/*     */   }
/*     */   
/*     */   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
/* 107 */     return null;
/*     */   }
/*     */   
/*     */   public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
/* 111 */     if (this.ios == null) {
/* 112 */       throw new IllegalStateException("output stream is null");
/*     */     }
/*     */     
/* 115 */     if (image == null) {
/* 116 */       throw new IllegalArgumentException("image is null");
/*     */     }
/*     */     
/* 119 */     clearAbortRequest();
/* 120 */     processImageStarted(0);
/* 121 */     if (param == null) {
/* 122 */       param = getDefaultWriteParam();
/*     */     }
/* 124 */     boolean writeRaster = image.hasRaster();
/*     */     
/* 126 */     this.sourceRegion = param.getSourceRegion();
/*     */     
/* 128 */     SampleModel sampleModel = null;
/* 129 */     ColorModel colorModel = null;
/*     */     
/* 131 */     if (writeRaster)
/* 132 */     { this.inputRaster = image.getRaster();
/* 133 */       sampleModel = this.inputRaster.getSampleModel();
/* 134 */       colorModel = ImageUtil.createColorModel(null, sampleModel);
/* 135 */       if (this.sourceRegion == null) {
/* 136 */         this.sourceRegion = this.inputRaster.getBounds();
/*     */       } else {
/* 138 */         this.sourceRegion = this.sourceRegion.intersection(this.inputRaster.getBounds());
/*     */       }  }
/* 140 */     else { RenderedImage input = image.getRenderedImage();
/* 141 */       this.inputRaster = input.getData();
/* 142 */       sampleModel = input.getSampleModel();
/* 143 */       colorModel = input.getColorModel();
/*     */       
/* 145 */       Rectangle rect = new Rectangle(input.getMinX(), input.getMinY(), input.getWidth(), input.getHeight());
/* 146 */       if (this.sourceRegion == null) {
/* 147 */         this.sourceRegion = rect;
/*     */       } else {
/* 149 */         this.sourceRegion = this.sourceRegion.intersection(rect);
/*     */       }  }
/*     */     
/* 152 */     if (this.sourceRegion.isEmpty()) {
/* 153 */       throw new IllegalArgumentException("source region is empty");
/*     */     }
/* 155 */     IIOMetadata imageMetadata = image.getMetadata();
/* 156 */     PCXMetadata pcxImageMetadata = null;
/*     */     
/* 158 */     ImageTypeSpecifier imageType = new ImageTypeSpecifier(colorModel, sampleModel);
/* 159 */     if (imageMetadata != null) {
/*     */       
/* 161 */       pcxImageMetadata = (PCXMetadata)convertImageMetadata(imageMetadata, imageType, param);
/*     */     } else {
/*     */       
/* 164 */       pcxImageMetadata = (PCXMetadata)getDefaultImageMetadata(imageType, param);
/*     */     } 
/*     */     
/* 167 */     this.scaleX = param.getSourceXSubsampling();
/* 168 */     this.scaleY = param.getSourceYSubsampling();
/*     */     
/* 170 */     int xOffset = param.getSubsamplingXOffset();
/* 171 */     int yOffset = param.getSubsamplingYOffset();
/*     */ 
/*     */     
/* 174 */     int dataType = sampleModel.getDataType();
/*     */     
/* 176 */     this.sourceRegion.translate(xOffset, yOffset);
/* 177 */     this.sourceRegion.width -= xOffset;
/* 178 */     this.sourceRegion.height -= yOffset;
/*     */     
/* 180 */     int minX = this.sourceRegion.x / this.scaleX;
/* 181 */     int minY = this.sourceRegion.y / this.scaleY;
/* 182 */     int w = (this.sourceRegion.width + this.scaleX - 1) / this.scaleX;
/* 183 */     int h = (this.sourceRegion.height + this.scaleY - 1) / this.scaleY;
/*     */     
/* 185 */     xOffset = this.sourceRegion.x % this.scaleX;
/* 186 */     yOffset = this.sourceRegion.y % this.scaleY;
/*     */     
/* 188 */     this.destinationRegion = new Rectangle(minX, minY, w, h);
/*     */     
/* 190 */     boolean noTransform = this.destinationRegion.equals(this.sourceRegion);
/*     */ 
/*     */     
/* 193 */     int[] sourceBands = param.getSourceBands();
/* 194 */     boolean noSubband = true;
/* 195 */     int numBands = sampleModel.getNumBands();
/*     */     
/* 197 */     if (sourceBands != null) {
/* 198 */       sampleModel = sampleModel.createSubsetSampleModel(sourceBands);
/* 199 */       colorModel = null;
/* 200 */       noSubband = false;
/* 201 */       numBands = sampleModel.getNumBands();
/*     */     } else {
/* 203 */       sourceBands = new int[numBands];
/* 204 */       for (int j = 0; j < numBands; j++) {
/* 205 */         sourceBands[j] = j;
/*     */       }
/*     */     } 
/* 208 */     this.ios.writeByte(10);
/* 209 */     this.ios.writeByte(5);
/* 210 */     this.ios.writeByte(1);
/*     */     
/* 212 */     int bitsPerPixel = sampleModel.getSampleSize(0);
/* 213 */     this.ios.writeByte(bitsPerPixel);
/*     */     
/* 215 */     this.ios.writeShort(this.destinationRegion.x);
/* 216 */     this.ios.writeShort(this.destinationRegion.y);
/* 217 */     this.ios.writeShort(this.destinationRegion.x + this.destinationRegion.width - 1);
/* 218 */     this.ios.writeShort(this.destinationRegion.y + this.destinationRegion.height - 1);
/*     */     
/* 220 */     this.ios.writeShort(pcxImageMetadata.hdpi);
/* 221 */     this.ios.writeShort(pcxImageMetadata.vdpi);
/*     */     
/* 223 */     byte[] smallpalette = createSmallPalette(colorModel);
/* 224 */     this.ios.write(smallpalette);
/* 225 */     this.ios.writeByte(0);
/*     */     
/* 227 */     this.colorPlanes = sampleModel.getNumBands();
/*     */     
/* 229 */     this.ios.writeByte(this.colorPlanes);
/*     */     
/* 231 */     this.bytesPerLine = this.destinationRegion.width * bitsPerPixel / 8;
/* 232 */     this.bytesPerLine += this.bytesPerLine % 2;
/*     */     
/* 234 */     this.ios.writeShort(this.bytesPerLine);
/*     */     
/* 236 */     if (colorModel.getColorSpace().getType() == 6) {
/* 237 */       this.ios.writeShort(2);
/*     */     } else {
/* 239 */       this.ios.writeShort(1);
/*     */     } 
/* 241 */     this.ios.writeShort(pcxImageMetadata.hsize);
/* 242 */     this.ios.writeShort(pcxImageMetadata.vsize);
/*     */     
/* 244 */     for (int i = 0; i < 54; i++) {
/* 245 */       this.ios.writeByte(0);
/*     */     }
/*     */ 
/*     */     
/* 249 */     if (this.colorPlanes == 1 && bitsPerPixel == 1) {
/* 250 */       write1Bit();
/*     */     }
/* 252 */     else if (this.colorPlanes == 1 && bitsPerPixel == 4) {
/* 253 */       write4Bit();
/*     */     } else {
/*     */       
/* 256 */       write8Bit();
/*     */     } 
/*     */ 
/*     */     
/* 260 */     if (this.colorPlanes == 1 && bitsPerPixel == 8 && colorModel
/* 261 */       .getColorSpace().getType() != 6) {
/* 262 */       this.ios.writeByte(12);
/* 263 */       this.ios.write(createLargePalette(colorModel));
/*     */     } 
/*     */     
/* 266 */     if (abortRequested()) {
/* 267 */       processWriteAborted();
/*     */     } else {
/* 269 */       processImageComplete();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void write4Bit() throws IOException {
/* 274 */     int[] unpacked = new int[this.sourceRegion.width];
/* 275 */     int[] samples = new int[this.bytesPerLine];
/*     */     
/* 277 */     for (int line = 0; line < this.sourceRegion.height; line += this.scaleY) {
/* 278 */       this.inputRaster.getSamples(this.sourceRegion.x, line + this.sourceRegion.y, this.sourceRegion.width, 1, 0, unpacked);
/*     */       
/* 280 */       int val = 0, dst = 0; int x, nibble;
/* 281 */       for (x = 0, nibble = 0; x < this.sourceRegion.width; x += this.scaleX) {
/* 282 */         val |= unpacked[x] & 0xF;
/* 283 */         if (nibble == 1) {
/* 284 */           samples[dst++] = val;
/* 285 */           nibble = 0;
/* 286 */           val = 0;
/*     */         } else {
/* 288 */           nibble = 1;
/* 289 */           val <<= 4;
/*     */         } 
/*     */       } 
/*     */       
/* 293 */       int last = samples[0];
/* 294 */       int count = 0;
/*     */       int i;
/* 296 */       for (i = 0; i < this.bytesPerLine; i += this.scaleX) {
/* 297 */         int sample = samples[i];
/* 298 */         if (sample != last || count == 63) {
/* 299 */           writeRLE(last, count);
/* 300 */           count = 1;
/* 301 */           last = sample;
/*     */         } else {
/* 303 */           count++;
/*     */         } 
/* 305 */       }  if (count >= 1) {
/* 306 */         writeRLE(last, count);
/*     */       }
/*     */       
/* 309 */       processImageProgress(100.0F * line / this.sourceRegion.height);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void write1Bit() throws IOException {
/* 314 */     int[] unpacked = new int[this.sourceRegion.width];
/* 315 */     int[] samples = new int[this.bytesPerLine];
/*     */     
/* 317 */     for (int line = 0; line < this.sourceRegion.height; line += this.scaleY) {
/* 318 */       this.inputRaster.getSamples(this.sourceRegion.x, line + this.sourceRegion.y, this.sourceRegion.width, 1, 0, unpacked);
/*     */       
/* 320 */       int val = 0, dst = 0; int x, bit;
/* 321 */       for (x = 0, bit = 128; x < this.sourceRegion.width; x += this.scaleX) {
/* 322 */         if (unpacked[x] > 0)
/* 323 */           val |= bit; 
/* 324 */         if (bit == 1) {
/* 325 */           samples[dst++] = val;
/* 326 */           bit = 128;
/* 327 */           val = 0;
/*     */         } else {
/* 329 */           bit >>= 1;
/*     */         } 
/*     */       } 
/*     */       
/* 333 */       int last = samples[0];
/* 334 */       int count = 0;
/*     */       int i;
/* 336 */       for (i = 0; i < this.bytesPerLine; i += this.scaleX) {
/* 337 */         int sample = samples[i];
/* 338 */         if (sample != last || count == 63) {
/* 339 */           writeRLE(last, count);
/* 340 */           count = 1;
/* 341 */           last = sample;
/*     */         } else {
/* 343 */           count++;
/*     */         } 
/* 345 */       }  if (count >= 1) {
/* 346 */         writeRLE(last, count);
/*     */       }
/*     */       
/* 349 */       processImageProgress(100.0F * line / this.sourceRegion.height);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void write8Bit() throws IOException {
/* 354 */     int[][] samples = new int[this.colorPlanes][this.bytesPerLine];
/*     */     
/* 356 */     for (int line = 0; line < this.sourceRegion.height; line += this.scaleY) {
/* 357 */       for (int band = 0; band < this.colorPlanes; band++) {
/* 358 */         this.inputRaster.getSamples(this.sourceRegion.x, line + this.sourceRegion.y, this.sourceRegion.width, 1, band, samples[band]);
/*     */       }
/*     */       
/* 361 */       int last = samples[0][0];
/* 362 */       int count = 0;
/*     */       
/* 364 */       for (int i = 0; i < this.colorPlanes; i++) {
/* 365 */         int x; for (x = 0; x < this.bytesPerLine; x += this.scaleX) {
/* 366 */           int sample = samples[i][x];
/* 367 */           if (sample != last || count == 63) {
/* 368 */             writeRLE(last, count);
/* 369 */             count = 1;
/* 370 */             last = sample;
/*     */           } else {
/* 372 */             count++;
/*     */           } 
/*     */         } 
/* 375 */       }  if (count >= 1) {
/* 376 */         writeRLE(last, count);
/*     */       }
/*     */       
/* 379 */       processImageProgress(100.0F * line / this.sourceRegion.height);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeRLE(int val, int count) throws IOException {
/* 384 */     if (count == 1 && (val & 0xC0) != 192) {
/* 385 */       this.ios.writeByte(val);
/*     */     } else {
/* 387 */       this.ios.writeByte(0xC0 | count);
/* 388 */       this.ios.writeByte(val);
/*     */     } 
/*     */   }
/*     */   
/*     */   private byte[] createSmallPalette(ColorModel cm) {
/* 393 */     byte[] palette = new byte[48];
/*     */     
/* 395 */     if (!(cm instanceof IndexColorModel)) {
/* 396 */       return palette;
/*     */     }
/* 398 */     IndexColorModel icm = (IndexColorModel)cm;
/* 399 */     if (icm.getMapSize() > 16) {
/* 400 */       return palette;
/*     */     }
/* 402 */     for (int i = 0, offset = 0; i < icm.getMapSize(); i++) {
/* 403 */       palette[offset++] = (byte)icm.getRed(i);
/* 404 */       palette[offset++] = (byte)icm.getGreen(i);
/* 405 */       palette[offset++] = (byte)icm.getBlue(i);
/*     */     } 
/*     */     
/* 408 */     return palette;
/*     */   }
/*     */   private byte[] createLargePalette(ColorModel cm) {
/* 411 */     byte[] palette = new byte[768];
/*     */     
/* 413 */     if (!(cm instanceof IndexColorModel)) {
/* 414 */       return palette;
/*     */     }
/* 416 */     IndexColorModel icm = (IndexColorModel)cm;
/*     */     
/* 418 */     for (int i = 0, offset = 0; i < icm.getMapSize(); i++) {
/* 419 */       palette[offset++] = (byte)icm.getRed(i);
/* 420 */       palette[offset++] = (byte)icm.getGreen(i);
/* 421 */       palette[offset++] = (byte)icm.getBlue(i);
/*     */     } 
/*     */     
/* 424 */     return palette;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\pcx\PCXImageWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */