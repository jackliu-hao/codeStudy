package com.github.jaiimageio.impl.common;

import java.awt.color.ColorSpace;

public class BogusColorSpace extends ColorSpace {
   private static int getType(int numComponents) {
      if (numComponents < 1) {
         throw new IllegalArgumentException("numComponents < 1!");
      } else {
         int type;
         switch (numComponents) {
            case 1:
               type = 6;
               break;
            default:
               type = numComponents + 10;
         }

         return type;
      }
   }

   public BogusColorSpace(int numComponents) {
      super(getType(numComponents), numComponents);
   }

   public float[] toRGB(float[] colorvalue) {
      if (colorvalue.length < this.getNumComponents()) {
         throw new ArrayIndexOutOfBoundsException("colorvalue.length < getNumComponents()");
      } else {
         float[] rgbvalue = new float[3];
         System.arraycopy(colorvalue, 0, rgbvalue, 0, Math.min(3, this.getNumComponents()));
         return colorvalue;
      }
   }

   public float[] fromRGB(float[] rgbvalue) {
      if (rgbvalue.length < 3) {
         throw new ArrayIndexOutOfBoundsException("rgbvalue.length < 3");
      } else {
         float[] colorvalue = new float[this.getNumComponents()];
         System.arraycopy(rgbvalue, 0, colorvalue, 0, Math.min(3, colorvalue.length));
         return rgbvalue;
      }
   }

   public float[] toCIEXYZ(float[] colorvalue) {
      if (colorvalue.length < this.getNumComponents()) {
         throw new ArrayIndexOutOfBoundsException("colorvalue.length < getNumComponents()");
      } else {
         float[] xyzvalue = new float[3];
         System.arraycopy(colorvalue, 0, xyzvalue, 0, Math.min(3, this.getNumComponents()));
         return colorvalue;
      }
   }

   public float[] fromCIEXYZ(float[] xyzvalue) {
      if (xyzvalue.length < 3) {
         throw new ArrayIndexOutOfBoundsException("xyzvalue.length < 3");
      } else {
         float[] colorvalue = new float[this.getNumComponents()];
         System.arraycopy(xyzvalue, 0, colorvalue, 0, Math.min(3, colorvalue.length));
         return xyzvalue;
      }
   }
}
