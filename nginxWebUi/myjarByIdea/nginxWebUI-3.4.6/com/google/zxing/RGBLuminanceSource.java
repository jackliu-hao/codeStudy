package com.google.zxing;

public final class RGBLuminanceSource extends LuminanceSource {
   private final byte[] luminances;
   private final int dataWidth;
   private final int dataHeight;
   private final int left;
   private final int top;

   public RGBLuminanceSource(int width, int height, int[] pixels) {
      super(width, height);
      this.dataWidth = width;
      this.dataHeight = height;
      this.left = 0;
      this.top = 0;
      int size = width * height;
      this.luminances = new byte[size];

      for(int offset = 0; offset < size; ++offset) {
         int pixel;
         int r = (pixel = pixels[offset]) >> 16 & 255;
         int g2 = pixel >> 7 & 510;
         int b = pixel & 255;
         this.luminances[offset] = (byte)((r + g2 + b) / 4);
      }

   }

   private RGBLuminanceSource(byte[] pixels, int dataWidth, int dataHeight, int left, int top, int width, int height) {
      super(width, height);
      if (left + width <= dataWidth && top + height <= dataHeight) {
         this.luminances = pixels;
         this.dataWidth = dataWidth;
         this.dataHeight = dataHeight;
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

         int offset = (y + this.top) * this.dataWidth + this.left;
         System.arraycopy(this.luminances, offset, row, 0, width);
         return row;
      } else {
         throw new IllegalArgumentException("Requested row is outside the image: " + y);
      }
   }

   public byte[] getMatrix() {
      int width = this.getWidth();
      int height = this.getHeight();
      if (width == this.dataWidth && height == this.dataHeight) {
         return this.luminances;
      } else {
         int area;
         byte[] matrix = new byte[area = width * height];
         int inputOffset = this.top * this.dataWidth + this.left;
         if (width == this.dataWidth) {
            System.arraycopy(this.luminances, inputOffset, matrix, 0, area);
            return matrix;
         } else {
            for(int y = 0; y < height; ++y) {
               int outputOffset = y * width;
               System.arraycopy(this.luminances, inputOffset, matrix, outputOffset, width);
               inputOffset += this.dataWidth;
            }

            return matrix;
         }
      }
   }

   public boolean isCropSupported() {
      return true;
   }

   public LuminanceSource crop(int left, int top, int width, int height) {
      return new RGBLuminanceSource(this.luminances, this.dataWidth, this.dataHeight, this.left + left, this.top + top, width, height);
   }
}
