/*     */ package com.github.jaiimageio.impl.plugins.pnm;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.ImageUtil;
/*     */ import com.github.jaiimageio.plugins.pnm.PNMImageWriteParam;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ import javax.imageio.ImageWriteParam;
/*     */ import javax.imageio.metadata.IIOInvalidTreeException;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.metadata.IIOMetadataNode;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PNMMetadata
/*     */   extends IIOMetadata
/*     */   implements Cloneable
/*     */ {
/*     */   static final String nativeMetadataFormatName = "com_sun_media_imageio_plugins_pnm_image_1.0";
/*     */   private int maxSample;
/*     */   private int width;
/*     */   private int height;
/*     */   private int variant;
/*     */   private ArrayList comments;
/*     */   private int maxSampleSize;
/*     */   
/*     */   PNMMetadata() {
/*  96 */     super(true, "com_sun_media_imageio_plugins_pnm_image_1.0", "com.github.jaiimageio.impl.plugins.pnm.PNMMetadataFormat", null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PNMMetadata(IIOMetadata metadata) throws IIOInvalidTreeException {
/* 104 */     this();
/*     */     
/* 106 */     if (metadata != null) {
/* 107 */       List<String> formats = Arrays.asList(metadata.getMetadataFormatNames());
/*     */       
/* 109 */       if (formats.contains("com_sun_media_imageio_plugins_pnm_image_1.0")) {
/*     */         
/* 111 */         setFromTree("com_sun_media_imageio_plugins_pnm_image_1.0", metadata
/* 112 */             .getAsTree("com_sun_media_imageio_plugins_pnm_image_1.0"));
/* 113 */       } else if (metadata.isStandardMetadataFormatSupported()) {
/*     */         
/* 115 */         String format = "javax_imageio_1.0";
/*     */         
/* 117 */         setFromTree(format, metadata.getAsTree(format));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   PNMMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
/* 128 */     this();
/* 129 */     initialize(imageType, param);
/*     */   }
/*     */ 
/*     */   
/*     */   void initialize(ImageTypeSpecifier imageType, ImageWriteParam param) {
/* 134 */     ImageTypeSpecifier destType = null;
/*     */     
/* 136 */     if (param != null) {
/* 137 */       destType = param.getDestinationType();
/* 138 */       if (destType == null) {
/* 139 */         destType = imageType;
/*     */       }
/*     */     } else {
/* 142 */       destType = imageType;
/*     */     } 
/*     */     
/* 145 */     if (destType != null) {
/* 146 */       SampleModel sm = destType.getSampleModel();
/* 147 */       int[] sampleSize = sm.getSampleSize();
/*     */       
/* 149 */       this.width = sm.getWidth();
/* 150 */       this.height = sm.getHeight();
/*     */       
/* 152 */       for (int i = 0; i < sampleSize.length; i++) {
/* 153 */         if (sampleSize[i] > this.maxSampleSize) {
/* 154 */           this.maxSampleSize = sampleSize[i];
/*     */         }
/*     */       } 
/* 157 */       this.maxSample = (1 << this.maxSampleSize) - 1;
/*     */       
/* 159 */       boolean isRaw = true;
/* 160 */       if (param instanceof PNMImageWriteParam) {
/* 161 */         isRaw = ((PNMImageWriteParam)param).getRaw();
/*     */       }
/*     */       
/* 164 */       if (this.maxSampleSize == 1) {
/* 165 */         this.variant = 49;
/* 166 */       } else if (sm.getNumBands() == 1) {
/* 167 */         this.variant = 50;
/* 168 */       } else if (sm.getNumBands() == 3) {
/* 169 */         this.variant = 51;
/*     */       } 
/*     */ 
/*     */       
/* 173 */       if (this.variant <= 51 && isRaw && this.maxSampleSize <= 8) {
/* 174 */         this.variant += 3;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   protected Object clone() {
/* 180 */     PNMMetadata theClone = null;
/*     */     
/*     */     try {
/* 183 */       theClone = (PNMMetadata)super.clone();
/* 184 */     } catch (CloneNotSupportedException cloneNotSupportedException) {}
/*     */     
/* 186 */     if (this.comments != null) {
/* 187 */       int numComments = this.comments.size();
/* 188 */       for (int i = 0; i < numComments; i++) {
/* 189 */         theClone.addComment(this.comments.get(i));
/*     */       }
/*     */     } 
/* 192 */     return theClone;
/*     */   }
/*     */   
/*     */   public Node getAsTree(String formatName) {
/* 196 */     if (formatName == null) {
/* 197 */       throw new IllegalArgumentException(I18N.getString("PNMMetadata0"));
/*     */     }
/*     */     
/* 200 */     if (formatName.equals("com_sun_media_imageio_plugins_pnm_image_1.0")) {
/* 201 */       return getNativeTree();
/*     */     }
/*     */     
/* 204 */     if (formatName
/* 205 */       .equals("javax_imageio_1.0")) {
/* 206 */       return getStandardTree();
/*     */     }
/*     */     
/* 209 */     throw new IllegalArgumentException(I18N.getString("PNMMetadata1") + " " + formatName);
/*     */   }
/*     */ 
/*     */   
/*     */   IIOMetadataNode getNativeTree() {
/* 214 */     IIOMetadataNode root = new IIOMetadataNode("com_sun_media_imageio_plugins_pnm_image_1.0");
/*     */ 
/*     */     
/* 217 */     IIOMetadataNode child = new IIOMetadataNode("FormatName");
/* 218 */     child.setUserObject(getFormatName());
/* 219 */     child.setNodeValue(getFormatName());
/* 220 */     root.appendChild(child);
/*     */     
/* 222 */     child = new IIOMetadataNode("Variant");
/* 223 */     child.setUserObject(getVariant());
/* 224 */     child.setNodeValue(getVariant());
/* 225 */     root.appendChild(child);
/*     */     
/* 227 */     child = new IIOMetadataNode("Width");
/* 228 */     Object tmp = new Integer(this.width);
/* 229 */     child.setUserObject(tmp);
/* 230 */     child.setNodeValue(ImageUtil.convertObjectToString(tmp));
/* 231 */     root.appendChild(child);
/*     */     
/* 233 */     child = new IIOMetadataNode("Height");
/* 234 */     tmp = new Integer(this.height);
/* 235 */     child.setUserObject(tmp);
/* 236 */     child.setNodeValue(ImageUtil.convertObjectToString(tmp));
/* 237 */     root.appendChild(child);
/*     */     
/* 239 */     child = new IIOMetadataNode("MaximumSample");
/* 240 */     tmp = new Byte((byte)this.maxSample);
/* 241 */     child.setUserObject(tmp);
/* 242 */     child.setNodeValue(ImageUtil.convertObjectToString(new Integer(this.maxSample)));
/* 243 */     root.appendChild(child);
/*     */     
/* 245 */     if (this.comments != null) {
/* 246 */       for (int i = 0; i < this.comments.size(); i++) {
/* 247 */         child = new IIOMetadataNode("Comment");
/* 248 */         tmp = this.comments.get(i);
/* 249 */         child.setUserObject(tmp);
/* 250 */         child.setNodeValue(ImageUtil.convertObjectToString(tmp));
/* 251 */         root.appendChild(child);
/*     */       } 
/*     */     }
/*     */     
/* 255 */     return root;
/*     */   }
/*     */ 
/*     */   
/*     */   protected IIOMetadataNode getStandardChromaNode() {
/* 260 */     IIOMetadataNode node = new IIOMetadataNode("Chroma");
/*     */     
/* 262 */     int temp = (this.variant - 49) % 3 + 1;
/*     */     
/* 264 */     IIOMetadataNode subNode = new IIOMetadataNode("ColorSpaceType");
/* 265 */     if (temp == 3) {
/* 266 */       subNode.setAttribute("name", "RGB");
/*     */     } else {
/* 268 */       subNode.setAttribute("name", "GRAY");
/*     */     } 
/* 270 */     node.appendChild(subNode);
/*     */     
/* 272 */     subNode = new IIOMetadataNode("NumChannels");
/* 273 */     subNode.setAttribute("value", "" + ((temp == 3) ? 3 : 1));
/* 274 */     node.appendChild(subNode);
/*     */     
/* 276 */     if (temp != 3) {
/* 277 */       subNode = new IIOMetadataNode("BlackIsZero");
/* 278 */       subNode.setAttribute("value", "TRUE");
/* 279 */       node.appendChild(subNode);
/*     */     } 
/*     */     
/* 282 */     return node;
/*     */   }
/*     */   
/*     */   protected IIOMetadataNode getStandardDataNode() {
/* 286 */     IIOMetadataNode node = new IIOMetadataNode("Data");
/*     */     
/* 288 */     IIOMetadataNode subNode = new IIOMetadataNode("SampleFormat");
/* 289 */     subNode.setAttribute("value", "UnsignedIntegral");
/* 290 */     node.appendChild(subNode);
/*     */     
/* 292 */     int temp = (this.variant - 49) % 3 + 1;
/* 293 */     subNode = new IIOMetadataNode("BitsPerSample");
/* 294 */     if (temp == 1) {
/* 295 */       subNode.setAttribute("value", "1");
/* 296 */     } else if (temp == 2) {
/* 297 */       subNode.setAttribute("value", "8");
/*     */     } else {
/* 299 */       subNode.setAttribute("value", "8 8 8");
/*     */     } 
/* 301 */     node.appendChild(subNode);
/*     */     
/* 303 */     subNode = new IIOMetadataNode("SignificantBitsPerSample");
/* 304 */     if (temp == 1 || temp == 2) {
/* 305 */       subNode.setAttribute("value", "" + this.maxSampleSize);
/*     */     } else {
/* 307 */       subNode.setAttribute("value", this.maxSampleSize + " " + this.maxSampleSize + " " + this.maxSampleSize);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 312 */     node.appendChild(subNode);
/*     */     
/* 314 */     return node;
/*     */   }
/*     */   
/*     */   protected IIOMetadataNode getStandardDimensionNode() {
/* 318 */     IIOMetadataNode node = new IIOMetadataNode("Dimension");
/*     */     
/* 320 */     IIOMetadataNode subNode = new IIOMetadataNode("ImageOrientation");
/* 321 */     subNode.setAttribute("value", "Normal");
/* 322 */     node.appendChild(subNode);
/*     */     
/* 324 */     return node;
/*     */   }
/*     */   
/*     */   protected IIOMetadataNode getStandardTextNode() {
/* 328 */     if (this.comments != null) {
/* 329 */       IIOMetadataNode node = new IIOMetadataNode("Text");
/* 330 */       Iterator<String> iter = this.comments.iterator();
/* 331 */       while (iter.hasNext()) {
/* 332 */         String comment = iter.next();
/* 333 */         IIOMetadataNode subNode = new IIOMetadataNode("TextEntry");
/* 334 */         subNode.setAttribute("keyword", "comment");
/* 335 */         subNode.setAttribute("value", comment);
/* 336 */         node.appendChild(subNode);
/*     */       } 
/* 338 */       return node;
/*     */     } 
/* 340 */     return null;
/*     */   }
/*     */   
/*     */   public boolean isReadOnly() {
/* 344 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
/* 349 */     if (formatName == null) {
/* 350 */       throw new IllegalArgumentException(I18N.getString("PNMMetadata0"));
/*     */     }
/*     */     
/* 353 */     if (root == null) {
/* 354 */       throw new IllegalArgumentException(I18N.getString("PNMMetadata2"));
/*     */     }
/*     */     
/* 357 */     if (formatName.equals("com_sun_media_imageio_plugins_pnm_image_1.0") && root
/* 358 */       .getNodeName().equals("com_sun_media_imageio_plugins_pnm_image_1.0")) {
/* 359 */       mergeNativeTree(root);
/* 360 */     } else if (formatName
/* 361 */       .equals("javax_imageio_1.0")) {
/* 362 */       mergeStandardTree(root);
/*     */     } else {
/* 364 */       throw new IllegalArgumentException(I18N.getString("PNMMetadata1") + " " + formatName);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFromTree(String formatName, Node root) throws IIOInvalidTreeException {
/* 371 */     if (formatName == null) {
/* 372 */       throw new IllegalArgumentException(I18N.getString("PNMMetadata0"));
/*     */     }
/*     */     
/* 375 */     if (root == null) {
/* 376 */       throw new IllegalArgumentException(I18N.getString("PNMMetadata2"));
/*     */     }
/*     */     
/* 379 */     if (formatName.equals("com_sun_media_imageio_plugins_pnm_image_1.0") && root
/* 380 */       .getNodeName().equals("com_sun_media_imageio_plugins_pnm_image_1.0")) {
/* 381 */       mergeNativeTree(root);
/* 382 */     } else if (formatName
/* 383 */       .equals("javax_imageio_1.0")) {
/* 384 */       mergeStandardTree(root);
/*     */     } else {
/* 386 */       throw new IllegalArgumentException(I18N.getString("PNMMetadata2") + " " + formatName);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 392 */     this.maxSample = this.width = this.height = this.variant = this.maxSampleSize = 0;
/* 393 */     this.comments = null;
/*     */   }
/*     */   
/*     */   public String getFormatName() {
/* 397 */     int v = (this.variant - 49) % 3 + 1;
/* 398 */     if (v == 1)
/* 399 */       return "PBM"; 
/* 400 */     if (v == 2)
/* 401 */       return "PGM"; 
/* 402 */     if (v == 3)
/* 403 */       return "PPM"; 
/* 404 */     return null;
/*     */   }
/*     */   
/*     */   public String getVariant() {
/* 408 */     if (this.variant > 51)
/* 409 */       return "RAWBITS"; 
/* 410 */     return "ASCII";
/*     */   }
/*     */   
/*     */   boolean isRaw() {
/* 414 */     return getVariant().equals("RAWBITS");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setVariant(int v) {
/* 419 */     this.variant = v;
/*     */   }
/*     */   
/*     */   public void setWidth(int w) {
/* 423 */     this.width = w;
/*     */   }
/*     */   
/*     */   public void setHeight(int h) {
/* 427 */     this.height = h;
/*     */   }
/*     */   
/*     */   int getMaxBitDepth() {
/* 431 */     return this.maxSampleSize;
/*     */   }
/*     */   
/*     */   int getMaxValue() {
/* 435 */     return this.maxSample;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxBitDepth(int maxValue) {
/* 443 */     this.maxSample = maxValue;
/*     */     
/* 445 */     this.maxSampleSize = 0;
/* 446 */     while (maxValue > 0) {
/* 447 */       maxValue >>>= 1;
/* 448 */       this.maxSampleSize++;
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized void addComment(String comment) {
/* 453 */     if (this.comments == null) {
/* 454 */       this.comments = new ArrayList();
/*     */     }
/* 456 */     comment = comment.replaceAll("[\n\r\f]", " ");
/* 457 */     this.comments.add(comment);
/*     */   }
/*     */   
/*     */   Iterator getComments() {
/* 461 */     return (this.comments == null) ? null : this.comments.iterator();
/*     */   }
/*     */   
/*     */   private void mergeNativeTree(Node root) throws IIOInvalidTreeException {
/* 465 */     NodeList list = root.getChildNodes();
/* 466 */     String format = null;
/* 467 */     String var = null;
/*     */     
/* 469 */     for (int i = list.getLength() - 1; i >= 0; i--) {
/* 470 */       IIOMetadataNode node = (IIOMetadataNode)list.item(i);
/* 471 */       String name = node.getNodeName();
/*     */       
/* 473 */       if (name.equals("Comment")) {
/* 474 */         addComment((String)node.getUserObject());
/* 475 */       } else if (name.equals("Width")) {
/* 476 */         this.width = ((Integer)node.getUserObject()).intValue();
/* 477 */       } else if (name.equals("Height")) {
/* 478 */         this.width = ((Integer)node.getUserObject()).intValue();
/* 479 */       } else if (name.equals("MaximumSample")) {
/* 480 */         int maxValue = ((Integer)node.getUserObject()).intValue();
/* 481 */         setMaxBitDepth(maxValue);
/* 482 */       } else if (name.equals("FormatName")) {
/* 483 */         format = (String)node.getUserObject();
/* 484 */       } else if (name.equals("Variant")) {
/* 485 */         var = (String)node.getUserObject();
/*     */       } 
/*     */     } 
/*     */     
/* 489 */     if (format.equals("PBM")) {
/* 490 */       this.variant = 49;
/* 491 */     } else if (format.equals("PGM")) {
/* 492 */       this.variant = 50;
/* 493 */     } else if (format.equals("PPM")) {
/* 494 */       this.variant = 51;
/*     */     } 
/* 496 */     if (var.equals("RAWBITS"))
/* 497 */       this.variant += 3; 
/*     */   }
/*     */   
/*     */   private void mergeStandardTree(Node root) throws IIOInvalidTreeException {
/* 501 */     NodeList children = root.getChildNodes();
/*     */     
/* 503 */     String colorSpace = null;
/* 504 */     int numComps = 0;
/* 505 */     int[] bitsPerSample = null;
/*     */     
/* 507 */     for (int i = 0; i < children.getLength(); i++) {
/* 508 */       Node node = children.item(i);
/* 509 */       String name = node.getNodeName();
/* 510 */       if (name.equals("Chroma")) {
/* 511 */         NodeList children1 = node.getChildNodes();
/* 512 */         for (int j = 0; j < children1.getLength(); j++) {
/* 513 */           Node child = children1.item(j);
/* 514 */           String name1 = child.getNodeName();
/*     */           
/* 516 */           if (name1.equals("NumChannels")) {
/* 517 */             String s = (String)getAttribute(child, "value");
/* 518 */             numComps = (new Integer(s)).intValue();
/* 519 */           } else if (name1.equals("ColorSpaceType")) {
/* 520 */             colorSpace = (String)getAttribute(child, "name");
/*     */           } 
/*     */         } 
/* 523 */       } else if (!name.equals("Compression")) {
/*     */         
/* 525 */         if (name.equals("Data")) {
/* 526 */           NodeList children1 = node.getChildNodes();
/* 527 */           int maxBitDepth = -1;
/* 528 */           for (int j = 0; j < children1.getLength(); j++) {
/* 529 */             Node child = children1.item(j);
/* 530 */             String name1 = child.getNodeName();
/*     */             
/* 532 */             if (name1.equals("BitsPerSample")) {
/* 533 */               List<Integer> bps = new ArrayList(3);
/* 534 */               String s = (String)getAttribute(child, "value");
/* 535 */               StringTokenizer t = new StringTokenizer(s);
/* 536 */               while (t.hasMoreTokens()) {
/* 537 */                 bps.add(Integer.valueOf(t.nextToken()));
/*     */               }
/* 539 */               bitsPerSample = new int[bps.size()];
/* 540 */               for (int k = 0; k < bitsPerSample.length; k++) {
/* 541 */                 bitsPerSample[k] = ((Integer)bps
/* 542 */                   .get(k)).intValue();
/*     */               }
/* 544 */             } else if (name1.equals("SignificantBitsPerSample")) {
/* 545 */               String s = (String)getAttribute(child, "value");
/* 546 */               StringTokenizer t = new StringTokenizer(s);
/* 547 */               while (t.hasMoreTokens()) {
/*     */                 
/* 549 */                 int sbps = Integer.valueOf(t.nextToken()).intValue();
/* 550 */                 maxBitDepth = Math.max(sbps, maxBitDepth);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */ 
/*     */           
/* 556 */           if (maxBitDepth > 0) {
/* 557 */             setMaxBitDepth((1 << maxBitDepth) - 1);
/* 558 */           } else if (bitsPerSample != null) {
/* 559 */             for (int k = 0; k < bitsPerSample.length; k++) {
/* 560 */               if (bitsPerSample[k] > maxBitDepth) {
/* 561 */                 maxBitDepth = bitsPerSample[k];
/*     */               }
/*     */             } 
/* 564 */             setMaxBitDepth((1 << maxBitDepth) - 1);
/*     */           } 
/* 566 */         } else if (!name.equals("Dimension")) {
/*     */           
/* 568 */           if (!name.equals("Document"))
/*     */           {
/* 570 */             if (name.equals("Text")) {
/* 571 */               NodeList children1 = node.getChildNodes();
/* 572 */               for (int j = 0; j < children1.getLength(); j++) {
/* 573 */                 Node child = children1.item(j);
/* 574 */                 String name1 = child.getNodeName();
/*     */                 
/* 576 */                 if (name1.equals("TextEntry")) {
/* 577 */                   addComment((String)getAttribute(child, "value"));
/*     */                 }
/*     */               } 
/* 580 */             } else if (!name.equals("Transparency")) {
/*     */ 
/*     */               
/* 583 */               throw new IIOInvalidTreeException(I18N.getString("PNMMetadata3") + " " + name, node);
/*     */             } 
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 589 */     if ((colorSpace != null && colorSpace.equals("RGB")) || numComps > 1 || bitsPerSample.length > 1) {
/*     */ 
/*     */       
/* 592 */       this.variant = 51;
/* 593 */     } else if (this.maxSampleSize > 1) {
/* 594 */       this.variant = 50;
/*     */     } else {
/* 596 */       this.variant = 49;
/*     */     } 
/*     */   }
/*     */   
/*     */   public Object getAttribute(Node node, String name) {
/* 601 */     NamedNodeMap map = node.getAttributes();
/* 602 */     node = map.getNamedItem(name);
/* 603 */     return (node != null) ? node.getNodeValue() : null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\pnm\PNMMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */