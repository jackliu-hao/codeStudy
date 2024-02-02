/*     */ package com.github.jaiimageio.impl.plugins.gif;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import javax.imageio.metadata.IIOInvalidTreeException;
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
/*     */ class GIFWritableImageMetadata
/*     */   extends GIFImageMetadata
/*     */ {
/*     */   static final String NATIVE_FORMAT_NAME = "javax_imageio_gif_image_1.0";
/*     */   
/*     */   GIFWritableImageMetadata() {
/*  65 */     super(true, "javax_imageio_gif_image_1.0", "com.github.jaiimageio.impl.plugins.gif.GIFImageMetadataFormat", (String[])null, (String[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/*  72 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/*  77 */     this.imageLeftPosition = 0;
/*  78 */     this.imageTopPosition = 0;
/*  79 */     this.imageWidth = 0;
/*  80 */     this.imageHeight = 0;
/*  81 */     this.interlaceFlag = false;
/*  82 */     this.sortFlag = false;
/*  83 */     this.localColorTable = null;
/*     */ 
/*     */     
/*  86 */     this.disposalMethod = 0;
/*  87 */     this.userInputFlag = false;
/*  88 */     this.transparentColorFlag = false;
/*  89 */     this.delayTime = 0;
/*  90 */     this.transparentColorIndex = 0;
/*     */ 
/*     */     
/*  93 */     this.hasPlainTextExtension = false;
/*  94 */     this.textGridLeft = 0;
/*  95 */     this.textGridTop = 0;
/*  96 */     this.textGridWidth = 0;
/*  97 */     this.textGridHeight = 0;
/*  98 */     this.characterCellWidth = 0;
/*  99 */     this.characterCellHeight = 0;
/* 100 */     this.textForegroundColor = 0;
/* 101 */     this.textBackgroundColor = 0;
/* 102 */     this.text = null;
/*     */ 
/*     */     
/* 105 */     this.applicationIDs = null;
/* 106 */     this.authenticationCodes = null;
/* 107 */     this.applicationData = null;
/*     */ 
/*     */ 
/*     */     
/* 111 */     this.comments = null;
/*     */   }
/*     */   
/*     */   private byte[] fromISO8859(String data) {
/*     */     try {
/* 116 */       return data.getBytes("ISO-8859-1");
/* 117 */     } catch (UnsupportedEncodingException e) {
/* 118 */       return (new String("")).getBytes();
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void mergeNativeTree(Node root) throws IIOInvalidTreeException {
/* 123 */     Node node = root;
/* 124 */     if (!node.getNodeName().equals("javax_imageio_gif_image_1.0")) {
/* 125 */       fatal(node, "Root must be javax_imageio_gif_image_1.0");
/*     */     }
/*     */     
/* 128 */     node = node.getFirstChild();
/* 129 */     while (node != null) {
/* 130 */       String name = node.getNodeName();
/*     */       
/* 132 */       if (name.equals("ImageDescriptor")) {
/* 133 */         this.imageLeftPosition = getIntAttribute(node, "imageLeftPosition", -1, true, true, 0, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 138 */         this.imageTopPosition = getIntAttribute(node, "imageTopPosition", -1, true, true, 0, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 143 */         this.imageWidth = getIntAttribute(node, "imageWidth", -1, true, true, 1, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 148 */         this.imageHeight = getIntAttribute(node, "imageHeight", -1, true, true, 1, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 153 */         this.interlaceFlag = getBooleanAttribute(node, "interlaceFlag", false, true);
/*     */       }
/* 155 */       else if (name.equals("LocalColorTable")) {
/*     */         
/* 157 */         int sizeOfLocalColorTable = getIntAttribute(node, "sizeOfLocalColorTable", true, 2, 256);
/*     */         
/* 159 */         if (sizeOfLocalColorTable != 2 && sizeOfLocalColorTable != 4 && sizeOfLocalColorTable != 8 && sizeOfLocalColorTable != 16 && sizeOfLocalColorTable != 32 && sizeOfLocalColorTable != 64 && sizeOfLocalColorTable != 128 && sizeOfLocalColorTable != 256)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 167 */           fatal(node, "Bad value for LocalColorTable attribute sizeOfLocalColorTable!");
/*     */         }
/*     */ 
/*     */         
/* 171 */         this.sortFlag = getBooleanAttribute(node, "sortFlag", false, true);
/*     */         
/* 173 */         this.localColorTable = getColorTable(node, "ColorTableEntry", true, sizeOfLocalColorTable);
/*     */       }
/* 175 */       else if (name.equals("GraphicControlExtension")) {
/*     */         
/* 177 */         String disposalMethodName = getStringAttribute(node, "disposalMethod", null, true, disposalMethodNames);
/*     */         
/* 179 */         this.disposalMethod = 0;
/* 180 */         while (!disposalMethodName.equals(disposalMethodNames[this.disposalMethod])) {
/* 181 */           this.disposalMethod++;
/*     */         }
/*     */         
/* 184 */         this.userInputFlag = getBooleanAttribute(node, "userInputFlag", false, true);
/*     */ 
/*     */         
/* 187 */         this
/* 188 */           .transparentColorFlag = getBooleanAttribute(node, "transparentColorFlag", false, true);
/*     */ 
/*     */         
/* 191 */         this.delayTime = getIntAttribute(node, "delayTime", -1, true, true, 0, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 196 */         this
/* 197 */           .transparentColorIndex = getIntAttribute(node, "transparentColorIndex", -1, true, true, 0, 65535);
/*     */       
/*     */       }
/* 200 */       else if (name.equals("PlainTextExtension")) {
/* 201 */         this.hasPlainTextExtension = true;
/*     */         
/* 203 */         this.textGridLeft = getIntAttribute(node, "textGridLeft", -1, true, true, 0, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 208 */         this.textGridTop = getIntAttribute(node, "textGridTop", -1, true, true, 0, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 213 */         this.textGridWidth = getIntAttribute(node, "textGridWidth", -1, true, true, 1, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 218 */         this.textGridHeight = getIntAttribute(node, "textGridHeight", -1, true, true, 1, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 223 */         this.characterCellWidth = getIntAttribute(node, "characterCellWidth", -1, true, true, 1, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 228 */         this.characterCellHeight = getIntAttribute(node, "characterCellHeight", -1, true, true, 1, 65535);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 233 */         this.textForegroundColor = getIntAttribute(node, "textForegroundColor", -1, true, true, 0, 255);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 238 */         this.textBackgroundColor = getIntAttribute(node, "textBackgroundColor", -1, true, true, 0, 255);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 250 */         String textString = getStringAttribute(node, "text", "", false, null);
/* 251 */         this.text = fromISO8859(textString);
/* 252 */       } else if (name.equals("ApplicationExtensions")) {
/*     */         
/* 254 */         IIOMetadataNode applicationExtension = (IIOMetadataNode)node.getFirstChild();
/*     */         
/* 256 */         if (!applicationExtension.getNodeName().equals("ApplicationExtension")) {
/* 257 */           fatal(node, "Only a ApplicationExtension may be a child of a ApplicationExtensions!");
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 262 */         String applicationIDString = getStringAttribute(applicationExtension, "applicationID", null, true, null);
/*     */ 
/*     */ 
/*     */         
/* 266 */         String authenticationCodeString = getStringAttribute(applicationExtension, "authenticationCode", null, true, null);
/*     */ 
/*     */ 
/*     */         
/* 270 */         Object applicationExtensionData = applicationExtension.getUserObject();
/* 271 */         if (applicationExtensionData == null || !(applicationExtensionData instanceof byte[]))
/*     */         {
/* 273 */           fatal(applicationExtension, "Bad user object in ApplicationExtension!");
/*     */         }
/*     */ 
/*     */         
/* 277 */         if (this.applicationIDs == null) {
/* 278 */           this.applicationIDs = new ArrayList();
/* 279 */           this.authenticationCodes = new ArrayList();
/* 280 */           this.applicationData = new ArrayList();
/*     */         } 
/*     */         
/* 283 */         this.applicationIDs.add(fromISO8859(applicationIDString));
/* 284 */         this.authenticationCodes.add(fromISO8859(authenticationCodeString));
/* 285 */         this.applicationData.add(applicationExtensionData);
/* 286 */       } else if (name.equals("CommentExtensions")) {
/* 287 */         Node commentExtension = node.getFirstChild();
/* 288 */         if (commentExtension != null) {
/* 289 */           while (commentExtension != null) {
/* 290 */             if (!commentExtension.getNodeName().equals("CommentExtension")) {
/* 291 */               fatal(node, "Only a CommentExtension may be a child of a CommentExtensions!");
/*     */             }
/*     */ 
/*     */             
/* 295 */             if (this.comments == null) {
/* 296 */               this.comments = new ArrayList();
/*     */             }
/*     */ 
/*     */             
/* 300 */             String comment = getStringAttribute(commentExtension, "value", null, true, null);
/*     */ 
/*     */             
/* 303 */             this.comments.add(fromISO8859(comment));
/*     */             
/* 305 */             commentExtension = commentExtension.getNextSibling();
/*     */           } 
/*     */         }
/*     */       } else {
/* 309 */         fatal(node, "Unknown child of root node!");
/*     */       } 
/*     */       
/* 312 */       node = node.getNextSibling();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void mergeStandardTree(Node root) throws IIOInvalidTreeException {
/* 318 */     Node node = root;
/*     */     
/* 320 */     if (!node.getNodeName().equals("javax_imageio_1.0")) {
/* 321 */       fatal(node, "Root must be javax_imageio_1.0");
/*     */     }
/*     */ 
/*     */     
/* 325 */     node = node.getFirstChild();
/* 326 */     while (node != null) {
/* 327 */       String name = node.getNodeName();
/*     */       
/* 329 */       if (name.equals("Chroma")) {
/* 330 */         Node childNode = node.getFirstChild();
/* 331 */         while (childNode != null) {
/* 332 */           String childName = childNode.getNodeName();
/* 333 */           if (childName.equals("Palette")) {
/* 334 */             this.localColorTable = getColorTable(childNode, "PaletteEntry", false, -1);
/*     */             
/*     */             break;
/*     */           } 
/*     */           
/* 339 */           childNode = childNode.getNextSibling();
/*     */         } 
/* 341 */       } else if (name.equals("Compression")) {
/* 342 */         Node childNode = node.getFirstChild();
/* 343 */         while (childNode != null) {
/* 344 */           String childName = childNode.getNodeName();
/* 345 */           if (childName.equals("NumProgressiveScans")) {
/*     */             
/* 347 */             int numProgressiveScans = getIntAttribute(childNode, "value", 4, false, true, 1, 2147483647);
/*     */             
/* 349 */             if (numProgressiveScans > 1) {
/* 350 */               this.interlaceFlag = true;
/*     */             }
/*     */             break;
/*     */           } 
/* 354 */           childNode = childNode.getNextSibling();
/*     */         } 
/* 356 */       } else if (name.equals("Dimension")) {
/* 357 */         Node childNode = node.getFirstChild();
/* 358 */         while (childNode != null) {
/* 359 */           String childName = childNode.getNodeName();
/* 360 */           if (childName.equals("HorizontalPixelOffset")) {
/* 361 */             this.imageLeftPosition = getIntAttribute(childNode, "value", -1, true, true, 0, 65535);
/*     */ 
/*     */           
/*     */           }
/* 365 */           else if (childName.equals("VerticalPixelOffset")) {
/* 366 */             this.imageTopPosition = getIntAttribute(childNode, "value", -1, true, true, 0, 65535);
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 371 */           childNode = childNode.getNextSibling();
/*     */         } 
/* 373 */       } else if (name.equals("Text")) {
/* 374 */         Node childNode = node.getFirstChild();
/* 375 */         while (childNode != null) {
/* 376 */           String childName = childNode.getNodeName();
/* 377 */           if (childName.equals("TextEntry") && 
/* 378 */             getAttribute(childNode, "compression", "none", false)
/* 379 */             .equals("none") && 
/* 380 */             Charset.isSupported(getAttribute(childNode, "encoding", "ISO-8859-1", false))) {
/*     */ 
/*     */ 
/*     */             
/* 384 */             String value = getAttribute(childNode, "value");
/* 385 */             byte[] comment = fromISO8859(value);
/* 386 */             if (this.comments == null) {
/* 387 */               this.comments = new ArrayList();
/*     */             }
/* 389 */             this.comments.add(comment);
/*     */           } 
/* 391 */           childNode = childNode.getNextSibling();
/*     */         } 
/* 393 */       } else if (name.equals("Transparency")) {
/* 394 */         Node childNode = node.getFirstChild();
/* 395 */         while (childNode != null) {
/* 396 */           String childName = childNode.getNodeName();
/* 397 */           if (childName.equals("TransparentIndex")) {
/* 398 */             this.transparentColorIndex = getIntAttribute(childNode, "value", -1, true, true, 0, 255);
/*     */ 
/*     */ 
/*     */             
/* 402 */             this.transparentColorFlag = true;
/*     */             break;
/*     */           } 
/* 405 */           childNode = childNode.getNextSibling();
/*     */         } 
/*     */       } 
/*     */       
/* 409 */       node = node.getNextSibling();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFromTree(String formatName, Node root) throws IIOInvalidTreeException {
/* 416 */     reset();
/* 417 */     mergeTree(formatName, root);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\gif\GIFWritableImageMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */