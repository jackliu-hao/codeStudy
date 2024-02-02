package org.apache.commons.compress.compressors.lzma;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;
import org.tukaani.xz.LZMAInputStream;
import org.tukaani.xz.MemoryLimitException;

public class LZMACompressorInputStream extends CompressorInputStream implements InputStreamStatistics {
   private final CountingInputStream countingStream;
   private final InputStream in;

   public LZMACompressorInputStream(InputStream inputStream) throws IOException {
      this.in = new LZMAInputStream(this.countingStream = new CountingInputStream(inputStream), -1);
   }

   public LZMACompressorInputStream(InputStream inputStream, int memoryLimitInKb) throws IOException {
      try {
         this.in = new LZMAInputStream(this.countingStream = new CountingInputStream(inputStream), memoryLimitInKb);
      } catch (MemoryLimitException var4) {
         throw new org.apache.commons.compress.MemoryLimitException((long)var4.getMemoryNeeded(), var4.getMemoryLimit(), var4);
      }
   }

   public int read() throws IOException {
      int ret = this.in.read();
      this.count(ret == -1 ? 0 : 1);
      return ret;
   }

   public int read(byte[] buf, int off, int len) throws IOException {
      int ret = this.in.read(buf, off, len);
      this.count(ret);
      return ret;
   }

   public long skip(long n) throws IOException {
      return IOUtils.skip(this.in, n);
   }

   public int available() throws IOException {
      return this.in.available();
   }

   public void close() throws IOException {
      this.in.close();
   }

   public long getCompressedCount() {
      return this.countingStream.getBytesRead();
   }

   public static boolean matches(byte[] signature, int length) {
      return signature != null && length >= 3 && signature[0] == 93 && signature[1] == 0 && signature[2] == 0;
   }
}
