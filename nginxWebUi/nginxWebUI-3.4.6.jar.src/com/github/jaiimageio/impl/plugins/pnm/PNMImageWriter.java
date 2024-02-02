/*     */ package com.github.jaiimageio.impl.plugins.pnm;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.ImageUtil;
/*     */ import com.github.jaiimageio.plugins.pnm.PNMImageWriteParam;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ComponentSampleModel;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.MultiPixelPackedSampleModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.RenderedImage;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import java.util.Iterator;
/*     */ import javax.imageio.IIOException;
/*     */ import javax.imageio.IIOImage;
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.ImageWriteParam;
/*     */ import javax.imageio.ImageWriter;
/*     */ import javax.imageio.metadata.IIOInvalidTreeException;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.spi.ImageWriterSpi;
/*     */ import javax.imageio.stream.ImageOutputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PNMImageWriter
/*     */   extends ImageWriter
/*     */ {
/*     */   private static final int PBM_ASCII = 49;
/*     */   private static final int PGM_ASCII = 50;
/*     */   private static final int PPM_ASCII = 51;
/*     */   private static final int PBM_RAW = 52;
/*     */   private static final int PGM_RAW = 53;
/*     */   private static final int PPM_RAW = 54;
/*     */   private static final int SPACE = 32;
/*     */   private static final String COMMENT = "# written by com.github.jaiimageio.impl.PNMImageWriter";
/*     */   private static byte[] lineSeparator;
/*     */   private int variant;
/*     */   private int maxValue;
/*     */   
/*     */   static {
/* 101 */     if (lineSeparator == null) {
/* 102 */       String ls = AccessController.<String>doPrivileged(new GetPropertyAction("line.separator"));
/*     */       
/* 104 */       lineSeparator = ls.getBytes();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/* 109 */   private ImageOutputStream stream = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PNMImageWriter(ImageWriterSpi originator) {
/* 115 */     super(originator);
/*     */   }
/*     */   
/*     */   public void setOutput(Object output) {
/* 119 */     super.setOutput(output);
/* 120 */     if (output != null) {
/* 121 */       if (!(output instanceof ImageOutputStream))
/* 122 */         throw new IllegalArgumentException(I18N.getString("PNMImageWriter0")); 
/* 123 */       this.stream = (ImageOutputStream)output;
/*     */     } else {
/* 125 */       this.stream = null;
/*     */     } 
/*     */   }
/*     */   public ImageWriteParam getDefaultWriteParam() {
/* 129 */     return (ImageWriteParam)new PNMImageWriteParam();
/*     */   }
/*     */   
/*     */   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
/* 133 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
/* 138 */     return new PNMMetadata(imageType, param);
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
/* 143 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
/* 150 */     if (inData == null) {
/* 151 */       throw new IllegalArgumentException("inData == null!");
/*     */     }
/* 153 */     if (imageType == null) {
/* 154 */       throw new IllegalArgumentException("imageType == null!");
/*     */     }
/*     */     
/* 157 */     PNMMetadata outData = null;
/*     */ 
/*     */     
/* 160 */     if (inData instanceof PNMMetadata) {
/*     */       
/* 162 */       outData = (PNMMetadata)((PNMMetadata)inData).clone();
/*     */     } else {
/*     */       try {
/* 165 */         outData = new PNMMetadata(inData);
/* 166 */       } catch (IIOInvalidTreeException e) {
/*     */         
/* 168 */         outData = new PNMMetadata();
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 173 */     outData.initialize(imageType, param);
/*     */     
/* 175 */     return outData;
/*     */   }
/*     */   
/*     */   public boolean canWriteRasters() {
/* 179 */     return true;
/*     */   }
/*     */   public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
/*     */     ImageTypeSpecifier imageType;
/*     */     PNMMetadata metadata;
/*     */     boolean isRawPNM;
/* 185 */     clearAbortRequest();
/* 186 */     processImageStarted(0);
/* 187 */     if (param == null) {
/* 188 */       param = getDefaultWriteParam();
/*     */     }
/* 190 */     RenderedImage input = null;
/* 191 */     Raster inputRaster = null;
/* 192 */     boolean writeRaster = image.hasRaster();
/* 193 */     Rectangle sourceRegion = param.getSourceRegion();
/* 194 */     SampleModel sampleModel = null;
/* 195 */     ColorModel colorModel = null;
/*     */     
/* 197 */     if (writeRaster)
/* 198 */     { inputRaster = image.getRaster();
/* 199 */       sampleModel = inputRaster.getSampleModel();
/* 200 */       if (sourceRegion == null) {
/* 201 */         sourceRegion = inputRaster.getBounds();
/*     */       } else {
/* 203 */         sourceRegion = sourceRegion.intersection(inputRaster.getBounds());
/*     */       }  }
/* 205 */     else { input = image.getRenderedImage();
/* 206 */       sampleModel = input.getSampleModel();
/* 207 */       colorModel = input.getColorModel();
/*     */       
/* 209 */       Rectangle rect = new Rectangle(input.getMinX(), input.getMinY(), input.getWidth(), input.getHeight());
/* 210 */       if (sourceRegion == null) {
/* 211 */         sourceRegion = rect;
/*     */       } else {
/* 213 */         sourceRegion = sourceRegion.intersection(rect);
/*     */       }  }
/*     */     
/* 216 */     if (sourceRegion.isEmpty()) {
/* 217 */       throw new RuntimeException(I18N.getString("PNMImageWrite1"));
/*     */     }
/* 219 */     ImageUtil.canEncodeImage(this, colorModel, sampleModel);
/*     */     
/* 221 */     int scaleX = param.getSourceXSubsampling();
/* 222 */     int scaleY = param.getSourceYSubsampling();
/* 223 */     int xOffset = param.getSubsamplingXOffset();
/* 224 */     int yOffset = param.getSubsamplingYOffset();
/*     */     
/* 226 */     sourceRegion.translate(xOffset, yOffset);
/* 227 */     sourceRegion.width -= xOffset;
/* 228 */     sourceRegion.height -= yOffset;
/*     */     
/* 230 */     int minX = sourceRegion.x / scaleX;
/* 231 */     int minY = sourceRegion.y / scaleY;
/* 232 */     int w = (sourceRegion.width + scaleX - 1) / scaleX;
/* 233 */     int h = (sourceRegion.height + scaleY - 1) / scaleY;
/*     */     
/* 235 */     Rectangle destinationRegion = new Rectangle(minX, minY, w, h);
/*     */     
/* 237 */     int tileHeight = sampleModel.getHeight();
/* 238 */     int tileWidth = sampleModel.getWidth();
/*     */ 
/*     */     
/* 241 */     int[] sampleSize = sampleModel.getSampleSize();
/* 242 */     int[] sourceBands = param.getSourceBands();
/* 243 */     boolean noSubband = true;
/* 244 */     int numBands = sampleModel.getNumBands();
/*     */     
/* 246 */     if (sourceBands != null) {
/* 247 */       sampleModel = sampleModel.createSubsetSampleModel(sourceBands);
/* 248 */       colorModel = null;
/* 249 */       noSubband = false;
/* 250 */       numBands = sampleModel.getNumBands();
/*     */     } else {
/* 252 */       sourceBands = new int[numBands];
/* 253 */       for (int j = 0; j < numBands; j++) {
/* 254 */         sourceBands[j] = j;
/*     */       }
/*     */     } 
/*     */     
/* 258 */     byte[] reds = null;
/* 259 */     byte[] greens = null;
/* 260 */     byte[] blues = null;
/*     */ 
/*     */     
/* 263 */     boolean isPBMInverted = false;
/*     */     
/* 265 */     if (numBands == 1) {
/* 266 */       if (colorModel instanceof IndexColorModel) {
/* 267 */         IndexColorModel icm = (IndexColorModel)colorModel;
/*     */         
/* 269 */         int mapSize = icm.getMapSize();
/* 270 */         if (mapSize < 1 << sampleSize[0]) {
/* 271 */           throw new RuntimeException(I18N.getString("PNMImageWrite2"));
/*     */         }
/* 273 */         if (sampleSize[0] == 1) {
/* 274 */           this.variant = 52;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 279 */           isPBMInverted = (icm.getRed(1) > icm.getRed(0));
/*     */         } else {
/* 281 */           this.variant = 54;
/*     */           
/* 283 */           reds = new byte[mapSize];
/* 284 */           greens = new byte[mapSize];
/* 285 */           blues = new byte[mapSize];
/*     */           
/* 287 */           icm.getReds(reds);
/* 288 */           icm.getGreens(greens);
/* 289 */           icm.getBlues(blues);
/*     */         } 
/* 291 */       } else if (sampleSize[0] == 1) {
/* 292 */         this.variant = 52;
/* 293 */       } else if (sampleSize[0] <= 8) {
/* 294 */         this.variant = 53;
/*     */       } else {
/* 296 */         this.variant = 50;
/*     */       } 
/* 298 */     } else if (numBands == 3) {
/* 299 */       if (sampleSize[0] <= 8 && sampleSize[1] <= 8 && sampleSize[2] <= 8) {
/*     */         
/* 301 */         this.variant = 54;
/*     */       } else {
/* 303 */         this.variant = 51;
/*     */       } 
/*     */     } else {
/* 306 */       throw new RuntimeException(I18N.getString("PNMImageWrite3"));
/*     */     } 
/*     */     
/* 309 */     IIOMetadata inputMetadata = image.getMetadata();
/*     */     
/* 311 */     if (colorModel != null) {
/* 312 */       imageType = new ImageTypeSpecifier(colorModel, sampleModel);
/*     */     } else {
/* 314 */       ColorSpace cs; int dataType = sampleModel.getDataType();
/* 315 */       switch (numBands) {
/*     */         
/*     */         case 1:
/* 318 */           imageType = ImageTypeSpecifier.createGrayscale(sampleSize[0], dataType, false);
/*     */           break;
/*     */         
/*     */         case 3:
/* 322 */           cs = ColorSpace.getInstance(1000);
/*     */           
/* 324 */           imageType = ImageTypeSpecifier.createInterleaved(cs, new int[] { 0, 1, 2 }, dataType, false, false);
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         default:
/* 330 */           throw new IIOException("Cannot encode image with " + numBands + " bands!");
/*     */       } 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 336 */     if (inputMetadata != null) {
/*     */       
/* 338 */       metadata = (PNMMetadata)convertImageMetadata(inputMetadata, imageType, param);
/*     */     }
/*     */     else {
/*     */       
/* 342 */       metadata = (PNMMetadata)getDefaultImageMetadata(imageType, param);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 347 */     if (param instanceof PNMImageWriteParam) {
/* 348 */       isRawPNM = ((PNMImageWriteParam)param).getRaw();
/*     */     } else {
/* 350 */       isRawPNM = metadata.isRaw();
/*     */     } 
/*     */     
/* 353 */     this.maxValue = metadata.getMaxValue();
/* 354 */     for (int i = 0; i < sampleSize.length; i++) {
/* 355 */       int v = (1 << sampleSize[i]) - 1;
/* 356 */       if (v > this.maxValue) {
/* 357 */         this.maxValue = v;
/*     */       }
/*     */     } 
/*     */     
/* 361 */     if (isRawPNM) {
/*     */       
/* 363 */       int maxBitDepth = metadata.getMaxBitDepth();
/* 364 */       if (!isRaw(this.variant) && maxBitDepth <= 8) {
/*     */ 
/*     */         
/* 367 */         this.variant += 3;
/* 368 */       } else if (isRaw(this.variant) && maxBitDepth > 8) {
/*     */ 
/*     */         
/* 371 */         this.variant -= 3;
/*     */       }
/*     */     
/*     */     }
/* 375 */     else if (isRaw(this.variant)) {
/*     */       
/* 377 */       this.variant -= 3;
/*     */     } 
/*     */ 
/*     */     
/* 381 */     this.stream.writeByte(80);
/* 382 */     this.stream.writeByte(this.variant);
/*     */     
/* 384 */     this.stream.write(lineSeparator);
/* 385 */     this.stream.write("# written by com.github.jaiimageio.impl.PNMImageWriter".getBytes());
/*     */ 
/*     */     
/* 388 */     Iterator<String> comments = metadata.getComments();
/* 389 */     if (comments != null) {
/* 390 */       while (comments.hasNext()) {
/* 391 */         this.stream.write(lineSeparator);
/* 392 */         String comment = "# " + (String)comments.next();
/* 393 */         this.stream.write(comment.getBytes());
/*     */       } 
/*     */     }
/*     */     
/* 397 */     this.stream.write(lineSeparator);
/* 398 */     writeInteger(this.stream, w);
/* 399 */     this.stream.write(32);
/* 400 */     writeInteger(this.stream, h);
/*     */ 
/*     */     
/* 403 */     if (this.variant != 52 && this.variant != 49) {
/* 404 */       this.stream.write(lineSeparator);
/* 405 */       writeInteger(this.stream, this.maxValue);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 410 */     if (this.variant == 52 || this.variant == 53 || this.variant == 54)
/*     */     {
/*     */       
/* 413 */       this.stream.write(10);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 418 */     boolean writeOptimal = false;
/* 419 */     if (this.variant == 52 && sampleModel
/* 420 */       .getTransferType() == 0 && sampleModel instanceof MultiPixelPackedSampleModel) {
/*     */ 
/*     */       
/* 423 */       MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sampleModel;
/*     */ 
/*     */       
/* 426 */       int originX = 0;
/* 427 */       if (writeRaster) {
/* 428 */         originX = inputRaster.getMinX();
/*     */       } else {
/* 430 */         originX = input.getMinX();
/*     */       } 
/*     */       
/* 433 */       if (mppsm.getBitOffset((sourceRegion.x - originX) % tileWidth) == 0 && mppsm
/* 434 */         .getPixelBitStride() == 1 && scaleX == 1)
/* 435 */         writeOptimal = true; 
/* 436 */     } else if ((this.variant == 53 || this.variant == 54) && sampleModel instanceof ComponentSampleModel && !(colorModel instanceof IndexColorModel)) {
/*     */ 
/*     */ 
/*     */       
/* 440 */       ComponentSampleModel csm = (ComponentSampleModel)sampleModel;
/*     */ 
/*     */ 
/*     */       
/* 444 */       if (csm.getPixelStride() == numBands && scaleX == 1) {
/* 445 */         writeOptimal = true;
/*     */ 
/*     */         
/* 448 */         if (this.variant == 54) {
/* 449 */           int[] bandOffsets = csm.getBandOffsets();
/* 450 */           for (int b = 0; b < numBands; b++) {
/* 451 */             if (bandOffsets[b] != b) {
/* 452 */               writeOptimal = false;
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 461 */     if (writeOptimal) {
/*     */       
/* 463 */       int bytesPerRow = (this.variant == 52) ? ((w + 7) / 8) : (w * sampleModel.getNumBands());
/* 464 */       byte[] bdata = null;
/* 465 */       byte[] invertedData = new byte[bytesPerRow];
/*     */ 
/*     */       
/* 468 */       for (int j = 0; j < sourceRegion.height && 
/* 469 */         !abortRequested(); j++) {
/*     */         
/* 471 */         Raster lineRaster = null;
/* 472 */         if (writeRaster) {
/* 473 */           lineRaster = inputRaster.createChild(sourceRegion.x, j, sourceRegion.width, 1, 0, 0, null);
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 479 */           lineRaster = input.getData(new Rectangle(sourceRegion.x, sourceRegion.y + j, w, 1));
/*     */ 
/*     */           
/* 482 */           lineRaster = lineRaster.createTranslatedChild(0, 0);
/*     */         } 
/*     */         
/* 485 */         bdata = ((DataBufferByte)lineRaster.getDataBuffer()).getData();
/*     */         
/* 487 */         sampleModel = lineRaster.getSampleModel();
/* 488 */         int offset = 0;
/* 489 */         if (sampleModel instanceof ComponentSampleModel) {
/*     */           
/* 491 */           offset = ((ComponentSampleModel)sampleModel).getOffset(lineRaster.getMinX() - lineRaster.getSampleModelTranslateX(), lineRaster
/* 492 */               .getMinY() - lineRaster.getSampleModelTranslateY());
/* 493 */         } else if (sampleModel instanceof MultiPixelPackedSampleModel) {
/* 494 */           offset = ((MultiPixelPackedSampleModel)sampleModel).getOffset(lineRaster.getMinX() - lineRaster
/* 495 */               .getSampleModelTranslateX(), lineRaster
/* 496 */               .getMinX() - lineRaster.getSampleModelTranslateY());
/*     */         } 
/*     */         
/* 499 */         if (isPBMInverted) {
/* 500 */           for (int k = offset, m = 0; m < bytesPerRow; k++, m++)
/* 501 */             invertedData[m] = (byte)(bdata[k] ^ 0xFFFFFFFF); 
/* 502 */           bdata = invertedData;
/* 503 */           offset = 0;
/*     */         } 
/*     */         
/* 506 */         this.stream.write(bdata, offset, bytesPerRow);
/* 507 */         processImageProgress(100.0F * j / sourceRegion.height);
/*     */       } 
/*     */ 
/*     */       
/* 511 */       this.stream.flush();
/* 512 */       if (abortRequested()) {
/* 513 */         processWriteAborted();
/*     */       } else {
/* 515 */         processImageComplete();
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 520 */     int size = sourceRegion.width * numBands;
/*     */     
/* 522 */     int[] pixels = new int[size];
/*     */ 
/*     */ 
/*     */     
/* 526 */     byte[] bpixels = (reds == null) ? new byte[w * numBands] : new byte[w * 3];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 532 */     int count = 0;
/*     */ 
/*     */     
/* 535 */     int lastRow = sourceRegion.y + sourceRegion.height;
/*     */     int row;
/* 537 */     for (row = sourceRegion.y; row < lastRow && 
/* 538 */       !abortRequested(); row += scaleY) {
/*     */       int k, kdst, ksrc, b, pos, m, j;
/*     */       
/* 541 */       Raster src = null;
/*     */       
/* 543 */       if (writeRaster) {
/* 544 */         src = inputRaster.createChild(sourceRegion.x, row, sourceRegion.width, 1, sourceRegion.x, row, sourceBands);
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 549 */         src = input.getData(new Rectangle(sourceRegion.x, row, sourceRegion.width, 1));
/*     */       } 
/* 551 */       src.getPixels(sourceRegion.x, row, sourceRegion.width, 1, pixels);
/*     */       
/* 553 */       if (isPBMInverted) {
/* 554 */         int n; for (n = 0; n < size; n += scaleX)
/* 555 */           bpixels[n] = (byte)(bpixels[n] ^ 0x1); 
/*     */       } 
/* 557 */       switch (this.variant) {
/*     */         case 49:
/*     */         case 50:
/* 560 */           for (k = 0; k < size; k += scaleX) {
/* 561 */             if (count++ % 16 == 0) {
/* 562 */               this.stream.write(lineSeparator);
/*     */             } else {
/* 564 */               this.stream.write(32);
/*     */             } 
/* 566 */             writeInteger(this.stream, pixels[k]);
/*     */           } 
/* 568 */           this.stream.write(lineSeparator);
/*     */           break;
/*     */         
/*     */         case 51:
/* 572 */           if (reds == null) {
/* 573 */             for (k = 0; k < size; k += scaleX * numBands) {
/* 574 */               for (int n = 0; n < numBands; n++) {
/* 575 */                 if (count++ % 16 == 0) {
/* 576 */                   this.stream.write(lineSeparator);
/*     */                 } else {
/* 578 */                   this.stream.write(32);
/*     */                 } 
/* 580 */                 writeInteger(this.stream, pixels[k + n]);
/*     */               } 
/*     */             } 
/*     */           } else {
/* 584 */             for (k = 0; k < size; k += scaleX) {
/* 585 */               if (count++ % 5 == 0) {
/* 586 */                 this.stream.write(lineSeparator);
/*     */               } else {
/* 588 */                 this.stream.write(32);
/*     */               } 
/* 590 */               writeInteger(this.stream, reds[pixels[k]] & 0xFF);
/* 591 */               this.stream.write(32);
/* 592 */               writeInteger(this.stream, greens[pixels[k]] & 0xFF);
/* 593 */               this.stream.write(32);
/* 594 */               writeInteger(this.stream, blues[pixels[k]] & 0xFF);
/*     */             } 
/*     */           } 
/* 597 */           this.stream.write(lineSeparator);
/*     */           break;
/*     */ 
/*     */         
/*     */         case 52:
/* 602 */           kdst = 0;
/* 603 */           ksrc = 0;
/* 604 */           b = 0;
/* 605 */           pos = 7;
/* 606 */           for (m = 0; m < size; m += scaleX) {
/* 607 */             b |= pixels[m] << pos;
/* 608 */             pos--;
/* 609 */             if (pos == -1) {
/* 610 */               bpixels[kdst++] = (byte)b;
/* 611 */               b = 0;
/* 612 */               pos = 7;
/*     */             } 
/*     */           } 
/*     */           
/* 616 */           if (pos != 7) {
/* 617 */             bpixels[kdst++] = (byte)b;
/*     */           }
/* 619 */           this.stream.write(bpixels, 0, kdst);
/*     */           break;
/*     */         
/*     */         case 53:
/* 623 */           for (m = 0, j = 0; m < size; m += scaleX) {
/* 624 */             bpixels[j++] = (byte)pixels[m];
/*     */           }
/* 626 */           this.stream.write(bpixels, 0, w);
/*     */           break;
/*     */         
/*     */         case 54:
/* 630 */           if (reds == null) {
/* 631 */             int n; for (m = 0, n = 0; m < size; m += scaleX * numBands) {
/* 632 */               for (int i1 = 0; i1 < numBands; i1++)
/* 633 */                 bpixels[n++] = (byte)(pixels[m + i1] & 0xFF); 
/*     */             } 
/*     */           } else {
/* 636 */             for (m = 0, j = 0; m < size; m += scaleX) {
/* 637 */               bpixels[j++] = reds[pixels[m]];
/* 638 */               bpixels[j++] = greens[pixels[m]];
/* 639 */               bpixels[j++] = blues[pixels[m]];
/*     */             } 
/*     */           } 
/* 642 */           this.stream.write(bpixels, 0, bpixels.length);
/*     */           break;
/*     */       } 
/*     */       
/* 646 */       processImageProgress(100.0F * (row - sourceRegion.y) / sourceRegion.height);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 651 */     this.stream.flush();
/*     */     
/* 653 */     if (abortRequested()) {
/* 654 */       processWriteAborted();
/*     */     } else {
/* 656 */       processImageComplete();
/*     */     } 
/*     */   }
/*     */   public void reset() {
/* 660 */     super.reset();
/* 661 */     this.stream = null;
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeInteger(ImageOutputStream output, int i) throws IOException {
/* 666 */     output.write(Integer.toString(i).getBytes());
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeByte(ImageOutputStream output, byte b) throws IOException {
/* 671 */     output.write(Byte.toString(b).getBytes());
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isRaw(int v) {
/* 676 */     return (v >= 52);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\pnm\PNMImageWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */