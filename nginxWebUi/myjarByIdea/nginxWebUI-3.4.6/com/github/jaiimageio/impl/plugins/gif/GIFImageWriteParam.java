package com.github.jaiimageio.impl.plugins.gif;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

class GIFImageWriteParam extends ImageWriteParam {
   GIFImageWriteParam(Locale locale) {
      super(locale);
      this.canWriteCompressed = true;
      this.canWriteProgressive = true;
      this.compressionTypes = new String[]{"LZW", "lzw"};
      this.compressionType = this.compressionTypes[0];
   }

   public void setCompressionMode(int mode) {
      if (mode == 0) {
         throw new UnsupportedOperationException("MODE_DISABLED is not supported.");
      } else {
         super.setCompressionMode(mode);
      }
   }
}
