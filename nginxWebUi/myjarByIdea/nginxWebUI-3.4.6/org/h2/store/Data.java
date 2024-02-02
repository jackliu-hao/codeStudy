package org.h2.store;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import org.h2.util.Bits;
import org.h2.util.MathUtils;
import org.h2.util.Utils;

public class Data {
   private byte[] data;
   private int pos;

   private Data(byte[] var1) {
      this.data = var1;
   }

   public void writeInt(int var1) {
      Bits.writeInt(this.data, this.pos, var1);
      this.pos += 4;
   }

   public int readInt() {
      int var1 = Bits.readInt(this.data, this.pos);
      this.pos += 4;
      return var1;
   }

   private void writeStringWithoutLength(char[] var1, int var2) {
      int var3 = this.pos;
      byte[] var4 = this.data;

      for(int var5 = 0; var5 < var2; ++var5) {
         char var6 = var1[var5];
         if (var6 < 128) {
            var4[var3++] = (byte)var6;
         } else if (var6 >= 2048) {
            var4[var3++] = (byte)(224 | var6 >> 12);
            var4[var3++] = (byte)(var6 >> 6 & 63);
            var4[var3++] = (byte)(var6 & 63);
         } else {
            var4[var3++] = (byte)(192 | var6 >> 6);
            var4[var3++] = (byte)(var6 & 63);
         }
      }

      this.pos = var3;
   }

   public static Data create(int var0) {
      return new Data(new byte[var0]);
   }

   public int length() {
      return this.pos;
   }

   public byte[] getBytes() {
      return this.data;
   }

   public void reset() {
      this.pos = 0;
   }

   public void write(byte[] var1, int var2, int var3) {
      System.arraycopy(var1, var2, this.data, this.pos, var3);
      this.pos += var3;
   }

   public void read(byte[] var1, int var2, int var3) {
      System.arraycopy(this.data, this.pos, var1, var2, var3);
      this.pos += var3;
   }

   public void setPos(int var1) {
      this.pos = var1;
   }

   public byte readByte() {
      return this.data[this.pos++];
   }

   public void checkCapacity(int var1) {
      if (this.pos + var1 >= this.data.length) {
         this.expand(var1);
      }

   }

   private void expand(int var1) {
      this.data = Utils.copyBytes(this.data, (this.data.length + var1) * 2);
   }

   public void fillAligned() {
      int var1 = MathUtils.roundUpInt(this.pos + 2, 16);
      this.pos = var1;
      if (this.data.length < var1) {
         this.checkCapacity(var1 - this.data.length);
      }

   }

   public static void copyString(Reader var0, OutputStream var1) throws IOException {
      char[] var2 = new char[4096];
      Data var3 = new Data(new byte[12288]);

      while(true) {
         int var4 = var0.read(var2);
         if (var4 < 0) {
            return;
         }

         var3.writeStringWithoutLength(var2, var4);
         var1.write(var3.data, 0, var3.pos);
         var3.reset();
      }
   }
}
