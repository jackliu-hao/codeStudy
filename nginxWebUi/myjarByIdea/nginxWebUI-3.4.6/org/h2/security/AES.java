package org.h2.security;

import org.h2.util.Bits;

public class AES implements BlockCipher {
   private static final int[] RCON = new int[10];
   private static final int[] FS = new int[256];
   private static final int[] FT0 = new int[256];
   private static final int[] FT1 = new int[256];
   private static final int[] FT2 = new int[256];
   private static final int[] FT3 = new int[256];
   private static final int[] RS = new int[256];
   private static final int[] RT0 = new int[256];
   private static final int[] RT1 = new int[256];
   private static final int[] RT2 = new int[256];
   private static final int[] RT3 = new int[256];
   private final int[] encKey = new int[44];
   private final int[] decKey = new int[44];

   private static int rot8(int var0) {
      return var0 >>> 8 | var0 << 24;
   }

   private static int xtime(int var0) {
      return (var0 << 1 ^ ((var0 & 128) != 0 ? 27 : 0)) & 255;
   }

   private static int mul(int[] var0, int[] var1, int var2, int var3) {
      return var2 != 0 && var3 != 0 ? var0[(var1[var2] + var1[var3]) % 255] : 0;
   }

   private static int getDec(int var0) {
      return RT0[FS[var0 >> 24 & 255]] ^ RT1[FS[var0 >> 16 & 255]] ^ RT2[FS[var0 >> 8 & 255]] ^ RT3[FS[var0 & 255]];
   }

   public void setKey(byte[] var1) {
      int var2 = 0;

      int var3;
      for(var3 = 0; var2 < 4; ++var2) {
         this.encKey[var2] = this.decKey[var2] = (var1[var3++] & 255) << 24 | (var1[var3++] & 255) << 16 | (var1[var3++] & 255) << 8 | var1[var3++] & 255;
      }

      var2 = 0;

      for(var3 = 0; var3 < 10; var2 += 4) {
         this.encKey[var2 + 4] = this.encKey[var2] ^ RCON[var3] ^ FS[this.encKey[var2 + 3] >> 16 & 255] << 24 ^ FS[this.encKey[var2 + 3] >> 8 & 255] << 16 ^ FS[this.encKey[var2 + 3] & 255] << 8 ^ FS[this.encKey[var2 + 3] >> 24 & 255];
         this.encKey[var2 + 5] = this.encKey[var2 + 1] ^ this.encKey[var2 + 4];
         this.encKey[var2 + 6] = this.encKey[var2 + 2] ^ this.encKey[var2 + 5];
         this.encKey[var2 + 7] = this.encKey[var2 + 3] ^ this.encKey[var2 + 6];
         ++var3;
      }

      byte var5 = 0;
      var3 = var5 + 1;
      this.decKey[var5] = this.encKey[var2++];
      this.decKey[var3++] = this.encKey[var2++];
      this.decKey[var3++] = this.encKey[var2++];
      this.decKey[var3++] = this.encKey[var2++];

      for(int var4 = 1; var4 < 10; ++var4) {
         var2 -= 8;
         this.decKey[var3++] = getDec(this.encKey[var2++]);
         this.decKey[var3++] = getDec(this.encKey[var2++]);
         this.decKey[var3++] = getDec(this.encKey[var2++]);
         this.decKey[var3++] = getDec(this.encKey[var2++]);
      }

      var2 -= 8;
      this.decKey[var3++] = this.encKey[var2++];
      this.decKey[var3++] = this.encKey[var2++];
      this.decKey[var3++] = this.encKey[var2++];
      this.decKey[var3] = this.encKey[var2];
   }

   public void encrypt(byte[] var1, int var2, int var3) {
      for(int var4 = var2; var4 < var2 + var3; var4 += 16) {
         this.encryptBlock(var1, var1, var4);
      }

   }

   public void decrypt(byte[] var1, int var2, int var3) {
      for(int var4 = var2; var4 < var2 + var3; var4 += 16) {
         this.decryptBlock(var1, var1, var4);
      }

   }

