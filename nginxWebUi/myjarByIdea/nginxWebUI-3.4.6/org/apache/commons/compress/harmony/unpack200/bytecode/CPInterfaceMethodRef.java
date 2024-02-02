package org.apache.commons.compress.harmony.unpack200.bytecode;

public class CPInterfaceMethodRef extends CPRef {
   private boolean hashcodeComputed;
   private int cachedHashCode;

   public CPInterfaceMethodRef(CPClass className, CPNameAndType descriptor, int globalIndex) {
      super((byte)11, className, descriptor, globalIndex);
   }

   public int invokeInterfaceCount() {
      return this.nameAndType.invokeInterfaceCount();
   }

   private void generateHashCode() {
      this.hashcodeComputed = true;
      int PRIME = true;
      int result = 1;
      result = 31 * result + this.className.hashCode();
      result = 31 * result + this.nameAndType.hashCode();
      this.cachedHashCode = result;
   }

   public int hashCode() {
      if (!this.hashcodeComputed) {
         this.generateHashCode();
      }

      return this.cachedHashCode;
   }
}
