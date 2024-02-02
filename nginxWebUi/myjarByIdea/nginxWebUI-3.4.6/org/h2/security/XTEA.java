package org.h2.security;

import org.h2.message.DbException;
import org.h2.util.Bits;

public class XTEA implements BlockCipher {
   private static final int DELTA = -1640531527;
   private int k0;
   private int k1;
   private int k2;
   private int k3;
   private int k4;
   private int k5;
   private int k6;
   private int k7;
   private int k8;
   private int k9;
   private int k10;
   private int k11;
   private int k12;
   private int k13;
   private int k14;
   private int k15;
   private int k16;
   private int k17;
   private int k18;
   private int k19;
   private int k20;
   private int k21;
   private int k22;
   private int k23;
   private int k24;
   private int k25;
   private int k26;
   private int k27;
   private int k28;
   private int k29;
   private int k30;
   private int k31;

   public void setKey(byte[] var1) {
      int[] var2 = new int[4];

      for(int var3 = 0; var3 < 16; var3 += 4) {
         var2[var3 / 4] = Bits.readInt(var1, var3);
      }

      int[] var6 = new int[32];
      int var4 = 0;

      for(int var5 = 0; var4 < 32; var6[var4++] = var5 + var2[var5 >>> 11 & 3]) {
         var6[var4++] = var5 + var2[var5 & 3];
         var5 -= 1640531527;
      }

      this.k0 = var6[0];
      this.k1 = var6[1];
      this.k2 = var6[2];
      this.k3 = var6[3];
      this.k4 = var6[4];
      this.k5 = var6[5];
      this.k6 = var6[6];
      this.k7 = var6[7];
      this.k8 = var6[8];
      this.k9 = var6[9];
      this.k10 = var6[10];
      this.k11 = var6[11];
      this.k12 = var6[12];
      this.k13 = var6[13];
      this.k14 = var6[14];
      this.k15 = var6[15];
      this.k16 = var6[16];
      this.k17 = var6[17];
      this.k18 = var6[18];
      this.k19 = var6[19];
      this.k20 = var6[20];
      this.k21 = var6[21];
      this.k22 = var6[22];
      this.k23 = var6[23];
      this.k24 = var6[24];
      this.k25 = var6[25];
      this.k26 = var6[26];
      this.k27 = var6[27];
      this.k28 = var6[28];
      this.k29 = var6[29];
      this.k30 = var6[30];
      this.k31 = var6[31];
   }

   public void encrypt(byte[] var1, int var2, int var3) {
      if (var3 % 16 != 0) {
         throw DbException.getInternalError("unaligned len " + var3);
      } else {
         for(int var4 = var2; var4 < var2 + var3; var4 += 8) {
            this.encryptBlock(var1, var1, var4);
         }

      }
   }

   public void decrypt(byte[] var1, int var2, int var3) {
      if (var3 % 16 != 0) {
         throw DbException.getInternalError("unaligned len " + var3);
      } else {
         for(int var4 = var2; var4 < var2 + var3; var4 += 8) {
            this.decryptBlock(var1, var1, var4);
         }

      }
   }

   private void encryptBlock(byte[] var1, byte[] var2, int var3) {
      int var4 = Bits.readInt(var1, var3);
      int var5 = Bits.readInt(var1, var3 + 4);
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k0;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k1;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k2;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k3;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k4;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k5;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k6;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k7;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k8;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k9;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k10;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k11;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k12;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k13;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k14;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k15;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k16;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k17;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k18;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k19;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k20;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k21;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k22;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k23;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k24;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k25;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k26;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k27;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k28;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k29;
      var4 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k30;
      var5 += (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k31;
      Bits.writeInt(var2, var3, var4);
      Bits.writeInt(var2, var3 + 4, var5);
   }

   private void decryptBlock(byte[] var1, byte[] var2, int var3) {
      int var4 = Bits.readInt(var1, var3);
      int var5 = Bits.readInt(var1, var3 + 4);
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k31;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k30;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k29;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k28;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k27;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k26;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k25;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k24;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k23;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k22;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k21;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k20;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k19;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k18;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k17;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k16;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k15;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k14;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k13;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k12;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k11;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k10;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k9;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k8;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k7;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k6;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k5;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k4;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k3;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k2;
      var5 -= (var4 >>> 5 ^ var4 << 4) + var4 ^ this.k1;
      var4 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this.k0;
      Bits.writeInt(var2, var3, var4);
      Bits.writeInt(var2, var3 + 4, var5);
   }

   public int getKeyLength() {
      return 16;
   }
}
