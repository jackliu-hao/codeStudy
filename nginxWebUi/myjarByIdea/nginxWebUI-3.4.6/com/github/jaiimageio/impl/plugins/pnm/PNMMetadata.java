package com.github.jaiimageio.impl.plugins.pnm;

import com.github.jaiimageio.impl.common.ImageUtil;
import com.github.jaiimageio.plugins.pnm.PNMImageWriteParam;
import java.awt.image.SampleModel;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PNMMetadata extends IIOMetadata implements Cloneable {
   static final String nativeMetadataFormatName = "com_sun_media_imageio_plugins_pnm_image_1.0";
   private int maxSample;
   private int width;
   private int height;
   private int variant;
   private ArrayList comments;
   private int maxSampleSize;

   PNMMetadata() {
      super(true, "com_sun_media_imageio_plugins_pnm_image_1.0", "com.github.jaiimageio.impl.plugins.pnm.PNMMetadataFormat", (String[])null, (String[])null);
   }

   public PNMMetadata(IIOMetadata metadata) throws IIOInvalidTreeException {
      this();
      if (metadata != null) {
         List formats = Arrays.asList(metadata.getMetadataFormatNames());
         if (formats.contains("com_sun_media_imageio_plugins_pnm_image_1.0")) {
            this.setFromTree("com_sun_media_imageio_plugins_pnm_image_1.0", metadata.getAsTree("com_sun_media_imageio_plugins_pnm_image_1.0"));
         } else if (metadata.isStandardMetadataFormatSupported()) {
            String format = "javax_imageio_1.0";
            this.setFromTree(format, metadata.getAsTree(format));
         }
      }

   }

   PNMMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
      this();
      this.initialize(imageType, param);
   }

   void initialize(ImageTypeSpecifier imageType, ImageWriteParam param) {
      ImageTypeSpecifier destType = null;
      if (param != null) {
         destType = param.getDestinationType();
         if (destType == null) {
            destType = imageType;
         }
      } else {
         destType = imageType;
      }

      if (destType != null) {
         SampleModel sm = destType.getSampleModel();
         int[] sampleSize = sm.getSampleSize();
         this.width = sm.getWidth();
         this.height = sm.getHeight();

         for(int i = 0; i < sampleSize.length; ++i) {
            if (sampleSize[i] > this.maxSampleSize) {
               this.maxSampleSize = sampleSize[i];
            }
         }

         this.maxSample = (1 << this.maxSampleSize) - 1;
         boolean isRaw = true;
         if (param instanceof PNMImageWriteParam) {
            isRaw = ((PNMImageWriteParam)param).getRaw();
         }

         if (this.maxSampleSize == 1) {
            this.variant = 49;
         } else if (sm.getNumBands() == 1) {
            this.variant = 50;
         } else if (sm.getNumBands() == 3) {
            this.variant = 51;
         }

         if (this.variant <= 51 && isRaw && this.maxSampleSize <= 8) {
            this.variant += 3;
         }
      }

   }

   protected Object clone() {
      PNMMetadata theClone = null;

      try {
         theClone = (PNMMetadata)super.clone();
      } catch (CloneNotSupportedException var4) {
      }

      if (this.comments != null) {
         int numComments = this.comments.size();

         for(int i = 0; i < numComments; ++i) {
            theClone.addComment((String)this.comments.get(i));
         }
      }

      return theClone;
   }

   public Node getAsTree(String formatName) {
      if (formatName == null) {
         throw new IllegalArgumentException(I18N.getString("PNMMetadata0"));
      } else if (formatName.equals("com_sun_media_imageio_plugins_pnm_image_1.0")) {
         return this.getNativeTree();
      } else if (formatName.equals("javax_imageio_1.0")) {
         return this.getStandardTree();
      } else {
         throw new IllegalArgumentException(I18N.getString("PNMMetadata1") + " " + formatName);
      }
   }

   IIOMetadataNode getNativeTree() {
      IIOMetadataNode root = new IIOMetadataNode("com_sun_media_imageio_plugins_pnm_image_1.0");
      IIOMetadataNode child = new IIOMetadataNode("FormatName");
      child.setUserObject(this.getFormatName());
      child.setNodeValue(this.getFormatName());
      root.appendChild(child);
      child = new IIOMetadataNode("Variant");
      child.setUserObject(this.getVariant());
      child.setNodeValue(this.getVariant());
      root.appendChild(child);
      child = new IIOMetadataNode("Width");
      Object tmp = new Integer(this.width);
      child.setUserObject(tmp);
      child.setNodeValue(ImageUtil.convertObjectToString(tmp));
      root.appendChild(child);
      child = new IIOMetadataNode("Height");
      tmp = new Integer(this.height);
      child.setUserObject(tmp);
      child.setNodeValue(ImageUtil.convertObjectToString(tmp));
      root.appendChild(child);
      child = new IIOMetadataNode("MaximumSample");
      Object tmp = new Byte((byte)this.maxSample);
      child.setUserObject(tmp);
      child.setNodeValue(ImageUtil.convertObjectToString(new Integer(this.maxSample)));
      root.appendChild(child);
      if (this.comments != null) {
         for(int i = 0; i < this.comments.size(); ++i) {
            child = new IIOMetadataNode("Comment");
            Object tmp = this.comments.get(i);
            child.setUserObject(tmp);
            child.setNodeValue(ImageUtil.convertObjectToString(tmp));
            root.appendChild(child);
         }
      }

      return root;
   }

   protected IIOMetadataNode getStandardChromaNode() {
      IIOMetadataNode node = new IIOMetadataNode("Chroma");
      int temp = (this.variant - 49) % 3 + 1;
      IIOMetadataNode subNode = new IIOMetadataNode("ColorSpaceType");
      if (temp == 3) {
         subNode.setAttribute("name", "RGB");
      } else {
         subNode.setAttribute("name", "GRAY");
      }

      node.appendChild(subNode);
      subNode = new IIOMetadataNode("NumChannels");
      subNode.setAttribute("value", "" + (temp == 3 ? 3 : 1));
      node.appendChild(subNode);
      if (temp != 3) {
         subNode = new IIOMetadataNode("BlackIsZero");
         subNode.setAttribute("value", "TRUE");
         node.appendChild(subNode);
      }

      return node;
   }

   protected IIOMetadataNode getStandardDataNode() {
      IIOMetadataNode node = new IIOMetadataNode("Data");
      IIOMetadataNode subNode = new IIOMetadataNode("SampleFormat");
      subNode.setAttribute("value", "UnsignedIntegral");
      node.appendChild(subNode);
      int temp = (this.variant - 49) % 3 + 1;
      subNode = new IIOMetadataNode("BitsPerSample");
      if (temp == 1) {
         subNode.setAttribute("value", "1");
      } else if (temp == 2) {
         subNode.setAttribute("value", "8");
      } else {
         subNode.setAttribute("value", "8 8 8");
      }

      node.appendChild(subNode);
      subNode = new IIOMetadataNode("SignificantBitsPerSample");
      if (temp != 1 && temp != 2) {
         subNode.setAttribute("value", this.maxSampleSize + " " + this.maxSampleSize + " " + this.maxSampleSize);
      } else {
         subNode.setAttribute("value", "" + this.maxSampleSize);
      }

      node.appendChild(subNode);
      return node;
   }

   protected IIOMetadataNode getStandardDimensionNode() {
      IIOMetadataNode node = new IIOMetadataNode("Dimension");
      IIOMetadataNode subNode = new IIOMetadataNode("ImageOrientation");
      subNode.setAttribute("value", "Normal");
      node.appendChild(subNode);
      return node;
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

   public boolean isReadOnly() {
      return false;
   }

   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
      if (formatName == null) {
         throw new IllegalArgumentException(I18N.getString("PNMMetadata0"));
      } else if (root == null) {
         throw new IllegalArgumentException(I18N.getString("PNMMetadata2"));
      } else {
         if (formatName.equals("com_sun_media_imageio_plugins_pnm_image_1.0") && root.getNodeName().equals("com_sun_media_imageio_plugins_pnm_image_1.0")) {
            this.mergeNativeTree(root);
         } else {
            if (!formatName.equals("javax_imageio_1.0")) {
               throw new IllegalArgumentException(I18N.getString("PNMMetadata1") + " " + formatName);
            }

            this.mergeStandardTree(root);
         }

      }
   }

   public void setFromTree(String formatName, Node root) throws IIOInvalidTreeException {
      if (formatName == null) {
         throw new IllegalArgumentException(I18N.getString("PNMMetadata0"));
      } else if (root == null) {
         throw new IllegalArgumentException(I18N.getString("PNMMetadata2"));
      } else {
         if (formatName.equals("com_sun_media_imageio_plugins_pnm_image_1.0") && root.getNodeName().equals("com_sun_media_imageio_plugins_pnm_image_1.0")) {
            this.mergeNativeTree(root);
         } else {
            if (!formatName.equals("javax_imageio_1.0")) {
               throw new IllegalArgumentException(I18N.getString("PNMMetadata2") + " " + formatName);
            }

            this.mergeStandardTree(root);
         }

      }
   }

   public void reset() {
      this.maxSample = this.width = this.height = this.variant = this.maxSampleSize = 0;
      this.comments = null;
   }

   public String getFormatName() {
      int v = (this.variant - 49) % 3 + 1;
      if (v == 1) {
         return "PBM";
      } else if (v == 2) {
         return "PGM";
      } else {
         return v == 3 ? "PPM" : null;
      }
   }

   public String getVariant() {
      return this.variant > 51 ? "RAWBITS" : "ASCII";
   }

   boolean isRaw() {
      return this.getVariant().equals("RAWBITS");
   }

   public void setVariant(int v) {
      this.variant = v;
   }

   public void setWidth(int w) {
      this.width = w;
   }

   public void setHeight(int h) {
      this.height = h;
   }

   int getMaxBitDepth() {
      return this.maxSampleSize;
   }

   int getMaxValue() {
      return this.maxSample;
   }

   public void setMaxBitDepth(int maxValue) {
      this.maxSample = maxValue;

      for(this.maxSampleSize = 0; maxValue > 0; ++this.maxSampleSize) {
         maxValue >>>= 1;
      }

   }

   public synchronized void addComment(String comment) {
      if (this.comments == null) {
         this.comments = new ArrayList();
      }

      comment = comment.replaceAll("[\n\r\f]", " ");
      this.comments.add(comment);
   }

   Iterator getComments() {
      return this.comments == null ? null : this.comments.iterator();
   }

   private void mergeNativeTree(Node root) throws IIOInvalidTreeException {
      NodeList list = root.getChildNodes();
      String format = null;
      String var = null;

      for(int i = list.getLength() - 1; i >= 0; --i) {
         IIOMetadataNode node = (IIOMetadataNode)list.item(i);
         String name = node.getNodeName();
         if (name.equals("Comment")) {
            this.addComment((String)node.getUserObject());
         } else if (name.equals("Width")) {
            this.width = (Integer)node.getUserObject();
         } else if (name.equals("Height")) {
            this.width = (Integer)node.getUserObject();
         } else if (name.equals("MaximumSample")) {
            int maxValue = (Integer)node.getUserObject();
            this.setMaxBitDepth(maxValue);
         } else if (name.equals("FormatName")) {
            format = (String)node.getUserObject();
         } else if (name.equals("Variant")) {
            var = (String)node.getUserObject();
         }
      }

      if (format.equals("PBM")) {
         this.variant = 49;
      } else if (format.equals("PGM")) {
         this.variant = 50;
      } else if (format.equals("PPM")) {
         this.variant = 51;
      }

      if (var.equals("RAWBITS")) {
         this.variant += 3;
      }

   }

   private void mergeStandardTree(Node root) throws IIOInvalidTreeException {
      NodeList children = root.getChildNodes();
      String colorSpace = null;
      int numComps = 0;
      int[] bitsPerSample = null;

      for(int i = 0; i < children.getLength(); ++i) {
         Node node = children.item(i);
         String name = node.getNodeName();
         NodeList children1;
         int maxBitDepth;
         String name1;
         Node child;
         String name1;
         if (name.equals("Chroma")) {
            children1 = node.getChildNodes();

            for(maxBitDepth = 0; maxBitDepth < children1.getLength(); ++maxBitDepth) {
               child = children1.item(maxBitDepth);
               name1 = child.getNodeName();
               if (name1.equals("NumChannels")) {
                  name1 = (String)this.getAttribute(child, "value");
                  numComps = new Integer(name1);
               } else if (name1.equals("ColorSpaceType")) {
                  colorSpace = (String)this.getAttribute(child, "name");
               }
            }
         } else if (!name.equals("Compression")) {
            if (!name.equals("Data")) {
               if (!name.equals("Dimension") && !name.equals("Document")) {
                  if (name.equals("Text")) {
                     children1 = node.getChildNodes();

                     for(maxBitDepth = 0; maxBitDepth < children1.getLength(); ++maxBitDepth) {
                        child = children1.item(maxBitDepth);
                        name1 = child.getNodeName();
                        if (name1.equals("TextEntry")) {
                           this.addComment((String)this.getAttribute(child, "value"));
                        }
                     }
                  } else if (!name.equals("Transparency")) {
                     throw new IIOInvalidTreeException(I18N.getString("PNMMetadata3") + " " + name, node);
                  }
               }
            } else {
               children1 = node.getChildNodes();
               maxBitDepth = -1;

               int k;
               for(k = 0; k < children1.getLength(); ++k) {
                  Node child = children1.item(k);
                  name1 = child.getNodeName();
                  if (!name1.equals("BitsPerSample")) {
                     if (name1.equals("SignificantBitsPerSample")) {
                        String s = (String)this.getAttribute(child, "value");

                        int sbps;
                        for(StringTokenizer t = new StringTokenizer(s); t.hasMoreTokens(); maxBitDepth = Math.max(sbps, maxBitDepth)) {
                           sbps = Integer.valueOf(t.nextToken());
                        }
                     }
                  } else {
                     List bps = new ArrayList(3);
                     String s = (String)this.getAttribute(child, "value");
                     StringTokenizer t = new StringTokenizer(s);

                     while(t.hasMoreTokens()) {
                        bps.add(Integer.valueOf(t.nextToken()));
                     }

                     bitsPerSample = new int[bps.size()];

                     for(int k = 0; k < bitsPerSample.length; ++k) {
                        bitsPerSample[k] = (Integer)bps.get(k);
                     }
                  }
               }

               if (maxBitDepth > 0) {
                  this.setMaxBitDepth((1 << maxBitDepth) - 1);
               } else if (bitsPerSample != null) {
                  for(k = 0; k < bitsPerSample.length; ++k) {
                     if (bitsPerSample[k] > maxBitDepth) {
                        maxBitDepth = bitsPerSample[k];
                     }
                  }

                  this.setMaxBitDepth((1 << maxBitDepth) - 1);
               }
            }
         }
      }

      if ((colorSpace == null || !colorSpace.equals("RGB")) && numComps <= 1 && bitsPerSample.length <= 1) {
         if (this.maxSampleSize > 1) {
            this.variant = 50;
         } else {
            this.variant = 49;
         }
      } else {
         this.variant = 51;
      }

   }

   public Object getAttribute(Node node, String name) {
      NamedNodeMap map = node.getAttributes();
      node = map.getNamedItem(name);
      return node != null ? node.getNodeValue() : null;
   }
}
