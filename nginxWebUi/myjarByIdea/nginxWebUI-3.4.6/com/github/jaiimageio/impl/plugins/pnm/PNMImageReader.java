package com.github.jaiimageio.impl.plugins.pnm;

import com.github.jaiimageio.impl.common.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import sun.security.action.GetPropertyAction;

public class PNMImageReader extends ImageReader {
   private static final int PBM_ASCII = 49;
   private static final int PGM_ASCII = 50;
   private static final int PPM_ASCII = 51;
   private static final int PBM_RAW = 52;
   private static final int PGM_RAW = 53;
   private static final int PPM_RAW = 54;
   private static final int LINE_FEED = 10;
   private static byte[] lineSeparator;
   private int variant;
   private int maxValue;
   private ImageInputStream iis = null;
   private boolean gotHeader = false;
   private long imageDataOffset;
   private int width;
   private int height;
   private String aLine;
   private StringTokenizer token;
   private PNMMetadata metadata;

   public PNMImageReader(ImageReaderSpi originator) {
      super(originator);
   }

   public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
      super.setInput(input, seekForwardOnly, ignoreMetadata);
      this.iis = (ImageInputStream)input;
   }

   public int getNumImages(boolean allowSearch) throws IOException {
      return 1;
   }

   public int getWidth(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      this.readHeader();
      return this.width;
   }

   public int getHeight(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      this.readHeader();
      return this.height;
   }

   public int getVariant() {
      return this.variant;
   }

   public int getMaxValue() {
      return this.maxValue;
   }

   private void checkIndex(int imageIndex) {
      if (imageIndex != 0) {
         throw new IndexOutOfBoundsException(I18N.getString("PNMImageReader1"));
      }
   }

   public synchronized void readHeader() throws IOException {
      if (this.gotHeader) {
         this.iis.seek(this.imageDataOffset);
      } else {
         if (this.iis != null) {
            if (this.iis.readByte() != 80) {
               throw new RuntimeException(I18N.getString("PNMImageReader0"));
            }

            this.variant = this.iis.readByte();
            if (this.variant < 49 || this.variant > 54) {
               throw new RuntimeException(I18N.getString("PNMImageReader0"));
            }

            this.metadata = new PNMMetadata();
            this.metadata.setVariant(this.variant);
            this.iis.readLine();
            this.readComments(this.iis, this.metadata);
            this.width = this.readInteger(this.iis);
            this.height = this.readInteger(this.iis);
            if (this.variant != 49 && this.variant != 52) {
               this.maxValue = this.readInteger(this.iis);
            } else {
               this.maxValue = 1;
            }

            this.metadata.setWidth(this.width);
            this.metadata.setHeight(this.height);
            this.metadata.setMaxBitDepth(this.maxValue);
            this.gotHeader = true;
            this.imageDataOffset = this.iis.getStreamPosition();
         }

      }
   }

   public Iterator getImageTypes(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      this.readHeader();
      int tmp = (this.variant - 49) % 3;
      ArrayList list = new ArrayList(1);
      int dataType = 3;
      if (this.maxValue < 256) {
         dataType = 0;
      } else if (this.maxValue < 65536) {
         dataType = 1;
      }

      SampleModel sampleModel = null;
      ColorModel colorModel = null;
      if (this.variant != 49 && this.variant != 52) {
         sampleModel = new PixelInterleavedSampleModel(dataType, this.width, this.height, tmp == 1 ? 1 : 3, this.width * (tmp == 1 ? 1 : 3), tmp == 1 ? new int[]{0} : new int[]{0, 1, 2});
         colorModel = ImageUtil.createColorModel((ColorSpace)null, (SampleModel)sampleModel);
      } else {
         sampleModel = new MultiPixelPackedSampleModel(0, this.width, this.height, 1);
         byte[] color = new byte[]{-1, 0};
         colorModel = new IndexColorModel(1, 2, color, color, color);
      }

      list.add(new ImageTypeSpecifier((ColorModel)colorModel, (SampleModel)sampleModel));
      return list.iterator();
   }

   public ImageReadParam getDefaultReadParam() {
      return new ImageReadParam();
   }

   public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      this.readHeader();
      return this.metadata;
   }

   public IIOMetadata getStreamMetadata() throws IOException {
      return null;
   }

   public boolean isRandomAccessEasy(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      return true;
   }

   public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
      this.checkIndex(imageIndex);
      this.clearAbortRequest();
      this.processImageStarted(imageIndex);
      if (param == null) {
         param = this.getDefaultReadParam();
      }

      this.readHeader();
      Rectangle sourceRegion = new Rectangle(0, 0, 0, 0);
      Rectangle destinationRegion = new Rectangle(0, 0, 0, 0);
      computeRegions(param, this.width, this.height, param.getDestination(), sourceRegion, destinationRegion);
      int scaleX = param.getSourceXSubsampling();
      int scaleY = param.getSourceYSubsampling();
      int[] sourceBands = param.getSourceBands();
      int[] destBands = param.getDestinationBands();
      boolean seleBand = sourceBands != null && destBands != null;
      boolean noTransform = destinationRegion.equals(new Rectangle(0, 0, this.width, this.height)) || seleBand;
      if (this.isRaw(this.variant) && this.maxValue >= 256) {
         this.maxValue = 255;
      }

      int numBands = 1;
      if (this.variant == 51 || this.variant == 54) {
         numBands = 3;
      }

      if (!seleBand) {
         sourceBands = new int[numBands];
         destBands = new int[numBands];

         for(int i = 0; i < numBands; ++i) {
            destBands[i] = sourceBands[i] = i;
         }
      }

      int dataType = 3;
      if (this.maxValue < 256) {
         dataType = 0;
      } else if (this.maxValue < 65536) {
         dataType = 1;
      }

      SampleModel sampleModel = null;
      ColorModel colorModel = null;
      if (this.variant != 49 && this.variant != 52) {
         sampleModel = new PixelInterleavedSampleModel(dataType, destinationRegion.width, destinationRegion.height, sourceBands.length, destinationRegion.width * sourceBands.length, destBands);
         colorModel = ImageUtil.createColorModel((ColorSpace)null, (SampleModel)sampleModel);
      } else {
         sampleModel = new MultiPixelPackedSampleModel(0, destinationRegion.width, destinationRegion.height, 1);
         byte[] color = new byte[]{-1, 0};
         colorModel = new IndexColorModel(1, 2, color, color, color);
      }

      BufferedImage bi = param.getDestination();
      WritableRaster raster = null;
      SampleModel sampleModel;
      if (bi == null) {
         sampleModel = ((SampleModel)sampleModel).createCompatibleSampleModel(destinationRegion.x + destinationRegion.width, destinationRegion.y + destinationRegion.height);
         if (seleBand) {
            sampleModel = sampleModel.createSubsetSampleModel(sourceBands);
         }

         raster = Raster.createWritableRaster(sampleModel, new Point());
         bi = new BufferedImage((ColorModel)colorModel, raster, false, (Hashtable)null);
      } else {
         raster = bi.getWritableTile(0, 0);
         sampleModel = bi.getSampleModel();
         ColorModel colorModel = bi.getColorModel();
         noTransform &= destinationRegion.equals(raster.getBounds());
      }

      DataBuffer dataBuffer;
      byte[] buf;
      int i;
      int destLS;
      int skip;
      int i;
      int pixelStride;
      int j;
      int k;
      int b;
      int n;
      int j;
      int k;
      int m;
      int skipY;
      label401:
      switch (this.variant) {
         case 49:
            dataBuffer = raster.getDataBuffer();
            buf = ((DataBufferByte)dataBuffer).getData();
            if (noTransform) {
               i = 0;
               skipY = 0;

               while(true) {
                  if (i >= this.height) {
                     break label401;
                  }

                  destLS = 0;
                  skip = 7;

                  for(i = 0; i < this.width; ++i) {
                     destLS |= (this.readInteger(this.iis) & 1) << skip;
                     --skip;
                     if (skip == -1) {
                        buf[skipY++] = (byte)destLS;
                        destLS = 0;
                        skip = 7;
                     }
                  }

                  if (skip != 7) {
                     buf[skipY++] = (byte)destLS;
                  }

                  this.processImageUpdate(bi, 0, i, this.width, 1, 1, 1, destBands);
                  this.processImageProgress(100.0F * (float)i / (float)this.height);
                  ++i;
               }
            } else {
               this.skipInteger(this.iis, sourceRegion.y * this.width + sourceRegion.x);
               i = scaleX - 1;
               skipY = (scaleY - 1) * this.width + this.width - destinationRegion.width * scaleX;
               destLS = (bi.getWidth() + 7 >> 3) * destinationRegion.y + (destinationRegion.x >> 3);
               skip = 0;
               i = destLS;

               while(true) {
                  if (skip >= destinationRegion.height) {
                     break label401;
                  }

                  pixelStride = 0;
                  j = 7 - (destinationRegion.x & 7);

                  for(k = 0; k < destinationRegion.width; ++k) {
                     pixelStride |= (this.readInteger(this.iis) & 1) << j;
                     --j;
                     if (j == -1) {
                        buf[i++] = (byte)pixelStride;
                        pixelStride = 0;
                        j = 7;
                     }

                     this.skipInteger(this.iis, i);
                  }

                  if (j != 7) {
                     buf[i++] = (byte)pixelStride;
                  }

                  i += destinationRegion.x >> 3;
                  this.skipInteger(this.iis, skipY);
                  this.processImageUpdate(bi, 0, skip, destinationRegion.width, 1, 1, 1, destBands);
                  this.processImageProgress(100.0F * (float)skip / (float)destinationRegion.height);
                  ++skip;
               }
            }
         case 50:
         case 51:
         case 53:
         case 54:
            int skipX = (scaleX - 1) * numBands;
            int skipY = (scaleY * this.width - destinationRegion.width * scaleX) * numBands;
            i = (bi.getWidth() * destinationRegion.y + destinationRegion.x) * numBands;
            switch (dataType) {
               case 0:
                  DataBufferByte bbuf = (DataBufferByte)raster.getDataBuffer();
                  byte[] byteArray = bbuf.getData();
                  if (this.isRaw(this.variant)) {
                     if (noTransform) {
                        this.iis.readFully(byteArray);
                        this.processImageUpdate(bi, 0, 0, this.width, this.height, 1, 1, destBands);
                        this.processImageProgress(100.0F);
                        break label401;
                     } else {
                        this.iis.skipBytes(sourceRegion.y * this.width * numBands);
                        skip = (scaleY - 1) * this.width * numBands;
                        byte[] data = new byte[this.width * numBands];
                        pixelStride = scaleX * numBands;
                        j = sourceRegion.x * numBands;
                        k = this.width;
                        b = 0;
                        n = i;

                        while(true) {
                           if (b >= destinationRegion.height) {
                              break label401;
                           }

                           this.iis.read(data);
                           j = sourceRegion.x;

                           for(k = j; j < sourceRegion.x + sourceRegion.width; k += pixelStride) {
                              for(m = 0; m < sourceBands.length; ++m) {
                                 byteArray[n + destBands[m]] = data[k + sourceBands[m]];
                              }

                              n += sourceBands.length;
                              j += scaleX;
                           }

                           n += destinationRegion.x * numBands;
                           this.iis.skipBytes(skip);
                           this.processImageUpdate(bi, 0, b, destinationRegion.width, 1, 1, 1, destBands);
                           this.processImageProgress(100.0F * (float)b / (float)destinationRegion.height);
                           ++b;
                        }
                     }
                  } else {
                     this.skipInteger(this.iis, (sourceRegion.y * this.width + sourceRegion.x) * numBands);
                     if (seleBand) {
                        byte[] data = new byte[numBands];
                        i = 0;
                        pixelStride = i;

                        while(true) {
                           if (i >= destinationRegion.height) {
                              break label401;
                           }

                           for(j = 0; j < destinationRegion.width; ++j) {
                              for(k = 0; k < numBands; ++k) {
                                 data[k] = (byte)this.readInteger(this.iis);
                              }

                              for(k = 0; k < sourceBands.length; ++k) {
                                 byteArray[pixelStride + destBands[k]] = data[sourceBands[k]];
                              }

                              pixelStride += sourceBands.length;
                              this.skipInteger(this.iis, skipX);
                           }

                           pixelStride += destinationRegion.x * sourceBands.length;
                           this.skipInteger(this.iis, skipY);
                           this.processImageUpdate(bi, 0, i, destinationRegion.width, 1, 1, 1, destBands);
                           this.processImageProgress(100.0F * (float)i / (float)destinationRegion.height);
                           ++i;
                        }
                     } else {
                        skip = 0;
                        i = i;

                        while(true) {
                           if (skip >= destinationRegion.height) {
                              break label401;
                           }

                           for(pixelStride = 0; pixelStride < destinationRegion.width; ++pixelStride) {
                              for(j = 0; j < numBands; ++j) {
                                 byteArray[i++] = (byte)this.readInteger(this.iis);
                              }

                              this.skipInteger(this.iis, skipX);
                           }

                           i += destinationRegion.x * sourceBands.length;
                           this.skipInteger(this.iis, skipY);
                           this.processImageUpdate(bi, 0, skip, destinationRegion.width, 1, 1, 1, destBands);
                           this.processImageProgress(100.0F * (float)skip / (float)destinationRegion.height);
                           ++skip;
                        }
                     }
                  }
               case 1:
                  DataBufferUShort sbuf = (DataBufferUShort)raster.getDataBuffer();
                  short[] shortArray = sbuf.getData();
                  this.skipInteger(this.iis, sourceRegion.y * this.width * numBands + sourceRegion.x);
                  if (seleBand) {
                     short[] data = new short[numBands];
                     j = 0;

                     for(k = i; j < destinationRegion.height; ++j) {
                        for(b = 0; b < destinationRegion.width; ++b) {
                           for(n = 0; n < numBands; ++n) {
                              data[n] = (short)this.readInteger(this.iis);
                           }

                           for(n = 0; n < sourceBands.length; ++n) {
                              shortArray[k + destBands[n]] = data[sourceBands[n]];
                           }

                           k += sourceBands.length;
                           this.skipInteger(this.iis, skipX);
                        }

                        k += destinationRegion.x * sourceBands.length;
                        this.skipInteger(this.iis, skipY);
                        this.processImageUpdate(bi, 0, j, destinationRegion.width, 1, 1, 1, destBands);
                        this.processImageProgress(100.0F * (float)j / (float)destinationRegion.height);
                     }
                  } else {
                     pixelStride = 0;

                     for(j = i; pixelStride < destinationRegion.height; ++pixelStride) {
                        for(k = 0; k < destinationRegion.width; ++k) {
                           for(b = 0; b < numBands; ++b) {
                              shortArray[j++] = (short)this.readInteger(this.iis);
                           }

                           this.skipInteger(this.iis, skipX);
                        }

                        j += destinationRegion.x * sourceBands.length;
                        this.skipInteger(this.iis, skipY);
                        this.processImageUpdate(bi, 0, pixelStride, destinationRegion.width, 1, 1, 1, destBands);
                        this.processImageProgress(100.0F * (float)pixelStride / (float)destinationRegion.height);
                     }
                  }
               case 2:
               default:
                  break label401;
               case 3:
                  DataBufferInt ibuf = (DataBufferInt)raster.getDataBuffer();
                  int[] intArray = ibuf.getData();
                  this.skipInteger(this.iis, sourceRegion.y * this.width * numBands + sourceRegion.x);
                  if (seleBand) {
                     int[] data = new int[numBands];
                     b = 0;
                     n = i;

                     while(true) {
                        if (b >= destinationRegion.height) {
                           break label401;
                        }

                        for(j = 0; j < destinationRegion.width; ++j) {
                           for(k = 0; k < numBands; ++k) {
                              data[k] = this.readInteger(this.iis);
                           }

                           for(k = 0; k < sourceBands.length; ++k) {
                              intArray[n + destBands[k]] = data[sourceBands[k]];
                           }

                           n += sourceBands.length;
                           this.skipInteger(this.iis, skipX);
                        }

                        n += destinationRegion.x * sourceBands.length;
                        this.skipInteger(this.iis, skipY);
                        this.processImageUpdate(bi, 0, b, destinationRegion.width, 1, 1, 1, destBands);
                        this.processImageProgress(100.0F * (float)b / (float)destinationRegion.height);
                        ++b;
                     }
                  } else {
                     k = 0;
                     b = i;

                     while(true) {
                        if (k >= destinationRegion.height) {
                           break label401;
                        }

                        for(n = 0; n < destinationRegion.width; ++n) {
                           for(j = 0; j < numBands; ++j) {
                              intArray[b++] = this.readInteger(this.iis);
                           }

                           this.skipInteger(this.iis, skipX);
                        }

                        b += destinationRegion.x * sourceBands.length;
                        this.skipInteger(this.iis, skipY);
                        this.processImageUpdate(bi, 0, k, destinationRegion.width, 1, 1, 1, destBands);
                        this.processImageProgress(100.0F * (float)k / (float)destinationRegion.height);
                        ++k;
                     }
                  }
            }
         case 52:
            dataBuffer = raster.getDataBuffer();
            buf = ((DataBufferByte)dataBuffer).getData();
            if (noTransform) {
               this.iis.readFully(buf, 0, buf.length);
               this.processImageUpdate(bi, 0, 0, this.width, this.height, 1, 1, destBands);
               this.processImageProgress(100.0F);
            } else if (scaleX == 1 && sourceRegion.x % 8 == 0) {
               i = sourceRegion.x >> 3;
               skipY = this.width + 7 >> 3;
               destLS = raster.getWidth() + 7 >> 3;
               skip = sourceRegion.width + 7 >> 3;
               i = sourceRegion.y * skipY;
               this.iis.skipBytes(i + i);
               i = skipY * (scaleY - 1) + skipY - skip;
               byte[] lineData = new byte[skip];
               j = destinationRegion.x & 7;
               boolean reformat = j != 0;
               b = 0;
               n = 0;

               for(j = destinationRegion.y * destLS + (destinationRegion.x >> 3); b < destinationRegion.height; n += scaleY) {
                  if (!reformat) {
                     this.iis.read(buf, j, skip);
                  } else {
                     this.iis.read(lineData, 0, skip);
                     k = 255 << j & 255;
                     m = ~k & 255;
                     int shift = 8 - j;
                     int n = 0;

                     int m;
                     for(m = j; n < skip - 1; ++m) {
                        buf[m] = (byte)((lineData[n] & m) << shift | (lineData[n + 1] & k) >> j);
                        ++n;
                     }

                     buf[m] = (byte)((lineData[n] & m) << shift);
                  }

                  this.iis.skipBytes(i);
                  j += destLS;
                  this.processImageUpdate(bi, 0, b, destinationRegion.width, 1, 1, 1, destBands);
                  this.processImageProgress(100.0F * (float)b / (float)destinationRegion.height);
                  ++b;
               }
            } else {
               i = this.width + 7 >> 3;
               byte[] data = new byte[i];
               this.iis.skipBytes(sourceRegion.y * i);
               destLS = bi.getWidth() + 7 >> 3;
               skip = i * (scaleY - 1);
               i = destLS * destinationRegion.y + (destinationRegion.x >> 3);
               pixelStride = 0;
               j = 0;

               for(k = i; pixelStride < destinationRegion.height; j += scaleY) {
                  this.iis.read(data, 0, i);
                  this.iis.skipBytes(skip);
                  b = 0;
                  n = 7 - (destinationRegion.x & 7);

                  for(j = sourceRegion.x; j < sourceRegion.x + sourceRegion.width; j += scaleX) {
                     b |= (data[j >> 3] >> 7 - (j & 7) & 1) << n;
                     --n;
                     if (n == -1) {
                        buf[k++] = (byte)b;
                        b = 0;
                        n = 7;
                     }
                  }

                  if (n != 7) {
                     buf[k++] = (byte)b;
                  }

                  k += destinationRegion.x >> 3;
                  this.processImageUpdate(bi, 0, pixelStride, destinationRegion.width, 1, 1, 1, destBands);
                  this.processImageProgress(100.0F * (float)pixelStride / (float)destinationRegion.height);
                  ++pixelStride;
               }
            }
      }

      if (this.abortRequested()) {
         this.processReadAborted();
      } else {
         this.processImageComplete();
      }

      return bi;
   }

   public boolean canReadRaster() {
      return true;
   }

   public Raster readRaster(int imageIndex, ImageReadParam param) throws IOException {
      BufferedImage bi = this.read(imageIndex, param);
      return bi.getData();
   }

   public void reset() {
      super.reset();
      this.iis = null;
      this.gotHeader = false;
      System.gc();
   }

   private boolean isRaw(int v) {
      return v >= 52;
   }

   private void readComments(ImageInputStream stream, PNMMetadata metadata) throws IOException {
      String line = null;
      int pos = true;
      stream.mark();

      int pos;
      while((line = stream.readLine()) != null && (pos = line.indexOf("#")) >= 0) {
         metadata.addComment(line.substring(pos + 1).trim());
      }

      stream.reset();
   }

   private int readInteger(ImageInputStream stream) throws IOException {
      boolean foundDigit = false;

      while(this.aLine == null) {
         this.aLine = stream.readLine();
         if (this.aLine == null) {
            return 0;
         }

         int pos = this.aLine.indexOf("#");
         if (pos == 0) {
            this.aLine = null;
         } else if (pos > 0) {
            this.aLine = this.aLine.substring(0, pos - 1);
         }

         if (this.aLine != null) {
            this.token = new StringTokenizer(this.aLine);
         }
      }

      while(this.token.hasMoreTokens()) {
         String s = this.token.nextToken();

         try {
            return new Integer(s);
         } catch (NumberFormatException var5) {
         }
      }

      if (!foundDigit) {
         this.aLine = null;
         return this.readInteger(stream);
      } else {
         return 0;
      }
   }

   private void skipInteger(ImageInputStream stream, int num) throws IOException {
      for(int i = 0; i < num; ++i) {
         this.readInteger(stream);
      }

   }

   static {
      if (lineSeparator == null) {
         String ls = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
         lineSeparator = ls.getBytes();
      }

   }
}
