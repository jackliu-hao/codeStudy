package com.github.jaiimageio.impl.plugins.tiff;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;

public class TIFFImageMetadataFormat extends TIFFMetadataFormat {
   private static TIFFImageMetadataFormat theInstance = null;

   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
      return false;
   }

   private TIFFImageMetadataFormat() {
      this.resourceBaseName = "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormatResources";
      this.rootName = "com_sun_media_imageio_plugins_tiff_image_1.0";
      String[] empty = new String[0];
      String[] childNames = new String[]{"TIFFIFD"};
      TIFFElementInfo einfo = new TIFFElementInfo(childNames, empty, 4);
      this.elementInfoMap.put("com_sun_media_imageio_plugins_tiff_image_1.0", einfo);
      childNames = new String[]{"TIFFField", "TIFFIFD"};
      String[] attrNames = new String[]{"tagSets", "parentTagNumber", "parentTagName"};
      einfo = new TIFFElementInfo(childNames, attrNames, 4);
      this.elementInfoMap.put("TIFFIFD", einfo);
      TIFFAttrInfo ainfo = new TIFFAttrInfo();
      ainfo.dataType = 0;
      ainfo.isRequired = true;
      this.attrInfoMap.put("TIFFIFD/tagSets", ainfo);
      ainfo = new TIFFAttrInfo();
      ainfo.dataType = 2;
      ainfo.isRequired = false;
      this.attrInfoMap.put("TIFFIFD/parentTagNumber", ainfo);
      ainfo = new TIFFAttrInfo();
      ainfo.dataType = 0;
      ainfo.isRequired = false;
      this.attrInfoMap.put("TIFFIFD/parentTagName", ainfo);
      String[] types = new String[]{"TIFFByte", "TIFFAscii", "TIFFShort", "TIFFSShort", "TIFFLong", "TIFFSLong", "TIFFRational", "TIFFSRational", "TIFFFloat", "TIFFDouble", "TIFFUndefined"};
      attrNames = new String[]{"value", "description"};
      String[] attrNamesValueOnly = new String[]{"value"};
      TIFFAttrInfo ainfoValue = new TIFFAttrInfo();
      TIFFAttrInfo ainfoDescription = new TIFFAttrInfo();

      int i;
      for(i = 0; i < types.length; ++i) {
         if (!types[i].equals("TIFFUndefined")) {
            childNames = new String[]{types[i]};
            einfo = new TIFFElementInfo(childNames, empty, 4);
            this.elementInfoMap.put(types[i] + "s", einfo);
         }

         boolean hasDescription = !types[i].equals("TIFFUndefined") && !types[i].equals("TIFFAscii") && !types[i].equals("TIFFRational") && !types[i].equals("TIFFSRational") && !types[i].equals("TIFFFloat") && !types[i].equals("TIFFDouble");
         String[] anames = hasDescription ? attrNames : attrNamesValueOnly;
         einfo = new TIFFElementInfo(empty, anames, 0);
         this.elementInfoMap.put(types[i], einfo);
         this.attrInfoMap.put(types[i] + "/value", ainfoValue);
         if (hasDescription) {
            this.attrInfoMap.put(types[i] + "/description", ainfoDescription);
         }
      }

      childNames = new String[2 * types.length - 1];

      for(i = 0; i < types.length; ++i) {
         childNames[2 * i] = types[i];
         if (!types[i].equals("TIFFUndefined")) {
            childNames[2 * i + 1] = types[i] + "s";
         }
      }

      attrNames = new String[]{"number", "name"};
      einfo = new TIFFElementInfo(childNames, attrNames, 3);
      this.elementInfoMap.put("TIFFField", einfo);
      ainfo = new TIFFAttrInfo();
      ainfo.isRequired = true;
      this.attrInfoMap.put("TIFFField/number", ainfo);
      ainfo = new TIFFAttrInfo();
      this.attrInfoMap.put("TIFFField/name", ainfo);
   }

   public static synchronized IIOMetadataFormat getInstance() {
      if (theInstance == null) {
         theInstance = new TIFFImageMetadataFormat();
      }

      return theInstance;
   }
}
