package com.github.jaiimageio.impl.plugins.tiff;

import java.nio.ByteOrder;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class TIFFStreamMetadata extends IIOMetadata {
   static final String nativeMetadataFormatName = "com_sun_media_imageio_plugins_tiff_stream_1.0";
   static final String nativeMetadataFormatClassName = "com.github.jaiimageio.impl.plugins.tiff.TIFFStreamMetadataFormat";
   private static final String bigEndianString;
   private static final String littleEndianString;
   public ByteOrder byteOrder;

   public TIFFStreamMetadata() {
      super(false, "com_sun_media_imageio_plugins_tiff_stream_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFStreamMetadataFormat", (String[])null, (String[])null);
      this.byteOrder = ByteOrder.BIG_ENDIAN;
   }

   public boolean isReadOnly() {
      return false;
   }

   private static void fatal(Node node, String reason) throws IIOInvalidTreeException {
      throw new IIOInvalidTreeException(reason, node);
   }

   public Node getAsTree(String formatName) {
      IIOMetadataNode root = new IIOMetadataNode("com_sun_media_imageio_plugins_tiff_stream_1.0");
      IIOMetadataNode byteOrderNode = new IIOMetadataNode("ByteOrder");
      byteOrderNode.setAttribute("value", this.byteOrder.toString());
      root.appendChild(byteOrderNode);
      return root;
   }

   private void mergeNativeTree(Node root) throws IIOInvalidTreeException {
      if (!root.getNodeName().equals("com_sun_media_imageio_plugins_tiff_stream_1.0")) {
         fatal(root, "Root must be com_sun_media_imageio_plugins_tiff_stream_1.0");
      }

      Node node = root.getFirstChild();
      if (node == null || !node.getNodeName().equals("ByteOrder")) {
         fatal(node, "Root must have \"ByteOrder\" child");
      }

      NamedNodeMap attrs = node.getAttributes();
      String order = attrs.getNamedItem("value").getNodeValue();
      if (order == null) {
         fatal(node, "ByteOrder node must have a \"value\" attribute");
      }

      if (order.equals(bigEndianString)) {
         this.byteOrder = ByteOrder.BIG_ENDIAN;
      } else if (order.equals(littleEndianString)) {
         this.byteOrder = ByteOrder.LITTLE_ENDIAN;
      } else {
         fatal(node, "Incorrect value for ByteOrder \"value\" attribute");
      }

   }

   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
      if (formatName.equals("com_sun_media_imageio_plugins_tiff_stream_1.0")) {
         if (root == null) {
            throw new IllegalArgumentException("root == null!");
         } else {
            this.mergeNativeTree(root);
         }
      } else {
         throw new IllegalArgumentException("Not a recognized format!");
      }
   }

   public void reset() {
      this.byteOrder = ByteOrder.BIG_ENDIAN;
   }

   static {
      bigEndianString = ByteOrder.BIG_ENDIAN.toString();
      littleEndianString = ByteOrder.LITTLE_ENDIAN.toString();
   }
}
