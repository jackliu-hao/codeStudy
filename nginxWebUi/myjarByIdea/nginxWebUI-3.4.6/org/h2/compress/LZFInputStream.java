package org.h2.compress;

import java.io.IOException;
import java.io.InputStream;
import org.h2.mvstore.DataUtils;
import org.h2.util.Utils;

public class LZFInputStream extends InputStream {
   private final InputStream in;
   private CompressLZF decompress = new CompressLZF();
   private int pos;
   private int bufferLength;
   private byte[] inBuffer;
   private byte[] buffer;

   public LZFInputStream(InputStream var1) throws IOException {
      this.in = var1;
      if (this.readInt() != 1211255123) {
         throw new IOException("Not an LZFInputStream");
      }
   }

   private static byte[] ensureSize(byte[] var0, int var1) {
      return var0 != null && var0.length >= var1 ? var0 : Utils.newBytes(var1);
   }

   private void fillBuffer() throws IOException {
      if (this.buffer == null || this.pos >= this.bufferLength) {
         int var1 = this.readInt();
         if (this.decompress == null) {
            this.bufferLength = 0;
         } else if (var1 < 0) {
            var1 = -var1;
            this.buffer = ensureSize(this.buffer, var1);
            this.readFully(this.buffer, var1);
            this.bufferLength = var1;
         } else {
            this.inBuffer = ensureSize(this.inBuffer, var1);
            int var2 = this.readInt();
            this.readFully(this.inBuffer, var1);
            this.buffer = ensureSize(this.buffer, var2);

            try {
               this.decompress.expand(this.inBuffer, 0, var1, this.buffer, 0, var2);
            } catch (ArrayIndexOutOfBoundsException var4) {
               throw DataUtils.convertToIOException(var4);
            }

            this.bufferLength = var2;
         }

         this.pos = 0;
      }
   }

   private void readFully(byte[] var1, int var2) throws IOException {
      int var4;
      for(int var3 = 0; var2 > 0; var3 += var4) {
         var4 = this.in.read(var1, var3, var2);
         var2 -= var4;
      }

   }

   private int readInt() throws IOException {
      int var1 = this.in.read();
      if (var1 < 0) {
         this.decompress = null;
         return 0;
      } else {
         var1 = (var1 << 24) + (this.in.read() << 16) + (this.in.read() << 8) + this.in.read();
         return var1;
      }
   }

   public int read() throws IOException {
      this.fillBuffer();
      return this.pos >= this.bufferLength ? -1 : this.buffer[this.pos++] & 255;
   }

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var3 == 0) {
         return 0;
      } else {
         int var4;
         int var5;
         for(var4 = 0; var3 > 0; var3 -= var5) {
            var5 = this.readBlock(var1, var2, var3);
            if (var5 < 0) {
               break;
            }

            var4 += var5;
            var2 += var5;
         }

         return var4 == 0 ? -1 : var4;
      }
   }

   private int readBlock(byte[] var1, int var2, int var3) throws IOException {
      this.fillBuffer();
      if (this.pos >= this.bufferLength) {
         return -1;
      } else {
         int var4 = Math.min(var3, this.bufferLength - this.pos);
         var4 = Math.min(var4, var1.length - var2);
         System.arraycopy(this.buffer, this.pos, var1, var2, var4);
         this.pos += var4;
         return var4;
      }
   }

   public void close() throws IOException {
      this.in.close();
   }
}
