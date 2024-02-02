/*     */ package com.github.jaiimageio.impl.plugins.gif;
/*     */ 
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
/*     */ 
/*     */ public class GIFStreamMetadata
/*     */   extends GIFMetadata
/*     */ {
/*     */   static final String nativeMetadataFormatName = "javax_imageio_gif_stream_1.0";
/*  65 */   public static final String[] versionStrings = new String[] { "87a", "89a" };
/*     */   
/*     */   public String version;
/*     */   
/*     */   public int logicalScreenWidth;
/*     */   
/*     */   public int logicalScreenHeight;
/*     */   public int colorResolution;
/*     */   public int pixelAspectRatio;
/*     */   public int backgroundColorIndex;
/*     */   public boolean sortFlag;
/*  76 */   public static final String[] colorTableSizes = new String[] { "2", "4", "8", "16", "32", "64", "128", "256" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  81 */   public byte[] globalColorTable = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected GIFStreamMetadata(boolean standardMetadataFormatSupported, String nativeMetadataFormatName, String nativeMetadataFormatClassName, String[] extraMetadataFormatNames, String[] extraMetadataFormatClassNames) {
/*  89 */     super(standardMetadataFormatSupported, nativeMetadataFormatName, nativeMetadataFormatClassName, extraMetadataFormatNames, extraMetadataFormatClassNames);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GIFStreamMetadata() {
/*  97 */     this(true, "javax_imageio_gif_stream_1.0", "com.github.jaiimageio.impl.plugins.gif.GIFStreamMetadataFormat", null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 105 */     return true;
/*     */   }
/*     */   
/*     */   public Node getAsTree(String formatName) {
/* 109 */     if (formatName.equals("javax_imageio_gif_stream_1.0"))
/* 110 */       return getNativeTree(); 
/* 111 */     if (formatName
/* 112 */       .equals("javax_imageio_1.0")) {
/* 113 */       return getStandardTree();
/*     */     }
/* 115 */     throw new IllegalArgumentException("Not a recognized format!");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Node getNativeTree() {
/* 121 */     IIOMetadataNode root = new IIOMetadataNode("javax_imageio_gif_stream_1.0");
/*     */ 
/*     */     
/* 124 */     IIOMetadataNode node = new IIOMetadataNode("Version");
/* 125 */     node.setAttribute("value", this.version);
/* 126 */     root.appendChild(node);
/*     */ 
/*     */     
/* 129 */     node = new IIOMetadataNode("LogicalScreenDescriptor");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 134 */     node.setAttribute("logicalScreenWidth", (this.logicalScreenWidth == -1) ? "" : 
/*     */         
/* 136 */         Integer.toString(this.logicalScreenWidth));
/* 137 */     node.setAttribute("logicalScreenHeight", (this.logicalScreenHeight == -1) ? "" : 
/*     */         
/* 139 */         Integer.toString(this.logicalScreenHeight));
/*     */     
/* 141 */     node.setAttribute("colorResolution", (this.colorResolution == -1) ? "" : 
/*     */         
/* 143 */         Integer.toString(this.colorResolution));
/* 144 */     node.setAttribute("pixelAspectRatio", 
/* 145 */         Integer.toString(this.pixelAspectRatio));
/* 146 */     root.appendChild(node);
/*     */     
/* 148 */     if (this.globalColorTable != null) {
/* 149 */       node = new IIOMetadataNode("GlobalColorTable");
/* 150 */       int numEntries = this.globalColorTable.length / 3;
/* 151 */       node.setAttribute("sizeOfGlobalColorTable", 
/* 152 */           Integer.toString(numEntries));
/* 153 */       node.setAttribute("backgroundColorIndex", 
/* 154 */           Integer.toString(this.backgroundColorIndex));
/* 155 */       node.setAttribute("sortFlag", this.sortFlag ? "TRUE" : "FALSE");
/*     */ 
/*     */       
/* 158 */       for (int i = 0; i < numEntries; i++) {
/* 159 */         IIOMetadataNode entry = new IIOMetadataNode("ColorTableEntry");
/*     */         
/* 161 */         entry.setAttribute("index", Integer.toString(i));
/* 162 */         int r = this.globalColorTable[3 * i] & 0xFF;
/* 163 */         int g = this.globalColorTable[3 * i + 1] & 0xFF;
/* 164 */         int b = this.globalColorTable[3 * i + 2] & 0xFF;
/* 165 */         entry.setAttribute("red", Integer.toString(r));
/* 166 */         entry.setAttribute("green", Integer.toString(g));
/* 167 */         entry.setAttribute("blue", Integer.toString(b));
/* 168 */         node.appendChild(entry);
/*     */       } 
/* 170 */       root.appendChild(node);
/*     */     } 
/*     */     
/* 173 */     return root;
/*     */   }
/*     */   
/*     */   public IIOMetadataNode getStandardChromaNode() {
/* 177 */     IIOMetadataNode chroma_node = new IIOMetadataNode("Chroma");
/* 178 */     IIOMetadataNode node = null;
/*     */     
/* 180 */     node = new IIOMetadataNode("ColorSpaceType");
/* 181 */     node.setAttribute("name", "RGB");
/* 182 */     chroma_node.appendChild(node);
/*     */     
/* 184 */     node = new IIOMetadataNode("BlackIsZero");
/* 185 */     node.setAttribute("value", "TRUE");
/* 186 */     chroma_node.appendChild(node);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 191 */     if (this.globalColorTable != null) {
/* 192 */       node = new IIOMetadataNode("Palette");
/* 193 */       int numEntries = this.globalColorTable.length / 3;
/* 194 */       for (int i = 0; i < numEntries; i++) {
/* 195 */         IIOMetadataNode entry = new IIOMetadataNode("PaletteEntry");
/*     */         
/* 197 */         entry.setAttribute("index", Integer.toString(i));
/* 198 */         entry.setAttribute("red", 
/* 199 */             Integer.toString(this.globalColorTable[3 * i] & 0xFF));
/* 200 */         entry.setAttribute("green", 
/* 201 */             Integer.toString(this.globalColorTable[3 * i + 1] & 0xFF));
/* 202 */         entry.setAttribute("blue", 
/* 203 */             Integer.toString(this.globalColorTable[3 * i + 2] & 0xFF));
/* 204 */         node.appendChild(entry);
/*     */       } 
/* 206 */       chroma_node.appendChild(node);
/*     */ 
/*     */       
/* 209 */       node = new IIOMetadataNode("BackgroundIndex");
/* 210 */       node.setAttribute("value", Integer.toString(this.backgroundColorIndex));
/* 211 */       chroma_node.appendChild(node);
/*     */     } 
/*     */     
/* 214 */     return chroma_node;
/*     */   }
/*     */   
/*     */   public IIOMetadataNode getStandardCompressionNode() {
/* 218 */     IIOMetadataNode compression_node = new IIOMetadataNode("Compression");
/* 219 */     IIOMetadataNode node = null;
/*     */     
/* 221 */     node = new IIOMetadataNode("CompressionTypeName");
/* 222 */     node.setAttribute("value", "lzw");
/* 223 */     compression_node.appendChild(node);
/*     */     
/* 225 */     node = new IIOMetadataNode("Lossless");
/* 226 */     node.setAttribute("value", "true");
/* 227 */     compression_node.appendChild(node);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 232 */     return compression_node;
/*     */   }
/*     */   
/*     */   public IIOMetadataNode getStandardDataNode() {
/* 236 */     IIOMetadataNode data_node = new IIOMetadataNode("Data");
/* 237 */     IIOMetadataNode node = null;
/*     */ 
/*     */ 
/*     */     
/* 241 */     node = new IIOMetadataNode("SampleFormat");
/* 242 */     node.setAttribute("value", "Index");
/* 243 */     data_node.appendChild(node);
/*     */     
/* 245 */     node = new IIOMetadataNode("BitsPerSample");
/* 246 */     node.setAttribute("value", (this.colorResolution == -1) ? "" : 
/*     */         
/* 248 */         Integer.toString(this.colorResolution));
/* 249 */     data_node.appendChild(node);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 254 */     return data_node;
/*     */   }
/*     */   
/*     */   public IIOMetadataNode getStandardDimensionNode() {
/* 258 */     IIOMetadataNode dimension_node = new IIOMetadataNode("Dimension");
/* 259 */     IIOMetadataNode node = null;
/*     */     
/* 261 */     node = new IIOMetadataNode("PixelAspectRatio");
/* 262 */     float aspectRatio = 1.0F;
/* 263 */     if (this.pixelAspectRatio != 0) {
/* 264 */       aspectRatio = (this.pixelAspectRatio + 15) / 64.0F;
/*     */     }
/* 266 */     node.setAttribute("value", Float.toString(aspectRatio));
/* 267 */     dimension_node.appendChild(node);
/*     */     
/* 269 */     node = new IIOMetadataNode("ImageOrientation");
/* 270 */     node.setAttribute("value", "Normal");
/* 271 */     dimension_node.appendChild(node);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 282 */     node = new IIOMetadataNode("HorizontalScreenSize");
/* 283 */     node.setAttribute("value", (this.logicalScreenWidth == -1) ? "" : 
/*     */         
/* 285 */         Integer.toString(this.logicalScreenWidth));
/* 286 */     dimension_node.appendChild(node);
/*     */     
/* 288 */     node = new IIOMetadataNode("VerticalScreenSize");
/* 289 */     node.setAttribute("value", (this.logicalScreenHeight == -1) ? "" : 
/*     */         
/* 291 */         Integer.toString(this.logicalScreenHeight));
/* 292 */     dimension_node.appendChild(node);
/*     */     
/* 294 */     return dimension_node;
/*     */   }
/*     */   
/*     */   public IIOMetadataNode getStandardDocumentNode() {
/* 298 */     IIOMetadataNode document_node = new IIOMetadataNode("Document");
/* 299 */     IIOMetadataNode node = null;
/*     */     
/* 301 */     node = new IIOMetadataNode("FormatVersion");
/* 302 */     node.setAttribute("value", this.version);
/* 303 */     document_node.appendChild(node);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 309 */     return document_node;
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadataNode getStandardTextNode() {
/* 314 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public IIOMetadataNode getStandardTransparencyNode() {
/* 319 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFromTree(String formatName, Node root) throws IIOInvalidTreeException {
/* 325 */     throw new IllegalStateException("Metadata is read-only!");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void mergeNativeTree(Node root) throws IIOInvalidTreeException {
/* 330 */     throw new IllegalStateException("Metadata is read-only!");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void mergeStandardTree(Node root) throws IIOInvalidTreeException {
/* 335 */     throw new IllegalStateException("Metadata is read-only!");
/*     */   }
/*     */   
/*     */   public void reset() {
/* 339 */     throw new IllegalStateException("Metadata is read-only!");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\gif\GIFStreamMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */