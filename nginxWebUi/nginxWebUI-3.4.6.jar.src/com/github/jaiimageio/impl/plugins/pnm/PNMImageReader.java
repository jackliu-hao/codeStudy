/*     */ package com.github.jaiimageio.impl.plugins.pnm;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.ImageUtil;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.DataBuffer;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.DataBufferUShort;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.MultiPixelPackedSampleModel;
/*     */ import java.awt.image.PixelInterleavedSampleModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.imageio.ImageReadParam;
/*     */ import javax.imageio.ImageReader;
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.spi.ImageReaderSpi;
/*     */ import javax.imageio.stream.ImageInputStream;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PNMImageReader
/*     */   extends ImageReader
/*     */ {
/*     */   private static final int PBM_ASCII = 49;
/*     */   private static final int PGM_ASCII = 50;
/*     */   private static final int PPM_ASCII = 51;
/*     */   private static final int PBM_RAW = 52;
/*     */   private static final int PGM_RAW = 53;
/*     */   private static final int PPM_RAW = 54;
/*     */   private static final int LINE_FEED = 10;
/*     */   private static byte[] lineSeparator;
/*     */   private int variant;
/*     */   private int maxValue;
/*     */   
/*     */   static {
/*  92 */     if (lineSeparator == null) {
/*  93 */       String ls = AccessController.<String>doPrivileged(new GetPropertyAction("line.separator"));
/*     */       
/*  95 */       lineSeparator = ls.getBytes();
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
/* 106 */   private ImageInputStream iis = null;
/*     */ 
/*     */   
/*     */   private boolean gotHeader = false;
/*     */ 
/*     */   
/*     */   private long imageDataOffset;
/*     */ 
/*     */   
/*     */   private int width;
/*     */ 
/*     */   
/*     */   private int height;
/*     */ 
/*     */   
/*     */   private String aLine;
/*     */   
/*     */   private StringTokenizer token;
/*     */   
/*     */   private PNMMetadata metadata;
/*     */ 
/*     */   
/*     */   public PNMImageReader(ImageReaderSpi originator) {
/* 129 */     super(originator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
/* 136 */     super.setInput(input, seekForwardOnly, ignoreMetadata);
/* 137 */     this.iis = (ImageInputStream)input;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNumImages(boolean allowSearch) throws IOException {
/* 142 */     return 1;
/*     */   }
/*     */   
/*     */   public int getWidth(int imageIndex) throws IOException {
/* 146 */     checkIndex(imageIndex);
/* 147 */     readHeader();
/* 148 */     return this.width;
/*     */   }
/*     */   
/*     */   public int getHeight(int imageIndex) throws IOException {
/* 152 */     checkIndex(imageIndex);
/* 153 */     readHeader();
/* 154 */     return this.height;
/*     */   }
/*     */   
/*     */   public int getVariant() {
/* 158 */     return this.variant;
/*     */   }
/*     */   
/*     */   public int getMaxValue() {
/* 162 */     return this.maxValue;
/*     */   }
/*     */   
/*     */   private void checkIndex(int imageIndex) {
/* 166 */     if (imageIndex != 0) {
/* 167 */       throw new IndexOutOfBoundsException(I18N.getString("PNMImageReader1"));
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void readHeader() throws IOException {
/* 172 */     if (this.gotHeader) {
/*     */ 
/*     */       
/* 175 */       this.iis.seek(this.imageDataOffset);
/*     */       
/*     */       return;
/*     */     } 
/* 179 */     if (this.iis != null) {
/* 180 */       if (this.iis.readByte() != 80) {
/* 181 */         throw new RuntimeException(I18N.getString("PNMImageReader0"));
/*     */       }
/*     */       
/* 184 */       this.variant = this.iis.readByte();
/* 185 */       if (this.variant < 49 || this.variant > 54) {
/* 186 */         throw new RuntimeException(I18N.getString("PNMImageReader0"));
/*     */       }
/*     */ 
/*     */       
/* 190 */       this.metadata = new PNMMetadata();
/*     */ 
/*     */       
/* 193 */       this.metadata.setVariant(this.variant);
/*     */ 
/*     */       
/* 196 */       this.iis.readLine();
/*     */       
/* 198 */       readComments(this.iis, this.metadata);
/*     */       
/* 200 */       this.width = readInteger(this.iis);
/* 201 */       this.height = readInteger(this.iis);
/*     */       
/* 203 */       if (this.variant == 49 || this.variant == 52) {
/* 204 */         this.maxValue = 1;
/*     */       } else {
/* 206 */         this.maxValue = readInteger(this.iis);
/*     */       } 
/*     */       
/* 209 */       this.metadata.setWidth(this.width);
/* 210 */       this.metadata.setHeight(this.height);
/* 211 */       this.metadata.setMaxBitDepth(this.maxValue);
/*     */       
/* 213 */       this.gotHeader = true;
/*     */ 
/*     */       
/* 216 */       this.imageDataOffset = this.iis.getStreamPosition();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator getImageTypes(int imageIndex) throws IOException {
/* 222 */     checkIndex(imageIndex);
/*     */     
/* 224 */     readHeader();
/* 225 */     int tmp = (this.variant - 49) % 3;
/*     */     
/* 227 */     ArrayList<ImageTypeSpecifier> list = new ArrayList(1);
/* 228 */     int dataType = 3;
/*     */     
/* 230 */     if (this.maxValue < 256) {
/* 231 */       dataType = 0;
/* 232 */     } else if (this.maxValue < 65536) {
/* 233 */       dataType = 1;
/*     */     } 
/*     */ 
/*     */     
/* 237 */     SampleModel sampleModel = null;
/* 238 */     ColorModel colorModel = null;
/* 239 */     if (this.variant == 49 || this.variant == 52) {
/*     */       
/* 241 */       sampleModel = new MultiPixelPackedSampleModel(0, this.width, this.height, 1);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 246 */       byte[] color = { -1, 0 };
/* 247 */       colorModel = new IndexColorModel(1, 2, color, color, color);
/*     */     } else {
/* 249 */       (new int[1])[0] = 0; (new int[3])[0] = 0; (new int[3])[1] = 1; (new int[3])[2] = 2; sampleModel = new PixelInterleavedSampleModel(dataType, this.width, this.height, (tmp == 1) ? 1 : 3, this.width * ((tmp == 1) ? 1 : 3), (tmp == 1) ? new int[1] : new int[3]);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 257 */       colorModel = ImageUtil.createColorModel(null, sampleModel);
/*     */     } 
/*     */     
/* 260 */     list.add(new ImageTypeSpecifier(colorModel, sampleModel));
/*     */     
/* 262 */     return list.iterator();
/*     */   }
/*     */   
/*     */   public ImageReadParam getDefaultReadParam() {
/* 266 */     return new ImageReadParam();
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
/* 271 */     checkIndex(imageIndex);
/* 272 */     readHeader();
/* 273 */     return this.metadata;
/*     */   }
/*     */   
/*     */   public IIOMetadata getStreamMetadata() throws IOException {
/* 277 */     return null;
/*     */   }
/*     */   
/*     */   public boolean isRandomAccessEasy(int imageIndex) throws IOException {
/* 281 */     checkIndex(imageIndex);
/* 282 */     return true; } public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException { DataBuffer dataBuffer; int skipX; byte[] buf; int skipY, originalLS, k, dsx; byte[] data; int m; DataBufferByte bbuf; int destLS, i1; byte[] byteArray; int offset, i; DataBufferUShort sbuf; int i2, n;
/*     */     short[] shortArray;
/*     */     int i3;
/*     */     DataBufferInt ibuf;
/*     */     int j, i4, intArray[], i6, i5, i7;
/* 287 */     checkIndex(imageIndex);
/* 288 */     clearAbortRequest();
/* 289 */     processImageStarted(imageIndex);
/*     */     
/* 291 */     if (param == null) {
/* 292 */       param = getDefaultReadParam();
/*     */     }
/*     */     
/* 295 */     readHeader();
/*     */     
/* 297 */     Rectangle sourceRegion = new Rectangle(0, 0, 0, 0);
/* 298 */     Rectangle destinationRegion = new Rectangle(0, 0, 0, 0);
/*     */     
/* 300 */     computeRegions(param, this.width, this.height, param
/* 301 */         .getDestination(), sourceRegion, destinationRegion);
/*     */ 
/*     */ 
/*     */     
/* 305 */     int scaleX = param.getSourceXSubsampling();
/* 306 */     int scaleY = param.getSourceYSubsampling();
/*     */ 
/*     */     
/* 309 */     int[] sourceBands = param.getSourceBands();
/* 310 */     int[] destBands = param.getDestinationBands();
/*     */     
/* 312 */     boolean seleBand = (sourceBands != null && destBands != null);
/*     */     
/* 314 */     boolean noTransform = (destinationRegion.equals(new Rectangle(0, 0, this.width, this.height)) || seleBand);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 320 */     if (isRaw(this.variant) && this.maxValue >= 256) {
/* 321 */       this.maxValue = 255;
/*     */     }
/*     */     
/* 324 */     int numBands = 1;
/*     */ 
/*     */     
/* 327 */     if (this.variant == 51 || this.variant == 54) {
/* 328 */       numBands = 3;
/*     */     }
/*     */     
/* 331 */     if (!seleBand) {
/* 332 */       sourceBands = new int[numBands];
/* 333 */       destBands = new int[numBands];
/* 334 */       for (int i8 = 0; i8 < numBands; i8++) {
/* 335 */         sourceBands[i8] = i8; destBands[i8] = i8;
/*     */       } 
/*     */     } 
/* 338 */     int dataType = 3;
/*     */     
/* 340 */     if (this.maxValue < 256) {
/* 341 */       dataType = 0;
/* 342 */     } else if (this.maxValue < 65536) {
/* 343 */       dataType = 1;
/*     */     } 
/*     */ 
/*     */     
/* 347 */     SampleModel sampleModel = null;
/* 348 */     ColorModel colorModel = null;
/* 349 */     if (this.variant == 49 || this.variant == 52) {
/*     */       
/* 351 */       sampleModel = new MultiPixelPackedSampleModel(0, destinationRegion.width, destinationRegion.height, 1);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 356 */       byte[] color = { -1, 0 };
/* 357 */       colorModel = new IndexColorModel(1, 2, color, color, color);
/*     */     } else {
/* 359 */       sampleModel = new PixelInterleavedSampleModel(dataType, destinationRegion.width, destinationRegion.height, sourceBands.length, destinationRegion.width * sourceBands.length, destBands);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 368 */       colorModel = ImageUtil.createColorModel(null, sampleModel);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 373 */     BufferedImage bi = param.getDestination();
/*     */ 
/*     */     
/* 376 */     WritableRaster raster = null;
/*     */     
/* 378 */     if (bi == null) {
/* 379 */       sampleModel = sampleModel.createCompatibleSampleModel(destinationRegion.x + destinationRegion.width, destinationRegion.y + destinationRegion.height);
/*     */ 
/*     */       
/* 382 */       if (seleBand) {
/* 383 */         sampleModel = sampleModel.createSubsetSampleModel(sourceBands);
/*     */       }
/* 385 */       raster = Raster.createWritableRaster(sampleModel, new Point());
/* 386 */       bi = new BufferedImage(colorModel, raster, false, null);
/*     */     } else {
/* 388 */       raster = bi.getWritableTile(0, 0);
/* 389 */       sampleModel = bi.getSampleModel();
/* 390 */       colorModel = bi.getColorModel();
/* 391 */       noTransform &= destinationRegion.equals(raster.getBounds());
/*     */     } 
/*     */     
/* 394 */     switch (this.variant) {
/*     */ 
/*     */ 
/*     */       
/*     */       case 52:
/* 399 */         dataBuffer = raster.getDataBuffer();
/*     */ 
/*     */         
/* 402 */         buf = ((DataBufferByte)dataBuffer).getData();
/* 403 */         if (noTransform) {
/* 404 */           this.iis.readFully(buf, 0, buf.length);
/* 405 */           processImageUpdate(bi, 0, 0, this.width, this.height, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */           
/* 409 */           processImageProgress(100.0F); break;
/* 410 */         }  if (scaleX == 1 && sourceRegion.x % 8 == 0) {
/* 411 */           int skip = sourceRegion.x >> 3;
/* 412 */           int i8 = this.width + 7 >> 3;
/* 413 */           int i9 = raster.getWidth() + 7 >> 3;
/*     */           
/* 415 */           int readLength = sourceRegion.width + 7 >> 3;
/* 416 */           int i10 = sourceRegion.y * i8;
/* 417 */           this.iis.skipBytes(i10 + skip);
/* 418 */           i10 = i8 * (scaleY - 1) + i8 - readLength;
/* 419 */           byte[] lineData = new byte[readLength];
/*     */           
/* 421 */           int bitoff = destinationRegion.x & 0x7;
/* 422 */           boolean reformat = (bitoff != 0);
/*     */           
/* 424 */           int i11 = 0, i12 = 0;
/* 425 */           int i13 = destinationRegion.y * i9 + (destinationRegion.x >> 3);
/* 426 */           for (; i11 < destinationRegion.height; i11++, i12 += scaleY) {
/* 427 */             if (reformat) {
/* 428 */               this.iis.read(lineData, 0, readLength);
/* 429 */               int mask1 = 255 << bitoff & 0xFF;
/* 430 */               int mask2 = (mask1 ^ 0xFFFFFFFF) & 0xFF;
/* 431 */               int shift = 8 - bitoff;
/*     */               
/* 433 */               int i14 = 0;
/* 434 */               int i15 = i13;
/* 435 */               for (; i14 < readLength - 1; i14++, i15++) {
/* 436 */                 buf[i15] = (byte)((lineData[i14] & mask2) << shift | (lineData[i14 + 1] & mask1) >> bitoff);
/*     */               }
/* 438 */               buf[i15] = (byte)((lineData[i14] & mask2) << shift);
/*     */             } else {
/* 440 */               this.iis.read(buf, i13, readLength);
/*     */             } 
/*     */             
/* 443 */             this.iis.skipBytes(i10);
/* 444 */             i13 += i9;
/*     */             
/* 446 */             processImageUpdate(bi, 0, i11, destinationRegion.width, 1, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */             
/* 450 */             processImageProgress(100.0F * i11 / destinationRegion.height);
/*     */           }  break;
/*     */         } 
/* 453 */         originalLS = this.width + 7 >> 3;
/* 454 */         data = new byte[originalLS];
/* 455 */         this.iis.skipBytes(sourceRegion.y * originalLS);
/* 456 */         destLS = bi.getWidth() + 7 >> 3;
/* 457 */         offset = originalLS * (scaleY - 1);
/* 458 */         i2 = destLS * destinationRegion.y + (destinationRegion.x >> 3);
/*     */         
/* 460 */         i3 = 0; j = 0; i6 = i2;
/* 461 */         for (; i3 < destinationRegion.height; i3++, j += scaleY) {
/* 462 */           this.iis.read(data, 0, originalLS);
/* 463 */           this.iis.skipBytes(offset);
/*     */           
/* 465 */           int b = 0;
/* 466 */           int pos = 7 - (destinationRegion.x & 0x7);
/* 467 */           int i8 = sourceRegion.x;
/* 468 */           for (; i8 < sourceRegion.x + sourceRegion.width; 
/* 469 */             i8 += scaleX) {
/* 470 */             b |= (data[i8 >> 3] >> 7 - (i8 & 0x7) & 0x1) << pos;
/* 471 */             pos--;
/* 472 */             if (pos == -1) {
/* 473 */               buf[i6++] = (byte)b;
/* 474 */               b = 0;
/* 475 */               pos = 7;
/*     */             } 
/*     */           } 
/*     */           
/* 479 */           if (pos != 7) {
/* 480 */             buf[i6++] = (byte)b;
/*     */           }
/* 482 */           i6 += destinationRegion.x >> 3;
/* 483 */           processImageUpdate(bi, 0, i3, destinationRegion.width, 1, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */           
/* 487 */           processImageProgress(100.0F * i3 / destinationRegion.height);
/*     */         } 
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case 49:
/* 494 */         dataBuffer = raster.getDataBuffer();
/* 495 */         buf = ((DataBufferByte)dataBuffer).getData();
/* 496 */         if (noTransform) {
/* 497 */           for (int i8 = 0, i9 = 0; i8 < this.height; i8++) {
/* 498 */             int b = 0;
/* 499 */             int pos = 7;
/* 500 */             for (int i10 = 0; i10 < this.width; i10++) {
/* 501 */               b |= (readInteger(this.iis) & 0x1) << pos;
/* 502 */               pos--;
/* 503 */               if (pos == -1) {
/* 504 */                 buf[i9++] = (byte)b;
/* 505 */                 b = 0;
/* 506 */                 pos = 7;
/*     */               } 
/*     */             } 
/* 509 */             if (pos != 7)
/* 510 */               buf[i9++] = (byte)b; 
/* 511 */             processImageUpdate(bi, 0, i8, this.width, 1, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */             
/* 515 */             processImageProgress(100.0F * i8 / this.height);
/*     */           }  break;
/*     */         } 
/* 518 */         skipInteger(this.iis, sourceRegion.y * this.width + sourceRegion.x);
/* 519 */         k = scaleX - 1;
/* 520 */         m = (scaleY - 1) * this.width + this.width - destinationRegion.width * scaleX;
/*     */         
/* 522 */         i1 = (bi.getWidth() + 7 >> 3) * destinationRegion.y + (destinationRegion.x >> 3);
/*     */         
/* 524 */         for (i = 0, n = i1; i < destinationRegion.height; i++) {
/* 525 */           int b = 0;
/* 526 */           int pos = 7 - (destinationRegion.x & 0x7);
/* 527 */           for (int i8 = 0; i8 < destinationRegion.width; i8++) {
/* 528 */             b |= (readInteger(this.iis) & 0x1) << pos;
/* 529 */             pos--;
/* 530 */             if (pos == -1) {
/* 531 */               buf[n++] = (byte)b;
/* 532 */               b = 0;
/* 533 */               pos = 7;
/*     */             } 
/* 535 */             skipInteger(this.iis, k);
/*     */           } 
/* 537 */           if (pos != 7) {
/* 538 */             buf[n++] = (byte)b;
/*     */           }
/* 540 */           n += destinationRegion.x >> 3;
/* 541 */           skipInteger(this.iis, m);
/* 542 */           processImageUpdate(bi, 0, i, destinationRegion.width, 1, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */           
/* 546 */           processImageProgress(100.0F * i / destinationRegion.height);
/*     */         } 
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case 50:
/*     */       case 51:
/*     */       case 53:
/*     */       case 54:
/* 556 */         skipX = (scaleX - 1) * numBands;
/* 557 */         skipY = (scaleY * this.width - destinationRegion.width * scaleX) * numBands;
/*     */         
/* 559 */         dsx = (bi.getWidth() * destinationRegion.y + destinationRegion.x) * numBands;
/*     */         
/* 561 */         switch (dataType) {
/*     */           
/*     */           case 0:
/* 564 */             bbuf = (DataBufferByte)raster.getDataBuffer();
/* 565 */             byteArray = bbuf.getData();
/* 566 */             if (isRaw(this.variant)) {
/* 567 */               if (noTransform) {
/* 568 */                 this.iis.readFully(byteArray);
/* 569 */                 processImageUpdate(bi, 0, 0, this.width, this.height, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */                 
/* 573 */                 processImageProgress(100.0F); break;
/*     */               } 
/* 575 */               this.iis.skipBytes(sourceRegion.y * this.width * numBands);
/* 576 */               int skip = (scaleY - 1) * this.width * numBands;
/* 577 */               byte[] arrayOfByte = new byte[this.width * numBands];
/* 578 */               int pixelStride = scaleX * numBands;
/* 579 */               int sx = sourceRegion.x * numBands;
/* 580 */               int ex = this.width;
/* 581 */               for (int i8 = 0, i9 = dsx; i8 < destinationRegion.height; i8++) {
/* 582 */                 this.iis.read(arrayOfByte);
/* 583 */                 int i10 = sourceRegion.x, i11 = sx;
/* 584 */                 for (; i10 < sourceRegion.x + sourceRegion.width; 
/* 585 */                   i10 += scaleX, i11 += pixelStride) {
/* 586 */                   for (int i12 = 0; i12 < sourceBands.length; i12++)
/* 587 */                     byteArray[i9 + destBands[i12]] = arrayOfByte[i11 + sourceBands[i12]]; 
/* 588 */                   i9 += sourceBands.length;
/*     */                 } 
/* 590 */                 i9 += destinationRegion.x * numBands;
/* 591 */                 this.iis.skipBytes(skip);
/* 592 */                 processImageUpdate(bi, 0, i8, destinationRegion.width, 1, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */                 
/* 596 */                 processImageProgress(100.0F * i8 / destinationRegion.height);
/*     */               } 
/*     */               break;
/*     */             } 
/* 600 */             skipInteger(this.iis, (sourceRegion.y * this.width + sourceRegion.x) * numBands);
/*     */ 
/*     */ 
/*     */             
/* 604 */             if (seleBand) {
/* 605 */               byte[] arrayOfByte = new byte[numBands];
/* 606 */               for (int i8 = 0, i9 = dsx; i8 < destinationRegion.height; i8++) {
/* 607 */                 for (j = 0; j < destinationRegion.width; j++) {
/* 608 */                   int i10; for (i10 = 0; i10 < numBands; i10++)
/* 609 */                     arrayOfByte[i10] = (byte)readInteger(this.iis); 
/* 610 */                   for (i10 = 0; i10 < sourceBands.length; i10++)
/* 611 */                     byteArray[i9 + destBands[i10]] = arrayOfByte[sourceBands[i10]]; 
/* 612 */                   i9 += sourceBands.length;
/* 613 */                   skipInteger(this.iis, skipX);
/*     */                 } 
/* 615 */                 i9 += destinationRegion.x * sourceBands.length;
/* 616 */                 skipInteger(this.iis, skipY);
/* 617 */                 processImageUpdate(bi, 0, i8, destinationRegion.width, 1, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */                 
/* 621 */                 processImageProgress(100.0F * i8 / destinationRegion.height);
/*     */               }  break;
/*     */             } 
/* 624 */             for (i = 0, n = dsx; i < destinationRegion.height; i++) {
/* 625 */               for (int i8 = 0; i8 < destinationRegion.width; i8++) {
/* 626 */                 for (int i9 = 0; i9 < numBands; i9++)
/* 627 */                   byteArray[n++] = (byte)readInteger(this.iis); 
/* 628 */                 skipInteger(this.iis, skipX);
/*     */               } 
/* 630 */               n += destinationRegion.x * sourceBands.length;
/* 631 */               skipInteger(this.iis, skipY);
/* 632 */               processImageUpdate(bi, 0, i, destinationRegion.width, 1, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */               
/* 636 */               processImageProgress(100.0F * i / destinationRegion.height);
/*     */             } 
/*     */             break;
/*     */ 
/*     */ 
/*     */           
/*     */           case 1:
/* 643 */             sbuf = (DataBufferUShort)raster.getDataBuffer();
/* 644 */             shortArray = sbuf.getData();
/* 645 */             skipInteger(this.iis, sourceRegion.y * this.width * numBands + sourceRegion.x);
/*     */             
/* 647 */             if (seleBand) {
/* 648 */               short[] arrayOfShort = new short[numBands];
/* 649 */               for (int i8 = 0; i8 < destinationRegion.height; i8++) {
/* 650 */                 for (int i9 = 0; i9 < destinationRegion.width; i9++) {
/* 651 */                   int i10; for (i10 = 0; i10 < numBands; i10++)
/* 652 */                     arrayOfShort[i10] = (short)readInteger(this.iis); 
/* 653 */                   for (i10 = 0; i10 < sourceBands.length; i10++)
/* 654 */                     shortArray[i6 + destBands[i10]] = arrayOfShort[sourceBands[i10]]; 
/* 655 */                   i6 += sourceBands.length;
/* 656 */                   skipInteger(this.iis, skipX);
/*     */                 } 
/* 658 */                 i6 += destinationRegion.x * sourceBands.length;
/* 659 */                 skipInteger(this.iis, skipY);
/* 660 */                 processImageUpdate(bi, 0, i8, destinationRegion.width, 1, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */                 
/* 664 */                 processImageProgress(100.0F * i8 / destinationRegion.height);
/*     */               }  break;
/*     */             } 
/* 667 */             for (i3 = 0, i4 = dsx; i3 < destinationRegion.height; i3++) {
/* 668 */               for (int i8 = 0; i8 < destinationRegion.width; i8++) {
/* 669 */                 for (int i9 = 0; i9 < numBands; i9++)
/* 670 */                   shortArray[i4++] = (short)readInteger(this.iis); 
/* 671 */                 skipInteger(this.iis, skipX);
/*     */               } 
/* 673 */               i4 += destinationRegion.x * sourceBands.length;
/* 674 */               skipInteger(this.iis, skipY);
/* 675 */               processImageUpdate(bi, 0, i3, destinationRegion.width, 1, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */               
/* 679 */               processImageProgress(100.0F * i3 / destinationRegion.height);
/*     */             } 
/*     */             break;
/*     */ 
/*     */           
/*     */           case 3:
/* 685 */             ibuf = (DataBufferInt)raster.getDataBuffer();
/* 686 */             intArray = ibuf.getData();
/* 687 */             skipInteger(this.iis, sourceRegion.y * this.width * numBands + sourceRegion.x);
/* 688 */             if (seleBand) {
/* 689 */               int[] arrayOfInt = new int[numBands];
/* 690 */               for (int i8 = 0, i9 = dsx; i8 < destinationRegion.height; i8++) {
/* 691 */                 for (int i10 = 0; i10 < destinationRegion.width; i10++) {
/* 692 */                   int i11; for (i11 = 0; i11 < numBands; i11++)
/* 693 */                     arrayOfInt[i11] = readInteger(this.iis); 
/* 694 */                   for (i11 = 0; i11 < sourceBands.length; i11++)
/* 695 */                     intArray[i9 + destBands[i11]] = arrayOfInt[sourceBands[i11]]; 
/* 696 */                   i9 += sourceBands.length;
/* 697 */                   skipInteger(this.iis, skipX);
/*     */                 } 
/* 699 */                 i9 += destinationRegion.x * sourceBands.length;
/* 700 */                 skipInteger(this.iis, skipY);
/* 701 */                 processImageUpdate(bi, 0, i8, destinationRegion.width, 1, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */                 
/* 705 */                 processImageProgress(100.0F * i8 / destinationRegion.height);
/*     */               }  break;
/*     */             } 
/* 708 */             for (i5 = 0, i7 = dsx; i5 < destinationRegion.height; i5++) {
/* 709 */               for (int i8 = 0; i8 < destinationRegion.width; i8++) {
/* 710 */                 for (int i9 = 0; i9 < numBands; i9++)
/* 711 */                   intArray[i7++] = readInteger(this.iis); 
/* 712 */                 skipInteger(this.iis, skipX);
/*     */               } 
/* 714 */               i7 += destinationRegion.x * sourceBands.length;
/* 715 */               skipInteger(this.iis, skipY);
/* 716 */               processImageUpdate(bi, 0, i5, destinationRegion.width, 1, 1, 1, destBands);
/*     */ 
/*     */ 
/*     */               
/* 720 */               processImageProgress(100.0F * i5 / destinationRegion.height);
/*     */             } 
/*     */             break;
/*     */         } 
/*     */         
/*     */         break;
/*     */     } 
/* 727 */     if (abortRequested()) {
/* 728 */       processReadAborted();
/*     */     } else {
/* 730 */       processImageComplete();
/* 731 */     }  return bi; }
/*     */ 
/*     */   
/*     */   public boolean canReadRaster() {
/* 735 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Raster readRaster(int imageIndex, ImageReadParam param) throws IOException {
/* 740 */     BufferedImage bi = read(imageIndex, param);
/* 741 */     return bi.getData();
/*     */   }
/*     */   
/*     */   public void reset() {
/* 745 */     super.reset();
/* 746 */     this.iis = null;
/* 747 */     this.gotHeader = false;
/* 748 */     System.gc();
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isRaw(int v) {
/* 753 */     return (v >= 52);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void readComments(ImageInputStream stream, PNMMetadata metadata) throws IOException {
/* 759 */     String line = null;
/* 760 */     int pos = -1;
/* 761 */     stream.mark();
/* 762 */     while ((line = stream.readLine()) != null && (
/* 763 */       pos = line.indexOf("#")) >= 0) {
/* 764 */       metadata.addComment(line.substring(pos + 1).trim());
/*     */     }
/* 766 */     stream.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   private int readInteger(ImageInputStream stream) throws IOException {
/* 771 */     boolean foundDigit = false;
/*     */     
/* 773 */     while (this.aLine == null) {
/* 774 */       this.aLine = stream.readLine();
/* 775 */       if (this.aLine == null)
/* 776 */         return 0; 
/* 777 */       int pos = this.aLine.indexOf("#");
/* 778 */       if (pos == 0) {
/* 779 */         this.aLine = null;
/* 780 */       } else if (pos > 0) {
/* 781 */         this.aLine = this.aLine.substring(0, pos - 1);
/*     */       } 
/* 783 */       if (this.aLine != null) {
/* 784 */         this.token = new StringTokenizer(this.aLine);
/*     */       }
/*     */     } 
/* 787 */     while (this.token.hasMoreTokens()) {
/* 788 */       String s = this.token.nextToken();
/*     */       
/*     */       try {
/* 791 */         return (new Integer(s)).intValue();
/* 792 */       } catch (NumberFormatException e) {}
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 797 */     if (!foundDigit) {
/* 798 */       this.aLine = null;
/* 799 */       return readInteger(stream);
/*     */     } 
/*     */     
/* 802 */     return 0;
/*     */   }
/*     */   
/*     */   private void skipInteger(ImageInputStream stream, int num) throws IOException {
/* 806 */     for (int i = 0; i < num; i++)
/* 807 */       readInteger(stream); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\pnm\PNMImageReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */