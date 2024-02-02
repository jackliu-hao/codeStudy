/*     */ package com.github.jaiimageio.impl.plugins.pcx;
/*     */ 
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
/*     */ public class PCXMetadata
/*     */   extends IIOMetadata
/*     */   implements Cloneable, PCXConstants
/*     */ {
/*     */   short version;
/*     */   byte bitsPerPixel;
/*     */   boolean gotxmin;
/*     */   boolean gotymin;
/*     */   short xmin;
/*     */   short ymin;
/*     */   int vdpi;
/*     */   int hdpi;
/*     */   int hsize;
/*     */   int vsize;
/*     */   
/*     */   PCXMetadata() {
/*  65 */     super(true, null, null, null, null);
/*  66 */     reset();
/*     */   }
/*     */   
/*     */   public Node getAsTree(String formatName) {
/*  70 */     if (formatName.equals("javax_imageio_1.0")) {
/*  71 */       return getStandardTree();
/*     */     }
/*  73 */     throw new IllegalArgumentException("Not a recognized format!");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/*  78 */     return false;
/*     */   }
/*     */   
/*     */   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
/*  82 */     if (formatName.equals("javax_imageio_1.0")) {
/*  83 */       if (root == null) {
/*  84 */         throw new IllegalArgumentException("root == null!");
/*     */       }
/*  86 */       mergeStandardTree(root);
/*     */     } else {
/*  88 */       throw new IllegalArgumentException("Not a recognized format!");
/*     */     } 
/*     */   }
/*     */   
/*     */   public void reset() {
/*  93 */     this.version = 5;
/*  94 */     this.bitsPerPixel = 0;
/*  95 */     this.gotxmin = false;
/*  96 */     this.gotymin = false;
/*  97 */     this.xmin = 0;
/*  98 */     this.ymin = 0;
/*  99 */     this.vdpi = 72;
/* 100 */     this.hdpi = 72;
/* 101 */     this.hsize = 0;
/* 102 */     this.vsize = 0;
/*     */   }
/*     */   
/*     */   public IIOMetadataNode getStandardDocumentNode() {
/*     */     String versionString;
/* 107 */     switch (this.version) {
/*     */       case 0:
/* 109 */         versionString = "2.5";
/*     */         break;
/*     */       case 2:
/* 112 */         versionString = "2.8 with palette";
/*     */         break;
/*     */       case 3:
/* 115 */         versionString = "2.8 without palette";
/*     */         break;
/*     */       case 4:
/* 118 */         versionString = "PC Paintbrush for Windows";
/*     */         break;
/*     */       case 5:
/* 121 */         versionString = "3.0";
/*     */         break;
/*     */       
/*     */       default:
/* 125 */         versionString = null;
/*     */         break;
/*     */     } 
/* 128 */     IIOMetadataNode documentNode = null;
/* 129 */     if (versionString != null) {
/* 130 */       documentNode = new IIOMetadataNode("Document");
/* 131 */       IIOMetadataNode node = new IIOMetadataNode("FormatVersion");
/* 132 */       node.setAttribute("value", versionString);
/* 133 */       documentNode.appendChild(node);
/*     */     } 
/*     */     
/* 136 */     return documentNode;
/*     */   }
/*     */   
/*     */   public IIOMetadataNode getStandardDimensionNode() {
/* 140 */     IIOMetadataNode dimensionNode = new IIOMetadataNode("Dimension");
/* 141 */     IIOMetadataNode node = null;
/*     */     
/* 143 */     node = new IIOMetadataNode("HorizontalPixelOffset");
/* 144 */     node.setAttribute("value", String.valueOf(this.xmin));
/* 145 */     dimensionNode.appendChild(node);
/*     */     
/* 147 */     node = new IIOMetadataNode("VerticalPixelOffset");
/* 148 */     node.setAttribute("value", String.valueOf(this.ymin));
/* 149 */     dimensionNode.appendChild(node);
/*     */     
/* 151 */     node = new IIOMetadataNode("HorizontalPixelSize");
/* 152 */     node.setAttribute("value", String.valueOf(254.0D / this.hdpi));
/* 153 */     dimensionNode.appendChild(node);
/*     */     
/* 155 */     node = new IIOMetadataNode("VerticalPixelSize");
/* 156 */     node.setAttribute("value", String.valueOf(254.0D / this.vdpi));
/* 157 */     dimensionNode.appendChild(node);
/*     */     
/* 159 */     if (this.hsize != 0) {
/* 160 */       node = new IIOMetadataNode("HorizontalScreenSize");
/* 161 */       node.setAttribute("value", String.valueOf(this.hsize));
/* 162 */       dimensionNode.appendChild(node);
/*     */     } 
/*     */     
/* 165 */     if (this.vsize != 0) {
/* 166 */       node = new IIOMetadataNode("VerticalScreenSize");
/* 167 */       node.setAttribute("value", String.valueOf(this.vsize));
/* 168 */       dimensionNode.appendChild(node);
/*     */     } 
/*     */     
/* 171 */     return dimensionNode;
/*     */   }
/*     */   
/*     */   private void mergeStandardTree(Node root) throws IIOInvalidTreeException {
/* 175 */     Node node = root;
/* 176 */     if (!node.getNodeName().equals("javax_imageio_1.0")) {
/* 177 */       throw new IIOInvalidTreeException("Root must be javax_imageio_1.0", node);
/*     */     }
/*     */ 
/*     */     
/* 181 */     node = node.getFirstChild();
/* 182 */     while (node != null) {
/* 183 */       String name = node.getNodeName();
/*     */       
/* 185 */       if (name.equals("Dimension")) {
/* 186 */         Node child = node.getFirstChild();
/*     */         
/* 188 */         while (child != null) {
/* 189 */           String childName = child.getNodeName();
/* 190 */           if (childName.equals("HorizontalPixelOffset")) {
/* 191 */             String hpo = getAttribute(child, "value");
/* 192 */             this.xmin = Short.valueOf(hpo).shortValue();
/* 193 */             this.gotxmin = true;
/* 194 */           } else if (childName.equals("VerticalPixelOffset")) {
/* 195 */             String vpo = getAttribute(child, "value");
/* 196 */             this.ymin = Short.valueOf(vpo).shortValue();
/* 197 */             this.gotymin = true;
/* 198 */           } else if (childName.equals("HorizontalPixelSize")) {
/* 199 */             String hps = getAttribute(child, "value");
/* 200 */             this.hdpi = (int)(254.0F / Float.parseFloat(hps) + 0.5F);
/* 201 */           } else if (childName.equals("VerticalPixelSize")) {
/* 202 */             String vps = getAttribute(child, "value");
/* 203 */             this.vdpi = (int)(254.0F / Float.parseFloat(vps) + 0.5F);
/* 204 */           } else if (childName.equals("HorizontalScreenSize")) {
/* 205 */             String hss = getAttribute(child, "value");
/* 206 */             this.hsize = Integer.valueOf(hss).intValue();
/* 207 */           } else if (childName.equals("VerticalScreenSize")) {
/* 208 */             String vss = getAttribute(child, "value");
/* 209 */             this.vsize = Integer.valueOf(vss).intValue();
/*     */           } 
/*     */           
/* 212 */           child = child.getNextSibling();
/*     */         } 
/*     */       } 
/*     */       
/* 216 */       node = node.getNextSibling();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static String getAttribute(Node node, String attrName) {
/* 221 */     NamedNodeMap attrs = node.getAttributes();
/* 222 */     Node attr = attrs.getNamedItem(attrName);
/* 223 */     return (attr != null) ? attr.getNodeValue() : null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\pcx\PCXMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */