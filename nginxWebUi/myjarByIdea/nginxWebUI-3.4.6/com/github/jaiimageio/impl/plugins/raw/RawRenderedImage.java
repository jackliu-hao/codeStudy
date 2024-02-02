package com.github.jaiimageio.impl.plugins.raw;

import com.github.jaiimageio.impl.common.ImageUtil;
import com.github.jaiimageio.impl.common.SimpleRenderedImage;
import com.github.jaiimageio.stream.RawImageInputStream;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;

public class RawRenderedImage extends SimpleRenderedImage {
   private SampleModel originalSampleModel;
   private Raster currentTile;
   private Point currentTileGrid;
   private RawImageInputStream iis = null;
   private RawImageReader reader;
   private ImageReadParam param = null;
   private int imageIndex;
   private Rectangle destinationRegion;
   private Rectangle originalRegion;
   private Point sourceOrigin;
   private Dimension originalDimension;
   private int maxXTile;
   private int maxYTile;
   private int scaleX;
   private int scaleY;
   private int xOffset;
   private int yOffset;
   private int[] destinationBands = null;
   private int[] sourceBands = null;
   private int nComp;
   private boolean noTransform = true;
   private WritableRaster rasForATile;
   private BufferedImage destImage;
   private long position;
   private long tileDataSize;
   private int originalNumXTiles;

   public RawRenderedImage(RawImageInputStream iis, RawImageReader reader, ImageReadParam param, int imageIndex) throws IOException {
      this.iis = iis;
      this.reader = reader;
      this.param = param;
      this.imageIndex = imageIndex;
      this.position = iis.getImageOffset(imageIndex);
      this.originalDimension = iis.getImageDimension(imageIndex);
      ImageTypeSpecifier type = iis.getImageType();
      this.sampleModel = this.originalSampleModel = type.getSampleModel();
      this.colorModel = type.getColorModel();
      this.sourceBands = param == null ? null : param.getSourceBands();
      int i;
      if (this.sourceBands == null) {
         this.nComp = this.originalSampleModel.getNumBands();
         this.sourceBands = new int[this.nComp];

         for(i = 0; i < this.nComp; this.sourceBands[i] = i++) {
         }
      } else {
         this.sampleModel = this.originalSampleModel.createSubsetSampleModel(this.sourceBands);
         this.colorModel = ImageUtil.createColorModel((ColorSpace)null, this.sampleModel);
      }

      this.nComp = this.sourceBands.length;
      this.destinationBands = param == null ? null : param.getDestinationBands();
      if (this.destinationBands == null) {
         this.destinationBands = new int[this.nComp];

         for(i = 0; i < this.nComp; this.destinationBands[i] = i++) {
         }
      }

      Dimension dim = iis.getImageDimension(imageIndex);
      this.width = dim.width;
      this.height = dim.height;
      Rectangle sourceRegion = new Rectangle(0, 0, this.width, this.height);
      this.originalRegion = (Rectangle)sourceRegion.clone();
      this.destinationRegion = (Rectangle)sourceRegion.clone();
      if (param != null) {
         RawImageReader.computeRegionsWrapper(param, this.width, this.height, param.getDestination(), sourceRegion, this.destinationRegion);
         this.scaleX = param.getSourceXSubsampling();
         this.scaleY = param.getSourceYSubsampling();
         this.xOffset = param.getSubsamplingXOffset();
         this.yOffset = param.getSubsamplingYOffset();
      }

      this.sourceOrigin = new Point(sourceRegion.x, sourceRegion.y);
      if (!this.destinationRegion.equals(sourceRegion)) {
         this.noTransform = false;
      }

      this.tileDataSize = ImageUtil.getTileSize(this.originalSampleModel);
      this.tileWidth = this.originalSampleModel.getWidth();
      this.tileHeight = this.originalSampleModel.getHeight();
      this.tileGridXOffset = this.destinationRegion.x;
      this.tileGridYOffset = this.destinationRegion.y;
      this.originalNumXTiles = this.getNumXTiles();
      this.width = this.destinationRegion.width;
      this.height = this.destinationRegion.height;
      this.minX = this.destinationRegion.x;
      this.minY = this.destinationRegion.y;
      this.sampleModel = this.sampleModel.createCompatibleSampleModel(this.tileWidth, this.tileHeight);
      this.maxXTile = this.originalDimension.width / this.tileWidth;
      this.maxYTile = this.originalDimension.height / this.tileHeight;
   }

