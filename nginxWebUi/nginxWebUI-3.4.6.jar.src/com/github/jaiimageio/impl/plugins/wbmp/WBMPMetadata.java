/*     */ package com.github.jaiimageio.impl.plugins.wbmp;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.ImageUtil;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.metadata.IIOMetadataNode;
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
/*     */ public class WBMPMetadata
/*     */   extends IIOMetadata
/*     */ {
/*     */   public static final String nativeMetadataFormatName = "com_sun_media_imageio_plugins_wbmp_image_1.0";
/*     */   public int wbmpType;
/*     */   public int width;
/*     */   public int height;
/*     */   
/*     */   public WBMPMetadata() {
/*  67 */     super(true, "com_sun_media_imageio_plugins_wbmp_image_1.0", "com.github.jaiimageio.impl.plugins.wbmp.WBMPMetadataFormat", null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/*  74 */     return true;
/*     */   }
/*     */   
/*     */   public Node getAsTree(String formatName) {
/*  78 */     if (formatName.equals("com_sun_media_imageio_plugins_wbmp_image_1.0"))
/*  79 */       return getNativeTree(); 
/*  80 */     if (formatName
/*  81 */       .equals("javax_imageio_1.0")) {
/*  82 */       return getStandardTree();
/*     */     }
/*  84 */     throw new IllegalArgumentException(I18N.getString("WBMPMetadata0"));
/*     */   }
/*     */ 
/*     */   
/*     */   private Node getNativeTree() {
/*  89 */     IIOMetadataNode root = new IIOMetadataNode("com_sun_media_imageio_plugins_wbmp_image_1.0");
/*     */ 
/*     */     
/*  92 */     addChildNode(root, "WBMPType", new Integer(this.wbmpType));
/*  93 */     addChildNode(root, "Width", new Integer(this.width));
/*  94 */     addChildNode(root, "Height", new Integer(this.height));
/*     */     
/*  96 */     return root;
/*     */   }
/*     */   
/*     */   public void setFromTree(String formatName, Node root) {
/* 100 */     throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
/*     */   }
/*     */   
/*     */   public void mergeTree(String formatName, Node root) {
/* 104 */     throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
/*     */   }
/*     */   
/*     */   public void reset() {
/* 108 */     throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private IIOMetadataNode addChildNode(IIOMetadataNode root, String name, Object object) {
/* 114 */     IIOMetadataNode child = new IIOMetadataNode(name);
/* 115 */     if (object != null) {
/* 116 */       child.setUserObject(object);
/* 117 */       child.setNodeValue(ImageUtil.convertObjectToString(object));
/*     */     } 
/* 119 */     root.appendChild(child);
/* 120 */     return child;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected IIOMetadataNode getStandardChromaNode() {
/* 126 */     IIOMetadataNode node = new IIOMetadataNode("Chroma");
/*     */     
/* 128 */     IIOMetadataNode subNode = new IIOMetadataNode("ColorSpaceType");
/* 129 */     subNode.setAttribute("name", "GRAY");
/* 130 */     node.appendChild(subNode);
/*     */     
/* 132 */     subNode = new IIOMetadataNode("NumChannels");
/* 133 */     subNode.setAttribute("value", "1");
/* 134 */     node.appendChild(subNode);
/*     */     
/* 136 */     subNode = new IIOMetadataNode("BlackIsZero");
/* 137 */     subNode.setAttribute("value", "TRUE");
/* 138 */     node.appendChild(subNode);
/*     */     
/* 140 */     return node;
/*     */   }
/*     */ 
/*     */   
/*     */   protected IIOMetadataNode getStandardDataNode() {
/* 145 */     IIOMetadataNode node = new IIOMetadataNode("Data");
/*     */     
/* 147 */     IIOMetadataNode subNode = new IIOMetadataNode("SampleFormat");
/* 148 */     subNode.setAttribute("value", "UnsignedIntegral");
/* 149 */     node.appendChild(subNode);
/*     */     
/* 151 */     subNode = new IIOMetadataNode("BitsPerSample");
/* 152 */     subNode.setAttribute("value", "1");
/* 153 */     node.appendChild(subNode);
/*     */     
/* 155 */     return node;
/*     */   }
/*     */   
/*     */   protected IIOMetadataNode getStandardDimensionNode() {
/* 159 */     IIOMetadataNode dimension_node = new IIOMetadataNode("Dimension");
/* 160 */     IIOMetadataNode node = null;
/*     */ 
/*     */ 
/*     */     
/* 164 */     node = new IIOMetadataNode("ImageOrientation");
/* 165 */     node.setAttribute("value", "Normal");
/* 166 */     dimension_node.appendChild(node);
/*     */     
/* 168 */     return dimension_node;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\wbmp\WBMPMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */