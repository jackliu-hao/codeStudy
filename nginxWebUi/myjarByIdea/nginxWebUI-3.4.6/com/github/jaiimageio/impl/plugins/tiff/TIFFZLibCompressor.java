package com.github.jaiimageio.impl.plugins.tiff;

import javax.imageio.ImageWriteParam;

public class TIFFZLibCompressor extends TIFFDeflater {
   public TIFFZLibCompressor(ImageWriteParam param, int predictor) {
      super("ZLib", 8, param, predictor);
   }
}
