/*      */ package com.github.jaiimageio.impl.plugins.bmp;
/*      */ 
/*      */ import com.github.jaiimageio.impl.common.ImageUtil;
/*      */ import com.github.jaiimageio.plugins.bmp.BMPImageWriteParam;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ComponentSampleModel;
/*      */ import java.awt.image.DataBuffer;
/*      */ import java.awt.image.DataBufferByte;
/*      */ import java.awt.image.DataBufferInt;
/*      */ import java.awt.image.DataBufferShort;
/*      */ import java.awt.image.DataBufferUShort;
/*      */ import java.awt.image.DirectColorModel;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.MultiPixelPackedSampleModel;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.RenderedImage;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.awt.image.SinglePixelPackedSampleModel;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.nio.ByteOrder;
/*      */ import java.util.Iterator;
/*      */ import javax.imageio.IIOException;
/*      */ import javax.imageio.IIOImage;
/*      */ import javax.imageio.ImageIO;
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ import javax.imageio.ImageWriteParam;
/*      */ import javax.imageio.ImageWriter;
/*      */ import javax.imageio.event.IIOWriteProgressListener;
/*      */ import javax.imageio.event.IIOWriteWarningListener;
/*      */ import javax.imageio.metadata.IIOInvalidTreeException;
/*      */ import javax.imageio.metadata.IIOMetadata;
/*      */ import javax.imageio.spi.ImageWriterSpi;
/*      */ import javax.imageio.stream.ImageOutputStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BMPImageWriter
/*      */   extends ImageWriter
/*      */   implements BMPConstants
/*      */ {
/*   96 */   private ImageOutputStream stream = null;
/*   97 */   private ByteArrayOutputStream embedded_stream = null; private int compressionType;
/*      */   private boolean isTopDown;
/*      */   private int w;
/*      */   private int h;
/*  101 */   private int compImageSize = 0;
/*      */   
/*      */   private int[] bitMasks;
/*      */   
/*      */   private int[] bitPos;
/*      */   
/*      */   private byte[] bpixels;
/*      */   private short[] spixels;
/*      */   private int[] ipixels;
/*      */   
/*      */   public BMPImageWriter(ImageWriterSpi originator) {
/*  112 */     super(originator);
/*      */   }
/*      */   
/*      */   public void setOutput(Object output) {
/*  116 */     super.setOutput(output);
/*  117 */     if (output != null) {
/*  118 */       if (!(output instanceof ImageOutputStream))
/*  119 */         throw new IllegalArgumentException(I18N.getString("BMPImageWriter0")); 
/*  120 */       this.stream = (ImageOutputStream)output;
/*  121 */       this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
/*      */     } else {
/*  123 */       this.stream = null;
/*      */     } 
/*      */   }
/*      */   public ImageWriteParam getDefaultWriteParam() {
/*  127 */     return (ImageWriteParam)new BMPImageWriteParam();
/*      */   }
/*      */   
/*      */   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
/*  131 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
/*  136 */     BMPMetadata meta = new BMPMetadata();
/*  137 */     meta.initialize(imageType.getColorModel(), imageType
/*  138 */         .getSampleModel(), param);
/*      */     
/*  140 */     return meta;
/*      */   }
/*      */ 
/*      */   
/*      */   public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
/*  145 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
/*  153 */     if (inData == null) {
/*  154 */       throw new IllegalArgumentException("inData == null!");
/*      */     }
/*  156 */     if (imageType == null) {
/*  157 */       throw new IllegalArgumentException("imageType == null!");
/*      */     }
/*      */     
/*  160 */     BMPMetadata outData = null;
/*      */ 
/*      */     
/*  163 */     if (inData instanceof BMPMetadata) {
/*      */       
/*  165 */       outData = (BMPMetadata)((BMPMetadata)inData).clone();
/*      */     } else {
/*      */       try {
/*  168 */         outData = new BMPMetadata(inData);
/*  169 */       } catch (IIOInvalidTreeException e) {
/*      */         
/*  171 */         outData = new BMPMetadata();
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  176 */     outData.initialize(imageType.getColorModel(), imageType
/*  177 */         .getSampleModel(), param);
/*      */ 
/*      */     
/*  180 */     return outData;
/*      */   }
/*      */   
/*      */   public boolean canWriteRasters() {
/*  184 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
/*      */     int k;
/*  191 */     if (this.stream == null) {
/*  192 */       throw new IllegalStateException(I18N.getString("BMPImageWriter7"));
/*      */     }
/*      */     
/*  195 */     if (image == null) {
/*  196 */       throw new IllegalArgumentException(I18N.getString("BMPImageWriter8"));
/*      */     }
/*      */     
/*  199 */     clearAbortRequest();
/*  200 */     processImageStarted(0);
/*  201 */     if (param == null) {
/*  202 */       param = getDefaultWriteParam();
/*      */     }
/*  204 */     BMPImageWriteParam bmpParam = (BMPImageWriteParam)param;
/*      */ 
/*      */     
/*  207 */     int bitsPerPixel = 24;
/*  208 */     boolean isPalette = false;
/*  209 */     int paletteEntries = 0;
/*  210 */     IndexColorModel icm = null;
/*      */     
/*  212 */     RenderedImage input = null;
/*  213 */     Raster inputRaster = null;
/*  214 */     boolean writeRaster = image.hasRaster();
/*  215 */     Rectangle sourceRegion = param.getSourceRegion();
/*  216 */     SampleModel sampleModel = null;
/*  217 */     ColorModel colorModel = null;
/*      */     
/*  219 */     this.compImageSize = 0;
/*      */     
/*  221 */     if (writeRaster)
/*  222 */     { inputRaster = image.getRaster();
/*  223 */       sampleModel = inputRaster.getSampleModel();
/*  224 */       colorModel = ImageUtil.createColorModel(null, sampleModel);
/*  225 */       if (sourceRegion == null) {
/*  226 */         sourceRegion = inputRaster.getBounds();
/*      */       } else {
/*  228 */         sourceRegion = sourceRegion.intersection(inputRaster.getBounds());
/*      */       }  }
/*  230 */     else { input = image.getRenderedImage();
/*  231 */       sampleModel = input.getSampleModel();
/*  232 */       colorModel = input.getColorModel();
/*      */       
/*  234 */       Rectangle rect = new Rectangle(input.getMinX(), input.getMinY(), input.getWidth(), input.getHeight());
/*  235 */       if (sourceRegion == null) {
/*  236 */         sourceRegion = rect;
/*      */       } else {
/*  238 */         sourceRegion = sourceRegion.intersection(rect);
/*      */       }  }
/*      */     
/*  241 */     IIOMetadata imageMetadata = image.getMetadata();
/*  242 */     BMPMetadata bmpImageMetadata = null;
/*  243 */     ImageTypeSpecifier imageType = new ImageTypeSpecifier(colorModel, sampleModel);
/*      */     
/*  245 */     if (imageMetadata != null) {
/*      */ 
/*      */       
/*  248 */       bmpImageMetadata = (BMPMetadata)convertImageMetadata(imageMetadata, imageType, param);
/*      */     
/*      */     }
/*      */     else {
/*      */       
/*  253 */       bmpImageMetadata = (BMPMetadata)getDefaultImageMetadata(imageType, param);
/*      */     } 
/*      */     
/*  256 */     if (sourceRegion.isEmpty()) {
/*  257 */       throw new RuntimeException(I18N.getString("BMPImageWrite0"));
/*      */     }
/*  259 */     int scaleX = param.getSourceXSubsampling();
/*  260 */     int scaleY = param.getSourceYSubsampling();
/*  261 */     int xOffset = param.getSubsamplingXOffset();
/*  262 */     int yOffset = param.getSubsamplingYOffset();
/*      */ 
/*      */     
/*  265 */     int dataType = sampleModel.getDataType();
/*      */     
/*  267 */     sourceRegion.translate(xOffset, yOffset);
/*  268 */     sourceRegion.width -= xOffset;
/*  269 */     sourceRegion.height -= yOffset;
/*      */     
/*  271 */     int minX = sourceRegion.x / scaleX;
/*  272 */     int minY = sourceRegion.y / scaleY;
/*  273 */     this.w = (sourceRegion.width + scaleX - 1) / scaleX;
/*  274 */     this.h = (sourceRegion.height + scaleY - 1) / scaleY;
/*  275 */     xOffset = sourceRegion.x % scaleX;
/*  276 */     yOffset = sourceRegion.y % scaleY;
/*      */     
/*  278 */     Rectangle destinationRegion = new Rectangle(minX, minY, this.w, this.h);
/*  279 */     int noTransform = destinationRegion.equals(sourceRegion);
/*      */ 
/*      */     
/*  282 */     int[] sourceBands = param.getSourceBands();
/*  283 */     boolean noSubband = true;
/*  284 */     int numBands = sampleModel.getNumBands();
/*      */     
/*  286 */     if (sourceBands != null) {
/*  287 */       sampleModel = sampleModel.createSubsetSampleModel(sourceBands);
/*  288 */       colorModel = null;
/*  289 */       noSubband = false;
/*  290 */       numBands = sampleModel.getNumBands();
/*      */     } else {
/*  292 */       sourceBands = new int[numBands];
/*  293 */       for (int n = 0; n < numBands; n++) {
/*  294 */         sourceBands[n] = n;
/*      */       }
/*      */     } 
/*  297 */     int[] bandOffsets = null;
/*  298 */     boolean bgrOrder = true;
/*      */     
/*  300 */     if (sampleModel instanceof ComponentSampleModel) {
/*  301 */       bandOffsets = ((ComponentSampleModel)sampleModel).getBandOffsets();
/*  302 */       if (sampleModel instanceof java.awt.image.BandedSampleModel)
/*      */       {
/*      */         
/*  305 */         bgrOrder = false;
/*      */       
/*      */       }
/*      */       else
/*      */       {
/*  310 */         for (int n = 0; n < bandOffsets.length; n++) {
/*  311 */           k = bgrOrder & ((bandOffsets[n] == bandOffsets.length - n - 1) ? 1 : 0);
/*      */         }
/*      */       }
/*      */     
/*  315 */     } else if (sampleModel instanceof SinglePixelPackedSampleModel) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  320 */       int[] bitOffsets = ((SinglePixelPackedSampleModel)sampleModel).getBitOffsets();
/*  321 */       for (int n = 0; n < bitOffsets.length - 1; n++) {
/*  322 */         k &= (bitOffsets[n] > bitOffsets[n + 1]) ? 1 : 0;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  327 */     if (bandOffsets == null) {
/*      */ 
/*      */       
/*  330 */       bandOffsets = new int[numBands];
/*  331 */       for (int n = 0; n < numBands; n++) {
/*  332 */         bandOffsets[n] = n;
/*      */       }
/*      */     } 
/*  335 */     int j = noTransform & k;
/*      */     
/*  337 */     int[] sampleSize = sampleModel.getSampleSize();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  342 */     int destScanlineBytes = this.w * numBands;
/*      */     
/*  344 */     switch (bmpParam.getCompressionMode()) {
/*      */       case 2:
/*  346 */         this.compressionType = getCompressionType(bmpParam.getCompressionType());
/*      */         break;
/*      */       case 3:
/*  349 */         this.compressionType = bmpImageMetadata.compression;
/*      */         break;
/*      */       case 1:
/*  352 */         this.compressionType = getPreferredCompressionType(colorModel, sampleModel);
/*      */         break;
/*      */       
/*      */       default:
/*  356 */         this.compressionType = 0;
/*      */         break;
/*      */     } 
/*  359 */     if (!canEncodeImage(this.compressionType, colorModel, sampleModel)) {
/*  360 */       if (param.getCompressionMode() == 2) {
/*  361 */         throw new IIOException("Image can not be encoded with compression type " + compressionTypeNames[this.compressionType]);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  366 */       this.compressionType = getPreferredCompressionType(colorModel, sampleModel);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  371 */     byte[] r = null, g = null, b = null, a = null;
/*      */     
/*  373 */     if (this.compressionType == 3) {
/*      */       
/*  375 */       bitsPerPixel = DataBuffer.getDataTypeSize(sampleModel.getDataType());
/*      */       
/*  377 */       if (bitsPerPixel != 16 && bitsPerPixel != 32) {
/*      */ 
/*      */         
/*  380 */         bitsPerPixel = 32;
/*      */ 
/*      */ 
/*      */         
/*  384 */         j = 0;
/*      */       } 
/*      */       
/*  387 */       destScanlineBytes = this.w * bitsPerPixel + 7 >> 3;
/*      */       
/*  389 */       isPalette = true;
/*  390 */       paletteEntries = 3;
/*  391 */       r = new byte[paletteEntries];
/*  392 */       g = new byte[paletteEntries];
/*  393 */       b = new byte[paletteEntries];
/*  394 */       a = new byte[paletteEntries];
/*      */       
/*  396 */       int rmask = 16711680;
/*  397 */       int gmask = 65280;
/*  398 */       int bmask = 255;
/*      */       
/*  400 */       if (bitsPerPixel == 16)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  407 */         if (colorModel instanceof DirectColorModel) {
/*  408 */           DirectColorModel dcm = (DirectColorModel)colorModel;
/*  409 */           rmask = dcm.getRedMask();
/*  410 */           gmask = dcm.getGreenMask();
/*  411 */           bmask = dcm.getBlueMask();
/*      */         }
/*      */         else {
/*      */           
/*  415 */           throw new IOException("Image can not be encoded with compression type " + compressionTypeNames[this.compressionType]);
/*      */         } 
/*      */       }
/*      */ 
/*      */       
/*  420 */       writeMaskToPalette(rmask, 0, r, g, b, a);
/*  421 */       writeMaskToPalette(gmask, 1, r, g, b, a);
/*  422 */       writeMaskToPalette(bmask, 2, r, g, b, a);
/*      */       
/*  424 */       if (j == 0) {
/*      */         
/*  426 */         this.bitMasks = new int[3];
/*  427 */         this.bitMasks[0] = rmask;
/*  428 */         this.bitMasks[1] = gmask;
/*  429 */         this.bitMasks[2] = bmask;
/*      */         
/*  431 */         this.bitPos = new int[3];
/*  432 */         this.bitPos[0] = firstLowBit(rmask);
/*  433 */         this.bitPos[1] = firstLowBit(gmask);
/*  434 */         this.bitPos[2] = firstLowBit(bmask);
/*      */       } 
/*      */       
/*  437 */       if (colorModel instanceof IndexColorModel) {
/*  438 */         icm = (IndexColorModel)colorModel;
/*      */       }
/*      */     }
/*  441 */     else if (colorModel instanceof IndexColorModel) {
/*  442 */       isPalette = true;
/*  443 */       icm = (IndexColorModel)colorModel;
/*  444 */       paletteEntries = icm.getMapSize();
/*      */       
/*  446 */       if (paletteEntries <= 2) {
/*  447 */         bitsPerPixel = 1;
/*  448 */         destScanlineBytes = this.w + 7 >> 3;
/*  449 */       } else if (paletteEntries <= 16) {
/*  450 */         bitsPerPixel = 4;
/*  451 */         destScanlineBytes = this.w + 1 >> 1;
/*  452 */       } else if (paletteEntries <= 256) {
/*  453 */         bitsPerPixel = 8;
/*      */       }
/*      */       else {
/*      */         
/*  457 */         bitsPerPixel = 24;
/*  458 */         isPalette = false;
/*  459 */         paletteEntries = 0;
/*  460 */         destScanlineBytes = this.w * 3;
/*      */       } 
/*      */       
/*  463 */       if (isPalette == true) {
/*  464 */         r = new byte[paletteEntries];
/*  465 */         g = new byte[paletteEntries];
/*  466 */         b = new byte[paletteEntries];
/*      */         
/*  468 */         icm.getReds(r);
/*  469 */         icm.getGreens(g);
/*  470 */         icm.getBlues(b);
/*      */       
/*      */       }
/*      */     
/*      */     }
/*  475 */     else if (numBands == 1) {
/*      */       
/*  477 */       isPalette = true;
/*  478 */       paletteEntries = 256;
/*  479 */       bitsPerPixel = sampleSize[0];
/*      */       
/*  481 */       destScanlineBytes = this.w * bitsPerPixel + 7 >> 3;
/*      */       
/*  483 */       r = new byte[256];
/*  484 */       g = new byte[256];
/*  485 */       b = new byte[256];
/*      */       
/*  487 */       for (int n = 0; n < 256; n++) {
/*  488 */         r[n] = (byte)n;
/*  489 */         g[n] = (byte)n;
/*  490 */         b[n] = (byte)n;
/*      */       }
/*      */     
/*      */     }
/*  494 */     else if (sampleModel instanceof SinglePixelPackedSampleModel && noSubband) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  503 */       int[] sample_sizes = sampleModel.getSampleSize();
/*  504 */       bitsPerPixel = 0;
/*  505 */       for (int n = 0; n < sample_sizes.length; n++) {
/*  506 */         bitsPerPixel += sample_sizes[n];
/*      */       }
/*  508 */       bitsPerPixel = roundBpp(bitsPerPixel);
/*  509 */       if (bitsPerPixel != DataBuffer.getDataTypeSize(sampleModel.getDataType())) {
/*  510 */         j = 0;
/*      */       }
/*  512 */       destScanlineBytes = this.w * bitsPerPixel + 7 >> 3;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  519 */     int fileSize = 0;
/*  520 */     int offset = 0;
/*  521 */     int headerSize = 0;
/*  522 */     int imageSize = 0;
/*  523 */     int xPelsPerMeter = bmpImageMetadata.xPixelsPerMeter;
/*  524 */     int yPelsPerMeter = bmpImageMetadata.yPixelsPerMeter;
/*  525 */     int colorsUsed = (bmpImageMetadata.colorsUsed > 0) ? bmpImageMetadata.colorsUsed : paletteEntries;
/*      */     
/*  527 */     int colorsImportant = paletteEntries;
/*      */ 
/*      */     
/*  530 */     int padding = destScanlineBytes % 4;
/*  531 */     if (padding != 0) {
/*  532 */       padding = 4 - padding;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  538 */     offset = 54 + paletteEntries * 4;
/*      */     
/*  540 */     imageSize = (destScanlineBytes + padding) * this.h;
/*  541 */     fileSize = imageSize + offset;
/*  542 */     headerSize = 40;
/*      */     
/*  544 */     long headPos = this.stream.getStreamPosition();
/*      */     
/*  546 */     if (param instanceof BMPImageWriteParam) {
/*  547 */       this.isTopDown = ((BMPImageWriteParam)param).isTopDown();
/*      */ 
/*      */       
/*  550 */       if (this.compressionType != 0 && this.compressionType != 3)
/*  551 */         this.isTopDown = false; 
/*      */     } else {
/*  553 */       this.isTopDown = false;
/*      */     } 
/*      */     
/*  556 */     writeFileHeader(fileSize, offset);
/*      */     
/*  558 */     writeInfoHeader(headerSize, bitsPerPixel);
/*      */ 
/*      */     
/*  561 */     this.stream.writeInt(this.compressionType);
/*      */ 
/*      */     
/*  564 */     this.stream.writeInt(imageSize);
/*      */ 
/*      */     
/*  567 */     this.stream.writeInt(xPelsPerMeter);
/*      */ 
/*      */     
/*  570 */     this.stream.writeInt(yPelsPerMeter);
/*      */ 
/*      */     
/*  573 */     this.stream.writeInt(colorsUsed);
/*      */ 
/*      */     
/*  576 */     this.stream.writeInt(colorsImportant);
/*      */ 
/*      */     
/*  579 */     if (isPalette == true)
/*      */     {
/*      */       
/*  582 */       if (this.compressionType == 3) {
/*      */         
/*  584 */         for (int n = 0; n < 3; n++) {
/*  585 */           int mask = (a[n] & 0xFF) + (r[n] & 0xFF) * 256 + (g[n] & 0xFF) * 65536 + (b[n] & 0xFF) * 16777216;
/*  586 */           this.stream.writeInt(mask);
/*      */         } 
/*      */       } else {
/*  589 */         for (int n = 0; n < paletteEntries; n++) {
/*  590 */           this.stream.writeByte(b[n]);
/*  591 */           this.stream.writeByte(g[n]);
/*  592 */           this.stream.writeByte(r[n]);
/*  593 */           this.stream.writeByte(0);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  599 */     int scanlineBytes = this.w * numBands;
/*      */ 
/*      */     
/*  602 */     int[] pixels = new int[scanlineBytes * scaleX];
/*      */ 
/*      */ 
/*      */     
/*  606 */     this.bpixels = new byte[destScanlineBytes];
/*      */ 
/*      */ 
/*      */     
/*  610 */     if (this.compressionType == 4 || this.compressionType == 5) {
/*      */ 
/*      */       
/*  613 */       this.embedded_stream = new ByteArrayOutputStream();
/*  614 */       writeEmbedded(image, (ImageWriteParam)bmpParam);
/*      */       
/*  616 */       this.embedded_stream.flush();
/*  617 */       imageSize = this.embedded_stream.size();
/*      */       
/*  619 */       long endPos = this.stream.getStreamPosition();
/*  620 */       fileSize = offset + imageSize;
/*  621 */       this.stream.seek(headPos);
/*  622 */       writeSize(fileSize, 2);
/*  623 */       this.stream.seek(headPos);
/*  624 */       writeSize(imageSize, 34);
/*  625 */       this.stream.seek(endPos);
/*  626 */       this.stream.write(this.embedded_stream.toByteArray());
/*  627 */       this.embedded_stream = null;
/*      */       
/*  629 */       if (abortRequested()) {
/*  630 */         processWriteAborted();
/*      */       } else {
/*  632 */         processImageComplete();
/*  633 */         this.stream.flushBefore(this.stream.getStreamPosition());
/*      */       } 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  639 */     int maxBandOffset = bandOffsets[0];
/*  640 */     for (int i = 1; i < bandOffsets.length; i++) {
/*  641 */       if (bandOffsets[i] > maxBandOffset)
/*  642 */         maxBandOffset = bandOffsets[i]; 
/*      */     } 
/*  644 */     int[] pixel = new int[maxBandOffset + 1];
/*      */     
/*  646 */     int destScanlineLength = destScanlineBytes;
/*      */     
/*  648 */     if (j != 0 && noSubband) {
/*  649 */       destScanlineLength = destScanlineBytes / (DataBuffer.getDataTypeSize(dataType) >> 3);
/*      */     }
/*  651 */     for (int m = 0; m < this.h && 
/*  652 */       !abortRequested(); m++) {
/*      */ 
/*      */ 
/*      */       
/*  656 */       int row = minY + m;
/*      */       
/*  658 */       if (!this.isTopDown) {
/*  659 */         row = minY + this.h - m - 1;
/*      */       }
/*      */       
/*  662 */       Raster src = inputRaster;
/*      */       
/*  664 */       Rectangle srcRect = new Rectangle(minX * scaleX + xOffset, row * scaleY + yOffset, (this.w - 1) * scaleX + 1, 1);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  669 */       if (!writeRaster) {
/*  670 */         src = input.getData(srcRect);
/*      */       }
/*  672 */       if (j != 0 && noSubband) {
/*  673 */         SampleModel sm = src.getSampleModel();
/*  674 */         int pos = 0;
/*  675 */         int startX = srcRect.x - src.getSampleModelTranslateX();
/*  676 */         int startY = srcRect.y - src.getSampleModelTranslateY();
/*  677 */         if (sm instanceof ComponentSampleModel) {
/*  678 */           ComponentSampleModel csm = (ComponentSampleModel)sm;
/*  679 */           pos = csm.getOffset(startX, startY, 0);
/*  680 */           for (int nb = 1; nb < csm.getNumBands(); nb++) {
/*  681 */             if (pos > csm.getOffset(startX, startY, nb)) {
/*  682 */               pos = csm.getOffset(startX, startY, nb);
/*      */             }
/*      */           } 
/*  685 */         } else if (sm instanceof MultiPixelPackedSampleModel) {
/*  686 */           MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sm;
/*      */           
/*  688 */           pos = mppsm.getOffset(startX, startY);
/*  689 */         } else if (sm instanceof SinglePixelPackedSampleModel) {
/*  690 */           SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
/*      */           
/*  692 */           pos = sppsm.getOffset(startX, startY);
/*      */         } 
/*      */         
/*  695 */         if (this.compressionType == 0 || this.compressionType == 3) {
/*  696 */           byte[] bdata; short[] sdata; short[] usdata; int[] idata; switch (dataType) {
/*      */             
/*      */             case 0:
/*  699 */               bdata = ((DataBufferByte)src.getDataBuffer()).getData();
/*  700 */               this.stream.write(bdata, pos, destScanlineLength);
/*      */               break;
/*      */ 
/*      */             
/*      */             case 2:
/*  705 */               sdata = ((DataBufferShort)src.getDataBuffer()).getData();
/*  706 */               this.stream.writeShorts(sdata, pos, destScanlineLength);
/*      */               break;
/*      */ 
/*      */             
/*      */             case 1:
/*  711 */               usdata = ((DataBufferUShort)src.getDataBuffer()).getData();
/*  712 */               this.stream.writeShorts(usdata, pos, destScanlineLength);
/*      */               break;
/*      */ 
/*      */             
/*      */             case 3:
/*  717 */               idata = ((DataBufferInt)src.getDataBuffer()).getData();
/*  718 */               this.stream.writeInts(idata, pos, destScanlineLength);
/*      */               break;
/*      */           } 
/*      */           
/*  722 */           for (int n = 0; n < padding; n++) {
/*  723 */             this.stream.writeByte(0);
/*      */           }
/*  725 */         } else if (this.compressionType == 2) {
/*  726 */           if (this.bpixels == null || this.bpixels.length < scanlineBytes)
/*  727 */             this.bpixels = new byte[scanlineBytes]; 
/*  728 */           src.getPixels(srcRect.x, srcRect.y, srcRect.width, srcRect.height, pixels);
/*      */           
/*  730 */           for (int h = 0; h < scanlineBytes; h++) {
/*  731 */             this.bpixels[h] = (byte)pixels[h];
/*      */           }
/*  733 */           encodeRLE4(this.bpixels, scanlineBytes);
/*  734 */         } else if (this.compressionType == 1) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  739 */           if (this.bpixels == null || this.bpixels.length < scanlineBytes)
/*  740 */             this.bpixels = new byte[scanlineBytes]; 
/*  741 */           src.getPixels(srcRect.x, srcRect.y, srcRect.width, srcRect.height, pixels);
/*      */           
/*  743 */           for (int h = 0; h < scanlineBytes; h++) {
/*  744 */             this.bpixels[h] = (byte)pixels[h];
/*      */           }
/*      */           
/*  747 */           encodeRLE8(this.bpixels, scanlineBytes);
/*      */         } 
/*      */       } else {
/*  750 */         src.getPixels(srcRect.x, srcRect.y, srcRect.width, srcRect.height, pixels);
/*      */ 
/*      */         
/*  753 */         if (scaleX != 1 || maxBandOffset != numBands - 1) {
/*  754 */           int n; for (int i1 = 0, i2 = 0; i1 < this.w; 
/*  755 */             i1++, i2 += scaleX * numBands, n += numBands) {
/*      */             
/*  757 */             System.arraycopy(pixels, i2, pixel, 0, pixel.length);
/*      */             
/*  759 */             for (int i3 = 0; i3 < numBands; i3++)
/*      */             {
/*  761 */               pixels[n + i3] = pixel[sourceBands[i3]];
/*      */             }
/*      */           } 
/*      */         } 
/*  765 */         writePixels(0, scanlineBytes, bitsPerPixel, pixels, padding, numBands, icm);
/*      */       } 
/*      */ 
/*      */       
/*  769 */       processImageProgress(100.0F * m / this.h);
/*      */     } 
/*      */     
/*  772 */     if (this.compressionType == 2 || this.compressionType == 1) {
/*      */ 
/*      */       
/*  775 */       this.stream.writeByte(0);
/*  776 */       this.stream.writeByte(1);
/*  777 */       incCompImageSize(2);
/*      */       
/*  779 */       imageSize = this.compImageSize;
/*  780 */       fileSize = this.compImageSize + offset;
/*  781 */       long endPos = this.stream.getStreamPosition();
/*  782 */       this.stream.seek(headPos);
/*  783 */       writeSize(fileSize, 2);
/*  784 */       this.stream.seek(headPos);
/*  785 */       writeSize(imageSize, 34);
/*  786 */       this.stream.seek(endPos);
/*      */     } 
/*      */     
/*  789 */     if (abortRequested()) {
/*  790 */       processWriteAborted();
/*      */     } else {
/*  792 */       processImageComplete();
/*  793 */       this.stream.flushBefore(this.stream.getStreamPosition());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void writePixels(int l, int scanlineBytes, int bitsPerPixel, int[] pixels, int padding, int numBands, IndexColorModel icm) throws IOException {
/*      */     int j, entries, m;
/*      */     byte[] r, g, b;
/*  801 */     int i, pixel = 0;
/*  802 */     int k = 0;
/*  803 */     switch (bitsPerPixel) {
/*      */ 
/*      */       
/*      */       case 1:
/*  807 */         for (j = 0; j < scanlineBytes / 8; j++) {
/*  808 */           this.bpixels[k++] = (byte)(pixels[l++] << 7 | pixels[l++] << 6 | pixels[l++] << 5 | pixels[l++] << 4 | pixels[l++] << 3 | pixels[l++] << 2 | pixels[l++] << 1 | pixels[l++]);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  819 */         if (scanlineBytes % 8 > 0) {
/*  820 */           pixel = 0;
/*  821 */           for (j = 0; j < scanlineBytes % 8; j++) {
/*  822 */             pixel |= pixels[l++] << 7 - j;
/*      */           }
/*  824 */           this.bpixels[k++] = (byte)pixel;
/*      */         } 
/*  826 */         this.stream.write(this.bpixels, 0, (scanlineBytes + 7) / 8);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 4:
/*  831 */         if (this.compressionType == 2) {
/*  832 */           byte[] bipixels = new byte[scanlineBytes];
/*  833 */           for (int h = 0; h < scanlineBytes; h++) {
/*  834 */             bipixels[h] = (byte)pixels[l++];
/*      */           }
/*  836 */           encodeRLE4(bipixels, scanlineBytes); break;
/*      */         } 
/*  838 */         for (j = 0; j < scanlineBytes / 2; j++) {
/*  839 */           pixel = pixels[l++] << 4 | pixels[l++];
/*  840 */           this.bpixels[k++] = (byte)pixel;
/*      */         } 
/*      */         
/*  843 */         if (scanlineBytes % 2 == 1) {
/*  844 */           pixel = pixels[l] << 4;
/*  845 */           this.bpixels[k++] = (byte)pixel;
/*      */         } 
/*  847 */         this.stream.write(this.bpixels, 0, (scanlineBytes + 1) / 2);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 8:
/*  852 */         if (this.compressionType == 1) {
/*  853 */           for (int h = 0; h < scanlineBytes; h++) {
/*  854 */             this.bpixels[h] = (byte)pixels[l++];
/*      */           }
/*  856 */           encodeRLE8(this.bpixels, scanlineBytes); break;
/*      */         } 
/*  858 */         for (j = 0; j < scanlineBytes; j++) {
/*  859 */           this.bpixels[j] = (byte)pixels[l++];
/*      */         }
/*  861 */         this.stream.write(this.bpixels, 0, scanlineBytes);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 16:
/*  866 */         if (this.spixels == null) {
/*  867 */           this.spixels = new short[scanlineBytes / numBands];
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  877 */         for (j = 0, m = 0; j < scanlineBytes; m++) {
/*  878 */           this.spixels[m] = 0;
/*  879 */           if (this.compressionType == 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  884 */             this.spixels[m] = (short)((0x1F & pixels[j]) << 10 | (0x1F & pixels[j + 1]) << 5 | 0x1F & pixels[j + 2]);
/*      */ 
/*      */ 
/*      */             
/*  888 */             j += 3;
/*      */           } else {
/*  890 */             for (int n = 0; n < numBands; n++, j++) {
/*  891 */               this.spixels[m] = (short)(this.spixels[m] | pixels[j] << this.bitPos[n] & this.bitMasks[n]);
/*      */             }
/*      */           } 
/*      */         } 
/*      */         
/*  896 */         this.stream.writeShorts(this.spixels, 0, this.spixels.length);
/*      */         break;
/*      */       
/*      */       case 24:
/*  900 */         if (numBands == 3) {
/*  901 */           for (j = 0; j < scanlineBytes; j += 3) {
/*      */             
/*  903 */             this.bpixels[k++] = (byte)pixels[l + 2];
/*  904 */             this.bpixels[k++] = (byte)pixels[l + 1];
/*  905 */             this.bpixels[k++] = (byte)pixels[l];
/*  906 */             l += 3;
/*      */           } 
/*  908 */           this.stream.write(this.bpixels, 0, scanlineBytes);
/*      */           break;
/*      */         } 
/*  911 */         entries = icm.getMapSize();
/*      */         
/*  913 */         r = new byte[entries];
/*  914 */         g = new byte[entries];
/*  915 */         b = new byte[entries];
/*      */         
/*  917 */         icm.getReds(r);
/*  918 */         icm.getGreens(g);
/*  919 */         icm.getBlues(b);
/*      */ 
/*      */         
/*  922 */         for (i = 0; i < scanlineBytes; i++) {
/*  923 */           int index = pixels[l];
/*  924 */           this.bpixels[k++] = b[index];
/*  925 */           this.bpixels[k++] = g[index];
/*  926 */           this.bpixels[k++] = b[index];
/*  927 */           l++;
/*      */         } 
/*  929 */         this.stream.write(this.bpixels, 0, scanlineBytes * 3);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 32:
/*  934 */         if (this.ipixels == null)
/*  935 */           this.ipixels = new int[scanlineBytes / numBands]; 
/*  936 */         if (numBands == 3) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  946 */           for (int n = 0, i1 = 0; n < scanlineBytes; i1++) {
/*  947 */             this.ipixels[i1] = 0;
/*  948 */             if (this.compressionType == 0) {
/*  949 */               this.ipixels[i1] = (0xFF & pixels[n + 2]) << 16 | (0xFF & pixels[n + 1]) << 8 | 0xFF & pixels[n];
/*      */ 
/*      */ 
/*      */               
/*  953 */               n += 3;
/*      */             } else {
/*  955 */               for (int i2 = 0; i2 < numBands; i2++, n++) {
/*  956 */                 this.ipixels[i1] = this.ipixels[i1] | pixels[n] << this.bitPos[i2] & this.bitMasks[i2];
/*      */               
/*      */               }
/*      */             
/*      */             }
/*      */ 
/*      */           
/*      */           }
/*      */ 
/*      */         
/*      */         }
/*      */         else {
/*      */           
/*  969 */           for (int n = 0; n < scanlineBytes; n++) {
/*  970 */             if (icm != null) {
/*  971 */               this.ipixels[n] = icm.getRGB(pixels[n]);
/*      */             } else {
/*  973 */               this.ipixels[n] = pixels[n] << 16 | pixels[n] << 8 | pixels[n];
/*      */             } 
/*      */           } 
/*      */         } 
/*      */         
/*  978 */         this.stream.writeInts(this.ipixels, 0, this.ipixels.length);
/*      */         break;
/*      */     } 
/*      */ 
/*      */     
/*  983 */     if (this.compressionType == 0 || this.compressionType == 3)
/*      */     {
/*  985 */       for (k = 0; k < padding; k++) {
/*  986 */         this.stream.writeByte(0);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void encodeRLE8(byte[] bpixels, int scanlineBytes) throws IOException {
/*  994 */     int runCount = 1, absVal = -1, j = -1;
/*  995 */     byte runVal = 0, nextVal = 0;
/*      */     
/*  997 */     runVal = bpixels[++j];
/*  998 */     byte[] absBuf = new byte[256];
/*      */     
/* 1000 */     while (j < scanlineBytes - 1) {
/* 1001 */       nextVal = bpixels[++j];
/* 1002 */       if (nextVal == runVal) {
/* 1003 */         if (absVal >= 3) {
/*      */           
/* 1005 */           this.stream.writeByte(0);
/* 1006 */           this.stream.writeByte(absVal);
/* 1007 */           incCompImageSize(2);
/* 1008 */           for (int a = 0; a < absVal; a++) {
/* 1009 */             this.stream.writeByte(absBuf[a]);
/* 1010 */             incCompImageSize(1);
/*      */           } 
/* 1012 */           if (!isEven(absVal))
/*      */           {
/* 1014 */             this.stream.writeByte(0);
/* 1015 */             incCompImageSize(1);
/*      */           }
/*      */         
/* 1018 */         } else if (absVal > -1) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1023 */           for (int b = 0; b < absVal; b++) {
/* 1024 */             this.stream.writeByte(1);
/* 1025 */             this.stream.writeByte(absBuf[b]);
/* 1026 */             incCompImageSize(2);
/*      */           } 
/*      */         } 
/* 1029 */         absVal = -1;
/* 1030 */         runCount++;
/* 1031 */         if (runCount == 256) {
/*      */           
/* 1033 */           this.stream.writeByte(runCount - 1);
/* 1034 */           this.stream.writeByte(runVal);
/* 1035 */           incCompImageSize(2);
/* 1036 */           runCount = 1;
/*      */         } 
/*      */       } else {
/*      */         
/* 1040 */         if (runCount > 1) {
/*      */           
/* 1042 */           this.stream.writeByte(runCount);
/* 1043 */           this.stream.writeByte(runVal);
/* 1044 */           incCompImageSize(2);
/* 1045 */         } else if (absVal < 0) {
/*      */           
/* 1047 */           absBuf[++absVal] = runVal;
/* 1048 */           absBuf[++absVal] = nextVal;
/* 1049 */         } else if (absVal < 254) {
/*      */           
/* 1051 */           absBuf[++absVal] = nextVal;
/*      */         } else {
/* 1053 */           this.stream.writeByte(0);
/* 1054 */           this.stream.writeByte(absVal + 1);
/* 1055 */           incCompImageSize(2);
/* 1056 */           for (int a = 0; a <= absVal; a++) {
/* 1057 */             this.stream.writeByte(absBuf[a]);
/* 1058 */             incCompImageSize(1);
/*      */           } 
/*      */           
/* 1061 */           this.stream.writeByte(0);
/* 1062 */           incCompImageSize(1);
/* 1063 */           absVal = -1;
/*      */         } 
/* 1065 */         runVal = nextVal;
/* 1066 */         runCount = 1;
/*      */       } 
/*      */       
/* 1069 */       if (j == scanlineBytes - 1) {
/*      */         
/* 1071 */         if (absVal == -1) {
/* 1072 */           this.stream.writeByte(runCount);
/* 1073 */           this.stream.writeByte(runVal);
/* 1074 */           incCompImageSize(2);
/* 1075 */           runCount = 1;
/*      */ 
/*      */         
/*      */         }
/* 1079 */         else if (absVal >= 2) {
/* 1080 */           this.stream.writeByte(0);
/* 1081 */           this.stream.writeByte(absVal + 1);
/* 1082 */           incCompImageSize(2);
/* 1083 */           for (int a = 0; a <= absVal; a++) {
/* 1084 */             this.stream.writeByte(absBuf[a]);
/* 1085 */             incCompImageSize(1);
/*      */           } 
/* 1087 */           if (!isEven(absVal + 1))
/*      */           {
/* 1089 */             this.stream.writeByte(0);
/* 1090 */             incCompImageSize(1);
/*      */           }
/*      */         
/*      */         }
/* 1094 */         else if (absVal > -1) {
/* 1095 */           for (int b = 0; b <= absVal; b++) {
/* 1096 */             this.stream.writeByte(1);
/* 1097 */             this.stream.writeByte(absBuf[b]);
/* 1098 */             incCompImageSize(2);
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1104 */         this.stream.writeByte(0);
/* 1105 */         this.stream.writeByte(0);
/* 1106 */         incCompImageSize(2);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void encodeRLE4(byte[] bipixels, int scanlineBytes) throws IOException {
/* 1114 */     int runCount = 2, absVal = -1, j = -1, pixel = 0, q = 0;
/* 1115 */     byte runVal1 = 0, runVal2 = 0, nextVal1 = 0, nextVal2 = 0;
/* 1116 */     byte[] absBuf = new byte[256];
/*      */ 
/*      */     
/* 1119 */     runVal1 = bipixels[++j];
/* 1120 */     runVal2 = bipixels[++j];
/*      */     
/* 1122 */     while (j < scanlineBytes - 2) {
/* 1123 */       nextVal1 = bipixels[++j];
/* 1124 */       nextVal2 = bipixels[++j];
/*      */       
/* 1126 */       if (nextVal1 == runVal1) {
/*      */ 
/*      */         
/* 1129 */         if (absVal >= 4) {
/* 1130 */           this.stream.writeByte(0);
/* 1131 */           this.stream.writeByte(absVal - 1);
/* 1132 */           incCompImageSize(2);
/*      */ 
/*      */           
/* 1135 */           for (int a = 0; a < absVal - 2; a += 2) {
/* 1136 */             pixel = absBuf[a] << 4 | absBuf[a + 1];
/* 1137 */             this.stream.writeByte((byte)pixel);
/* 1138 */             incCompImageSize(1);
/*      */           } 
/*      */           
/* 1141 */           if (!isEven(absVal - 1)) {
/* 1142 */             q = absBuf[absVal - 2] << 4 | 0x0;
/* 1143 */             this.stream.writeByte(q);
/* 1144 */             incCompImageSize(1);
/*      */           } 
/*      */           
/* 1147 */           if (!isEven((int)Math.ceil(((absVal - 1) / 2)))) {
/* 1148 */             this.stream.writeByte(0);
/* 1149 */             incCompImageSize(1);
/*      */           } 
/* 1151 */         } else if (absVal > -1) {
/* 1152 */           this.stream.writeByte(2);
/* 1153 */           pixel = absBuf[0] << 4 | absBuf[1];
/* 1154 */           this.stream.writeByte(pixel);
/* 1155 */           incCompImageSize(2);
/*      */         } 
/* 1157 */         absVal = -1;
/*      */         
/* 1159 */         if (nextVal2 == runVal2) {
/*      */           
/* 1161 */           runCount += 2;
/* 1162 */           if (runCount == 256) {
/* 1163 */             this.stream.writeByte(runCount - 1);
/* 1164 */             pixel = runVal1 << 4 | runVal2;
/* 1165 */             this.stream.writeByte(pixel);
/* 1166 */             incCompImageSize(2);
/* 1167 */             runCount = 2;
/* 1168 */             if (j < scanlineBytes - 1) {
/* 1169 */               runVal1 = runVal2;
/* 1170 */               runVal2 = bipixels[++j];
/*      */             } else {
/* 1172 */               this.stream.writeByte(1);
/* 1173 */               int r = runVal2 << 4 | 0x0;
/* 1174 */               this.stream.writeByte(r);
/* 1175 */               incCompImageSize(2);
/* 1176 */               runCount = -1;
/*      */             }
/*      */           
/*      */           }
/*      */         
/*      */         } else {
/*      */           
/* 1183 */           runCount++;
/* 1184 */           pixel = runVal1 << 4 | runVal2;
/* 1185 */           this.stream.writeByte(runCount);
/* 1186 */           this.stream.writeByte(pixel);
/* 1187 */           incCompImageSize(2);
/* 1188 */           runCount = 2;
/* 1189 */           runVal1 = nextVal2;
/*      */           
/* 1191 */           if (j < scanlineBytes - 1) {
/* 1192 */             runVal2 = bipixels[++j];
/*      */           } else {
/* 1194 */             this.stream.writeByte(1);
/* 1195 */             int r = nextVal2 << 4 | 0x0;
/* 1196 */             this.stream.writeByte(r);
/* 1197 */             incCompImageSize(2);
/* 1198 */             runCount = -1;
/*      */           }
/*      */         
/*      */         } 
/*      */       } else {
/*      */         
/* 1204 */         if (runCount > 2) {
/* 1205 */           pixel = runVal1 << 4 | runVal2;
/* 1206 */           this.stream.writeByte(runCount);
/* 1207 */           this.stream.writeByte(pixel);
/* 1208 */           incCompImageSize(2);
/* 1209 */         } else if (absVal < 0) {
/* 1210 */           absBuf[++absVal] = runVal1;
/* 1211 */           absBuf[++absVal] = runVal2;
/* 1212 */           absBuf[++absVal] = nextVal1;
/* 1213 */           absBuf[++absVal] = nextVal2;
/* 1214 */         } else if (absVal < 253) {
/* 1215 */           absBuf[++absVal] = nextVal1;
/* 1216 */           absBuf[++absVal] = nextVal2;
/*      */         } else {
/* 1218 */           this.stream.writeByte(0);
/* 1219 */           this.stream.writeByte(absVal + 1);
/* 1220 */           incCompImageSize(2);
/* 1221 */           for (int a = 0; a < absVal; a += 2) {
/* 1222 */             pixel = absBuf[a] << 4 | absBuf[a + 1];
/* 1223 */             this.stream.writeByte((byte)pixel);
/* 1224 */             incCompImageSize(1);
/*      */           } 
/*      */ 
/*      */           
/* 1228 */           this.stream.writeByte(0);
/* 1229 */           incCompImageSize(1);
/* 1230 */           absVal = -1;
/*      */         } 
/*      */         
/* 1233 */         runVal1 = nextVal1;
/* 1234 */         runVal2 = nextVal2;
/* 1235 */         runCount = 2;
/*      */       } 
/*      */       
/* 1238 */       if (j >= scanlineBytes - 2) {
/* 1239 */         if (absVal == -1 && runCount >= 2) {
/* 1240 */           if (j == scanlineBytes - 2) {
/* 1241 */             if (bipixels[++j] == runVal1) {
/* 1242 */               runCount++;
/* 1243 */               pixel = runVal1 << 4 | runVal2;
/* 1244 */               this.stream.writeByte(runCount);
/* 1245 */               this.stream.writeByte(pixel);
/* 1246 */               incCompImageSize(2);
/*      */             } else {
/* 1248 */               pixel = runVal1 << 4 | runVal2;
/* 1249 */               this.stream.writeByte(runCount);
/* 1250 */               this.stream.writeByte(pixel);
/* 1251 */               this.stream.writeByte(1);
/* 1252 */               pixel = bipixels[j] << 4 | 0x0;
/* 1253 */               this.stream.writeByte(pixel);
/* 1254 */               int n = bipixels[j] << 4 | 0x0;
/* 1255 */               incCompImageSize(4);
/*      */             } 
/*      */           } else {
/* 1258 */             this.stream.writeByte(runCount);
/* 1259 */             pixel = runVal1 << 4 | runVal2;
/* 1260 */             this.stream.writeByte(pixel);
/* 1261 */             incCompImageSize(2);
/*      */           } 
/* 1263 */         } else if (absVal > -1) {
/* 1264 */           if (j == scanlineBytes - 2) {
/* 1265 */             absBuf[++absVal] = bipixels[++j];
/*      */           }
/* 1267 */           if (absVal >= 2) {
/* 1268 */             this.stream.writeByte(0);
/* 1269 */             this.stream.writeByte(absVal + 1);
/* 1270 */             incCompImageSize(2);
/* 1271 */             for (int a = 0; a < absVal; a += 2) {
/* 1272 */               pixel = absBuf[a] << 4 | absBuf[a + 1];
/* 1273 */               this.stream.writeByte((byte)pixel);
/* 1274 */               incCompImageSize(1);
/*      */             } 
/* 1276 */             if (!isEven(absVal + 1)) {
/* 1277 */               q = absBuf[absVal] << 4 | 0x0;
/* 1278 */               this.stream.writeByte(q);
/* 1279 */               incCompImageSize(1);
/*      */             } 
/*      */ 
/*      */             
/* 1283 */             if (!isEven((int)Math.ceil(((absVal + 1) / 2)))) {
/* 1284 */               this.stream.writeByte(0);
/* 1285 */               incCompImageSize(1);
/*      */             } 
/*      */           } else {
/*      */             int n;
/* 1289 */             switch (absVal) {
/*      */               case 0:
/* 1291 */                 this.stream.writeByte(1);
/* 1292 */                 n = absBuf[0] << 4 | 0x0;
/* 1293 */                 this.stream.writeByte(n);
/* 1294 */                 incCompImageSize(2);
/*      */                 break;
/*      */               case 1:
/* 1297 */                 this.stream.writeByte(2);
/* 1298 */                 pixel = absBuf[0] << 4 | absBuf[1];
/* 1299 */                 this.stream.writeByte(pixel);
/* 1300 */                 incCompImageSize(2);
/*      */                 break;
/*      */             } 
/*      */           
/*      */           } 
/*      */         } 
/* 1306 */         this.stream.writeByte(0);
/* 1307 */         this.stream.writeByte(0);
/* 1308 */         incCompImageSize(2);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private synchronized void incCompImageSize(int value) {
/* 1315 */     this.compImageSize += value;
/*      */   }
/*      */   
/*      */   private boolean isEven(int number) {
/* 1319 */     return (number % 2 == 0);
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeFileHeader(int fileSize, int offset) throws IOException {
/* 1324 */     this.stream.writeByte(66);
/* 1325 */     this.stream.writeByte(77);
/*      */ 
/*      */     
/* 1328 */     this.stream.writeInt(fileSize);
/*      */ 
/*      */     
/* 1331 */     this.stream.writeInt(0);
/*      */ 
/*      */     
/* 1334 */     this.stream.writeInt(offset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeInfoHeader(int headerSize, int bitsPerPixel) throws IOException {
/* 1341 */     this.stream.writeInt(headerSize);
/*      */ 
/*      */     
/* 1344 */     this.stream.writeInt(this.w);
/*      */ 
/*      */     
/* 1347 */     if (this.isTopDown == true) {
/* 1348 */       this.stream.writeInt(-this.h);
/*      */     } else {
/* 1350 */       this.stream.writeInt(this.h);
/*      */     } 
/*      */     
/* 1353 */     this.stream.writeShort(1);
/*      */ 
/*      */     
/* 1356 */     this.stream.writeShort(bitsPerPixel);
/*      */   }
/*      */   
/*      */   private void writeSize(int dword, int offset) throws IOException {
/* 1360 */     this.stream.skipBytes(offset);
/* 1361 */     this.stream.writeInt(dword);
/*      */   }
/*      */   
/*      */   public void reset() {
/* 1365 */     super.reset();
/* 1366 */     this.stream = null;
/*      */   }
/*      */   
/*      */   static int getCompressionType(String typeString) {
/* 1370 */     for (int i = 0; i < BMPConstants.compressionTypeNames.length; i++) {
/* 1371 */       if (BMPConstants.compressionTypeNames[i].equals(typeString))
/* 1372 */         return i; 
/* 1373 */     }  return 0;
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeEmbedded(IIOImage image, ImageWriteParam bmpParam) throws IOException {
/* 1378 */     String format = (this.compressionType == 4) ? "jpeg" : "png";
/*      */     
/* 1380 */     Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName(format);
/* 1381 */     ImageWriter writer = null;
/* 1382 */     if (iterator.hasNext())
/* 1383 */       writer = iterator.next(); 
/* 1384 */     if (writer != null) {
/* 1385 */       if (this.embedded_stream == null) {
/* 1386 */         throw new RuntimeException("No stream for writing embedded image!");
/*      */       }
/*      */       
/* 1389 */       writer.addIIOWriteProgressListener(new IIOWriteProgressAdapter() {
/*      */             public void imageProgress(ImageWriter source, float percentageDone) {
/* 1391 */               BMPImageWriter.this.processImageProgress(percentageDone);
/*      */             }
/*      */           });
/*      */       
/* 1395 */       writer.addIIOWriteWarningListener(new IIOWriteWarningListener() {
/*      */             public void warningOccurred(ImageWriter source, int imageIndex, String warning) {
/* 1397 */               BMPImageWriter.this.processWarningOccurred(imageIndex, warning);
/*      */             }
/*      */           });
/*      */ 
/*      */       
/* 1402 */       ImageOutputStream emb_ios = ImageIO.createImageOutputStream(this.embedded_stream);
/* 1403 */       writer.setOutput(emb_ios);
/* 1404 */       ImageWriteParam param = writer.getDefaultWriteParam();
/*      */       
/* 1406 */       param.setDestinationOffset(bmpParam.getDestinationOffset());
/* 1407 */       param.setSourceBands(bmpParam.getSourceBands());
/* 1408 */       param.setSourceRegion(bmpParam.getSourceRegion());
/* 1409 */       param.setSourceSubsampling(bmpParam.getSourceXSubsampling(), bmpParam
/* 1410 */           .getSourceYSubsampling(), bmpParam
/* 1411 */           .getSubsamplingXOffset(), bmpParam
/* 1412 */           .getSubsamplingYOffset());
/* 1413 */       writer.write((IIOMetadata)null, image, param);
/* 1414 */       emb_ios.flush();
/*      */     } else {
/* 1416 */       throw new RuntimeException(I18N.getString("BMPImageWrite5") + " " + format);
/*      */     } 
/*      */   }
/*      */   
/*      */   private int firstLowBit(int num) {
/* 1421 */     int count = 0;
/* 1422 */     while ((num & 0x1) == 0) {
/* 1423 */       count++;
/* 1424 */       num >>>= 1;
/*      */     } 
/* 1426 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private class IIOWriteProgressAdapter
/*      */     implements IIOWriteProgressListener
/*      */   {
/*      */     private IIOWriteProgressAdapter() {}
/*      */ 
/*      */ 
/*      */     
/*      */     public void imageComplete(ImageWriter source) {}
/*      */ 
/*      */ 
/*      */     
/*      */     public void imageProgress(ImageWriter source, float percentageDone) {}
/*      */ 
/*      */ 
/*      */     
/*      */     public void imageStarted(ImageWriter source, int imageIndex) {}
/*      */ 
/*      */ 
/*      */     
/*      */     public void thumbnailComplete(ImageWriter source) {}
/*      */ 
/*      */ 
/*      */     
/*      */     public void thumbnailProgress(ImageWriter source, float percentageDone) {}
/*      */ 
/*      */     
/*      */     public void thumbnailStarted(ImageWriter source, int imageIndex, int thumbnailIndex) {}
/*      */ 
/*      */     
/*      */     public void writeAborted(ImageWriter source) {}
/*      */   }
/*      */ 
/*      */   
/*      */   static int getPreferredCompressionType(ColorModel cm, SampleModel sm) {
/* 1465 */     ImageTypeSpecifier imageType = new ImageTypeSpecifier(cm, sm);
/* 1466 */     return getPreferredCompressionType(imageType);
/*      */   }
/*      */   
/*      */   static int getPreferredCompressionType(ImageTypeSpecifier imageType) {
/* 1470 */     int biType = imageType.getBufferedImageType();
/* 1471 */     if (biType == 8 || biType == 9)
/*      */     {
/* 1473 */       return 3;
/*      */     }
/* 1475 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean canEncodeImage(int compression, ColorModel cm, SampleModel sm) {
/* 1487 */     ImageTypeSpecifier imgType = new ImageTypeSpecifier(cm, sm);
/* 1488 */     return canEncodeImage(compression, imgType);
/*      */   }
/*      */   
/*      */   protected boolean canEncodeImage(int compression, ImageTypeSpecifier imgType) {
/* 1492 */     ImageWriterSpi spi = getOriginatingProvider();
/* 1493 */     if (!spi.canEncodeImage(imgType)) {
/* 1494 */       return false;
/*      */     }
/* 1496 */     int bpp = imgType.getColorModel().getPixelSize();
/* 1497 */     if (this.compressionType == 2 && bpp != 4)
/*      */     {
/* 1499 */       return false;
/*      */     }
/* 1501 */     if (this.compressionType == 1 && bpp != 8)
/*      */     {
/* 1503 */       return false;
/*      */     }
/* 1505 */     if (bpp == 16) {
/*      */       int i, j;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1532 */       boolean canUseRGB = false;
/* 1533 */       boolean canUseBITFIELDS = false;
/*      */       
/* 1535 */       SampleModel sm = imgType.getSampleModel();
/* 1536 */       if (sm instanceof SinglePixelPackedSampleModel) {
/*      */         
/* 1538 */         int[] sizes = ((SinglePixelPackedSampleModel)sm).getSampleSize();
/*      */         
/* 1540 */         canUseRGB = true;
/* 1541 */         canUseBITFIELDS = true;
/* 1542 */         for (int k = 0; k < sizes.length; k++) {
/* 1543 */           i = canUseRGB & ((sizes[k] == 5) ? 1 : 0);
/* 1544 */           j = canUseBITFIELDS & ((sizes[k] == 5 || (k == 1 && sizes[k] == 6)) ? 1 : 0);
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 1549 */       return ((this.compressionType == 0 && i != 0) || (this.compressionType == 3 && j != 0));
/*      */     } 
/*      */     
/* 1552 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void writeMaskToPalette(int mask, int i, byte[] r, byte[] g, byte[] b, byte[] a) {
/* 1557 */     b[i] = (byte)(0xFF & mask >> 24);
/* 1558 */     g[i] = (byte)(0xFF & mask >> 16);
/* 1559 */     r[i] = (byte)(0xFF & mask >> 8);
/* 1560 */     a[i] = (byte)(0xFF & mask);
/*      */   }
/*      */   
/*      */   private int roundBpp(int x) {
/* 1564 */     if (x <= 8)
/* 1565 */       return 8; 
/* 1566 */     if (x <= 16)
/* 1567 */       return 16; 
/* 1568 */     if (x <= 24) {
/* 1569 */       return 24;
/*      */     }
/* 1571 */     return 32;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\bmp\BMPImageWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */