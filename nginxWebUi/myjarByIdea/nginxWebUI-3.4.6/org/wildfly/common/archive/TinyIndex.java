package org.wildfly.common.archive;

import java.util.Arrays;

final class TinyIndex extends Index {
   private final short[] table;

   TinyIndex(int entries) {
      super(entries);
      short[] array = new short[this.size()];
      Arrays.fill(array, (short)-1);
      this.table = array;
   }

   long get(int index) {
      int val = this.table[index];
      return val == -1 ? -1L : (long)(val & '\uffff');
   }

   void put(int index, long offset) {
      short[] table = this.table;

      for(int val = table[index]; (long)val != -1L; val = table[index]) {
         index = index + 1 & this.getMask();
      }

      table[index] = (short)((int)offset);
   }
}
