package org.apache.commons.compress.harmony.unpack200.bytecode;

public abstract class CPConstant extends ConstantPoolEntry {
   private final Object value;

   public CPConstant(byte tag, Object value, int globalIndex) {
      super(tag, globalIndex);
      this.value = value;
      if (value == null) {
         throw new NullPointerException("Null arguments are not allowed");
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CPConstant other = (CPConstant)obj;
         if (this.value == null) {
            if (other.value != null) {
               return false;
            }
         } else if (!this.value.equals(other.value)) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int PRIME = true;
      int result = 1;
      result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
      return result;
   }

   protected Object getValue() {
      return this.value;
   }
}
