package org.apache.commons.compress.harmony.pack200;

public abstract class ConstantPoolEntry {
   private int index = -1;

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }
}