   public synchronized Raster getTile(int tileX, int tileY) {
      if (this.currentTile != null && this.currentTileGrid.x == tileX && this.currentTileGrid.y == tileY) {
         return this.currentTile;
      } else if (tileX < this.getNumXTiles() && tileY < this.getNumYTiles()) {
         try {
            this.iis.seek(this.position + (long)(tileY * this.originalNumXTiles + tileX) * this.tileDataSize);
            int x = this.tileXToX(tileX);
            int y = this.tileYToY(tileY);
            this.currentTile = Raster.createWritableRaster(this.sampleModel, new Point(x, y));
            if (this.noTransform) {
               label81:
               switch (this.sampleModel.getDataType()) {
                  case 0:
                     byte[][] buf = ((DataBufferByte)this.currentTile.getDataBuffer()).getBankData();
                     int i = 0;

                     while(true) {
                        if (i >= buf.length) {
                           break label81;
                        }

                        this.iis.readFully((byte[])buf[i], 0, buf[i].length);
                        ++i;
                     }
                  case 1:
                     short[][] usbuf = ((DataBufferUShort)this.currentTile.getDataBuffer()).getBankData();
                     int i = 0;

                     while(true) {
                        if (i >= usbuf.length) {
                           break label81;
                        }

                        this.iis.readFully((short[])usbuf[i], 0, usbuf[i].length);
                        ++i;
                     }
                  case 2:
                     short[][] sbuf = ((DataBufferShort)this.currentTile.getDataBuffer()).getBankData();
                     int i = 0;

                     while(true) {
                        if (i >= sbuf.length) {
                           break label81;
                        }

                        this.iis.readFully((short[])sbuf[i], 0, sbuf[i].length);
                        ++i;
                     }
                  case 3:
                     int[][] ibuf = ((DataBufferInt)this.currentTile.getDataBuffer()).getBankData();
                     int i = 0;

                     while(true) {
                        if (i >= ibuf.length) {
                           break label81;
                        }

                        this.iis.readFully((int[])ibuf[i], 0, ibuf[i].length);
                        ++i;
                     }
                  case 4:
                     float[][] fbuf = ((DataBufferFloat)this.currentTile.getDataBuffer()).getBankData();
                     int i = 0;

                     while(true) {
                        if (i >= fbuf.length) {
                           break label81;
                        }

                        this.iis.readFully((float[])fbuf[i], 0, fbuf[i].length);
                        ++i;
                     }
                  case 5:
                     double[][] dbuf = ((DataBufferDouble)this.currentTile.getDataBuffer()).getBankData();

                     for(int i = 0; i < dbuf.length; ++i) {
                        this.iis.readFully((double[])dbuf[i], 0, dbuf[i].length);
                     }
               }
            } else {
               this.currentTile = this.readSubsampledRaster((WritableRaster)this.currentTile);
            }
         } catch (IOException var12) {
            throw new RuntimeException(var12);
         }

         if (this.currentTileGrid == null) {
            this.currentTileGrid = new Point(tileX, tileY);
         } else {
            this.currentTileGrid.x = tileX;
            this.currentTileGrid.y = tileY;
         }

         return this.currentTile;
      } else {
         throw new IllegalArgumentException(I18N.getString("RawRenderedImage0"));
      }
   }

   public void readAsRaster(WritableRaster raster) throws IOException {
      this.readSubsampledRaster(raster);
   }

