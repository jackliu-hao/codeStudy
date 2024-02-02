package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFField;
import com.github.jaiimageio.plugins.tiff.TIFFTag;
import com.github.jaiimageio.plugins.tiff.TIFFTagSet;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TIFFImageMetadata extends IIOMetadata {
   public static final String nativeMetadataFormatName = "com_sun_media_imageio_plugins_tiff_image_1.0";
   public static final String nativeMetadataFormatClassName = "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat";
   List tagSets;
   TIFFIFD rootIFD;
   private static final String[] colorSpaceNames = new String[]{"GRAY", "GRAY", "RGB", "RGB", "GRAY", "CMYK", "YCbCr", "Lab", "Lab"};
   private static final String[] orientationNames = new String[]{null, "Normal", "FlipH", "Rotate180", "FlipV", "FlipHRotate90", "Rotate270", "FlipVRotate90", "Rotate90"};

   public TIFFImageMetadata(List tagSets) {
      super(true, "com_sun_media_imageio_plugins_tiff_image_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat", (String[])null, (String[])null);
      this.tagSets = tagSets;
      this.rootIFD = new TIFFIFD(tagSets);
   }

   public TIFFImageMetadata(TIFFIFD ifd) {
      super(true, "com_sun_media_imageio_plugins_tiff_image_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat", (String[])null, (String[])null);
      this.tagSets = ifd.getTagSetList();
      this.rootIFD = ifd;
   }

   public void initializeFromStream(ImageInputStream stream, boolean ignoreUnknownFields) throws IOException {
      this.rootIFD.initialize(stream, ignoreUnknownFields);
   }

   public void addShortOrLongField(int tagNumber, int value) {
      TIFFField field = new TIFFField(this.rootIFD.getTag(tagNumber), value);
      this.rootIFD.addTIFFField(field);
   }

   public boolean isReadOnly() {
      return false;
   }

   private Node getIFDAsTree(TIFFIFD ifd, String parentTagName, int parentTagNumber) {
      IIOMetadataNode IFDRoot = new IIOMetadataNode("TIFFIFD");
      if (parentTagNumber != 0) {
         IFDRoot.setAttribute("parentTagNumber", Integer.toString(parentTagNumber));
      }

      if (parentTagName != null) {
         IFDRoot.setAttribute("parentTagName", parentTagName);
      }

      List tagSets = ifd.getTagSetList();
      Iterator iter;
      if (tagSets.size() > 0) {
         iter = tagSets.iterator();
         String tagSetNames = "";

         while(iter.hasNext()) {
            TIFFTagSet tagSet = (TIFFTagSet)iter.next();
            tagSetNames = tagSetNames + tagSet.getClass().getName();
            if (iter.hasNext()) {
               tagSetNames = tagSetNames + ",";
            }
         }

         IFDRoot.setAttribute("tagSets", tagSetNames);
      }

      iter = ifd.iterator();

      while(iter.hasNext()) {
         TIFFField f = (TIFFField)iter.next();
         int tagNumber = f.getTagNumber();
         TIFFTag tag = TIFFIFD.getTag(tagNumber, tagSets);
         Node node = null;
         if (tag == null) {
            node = f.getAsNativeNode();
         } else if (tag.isIFDPointer()) {
            TIFFIFD subIFD = (TIFFIFD)f.getData();
            node = this.getIFDAsTree(subIFD, tag.getName(), tag.getNumber());
         } else {
            node = f.getAsNativeNode();
         }

         if (node != null) {
            IFDRoot.appendChild(node);
         }
      }

      return IFDRoot;
   }

   public Node getAsTree(String formatName) {
      if (formatName.equals("com_sun_media_imageio_plugins_tiff_image_1.0")) {
         return this.getNativeTree();
      } else if (formatName.equals("javax_imageio_1.0")) {
         return this.getStandardTree();
      } else {
         throw new IllegalArgumentException("Not a recognized format!");
      }
   }

   private Node getNativeTree() {
      IIOMetadataNode root = new IIOMetadataNode("com_sun_media_imageio_plugins_tiff_image_1.0");
      Node IFDNode = this.getIFDAsTree(this.rootIFD, (String)null, 0);
      root.appendChild(IFDNode);
      return root;
   }

   public IIOMetadataNode getStandardChromaNode() {
      IIOMetadataNode chroma_node = new IIOMetadataNode("Chroma");
      IIOMetadataNode node = null;
      int photometricInterpretation = -1;
      boolean isPaletteColor = false;
      TIFFField f = this.getTIFFField(262);
      if (f != null) {
         photometricInterpretation = f.getAsInt(0);
         isPaletteColor = photometricInterpretation == 3;
      }

      int numChannels = -1;
      if (isPaletteColor) {
         numChannels = 3;
      } else {
         f = this.getTIFFField(277);
         if (f != null) {
            numChannels = f.getAsInt(0);
         } else {
            f = this.getTIFFField(258);
            if (f != null) {
               numChannels = f.getCount();
            }
         }
      }

      if (photometricInterpretation != -1) {
         if (photometricInterpretation >= 0 && photometricInterpretation < colorSpaceNames.length) {
            node = new IIOMetadataNode("ColorSpaceType");
            String csName;
            if (photometricInterpretation == 5 && numChannels == 3) {
               csName = "CMY";
            } else {
               csName = colorSpaceNames[photometricInterpretation];
            }

            node.setAttribute("name", csName);
            chroma_node.appendChild(node);
         }

         node = new IIOMetadataNode("BlackIsZero");
         node.setAttribute("value", photometricInterpretation == 0 ? "FALSE" : "TRUE");
         chroma_node.appendChild(node);
      }

      if (numChannels != -1) {
         node = new IIOMetadataNode("NumChannels");
         node.setAttribute("value", Integer.toString(numChannels));
         chroma_node.appendChild(node);
      }

      f = this.getTIFFField(320);
      if (f != null) {
         boolean hasAlpha = false;
         node = new IIOMetadataNode("Palette");
         int len = f.getCount() / (hasAlpha ? 4 : 3);

         for(int i = 0; i < len; ++i) {
            IIOMetadataNode entry = new IIOMetadataNode("PaletteEntry");
            entry.setAttribute("index", Integer.toString(i));
            int r = f.getAsInt(i) * 255 / '\uffff';
            int g = f.getAsInt(len + i) * 255 / '\uffff';
            int b = f.getAsInt(2 * len + i) * 255 / '\uffff';
            entry.setAttribute("red", Integer.toString(r));
            entry.setAttribute("green", Integer.toString(g));
            entry.setAttribute("blue", Integer.toString(b));
            if (hasAlpha) {
               int alpha = 0;
               entry.setAttribute("alpha", Integer.toString(alpha));
            }

            node.appendChild(entry);
         }

         chroma_node.appendChild(node);
      }

      return chroma_node;
   }

   public IIOMetadataNode getStandardCompressionNode() {
      IIOMetadataNode compression_node = new IIOMetadataNode("Compression");
      IIOMetadataNode node = null;
      TIFFField f = this.getTIFFField(259);
      if (f != null) {
         String compressionTypeName = null;
         int compression = f.getAsInt(0);
         boolean isLossless = true;
         if (compression == 1) {
            compressionTypeName = "None";
            isLossless = true;
         } else {
            int[] compressionNumbers = TIFFImageWriter.compressionNumbers;

            for(int i = 0; i < compressionNumbers.length; ++i) {
               if (compression == compressionNumbers[i]) {
                  compressionTypeName = TIFFImageWriter.compressionTypes[i];
                  isLossless = TIFFImageWriter.isCompressionLossless[i];
                  break;
               }
            }
         }

         if (compressionTypeName != null) {
            node = new IIOMetadataNode("CompressionTypeName");
            node.setAttribute("value", compressionTypeName);
            compression_node.appendChild(node);
            node = new IIOMetadataNode("Lossless");
            node.setAttribute("value", isLossless ? "TRUE" : "FALSE");
            compression_node.appendChild(node);
         }
      }

      node = new IIOMetadataNode("NumProgressiveScans");
      node.setAttribute("value", "1");
      compression_node.appendChild(node);
      return compression_node;
   }

   private String repeat(String s, int times) {
      if (times == 1) {
         return s;
      } else {
         StringBuffer sb = new StringBuffer((s.length() + 1) * times - 1);
         sb.append(s);

         for(int i = 1; i < times; ++i) {
            sb.append(" ");
            sb.append(s);
         }

         return sb.toString();
      }
   }

   public IIOMetadataNode getStandardDataNode() {
      IIOMetadataNode data_node = new IIOMetadataNode("Data");
      IIOMetadataNode node = null;
      boolean isPaletteColor = false;
      TIFFField f = this.getTIFFField(262);
      if (f != null) {
         isPaletteColor = f.getAsInt(0) == 3;
      }

      f = this.getTIFFField(284);
      String planarConfiguration = "PixelInterleaved";
      if (f != null && f.getAsInt(0) == 2) {
         planarConfiguration = "PlaneInterleaved";
      }

      node = new IIOMetadataNode("PlanarConfiguration");
      node.setAttribute("value", planarConfiguration);
      data_node.appendChild(node);
      f = this.getTIFFField(262);
      int fillOrder;
      if (f != null) {
         int photometricInterpretation = f.getAsInt(0);
         String sampleFormat = "UnsignedIntegral";
         if (photometricInterpretation == 3) {
            sampleFormat = "Index";
         } else {
            f = this.getTIFFField(339);
            if (f != null) {
               fillOrder = f.getAsInt(0);
               if (fillOrder == 2) {
                  sampleFormat = "SignedIntegral";
               } else if (fillOrder == 1) {
                  sampleFormat = "UnsignedIntegral";
               } else if (fillOrder == 3) {
                  sampleFormat = "Real";
               } else {
                  sampleFormat = null;
               }
            }
         }

         if (sampleFormat != null) {
            node = new IIOMetadataNode("SampleFormat");
            node.setAttribute("value", sampleFormat);
            data_node.appendChild(node);
         }
      }

      f = this.getTIFFField(258);
      int[] bitsPerSample = null;
      int[] bitsPerSample;
      if (f != null) {
         bitsPerSample = f.getAsInts();
      } else {
         f = this.getTIFFField(259);
         int compression = f != null ? f.getAsInt(0) : 1;
         if (this.getTIFFField(34665) == null && compression != 7 && compression != 6 && this.getTIFFField(513) == null) {
            bitsPerSample = new int[]{1};
         } else {
            f = this.getTIFFField(262);
            if (f == null || f.getAsInt(0) != 0 && f.getAsInt(0) != 1) {
               bitsPerSample = new int[]{8, 8, 8};
            } else {
               bitsPerSample = new int[]{8};
            }
         }
      }

      StringBuffer sb = new StringBuffer();

      for(fillOrder = 0; fillOrder < bitsPerSample.length; ++fillOrder) {
         if (fillOrder > 0) {
            sb.append(" ");
         }

         sb.append(Integer.toString(bitsPerSample[fillOrder]));
      }

      node = new IIOMetadataNode("BitsPerSample");
      if (isPaletteColor) {
         node.setAttribute("value", this.repeat(sb.toString(), 3));
      } else {
         node.setAttribute("value", sb.toString());
      }

      data_node.appendChild(node);
      f = this.getTIFFField(266);
      fillOrder = f != null ? f.getAsInt(0) : 1;
      sb = new StringBuffer();

      for(int i = 0; i < bitsPerSample.length; ++i) {
         if (i > 0) {
            sb.append(" ");
         }

         int maxBitIndex = bitsPerSample[i] == 1 ? 7 : bitsPerSample[i] - 1;
         int msb = fillOrder == 1 ? maxBitIndex : 0;
         sb.append(Integer.toString(msb));
      }

      node = new IIOMetadataNode("SampleMSB");
      if (isPaletteColor) {
         node.setAttribute("value", this.repeat(sb.toString(), 3));
      } else {
         node.setAttribute("value", sb.toString());
      }

      data_node.appendChild(node);
      return data_node;
   }

   public IIOMetadataNode getStandardDimensionNode() {
      IIOMetadataNode dimension_node = new IIOMetadataNode("Dimension");
      IIOMetadataNode node = null;
      long[] xres = null;
      long[] yres = null;
      TIFFField f = this.getTIFFField(282);
      if (f != null) {
         xres = (long[])((long[])f.getAsRational(0).clone());
      }

      f = this.getTIFFField(283);
      if (f != null) {
         yres = (long[])((long[])f.getAsRational(0).clone());
      }

      if (xres != null && yres != null) {
         node = new IIOMetadataNode("PixelAspectRatio");
         float ratio = (float)((double)xres[1] * (double)yres[0]) / (float)(xres[0] * yres[1]);
         node.setAttribute("value", Float.toString(ratio));
         dimension_node.appendChild(node);
      }

      float yPosition;
      int resolutionUnit;
      if (xres != null || yres != null) {
         f = this.getTIFFField(296);
         resolutionUnit = f != null ? f.getAsInt(0) : 2;
         boolean gotPixelSize = resolutionUnit != 1;
         if (resolutionUnit == 2) {
            if (xres != null) {
               xres[0] *= 100L;
               xres[1] *= 254L;
            }

            if (yres != null) {
               yres[0] *= 100L;
               yres[1] *= 254L;
            }
         }

         if (gotPixelSize) {
            if (xres != null) {
               yPosition = (float)(10.0 * (double)xres[1] / (double)xres[0]);
               node = new IIOMetadataNode("HorizontalPixelSize");
               node.setAttribute("value", Float.toString(yPosition));
               dimension_node.appendChild(node);
            }

            if (yres != null) {
               yPosition = (float)(10.0 * (double)yres[1] / (double)yres[0]);
               node = new IIOMetadataNode("VerticalPixelSize");
               node.setAttribute("value", Float.toString(yPosition));
               dimension_node.appendChild(node);
            }
         }
      }

      f = this.getTIFFField(296);
      resolutionUnit = f != null ? f.getAsInt(0) : 2;
      if (resolutionUnit == 2 || resolutionUnit == 3) {
         f = this.getTIFFField(286);
         long[] ypos;
         if (f != null) {
            ypos = (long[])f.getAsRational(0);
            yPosition = (float)ypos[0] / (float)ypos[1];
            if (resolutionUnit == 2) {
               yPosition *= 254.0F;
            } else {
               yPosition *= 10.0F;
            }

            node = new IIOMetadataNode("HorizontalPosition");
            node.setAttribute("value", Float.toString(yPosition));
            dimension_node.appendChild(node);
         }

         f = this.getTIFFField(287);
         if (f != null) {
            ypos = (long[])f.getAsRational(0);
            yPosition = (float)ypos[0] / (float)ypos[1];
            if (resolutionUnit == 2) {
               yPosition *= 254.0F;
            } else {
               yPosition *= 10.0F;
            }

            node = new IIOMetadataNode("VerticalPosition");
            node.setAttribute("value", Float.toString(yPosition));
            dimension_node.appendChild(node);
         }
      }

      f = this.getTIFFField(274);
      if (f != null) {
         int o = f.getAsInt(0);
         if (o >= 0 && o < orientationNames.length) {
            node = new IIOMetadataNode("ImageOrientation");
            node.setAttribute("value", orientationNames[o]);
            dimension_node.appendChild(node);
         }
      }

      return dimension_node;
   }

   public IIOMetadataNode getStandardDocumentNode() {
      IIOMetadataNode document_node = new IIOMetadataNode("Document");
      IIOMetadataNode node = null;
      node = new IIOMetadataNode("FormatVersion");
      node.setAttribute("value", "6.0");
      document_node.appendChild(node);
      TIFFField f = this.getTIFFField(254);
      if (f != null) {
         int newSubFileType = f.getAsInt(0);
         String value = null;
         if ((newSubFileType & 4) != 0) {
            value = "TransparencyMask";
         } else if ((newSubFileType & 1) != 0) {
            value = "ReducedResolution";
         } else if ((newSubFileType & 2) != 0) {
            value = "SinglePage";
         }

         if (value != null) {
            node = new IIOMetadataNode("SubimageInterpretation");
            node.setAttribute("value", value);
            document_node.appendChild(node);
         }
      }

      f = this.getTIFFField(306);
      if (f != null) {
         String s = f.getAsString(0);
         if (s.length() == 19) {
            node = new IIOMetadataNode("ImageCreationTime");

            boolean appendNode;
            try {
               node.setAttribute("year", s.substring(0, 4));
               node.setAttribute("month", s.substring(5, 7));
               node.setAttribute("day", s.substring(8, 10));
               node.setAttribute("hour", s.substring(11, 13));
               node.setAttribute("minute", s.substring(14, 16));
               node.setAttribute("second", s.substring(17, 19));
               appendNode = true;
            } catch (IndexOutOfBoundsException var7) {
               appendNode = false;
            }

            if (appendNode) {
               document_node.appendChild(node);
            }
         }
      }

      return document_node;
   }

   public IIOMetadataNode getStandardTextNode() {
      IIOMetadataNode text_node = null;
      IIOMetadataNode node = null;
      int[] textFieldTagNumbers = new int[]{269, 270, 271, 272, 285, 305, 315, 316, 333, 33432};

      for(int i = 0; i < textFieldTagNumbers.length; ++i) {
         TIFFField f = this.getTIFFField(textFieldTagNumbers[i]);
         if (f != null) {
            String value = f.getAsString(0);
            if (text_node == null) {
               text_node = new IIOMetadataNode("Text");
            }

            node = new IIOMetadataNode("TextEntry");
            node.setAttribute("keyword", f.getTag().getName());
            node.setAttribute("value", value);
            text_node.appendChild(node);
         }
      }

      return text_node;
   }

   public IIOMetadataNode getStandardTransparencyNode() {
      IIOMetadataNode transparency_node = new IIOMetadataNode("Transparency");
      IIOMetadataNode node = null;
      node = new IIOMetadataNode("Alpha");
      String value = "none";
      TIFFField f = this.getTIFFField(338);
      if (f != null) {
         int[] extraSamples = f.getAsInts();

         for(int i = 0; i < extraSamples.length; ++i) {
            if (extraSamples[i] == 1) {
               value = "premultiplied";
               break;
            }

            if (extraSamples[i] == 2) {
               value = "nonpremultiplied";
               break;
            }
         }
      }

      node.setAttribute("value", value);
      transparency_node.appendChild(node);
      return transparency_node;
   }

   private static void fatal(Node node, String reason) throws IIOInvalidTreeException {
      throw new IIOInvalidTreeException(reason, node);
   }

   private int[] listToIntArray(String list) {
      StringTokenizer st = new StringTokenizer(list, " ");
      ArrayList intList = new ArrayList();

      while(st.hasMoreTokens()) {
         String nextInteger = st.nextToken();
         Integer nextInt = new Integer(nextInteger);
         intList.add(nextInt);
      }

      int[] intArray = new int[intList.size()];

      for(int i = 0; i < intArray.length; ++i) {
         intArray[i] = (Integer)intList.get(i);
      }

      return intArray;
   }

   private char[] listToCharArray(String list) {
      StringTokenizer st = new StringTokenizer(list, " ");
      ArrayList intList = new ArrayList();

      while(st.hasMoreTokens()) {
         String nextInteger = st.nextToken();
         Integer nextInt = new Integer(nextInteger);
         intList.add(nextInt);
      }

      char[] charArray = new char[intList.size()];

      for(int i = 0; i < charArray.length; ++i) {
         charArray[i] = (char)(Integer)intList.get(i);
      }

      return charArray;
   }

   private void mergeStandardTree(Node root) throws IIOInvalidTreeException {
      if (!root.getNodeName().equals("javax_imageio_1.0")) {
         fatal(root, "Root must be javax_imageio_1.0");
      }

      String sampleFormat = null;
      Node dataNode = this.getChildNode(root, "Data");
      boolean isPaletteColor = false;
      Node chromaNode;
      if (dataNode != null) {
         chromaNode = this.getChildNode(dataNode, "SampleFormat");
         if (chromaNode != null) {
            sampleFormat = getAttribute(chromaNode, "value");
            isPaletteColor = sampleFormat.equals("Index");
         }
      }

      if (!isPaletteColor) {
         chromaNode = this.getChildNode(root, "Chroma");
         if (chromaNode != null && this.getChildNode(chromaNode, "Palette") != null) {
            isPaletteColor = true;
         }
      }

      TIFFField f;
      TIFFTag tag;
      for(Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
         String name = node.getNodeName();
         String theAuthor;
         String day;
         int tagNumber;
         String keyword;
         String value;
         int idx;
         boolean gotVerticalPixelSize;
         if (name.equals("Chroma")) {
            String colorSpaceType = null;
            theAuthor = null;
            boolean gotPalette = false;

            for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
               day = child.getNodeName();
               if (day.equals("ColorSpaceType")) {
                  colorSpaceType = getAttribute(child, "name");
               } else if (day.equals("NumChannels")) {
                  tag = this.rootIFD.getTag(277);
                  tagNumber = isPaletteColor ? 1 : Integer.parseInt(getAttribute(child, "value"));
                  f = new TIFFField(tag, tagNumber);
                  this.rootIFD.addTIFFField(f);
               } else if (day.equals("BlackIsZero")) {
                  theAuthor = getAttribute(child, "value");
               } else if (day.equals("Palette")) {
                  Node entry = child.getFirstChild();
                  HashMap palette = new HashMap();

                  int maxIndex;
                  for(maxIndex = -1; entry != null; entry = entry.getNextSibling()) {
                     keyword = entry.getNodeName();
                     if (keyword.equals("PaletteEntry")) {
                        value = getAttribute(entry, "index");
                        int id = Integer.parseInt(value);
                        if (id > maxIndex) {
                           maxIndex = id;
                        }

                        char red = (char)Integer.parseInt(getAttribute(entry, "red"));
                        char green = (char)Integer.parseInt(getAttribute(entry, "green"));
                        char blue = (char)Integer.parseInt(getAttribute(entry, "blue"));
                        palette.put(new Integer(id), new char[]{red, green, blue});
                        gotPalette = true;
                     }
                  }

                  if (gotPalette) {
                     int mapSize = maxIndex + 1;
                     int paletteLength = 3 * mapSize;
                     char[] paletteEntries = new char[paletteLength];

                     char[] rgb;
                     for(Iterator paletteIter = palette.keySet().iterator(); paletteIter.hasNext(); paletteEntries[2 * mapSize + idx] = (char)(rgb[2] * '\uffff' / 255)) {
                        Integer index = (Integer)paletteIter.next();
                        rgb = (char[])((char[])palette.get(index));
                        idx = index;
                        paletteEntries[idx] = (char)(rgb[0] * '\uffff' / 255);
                        paletteEntries[mapSize + idx] = (char)(rgb[1] * '\uffff' / 255);
                     }

                     tag = this.rootIFD.getTag(320);
                     f = new TIFFField(tag, 3, paletteLength, paletteEntries);
                     this.rootIFD.addTIFFField(f);
                  }
               }
            }

            int photometricInterpretation = -1;
            if ((colorSpaceType == null || colorSpaceType.equals("GRAY")) && theAuthor != null && theAuthor.equalsIgnoreCase("FALSE")) {
               photometricInterpretation = 0;
            } else if (colorSpaceType != null) {
               if (colorSpaceType.equals("GRAY")) {
                  gotVerticalPixelSize = false;
                  if (root instanceof IIOMetadataNode) {
                     IIOMetadataNode iioRoot = (IIOMetadataNode)root;
                     NodeList siNodeList = iioRoot.getElementsByTagName("SubimageInterpretation");
                     if (siNodeList.getLength() == 1) {
                        Node siNode = siNodeList.item(0);
                        value = getAttribute(siNode, "value");
                        if (value.equals("TransparencyMask")) {
                           gotVerticalPixelSize = true;
                        }
                     }
                  }

                  if (gotVerticalPixelSize) {
                     photometricInterpretation = 4;
                  } else {
                     photometricInterpretation = 1;
                  }
               } else if (colorSpaceType.equals("RGB")) {
                  photometricInterpretation = gotPalette ? 3 : 2;
               } else if (colorSpaceType.equals("YCbCr")) {
                  photometricInterpretation = 6;
               } else if (colorSpaceType.equals("CMYK")) {
                  photometricInterpretation = 5;
               } else if (colorSpaceType.equals("Lab")) {
                  photometricInterpretation = 8;
               }
            }

            if (photometricInterpretation != -1) {
               tag = this.rootIFD.getTag(262);
               f = new TIFFField(tag, photometricInterpretation);
               this.rootIFD.addTIFFField(f);
            }
         } else {
            Node child;
            String theTitle;
            if (name.equals("Compression")) {
               for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                  theAuthor = child.getNodeName();
                  if (theAuthor.equals("CompressionTypeName")) {
                     int compression = -1;
                     theTitle = getAttribute(child, "value");
                     if (theTitle.equalsIgnoreCase("None")) {
                        compression = 1;
                     } else {
                        String[] compressionNames = TIFFImageWriter.compressionTypes;

                        for(tagNumber = 0; tagNumber < compressionNames.length; ++tagNumber) {
                           if (compressionNames[tagNumber].equalsIgnoreCase(theTitle)) {
                              compression = TIFFImageWriter.compressionNumbers[tagNumber];
                              break;
                           }
                        }
                     }

                     if (compression != -1) {
                        tag = this.rootIFD.getTag(259);
                        f = new TIFFField(tag, compression);
                        this.rootIFD.addTIFFField(f);
                     }
                  }
               }
            } else {
               String theDescription;
               byte newSubFileType;
               if (name.equals("Data")) {
                  for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                     theAuthor = child.getNodeName();
                     if (theAuthor.equals("PlanarConfiguration")) {
                        theDescription = getAttribute(child, "value");
                        newSubFileType = -1;
                        if (theDescription.equals("PixelInterleaved")) {
                           newSubFileType = 1;
                        } else if (theDescription.equals("PlaneInterleaved")) {
                           newSubFileType = 2;
                        }

                        if (newSubFileType != -1) {
                           tag = this.rootIFD.getTag(284);
                           f = new TIFFField(tag, newSubFileType);
                           this.rootIFD.addTIFFField(f);
                        }
                     } else if (theAuthor.equals("BitsPerSample")) {
                        theDescription = getAttribute(child, "value");
                        char[] bitsPerSample = this.listToCharArray(theDescription);
                        tag = this.rootIFD.getTag(258);
                        if (isPaletteColor) {
                           f = new TIFFField(tag, 3, 1, new char[]{bitsPerSample[0]});
                        } else {
                           f = new TIFFField(tag, 3, bitsPerSample.length, bitsPerSample);
                        }

                        this.rootIFD.addTIFFField(f);
                     } else if (theAuthor.equals("SampleMSB")) {
                        theDescription = getAttribute(child, "value");
                        int[] sampleMSB = this.listToIntArray(theDescription);
                        boolean isRightToLeft = true;

                        for(tagNumber = 0; tagNumber < sampleMSB.length; ++tagNumber) {
                           if (sampleMSB[tagNumber] != 0) {
                              isRightToLeft = false;
                              break;
                           }
                        }

                        tagNumber = isRightToLeft ? 2 : 1;
                        tag = this.rootIFD.getTag(266);
                        f = new TIFFField(tag, tagNumber);
                        this.rootIFD.addTIFFField(f);
                     }
                  }
               } else if (name.equals("Dimension")) {
                  float pixelAspectRatio = -1.0F;
                  boolean gotPixelAspectRatio = false;
                  float horizontalPixelSize = -1.0F;
                  boolean gotHorizontalPixelSize = false;
                  float verticalPixelSize = -1.0F;
                  gotVerticalPixelSize = false;
                  boolean sizeIsAbsolute = false;
                  float horizontalPosition = -1.0F;
                  boolean gotHorizontalPosition = false;
                  float verticalPosition = -1.0F;
                  boolean gotVerticalPosition = false;

                  for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                     String childName = child.getNodeName();
                     String vp;
                     if (childName.equals("PixelAspectRatio")) {
                        vp = getAttribute(child, "value");
                        pixelAspectRatio = Float.parseFloat(vp);
                        gotPixelAspectRatio = true;
                     } else if (childName.equals("ImageOrientation")) {
                        vp = getAttribute(child, "value");

                        for(idx = 0; idx < orientationNames.length; ++idx) {
                           if (vp.equals(orientationNames[idx])) {
                              char[] oData = new char[]{(char)idx};
                              f = new TIFFField(this.rootIFD.getTag(274), 3, 1, oData);
                              this.rootIFD.addTIFFField(f);
                              break;
                           }
                        }
                     } else if (childName.equals("HorizontalPixelSize")) {
                        vp = getAttribute(child, "value");
                        horizontalPixelSize = Float.parseFloat(vp);
                        gotHorizontalPixelSize = true;
                     } else if (childName.equals("VerticalPixelSize")) {
                        vp = getAttribute(child, "value");
                        verticalPixelSize = Float.parseFloat(vp);
                        gotVerticalPixelSize = true;
                     } else if (childName.equals("HorizontalPosition")) {
                        vp = getAttribute(child, "value");
                        horizontalPosition = Float.parseFloat(vp);
                        gotHorizontalPosition = true;
                     } else if (childName.equals("VerticalPosition")) {
                        vp = getAttribute(child, "value");
                        verticalPosition = Float.parseFloat(vp);
                        gotVerticalPosition = true;
                     }
                  }

                  sizeIsAbsolute = gotHorizontalPixelSize || gotVerticalPixelSize;
                  if (gotPixelAspectRatio) {
                     if (gotHorizontalPixelSize && !gotVerticalPixelSize) {
                        verticalPixelSize = horizontalPixelSize / pixelAspectRatio;
                        gotVerticalPixelSize = true;
                     } else if (gotVerticalPixelSize && !gotHorizontalPixelSize) {
                        horizontalPixelSize = verticalPixelSize * pixelAspectRatio;
                        gotHorizontalPixelSize = true;
                     } else if (!gotHorizontalPixelSize && !gotVerticalPixelSize) {
                        horizontalPixelSize = pixelAspectRatio;
                        verticalPixelSize = 1.0F;
                        gotHorizontalPixelSize = true;
                        gotVerticalPixelSize = true;
                     }
                  }

                  float yResolution;
                  long[][] vData;
                  if (gotHorizontalPixelSize) {
                     yResolution = (sizeIsAbsolute ? 10.0F : 1.0F) / horizontalPixelSize;
                     vData = new long[1][2];
                     vData[0] = new long[2];
                     vData[0][0] = (long)(yResolution * 10000.0F);
                     vData[0][1] = 10000L;
                     f = new TIFFField(this.rootIFD.getTag(282), 5, 1, vData);
                     this.rootIFD.addTIFFField(f);
                  }

                  if (gotVerticalPixelSize) {
                     yResolution = (sizeIsAbsolute ? 10.0F : 1.0F) / verticalPixelSize;
                     vData = new long[1][2];
                     vData[0] = new long[2];
                     vData[0][0] = (long)(yResolution * 10000.0F);
                     vData[0][1] = 10000L;
                     f = new TIFFField(this.rootIFD.getTag(283), 5, 1, vData);
                     this.rootIFD.addTIFFField(f);
                  }

                  char[] res = new char[]{(char)(sizeIsAbsolute ? 3 : 1)};
                  f = new TIFFField(this.rootIFD.getTag(296), 3, 1, res);
                  this.rootIFD.addTIFFField(f);
                  if (sizeIsAbsolute) {
                     if (gotHorizontalPosition) {
                        vData = new long[1][2];
                        vData[0][0] = (long)(horizontalPosition * 10000.0F);
                        vData[0][1] = 100000L;
                        f = new TIFFField(this.rootIFD.getTag(286), 5, 1, vData);
                        this.rootIFD.addTIFFField(f);
                     }

                     if (gotVerticalPosition) {
                        vData = new long[1][2];
                        vData[0][0] = (long)(verticalPosition * 10000.0F);
                        vData[0][1] = 100000L;
                        f = new TIFFField(this.rootIFD.getTag(287), 5, 1, vData);
                        this.rootIFD.addTIFFField(f);
                     }
                  }
               } else if (name.equals("Document")) {
                  for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                     theAuthor = child.getNodeName();
                     if (theAuthor.equals("SubimageInterpretation")) {
                        theDescription = getAttribute(child, "value");
                        newSubFileType = -1;
                        if (theDescription.equals("TransparencyMask")) {
                           newSubFileType = 4;
                        } else if (theDescription.equals("ReducedResolution")) {
                           newSubFileType = 1;
                        } else if (theDescription.equals("SinglePage")) {
                           newSubFileType = 2;
                        }

                        if (newSubFileType != -1) {
                           tag = this.rootIFD.getTag(254);
                           f = new TIFFField(tag, newSubFileType);
                           this.rootIFD.addTIFFField(f);
                        }
                     }

                     if (theAuthor.equals("ImageCreationTime")) {
                        theDescription = getAttribute(child, "year");
                        theTitle = getAttribute(child, "month");
                        day = getAttribute(child, "day");
                        String hour = getAttribute(child, "hour");
                        String minute = getAttribute(child, "minute");
                        String second = getAttribute(child, "second");
                        StringBuffer sb = new StringBuffer();
                        sb.append(theDescription);
                        sb.append(":");
                        if (theTitle.length() == 1) {
                           sb.append("0");
                        }

                        sb.append(theTitle);
                        sb.append(":");
                        if (day.length() == 1) {
                           sb.append("0");
                        }

                        sb.append(day);
                        sb.append(" ");
                        if (hour.length() == 1) {
                           sb.append("0");
                        }

                        sb.append(hour);
                        sb.append(":");
                        if (minute.length() == 1) {
                           sb.append("0");
                        }

                        sb.append(minute);
                        sb.append(":");
                        if (second.length() == 1) {
                           sb.append("0");
                        }

                        sb.append(second);
                        String[] dt = new String[]{sb.toString()};
                        f = new TIFFField(this.rootIFD.getTag(306), 2, 1, dt);
                        this.rootIFD.addTIFFField(f);
                     }
                  }
               } else if (!name.equals("Text")) {
                  if (name.equals("Transparency")) {
                     for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                        theAuthor = child.getNodeName();
                        if (theAuthor.equals("Alpha")) {
                           theDescription = getAttribute(child, "value");
                           f = null;
                           if (theDescription.equals("premultiplied")) {
                              f = new TIFFField(this.rootIFD.getTag(338), 1);
                           } else if (theDescription.equals("nonpremultiplied")) {
                              f = new TIFFField(this.rootIFD.getTag(338), 2);
                           }

                           if (f != null) {
                              this.rootIFD.addTIFFField(f);
                           }
                        }
                     }
                  }
               } else {
                  child = node.getFirstChild();
                  theAuthor = null;
                  theDescription = null;

                  for(theTitle = null; child != null; child = child.getNextSibling()) {
                     day = child.getNodeName();
                     if (day.equals("TextEntry")) {
                        tagNumber = -1;
                        NamedNodeMap childAttrs = child.getAttributes();
                        Node keywordNode = childAttrs.getNamedItem("keyword");
                        if (keywordNode != null) {
                           keyword = keywordNode.getNodeValue();
                           value = getAttribute(child, "value");
                           if (!keyword.equals("") && !value.equals("")) {
                              if (keyword.equalsIgnoreCase("DocumentName")) {
                                 tagNumber = 269;
                              } else if (keyword.equalsIgnoreCase("ImageDescription")) {
                                 tagNumber = 270;
                              } else if (keyword.equalsIgnoreCase("Make")) {
                                 tagNumber = 271;
                              } else if (keyword.equalsIgnoreCase("Model")) {
                                 tagNumber = 272;
                              } else if (keyword.equalsIgnoreCase("PageName")) {
                                 tagNumber = 285;
                              } else if (keyword.equalsIgnoreCase("Software")) {
                                 tagNumber = 305;
                              } else if (keyword.equalsIgnoreCase("Artist")) {
                                 tagNumber = 315;
                              } else if (keyword.equalsIgnoreCase("HostComputer")) {
                                 tagNumber = 316;
                              } else if (keyword.equalsIgnoreCase("InkNames")) {
                                 tagNumber = 333;
                              } else if (keyword.equalsIgnoreCase("Copyright")) {
                                 tagNumber = 33432;
                              } else if (keyword.equalsIgnoreCase("author")) {
                                 theAuthor = value;
                              } else if (keyword.equalsIgnoreCase("description")) {
                                 theDescription = value;
                              } else if (keyword.equalsIgnoreCase("title")) {
                                 theTitle = value;
                              }

                              if (tagNumber != -1) {
                                 f = new TIFFField(this.rootIFD.getTag(tagNumber), 2, 1, new String[]{value});
                                 this.rootIFD.addTIFFField(f);
                              }
                           }
                        }
                     }
                  }

                  if (theAuthor != null && this.getTIFFField(315) == null) {
                     f = new TIFFField(this.rootIFD.getTag(315), 2, 1, new String[]{theAuthor});
                     this.rootIFD.addTIFFField(f);
                  }

                  if (theDescription != null && this.getTIFFField(270) == null) {
                     f = new TIFFField(this.rootIFD.getTag(270), 2, 1, new String[]{theDescription});
                     this.rootIFD.addTIFFField(f);
                  }

                  if (theTitle != null && this.getTIFFField(269) == null) {
                     f = new TIFFField(this.rootIFD.getTag(269), 2, 1, new String[]{theTitle});
                     this.rootIFD.addTIFFField(f);
                  }
               }
            }
         }
      }

      if (sampleFormat != null) {
         int sf = -1;
         if (sampleFormat.equals("SignedIntegral")) {
            sf = 2;
         } else if (sampleFormat.equals("UnsignedIntegral")) {
            sf = 1;
         } else if (sampleFormat.equals("Real")) {
            sf = 3;
         } else if (sampleFormat.equals("Index")) {
            sf = 1;
         }

         if (sf != -1) {
            int count = 1;
            f = this.getTIFFField(277);
            if (f != null) {
               count = f.getAsInt(0);
            } else {
               f = this.getTIFFField(258);
               if (f != null) {
                  count = f.getCount();
               }
            }

            char[] sampleFormatArray = new char[count];
            Arrays.fill(sampleFormatArray, (char)sf);
            tag = this.rootIFD.getTag(339);
            f = new TIFFField(tag, 3, sampleFormatArray.length, sampleFormatArray);
            this.rootIFD.addTIFFField(f);
         }
      }

   }

   private static String getAttribute(Node node, String attrName) {
      NamedNodeMap attrs = node.getAttributes();
      Node attr = attrs.getNamedItem(attrName);
      return attr != null ? attr.getNodeValue() : null;
   }

   private Node getChildNode(Node node, String childName) {
      Node childNode = null;
      if (node.hasChildNodes()) {
         NodeList childNodes = node.getChildNodes();
         int length = childNodes.getLength();

         for(int i = 0; i < length; ++i) {
            Node item = childNodes.item(i);
            if (item.getNodeName().equals(childName)) {
               childNode = item;
               break;
            }
         }
      }

      return childNode;
   }

   public static TIFFIFD parseIFD(Node node) throws IIOInvalidTreeException {
      if (!node.getNodeName().equals("TIFFIFD")) {
         fatal(node, "Expected \"TIFFIFD\" node");
      }

      String tagSetNames = getAttribute(node, "tagSets");
      List tagSets = new ArrayList(5);
      String name;
      if (tagSetNames != null) {
         StringTokenizer st = new StringTokenizer(tagSetNames, ",");

         while(st.hasMoreTokens()) {
            name = st.nextToken();
            Object o = null;

            try {
               Class setClass = Class.forName(name);
               Method getInstanceMethod = setClass.getMethod("getInstance", (Class[])null);
               o = getInstanceMethod.invoke((Object)null, (Object[])null);
            } catch (NoSuchMethodException var11) {
               throw new RuntimeException(var11);
            } catch (IllegalAccessException var12) {
               throw new RuntimeException(var12);
            } catch (InvocationTargetException var13) {
               throw new RuntimeException(var13);
            } catch (ClassNotFoundException var14) {
               throw new RuntimeException(var14);
            }

            if (!(o instanceof TIFFTagSet)) {
               fatal(node, "Specified tag set class \"" + name + "\" is not an instance of TIFFTagSet");
            } else {
               tagSets.add((TIFFTagSet)o);
            }
         }
      }

      TIFFIFD ifd = new TIFFIFD(tagSets);

      for(node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
         name = node.getNodeName();
         TIFFField f = null;
         if (name.equals("TIFFIFD")) {
            TIFFIFD subIFD = parseIFD(node);
            String parentTagName = getAttribute(node, "parentTagName");
            String parentTagNumber = getAttribute(node, "parentTagNumber");
            TIFFTag tag = null;
            if (parentTagName != null) {
               tag = TIFFIFD.getTag(parentTagName, tagSets);
            } else if (parentTagNumber != null) {
               int tagNumber = Integer.valueOf(parentTagNumber);
               tag = TIFFIFD.getTag(tagNumber, tagSets);
            }

            if (tag == null) {
               tag = new TIFFTag("unknown", 0, 0, (TIFFTagSet)null);
            }

            byte type;
            if (tag.isDataTypeOK(13)) {
               type = 13;
            } else {
               type = 4;
            }

            f = new TIFFField(tag, type, 1, subIFD);
         } else if (!name.equals("TIFFField")) {
            fatal(node, "Expected either \"TIFFIFD\" or \"TIFFField\" node, got " + name);
         } else {
            int number = Integer.parseInt(getAttribute(node, "number"));
            TIFFTagSet tagSet = null;
            Iterator iter = tagSets.iterator();

            while(iter.hasNext()) {
               TIFFTagSet t = (TIFFTagSet)iter.next();
               if (t.getTag(number) != null) {
                  tagSet = t;
                  break;
               }
            }

            f = TIFFField.createFromMetadataNode(tagSet, node);
         }

         ifd.addTIFFField(f);
      }

      return ifd;
   }

   private void mergeNativeTree(Node root) throws IIOInvalidTreeException {
      if (!root.getNodeName().equals("com_sun_media_imageio_plugins_tiff_image_1.0")) {
         fatal(root, "Root must be com_sun_media_imageio_plugins_tiff_image_1.0");
      }

      Node node = root.getFirstChild();
      if (node == null || !node.getNodeName().equals("TIFFIFD")) {
         fatal(root, "Root must have \"TIFFIFD\" child");
      }

      TIFFIFD ifd = parseIFD(node);
      List rootIFDTagSets = this.rootIFD.getTagSetList();
      Iterator tagSetIter = ifd.getTagSetList().iterator();

      while(tagSetIter.hasNext()) {
         Object o = tagSetIter.next();
         if (o instanceof TIFFTagSet && !rootIFDTagSets.contains(o)) {
            this.rootIFD.addTagSet((TIFFTagSet)o);
         }
      }

      Iterator ifdIter = ifd.iterator();

      while(ifdIter.hasNext()) {
         TIFFField field = (TIFFField)ifdIter.next();
         this.rootIFD.addTIFFField(field);
      }

   }

   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
      if (formatName.equals("com_sun_media_imageio_plugins_tiff_image_1.0")) {
         if (root == null) {
            throw new IllegalArgumentException("root == null!");
         }

         this.mergeNativeTree(root);
      } else {
         if (!formatName.equals("javax_imageio_1.0")) {
            throw new IllegalArgumentException("Not a recognized format!");
         }

         if (root == null) {
            throw new IllegalArgumentException("root == null!");
         }

         this.mergeStandardTree(root);
      }

   }

   public void reset() {
      this.rootIFD = new TIFFIFD(this.tagSets);
   }

   public TIFFIFD getRootIFD() {
      return this.rootIFD;
   }

   public TIFFField getTIFFField(int tagNumber) {
      return this.rootIFD.getTIFFField(tagNumber);
   }

   public void removeTIFFField(int tagNumber) {
      this.rootIFD.removeTIFFField(tagNumber);
   }

   public TIFFImageMetadata getShallowClone() {
      return new TIFFImageMetadata(this.rootIFD.getShallowClone());
   }
}
