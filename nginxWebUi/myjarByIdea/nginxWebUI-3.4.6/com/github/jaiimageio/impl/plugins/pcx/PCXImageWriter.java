package com.github.jaiimageio.impl.plugins.pcx;

import com.github.jaiimageio.impl.common.ImageUtil;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

public class PCXImageWriter extends ImageWriter implements PCXConstants {
   private ImageOutputStream ios;
   private Rectangle sourceRegion;
   private Rectangle destinationRegion;
   private int colorPlanes;
   private int bytesPerLine;
   private Raster inputRaster = null;
   private int scaleX;
   private int scaleY;

   public PCXImageWriter(PCXImageWriterSpi imageWriterSpi) {
      super(imageWriterSpi);
   }

   public void setOutput(Object output) {
      super.setOutput(output);
      if (output != null) {
         if (!(output instanceof ImageOutputStream)) {
            throw new IllegalArgumentException("output not instance of ImageOutputStream");
         }

         this.ios = (ImageOutputStream)output;
         this.ios.setByteOrder(ByteOrder.LITTLE_ENDIAN);
      } else {
         this.ios = null;
      }

   }

   public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
      return inData instanceof PCXMetadata ? inData : null;
   }

   public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
      return null;
   }

   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
      PCXMetadata md = new PCXMetadata();
      md.bitsPerPixel = (byte)imageType.getSampleModel().getSampleSize()[0];
      return md;
   }

   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
      return null;
   }

   public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
      if (this.ios == null) {
         throw new IllegalStateException("output stream is null");
      } else if (image == null) {
         throw new IllegalArgumentException("image is null");
      } else {
         this.clearAbortRequest();
         this.processImageStarted(0);
         if (param == null) {
            param = this.getDefaultWriteParam();
         }

         boolean writeRaster = image.hasRaster();
         this.sourceRegion = param.getSourceRegion();
         SampleModel sampleModel = null;
         ColorModel colorModel = null;
         Rectangle pcxImageMetadata;
         if (writeRaster) {
            this.inputRaster = image.getRaster();
            sampleModel = this.inputRaster.getSampleModel();
            colorModel = ImageUtil.createColorModel((ColorSpace)null, sampleModel);
            if (this.sourceRegion == null) {
               this.sourceRegion = this.inputRaster.getBounds();
            } else {
               this.sourceRegion = this.sourceRegion.intersection(this.inputRaster.getBounds());
            }
         } else {
            RenderedImage input = image.getRenderedImage();
            this.inputRaster = input.getData();
            sampleModel = input.getSampleModel();
            colorModel = input.getColorModel();
            pcxImageMetadata = new Rectangle(input.getMinX(), input.getMinY(), input.getWidth(), input.getHeight());
            if (this.sourceRegion == null) {
               this.sourceRegion = pcxImageMetadata;
            } else {
               this.sourceRegion = this.sourceRegion.intersection(pcxImageMetadata);
            }
         }

         if (this.sourceRegion.isEmpty()) {
            throw new IllegalArgumentException("source region is empty");
         } else {
            IIOMetadata imageMetadata = image.getMetadata();
            pcxImageMetadata = null;
            ImageTypeSpecifier imageType = new ImageTypeSpecifier(colorModel, sampleModel);
            PCXMetadata pcxImageMetadata;
            if (imageMetadata != null) {
               pcxImageMetadata = (PCXMetadata)this.convertImageMetadata(imageMetadata, imageType, param);
            } else {
               pcxImageMetadata = (PCXMetadata)this.getDefaultImageMetadata(imageType, param);
            }

            this.scaleX = param.getSourceXSubsampling();
            this.scaleY = param.getSourceYSubsampling();
            int xOffset = param.getSubsamplingXOffset();
            int yOffset = param.getSubsamplingYOffset();
            int dataType = sampleModel.getDataType();
            this.sourceRegion.translate(xOffset, yOffset);
            Rectangle var10000 = this.sourceRegion;
            var10000.width -= xOffset;
            var10000 = this.sourceRegion;
            var10000.height -= yOffset;
            int minX = this.sourceRegion.x / this.scaleX;
            int minY = this.sourceRegion.y / this.scaleY;
            int w = (this.sourceRegion.width + this.scaleX - 1) / this.scaleX;
            int h = (this.sourceRegion.height + this.scaleY - 1) / this.scaleY;
            int var26 = this.sourceRegion.x % this.scaleX;
            var26 = this.sourceRegion.y % this.scaleY;
            this.destinationRegion = new Rectangle(minX, minY, w, h);
            boolean noTransform = this.destinationRegion.equals(this.sourceRegion);
            int[] sourceBands = param.getSourceBands();
            boolean noSubband = true;
            int numBands = sampleModel.getNumBands();
            int bitsPerPixel;
            if (sourceBands != null) {
               sampleModel = sampleModel.createSubsetSampleModel(sourceBands);
               colorModel = null;
               noSubband = false;
               numBands = sampleModel.getNumBands();
            } else {
               sourceBands = new int[numBands];

               for(bitsPerPixel = 0; bitsPerPixel < numBands; sourceBands[bitsPerPixel] = bitsPerPixel++) {
               }
            }

            this.ios.writeByte(10);
            this.ios.writeByte(5);
            this.ios.writeByte(1);
            bitsPerPixel = sampleModel.getSampleSize(0);
            this.ios.writeByte(bitsPerPixel);
            this.ios.writeShort(this.destinationRegion.x);
            this.ios.writeShort(this.destinationRegion.y);
            this.ios.writeShort(this.destinationRegion.x + this.destinationRegion.width - 1);
            this.ios.writeShort(this.destinationRegion.y + this.destinationRegion.height - 1);
            this.ios.writeShort(pcxImageMetadata.hdpi);
            this.ios.writeShort(pcxImageMetadata.vdpi);
            byte[] smallpalette = this.createSmallPalette(colorModel);
            this.ios.write(smallpalette);
            this.ios.writeByte(0);
            this.colorPlanes = sampleModel.getNumBands();
            this.ios.writeByte(this.colorPlanes);
            this.bytesPerLine = this.destinationRegion.width * bitsPerPixel / 8;
            this.bytesPerLine += this.bytesPerLine % 2;
            this.ios.writeShort(this.bytesPerLine);
            if (colorModel.getColorSpace().getType() == 6) {
               this.ios.writeShort(2);
            } else {
               this.ios.writeShort(1);
            }

            this.ios.writeShort(pcxImageMetadata.hsize);
            this.ios.writeShort(pcxImageMetadata.vsize);

            for(int i = 0; i < 54; ++i) {
               this.ios.writeByte(0);
            }

            if (this.colorPlanes == 1 && bitsPerPixel == 1) {
               this.write1Bit();
            } else if (this.colorPlanes == 1 && bitsPerPixel == 4) {
               this.write4Bit();
            } else {
               this.write8Bit();
            }

            if (this.colorPlanes == 1 && bitsPerPixel == 8 && colorModel.getColorSpace().getType() != 6) {
               this.ios.writeByte(12);
               this.ios.write(this.createLargePalette(colorModel));
            }

            if (this.abortRequested()) {
               this.processWriteAborted();
            } else {
               this.processImageComplete();
            }

         }
      }
   }

   private void write4Bit() throws IOException {
      int[] unpacked = new int[this.sourceRegion.width];
      int[] samples = new int[this.bytesPerLine];

      for(int line = 0; line < this.sourceRegion.height; line += this.scaleY) {
         this.inputRaster.getSamples(this.sourceRegion.x, line + this.sourceRegion.y, this.sourceRegion.width, 1, 0, unpacked);
         int val = 0;
         int dst = 0;
         int last = 0;

         for(int nibble = false; last < this.sourceRegion.width; last += this.scaleX) {
            val |= unpacked[last] & 15;
            if (nibble) {
               samples[dst++] = val;
               nibble = false;
               val = 0;
            } else {
               nibble = true;
               val <<= 4;
            }
         }

         last = samples[0];
         int count = 0;

         for(int x = 0; x < this.bytesPerLine; x += this.scaleX) {
            int sample = samples[x];
            if (sample == last && count != 63) {
               ++count;
            } else {
               this.writeRLE(last, count);
               count = 1;
               last = sample;
            }
         }

         if (count >= 1) {
            this.writeRLE(last, count);
         }

         this.processImageProgress(100.0F * (float)line / (float)this.sourceRegion.height);
      }

   }

   private void write1Bit() throws IOException {
      int[] unpacked = new int[this.sourceRegion.width];
      int[] samples = new int[this.bytesPerLine];

      for(int line = 0; line < this.sourceRegion.height; line += this.scaleY) {
         this.inputRaster.getSamples(this.sourceRegion.x, line + this.sourceRegion.y, this.sourceRegion.width, 1, 0, unpacked);
         int val = 0;
         int dst = 0;
         int last = 0;

         int count;
         for(count = 128; last < this.sourceRegion.width; last += this.scaleX) {
            if (unpacked[last] > 0) {
               val |= count;
            }

            if (count == 1) {
               samples[dst++] = val;
               count = 128;
               val = 0;
            } else {
               count >>= 1;
            }
         }

         last = samples[0];
         count = 0;

         for(int x = 0; x < this.bytesPerLine; x += this.scaleX) {
            int sample = samples[x];
            if (sample == last && count != 63) {
               ++count;
            } else {
               this.writeRLE(last, count);
               count = 1;
               last = sample;
            }
         }

         if (count >= 1) {
            this.writeRLE(last, count);
         }

         this.processImageProgress(100.0F * (float)line / (float)this.sourceRegion.height);
      }

   }

   private void write8Bit() throws IOException {
      int[][] samples = new int[this.colorPlanes][this.bytesPerLine];

      for(int line = 0; line < this.sourceRegion.height; line += this.scaleY) {
         int last;
         for(last = 0; last < this.colorPlanes; ++last) {
            this.inputRaster.getSamples(this.sourceRegion.x, line + this.sourceRegion.y, this.sourceRegion.width, 1, last, samples[last]);
         }

         last = samples[0][0];
         int count = 0;

         for(int band = 0; band < this.colorPlanes; ++band) {
            for(int x = 0; x < this.bytesPerLine; x += this.scaleX) {
               int sample = samples[band][x];
               if (sample == last && count != 63) {
                  ++count;
               } else {
                  this.writeRLE(last, count);
                  count = 1;
                  last = sample;
               }
            }
         }

         if (count >= 1) {
            this.writeRLE(last, count);
         }

         this.processImageProgress(100.0F * (float)line / (float)this.sourceRegion.height);
      }

   }

   private void writeRLE(int val, int count) throws IOException {
      if (count == 1 && (val & 192) != 192) {
         this.ios.writeByte(val);
      } else {
         this.ios.writeByte(192 | count);
         this.ios.writeByte(val);
      }

   }

   private byte[] createSmallPalette(ColorModel cm) {
      byte[] palette = new byte[48];
      if (!(cm instanceof IndexColorModel)) {
         return palette;
      } else {
         IndexColorModel icm = (IndexColorModel)cm;
         if (icm.getMapSize() > 16) {
            return palette;
         } else {
            int i = 0;

            for(int offset = 0; i < icm.getMapSize(); ++i) {
               palette[offset++] = (byte)icm.getRed(i);
               palette[offset++] = (byte)icm.getGreen(i);
               palette[offset++] = (byte)icm.getBlue(i);
            }

            return palette;
         }
      }
   }

   private byte[] createLargePalette(ColorModel cm) {
      byte[] palette = new byte[768];
      if (!(cm instanceof IndexColorModel)) {
         return palette;
      } else {
         IndexColorModel icm = (IndexColorModel)cm;
         int i = 0;

         for(int offset = 0; i < icm.getMapSize(); ++i) {
            palette[offset++] = (byte)icm.getRed(i);
            palette[offset++] = (byte)icm.getGreen(i);
            palette[offset++] = (byte)icm.getBlue(i);
         }

         return palette;
      }
   }
}
