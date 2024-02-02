package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.OutputStream;

/** @deprecated */
public class StringOutputStream extends OutputStream {
   private StringBuffer buf = new StringBuffer();

   public void write(byte[] b) throws IOException {
      this.buf.append(new String(b));
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.buf.append(new String(b, off, len));
   }

   public void write(int b) throws IOException {
      byte[] bytes = new byte[]{(byte)b};
      this.buf.append(new String(bytes));
   }

   public String toString() {
      return this.buf.toString();
   }
}
