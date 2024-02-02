package org.h2.util;

import org.h2.message.DbException;

public class Permutations<T> {
   private final T[] in;
   private final T[] out;
   private final int n;
   private final int m;
   private final int[] index;
   private boolean hasNext = true;

   private Permutations(T[] var1, T[] var2, int var3) {
      this.n = var1.length;
      this.m = var3;
      if (this.n >= var3 && var3 >= 0) {
         this.in = var1;
         this.out = var2;
         this.index = new int[this.n];

         for(int var4 = 0; var4 < this.n; this.index[var4] = var4++) {
         }

         this.reverseAfter(var3 - 1);
      } else {
         throw DbException.getInternalError("n < m or m < 0");
      }
   }

   public static <T> Permutations<T> create(T[] var0, T[] var1) {
      return new Permutations(var0, var1, var0.length);
   }

   public static <T> Permutations<T> create(T[] var0, T[] var1, int var2) {
      return new Permutations(var0, var1, var2);
   }

   private void moveIndex() {
      int var1 = this.rightmostDip();
      if (var1 < 0) {
         this.hasNext = false;
      } else {
         int var2 = var1 + 1;

         int var3;
         for(var3 = var1 + 2; var3 < this.n; ++var3) {
            if (this.index[var3] < this.index[var2] && this.index[var3] > this.index[var1]) {
               var2 = var3;
            }
         }

         var3 = this.index[var1];
         this.index[var1] = this.index[var2];
         this.index[var2] = var3;
         if (this.m - 1 > var1) {
            this.reverseAfter(var1);
            this.reverseAfter(this.m - 1);
         }

      }
   }

   private int rightmostDip() {
      for(int var1 = this.n - 2; var1 >= 0; --var1) {
         if (this.index[var1] < this.index[var1 + 1]) {
            return var1;
         }
      }

      return -1;
   }

   private void reverseAfter(int var1) {
      int var2 = var1 + 1;

      for(int var3 = this.n - 1; var2 < var3; --var3) {
         int var4 = this.index[var2];
         this.index[var2] = this.index[var3];
         this.index[var3] = var4;
         ++var2;
      }

   }

   public boolean next() {
      if (!this.hasNext) {
         return false;
      } else {
         for(int var1 = 0; var1 < this.m; ++var1) {
            this.out[var1] = this.in[this.index[var1]];
         }

         this.moveIndex();
         return true;
      }
   }
}
