package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.util.List;

public class CPMethod extends CPMember {
   private boolean hashcodeComputed;
   private int cachedHashCode;

   public CPMethod(CPUTF8 name, CPUTF8 descriptor, long flags, List attributes) {
      super(name, descriptor, flags, attributes);
   }

   public String toString() {
      return "Method: " + this.name + "(" + this.descriptor + ")";
   }

   private void generateHashCode() {
      this.hashcodeComputed = true;
      int PRIME = true;
      int result = 1;
      result = 31 * result + this.name.hashCode();
      result = 31 * result + this.descriptor.hashCode();
      this.cachedHashCode = result;
   }

   public int hashCode() {
      if (!this.hashcodeComputed) {
         this.generateHashCode();
      }

      return this.cachedHashCode;
   }
}
