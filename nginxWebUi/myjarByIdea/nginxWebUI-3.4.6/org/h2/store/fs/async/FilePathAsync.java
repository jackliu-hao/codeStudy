package org.h2.store.fs.async;

import java.io.IOException;
import java.nio.channels.FileChannel;
import org.h2.store.fs.FilePathWrapper;

public class FilePathAsync extends FilePathWrapper {
   public FileChannel open(String var1) throws IOException {
      return new FileAsync(this.name.substring(this.getScheme().length() + 1), var1);
   }

   public String getScheme() {
      return "async";
   }
}
