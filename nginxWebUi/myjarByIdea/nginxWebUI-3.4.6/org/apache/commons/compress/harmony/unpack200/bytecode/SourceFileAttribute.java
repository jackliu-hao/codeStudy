package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public class SourceFileAttribute extends Attribute {
   private final CPUTF8 name;
   private int nameIndex;
   private static CPUTF8 attributeName;

   public static void setAttributeName(CPUTF8 cpUTF8Value) {
      attributeName = cpUTF8Value;
   }

   public SourceFileAttribute(CPUTF8 name) {
      super(attributeName);
      this.name = name;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!super.equals(obj)) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         SourceFileAttribute other = (SourceFileAttribute)obj;
         if (this.name == null) {
            if (other.name != null) {
               return false;
            }
         } else if (!this.name.equals(other.name)) {
            return false;
         }

         return true;
      }
   }

   public boolean isSourceFileAttribute() {
      return true;
   }

   protected int getLength() {
      return 2;
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      return new ClassFileEntry[]{this.getAttributeName(), this.name};
   }

   public int hashCode() {
      int PRIME = true;
      int result = super.hashCode();
      result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
      return result;
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      this.nameIndex = pool.indexOf(this.name);
   }

   public String toString() {
      return "SourceFile: " + this.name;
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeShort(this.nameIndex);
   }
}
