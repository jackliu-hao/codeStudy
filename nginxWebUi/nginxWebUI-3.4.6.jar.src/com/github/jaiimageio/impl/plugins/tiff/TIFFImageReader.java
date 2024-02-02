/*      */ package com.github.jaiimageio.impl.plugins.tiff;
/*      */ 
/*      */ import com.github.jaiimageio.impl.common.ImageUtil;
/*      */ import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFColorConverter;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFImageReadParam;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.color.ColorSpace;
/*      */ import java.awt.color.ICC_ColorSpace;
/*      */ import java.awt.color.ICC_Profile;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ComponentColorModel;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.RenderedImage;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.io.IOException;
/*      */ import java.nio.ByteOrder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import javax.imageio.IIOException;
/*      */ import javax.imageio.ImageIO;
/*      */ import javax.imageio.ImageReadParam;
/*      */ import javax.imageio.ImageReader;
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ import javax.imageio.metadata.IIOMetadata;
/*      */ import javax.imageio.spi.ImageReaderSpi;
/*      */ import javax.imageio.stream.ImageInputStream;
/*      */ import org.w3c.dom.Node;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TIFFImageReader
/*      */   extends ImageReader
/*      */ {
/*      */   private static final boolean DEBUG = false;
/*   88 */   ImageInputStream stream = null;
/*      */ 
/*      */   
/*      */   boolean gotHeader = false;
/*      */   
/*   93 */   ImageReadParam imageReadParam = getDefaultReadParam();
/*      */ 
/*      */   
/*   96 */   TIFFStreamMetadata streamMetadata = null;
/*      */ 
/*      */   
/*   99 */   int currIndex = -1;
/*      */ 
/*      */   
/*  102 */   TIFFImageMetadata imageMetadata = null;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  107 */   List imageStartPosition = new ArrayList();
/*      */ 
/*      */   
/*  110 */   int numImages = -1;
/*      */ 
/*      */ 
/*      */   
/*  114 */   HashMap imageTypeMap = new HashMap<Object, Object>();
/*      */   
/*  116 */   BufferedImage theImage = null;
/*      */   
/*  118 */   int width = -1;
/*  119 */   int height = -1;
/*  120 */   int numBands = -1;
/*  121 */   int tileOrStripWidth = -1, tileOrStripHeight = -1;
/*      */   
/*  123 */   int planarConfiguration = 1;
/*      */   
/*  125 */   int rowsDone = 0;
/*      */   
/*      */   int compression;
/*      */   
/*      */   int photometricInterpretation;
/*      */   int samplesPerPixel;
/*      */   int[] sampleFormat;
/*      */   int[] bitsPerSample;
/*      */   int[] extraSamples;
/*      */   char[] colorMap;
/*      */   int sourceXOffset;
/*      */   int sourceYOffset;
/*      */   int srcXSubsampling;
/*      */   int srcYSubsampling;
/*      */   int dstWidth;
/*      */   int dstHeight;
/*      */   int dstMinX;
/*      */   int dstMinY;
/*      */   int dstXOffset;
/*      */   int dstYOffset;
/*      */   int tilesAcross;
/*      */   int tilesDown;
/*      */   int pixelsRead;
/*      */   int pixelsToRead;
/*      */   private int[] sourceBands;
/*      */   private int[] destinationBands;
/*      */   private TIFFDecompressor decompressor;
/*      */   
/*      */   public TIFFImageReader(ImageReaderSpi originatingProvider) {
/*  154 */     super(originatingProvider);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
/*  160 */     super.setInput(input, seekForwardOnly, ignoreMetadata);
/*      */ 
/*      */     
/*  163 */     resetLocal();
/*      */     
/*  165 */     if (input != null) {
/*  166 */       if (!(input instanceof ImageInputStream)) {
/*  167 */         throw new IllegalArgumentException("input not an ImageInputStream!");
/*      */       }
/*      */       
/*  170 */       this.stream = (ImageInputStream)input;
/*      */     } else {
/*  172 */       this.stream = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void readHeader() throws IIOException {
/*  179 */     if (this.gotHeader) {
/*      */       return;
/*      */     }
/*  182 */     if (this.stream == null) {
/*  183 */       throw new IllegalStateException("Input not set!");
/*      */     }
/*      */ 
/*      */     
/*  187 */     this.streamMetadata = new TIFFStreamMetadata();
/*      */     
/*      */     try {
/*  190 */       int byteOrder = this.stream.readUnsignedShort();
/*  191 */       if (byteOrder == 19789) {
/*  192 */         this.streamMetadata.byteOrder = ByteOrder.BIG_ENDIAN;
/*  193 */         this.stream.setByteOrder(ByteOrder.BIG_ENDIAN);
/*  194 */       } else if (byteOrder == 18761) {
/*  195 */         this.streamMetadata.byteOrder = ByteOrder.LITTLE_ENDIAN;
/*  196 */         this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
/*      */       } else {
/*  198 */         processWarningOccurred("Bad byte order in header, assuming little-endian");
/*      */         
/*  200 */         this.streamMetadata.byteOrder = ByteOrder.LITTLE_ENDIAN;
/*  201 */         this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
/*      */       } 
/*      */       
/*  204 */       int magic = this.stream.readUnsignedShort();
/*  205 */       if (magic != 42) {
/*  206 */         processWarningOccurred("Bad magic number in header, continuing");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  211 */       long offset = this.stream.readUnsignedInt();
/*  212 */       this.imageStartPosition.add(new Long(offset));
/*  213 */       this.stream.seek(offset);
/*  214 */     } catch (IOException e) {
/*  215 */       throw new IIOException("I/O error reading header!", e);
/*      */     } 
/*      */     
/*  218 */     this.gotHeader = true;
/*      */   }
/*      */   
/*      */   private int locateImage(int imageIndex) throws IIOException {
/*  222 */     readHeader();
/*      */ 
/*      */     
/*      */     try {
/*  226 */       int index = Math.min(imageIndex, this.imageStartPosition.size() - 1);
/*      */ 
/*      */       
/*  229 */       Long l = this.imageStartPosition.get(index);
/*  230 */       this.stream.seek(l.longValue());
/*      */ 
/*      */       
/*  233 */       while (index < imageIndex) {
/*  234 */         int count = this.stream.readUnsignedShort();
/*  235 */         this.stream.skipBytes(12 * count);
/*      */         
/*  237 */         long offset = this.stream.readUnsignedInt();
/*  238 */         if (offset == 0L) {
/*  239 */           return index;
/*      */         }
/*      */         
/*  242 */         this.imageStartPosition.add(new Long(offset));
/*  243 */         this.stream.seek(offset);
/*  244 */         index++;
/*      */       } 
/*  246 */     } catch (IOException e) {
/*  247 */       throw new IIOException("Couldn't seek!", e);
/*      */     } 
/*      */     
/*  250 */     if (this.currIndex != imageIndex) {
/*  251 */       this.imageMetadata = null;
/*      */     }
/*  253 */     this.currIndex = imageIndex;
/*  254 */     return imageIndex;
/*      */   }
/*      */   
/*      */   public int getNumImages(boolean allowSearch) throws IOException {
/*  258 */     if (this.stream == null) {
/*  259 */       throw new IllegalStateException("Input not set!");
/*      */     }
/*  261 */     if (this.seekForwardOnly && allowSearch) {
/*  262 */       throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!");
/*      */     }
/*      */ 
/*      */     
/*  266 */     if (this.numImages > 0) {
/*  267 */       return this.numImages;
/*      */     }
/*  269 */     if (allowSearch) {
/*  270 */       this.numImages = locateImage(2147483647) + 1;
/*      */     }
/*  272 */     return this.numImages;
/*      */   }
/*      */   
/*      */   public IIOMetadata getStreamMetadata() throws IIOException {
/*  276 */     readHeader();
/*  277 */     return this.streamMetadata;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkIndex(int imageIndex) {
/*  283 */     if (imageIndex < this.minIndex) {
/*  284 */       throw new IndexOutOfBoundsException("imageIndex < minIndex!");
/*      */     }
/*  286 */     if (this.seekForwardOnly) {
/*  287 */       this.minIndex = imageIndex;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void seekToImage(int imageIndex) throws IIOException {
/*  294 */     checkIndex(imageIndex);
/*      */     
/*  296 */     int index = locateImage(imageIndex);
/*  297 */     if (index != imageIndex) {
/*  298 */       throw new IndexOutOfBoundsException("imageIndex out of bounds!");
/*      */     }
/*      */     
/*  301 */     readMetadata();
/*      */     
/*  303 */     initializeFromMetadata();
/*      */   }
/*      */ 
/*      */   
/*      */   private void readMetadata() throws IIOException {
/*  308 */     if (this.stream == null) {
/*  309 */       throw new IllegalStateException("Input not set!");
/*      */     }
/*      */     
/*  312 */     if (this.imageMetadata != null) {
/*      */       return;
/*      */     }
/*      */     
/*      */     try {
/*      */       List<BaselineTIFFTagSet> tagSets;
/*  318 */       if (this.imageReadParam instanceof TIFFImageReadParam) {
/*      */         
/*  320 */         tagSets = ((TIFFImageReadParam)this.imageReadParam).getAllowedTagSets();
/*      */       } else {
/*  322 */         tagSets = new ArrayList(1);
/*  323 */         tagSets.add(BaselineTIFFTagSet.getInstance());
/*      */       } 
/*      */       
/*  326 */       this.imageMetadata = new TIFFImageMetadata(tagSets);
/*  327 */       this.imageMetadata.initializeFromStream(this.stream, this.ignoreMetadata);
/*  328 */     } catch (IIOException iioe) {
/*  329 */       throw iioe;
/*  330 */     } catch (IOException ioe) {
/*  331 */       throw new IIOException("I/O error reading image metadata!", ioe);
/*      */     } 
/*      */   }
/*      */   
/*      */   private int getWidth() {
/*  336 */     return this.width;
/*      */   }
/*      */   
/*      */   private int getHeight() {
/*  340 */     return this.height;
/*      */   }
/*      */   
/*      */   private int getNumBands() {
/*  344 */     return this.numBands;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private int getTileOrStripWidth() {
/*  350 */     TIFFField f = this.imageMetadata.getTIFFField(322);
/*  351 */     return (f == null) ? getWidth() : f.getAsInt(0);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private int getTileOrStripHeight() {
/*  357 */     TIFFField f = this.imageMetadata.getTIFFField(323);
/*  358 */     if (f != null) {
/*  359 */       return f.getAsInt(0);
/*      */     }
/*      */     
/*  362 */     f = this.imageMetadata.getTIFFField(278);
/*      */     
/*  364 */     int h = (f == null) ? -1 : f.getAsInt(0);
/*  365 */     return (h == -1) ? getHeight() : h;
/*      */   }
/*      */ 
/*      */   
/*      */   private int getPlanarConfiguration() {
/*  370 */     TIFFField f = this.imageMetadata.getTIFFField(284);
/*  371 */     if (f != null) {
/*  372 */       int planarConfigurationValue = f.getAsInt(0);
/*  373 */       if (planarConfigurationValue == 2)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  379 */         if (getCompression() == 6 && this.imageMetadata
/*      */           
/*  381 */           .getTIFFField(513) != null) {
/*      */ 
/*      */ 
/*      */           
/*  385 */           processWarningOccurred("PlanarConfiguration \"Planar\" value inconsistent with JPEGInterchangeFormat; resetting to \"Chunky\".");
/*  386 */           planarConfigurationValue = 1;
/*      */         }
/*      */         else {
/*      */           
/*  390 */           TIFFField offsetField = this.imageMetadata.getTIFFField(324);
/*  391 */           if (offsetField == null) {
/*      */ 
/*      */             
/*  394 */             offsetField = this.imageMetadata.getTIFFField(273);
/*  395 */             int tw = getTileOrStripWidth();
/*  396 */             int th = getTileOrStripHeight();
/*  397 */             int tAcross = (getWidth() + tw - 1) / tw;
/*  398 */             int tDown = (getHeight() + th - 1) / th;
/*  399 */             int tilesPerImage = tAcross * tDown;
/*  400 */             long[] offsetArray = offsetField.getAsLongs();
/*  401 */             if (offsetArray != null && offsetArray.length == tilesPerImage)
/*      */             {
/*      */ 
/*      */ 
/*      */               
/*  406 */               processWarningOccurred("PlanarConfiguration \"Planar\" value inconsistent with TileOffsets field value count; resetting to \"Chunky\".");
/*  407 */               planarConfigurationValue = 1;
/*      */             }
/*      */           
/*      */           } else {
/*      */             
/*  412 */             int rowsPerStrip = getTileOrStripHeight();
/*      */             
/*  414 */             int stripsPerImage = (getHeight() + rowsPerStrip - 1) / rowsPerStrip;
/*  415 */             long[] offsetArray = offsetField.getAsLongs();
/*  416 */             if (offsetArray != null && offsetArray.length == stripsPerImage) {
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  421 */               processWarningOccurred("PlanarConfiguration \"Planar\" value inconsistent with StripOffsets field value count; resetting to \"Chunky\".");
/*  422 */               planarConfigurationValue = 1;
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       }
/*      */       
/*  428 */       return planarConfigurationValue;
/*      */     } 
/*      */     
/*  431 */     return 1;
/*      */   }
/*      */ 
/*      */   
/*      */   private long getTileOrStripOffset(int tileIndex) throws IIOException {
/*  436 */     TIFFField f = this.imageMetadata.getTIFFField(324);
/*  437 */     if (f == null) {
/*  438 */       f = this.imageMetadata.getTIFFField(273);
/*      */     }
/*  440 */     if (f == null) {
/*  441 */       f = this.imageMetadata.getTIFFField(513);
/*      */     }
/*      */     
/*  444 */     if (f == null) {
/*  445 */       throw new IIOException("Missing required strip or tile offsets field.");
/*      */     }
/*      */ 
/*      */     
/*  449 */     return f.getAsLong(tileIndex);
/*      */   }
/*      */   
/*      */   private long getTileOrStripByteCount(int tileIndex) throws IOException {
/*      */     long tileOrStripByteCount;
/*  454 */     TIFFField f = this.imageMetadata.getTIFFField(325);
/*  455 */     if (f == null)
/*      */     {
/*  457 */       f = this.imageMetadata.getTIFFField(279);
/*      */     }
/*  459 */     if (f == null) {
/*  460 */       f = this.imageMetadata.getTIFFField(514);
/*      */     }
/*      */ 
/*      */     
/*  464 */     if (f != null) {
/*  465 */       tileOrStripByteCount = f.getAsLong(tileIndex);
/*      */     } else {
/*  467 */       processWarningOccurred("TIFF directory contains neither StripByteCounts nor TileByteCounts field: attempting to calculate from strip or tile width and height.");
/*      */ 
/*      */ 
/*      */       
/*  471 */       int bitsPerPixel = this.bitsPerSample[0];
/*  472 */       for (int i = 1; i < this.samplesPerPixel; i++) {
/*  473 */         bitsPerPixel += this.bitsPerSample[i];
/*      */       }
/*  475 */       int bytesPerRow = (getTileOrStripWidth() * bitsPerPixel + 7) / 8;
/*  476 */       tileOrStripByteCount = (bytesPerRow * getTileOrStripHeight());
/*      */ 
/*      */       
/*  479 */       long streamLength = this.stream.length();
/*  480 */       if (streamLength != -1L) {
/*      */         
/*  482 */         tileOrStripByteCount = Math.min(tileOrStripByteCount, streamLength - 
/*  483 */             getTileOrStripOffset(tileIndex));
/*      */       } else {
/*  485 */         processWarningOccurred("Stream length is unknown: cannot clamp estimated strip or tile byte count to EOF.");
/*      */       } 
/*      */     } 
/*      */     
/*  489 */     return tileOrStripByteCount;
/*      */   }
/*      */ 
/*      */   
/*      */   private int getCompression() {
/*  494 */     TIFFField f = this.imageMetadata.getTIFFField(259);
/*  495 */     if (f == null) {
/*  496 */       return 1;
/*      */     }
/*  498 */     return f.getAsInt(0);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getWidth(int imageIndex) throws IOException {
/*  503 */     seekToImage(imageIndex);
/*  504 */     return getWidth();
/*      */   }
/*      */   
/*      */   public int getHeight(int imageIndex) throws IOException {
/*  508 */     seekToImage(imageIndex);
/*  509 */     return getHeight();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initializeFromMetadata() {
/*  533 */     TIFFField f = this.imageMetadata.getTIFFField(259);
/*  534 */     if (f == null) {
/*      */       
/*  536 */       processWarningOccurred("Compression field is missing; assuming no compression");
/*  537 */       this.compression = 1;
/*      */     } else {
/*  539 */       this.compression = f.getAsInt(0);
/*      */     } 
/*      */ 
/*      */     
/*  543 */     boolean isMissingDimension = false;
/*      */ 
/*      */     
/*  546 */     f = this.imageMetadata.getTIFFField(256);
/*  547 */     if (f != null) {
/*  548 */       this.width = f.getAsInt(0);
/*      */     } else {
/*  550 */       processWarningOccurred("ImageWidth field is missing.");
/*  551 */       isMissingDimension = true;
/*      */     } 
/*      */ 
/*      */     
/*  555 */     f = this.imageMetadata.getTIFFField(257);
/*  556 */     if (f != null) {
/*  557 */       this.height = f.getAsInt(0);
/*      */     } else {
/*  559 */       processWarningOccurred("ImageLength field is missing.");
/*  560 */       isMissingDimension = true;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  565 */     f = this.imageMetadata.getTIFFField(277);
/*  566 */     if (f != null) {
/*  567 */       this.samplesPerPixel = f.getAsInt(0);
/*      */     } else {
/*  569 */       this.samplesPerPixel = 1;
/*  570 */       isMissingDimension = true;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  575 */     int defaultBitDepth = 1;
/*  576 */     if (isMissingDimension && (
/*  577 */       f = this.imageMetadata.getTIFFField(513)) != null) {
/*  578 */       Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("JPEG");
/*  579 */       if (iter != null && iter.hasNext()) {
/*  580 */         ImageReader jreader = iter.next();
/*      */         try {
/*  582 */           this.stream.mark();
/*  583 */           this.stream.seek(f.getAsLong(0));
/*  584 */           jreader.setInput(this.stream);
/*  585 */           if (this.imageMetadata.getTIFFField(256) == null) {
/*  586 */             this.width = jreader.getWidth(0);
/*      */           }
/*  588 */           if (this.imageMetadata.getTIFFField(257) == null) {
/*  589 */             this.height = jreader.getHeight(0);
/*      */           }
/*  591 */           ImageTypeSpecifier imageType = jreader.getRawImageType(0);
/*  592 */           if (this.imageMetadata.getTIFFField(277) == null) {
/*  593 */             this
/*  594 */               .samplesPerPixel = imageType.getSampleModel().getNumBands();
/*      */           }
/*  596 */           this.stream.reset();
/*      */           
/*  598 */           defaultBitDepth = imageType.getColorModel().getComponentSize(0);
/*  599 */         } catch (IOException iOException) {}
/*      */ 
/*      */         
/*  602 */         jreader.dispose();
/*      */       } 
/*      */     } 
/*      */     
/*  606 */     if (this.samplesPerPixel < 1) {
/*  607 */       processWarningOccurred("Samples per pixel < 1!");
/*      */     }
/*      */ 
/*      */     
/*  611 */     this.numBands = this.samplesPerPixel;
/*      */ 
/*      */     
/*  614 */     this.colorMap = null;
/*  615 */     f = this.imageMetadata.getTIFFField(320);
/*  616 */     if (f != null)
/*      */     {
/*  618 */       this.colorMap = f.getAsChars();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  623 */     f = this.imageMetadata.getTIFFField(262);
/*  624 */     if (f == null) {
/*  625 */       if (this.compression == 2 || this.compression == 3 || this.compression == 4) {
/*      */ 
/*      */ 
/*      */         
/*  629 */         processWarningOccurred("PhotometricInterpretation field is missing; assuming WhiteIsZero");
/*      */         
/*  631 */         this.photometricInterpretation = 0;
/*      */       }
/*  633 */       else if (this.colorMap != null) {
/*  634 */         this.photometricInterpretation = 3;
/*      */       }
/*  636 */       else if (this.samplesPerPixel == 3 || this.samplesPerPixel == 4) {
/*  637 */         this.photometricInterpretation = 2;
/*      */       }
/*      */       else {
/*      */         
/*  641 */         processWarningOccurred("PhotometricInterpretation field is missing; assuming BlackIsZero");
/*      */         
/*  643 */         this.photometricInterpretation = 1;
/*      */       } 
/*      */     } else {
/*      */       
/*  647 */       this.photometricInterpretation = f.getAsInt(0);
/*      */     } 
/*      */ 
/*      */     
/*  651 */     boolean replicateFirst = false;
/*  652 */     int first = -1;
/*      */     
/*  654 */     f = this.imageMetadata.getTIFFField(339);
/*  655 */     this.sampleFormat = new int[this.samplesPerPixel];
/*  656 */     replicateFirst = false;
/*  657 */     if (f == null) {
/*  658 */       replicateFirst = true;
/*  659 */       first = 4;
/*  660 */     } else if (f.getCount() != this.samplesPerPixel) {
/*  661 */       replicateFirst = true;
/*  662 */       first = f.getAsInt(0);
/*      */     } 
/*      */     int i;
/*  665 */     for (i = 0; i < this.samplesPerPixel; i++) {
/*  666 */       this.sampleFormat[i] = replicateFirst ? first : f.getAsInt(i);
/*  667 */       if (this.sampleFormat[i] != 1 && this.sampleFormat[i] != 2 && this.sampleFormat[i] != 3 && this.sampleFormat[i] != 4) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  675 */         processWarningOccurred("Illegal value for SAMPLE_FORMAT, assuming SAMPLE_FORMAT_UNDEFINED");
/*      */         
/*  677 */         this.sampleFormat[i] = 4;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  682 */     f = this.imageMetadata.getTIFFField(258);
/*  683 */     this.bitsPerSample = new int[this.samplesPerPixel];
/*  684 */     replicateFirst = false;
/*  685 */     if (f == null) {
/*  686 */       replicateFirst = true;
/*  687 */       first = defaultBitDepth;
/*  688 */     } else if (f.getCount() != this.samplesPerPixel) {
/*  689 */       replicateFirst = true;
/*  690 */       first = f.getAsInt(0);
/*      */     } 
/*      */     
/*  693 */     for (i = 0; i < this.samplesPerPixel; i++)
/*      */     {
/*  695 */       this.bitsPerSample[i] = replicateFirst ? first : f.getAsInt(i);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  704 */     this.extraSamples = null;
/*  705 */     f = this.imageMetadata.getTIFFField(338);
/*  706 */     if (f != null) {
/*  707 */       this.extraSamples = f.getAsInts();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Iterator getImageTypes(int imageIndex) throws IIOException {
/*      */     List<ImageTypeSpecifier> l;
/*  722 */     Integer imageIndexInteger = new Integer(imageIndex);
/*  723 */     if (this.imageTypeMap.containsKey(imageIndexInteger)) {
/*      */       
/*  725 */       l = (List)this.imageTypeMap.get(imageIndexInteger);
/*      */     } else {
/*      */       
/*  728 */       l = new ArrayList(1);
/*      */ 
/*      */ 
/*      */       
/*  732 */       seekToImage(imageIndex);
/*      */ 
/*      */       
/*  735 */       ImageTypeSpecifier itsRaw = TIFFDecompressor.getRawImageTypeSpecifier(this.photometricInterpretation, this.compression, this.samplesPerPixel, this.bitsPerSample, this.sampleFormat, this.extraSamples, this.colorMap);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  745 */       TIFFField iccProfileField = this.imageMetadata.getTIFFField(34675);
/*      */ 
/*      */ 
/*      */       
/*  749 */       if (iccProfileField != null && itsRaw
/*  750 */         .getColorModel() instanceof ComponentColorModel) {
/*      */         
/*  752 */         byte[] iccProfileValue = iccProfileField.getAsBytes();
/*      */         
/*  754 */         ICC_Profile iccProfile = ICC_Profile.getInstance(iccProfileValue);
/*  755 */         ICC_ColorSpace iccColorSpace = new ICC_ColorSpace(iccProfile);
/*      */ 
/*      */ 
/*      */         
/*  759 */         ColorModel cmRaw = itsRaw.getColorModel();
/*  760 */         ColorSpace csRaw = cmRaw.getColorSpace();
/*  761 */         SampleModel smRaw = itsRaw.getSampleModel();
/*      */ 
/*      */ 
/*      */         
/*  765 */         int numBands = smRaw.getNumBands();
/*  766 */         int numComponents = iccColorSpace.getNumComponents();
/*      */ 
/*      */ 
/*      */         
/*  770 */         if (numBands == numComponents || numBands == numComponents + 1) {
/*      */ 
/*      */           
/*  773 */           boolean hasAlpha = (numComponents != numBands);
/*      */           
/*  775 */           boolean isAlphaPre = (hasAlpha && cmRaw.isAlphaPremultiplied());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  785 */           ColorModel iccColorModel = new ComponentColorModel(iccColorSpace, cmRaw.getComponentSize(), hasAlpha, isAlphaPre, cmRaw.getTransparency(), cmRaw.getTransferType());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  792 */           l.add(new ImageTypeSpecifier(iccColorModel, smRaw));
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  797 */           if (csRaw.getType() == iccColorSpace.getType() && csRaw
/*  798 */             .getNumComponents() == iccColorSpace
/*  799 */             .getNumComponents()) {
/*  800 */             l.add(itsRaw);
/*      */           }
/*      */         } else {
/*      */           
/*  804 */           l.add(itsRaw);
/*      */         } 
/*      */       } else {
/*      */         
/*  808 */         l.add(itsRaw);
/*      */       } 
/*      */ 
/*      */       
/*  812 */       this.imageTypeMap.put(imageIndexInteger, l);
/*      */     } 
/*      */     
/*  815 */     return l.iterator();
/*      */   }
/*      */   
/*      */   public IIOMetadata getImageMetadata(int imageIndex) throws IIOException {
/*  819 */     seekToImage(imageIndex);
/*      */     
/*  821 */     TIFFImageMetadata im = new TIFFImageMetadata(this.imageMetadata.getRootIFD().getTagSetList());
/*      */     
/*  823 */     Node root = this.imageMetadata.getAsTree("com_sun_media_imageio_plugins_tiff_image_1.0");
/*  824 */     im.setFromTree("com_sun_media_imageio_plugins_tiff_image_1.0", root);
/*  825 */     return im;
/*      */   }
/*      */   
/*      */   public IIOMetadata getStreamMetadata(int imageIndex) throws IIOException {
/*  829 */     readHeader();
/*  830 */     TIFFStreamMetadata sm = new TIFFStreamMetadata();
/*  831 */     Node root = sm.getAsTree("com_sun_media_imageio_plugins_tiff_stream_1.0");
/*  832 */     sm.setFromTree("com_sun_media_imageio_plugins_tiff_stream_1.0", root);
/*  833 */     return sm;
/*      */   }
/*      */   
/*      */   public boolean isRandomAccessEasy(int imageIndex) throws IOException {
/*  837 */     if (this.currIndex != -1) {
/*  838 */       seekToImage(this.currIndex);
/*  839 */       return (getCompression() == 1);
/*      */     } 
/*  841 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean readSupportsThumbnails() {
/*  848 */     return false;
/*      */   }
/*      */   
/*      */   public boolean hasThumbnails(int imageIndex) {
/*  852 */     return false;
/*      */   }
/*      */   
/*      */   public int getNumThumbnails(int imageIndex) throws IOException {
/*  856 */     return 0;
/*      */   }
/*      */   
/*      */   public ImageReadParam getDefaultReadParam() {
/*  860 */     return (ImageReadParam)new TIFFImageReadParam();
/*      */   }
/*      */   
/*      */   public boolean isImageTiled(int imageIndex) throws IOException {
/*  864 */     seekToImage(imageIndex);
/*      */ 
/*      */     
/*  867 */     TIFFField f = this.imageMetadata.getTIFFField(322);
/*  868 */     return (f != null);
/*      */   }
/*      */   
/*      */   public int getTileWidth(int imageIndex) throws IOException {
/*  872 */     seekToImage(imageIndex);
/*  873 */     return getTileOrStripWidth();
/*      */   }
/*      */   
/*      */   public int getTileHeight(int imageIndex) throws IOException {
/*  877 */     seekToImage(imageIndex);
/*  878 */     return getTileOrStripHeight();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public BufferedImage readTile(int imageIndex, int tileX, int tileY) throws IOException {
/*  884 */     int w = getWidth(imageIndex);
/*  885 */     int h = getHeight(imageIndex);
/*  886 */     int tw = getTileWidth(imageIndex);
/*  887 */     int th = getTileHeight(imageIndex);
/*      */     
/*  889 */     int x = tw * tileX;
/*  890 */     int y = th * tileY;
/*      */     
/*  892 */     if (tileX < 0 || tileY < 0 || x >= w || y >= h) {
/*  893 */       throw new IllegalArgumentException("Tile indices are out of bounds!");
/*      */     }
/*      */ 
/*      */     
/*  897 */     if (x + tw > w) {
/*  898 */       tw = w - x;
/*      */     }
/*      */     
/*  901 */     if (y + th > h) {
/*  902 */       th = h - y;
/*      */     }
/*      */     
/*  905 */     ImageReadParam param = getDefaultReadParam();
/*  906 */     Rectangle tileRect = new Rectangle(x, y, tw, th);
/*  907 */     param.setSourceRegion(tileRect);
/*      */     
/*  909 */     return read(imageIndex, param);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean canReadRaster() {
/*  914 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Raster readRaster(int imageIndex, ImageReadParam param) throws IOException {
/*  920 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int ifloor(int num, int den) {
/*  935 */     if (num < 0) {
/*  936 */       num -= den - 1;
/*      */     }
/*  938 */     return num / den;
/*      */   }
/*      */ 
/*      */   
/*      */   private static int iceil(int num, int den) {
/*  943 */     if (num > 0) {
/*  944 */       num += den - 1;
/*      */     }
/*  946 */     return num / den;
/*      */   }
/*      */ 
/*      */   
/*      */   private void prepareRead(int imageIndex, ImageReadParam param) throws IOException {
/*  951 */     if (this.stream == null) {
/*  952 */       throw new IllegalStateException("Input not set!");
/*      */     }
/*      */ 
/*      */     
/*  956 */     if (param == null) {
/*  957 */       param = getDefaultReadParam();
/*      */     }
/*      */     
/*  960 */     this.imageReadParam = param;
/*      */     
/*  962 */     seekToImage(imageIndex);
/*      */     
/*  964 */     this.tileOrStripWidth = getTileOrStripWidth();
/*  965 */     this.tileOrStripHeight = getTileOrStripHeight();
/*  966 */     this.planarConfiguration = getPlanarConfiguration();
/*      */     
/*  968 */     this.sourceBands = param.getSourceBands();
/*  969 */     if (this.sourceBands == null) {
/*  970 */       this.sourceBands = new int[this.numBands];
/*  971 */       for (int j = 0; j < this.numBands; j++) {
/*  972 */         this.sourceBands[j] = j;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  977 */     Iterator imageTypes = getImageTypes(imageIndex);
/*      */     
/*  979 */     ImageTypeSpecifier theImageType = ImageUtil.getDestinationType(param, imageTypes);
/*      */     
/*  981 */     int destNumBands = theImageType.getSampleModel().getNumBands();
/*      */     
/*  983 */     this.destinationBands = param.getDestinationBands();
/*  984 */     if (this.destinationBands == null) {
/*  985 */       this.destinationBands = new int[destNumBands];
/*  986 */       for (int j = 0; j < destNumBands; j++) {
/*  987 */         this.destinationBands[j] = j;
/*      */       }
/*      */     } 
/*      */     
/*  991 */     if (this.sourceBands.length != this.destinationBands.length) {
/*  992 */       throw new IllegalArgumentException("sourceBands.length != destinationBands.length");
/*      */     }
/*      */ 
/*      */     
/*  996 */     for (int i = 0; i < this.sourceBands.length; i++) {
/*  997 */       int sb = this.sourceBands[i];
/*  998 */       if (sb < 0 || sb >= this.numBands) {
/*  999 */         throw new IllegalArgumentException("Source band out of range!");
/*      */       }
/*      */       
/* 1002 */       int db = this.destinationBands[i];
/* 1003 */       if (db < 0 || db >= destNumBands) {
/* 1004 */         throw new IllegalArgumentException("Destination band out of range!");
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RenderedImage readAsRenderedImage(int imageIndex, ImageReadParam param) throws IOException {
/* 1013 */     prepareRead(imageIndex, param);
/* 1014 */     return new TIFFRenderedImage(this, imageIndex, this.imageReadParam, this.width, this.height);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void decodeTile(int ti, int tj, int band) throws IOException {
/* 1024 */     Rectangle tileRect = new Rectangle(ti * this.tileOrStripWidth, tj * this.tileOrStripHeight, this.tileOrStripWidth, this.tileOrStripHeight);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1031 */     if (!isImageTiled(this.currIndex))
/*      */     {
/* 1033 */       tileRect = tileRect.intersection(new Rectangle(0, 0, this.width, this.height));
/*      */     }
/*      */ 
/*      */     
/* 1037 */     if (tileRect.width <= 0 || tileRect.height <= 0) {
/*      */       return;
/*      */     }
/*      */     
/* 1041 */     int srcMinX = tileRect.x;
/* 1042 */     int srcMinY = tileRect.y;
/* 1043 */     int srcWidth = tileRect.width;
/* 1044 */     int srcHeight = tileRect.height;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1049 */     this.dstMinX = iceil(srcMinX - this.sourceXOffset, this.srcXSubsampling);
/* 1050 */     int dstMaxX = ifloor(srcMinX + srcWidth - 1 - this.sourceXOffset, this.srcXSubsampling);
/*      */ 
/*      */     
/* 1053 */     this.dstMinY = iceil(srcMinY - this.sourceYOffset, this.srcYSubsampling);
/* 1054 */     int dstMaxY = ifloor(srcMinY + srcHeight - 1 - this.sourceYOffset, this.srcYSubsampling);
/*      */ 
/*      */     
/* 1057 */     this.dstWidth = dstMaxX - this.dstMinX + 1;
/* 1058 */     this.dstHeight = dstMaxY - this.dstMinY + 1;
/*      */     
/* 1060 */     this.dstMinX += this.dstXOffset;
/* 1061 */     this.dstMinY += this.dstYOffset;
/*      */ 
/*      */ 
/*      */     
/* 1065 */     Rectangle dstRect = new Rectangle(this.dstMinX, this.dstMinY, this.dstWidth, this.dstHeight);
/*      */ 
/*      */     
/* 1068 */     dstRect = dstRect.intersection(this.theImage.getRaster().getBounds());
/*      */     
/* 1070 */     this.dstMinX = dstRect.x;
/* 1071 */     this.dstMinY = dstRect.y;
/* 1072 */     this.dstWidth = dstRect.width;
/* 1073 */     this.dstHeight = dstRect.height;
/*      */     
/* 1075 */     if (this.dstWidth <= 0 || this.dstHeight <= 0) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1082 */     int activeSrcMinX = (this.dstMinX - this.dstXOffset) * this.srcXSubsampling + this.sourceXOffset;
/*      */     
/* 1084 */     int sxmax = (this.dstMinX + this.dstWidth - 1 - this.dstXOffset) * this.srcXSubsampling + this.sourceXOffset;
/*      */ 
/*      */     
/* 1087 */     int activeSrcWidth = sxmax - activeSrcMinX + 1;
/*      */     
/* 1089 */     int activeSrcMinY = (this.dstMinY - this.dstYOffset) * this.srcYSubsampling + this.sourceYOffset;
/*      */     
/* 1091 */     int symax = (this.dstMinY + this.dstHeight - 1 - this.dstYOffset) * this.srcYSubsampling + this.sourceYOffset;
/*      */ 
/*      */     
/* 1094 */     int activeSrcHeight = symax - activeSrcMinY + 1;
/*      */     
/* 1096 */     this.decompressor.setSrcMinX(srcMinX);
/* 1097 */     this.decompressor.setSrcMinY(srcMinY);
/* 1098 */     this.decompressor.setSrcWidth(srcWidth);
/* 1099 */     this.decompressor.setSrcHeight(srcHeight);
/*      */     
/* 1101 */     this.decompressor.setDstMinX(this.dstMinX);
/* 1102 */     this.decompressor.setDstMinY(this.dstMinY);
/* 1103 */     this.decompressor.setDstWidth(this.dstWidth);
/* 1104 */     this.decompressor.setDstHeight(this.dstHeight);
/*      */     
/* 1106 */     this.decompressor.setActiveSrcMinX(activeSrcMinX);
/* 1107 */     this.decompressor.setActiveSrcMinY(activeSrcMinY);
/* 1108 */     this.decompressor.setActiveSrcWidth(activeSrcWidth);
/* 1109 */     this.decompressor.setActiveSrcHeight(activeSrcHeight);
/*      */     
/* 1111 */     int tileIndex = tj * this.tilesAcross + ti;
/*      */     
/* 1113 */     if (this.planarConfiguration == 2)
/*      */     {
/* 1115 */       tileIndex += band * this.tilesAcross * this.tilesDown;
/*      */     }
/*      */     
/* 1118 */     long offset = getTileOrStripOffset(tileIndex);
/* 1119 */     long byteCount = getTileOrStripByteCount(tileIndex);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1128 */     long streamLength = this.stream.length();
/*      */     
/* 1130 */     processWarningOccurred("Attempting to process truncated stream.");
/* 1131 */     if (streamLength > 0L && offset + byteCount > streamLength && Math.max(byteCount = streamLength - offset, 0L) == 0L) {
/* 1132 */       processWarningOccurred("No bytes in strip/tile: skipping.");
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 1137 */     this.decompressor.setStream(this.stream);
/* 1138 */     this.decompressor.setOffset(offset);
/* 1139 */     this.decompressor.setByteCount((int)byteCount);
/*      */     
/* 1141 */     this.decompressor.beginDecoding();
/*      */     
/* 1143 */     this.stream.mark();
/* 1144 */     this.decompressor.decode();
/* 1145 */     this.stream.reset();
/*      */   }
/*      */ 
/*      */   
/*      */   private void reportProgress() {
/* 1150 */     this.pixelsRead += this.dstWidth * this.dstHeight;
/* 1151 */     processImageProgress(100.0F * this.pixelsRead / this.pixelsToRead);
/* 1152 */     processImageUpdate(this.theImage, this.dstMinX, this.dstMinY, this.dstWidth, this.dstHeight, 1, 1, this.destinationBands);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
/* 1160 */     prepareRead(imageIndex, param);
/* 1161 */     this.theImage = ImageReader.getDestination(param, 
/* 1162 */         getImageTypes(imageIndex), this.width, this.height);
/*      */ 
/*      */     
/* 1165 */     this.srcXSubsampling = this.imageReadParam.getSourceXSubsampling();
/* 1166 */     this.srcYSubsampling = this.imageReadParam.getSourceYSubsampling();
/*      */     
/* 1168 */     Point p = this.imageReadParam.getDestinationOffset();
/* 1169 */     this.dstXOffset = p.x;
/* 1170 */     this.dstYOffset = p.y;
/*      */ 
/*      */     
/* 1173 */     Rectangle srcRegion = new Rectangle(0, 0, 0, 0);
/* 1174 */     Rectangle destRegion = new Rectangle(0, 0, 0, 0);
/*      */     
/* 1176 */     computeRegions(this.imageReadParam, this.width, this.height, this.theImage, srcRegion, destRegion);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1181 */     this.sourceXOffset = srcRegion.x;
/* 1182 */     this.sourceYOffset = srcRegion.y;
/*      */     
/* 1184 */     this.pixelsToRead = destRegion.width * destRegion.height;
/* 1185 */     this.pixelsRead = 0;
/*      */     
/* 1187 */     processImageStarted(imageIndex);
/* 1188 */     processImageProgress(0.0F);
/*      */     
/* 1190 */     this.tilesAcross = (this.width + this.tileOrStripWidth - 1) / this.tileOrStripWidth;
/* 1191 */     this.tilesDown = (this.height + this.tileOrStripHeight - 1) / this.tileOrStripHeight;
/*      */     
/* 1193 */     int compression = getCompression();
/*      */ 
/*      */ 
/*      */     
/* 1197 */     TIFFColorConverter colorConverter = null;
/* 1198 */     if (this.imageReadParam instanceof TIFFImageReadParam) {
/* 1199 */       TIFFImageReadParam tparam = (TIFFImageReadParam)this.imageReadParam;
/*      */       
/* 1201 */       this.decompressor = tparam.getTIFFDecompressor();
/* 1202 */       colorConverter = tparam.getColorConverter();
/*      */     } 
/*      */ 
/*      */     
/* 1206 */     if (this.decompressor == null) {
/* 1207 */       if (compression == 1) {
/*      */ 
/*      */ 
/*      */         
/* 1211 */         TIFFField fillOrderField = this.imageMetadata.getTIFFField(266);
/*      */ 
/*      */         
/* 1214 */         if (fillOrderField != null && fillOrderField.getAsInt(0) == 2) {
/* 1215 */           this.decompressor = new TIFFLSBDecompressor();
/*      */         } else {
/* 1217 */           this.decompressor = new TIFFNullDecompressor();
/*      */         } 
/* 1219 */       } else if (compression == 4) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1224 */         if (this.decompressor == null)
/*      */         {
/*      */ 
/*      */           
/* 1228 */           this.decompressor = new TIFFFaxDecompressor();
/*      */         }
/* 1230 */       } else if (compression == 3) {
/*      */ 
/*      */ 
/*      */         
/* 1234 */         if (this.decompressor == null)
/*      */         {
/*      */ 
/*      */           
/* 1238 */           this.decompressor = new TIFFFaxDecompressor();
/*      */         }
/* 1240 */       } else if (compression == 2) {
/*      */         
/* 1242 */         this.decompressor = new TIFFFaxDecompressor();
/* 1243 */       } else if (compression == 32773) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1248 */         this.decompressor = new TIFFPackBitsDecompressor();
/* 1249 */       } else if (compression == 5) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1255 */         TIFFField predictorField = this.imageMetadata.getTIFFField(317);
/*      */ 
/*      */         
/* 1258 */         int predictor = (predictorField == null) ? 1 : predictorField.getAsInt(0);
/* 1259 */         this.decompressor = new TIFFLZWDecompressor(predictor);
/* 1260 */       } else if (compression == 7) {
/*      */         
/* 1262 */         this.decompressor = new TIFFJPEGDecompressor();
/* 1263 */       } else if (compression == 8 || compression == 32946) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1268 */         TIFFField predictorField = this.imageMetadata.getTIFFField(317);
/*      */ 
/*      */         
/* 1271 */         int predictor = (predictorField == null) ? 1 : predictorField.getAsInt(0);
/* 1272 */         this.decompressor = new TIFFDeflateDecompressor(predictor);
/* 1273 */       } else if (compression == 6) {
/*      */ 
/*      */         
/* 1276 */         TIFFField JPEGProcField = this.imageMetadata.getTIFFField(512);
/* 1277 */         if (JPEGProcField == null) {
/*      */           
/* 1279 */           processWarningOccurred("JPEGProc field missing; assuming baseline sequential JPEG process.");
/* 1280 */         } else if (JPEGProcField.getAsInt(0) != 1) {
/*      */           
/* 1282 */           throw new IIOException("Old-style JPEG supported for baseline sequential JPEG process only!");
/*      */         } 
/*      */         
/* 1285 */         this.decompressor = new TIFFOldJPEGDecompressor();
/*      */       } else {
/*      */         
/* 1288 */         throw new IIOException("Unsupported compression type (tag number = " + compression + ")!");
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1293 */       if (this.photometricInterpretation == 6 && compression != 7 && compression != 6) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1298 */         boolean convertYCbCrToRGB = (this.theImage.getColorModel().getColorSpace().getType() == 5);
/*      */         
/* 1300 */         TIFFDecompressor wrappedDecompressor = (this.decompressor instanceof TIFFNullDecompressor) ? null : this.decompressor;
/*      */ 
/*      */         
/* 1303 */         this.decompressor = new TIFFYCbCrDecompressor(wrappedDecompressor, convertYCbCrToRGB);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1314 */     if (colorConverter == null) {
/* 1315 */       if (this.photometricInterpretation == 8 && this.theImage
/*      */         
/* 1317 */         .getColorModel().getColorSpace().getType() == 5) {
/*      */         
/* 1319 */         colorConverter = new TIFFCIELabColorConverter();
/* 1320 */       } else if (this.photometricInterpretation == 6 && !(this.decompressor instanceof TIFFYCbCrDecompressor) && compression != 7 && compression != 6) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1325 */         colorConverter = new TIFFYCbCrColorConverter(this.imageMetadata);
/*      */       } 
/*      */     }
/*      */     
/* 1329 */     this.decompressor.setReader(this);
/* 1330 */     this.decompressor.setMetadata(this.imageMetadata);
/* 1331 */     this.decompressor.setImage(this.theImage);
/*      */     
/* 1333 */     this.decompressor.setPhotometricInterpretation(this.photometricInterpretation);
/* 1334 */     this.decompressor.setCompression(compression);
/* 1335 */     this.decompressor.setSamplesPerPixel(this.samplesPerPixel);
/* 1336 */     this.decompressor.setBitsPerSample(this.bitsPerSample);
/* 1337 */     this.decompressor.setSampleFormat(this.sampleFormat);
/* 1338 */     this.decompressor.setExtraSamples(this.extraSamples);
/* 1339 */     this.decompressor.setColorMap(this.colorMap);
/*      */     
/* 1341 */     this.decompressor.setColorConverter(colorConverter);
/*      */     
/* 1343 */     this.decompressor.setSourceXOffset(this.sourceXOffset);
/* 1344 */     this.decompressor.setSourceYOffset(this.sourceYOffset);
/* 1345 */     this.decompressor.setSubsampleX(this.srcXSubsampling);
/* 1346 */     this.decompressor.setSubsampleY(this.srcYSubsampling);
/*      */     
/* 1348 */     this.decompressor.setDstXOffset(this.dstXOffset);
/* 1349 */     this.decompressor.setDstYOffset(this.dstYOffset);
/*      */     
/* 1351 */     this.decompressor.setSourceBands(this.sourceBands);
/* 1352 */     this.decompressor.setDestinationBands(this.destinationBands);
/*      */ 
/*      */ 
/*      */     
/* 1356 */     int minTileX = TIFFImageWriter.XToTileX(srcRegion.x, 0, this.tileOrStripWidth);
/*      */     
/* 1358 */     int minTileY = TIFFImageWriter.YToTileY(srcRegion.y, 0, this.tileOrStripHeight);
/*      */     
/* 1360 */     int maxTileX = TIFFImageWriter.XToTileX(srcRegion.x + srcRegion.width - 1, 0, this.tileOrStripWidth);
/*      */ 
/*      */     
/* 1363 */     int maxTileY = TIFFImageWriter.YToTileY(srcRegion.y + srcRegion.height - 1, 0, this.tileOrStripHeight);
/*      */ 
/*      */     
/* 1366 */     boolean isAbortRequested = false;
/* 1367 */     if (this.planarConfiguration == 2) {
/*      */ 
/*      */       
/* 1370 */       this.decompressor.setPlanar(true);
/*      */       
/* 1372 */       int[] sb = new int[1];
/* 1373 */       int[] db = new int[1];
/* 1374 */       for (int tj = minTileY; tj <= maxTileY; tj++) {
/* 1375 */         for (int ti = minTileX; ti <= maxTileX; ti++) {
/* 1376 */           for (int band = 0; band < this.numBands; band++) {
/* 1377 */             sb[0] = this.sourceBands[band];
/* 1378 */             this.decompressor.setSourceBands(sb);
/* 1379 */             db[0] = this.destinationBands[band];
/* 1380 */             this.decompressor.setDestinationBands(db);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1386 */             if (abortRequested()) {
/* 1387 */               isAbortRequested = true;
/*      */               
/*      */               break;
/*      */             } 
/* 1391 */             decodeTile(ti, tj, band);
/*      */           } 
/*      */           
/* 1394 */           if (isAbortRequested)
/*      */             break; 
/* 1396 */           reportProgress();
/*      */         } 
/*      */         
/* 1399 */         if (isAbortRequested) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } else {
/* 1404 */       for (int tj = minTileY; tj <= maxTileY; tj++) {
/* 1405 */         for (int ti = minTileX; ti <= maxTileX; ti++) {
/*      */ 
/*      */ 
/*      */           
/* 1409 */           if (abortRequested()) {
/* 1410 */             isAbortRequested = true;
/*      */             
/*      */             break;
/*      */           } 
/* 1414 */           decodeTile(ti, tj, -1);
/*      */           
/* 1416 */           reportProgress();
/*      */         } 
/*      */         
/* 1419 */         if (isAbortRequested)
/*      */           break; 
/*      */       } 
/*      */     } 
/* 1423 */     if (isAbortRequested) {
/* 1424 */       processReadAborted();
/*      */     } else {
/* 1426 */       processImageComplete();
/*      */     } 
/*      */     
/* 1429 */     return this.theImage;
/*      */   }
/*      */   
/*      */   public void reset() {
/* 1433 */     super.reset();
/* 1434 */     resetLocal();
/*      */   }
/*      */   
/*      */   protected void resetLocal() {
/* 1438 */     this.stream = null;
/* 1439 */     this.gotHeader = false;
/* 1440 */     this.imageReadParam = getDefaultReadParam();
/* 1441 */     this.streamMetadata = null;
/* 1442 */     this.currIndex = -1;
/* 1443 */     this.imageMetadata = null;
/* 1444 */     this.imageStartPosition = new ArrayList();
/* 1445 */     this.numImages = -1;
/* 1446 */     this.imageTypeMap = new HashMap<Object, Object>();
/* 1447 */     this.width = -1;
/* 1448 */     this.height = -1;
/* 1449 */     this.numBands = -1;
/* 1450 */     this.tileOrStripWidth = -1;
/* 1451 */     this.tileOrStripHeight = -1;
/* 1452 */     this.planarConfiguration = 1;
/* 1453 */     this.rowsDone = 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void forwardWarningMessage(String warning) {
/* 1461 */     processWarningOccurred(warning);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static BufferedImage getDestination(ImageReadParam param, Iterator<ImageTypeSpecifier> imageTypes, int width, int height) throws IIOException {
/* 1468 */     if (imageTypes == null || !imageTypes.hasNext()) {
/* 1469 */       throw new IllegalArgumentException("imageTypes null or empty!");
/*      */     }
/*      */     
/* 1472 */     BufferedImage dest = null;
/* 1473 */     ImageTypeSpecifier imageType = null;
/*      */ 
/*      */     
/* 1476 */     if (param != null) {
/*      */       
/* 1478 */       dest = param.getDestination();
/* 1479 */       if (dest != null) {
/* 1480 */         return dest;
/*      */       }
/*      */ 
/*      */       
/* 1484 */       imageType = param.getDestinationType();
/*      */     } 
/*      */ 
/*      */     
/* 1488 */     if (imageType == null) {
/* 1489 */       Object o = imageTypes.next();
/* 1490 */       if (!(o instanceof ImageTypeSpecifier)) {
/* 1491 */         throw new IllegalArgumentException("Non-ImageTypeSpecifier retrieved from imageTypes!");
/*      */       }
/*      */       
/* 1494 */       imageType = (ImageTypeSpecifier)o;
/*      */     } else {
/* 1496 */       boolean foundIt = false;
/* 1497 */       while (imageTypes.hasNext()) {
/*      */         
/* 1499 */         ImageTypeSpecifier type = imageTypes.next();
/* 1500 */         if (type.equals(imageType)) {
/* 1501 */           foundIt = true;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/* 1506 */       if (!foundIt) {
/* 1507 */         throw new IIOException("Destination type from ImageReadParam does not match!");
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1512 */     Rectangle srcRegion = new Rectangle(0, 0, 0, 0);
/* 1513 */     Rectangle destRegion = new Rectangle(0, 0, 0, 0);
/* 1514 */     computeRegions(param, width, height, null, srcRegion, destRegion);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1521 */     int destWidth = destRegion.x + destRegion.width;
/* 1522 */     int destHeight = destRegion.y + destRegion.height;
/*      */ 
/*      */     
/* 1525 */     if (destWidth * destHeight > 2147483647L) {
/* 1526 */       throw new IllegalArgumentException("width*height > Integer.MAX_VALUE!");
/*      */     }
/*      */ 
/*      */     
/* 1530 */     return imageType.createBufferedImage(destWidth, destHeight);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFImageReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */