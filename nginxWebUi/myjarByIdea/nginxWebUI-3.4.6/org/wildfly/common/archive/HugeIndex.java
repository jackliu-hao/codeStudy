package org.wildfly.common.archive;

import java.util.Arrays;

final class HugeIndex extends Index {
   private final long[] table;

   HugeIndex(int entries) {
      super(entries);
      long[] array = new long[this.size()];
      Arrays.fill(array, -1L);
      this.table = array;
   }

   long get(int index) {
      return this.table[index];
   }

   void put(int index, long offset) {
      long[] table = this.table;

      for(long val = table[index]; val != -1L; val = table[index]) {
         index = index + 1 & this.getMask();
      }

      table[index] = offset;
   }
}
