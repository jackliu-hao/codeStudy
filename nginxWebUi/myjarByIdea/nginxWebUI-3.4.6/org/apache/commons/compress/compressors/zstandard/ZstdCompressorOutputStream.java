package org.apache.commons.compress.compressors.zstandard;

import com.github.luben.zstd.ZstdOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;

public class ZstdCompressorOutputStream extends CompressorOutputStream {
   private final ZstdOutputStream encOS;

   public ZstdCompressorOutputStream(OutputStream outStream, int level, boolean closeFrameOnFlush, boolean useChecksum) throws IOException {
      this.encOS = new ZstdOutputStream(outStream, level);
      this.encOS.setCloseFrameOnFlush(closeFrameOnFlush);
      this.encOS.setChecksum(useChecksum);
   }

   public ZstdCompressorOutputStream(OutputStream outStream, int level, boolean closeFrameOnFlush) throws IOException {
      this.encOS = new ZstdOutputStream(outStream, level);
      this.encOS.setCloseFrameOnFlush(closeFrameOnFlush);
   }

   public ZstdCompressorOutputStream(OutputStream outStream, int level) throws IOException {
      this.encOS = new ZstdOutputStream(outStream, level);
   }

   public ZstdCompressorOutputStream(OutputStream outStream) throws IOException {
      this.encOS = new ZstdOutputStream(outStream);
   }

   public void close() throws IOException {
      this.encOS.close();
   }

   public void write(int b) throws IOException {
      this.encOS.write(b);
   }

   public void write(byte[] buf, int off, int len) throws IOException {
      this.encOS.write(buf, off, len);
   }

   public String toString() {
      return this.encOS.toString();
   }

   public void flush() throws IOException {
      this.encOS.flush();
   }
}
