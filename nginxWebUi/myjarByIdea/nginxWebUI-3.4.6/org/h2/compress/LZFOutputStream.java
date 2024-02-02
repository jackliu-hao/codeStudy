package org.h2.compress;

import java.io.IOException;
import java.io.OutputStream;

public class LZFOutputStream extends OutputStream {
   static final int MAGIC = 1211255123;
   private final OutputStream out;
   private final CompressLZF compress = new CompressLZF();
   private final byte[] buffer;
   private int pos;
   private byte[] outBuffer;

   public LZFOutputStream(OutputStream var1) throws IOException {
      this.out = var1;
      int var2 = 131072;
      this.buffer = new byte[var2];
      this.ensureOutput(var2);
      this.writeInt(1211255123);
   }

   private void ensureOutput(int var1) {
      int var2 = (var1 < 100 ? var1 + 100 : var1) * 2;
      if (this.outBuffer == null || this.outBuffer.length < var2) {
         this.outBuffer = new byte[var2];
      }

   }

   public void write(int var1) throws IOException {
      if (this.pos >= this.buffer.length) {
         this.flush();
      }

      this.buffer[this.pos++] = (byte)var1;
   }

   private void compressAndWrite(byte[] var1, int var2) throws IOException {
      if (var2 > 0) {
         this.ensureOutput(var2);
         int var3 = this.compress.compress(var1, 0, var2, this.outBuffer, 0);
         if (var3 > var2) {
            this.writeInt(-var2);
            this.out.write(var1, 0, var2);
         } else {
            this.writeInt(var3);
            this.writeInt(var2);
            this.out.write(this.outBuffer, 0, var3);
         }
      }

   }

   private void writeInt(int var1) throws IOException {
      this.out.write((byte)(var1 >> 24));
      this.out.write((byte)(var1 >> 16));
      this.out.write((byte)(var1 >> 8));
      this.out.write((byte)var1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      while(var3 > 0) {
         int var4 = Math.min(this.buffer.length - this.pos, var3);
         System.arraycopy(var1, var2, this.buffer, this.pos, var4);
         this.pos += var4;
         if (this.pos >= this.buffer.length) {
            this.flush();
         }

         var2 += var4;
         var3 -= var4;
      }

   }

   public void flush() throws IOException {
      this.compressAndWrite(this.buffer, this.pos);
      this.pos = 0;
   }

   public void close() throws IOException {
      this.flush();
      this.out.close();
   }
}
