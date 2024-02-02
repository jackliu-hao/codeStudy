package com.github.jaiimageio.plugins.tiff;

import com.github.jaiimageio.impl.common.BogusColorSpace;
import com.github.jaiimageio.impl.common.ImageUtil;
import com.github.jaiimageio.impl.common.SimpleCMYKColorSpace;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

public abstract class TIFFDecompressor {
   private static final boolean DEBUG = false;
   protected ImageReader reader;
   protected IIOMetadata metadata;
   protected int photometricInterpretation;
   protected int compression;
   protected boolean planar;
   protected int samplesPerPixel;
   protected int[] bitsPerSample;
   protected int[] sampleFormat = new int[]{1};
   protected int[] extraSamples;
   protected char[] colorMap;
   protected ImageInputStream stream;
   protected long offset;
   protected int byteCount;
   protected int srcMinX;
   protected int srcMinY;
   protected int srcWidth;
   protected int srcHeight;
   protected int sourceXOffset;
   protected int dstXOffset;
   protected int sourceYOffset;
   protected int dstYOffset;
   protected int subsampleX;
   protected int subsampleY;
   protected int[] sourceBands;
   protected int[] destinationBands;
   protected BufferedImage rawImage;
   protected BufferedImage image;
   protected int dstMinX;
   protected int dstMinY;
   protected int dstWidth;
   protected int dstHeight;
   protected int activeSrcMinX;
   protected int activeSrcMinY;
   protected int activeSrcWidth;
   protected int activeSrcHeight;
   protected TIFFColorConverter colorConverter;
   boolean isBilevel;
   boolean isContiguous;
   boolean isImageSimple;
   boolean adjustBitDepths;
   int[][] bitDepthScale;
   private boolean isFirstBitDepthTable = true;
   private boolean planarCache = false;
   private int[] destBitsPerSampleCache = null;
   private int[] sourceBandsCache = null;
   private int[] bitsPerSampleCache = null;
   private int[] destinationBandsCache = null;

   static SampleModel createInterleavedSM(int dataType, int numBands) {
      int[] bandOffsets = new int[numBands];

      for(int i = 0; i < numBands; bandOffsets[i] = i++) {
      }

      return new PixelInterleavedSampleModel(dataType, 1, 1, numBands, numBands, bandOffsets);
   }

   static ColorModel createComponentCM(ColorSpace colorSpace, int numBands, int dataType, boolean hasAlpha, boolean isAlphaPremultiplied) {
      int transparency = hasAlpha ? 3 : 1;
      ComponentColorModel colorModel;
      if (dataType != 4 && dataType != 5) {
         int[] numBits = new int[numBands];
         byte bits;
         if (dataType == 0) {
            bits = 8;
         } else if (dataType != 2 && dataType != 1) {
            if (dataType != 3) {
               throw new IllegalArgumentException("dataType = " + dataType);
            }

            bits = 32;
         } else {
            bits = 16;
         }

         for(int i = 0; i < numBands; ++i) {
            numBits[i] = bits;
         }

         colorModel = new ComponentColorModel(colorSpace, numBits, hasAlpha, isAlphaPremultiplied, transparency, dataType);
      } else {
         colorModel = new ComponentColorModel(colorSpace, hasAlpha, isAlphaPremultiplied, transparency, dataType);
      }

      return colorModel;
   }

   private static int createMask(int[] bitsPerSample, int band) {
      int mask = (1 << bitsPerSample[band]) - 1;

      for(int i = band + 1; i < bitsPerSample.length; ++i) {
         mask <<= bitsPerSample[i];
      }

      return mask;
   }

   private static int getDataTypeFromNumBits(int numBits, boolean isSigned) {
      int dataType;
      if (numBits <= 8) {
         dataType = 0;
      } else if (numBits <= 16) {
         dataType = isSigned ? 2 : 1;
      } else {
         dataType = 3;
      }

      return dataType;
   }

