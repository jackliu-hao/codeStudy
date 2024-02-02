package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Attribute extends ClassFileEntry {
   protected final CPUTF8 attributeName;
   private int attributeNameIndex;

   public Attribute(CPUTF8 attributeName) {
      this.attributeName = attributeName;
   }

   protected void doWrite(DataOutputStream dos) throws IOException {
      dos.writeShort(this.attributeNameIndex);
      dos.writeInt(this.getLength());
      this.writeBody(dos);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Attribute other = (Attribute)obj;
         if (this.attributeName == null) {
            if (other.attributeName != null) {
               return false;
            }
         } else if (!this.attributeName.equals(other.attributeName)) {
            return false;
         }

         return true;
      }
   }

   protected CPUTF8 getAttributeName() {
      return this.attributeName;
   }

   protected abstract int getLength();

   protected int getLengthIncludingHeader() {
      return this.getLength() + 2 + 4;
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      return new ClassFileEntry[]{this.getAttributeName()};
   }

   public boolean hasBCIRenumbering() {
      return false;
   }

   public boolean isSourceFileAttribute() {
      return false;
   }

   public int hashCode() {
      int PRIME = true;
      int result = 1;
      result = 31 * result + (this.attributeName == null ? 0 : this.attributeName.hashCode());
      return result;
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      this.attributeNameIndex = pool.indexOf(this.attributeName);
   }

   protected abstract void writeBody(DataOutputStream var1) throws IOException;
}
