package com.github.jaiimageio.impl.plugins.clib;

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStream;

public final class InputStreamAdapter extends InputStream {
   ImageInputStream stream;

   public InputStreamAdapter(ImageInputStream stream) {
      this.stream = stream;
   }

   public void close() throws IOException {
      this.stream.close();
   }

   public void mark(int readlimit) {
      this.stream.mark();
   }

   public boolean markSupported() {
      return true;
   }

   public int read() throws IOException {
      return this.stream.read();
   }

   public int read(byte[] b, int off, int len) throws IOException {
      return this.stream.read(b, off, len);
   }

   public void reset() throws IOException {
      this.stream.reset();
   }

   public long skip(long n) throws IOException {
      return this.stream.skipBytes(n);
   }

   public ImageInputStream getWrappedStream() {
      return this.stream;
   }
}
