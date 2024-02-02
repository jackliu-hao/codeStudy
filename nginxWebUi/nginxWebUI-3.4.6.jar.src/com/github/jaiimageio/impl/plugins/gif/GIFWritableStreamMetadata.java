/*     */ package com.github.jaiimageio.impl.plugins.gif;
/*     */ 
/*     */ import javax.imageio.metadata.IIOInvalidTreeException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class GIFWritableStreamMetadata
/*     */   extends GIFStreamMetadata
/*     */ {
/*     */   static final String NATIVE_FORMAT_NAME = "javax_imageio_gif_stream_1.0";
/*     */   
/*     */   public GIFWritableStreamMetadata() {
/*  66 */     super(true, "javax_imageio_gif_stream_1.0", "com.github.jaiimageio.impl.plugins.gif.GIFStreamMetadataFormat", (String[])null, (String[])null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  72 */     reset();
/*     */   }
/*     */   
/*     */   public boolean isReadOnly() {
/*  76 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
/*  81 */     if (formatName.equals("javax_imageio_gif_stream_1.0")) {
/*  82 */       if (root == null) {
/*  83 */         throw new IllegalArgumentException("root == null!");
/*     */       }
/*  85 */       mergeNativeTree(root);
/*  86 */     } else if (formatName
/*  87 */       .equals("javax_imageio_1.0")) {
/*  88 */       if (root == null) {
/*  89 */         throw new IllegalArgumentException("root == null!");
/*     */       }
/*  91 */       mergeStandardTree(root);
/*     */     } else {
/*  93 */       throw new IllegalArgumentException("Not a recognized format!");
/*     */     } 
/*     */   }
/*     */   
/*     */   public void reset() {
/*  98 */     this.version = null;
/*     */     
/* 100 */     this.logicalScreenWidth = -1;
/* 101 */     this.logicalScreenHeight = -1;
/* 102 */     this.colorResolution = -1;
/* 103 */     this.pixelAspectRatio = 0;
/*     */     
/* 105 */     this.backgroundColorIndex = 0;
/* 106 */     this.sortFlag = false;
/* 107 */     this.globalColorTable = null;
/*     */   }
/*     */   
/*     */   protected void mergeNativeTree(Node root) throws IIOInvalidTreeException {
/* 111 */     Node node = root;
/* 112 */     if (!node.getNodeName().equals("javax_imageio_gif_stream_1.0")) {
/* 113 */       fatal(node, "Root must be javax_imageio_gif_stream_1.0");
/*     */     }
/*     */     
/* 116 */     node = node.getFirstChild();
/* 117 */     while (node != null) {
/* 118 */       String name = node.getNodeName();
/*     */       
/* 120 */       if (name.equals("Version")) {
/* 121 */         this.version = getStringAttribute(node, "value", null, true, versionStrings);
/*     */       }
/* 123 */       else if (name.equals("LogicalScreenDescriptor")) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 129 */         this.logicalScreenWidth = getIntAttribute(node, "logicalScreenWidth", -1, true, true, 1, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 135 */         this.logicalScreenHeight = getIntAttribute(node, "logicalScreenHeight", -1, true, true, 1, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 141 */         this.colorResolution = getIntAttribute(node, "colorResolution", -1, true, true, 1, 8);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 147 */         this.pixelAspectRatio = getIntAttribute(node, "pixelAspectRatio", 0, true, true, 0, 255);
/*     */ 
/*     */       
/*     */       }
/* 151 */       else if (name.equals("GlobalColorTable")) {
/*     */         
/* 153 */         int sizeOfGlobalColorTable = getIntAttribute(node, "sizeOfGlobalColorTable", true, 2, 256);
/*     */         
/* 155 */         if (sizeOfGlobalColorTable != 2 && sizeOfGlobalColorTable != 4 && sizeOfGlobalColorTable != 8 && sizeOfGlobalColorTable != 16 && sizeOfGlobalColorTable != 32 && sizeOfGlobalColorTable != 64 && sizeOfGlobalColorTable != 128 && sizeOfGlobalColorTable != 256)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 163 */           fatal(node, "Bad value for GlobalColorTable attribute sizeOfGlobalColorTable!");
/*     */         }
/*     */ 
/*     */         
/* 167 */         this.backgroundColorIndex = getIntAttribute(node, "backgroundColorIndex", 0, true, true, 0, 255);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 172 */         this.sortFlag = getBooleanAttribute(node, "sortFlag", false, true);
/*     */         
/* 174 */         this.globalColorTable = getColorTable(node, "ColorTableEntry", true, sizeOfGlobalColorTable);
/*     */       } else {
/*     */         
/* 177 */         fatal(node, "Unknown child of root node!");
/*     */       } 
/*     */       
/* 180 */       node = node.getNextSibling();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void mergeStandardTree(Node root) throws IIOInvalidTreeException {
/* 186 */     Node node = root;
/*     */     
/* 188 */     if (!node.getNodeName().equals("javax_imageio_1.0")) {
/* 189 */       fatal(node, "Root must be javax_imageio_1.0");
/*     */     }
/*     */ 
/*     */     
/* 193 */     node = node.getFirstChild();
/* 194 */     while (node != null) {
/* 195 */       String name = node.getNodeName();
/*     */       
/* 197 */       if (name.equals("Chroma")) {
/* 198 */         Node childNode = node.getFirstChild();
/* 199 */         while (childNode != null) {
/* 200 */           String childName = childNode.getNodeName();
/* 201 */           if (childName.equals("Palette")) {
/* 202 */             this.globalColorTable = getColorTable(childNode, "PaletteEntry", false, -1);
/*     */ 
/*     */           
/*     */           }
/* 206 */           else if (childName.equals("BackgroundIndex")) {
/* 207 */             this.backgroundColorIndex = getIntAttribute(childNode, "value", -1, true, true, 0, 255);
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 212 */           childNode = childNode.getNextSibling();
/*     */         } 
/* 214 */       } else if (name.equals("Data")) {
/* 215 */         Node childNode = node.getFirstChild();
/* 216 */         while (childNode != null) {
/* 217 */           String childName = childNode.getNodeName();
/* 218 */           if (childName.equals("BitsPerSample")) {
/* 219 */             this.colorResolution = getIntAttribute(childNode, "value", -1, true, true, 1, 8);
/*     */ 
/*     */             
/*     */             break;
/*     */           } 
/*     */           
/* 225 */           childNode = childNode.getNextSibling();
/*     */         } 
/* 227 */       } else if (name.equals("Dimension")) {
/* 228 */         Node childNode = node.getFirstChild();
/* 229 */         while (childNode != null) {
/* 230 */           String childName = childNode.getNodeName();
/* 231 */           if (childName.equals("PixelAspectRatio")) {
/* 232 */             float aspectRatio = getFloatAttribute(childNode, "value");
/*     */             
/* 234 */             if (aspectRatio == 1.0F) {
/* 235 */               this.pixelAspectRatio = 0;
/*     */             } else {
/* 237 */               int ratio = (int)(aspectRatio * 64.0F - 15.0F);
/* 238 */               this
/* 239 */                 .pixelAspectRatio = Math.max(Math.min(ratio, 255), 0);
/*     */             } 
/* 241 */           } else if (childName.equals("HorizontalScreenSize")) {
/* 242 */             this.logicalScreenWidth = getIntAttribute(childNode, "value", -1, true, true, 1, 65535);
/*     */ 
/*     */           
/*     */           }
/* 246 */           else if (childName.equals("VerticalScreenSize")) {
/* 247 */             this.logicalScreenHeight = getIntAttribute(childNode, "value", -1, true, true, 1, 65535);
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 252 */           childNode = childNode.getNextSibling();
/*     */         } 
/* 254 */       } else if (name.equals("Document")) {
/* 255 */         Node childNode = node.getFirstChild();
/* 256 */         while (childNode != null) {
/* 257 */           String childName = childNode.getNodeName();
/* 258 */           if (childName.equals("FormatVersion")) {
/*     */             
/* 260 */             String formatVersion = getStringAttribute(childNode, "value", null, true, null);
/*     */             
/* 262 */             for (int i = 0; i < versionStrings.length; i++) {
/* 263 */               if (formatVersion.equals(versionStrings[i])) {
/* 264 */                 this.version = formatVersion;
/*     */                 break;
/*     */               } 
/*     */             } 
/*     */             break;
/*     */           } 
/* 270 */           childNode = childNode.getNextSibling();
/*     */         } 
/*     */       } 
/*     */       
/* 274 */       node = node.getNextSibling();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFromTree(String formatName, Node root) throws IIOInvalidTreeException {
/* 281 */     reset();
/* 282 */     mergeTree(formatName, root);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\gif\GIFWritableStreamMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */