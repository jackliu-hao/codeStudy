package com.github.jaiimageio.impl.stream;

import com.github.jaiimageio.impl.common.PackageUtil;
import com.github.jaiimageio.stream.FileChannelImageInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.FileCacheImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class ChannelImageInputStreamSpi extends ImageInputStreamSpi {
   public ChannelImageInputStreamSpi() {
      super(PackageUtil.getVendor(), PackageUtil.getVersion(), ReadableByteChannel.class);
   }

   public ImageInputStream createInputStreamInstance(Object input, boolean useCache, File cacheDir) throws IOException {
      if (input != null && input instanceof ReadableByteChannel) {
         ImageInputStream stream = null;
         if (input instanceof FileChannel) {
            stream = new FileChannelImageInputStream((FileChannel)input);
         } else {
            InputStream inStream = Channels.newInputStream((ReadableByteChannel)input);
            if (useCache) {
               try {
                  stream = new FileCacheImageInputStream(inStream, cacheDir);
               } catch (IOException var7) {
               }
            }

            if (stream == null) {
               stream = new MemoryCacheImageInputStream(inStream);
            }
         }

         return (ImageInputStream)stream;
      } else {
         throw new IllegalArgumentException("XXX");
      }
   }

   public String getDescription(Locale locale) {
      return "NIO Channel ImageInputStream";
   }
}
