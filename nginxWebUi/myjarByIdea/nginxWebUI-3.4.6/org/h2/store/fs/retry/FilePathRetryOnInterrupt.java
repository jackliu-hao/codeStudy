package org.h2.store.fs.retry;

import java.io.IOException;
import java.nio.channels.FileChannel;
import org.h2.store.fs.FilePathWrapper;

public class FilePathRetryOnInterrupt extends FilePathWrapper {
   static final String SCHEME = "retry";

   public FileChannel open(String var1) throws IOException {
      return new FileRetryOnInterrupt(this.name.substring(this.getScheme().length() + 1), var1);
   }

   public String getScheme() {
      return "retry";
   }
}
