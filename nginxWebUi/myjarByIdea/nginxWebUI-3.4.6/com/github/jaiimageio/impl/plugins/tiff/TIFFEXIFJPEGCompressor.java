package com.github.jaiimageio.impl.plugins.tiff;

import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;

public class TIFFEXIFJPEGCompressor extends TIFFBaseJPEGCompressor {
   public TIFFEXIFJPEGCompressor(ImageWriteParam param) {
      super("EXIF JPEG", 6, false, param);
   }

   public void setMetadata(IIOMetadata metadata) {
      super.setMetadata(metadata);
      this.initJPEGWriter(false, true);
   }
}
