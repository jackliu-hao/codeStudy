package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

class TempFileCachingStreamBridge extends StreamBridge {
   private final File f = File.createTempFile("commons-compress", "packtemp");

   TempFileCachingStreamBridge() throws IOException {
      this.f.deleteOnExit();
      this.out = Files.newOutputStream(this.f.toPath());
   }

   InputStream getInputView() throws IOException {
      this.out.close();
      return new FilterInputStream(Files.newInputStream(this.f.toPath())) {
         public void close() throws IOException {
            try {
               super.close();
            } finally {
               TempFileCachingStreamBridge.this.f.delete();
            }

         }
      };
   }
}
