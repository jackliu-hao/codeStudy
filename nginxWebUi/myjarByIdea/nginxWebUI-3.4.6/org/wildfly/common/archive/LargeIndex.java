package org.wildfly.common.archive;

import java.util.Arrays;

final class LargeIndex extends Index {
   private final int[] table;

   LargeIndex(int entries) {
      super(entries);
      int[] array = new int[this.size()];
      Arrays.fill(array, -1);
      this.table = array;
   }

   long get(int index) {
      int val = this.table[index];
      return val == -1 ? -1L : (long)(val & -1);
   }

   void put(int index, long offset) {
      int[] table = this.table;

      for(int val = table[index]; (long)val != -1L; val = table[index]) {
         index = index + 1 & this.getMask();
      }

      table[index] = Math.toIntExact(offset);
   }
}
