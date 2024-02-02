package org.h2.util;

import java.util.Arrays;
import java.util.NoSuchElementException;

public final class ByteStack {
   private static final int MAX_ARRAY_SIZE = 2147483639;
   private int size;
   private byte[] array;

   public ByteStack() {
      this.array = Utils.EMPTY_BYTES;
   }

   public void push(byte var1) {
      int var2 = this.size;
      int var3 = this.array.length;
      if (var2 >= var3) {
         this.grow(var3);
      }

      this.array[var2] = var1;
      this.size = var2 + 1;
   }

   public byte pop() {
      int var1 = this.size - 1;
      if (var1 < 0) {
         throw new NoSuchElementException();
      } else {
         this.size = var1;
         return this.array[var1];
      }
   }

   public int poll(int var1) {
      int var2 = this.size - 1;
      if (var2 < 0) {
         return var1;
      } else {
         this.size = var2;
         return this.array[var2];
      }
   }

   public int peek(int var1) {
      int var2 = this.size - 1;
      return var2 < 0 ? var1 : this.array[var2];
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public int size() {
      return this.size;
   }

   private void grow(int var1) {
      if (var1 == 0) {
         var1 = 16;
      } else {
         if (var1 >= 2147483639) {
            throw new OutOfMemoryError();
         }

         if ((var1 <<= 1) < 0) {
            var1 = 2147483639;
         }
      }

      this.array = Arrays.copyOf(this.array, var1);
   }
}
