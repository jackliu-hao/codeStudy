package org.h2.compress;

public interface Compressor {
   int NO = 0;
   int LZF = 1;
   int DEFLATE = 2;

   int getAlgorithm();

   int compress(byte[] var1, int var2, int var3, byte[] var4, int var5);

   void expand(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6);

   void setOptions(String var1);
}
