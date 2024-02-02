package com.github.jaiimageio.stream;

import com.github.jaiimageio.impl.common.ImageUtil;
import java.awt.Dimension;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.StringTokenizer;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.IIOByteBuffer;
import javax.imageio.stream.ImageInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class RawImageInputStream implements ImageInputStream {
   private static final String[] preDefinedColorSpaces = new String[]{"GRAY", "sRGB", "LINEAR_RGB", "PYCC", "CIEXYZ"};
   private static final int[] preDefinedTypes = new int[]{1003, 1000, 1004, 1002, 1001};
   private ImageInputStream source;
   private ImageTypeSpecifier type;
   private long[] imageOffsets;
   private Dimension[] imageDimensions;

   private static String getAttribute(Node node, String name) {
      NamedNodeMap map = node.getAttributes();
      node = map.getNamedItem(name);
      return node != null ? node.getNodeValue() : null;
   }

   private static boolean getBoolean(Node node, String name) {
      String s = getAttribute(node, name);
      return s == null ? false : new Boolean(s);
   }

   private static int getInt(Node node, String name) {
      String s = getAttribute(node, name);
      return s == null ? 0 : new Integer(s);
   }

   private static byte[] getByteArray(Node node, String name) {
      String s = getAttribute(node, name);
      if (s == null) {
         return null;
      } else {
         StringTokenizer token = new StringTokenizer(s);
         int count = token.countTokens();
         if (count == 0) {
            return null;
         } else {
            byte[] buf = new byte[count];

            for(int i = 0; token.hasMoreElements(); buf[i++] = new Byte(token.nextToken())) {
            }

            return buf;
         }
      }
   }

   private static int[] getIntArray(Node node, String name) {
      String s = getAttribute(node, name);
      if (s == null) {
         return null;
      } else {
         StringTokenizer token = new StringTokenizer(s);
         int count = token.countTokens();
         if (count == 0) {
            return null;
         } else {
            int[] buf = new int[count];

            for(int i = 0; token.hasMoreElements(); buf[i++] = new Integer(token.nextToken())) {
            }

            return buf;
         }
      }
   }

   private static int getTransparency(String s) {
      if ("BITMASK".equals(s)) {
         return 2;
      } else if ("OPAQUE".equals(s)) {
         return 1;
      } else {
         return "TRANSLUCENT".equals(s) ? 3 : 0;
      }
   }

   private static ColorSpace getColorSpace(Node node) throws IOException {
      NodeList nodes = node.getChildNodes();

      for(int i = 0; i < nodes.getLength(); ++i) {
         Node child = nodes.item(i);
         if ("colorSpace".equals(child.getNodeName())) {
            String s = child.getNodeValue();

            for(int j = 0; j < preDefinedColorSpaces.length; ++j) {
               if (preDefinedColorSpaces[j].equals(s)) {
                  return ColorSpace.getInstance(preDefinedTypes[j]);
               }
            }

            InputStream stm = (new URL(s)).openStream();
            ColorSpace cp = new ICC_ColorSpace(ICC_Profile.getInstance(stm));
            stm.close();
            return cp;
         }
      }

      return null;
   }

   public RawImageInputStream(ImageInputStream source, ImageTypeSpecifier type, long[] imageOffsets, Dimension[] imageDimensions) {
      if (imageOffsets != null && imageDimensions != null && imageOffsets.length == imageDimensions.length) {
         this.source = source;
         this.type = type;
         this.imageOffsets = imageOffsets;
         this.imageDimensions = imageDimensions;
      } else {
         throw new IllegalArgumentException(I18N.getString("RawImageInputStream0"));
      }
   }

   public RawImageInputStream(ImageInputStream source, SampleModel sampleModel, long[] imageOffsets, Dimension[] imageDimensions) {
      if (imageOffsets != null && imageDimensions != null && imageOffsets.length == imageDimensions.length) {
         this.source = source;
         ColorModel colorModel = ImageUtil.createColorModel(sampleModel);
         if (colorModel == null) {
            throw new IllegalArgumentException(I18N.getString("RawImageInputStream4"));
         } else {
            this.type = new ImageTypeSpecifier(colorModel, sampleModel);
            this.imageOffsets = imageOffsets;
            this.imageDimensions = imageDimensions;
         }
      } else {
         throw new IllegalArgumentException(I18N.getString("RawImageInputStream0"));
      }
   }

   public RawImageInputStream(ImageInputStream source, InputSource xmlSource) throws SAXException, IOException {
      this.source = source;
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setValidating(true);
      dbf.setNamespaceAware(true);
      dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
      DocumentBuilder db = null;

      try {
         db = dbf.newDocumentBuilder();
      } catch (ParserConfigurationException var14) {
         throw new RuntimeException(I18N.getString("RawImageInputStream1"), var14);
      }

      Document doc = db.parse(xmlSource);
      NodeList nodes = doc.getElementsByTagName("byteOrder");
      String byteOrder = nodes.item(0).getNodeValue();
      if ("NETWORK".equals(byteOrder)) {
         this.setByteOrder(ByteOrder.BIG_ENDIAN);
         this.source.setByteOrder(ByteOrder.BIG_ENDIAN);
      } else if ("REVERSE".equals(byteOrder)) {
         this.setByteOrder(ByteOrder.LITTLE_ENDIAN);
         this.setByteOrder(ByteOrder.LITTLE_ENDIAN);
      }

      nodes = doc.getElementsByTagName("offset");
      int length = nodes.getLength();
      this.imageOffsets = new long[length];

      for(int i = 0; i < length; ++i) {
         this.imageOffsets[i] = new Long(nodes.item(i).getNodeValue());
      }

      nodes = doc.getElementsByTagName("width");
      NodeList nodes1 = doc.getElementsByTagName("height");
      length = nodes.getLength();
      if (length != nodes1.getLength()) {
         throw new IllegalArgumentException(I18N.getString("RawImageInputStream2"));
      } else {
         this.imageDimensions = new Dimension[length];

         for(int i = 0; i < length; ++i) {
            String w = nodes.item(i).getNodeValue();
            String h = nodes1.item(i).getNodeValue();
            this.imageDimensions[i] = new Dimension(new Integer(w), new Integer(h));
         }

         SampleModel sampleModel = null;
         nodes = doc.getElementsByTagName("ComponentSampleModel");
         Node node;
         if (nodes.getLength() > 0) {
            node = nodes.item(0);
            int[] bankIndices = getIntArray(node, "bankIndices");
            if (bankIndices == null) {
               sampleModel = new ComponentSampleModel(getInt(node, "dataType"), getInt(node, "w"), getInt(node, "h"), getInt(node, "pixelStride"), getInt(node, "scanlineStride"), getIntArray(node, "bandOffsets"));
            } else {
               sampleModel = new ComponentSampleModel(getInt(node, "dataType"), getInt(node, "w"), getInt(node, "h"), getInt(node, "pixelStride"), getInt(node, "scanlineStride"), bankIndices, getIntArray(node, "bandOffsets"));
            }
         }

         nodes = doc.getElementsByTagName("MultiPixelPackedSampleModel");
         if (nodes.getLength() > 0) {
            node = nodes.item(0);
            sampleModel = new MultiPixelPackedSampleModel(getInt(node, "dataType"), getInt(node, "w"), getInt(node, "h"), getInt(node, "numberOfBits"), getInt(node, "scanlineStride"), getInt(node, "dataBitOffset"));
         }

         nodes = doc.getElementsByTagName("SinglePixelPackedSampleModel");
         if (nodes.getLength() > 0) {
            node = nodes.item(0);
            sampleModel = new SinglePixelPackedSampleModel(getInt(node, "dataType"), getInt(node, "w"), getInt(node, "h"), getInt(node, "scanlineStride"), getIntArray(node, "bitMasks"));
         }

         ColorModel colorModel = null;
         nodes = doc.getElementsByTagName("ComponentColorModel");
         Node node;
         if (nodes.getLength() > 0) {
            node = nodes.item(0);
            colorModel = new ComponentColorModel(getColorSpace(node), getIntArray(node, "bits"), getBoolean(node, "hasAlpha"), getBoolean(node, "isAlphaPremultiplied"), getTransparency(getAttribute(node, "transparency")), getInt(node, "transferType"));
         }

         nodes = doc.getElementsByTagName("DirectColorModel");
         if (nodes.getLength() > 0) {
            node = nodes.item(0);
            colorModel = new DirectColorModel(getColorSpace(node), getInt(node, "bits"), getInt(node, "rmask"), getInt(node, "gmask"), getInt(node, "bmask"), getInt(node, "amask"), false, 1);
         }

         nodes = doc.getElementsByTagName("IndexColorModel");
         if (nodes.getLength() > 0) {
            node = nodes.item(0);
            byte[] alpha = getByteArray(node, "a");
            if (alpha == null) {
               colorModel = new IndexColorModel(getInt(node, "bits"), getInt(node, "size"), getByteArray(node, "r"), getByteArray(node, "g"), getByteArray(node, "b"));
            } else {
               colorModel = new IndexColorModel(getInt(node, "bits"), getInt(node, "size"), getByteArray(node, "r"), getByteArray(node, "g"), getByteArray(node, "b"), alpha);
            }
         }

         this.type = new ImageTypeSpecifier((ColorModel)colorModel, (SampleModel)sampleModel);
         if (this.imageDimensions.length == 0) {
            this.imageDimensions = new Dimension[this.imageOffsets.length];
            this.imageDimensions[0] = new Dimension(((SampleModel)sampleModel).getWidth(), ((SampleModel)sampleModel).getHeight());

            for(int i = 1; i < this.imageDimensions.length; ++i) {
               this.imageDimensions[i] = this.imageDimensions[0];
            }
         }

      }
   }

   public ImageTypeSpecifier getImageType() {
      return this.type;
   }

   public long getImageOffset(int imageIndex) {
      if (imageIndex >= 0 && imageIndex < this.imageOffsets.length) {
         return this.imageOffsets[imageIndex];
      } else {
         throw new IllegalArgumentException(I18N.getString("RawImageInputStream3"));
      }
   }

   public Dimension getImageDimension(int imageIndex) {
      if (imageIndex >= 0 && imageIndex < this.imageOffsets.length) {
         return this.imageDimensions[imageIndex];
      } else {
         throw new IllegalArgumentException(I18N.getString("RawImageInputStream3"));
      }
   }

   public int getNumImages() {
      return this.imageOffsets.length;
   }

   public void setByteOrder(ByteOrder byteOrder) {
      this.source.setByteOrder(byteOrder);
   }

   public ByteOrder getByteOrder() {
      return this.source.getByteOrder();
   }

   public int read() throws IOException {
      return this.source.read();
   }

   public int read(byte[] b) throws IOException {
      return this.source.read(b);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      return this.source.read(b, off, len);
   }

   public void readBytes(IIOByteBuffer buf, int len) throws IOException {
      this.source.readBytes(buf, len);
   }

   public boolean readBoolean() throws IOException {
      return this.source.readBoolean();
   }

   public byte readByte() throws IOException {
      return this.source.readByte();
   }

   public int readUnsignedByte() throws IOException {
      return this.source.readUnsignedByte();
   }

   public short readShort() throws IOException {
      return this.source.readShort();
   }

   public int readUnsignedShort() throws IOException {
      return this.source.readUnsignedShort();
   }

   public char readChar() throws IOException {
      return this.source.readChar();
   }

   public int readInt() throws IOException {
      return this.source.readInt();
   }

   public long readUnsignedInt() throws IOException {
      return this.source.readUnsignedInt();
   }

   public long readLong() throws IOException {
      return this.source.readLong();
   }

   public float readFloat() throws IOException {
      return this.source.readFloat();
   }

   public double readDouble() throws IOException {
      return this.source.readDouble();
   }

   public String readLine() throws IOException {
      return this.source.readLine();
   }

   public String readUTF() throws IOException {
      return this.source.readUTF();
   }

   public void readFully(byte[] b, int off, int len) throws IOException {
      this.source.readFully(b, off, len);
   }

   public void readFully(byte[] b) throws IOException {
      this.source.readFully(b);
   }

   public void readFully(short[] s, int off, int len) throws IOException {
      this.source.readFully(s, off, len);
   }

   public void readFully(char[] c, int off, int len) throws IOException {
      this.source.readFully(c, off, len);
   }

   public void readFully(int[] i, int off, int len) throws IOException {
      this.source.readFully(i, off, len);
   }

   public void readFully(long[] l, int off, int len) throws IOException {
      this.source.readFully(l, off, len);
   }

   public void readFully(float[] f, int off, int len) throws IOException {
      this.source.readFully(f, off, len);
   }

   public void readFully(double[] d, int off, int len) throws IOException {
      this.source.readFully(d, off, len);
   }

   public long getStreamPosition() throws IOException {
      return this.source.getStreamPosition();
   }

   public int getBitOffset() throws IOException {
      return this.source.getBitOffset();
   }

   public void setBitOffset(int bitOffset) throws IOException {
      this.source.setBitOffset(bitOffset);
   }

   public int readBit() throws IOException {
      return this.source.readBit();
   }

   public long readBits(int numBits) throws IOException {
      return this.source.readBits(numBits);
   }

   public long length() throws IOException {
      return this.source.length();
   }

   public int skipBytes(int n) throws IOException {
      return this.source.skipBytes(n);
   }

   public long skipBytes(long n) throws IOException {
      return this.source.skipBytes(n);
   }

   public void seek(long pos) throws IOException {
      this.source.seek(pos);
   }

   public void mark() {
      this.source.mark();
   }

   public void reset() throws IOException {
      this.source.reset();
   }

   public void flushBefore(long pos) throws IOException {
      this.source.flushBefore(pos);
   }

   public void flush() throws IOException {
      this.source.flush();
   }

   public long getFlushedPosition() {
      return this.source.getFlushedPosition();
   }

   public boolean isCached() {
      return this.source.isCached();
   }

   public boolean isCachedMemory() {
      return this.source.isCachedMemory();
   }

   public boolean isCachedFile() {
      return this.source.isCachedFile();
   }

   public void close() throws IOException {
      this.source.close();
   }
}
