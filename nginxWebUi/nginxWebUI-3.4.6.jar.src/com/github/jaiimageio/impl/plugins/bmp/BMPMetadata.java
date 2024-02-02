/*      */ package com.github.jaiimageio.impl.plugins.bmp;
/*      */ 
/*      */ import com.github.jaiimageio.impl.common.ImageUtil;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.DirectColorModel;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.StringTokenizer;
/*      */ import javax.imageio.ImageWriteParam;
/*      */ import javax.imageio.metadata.IIOInvalidTreeException;
/*      */ import javax.imageio.metadata.IIOMetadata;
/*      */ import javax.imageio.metadata.IIOMetadataNode;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BMPMetadata
/*      */   extends IIOMetadata
/*      */   implements Cloneable, BMPConstants
/*      */ {
/*      */   public static final String nativeMetadataFormatName = "com_sun_media_imageio_plugins_bmp_image_1.0";
/*      */   public String bmpVersion;
/*      */   public int width;
/*      */   public int height;
/*      */   public short bitsPerPixel;
/*      */   public int compression;
/*      */   public int imageSize;
/*      */   public int xPixelsPerMeter;
/*      */   public int yPixelsPerMeter;
/*      */   public int colorsUsed;
/*      */   public int colorsImportant;
/*      */   public int redMask;
/*      */   public int greenMask;
/*      */   public int blueMask;
/*      */   public int alphaMask;
/*      */   public int colorSpace;
/*      */   public double redX;
/*      */   public double redY;
/*      */   public double redZ;
/*      */   public double greenX;
/*      */   public double greenY;
/*      */   public double greenZ;
/*      */   public double blueX;
/*      */   public double blueY;
/*      */   public double blueZ;
/*      */   public int gammaRed;
/*      */   public int gammaGreen;
/*      */   public int gammaBlue;
/*      */   public int intent;
/*  115 */   public byte[] palette = null;
/*      */   
/*      */   public int paletteSize;
/*      */   
/*      */   public int red;
/*      */   
/*      */   public int green;
/*      */   public int blue;
/*  123 */   public List comments = null;
/*      */   
/*      */   public BMPMetadata() {
/*  126 */     super(true, "com_sun_media_imageio_plugins_bmp_image_1.0", "com.github.jaiimageio.impl.bmp.BMPMetadataFormat", (String[])null, (String[])null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BMPMetadata(IIOMetadata metadata) throws IIOInvalidTreeException {
/*  135 */     this();
/*      */     
/*  137 */     if (metadata != null) {
/*  138 */       List<String> formats = Arrays.asList(metadata.getMetadataFormatNames());
/*      */       
/*  140 */       if (formats.contains("com_sun_media_imageio_plugins_bmp_image_1.0")) {
/*      */         
/*  142 */         setFromTree("com_sun_media_imageio_plugins_bmp_image_1.0", metadata
/*  143 */             .getAsTree("com_sun_media_imageio_plugins_bmp_image_1.0"));
/*  144 */       } else if (metadata.isStandardMetadataFormatSupported()) {
/*      */         
/*  146 */         String format = "javax_imageio_1.0";
/*      */         
/*  148 */         setFromTree(format, metadata.getAsTree(format));
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean isReadOnly() {
/*  154 */     return false;
/*      */   }
/*      */   
/*      */   public Object clone() {
/*      */     BMPMetadata metadata;
/*      */     try {
/*  160 */       metadata = (BMPMetadata)super.clone();
/*  161 */     } catch (CloneNotSupportedException e) {
/*  162 */       return null;
/*      */     } 
/*      */     
/*  165 */     return metadata;
/*      */   }
/*      */   
/*      */   public Node getAsTree(String formatName) {
/*  169 */     if (formatName.equals("com_sun_media_imageio_plugins_bmp_image_1.0"))
/*  170 */       return getNativeTree(); 
/*  171 */     if (formatName
/*  172 */       .equals("javax_imageio_1.0")) {
/*  173 */       return getStandardTree();
/*      */     }
/*  175 */     throw new IllegalArgumentException(I18N.getString("BMPMetadata0"));
/*      */   }
/*      */ 
/*      */   
/*      */   private Node getNativeTree() {
/*  180 */     IIOMetadataNode root = new IIOMetadataNode("com_sun_media_imageio_plugins_bmp_image_1.0");
/*      */ 
/*      */     
/*  183 */     addChildNode(root, "BMPVersion", this.bmpVersion);
/*  184 */     addChildNode(root, "Width", new Integer(this.width));
/*  185 */     addChildNode(root, "Height", new Integer(this.height));
/*  186 */     addChildNode(root, "BitsPerPixel", new Short(this.bitsPerPixel));
/*  187 */     addChildNode(root, "Compression", new Integer(this.compression));
/*  188 */     addChildNode(root, "ImageSize", new Integer(this.imageSize));
/*      */ 
/*      */     
/*  191 */     if (this.xPixelsPerMeter > 0 && this.yPixelsPerMeter > 0) {
/*  192 */       IIOMetadataNode node = addChildNode(root, "PixelsPerMeter", (Object)null);
/*  193 */       addChildNode(node, "X", new Integer(this.xPixelsPerMeter));
/*  194 */       addChildNode(node, "Y", new Integer(this.yPixelsPerMeter));
/*      */     } 
/*      */     
/*  197 */     addChildNode(root, "ColorsUsed", new Integer(this.colorsUsed));
/*  198 */     addChildNode(root, "ColorsImportant", new Integer(this.colorsImportant));
/*      */     
/*  200 */     int version = 0;
/*  201 */     for (int i = 0; i < this.bmpVersion.length(); i++) {
/*  202 */       if (Character.isDigit(this.bmpVersion.charAt(i)))
/*  203 */         version = this.bmpVersion.charAt(i) - 48; 
/*      */     } 
/*  205 */     if (version >= 4) {
/*  206 */       IIOMetadataNode node = addChildNode(root, "Mask", (Object)null);
/*  207 */       addChildNode(node, "Red", new Integer(this.redMask));
/*  208 */       addChildNode(node, "Green", new Integer(this.greenMask));
/*  209 */       addChildNode(node, "Blue", new Integer(this.blueMask));
/*  210 */       addChildNode(node, "Alpha", new Integer(this.alphaMask));
/*      */       
/*  212 */       addChildNode(root, "ColorSpaceType", new Integer(this.colorSpace));
/*      */       
/*  214 */       node = addChildNode(root, "CIEXYZEndpoints", (Object)null);
/*  215 */       addXYZPoints(node, "Red", this.redX, this.redY, this.redZ);
/*  216 */       addXYZPoints(node, "Green", this.greenX, this.greenY, this.greenZ);
/*  217 */       addXYZPoints(node, "Blue", this.blueX, this.blueY, this.blueZ);
/*      */       
/*  219 */       node = addChildNode(root, "Gamma", (Object)null);
/*  220 */       addChildNode(node, "Red", new Integer(this.gammaRed));
/*  221 */       addChildNode(node, "Green", new Integer(this.gammaGreen));
/*  222 */       addChildNode(node, "Blue", new Integer(this.gammaBlue));
/*      */       
/*  224 */       node = addChildNode(root, "Intent", new Integer(this.intent));
/*      */     } 
/*      */ 
/*      */     
/*  228 */     if (this.palette != null && this.paletteSize > 0) {
/*  229 */       IIOMetadataNode node = addChildNode(root, "Palette", (Object)null);
/*      */       
/*  231 */       boolean isVersion2 = (this.bmpVersion != null && this.bmpVersion.equals("BMP v. 2.x"));
/*      */       
/*  233 */       for (int k = 0, j = 0; k < this.paletteSize; k++) {
/*      */         
/*  235 */         IIOMetadataNode entry = addChildNode(node, "PaletteEntry", (Object)null);
/*  236 */         this.blue = this.palette[j++] & 0xFF;
/*  237 */         this.green = this.palette[j++] & 0xFF;
/*  238 */         this.red = this.palette[j++] & 0xFF;
/*  239 */         addChildNode(entry, "Red", new Integer(this.red));
/*  240 */         addChildNode(entry, "Green", new Integer(this.green));
/*  241 */         addChildNode(entry, "Blue", new Integer(this.blue));
/*  242 */         if (!isVersion2) j++;
/*      */       
/*      */       } 
/*      */     } 
/*  246 */     return root;
/*      */   }
/*      */ 
/*      */   
/*      */   protected IIOMetadataNode getStandardChromaNode() {
/*      */     String colorSpaceType, numChannels;
/*  252 */     IIOMetadataNode node = new IIOMetadataNode("Chroma");
/*      */     
/*  254 */     IIOMetadataNode subNode = new IIOMetadataNode("ColorSpaceType");
/*      */     
/*  256 */     if ((this.palette != null && this.paletteSize > 0) || this.redMask != 0 || this.greenMask != 0 || this.blueMask != 0 || this.bitsPerPixel > 8) {
/*      */ 
/*      */       
/*  259 */       colorSpaceType = "RGB";
/*      */     } else {
/*  261 */       colorSpaceType = "GRAY";
/*      */     } 
/*  263 */     subNode.setAttribute("name", colorSpaceType);
/*  264 */     node.appendChild(subNode);
/*      */     
/*  266 */     subNode = new IIOMetadataNode("NumChannels");
/*      */     
/*  268 */     if ((this.palette != null && this.paletteSize > 0) || this.redMask != 0 || this.greenMask != 0 || this.blueMask != 0 || this.bitsPerPixel > 8) {
/*      */ 
/*      */       
/*  271 */       if (this.alphaMask != 0) {
/*  272 */         numChannels = "4";
/*      */       } else {
/*  274 */         numChannels = "3";
/*      */       } 
/*      */     } else {
/*  277 */       numChannels = "1";
/*      */     } 
/*  279 */     subNode.setAttribute("value", numChannels);
/*  280 */     node.appendChild(subNode);
/*      */     
/*  282 */     if (this.gammaRed != 0 && this.gammaGreen != 0 && this.gammaBlue != 0) {
/*  283 */       subNode = new IIOMetadataNode("Gamma");
/*  284 */       Double gamma = new Double((this.gammaRed + this.gammaGreen + this.gammaBlue) / 3.0D);
/*  285 */       subNode.setAttribute("value", gamma.toString());
/*  286 */       node.appendChild(subNode);
/*      */     } 
/*      */     
/*  289 */     if (numChannels.equals("1") && (this.palette == null || this.paletteSize == 0)) {
/*      */       
/*  291 */       subNode = new IIOMetadataNode("BlackIsZero");
/*  292 */       subNode.setAttribute("value", "TRUE");
/*  293 */       node.appendChild(subNode);
/*      */     } 
/*      */     
/*  296 */     if (this.palette != null && this.paletteSize > 0) {
/*  297 */       subNode = new IIOMetadataNode("Palette");
/*      */       
/*  299 */       boolean isVersion2 = (this.bmpVersion != null && this.bmpVersion.equals("BMP v. 2.x"));
/*      */       
/*  301 */       for (int i = 0, j = 0; i < this.paletteSize; i++) {
/*  302 */         IIOMetadataNode subNode1 = new IIOMetadataNode("PaletteEntry");
/*      */         
/*  304 */         subNode1.setAttribute("index", "" + i);
/*  305 */         subNode1.setAttribute("blue", "" + (this.palette[j++] & 0xFF));
/*  306 */         subNode1.setAttribute("green", "" + (this.palette[j++] & 0xFF));
/*  307 */         subNode1.setAttribute("red", "" + (this.palette[j++] & 0xFF));
/*  308 */         if (!isVersion2) j++; 
/*  309 */         subNode.appendChild(subNode1);
/*      */       } 
/*  311 */       node.appendChild(subNode);
/*      */     } 
/*      */     
/*  314 */     return node;
/*      */   }
/*      */   
/*      */   protected IIOMetadataNode getStandardCompressionNode() {
/*  318 */     IIOMetadataNode node = new IIOMetadataNode("Compression");
/*      */ 
/*      */     
/*  321 */     IIOMetadataNode subNode = new IIOMetadataNode("CompressionTypeName");
/*  322 */     subNode.setAttribute("value", compressionTypeNames[this.compression]);
/*  323 */     node.appendChild(subNode);
/*      */     
/*  325 */     subNode = new IIOMetadataNode("Lossless");
/*  326 */     subNode.setAttribute("value", (this.compression == 4) ? "FALSE" : "TRUE");
/*      */     
/*  328 */     node.appendChild(subNode);
/*      */     
/*  330 */     return node;
/*      */   }
/*      */   
/*      */   protected IIOMetadataNode getStandardDataNode() {
/*  334 */     IIOMetadataNode node = new IIOMetadataNode("Data");
/*      */     
/*  336 */     String sampleFormat = (this.palette != null && this.paletteSize > 0) ? "Index" : "UnsignedIntegral";
/*      */     
/*  338 */     IIOMetadataNode subNode = new IIOMetadataNode("SampleFormat");
/*  339 */     subNode.setAttribute("value", sampleFormat);
/*  340 */     node.appendChild(subNode);
/*      */     
/*  342 */     String bits = "";
/*  343 */     if (this.redMask != 0 || this.greenMask != 0 || this.blueMask != 0) {
/*      */ 
/*      */ 
/*      */       
/*  347 */       bits = countBits(this.redMask) + " " + countBits(this.greenMask) + " " + countBits(this.blueMask);
/*  348 */       if (this.alphaMask != 0) {
/*  349 */         bits = bits + " " + countBits(this.alphaMask);
/*      */       }
/*  351 */     } else if (this.palette != null && this.paletteSize > 0) {
/*  352 */       for (int i = 1; i <= 3; i++) {
/*  353 */         bits = bits + this.bitsPerPixel;
/*  354 */         if (i != 3) {
/*  355 */           bits = bits + " ";
/*      */         }
/*      */       }
/*      */     
/*  359 */     } else if (this.bitsPerPixel == 1) {
/*  360 */       bits = "1";
/*  361 */     } else if (this.bitsPerPixel == 4) {
/*  362 */       bits = "4";
/*  363 */     } else if (this.bitsPerPixel == 8) {
/*  364 */       bits = "8";
/*  365 */     } else if (this.bitsPerPixel == 16) {
/*  366 */       bits = "5 6 5";
/*  367 */     } else if (this.bitsPerPixel == 24) {
/*  368 */       bits = "8 8 8";
/*  369 */     } else if (this.bitsPerPixel == 32) {
/*  370 */       bits = "8 8 8 8";
/*      */     } 
/*      */ 
/*      */     
/*  374 */     if (!bits.equals("")) {
/*  375 */       subNode = new IIOMetadataNode("BitsPerSample");
/*  376 */       subNode.setAttribute("value", bits);
/*  377 */       node.appendChild(subNode);
/*      */     } 
/*      */     
/*  380 */     return node;
/*      */   }
/*      */   
/*      */   protected IIOMetadataNode getStandardDimensionNode() {
/*  384 */     if (this.yPixelsPerMeter > 0 && this.xPixelsPerMeter > 0) {
/*  385 */       IIOMetadataNode node = new IIOMetadataNode("Dimension");
/*  386 */       float ratio = this.yPixelsPerMeter / this.xPixelsPerMeter;
/*  387 */       IIOMetadataNode subNode = new IIOMetadataNode("PixelAspectRatio");
/*  388 */       subNode.setAttribute("value", "" + ratio);
/*  389 */       node.appendChild(subNode);
/*      */       
/*  391 */       subNode = new IIOMetadataNode("HorizontalPixelSize");
/*  392 */       subNode.setAttribute("value", "" + (1000.0F / this.xPixelsPerMeter));
/*  393 */       node.appendChild(subNode);
/*      */       
/*  395 */       subNode = new IIOMetadataNode("VerticalPixelSize");
/*  396 */       subNode.setAttribute("value", "" + (1000.0F / this.yPixelsPerMeter));
/*  397 */       node.appendChild(subNode);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  403 */       subNode = new IIOMetadataNode("HorizontalPhysicalPixelSpacing");
/*  404 */       subNode.setAttribute("value", "" + (1000.0F / this.xPixelsPerMeter));
/*  405 */       node.appendChild(subNode);
/*      */       
/*  407 */       subNode = new IIOMetadataNode("VerticalPhysicalPixelSpacing");
/*  408 */       subNode.setAttribute("value", "" + (1000.0F / this.yPixelsPerMeter));
/*  409 */       node.appendChild(subNode);
/*      */       
/*  411 */       return node;
/*      */     } 
/*  413 */     return null;
/*      */   }
/*      */   
/*      */   protected IIOMetadataNode getStandardDocumentNode() {
/*  417 */     if (this.bmpVersion != null) {
/*  418 */       IIOMetadataNode node = new IIOMetadataNode("Document");
/*  419 */       IIOMetadataNode subNode = new IIOMetadataNode("FormatVersion");
/*  420 */       subNode.setAttribute("value", this.bmpVersion);
/*  421 */       node.appendChild(subNode);
/*  422 */       return node;
/*      */     } 
/*  424 */     return null;
/*      */   }
/*      */   
/*      */   protected IIOMetadataNode getStandardTextNode() {
/*  428 */     if (this.comments != null) {
/*  429 */       IIOMetadataNode node = new IIOMetadataNode("Text");
/*  430 */       Iterator<String> iter = this.comments.iterator();
/*  431 */       while (iter.hasNext()) {
/*  432 */         String comment = iter.next();
/*  433 */         IIOMetadataNode subNode = new IIOMetadataNode("TextEntry");
/*  434 */         subNode.setAttribute("keyword", "comment");
/*  435 */         subNode.setAttribute("value", comment);
/*  436 */         node.appendChild(subNode);
/*      */       } 
/*  438 */       return node;
/*      */     } 
/*  440 */     return null;
/*      */   }
/*      */   protected IIOMetadataNode getStandardTransparencyNode() {
/*      */     String alpha;
/*  444 */     IIOMetadataNode node = new IIOMetadataNode("Transparency");
/*  445 */     IIOMetadataNode subNode = new IIOMetadataNode("Alpha");
/*      */     
/*  447 */     if (this.alphaMask != 0) {
/*  448 */       alpha = "nonpremultiplied";
/*      */     } else {
/*  450 */       alpha = "none";
/*      */     } 
/*      */     
/*  453 */     subNode.setAttribute("value", alpha);
/*  454 */     node.appendChild(subNode);
/*  455 */     return node;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void fatal(Node node, String reason) throws IIOInvalidTreeException {
/*  461 */     throw new IIOInvalidTreeException(reason, node);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int getIntAttribute(Node node, String name, int defaultValue, boolean required) throws IIOInvalidTreeException {
/*  468 */     String value = getAttribute(node, name, (String)null, required);
/*  469 */     if (value == null) {
/*  470 */       return defaultValue;
/*      */     }
/*  472 */     return Integer.parseInt(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double getDoubleAttribute(Node node, String name, double defaultValue, boolean required) throws IIOInvalidTreeException {
/*  479 */     String value = getAttribute(node, name, (String)null, required);
/*  480 */     if (value == null) {
/*  481 */       return defaultValue;
/*      */     }
/*  483 */     return Double.parseDouble(value);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private int getIntAttribute(Node node, String name) throws IIOInvalidTreeException {
/*  489 */     return getIntAttribute(node, name, -1, true);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private double getDoubleAttribute(Node node, String name) throws IIOInvalidTreeException {
/*  495 */     return getDoubleAttribute(node, name, -1.0D, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String getAttribute(Node node, String name, String defaultValue, boolean required) throws IIOInvalidTreeException {
/*  502 */     Node attr = node.getAttributes().getNamedItem(name);
/*  503 */     if (attr == null) {
/*  504 */       if (!required) {
/*  505 */         return defaultValue;
/*      */       }
/*  507 */       fatal(node, "Required attribute " + name + " not present!");
/*      */     } 
/*      */     
/*  510 */     return attr.getNodeValue();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private String getAttribute(Node node, String name) throws IIOInvalidTreeException {
/*  516 */     return getAttribute(node, name, (String)null, true);
/*      */   }
/*      */ 
/*      */   
/*      */   void initialize(ColorModel cm, SampleModel sm, ImageWriteParam param) {
/*  521 */     if (param != null) {
/*  522 */       this.bmpVersion = "BMP v. 3.x";
/*      */       
/*  524 */       if (param.getCompressionMode() == 2) {
/*  525 */         String compressionType = param.getCompressionType();
/*  526 */         this
/*  527 */           .compression = BMPImageWriter.getCompressionType(compressionType);
/*      */       } 
/*      */     } else {
/*  530 */       this.bmpVersion = "BMP v. 3.x";
/*  531 */       this.compression = BMPImageWriter.getPreferredCompressionType(cm, sm);
/*      */     } 
/*      */ 
/*      */     
/*  535 */     this.width = sm.getWidth();
/*  536 */     this.height = sm.getHeight();
/*      */ 
/*      */     
/*  539 */     this.bitsPerPixel = (short)cm.getPixelSize();
/*      */ 
/*      */     
/*  542 */     if (cm instanceof DirectColorModel) {
/*  543 */       DirectColorModel dcm = (DirectColorModel)cm;
/*  544 */       this.redMask = dcm.getRedMask();
/*  545 */       this.greenMask = dcm.getGreenMask();
/*  546 */       this.blueMask = dcm.getBlueMask();
/*  547 */       this.alphaMask = dcm.getAlphaMask();
/*      */     } 
/*      */ 
/*      */     
/*  551 */     if (cm instanceof IndexColorModel) {
/*  552 */       IndexColorModel icm = (IndexColorModel)cm;
/*  553 */       this.paletteSize = icm.getMapSize();
/*      */       
/*  555 */       byte[] r = new byte[this.paletteSize];
/*  556 */       byte[] g = new byte[this.paletteSize];
/*  557 */       byte[] b = new byte[this.paletteSize];
/*      */       
/*  559 */       icm.getReds(r);
/*  560 */       icm.getGreens(g);
/*  561 */       icm.getBlues(b);
/*      */ 
/*      */       
/*  564 */       boolean isVersion2 = (this.bmpVersion != null && this.bmpVersion.equals("BMP v. 2.x"));
/*      */       
/*  566 */       this.palette = new byte[(isVersion2 ? 3 : 4) * this.paletteSize];
/*  567 */       for (int i = 0, j = 0; i < this.paletteSize; i++) {
/*  568 */         this.palette[j++] = b[i];
/*  569 */         this.palette[j++] = g[i];
/*  570 */         this.palette[j++] = r[i];
/*  571 */         if (!isVersion2) j++;
/*      */       
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
/*  578 */     if (formatName.equals("com_sun_media_imageio_plugins_bmp_image_1.0")) {
/*  579 */       if (root == null) {
/*  580 */         throw new IllegalArgumentException("root == null!");
/*      */       }
/*  582 */       mergeNativeTree(root);
/*  583 */     } else if (formatName
/*  584 */       .equals("javax_imageio_1.0")) {
/*  585 */       if (root == null) {
/*  586 */         throw new IllegalArgumentException("root == null!");
/*      */       }
/*  588 */       mergeStandardTree(root);
/*      */     } else {
/*  590 */       throw new IllegalArgumentException("Not a recognized format!");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void mergeNativeTree(Node root) throws IIOInvalidTreeException {
/*  596 */     Node node = root;
/*  597 */     if (!node.getNodeName().equals("com_sun_media_imageio_plugins_bmp_image_1.0")) {
/*  598 */       fatal(node, "Root must be com_sun_media_imageio_plugins_bmp_image_1.0");
/*      */     }
/*      */     
/*  601 */     byte[] r = null, g = null, b = null;
/*  602 */     int maxIndex = -1;
/*      */     
/*  604 */     node = node.getFirstChild();
/*  605 */     while (node != null) {
/*  606 */       String name = node.getNodeName();
/*      */       
/*  608 */       if (name.equals("BMPVersion")) {
/*  609 */         String value = getStringValue(node);
/*  610 */         if (value != null) this.bmpVersion = value; 
/*  611 */       } else if (name.equals("Width")) {
/*  612 */         Integer value = getIntegerValue(node);
/*  613 */         if (value != null) this.width = value.intValue(); 
/*  614 */       } else if (name.equals("Height")) {
/*  615 */         Integer value = getIntegerValue(node);
/*  616 */         if (value != null) this.height = value.intValue(); 
/*  617 */       } else if (name.equals("BitsPerPixel")) {
/*  618 */         Short value = getShortValue(node);
/*  619 */         if (value != null) this.bitsPerPixel = value.shortValue(); 
/*  620 */       } else if (name.equals("Compression")) {
/*  621 */         Integer value = getIntegerValue(node);
/*  622 */         if (value != null) this.compression = value.intValue(); 
/*  623 */       } else if (name.equals("ImageSize")) {
/*  624 */         Integer value = getIntegerValue(node);
/*  625 */         if (value != null) this.imageSize = value.intValue(); 
/*  626 */       } else if (name.equals("PixelsPerMeter")) {
/*  627 */         Node subNode = node.getFirstChild();
/*  628 */         while (subNode != null) {
/*  629 */           String subName = subNode.getNodeName();
/*  630 */           if (subName.equals("X")) {
/*  631 */             Integer value = getIntegerValue(subNode);
/*  632 */             if (value != null)
/*  633 */               this.xPixelsPerMeter = value.intValue(); 
/*  634 */           } else if (subName.equals("Y")) {
/*  635 */             Integer value = getIntegerValue(subNode);
/*  636 */             if (value != null)
/*  637 */               this.yPixelsPerMeter = value.intValue(); 
/*      */           } 
/*  639 */           subNode = subNode.getNextSibling();
/*      */         } 
/*  641 */       } else if (name.equals("ColorsUsed")) {
/*  642 */         Integer value = getIntegerValue(node);
/*  643 */         if (value != null) this.colorsUsed = value.intValue(); 
/*  644 */       } else if (name.equals("ColorsImportant")) {
/*  645 */         Integer value = getIntegerValue(node);
/*  646 */         if (value != null) this.colorsImportant = value.intValue(); 
/*  647 */       } else if (name.equals("Mask")) {
/*  648 */         Node subNode = node.getFirstChild();
/*  649 */         while (subNode != null) {
/*  650 */           String subName = subNode.getNodeName();
/*  651 */           if (subName.equals("Red")) {
/*  652 */             Integer value = getIntegerValue(subNode);
/*  653 */             if (value != null)
/*  654 */               this.redMask = value.intValue(); 
/*  655 */           } else if (subName.equals("Green")) {
/*  656 */             Integer value = getIntegerValue(subNode);
/*  657 */             if (value != null)
/*  658 */               this.greenMask = value.intValue(); 
/*  659 */           } else if (subName.equals("Blue")) {
/*  660 */             Integer value = getIntegerValue(subNode);
/*  661 */             if (value != null)
/*  662 */               this.blueMask = value.intValue(); 
/*  663 */           } else if (subName.equals("Alpha")) {
/*  664 */             Integer value = getIntegerValue(subNode);
/*  665 */             if (value != null)
/*  666 */               this.alphaMask = value.intValue(); 
/*      */           } 
/*  668 */           subNode = subNode.getNextSibling();
/*      */         } 
/*  670 */       } else if (name.equals("ColorSpace")) {
/*  671 */         Integer value = getIntegerValue(node);
/*  672 */         if (value != null) this.colorSpace = value.intValue(); 
/*  673 */       } else if (name.equals("CIEXYZEndpoints")) {
/*  674 */         Node subNode = node.getFirstChild();
/*  675 */         while (subNode != null) {
/*  676 */           String subName = subNode.getNodeName();
/*  677 */           if (subName.equals("Red")) {
/*  678 */             Node subNode1 = subNode.getFirstChild();
/*  679 */             while (subNode1 != null) {
/*  680 */               String subName1 = subNode1.getNodeName();
/*  681 */               if (subName1.equals("X")) {
/*  682 */                 Double value = getDoubleValue(subNode1);
/*  683 */                 if (value != null)
/*  684 */                   this.redX = value.doubleValue(); 
/*  685 */               } else if (subName1.equals("Y")) {
/*  686 */                 Double value = getDoubleValue(subNode1);
/*  687 */                 if (value != null)
/*  688 */                   this.redY = value.doubleValue(); 
/*  689 */               } else if (subName1.equals("Z")) {
/*  690 */                 Double value = getDoubleValue(subNode1);
/*  691 */                 if (value != null)
/*  692 */                   this.redZ = value.doubleValue(); 
/*      */               } 
/*  694 */               subNode1 = subNode1.getNextSibling();
/*      */             } 
/*  696 */           } else if (subName.equals("Green")) {
/*  697 */             Node subNode1 = subNode.getFirstChild();
/*  698 */             while (subNode1 != null) {
/*  699 */               String subName1 = subNode1.getNodeName();
/*  700 */               if (subName1.equals("X")) {
/*  701 */                 Double value = getDoubleValue(subNode1);
/*  702 */                 if (value != null)
/*  703 */                   this.greenX = value.doubleValue(); 
/*  704 */               } else if (subName1.equals("Y")) {
/*  705 */                 Double value = getDoubleValue(subNode1);
/*  706 */                 if (value != null)
/*  707 */                   this.greenY = value.doubleValue(); 
/*  708 */               } else if (subName1.equals("Z")) {
/*  709 */                 Double value = getDoubleValue(subNode1);
/*  710 */                 if (value != null)
/*  711 */                   this.greenZ = value.doubleValue(); 
/*      */               } 
/*  713 */               subNode1 = subNode1.getNextSibling();
/*      */             } 
/*  715 */           } else if (subName.equals("Blue")) {
/*  716 */             Node subNode1 = subNode.getFirstChild();
/*  717 */             while (subNode1 != null) {
/*  718 */               String subName1 = subNode1.getNodeName();
/*  719 */               if (subName1.equals("X")) {
/*  720 */                 Double value = getDoubleValue(subNode1);
/*  721 */                 if (value != null)
/*  722 */                   this.blueX = value.doubleValue(); 
/*  723 */               } else if (subName1.equals("Y")) {
/*  724 */                 Double value = getDoubleValue(subNode1);
/*  725 */                 if (value != null)
/*  726 */                   this.blueY = value.doubleValue(); 
/*  727 */               } else if (subName1.equals("Z")) {
/*  728 */                 Double value = getDoubleValue(subNode1);
/*  729 */                 if (value != null)
/*  730 */                   this.blueZ = value.doubleValue(); 
/*      */               } 
/*  732 */               subNode1 = subNode1.getNextSibling();
/*      */             } 
/*      */           } 
/*  735 */           subNode = subNode.getNextSibling();
/*      */         } 
/*  737 */       } else if (name.equals("Gamma")) {
/*  738 */         Node subNode = node.getFirstChild();
/*  739 */         while (subNode != null) {
/*  740 */           String subName = subNode.getNodeName();
/*  741 */           if (subName.equals("Red")) {
/*  742 */             Integer value = getIntegerValue(subNode);
/*  743 */             if (value != null)
/*  744 */               this.gammaRed = value.intValue(); 
/*  745 */           } else if (subName.equals("Green")) {
/*  746 */             Integer value = getIntegerValue(subNode);
/*  747 */             if (value != null)
/*  748 */               this.gammaGreen = value.intValue(); 
/*  749 */           } else if (subName.equals("Blue")) {
/*  750 */             Integer value = getIntegerValue(subNode);
/*  751 */             if (value != null)
/*  752 */               this.gammaBlue = value.intValue(); 
/*      */           } 
/*  754 */           subNode = subNode.getNextSibling();
/*      */         } 
/*  756 */       } else if (name.equals("Intent")) {
/*  757 */         Integer value = getIntegerValue(node);
/*  758 */         if (value != null) this.intent = value.intValue(); 
/*  759 */       } else if (name.equals("Palette")) {
/*  760 */         this.paletteSize = getIntAttribute(node, "sizeOfPalette");
/*      */         
/*  762 */         r = new byte[this.paletteSize];
/*  763 */         g = new byte[this.paletteSize];
/*  764 */         b = new byte[this.paletteSize];
/*  765 */         maxIndex = -1;
/*      */         
/*  767 */         Node paletteEntry = node.getFirstChild();
/*  768 */         if (paletteEntry == null) {
/*  769 */           fatal(node, "Palette has no entries!");
/*      */         }
/*      */         
/*  772 */         int numPaletteEntries = 0;
/*  773 */         while (paletteEntry != null) {
/*  774 */           if (!paletteEntry.getNodeName().equals("PaletteEntry")) {
/*  775 */             fatal(node, "Only a PaletteEntry may be a child of a Palette!");
/*      */           }
/*      */ 
/*      */           
/*  779 */           int index = -1;
/*  780 */           Node subNode = paletteEntry.getFirstChild();
/*  781 */           while (subNode != null) {
/*  782 */             String subName = subNode.getNodeName();
/*  783 */             if (subName.equals("Index")) {
/*  784 */               Integer value = getIntegerValue(subNode);
/*  785 */               if (value != null)
/*  786 */                 index = value.intValue(); 
/*  787 */               if (index < 0 || index > this.paletteSize - 1) {
/*  788 */                 fatal(node, "Bad value for PaletteEntry attribute index!");
/*      */               }
/*      */             }
/*  791 */             else if (subName.equals("Red")) {
/*  792 */               Integer value = getIntegerValue(subNode);
/*  793 */               if (value != null)
/*  794 */                 this.red = value.intValue(); 
/*  795 */             } else if (subName.equals("Green")) {
/*  796 */               Integer value = getIntegerValue(subNode);
/*  797 */               if (value != null)
/*  798 */                 this.green = value.intValue(); 
/*  799 */             } else if (subName.equals("Blue")) {
/*  800 */               Integer value = getIntegerValue(subNode);
/*  801 */               if (value != null)
/*  802 */                 this.blue = value.intValue(); 
/*      */             } 
/*  804 */             subNode = subNode.getNextSibling();
/*      */           } 
/*      */           
/*  807 */           if (index == -1) {
/*  808 */             index = numPaletteEntries;
/*      */           }
/*  810 */           if (index > maxIndex) {
/*  811 */             maxIndex = index;
/*      */           }
/*      */           
/*  814 */           r[index] = (byte)this.red;
/*  815 */           g[index] = (byte)this.green;
/*  816 */           b[index] = (byte)this.blue;
/*      */           
/*  818 */           numPaletteEntries++;
/*  819 */           paletteEntry = paletteEntry.getNextSibling();
/*      */         } 
/*  821 */       } else if (name.equals("CommentExtensions")) {
/*      */         
/*  823 */         Node commentExtension = node.getFirstChild();
/*  824 */         if (commentExtension == null) {
/*  825 */           fatal(node, "CommentExtensions has no entries!");
/*      */         }
/*      */         
/*  828 */         if (this.comments == null) {
/*  829 */           this.comments = new ArrayList();
/*      */         }
/*      */         
/*  832 */         while (commentExtension != null) {
/*  833 */           if (!commentExtension.getNodeName().equals("CommentExtension")) {
/*  834 */             fatal(node, "Only a CommentExtension may be a child of a CommentExtensions!");
/*      */           }
/*      */ 
/*      */           
/*  838 */           this.comments.add(getAttribute(commentExtension, "value"));
/*      */           
/*  840 */           commentExtension = commentExtension.getNextSibling();
/*      */         } 
/*      */       } else {
/*  843 */         fatal(node, "Unknown child of root node!");
/*      */       } 
/*      */       
/*  846 */       node = node.getNextSibling();
/*      */     } 
/*      */     
/*  849 */     if (r != null && g != null && b != null) {
/*      */       
/*  851 */       boolean isVersion2 = (this.bmpVersion != null && this.bmpVersion.equals("BMP v. 2.x"));
/*      */       
/*  853 */       int numEntries = maxIndex + 1;
/*  854 */       this.palette = new byte[(isVersion2 ? 3 : 4) * numEntries];
/*  855 */       for (int i = 0, j = 0; i < numEntries; i++) {
/*  856 */         this.palette[j++] = b[i];
/*  857 */         this.palette[j++] = g[i];
/*  858 */         this.palette[j++] = r[i];
/*  859 */         if (!isVersion2) j++;
/*      */       
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void mergeStandardTree(Node root) throws IIOInvalidTreeException {
/*  866 */     Node node = root;
/*      */     
/*  868 */     if (!node.getNodeName().equals("javax_imageio_1.0")) {
/*  869 */       fatal(node, "Root must be javax_imageio_1.0");
/*      */     }
/*      */ 
/*      */     
/*  873 */     String colorSpaceType = null;
/*  874 */     int numChannels = 0;
/*  875 */     int[] bitsPerSample = null;
/*  876 */     boolean hasAlpha = false;
/*      */     
/*  878 */     byte[] r = null, g = null, b = null;
/*  879 */     int maxIndex = -1;
/*      */     
/*  881 */     node = node.getFirstChild();
/*  882 */     while (node != null) {
/*  883 */       String name = node.getNodeName();
/*      */       
/*  885 */       if (name.equals("Chroma")) {
/*  886 */         Node child = node.getFirstChild();
/*  887 */         while (child != null) {
/*  888 */           String childName = child.getNodeName();
/*  889 */           if (childName.equals("ColorSpaceType")) {
/*  890 */             colorSpaceType = getAttribute(child, "name");
/*  891 */           } else if (childName.equals("NumChannels")) {
/*  892 */             numChannels = getIntAttribute(child, "value");
/*  893 */           } else if (childName.equals("Gamma")) {
/*  894 */             this
/*  895 */               .gammaRed = this.gammaGreen = this.gammaBlue = (int)(getDoubleAttribute(child, "value") + 0.5D);
/*  896 */           } else if (childName.equals("Palette")) {
/*  897 */             r = new byte[256];
/*  898 */             g = new byte[256];
/*  899 */             b = new byte[256];
/*  900 */             maxIndex = -1;
/*      */             
/*  902 */             Node paletteEntry = child.getFirstChild();
/*  903 */             if (paletteEntry == null) {
/*  904 */               fatal(node, "Palette has no entries!");
/*      */             }
/*      */             
/*  907 */             while (paletteEntry != null) {
/*  908 */               if (!paletteEntry.getNodeName().equals("PaletteEntry")) {
/*  909 */                 fatal(node, "Only a PaletteEntry may be a child of a Palette!");
/*      */               }
/*      */ 
/*      */               
/*  913 */               int index = getIntAttribute(paletteEntry, "index");
/*  914 */               if (index < 0 || index > 255) {
/*  915 */                 fatal(node, "Bad value for PaletteEntry attribute index!");
/*      */               }
/*      */               
/*  918 */               if (index > maxIndex) {
/*  919 */                 maxIndex = index;
/*      */               }
/*  921 */               r[index] = 
/*  922 */                 (byte)getIntAttribute(paletteEntry, "red");
/*  923 */               g[index] = 
/*  924 */                 (byte)getIntAttribute(paletteEntry, "green");
/*  925 */               b[index] = 
/*  926 */                 (byte)getIntAttribute(paletteEntry, "blue");
/*      */               
/*  928 */               paletteEntry = paletteEntry.getNextSibling();
/*      */             } 
/*      */           } 
/*      */           
/*  932 */           child = child.getNextSibling();
/*      */         } 
/*  934 */       } else if (name.equals("Compression")) {
/*  935 */         Node child = node.getFirstChild();
/*  936 */         while (child != null) {
/*  937 */           String childName = child.getNodeName();
/*  938 */           if (childName.equals("CompressionTypeName")) {
/*  939 */             String compressionName = getAttribute(child, "value");
/*  940 */             this
/*  941 */               .compression = BMPImageWriter.getCompressionType(compressionName);
/*      */           } 
/*  943 */           child = child.getNextSibling();
/*      */         } 
/*  945 */       } else if (name.equals("Data")) {
/*  946 */         Node child = node.getFirstChild();
/*  947 */         while (child != null) {
/*  948 */           String childName = child.getNodeName();
/*  949 */           if (childName.equals("BitsPerSample")) {
/*  950 */             List<Integer> bps = new ArrayList(4);
/*  951 */             String s = getAttribute(child, "value");
/*  952 */             StringTokenizer t = new StringTokenizer(s);
/*  953 */             while (t.hasMoreTokens()) {
/*  954 */               bps.add(Integer.valueOf(t.nextToken()));
/*      */             }
/*  956 */             bitsPerSample = new int[bps.size()];
/*  957 */             for (int i = 0; i < bitsPerSample.length; i++) {
/*  958 */               bitsPerSample[i] = ((Integer)bps
/*  959 */                 .get(i)).intValue();
/*      */             }
/*      */             break;
/*      */           } 
/*  963 */           child = child.getNextSibling();
/*      */         } 
/*  965 */       } else if (name.equals("Dimension")) {
/*  966 */         boolean gotWidth = false;
/*  967 */         boolean gotHeight = false;
/*  968 */         boolean gotAspectRatio = false;
/*  969 */         boolean gotSpaceX = false;
/*  970 */         boolean gotSpaceY = false;
/*      */         
/*  972 */         double width = -1.0D;
/*  973 */         double height = -1.0D;
/*  974 */         double aspectRatio = -1.0D;
/*  975 */         double spaceX = -1.0D;
/*  976 */         double spaceY = -1.0D;
/*      */         
/*  978 */         Node child = node.getFirstChild();
/*  979 */         while (child != null) {
/*  980 */           String childName = child.getNodeName();
/*  981 */           if (childName.equals("PixelAspectRatio")) {
/*  982 */             aspectRatio = getDoubleAttribute(child, "value");
/*  983 */             gotAspectRatio = true;
/*  984 */           } else if (childName.equals("HorizontalPixelSize")) {
/*  985 */             width = getDoubleAttribute(child, "value");
/*  986 */             gotWidth = true;
/*  987 */           } else if (childName.equals("VerticalPixelSize")) {
/*  988 */             height = getDoubleAttribute(child, "value");
/*  989 */             gotHeight = true;
/*  990 */           } else if (childName.equals("HorizontalPhysicalPixelSpacing")) {
/*  991 */             spaceX = getDoubleAttribute(child, "value");
/*  992 */             gotSpaceX = true;
/*  993 */           } else if (childName.equals("VerticalPhysicalPixelSpacing")) {
/*  994 */             spaceY = getDoubleAttribute(child, "value");
/*  995 */             gotSpaceY = true;
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1002 */           child = child.getNextSibling();
/*      */         } 
/*      */ 
/*      */         
/* 1006 */         if (!gotWidth && !gotHeight && (gotSpaceX || gotSpaceY)) {
/* 1007 */           width = spaceX;
/* 1008 */           gotWidth = gotSpaceX;
/* 1009 */           height = spaceY;
/* 1010 */           gotHeight = gotSpaceY;
/*      */         } 
/*      */ 
/*      */         
/* 1014 */         if (gotWidth && gotHeight) {
/* 1015 */           this.xPixelsPerMeter = (int)(1000.0D / width + 0.5D);
/* 1016 */           this.yPixelsPerMeter = (int)(1000.0D / height + 0.5D);
/* 1017 */         } else if (gotAspectRatio && aspectRatio != 0.0D) {
/* 1018 */           if (gotWidth) {
/* 1019 */             this.xPixelsPerMeter = (int)(1000.0D / width + 0.5D);
/* 1020 */             this.yPixelsPerMeter = (int)(aspectRatio * 1000.0D / width + 0.5D);
/*      */           }
/* 1022 */           else if (gotHeight) {
/* 1023 */             this.xPixelsPerMeter = (int)(1000.0D / height / aspectRatio + 0.5D);
/*      */             
/* 1025 */             this.yPixelsPerMeter = (int)(1000.0D / height + 0.5D);
/*      */           } 
/*      */         } 
/* 1028 */       } else if (name.equals("Document")) {
/* 1029 */         Node child = node.getFirstChild();
/* 1030 */         while (child != null) {
/* 1031 */           String childName = child.getNodeName();
/* 1032 */           if (childName.equals("FormatVersion")) {
/* 1033 */             this.bmpVersion = getAttribute(child, "value");
/*      */             break;
/*      */           } 
/* 1036 */           child = child.getNextSibling();
/*      */         } 
/* 1038 */       } else if (name.equals("Text")) {
/* 1039 */         Node child = node.getFirstChild();
/* 1040 */         while (child != null) {
/* 1041 */           String childName = child.getNodeName();
/* 1042 */           if (childName.equals("TextEntry")) {
/* 1043 */             if (this.comments == null) {
/* 1044 */               this.comments = new ArrayList();
/*      */             }
/* 1046 */             this.comments.add(getAttribute(child, "value"));
/*      */           } 
/* 1048 */           child = child.getNextSibling();
/*      */         } 
/* 1050 */       } else if (name.equals("Transparency")) {
/* 1051 */         Node child = node.getFirstChild();
/* 1052 */         while (child != null) {
/* 1053 */           String childName = child.getNodeName();
/* 1054 */           if (childName.equals("Alpha")) {
/*      */             
/* 1056 */             hasAlpha = !getAttribute(child, "value").equals("none");
/*      */             break;
/*      */           } 
/* 1059 */           child = child.getNextSibling();
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1065 */       node = node.getNextSibling();
/*      */     } 
/*      */ 
/*      */     
/* 1069 */     if (bitsPerSample != null) {
/* 1070 */       if (this.palette != null && this.paletteSize > 0) {
/* 1071 */         this.bitsPerPixel = (short)bitsPerSample[0];
/*      */       } else {
/* 1073 */         this.bitsPerPixel = 0;
/* 1074 */         for (int i = 0; i < bitsPerSample.length; i++) {
/* 1075 */           this.bitsPerPixel = (short)(this.bitsPerPixel + bitsPerSample[i]);
/*      */         }
/*      */       } 
/* 1078 */     } else if (this.palette != null) {
/* 1079 */       this.bitsPerPixel = 8;
/* 1080 */     } else if (numChannels == 1) {
/* 1081 */       this.bitsPerPixel = 8;
/* 1082 */     } else if (numChannels == 3) {
/* 1083 */       this.bitsPerPixel = 24;
/* 1084 */     } else if (numChannels == 4) {
/* 1085 */       this.bitsPerPixel = 32;
/* 1086 */     } else if (colorSpaceType.equals("GRAY")) {
/* 1087 */       this.bitsPerPixel = 8;
/* 1088 */     } else if (colorSpaceType.equals("RGB")) {
/* 1089 */       this.bitsPerPixel = (short)(hasAlpha ? 32 : 24);
/*      */     } 
/*      */ 
/*      */     
/* 1093 */     if ((bitsPerSample != null && bitsPerSample.length == 4) || this.bitsPerPixel >= 24) {
/*      */       
/* 1095 */       this.redMask = 16711680;
/* 1096 */       this.greenMask = 65280;
/* 1097 */       this.blueMask = 255;
/*      */     } 
/*      */ 
/*      */     
/* 1101 */     if ((bitsPerSample != null && bitsPerSample.length == 4) || this.bitsPerPixel > 24)
/*      */     {
/* 1103 */       this.alphaMask = -16777216;
/*      */     }
/*      */ 
/*      */     
/* 1107 */     if (r != null && g != null && b != null) {
/*      */       
/* 1109 */       boolean isVersion2 = (this.bmpVersion != null && this.bmpVersion.equals("BMP v. 2.x"));
/*      */       
/* 1111 */       this.paletteSize = maxIndex + 1;
/* 1112 */       this.palette = new byte[(isVersion2 ? 3 : 4) * this.paletteSize];
/* 1113 */       for (int i = 0, j = 0; i < this.paletteSize; i++) {
/* 1114 */         this.palette[j++] = b[i];
/* 1115 */         this.palette[j++] = g[i];
/* 1116 */         this.palette[j++] = r[i];
/* 1117 */         if (!isVersion2) j++;
/*      */       
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void reset() {
/* 1124 */     this.bmpVersion = null;
/* 1125 */     this.width = 0;
/* 1126 */     this.height = 0;
/* 1127 */     this.bitsPerPixel = 0;
/* 1128 */     this.compression = 0;
/* 1129 */     this.imageSize = 0;
/*      */ 
/*      */     
/* 1132 */     this.xPixelsPerMeter = 0;
/* 1133 */     this.yPixelsPerMeter = 0;
/*      */     
/* 1135 */     this.colorsUsed = 0;
/* 1136 */     this.colorsImportant = 0;
/*      */ 
/*      */     
/* 1139 */     this.redMask = 0;
/* 1140 */     this.greenMask = 0;
/* 1141 */     this.blueMask = 0;
/* 1142 */     this.alphaMask = 0;
/*      */     
/* 1144 */     this.colorSpace = 0;
/*      */ 
/*      */     
/* 1147 */     this.redX = 0.0D;
/* 1148 */     this.redY = 0.0D;
/* 1149 */     this.redZ = 0.0D;
/* 1150 */     this.greenX = 0.0D;
/* 1151 */     this.greenY = 0.0D;
/* 1152 */     this.greenZ = 0.0D;
/* 1153 */     this.blueX = 0.0D;
/* 1154 */     this.blueY = 0.0D;
/* 1155 */     this.blueZ = 0.0D;
/*      */ 
/*      */     
/* 1158 */     this.gammaRed = 0;
/* 1159 */     this.gammaGreen = 0;
/* 1160 */     this.gammaBlue = 0;
/*      */     
/* 1162 */     this.intent = 0;
/*      */ 
/*      */     
/* 1165 */     this.palette = null;
/* 1166 */     this.paletteSize = 0;
/* 1167 */     this.red = 0;
/* 1168 */     this.green = 0;
/* 1169 */     this.blue = 0;
/*      */ 
/*      */     
/* 1172 */     this.comments = null;
/*      */   }
/*      */   
/*      */   private String countBits(int num) {
/* 1176 */     int count = 0;
/* 1177 */     while (num != 0) {
/* 1178 */       if ((num & 0x1) == 1)
/* 1179 */         count++; 
/* 1180 */       num >>>= 1;
/*      */     } 
/*      */     
/* 1183 */     return (count == 0) ? "0" : ("" + count);
/*      */   }
/*      */   
/*      */   private void addXYZPoints(IIOMetadataNode root, String name, double x, double y, double z) {
/* 1187 */     IIOMetadataNode node = addChildNode(root, name, (Object)null);
/* 1188 */     addChildNode(node, "X", new Double(x));
/* 1189 */     addChildNode(node, "Y", new Double(y));
/* 1190 */     addChildNode(node, "Z", new Double(z));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private IIOMetadataNode addChildNode(IIOMetadataNode root, String name, Object object) {
/* 1196 */     IIOMetadataNode child = new IIOMetadataNode(name);
/* 1197 */     if (object != null) {
/* 1198 */       child.setUserObject(object);
/* 1199 */       child.setNodeValue(ImageUtil.convertObjectToString(object));
/*      */     } 
/* 1201 */     root.appendChild(child);
/* 1202 */     return child;
/*      */   }
/*      */   
/*      */   private Object getObjectValue(Node node) {
/* 1206 */     Object tmp = node.getNodeValue();
/*      */     
/* 1208 */     if (tmp == null && node instanceof IIOMetadataNode) {
/* 1209 */       tmp = ((IIOMetadataNode)node).getUserObject();
/*      */     }
/*      */     
/* 1212 */     return tmp;
/*      */   }
/*      */   
/*      */   private String getStringValue(Node node) {
/* 1216 */     Object tmp = getObjectValue(node);
/* 1217 */     return (tmp instanceof String) ? (String)tmp : null;
/*      */   }
/*      */   
/*      */   private Byte getByteValue(Node node) {
/* 1221 */     Object tmp = getObjectValue(node);
/* 1222 */     Byte value = null;
/* 1223 */     if (tmp instanceof String) {
/* 1224 */       value = Byte.valueOf((String)tmp);
/* 1225 */     } else if (tmp instanceof Byte) {
/* 1226 */       value = (Byte)tmp;
/*      */     } 
/* 1228 */     return value;
/*      */   }
/*      */   
/*      */   private Short getShortValue(Node node) {
/* 1232 */     Object tmp = getObjectValue(node);
/* 1233 */     Short value = null;
/* 1234 */     if (tmp instanceof String) {
/* 1235 */       value = Short.valueOf((String)tmp);
/* 1236 */     } else if (tmp instanceof Short) {
/* 1237 */       value = (Short)tmp;
/*      */     } 
/* 1239 */     return value;
/*      */   }
/*      */   
/*      */   private Integer getIntegerValue(Node node) {
/* 1243 */     Object tmp = getObjectValue(node);
/* 1244 */     Integer value = null;
/* 1245 */     if (tmp instanceof String) {
/* 1246 */       value = Integer.valueOf((String)tmp);
/* 1247 */     } else if (tmp instanceof Integer) {
/* 1248 */       value = (Integer)tmp;
/* 1249 */     } else if (tmp instanceof Byte) {
/* 1250 */       value = new Integer(((Byte)tmp).byteValue() & 0xFF);
/*      */     } 
/* 1252 */     return value;
/*      */   }
/*      */   
/*      */   private Double getDoubleValue(Node node) {
/* 1256 */     Object tmp = getObjectValue(node);
/* 1257 */     Double value = null;
/* 1258 */     if (tmp instanceof String) {
/* 1259 */       value = Double.valueOf((String)tmp);
/* 1260 */     } else if (tmp instanceof Double) {
/* 1261 */       value = (Double)tmp;
/*      */     } 
/* 1263 */     return value;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\bmp\BMPMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */