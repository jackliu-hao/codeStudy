package org.wildfly.common.iteration;

import java.io.IOException;
import java.io.InputStream;

final class ByteIteratorInputStream extends InputStream {
   private final ByteIterator iter;

   ByteIteratorInputStream(ByteIterator iter) {
      this.iter = iter;
   }

   public int read() throws IOException {
      return this.iter.hasNext() ? this.iter.next() : -1;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (len == 0) {
         return 0;
      } else {
         return !this.iter.hasNext() ? -1 : this.iter.drain(b, off, len);
      }
   }
}
