/*      */ package com.github.jaiimageio.plugins.tiff;
/*      */ 
/*      */ import com.github.jaiimageio.impl.common.BogusColorSpace;
/*      */ import com.github.jaiimageio.impl.common.ImageUtil;
/*      */ import com.github.jaiimageio.impl.common.SimpleCMYKColorSpace;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.color.ColorSpace;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ComponentColorModel;
/*      */ import java.awt.image.ComponentSampleModel;
/*      */ import java.awt.image.DataBuffer;
/*      */ import java.awt.image.DataBufferByte;
/*      */ import java.awt.image.DataBufferFloat;
/*      */ import java.awt.image.DataBufferInt;
/*      */ import java.awt.image.DataBufferShort;
/*      */ import java.awt.image.DataBufferUShort;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.MultiPixelPackedSampleModel;
/*      */ import java.awt.image.PixelInterleavedSampleModel;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.awt.image.SinglePixelPackedSampleModel;
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.nio.ByteOrder;
/*      */ import javax.imageio.IIOException;
/*      */ import javax.imageio.ImageReader;
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ import javax.imageio.metadata.IIOMetadata;
/*      */ import javax.imageio.stream.ImageInputStream;
/*      */ import javax.imageio.stream.MemoryCacheImageInputStream;
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
/*      */ public abstract class TIFFDecompressor
/*      */ {
/*      */   private static final boolean DEBUG = false;
/*      */   protected ImageReader reader;
/*      */   protected IIOMetadata metadata;
/*      */   protected int photometricInterpretation;
/*      */   protected int compression;
/*      */   protected boolean planar;
/*      */   protected int samplesPerPixel;
/*      */   protected int[] bitsPerSample;
/*  200 */   protected int[] sampleFormat = new int[] { 1 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int[] extraSamples;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] colorMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ImageInputStream stream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long offset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int byteCount;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int srcMinX;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int srcMinY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int srcWidth;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int srcHeight;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int sourceXOffset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int dstXOffset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int sourceYOffset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int dstYOffset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int subsampleX;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int subsampleY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int[] sourceBands;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int[] destinationBands;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BufferedImage rawImage;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BufferedImage image;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int dstMinX;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int dstMinY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int dstWidth;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int dstHeight;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int activeSrcMinX;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int activeSrcMinY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int activeSrcWidth;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int activeSrcHeight;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected TIFFColorConverter colorConverter;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean isBilevel;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean isContiguous;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean isImageSimple;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean adjustBitDepths;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int[][] bitDepthScale;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static SampleModel createInterleavedSM(int dataType, int numBands) {
/*  457 */     int[] bandOffsets = new int[numBands];
/*  458 */     for (int i = 0; i < numBands; i++) {
/*  459 */       bandOffsets[i] = i;
/*      */     }
/*  461 */     return new PixelInterleavedSampleModel(dataType, 1, 1, numBands, numBands, bandOffsets);
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
/*      */   static ColorModel createComponentCM(ColorSpace colorSpace, int numBands, int dataType, boolean hasAlpha, boolean isAlphaPremultiplied) {
/*      */     ColorModel colorModel;
/*  480 */     int transparency = hasAlpha ? 3 : 1;
/*      */ 
/*      */ 
/*      */     
/*  484 */     if (dataType == 4 || dataType == 5) {
/*      */ 
/*      */       
/*  487 */       colorModel = new ComponentColorModel(colorSpace, hasAlpha, isAlphaPremultiplied, transparency, dataType);
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  493 */       int bits, numBits[] = new int[numBands];
/*      */       
/*  495 */       if (dataType == 0) {
/*  496 */         bits = 8;
/*  497 */       } else if (dataType == 2 || dataType == 1) {
/*      */         
/*  499 */         bits = 16;
/*  500 */       } else if (dataType == 3) {
/*  501 */         bits = 32;
/*      */       } else {
/*  503 */         throw new IllegalArgumentException("dataType = " + dataType);
/*      */       } 
/*  505 */       for (int i = 0; i < numBands; i++) {
/*  506 */         numBits[i] = bits;
/*      */       }
/*      */       
/*  509 */       colorModel = new ComponentColorModel(colorSpace, numBits, hasAlpha, isAlphaPremultiplied, transparency, dataType);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  517 */     return colorModel;
/*      */   }
/*      */   
/*      */   private static int createMask(int[] bitsPerSample, int band) {
/*  521 */     int mask = (1 << bitsPerSample[band]) - 1;
/*  522 */     for (int i = band + 1; i < bitsPerSample.length; i++) {
/*  523 */       mask <<= bitsPerSample[i];
/*      */     }
/*      */     
/*  526 */     return mask;
/*      */   }
/*      */ 
/*      */   
/*      */   private static int getDataTypeFromNumBits(int numBits, boolean isSigned) {
/*      */     int dataType;
/*  532 */     if (numBits <= 8) {
/*  533 */       dataType = 0;
/*  534 */     } else if (numBits <= 16) {
/*  535 */       dataType = isSigned ? 2 : 1;
/*      */     } else {
/*      */       
/*  538 */       dataType = 3;
/*      */     } 
/*      */     
/*  541 */     return dataType;
/*      */   }
/*      */   
/*      */   private static boolean areIntArraysEqual(int[] a, int[] b) {
/*  545 */     if (a == null || b == null) {
/*  546 */       if (a == null && b == null) {
/*  547 */         return true;
/*      */       }
/*  549 */       return false;
/*      */     } 
/*      */ 
/*      */     
/*  553 */     if (a.length != b.length) {
/*  554 */       return false;
/*      */     }
/*      */     
/*  557 */     int length = a.length;
/*  558 */     for (int i = 0; i < length; i++) {
/*  559 */       if (a[i] != b[i]) {
/*  560 */         return false;
/*      */       }
/*      */     } 
/*      */     
/*  564 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int getDataTypeSize(int dataType) throws IIOException {
/*  572 */     int dataTypeSize = 0;
/*  573 */     switch (dataType) {
/*      */       case 0:
/*  575 */         dataTypeSize = 8;
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
/*  592 */         return dataTypeSize;case 1: case 2: dataTypeSize = 16; return dataTypeSize;case 3: case 4: dataTypeSize = 32; return dataTypeSize;case 5: dataTypeSize = 64; return dataTypeSize;
/*      */     } 
/*      */     throw new IIOException("Unknown data type " + dataType);
/*      */   }
/*      */ 
/*      */   
/*      */   private static int getBitsPerPixel(SampleModel sm) {
/*  599 */     int bitsPerPixel = 0;
/*  600 */     int[] sampleSize = sm.getSampleSize();
/*  601 */     int numBands = sampleSize.length;
/*  602 */     for (int i = 0; i < numBands; i++) {
/*  603 */       bitsPerPixel += sampleSize[i];
/*      */     }
/*  605 */     return bitsPerPixel;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean areSampleSizesEqual(SampleModel sm) {
/*  612 */     boolean allSameSize = true;
/*  613 */     int[] sampleSize = sm.getSampleSize();
/*  614 */     int sampleSize0 = sampleSize[0];
/*  615 */     int numBands = sampleSize.length;
/*      */     
/*  617 */     for (int i = 1; i < numBands; i++) {
/*  618 */       if (sampleSize[i] != sampleSize0) {
/*  619 */         allSameSize = false;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/*  624 */     return allSameSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean isDataBufferBitContiguous(SampleModel sm) throws IIOException {
/*  633 */     int dataTypeSize = getDataTypeSize(sm.getDataType());
/*      */     
/*  635 */     if (sm instanceof ComponentSampleModel) {
/*  636 */       int numBands = sm.getNumBands();
/*  637 */       for (int i = 0; i < numBands; i++) {
/*  638 */         if (sm.getSampleSize(i) != dataTypeSize)
/*      */         {
/*  640 */           return false;
/*      */         }
/*      */       } 
/*  643 */     } else if (sm instanceof MultiPixelPackedSampleModel) {
/*  644 */       MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sm;
/*      */       
/*  646 */       if (dataTypeSize % mppsm.getPixelBitStride() != 0)
/*      */       {
/*  648 */         return false;
/*      */       }
/*  650 */     } else if (sm instanceof SinglePixelPackedSampleModel) {
/*  651 */       SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
/*      */       
/*  653 */       int numBands = sm.getNumBands();
/*  654 */       int numBits = 0;
/*  655 */       for (int i = 0; i < numBands; i++) {
/*  656 */         numBits += sm.getSampleSize(i);
/*      */       }
/*  658 */       if (numBits != dataTypeSize)
/*      */       {
/*  660 */         return false;
/*      */       }
/*      */     } else {
/*      */       
/*  664 */       return false;
/*      */     } 
/*      */     
/*  667 */     return true;
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
/*      */   private static void reformatData(byte[] buf, int bytesPerRow, int numRows, short[] shortData, int[] intData, int outOffset, int outStride) throws IIOException {
/*  682 */     if (shortData != null) {
/*      */ 
/*      */ 
/*      */       
/*  686 */       int inOffset = 0;
/*  687 */       int shortsPerRow = bytesPerRow / 2;
/*  688 */       int numExtraBytes = bytesPerRow % 2;
/*  689 */       for (int j = 0; j < numRows; j++) {
/*  690 */         int k = outOffset;
/*  691 */         for (int i = 0; i < shortsPerRow; i++) {
/*  692 */           shortData[k++] = (short)((buf[inOffset++] & 0xFF) << 8 | buf[inOffset++] & 0xFF);
/*      */         }
/*      */ 
/*      */         
/*  696 */         if (numExtraBytes != 0) {
/*  697 */           shortData[k++] = (short)((buf[inOffset++] & 0xFF) << 8);
/*      */         }
/*  699 */         outOffset += outStride;
/*      */       } 
/*  701 */     } else if (intData != null) {
/*      */ 
/*      */ 
/*      */       
/*  705 */       int inOffset = 0;
/*  706 */       int intsPerRow = bytesPerRow / 4;
/*  707 */       int numExtraBytes = bytesPerRow % 4;
/*  708 */       for (int j = 0; j < numRows; j++) {
/*  709 */         int k = outOffset;
/*  710 */         for (int i = 0; i < intsPerRow; i++) {
/*  711 */           intData[k++] = (buf[inOffset++] & 0xFF) << 24 | (buf[inOffset++] & 0xFF) << 16 | (buf[inOffset++] & 0xFF) << 8 | buf[inOffset++] & 0xFF;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  717 */         if (numExtraBytes != 0) {
/*  718 */           int shift = 24;
/*  719 */           int ival = 0;
/*  720 */           for (int b = 0; b < numExtraBytes; b++) {
/*  721 */             ival |= (buf[inOffset++] & 0xFF) << shift;
/*  722 */             shift -= 8;
/*      */           } 
/*  724 */           intData[k++] = ival;
/*      */         } 
/*  726 */         outOffset += outStride;
/*      */       } 
/*      */     } else {
/*  729 */       throw new IIOException("shortData == null && intData == null!");
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
/*      */   
/*      */   private static void reformatDiscontiguousData(byte[] buf, int stride, int w, int h, WritableRaster raster) throws IOException {
/*  749 */     SampleModel sm = raster.getSampleModel();
/*  750 */     int numBands = sm.getNumBands();
/*  751 */     int[] sampleSize = sm.getSampleSize();
/*      */ 
/*      */     
/*  754 */     ByteArrayInputStream is = new ByteArrayInputStream(buf);
/*  755 */     ImageInputStream iis = new MemoryCacheImageInputStream(is);
/*      */ 
/*      */     
/*  758 */     long iisPosition = 0L;
/*  759 */     int y = raster.getMinY();
/*  760 */     for (int j = 0; j < h; j++, y++) {
/*  761 */       iis.seek(iisPosition);
/*  762 */       int x = raster.getMinX();
/*  763 */       for (int i = 0; i < w; i++, x++) {
/*  764 */         for (int b = 0; b < numBands; b++) {
/*  765 */           long bits = iis.readBits(sampleSize[b]);
/*  766 */           raster.setSample(x, y, b, (int)bits);
/*      */         } 
/*      */       } 
/*  769 */       iisPosition += stride;
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
/*      */   public static ImageTypeSpecifier getRawImageTypeSpecifier(int photometricInterpretation, int compression, int samplesPerPixel, int[] bitsPerSample, int[] sampleFormat, int[] extraSamples, char[] colorMap) {
/*  851 */     if (samplesPerPixel == 1 && (bitsPerSample[0] == 1 || bitsPerSample[0] == 2 || bitsPerSample[0] == 4 || bitsPerSample[0] == 8 || bitsPerSample[0] == 16)) {
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
/*  864 */       if (colorMap == null) {
/*      */         int k;
/*  866 */         boolean isSigned = (sampleFormat[0] == 2);
/*      */ 
/*      */         
/*  869 */         if (bitsPerSample[0] <= 8) {
/*  870 */           k = 0;
/*      */         } else {
/*  872 */           k = (sampleFormat[0] == 2) ? 2 : 1;
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  878 */         return ImageTypeSpecifier.createGrayscale(bitsPerSample[0], k, isSigned);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  883 */       int mapSize = 1 << bitsPerSample[0];
/*  884 */       byte[] redLut = new byte[mapSize];
/*  885 */       byte[] greenLut = new byte[mapSize];
/*  886 */       byte[] blueLut = new byte[mapSize];
/*  887 */       byte[] alphaLut = null;
/*      */       
/*  889 */       int idx = 0;
/*  890 */       for (int j = 0; j < mapSize; j++) {
/*  891 */         redLut[j] = (byte)(colorMap[j] * 255 / 65535);
/*  892 */         greenLut[j] = (byte)(colorMap[mapSize + j] * 255 / 65535);
/*  893 */         blueLut[j] = (byte)(colorMap[2 * mapSize + j] * 255 / 65535);
/*      */       } 
/*      */       
/*  896 */       int dataType = (bitsPerSample[0] == 8) ? 0 : 1;
/*      */       
/*  898 */       return ImageTypeSpecifier.createIndexed(redLut, greenLut, blueLut, alphaLut, bitsPerSample[0], dataType);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  908 */     if (samplesPerPixel == 2 && bitsPerSample[0] == 8 && bitsPerSample[1] == 8) {
/*      */ 
/*      */       
/*  911 */       int dataType = 0;
/*  912 */       boolean alphaPremultiplied = false;
/*  913 */       if (extraSamples != null && extraSamples[0] == 1)
/*      */       {
/*      */         
/*  916 */         alphaPremultiplied = true;
/*      */       }
/*      */       
/*  919 */       return ImageTypeSpecifier.createGrayscale(8, dataType, false, alphaPremultiplied);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  926 */     if (samplesPerPixel == 2 && bitsPerSample[0] == 16 && bitsPerSample[1] == 16) {
/*      */ 
/*      */       
/*  929 */       int dataType = (sampleFormat[0] == 2) ? 2 : 1;
/*      */ 
/*      */ 
/*      */       
/*  933 */       boolean alphaPremultiplied = false;
/*  934 */       if (extraSamples != null && extraSamples[0] == 1)
/*      */       {
/*      */         
/*  937 */         alphaPremultiplied = true;
/*      */       }
/*      */       
/*  940 */       boolean isSigned = (dataType == 2);
/*  941 */       return ImageTypeSpecifier.createGrayscale(16, dataType, isSigned, alphaPremultiplied);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  947 */     ColorSpace rgb = ColorSpace.getInstance(1000);
/*      */ 
/*      */     
/*  950 */     if (samplesPerPixel == 3 && bitsPerSample[0] == 8 && bitsPerSample[1] == 8 && bitsPerSample[2] == 8) {
/*      */       ColorSpace theColorSpace;
/*      */ 
/*      */       
/*  954 */       int[] bandOffsets = new int[3];
/*  955 */       bandOffsets[0] = 0;
/*  956 */       bandOffsets[1] = 1;
/*  957 */       bandOffsets[2] = 2;
/*  958 */       int dataType = 0;
/*      */       
/*  960 */       if ((photometricInterpretation == 6 && compression != 7 && compression != 6) || photometricInterpretation == 8) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  967 */         theColorSpace = ColorSpace.getInstance(1004);
/*      */       } else {
/*  969 */         theColorSpace = rgb;
/*      */       } 
/*  971 */       return ImageTypeSpecifier.createInterleaved(theColorSpace, bandOffsets, dataType, false, false);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  979 */     if (samplesPerPixel == 4 && bitsPerSample[0] == 8 && bitsPerSample[1] == 8 && bitsPerSample[2] == 8 && bitsPerSample[3] == 8) {
/*      */       ColorSpace theColorSpace;
/*      */       
/*      */       boolean hasAlpha;
/*      */       
/*  984 */       int[] bandOffsets = new int[4];
/*  985 */       bandOffsets[0] = 0;
/*  986 */       bandOffsets[1] = 1;
/*  987 */       bandOffsets[2] = 2;
/*  988 */       bandOffsets[3] = 3;
/*  989 */       int dataType = 0;
/*      */ 
/*      */ 
/*      */       
/*  993 */       boolean alphaPremultiplied = false;
/*  994 */       if (photometricInterpretation == 5) {
/*      */         
/*  996 */         theColorSpace = SimpleCMYKColorSpace.getInstance();
/*  997 */         hasAlpha = false;
/*      */       } else {
/*  999 */         theColorSpace = rgb;
/* 1000 */         hasAlpha = true;
/* 1001 */         if (extraSamples != null && extraSamples[0] == 1)
/*      */         {
/*      */           
/* 1004 */           alphaPremultiplied = true;
/*      */         }
/*      */       } 
/*      */       
/* 1008 */       return ImageTypeSpecifier.createInterleaved(theColorSpace, bandOffsets, dataType, hasAlpha, alphaPremultiplied);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1016 */     if (samplesPerPixel == 3 && bitsPerSample[0] == 16 && bitsPerSample[1] == 16 && bitsPerSample[2] == 16) {
/*      */ 
/*      */ 
/*      */       
/* 1020 */       int[] bandOffsets = new int[3];
/* 1021 */       bandOffsets[0] = 0;
/* 1022 */       bandOffsets[1] = 1;
/* 1023 */       bandOffsets[2] = 2;
/* 1024 */       int dataType = (sampleFormat[0] == 2) ? 2 : 1;
/*      */ 
/*      */ 
/*      */       
/* 1028 */       return ImageTypeSpecifier.createInterleaved(rgb, bandOffsets, dataType, false, false);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1036 */     if (samplesPerPixel == 4 && bitsPerSample[0] == 16 && bitsPerSample[1] == 16 && bitsPerSample[2] == 16 && bitsPerSample[3] == 16) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1041 */       int[] bandOffsets = new int[4];
/* 1042 */       bandOffsets[0] = 0;
/* 1043 */       bandOffsets[1] = 1;
/* 1044 */       bandOffsets[2] = 2;
/* 1045 */       bandOffsets[3] = 3;
/* 1046 */       int dataType = (sampleFormat[0] == 2) ? 2 : 1;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1051 */       boolean alphaPremultiplied = false;
/* 1052 */       if (extraSamples != null && extraSamples[0] == 1)
/*      */       {
/*      */         
/* 1055 */         alphaPremultiplied = true;
/*      */       }
/* 1057 */       return ImageTypeSpecifier.createInterleaved(rgb, bandOffsets, dataType, true, alphaPremultiplied);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1066 */     if (photometricInterpretation == 5 && (bitsPerSample[0] == 1 || bitsPerSample[0] == 2 || bitsPerSample[0] == 4)) {
/*      */       BogusColorSpace bogusColorSpace;
/*      */ 
/*      */       
/* 1070 */       ColorSpace cs = null;
/* 1071 */       if (samplesPerPixel == 4) {
/* 1072 */         cs = SimpleCMYKColorSpace.getInstance();
/*      */       } else {
/* 1074 */         bogusColorSpace = new BogusColorSpace(samplesPerPixel);
/*      */       } 
/*      */       
/* 1077 */       ColorModel cm = new ComponentColorModel((ColorSpace)bogusColorSpace, bitsPerSample, false, false, 1, 0);
/*      */ 
/*      */ 
/*      */       
/* 1081 */       return new ImageTypeSpecifier(cm, cm
/* 1082 */           .createCompatibleSampleModel(1, 1));
/*      */     } 
/*      */ 
/*      */     
/* 1086 */     int totalBits = 0;
/* 1087 */     for (int i = 0; i < bitsPerSample.length; i++) {
/* 1088 */       totalBits += bitsPerSample[i];
/*      */     }
/*      */ 
/*      */     
/* 1092 */     if ((samplesPerPixel == 3 || samplesPerPixel == 4) && (totalBits == 8 || totalBits == 16)) {
/*      */       
/* 1094 */       int redMask = createMask(bitsPerSample, 0);
/* 1095 */       int greenMask = createMask(bitsPerSample, 1);
/* 1096 */       int blueMask = createMask(bitsPerSample, 2);
/*      */       
/* 1098 */       int alphaMask = (samplesPerPixel == 4) ? createMask(bitsPerSample, 3) : 0;
/* 1099 */       int transferType = (totalBits == 8) ? 0 : 1;
/*      */       
/* 1101 */       boolean alphaPremultiplied = false;
/* 1102 */       if (extraSamples != null && extraSamples[0] == 1)
/*      */       {
/*      */         
/* 1105 */         alphaPremultiplied = true;
/*      */       }
/* 1107 */       return ImageTypeSpecifier.createPacked(rgb, redMask, greenMask, blueMask, alphaMask, transferType, alphaPremultiplied);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1117 */     if (bitsPerSample[0] % 8 == 0) {
/*      */       
/* 1119 */       boolean allSameBitDepth = true;
/* 1120 */       for (int j = 1; j < bitsPerSample.length; j++) {
/* 1121 */         if (bitsPerSample[j] != bitsPerSample[j - 1]) {
/* 1122 */           allSameBitDepth = false;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*      */       
/* 1128 */       if (allSameBitDepth) {
/*      */         
/* 1130 */         int dataType = -1;
/* 1131 */         boolean isDataTypeSet = false;
/* 1132 */         switch (bitsPerSample[0]) {
/*      */           case 8:
/* 1134 */             if (sampleFormat[0] != 3) {
/*      */ 
/*      */ 
/*      */               
/* 1138 */               dataType = 0;
/* 1139 */               isDataTypeSet = true;
/*      */             } 
/*      */             break;
/*      */           case 16:
/* 1143 */             if (sampleFormat[0] != 3) {
/*      */               
/* 1145 */               if (sampleFormat[0] == 2) {
/*      */                 
/* 1147 */                 dataType = 2;
/*      */               } else {
/* 1149 */                 dataType = 1;
/*      */               } 
/* 1151 */               isDataTypeSet = true;
/*      */             } 
/*      */             break;
/*      */           case 32:
/* 1155 */             if (sampleFormat[0] == 3) {
/*      */               
/* 1157 */               dataType = 4;
/*      */             } else {
/* 1159 */               dataType = 3;
/*      */             } 
/* 1161 */             isDataTypeSet = true;
/*      */             break;
/*      */         } 
/*      */         
/* 1165 */         if (isDataTypeSet) {
/*      */           ColorModel cm;
/* 1167 */           SampleModel sm = createInterleavedSM(dataType, samplesPerPixel);
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1172 */           if (samplesPerPixel >= 1 && samplesPerPixel <= 4 && (dataType == 3 || dataType == 4)) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1177 */             ColorSpace cs = (samplesPerPixel <= 2) ? ColorSpace.getInstance(1003) : rgb;
/* 1178 */             boolean hasAlpha = (samplesPerPixel % 2 == 0);
/* 1179 */             boolean alphaPremultiplied = false;
/* 1180 */             if (hasAlpha && extraSamples != null && extraSamples[0] == 1)
/*      */             {
/*      */               
/* 1183 */               alphaPremultiplied = true;
/*      */             }
/*      */             
/* 1186 */             cm = createComponentCM(cs, samplesPerPixel, dataType, hasAlpha, alphaPremultiplied);
/*      */           
/*      */           }
/*      */           else {
/*      */ 
/*      */             
/* 1192 */             BogusColorSpace bogusColorSpace = new BogusColorSpace(samplesPerPixel);
/* 1193 */             cm = createComponentCM((ColorSpace)bogusColorSpace, samplesPerPixel, dataType, false, false);
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1200 */           return new ImageTypeSpecifier(cm, sm);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1208 */     if (colorMap == null && sampleFormat[0] != 3) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1213 */       int maxBitsPerSample = 0;
/* 1214 */       for (int j = 0; j < bitsPerSample.length; j++) {
/* 1215 */         if (bitsPerSample[j] > maxBitsPerSample) {
/* 1216 */           maxBitsPerSample = bitsPerSample[j];
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/* 1221 */       boolean isSigned = (sampleFormat[0] == 2);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1226 */       if (samplesPerPixel == 1) {
/*      */         
/* 1228 */         int dataType = getDataTypeFromNumBits(maxBitsPerSample, isSigned);
/*      */         
/* 1230 */         return ImageTypeSpecifier.createGrayscale(maxBitsPerSample, dataType, isSigned);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1236 */       if (samplesPerPixel == 2) {
/* 1237 */         boolean alphaPremultiplied = false;
/* 1238 */         if (extraSamples != null && extraSamples[0] == 1)
/*      */         {
/*      */           
/* 1241 */           alphaPremultiplied = true;
/*      */         }
/*      */ 
/*      */         
/* 1245 */         int dataType = getDataTypeFromNumBits(maxBitsPerSample, isSigned);
/*      */         
/* 1247 */         return ImageTypeSpecifier.createGrayscale(maxBitsPerSample, dataType, false, alphaPremultiplied);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1253 */       if (samplesPerPixel == 3 || samplesPerPixel == 4) {
/* 1254 */         if (totalBits <= 32 && !isSigned) {
/*      */           
/* 1256 */           int redMask = createMask(bitsPerSample, 0);
/* 1257 */           int greenMask = createMask(bitsPerSample, 1);
/* 1258 */           int blueMask = createMask(bitsPerSample, 2);
/*      */           
/* 1260 */           int alphaMask = (samplesPerPixel == 4) ? createMask(bitsPerSample, 3) : 0;
/*      */           
/* 1262 */           int transferType = getDataTypeFromNumBits(totalBits, false);
/* 1263 */           boolean alphaPremultiplied = false;
/* 1264 */           if (extraSamples != null && extraSamples[0] == 1)
/*      */           {
/*      */             
/* 1267 */             alphaPremultiplied = true;
/*      */           }
/* 1269 */           return ImageTypeSpecifier.createPacked(rgb, redMask, greenMask, blueMask, alphaMask, transferType, alphaPremultiplied);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1276 */         if (samplesPerPixel == 3) {
/*      */           
/* 1278 */           int[] bandOffsets = { 0, 1, 2 };
/*      */           
/* 1280 */           int dataType = getDataTypeFromNumBits(maxBitsPerSample, isSigned);
/* 1281 */           return ImageTypeSpecifier.createInterleaved(rgb, bandOffsets, dataType, false, false);
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1286 */         if (samplesPerPixel == 4)
/*      */         {
/* 1288 */           int[] bandOffsets = { 0, 1, 2, 3 };
/*      */           
/* 1290 */           int dataType = getDataTypeFromNumBits(maxBitsPerSample, isSigned);
/* 1291 */           boolean alphaPremultiplied = false;
/* 1292 */           if (extraSamples != null && extraSamples[0] == 1)
/*      */           {
/*      */             
/* 1295 */             alphaPremultiplied = true;
/*      */           }
/* 1297 */           return ImageTypeSpecifier.createInterleaved(rgb, bandOffsets, dataType, true, alphaPremultiplied);
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */         
/* 1306 */         int dataType = getDataTypeFromNumBits(maxBitsPerSample, isSigned);
/* 1307 */         SampleModel sm = createInterleavedSM(dataType, samplesPerPixel);
/*      */         
/* 1309 */         BogusColorSpace bogusColorSpace = new BogusColorSpace(samplesPerPixel);
/* 1310 */         ColorModel cm = createComponentCM((ColorSpace)bogusColorSpace, samplesPerPixel, dataType, false, false);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1315 */         return new ImageTypeSpecifier(cm, sm);
/*      */       } 
/*      */     } 
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
/* 1352 */     return null;
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
/*      */   public void setReader(ImageReader reader) {
/* 1365 */     this.reader = reader;
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
/*      */   public void setMetadata(IIOMetadata metadata) {
/* 1379 */     this.metadata = metadata;
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
/*      */   public void setPhotometricInterpretation(int photometricInterpretation) {
/* 1394 */     this.photometricInterpretation = photometricInterpretation;
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
/*      */   public void setCompression(int compression) {
/* 1407 */     this.compression = compression;
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
/*      */   public void setPlanar(boolean planar) {
/* 1421 */     this.planar = planar;
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
/*      */   public void setSamplesPerPixel(int samplesPerPixel) {
/* 1435 */     this.samplesPerPixel = samplesPerPixel;
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
/*      */   public void setBitsPerSample(int[] bitsPerSample) {
/* 1449 */     this
/* 1450 */       .bitsPerSample = (bitsPerSample == null) ? null : (int[])bitsPerSample.clone();
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
/*      */   public void setSampleFormat(int[] sampleFormat) {
/* 1464 */     (new int[1])[0] = 1; this
/*      */       
/* 1466 */       .sampleFormat = (sampleFormat == null) ? new int[1] : (int[])sampleFormat.clone();
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
/*      */   public void setExtraSamples(int[] extraSamples) {
/* 1481 */     this
/* 1482 */       .extraSamples = (extraSamples == null) ? null : (int[])extraSamples.clone();
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
/*      */   public void setColorMap(char[] colorMap) {
/* 1496 */     this
/* 1497 */       .colorMap = (colorMap == null) ? null : (char[])colorMap.clone();
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
/*      */   public void setStream(ImageInputStream stream) {
/* 1510 */     this.stream = stream;
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
/*      */   public void setOffset(long offset) {
/* 1524 */     this.offset = offset;
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
/*      */   public void setByteCount(int byteCount) {
/* 1537 */     this.byteCount = byteCount;
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
/*      */   public void setSrcMinX(int srcMinX) {
/* 1554 */     this.srcMinX = srcMinX;
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
/*      */   public void setSrcMinY(int srcMinY) {
/* 1569 */     this.srcMinY = srcMinY;
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
/*      */   public void setSrcWidth(int srcWidth) {
/* 1583 */     this.srcWidth = srcWidth;
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
/*      */   public void setSrcHeight(int srcHeight) {
/* 1597 */     this.srcHeight = srcHeight;
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
/*      */   public void setSourceXOffset(int sourceXOffset) {
/* 1613 */     this.sourceXOffset = sourceXOffset;
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
/*      */   public void setDstXOffset(int dstXOffset) {
/* 1627 */     this.dstXOffset = dstXOffset;
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
/*      */   public void setSourceYOffset(int sourceYOffset) {
/* 1641 */     this.sourceYOffset = sourceYOffset;
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
/*      */   public void setDstYOffset(int dstYOffset) {
/* 1655 */     this.dstYOffset = dstYOffset;
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
/*      */   public void setSubsampleX(int subsampleX) {
/* 1673 */     if (subsampleX <= 0) {
/* 1674 */       throw new IllegalArgumentException("subsampleX <= 0!");
/*      */     }
/* 1676 */     this.subsampleX = subsampleX;
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
/*      */   public void setSubsampleY(int subsampleY) {
/* 1692 */     if (subsampleY <= 0) {
/* 1693 */       throw new IllegalArgumentException("subsampleY <= 0!");
/*      */     }
/* 1695 */     this.subsampleY = subsampleY;
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
/*      */   public void setSourceBands(int[] sourceBands) {
/* 1711 */     this
/* 1712 */       .sourceBands = (sourceBands == null) ? null : (int[])sourceBands.clone();
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
/*      */   public void setDestinationBands(int[] destinationBands) {
/* 1726 */     this
/* 1727 */       .destinationBands = (destinationBands == null) ? null : (int[])destinationBands.clone();
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
/*      */   public void setImage(BufferedImage image) {
/* 1742 */     this.image = image;
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
/*      */   public void setDstMinX(int dstMinX) {
/* 1756 */     this.dstMinX = dstMinX;
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
/*      */   public void setDstMinY(int dstMinY) {
/* 1770 */     this.dstMinY = dstMinY;
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
/*      */   public void setDstWidth(int dstWidth) {
/* 1783 */     this.dstWidth = dstWidth;
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
/*      */   public void setDstHeight(int dstHeight) {
/* 1796 */     this.dstHeight = dstHeight;
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
/*      */   public void setActiveSrcMinX(int activeSrcMinX) {
/* 1812 */     this.activeSrcMinX = activeSrcMinX;
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
/*      */   public void setActiveSrcMinY(int activeSrcMinY) {
/* 1826 */     this.activeSrcMinY = activeSrcMinY;
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
/*      */   public void setActiveSrcWidth(int activeSrcWidth) {
/* 1839 */     this.activeSrcWidth = activeSrcWidth;
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
/*      */   public void setActiveSrcHeight(int activeSrcHeight) {
/* 1852 */     this.activeSrcHeight = activeSrcHeight;
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
/*      */   public void setColorConverter(TIFFColorConverter colorConverter) {
/* 1864 */     this.colorConverter = colorConverter;
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
/*      */   public ImageTypeSpecifier getRawImageType() {
/* 1876 */     ImageTypeSpecifier its = getRawImageTypeSpecifier(this.photometricInterpretation, this.compression, this.samplesPerPixel, this.bitsPerSample, this.sampleFormat, this.extraSamples, this.colorMap);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1883 */     return its;
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
/*      */   public BufferedImage createRawImage() {
/* 1902 */     if (this.planar) {
/*      */ 
/*      */ 
/*      */       
/* 1906 */       int dataType, bps = this.bitsPerSample[this.sourceBands[0]];
/*      */ 
/*      */ 
/*      */       
/* 1910 */       if (this.sampleFormat[0] == 3) {
/*      */         
/* 1912 */         dataType = 4;
/* 1913 */       } else if (bps <= 8) {
/* 1914 */         dataType = 0;
/* 1915 */       } else if (bps <= 16) {
/* 1916 */         if (this.sampleFormat[0] == 2) {
/*      */           
/* 1918 */           dataType = 2;
/*      */         } else {
/* 1920 */           dataType = 1;
/*      */         } 
/*      */       } else {
/* 1923 */         dataType = 3;
/*      */       } 
/*      */       
/* 1926 */       ColorSpace csGray = ColorSpace.getInstance(1003);
/*      */       
/* 1928 */       ImageTypeSpecifier imageTypeSpecifier = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1934 */       if (bps == 1 || bps == 2 || bps == 4) {
/* 1935 */         int bits = bps;
/* 1936 */         int size = 1 << bits;
/* 1937 */         byte[] r = new byte[size];
/* 1938 */         byte[] g = new byte[size];
/* 1939 */         byte[] b = new byte[size];
/* 1940 */         for (int j = 0; j < r.length; j++) {
/* 1941 */           r[j] = 0;
/* 1942 */           g[j] = 0;
/* 1943 */           b[j] = 0;
/*      */         } 
/* 1945 */         ColorModel cmGray = new IndexColorModel(bits, size, r, g, b);
/* 1946 */         SampleModel smGray = new MultiPixelPackedSampleModel(0, 1, 1, bits);
/* 1947 */         imageTypeSpecifier = new ImageTypeSpecifier(cmGray, smGray);
/*      */       } else {
/*      */         
/* 1950 */         imageTypeSpecifier = ImageTypeSpecifier.createInterleaved(csGray, new int[] { 0 }, dataType, false, false);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1957 */       return imageTypeSpecifier.createBufferedImage(this.srcWidth, this.srcHeight);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1964 */     ImageTypeSpecifier its = getRawImageType();
/* 1965 */     if (its == null) {
/* 1966 */       return null;
/*      */     }
/*      */     
/* 1969 */     BufferedImage bi = its.createBufferedImage(this.srcWidth, this.srcHeight);
/* 1970 */     return bi;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void decodeRaw(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) throws IOException;
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
/*      */   public void decodeRaw(short[] s, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
/* 2022 */     int bytesPerRow = (this.srcWidth * bitsPerPixel + 7) / 8;
/* 2023 */     int shortsPerRow = bytesPerRow / 2;
/*      */     
/* 2025 */     byte[] b = new byte[bytesPerRow * this.srcHeight];
/* 2026 */     decodeRaw(b, 0, bitsPerPixel, bytesPerRow);
/*      */     
/* 2028 */     int bOffset = 0;
/* 2029 */     if (this.stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
/* 2030 */       for (int j = 0; j < this.srcHeight; j++) {
/* 2031 */         for (int i = 0; i < shortsPerRow; i++) {
/* 2032 */           short hiVal = (short)b[bOffset++];
/* 2033 */           short loVal = (short)b[bOffset++];
/* 2034 */           short sval = (short)(hiVal << 8 | loVal & 0xFF);
/* 2035 */           s[dstOffset + i] = sval;
/*      */         } 
/*      */         
/* 2038 */         dstOffset += scanlineStride;
/*      */       } 
/*      */     } else {
/* 2041 */       for (int j = 0; j < this.srcHeight; j++) {
/* 2042 */         for (int i = 0; i < shortsPerRow; i++) {
/* 2043 */           short loVal = (short)b[bOffset++];
/* 2044 */           short hiVal = (short)b[bOffset++];
/* 2045 */           short sval = (short)(hiVal << 8 | loVal & 0xFF);
/* 2046 */           s[dstOffset + i] = sval;
/*      */         } 
/*      */         
/* 2049 */         dstOffset += scanlineStride;
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
/*      */   public void decodeRaw(int[] i, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
/* 2079 */     int numBands = bitsPerPixel / 32;
/* 2080 */     int intsPerRow = this.srcWidth * numBands;
/* 2081 */     int bytesPerRow = intsPerRow * 4;
/*      */     
/* 2083 */     byte[] b = new byte[bytesPerRow * this.srcHeight];
/* 2084 */     decodeRaw(b, 0, bitsPerPixel, bytesPerRow);
/*      */     
/* 2086 */     int bOffset = 0;
/* 2087 */     if (this.stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
/* 2088 */       for (int j = 0; j < this.srcHeight; j++) {
/* 2089 */         for (int k = 0; k < intsPerRow; k++) {
/* 2090 */           int v0 = b[bOffset++] & 0xFF;
/* 2091 */           int v1 = b[bOffset++] & 0xFF;
/* 2092 */           int v2 = b[bOffset++] & 0xFF;
/* 2093 */           int v3 = b[bOffset++] & 0xFF;
/* 2094 */           int ival = v0 << 24 | v1 << 16 | v2 << 8 | v3;
/* 2095 */           i[dstOffset + k] = ival;
/*      */         } 
/*      */         
/* 2098 */         dstOffset += scanlineStride;
/*      */       } 
/*      */     } else {
/* 2101 */       for (int j = 0; j < this.srcHeight; j++) {
/* 2102 */         for (int k = 0; k < intsPerRow; k++) {
/* 2103 */           int v3 = b[bOffset++] & 0xFF;
/* 2104 */           int v2 = b[bOffset++] & 0xFF;
/* 2105 */           int v1 = b[bOffset++] & 0xFF;
/* 2106 */           int v0 = b[bOffset++] & 0xFF;
/* 2107 */           int ival = v0 << 24 | v1 << 16 | v2 << 8 | v3;
/* 2108 */           i[dstOffset + k] = ival;
/*      */         } 
/*      */         
/* 2111 */         dstOffset += scanlineStride;
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
/*      */   public void decodeRaw(float[] f, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
/* 2141 */     int numBands = bitsPerPixel / 32;
/* 2142 */     int floatsPerRow = this.srcWidth * numBands;
/* 2143 */     int bytesPerRow = floatsPerRow * 4;
/*      */     
/* 2145 */     byte[] b = new byte[bytesPerRow * this.srcHeight];
/* 2146 */     decodeRaw(b, 0, bitsPerPixel, bytesPerRow);
/*      */     
/* 2148 */     int bOffset = 0;
/* 2149 */     if (this.stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
/* 2150 */       for (int j = 0; j < this.srcHeight; j++) {
/* 2151 */         for (int i = 0; i < floatsPerRow; i++) {
/* 2152 */           int v0 = b[bOffset++] & 0xFF;
/* 2153 */           int v1 = b[bOffset++] & 0xFF;
/* 2154 */           int v2 = b[bOffset++] & 0xFF;
/* 2155 */           int v3 = b[bOffset++] & 0xFF;
/* 2156 */           int ival = v0 << 24 | v1 << 16 | v2 << 8 | v3;
/* 2157 */           float fval = Float.intBitsToFloat(ival);
/* 2158 */           f[dstOffset + i] = fval;
/*      */         } 
/*      */         
/* 2161 */         dstOffset += scanlineStride;
/*      */       } 
/*      */     } else {
/* 2164 */       for (int j = 0; j < this.srcHeight; j++) {
/* 2165 */         for (int i = 0; i < floatsPerRow; i++) {
/* 2166 */           int v3 = b[bOffset++] & 0xFF;
/* 2167 */           int v2 = b[bOffset++] & 0xFF;
/* 2168 */           int v1 = b[bOffset++] & 0xFF;
/* 2169 */           int v0 = b[bOffset++] & 0xFF;
/* 2170 */           int ival = v0 << 24 | v1 << 16 | v2 << 8 | v3;
/* 2171 */           float fval = Float.intBitsToFloat(ival);
/* 2172 */           f[dstOffset + i] = fval;
/*      */         } 
/*      */         
/* 2175 */         dstOffset += scanlineStride;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isFirstBitDepthTable = true;
/*      */   
/*      */   private boolean planarCache = false;
/*      */   
/* 2185 */   private int[] destBitsPerSampleCache = null;
/* 2186 */   private int[] sourceBandsCache = null;
/* 2187 */   private int[] bitsPerSampleCache = null;
/* 2188 */   private int[] destinationBandsCache = null;
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
/*      */   public void beginDecoding() {
/* 2212 */     this.adjustBitDepths = false;
/* 2213 */     int numBands = this.destinationBands.length;
/* 2214 */     int[] destBitsPerSample = null;
/* 2215 */     if (this.planar) {
/* 2216 */       int totalNumBands = this.bitsPerSample.length;
/* 2217 */       destBitsPerSample = new int[totalNumBands];
/* 2218 */       int dbps = this.image.getSampleModel().getSampleSize(0);
/* 2219 */       for (int b = 0; b < totalNumBands; b++) {
/* 2220 */         destBitsPerSample[b] = dbps;
/*      */       }
/*      */     } else {
/* 2223 */       destBitsPerSample = this.image.getSampleModel().getSampleSize();
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2228 */     if (this.photometricInterpretation != 5 || (this.bitsPerSample[0] != 1 && this.bitsPerSample[0] != 2 && this.bitsPerSample[0] != 4))
/*      */     {
/*      */ 
/*      */       
/* 2232 */       for (int b = 0; b < numBands; b++) {
/* 2233 */         if (destBitsPerSample[this.destinationBands[b]] != this.bitsPerSample[this.sourceBands[b]]) {
/*      */           
/* 2235 */           this.adjustBitDepths = true;
/*      */ 
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 2243 */     if (this.adjustBitDepths) {
/*      */ 
/*      */ 
/*      */       
/* 2247 */       if (this.isFirstBitDepthTable || this.planar != this.planarCache || 
/*      */         
/* 2249 */         !areIntArraysEqual(destBitsPerSample, this.destBitsPerSampleCache) || 
/*      */         
/* 2251 */         !areIntArraysEqual(this.sourceBands, this.sourceBandsCache) || 
/*      */         
/* 2253 */         !areIntArraysEqual(this.bitsPerSample, this.bitsPerSampleCache) || 
/*      */         
/* 2255 */         !areIntArraysEqual(this.destinationBands, this.destinationBandsCache))
/*      */       {
/*      */         
/* 2258 */         this.isFirstBitDepthTable = false;
/*      */ 
/*      */         
/* 2261 */         this.planarCache = this.planar;
/* 2262 */         this
/* 2263 */           .destBitsPerSampleCache = (int[])destBitsPerSample.clone();
/* 2264 */         this
/* 2265 */           .sourceBandsCache = (this.sourceBands == null) ? null : (int[])this.sourceBands.clone();
/* 2266 */         this
/* 2267 */           .bitsPerSampleCache = (this.bitsPerSample == null) ? null : (int[])this.bitsPerSample.clone();
/* 2268 */         this
/* 2269 */           .destinationBandsCache = (this.destinationBands == null) ? null : (int[])this.destinationBands.clone();
/*      */ 
/*      */         
/* 2272 */         this.bitDepthScale = new int[numBands][];
/* 2273 */         for (int b = 0; b < numBands; b++) {
/* 2274 */           int maxInSample = (1 << this.bitsPerSample[this.sourceBands[b]]) - 1;
/* 2275 */           int halfMaxInSample = maxInSample / 2;
/*      */           
/* 2277 */           int maxOutSample = (1 << destBitsPerSample[this.destinationBands[b]]) - 1;
/*      */ 
/*      */           
/* 2280 */           this.bitDepthScale[b] = new int[maxInSample + 1];
/* 2281 */           for (int s = 0; s <= maxInSample; s++) {
/* 2282 */             this.bitDepthScale[b][s] = (s * maxOutSample + halfMaxInSample) / maxInSample;
/*      */           }
/*      */         }
/*      */       
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/* 2290 */       this.bitDepthScale = (int[][])null;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2299 */     boolean sourceBandsNormal = false;
/* 2300 */     boolean destinationBandsNormal = false;
/* 2301 */     if (numBands == this.samplesPerPixel) {
/* 2302 */       sourceBandsNormal = true;
/* 2303 */       destinationBandsNormal = true;
/* 2304 */       for (int i = 0; i < numBands; i++) {
/* 2305 */         if (this.sourceBands[i] != i) {
/* 2306 */           sourceBandsNormal = false;
/*      */         }
/* 2308 */         if (this.destinationBands[i] != i) {
/* 2309 */           destinationBandsNormal = false;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2318 */     this
/* 2319 */       .isBilevel = ImageUtil.isBinary(this.image.getRaster().getSampleModel());
/* 2320 */     this
/* 2321 */       .isContiguous = this.isBilevel ? true : ImageUtil.imageIsContiguous(this.image);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2326 */     this
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2331 */       .isImageSimple = (this.colorConverter == null && this.subsampleX == 1 && this.subsampleY == 1 && this.srcWidth == this.dstWidth && this.srcHeight == this.dstHeight && this.dstMinX + this.dstWidth <= this.image.getWidth() && this.dstMinY + this.dstHeight <= this.image.getHeight() && sourceBandsNormal && destinationBandsNormal && !this.adjustBitDepths);
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
/*      */   public void decode() throws IOException {
/* 2427 */     byte[] byteData = null;
/* 2428 */     short[] shortData = null;
/* 2429 */     int[] intData = null;
/* 2430 */     float[] floatData = null;
/*      */     
/* 2432 */     int dstOffset = 0;
/* 2433 */     int pixelBitStride = 1;
/* 2434 */     int scanlineStride = 0;
/*      */ 
/*      */ 
/*      */     
/* 2438 */     this.rawImage = null;
/* 2439 */     if (this.isImageSimple) {
/* 2440 */       if (this.isBilevel) {
/* 2441 */         this.rawImage = this.image;
/* 2442 */       } else if (this.isContiguous) {
/* 2443 */         this
/* 2444 */           .rawImage = this.image.getSubimage(this.dstMinX, this.dstMinY, this.dstWidth, this.dstHeight);
/*      */       } 
/*      */     }
/*      */     
/* 2448 */     boolean isDirectCopy = (this.rawImage != null);
/*      */     
/* 2450 */     if (this.rawImage == null) {
/* 2451 */       this.rawImage = createRawImage();
/* 2452 */       if (this.rawImage == null) {
/* 2453 */         throw new IIOException("Couldn't create image buffer!");
/*      */       }
/*      */     } 
/*      */     
/* 2457 */     WritableRaster ras = this.rawImage.getRaster();
/*      */     
/* 2459 */     if (this.isBilevel) {
/*      */ 
/*      */       
/* 2462 */       Rectangle rect = this.isImageSimple ? new Rectangle(this.dstMinX, this.dstMinY, this.dstWidth, this.dstHeight) : ras.getBounds();
/* 2463 */       byteData = ImageUtil.getPackedBinaryData(ras, rect);
/* 2464 */       dstOffset = 0;
/* 2465 */       pixelBitStride = 1;
/* 2466 */       scanlineStride = (rect.width + 7) / 8;
/*      */     } else {
/* 2468 */       SampleModel sm = ras.getSampleModel();
/* 2469 */       DataBuffer db = ras.getDataBuffer();
/*      */       
/* 2471 */       boolean isSupportedType = false;
/*      */       
/* 2473 */       if (sm instanceof ComponentSampleModel) {
/* 2474 */         ComponentSampleModel csm = (ComponentSampleModel)sm;
/* 2475 */         dstOffset = csm.getOffset(-ras.getSampleModelTranslateX(), 
/* 2476 */             -ras.getSampleModelTranslateY());
/* 2477 */         scanlineStride = csm.getScanlineStride();
/* 2478 */         if (db instanceof DataBufferByte) {
/* 2479 */           DataBufferByte dbb = (DataBufferByte)db;
/*      */           
/* 2481 */           byteData = dbb.getData();
/* 2482 */           pixelBitStride = csm.getPixelStride() * 8;
/* 2483 */           isSupportedType = true;
/* 2484 */         } else if (db instanceof DataBufferUShort) {
/* 2485 */           DataBufferUShort dbus = (DataBufferUShort)db;
/*      */           
/* 2487 */           shortData = dbus.getData();
/* 2488 */           pixelBitStride = csm.getPixelStride() * 16;
/* 2489 */           isSupportedType = true;
/* 2490 */         } else if (db instanceof DataBufferShort) {
/* 2491 */           DataBufferShort dbs = (DataBufferShort)db;
/*      */           
/* 2493 */           shortData = dbs.getData();
/* 2494 */           pixelBitStride = csm.getPixelStride() * 16;
/* 2495 */           isSupportedType = true;
/* 2496 */         } else if (db instanceof DataBufferInt) {
/* 2497 */           DataBufferInt dbi = (DataBufferInt)db;
/*      */           
/* 2499 */           intData = dbi.getData();
/* 2500 */           pixelBitStride = csm.getPixelStride() * 32;
/* 2501 */           isSupportedType = true;
/* 2502 */         } else if (db instanceof DataBufferFloat) {
/* 2503 */           DataBufferFloat dbf = (DataBufferFloat)db;
/*      */           
/* 2505 */           floatData = dbf.getData();
/* 2506 */           pixelBitStride = csm.getPixelStride() * 32;
/* 2507 */           isSupportedType = true;
/*      */         } 
/* 2509 */       } else if (sm instanceof MultiPixelPackedSampleModel) {
/* 2510 */         MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sm;
/*      */ 
/*      */         
/* 2513 */         dstOffset = mppsm.getOffset(-ras.getSampleModelTranslateX(), 
/* 2514 */             -ras.getSampleModelTranslateY());
/* 2515 */         pixelBitStride = mppsm.getPixelBitStride();
/* 2516 */         scanlineStride = mppsm.getScanlineStride();
/* 2517 */         if (db instanceof DataBufferByte) {
/* 2518 */           DataBufferByte dbb = (DataBufferByte)db;
/*      */           
/* 2520 */           byteData = dbb.getData();
/* 2521 */           isSupportedType = true;
/* 2522 */         } else if (db instanceof DataBufferUShort) {
/* 2523 */           DataBufferUShort dbus = (DataBufferUShort)db;
/*      */           
/* 2525 */           shortData = dbus.getData();
/* 2526 */           isSupportedType = true;
/* 2527 */         } else if (db instanceof DataBufferInt) {
/* 2528 */           DataBufferInt dbi = (DataBufferInt)db;
/*      */           
/* 2530 */           intData = dbi.getData();
/* 2531 */           isSupportedType = true;
/*      */         } 
/* 2533 */       } else if (sm instanceof SinglePixelPackedSampleModel) {
/* 2534 */         SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
/*      */ 
/*      */         
/* 2537 */         dstOffset = sppsm.getOffset(-ras.getSampleModelTranslateX(), 
/* 2538 */             -ras.getSampleModelTranslateY());
/* 2539 */         scanlineStride = sppsm.getScanlineStride();
/* 2540 */         if (db instanceof DataBufferByte) {
/* 2541 */           DataBufferByte dbb = (DataBufferByte)db;
/*      */           
/* 2543 */           byteData = dbb.getData();
/* 2544 */           pixelBitStride = 8;
/* 2545 */           isSupportedType = true;
/* 2546 */         } else if (db instanceof DataBufferUShort) {
/* 2547 */           DataBufferUShort dbus = (DataBufferUShort)db;
/*      */           
/* 2549 */           shortData = dbus.getData();
/* 2550 */           pixelBitStride = 16;
/* 2551 */           isSupportedType = true;
/* 2552 */         } else if (db instanceof DataBufferInt) {
/* 2553 */           DataBufferInt dbi = (DataBufferInt)db;
/*      */           
/* 2555 */           intData = dbi.getData();
/* 2556 */           pixelBitStride = 32;
/* 2557 */           isSupportedType = true;
/*      */         } 
/*      */       } 
/*      */       
/* 2561 */       if (!isSupportedType) {
/* 2562 */         throw new IIOException("Unsupported raw image type: SampleModel = " + sm + "; DataBuffer = " + db);
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2568 */     if (this.isBilevel) {
/*      */       
/* 2570 */       decodeRaw(byteData, dstOffset, pixelBitStride, scanlineStride);
/*      */     } else {
/* 2572 */       SampleModel sm = ras.getSampleModel();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2577 */       if (isDataBufferBitContiguous(sm)) {
/*      */         
/* 2579 */         if (byteData != null) {
/*      */ 
/*      */ 
/*      */           
/* 2583 */           decodeRaw(byteData, dstOffset, pixelBitStride, scanlineStride);
/*      */         }
/* 2585 */         else if (floatData != null) {
/*      */ 
/*      */ 
/*      */           
/* 2589 */           decodeRaw(floatData, dstOffset, pixelBitStride, scanlineStride);
/*      */         
/*      */         }
/* 2592 */         else if (shortData != null) {
/* 2593 */           if (areSampleSizesEqual(sm) && sm
/* 2594 */             .getSampleSize(0) == 16)
/*      */           {
/*      */ 
/*      */ 
/*      */             
/* 2599 */             decodeRaw(shortData, dstOffset, pixelBitStride, scanlineStride);
/*      */ 
/*      */           
/*      */           }
/*      */           else
/*      */           {
/*      */             
/* 2606 */             int bpp = getBitsPerPixel(sm);
/* 2607 */             int bytesPerRow = (bpp * this.srcWidth + 7) / 8;
/* 2608 */             byte[] buf = new byte[bytesPerRow * this.srcHeight];
/* 2609 */             decodeRaw(buf, 0, bpp, bytesPerRow);
/* 2610 */             reformatData(buf, bytesPerRow, this.srcHeight, shortData, null, dstOffset, scanlineStride);
/*      */           }
/*      */         
/*      */         }
/* 2614 */         else if (intData != null) {
/* 2615 */           if (areSampleSizesEqual(sm) && sm
/* 2616 */             .getSampleSize(0) == 32)
/*      */           {
/*      */ 
/*      */ 
/*      */             
/* 2621 */             decodeRaw(intData, dstOffset, pixelBitStride, scanlineStride);
/*      */ 
/*      */           
/*      */           }
/*      */           else
/*      */           {
/*      */             
/* 2628 */             int bpp = getBitsPerPixel(sm);
/* 2629 */             int bytesPerRow = (bpp * this.srcWidth + 7) / 8;
/* 2630 */             byte[] buf = new byte[bytesPerRow * this.srcHeight];
/* 2631 */             decodeRaw(buf, 0, bpp, bytesPerRow);
/* 2632 */             reformatData(buf, bytesPerRow, this.srcHeight, null, intData, dstOffset, scanlineStride);
/*      */           
/*      */           }
/*      */ 
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */         
/* 2644 */         int bpp = getBitsPerPixel(sm);
/* 2645 */         int bytesPerRow = (bpp * this.srcWidth + 7) / 8;
/* 2646 */         byte[] buf = new byte[bytesPerRow * this.srcHeight];
/* 2647 */         decodeRaw(buf, 0, bpp, bytesPerRow);
/* 2648 */         reformatDiscontiguousData(buf, bytesPerRow, this.srcWidth, this.srcHeight, ras);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2656 */     if (this.colorConverter != null) {
/* 2657 */       float[] rgb = new float[3];
/*      */       
/* 2659 */       if (byteData != null) {
/* 2660 */         for (int j = 0; j < this.dstHeight; j++) {
/* 2661 */           int idx = dstOffset;
/* 2662 */           for (int i = 0; i < this.dstWidth; i++) {
/* 2663 */             float x0 = (byteData[idx] & 0xFF);
/* 2664 */             float x1 = (byteData[idx + 1] & 0xFF);
/* 2665 */             float x2 = (byteData[idx + 2] & 0xFF);
/*      */             
/* 2667 */             this.colorConverter.toRGB(x0, x1, x2, rgb);
/*      */             
/* 2669 */             byteData[idx] = (byte)(int)rgb[0];
/* 2670 */             byteData[idx + 1] = (byte)(int)rgb[1];
/* 2671 */             byteData[idx + 2] = (byte)(int)rgb[2];
/*      */             
/* 2673 */             idx += 3;
/*      */           } 
/*      */           
/* 2676 */           dstOffset += scanlineStride;
/*      */         } 
/* 2678 */       } else if (shortData != null) {
/* 2679 */         if (this.sampleFormat[0] == 2) {
/*      */           
/* 2681 */           for (int j = 0; j < this.dstHeight; j++) {
/* 2682 */             int idx = dstOffset;
/* 2683 */             for (int i = 0; i < this.dstWidth; i++) {
/* 2684 */               float x0 = shortData[idx];
/* 2685 */               float x1 = shortData[idx + 1];
/* 2686 */               float x2 = shortData[idx + 2];
/*      */               
/* 2688 */               this.colorConverter.toRGB(x0, x1, x2, rgb);
/*      */               
/* 2690 */               shortData[idx] = (short)(int)rgb[0];
/* 2691 */               shortData[idx + 1] = (short)(int)rgb[1];
/* 2692 */               shortData[idx + 2] = (short)(int)rgb[2];
/*      */               
/* 2694 */               idx += 3;
/*      */             } 
/*      */             
/* 2697 */             dstOffset += scanlineStride;
/*      */           } 
/*      */         } else {
/* 2700 */           for (int j = 0; j < this.dstHeight; j++) {
/* 2701 */             int idx = dstOffset;
/* 2702 */             for (int i = 0; i < this.dstWidth; i++) {
/* 2703 */               float x0 = (shortData[idx] & 0xFFFF);
/* 2704 */               float x1 = (shortData[idx + 1] & 0xFFFF);
/* 2705 */               float x2 = (shortData[idx + 2] & 0xFFFF);
/*      */               
/* 2707 */               this.colorConverter.toRGB(x0, x1, x2, rgb);
/*      */               
/* 2709 */               shortData[idx] = (short)(int)rgb[0];
/* 2710 */               shortData[idx + 1] = (short)(int)rgb[1];
/* 2711 */               shortData[idx + 2] = (short)(int)rgb[2];
/*      */               
/* 2713 */               idx += 3;
/*      */             } 
/*      */             
/* 2716 */             dstOffset += scanlineStride;
/*      */           } 
/*      */         } 
/* 2719 */       } else if (intData != null) {
/* 2720 */         for (int j = 0; j < this.dstHeight; j++) {
/* 2721 */           int idx = dstOffset;
/* 2722 */           for (int i = 0; i < this.dstWidth; i++) {
/* 2723 */             float x0 = intData[idx];
/* 2724 */             float x1 = intData[idx + 1];
/* 2725 */             float x2 = intData[idx + 2];
/*      */             
/* 2727 */             this.colorConverter.toRGB(x0, x1, x2, rgb);
/*      */             
/* 2729 */             intData[idx] = (int)rgb[0];
/* 2730 */             intData[idx + 1] = (int)rgb[1];
/* 2731 */             intData[idx + 2] = (int)rgb[2];
/*      */             
/* 2733 */             idx += 3;
/*      */           } 
/*      */           
/* 2736 */           dstOffset += scanlineStride;
/*      */         } 
/* 2738 */       } else if (floatData != null) {
/* 2739 */         for (int j = 0; j < this.dstHeight; j++) {
/* 2740 */           int idx = dstOffset;
/* 2741 */           for (int i = 0; i < this.dstWidth; i++) {
/* 2742 */             float x0 = floatData[idx];
/* 2743 */             float x1 = floatData[idx + 1];
/* 2744 */             float x2 = floatData[idx + 2];
/*      */             
/* 2746 */             this.colorConverter.toRGB(x0, x1, x2, rgb);
/*      */             
/* 2748 */             floatData[idx] = rgb[0];
/* 2749 */             floatData[idx + 1] = rgb[1];
/* 2750 */             floatData[idx + 2] = rgb[2];
/*      */             
/* 2752 */             idx += 3;
/*      */           } 
/*      */           
/* 2755 */           dstOffset += scanlineStride;
/*      */         } 
/*      */       } 
/*      */     } 
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
/* 2780 */     if (this.photometricInterpretation == 0)
/*      */     {
/* 2782 */       if (byteData != null) {
/* 2783 */         int bytesPerRow = (this.srcWidth * pixelBitStride + 7) / 8;
/* 2784 */         for (int y = 0; y < this.srcHeight; y++) {
/* 2785 */           int offset = dstOffset + y * scanlineStride;
/* 2786 */           for (int i = 0; i < bytesPerRow; i++) {
/* 2787 */             byteData[offset + i] = (byte)(byteData[offset + i] ^ 0xFF);
/*      */           }
/*      */         } 
/* 2790 */       } else if (shortData != null) {
/* 2791 */         int shortsPerRow = (this.srcWidth * pixelBitStride + 15) / 16;
/* 2792 */         if (this.sampleFormat[0] == 2) {
/*      */           
/* 2794 */           for (int y = 0; y < this.srcHeight; y++) {
/* 2795 */             int offset = dstOffset + y * scanlineStride;
/* 2796 */             for (int i = 0; i < shortsPerRow; i++) {
/* 2797 */               int shortOffset = offset + i;
/*      */               
/* 2799 */               shortData[shortOffset] = (short)(32767 - shortData[shortOffset]);
/*      */             }
/*      */           
/*      */           } 
/*      */         } else {
/*      */           
/* 2805 */           for (int y = 0; y < this.srcHeight; y++) {
/* 2806 */             int offset = dstOffset + y * scanlineStride;
/* 2807 */             for (int i = 0; i < shortsPerRow; i++) {
/* 2808 */               shortData[offset + i] = (short)(shortData[offset + i] ^ 0xFFFF);
/*      */             }
/*      */           } 
/*      */         } 
/* 2812 */       } else if (intData != null) {
/* 2813 */         int intsPerRow = (this.srcWidth * pixelBitStride + 15) / 16;
/* 2814 */         for (int y = 0; y < this.srcHeight; y++) {
/* 2815 */           int offset = dstOffset + y * scanlineStride;
/* 2816 */           for (int i = 0; i < intsPerRow; i++) {
/* 2817 */             int intOffset = offset + i;
/*      */             
/* 2819 */             intData[intOffset] = Integer.MAX_VALUE - intData[intOffset];
/*      */           }
/*      */         
/*      */         } 
/* 2823 */       } else if (floatData != null) {
/* 2824 */         int floatsPerRow = (this.srcWidth * pixelBitStride + 15) / 16;
/* 2825 */         for (int y = 0; y < this.srcHeight; y++) {
/* 2826 */           int offset = dstOffset + y * scanlineStride;
/* 2827 */           for (int i = 0; i < floatsPerRow; i++) {
/* 2828 */             int floatOffset = offset + i;
/*      */             
/* 2830 */             floatData[floatOffset] = 1.0F - floatData[floatOffset];
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 2837 */     if (this.isBilevel) {
/*      */ 
/*      */       
/* 2840 */       Rectangle rect = this.isImageSimple ? new Rectangle(this.dstMinX, this.dstMinY, this.dstWidth, this.dstHeight) : ras.getBounds();
/* 2841 */       ImageUtil.setPackedBinaryData(byteData, ras, rect);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2846 */     if (isDirectCopy) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 2851 */     Raster src = this.rawImage.getRaster();
/*      */ 
/*      */     
/* 2854 */     Raster srcChild = src.createChild(0, 0, this.srcWidth, this.srcHeight, this.srcMinX, this.srcMinY, this.planar ? null : this.sourceBands);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2859 */     WritableRaster dst = this.image.getRaster();
/*      */ 
/*      */     
/* 2862 */     WritableRaster dstChild = dst.createWritableChild(this.dstMinX, this.dstMinY, this.dstWidth, this.dstHeight, this.dstMinX, this.dstMinY, this.destinationBands);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2867 */     if (this.subsampleX == 1 && this.subsampleY == 1 && !this.adjustBitDepths) {
/* 2868 */       srcChild = srcChild.createChild(this.activeSrcMinX, this.activeSrcMinY, this.activeSrcWidth, this.activeSrcHeight, this.dstMinX, this.dstMinY, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2874 */       dstChild.setRect(srcChild);
/* 2875 */     } else if (this.subsampleX == 1 && !this.adjustBitDepths) {
/* 2876 */       int sy = this.activeSrcMinY;
/* 2877 */       int dy = this.dstMinY;
/* 2878 */       while (sy < this.srcMinY + this.srcHeight) {
/* 2879 */         Raster srcRow = srcChild.createChild(this.activeSrcMinX, sy, this.activeSrcWidth, 1, this.dstMinX, dy, null);
/*      */ 
/*      */ 
/*      */         
/* 2883 */         dstChild.setRect(srcRow);
/*      */         
/* 2885 */         sy += this.subsampleY;
/* 2886 */         dy++;
/*      */       } 
/*      */     } else {
/* 2889 */       int[] p = srcChild.getPixel(this.srcMinX, this.srcMinY, (int[])null);
/* 2890 */       int numBands = p.length;
/*      */       
/* 2892 */       int sy = this.activeSrcMinY;
/* 2893 */       int dy = this.dstMinY;
/*      */       
/* 2895 */       while (sy < this.activeSrcMinY + this.activeSrcHeight) {
/* 2896 */         int sx = this.activeSrcMinX;
/* 2897 */         int dx = this.dstMinX;
/*      */         
/* 2899 */         while (sx < this.activeSrcMinX + this.activeSrcWidth) {
/* 2900 */           srcChild.getPixel(sx, sy, p);
/* 2901 */           if (this.adjustBitDepths) {
/* 2902 */             for (int band = 0; band < numBands; band++) {
/* 2903 */               p[band] = this.bitDepthScale[band][p[band]];
/*      */             }
/*      */           }
/* 2906 */           dstChild.setPixel(dx, dy, p);
/*      */           
/* 2908 */           sx += this.subsampleX;
/* 2909 */           dx++;
/*      */         } 
/*      */         
/* 2912 */         sy += this.subsampleY;
/* 2913 */         dy++;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\TIFFDecompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */