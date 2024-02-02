package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.impl.common.PackageUtil;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;

public class TIFFImageWriterSpi extends ImageWriterSpi {
   private static final String[] names = new String[]{"tif", "TIF", "tiff", "TIFF"};
   private static final String[] suffixes = new String[]{"tif", "tiff"};
   private static final String[] MIMETypes = new String[]{"image/tiff"};
   private static final String writerClassName = "com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriter";
   private static final String[] readerSpiNames = new String[]{"com.github.jaiimageio.impl.plugins.tiff.TIFFImageReaderSpi"};
   private boolean registered = false;

   public TIFFImageWriterSpi() {
      super(PackageUtil.getVendor(), PackageUtil.getVersion(), names, suffixes, MIMETypes, "com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriter", STANDARD_OUTPUT_TYPE, readerSpiNames, false, "com_sun_media_imageio_plugins_tiff_stream_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFStreamMetadataFormat", (String[])null, (String[])null, false, "com_sun_media_imageio_plugins_tiff_image_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat", (String[])null, (String[])null);
   }

   public boolean canEncodeImage(ImageTypeSpecifier type) {
      return true;
   }

   public String getDescription(Locale locale) {
      String desc = PackageUtil.getSpecificationTitle() + " TIFF Image Writer";
      return desc;
   }

   public ImageWriter createWriterInstance(Object extension) {
      return new TIFFImageWriter(this);
   }

   public void onRegistration(ServiceRegistry registry, Class category) {
      if (!this.registered) {
         this.registered = true;
      }
   }
}
