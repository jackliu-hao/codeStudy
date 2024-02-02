package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.Checksum;

public class ChecksumCalculatingInputStream extends InputStream {
   private final InputStream in;
   private final Checksum checksum;

   public ChecksumCalculatingInputStream(Checksum checksum, InputStream inputStream) {
      Objects.requireNonNull(checksum, "checksum");
      Objects.requireNonNull(inputStream, "in");
      this.checksum = checksum;
      this.in = inputStream;
   }

   public int read() throws IOException {
      int ret = this.in.read();
      if (ret >= 0) {
         this.checksum.update(ret);
      }

      return ret;
   }

   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (len == 0) {
         return 0;
      } else {
         int ret = this.in.read(b, off, len);
         if (ret >= 0) {
            this.checksum.update(b, off, ret);
         }

         return ret;
      }
   }

   public long skip(long n) throws IOException {
      return this.read() >= 0 ? 1L : 0L;
   }

   public long getValue() {
      return this.checksum.getValue();
   }
}
