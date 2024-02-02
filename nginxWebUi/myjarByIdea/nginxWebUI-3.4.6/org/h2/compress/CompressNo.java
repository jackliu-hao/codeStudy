package org.h2.compress;

public class CompressNo implements Compressor {
   public int getAlgorithm() {
      return 0;
   }

   public void setOptions(String var1) {
   }

   public int compress(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      System.arraycopy(var1, var2, var4, var5, var3);
      return var5 + var3;
   }

   public void expand(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6) {
      System.arraycopy(var1, var2, var4, var5, var6);
   }
}
