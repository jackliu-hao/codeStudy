package com.github.jaiimageio.impl.common;

import java.awt.color.ColorSpace;

public final class SimpleCMYKColorSpace extends ColorSpace {
   private static ColorSpace theInstance = null;
   private ColorSpace csRGB = ColorSpace.getInstance(1004);
   private static final double power1 = 0.4166666666666667;

   public static final synchronized ColorSpace getInstance() {
      if (theInstance == null) {
         theInstance = new SimpleCMYKColorSpace();
      }

      return theInstance;
   }

   private SimpleCMYKColorSpace() {
      super(9, 4);
   }

   public boolean equals(Object o) {
      return o != null && o instanceof SimpleCMYKColorSpace;
   }

   public float[] toRGB(float[] colorvalue) {
      float C = colorvalue[0];
      float M = colorvalue[1];
      float Y = colorvalue[2];
      float K = colorvalue[3];
      float K1 = 1.0F - K;
      float[] rgbvalue = new float[]{K1 * (1.0F - C), K1 * (1.0F - M), K1 * (1.0F - Y)};

      for(int i = 0; i < 3; ++i) {
         float v = rgbvalue[i];
         if (v < 0.0F) {
            v = 0.0F;
         }

         if (v < 0.0031308F) {
            rgbvalue[i] = 12.92F * v;
         } else {
            if (v > 1.0F) {
               v = 1.0F;
            }

            rgbvalue[i] = (float)(1.055 * Math.pow((double)v, 0.4166666666666667) - 0.055);
         }
      }

      return rgbvalue;
   }

   public float[] fromRGB(float[] rgbvalue) {
      for(int i = 0; i < 3; ++i) {
         if (rgbvalue[i] < 0.040449936F) {
            rgbvalue[i] /= 12.92F;
         } else {
            rgbvalue[i] = (float)Math.pow(((double)rgbvalue[i] + 0.055) / 1.055, 2.4);
         }
      }

      float C = 1.0F - rgbvalue[0];
      float M = 1.0F - rgbvalue[1];
      float Y = 1.0F - rgbvalue[2];
      float K = Math.min(C, Math.min(M, Y));
      if (K != 1.0F) {
         float K1 = 1.0F - K;
         C = (C - K) / K1;
         M = (M - K) / K1;
         Y = (Y - K) / K1;
      } else {
         Y = 0.0F;
         M = 0.0F;
         C = 0.0F;
      }

      return new float[]{C, M, Y, K};
   }

   public float[] toCIEXYZ(float[] colorvalue) {
      return this.csRGB.toCIEXYZ(this.toRGB(colorvalue));
   }

   public float[] fromCIEXYZ(float[] xyzvalue) {
      return this.fromRGB(this.csRGB.fromCIEXYZ(xyzvalue));
   }
}
