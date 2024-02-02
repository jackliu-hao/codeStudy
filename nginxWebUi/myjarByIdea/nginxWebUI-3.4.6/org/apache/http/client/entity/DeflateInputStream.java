package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

public class DeflateInputStream extends InputStream {
   private final InputStream sourceStream;

   public DeflateInputStream(InputStream wrapped) throws IOException {
      PushbackInputStream pushback = new PushbackInputStream(wrapped, 2);
      int i1 = pushback.read();
      int i2 = pushback.read();
      if (i1 != -1 && i2 != -1) {
         pushback.unread(i2);
         pushback.unread(i1);
         boolean nowrap = true;
         int b1 = i1 & 255;
         int compressionMethod = b1 & 15;
         int compressionInfo = b1 >> 4 & 15;
         int b2 = i2 & 255;
         if (compressionMethod == 8 && compressionInfo <= 7 && (b1 << 8 | b2) % 31 == 0) {
            nowrap = false;
         }

         this.sourceStream = new DeflateStream(pushback, new Inflater(nowrap));
      } else {
         throw new ZipException("Unexpected end of stream");
      }
   }

   public int read() throws IOException {
      return this.sourceStream.read();
   }

   public int read(byte[] b) throws IOException {
      return this.sourceStream.read(b);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      return this.sourceStream.read(b, off, len);
   }

   public long skip(long n) throws IOException {
      return this.sourceStream.skip(n);
   }

   public int available() throws IOException {
      return this.sourceStream.available();
   }

   public void mark(int readLimit) {
      this.sourceStream.mark(readLimit);
   }

   public void reset() throws IOException {
      this.sourceStream.reset();
   }

   public boolean markSupported() {
      return this.sourceStream.markSupported();
   }

   public void close() throws IOException {
      this.sourceStream.close();
   }

   static class DeflateStream extends InflaterInputStream {
      private boolean closed = false;

      public DeflateStream(InputStream in, Inflater inflater) {
         super(in, inflater);
      }

      public void close() throws IOException {
         if (!this.closed) {
            this.closed = true;
            this.inf.end();
            super.close();
         }
      }
   }
}
