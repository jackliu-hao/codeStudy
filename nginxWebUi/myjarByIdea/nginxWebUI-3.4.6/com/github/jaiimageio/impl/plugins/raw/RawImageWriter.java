package com.github.jaiimageio.impl.plugins.raw;

import com.github.jaiimageio.impl.common.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BandedSampleModel;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class RawImageWriter extends ImageWriter {
   private ImageOutputStream stream = null;
   private int imageIndex;
   private int tileWidth;
   private int tileHeight;
   private int tileXOffset;
   private int tileYOffset;
   private int scaleX;
   private int scaleY;
   private int xOffset;
   private int yOffset;
   private int[] sourceBands = null;
   private int numBands;
   private RenderedImage input;
   private Raster inputRaster;
   private Rectangle destinationRegion = null;
   private SampleModel sampleModel;
   private boolean noTransform = true;
   private boolean noSubband = true;
   private boolean writeRaster = false;
   private boolean optimal = false;
   private int pxlStride;
   private int lineStride;
   private int bandStride;

   public RawImageWriter(ImageWriterSpi originator) {
      super(originator);
   }

   public void setOutput(Object output) {
      super.setOutput(output);
      if (output != null) {
         if (!(output instanceof ImageOutputStream)) {
            throw new IllegalArgumentException(I18N.getString("RawImageWriter0"));
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
      return null;
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

   public ImageWriteParam getDefaultWriteParam() {
      return new RawImageWriteParam(this.getLocale());
   }

   public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
      this.clearAbortRequest();
      this.processImageStarted(this.imageIndex++);
      if (param == null) {
         param = this.getDefaultWriteParam();
      }

      this.writeRaster = image.hasRaster();
      Rectangle sourceRegion = param.getSourceRegion();
      ColorModel colorModel = null;
      Rectangle originalRegion = null;
      if (this.writeRaster) {
         this.inputRaster = image.getRaster();
         this.sampleModel = this.inputRaster.getSampleModel();
         originalRegion = this.inputRaster.getBounds();
      } else {
         this.input = image.getRenderedImage();
         this.sampleModel = this.input.getSampleModel();
         originalRegion = new Rectangle(this.input.getMinX(), this.input.getMinY(), this.input.getWidth(), this.input.getHeight());
         colorModel = this.input.getColorModel();
      }

      if (sourceRegion == null) {
         sourceRegion = (Rectangle)originalRegion.clone();
      } else {
         sourceRegion = sourceRegion.intersection(originalRegion);
      }

      if (sourceRegion.isEmpty()) {
         throw new RuntimeException(I18N.getString("RawImageWriter1"));
      } else {
         this.scaleX = param.getSourceXSubsampling();
         this.scaleY = param.getSourceYSubsampling();
         this.xOffset = param.getSubsamplingXOffset();
         this.yOffset = param.getSubsamplingYOffset();
         sourceRegion.translate(this.xOffset, this.yOffset);
         sourceRegion.width -= this.xOffset;
         sourceRegion.height -= this.yOffset;
         this.xOffset = sourceRegion.x % this.scaleX;
         this.yOffset = sourceRegion.y % this.scaleY;
         int minX = sourceRegion.x / this.scaleX;
         int minY = sourceRegion.y / this.scaleY;
         int w = (sourceRegion.width + this.scaleX - 1) / this.scaleX;
         int h = (sourceRegion.height + this.scaleY - 1) / this.scaleY;
         this.destinationRegion = new Rectangle(minX, minY, w, h);
         this.noTransform = this.destinationRegion.equals(originalRegion);
         this.tileHeight = this.sampleModel.getHeight();
         this.tileWidth = this.sampleModel.getWidth();
         if (this.noTransform) {
            if (this.writeRaster) {
               this.tileXOffset = this.inputRaster.getMinX();
               this.tileYOffset = this.inputRaster.getMinY();
            } else {
               this.tileXOffset = this.input.getTileGridXOffset();
               this.tileYOffset = this.input.getTileGridYOffset();
            }
         } else {
            this.tileXOffset = this.destinationRegion.x;
            this.tileYOffset = this.destinationRegion.y;
         }

         this.sourceBands = param.getSourceBands();
         boolean noSubband = true;
         this.numBands = this.sampleModel.getNumBands();
         int numXTiles;
         if (this.sourceBands != null) {
            this.sampleModel = this.sampleModel.createSubsetSampleModel(this.sourceBands);
            colorModel = null;
            noSubband = false;
            this.numBands = this.sampleModel.getNumBands();
         } else {
            this.sourceBands = new int[this.numBands];

            for(numXTiles = 0; numXTiles < this.numBands; this.sourceBands[numXTiles] = numXTiles++) {
            }
         }

         int y;
         int numBank;
         if (this.sampleModel instanceof ComponentSampleModel) {
            ComponentSampleModel csm = (ComponentSampleModel)this.sampleModel;
            int[] bandOffsets = csm.getBandOffsets();
            this.bandStride = bandOffsets[0];

            for(y = 1; y < bandOffsets.length; ++y) {
               if (this.bandStride > bandOffsets[y]) {
                  this.bandStride = bandOffsets[y];
               }
            }

            int[] bankIndices = csm.getBankIndices();
            numBank = bankIndices[0];

            for(int i = 1; i < bankIndices.length; ++i) {
               if (numBank > bankIndices[i]) {
                  numBank = bankIndices[i];
               }
            }

            this.pxlStride = csm.getPixelStride();
            this.lineStride = csm.getScanlineStride();
            this.optimal = this.bandStride == 0 || this.pxlStride < this.lineStride && this.pxlStride == this.numBands || this.lineStride < this.pxlStride && this.lineStride == this.numBands || this.pxlStride < this.lineStride && this.lineStride == this.numBands * csm.getWidth() || this.lineStride < this.pxlStride && this.pxlStride == this.numBands * csm.getHeight() || csm instanceof BandedSampleModel;
         } else if (this.sampleModel instanceof SinglePixelPackedSampleModel || this.sampleModel instanceof MultiPixelPackedSampleModel) {
            this.optimal = true;
         }

         numXTiles = this.getMaxTileX() - this.getMinTileX() + 1;
         int totalTiles = numXTiles * (this.getMaxTileY() - this.getMinTileY() + 1);

         for(y = this.getMinTileY(); y <= this.getMaxTileY(); ++y) {
            for(numBank = this.getMinTileX(); numBank <= this.getMaxTileX(); ++numBank) {
               this.writeRaster(this.getTile(numBank, y));
               float percentage = ((float)(numBank + y * numXTiles) + 1.0F) / (float)totalTiles;
               this.processImageProgress(percentage * 100.0F);
            }
         }

         this.stream.flush();
         if (this.abortRequested()) {
            this.processWriteAborted();
         } else {
            this.processImageComplete();
         }

      }
   }

   public int getWidth() {
      return this.destinationRegion.width;
   }

   public int getHeight() {
      return this.destinationRegion.height;
   }

   private void writeRaster(Raster raster) throws IOException {
      int numBank = 0;
      int bandStride = 0;
      int[] bankIndices = null;
      int[] bandOffsets = null;
      int bandSize = 0;
      int numBand = this.sampleModel.getNumBands();
      int type = this.sampleModel.getDataType();
      if (this.sampleModel instanceof ComponentSampleModel) {
         ComponentSampleModel csm = (ComponentSampleModel)this.sampleModel;
         bandOffsets = csm.getBandOffsets();

         int i;
         for(i = 0; i < numBand; ++i) {
            if (bandStride < bandOffsets[i]) {
               bandStride = bandOffsets[i];
            }
         }

         bankIndices = csm.getBankIndices();

         for(i = 0; i < numBand; ++i) {
            if (numBank < bankIndices[i]) {
               numBank = bankIndices[i];
            }
         }

         bandSize = (int)ImageUtil.getBandSize(this.sampleModel);
      }

      byte[] bdata = null;
      short[] sdata = null;
      int[] idata = null;
      float[] fdata = null;
      double[] ddata = null;
      if (((Raster)raster).getParent() != null && !this.sampleModel.equals(((Raster)raster).getParent().getSampleModel())) {
         WritableRaster ras = Raster.createWritableRaster(this.sampleModel, new Point(((Raster)raster).getMinX(), ((Raster)raster).getMinY()));
         ras.setRect((Raster)raster);
         raster = ras;
      }

      DataBuffer data = ((Raster)raster).getDataBuffer();
      int offset;
      if (this.optimal) {
         int i;
         if (numBank > 0) {
            for(i = 0; i < this.numBands; ++i) {
               offset = bankIndices[this.sourceBands[i]];
               switch (type) {
                  case 0:
                     bdata = ((DataBufferByte)data).getData(offset);
                     this.stream.write(bdata, 0, bdata.length);
                     break;
                  case 1:
                     sdata = ((DataBufferUShort)data).getData(offset);
                     this.stream.writeShorts(sdata, 0, sdata.length);
                     break;
                  case 2:
                     sdata = ((DataBufferShort)data).getData(offset);
                     this.stream.writeShorts(sdata, 0, sdata.length);
                     break;
                  case 3:
                     idata = ((DataBufferInt)data).getData(offset);
                     this.stream.writeInts(idata, 0, idata.length);
                     break;
                  case 4:
                     fdata = ((DataBufferFloat)data).getData(offset);
                     this.stream.writeFloats(fdata, 0, fdata.length);
                     break;
                  case 5:
                     ddata = ((DataBufferDouble)data).getData(offset);
                     this.stream.writeDoubles(ddata, 0, ddata.length);
               }
            }
         } else {
            switch (type) {
               case 0:
                  bdata = ((DataBufferByte)data).getData();
                  break;
               case 1:
                  sdata = ((DataBufferUShort)data).getData();
                  break;
               case 2:
                  sdata = ((DataBufferShort)data).getData();
                  break;
               case 3:
                  idata = ((DataBufferInt)data).getData();
                  break;
               case 4:
                  fdata = ((DataBufferFloat)data).getData();
                  break;
               case 5:
                  ddata = ((DataBufferDouble)data).getData();
            }

            if (!this.noSubband && bandStride >= ((Raster)raster).getWidth() * ((Raster)raster).getHeight() * (this.numBands - 1)) {
               for(i = 0; i < this.numBands; ++i) {
                  offset = bandOffsets[this.sourceBands[i]];
                  switch (type) {
                     case 0:
                        this.stream.write(bdata, offset, bandSize);
                        break;
                     case 1:
                     case 2:
                        this.stream.writeShorts(sdata, offset, bandSize);
                        break;
                     case 3:
                        this.stream.writeInts(idata, offset, bandSize);
                        break;
                     case 4:
                        this.stream.writeFloats(fdata, offset, bandSize);
                        break;
                     case 5:
                        this.stream.writeDoubles(ddata, offset, bandSize);
                  }
               }
            } else {
               switch (type) {
                  case 0:
                     this.stream.write(bdata, 0, bdata.length);
                     break;
                  case 1:
                  case 2:
                     this.stream.writeShorts(sdata, 0, sdata.length);
                     break;
                  case 3:
                     this.stream.writeInts(idata, 0, idata.length);
                     break;
                  case 4:
                     this.stream.writeFloats(fdata, 0, fdata.length);
                     break;
                  case 5:
                     this.stream.writeDoubles(ddata, 0, ddata.length);
               }
            }
         }
      } else if (this.sampleModel instanceof ComponentSampleModel) {
         switch (type) {
            case 0:
               bdata = ((DataBufferByte)data).getData();
               break;
            case 1:
               sdata = ((DataBufferUShort)data).getData();
               break;
            case 2:
               sdata = ((DataBufferShort)data).getData();
               break;
            case 3:
               idata = ((DataBufferInt)data).getData();
               break;
            case 4:
               fdata = ((DataBufferFloat)data).getData();
               break;
            case 5:
               ddata = ((DataBufferDouble)data).getData();
         }

         ComponentSampleModel csm = (ComponentSampleModel)this.sampleModel;
         offset = csm.getOffset(((Raster)raster).getMinX() - ((Raster)raster).getSampleModelTranslateX(), ((Raster)raster).getMinY() - ((Raster)raster).getSampleModelTranslateY()) - bandOffsets[0];
         int srcSkip = this.pxlStride;
         int copyLength = 1;
         int innerStep = this.pxlStride;
         int width = ((Raster)raster).getWidth();
         int height = ((Raster)raster).getHeight();
         int innerBound = width;
         int outerBound = height;
         if (srcSkip < this.lineStride) {
            if (bandStride > this.pxlStride) {
               copyLength = width;
            }

            srcSkip = this.lineStride;
         } else {
            if (bandStride > this.lineStride) {
               copyLength = height;
            }

            innerStep = this.lineStride;
            innerBound = height;
            outerBound = width;
         }

         int writeLength = innerBound * this.numBands;
         byte[] destBBuf = null;
         short[] destSBuf = null;
         int[] destIBuf = null;
         float[] destFBuf = null;
         double[] destDBuf = null;
         Object srcBuf = null;
         Object dstBuf = null;
         switch (type) {
            case 0:
               srcBuf = bdata;
               dstBuf = destBBuf = new byte[writeLength];
               break;
            case 1:
            case 2:
               srcBuf = sdata;
               dstBuf = destSBuf = new short[writeLength];
               break;
            case 3:
               srcBuf = idata;
               dstBuf = destIBuf = new int[writeLength];
               break;
            case 4:
               srcBuf = fdata;
               dstBuf = destFBuf = new float[writeLength];
               break;
            case 5:
               srcBuf = ddata;
               dstBuf = destDBuf = new double[writeLength];
         }

         int i;
         int b;
         int k;
         if (copyLength > 1) {
            for(i = 0; i < outerBound; ++i) {
               for(b = 0; b < this.numBands; ++b) {
                  k = bandOffsets[b];
                  System.arraycopy(srcBuf, offset + k, dstBuf, b * innerBound, innerBound);
               }

               switch (type) {
                  case 0:
                     this.stream.write((byte[])((byte[])dstBuf), 0, writeLength);
                     break;
                  case 1:
                  case 2:
                     this.stream.writeShorts((short[])((short[])dstBuf), 0, writeLength);
                     break;
                  case 3:
                     this.stream.writeInts((int[])((int[])dstBuf), 0, writeLength);
                     break;
                  case 4:
                     this.stream.writeFloats((float[])((float[])dstBuf), 0, writeLength);
                     break;
                  case 5:
                     this.stream.writeDoubles((double[])((double[])dstBuf), 0, writeLength);
               }

               offset += srcSkip;
            }
         } else {
            int bandOffset;
            int j;
            int m;
            switch (type) {
               case 0:
                  for(i = 0; i < outerBound; ++i) {
                     b = 0;

                     for(k = 0; b < this.numBands; ++b) {
                        bandOffset = bandOffsets[b];
                        j = 0;

                        for(m = offset; j < innerBound; m += innerStep) {
                           destBBuf[k++] = bdata[m + bandOffset];
                           ++j;
                        }
                     }

                     this.stream.write(destBBuf, 0, writeLength);
                     offset += srcSkip;
                  }

                  return;
               case 1:
               case 2:
                  for(i = 0; i < outerBound; ++i) {
                     b = 0;

                     for(k = 0; b < this.numBands; ++b) {
                        bandOffset = bandOffsets[b];
                        j = 0;

                        for(m = offset; j < innerBound; m += innerStep) {
                           destSBuf[k++] = sdata[m + bandOffset];
                           ++j;
                        }
                     }

                     this.stream.writeShorts(destSBuf, 0, writeLength);
                     offset += srcSkip;
                  }

                  return;
               case 3:
                  for(i = 0; i < outerBound; ++i) {
                     b = 0;

                     for(k = 0; b < this.numBands; ++b) {
                        bandOffset = bandOffsets[b];
                        j = 0;

                        for(m = offset; j < innerBound; m += innerStep) {
                           destIBuf[k++] = idata[m + bandOffset];
                           ++j;
                        }
                     }

                     this.stream.writeInts(destIBuf, 0, writeLength);
                     offset += srcSkip;
                  }

                  return;
               case 4:
                  for(i = 0; i < outerBound; ++i) {
                     b = 0;

                     for(k = 0; b < this.numBands; ++b) {
                        bandOffset = bandOffsets[b];
                        j = 0;

                        for(m = offset; j < innerBound; m += innerStep) {
                           destFBuf[k++] = fdata[m + bandOffset];
                           ++j;
                        }
                     }

                     this.stream.writeFloats(destFBuf, 0, writeLength);
                     offset += srcSkip;
                  }

                  return;
               case 5:
                  for(i = 0; i < outerBound; ++i) {
                     b = 0;

                     for(k = 0; b < this.numBands; ++b) {
                        bandOffset = bandOffsets[b];
                        j = 0;

                        for(m = offset; j < innerBound; m += innerStep) {
                           destDBuf[k++] = ddata[m + bandOffset];
                           ++j;
                        }
                     }

                     this.stream.writeDoubles(destDBuf, 0, writeLength);
                     offset += srcSkip;
                  }
            }
         }
      }

   }

   private Raster getTile(int tileX, int tileY) {
      int sx = this.tileXOffset + tileX * this.tileWidth;
      int sy = this.tileYOffset + tileY * this.tileHeight;
      Rectangle bounds = new Rectangle(sx, sy, this.tileWidth, this.tileHeight);
      WritableRaster ras;
      int x;
      int y;
      int minY;
      int maxY;
      int cTileWidth;
      int length;
      int j;
      Raster source;
      int tempX;
      int i;
      int offset;
      int k;
      int p;
      if (this.writeRaster) {
         bounds = bounds.intersection(this.destinationRegion);
         if (this.noTransform) {
            return this.inputRaster.createChild(bounds.x, bounds.y, bounds.width, bounds.height, bounds.x, bounds.y, this.sourceBands);
         } else {
            sx = bounds.x;
            sy = bounds.y;
            ras = Raster.createWritableRaster(this.sampleModel, new Point(sx, sy));
            x = this.mapToSourceX(sx);
            y = this.mapToSourceY(sy);
            minY = this.inputRaster.getMinY();
            maxY = this.inputRaster.getMinY() + this.inputRaster.getHeight();
            cTileWidth = bounds.width;
            length = (cTileWidth - 1) * this.scaleX + 1;

            for(j = 0; j < bounds.height; y += this.scaleY) {
               if (y >= minY && y < maxY) {
                  source = this.inputRaster.createChild(x, y, length, 1, x, y, (int[])null);
                  tempX = sx;
                  i = 0;

                  for(offset = x; i < cTileWidth; offset += this.scaleX) {
                     for(k = 0; k < this.numBands; ++k) {
                        p = source.getSample(offset, y, this.sourceBands[k]);
                        ras.setSample(tempX, sy, k, p);
                     }

                     ++i;
                     ++tempX;
                  }
               }

               ++j;
               ++sy;
            }

            return ras;
         }
      } else if (this.noTransform) {
         Raster ras = this.input.getTile(tileX, tileY);
         if (this.destinationRegion.contains(bounds) && this.noSubband) {
            return ras;
         } else {
            bounds = bounds.intersection(this.destinationRegion);
            return ras.createChild(bounds.x, bounds.y, bounds.width, bounds.height, bounds.x, bounds.y, this.sourceBands);
         }
      } else {
         bounds = bounds.intersection(this.destinationRegion);
         sx = bounds.x;
         sy = bounds.y;
         ras = Raster.createWritableRaster(this.sampleModel, new Point(sx, sy));
         x = this.mapToSourceX(sx);
         y = this.mapToSourceY(sy);
         minY = this.input.getMinY();
         maxY = this.input.getMinY() + this.input.getHeight();
         cTileWidth = bounds.width;
         length = (cTileWidth - 1) * this.scaleX + 1;

         for(j = 0; j < bounds.height; y += this.scaleY) {
            if (y >= minY && y < maxY) {
               source = this.input.getData(new Rectangle(x, y, length, 1));
               tempX = sx;
               i = 0;

               for(offset = x; i < cTileWidth; offset += this.scaleX) {
                  for(k = 0; k < this.numBands; ++k) {
                     p = source.getSample(offset, y, this.sourceBands[k]);
                     ras.setSample(tempX, sy, k, p);
                  }

                  ++i;
                  ++tempX;
               }
            }

            ++j;
            ++sy;
         }

         return ras;
      }
   }

   private int mapToSourceX(int x) {
      return x * this.scaleX + this.xOffset;
   }

   private int mapToSourceY(int y) {
      return y * this.scaleY + this.yOffset;
   }

   private int getMinTileX() {
      return ToTile(this.destinationRegion.x, this.tileXOffset, this.tileWidth);
   }

   private int getMaxTileX() {
      return ToTile(this.destinationRegion.x + this.destinationRegion.width - 1, this.tileXOffset, this.tileWidth);
   }

   private int getMinTileY() {
      return ToTile(this.destinationRegion.y, this.tileYOffset, this.tileHeight);
   }

   private int getMaxTileY() {
      return ToTile(this.destinationRegion.y + this.destinationRegion.height - 1, this.tileYOffset, this.tileHeight);
   }

   private static int ToTile(int pos, int tileOffset, int tileSize) {
      pos -= tileOffset;
      if (pos < 0) {
         pos += 1 - tileSize;
      }

      return pos / tileSize;
   }

   public void reset() {
      super.reset();
      this.stream = null;
      this.optimal = false;
      this.sourceBands = null;
      this.destinationRegion = null;
      this.noTransform = true;
      this.noSubband = true;
      this.writeRaster = false;
   }
}
