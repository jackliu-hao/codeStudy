package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public class CPUTF8 extends ConstantPoolEntry {
   private final String utf8;
   private boolean hashcodeComputed;
   private int cachedHashCode;

   public CPUTF8(String utf8, int globalIndex) {
      super((byte)1, globalIndex);
      this.utf8 = utf8;
      if (utf8 == null) {
         throw new NullPointerException("Null arguments are not allowed");
      }
   }

   public CPUTF8(String string) {
      this(string, -1);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CPUTF8 other = (CPUTF8)obj;
         return this.utf8.equals(other.utf8);
      }
   }

   private void generateHashCode() {
      this.hashcodeComputed = true;
      int PRIME = true;
      this.cachedHashCode = 31 + this.utf8.hashCode();
   }

   public int hashCode() {
      if (!this.hashcodeComputed) {
         this.generateHashCode();
      }

      return this.cachedHashCode;
   }

   public String toString() {
      return "UTF8: " + this.utf8;
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeUTF(this.utf8);
   }

   public String underlyingString() {
      return this.utf8;
   }

   public void setGlobalIndex(int index) {
      this.globalIndex = index;
   }
}
