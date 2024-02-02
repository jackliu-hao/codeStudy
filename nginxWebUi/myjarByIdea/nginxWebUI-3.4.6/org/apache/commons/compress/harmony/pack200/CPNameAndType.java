package org.apache.commons.compress.harmony.pack200;

public class CPNameAndType extends ConstantPoolEntry implements Comparable {
   private final CPUTF8 name;
   private final CPSignature signature;

   public CPNameAndType(CPUTF8 name, CPSignature signature) {
      this.name = name;
      this.signature = signature;
   }

   public String toString() {
      return this.name + ":" + this.signature;
   }

   public int compareTo(Object obj) {
      if (obj instanceof CPNameAndType) {
         CPNameAndType nat = (CPNameAndType)obj;
         int compareSignature = this.signature.compareTo(nat.signature);
         return compareSignature == 0 ? this.name.compareTo(nat.name) : compareSignature;
      } else {
         return 0;
      }
   }

   public int getNameIndex() {
      return this.name.getIndex();
   }

   public String getName() {
      return this.name.getUnderlyingString();
   }

   public int getTypeIndex() {
      return this.signature.getIndex();
   }
}
