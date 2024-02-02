package org.h2.compress;

import java.nio.ByteBuffer;

public final class CompressLZF implements Compressor {
   private static final int HASH_SIZE = 16384;
   private static final int MAX_LITERAL = 32;
   private static final int MAX_OFF = 8192;
   private static final int MAX_REF = 264;
   private int[] cachedHashTable;

   public void setOptions(String var1) {
   }

   private static int first(byte[] var0, int var1) {
      return var0[var1] << 8 | var0[var1 + 1] & 255;
   }

   private static int first(ByteBuffer var0, int var1) {
      return var0.get(var1) << 8 | var0.get(var1 + 1) & 255;
   }

   private static int next(int var0, byte[] var1, int var2) {
      return var0 << 8 | var1[var2 + 2] & 255;
   }

   private static int next(int var0, ByteBuffer var1, int var2) {
      return var0 << 8 | var1.get(var2 + 2) & 255;
   }

   private static int hash(int var0) {
      return var0 * 2777 >> 9 & 16383;
   }

   public int compress(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      int var6 = var2;
      var3 += var2;
      if (this.cachedHashTable == null) {
         this.cachedHashTable = new int[16384];
      }

      int[] var7 = this.cachedHashTable;
      int var8 = 0;
      ++var5;
      int var9 = first(var1, var2);

      while(true) {
         while(var2 < var3 - 4) {
            byte var10 = var1[var2 + 2];
            var9 = (var9 << 8) + (var10 & 255);
            int var11 = hash(var9);
            int var12 = var7[var11];
            var7[var11] = var2;
            if (var12 < var2 && var12 > var6 && (var11 = var2 - var12 - 1) < 8192 && var1[var12 + 2] == var10 && var1[var12 + 1] == (byte)(var9 >> 8) && var1[var12] == (byte)(var9 >> 16)) {
               int var13 = var3 - var2 - 2;
               if (var13 > 264) {
                  var13 = 264;
               }

               if (var8 == 0) {
                  --var5;
               } else {
                  var4[var5 - var8 - 1] = (byte)(var8 - 1);
                  var8 = 0;
               }

               int var14;
               for(var14 = 3; var14 < var13 && var1[var12 + var14] == var1[var2 + var14]; ++var14) {
               }

               var14 -= 2;
               if (var14 < 7) {
                  var4[var5++] = (byte)((var11 >> 8) + (var14 << 5));
               } else {
                  var4[var5++] = (byte)((var11 >> 8) + 224);
                  var4[var5++] = (byte)(var14 - 7);
               }

               var4[var5++] = (byte)var11;
               ++var5;
               var2 += var14;
               var9 = first(var1, var2);
               var9 = next(var9, var1, var2);
               var7[hash(var9)] = var2++;
               var9 = next(var9, var1, var2);
               var7[hash(var9)] = var2++;
            } else {
               var4[var5++] = var1[var2++];
               ++var8;
               if (var8 == 32) {
                  var4[var5 - var8 - 1] = (byte)(var8 - 1);
                  var8 = 0;
                  ++var5;
               }
            }
         }

         while(var2 < var3) {
            var4[var5++] = var1[var2++];
            ++var8;
            if (var8 == 32) {
               var4[var5 - var8 - 1] = (byte)(var8 - 1);
               var8 = 0;
               ++var5;
            }
         }

         var4[var5 - var8 - 1] = (byte)(var8 - 1);
         if (var8 == 0) {
            --var5;
         }

         return var5;
      }
   }

   public int compress(ByteBuffer var1, int var2, byte[] var3, int var4) {
      int var5 = var2;
      int var6 = var1.capacity();
      if (this.cachedHashTable == null) {
         this.cachedHashTable = new int[16384];
      }

      int[] var7 = this.cachedHashTable;
      int var8 = 0;
      ++var4;
      int var9 = first(var1, var2);

      while(true) {
         while(var2 < var6 - 4) {
            byte var10 = var1.get(var2 + 2);
            var9 = (var9 << 8) + (var10 & 255);
            int var11 = hash(var9);
            int var12 = var7[var11];
            var7[var11] = var2;
            if (var12 < var2 && var12 > var5 && (var11 = var2 - var12 - 1) < 8192 && var1.get(var12 + 2) == var10 && var1.get(var12 + 1) == (byte)(var9 >> 8) && var1.get(var12) == (byte)(var9 >> 16)) {
               int var13 = var6 - var2 - 2;
               if (var13 > 264) {
                  var13 = 264;
               }

               if (var8 == 0) {
                  --var4;
               } else {
                  var3[var4 - var8 - 1] = (byte)(var8 - 1);
                  var8 = 0;
               }

               int var14;
               for(var14 = 3; var14 < var13 && var1.get(var12 + var14) == var1.get(var2 + var14); ++var14) {
               }

               var14 -= 2;
               if (var14 < 7) {
                  var3[var4++] = (byte)((var11 >> 8) + (var14 << 5));
               } else {
                  var3[var4++] = (byte)((var11 >> 8) + 224);
                  var3[var4++] = (byte)(var14 - 7);
               }

               var3[var4++] = (byte)var11;
               ++var4;
               var2 += var14;
               var9 = first(var1, var2);
               var9 = next(var9, var1, var2);
               var7[hash(var9)] = var2++;
               var9 = next(var9, var1, var2);
               var7[hash(var9)] = var2++;
            } else {
               var3[var4++] = var1.get(var2++);
               ++var8;
               if (var8 == 32) {
                  var3[var4 - var8 - 1] = (byte)(var8 - 1);
                  var8 = 0;
                  ++var4;
               }
            }
         }

         while(var2 < var6) {
            var3[var4++] = var1.get(var2++);
            ++var8;
            if (var8 == 32) {
               var3[var4 - var8 - 1] = (byte)(var8 - 1);
               var8 = 0;
               ++var4;
            }
         }

         var3[var4 - var8 - 1] = (byte)(var8 - 1);
         if (var8 == 0) {
            --var4;
         }

         return var4;
      }
   }

   public void expand(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6) {
      if (var2 >= 0 && var5 >= 0 && var6 >= 0) {
         do {
            int var7 = var1[var2++] & 255;
            if (var7 < 32) {
               ++var7;
               System.arraycopy(var1, var2, var4, var5, var7);
               var5 += var7;
               var2 += var7;
            } else {
               int var8 = var7 >> 5;
               if (var8 == 7) {
                  var8 += var1[var2++] & 255;
               }

               var8 += 2;
               var7 = -((var7 & 31) << 8) - 1;
               var7 -= var1[var2++] & 255;
               var7 += var5;
               if (var5 + var8 >= var4.length) {
                  throw new ArrayIndexOutOfBoundsException();
               }

               for(int var9 = 0; var9 < var8; ++var9) {
                  var4[var5++] = var4[var7++];
               }
            }
         } while(var5 < var6);

      } else {
         throw new IllegalArgumentException();
      }
   }

   public static void expand(ByteBuffer var0, ByteBuffer var1) {
      do {
         int var2 = var0.get() & 255;
         int var3;
         if (var2 < 32) {
            ++var2;

            for(var3 = 0; var3 < var2; ++var3) {
               var1.put(var0.get());
            }
         } else {
            var3 = var2 >> 5;
            if (var3 == 7) {
               var3 += var0.get() & 255;
            }

            var3 += 2;
            var2 = -((var2 & 31) << 8) - 1;
            var2 -= var0.get() & 255;
            var2 += var1.position();

            for(int var4 = 0; var4 < var3; ++var4) {
               var1.put(var1.get(var2++));
            }
         }
      } while(var1.position() < var1.capacity());

   }

   public int getAlgorithm() {
      return 1;
   }
}
