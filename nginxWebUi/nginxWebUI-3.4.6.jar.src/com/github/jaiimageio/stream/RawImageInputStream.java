/*     */ package com.github.jaiimageio.stream;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.ImageUtil;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.color.ICC_ColorSpace;
/*     */ import java.awt.color.ICC_Profile;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ComponentColorModel;
/*     */ import java.awt.image.ComponentSampleModel;
/*     */ import java.awt.image.DirectColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.MultiPixelPackedSampleModel;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.SinglePixelPackedSampleModel;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.stream.IIOByteBuffer;
/*     */ import javax.imageio.stream.ImageInputStream;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RawImageInputStream
/*     */   implements ImageInputStream
/*     */ {
/*  92 */   private static final String[] preDefinedColorSpaces = new String[] { "GRAY", "sRGB", "LINEAR_RGB", "PYCC", "CIEXYZ" };
/*     */ 
/*     */   
/*  95 */   private static final int[] preDefinedTypes = new int[] { 1003, 1000, 1004, 1002, 1001 };
/*     */   private ImageInputStream source;
/*     */   private ImageTypeSpecifier type;
/*     */   private long[] imageOffsets;
/*     */   private Dimension[] imageDimensions;
/*     */   
/*     */   private static String getAttribute(Node node, String name) {
/* 102 */     NamedNodeMap map = node.getAttributes();
/* 103 */     node = map.getNamedItem(name);
/* 104 */     return (node != null) ? node.getNodeValue() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean getBoolean(Node node, String name) {
/* 109 */     String s = getAttribute(node, name);
/* 110 */     return (s == null) ? false : (new Boolean(s)).booleanValue();
/*     */   }
/*     */ 
/*     */   
/*     */   private static int getInt(Node node, String name) {
/* 115 */     String s = getAttribute(node, name);
/* 116 */     return (s == null) ? 0 : (new Integer(s)).intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   private static byte[] getByteArray(Node node, String name) {
/* 121 */     String s = getAttribute(node, name);
/* 122 */     if (s == null)
/* 123 */       return null; 
/* 124 */     StringTokenizer token = new StringTokenizer(s);
/* 125 */     int count = token.countTokens();
/* 126 */     if (count == 0) {
/* 127 */       return null;
/*     */     }
/* 129 */     byte[] buf = new byte[count];
/* 130 */     int i = 0;
/* 131 */     while (token.hasMoreElements()) {
/* 132 */       buf[i++] = (new Byte(token.nextToken())).byteValue();
/*     */     }
/* 134 */     return buf;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int[] getIntArray(Node node, String name) {
/* 139 */     String s = getAttribute(node, name);
/* 140 */     if (s == null) {
/* 141 */       return null;
/*     */     }
/* 143 */     StringTokenizer token = new StringTokenizer(s);
/* 144 */     int count = token.countTokens();
/* 145 */     if (count == 0) {
/* 146 */       return null;
/*     */     }
/* 148 */     int[] buf = new int[count];
/* 149 */     int i = 0;
/* 150 */     while (token.hasMoreElements()) {
/* 151 */       buf[i++] = (new Integer(token.nextToken())).intValue();
/*     */     }
/* 153 */     return buf;
/*     */   }
/*     */   
/*     */   private static int getTransparency(String s) {
/* 157 */     if ("BITMASK".equals(s))
/* 158 */       return 2; 
/* 159 */     if ("OPAQUE".equals(s))
/* 160 */       return 1; 
/* 161 */     if ("TRANSLUCENT".equals(s))
/* 162 */       return 3; 
/* 163 */     return 0;
/*     */   }
/*     */   
/*     */   private static ColorSpace getColorSpace(Node node) throws IOException {
/* 167 */     NodeList nodes = node.getChildNodes();
/* 168 */     for (int i = 0; i < nodes.getLength(); i++) {
/* 169 */       Node child = nodes.item(i);
/* 170 */       if ("colorSpace".equals(child.getNodeName())) {
/* 171 */         String s = child.getNodeValue();
/* 172 */         for (int j = 0; j < preDefinedColorSpaces.length; j++) {
/* 173 */           if (preDefinedColorSpaces[j].equals(s)) {
/* 174 */             return ColorSpace.getInstance(preDefinedTypes[j]);
/*     */           }
/*     */         } 
/* 177 */         InputStream stm = (new URL(s)).openStream();
/*     */         
/* 179 */         ColorSpace cp = new ICC_ColorSpace(ICC_Profile.getInstance(stm));
/* 180 */         stm.close();
/* 181 */         return cp;
/*     */       } 
/*     */     } 
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RawImageInputStream(ImageInputStream source, ImageTypeSpecifier type, long[] imageOffsets, Dimension[] imageDimensions) {
/* 217 */     if (imageOffsets == null || imageDimensions == null || imageOffsets.length != imageDimensions.length)
/*     */     {
/* 219 */       throw new IllegalArgumentException(
/* 220 */           I18N.getString("RawImageInputStream0"));
/*     */     }
/*     */     
/* 223 */     this.source = source;
/* 224 */     this.type = type;
/* 225 */     this.imageOffsets = imageOffsets;
/* 226 */     this.imageDimensions = imageDimensions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RawImageInputStream(ImageInputStream source, SampleModel sampleModel, long[] imageOffsets, Dimension[] imageDimensions) {
/* 282 */     if (imageOffsets == null || imageDimensions == null || imageOffsets.length != imageDimensions.length)
/*     */     {
/* 284 */       throw new IllegalArgumentException(
/* 285 */           I18N.getString("RawImageInputStream0"));
/*     */     }
/*     */     
/* 288 */     this.source = source;
/* 289 */     ColorModel colorModel = ImageUtil.createColorModel(sampleModel);
/* 290 */     if (colorModel == null) {
/* 291 */       throw new IllegalArgumentException(
/* 292 */           I18N.getString("RawImageInputStream4"));
/*     */     }
/* 294 */     this.type = new ImageTypeSpecifier(colorModel, sampleModel);
/* 295 */     this.imageOffsets = imageOffsets;
/* 296 */     this.imageDimensions = imageDimensions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RawImageInputStream(ImageInputStream source, InputSource xmlSource) throws SAXException, IOException {
/* 496 */     this.source = source;
/*     */     
/* 498 */     DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/* 499 */     dbf.setValidating(true);
/* 500 */     dbf.setNamespaceAware(true);
/* 501 */     dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
/*     */     
/* 503 */     DocumentBuilder db = null;
/*     */     try {
/* 505 */       db = dbf.newDocumentBuilder();
/* 506 */     } catch (ParserConfigurationException ex) {
/* 507 */       throw new RuntimeException(I18N.getString("RawImageInputStream1"), ex);
/*     */     } 
/*     */ 
/*     */     
/* 511 */     Document doc = db.parse(xmlSource);
/*     */ 
/*     */     
/* 514 */     NodeList nodes = doc.getElementsByTagName("byteOrder");
/* 515 */     String byteOrder = nodes.item(0).getNodeValue();
/* 516 */     if ("NETWORK".equals(byteOrder)) {
/* 517 */       setByteOrder(ByteOrder.BIG_ENDIAN);
/* 518 */       this.source.setByteOrder(ByteOrder.BIG_ENDIAN);
/* 519 */     } else if ("REVERSE".equals(byteOrder)) {
/* 520 */       setByteOrder(ByteOrder.LITTLE_ENDIAN);
/* 521 */       setByteOrder(ByteOrder.LITTLE_ENDIAN);
/*     */     } 
/*     */ 
/*     */     
/* 525 */     nodes = doc.getElementsByTagName("offset");
/* 526 */     int length = nodes.getLength();
/* 527 */     this.imageOffsets = new long[length];
/* 528 */     for (int i = 0; i < length; i++) {
/* 529 */       this.imageOffsets[i] = (new Long(nodes.item(i).getNodeValue())).longValue();
/*     */     }
/*     */ 
/*     */     
/* 533 */     nodes = doc.getElementsByTagName("width");
/* 534 */     NodeList nodes1 = doc.getElementsByTagName("height");
/* 535 */     length = nodes.getLength();
/* 536 */     if (length != nodes1.getLength()) {
/* 537 */       throw new IllegalArgumentException(
/* 538 */           I18N.getString("RawImageInputStream2"));
/*     */     }
/* 540 */     this.imageDimensions = new Dimension[length];
/* 541 */     for (int j = 0; j < length; j++) {
/* 542 */       String w = nodes.item(j).getNodeValue();
/* 543 */       String h = nodes1.item(j).getNodeValue();
/*     */       
/* 545 */       this.imageDimensions[j] = new Dimension((new Integer(w))
/* 546 */           .intValue(), (new Integer(h))
/* 547 */           .intValue());
/*     */     } 
/*     */ 
/*     */     
/* 551 */     SampleModel sampleModel = null;
/*     */ 
/*     */     
/* 554 */     nodes = doc.getElementsByTagName("ComponentSampleModel");
/* 555 */     if (nodes.getLength() > 0) {
/* 556 */       Node node = nodes.item(0);
/* 557 */       int[] bankIndices = getIntArray(node, "bankIndices");
/*     */       
/* 559 */       if (bankIndices == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 566 */         sampleModel = new ComponentSampleModel(getInt(node, "dataType"), getInt(node, "w"), getInt(node, "h"), getInt(node, "pixelStride"), getInt(node, "scanlineStride"), getIntArray(node, "bandOffsets"));
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */ 
/*     */         
/* 575 */         sampleModel = new ComponentSampleModel(getInt(node, "dataType"), getInt(node, "w"), getInt(node, "h"), getInt(node, "pixelStride"), getInt(node, "scanlineStride"), bankIndices, getIntArray(node, "bandOffsets"));
/*     */       } 
/*     */     } 
/*     */     
/* 579 */     nodes = doc.getElementsByTagName("MultiPixelPackedSampleModel");
/* 580 */     if (nodes.getLength() > 0) {
/* 581 */       Node node = nodes.item(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 588 */       sampleModel = new MultiPixelPackedSampleModel(getInt(node, "dataType"), getInt(node, "w"), getInt(node, "h"), getInt(node, "numberOfBits"), getInt(node, "scanlineStride"), getInt(node, "dataBitOffset"));
/*     */     } 
/*     */ 
/*     */     
/* 592 */     nodes = doc.getElementsByTagName("SinglePixelPackedSampleModel");
/* 593 */     if (nodes.getLength() > 0) {
/* 594 */       Node node = nodes.item(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 600 */       sampleModel = new SinglePixelPackedSampleModel(getInt(node, "dataType"), getInt(node, "w"), getInt(node, "h"), getInt(node, "scanlineStride"), getIntArray(node, "bitMasks"));
/*     */     } 
/*     */ 
/*     */     
/* 604 */     ColorModel colorModel = null;
/*     */ 
/*     */     
/* 607 */     nodes = doc.getElementsByTagName("ComponentColorModel");
/* 608 */     if (nodes.getLength() > 0) {
/* 609 */       Node node = nodes.item(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 616 */       colorModel = new ComponentColorModel(getColorSpace(node), getIntArray(node, "bits"), getBoolean(node, "hasAlpha"), getBoolean(node, "isAlphaPremultiplied"), getTransparency(getAttribute(node, "transparency")), getInt(node, "transferType"));
/*     */     } 
/*     */ 
/*     */     
/* 620 */     nodes = doc.getElementsByTagName("DirectColorModel");
/* 621 */     if (nodes.getLength() > 0) {
/* 622 */       Node node = nodes.item(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 629 */       colorModel = new DirectColorModel(getColorSpace(node), getInt(node, "bits"), getInt(node, "rmask"), getInt(node, "gmask"), getInt(node, "bmask"), getInt(node, "amask"), false, 1);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 635 */     nodes = doc.getElementsByTagName("IndexColorModel");
/* 636 */     if (nodes.getLength() > 0) {
/* 637 */       Node node = nodes.item(0);
/* 638 */       byte[] alpha = getByteArray(node, "a");
/*     */       
/* 640 */       if (alpha == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 646 */         colorModel = new IndexColorModel(getInt(node, "bits"), getInt(node, "size"), getByteArray(node, "r"), getByteArray(node, "g"), getByteArray(node, "b"));
/*     */ 
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 653 */         colorModel = new IndexColorModel(getInt(node, "bits"), getInt(node, "size"), getByteArray(node, "r"), getByteArray(node, "g"), getByteArray(node, "b"), alpha);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 658 */     this.type = new ImageTypeSpecifier(colorModel, sampleModel);
/*     */ 
/*     */     
/* 661 */     if (this.imageDimensions.length == 0) {
/* 662 */       this.imageDimensions = new Dimension[this.imageOffsets.length];
/*     */       
/* 664 */       this.imageDimensions[0] = new Dimension(sampleModel.getWidth(), sampleModel
/* 665 */           .getHeight());
/* 666 */       for (int k = 1; k < this.imageDimensions.length; k++) {
/* 667 */         this.imageDimensions[k] = this.imageDimensions[0];
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ImageTypeSpecifier getImageType() {
/* 677 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getImageOffset(int imageIndex) {
/* 689 */     if (imageIndex < 0 || imageIndex >= this.imageOffsets.length)
/* 690 */       throw new IllegalArgumentException(
/* 691 */           I18N.getString("RawImageInputStream3")); 
/* 692 */     return this.imageOffsets[imageIndex];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dimension getImageDimension(int imageIndex) {
/* 702 */     if (imageIndex < 0 || imageIndex >= this.imageOffsets.length)
/* 703 */       throw new IllegalArgumentException(
/* 704 */           I18N.getString("RawImageInputStream3")); 
/* 705 */     return this.imageDimensions[imageIndex];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumImages() {
/* 713 */     return this.imageOffsets.length;
/*     */   }
/*     */   
/*     */   public void setByteOrder(ByteOrder byteOrder) {
/* 717 */     this.source.setByteOrder(byteOrder);
/*     */   }
/*     */   
/*     */   public ByteOrder getByteOrder() {
/* 721 */     return this.source.getByteOrder();
/*     */   }
/*     */   
/*     */   public int read() throws IOException {
/* 725 */     return this.source.read();
/*     */   }
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 729 */     return this.source.read(b);
/*     */   }
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 733 */     return this.source.read(b, off, len);
/*     */   }
/*     */   
/*     */   public void readBytes(IIOByteBuffer buf, int len) throws IOException {
/* 737 */     this.source.readBytes(buf, len);
/*     */   }
/*     */   
/*     */   public boolean readBoolean() throws IOException {
/* 741 */     return this.source.readBoolean();
/*     */   }
/*     */   
/*     */   public byte readByte() throws IOException {
/* 745 */     return this.source.readByte();
/*     */   }
/*     */   
/*     */   public int readUnsignedByte() throws IOException {
/* 749 */     return this.source.readUnsignedByte();
/*     */   }
/*     */   
/*     */   public short readShort() throws IOException {
/* 753 */     return this.source.readShort();
/*     */   }
/*     */   
/*     */   public int readUnsignedShort() throws IOException {
/* 757 */     return this.source.readUnsignedShort();
/*     */   }
/*     */   
/*     */   public char readChar() throws IOException {
/* 761 */     return this.source.readChar();
/*     */   }
/*     */   
/*     */   public int readInt() throws IOException {
/* 765 */     return this.source.readInt();
/*     */   }
/*     */   
/*     */   public long readUnsignedInt() throws IOException {
/* 769 */     return this.source.readUnsignedInt();
/*     */   }
/*     */   
/*     */   public long readLong() throws IOException {
/* 773 */     return this.source.readLong();
/*     */   }
/*     */   
/*     */   public float readFloat() throws IOException {
/* 777 */     return this.source.readFloat();
/*     */   }
/*     */   
/*     */   public double readDouble() throws IOException {
/* 781 */     return this.source.readDouble();
/*     */   }
/*     */   
/*     */   public String readLine() throws IOException {
/* 785 */     return this.source.readLine();
/*     */   }
/*     */   
/*     */   public String readUTF() throws IOException {
/* 789 */     return this.source.readUTF();
/*     */   }
/*     */   
/*     */   public void readFully(byte[] b, int off, int len) throws IOException {
/* 793 */     this.source.readFully(b, off, len);
/*     */   }
/*     */   
/*     */   public void readFully(byte[] b) throws IOException {
/* 797 */     this.source.readFully(b);
/*     */   }
/*     */   
/*     */   public void readFully(short[] s, int off, int len) throws IOException {
/* 801 */     this.source.readFully(s, off, len);
/*     */   }
/*     */   
/*     */   public void readFully(char[] c, int off, int len) throws IOException {
/* 805 */     this.source.readFully(c, off, len);
/*     */   }
/*     */   
/*     */   public void readFully(int[] i, int off, int len) throws IOException {
/* 809 */     this.source.readFully(i, off, len);
/*     */   }
/*     */   
/*     */   public void readFully(long[] l, int off, int len) throws IOException {
/* 813 */     this.source.readFully(l, off, len);
/*     */   }
/*     */   
/*     */   public void readFully(float[] f, int off, int len) throws IOException {
/* 817 */     this.source.readFully(f, off, len);
/*     */   }
/*     */   
/*     */   public void readFully(double[] d, int off, int len) throws IOException {
/* 821 */     this.source.readFully(d, off, len);
/*     */   }
/*     */   
/*     */   public long getStreamPosition() throws IOException {
/* 825 */     return this.source.getStreamPosition();
/*     */   }
/*     */   
/*     */   public int getBitOffset() throws IOException {
/* 829 */     return this.source.getBitOffset();
/*     */   }
/*     */   
/*     */   public void setBitOffset(int bitOffset) throws IOException {
/* 833 */     this.source.setBitOffset(bitOffset);
/*     */   }
/*     */   
/*     */   public int readBit() throws IOException {
/* 837 */     return this.source.readBit();
/*     */   }
/*     */   
/*     */   public long readBits(int numBits) throws IOException {
/* 841 */     return this.source.readBits(numBits);
/*     */   }
/*     */   
/*     */   public long length() throws IOException {
/* 845 */     return this.source.length();
/*     */   }
/*     */   
/*     */   public int skipBytes(int n) throws IOException {
/* 849 */     return this.source.skipBytes(n);
/*     */   }
/*     */   
/*     */   public long skipBytes(long n) throws IOException {
/* 853 */     return this.source.skipBytes(n);
/*     */   }
/*     */   
/*     */   public void seek(long pos) throws IOException {
/* 857 */     this.source.seek(pos);
/*     */   }
/*     */   
/*     */   public void mark() {
/* 861 */     this.source.mark();
/*     */   }
/*     */   
/*     */   public void reset() throws IOException {
/* 865 */     this.source.reset();
/*     */   }
/*     */   
/*     */   public void flushBefore(long pos) throws IOException {
/* 869 */     this.source.flushBefore(pos);
/*     */   }
/*     */   
/*     */   public void flush() throws IOException {
/* 873 */     this.source.flush();
/*     */   }
/*     */   
/*     */   public long getFlushedPosition() {
/* 877 */     return this.source.getFlushedPosition();
/*     */   }
/*     */   
/*     */   public boolean isCached() {
/* 881 */     return this.source.isCached();
/*     */   }
/*     */   
/*     */   public boolean isCachedMemory() {
/* 885 */     return this.source.isCachedMemory();
/*     */   }
/*     */   
/*     */   public boolean isCachedFile() {
/* 889 */     return this.source.isCachedFile();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 893 */     this.source.close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\stream\RawImageInputStream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */