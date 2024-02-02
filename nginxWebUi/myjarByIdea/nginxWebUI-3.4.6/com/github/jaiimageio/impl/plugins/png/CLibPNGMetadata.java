package com.github.jaiimageio.impl.plugins.png;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node;

public class CLibPNGMetadata extends IIOMetadata implements Cloneable {
   public static final String nativeMetadataFormatName = "javax_imageio_png_1.0";
   protected static final String nativeMetadataFormatClassName = "com.github.jaiimageio.impl.plugins.png.CLibPNGMetadataFormat";
   public static final String[] IHDR_colorTypeNames = new String[]{"Grayscale", null, "RGB", "Palette", "GrayAlpha", null, "RGBAlpha"};
   public static final int[] IHDR_numChannels = new int[]{1, 0, 3, 3, 2, 0, 4};
   public static final String[] IHDR_bitDepths = new String[]{"1", "2", "4", "8", "16"};
   public static final String[] IHDR_compressionMethodNames = new String[]{"deflate"};
   public static final String[] IHDR_filterMethodNames = new String[]{"adaptive"};
   public static final String[] IHDR_interlaceMethodNames = new String[]{"none", "adam7"};
   public static final String[] iCCP_compressionMethodNames = new String[]{"deflate"};
   public static final String[] zTXt_compressionMethodNames = new String[]{"deflate"};
   public static final int PHYS_UNIT_UNKNOWN = 0;
   public static final int PHYS_UNIT_METER = 1;
   public static final String[] unitSpecifierNames = new String[]{"unknown", "meter"};
   public static final String[] renderingIntentNames = new String[]{"Perceptual", "Relative colorimetric", "Saturation", "Absolute colorimetric"};
   public static final String[] colorSpaceTypeNames = new String[]{"GRAY", null, "RGB", "RGB", "GRAY", null, "RGB"};
   static final int IHDR_TYPE = chunkType("IHDR");
   static final int PLTE_TYPE = chunkType("PLTE");
   static final int IDAT_TYPE = chunkType("IDAT");
   static final int IEND_TYPE = chunkType("IEND");
   static final int bKGD_TYPE = chunkType("bKGD");
   static final int cHRM_TYPE = chunkType("cHRM");
   static final int gAMA_TYPE = chunkType("gAMA");
   static final int hIST_TYPE = chunkType("hIST");
   static final int iCCP_TYPE = chunkType("iCCP");
   static final int iTXt_TYPE = chunkType("iTXt");
   static final int pHYs_TYPE = chunkType("pHYs");
   static final int sBIT_TYPE = chunkType("sBIT");
   static final int sPLT_TYPE = chunkType("sPLT");
   static final int sRGB_TYPE = chunkType("sRGB");
   static final int tEXt_TYPE = chunkType("tEXt");
   static final int tIME_TYPE = chunkType("tIME");
   static final int tRNS_TYPE = chunkType("tRNS");
   static final int zTXt_TYPE = chunkType("zTXt");
   static final int PNG_COLOR_GRAY = 0;
   static final int PNG_COLOR_RGB = 2;
   static final int PNG_COLOR_PALETTE = 3;
   static final int PNG_COLOR_GRAY_ALPHA = 4;
   static final int PNG_COLOR_RGB_ALPHA = 6;
   public boolean IHDR_present;
   public int IHDR_width;
   public int IHDR_height;
   public int IHDR_bitDepth;
   public int IHDR_colorType;
   public int IHDR_compressionMethod;
   public int IHDR_filterMethod;
   public int IHDR_interlaceMethod;
   public boolean PLTE_present;
   public byte[] PLTE_red;
   public byte[] PLTE_green;
   public byte[] PLTE_blue;
   public boolean bKGD_present;
   public int bKGD_colorType;
   public int bKGD_index;
   public int bKGD_gray;
   public int bKGD_red;
   public int bKGD_green;
   public int bKGD_blue;
   public boolean cHRM_present;
   public int cHRM_whitePointX;
   public int cHRM_whitePointY;
   public int cHRM_redX;
   public int cHRM_redY;
   public int cHRM_greenX;
   public int cHRM_greenY;
   public int cHRM_blueX;
   public int cHRM_blueY;
   public boolean gAMA_present;
   public int gAMA_gamma;
   public boolean hIST_present;
   public char[] hIST_histogram;
   public boolean iCCP_present;
   public String iCCP_profileName;
   public int iCCP_compressionMethod;
   public byte[] iCCP_compressedProfile;
   public ArrayList iTXt_keyword;
   public ArrayList iTXt_compressionFlag;
   public ArrayList iTXt_compressionMethod;
   public ArrayList iTXt_languageTag;
   public ArrayList iTXt_translatedKeyword;
   public ArrayList iTXt_text;
   public boolean pHYs_present;
   public int pHYs_pixelsPerUnitXAxis;
   public int pHYs_pixelsPerUnitYAxis;
   public int pHYs_unitSpecifier;
   public boolean sBIT_present;
   public int sBIT_colorType;
   public int sBIT_grayBits;
   public int sBIT_redBits;
   public int sBIT_greenBits;
   public int sBIT_blueBits;
   public int sBIT_alphaBits;
   public boolean sPLT_present;
   public String sPLT_paletteName;
   public int sPLT_sampleDepth;
   public int[] sPLT_red;
   public int[] sPLT_green;
   public int[] sPLT_blue;
   public int[] sPLT_alpha;
   public int[] sPLT_frequency;
   public boolean sRGB_present;
   public int sRGB_renderingIntent;
   public ArrayList tEXt_keyword;
   public ArrayList tEXt_text;
   public boolean tIME_present;
   public int tIME_year;
   public int tIME_month;
   public int tIME_day;
   public int tIME_hour;
   public int tIME_minute;
   public int tIME_second;
   public boolean tRNS_present;
   public int tRNS_colorType;
   public byte[] tRNS_alpha;
   public int tRNS_gray;
   public int tRNS_red;
   public int tRNS_green;
   public int tRNS_blue;
   public ArrayList zTXt_keyword;
   public ArrayList zTXt_compressionMethod;
   public ArrayList zTXt_text;
   public ArrayList unknownChunkType;
   public ArrayList unknownChunkData;
   private boolean gotHeader;
   private boolean gotMetadata;

   static String toPrintableLatin1(String s) {
      if (s == null) {
         return null;
      } else {
         byte[] data = null;

         byte[] data;
         try {
            data = s.getBytes("ISO-8859-1");
         } catch (UnsupportedEncodingException var6) {
            data = s.getBytes();
         }

         int len = 0;
         int prev = 0;

         for(int i = 0; i < data.length; ++i) {
            int d = data[i] & 255;
            if (prev != 32 || d != 32) {
               if (d > 32 && d <= 126 || d >= 161 && d <= 255 || d == 32 && len != 0) {
                  data[len++] = (byte)d;
               }

               prev = d;
            }
         }

         if (len == 0) {
            return "";
         } else {
            if (data[len - 1] == 32) {
               --len;
            }

            return new String(data, 0, len);
         }
      }
   }

   public CLibPNGMetadata() {
      super(true, "javax_imageio_png_1.0", "com.github.jaiimageio.impl.plugins.png.CLibPNGMetadataFormat", (String[])null, (String[])null);
      this.iTXt_keyword = new ArrayList();
      this.iTXt_compressionFlag = new ArrayList();
      this.iTXt_compressionMethod = new ArrayList();
      this.iTXt_languageTag = new ArrayList();
      this.iTXt_translatedKeyword = new ArrayList();
      this.iTXt_text = new ArrayList();
      this.tEXt_keyword = new ArrayList();
      this.tEXt_text = new ArrayList();
      this.zTXt_keyword = new ArrayList();
      this.zTXt_compressionMethod = new ArrayList();
      this.zTXt_text = new ArrayList();
      this.unknownChunkType = new ArrayList();
      this.unknownChunkData = new ArrayList();
      this.gotHeader = false;
      this.gotMetadata = false;
   }

   public CLibPNGMetadata(IIOMetadata metadata) throws IIOInvalidTreeException {
      this();
      if (metadata != null) {
         List formats = Arrays.asList(metadata.getMetadataFormatNames());
         String format;
         if (formats.contains("javax_imageio_png_1.0")) {
            format = "javax_imageio_png_1.0";
            this.setFromTree(format, metadata.getAsTree(format));
         } else if (metadata.isStandardMetadataFormatSupported()) {
            format = "javax_imageio_1.0";
            this.setFromTree(format, metadata.getAsTree(format));
         }
      }

   }

   public void initialize(ImageTypeSpecifier imageType, int numBands, ImageWriteParam param, int interlaceMethod) {
      ColorModel colorModel = imageType.getColorModel();
      SampleModel sampleModel = imageType.getSampleModel();
      this.IHDR_width = sampleModel.getWidth();
      this.IHDR_height = sampleModel.getHeight();
      int[] sampleSize = sampleModel.getSampleSize();
      int bitDepth = sampleSize[0];

      for(int i = 1; i < sampleSize.length; ++i) {
         if (sampleSize[i] > bitDepth) {
            bitDepth = sampleSize[i];
         }
      }

      if (sampleSize.length > 1 && bitDepth < 8) {
         bitDepth = 8;
      }

      if (bitDepth > 2 && bitDepth < 4) {
         bitDepth = 4;
      } else if (bitDepth > 4 && bitDepth < 8) {
         bitDepth = 8;
      } else if (bitDepth > 8 && bitDepth < 16) {
         bitDepth = 16;
      } else if (bitDepth > 16) {
         throw new RuntimeException("bitDepth > 16!");
      }

      this.IHDR_bitDepth = bitDepth;
      if (colorModel instanceof IndexColorModel) {
         IndexColorModel icm = (IndexColorModel)colorModel;
         int size = icm.getMapSize();
         byte[] reds = new byte[size];
         icm.getReds(reds);
         byte[] greens = new byte[size];
         icm.getGreens(greens);
         byte[] blues = new byte[size];
         icm.getBlues(blues);
         boolean isGray = false;
         if (!this.IHDR_present || this.IHDR_colorType != 3) {
            isGray = true;
            int scale = 255 / ((1 << this.IHDR_bitDepth) - 1);

            for(int i = 0; i < size; ++i) {
               byte red = reds[i];
               if (red != (byte)(i * scale) || red != greens[i] || red != blues[i]) {
                  isGray = false;
                  break;
               }
            }
         }

         boolean hasAlpha = colorModel.hasAlpha();
         byte[] alpha = null;
         if (hasAlpha) {
            alpha = new byte[size];
            icm.getAlphas(alpha);
         }

         if (isGray && hasAlpha) {
            this.IHDR_colorType = 4;
         } else if (isGray) {
            this.IHDR_colorType = 0;
         } else {
            this.IHDR_colorType = 3;
            this.PLTE_present = true;
            this.PLTE_red = (byte[])((byte[])reds.clone());
            this.PLTE_green = (byte[])((byte[])greens.clone());
            this.PLTE_blue = (byte[])((byte[])blues.clone());
            if (hasAlpha) {
               this.tRNS_present = true;
               this.tRNS_colorType = 3;
               this.tRNS_alpha = (byte[])((byte[])alpha.clone());
            }
         }
      } else if (numBands == 1) {
         this.IHDR_colorType = 0;
      } else if (numBands == 2) {
         this.IHDR_colorType = 4;
      } else if (numBands == 3) {
         this.IHDR_colorType = 2;
      } else {
         if (numBands != 4) {
            throw new RuntimeException("Number of bands not 1-4!");
         }

         this.IHDR_colorType = 6;
      }

      this.IHDR_compressionMethod = this.IHDR_filterMethod = 0;
      if (param != null && param.getProgressiveMode() == 0) {
         this.IHDR_interlaceMethod = 0;
      } else if (param != null && param.getProgressiveMode() == 1) {
         this.IHDR_interlaceMethod = 1;
      } else {
         this.IHDR_interlaceMethod = interlaceMethod;
      }

      this.IHDR_present = true;
   }

   public boolean isReadOnly() {
      return false;
   }

   private ArrayList cloneBytesArrayList(ArrayList in) {
      if (in == null) {
         return null;
      } else {
         ArrayList list = new ArrayList(in.size());
         Iterator iter = in.iterator();

         while(iter.hasNext()) {
            Object o = iter.next();
            if (o == null) {
               list.add((Object)null);
            } else {
               list.add(((byte[])((byte[])o)).clone());
            }
         }

         return list;
      }
   }

   public Object clone() {
      CLibPNGMetadata metadata;
      try {
         metadata = (CLibPNGMetadata)super.clone();
      } catch (CloneNotSupportedException var3) {
         return null;
      }

      metadata.unknownChunkData = this.cloneBytesArrayList(this.unknownChunkData);
      return metadata;
   }

   public Node getAsTree(String formatName) {
      if (formatName.equals("javax_imageio_png_1.0")) {
         return this.getNativeTree();
      } else if (formatName.equals("javax_imageio_1.0")) {
         return this.getStandardTree();
      } else {
         throw new IllegalArgumentException("Not a recognized format!");
      }
   }

   private Node getNativeTree() {
      IIOMetadataNode node = null;
      IIOMetadataNode root = new IIOMetadataNode("javax_imageio_png_1.0");
      IIOMetadataNode unknown_parent;
      if (this.IHDR_present) {
         unknown_parent = new IIOMetadataNode("IHDR");
         unknown_parent.setAttribute("width", Integer.toString(this.IHDR_width));
         unknown_parent.setAttribute("height", Integer.toString(this.IHDR_height));
         unknown_parent.setAttribute("bitDepth", Integer.toString(this.IHDR_bitDepth));
         unknown_parent.setAttribute("colorType", IHDR_colorTypeNames[this.IHDR_colorType]);
         unknown_parent.setAttribute("compressionMethod", IHDR_compressionMethodNames[this.IHDR_compressionMethod]);
         unknown_parent.setAttribute("filterMethod", IHDR_filterMethodNames[this.IHDR_filterMethod]);
         unknown_parent.setAttribute("interlaceMethod", IHDR_interlaceMethodNames[this.IHDR_interlaceMethod]);
         root.appendChild(unknown_parent);
      }

      int i;
      int i;
      IIOMetadataNode entry;
      if (this.PLTE_present) {
         unknown_parent = new IIOMetadataNode("PLTE");
         i = this.PLTE_red.length;

         for(i = 0; i < i; ++i) {
            entry = new IIOMetadataNode("PLTEEntry");
            entry.setAttribute("index", Integer.toString(i));
            entry.setAttribute("red", Integer.toString(this.PLTE_red[i] & 255));
            entry.setAttribute("green", Integer.toString(this.PLTE_green[i] & 255));
            entry.setAttribute("blue", Integer.toString(this.PLTE_blue[i] & 255));
            unknown_parent.appendChild(entry);
         }

         root.appendChild(unknown_parent);
      }

      if (this.bKGD_present) {
         unknown_parent = new IIOMetadataNode("bKGD");
         if (this.bKGD_colorType == 3) {
            node = new IIOMetadataNode("bKGD_Palette");
            node.setAttribute("index", Integer.toString(this.bKGD_index));
         } else if (this.bKGD_colorType == 0) {
            node = new IIOMetadataNode("bKGD_Grayscale");
            node.setAttribute("gray", Integer.toString(this.bKGD_gray));
         } else if (this.bKGD_colorType == 2) {
            node = new IIOMetadataNode("bKGD_RGB");
            node.setAttribute("red", Integer.toString(this.bKGD_red));
            node.setAttribute("green", Integer.toString(this.bKGD_green));
            node.setAttribute("blue", Integer.toString(this.bKGD_blue));
         }

         unknown_parent.appendChild(node);
         root.appendChild(unknown_parent);
      }

      if (this.cHRM_present) {
         unknown_parent = new IIOMetadataNode("cHRM");
         unknown_parent.setAttribute("whitePointX", Integer.toString(this.cHRM_whitePointX));
         unknown_parent.setAttribute("whitePointY", Integer.toString(this.cHRM_whitePointY));
         unknown_parent.setAttribute("redX", Integer.toString(this.cHRM_redX));
         unknown_parent.setAttribute("redY", Integer.toString(this.cHRM_redY));
         unknown_parent.setAttribute("greenX", Integer.toString(this.cHRM_greenX));
         unknown_parent.setAttribute("greenY", Integer.toString(this.cHRM_greenY));
         unknown_parent.setAttribute("blueX", Integer.toString(this.cHRM_blueX));
         unknown_parent.setAttribute("blueY", Integer.toString(this.cHRM_blueY));
         root.appendChild(unknown_parent);
      }

      if (this.gAMA_present) {
         unknown_parent = new IIOMetadataNode("gAMA");
         unknown_parent.setAttribute("value", Integer.toString(this.gAMA_gamma));
         root.appendChild(unknown_parent);
      }

      IIOMetadataNode unknown_node;
      if (this.hIST_present) {
         unknown_parent = new IIOMetadataNode("hIST");

         for(i = 0; i < this.hIST_histogram.length; ++i) {
            unknown_node = new IIOMetadataNode("hISTEntry");
            unknown_node.setAttribute("index", Integer.toString(i));
            unknown_node.setAttribute("value", Integer.toString(this.hIST_histogram[i]));
            unknown_parent.appendChild(unknown_node);
         }

         root.appendChild(unknown_parent);
      }

      if (this.iCCP_present) {
         unknown_parent = new IIOMetadataNode("iCCP");
         unknown_parent.setAttribute("profileName", this.iCCP_profileName);
         unknown_parent.setAttribute("compressionMethod", iCCP_compressionMethodNames[this.iCCP_compressionMethod]);
         Object profile = this.iCCP_compressedProfile;
         if (profile != null) {
            profile = ((byte[])((byte[])profile)).clone();
         }

         unknown_parent.setUserObject(profile);
         root.appendChild(unknown_parent);
      }

      if (this.iTXt_keyword.size() > 0) {
         unknown_parent = new IIOMetadataNode("iTXt");

         for(i = 0; i < this.iTXt_keyword.size(); ++i) {
            entry = new IIOMetadataNode("iTXtEntry");
            entry.setAttribute("keyword", (String)this.iTXt_keyword.get(i));
            Integer val = (Integer)this.iTXt_compressionFlag.get(i);
            entry.setAttribute("compressionFlag", val.toString());
            val = (Integer)this.iTXt_compressionMethod.get(i);
            entry.setAttribute("compressionMethod", val.toString());
            entry.setAttribute("languageTag", (String)this.iTXt_languageTag.get(i));
            entry.setAttribute("translatedKeyword", (String)this.iTXt_translatedKeyword.get(i));
            entry.setAttribute("text", (String)this.iTXt_text.get(i));
            unknown_parent.appendChild(entry);
         }

         root.appendChild(unknown_parent);
      }

      if (this.pHYs_present) {
         unknown_parent = new IIOMetadataNode("pHYs");
         unknown_parent.setAttribute("pixelsPerUnitXAxis", Integer.toString(this.pHYs_pixelsPerUnitXAxis));
         unknown_parent.setAttribute("pixelsPerUnitYAxis", Integer.toString(this.pHYs_pixelsPerUnitYAxis));
         unknown_parent.setAttribute("unitSpecifier", unitSpecifierNames[this.pHYs_unitSpecifier]);
         root.appendChild(unknown_parent);
      }

      if (this.sBIT_present) {
         unknown_parent = new IIOMetadataNode("sBIT");
         if (this.sBIT_colorType == 0) {
            node = new IIOMetadataNode("sBIT_Grayscale");
            node.setAttribute("gray", Integer.toString(this.sBIT_grayBits));
         } else if (this.sBIT_colorType == 4) {
            node = new IIOMetadataNode("sBIT_GrayAlpha");
            node.setAttribute("gray", Integer.toString(this.sBIT_grayBits));
            node.setAttribute("alpha", Integer.toString(this.sBIT_alphaBits));
         } else if (this.sBIT_colorType == 2) {
            node = new IIOMetadataNode("sBIT_RGB");
            node.setAttribute("red", Integer.toString(this.sBIT_redBits));
            node.setAttribute("green", Integer.toString(this.sBIT_greenBits));
            node.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
         } else if (this.sBIT_colorType == 6) {
            node = new IIOMetadataNode("sBIT_RGBAlpha");
            node.setAttribute("red", Integer.toString(this.sBIT_redBits));
            node.setAttribute("green", Integer.toString(this.sBIT_greenBits));
            node.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
            node.setAttribute("alpha", Integer.toString(this.sBIT_alphaBits));
         } else if (this.sBIT_colorType == 3) {
            node = new IIOMetadataNode("sBIT_Palette");
            node.setAttribute("red", Integer.toString(this.sBIT_redBits));
            node.setAttribute("green", Integer.toString(this.sBIT_greenBits));
            node.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
         }

         unknown_parent.appendChild(node);
         root.appendChild(unknown_parent);
      }

      if (this.sPLT_present) {
         unknown_parent = new IIOMetadataNode("sPLT");
         unknown_parent.setAttribute("name", this.sPLT_paletteName);
         unknown_parent.setAttribute("sampleDepth", Integer.toString(this.sPLT_sampleDepth));
         i = this.sPLT_red.length;

         for(i = 0; i < i; ++i) {
            entry = new IIOMetadataNode("sPLTEntry");
            entry.setAttribute("index", Integer.toString(i));
            entry.setAttribute("red", Integer.toString(this.sPLT_red[i]));
            entry.setAttribute("green", Integer.toString(this.sPLT_green[i]));
            entry.setAttribute("blue", Integer.toString(this.sPLT_blue[i]));
            entry.setAttribute("alpha", Integer.toString(this.sPLT_alpha[i]));
            entry.setAttribute("frequency", Integer.toString(this.sPLT_frequency[i]));
            unknown_parent.appendChild(entry);
         }

         root.appendChild(unknown_parent);
      }

      if (this.sRGB_present) {
         unknown_parent = new IIOMetadataNode("sRGB");
         unknown_parent.setAttribute("renderingIntent", renderingIntentNames[this.sRGB_renderingIntent]);
         root.appendChild(unknown_parent);
      }

      if (this.tEXt_keyword.size() > 0) {
         unknown_parent = new IIOMetadataNode("tEXt");

         for(i = 0; i < this.tEXt_keyword.size(); ++i) {
            unknown_node = new IIOMetadataNode("tEXtEntry");
            unknown_node.setAttribute("keyword", (String)this.tEXt_keyword.get(i));
            unknown_node.setAttribute("value", (String)this.tEXt_text.get(i));
            unknown_parent.appendChild(unknown_node);
         }

         root.appendChild(unknown_parent);
      }

      if (this.tIME_present) {
         unknown_parent = new IIOMetadataNode("tIME");
         unknown_parent.setAttribute("year", Integer.toString(this.tIME_year));
         unknown_parent.setAttribute("month", Integer.toString(this.tIME_month));
         unknown_parent.setAttribute("day", Integer.toString(this.tIME_day));
         unknown_parent.setAttribute("hour", Integer.toString(this.tIME_hour));
         unknown_parent.setAttribute("minute", Integer.toString(this.tIME_minute));
         unknown_parent.setAttribute("second", Integer.toString(this.tIME_second));
         root.appendChild(unknown_parent);
      }

      if (this.tRNS_present) {
         unknown_parent = new IIOMetadataNode("tRNS");
         if (this.tRNS_colorType == 3) {
            node = new IIOMetadataNode("tRNS_Palette");

            for(i = 0; i < this.tRNS_alpha.length; ++i) {
               unknown_node = new IIOMetadataNode("tRNS_PaletteEntry");
               unknown_node.setAttribute("index", Integer.toString(i));
               unknown_node.setAttribute("alpha", Integer.toString(this.tRNS_alpha[i] & 255));
               node.appendChild(unknown_node);
            }
         } else if (this.tRNS_colorType == 0) {
            node = new IIOMetadataNode("tRNS_Grayscale");
            node.setAttribute("gray", Integer.toString(this.tRNS_gray));
         } else if (this.tRNS_colorType == 2) {
            node = new IIOMetadataNode("tRNS_RGB");
            node.setAttribute("red", Integer.toString(this.tRNS_red));
            node.setAttribute("green", Integer.toString(this.tRNS_green));
            node.setAttribute("blue", Integer.toString(this.tRNS_blue));
         }

         unknown_parent.appendChild(node);
         root.appendChild(unknown_parent);
      }

      if (this.zTXt_keyword.size() > 0) {
         unknown_parent = new IIOMetadataNode("zTXt");

         for(i = 0; i < this.zTXt_keyword.size(); ++i) {
            unknown_node = new IIOMetadataNode("zTXtEntry");
            unknown_node.setAttribute("keyword", (String)this.zTXt_keyword.get(i));
            int cm = (Integer)this.zTXt_compressionMethod.get(i);
            unknown_node.setAttribute("compressionMethod", zTXt_compressionMethodNames[cm]);
            unknown_node.setAttribute("text", (String)this.zTXt_text.get(i));
            unknown_parent.appendChild(unknown_node);
         }

         root.appendChild(unknown_parent);
      }

      if (this.unknownChunkType.size() > 0) {
         unknown_parent = new IIOMetadataNode("UnknownChunks");

         for(i = 0; i < this.unknownChunkType.size(); ++i) {
            unknown_node = new IIOMetadataNode("UnknownChunk");
            unknown_node.setAttribute("type", (String)this.unknownChunkType.get(i));
            unknown_node.setUserObject((byte[])((byte[])this.unknownChunkData.get(i)));
            unknown_parent.appendChild(unknown_node);
         }

         root.appendChild(unknown_parent);
      }

      return root;
   }

   private int getNumChannels() {
      int numChannels = IHDR_numChannels[this.IHDR_colorType];
      if (this.IHDR_colorType == 3 && this.tRNS_present && this.tRNS_colorType == this.IHDR_colorType) {
         numChannels = 4;
      }

      return numChannels;
   }

   public IIOMetadataNode getStandardChromaNode() {
      IIOMetadataNode chroma_node = new IIOMetadataNode("Chroma");
      IIOMetadataNode node = null;
      node = new IIOMetadataNode("ColorSpaceType");
      node.setAttribute("name", colorSpaceTypeNames[this.IHDR_colorType]);
      chroma_node.appendChild(node);
      node = new IIOMetadataNode("NumChannels");
      node.setAttribute("value", Integer.toString(this.getNumChannels()));
      chroma_node.appendChild(node);
      if (this.gAMA_present) {
         node = new IIOMetadataNode("Gamma");
         node.setAttribute("value", Float.toString((float)this.gAMA_gamma * 1.0E-5F));
         chroma_node.appendChild(node);
      }

      node = new IIOMetadataNode("BlackIsZero");
      node.setAttribute("value", "TRUE");
      chroma_node.appendChild(node);
      int g;
      if (this.PLTE_present) {
         boolean hasAlpha = this.tRNS_present && this.tRNS_colorType == 3;
         node = new IIOMetadataNode("Palette");

         for(g = 0; g < this.PLTE_red.length; ++g) {
            IIOMetadataNode entry = new IIOMetadataNode("PaletteEntry");
            entry.setAttribute("index", Integer.toString(g));
            entry.setAttribute("red", Integer.toString(this.PLTE_red[g] & 255));
            entry.setAttribute("green", Integer.toString(this.PLTE_green[g] & 255));
            entry.setAttribute("blue", Integer.toString(this.PLTE_blue[g] & 255));
            if (hasAlpha) {
               int alpha = g < this.tRNS_alpha.length ? this.tRNS_alpha[g] & 255 : 255;
               entry.setAttribute("alpha", Integer.toString(alpha));
            }

            node.appendChild(entry);
         }

         chroma_node.appendChild(node);
      }

      if (this.bKGD_present) {
         if (this.bKGD_colorType == 3) {
            node = new IIOMetadataNode("BackgroundIndex");
            node.setAttribute("value", Integer.toString(this.bKGD_index));
         } else {
            node = new IIOMetadataNode("BackgroundColor");
            int r;
            int b;
            if (this.bKGD_colorType == 0) {
               r = g = b = this.bKGD_gray;
            } else {
               r = this.bKGD_red;
               g = this.bKGD_green;
               b = this.bKGD_blue;
            }

            node.setAttribute("red", Integer.toString(r));
            node.setAttribute("green", Integer.toString(g));
            node.setAttribute("blue", Integer.toString(b));
         }

         chroma_node.appendChild(node);
      }

      return chroma_node;
   }

   public IIOMetadataNode getStandardCompressionNode() {
      IIOMetadataNode compression_node = new IIOMetadataNode("Compression");
      IIOMetadataNode node = null;
      node = new IIOMetadataNode("CompressionTypeName");
      node.setAttribute("value", "deflate");
      compression_node.appendChild(node);
      node = new IIOMetadataNode("Lossless");
      node.setAttribute("value", "TRUE");
      compression_node.appendChild(node);
      node = new IIOMetadataNode("NumProgressiveScans");
      node.setAttribute("value", this.IHDR_interlaceMethod == 0 ? "1" : "7");
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
      node = new IIOMetadataNode("PlanarConfiguration");
      node.setAttribute("value", "PixelInterleaved");
      data_node.appendChild(node);
      node = new IIOMetadataNode("SampleFormat");
      node.setAttribute("value", this.IHDR_colorType == 3 ? "Index" : "UnsignedIntegral");
      data_node.appendChild(node);
      String bitDepth = Integer.toString(this.IHDR_bitDepth);
      node = new IIOMetadataNode("BitsPerSample");
      node.setAttribute("value", this.repeat(bitDepth, this.getNumChannels()));
      data_node.appendChild(node);
      if (this.sBIT_present) {
         node = new IIOMetadataNode("SignificantBitsPerSample");
         String sbits;
         if (this.sBIT_colorType != 0 && this.sBIT_colorType != 4) {
            sbits = Integer.toString(this.sBIT_redBits) + " " + Integer.toString(this.sBIT_greenBits) + " " + Integer.toString(this.sBIT_blueBits);
         } else {
            sbits = Integer.toString(this.sBIT_grayBits);
         }

         if (this.sBIT_colorType == 4 || this.sBIT_colorType == 6) {
            sbits = sbits + " " + Integer.toString(this.sBIT_alphaBits);
         }

         node.setAttribute("value", sbits);
         data_node.appendChild(node);
      }

      return data_node;
   }

   public IIOMetadataNode getStandardDimensionNode() {
      IIOMetadataNode dimension_node = new IIOMetadataNode("Dimension");
      IIOMetadataNode node = null;
      node = new IIOMetadataNode("PixelAspectRatio");
      float ratio = this.pHYs_present ? (float)this.pHYs_pixelsPerUnitYAxis / (float)this.pHYs_pixelsPerUnitXAxis : 1.0F;
      node.setAttribute("value", Float.toString(ratio));
      dimension_node.appendChild(node);
      node = new IIOMetadataNode("ImageOrientation");
      node.setAttribute("value", "Normal");
      dimension_node.appendChild(node);
      if (this.pHYs_present && this.pHYs_unitSpecifier == 1) {
         node = new IIOMetadataNode("HorizontalPixelSize");
         node.setAttribute("value", Float.toString(1000.0F / (float)this.pHYs_pixelsPerUnitXAxis));
         dimension_node.appendChild(node);
         node = new IIOMetadataNode("VerticalPixelSize");
         node.setAttribute("value", Float.toString(1000.0F / (float)this.pHYs_pixelsPerUnitYAxis));
         dimension_node.appendChild(node);
      }

      return dimension_node;
   }

   public IIOMetadataNode getStandardDocumentNode() {
      if (!this.tIME_present) {
         return null;
      } else {
         IIOMetadataNode document_node = new IIOMetadataNode("Document");
         IIOMetadataNode node = null;
         node = new IIOMetadataNode("ImageModificationTime");
         node.setAttribute("year", Integer.toString(this.tIME_year));
         node.setAttribute("month", Integer.toString(this.tIME_month));
         node.setAttribute("day", Integer.toString(this.tIME_day));
         node.setAttribute("hour", Integer.toString(this.tIME_hour));
         node.setAttribute("minute", Integer.toString(this.tIME_minute));
         node.setAttribute("second", Integer.toString(this.tIME_second));
         document_node.appendChild(node);
         return document_node;
      }
   }

   public IIOMetadataNode getStandardTextNode() {
      int numEntries = this.tEXt_keyword.size() + this.iTXt_keyword.size() + this.zTXt_keyword.size();
      if (numEntries == 0) {
         return null;
      } else {
         IIOMetadataNode text_node = new IIOMetadataNode("Text");
         IIOMetadataNode node = null;

         int i;
         for(i = 0; i < this.tEXt_keyword.size(); ++i) {
            node = new IIOMetadataNode("TextEntry");
            node.setAttribute("keyword", (String)this.tEXt_keyword.get(i));
            node.setAttribute("value", (String)this.tEXt_text.get(i));
            node.setAttribute("encoding", "ISO-8859-1");
            node.setAttribute("compression", "none");
            text_node.appendChild(node);
         }

         for(i = 0; i < this.iTXt_keyword.size(); ++i) {
            node = new IIOMetadataNode("TextEntry");
            node.setAttribute("keyword", (String)this.iTXt_keyword.get(i));
            node.setAttribute("value", (String)this.iTXt_text.get(i));
            node.setAttribute("language", (String)this.iTXt_languageTag.get(i));
            if ((Integer)this.iTXt_compressionFlag.get(i) == 1) {
               node.setAttribute("compression", "deflate");
            } else {
               node.setAttribute("compression", "none");
            }

            text_node.appendChild(node);
         }

         for(i = 0; i < this.zTXt_keyword.size(); ++i) {
            node = new IIOMetadataNode("TextEntry");
            node.setAttribute("keyword", (String)this.zTXt_keyword.get(i));
            node.setAttribute("value", (String)this.zTXt_text.get(i));
            node.setAttribute("compression", "deflate");
            text_node.appendChild(node);
         }

         return text_node;
      }
   }

