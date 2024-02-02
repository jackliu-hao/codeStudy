/*      */ package com.github.jaiimageio.impl.common;
/*      */ 
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.color.ColorSpace;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ComponentColorModel;
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
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import javax.imageio.IIOException;
/*      */ import javax.imageio.ImageReadParam;
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ import javax.imageio.ImageWriter;
/*      */ import javax.imageio.spi.IIORegistry;
/*      */ import javax.imageio.spi.ImageReaderSpi;
/*      */ import javax.imageio.spi.ImageReaderWriterSpi;
/*      */ import javax.imageio.spi.ImageWriterSpi;
/*      */ import javax.imageio.spi.ServiceRegistry;
/*      */ import javax.imageio.stream.ImageInputStream;
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
/*      */ public class ImageUtil
/*      */ {
/*      */   public static final ColorModel createColorModel(SampleModel sampleModel) {
/*  167 */     if (sampleModel == null) {
/*  168 */       throw new IllegalArgumentException("sampleModel == null!");
/*      */     }
/*      */ 
/*      */     
/*  172 */     int dataType = sampleModel.getDataType();
/*      */ 
/*      */     
/*  175 */     switch (dataType) {
/*      */       case 0:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */         break;
/*      */       
/*      */       default:
/*  185 */         return null;
/*      */     } 
/*      */ 
/*      */     
/*  189 */     ColorModel colorModel = null;
/*      */ 
/*      */     
/*  192 */     int[] sampleSize = sampleModel.getSampleSize();
/*      */ 
/*      */     
/*  195 */     if (sampleModel instanceof ComponentSampleModel) {
/*      */       
/*  197 */       int numBands = sampleModel.getNumBands();
/*      */ 
/*      */       
/*  200 */       ColorSpace colorSpace = null;
/*  201 */       if (numBands <= 2) {
/*  202 */         colorSpace = ColorSpace.getInstance(1003);
/*  203 */       } else if (numBands <= 4) {
/*  204 */         colorSpace = ColorSpace.getInstance(1000);
/*      */       } else {
/*  206 */         colorSpace = new BogusColorSpace(numBands);
/*      */       } 
/*      */       
/*  209 */       boolean hasAlpha = (numBands == 2 || numBands == 4);
/*  210 */       boolean isAlphaPremultiplied = false;
/*  211 */       int transparency = hasAlpha ? 3 : 1;
/*      */ 
/*      */       
/*  214 */       colorModel = new ComponentColorModel(colorSpace, sampleSize, hasAlpha, isAlphaPremultiplied, transparency, dataType);
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  220 */       if (sampleModel.getNumBands() <= 4 && sampleModel instanceof SinglePixelPackedSampleModel) {
/*      */         
/*  222 */         SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sampleModel;
/*      */ 
/*      */         
/*  225 */         int[] bitMasks = sppsm.getBitMasks();
/*  226 */         int rmask = 0;
/*  227 */         int gmask = 0;
/*  228 */         int bmask = 0;
/*  229 */         int amask = 0;
/*      */         
/*  231 */         int numBands = bitMasks.length;
/*  232 */         if (numBands <= 2) {
/*  233 */           rmask = gmask = bmask = bitMasks[0];
/*  234 */           if (numBands == 2) {
/*  235 */             amask = bitMasks[1];
/*      */           }
/*      */         } else {
/*  238 */           rmask = bitMasks[0];
/*  239 */           gmask = bitMasks[1];
/*  240 */           bmask = bitMasks[2];
/*  241 */           if (numBands == 4) {
/*  242 */             amask = bitMasks[3];
/*      */           }
/*      */         } 
/*      */         
/*  246 */         int bits = 0;
/*  247 */         for (int i = 0; i < sampleSize.length; i++) {
/*  248 */           bits += sampleSize[i];
/*      */         }
/*      */         
/*  251 */         return new DirectColorModel(bits, rmask, gmask, bmask, amask);
/*      */       } 
/*  253 */       if (sampleModel instanceof MultiPixelPackedSampleModel) {
/*      */         
/*  255 */         int bitsPerSample = sampleSize[0];
/*  256 */         int numEntries = 1 << bitsPerSample;
/*  257 */         byte[] map = new byte[numEntries];
/*  258 */         for (int i = 0; i < numEntries; i++) {
/*  259 */           map[i] = (byte)(i * 255 / (numEntries - 1));
/*      */         }
/*      */         
/*  262 */         colorModel = new IndexColorModel(bitsPerSample, numEntries, map, map, map);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  267 */     return colorModel;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] getPackedBinaryData(Raster raster, Rectangle rect) {
/*  287 */     SampleModel sm = raster.getSampleModel();
/*  288 */     if (!isBinary(sm)) {
/*  289 */       throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
/*      */     }
/*      */     
/*  292 */     int rectX = rect.x;
/*  293 */     int rectY = rect.y;
/*  294 */     int rectWidth = rect.width;
/*  295 */     int rectHeight = rect.height;
/*      */     
/*  297 */     DataBuffer dataBuffer = raster.getDataBuffer();
/*      */     
/*  299 */     int dx = rectX - raster.getSampleModelTranslateX();
/*  300 */     int dy = rectY - raster.getSampleModelTranslateY();
/*      */     
/*  302 */     MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
/*  303 */     int lineStride = mpp.getScanlineStride();
/*  304 */     int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
/*  305 */     int bitOffset = mpp.getBitOffset(dx);
/*      */     
/*  307 */     int numBytesPerRow = (rectWidth + 7) / 8;
/*  308 */     if (dataBuffer instanceof DataBufferByte && eltOffset == 0 && bitOffset == 0 && numBytesPerRow == lineStride && (((DataBufferByte)dataBuffer)
/*      */ 
/*      */       
/*  311 */       .getData()).length == numBytesPerRow * rectHeight)
/*      */     {
/*  313 */       return ((DataBufferByte)dataBuffer).getData();
/*      */     }
/*      */     
/*  316 */     byte[] binaryDataArray = new byte[numBytesPerRow * rectHeight];
/*      */     
/*  318 */     int b = 0;
/*      */     
/*  320 */     if (bitOffset == 0) {
/*  321 */       if (dataBuffer instanceof DataBufferByte) {
/*  322 */         byte[] data = ((DataBufferByte)dataBuffer).getData();
/*  323 */         int stride = numBytesPerRow;
/*  324 */         int offset = 0;
/*  325 */         for (int y = 0; y < rectHeight; y++) {
/*  326 */           System.arraycopy(data, eltOffset, binaryDataArray, offset, stride);
/*      */ 
/*      */           
/*  329 */           offset += stride;
/*  330 */           eltOffset += lineStride;
/*      */         } 
/*  332 */       } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
/*      */ 
/*      */ 
/*      */         
/*  336 */         short[] data = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
/*      */         
/*  338 */         for (int y = 0; y < rectHeight; y++) {
/*  339 */           int xRemaining = rectWidth;
/*  340 */           int i = eltOffset;
/*  341 */           while (xRemaining > 8) {
/*  342 */             short datum = data[i++];
/*  343 */             binaryDataArray[b++] = (byte)(datum >>> 8 & 0xFF);
/*  344 */             binaryDataArray[b++] = (byte)(datum & 0xFF);
/*  345 */             xRemaining -= 16;
/*      */           } 
/*  347 */           if (xRemaining > 0) {
/*  348 */             binaryDataArray[b++] = (byte)(data[i] >>> 8 & 0xFF);
/*      */           }
/*  350 */           eltOffset += lineStride;
/*      */         } 
/*  352 */       } else if (dataBuffer instanceof DataBufferInt) {
/*  353 */         int[] data = ((DataBufferInt)dataBuffer).getData();
/*      */         
/*  355 */         for (int y = 0; y < rectHeight; y++) {
/*  356 */           int xRemaining = rectWidth;
/*  357 */           int i = eltOffset;
/*  358 */           while (xRemaining > 24) {
/*  359 */             int datum = data[i++];
/*  360 */             binaryDataArray[b++] = (byte)(datum >>> 24 & 0xFF);
/*  361 */             binaryDataArray[b++] = (byte)(datum >>> 16 & 0xFF);
/*  362 */             binaryDataArray[b++] = (byte)(datum >>> 8 & 0xFF);
/*  363 */             binaryDataArray[b++] = (byte)(datum & 0xFF);
/*  364 */             xRemaining -= 32;
/*      */           } 
/*  366 */           int shift = 24;
/*  367 */           while (xRemaining > 0) {
/*  368 */             binaryDataArray[b++] = (byte)(data[i] >>> shift & 0xFF);
/*      */             
/*  370 */             shift -= 8;
/*  371 */             xRemaining -= 8;
/*      */           } 
/*  373 */           eltOffset += lineStride;
/*      */         }
/*      */       
/*      */       } 
/*  377 */     } else if (dataBuffer instanceof DataBufferByte) {
/*  378 */       byte[] data = ((DataBufferByte)dataBuffer).getData();
/*      */       
/*  380 */       if ((bitOffset & 0x7) == 0) {
/*  381 */         int stride = numBytesPerRow;
/*  382 */         int offset = 0;
/*  383 */         for (int y = 0; y < rectHeight; y++) {
/*  384 */           System.arraycopy(data, eltOffset, binaryDataArray, offset, stride);
/*      */ 
/*      */           
/*  387 */           offset += stride;
/*  388 */           eltOffset += lineStride;
/*      */         } 
/*      */       } else {
/*  391 */         int leftShift = bitOffset & 0x7;
/*  392 */         int rightShift = 8 - leftShift;
/*  393 */         for (int y = 0; y < rectHeight; y++) {
/*  394 */           int i = eltOffset;
/*  395 */           int xRemaining = rectWidth;
/*  396 */           while (xRemaining > 0) {
/*  397 */             if (xRemaining > rightShift) {
/*  398 */               binaryDataArray[b++] = (byte)((data[i++] & 0xFF) << leftShift | (data[i] & 0xFF) >>> rightShift);
/*      */             }
/*      */             else {
/*      */               
/*  402 */               binaryDataArray[b++] = (byte)((data[i] & 0xFF) << leftShift);
/*      */             } 
/*      */             
/*  405 */             xRemaining -= 8;
/*      */           } 
/*  407 */           eltOffset += lineStride;
/*      */         } 
/*      */       } 
/*  410 */     } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
/*      */ 
/*      */ 
/*      */       
/*  414 */       short[] data = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
/*      */       
/*  416 */       for (int y = 0; y < rectHeight; y++) {
/*  417 */         int bOffset = bitOffset;
/*  418 */         for (int x = 0; x < rectWidth; x += 8, bOffset += 8) {
/*  419 */           int i = eltOffset + bOffset / 16;
/*  420 */           int mod = bOffset % 16;
/*  421 */           int left = data[i] & 0xFFFF;
/*  422 */           if (mod <= 8) {
/*  423 */             binaryDataArray[b++] = (byte)(left >>> 8 - mod);
/*      */           } else {
/*  425 */             int delta = mod - 8;
/*  426 */             int right = data[i + 1] & 0xFFFF;
/*  427 */             binaryDataArray[b++] = (byte)(left << delta | right >>> 16 - delta);
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/*  432 */         eltOffset += lineStride;
/*      */       } 
/*  434 */     } else if (dataBuffer instanceof DataBufferInt) {
/*  435 */       int[] data = ((DataBufferInt)dataBuffer).getData();
/*      */       
/*  437 */       for (int y = 0; y < rectHeight; y++) {
/*  438 */         int bOffset = bitOffset;
/*  439 */         for (int x = 0; x < rectWidth; x += 8, bOffset += 8) {
/*  440 */           int i = eltOffset + bOffset / 32;
/*  441 */           int mod = bOffset % 32;
/*  442 */           int left = data[i];
/*  443 */           if (mod <= 24) {
/*  444 */             binaryDataArray[b++] = (byte)(left >>> 24 - mod);
/*      */           } else {
/*      */             
/*  447 */             int delta = mod - 24;
/*  448 */             int right = data[i + 1];
/*  449 */             binaryDataArray[b++] = (byte)(left << delta | right >>> 32 - delta);
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/*  454 */         eltOffset += lineStride;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  459 */     return binaryDataArray;
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
/*      */   
/*      */   public static byte[] getUnpackedBinaryData(Raster raster, Rectangle rect) {
/*  472 */     SampleModel sm = raster.getSampleModel();
/*  473 */     if (!isBinary(sm)) {
/*  474 */       throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
/*      */     }
/*      */     
/*  477 */     int rectX = rect.x;
/*  478 */     int rectY = rect.y;
/*  479 */     int rectWidth = rect.width;
/*  480 */     int rectHeight = rect.height;
/*      */     
/*  482 */     DataBuffer dataBuffer = raster.getDataBuffer();
/*      */     
/*  484 */     int dx = rectX - raster.getSampleModelTranslateX();
/*  485 */     int dy = rectY - raster.getSampleModelTranslateY();
/*      */     
/*  487 */     MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
/*  488 */     int lineStride = mpp.getScanlineStride();
/*  489 */     int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
/*  490 */     int bitOffset = mpp.getBitOffset(dx);
/*      */     
/*  492 */     byte[] bdata = new byte[rectWidth * rectHeight];
/*  493 */     int maxY = rectY + rectHeight;
/*  494 */     int maxX = rectX + rectWidth;
/*  495 */     int k = 0;
/*      */     
/*  497 */     if (dataBuffer instanceof DataBufferByte) {
/*  498 */       byte[] data = ((DataBufferByte)dataBuffer).getData();
/*  499 */       for (int y = rectY; y < maxY; y++) {
/*  500 */         int bOffset = eltOffset * 8 + bitOffset;
/*  501 */         for (int x = rectX; x < maxX; x++) {
/*  502 */           byte b = data[bOffset / 8];
/*  503 */           bdata[k++] = (byte)(b >>> (7 - bOffset & 0x7) & 0x1);
/*      */           
/*  505 */           bOffset++;
/*      */         } 
/*  507 */         eltOffset += lineStride;
/*      */       } 
/*  509 */     } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
/*      */ 
/*      */ 
/*      */       
/*  513 */       short[] data = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
/*  514 */       for (int y = rectY; y < maxY; y++) {
/*  515 */         int bOffset = eltOffset * 16 + bitOffset;
/*  516 */         for (int x = rectX; x < maxX; x++) {
/*  517 */           short s = data[bOffset / 16];
/*  518 */           bdata[k++] = (byte)(s >>> 15 - bOffset % 16 & 0x1);
/*      */ 
/*      */           
/*  521 */           bOffset++;
/*      */         } 
/*  523 */         eltOffset += lineStride;
/*      */       } 
/*  525 */     } else if (dataBuffer instanceof DataBufferInt) {
/*  526 */       int[] data = ((DataBufferInt)dataBuffer).getData();
/*  527 */       for (int y = rectY; y < maxY; y++) {
/*  528 */         int bOffset = eltOffset * 32 + bitOffset;
/*  529 */         for (int x = rectX; x < maxX; x++) {
/*  530 */           int i = data[bOffset / 32];
/*  531 */           bdata[k++] = (byte)(i >>> 31 - bOffset % 32 & 0x1);
/*      */ 
/*      */           
/*  534 */           bOffset++;
/*      */         } 
/*  536 */         eltOffset += lineStride;
/*      */       } 
/*      */     } 
/*      */     
/*  540 */     return bdata;
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
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setPackedBinaryData(byte[] binaryDataArray, WritableRaster raster, Rectangle rect) {
/*  555 */     SampleModel sm = raster.getSampleModel();
/*  556 */     if (!isBinary(sm)) {
/*  557 */       throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
/*      */     }
/*      */     
/*  560 */     int rectX = rect.x;
/*  561 */     int rectY = rect.y;
/*  562 */     int rectWidth = rect.width;
/*  563 */     int rectHeight = rect.height;
/*      */     
/*  565 */     DataBuffer dataBuffer = raster.getDataBuffer();
/*      */     
/*  567 */     int dx = rectX - raster.getSampleModelTranslateX();
/*  568 */     int dy = rectY - raster.getSampleModelTranslateY();
/*      */     
/*  570 */     MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
/*  571 */     int lineStride = mpp.getScanlineStride();
/*  572 */     int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
/*  573 */     int bitOffset = mpp.getBitOffset(dx);
/*      */     
/*  575 */     int b = 0;
/*      */     
/*  577 */     if (bitOffset == 0) {
/*  578 */       if (dataBuffer instanceof DataBufferByte) {
/*  579 */         byte[] data = ((DataBufferByte)dataBuffer).getData();
/*  580 */         if (data == binaryDataArray) {
/*      */           return;
/*      */         }
/*      */         
/*  584 */         int stride = (rectWidth + 7) / 8;
/*  585 */         int offset = 0;
/*  586 */         for (int y = 0; y < rectHeight; y++) {
/*  587 */           System.arraycopy(binaryDataArray, offset, data, eltOffset, stride);
/*      */ 
/*      */           
/*  590 */           offset += stride;
/*  591 */           eltOffset += lineStride;
/*      */         } 
/*  593 */       } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
/*      */ 
/*      */ 
/*      */         
/*  597 */         short[] data = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
/*      */         
/*  599 */         for (int y = 0; y < rectHeight; y++) {
/*  600 */           int xRemaining = rectWidth;
/*  601 */           int i = eltOffset;
/*  602 */           while (xRemaining > 8) {
/*  603 */             data[i++] = (short)((binaryDataArray[b++] & 0xFF) << 8 | binaryDataArray[b++] & 0xFF);
/*      */ 
/*      */             
/*  606 */             xRemaining -= 16;
/*      */           } 
/*  608 */           if (xRemaining > 0) {
/*  609 */             data[i++] = (short)((binaryDataArray[b++] & 0xFF) << 8);
/*      */           }
/*      */           
/*  612 */           eltOffset += lineStride;
/*      */         } 
/*  614 */       } else if (dataBuffer instanceof DataBufferInt) {
/*  615 */         int[] data = ((DataBufferInt)dataBuffer).getData();
/*      */         
/*  617 */         for (int y = 0; y < rectHeight; y++) {
/*  618 */           int xRemaining = rectWidth;
/*  619 */           int i = eltOffset;
/*  620 */           while (xRemaining > 24) {
/*  621 */             data[i++] = (binaryDataArray[b++] & 0xFF) << 24 | (binaryDataArray[b++] & 0xFF) << 16 | (binaryDataArray[b++] & 0xFF) << 8 | binaryDataArray[b++] & 0xFF;
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  626 */             xRemaining -= 32;
/*      */           } 
/*  628 */           int shift = 24;
/*  629 */           while (xRemaining > 0) {
/*  630 */             data[i] = data[i] | (binaryDataArray[b++] & 0xFF) << shift;
/*      */             
/*  632 */             shift -= 8;
/*  633 */             xRemaining -= 8;
/*      */           } 
/*  635 */           eltOffset += lineStride;
/*      */         } 
/*      */       } 
/*      */     } else {
/*  639 */       int stride = (rectWidth + 7) / 8;
/*  640 */       int offset = 0;
/*  641 */       if (dataBuffer instanceof DataBufferByte) {
/*  642 */         byte[] data = ((DataBufferByte)dataBuffer).getData();
/*      */         
/*  644 */         if ((bitOffset & 0x7) == 0) {
/*  645 */           for (int y = 0; y < rectHeight; y++) {
/*  646 */             System.arraycopy(binaryDataArray, offset, data, eltOffset, stride);
/*      */ 
/*      */             
/*  649 */             offset += stride;
/*  650 */             eltOffset += lineStride;
/*      */           } 
/*      */         } else {
/*  653 */           int rightShift = bitOffset & 0x7;
/*  654 */           int leftShift = 8 - rightShift;
/*  655 */           int leftShift8 = 8 + leftShift;
/*  656 */           int mask = (byte)(255 << leftShift);
/*  657 */           int mask1 = (byte)(mask ^ 0xFFFFFFFF);
/*      */           
/*  659 */           for (int y = 0; y < rectHeight; y++) {
/*  660 */             int i = eltOffset;
/*  661 */             int xRemaining = rectWidth;
/*  662 */             while (xRemaining > 0) {
/*  663 */               byte datum = binaryDataArray[b++];
/*      */               
/*  665 */               if (xRemaining > leftShift8) {
/*      */ 
/*      */                 
/*  668 */                 data[i] = (byte)(data[i] & mask | (datum & 0xFF) >>> rightShift);
/*      */                 
/*  670 */                 data[++i] = (byte)((datum & 0xFF) << leftShift);
/*  671 */               } else if (xRemaining > leftShift) {
/*      */ 
/*      */ 
/*      */                 
/*  675 */                 data[i] = (byte)(data[i] & mask | (datum & 0xFF) >>> rightShift);
/*      */                 
/*  677 */                 i++;
/*  678 */                 data[i] = (byte)(data[i] & mask1 | (datum & 0xFF) << leftShift);
/*      */               
/*      */               }
/*      */               else {
/*      */                 
/*  683 */                 int remainMask = (1 << leftShift - xRemaining) - 1;
/*  684 */                 data[i] = (byte)(data[i] & (mask | remainMask) | (datum & 0xFF) >>> rightShift & (remainMask ^ 0xFFFFFFFF));
/*      */               } 
/*      */ 
/*      */               
/*  688 */               xRemaining -= 8;
/*      */             } 
/*  690 */             eltOffset += lineStride;
/*      */           } 
/*      */         } 
/*  693 */       } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
/*      */ 
/*      */ 
/*      */         
/*  697 */         short[] data = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
/*      */         
/*  699 */         int rightShift = bitOffset & 0x7;
/*  700 */         int leftShift = 8 - rightShift;
/*  701 */         int leftShift16 = 16 + leftShift;
/*  702 */         int mask = (short)(255 << leftShift ^ 0xFFFFFFFF);
/*  703 */         int mask1 = (short)(65535 << leftShift);
/*  704 */         int mask2 = (short)(mask1 ^ 0xFFFFFFFF);
/*      */         
/*  706 */         for (int y = 0; y < rectHeight; y++) {
/*  707 */           int bOffset = bitOffset;
/*  708 */           int xRemaining = rectWidth;
/*  709 */           for (int x = 0; x < rectWidth; 
/*  710 */             x += 8, bOffset += 8, xRemaining -= 8) {
/*  711 */             int i = eltOffset + (bOffset >> 4);
/*  712 */             int mod = bOffset & 0xF;
/*  713 */             int datum = binaryDataArray[b++] & 0xFF;
/*  714 */             if (mod <= 8) {
/*      */               
/*  716 */               if (xRemaining < 8)
/*      */               {
/*  718 */                 datum &= 255 << 8 - xRemaining;
/*      */               }
/*  720 */               data[i] = (short)(data[i] & mask | datum << leftShift);
/*  721 */             } else if (xRemaining > leftShift16) {
/*      */               
/*  723 */               data[i] = (short)(data[i] & mask1 | datum >>> rightShift & 0xFFFF);
/*  724 */               data[++i] = (short)(datum << leftShift & 0xFFFF);
/*      */             }
/*  726 */             else if (xRemaining > leftShift) {
/*      */ 
/*      */               
/*  729 */               data[i] = (short)(data[i] & mask1 | datum >>> rightShift & 0xFFFF);
/*  730 */               i++;
/*  731 */               data[i] = (short)(data[i] & mask2 | datum << leftShift & 0xFFFF);
/*      */             
/*      */             }
/*      */             else {
/*      */               
/*  736 */               int remainMask = (1 << leftShift - xRemaining) - 1;
/*  737 */               data[i] = (short)(data[i] & (mask1 | remainMask) | datum >>> rightShift & 0xFFFF & (remainMask ^ 0xFFFFFFFF));
/*      */             } 
/*      */           } 
/*      */           
/*  741 */           eltOffset += lineStride;
/*      */         } 
/*  743 */       } else if (dataBuffer instanceof DataBufferInt) {
/*  744 */         int[] data = ((DataBufferInt)dataBuffer).getData();
/*  745 */         int rightShift = bitOffset & 0x7;
/*  746 */         int leftShift = 8 - rightShift;
/*  747 */         int leftShift32 = 32 + leftShift;
/*  748 */         int mask = -1 << leftShift;
/*  749 */         int mask1 = mask ^ 0xFFFFFFFF;
/*      */         
/*  751 */         for (int y = 0; y < rectHeight; y++) {
/*  752 */           int bOffset = bitOffset;
/*  753 */           int xRemaining = rectWidth;
/*  754 */           for (int x = 0; x < rectWidth; 
/*  755 */             x += 8, bOffset += 8, xRemaining -= 8) {
/*  756 */             int i = eltOffset + (bOffset >> 5);
/*  757 */             int mod = bOffset & 0x1F;
/*  758 */             int datum = binaryDataArray[b++] & 0xFF;
/*  759 */             if (mod <= 24) {
/*      */               
/*  761 */               int shift = 24 - mod;
/*  762 */               if (xRemaining < 8)
/*      */               {
/*  764 */                 datum &= 255 << 8 - xRemaining;
/*      */               }
/*  766 */               data[i] = data[i] & (255 << shift ^ 0xFFFFFFFF) | datum << shift;
/*  767 */             } else if (xRemaining > leftShift32) {
/*      */               
/*  769 */               data[i] = data[i] & mask | datum >>> rightShift;
/*  770 */               data[++i] = datum << leftShift;
/*  771 */             } else if (xRemaining > leftShift) {
/*      */ 
/*      */               
/*  774 */               data[i] = data[i] & mask | datum >>> rightShift;
/*  775 */               i++;
/*  776 */               data[i] = data[i] & mask1 | datum << leftShift;
/*      */             } else {
/*      */               
/*  779 */               int remainMask = (1 << leftShift - xRemaining) - 1;
/*  780 */               data[i] = data[i] & (mask | remainMask) | datum >>> rightShift & (remainMask ^ 0xFFFFFFFF);
/*      */             } 
/*      */           } 
/*      */           
/*  784 */           eltOffset += lineStride;
/*      */         } 
/*      */       } 
/*      */     } 
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setUnpackedBinaryData(byte[] bdata, WritableRaster raster, Rectangle rect) {
/*  805 */     SampleModel sm = raster.getSampleModel();
/*  806 */     if (!isBinary(sm)) {
/*  807 */       throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
/*      */     }
/*      */     
/*  810 */     int rectX = rect.x;
/*  811 */     int rectY = rect.y;
/*  812 */     int rectWidth = rect.width;
/*  813 */     int rectHeight = rect.height;
/*      */     
/*  815 */     DataBuffer dataBuffer = raster.getDataBuffer();
/*      */     
/*  817 */     int dx = rectX - raster.getSampleModelTranslateX();
/*  818 */     int dy = rectY - raster.getSampleModelTranslateY();
/*      */     
/*  820 */     MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
/*  821 */     int lineStride = mpp.getScanlineStride();
/*  822 */     int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
/*  823 */     int bitOffset = mpp.getBitOffset(dx);
/*      */     
/*  825 */     int k = 0;
/*      */     
/*  827 */     if (dataBuffer instanceof DataBufferByte) {
/*  828 */       byte[] data = ((DataBufferByte)dataBuffer).getData();
/*  829 */       for (int y = 0; y < rectHeight; y++) {
/*  830 */         int bOffset = eltOffset * 8 + bitOffset;
/*  831 */         for (int x = 0; x < rectWidth; x++) {
/*  832 */           if (bdata[k++] != 0) {
/*  833 */             data[bOffset / 8] = (byte)(data[bOffset / 8] | (byte)(1 << (7 - bOffset & 0x7)));
/*      */           }
/*      */           
/*  836 */           bOffset++;
/*      */         } 
/*  838 */         eltOffset += lineStride;
/*      */       } 
/*  840 */     } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
/*      */ 
/*      */ 
/*      */       
/*  844 */       short[] data = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
/*  845 */       for (int y = 0; y < rectHeight; y++) {
/*  846 */         int bOffset = eltOffset * 16 + bitOffset;
/*  847 */         for (int x = 0; x < rectWidth; x++) {
/*  848 */           if (bdata[k++] != 0) {
/*  849 */             data[bOffset / 16] = (short)(data[bOffset / 16] | (short)(1 << 15 - bOffset % 16));
/*      */           }
/*      */ 
/*      */           
/*  853 */           bOffset++;
/*      */         } 
/*  855 */         eltOffset += lineStride;
/*      */       } 
/*  857 */     } else if (dataBuffer instanceof DataBufferInt) {
/*  858 */       int[] data = ((DataBufferInt)dataBuffer).getData();
/*  859 */       for (int y = 0; y < rectHeight; y++) {
/*  860 */         int bOffset = eltOffset * 32 + bitOffset;
/*  861 */         for (int x = 0; x < rectWidth; x++) {
/*  862 */           if (bdata[k++] != 0) {
/*  863 */             data[bOffset / 32] = data[bOffset / 32] | 1 << 31 - bOffset % 32;
/*      */           }
/*      */ 
/*      */           
/*  867 */           bOffset++;
/*      */         } 
/*  869 */         eltOffset += lineStride;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public static boolean isBinary(SampleModel sm) {
/*  875 */     return (sm instanceof MultiPixelPackedSampleModel && ((MultiPixelPackedSampleModel)sm)
/*  876 */       .getPixelBitStride() == 1 && sm
/*  877 */       .getNumBands() == 1);
/*      */   }
/*      */ 
/*      */   
/*      */   public static ColorModel createColorModel(ColorSpace colorSpace, SampleModel sampleModel) {
/*  882 */     ColorModel colorModel = null;
/*      */     
/*  884 */     if (sampleModel == null) {
/*  885 */       throw new IllegalArgumentException(I18N.getString("ImageUtil1"));
/*      */     }
/*      */     
/*  888 */     int numBands = sampleModel.getNumBands();
/*  889 */     if (numBands < 1 || numBands > 4) {
/*  890 */       return null;
/*      */     }
/*      */     
/*  893 */     int dataType = sampleModel.getDataType();
/*  894 */     if (sampleModel instanceof ComponentSampleModel) {
/*  895 */       if (dataType < 0 || dataType > 5)
/*      */       {
/*      */         
/*  898 */         return null;
/*      */       }
/*      */       
/*  901 */       if (colorSpace == null)
/*      */       {
/*      */ 
/*      */         
/*  905 */         colorSpace = (numBands <= 2) ? ColorSpace.getInstance(1003) : ColorSpace.getInstance(1000);
/*      */       }
/*  907 */       boolean useAlpha = (numBands == 2 || numBands == 4);
/*  908 */       int transparency = useAlpha ? 3 : 1;
/*      */ 
/*      */       
/*  911 */       boolean premultiplied = false;
/*      */       
/*  913 */       int dataTypeSize = DataBuffer.getDataTypeSize(dataType);
/*  914 */       int[] bits = new int[numBands];
/*  915 */       for (int i = 0; i < numBands; i++) {
/*  916 */         bits[i] = dataTypeSize;
/*      */       }
/*      */       
/*  919 */       colorModel = new ComponentColorModel(colorSpace, bits, useAlpha, premultiplied, transparency, dataType);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*  925 */     else if (sampleModel instanceof SinglePixelPackedSampleModel) {
/*  926 */       SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sampleModel;
/*      */ 
/*      */       
/*  929 */       int[] bitMasks = sppsm.getBitMasks();
/*  930 */       int rmask = 0;
/*  931 */       int gmask = 0;
/*  932 */       int bmask = 0;
/*  933 */       int amask = 0;
/*      */       
/*  935 */       numBands = bitMasks.length;
/*  936 */       if (numBands <= 2) {
/*  937 */         rmask = gmask = bmask = bitMasks[0];
/*  938 */         if (numBands == 2) {
/*  939 */           amask = bitMasks[1];
/*      */         }
/*      */       } else {
/*  942 */         rmask = bitMasks[0];
/*  943 */         gmask = bitMasks[1];
/*  944 */         bmask = bitMasks[2];
/*  945 */         if (numBands == 4) {
/*  946 */           amask = bitMasks[3];
/*      */         }
/*      */       } 
/*      */       
/*  950 */       int[] sampleSize = sppsm.getSampleSize();
/*  951 */       int bits = 0;
/*  952 */       for (int i = 0; i < sampleSize.length; i++) {
/*  953 */         bits += sampleSize[i];
/*      */       }
/*      */       
/*  956 */       if (colorSpace == null) {
/*  957 */         colorSpace = ColorSpace.getInstance(1000);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  963 */       colorModel = new DirectColorModel(colorSpace, bits, rmask, gmask, bmask, amask, false, sampleModel.getDataType());
/*  964 */     } else if (sampleModel instanceof MultiPixelPackedSampleModel) {
/*      */       
/*  966 */       int bits = ((MultiPixelPackedSampleModel)sampleModel).getPixelBitStride();
/*  967 */       int size = 1 << bits;
/*  968 */       byte[] comp = new byte[size];
/*      */       
/*  970 */       for (int i = 0; i < size; i++) {
/*  971 */         comp[i] = (byte)(255 * i / (size - 1));
/*      */       }
/*  973 */       colorModel = new IndexColorModel(bits, size, comp, comp, comp);
/*      */     } 
/*      */     
/*  976 */     return colorModel;
/*      */   }
/*      */   
/*      */   public static int getElementSize(SampleModel sm) {
/*  980 */     int elementSize = DataBuffer.getDataTypeSize(sm.getDataType());
/*      */     
/*  982 */     if (sm instanceof MultiPixelPackedSampleModel) {
/*  983 */       MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sm;
/*      */       
/*  985 */       return mppsm.getSampleSize(0) * mppsm.getNumBands();
/*  986 */     }  if (sm instanceof ComponentSampleModel)
/*  987 */       return sm.getNumBands() * elementSize; 
/*  988 */     if (sm instanceof SinglePixelPackedSampleModel) {
/*  989 */       return elementSize;
/*      */     }
/*      */     
/*  992 */     return elementSize * sm.getNumBands();
/*      */   }
/*      */ 
/*      */   
/*      */   public static long getTileSize(SampleModel sm) {
/*  997 */     int elementSize = DataBuffer.getDataTypeSize(sm.getDataType());
/*      */     
/*  999 */     if (sm instanceof MultiPixelPackedSampleModel) {
/* 1000 */       MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sm;
/*      */ 
/*      */       
/* 1003 */       return ((mppsm.getScanlineStride() * mppsm.getHeight() + (mppsm.getDataBitOffset() + elementSize - 1) / elementSize) * (elementSize + 7) / 8);
/*      */     } 
/* 1005 */     if (sm instanceof ComponentSampleModel) {
/* 1006 */       ComponentSampleModel csm = (ComponentSampleModel)sm;
/* 1007 */       int[] bandOffsets = csm.getBandOffsets();
/* 1008 */       int maxBandOff = bandOffsets[0];
/* 1009 */       for (int i = 1; i < bandOffsets.length; i++) {
/* 1010 */         maxBandOff = Math.max(maxBandOff, bandOffsets[i]);
/*      */       }
/* 1012 */       long size = 0L;
/* 1013 */       int pixelStride = csm.getPixelStride();
/* 1014 */       int scanlineStride = csm.getScanlineStride();
/* 1015 */       if (maxBandOff >= 0)
/* 1016 */         size += (maxBandOff + 1); 
/* 1017 */       if (pixelStride > 0)
/* 1018 */         size += (pixelStride * (sm.getWidth() - 1)); 
/* 1019 */       if (scanlineStride > 0) {
/* 1020 */         size += (scanlineStride * (sm.getHeight() - 1));
/*      */       }
/* 1022 */       int[] bankIndices = csm.getBankIndices();
/* 1023 */       maxBandOff = bankIndices[0];
/* 1024 */       for (int j = 1; j < bankIndices.length; j++)
/* 1025 */         maxBandOff = Math.max(maxBandOff, bankIndices[j]); 
/* 1026 */       return size * (maxBandOff + 1) * ((elementSize + 7) / 8);
/* 1027 */     }  if (sm instanceof SinglePixelPackedSampleModel) {
/* 1028 */       SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
/*      */ 
/*      */       
/* 1031 */       long size = (sppsm.getScanlineStride() * (sppsm.getHeight() - 1) + sppsm.getWidth());
/* 1032 */       return size * ((elementSize + 7) / 8);
/*      */     } 
/*      */     
/* 1035 */     return 0L;
/*      */   }
/*      */   
/*      */   public static long getBandSize(SampleModel sm) {
/* 1039 */     int elementSize = DataBuffer.getDataTypeSize(sm.getDataType());
/*      */     
/* 1041 */     if (sm instanceof ComponentSampleModel) {
/* 1042 */       ComponentSampleModel csm = (ComponentSampleModel)sm;
/* 1043 */       int pixelStride = csm.getPixelStride();
/* 1044 */       int scanlineStride = csm.getScanlineStride();
/* 1045 */       long size = Math.min(pixelStride, scanlineStride);
/*      */       
/* 1047 */       if (pixelStride > 0)
/* 1048 */         size += (pixelStride * (sm.getWidth() - 1)); 
/* 1049 */       if (scanlineStride > 0)
/* 1050 */         size += (scanlineStride * (sm.getHeight() - 1)); 
/* 1051 */       return size * ((elementSize + 7) / 8);
/*      */     } 
/* 1053 */     return getTileSize(sm);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isGrayscaleMapping(IndexColorModel icm) {
/* 1069 */     if (icm == null) {
/* 1070 */       throw new IllegalArgumentException("icm == null!");
/*      */     }
/*      */ 
/*      */     
/* 1074 */     int mapSize = icm.getMapSize();
/*      */     
/* 1076 */     byte[] r = new byte[mapSize];
/* 1077 */     byte[] g = new byte[mapSize];
/* 1078 */     byte[] b = new byte[mapSize];
/*      */     
/* 1080 */     icm.getReds(r);
/* 1081 */     icm.getGreens(g);
/* 1082 */     icm.getBlues(b);
/*      */     
/* 1084 */     boolean isGrayToColor = true;
/*      */     
/*      */     int i;
/* 1087 */     for (i = 0; i < mapSize; i++) {
/* 1088 */       byte temp = (byte)(i * 255 / (mapSize - 1));
/*      */       
/* 1090 */       if (r[i] != temp || g[i] != temp || b[i] != temp) {
/* 1091 */         isGrayToColor = false;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/* 1096 */     if (!isGrayToColor) {
/* 1097 */       isGrayToColor = true;
/*      */       
/*      */       int j;
/* 1100 */       for (i = 0, j = mapSize - 1; i < mapSize; i++, j--) {
/* 1101 */         byte temp = (byte)(j * 255 / (mapSize - 1));
/*      */         
/* 1103 */         if (r[i] != temp || g[i] != temp || b[i] != temp) {
/* 1104 */           isGrayToColor = false;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1110 */     return isGrayToColor;
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
/*      */   
/*      */   public static boolean isIndicesForGrayscale(byte[] r, byte[] g, byte[] b) {
/* 1123 */     if (r.length != g.length || r.length != b.length) {
/* 1124 */       return false;
/*      */     }
/* 1126 */     int size = r.length;
/*      */     
/* 1128 */     if (size != 256) {
/* 1129 */       return false;
/*      */     }
/* 1131 */     for (int i = 0; i < size; i++) {
/* 1132 */       byte temp = (byte)i;
/*      */       
/* 1134 */       if (r[i] != temp || g[i] != temp || b[i] != temp) {
/* 1135 */         return false;
/*      */       }
/*      */     } 
/* 1138 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public static String convertObjectToString(Object obj) {
/* 1143 */     if (obj == null) {
/* 1144 */       return "";
/*      */     }
/* 1146 */     String s = "";
/* 1147 */     if (obj instanceof byte[]) {
/* 1148 */       byte[] bArray = (byte[])obj;
/* 1149 */       for (int i = 0; i < bArray.length; i++)
/* 1150 */         s = s + bArray[i] + " "; 
/* 1151 */       return s;
/*      */     } 
/*      */     
/* 1154 */     if (obj instanceof int[]) {
/* 1155 */       int[] iArray = (int[])obj;
/* 1156 */       for (int i = 0; i < iArray.length; i++)
/* 1157 */         s = s + iArray[i] + " "; 
/* 1158 */       return s;
/*      */     } 
/*      */     
/* 1161 */     if (obj instanceof short[]) {
/* 1162 */       short[] sArray = (short[])obj;
/* 1163 */       for (int i = 0; i < sArray.length; i++)
/* 1164 */         s = s + sArray[i] + " "; 
/* 1165 */       return s;
/*      */     } 
/*      */     
/* 1168 */     return obj.toString();
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
/*      */ 
/*      */   
/*      */   public static final void canEncodeImage(ImageWriter writer, ImageTypeSpecifier type) throws IIOException {
/* 1182 */     ImageWriterSpi spi = writer.getOriginatingProvider();
/*      */     
/* 1184 */     if (type != null && spi != null && !spi.canEncodeImage(type)) {
/* 1185 */       throw new IIOException(I18N.getString("ImageUtil2") + " " + writer
/* 1186 */           .getClass().getName());
/*      */     }
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
/*      */ 
/*      */ 
/*      */   
/*      */   public static final void canEncodeImage(ImageWriter writer, ColorModel colorModel, SampleModel sampleModel) throws IIOException {
/* 1202 */     ImageTypeSpecifier type = null;
/* 1203 */     if (colorModel != null && sampleModel != null)
/* 1204 */       type = new ImageTypeSpecifier(colorModel, sampleModel); 
/* 1205 */     canEncodeImage(writer, type);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final boolean imageIsContiguous(RenderedImage image) {
/*      */     SampleModel sm;
/* 1213 */     if (image instanceof BufferedImage) {
/* 1214 */       WritableRaster ras = ((BufferedImage)image).getRaster();
/* 1215 */       sm = ras.getSampleModel();
/*      */     } else {
/* 1217 */       sm = image.getSampleModel();
/*      */     } 
/*      */     
/* 1220 */     if (sm instanceof ComponentSampleModel) {
/*      */ 
/*      */       
/* 1223 */       ComponentSampleModel csm = (ComponentSampleModel)sm;
/*      */       
/* 1225 */       if (csm.getPixelStride() != csm.getNumBands()) {
/* 1226 */         return false;
/*      */       }
/*      */       
/* 1229 */       int[] bandOffsets = csm.getBandOffsets();
/* 1230 */       for (int i = 0; i < bandOffsets.length; i++) {
/* 1231 */         if (bandOffsets[i] != i) {
/* 1232 */           return false;
/*      */         }
/*      */       } 
/*      */       
/* 1236 */       int[] bankIndices = csm.getBankIndices();
/* 1237 */       for (int j = 0; j < bandOffsets.length; j++) {
/* 1238 */         if (bankIndices[j] != 0) {
/* 1239 */           return false;
/*      */         }
/*      */       } 
/*      */       
/* 1243 */       return true;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1249 */     return isBinary(sm);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final ImageTypeSpecifier getDestinationType(ImageReadParam param, Iterator<ImageTypeSpecifier> imageTypes) throws IIOException {
/* 1260 */     if (imageTypes == null || !imageTypes.hasNext()) {
/* 1261 */       throw new IllegalArgumentException("imageTypes null or empty!");
/*      */     }
/*      */     
/* 1264 */     ImageTypeSpecifier imageType = null;
/*      */ 
/*      */     
/* 1267 */     if (param != null) {
/* 1268 */       imageType = param.getDestinationType();
/*      */     }
/*      */ 
/*      */     
/* 1272 */     if (imageType == null) {
/* 1273 */       Object o = imageTypes.next();
/* 1274 */       if (!(o instanceof ImageTypeSpecifier)) {
/* 1275 */         throw new IllegalArgumentException("Non-ImageTypeSpecifier retrieved from imageTypes!");
/*      */       }
/*      */       
/* 1278 */       imageType = (ImageTypeSpecifier)o;
/*      */     } else {
/* 1280 */       boolean foundIt = false;
/* 1281 */       while (imageTypes.hasNext()) {
/*      */         
/* 1283 */         ImageTypeSpecifier type = imageTypes.next();
/* 1284 */         if (type.equals(imageType)) {
/* 1285 */           foundIt = true;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/* 1290 */       if (!foundIt) {
/* 1291 */         throw new IIOException("Destination type from ImageReadParam does not match!");
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1296 */     return imageType;
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
/*      */   public static boolean isNonStandardICCColorSpace(ColorSpace cs) {
/* 1308 */     boolean retval = false;
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
/*      */     try {
/* 1320 */       retval = (cs instanceof java.awt.color.ICC_ColorSpace && !cs.isCS_sRGB() && !cs.equals(ColorSpace.getInstance(1004)) && !cs.equals(ColorSpace.getInstance(1003)) && !cs.equals(ColorSpace.getInstance(1001)) && !cs.equals(ColorSpace.getInstance(1002)));
/* 1321 */     } catch (IllegalArgumentException illegalArgumentException) {}
/*      */ 
/*      */ 
/*      */     
/* 1325 */     return retval;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List getJDKImageReaderWriterSPI(ServiceRegistry registry, String formatName, boolean isReader) {
/*      */     Class<ImageWriterSpi> spiClass;
/*      */     String descPart;
/* 1334 */     IIORegistry iioRegistry = (IIORegistry)registry;
/*      */ 
/*      */ 
/*      */     
/* 1338 */     if (isReader) {
/* 1339 */       Class<ImageReaderSpi> clazz = ImageReaderSpi.class;
/* 1340 */       descPart = " image reader";
/*      */     } else {
/* 1342 */       spiClass = ImageWriterSpi.class;
/* 1343 */       descPart = " image writer";
/*      */     } 
/*      */     
/* 1346 */     Iterator<ImageWriterSpi> iter = iioRegistry.getServiceProviders(spiClass, true);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1351 */     String desc = "standard " + formatName + descPart;
/* 1352 */     String jiioPath = "com.github.jaiimageio.impl";
/* 1353 */     Locale locale = Locale.getDefault();
/* 1354 */     ArrayList<ImageReaderWriterSpi> list = new ArrayList();
/* 1355 */     while (iter.hasNext()) {
/* 1356 */       ImageReaderWriterSpi provider = iter.next();
/*      */ 
/*      */       
/* 1359 */       if (provider.getVendorName().startsWith("Sun Microsystems") && desc
/* 1360 */         .equalsIgnoreCase(provider.getDescription(locale)) && 
/*      */         
/* 1362 */         !provider.getPluginClassName().startsWith(jiioPath)) {
/*      */ 
/*      */         
/* 1365 */         String[] formatNames = provider.getFormatNames();
/* 1366 */         for (int i = 0; i < formatNames.length; i++) {
/* 1367 */           if (formatNames[i].equalsIgnoreCase(formatName)) {
/*      */             
/* 1369 */             list.add(provider);
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/* 1376 */     return list;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void processOnRegistration(ServiceRegistry registry, Class<ImageReaderWriterSpi> category, String formatName, ImageReaderWriterSpi spi, int deregisterJvmVersion, int priorityJvmVersion) {
/* 1387 */     String jvmVendor = System.getProperty("java.vendor");
/*      */     
/* 1389 */     String jvmVersionString = System.getProperty("java.specification.version");
/* 1390 */     int verIndex = jvmVersionString.indexOf("1.");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1399 */     jvmVersionString = jvmVersionString.substring(verIndex + 2);
/*      */     
/* 1401 */     int jvmVersion = Integer.parseInt(jvmVersionString);
/*      */     
/* 1403 */     if (jvmVendor.equals("Sun Microsystems Inc.")) {
/*      */       List<ImageReaderWriterSpi> list;
/*      */       
/* 1406 */       if (spi instanceof ImageReaderSpi) {
/* 1407 */         list = getJDKImageReaderWriterSPI(registry, formatName, true);
/*      */       } else {
/* 1409 */         list = getJDKImageReaderWriterSPI(registry, formatName, false);
/*      */       } 
/* 1411 */       if (jvmVersion >= deregisterJvmVersion && list.size() != 0) {
/*      */         
/* 1413 */         registry.deregisterServiceProvider(spi, category);
/*      */       } else {
/* 1415 */         for (int i = 0; i < list.size(); i++) {
/* 1416 */           if (jvmVersion >= priorityJvmVersion) {
/*      */             
/* 1418 */             registry.setOrdering(category, list
/* 1419 */                 .get(i), spi);
/*      */           }
/*      */           else {
/*      */             
/* 1423 */             registry.setOrdering(category, spi, list
/*      */                 
/* 1425 */                 .get(i));
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public static int readMultiByteInteger(ImageInputStream iis) throws IOException {
/* 1433 */     int value = iis.readByte();
/* 1434 */     int result = value & 0x7F;
/* 1435 */     while ((value & 0x80) == 128) {
/* 1436 */       result <<= 7;
/* 1437 */       value = iis.readByte();
/* 1438 */       result |= value & 0x7F;
/*      */     } 
/* 1440 */     return result;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\common\ImageUtil.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */