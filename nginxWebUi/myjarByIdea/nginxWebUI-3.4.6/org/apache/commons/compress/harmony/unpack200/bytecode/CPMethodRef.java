package org.apache.commons.compress.harmony.unpack200.bytecode;

public class CPMethodRef extends CPRef {
   private boolean hashcodeComputed;
   private int cachedHashCode;

   public CPMethodRef(CPClass className, CPNameAndType descriptor, int globalIndex) {
      super((byte)10, className, descriptor, globalIndex);
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      return new ClassFileEntry[]{this.className, this.nameAndType};
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