   public IIOMetadataNode getStandardTransparencyNode() {
      IIOMetadataNode transparency_node = new IIOMetadataNode("Transparency");
      IIOMetadataNode node = null;
      node = new IIOMetadataNode("Alpha");
      boolean hasAlpha = this.IHDR_colorType == 6 || this.IHDR_colorType == 4 || this.IHDR_colorType == 3 && this.tRNS_present && this.tRNS_colorType == this.IHDR_colorType && this.tRNS_alpha != null;
      node.setAttribute("value", hasAlpha ? "nonpremultiplied" : "none");
      transparency_node.appendChild(node);
      if (this.tRNS_present && (this.tRNS_colorType == 2 || this.tRNS_colorType == 0)) {
         node = new IIOMetadataNode("TransparentColor");
         if (this.tRNS_colorType == 2) {
            node.setAttribute("value", Integer.toString(this.tRNS_red) + " " + Integer.toString(this.tRNS_green) + " " + Integer.toString(this.tRNS_blue));
         } else if (this.tRNS_colorType == 0) {
            node.setAttribute("value", Integer.toString(this.tRNS_gray));
         }

         transparency_node.appendChild(node);
      }

      return transparency_node;
   }

   private void fatal(Node node, String reason) throws IIOInvalidTreeException {
      throw new IIOInvalidTreeException(reason, node);
   }

   private int getIntAttribute(Node node, String name, int defaultValue, boolean required) throws IIOInvalidTreeException {
      String value = this.getAttribute(node, name, (String)null, required);
      return value == null ? defaultValue : Integer.parseInt(value);
   }

   private float getFloatAttribute(Node node, String name, float defaultValue, boolean required) throws IIOInvalidTreeException {
      String value = this.getAttribute(node, name, (String)null, required);
      return value == null ? defaultValue : Float.parseFloat(value);
   }

   private int getIntAttribute(Node node, String name) throws IIOInvalidTreeException {
      return this.getIntAttribute(node, name, -1, true);
   }

   private float getFloatAttribute(Node node, String name) throws IIOInvalidTreeException {
      return this.getFloatAttribute(node, name, -1.0F, true);
   }

   private boolean getBooleanAttribute(Node node, String name, boolean defaultValue, boolean required) throws IIOInvalidTreeException {
      Node attr = node.getAttributes().getNamedItem(name);
      if (attr == null) {
         if (!required) {
            return defaultValue;
         }

         this.fatal(node, "Required attribute " + name + " not present!");
      }

      String value = attr.getNodeValue();
      if (value.equalsIgnoreCase("true")) {
         return true;
      } else if (value.equalsIgnoreCase("false")) {
         return false;
      } else {
         this.fatal(node, "Attribute " + name + " must be 'true' or 'false'!");
         return false;
      }
   }

   private boolean getBooleanAttribute(Node node, String name) throws IIOInvalidTreeException {
      return this.getBooleanAttribute(node, name, false, true);
   }

   private int getEnumeratedAttribute(Node node, String name, String[] legalNames, int defaultValue, boolean required) throws IIOInvalidTreeException {
      Node attr = node.getAttributes().getNamedItem(name);
      if (attr == null) {
         if (!required) {
            return defaultValue;
         }

         this.fatal(node, "Required attribute " + name + " not present!");
      }

      String value = attr.getNodeValue();

      for(int i = 0; i < legalNames.length; ++i) {
         if (value.equals(legalNames[i])) {
            return i;
         }
      }

      this.fatal(node, "Illegal value for attribute " + name + "!");
      return -1;
   }

   private int getEnumeratedAttribute(Node node, String name, String[] legalNames) throws IIOInvalidTreeException {
      return this.getEnumeratedAttribute(node, name, legalNames, -1, true);
   }

   private String getAttribute(Node node, String name, String defaultValue, boolean required) throws IIOInvalidTreeException {
      Node attr = node.getAttributes().getNamedItem(name);
      if (attr == null) {
         if (!required) {
            return defaultValue;
         }

         this.fatal(node, "Required attribute " + name + " not present!");
      }

      return attr.getNodeValue();
   }

