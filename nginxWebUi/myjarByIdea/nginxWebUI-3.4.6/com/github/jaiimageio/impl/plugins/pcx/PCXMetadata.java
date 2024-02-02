package com.github.jaiimageio.impl.plugins.pcx;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class PCXMetadata extends IIOMetadata implements Cloneable, PCXConstants {
   short version;
   byte bitsPerPixel;
   boolean gotxmin;
   boolean gotymin;
   short xmin;
   short ymin;
   int vdpi;
   int hdpi;
   int hsize;
   int vsize;

   PCXMetadata() {
      super(true, (String)null, (String)null, (String[])null, (String[])null);
      this.reset();
   }

   public Node getAsTree(String formatName) {
      if (formatName.equals("javax_imageio_1.0")) {
         return this.getStandardTree();
      } else {
         throw new IllegalArgumentException("Not a recognized format!");
      }
   }

   public boolean isReadOnly() {
      return false;
   }

   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
      if (formatName.equals("javax_imageio_1.0")) {
         if (root == null) {
            throw new IllegalArgumentException("root == null!");
         } else {
            this.mergeStandardTree(root);
         }
      } else {
         throw new IllegalArgumentException("Not a recognized format!");
      }
   }

   public void reset() {
      this.version = 5;
      this.bitsPerPixel = 0;
      this.gotxmin = false;
      this.gotymin = false;
      this.xmin = 0;
      this.ymin = 0;
      this.vdpi = 72;
      this.hdpi = 72;
      this.hsize = 0;
      this.vsize = 0;
   }

   public IIOMetadataNode getStandardDocumentNode() {
      String versionString;
      switch (this.version) {
         case 0:
            versionString = "2.5";
            break;
         case 1:
         default:
            versionString = null;
            break;
         case 2:
            versionString = "2.8 with palette";
            break;
         case 3:
            versionString = "2.8 without palette";
            break;
         case 4:
            versionString = "PC Paintbrush for Windows";
            break;
         case 5:
            versionString = "3.0";
      }

      IIOMetadataNode documentNode = null;
      if (versionString != null) {
         documentNode = new IIOMetadataNode("Document");
         IIOMetadataNode node = new IIOMetadataNode("FormatVersion");
         node.setAttribute("value", versionString);
         documentNode.appendChild(node);
      }

      return documentNode;
   }

   public IIOMetadataNode getStandardDimensionNode() {
      IIOMetadataNode dimensionNode = new IIOMetadataNode("Dimension");
      IIOMetadataNode node = null;
      node = new IIOMetadataNode("HorizontalPixelOffset");
      node.setAttribute("value", String.valueOf(this.xmin));
      dimensionNode.appendChild(node);
      node = new IIOMetadataNode("VerticalPixelOffset");
      node.setAttribute("value", String.valueOf(this.ymin));
      dimensionNode.appendChild(node);
      node = new IIOMetadataNode("HorizontalPixelSize");
      node.setAttribute("value", String.valueOf(254.0 / (double)this.hdpi));
      dimensionNode.appendChild(node);
      node = new IIOMetadataNode("VerticalPixelSize");
      node.setAttribute("value", String.valueOf(254.0 / (double)this.vdpi));
      dimensionNode.appendChild(node);
      if (this.hsize != 0) {
         node = new IIOMetadataNode("HorizontalScreenSize");
         node.setAttribute("value", String.valueOf(this.hsize));
         dimensionNode.appendChild(node);
      }

      if (this.vsize != 0) {
         node = new IIOMetadataNode("VerticalScreenSize");
         node.setAttribute("value", String.valueOf(this.vsize));
         dimensionNode.appendChild(node);
      }

      return dimensionNode;
   }

   private void mergeStandardTree(Node root) throws IIOInvalidTreeException {
      if (!root.getNodeName().equals("javax_imageio_1.0")) {
         throw new IIOInvalidTreeException("Root must be javax_imageio_1.0", root);
      } else {
         for(Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
            String name = node.getNodeName();
            if (name.equals("Dimension")) {
               for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                  String childName = child.getNodeName();
                  String vss;
                  if (childName.equals("HorizontalPixelOffset")) {
                     vss = getAttribute(child, "value");
                     this.xmin = Short.valueOf(vss);
                     this.gotxmin = true;
                  } else if (childName.equals("VerticalPixelOffset")) {
                     vss = getAttribute(child, "value");
                     this.ymin = Short.valueOf(vss);
                     this.gotymin = true;
                  } else if (childName.equals("HorizontalPixelSize")) {
                     vss = getAttribute(child, "value");
                     this.hdpi = (int)(254.0F / Float.parseFloat(vss) + 0.5F);
                  } else if (childName.equals("VerticalPixelSize")) {
                     vss = getAttribute(child, "value");
                     this.vdpi = (int)(254.0F / Float.parseFloat(vss) + 0.5F);
                  } else if (childName.equals("HorizontalScreenSize")) {
                     vss = getAttribute(child, "value");
                     this.hsize = Integer.valueOf(vss);
                  } else if (childName.equals("VerticalScreenSize")) {
                     vss = getAttribute(child, "value");
                     this.vsize = Integer.valueOf(vss);
                  }
               }
            }
         }

      }
   }

   private static String getAttribute(Node node, String attrName) {
      NamedNodeMap attrs = node.getAttributes();
      Node attr = attrs.getNamedItem(attrName);
      return attr != null ? attr.getNodeValue() : null;
   }
}
