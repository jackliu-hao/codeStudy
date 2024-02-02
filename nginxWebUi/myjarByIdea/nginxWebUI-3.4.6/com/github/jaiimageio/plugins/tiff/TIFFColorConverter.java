package com.github.jaiimageio.plugins.tiff;

public abstract class TIFFColorConverter {
   public abstract void fromRGB(float var1, float var2, float var3, float[] var4);

   public abstract void toRGB(float var1, float var2, float var3, float[] var4);
}
