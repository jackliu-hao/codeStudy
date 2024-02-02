package com.github.jaiimageio.impl.plugins.gif;

import java.util.Arrays;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class GIFStreamMetadataFormat extends IIOMetadataFormatImpl {
   private static IIOMetadataFormat instance = null;

   private GIFStreamMetadataFormat() {
      super("javax_imageio_gif_stream_1.0", 2);
      this.addElement("Version", "javax_imageio_gif_stream_1.0", 0);
      this.addAttribute("Version", "value", 0, true, (String)null, Arrays.asList(GIFStreamMetadata.versionStrings));
      this.addElement("LogicalScreenDescriptor", "javax_imageio_gif_stream_1.0", 0);
      this.addAttribute("LogicalScreenDescriptor", "logicalScreenWidth", 2, true, (String)null, "1", "65535", true, true);
      this.addAttribute("LogicalScreenDescriptor", "logicalScreenHeight", 2, true, (String)null, "1", "65535", true, true);
      this.addAttribute("LogicalScreenDescriptor", "colorResolution", 2, true, (String)null, "1", "8", true, true);
      this.addAttribute("LogicalScreenDescriptor", "pixelAspectRatio", 2, true, (String)null, "0", "255", true, true);
      this.addElement("GlobalColorTable", "javax_imageio_gif_stream_1.0", 2, 256);
      this.addAttribute("GlobalColorTable", "sizeOfGlobalColorTable", 2, true, (String)null, Arrays.asList(GIFStreamMetadata.colorTableSizes));
      this.addAttribute("GlobalColorTable", "backgroundColorIndex", 2, true, (String)null, "0", "255", true, true);
      this.addBooleanAttribute("GlobalColorTable", "sortFlag", false, false);
      this.addElement("ColorTableEntry", "GlobalColorTable", 0);
      this.addAttribute("ColorTableEntry", "index", 2, true, (String)null, "0", "255", true, true);
      this.addAttribute("ColorTableEntry", "red", 2, true, (String)null, "0", "255", true, true);
      this.addAttribute("ColorTableEntry", "green", 2, true, (String)null, "0", "255", true, true);
      this.addAttribute("ColorTableEntry", "blue", 2, true, (String)null, "0", "255", true, true);
   }

   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
      return true;
   }

   public static synchronized IIOMetadataFormat getInstance() {
      if (instance == null) {
         instance = new GIFStreamMetadataFormat();
      }

      return instance;
   }
}
