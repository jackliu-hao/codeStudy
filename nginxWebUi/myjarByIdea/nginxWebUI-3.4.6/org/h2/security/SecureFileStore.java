package org.h2.security;

import org.h2.store.DataHandler;
import org.h2.store.FileStore;
import org.h2.util.Bits;
import org.h2.util.MathUtils;

public class SecureFileStore extends FileStore {
   private byte[] key;
   private final BlockCipher cipher;
   private final BlockCipher cipherForInitVector;
   private byte[] buffer = new byte[4];
   private long pos;
   private final byte[] bufferForInitVector;
   private final int keyIterations;

   public SecureFileStore(DataHandler var1, String var2, String var3, String var4, byte[] var5, int var6) {
      super(var1, var2, var3);
      this.key = var5;
      this.cipher = CipherFactory.getBlockCipher(var4);
      this.cipherForInitVector = CipherFactory.getBlockCipher(var4);
      this.keyIterations = var6;
      this.bufferForInitVector = new byte[16];
   }

   protected byte[] generateSalt() {
      return MathUtils.secureRandomBytes(16);
   }

   protected void initKey(byte[] var1) {
      this.key = SHA256.getHashWithSalt(this.key, var1);

      for(int var2 = 0; var2 < this.keyIterations; ++var2) {
         this.key = SHA256.getHash(this.key, true);
      }

      this.cipher.setKey(this.key);
      this.key = SHA256.getHash(this.key, true);
      this.cipherForInitVector.setKey(this.key);
   }

   protected void writeDirect(byte[] var1, int var2, int var3) {
      super.write(var1, var2, var3);
      this.pos += (long)var3;
   }

   public void write(byte[] var1, int var2, int var3) {
      if (this.buffer.length < var1.length) {
         this.buffer = new byte[var3];
      }

      System.arraycopy(var1, var2, this.buffer, 0, var3);
      this.xorInitVector(this.buffer, 0, var3, this.pos);
      this.cipher.encrypt(this.buffer, 0, var3);
      super.write(this.buffer, 0, var3);
      this.pos += (long)var3;
   }

   public void readFullyDirect(byte[] var1, int var2, int var3) {
      super.readFully(var1, var2, var3);
      this.pos += (long)var3;
   }

   public void readFully(byte[] var1, int var2, int var3) {
      super.readFully(var1, var2, var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         if (var1[var4] != 0) {
            this.cipher.decrypt(var1, var2, var3);
            this.xorInitVector(var1, var2, var3, this.pos);
            break;
         }
      }

      this.pos += (long)var3;
   }

   public void seek(long var1) {
      this.pos = var1;
      super.seek(var1);
   }

   private void xorInitVector(byte[] var1, int var2, int var3, long var4) {
      for(byte[] var6 = this.bufferForInitVector; var3 > 0; var3 -= 16) {
         int var7;
         for(var7 = 0; var7 < 16; var7 += 8) {
            Bits.writeLong(var6, var7, var4 + (long)var7 >>> 3);
         }

         this.cipherForInitVector.encrypt(var6, 0, 16);

         for(var7 = 0; var7 < 16; ++var7) {
            var1[var2 + var7] ^= var6[var7];
         }

         var4 += 16L;
         var2 += 16;
      }

   }
}