   private static boolean areIntArraysEqual(int[] a, int[] b) {
      if (a != null && b != null) {
         if (a.length != b.length) {
            return false;
         } else {
            int length = a.length;

            for(int i = 0; i < length; ++i) {
               if (a[i] != b[i]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return a == null && b == null;
      }
   }

   private static int getDataTypeSize(int dataType) throws IIOException {
      int dataTypeSize = false;
      byte dataTypeSize;
      switch (dataType) {
         case 0:
            dataTypeSize = 8;
            break;
         case 1:
         case 2:
            dataTypeSize = 16;
            break;
         case 3:
         case 4:
            dataTypeSize = 32;
            break;
         case 5:
            dataTypeSize = 64;
            break;
         default:
            throw new IIOException("Unknown data type " + dataType);
      }

      return dataTypeSize;
   }

   private static int getBitsPerPixel(SampleModel sm) {
      int bitsPerPixel = 0;
      int[] sampleSize = sm.getSampleSize();
      int numBands = sampleSize.length;

      for(int i = 0; i < numBands; ++i) {
         bitsPerPixel += sampleSize[i];
      }

      return bitsPerPixel;
   }

   private static boolean areSampleSizesEqual(SampleModel sm) {
      boolean allSameSize = true;
      int[] sampleSize = sm.getSampleSize();
      int sampleSize0 = sampleSize[0];
      int numBands = sampleSize.length;

      for(int i = 1; i < numBands; ++i) {
         if (sampleSize[i] != sampleSize0) {
            allSameSize = false;
            break;
         }
      }

      return allSameSize;
   }

   private static boolean isDataBufferBitContiguous(SampleModel sm) throws IIOException {
      int dataTypeSize = getDataTypeSize(sm.getDataType());
      int numBands;
      if (sm instanceof ComponentSampleModel) {
         int numBands = sm.getNumBands();

         for(numBands = 0; numBands < numBands; ++numBands) {
            if (sm.getSampleSize(numBands) != dataTypeSize) {
               return false;
            }
         }
      } else if (sm instanceof MultiPixelPackedSampleModel) {
         MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sm;
         if (dataTypeSize % mppsm.getPixelBitStride() != 0) {
            return false;
         }
      } else {
         if (!(sm instanceof SinglePixelPackedSampleModel)) {
            return false;
         }

         SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
         numBands = sm.getNumBands();
         int numBits = 0;

         for(int i = 0; i < numBands; ++i) {
            numBits += sm.getSampleSize(i);
         }

         if (numBits != dataTypeSize) {
            return false;
         }
      }

      return true;
   }

   private static void reformatData(byte[] buf, int bytesPerRow, int numRows, short[] shortData, int[] intData, int outOffset, int outStride) throws IIOException {
      int inOffset;
      int intsPerRow;
      int numExtraBytes;
      int j;
      int k;
      int shift;
      if (shortData != null) {
         inOffset = 0;
         intsPerRow = bytesPerRow / 2;
         numExtraBytes = bytesPerRow % 2;

         for(j = 0; j < numRows; ++j) {
            k = outOffset;

            for(shift = 0; shift < intsPerRow; ++shift) {
               shortData[k++] = (short)((buf[inOffset++] & 255) << 8 | buf[inOffset++] & 255);
            }

            if (numExtraBytes != 0) {
               shortData[k++] = (short)((buf[inOffset++] & 255) << 8);
            }

            outOffset += outStride;
         }
      } else {
         if (intData == null) {
            throw new IIOException("shortData == null && intData == null!");
         }

         inOffset = 0;
         intsPerRow = bytesPerRow / 4;
         numExtraBytes = bytesPerRow % 4;

         for(j = 0; j < numRows; ++j) {
            k = outOffset;

            for(shift = 0; shift < intsPerRow; ++shift) {
               intData[k++] = (buf[inOffset++] & 255) << 24 | (buf[inOffset++] & 255) << 16 | (buf[inOffset++] & 255) << 8 | buf[inOffset++] & 255;
            }

            if (numExtraBytes != 0) {
               shift = 24;
               int ival = 0;

               for(int b = 0; b < numExtraBytes; ++b) {
                  ival |= (buf[inOffset++] & 255) << shift;
                  shift -= 8;
               }

               intData[k++] = ival;
            }

            outOffset += outStride;
         }
      }

   }

   private static void reformatDiscontiguousData(byte[] buf, int stride, int w, int h, WritableRaster raster) throws IOException {
      SampleModel sm = raster.getSampleModel();
      int numBands = sm.getNumBands();
      int[] sampleSize = sm.getSampleSize();
      ByteArrayInputStream is = new ByteArrayInputStream(buf);
      ImageInputStream iis = new MemoryCacheImageInputStream(is);
      long iisPosition = 0L;
      int y = raster.getMinY();

      for(int j = 0; j < h; ++y) {
         iis.seek(iisPosition);
         int x = raster.getMinX();

         for(int i = 0; i < w; ++x) {
            for(int b = 0; b < numBands; ++b) {
               long bits = iis.readBits(sampleSize[b]);
               raster.setSample(x, y, b, (int)bits);
            }

            ++i;
         }

         iisPosition += (long)stride;
         ++j;
      }

   }

   public static ImageTypeSpecifier getRawImageTypeSpecifier(int photometricInterpretation, int compression, int samplesPerPixel, int[] bitsPerSample, int[] sampleFormat, int[] extraSamples, char[] colorMap) {
      int dataType;
      boolean alphaPremultiplied;
      int blueMask;
      int totalBits;
      if (samplesPerPixel != 1 || bitsPerSample[0] != 1 && bitsPerSample[0] != 2 && bitsPerSample[0] != 4 && bitsPerSample[0] != 8 && bitsPerSample[0] != 16) {
         boolean alphaPremultiplied;
         if (samplesPerPixel == 2 && bitsPerSample[0] == 8 && bitsPerSample[1] == 8) {
            int dataType = 0;
            alphaPremultiplied = false;
            if (extraSamples != null && extraSamples[0] == 1) {
               alphaPremultiplied = true;
            }

            return ImageTypeSpecifier.createGrayscale(8, dataType, false, alphaPremultiplied);
         } else {
            boolean allSameBitDepth;
            if (samplesPerPixel == 2 && bitsPerSample[0] == 16 && bitsPerSample[1] == 16) {
               dataType = sampleFormat[0] == 2 ? 2 : 1;
               alphaPremultiplied = false;
               if (extraSamples != null && extraSamples[0] == 1) {
                  alphaPremultiplied = true;
               }

               allSameBitDepth = dataType == 2;
               return ImageTypeSpecifier.createGrayscale(16, dataType, allSameBitDepth, alphaPremultiplied);
            } else {
               ColorSpace rgb = ColorSpace.getInstance(1000);
               int[] bandOffsets;
               byte dataType;
               ColorSpace theColorSpace;
               if (samplesPerPixel == 3 && bitsPerSample[0] == 8 && bitsPerSample[1] == 8 && bitsPerSample[2] == 8) {
                  bandOffsets = new int[]{0, 1, 2};
                  dataType = 0;
                  if ((photometricInterpretation != 6 || compression == 7 || compression == 6) && photometricInterpretation != 8) {
                     theColorSpace = rgb;
                  } else {
                     theColorSpace = ColorSpace.getInstance(1004);
                  }

                  return ImageTypeSpecifier.createInterleaved(theColorSpace, bandOffsets, dataType, false, false);
               } else {
                  boolean isDataTypeSet;
                  if (samplesPerPixel == 4 && bitsPerSample[0] == 8 && bitsPerSample[1] == 8 && bitsPerSample[2] == 8 && bitsPerSample[3] == 8) {
                     bandOffsets = new int[]{0, 1, 2, 3};
                     dataType = 0;
                     alphaPremultiplied = false;
                     if (photometricInterpretation == 5) {
                        theColorSpace = SimpleCMYKColorSpace.getInstance();
                        isDataTypeSet = false;
                     } else {
                        theColorSpace = rgb;
                        isDataTypeSet = true;
                        if (extraSamples != null && extraSamples[0] == 1) {
                           alphaPremultiplied = true;
                        }
                     }

                     return ImageTypeSpecifier.createInterleaved(theColorSpace, bandOffsets, dataType, isDataTypeSet, alphaPremultiplied);
                  } else {
                     int maxBitsPerSample;
                     if (samplesPerPixel == 3 && bitsPerSample[0] == 16 && bitsPerSample[1] == 16 && bitsPerSample[2] == 16) {
                        bandOffsets = new int[]{0, 1, 2};
                        maxBitsPerSample = sampleFormat[0] == 2 ? 2 : 1;
                        return ImageTypeSpecifier.createInterleaved(rgb, bandOffsets, maxBitsPerSample, false, false);
                     } else {
                        boolean isSigned;
                        if (samplesPerPixel == 4 && bitsPerSample[0] == 16 && bitsPerSample[1] == 16 && bitsPerSample[2] == 16 && bitsPerSample[3] == 16) {
                           bandOffsets = new int[]{0, 1, 2, 3};
                           maxBitsPerSample = sampleFormat[0] == 2 ? 2 : 1;
                           isSigned = false;
                           if (extraSamples != null && extraSamples[0] == 1) {
                              isSigned = true;
                           }

                           return ImageTypeSpecifier.createInterleaved(rgb, bandOffsets, maxBitsPerSample, true, isSigned);
                        } else if (photometricInterpretation == 5 && (bitsPerSample[0] == 1 || bitsPerSample[0] == 2 || bitsPerSample[0] == 4)) {
                           ColorSpace cs = null;
                           if (samplesPerPixel == 4) {
                              cs = SimpleCMYKColorSpace.getInstance();
                           } else {
                              cs = new BogusColorSpace(samplesPerPixel);
                           }

                           ColorModel cm = new ComponentColorModel((ColorSpace)cs, bitsPerSample, false, false, 1, 0);
                           return new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
                        } else {
                           totalBits = 0;

                           for(maxBitsPerSample = 0; maxBitsPerSample < bitsPerSample.length; ++maxBitsPerSample) {
                              totalBits += bitsPerSample[maxBitsPerSample];
                           }

                           int i;
                           int redMask;
                           int dataType;
                           if (samplesPerPixel != 3 && samplesPerPixel != 4 || totalBits != 8 && totalBits != 16) {
                              boolean alphaPremultiplied;
                              SampleModel sm;
                              if (bitsPerSample[0] % 8 == 0) {
                                 allSameBitDepth = true;

                                 for(i = 1; i < bitsPerSample.length; ++i) {
                                    if (bitsPerSample[i] != bitsPerSample[i - 1]) {
                                       allSameBitDepth = false;
                                       break;
                                    }
                                 }

                                 if (allSameBitDepth) {
                                    int dataType = -1;
                                    isDataTypeSet = false;
                                    switch (bitsPerSample[0]) {
                                       case 8:
                                          if (sampleFormat[0] != 3) {
                                             dataType = 0;
                                             isDataTypeSet = true;
                                          }
                                          break;
                                       case 16:
                                          if (sampleFormat[0] != 3) {
                                             if (sampleFormat[0] == 2) {
                                                dataType = 2;
                                             } else {
                                                dataType = 1;
                                             }

                                             isDataTypeSet = true;
                                          }
                                          break;
                                       case 32:
                                          if (sampleFormat[0] == 3) {
                                             dataType = 4;
                                          } else {
                                             dataType = 3;
                                          }

                                          isDataTypeSet = true;
                                    }

                                    if (isDataTypeSet) {
                                       sm = createInterleavedSM(dataType, samplesPerPixel);
                                       ColorModel cm;
                                       if (samplesPerPixel >= 1 && samplesPerPixel <= 4 && (dataType == 3 || dataType == 4)) {
                                          ColorSpace cs = samplesPerPixel <= 2 ? ColorSpace.getInstance(1003) : rgb;
                                          boolean hasAlpha = samplesPerPixel % 2 == 0;
                                          alphaPremultiplied = false;
                                          if (hasAlpha && extraSamples != null && extraSamples[0] == 1) {
                                             alphaPremultiplied = true;
                                          }

                                          cm = createComponentCM(cs, samplesPerPixel, dataType, hasAlpha, alphaPremultiplied);
                                       } else {
                                          ColorSpace cs = new BogusColorSpace(samplesPerPixel);
                                          cm = createComponentCM(cs, samplesPerPixel, dataType, false, false);
                                       }

                                       return new ImageTypeSpecifier(cm, sm);
                                    }
                                 }
                              }

                              if (colorMap == null && sampleFormat[0] != 3) {
                                 maxBitsPerSample = 0;

                                 for(i = 0; i < bitsPerSample.length; ++i) {
                                    if (bitsPerSample[i] > maxBitsPerSample) {
                                       maxBitsPerSample = bitsPerSample[i];
                                    }
                                 }

                                 isSigned = sampleFormat[0] == 2;
                                 if (samplesPerPixel == 1) {
                                    redMask = getDataTypeFromNumBits(maxBitsPerSample, isSigned);
                                    return ImageTypeSpecifier.createGrayscale(maxBitsPerSample, redMask, isSigned);
                                 }

                                 if (samplesPerPixel == 2) {
                                    isDataTypeSet = false;
                                    if (extraSamples != null && extraSamples[0] == 1) {
                                       isDataTypeSet = true;
                                    }

                                    dataType = getDataTypeFromNumBits(maxBitsPerSample, isSigned);
                                    return ImageTypeSpecifier.createGrayscale(maxBitsPerSample, dataType, false, isDataTypeSet);
                                 }

                                 if (samplesPerPixel != 3 && samplesPerPixel != 4) {
                                    redMask = getDataTypeFromNumBits(maxBitsPerSample, isSigned);
                                    sm = createInterleavedSM(redMask, samplesPerPixel);
                                    ColorSpace cs = new BogusColorSpace(samplesPerPixel);
                                    ColorModel cm = createComponentCM(cs, samplesPerPixel, redMask, false, false);
                                    return new ImageTypeSpecifier(cm, sm);
                                 }

                                 if (totalBits <= 32 && !isSigned) {
                                    redMask = createMask(bitsPerSample, 0);
                                    dataType = createMask(bitsPerSample, 1);
                                    blueMask = createMask(bitsPerSample, 2);
                                    int alphaMask = samplesPerPixel == 4 ? createMask(bitsPerSample, 3) : 0;
                                    int transferType = getDataTypeFromNumBits(totalBits, false);
                                    alphaPremultiplied = false;
                                    if (extraSamples != null && extraSamples[0] == 1) {
                                       alphaPremultiplied = true;
                                    }

                                    return ImageTypeSpecifier.createPacked(rgb, redMask, dataType, blueMask, alphaMask, transferType, alphaPremultiplied);
                                 }

                                 int[] bandOffsets;
                                 if (samplesPerPixel == 3) {
                                    bandOffsets = new int[]{0, 1, 2};
                                    dataType = getDataTypeFromNumBits(maxBitsPerSample, isSigned);
                                    return ImageTypeSpecifier.createInterleaved(rgb, bandOffsets, dataType, false, false);
                                 }

                                 if (samplesPerPixel == 4) {
                                    bandOffsets = new int[]{0, 1, 2, 3};
                                    dataType = getDataTypeFromNumBits(maxBitsPerSample, isSigned);
                                    boolean alphaPremultiplied = false;
                                    if (extraSamples != null && extraSamples[0] == 1) {
                                       alphaPremultiplied = true;
                                    }

                                    return ImageTypeSpecifier.createInterleaved(rgb, bandOffsets, dataType, true, alphaPremultiplied);
                                 }
                              }

                              return null;
                           } else {
                              maxBitsPerSample = createMask(bitsPerSample, 0);
                              i = createMask(bitsPerSample, 1);
                              redMask = createMask(bitsPerSample, 2);
                              dataType = samplesPerPixel == 4 ? createMask(bitsPerSample, 3) : 0;
                              blueMask = totalBits == 8 ? 0 : 1;
                              boolean alphaPremultiplied = false;
                              if (extraSamples != null && extraSamples[0] == 1) {
                                 alphaPremultiplied = true;
                              }

                              return ImageTypeSpecifier.createPacked(rgb, maxBitsPerSample, i, redMask, dataType, blueMask, alphaPremultiplied);
                           }
                        }
                     }
                  }
               }
            }
         }
      } else if (colorMap == null) {
         boolean isSigned = sampleFormat[0] == 2;
         if (bitsPerSample[0] <= 8) {
            totalBits = 0;
         } else {
            totalBits = sampleFormat[0] == 2 ? 2 : 1;
         }

         return ImageTypeSpecifier.createGrayscale(bitsPerSample[0], totalBits, isSigned);
      } else {
         dataType = 1 << bitsPerSample[0];
         byte[] redLut = new byte[dataType];
         byte[] greenLut = new byte[dataType];
         byte[] blueLut = new byte[dataType];
         byte[] alphaLut = null;
         alphaPremultiplied = false;

         for(blueMask = 0; blueMask < dataType; ++blueMask) {
            redLut[blueMask] = (byte)(colorMap[blueMask] * 255 / '\uffff');
            greenLut[blueMask] = (byte)(colorMap[dataType + blueMask] * 255 / '\uffff');
            blueLut[blueMask] = (byte)(colorMap[2 * dataType + blueMask] * 255 / '\uffff');
         }

         blueMask = bitsPerSample[0] == 8 ? 0 : 1;
         return ImageTypeSpecifier.createIndexed(redLut, greenLut, blueLut, (byte[])alphaLut, bitsPerSample[0], blueMask);
      }
   }

   public void setReader(ImageReader reader) {
      this.reader = reader;
   }

   public void setMetadata(IIOMetadata metadata) {
      this.metadata = metadata;
   }

   public void setPhotometricInterpretation(int photometricInterpretation) {
      this.photometricInterpretation = photometricInterpretation;
   }

   public void setCompression(int compression) {
      this.compression = compression;
   }

   public void setPlanar(boolean planar) {
      this.planar = planar;
   }

   public void setSamplesPerPixel(int samplesPerPixel) {
      this.samplesPerPixel = samplesPerPixel;
   }

   public void setBitsPerSample(int[] bitsPerSample) {
      this.bitsPerSample = bitsPerSample == null ? null : (int[])((int[])bitsPerSample.clone());
   }

   public void setSampleFormat(int[] sampleFormat) {
      this.sampleFormat = sampleFormat == null ? new int[]{1} : (int[])((int[])sampleFormat.clone());
   }

   public void setExtraSamples(int[] extraSamples) {
      this.extraSamples = extraSamples == null ? null : (int[])((int[])extraSamples.clone());
   }

   public void setColorMap(char[] colorMap) {
      this.colorMap = colorMap == null ? null : (char[])((char[])colorMap.clone());
   }

   public void setStream(ImageInputStream stream) {
      this.stream = stream;
   }

   public void setOffset(long offset) {
      this.offset = offset;
   }

   public void setByteCount(int byteCount) {
      this.byteCount = byteCount;
   }

   public void setSrcMinX(int srcMinX) {
      this.srcMinX = srcMinX;
   }

   public void setSrcMinY(int srcMinY) {
      this.srcMinY = srcMinY;
   }

   public void setSrcWidth(int srcWidth) {
      this.srcWidth = srcWidth;
   }

   public void setSrcHeight(int srcHeight) {
      this.srcHeight = srcHeight;
   }

   public void setSourceXOffset(int sourceXOffset) {
      this.sourceXOffset = sourceXOffset;
   }

   public void setDstXOffset(int dstXOffset) {
      this.dstXOffset = dstXOffset;
   }

   public void setSourceYOffset(int sourceYOffset) {
      this.sourceYOffset = sourceYOffset;
   }

   public void setDstYOffset(int dstYOffset) {
      this.dstYOffset = dstYOffset;
   }

   public void setSubsampleX(int subsampleX) {
      if (subsampleX <= 0) {
         throw new IllegalArgumentException("subsampleX <= 0!");
      } else {
         this.subsampleX = subsampleX;
      }
   }

   public void setSubsampleY(int subsampleY) {
      if (subsampleY <= 0) {
         throw new IllegalArgumentException("subsampleY <= 0!");
      } else {
         this.subsampleY = subsampleY;
      }
   }

   public void setSourceBands(int[] sourceBands) {
      this.sourceBands = sourceBands == null ? null : (int[])((int[])sourceBands.clone());
   }

   public void setDestinationBands(int[] destinationBands) {
      this.destinationBands = destinationBands == null ? null : (int[])((int[])destinationBands.clone());
   }

   public void setImage(BufferedImage image) {
      this.image = image;
   }

   public void setDstMinX(int dstMinX) {
      this.dstMinX = dstMinX;
   }

   public void setDstMinY(int dstMinY) {
      this.dstMinY = dstMinY;
   }

   public void setDstWidth(int dstWidth) {
      this.dstWidth = dstWidth;
   }

   public void setDstHeight(int dstHeight) {
      this.dstHeight = dstHeight;
   }

   public void setActiveSrcMinX(int activeSrcMinX) {
      this.activeSrcMinX = activeSrcMinX;
   }

   public void setActiveSrcMinY(int activeSrcMinY) {
      this.activeSrcMinY = activeSrcMinY;
   }

   public void setActiveSrcWidth(int activeSrcWidth) {
      this.activeSrcWidth = activeSrcWidth;
   }

   public void setActiveSrcHeight(int activeSrcHeight) {
      this.activeSrcHeight = activeSrcHeight;
   }

   public void setColorConverter(TIFFColorConverter colorConverter) {
      this.colorConverter = colorConverter;
   }

   public ImageTypeSpecifier getRawImageType() {
      ImageTypeSpecifier its = getRawImageTypeSpecifier(this.photometricInterpretation, this.compression, this.samplesPerPixel, this.bitsPerSample, this.sampleFormat, this.extraSamples, this.colorMap);
      return its;
   }

   public BufferedImage createRawImage() {
      if (this.planar) {
         int bps = this.bitsPerSample[this.sourceBands[0]];
         byte dataType;
         if (this.sampleFormat[0] == 3) {
            dataType = 4;
         } else if (bps <= 8) {
            dataType = 0;
         } else if (bps <= 16) {
            if (this.sampleFormat[0] == 2) {
               dataType = 2;
            } else {
               dataType = 1;
            }
         } else {
            dataType = 3;
         }

         ColorSpace csGray = ColorSpace.getInstance(1003);
         ImageTypeSpecifier its = null;
         if (bps != 1 && bps != 2 && bps != 4) {
            its = ImageTypeSpecifier.createInterleaved(csGray, new int[]{0}, dataType, false, false);
         } else {
            int size = 1 << bps;
            byte[] r = new byte[size];
            byte[] g = new byte[size];
            byte[] b = new byte[size];

            for(int j = 0; j < r.length; ++j) {
               r[j] = 0;
               g[j] = 0;
               b[j] = 0;
            }

            ColorModel cmGray = new IndexColorModel(bps, size, r, g, b);
            SampleModel smGray = new MultiPixelPackedSampleModel(0, 1, 1, bps);
            its = new ImageTypeSpecifier(cmGray, smGray);
         }

         return its.createBufferedImage(this.srcWidth, this.srcHeight);
      } else {
         ImageTypeSpecifier its = this.getRawImageType();
         if (its == null) {
            return null;
         } else {
            BufferedImage bi = its.createBufferedImage(this.srcWidth, this.srcHeight);
            return bi;
         }
      }
   }

   public abstract void decodeRaw(byte[] var1, int var2, int var3, int var4) throws IOException;

   public void decodeRaw(short[] s, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
      int bytesPerRow = (this.srcWidth * bitsPerPixel + 7) / 8;
      int shortsPerRow = bytesPerRow / 2;
      byte[] b = new byte[bytesPerRow * this.srcHeight];
      this.decodeRaw((byte[])b, 0, bitsPerPixel, bytesPerRow);
      int bOffset = 0;
      int j;
      int i;
      short hiVal;
      short loVal;
      short sval;
      if (this.stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
         for(j = 0; j < this.srcHeight; ++j) {
            for(i = 0; i < shortsPerRow; ++i) {
               hiVal = (short)b[bOffset++];
               loVal = (short)b[bOffset++];
               sval = (short)(hiVal << 8 | loVal & 255);
               s[dstOffset + i] = sval;
            }

            dstOffset += scanlineStride;
         }
      } else {
         for(j = 0; j < this.srcHeight; ++j) {
            for(i = 0; i < shortsPerRow; ++i) {
               hiVal = (short)b[bOffset++];
               loVal = (short)b[bOffset++];
               sval = (short)(loVal << 8 | hiVal & 255);
               s[dstOffset + i] = sval;
            }

            dstOffset += scanlineStride;
         }
      }

   }

   public void decodeRaw(int[] i, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
      int numBands = bitsPerPixel / 32;
      int intsPerRow = this.srcWidth * numBands;
      int bytesPerRow = intsPerRow * 4;
      byte[] b = new byte[bytesPerRow * this.srcHeight];
      this.decodeRaw((byte[])b, 0, bitsPerPixel, bytesPerRow);
      int bOffset = 0;
      int j;
      int k;
      int v0;
      int v1;
      int v2;
      int v3;
      int ival;
      if (this.stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
         for(j = 0; j < this.srcHeight; ++j) {
            for(k = 0; k < intsPerRow; ++k) {
               v0 = b[bOffset++] & 255;
               v1 = b[bOffset++] & 255;
               v2 = b[bOffset++] & 255;
               v3 = b[bOffset++] & 255;
               ival = v0 << 24 | v1 << 16 | v2 << 8 | v3;
               i[dstOffset + k] = ival;
            }

            dstOffset += scanlineStride;
         }
      } else {
         for(j = 0; j < this.srcHeight; ++j) {
            for(k = 0; k < intsPerRow; ++k) {
               v0 = b[bOffset++] & 255;
               v1 = b[bOffset++] & 255;
               v2 = b[bOffset++] & 255;
               v3 = b[bOffset++] & 255;
               ival = v3 << 24 | v2 << 16 | v1 << 8 | v0;
               i[dstOffset + k] = ival;
            }

            dstOffset += scanlineStride;
         }
      }

   }

   public void decodeRaw(float[] f, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
      int numBands = bitsPerPixel / 32;
      int floatsPerRow = this.srcWidth * numBands;
      int bytesPerRow = floatsPerRow * 4;
      byte[] b = new byte[bytesPerRow * this.srcHeight];
      this.decodeRaw((byte[])b, 0, bitsPerPixel, bytesPerRow);
      int bOffset = 0;
      int j;
      int i;
      int v0;
      int v1;
      int v2;
      int v3;
      int ival;
      float fval;
      if (this.stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
         for(j = 0; j < this.srcHeight; ++j) {
            for(i = 0; i < floatsPerRow; ++i) {
               v0 = b[bOffset++] & 255;
               v1 = b[bOffset++] & 255;
               v2 = b[bOffset++] & 255;
               v3 = b[bOffset++] & 255;
               ival = v0 << 24 | v1 << 16 | v2 << 8 | v3;
               fval = Float.intBitsToFloat(ival);
               f[dstOffset + i] = fval;
            }

            dstOffset += scanlineStride;
         }
      } else {
         for(j = 0; j < this.srcHeight; ++j) {
            for(i = 0; i < floatsPerRow; ++i) {
               v0 = b[bOffset++] & 255;
               v1 = b[bOffset++] & 255;
               v2 = b[bOffset++] & 255;
               v3 = b[bOffset++] & 255;
               ival = v3 << 24 | v2 << 16 | v1 << 8 | v0;
               fval = Float.intBitsToFloat(ival);
               f[dstOffset + i] = fval;
            }

            dstOffset += scanlineStride;
         }
      }

   }

   public void beginDecoding() {
      this.adjustBitDepths = false;
      int numBands = this.destinationBands.length;
      int[] destBitsPerSample = null;
      int b;
      int maxInSample;
      int i;
      int[] destBitsPerSample;
      if (this.planar) {
         b = this.bitsPerSample.length;
         destBitsPerSample = new int[b];
         maxInSample = this.image.getSampleModel().getSampleSize(0);

         for(i = 0; i < b; ++i) {
            destBitsPerSample[i] = maxInSample;
         }
      } else {
         destBitsPerSample = this.image.getSampleModel().getSampleSize();
      }

      if (this.photometricInterpretation != 5 || this.bitsPerSample[0] != 1 && this.bitsPerSample[0] != 2 && this.bitsPerSample[0] != 4) {
         for(b = 0; b < numBands; ++b) {
            if (destBitsPerSample[this.destinationBands[b]] != this.bitsPerSample[this.sourceBands[b]]) {
               this.adjustBitDepths = true;
               break;
            }
         }
      }

      if (this.adjustBitDepths) {
         if (this.isFirstBitDepthTable || this.planar != this.planarCache || !areIntArraysEqual(destBitsPerSample, this.destBitsPerSampleCache) || !areIntArraysEqual(this.sourceBands, this.sourceBandsCache) || !areIntArraysEqual(this.bitsPerSample, this.bitsPerSampleCache) || !areIntArraysEqual(this.destinationBands, this.destinationBandsCache)) {
            this.isFirstBitDepthTable = false;
            this.planarCache = this.planar;
            this.destBitsPerSampleCache = (int[])((int[])destBitsPerSample.clone());
            this.sourceBandsCache = this.sourceBands == null ? null : (int[])((int[])this.sourceBands.clone());
            this.bitsPerSampleCache = this.bitsPerSample == null ? null : (int[])((int[])this.bitsPerSample.clone());
            this.destinationBandsCache = this.destinationBands == null ? null : (int[])((int[])this.destinationBands.clone());
            this.bitDepthScale = new int[numBands][];

            for(b = 0; b < numBands; ++b) {
               maxInSample = (1 << this.bitsPerSample[this.sourceBands[b]]) - 1;
               i = maxInSample / 2;
               int maxOutSample = (1 << destBitsPerSample[this.destinationBands[b]]) - 1;
               this.bitDepthScale[b] = new int[maxInSample + 1];

               for(int s = 0; s <= maxInSample; ++s) {
                  this.bitDepthScale[b][s] = (s * maxOutSample + i) / maxInSample;
               }
            }
         }
      } else {
         this.bitDepthScale = (int[][])null;
      }

      boolean sourceBandsNormal = false;
      boolean destinationBandsNormal = false;
      if (numBands == this.samplesPerPixel) {
         sourceBandsNormal = true;
         destinationBandsNormal = true;

         for(i = 0; i < numBands; ++i) {
            if (this.sourceBands[i] != i) {
               sourceBandsNormal = false;
            }

            if (this.destinationBands[i] != i) {
               destinationBandsNormal = false;
            }
         }
      }

      this.isBilevel = ImageUtil.isBinary(this.image.getRaster().getSampleModel());
      this.isContiguous = this.isBilevel ? true : ImageUtil.imageIsContiguous(this.image);
      this.isImageSimple = this.colorConverter == null && this.subsampleX == 1 && this.subsampleY == 1 && this.srcWidth == this.dstWidth && this.srcHeight == this.dstHeight && this.dstMinX + this.dstWidth <= this.image.getWidth() && this.dstMinY + this.dstHeight <= this.image.getHeight() && sourceBandsNormal && destinationBandsNormal && !this.adjustBitDepths;
   }

   public void decode() throws IOException {
      byte[] byteData = null;
      short[] shortData = null;
      int[] intData = null;
      float[] floatData = null;
      int dstOffset = 0;
      int pixelBitStride = 1;
      int scanlineStride = 0;
      this.rawImage = null;
      if (this.isImageSimple) {
         if (this.isBilevel) {
            this.rawImage = this.image;
         } else if (this.isContiguous) {
            this.rawImage = this.image.getSubimage(this.dstMinX, this.dstMinY, this.dstWidth, this.dstHeight);
         }
      }

      boolean isDirectCopy = this.rawImage != null;
      if (this.rawImage == null) {
         this.rawImage = this.createRawImage();
         if (this.rawImage == null) {
            throw new IIOException("Couldn't create image buffer!");
         }
      }

      WritableRaster ras = this.rawImage.getRaster();
      Rectangle rect;
      SampleModel sm;
      if (this.isBilevel) {
         rect = this.isImageSimple ? new Rectangle(this.dstMinX, this.dstMinY, this.dstWidth, this.dstHeight) : ras.getBounds();
         byteData = ImageUtil.getPackedBinaryData(ras, rect);
         dstOffset = 0;
         pixelBitStride = 1;
         scanlineStride = (rect.width + 7) / 8;
      } else {
         sm = ras.getSampleModel();
         DataBuffer db = ras.getDataBuffer();
         boolean isSupportedType = false;
         DataBufferByte dbb;
         DataBufferUShort dbus;
         DataBufferInt dbi;
         if (sm instanceof ComponentSampleModel) {
            ComponentSampleModel csm = (ComponentSampleModel)sm;
            dstOffset = csm.getOffset(-ras.getSampleModelTranslateX(), -ras.getSampleModelTranslateY());
            scanlineStride = csm.getScanlineStride();
            if (db instanceof DataBufferByte) {
               dbb = (DataBufferByte)db;
               byteData = dbb.getData();
               pixelBitStride = csm.getPixelStride() * 8;
               isSupportedType = true;
            } else if (db instanceof DataBufferUShort) {
               dbus = (DataBufferUShort)db;
               shortData = dbus.getData();
               pixelBitStride = csm.getPixelStride() * 16;
               isSupportedType = true;
            } else if (db instanceof DataBufferShort) {
               DataBufferShort dbs = (DataBufferShort)db;
               shortData = dbs.getData();
               pixelBitStride = csm.getPixelStride() * 16;
               isSupportedType = true;
            } else if (db instanceof DataBufferInt) {
               dbi = (DataBufferInt)db;
               intData = dbi.getData();
               pixelBitStride = csm.getPixelStride() * 32;
               isSupportedType = true;
            } else if (db instanceof DataBufferFloat) {
               DataBufferFloat dbf = (DataBufferFloat)db;
               floatData = dbf.getData();
               pixelBitStride = csm.getPixelStride() * 32;
               isSupportedType = true;
            }
         } else if (sm instanceof MultiPixelPackedSampleModel) {
            MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sm;
            dstOffset = mppsm.getOffset(-ras.getSampleModelTranslateX(), -ras.getSampleModelTranslateY());
            pixelBitStride = mppsm.getPixelBitStride();
            scanlineStride = mppsm.getScanlineStride();
            if (db instanceof DataBufferByte) {
               dbb = (DataBufferByte)db;
               byteData = dbb.getData();
               isSupportedType = true;
            } else if (db instanceof DataBufferUShort) {
               dbus = (DataBufferUShort)db;
               shortData = dbus.getData();
               isSupportedType = true;
            } else if (db instanceof DataBufferInt) {
               dbi = (DataBufferInt)db;
               intData = dbi.getData();
               isSupportedType = true;
            }
         } else if (sm instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
            dstOffset = sppsm.getOffset(-ras.getSampleModelTranslateX(), -ras.getSampleModelTranslateY());
            scanlineStride = sppsm.getScanlineStride();
            if (db instanceof DataBufferByte) {
               dbb = (DataBufferByte)db;
               byteData = dbb.getData();
               pixelBitStride = 8;
               isSupportedType = true;
            } else if (db instanceof DataBufferUShort) {
               dbus = (DataBufferUShort)db;
               shortData = dbus.getData();
               pixelBitStride = 16;
               isSupportedType = true;
            } else if (db instanceof DataBufferInt) {
               dbi = (DataBufferInt)db;
               intData = dbi.getData();
               pixelBitStride = 32;
               isSupportedType = true;
            }
         }

         if (!isSupportedType) {
            throw new IIOException("Unsupported raw image type: SampleModel = " + sm + "; DataBuffer = " + db);
         }
      }

      int y;
      int offset;
      if (this.isBilevel) {
         this.decodeRaw(byteData, dstOffset, pixelBitStride, scanlineStride);
      } else {
         sm = ras.getSampleModel();
         byte[] buf;
         if (isDataBufferBitContiguous(sm)) {
            if (byteData != null) {
               this.decodeRaw(byteData, dstOffset, pixelBitStride, scanlineStride);
            } else if (floatData != null) {
               this.decodeRaw(floatData, dstOffset, pixelBitStride, scanlineStride);
            } else if (shortData != null) {
               if (areSampleSizesEqual(sm) && sm.getSampleSize(0) == 16) {
                  this.decodeRaw(shortData, dstOffset, pixelBitStride, scanlineStride);
               } else {
                  y = getBitsPerPixel(sm);
                  offset = (y * this.srcWidth + 7) / 8;
                  buf = new byte[offset * this.srcHeight];
                  this.decodeRaw((byte[])buf, 0, y, offset);
                  reformatData(buf, offset, this.srcHeight, shortData, (int[])null, dstOffset, scanlineStride);
               }
            } else if (intData != null) {
               if (areSampleSizesEqual(sm) && sm.getSampleSize(0) == 32) {
                  this.decodeRaw(intData, dstOffset, pixelBitStride, scanlineStride);
               } else {
                  y = getBitsPerPixel(sm);
                  offset = (y * this.srcWidth + 7) / 8;
                  buf = new byte[offset * this.srcHeight];
                  this.decodeRaw((byte[])buf, 0, y, offset);
                  reformatData(buf, offset, this.srcHeight, (short[])null, intData, dstOffset, scanlineStride);
               }
            }
         } else {
            y = getBitsPerPixel(sm);
            offset = (y * this.srcWidth + 7) / 8;
            buf = new byte[offset * this.srcHeight];
            this.decodeRaw((byte[])buf, 0, y, offset);
            reformatDiscontiguousData(buf, offset, this.srcWidth, this.srcHeight, ras);
         }
      }

      int i;
      if (this.colorConverter != null) {
         float[] rgb = new float[3];
         float x1;
         float x2;
         float x0;
         if (byteData != null) {
            for(y = 0; y < this.dstHeight; ++y) {
               offset = dstOffset;

               for(i = 0; i < this.dstWidth; ++i) {
                  x0 = (float)(byteData[offset] & 255);
                  x1 = (float)(byteData[offset + 1] & 255);
                  x2 = (float)(byteData[offset + 2] & 255);
                  this.colorConverter.toRGB(x0, x1, x2, rgb);
                  byteData[offset] = (byte)((int)rgb[0]);
                  byteData[offset + 1] = (byte)((int)rgb[1]);
                  byteData[offset + 2] = (byte)((int)rgb[2]);
                  offset += 3;
               }

               dstOffset += scanlineStride;
            }
         } else if (shortData != null) {
            if (this.sampleFormat[0] == 2) {
               for(y = 0; y < this.dstHeight; ++y) {
                  offset = dstOffset;

                  for(i = 0; i < this.dstWidth; ++i) {
                     x0 = (float)shortData[offset];
                     x1 = (float)shortData[offset + 1];
                     x2 = (float)shortData[offset + 2];
                     this.colorConverter.toRGB(x0, x1, x2, rgb);
                     shortData[offset] = (short)((int)rgb[0]);
                     shortData[offset + 1] = (short)((int)rgb[1]);
                     shortData[offset + 2] = (short)((int)rgb[2]);
                     offset += 3;
                  }

                  dstOffset += scanlineStride;
               }
            } else {
               for(y = 0; y < this.dstHeight; ++y) {
                  offset = dstOffset;

                  for(i = 0; i < this.dstWidth; ++i) {
                     x0 = (float)(shortData[offset] & '\uffff');
                     x1 = (float)(shortData[offset + 1] & '\uffff');
                     x2 = (float)(shortData[offset + 2] & '\uffff');
                     this.colorConverter.toRGB(x0, x1, x2, rgb);
                     shortData[offset] = (short)((int)rgb[0]);
                     shortData[offset + 1] = (short)((int)rgb[1]);
                     shortData[offset + 2] = (short)((int)rgb[2]);
                     offset += 3;
                  }

                  dstOffset += scanlineStride;
               }
            }
         } else if (intData != null) {
            for(y = 0; y < this.dstHeight; ++y) {
               offset = dstOffset;

               for(i = 0; i < this.dstWidth; ++i) {
                  x0 = (float)intData[offset];
                  x1 = (float)intData[offset + 1];
                  x2 = (float)intData[offset + 2];
                  this.colorConverter.toRGB(x0, x1, x2, rgb);
                  intData[offset] = (int)rgb[0];
                  intData[offset + 1] = (int)rgb[1];
                  intData[offset + 2] = (int)rgb[2];
                  offset += 3;
               }

               dstOffset += scanlineStride;
            }
         } else if (floatData != null) {
            for(y = 0; y < this.dstHeight; ++y) {
               offset = dstOffset;

               for(i = 0; i < this.dstWidth; ++i) {
                  x0 = floatData[offset];
                  x1 = floatData[offset + 1];
                  x2 = floatData[offset + 2];
                  this.colorConverter.toRGB(x0, x1, x2, rgb);
                  floatData[offset] = rgb[0];
                  floatData[offset + 1] = rgb[1];
                  floatData[offset + 2] = rgb[2];
                  offset += 3;
               }

               dstOffset += scanlineStride;
            }
         }
      }

      int floatOffset;
      if (this.photometricInterpretation == 0) {
         int floatsPerRow;
         if (byteData != null) {
            floatsPerRow = (this.srcWidth * pixelBitStride + 7) / 8;

            for(y = 0; y < this.srcHeight; ++y) {
               offset = dstOffset + y * scanlineStride;

               for(i = 0; i < floatsPerRow; ++i) {
                  byteData[offset + i] = (byte)(byteData[offset + i] ^ 255);
               }
            }
         } else if (shortData != null) {
            floatsPerRow = (this.srcWidth * pixelBitStride + 15) / 16;
            if (this.sampleFormat[0] == 2) {
               for(y = 0; y < this.srcHeight; ++y) {
                  offset = dstOffset + y * scanlineStride;

                  for(i = 0; i < floatsPerRow; ++i) {
                     floatOffset = offset + i;
                     shortData[floatOffset] = (short)(32767 - shortData[floatOffset]);
                  }
               }
            } else {
               for(y = 0; y < this.srcHeight; ++y) {
                  offset = dstOffset + y * scanlineStride;

                  for(i = 0; i < floatsPerRow; ++i) {
                     shortData[offset + i] = (short)(shortData[offset + i] ^ '\uffff');
                  }
               }
            }
         } else if (intData != null) {
            floatsPerRow = (this.srcWidth * pixelBitStride + 15) / 16;

            for(y = 0; y < this.srcHeight; ++y) {
               offset = dstOffset + y * scanlineStride;

               for(i = 0; i < floatsPerRow; ++i) {
                  floatOffset = offset + i;
                  intData[floatOffset] = Integer.MAX_VALUE - intData[floatOffset];
               }
            }
         } else if (floatData != null) {
            floatsPerRow = (this.srcWidth * pixelBitStride + 15) / 16;

            for(y = 0; y < this.srcHeight; ++y) {
               offset = dstOffset + y * scanlineStride;

               for(i = 0; i < floatsPerRow; ++i) {
                  floatOffset = offset + i;
                  floatData[floatOffset] = 1.0F - floatData[floatOffset];
               }
            }
         }
      }

      if (this.isBilevel) {
         rect = this.isImageSimple ? new Rectangle(this.dstMinX, this.dstMinY, this.dstWidth, this.dstHeight) : ras.getBounds();
         ImageUtil.setPackedBinaryData(byteData, ras, rect);
      }

      if (!isDirectCopy) {
         Raster src = this.rawImage.getRaster();
         Raster srcChild = src.createChild(0, 0, this.srcWidth, this.srcHeight, this.srcMinX, this.srcMinY, this.planar ? null : this.sourceBands);
         WritableRaster dst = this.image.getRaster();
         WritableRaster dstChild = dst.createWritableChild(this.dstMinX, this.dstMinY, this.dstWidth, this.dstHeight, this.dstMinX, this.dstMinY, this.destinationBands);
         if (this.subsampleX == 1 && this.subsampleY == 1 && !this.adjustBitDepths) {
            srcChild = srcChild.createChild(this.activeSrcMinX, this.activeSrcMinY, this.activeSrcWidth, this.activeSrcHeight, this.dstMinX, this.dstMinY, (int[])null);
            dstChild.setRect(srcChild);
         } else {
            int numBands;
            if (this.subsampleX == 1 && !this.adjustBitDepths) {
               floatOffset = this.activeSrcMinY;

               for(numBands = this.dstMinY; floatOffset < this.srcMinY + this.srcHeight; ++numBands) {
                  Raster srcRow = srcChild.createChild(this.activeSrcMinX, floatOffset, this.activeSrcWidth, 1, this.dstMinX, numBands, (int[])null);
                  dstChild.setRect(srcRow);
                  floatOffset += this.subsampleY;
               }
            } else {
               int[] p = srcChild.getPixel(this.srcMinX, this.srcMinY, (int[])null);
               numBands = p.length;
               int sy = this.activeSrcMinY;

               for(int dy = this.dstMinY; sy < this.activeSrcMinY + this.activeSrcHeight; ++dy) {
                  int sx = this.activeSrcMinX;

                  for(int dx = this.dstMinX; sx < this.activeSrcMinX + this.activeSrcWidth; ++dx) {
                     srcChild.getPixel(sx, sy, p);
                     if (this.adjustBitDepths) {
                        for(int band = 0; band < numBands; ++band) {
                           p[band] = this.bitDepthScale[band][p[band]];
                        }
                     }

                     dstChild.setPixel(dx, dy, p);
                     sx += this.subsampleX;
                  }

                  sy += this.subsampleY;
               }
            }
         }

      }
   }
}