   private void encryptBlock(byte[] var1, byte[] var2, int var3) {
      int[] var4 = this.encKey;
      int var5 = Bits.readInt(var1, var3) ^ var4[0];
      int var6 = Bits.readInt(var1, var3 + 4) ^ var4[1];
      int var7 = Bits.readInt(var1, var3 + 8) ^ var4[2];
      int var8 = Bits.readInt(var1, var3 + 12) ^ var4[3];
      int var9 = FT0[var5 >> 24 & 255] ^ FT1[var6 >> 16 & 255] ^ FT2[var7 >> 8 & 255] ^ FT3[var8 & 255] ^ var4[4];
      int var10 = FT0[var6 >> 24 & 255] ^ FT1[var7 >> 16 & 255] ^ FT2[var8 >> 8 & 255] ^ FT3[var5 & 255] ^ var4[5];
      int var11 = FT0[var7 >> 24 & 255] ^ FT1[var8 >> 16 & 255] ^ FT2[var5 >> 8 & 255] ^ FT3[var6 & 255] ^ var4[6];
      int var12 = FT0[var8 >> 24 & 255] ^ FT1[var5 >> 16 & 255] ^ FT2[var6 >> 8 & 255] ^ FT3[var7 & 255] ^ var4[7];
      var5 = FT0[var9 >> 24 & 255] ^ FT1[var10 >> 16 & 255] ^ FT2[var11 >> 8 & 255] ^ FT3[var12 & 255] ^ var4[8];
      var6 = FT0[var10 >> 24 & 255] ^ FT1[var11 >> 16 & 255] ^ FT2[var12 >> 8 & 255] ^ FT3[var9 & 255] ^ var4[9];
      var7 = FT0[var11 >> 24 & 255] ^ FT1[var12 >> 16 & 255] ^ FT2[var9 >> 8 & 255] ^ FT3[var10 & 255] ^ var4[10];
      var8 = FT0[var12 >> 24 & 255] ^ FT1[var9 >> 16 & 255] ^ FT2[var10 >> 8 & 255] ^ FT3[var11 & 255] ^ var4[11];
      var9 = FT0[var5 >> 24 & 255] ^ FT1[var6 >> 16 & 255] ^ FT2[var7 >> 8 & 255] ^ FT3[var8 & 255] ^ var4[12];
      var10 = FT0[var6 >> 24 & 255] ^ FT1[var7 >> 16 & 255] ^ FT2[var8 >> 8 & 255] ^ FT3[var5 & 255] ^ var4[13];
      var11 = FT0[var7 >> 24 & 255] ^ FT1[var8 >> 16 & 255] ^ FT2[var5 >> 8 & 255] ^ FT3[var6 & 255] ^ var4[14];
      var12 = FT0[var8 >> 24 & 255] ^ FT1[var5 >> 16 & 255] ^ FT2[var6 >> 8 & 255] ^ FT3[var7 & 255] ^ var4[15];
      var5 = FT0[var9 >> 24 & 255] ^ FT1[var10 >> 16 & 255] ^ FT2[var11 >> 8 & 255] ^ FT3[var12 & 255] ^ var4[16];
      var6 = FT0[var10 >> 24 & 255] ^ FT1[var11 >> 16 & 255] ^ FT2[var12 >> 8 & 255] ^ FT3[var9 & 255] ^ var4[17];
      var7 = FT0[var11 >> 24 & 255] ^ FT1[var12 >> 16 & 255] ^ FT2[var9 >> 8 & 255] ^ FT3[var10 & 255] ^ var4[18];
      var8 = FT0[var12 >> 24 & 255] ^ FT1[var9 >> 16 & 255] ^ FT2[var10 >> 8 & 255] ^ FT3[var11 & 255] ^ var4[19];
      var9 = FT0[var5 >> 24 & 255] ^ FT1[var6 >> 16 & 255] ^ FT2[var7 >> 8 & 255] ^ FT3[var8 & 255] ^ var4[20];
      var10 = FT0[var6 >> 24 & 255] ^ FT1[var7 >> 16 & 255] ^ FT2[var8 >> 8 & 255] ^ FT3[var5 & 255] ^ var4[21];
      var11 = FT0[var7 >> 24 & 255] ^ FT1[var8 >> 16 & 255] ^ FT2[var5 >> 8 & 255] ^ FT3[var6 & 255] ^ var4[22];
      var12 = FT0[var8 >> 24 & 255] ^ FT1[var5 >> 16 & 255] ^ FT2[var6 >> 8 & 255] ^ FT3[var7 & 255] ^ var4[23];
      var5 = FT0[var9 >> 24 & 255] ^ FT1[var10 >> 16 & 255] ^ FT2[var11 >> 8 & 255] ^ FT3[var12 & 255] ^ var4[24];
      var6 = FT0[var10 >> 24 & 255] ^ FT1[var11 >> 16 & 255] ^ FT2[var12 >> 8 & 255] ^ FT3[var9 & 255] ^ var4[25];
      var7 = FT0[var11 >> 24 & 255] ^ FT1[var12 >> 16 & 255] ^ FT2[var9 >> 8 & 255] ^ FT3[var10 & 255] ^ var4[26];
      var8 = FT0[var12 >> 24 & 255] ^ FT1[var9 >> 16 & 255] ^ FT2[var10 >> 8 & 255] ^ FT3[var11 & 255] ^ var4[27];
      var9 = FT0[var5 >> 24 & 255] ^ FT1[var6 >> 16 & 255] ^ FT2[var7 >> 8 & 255] ^ FT3[var8 & 255] ^ var4[28];
      var10 = FT0[var6 >> 24 & 255] ^ FT1[var7 >> 16 & 255] ^ FT2[var8 >> 8 & 255] ^ FT3[var5 & 255] ^ var4[29];
      var11 = FT0[var7 >> 24 & 255] ^ FT1[var8 >> 16 & 255] ^ FT2[var5 >> 8 & 255] ^ FT3[var6 & 255] ^ var4[30];
      var12 = FT0[var8 >> 24 & 255] ^ FT1[var5 >> 16 & 255] ^ FT2[var6 >> 8 & 255] ^ FT3[var7 & 255] ^ var4[31];
      var5 = FT0[var9 >> 24 & 255] ^ FT1[var10 >> 16 & 255] ^ FT2[var11 >> 8 & 255] ^ FT3[var12 & 255] ^ var4[32];
      var6 = FT0[var10 >> 24 & 255] ^ FT1[var11 >> 16 & 255] ^ FT2[var12 >> 8 & 255] ^ FT3[var9 & 255] ^ var4[33];
      var7 = FT0[var11 >> 24 & 255] ^ FT1[var12 >> 16 & 255] ^ FT2[var9 >> 8 & 255] ^ FT3[var10 & 255] ^ var4[34];
      var8 = FT0[var12 >> 24 & 255] ^ FT1[var9 >> 16 & 255] ^ FT2[var10 >> 8 & 255] ^ FT3[var11 & 255] ^ var4[35];
      var9 = FT0[var5 >> 24 & 255] ^ FT1[var6 >> 16 & 255] ^ FT2[var7 >> 8 & 255] ^ FT3[var8 & 255] ^ var4[36];
      var10 = FT0[var6 >> 24 & 255] ^ FT1[var7 >> 16 & 255] ^ FT2[var8 >> 8 & 255] ^ FT3[var5 & 255] ^ var4[37];
      var11 = FT0[var7 >> 24 & 255] ^ FT1[var8 >> 16 & 255] ^ FT2[var5 >> 8 & 255] ^ FT3[var6 & 255] ^ var4[38];
      var12 = FT0[var8 >> 24 & 255] ^ FT1[var5 >> 16 & 255] ^ FT2[var6 >> 8 & 255] ^ FT3[var7 & 255] ^ var4[39];
      var5 = (FS[var9 >> 24 & 255] << 24 | FS[var10 >> 16 & 255] << 16 | FS[var11 >> 8 & 255] << 8 | FS[var12 & 255]) ^ var4[40];
      var6 = (FS[var10 >> 24 & 255] << 24 | FS[var11 >> 16 & 255] << 16 | FS[var12 >> 8 & 255] << 8 | FS[var9 & 255]) ^ var4[41];
      var7 = (FS[var11 >> 24 & 255] << 24 | FS[var12 >> 16 & 255] << 16 | FS[var9 >> 8 & 255] << 8 | FS[var10 & 255]) ^ var4[42];
      var8 = (FS[var12 >> 24 & 255] << 24 | FS[var9 >> 16 & 255] << 16 | FS[var10 >> 8 & 255] << 8 | FS[var11 & 255]) ^ var4[43];
      Bits.writeInt(var2, var3, var5);
      Bits.writeInt(var2, var3 + 4, var6);
      Bits.writeInt(var2, var3 + 8, var7);
      Bits.writeInt(var2, var3 + 12, var8);
   }

   private void decryptBlock(byte[] var1, byte[] var2, int var3) {
      int[] var4 = this.decKey;
      int var5 = Bits.readInt(var1, var3) ^ var4[0];
      int var6 = Bits.readInt(var1, var3 + 4) ^ var4[1];
      int var7 = Bits.readInt(var1, var3 + 8) ^ var4[2];
      int var8 = Bits.readInt(var1, var3 + 12) ^ var4[3];
      int var9 = RT0[var5 >> 24 & 255] ^ RT1[var8 >> 16 & 255] ^ RT2[var7 >> 8 & 255] ^ RT3[var6 & 255] ^ var4[4];
      int var10 = RT0[var6 >> 24 & 255] ^ RT1[var5 >> 16 & 255] ^ RT2[var8 >> 8 & 255] ^ RT3[var7 & 255] ^ var4[5];
      int var11 = RT0[var7 >> 24 & 255] ^ RT1[var6 >> 16 & 255] ^ RT2[var5 >> 8 & 255] ^ RT3[var8 & 255] ^ var4[6];
      int var12 = RT0[var8 >> 24 & 255] ^ RT1[var7 >> 16 & 255] ^ RT2[var6 >> 8 & 255] ^ RT3[var5 & 255] ^ var4[7];
      var5 = RT0[var9 >> 24 & 255] ^ RT1[var12 >> 16 & 255] ^ RT2[var11 >> 8 & 255] ^ RT3[var10 & 255] ^ var4[8];
      var6 = RT0[var10 >> 24 & 255] ^ RT1[var9 >> 16 & 255] ^ RT2[var12 >> 8 & 255] ^ RT3[var11 & 255] ^ var4[9];
      var7 = RT0[var11 >> 24 & 255] ^ RT1[var10 >> 16 & 255] ^ RT2[var9 >> 8 & 255] ^ RT3[var12 & 255] ^ var4[10];
      var8 = RT0[var12 >> 24 & 255] ^ RT1[var11 >> 16 & 255] ^ RT2[var10 >> 8 & 255] ^ RT3[var9 & 255] ^ var4[11];
      var9 = RT0[var5 >> 24 & 255] ^ RT1[var8 >> 16 & 255] ^ RT2[var7 >> 8 & 255] ^ RT3[var6 & 255] ^ var4[12];
      var10 = RT0[var6 >> 24 & 255] ^ RT1[var5 >> 16 & 255] ^ RT2[var8 >> 8 & 255] ^ RT3[var7 & 255] ^ var4[13];
      var11 = RT0[var7 >> 24 & 255] ^ RT1[var6 >> 16 & 255] ^ RT2[var5 >> 8 & 255] ^ RT3[var8 & 255] ^ var4[14];
      var12 = RT0[var8 >> 24 & 255] ^ RT1[var7 >> 16 & 255] ^ RT2[var6 >> 8 & 255] ^ RT3[var5 & 255] ^ var4[15];
      var5 = RT0[var9 >> 24 & 255] ^ RT1[var12 >> 16 & 255] ^ RT2[var11 >> 8 & 255] ^ RT3[var10 & 255] ^ var4[16];
      var6 = RT0[var10 >> 24 & 255] ^ RT1[var9 >> 16 & 255] ^ RT2[var12 >> 8 & 255] ^ RT3[var11 & 255] ^ var4[17];
      var7 = RT0[var11 >> 24 & 255] ^ RT1[var10 >> 16 & 255] ^ RT2[var9 >> 8 & 255] ^ RT3[var12 & 255] ^ var4[18];
      var8 = RT0[var12 >> 24 & 255] ^ RT1[var11 >> 16 & 255] ^ RT2[var10 >> 8 & 255] ^ RT3[var9 & 255] ^ var4[19];
      var9 = RT0[var5 >> 24 & 255] ^ RT1[var8 >> 16 & 255] ^ RT2[var7 >> 8 & 255] ^ RT3[var6 & 255] ^ var4[20];
      var10 = RT0[var6 >> 24 & 255] ^ RT1[var5 >> 16 & 255] ^ RT2[var8 >> 8 & 255] ^ RT3[var7 & 255] ^ var4[21];
      var11 = RT0[var7 >> 24 & 255] ^ RT1[var6 >> 16 & 255] ^ RT2[var5 >> 8 & 255] ^ RT3[var8 & 255] ^ var4[22];
      var12 = RT0[var8 >> 24 & 255] ^ RT1[var7 >> 16 & 255] ^ RT2[var6 >> 8 & 255] ^ RT3[var5 & 255] ^ var4[23];
      var5 = RT0[var9 >> 24 & 255] ^ RT1[var12 >> 16 & 255] ^ RT2[var11 >> 8 & 255] ^ RT3[var10 & 255] ^ var4[24];
      var6 = RT0[var10 >> 24 & 255] ^ RT1[var9 >> 16 & 255] ^ RT2[var12 >> 8 & 255] ^ RT3[var11 & 255] ^ var4[25];
      var7 = RT0[var11 >> 24 & 255] ^ RT1[var10 >> 16 & 255] ^ RT2[var9 >> 8 & 255] ^ RT3[var12 & 255] ^ var4[26];
      var8 = RT0[var12 >> 24 & 255] ^ RT1[var11 >> 16 & 255] ^ RT2[var10 >> 8 & 255] ^ RT3[var9 & 255] ^ var4[27];
      var9 = RT0[var5 >> 24 & 255] ^ RT1[var8 >> 16 & 255] ^ RT2[var7 >> 8 & 255] ^ RT3[var6 & 255] ^ var4[28];
      var10 = RT0[var6 >> 24 & 255] ^ RT1[var5 >> 16 & 255] ^ RT2[var8 >> 8 & 255] ^ RT3[var7 & 255] ^ var4[29];
      var11 = RT0[var7 >> 24 & 255] ^ RT1[var6 >> 16 & 255] ^ RT2[var5 >> 8 & 255] ^ RT3[var8 & 255] ^ var4[30];
      var12 = RT0[var8 >> 24 & 255] ^ RT1[var7 >> 16 & 255] ^ RT2[var6 >> 8 & 255] ^ RT3[var5 & 255] ^ var4[31];
      var5 = RT0[var9 >> 24 & 255] ^ RT1[var12 >> 16 & 255] ^ RT2[var11 >> 8 & 255] ^ RT3[var10 & 255] ^ var4[32];
      var6 = RT0[var10 >> 24 & 255] ^ RT1[var9 >> 16 & 255] ^ RT2[var12 >> 8 & 255] ^ RT3[var11 & 255] ^ var4[33];
      var7 = RT0[var11 >> 24 & 255] ^ RT1[var10 >> 16 & 255] ^ RT2[var9 >> 8 & 255] ^ RT3[var12 & 255] ^ var4[34];
      var8 = RT0[var12 >> 24 & 255] ^ RT1[var11 >> 16 & 255] ^ RT2[var10 >> 8 & 255] ^ RT3[var9 & 255] ^ var4[35];
      var9 = RT0[var5 >> 24 & 255] ^ RT1[var8 >> 16 & 255] ^ RT2[var7 >> 8 & 255] ^ RT3[var6 & 255] ^ var4[36];
      var10 = RT0[var6 >> 24 & 255] ^ RT1[var5 >> 16 & 255] ^ RT2[var8 >> 8 & 255] ^ RT3[var7 & 255] ^ var4[37];
      var11 = RT0[var7 >> 24 & 255] ^ RT1[var6 >> 16 & 255] ^ RT2[var5 >> 8 & 255] ^ RT3[var8 & 255] ^ var4[38];
      var12 = RT0[var8 >> 24 & 255] ^ RT1[var7 >> 16 & 255] ^ RT2[var6 >> 8 & 255] ^ RT3[var5 & 255] ^ var4[39];
      var5 = (RS[var9 >> 24 & 255] << 24 | RS[var12 >> 16 & 255] << 16 | RS[var11 >> 8 & 255] << 8 | RS[var10 & 255]) ^ var4[40];
      var6 = (RS[var10 >> 24 & 255] << 24 | RS[var9 >> 16 & 255] << 16 | RS[var12 >> 8 & 255] << 8 | RS[var11 & 255]) ^ var4[41];
      var7 = (RS[var11 >> 24 & 255] << 24 | RS[var10 >> 16 & 255] << 16 | RS[var9 >> 8 & 255] << 8 | RS[var12 & 255]) ^ var4[42];
      var8 = (RS[var12 >> 24 & 255] << 24 | RS[var11 >> 16 & 255] << 16 | RS[var10 >> 8 & 255] << 8 | RS[var9 & 255]) ^ var4[43];
      Bits.writeInt(var2, var3, var5);
      Bits.writeInt(var2, var3 + 4, var6);
      Bits.writeInt(var2, var3 + 8, var7);
      Bits.writeInt(var2, var3 + 12, var8);
   }

