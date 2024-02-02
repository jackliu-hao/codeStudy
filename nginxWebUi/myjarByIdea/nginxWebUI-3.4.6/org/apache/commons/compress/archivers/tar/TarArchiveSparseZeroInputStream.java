package org.apache.commons.compress.archivers.tar;

import java.io.IOException;
import java.io.InputStream;

class TarArchiveSparseZeroInputStream extends InputStream {
   public int read() throws IOException {
      return 0;
   }

   public long skip(long n) {
      return n;
   }
}
