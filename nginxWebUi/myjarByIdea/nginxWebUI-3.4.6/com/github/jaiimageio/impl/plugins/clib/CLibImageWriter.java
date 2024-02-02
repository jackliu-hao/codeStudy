package com.github.jaiimageio.impl.plugins.clib;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;

public abstract class CLibImageWriter extends ImageWriter {
   private static final Object getDataBufferData(DataBuffer db) {
      int dType = db.getDataType();
      Object data;
      switch (dType) {
         case 0:
            data = ((DataBufferByte)db).getData();
            break;
         case 1:
            data = ((DataBufferUShort)db).getData();
            break;
         default:
            throw new IllegalArgumentException(I18N.getString("Generic0") + " " + dType);
      }

      return data;
   }

   private static final Raster getContiguousData(RenderedImage im, Rectangle region) {
      if (im == null) {
         throw new IllegalArgumentException("im == null");
      } else if (region == null) {
         throw new IllegalArgumentException("region == null");
      } else {
         Object raster;
         if (im.getNumXTiles() == 1 && im.getNumYTiles() == 1) {
            raster = im.getTile(im.getMinTileX(), im.getMinTileY());
            Rectangle bounds = ((Raster)raster).getBounds();
            if (!bounds.equals(region)) {
               raster = ((Raster)raster).createChild(region.x, region.y, region.width, region.height, region.x, region.y, (int[])null);
            }
         } else {
            SampleModel sampleModel = im.getSampleModel();
            WritableRaster target = sampleModel.getSampleSize(0) == 8 ? Raster.createInterleavedRaster(0, im.getWidth(), im.getHeight(), sampleModel.getNumBands(), new Point(im.getMinX(), im.getMinY())) : null;
            raster = im.copyData(target);
         }

         return (Raster)raster;
      }
   }

   private static void reformat(Raster source, int[] sourceBands, int subsampleX, int subsampleY, WritableRaster dst) {
      if (source == null) {
         throw new IllegalArgumentException("source == null!");
      } else if (dst == null) {
         throw new IllegalArgumentException("dst == null!");
      } else {
         Rectangle sourceBounds = source.getBounds();
         if (sourceBounds.isEmpty()) {
            throw new IllegalArgumentException("source.getBounds().isEmpty()!");
         } else {
            boolean isSubBanding = false;
            int numSourceBands = source.getSampleModel().getNumBands();
            if (sourceBands != null) {
               if (sourceBands.length > numSourceBands) {
                  throw new IllegalArgumentException("sourceBands.length > numSourceBands!");
               }

               boolean isRamp = sourceBands.length == numSourceBands;
               int i = 0;

               while(true) {
                  if (i >= sourceBands.length) {
                     isSubBanding = !isRamp;
                     break;
                  }

                  if (sourceBands[i] < 0 || sourceBands[i] >= numSourceBands) {
                     throw new IllegalArgumentException("sourceBands[i] < 0 || sourceBands[i] >= numSourceBands!");
                  }

                  if (sourceBands[i] != i) {
                     isRamp = false;
                  }

                  ++i;
               }
            }

            int sourceWidth = sourceBounds.width;
            int[] pixels = new int[sourceWidth * numSourceBands];
            int sourceX = sourceBounds.x;
            int sourceY = sourceBounds.y;
            int numBands = sourceBands != null ? sourceBands.length : numSourceBands;
            int dstWidth = dst.getWidth();
            int dstYMax = dst.getHeight() - 1;
            int copyFromIncrement = numSourceBands * subsampleX;

            for(int dstY = 0; dstY <= dstYMax; ++dstY) {
               source.getPixels(sourceX, sourceY, sourceWidth, 1, pixels);
               int copyFrom;
               int copyTo;
               int i;
               int j;
               if (isSubBanding) {
                  copyFrom = 0;
                  copyTo = 0;

                  for(i = 0; i < dstWidth; ++i) {
                     for(j = 0; j < numBands; ++j) {
                        pixels[copyTo++] = pixels[copyFrom + sourceBands[j]];
                     }

                     copyFrom += copyFromIncrement;
                  }
               } else {
                  copyFrom = copyFromIncrement;
                  copyTo = numSourceBands;

                  for(i = 1; i < dstWidth; ++i) {
                     j = copyFrom;

                     for(int j = 0; j < numSourceBands; ++j) {
                        pixels[copyTo++] = pixels[j++];
                     }

                     copyFrom += copyFromIncrement;
                  }
               }

               dst.setPixels(0, dstY, dstWidth, 1, pixels);
               sourceY += subsampleY;
            }

         }
      }
   }

   protected CLibImageWriter(ImageWriterSpi originatingProvider) {
      super(originatingProvider);
   }

   public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
      return null;
   }

   public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
      return null;
   }

   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
      return null;
   }

   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
      return null;
   }

   private static final Rectangle getSourceRegion(ImageWriteParam param, int sourceMinX, int sourceMinY, int srcWidth, int srcHeight) {
      Rectangle sourceRegion = new Rectangle(sourceMinX, sourceMinY, srcWidth, srcHeight);
      if (param != null) {
         Rectangle region = param.getSourceRegion();
         if (region != null) {
            sourceRegion = sourceRegion.intersection(region);
         }

         int subsampleXOffset = param.getSubsamplingXOffset();
         int subsampleYOffset = param.getSubsamplingYOffset();
         sourceRegion.x += subsampleXOffset;
         sourceRegion.y += subsampleYOffset;
         sourceRegion.width -= subsampleXOffset;
         sourceRegion.height -= subsampleYOffset;
      }

      return sourceRegion;
   }
}
