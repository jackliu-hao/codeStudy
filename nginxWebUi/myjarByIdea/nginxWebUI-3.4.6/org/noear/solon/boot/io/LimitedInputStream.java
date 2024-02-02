package org.noear.solon.boot.io;

import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends FilterInputStream implements Closeable {
   private final long sizeMax;
   private long count;

   public LimitedInputStream(final InputStream inputStream, final long limitSize) {
      super(inputStream);
      this.sizeMax = limitSize;
   }

   protected void raiseError(long pSizeMax, long pCount) throws IOException {
      throw new LimitedInputException("The input stream is too large: " + pSizeMax);
   }

   private void checkLimit() throws IOException {
      if (this.sizeMax > 0L && this.count > this.sizeMax) {
         this.raiseError(this.sizeMax, this.count);
      }

   }

   public int read() throws IOException {
      int res = super.read();
      if (res != -1) {
         ++this.count;
         this.checkLimit();
      }

      return res;
   }

   public int read(final byte[] b, final int off, final int len) throws IOException {
      int res = super.read(b, off, len);
      if (res > 0) {
         this.count += (long)res;
         this.checkLimit();
      }

      return res;
   }

   public void close() throws IOException {
      super.close();
   }
}
