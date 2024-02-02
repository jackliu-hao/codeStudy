/*     */ package com.github.jaiimageio.impl.plugins.pcx;
/*     */ 
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ComponentColorModel;
/*     */ import java.awt.image.ComponentSampleModel;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.MultiPixelPackedSampleModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import javax.imageio.ImageReadParam;
/*     */ import javax.imageio.ImageReader;
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.stream.ImageInputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PCXImageReader
/*     */   extends ImageReader
/*     */   implements PCXConstants
/*     */ {
/*     */   private ImageInputStream iis;
/*     */   private int width;
/*     */   private int height;
/*     */   private boolean gotHeader = false;
/*     */   private byte manufacturer;
/*     */   private byte encoding;
/*     */   private short xmax;
/*     */   private short ymax;
/*  82 */   private byte[] smallPalette = new byte[48];
/*  83 */   private byte[] largePalette = new byte[768];
/*     */   
/*     */   private byte colorPlanes;
/*     */   
/*     */   private short bytesPerLine;
/*     */   
/*     */   private short paletteType;
/*     */   
/*     */   private PCXMetadata metadata;
/*     */   
/*     */   private SampleModel sampleModel;
/*     */   
/*     */   private SampleModel originalSampleModel;
/*     */   
/*     */   private ColorModel colorModel;
/*     */   
/*     */   private ColorModel originalColorModel;
/*     */   
/*     */   private Rectangle destinationRegion;
/*     */   
/*     */   private Rectangle sourceRegion;
/*     */   
/*     */   private BufferedImage bi;
/*     */   
/*     */   private boolean noTransform = true;
/*     */   
/*     */   private boolean seleBand = false;
/*     */   
/*     */   private int scaleX;
/*     */   private int scaleY;
/*     */   private int[] sourceBands;
/*     */   private int[] destBands;
/*     */   
/*     */   public PCXImageReader(PCXImageReaderSpi imageReaderSpi) {
/* 117 */     super(imageReaderSpi);
/*     */   }
/*     */   
/*     */   public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
/* 121 */     super.setInput(input, seekForwardOnly, ignoreMetadata);
/* 122 */     this.iis = (ImageInputStream)input;
/* 123 */     if (this.iis != null)
/* 124 */       this.iis.setByteOrder(ByteOrder.LITTLE_ENDIAN); 
/* 125 */     this.gotHeader = false;
/*     */   }
/*     */   
/*     */   public int getHeight(int imageIndex) throws IOException {
/* 129 */     checkIndex(imageIndex);
/* 130 */     readHeader();
/* 131 */     return this.height;
/*     */   }
/*     */   
/*     */   public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
/* 135 */     checkIndex(imageIndex);
/* 136 */     readHeader();
/* 137 */     return this.metadata;
/*     */   }
/*     */   
/*     */   public Iterator getImageTypes(int imageIndex) throws IOException {
/* 141 */     checkIndex(imageIndex);
/* 142 */     readHeader();
/* 143 */     return Collections.<ImageTypeSpecifier>singletonList(new ImageTypeSpecifier(this.originalColorModel, this.originalSampleModel)).iterator();
/*     */   }
/*     */   
/*     */   public int getNumImages(boolean allowSearch) throws IOException {
/* 147 */     if (this.iis == null) {
/* 148 */       throw new IllegalStateException("input is null");
/*     */     }
/* 150 */     if (this.seekForwardOnly && allowSearch) {
/* 151 */       throw new IllegalStateException("cannot search with forward only input");
/*     */     }
/* 153 */     return 1;
/*     */   }
/*     */   
/*     */   public IIOMetadata getStreamMetadata() throws IOException {
/* 157 */     return null;
/*     */   }
/*     */   
/*     */   public int getWidth(int imageIndex) throws IOException {
/* 161 */     checkIndex(imageIndex);
/* 162 */     readHeader();
/* 163 */     return this.width;
/*     */   }
/*     */   
/*     */   public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
/* 167 */     checkIndex(imageIndex);
/* 168 */     readHeader();
/*     */     
/* 170 */     if (this.iis == null) {
/* 171 */       throw new IllegalStateException("input is null");
/*     */     }
/*     */ 
/*     */     
/* 175 */     clearAbortRequest();
/* 176 */     processImageStarted(imageIndex);
/*     */     
/* 178 */     if (param == null) {
/* 179 */       param = getDefaultReadParam();
/*     */     }
/* 181 */     this.sourceRegion = new Rectangle(0, 0, 0, 0);
/* 182 */     this.destinationRegion = new Rectangle(0, 0, 0, 0);
/*     */     
/* 184 */     computeRegions(param, this.width, this.height, param.getDestination(), this.sourceRegion, this.destinationRegion);
/*     */     
/* 186 */     this.scaleX = param.getSourceXSubsampling();
/* 187 */     this.scaleY = param.getSourceYSubsampling();
/*     */ 
/*     */     
/* 190 */     this.sourceBands = param.getSourceBands();
/* 191 */     this.destBands = param.getDestinationBands();
/*     */     
/* 193 */     this.seleBand = (this.sourceBands != null && this.destBands != null);
/* 194 */     this.noTransform = (this.destinationRegion.equals(new Rectangle(0, 0, this.width, this.height)) || this.seleBand);
/*     */     
/* 196 */     if (!this.seleBand) {
/* 197 */       this.sourceBands = new int[this.colorPlanes];
/* 198 */       this.destBands = new int[this.colorPlanes];
/* 199 */       for (int i = 0; i < this.colorPlanes; i++) {
/* 200 */         this.sourceBands[i] = i; this.destBands[i] = i;
/*     */       } 
/*     */     } 
/*     */     
/* 204 */     this.bi = param.getDestination();
/*     */ 
/*     */     
/* 207 */     WritableRaster raster = null;
/*     */     
/* 209 */     if (this.bi == null) {
/* 210 */       if (this.sampleModel != null && this.colorModel != null) {
/* 211 */         this.sampleModel = this.sampleModel.createCompatibleSampleModel(this.destinationRegion.width + this.destinationRegion.x, this.destinationRegion.height + this.destinationRegion.y);
/*     */         
/* 213 */         if (this.seleBand)
/* 214 */           this.sampleModel = this.sampleModel.createSubsetSampleModel(this.sourceBands); 
/* 215 */         raster = Raster.createWritableRaster(this.sampleModel, new Point(0, 0));
/* 216 */         this.bi = new BufferedImage(this.colorModel, raster, false, null);
/*     */       } 
/*     */     } else {
/* 219 */       raster = this.bi.getWritableTile(0, 0);
/* 220 */       this.sampleModel = this.bi.getSampleModel();
/* 221 */       this.colorModel = this.bi.getColorModel();
/*     */       
/* 223 */       this.noTransform &= this.destinationRegion.equals(raster.getBounds());
/*     */     } 
/*     */     
/* 226 */     byte[] bdata = null;
/*     */     
/* 228 */     if (this.sampleModel.getDataType() == 0) {
/* 229 */       bdata = ((DataBufferByte)raster.getDataBuffer()).getData();
/*     */     }
/* 231 */     readImage(bdata);
/*     */     
/* 233 */     if (abortRequested()) {
/* 234 */       processReadAborted();
/*     */     } else {
/* 236 */       processImageComplete();
/*     */     } 
/* 238 */     return this.bi;
/*     */   }
/*     */ 
/*     */   
/*     */   private void readImage(byte[] data) throws IOException {
/* 243 */     byte[] scanline = new byte[this.bytesPerLine * this.colorPlanes];
/*     */     
/* 245 */     if (this.noTransform) {
/*     */       try {
/* 247 */         int offset = 0;
/* 248 */         int nbytes = (this.width * this.metadata.bitsPerPixel + 8 - this.metadata.bitsPerPixel) / 8;
/* 249 */         for (int line = 0; line < this.height; line++) {
/* 250 */           readScanLine(scanline);
/* 251 */           for (int band = 0; band < this.colorPlanes; band++) {
/* 252 */             System.arraycopy(scanline, this.bytesPerLine * band, data, offset, nbytes);
/* 253 */             offset += nbytes;
/*     */           } 
/* 255 */           processImageProgress(100.0F * line / this.height);
/*     */         } 
/* 257 */       } catch (EOFException eOFException) {}
/*     */     
/*     */     }
/* 260 */     else if (this.metadata.bitsPerPixel == 1) {
/* 261 */       read1Bit(data);
/* 262 */     } else if (this.metadata.bitsPerPixel == 4) {
/* 263 */       read4Bit(data);
/*     */     } else {
/* 265 */       read8Bit(data);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void read1Bit(byte[] data) throws IOException {
/* 270 */     byte[] scanline = new byte[this.bytesPerLine];
/*     */ 
/*     */     
/*     */     try {
/* 274 */       for (int line = 0; line < this.sourceRegion.y; line++) {
/* 275 */         readScanLine(scanline);
/*     */       }
/*     */       
/* 278 */       int lineStride = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
/*     */ 
/*     */       
/* 281 */       int[] srcOff = new int[this.destinationRegion.width];
/* 282 */       int[] destOff = new int[this.destinationRegion.width];
/* 283 */       int[] srcPos = new int[this.destinationRegion.width];
/* 284 */       int[] destPos = new int[this.destinationRegion.width];
/*     */       int x;
/* 286 */       for (int i = this.destinationRegion.x, j = 0; i < this.destinationRegion.x + this.destinationRegion.width; i++, j++, x += this.scaleX) {
/* 287 */         srcPos[j] = x >> 3;
/* 288 */         srcOff[j] = 7 - (x & 0x7);
/* 289 */         destPos[j] = i >> 3;
/* 290 */         destOff[j] = 7 - (i & 0x7);
/*     */       } 
/*     */       
/* 293 */       int k = this.destinationRegion.y * lineStride;
/*     */       
/* 295 */       for (int m = 0; m < this.sourceRegion.height; m++) {
/* 296 */         readScanLine(scanline);
/* 297 */         if (m % this.scaleY == 0) {
/* 298 */           for (int n = 0; n < this.destinationRegion.width; n++) {
/*     */             
/* 300 */             int v = scanline[srcPos[n]] >> srcOff[n] & 0x1;
/* 301 */             data[k + destPos[n]] = (byte)(data[k + destPos[n]] | v << destOff[n]);
/*     */           } 
/* 303 */           k += lineStride;
/*     */         } 
/* 305 */         processImageProgress(100.0F * m / this.sourceRegion.height);
/*     */       } 
/* 307 */     } catch (EOFException eOFException) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private void read4Bit(byte[] data) throws IOException {
/* 312 */     byte[] scanline = new byte[this.bytesPerLine];
/*     */     
/*     */     try {
/* 315 */       int lineStride = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
/*     */ 
/*     */       
/* 318 */       int[] srcOff = new int[this.destinationRegion.width];
/* 319 */       int[] destOff = new int[this.destinationRegion.width];
/* 320 */       int[] srcPos = new int[this.destinationRegion.width];
/* 321 */       int[] destPos = new int[this.destinationRegion.width];
/*     */       
/* 323 */       int i = this.destinationRegion.x, x = this.sourceRegion.x, j = 0;
/* 324 */       for (; i < this.destinationRegion.x + this.destinationRegion.width; 
/* 325 */         i++, j++, x += this.scaleX) {
/* 326 */         srcPos[j] = x >> 1;
/* 327 */         srcOff[j] = 1 - (x & 0x1) << 2;
/* 328 */         destPos[j] = i >> 1;
/* 329 */         destOff[j] = 1 - (i & 0x1) << 2;
/*     */       } 
/*     */       
/* 332 */       int k = this.destinationRegion.y * lineStride;
/*     */       
/* 334 */       for (int line = 0; line < this.sourceRegion.height; line++) {
/* 335 */         readScanLine(scanline);
/*     */         
/* 337 */         if (abortRequested())
/*     */           break; 
/* 339 */         if (line % this.scaleY == 0) {
/* 340 */           for (int m = 0; m < this.destinationRegion.width; m++) {
/*     */             
/* 342 */             int v = scanline[srcPos[m]] >> srcOff[m] & 0xF;
/* 343 */             data[k + destPos[m]] = (byte)(data[k + destPos[m]] | v << destOff[m]);
/*     */           } 
/* 345 */           k += lineStride;
/*     */         } 
/* 347 */         processImageProgress(100.0F * line / this.sourceRegion.height);
/*     */       } 
/* 349 */     } catch (EOFException eOFException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void read8Bit(byte[] data) throws IOException {
/* 355 */     byte[] scanline = new byte[this.colorPlanes * this.bytesPerLine];
/*     */     
/*     */     try {
/* 358 */       for (int line = 0; line < this.sourceRegion.y; line++) {
/* 359 */         readScanLine(scanline);
/*     */       }
/* 361 */       int dstOffset = this.destinationRegion.y * (this.destinationRegion.x + this.destinationRegion.width) * this.colorPlanes;
/* 362 */       for (int i = 0; i < this.sourceRegion.height; i++) {
/* 363 */         readScanLine(scanline);
/* 364 */         if (i % this.scaleY == 0) {
/* 365 */           int srcOffset = this.sourceRegion.x;
/* 366 */           for (int band = 0; band < this.colorPlanes; band++) {
/* 367 */             dstOffset += this.destinationRegion.x; int x;
/* 368 */             for (x = 0; x < this.destinationRegion.width; x += this.scaleX) {
/* 369 */               data[dstOffset++] = scanline[srcOffset + x];
/*     */             }
/* 371 */             srcOffset += this.bytesPerLine;
/*     */           } 
/*     */         } 
/* 374 */         processImageProgress(100.0F * i / this.sourceRegion.height);
/*     */       } 
/* 376 */     } catch (EOFException eOFException) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private void readScanLine(byte[] buffer) throws IOException {
/* 381 */     int max = this.bytesPerLine * this.colorPlanes;
/* 382 */     for (int j = 0; j < max; ) {
/* 383 */       int val = this.iis.readUnsignedByte();
/*     */       
/* 385 */       if ((val & 0xC0) == 192) {
/* 386 */         int count = val & 0xFFFFFF3F;
/* 387 */         val = this.iis.readUnsignedByte();
/* 388 */         for (int k = 0; k < count && j < max; k++)
/* 389 */           buffer[j++] = (byte)(val & 0xFF); 
/*     */         continue;
/*     */       } 
/* 392 */       buffer[j++] = (byte)(val & 0xFF);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkIndex(int imageIndex) {
/* 398 */     if (imageIndex != 0) {
/* 399 */       throw new IndexOutOfBoundsException("only one image exists in the stream");
/*     */     }
/*     */   }
/*     */   
/*     */   private void readHeader() throws IOException {
/* 404 */     if (this.gotHeader) {
/* 405 */       this.iis.seek(128L);
/*     */       
/*     */       return;
/*     */     } 
/* 409 */     this.metadata = new PCXMetadata();
/*     */     
/* 411 */     this.manufacturer = this.iis.readByte();
/* 412 */     if (this.manufacturer != 10)
/* 413 */       throw new IllegalStateException("image is not a PCX file"); 
/* 414 */     this.metadata.version = (short)this.iis.readByte();
/* 415 */     this.encoding = this.iis.readByte();
/* 416 */     if (this.encoding != 1) {
/* 417 */       throw new IllegalStateException("image is not a PCX file, invalid encoding " + this.encoding);
/*     */     }
/* 419 */     this.metadata.bitsPerPixel = this.iis.readByte();
/*     */     
/* 421 */     this.metadata.xmin = this.iis.readShort();
/* 422 */     this.metadata.ymin = this.iis.readShort();
/* 423 */     this.xmax = this.iis.readShort();
/* 424 */     this.ymax = this.iis.readShort();
/*     */     
/* 426 */     this.metadata.hdpi = this.iis.readShort();
/* 427 */     this.metadata.vdpi = this.iis.readShort();
/*     */     
/* 429 */     this.iis.readFully(this.smallPalette);
/*     */     
/* 431 */     this.iis.readByte();
/*     */     
/* 433 */     this.colorPlanes = this.iis.readByte();
/* 434 */     this.bytesPerLine = this.iis.readShort();
/* 435 */     this.paletteType = this.iis.readShort();
/*     */     
/* 437 */     this.metadata.hsize = this.iis.readShort();
/* 438 */     this.metadata.vsize = this.iis.readShort();
/*     */     
/* 440 */     this.iis.skipBytes(54);
/*     */     
/* 442 */     this.width = this.xmax - this.metadata.xmin + 1;
/* 443 */     this.height = this.ymax - this.metadata.ymin + 1;
/*     */     
/* 445 */     if (this.colorPlanes == 1) {
/* 446 */       if (this.paletteType == 2) {
/* 447 */         ColorSpace cs = ColorSpace.getInstance(1003);
/* 448 */         int[] nBits = { 8 };
/* 449 */         this.colorModel = new ComponentColorModel(cs, nBits, false, false, 1, 0);
/* 450 */         this.sampleModel = new ComponentSampleModel(0, this.width, this.height, 1, this.width, new int[] { 0 });
/*     */       }
/* 452 */       else if (this.metadata.bitsPerPixel == 8) {
/*     */         
/* 454 */         this.iis.mark();
/*     */         
/* 456 */         if (this.iis.length() == -1L) {
/*     */           
/* 458 */           while (this.iis.read() != -1);
/*     */           
/* 460 */           this.iis.seek(this.iis.getStreamPosition() - 768L - 1L);
/*     */         } else {
/* 462 */           this.iis.seek(this.iis.length() - 768L - 1L);
/*     */         } 
/*     */         
/* 465 */         int palletteMagic = this.iis.read();
/* 466 */         if (palletteMagic != 12) {
/* 467 */           processWarningOccurred("Expected palette magic number 12; instead read " + palletteMagic + " from this image.");
/*     */         }
/*     */         
/* 470 */         this.iis.readFully(this.largePalette);
/* 471 */         this.iis.reset();
/*     */         
/* 473 */         this.colorModel = new IndexColorModel(this.metadata.bitsPerPixel, 256, this.largePalette, 0, false);
/* 474 */         this.sampleModel = this.colorModel.createCompatibleSampleModel(this.width, this.height);
/*     */       } else {
/* 476 */         int msize = (this.metadata.bitsPerPixel == 1) ? 2 : 16;
/* 477 */         this.colorModel = new IndexColorModel(this.metadata.bitsPerPixel, msize, this.smallPalette, 0, false);
/* 478 */         this.sampleModel = this.colorModel.createCompatibleSampleModel(this.width, this.height);
/*     */       } 
/*     */     } else {
/*     */       
/* 482 */       ColorSpace cs = ColorSpace.getInstance(1000);
/* 483 */       int[] nBits = { 8, 8, 8 };
/* 484 */       this.colorModel = new ComponentColorModel(cs, nBits, false, false, 1, 0);
/* 485 */       this.sampleModel = new ComponentSampleModel(0, this.width, this.height, 1, this.width * this.colorPlanes, new int[] { 0, this.width, this.width * 2 });
/*     */     } 
/*     */     
/* 488 */     this.originalSampleModel = this.sampleModel;
/* 489 */     this.originalColorModel = this.colorModel;
/*     */     
/* 491 */     this.gotHeader = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\pcx\PCXImageReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */