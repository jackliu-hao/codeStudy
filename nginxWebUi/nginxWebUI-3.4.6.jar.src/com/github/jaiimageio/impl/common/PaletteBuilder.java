/*     */ package com.github.jaiimageio.impl.common;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.RenderedImage;
/*     */ import java.awt.image.WritableRaster;
/*     */ import javax.imageio.ImageTypeSpecifier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PaletteBuilder
/*     */ {
/*     */   protected static final int MAXLEVEL = 8;
/*     */   protected RenderedImage src;
/*     */   protected ColorModel srcColorModel;
/*     */   protected Raster srcRaster;
/*     */   protected int requiredSize;
/*     */   protected ColorNode root;
/*     */   protected int numNodes;
/*     */   protected int maxNodes;
/*     */   protected int currLevel;
/*     */   protected int currSize;
/*     */   protected ColorNode[] reduceList;
/*     */   protected ColorNode[] palette;
/*     */   protected int transparency;
/*     */   protected ColorNode transColor;
/*     */   
/*     */   public static RenderedImage createIndexedImage(RenderedImage src) {
/* 115 */     PaletteBuilder pb = new PaletteBuilder(src);
/* 116 */     pb.buildPalette();
/* 117 */     return pb.getIndexedImage();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static IndexColorModel createIndexColorModel(RenderedImage img) {
/* 138 */     PaletteBuilder pb = new PaletteBuilder(img);
/* 139 */     pb.buildPalette();
/* 140 */     return pb.getIndexColorModel();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean canCreatePalette(ImageTypeSpecifier type) {
/* 157 */     if (type == null) {
/* 158 */       throw new IllegalArgumentException("type == null");
/*     */     }
/* 160 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean canCreatePalette(RenderedImage image) {
/* 177 */     if (image == null) {
/* 178 */       throw new IllegalArgumentException("image == null");
/*     */     }
/* 180 */     ImageTypeSpecifier type = new ImageTypeSpecifier(image);
/* 181 */     return canCreatePalette(type);
/*     */   }
/*     */   
/*     */   protected RenderedImage getIndexedImage() {
/* 185 */     IndexColorModel icm = getIndexColorModel();
/*     */ 
/*     */     
/* 188 */     BufferedImage dst = new BufferedImage(this.src.getWidth(), this.src.getHeight(), 13, icm);
/*     */ 
/*     */     
/* 191 */     WritableRaster wr = dst.getRaster();
/* 192 */     int minX = this.src.getMinX();
/* 193 */     int minY = this.src.getMinY();
/* 194 */     for (int y = 0; y < dst.getHeight(); y++) {
/* 195 */       for (int x = 0; x < dst.getWidth(); x++) {
/* 196 */         Color aColor = getSrcColor(x + minX, y + minY);
/* 197 */         wr.setSample(x, y, 0, findColorIndex(this.root, aColor));
/*     */       } 
/*     */     } 
/*     */     
/* 201 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   protected PaletteBuilder(RenderedImage src) {
/* 206 */     this(src, 256);
/*     */   }
/*     */   
/*     */   protected PaletteBuilder(RenderedImage src, int size) {
/* 210 */     this.src = src;
/* 211 */     this.srcColorModel = src.getColorModel();
/* 212 */     this.srcRaster = src.getData();
/*     */     
/* 214 */     this
/* 215 */       .transparency = this.srcColorModel.getTransparency();
/*     */     
/* 217 */     if (this.transparency != 1) {
/* 218 */       this.requiredSize = size - 1;
/* 219 */       this.transColor = new ColorNode();
/* 220 */       this.transColor.isLeaf = true;
/*     */     } else {
/* 222 */       this.requiredSize = size;
/*     */     } 
/*     */   }
/*     */   
/*     */   private Color getSrcColor(int x, int y) {
/* 227 */     int argb = this.srcColorModel.getRGB(this.srcRaster.getDataElements(x, y, null));
/* 228 */     return new Color(argb, (this.transparency != 1));
/*     */   }
/*     */   
/*     */   protected int findColorIndex(ColorNode aNode, Color aColor) {
/* 232 */     if (this.transparency != 1 && aColor
/* 233 */       .getAlpha() != 255)
/*     */     {
/* 235 */       return 0;
/*     */     }
/*     */     
/* 238 */     if (aNode.isLeaf) {
/* 239 */       return aNode.paletteIndex;
/*     */     }
/* 241 */     int childIndex = getBranchIndex(aColor, aNode.level);
/*     */     
/* 243 */     return findColorIndex(aNode.children[childIndex], aColor);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void buildPalette() {
/* 248 */     this.reduceList = new ColorNode[9];
/* 249 */     for (int i = 0; i < this.reduceList.length; i++) {
/* 250 */       this.reduceList[i] = null;
/*     */     }
/*     */     
/* 253 */     this.numNodes = 0;
/* 254 */     this.maxNodes = 0;
/* 255 */     this.root = null;
/* 256 */     this.currSize = 0;
/* 257 */     this.currLevel = 8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 264 */     int w = this.src.getWidth();
/* 265 */     int h = this.src.getHeight();
/* 266 */     int minX = this.src.getMinX();
/* 267 */     int minY = this.src.getMinY();
/* 268 */     for (int y = 0; y < h; y++) {
/* 269 */       for (int x = 0; x < w; x++) {
/*     */         
/* 271 */         Color aColor = getSrcColor(w - x + minX - 1, h - y + minY - 1);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 276 */         if (this.transparency != 1 && aColor
/* 277 */           .getAlpha() != 255) {
/*     */           
/* 279 */           this.transColor = insertNode(this.transColor, aColor, 0);
/*     */         } else {
/* 281 */           this.root = insertNode(this.root, aColor, 0);
/*     */         } 
/* 283 */         if (this.currSize > this.requiredSize) {
/* 284 */           reduceTree();
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected ColorNode insertNode(ColorNode aNode, Color aColor, int aLevel) {
/* 292 */     if (aNode == null) {
/* 293 */       aNode = new ColorNode();
/* 294 */       this.numNodes++;
/* 295 */       if (this.numNodes > this.maxNodes) {
/* 296 */         this.maxNodes = this.numNodes;
/*     */       }
/* 298 */       aNode.level = aLevel;
/* 299 */       aNode.isLeaf = (aLevel > 8);
/* 300 */       if (aNode.isLeaf) {
/* 301 */         this.currSize++;
/*     */       }
/*     */     } 
/* 304 */     aNode.colorCount++;
/* 305 */     aNode.red += aColor.getRed();
/* 306 */     aNode.green += aColor.getGreen();
/* 307 */     aNode.blue += aColor.getBlue();
/*     */     
/* 309 */     if (!aNode.isLeaf) {
/* 310 */       int branchIndex = getBranchIndex(aColor, aLevel);
/* 311 */       if (aNode.children[branchIndex] == null) {
/* 312 */         aNode.childCount++;
/* 313 */         if (aNode.childCount == 2) {
/* 314 */           aNode.nextReducible = this.reduceList[aLevel];
/* 315 */           this.reduceList[aLevel] = aNode;
/*     */         } 
/*     */       } 
/* 318 */       aNode.children[branchIndex] = 
/* 319 */         insertNode(aNode.children[branchIndex], aColor, aLevel + 1);
/*     */     } 
/* 321 */     return aNode;
/*     */   }
/*     */   
/*     */   protected IndexColorModel getIndexColorModel() {
/* 325 */     int size = this.currSize;
/* 326 */     if (this.transparency != 1) {
/* 327 */       size++;
/*     */     }
/*     */     
/* 330 */     byte[] red = new byte[size];
/* 331 */     byte[] green = new byte[size];
/* 332 */     byte[] blue = new byte[size];
/*     */     
/* 334 */     int index = 0;
/* 335 */     this.palette = new ColorNode[size];
/* 336 */     if (this.transparency != 1) {
/* 337 */       index++;
/*     */     }
/*     */     
/* 340 */     int lastIndex = findPaletteEntry(this.root, index, red, green, blue);
/*     */     
/* 342 */     IndexColorModel icm = null;
/* 343 */     if (this.transparency != 1) {
/* 344 */       icm = new IndexColorModel(8, size, red, green, blue, 0);
/*     */     } else {
/* 346 */       icm = new IndexColorModel(8, this.currSize, red, green, blue);
/*     */     } 
/* 348 */     return icm;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected int findPaletteEntry(ColorNode aNode, int index, byte[] red, byte[] green, byte[] blue) {
/* 354 */     if (aNode.isLeaf) {
/* 355 */       red[index] = (byte)(int)(aNode.red / aNode.colorCount);
/* 356 */       green[index] = (byte)(int)(aNode.green / aNode.colorCount);
/* 357 */       blue[index] = (byte)(int)(aNode.blue / aNode.colorCount);
/* 358 */       aNode.paletteIndex = index;
/*     */       
/* 360 */       this.palette[index] = aNode;
/*     */       
/* 362 */       index++;
/*     */     } else {
/* 364 */       for (int i = 0; i < 8; i++) {
/* 365 */         if (aNode.children[i] != null) {
/* 366 */           index = findPaletteEntry(aNode.children[i], index, red, green, blue);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 371 */     return index;
/*     */   }
/*     */   
/*     */   protected int getBranchIndex(Color aColor, int aLevel) {
/* 375 */     if (aLevel > 8 || aLevel < 0) {
/* 376 */       throw new IllegalArgumentException("Invalid octree node depth: " + aLevel);
/*     */     }
/*     */ 
/*     */     
/* 380 */     int shift = 8 - aLevel;
/* 381 */     int red_index = 0x1 & (0xFF & aColor.getRed()) >> shift;
/* 382 */     int green_index = 0x1 & (0xFF & aColor.getGreen()) >> shift;
/* 383 */     int blue_index = 0x1 & (0xFF & aColor.getBlue()) >> shift;
/* 384 */     int index = red_index << 2 | green_index << 1 | blue_index;
/* 385 */     return index;
/*     */   }
/*     */   
/*     */   protected void reduceTree() {
/* 389 */     int level = this.reduceList.length - 1;
/* 390 */     while (this.reduceList[level] == null && level >= 0) {
/* 391 */       level--;
/*     */     }
/*     */     
/* 394 */     ColorNode thisNode = this.reduceList[level];
/* 395 */     if (thisNode == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 401 */     ColorNode pList = thisNode;
/* 402 */     int minColorCount = pList.colorCount;
/*     */     
/* 404 */     int cnt = 1;
/* 405 */     while (pList.nextReducible != null) {
/* 406 */       if (minColorCount > pList.nextReducible.colorCount) {
/* 407 */         thisNode = pList;
/* 408 */         minColorCount = pList.colorCount;
/*     */       } 
/* 410 */       pList = pList.nextReducible;
/* 411 */       cnt++;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 416 */     if (thisNode == this.reduceList[level]) {
/* 417 */       this.reduceList[level] = thisNode.nextReducible;
/*     */     } else {
/* 419 */       pList = thisNode.nextReducible;
/* 420 */       thisNode.nextReducible = pList.nextReducible;
/* 421 */       thisNode = pList;
/*     */     } 
/*     */     
/* 424 */     if (thisNode.isLeaf) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 429 */     int leafChildCount = thisNode.getLeafChildCount();
/* 430 */     thisNode.isLeaf = true;
/* 431 */     this.currSize -= leafChildCount - 1;
/* 432 */     int aDepth = thisNode.level;
/* 433 */     for (int i = 0; i < 8; i++) {
/* 434 */       thisNode.children[i] = freeTree(thisNode.children[i]);
/*     */     }
/* 436 */     thisNode.childCount = 0;
/*     */   }
/*     */   
/*     */   protected ColorNode freeTree(ColorNode aNode) {
/* 440 */     if (aNode == null) {
/* 441 */       return null;
/*     */     }
/* 443 */     for (int i = 0; i < 8; i++) {
/* 444 */       aNode.children[i] = freeTree(aNode.children[i]);
/*     */     }
/*     */     
/* 447 */     this.numNodes--;
/* 448 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected class ColorNode
/*     */   {
/*     */     public boolean isLeaf = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 471 */     public int level = 0;
/* 472 */     public int childCount = 0;
/* 473 */     ColorNode[] children = new ColorNode[8]; public int colorCount; public long red; public long blue; public ColorNode() {
/* 474 */       for (int i = 0; i < 8; i++) {
/* 475 */         this.children[i] = null;
/*     */       }
/*     */       
/* 478 */       this.colorCount = 0;
/* 479 */       this.red = this.green = this.blue = 0L;
/*     */       
/* 481 */       this.paletteIndex = 0;
/*     */     }
/*     */     public long green; public int paletteIndex; ColorNode nextReducible;
/*     */     public int getLeafChildCount() {
/* 485 */       if (this.isLeaf) {
/* 486 */         return 0;
/*     */       }
/* 488 */       int cnt = 0;
/* 489 */       for (int i = 0; i < this.children.length; i++) {
/* 490 */         if (this.children[i] != null) {
/* 491 */           if ((this.children[i]).isLeaf) {
/* 492 */             cnt++;
/*     */           } else {
/* 494 */             cnt += this.children[i].getLeafChildCount();
/*     */           } 
/*     */         }
/*     */       } 
/* 498 */       return cnt;
/*     */     }
/*     */     
/*     */     public int getRGB() {
/* 502 */       int r = (int)this.red / this.colorCount;
/* 503 */       int g = (int)this.green / this.colorCount;
/* 504 */       int b = (int)this.blue / this.colorCount;
/*     */       
/* 506 */       int c = 0xFF000000 | (0xFF & r) << 16 | (0xFF & g) << 8 | 0xFF & b;
/* 507 */       return c;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\common\PaletteBuilder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */