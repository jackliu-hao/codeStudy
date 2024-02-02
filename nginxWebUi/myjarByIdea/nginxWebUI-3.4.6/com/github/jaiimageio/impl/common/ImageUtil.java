package com.github.jaiimageio.impl.common;

import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public class ImageUtil {
   public static final ColorModel createColorModel(SampleModel sampleModel) {
      if (sampleModel == null) {
         throw new IllegalArgumentException("sampleModel == null!");
      } else {
         int dataType = sampleModel.getDataType();
         switch (dataType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
               ColorModel colorModel = null;
               int[] sampleSize = sampleModel.getSampleSize();
               int bitsPerSample;
               boolean hasAlpha;
               boolean isAlphaPremultiplied;
               int bmask;
               if (sampleModel instanceof ComponentSampleModel) {
                  bitsPerSample = sampleModel.getNumBands();
                  ColorSpace colorSpace = null;
                  if (bitsPerSample <= 2) {
                     colorSpace = ColorSpace.getInstance(1003);
                  } else if (bitsPerSample <= 4) {
                     colorSpace = ColorSpace.getInstance(1000);
                  } else {
                     colorSpace = new BogusColorSpace(bitsPerSample);
                  }

                  hasAlpha = bitsPerSample == 2 || bitsPerSample == 4;
                  isAlphaPremultiplied = false;
                  bmask = hasAlpha ? 3 : 1;
                  colorModel = new ComponentColorModel((ColorSpace)colorSpace, sampleSize, hasAlpha, isAlphaPremultiplied, bmask, dataType);
               } else {
                  int gmask;
                  if (sampleModel.getNumBands() <= 4 && sampleModel instanceof SinglePixelPackedSampleModel) {
                     SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sampleModel;
                     int[] bitMasks = sppsm.getBitMasks();
                     hasAlpha = false;
                     isAlphaPremultiplied = false;
                     int bmask = false;
                     int amask = 0;
                     int numBands = bitMasks.length;
                     int rmask;
                     if (numBands <= 2) {
                        rmask = gmask = bmask = bitMasks[0];
                        if (numBands == 2) {
                           amask = bitMasks[1];
                        }
                     } else {
                        rmask = bitMasks[0];
                        gmask = bitMasks[1];
                        bmask = bitMasks[2];
                        if (numBands == 4) {
                           amask = bitMasks[3];
                        }
                     }

                     int bits = 0;

                     for(int i = 0; i < sampleSize.length; ++i) {
                        bits += sampleSize[i];
                     }

                     return new DirectColorModel(bits, rmask, gmask, bmask, amask);
                  }

                  if (sampleModel instanceof MultiPixelPackedSampleModel) {
                     bitsPerSample = sampleSize[0];
                     int numEntries = 1 << bitsPerSample;
                     byte[] map = new byte[numEntries];

                     for(gmask = 0; gmask < numEntries; ++gmask) {
                        map[gmask] = (byte)(gmask * 255 / (numEntries - 1));
                     }

                     colorModel = new IndexColorModel(bitsPerSample, numEntries, map, map, map);
                  }
               }

               return (ColorModel)colorModel;
            default:
               return null;
         }
      }
   }

   public static byte[] getPackedBinaryData(Raster raster, Rectangle rect) {
      SampleModel sm = raster.getSampleModel();
      if (!isBinary(sm)) {
         throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
      } else {
         int rectX = rect.x;
         int rectY = rect.y;
         int rectWidth = rect.width;
         int rectHeight = rect.height;
         DataBuffer dataBuffer = raster.getDataBuffer();
         int dx = rectX - raster.getSampleModelTranslateX();
         int dy = rectY - raster.getSampleModelTranslateY();
         MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
         int lineStride = mpp.getScanlineStride();
         int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
         int bitOffset = mpp.getBitOffset(dx);
         int numBytesPerRow = (rectWidth + 7) / 8;
         if (dataBuffer instanceof DataBufferByte && eltOffset == 0 && bitOffset == 0 && numBytesPerRow == lineStride && ((DataBufferByte)dataBuffer).getData().length == numBytesPerRow * rectHeight) {
            return ((DataBufferByte)dataBuffer).getData();
         } else {
            byte[] binaryDataArray = new byte[numBytesPerRow * rectHeight];
            int b = 0;
            byte[] data;
            int leftShift;
            int xRemaining;
            int i;
            int shift;
            short[] data;
            int[] data;
            if (bitOffset == 0) {
               if (dataBuffer instanceof DataBufferByte) {
                  data = ((DataBufferByte)dataBuffer).getData();
                  leftShift = numBytesPerRow;
                  xRemaining = 0;

                  for(i = 0; i < rectHeight; ++i) {
                     System.arraycopy(data, eltOffset, binaryDataArray, xRemaining, leftShift);
                     xRemaining += leftShift;
                     eltOffset += lineStride;
                  }
               } else if (!(dataBuffer instanceof DataBufferShort) && !(dataBuffer instanceof DataBufferUShort)) {
                  if (dataBuffer instanceof DataBufferInt) {
                     data = ((DataBufferInt)dataBuffer).getData();

                     for(leftShift = 0; leftShift < rectHeight; ++leftShift) {
                        xRemaining = rectWidth;

                        for(i = eltOffset; xRemaining > 24; xRemaining -= 32) {
                           shift = data[i++];
                           binaryDataArray[b++] = (byte)(shift >>> 24 & 255);
                           binaryDataArray[b++] = (byte)(shift >>> 16 & 255);
                           binaryDataArray[b++] = (byte)(shift >>> 8 & 255);
                           binaryDataArray[b++] = (byte)(shift & 255);
                        }

                        for(shift = 24; xRemaining > 0; xRemaining -= 8) {
                           binaryDataArray[b++] = (byte)(data[i] >>> shift & 255);
                           shift -= 8;
                        }

                        eltOffset += lineStride;
                     }
                  }
               } else {
                  data = dataBuffer instanceof DataBufferShort ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();

                  for(leftShift = 0; leftShift < rectHeight; ++leftShift) {
                     xRemaining = rectWidth;

                     for(i = eltOffset; xRemaining > 8; xRemaining -= 16) {
                        shift = data[i++];
                        binaryDataArray[b++] = (byte)(shift >>> 8 & 255);
                        binaryDataArray[b++] = (byte)(shift & 255);
                     }

                     if (xRemaining > 0) {
                        binaryDataArray[b++] = (byte)(data[i] >>> 8 & 255);
                     }

                     eltOffset += lineStride;
                  }
               }
            } else {
               int mod;
               if (dataBuffer instanceof DataBufferByte) {
                  data = ((DataBufferByte)dataBuffer).getData();
                  if ((bitOffset & 7) == 0) {
                     leftShift = numBytesPerRow;
                     xRemaining = 0;

                     for(i = 0; i < rectHeight; ++i) {
                        System.arraycopy(data, eltOffset, binaryDataArray, xRemaining, leftShift);
                        xRemaining += leftShift;
                        eltOffset += lineStride;
                     }
                  } else {
                     leftShift = bitOffset & 7;
                     xRemaining = 8 - leftShift;

                     for(i = 0; i < rectHeight; ++i) {
                        shift = eltOffset;

                        for(mod = rectWidth; mod > 0; mod -= 8) {
                           if (mod > xRemaining) {
                              binaryDataArray[b++] = (byte)((data[shift++] & 255) << leftShift | (data[shift] & 255) >>> xRemaining);
                           } else {
                              binaryDataArray[b++] = (byte)((data[shift] & 255) << leftShift);
                           }
                        }

                        eltOffset += lineStride;
                     }
                  }
               } else {
                  int left;
                  int delta;
                  int right;
                  if (!(dataBuffer instanceof DataBufferShort) && !(dataBuffer instanceof DataBufferUShort)) {
                     if (dataBuffer instanceof DataBufferInt) {
                        data = ((DataBufferInt)dataBuffer).getData();

                        for(leftShift = 0; leftShift < rectHeight; ++leftShift) {
                           xRemaining = bitOffset;

                           for(i = 0; i < rectWidth; xRemaining += 8) {
                              shift = eltOffset + xRemaining / 32;
                              mod = xRemaining % 32;
                              left = data[shift];
                              if (mod <= 24) {
                                 binaryDataArray[b++] = (byte)(left >>> 24 - mod);
                              } else {
                                 delta = mod - 24;
                                 right = data[shift + 1];
                                 binaryDataArray[b++] = (byte)(left << delta | right >>> 32 - delta);
                              }

                              i += 8;
                           }

                           eltOffset += lineStride;
                        }
                     }
                  } else {
                     data = dataBuffer instanceof DataBufferShort ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();

                     for(leftShift = 0; leftShift < rectHeight; ++leftShift) {
                        xRemaining = bitOffset;

                        for(i = 0; i < rectWidth; xRemaining += 8) {
                           shift = eltOffset + xRemaining / 16;
                           mod = xRemaining % 16;
                           left = data[shift] & '\uffff';
                           if (mod <= 8) {
                              binaryDataArray[b++] = (byte)(left >>> 8 - mod);
                           } else {
                              delta = mod - 8;
                              right = data[shift + 1] & '\uffff';
                              binaryDataArray[b++] = (byte)(left << delta | right >>> 16 - delta);
                           }

                           i += 8;
                        }

                        eltOffset += lineStride;
                     }
                  }
               }
            }

            return binaryDataArray;
         }
      }
   }

   public static byte[] getUnpackedBinaryData(Raster raster, Rectangle rect) {
      SampleModel sm = raster.getSampleModel();
      if (!isBinary(sm)) {
         throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
      } else {
         int rectX = rect.x;
         int rectY = rect.y;
         int rectWidth = rect.width;
         int rectHeight = rect.height;
         DataBuffer dataBuffer = raster.getDataBuffer();
         int dx = rectX - raster.getSampleModelTranslateX();
         int dy = rectY - raster.getSampleModelTranslateY();
         MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
         int lineStride = mpp.getScanlineStride();
         int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
         int bitOffset = mpp.getBitOffset(dx);
         byte[] bdata = new byte[rectWidth * rectHeight];
         int maxY = rectY + rectHeight;
         int maxX = rectX + rectWidth;
         int k = 0;
         int y;
         int bOffset;
         int x;
         int i;
         if (dataBuffer instanceof DataBufferByte) {
            byte[] data = ((DataBufferByte)dataBuffer).getData();

            for(y = rectY; y < maxY; ++y) {
               bOffset = eltOffset * 8 + bitOffset;

               for(x = rectX; x < maxX; ++x) {
                  i = data[bOffset / 8];
                  bdata[k++] = (byte)(i >>> (7 - bOffset & 7) & 1);
                  ++bOffset;
               }

               eltOffset += lineStride;
            }
         } else if (!(dataBuffer instanceof DataBufferShort) && !(dataBuffer instanceof DataBufferUShort)) {
            if (dataBuffer instanceof DataBufferInt) {
               int[] data = ((DataBufferInt)dataBuffer).getData();

               for(y = rectY; y < maxY; ++y) {
                  bOffset = eltOffset * 32 + bitOffset;

                  for(x = rectX; x < maxX; ++x) {
                     i = data[bOffset / 32];
                     bdata[k++] = (byte)(i >>> 31 - bOffset % 32 & 1);
                     ++bOffset;
                  }

                  eltOffset += lineStride;
               }
            }
         } else {
            short[] data = dataBuffer instanceof DataBufferShort ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();

            for(y = rectY; y < maxY; ++y) {
               bOffset = eltOffset * 16 + bitOffset;

               for(x = rectX; x < maxX; ++x) {
                  i = data[bOffset / 16];
                  bdata[k++] = (byte)(i >>> 15 - bOffset % 16 & 1);
                  ++bOffset;
               }

               eltOffset += lineStride;
            }
         }

         return bdata;
      }
   }

   public static void setPackedBinaryData(byte[] binaryDataArray, WritableRaster raster, Rectangle rect) {
      SampleModel sm = raster.getSampleModel();
      if (!isBinary(sm)) {
         throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
      } else {
         int rectX = rect.x;
         int rectY = rect.y;
         int rectWidth = rect.width;
         int rectHeight = rect.height;
         DataBuffer dataBuffer = raster.getDataBuffer();
         int dx = rectX - raster.getSampleModelTranslateX();
         int dy = rectY - raster.getSampleModelTranslateY();
         MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
         int lineStride = mpp.getScanlineStride();
         int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
         int bitOffset = mpp.getBitOffset(dx);
         int b = 0;
         int y;
         int rightShift;
         int leftShift;
         if (bitOffset == 0) {
            int xRemaining;
            if (dataBuffer instanceof DataBufferByte) {
               byte[] data = ((DataBufferByte)dataBuffer).getData();
               if (data == binaryDataArray) {
                  return;
               }

               y = (rectWidth + 7) / 8;
               xRemaining = 0;

               for(rightShift = 0; rightShift < rectHeight; ++rightShift) {
                  System.arraycopy(binaryDataArray, xRemaining, data, eltOffset, y);
                  xRemaining += y;
                  eltOffset += lineStride;
               }
            } else if (!(dataBuffer instanceof DataBufferShort) && !(dataBuffer instanceof DataBufferUShort)) {
               if (dataBuffer instanceof DataBufferInt) {
                  int[] data = ((DataBufferInt)dataBuffer).getData();

                  for(y = 0; y < rectHeight; ++y) {
                     xRemaining = rectWidth;

                     for(rightShift = eltOffset; xRemaining > 24; xRemaining -= 32) {
                        data[rightShift++] = (binaryDataArray[b++] & 255) << 24 | (binaryDataArray[b++] & 255) << 16 | (binaryDataArray[b++] & 255) << 8 | binaryDataArray[b++] & 255;
                     }

                     for(leftShift = 24; xRemaining > 0; xRemaining -= 8) {
                        data[rightShift] |= (binaryDataArray[b++] & 255) << leftShift;
                        leftShift -= 8;
                     }

                     eltOffset += lineStride;
                  }
               }
            } else {
               short[] data = dataBuffer instanceof DataBufferShort ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();

               for(y = 0; y < rectHeight; ++y) {
                  xRemaining = rectWidth;

                  for(rightShift = eltOffset; xRemaining > 8; xRemaining -= 16) {
                     data[rightShift++] = (short)((binaryDataArray[b++] & 255) << 8 | binaryDataArray[b++] & 255);
                  }

                  if (xRemaining > 0) {
                     data[rightShift++] = (short)((binaryDataArray[b++] & 255) << 8);
                  }

                  eltOffset += lineStride;
               }
            }
         } else {
            int stride = (rectWidth + 7) / 8;
            y = 0;
            int leftShift32;
            int mask;
            int mask1;
            int y;
            int i;
            int xRemaining;
            int xRemaining;
            int i;
            if (dataBuffer instanceof DataBufferByte) {
               byte[] data = ((DataBufferByte)dataBuffer).getData();
               if ((bitOffset & 7) == 0) {
                  for(rightShift = 0; rightShift < rectHeight; ++rightShift) {
                     System.arraycopy(binaryDataArray, y, data, eltOffset, stride);
                     y += stride;
                     eltOffset += lineStride;
                  }
               } else {
                  rightShift = bitOffset & 7;
                  leftShift = 8 - rightShift;
                  leftShift32 = 8 + leftShift;
                  mask = (byte)(255 << leftShift);
                  mask1 = (byte)(~mask);

                  for(y = 0; y < rectHeight; ++y) {
                     i = eltOffset;

                     for(xRemaining = rectWidth; xRemaining > 0; xRemaining -= 8) {
                        xRemaining = binaryDataArray[b++];
                        if (xRemaining > leftShift32) {
                           data[i] = (byte)(data[i] & mask | (xRemaining & 255) >>> rightShift);
                           ++i;
                           data[i] = (byte)((xRemaining & 255) << leftShift);
                        } else if (xRemaining > leftShift) {
                           data[i] = (byte)(data[i] & mask | (xRemaining & 255) >>> rightShift);
                           ++i;
                           data[i] = (byte)(data[i] & mask1 | (xRemaining & 255) << leftShift);
                        } else {
                           i = (1 << leftShift - xRemaining) - 1;
                           data[i] = (byte)(data[i] & (mask | i) | (xRemaining & 255) >>> rightShift & ~i);
                        }
                     }

                     eltOffset += lineStride;
                  }
               }
            } else {
               int i;
               int datum;
               int remainMask;
               if (!(dataBuffer instanceof DataBufferShort) && !(dataBuffer instanceof DataBufferUShort)) {
                  if (dataBuffer instanceof DataBufferInt) {
                     int[] data = ((DataBufferInt)dataBuffer).getData();
                     rightShift = bitOffset & 7;
                     leftShift = 8 - rightShift;
                     leftShift32 = 32 + leftShift;
                     mask = -1 << leftShift;
                     mask1 = ~mask;

                     for(y = 0; y < rectHeight; ++y) {
                        i = bitOffset;
                        xRemaining = rectWidth;

                        for(xRemaining = 0; xRemaining < rectWidth; xRemaining -= 8) {
                           i = eltOffset + (i >> 5);
                           i = i & 31;
                           datum = binaryDataArray[b++] & 255;
                           if (i <= 24) {
                              remainMask = 24 - i;
                              if (xRemaining < 8) {
                                 datum &= 255 << 8 - xRemaining;
                              }

                              data[i] = data[i] & ~(255 << remainMask) | datum << remainMask;
                           } else if (xRemaining > leftShift32) {
                              data[i] = data[i] & mask | datum >>> rightShift;
                              ++i;
                              data[i] = datum << leftShift;
                           } else if (xRemaining > leftShift) {
                              data[i] = data[i] & mask | datum >>> rightShift;
                              ++i;
                              data[i] = data[i] & mask1 | datum << leftShift;
                           } else {
                              remainMask = (1 << leftShift - xRemaining) - 1;
                              data[i] = data[i] & (mask | remainMask) | datum >>> rightShift & ~remainMask;
                           }

                           xRemaining += 8;
                           i += 8;
                        }

                        eltOffset += lineStride;
                     }
                  }
               } else {
                  short[] data = dataBuffer instanceof DataBufferShort ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
                  rightShift = bitOffset & 7;
                  leftShift = 8 - rightShift;
                  leftShift32 = 16 + leftShift;
                  mask = (short)(~(255 << leftShift));
                  mask1 = (short)('\uffff' << leftShift);
                  int mask2 = (short)(~mask1);

                  for(i = 0; i < rectHeight; ++i) {
                     xRemaining = bitOffset;
                     xRemaining = rectWidth;

                     for(i = 0; i < rectWidth; xRemaining -= 8) {
                        i = eltOffset + (xRemaining >> 4);
                        datum = xRemaining & 15;
                        remainMask = binaryDataArray[b++] & 255;
                        if (datum <= 8) {
                           if (xRemaining < 8) {
                              remainMask &= 255 << 8 - xRemaining;
                           }

                           data[i] = (short)(data[i] & mask | remainMask << leftShift);
                        } else if (xRemaining > leftShift32) {
                           data[i] = (short)(data[i] & mask1 | remainMask >>> rightShift & '\uffff');
                           ++i;
                           data[i] = (short)(remainMask << leftShift & '\uffff');
                        } else if (xRemaining > leftShift) {
                           data[i] = (short)(data[i] & mask1 | remainMask >>> rightShift & '\uffff');
                           ++i;
                           data[i] = (short)(data[i] & mask2 | remainMask << leftShift & '\uffff');
                        } else {
                           int remainMask = (1 << leftShift - xRemaining) - 1;
                           data[i] = (short)(data[i] & (mask1 | remainMask) | remainMask >>> rightShift & '\uffff' & ~remainMask);
                        }

                        i += 8;
                        xRemaining += 8;
                     }

                     eltOffset += lineStride;
                  }
               }
            }
         }

      }
   }

   public static void setUnpackedBinaryData(byte[] bdata, WritableRaster raster, Rectangle rect) {
      SampleModel sm = raster.getSampleModel();
      if (!isBinary(sm)) {
         throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
      } else {
         int rectX = rect.x;
         int rectY = rect.y;
         int rectWidth = rect.width;
         int rectHeight = rect.height;
         DataBuffer dataBuffer = raster.getDataBuffer();
         int dx = rectX - raster.getSampleModelTranslateX();
         int dy = rectY - raster.getSampleModelTranslateY();
         MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
         int lineStride = mpp.getScanlineStride();
         int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
         int bitOffset = mpp.getBitOffset(dx);
         int k = 0;
         int y;
         int bOffset;
         int x;
         if (dataBuffer instanceof DataBufferByte) {
            byte[] data = ((DataBufferByte)dataBuffer).getData();

            for(y = 0; y < rectHeight; ++y) {
               bOffset = eltOffset * 8 + bitOffset;

               for(x = 0; x < rectWidth; ++x) {
                  if (bdata[k++] != 0) {
                     data[bOffset / 8] |= (byte)(1 << (7 - bOffset & 7));
                  }

                  ++bOffset;
               }

               eltOffset += lineStride;
            }
         } else if (!(dataBuffer instanceof DataBufferShort) && !(dataBuffer instanceof DataBufferUShort)) {
            if (dataBuffer instanceof DataBufferInt) {
               int[] data = ((DataBufferInt)dataBuffer).getData();

               for(y = 0; y < rectHeight; ++y) {
                  bOffset = eltOffset * 32 + bitOffset;

                  for(x = 0; x < rectWidth; ++x) {
                     if (bdata[k++] != 0) {
                        data[bOffset / 32] |= 1 << 31 - bOffset % 32;
                     }

                     ++bOffset;
                  }

                  eltOffset += lineStride;
               }
            }
         } else {
            short[] data = dataBuffer instanceof DataBufferShort ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();

            for(y = 0; y < rectHeight; ++y) {
               bOffset = eltOffset * 16 + bitOffset;

               for(x = 0; x < rectWidth; ++x) {
                  if (bdata[k++] != 0) {
                     data[bOffset / 16] |= (short)(1 << 15 - bOffset % 16);
                  }

                  ++bOffset;
               }

               eltOffset += lineStride;
            }
         }

      }
   }

   public static boolean isBinary(SampleModel sm) {
      return sm instanceof MultiPixelPackedSampleModel && ((MultiPixelPackedSampleModel)sm).getPixelBitStride() == 1 && sm.getNumBands() == 1;
   }

   public static ColorModel createColorModel(ColorSpace colorSpace, SampleModel sampleModel) {
      ColorModel colorModel = null;
      if (sampleModel == null) {
         throw new IllegalArgumentException(I18N.getString("ImageUtil1"));
      } else {
         int numBands = sampleModel.getNumBands();
         if (numBands >= 1 && numBands <= 4) {
            int dataType = sampleModel.getDataType();
            int transparency;
            boolean premultiplied;
            int gmask;
            int amask;
            if (sampleModel instanceof ComponentSampleModel) {
               if (dataType < 0 || dataType > 5) {
                  return null;
               }

               if (colorSpace == null) {
                  colorSpace = numBands <= 2 ? ColorSpace.getInstance(1003) : ColorSpace.getInstance(1000);
               }

               boolean useAlpha = numBands == 2 || numBands == 4;
               transparency = useAlpha ? 3 : 1;
               premultiplied = false;
               gmask = DataBuffer.getDataTypeSize(dataType);
               int[] bits = new int[numBands];

               for(amask = 0; amask < numBands; ++amask) {
                  bits[amask] = gmask;
               }

               colorModel = new ComponentColorModel(colorSpace, bits, useAlpha, premultiplied, transparency, dataType);
            } else if (sampleModel instanceof SinglePixelPackedSampleModel) {
               SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sampleModel;
               int[] bitMasks = sppsm.getBitMasks();
               premultiplied = false;
               int gmask = false;
               int bmask = false;
               amask = 0;
               numBands = bitMasks.length;
               int rmask;
               int bmask;
               if (numBands <= 2) {
                  rmask = gmask = bmask = bitMasks[0];
                  if (numBands == 2) {
                     amask = bitMasks[1];
                  }
               } else {
                  rmask = bitMasks[0];
                  gmask = bitMasks[1];
                  bmask = bitMasks[2];
                  if (numBands == 4) {
                     amask = bitMasks[3];
                  }
               }

               int[] sampleSize = sppsm.getSampleSize();
               int bits = 0;

               for(int i = 0; i < sampleSize.length; ++i) {
                  bits += sampleSize[i];
               }

               if (colorSpace == null) {
                  colorSpace = ColorSpace.getInstance(1000);
               }

               colorModel = new DirectColorModel(colorSpace, bits, rmask, gmask, bmask, amask, false, sampleModel.getDataType());
            } else if (sampleModel instanceof MultiPixelPackedSampleModel) {
               int bits = ((MultiPixelPackedSampleModel)sampleModel).getPixelBitStride();
               transparency = 1 << bits;
               byte[] comp = new byte[transparency];

               for(gmask = 0; gmask < transparency; ++gmask) {
                  comp[gmask] = (byte)(255 * gmask / (transparency - 1));
               }

               colorModel = new IndexColorModel(bits, transparency, comp, comp, comp);
            }

            return (ColorModel)colorModel;
         } else {
            return null;
         }
      }
   }

   public static int getElementSize(SampleModel sm) {
      int elementSize = DataBuffer.getDataTypeSize(sm.getDataType());
      if (sm instanceof MultiPixelPackedSampleModel) {
         MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sm;
         return mppsm.getSampleSize(0) * mppsm.getNumBands();
      } else if (sm instanceof ComponentSampleModel) {
         return sm.getNumBands() * elementSize;
      } else {
         return sm instanceof SinglePixelPackedSampleModel ? elementSize : elementSize * sm.getNumBands();
      }
   }

   public static long getTileSize(SampleModel sm) {
      int elementSize = DataBuffer.getDataTypeSize(sm.getDataType());
      if (sm instanceof MultiPixelPackedSampleModel) {
         MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sm;
         return (long)((mppsm.getScanlineStride() * mppsm.getHeight() + (mppsm.getDataBitOffset() + elementSize - 1) / elementSize) * ((elementSize + 7) / 8));
      } else if (!(sm instanceof ComponentSampleModel)) {
         if (sm instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
            long size = (long)(sppsm.getScanlineStride() * (sppsm.getHeight() - 1) + sppsm.getWidth());
            return size * (long)((elementSize + 7) / 8);
         } else {
            return 0L;
         }
      } else {
         ComponentSampleModel csm = (ComponentSampleModel)sm;
         int[] bandOffsets = csm.getBandOffsets();
         int maxBandOff = bandOffsets[0];

         for(int i = 1; i < bandOffsets.length; ++i) {
            maxBandOff = Math.max(maxBandOff, bandOffsets[i]);
         }

         long size = 0L;
         int pixelStride = csm.getPixelStride();
         int scanlineStride = csm.getScanlineStride();
         if (maxBandOff >= 0) {
            size += (long)(maxBandOff + 1);
         }

         if (pixelStride > 0) {
            size += (long)(pixelStride * (sm.getWidth() - 1));
         }

         if (scanlineStride > 0) {
            size += (long)(scanlineStride * (sm.getHeight() - 1));
         }

         int[] bankIndices = csm.getBankIndices();
         maxBandOff = bankIndices[0];

         for(int i = 1; i < bankIndices.length; ++i) {
            maxBandOff = Math.max(maxBandOff, bankIndices[i]);
         }

         return size * (long)(maxBandOff + 1) * (long)((elementSize + 7) / 8);
      }
   }

   public static long getBandSize(SampleModel sm) {
      int elementSize = DataBuffer.getDataTypeSize(sm.getDataType());
      if (sm instanceof ComponentSampleModel) {
         ComponentSampleModel csm = (ComponentSampleModel)sm;
         int pixelStride = csm.getPixelStride();
         int scanlineStride = csm.getScanlineStride();
         long size = (long)Math.min(pixelStride, scanlineStride);
         if (pixelStride > 0) {
            size += (long)(pixelStride * (sm.getWidth() - 1));
         }

         if (scanlineStride > 0) {
            size += (long)(scanlineStride * (sm.getHeight() - 1));
         }

         return size * (long)((elementSize + 7) / 8);
      } else {
         return getTileSize(sm);
      }
   }

   public static boolean isGrayscaleMapping(IndexColorModel icm) {
      if (icm == null) {
         throw new IllegalArgumentException("icm == null!");
      } else {
         int mapSize = icm.getMapSize();
         byte[] r = new byte[mapSize];
         byte[] g = new byte[mapSize];
         byte[] b = new byte[mapSize];
         icm.getReds(r);
         icm.getGreens(g);
         icm.getBlues(b);
         boolean isGrayToColor = true;

         int i;
         int j;
         for(i = 0; i < mapSize; ++i) {
            j = (byte)(i * 255 / (mapSize - 1));
            if (r[i] != j || g[i] != j || b[i] != j) {
               isGrayToColor = false;
               break;
            }
         }

         if (!isGrayToColor) {
            isGrayToColor = true;
            i = 0;

            for(j = mapSize - 1; i < mapSize; --j) {
               byte temp = (byte)(j * 255 / (mapSize - 1));
               if (r[i] != temp || g[i] != temp || b[i] != temp) {
                  isGrayToColor = false;
                  break;
               }

               ++i;
            }
         }

         return isGrayToColor;
      }
   }

   public static boolean isIndicesForGrayscale(byte[] r, byte[] g, byte[] b) {
      if (r.length == g.length && r.length == b.length) {
         int size = r.length;
         if (size != 256) {
            return false;
         } else {
            for(int i = 0; i < size; ++i) {
               byte temp = (byte)i;
               if (r[i] != temp || g[i] != temp || b[i] != temp) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static String convertObjectToString(Object obj) {
      if (obj == null) {
         return "";
      } else {
         String s = "";
         int i;
         if (obj instanceof byte[]) {
            byte[] bArray = (byte[])((byte[])obj);

            for(i = 0; i < bArray.length; ++i) {
               s = s + bArray[i] + " ";
            }

            return s;
         } else if (obj instanceof int[]) {
            int[] iArray = (int[])((int[])obj);

            for(i = 0; i < iArray.length; ++i) {
               s = s + iArray[i] + " ";
            }

            return s;
         } else if (!(obj instanceof short[])) {
            return obj.toString();
         } else {
            short[] sArray = (short[])((short[])obj);

            for(i = 0; i < sArray.length; ++i) {
               s = s + sArray[i] + " ";
            }

            return s;
         }
      }
   }

   public static final void canEncodeImage(ImageWriter writer, ImageTypeSpecifier type) throws IIOException {
      ImageWriterSpi spi = writer.getOriginatingProvider();
      if (type != null && spi != null && !spi.canEncodeImage(type)) {
         throw new IIOException(I18N.getString("ImageUtil2") + " " + writer.getClass().getName());
      }
   }

   public static final void canEncodeImage(ImageWriter writer, ColorModel colorModel, SampleModel sampleModel) throws IIOException {
      ImageTypeSpecifier type = null;
      if (colorModel != null && sampleModel != null) {
         type = new ImageTypeSpecifier(colorModel, sampleModel);
      }

      canEncodeImage(writer, type);
   }

   public static final boolean imageIsContiguous(RenderedImage image) {
      SampleModel sm;
      if (image instanceof BufferedImage) {
         WritableRaster ras = ((BufferedImage)image).getRaster();
         sm = ras.getSampleModel();
      } else {
         sm = image.getSampleModel();
      }

      if (sm instanceof ComponentSampleModel) {
         ComponentSampleModel csm = (ComponentSampleModel)sm;
         if (csm.getPixelStride() != csm.getNumBands()) {
            return false;
         } else {
            int[] bandOffsets = csm.getBandOffsets();

            for(int i = 0; i < bandOffsets.length; ++i) {
               if (bandOffsets[i] != i) {
                  return false;
               }
            }

            int[] bankIndices = csm.getBankIndices();

            for(int i = 0; i < bandOffsets.length; ++i) {
               if (bankIndices[i] != 0) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return isBinary(sm);
      }
   }

   public static final ImageTypeSpecifier getDestinationType(ImageReadParam param, Iterator imageTypes) throws IIOException {
      if (imageTypes != null && imageTypes.hasNext()) {
         ImageTypeSpecifier imageType = null;
         if (param != null) {
            imageType = param.getDestinationType();
         }

         if (imageType == null) {
            Object o = imageTypes.next();
            if (!(o instanceof ImageTypeSpecifier)) {
               throw new IllegalArgumentException("Non-ImageTypeSpecifier retrieved from imageTypes!");
            }

            imageType = (ImageTypeSpecifier)o;
         } else {
            boolean foundIt = false;

            while(imageTypes.hasNext()) {
               ImageTypeSpecifier type = (ImageTypeSpecifier)imageTypes.next();
               if (type.equals(imageType)) {
                  foundIt = true;
                  break;
               }
            }

            if (!foundIt) {
               throw new IIOException("Destination type from ImageReadParam does not match!");
            }
         }

         return imageType;
      } else {
         throw new IllegalArgumentException("imageTypes null or empty!");
      }
   }

   public static boolean isNonStandardICCColorSpace(ColorSpace cs) {
      boolean retval = false;

      try {
         retval = cs instanceof ICC_ColorSpace && !cs.isCS_sRGB() && !cs.equals(ColorSpace.getInstance(1004)) && !cs.equals(ColorSpace.getInstance(1003)) && !cs.equals(ColorSpace.getInstance(1001)) && !cs.equals(ColorSpace.getInstance(1002));
      } catch (IllegalArgumentException var3) {
      }

      return retval;
   }

   public static List getJDKImageReaderWriterSPI(ServiceRegistry registry, String formatName, boolean isReader) {
      IIORegistry iioRegistry = (IIORegistry)registry;
      Class spiClass;
      String descPart;
      if (isReader) {
         spiClass = ImageReaderSpi.class;
         descPart = " image reader";
      } else {
         spiClass = ImageWriterSpi.class;
         descPart = " image writer";
      }

      Iterator iter = iioRegistry.getServiceProviders(spiClass, true);
      String desc = "standard " + formatName + descPart;
      String jiioPath = "com.github.jaiimageio.impl";
      Locale locale = Locale.getDefault();
      ArrayList list = new ArrayList();

      while(true) {
         while(true) {
            ImageReaderWriterSpi provider;
            do {
               do {
                  do {
                     if (!iter.hasNext()) {
                        return list;
                     }

                     provider = (ImageReaderWriterSpi)iter.next();
                  } while(!provider.getVendorName().startsWith("Sun Microsystems"));
               } while(!desc.equalsIgnoreCase(provider.getDescription(locale)));
            } while(provider.getPluginClassName().startsWith(jiioPath));

            String[] formatNames = provider.getFormatNames();

            for(int i = 0; i < formatNames.length; ++i) {
               if (formatNames[i].equalsIgnoreCase(formatName)) {
                  list.add(provider);
                  break;
               }
            }
         }
      }
   }

   public static void processOnRegistration(ServiceRegistry registry, Class category, String formatName, ImageReaderWriterSpi spi, int deregisterJvmVersion, int priorityJvmVersion) {
      String jvmVendor = System.getProperty("java.vendor");
      String jvmVersionString = System.getProperty("java.specification.version");
      int verIndex = jvmVersionString.indexOf("1.");
      jvmVersionString = jvmVersionString.substring(verIndex + 2);
      int jvmVersion = Integer.parseInt(jvmVersionString);
      if (jvmVendor.equals("Sun Microsystems Inc.")) {
         List list;
         if (spi instanceof ImageReaderSpi) {
            list = getJDKImageReaderWriterSPI(registry, formatName, true);
         } else {
            list = getJDKImageReaderWriterSPI(registry, formatName, false);
         }

         if (jvmVersion >= deregisterJvmVersion && list.size() != 0) {
            registry.deregisterServiceProvider(spi, category);
         } else {
            for(int i = 0; i < list.size(); ++i) {
               if (jvmVersion >= priorityJvmVersion) {
                  registry.setOrdering(category, list.get(i), spi);
               } else {
                  registry.setOrdering(category, spi, list.get(i));
               }
            }
         }
      }

   }

   public static int readMultiByteInteger(ImageInputStream iis) throws IOException {
      int value = iis.readByte();

      int result;
      for(result = value & 127; (value & 128) == 128; result |= value & 127) {
         result <<= 7;
         value = iis.readByte();
      }

      return result;
   }
}
