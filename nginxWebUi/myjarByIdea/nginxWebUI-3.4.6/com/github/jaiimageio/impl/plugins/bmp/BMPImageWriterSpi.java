package com.github.jaiimageio.impl.plugins.bmp;

import com.github.jaiimageio.impl.common.ImageUtil;
import com.github.jaiimageio.impl.common.PackageUtil;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;

public class BMPImageWriterSpi extends ImageWriterSpi {
   private static String[] readerSpiNames = new String[]{"com.github.jaiimageio.impl.plugins.bmp.BMPImageReaderSpi"};
   private static String[] formatNames = new String[]{"bmp", "BMP"};
   private static String[] extensions = new String[]{"bmp"};
   private static String[] mimeTypes = new String[]{"image/bmp", "image/x-bmp", "image/x-windows-bmp"};
   private boolean registered = false;

   public BMPImageWriterSpi() {
      super(PackageUtil.getVendor(), PackageUtil.getVersion(), formatNames, extensions, mimeTypes, "com.github.jaiimageio.impl.plugins.bmp.BMPImageWriter", STANDARD_OUTPUT_TYPE, readerSpiNames, false, (String)null, (String)null, (String[])null, (String[])null, true, "com_sun_media_imageio_plugins_bmp_image_1.0", "com.github.jaiimageio.impl.plugins.bmp.BMPMetadataFormat", (String[])null, (String[])null);
   }

   public String getDescription(Locale locale) {
      String desc = PackageUtil.getSpecificationTitle() + " BMP Image Writer";
      return desc;
   }

   public void onRegistration(ServiceRegistry registry, Class category) {
      if (!this.registered) {
         this.registered = true;
         ImageUtil.processOnRegistration(registry, category, "BMP", this, 8, 7);
      }
   }

   public boolean canEncodeImage(ImageTypeSpecifier type) {
      int dataType = type.getSampleModel().getDataType();
      if (dataType >= 0 && dataType <= 3) {
         SampleModel sm = type.getSampleModel();
         int numBands = sm.getNumBands();
         if (numBands != 1 && numBands != 3) {
            return false;
         } else if (numBands == 1 && dataType != 0) {
            return false;
         } else {
            return dataType <= 0 || sm instanceof SinglePixelPackedSampleModel;
         }
      } else {
         return false;
      }
   }

   public ImageWriter createWriterInstance(Object extension) throws IIOException {
      return new BMPImageWriter(this);
   }
}
