package com.github.jaiimageio.impl.plugins.pnm;

import com.github.jaiimageio.impl.common.ImageUtil;
import com.github.jaiimageio.plugins.pnm.PNMImageWriteParam;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.security.AccessController;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import sun.security.action.GetPropertyAction;

public class PNMImageWriter extends ImageWriter {
   private static final int PBM_ASCII = 49;
   private static final int PGM_ASCII = 50;
   private static final int PPM_ASCII = 51;
   private static final int PBM_RAW = 52;
   private static final int PGM_RAW = 53;
   private static final int PPM_RAW = 54;
   private static final int SPACE = 32;
   private static final String COMMENT = "# written by com.github.jaiimageio.impl.PNMImageWriter";
   private static byte[] lineSeparator;
   private int variant;
   private int maxValue;
   private ImageOutputStream stream = null;

   public PNMImageWriter(ImageWriterSpi originator) {
      super(originator);
   }

   public void setOutput(Object output) {
      super.setOutput(output);
      if (output != null) {
         if (!(output instanceof ImageOutputStream)) {
            throw new IllegalArgumentException(I18N.getString("PNMImageWriter0"));
         }

         this.stream = (ImageOutputStream)output;
      } else {
         this.stream = null;
      }

   }

   public ImageWriteParam getDefaultWriteParam() {
      return new PNMImageWriteParam();
   }

   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
      return null;
   }

   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
      return new PNMMetadata(imageType, param);
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
         PNMMetadata outData = null;
         if (inData instanceof PNMMetadata) {
            outData = (PNMMetadata)((PNMMetadata)inData).clone();
         } else {
            try {
               outData = new PNMMetadata(inData);
            } catch (IIOInvalidTreeException var6) {
               outData = new PNMMetadata();
            }
         }

         outData.initialize(imageType, param);
         return outData;
      }
   }

   public boolean canWriteRasters() {
      return true;
   }

   public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
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
      ColorModel colorModel = null;
      if (writeRaster) {
         inputRaster = image.getRaster();
         sampleModel = inputRaster.getSampleModel();
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

      if (sourceRegion.isEmpty()) {
         throw new RuntimeException(I18N.getString("PNMImageWrite1"));
      } else {
         ImageUtil.canEncodeImage(this, colorModel, sampleModel);
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
         new Rectangle(minX, minY, w, h);
         int tileHeight = sampleModel.getHeight();
         int tileWidth = sampleModel.getWidth();
         int[] sampleSize = sampleModel.getSampleSize();
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

         byte[] reds = null;
         byte[] greens = null;
         byte[] blues = null;
         boolean isPBMInverted = false;
         if (numBands == 1) {
            if (colorModel instanceof IndexColorModel) {
               IndexColorModel icm = (IndexColorModel)colorModel;
               int mapSize = icm.getMapSize();
               if (mapSize < 1 << sampleSize[0]) {
                  throw new RuntimeException(I18N.getString("PNMImageWrite2"));
               }

               if (sampleSize[0] == 1) {
                  this.variant = 52;
                  isPBMInverted = icm.getRed(1) > icm.getRed(0);
               } else {
                  this.variant = 54;
                  reds = new byte[mapSize];
                  greens = new byte[mapSize];
                  blues = new byte[mapSize];
                  icm.getReds(reds);
                  icm.getGreens(greens);
                  icm.getBlues(blues);
               }
            } else if (sampleSize[0] == 1) {
               this.variant = 52;
            } else if (sampleSize[0] <= 8) {
               this.variant = 53;
            } else {
               this.variant = 50;
            }
         } else {
            if (numBands != 3) {
               throw new RuntimeException(I18N.getString("PNMImageWrite3"));
            }

            if (sampleSize[0] <= 8 && sampleSize[1] <= 8 && sampleSize[2] <= 8) {
               this.variant = 54;
            } else {
               this.variant = 51;
            }
         }

         IIOMetadata inputMetadata = image.getMetadata();
         ImageTypeSpecifier imageType;
         if (colorModel != null) {
            imageType = new ImageTypeSpecifier(colorModel, sampleModel);
         } else {
            int dataType = sampleModel.getDataType();
            switch (numBands) {
               case 1:
                  imageType = ImageTypeSpecifier.createGrayscale(sampleSize[0], dataType, false);
                  break;
               case 3:
                  ColorSpace cs = ColorSpace.getInstance(1000);
                  imageType = ImageTypeSpecifier.createInterleaved(cs, new int[]{0, 1, 2}, dataType, false, false);
                  break;
               default:
                  throw new IIOException("Cannot encode image with " + numBands + " bands!");
            }
         }

         PNMMetadata metadata;
         if (inputMetadata != null) {
            metadata = (PNMMetadata)this.convertImageMetadata(inputMetadata, imageType, param);
         } else {
            metadata = (PNMMetadata)this.getDefaultImageMetadata(imageType, param);
         }

         boolean isRawPNM;
         if (param instanceof PNMImageWriteParam) {
            isRawPNM = ((PNMImageWriteParam)param).getRaw();
         } else {
            isRawPNM = metadata.isRaw();
         }

         this.maxValue = metadata.getMaxValue();

         int maxBitDepth;
         for(maxBitDepth = 0; maxBitDepth < sampleSize.length; ++maxBitDepth) {
            int v = (1 << sampleSize[maxBitDepth]) - 1;
            if (v > this.maxValue) {
               this.maxValue = v;
            }
         }

         if (isRawPNM) {
            maxBitDepth = metadata.getMaxBitDepth();
            if (!this.isRaw(this.variant) && maxBitDepth <= 8) {
               this.variant += 3;
            } else if (this.isRaw(this.variant) && maxBitDepth > 8) {
               this.variant -= 3;
            }
         } else if (this.isRaw(this.variant)) {
            this.variant -= 3;
         }

         this.stream.writeByte(80);
         this.stream.writeByte(this.variant);
         this.stream.write(lineSeparator);
         this.stream.write("# written by com.github.jaiimageio.impl.PNMImageWriter".getBytes());
         Iterator comments = metadata.getComments();
         if (comments != null) {
            while(comments.hasNext()) {
               this.stream.write(lineSeparator);
               String comment = "# " + (String)comments.next();
               this.stream.write(comment.getBytes());
            }
         }

         this.stream.write(lineSeparator);
         this.writeInteger(this.stream, w);
         this.stream.write(32);
         this.writeInteger(this.stream, h);
         if (this.variant != 52 && this.variant != 49) {
            this.stream.write(lineSeparator);
            this.writeInteger(this.stream, this.maxValue);
         }

         if (this.variant == 52 || this.variant == 53 || this.variant == 54) {
            this.stream.write(10);
         }

         boolean writeOptimal = false;
         int[] pixels;
         if (this.variant == 52 && sampleModel.getTransferType() == 0 && sampleModel instanceof MultiPixelPackedSampleModel) {
            MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sampleModel;
            int originX = false;
            int originX;
            if (writeRaster) {
               originX = inputRaster.getMinX();
            } else {
               originX = input.getMinX();
            }

            if (mppsm.getBitOffset((sourceRegion.x - originX) % tileWidth) == 0 && mppsm.getPixelBitStride() == 1 && scaleX == 1) {
               writeOptimal = true;
            }
         } else if ((this.variant == 53 || this.variant == 54) && sampleModel instanceof ComponentSampleModel && !(colorModel instanceof IndexColorModel)) {
            ComponentSampleModel csm = (ComponentSampleModel)sampleModel;
            if (csm.getPixelStride() == numBands && scaleX == 1) {
               writeOptimal = true;
               if (this.variant == 54) {
                  pixels = csm.getBandOffsets();

                  for(int b = 0; b < numBands; ++b) {
                     if (pixels[b] != b) {
                        writeOptimal = false;
                        break;
                     }
                  }
               }
            }
         }

         int j;
         int offset;
         int i;
         int size;
         byte[] bpixels;
         if (writeOptimal) {
            size = this.variant == 52 ? (w + 7) / 8 : w * sampleModel.getNumBands();
            byte[] bdata = null;
            bpixels = new byte[size];

            for(j = 0; j < sourceRegion.height && !this.abortRequested(); ++j) {
               Raster lineRaster = null;
               if (writeRaster) {
                  lineRaster = inputRaster.createChild(sourceRegion.x, j, sourceRegion.width, 1, 0, 0, (int[])null);
               } else {
                  lineRaster = input.getData(new Rectangle(sourceRegion.x, sourceRegion.y + j, w, 1));
                  lineRaster = lineRaster.createTranslatedChild(0, 0);
               }

               byte[] bdata = ((DataBufferByte)lineRaster.getDataBuffer()).getData();
               sampleModel = lineRaster.getSampleModel();
               offset = 0;
               if (sampleModel instanceof ComponentSampleModel) {
                  offset = ((ComponentSampleModel)sampleModel).getOffset(lineRaster.getMinX() - lineRaster.getSampleModelTranslateX(), lineRaster.getMinY() - lineRaster.getSampleModelTranslateY());
               } else if (sampleModel instanceof MultiPixelPackedSampleModel) {
                  offset = ((MultiPixelPackedSampleModel)sampleModel).getOffset(lineRaster.getMinX() - lineRaster.getSampleModelTranslateX(), lineRaster.getMinX() - lineRaster.getSampleModelTranslateY());
               }

               if (isPBMInverted) {
                  int k = offset;

                  for(i = 0; i < size; ++i) {
                     bpixels[i] = (byte)(~bdata[k]);
                     ++k;
                  }

                  bdata = bpixels;
                  offset = 0;
               }

               this.stream.write(bdata, offset, size);
               this.processImageProgress(100.0F * (float)j / (float)sourceRegion.height);
            }

            this.stream.flush();
            if (this.abortRequested()) {
               this.processWriteAborted();
            } else {
               this.processImageComplete();
            }

         } else {
            size = sourceRegion.width * numBands;
            pixels = new int[size];
            bpixels = reds == null ? new byte[w * numBands] : new byte[w * 3];
            j = 0;
            int lastRow = sourceRegion.y + sourceRegion.height;

            for(offset = sourceRegion.y; offset < lastRow && !this.abortRequested(); offset += scaleY) {
               Raster src = null;
               if (writeRaster) {
                  src = inputRaster.createChild(sourceRegion.x, offset, sourceRegion.width, 1, sourceRegion.x, offset, sourceBands);
               } else {
                  src = input.getData(new Rectangle(sourceRegion.x, offset, sourceRegion.width, 1));
               }

               src.getPixels(sourceRegion.x, offset, sourceRegion.width, 1, pixels);
               if (isPBMInverted) {
                  for(i = 0; i < size; i += scaleX) {
                     bpixels[i] = (byte)(bpixels[i] ^ 1);
                  }
               }

               int i;
               int k;
               switch (this.variant) {
                  case 49:
                  case 50:
                     for(i = 0; i < size; i += scaleX) {
                        if (j++ % 16 == 0) {
                           this.stream.write(lineSeparator);
                        } else {
                           this.stream.write(32);
                        }

                        this.writeInteger(this.stream, pixels[i]);
                     }

                     this.stream.write(lineSeparator);
                     break;
                  case 51:
                     if (reds != null) {
                        for(i = 0; i < size; i += scaleX) {
                           if (j++ % 5 == 0) {
                              this.stream.write(lineSeparator);
                           } else {
                              this.stream.write(32);
                           }

                           this.writeInteger(this.stream, reds[pixels[i]] & 255);
                           this.stream.write(32);
                           this.writeInteger(this.stream, greens[pixels[i]] & 255);
                           this.stream.write(32);
                           this.writeInteger(this.stream, blues[pixels[i]] & 255);
                        }
                     } else {
                        for(i = 0; i < size; i += scaleX * numBands) {
                           for(int j = 0; j < numBands; ++j) {
                              if (j++ % 16 == 0) {
                                 this.stream.write(lineSeparator);
                              } else {
                                 this.stream.write(32);
                              }

                              this.writeInteger(this.stream, pixels[i + j]);
                           }
                        }
                     }

                     this.stream.write(lineSeparator);
                     break;
                  case 52:
                     i = 0;
                     int ksrc = false;
                     int b = 0;
                     int pos = 7;

                     for(i = 0; i < size; i += scaleX) {
                        b |= pixels[i] << pos;
                        --pos;
                        if (pos == -1) {
                           bpixels[i++] = (byte)b;
                           b = 0;
                           pos = 7;
                        }
                     }

                     if (pos != 7) {
                        bpixels[i++] = (byte)b;
                     }

                     this.stream.write(bpixels, 0, i);
                     break;
                  case 53:
                     i = 0;

                     for(k = 0; i < size; i += scaleX) {
                        bpixels[k++] = (byte)pixels[i];
                     }

                     this.stream.write(bpixels, 0, w);
                     break;
                  case 54:
                     if (reds != null) {
                        i = 0;

                        for(k = 0; i < size; i += scaleX) {
                           bpixels[k++] = reds[pixels[i]];
                           bpixels[k++] = greens[pixels[i]];
                           bpixels[k++] = blues[pixels[i]];
                        }
                     } else {
                        i = 0;

                        for(k = 0; i < size; i += scaleX * numBands) {
                           for(int j = 0; j < numBands; ++j) {
                              bpixels[k++] = (byte)(pixels[i + j] & 255);
                           }
                        }
                     }

                     this.stream.write(bpixels, 0, bpixels.length);
               }

               this.processImageProgress(100.0F * (float)(offset - sourceRegion.y) / (float)sourceRegion.height);
            }

            this.stream.flush();
            if (this.abortRequested()) {
               this.processWriteAborted();
            } else {
               this.processImageComplete();
            }

         }
      }
   }

   public void reset() {
      super.reset();
      this.stream = null;
   }

   private void writeInteger(ImageOutputStream output, int i) throws IOException {
      output.write(Integer.toString(i).getBytes());
   }

   private void writeByte(ImageOutputStream output, byte b) throws IOException {
      output.write(Byte.toString(b).getBytes());
   }

   private boolean isRaw(int v) {
      return v >= 52;
   }

   static {
      if (lineSeparator == null) {
         String ls = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
         lineSeparator = ls.getBytes();
      }

   }
}
