package com.github.jaiimageio.impl.plugins.tiff;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;

public class TIFFStreamMetadataFormat extends TIFFMetadataFormat {
   private static TIFFStreamMetadataFormat theInstance = null;

   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
      return false;
   }

   private TIFFStreamMetadataFormat() {
      this.resourceBaseName = "com.github.jaiimageio.impl.plugins.tiff.TIFFStreamMetadataFormatResources";
      this.rootName = "com_sun_media_imageio_plugins_tiff_stream_1.0";
      String[] empty = new String[0];
      String[] childNames = new String[]{"ByteOrder"};
      TIFFElementInfo einfo = new TIFFElementInfo(childNames, empty, 1);
      this.elementInfoMap.put("com_sun_media_imageio_plugins_tiff_stream_1.0", einfo);
      String[] attrNames = new String[]{"value"};
      einfo = new TIFFElementInfo(empty, attrNames, 0);
      this.elementInfoMap.put("ByteOrder", einfo);
      TIFFAttrInfo ainfo = new TIFFAttrInfo();
      ainfo.dataType = 0;
      ainfo.isRequired = true;
      this.attrInfoMap.put("ByteOrder/value", ainfo);
   }

   public static synchronized IIOMetadataFormat getInstance() {
      if (theInstance == null) {
         theInstance = new TIFFStreamMetadataFormat();
      }

      return theInstance;
   }
}
