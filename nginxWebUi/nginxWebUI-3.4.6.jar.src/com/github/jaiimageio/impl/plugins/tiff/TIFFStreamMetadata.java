/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import java.nio.ByteOrder;
/*     */ import javax.imageio.metadata.IIOInvalidTreeException;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.metadata.IIOMetadataNode;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFStreamMetadata
/*     */   extends IIOMetadata
/*     */ {
/*     */   static final String nativeMetadataFormatName = "com_sun_media_imageio_plugins_tiff_stream_1.0";
/*     */   static final String nativeMetadataFormatClassName = "com.github.jaiimageio.impl.plugins.tiff.TIFFStreamMetadataFormat";
/*  65 */   private static final String bigEndianString = ByteOrder.BIG_ENDIAN
/*  66 */     .toString();
/*  67 */   private static final String littleEndianString = ByteOrder.LITTLE_ENDIAN
/*  68 */     .toString();
/*     */   
/*  70 */   public ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
/*     */   
/*     */   public TIFFStreamMetadata() {
/*  73 */     super(false, "com_sun_media_imageio_plugins_tiff_stream_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFStreamMetadataFormat", null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/*  80 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void fatal(Node node, String reason) throws IIOInvalidTreeException {
/*  86 */     throw new IIOInvalidTreeException(reason, node);
/*     */   }
/*     */   
/*     */   public Node getAsTree(String formatName) {
/*  90 */     IIOMetadataNode root = new IIOMetadataNode("com_sun_media_imageio_plugins_tiff_stream_1.0");
/*     */     
/*  92 */     IIOMetadataNode byteOrderNode = new IIOMetadataNode("ByteOrder");
/*  93 */     byteOrderNode.setAttribute("value", this.byteOrder.toString());
/*     */     
/*  95 */     root.appendChild(byteOrderNode);
/*  96 */     return root;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void mergeNativeTree(Node root) throws IIOInvalidTreeException {
/* 103 */     Node node = root;
/* 104 */     if (!node.getNodeName().equals("com_sun_media_imageio_plugins_tiff_stream_1.0")) {
/* 105 */       fatal(node, "Root must be com_sun_media_imageio_plugins_tiff_stream_1.0");
/*     */     }
/*     */     
/* 108 */     node = node.getFirstChild();
/* 109 */     if (node == null || !node.getNodeName().equals("ByteOrder")) {
/* 110 */       fatal(node, "Root must have \"ByteOrder\" child");
/*     */     }
/*     */     
/* 113 */     NamedNodeMap attrs = node.getAttributes();
/* 114 */     String order = attrs.getNamedItem("value").getNodeValue();
/*     */     
/* 116 */     if (order == null) {
/* 117 */       fatal(node, "ByteOrder node must have a \"value\" attribute");
/*     */     }
/* 119 */     if (order.equals(bigEndianString)) {
/* 120 */       this.byteOrder = ByteOrder.BIG_ENDIAN;
/* 121 */     } else if (order.equals(littleEndianString)) {
/* 122 */       this.byteOrder = ByteOrder.LITTLE_ENDIAN;
/*     */     } else {
/* 124 */       fatal(node, "Incorrect value for ByteOrder \"value\" attribute");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
/* 130 */     if (formatName.equals("com_sun_media_imageio_plugins_tiff_stream_1.0")) {
/* 131 */       if (root == null) {
/* 132 */         throw new IllegalArgumentException("root == null!");
/*     */       }
/* 134 */       mergeNativeTree(root);
/*     */     } else {
/* 136 */       throw new IllegalArgumentException("Not a recognized format!");
/*     */     } 
/*     */   }
/*     */   
/*     */   public void reset() {
/* 141 */     this.byteOrder = ByteOrder.BIG_ENDIAN;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFStreamMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */