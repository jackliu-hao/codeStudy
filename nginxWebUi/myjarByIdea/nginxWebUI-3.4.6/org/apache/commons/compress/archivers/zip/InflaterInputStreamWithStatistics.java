package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.apache.commons.compress.utils.InputStreamStatistics;

class InflaterInputStreamWithStatistics extends InflaterInputStream implements InputStreamStatistics {
   private long compressedCount;
   private long uncompressedCount;

   public InflaterInputStreamWithStatistics(InputStream in) {
      super(in);
   }

   public InflaterInputStreamWithStatistics(InputStream in, Inflater inf) {
      super(in, inf);
   }

   public InflaterInputStreamWithStatistics(InputStream in, Inflater inf, int size) {
      super(in, inf, size);
   }

   protected void fill() throws IOException {
      super.fill();
      this.compressedCount += (long)this.inf.getRemaining();
   }

   public int read() throws IOException {
      int b = super.read();
      if (b > -1) {
         ++this.uncompressedCount;
      }

      return b;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      int bytes = super.read(b, off, len);
      if (bytes > -1) {
         this.uncompressedCount += (long)bytes;
      }

      return bytes;
   }

   public long getCompressedCount() {
      return this.compressedCount;
   }

   public long getUncompressedCount() {
      return this.uncompressedCount;
   }
}