   private String getAttribute(Node node, String name) throws IIOInvalidTreeException {
      return this.getAttribute(node, name, (String)null, true);
   }

   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
      if (formatName.equals("javax_imageio_png_1.0")) {
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

   private void mergeNativeTree(Node root) throws IIOInvalidTreeException {
      if (!root.getNodeName().equals("javax_imageio_png_1.0")) {
         this.fatal(root, "Root must be javax_imageio_png_1.0");
      }

      for(Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
         String name = node.getNodeName();
         if (name.equals("IHDR")) {
            this.IHDR_width = this.getIntAttribute(node, "width");
            this.IHDR_height = this.getIntAttribute(node, "height");
            this.IHDR_bitDepth = this.getEnumeratedAttribute(node, "bitDepth", IHDR_bitDepths);
            this.IHDR_colorType = this.getEnumeratedAttribute(node, "colorType", IHDR_colorTypeNames);
            this.IHDR_compressionMethod = this.getEnumeratedAttribute(node, "compressionMethod", IHDR_compressionMethodNames);
            this.IHDR_filterMethod = this.getEnumeratedAttribute(node, "filterMethod", IHDR_filterMethodNames);
            this.IHDR_interlaceMethod = this.getEnumeratedAttribute(node, "interlaceMethod", IHDR_interlaceMethodNames);
            this.IHDR_present = true;
         } else {
            Node tRNS_paletteEntry;
            int index;
            byte[] alpha;
            int maxindex;
            if (name.equals("PLTE")) {
               byte[] red = new byte[256];
               byte[] green = new byte[256];
               alpha = new byte[256];
               maxindex = -1;
               tRNS_paletteEntry = node.getFirstChild();
               if (tRNS_paletteEntry == null) {
                  this.fatal(node, "Palette has no entries!");
               }

               while(tRNS_paletteEntry != null) {
                  if (!tRNS_paletteEntry.getNodeName().equals("PLTEEntry")) {
                     this.fatal(node, "Only a PLTEEntry may be a child of a PLTE!");
                  }

                  index = this.getIntAttribute(tRNS_paletteEntry, "index");
                  if (index < 0 || index > 255) {
                     this.fatal(node, "Bad value for PLTEEntry attribute index!");
                  }

                  if (index > maxindex) {
                     maxindex = index;
                  }

                  red[index] = (byte)this.getIntAttribute(tRNS_paletteEntry, "red");
                  green[index] = (byte)this.getIntAttribute(tRNS_paletteEntry, "green");
                  alpha[index] = (byte)this.getIntAttribute(tRNS_paletteEntry, "blue");
                  tRNS_paletteEntry = tRNS_paletteEntry.getNextSibling();
               }

               index = maxindex + 1;
               this.PLTE_red = new byte[index];
               this.PLTE_green = new byte[index];
               this.PLTE_blue = new byte[index];
               System.arraycopy(red, 0, this.PLTE_red, 0, index);
               System.arraycopy(green, 0, this.PLTE_green, 0, index);
               System.arraycopy(alpha, 0, this.PLTE_blue, 0, index);
               this.PLTE_present = true;
            } else {
               Node unknown_node;
               String chunkType;
               if (name.equals("bKGD")) {
                  this.bKGD_present = false;
                  unknown_node = node.getFirstChild();
                  if (unknown_node == null) {
                     this.fatal(node, "bKGD node has no children!");
                  }

                  chunkType = unknown_node.getNodeName();
                  if (chunkType.equals("bKGD_Palette")) {
                     this.bKGD_index = this.getIntAttribute(unknown_node, "index");
                     this.bKGD_colorType = 3;
                  } else if (chunkType.equals("bKGD_Grayscale")) {
                     this.bKGD_gray = this.getIntAttribute(unknown_node, "gray");
                     this.bKGD_colorType = 0;
                  } else if (chunkType.equals("bKGD_RGB")) {
                     this.bKGD_red = this.getIntAttribute(unknown_node, "red");
                     this.bKGD_green = this.getIntAttribute(unknown_node, "green");
                     this.bKGD_blue = this.getIntAttribute(unknown_node, "blue");
                     this.bKGD_colorType = 2;
                  } else {
                     this.fatal(node, "Bad child of a bKGD node!");
                  }

                  if (unknown_node.getNextSibling() != null) {
                     this.fatal(node, "bKGD node has more than one child!");
                  }

                  this.bKGD_present = true;
               } else if (name.equals("cHRM")) {
                  this.cHRM_whitePointX = this.getIntAttribute(node, "whitePointX");
                  this.cHRM_whitePointY = this.getIntAttribute(node, "whitePointY");
                  this.cHRM_redX = this.getIntAttribute(node, "redX");
                  this.cHRM_redY = this.getIntAttribute(node, "redY");
                  this.cHRM_greenX = this.getIntAttribute(node, "greenX");
                  this.cHRM_greenY = this.getIntAttribute(node, "greenY");
                  this.cHRM_blueX = this.getIntAttribute(node, "blueX");
                  this.cHRM_blueY = this.getIntAttribute(node, "blueY");
                  this.cHRM_present = true;
               } else if (name.equals("gAMA")) {
                  this.gAMA_gamma = this.getIntAttribute(node, "value");
                  this.gAMA_present = true;
               } else if (name.equals("hIST")) {
                  char[] hist = new char[256];
                  int maxindex = -1;
                  Node hIST_entry = node.getFirstChild();
                  if (hIST_entry == null) {
                     this.fatal(node, "hIST node has no children!");
                  }

                  while(hIST_entry != null) {
                     if (!hIST_entry.getNodeName().equals("hISTEntry")) {
                        this.fatal(node, "Only a hISTEntry may be a child of a hIST!");
                     }

                     maxindex = this.getIntAttribute(hIST_entry, "index");
                     if (maxindex < 0 || maxindex > 255) {
                        this.fatal(node, "Bad value for histEntry attribute index!");
                     }

                     if (maxindex > maxindex) {
                        maxindex = maxindex;
                     }

                     hist[maxindex] = (char)this.getIntAttribute(hIST_entry, "value");
                     hIST_entry = hIST_entry.getNextSibling();
                  }

                  maxindex = maxindex + 1;
                  this.hIST_histogram = new char[maxindex];
                  System.arraycopy(hist, 0, this.hIST_histogram, 0, maxindex);
                  this.hIST_present = true;
               } else if (name.equals("iCCP")) {
                  this.iCCP_profileName = toPrintableLatin1(this.getAttribute(node, "profileName"));
                  this.iCCP_compressionMethod = this.getEnumeratedAttribute(node, "compressionMethod", iCCP_compressionMethodNames);
                  Object compressedProfile = ((IIOMetadataNode)node).getUserObject();
                  if (compressedProfile == null) {
                     this.fatal(node, "No ICCP profile present in user object!");
                  }

                  if (!(compressedProfile instanceof byte[])) {
                     this.fatal(node, "User object not a byte array!");
                  }

                  this.iCCP_compressedProfile = (byte[])((byte[])((byte[])((byte[])compressedProfile)).clone());
                  this.iCCP_present = true;
               } else {
                  String text;
                  if (name.equals("iTXt")) {
                     for(unknown_node = node.getFirstChild(); unknown_node != null; unknown_node = unknown_node.getNextSibling()) {
                        if (!unknown_node.getNodeName().equals("iTXtEntry")) {
                           this.fatal(node, "Only an iTXtEntry may be a child of an iTXt!");
                        }

                        chunkType = toPrintableLatin1(this.getAttribute(unknown_node, "keyword"));
                        this.iTXt_keyword.add(chunkType);
                        boolean compressionFlag = this.getBooleanAttribute(unknown_node, "compressionFlag");
                        this.iTXt_compressionFlag.add(new Boolean(compressionFlag));
                        text = this.getAttribute(unknown_node, "compressionMethod");
                        this.iTXt_compressionMethod.add(text);
                        String languageTag = this.getAttribute(unknown_node, "languageTag");
                        this.iTXt_languageTag.add(languageTag);
                        String translatedKeyword = this.getAttribute(unknown_node, "translatedKeyword");
                        this.iTXt_translatedKeyword.add(translatedKeyword);
                        String text = this.getAttribute(unknown_node, "text");
                        this.iTXt_text.add(text);
                     }
                  } else if (name.equals("pHYs")) {
                     this.pHYs_pixelsPerUnitXAxis = this.getIntAttribute(node, "pixelsPerUnitXAxis");
                     this.pHYs_pixelsPerUnitYAxis = this.getIntAttribute(node, "pixelsPerUnitYAxis");
                     this.pHYs_unitSpecifier = this.getEnumeratedAttribute(node, "unitSpecifier", unitSpecifierNames);
                     this.pHYs_present = true;
                  } else if (name.equals("sBIT")) {
                     this.sBIT_present = false;
                     unknown_node = node.getFirstChild();
                     if (unknown_node == null) {
                        this.fatal(node, "sBIT node has no children!");
                     }

                     chunkType = unknown_node.getNodeName();
                     if (chunkType.equals("sBIT_Grayscale")) {
                        this.sBIT_grayBits = this.getIntAttribute(unknown_node, "gray");
                        this.sBIT_colorType = 0;
                     } else if (chunkType.equals("sBIT_GrayAlpha")) {
                        this.sBIT_grayBits = this.getIntAttribute(unknown_node, "gray");
                        this.sBIT_alphaBits = this.getIntAttribute(unknown_node, "alpha");
                        this.sBIT_colorType = 4;
                     } else if (chunkType.equals("sBIT_RGB")) {
                        this.sBIT_redBits = this.getIntAttribute(unknown_node, "red");
                        this.sBIT_greenBits = this.getIntAttribute(unknown_node, "green");
                        this.sBIT_blueBits = this.getIntAttribute(unknown_node, "blue");
                        this.sBIT_colorType = 2;
                     } else if (chunkType.equals("sBIT_RGBAlpha")) {
                        this.sBIT_redBits = this.getIntAttribute(unknown_node, "red");
                        this.sBIT_greenBits = this.getIntAttribute(unknown_node, "green");
                        this.sBIT_blueBits = this.getIntAttribute(unknown_node, "blue");
                        this.sBIT_alphaBits = this.getIntAttribute(unknown_node, "alpha");
                        this.sBIT_colorType = 6;
                     } else if (chunkType.equals("sBIT_Palette")) {
                        this.sBIT_redBits = this.getIntAttribute(unknown_node, "red");
                        this.sBIT_greenBits = this.getIntAttribute(unknown_node, "green");
                        this.sBIT_blueBits = this.getIntAttribute(unknown_node, "blue");
                        this.sBIT_colorType = 3;
                     } else {
                        this.fatal(node, "Bad child of an sBIT node!");
                     }

                     if (unknown_node.getNextSibling() != null) {
                        this.fatal(node, "sBIT node has more than one child!");
                     }

                     this.sBIT_present = true;
                  } else if (name.equals("sPLT")) {
                     this.sPLT_paletteName = toPrintableLatin1(this.getAttribute(node, "name"));
                     this.sPLT_sampleDepth = this.getIntAttribute(node, "sampleDepth");
                     int[] red = new int[256];
                     int[] green = new int[256];
                     int[] blue = new int[256];
                     int[] alpha = new int[256];
                     int[] frequency = new int[256];
                     index = -1;
                     Node sPLT_entry = node.getFirstChild();
                     if (sPLT_entry == null) {
                        this.fatal(node, "sPLT node has no children!");
                     }

                     int index;
                     while(sPLT_entry != null) {
                        if (!sPLT_entry.getNodeName().equals("sPLTEntry")) {
                           this.fatal(node, "Only an sPLTEntry may be a child of an sPLT!");
                        }

                        index = this.getIntAttribute(sPLT_entry, "index");
                        if (index < 0 || index > 255) {
                           this.fatal(node, "Bad value for PLTEEntry attribute index!");
                        }

                        if (index > index) {
                           index = index;
                        }

                        red[index] = this.getIntAttribute(sPLT_entry, "red");
                        green[index] = this.getIntAttribute(sPLT_entry, "green");
                        blue[index] = this.getIntAttribute(sPLT_entry, "blue");
                        alpha[index] = this.getIntAttribute(sPLT_entry, "alpha");
                        frequency[index] = this.getIntAttribute(sPLT_entry, "frequency");
                        sPLT_entry = sPLT_entry.getNextSibling();
                     }

                     index = index + 1;
                     this.sPLT_red = new int[index];
                     this.sPLT_green = new int[index];
                     this.sPLT_blue = new int[index];
                     this.sPLT_alpha = new int[index];
                     this.sPLT_frequency = new int[index];
                     System.arraycopy(red, 0, this.sPLT_red, 0, index);
                     System.arraycopy(green, 0, this.sPLT_green, 0, index);
                     System.arraycopy(blue, 0, this.sPLT_blue, 0, index);
                     System.arraycopy(alpha, 0, this.sPLT_alpha, 0, index);
                     System.arraycopy(frequency, 0, this.sPLT_frequency, 0, index);
                     this.sPLT_present = true;
                  } else if (name.equals("sRGB")) {
                     this.sRGB_renderingIntent = this.getEnumeratedAttribute(node, "renderingIntent", renderingIntentNames);
                     this.sRGB_present = true;
                  } else if (name.equals("tEXt")) {
                     for(unknown_node = node.getFirstChild(); unknown_node != null; unknown_node = unknown_node.getNextSibling()) {
                        if (!unknown_node.getNodeName().equals("tEXtEntry")) {
                           this.fatal(node, "Only an tEXtEntry may be a child of an tEXt!");
                        }

                        chunkType = toPrintableLatin1(this.getAttribute(unknown_node, "keyword"));
                        this.tEXt_keyword.add(chunkType);
                        String text = this.getAttribute(unknown_node, "value");
                        this.tEXt_text.add(text);
                     }
                  } else if (name.equals("tIME")) {
                     this.tIME_year = this.getIntAttribute(node, "year");
                     this.tIME_month = this.getIntAttribute(node, "month");
                     this.tIME_day = this.getIntAttribute(node, "day");
                     this.tIME_hour = this.getIntAttribute(node, "hour");
                     this.tIME_minute = this.getIntAttribute(node, "minute");
                     this.tIME_second = this.getIntAttribute(node, "second");
                     this.tIME_present = true;
                  } else if (name.equals("tRNS")) {
                     this.tRNS_present = false;
                     unknown_node = node.getFirstChild();
                     if (unknown_node == null) {
                        this.fatal(node, "tRNS node has no children!");
                     }

                     chunkType = unknown_node.getNodeName();
                     if (chunkType.equals("tRNS_Palette")) {
                        alpha = new byte[256];
                        maxindex = -1;
                        tRNS_paletteEntry = unknown_node.getFirstChild();
                        if (tRNS_paletteEntry == null) {
                           this.fatal(node, "tRNS_Palette node has no children!");
                        }

                        while(tRNS_paletteEntry != null) {
                           if (!tRNS_paletteEntry.getNodeName().equals("tRNS_PaletteEntry")) {
                              this.fatal(node, "Only a tRNS_PaletteEntry may be a child of a tRNS_Palette!");
                           }

                           index = this.getIntAttribute(tRNS_paletteEntry, "index");
                           if (index < 0 || index > 255) {
                              this.fatal(node, "Bad value for tRNS_PaletteEntry attribute index!");
                           }

                           if (index > maxindex) {
                              maxindex = index;
                           }

                           alpha[index] = (byte)this.getIntAttribute(tRNS_paletteEntry, "alpha");
                           tRNS_paletteEntry = tRNS_paletteEntry.getNextSibling();
                        }

                        index = maxindex + 1;
                        this.tRNS_alpha = new byte[index];
                        this.tRNS_colorType = 3;
                        System.arraycopy(alpha, 0, this.tRNS_alpha, 0, index);
                     } else if (chunkType.equals("tRNS_Grayscale")) {
                        this.tRNS_gray = this.getIntAttribute(unknown_node, "gray");
                        this.tRNS_colorType = 0;
                     } else if (chunkType.equals("tRNS_RGB")) {
                        this.tRNS_red = this.getIntAttribute(unknown_node, "red");
                        this.tRNS_green = this.getIntAttribute(unknown_node, "green");
                        this.tRNS_blue = this.getIntAttribute(unknown_node, "blue");
                        this.tRNS_colorType = 2;
                     } else {
                        this.fatal(node, "Bad child of a tRNS node!");
                     }

                     if (unknown_node.getNextSibling() != null) {
                        this.fatal(node, "tRNS node has more than one child!");
                     }

                     this.tRNS_present = true;
                  } else if (name.equals("zTXt")) {
                     for(unknown_node = node.getFirstChild(); unknown_node != null; unknown_node = unknown_node.getNextSibling()) {
                        if (!unknown_node.getNodeName().equals("zTXtEntry")) {
                           this.fatal(node, "Only an zTXtEntry may be a child of an zTXt!");
                        }

                        chunkType = toPrintableLatin1(this.getAttribute(unknown_node, "keyword"));
                        this.zTXt_keyword.add(chunkType);
                        int compressionMethod = this.getEnumeratedAttribute(unknown_node, "compressionMethod", zTXt_compressionMethodNames);
                        this.zTXt_compressionMethod.add(new Integer(compressionMethod));
                        text = this.getAttribute(unknown_node, "text");
                        this.zTXt_text.add(text);
                     }
                  } else if (name.equals("UnknownChunks")) {
                     for(unknown_node = node.getFirstChild(); unknown_node != null; unknown_node = unknown_node.getNextSibling()) {
                        if (!unknown_node.getNodeName().equals("UnknownChunk")) {
                           this.fatal(node, "Only an UnknownChunk may be a child of an UnknownChunks!");
                        }

                        chunkType = this.getAttribute(unknown_node, "type");
                        Object chunkData = ((IIOMetadataNode)unknown_node).getUserObject();
                        if (chunkType.length() != 4) {
                           this.fatal(unknown_node, "Chunk type must be 4 characters!");
                        }

                        if (chunkData == null) {
                           this.fatal(unknown_node, "No chunk data present in user object!");
                        }

                        if (!(chunkData instanceof byte[])) {
                           this.fatal(unknown_node, "User object not a byte array!");
                        }

                        this.unknownChunkType.add(chunkType);
                        this.unknownChunkData.add(((byte[])((byte[])chunkData)).clone());
                     }
                  } else {
                     this.fatal(node, "Unknown child of root node!");
                  }
               }
            }
         }
      }

   }

   private boolean isISOLatin(String s) {
      int len = s.length();

      for(int i = 0; i < len; ++i) {
         if (s.charAt(i) > 255) {
            return false;
         }
      }

      return true;
   }

   private void mergeStandardTree(Node root) throws IIOInvalidTreeException {
      if (!root.getNodeName().equals("javax_imageio_1.0")) {
         this.fatal(root, "Root must be javax_imageio_1.0");
      }

      for(Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
         String name = node.getNodeName();
         Node child;
         String childName;
         int num;
         Node child;
         String childName;
         int red;
         int maxBits;
         int maxindex;
         int denom;
         if (name.equals("Chroma")) {
            for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
               childName = child.getNodeName();
               if (childName.equals("Gamma")) {
                  float gamma = this.getFloatAttribute(child, "value");
                  this.gAMA_present = true;
                  this.gAMA_gamma = (int)((double)(gamma * 100000.0F) + 0.5);
               } else if (childName.equals("Palette")) {
                  byte[] red = new byte[256];
                  byte[] green = new byte[256];
                  byte[] blue = new byte[256];
                  maxindex = -1;

                  for(child = child.getFirstChild(); child != null; child = child.getNextSibling()) {
                     childName = child.getNodeName();
                     if (childName.equals("PaletteEntry")) {
                        num = this.getIntAttribute(child, "index");
                        if (num >= 0 && num <= 255) {
                           red[num] = (byte)this.getIntAttribute(child, "red");
                           green[num] = (byte)this.getIntAttribute(child, "green");
                           blue[num] = (byte)this.getIntAttribute(child, "blue");
                           if (num > maxindex) {
                              maxindex = num;
                           }
                        }
                     }
                  }

                  denom = maxindex + 1;
                  this.PLTE_red = new byte[denom];
                  this.PLTE_green = new byte[denom];
                  this.PLTE_blue = new byte[denom];
                  System.arraycopy(red, 0, this.PLTE_red, 0, denom);
                  System.arraycopy(green, 0, this.PLTE_green, 0, denom);
                  System.arraycopy(blue, 0, this.PLTE_blue, 0, denom);
                  this.PLTE_present = true;
               } else if (childName.equals("BackgroundIndex")) {
                  this.bKGD_present = true;
                  this.bKGD_colorType = 3;
                  this.bKGD_index = this.getIntAttribute(child, "value");
               } else if (childName.equals("BackgroundColor")) {
                  red = this.getIntAttribute(child, "red");
                  int green = this.getIntAttribute(child, "green");
                  maxBits = this.getIntAttribute(child, "blue");
                  if (red == green && red == maxBits) {
                     this.bKGD_colorType = 0;
                     this.bKGD_gray = red;
                  } else {
                     this.bKGD_colorType = 2;
                     this.bKGD_red = red;
                     this.bKGD_green = green;
                     this.bKGD_blue = maxBits;
                  }

                  this.bKGD_present = true;
               }
            }
         } else if (name.equals("Compression")) {
            for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
               childName = child.getNodeName();
               if (childName.equals("NumProgressiveScans")) {
                  red = this.getIntAttribute(child, "value");
                  this.IHDR_interlaceMethod = red > 1 ? 1 : 0;
               }
            }
         } else {
            String keyword;
            if (name.equals("Data")) {
               for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                  childName = child.getNodeName();
                  StringTokenizer t;
                  if (!childName.equals("BitsPerSample")) {
                     if (childName.equals("SignificantBitsPerSample")) {
                        keyword = this.getAttribute(child, "value");
                        t = new StringTokenizer(keyword);
                        maxBits = t.countTokens();
                        if (maxBits == 1) {
                           this.sBIT_colorType = 0;
                           this.sBIT_grayBits = Integer.parseInt(t.nextToken());
                        } else if (maxBits == 2) {
                           this.sBIT_colorType = 4;
                           this.sBIT_grayBits = Integer.parseInt(t.nextToken());
                           this.sBIT_alphaBits = Integer.parseInt(t.nextToken());
                        } else if (maxBits == 3) {
                           this.sBIT_colorType = 2;
                           this.sBIT_redBits = Integer.parseInt(t.nextToken());
                           this.sBIT_greenBits = Integer.parseInt(t.nextToken());
                           this.sBIT_blueBits = Integer.parseInt(t.nextToken());
                        } else if (maxBits == 4) {
                           this.sBIT_colorType = 6;
                           this.sBIT_redBits = Integer.parseInt(t.nextToken());
                           this.sBIT_greenBits = Integer.parseInt(t.nextToken());
                           this.sBIT_blueBits = Integer.parseInt(t.nextToken());
                           this.sBIT_alphaBits = Integer.parseInt(t.nextToken());
                        }

                        if (maxBits >= 1 && maxBits <= 4) {
                           this.sBIT_present = true;
                        }
                     }
                  } else {
                     keyword = this.getAttribute(child, "value");
                     t = new StringTokenizer(keyword);
                     maxBits = -1;

                     while(t.hasMoreTokens()) {
                        maxindex = Integer.parseInt(t.nextToken());
                        if (maxindex > maxBits) {
                           maxBits = maxindex;
                        }
                     }

                     if (maxBits < 1) {
                        maxBits = 1;
                     } else if (maxBits == 3) {
                        maxBits = 4;
                     } else if (maxBits > 4 && maxBits < 8) {
                        maxBits = 8;
                     } else if (maxBits > 8) {
                        maxBits = 16;
                     }

                     this.IHDR_bitDepth = maxBits;
                  }
               }
            } else if (!name.equals("Dimension")) {
               if (name.equals("Document")) {
                  for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                     childName = child.getNodeName();
                     if (childName.equals("ImageModificationTime")) {
                        this.tIME_present = true;
                        this.tIME_year = this.getIntAttribute(child, "year");
                        this.tIME_month = this.getIntAttribute(child, "month");
                        this.tIME_day = this.getIntAttribute(child, "day");
                        this.tIME_hour = this.getIntAttribute(child, "hour", 0, false);
                        this.tIME_minute = this.getIntAttribute(child, "minute", 0, false);
                        this.tIME_second = this.getIntAttribute(child, "second", 0, false);
                     }
                  }
               } else if (name.equals("Text")) {
                  for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                     childName = child.getNodeName();
                     if (childName.equals("TextEntry")) {
                        keyword = this.getAttribute(child, "keyword", "text", false);
                        String value = this.getAttribute(child, "value");
                        this.getAttribute(child, "encoding", "unknown", false);
                        String language = this.getAttribute(child, "language", "unknown", false);
                        String compression = this.getAttribute(child, "compression", "other", false);
                        if (this.isISOLatin(value)) {
                           if (compression.equals("zip")) {
                              this.zTXt_keyword.add(toPrintableLatin1(keyword));
                              this.zTXt_text.add(value);
                              this.zTXt_compressionMethod.add(new Integer(0));
                           } else {
                              this.tEXt_keyword.add(toPrintableLatin1(keyword));
                              this.tEXt_text.add(value);
                           }
                        } else {
                           denom = compression.equals("zip") ? 1 : 0;
                           this.iTXt_keyword.add(toPrintableLatin1(keyword));
                           this.iTXt_compressionFlag.add(new Integer(denom));
                           this.iTXt_compressionMethod.add(new Integer(0));
                           this.iTXt_languageTag.add(language);
                           this.iTXt_translatedKeyword.add(keyword);
                           this.iTXt_text.add(value);
                        }
                     }
                  }
               }
            } else {
               boolean gotWidth = false;
               boolean gotHeight = false;
               boolean gotAspectRatio = false;
               float width = -1.0F;
               float height = -1.0F;
               float aspectRatio = -1.0F;

               for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                  childName = child.getNodeName();
                  if (childName.equals("PixelAspectRatio")) {
                     aspectRatio = this.getFloatAttribute(child, "value");
                     gotAspectRatio = true;
                  } else if (childName.equals("HorizontalPixelSize")) {
                     width = this.getFloatAttribute(child, "value");
                     gotWidth = true;
                  } else if (childName.equals("VerticalPixelSize")) {
                     height = this.getFloatAttribute(child, "value");
                     gotHeight = true;
                  }
               }

               if (gotWidth && gotHeight) {
                  this.pHYs_present = true;
                  this.pHYs_unitSpecifier = 1;
                  this.pHYs_pixelsPerUnitXAxis = (int)(1000.0F / width + 0.5F);
                  this.pHYs_pixelsPerUnitYAxis = (int)(1000.0F / height + 0.5F);
               } else if (gotAspectRatio) {
                  this.pHYs_present = true;
                  this.pHYs_unitSpecifier = 0;

                  for(denom = 1; denom < 100; ++denom) {
                     num = (int)(aspectRatio * (float)denom);
                     if ((double)Math.abs((float)(num / denom) - aspectRatio) < 0.001) {
                        break;
                     }
                  }

                  this.pHYs_pixelsPerUnitXAxis = (int)(aspectRatio * (float)denom);
                  this.pHYs_pixelsPerUnitYAxis = denom;
               }
            }
         }
      }

   }

   public void reset() {
      this.IHDR_present = false;
      this.PLTE_present = false;
      this.bKGD_present = false;
      this.cHRM_present = false;
      this.gAMA_present = false;
      this.hIST_present = false;
      this.iCCP_present = false;
      this.iTXt_keyword = new ArrayList();
      this.iTXt_compressionFlag = new ArrayList();
      this.iTXt_compressionMethod = new ArrayList();
      this.iTXt_languageTag = new ArrayList();
      this.iTXt_translatedKeyword = new ArrayList();
      this.iTXt_text = new ArrayList();
      this.pHYs_present = false;
      this.sBIT_present = false;
      this.sPLT_present = false;
      this.sRGB_present = false;
      this.tEXt_keyword = new ArrayList();
      this.tEXt_text = new ArrayList();
      this.tIME_present = false;
      this.tRNS_present = false;
      this.zTXt_keyword = new ArrayList();
      this.zTXt_compressionMethod = new ArrayList();
      this.zTXt_text = new ArrayList();
      this.unknownChunkType = new ArrayList();
      this.unknownChunkData = new ArrayList();
   }

   private static int chunkType(String typeString) {
      char c0 = typeString.charAt(0);
      char c1 = typeString.charAt(1);
      char c2 = typeString.charAt(2);
      char c3 = typeString.charAt(3);
      int type = c0 << 24 | c1 << 16 | c2 << 8 | c3;
      return type;
   }

   private String readNullTerminatedString(ImageInputStream stream) throws IOException {
      StringBuffer b = new StringBuffer();

      int c;
      while((c = stream.read()) != 0) {
         b.append((char)c);
      }

      return b.toString();
   }
}