   private Raster readSubsampledRaster(WritableRaster raster) throws IOException {
      if (raster == null) {
         raster = Raster.createWritableRaster(this.sampleModel.createCompatibleSampleModel(this.destinationRegion.x + this.destinationRegion.width, this.destinationRegion.y + this.destinationRegion.height), new Point(this.destinationRegion.x, this.destinationRegion.y));
      }

      int numBands = this.sourceBands.length;
      int dataType = this.sampleModel.getDataType();
      int sampleSizeBit = DataBuffer.getDataTypeSize(dataType);
      int sampleSizeByte = (sampleSizeBit + 7) / 8;
      Rectangle destRect = raster.getBounds().intersection(this.destinationRegion);
      int offx = this.destinationRegion.x;
      int offy = this.destinationRegion.y;
      int sourceSX = (destRect.x - offx) * this.scaleX + this.sourceOrigin.x;
      int sourceSY = (destRect.y - offy) * this.scaleY + this.sourceOrigin.y;
      int sourceEX = (destRect.width - 1) * this.scaleX + sourceSX;
      int sourceEY = (destRect.height - 1) * this.scaleY + sourceSY;
      int startXTile = sourceSX / this.tileWidth;
      int startYTile = sourceSY / this.tileHeight;
      int endXTile = sourceEX / this.tileWidth;
      int endYTile = sourceEY / this.tileHeight;
      startXTile = this.clip(startXTile, 0, this.maxXTile);
      startYTile = this.clip(startYTile, 0, this.maxYTile);
      endXTile = this.clip(endXTile, 0, this.maxXTile);
      endYTile = this.clip(endYTile, 0, this.maxYTile);
      int totalXTiles = this.getNumXTiles();
      int totalYTiles = this.getNumYTiles();
      int totalTiles = totalXTiles * totalYTiles;
      byte[] pixbuf = null;
      short[] spixbuf = null;
      int[] ipixbuf = null;
      float[] fpixbuf = null;
      double[] dpixbuf = null;
      boolean singleBank = true;
      int pixelStride = 0;
      int scanlineStride = 0;
      int bandStride = 0;
      int[] bandOffsets = null;
      int[] bankIndices = null;
      if (this.originalSampleModel instanceof ComponentSampleModel) {
         ComponentSampleModel csm = (ComponentSampleModel)this.originalSampleModel;
         bankIndices = csm.getBankIndices();
         int maxBank = 0;

         int i;
         for(i = 0; i < bankIndices.length; ++i) {
            if (maxBank > bankIndices[i]) {
               maxBank = bankIndices[i];
            }
         }

         if (maxBank > 0) {
            singleBank = false;
         }

         pixelStride = csm.getPixelStride();
         scanlineStride = csm.getScanlineStride();
         bandOffsets = csm.getBandOffsets();

         for(i = 0; i < bandOffsets.length; ++i) {
            if (bandStride < bandOffsets[i]) {
               bandStride = bandOffsets[i];
            }
         }
      } else if (this.originalSampleModel instanceof MultiPixelPackedSampleModel) {
         scanlineStride = ((MultiPixelPackedSampleModel)this.originalSampleModel).getScanlineStride();
      } else if (this.originalSampleModel instanceof SinglePixelPackedSampleModel) {
         pixelStride = 1;
         scanlineStride = ((SinglePixelPackedSampleModel)this.originalSampleModel).getScanlineStride();
      }

      byte[] destPixbuf = null;
      short[] destSPixbuf = null;
      int[] destIPixbuf = null;
      float[] destFPixbuf = null;
      double[] destDPixbuf = null;
      int[] destBandOffsets = null;
      int destPixelStride = 0;
      int destScanlineStride = 0;
      int destSX = 0;
      int y;
      if (raster.getSampleModel() instanceof ComponentSampleModel) {
         ComponentSampleModel csm = (ComponentSampleModel)raster.getSampleModel();
         bankIndices = csm.getBankIndices();
         destBandOffsets = csm.getBandOffsets();
         destPixelStride = csm.getPixelStride();
         destScanlineStride = csm.getScanlineStride();
         destSX = csm.getOffset(raster.getMinX() - raster.getSampleModelTranslateX(), raster.getMinY() - raster.getSampleModelTranslateY()) - destBandOffsets[0];
         switch (dataType) {
            case 0:
               destPixbuf = ((DataBufferByte)raster.getDataBuffer()).getData();
               break;
            case 1:
               destSPixbuf = ((DataBufferUShort)raster.getDataBuffer()).getData();
               break;
            case 2:
               destSPixbuf = ((DataBufferShort)raster.getDataBuffer()).getData();
               break;
            case 3:
               destIPixbuf = ((DataBufferInt)raster.getDataBuffer()).getData();
               break;
            case 4:
               destFPixbuf = ((DataBufferFloat)raster.getDataBuffer()).getData();
               break;
            case 5:
               destDPixbuf = ((DataBufferDouble)raster.getDataBuffer()).getData();
         }
      } else if (raster.getSampleModel() instanceof SinglePixelPackedSampleModel) {
         numBands = 1;
         bankIndices = new int[]{0};
         destBandOffsets = new int[numBands];

         for(y = 0; y < numBands; ++y) {
            destBandOffsets[y] = 0;
         }

         destPixelStride = 1;
         destScanlineStride = ((SinglePixelPackedSampleModel)raster.getSampleModel()).getScanlineStride();
      }

      for(y = startYTile; y <= endYTile && !this.reader.getAbortRequest(); ++y) {
         for(int x = startXTile; x <= endXTile && !this.reader.getAbortRequest(); ++x) {
            long tilePosition = this.position + (long)(y * this.originalNumXTiles + x) * this.tileDataSize;
            this.iis.seek(tilePosition);
            float percentage = (float)((x - startXTile + y * totalXTiles) / totalXTiles);
            int startX = x * this.tileWidth;
            int startY = y * this.tileHeight;
            int cTileHeight = this.tileHeight;
            int cTileWidth = this.tileWidth;
            if (startY + cTileHeight >= this.originalDimension.height) {
               cTileHeight = this.originalDimension.height - startY;
            }

            if (startX + cTileWidth >= this.originalDimension.width) {
               cTileWidth = this.originalDimension.width - startX;
            }

            int tx = startX;
            int ty = startY;
            if (sourceSX > startX) {
               cTileWidth += startX - sourceSX;
               tx = sourceSX;
               startX = sourceSX;
            }

            if (sourceSY > startY) {
               cTileHeight += startY - sourceSY;
               ty = sourceSY;
               startY = sourceSY;
            }

            if (sourceEX < startX + cTileWidth - 1) {
               cTileWidth += sourceEX - startX - cTileWidth + 1;
            }

            if (sourceEY < startY + cTileHeight - 1) {
               cTileHeight += sourceEY - startY - cTileHeight + 1;
            }

            int x1 = (startX + this.scaleX - 1 - this.sourceOrigin.x) / this.scaleX;
            int x2 = (startX + this.scaleX - 1 + cTileWidth - this.sourceOrigin.x) / this.scaleX;
            int lineLength = x2 - x1;
            x2 = (x2 - 1) * this.scaleX + this.sourceOrigin.x;
            int y1 = (startY + this.scaleY - 1 - this.sourceOrigin.y) / this.scaleY;
            startX = x1 * this.scaleX + this.sourceOrigin.x;
            startY = y1 * this.scaleY + this.sourceOrigin.y;
            x1 += offx;
            y1 += offy;
            tx -= x * this.tileWidth;
            ty -= y * this.tileHeight;
            int skipLength;
            int bufferSize;
            int b;
            int b;
            int destPos;
            int innerStep;
            int innerStep1;
            int skipX;
            int outerBound;
            int innerStep;
            if (this.sampleModel instanceof MultiPixelPackedSampleModel) {
               MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)this.originalSampleModel;
               this.iis.skipBytes(mppsm.getOffset(tx, ty) * sampleSizeByte);
               skipLength = (mppsm.getOffset(x2, 0) - mppsm.getOffset(startX, 0) + 1) * sampleSizeByte;
               bufferSize = (scanlineStride * this.scaleY - skipLength) * sampleSizeByte;
               skipLength *= sampleSizeByte;
               if (pixbuf == null || pixbuf.length < skipLength) {
                  pixbuf = new byte[skipLength];
               }

               b = mppsm.getBitOffset(tx);
               b = 0;

               for(destPos = y1; b < cTileHeight && !this.reader.getAbortRequest(); ++destPos) {
                  this.iis.readFully((byte[])pixbuf, 0, skipLength);
                  if (this.scaleX != 1) {
                     innerStep = 7;
                     innerStep1 = 0;
                     int mask = true;
                     outerBound = 0;

                     for(innerStep = startX & 7; outerBound < lineLength; innerStep += this.scaleX) {
                        pixbuf[innerStep1] = (byte)(pixbuf[innerStep1] & ~(1 << innerStep) | (pixbuf[innerStep >> 3] >> 7 - (innerStep & 7) & 1) << innerStep);
                        --innerStep;
                        if (innerStep == -1) {
                           innerStep = 7;
                           ++innerStep1;
                        }

                        ++outerBound;
                     }
                  } else if (b != 0) {
                     innerStep = 255 << b & 255;
                     innerStep1 = ~innerStep & 255;
                     skipX = 8 - b;

                     for(outerBound = 0; outerBound < skipLength - 1; ++outerBound) {
                        pixbuf[outerBound] = (byte)((pixbuf[outerBound] & innerStep1) << skipX | (pixbuf[outerBound + 1] & innerStep) >> b);
                     }

                     pixbuf[outerBound] = (byte)((pixbuf[outerBound] & innerStep1) << skipX);
                  }

                  ImageUtil.setPackedBinaryData(pixbuf, raster, new Rectangle(x1, destPos, lineLength, 1));
                  this.iis.skipBytes(bufferSize);
                  if (this.destImage != null) {
                     this.reader.processImageUpdateWrapper(this.destImage, x1, destPos, cTileWidth, 1, 1, 1, this.destinationBands);
                  }

                  this.reader.processImageProgressWrapper(percentage + ((float)(b - startY) + 1.0F) / (float)cTileHeight / (float)totalTiles);
                  b += this.scaleY;
               }
            } else {
               int readLength;
               if (pixelStride < scanlineStride) {
                  readLength = cTileWidth * pixelStride;
                  skipLength = (scanlineStride * this.scaleY - readLength) * sampleSizeByte;
               } else {
                  readLength = cTileHeight * scanlineStride;
                  skipLength = (pixelStride * this.scaleX - readLength) * sampleSizeByte;
               }

               switch (this.sampleModel.getDataType()) {
                  case 0:
                     if (pixbuf == null || pixbuf.length < readLength) {
                        pixbuf = new byte[readLength];
                     }
                     break;
                  case 1:
                  case 2:
                     if (spixbuf == null || spixbuf.length < readLength) {
                        spixbuf = new short[readLength];
                     }
                     break;
                  case 3:
                     if (ipixbuf == null || ipixbuf.length < readLength) {
                        ipixbuf = new int[readLength];
                     }
                     break;
                  case 4:
                     if (fpixbuf == null || fpixbuf.length < readLength) {
                        fpixbuf = new float[readLength];
                     }
                     break;
                  case 5:
                     if (dpixbuf == null || dpixbuf.length < readLength) {
                        dpixbuf = new double[readLength];
                     }
               }

               int innerStep1;
               int m1;
               int m2;
               int sourceBandOffset;
               int l;
               int m;
               if (this.sampleModel instanceof PixelInterleavedSampleModel) {
                  this.iis.skipBytes((tx * pixelStride + ty * scanlineStride) * sampleSizeByte);
                  byte outerFirst;
                  if (pixelStride < scanlineStride) {
                     outerFirst = 0;
                     b = y1;
                     b = this.scaleY;
                     destPos = cTileHeight;
                     innerStep = this.scaleX * pixelStride;
                     innerStep1 = destPixelStride;
                     skipX = destScanlineStride;
                  } else {
                     outerFirst = 0;
                     b = x1;
                     b = this.scaleX;
                     destPos = cTileWidth;
                     innerStep = this.scaleY * scanlineStride;
                     innerStep1 = destScanlineStride;
                     skipX = destPixelStride;
                  }

                  outerBound = destSX + (y1 - raster.getSampleModelTranslateY()) * destScanlineStride + (x1 - raster.getSampleModelTranslateX()) * destPixelStride;
                  innerStep = outerFirst;

                  for(innerStep1 = b; innerStep < destPos && !this.reader.getAbortRequest(); ++innerStep1) {
                     switch (dataType) {
                        case 0:
                           if (innerStep == numBands && innerStep1 == numBands) {
                              this.iis.readFully(destPixbuf, outerBound, readLength);
                              break;
                           }

                           this.iis.readFully((byte[])pixbuf, 0, readLength);
                           break;
                        case 1:
                        case 2:
                           if (innerStep == numBands && innerStep1 == numBands) {
                              this.iis.readFully(destSPixbuf, outerBound, readLength);
                              break;
                           }

                           this.iis.readFully((short[])spixbuf, 0, readLength);
                           break;
                        case 3:
                           if (innerStep == numBands && innerStep1 == numBands) {
                              this.iis.readFully(destIPixbuf, outerBound, readLength);
                              break;
                           }

                           this.iis.readFully((int[])ipixbuf, 0, readLength);
                           break;
                        case 4:
                           if (innerStep == numBands && innerStep1 == numBands) {
                              this.iis.readFully(destFPixbuf, outerBound, readLength);
                              break;
                           }

                           this.iis.readFully((float[])fpixbuf, 0, readLength);
                           break;
                        case 5:
                           if (innerStep == numBands && innerStep1 == numBands) {
                              this.iis.readFully(destDPixbuf, outerBound, readLength);
                           } else {
                              this.iis.readFully((double[])dpixbuf, 0, readLength);
                           }
                     }

                     if (innerStep != numBands || innerStep1 != numBands) {
                        for(m1 = 0; m1 < numBands; ++m1) {
                           m2 = destBandOffsets[this.destinationBands[m1]];
                           outerBound += m2;
                           sourceBandOffset = bandOffsets[this.sourceBands[m1]];
                           label516:
                           switch (dataType) {
                              case 0:
                                 l = 0;
                                 m = outerBound;

                                 while(true) {
                                    if (l >= readLength) {
                                       break label516;
                                    }

                                    destPixbuf[m] = pixbuf[l + sourceBandOffset];
                                    l += innerStep;
                                    m += innerStep1;
                                 }
                              case 1:
                              case 2:
                                 l = 0;
                                 m = outerBound;

                                 while(true) {
                                    if (l >= readLength) {
                                       break label516;
                                    }

                                    destSPixbuf[m] = spixbuf[l + sourceBandOffset];
                                    l += innerStep;
                                    m += innerStep1;
                                 }
                              case 3:
                                 l = 0;
                                 m = outerBound;

                                 while(true) {
                                    if (l >= readLength) {
                                       break label516;
                                    }

                                    destIPixbuf[m] = ipixbuf[l + sourceBandOffset];
                                    l += innerStep;
                                    m += innerStep1;
                                 }
                              case 4:
                                 l = 0;
                                 m = outerBound;

                                 while(true) {
                                    if (l >= readLength) {
                                       break label516;
                                    }

                                    destFPixbuf[m] = fpixbuf[l + sourceBandOffset];
                                    l += innerStep;
                                    m += innerStep1;
                                 }
                              case 5:
                                 l = 0;

                                 for(m = outerBound; l < readLength; m += innerStep1) {
                                    destDPixbuf[m] = dpixbuf[l + sourceBandOffset];
                                    l += innerStep;
                                 }
                           }

                           outerBound -= m2;
                        }
                     }

                     this.iis.skipBytes(skipLength);
                     outerBound += skipX;
                     if (this.destImage != null) {
                        if (pixelStride < scanlineStride) {
                           this.reader.processImageUpdateWrapper(this.destImage, x1, innerStep1, destPos, 1, 1, 1, this.destinationBands);
                        } else {
                           this.reader.processImageUpdateWrapper(this.destImage, innerStep1, y1, 1, destPos, 1, 1, this.destinationBands);
                        }
                     }

                     this.reader.processImageProgressWrapper(percentage + ((float)innerStep + 1.0F) / (float)destPos / (float)totalTiles);
                     innerStep += b;
                  }
               } else if (!(this.sampleModel instanceof BandedSampleModel) && !(this.sampleModel instanceof SinglePixelPackedSampleModel) && bandStride != 0) {
                  if (!(this.sampleModel instanceof ComponentSampleModel)) {
                     throw new IllegalArgumentException(I18N.getString("RawRenderedImage1"));
                  }

                  bufferSize = (int)this.tileDataSize;
                  switch (this.sampleModel.getDataType()) {
                     case 0:
                        if (pixbuf == null || (long)pixbuf.length < this.tileDataSize) {
                           pixbuf = new byte[(int)this.tileDataSize];
                        }

                        this.iis.readFully((byte[])pixbuf, 0, (int)this.tileDataSize);
                        break;
                     case 1:
                     case 2:
                        bufferSize /= 2;
                        if (spixbuf == null || spixbuf.length < bufferSize) {
                           spixbuf = new short[bufferSize];
                        }

                        this.iis.readFully((short[])spixbuf, 0, bufferSize);
                        break;
                     case 3:
                        bufferSize /= 4;
                        if (ipixbuf == null || ipixbuf.length < bufferSize) {
                           ipixbuf = new int[bufferSize];
                        }

                        this.iis.readFully((int[])ipixbuf, 0, bufferSize);
                        break;
                     case 4:
                        bufferSize /= 4;
                        if (fpixbuf == null || fpixbuf.length < bufferSize) {
                           fpixbuf = new float[bufferSize];
                        }

                        this.iis.readFully((float[])fpixbuf, 0, bufferSize);
                        break;
                     case 5:
                        bufferSize /= 8;
                        if (dpixbuf == null || dpixbuf.length < bufferSize) {
                           dpixbuf = new double[bufferSize];
                        }

                        this.iis.readFully((double[])dpixbuf, 0, bufferSize);
                  }

                  for(b = 0; b < numBands; ++b) {
                     int var10000 = destBandOffsets[this.destinationBands[b]];
                     destPos = ((ComponentSampleModel)raster.getSampleModel()).getOffset(x1 - raster.getSampleModelTranslateX(), y1 - raster.getSampleModelTranslateY(), this.destinationBands[b]);
                     innerStep = bankIndices[this.destinationBands[b]];
                     switch (dataType) {
                        case 0:
                           destPixbuf = ((DataBufferByte)raster.getDataBuffer()).getData(innerStep);
                           break;
                        case 1:
                           destSPixbuf = ((DataBufferUShort)raster.getDataBuffer()).getData(innerStep);
                           break;
                        case 2:
                           destSPixbuf = ((DataBufferShort)raster.getDataBuffer()).getData(innerStep);
                           break;
                        case 3:
                           destIPixbuf = ((DataBufferInt)raster.getDataBuffer()).getData(innerStep);
                           break;
                        case 4:
                           destFPixbuf = ((DataBufferFloat)raster.getDataBuffer()).getData(innerStep);
                           break;
                        case 5:
                           destDPixbuf = ((DataBufferDouble)raster.getDataBuffer()).getData(innerStep);
                     }

                     innerStep1 = ((ComponentSampleModel)this.originalSampleModel).getOffset(tx, ty, this.sourceBands[b]);
                     skipX = this.scaleX * pixelStride;
                     outerBound = 0;

                     for(innerStep = y1; outerBound < cTileHeight && !this.reader.getAbortRequest(); ++innerStep) {
                        label363:
                        switch (dataType) {
                           case 0:
                              innerStep1 = 0;
                              m1 = innerStep1;
                              m2 = destPos;

                              while(true) {
                                 if (innerStep1 >= lineLength) {
                                    break label363;
                                 }

                                 destPixbuf[m2] = pixbuf[m1];
                                 ++innerStep1;
                                 m1 += skipX;
                                 m2 += destPixelStride;
                              }
                           case 1:
                           case 2:
                              innerStep1 = 0;
                              m1 = innerStep1;
                              m2 = destPos;

                              while(true) {
                                 if (innerStep1 >= lineLength) {
                                    break label363;
                                 }

                                 destSPixbuf[m2] = spixbuf[m1];
                                 ++innerStep1;
                                 m1 += skipX;
                                 m2 += destPixelStride;
                              }
                           case 3:
                              innerStep1 = 0;
                              m1 = innerStep1;
                              m2 = destPos;

                              while(true) {
                                 if (innerStep1 >= lineLength) {
                                    break label363;
                                 }

                                 destIPixbuf[m2] = ipixbuf[m1];
                                 ++innerStep1;
                                 m1 += skipX;
                                 m2 += destPixelStride;
                              }
                           case 4:
                              innerStep1 = 0;
                              m1 = innerStep1;
                              m2 = destPos;

                              while(true) {
                                 if (innerStep1 >= lineLength) {
                                    break label363;
                                 }

                                 destFPixbuf[m2] = fpixbuf[m1];
                                 ++innerStep1;
                                 m1 += skipX;
                                 m2 += destPixelStride;
                              }
                           case 5:
                              innerStep1 = 0;
                              m1 = innerStep1;

                              for(m2 = destPos; innerStep1 < lineLength; m2 += destPixelStride) {
                                 destDPixbuf[m2] = dpixbuf[m1];
                                 ++innerStep1;
                                 m1 += skipX;
                              }
                        }

                        destPos += destScanlineStride;
                        innerStep1 += scanlineStride * this.scaleY;
                        if (this.destImage != null) {
                           int[] destBands = new int[]{this.destinationBands[b]};
                           this.reader.processImageUpdateWrapper(this.destImage, x1, innerStep, cTileHeight, 1, 1, 1, destBands);
                        }

                        this.reader.processImageProgressWrapper(percentage + ((float)outerBound + 1.0F) / (float)cTileHeight / (float)numBands / (float)totalTiles);
                        outerBound += this.scaleY;
                     }
                  }
               } else {
                  boolean isBanded = this.sampleModel instanceof BandedSampleModel;
                  b = (int)ImageUtil.getBandSize(this.originalSampleModel);

                  for(b = 0; b < numBands; ++b) {
                     this.iis.seek(tilePosition + (long)(b * this.sourceBands[b] * sampleSizeByte));
                     destPos = destBandOffsets[this.destinationBands[b]];
                     this.iis.skipBytes((ty * scanlineStride + tx * pixelStride) * sampleSizeByte);
                     byte outerFirst;
                     if (pixelStride < scanlineStride) {
                        outerFirst = 0;
                        innerStep1 = y1;
                        skipX = this.scaleY;
                        outerBound = cTileHeight;
                        innerStep = this.scaleX * pixelStride;
                        innerStep1 = destPixelStride;
                        m1 = destScanlineStride;
                     } else {
                        outerFirst = 0;
                        innerStep1 = x1;
                        skipX = this.scaleX;
                        outerBound = cTileWidth;
                        innerStep = this.scaleY * scanlineStride;
                        innerStep1 = destScanlineStride;
                        m1 = destPixelStride;
                     }

                     m2 = destSX + (y1 - raster.getSampleModelTranslateY()) * destScanlineStride + (x1 - raster.getSampleModelTranslateX()) * destPixelStride + destPos;
                     sourceBandOffset = bankIndices[this.destinationBands[b]];
                     switch (dataType) {
                        case 0:
                           destPixbuf = ((DataBufferByte)raster.getDataBuffer()).getData(sourceBandOffset);
                           break;
                        case 1:
                           destSPixbuf = ((DataBufferUShort)raster.getDataBuffer()).getData(sourceBandOffset);
                           break;
                        case 2:
                           destSPixbuf = ((DataBufferShort)raster.getDataBuffer()).getData(sourceBandOffset);
                           break;
                        case 3:
                           destIPixbuf = ((DataBufferInt)raster.getDataBuffer()).getData(sourceBandOffset);
                           break;
                        case 4:
                           destFPixbuf = ((DataBufferFloat)raster.getDataBuffer()).getData(sourceBandOffset);
                           break;
                        case 5:
                           destDPixbuf = ((DataBufferDouble)raster.getDataBuffer()).getData(sourceBandOffset);
                     }

                     l = outerFirst;

                     for(m = innerStep1; l < outerBound && !this.reader.getAbortRequest(); ++m) {
                        int m1;
                        int n;
                        label450:
                        switch (dataType) {
                           case 0:
                              if (innerStep == 1 && innerStep1 == 1) {
                                 this.iis.readFully(destPixbuf, m2, readLength);
                                 break;
                              } else {
                                 this.iis.readFully((byte[])pixbuf, 0, readLength);
                                 m1 = 0;
                                 n = m2;

                                 while(true) {
                                    if (m1 >= readLength) {
                                       break label450;
                                    }

                                    destPixbuf[n] = pixbuf[m1];
                                    m1 += innerStep;
                                    n += innerStep1;
                                 }
                              }
                           case 1:
                           case 2:
                              if (innerStep == 1 && innerStep1 == 1) {
                                 this.iis.readFully(destSPixbuf, m2, readLength);
                                 break;
                              } else {
                                 this.iis.readFully((short[])spixbuf, 0, readLength);
                                 m1 = 0;
                                 n = m2;

                                 while(true) {
                                    if (m1 >= readLength) {
                                       break label450;
                                    }

                                    destSPixbuf[n] = spixbuf[m1];
                                    m1 += innerStep;
                                    n += innerStep1;
                                 }
                              }
                           case 3:
                              if (innerStep == 1 && innerStep1 == 1) {
                                 this.iis.readFully(destIPixbuf, m2, readLength);
                                 break;
                              } else {
                                 this.iis.readFully((int[])ipixbuf, 0, readLength);
                                 m1 = 0;
                                 n = m2;

                                 while(true) {
                                    if (m1 >= readLength) {
                                       break label450;
                                    }

                                    destIPixbuf[n] = ipixbuf[m1];
                                    m1 += innerStep;
                                    n += innerStep1;
                                 }
                              }
                           case 4:
                              if (innerStep == 1 && innerStep1 == 1) {
                                 this.iis.readFully(destFPixbuf, m2, readLength);
                                 break;
                              } else {
                                 this.iis.readFully((float[])fpixbuf, 0, readLength);
                                 m1 = 0;
                                 n = m2;

                                 while(true) {
                                    if (m1 >= readLength) {
                                       break label450;
                                    }

                                    destFPixbuf[n] = fpixbuf[m1];
                                    m1 += innerStep;
                                    n += innerStep1;
                                 }
                              }
                           case 5:
                              if (innerStep == 1 && innerStep1 == 1) {
                                 this.iis.readFully(destDPixbuf, m2, readLength);
                              } else {
                                 this.iis.readFully((double[])dpixbuf, 0, readLength);
                                 m1 = 0;

                                 for(n = m2; m1 < readLength; n += innerStep1) {
                                    destDPixbuf[n] = dpixbuf[m1];
                                    m1 += innerStep;
                                 }
                              }
                        }

                        this.iis.skipBytes(skipLength);
                        m2 += m1;
                        if (this.destImage != null) {
                           int[] destBands = new int[]{this.destinationBands[b]};
                           if (pixelStride < scanlineStride) {
                              this.reader.processImageUpdateWrapper(this.destImage, x1, m, outerBound, 1, 1, 1, destBands);
                           } else {
                              this.reader.processImageUpdateWrapper(this.destImage, m, y1, 1, outerBound, 1, 1, destBands);
                           }
                        }

                        this.reader.processImageProgressWrapper((percentage + ((float)l + 1.0F) / (float)outerBound / (float)numBands / (float)totalTiles) * 100.0F);
                        l += skipX;
                     }
                  }
               }
            }
         }
      }

      return raster;
   }

   public void setDestImage(BufferedImage image) {
      this.destImage = image;
   }

   public void clearDestImage() {
      this.destImage = null;
   }

   private int getTileNum(int x, int y) {
      int num = (y - this.getMinTileY()) * this.getNumXTiles() + x - this.getMinTileX();
      if (num >= 0 && num < this.getNumXTiles() * this.getNumYTiles()) {
         return num;
      } else {
         throw new IllegalArgumentException(I18N.getString("RawRenderedImage0"));
      }
   }

   private int clip(int value, int min, int max) {
      if (value < min) {
         value = min;
      }

      if (value > max) {
         value = max;
      }

      return value;
   }
}
