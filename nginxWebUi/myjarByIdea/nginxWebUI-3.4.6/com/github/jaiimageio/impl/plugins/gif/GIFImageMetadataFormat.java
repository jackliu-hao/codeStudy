package com.github.jaiimageio.impl.plugins.gif;

import java.util.Arrays;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class GIFImageMetadataFormat extends IIOMetadataFormatImpl {
   private static IIOMetadataFormat instance = null;

   private GIFImageMetadataFormat() {
      super("javax_imageio_gif_image_1.0", 2);
      this.addElement("ImageDescriptor", "javax_imageio_gif_image_1.0", 0);
      this.addAttribute("ImageDescriptor", "imageLeftPosition", 2, true, (String)null, "0", "65535", true, true);
      this.addAttribute("ImageDescriptor", "imageTopPosition", 2, true, (String)null, "0", "65535", true, true);
      this.addAttribute("ImageDescriptor", "imageWidth", 2, true, (String)null, "1", "65535", true, true);
      this.addAttribute("ImageDescriptor", "imageHeight", 2, true, (String)null, "1", "65535", true, true);
      this.addBooleanAttribute("ImageDescriptor", "interlaceFlag", false, false);
      this.addElement("LocalColorTable", "javax_imageio_gif_image_1.0", 2, 256);
      this.addAttribute("LocalColorTable", "sizeOfLocalColorTable", 2, true, (String)null, Arrays.asList(GIFStreamMetadata.colorTableSizes));
      this.addBooleanAttribute("LocalColorTable", "sortFlag", false, false);
      this.addElement("ColorTableEntry", "LocalColorTable", 0);
      this.addAttribute("ColorTableEntry", "index", 2, true, (String)null, "0", "255", true, true);
      this.addAttribute("ColorTableEntry", "red", 2, true, (String)null, "0", "255", true, true);
      this.addAttribute("ColorTableEntry", "green", 2, true, (String)null, "0", "255", true, true);
      this.addAttribute("ColorTableEntry", "blue", 2, true, (String)null, "0", "255", true, true);
      this.addElement("GraphicControlExtension", "javax_imageio_gif_image_1.0", 0);
      this.addAttribute("GraphicControlExtension", "disposalMethod", 0, true, (String)null, Arrays.asList(GIFImageMetadata.disposalMethodNames));
      this.addBooleanAttribute("GraphicControlExtension", "userInputFlag", false, false);
      this.addBooleanAttribute("GraphicControlExtension", "transparentColorFlag", false, false);
      this.addAttribute("GraphicControlExtension", "delayTime", 2, true, (String)null, "0", "65535", true, true);
      this.addAttribute("GraphicControlExtension", "transparentColorIndex", 2, true, (String)null, "0", "255", true, true);
      this.addElement("PlainTextExtension", "javax_imageio_gif_image_1.0", 0);
      this.addAttribute("PlainTextExtension", "textGridLeft", 2, true, (String)null, "0", "65535", true, true);
      this.addAttribute("PlainTextExtension", "textGridTop", 2, true, (String)null, "0", "65535", true, true);
      this.addAttribute("PlainTextExtension", "textGridWidth", 2, true, (String)null, "1", "65535", true, true);
      this.addAttribute("PlainTextExtension", "textGridHeight", 2, true, (String)null, "1", "65535", true, true);
      this.addAttribute("PlainTextExtension", "characterCellWidth", 2, true, (String)null, "1", "65535", true, true);
      this.addAttribute("PlainTextExtension", "characterCellHeight", 2, true, (String)null, "1", "65535", true, true);
      this.addAttribute("PlainTextExtension", "textForegroundColor", 2, true, (String)null, "0", "255", true, true);
      this.addAttribute("PlainTextExtension", "textBackgroundColor", 2, true, (String)null, "0", "255", true, true);
      this.addElement("ApplicationExtensions", "javax_imageio_gif_image_1.0", 1, Integer.MAX_VALUE);
      this.addElement("ApplicationExtension", "ApplicationExtensions", 0);
      this.addAttribute("ApplicationExtension", "applicationID", 0, true, (String)null);
      this.addAttribute("ApplicationExtension", "authenticationCode", 0, true, (String)null);
      this.addObjectValue("ApplicationExtension", Byte.TYPE, 0, Integer.MAX_VALUE);
      this.addElement("CommentExtensions", "javax_imageio_gif_image_1.0", 1, Integer.MAX_VALUE);
      this.addElement("CommentExtension", "CommentExtensions", 0);
      this.addAttribute("CommentExtension", "value", 0, true, (String)null);
   }

   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
      return true;
   }

   public static synchronized IIOMetadataFormat getInstance() {
      if (instance == null) {
         instance = new GIFImageMetadataFormat();
      }

      return instance;
   }
}
