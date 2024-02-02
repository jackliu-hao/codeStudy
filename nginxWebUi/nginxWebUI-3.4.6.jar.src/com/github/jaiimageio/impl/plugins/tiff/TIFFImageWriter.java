/*      */ package com.github.jaiimageio.impl.plugins.tiff;
/*      */ 
/*      */ import com.github.jaiimageio.impl.common.ImageUtil;
/*      */ import com.github.jaiimageio.impl.common.SingleTileRenderedImage;
/*      */ import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
/*      */ import com.github.jaiimageio.plugins.tiff.EXIFParentTIFFTagSet;
/*      */ import com.github.jaiimageio.plugins.tiff.EXIFTIFFTagSet;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFColorConverter;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFCompressor;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFImageWriteParam;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFTag;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.color.ICC_ColorSpace;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ComponentSampleModel;
/*      */ import java.awt.image.DataBuffer;
/*      */ import java.awt.image.DataBufferByte;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.RenderedImage;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.nio.ByteOrder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import javax.imageio.IIOException;
/*      */ import javax.imageio.IIOImage;
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ import javax.imageio.ImageWriteParam;
/*      */ import javax.imageio.ImageWriter;
/*      */ import javax.imageio.metadata.IIOInvalidTreeException;
/*      */ import javax.imageio.metadata.IIOMetadata;
/*      */ import javax.imageio.spi.ImageWriterSpi;
/*      */ import javax.imageio.stream.ImageOutputStream;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TIFFImageWriter
/*      */   extends ImageWriter
/*      */ {
/*      */   private static final boolean DEBUG = false;
/*      */   static final String EXIF_JPEG_COMPRESSION_TYPE = "EXIF JPEG";
/*      */   public static final int DEFAULT_BYTES_PER_STRIP = 8192;
/*  105 */   public static final String[] TIFFCompressionTypes = new String[] { "CCITT RLE", "CCITT T.4", "CCITT T.6", "LZW", "JPEG", "ZLib", "PackBits", "Deflate", "EXIF JPEG" };
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
/*  127 */   public static final String[] compressionTypes = new String[] { "CCITT RLE", "CCITT T.4", "CCITT T.6", "LZW", "Old JPEG", "JPEG", "ZLib", "PackBits", "Deflate", "EXIF JPEG" };
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
/*  143 */   public static final boolean[] isCompressionLossless = new boolean[] { true, true, true, true, false, false, true, true, true, false };
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
/*  159 */   public static final int[] compressionNumbers = new int[] { 2, 3, 4, 5, 6, 7, 8, 32773, 32946, 6 };
/*      */   
/*      */   ImageOutputStream stream;
/*      */   
/*      */   long headerPosition;
/*      */   
/*      */   RenderedImage image;
/*      */   
/*      */   ImageTypeSpecifier imageType;
/*      */   
/*      */   ByteOrder byteOrder;
/*      */   
/*      */   ImageWriteParam param;
/*      */   
/*      */   TIFFCompressor compressor;
/*      */   
/*      */   TIFFColorConverter colorConverter;
/*      */   
/*      */   TIFFStreamMetadata streamMetadata;
/*      */   
/*      */   TIFFImageMetadata imageMetadata;
/*      */   
/*      */   int sourceXOffset;
/*      */   
/*      */   int sourceYOffset;
/*      */   
/*      */   int sourceWidth;
/*      */   
/*      */   int sourceHeight;
/*      */   
/*      */   int[] sourceBands;
/*      */   
/*      */   int periodX;
/*      */   int periodY;
/*      */   int bitDepth;
/*      */   int numBands;
/*      */   int tileWidth;
/*      */   int tileLength;
/*      */   int tilesAcross;
/*      */   int tilesDown;
/*  199 */   int[] sampleSize = null;
/*  200 */   int scalingBitDepth = -1;
/*      */   
/*      */   boolean isRescaling = false;
/*      */   
/*      */   boolean isBilevel;
/*      */   
/*      */   boolean isImageSimple;
/*      */   
/*      */   boolean isInverted;
/*      */   boolean isTiled;
/*      */   int nativePhotometricInterpretation;
/*      */   int photometricInterpretation;
/*      */   char[] bitsPerSample;
/*  213 */   int sampleFormat = 4;
/*      */ 
/*      */ 
/*      */   
/*  217 */   byte[][] scale = (byte[][])null;
/*  218 */   byte[] scale0 = null;
/*      */ 
/*      */   
/*  221 */   byte[][] scaleh = (byte[][])null;
/*  222 */   byte[][] scalel = (byte[][])null;
/*      */   
/*      */   int compression;
/*      */   
/*      */   int predictor;
/*      */   
/*      */   int totalPixels;
/*      */   
/*      */   int pixelsDone;
/*      */   
/*      */   long nextIFDPointerPos;
/*  233 */   long nextSpace = 0L;
/*      */   
/*      */   boolean isWritingSequence = false;
/*      */   private boolean isInsertingEmpty;
/*      */   private boolean isWritingEmpty;
/*      */   private Object replacePixelsLock;
/*      */   private int replacePixelsIndex;
/*      */   private TIFFImageMetadata replacePixelsMetadata;
/*      */   private long[] replacePixelsTileOffsets;
/*      */   private long[] replacePixelsByteCounts;
/*      */   private long replacePixelsOffsetsPosition;
/*      */   private long replacePixelsByteCountsPosition;
/*      */   private Rectangle replacePixelsRegion;
/*      */   private boolean inReplacePixelsNest;
/*      */   private TIFFImageReader reader;
/*      */   
/*      */   public static int XToTileX(int x, int tileGridXOffset, int tileWidth) {
/*  250 */     x -= tileGridXOffset;
/*  251 */     if (x < 0) {
/*  252 */       x += 1 - tileWidth;
/*      */     }
/*  254 */     return x / tileWidth;
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
/*      */   public static int YToTileY(int y, int tileGridYOffset, int tileHeight) {
/*  269 */     y -= tileGridYOffset;
/*  270 */     if (y < 0) {
/*  271 */       y += 1 - tileHeight;
/*      */     }
/*  273 */     return y / tileHeight;
/*      */   }
/*      */   public ImageWriteParam getDefaultWriteParam() { return (ImageWriteParam)new TIFFImageWriteParam(getLocale()); }
/*      */   public void setOutput(Object output) { super.setOutput(output); if (output != null) { if (!(output instanceof ImageOutputStream)) throw new IllegalArgumentException("output not an ImageOutputStream!");  this.stream = (ImageOutputStream)output; try { this.headerPosition = this.stream.getStreamPosition(); try { byte[] b = new byte[4]; this.stream.readFully(b); if ((b[0] == 73 && b[1] == 73 && b[2] == 42 && b[3] == 0) || (b[0] == 77 && b[1] == 77 && b[2] == 0 && b[3] == 42)) { this.nextSpace = this.stream.length(); } else { this.nextSpace = this.headerPosition; }  } catch (IOException io) { this.nextSpace = this.headerPosition; }  this.stream.seek(this.headerPosition); } catch (IOException ioe) { this.nextSpace = this.headerPosition = 0L; }  } else { this.stream = null; }  }
/*  277 */   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) { return new TIFFStreamMetadata(); } public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) { List<BaselineTIFFTagSet> tagSets = new ArrayList(1); tagSets.add(BaselineTIFFTagSet.getInstance()); TIFFImageMetadata imageMetadata = new TIFFImageMetadata(tagSets); if (imageType != null) { TIFFImageMetadata im = (TIFFImageMetadata)convertImageMetadata(imageMetadata, imageType, param); if (im != null) imageMetadata = im;  }  return imageMetadata; } public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) { if (inData == null) throw new IllegalArgumentException("inData == null!");  TIFFStreamMetadata outData = null; if (inData instanceof TIFFStreamMetadata) { outData = new TIFFStreamMetadata(); outData.byteOrder = ((TIFFStreamMetadata)inData).byteOrder; return outData; }  if (Arrays.<String>asList(inData.getMetadataFormatNames()).contains("com_sun_media_imageio_plugins_tiff_stream_1.0")) { outData = new TIFFStreamMetadata(); String format = "com_sun_media_imageio_plugins_tiff_stream_1.0"; try { outData.mergeTree(format, inData.getAsTree(format)); } catch (IIOInvalidTreeException iIOInvalidTreeException) {} }  return outData; } public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) { if (inData == null) throw new IllegalArgumentException("inData == null!");  if (imageType == null) throw new IllegalArgumentException("imageType == null!");  TIFFImageMetadata outData = null; if (inData instanceof TIFFImageMetadata) { TIFFIFD inIFD = ((TIFFImageMetadata)inData).getRootIFD(); outData = new TIFFImageMetadata(inIFD.getShallowClone()); } else if (Arrays.<String>asList(inData.getMetadataFormatNames()).contains("com_sun_media_imageio_plugins_tiff_image_1.0")) { try { outData = convertNativeImageMetadata(inData); } catch (IIOInvalidTreeException iIOInvalidTreeException) {} } else if (inData.isStandardMetadataFormatSupported()) { try { outData = convertStandardImageMetadata(inData); } catch (IIOInvalidTreeException iIOInvalidTreeException) {} }  if (outData != null) { TIFFImageWriter bogusWriter = new TIFFImageWriter(this.originatingProvider); bogusWriter.imageMetadata = outData; bogusWriter.param = param; SampleModel sm = imageType.getSampleModel(); try { bogusWriter.setupMetadata(imageType.getColorModel(), sm, sm.getWidth(), sm.getHeight()); return bogusWriter.imageMetadata; } catch (IIOException e) { return null; } finally { bogusWriter.dispose(); }  }  return outData; } private TIFFImageMetadata convertStandardImageMetadata(IIOMetadata inData) throws IIOInvalidTreeException { if (inData == null) throw new IllegalArgumentException("inData == null!");  if (!inData.isStandardMetadataFormatSupported()) throw new IllegalArgumentException("inData does not support standard metadata format!");  TIFFImageMetadata outData = null; String formatName = "javax_imageio_1.0"; Node tree = inData.getAsTree(formatName); if (tree != null) { List<BaselineTIFFTagSet> tagSets = new ArrayList(1); tagSets.add(BaselineTIFFTagSet.getInstance()); outData = new TIFFImageMetadata(tagSets); outData.setFromTree(formatName, tree); }  return outData; } private TIFFImageMetadata convertNativeImageMetadata(IIOMetadata inData) throws IIOInvalidTreeException { if (inData == null) throw new IllegalArgumentException("inData == null!");  if (!Arrays.<String>asList(inData.getMetadataFormatNames()).contains("com_sun_media_imageio_plugins_tiff_image_1.0")) throw new IllegalArgumentException("inData does not support native metadata format!");  TIFFImageMetadata outData = null; String formatName = "com_sun_media_imageio_plugins_tiff_image_1.0"; Node tree = inData.getAsTree(formatName); if (tree != null) { List<BaselineTIFFTagSet> tagSets = new ArrayList(1); tagSets.add(BaselineTIFFTagSet.getInstance()); outData = new TIFFImageMetadata(tagSets); outData.setFromTree(formatName, tree); }  return outData; } public TIFFImageWriter(ImageWriterSpi originatingProvider) { super(originatingProvider);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2882 */     this.isInsertingEmpty = false;
/* 2883 */     this.isWritingEmpty = false;
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
/* 3072 */     this.replacePixelsLock = new Object();
/*      */     
/* 3074 */     this.replacePixelsIndex = -1;
/* 3075 */     this.replacePixelsMetadata = null;
/* 3076 */     this.replacePixelsTileOffsets = null;
/* 3077 */     this.replacePixelsByteCounts = null;
/* 3078 */     this.replacePixelsOffsetsPosition = 0L;
/* 3079 */     this.replacePixelsByteCountsPosition = 0L;
/* 3080 */     this.replacePixelsRegion = null;
/* 3081 */     this.inReplacePixelsNest = false;
/*      */     
/* 3083 */     this.reader = null; }
/*      */   void setupMetadata(ColorModel cm, SampleModel sm, int destWidth, int destHeight) throws IIOException { String compressionType; TIFFField compField; TIFFIFD rootIFD = this.imageMetadata.getRootIFD(); BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance(); TIFFField f = rootIFD.getTIFFField(284); if (f != null && f.getAsInt(0) != 1) { TIFFField planarConfigurationField = new TIFFField(base.getTag(284), 1); rootIFD.addTIFFField(planarConfigurationField); }  char[] extraSamples = null; this.photometricInterpretation = -1; boolean forcePhotometricInterpretation = false; f = rootIFD.getTIFFField(262); if (f != null) { this.photometricInterpretation = f.getAsInt(0); if (this.photometricInterpretation == 3 && !(cm instanceof IndexColorModel)) { this.photometricInterpretation = -1; } else { forcePhotometricInterpretation = true; }  }  int[] sampleSize = sm.getSampleSize(); int numBands = sm.getNumBands(); int numExtraSamples = 0; if (numBands > 1 && cm != null && cm.hasAlpha()) { numBands--; numExtraSamples = 1; extraSamples = new char[1]; if (cm.isAlphaPremultiplied()) { extraSamples[0] = '\001'; } else { extraSamples[0] = '\002'; }  }  if (numBands == 3) { this.nativePhotometricInterpretation = 2; if (this.photometricInterpretation == -1) this.photometricInterpretation = 2;  } else if (sm.getNumBands() == 1 && cm instanceof IndexColorModel) { IndexColorModel icm = (IndexColorModel)cm; int r0 = icm.getRed(0); int r1 = icm.getRed(1); if (icm.getMapSize() == 2 && r0 == icm.getGreen(0) && r0 == icm.getBlue(0) && r1 == icm.getGreen(1) && r1 == icm.getBlue(1) && (r0 == 0 || r0 == 255) && (r1 == 0 || r1 == 255) && r0 != r1) { if (r0 == 0) { this.nativePhotometricInterpretation = 1; } else { this.nativePhotometricInterpretation = 0; }  if (this.photometricInterpretation != 1 && this.photometricInterpretation != 0) this.photometricInterpretation = (r0 == 0) ? 1 : 0;  } else { this.nativePhotometricInterpretation = this.photometricInterpretation = 3; }  } else { if (cm != null) { switch (cm.getColorSpace().getType()) { case 1: this.nativePhotometricInterpretation = 8; break;case 3: this.nativePhotometricInterpretation = 6; break;case 9: this.nativePhotometricInterpretation = 5; break;default: this.nativePhotometricInterpretation = 1; break; }  } else { this.nativePhotometricInterpretation = 1; }  if (this.photometricInterpretation == -1) this.photometricInterpretation = this.nativePhotometricInterpretation;  }  this.compressor = null; this.colorConverter = null; if (this.param instanceof TIFFImageWriteParam) { TIFFImageWriteParam tparam = (TIFFImageWriteParam)this.param; if (tparam.getCompressionMode() == 2) { this.compressor = tparam.getTIFFCompressor(); String str = this.param.getCompressionType(); if (this.compressor != null && !this.compressor.getCompressionType().equals(str)) this.compressor = null;  } else { this.compressor = null; }  this.colorConverter = tparam.getColorConverter(); if (this.colorConverter != null) this.photometricInterpretation = tparam.getPhotometricInterpretation();  }  int compressionMode = (this.param instanceof TIFFImageWriteParam) ? this.param.getCompressionMode() : 1; switch (compressionMode) { case 2: compressionType = this.param.getCompressionType(); if (compressionType == null) { this.compression = 1; } else { int len = compressionTypes.length; for (int j = 0; j < len; j++) { if (compressionType.equals(compressionTypes[j])) this.compression = compressionNumbers[j];  }  }  if (this.compressor != null && this.compressor.getCompressionTagValue() != this.compression) this.compressor = null;  break;case 3: compField = rootIFD.getTIFFField(259); if (compField != null) { this.compression = compField.getAsInt(0); break; } default: this.compression = 1; break; }  TIFFField predictorField = rootIFD.getTIFFField(317); if (predictorField != null) { this.predictor = predictorField.getAsInt(0); if (sampleSize[0] != 8 || (this.predictor != 1 && this.predictor != 2)) { this.predictor = 1; TIFFField newPredictorField = new TIFFField(base.getTag(317), this.predictor); rootIFD.addTIFFField(newPredictorField); }  }  TIFFField compressionField = new TIFFField(base.getTag(259), this.compression); rootIFD.addTIFFField(compressionField); boolean isEXIF = false; if (numBands == 3 && sampleSize[0] == 8 && sampleSize[1] == 8 && sampleSize[2] == 8) if (rootIFD.getTIFFField(34665) != null) { if (this.compression == 1 && (this.photometricInterpretation == 2 || this.photometricInterpretation == 6)) { isEXIF = true; } else if (this.compression == 6) { isEXIF = true; }  } else if (compressionMode == 2 && "EXIF JPEG".equals(this.param.getCompressionType())) { isEXIF = true; }   boolean isJPEGInterchange = (isEXIF && this.compression == 6); if (this.compressor == null) if (this.compression == 2) { if (this.compressor == null) this.compressor = new TIFFRLECompressor();  if (!forcePhotometricInterpretation) this.photometricInterpretation = 0;  } else if (this.compression == 3) { if (this.compressor == null) this.compressor = new TIFFT4Compressor();  if (!forcePhotometricInterpretation) this.photometricInterpretation = 0;  } else if (this.compression == 4) { if (this.compressor == null) this.compressor = new TIFFT6Compressor();  if (!forcePhotometricInterpretation) this.photometricInterpretation = 0;  } else if (this.compression == 5) { this.compressor = new TIFFLZWCompressor(this.predictor); } else if (this.compression == 6) { if (isEXIF) { this.compressor = new TIFFEXIFJPEGCompressor(this.param); } else { throw new IIOException("Old JPEG compression not supported!"); }  } else if (this.compression == 7) { if (numBands == 3 && sampleSize[0] == 8 && sampleSize[1] == 8 && sampleSize[2] == 8) { this.photometricInterpretation = 6; } else if (numBands == 1 && sampleSize[0] == 8) { this.photometricInterpretation = 1; } else { throw new IIOException("JPEG compression supported for 1- and 3-band byte images only!"); }  this.compressor = new TIFFJPEGCompressor(this.param); } else if (this.compression == 8) { this.compressor = new TIFFZLibCompressor(this.param, this.predictor); } else if (this.compression == 32773) { this.compressor = new TIFFPackBitsCompressor(); } else if (this.compression == 32946) { this.compressor = new TIFFDeflateCompressor(this.param, this.predictor); } else { f = rootIFD.getTIFFField(266); boolean inverseFill = (f != null && f.getAsInt(0) == 2); if (inverseFill) { this.compressor = new TIFFLSBCompressor(); } else { this.compressor = new TIFFNullCompressor(); }  }   if (this.colorConverter == null && cm != null && cm.getColorSpace().getType() == 5) if (this.photometricInterpretation == 6 && this.compression != 7) { this.colorConverter = new TIFFYCbCrColorConverter(this.imageMetadata); } else if (this.photometricInterpretation == 8) { this.colorConverter = new TIFFCIELabColorConverter(); }   if (this.photometricInterpretation == 6 && this.compression != 7) { rootIFD.removeTIFFField(530); rootIFD.removeTIFFField(531); rootIFD.addTIFFField(new TIFFField(base.getTag(530), 3, 2, new char[] { '\001', '\001' })); rootIFD.addTIFFField(new TIFFField(base.getTag(531), 3, 1, new char[] { '\002' })); }  TIFFField photometricInterpretationField = new TIFFField(base.getTag(262), this.photometricInterpretation); rootIFD.addTIFFField(photometricInterpretationField); this.bitsPerSample = new char[numBands + numExtraSamples]; this.bitDepth = 0; int i; for (i = 0; i < numBands; i++) this.bitDepth = Math.max(this.bitDepth, sampleSize[i]);  if (this.bitDepth == 3) { this.bitDepth = 4; } else if (this.bitDepth > 4 && this.bitDepth < 8) { this.bitDepth = 8; } else if (this.bitDepth > 8 && this.bitDepth < 16) { this.bitDepth = 16; } else if (this.bitDepth > 16) { this.bitDepth = 32; }  for (i = 0; i < this.bitsPerSample.length; i++) this.bitsPerSample[i] = (char)this.bitDepth;  if (this.bitsPerSample.length != 1 || this.bitsPerSample[0] != '\001') { TIFFField bitsPerSampleField = new TIFFField(base.getTag(258), 3, this.bitsPerSample.length, this.bitsPerSample); rootIFD.addTIFFField(bitsPerSampleField); } else { TIFFField bitsPerSampleField = rootIFD.getTIFFField(258); if (bitsPerSampleField != null) { int[] bps = bitsPerSampleField.getAsInts(); if (bps == null || bps.length != 1 || bps[0] != 1) rootIFD.removeTIFFField(258);  }  }  f = rootIFD.getTIFFField(339); if (f == null && (this.bitDepth == 16 || this.bitDepth == 32)) { char sampleFormatValue; int dataType = sm.getDataType(); if (this.bitDepth == 16 && dataType == 1) { sampleFormatValue = '\001'; } else if (this.bitDepth == 32 && dataType == 4) { sampleFormatValue = '\003'; } else { sampleFormatValue = '\002'; }  this.sampleFormat = sampleFormatValue; char[] sampleFormatArray = new char[this.bitsPerSample.length]; Arrays.fill(sampleFormatArray, sampleFormatValue); TIFFTag sampleFormatTag = base.getTag(339); TIFFField sampleFormatField = new TIFFField(sampleFormatTag, 3, sampleFormatArray.length, sampleFormatArray); rootIFD.addTIFFField(sampleFormatField); } else if (f != null) { this.sampleFormat = f.getAsInt(0); } else { this.sampleFormat = 4; }  if (extraSamples != null) { TIFFField extraSamplesField = new TIFFField(base.getTag(338), 3, extraSamples.length, extraSamples); rootIFD.addTIFFField(extraSamplesField); } else { rootIFD.removeTIFFField(338); }  TIFFField samplesPerPixelField = new TIFFField(base.getTag(277), this.bitsPerSample.length); rootIFD.addTIFFField(samplesPerPixelField); if (this.photometricInterpretation == 3 && cm instanceof IndexColorModel) { char[] colorMap = new char[3 * (1 << this.bitsPerSample[0])]; IndexColorModel icm = (IndexColorModel)cm; int mapSize = 1 << this.bitsPerSample[0]; int indexBound = Math.min(mapSize, icm.getMapSize()); for (int j = 0; j < indexBound; j++) { colorMap[j] = (char)(icm.getRed(j) * 65535 / 255); colorMap[mapSize + j] = (char)(icm.getGreen(j) * 65535 / 255); colorMap[2 * mapSize + j] = (char)(icm.getBlue(j) * 65535 / 255); }  TIFFField colorMapField = new TIFFField(base.getTag(320), 3, colorMap.length, colorMap); rootIFD.addTIFFField(colorMapField); } else { rootIFD.removeTIFFField(320); }  if (cm != null && rootIFD.getTIFFField(34675) == null && ImageUtil.isNonStandardICCColorSpace(cm.getColorSpace())) { ICC_ColorSpace iccColorSpace = (ICC_ColorSpace)cm.getColorSpace(); byte[] iccProfileData = iccColorSpace.getProfile().getData(); TIFFField iccProfileField = new TIFFField(base.getTag(34675), 7, iccProfileData.length, iccProfileData); rootIFD.addTIFFField(iccProfileField); }  TIFFField XResolutionField = rootIFD.getTIFFField(282); TIFFField YResolutionField = rootIFD.getTIFFField(283); if (XResolutionField == null && YResolutionField == null) { long[][] resRational = new long[1][2]; resRational[0] = new long[2]; TIFFField ResolutionUnitField = rootIFD.getTIFFField(296); if (ResolutionUnitField == null && rootIFD.getTIFFField(286) == null && rootIFD.getTIFFField(287) == null) { resRational[0][0] = 1L; resRational[0][1] = 1L; ResolutionUnitField = new TIFFField(rootIFD.getTag(296), 1); rootIFD.addTIFFField(ResolutionUnitField); } else { int resolutionUnit = (ResolutionUnitField != null) ? ResolutionUnitField.getAsInt(0) : 2; int maxDimension = Math.max(destWidth, destHeight); switch (resolutionUnit) { case 2: resRational[0][0] = maxDimension; resRational[0][1] = 4L; break;case 3: resRational[0][0] = 100L * maxDimension; resRational[0][1] = 1016L; break;default: resRational[0][0] = 1L; resRational[0][1] = 1L; break; }  }  XResolutionField = new TIFFField(rootIFD.getTag(282), 5, 1, resRational); rootIFD.addTIFFField(XResolutionField); YResolutionField = new TIFFField(rootIFD.getTag(283), 5, 1, resRational); rootIFD.addTIFFField(YResolutionField); } else if (XResolutionField == null && YResolutionField != null) { long[] yResolution = (long[])YResolutionField.getAsRational(0).clone(); XResolutionField = new TIFFField(rootIFD.getTag(282), 5, 1, yResolution); rootIFD.addTIFFField(XResolutionField); } else if (XResolutionField != null && YResolutionField == null) { long[] xResolution = (long[])XResolutionField.getAsRational(0).clone(); YResolutionField = new TIFFField(rootIFD.getTag(283), 5, 1, xResolution); rootIFD.addTIFFField(YResolutionField); }  int width = destWidth; TIFFField imageWidthField = new TIFFField(base.getTag(256), width); rootIFD.addTIFFField(imageWidthField); int height = destHeight; TIFFField imageLengthField = new TIFFField(base.getTag(257), height); rootIFD.addTIFFField(imageLengthField); TIFFField rowsPerStripField = rootIFD.getTIFFField(278); if (rowsPerStripField != null) { rowsPerStrip = rowsPerStripField.getAsInt(0); if (rowsPerStrip < 0) rowsPerStrip = height;  } else { int bitsPerPixel = this.bitDepth * (numBands + numExtraSamples); int bytesPerRow = (bitsPerPixel * width + 7) / 8; rowsPerStrip = Math.max(Math.max(8192 / bytesPerRow, 1), 8); }  int rowsPerStrip = Math.min(rowsPerStrip, height); boolean useTiling = false; int tilingMode = (this.param instanceof TIFFImageWriteParam) ? this.param.getTilingMode() : 1; if (tilingMode == 0 || tilingMode == 1) { this.tileWidth = width; this.tileLength = rowsPerStrip; useTiling = false; } else if (tilingMode == 2) { this.tileWidth = this.param.getTileWidth(); this.tileLength = this.param.getTileHeight(); useTiling = true; } else if (tilingMode == 3) { f = rootIFD.getTIFFField(322); if (f == null) { this.tileWidth = width; useTiling = false; } else { this.tileWidth = f.getAsInt(0); useTiling = true; }  f = rootIFD.getTIFFField(323); if (f == null) { this.tileLength = rowsPerStrip; } else { this.tileLength = f.getAsInt(0); useTiling = true; }  } else { throw new IIOException("Illegal value of tilingMode!"); }  if (this.compression == 7) { int subX; int subY; if (numBands == 1) { subX = subY = 1; } else { subX = subY = 2; }  if (useTiling) { int MCUMultipleX = 8 * subX; int MCUMultipleY = 8 * subY; this.tileWidth = Math.max(MCUMultipleX * (this.tileWidth + MCUMultipleX / 2) / MCUMultipleX, MCUMultipleX); this.tileLength = Math.max(MCUMultipleY * (this.tileLength + MCUMultipleY / 2) / MCUMultipleY, MCUMultipleY); } else if (rowsPerStrip < height) { int MCUMultiple = 8 * Math.max(subX, subY); rowsPerStrip = this.tileLength = Math.max(MCUMultiple * (this.tileLength + MCUMultiple / 2) / MCUMultiple, MCUMultiple); }  } else if (isJPEGInterchange) { this.tileWidth = width; this.tileLength = height; } else if (useTiling) { int tileWidthRemainder = this.tileWidth % 16; if (tileWidthRemainder != 0) this.tileWidth = Math.max(16 * (this.tileWidth + 8) / 16, 16);  int tileLengthRemainder = this.tileLength % 16; if (tileLengthRemainder != 0) this.tileLength = Math.max(16 * (this.tileLength + 8) / 16, 16);  }  this.tilesAcross = (width + this.tileWidth - 1) / this.tileWidth; this.tilesDown = (height + this.tileLength - 1) / this.tileLength; if (!useTiling) { this.isTiled = false; rootIFD.removeTIFFField(322); rootIFD.removeTIFFField(323); rootIFD.removeTIFFField(324); rootIFD.removeTIFFField(325); rowsPerStripField = new TIFFField(base.getTag(278), rowsPerStrip); rootIFD.addTIFFField(rowsPerStripField); TIFFField stripOffsetsField = new TIFFField(base.getTag(273), 4, this.tilesDown); rootIFD.addTIFFField(stripOffsetsField); TIFFField stripByteCountsField = new TIFFField(base.getTag(279), 4, this.tilesDown); rootIFD.addTIFFField(stripByteCountsField); } else { this.isTiled = true; rootIFD.removeTIFFField(278); rootIFD.removeTIFFField(273); rootIFD.removeTIFFField(279); TIFFField tileWidthField = new TIFFField(base.getTag(322), this.tileWidth); rootIFD.addTIFFField(tileWidthField); TIFFField tileLengthField = new TIFFField(base.getTag(323), this.tileLength); rootIFD.addTIFFField(tileLengthField); TIFFField tileOffsetsField = new TIFFField(base.getTag(324), 4, this.tilesDown * this.tilesAcross); rootIFD.addTIFFField(tileOffsetsField); TIFFField tileByteCountsField = new TIFFField(base.getTag(325), 4, this.tilesDown * this.tilesAcross); rootIFD.addTIFFField(tileByteCountsField); }  if (isEXIF) { boolean isPrimaryIFD = isEncodingEmpty(); if (this.compression == 6) { rootIFD.removeTIFFField(256); rootIFD.removeTIFFField(257); rootIFD.removeTIFFField(258); if (isPrimaryIFD) rootIFD.removeTIFFField(259);  rootIFD.removeTIFFField(262); rootIFD.removeTIFFField(273); rootIFD.removeTIFFField(277); rootIFD.removeTIFFField(278); rootIFD.removeTIFFField(279); rootIFD.removeTIFFField(284); if (rootIFD.getTIFFField(296) == null) { f = new TIFFField(base.getTag(296), 2); rootIFD.addTIFFField(f); }  if (isPrimaryIFD) { rootIFD.removeTIFFField(513); rootIFD.removeTIFFField(514); rootIFD.removeTIFFField(530); if (rootIFD.getTIFFField(531) == null) { f = new TIFFField(base.getTag(531), 3, 1, new char[] { '\001' }); rootIFD.addTIFFField(f); }  } else { f = new TIFFField(base.getTag(513), 4, 1); rootIFD.addTIFFField(f); f = new TIFFField(base.getTag(514), 4, 1); rootIFD.addTIFFField(f); rootIFD.removeTIFFField(530); }  } else { if (rootIFD.getTIFFField(296) == null) { f = new TIFFField(base.getTag(296), 2); rootIFD.addTIFFField(f); }  rootIFD.removeTIFFField(513); rootIFD.removeTIFFField(514); if (this.photometricInterpretation == 2) { rootIFD.removeTIFFField(529); rootIFD.removeTIFFField(530); rootIFD.removeTIFFField(531); }  }  EXIFTIFFTagSet eXIFTIFFTagSet = EXIFTIFFTagSet.getInstance(); TIFFIFD exifIFD = null; f = rootIFD.getTIFFField(34665); if (f != null) { exifIFD = (TIFFIFD)f.getData(); } else if (isPrimaryIFD) { List<EXIFTIFFTagSet> exifTagSets = new ArrayList(1); exifTagSets.add(eXIFTIFFTagSet); exifIFD = new TIFFIFD(exifTagSets); EXIFParentTIFFTagSet eXIFParentTIFFTagSet = EXIFParentTIFFTagSet.getInstance(); TIFFTag exifIFDTag = eXIFParentTIFFTagSet.getTag(34665); rootIFD.addTIFFField(new TIFFField(exifIFDTag, 4, 1, exifIFD)); }  if (exifIFD != null) { if (exifIFD.getTIFFField(36864) == null) { f = new TIFFField(eXIFTIFFTagSet.getTag(36864), 7, 4, EXIFTIFFTagSet.EXIF_VERSION_2_2); exifIFD.addTIFFField(f); }  if (this.compression == 6) { if (exifIFD.getTIFFField(37121) == null) { f = new TIFFField(eXIFTIFFTagSet.getTag(37121), 7, 4, new byte[] { 1, 2, 3, 0 }); exifIFD.addTIFFField(f); }  } else { exifIFD.removeTIFFField(37121); exifIFD.removeTIFFField(37122); }  if (exifIFD.getTIFFField(40960) == null) { f = new TIFFField(eXIFTIFFTagSet.getTag(40960), 7, 4, new byte[] { 48, 49, 48, 48 }); exifIFD.addTIFFField(f); }  if (exifIFD.getTIFFField(40961) == null) { f = new TIFFField(eXIFTIFFTagSet.getTag(40961), 3, 1, new char[] { '\001' }); exifIFD.addTIFFField(f); }  if (this.compression == 6) { if (exifIFD.getTIFFField(40962) == null) { f = new TIFFField(eXIFTIFFTagSet.getTag(40962), width); exifIFD.addTIFFField(f); }  if (exifIFD.getTIFFField(40963) == null) { f = new TIFFField(eXIFTIFFTagSet.getTag(40963), height); exifIFD.addTIFFField(f); }  } else { exifIFD.removeTIFFField(40965); }  }  }  }
/*      */   private int writeTile(Rectangle tileRect, TIFFCompressor compressor) throws IOException { Rectangle activeRect; boolean isPadded; Rectangle imageBounds = new Rectangle(this.image.getMinX(), this.image.getMinY(), this.image.getWidth(), this.image.getHeight()); if (!this.isTiled) { activeRect = tileRect.intersection(imageBounds); tileRect = activeRect; isPadded = false; } else if (imageBounds.contains(tileRect)) { activeRect = tileRect; isPadded = false; } else { activeRect = imageBounds.intersection(tileRect); isPadded = true; }  if (activeRect.isEmpty()) return 0;  int minX = tileRect.x; int minY = tileRect.y; int width = tileRect.width; int height = tileRect.height; if (this.isImageSimple) { SampleModel sm = this.image.getSampleModel(); Raster raster = this.image.getData(activeRect); if (isPadded) { WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY, width, height); wr.setRect(raster); raster = wr; }  if (this.isBilevel) { byte[] buf = ImageUtil.getPackedBinaryData(raster, tileRect); if (this.isInverted) { DataBuffer dbb = raster.getDataBuffer(); if (dbb instanceof DataBufferByte && buf == ((DataBufferByte)dbb).getData()) { byte[] bbuf = new byte[buf.length]; int len = buf.length; for (int j = 0; j < len; j++) bbuf[j] = (byte)(buf[j] ^ 0xFF);  buf = bbuf; } else { int len = buf.length; for (int j = 0; j < len; j++) buf[j] = (byte)(buf[j] ^ 0xFF);  }  }  return compressor.encode(buf, 0, width, height, this.sampleSize, (tileRect.width + 7) / 8); }  if (this.bitDepth == 8 && sm.getDataType() == 0) { ComponentSampleModel csm = (ComponentSampleModel)raster.getSampleModel(); byte[] buf = ((DataBufferByte)raster.getDataBuffer()).getData(); int off = csm.getOffset(minX - raster.getSampleModelTranslateX(), minY - raster.getSampleModelTranslateY()); return compressor.encode(buf, off, width, height, this.sampleSize, csm.getScanlineStride()); }  }  int xOffset = minX; int xSkip = this.periodX; int yOffset = minY; int ySkip = this.periodY; int hpixels = (width + xSkip - 1) / xSkip; int vpixels = (height + ySkip - 1) / ySkip; if (hpixels == 0 || vpixels == 0) return 0;  xOffset *= this.numBands; xSkip *= this.numBands; int samplesPerByte = 8 / this.bitDepth; int numSamples = width * this.numBands; int bytesPerRow = hpixels * this.numBands; if (this.bitDepth < 8) { bytesPerRow = (bytesPerRow + samplesPerByte - 1) / samplesPerByte; } else if (this.bitDepth == 16) { bytesPerRow *= 2; } else if (this.bitDepth == 32) { bytesPerRow *= 4; }  int[] samples = null; float[] fsamples = null; if (this.sampleFormat == 3) { fsamples = new float[numSamples]; } else { samples = new int[numSamples]; }  byte[] currTile = new byte[bytesPerRow * vpixels]; if (!this.isInverted && !this.isRescaling && this.sourceBands == null && this.periodX == 1 && this.periodY == 1 && this.colorConverter == null) { SampleModel sm = this.image.getSampleModel(); if (sm instanceof ComponentSampleModel && this.bitDepth == 8 && sm.getDataType() == 0) { Raster raster = this.image.getData(activeRect); if (isPadded) { WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY, width, height); wr.setRect(raster); raster = wr; }  ComponentSampleModel csm = (ComponentSampleModel)raster.getSampleModel(); int[] bankIndices = csm.getBankIndices(); byte[][] bankData = ((DataBufferByte)raster.getDataBuffer()).getBankData(); int lineStride = csm.getScanlineStride(); int pixelStride = csm.getPixelStride(); for (int k = 0; k < this.numBands; k++) { byte[] bandData = bankData[bankIndices[k]]; int lineOffset = csm.getOffset(raster.getMinX() - raster.getSampleModelTranslateX(), raster.getMinY() - raster.getSampleModelTranslateY(), k); int idx = k; for (int j = 0; j < vpixels; j++) { int offset = lineOffset; for (int m = 0; m < hpixels; m++) { currTile[idx] = bandData[offset]; idx += this.numBands; offset += pixelStride; }  lineOffset += lineStride; }  }  return compressor.encode(currTile, 0, width, height, this.sampleSize, width * this.numBands); }  }  int tcount = 0; int activeMinX = activeRect.x; int activeMinY = activeRect.y; int activeMaxY = activeMinY + activeRect.height - 1; int activeWidth = activeRect.width; SampleModel rowSampleModel = null; if (isPadded) rowSampleModel = this.image.getSampleModel().createCompatibleSampleModel(width, 1);  int row; for (row = yOffset; row < yOffset + height; row += ySkip) { int s; Raster ras = null; if (isPadded) { WritableRaster wr = Raster.createWritableRaster(rowSampleModel, new Point(minX, row)); if (row >= activeMinY && row <= activeMaxY) { Rectangle rect = new Rectangle(activeMinX, row, activeWidth, 1); ras = this.image.getData(rect); wr.setRect(ras); }  ras = wr; } else { Rectangle rect = new Rectangle(minX, row, width, 1); ras = this.image.getData(rect); }  if (this.sourceBands != null) ras = ras.createChild(minX, row, width, 1, minX, row, this.sourceBands);  if (this.sampleFormat == 3) { ras.getPixels(minX, row, width, 1, fsamples); } else { ras.getPixels(minX, row, width, 1, samples); if ((this.nativePhotometricInterpretation == 1 && this.photometricInterpretation == 0) || (this.nativePhotometricInterpretation == 0 && this.photometricInterpretation == 1)) { int bitMask = (1 << this.bitDepth) - 1; for (int j = 0; j < numSamples; j++) samples[j] = samples[j] ^ bitMask;  }  }  if (this.colorConverter != null) { int idx = 0; float[] result = new float[3]; if (this.sampleFormat == 3) { for (int j = 0; j < width; j++) { float r = fsamples[idx]; float g = fsamples[idx + 1]; float b = fsamples[idx + 2]; this.colorConverter.fromRGB(r, g, b, result); fsamples[idx] = result[0]; fsamples[idx + 1] = result[1]; fsamples[idx + 2] = result[2]; idx += 3; }  } else { for (int j = 0; j < width; j++) { float r = samples[idx]; float g = samples[idx + 1]; float b = samples[idx + 2]; this.colorConverter.fromRGB(r, g, b, result); samples[idx] = (int)result[0]; samples[idx + 1] = (int)result[1]; samples[idx + 2] = (int)result[2]; idx += 3; }  }  }  int tmp = 0; int pos = 0; switch (this.bitDepth) { case 1: case 2: case 4: if (this.isRescaling) { int j; for (j = 0; j < numSamples; j += xSkip) { byte val = this.scale0[samples[j]]; tmp = tmp << this.bitDepth | val; if (++pos == samplesPerByte) { currTile[tcount++] = (byte)tmp; tmp = 0; pos = 0; }  }  } else { int j; for (j = 0; j < numSamples; j += xSkip) { byte val = (byte)samples[j]; tmp = tmp << this.bitDepth | val; if (++pos == samplesPerByte) { currTile[tcount++] = (byte)tmp; tmp = 0; pos = 0; }  }  }  if (pos != 0) { tmp <<= (8 / this.bitDepth - pos) * this.bitDepth; currTile[tcount++] = (byte)tmp; }  break;case 8: if (this.numBands == 1) { if (this.isRescaling) { int k; for (k = 0; k < numSamples; k += xSkip) currTile[tcount++] = this.scale0[samples[k]];  break; }  int j; for (j = 0; j < numSamples; j += xSkip) currTile[tcount++] = (byte)samples[j];  break; }  if (this.isRescaling) { int j; for (j = 0; j < numSamples; j += xSkip) { for (int b = 0; b < this.numBands; b++) currTile[tcount++] = this.scale[b][samples[j + b]];  }  break; }  for (s = 0; s < numSamples; s += xSkip) { for (int b = 0; b < this.numBands; b++) currTile[tcount++] = (byte)samples[s + b];  }  break;case 16: if (this.isRescaling) { if (this.stream.getByteOrder() == ByteOrder.BIG_ENDIAN) { for (s = 0; s < numSamples; s += xSkip) { for (int b = 0; b < this.numBands; b++) { int sample = samples[s + b]; currTile[tcount++] = this.scaleh[b][sample]; currTile[tcount++] = this.scalel[b][sample]; }  }  break; }  for (s = 0; s < numSamples; s += xSkip) { for (int b = 0; b < this.numBands; b++) { int sample = samples[s + b]; currTile[tcount++] = this.scalel[b][sample]; currTile[tcount++] = this.scaleh[b][sample]; }  }  break; }  if (this.stream.getByteOrder() == ByteOrder.BIG_ENDIAN) { for (s = 0; s < numSamples; s += xSkip) { for (int b = 0; b < this.numBands; b++) { int sample = samples[s + b]; currTile[tcount++] = (byte)(sample >>> 8 & 0xFF); currTile[tcount++] = (byte)(sample & 0xFF); }  }  break; }  for (s = 0; s < numSamples; s += xSkip) { for (int b = 0; b < this.numBands; b++) { int sample = samples[s + b]; currTile[tcount++] = (byte)(sample & 0xFF); currTile[tcount++] = (byte)(sample >>> 8 & 0xFF); }  }  break;case 32: if (this.sampleFormat == 3) { if (this.stream.getByteOrder() == ByteOrder.BIG_ENDIAN) { for (s = 0; s < numSamples; s += xSkip) { for (int b = 0; b < this.numBands; b++) { float fsample = fsamples[s + b]; int isample = Float.floatToIntBits(fsample); currTile[tcount++] = (byte)((isample & 0xFF000000) >> 24); currTile[tcount++] = (byte)((isample & 0xFF0000) >> 16); currTile[tcount++] = (byte)((isample & 0xFF00) >> 8); currTile[tcount++] = (byte)(isample & 0xFF); }  }  break; }  for (s = 0; s < numSamples; s += xSkip) { for (int b = 0; b < this.numBands; b++) { float fsample = fsamples[s + b]; int isample = Float.floatToIntBits(fsample); currTile[tcount++] = (byte)(isample & 0xFF); currTile[tcount++] = (byte)((isample & 0xFF00) >> 8); currTile[tcount++] = (byte)((isample & 0xFF0000) >> 16); currTile[tcount++] = (byte)((isample & 0xFF000000) >> 24); }  }  break; }  if (this.isRescaling) { long[] maxIn = new long[this.numBands]; long[] halfIn = new long[this.numBands]; long maxOut = (1L << (int)this.bitDepth) - 1L; for (int b = 0; b < this.numBands; b++) { maxIn[b] = (1L << (int)this.sampleSize[b]) - 1L; halfIn[b] = maxIn[b] / 2L; }  if (this.stream.getByteOrder() == ByteOrder.BIG_ENDIAN) { int k; for (k = 0; k < numSamples; k += xSkip) { for (int m = 0; m < this.numBands; m++) { long sampleOut = (samples[k + m] * maxOut + halfIn[m]) / maxIn[m]; currTile[tcount++] = (byte)(int)((sampleOut & 0xFFFFFFFFFF000000L) >> 24L); currTile[tcount++] = (byte)(int)((sampleOut & 0xFF0000L) >> 16L); currTile[tcount++] = (byte)(int)((sampleOut & 0xFF00L) >> 8L); currTile[tcount++] = (byte)(int)(sampleOut & 0xFFL); }  }  break; }  int j; for (j = 0; j < numSamples; j += xSkip) { for (int k = 0; k < this.numBands; k++) { long sampleOut = (samples[j + k] * maxOut + halfIn[k]) / maxIn[k]; currTile[tcount++] = (byte)(int)(sampleOut & 0xFFL); currTile[tcount++] = (byte)(int)((sampleOut & 0xFF00L) >> 8L); currTile[tcount++] = (byte)(int)((sampleOut & 0xFF0000L) >> 16L); currTile[tcount++] = (byte)(int)((sampleOut & 0xFFFFFFFFFF000000L) >> 24L); }  }  break; }  if (this.stream.getByteOrder() == ByteOrder.BIG_ENDIAN) { for (s = 0; s < numSamples; s += xSkip) { for (int b = 0; b < this.numBands; b++) { int isample = samples[s + b]; currTile[tcount++] = (byte)((isample & 0xFF000000) >> 24); currTile[tcount++] = (byte)((isample & 0xFF0000) >> 16); currTile[tcount++] = (byte)((isample & 0xFF00) >> 8); currTile[tcount++] = (byte)(isample & 0xFF); }  }  break; }  for (s = 0; s < numSamples; s += xSkip) { for (int b = 0; b < this.numBands; b++) { int isample = samples[s + b]; currTile[tcount++] = (byte)(isample & 0xFF); currTile[tcount++] = (byte)((isample & 0xFF00) >> 8); currTile[tcount++] = (byte)((isample & 0xFF0000) >> 16); currTile[tcount++] = (byte)((isample & 0xFF000000) >> 24); }  }  break; }  }  int[] bitsPerSample = new int[this.numBands]; for (int i = 0; i < bitsPerSample.length; i++) bitsPerSample[i] = this.bitDepth;  int byteCount = compressor.encode(currTile, 0, hpixels, vpixels, bitsPerSample, bytesPerRow); return byteCount; }
/*      */   private boolean equals(int[] s0, int[] s1) { if (s0 == null || s1 == null) return false;  if (s0.length != s1.length) return false;  for (int i = 0; i < s0.length; i++) { if (s0[i] != s1[i]) return false;  }  return true; }
/* 3087 */   private void initializeScaleTables(int[] sampleSize) { if (this.bitDepth == this.scalingBitDepth && equals(sampleSize, this.sampleSize)) return;  this.isRescaling = false; this.scalingBitDepth = -1; this.scale = this.scalel = this.scaleh = (byte[][])null; this.scale0 = null; this.sampleSize = sampleSize; if (this.bitDepth <= 16) for (int b = 0; b < this.numBands; b++) { if (sampleSize[b] != this.bitDepth) { this.isRescaling = true; break; }  }   if (!this.isRescaling) return;  this.scalingBitDepth = this.bitDepth; int maxOutSample = (1 << this.bitDepth) - 1; if (this.bitDepth <= 8) { this.scale = new byte[this.numBands][]; for (int b = 0; b < this.numBands; b++) { int maxInSample = (1 << sampleSize[b]) - 1; int halfMaxInSample = maxInSample / 2; this.scale[b] = new byte[maxInSample + 1]; for (int s = 0; s <= maxInSample; s++) this.scale[b][s] = (byte)((s * maxOutSample + halfMaxInSample) / maxInSample);  }  this.scale0 = this.scale[0]; this.scaleh = this.scalel = (byte[][])null; } else if (this.bitDepth <= 16) { this.scaleh = new byte[this.numBands][]; this.scalel = new byte[this.numBands][]; for (int b = 0; b < this.numBands; b++) { int maxInSample = (1 << sampleSize[b]) - 1; int halfMaxInSample = maxInSample / 2; this.scaleh[b] = new byte[maxInSample + 1]; this.scalel[b] = new byte[maxInSample + 1]; for (int s = 0; s <= maxInSample; s++) { int val = (s * maxOutSample + halfMaxInSample) / maxInSample; this.scaleh[b][s] = (byte)(val >> 8); this.scalel[b][s] = (byte)(val & 0xFF); }  }  this.scale = (byte[][])null; this.scale0 = null; }  } public void write(IIOMetadata sm, IIOImage iioimage, ImageWriteParam p) throws IOException { write(sm, iioimage, p, true, true); } private void writeHeader() throws IOException { if (this.streamMetadata != null) { this.byteOrder = this.streamMetadata.byteOrder; } else { this.byteOrder = ByteOrder.BIG_ENDIAN; }  this.stream.setByteOrder(this.byteOrder); if (this.byteOrder == ByteOrder.BIG_ENDIAN) { this.stream.writeShort(19789); } else { this.stream.writeShort(18761); }  this.stream.writeShort(42); this.stream.writeInt(0); this.nextSpace = this.stream.getStreamPosition(); this.headerPosition = this.nextSpace - 8L; } private void write(IIOMetadata sm, IIOImage iioimage, ImageWriteParam p, boolean writeHeader, boolean writeData) throws IOException { if (this.stream == null) throw new IllegalStateException("output == null!");  if (iioimage == null) throw new IllegalArgumentException("image == null!");  if (iioimage.hasRaster() && !canWriteRasters()) throw new UnsupportedOperationException("TIFF ImageWriter cannot write Rasters!");  this.image = iioimage.getRenderedImage(); SampleModel sampleModel = this.image.getSampleModel(); this.sourceXOffset = this.image.getMinX(); this.sourceYOffset = this.image.getMinY(); this.sourceWidth = this.image.getWidth(); this.sourceHeight = this.image.getHeight(); Rectangle imageBounds = new Rectangle(this.sourceXOffset, this.sourceYOffset, this.sourceWidth, this.sourceHeight); ColorModel colorModel = null; if (p == null) { this.param = getDefaultWriteParam(); this.sourceBands = null; this.periodX = 1; this.periodY = 1; this.numBands = sampleModel.getNumBands(); colorModel = this.image.getColorModel(); } else { this.param = p; Rectangle sourceRegion = this.param.getSourceRegion(); if (sourceRegion != null) { sourceRegion = sourceRegion.intersection(imageBounds); this.sourceXOffset = sourceRegion.x; this.sourceYOffset = sourceRegion.y; this.sourceWidth = sourceRegion.width; this.sourceHeight = sourceRegion.height; }  int gridX = this.param.getSubsamplingXOffset(); int gridY = this.param.getSubsamplingYOffset(); this.sourceXOffset += gridX; this.sourceYOffset += gridY; this.sourceWidth -= gridX; this.sourceHeight -= gridY; this.periodX = this.param.getSourceXSubsampling(); this.periodY = this.param.getSourceYSubsampling(); int[] sBands = this.param.getSourceBands(); if (sBands != null) { this.sourceBands = sBands; this.numBands = this.sourceBands.length; } else { this.numBands = sampleModel.getNumBands(); }  ImageTypeSpecifier destType = p.getDestinationType(); if (destType != null) { ColorModel cm = destType.getColorModel(); if (cm.getNumComponents() == this.numBands) colorModel = cm;  }  if (colorModel == null) colorModel = this.image.getColorModel();  }  this.imageType = new ImageTypeSpecifier(colorModel, sampleModel); ImageUtil.canEncodeImage(this, this.imageType); int destWidth = (this.sourceWidth + this.periodX - 1) / this.periodX; int destHeight = (this.sourceHeight + this.periodY - 1) / this.periodY; if (destWidth <= 0 || destHeight <= 0) throw new IllegalArgumentException("Empty source region!");  clearAbortRequest(); processImageStarted(0); if (writeHeader) { this.streamMetadata = null; if (sm != null) this.streamMetadata = (TIFFStreamMetadata)convertStreamMetadata(sm, this.param);  if (this.streamMetadata == null) this.streamMetadata = (TIFFStreamMetadata)getDefaultStreamMetadata(this.param);  writeHeader(); this.stream.seek(this.headerPosition + 4L); this.nextSpace = this.nextSpace + 3L & 0xFFFFFFFFFFFFFFFCL; this.stream.writeInt((int)this.nextSpace); }  this.imageMetadata = null; IIOMetadata im = iioimage.getMetadata(); if (im != null) if (im instanceof TIFFImageMetadata) { this.imageMetadata = ((TIFFImageMetadata)im).getShallowClone(); } else if (Arrays.<String>asList(im.getMetadataFormatNames()).contains("com_sun_media_imageio_plugins_tiff_image_1.0")) { this.imageMetadata = convertNativeImageMetadata(im); } else if (im.isStandardMetadataFormatSupported()) { try { this.imageMetadata = convertStandardImageMetadata(im); } catch (IIOInvalidTreeException iIOInvalidTreeException) {} }   if (this.imageMetadata == null) this.imageMetadata = (TIFFImageMetadata)getDefaultImageMetadata(this.imageType, this.param);  setupMetadata(colorModel, sampleModel, destWidth, destHeight); this.compressor.setWriter(this); this.compressor.setMetadata(this.imageMetadata); this.compressor.setStream(this.stream); int[] sampleSize = sampleModel.getSampleSize(); initializeScaleTables(sampleModel.getSampleSize()); this.isBilevel = ImageUtil.isBinary(this.image.getSampleModel()); this.isInverted = ((this.nativePhotometricInterpretation == 1 && this.photometricInterpretation == 0) || (this.nativePhotometricInterpretation == 0 && this.photometricInterpretation == 1)); this.isImageSimple = ((this.isBilevel || (!this.isInverted && ImageUtil.imageIsContiguous(this.image))) && !this.isRescaling && this.sourceBands == null && this.periodX == 1 && this.periodY == 1 && this.colorConverter == null); TIFFIFD rootIFD = this.imageMetadata.getRootIFD(); rootIFD.writeToStream(this.stream); this.nextIFDPointerPos = this.stream.getStreamPosition(); this.stream.writeInt(0); long lastIFDPosition = rootIFD.getLastPosition(); this.stream.seek(lastIFDPosition); if (lastIFDPosition > this.nextSpace) this.nextSpace = lastIFDPosition;  if (!writeData) return;  long stripOrTileByteCountsPosition = rootIFD.getStripOrTileByteCountsPosition(); long stripOrTileOffsetsPosition = rootIFD.getStripOrTileOffsetsPosition(); this.totalPixels = this.tileWidth * this.tileLength * this.tilesDown * this.tilesAcross; this.pixelsDone = 0; for (int tj = 0; tj < this.tilesDown; tj++) { for (int ti = 0; ti < this.tilesAcross; ti++) { long pos = this.stream.getStreamPosition(); Rectangle tileRect = new Rectangle(this.sourceXOffset + ti * this.tileWidth * this.periodX, this.sourceYOffset + tj * this.tileLength * this.periodY, this.tileWidth * this.periodX, this.tileLength * this.periodY); try { int byteCount = writeTile(tileRect, this.compressor); if (pos + byteCount > this.nextSpace) this.nextSpace = pos + byteCount;  this.pixelsDone += tileRect.width * tileRect.height; processImageProgress(100.0F * this.pixelsDone / this.totalPixels); this.stream.mark(); this.stream.seek(stripOrTileOffsetsPosition); this.stream.writeInt((int)pos); stripOrTileOffsetsPosition += 4L; this.stream.seek(stripOrTileByteCountsPosition); this.stream.writeInt(byteCount); stripOrTileByteCountsPosition += 4L; this.stream.reset(); } catch (IOException e) { throw new IIOException("I/O error writing TIFF file!", e); }  if (abortRequested()) { processWriteAborted(); return; }  }  }  processImageComplete(); } public boolean canWriteSequence() { return true; } public void prepareReplacePixels(int imageIndex, Rectangle region) throws IOException { synchronized (this.replacePixelsLock)
/*      */     
/* 3089 */     { if (this.stream == null) {
/* 3090 */         throw new IllegalStateException("Output not set!");
/*      */       }
/* 3092 */       if (region == null) {
/* 3093 */         throw new IllegalArgumentException("region == null!");
/*      */       }
/* 3095 */       if (region.getWidth() < 1.0D) {
/* 3096 */         throw new IllegalArgumentException("region.getWidth() < 1!");
/*      */       }
/* 3098 */       if (region.getHeight() < 1.0D) {
/* 3099 */         throw new IllegalArgumentException("region.getHeight() < 1!");
/*      */       }
/* 3101 */       if (this.inReplacePixelsNest) {
/* 3102 */         throw new IllegalStateException("In nested call to prepareReplacePixels!");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 3107 */       TIFFIFD replacePixelsIFD = readIFD(imageIndex);
/*      */ 
/*      */ 
/*      */       
/* 3111 */       TIFFField f = replacePixelsIFD.getTIFFField(259);
/* 3112 */       int compression = f.getAsInt(0);
/* 3113 */       if (compression != 1) {
/* 3114 */         throw new UnsupportedOperationException("canReplacePixels(imageIndex) == false!");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3120 */       f = replacePixelsIFD.getTIFFField(256);
/* 3121 */       if (f == null) {
/* 3122 */         throw new IIOException("Cannot read ImageWidth field.");
/*      */       }
/* 3124 */       int w = f.getAsInt(0);
/*      */ 
/*      */       
/* 3127 */       f = replacePixelsIFD.getTIFFField(257);
/* 3128 */       if (f == null) {
/* 3129 */         throw new IIOException("Cannot read ImageHeight field.");
/*      */       }
/* 3131 */       int h = f.getAsInt(0);
/*      */ 
/*      */       
/* 3134 */       Rectangle bounds = new Rectangle(0, 0, w, h);
/*      */ 
/*      */       
/* 3137 */       region = region.intersection(bounds);
/*      */ 
/*      */       
/* 3140 */       if (region.isEmpty()) {
/* 3141 */         throw new IIOException("Region does not intersect image bounds");
/*      */       }
/*      */ 
/*      */       
/* 3145 */       this.replacePixelsRegion = region;
/*      */ 
/*      */       
/* 3148 */       f = replacePixelsIFD.getTIFFField(324);
/* 3149 */       if (f == null) {
/* 3150 */         f = replacePixelsIFD.getTIFFField(273);
/*      */       }
/* 3152 */       this.replacePixelsTileOffsets = f.getAsLongs();
/*      */ 
/*      */       
/* 3155 */       f = replacePixelsIFD.getTIFFField(325);
/* 3156 */       if (f == null) {
/* 3157 */         f = replacePixelsIFD.getTIFFField(279);
/*      */       }
/* 3159 */       this.replacePixelsByteCounts = f.getAsLongs();
/*      */       
/* 3161 */       this
/* 3162 */         .replacePixelsOffsetsPosition = replacePixelsIFD.getStripOrTileOffsetsPosition();
/* 3163 */       this
/* 3164 */         .replacePixelsByteCountsPosition = replacePixelsIFD.getStripOrTileByteCountsPosition();
/*      */ 
/*      */       
/* 3167 */       this.replacePixelsMetadata = new TIFFImageMetadata(replacePixelsIFD);
/*      */ 
/*      */       
/* 3170 */       this.replacePixelsIndex = imageIndex;
/*      */ 
/*      */       
/* 3173 */       this.inReplacePixelsNest = true; }  } public void prepareWriteSequence(IIOMetadata streamMetadata) throws IOException { if (getOutput() == null) throw new IllegalStateException("getOutput() == null!");  if (streamMetadata != null) streamMetadata = convertStreamMetadata(streamMetadata, (ImageWriteParam)null);  if (streamMetadata == null) streamMetadata = getDefaultStreamMetadata((ImageWriteParam)null);  this.streamMetadata = (TIFFStreamMetadata)streamMetadata; writeHeader(); this.isWritingSequence = true; } public void writeToSequence(IIOImage image, ImageWriteParam param) throws IOException { if (!this.isWritingSequence) throw new IllegalStateException("prepareWriteSequence() has not been called!");  writeInsert(-1, image, param); } public void endWriteSequence() throws IOException { if (getOutput() == null) throw new IllegalStateException("getOutput() == null!");  if (!this.isWritingSequence) throw new IllegalStateException("prepareWriteSequence() has not been called!");  this.isWritingSequence = false; } public boolean canInsertImage(int imageIndex) throws IOException { if (getOutput() == null) throw new IllegalStateException("getOutput() == null!");  this.stream.mark(); long[] ifdpos = new long[1]; long[] ifd = new long[1]; locateIFD(imageIndex, ifdpos, ifd); this.stream.reset(); return true; } private void locateIFD(int imageIndex, long[] ifdpos, long[] ifd) throws IOException { if (imageIndex < -1) throw new IndexOutOfBoundsException("imageIndex < -1!");  long startPos = this.stream.getStreamPosition(); this.stream.seek(this.headerPosition); int byteOrder = this.stream.readUnsignedShort(); if (byteOrder == 19789) { this.stream.setByteOrder(ByteOrder.BIG_ENDIAN); } else if (byteOrder == 18761) { this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN); } else { this.stream.seek(startPos); throw new IIOException("Illegal byte order"); }  if (this.stream.readUnsignedShort() != 42) { this.stream.seek(startPos); throw new IIOException("Illegal magic number"); }  ifdpos[0] = this.stream.getStreamPosition(); ifd[0] = this.stream.readUnsignedInt(); if (ifd[0] == 0L) { if (imageIndex > 0) { this.stream.seek(startPos); throw new IndexOutOfBoundsException("imageIndex is greater than the largest available index!"); }  return; }  this.stream.seek(ifd[0]); for (int i = 0; imageIndex == -1 || i < imageIndex; i++) { int numFields; try { numFields = this.stream.readShort(); } catch (EOFException eof) { this.stream.seek(startPos); ifd[0] = 0L; return; }  this.stream.skipBytes(12 * numFields); ifdpos[0] = this.stream.getStreamPosition(); ifd[0] = this.stream.readUnsignedInt(); if (ifd[0] == 0L) { if (imageIndex != -1 && i < imageIndex - 1) { this.stream.seek(startPos); throw new IndexOutOfBoundsException("imageIndex is greater than the largest available index!"); }  break; }  this.stream.seek(ifd[0]); }  } public void writeInsert(int imageIndex, IIOImage image, ImageWriteParam param) throws IOException { insert(imageIndex, image, param, true); } private void insert(int imageIndex, IIOImage image, ImageWriteParam param, boolean writeData) throws IOException { if (this.stream == null) throw new IllegalStateException("Output not set!");  if (image == null) throw new IllegalArgumentException("image == null!");  long[] ifdpos = new long[1]; long[] ifd = new long[1]; locateIFD(imageIndex, ifdpos, ifd); this.stream.seek(ifdpos[0]); if (ifdpos[0] + 4L > this.nextSpace) this.nextSpace = ifdpos[0] + 4L;  this.nextSpace = this.nextSpace + 3L & 0xFFFFFFFFFFFFFFFCL; this.stream.writeInt((int)this.nextSpace); this.stream.seek(this.nextSpace); write((IIOMetadata)null, image, param, false, writeData); this.stream.seek(this.nextIFDPointerPos); this.stream.writeInt((int)ifd[0]); } private boolean isEncodingEmpty() { return (this.isInsertingEmpty || this.isWritingEmpty); }
/*      */   public boolean canInsertEmpty(int imageIndex) throws IOException { return canInsertImage(imageIndex); }
/*      */   public boolean canWriteEmpty() throws IOException { if (getOutput() == null) throw new IllegalStateException("getOutput() == null!");  return true; }
/*      */   private void checkParamsEmpty(ImageTypeSpecifier imageType, int width, int height, List thumbnails) { if (getOutput() == null) throw new IllegalStateException("getOutput() == null!");  if (imageType == null) throw new IllegalArgumentException("imageType == null!");  if (width < 1 || height < 1) throw new IllegalArgumentException("width < 1 || height < 1!");  if (thumbnails != null) { int numThumbs = thumbnails.size(); for (int i = 0; i < numThumbs; i++) { Object thumb = thumbnails.get(i); if (thumb == null || !(thumb instanceof BufferedImage)) throw new IllegalArgumentException("thumbnails contains null references or objects other than BufferedImages!");  }  }  if (this.isInsertingEmpty) throw new IllegalStateException("Previous call to prepareInsertEmpty() without corresponding call to endInsertEmpty()!");  if (this.isWritingEmpty) throw new IllegalStateException("Previous call to prepareWriteEmpty() without corresponding call to endWriteEmpty()!");  }
/*      */   public void prepareInsertEmpty(int imageIndex, ImageTypeSpecifier imageType, int width, int height, IIOMetadata imageMetadata, List thumbnails, ImageWriteParam param) throws IOException { checkParamsEmpty(imageType, width, height, thumbnails); this.isInsertingEmpty = true; SampleModel emptySM = imageType.getSampleModel(); EmptyImage emptyImage = new EmptyImage(0, 0, width, height, 0, 0, emptySM.getWidth(), emptySM.getHeight(), emptySM, imageType.getColorModel()); insert(imageIndex, new IIOImage((RenderedImage)emptyImage, null, imageMetadata), param, false); }
/*      */   public void prepareWriteEmpty(IIOMetadata streamMetadata, ImageTypeSpecifier imageType, int width, int height, IIOMetadata imageMetadata, List thumbnails, ImageWriteParam param) throws IOException { checkParamsEmpty(imageType, width, height, thumbnails); this.isWritingEmpty = true; SampleModel emptySM = imageType.getSampleModel(); EmptyImage emptyImage = new EmptyImage(0, 0, width, height, 0, 0, emptySM.getWidth(), emptySM.getHeight(), emptySM, imageType.getColorModel()); write(streamMetadata, new IIOImage((RenderedImage)emptyImage, null, imageMetadata), param, true, false); }
/*      */   public void endInsertEmpty() throws IOException { if (getOutput() == null) throw new IllegalStateException("getOutput() == null!");  if (!this.isInsertingEmpty) throw new IllegalStateException("No previous call to prepareInsertEmpty()!");  if (this.isWritingEmpty) throw new IllegalStateException("Previous call to prepareWriteEmpty() without corresponding call to endWriteEmpty()!");  if (this.inReplacePixelsNest) throw new IllegalStateException("In nested call to prepareReplacePixels!");  this.isInsertingEmpty = false; }
/*      */   public void endWriteEmpty() throws IOException { if (getOutput() == null) throw new IllegalStateException("getOutput() == null!");  if (!this.isWritingEmpty) throw new IllegalStateException("No previous call to prepareWriteEmpty()!");  if (this.isInsertingEmpty) throw new IllegalStateException("Previous call to prepareInsertEmpty() without corresponding call to endInsertEmpty()!");  if (this.inReplacePixelsNest) throw new IllegalStateException("In nested call to prepareReplacePixels!");  this.isWritingEmpty = false; }
/*      */   private TIFFIFD readIFD(int imageIndex) throws IOException { if (this.stream == null) throw new IllegalStateException("Output not set!");  if (imageIndex < 0) throw new IndexOutOfBoundsException("imageIndex < 0!");  this.stream.mark(); long[] ifdpos = new long[1]; long[] ifd = new long[1]; locateIFD(imageIndex, ifdpos, ifd); if (ifd[0] == 0L) { this.stream.reset(); throw new IndexOutOfBoundsException("imageIndex out of bounds!"); }  List<BaselineTIFFTagSet> tagSets = new ArrayList(1); tagSets.add(BaselineTIFFTagSet.getInstance()); TIFFIFD rootIFD = new TIFFIFD(tagSets); rootIFD.initialize(this.stream, true); this.stream.reset(); return rootIFD; }
/*      */   public boolean canReplacePixels(int imageIndex) throws IOException { if (getOutput() == null) throw new IllegalStateException("getOutput() == null!");  TIFFIFD rootIFD = readIFD(imageIndex); TIFFField f = rootIFD.getTIFFField(259); int compression = f.getAsInt(0); return (compression == 1); }
/* 3183 */   private Raster subsample(Raster raster, int[] sourceBands, int subOriginX, int subOriginY, int subPeriodX, int subPeriodY, int dstOffsetX, int dstOffsetY, Rectangle target) { int x = raster.getMinX();
/* 3184 */     int y = raster.getMinY();
/* 3185 */     int w = raster.getWidth();
/* 3186 */     int h = raster.getHeight();
/* 3187 */     int b = raster.getSampleModel().getNumBands();
/* 3188 */     int t = raster.getSampleModel().getDataType();
/*      */     
/* 3190 */     int outMinX = XToTileX(x, subOriginX, subPeriodX) + dstOffsetX;
/* 3191 */     int outMinY = YToTileY(y, subOriginY, subPeriodY) + dstOffsetY;
/* 3192 */     int outMaxX = XToTileX(x + w - 1, subOriginX, subPeriodX) + dstOffsetX;
/* 3193 */     int outMaxY = YToTileY(y + h - 1, subOriginY, subPeriodY) + dstOffsetY;
/* 3194 */     int outWidth = outMaxX - outMinX + 1;
/* 3195 */     int outHeight = outMaxY - outMinY + 1;
/*      */     
/* 3197 */     if (outWidth <= 0 || outHeight <= 0) return null;
/*      */     
/* 3199 */     int inMinX = (outMinX - dstOffsetX) * subPeriodX + subOriginX;
/* 3200 */     int inMaxX = (outMaxX - dstOffsetX) * subPeriodX + subOriginX;
/* 3201 */     int inWidth = inMaxX - inMinX + 1;
/* 3202 */     int inMinY = (outMinY - dstOffsetY) * subPeriodY + subOriginY;
/* 3203 */     int inMaxY = (outMaxY - dstOffsetY) * subPeriodY + subOriginY;
/* 3204 */     int inHeight = inMaxY - inMinY + 1;
/*      */ 
/*      */     
/* 3207 */     WritableRaster wr = raster.createCompatibleWritableRaster(outMinX, outMinY, outWidth, outHeight);
/*      */ 
/*      */     
/* 3210 */     int jMax = inMinY + inHeight;
/*      */     
/* 3212 */     if (t == 4 || t == 5) {
/* 3213 */       float[] fsamples = new float[inWidth];
/* 3214 */       float[] fsubsamples = new float[outWidth];
/*      */       
/* 3216 */       for (int k = 0; k < b; k++) {
/* 3217 */         int outY = outMinY; int j;
/* 3218 */         for (j = inMinY; j < jMax; j += subPeriodY) {
/* 3219 */           raster.getSamples(inMinX, j, inWidth, 1, k, fsamples);
/* 3220 */           int s = 0; int i;
/* 3221 */           for (i = 0; i < inWidth; i += subPeriodX) {
/* 3222 */             fsubsamples[s++] = fsamples[i];
/*      */           }
/* 3224 */           wr.setSamples(outMinX, outY++, outWidth, 1, k, fsubsamples);
/*      */         } 
/*      */       } 
/*      */     } else {
/*      */       
/* 3229 */       int[] samples = new int[inWidth];
/* 3230 */       int[] subsamples = new int[outWidth];
/*      */       
/* 3232 */       for (int k = 0; k < b; k++) {
/* 3233 */         int outY = outMinY; int j;
/* 3234 */         for (j = inMinY; j < jMax; j += subPeriodY) {
/* 3235 */           raster.getSamples(inMinX, j, inWidth, 1, k, samples);
/* 3236 */           int s = 0; int i;
/* 3237 */           for (i = 0; i < inWidth; i += subPeriodX) {
/* 3238 */             subsamples[s++] = samples[i];
/*      */           }
/* 3240 */           wr.setSamples(outMinX, outY++, outWidth, 1, k, subsamples);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 3246 */     return wr.createChild(outMinX, outMinY, target.width, target.height, target.x, target.y, sourceBands); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void replacePixels(RenderedImage image, ImageWriteParam param) throws IOException {
/* 3255 */     synchronized (this.replacePixelsLock) {
/*      */       
/* 3257 */       if (this.stream == null) {
/* 3258 */         throw new IllegalStateException("stream == null!");
/*      */       }
/*      */       
/* 3261 */       if (image == null) {
/* 3262 */         throw new IllegalArgumentException("image == null!");
/*      */       }
/*      */       
/* 3265 */       if (!this.inReplacePixelsNest) {
/* 3266 */         throw new IllegalStateException("No previous call to prepareReplacePixels!");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 3271 */       int stepX = 1, stepY = 1, gridX = 0, gridY = 0;
/*      */ 
/*      */       
/* 3274 */       if (param == null) {
/*      */         
/* 3276 */         param = getDefaultWriteParam();
/*      */       } else {
/*      */         
/* 3279 */         ImageWriteParam paramCopy = getDefaultWriteParam();
/*      */ 
/*      */         
/* 3282 */         paramCopy.setCompressionMode(0);
/*      */ 
/*      */         
/* 3285 */         paramCopy.setTilingMode(3);
/*      */ 
/*      */         
/* 3288 */         paramCopy.setDestinationOffset(param.getDestinationOffset());
/* 3289 */         paramCopy.setSourceBands(param.getSourceBands());
/* 3290 */         paramCopy.setSourceRegion(param.getSourceRegion());
/*      */ 
/*      */ 
/*      */         
/* 3294 */         stepX = param.getSourceXSubsampling();
/* 3295 */         stepY = param.getSourceYSubsampling();
/* 3296 */         gridX = param.getSubsamplingXOffset();
/* 3297 */         gridY = param.getSubsamplingYOffset();
/*      */ 
/*      */         
/* 3300 */         param = paramCopy;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 3305 */       TIFFField f = this.replacePixelsMetadata.getTIFFField(258);
/* 3306 */       if (f == null) {
/* 3307 */         throw new IIOException("Cannot read destination BitsPerSample");
/*      */       }
/*      */       
/* 3310 */       int[] dstBitsPerSample = f.getAsInts();
/* 3311 */       int[] srcBitsPerSample = image.getSampleModel().getSampleSize();
/* 3312 */       int[] sourceBands = param.getSourceBands();
/* 3313 */       if (sourceBands != null) {
/* 3314 */         if (sourceBands.length != dstBitsPerSample.length) {
/* 3315 */           throw new IIOException("Source and destination have different SamplesPerPixel");
/*      */         }
/*      */         
/* 3318 */         for (int i = 0; i < sourceBands.length; i++) {
/* 3319 */           if (dstBitsPerSample[i] != srcBitsPerSample[sourceBands[i]])
/*      */           {
/* 3321 */             throw new IIOException("Source and destination have different BitsPerSample");
/*      */           }
/*      */         } 
/*      */       } else {
/*      */         
/* 3326 */         int srcNumBands = image.getSampleModel().getNumBands();
/* 3327 */         if (srcNumBands != dstBitsPerSample.length) {
/* 3328 */           throw new IIOException("Source and destination have different SamplesPerPixel");
/*      */         }
/*      */         
/* 3331 */         for (int i = 0; i < srcNumBands; i++) {
/* 3332 */           if (dstBitsPerSample[i] != srcBitsPerSample[i]) {
/* 3333 */             throw new IIOException("Source and destination have different BitsPerSample");
/*      */           }
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3342 */       Rectangle srcImageBounds = new Rectangle(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight());
/*      */ 
/*      */       
/* 3345 */       Rectangle srcRect = param.getSourceRegion();
/* 3346 */       if (srcRect == null) {
/* 3347 */         srcRect = srcImageBounds;
/*      */       }
/*      */ 
/*      */       
/* 3351 */       int subPeriodX = stepX;
/* 3352 */       int subPeriodY = stepY;
/* 3353 */       int subOriginX = gridX + srcRect.x;
/* 3354 */       int subOriginY = gridY + srcRect.y;
/*      */ 
/*      */       
/* 3357 */       if (!srcRect.equals(srcImageBounds)) {
/* 3358 */         srcRect = srcRect.intersection(srcImageBounds);
/* 3359 */         if (srcRect.isEmpty()) {
/* 3360 */           throw new IllegalArgumentException("Source region does not intersect source image!");
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 3366 */       Point dstOffset = param.getDestinationOffset();
/*      */ 
/*      */       
/* 3369 */       int dMinX = XToTileX(srcRect.x, subOriginX, subPeriodX) + dstOffset.x;
/*      */       
/* 3371 */       int dMinY = YToTileY(srcRect.y, subOriginY, subPeriodY) + dstOffset.y;
/*      */       
/* 3373 */       int dMaxX = XToTileX(srcRect.x + srcRect.width, subOriginX, subPeriodX) + dstOffset.x;
/*      */       
/* 3375 */       int dMaxY = YToTileY(srcRect.y + srcRect.height, subOriginY, subPeriodY) + dstOffset.y;
/*      */ 
/*      */ 
/*      */       
/* 3379 */       Rectangle dstRect = new Rectangle(dstOffset.x, dstOffset.y, dMaxX - dMinX, dMaxY - dMinY);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3384 */       dstRect = dstRect.intersection(this.replacePixelsRegion);
/* 3385 */       if (dstRect.isEmpty()) {
/* 3386 */         throw new IllegalArgumentException("Forward mapped source region does not intersect destination region!");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 3391 */       int activeSrcMinX = (dstRect.x - dstOffset.x) * subPeriodX + subOriginX;
/*      */       
/* 3393 */       int sxmax = (dstRect.x + dstRect.width - 1 - dstOffset.x) * subPeriodX + subOriginX;
/*      */ 
/*      */       
/* 3396 */       int activeSrcWidth = sxmax - activeSrcMinX + 1;
/*      */       
/* 3398 */       int activeSrcMinY = (dstRect.y - dstOffset.y) * subPeriodY + subOriginY;
/*      */       
/* 3400 */       int symax = (dstRect.y + dstRect.height - 1 - dstOffset.y) * subPeriodY + subOriginY;
/*      */ 
/*      */       
/* 3403 */       int activeSrcHeight = symax - activeSrcMinY + 1;
/* 3404 */       Rectangle activeSrcRect = new Rectangle(activeSrcMinX, activeSrcMinY, activeSrcWidth, activeSrcHeight);
/*      */ 
/*      */       
/* 3407 */       if (activeSrcRect.intersection(srcImageBounds).isEmpty()) {
/* 3408 */         throw new IllegalArgumentException("Backward mapped destination region does not intersect source image!");
/*      */       }
/*      */ 
/*      */       
/* 3412 */       if (this.reader == null) {
/* 3413 */         this.reader = new TIFFImageReader(new TIFFImageReaderSpi());
/*      */       } else {
/* 3415 */         this.reader.reset();
/*      */       } 
/*      */       
/* 3418 */       this.stream.mark();
/*      */       
/*      */       try {
/* 3421 */         this.stream.seek(this.headerPosition);
/* 3422 */         this.reader.setInput(this.stream);
/*      */         
/* 3424 */         this.imageMetadata = this.replacePixelsMetadata;
/* 3425 */         this.param = param;
/* 3426 */         SampleModel sm = image.getSampleModel();
/* 3427 */         ColorModel cm = image.getColorModel();
/* 3428 */         this.numBands = sm.getNumBands();
/* 3429 */         this.imageType = new ImageTypeSpecifier(image);
/* 3430 */         this.periodX = param.getSourceXSubsampling();
/* 3431 */         this.periodY = param.getSourceYSubsampling();
/* 3432 */         this.sourceBands = null;
/* 3433 */         int[] sBands = param.getSourceBands();
/* 3434 */         if (sBands != null) {
/* 3435 */           this.sourceBands = sBands;
/* 3436 */           this.numBands = sourceBands.length;
/*      */         } 
/* 3438 */         setupMetadata(cm, sm, this.reader
/* 3439 */             .getWidth(this.replacePixelsIndex), this.reader
/* 3440 */             .getHeight(this.replacePixelsIndex));
/* 3441 */         int[] scaleSampleSize = sm.getSampleSize();
/* 3442 */         initializeScaleTables(scaleSampleSize);
/*      */ 
/*      */         
/* 3445 */         this.isBilevel = ImageUtil.isBinary(image.getSampleModel());
/*      */ 
/*      */         
/* 3448 */         this.isInverted = ((this.nativePhotometricInterpretation == 1 && this.photometricInterpretation == 0) || (this.nativePhotometricInterpretation == 0 && this.photometricInterpretation == 1));
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
/* 3459 */         this
/*      */           
/* 3461 */           .isImageSimple = ((this.isBilevel || (!this.isInverted && ImageUtil.imageIsContiguous(image))) && !this.isRescaling && sourceBands == null && this.periodX == 1 && this.periodY == 1 && this.colorConverter == null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3467 */         int minTileX = XToTileX(dstRect.x, 0, this.tileWidth);
/* 3468 */         int minTileY = YToTileY(dstRect.y, 0, this.tileLength);
/* 3469 */         int maxTileX = XToTileX(dstRect.x + dstRect.width - 1, 0, this.tileWidth);
/*      */         
/* 3471 */         int maxTileY = YToTileY(dstRect.y + dstRect.height - 1, 0, this.tileLength);
/*      */ 
/*      */         
/* 3474 */         TIFFCompressor encoder = new TIFFNullCompressor();
/* 3475 */         encoder.setWriter(this);
/* 3476 */         encoder.setStream(this.stream);
/* 3477 */         encoder.setMetadata(this.imageMetadata);
/*      */         
/* 3479 */         Rectangle tileRect = new Rectangle();
/* 3480 */         for (int ty = minTileY; ty <= maxTileY; ty++) {
/* 3481 */           for (int tx = minTileX; tx <= maxTileX; tx++) {
/* 3482 */             int tileIndex = ty * this.tilesAcross + tx;
/* 3483 */             boolean isEmpty = (this.replacePixelsByteCounts[tileIndex] == 0L);
/*      */ 
/*      */             
/* 3486 */             if (isEmpty) {
/*      */               
/* 3488 */               SampleModel tileSM = sm.createCompatibleSampleModel(this.tileWidth, this.tileLength);
/*      */               
/* 3490 */               raster = Raster.createWritableRaster(tileSM, null);
/*      */             } else {
/*      */               
/* 3493 */               BufferedImage tileImage = this.reader.readTile(this.replacePixelsIndex, tx, ty);
/* 3494 */               raster = tileImage.getRaster();
/*      */             } 
/*      */             
/* 3497 */             tileRect.setLocation(tx * this.tileWidth, ty * this.tileLength);
/*      */             
/* 3499 */             tileRect.setSize(raster.getWidth(), raster
/* 3500 */                 .getHeight());
/*      */             
/* 3502 */             WritableRaster raster = raster.createWritableTranslatedChild(tileRect.x, tileRect.y);
/*      */ 
/*      */ 
/*      */             
/* 3506 */             Rectangle replacementRect = tileRect.intersection(dstRect);
/*      */             
/* 3508 */             int srcMinX = (replacementRect.x - dstOffset.x) * subPeriodX + subOriginX;
/*      */ 
/*      */             
/* 3511 */             int srcXmax = (replacementRect.x + replacementRect.width - 1 - dstOffset.x) * subPeriodX + subOriginX;
/*      */ 
/*      */             
/* 3514 */             int srcWidth = srcXmax - srcMinX + 1;
/*      */             
/* 3516 */             int srcMinY = (replacementRect.y - dstOffset.y) * subPeriodY + subOriginY;
/*      */ 
/*      */             
/* 3519 */             int srcYMax = (replacementRect.y + replacementRect.height - 1 - dstOffset.y) * subPeriodY + subOriginY;
/*      */ 
/*      */             
/* 3522 */             int srcHeight = srcYMax - srcMinY + 1;
/* 3523 */             Rectangle srcTileRect = new Rectangle(srcMinX, srcMinY, srcWidth, srcHeight);
/*      */ 
/*      */ 
/*      */             
/* 3527 */             Raster replacementData = image.getData(srcTileRect);
/* 3528 */             if (subPeriodX == 1 && subPeriodY == 1 && subOriginX == 0 && subOriginY == 0) {
/*      */ 
/*      */               
/* 3531 */               replacementData = replacementData.createChild(srcTileRect.x, srcTileRect.y, srcTileRect.width, srcTileRect.height, replacementRect.x, replacementRect.y, sourceBands);
/*      */ 
/*      */             
/*      */             }
/*      */             else {
/*      */ 
/*      */ 
/*      */               
/* 3539 */               replacementData = subsample(replacementData, sourceBands, subOriginX, subOriginY, subPeriodX, subPeriodY, dstOffset.x, dstOffset.y, replacementRect);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 3548 */               if (replacementData == null) {
/*      */                 continue;
/*      */               }
/*      */             } 
/*      */             
/* 3553 */             raster.setRect(replacementData);
/*      */             
/* 3555 */             if (isEmpty) {
/* 3556 */               this.stream.seek(this.nextSpace);
/*      */             } else {
/* 3558 */               this.stream.seek(this.replacePixelsTileOffsets[tileIndex]);
/*      */             } 
/*      */             
/* 3561 */             this.image = (RenderedImage)new SingleTileRenderedImage(raster, cm);
/*      */             
/* 3563 */             int numBytes = writeTile(tileRect, encoder);
/*      */             
/* 3565 */             if (isEmpty) {
/*      */ 
/*      */               
/* 3568 */               this.stream.mark();
/* 3569 */               this.stream.seek(this.replacePixelsOffsetsPosition + (4 * tileIndex));
/*      */               
/* 3571 */               this.stream.writeInt((int)this.nextSpace);
/* 3572 */               this.stream.seek(this.replacePixelsByteCountsPosition + (4 * tileIndex));
/*      */               
/* 3574 */               this.stream.writeInt(numBytes);
/* 3575 */               this.stream.reset();
/*      */ 
/*      */               
/* 3578 */               this.nextSpace += numBytes;
/*      */             } 
/*      */             continue;
/*      */           } 
/*      */         } 
/* 3583 */       } catch (IOException e) {
/* 3584 */         throw e;
/*      */       } finally {
/* 3586 */         this.stream.reset();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void replacePixels(Raster raster, ImageWriteParam param) throws IOException {
/* 3593 */     if (raster == null) {
/* 3594 */       throw new IllegalArgumentException("raster == null!");
/*      */     }
/*      */     
/* 3597 */     replacePixels((RenderedImage)new SingleTileRenderedImage(raster, this.image
/* 3598 */           .getColorModel()), param);
/*      */   }
/*      */ 
/*      */   
/*      */   public void endReplacePixels() throws IOException {
/* 3603 */     synchronized (this.replacePixelsLock) {
/* 3604 */       if (!this.inReplacePixelsNest) {
/* 3605 */         throw new IllegalStateException("No previous call to prepareReplacePixels()!");
/*      */       }
/*      */       
/* 3608 */       this.replacePixelsIndex = -1;
/* 3609 */       this.replacePixelsMetadata = null;
/* 3610 */       this.replacePixelsTileOffsets = null;
/* 3611 */       this.replacePixelsByteCounts = null;
/* 3612 */       this.replacePixelsOffsetsPosition = 0L;
/* 3613 */       this.replacePixelsByteCountsPosition = 0L;
/* 3614 */       this.replacePixelsRegion = null;
/* 3615 */       this.inReplacePixelsNest = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void reset() {
/* 3622 */     super.reset();
/*      */     
/* 3624 */     this.stream = null;
/* 3625 */     this.image = null;
/* 3626 */     this.imageType = null;
/* 3627 */     this.byteOrder = null;
/* 3628 */     this.param = null;
/* 3629 */     if (this.compressor != null) {
/* 3630 */       this.compressor.dispose();
/*      */     }
/* 3632 */     this.compressor = null;
/* 3633 */     this.colorConverter = null;
/* 3634 */     this.streamMetadata = null;
/* 3635 */     this.imageMetadata = null;
/*      */     
/* 3637 */     this.isWritingSequence = false;
/* 3638 */     this.isWritingEmpty = false;
/* 3639 */     this.isInsertingEmpty = false;
/*      */     
/* 3641 */     this.replacePixelsIndex = -1;
/* 3642 */     this.replacePixelsMetadata = null;
/* 3643 */     this.replacePixelsTileOffsets = null;
/* 3644 */     this.replacePixelsByteCounts = null;
/* 3645 */     this.replacePixelsOffsetsPosition = 0L;
/* 3646 */     this.replacePixelsByteCountsPosition = 0L;
/* 3647 */     this.replacePixelsRegion = null;
/* 3648 */     this.inReplacePixelsNest = false;
/*      */   }
/*      */   
/*      */   public void dispose() {
/* 3652 */     reset();
/* 3653 */     super.dispose();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFImageWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */