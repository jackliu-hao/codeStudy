package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.commons.compress.harmony.unpack200.SegmentUtils;

public class CPNameAndType extends ConstantPoolEntry {
   CPUTF8 descriptor;
   transient int descriptorIndex;
   CPUTF8 name;
   transient int nameIndex;
   private boolean hashcodeComputed;
   private int cachedHashCode;

   public CPNameAndType(CPUTF8 name, CPUTF8 descriptor, int globalIndex) {
      super((byte)12, globalIndex);
      this.name = name;
      this.descriptor = descriptor;
      if (name == null || descriptor == null) {
         throw new NullPointerException("Null arguments are not allowed");
      }
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      return new ClassFileEntry[]{this.name, this.descriptor};
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      this.descriptorIndex = pool.indexOf(this.descriptor);
      this.nameIndex = pool.indexOf(this.name);
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeShort(this.nameIndex);
      dos.writeShort(this.descriptorIndex);
   }

   public String toString() {
      return "NameAndType: " + this.name + "(" + this.descriptor + ")";
   }

   private void generateHashCode() {
      this.hashcodeComputed = true;
      int PRIME = true;
      int result = 1;
      result = 31 * result + this.descriptor.hashCode();
      result = 31 * result + this.name.hashCode();
      this.cachedHashCode = result;
   }

   public int hashCode() {
      if (!this.hashcodeComputed) {
         this.generateHashCode();
      }

      return this.cachedHashCode;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CPNameAndType other = (CPNameAndType)obj;
         if (!this.descriptor.equals(other.descriptor)) {
            return false;
         } else {
            return this.name.equals(other.name);
         }
      }
   }

   public int invokeInterfaceCount() {
      return 1 + SegmentUtils.countInvokeInterfaceArgs(this.descriptor.underlyingString());
   }
}
