package org.h2.store.fs.niomapped;

import java.io.IOException;
import java.nio.channels.FileChannel;
import org.h2.store.fs.FilePathWrapper;

public class FilePathNioMapped extends FilePathWrapper {
   public FileChannel open(String var1) throws IOException {
      return new FileNioMapped(this.name.substring(this.getScheme().length() + 1), var1);
   }

   public String getScheme() {
      return "nioMapped";
   }
}
