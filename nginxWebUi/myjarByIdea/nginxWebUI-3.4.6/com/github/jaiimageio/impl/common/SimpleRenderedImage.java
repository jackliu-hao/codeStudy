package com.github.jaiimageio.impl.common;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public abstract class SimpleRenderedImage implements RenderedImage {
   protected int minX;
   protected int minY;
   protected int width;
   protected int height;
   protected int tileWidth;
   protected int tileHeight;
   protected int tileGridXOffset = 0;
   protected int tileGridYOffset = 0;
   protected SampleModel sampleModel;
   protected ColorModel colorModel;
   protected Vector sources = new Vector();
   protected Hashtable properties = new Hashtable();

   public int getMinX() {
      return this.minX;
   }

   public final int getMaxX() {
      return this.getMinX() + this.getWidth();
   }

   public int getMinY() {
      return this.minY;
   }

   public final int getMaxY() {
      return this.getMinY() + this.getHeight();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public Rectangle getBounds() {
      return new Rectangle(this.getMinX(), this.getMinY(), this.getWidth(), this.getHeight());
   }

   public int getTileWidth() {
      return this.tileWidth;
   }

   public int getTileHeight() {
      return this.tileHeight;
   }

   public int getTileGridXOffset() {
      return this.tileGridXOffset;
   }

   public int getTileGridYOffset() {
      return this.tileGridYOffset;
   }

   public int getMinTileX() {
      return this.XToTileX(this.getMinX());
   }

   public int getMaxTileX() {
      return this.XToTileX(this.getMaxX() - 1);
   }

   public int getNumXTiles() {
      return this.getMaxTileX() - this.getMinTileX() + 1;
   }

   public int getMinTileY() {
      return this.YToTileY(this.getMinY());
   }

   public int getMaxTileY() {
      return this.YToTileY(this.getMaxY() - 1);
   }

   public int getNumYTiles() {
      return this.getMaxTileY() - this.getMinTileY() + 1;
   }

   public SampleModel getSampleModel() {
      return this.sampleModel;
   }

   public ColorModel getColorModel() {
      return this.colorModel;
   }

   public Object getProperty(String name) {
      name = name.toLowerCase();
      Object value = this.properties.get(name);
      return value != null ? value : Image.UndefinedProperty;
   }

   public String[] getPropertyNames() {
      String[] names = null;
      if (this.properties.size() > 0) {
         names = new String[this.properties.size()];
         int index = 0;

         String name;
         for(Enumeration e = this.properties.keys(); e.hasMoreElements(); names[index++] = name) {
            name = (String)e.nextElement();
         }
      }

      return names;
   }

   public String[] getPropertyNames(String prefix) {
      String[] propertyNames = this.getPropertyNames();
      if (propertyNames == null) {
         return null;
      } else {
         prefix = prefix.toLowerCase();
         Vector names = new Vector();

         for(int i = 0; i < propertyNames.length; ++i) {
            if (propertyNames[i].startsWith(prefix)) {
               names.addElement(propertyNames[i]);
            }
         }

         if (names.size() == 0) {
            return null;
         } else {
            String[] prefixNames = new String[names.size()];
            int count = 0;

            for(Iterator it = names.iterator(); it.hasNext(); prefixNames[count++] = (String)it.next()) {
            }

            return prefixNames;
         }
      }
   }

   public static int XToTileX(int x, int tileGridXOffset, int tileWidth) {
      x -= tileGridXOffset;
      if (x < 0) {
         x += 1 - tileWidth;
      }

      return x / tileWidth;
   }

   public static int YToTileY(int y, int tileGridYOffset, int tileHeight) {
      y -= tileGridYOffset;
      if (y < 0) {
         y += 1 - tileHeight;
      }

      return y / tileHeight;
   }

   public int XToTileX(int x) {
      return XToTileX(x, this.getTileGridXOffset(), this.getTileWidth());
   }

   public int YToTileY(int y) {
      return YToTileY(y, this.getTileGridYOffset(), this.getTileHeight());
   }

   public static int tileXToX(int tx, int tileGridXOffset, int tileWidth) {
      return tx * tileWidth + tileGridXOffset;
   }

   public static int tileYToY(int ty, int tileGridYOffset, int tileHeight) {
      return ty * tileHeight + tileGridYOffset;
   }

   public int tileXToX(int tx) {
      return tx * this.tileWidth + this.tileGridXOffset;
   }

   public int tileYToY(int ty) {
      return ty * this.tileHeight + this.tileGridYOffset;
   }

   public Vector getSources() {
      return null;
   }

   public Raster getData() {
      Rectangle rect = new Rectangle(this.getMinX(), this.getMinY(), this.getWidth(), this.getHeight());
      return this.getData(rect);
   }

   public Raster getData(Rectangle bounds) {
      Rectangle imageBounds = this.getBounds();
      if (bounds == null) {
         bounds = imageBounds;
      } else if (!bounds.intersects(imageBounds)) {
         throw new IllegalArgumentException(I18N.getString("SimpleRenderedImage0"));
      }

      int startX = this.XToTileX(bounds.x);
      int startY = this.YToTileY(bounds.y);
      int endX = this.XToTileX(bounds.x + bounds.width - 1);
      int endY = this.YToTileY(bounds.y + bounds.height - 1);
      if (startX == endX && startY == endY) {
         Raster tile = this.getTile(startX, startY);
         return tile.createChild(bounds.x, bounds.y, bounds.width, bounds.height, bounds.x, bounds.y, (int[])null);
      } else {
         if (!imageBounds.contains(bounds)) {
            Rectangle xsect = bounds.intersection(imageBounds);
            startX = this.XToTileX(xsect.x);
            startY = this.YToTileY(xsect.y);
            endX = this.XToTileX(xsect.x + xsect.width - 1);
            endY = this.YToTileY(xsect.y + xsect.height - 1);
         }

         SampleModel sm = this.sampleModel.createCompatibleSampleModel(bounds.width, bounds.height);
         WritableRaster dest = Raster.createWritableRaster(sm, bounds.getLocation());

         for(int j = startY; j <= endY; ++j) {
            for(int i = startX; i <= endX; ++i) {
               Raster tile = this.getTile(i, j);
               Rectangle tileRect = tile.getBounds();
               Rectangle intersectRect = bounds.intersection(tile.getBounds());
               Raster liveRaster = tile.createChild(intersectRect.x, intersectRect.y, intersectRect.width, intersectRect.height, intersectRect.x, intersectRect.y, (int[])null);
               dest.setRect(liveRaster);
            }
         }

         return dest;
      }
   }

   public WritableRaster copyData(WritableRaster dest) {
      Rectangle imageBounds = this.getBounds();
      Rectangle bounds;
      if (dest == null) {
         bounds = imageBounds;
         Point p = new Point(this.minX, this.minY);
         SampleModel sm = this.sampleModel.createCompatibleSampleModel(this.width, this.height);
         dest = Raster.createWritableRaster(sm, p);
      } else {
         bounds = dest.getBounds();
      }

      Rectangle xsect = imageBounds.contains(bounds) ? bounds : bounds.intersection(imageBounds);
      int startX = this.XToTileX(xsect.x);
      int startY = this.YToTileY(xsect.y);
      int endX = this.XToTileX(xsect.x + xsect.width - 1);
      int endY = this.YToTileY(xsect.y + xsect.height - 1);

      for(int j = startY; j <= endY; ++j) {
         for(int i = startX; i <= endX; ++i) {
            Raster tile = this.getTile(i, j);
            Rectangle tileRect = tile.getBounds();
            Rectangle intersectRect = bounds.intersection(tile.getBounds());
            Raster liveRaster = tile.createChild(intersectRect.x, intersectRect.y, intersectRect.width, intersectRect.height, intersectRect.x, intersectRect.y, (int[])null);
            dest.setRect(liveRaster);
         }
      }

      return dest;
   }
}
