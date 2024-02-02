package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFColorConverter;

public class TIFFCIELabColorConverter extends TIFFColorConverter {
   private static final float Xn = 95.047F;
   private static final float Yn = 100.0F;
   private static final float Zn = 108.883F;
   private static final float THRESHOLD = (float)Math.pow(0.008856, 0.3333333333333333);

   private float clamp(float x) {
      if (x < 0.0F) {
         return 0.0F;
      } else {
         return x > 100.0F ? 255.0F : x * 2.55F;
      }
   }

   private float clamp2(float x) {
      if (x < 0.0F) {
         return 0.0F;
      } else {
         return x > 255.0F ? 255.0F : x;
      }
   }

   public void fromRGB(float r, float g, float b, float[] result) {
      float X = 0.412453F * r + 0.35758F * g + 0.180423F * b;
      float Y = 0.212671F * r + 0.71516F * g + 0.072169F * b;
      float Z = 0.019334F * r + 0.119193F * g + 0.950227F * b;
      float YYn = Y / 100.0F;
      float XXn = X / 95.047F;
      float ZZn = Z / 108.883F;
      if (YYn < 0.008856F) {
         YYn = 7.787F * YYn + 0.13793103F;
      } else {
         YYn = (float)Math.pow((double)YYn, 0.3333333333333333);
      }

      if (XXn < 0.008856F) {
         XXn = 7.787F * XXn + 0.13793103F;
      } else {
         XXn = (float)Math.pow((double)XXn, 0.3333333333333333);
      }

      if (ZZn < 0.008856F) {
         ZZn = 7.787F * ZZn + 0.13793103F;
      } else {
         ZZn = (float)Math.pow((double)ZZn, 0.3333333333333333);
      }

      float LStar = 116.0F * YYn - 16.0F;
      float aStar = 500.0F * (XXn - YYn);
      float bStar = 200.0F * (YYn - ZZn);
      LStar *= 2.55F;
      if (aStar < 0.0F) {
         aStar += 256.0F;
      }

      if (bStar < 0.0F) {
         bStar += 256.0F;
      }

      result[0] = this.clamp2(LStar);
      result[1] = this.clamp2(aStar);
      result[2] = this.clamp2(bStar);
   }

   public void toRGB(float x0, float x1, float x2, float[] rgb) {
      float LStar = x0 * 100.0F / 255.0F;
      float aStar = x1 > 128.0F ? x1 - 256.0F : x1;
      float bStar = x2 > 128.0F ? x2 - 256.0F : x2;
      float YYn;
      float fY;
      float Y;
      if (LStar < 8.0F) {
         YYn = LStar / 903.3F;
         fY = 7.787F * YYn + 0.13793103F;
      } else {
         Y = (LStar + 16.0F) / 116.0F;
         YYn = Y * Y * Y;
         fY = (float)Math.pow((double)YYn, 0.3333333333333333);
      }

      Y = YYn * 100.0F;
      float fX = fY + aStar / 500.0F;
      float X;
      if (fX <= THRESHOLD) {
         X = 95.047F * (fX - 0.13793103F) / 7.787F;
      } else {
         X = 95.047F * fX * fX * fX;
      }

      float fZ = fY - bStar / 200.0F;
      float Z;
      if (fZ <= THRESHOLD) {
         Z = 108.883F * (fZ - 0.13793103F) / 7.787F;
      } else {
         Z = 108.883F * fZ * fZ * fZ;
      }

      float R = 3.240479F * X - 1.53715F * Y - 0.498535F * Z;
      float G = -0.969256F * X + 1.875992F * Y + 0.041556F * Z;
      float B = 0.055648F * X - 0.204043F * Y + 1.057311F * Z;
      rgb[0] = this.clamp(R);
      rgb[1] = this.clamp(G);
      rgb[2] = this.clamp(B);
   }
}
