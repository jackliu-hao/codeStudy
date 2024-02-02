package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantValueAttribute extends Attribute {
   private int constantIndex;
   private final ClassFileEntry entry;
   private static CPUTF8 attributeName;

   public static void setAttributeName(CPUTF8 cpUTF8Value) {
      attributeName = cpUTF8Value;
   }

   public ConstantValueAttribute(ClassFileEntry entry) {
      super(attributeName);
      if (entry == null) {
         throw new NullPointerException();
      } else {
         this.entry = entry;
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!super.equals(obj)) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         ConstantValueAttribute other = (ConstantValueAttribute)obj;
         if (this.entry == null) {
            if (other.entry != null) {
               return false;
            }
         } else if (!this.entry.equals(other.entry)) {
            return false;
         }

         return true;
      }
   }

   protected int getLength() {
      return 2;
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      return new ClassFileEntry[]{this.getAttributeName(), this.entry};
   }

   public int hashCode() {
      int PRIME = true;
      int result = super.hashCode();
      result = 31 * result + (this.entry == null ? 0 : this.entry.hashCode());
      return result;
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      this.entry.resolve(pool);
      this.constantIndex = pool.indexOf(this.entry);
   }

   public String toString() {
      return "Constant:" + this.entry;
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeShort(this.constantIndex);
   }
}
