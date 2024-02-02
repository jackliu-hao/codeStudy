package com.sun.mail.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BASE64EncoderStream extends FilterOutputStream {
   private byte[] buffer;
   private int bufsize;
   private byte[] outbuf;
   private int count;
   private int bytesPerLine;
   private int lineLimit;
   private boolean noCRLF;
   private static byte[] newline = new byte[]{13, 10};
   private static final char[] pem_array = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

   public BASE64EncoderStream(OutputStream out, int bytesPerLine) {
      super(out);
      this.bufsize = 0;
      this.count = 0;
      this.noCRLF = false;
      this.buffer = new byte[3];
      if (bytesPerLine == Integer.MAX_VALUE || bytesPerLine < 4) {
         this.noCRLF = true;
         bytesPerLine = 76;
      }

      bytesPerLine = bytesPerLine / 4 * 4;
      this.bytesPerLine = bytesPerLine;
      this.lineLimit = bytesPerLine / 4 * 3;
      if (this.noCRLF) {
         this.outbuf = new byte[bytesPerLine];
      } else {
         this.outbuf = new byte[bytesPerLine + 2];
         this.outbuf[bytesPerLine] = 13;
         this.outbuf[bytesPerLine + 1] = 10;
      }

   }

   public BASE64EncoderStream(OutputStream out) {
      this(out, 76);
   }

   public synchronized void write(byte[] b, int off, int len) throws IOException {
      int end = off + len;

      while(this.bufsize != 0 && off < end) {
         this.write(b[off++]);
      }

      int blen = (this.bytesPerLine - this.count) / 4 * 3;
      int outlen;
      if (off + blen <= end) {
         outlen = encodedSize(blen);
         if (!this.noCRLF) {
            this.outbuf[outlen++] = 13;
            this.outbuf[outlen++] = 10;
         }

         this.out.write(encode(b, off, blen, this.outbuf), 0, outlen);
         off += blen;
         this.count = 0;
      }

      while(off + this.lineLimit <= end) {
         this.out.write(encode(b, off, this.lineLimit, this.outbuf));
         off += this.lineLimit;
      }

      if (off + 3 <= end) {
         blen = end - off;
         blen = blen / 3 * 3;
         outlen = encodedSize(blen);
         this.out.write(encode(b, off, blen, this.outbuf), 0, outlen);
         off += blen;
         this.count += outlen;
      }

      while(off < end) {
         this.write(b[off]);
         ++off;
      }

   }

   public void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   public synchronized void write(int c) throws IOException {
      this.buffer[this.bufsize++] = (byte)c;
      if (this.bufsize == 3) {
         this.encode();
         this.bufsize = 0;
      }

   }

   public synchronized void flush() throws IOException {
      if (this.bufsize > 0) {
         this.encode();
         this.bufsize = 0;
      }

      this.out.flush();
   }

   public synchronized void close() throws IOException {
      this.flush();
      if (this.count > 0 && !this.noCRLF) {
         this.out.write(newline);
         this.out.flush();
      }

      this.out.close();
   }

   private void encode() throws IOException {
      int osize = encodedSize(this.bufsize);
      this.out.write(encode(this.buffer, 0, this.bufsize, this.outbuf), 0, osize);
      this.count += osize;
      if (this.count >= this.bytesPerLine) {
         if (!this.noCRLF) {
            this.out.write(newline);
         }

         this.count = 0;
      }

   }

   public static byte[] encode(byte[] inbuf) {
      return inbuf.length == 0 ? inbuf : encode(inbuf, 0, inbuf.length, (byte[])null);
   }

   private static byte[] encode(byte[] inbuf, int off, int size, byte[] outbuf) {
      if (outbuf == null) {
         outbuf = new byte[encodedSize(size)];
      }

      int inpos = off;

      int outpos;
      int val;
      for(outpos = 0; size >= 3; outpos += 4) {
         val = inbuf[inpos++] & 255;
         val <<= 8;
         val |= inbuf[inpos++] & 255;
         val <<= 8;
         val |= inbuf[inpos++] & 255;
         outbuf[outpos + 3] = (byte)pem_array[val & 63];
         val >>= 6;
         outbuf[outpos + 2] = (byte)pem_array[val & 63];
         val >>= 6;
         outbuf[outpos + 1] = (byte)pem_array[val & 63];
         val >>= 6;
         outbuf[outpos + 0] = (byte)pem_array[val & 63];
         size -= 3;
      }

      if (size == 1) {
         val = inbuf[inpos++] & 255;
         val <<= 4;
         outbuf[outpos + 3] = 61;
         outbuf[outpos + 2] = 61;
         outbuf[outpos + 1] = (byte)pem_array[val & 63];
         val >>= 6;
         outbuf[outpos + 0] = (byte)pem_array[val & 63];
      } else if (size == 2) {
         val = inbuf[inpos++] & 255;
         val <<= 8;
         val |= inbuf[inpos++] & 255;
         val <<= 2;
         outbuf[outpos + 3] = 61;
         outbuf[outpos + 2] = (byte)pem_array[val & 63];
         val >>= 6;
         outbuf[outpos + 1] = (byte)pem_array[val & 63];
         val >>= 6;
         outbuf[outpos + 0] = (byte)pem_array[val & 63];
      }

      return outbuf;
   }

   private static int encodedSize(int size) {
      return (size + 2) / 3 * 4;
   }
}
