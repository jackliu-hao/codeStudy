package com.github.jaiimageio.impl.plugins.pnm;

import java.util.ListResourceBundle;

public class PNMMetadataFormatResources extends ListResourceBundle {
   static final Object[][] contents = new Object[][]{{"FormatName", "The format name. One of PBM, PGM or PPM"}, {"Variant", "The variant: RAWBITS or ASCII"}, {"Width", "The image width"}, {"Height", "The image height"}, {"MaximumSample", "The maximum bit depth of one sample."}, {"Comment", "A comment."}};

   protected Object[][] getContents() {
      return contents;
   }
}
