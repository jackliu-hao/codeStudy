package org.h2.security;

public interface BlockCipher {
   int ALIGN = 16;

   void setKey(byte[] var1);

   void encrypt(byte[] var1, int var2, int var3);

   void decrypt(byte[] var1, int var2, int var3);

   int getKeyLength();
}
