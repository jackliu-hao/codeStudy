package com.github.jaiimageio.impl.plugins.bmp;

import com.github.jaiimageio.impl.common.ImageUtil;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class BMPMetadata extends IIOMetadata implements Cloneable, BMPConstants {
   public static final String nativeMetadataFormatName = "com_sun_media_imageio_plugins_bmp_image_1.0";
   public String bmpVersion;
   public int width;
   public int height;
   public short bitsPerPixel;
   public int compression;
   public int imageSize;
   public int xPixelsPerMeter;
   public int yPixelsPerMeter;
   public int colorsUsed;
   public int colorsImportant;
   public int redMask;
   public int greenMask;
   public int blueMask;
   public int alphaMask;
   public int colorSpace;
   public double redX;
   public double redY;
   public double redZ;
   public double greenX;
   public double greenY;
   public double greenZ;
   public double blueX;
   public double blueY;
   public double blueZ;
   public int gammaRed;
   public int gammaGreen;
   public int gammaBlue;
   public int intent;
   public byte[] palette;
   public int paletteSize;
   public int red;
   public int green;
   public int blue;
   public List comments;

   public BMPMetadata() {
      super(true, "com_sun_media_imageio_plugins_bmp_image_1.0", "com.github.jaiimageio.impl.bmp.BMPMetadataFormat", (String[])null, (String[])null);
      this.palette = null;
      this.comments = null;
   }

   public BMPMetadata(IIOMetadata metadata) throws IIOInvalidTreeException {
      this();
      if (metadata != null) {
         List formats = Arrays.asList(metadata.getMetadataFormatNames());
         if (formats.contains("com_sun_media_imageio_plugins_bmp_image_1.0")) {
            this.setFromTree("com_sun_media_imageio_plugins_bmp_image_1.0", metadata.getAsTree("com_sun_media_imageio_plugins_bmp_image_1.0"));
         } else if (metadata.isStandardMetadataFormatSupported()) {
            String format = "javax_imageio_1.0";
            this.setFromTree(format, metadata.getAsTree(format));
         }
      }

   }

   public boolean isReadOnly() {
      return false;
   }

   public Object clone() {
      try {
         BMPMetadata metadata = (BMPMetadata)super.clone();
         return metadata;
      } catch (CloneNotSupportedException var3) {
         return null;
      }
   }

   public Node getAsTree(String formatName) {
      if (formatName.equals("com_sun_media_imageio_plugins_bmp_image_1.0")) {
         return this.getNativeTree();
      } else if (formatName.equals("javax_imageio_1.0")) {
         return this.getStandardTree();
      } else {
         throw new IllegalArgumentException(I18N.getString("BMPMetadata0"));
      }
   }

   private Node getNativeTree() {
      IIOMetadataNode root = new IIOMetadataNode("com_sun_media_imageio_plugins_bmp_image_1.0");
      this.addChildNode(root, "BMPVersion", this.bmpVersion);
      this.addChildNode(root, "Width", new Integer(this.width));
      this.addChildNode(root, "Height", new Integer(this.height));
      this.addChildNode(root, "BitsPerPixel", new Short(this.bitsPerPixel));
      this.addChildNode(root, "Compression", new Integer(this.compression));
      this.addChildNode(root, "ImageSize", new Integer(this.imageSize));
      IIOMetadataNode node;
      if (this.xPixelsPerMeter > 0 && this.yPixelsPerMeter > 0) {
         node = this.addChildNode(root, "PixelsPerMeter", (Object)null);
         this.addChildNode(node, "X", new Integer(this.xPixelsPerMeter));
         this.addChildNode(node, "Y", new Integer(this.yPixelsPerMeter));
      }

      this.addChildNode(root, "ColorsUsed", new Integer(this.colorsUsed));
      this.addChildNode(root, "ColorsImportant", new Integer(this.colorsImportant));
      int version = 0;

      for(int i = 0; i < this.bmpVersion.length(); ++i) {
         if (Character.isDigit(this.bmpVersion.charAt(i))) {
            version = this.bmpVersion.charAt(i) - 48;
         }
      }

      if (version >= 4) {
         node = this.addChildNode(root, "Mask", (Object)null);
         this.addChildNode(node, "Red", new Integer(this.redMask));
         this.addChildNode(node, "Green", new Integer(this.greenMask));
         this.addChildNode(node, "Blue", new Integer(this.blueMask));
         this.addChildNode(node, "Alpha", new Integer(this.alphaMask));
         this.addChildNode(root, "ColorSpaceType", new Integer(this.colorSpace));
         node = this.addChildNode(root, "CIEXYZEndpoints", (Object)null);
         this.addXYZPoints(node, "Red", this.redX, this.redY, this.redZ);
         this.addXYZPoints(node, "Green", this.greenX, this.greenY, this.greenZ);
         this.addXYZPoints(node, "Blue", this.blueX, this.blueY, this.blueZ);
         node = this.addChildNode(root, "Gamma", (Object)null);
         this.addChildNode(node, "Red", new Integer(this.gammaRed));
         this.addChildNode(node, "Green", new Integer(this.gammaGreen));
         this.addChildNode(node, "Blue", new Integer(this.gammaBlue));
         this.addChildNode(root, "Intent", new Integer(this.intent));
      }

      if (this.palette != null && this.paletteSize > 0) {
         node = this.addChildNode(root, "Palette", (Object)null);
         boolean isVersion2 = this.bmpVersion != null && this.bmpVersion.equals("BMP v. 2.x");
         int i = 0;

         for(int j = 0; i < this.paletteSize; ++i) {
            IIOMetadataNode entry = this.addChildNode(node, "PaletteEntry", (Object)null);
            this.blue = this.palette[j++] & 255;
            this.green = this.palette[j++] & 255;
            this.red = this.palette[j++] & 255;
            this.addChildNode(entry, "Red", new Integer(this.red));
            this.addChildNode(entry, "Green", new Integer(this.green));
            this.addChildNode(entry, "Blue", new Integer(this.blue));
            if (!isVersion2) {
               ++j;
            }
         }
      }

      return root;
   }

   protected IIOMetadataNode getStandardChromaNode() {
      IIOMetadataNode node = new IIOMetadataNode("Chroma");
      IIOMetadataNode subNode = new IIOMetadataNode("ColorSpaceType");
      String colorSpaceType;
      if ((this.palette == null || this.paletteSize <= 0) && this.redMask == 0 && this.greenMask == 0 && this.blueMask == 0 && this.bitsPerPixel <= 8) {
         colorSpaceType = "GRAY";
      } else {
         colorSpaceType = "RGB";
      }

      subNode.setAttribute("name", colorSpaceType);
      node.appendChild(subNode);
      subNode = new IIOMetadataNode("NumChannels");
      String numChannels;
      if ((this.palette == null || this.paletteSize <= 0) && this.redMask == 0 && this.greenMask == 0 && this.blueMask == 0 && this.bitsPerPixel <= 8) {
         numChannels = "1";
      } else if (this.alphaMask != 0) {
         numChannels = "4";
      } else {
         numChannels = "3";
      }

      subNode.setAttribute("value", numChannels);
      node.appendChild(subNode);
      if (this.gammaRed != 0 && this.gammaGreen != 0 && this.gammaBlue != 0) {
         subNode = new IIOMetadataNode("Gamma");
         Double gamma = new Double((double)(this.gammaRed + this.gammaGreen + this.gammaBlue) / 3.0);
         subNode.setAttribute("value", gamma.toString());
         node.appendChild(subNode);
      }

      if (numChannels.equals("1") && (this.palette == null || this.paletteSize == 0)) {
         subNode = new IIOMetadataNode("BlackIsZero");
         subNode.setAttribute("value", "TRUE");
         node.appendChild(subNode);
      }

      if (this.palette != null && this.paletteSize > 0) {
         subNode = new IIOMetadataNode("Palette");
         boolean isVersion2 = this.bmpVersion != null && this.bmpVersion.equals("BMP v. 2.x");
         int i = 0;

         for(int j = 0; i < this.paletteSize; ++i) {
            IIOMetadataNode subNode1 = new IIOMetadataNode("PaletteEntry");
            subNode1.setAttribute("index", "" + i);
            subNode1.setAttribute("blue", "" + (this.palette[j++] & 255));
            subNode1.setAttribute("green", "" + (this.palette[j++] & 255));
            subNode1.setAttribute("red", "" + (this.palette[j++] & 255));
            if (!isVersion2) {
               ++j;
            }

            subNode.appendChild(subNode1);
         }

         node.appendChild(subNode);
      }

      return node;
   }

   protected IIOMetadataNode getStandardCompressionNode() {
      IIOMetadataNode node = new IIOMetadataNode("Compression");
      IIOMetadataNode subNode = new IIOMetadataNode("CompressionTypeName");
      subNode.setAttribute("value", compressionTypeNames[this.compression]);
      node.appendChild(subNode);
      subNode = new IIOMetadataNode("Lossless");
      subNode.setAttribute("value", this.compression == 4 ? "FALSE" : "TRUE");
      node.appendChild(subNode);
      return node;
   }

   protected IIOMetadataNode getStandardDataNode() {
      IIOMetadataNode node = new IIOMetadataNode("Data");
      String sampleFormat = this.palette != null && this.paletteSize > 0 ? "Index" : "UnsignedIntegral";
      IIOMetadataNode subNode = new IIOMetadataNode("SampleFormat");
      subNode.setAttribute("value", sampleFormat);
      node.appendChild(subNode);
      String bits = "";
      if (this.redMask == 0 && this.greenMask == 0 && this.blueMask == 0) {
         if (this.palette != null && this.paletteSize > 0) {
            for(int i = 1; i <= 3; ++i) {
               bits = bits + this.bitsPerPixel;
               if (i != 3) {
                  bits = bits + " ";
               }
            }
         } else if (this.bitsPerPixel == 1) {
            bits = "1";
         } else if (this.bitsPerPixel == 4) {
            bits = "4";
         } else if (this.bitsPerPixel == 8) {
            bits = "8";
         } else if (this.bitsPerPixel == 16) {
            bits = "5 6 5";
         } else if (this.bitsPerPixel == 24) {
            bits = "8 8 8";
         } else if (this.bitsPerPixel == 32) {
            bits = "8 8 8 8";
         }
      } else {
         bits = this.countBits(this.redMask) + " " + this.countBits(this.greenMask) + " " + this.countBits(this.blueMask);
         if (this.alphaMask != 0) {
            bits = bits + " " + this.countBits(this.alphaMask);
         }
      }

      if (!bits.equals("")) {
         subNode = new IIOMetadataNode("BitsPerSample");
         subNode.setAttribute("value", bits);
         node.appendChild(subNode);
      }

      return node;
   }

   protected IIOMetadataNode getStandardDimensionNode() {
      if (this.yPixelsPerMeter > 0 && this.xPixelsPerMeter > 0) {
         IIOMetadataNode node = new IIOMetadataNode("Dimension");
         float ratio = (float)this.yPixelsPerMeter / (float)this.xPixelsPerMeter;
         IIOMetadataNode subNode = new IIOMetadataNode("PixelAspectRatio");
         subNode.setAttribute("value", "" + ratio);
         node.appendChild(subNode);
         subNode = new IIOMetadataNode("HorizontalPixelSize");
         subNode.setAttribute("value", "" + 1000.0F / (float)this.xPixelsPerMeter);
         node.appendChild(subNode);
         subNode = new IIOMetadataNode("VerticalPixelSize");
         subNode.setAttribute("value", "" + 1000.0F / (float)this.yPixelsPerMeter);
         node.appendChild(subNode);
         subNode = new IIOMetadataNode("HorizontalPhysicalPixelSpacing");
         subNode.setAttribute("value", "" + 1000.0F / (float)this.xPixelsPerMeter);
         node.appendChild(subNode);
         subNode = new IIOMetadataNode("VerticalPhysicalPixelSpacing");
         subNode.setAttribute("value", "" + 1000.0F / (float)this.yPixelsPerMeter);
         node.appendChild(subNode);
         return node;
      } else {
         return null;
      }
   }

   protected IIOMetadataNode getStandardDocumentNode() {
      if (this.bmpVersion != null) {
         IIOMetadataNode node = new IIOMetadataNode("Document");
         IIOMetadataNode subNode = new IIOMetadataNode("FormatVersion");
         subNode.setAttribute("value", this.bmpVersion);
         node.appendChild(subNode);
         return node;
      } else {
         return null;
      }
   }

   protected IIOMetadataNode getStandardTextNode() {
      if (this.comments == null) {
         return null;
      } else {
         IIOMetadataNode node = new IIOMetadataNode("Text");
         Iterator iter = this.comments.iterator();

         while(iter.hasNext()) {
            String comment = (String)iter.next();
            IIOMetadataNode subNode = new IIOMetadataNode("TextEntry");
            subNode.setAttribute("keyword", "comment");
            subNode.setAttribute("value", comment);
            node.appendChild(subNode);
         }

         return node;
      }
   }

   protected IIOMetadataNode getStandardTransparencyNode() {
      IIOMetadataNode node = new IIOMetadataNode("Transparency");
      IIOMetadataNode subNode = new IIOMetadataNode("Alpha");
      String alpha;
      if (this.alphaMask != 0) {
         alpha = "nonpremultiplied";
      } else {
         alpha = "none";
      }

      subNode.setAttribute("value", alpha);
      node.appendChild(subNode);
      return node;
   }

   private void fatal(Node node, String reason) throws IIOInvalidTreeException {
      throw new IIOInvalidTreeException(reason, node);
   }

   private int getIntAttribute(Node node, String name, int defaultValue, boolean required) throws IIOInvalidTreeException {
      String value = this.getAttribute(node, name, (String)null, required);
      return value == null ? defaultValue : Integer.parseInt(value);
   }

   private double getDoubleAttribute(Node node, String name, double defaultValue, boolean required) throws IIOInvalidTreeException {
      String value = this.getAttribute(node, name, (String)null, required);
      return value == null ? defaultValue : Double.parseDouble(value);
   }

   private int getIntAttribute(Node node, String name) throws IIOInvalidTreeException {
      return this.getIntAttribute(node, name, -1, true);
   }

   private double getDoubleAttribute(Node node, String name) throws IIOInvalidTreeException {
      return this.getDoubleAttribute(node, name, -1.0, true);
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

   void initialize(ColorModel cm, SampleModel sm, ImageWriteParam param) {
      if (param != null) {
         this.bmpVersion = "BMP v. 3.x";
         if (param.getCompressionMode() == 2) {
            String compressionType = param.getCompressionType();
            this.compression = BMPImageWriter.getCompressionType(compressionType);
         }
      } else {
         this.bmpVersion = "BMP v. 3.x";
         this.compression = BMPImageWriter.getPreferredCompressionType(cm, sm);
      }

      this.width = sm.getWidth();
      this.height = sm.getHeight();
      this.bitsPerPixel = (short)cm.getPixelSize();
      if (cm instanceof DirectColorModel) {
         DirectColorModel dcm = (DirectColorModel)cm;
         this.redMask = dcm.getRedMask();
         this.greenMask = dcm.getGreenMask();
         this.blueMask = dcm.getBlueMask();
         this.alphaMask = dcm.getAlphaMask();
      }

      if (cm instanceof IndexColorModel) {
         IndexColorModel icm = (IndexColorModel)cm;
         this.paletteSize = icm.getMapSize();
         byte[] r = new byte[this.paletteSize];
         byte[] g = new byte[this.paletteSize];
         byte[] b = new byte[this.paletteSize];
         icm.getReds(r);
         icm.getGreens(g);
         icm.getBlues(b);
         boolean isVersion2 = this.bmpVersion != null && this.bmpVersion.equals("BMP v. 2.x");
         this.palette = new byte[(isVersion2 ? 3 : 4) * this.paletteSize];
         int i = 0;

         for(int j = 0; i < this.paletteSize; ++i) {
            this.palette[j++] = b[i];
            this.palette[j++] = g[i];
            this.palette[j++] = r[i];
            if (!isVersion2) {
               ++j;
            }
         }
      }

   }

   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
      if (formatName.equals("com_sun_media_imageio_plugins_bmp_image_1.0")) {
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
      if (!root.getNodeName().equals("com_sun_media_imageio_plugins_bmp_image_1.0")) {
         this.fatal(root, "Root must be com_sun_media_imageio_plugins_bmp_image_1.0");
      }

      byte[] r = null;
      byte[] g = null;
      byte[] b = null;
      int maxIndex = -1;

      int numPaletteEntries;
      int index;
      for(Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
         String name = node.getNodeName();
         if (name.equals("BMPVersion")) {
            String value = this.getStringValue(node);
            if (value != null) {
               this.bmpVersion = value;
            }
         } else {
            Integer value;
            if (name.equals("Width")) {
               value = this.getIntegerValue(node);
               if (value != null) {
                  this.width = value;
               }
            } else if (name.equals("Height")) {
               value = this.getIntegerValue(node);
               if (value != null) {
                  this.height = value;
               }
            } else if (name.equals("BitsPerPixel")) {
               Short value = this.getShortValue(node);
               if (value != null) {
                  this.bitsPerPixel = value;
               }
            } else if (name.equals("Compression")) {
               value = this.getIntegerValue(node);
               if (value != null) {
                  this.compression = value;
               }
            } else if (name.equals("ImageSize")) {
               value = this.getIntegerValue(node);
               if (value != null) {
                  this.imageSize = value;
               }
            } else {
               Node paletteEntry;
               String subName;
               Integer value;
               if (name.equals("PixelsPerMeter")) {
                  for(paletteEntry = node.getFirstChild(); paletteEntry != null; paletteEntry = paletteEntry.getNextSibling()) {
                     subName = paletteEntry.getNodeName();
                     if (subName.equals("X")) {
                        value = this.getIntegerValue(paletteEntry);
                        if (value != null) {
                           this.xPixelsPerMeter = value;
                        }
                     } else if (subName.equals("Y")) {
                        value = this.getIntegerValue(paletteEntry);
                        if (value != null) {
                           this.yPixelsPerMeter = value;
                        }
                     }
                  }
               } else if (name.equals("ColorsUsed")) {
                  value = this.getIntegerValue(node);
                  if (value != null) {
                     this.colorsUsed = value;
                  }
               } else if (name.equals("ColorsImportant")) {
                  value = this.getIntegerValue(node);
                  if (value != null) {
                     this.colorsImportant = value;
                  }
               } else if (name.equals("Mask")) {
                  for(paletteEntry = node.getFirstChild(); paletteEntry != null; paletteEntry = paletteEntry.getNextSibling()) {
                     subName = paletteEntry.getNodeName();
                     if (subName.equals("Red")) {
                        value = this.getIntegerValue(paletteEntry);
                        if (value != null) {
                           this.redMask = value;
                        }
                     } else if (subName.equals("Green")) {
                        value = this.getIntegerValue(paletteEntry);
                        if (value != null) {
                           this.greenMask = value;
                        }
                     } else if (subName.equals("Blue")) {
                        value = this.getIntegerValue(paletteEntry);
                        if (value != null) {
                           this.blueMask = value;
                        }
                     } else if (subName.equals("Alpha")) {
                        value = this.getIntegerValue(paletteEntry);
                        if (value != null) {
                           this.alphaMask = value;
                        }
                     }
                  }
               } else if (name.equals("ColorSpace")) {
                  value = this.getIntegerValue(node);
                  if (value != null) {
                     this.colorSpace = value;
                  }
               } else if (name.equals("CIEXYZEndpoints")) {
                  for(paletteEntry = node.getFirstChild(); paletteEntry != null; paletteEntry = paletteEntry.getNextSibling()) {
                     subName = paletteEntry.getNodeName();
                     String subName1;
                     Double value;
                     Node subNode1;
                     if (subName.equals("Red")) {
                        for(subNode1 = paletteEntry.getFirstChild(); subNode1 != null; subNode1 = subNode1.getNextSibling()) {
                           subName1 = subNode1.getNodeName();
                           if (subName1.equals("X")) {
                              value = this.getDoubleValue(subNode1);
                              if (value != null) {
                                 this.redX = value;
                              }
                           } else if (subName1.equals("Y")) {
                              value = this.getDoubleValue(subNode1);
                              if (value != null) {
                                 this.redY = value;
                              }
                           } else if (subName1.equals("Z")) {
                              value = this.getDoubleValue(subNode1);
                              if (value != null) {
                                 this.redZ = value;
                              }
                           }
                        }
                     } else if (subName.equals("Green")) {
                        for(subNode1 = paletteEntry.getFirstChild(); subNode1 != null; subNode1 = subNode1.getNextSibling()) {
                           subName1 = subNode1.getNodeName();
                           if (subName1.equals("X")) {
                              value = this.getDoubleValue(subNode1);
                              if (value != null) {
                                 this.greenX = value;
                              }
                           } else if (subName1.equals("Y")) {
                              value = this.getDoubleValue(subNode1);
                              if (value != null) {
                                 this.greenY = value;
                              }
                           } else if (subName1.equals("Z")) {
                              value = this.getDoubleValue(subNode1);
                              if (value != null) {
                                 this.greenZ = value;
                              }
                           }
                        }
                     } else if (subName.equals("Blue")) {
                        for(subNode1 = paletteEntry.getFirstChild(); subNode1 != null; subNode1 = subNode1.getNextSibling()) {
                           subName1 = subNode1.getNodeName();
                           if (subName1.equals("X")) {
                              value = this.getDoubleValue(subNode1);
                              if (value != null) {
                                 this.blueX = value;
                              }
                           } else if (subName1.equals("Y")) {
                              value = this.getDoubleValue(subNode1);
                              if (value != null) {
                                 this.blueY = value;
                              }
                           } else if (subName1.equals("Z")) {
                              value = this.getDoubleValue(subNode1);
                              if (value != null) {
                                 this.blueZ = value;
                              }
                           }
                        }
                     }
                  }
               } else if (name.equals("Gamma")) {
                  for(paletteEntry = node.getFirstChild(); paletteEntry != null; paletteEntry = paletteEntry.getNextSibling()) {
                     subName = paletteEntry.getNodeName();
                     if (subName.equals("Red")) {
                        value = this.getIntegerValue(paletteEntry);
                        if (value != null) {
                           this.gammaRed = value;
                        }
                     } else if (subName.equals("Green")) {
                        value = this.getIntegerValue(paletteEntry);
                        if (value != null) {
                           this.gammaGreen = value;
                        }
                     } else if (subName.equals("Blue")) {
                        value = this.getIntegerValue(paletteEntry);
                        if (value != null) {
                           this.gammaBlue = value;
                        }
                     }
                  }
               } else if (name.equals("Intent")) {
                  value = this.getIntegerValue(node);
                  if (value != null) {
                     this.intent = value;
                  }
               } else if (!name.equals("Palette")) {
                  if (name.equals("CommentExtensions")) {
                     paletteEntry = node.getFirstChild();
                     if (paletteEntry == null) {
                        this.fatal(node, "CommentExtensions has no entries!");
                     }

                     if (this.comments == null) {
                        this.comments = new ArrayList();
                     }

                     while(paletteEntry != null) {
                        if (!paletteEntry.getNodeName().equals("CommentExtension")) {
                           this.fatal(node, "Only a CommentExtension may be a child of a CommentExtensions!");
                        }

                        this.comments.add(this.getAttribute(paletteEntry, "value"));
                        paletteEntry = paletteEntry.getNextSibling();
                     }
                  } else {
                     this.fatal(node, "Unknown child of root node!");
                  }
               } else {
                  this.paletteSize = this.getIntAttribute(node, "sizeOfPalette");
                  r = new byte[this.paletteSize];
                  g = new byte[this.paletteSize];
                  b = new byte[this.paletteSize];
                  maxIndex = -1;
                  paletteEntry = node.getFirstChild();
                  if (paletteEntry == null) {
                     this.fatal(node, "Palette has no entries!");
                  }

                  for(numPaletteEntries = 0; paletteEntry != null; paletteEntry = paletteEntry.getNextSibling()) {
                     if (!paletteEntry.getNodeName().equals("PaletteEntry")) {
                        this.fatal(node, "Only a PaletteEntry may be a child of a Palette!");
                     }

                     index = -1;

                     for(Node subNode = paletteEntry.getFirstChild(); subNode != null; subNode = subNode.getNextSibling()) {
                        String subName = subNode.getNodeName();
                        Integer value;
                        if (subName.equals("Index")) {
                           value = this.getIntegerValue(subNode);
                           if (value != null) {
                              index = value;
                           }

                           if (index < 0 || index > this.paletteSize - 1) {
                              this.fatal(node, "Bad value for PaletteEntry attribute index!");
                           }
                        } else if (subName.equals("Red")) {
                           value = this.getIntegerValue(subNode);
                           if (value != null) {
                              this.red = value;
                           }
                        } else if (subName.equals("Green")) {
                           value = this.getIntegerValue(subNode);
                           if (value != null) {
                              this.green = value;
                           }
                        } else if (subName.equals("Blue")) {
                           value = this.getIntegerValue(subNode);
                           if (value != null) {
                              this.blue = value;
                           }
                        }
                     }

                     if (index == -1) {
                        index = numPaletteEntries;
                     }

                     if (index > maxIndex) {
                        maxIndex = index;
                     }

                     r[index] = (byte)this.red;
                     g[index] = (byte)this.green;
                     b[index] = (byte)this.blue;
                     ++numPaletteEntries;
                  }
               }
            }
         }
      }

      if (r != null && g != null && b != null) {
         boolean isVersion2 = this.bmpVersion != null && this.bmpVersion.equals("BMP v. 2.x");
         int numEntries = maxIndex + 1;
         this.palette = new byte[(isVersion2 ? 3 : 4) * numEntries];
         numPaletteEntries = 0;

         for(index = 0; numPaletteEntries < numEntries; ++numPaletteEntries) {
            this.palette[index++] = b[numPaletteEntries];
            this.palette[index++] = g[numPaletteEntries];
            this.palette[index++] = r[numPaletteEntries];
            if (!isVersion2) {
               ++index;
            }
         }
      }

   }

   private void mergeStandardTree(Node root) throws IIOInvalidTreeException {
      if (!root.getNodeName().equals("javax_imageio_1.0")) {
         this.fatal(root, "Root must be javax_imageio_1.0");
      }

      String colorSpaceType = null;
      int numChannels = 0;
      int[] bitsPerSample = null;
      boolean hasAlpha = false;
      byte[] r = null;
      byte[] g = null;
      byte[] b = null;
      int maxIndex = -1;

      label324:
      for(Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
         String name = node.getNodeName();
         Node child;
         String childName;
         if (name.equals("Chroma")) {
            for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
               childName = child.getNodeName();
               if (childName.equals("ColorSpaceType")) {
                  colorSpaceType = this.getAttribute(child, "name");
               } else if (childName.equals("NumChannels")) {
                  numChannels = this.getIntAttribute(child, "value");
               } else if (childName.equals("Gamma")) {
                  this.gammaRed = this.gammaGreen = this.gammaBlue = (int)(this.getDoubleAttribute(child, "value") + 0.5);
               } else if (childName.equals("Palette")) {
                  r = new byte[256];
                  g = new byte[256];
                  b = new byte[256];
                  maxIndex = -1;
                  Node paletteEntry = child.getFirstChild();
                  if (paletteEntry == null) {
                     this.fatal(node, "Palette has no entries!");
                  }

                  while(paletteEntry != null) {
                     if (!paletteEntry.getNodeName().equals("PaletteEntry")) {
                        this.fatal(node, "Only a PaletteEntry may be a child of a Palette!");
                     }

                     int index = this.getIntAttribute(paletteEntry, "index");
                     if (index < 0 || index > 255) {
                        this.fatal(node, "Bad value for PaletteEntry attribute index!");
                     }

                     if (index > maxIndex) {
                        maxIndex = index;
                     }

                     r[index] = (byte)this.getIntAttribute(paletteEntry, "red");
                     g[index] = (byte)this.getIntAttribute(paletteEntry, "green");
                     b[index] = (byte)this.getIntAttribute(paletteEntry, "blue");
                     paletteEntry = paletteEntry.getNextSibling();
                  }
               }
            }
         } else if (name.equals("Compression")) {
            for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
               childName = child.getNodeName();
               if (childName.equals("CompressionTypeName")) {
                  String compressionName = this.getAttribute(child, "value");
                  this.compression = BMPImageWriter.getCompressionType(compressionName);
               }
            }
         } else if (name.equals("Data")) {
            for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
               childName = child.getNodeName();
               if (childName.equals("BitsPerSample")) {
                  List bps = new ArrayList(4);
                  String s = this.getAttribute(child, "value");
                  StringTokenizer t = new StringTokenizer(s);

                  while(t.hasMoreTokens()) {
                     bps.add(Integer.valueOf(t.nextToken()));
                  }

                  bitsPerSample = new int[bps.size()];
                  int i = 0;

                  while(true) {
                     if (i >= bitsPerSample.length) {
                        continue label324;
                     }

                     bitsPerSample[i] = (Integer)bps.get(i);
                     ++i;
                  }
               }
            }
         } else if (!name.equals("Dimension")) {
            if (name.equals("Document")) {
               for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                  childName = child.getNodeName();
                  if (childName.equals("FormatVersion")) {
                     this.bmpVersion = this.getAttribute(child, "value");
                     break;
                  }
               }
            } else if (name.equals("Text")) {
               for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                  childName = child.getNodeName();
                  if (childName.equals("TextEntry")) {
                     if (this.comments == null) {
                        this.comments = new ArrayList();
                     }

                     this.comments.add(this.getAttribute(child, "value"));
                  }
               }
            } else if (name.equals("Transparency")) {
               for(child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                  childName = child.getNodeName();
                  if (childName.equals("Alpha")) {
                     hasAlpha = !this.getAttribute(child, "value").equals("none");
                     break;
                  }
               }
            }
         } else {
            boolean gotWidth = false;
            boolean gotHeight = false;
            boolean gotAspectRatio = false;
            boolean gotSpaceX = false;
            boolean gotSpaceY = false;
            double width = -1.0;
            double height = -1.0;
            double aspectRatio = -1.0;
            double spaceX = -1.0;
            double spaceY = -1.0;

            for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
               String childName = child.getNodeName();
               if (childName.equals("PixelAspectRatio")) {
                  aspectRatio = this.getDoubleAttribute(child, "value");
                  gotAspectRatio = true;
               } else if (childName.equals("HorizontalPixelSize")) {
                  width = this.getDoubleAttribute(child, "value");
                  gotWidth = true;
               } else if (childName.equals("VerticalPixelSize")) {
                  height = this.getDoubleAttribute(child, "value");
                  gotHeight = true;
               } else if (childName.equals("HorizontalPhysicalPixelSpacing")) {
                  spaceX = this.getDoubleAttribute(child, "value");
                  gotSpaceX = true;
               } else if (childName.equals("VerticalPhysicalPixelSpacing")) {
                  spaceY = this.getDoubleAttribute(child, "value");
                  gotSpaceY = true;
               }
            }

            if (!gotWidth && !gotHeight && (gotSpaceX || gotSpaceY)) {
               width = spaceX;
               gotWidth = gotSpaceX;
               height = spaceY;
               gotHeight = gotSpaceY;
            }

            if (gotWidth && gotHeight) {
               this.xPixelsPerMeter = (int)(1000.0 / width + 0.5);
               this.yPixelsPerMeter = (int)(1000.0 / height + 0.5);
            } else if (gotAspectRatio && aspectRatio != 0.0) {
               if (gotWidth) {
                  this.xPixelsPerMeter = (int)(1000.0 / width + 0.5);
                  this.yPixelsPerMeter = (int)(aspectRatio * (1000.0 / width) + 0.5);
               } else if (gotHeight) {
                  this.xPixelsPerMeter = (int)(1000.0 / height / aspectRatio + 0.5);
                  this.yPixelsPerMeter = (int)(1000.0 / height + 0.5);
               }
            }
         }
      }

      if (bitsPerSample != null) {
         if (this.palette != null && this.paletteSize > 0) {
            this.bitsPerPixel = (short)bitsPerSample[0];
         } else {
            this.bitsPerPixel = 0;

            for(int i = 0; i < bitsPerSample.length; ++i) {
               this.bitsPerPixel = (short)(this.bitsPerPixel + bitsPerSample[i]);
            }
         }
      } else if (this.palette != null) {
         this.bitsPerPixel = 8;
      } else if (numChannels == 1) {
         this.bitsPerPixel = 8;
      } else if (numChannels == 3) {
         this.bitsPerPixel = 24;
      } else if (numChannels == 4) {
         this.bitsPerPixel = 32;
      } else if (colorSpaceType.equals("GRAY")) {
         this.bitsPerPixel = 8;
      } else if (colorSpaceType.equals("RGB")) {
         this.bitsPerPixel = (short)(hasAlpha ? 32 : 24);
      }

      if (bitsPerSample != null && bitsPerSample.length == 4 || this.bitsPerPixel >= 24) {
         this.redMask = 16711680;
         this.greenMask = 65280;
         this.blueMask = 255;
      }

      if (bitsPerSample != null && bitsPerSample.length == 4 || this.bitsPerPixel > 24) {
         this.alphaMask = -16777216;
      }

      if (r != null && g != null && b != null) {
         boolean isVersion2 = this.bmpVersion != null && this.bmpVersion.equals("BMP v. 2.x");
         this.paletteSize = maxIndex + 1;
         this.palette = new byte[(isVersion2 ? 3 : 4) * this.paletteSize];
         int i = 0;

         for(int j = 0; i < this.paletteSize; ++i) {
            this.palette[j++] = b[i];
            this.palette[j++] = g[i];
            this.palette[j++] = r[i];
            if (!isVersion2) {
               ++j;
            }
         }
      }

   }

   public void reset() {
      this.bmpVersion = null;
      this.width = 0;
      this.height = 0;
      this.bitsPerPixel = 0;
      this.compression = 0;
      this.imageSize = 0;
      this.xPixelsPerMeter = 0;
      this.yPixelsPerMeter = 0;
      this.colorsUsed = 0;
      this.colorsImportant = 0;
      this.redMask = 0;
      this.greenMask = 0;
      this.blueMask = 0;
      this.alphaMask = 0;
      this.colorSpace = 0;
      this.redX = 0.0;
      this.redY = 0.0;
      this.redZ = 0.0;
      this.greenX = 0.0;
      this.greenY = 0.0;
      this.greenZ = 0.0;
      this.blueX = 0.0;
      this.blueY = 0.0;
      this.blueZ = 0.0;
      this.gammaRed = 0;
      this.gammaGreen = 0;
      this.gammaBlue = 0;
      this.intent = 0;
      this.palette = null;
      this.paletteSize = 0;
      this.red = 0;
      this.green = 0;
      this.blue = 0;
      this.comments = null;
   }

   private String countBits(int num) {
      int count;
      for(count = 0; num != 0; num >>>= 1) {
         if ((num & 1) == 1) {
            ++count;
         }
      }

      return count == 0 ? "0" : "" + count;
   }

   private void addXYZPoints(IIOMetadataNode root, String name, double x, double y, double z) {
      IIOMetadataNode node = this.addChildNode(root, name, (Object)null);
      this.addChildNode(node, "X", new Double(x));
      this.addChildNode(node, "Y", new Double(y));
      this.addChildNode(node, "Z", new Double(z));
   }

   private IIOMetadataNode addChildNode(IIOMetadataNode root, String name, Object object) {
      IIOMetadataNode child = new IIOMetadataNode(name);
      if (object != null) {
         child.setUserObject(object);
         child.setNodeValue(ImageUtil.convertObjectToString(object));
      }

      root.appendChild(child);
      return child;
   }

   private Object getObjectValue(Node node) {
      Object tmp = node.getNodeValue();
      if (tmp == null && node instanceof IIOMetadataNode) {
         tmp = ((IIOMetadataNode)node).getUserObject();
      }

      return tmp;
   }

   private String getStringValue(Node node) {
      Object tmp = this.getObjectValue(node);
      return tmp instanceof String ? (String)tmp : null;
   }

   private Byte getByteValue(Node node) {
      Object tmp = this.getObjectValue(node);
      Byte value = null;
      if (tmp instanceof String) {
         value = Byte.valueOf((String)tmp);
      } else if (tmp instanceof Byte) {
         value = (Byte)tmp;
      }

      return value;
   }

   private Short getShortValue(Node node) {
      Object tmp = this.getObjectValue(node);
      Short value = null;
      if (tmp instanceof String) {
         value = Short.valueOf((String)tmp);
      } else if (tmp instanceof Short) {
         value = (Short)tmp;
      }

      return value;
   }

   private Integer getIntegerValue(Node node) {
      Object tmp = this.getObjectValue(node);
      Integer value = null;
      if (tmp instanceof String) {
         value = Integer.valueOf((String)tmp);
      } else if (tmp instanceof Integer) {
         value = (Integer)tmp;
      } else if (tmp instanceof Byte) {
         value = new Integer((Byte)tmp & 255);
      }

      return value;
   }

   private Double getDoubleValue(Node node) {
      Object tmp = this.getObjectValue(node);
      Double value = null;
      if (tmp instanceof String) {
         value = Double.valueOf((String)tmp);
      } else if (tmp instanceof Double) {
         value = (Double)tmp;
      }

      return value;
   }
}
