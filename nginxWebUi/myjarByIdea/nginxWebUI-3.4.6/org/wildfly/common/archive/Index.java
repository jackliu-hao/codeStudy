package org.wildfly.common.archive;

abstract class Index {
   final int tableSize;

   Index(int entries) {
      if (entries >= 1073741824) {
         throw new IllegalStateException("Index is too large");
      } else {
         this.tableSize = Integer.highestOneBit(entries << 2);
      }
   }

   final int size() {
      return this.tableSize;
   }

   abstract long get(int var1);

   abstract void put(int var1, long var2);

   int getMask() {
      return this.tableSize - 1;
   }
}
