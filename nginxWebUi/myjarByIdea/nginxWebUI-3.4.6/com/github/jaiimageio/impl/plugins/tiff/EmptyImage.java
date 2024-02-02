package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.impl.common.SimpleRenderedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

class EmptyImage extends SimpleRenderedImage {
   EmptyImage(int minX, int minY, int width, int height, int tileGridXOffset, int tileGridYOffset, int tileWidth, int tileHeight, SampleModel sampleModel, ColorModel colorModel) {
      this.minX = minX;
      this.minY = minY;
      this.width = width;
      this.height = height;
      this.tileGridXOffset = tileGridXOffset;
      this.tileGridYOffset = tileGridYOffset;
      this.tileWidth = tileWidth;
      this.tileHeight = tileHeight;
      this.sampleModel = sampleModel;
      this.colorModel = colorModel;
   }

   public Raster getTile(int tileX, int tileY) {
      return null;
   }
}
