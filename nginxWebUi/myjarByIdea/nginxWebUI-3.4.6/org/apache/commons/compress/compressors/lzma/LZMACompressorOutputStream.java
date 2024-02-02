package org.apache.commons.compress.compressors.lzma;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.LZMAOutputStream;

public class LZMACompressorOutputStream extends CompressorOutputStream {
   private final LZMAOutputStream out;

   public LZMACompressorOutputStream(OutputStream outputStream) throws IOException {
      this.out = new LZMAOutputStream(outputStream, new LZMA2Options(), -1L);
   }

   public void write(int b) throws IOException {
      this.out.write(b);
   }

   public void write(byte[] buf, int off, int len) throws IOException {
      this.out.write(buf, off, len);
   }

   public void flush() throws IOException {
   }

   public void finish() throws IOException {
      this.out.finish();
   }

   public void close() throws IOException {
      this.out.close();
   }
}
