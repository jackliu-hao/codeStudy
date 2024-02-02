package com.github.jaiimageio.impl.plugins.bmp;

import com.github.jaiimageio.impl.common.ImageUtil;
import com.github.jaiimageio.plugins.bmp.BMPImageWriteParam;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.ColorModel;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class BMPImageWriter extends ImageWriter implements BMPConstants {
   private ImageOutputStream stream = null;
   private ByteArrayOutputStream embedded_stream = null;
   private int compressionType;
   private boolean isTopDown;
   private int w;
   private int h;
   private int compImageSize = 0;
   private int[] bitMasks;
   private int[] bitPos;
   private byte[] bpixels;
   private short[] spixels;
   private int[] ipixels;

   public BMPImageWriter(ImageWriterSpi originator) {
      super(originator);
   }

   public void setOutput(Object output) {
      super.setOutput(output);
      if (output != null) {
         if (!(output instanceof ImageOutputStream)) {
            throw new IllegalArgumentException(I18N.getString("BMPImageWriter0"));
         }

         this.stream = (ImageOutputStream)output;
         this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
      } else {
         this.stream = null;
      }

   }

   public ImageWriteParam getDefaultWriteParam() {
      return new BMPImageWriteParam();
   }

   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
      return null;
   }

   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
      BMPMetadata meta = new BMPMetadata();
      meta.initialize(imageType.getColorModel(), imageType.getSampleModel(), param);
      return meta;
   }

   public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
      return null;
   }

   public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
      if (inData == null) {
         throw new IllegalArgumentException("inData == null!");
      } else if (imageType == null) {
         throw new IllegalArgumentException("imageType == null!");
      } else {
         BMPMetadata outData = null;
         if (inData instanceof BMPMetadata) {
            outData = (BMPMetadata)((BMPMetadata)inData).clone();
         } else {
            try {
               outData = new BMPMetadata(inData);
            } catch (IIOInvalidTreeException var6) {
               outData = new BMPMetadata();
            }
         }

         outData.initialize(imageType.getColorModel(), imageType.getSampleModel(), param);
         return outData;
      }
   }

   public boolean canWriteRasters() {
      return true;
   }

   public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException(I18N.getString("BMPImageWriter7"));
      } else if (image == null) {
         throw new IllegalArgumentException(I18N.getString("BMPImageWriter8"));
      } else {
         this.clearAbortRequest();
         this.processImageStarted(0);
         if (param == null) {
            param = this.getDefaultWriteParam();
         }

         BMPImageWriteParam bmpParam = (BMPImageWriteParam)param;
         int bitsPerPixel = 24;
         boolean isPalette = false;
         int paletteEntries = 0;
         IndexColorModel icm = null;
         RenderedImage input = null;
         Raster inputRaster = null;
         boolean writeRaster = image.hasRaster();
         Rectangle sourceRegion = param.getSourceRegion();
         SampleModel sampleModel = null;
         ColorModel colorModel = null;
         this.compImageSize = 0;
         if (writeRaster) {
            inputRaster = image.getRaster();
            sampleModel = inputRaster.getSampleModel();
            colorModel = ImageUtil.createColorModel((ColorSpace)null, sampleModel);
            if (sourceRegion == null) {
               sourceRegion = inputRaster.getBounds();
            } else {
               sourceRegion = sourceRegion.intersection(inputRaster.getBounds());
            }
         } else {
            input = image.getRenderedImage();
            sampleModel = input.getSampleModel();
            colorModel = input.getColorModel();
            Rectangle rect = new Rectangle(input.getMinX(), input.getMinY(), input.getWidth(), input.getHeight());
            if (sourceRegion == null) {
               sourceRegion = rect;
            } else {
               sourceRegion = sourceRegion.intersection(rect);
            }
         }

         IIOMetadata imageMetadata = image.getMetadata();
         BMPMetadata bmpImageMetadata = null;
         ImageTypeSpecifier imageType = new ImageTypeSpecifier(colorModel, sampleModel);
         if (imageMetadata != null) {
            bmpImageMetadata = (BMPMetadata)this.convertImageMetadata(imageMetadata, imageType, param);
         } else {
            bmpImageMetadata = (BMPMetadata)this.getDefaultImageMetadata(imageType, param);
         }

         if (sourceRegion.isEmpty()) {
            throw new RuntimeException(I18N.getString("BMPImageWrite0"));
         } else {
            int scaleX = param.getSourceXSubsampling();
            int scaleY = param.getSourceYSubsampling();
            int xOffset = param.getSubsamplingXOffset();
            int yOffset = param.getSubsamplingYOffset();
            int dataType = sampleModel.getDataType();
            sourceRegion.translate(xOffset, yOffset);
            sourceRegion.width -= xOffset;
            sourceRegion.height -= yOffset;
            int minX = sourceRegion.x / scaleX;
            int minY = sourceRegion.y / scaleY;
            this.w = (sourceRegion.width + scaleX - 1) / scaleX;
            this.h = (sourceRegion.height + scaleY - 1) / scaleY;
            xOffset = sourceRegion.x % scaleX;
            yOffset = sourceRegion.y % scaleY;
            Rectangle destinationRegion = new Rectangle(minX, minY, this.w, this.h);
            boolean noTransform = destinationRegion.equals(sourceRegion);
            int[] sourceBands = param.getSourceBands();
            boolean noSubband = true;
            int numBands = sampleModel.getNumBands();
            if (sourceBands != null) {
               sampleModel = sampleModel.createSubsetSampleModel(sourceBands);
               colorModel = null;
               noSubband = false;
               numBands = sampleModel.getNumBands();
            } else {
               sourceBands = new int[numBands];

               for(int i = 0; i < numBands; sourceBands[i] = i++) {
               }
            }

            int[] bandOffsets = null;
            boolean bgrOrder = true;
            int i;
            int destScanlineBytes;
            int[] sampleSize;
            if (sampleModel instanceof ComponentSampleModel) {
               bandOffsets = ((ComponentSampleModel)sampleModel).getBandOffsets();
               if (sampleModel instanceof BandedSampleModel) {
                  bgrOrder = false;
               } else {
                  for(i = 0; i < bandOffsets.length; ++i) {
                     bgrOrder &= bandOffsets[i] == bandOffsets.length - i - 1;
                  }
               }
            } else if (sampleModel instanceof SinglePixelPackedSampleModel) {
               sampleSize = ((SinglePixelPackedSampleModel)sampleModel).getBitOffsets();

               for(destScanlineBytes = 0; destScanlineBytes < sampleSize.length - 1; ++destScanlineBytes) {
                  bgrOrder &= sampleSize[destScanlineBytes] > sampleSize[destScanlineBytes + 1];
               }
            }

            if (bandOffsets == null) {
               bandOffsets = new int[numBands];

               for(i = 0; i < numBands; bandOffsets[i] = i++) {
               }
            }

            noTransform &= bgrOrder;
            sampleSize = sampleModel.getSampleSize();
            destScanlineBytes = this.w * numBands;
            switch (bmpParam.getCompressionMode()) {
               case 1:
                  this.compressionType = getPreferredCompressionType(colorModel, sampleModel);
                  break;
               case 2:
                  this.compressionType = getCompressionType(bmpParam.getCompressionType());
                  break;
               case 3:
                  this.compressionType = bmpImageMetadata.compression;
                  break;
               default:
                  this.compressionType = 0;
            }

            if (!this.canEncodeImage(this.compressionType, colorModel, sampleModel)) {
               if (param.getCompressionMode() == 2) {
                  throw new IIOException("Image can not be encoded with compression type " + compressionTypeNames[this.compressionType]);
               }

               this.compressionType = getPreferredCompressionType(colorModel, sampleModel);
            }

            byte[] r = null;
            byte[] g = null;
            byte[] b = null;
            byte[] a = null;
            int fileSize;
            int offset;
            if (this.compressionType == 3) {
               bitsPerPixel = DataBuffer.getDataTypeSize(sampleModel.getDataType());
               if (bitsPerPixel != 16 && bitsPerPixel != 32) {
                  bitsPerPixel = 32;
                  noTransform = false;
               }

               destScanlineBytes = this.w * bitsPerPixel + 7 >> 3;
               isPalette = true;
               paletteEntries = 3;
               r = new byte[paletteEntries];
               g = new byte[paletteEntries];
               b = new byte[paletteEntries];
               a = new byte[paletteEntries];
               fileSize = 16711680;
               offset = 65280;
               int bmask = 255;
               if (bitsPerPixel == 16) {
                  if (!(colorModel instanceof DirectColorModel)) {
                     throw new IOException("Image can not be encoded with compression type " + compressionTypeNames[this.compressionType]);
                  }

                  DirectColorModel dcm = (DirectColorModel)colorModel;
                  fileSize = dcm.getRedMask();
                  offset = dcm.getGreenMask();
                  bmask = dcm.getBlueMask();
               }

               this.writeMaskToPalette(fileSize, 0, r, g, b, a);
               this.writeMaskToPalette(offset, 1, r, g, b, a);
               this.writeMaskToPalette(bmask, 2, r, g, b, a);
               if (!noTransform) {
                  this.bitMasks = new int[3];
                  this.bitMasks[0] = fileSize;
                  this.bitMasks[1] = offset;
                  this.bitMasks[2] = bmask;
                  this.bitPos = new int[3];
                  this.bitPos[0] = this.firstLowBit(fileSize);
                  this.bitPos[1] = this.firstLowBit(offset);
                  this.bitPos[2] = this.firstLowBit(bmask);
               }

               if (colorModel instanceof IndexColorModel) {
                  icm = (IndexColorModel)colorModel;
               }
            } else if (colorModel instanceof IndexColorModel) {
               isPalette = true;
               icm = (IndexColorModel)colorModel;
               paletteEntries = icm.getMapSize();
               if (paletteEntries <= 2) {
                  bitsPerPixel = 1;
                  destScanlineBytes = this.w + 7 >> 3;
               } else if (paletteEntries <= 16) {
                  bitsPerPixel = 4;
                  destScanlineBytes = this.w + 1 >> 1;
               } else if (paletteEntries <= 256) {
                  bitsPerPixel = 8;
               } else {
                  bitsPerPixel = 24;
                  isPalette = false;
                  paletteEntries = 0;
                  destScanlineBytes = this.w * 3;
               }

               if (isPalette) {
                  r = new byte[paletteEntries];
                  g = new byte[paletteEntries];
                  b = new byte[paletteEntries];
                  icm.getReds(r);
                  icm.getGreens(g);
                  icm.getBlues(b);
               }
            } else if (numBands == 1) {
               isPalette = true;
               paletteEntries = 256;
               bitsPerPixel = sampleSize[0];
               destScanlineBytes = this.w * bitsPerPixel + 7 >> 3;
               r = new byte[256];
               g = new byte[256];
               b = new byte[256];

               for(fileSize = 0; fileSize < 256; ++fileSize) {
                  r[fileSize] = (byte)fileSize;
                  g[fileSize] = (byte)fileSize;
                  b[fileSize] = (byte)fileSize;
               }
            } else if (sampleModel instanceof SinglePixelPackedSampleModel && noSubband) {
               int[] sample_sizes = sampleModel.getSampleSize();
               bitsPerPixel = 0;

               for(offset = 0; offset < sample_sizes.length; ++offset) {
                  bitsPerPixel += sample_sizes[offset];
               }

               bitsPerPixel = this.roundBpp(bitsPerPixel);
               if (bitsPerPixel != DataBuffer.getDataTypeSize(sampleModel.getDataType())) {
                  noTransform = false;
               }

               destScanlineBytes = this.w * bitsPerPixel + 7 >> 3;
            }

            int fileSize = false;
            int offset = false;
            int headerSize = false;
            int imageSize = false;
            int xPelsPerMeter = bmpImageMetadata.xPixelsPerMeter;
            int yPelsPerMeter = bmpImageMetadata.yPixelsPerMeter;
            int colorsUsed = bmpImageMetadata.colorsUsed > 0 ? bmpImageMetadata.colorsUsed : paletteEntries;
            int padding = destScanlineBytes % 4;
            if (padding != 0) {
               padding = 4 - padding;
            }

            offset = 54 + paletteEntries * 4;
            int imageSize = (destScanlineBytes + padding) * this.h;
            fileSize = imageSize + offset;
            int headerSize = 40;
            long headPos = this.stream.getStreamPosition();
            if (param instanceof BMPImageWriteParam) {
               this.isTopDown = ((BMPImageWriteParam)param).isTopDown();
               if (this.compressionType != 0 && this.compressionType != 3) {
                  this.isTopDown = false;
               }
            } else {
               this.isTopDown = false;
            }

            this.writeFileHeader(fileSize, offset);
            this.writeInfoHeader(headerSize, bitsPerPixel);
            this.stream.writeInt(this.compressionType);
            this.stream.writeInt(imageSize);
            this.stream.writeInt(xPelsPerMeter);
            this.stream.writeInt(yPelsPerMeter);
            this.stream.writeInt(colorsUsed);
            this.stream.writeInt(paletteEntries);
            int scanlineBytes;
            if (isPalette) {
               if (this.compressionType == 3) {
                  for(scanlineBytes = 0; scanlineBytes < 3; ++scanlineBytes) {
                     int mask = (a[scanlineBytes] & 255) + (r[scanlineBytes] & 255) * 256 + (g[scanlineBytes] & 255) * 65536 + (b[scanlineBytes] & 255) * 16777216;
                     this.stream.writeInt(mask);
                  }
               } else {
                  for(scanlineBytes = 0; scanlineBytes < paletteEntries; ++scanlineBytes) {
                     this.stream.writeByte(b[scanlineBytes]);
                     this.stream.writeByte(g[scanlineBytes]);
                     this.stream.writeByte(r[scanlineBytes]);
                     this.stream.writeByte(0);
                  }
               }
            }

            scanlineBytes = this.w * numBands;
            int[] pixels = new int[scanlineBytes * scaleX];
            this.bpixels = new byte[destScanlineBytes];
            if (this.compressionType != 4 && this.compressionType != 5) {
               int maxBandOffset = bandOffsets[0];

               for(int i = 1; i < bandOffsets.length; ++i) {
                  if (bandOffsets[i] > maxBandOffset) {
                     maxBandOffset = bandOffsets[i];
                  }
               }

               int[] pixel = new int[maxBandOffset + 1];
               int destScanlineLength = destScanlineBytes;
               if (noTransform && noSubband) {
                  destScanlineLength = destScanlineBytes / (DataBuffer.getDataTypeSize(dataType) >> 3);
               }

               for(int i = 0; i < this.h && !this.abortRequested(); ++i) {
                  int row = minY + i;
                  if (!this.isTopDown) {
                     row = minY + this.h - i - 1;
                  }

                  Raster src = inputRaster;
                  Rectangle srcRect = new Rectangle(minX * scaleX + xOffset, row * scaleY + yOffset, (this.w - 1) * scaleX + 1, 1);
                  if (!writeRaster) {
                     src = input.getData(srcRect);
                  }

                  int pos;
                  int n;
                  int m;
                  if (noTransform && noSubband) {
                     SampleModel sm = src.getSampleModel();
                     pos = 0;
                     n = srcRect.x - src.getSampleModelTranslateX();
                     m = srcRect.y - src.getSampleModelTranslateY();
                     if (sm instanceof ComponentSampleModel) {
                        ComponentSampleModel csm = (ComponentSampleModel)sm;
                        pos = csm.getOffset(n, m, 0);

                        for(int nb = 1; nb < csm.getNumBands(); ++nb) {
                           if (pos > csm.getOffset(n, m, nb)) {
                              pos = csm.getOffset(n, m, nb);
                           }
                        }
                     } else if (sm instanceof MultiPixelPackedSampleModel) {
                        MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sm;
                        pos = mppsm.getOffset(n, m);
                     } else if (sm instanceof SinglePixelPackedSampleModel) {
                        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
                        pos = sppsm.getOffset(n, m);
                     }

                     int h;
                     if (this.compressionType == 0 || this.compressionType == 3) {
                        switch (dataType) {
                           case 0:
                              byte[] bdata = ((DataBufferByte)src.getDataBuffer()).getData();
                              this.stream.write(bdata, pos, destScanlineLength);
                              break;
                           case 1:
                              short[] usdata = ((DataBufferUShort)src.getDataBuffer()).getData();
                              this.stream.writeShorts(usdata, pos, destScanlineLength);
                              break;
                           case 2:
                              short[] sdata = ((DataBufferShort)src.getDataBuffer()).getData();
                              this.stream.writeShorts(sdata, pos, destScanlineLength);
                              break;
                           case 3:
                              int[] idata = ((DataBufferInt)src.getDataBuffer()).getData();
                              this.stream.writeInts(idata, pos, destScanlineLength);
                        }

                        for(h = 0; h < padding; ++h) {
                           this.stream.writeByte(0);
                        }
                     } else if (this.compressionType == 2) {
                        if (this.bpixels == null || this.bpixels.length < scanlineBytes) {
                           this.bpixels = new byte[scanlineBytes];
                        }

                        src.getPixels(srcRect.x, srcRect.y, srcRect.width, srcRect.height, pixels);

                        for(h = 0; h < scanlineBytes; ++h) {
                           this.bpixels[h] = (byte)pixels[h];
                        }

                        this.encodeRLE4(this.bpixels, scanlineBytes);
                     } else if (this.compressionType == 1) {
                        if (this.bpixels == null || this.bpixels.length < scanlineBytes) {
                           this.bpixels = new byte[scanlineBytes];
                        }

                        src.getPixels(srcRect.x, srcRect.y, srcRect.width, srcRect.height, pixels);

                        for(h = 0; h < scanlineBytes; ++h) {
                           this.bpixels[h] = (byte)pixels[h];
                        }

                        this.encodeRLE8(this.bpixels, scanlineBytes);
                     }
                  } else {
                     src.getPixels(srcRect.x, srcRect.y, srcRect.width, srcRect.height, pixels);
                     if (scaleX != 1 || maxBandOffset != numBands - 1) {
                        int j = 0;
                        pos = 0;

                        for(n = 0; j < this.w; n += numBands) {
                           System.arraycopy(pixels, pos, pixel, 0, pixel.length);

                           for(m = 0; m < numBands; ++m) {
                              pixels[n + m] = pixel[sourceBands[m]];
                           }

                           ++j;
                           pos += scaleX * numBands;
                        }
                     }

                     this.writePixels(0, scanlineBytes, bitsPerPixel, pixels, padding, numBands, icm);
                  }

                  this.processImageProgress(100.0F * ((float)i / (float)this.h));
               }

               if (this.compressionType == 2 || this.compressionType == 1) {
                  this.stream.writeByte(0);
                  this.stream.writeByte(1);
                  this.incCompImageSize(2);
                  imageSize = this.compImageSize;
                  fileSize = this.compImageSize + offset;
                  long endPos = this.stream.getStreamPosition();
                  this.stream.seek(headPos);
                  this.writeSize(fileSize, 2);
                  this.stream.seek(headPos);
                  this.writeSize(imageSize, 34);
                  this.stream.seek(endPos);
               }

               if (this.abortRequested()) {
                  this.processWriteAborted();
               } else {
                  this.processImageComplete();
                  this.stream.flushBefore(this.stream.getStreamPosition());
               }

            } else {
               this.embedded_stream = new ByteArrayOutputStream();
               this.writeEmbedded(image, bmpParam);
               this.embedded_stream.flush();
               imageSize = this.embedded_stream.size();
               long endPos = this.stream.getStreamPosition();
               fileSize = offset + imageSize;
               this.stream.seek(headPos);
               this.writeSize(fileSize, 2);
               this.stream.seek(headPos);
               this.writeSize(imageSize, 34);
               this.stream.seek(endPos);
               this.stream.write(this.embedded_stream.toByteArray());
               this.embedded_stream = null;
               if (this.abortRequested()) {
                  this.processWriteAborted();
               } else {
                  this.processImageComplete();
                  this.stream.flushBefore(this.stream.getStreamPosition());
               }

            }
         }
      }
   }

   private void writePixels(int l, int scanlineBytes, int bitsPerPixel, int[] pixels, int padding, int numBands, IndexColorModel icm) throws IOException {
      int pixel = false;
      int k = 0;
      int j;
      int m;
      int i;
      int pixel;
      switch (bitsPerPixel) {
         case 1:
            for(j = 0; j < scanlineBytes / 8; ++j) {
               this.bpixels[k++] = (byte)(pixels[l++] << 7 | pixels[l++] << 6 | pixels[l++] << 5 | pixels[l++] << 4 | pixels[l++] << 3 | pixels[l++] << 2 | pixels[l++] << 1 | pixels[l++]);
            }

            if (scanlineBytes % 8 > 0) {
               pixel = 0;

               for(j = 0; j < scanlineBytes % 8; ++j) {
                  pixel |= pixels[l++] << 7 - j;
               }

               this.bpixels[k++] = (byte)pixel;
            }

            this.stream.write(this.bpixels, 0, (scanlineBytes + 7) / 8);
            break;
         case 4:
            if (this.compressionType == 2) {
               byte[] bipixels = new byte[scanlineBytes];

               for(m = 0; m < scanlineBytes; ++m) {
                  bipixels[m] = (byte)pixels[l++];
               }

               this.encodeRLE4(bipixels, scanlineBytes);
            } else {
               for(j = 0; j < scanlineBytes / 2; ++j) {
                  pixel = pixels[l++] << 4 | pixels[l++];
                  this.bpixels[k++] = (byte)pixel;
               }

               if (scanlineBytes % 2 == 1) {
                  pixel = pixels[l] << 4;
                  this.bpixels[k++] = (byte)pixel;
               }

               this.stream.write(this.bpixels, 0, (scanlineBytes + 1) / 2);
            }
            break;
         case 8:
            if (this.compressionType == 1) {
               for(j = 0; j < scanlineBytes; ++j) {
                  this.bpixels[j] = (byte)pixels[l++];
               }

               this.encodeRLE8(this.bpixels, scanlineBytes);
            } else {
               for(j = 0; j < scanlineBytes; ++j) {
                  this.bpixels[j] = (byte)pixels[l++];
               }

               this.stream.write(this.bpixels, 0, scanlineBytes);
            }
            break;
         case 16:
            if (this.spixels == null) {
               this.spixels = new short[scanlineBytes / numBands];
            }

            j = 0;

            for(m = 0; j < scanlineBytes; ++m) {
               this.spixels[m] = 0;
               if (this.compressionType == 0) {
                  this.spixels[m] = (short)((31 & pixels[j]) << 10 | (31 & pixels[j + 1]) << 5 | 31 & pixels[j + 2]);
                  j += 3;
               } else {
                  for(i = 0; i < numBands; ++j) {
                     short[] var20 = this.spixels;
                     var20[m] = (short)(var20[m] | pixels[j] << this.bitPos[i] & this.bitMasks[i]);
                     ++i;
                  }
               }
            }

            this.stream.writeShorts(this.spixels, 0, this.spixels.length);
            break;
         case 24:
            if (numBands == 3) {
               for(j = 0; j < scanlineBytes; j += 3) {
                  this.bpixels[k++] = (byte)pixels[l + 2];
                  this.bpixels[k++] = (byte)pixels[l + 1];
                  this.bpixels[k++] = (byte)pixels[l];
                  l += 3;
               }

               this.stream.write(this.bpixels, 0, scanlineBytes);
            } else {
               j = icm.getMapSize();
               byte[] r = new byte[j];
               byte[] g = new byte[j];
               byte[] b = new byte[j];
               icm.getReds(r);
               icm.getGreens(g);
               icm.getBlues(b);

               for(int j = 0; j < scanlineBytes; ++j) {
                  int index = pixels[l];
                  this.bpixels[k++] = b[index];
                  this.bpixels[k++] = g[index];
                  this.bpixels[k++] = b[index];
                  ++l;
               }

               this.stream.write(this.bpixels, 0, scanlineBytes * 3);
            }
            break;
         case 32:
            if (this.ipixels == null) {
               this.ipixels = new int[scanlineBytes / numBands];
            }

            if (numBands != 3) {
               for(j = 0; j < scanlineBytes; ++j) {
                  if (icm != null) {
                     this.ipixels[j] = icm.getRGB(pixels[j]);
                  } else {
                     this.ipixels[j] = pixels[j] << 16 | pixels[j] << 8 | pixels[j];
                  }
               }
            } else {
               j = 0;

               for(m = 0; j < scanlineBytes; ++m) {
                  this.ipixels[m] = 0;
                  if (this.compressionType == 0) {
                     this.ipixels[m] = (255 & pixels[j + 2]) << 16 | (255 & pixels[j + 1]) << 8 | 255 & pixels[j];
                     j += 3;
                  } else {
                     for(i = 0; i < numBands; ++j) {
                        int[] var10000 = this.ipixels;
                        var10000[m] |= pixels[j] << this.bitPos[i] & this.bitMasks[i];
                        ++i;
                     }
                  }
               }
            }

            this.stream.writeInts(this.ipixels, 0, this.ipixels.length);
      }

      if (this.compressionType == 0 || this.compressionType == 3) {
         for(k = 0; k < padding; ++k) {
            this.stream.writeByte(0);
         }
      }

   }

   private void encodeRLE8(byte[] bpixels, int scanlineBytes) throws IOException {
      int runCount = 1;
      int absVal = -1;
      int j = -1;
      byte runVal = false;
      byte nextVal = false;
      ++j;
      byte runVal = bpixels[j];
      byte[] absBuf = new byte[256];

      while(true) {
         int b;
         do {
            if (j >= scanlineBytes - 1) {
               return;
            }

            ++j;
            byte nextVal = bpixels[j];
            if (nextVal == runVal) {
               if (absVal >= 3) {
                  this.stream.writeByte(0);
                  this.stream.writeByte(absVal);
                  this.incCompImageSize(2);

                  for(b = 0; b < absVal; ++b) {
                     this.stream.writeByte(absBuf[b]);
                     this.incCompImageSize(1);
                  }

                  if (!this.isEven(absVal)) {
                     this.stream.writeByte(0);
                     this.incCompImageSize(1);
                  }
               } else if (absVal > -1) {
                  for(b = 0; b < absVal; ++b) {
                     this.stream.writeByte(1);
                     this.stream.writeByte(absBuf[b]);
                     this.incCompImageSize(2);
                  }
               }

               absVal = -1;
               ++runCount;
               if (runCount == 256) {
                  this.stream.writeByte(runCount - 1);
                  this.stream.writeByte(runVal);
                  this.incCompImageSize(2);
                  runCount = 1;
               }
            } else {
               if (runCount > 1) {
                  this.stream.writeByte(runCount);
                  this.stream.writeByte(runVal);
                  this.incCompImageSize(2);
               } else if (absVal < 0) {
                  ++absVal;
                  absBuf[absVal] = runVal;
                  ++absVal;
                  absBuf[absVal] = nextVal;
               } else if (absVal < 254) {
                  ++absVal;
                  absBuf[absVal] = nextVal;
               } else {
                  this.stream.writeByte(0);
                  this.stream.writeByte(absVal + 1);
                  this.incCompImageSize(2);

                  for(b = 0; b <= absVal; ++b) {
                     this.stream.writeByte(absBuf[b]);
                     this.incCompImageSize(1);
                  }

                  this.stream.writeByte(0);
                  this.incCompImageSize(1);
                  absVal = -1;
               }

               runVal = nextVal;
               runCount = 1;
            }
         } while(j != scanlineBytes - 1);

         if (absVal == -1) {
            this.stream.writeByte(runCount);
            this.stream.writeByte(runVal);
            this.incCompImageSize(2);
            runCount = 1;
         } else if (absVal < 2) {
            if (absVal > -1) {
               for(b = 0; b <= absVal; ++b) {
                  this.stream.writeByte(1);
                  this.stream.writeByte(absBuf[b]);
                  this.incCompImageSize(2);
               }
            }
         } else {
            this.stream.writeByte(0);
            this.stream.writeByte(absVal + 1);
            this.incCompImageSize(2);

            for(b = 0; b <= absVal; ++b) {
               this.stream.writeByte(absBuf[b]);
               this.incCompImageSize(1);
            }

            if (!this.isEven(absVal + 1)) {
               this.stream.writeByte(0);
               this.incCompImageSize(1);
            }
         }

         this.stream.writeByte(0);
         this.stream.writeByte(0);
         this.incCompImageSize(2);
      }
   }

   private void encodeRLE4(byte[] bipixels, int scanlineBytes) throws IOException {
      int runCount = 2;
      int absVal = -1;
      int j = -1;
      int pixel = false;
      int q = false;
      byte runVal1 = false;
      byte runVal2 = false;
      byte nextVal1 = false;
      byte nextVal2 = false;
      byte[] absBuf = new byte[256];
      ++j;
      byte runVal1 = bipixels[j];
      ++j;
      byte runVal2 = bipixels[j];

      while(true) {
         int a;
         int pixel;
         int q;
         do {
            if (j >= scanlineBytes - 2) {
               return;
            }

            ++j;
            byte nextVal1 = bipixels[j];
            ++j;
            byte nextVal2 = bipixels[j];
            if (nextVal1 == runVal1) {
               if (absVal >= 4) {
                  this.stream.writeByte(0);
                  this.stream.writeByte(absVal - 1);
                  this.incCompImageSize(2);

                  for(a = 0; a < absVal - 2; a += 2) {
                     pixel = absBuf[a] << 4 | absBuf[a + 1];
                     this.stream.writeByte((byte)pixel);
                     this.incCompImageSize(1);
                  }

                  if (!this.isEven(absVal - 1)) {
                     q = absBuf[absVal - 2] << 4 | 0;
                     this.stream.writeByte(q);
                     this.incCompImageSize(1);
                  }

                  if (!this.isEven((int)Math.ceil((double)((absVal - 1) / 2)))) {
                     this.stream.writeByte(0);
                     this.incCompImageSize(1);
                  }
               } else if (absVal > -1) {
                  this.stream.writeByte(2);
                  pixel = absBuf[0] << 4 | absBuf[1];
                  this.stream.writeByte(pixel);
                  this.incCompImageSize(2);
               }

               absVal = -1;
               if (nextVal2 == runVal2) {
                  runCount += 2;
                  if (runCount == 256) {
                     this.stream.writeByte(runCount - 1);
                     pixel = runVal1 << 4 | runVal2;
                     this.stream.writeByte(pixel);
                     this.incCompImageSize(2);
                     runCount = 2;
                     if (j < scanlineBytes - 1) {
                        runVal1 = runVal2;
                        ++j;
                        runVal2 = bipixels[j];
                     } else {
                        this.stream.writeByte(1);
                        a = runVal2 << 4 | 0;
                        this.stream.writeByte(a);
                        this.incCompImageSize(2);
                        runCount = -1;
                     }
                  }
               } else {
                  ++runCount;
                  pixel = runVal1 << 4 | runVal2;
                  this.stream.writeByte(runCount);
                  this.stream.writeByte(pixel);
                  this.incCompImageSize(2);
                  runCount = 2;
                  runVal1 = nextVal2;
                  if (j < scanlineBytes - 1) {
                     ++j;
                     runVal2 = bipixels[j];
                  } else {
                     this.stream.writeByte(1);
                     a = nextVal2 << 4 | 0;
                     this.stream.writeByte(a);
                     this.incCompImageSize(2);
                     runCount = -1;
                  }
               }
            } else {
               if (runCount > 2) {
                  pixel = runVal1 << 4 | runVal2;
                  this.stream.writeByte(runCount);
                  this.stream.writeByte(pixel);
                  this.incCompImageSize(2);
               } else if (absVal < 0) {
                  ++absVal;
                  absBuf[absVal] = runVal1;
                  ++absVal;
                  absBuf[absVal] = runVal2;
                  ++absVal;
                  absBuf[absVal] = nextVal1;
                  ++absVal;
                  absBuf[absVal] = nextVal2;
               } else if (absVal < 253) {
                  ++absVal;
                  absBuf[absVal] = nextVal1;
                  ++absVal;
                  absBuf[absVal] = nextVal2;
               } else {
                  this.stream.writeByte(0);
                  this.stream.writeByte(absVal + 1);
                  this.incCompImageSize(2);

                  for(a = 0; a < absVal; a += 2) {
                     pixel = absBuf[a] << 4 | absBuf[a + 1];
                     this.stream.writeByte((byte)pixel);
                     this.incCompImageSize(1);
                  }

                  this.stream.writeByte(0);
                  this.incCompImageSize(1);
                  absVal = -1;
               }

               runVal1 = nextVal1;
               runVal2 = nextVal2;
               runCount = 2;
            }
         } while(j < scanlineBytes - 2);

         if (absVal == -1 && runCount >= 2) {
            if (j == scanlineBytes - 2) {
               ++j;
               if (bipixels[j] == runVal1) {
                  ++runCount;
                  pixel = runVal1 << 4 | runVal2;
                  this.stream.writeByte(runCount);
                  this.stream.writeByte(pixel);
                  this.incCompImageSize(2);
               } else {
                  pixel = runVal1 << 4 | runVal2;
                  this.stream.writeByte(runCount);
                  this.stream.writeByte(pixel);
                  this.stream.writeByte(1);
                  pixel = bipixels[j] << 4 | 0;
                  this.stream.writeByte(pixel);
                  a = bipixels[j] << 4 | 0;
                  this.incCompImageSize(4);
               }
            } else {
               this.stream.writeByte(runCount);
               pixel = runVal1 << 4 | runVal2;
               this.stream.writeByte(pixel);
               this.incCompImageSize(2);
            }
         } else if (absVal > -1) {
            if (j == scanlineBytes - 2) {
               ++absVal;
               ++j;
               absBuf[absVal] = bipixels[j];
            }

            if (absVal < 2) {
               switch (absVal) {
                  case 0:
                     this.stream.writeByte(1);
                     a = absBuf[0] << 4 | 0;
                     this.stream.writeByte(a);
                     this.incCompImageSize(2);
                     break;
                  case 1:
                     this.stream.writeByte(2);
                     pixel = absBuf[0] << 4 | absBuf[1];
                     this.stream.writeByte(pixel);
                     this.incCompImageSize(2);
               }
            } else {
               this.stream.writeByte(0);
               this.stream.writeByte(absVal + 1);
               this.incCompImageSize(2);

               for(a = 0; a < absVal; a += 2) {
                  pixel = absBuf[a] << 4 | absBuf[a + 1];
                  this.stream.writeByte((byte)pixel);
                  this.incCompImageSize(1);
               }

               if (!this.isEven(absVal + 1)) {
                  q = absBuf[absVal] << 4 | 0;
                  this.stream.writeByte(q);
                  this.incCompImageSize(1);
               }

               if (!this.isEven((int)Math.ceil((double)((absVal + 1) / 2)))) {
                  this.stream.writeByte(0);
                  this.incCompImageSize(1);
               }
            }
         }

         this.stream.writeByte(0);
         this.stream.writeByte(0);
         this.incCompImageSize(2);
      }
   }

   private synchronized void incCompImageSize(int value) {
      this.compImageSize += value;
   }

   private boolean isEven(int number) {
      return number % 2 == 0;
   }

   private void writeFileHeader(int fileSize, int offset) throws IOException {
      this.stream.writeByte(66);
      this.stream.writeByte(77);
      this.stream.writeInt(fileSize);
      this.stream.writeInt(0);
      this.stream.writeInt(offset);
   }

   private void writeInfoHeader(int headerSize, int bitsPerPixel) throws IOException {
      this.stream.writeInt(headerSize);
      this.stream.writeInt(this.w);
      if (this.isTopDown) {
         this.stream.writeInt(-this.h);
      } else {
         this.stream.writeInt(this.h);
      }

      this.stream.writeShort(1);
      this.stream.writeShort(bitsPerPixel);
   }

   private void writeSize(int dword, int offset) throws IOException {
      this.stream.skipBytes(offset);
      this.stream.writeInt(dword);
   }

   public void reset() {
      super.reset();
      this.stream = null;
   }

   static int getCompressionType(String typeString) {
      for(int i = 0; i < BMPConstants.compressionTypeNames.length; ++i) {
         if (BMPConstants.compressionTypeNames[i].equals(typeString)) {
            return i;
         }
      }

      return 0;
   }

   private void writeEmbedded(IIOImage image, ImageWriteParam bmpParam) throws IOException {
      String format = this.compressionType == 4 ? "jpeg" : "png";
      Iterator iterator = ImageIO.getImageWritersByFormatName(format);
      ImageWriter writer = null;
      if (iterator.hasNext()) {
         writer = (ImageWriter)iterator.next();
      }

      if (writer != null) {
         if (this.embedded_stream == null) {
            throw new RuntimeException("No stream for writing embedded image!");
         } else {
            writer.addIIOWriteProgressListener(new IIOWriteProgressAdapter() {
               public void imageProgress(ImageWriter source, float percentageDone) {
                  BMPImageWriter.this.processImageProgress(percentageDone);
               }
            });
            writer.addIIOWriteWarningListener(new IIOWriteWarningListener() {
               public void warningOccurred(ImageWriter source, int imageIndex, String warning) {
                  BMPImageWriter.this.processWarningOccurred(imageIndex, warning);
               }
            });
            ImageOutputStream emb_ios = ImageIO.createImageOutputStream(this.embedded_stream);
            writer.setOutput(emb_ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setDestinationOffset(bmpParam.getDestinationOffset());
            param.setSourceBands(bmpParam.getSourceBands());
            param.setSourceRegion(bmpParam.getSourceRegion());
            param.setSourceSubsampling(bmpParam.getSourceXSubsampling(), bmpParam.getSourceYSubsampling(), bmpParam.getSubsamplingXOffset(), bmpParam.getSubsamplingYOffset());
            writer.write((IIOMetadata)null, image, param);
            emb_ios.flush();
         }
      } else {
         throw new RuntimeException(I18N.getString("BMPImageWrite5") + " " + format);
      }
   }

   private int firstLowBit(int num) {
      int count;
      for(count = 0; (num & 1) == 0; num >>>= 1) {
         ++count;
      }

      return count;
   }

   static int getPreferredCompressionType(ColorModel cm, SampleModel sm) {
      ImageTypeSpecifier imageType = new ImageTypeSpecifier(cm, sm);
      return getPreferredCompressionType(imageType);
   }

   static int getPreferredCompressionType(ImageTypeSpecifier imageType) {
      int biType = imageType.getBufferedImageType();
      return biType != 8 && biType != 9 ? 0 : 3;
   }

   protected boolean canEncodeImage(int compression, ColorModel cm, SampleModel sm) {
      ImageTypeSpecifier imgType = new ImageTypeSpecifier(cm, sm);
      return this.canEncodeImage(compression, imgType);
   }

   protected boolean canEncodeImage(int compression, ImageTypeSpecifier imgType) {
      ImageWriterSpi spi = this.getOriginatingProvider();
      if (!spi.canEncodeImage(imgType)) {
         return false;
      } else {
         int bpp = imgType.getColorModel().getPixelSize();
         if (this.compressionType == 2 && bpp != 4) {
            return false;
         } else if (this.compressionType == 1 && bpp != 8) {
            return false;
         } else if (bpp != 16) {
            return true;
         } else {
            boolean canUseRGB = false;
            boolean canUseBITFIELDS = false;
            SampleModel sm = imgType.getSampleModel();
            if (sm instanceof SinglePixelPackedSampleModel) {
               int[] sizes = ((SinglePixelPackedSampleModel)sm).getSampleSize();
               canUseRGB = true;
               canUseBITFIELDS = true;

               for(int i = 0; i < sizes.length; ++i) {
                  canUseRGB &= sizes[i] == 5;
                  canUseBITFIELDS &= sizes[i] == 5 || i == 1 && sizes[i] == 6;
               }
            }

            return this.compressionType == 0 && canUseRGB || this.compressionType == 3 && canUseBITFIELDS;
         }
      }
   }

   protected void writeMaskToPalette(int mask, int i, byte[] r, byte[] g, byte[] b, byte[] a) {
      b[i] = (byte)(255 & mask >> 24);
      g[i] = (byte)(255 & mask >> 16);
      r[i] = (byte)(255 & mask >> 8);
      a[i] = (byte)(255 & mask);
   }

   private int roundBpp(int x) {
      if (x <= 8) {
         return 8;
      } else if (x <= 16) {
         return 16;
      } else {
         return x <= 24 ? 24 : 32;
      }
   }

   private class IIOWriteProgressAdapter implements IIOWriteProgressListener {
      private IIOWriteProgressAdapter() {
      }

      public void imageComplete(ImageWriter source) {
      }

      public void imageProgress(ImageWriter source, float percentageDone) {
      }

      public void imageStarted(ImageWriter source, int imageIndex) {
      }

      public void thumbnailComplete(ImageWriter source) {
      }

      public void thumbnailProgress(ImageWriter source, float percentageDone) {
      }

      public void thumbnailStarted(ImageWriter source, int imageIndex, int thumbnailIndex) {
      }

      public void writeAborted(ImageWriter source) {
      }

      // $FF: synthetic method
      IIOWriteProgressAdapter(Object x1) {
         this();
      }
   }
}
