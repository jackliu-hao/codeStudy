package com.github.jaiimageio.impl.plugins.wbmp;

import com.github.jaiimageio.impl.common.ImageUtil;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class WBMPMetadata extends IIOMetadata {
   public static final String nativeMetadataFormatName = "com_sun_media_imageio_plugins_wbmp_image_1.0";
   public int wbmpType;
   public int width;
   public int height;

   public WBMPMetadata() {
      super(true, "com_sun_media_imageio_plugins_wbmp_image_1.0", "com.github.jaiimageio.impl.plugins.wbmp.WBMPMetadataFormat", (String[])null, (String[])null);
   }

   public boolean isReadOnly() {
      return true;
   }

   public Node getAsTree(String formatName) {
      if (formatName.equals("com_sun_media_imageio_plugins_wbmp_image_1.0")) {
         return this.getNativeTree();
      } else if (formatName.equals("javax_imageio_1.0")) {
         return this.getStandardTree();
      } else {
         throw new IllegalArgumentException(I18N.getString("WBMPMetadata0"));
      }
   }

   private Node getNativeTree() {
      IIOMetadataNode root = new IIOMetadataNode("com_sun_media_imageio_plugins_wbmp_image_1.0");
      this.addChildNode(root, "WBMPType", new Integer(this.wbmpType));
      this.addChildNode(root, "Width", new Integer(this.width));
      this.addChildNode(root, "Height", new Integer(this.height));
      return root;
   }

   public void setFromTree(String formatName, Node root) {
      throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
   }

   public void mergeTree(String formatName, Node root) {
      throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
   }

   public void reset() {
      throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
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

   protected IIOMetadataNode getStandardChromaNode() {
      IIOMetadataNode node = new IIOMetadataNode("Chroma");
      IIOMetadataNode subNode = new IIOMetadataNode("ColorSpaceType");
      subNode.setAttribute("name", "GRAY");
      node.appendChild(subNode);
      subNode = new IIOMetadataNode("NumChannels");
      subNode.setAttribute("value", "1");
      node.appendChild(subNode);
      subNode = new IIOMetadataNode("BlackIsZero");
      subNode.setAttribute("value", "TRUE");
      node.appendChild(subNode);
      return node;
   }

   protected IIOMetadataNode getStandardDataNode() {
      IIOMetadataNode node = new IIOMetadataNode("Data");
      IIOMetadataNode subNode = new IIOMetadataNode("SampleFormat");
      subNode.setAttribute("value", "UnsignedIntegral");
      node.appendChild(subNode);
      subNode = new IIOMetadataNode("BitsPerSample");
      subNode.setAttribute("value", "1");
      node.appendChild(subNode);
      return node;
   }

   protected IIOMetadataNode getStandardDimensionNode() {
      IIOMetadataNode dimension_node = new IIOMetadataNode("Dimension");
      IIOMetadataNode node = null;
      node = new IIOMetadataNode("ImageOrientation");
      node.setAttribute("value", "Normal");
      dimension_node.appendChild(node);
      return dimension_node;
   }
}
