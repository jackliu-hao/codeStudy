/*     */ package com.github.jaiimageio.impl.plugins.gif;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GIFImageMetadata
/*     */   extends GIFMetadata
/*     */ {
/*     */   static final String nativeMetadataFormatName = "javax_imageio_gif_image_1.0";
/*  67 */   static final String[] disposalMethodNames = new String[] { "none", "doNotDispose", "restoreToBackgroundColor", "restoreToPrevious", "undefinedDisposalMethod4", "undefinedDisposalMethod5", "undefinedDisposalMethod6", "undefinedDisposalMethod7" };
/*     */ 
/*     */   
/*     */   public int imageLeftPosition;
/*     */ 
/*     */   
/*     */   public int imageTopPosition;
/*     */ 
/*     */   
/*     */   public int imageWidth;
/*     */ 
/*     */   
/*     */   public int imageHeight;
/*     */   
/*     */   public boolean interlaceFlag = false;
/*     */   
/*     */   public boolean sortFlag = false;
/*     */   
/*  85 */   public byte[] localColorTable = null;
/*     */ 
/*     */   
/*  88 */   public int disposalMethod = 0;
/*     */   public boolean userInputFlag = false;
/*     */   public boolean transparentColorFlag = false;
/*  91 */   public int delayTime = 0;
/*  92 */   public int transparentColorIndex = 0;
/*     */   
/*     */   public boolean hasPlainTextExtension = false;
/*     */   
/*     */   public int textGridLeft;
/*     */   
/*     */   public int textGridTop;
/*     */   
/*     */   public int textGridWidth;
/*     */   
/*     */   public int textGridHeight;
/*     */   public int characterCellWidth;
/*     */   public int characterCellHeight;
/*     */   public int textForegroundColor;
/*     */   public int textBackgroundColor;
/*     */   public byte[] text;
/* 108 */   public List applicationIDs = null;
/*     */ 
/*     */   
/* 111 */   public List authenticationCodes = null;
/*     */ 
/*     */   
/* 114 */   public List applicationData = null;
/*     */ 
/*     */ 
/*     */   
/* 118 */   public List comments = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected GIFImageMetadata(boolean standardMetadataFormatSupported, String nativeMetadataFormatName, String nativeMetadataFormatClassName, String[] extraMetadataFormatNames, String[] extraMetadataFormatClassNames) {
/* 126 */     super(standardMetadataFormatSupported, nativeMetadataFormatName, nativeMetadataFormatClassName, extraMetadataFormatNames, extraMetadataFormatClassNames);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GIFImageMetadata() {
/* 134 */     this(true, "javax_imageio_gif_image_1.0", "com.github.jaiimageio.impl.plugins.gif.GIFImageMetadataFormat", null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 141 */     return true;
/*     */   }
/*     */   
/*     */   public Node getAsTree(String formatName) {
/* 145 */     if (formatName.equals("javax_imageio_gif_image_1.0"))
/* 146 */       return getNativeTree(); 
/* 147 */     if (formatName
/* 148 */       .equals("javax_imageio_1.0")) {
/* 149 */       return getStandardTree();
/*     */     }
/* 151 */     throw new IllegalArgumentException("Not a recognized format!");
/*     */   }
/*     */ 
/*     */   
/*     */   private String toISO8859(byte[] data) {
/*     */     try {
/* 157 */       return new String(data, "ISO-8859-1");
/* 158 */     } catch (UnsupportedEncodingException e) {
/* 159 */       return "";
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Node getNativeTree() {
/* 165 */     IIOMetadataNode root = new IIOMetadataNode("javax_imageio_gif_image_1.0");
/*     */ 
/*     */ 
/*     */     
/* 169 */     IIOMetadataNode node = new IIOMetadataNode("ImageDescriptor");
/* 170 */     node.setAttribute("imageLeftPosition", 
/* 171 */         Integer.toString(this.imageLeftPosition));
/* 172 */     node.setAttribute("imageTopPosition", 
/* 173 */         Integer.toString(this.imageTopPosition));
/* 174 */     node.setAttribute("imageWidth", Integer.toString(this.imageWidth));
/* 175 */     node.setAttribute("imageHeight", Integer.toString(this.imageHeight));
/* 176 */     node.setAttribute("interlaceFlag", this.interlaceFlag ? "true" : "false");
/*     */     
/* 178 */     root.appendChild(node);
/*     */ 
/*     */     
/* 181 */     if (this.localColorTable != null) {
/* 182 */       node = new IIOMetadataNode("LocalColorTable");
/* 183 */       int numEntries = this.localColorTable.length / 3;
/* 184 */       node.setAttribute("sizeOfLocalColorTable", 
/* 185 */           Integer.toString(numEntries));
/* 186 */       node.setAttribute("sortFlag", this.sortFlag ? "TRUE" : "FALSE");
/*     */ 
/*     */       
/* 189 */       for (int i = 0; i < numEntries; i++) {
/* 190 */         IIOMetadataNode entry = new IIOMetadataNode("ColorTableEntry");
/*     */         
/* 192 */         entry.setAttribute("index", Integer.toString(i));
/* 193 */         int r = this.localColorTable[3 * i] & 0xFF;
/* 194 */         int g = this.localColorTable[3 * i + 1] & 0xFF;
/* 195 */         int b = this.localColorTable[3 * i + 2] & 0xFF;
/* 196 */         entry.setAttribute("red", Integer.toString(r));
/* 197 */         entry.setAttribute("green", Integer.toString(g));
/* 198 */         entry.setAttribute("blue", Integer.toString(b));
/* 199 */         node.appendChild(entry);
/*     */       } 
/* 201 */       root.appendChild(node);
/*     */     } 
/*     */ 
/*     */     
/* 205 */     node = new IIOMetadataNode("GraphicControlExtension");
/* 206 */     node.setAttribute("disposalMethod", disposalMethodNames[this.disposalMethod]);
/*     */     
/* 208 */     node.setAttribute("userInputFlag", this.userInputFlag ? "true" : "false");
/*     */     
/* 210 */     node.setAttribute("transparentColorFlag", this.transparentColorFlag ? "true" : "false");
/*     */     
/* 212 */     node.setAttribute("delayTime", 
/* 213 */         Integer.toString(this.delayTime));
/* 214 */     node.setAttribute("transparentColorIndex", 
/* 215 */         Integer.toString(this.transparentColorIndex));
/* 216 */     root.appendChild(node);
/*     */     
/* 218 */     if (this.hasPlainTextExtension) {
/* 219 */       node = new IIOMetadataNode("PlainTextExtension");
/* 220 */       node.setAttribute("textGridLeft", 
/* 221 */           Integer.toString(this.textGridLeft));
/* 222 */       node.setAttribute("textGridTop", 
/* 223 */           Integer.toString(this.textGridTop));
/* 224 */       node.setAttribute("textGridWidth", 
/* 225 */           Integer.toString(this.textGridWidth));
/* 226 */       node.setAttribute("textGridHeight", 
/* 227 */           Integer.toString(this.textGridHeight));
/* 228 */       node.setAttribute("characterCellWidth", 
/* 229 */           Integer.toString(this.characterCellWidth));
/* 230 */       node.setAttribute("characterCellHeight", 
/* 231 */           Integer.toString(this.characterCellHeight));
/* 232 */       node.setAttribute("textForegroundColor", 
/* 233 */           Integer.toString(this.textForegroundColor));
/* 234 */       node.setAttribute("textBackgroundColor", 
/* 235 */           Integer.toString(this.textBackgroundColor));
/* 236 */       node.setAttribute("text", toISO8859(this.text));
/*     */       
/* 238 */       root.appendChild(node);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 243 */     int numAppExtensions = (this.applicationIDs == null) ? 0 : this.applicationIDs.size();
/* 244 */     if (numAppExtensions > 0) {
/* 245 */       node = new IIOMetadataNode("ApplicationExtensions");
/* 246 */       for (int i = 0; i < numAppExtensions; i++) {
/* 247 */         IIOMetadataNode appExtNode = new IIOMetadataNode("ApplicationExtension");
/*     */         
/* 249 */         byte[] applicationID = this.applicationIDs.get(i);
/* 250 */         appExtNode.setAttribute("applicationID", 
/* 251 */             toISO8859(applicationID));
/* 252 */         byte[] authenticationCode = this.authenticationCodes.get(i);
/* 253 */         appExtNode.setAttribute("authenticationCode", 
/* 254 */             toISO8859(authenticationCode));
/* 255 */         byte[] appData = this.applicationData.get(i);
/* 256 */         appExtNode.setUserObject(appData.clone());
/* 257 */         node.appendChild(appExtNode);
/*     */       } 
/*     */       
/* 260 */       root.appendChild(node);
/*     */     } 
/*     */ 
/*     */     
/* 264 */     int numComments = (this.comments == null) ? 0 : this.comments.size();
/* 265 */     if (numComments > 0) {
/* 266 */       node = new IIOMetadataNode("CommentExtensions");
/* 267 */       for (int i = 0; i < numComments; i++) {
/* 268 */         IIOMetadataNode commentNode = new IIOMetadataNode("CommentExtension");
/*     */         
/* 270 */         byte[] comment = this.comments.get(i);
/* 271 */         commentNode.setAttribute("value", toISO8859(comment));
/* 272 */         node.appendChild(commentNode);
/*     */       } 
/*     */       
/* 275 */       root.appendChild(node);
/*     */     } 
/*     */     
/* 278 */     return root;
/*     */   }
/*     */   
/*     */   public IIOMetadataNode getStandardChromaNode() {
/* 282 */     IIOMetadataNode chroma_node = new IIOMetadataNode("Chroma");
/* 283 */     IIOMetadataNode node = null;
/*     */     
/* 285 */     node = new IIOMetadataNode("ColorSpaceType");
/* 286 */     node.setAttribute("name", "RGB");
/* 287 */     chroma_node.appendChild(node);
/*     */     
/* 289 */     node = new IIOMetadataNode("NumChannels");
/* 290 */     node.setAttribute("value", this.transparentColorFlag ? "4" : "3");
/* 291 */     chroma_node.appendChild(node);
/*     */ 
/*     */ 
/*     */     
/* 295 */     node = new IIOMetadataNode("BlackIsZero");
/* 296 */     node.setAttribute("value", "TRUE");
/* 297 */     chroma_node.appendChild(node);
/*     */     
/* 299 */     if (this.localColorTable != null) {
/* 300 */       node = new IIOMetadataNode("Palette");
/* 301 */       int numEntries = this.localColorTable.length / 3;
/* 302 */       for (int i = 0; i < numEntries; i++) {
/* 303 */         IIOMetadataNode entry = new IIOMetadataNode("PaletteEntry");
/*     */         
/* 305 */         entry.setAttribute("index", Integer.toString(i));
/* 306 */         entry.setAttribute("red", 
/* 307 */             Integer.toString(this.localColorTable[3 * i] & 0xFF));
/* 308 */         entry.setAttribute("green", 
/* 309 */             Integer.toString(this.localColorTable[3 * i + 1] & 0xFF));
/* 310 */         entry.setAttribute("blue", 
/* 311 */             Integer.toString(this.localColorTable[3 * i + 2] & 0xFF));
/* 312 */         node.appendChild(entry);
/*     */       } 
/* 314 */       chroma_node.appendChild(node);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 320 */     return chroma_node;
/*     */   }
/*     */   
/*     */   public IIOMetadataNode getStandardCompressionNode() {
/* 324 */     IIOMetadataNode compression_node = new IIOMetadataNode("Compression");
/* 325 */     IIOMetadataNode node = null;
/*     */     
/* 327 */     node = new IIOMetadataNode("CompressionTypeName");
/* 328 */     node.setAttribute("value", "lzw");
/* 329 */     compression_node.appendChild(node);
/*     */     
/* 331 */     node = new IIOMetadataNode("Lossless");
/* 332 */     node.setAttribute("value", "TRUE");
/* 333 */     compression_node.appendChild(node);
/*     */     
/* 335 */     node = new IIOMetadataNode("NumProgressiveScans");
/* 336 */     node.setAttribute("value", this.interlaceFlag ? "4" : "1");
/* 337 */     compression_node.appendChild(node);
/*     */ 
/*     */ 
/*     */     
/* 341 */     return compression_node;
/*     */   }
/*     */   
/*     */   public IIOMetadataNode getStandardDataNode() {
/* 345 */     IIOMetadataNode data_node = new IIOMetadataNode("Data");
/* 346 */     IIOMetadataNode node = null;
/*     */ 
/*     */ 
/*     */     
/* 350 */     node = new IIOMetadataNode("SampleFormat");
/* 351 */     node.setAttribute("value", "Index");
/* 352 */     data_node.appendChild(node);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 358 */     return data_node;
/*     */   }
/*     */   
/*     */   public IIOMetadataNode getStandardDimensionNode() {
/* 362 */     IIOMetadataNode dimension_node = new IIOMetadataNode("Dimension");
/* 363 */     IIOMetadataNode node = null;
/*     */ 
/*     */ 
/*     */     
/* 367 */     node = new IIOMetadataNode("ImageOrientation");
/* 368 */     node.setAttribute("value", "Normal");
/* 369 */     dimension_node.appendChild(node);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 378 */     node = new IIOMetadataNode("HorizontalPixelOffset");
/* 379 */     node.setAttribute("value", Integer.toString(this.imageLeftPosition));
/* 380 */     dimension_node.appendChild(node);
/*     */     
/* 382 */     node = new IIOMetadataNode("VerticalPixelOffset");
/* 383 */     node.setAttribute("value", Integer.toString(this.imageTopPosition));
/* 384 */     dimension_node.appendChild(node);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 389 */     return dimension_node;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public IIOMetadataNode getStandardTextNode() {
/* 395 */     if (this.comments == null) {
/* 396 */       return null;
/*     */     }
/* 398 */     Iterator<byte[]> commentIter = this.comments.iterator();
/* 399 */     if (!commentIter.hasNext()) {
/* 400 */       return null;
/*     */     }
/*     */     
/* 403 */     IIOMetadataNode text_node = new IIOMetadataNode("Text");
/* 404 */     IIOMetadataNode node = null;
/*     */     
/* 406 */     while (commentIter.hasNext()) {
/* 407 */       byte[] comment = commentIter.next();
/* 408 */       String s = null;
/*     */       try {
/* 410 */         s = new String(comment, "ISO-8859-1");
/* 411 */       } catch (UnsupportedEncodingException e) {
/* 412 */         throw new RuntimeException("Encoding ISO-8859-1 unknown!");
/*     */       } 
/*     */       
/* 415 */       node = new IIOMetadataNode("TextEntry");
/* 416 */       node.setAttribute("value", s);
/* 417 */       node.setAttribute("encoding", "ISO-8859-1");
/* 418 */       node.setAttribute("compression", "none");
/* 419 */       text_node.appendChild(node);
/*     */     } 
/*     */     
/* 422 */     return text_node;
/*     */   }
/*     */   
/*     */   public IIOMetadataNode getStandardTransparencyNode() {
/* 426 */     if (!this.transparentColorFlag) {
/* 427 */       return null;
/*     */     }
/*     */     
/* 430 */     IIOMetadataNode transparency_node = new IIOMetadataNode("Transparency");
/*     */     
/* 432 */     IIOMetadataNode node = null;
/*     */ 
/*     */ 
/*     */     
/* 436 */     node = new IIOMetadataNode("TransparentIndex");
/* 437 */     node.setAttribute("value", 
/* 438 */         Integer.toString(this.transparentColorIndex));
/* 439 */     transparency_node.appendChild(node);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 445 */     return transparency_node;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFromTree(String formatName, Node root) throws IIOInvalidTreeException {
/* 451 */     throw new IllegalStateException("Metadata is read-only!");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void mergeNativeTree(Node root) throws IIOInvalidTreeException {
/* 456 */     throw new IllegalStateException("Metadata is read-only!");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void mergeStandardTree(Node root) throws IIOInvalidTreeException {
/* 461 */     throw new IllegalStateException("Metadata is read-only!");
/*     */   }
/*     */   
/*     */   public void reset() {
/* 465 */     throw new IllegalStateException("Metadata is read-only!");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\gif\GIFImageMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */