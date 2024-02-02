package com.github.jaiimageio.impl.plugins.bmp;

import com.github.jaiimageio.impl.common.ImageUtil;
import com.github.jaiimageio.impl.common.PackageUtil;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public class BMPImageReaderSpi extends ImageReaderSpi {
   private static String[] writerSpiNames = new String[]{"com.github.jaiimageio.impl.plugins.bmp.BMPImageWriterSpi"};
   private static String[] formatNames = new String[]{"bmp", "BMP"};
   private static String[] extensions = new String[]{"bmp"};
   private static String[] mimeTypes = new String[]{"image/bmp", "image/x-bmp", "image/x-windows-bmp"};
   private boolean registered = false;

   public BMPImageReaderSpi() {
      super(PackageUtil.getVendor(), PackageUtil.getVersion(), formatNames, extensions, mimeTypes, "com.github.jaiimageio.impl.plugins.bmp.BMPImageReader", STANDARD_INPUT_TYPE, writerSpiNames, false, (String)null, (String)null, (String[])null, (String[])null, true, "com_sun_media_imageio_plugins_bmp_image_1.0", "com.github.jaiimageio.impl.plugins.bmp.BMPMetadataFormat", (String[])null, (String[])null);
   }

   public void onRegistration(ServiceRegistry registry, Class category) {
      if (!this.registered) {
         this.registered = true;
         ImageUtil.processOnRegistration(registry, category, "BMP", this, 8, 7);
      }
   }

   public String getDescription(Locale locale) {
      String desc = PackageUtil.getSpecificationTitle() + " BMP Image Reader";
      return desc;
   }

   public boolean canDecodeInput(Object source) throws IOException {
      if (!(source instanceof ImageInputStream)) {
         return false;
      } else {
         ImageInputStream stream = (ImageInputStream)source;
         byte[] b = new byte[2];
         stream.mark();
         stream.readFully(b);
         stream.reset();
         return b[0] == 66 && b[1] == 77;
      }
   }

   public ImageReader createReaderInstance(Object extension) throws IIOException {
      return new BMPImageReader(this);
   }
}