   public int getKeyLength() {
      return 16;
   }

   static {
      int[] var0 = new int[256];
      int[] var1 = new int[256];
      int var2 = 0;

      int var3;
      for(var3 = 1; var2 < 256; var3 ^= xtime(var3)) {
         var0[var2] = var3;
         var1[var3] = var2++;
      }

      var2 = 0;

      for(var3 = 1; var2 < 10; var3 = xtime(var3)) {
         RCON[var2] = var3 << 24;
         ++var2;
      }

      FS[0] = 99;
      RS[99] = 0;

      int var4;
      for(var2 = 1; var2 < 256; ++var2) {
         var3 = var0[255 - var1[var2]];
         var4 = (var3 << 1 | var3 >> 7) & 255;
         var3 ^= var4;
         var4 = (var4 << 1 | var4 >> 7) & 255;
         var3 ^= var4;
         var4 = (var4 << 1 | var4 >> 7) & 255;
         var3 ^= var4;
         var4 = (var4 << 1 | var4 >> 7) & 255;
         var3 ^= var4 ^ 99;
         FS[var2] = var3 & 255;
         RS[var3] = var2 & 255;
      }

      for(var2 = 0; var2 < 256; ++var2) {
         var3 = FS[var2];
         var4 = xtime(var3);
         FT0[var2] = var3 ^ var4 ^ var3 << 8 ^ var3 << 16 ^ var4 << 24;
         FT1[var2] = rot8(FT0[var2]);
         FT2[var2] = rot8(FT1[var2]);
         FT3[var2] = rot8(FT2[var2]);
         var4 = RS[var2];
         RT0[var2] = mul(var0, var1, 11, var4) ^ mul(var0, var1, 13, var4) << 8 ^ mul(var0, var1, 9, var4) << 16 ^ mul(var0, var1, 14, var4) << 24;
         RT1[var2] = rot8(RT0[var2]);
         RT2[var2] = rot8(RT1[var2]);
         RT3[var2] = rot8(RT2[var2]);
      }

   }
}
