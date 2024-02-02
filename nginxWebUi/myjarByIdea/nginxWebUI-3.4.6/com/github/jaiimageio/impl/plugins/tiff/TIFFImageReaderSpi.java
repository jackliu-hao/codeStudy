package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.impl.common.PackageUtil;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public class TIFFImageReaderSpi extends ImageReaderSpi {
   private static final String[] names = new String[]{"tif", "TIF", "tiff", "TIFF"};
   private static final String[] suffixes = new String[]{"tif", "tiff"};
   private static final String[] MIMETypes = new String[]{"image/tiff"};
   private static final String readerClassName = "com.github.jaiimageio.impl.plugins.tiff.TIFFImageReader";
   private static final String[] writerSpiNames = new String[]{"com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriterSpi"};
   private boolean registered = false;

   public TIFFImageReaderSpi() {
      super(PackageUtil.getVendor(), PackageUtil.getVersion(), names, suffixes, MIMETypes, "com.github.jaiimageio.impl.plugins.tiff.TIFFImageReader", STANDARD_INPUT_TYPE, writerSpiNames, false, "com_sun_media_imageio_plugins_tiff_stream_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFStreamMetadataFormat", (String[])null, (String[])null, true, "com_sun_media_imageio_plugins_tiff_image_1.0", "com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadataFormat", (String[])null, (String[])null);
   }

   public String getDescription(Locale locale) {
      String desc = PackageUtil.getSpecificationTitle() + " TIFF Image Reader";
      return desc;
   }

   public boolean canDecodeInput(Object input) throws IOException {
      if (!(input instanceof ImageInputStream)) {
         return false;
      } else {
         ImageInputStream stream = (ImageInputStream)input;
         byte[] b = new byte[4];
         stream.mark();
         stream.readFully(b);
         stream.reset();
         return b[0] == 73 && b[1] == 73 && b[2] == 42 && b[3] == 0 || b[0] == 77 && b[1] == 77 && b[2] == 0 && b[3] == 42;
      }
   }

   public ImageReader createReaderInstance(Object extension) {
      return new TIFFImageReader(this);
   }

   public void onRegistration(ServiceRegistry registry, Class category) {
      if (!this.registered) {
         this.registered = true;
      }
   }
}
