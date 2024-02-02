package com.github.jaiimageio.impl.plugins.tiff;

import java.util.ListResourceBundle;

public class TIFFStreamMetadataFormatResources extends ListResourceBundle {
   private static final Object[][] contents = new Object[][]{{"ByteOrder", "The stream byte order"}, {"ByteOrder/value", "One of \"BIG_ENDIAN\" or \"LITTLE_ENDIAN\""}};

   public Object[][] getContents() {
      return contents;
   }
}
