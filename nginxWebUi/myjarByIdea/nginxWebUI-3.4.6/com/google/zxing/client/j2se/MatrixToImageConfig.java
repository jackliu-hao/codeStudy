package com.google.zxing.client.j2se;

public final class MatrixToImageConfig {
   public static final int BLACK = -16777216;
   public static final int WHITE = -1;
   private final int onColor;
   private final int offColor;

   public MatrixToImageConfig() {
      this(-16777216, -1);
   }

   public MatrixToImageConfig(int onColor, int offColor) {
      this.onColor = onColor;
      this.offColor = offColor;
   }

   public int getPixelOnColor() {
      return this.onColor;
   }

   public int getPixelOffColor() {
      return this.offColor;
   }

   int getBufferedImageColorModel() {
      if (this.onColor == -16777216 && this.offColor == -1) {
         return 12;
      } else {
         return !hasTransparency(this.onColor) && !hasTransparency(this.offColor) ? 1 : 2;
      }
   }

   private static boolean hasTransparency(int argb) {
      return (argb & -16777216) != -16777216;
   }
}
