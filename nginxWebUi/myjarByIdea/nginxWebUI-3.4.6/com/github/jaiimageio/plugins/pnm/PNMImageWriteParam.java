package com.github.jaiimageio.plugins.pnm;

import javax.imageio.ImageWriteParam;

public class PNMImageWriteParam extends ImageWriteParam {
   private boolean raw = true;

   public void setRaw(boolean raw) {
      this.raw = raw;
   }

   public boolean getRaw() {
      return this.raw;
   }
}
