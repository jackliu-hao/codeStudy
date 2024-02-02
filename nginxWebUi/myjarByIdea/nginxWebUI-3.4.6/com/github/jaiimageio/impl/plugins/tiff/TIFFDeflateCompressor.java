package com.github.jaiimageio.impl.plugins.tiff;

import javax.imageio.ImageWriteParam;

public class TIFFDeflateCompressor extends TIFFDeflater {
   public TIFFDeflateCompressor(ImageWriteParam param, int predictor) {
      super("Deflate", 32946, param, predictor);
   }
}
