/*     */ package com.github.jaiimageio.impl.plugins.raw;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.ImageUtil;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ComponentSampleModel;
/*     */ import java.awt.image.DataBuffer;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.DataBufferDouble;
/*     */ import java.awt.image.DataBufferFloat;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.DataBufferShort;
/*     */ import java.awt.image.DataBufferUShort;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RawImageWriter
/*     */   extends ImageWriter
/*     */ {
/* 118 */   private ImageOutputStream stream = null;
/*     */   
/*     */   private int imageIndex;
/*     */   
/*     */   private int tileWidth;
/*     */   
/*     */   private int tileHeight;
/*     */   
/*     */   private int tileXOffset;
/*     */   
/*     */   private int tileYOffset;
/*     */   
/*     */   private int scaleX;
/*     */   
/*     */   private int scaleY;
/*     */   
/*     */   private int xOffset;
/*     */   private int yOffset;
/* 136 */   private int[] sourceBands = null;
/*     */ 
/*     */   
/*     */   private int numBands;
/*     */ 
/*     */   
/*     */   private RenderedImage input;
/*     */ 
/*     */   
/*     */   private Raster inputRaster;
/*     */   
/* 147 */   private Rectangle destinationRegion = null;
/*     */ 
/*     */   
/*     */   private SampleModel sampleModel;
/*     */ 
/*     */   
/*     */   private boolean noTransform = true;
/*     */ 
/*     */   
/*     */   private boolean noSubband = true;
/*     */   
/*     */   private boolean writeRaster = false;
/*     */   
/*     */   private boolean optimal = false;
/*     */   
/*     */   private int pxlStride;
/*     */   
/*     */   private int lineStride;
/*     */   
/*     */   private int bandStride;
/*     */ 
/*     */   
/*     */   public RawImageWriter(ImageWriterSpi originator) {
/* 170 */     super(originator);
/*     */   }
/*     */   
/*     */   public void setOutput(Object output) {
/* 174 */     super.setOutput(output);
/* 175 */     if (output != null) {
/* 176 */       if (!(output instanceof ImageOutputStream))
/* 177 */         throw new IllegalArgumentException(I18N.getString("RawImageWriter0")); 
/* 178 */       this.stream = (ImageOutputStream)output;
/*     */     } else {
/* 180 */       this.stream = null;
/*     */     } 
/*     */   }
/*     */   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
/* 189 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
/* 194 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public IIOMetadata convertImageMetadata(IIOMetadata metadata, ImageTypeSpecifier type, ImageWriteParam param) {
/* 200 */     return null;
/*     */   }
/*     */   
/*     */   public boolean canWriteRasters() {
/* 204 */     return true;
/*     */   }
/*     */   
/*     */   public ImageWriteParam getDefaultWriteParam() {
/* 208 */     return new RawImageWriteParam(getLocale());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
/* 214 */     clearAbortRequest();
/* 215 */     processImageStarted(this.imageIndex++);
/*     */     
/* 217 */     if (param == null) {
/* 218 */       param = getDefaultWriteParam();
/*     */     }
/* 220 */     this.writeRaster = image.hasRaster();
/* 221 */     Rectangle sourceRegion = param.getSourceRegion();
/* 222 */     ColorModel colorModel = null;
/* 223 */     Rectangle originalRegion = null;
/*     */     
/* 225 */     if (this.writeRaster) {
/* 226 */       this.inputRaster = image.getRaster();
/* 227 */       this.sampleModel = this.inputRaster.getSampleModel();
/* 228 */       originalRegion = this.inputRaster.getBounds();
/*     */     } else {
/* 230 */       this.input = image.getRenderedImage();
/* 231 */       this.sampleModel = this.input.getSampleModel();
/*     */ 
/*     */       
/* 234 */       originalRegion = new Rectangle(this.input.getMinX(), this.input.getMinY(), this.input.getWidth(), this.input.getHeight());
/*     */       
/* 236 */       colorModel = this.input.getColorModel();
/*     */     } 
/*     */     
/* 239 */     if (sourceRegion == null) {
/* 240 */       sourceRegion = (Rectangle)originalRegion.clone();
/*     */     } else {
/* 242 */       sourceRegion = sourceRegion.intersection(originalRegion);
/*     */     } 
/* 244 */     if (sourceRegion.isEmpty()) {
/* 245 */       throw new RuntimeException(I18N.getString("RawImageWriter1"));
/*     */     }
/* 247 */     this.scaleX = param.getSourceXSubsampling();
/* 248 */     this.scaleY = param.getSourceYSubsampling();
/* 249 */     this.xOffset = param.getSubsamplingXOffset();
/* 250 */     this.yOffset = param.getSubsamplingYOffset();
/*     */     
/* 252 */     sourceRegion.translate(this.xOffset, this.yOffset);
/* 253 */     sourceRegion.width -= this.xOffset;
/* 254 */     sourceRegion.height -= this.yOffset;
/*     */     
/* 256 */     this.xOffset = sourceRegion.x % this.scaleX;
/* 257 */     this.yOffset = sourceRegion.y % this.scaleY;
/*     */     
/* 259 */     int minX = sourceRegion.x / this.scaleX;
/* 260 */     int minY = sourceRegion.y / this.scaleY;
/* 261 */     int w = (sourceRegion.width + this.scaleX - 1) / this.scaleX;
/* 262 */     int h = (sourceRegion.height + this.scaleY - 1) / this.scaleY;
/*     */     
/* 264 */     this.destinationRegion = new Rectangle(minX, minY, w, h);
/* 265 */     this.noTransform = this.destinationRegion.equals(originalRegion);
/*     */     
/* 267 */     this.tileHeight = this.sampleModel.getHeight();
/* 268 */     this.tileWidth = this.sampleModel.getWidth();
/* 269 */     if (this.noTransform) {
/* 270 */       if (this.writeRaster) {
/* 271 */         this.tileXOffset = this.inputRaster.getMinX();
/* 272 */         this.tileYOffset = this.inputRaster.getMinY();
/*     */       } else {
/* 274 */         this.tileXOffset = this.input.getTileGridXOffset();
/* 275 */         this.tileYOffset = this.input.getTileGridYOffset();
/*     */       } 
/*     */     } else {
/* 278 */       this.tileXOffset = this.destinationRegion.x;
/* 279 */       this.tileYOffset = this.destinationRegion.y;
/*     */     } 
/*     */     
/* 282 */     this.sourceBands = param.getSourceBands();
/* 283 */     boolean noSubband = true;
/* 284 */     this.numBands = this.sampleModel.getNumBands();
/*     */     
/* 286 */     if (this.sourceBands != null) {
/* 287 */       this.sampleModel = this.sampleModel.createSubsetSampleModel(this.sourceBands);
/* 288 */       colorModel = null;
/* 289 */       noSubband = false;
/* 290 */       this.numBands = this.sampleModel.getNumBands();
/*     */     } else {
/* 292 */       this.sourceBands = new int[this.numBands];
/* 293 */       for (int i = 0; i < this.numBands; i++) {
/* 294 */         this.sourceBands[i] = i;
/*     */       }
/*     */     } 
/* 297 */     if (this.sampleModel instanceof ComponentSampleModel) {
/* 298 */       ComponentSampleModel csm = (ComponentSampleModel)this.sampleModel;
/* 299 */       int[] bandOffsets = csm.getBandOffsets();
/*     */       
/* 301 */       this.bandStride = bandOffsets[0];
/*     */       
/* 303 */       for (int i = 1; i < bandOffsets.length; i++) {
/* 304 */         if (this.bandStride > bandOffsets[i])
/* 305 */           this.bandStride = bandOffsets[i]; 
/*     */       } 
/* 307 */       int[] bankIndices = csm.getBankIndices();
/* 308 */       int numBank = bankIndices[0];
/* 309 */       for (int j = 1; j < bankIndices.length; j++) {
/* 310 */         if (numBank > bankIndices[j])
/* 311 */           numBank = bankIndices[j]; 
/*     */       } 
/* 313 */       this.pxlStride = csm.getPixelStride();
/* 314 */       this.lineStride = csm.getScanlineStride();
/*     */       
/* 316 */       this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 322 */         .optimal = (this.bandStride == 0 || (this.pxlStride < this.lineStride && this.pxlStride == this.numBands) || (this.lineStride < this.pxlStride && this.lineStride == this.numBands) || (this.pxlStride < this.lineStride && this.lineStride == this.numBands * csm.getWidth()) || (this.lineStride < this.pxlStride && this.pxlStride == this.numBands * csm.getHeight()) || csm instanceof java.awt.image.BandedSampleModel);
/*     */     }
/* 324 */     else if (this.sampleModel instanceof java.awt.image.SinglePixelPackedSampleModel || this.sampleModel instanceof java.awt.image.MultiPixelPackedSampleModel) {
/*     */       
/* 326 */       this.optimal = true;
/*     */     } 
/*     */     
/* 329 */     int numXTiles = getMaxTileX() - getMinTileX() + 1;
/* 330 */     int totalTiles = numXTiles * (getMaxTileY() - getMinTileY() + 1);
/*     */     
/* 332 */     for (int y = getMinTileY(); y <= getMaxTileY(); y++) {
/* 333 */       for (int x = getMinTileX(); x <= getMaxTileX(); x++) {
/* 334 */         writeRaster(getTile(x, y));
/*     */         
/* 336 */         float percentage = ((x + y * numXTiles) + 1.0F) / totalTiles;
/* 337 */         processImageProgress(percentage * 100.0F);
/*     */       } 
/*     */     } 
/*     */     
/* 341 */     this.stream.flush();
/* 342 */     if (abortRequested()) {
/* 343 */       processWriteAborted();
/*     */     } else {
/* 345 */       processImageComplete();
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getWidth() {
/* 350 */     return this.destinationRegion.width;
/*     */   }
/*     */   
/*     */   public int getHeight() {
/* 354 */     return this.destinationRegion.height;
/*     */   }
/*     */   
/*     */   private void writeRaster(Raster raster) throws IOException {
/* 358 */     int numBank = 0;
/* 359 */     int bandStride = 0;
/* 360 */     int[] bankIndices = null;
/* 361 */     int[] bandOffsets = null;
/* 362 */     int bandSize = 0;
/* 363 */     int numBand = this.sampleModel.getNumBands();
/* 364 */     int type = this.sampleModel.getDataType();
/*     */     
/* 366 */     if (this.sampleModel instanceof ComponentSampleModel) {
/* 367 */       ComponentSampleModel csm = (ComponentSampleModel)this.sampleModel;
/*     */       
/* 369 */       bandOffsets = csm.getBandOffsets(); int i;
/* 370 */       for (i = 0; i < numBand; i++) {
/* 371 */         if (bandStride < bandOffsets[i])
/* 372 */           bandStride = bandOffsets[i]; 
/*     */       } 
/* 374 */       bankIndices = csm.getBankIndices();
/* 375 */       for (i = 0; i < numBand; i++) {
/* 376 */         if (numBank < bankIndices[i])
/* 377 */           numBank = bankIndices[i]; 
/*     */       } 
/* 379 */       bandSize = (int)ImageUtil.getBandSize(this.sampleModel);
/*     */     } 
/*     */     
/* 382 */     byte[] bdata = null;
/* 383 */     short[] sdata = null;
/* 384 */     int[] idata = null;
/* 385 */     float[] fdata = null;
/* 386 */     double[] ddata = null;
/*     */     
/* 388 */     if (raster.getParent() != null && 
/* 389 */       !this.sampleModel.equals(raster.getParent().getSampleModel())) {
/*     */       
/* 391 */       WritableRaster ras = Raster.createWritableRaster(this.sampleModel, new Point(raster
/* 392 */             .getMinX(), raster
/* 393 */             .getMinY()));
/* 394 */       ras.setRect(raster);
/* 395 */       raster = ras;
/*     */     } 
/*     */     
/* 398 */     DataBuffer data = raster.getDataBuffer();
/*     */     
/* 400 */     if (this.optimal) {
/* 401 */       if (numBank > 0) {
/* 402 */         for (int i = 0; i < this.numBands; i++) {
/* 403 */           int bank = bankIndices[this.sourceBands[i]];
/* 404 */           switch (type) {
/*     */             case 0:
/* 406 */               bdata = ((DataBufferByte)data).getData(bank);
/* 407 */               this.stream.write(bdata, 0, bdata.length);
/*     */               break;
/*     */             case 2:
/* 410 */               sdata = ((DataBufferShort)data).getData(bank);
/* 411 */               this.stream.writeShorts(sdata, 0, sdata.length);
/*     */               break;
/*     */             case 1:
/* 414 */               sdata = ((DataBufferUShort)data).getData(bank);
/* 415 */               this.stream.writeShorts(sdata, 0, sdata.length);
/*     */               break;
/*     */             case 3:
/* 418 */               idata = ((DataBufferInt)data).getData(bank);
/* 419 */               this.stream.writeInts(idata, 0, idata.length);
/*     */               break;
/*     */             case 4:
/* 422 */               fdata = ((DataBufferFloat)data).getData(bank);
/* 423 */               this.stream.writeFloats(fdata, 0, fdata.length);
/*     */               break;
/*     */             case 5:
/* 426 */               ddata = ((DataBufferDouble)data).getData(bank);
/* 427 */               this.stream.writeDoubles(ddata, 0, ddata.length);
/*     */               break;
/*     */           } 
/*     */         } 
/*     */       } else {
/* 432 */         switch (type) {
/*     */           case 0:
/* 434 */             bdata = ((DataBufferByte)data).getData();
/*     */             break;
/*     */           case 2:
/* 437 */             sdata = ((DataBufferShort)data).getData();
/*     */             break;
/*     */           case 1:
/* 440 */             sdata = ((DataBufferUShort)data).getData();
/*     */             break;
/*     */           case 3:
/* 443 */             idata = ((DataBufferInt)data).getData();
/*     */             break;
/*     */           case 4:
/* 446 */             fdata = ((DataBufferFloat)data).getData();
/*     */             break;
/*     */           case 5:
/* 449 */             ddata = ((DataBufferDouble)data).getData();
/*     */             break;
/*     */         } 
/*     */         
/* 453 */         if (!this.noSubband && bandStride >= raster
/* 454 */           .getWidth() * raster
/* 455 */           .getHeight() * (this.numBands - 1)) {
/*     */           
/* 457 */           for (int i = 0; i < this.numBands; i++) {
/* 458 */             int offset = bandOffsets[this.sourceBands[i]];
/* 459 */             switch (type) {
/*     */               case 0:
/* 461 */                 this.stream.write(bdata, offset, bandSize);
/*     */                 break;
/*     */               case 1:
/*     */               case 2:
/* 465 */                 this.stream.writeShorts(sdata, offset, bandSize);
/*     */                 break;
/*     */               case 3:
/* 468 */                 this.stream.writeInts(idata, offset, bandSize);
/*     */                 break;
/*     */               case 4:
/* 471 */                 this.stream.writeFloats(fdata, offset, bandSize);
/*     */                 break;
/*     */               case 5:
/* 474 */                 this.stream.writeDoubles(ddata, offset, bandSize);
/*     */                 break;
/*     */             } 
/*     */           } 
/*     */         } else {
/* 479 */           switch (type) {
/*     */             case 0:
/* 481 */               this.stream.write(bdata, 0, bdata.length);
/*     */               break;
/*     */             case 1:
/*     */             case 2:
/* 485 */               this.stream.writeShorts(sdata, 0, sdata.length);
/*     */               break;
/*     */             case 3:
/* 488 */               this.stream.writeInts(idata, 0, idata.length);
/*     */               break;
/*     */             case 4:
/* 491 */               this.stream.writeFloats(fdata, 0, fdata.length);
/*     */               break;
/*     */             case 5:
/* 494 */               this.stream.writeDoubles(ddata, 0, ddata.length);
/*     */               break;
/*     */           } 
/*     */         } 
/*     */       } 
/* 499 */     } else if (this.sampleModel instanceof ComponentSampleModel) {
/*     */       
/* 501 */       switch (type) {
/*     */         case 0:
/* 503 */           bdata = ((DataBufferByte)data).getData();
/*     */           break;
/*     */         case 2:
/* 506 */           sdata = ((DataBufferShort)data).getData();
/*     */           break;
/*     */         case 1:
/* 509 */           sdata = ((DataBufferUShort)data).getData();
/*     */           break;
/*     */         case 3:
/* 512 */           idata = ((DataBufferInt)data).getData();
/*     */           break;
/*     */         case 4:
/* 515 */           fdata = ((DataBufferFloat)data).getData();
/*     */           break;
/*     */         case 5:
/* 518 */           ddata = ((DataBufferDouble)data).getData();
/*     */           break;
/*     */       } 
/*     */       
/* 522 */       ComponentSampleModel csm = (ComponentSampleModel)this.sampleModel;
/*     */       
/* 524 */       int offset = csm.getOffset(raster.getMinX() - raster.getSampleModelTranslateX(), raster
/* 525 */           .getMinY() - raster.getSampleModelTranslateY()) - bandOffsets[0];
/*     */ 
/*     */       
/* 528 */       int srcSkip = this.pxlStride;
/* 529 */       int copyLength = 1;
/* 530 */       int innerStep = this.pxlStride;
/*     */       
/* 532 */       int width = raster.getWidth();
/* 533 */       int height = raster.getHeight();
/*     */       
/* 535 */       int innerBound = width;
/* 536 */       int outerBound = height;
/*     */       
/* 538 */       if (srcSkip < this.lineStride) {
/* 539 */         if (bandStride > this.pxlStride)
/* 540 */           copyLength = width; 
/* 541 */         srcSkip = this.lineStride;
/*     */       } else {
/* 543 */         if (bandStride > this.lineStride)
/* 544 */           copyLength = height; 
/* 545 */         innerStep = this.lineStride;
/* 546 */         innerBound = height;
/* 547 */         outerBound = width;
/*     */       } 
/*     */       
/* 550 */       int writeLength = innerBound * this.numBands;
/* 551 */       byte[] destBBuf = null;
/* 552 */       short[] destSBuf = null;
/* 553 */       int[] destIBuf = null;
/* 554 */       float[] destFBuf = null;
/* 555 */       double[] destDBuf = null;
/* 556 */       Object srcBuf = null;
/* 557 */       Object dstBuf = null;
/*     */       
/* 559 */       switch (type) {
/*     */         case 0:
/* 561 */           srcBuf = bdata;
/* 562 */           dstBuf = destBBuf = new byte[writeLength];
/*     */           break;
/*     */         case 1:
/*     */         case 2:
/* 566 */           srcBuf = sdata;
/* 567 */           dstBuf = destSBuf = new short[writeLength];
/*     */           break;
/*     */         case 3:
/* 570 */           srcBuf = idata;
/* 571 */           dstBuf = destIBuf = new int[writeLength];
/*     */           break;
/*     */         case 4:
/* 574 */           srcBuf = fdata;
/* 575 */           dstBuf = destFBuf = new float[writeLength];
/*     */           break;
/*     */         case 5:
/* 578 */           srcBuf = ddata;
/* 579 */           dstBuf = destDBuf = new double[writeLength];
/*     */           break;
/*     */       } 
/*     */       
/* 583 */       if (copyLength > 1) {
/* 584 */         for (int i = 0; i < outerBound; i++) {
/* 585 */           for (int b = 0; b < this.numBands; b++) {
/* 586 */             int bandOffset = bandOffsets[b];
/*     */             
/* 588 */             System.arraycopy(srcBuf, offset + bandOffset, dstBuf, b * innerBound, innerBound);
/*     */           } 
/*     */ 
/*     */           
/* 592 */           switch (type) {
/*     */             case 0:
/* 594 */               this.stream.write((byte[])dstBuf, 0, writeLength);
/*     */               break;
/*     */             case 1:
/*     */             case 2:
/* 598 */               this.stream.writeShorts((short[])dstBuf, 0, writeLength);
/*     */               break;
/*     */             case 3:
/* 601 */               this.stream.writeInts((int[])dstBuf, 0, writeLength);
/*     */               break;
/*     */             case 4:
/* 604 */               this.stream.writeFloats((float[])dstBuf, 0, writeLength);
/*     */               break;
/*     */             case 5:
/* 607 */               this.stream.writeDoubles((double[])dstBuf, 0, writeLength);
/*     */               break;
/*     */           } 
/* 610 */           offset += srcSkip;
/*     */         } 
/*     */       } else {
/* 613 */         int i; switch (type) {
/*     */           case 0:
/* 615 */             for (i = 0; i < outerBound; i++) {
/* 616 */               for (int b = 0, k = 0; b < this.numBands; b++) {
/* 617 */                 int bandOffset = bandOffsets[b];
/*     */                 int m;
/* 619 */                 for (int j = 0; j < innerBound; 
/* 620 */                   j++, m += innerStep)
/*     */                 {
/* 622 */                   destBBuf[k++] = bdata[m + bandOffset];
/*     */                 }
/*     */               } 
/* 625 */               this.stream.write(destBBuf, 0, writeLength);
/* 626 */               offset += srcSkip;
/*     */             } 
/*     */             break;
/*     */           
/*     */           case 1:
/*     */           case 2:
/* 632 */             for (i = 0; i < outerBound; i++) {
/* 633 */               for (int b = 0, k = 0; b < this.numBands; b++) {
/* 634 */                 int bandOffset = bandOffsets[b];
/*     */                 int m;
/* 636 */                 for (int j = 0; j < innerBound; 
/* 637 */                   j++, m += innerStep)
/*     */                 {
/* 639 */                   destSBuf[k++] = sdata[m + bandOffset];
/*     */                 }
/*     */               } 
/* 642 */               this.stream.writeShorts(destSBuf, 0, writeLength);
/* 643 */               offset += srcSkip;
/*     */             } 
/*     */             break;
/*     */           
/*     */           case 3:
/* 648 */             for (i = 0; i < outerBound; i++) {
/* 649 */               for (int b = 0, k = 0; b < this.numBands; b++) {
/* 650 */                 int bandOffset = bandOffsets[b];
/*     */                 int m;
/* 652 */                 for (int j = 0; j < innerBound; 
/* 653 */                   j++, m += innerStep)
/*     */                 {
/* 655 */                   destIBuf[k++] = idata[m + bandOffset];
/*     */                 }
/*     */               } 
/* 658 */               this.stream.writeInts(destIBuf, 0, writeLength);
/* 659 */               offset += srcSkip;
/*     */             } 
/*     */             break;
/*     */           
/*     */           case 4:
/* 664 */             for (i = 0; i < outerBound; i++) {
/* 665 */               for (int b = 0, k = 0; b < this.numBands; b++) {
/* 666 */                 int bandOffset = bandOffsets[b];
/*     */                 int m;
/* 668 */                 for (int j = 0; j < innerBound; 
/* 669 */                   j++, m += innerStep)
/*     */                 {
/* 671 */                   destFBuf[k++] = fdata[m + bandOffset];
/*     */                 }
/*     */               } 
/* 674 */               this.stream.writeFloats(destFBuf, 0, writeLength);
/* 675 */               offset += srcSkip;
/*     */             } 
/*     */             break;
/*     */           
/*     */           case 5:
/* 680 */             for (i = 0; i < outerBound; i++) {
/* 681 */               for (int b = 0, k = 0; b < this.numBands; b++) {
/* 682 */                 int bandOffset = bandOffsets[b];
/*     */                 int m;
/* 684 */                 for (int j = 0; j < innerBound; 
/* 685 */                   j++, m += innerStep)
/*     */                 {
/* 687 */                   destDBuf[k++] = ddata[m + bandOffset];
/*     */                 }
/*     */               } 
/* 690 */               this.stream.writeDoubles(destDBuf, 0, writeLength);
/* 691 */               offset += srcSkip;
/*     */             } 
/*     */             break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Raster getTile(int tileX, int tileY) {
/* 701 */     int sx = this.tileXOffset + tileX * this.tileWidth;
/* 702 */     int sy = this.tileYOffset + tileY * this.tileHeight;
/* 703 */     Rectangle bounds = new Rectangle(sx, sy, this.tileWidth, this.tileHeight);
/*     */     
/* 705 */     if (this.writeRaster) {
/* 706 */       bounds = bounds.intersection(this.destinationRegion);
/* 707 */       if (this.noTransform) {
/* 708 */         return this.inputRaster.createChild(bounds.x, bounds.y, bounds.width, bounds.height, bounds.x, bounds.y, this.sourceBands);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 713 */       sx = bounds.x;
/* 714 */       sy = bounds.y;
/*     */ 
/*     */       
/* 717 */       WritableRaster writableRaster = Raster.createWritableRaster(this.sampleModel, new Point(sx, sy));
/*     */       
/* 719 */       int i = mapToSourceX(sx);
/* 720 */       int k = mapToSourceY(sy);
/*     */       
/* 722 */       int m = this.inputRaster.getMinY();
/* 723 */       int n = this.inputRaster.getMinY() + this.inputRaster.getHeight();
/*     */       
/* 725 */       int i1 = bounds.width;
/*     */       
/* 727 */       int i2 = (i1 - 1) * this.scaleX + 1;
/*     */       
/* 729 */       for (int i3 = 0; i3 < bounds.height; i3++, sy++, k += this.scaleY) {
/* 730 */         if (k >= m && k < n) {
/*     */ 
/*     */           
/* 733 */           Raster source = this.inputRaster.createChild(i, k, i2, 1, i, k, null);
/*     */           
/* 735 */           int tempX = sx; int offset;
/* 736 */           for (int i4 = 0; i4 < i1; 
/* 737 */             i4++, tempX++, offset += this.scaleX) {
/* 738 */             for (int i5 = 0; i5 < this.numBands; i5++) {
/* 739 */               int p = source.getSample(offset, k, this.sourceBands[i5]);
/* 740 */               writableRaster.setSample(tempX, sy, i5, p);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 745 */       return writableRaster;
/*     */     } 
/*     */     
/* 748 */     if (this.noTransform) {
/* 749 */       Raster raster = this.input.getTile(tileX, tileY);
/* 750 */       if (this.destinationRegion.contains(bounds) && this.noSubband) {
/* 751 */         return raster;
/*     */       }
/* 753 */       bounds = bounds.intersection(this.destinationRegion);
/* 754 */       return raster.createChild(bounds.x, bounds.y, bounds.width, bounds.height, bounds.x, bounds.y, this.sourceBands);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 760 */     bounds = bounds.intersection(this.destinationRegion);
/* 761 */     sx = bounds.x;
/* 762 */     sy = bounds.y;
/*     */ 
/*     */     
/* 765 */     WritableRaster ras = Raster.createWritableRaster(this.sampleModel, new Point(sx, sy));
/*     */     
/* 767 */     int x = mapToSourceX(sx);
/* 768 */     int y = mapToSourceY(sy);
/*     */     
/* 770 */     int minY = this.input.getMinY();
/* 771 */     int maxY = this.input.getMinY() + this.input.getHeight();
/*     */     
/* 773 */     int cTileWidth = bounds.width;
/* 774 */     int length = (cTileWidth - 1) * this.scaleX + 1;
/*     */     
/* 776 */     for (int j = 0; j < bounds.height; j++, sy++, y += this.scaleY) {
/* 777 */       if (y >= minY && y < maxY) {
/*     */ 
/*     */ 
/*     */         
/* 781 */         Raster source = this.input.getData(new Rectangle(x, y, length, 1));
/*     */         
/* 783 */         int tempX = sx; int offset;
/* 784 */         for (int i = 0; i < cTileWidth; 
/* 785 */           i++, tempX++, offset += this.scaleX) {
/* 786 */           for (int k = 0; k < this.numBands; k++) {
/* 787 */             int p = source.getSample(offset, y, this.sourceBands[k]);
/* 788 */             ras.setSample(tempX, sy, k, p);
/*     */           } 
/*     */         } 
/*     */       } 
/* 792 */     }  return ras;
/*     */   }
/*     */ 
/*     */   
/*     */   private int mapToSourceX(int x) {
/* 797 */     return x * this.scaleX + this.xOffset;
/*     */   }
/*     */   
/*     */   private int mapToSourceY(int y) {
/* 801 */     return y * this.scaleY + this.yOffset;
/*     */   }
/*     */   
/*     */   private int getMinTileX() {
/* 805 */     return ToTile(this.destinationRegion.x, this.tileXOffset, this.tileWidth);
/*     */   }
/*     */   
/*     */   private int getMaxTileX() {
/* 809 */     return ToTile(this.destinationRegion.x + this.destinationRegion.width - 1, this.tileXOffset, this.tileWidth);
/*     */   }
/*     */ 
/*     */   
/*     */   private int getMinTileY() {
/* 814 */     return ToTile(this.destinationRegion.y, this.tileYOffset, this.tileHeight);
/*     */   }
/*     */   
/*     */   private int getMaxTileY() {
/* 818 */     return ToTile(this.destinationRegion.y + this.destinationRegion.height - 1, this.tileYOffset, this.tileHeight);
/*     */   }
/*     */ 
/*     */   
/*     */   private static int ToTile(int pos, int tileOffset, int tileSize) {
/* 823 */     pos -= tileOffset;
/* 824 */     if (pos < 0) {
/* 825 */       pos += 1 - tileSize;
/*     */     }
/* 827 */     return pos / tileSize;
/*     */   }
/*     */   
/*     */   public void reset() {
/* 831 */     super.reset();
/* 832 */     this.stream = null;
/* 833 */     this.optimal = false;
/* 834 */     this.sourceBands = null;
/* 835 */     this.destinationRegion = null;
/* 836 */     this.noTransform = true;
/* 837 */     this.noSubband = true;
/* 838 */     this.writeRaster = false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\raw\RawImageWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */