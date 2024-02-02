package com.sun.mail.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class QPEncoderStream extends FilterOutputStream {
   private int count;
   private int bytesPerLine;
   private boolean gotSpace;
   private boolean gotCR;
   private static final char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   public QPEncoderStream(OutputStream out, int bytesPerLine) {
      super(out);
      this.count = 0;
      this.gotSpace = false;
      this.gotCR = false;
      this.bytesPerLine = bytesPerLine - 1;
   }

   public QPEncoderStream(OutputStream out) {
      this(out, 76);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      for(int i = 0; i < len; ++i) {
         this.write(b[off + i]);
      }

   }

   public void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   public void write(int c) throws IOException {
      c &= 255;
      if (this.gotSpace) {
         if (c != 13 && c != 10) {
            this.output(32, false);
         } else {
            this.output(32, true);
         }

         this.gotSpace = false;
      }

      if (c == 13) {
         this.gotCR = true;
         this.outputCRLF();
      } else {
         if (c == 10) {
            if (!this.gotCR) {
               this.outputCRLF();
            }
         } else if (c == 32) {
            this.gotSpace = true;
         } else if (c >= 32 && c < 127 && c != 61) {
            this.output(c, false);
         } else {
            this.output(c, true);
         }

         this.gotCR = false;
      }

   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public void close() throws IOException {
      if (this.gotSpace) {
         this.output(32, true);
         this.gotSpace = false;
      }

      this.out.close();
   }

   private void outputCRLF() throws IOException {
      this.out.write(13);
      this.out.write(10);
      this.count = 0;
   }

   protected void output(int c, boolean encode) throws IOException {
      if (encode) {
         if ((this.count += 3) > this.bytesPerLine) {
            this.out.write(61);
            this.out.write(13);
            this.out.write(10);
            this.count = 3;
         }

         this.out.write(61);
         this.out.write(hex[c >> 4]);
         this.out.write(hex[c & 15]);
      } else {
         if (++this.count > this.bytesPerLine) {
            this.out.write(61);
            this.out.write(13);
            this.out.write(10);
            this.count = 1;
         }

         this.out.write(c);
      }

   }
}
