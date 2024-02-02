package org.xnio.streams;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

public final class LimitedOutputStream extends OutputStream {
   private final OutputStream delegate;
   private long remaining;

   public LimitedOutputStream(OutputStream delegate, long size) {
      this.delegate = delegate;
      this.remaining = size;
   }

   public void write(int b) throws IOException {
      long remaining = this.remaining;
      if (remaining < 1L) {
         throw notEnoughSpace();
      } else {
         this.delegate.write(b);
         this.remaining = remaining - 1L;
      }
   }

   public void write(byte[] b, int off, int len) throws IOException {
      long remaining = this.remaining;
      if (remaining < (long)len) {
         throw notEnoughSpace();
      } else {
         try {
            this.delegate.write(b, off, len);
            this.remaining = remaining - (long)len;
         } catch (InterruptedIOException var7) {
            this.remaining = remaining - ((long)var7.bytesTransferred & 4294967295L);
            throw var7;
         }
      }
   }

   public void flush() throws IOException {
      this.delegate.flush();
   }

   public void close() throws IOException {
      this.delegate.close();
   }

   private static IOException notEnoughSpace() {
      return new IOException("Not enough space in output stream");
   }
}
