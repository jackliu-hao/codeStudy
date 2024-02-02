/*     */ package com.github.jaiimageio.impl.common;
/*     */ 
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.RenderedImage;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SimpleRenderedImage
/*     */   implements RenderedImage
/*     */ {
/*     */   protected int minX;
/*     */   protected int minY;
/*     */   protected int width;
/*     */   protected int height;
/*     */   protected int tileWidth;
/*     */   protected int tileHeight;
/*  79 */   protected int tileGridXOffset = 0;
/*     */ 
/*     */   
/*  82 */   protected int tileGridYOffset = 0;
/*     */ 
/*     */   
/*     */   protected SampleModel sampleModel;
/*     */ 
/*     */   
/*     */   protected ColorModel colorModel;
/*     */ 
/*     */   
/*  91 */   protected Vector sources = new Vector();
/*     */ 
/*     */   
/*  94 */   protected Hashtable properties = new Hashtable<Object, Object>();
/*     */ 
/*     */   
/*     */   public int getMinX() {
/*  98 */     return this.minX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getMaxX() {
/* 108 */     return getMinX() + getWidth();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMinY() {
/* 113 */     return this.minY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getMaxY() {
/* 123 */     return getMinY() + getHeight();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWidth() {
/* 128 */     return this.width;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHeight() {
/* 133 */     return this.height;
/*     */   }
/*     */ 
/*     */   
/*     */   public Rectangle getBounds() {
/* 138 */     return new Rectangle(getMinX(), getMinY(), getWidth(), getHeight());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTileWidth() {
/* 143 */     return this.tileWidth;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTileHeight() {
/* 148 */     return this.tileHeight;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTileGridXOffset() {
/* 155 */     return this.tileGridXOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTileGridYOffset() {
/* 162 */     return this.tileGridYOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMinTileX() {
/* 171 */     return XToTileX(getMinX());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxTileX() {
/* 180 */     return XToTileX(getMaxX() - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumXTiles() {
/* 190 */     return getMaxTileX() - getMinTileX() + 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMinTileY() {
/* 199 */     return YToTileY(getMinY());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxTileY() {
/* 208 */     return YToTileY(getMaxY() - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumYTiles() {
/* 218 */     return getMaxTileY() - getMinTileY() + 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public SampleModel getSampleModel() {
/* 223 */     return this.sampleModel;
/*     */   }
/*     */ 
/*     */   
/*     */   public ColorModel getColorModel() {
/* 228 */     return this.colorModel;
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
/*     */   public Object getProperty(String name) {
/* 242 */     name = name.toLowerCase();
/* 243 */     Object value = this.properties.get(name);
/* 244 */     return (value != null) ? value : Image.UndefinedProperty;
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
/*     */   public String[] getPropertyNames() {
/* 256 */     String[] names = null;
/*     */     
/* 258 */     if (this.properties.size() > 0) {
/* 259 */       names = new String[this.properties.size()];
/* 260 */       int index = 0;
/*     */       
/* 262 */       Enumeration<String> e = this.properties.keys();
/* 263 */       while (e.hasMoreElements()) {
/* 264 */         String name = e.nextElement();
/* 265 */         names[index++] = name;
/*     */       } 
/*     */     } 
/*     */     
/* 269 */     return names;
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
/*     */   public String[] getPropertyNames(String prefix) {
/* 286 */     String[] propertyNames = getPropertyNames();
/* 287 */     if (propertyNames == null) {
/* 288 */       return null;
/*     */     }
/*     */     
/* 291 */     prefix = prefix.toLowerCase();
/*     */     
/* 293 */     Vector<String> names = new Vector();
/* 294 */     for (int i = 0; i < propertyNames.length; i++) {
/* 295 */       if (propertyNames[i].startsWith(prefix)) {
/* 296 */         names.addElement(propertyNames[i]);
/*     */       }
/*     */     } 
/*     */     
/* 300 */     if (names.size() == 0) {
/* 301 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 305 */     String[] prefixNames = new String[names.size()];
/* 306 */     int count = 0;
/* 307 */     for (Iterator<String> it = names.iterator(); it.hasNext();) {
/* 308 */       prefixNames[count++] = it.next();
/*     */     }
/*     */     
/* 311 */     return prefixNames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int XToTileX(int x, int tileGridXOffset, int tileWidth) {
/* 322 */     x -= tileGridXOffset;
/* 323 */     if (x < 0) {
/* 324 */       x += 1 - tileWidth;
/*     */     }
/* 326 */     return x / tileWidth;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int YToTileY(int y, int tileGridYOffset, int tileHeight) {
/* 335 */     y -= tileGridYOffset;
/* 336 */     if (y < 0) {
/* 337 */       y += 1 - tileHeight;
/*     */     }
/* 339 */     return y / tileHeight;
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
/*     */   public int XToTileX(int x) {
/* 351 */     return XToTileX(x, getTileGridXOffset(), getTileWidth());
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
/*     */   public int YToTileY(int y) {
/* 363 */     return YToTileY(y, getTileGridYOffset(), getTileHeight());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int tileXToX(int tx, int tileGridXOffset, int tileWidth) {
/* 372 */     return tx * tileWidth + tileGridXOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int tileYToY(int ty, int tileGridYOffset, int tileHeight) {
/* 381 */     return ty * tileHeight + tileGridYOffset;
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
/*     */   public int tileXToX(int tx) {
/* 393 */     return tx * this.tileWidth + this.tileGridXOffset;
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
/*     */   public int tileYToY(int ty) {
/* 405 */     return ty * this.tileHeight + this.tileGridYOffset;
/*     */   }
/*     */   
/*     */   public Vector getSources() {
/* 409 */     return null;
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
/*     */   public Raster getData() {
/* 430 */     Rectangle rect = new Rectangle(getMinX(), getMinY(), getWidth(), getHeight());
/* 431 */     return getData(rect);
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
/*     */   
/*     */   public Raster getData(Rectangle bounds) {
/* 453 */     Rectangle imageBounds = getBounds();
/*     */ 
/*     */     
/* 456 */     if (bounds == null) {
/* 457 */       bounds = imageBounds;
/* 458 */     } else if (!bounds.intersects(imageBounds)) {
/* 459 */       throw new IllegalArgumentException(I18N.getString("SimpleRenderedImage0"));
/*     */     } 
/*     */ 
/*     */     
/* 463 */     int startX = XToTileX(bounds.x);
/* 464 */     int startY = YToTileY(bounds.y);
/* 465 */     int endX = XToTileX(bounds.x + bounds.width - 1);
/* 466 */     int endY = YToTileY(bounds.y + bounds.height - 1);
/*     */ 
/*     */ 
/*     */     
/* 470 */     if (startX == endX && startY == endY) {
/* 471 */       Raster tile = getTile(startX, startY);
/* 472 */       return tile.createChild(bounds.x, bounds.y, bounds.width, bounds.height, bounds.x, bounds.y, null);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 478 */     if (!imageBounds.contains(bounds)) {
/* 479 */       Rectangle xsect = bounds.intersection(imageBounds);
/* 480 */       startX = XToTileX(xsect.x);
/* 481 */       startY = YToTileY(xsect.y);
/* 482 */       endX = XToTileX(xsect.x + xsect.width - 1);
/* 483 */       endY = YToTileY(xsect.y + xsect.height - 1);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 488 */     SampleModel sm = this.sampleModel.createCompatibleSampleModel(bounds.width, bounds.height);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 493 */     WritableRaster dest = Raster.createWritableRaster(sm, bounds.getLocation());
/*     */ 
/*     */     
/* 496 */     for (int j = startY; j <= endY; j++) {
/* 497 */       for (int i = startX; i <= endX; i++) {
/*     */         
/* 499 */         Raster tile = getTile(i, j);
/*     */ 
/*     */ 
/*     */         
/* 503 */         Rectangle tileRect = tile.getBounds();
/*     */         
/* 505 */         Rectangle intersectRect = bounds.intersection(tile.getBounds());
/* 506 */         Raster liveRaster = tile.createChild(intersectRect.x, intersectRect.y, intersectRect.width, intersectRect.height, intersectRect.x, intersectRect.y, null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 515 */         dest.setRect(liveRaster);
/*     */       } 
/*     */     } 
/*     */     
/* 519 */     return dest;
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
/*     */ 
/*     */   
/*     */   public WritableRaster copyData(WritableRaster dest) {
/* 542 */     Rectangle bounds, imageBounds = getBounds();
/*     */ 
/*     */     
/* 545 */     if (dest == null) {
/*     */       
/* 547 */       bounds = imageBounds;
/* 548 */       Point p = new Point(this.minX, this.minY);
/*     */       
/* 550 */       SampleModel sm = this.sampleModel.createCompatibleSampleModel(this.width, this.height);
/* 551 */       dest = Raster.createWritableRaster(sm, p);
/*     */     } else {
/* 553 */       bounds = dest.getBounds();
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 559 */     Rectangle xsect = imageBounds.contains(bounds) ? bounds : bounds.intersection(imageBounds);
/* 560 */     int startX = XToTileX(xsect.x);
/* 561 */     int startY = YToTileY(xsect.y);
/* 562 */     int endX = XToTileX(xsect.x + xsect.width - 1);
/* 563 */     int endY = YToTileY(xsect.y + xsect.height - 1);
/*     */ 
/*     */     
/* 566 */     for (int j = startY; j <= endY; j++) {
/* 567 */       for (int i = startX; i <= endX; i++) {
/*     */         
/* 569 */         Raster tile = getTile(i, j);
/*     */ 
/*     */ 
/*     */         
/* 573 */         Rectangle tileRect = tile.getBounds();
/*     */         
/* 575 */         Rectangle intersectRect = bounds.intersection(tile.getBounds());
/* 576 */         Raster liveRaster = tile.createChild(intersectRect.x, intersectRect.y, intersectRect.width, intersectRect.height, intersectRect.x, intersectRect.y, null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 585 */         dest.setRect(liveRaster);
/*     */       } 
/*     */     } 
/*     */     
/* 589 */     return dest;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\common\SimpleRenderedImage.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */