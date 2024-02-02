package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public class CPClass extends ConstantPoolEntry {
   private int index;
   public String name;
   private final CPUTF8 utf8;
   private boolean hashcodeComputed;
   private int cachedHashCode;

   public CPClass(CPUTF8 name, int globalIndex) {
      super((byte)7, globalIndex);
      if (name == null) {
         throw new NullPointerException("Null arguments are not allowed");
      } else {
         this.name = name.underlyingString();
         this.utf8 = name;
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
         CPClass other = (CPClass)obj;
         return this.utf8.equals(other.utf8);
      }
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      return new ClassFileEntry[]{this.utf8};
   }

   private void generateHashCode() {
      this.hashcodeComputed = true;
      this.cachedHashCode = this.utf8.hashCode();
   }

   public int hashCode() {
      if (!this.hashcodeComputed) {
         this.generateHashCode();
      }

      return this.cachedHashCode;
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      this.index = pool.indexOf(this.utf8);
   }

   public String toString() {
      return "Class: " + this.getName();
   }

   public String getName() {
      return this.name;
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeShort(this.index);
   }
}
