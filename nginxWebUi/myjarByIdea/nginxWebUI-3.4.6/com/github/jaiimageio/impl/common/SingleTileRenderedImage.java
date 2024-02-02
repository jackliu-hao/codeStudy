package com.github.jaiimageio.impl.common;

import java.awt.image.ColorModel;
import java.awt.image.Raster;

public class SingleTileRenderedImage extends SimpleRenderedImage {
   Raster ras;

   public SingleTileRenderedImage(Raster ras, ColorModel colorModel) {
      this.ras = ras;
      this.tileGridXOffset = this.minX = ras.getMinX();
      this.tileGridYOffset = this.minY = ras.getMinY();
      this.tileWidth = this.width = ras.getWidth();
      this.tileHeight = this.height = ras.getHeight();
      this.sampleModel = ras.getSampleModel();
      this.colorModel = colorModel;
   }

   public Raster getTile(int tileX, int tileY) {
      if (tileX == 0 && tileY == 0) {
         return this.ras;
      } else {
         throw new IllegalArgumentException("tileX != 0 || tileY != 0");
      }
   }
}
