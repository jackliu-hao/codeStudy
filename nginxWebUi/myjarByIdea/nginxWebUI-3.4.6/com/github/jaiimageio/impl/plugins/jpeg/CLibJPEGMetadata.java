package com.github.jaiimageio.impl.plugins.jpeg;

import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
import com.github.jaiimageio.plugins.tiff.EXIFParentTIFFTagSet;
import com.github.jaiimageio.plugins.tiff.TIFFDirectory;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import com.github.jaiimageio.plugins.tiff.TIFFTag;
import com.github.jaiimageio.plugins.tiff.TIFFTagSet;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CLibJPEGMetadata extends IIOMetadata {
   static final String NATIVE_FORMAT = "javax_imageio_jpeg_image_1.0";
   static final String NATIVE_FORMAT_CLASS = "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat";
   static final String TIFF_FORMAT = "com_sun_media_imageio_plugins_tiff_image_1.0";
   static final String TIFF_FORMAT_CLASS = "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat";
   static final int TEM = 1;
   static final int SOF0 = 192;
   static final int SOF1 = 193;
   static final int SOF2 = 194;
   static final int SOF3 = 195;
   static final int DHT = 196;
   static final int SOF5 = 197;
   static final int SOF6 = 198;
   static final int SOF7 = 199;
   static final int JPG = 200;
   static final int SOF9 = 201;
   static final int SOF10 = 202;
   static final int SOF11 = 203;
   static final int DAC = 204;
   static final int SOF13 = 205;
   static final int SOF14 = 206;
   static final int SOF15 = 207;
   static final int RST0 = 208;
   static final int RST1 = 209;
   static final int RST2 = 210;
   static final int RST3 = 211;
   static final int RST4 = 212;
   static final int RST5 = 213;
   static final int RST6 = 214;
   static final int RST7 = 215;
   static final int RESTART_RANGE = 8;
   static final int SOI = 216;
   static final int EOI = 217;
   static final int SOS = 218;
   static final int DQT = 219;
   static final int DNL = 220;
   static final int DRI = 221;
   static final int DHP = 222;
   static final int EXP = 223;
   static final int APP0 = 224;
   static final int APP1 = 225;
   static final int APP2 = 226;
   static final int APP3 = 227;
   static final int APP4 = 228;
   static final int APP5 = 229;
   static final int APP6 = 230;
   static final int APP7 = 231;
   static final int APP8 = 232;
   static final int APP9 = 233;
   static final int APP10 = 234;
   static final int APP11 = 235;
   static final int APP12 = 236;
   static final int APP13 = 237;
   static final int APP14 = 238;
   static final int APP15 = 239;
   static final int COM = 254;
   static final int SOF55 = 247;
   static final int LSE = 242;
   static final int APPN_MIN = 224;
   static final int APPN_MAX = 239;
   static final int SOFN_MIN = 192;
   static final int SOFN_MAX = 207;
   static final int RST_MIN = 208;
   static final int RST_MAX = 215;
   static final int APP0_JFIF = 57344;
   static final int APP0_JFXX = 57345;
   static final int APP1_EXIF = 57600;
   static final int APP2_ICC = 57856;
   static final int APP14_ADOBE = 60928;
   static final int UNKNOWN_MARKER = 65535;
   static final int SOF_MARKER = 49152;
   static final int JFIF_RESUNITS_ASPECT = 0;
   static final int JFIF_RESUNITS_DPI = 1;
   static final int JFIF_RESUNITS_DPC = 2;
   static final int THUMBNAIL_JPEG = 16;
   static final int THUMBNAIL_PALETTE = 17;
   static final int THUMBNAIL_RGB = 18;
   static final int ADOBE_TRANSFORM_UNKNOWN = 0;
   static final int ADOBE_TRANSFORM_YCC = 1;
   static final int ADOBE_TRANSFORM_YCCK = 2;
   static final int[] zigzag = new int[]{0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63};
   private boolean isReadOnly;
   boolean app0JFIFPresent;
   int majorVersion;
   int minorVersion;
   int resUnits;
   int Xdensity;
   int Ydensity;
   int thumbWidth;
   int thumbHeight;
   BufferedImage jfifThumbnail;
   boolean app0JFXXPresent;
   List extensionCodes;
   List jfxxThumbnails;
   boolean app2ICCPresent;
   ICC_Profile profile;
   boolean dqtPresent;
   List qtables;
   boolean dhtPresent;
   List htables;
   boolean driPresent;
   int driInterval;
   boolean comPresent;
   List comments;
   boolean unknownPresent;
   List markerTags;
   List unknownData;
   boolean app14AdobePresent;
   int version;
   int flags0;
   int flags1;
   int transform;
   boolean sofPresent;
   int sofProcess;
   int samplePrecision;
   int numLines;
   int samplesPerLine;
   int numFrameComponents;
   int[] componentId;
   int[] hSamplingFactor;
   int[] vSamplingFactor;
   int[] qtableSelector;
   boolean sosPresent;
   int numScanComponents;
   int[] componentSelector;
   int[] dcHuffTable;
   int[] acHuffTable;
   int startSpectralSelection;
   int endSpectralSelection;
   int approxHigh;
   int approxLow;
   byte[] exifData;
   private List markers;
   private boolean hasAlpha;
   private boolean thumbnailsInitialized;
   private List thumbnails;

   private static IIOImage getThumbnail(ImageInputStream stream, int len, int thumbnailType, int w, int h) throws IOException {
      long startPos = stream.getStreamPosition();
      IIOImage result;
      if (thumbnailType == 16) {
         Iterator readers = ImageIO.getImageReaders(stream);
         if (readers == null || !readers.hasNext()) {
            return null;
         }

         ImageReader reader = (ImageReader)readers.next();
         reader.setInput(stream);
         BufferedImage image = reader.read(0, (ImageReadParam)null);
         IIOMetadata metadata = null;

         try {
            metadata = reader.getImageMetadata(0);
         } catch (Exception var16) {
         }

         result = new IIOImage(image, (List)null, metadata);
      } else {
         byte numBands;
         Object cm;
         byte[] palette;
         if (thumbnailType != 17) {
            if (len < 3 * w * h) {
               return null;
            }

            numBands = 3;
            ColorSpace cs = ColorSpace.getInstance(1000);
            cm = new ComponentColorModel(cs, false, false, 1, 0);
         } else {
            if (len < 768 + w * h) {
               return null;
            }

            numBands = 1;
            palette = new byte[768];
            stream.readFully(palette);
            byte[] r = new byte[256];
            byte[] g = new byte[256];
            byte[] b = new byte[256];
            int i = 0;

            for(int off = 0; i < 256; ++i) {
               r[i] = palette[off++];
               g[i] = palette[off++];
               b[i] = palette[off++];
            }

            cm = new IndexColorModel(8, 256, r, g, b);
         }

         palette = new byte[w * h * numBands];
         stream.readFully(palette);
         DataBufferByte db = new DataBufferByte(palette, palette.length);
         WritableRaster wr = Raster.createInterleavedRaster(db, w, h, w * numBands, numBands, new int[]{0, 1, 2}, (Point)null);
         BufferedImage image = new BufferedImage((ColorModel)cm, wr, false, (Hashtable)null);
         result = new IIOImage(image, (List)null, (IIOMetadata)null);
      }

      stream.seek(startPos + (long)len);
      return result;
   }

   CLibJPEGMetadata() {
      super(true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", new String[]{"com_sun_media_imageio_plugins_tiff_image_1.0"}, new String[]{"com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat"});
      this.isReadOnly = true;
      this.majorVersion = 1;
      this.minorVersion = 2;
      this.Xdensity = 1;
      this.Ydensity = 1;
      this.thumbWidth = 0;
      this.thumbHeight = 0;
      this.profile = null;
      this.version = 100;
      this.flags0 = 0;
      this.flags1 = 0;
      this.samplePrecision = 8;
      this.exifData = null;
      this.markers = null;
      this.hasAlpha = false;
      this.thumbnailsInitialized = false;
      this.thumbnails = new ArrayList();
      this.isReadOnly = this.isReadOnly;
   }

   CLibJPEGMetadata(ImageInputStream stream) throws IIOException {
      this();

      try {
         this.initializeFromStream(stream);
      } catch (IOException var3) {
         throw new IIOException("Cannot initialize JPEG metadata!", var3);
      }
   }

   private synchronized void initializeFromStream(ImageInputStream iis) throws IOException {
      iis.mark();
      iis.setByteOrder(ByteOrder.BIG_ENDIAN);
      this.markers = new ArrayList();
      boolean isICCProfileValid = true;
      int numICCProfileChunks = 0;
      long[] iccProfileChunkOffsets = null;
      int[] iccProfileChunkLengths = null;

      int code;
      int dataLength;
      int i;
      label277:
      while(true) {
         while(true) {
            while(true) {
               while(true) {
                  try {
                     if (iis.read() == 255) {
                        code = iis.read();
                        if (code != 0 && code != 255 && code != 216 && code != 1 && (code < 208 || code > 215)) {
                           if (code == 217) {
                              break label277;
                           }

                           dataLength = iis.readUnsignedShort() - 2;
                           if (224 <= code && code <= 239) {
                              long pos = iis.getStreamPosition();
                              boolean appnAdded = false;
                              byte[] b;
                              String id;
                              int chunkNum;
                              int numChunks;
                              switch (code) {
                                 case 224:
                                    if (dataLength >= 5) {
                                       b = new byte[5];
                                       iis.readFully(b);
                                       id = new String(b);
                                       if (id.startsWith("JFIF") && !this.app0JFIFPresent) {
                                          this.app0JFIFPresent = true;
                                          this.markers.add(new Integer(57344));
                                          this.majorVersion = iis.read();
                                          this.minorVersion = iis.read();
                                          this.resUnits = iis.read();
                                          this.Xdensity = iis.readUnsignedShort();
                                          this.Ydensity = iis.readUnsignedShort();
                                          this.thumbWidth = iis.read();
                                          this.thumbHeight = iis.read();
                                          if (this.thumbWidth > 0 && this.thumbHeight > 0) {
                                             IIOImage imiio = getThumbnail(iis, dataLength - 14, 18, this.thumbWidth, this.thumbHeight);
                                             if (imiio != null) {
                                                this.jfifThumbnail = (BufferedImage)imiio.getRenderedImage();
                                             }
                                          }

                                          appnAdded = true;
                                       } else if (id.startsWith("JFXX")) {
                                          if (!this.app0JFXXPresent) {
                                             this.extensionCodes = new ArrayList(1);
                                             this.jfxxThumbnails = new ArrayList(1);
                                             this.app0JFXXPresent = true;
                                          }

                                          this.markers.add(new Integer(57345));
                                          chunkNum = iis.read();
                                          this.extensionCodes.add(new Integer(chunkNum));
                                          numChunks = 0;
                                          int h = 0;
                                          int offset = 6;
                                          if (chunkNum != 16) {
                                             numChunks = iis.read();
                                             h = iis.read();
                                             offset += 2;
                                          }

                                          IIOImage imiio = getThumbnail(iis, dataLength - offset, chunkNum, numChunks, h);
                                          if (imiio != null) {
                                             this.jfxxThumbnails.add(imiio);
                                          }

                                          appnAdded = true;
                                       }
                                    }
                                    break;
                                 case 225:
                                    if (dataLength >= 6) {
                                       b = new byte[6];
                                       iis.readFully(b);
                                       if (b[0] == 69 && b[1] == 120 && b[2] == 105 && b[3] == 102 && b[4] == 0 && b[5] == 0) {
                                          this.exifData = new byte[dataLength - 6];
                                          iis.readFully(this.exifData);
                                       }
                                    }
                                 case 226:
                                    if (dataLength >= 12) {
                                       b = new byte[12];
                                       iis.readFully(b);
                                       id = new String(b);
                                       if (id.startsWith("ICC_PROFILE")) {
                                          if (!isICCProfileValid) {
                                             iis.skipBytes(dataLength - 12);
                                             continue;
                                          }

                                          chunkNum = iis.read();
                                          numChunks = iis.read();
                                          if (numChunks == 0 || chunkNum == 0 || chunkNum > numChunks || this.app2ICCPresent && (numChunks != numICCProfileChunks || iccProfileChunkOffsets[chunkNum] != 0L)) {
                                             isICCProfileValid = false;
                                             iis.skipBytes(dataLength - 14);
                                             continue;
                                          }

                                          if (!this.app2ICCPresent) {
                                             this.app2ICCPresent = true;
                                             this.markers.add(new Integer(57856));
                                             numICCProfileChunks = numChunks;
                                             if (numChunks == 1) {
                                                b = new byte[dataLength - 14];
                                                iis.readFully(b);
                                                this.profile = ICC_Profile.getInstance(b);
                                             } else {
                                                iccProfileChunkOffsets = new long[numChunks + 1];
                                                iccProfileChunkLengths = new int[numChunks + 1];
                                                iccProfileChunkOffsets[chunkNum] = iis.getStreamPosition();
                                                iccProfileChunkLengths[chunkNum] = dataLength - 14;
                                                iis.skipBytes(dataLength - 14);
                                             }
                                          } else {
                                             iccProfileChunkOffsets[chunkNum] = iis.getStreamPosition();
                                             iccProfileChunkLengths[chunkNum] = dataLength - 14;
                                             iis.skipBytes(dataLength - 14);
                                          }

                                          appnAdded = true;
                                       }
                                    }
                                    break;
                                 case 238:
                                    if (dataLength >= 5) {
                                       b = new byte[5];
                                       iis.readFully(b);
                                       id = new String(b);
                                       if (id.startsWith("Adobe") && !this.app14AdobePresent) {
                                          this.app14AdobePresent = true;
                                          this.markers.add(new Integer(60928));
                                          this.version = iis.readUnsignedShort();
                                          this.flags0 = iis.readUnsignedShort();
                                          this.flags1 = iis.readUnsignedShort();
                                          this.transform = iis.read();
                                          iis.skipBytes(dataLength - 12);
                                          appnAdded = true;
                                       }
                                    }
                                    break;
                                 default:
                                    appnAdded = false;
                              }

                              if (!appnAdded) {
                                 iis.seek(pos);
                                 this.addUnknownMarkerSegment(iis, code, dataLength);
                              }
                           } else {
                              ArrayList l;
                              if (code == 219) {
                                 if (!this.dqtPresent) {
                                    this.dqtPresent = true;
                                    this.qtables = new ArrayList(1);
                                 }

                                 this.markers.add(new Integer(219));
                                 l = new ArrayList(1);

                                 do {
                                    QTable t = new QTable(iis);
                                    l.add(t);
                                    dataLength -= t.length;
                                 } while(dataLength > 0);

                                 this.qtables.add(l);
                              } else if (code == 196) {
                                 if (!this.dhtPresent) {
                                    this.dhtPresent = true;
                                    this.htables = new ArrayList(1);
                                 }

                                 this.markers.add(new Integer(196));
                                 l = new ArrayList(1);

                                 do {
                                    HuffmanTable t = new HuffmanTable(iis);
                                    l.add(t);
                                    dataLength -= t.length;
                                 } while(dataLength > 0);

                                 this.htables.add(l);
                              } else if (code == 221) {
                                 if (!this.driPresent) {
                                    this.driPresent = true;
                                 }

                                 this.markers.add(new Integer(221));
                                 this.driInterval = iis.readUnsignedShort();
                              } else if (code == 254) {
                                 if (!this.comPresent) {
                                    this.comPresent = true;
                                    this.comments = new ArrayList(1);
                                 }

                                 this.markers.add(new Integer(254));
                                 byte[] b = new byte[dataLength];
                                 iis.readFully(b);
                                 this.comments.add(b);
                              } else if ((code < 192 || code > 207) && code != 247) {
                                 if (code == 218) {
                                    if (!this.sosPresent) {
                                       this.sosPresent = true;
                                       this.numScanComponents = iis.read();
                                       this.componentSelector = new int[this.numScanComponents];
                                       this.dcHuffTable = new int[this.numScanComponents];
                                       this.acHuffTable = new int[this.numScanComponents];

                                       for(i = 0; i < this.numScanComponents; ++i) {
                                          this.componentSelector[i] = iis.read();
                                          this.dcHuffTable[i] = (int)iis.readBits(4);
                                          this.acHuffTable[i] = (int)iis.readBits(4);
                                       }

                                       this.startSpectralSelection = iis.read();
                                       this.endSpectralSelection = iis.read();
                                       this.approxHigh = (int)iis.readBits(4);
                                       this.approxLow = (int)iis.readBits(4);
                                       this.markers.add(new Integer(218));
                                    }
                                    break label277;
                                 }

                                 this.addUnknownMarkerSegment(iis, code, dataLength);
                              } else if (!this.sofPresent) {
                                 this.sofPresent = true;
                                 this.sofProcess = code - 192;
                                 this.samplePrecision = iis.read();
                                 this.numLines = iis.readUnsignedShort();
                                 this.samplesPerLine = iis.readUnsignedShort();
                                 this.numFrameComponents = iis.read();
                                 this.componentId = new int[this.numFrameComponents];
                                 this.hSamplingFactor = new int[this.numFrameComponents];
                                 this.vSamplingFactor = new int[this.numFrameComponents];
                                 this.qtableSelector = new int[this.numFrameComponents];

                                 for(i = 0; i < this.numFrameComponents; ++i) {
                                    this.componentId[i] = iis.read();
                                    this.hSamplingFactor[i] = (int)iis.readBits(4);
                                    this.vSamplingFactor[i] = (int)iis.readBits(4);
                                    this.qtableSelector[i] = iis.read();
                                 }

                                 this.markers.add(new Integer(49152));
                              }
                           }
                        }
                     }
                  } catch (EOFException var18) {
                     break label277;
                  }
               }
            }
         }
      }

      if (this.app2ICCPresent && isICCProfileValid && this.profile == null) {
         code = 0;

         for(dataLength = 1; dataLength <= numICCProfileChunks; ++dataLength) {
            if (iccProfileChunkOffsets[dataLength] == 0L) {
               isICCProfileValid = false;
               break;
            }

            code += iccProfileChunkLengths[dataLength];
         }

         if (isICCProfileValid) {
            byte[] b = new byte[code];
            i = 0;

            for(int i = 1; i <= numICCProfileChunks; ++i) {
               iis.seek(iccProfileChunkOffsets[i]);
               iis.read(b, i, iccProfileChunkLengths[i]);
               i += iccProfileChunkLengths[i];
            }

            this.profile = ICC_Profile.getInstance(b);
         }
      }

      iis.reset();
   }

   private void addUnknownMarkerSegment(ImageInputStream stream, int code, int len) throws IOException {
      if (!this.unknownPresent) {
         this.unknownPresent = true;
         this.markerTags = new ArrayList(1);
         this.unknownData = new ArrayList(1);
      }

      this.markerTags.add(new Integer(code));
      byte[] b = new byte[len];
      stream.readFully(b);
      this.unknownData.add(b);
      this.markers.add(new Integer(65535));
   }

   public boolean isReadOnly() {
      return this.isReadOnly;
   }

   public Node getAsTree(String formatName) {
      if (formatName.equals(this.nativeMetadataFormatName)) {
         return this.getNativeTree();
      } else if (formatName.equals("javax_imageio_1.0")) {
         return this.getStandardTree();
      } else if (formatName.equals("com_sun_media_imageio_plugins_tiff_image_1.0")) {
         return this.getTIFFTree();
      } else {
         throw new IllegalArgumentException("Not a recognized format!");
      }
   }

   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
      if (this.isReadOnly) {
         throw new IllegalStateException("isReadOnly() == true!");
      }
   }

   public void reset() {
      if (this.isReadOnly) {
         throw new IllegalStateException("isReadOnly() == true!");
      }
   }

   private Node getNativeTree() {
      int jfxxIndex = 0;
      int dqtIndex = 0;
      int dhtIndex = 0;
      int comIndex = 0;
      int unknownIndex = 0;
      IIOMetadataNode root = new IIOMetadataNode(this.nativeMetadataFormatName);
      IIOMetadataNode JPEGvariety = new IIOMetadataNode("JPEGvariety");
      root.appendChild(JPEGvariety);
      IIOMetadataNode markerSequence = new IIOMetadataNode("markerSequence");
      root.appendChild(markerSequence);
      IIOMetadataNode app0JFIF = null;
      if (this.app0JFIFPresent || this.app0JFXXPresent || this.app2ICCPresent) {
         app0JFIF = new IIOMetadataNode("app0JFIF");
         app0JFIF.setAttribute("majorVersion", Integer.toString(this.majorVersion));
         app0JFIF.setAttribute("minorVersion", Integer.toString(this.minorVersion));
         app0JFIF.setAttribute("resUnits", Integer.toString(this.resUnits));
         app0JFIF.setAttribute("Xdensity", Integer.toString(this.Xdensity));
         app0JFIF.setAttribute("Ydensity", Integer.toString(this.Ydensity));
         app0JFIF.setAttribute("thumbWidth", Integer.toString(this.thumbWidth));
         app0JFIF.setAttribute("thumbHeight", Integer.toString(this.thumbHeight));
         JPEGvariety.appendChild(app0JFIF);
      }

      IIOMetadataNode JFXX = null;
      if (this.app0JFXXPresent) {
         JFXX = new IIOMetadataNode("JFXX");
         app0JFIF.appendChild(JFXX);
      }

      Iterator markerIter = this.markers.iterator();

      while(true) {
         while(markerIter.hasNext()) {
            int marker = (Integer)markerIter.next();
            IIOMetadataNode dqtable;
            IIOMetadataNode dhtable;
            List tables;
            int numTables;
            switch (marker) {
               case 196:
                  IIOMetadataNode dht = new IIOMetadataNode("dht");
                  tables = (List)this.htables.get(dhtIndex++);
                  numTables = tables.size();

                  for(int j = 0; j < numTables; ++j) {
                     dhtable = new IIOMetadataNode("dhtable");
                     HuffmanTable t = (HuffmanTable)tables.get(j);
                     dhtable.setAttribute("class", Integer.toString(t.tableClass));
                     dhtable.setAttribute("htableId", Integer.toString(t.tableID));
                     dhtable.setUserObject(t.table);
                     dht.appendChild(dhtable);
                  }

                  markerSequence.appendChild(dht);
                  break;
               case 218:
                  IIOMetadataNode sos = new IIOMetadataNode("sos");
                  sos.setAttribute("numScanComponents", Integer.toString(this.numScanComponents));
                  sos.setAttribute("startSpectralSelection", Integer.toString(this.startSpectralSelection));
                  sos.setAttribute("endSpectralSelection", Integer.toString(this.endSpectralSelection));
                  sos.setAttribute("approxHigh", Integer.toString(this.approxHigh));
                  sos.setAttribute("approxLow", Integer.toString(this.approxLow));

                  for(int i = 0; i < this.numScanComponents; ++i) {
                     IIOMetadataNode scanComponentSpec = new IIOMetadataNode("scanComponentSpec");
                     scanComponentSpec.setAttribute("componentSelector", Integer.toString(this.componentSelector[i]));
                     scanComponentSpec.setAttribute("dcHuffTable", Integer.toString(this.dcHuffTable[i]));
                     scanComponentSpec.setAttribute("acHuffTable", Integer.toString(this.acHuffTable[i]));
                     sos.appendChild(scanComponentSpec);
                  }

                  markerSequence.appendChild(sos);
                  break;
               case 219:
                  IIOMetadataNode dqt = new IIOMetadataNode("dqt");
                  tables = (List)this.qtables.get(dqtIndex++);
                  numTables = tables.size();

                  for(int j = 0; j < numTables; ++j) {
                     dqtable = new IIOMetadataNode("dqtable");
                     QTable t = (QTable)tables.get(j);
                     dqtable.setAttribute("elementPrecision", Integer.toString(t.elementPrecision));
                     dqtable.setAttribute("qtableId", Integer.toString(t.tableID));
                     dqtable.setUserObject(t.table);
                     dqt.appendChild(dqtable);
                  }

                  markerSequence.appendChild(dqt);
                  break;
               case 221:
                  dqtable = new IIOMetadataNode("dri");
                  dqtable.setAttribute("interval", Integer.toString(this.driInterval));
                  markerSequence.appendChild(dqtable);
                  break;
               case 254:
                  dhtable = new IIOMetadataNode("com");
                  dhtable.setUserObject(this.comments.get(comIndex++));
                  markerSequence.appendChild(dhtable);
                  break;
               case 49152:
                  IIOMetadataNode sof = new IIOMetadataNode("sof");
                  sof.setAttribute("process", Integer.toString(this.sofProcess));
                  sof.setAttribute("samplePrecision", Integer.toString(this.samplePrecision));
                  sof.setAttribute("numLines", Integer.toString(this.numLines));
                  sof.setAttribute("samplesPerLine", Integer.toString(this.samplesPerLine));
                  sof.setAttribute("numFrameComponents", Integer.toString(this.numFrameComponents));

                  for(int i = 0; i < this.numFrameComponents; ++i) {
                     IIOMetadataNode componentSpec = new IIOMetadataNode("componentSpec");
                     componentSpec.setAttribute("componentId", Integer.toString(this.componentId[i]));
                     componentSpec.setAttribute("HsamplingFactor", Integer.toString(this.hSamplingFactor[i]));
                     componentSpec.setAttribute("VsamplingFactor", Integer.toString(this.vSamplingFactor[i]));
                     componentSpec.setAttribute("QtableSelector", Integer.toString(this.qtableSelector[i]));
                     sof.appendChild(componentSpec);
                  }

                  markerSequence.appendChild(sof);
               case 57344:
               default:
                  break;
               case 57345:
                  IIOMetadataNode app0JFXX = new IIOMetadataNode("app0JFXX");
                  Integer extensionCode = (Integer)this.extensionCodes.get(jfxxIndex);
                  app0JFXX.setAttribute("extensionCode", extensionCode.toString());
                  IIOMetadataNode JFIFthumb = null;
                  switch (extensionCode) {
                     case 16:
                        JFIFthumb = new IIOMetadataNode("JFIFthumbJPEG");
                        break;
                     case 17:
                        JFIFthumb = new IIOMetadataNode("JFIFthumbPalette");
                        break;
                     case 18:
                        JFIFthumb = new IIOMetadataNode("JFIFthumbRGB");
                  }

                  if (JFIFthumb != null) {
                     IIOImage img = (IIOImage)this.jfxxThumbnails.get(jfxxIndex++);
                     if (extensionCode == 16) {
                        IIOMetadata thumbMetadata = img.getMetadata();
                        if (thumbMetadata != null) {
                           Node thumbTree = thumbMetadata.getAsTree(this.nativeMetadataFormatName);
                           if (thumbTree instanceof IIOMetadataNode) {
                              IIOMetadataNode elt = (IIOMetadataNode)thumbTree;
                              NodeList elts = elt.getElementsByTagName("markerSequence");
                              if (elts.getLength() > 0) {
                                 JFIFthumb.appendChild(elts.item(0));
                              }
                           }
                        }
                     } else {
                        BufferedImage thumb = (BufferedImage)img.getRenderedImage();
                        JFIFthumb.setAttribute("thumbWidth", Integer.toString(thumb.getWidth()));
                        JFIFthumb.setAttribute("thumbHeight", Integer.toString(thumb.getHeight()));
                     }

                     JFIFthumb.setUserObject(img);
                     app0JFXX.appendChild(JFIFthumb);
                  }

                  JFXX.appendChild(app0JFXX);
                  break;
               case 57856:
                  IIOMetadataNode app2ICC = new IIOMetadataNode("app2ICC");
                  app2ICC.setUserObject(this.profile);
                  app0JFIF.appendChild(app2ICC);
                  break;
               case 60928:
                  IIOMetadataNode app14Adobe = new IIOMetadataNode("app14Adobe");
                  app14Adobe.setAttribute("version", Integer.toString(this.version));
                  app14Adobe.setAttribute("flags0", Integer.toString(this.flags0));
                  app14Adobe.setAttribute("flags1", Integer.toString(this.flags1));
                  app14Adobe.setAttribute("transform", Integer.toString(this.transform));
                  markerSequence.appendChild(app14Adobe);
                  break;
               case 65535:
                  IIOMetadataNode unknown = new IIOMetadataNode("unknown");
                  Integer markerTag = (Integer)this.markerTags.get(unknownIndex);
                  unknown.setAttribute("MarkerTag", markerTag.toString());
                  unknown.setUserObject(this.unknownData.get(unknownIndex++));
                  markerSequence.appendChild(unknown);
            }
         }

         return root;
      }
   }

   protected IIOMetadataNode getStandardChromaNode() {
      if (!this.sofPresent) {
         return null;
      } else {
         IIOMetadataNode chroma = new IIOMetadataNode("Chroma");
         IIOMetadataNode csType = new IIOMetadataNode("ColorSpaceType");
         chroma.appendChild(csType);
         IIOMetadataNode numChanNode = new IIOMetadataNode("NumChannels");
         chroma.appendChild(numChanNode);
         numChanNode.setAttribute("value", Integer.toString(this.numFrameComponents));
         if (this.app0JFIFPresent) {
            if (this.numFrameComponents == 1) {
               csType.setAttribute("name", "GRAY");
            } else {
               csType.setAttribute("name", "YCbCr");
            }

            return chroma;
         } else if (this.app14AdobePresent) {
            switch (this.transform) {
               case 0:
                  if (this.numFrameComponents == 3) {
                     csType.setAttribute("name", "RGB");
                  } else if (this.numFrameComponents == 4) {
                     csType.setAttribute("name", "CMYK");
                  }
                  break;
               case 1:
                  csType.setAttribute("name", "YCbCr");
                  break;
               case 2:
                  csType.setAttribute("name", "YCCK");
            }

            return chroma;
         } else {
            this.hasAlpha = false;
            if (this.numFrameComponents < 3) {
               csType.setAttribute("name", "GRAY");
               if (this.numFrameComponents == 2) {
                  this.hasAlpha = true;
               }

               return chroma;
            } else {
               boolean idsAreJFIF = true;

               int hfactor;
               for(int i = 0; i < this.componentId.length; ++i) {
                  hfactor = this.componentId[i];
                  if (hfactor < 1 || hfactor >= this.componentId.length) {
                     idsAreJFIF = false;
                  }
               }

               if (idsAreJFIF) {
                  csType.setAttribute("name", "YCbCr");
                  if (this.numFrameComponents == 4) {
                     this.hasAlpha = true;
                  }

                  return chroma;
               } else if (this.componentId[0] == 82 && this.componentId[1] == 71 && this.componentId[2] == 66) {
                  csType.setAttribute("name", "RGB");
                  if (this.numFrameComponents == 4 && this.componentId[3] == 65) {
                     this.hasAlpha = true;
                  }

                  return chroma;
               } else if (this.componentId[0] == 89 && this.componentId[1] == 67 && this.componentId[2] == 99) {
                  csType.setAttribute("name", "PhotoYCC");
                  if (this.numFrameComponents == 4 && this.componentId[3] == 65) {
                     this.hasAlpha = true;
                  }

                  return chroma;
               } else {
                  boolean subsampled = false;
                  hfactor = this.hSamplingFactor[0];
                  int vfactor = this.vSamplingFactor[0];

                  for(int i = 1; i < this.componentId.length; ++i) {
                     if (this.hSamplingFactor[i] != hfactor || this.vSamplingFactor[i] != vfactor) {
                        subsampled = true;
                        break;
                     }
                  }

                  if (subsampled) {
                     csType.setAttribute("name", "YCbCr");
                     if (this.numFrameComponents == 4) {
                        this.hasAlpha = true;
                     }

                     return chroma;
                  } else {
                     if (this.numFrameComponents == 3) {
                        csType.setAttribute("name", "RGB");
                     } else {
                        csType.setAttribute("name", "CMYK");
                     }

                     return chroma;
                  }
               }
            }
         }
      }
   }

   protected IIOMetadataNode getStandardCompressionNode() {
      IIOMetadataNode compression = null;
      if (this.sofPresent || this.sosPresent) {
         compression = new IIOMetadataNode("Compression");
         if (this.sofPresent) {
            boolean isLossless = this.sofProcess == 3 || this.sofProcess == 7 || this.sofProcess == 11 || this.sofProcess == 15 || this.sofProcess == 55;
            IIOMetadataNode name = new IIOMetadataNode("CompressionTypeName");
            String compressionType = isLossless ? (this.sofProcess == 55 ? "JPEG-LS" : "JPEG-LOSSLESS") : "JPEG";
            name.setAttribute("value", compressionType);
            compression.appendChild(name);
            IIOMetadataNode lossless = new IIOMetadataNode("Lossless");
            lossless.setAttribute("value", isLossless ? "true" : "false");
            compression.appendChild(lossless);
         }

         if (this.sosPresent) {
            IIOMetadataNode prog = new IIOMetadataNode("NumProgressiveScans");
            prog.setAttribute("value", "1");
            compression.appendChild(prog);
         }
      }

      return compression;
   }

   protected IIOMetadataNode getStandardDimensionNode() {
      IIOMetadataNode dim = new IIOMetadataNode("Dimension");
      IIOMetadataNode orient = new IIOMetadataNode("ImageOrientation");
      orient.setAttribute("value", "normal");
      dim.appendChild(orient);
      if (this.app0JFIFPresent) {
         float aspectRatio;
         if (this.resUnits == 0) {
            aspectRatio = (float)this.Xdensity / (float)this.Ydensity;
         } else {
            aspectRatio = (float)this.Ydensity / (float)this.Xdensity;
         }

         IIOMetadataNode aspect = new IIOMetadataNode("PixelAspectRatio");
         aspect.setAttribute("value", Float.toString(aspectRatio));
         dim.insertBefore(aspect, orient);
         if (this.resUnits != 0) {
            float scale = this.resUnits == 1 ? 25.4F : 10.0F;
            IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
            horiz.setAttribute("value", Float.toString(scale / (float)this.Xdensity));
            dim.appendChild(horiz);
            IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
            vert.setAttribute("value", Float.toString(scale / (float)this.Ydensity));
            dim.appendChild(vert);
         }
      }

      return dim;
   }

   protected IIOMetadataNode getStandardTextNode() {
      IIOMetadataNode text = null;
      if (this.comPresent) {
         text = new IIOMetadataNode("Text");

         IIOMetadataNode entry;
         for(Iterator iter = this.comments.iterator(); iter.hasNext(); text.appendChild(entry)) {
            entry = new IIOMetadataNode("TextEntry");
            entry.setAttribute("keyword", "comment");
            byte[] data = (byte[])((byte[])iter.next());

            try {
               entry.setAttribute("value", new String(data, "ISO-8859-1"));
            } catch (UnsupportedEncodingException var6) {
               entry.setAttribute("value", new String(data));
            }
         }
      }

      return text;
   }

   protected IIOMetadataNode getStandardTransparencyNode() {
      IIOMetadataNode trans = null;
      if (this.hasAlpha) {
         trans = new IIOMetadataNode("Transparency");
         IIOMetadataNode alpha = new IIOMetadataNode("Alpha");
         alpha.setAttribute("value", "nonpremultiplied");
         trans.appendChild(alpha);
      }

      return trans;
   }

   private Node getTIFFTree() {
      String metadataName = "com_sun_media_imageio_plugins_tiff_image_1.0";
      BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
      TIFFDirectory dir = new TIFFDirectory(new TIFFTagSet[]{base, EXIFParentTIFFTagSet.getInstance()}, (TIFFTag)null);
      TIFFField JPEGTablesField;
      TIFFField YResolutionField;
      TIFFField ResolutionUnitField;
      int numValues;
      if (this.sofPresent) {
         int compression = 7;
         JPEGTablesField = new TIFFField(base.getTag(259), compression);
         dir.addTIFFField(JPEGTablesField);
         char[] bitsPerSample = new char[this.numFrameComponents];
         Arrays.fill(bitsPerSample, (char)(this.samplePrecision & 255));
         YResolutionField = new TIFFField(base.getTag(258), 3, bitsPerSample.length, bitsPerSample);
         dir.addTIFFField(YResolutionField);
         TIFFField imageLengthField = new TIFFField(base.getTag(257), this.numLines);
         dir.addTIFFField(imageLengthField);
         ResolutionUnitField = new TIFFField(base.getTag(256), this.samplesPerLine);
         dir.addTIFFField(ResolutionUnitField);
         TIFFField samplesPerPixelField = new TIFFField(base.getTag(277), this.numFrameComponents);
         dir.addTIFFField(samplesPerPixelField);
         IIOMetadataNode chroma = this.getStandardChromaNode();
         if (chroma != null) {
            IIOMetadataNode csType = (IIOMetadataNode)chroma.getElementsByTagName("ColorSpaceType").item(0);
            String name = csType.getAttribute("name");
            numValues = -1;
            if (name.equals("GRAY")) {
               numValues = 1;
            } else if (!name.equals("YCbCr") && !name.equals("PhotoYCC")) {
               if (name.equals("RGB")) {
                  numValues = 2;
               } else if (name.equals("CMYK") || name.equals("YCCK")) {
                  numValues = 5;
               }
            } else {
               numValues = 6;
            }

            if (numValues != -1) {
               TIFFField photometricInterpretationField = new TIFFField(base.getTag(262), numValues);
               dir.addTIFFField(photometricInterpretationField);
            }

            if (this.hasAlpha) {
               char[] extraSamples = new char[]{'\u0001'};
               TIFFField extraSamplesField = new TIFFField(base.getTag(338), 3, extraSamples.length, extraSamples);
               dir.addTIFFField(extraSamplesField);
            }
         }
      }

      if (this.app0JFIFPresent) {
         long[][] xResolution = new long[][]{{(long)this.Xdensity, 1L}};
         JPEGTablesField = new TIFFField(base.getTag(282), 5, 1, xResolution);
         dir.addTIFFField(JPEGTablesField);
         long[][] yResolution = new long[][]{{(long)this.Ydensity, 1L}};
         YResolutionField = new TIFFField(base.getTag(283), 5, 1, yResolution);
         dir.addTIFFField(YResolutionField);
         int resolutionUnit = 1;
         switch (this.resUnits) {
            case 0:
               int resolutionUnit = true;
            case 1:
               resolutionUnit = 2;
               break;
            case 2:
               resolutionUnit = 3;
         }

         ResolutionUnitField = new TIFFField(base.getTag(296), resolutionUnit);
         dir.addTIFFField(ResolutionUnitField);
      }

      byte[] jpegTablesData = null;
      Iterator tiffReaders;
      if (this.dqtPresent || this.dqtPresent) {
         int jpegTablesLength = 2;
         Iterator dhts;
         if (this.dqtPresent) {
            tiffReaders = this.qtables.iterator();

            QTable qt;
            while(tiffReaders.hasNext()) {
               for(dhts = ((List)tiffReaders.next()).iterator(); dhts.hasNext(); jpegTablesLength += 4 + qt.length) {
                  qt = (QTable)dhts.next();
               }
            }
         }

         if (this.dhtPresent) {
            tiffReaders = this.htables.iterator();

            HuffmanTable ht;
            while(tiffReaders.hasNext()) {
               for(dhts = ((List)tiffReaders.next()).iterator(); dhts.hasNext(); jpegTablesLength += 4 + ht.length) {
                  ht = (HuffmanTable)dhts.next();
               }
            }
         }

         jpegTablesLength += 2;
         jpegTablesData = new byte[jpegTablesLength];
         jpegTablesData[0] = -1;
         jpegTablesData[1] = -40;
         int jpoff = 2;
         int htlength;
         Iterator htiter;
         int numLengths;
         int i;
         if (this.dqtPresent) {
            dhts = this.qtables.iterator();

            while(dhts.hasNext()) {
               for(htiter = ((List)dhts.next()).iterator(); htiter.hasNext(); jpoff += numLengths) {
                  jpegTablesData[jpoff++] = -1;
                  jpegTablesData[jpoff++] = -37;
                  QTable qt = (QTable)htiter.next();
                  htlength = qt.length + 2;
                  jpegTablesData[jpoff++] = (byte)((htlength & '\uff00') >> 8);
                  jpegTablesData[jpoff++] = (byte)(htlength & 255);
                  jpegTablesData[jpoff++] = (byte)((qt.elementPrecision & 240) << 4 | qt.tableID & 15);
                  int[] table = qt.table.getTable();
                  numLengths = table.length;

                  for(i = 0; i < numLengths; ++i) {
                     jpegTablesData[jpoff + zigzag[i]] = (byte)table[i];
                  }
               }
            }
         }

         if (this.dhtPresent) {
            dhts = this.htables.iterator();

            while(dhts.hasNext()) {
               htiter = ((List)dhts.next()).iterator();

               while(htiter.hasNext()) {
                  jpegTablesData[jpoff++] = -1;
                  jpegTablesData[jpoff++] = -60;
                  HuffmanTable ht = (HuffmanTable)htiter.next();
                  htlength = ht.length + 2;
                  jpegTablesData[jpoff++] = (byte)((htlength & '\uff00') >> 8);
                  jpegTablesData[jpoff++] = (byte)(htlength & 255);
                  jpegTablesData[jpoff++] = (byte)((ht.tableClass & 15) << 4 | ht.tableID & 15);
                  short[] lengths = ht.table.getLengths();
                  numLengths = lengths.length;

                  for(i = 0; i < numLengths; ++i) {
                     jpegTablesData[jpoff++] = (byte)lengths[i];
                  }

                  short[] values = ht.table.getValues();
                  numValues = values.length;

                  for(int i = 0; i < numValues; ++i) {
                     jpegTablesData[jpoff++] = (byte)values[i];
                  }
               }
            }
         }

         jpegTablesData[jpoff++] = -1;
         jpegTablesData[jpoff] = -39;
      }

      if (jpegTablesData != null) {
         JPEGTablesField = new TIFFField(base.getTag(347), 7, jpegTablesData.length, jpegTablesData);
         dir.addTIFFField(JPEGTablesField);
      }

      IIOMetadata tiffMetadata = dir.getAsMetadata();
      if (this.exifData != null) {
         try {
            tiffReaders = ImageIO.getImageReadersByFormatName("TIFF");
            if (tiffReaders != null && tiffReaders.hasNext()) {
               ImageReader tiffReader = (ImageReader)tiffReaders.next();
               ByteArrayInputStream bais = new ByteArrayInputStream(this.exifData);
               ImageInputStream exifStream = new MemoryCacheImageInputStream(bais);
               tiffReader.setInput(exifStream);
               IIOMetadata exifMetadata = tiffReader.getImageMetadata(0);
               tiffMetadata.mergeTree(metadataName, exifMetadata.getAsTree(metadataName));
               tiffReader.reset();
            }
         } catch (IOException var17) {
         }
      }

      return tiffMetadata.getAsTree(metadataName);
   }

   private void initializeThumbnails() {
      synchronized(this.thumbnails) {
         if (!this.thumbnailsInitialized) {
            if (this.app0JFIFPresent && this.jfifThumbnail != null) {
               this.thumbnails.add(this.jfifThumbnail);
            }

            if (this.app0JFXXPresent && this.jfxxThumbnails != null) {
               int numJFXX = this.jfxxThumbnails.size();

               for(int i = 0; i < numJFXX; ++i) {
                  IIOImage img = (IIOImage)this.jfxxThumbnails.get(i);
                  BufferedImage jfxxThumbnail = (BufferedImage)img.getRenderedImage();
                  this.thumbnails.add(jfxxThumbnail);
               }
            }

            if (this.exifData != null) {
               try {
                  Iterator tiffReaders = ImageIO.getImageReadersByFormatName("TIFF");
                  if (tiffReaders != null && tiffReaders.hasNext()) {
                     ImageReader tiffReader = (ImageReader)tiffReaders.next();
                     ByteArrayInputStream bais = new ByteArrayInputStream(this.exifData);
                     ImageInputStream exifStream = new MemoryCacheImageInputStream(bais);
                     tiffReader.setInput(exifStream);
                     if (tiffReader.getNumImages(true) > 1) {
                        BufferedImage exifThumbnail = tiffReader.read(1, (ImageReadParam)null);
                        this.thumbnails.add(exifThumbnail);
                     }

                     tiffReader.reset();
                  }
               } catch (IOException var8) {
               }
            }

            this.thumbnailsInitialized = true;
         }

      }
   }

   int getNumThumbnails() throws IOException {
      this.initializeThumbnails();
      return this.thumbnails.size();
   }

   BufferedImage getThumbnail(int thumbnailIndex) throws IOException {
      if (thumbnailIndex < 0) {
         throw new IndexOutOfBoundsException("thumbnailIndex < 0!");
      } else {
         this.initializeThumbnails();
         if (thumbnailIndex >= this.thumbnails.size()) {
            throw new IndexOutOfBoundsException("thumbnailIndex > getNumThumbnails()");
         } else {
            return (BufferedImage)this.thumbnails.get(thumbnailIndex);
         }
      }
   }

   private class HuffmanTable {
      private static final int NUM_LENGTHS = 16;
      int tableClass;
      int tableID;
      JPEGHuffmanTable table;
      int length;

      HuffmanTable(ImageInputStream stream) throws IOException {
         this.tableClass = (int)stream.readBits(4);
         this.tableID = (int)stream.readBits(4);
         short[] lengths = new short[16];

         int numValues;
         for(numValues = 0; numValues < 16; ++numValues) {
            lengths[numValues] = (short)stream.read();
         }

         numValues = 0;

         for(int ix = 0; ix < 16; ++ix) {
            numValues += lengths[ix];
         }

         short[] values = new short[numValues];

         for(int i = 0; i < numValues; ++i) {
            values[i] = (short)stream.read();
         }

         this.table = new JPEGHuffmanTable(lengths, values);
         this.length = 17 + values.length;
      }
   }

   private class QTable {
      private static final int QTABLE_SIZE = 64;
      int elementPrecision;
      int tableID;
      JPEGQTable table;
      int length;

      QTable(ImageInputStream stream) throws IOException {
         this.elementPrecision = (int)stream.readBits(4);
         this.tableID = (int)stream.readBits(4);
         byte[] tmp = new byte[64];
         stream.readFully(tmp);
         int[] data = new int[64];

         for(int i = 0; i < 64; ++i) {
            data[i] = tmp[CLibJPEGMetadata.zigzag[i]] & 255;
         }

         this.table = new JPEGQTable(data);
         this.length = data.length + 1;
      }
   }
}
