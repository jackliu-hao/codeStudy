package org.h2.util;

import java.util.Arrays;

public class IntArray {
   private int[] data;
   private int size;
   private int hash;

   public IntArray() {
      this(10);
   }

   public IntArray(int var1) {
      this.data = new int[var1];
   }

   public IntArray(int[] var1) {
      this.data = var1;
      this.size = var1.length;
   }

   public void add(int var1) {
      if (this.size >= this.data.length) {
         this.ensureCapacity(this.size + this.size);
      }

      this.data[this.size++] = var1;
   }

   public int get(int var1) {
      if (var1 >= this.size) {
         throw new ArrayIndexOutOfBoundsException("i=" + var1 + " size=" + this.size);
      } else {
         return this.data[var1];
      }
   }

   public void remove(int var1) {
      if (var1 >= this.size) {
         throw new ArrayIndexOutOfBoundsException("i=" + var1 + " size=" + this.size);
      } else {
         System.arraycopy(this.data, var1 + 1, this.data, var1, this.size - var1 - 1);
         --this.size;
      }
   }

   public void ensureCapacity(int var1) {
      var1 = Math.max(4, var1);
      if (var1 >= this.data.length) {
         this.data = Arrays.copyOf(this.data, var1);
      }

   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof IntArray)) {
         return false;
      } else {
         IntArray var2 = (IntArray)var1;
         if (this.hashCode() == var2.hashCode() && this.size == var2.size) {
            for(int var3 = 0; var3 < this.size; ++var3) {
               if (this.data[var3] != var2.data[var3]) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      if (this.hash != 0) {
         return this.hash;
      } else {
         int var1 = this.size + 1;

         for(int var2 = 0; var2 < this.size; ++var2) {
            var1 = var1 * 31 + this.data[var2];
         }

         this.hash = var1;
         return var1;
      }
   }

   public int size() {
      return this.size;
   }

   public void toArray(int[] var1) {
      System.arraycopy(this.data, 0, var1, 0, this.size);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");

      for(int var2 = 0; var2 < this.size; ++var2) {
         if (var2 > 0) {
            var1.append(", ");
         }

         var1.append(this.data[var2]);
      }

      return var1.append('}').toString();
   }

   public void removeRange(int var1, int var2) {
      if (var1 <= var2 && var2 <= this.size) {
         System.arraycopy(this.data, var2, this.data, var1, this.size - var2);
         this.size -= var2 - var1;
      } else {
         throw new ArrayIndexOutOfBoundsException("from=" + var1 + " to=" + var2 + " size=" + this.size);
      }
   }
}
