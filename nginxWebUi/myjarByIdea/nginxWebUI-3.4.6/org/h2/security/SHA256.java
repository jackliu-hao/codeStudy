package org.h2.security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.h2.util.Bits;

public class SHA256 {
   private SHA256() {
   }

   public static byte[] getHashWithSalt(byte[] var0, byte[] var1) {
      byte[] var2 = new byte[var0.length + var1.length];
      System.arraycopy(var0, 0, var2, 0, var0.length);
      System.arraycopy(var1, 0, var2, var0.length, var1.length);
      return getHash(var2, true);
   }

   public static byte[] getKeyPasswordHash(String var0, char[] var1) {
      String var2 = var0 + "@";
      byte[] var3 = new byte[2 * (var2.length() + var1.length)];
      int var4 = 0;
      int var5 = 0;

      int var6;
      int var7;
      for(var6 = var2.length(); var5 < var6; ++var5) {
         var7 = var2.charAt(var5);
         var3[var4++] = (byte)(var7 >> 8);
         var3[var4++] = (byte)var7;
      }

      char[] var9 = var1;
      var6 = var1.length;

      for(var7 = 0; var7 < var6; ++var7) {
         char var8 = var9[var7];
         var3[var4++] = (byte)(var8 >> 8);
         var3[var4++] = (byte)var8;
      }

      Arrays.fill(var1, '\u0000');
      return getHash(var3, true);
   }

   public static byte[] getHMAC(byte[] var0, byte[] var1) {
      return initMac(var0).doFinal(var1);
   }

   private static Mac initMac(byte[] var0) {
      if (var0.length == 0) {
         var0 = new byte[1];
      }

      try {
         Mac var1 = Mac.getInstance("HmacSHA256");
         var1.init(new SecretKeySpec(var0, "HmacSHA256"));
         return var1;
      } catch (GeneralSecurityException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static byte[] getPBKDF2(byte[] var0, byte[] var1, int var2, int var3) {
      byte[] var4 = new byte[var3];
      Mac var5 = initMac(var0);
      int var6 = 64 + Math.max(32, var1.length + 4);
      byte[] var7 = new byte[var6];
      byte[] var8 = null;
      int var9 = 1;

      for(int var10 = 0; var10 < var3; var10 += 32) {
         for(int var11 = 0; var11 < var2; ++var11) {
            if (var11 == 0) {
               System.arraycopy(var1, 0, var7, 0, var1.length);
               Bits.writeInt(var7, var1.length, var9);
               var6 = var1.length + 4;
            } else {
               System.arraycopy(var8, 0, var7, 0, 32);
               var6 = 32;
            }

            var5.update(var7, 0, var6);
            var8 = var5.doFinal();

            for(int var12 = 0; var12 < 32 && var12 + var10 < var3; ++var12) {
               var4[var12 + var10] ^= var8[var12];
            }
         }

         ++var9;
      }

      Arrays.fill(var0, (byte)0);
      return var4;
   }

   public static byte[] getHash(byte[] var0, boolean var1) {
      byte[] var2;
      try {
         var2 = MessageDigest.getInstance("SHA-256").digest(var0);
      } catch (NoSuchAlgorithmException var4) {
         throw new RuntimeException(var4);
      }

      if (var1) {
         Arrays.fill(var0, (byte)0);
      }

      return var2;
   }
}
