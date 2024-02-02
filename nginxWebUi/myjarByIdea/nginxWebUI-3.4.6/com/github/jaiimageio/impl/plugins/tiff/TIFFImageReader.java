package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.impl.common.ImageUtil;
import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
import com.github.jaiimageio.plugins.tiff.TIFFColorConverter;
import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import com.github.jaiimageio.plugins.tiff.TIFFImageReadParam;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node;

public class TIFFImageReader extends ImageReader {
   private static final boolean DEBUG = false;
   ImageInputStream stream = null;
   boolean gotHeader = false;
   ImageReadParam imageReadParam = this.getDefaultReadParam();
   TIFFStreamMetadata streamMetadata = null;
   int currIndex = -1;
   TIFFImageMetadata imageMetadata = null;
   List imageStartPosition = new ArrayList();
   int numImages = -1;
   HashMap imageTypeMap = new HashMap();
   BufferedImage theImage = null;
   int width = -1;
   int height = -1;
   int numBands = -1;
   int tileOrStripWidth = -1;
   int tileOrStripHeight = -1;
   int planarConfiguration = 1;
   int rowsDone = 0;
   int compression;
   int photometricInterpretation;
   int samplesPerPixel;
   int[] sampleFormat;
   int[] bitsPerSample;
   int[] extraSamples;
   char[] colorMap;
   int sourceXOffset;
   int sourceYOffset;
   int srcXSubsampling;
   int srcYSubsampling;
   int dstWidth;
   int dstHeight;
   int dstMinX;
   int dstMinY;
   int dstXOffset;
   int dstYOffset;
   int tilesAcross;
   int tilesDown;
   int pixelsRead;
   int pixelsToRead;
   private int[] sourceBands;
   private int[] destinationBands;
   private TIFFDecompressor decompressor;

   public TIFFImageReader(ImageReaderSpi originatingProvider) {
      super(originatingProvider);
   }

