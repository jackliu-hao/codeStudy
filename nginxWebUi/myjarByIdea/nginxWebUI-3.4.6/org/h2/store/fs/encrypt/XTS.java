package org.h2.store.fs.encrypt;

import org.h2.security.BlockCipher;

class XTS {
   private static final int GF_128_FEEDBACK = 135;
   private static final int CIPHER_BLOCK_SIZE = 16;
   private final BlockCipher cipher;

   XTS(BlockCipher var1) {
      this.cipher = var1;
   }

   void encrypt(long var1, int var3, byte[] var4, int var5) {
      byte[] var6 = this.initTweak(var1);

      int var7;
      for(var7 = 0; var7 + 16 <= var3; var7 += 16) {
         if (var7 > 0) {
            updateTweak(var6);
         }

         xorTweak(var4, var7 + var5, var6);
         this.cipher.encrypt(var4, var7 + var5, 16);
         xorTweak(var4, var7 + var5, var6);
      }

      if (var7 < var3) {
         updateTweak(var6);
         swap(var4, var7 + var5, var7 - 16 + var5, var3 - var7);
         xorTweak(var4, var7 - 16 + var5, var6);
         this.cipher.encrypt(var4, var7 - 16 + var5, 16);
         xorTweak(var4, var7 - 16 + var5, var6);
      }

   }

   void decrypt(long var1, int var3, byte[] var4, int var5) {
      byte[] var6 = this.initTweak(var1);
      byte[] var7 = var6;

      int var8;
      for(var8 = 0; var8 + 16 <= var3; var8 += 16) {
         if (var8 > 0) {
            updateTweak(var6);
            if (var8 + 16 + 16 > var3 && var8 + 16 < var3) {
               var7 = (byte[])var6.clone();
               updateTweak(var6);
            }
         }

         xorTweak(var4, var8 + var5, var6);
         this.cipher.decrypt(var4, var8 + var5, 16);
         xorTweak(var4, var8 + var5, var6);
      }

      if (var8 < var3) {
         swap(var4, var8, var8 - 16 + var5, var3 - var8 + var5);
         xorTweak(var4, var8 - 16 + var5, var7);
         this.cipher.decrypt(var4, var8 - 16 + var5, 16);
         xorTweak(var4, var8 - 16 + var5, var7);
      }

   }

   private byte[] initTweak(long var1) {
      byte[] var3 = new byte[16];

      for(int var4 = 0; var4 < 16; var1 >>>= 8) {
         var3[var4] = (byte)((int)(var1 & 255L));
         ++var4;
      }

      this.cipher.encrypt(var3, 0, 16);
      return var3;
   }

   private static void xorTweak(byte[] var0, int var1, byte[] var2) {
      for(int var3 = 0; var3 < 16; ++var3) {
         var0[var1 + var3] ^= var2[var3];
      }

   }

   private static void updateTweak(byte[] var0) {
      byte var1 = 0;
      byte var2 = 0;

      for(int var3 = 0; var3 < 16; ++var3) {
         var2 = (byte)(var0[var3] >> 7 & 1);
         var0[var3] = (byte)((var0[var3] << 1) + var1 & 255);
         var1 = var2;
      }

      if (var2 != 0) {
         var0[0] = (byte)(var0[0] ^ 135);
      }

   }

   private static void swap(byte[] var0, int var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         byte var5 = var0[var1 + var4];
         var0[var1 + var4] = var0[var2 + var4];
         var0[var2 + var4] = var5;
      }

   }
}
