package com.github.jaiimageio.impl.plugins.clib;

import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.stream.ImageOutputStream;

public final class OutputStreamAdapter extends OutputStream {
   ImageOutputStream stream;

   public OutputStreamAdapter(ImageOutputStream stream) {
      this.stream = stream;
   }

   public void close() throws IOException {
      this.stream.close();
   }

   public void write(byte[] b) throws IOException {
      this.stream.write(b);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.stream.write(b, off, len);
   }

   public void write(int b) throws IOException {
      this.stream.write(b);
   }
}
