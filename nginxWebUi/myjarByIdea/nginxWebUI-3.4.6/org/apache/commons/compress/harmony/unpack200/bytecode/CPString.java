package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public class CPString extends CPConstant {
   private transient int nameIndex;
   private final CPUTF8 name;
   private boolean hashcodeComputed;
   private int cachedHashCode;

   public CPString(CPUTF8 value, int globalIndex) {
      super((byte)8, value, globalIndex);
      this.name = value;
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeShort(this.nameIndex);
   }

   public String toString() {
      return "String: " + this.getValue();
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      this.nameIndex = pool.indexOf(this.name);
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      return new ClassFileEntry[]{this.name};
   }

   private void generateHashCode() {
      this.hashcodeComputed = true;
      int PRIME = true;
      int result = 1;
      result = 31 * result + this.name.hashCode();
      this.cachedHashCode = result;
   }

   public int hashCode() {
      if (!this.hashcodeComputed) {
         this.generateHashCode();
      }

      return this.cachedHashCode;
   }
}
