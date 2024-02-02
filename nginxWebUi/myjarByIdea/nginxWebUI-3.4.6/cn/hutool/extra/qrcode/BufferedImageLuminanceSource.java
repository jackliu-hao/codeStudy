package cn.hutool.extra.qrcode;

import com.google.zxing.LuminanceSource;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public final class BufferedImageLuminanceSource extends LuminanceSource {
   private final BufferedImage image;
   private final int left;
   private final int top;

   public BufferedImageLuminanceSource(BufferedImage image) {
      this(image, 0, 0, image.getWidth(), image.getHeight());
   }

   public BufferedImageLuminanceSource(BufferedImage image, int left, int top, int width, int height) {
      super(width, height);
      int sourceWidth = image.getWidth();
      int sourceHeight = image.getHeight();
      if (left + width <= sourceWidth && top + height <= sourceHeight) {
         for(int y = top; y < top + height; ++y) {
            for(int x = left; x < left + width; ++x) {
               if ((image.getRGB(x, y) & -16777216) == 0) {
                  image.setRGB(x, y, -1);
               }
            }
         }

         this.image = new BufferedImage(sourceWidth, sourceHeight, 10);
         this.image.getGraphics().drawImage(image, 0, 0, (ImageObserver)null);
         this.left = left;
         this.top = top;
      } else {
         throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
      }
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
}