   public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
      super.setInput(input, seekForwardOnly, ignoreMetadata);
      this.resetLocal();
      if (input != null) {
         if (!(input instanceof ImageInputStream)) {
            throw new IllegalArgumentException("input not an ImageInputStream!");
         }

         this.stream = (ImageInputStream)input;
      } else {
         this.stream = null;
      }

   }

   private void readHeader() throws IIOException {
      if (!this.gotHeader) {
         if (this.stream == null) {
            throw new IllegalStateException("Input not set!");
         } else {
            this.streamMetadata = new TIFFStreamMetadata();

            try {
               int byteOrder = this.stream.readUnsignedShort();
               if (byteOrder == 19789) {
                  this.streamMetadata.byteOrder = ByteOrder.BIG_ENDIAN;
                  this.stream.setByteOrder(ByteOrder.BIG_ENDIAN);
               } else if (byteOrder == 18761) {
                  this.streamMetadata.byteOrder = ByteOrder.LITTLE_ENDIAN;
                  this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
               } else {
                  this.processWarningOccurred("Bad byte order in header, assuming little-endian");
                  this.streamMetadata.byteOrder = ByteOrder.LITTLE_ENDIAN;
                  this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
               }

               int magic = this.stream.readUnsignedShort();
               if (magic != 42) {
                  this.processWarningOccurred("Bad magic number in header, continuing");
               }

               long offset = this.stream.readUnsignedInt();
               this.imageStartPosition.add(new Long(offset));
               this.stream.seek(offset);
            } catch (IOException var5) {
               throw new IIOException("I/O error reading header!", var5);
            }

            this.gotHeader = true;
         }
      }
   }

   private int locateImage(int imageIndex) throws IIOException {
      this.readHeader();

      try {
         int index = Math.min(imageIndex, this.imageStartPosition.size() - 1);
         Long l = (Long)this.imageStartPosition.get(index);
         this.stream.seek(l);

         while(index < imageIndex) {
            int count = this.stream.readUnsignedShort();
            this.stream.skipBytes(12 * count);
            long offset = this.stream.readUnsignedInt();
            if (offset == 0L) {
               return index;
            }

            this.imageStartPosition.add(new Long(offset));
            this.stream.seek(offset);
            ++index;
         }
      } catch (IOException var7) {
         throw new IIOException("Couldn't seek!", var7);
      }

      if (this.currIndex != imageIndex) {
         this.imageMetadata = null;
      }

      this.currIndex = imageIndex;
      return imageIndex;
   }

   public int getNumImages(boolean allowSearch) throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException("Input not set!");
      } else if (this.seekForwardOnly && allowSearch) {
         throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!");
      } else if (this.numImages > 0) {
         return this.numImages;
      } else {
         if (allowSearch) {
            this.numImages = this.locateImage(Integer.MAX_VALUE) + 1;
         }

         return this.numImages;
      }
   }

   public IIOMetadata getStreamMetadata() throws IIOException {
      this.readHeader();
      return this.streamMetadata;
   }

   private void checkIndex(int imageIndex) {
      if (imageIndex < this.minIndex) {
         throw new IndexOutOfBoundsException("imageIndex < minIndex!");
      } else {
         if (this.seekForwardOnly) {
            this.minIndex = imageIndex;
         }

      }
   }

   private void seekToImage(int imageIndex) throws IIOException {
      this.checkIndex(imageIndex);
      int index = this.locateImage(imageIndex);
      if (index != imageIndex) {
         throw new IndexOutOfBoundsException("imageIndex out of bounds!");
      } else {
         this.readMetadata();
         this.initializeFromMetadata();
      }
   }

   private void readMetadata() throws IIOException {
      if (this.stream == null) {
         throw new IllegalStateException("Input not set!");
      } else if (this.imageMetadata == null) {
         try {
            Object tagSets;
            if (this.imageReadParam instanceof TIFFImageReadParam) {
               tagSets = ((TIFFImageReadParam)this.imageReadParam).getAllowedTagSets();
            } else {
               tagSets = new ArrayList(1);
               ((List)tagSets).add(BaselineTIFFTagSet.getInstance());
            }

            this.imageMetadata = new TIFFImageMetadata((List)tagSets);
            this.imageMetadata.initializeFromStream(this.stream, this.ignoreMetadata);
         } catch (IIOException var2) {
            throw var2;
         } catch (IOException var3) {
            throw new IIOException("I/O error reading image metadata!", var3);
         }
      }
   }

   private int getWidth() {
      return this.width;
   }

   private int getHeight() {
      return this.height;
   }

   private int getNumBands() {
      return this.numBands;
   }

   private int getTileOrStripWidth() {
      TIFFField f = this.imageMetadata.getTIFFField(322);
      return f == null ? this.getWidth() : f.getAsInt(0);
   }

   private int getTileOrStripHeight() {
      TIFFField f = this.imageMetadata.getTIFFField(323);
      if (f != null) {
         return f.getAsInt(0);
      } else {
         f = this.imageMetadata.getTIFFField(278);
         int h = f == null ? -1 : f.getAsInt(0);
         return h == -1 ? this.getHeight() : h;
      }
   }

   private int getPlanarConfiguration() {
      TIFFField f = this.imageMetadata.getTIFFField(284);
      if (f == null) {
         return 1;
      } else {
         int planarConfigurationValue = f.getAsInt(0);
         if (planarConfigurationValue == 2) {
            if (this.getCompression() == 6 && this.imageMetadata.getTIFFField(513) != null) {
               this.processWarningOccurred("PlanarConfiguration \"Planar\" value inconsistent with JPEGInterchangeFormat; resetting to \"Chunky\".");
               planarConfigurationValue = 1;
            } else {
               TIFFField offsetField = this.imageMetadata.getTIFFField(324);
               int tw;
               int stripsPerImage;
               if (offsetField == null) {
                  offsetField = this.imageMetadata.getTIFFField(273);
                  tw = this.getTileOrStripWidth();
                  stripsPerImage = this.getTileOrStripHeight();
                  int tAcross = (this.getWidth() + tw - 1) / tw;
                  int tDown = (this.getHeight() + stripsPerImage - 1) / stripsPerImage;
                  int tilesPerImage = tAcross * tDown;
                  long[] offsetArray = offsetField.getAsLongs();
                  if (offsetArray != null && offsetArray.length == tilesPerImage) {
                     this.processWarningOccurred("PlanarConfiguration \"Planar\" value inconsistent with TileOffsets field value count; resetting to \"Chunky\".");
                     planarConfigurationValue = 1;
                  }
               } else {
                  tw = this.getTileOrStripHeight();
                  stripsPerImage = (this.getHeight() + tw - 1) / tw;
                  long[] offsetArray = offsetField.getAsLongs();
                  if (offsetArray != null && offsetArray.length == stripsPerImage) {
                     this.processWarningOccurred("PlanarConfiguration \"Planar\" value inconsistent with StripOffsets field value count; resetting to \"Chunky\".");
                     planarConfigurationValue = 1;
                  }
               }
            }
         }

         return planarConfigurationValue;
      }
   }

   private long getTileOrStripOffset(int tileIndex) throws IIOException {
      TIFFField f = this.imageMetadata.getTIFFField(324);
      if (f == null) {
         f = this.imageMetadata.getTIFFField(273);
      }

      if (f == null) {
         f = this.imageMetadata.getTIFFField(513);
      }

      if (f == null) {
         throw new IIOException("Missing required strip or tile offsets field.");
      } else {
         return f.getAsLong(tileIndex);
      }
   }

   private long getTileOrStripByteCount(int tileIndex) throws IOException {
      TIFFField f = this.imageMetadata.getTIFFField(325);
      if (f == null) {
         f = this.imageMetadata.getTIFFField(279);
      }

      if (f == null) {
         f = this.imageMetadata.getTIFFField(514);
      }

      long tileOrStripByteCount;
      if (f != null) {
         tileOrStripByteCount = f.getAsLong(tileIndex);
      } else {
         this.processWarningOccurred("TIFF directory contains neither StripByteCounts nor TileByteCounts field: attempting to calculate from strip or tile width and height.");
         int bitsPerPixel = this.bitsPerSample[0];

         int bytesPerRow;
         for(bytesPerRow = 1; bytesPerRow < this.samplesPerPixel; ++bytesPerRow) {
            bitsPerPixel += this.bitsPerSample[bytesPerRow];
         }

         bytesPerRow = (this.getTileOrStripWidth() * bitsPerPixel + 7) / 8;
         tileOrStripByteCount = (long)(bytesPerRow * this.getTileOrStripHeight());
         long streamLength = this.stream.length();
         if (streamLength != -1L) {
            tileOrStripByteCount = Math.min(tileOrStripByteCount, streamLength - this.getTileOrStripOffset(tileIndex));
         } else {
            this.processWarningOccurred("Stream length is unknown: cannot clamp estimated strip or tile byte count to EOF.");
         }
      }

      return tileOrStripByteCount;
   }

   private int getCompression() {
      TIFFField f = this.imageMetadata.getTIFFField(259);
      return f == null ? 1 : f.getAsInt(0);
   }

   public int getWidth(int imageIndex) throws IOException {
      this.seekToImage(imageIndex);
      return this.getWidth();
   }

   public int getHeight(int imageIndex) throws IOException {
      this.seekToImage(imageIndex);
      return this.getHeight();
   }

   private void initializeFromMetadata() {
      TIFFField f = this.imageMetadata.getTIFFField(259);
      if (f == null) {
         this.processWarningOccurred("Compression field is missing; assuming no compression");
         this.compression = 1;
      } else {
         this.compression = f.getAsInt(0);
      }

      boolean isMissingDimension = false;
      f = this.imageMetadata.getTIFFField(256);
      if (f != null) {
         this.width = f.getAsInt(0);
      } else {
         this.processWarningOccurred("ImageWidth field is missing.");
         isMissingDimension = true;
      }

      f = this.imageMetadata.getTIFFField(257);
      if (f != null) {
         this.height = f.getAsInt(0);
      } else {
         this.processWarningOccurred("ImageLength field is missing.");
         isMissingDimension = true;
      }

      f = this.imageMetadata.getTIFFField(277);
      if (f != null) {
         this.samplesPerPixel = f.getAsInt(0);
      } else {
         this.samplesPerPixel = 1;
         isMissingDimension = true;
      }

      int defaultBitDepth = 1;
      if (isMissingDimension && (f = this.imageMetadata.getTIFFField(513)) != null) {
         Iterator iter = ImageIO.getImageReadersByFormatName("JPEG");
         if (iter != null && iter.hasNext()) {
            ImageReader jreader = (ImageReader)iter.next();

            try {
               this.stream.mark();
               this.stream.seek(f.getAsLong(0));
               jreader.setInput(this.stream);
               if (this.imageMetadata.getTIFFField(256) == null) {
                  this.width = jreader.getWidth(0);
               }

               if (this.imageMetadata.getTIFFField(257) == null) {
                  this.height = jreader.getHeight(0);
               }

               ImageTypeSpecifier imageType = jreader.getRawImageType(0);
               if (this.imageMetadata.getTIFFField(277) == null) {
                  this.samplesPerPixel = imageType.getSampleModel().getNumBands();
               }

               this.stream.reset();
               defaultBitDepth = imageType.getColorModel().getComponentSize(0);
            } catch (IOException var7) {
            }

            jreader.dispose();
         }
      }

      if (this.samplesPerPixel < 1) {
         this.processWarningOccurred("Samples per pixel < 1!");
      }

      this.numBands = this.samplesPerPixel;
      this.colorMap = null;
      f = this.imageMetadata.getTIFFField(320);
      if (f != null) {
         this.colorMap = f.getAsChars();
      }

      f = this.imageMetadata.getTIFFField(262);
      if (f == null) {
         if (this.compression != 2 && this.compression != 3 && this.compression != 4) {
            if (this.colorMap != null) {
               this.photometricInterpretation = 3;
            } else if (this.samplesPerPixel != 3 && this.samplesPerPixel != 4) {
               this.processWarningOccurred("PhotometricInterpretation field is missing; assuming BlackIsZero");
               this.photometricInterpretation = 1;
            } else {
               this.photometricInterpretation = 2;
            }
         } else {
            this.processWarningOccurred("PhotometricInterpretation field is missing; assuming WhiteIsZero");
            this.photometricInterpretation = 0;
         }
      } else {
         this.photometricInterpretation = f.getAsInt(0);
      }

      boolean replicateFirst = false;
      int first = -1;
      f = this.imageMetadata.getTIFFField(339);
      this.sampleFormat = new int[this.samplesPerPixel];
      replicateFirst = false;
      if (f == null) {
         replicateFirst = true;
         first = 4;
      } else if (f.getCount() != this.samplesPerPixel) {
         replicateFirst = true;
         first = f.getAsInt(0);
      }

      int i;
      for(i = 0; i < this.samplesPerPixel; ++i) {
         this.sampleFormat[i] = replicateFirst ? first : f.getAsInt(i);
         if (this.sampleFormat[i] != 1 && this.sampleFormat[i] != 2 && this.sampleFormat[i] != 3 && this.sampleFormat[i] != 4) {
            this.processWarningOccurred("Illegal value for SAMPLE_FORMAT, assuming SAMPLE_FORMAT_UNDEFINED");
            this.sampleFormat[i] = 4;
         }
      }

      f = this.imageMetadata.getTIFFField(258);
      this.bitsPerSample = new int[this.samplesPerPixel];
      replicateFirst = false;
      if (f == null) {
         replicateFirst = true;
         first = defaultBitDepth;
      } else if (f.getCount() != this.samplesPerPixel) {
         replicateFirst = true;
         first = f.getAsInt(0);
      }

      for(i = 0; i < this.samplesPerPixel; ++i) {
         this.bitsPerSample[i] = replicateFirst ? first : f.getAsInt(i);
      }

      this.extraSamples = null;
      f = this.imageMetadata.getTIFFField(338);
      if (f != null) {
         this.extraSamples = f.getAsInts();
      }

   }

   public Iterator getImageTypes(int imageIndex) throws IIOException {
      Integer imageIndexInteger = new Integer(imageIndex);
      Object l;
      if (this.imageTypeMap.containsKey(imageIndexInteger)) {
         l = (List)this.imageTypeMap.get(imageIndexInteger);
      } else {
         l = new ArrayList(1);
         this.seekToImage(imageIndex);
         ImageTypeSpecifier itsRaw = TIFFDecompressor.getRawImageTypeSpecifier(this.photometricInterpretation, this.compression, this.samplesPerPixel, this.bitsPerSample, this.sampleFormat, this.extraSamples, this.colorMap);
         TIFFField iccProfileField = this.imageMetadata.getTIFFField(34675);
         if (iccProfileField != null && itsRaw.getColorModel() instanceof ComponentColorModel) {
            byte[] iccProfileValue = iccProfileField.getAsBytes();
            ICC_Profile iccProfile = ICC_Profile.getInstance(iccProfileValue);
            ICC_ColorSpace iccColorSpace = new ICC_ColorSpace(iccProfile);
            ColorModel cmRaw = itsRaw.getColorModel();
            ColorSpace csRaw = cmRaw.getColorSpace();
            SampleModel smRaw = itsRaw.getSampleModel();
            int numBands = smRaw.getNumBands();
            int numComponents = iccColorSpace.getNumComponents();
            if (numBands != numComponents && numBands != numComponents + 1) {
               ((List)l).add(itsRaw);
            } else {
               boolean hasAlpha = numComponents != numBands;
               boolean isAlphaPre = hasAlpha && cmRaw.isAlphaPremultiplied();
               ColorModel iccColorModel = new ComponentColorModel(iccColorSpace, cmRaw.getComponentSize(), hasAlpha, isAlphaPre, cmRaw.getTransparency(), cmRaw.getTransferType());
               ((List)l).add(new ImageTypeSpecifier(iccColorModel, smRaw));
               if (csRaw.getType() == iccColorSpace.getType() && csRaw.getNumComponents() == iccColorSpace.getNumComponents()) {
                  ((List)l).add(itsRaw);
               }
            }
         } else {
            ((List)l).add(itsRaw);
         }

         this.imageTypeMap.put(imageIndexInteger, l);
      }

      return ((List)l).iterator();
   }

   public IIOMetadata getImageMetadata(int imageIndex) throws IIOException {
      this.seekToImage(imageIndex);
      TIFFImageMetadata im = new TIFFImageMetadata(this.imageMetadata.getRootIFD().getTagSetList());
      Node root = this.imageMetadata.getAsTree("com_sun_media_imageio_plugins_tiff_image_1.0");
      im.setFromTree("com_sun_media_imageio_plugins_tiff_image_1.0", root);
      return im;
   }

   public IIOMetadata getStreamMetadata(int imageIndex) throws IIOException {
      this.readHeader();
      TIFFStreamMetadata sm = new TIFFStreamMetadata();
      Node root = sm.getAsTree("com_sun_media_imageio_plugins_tiff_stream_1.0");
      sm.setFromTree("com_sun_media_imageio_plugins_tiff_stream_1.0", root);
      return sm;
   }

   public boolean isRandomAccessEasy(int imageIndex) throws IOException {
      if (this.currIndex != -1) {
         this.seekToImage(this.currIndex);
         return this.getCompression() == 1;
      } else {
         return false;
      }
   }

   public boolean readSupportsThumbnails() {
      return false;
   }

   public boolean hasThumbnails(int imageIndex) {
      return false;
   }

   public int getNumThumbnails(int imageIndex) throws IOException {
      return 0;
   }

   public ImageReadParam getDefaultReadParam() {
      return new TIFFImageReadParam();
   }

   public boolean isImageTiled(int imageIndex) throws IOException {
      this.seekToImage(imageIndex);
      TIFFField f = this.imageMetadata.getTIFFField(322);
      return f != null;
   }

   public int getTileWidth(int imageIndex) throws IOException {
      this.seekToImage(imageIndex);
      return this.getTileOrStripWidth();
   }

   public int getTileHeight(int imageIndex) throws IOException {
      this.seekToImage(imageIndex);
      return this.getTileOrStripHeight();
   }

   public BufferedImage readTile(int imageIndex, int tileX, int tileY) throws IOException {
      int w = this.getWidth(imageIndex);
      int h = this.getHeight(imageIndex);
      int tw = this.getTileWidth(imageIndex);
      int th = this.getTileHeight(imageIndex);
      int x = tw * tileX;
      int y = th * tileY;
      if (tileX >= 0 && tileY >= 0 && x < w && y < h) {
         if (x + tw > w) {
            tw = w - x;
         }

         if (y + th > h) {
            th = h - y;
         }

         ImageReadParam param = this.getDefaultReadParam();
         Rectangle tileRect = new Rectangle(x, y, tw, th);
         param.setSourceRegion(tileRect);
         return this.read(imageIndex, param);
      } else {
         throw new IllegalArgumentException("Tile indices are out of bounds!");
      }
   }

   public boolean canReadRaster() {
      return false;
   }

   public Raster readRaster(int imageIndex, ImageReadParam param) throws IOException {
      throw new UnsupportedOperationException();
   }

   private static int ifloor(int num, int den) {
      if (num < 0) {
         num -= den - 1;
      }

      return num / den;
   }

   private static int iceil(int num, int den) {
      if (num > 0) {
         num += den - 1;
      }

      return num / den;
   }

   private void prepareRead(int imageIndex, ImageReadParam param) throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException("Input not set!");
      } else {
         if (param == null) {
            param = this.getDefaultReadParam();
         }

         this.imageReadParam = param;
         this.seekToImage(imageIndex);
         this.tileOrStripWidth = this.getTileOrStripWidth();
         this.tileOrStripHeight = this.getTileOrStripHeight();
         this.planarConfiguration = this.getPlanarConfiguration();
         this.sourceBands = param.getSourceBands();
         if (this.sourceBands == null) {
            this.sourceBands = new int[this.numBands];

            for(int i = 0; i < this.numBands; this.sourceBands[i] = i++) {
            }
         }

         Iterator imageTypes = this.getImageTypes(imageIndex);
         ImageTypeSpecifier theImageType = ImageUtil.getDestinationType(param, imageTypes);
         int destNumBands = theImageType.getSampleModel().getNumBands();
         this.destinationBands = param.getDestinationBands();
         int i;
         if (this.destinationBands == null) {
            this.destinationBands = new int[destNumBands];

            for(i = 0; i < destNumBands; this.destinationBands[i] = i++) {
            }
         }

         if (this.sourceBands.length != this.destinationBands.length) {
            throw new IllegalArgumentException("sourceBands.length != destinationBands.length");
         } else {
            i = 0;

            while(i < this.sourceBands.length) {
               int sb = this.sourceBands[i];
               if (sb >= 0 && sb < this.numBands) {
                  int db = this.destinationBands[i];
                  if (db >= 0 && db < destNumBands) {
                     ++i;
                     continue;
                  }

                  throw new IllegalArgumentException("Destination band out of range!");
               }

               throw new IllegalArgumentException("Source band out of range!");
            }

         }
      }
   }

   public RenderedImage readAsRenderedImage(int imageIndex, ImageReadParam param) throws IOException {
      this.prepareRead(imageIndex, param);
      return new TIFFRenderedImage(this, imageIndex, this.imageReadParam, this.width, this.height);
   }

   private void decodeTile(int ti, int tj, int band) throws IOException {
      Rectangle tileRect = new Rectangle(ti * this.tileOrStripWidth, tj * this.tileOrStripHeight, this.tileOrStripWidth, this.tileOrStripHeight);
      if (!this.isImageTiled(this.currIndex)) {
         tileRect = tileRect.intersection(new Rectangle(0, 0, this.width, this.height));
      }

      if (tileRect.width > 0 && tileRect.height > 0) {
         int srcMinX = tileRect.x;
         int srcMinY = tileRect.y;
         int srcWidth = tileRect.width;
         int srcHeight = tileRect.height;
         this.dstMinX = iceil(srcMinX - this.sourceXOffset, this.srcXSubsampling);
         int dstMaxX = ifloor(srcMinX + srcWidth - 1 - this.sourceXOffset, this.srcXSubsampling);
         this.dstMinY = iceil(srcMinY - this.sourceYOffset, this.srcYSubsampling);
         int dstMaxY = ifloor(srcMinY + srcHeight - 1 - this.sourceYOffset, this.srcYSubsampling);
         this.dstWidth = dstMaxX - this.dstMinX + 1;
         this.dstHeight = dstMaxY - this.dstMinY + 1;
         this.dstMinX += this.dstXOffset;
         this.dstMinY += this.dstYOffset;
         Rectangle dstRect = new Rectangle(this.dstMinX, this.dstMinY, this.dstWidth, this.dstHeight);
         dstRect = dstRect.intersection(this.theImage.getRaster().getBounds());
         this.dstMinX = dstRect.x;
         this.dstMinY = dstRect.y;
         this.dstWidth = dstRect.width;
         this.dstHeight = dstRect.height;
         if (this.dstWidth > 0 && this.dstHeight > 0) {
            int activeSrcMinX = (this.dstMinX - this.dstXOffset) * this.srcXSubsampling + this.sourceXOffset;
            int sxmax = (this.dstMinX + this.dstWidth - 1 - this.dstXOffset) * this.srcXSubsampling + this.sourceXOffset;
            int activeSrcWidth = sxmax - activeSrcMinX + 1;
            int activeSrcMinY = (this.dstMinY - this.dstYOffset) * this.srcYSubsampling + this.sourceYOffset;
            int symax = (this.dstMinY + this.dstHeight - 1 - this.dstYOffset) * this.srcYSubsampling + this.sourceYOffset;
            int activeSrcHeight = symax - activeSrcMinY + 1;
            this.decompressor.setSrcMinX(srcMinX);
            this.decompressor.setSrcMinY(srcMinY);
            this.decompressor.setSrcWidth(srcWidth);
            this.decompressor.setSrcHeight(srcHeight);
            this.decompressor.setDstMinX(this.dstMinX);
            this.decompressor.setDstMinY(this.dstMinY);
            this.decompressor.setDstWidth(this.dstWidth);
            this.decompressor.setDstHeight(this.dstHeight);
            this.decompressor.setActiveSrcMinX(activeSrcMinX);
            this.decompressor.setActiveSrcMinY(activeSrcMinY);
            this.decompressor.setActiveSrcWidth(activeSrcWidth);
            this.decompressor.setActiveSrcHeight(activeSrcHeight);
            int tileIndex = tj * this.tilesAcross + ti;
            if (this.planarConfiguration == 2) {
               tileIndex += band * this.tilesAcross * this.tilesDown;
            }

            long offset = this.getTileOrStripOffset(tileIndex);
            long byteCount = this.getTileOrStripByteCount(tileIndex);
            long streamLength = this.stream.length();
            if (streamLength > 0L && offset + byteCount > streamLength) {
               this.processWarningOccurred("Attempting to process truncated stream.");
               if (Math.max(byteCount = streamLength - offset, 0L) == 0L) {
                  this.processWarningOccurred("No bytes in strip/tile: skipping.");
                  return;
               }
            }

            this.decompressor.setStream(this.stream);
            this.decompressor.setOffset(offset);
            this.decompressor.setByteCount((int)byteCount);
            this.decompressor.beginDecoding();
            this.stream.mark();
            this.decompressor.decode();
            this.stream.reset();
         }
      }
   }

   private void reportProgress() {
      this.pixelsRead += this.dstWidth * this.dstHeight;
      this.processImageProgress(100.0F * (float)this.pixelsRead / (float)this.pixelsToRead);
      this.processImageUpdate(this.theImage, this.dstMinX, this.dstMinY, this.dstWidth, this.dstHeight, 1, 1, this.destinationBands);
   }

   public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
      this.prepareRead(imageIndex, param);
      this.theImage = ImageReader.getDestination(param, this.getImageTypes(imageIndex), this.width, this.height);
      this.srcXSubsampling = this.imageReadParam.getSourceXSubsampling();
      this.srcYSubsampling = this.imageReadParam.getSourceYSubsampling();
      Point p = this.imageReadParam.getDestinationOffset();
      this.dstXOffset = p.x;
      this.dstYOffset = p.y;
      Rectangle srcRegion = new Rectangle(0, 0, 0, 0);
      Rectangle destRegion = new Rectangle(0, 0, 0, 0);
      computeRegions(this.imageReadParam, this.width, this.height, this.theImage, srcRegion, destRegion);
      this.sourceXOffset = srcRegion.x;
      this.sourceYOffset = srcRegion.y;
      this.pixelsToRead = destRegion.width * destRegion.height;
      this.pixelsRead = 0;
      this.processImageStarted(imageIndex);
      this.processImageProgress(0.0F);
      this.tilesAcross = (this.width + this.tileOrStripWidth - 1) / this.tileOrStripWidth;
      this.tilesDown = (this.height + this.tileOrStripHeight - 1) / this.tileOrStripHeight;
      int compression = this.getCompression();
      TIFFColorConverter colorConverter = null;
      if (this.imageReadParam instanceof TIFFImageReadParam) {
         TIFFImageReadParam tparam = (TIFFImageReadParam)this.imageReadParam;
         this.decompressor = tparam.getTIFFDecompressor();
         colorConverter = tparam.getColorConverter();
      }

      int predictor;
      if (this.decompressor == null) {
         TIFFField JPEGProcField;
         if (compression == 1) {
            JPEGProcField = this.imageMetadata.getTIFFField(266);
            if (JPEGProcField != null && JPEGProcField.getAsInt(0) == 2) {
               this.decompressor = new TIFFLSBDecompressor();
            } else {
               this.decompressor = new TIFFNullDecompressor();
            }
         } else if (compression == 4) {
            if (this.decompressor == null) {
               this.decompressor = new TIFFFaxDecompressor();
            }
         } else if (compression == 3) {
            if (this.decompressor == null) {
               this.decompressor = new TIFFFaxDecompressor();
            }
         } else if (compression == 2) {
            this.decompressor = new TIFFFaxDecompressor();
         } else if (compression == 32773) {
            this.decompressor = new TIFFPackBitsDecompressor();
         } else if (compression == 5) {
            JPEGProcField = this.imageMetadata.getTIFFField(317);
            predictor = JPEGProcField == null ? 1 : JPEGProcField.getAsInt(0);
            this.decompressor = new TIFFLZWDecompressor(predictor);
         } else if (compression == 7) {
            this.decompressor = new TIFFJPEGDecompressor();
         } else if (compression != 8 && compression != 32946) {
            if (compression != 6) {
               throw new IIOException("Unsupported compression type (tag number = " + compression + ")!");
            }

            JPEGProcField = this.imageMetadata.getTIFFField(512);
            if (JPEGProcField == null) {
               this.processWarningOccurred("JPEGProc field missing; assuming baseline sequential JPEG process.");
            } else if (JPEGProcField.getAsInt(0) != 1) {
               throw new IIOException("Old-style JPEG supported for baseline sequential JPEG process only!");
            }

            this.decompressor = new TIFFOldJPEGDecompressor();
         } else {
            JPEGProcField = this.imageMetadata.getTIFFField(317);
            predictor = JPEGProcField == null ? 1 : JPEGProcField.getAsInt(0);
            this.decompressor = new TIFFDeflateDecompressor(predictor);
         }

         if (this.photometricInterpretation == 6 && compression != 7 && compression != 6) {
            boolean convertYCbCrToRGB = this.theImage.getColorModel().getColorSpace().getType() == 5;
            TIFFDecompressor wrappedDecompressor = this.decompressor instanceof TIFFNullDecompressor ? null : this.decompressor;
            this.decompressor = new TIFFYCbCrDecompressor(wrappedDecompressor, convertYCbCrToRGB);
         }
      }

      if (colorConverter == null) {
         if (this.photometricInterpretation == 8 && this.theImage.getColorModel().getColorSpace().getType() == 5) {
            colorConverter = new TIFFCIELabColorConverter();
         } else if (this.photometricInterpretation == 6 && !(this.decompressor instanceof TIFFYCbCrDecompressor) && compression != 7 && compression != 6) {
            colorConverter = new TIFFYCbCrColorConverter(this.imageMetadata);
         }
      }

      this.decompressor.setReader(this);
      this.decompressor.setMetadata(this.imageMetadata);
      this.decompressor.setImage(this.theImage);
      this.decompressor.setPhotometricInterpretation(this.photometricInterpretation);
      this.decompressor.setCompression(compression);
      this.decompressor.setSamplesPerPixel(this.samplesPerPixel);
      this.decompressor.setBitsPerSample(this.bitsPerSample);
      this.decompressor.setSampleFormat(this.sampleFormat);
      this.decompressor.setExtraSamples(this.extraSamples);
      this.decompressor.setColorMap(this.colorMap);
      this.decompressor.setColorConverter((TIFFColorConverter)colorConverter);
      this.decompressor.setSourceXOffset(this.sourceXOffset);
      this.decompressor.setSourceYOffset(this.sourceYOffset);
      this.decompressor.setSubsampleX(this.srcXSubsampling);
      this.decompressor.setSubsampleY(this.srcYSubsampling);
      this.decompressor.setDstXOffset(this.dstXOffset);
      this.decompressor.setDstYOffset(this.dstYOffset);
      this.decompressor.setSourceBands(this.sourceBands);
      this.decompressor.setDestinationBands(this.destinationBands);
      int minTileX = TIFFImageWriter.XToTileX(srcRegion.x, 0, this.tileOrStripWidth);
      predictor = TIFFImageWriter.YToTileY(srcRegion.y, 0, this.tileOrStripHeight);
      int maxTileX = TIFFImageWriter.XToTileX(srcRegion.x + srcRegion.width - 1, 0, this.tileOrStripWidth);
      int maxTileY = TIFFImageWriter.YToTileY(srcRegion.y + srcRegion.height - 1, 0, this.tileOrStripHeight);
      boolean isAbortRequested = false;
      if (this.planarConfiguration == 2) {
         this.decompressor.setPlanar(true);
         int[] sb = new int[1];
         int[] db = new int[1];

         for(int tj = predictor; tj <= maxTileY; ++tj) {
            for(int ti = minTileX; ti <= maxTileX; ++ti) {
               for(int band = 0; band < this.numBands; ++band) {
                  sb[0] = this.sourceBands[band];
                  this.decompressor.setSourceBands(sb);
                  db[0] = this.destinationBands[band];
                  this.decompressor.setDestinationBands(db);
                  if (this.abortRequested()) {
                     isAbortRequested = true;
                     break;
                  }

                  this.decodeTile(ti, tj, band);
               }

               if (isAbortRequested) {
                  break;
               }

               this.reportProgress();
            }

            if (isAbortRequested) {
               break;
            }
         }
      } else {
         for(int tj = predictor; tj <= maxTileY; ++tj) {
            for(int ti = minTileX; ti <= maxTileX; ++ti) {
               if (this.abortRequested()) {
                  isAbortRequested = true;
                  break;
               }

               this.decodeTile(ti, tj, -1);
               this.reportProgress();
            }

            if (isAbortRequested) {
               break;
            }
         }
      }

      if (isAbortRequested) {
         this.processReadAborted();
      } else {
         this.processImageComplete();
      }

      return this.theImage;
   }

   public void reset() {
      super.reset();
      this.resetLocal();
   }

   protected void resetLocal() {
      this.stream = null;
      this.gotHeader = false;
      this.imageReadParam = this.getDefaultReadParam();
      this.streamMetadata = null;
      this.currIndex = -1;
      this.imageMetadata = null;
      this.imageStartPosition = new ArrayList();
      this.numImages = -1;
      this.imageTypeMap = new HashMap();
      this.width = -1;
      this.height = -1;
      this.numBands = -1;
      this.tileOrStripWidth = -1;
      this.tileOrStripHeight = -1;
      this.planarConfiguration = 1;
      this.rowsDone = 0;
   }

   void forwardWarningMessage(String warning) {
      this.processWarningOccurred(warning);
   }

   protected static BufferedImage getDestination(ImageReadParam param, Iterator imageTypes, int width, int height) throws IIOException {
      if (imageTypes != null && imageTypes.hasNext()) {
         BufferedImage dest = null;
         ImageTypeSpecifier imageType = null;
         if (param != null) {
            dest = param.getDestination();
            if (dest != null) {
               return dest;
            }

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

         Rectangle srcRegion = new Rectangle(0, 0, 0, 0);
         Rectangle destRegion = new Rectangle(0, 0, 0, 0);
         computeRegions(param, width, height, (BufferedImage)null, srcRegion, destRegion);
         int destWidth = destRegion.x + destRegion.width;
         int destHeight = destRegion.y + destRegion.height;
         if ((long)destWidth * (long)destHeight > 2147483647L) {
            throw new IllegalArgumentException("width*height > Integer.MAX_VALUE!");
         } else {
            return imageType.createBufferedImage(destWidth, destHeight);
         }
      } else {
         throw new IllegalArgumentException("imageTypes null or empty!");
      }
   }
}
