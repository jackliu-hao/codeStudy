package com.sun.mail.imap;

import java.io.IOException;
import java.io.OutputStream;

class LengthCounter extends OutputStream {
   private int size = 0;
   private byte[] buf = new byte[8192];
   private int maxsize;

   public LengthCounter(int maxsize) {
      this.maxsize = maxsize;
   }

   public void write(int b) {
      int newsize = this.size + 1;
      if (this.buf != null) {
         if (newsize > this.maxsize && this.maxsize >= 0) {
            this.buf = null;
         } else if (newsize > this.buf.length) {
            byte[] newbuf = new byte[Math.max(this.buf.length << 1, newsize)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.size);
            this.buf = newbuf;
            this.buf[this.size] = (byte)b;
         } else {
            this.buf[this.size] = (byte)b;
         }
      }

      this.size = newsize;
   }

   public void write(byte[] b, int off, int len) {
      if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
         if (len != 0) {
            int newsize = this.size + len;
            if (this.buf != null) {
               if (newsize > this.maxsize && this.maxsize >= 0) {
                  this.buf = null;
               } else if (newsize > this.buf.length) {
                  byte[] newbuf = new byte[Math.max(this.buf.length << 1, newsize)];
                  System.arraycopy(this.buf, 0, newbuf, 0, this.size);
                  this.buf = newbuf;
                  System.arraycopy(b, off, this.buf, this.size, len);
               } else {
                  System.arraycopy(b, off, this.buf, this.size, len);
               }
            }

            this.size = newsize;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   public int getSize() {
      return this.size;
   }

   public byte[] getBytes() {
      return this.buf;
   }
}
