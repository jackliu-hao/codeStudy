package com.github.jaiimageio.impl.plugins.pcx;

import com.github.jaiimageio.impl.common.PackageUtil;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public class PCXImageReaderSpi extends ImageReaderSpi {
   private static String[] writerSpiNames = new String[]{"com.github.jaiimageio.impl.plugins.pcx.PCXImageWriterSpi"};
   private static String[] formatNames = new String[]{"pcx", "PCX"};
   private static String[] extensions = new String[]{"pcx"};
   private static String[] mimeTypes = new String[]{"image/pcx", "image/x-pcx", "image/x-windows-pcx", "image/x-pc-paintbrush"};
   private boolean registered = false;

   public PCXImageReaderSpi() {
      super(PackageUtil.getVendor(), PackageUtil.getVersion(), formatNames, extensions, mimeTypes, "com.github.jaiimageio.impl.plugins.pcx.PCXImageReader", STANDARD_INPUT_TYPE, writerSpiNames, false, (String)null, (String)null, (String[])null, (String[])null, true, (String)null, (String)null, (String[])null, (String[])null);
   }

   public void onRegistration(ServiceRegistry registry, Class category) {
      if (!this.registered) {
         this.registered = true;
      }
   }

   public String getDescription(Locale locale) {
      String desc = PackageUtil.getSpecificationTitle() + " PCX Image Reader";
      return desc;
   }

   public boolean canDecodeInput(Object source) throws IOException {
      if (!(source instanceof ImageInputStream)) {
         return false;
      } else {
         ImageInputStream stream = (ImageInputStream)source;
         stream.mark();
         byte b = stream.readByte();
         stream.reset();
         return b == 10;
      }
   }

   public ImageReader createReaderInstance(Object extension) throws IIOException {
      return new PCXImageReader(this);
   }
}
