package org.codehaus.plexus.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileInputStreamFacade implements InputStreamFacade {
   private final File file;

   public FileInputStreamFacade(File file) {
      this.file = file;
   }

   public InputStream getInputStream() throws IOException {
      return new FileInputStream(this.file);
   }
}
