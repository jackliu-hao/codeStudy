package org.h2.security;

import org.h2.util.Bits;

public class Fog implements BlockCipher {
   private int key;

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
      int var4 = Bits.readInt(var1, var3);
      int var5 = Bits.readInt(var1, var3 + 4);
      int var6 = Bits.readInt(var1, var3 + 8);
      int var7 = Bits.readInt(var1, var3 + 12);
      int var8 = this.key;
      var4 = Integer.rotateLeft(var4 ^ var8, var5);
      var6 = Integer.rotateLeft(var6 ^ var8, var5);
      var5 = Integer.rotateLeft(var5 ^ var8, var4);
      var7 = Integer.rotateLeft(var7 ^ var8, var4);
      Bits.writeInt(var2, var3, var4);
      Bits.writeInt(var2, var3 + 4, var5);
      Bits.writeInt(var2, var3 + 8, var6);
      Bits.writeInt(var2, var3 + 12, var7);
   }

   private void decryptBlock(byte[] var1, byte[] var2, int var3) {
      int var4 = Bits.readInt(var1, var3);
      int var5 = Bits.readInt(var1, var3 + 4);
      int var6 = Bits.readInt(var1, var3 + 8);
      int var7 = Bits.readInt(var1, var3 + 12);
      int var8 = this.key;
      var5 = Integer.rotateRight(var5, var4) ^ var8;
      var7 = Integer.rotateRight(var7, var4) ^ var8;
      var4 = Integer.rotateRight(var4, var5) ^ var8;
      var6 = Integer.rotateRight(var6, var5) ^ var8;
      Bits.writeInt(var2, var3, var4);
      Bits.writeInt(var2, var3 + 4, var5);
      Bits.writeInt(var2, var3 + 8, var6);
      Bits.writeInt(var2, var3 + 12, var7);
   }

   public int getKeyLength() {
      return 16;
   }

   public void setKey(byte[] var1) {
      this.key = (int)Bits.readLong(var1, 0);
   }
}
