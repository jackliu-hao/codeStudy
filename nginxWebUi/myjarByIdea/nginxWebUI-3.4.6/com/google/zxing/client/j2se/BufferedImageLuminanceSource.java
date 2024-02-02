package com.google.zxing.client.j2se;

import com.google.zxing.LuminanceSource;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;

public final class BufferedImageLuminanceSource extends LuminanceSource {
   private static final double MINUS_45_IN_RADIANS = -0.7853981633974483;
   private final BufferedImage image;
   private final int left;
   private final int top;

   public BufferedImageLuminanceSource(BufferedImage image) {
      this(image, 0, 0, image.getWidth(), image.getHeight());
   }

   public BufferedImageLuminanceSource(BufferedImage image, int left, int top, int width, int height) {
      super(width, height);
      if (image.getType() == 10) {
         this.image = image;
      } else {
         int sourceWidth = image.getWidth();
         int sourceHeight = image.getHeight();
         if (left + width > sourceWidth || top + height > sourceHeight) {
            throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
         }

         this.image = new BufferedImage(sourceWidth, sourceHeight, 10);
         WritableRaster raster = this.image.getRaster();
         int[] buffer = new int[width];

         for(int y = top; y < top + height; ++y) {
            image.getRGB(left, y, width, 1, buffer, 0, sourceWidth);

            for(int x = 0; x < width; ++x) {
               int pixel = buffer[x];
               if ((pixel & -16777216) == 0) {
                  pixel = -1;
               }

               buffer[x] = 306 * (pixel >> 16 & 255) + 601 * (pixel >> 8 & 255) + 117 * (pixel & 255) + 512 >> 10;
            }

            raster.setPixels(left, y, width, 1, buffer);
         }
      }

      this.left = left;
      this.top = top;
   }

   public byte[] getRow(int y, byte[] row) {
      if (y >= 0 && y < this.getHeight()) {
         int width = this.getWidth();
         if (row == null || row.length < width) {
            row = new byte[width];
         }

         this.image.getRaster().getDataElements(this.left, this.top + y, width, 1, row);
         return row;
      } else {
         throw new IllegalArgumentException("Requested row is outside the image: " + y);
      }
   }

   public byte[] getMatrix() {
      int width = this.getWidth();
      int height = this.getHeight();
      int area = width * height;
      byte[] matrix = new byte[area];
      this.image.getRaster().getDataElements(this.left, this.top, width, height, matrix);
      return matrix;
   }

   public boolean isCropSupported() {
      return true;
   }

   public LuminanceSource crop(int left, int top, int width, int height) {
      return new BufferedImageLuminanceSource(this.image, this.left + left, this.top + top, width, height);
   }

   public boolean isRotateSupported() {
      return true;
   }

   public LuminanceSource rotateCounterClockwise() {
      int sourceWidth = this.image.getWidth();
      int sourceHeight = this.image.getHeight();
      AffineTransform transform = new AffineTransform(0.0, -1.0, 1.0, 0.0, 0.0, (double)sourceWidth);
      BufferedImage rotatedImage = new BufferedImage(sourceHeight, sourceWidth, 10);
      Graphics2D g = rotatedImage.createGraphics();
      g.drawImage(this.image, transform, (ImageObserver)null);
      g.dispose();
      int width = this.getWidth();
      return new BufferedImageLuminanceSource(rotatedImage, this.top, sourceWidth - (this.left + width), this.getHeight(), width);
   }

   public LuminanceSource rotateCounterClockwise45() {
      int width = this.getWidth();
      int height = this.getHeight();
      int oldCenterX = this.left + width / 2;
      int oldCenterY = this.top + height / 2;
      AffineTransform transform = AffineTransform.getRotateInstance(-0.7853981633974483, (double)oldCenterX, (double)oldCenterY);
      int sourceDimension = Math.max(this.image.getWidth(), this.image.getHeight());
      BufferedImage rotatedImage = new BufferedImage(sourceDimension, sourceDimension, 10);
      Graphics2D g = rotatedImage.createGraphics();
      g.drawImage(this.image, transform, (ImageObserver)null);
      g.dispose();
      int halfDimension = Math.max(width, height) / 2;
      int newLeft = Math.max(0, oldCenterX - halfDimension);
      int newTop = Math.max(0, oldCenterY - halfDimension);
      int newRight = Math.min(sourceDimension - 1, oldCenterX + halfDimension);
      int newBottom = Math.min(sourceDimension - 1, oldCenterY + halfDimension);
      return new BufferedImageLuminanceSource(rotatedImage, newLeft, newTop, newRight - newLeft, newBottom - newTop);
   }
}
