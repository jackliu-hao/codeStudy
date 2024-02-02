package org.apache.commons.compress.compressors.xz;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;
import org.tukaani.xz.MemoryLimitException;
import org.tukaani.xz.SingleXZInputStream;
import org.tukaani.xz.XZ;
import org.tukaani.xz.XZInputStream;

public class XZCompressorInputStream extends CompressorInputStream implements InputStreamStatistics {
   private final CountingInputStream countingStream;
   private final InputStream in;

   public static boolean matches(byte[] signature, int length) {
      if (length < XZ.HEADER_MAGIC.length) {
         return false;
      } else {
         for(int i = 0; i < XZ.HEADER_MAGIC.length; ++i) {
            if (signature[i] != XZ.HEADER_MAGIC[i]) {
               return false;
            }
         }

         return true;
      }
   }

   public XZCompressorInputStream(InputStream inputStream) throws IOException {
      this(inputStream, false);
   }

   public XZCompressorInputStream(InputStream inputStream, boolean decompressConcatenated) throws IOException {
      this(inputStream, decompressConcatenated, -1);
   }

   public XZCompressorInputStream(InputStream inputStream, boolean decompressConcatenated, int memoryLimitInKb) throws IOException {
      this.countingStream = new CountingInputStream(inputStream);
      if (decompressConcatenated) {
         this.in = new XZInputStream(this.countingStream, memoryLimitInKb);
      } else {
         this.in = new SingleXZInputStream(this.countingStream, memoryLimitInKb);
      }

   }

   public int read() throws IOException {
      try {
         int ret = this.in.read();
         this.count(ret == -1 ? -1 : 1);
         return ret;
      } catch (MemoryLimitException var2) {
         throw new org.apache.commons.compress.MemoryLimitException((long)var2.getMemoryNeeded(), var2.getMemoryLimit(), var2);
      }
   }

   public int read(byte[] buf, int off, int len) throws IOException {
      if (len == 0) {
         return 0;
      } else {
         try {
            int ret = this.in.read(buf, off, len);
            this.count(ret);
            return ret;
         } catch (MemoryLimitException var5) {
            throw new org.apache.commons.compress.MemoryLimitException((long)var5.getMemoryNeeded(), var5.getMemoryLimit(), var5);
         }
      }
   }

   public long skip(long n) throws IOException {
      try {
         return IOUtils.skip(this.in, n);
      } catch (MemoryLimitException var4) {
         throw new org.apache.commons.compress.MemoryLimitException((long)var4.getMemoryNeeded(), var4.getMemoryLimit(), var4);
      }
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
}
