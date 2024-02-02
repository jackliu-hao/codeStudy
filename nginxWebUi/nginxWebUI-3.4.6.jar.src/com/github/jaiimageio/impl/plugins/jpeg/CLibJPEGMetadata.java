/*      */ package com.github.jaiimageio.impl.plugins.jpeg;
/*      */ 
/*      */ import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
/*      */ import com.github.jaiimageio.plugins.tiff.EXIFParentTIFFTagSet;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFDirectory;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*      */ import com.github.jaiimageio.plugins.tiff.TIFFTagSet;
/*      */ import java.awt.Point;
/*      */ import java.awt.color.ColorSpace;
/*      */ import java.awt.color.ICC_Profile;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ComponentColorModel;
/*      */ import java.awt.image.DataBufferByte;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.nio.ByteOrder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import javax.imageio.IIOException;
/*      */ import javax.imageio.IIOImage;
/*      */ import javax.imageio.ImageIO;
/*      */ import javax.imageio.ImageReader;
/*      */ import javax.imageio.metadata.IIOInvalidTreeException;
/*      */ import javax.imageio.metadata.IIOMetadata;
/*      */ import javax.imageio.metadata.IIOMetadataNode;
/*      */ import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
/*      */ import javax.imageio.plugins.jpeg.JPEGQTable;
/*      */ import javax.imageio.stream.ImageInputStream;
/*      */ import javax.imageio.stream.MemoryCacheImageInputStream;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class CLibJPEGMetadata
/*      */   extends IIOMetadata
/*      */ {
/*      */   static final String NATIVE_FORMAT = "javax_imageio_jpeg_image_1.0";
/*      */   static final String NATIVE_FORMAT_CLASS = "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat";
/*      */   static final String TIFF_FORMAT = "com_sun_media_imageio_plugins_tiff_image_1.0";
/*      */   static final String TIFF_FORMAT_CLASS = "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat";
/*      */   static final int TEM = 1;
/*      */   static final int SOF0 = 192;
/*      */   static final int SOF1 = 193;
/*      */   static final int SOF2 = 194;
/*      */   static final int SOF3 = 195;
/*      */   static final int DHT = 196;
/*      */   static final int SOF5 = 197;
/*      */   static final int SOF6 = 198;
/*      */   static final int SOF7 = 199;
/*      */   static final int JPG = 200;
/*      */   static final int SOF9 = 201;
/*      */   static final int SOF10 = 202;
/*      */   static final int SOF11 = 203;
/*      */   static final int DAC = 204;
/*      */   static final int SOF13 = 205;
/*      */   static final int SOF14 = 206;
/*      */   static final int SOF15 = 207;
/*      */   static final int RST0 = 208;
/*      */   static final int RST1 = 209;
/*      */   static final int RST2 = 210;
/*      */   static final int RST3 = 211;
/*      */   static final int RST4 = 212;
/*      */   static final int RST5 = 213;
/*      */   static final int RST6 = 214;
/*      */   static final int RST7 = 215;
/*      */   static final int RESTART_RANGE = 8;
/*      */   static final int SOI = 216;
/*      */   static final int EOI = 217;
/*      */   static final int SOS = 218;
/*      */   static final int DQT = 219;
/*      */   static final int DNL = 220;
/*      */   static final int DRI = 221;
/*      */   static final int DHP = 222;
/*      */   static final int EXP = 223;
/*      */   static final int APP0 = 224;
/*      */   static final int APP1 = 225;
/*      */   static final int APP2 = 226;
/*      */   static final int APP3 = 227;
/*      */   static final int APP4 = 228;
/*      */   static final int APP5 = 229;
/*      */   static final int APP6 = 230;
/*      */   static final int APP7 = 231;
/*      */   static final int APP8 = 232;
/*      */   static final int APP9 = 233;
/*      */   static final int APP10 = 234;
/*      */   static final int APP11 = 235;
/*      */   static final int APP12 = 236;
/*      */   static final int APP13 = 237;
/*      */   static final int APP14 = 238;
/*      */   static final int APP15 = 239;
/*      */   static final int COM = 254;
/*      */   static final int SOF55 = 247;
/*      */   static final int LSE = 242;
/*      */   static final int APPN_MIN = 224;
/*      */   static final int APPN_MAX = 239;
/*      */   static final int SOFN_MIN = 192;
/*      */   static final int SOFN_MAX = 207;
/*      */   static final int RST_MIN = 208;
/*      */   static final int RST_MAX = 215;
/*      */   static final int APP0_JFIF = 57344;
/*      */   static final int APP0_JFXX = 57345;
/*      */   static final int APP1_EXIF = 57600;
/*      */   static final int APP2_ICC = 57856;
/*      */   static final int APP14_ADOBE = 60928;
/*      */   static final int UNKNOWN_MARKER = 65535;
/*      */   static final int SOF_MARKER = 49152;
/*      */   static final int JFIF_RESUNITS_ASPECT = 0;
/*      */   static final int JFIF_RESUNITS_DPI = 1;
/*      */   static final int JFIF_RESUNITS_DPC = 2;
/*      */   static final int THUMBNAIL_JPEG = 16;
/*      */   static final int THUMBNAIL_PALETTE = 17;
/*      */   static final int THUMBNAIL_RGB = 18;
/*      */   static final int ADOBE_TRANSFORM_UNKNOWN = 0;
/*      */   static final int ADOBE_TRANSFORM_YCC = 1;
/*      */   static final int ADOBE_TRANSFORM_YCCK = 2;
/*  261 */   static final int[] zigzag = new int[] { 0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static IIOImage getThumbnail(ImageInputStream stream, int len, int thumbnailType, int w, int h) throws IOException {
/*      */     IIOImage result;
/*  280 */     long startPos = stream.getStreamPosition();
/*      */     
/*  282 */     if (thumbnailType == 16) {
/*  283 */       Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
/*  284 */       if (readers == null || !readers.hasNext()) return null; 
/*  285 */       ImageReader reader = readers.next();
/*  286 */       reader.setInput(stream);
/*  287 */       BufferedImage image = reader.read(0, null);
/*  288 */       IIOMetadata metadata = null;
/*      */       try {
/*  290 */         metadata = reader.getImageMetadata(0);
/*  291 */       } catch (Exception exception) {}
/*      */ 
/*      */       
/*  294 */       result = new IIOImage(image, null, metadata);
/*      */     } else {
/*      */       int numBands;
/*      */       ColorModel cm;
/*  298 */       if (thumbnailType == 17) {
/*  299 */         if (len < 768 + w * h) {
/*  300 */           return null;
/*      */         }
/*      */         
/*  303 */         numBands = 1;
/*      */         
/*  305 */         byte[] palette = new byte[768];
/*  306 */         stream.readFully(palette);
/*  307 */         byte[] r = new byte[256];
/*  308 */         byte[] g = new byte[256];
/*  309 */         byte[] b = new byte[256];
/*  310 */         for (int i = 0, off = 0; i < 256; i++) {
/*  311 */           r[i] = palette[off++];
/*  312 */           g[i] = palette[off++];
/*  313 */           b[i] = palette[off++];
/*      */         } 
/*      */         
/*  316 */         cm = new IndexColorModel(8, 256, r, g, b);
/*      */       } else {
/*  318 */         if (len < 3 * w * h) {
/*  319 */           return null;
/*      */         }
/*      */         
/*  322 */         numBands = 3;
/*      */         
/*  324 */         ColorSpace cs = ColorSpace.getInstance(1000);
/*  325 */         cm = new ComponentColorModel(cs, false, false, 1, 0);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  330 */       byte[] data = new byte[w * h * numBands];
/*  331 */       stream.readFully(data);
/*  332 */       DataBufferByte db = new DataBufferByte(data, data.length);
/*      */       
/*  334 */       WritableRaster wr = Raster.createInterleavedRaster(db, w, h, w * numBands, numBands, new int[] { 0, 1, 2 }, (Point)null);
/*      */       
/*  336 */       BufferedImage image = new BufferedImage(cm, wr, false, null);
/*  337 */       result = new IIOImage(image, null, null);
/*      */     } 
/*      */     
/*  340 */     stream.seek(startPos + len);
/*      */     
/*  342 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isReadOnly = true;
/*      */ 
/*      */   
/*      */   boolean app0JFIFPresent;
/*      */   
/*  352 */   int majorVersion = 1;
/*  353 */   int minorVersion = 2;
/*      */   int resUnits;
/*  355 */   int Xdensity = 1;
/*  356 */   int Ydensity = 1;
/*  357 */   int thumbWidth = 0;
/*  358 */   int thumbHeight = 0;
/*      */   
/*      */   BufferedImage jfifThumbnail;
/*      */   
/*      */   boolean app0JFXXPresent;
/*      */   
/*      */   List extensionCodes;
/*      */   
/*      */   List jfxxThumbnails;
/*      */   boolean app2ICCPresent;
/*  368 */   ICC_Profile profile = null;
/*      */   
/*      */   boolean dqtPresent;
/*      */   
/*      */   List qtables;
/*      */   
/*      */   boolean dhtPresent;
/*      */   
/*      */   List htables;
/*      */   
/*      */   boolean driPresent;
/*      */   
/*      */   int driInterval;
/*      */   
/*      */   boolean comPresent;
/*      */   
/*      */   List comments;
/*      */   
/*      */   boolean unknownPresent;
/*      */   
/*      */   List markerTags;
/*      */   
/*      */   List unknownData;
/*      */   
/*      */   boolean app14AdobePresent;
/*  393 */   int version = 100;
/*  394 */   int flags0 = 0;
/*  395 */   int flags1 = 0;
/*      */   
/*      */   int transform;
/*      */   
/*      */   boolean sofPresent;
/*      */   int sofProcess;
/*  401 */   int samplePrecision = 8;
/*      */   
/*      */   int numLines;
/*      */   
/*      */   int samplesPerLine;
/*      */   
/*      */   int numFrameComponents;
/*      */   
/*      */   int[] componentId;
/*      */   int[] hSamplingFactor;
/*      */   int[] vSamplingFactor;
/*      */   int[] qtableSelector;
/*      */   boolean sosPresent;
/*      */   int numScanComponents;
/*      */   int[] componentSelector;
/*      */   int[] dcHuffTable;
/*      */   int[] acHuffTable;
/*      */   int startSpectralSelection;
/*      */   int endSpectralSelection;
/*      */   int approxHigh;
/*      */   int approxLow;
/*  422 */   byte[] exifData = null;
/*      */ 
/*      */   
/*  425 */   private List markers = null;
/*      */ 
/*      */   
/*      */   private boolean hasAlpha = false;
/*      */   
/*      */   private boolean thumbnailsInitialized = false;
/*      */   
/*  432 */   private List thumbnails = new ArrayList();
/*      */   
/*      */   CLibJPEGMetadata() {
/*  435 */     super(true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", new String[] { "com_sun_media_imageio_plugins_tiff_image_1.0" }, new String[] { "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat" });
/*      */ 
/*      */     
/*  438 */     this.isReadOnly = this.isReadOnly;
/*      */   }
/*      */ 
/*      */   
/*      */   CLibJPEGMetadata(ImageInputStream stream) throws IIOException {
/*  443 */     this();
/*      */     
/*      */     try {
/*  446 */       initializeFromStream(stream);
/*  447 */     } catch (IOException e) {
/*  448 */       throw new IIOException("Cannot initialize JPEG metadata!", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private class QTable
/*      */   {
/*      */     private static final int QTABLE_SIZE = 64;
/*      */     int elementPrecision;
/*      */     int tableID;
/*      */     JPEGQTable table;
/*      */     int length;
/*      */     
/*      */     QTable(ImageInputStream stream) throws IOException {
/*  462 */       this.elementPrecision = (int)stream.readBits(4);
/*  463 */       this.tableID = (int)stream.readBits(4);
/*  464 */       byte[] tmp = new byte[64];
/*  465 */       stream.readFully(tmp);
/*  466 */       int[] data = new int[64];
/*  467 */       for (int i = 0; i < 64; i++) {
/*  468 */         data[i] = tmp[CLibJPEGMetadata.zigzag[i]] & 0xFF;
/*      */       }
/*  470 */       this.table = new JPEGQTable(data);
/*  471 */       this.length = data.length + 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private class HuffmanTable
/*      */   {
/*      */     private static final int NUM_LENGTHS = 16;
/*      */     int tableClass;
/*      */     int tableID;
/*      */     JPEGHuffmanTable table;
/*      */     int length;
/*      */     
/*      */     HuffmanTable(ImageInputStream stream) throws IOException {
/*  485 */       this.tableClass = (int)stream.readBits(4);
/*  486 */       this.tableID = (int)stream.readBits(4);
/*  487 */       short[] lengths = new short[16];
/*  488 */       for (int i = 0; i < 16; i++) {
/*  489 */         lengths[i] = (short)stream.read();
/*      */       }
/*  491 */       int numValues = 0;
/*  492 */       for (int j = 0; j < 16; j++) {
/*  493 */         numValues += lengths[j];
/*      */       }
/*  495 */       short[] values = new short[numValues];
/*  496 */       for (int k = 0; k < numValues; k++) {
/*  497 */         values[k] = (short)stream.read();
/*      */       }
/*  499 */       this.table = new JPEGHuffmanTable(lengths, values);
/*      */       
/*  501 */       this.length = 17 + values.length;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private synchronized void initializeFromStream(ImageInputStream iis) throws IOException {
/*  507 */     iis.mark();
/*  508 */     iis.setByteOrder(ByteOrder.BIG_ENDIAN);
/*      */     
/*  510 */     this.markers = new ArrayList();
/*      */     
/*  512 */     boolean isICCProfileValid = true;
/*  513 */     int numICCProfileChunks = 0;
/*  514 */     long[] iccProfileChunkOffsets = null;
/*  515 */     int[] iccProfileChunkLengths = null;
/*      */ 
/*      */     
/*      */     try {
/*      */       while (true) {
/*  520 */         if (iis.read() == 255) {
/*      */           
/*  522 */           int code = iis.read();
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  527 */           if (code == 0 || code == 255 || code == 216 || code == 1 || (code >= 208 && code <= 215)) {
/*      */             continue;
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  534 */           if (code == 217) {
/*      */             break;
/*      */           }
/*      */ 
/*      */           
/*  539 */           int dataLength = iis.readUnsignedShort() - 2;
/*      */           
/*  541 */           if (224 <= code && code <= 239) {
/*  542 */             long pos = iis.getStreamPosition();
/*  543 */             boolean appnAdded = false;
/*      */             
/*  545 */             switch (code) {
/*      */               case 224:
/*  547 */                 if (dataLength >= 5) {
/*  548 */                   byte[] b = new byte[5];
/*  549 */                   iis.readFully(b);
/*  550 */                   String id = new String(b);
/*  551 */                   if (id.startsWith("JFIF") && !this.app0JFIFPresent) {
/*      */                     
/*  553 */                     this.app0JFIFPresent = true;
/*  554 */                     this.markers.add(new Integer(57344));
/*  555 */                     this.majorVersion = iis.read();
/*  556 */                     this.minorVersion = iis.read();
/*  557 */                     this.resUnits = iis.read();
/*  558 */                     this.Xdensity = iis.readUnsignedShort();
/*  559 */                     this.Ydensity = iis.readUnsignedShort();
/*  560 */                     this.thumbWidth = iis.read();
/*  561 */                     this.thumbHeight = iis.read();
/*  562 */                     if (this.thumbWidth > 0 && this.thumbHeight > 0) {
/*      */                       
/*  564 */                       IIOImage imiio = getThumbnail(iis, dataLength - 14, 18, this.thumbWidth, this.thumbHeight);
/*      */ 
/*      */ 
/*      */                       
/*  568 */                       if (imiio != null) {
/*  569 */                         this
/*  570 */                           .jfifThumbnail = (BufferedImage)imiio.getRenderedImage();
/*      */                       }
/*      */                     } 
/*  573 */                     appnAdded = true; break;
/*  574 */                   }  if (id.startsWith("JFXX")) {
/*  575 */                     if (!this.app0JFXXPresent) {
/*  576 */                       this.extensionCodes = new ArrayList(1);
/*  577 */                       this.jfxxThumbnails = new ArrayList(1);
/*  578 */                       this.app0JFXXPresent = true;
/*      */                     } 
/*  580 */                     this.markers.add(new Integer(57345));
/*  581 */                     int extCode = iis.read();
/*  582 */                     this.extensionCodes.add(new Integer(extCode));
/*  583 */                     int w = 0, h = 0, offset = 6;
/*  584 */                     if (extCode != 16) {
/*  585 */                       w = iis.read();
/*  586 */                       h = iis.read();
/*  587 */                       offset += 2;
/*      */                     } 
/*      */                     
/*  590 */                     IIOImage imiio = getThumbnail(iis, dataLength - offset, extCode, w, h);
/*      */                     
/*  592 */                     if (imiio != null) {
/*  593 */                       this.jfxxThumbnails.add(imiio);
/*      */                     }
/*  595 */                     appnAdded = true;
/*      */                   } 
/*      */                 } 
/*      */                 break;
/*      */               case 225:
/*  600 */                 if (dataLength >= 6) {
/*  601 */                   byte[] b = new byte[6];
/*  602 */                   iis.readFully(b);
/*  603 */                   if (b[0] == 69 && b[1] == 120 && b[2] == 105 && b[3] == 102 && b[4] == 0 && b[5] == 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                     
/*  609 */                     this.exifData = new byte[dataLength - 6];
/*  610 */                     iis.readFully(this.exifData);
/*      */                   } 
/*      */                 } 
/*      */               case 226:
/*  614 */                 if (dataLength >= 12) {
/*  615 */                   byte[] b = new byte[12];
/*  616 */                   iis.readFully(b);
/*  617 */                   String id = new String(b);
/*  618 */                   if (id.startsWith("ICC_PROFILE")) {
/*  619 */                     if (!isICCProfileValid) {
/*  620 */                       iis.skipBytes(dataLength - 12);
/*      */                       
/*      */                       continue;
/*      */                     } 
/*  624 */                     int chunkNum = iis.read();
/*  625 */                     int numChunks = iis.read();
/*  626 */                     if (numChunks == 0 || chunkNum == 0 || chunkNum > numChunks || (this.app2ICCPresent && (numChunks != numICCProfileChunks || iccProfileChunkOffsets[chunkNum] != 0L))) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                       
/*  633 */                       isICCProfileValid = false;
/*  634 */                       iis.skipBytes(dataLength - 14);
/*      */                       
/*      */                       continue;
/*      */                     } 
/*  638 */                     if (!this.app2ICCPresent) {
/*  639 */                       this.app2ICCPresent = true;
/*      */ 
/*      */                       
/*  642 */                       this.markers.add(new Integer(57856));
/*      */                       
/*  644 */                       numICCProfileChunks = numChunks;
/*      */                       
/*  646 */                       if (numChunks == 1) {
/*  647 */                         b = new byte[dataLength - 14];
/*  648 */                         iis.readFully(b);
/*  649 */                         this
/*  650 */                           .profile = ICC_Profile.getInstance(b);
/*      */                       } else {
/*  652 */                         iccProfileChunkOffsets = new long[numChunks + 1];
/*      */                         
/*  654 */                         iccProfileChunkLengths = new int[numChunks + 1];
/*      */                         
/*  656 */                         iccProfileChunkOffsets[chunkNum] = iis
/*  657 */                           .getStreamPosition();
/*  658 */                         iccProfileChunkLengths[chunkNum] = dataLength - 14;
/*      */                         
/*  660 */                         iis.skipBytes(dataLength - 14);
/*      */                       } 
/*      */                     } else {
/*  663 */                       iccProfileChunkOffsets[chunkNum] = iis
/*  664 */                         .getStreamPosition();
/*  665 */                       iccProfileChunkLengths[chunkNum] = dataLength - 14;
/*      */                       
/*  667 */                       iis.skipBytes(dataLength - 14);
/*      */                     } 
/*      */                     
/*  670 */                     appnAdded = true;
/*      */                   } 
/*      */                 } 
/*      */                 break;
/*      */               case 238:
/*  675 */                 if (dataLength >= 5) {
/*  676 */                   byte[] b = new byte[5];
/*  677 */                   iis.readFully(b);
/*  678 */                   String id = new String(b);
/*  679 */                   if (id.startsWith("Adobe") && !this.app14AdobePresent) {
/*      */                     
/*  681 */                     this.app14AdobePresent = true;
/*  682 */                     this.markers.add(new Integer(60928));
/*  683 */                     this.version = iis.readUnsignedShort();
/*  684 */                     this.flags0 = iis.readUnsignedShort();
/*  685 */                     this.flags1 = iis.readUnsignedShort();
/*  686 */                     this.transform = iis.read();
/*  687 */                     iis.skipBytes(dataLength - 12);
/*  688 */                     appnAdded = true;
/*      */                   } 
/*      */                 } 
/*      */                 break;
/*      */               default:
/*  693 */                 appnAdded = false;
/*      */                 break;
/*      */             } 
/*      */             
/*  697 */             if (!appnAdded) {
/*  698 */               iis.seek(pos);
/*  699 */               addUnknownMarkerSegment(iis, code, dataLength);
/*      */             }  continue;
/*  701 */           }  if (code == 219) {
/*  702 */             if (!this.dqtPresent) {
/*  703 */               this.dqtPresent = true;
/*  704 */               this.qtables = new ArrayList(1);
/*      */             } 
/*  706 */             this.markers.add(new Integer(219));
/*  707 */             List<QTable> l = new ArrayList(1);
/*      */             while (true)
/*  709 */             { QTable t = new QTable(iis);
/*  710 */               l.add(t);
/*  711 */               dataLength -= t.length;
/*  712 */               if (dataLength <= 0)
/*  713 */                 this.qtables.add(l);  } 
/*  714 */           }  if (code == 196) {
/*  715 */             if (!this.dhtPresent) {
/*  716 */               this.dhtPresent = true;
/*  717 */               this.htables = new ArrayList(1);
/*      */             } 
/*  719 */             this.markers.add(new Integer(196));
/*  720 */             List<HuffmanTable> l = new ArrayList(1);
/*      */             while (true)
/*  722 */             { HuffmanTable t = new HuffmanTable(iis);
/*  723 */               l.add(t);
/*  724 */               dataLength -= t.length;
/*  725 */               if (dataLength <= 0)
/*  726 */                 this.htables.add(l);  } 
/*  727 */           }  if (code == 221) {
/*  728 */             if (!this.driPresent) {
/*  729 */               this.driPresent = true;
/*      */             }
/*  731 */             this.markers.add(new Integer(221));
/*  732 */             this.driInterval = iis.readUnsignedShort(); continue;
/*  733 */           }  if (code == 254) {
/*  734 */             if (!this.comPresent) {
/*  735 */               this.comPresent = true;
/*  736 */               this.comments = new ArrayList(1);
/*      */             } 
/*  738 */             this.markers.add(new Integer(254));
/*  739 */             byte[] b = new byte[dataLength];
/*  740 */             iis.readFully(b);
/*  741 */             this.comments.add(b); continue;
/*  742 */           }  if ((code >= 192 && code <= 207) || code == 247) {
/*      */             
/*  744 */             if (!this.sofPresent) {
/*  745 */               this.sofPresent = true;
/*  746 */               this.sofProcess = code - 192;
/*  747 */               this.samplePrecision = iis.read();
/*  748 */               this.numLines = iis.readUnsignedShort();
/*  749 */               this.samplesPerLine = iis.readUnsignedShort();
/*  750 */               this.numFrameComponents = iis.read();
/*  751 */               this.componentId = new int[this.numFrameComponents];
/*  752 */               this.hSamplingFactor = new int[this.numFrameComponents];
/*  753 */               this.vSamplingFactor = new int[this.numFrameComponents];
/*  754 */               this.qtableSelector = new int[this.numFrameComponents];
/*  755 */               for (int i = 0; i < this.numFrameComponents; i++) {
/*  756 */                 this.componentId[i] = iis.read();
/*  757 */                 this.hSamplingFactor[i] = (int)iis.readBits(4);
/*  758 */                 this.vSamplingFactor[i] = (int)iis.readBits(4);
/*  759 */                 this.qtableSelector[i] = iis.read();
/*      */               } 
/*  761 */               this.markers.add(new Integer(49152));
/*      */             }  continue;
/*  763 */           }  if (code == 218) {
/*  764 */             if (!this.sosPresent) {
/*  765 */               this.sosPresent = true;
/*  766 */               this.numScanComponents = iis.read();
/*  767 */               this.componentSelector = new int[this.numScanComponents];
/*  768 */               this.dcHuffTable = new int[this.numScanComponents];
/*  769 */               this.acHuffTable = new int[this.numScanComponents];
/*  770 */               for (int i = 0; i < this.numScanComponents; i++) {
/*  771 */                 this.componentSelector[i] = iis.read();
/*  772 */                 this.dcHuffTable[i] = (int)iis.readBits(4);
/*  773 */                 this.acHuffTable[i] = (int)iis.readBits(4);
/*      */               } 
/*  775 */               this.startSpectralSelection = iis.read();
/*  776 */               this.endSpectralSelection = iis.read();
/*  777 */               this.approxHigh = (int)iis.readBits(4);
/*  778 */               this.approxLow = (int)iis.readBits(4);
/*  779 */               this.markers.add(new Integer(218));
/*      */             } 
/*      */             break;
/*      */           } 
/*  783 */           addUnknownMarkerSegment(iis, code, dataLength);
/*      */         } 
/*      */       } 
/*  786 */     } catch (EOFException eofe) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  792 */     if (this.app2ICCPresent && isICCProfileValid && this.profile == null) {
/*  793 */       int profileDataLength = 0;
/*  794 */       for (int i = 1; i <= numICCProfileChunks; i++) {
/*  795 */         if (iccProfileChunkOffsets[i] == 0L) {
/*  796 */           isICCProfileValid = false;
/*      */           break;
/*      */         } 
/*  799 */         profileDataLength += iccProfileChunkLengths[i];
/*      */       } 
/*      */       
/*  802 */       if (isICCProfileValid) {
/*  803 */         byte[] b = new byte[profileDataLength];
/*  804 */         int off = 0;
/*  805 */         for (int j = 1; j <= numICCProfileChunks; j++) {
/*  806 */           iis.seek(iccProfileChunkOffsets[j]);
/*  807 */           iis.read(b, off, iccProfileChunkLengths[j]);
/*  808 */           off += iccProfileChunkLengths[j];
/*      */         } 
/*      */         
/*  811 */         this.profile = ICC_Profile.getInstance(b);
/*      */       } 
/*      */     } 
/*      */     
/*  815 */     iis.reset();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void addUnknownMarkerSegment(ImageInputStream stream, int code, int len) throws IOException {
/*  821 */     if (!this.unknownPresent) {
/*  822 */       this.unknownPresent = true;
/*  823 */       this.markerTags = new ArrayList(1);
/*  824 */       this.unknownData = new ArrayList(1);
/*      */     } 
/*  826 */     this.markerTags.add(new Integer(code));
/*  827 */     byte[] b = new byte[len];
/*  828 */     stream.readFully(b);
/*  829 */     this.unknownData.add(b);
/*  830 */     this.markers.add(new Integer(65535));
/*      */   }
/*      */   
/*      */   public boolean isReadOnly() {
/*  834 */     return this.isReadOnly;
/*      */   }
/*      */   
/*      */   public Node getAsTree(String formatName) {
/*  838 */     if (formatName.equals(this.nativeMetadataFormatName))
/*  839 */       return getNativeTree(); 
/*  840 */     if (formatName
/*  841 */       .equals("javax_imageio_1.0"))
/*  842 */       return getStandardTree(); 
/*  843 */     if (formatName.equals("com_sun_media_imageio_plugins_tiff_image_1.0")) {
/*  844 */       return getTIFFTree();
/*      */     }
/*  846 */     throw new IllegalArgumentException("Not a recognized format!");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
/*  852 */     if (this.isReadOnly) {
/*  853 */       throw new IllegalStateException("isReadOnly() == true!");
/*      */     }
/*      */   }
/*      */   
/*      */   public void reset() {
/*  858 */     if (this.isReadOnly) {
/*  859 */       throw new IllegalStateException("isReadOnly() == true!");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Node getNativeTree() {
/*  866 */     int jfxxIndex = 0;
/*  867 */     int dqtIndex = 0;
/*  868 */     int dhtIndex = 0;
/*  869 */     int comIndex = 0;
/*  870 */     int unknownIndex = 0;
/*      */     
/*  872 */     IIOMetadataNode root = new IIOMetadataNode(this.nativeMetadataFormatName);
/*      */     
/*  874 */     IIOMetadataNode JPEGvariety = new IIOMetadataNode("JPEGvariety");
/*  875 */     root.appendChild(JPEGvariety);
/*      */     
/*  877 */     IIOMetadataNode markerSequence = new IIOMetadataNode("markerSequence");
/*  878 */     root.appendChild(markerSequence);
/*      */     
/*  880 */     IIOMetadataNode app0JFIF = null;
/*  881 */     if (this.app0JFIFPresent || this.app0JFXXPresent || this.app2ICCPresent) {
/*  882 */       app0JFIF = new IIOMetadataNode("app0JFIF");
/*  883 */       app0JFIF.setAttribute("majorVersion", 
/*  884 */           Integer.toString(this.majorVersion));
/*  885 */       app0JFIF.setAttribute("minorVersion", 
/*  886 */           Integer.toString(this.minorVersion));
/*  887 */       app0JFIF.setAttribute("resUnits", 
/*  888 */           Integer.toString(this.resUnits));
/*  889 */       app0JFIF.setAttribute("Xdensity", 
/*  890 */           Integer.toString(this.Xdensity));
/*  891 */       app0JFIF.setAttribute("Ydensity", 
/*  892 */           Integer.toString(this.Ydensity));
/*  893 */       app0JFIF.setAttribute("thumbWidth", 
/*  894 */           Integer.toString(this.thumbWidth));
/*  895 */       app0JFIF.setAttribute("thumbHeight", 
/*  896 */           Integer.toString(this.thumbHeight));
/*  897 */       JPEGvariety.appendChild(app0JFIF);
/*      */     } 
/*      */     
/*  900 */     IIOMetadataNode JFXX = null;
/*  901 */     if (this.app0JFXXPresent) {
/*  902 */       JFXX = new IIOMetadataNode("JFXX");
/*  903 */       app0JFIF.appendChild(JFXX);
/*      */     } 
/*      */     
/*  906 */     Iterator<Integer> markerIter = this.markers.iterator();
/*  907 */     while (markerIter.hasNext()) {
/*  908 */       IIOMetadataNode app0JFXX; Integer extensionCode; IIOMetadataNode JFIFthumb, app2ICC, dqt; List<QTable> list; List<HuffmanTable> tables; int numTables, j; IIOMetadataNode dht; int k; IIOMetadataNode dri, com, unknown; Integer markerTag; IIOMetadataNode app14Adobe, sof; int i; IIOMetadataNode sos; int m, marker = ((Integer)markerIter.next()).intValue();
/*  909 */       switch (marker) {
/*      */ 
/*      */ 
/*      */         
/*      */         case 57345:
/*  914 */           app0JFXX = new IIOMetadataNode("app0JFXX");
/*  915 */           extensionCode = this.extensionCodes.get(jfxxIndex);
/*  916 */           app0JFXX.setAttribute("extensionCode", extensionCode
/*  917 */               .toString());
/*  918 */           JFIFthumb = null;
/*  919 */           switch (extensionCode.intValue()) {
/*      */             case 16:
/*  921 */               JFIFthumb = new IIOMetadataNode("JFIFthumbJPEG");
/*      */               break;
/*      */             case 17:
/*  924 */               JFIFthumb = new IIOMetadataNode("JFIFthumbPalette");
/*      */               break;
/*      */             case 18:
/*  927 */               JFIFthumb = new IIOMetadataNode("JFIFthumbRGB");
/*      */               break;
/*      */           } 
/*      */ 
/*      */           
/*  932 */           if (JFIFthumb != null) {
/*  933 */             IIOImage img = this.jfxxThumbnails.get(jfxxIndex++);
/*  934 */             if (extensionCode.intValue() == 16) {
/*  935 */               IIOMetadata thumbMetadata = img.getMetadata();
/*  936 */               if (thumbMetadata != null) {
/*      */                 
/*  938 */                 Node thumbTree = thumbMetadata.getAsTree(this.nativeMetadataFormatName);
/*  939 */                 if (thumbTree instanceof IIOMetadataNode) {
/*  940 */                   IIOMetadataNode elt = (IIOMetadataNode)thumbTree;
/*      */ 
/*      */                   
/*  943 */                   NodeList elts = elt.getElementsByTagName("markerSequence");
/*  944 */                   if (elts.getLength() > 0) {
/*  945 */                     JFIFthumb.appendChild(elts.item(0));
/*      */                   }
/*      */                 } 
/*      */               } 
/*      */             } else {
/*      */               
/*  951 */               BufferedImage thumb = (BufferedImage)img.getRenderedImage();
/*  952 */               JFIFthumb.setAttribute("thumbWidth", 
/*  953 */                   Integer.toString(thumb.getWidth()));
/*  954 */               JFIFthumb.setAttribute("thumbHeight", 
/*  955 */                   Integer.toString(thumb.getHeight()));
/*      */             } 
/*      */ 
/*      */             
/*  959 */             JFIFthumb.setUserObject(img);
/*  960 */             app0JFXX.appendChild(JFIFthumb);
/*      */           } 
/*  962 */           JFXX.appendChild(app0JFXX);
/*      */         
/*      */         case 57856:
/*  965 */           app2ICC = new IIOMetadataNode("app2ICC");
/*  966 */           app2ICC.setUserObject(this.profile);
/*  967 */           app0JFIF.appendChild(app2ICC);
/*      */         
/*      */         case 219:
/*  970 */           dqt = new IIOMetadataNode("dqt");
/*  971 */           list = this.qtables.get(dqtIndex++);
/*  972 */           numTables = list.size();
/*  973 */           for (j = 0; j < numTables; j++) {
/*  974 */             IIOMetadataNode dqtable = new IIOMetadataNode("dqtable");
/*  975 */             QTable t = list.get(j);
/*  976 */             dqtable.setAttribute("elementPrecision", 
/*  977 */                 Integer.toString(t.elementPrecision));
/*  978 */             dqtable.setAttribute("qtableId", 
/*  979 */                 Integer.toString(t.tableID));
/*  980 */             dqtable.setUserObject(t.table);
/*  981 */             dqt.appendChild(dqtable);
/*      */           } 
/*  983 */           markerSequence.appendChild(dqt);
/*      */         
/*      */         case 196:
/*  986 */           dht = new IIOMetadataNode("dht");
/*  987 */           tables = this.htables.get(dhtIndex++);
/*  988 */           numTables = tables.size();
/*  989 */           for (k = 0; k < numTables; k++) {
/*  990 */             IIOMetadataNode dhtable = new IIOMetadataNode("dhtable");
/*  991 */             HuffmanTable t = tables.get(k);
/*  992 */             dhtable.setAttribute("class", 
/*  993 */                 Integer.toString(t.tableClass));
/*  994 */             dhtable.setAttribute("htableId", 
/*  995 */                 Integer.toString(t.tableID));
/*  996 */             dhtable.setUserObject(t.table);
/*  997 */             dht.appendChild(dhtable);
/*      */           } 
/*  999 */           markerSequence.appendChild(dht);
/*      */         
/*      */         case 221:
/* 1002 */           dri = new IIOMetadataNode("dri");
/* 1003 */           dri.setAttribute("interval", Integer.toString(this.driInterval));
/* 1004 */           markerSequence.appendChild(dri);
/*      */         
/*      */         case 254:
/* 1007 */           com = new IIOMetadataNode("com");
/* 1008 */           com.setUserObject(this.comments.get(comIndex++));
/* 1009 */           markerSequence.appendChild(com);
/*      */         
/*      */         case 65535:
/* 1012 */           unknown = new IIOMetadataNode("unknown");
/* 1013 */           markerTag = this.markerTags.get(unknownIndex);
/* 1014 */           unknown.setAttribute("MarkerTag", markerTag.toString());
/* 1015 */           unknown.setUserObject(this.unknownData.get(unknownIndex++));
/* 1016 */           markerSequence.appendChild(unknown);
/*      */         
/*      */         case 60928:
/* 1019 */           app14Adobe = new IIOMetadataNode("app14Adobe");
/* 1020 */           app14Adobe.setAttribute("version", Integer.toString(this.version));
/* 1021 */           app14Adobe.setAttribute("flags0", Integer.toString(this.flags0));
/* 1022 */           app14Adobe.setAttribute("flags1", Integer.toString(this.flags1));
/* 1023 */           app14Adobe.setAttribute("transform", 
/* 1024 */               Integer.toString(this.transform));
/* 1025 */           markerSequence.appendChild(app14Adobe);
/*      */         
/*      */         case 49152:
/* 1028 */           sof = new IIOMetadataNode("sof");
/* 1029 */           sof.setAttribute("process", Integer.toString(this.sofProcess));
/* 1030 */           sof.setAttribute("samplePrecision", 
/* 1031 */               Integer.toString(this.samplePrecision));
/* 1032 */           sof.setAttribute("numLines", Integer.toString(this.numLines));
/* 1033 */           sof.setAttribute("samplesPerLine", 
/* 1034 */               Integer.toString(this.samplesPerLine));
/* 1035 */           sof.setAttribute("numFrameComponents", 
/* 1036 */               Integer.toString(this.numFrameComponents));
/* 1037 */           for (i = 0; i < this.numFrameComponents; i++) {
/* 1038 */             IIOMetadataNode componentSpec = new IIOMetadataNode("componentSpec");
/*      */             
/* 1040 */             componentSpec.setAttribute("componentId", 
/* 1041 */                 Integer.toString(this.componentId[i]));
/* 1042 */             componentSpec.setAttribute("HsamplingFactor", 
/* 1043 */                 Integer.toString(this.hSamplingFactor[i]));
/* 1044 */             componentSpec.setAttribute("VsamplingFactor", 
/* 1045 */                 Integer.toString(this.vSamplingFactor[i]));
/* 1046 */             componentSpec.setAttribute("QtableSelector", 
/* 1047 */                 Integer.toString(this.qtableSelector[i]));
/* 1048 */             sof.appendChild(componentSpec);
/*      */           } 
/* 1050 */           markerSequence.appendChild(sof);
/*      */         
/*      */         case 218:
/* 1053 */           sos = new IIOMetadataNode("sos");
/* 1054 */           sos.setAttribute("numScanComponents", 
/* 1055 */               Integer.toString(this.numScanComponents));
/* 1056 */           sos.setAttribute("startSpectralSelection", 
/* 1057 */               Integer.toString(this.startSpectralSelection));
/* 1058 */           sos.setAttribute("endSpectralSelection", 
/* 1059 */               Integer.toString(this.endSpectralSelection));
/* 1060 */           sos.setAttribute("approxHigh", Integer.toString(this.approxHigh));
/* 1061 */           sos.setAttribute("approxLow", Integer.toString(this.approxLow));
/* 1062 */           for (m = 0; m < this.numScanComponents; m++) {
/* 1063 */             IIOMetadataNode scanComponentSpec = new IIOMetadataNode("scanComponentSpec");
/*      */             
/* 1065 */             scanComponentSpec.setAttribute("componentSelector", 
/* 1066 */                 Integer.toString(this.componentSelector[m]));
/* 1067 */             scanComponentSpec.setAttribute("dcHuffTable", 
/* 1068 */                 Integer.toString(this.dcHuffTable[m]));
/* 1069 */             scanComponentSpec.setAttribute("acHuffTable", 
/* 1070 */                 Integer.toString(this.acHuffTable[m]));
/* 1071 */             sos.appendChild(scanComponentSpec);
/*      */           } 
/* 1073 */           markerSequence.appendChild(sos);
/*      */       } 
/*      */ 
/*      */     
/*      */     } 
/* 1078 */     return root;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected IIOMetadataNode getStandardChromaNode() {
/* 1084 */     if (!this.sofPresent)
/*      */     {
/* 1086 */       return null;
/*      */     }
/*      */     
/* 1089 */     IIOMetadataNode chroma = new IIOMetadataNode("Chroma");
/* 1090 */     IIOMetadataNode csType = new IIOMetadataNode("ColorSpaceType");
/* 1091 */     chroma.appendChild(csType);
/*      */     
/* 1093 */     IIOMetadataNode numChanNode = new IIOMetadataNode("NumChannels");
/* 1094 */     chroma.appendChild(numChanNode);
/* 1095 */     numChanNode.setAttribute("value", 
/* 1096 */         Integer.toString(this.numFrameComponents));
/*      */ 
/*      */     
/* 1099 */     if (this.app0JFIFPresent) {
/* 1100 */       if (this.numFrameComponents == 1) {
/* 1101 */         csType.setAttribute("name", "GRAY");
/*      */       } else {
/* 1103 */         csType.setAttribute("name", "YCbCr");
/*      */       } 
/* 1105 */       return chroma;
/*      */     } 
/*      */ 
/*      */     
/* 1109 */     if (this.app14AdobePresent) {
/* 1110 */       switch (this.transform) {
/*      */         case 2:
/* 1112 */           csType.setAttribute("name", "YCCK");
/*      */           break;
/*      */         case 1:
/* 1115 */           csType.setAttribute("name", "YCbCr");
/*      */           break;
/*      */         case 0:
/* 1118 */           if (this.numFrameComponents == 3) {
/* 1119 */             csType.setAttribute("name", "RGB"); break;
/* 1120 */           }  if (this.numFrameComponents == 4) {
/* 1121 */             csType.setAttribute("name", "CMYK");
/*      */           }
/*      */           break;
/*      */       } 
/* 1125 */       return chroma;
/*      */     } 
/*      */ 
/*      */     
/* 1129 */     this.hasAlpha = false;
/*      */ 
/*      */     
/* 1132 */     if (this.numFrameComponents < 3) {
/* 1133 */       csType.setAttribute("name", "GRAY");
/* 1134 */       if (this.numFrameComponents == 2) {
/* 1135 */         this.hasAlpha = true;
/*      */       }
/* 1137 */       return chroma;
/*      */     } 
/*      */     
/* 1140 */     boolean idsAreJFIF = true;
/*      */     
/* 1142 */     for (int i = 0; i < this.componentId.length; i++) {
/* 1143 */       int id = this.componentId[i];
/* 1144 */       if (id < 1 || id >= this.componentId.length) {
/* 1145 */         idsAreJFIF = false;
/*      */       }
/*      */     } 
/*      */     
/* 1149 */     if (idsAreJFIF) {
/* 1150 */       csType.setAttribute("name", "YCbCr");
/* 1151 */       if (this.numFrameComponents == 4) {
/* 1152 */         this.hasAlpha = true;
/*      */       }
/* 1154 */       return chroma;
/*      */     } 
/*      */ 
/*      */     
/* 1158 */     if (this.componentId[0] == 82 && this.componentId[1] == 71 && this.componentId[2] == 66) {
/*      */ 
/*      */       
/* 1161 */       csType.setAttribute("name", "RGB");
/* 1162 */       if (this.numFrameComponents == 4 && this.componentId[3] == 65) {
/* 1163 */         this.hasAlpha = true;
/*      */       }
/* 1165 */       return chroma;
/*      */     } 
/*      */     
/* 1168 */     if (this.componentId[0] == 89 && this.componentId[1] == 67 && this.componentId[2] == 99) {
/*      */ 
/*      */       
/* 1171 */       csType.setAttribute("name", "PhotoYCC");
/* 1172 */       if (this.numFrameComponents == 4 && this.componentId[3] == 65)
/*      */       {
/* 1174 */         this.hasAlpha = true;
/*      */       }
/* 1176 */       return chroma;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1182 */     boolean subsampled = false;
/*      */     
/* 1184 */     int hfactor = this.hSamplingFactor[0];
/* 1185 */     int vfactor = this.vSamplingFactor[0];
/*      */     
/* 1187 */     for (int j = 1; j < this.componentId.length; j++) {
/* 1188 */       if (this.hSamplingFactor[j] != hfactor || this.vSamplingFactor[j] != vfactor) {
/*      */         
/* 1190 */         subsampled = true;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/* 1195 */     if (subsampled) {
/* 1196 */       csType.setAttribute("name", "YCbCr");
/* 1197 */       if (this.numFrameComponents == 4) {
/* 1198 */         this.hasAlpha = true;
/*      */       }
/* 1200 */       return chroma;
/*      */     } 
/*      */ 
/*      */     
/* 1204 */     if (this.numFrameComponents == 3) {
/* 1205 */       csType.setAttribute("name", "RGB");
/*      */     } else {
/* 1207 */       csType.setAttribute("name", "CMYK");
/*      */     } 
/*      */     
/* 1210 */     return chroma;
/*      */   }
/*      */   
/*      */   protected IIOMetadataNode getStandardCompressionNode() {
/* 1214 */     IIOMetadataNode compression = null;
/*      */     
/* 1216 */     if (this.sofPresent || this.sosPresent) {
/* 1217 */       compression = new IIOMetadataNode("Compression");
/*      */       
/* 1219 */       if (this.sofPresent) {
/*      */         
/* 1221 */         boolean isLossless = (this.sofProcess == 3 || this.sofProcess == 7 || this.sofProcess == 11 || this.sofProcess == 15 || this.sofProcess == 55);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1226 */         IIOMetadataNode name = new IIOMetadataNode("CompressionTypeName");
/*      */         
/* 1228 */         String compressionType = isLossless ? ((this.sofProcess == 55) ? "JPEG-LS" : "JPEG-LOSSLESS") : "JPEG";
/*      */         
/* 1230 */         name.setAttribute("value", compressionType);
/* 1231 */         compression.appendChild(name);
/*      */ 
/*      */         
/* 1234 */         IIOMetadataNode lossless = new IIOMetadataNode("Lossless");
/* 1235 */         lossless.setAttribute("value", isLossless ? "true" : "false");
/* 1236 */         compression.appendChild(lossless);
/*      */       } 
/*      */       
/* 1239 */       if (this.sosPresent) {
/* 1240 */         IIOMetadataNode prog = new IIOMetadataNode("NumProgressiveScans");
/*      */         
/* 1242 */         prog.setAttribute("value", "1");
/* 1243 */         compression.appendChild(prog);
/*      */       } 
/*      */     } 
/*      */     
/* 1247 */     return compression;
/*      */   }
/*      */   
/*      */   protected IIOMetadataNode getStandardDimensionNode() {
/* 1251 */     IIOMetadataNode dim = new IIOMetadataNode("Dimension");
/* 1252 */     IIOMetadataNode orient = new IIOMetadataNode("ImageOrientation");
/* 1253 */     orient.setAttribute("value", "normal");
/* 1254 */     dim.appendChild(orient);
/*      */     
/* 1256 */     if (this.app0JFIFPresent) {
/*      */       float aspectRatio;
/* 1258 */       if (this.resUnits == 0) {
/*      */         
/* 1260 */         aspectRatio = this.Xdensity / this.Ydensity;
/*      */       } else {
/*      */         
/* 1263 */         aspectRatio = this.Ydensity / this.Xdensity;
/*      */       } 
/* 1265 */       IIOMetadataNode aspect = new IIOMetadataNode("PixelAspectRatio");
/* 1266 */       aspect.setAttribute("value", Float.toString(aspectRatio));
/* 1267 */       dim.insertBefore(aspect, orient);
/*      */       
/* 1269 */       if (this.resUnits != 0) {
/*      */         
/* 1271 */         float scale = (this.resUnits == 1) ? 25.4F : 10.0F;
/*      */         
/* 1273 */         IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
/*      */         
/* 1275 */         horiz.setAttribute("value", 
/* 1276 */             Float.toString(scale / this.Xdensity));
/* 1277 */         dim.appendChild(horiz);
/*      */         
/* 1279 */         IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
/*      */         
/* 1281 */         vert.setAttribute("value", 
/* 1282 */             Float.toString(scale / this.Ydensity));
/* 1283 */         dim.appendChild(vert);
/*      */       } 
/*      */     } 
/* 1286 */     return dim;
/*      */   }
/*      */   
/*      */   protected IIOMetadataNode getStandardTextNode() {
/* 1290 */     IIOMetadataNode text = null;
/* 1291 */     if (this.comPresent) {
/* 1292 */       text = new IIOMetadataNode("Text");
/* 1293 */       Iterator<byte[]> iter = this.comments.iterator();
/* 1294 */       while (iter.hasNext()) {
/* 1295 */         IIOMetadataNode entry = new IIOMetadataNode("TextEntry");
/* 1296 */         entry.setAttribute("keyword", "comment");
/* 1297 */         byte[] data = iter.next();
/*      */         try {
/* 1299 */           entry.setAttribute("value", new String(data, "ISO-8859-1"));
/*      */         }
/* 1301 */         catch (UnsupportedEncodingException e) {
/* 1302 */           entry.setAttribute("value", new String(data));
/*      */         } 
/* 1304 */         text.appendChild(entry);
/*      */       } 
/*      */     } 
/* 1307 */     return text;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected IIOMetadataNode getStandardTransparencyNode() {
/* 1313 */     IIOMetadataNode trans = null;
/* 1314 */     if (this.hasAlpha == true) {
/* 1315 */       trans = new IIOMetadataNode("Transparency");
/* 1316 */       IIOMetadataNode alpha = new IIOMetadataNode("Alpha");
/* 1317 */       alpha.setAttribute("value", "nonpremultiplied");
/* 1318 */       trans.appendChild(alpha);
/*      */     } 
/* 1320 */     return trans;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Node getTIFFTree() {
/* 1326 */     String metadataName = "com_sun_media_imageio_plugins_tiff_image_1.0";
/*      */     
/* 1328 */     BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
/*      */ 
/*      */ 
/*      */     
/* 1332 */     TIFFDirectory dir = new TIFFDirectory(new TIFFTagSet[] { (TIFFTagSet)base, (TIFFTagSet)EXIFParentTIFFTagSet.getInstance() }, null);
/*      */ 
/*      */     
/* 1335 */     if (this.sofPresent) {
/*      */       
/* 1337 */       int compression = 7;
/*      */       
/* 1339 */       TIFFField compressionField = new TIFFField(base.getTag(259), compression);
/*      */       
/* 1341 */       dir.addTIFFField(compressionField);
/*      */ 
/*      */       
/* 1344 */       char[] bitsPerSample = new char[this.numFrameComponents];
/* 1345 */       Arrays.fill(bitsPerSample, (char)(this.samplePrecision & 0xFF));
/*      */ 
/*      */       
/* 1348 */       TIFFField bitsPerSampleField = new TIFFField(base.getTag(258), 3, bitsPerSample.length, bitsPerSample);
/*      */ 
/*      */ 
/*      */       
/* 1352 */       dir.addTIFFField(bitsPerSampleField);
/*      */ 
/*      */ 
/*      */       
/* 1356 */       TIFFField imageLengthField = new TIFFField(base.getTag(257), this.numLines);
/*      */       
/* 1358 */       dir.addTIFFField(imageLengthField);
/*      */ 
/*      */ 
/*      */       
/* 1362 */       TIFFField imageWidthField = new TIFFField(base.getTag(256), this.samplesPerLine);
/*      */       
/* 1364 */       dir.addTIFFField(imageWidthField);
/*      */ 
/*      */ 
/*      */       
/* 1368 */       TIFFField samplesPerPixelField = new TIFFField(base.getTag(277), this.numFrameComponents);
/*      */       
/* 1370 */       dir.addTIFFField(samplesPerPixelField);
/*      */ 
/*      */       
/* 1373 */       IIOMetadataNode chroma = getStandardChromaNode();
/* 1374 */       if (chroma != null) {
/*      */         
/* 1376 */         IIOMetadataNode csType = (IIOMetadataNode)chroma.getElementsByTagName("ColorSpaceType").item(0);
/* 1377 */         String name = csType.getAttribute("name");
/* 1378 */         int photometricInterpretation = -1;
/* 1379 */         if (name.equals("GRAY")) {
/* 1380 */           photometricInterpretation = 1;
/*      */         }
/* 1382 */         else if (name.equals("YCbCr") || name.equals("PhotoYCC")) {
/*      */           
/* 1384 */           photometricInterpretation = 6;
/*      */         }
/* 1386 */         else if (name.equals("RGB")) {
/* 1387 */           photometricInterpretation = 2;
/*      */         }
/* 1389 */         else if (name.equals("CMYK") || name.equals("YCCK")) {
/*      */           
/* 1391 */           photometricInterpretation = 5;
/*      */         } 
/*      */ 
/*      */         
/* 1395 */         if (photometricInterpretation != -1) {
/*      */           
/* 1397 */           TIFFField photometricInterpretationField = new TIFFField(base.getTag(262), photometricInterpretation);
/*      */           
/* 1399 */           dir.addTIFFField(photometricInterpretationField);
/*      */         } 
/*      */         
/* 1402 */         if (this.hasAlpha) {
/* 1403 */           char[] extraSamples = { '\001' };
/*      */ 
/*      */ 
/*      */           
/* 1407 */           TIFFField extraSamplesField = new TIFFField(base.getTag(338), 3, extraSamples.length, extraSamples);
/*      */ 
/*      */ 
/*      */           
/* 1411 */           dir.addTIFFField(extraSamplesField);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1417 */     if (this.app0JFIFPresent) {
/* 1418 */       long[][] xResolution = { { this.Xdensity, 1L } };
/*      */       
/* 1420 */       TIFFField XResolutionField = new TIFFField(base.getTag(282), 5, 1, xResolution);
/*      */ 
/*      */ 
/*      */       
/* 1424 */       dir.addTIFFField(XResolutionField);
/*      */       
/* 1426 */       long[][] yResolution = { { this.Ydensity, 1L } };
/*      */       
/* 1428 */       TIFFField YResolutionField = new TIFFField(base.getTag(283), 5, 1, yResolution);
/*      */ 
/*      */ 
/*      */       
/* 1432 */       dir.addTIFFField(YResolutionField);
/*      */       
/* 1434 */       int resolutionUnit = 1;
/* 1435 */       switch (this.resUnits) {
/*      */         case 0:
/* 1437 */           resolutionUnit = 1;
/*      */         case 1:
/* 1439 */           resolutionUnit = 2;
/*      */           break;
/*      */         case 2:
/* 1442 */           resolutionUnit = 3;
/*      */           break;
/*      */       } 
/*      */ 
/*      */       
/* 1447 */       TIFFField ResolutionUnitField = new TIFFField(base.getTag(296), resolutionUnit);
/*      */       
/* 1449 */       dir.addTIFFField(ResolutionUnitField);
/*      */     } 
/*      */ 
/*      */     
/* 1453 */     byte[] jpegTablesData = null;
/* 1454 */     if (this.dqtPresent || this.dqtPresent) {
/*      */       
/* 1456 */       int jpegTablesLength = 2;
/* 1457 */       if (this.dqtPresent) {
/* 1458 */         Iterator<List> dqts = this.qtables.iterator();
/* 1459 */         while (dqts.hasNext()) {
/* 1460 */           Iterator<QTable> qtiter = ((List)dqts.next()).iterator();
/* 1461 */           while (qtiter.hasNext()) {
/* 1462 */             QTable qt = qtiter.next();
/* 1463 */             jpegTablesLength += 4 + qt.length;
/*      */           } 
/*      */         } 
/*      */       } 
/* 1467 */       if (this.dhtPresent) {
/* 1468 */         Iterator<List> dhts = this.htables.iterator();
/* 1469 */         while (dhts.hasNext()) {
/* 1470 */           Iterator<HuffmanTable> htiter = ((List)dhts.next()).iterator();
/* 1471 */           while (htiter.hasNext()) {
/* 1472 */             HuffmanTable ht = htiter.next();
/* 1473 */             jpegTablesLength += 4 + ht.length;
/*      */           } 
/*      */         } 
/*      */       } 
/* 1477 */       jpegTablesLength += 2;
/*      */ 
/*      */       
/* 1480 */       jpegTablesData = new byte[jpegTablesLength];
/*      */ 
/*      */       
/* 1483 */       jpegTablesData[0] = -1;
/* 1484 */       jpegTablesData[1] = -40;
/* 1485 */       int jpoff = 2;
/*      */       
/* 1487 */       if (this.dqtPresent) {
/* 1488 */         Iterator<List> dqts = this.qtables.iterator();
/* 1489 */         while (dqts.hasNext()) {
/* 1490 */           Iterator<QTable> qtiter = ((List)dqts.next()).iterator();
/* 1491 */           while (qtiter.hasNext()) {
/* 1492 */             jpegTablesData[jpoff++] = -1;
/* 1493 */             jpegTablesData[jpoff++] = -37;
/* 1494 */             QTable qt = qtiter.next();
/* 1495 */             int qtlength = qt.length + 2;
/* 1496 */             jpegTablesData[jpoff++] = (byte)((qtlength & 0xFF00) >> 8);
/*      */             
/* 1498 */             jpegTablesData[jpoff++] = (byte)(qtlength & 0xFF);
/* 1499 */             jpegTablesData[jpoff++] = (byte)((qt.elementPrecision & 0xF0) << 4 | qt.tableID & 0xF);
/*      */ 
/*      */             
/* 1502 */             int[] table = qt.table.getTable();
/* 1503 */             int qlen = table.length;
/* 1504 */             for (int i = 0; i < qlen; i++) {
/* 1505 */               jpegTablesData[jpoff + zigzag[i]] = (byte)table[i];
/*      */             }
/* 1507 */             jpoff += qlen;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 1512 */       if (this.dhtPresent) {
/* 1513 */         Iterator<List> dhts = this.htables.iterator();
/* 1514 */         while (dhts.hasNext()) {
/* 1515 */           Iterator<HuffmanTable> htiter = ((List)dhts.next()).iterator();
/* 1516 */           while (htiter.hasNext()) {
/* 1517 */             jpegTablesData[jpoff++] = -1;
/* 1518 */             jpegTablesData[jpoff++] = -60;
/* 1519 */             HuffmanTable ht = htiter.next();
/* 1520 */             int htlength = ht.length + 2;
/* 1521 */             jpegTablesData[jpoff++] = (byte)((htlength & 0xFF00) >> 8);
/*      */             
/* 1523 */             jpegTablesData[jpoff++] = (byte)(htlength & 0xFF);
/* 1524 */             jpegTablesData[jpoff++] = (byte)((ht.tableClass & 0xF) << 4 | ht.tableID & 0xF);
/*      */ 
/*      */             
/* 1527 */             short[] lengths = ht.table.getLengths();
/* 1528 */             int numLengths = lengths.length;
/* 1529 */             for (int i = 0; i < numLengths; i++) {
/* 1530 */               jpegTablesData[jpoff++] = (byte)lengths[i];
/*      */             }
/* 1532 */             short[] values = ht.table.getValues();
/* 1533 */             int numValues = values.length;
/* 1534 */             for (int j = 0; j < numValues; j++) {
/* 1535 */               jpegTablesData[jpoff++] = (byte)values[j];
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 1541 */       jpegTablesData[jpoff++] = -1;
/* 1542 */       jpegTablesData[jpoff] = -39;
/*      */     } 
/* 1544 */     if (jpegTablesData != null) {
/*      */       
/* 1546 */       TIFFField JPEGTablesField = new TIFFField(base.getTag(347), 7, jpegTablesData.length, jpegTablesData);
/*      */ 
/*      */ 
/*      */       
/* 1550 */       dir.addTIFFField(JPEGTablesField);
/*      */     } 
/*      */     
/* 1553 */     IIOMetadata tiffMetadata = dir.getAsMetadata();
/*      */     
/* 1555 */     if (this.exifData != null) {
/*      */       
/*      */       try {
/* 1558 */         Iterator<ImageReader> tiffReaders = ImageIO.getImageReadersByFormatName("TIFF");
/* 1559 */         if (tiffReaders != null && tiffReaders.hasNext()) {
/* 1560 */           ImageReader tiffReader = tiffReaders.next();
/* 1561 */           ByteArrayInputStream bais = new ByteArrayInputStream(this.exifData);
/*      */           
/* 1563 */           ImageInputStream exifStream = new MemoryCacheImageInputStream(bais);
/*      */           
/* 1565 */           tiffReader.setInput(exifStream);
/* 1566 */           IIOMetadata exifMetadata = tiffReader.getImageMetadata(0);
/* 1567 */           tiffMetadata.mergeTree(metadataName, exifMetadata
/* 1568 */               .getAsTree(metadataName));
/* 1569 */           tiffReader.reset();
/*      */         } 
/* 1571 */       } catch (IOException iOException) {}
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1576 */     return tiffMetadata.getAsTree(metadataName);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void initializeThumbnails() {
/* 1582 */     synchronized (this.thumbnails) {
/* 1583 */       if (!this.thumbnailsInitialized) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1588 */         if (this.app0JFIFPresent && this.jfifThumbnail != null) {
/* 1589 */           this.thumbnails.add(this.jfifThumbnail);
/*      */         }
/*      */ 
/*      */         
/* 1593 */         if (this.app0JFXXPresent && this.jfxxThumbnails != null) {
/* 1594 */           int numJFXX = this.jfxxThumbnails.size();
/* 1595 */           for (int i = 0; i < numJFXX; i++) {
/* 1596 */             IIOImage img = this.jfxxThumbnails.get(i);
/*      */             
/* 1598 */             BufferedImage jfxxThumbnail = (BufferedImage)img.getRenderedImage();
/* 1599 */             this.thumbnails.add(jfxxThumbnail);
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/* 1604 */         if (this.exifData != null) {
/*      */           
/*      */           try {
/* 1607 */             Iterator<ImageReader> tiffReaders = ImageIO.getImageReadersByFormatName("TIFF");
/* 1608 */             if (tiffReaders != null && tiffReaders.hasNext()) {
/*      */               
/* 1610 */               ImageReader tiffReader = tiffReaders.next();
/* 1611 */               ByteArrayInputStream bais = new ByteArrayInputStream(this.exifData);
/*      */               
/* 1613 */               ImageInputStream exifStream = new MemoryCacheImageInputStream(bais);
/*      */               
/* 1615 */               tiffReader.setInput(exifStream);
/* 1616 */               if (tiffReader.getNumImages(true) > 1) {
/*      */                 
/* 1618 */                 BufferedImage exifThumbnail = tiffReader.read(1, null);
/* 1619 */                 this.thumbnails.add(exifThumbnail);
/*      */               } 
/* 1621 */               tiffReader.reset();
/*      */             } 
/* 1623 */           } catch (IOException iOException) {}
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 1628 */         this.thumbnailsInitialized = true;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   int getNumThumbnails() throws IOException {
/* 1634 */     initializeThumbnails();
/* 1635 */     return this.thumbnails.size();
/*      */   }
/*      */   
/*      */   BufferedImage getThumbnail(int thumbnailIndex) throws IOException {
/* 1639 */     if (thumbnailIndex < 0) {
/* 1640 */       throw new IndexOutOfBoundsException("thumbnailIndex < 0!");
/*      */     }
/*      */     
/* 1643 */     initializeThumbnails();
/*      */     
/* 1645 */     if (thumbnailIndex >= this.thumbnails.size()) {
/* 1646 */       throw new IndexOutOfBoundsException("thumbnailIndex > getNumThumbnails()");
/*      */     }
/*      */ 
/*      */     
/* 1650 */     return this.thumbnails.get(thumbnailIndex);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\jpeg\CLibJPEGMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */