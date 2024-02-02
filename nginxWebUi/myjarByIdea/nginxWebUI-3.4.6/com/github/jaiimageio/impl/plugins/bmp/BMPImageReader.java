package com.github.jaiimageio.impl.plugins.bmp;

import com.github.jaiimageio.impl.common.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class BMPImageReader extends ImageReader implements BMPConstants {
   private static final int VERSION_2_1_BIT = 0;
   private static final int VERSION_2_4_BIT = 1;
   private static final int VERSION_2_8_BIT = 2;
   private static final int VERSION_2_24_BIT = 3;
   private static final int VERSION_3_1_BIT = 4;
   private static final int VERSION_3_4_BIT = 5;
   private static final int VERSION_3_8_BIT = 6;
   private static final int VERSION_3_24_BIT = 7;
   private static final int VERSION_3_NT_16_BIT = 8;
   private static final int VERSION_3_NT_32_BIT = 9;
   private static final int VERSION_4_1_BIT = 10;
   private static final int VERSION_4_4_BIT = 11;
   private static final int VERSION_4_8_BIT = 12;
   private static final int VERSION_4_16_BIT = 13;
   private static final int VERSION_4_24_BIT = 14;
   private static final int VERSION_4_32_BIT = 15;
   private static final int VERSION_3_XP_EMBEDDED = 16;
   private static final int VERSION_4_XP_EMBEDDED = 17;
   private static final int VERSION_5_XP_EMBEDDED = 18;
   private long bitmapFileSize;
   private long bitmapOffset;
   private long compression;
   private long imageSize;
   private byte[] palette;
   private int imageType;
   private int numBands;
   private boolean isBottomUp;
   private int bitsPerPixel;
   private int redMask;
   private int greenMask;
   private int blueMask;
   private int alphaMask;
   private SampleModel sampleModel;
   private SampleModel originalSampleModel;
   private ColorModel colorModel;
   private ColorModel originalColorModel;
   private ImageInputStream iis = null;
   private boolean gotHeader = false;
   private long imageDataOffset;
   private int width;
   private int height;
   private Rectangle destinationRegion;
   private Rectangle sourceRegion;
   private BMPMetadata metadata;
   private BufferedImage bi;
   private boolean noTransform = true;
   private boolean seleBand = false;
   private int scaleX;
   private int scaleY;
   private int[] sourceBands;
   private int[] destBands;

   public BMPImageReader(ImageReaderSpi originator) {
      super(originator);
   }

   public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
      super.setInput(input, seekForwardOnly, ignoreMetadata);
      this.iis = (ImageInputStream)input;
      if (this.iis != null) {
         this.iis.setByteOrder(ByteOrder.LITTLE_ENDIAN);
      }

      this.resetHeaderInfo();
   }

   public int getNumImages(boolean allowSearch) throws IOException {
      if (this.iis == null) {
         throw new IllegalStateException(I18N.getString("GetNumImages0"));
      } else if (this.seekForwardOnly && allowSearch) {
         throw new IllegalStateException(I18N.getString("GetNumImages1"));
      } else {
         return 1;
      }
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

   private void checkIndex(int imageIndex) {
      if (imageIndex != 0) {
         throw new IndexOutOfBoundsException(I18N.getString("BMPImageReader0"));
      }
   }

   public void readHeader() throws IOException {
      if (this.gotHeader) {
         this.iis.seek(this.imageDataOffset);
      } else if (this.iis == null) {
         throw new IllegalStateException(I18N.getString("BMPImageReader5"));
      } else {
         int profileData = 0;
         int profileSize = 0;
         this.metadata = new BMPMetadata();
         this.iis.mark();
         byte[] marker = new byte[2];
         this.iis.read(marker);
         if (marker[0] == 66 && marker[1] == 77) {
            this.bitmapFileSize = this.iis.readUnsignedInt();
            this.iis.skipBytes(4);
            this.bitmapOffset = this.iis.readUnsignedInt();
            long size = this.iis.readUnsignedInt();
            if (size == 12L) {
               this.width = this.iis.readShort();
               this.height = this.iis.readShort();
            } else {
               this.width = this.iis.readInt();
               this.height = this.iis.readInt();
            }

            this.metadata.width = this.width;
            this.metadata.height = this.height;
            int planes = this.iis.readUnsignedShort();
            this.bitsPerPixel = this.iis.readUnsignedShort();
            this.metadata.bitsPerPixel = (short)this.bitsPerPixel;
            this.numBands = 3;
            if (size == 12L) {
               this.metadata.bmpVersion = "BMP v. 2.x";
               if (this.bitsPerPixel == 1) {
                  this.imageType = 0;
               } else if (this.bitsPerPixel == 4) {
                  this.imageType = 1;
               } else if (this.bitsPerPixel == 8) {
                  this.imageType = 2;
               } else if (this.bitsPerPixel == 24) {
                  this.imageType = 3;
               }

               int numberOfEntries = (int)((this.bitmapOffset - 14L - size) / 3L);
               int sizeOfPalette = numberOfEntries * 3;
               this.palette = new byte[sizeOfPalette];
               this.iis.readFully(this.palette, 0, sizeOfPalette);
               this.metadata.palette = this.palette;
               this.metadata.paletteSize = numberOfEntries;
            } else {
               this.compression = this.iis.readUnsignedInt();
               this.imageSize = this.iis.readUnsignedInt();
               long xPelsPerMeter = (long)this.iis.readInt();
               long yPelsPerMeter = (long)this.iis.readInt();
               long colorsUsed = this.iis.readUnsignedInt();
               long colorsImportant = this.iis.readUnsignedInt();
               this.metadata.compression = (int)this.compression;
               this.metadata.imageSize = (int)this.imageSize;
               this.metadata.xPixelsPerMeter = (int)xPelsPerMeter;
               this.metadata.yPixelsPerMeter = (int)yPelsPerMeter;
               this.metadata.colorsUsed = (int)colorsUsed;
               this.metadata.colorsImportant = (int)colorsImportant;
               if (size == 40L) {
                  int sizeOfPalette;
                  switch ((int)this.compression) {
                     case 0:
                     case 1:
                     case 2:
                        int numberOfEntries = (int)((this.bitmapOffset - 14L - size) / 4L);
                        sizeOfPalette = numberOfEntries * 4;
                        this.palette = new byte[sizeOfPalette];
                        this.iis.readFully(this.palette, 0, sizeOfPalette);
                        this.metadata.palette = this.palette;
                        this.metadata.paletteSize = numberOfEntries;
                        if (this.bitsPerPixel == 1) {
                           this.imageType = 4;
                        } else if (this.bitsPerPixel == 4) {
                           this.imageType = 5;
                        } else if (this.bitsPerPixel == 8) {
                           this.imageType = 6;
                        } else if (this.bitsPerPixel == 24) {
                           this.imageType = 7;
                        } else if (this.bitsPerPixel == 16) {
                           this.imageType = 8;
                           this.redMask = 31744;
                           this.greenMask = 992;
                           this.blueMask = 31;
                           this.metadata.redMask = this.redMask;
                           this.metadata.greenMask = this.greenMask;
                           this.metadata.blueMask = this.blueMask;
                        } else if (this.bitsPerPixel == 32) {
                           this.imageType = 9;
                           this.redMask = 16711680;
                           this.greenMask = 65280;
                           this.blueMask = 255;
                           this.metadata.redMask = this.redMask;
                           this.metadata.greenMask = this.greenMask;
                           this.metadata.blueMask = this.blueMask;
                        }

                        this.metadata.bmpVersion = "BMP v. 3.x";
                        break;
                     case 3:
                        if (this.bitsPerPixel == 16) {
                           this.imageType = 8;
                        } else if (this.bitsPerPixel == 32) {
                           this.imageType = 9;
                        }

                        this.redMask = (int)this.iis.readUnsignedInt();
                        this.greenMask = (int)this.iis.readUnsignedInt();
                        this.blueMask = (int)this.iis.readUnsignedInt();
                        this.metadata.redMask = this.redMask;
                        this.metadata.greenMask = this.greenMask;
                        this.metadata.blueMask = this.blueMask;
                        if (colorsUsed != 0L) {
                           sizeOfPalette = (int)colorsUsed * 4;
                           this.palette = new byte[sizeOfPalette];
                           this.iis.readFully(this.palette, 0, sizeOfPalette);
                           this.metadata.palette = this.palette;
                           this.metadata.paletteSize = (int)colorsUsed;
                        }

                        this.metadata.bmpVersion = "BMP v. 3.x NT";
                        break;
                     case 4:
                     case 5:
                        this.metadata.bmpVersion = "BMP v. 3.x";
                        this.imageType = 16;
                        break;
                     default:
                        throw new RuntimeException(I18N.getString("BMPImageReader2"));
                  }
               } else {
                  if (size != 108L && size != 124L) {
                     throw new RuntimeException(I18N.getString("BMPImageReader3"));
                  }

                  if (size == 108L) {
                     this.metadata.bmpVersion = "BMP v. 4.x";
                  } else if (size == 124L) {
                     this.metadata.bmpVersion = "BMP v. 5.x";
                  }

                  this.redMask = (int)this.iis.readUnsignedInt();
                  this.greenMask = (int)this.iis.readUnsignedInt();
                  this.blueMask = (int)this.iis.readUnsignedInt();
                  this.alphaMask = (int)this.iis.readUnsignedInt();
                  long csType = this.iis.readUnsignedInt();
                  int redX = this.iis.readInt();
                  int redY = this.iis.readInt();
                  int redZ = this.iis.readInt();
                  int greenX = this.iis.readInt();
                  int greenY = this.iis.readInt();
                  int greenZ = this.iis.readInt();
                  int blueX = this.iis.readInt();
                  int blueY = this.iis.readInt();
                  int blueZ = this.iis.readInt();
                  long gammaRed = this.iis.readUnsignedInt();
                  long gammaGreen = this.iis.readUnsignedInt();
                  long gammaBlue = this.iis.readUnsignedInt();
                  if (size == 124L) {
                     this.metadata.intent = this.iis.readInt();
                     profileData = this.iis.readInt();
                     profileSize = this.iis.readInt();
                     this.iis.skipBytes(4);
                  }

                  this.metadata.colorSpace = (int)csType;
                  if (csType == 0L) {
                     this.metadata.redX = (double)redX;
                     this.metadata.redY = (double)redY;
                     this.metadata.redZ = (double)redZ;
                     this.metadata.greenX = (double)greenX;
                     this.metadata.greenY = (double)greenY;
                     this.metadata.greenZ = (double)greenZ;
                     this.metadata.blueX = (double)blueX;
                     this.metadata.blueY = (double)blueY;
                     this.metadata.blueZ = (double)blueZ;
                     this.metadata.gammaRed = (int)gammaRed;
                     this.metadata.gammaGreen = (int)gammaGreen;
                     this.metadata.gammaBlue = (int)gammaBlue;
                  }

                  int numberOfEntries = (int)((this.bitmapOffset - 14L - size) / 4L);
                  int sizeOfPalette = numberOfEntries * 4;
                  this.palette = new byte[sizeOfPalette];
                  this.iis.readFully(this.palette, 0, sizeOfPalette);
                  this.metadata.palette = this.palette;
                  this.metadata.paletteSize = numberOfEntries;
                  switch ((int)this.compression) {
                     case 4:
                     case 5:
                        if (size == 108L) {
                           this.imageType = 17;
                        } else if (size == 124L) {
                           this.imageType = 18;
                        }
                        break;
                     default:
                        if (this.bitsPerPixel == 1) {
                           this.imageType = 10;
                        } else if (this.bitsPerPixel == 4) {
                           this.imageType = 11;
                        } else if (this.bitsPerPixel == 8) {
                           this.imageType = 12;
                        } else if (this.bitsPerPixel == 16) {
                           this.imageType = 13;
                           if ((int)this.compression == 0) {
                              this.redMask = 31744;
                              this.greenMask = 992;
                              this.blueMask = 31;
                           }
                        } else if (this.bitsPerPixel == 24) {
                           this.imageType = 14;
                        } else if (this.bitsPerPixel == 32) {
                           this.imageType = 15;
                           if ((int)this.compression == 0) {
                              this.redMask = 16711680;
                              this.greenMask = 65280;
                              this.blueMask = 255;
                           }
                        }

                        this.metadata.redMask = this.redMask;
                        this.metadata.greenMask = this.greenMask;
                        this.metadata.blueMask = this.blueMask;
                        this.metadata.alphaMask = this.alphaMask;
                  }
               }
            }

            if (this.height > 0) {
               this.isBottomUp = true;
            } else {
               this.isBottomUp = false;
               this.height = Math.abs(this.height);
            }

            ColorSpace colorSpace = ColorSpace.getInstance(1000);
            byte[] r;
            if (this.metadata.colorSpace == 3 || this.metadata.colorSpace == 4) {
               this.iis.mark();
               this.iis.skipBytes((long)profileData - size);
               r = new byte[profileSize];
               this.iis.readFully(r, 0, profileSize);
               this.iis.reset();

               try {
                  if (this.metadata.colorSpace == 3) {
                     colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(new String(r)));
                  } else {
                     colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(r));
                  }
               } catch (Exception var34) {
                  colorSpace = ColorSpace.getInstance(1000);
               }
            }

            if (this.bitsPerPixel != 0 && this.compression != 4L && this.compression != 5L) {
               int[] bandOffsets;
               int i;
               if (this.bitsPerPixel != 1 && this.bitsPerPixel != 4 && this.bitsPerPixel != 8) {
                  if (this.bitsPerPixel == 16) {
                     this.numBands = 3;
                     this.sampleModel = new SinglePixelPackedSampleModel(1, this.width, this.height, new int[]{this.redMask, this.greenMask, this.blueMask});
                     this.colorModel = new DirectColorModel((ColorSpace)colorSpace, 16, this.redMask, this.greenMask, this.blueMask, 0, false, 1);
                  } else if (this.bitsPerPixel == 32) {
                     this.numBands = this.alphaMask == 0 ? 3 : 4;
                     if (this.redMask == 0 || this.greenMask == 0 || this.blueMask == 0) {
                        this.redMask = 16711680;
                        this.greenMask = 65280;
                        this.blueMask = 255;
                        this.alphaMask = -16777216;
                     }

                     bandOffsets = this.numBands == 3 ? new int[]{this.redMask, this.greenMask, this.blueMask} : new int[]{this.redMask, this.greenMask, this.blueMask, this.alphaMask};
                     this.sampleModel = new SinglePixelPackedSampleModel(3, this.width, this.height, bandOffsets);
                     this.colorModel = new DirectColorModel((ColorSpace)colorSpace, 32, this.redMask, this.greenMask, this.blueMask, this.alphaMask, false, 3);
                  } else {
                     this.numBands = 3;
                     bandOffsets = new int[this.numBands];

                     for(i = 0; i < this.numBands; ++i) {
                        bandOffsets[i] = this.numBands - 1 - i;
                     }

                     this.sampleModel = new PixelInterleavedSampleModel(0, this.width, this.height, this.numBands, this.numBands * this.width, bandOffsets);
                     this.colorModel = ImageUtil.createColorModel((ColorSpace)colorSpace, this.sampleModel);
                  }
               } else {
                  this.numBands = 1;
                  if (this.bitsPerPixel != 8) {
                     this.sampleModel = new MultiPixelPackedSampleModel(0, this.width, this.height, this.bitsPerPixel);
                  } else {
                     bandOffsets = new int[this.numBands];

                     for(i = 0; i < this.numBands; ++i) {
                        bandOffsets[i] = this.numBands - 1 - i;
                     }

                     this.sampleModel = new PixelInterleavedSampleModel(0, this.width, this.height, this.numBands, this.numBands * this.width, bandOffsets);
                  }

                  byte[] b;
                  int i;
                  byte[] g;
                  int off;
                  if (this.imageType != 0 && this.imageType != 1 && this.imageType != 2) {
                     size = (long)(this.palette.length / 4);
                     if (size > 256L) {
                        size = 256L;
                     }

                     r = new byte[(int)size];
                     g = new byte[(int)size];
                     b = new byte[(int)size];

                     for(i = 0; (long)i < size; ++i) {
                        off = 4 * i;
                        b[i] = this.palette[off];
                        g[i] = this.palette[off + 1];
                        r[i] = this.palette[off + 2];
                     }
                  } else {
                     size = (long)(this.palette.length / 3);
                     if (size > 256L) {
                        size = 256L;
                     }

                     r = new byte[(int)size];
                     g = new byte[(int)size];
                     b = new byte[(int)size];

                     for(i = 0; i < (int)size; ++i) {
                        off = 3 * i;
                        b[i] = this.palette[off];
                        g[i] = this.palette[off + 1];
                        r[i] = this.palette[off + 2];
                     }
                  }

                  if (ImageUtil.isIndicesForGrayscale(r, g, b)) {
                     this.colorModel = ImageUtil.createColorModel((ColorSpace)null, this.sampleModel);
                  } else {
                     this.colorModel = new IndexColorModel(this.bitsPerPixel, (int)size, r, g, b);
                  }
               }
            } else {
               this.colorModel = null;
               this.sampleModel = null;
            }

            this.originalSampleModel = this.sampleModel;
            this.originalColorModel = this.colorModel;
            this.iis.reset();
            this.iis.skipBytes(this.bitmapOffset);
            this.gotHeader = true;
            this.imageDataOffset = this.iis.getStreamPosition();
         } else {
            throw new IllegalArgumentException(I18N.getString("BMPImageReader1"));
         }
      }
   }

   public Iterator getImageTypes(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      this.readHeader();
      ArrayList list = new ArrayList(1);
      list.add(new ImageTypeSpecifier(this.originalColorModel, this.originalSampleModel));
      return list.iterator();
   }

   public ImageReadParam getDefaultReadParam() {
      return new ImageReadParam();
   }

   public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      if (this.metadata == null) {
         this.readHeader();
      }

      return this.metadata;
   }

   public IIOMetadata getStreamMetadata() throws IOException {
      return null;
   }

   public boolean isRandomAccessEasy(int imageIndex) throws IOException {
      this.checkIndex(imageIndex);
      this.readHeader();
      return this.metadata.compression == 0;
   }

   public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
      if (this.iis == null) {
         throw new IllegalStateException(I18N.getString("BMPImageReader5"));
      } else {
         this.checkIndex(imageIndex);
         this.clearAbortRequest();
         this.processImageStarted(imageIndex);
         if (param == null) {
            param = this.getDefaultReadParam();
         }

         this.readHeader();
         this.sourceRegion = new Rectangle(0, 0, 0, 0);
         this.destinationRegion = new Rectangle(0, 0, 0, 0);
         computeRegions(param, this.width, this.height, param.getDestination(), this.sourceRegion, this.destinationRegion);
         this.scaleX = param.getSourceXSubsampling();
         this.scaleY = param.getSourceYSubsampling();
         this.sourceBands = param.getSourceBands();
         this.destBands = param.getDestinationBands();
         this.seleBand = this.sourceBands != null && this.destBands != null;
         this.noTransform = this.destinationRegion.equals(new Rectangle(0, 0, this.width, this.height)) || this.seleBand;
         if (!this.seleBand) {
            this.sourceBands = new int[this.numBands];
            this.destBands = new int[this.numBands];

            for(int i = 0; i < this.numBands; ++i) {
               this.destBands[i] = this.sourceBands[i] = i;
            }
         }

         this.bi = param.getDestination();
         WritableRaster raster = null;
         if (this.bi == null) {
            if (this.sampleModel != null && this.colorModel != null) {
               this.sampleModel = this.sampleModel.createCompatibleSampleModel(this.destinationRegion.x + this.destinationRegion.width, this.destinationRegion.y + this.destinationRegion.height);
               if (this.seleBand) {
                  this.sampleModel = this.sampleModel.createSubsetSampleModel(this.sourceBands);
               }

               raster = Raster.createWritableRaster(this.sampleModel, new Point());
               this.bi = new BufferedImage(this.colorModel, raster, false, (Hashtable)null);
            }
         } else {
            raster = this.bi.getWritableTile(0, 0);
            this.sampleModel = this.bi.getSampleModel();
            this.colorModel = this.bi.getColorModel();
            this.noTransform &= this.destinationRegion.equals(raster.getBounds());
         }

         byte[] bdata = null;
         short[] sdata = null;
         int[] idata = null;
         if (this.sampleModel != null) {
            if (this.sampleModel.getDataType() == 0) {
               bdata = (byte[])((DataBufferByte)raster.getDataBuffer()).getData();
            } else if (this.sampleModel.getDataType() == 1) {
               sdata = (short[])((DataBufferUShort)raster.getDataBuffer()).getData();
            } else if (this.sampleModel.getDataType() == 3) {
               idata = (int[])((DataBufferInt)raster.getDataBuffer()).getData();
            }
         }

         label74:
         switch (this.imageType) {
            case 0:
               this.read1Bit(bdata);
               break;
            case 1:
               this.read4Bit(bdata);
               break;
            case 2:
               this.read8Bit(bdata);
               break;
            case 3:
               this.read24Bit(bdata);
               break;
            case 4:
               this.read1Bit(bdata);
               break;
            case 5:
               switch ((int)this.compression) {
                  case 0:
                     this.read4Bit(bdata);
                     break label74;
                  case 2:
                     this.readRLE4(bdata);
                     break label74;
                  default:
                     throw new RuntimeException(I18N.getString("BMPImageReader1"));
               }
            case 6:
               switch ((int)this.compression) {
                  case 0:
                     this.read8Bit(bdata);
                     break label74;
                  case 1:
                     this.readRLE8(bdata);
                     break label74;
                  default:
                     throw new RuntimeException(I18N.getString("BMPImageReader1"));
               }
            case 7:
               this.read24Bit(bdata);
               break;
            case 8:
               this.read16Bit(sdata);
               break;
            case 9:
               this.read32Bit(idata);
               break;
            case 10:
               this.read1Bit(bdata);
               break;
            case 11:
               switch ((int)this.compression) {
                  case 0:
                     this.read4Bit(bdata);
                     break;
                  case 2:
                     this.readRLE4(bdata);
                     break;
                  default:
                     throw new RuntimeException(I18N.getString("BMPImageReader1"));
               }
            case 12:
               switch ((int)this.compression) {
                  case 0:
                     this.read8Bit(bdata);
                     break label74;
                  case 1:
                     this.readRLE8(bdata);
                     break label74;
                  default:
                     throw new RuntimeException(I18N.getString("BMPImageReader1"));
               }
            case 13:
               this.read16Bit(sdata);
               break;
            case 14:
               this.read24Bit(bdata);
               break;
            case 15:
               this.read32Bit(idata);
               break;
            case 16:
            case 17:
            case 18:
               this.bi = this.readEmbedded((int)this.compression, this.bi, param);
         }

         if (this.abortRequested()) {
            this.processReadAborted();
         } else {
            this.processImageComplete();
         }

         return this.bi;
      }
   }

   public boolean canReadRaster() {
      return true;
   }

   public Raster readRaster(int imageIndex, ImageReadParam param) throws IOException {
      BufferedImage bi = this.read(imageIndex, param);
      return bi.getData();
   }

   private void resetHeaderInfo() {
      this.gotHeader = false;
      this.bi = null;
      this.sampleModel = this.originalSampleModel = null;
      this.colorModel = this.originalColorModel = null;
   }

   public void reset() {
      super.reset();
      this.iis = null;
      this.resetHeaderInfo();
   }

   private void read1Bit(byte[] bdata) throws IOException {
      int bytesPerScanline = (this.width + 7) / 8;
      int padding = bytesPerScanline % 4;
      if (padding != 0) {
         padding = 4 - padding;
      }

      int lineLength = bytesPerScanline + padding;
      int lineStride;
      if (this.noTransform) {
         int j = this.isBottomUp ? (this.height - 1) * bytesPerScanline : 0;

         for(lineStride = 0; lineStride < this.height && !this.abortRequested(); ++lineStride) {
            this.iis.readFully(bdata, j, bytesPerScanline);
            this.iis.skipBytes(padding);
            j += this.isBottomUp ? -bytesPerScanline : bytesPerScanline;
            this.processImageUpdate(this.bi, 0, lineStride, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)lineStride / (float)this.destinationRegion.height);
         }
      } else {
         byte[] buf = new byte[lineLength];
         lineStride = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
         int skipLength;
         if (this.isBottomUp) {
            skipLength = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
            this.iis.skipBytes(lineLength * (this.height - 1 - skipLength));
         } else {
            this.iis.skipBytes(lineLength * this.sourceRegion.y);
         }

         skipLength = lineLength * (this.scaleY - 1);
         int[] srcOff = new int[this.destinationRegion.width];
         int[] destOff = new int[this.destinationRegion.width];
         int[] srcPos = new int[this.destinationRegion.width];
         int[] destPos = new int[this.destinationRegion.width];
         int k = this.destinationRegion.x;
         int j = this.sourceRegion.x;

         int y;
         for(y = 0; k < this.destinationRegion.x + this.destinationRegion.width; j += this.scaleX) {
            srcPos[y] = j >> 3;
            srcOff[y] = 7 - (j & 7);
            destPos[y] = k >> 3;
            destOff[y] = 7 - (k & 7);
            ++k;
            ++y;
         }

         k = this.destinationRegion.y * lineStride;
         if (this.isBottomUp) {
            k += (this.destinationRegion.height - 1) * lineStride;
         }

         j = 0;

         for(y = this.sourceRegion.y; j < this.destinationRegion.height && !this.abortRequested(); y += this.scaleY) {
            this.iis.read(buf, 0, lineLength);

            for(int i = 0; i < this.destinationRegion.width; ++i) {
               int v = buf[srcPos[i]] >> srcOff[i] & 1;
               bdata[k + destPos[i]] = (byte)(bdata[k + destPos[i]] | v << destOff[i]);
            }

            k += this.isBottomUp ? -lineStride : lineStride;
            this.iis.skipBytes(skipLength);
            this.processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)j / (float)this.destinationRegion.height);
            ++j;
         }
      }

   }

   private void read4Bit(byte[] bdata) throws IOException {
      int bytesPerScanline = (this.width + 1) / 2;
      int padding = bytesPerScanline % 4;
      if (padding != 0) {
         padding = 4 - padding;
      }

      int lineLength = bytesPerScanline + padding;
      int lineStride;
      if (this.noTransform) {
         int j = this.isBottomUp ? (this.height - 1) * bytesPerScanline : 0;

         for(lineStride = 0; lineStride < this.height && !this.abortRequested(); ++lineStride) {
            this.iis.readFully(bdata, j, bytesPerScanline);
            this.iis.skipBytes(padding);
            j += this.isBottomUp ? -bytesPerScanline : bytesPerScanline;
            this.processImageUpdate(this.bi, 0, lineStride, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)lineStride / (float)this.destinationRegion.height);
         }
      } else {
         byte[] buf = new byte[lineLength];
         lineStride = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
         int skipLength;
         if (this.isBottomUp) {
            skipLength = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
            this.iis.skipBytes(lineLength * (this.height - 1 - skipLength));
         } else {
            this.iis.skipBytes(lineLength * this.sourceRegion.y);
         }

         skipLength = lineLength * (this.scaleY - 1);
         int[] srcOff = new int[this.destinationRegion.width];
         int[] destOff = new int[this.destinationRegion.width];
         int[] srcPos = new int[this.destinationRegion.width];
         int[] destPos = new int[this.destinationRegion.width];
         int k = this.destinationRegion.x;
         int j = this.sourceRegion.x;

         int y;
         for(y = 0; k < this.destinationRegion.x + this.destinationRegion.width; j += this.scaleX) {
            srcPos[y] = j >> 1;
            srcOff[y] = 1 - (j & 1) << 2;
            destPos[y] = k >> 1;
            destOff[y] = 1 - (k & 1) << 2;
            ++k;
            ++y;
         }

         k = this.destinationRegion.y * lineStride;
         if (this.isBottomUp) {
            k += (this.destinationRegion.height - 1) * lineStride;
         }

         j = 0;

         for(y = this.sourceRegion.y; j < this.destinationRegion.height && !this.abortRequested(); y += this.scaleY) {
            this.iis.read(buf, 0, lineLength);

            for(int i = 0; i < this.destinationRegion.width; ++i) {
               int v = buf[srcPos[i]] >> srcOff[i] & 15;
               bdata[k + destPos[i]] = (byte)(bdata[k + destPos[i]] | v << destOff[i]);
            }

            k += this.isBottomUp ? -lineStride : lineStride;
            this.iis.skipBytes(skipLength);
            this.processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)j / (float)this.destinationRegion.height);
            ++j;
         }
      }

   }

   private void read8Bit(byte[] bdata) throws IOException {
      int padding = this.width % 4;
      if (padding != 0) {
         padding = 4 - padding;
      }

      int lineLength = this.width + padding;
      int lineStride;
      if (this.noTransform) {
         int j = this.isBottomUp ? (this.height - 1) * this.width : 0;

         for(lineStride = 0; lineStride < this.height && !this.abortRequested(); ++lineStride) {
            this.iis.readFully(bdata, j, this.width);
            this.iis.skipBytes(padding);
            j += this.isBottomUp ? -this.width : this.width;
            this.processImageUpdate(this.bi, 0, lineStride, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)lineStride / (float)this.destinationRegion.height);
         }
      } else {
         byte[] buf = new byte[lineLength];
         lineStride = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
         int skipLength;
         if (this.isBottomUp) {
            skipLength = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
            this.iis.skipBytes(lineLength * (this.height - 1 - skipLength));
         } else {
            this.iis.skipBytes(lineLength * this.sourceRegion.y);
         }

         skipLength = lineLength * (this.scaleY - 1);
         int k = this.destinationRegion.y * lineStride;
         if (this.isBottomUp) {
            k += (this.destinationRegion.height - 1) * lineStride;
         }

         k += this.destinationRegion.x;
         int j = 0;

         for(int y = this.sourceRegion.y; j < this.destinationRegion.height && !this.abortRequested(); y += this.scaleY) {
            this.iis.read(buf, 0, lineLength);
            int i = 0;

            for(int m = this.sourceRegion.x; i < this.destinationRegion.width; m += this.scaleX) {
               bdata[k + i] = buf[m];
               ++i;
            }

            k += this.isBottomUp ? -lineStride : lineStride;
            this.iis.skipBytes(skipLength);
            this.processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)j / (float)this.destinationRegion.height);
            ++j;
         }
      }

   }

   private void read24Bit(byte[] bdata) throws IOException {
      int padding = this.width * 3 % 4;
      if (padding != 0) {
         padding = 4 - padding;
      }

      int lineStride = this.width * 3;
      int lineLength = lineStride + padding;
      int skipLength;
      if (this.noTransform) {
         int j = this.isBottomUp ? (this.height - 1) * this.width * 3 : 0;

         for(skipLength = 0; skipLength < this.height && !this.abortRequested(); ++skipLength) {
            this.iis.readFully(bdata, j, lineStride);
            this.iis.skipBytes(padding);
            j += this.isBottomUp ? -lineStride : lineStride;
            this.processImageUpdate(this.bi, 0, skipLength, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)skipLength / (float)this.destinationRegion.height);
         }
      } else {
         byte[] buf = new byte[lineLength];
         lineStride = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
         if (this.isBottomUp) {
            skipLength = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
            this.iis.skipBytes(lineLength * (this.height - 1 - skipLength));
         } else {
            this.iis.skipBytes(lineLength * this.sourceRegion.y);
         }

         skipLength = lineLength * (this.scaleY - 1);
         int k = this.destinationRegion.y * lineStride;
         if (this.isBottomUp) {
            k += (this.destinationRegion.height - 1) * lineStride;
         }

         k += this.destinationRegion.x * 3;
         int j = 0;

         for(int y = this.sourceRegion.y; j < this.destinationRegion.height && !this.abortRequested(); y += this.scaleY) {
            this.iis.read(buf, 0, lineLength);
            int i = 0;

            for(int m = 3 * this.sourceRegion.x; i < this.destinationRegion.width; m += 3 * this.scaleX) {
               int n = 3 * i + k;

               for(int b = 0; b < this.destBands.length; ++b) {
                  bdata[n + this.destBands[b]] = buf[m + this.sourceBands[b]];
               }

               ++i;
            }

            k += this.isBottomUp ? -lineStride : lineStride;
            this.iis.skipBytes(skipLength);
            this.processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)j / (float)this.destinationRegion.height);
            ++j;
         }
      }

   }

   private void read16Bit(short[] sdata) throws IOException {
      int padding = this.width * 2 % 4;
      if (padding != 0) {
         padding = 4 - padding;
      }

      int lineLength = this.width + padding / 2;
      int lineStride;
      if (this.noTransform) {
         int j = this.isBottomUp ? (this.height - 1) * this.width : 0;

         for(lineStride = 0; lineStride < this.height && !this.abortRequested(); ++lineStride) {
            this.iis.readFully(sdata, j, this.width);
            this.iis.skipBytes(padding);
            j += this.isBottomUp ? -this.width : this.width;
            this.processImageUpdate(this.bi, 0, lineStride, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)lineStride / (float)this.destinationRegion.height);
         }
      } else {
         short[] buf = new short[lineLength];
         lineStride = ((SinglePixelPackedSampleModel)this.sampleModel).getScanlineStride();
         int skipLength;
         if (this.isBottomUp) {
            skipLength = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
            this.iis.skipBytes(lineLength * (this.height - 1 - skipLength) << 1);
         } else {
            this.iis.skipBytes(lineLength * this.sourceRegion.y << 1);
         }

         skipLength = lineLength * (this.scaleY - 1) << 1;
         int k = this.destinationRegion.y * lineStride;
         if (this.isBottomUp) {
            k += (this.destinationRegion.height - 1) * lineStride;
         }

         k += this.destinationRegion.x;
         int j = 0;

         for(int y = this.sourceRegion.y; j < this.destinationRegion.height && !this.abortRequested(); y += this.scaleY) {
            this.iis.readFully(buf, 0, lineLength);
            int i = 0;

            for(int m = this.sourceRegion.x; i < this.destinationRegion.width; m += this.scaleX) {
               sdata[k + i] = buf[m];
               ++i;
            }

            k += this.isBottomUp ? -lineStride : lineStride;
            this.iis.skipBytes(skipLength);
            this.processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)j / (float)this.destinationRegion.height);
            ++j;
         }
      }

   }

   private void read32Bit(int[] idata) throws IOException {
      int lineStride;
      if (this.noTransform) {
         int j = this.isBottomUp ? (this.height - 1) * this.width : 0;

         for(lineStride = 0; lineStride < this.height && !this.abortRequested(); ++lineStride) {
            this.iis.readFully(idata, j, this.width);
            j += this.isBottomUp ? -this.width : this.width;
            this.processImageUpdate(this.bi, 0, lineStride, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)lineStride / (float)this.destinationRegion.height);
         }
      } else {
         int[] buf = new int[this.width];
         lineStride = ((SinglePixelPackedSampleModel)this.sampleModel).getScanlineStride();
         int skipLength;
         if (this.isBottomUp) {
            skipLength = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
            this.iis.skipBytes(this.width * (this.height - 1 - skipLength) << 2);
         } else {
            this.iis.skipBytes(this.width * this.sourceRegion.y << 2);
         }

         skipLength = this.width * (this.scaleY - 1) << 2;
         int k = this.destinationRegion.y * lineStride;
         if (this.isBottomUp) {
            k += (this.destinationRegion.height - 1) * lineStride;
         }

         k += this.destinationRegion.x;
         int j = 0;

         for(int y = this.sourceRegion.y; j < this.destinationRegion.height && !this.abortRequested(); y += this.scaleY) {
            this.iis.readFully(buf, 0, this.width);
            int i = 0;

            for(int m = this.sourceRegion.x; i < this.destinationRegion.width; m += this.scaleX) {
               idata[k + i] = buf[m];
               ++i;
            }

            k += this.isBottomUp ? -lineStride : lineStride;
            this.iis.skipBytes(skipLength);
            this.processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)j / (float)this.destinationRegion.height);
            ++j;
         }
      }

   }

   private void readRLE8(byte[] bdata) throws IOException {
      int imSize = (int)this.imageSize;
      if (imSize == 0) {
         imSize = (int)(this.bitmapFileSize - this.bitmapOffset);
      }

      int padding = 0;
      int remainder = this.width % 4;
      if (remainder != 0) {
         padding = 4 - remainder;
      }

      byte[] values = new byte[imSize];
      int bytesRead = false;
      this.iis.readFully(values, 0, imSize);
      this.decodeRLE8(imSize, padding, values, bdata);
   }

   private void decodeRLE8(int imSize, int padding, byte[] values, byte[] bdata) throws IOException {
      byte[] val = new byte[this.width * this.height];
      int count = 0;
      int l = 0;
      boolean flag = false;
      int lineNo = this.isBottomUp ? this.height - 1 : 0;
      int lineStride = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
      int finished = 0;

      while(count != imSize) {
         int value = values[count++] & 255;
         int currentLine;
         if (value == 0) {
            int i;
            int i;
            switch (values[count++] & 255) {
               case 0:
               case 1:
                  if (lineNo >= this.sourceRegion.y && lineNo < this.sourceRegion.y + this.sourceRegion.height) {
                     if (this.noTransform) {
                        currentLine = lineNo * this.width;

                        for(i = 0; i < this.width; ++i) {
                           bdata[currentLine++] = val[i];
                        }

                        this.processImageUpdate(this.bi, 0, lineNo, this.destinationRegion.width, 1, 1, 1, new int[]{0});
                        ++finished;
                     } else if ((lineNo - this.sourceRegion.y) % this.scaleY == 0) {
                        currentLine = (lineNo - this.sourceRegion.y) / this.scaleY + this.destinationRegion.y;
                        i = currentLine * lineStride;
                        i += this.destinationRegion.x;

                        for(i = this.sourceRegion.x; i < this.sourceRegion.x + this.sourceRegion.width; i += this.scaleX) {
                           bdata[i++] = val[i];
                        }

                        this.processImageUpdate(this.bi, 0, currentLine, this.destinationRegion.width, 1, 1, 1, new int[]{0});
                        ++finished;
                     }
                  }

                  this.processImageProgress(100.0F * (float)finished / (float)this.destinationRegion.height);
                  lineNo += this.isBottomUp ? -1 : 1;
                  l = 0;
                  if (!this.abortRequested() && (values[count - 1] & 255) == 1) {
                     flag = true;
                  }
                  break;
               case 2:
                  currentLine = values[count++] & 255;
                  i = values[count] & 255;
                  l += currentLine + i * this.width;
                  break;
               default:
                  i = values[count - 1] & 255;

                  for(int i = 0; i < i; ++i) {
                     val[l++] = (byte)(values[count++] & 255);
                  }

                  if ((i & 1) == 1) {
                     ++count;
                  }
            }
         } else {
            for(currentLine = 0; currentLine < value; ++currentLine) {
               val[l++] = (byte)(values[count] & 255);
            }

            ++count;
         }

         if (flag) {
            break;
         }
      }

   }

   private void readRLE4(byte[] bdata) throws IOException {
      int imSize = (int)this.imageSize;
      if (imSize == 0) {
         imSize = (int)(this.bitmapFileSize - this.bitmapOffset);
      }

      int padding = 0;
      int remainder = this.width % 4;
      if (remainder != 0) {
         padding = 4 - remainder;
      }

      byte[] values = new byte[imSize];
      this.iis.readFully(values, 0, imSize);
      this.decodeRLE4(imSize, padding, values, bdata);
   }

   private void decodeRLE4(int imSize, int padding, byte[] values, byte[] bdata) throws IOException {
      byte[] val = new byte[this.width];
      int count = 0;
      int l = 0;
      boolean flag = false;
      int lineNo = this.isBottomUp ? this.height - 1 : 0;
      int lineStride = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
      int finished = 0;

      while(count != imSize) {
         int value = values[count++] & 255;
         int pos;
         if (value == 0) {
            int currentLine;
            int shift;
            int i;
            switch (values[count++] & 255) {
               case 0:
               case 1:
                  if (lineNo >= this.sourceRegion.y && lineNo < this.sourceRegion.y + this.sourceRegion.height) {
                     if (this.noTransform) {
                        currentLine = lineNo * (this.width + 1 >> 1);
                        pos = 0;

                        for(shift = 0; pos < this.width >> 1; ++pos) {
                           bdata[currentLine++] = (byte)(val[shift++] << 4 | val[shift++]);
                        }

                        if ((this.width & 1) == 1) {
                           bdata[currentLine] = (byte)(bdata[currentLine] | val[this.width - 1] << 4);
                        }

                        this.processImageUpdate(this.bi, 0, lineNo, this.destinationRegion.width, 1, 1, 1, new int[]{0});
                        ++finished;
                     } else if ((lineNo - this.sourceRegion.y) % this.scaleY == 0) {
                        currentLine = (lineNo - this.sourceRegion.y) / this.scaleY + this.destinationRegion.y;
                        pos = currentLine * lineStride;
                        pos += this.destinationRegion.x >> 1;
                        shift = 1 - (this.destinationRegion.x & 1) << 2;

                        for(i = this.sourceRegion.x; i < this.sourceRegion.x + this.sourceRegion.width; i += this.scaleX) {
                           bdata[pos] = (byte)(bdata[pos] | val[i] << shift);
                           shift += 4;
                           if (shift == 4) {
                              ++pos;
                           }

                           shift &= 7;
                        }

                        this.processImageUpdate(this.bi, 0, currentLine, this.destinationRegion.width, 1, 1, 1, new int[]{0});
                        ++finished;
                     }
                  }

                  this.processImageProgress(100.0F * (float)finished / (float)this.destinationRegion.height);
                  lineNo += this.isBottomUp ? -1 : 1;
                  l = 0;
                  if (!this.abortRequested() && (values[count - 1] & 255) == 1) {
                     flag = true;
                  }
                  break;
               case 2:
                  currentLine = values[count++] & 255;
                  pos = values[count] & 255;
                  l += currentLine + pos * this.width;
                  break;
               default:
                  shift = values[count - 1] & 255;

                  for(i = 0; i < shift; ++i) {
                     val[l++] = (byte)((i & 1) == 0 ? (values[count] & 240) >> 4 : values[count++] & 15);
                  }

                  if ((shift & 1) == 1) {
                     ++count;
                  }

                  if (((int)Math.ceil((double)(shift / 2)) & 1) == 1) {
                     ++count;
                  }
            }
         } else {
            int[] alternate = new int[]{(values[count] & 240) >> 4, values[count] & 15};

            for(pos = 0; pos < value && l < this.width; ++pos) {
               val[l++] = (byte)alternate[pos & 1];
            }

            ++count;
         }

         if (flag) {
            break;
         }
      }

   }

   private BufferedImage readEmbedded(int type, BufferedImage bi, ImageReadParam bmpParam) throws IOException {
      String format;
      switch (type) {
         case 4:
            format = "JPEG";
            break;
         case 5:
            format = "PNG";
            break;
         default:
            throw new IOException("Unexpected compression type: " + type);
      }

      ImageReader reader = (ImageReader)ImageIO.getImageReadersByFormatName(format).next();
      if (reader == null) {
         throw new RuntimeException(I18N.getString("BMPImageReader4") + " " + format);
      } else {
         byte[] buff = new byte[(int)this.imageSize];
         this.iis.read(buff);
         reader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(buff)));
         if (bi == null) {
            ImageTypeSpecifier embType = (ImageTypeSpecifier)reader.getImageTypes(0).next();
            bi = embType.createBufferedImage(this.destinationRegion.x + this.destinationRegion.width, this.destinationRegion.y + this.destinationRegion.height);
         }

         reader.addIIOReadProgressListener(new EmbeddedProgressAdapter() {
            public void imageProgress(ImageReader source, float percentageDone) {
               BMPImageReader.this.processImageProgress(percentageDone);
            }
         });
         reader.addIIOReadUpdateListener(new IIOReadUpdateListener() {
            public void imageUpdate(ImageReader source, BufferedImage theImage, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {
               BMPImageReader.this.processImageUpdate(theImage, minX, minY, width, height, periodX, periodY, bands);
            }

            public void passComplete(ImageReader source, BufferedImage theImage) {
               BMPImageReader.this.processPassComplete(theImage);
            }

            public void passStarted(ImageReader source, BufferedImage theImage, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {
               BMPImageReader.this.processPassStarted(theImage, pass, minPass, maxPass, minX, minY, periodX, periodY, bands);
            }

            public void thumbnailPassComplete(ImageReader source, BufferedImage thumb) {
            }

            public void thumbnailPassStarted(ImageReader source, BufferedImage thumb, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {
            }

            public void thumbnailUpdate(ImageReader source, BufferedImage theThumbnail, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {
            }
         });
         reader.addIIOReadWarningListener(new IIOReadWarningListener() {
            public void warningOccurred(ImageReader source, String warning) {
               BMPImageReader.this.processWarningOccurred(warning);
            }
         });
         ImageReadParam param = reader.getDefaultReadParam();
         param.setDestination(bi);
         param.setDestinationBands(bmpParam.getDestinationBands());
         param.setDestinationOffset(bmpParam.getDestinationOffset());
         param.setSourceBands(bmpParam.getSourceBands());
         param.setSourceRegion(bmpParam.getSourceRegion());
         param.setSourceSubsampling(bmpParam.getSourceXSubsampling(), bmpParam.getSourceYSubsampling(), bmpParam.getSubsamplingXOffset(), bmpParam.getSubsamplingYOffset());
         reader.read(0, param);
         return bi;
      }
   }

   private class EmbeddedProgressAdapter implements IIOReadProgressListener {
      private EmbeddedProgressAdapter() {
      }

      public void imageComplete(ImageReader src) {
      }

      public void imageProgress(ImageReader src, float percentageDone) {
      }

      public void imageStarted(ImageReader src, int imageIndex) {
      }

      public void thumbnailComplete(ImageReader src) {
      }

      public void thumbnailProgress(ImageReader src, float percentageDone) {
      }

      public void thumbnailStarted(ImageReader src, int iIdx, int tIdx) {
      }

      public void sequenceComplete(ImageReader src) {
      }

      public void sequenceStarted(ImageReader src, int minIndex) {
      }

      public void readAborted(ImageReader src) {
      }

      // $FF: synthetic method
      EmbeddedProgressAdapter(Object x1) {
         this();
      }
   }
}
