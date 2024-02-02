package com.github.jaiimageio.impl.plugins.pnm;

import java.util.Hashtable;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class PNMMetadataFormat extends IIOMetadataFormatImpl {
   private static Hashtable parents = new Hashtable();
   private static PNMMetadataFormat instance;
   String resourceBaseName = this.getClass().getName() + "Resources";

   public static synchronized PNMMetadataFormat getInstance() {
      if (instance == null) {
         instance = new PNMMetadataFormat();
      }

      return instance;
   }

   PNMMetadataFormat() {
      super("com_sun_media_imageio_plugins_pnm_image_1.0", 1);
      this.setResourceBaseName(this.resourceBaseName);
      this.addElements();
   }

   private void addElements() {
      this.addElement("FormatName", this.getParent("FormatName"), 0);
      this.addElement("Variant", this.getParent("Variant"), 0);
      this.addElement("Width", this.getParent("Width"), 0);
      this.addElement("Height", this.getParent("Height"), 0);
      this.addElement("MaximumSample", this.getParent("MaximumSample"), 0);
      this.addElement("Comment", this.getParent("Comment"), 0);
   }

   public String getParent(String elementName) {
      return (String)parents.get(elementName);
   }

   public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
      return this.getParent(elementName) != null;
   }

   static {
      parents.put("FormatName", "com_sun_media_imageio_plugins_pnm_image_1.0");
      parents.put("Variant", "com_sun_media_imageio_plugins_pnm_image_1.0");
      parents.put("Width", "com_sun_media_imageio_plugins_pnm_image_1.0");
      parents.put("Height", "com_sun_media_imageio_plugins_pnm_image_1.0");
      parents.put("MaximumSample", "com_sun_media_imageio_plugins_pnm_image_1.0");
      parents.put("Comment", "com_sun_media_imageio_plugins_pnm_image_1.0");
   }
}
