package com.github.jaiimageio.impl.plugins.wbmp;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class WBMPImageWriter extends ImageWriter {
   private ImageOutputStream stream = null;

   private static int getNumBits(int intValue) {
      int numBits = 32;

      for(int mask = Integer.MIN_VALUE; mask != 0 && (intValue & mask) == 0; mask >>>= 1) {
         --numBits;
      }

      return numBits;
   }

   private static byte[] intToMultiByte(int intValue) {
      int numBitsLeft = getNumBits(intValue);
      byte[] multiBytes = new byte[(numBitsLeft + 6) / 7];
      int maxIndex = multiBytes.length - 1;

      for(int b = 0; b <= maxIndex; ++b) {
         multiBytes[b] = (byte)(intValue >>> (maxIndex - b) * 7 & 127);
         if (b != maxIndex) {
            multiBytes[b] |= -128;
         }
      }

      return multiBytes;
   }

   public WBMPImageWriter(ImageWriterSpi originator) {
      super(originator);
   }

   public void setOutput(Object output) {
      super.setOutput(output);
      if (output != null) {
         if (!(output instanceof ImageOutputStream)) {
            throw new IllegalArgumentException(I18N.getString("WBMPImageWriter"));
         }

         this.stream = (ImageOutputStream)output;
      } else {
         this.stream = null;
      }

   }

   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
      return null;
   }

   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
      WBMPMetadata meta = new WBMPMetadata();
      meta.wbmpType = 0;
      return meta;
   }

   public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
      return null;
   }

   public IIOMetadata convertImageMetadata(IIOMetadata metadata, ImageTypeSpecifier type, ImageWriteParam param) {
      return null;
   }

   public boolean canWriteRasters() {
      return true;
   }

   public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException(I18N.getString("WBMPImageWriter3"));
      } else if (image == null) {
         throw new IllegalArgumentException(I18N.getString("WBMPImageWriter4"));
      } else {
         this.clearAbortRequest();
         this.processImageStarted(0);
         if (param == null) {
            param = this.getDefaultWriteParam();
         }

         RenderedImage input = null;
         Raster inputRaster = null;
         boolean writeRaster = image.hasRaster();
         Rectangle sourceRegion = param.getSourceRegion();
         SampleModel sampleModel = null;
         if (writeRaster) {
            inputRaster = image.getRaster();
            sampleModel = ((Raster)inputRaster).getSampleModel();
         } else {
            input = image.getRenderedImage();
            sampleModel = input.getSampleModel();
            inputRaster = input.getData();
         }

         this.checkSampleModel(sampleModel);
         if (sourceRegion == null) {
            sourceRegion = ((Raster)inputRaster).getBounds();
         } else {
            sourceRegion = sourceRegion.intersection(((Raster)inputRaster).getBounds());
         }

         if (sourceRegion.isEmpty()) {
            throw new RuntimeException(I18N.getString("WBMPImageWriter1"));
         } else {
            int scaleX = param.getSourceXSubsampling();
            int scaleY = param.getSourceYSubsampling();
            int xOffset = param.getSubsamplingXOffset();
            int yOffset = param.getSubsamplingYOffset();
            sourceRegion.translate(xOffset, yOffset);
            sourceRegion.width -= xOffset;
            sourceRegion.height -= yOffset;
            int minX = sourceRegion.x / scaleX;
            int minY = sourceRegion.y / scaleY;
            int w = (sourceRegion.width + scaleX - 1) / scaleX;
            int h = (sourceRegion.height + scaleY - 1) / scaleY;
            Rectangle destinationRegion = new Rectangle(minX, minY, w, h);
            sampleModel = sampleModel.createCompatibleSampleModel(w, h);
            SampleModel destSM = sampleModel;
            if (sampleModel.getDataType() != 0 || !(sampleModel instanceof MultiPixelPackedSampleModel) || ((MultiPixelPackedSampleModel)sampleModel).getDataBitOffset() != 0) {
               destSM = new MultiPixelPackedSampleModel(0, w, h, 1, w + 7 >> 3, 0);
            }

            WritableRaster ras;
            int bytesPerRow;
            int offset;
            int row;
            int row;
            int col;
            if (!destinationRegion.equals(sourceRegion)) {
               if (scaleX == 1 && scaleY == 1) {
                  inputRaster = ((Raster)inputRaster).createChild(((Raster)inputRaster).getMinX(), ((Raster)inputRaster).getMinY(), w, h, minX, minY, (int[])null);
               } else {
                  ras = Raster.createWritableRaster((SampleModel)destSM, new Point(minX, minY));
                  byte[] data = ((DataBufferByte)ras.getDataBuffer()).getData();
                  bytesPerRow = minY;
                  int y = sourceRegion.y;

                  for(offset = 0; bytesPerRow < minY + h; y += scaleY) {
                     row = 0;

                     for(row = sourceRegion.x; row < w; row += scaleX) {
                        col = ((Raster)inputRaster).getSample(row, y, 0);
                        data[offset + (row >> 3)] = (byte)(data[offset + (row >> 3)] | col << 7 - (row & 7));
                        ++row;
                     }

                     offset += w + 7 >> 3;
                     ++bytesPerRow;
                  }

                  inputRaster = ras;
               }
            }

            if (!destSM.equals(((Raster)inputRaster).getSampleModel())) {
               ras = Raster.createWritableRaster((SampleModel)destSM, new Point(((Raster)inputRaster).getMinX(), ((Raster)inputRaster).getMinY()));
               ras.setRect((Raster)inputRaster);
               inputRaster = ras;
            }

            boolean isWhiteZero = false;
            if (!writeRaster && input.getColorModel() instanceof IndexColorModel) {
               IndexColorModel icm = (IndexColorModel)input.getColorModel();
               isWhiteZero = icm.getRed(0) > icm.getRed(1);
            }

            int lineStride = ((MultiPixelPackedSampleModel)destSM).getScanlineStride();
            bytesPerRow = (w + 7) / 8;
            byte[] bdata = ((DataBufferByte)((Raster)inputRaster).getDataBuffer()).getData();
            this.stream.write(0);
            this.stream.write(0);
            this.stream.write(intToMultiByte(w));
            this.stream.write(intToMultiByte(h));
            if (!isWhiteZero && lineStride == bytesPerRow) {
               this.stream.write(bdata, 0, h * bytesPerRow);
               this.processImageProgress(100.0F);
            } else {
               offset = 0;
               if (!isWhiteZero) {
                  for(row = 0; row < h && !this.abortRequested(); ++row) {
                     this.stream.write(bdata, offset, bytesPerRow);
                     offset += lineStride;
                     this.processImageProgress(100.0F * (float)row / (float)h);
                  }
               } else {
                  byte[] inverted = new byte[bytesPerRow];

                  for(row = 0; row < h && !this.abortRequested(); ++row) {
                     for(col = 0; col < bytesPerRow; ++col) {
                        inverted[col] = (byte)(~bdata[col + offset]);
                     }

                     this.stream.write(inverted, 0, bytesPerRow);
                     offset += lineStride;
                     this.processImageProgress(100.0F * (float)row / (float)h);
                  }
               }
            }

            if (this.abortRequested()) {
               this.processWriteAborted();
            } else {
               this.processImageComplete();
               this.stream.flushBefore(this.stream.getStreamPosition());
            }

         }
      }
   }

   public void reset() {
      super.reset();
      this.stream = null;
   }

   private void checkSampleModel(SampleModel sm) {
      int type = sm.getDataType();
      if (type < 0 || type > 3 || sm.getNumBands() != 1 || sm.getSampleSize(0) != 1) {
         throw new IllegalArgumentException(I18N.getString("WBMPImageWriter2"));
      }
   }
}
